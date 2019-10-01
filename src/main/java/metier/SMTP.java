package metier;

import com.sendgrid.*;
import outils.Constant;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

public class SMTP {

    private static final Logger LOG = Logger.getLogger(SMTP.class.getName());

    // TODO: Variable env ??
    private static final String EMAIL_SUBJECT = "Your General-consent challenge code";
    private static final String EMAIL_BODY = "Votre code est : %s";

    private static final String MAIL_SEND = "mail/send";

    public static void sendMail(String email,String code) throws IOException {
        final Email from = new Email(Utils.getProps(Constant.TWILIO_PROPS,Constant.EMAIL_FROM));
        final Email to = new Email(email);
        final String subject = EMAIL_SUBJECT;
        Content content = new Content(TEXT_PLAIN, String.format(EMAIL_BODY, code));
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(Utils.getProps(Constant.TWILIO_PROPS,Constant.SENDGRID_API_KEY));
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
        }}


}





