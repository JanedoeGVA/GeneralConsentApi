package main;

import dao.DB;
import domain.*;
import entity.MessageError;
import entity.ResponseMessage;
import io.jsonwebtoken.JwtException;
import metier.PDFCreator;
import metier.SMTP;
import metier.ShortMessageService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import outils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.mail.MessagingException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.DATE;
import static javax.ws.rs.core.Response.Status.*;


@Path("/service")
public class Main {


    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    @Path("/test")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String message() {
        LOG.log(Level.INFO, "Main");
        String template = "http://example.com/{name}/{age}";
        // UriTemplate uriTemplate = new UriTemplate(template);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "Twilio");
        parameters.put("age", "110");
        UriBuilder builder = UriBuilder.fromPath(template);
        // Use .buildFromMap()
        URI output = builder.buildFromMap(parameters);
        try {
            SMTP.testSendMail();
            LOG.log(Level.INFO, "mail sent");
        } catch (MessagingException e) {
            LOG.log(Level.INFO, "error send mail",e.getMessage());
        }
        return output.toString();
    }


    @Path("/verification")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response generate(@QueryParam("contact") String contact, @QueryParam("code") String code) {
        LOG.log(Level.INFO, "verification call");
        LOG.log(Level.INFO, "contact" + contact);
        LOG.log(Level.INFO, "code" + code);
        try {
            final int id = DB.valideCode(contact, code);
            if (id != -1) {
                final String jws = Utils.generateJWSToken(id);
                TokenJWT tokenJWT = new TokenJWT(jws);
                return Response.status(OK)
                        .entity(tokenJWT)
                        .build();
            } else {
                return Response.status(BAD_REQUEST)
                        .entity(new MessageError("not valid", "Le code n'est pas valide ou a déjà été utilisé"))
                        .build();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            return Response.status(INTERNAL_SERVER_ERROR)
                    .entity(new MessageError("server error", "Un problème est survenu"))
                    .build();
        }
    }


    @Path("/check-phone")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkPhone(@QueryParam("phone") String phone) {
        LOG.log(Level.INFO, "check phone");
        LOG.log(Level.INFO, "phone : " + phone);
        final String match = (Utils.validatePhone(phone)) ? "le numero correspond a un numero de natel" : "le numero ne correspond pas";
        return Response.status(OK)
                .entity(new ResponseMessage(match))
                .build();
    }

    @Path("/check-email")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkEmail(@QueryParam("mail") String mail) {
        LOG.log(Level.INFO, "check mail");
        LOG.log(Level.INFO, "mail : " + mail);
        final String match = (Utils.validateMail(mail)) ? "correspond a un mail correct" : "ne correspond pas a un mail correct";
        return Response.status(OK)
                .entity(new ResponseMessage(match))
                .build();
    }

    @Path("/send-sms")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendSMS(@QueryParam("phone") String phone) {
        LOG.log(Level.INFO, "send SMS");
        LOG.log(Level.INFO, "phone : " + phone);
        if (!Utils.validatePhone(phone)) {
            return Response.status(BAD_REQUEST)
                    .entity(new MessageError("invalid phone", "Le numéro ne correspond pas à un natel"))
                    .build();
        }
        try {
            if (!DB.checkContactExist(phone)) {
                ChallengeCode challengeCode = generateChallengeCode(phone);
                ShortMessageService.send(phone, challengeCode.getCode());
                return Response.status(NO_CONTENT)
                        .build();
            } else {
                LOG.log(Level.INFO, "too many request");
                return Response.status(BAD_REQUEST)
                        .entity(new MessageError("too many request", "vous devez attendre 3 minutes avant de redemander un nouveau code"))
                        .build();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
            return Response.status(INTERNAL_SERVER_ERROR)
                    .entity(new MessageError("server error", "Un problème est survenu"))
                    .build();
        }


    }

    @Path("/send-mail")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendMail(@QueryParam("email") String email) {
        LOG.log(Level.INFO, "send email " + email);


        if (!Utils.validateMail(email)) {
            LOG.log(Level.INFO, "invalid mail");
            return Response.status(BAD_REQUEST)
                    .entity(new MessageError("invalid mail", "Le mail ne correspond pas à une adresse correct"))
                    .build();
        }
        try {
            if (!DB.checkContactExist(email)) {
                LOG.log(Level.INFO, "mail existe pas");
                ChallengeCode challengeCode = generateChallengeCode(email);
                SMTP.sendMail(email,challengeCode.getCode());
                return Response.status(NO_CONTENT)
                        .build();
            } else {
                return Response.status(BAD_REQUEST)
                        .entity(new MessageError("too many request", "Vous devez attendre 3 minutes avant de redemander un nouveau code"))
                        .build();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
            return Response.status(INTERNAL_SERVER_ERROR)
                    .entity(new MessageError("server error", "Un problème est survenu"))
                    .build();
        }
    }

    private static ChallengeCode generateChallengeCode(String contact) throws Exception {
        final String code = Utils.generateCode();
        final ChallengeCode challengeCode = new ChallengeCode(code);
        DB.addCodeChalenge(contact, challengeCode);
        return challengeCode;
    }


    @Path("/send-consent")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response sendGeneralConsent(
            @QueryParam("orientation") int orientation,
            @QueryParam("copy_to") String mail,
            @HeaderParam(AUTHORIZATION) String bearer,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("formulaireConsent") FormulaireConsent formulaireConsent
    ) {
        LOG.log(Level.INFO, "photo orientation : " + orientation);
        if (mail != null) {
            LOG.log(Level.INFO, "mail : " + mail);
        } else {
            LOG.log(Level.INFO, "pas de mail : ");
        }
        LOG.log(Level.INFO, "responsable is null ? " + (formulaireConsent.getRepresentant() == null));
        String token = bearer.substring(bearer.lastIndexOf(" ") + 1);
        int id = -1;
        try {
            id = Integer.valueOf(Utils.getSubjectJWSToken(token));
            LOG.log(Level.INFO, "id : " + id );
            if (!DB.valideToken(id)) {
                return Response.status(UNAUTHORIZED)
                        .entity(new MessageError("invalid token", "Vous n'avez pas la permission de poster"))
                        .build();
            }
        } catch (JwtException e) {
            return Response.status(UNAUTHORIZED)
                    .entity(new MessageError("invalid token", "Vous n'avez pas la permission de poster"))
                    .build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "error BDD : " + e.getMessage() );
        }
        try {
            final java.nio.file.Path path = Files.createTempFile("tempfiles", ".jpg");
            final int foundId = id;
                Files.copy(uploadedInputStream, path, StandardCopyOption.REPLACE_EXISTING);
                executor.execute(() -> {
                    try {
                        PDFCreator.create(path, formulaireConsent, mail);
                        DB.unValidToken(foundId);
                        LOG.log(Level.INFO, "fichier creer");
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "error pdf", e);
                    } finally {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, "error delete file", ex);
                        }
                    }
                });
                return Response.status(ACCEPTED).build();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "error file", ex);
            return Response.status(INTERNAL_SERVER_ERROR).build();
        }


    }


//    @Path("/formdata")
//    @POST
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public Response formadata(@@FormDataParam("formulaireConsent") FormulaireConsent formulaireConsent) {
//        try {
//            LOG.log(Level.INFO, "test form :" + formulaireConsent.getContact().getNom());
//            return Response.status(OK).build();
//        } catch (Exception e) {
//            LOG.log(Level.SEVERE, "error");
//            return Response.status(INTERNAL_SERVER_ERROR).entity(new MessageError("error",e.getMessage())).build();
//        }
//    }


}

