package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbUtil {
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "quiz_website";
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "root";
    private static final Properties CONNECTION_PROPS = new Properties();

    static {
        //
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
        CONNECTION_PROPS.setProperty("useSSL", "false");
        CONNECTION_PROPS.setProperty("allowPublicKeyRetrieval", "true");
        CONNECTION_PROPS.setProperty("serverTimezone", "UTC");
        CONNECTION_PROPS.setProperty("autoReconnect", "true");
        CONNECTION_PROPS.setProperty("useUnicode", "true");
        CONNECTION_PROPS.setProperty("characterEncoding", "UTF-8");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public static Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(DB_URL, username, password);
    }
} 