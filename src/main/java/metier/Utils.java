package metier;

import java.util.*;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cars.framework.secrets.DockerSecretLoadException;
import com.cars.framework.secrets.DockerSecrets;

public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    final static long WAIT_FOR = 180;

    public static String generateCode () {
        final Random ran = new Random();
        return String.format("%04d", ran.nextInt(10000));
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

}
