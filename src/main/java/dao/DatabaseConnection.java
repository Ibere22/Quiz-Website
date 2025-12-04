package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection utility class
 * Manages database connections for the application
 */
public class DatabaseConnection {
    
    // Database connection parameters
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "quiz_website";
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    
    // Default credentials - should be moved to configuration file in production
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "root"; // root
    
    // Connection properties for better performance and reliability
    private static final Properties CONNECTION_PROPS = new Properties();
    
    static {
        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
        
        // Set connection properties
        CONNECTION_PROPS.setProperty("useSSL", "false");
        CONNECTION_PROPS.setProperty("allowPublicKeyRetrieval", "true");
        CONNECTION_PROPS.setProperty("serverTimezone", "UTC");
        CONNECTION_PROPS.setProperty("autoReconnect", "true");
        CONNECTION_PROPS.setProperty("useUnicode", "true");
        CONNECTION_PROPS.setProperty("characterEncoding", "UTF-8");
    }
    
    /**
     * Get a database connection with default credentials
     * @return Database connection
     * @throws SQLException If connection fails
     */
    public static Connection getConnection() throws SQLException {
        return getConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }
    
    /**
     * Get a database connection with custom credentials
     * @param username Database username
     * @param password Database password
     * @return Database connection
     * @throws SQLException If connection fails
     */
    public static Connection getConnection(String username, String password) throws SQLException {
        Properties props = new Properties(CONNECTION_PROPS);
        props.setProperty("user", username);
        props.setProperty("password", password);
        
        return DriverManager.getConnection(DB_URL, props);
    }
    
    /**
     * Get a database connection with custom database name
     * @param dbName Database name
     * @param username Database username
     * @param password Database password
     * @return Database connection
     * @throws SQLException If connection fails
     */
    public static Connection getConnection(String dbName, String username, String password) throws SQLException {
        String customUrl = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + dbName;
        Properties props = new Properties(CONNECTION_PROPS);
        props.setProperty("user", username);
        props.setProperty("password", password);
        
        return DriverManager.getConnection(customUrl, props);
    }
    
    /**
     * Test database connectivity
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Test database connectivity with custom credentials
     * @param username Database username
     * @param password Database password
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection(String username, String password) {
        try (Connection conn = getConnection(username, password)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close a database connection safely
     * @param connection The connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get database URL for reference
     * @return Database URL
     */
    public static String getDatabaseUrl() {
        return DB_URL;
    }
    
    /**
     * Get database name
     * @return Database name
     */
    public static String getDatabaseName() {
        return DB_NAME;
    }
} 