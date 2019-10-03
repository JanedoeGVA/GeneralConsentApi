package metier;

import outils.Constant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {

    private static final Logger LOG = Logger.getLogger(DB.class.getName());


    public void connectTest() throws Exception {
        Connection connect = null;
        try {
            // This will load the MySQL driver, each DB has its own driver
            LOG.log(Level.INFO,"Opened database successfully");
            LOG.log(Level.INFO,"Opened database successfully");
            LOG.log(Level.INFO,"Opened database successfully");
            Class.forName("org.postgresql.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection(System.getenv("POSTGRES_URL"),
                    System.getenv("POSTGRES_USER"), Utils.getProps(Constant.POSTGRES_PROPS, Constant.DB_PASS));
            LOG.log(Level.INFO,"Opened database successfully");
        } catch (Exception e) {
            LOG.log(Level.SEVERE,e.getMessage());
        }

    }
}