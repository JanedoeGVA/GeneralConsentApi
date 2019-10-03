package metier;

import outils.Constant;

import java.sql.*;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {

    private static final Logger LOG = Logger.getLogger(DB.class.getName());


    private static Connection getConnection() throws Exception {
        Connection connection = null;
        try {
            // This will load the MySQL driver, each DB has its own driver
            LOG.log(Level.INFO,"pass: " + Utils.getProps(Constant.POSTGRES_PROPS, Constant.DB_PASS));
            Class.forName("org.postgresql.Driver");
            // Setup the connection with the DB
            connection = DriverManager
                    .getConnection(System.getenv("POSTGRES_URL"),
                            System.getenv("POSTGRES_USER"), Utils.getProps(Constant.POSTGRES_PROPS, Constant.DB_PASS));
            LOG.log(Level.INFO,"Opened database successfully");
        } catch (Exception e) {
            LOG.log(Level.SEVERE,e.getMessage());
        }
        return connection;
    }

    public static boolean checkContactExist(String contact) throws Exception {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT CASE WHEN EXISTS (\n" +
                "    SELECT *\n" +
                "    FROM tbl_code_chalenge\n" +
                "    WHERE contact = ? and time >?\n" +
                ")\n" +
                "THEN CAST(1 AS BIT)\n" +
                "ELSE CAST(0 AS BIT) END;\n");
        statement.setString(1, contact);
        statement.setInt(2, (int) Utils.getValideEpochSecond());
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            LOG.log(Level.INFO,"Deja un code");
            statement.close();
            resultSet.close();
            connection.close();
            return true;
        } else {
            LOG.log(Level.INFO,"Pas encore de code");
            statement.close();
            resultSet.close();
            connection.close();
            return false;
        }
    }




}