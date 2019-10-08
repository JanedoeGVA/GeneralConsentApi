package main;

import metier.*;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/service")
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    @Path("/test")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String message() {
        LOG.log(Level.INFO, "Main");
        String template = "http://example.com/{name}/{age}";
        // UriTemplate uriTemplate = new UriTemplate(template);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name","Twilio");
        parameters.put("age","110");
        UriBuilder builder = UriBuilder.fromPath(template);
        // Use .buildFromMap()
        URI output = builder.buildFromMap(parameters);
        return output.toString();

    }

    @Path("/check-phone")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkPhone(@QueryParam ("phone") String phone) {
        LOG.log(Level.INFO, "check phone");
        LOG.log(Level.INFO, "phone : " + phone);
        final String match = (Utils.validatePhone(phone)) ? "le numero correspond a un numero de natel" : "le numero ne correspond pas";
        return Response.status(OK)
                .entity(match)
                .build();
    }

    @Path("/check-email")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkEmail(@QueryParam ("mail") String mail) {
        LOG.log(Level.INFO, "check mail");
        LOG.log(Level.INFO, "mail : " + mail);
        final String match = (Utils.validateMail(mail)) ? "correspond a un mail correct" : "ne correspond pas a un mail correct";
        return Response.status(OK)
                .entity(match)
                .build();
    }

    @Path("/send-sms")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendSMS(@QueryParam ("phone") String phone) {
        LOG.log(Level.INFO, "send SMS");
        LOG.log(Level.INFO, "phone : " + phone);
        if (!Utils.validatePhone(phone)) {
            return Response.status(BAD_REQUEST)
                    .entity("Le numéro ne correspond pas à un natel")
                    .build();
        }
        try {
            if (!DB.checkContactExist(phone)) {
                ChallengeCode challengeCode = generateChallengeCode(phone);
                ShortMessageService.send(phone, challengeCode.getCode());
                return Response.status(OK)
                        .entity(challengeCode)
                        .build();
            } else {
                return Response.status(BAD_REQUEST)
                        .entity("You have already request a challenge code, you have to wait 3 minutes to request a new one")
                        .build();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
            return Response.status(INTERNAL_SERVER_ERROR).build();
        }


    }

    @Path("/send-mail")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendMail(@QueryParam ("email") String email) {
        LOG.log(Level.INFO, "send email");
        if (!Utils.validateMail(email)) {
            return Response.status(BAD_REQUEST)
                    .entity("Le mail ne correspond pas à une adresse correct")
                    .build();
        }
        try {
            if (!DB.checkContactExist(email)) {
                ChallengeCode challengeCode = generateChallengeCode(email);
                SMTP.sendMail(email,challengeCode.getCode());
                return Response.status(OK)
                        .entity(challengeCode)
                        .build();
            } else {
                return Response.status(BAD_REQUEST)
                        .entity("You have already request a challenge code, you have to wait 3 minutes to request a new one")
                        .build();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
            return Response.status(INTERNAL_SERVER_ERROR).build();
        }
    }

    private static ChallengeCode generateChallengeCode (String contact) throws Exception {
        final String code = Utils.generateCode();
        final ChallengeCode challengeCode = new ChallengeCode(code);
        DB.addCodeChalenge(contact,challengeCode);
        return challengeCode;
    }

    // @Path("/send-consent")
    //@POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response sendGeneralConsent(@FormDataParam("file") InputStream uploadedInputStream,
                                       @FormDataParam("file") FormDataContentDisposition fileDetail) {

        try {
            final java.nio.file.Path path = Files.createTempFile("tempfiles", ".jpg");
            try {

                Files.copy(uploadedInputStream, path, StandardCopyOption.REPLACE_EXISTING);
                try {
                    PDF.create(path);
                } catch(Exception e) {
                    LOG.log(Level.SEVERE,"error pdf",e);
                    return Response.status(INTERNAL_SERVER_ERROR).build();
                }
            } finally {
                Files.deleteIfExists(path);
                return Response.status(OK).build();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE,"error file",ex);
            return Response.status(INTERNAL_SERVER_ERROR).build();
        }


    }


}

