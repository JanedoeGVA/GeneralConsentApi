package main;

import metier.*;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.File;
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

    @Path("/testdb")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String dbtest() {
        DB dao = new DB();
        try {
            dao.connectTest();
            return "it works";
        } catch (Exception e){
            return e.getMessage();
        }


    }





    // @Path("/send-sms")
    // @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendSMS(@QueryParam ("phone") String phone) {
        LOG.log(Level.INFO, "send SMS");
        final String code = Utils.generateCode();
        final Code challengeCode = new Code(code);
        ShortMessageService.send(phone, code);
        return Response.status(OK)
                .entity(challengeCode)
                .build();

    }

    //@Path("/send-mail")
    //@POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendMail(@QueryParam ("email") String email) {
        LOG.log(Level.INFO, "send email");
        final String code = Utils.generateCode();
        final Code challengeCode = new Code(code);
        try {
            SMTP.sendMail(email,code);
            return Response.status(OK).entity(challengeCode).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "error Exception ", ex);
            return Response.status(INTERNAL_SERVER_ERROR).build();
        }
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

