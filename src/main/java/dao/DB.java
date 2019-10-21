package dao;

import domain.ChallengeCode;
import outils.Constant;
import outils.Utils;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {

    private static final String TBL_CODE_CHALENGE = "tbl_code_chalenge";
    private static final String SQL_INSERT_CHALLENGE = "INSERT INTO " + TBL_CODE_CHALENGE + "(contact, code, time) VALUES (?,?,?)";
    private static final String SQL_CHALLENGE_EXIST = "SELECT * FROM tbl_code_chalenge WHERE contact = ? and time > ?";
    private static final String SQL_CHALLENGE_VALID = "SELECT id FROM tbl_code_chalenge WHERE contact = ? and code = ? and pending = true and time > ?";
    private static final String SQL_UPDATE_PENDING = "UPDATE tbl_code_chalenge SET pending = ? WHERE id = ?";


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

        long now = Utils.getValideEpochSecond();
        LOG.log(Level.INFO,"" + now);
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_CHALLENGE_EXIST);
        LOG.log(Level.INFO, "phone : " + contact);
        statement.setString(1, contact);
        statement.setInt(2, (int) now);
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

    public static void addCodeChalenge(String contact, ChallengeCode challengeCode) throws Exception {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_INSERT_CHALLENGE);
        LOG.log(Level.INFO, "contact : " + contact);
        statement.setString(1, contact);
        statement.setString(2, challengeCode.getCode());
        statement.setInt(3, (int) challengeCode.getCreateTime());
        int row = statement.executeUpdate();
        LOG.log(Level.INFO,"Nombres de lignes insere correctement : " + row);
        statement.close();
        connection.close();
    }

    public static int valideCode(String contact,String code) throws Exception {
        LOG.log(Level.INFO,"contact" +contact);
        LOG.log(Level.INFO,"code" + code);
        long expire = Utils.getExpireEpochSecond();
        LOG.log(Level.INFO,"expire" + expire);
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_CHALLENGE_VALID);
        statement.setString(1, contact);
        statement.setString(2, code);
        statement.setInt(3, (int) expire);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            LOG.log(Level.INFO,"Existe");
            int id = resultSet.getInt("id");
            statement.close();
            resultSet.close();
            connection.close();
            return id;
        } else {
            LOG.log(Level.INFO,"existe pas ou plus valide");
            statement.close();
            resultSet.close();
            connection.close();
            return -1;
        }


    }

    public static void unPendingCode(int id) throws Exception {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_PENDING);
        statement.setBoolean(1, false);
        statement.setInt(2, id);
        int row = statement.executeUpdate();
        LOG.log(Level.INFO,"Nombres de lignes mis a jour : " + row);
        statement.close();
        connection.close();
    }




}