package metier;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import outils.Constant;

public class ShortMessageService {


    // TODO: A mettre dans var environment du docker
    private static final String SMS_BODY = "Votre code est : %s";


    public static void send (String phoneNumber,String code) {

        Twilio.init(Utils.getProps(Constant.TWILIO_PROPS,Constant.ACCOUNT_SID),Utils.getProps(Constant.TWILIO_PROPS,Constant.AUTH_TOKEN));
        Message message = Message.creator(new PhoneNumber(phoneNumber),new PhoneNumber(Utils.getProps(Constant.TWILIO_PROPS,Constant.PHONE_NUMBER)),String.format(SMS_BODY,code)).create();
        System.out.println(message.getSid());
    }

}
