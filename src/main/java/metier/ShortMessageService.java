package metier;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import outils.Constant;

public class ShortMessageService {

    public static void send (String phoneNumber,String code) {
        Twilio.init(Utils.getProps(Constant.TWILIO_PROPS,Constant.ACCOUNT_SID),Utils.getProps(Constant.TWILIO_PROPS,Constant.AUTH_TOKEN));
        Message.creator(new PhoneNumber(phoneNumber),new PhoneNumber(Utils.getProps(Constant.TWILIO_PROPS,Constant.PHONE_NUMBER)),String.format(Constant.SMS_BODY,code)).create();
    }




}
