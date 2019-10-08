package metier;

import java.util.*;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cars.framework.secrets.DockerSecretLoadException;
import com.cars.framework.secrets.DockerSecrets;

public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    final static long WAIT_FOR = 180;

    public static String generateCode () {
        final Random ran = new Random();
        return String.format("%06d", ran.nextInt(1000000));
    }

    public static long getValideEpochSecond () {return Instant.now().getEpochSecond() - WAIT_FOR; }

    public static long getEpochSecond () {
        return Instant.now().getEpochSecond();
    }

    public static String getProps (String propsName, String value) {
        LOG.log(Level.INFO,"getProps");
        try {
            Map<String, String> secrets = DockerSecrets.loadFromFile(propsName);
            return secrets.get(value);
        } catch (DockerSecretLoadException e) {
            LOG.log(Level.SEVERE,e.getMessage(),e);
            return null;
        }
    }

    // Number must start with +417 and contains 8 digits after
//    public static final String VALID_NATEL_NUMBER_REGEX = "\\+417\\d\\d\\d\\d\\d\\d\\d\\d";
//
//    public static boolean checkPhone (String phone) {
//        final boolean match = phone.matches(VALID_NATEL_NUMBER_REGEX);
//        LOG.log(Level.INFO,"Number isMatching pattern" + match);
//        return match;
//    }

    public static final Pattern VALID_NATEL_NUMBER_REGEX = Pattern.compile("^\\+417\\d\\d\\d\\d\\d\\d\\d\\d$");
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validatePhone(String phone) {
        return validate(phone,VALID_NATEL_NUMBER_REGEX);
    }

    public static boolean validateMail(String email) {
        return validate(email,VALID_EMAIL_ADDRESS_REGEX);
    }

    private static boolean validate(String str,Pattern pattern) {
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

}
