package metier;

import outils.Constant;
import outils.Utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Path;

import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;


public class SMTP {

    private static final Logger LOG = Logger.getLogger(SMTP.class.getName());

    private static final String EMAIL_SUBJECT_CODE = "Your General-consent challenge code";
    private static final String EMAIL_BODY = "Votre code est : %s";

    private static final String EMAIL_NO_REPLY = "noreply@unige.ch";

    private static final String EMAIL_SUBJECT_FORM = "Formulaire de consentement";

    private static String SMTP_PROPS = SMTP.class.getResource("/smtp.properties").getFile();

//

    private static Session getSession() {
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

        return session;
    }



    public static void sendMail(String email, String code) throws MessagingException {
        Message message = new MimeMessage(getSession());
        message.setFrom(new InternetAddress(Utils.getProps(Constant.UNIGE_PROPS, Constant.FROM_MAIL)));
        message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));
        message.setSubject(EMAIL_SUBJECT_CODE);
        String msg = String.format(EMAIL_BODY, code);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        message.setContent(multipart);
        Transport.send(message);
    }

    public static void sendFormConsent(Path pdfPath,String copyToMail) throws MessagingException, IOException {
        Message message = new MimeMessage(getSession());
        message.setFrom(new InternetAddress(Utils.getProps(Constant.UNIGE_PROPS, Constant.FROM_MAIL)));
        message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(Utils.getProps(Constant.UNIGE_PROPS, Constant.CONSENTEMENT_MAIL)));
        message.setSubject(EMAIL_SUBJECT_FORM);
        String msg = "Formulaire de consentement envoye depuis l'application";
//        if (copyToMail != null) {
//            msg = msg + "\r\n" + "La personne désire une copie à l'adresse suivante : " + copyToMail;
//        }
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.attachFile(pdfPath.toFile(), "application/pdf", null);
        multipart.addBodyPart(attachmentBodyPart);
        message.setContent(multipart);
        Transport.send(message);
    }


}





