package outils;

import java.io.*;
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
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import dao.DB;

public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    private static final String SECRETS_DIR = "/run/secrets/";

    private static final String KEYSTORE_FILE = "general-consent.keystore";

    private static final String KEYSTORE_PASS_FILE = "keystore-secret";
    private static final String PASS_VALUE= "pass";
    private static final String ALIAS_VALUE = "alias";

    private static final String PAYLOAD_CODE = "code";

    final static long WAIT_FOR = 180;

    final static long EXPIRE_IN_MINUTES = 15;

    public static String generateCode () {
        final Random ran = new Random();
        return String.format("%06d", ran.nextInt(1000000));
    }

    public static long getValideEpochSecond () {return Instant.now().getEpochSecond() - WAIT_FOR; }

    public static long getExpireEpochSecond () {return Instant.now().getEpochSecond()- EXPIRE_IN_MINUTES*60; }

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

    private static Key getKeystore ()  {
        final Key key = null;
        try {
            final InputStream keyStoreFile = loadSecretFile(KEYSTORE_FILE);
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(keyStoreFile, getProps(KEYSTORE_PASS_FILE, PASS_VALUE).toCharArray());
            return keyStore.getKey(getProps(KEYSTORE_PASS_FILE, ALIAS_VALUE), getProps(KEYSTORE_PASS_FILE, PASS_VALUE).toCharArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return key;
    }

    public static String generateJWSToken (int id) {
        final Date exp = Date.from(Instant.now().plus(EXPIRE_IN_MINUTES, ChronoUnit.MINUTES));
        final Date now = Date.from(Instant.now());
        final String jws = Jwts.builder().setSubject(""+id).setIssuedAt(now).setExpiration(exp).signWith(SignatureAlgorithm.RS256, getKeystore()).compact();
        try {
            DB.unPendingCode(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.log(Level.INFO, "JWS : " + jws);
        return jws;
    }

    public static void JWSToken () {
        final Date exp = Date.from(Instant.now().plus(720000, ChronoUnit.MINUTES));
        final Date now = Date.from(Instant.now());
        final String jws = Jwts.builder().setSubject("bearer").setIssuedAt(now).setExpiration(exp).signWith(SignatureAlgorithm.RS256, getKeystore()).compact();
        LOG.log(Level.INFO, "JWS : " + jws);
    }

    public static String getSubjectJWSToken (String jwtToken) throws JwtException {
        return Jwts.parser().setSigningKey(getKeystore()).parseClaimsJws(jwtToken).getBody().getSubject();
    }

}
