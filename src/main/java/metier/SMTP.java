package metier;

import com.sendgrid.*;
import outils.Constant;
import outils.Utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

public class SMTP {

    private static final Logger LOG = Logger.getLogger(SMTP.class.getName());

    // TODO: Variable env ??
    private static final String EMAIL_SUBJECT = "Your General-consent challenge code";
    private static final String EMAIL_BODY = "Votre code est : %s";

    private static final String EMAIL_SUBJECT_FORM = "Formulaire de consentement";

    private static final String MAIL_SEND = "mail/send";

    private static String SMTP_PROPS = SMTP.class.getResource("/smtp.properties").getFile();

    public static void sendMail(String email, String code) throws IOException {
        final Email from = new Email(Utils.getProps(Constant.TWILIO_PROPS, Constant.EMAIL_FROM));
        final Email to = new Email(email);
        final String subject = EMAIL_SUBJECT;
        Content content = new Content(TEXT_PLAIN, String.format(EMAIL_BODY, code));
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(Utils.getProps(Constant.TWILIO_PROPS, Constant.SENDGRID_API_KEY));
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint(MAIL_SEND);
            request.setBody(mail.build());
            Response response = sg.api(request);
            LOG.log(Level.INFO, "status code :" + response.getStatusCode());
            LOG.log(Level.INFO, "body :" + response.getBody());
            LOG.log(Level.INFO, "headers :" + response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }

//    public static void sendFormConsent(Path pdfPath, String copyToMail) throws IOException {
//        final Email from = new Email(Utils.getProps(Constant.TWILIO_PROPS, Constant.EMAIL_FROM));
//        final Email to = new Email("xavier.costa@unige.ch");
//        final String subject = EMAIL_SUBJECT_FORM;
//        Content content = new Content(TEXT_PLAIN, "Formulaire de consentement envoye depuis l'application");
//        Mail mail = new Mail(from, subject, to, content);
//        if (copyToMail != null) {
//            Email bcc = new Email(copyToMail);
//            mail.getPersonalization().get(0).addBcc(bcc);
//        }
//        Attachments attachments = new Attachments();
//        attachments.setFilename("form_econsent.pdf");
//        attachments.setType("application/pdf");
//        attachments.setDisposition("attachment");
//        byte[] attachmentContentBytes = new byte[0];
//        try {
//            attachmentContentBytes = Files.readAllBytes(pdfPath);
//        } catch (IOException e) {
//            LOG.log(Level.SEVERE, "error attachement " + e.getMessage());
//        }
//        String attachmentContent = Base64.getEncoder().encodeToString(attachmentContentBytes);
//        attachments.setContent(attachmentContent);
//        mail.addAttachments(attachments);
//        SendGrid sg = new SendGrid(Utils.getProps(Constant.TWILIO_PROPS, Constant.SENDGRID_API_KEY));
//        Request request = new Request();
//        try {
//            request.setMethod(Method.POST);
//            request.setEndpoint(MAIL_SEND);
//            request.setBody(mail.build());
//            Response response = sg.api(request);
//            LOG.log(Level.INFO, "status code :" + response.getStatusCode());
//            LOG.log(Level.INFO, "body :" + response.getBody());
//            LOG.log(Level.INFO, "headers :" + response.getHeaders());
//        } catch (IOException ex) {
//            throw ex;
//        }
//
//    }

    public static void sendFormConsent(Path pdfPath,String copyToMail) throws MessagingException, IOException {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(SMTP_PROPS)) {
            prop.load(input);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE,"error smtp.properties",ex);
        }
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Utils.getProps(Constant.UNIGE_PROPS, Constant.FROM_MAIL), Utils.getProps(Constant.UNIGE_PROPS, Constant.MAIL_PASS));
            }
        });
        // Session session = Session.getInstance(prop,null);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(Utils.getProps(Constant.UNIGE_PROPS, Constant.FROM_MAIL)));
        if (copyToMail == null) {
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(Utils.getProps(Constant.UNIGE_PROPS, Constant.CONSENTEMENT_MAIL)));
        } else {
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(copyToMail));
            message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse(Utils.getProps(Constant.UNIGE_PROPS, Constant.CONSENTEMENT_MAIL)));
        }
        message.setSubject(EMAIL_SUBJECT_FORM);
        String msg = "Formulaire de consentement envoye depuis l'application";
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.attachFile(pdfPath.toFile(), "application/pdf", null);
        multipart.addBodyPart(attachmentBodyPart);
        message.setContent(multipart);
        Transport.send(message);


//        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
//        attachmentBodyPart.attachFile(new File("path/to/file"));
//        multipart.addBodyPart(attachmentBodyPart);


    }


}





