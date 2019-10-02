package outils;

public class Constant {


    public static final String EMAIL_FROM = "email_from";

    // docker secret props and value
    public static final String TWILIO_PROPS ="twilio.properties";
    public static final String SENDGRID_API_KEY = "sendgrid_api_key";
    public static final String ACCOUNT_SID = "account_sid";
    public static final String AUTH_TOKEN = "auth_token";
    public static final String PHONE_NUMBER = "phone_number";


    // TODO: A mettre dans var environment du docker
    public static final String SMS_BODY = "Votre code est : %s";


}
