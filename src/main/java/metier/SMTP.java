package metier;

import com.sendgrid.*;
import outils.Constant;
import outils.Utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
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

    public static void sendFormConsent(Path pdfPath, String copyToMail) throws IOException {
        final Email from = new Email(Utils.getProps(Constant.TWILIO_PROPS, Constant.EMAIL_FROM));
        final Email to = new Email("xavier.costa@unige.ch");
        final String subject = EMAIL_SUBJECT_FORM;
        Content content = new Content(TEXT_PLAIN, "Formulaire de consentement envoye depuis l'application");
        Mail mail = new Mail(from, subject, to, content);
        if (copyToMail != null) {
            Email bcc = new Email(copyToMail);
            mail.getPersonalization().get(0).addBcc(bcc);
        }
        Attachments attachments = new Attachments();
        attachments.setFilename("form_econsent.pdf");
        attachments.setType("application/pdf");
        attachments.setDisposition("attachment");
        byte[] attachmentContentBytes = new byte[0];
        try {
            attachmentContentBytes = Files.readAllBytes(pdfPath);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "error attachement " + e.getMessage());
        }
        String attachmentContent = Base64.getEncoder().encodeToString(attachmentContentBytes);
        attachments.setContent(attachmentContent);
        mail.addAttachments(attachments);
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

    public static void testSendMail() throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.unige.ch");
        prop.put("mail.smtp.port", "25");
        prop.put("mail.smtp.ssl.trust", "smtp.unige.ch");
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Utils.getProps(Constant.UNIGE_PROPS, Constant.UNIGE_MAIL), Utils.getProps(Constant.UNIGE_PROPS, Constant.UNIGE_PASS));
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(Utils.getProps(Constant.UNIGE_PROPS, Constant.UNIGE_MAIL)));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse("xavier.costa1227@gmail.com"));
        message.setSubject("Mail Subject");

        String msg = "This is my first email using JavaMailer";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);


//        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
//        attachmentBodyPart.attachFile(new File("path/to/file"));
//        multipart.addBodyPart(attachmentBodyPart);


    }


}





