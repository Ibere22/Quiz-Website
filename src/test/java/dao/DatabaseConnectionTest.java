package dao;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DatabaseConnection class
 * Tests database connectivity, error handling, and utility methods
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseConnectionTest {

    private static Connection testConnection;

    @BeforeAll
    static void setUpClass() {
        // Test if we can establish a connection for all tests
        try {
            testConnection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Warning: Could not establish database connection for tests: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownClass() {
        DatabaseConnection.closeConnection(testConnection);
    }

    // ========================= CONNECTION TESTS =========================

    @Test
    @Order(1)
    @DisplayName("Test default connection establishment")
    void testGetConnection_Default_Success() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                assertNotNull(conn, "Connection should not be null");
                assertFalse(conn.isClosed(), "Connection should be open");
                assertTrue(conn.isValid(5), "Connection should be valid");
            }
        }, "Default connection should be established without exceptions");
    }

    @Test
    @Order(2)
    @DisplayName("Test connection with custom credentials")
    void testGetConnection_CustomCredentials_Success() {
        // Assuming default credentials work, test with same credentials explicitly
        String username = "root";
        String password = "root";

        // Act & Assert
        assertDoesNotThrow(() -> {
            try (Connection conn = DatabaseConnection.getConnection(username, password)) {
                assertNotNull(conn, "Connection with custom credentials should not be null");
                assertFalse(conn.isClosed(), "Connection should be open");
                assertTrue(conn.isValid(5), "Connection should be valid");
            }
        }, "Connection with custom credentials should be established without exceptions");
    }

    @Test
    @Order(3)
    @DisplayName("Test connection with custom database name")
    void testGetConnection_CustomDatabase_Success() {
        // Test with an existing database (using same DB for test)
        String dbName = "quiz_website";
        String username = "root";
        String password = "root";

        // Act & Assert
        assertDoesNotThrow(() -> {
            try (Connection conn = DatabaseConnection.getConnection(dbName, username, password)) {
                assertNotNull(conn, "Connection with custom database should not be null");
                assertFalse(conn.isClosed(), "Connection should be open");
                assertTrue(conn.isValid(5), "Connection should be valid");
            }
        }, "Connection with custom database should be established without exceptions");
    }

    @Test
    @Order(4)
    @DisplayName("Test connection with invalid credentials")
    void testGetConnection_InvalidCredentials_ThrowsException() {
        String invalidUsername = "invalid_user";
        String invalidPassword = "invalid_password";

        // Act & Assert
        assertThrows(SQLException.class, () -> {
            DatabaseConnection.getConnection(invalidUsername, invalidPassword);
        }, "Connection with invalid credentials should throw SQLException");
    }

    @Test
    @Order(5)
    @DisplayName("Test connection with invalid database name")
    void testGetConnection_InvalidDatabase_ThrowsException() {
        String invalidDbName = "nonexistent_database_12345";
        String username = "root";
        String password = "root";

        // Act & Assert
        assertThrows(SQLException.class, () -> {
            DatabaseConnection.getConnection(invalidDbName, username, password);
        }, "Connection with invalid database should throw SQLException");
    }

    // ========================= CONNECTION TESTING METHODS =========================

    @Test
    @Order(6)
    @DisplayName("Test connection test with default credentials")
    void testTestConnection_Default_Success() {
        // Act
        boolean connectionWorking = DatabaseConnection.testConnection();

        // Assert
        assertTrue(connectionWorking, "Default connection test should return true");
    }

    @Test
    @Order(7)
    @DisplayName("Test connection test with valid custom credentials")
    void testTestConnection_ValidCredentials_Success() {
        String username = "root";
        String password = "root";

        // Act
        boolean connectionWorking = DatabaseConnection.testConnection(username, password);

        // Assert
        assertTrue(connectionWorking, "Connection test with valid credentials should return true");
    }

    @Test
    @Order(8)
    @DisplayName("Test connection test with invalid credentials")
    void testTestConnection_InvalidCredentials_ReturnsFalse() {
        String invalidUsername = "invalid_user";
        String invalidPassword = "invalid_password";

        // Act
        boolean connectionWorking = DatabaseConnection.testConnection(invalidUsername, invalidPassword);

        // Assert
        assertFalse(connectionWorking, "Connection test with invalid credentials should return false");
    }

    // ========================= CONNECTION CLOSING TESTS =========================

    @Test
    @Order(9)
    @DisplayName("Test close connection with valid connection")
    void testCloseConnection_ValidConnection_Success() throws SQLException {
        // Arrange
        Connection conn = DatabaseConnection.getConnection();
        assertFalse(conn.isClosed(), "Connection should initially be open");

        // Act
        assertDoesNotThrow(() -> {
            DatabaseConnection.closeConnection(conn);
        }, "Closing valid connection should not throw exception");

        // Assert
        assertTrue(conn.isClosed(), "Connection should be closed after calling closeConnection");
    }

    @Test
    @Order(10)
    @DisplayName("Test close connection with null connection")
    void testCloseConnection_NullConnection_NoException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            DatabaseConnection.closeConnection(null);
        }, "Closing null connection should not throw exception");
    }

    @Test
    @Order(11)
    @DisplayName("Test close connection with already closed connection")
    void testCloseConnection_AlreadyClosed_NoException() throws SQLException {
        // Arrange
        Connection conn = DatabaseConnection.getConnection();
        conn.close(); // Close it manually first
        assertTrue(conn.isClosed(), "Connection should be closed");

        // Act & Assert
        assertDoesNotThrow(() -> {
            DatabaseConnection.closeConnection(conn);
        }, "Closing already closed connection should not throw exception");
    }

    // ========================= UTILITY METHOD TESTS =========================

    @Test
    @Order(12)
    @DisplayName("Test get database URL")
    void testGetDatabaseUrl_ReturnsCorrectUrl() {
        // Act
        String dbUrl = DatabaseConnection.getDatabaseUrl();

        // Assert
        assertNotNull(dbUrl, "Database URL should not be null");
        assertTrue(dbUrl.startsWith("jdbc:mysql://"), "URL should start with jdbc:mysql://");
        assertTrue(dbUrl.contains("localhost"), "URL should contain localhost");
        assertTrue(dbUrl.contains("3306"), "URL should contain port 3306");
        assertTrue(dbUrl.contains("quiz_website"), "URL should contain database name");
        
        // Verify exact format
        String expectedUrl = "jdbc:mysql://localhost:3306/quiz_website";
        assertEquals(expectedUrl, dbUrl, "Database URL should match expected format");
    }

    @Test
    @Order(13)
    @DisplayName("Test get database name")
    void testGetDatabaseName_ReturnsCorrectName() {
        // Act
        String dbName = DatabaseConnection.getDatabaseName();

        // Assert
        assertNotNull(dbName, "Database name should not be null");
        assertEquals("quiz_website", dbName, "Database name should be 'quiz_website'");
    }

    // ========================= CONNECTION PROPERTIES TESTS =========================

    @Test
    @Order(14)
    @DisplayName("Test connection properties are properly set")
    void testConnectionProperties_ProperlyConfigured() throws SQLException {
        // Arrange & Act
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Assert connection is configured correctly
            assertNotNull(conn, "Connection should not be null");
            
            // Test if we can execute a simple query (indicates proper charset and timezone settings)
            try (Statement stmt = conn.createStatement()) {
                boolean hasResult = stmt.execute("SELECT 1");
                assertTrue(hasResult, "Should be able to execute simple query");
            }

            // Verify connection is using UTF-8 encoding by testing with Unicode
            try (Statement stmt = conn.createStatement()) {
                boolean hasResult = stmt.execute("SELECT 'Testing UTF-8: αβγ δεζ' as test_unicode");
                assertTrue(hasResult, "Should be able to handle Unicode characters");
            }
        }
    }

    @Test
    @Order(15)
    @DisplayName("Test multiple connections can be established")
    void testMultipleConnections_Success() throws SQLException {
        // Act & Assert
        try (Connection conn1 = DatabaseConnection.getConnection();
             Connection conn2 = DatabaseConnection.getConnection();
             Connection conn3 = DatabaseConnection.getConnection()) {
            
            assertNotNull(conn1, "First connection should not be null");
            assertNotNull(conn2, "Second connection should not be null");
            assertNotNull(conn3, "Third connection should not be null");
            
            assertFalse(conn1.isClosed(), "First connection should be open");
            assertFalse(conn2.isClosed(), "Second connection should be open");
            assertFalse(conn3.isClosed(), "Third connection should be open");
            
            // Verify all connections are independent
            assertNotSame(conn1, conn2, "Connections should be different instances");
            assertNotSame(conn2, conn3, "Connections should be different instances");
            assertNotSame(conn1, conn3, "Connections should be different instances");
        }
    }

    // ========================= CONNECTION VALIDITY TESTS =========================

    @Test
    @Order(16)
    @DisplayName("Test connection remains valid during use")
    void testConnectionValidity_RemainsValid() throws SQLException {
        // Arrange & Act
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Initial validity check
            assertTrue(conn.isValid(5), "Connection should be initially valid");
            
            // Perform some operations
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT 1");
                stmt.execute("SELECT NOW()");
                stmt.execute("SELECT DATABASE()");
            }
            
            // Check validity after operations
            assertTrue(conn.isValid(5), "Connection should remain valid after operations");
            assertFalse(conn.isClosed(), "Connection should not be closed");
        }
    }

    @Test
    @Order(17)
    @DisplayName("Test connection auto-commit setting")
    void testConnectionAutoCommit_DefaultSetting() throws SQLException {
        // Act
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Assert
            assertTrue(conn.getAutoCommit(), "Connection should have auto-commit enabled by default");
        }
    }

    @Test
    @Order(18)
    @DisplayName("Test connection transaction support")
    void testConnectionTransactions_Supported() throws SQLException {
        // Act
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Test transaction support
            assertDoesNotThrow(() -> {
                conn.setAutoCommit(false);
                conn.commit();
                conn.rollback();
                conn.setAutoCommit(true);
            }, "Connection should support transaction operations");
        }
    }

    // ========================= ERROR HANDLING TESTS =========================


    @Test
    @Order(20)
    @DisplayName("Test connection with empty credentials")
    void testGetConnection_EmptyCredentials_HandledGracefully() {
        // Act & Assert
        assertThrows(SQLException.class, () -> {
            DatabaseConnection.getConnection("", "");
        }, "Connection with empty credentials should throw SQLException");
    }

    // ========================= STRESS TEST =========================

    @Test
    @Order(21)
    @DisplayName("Test rapid connection creation and closing")
    void testRapidConnectionOperations_Performance() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    assertNotNull(conn, "Connection " + i + " should not be null");
                    assertTrue(conn.isValid(1), "Connection " + i + " should be valid");
                }
            }
        }, "Rapid connection operations should not cause issues");
    }
} 