package metier;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cars.framework.secrets.DockerSecretLoadException;
import com.cars.framework.secrets.DockerSecrets;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    private static final String SECRETS_DIR = "/run/secrets/";

    private static final String KEYSTORE_FILE = "general-consent.keystore";

    private static final String KEYSTORE_PASS_FILE = "keystore-secret";
    private static final String PASS_VALUE= "pass";
    private static final String ALIAS_VALUE = "alias";

    private static final String PAYLOAD_CODE = "code";

    final static long WAIT_FOR = 180;

    final static long EXPIRE_IN = 15;

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

    public static FileInputStream loadSecretFile (String fileName) throws FileNotFoundException {
        return new FileInputStream(SECRETS_DIR+ fileName);
    }

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

    public static String generateJWSToken (String contact,String code) {
        final InputStream keyStoreFile;
        String jws = null;
        try {
            keyStoreFile = loadSecretFile(KEYSTORE_FILE);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(keyStoreFile, getProps(KEYSTORE_PASS_FILE,PASS_VALUE).toCharArray());
            final Key key = keyStore.getKey(getProps(KEYSTORE_PASS_FILE,ALIAS_VALUE), getProps(KEYSTORE_PASS_FILE,PASS_VALUE).toCharArray());
            if (key == null) {
                LOG.log(Level.INFO, " key is null");
            }
            Date exp = Date.from(Instant.now().plus(EXPIRE_IN, ChronoUnit.MINUTES));
            Date now = Date.from(Instant.now());
            jws = Jwts.builder().setSubject(contact).setIssuedAt(now).setExpiration(exp).signWith(SignatureAlgorithm.RS256, key).claim(PAYLOAD_CODE, code).compact();
            LOG.log(Level.INFO, "JWS : " + jws);
        } catch (
                FileNotFoundException e) {
            LOG.log(Level.SEVERE, "Keystore file not found");
        } catch (
                KeyStoreException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        } catch (
                CertificateException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        } catch (
                NoSuchAlgorithmException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        } catch (
                IOException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        } catch (
                UnrecoverableKeyException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
        return jws;
    }

}
