package metier;

import outils.Constant;
import outils.Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShortMessageService {

    private static final Logger LOG = Logger.getLogger(ShortMessageService.class.getName());

    public static void send (String phoneNumber,String code) throws IOException {

        URL url = new URL("https://api.swisscom.com/messaging/sms");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        try {
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("client_id", Utils.getProps(Constant.SWISSCOM_PROPS, Constant.CUSTOMER_KEY));
            con.setRequestProperty("SCS-Version", "2");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(true);
            String from = "Consentement-General";
            String str = "{\"from\": \"" + from + "\",\"to\":\"" + phoneNumber + "\",\"text\":\" Votre code : " + code + "\"}";
            byte[] outputInBytes = str.getBytes("UTF-8");
            try (OutputStream os = con.getOutputStream()) {
                os.write(outputInBytes);
            }
            int status = con.getResponseCode();
            LOG.log(Level.INFO, "code :" + status);
        } finally {
            con.disconnect();
        }

    }






}
