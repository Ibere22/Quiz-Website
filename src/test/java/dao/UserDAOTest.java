package dao;

import model.User;
import org.junit.jupiter.api.*;
import util.PasswordHasher;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserDAO class
 * Tests all CRUD operations, authentication, validation, and statistics methods
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDAOTest {

    private static Connection connection;
    private static UserDAO userDAO;
    private static User testUser;
    private static final String TEST_USERNAME = "testuser123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "testpassword";

    @BeforeAll
    static void setUpClass() throws SQLException {
        // Use test database or in-memory database
        connection = DatabaseConnection.getConnection();
        userDAO = new UserDAO(connection);
        
        // Clean up any existing test data
        cleanUpTestData();
    }

    @AfterAll
    static void tearDownClass() throws SQLException {
        cleanUpTestData();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Ensure clean state before each test
        cleanUpTestData();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up after each test
        cleanUpTestData();
    }

    private static void cleanUpTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Delete all test-related users with more comprehensive patterns
            stmt.executeUpdate("DELETE FROM users WHERE " +
                "username LIKE 'test%' " +
                "OR email LIKE 'test%' " +
                "OR username LIKE '%user%' " +
                "OR username = 'anotheruser' " +
                "OR username = 'updateduser' " +
                "OR username = 'newusername' " +
                "OR username = 'availableusername' " +
                "OR username = 'differentuser' " +
                "OR username = 'nonexistentuser' " +
                "OR email = 'updated@example.com' " +
                "OR email = 'newemail@example.com' " +
                "OR email = 'available@example.com' " +
                "OR email = 'different@email.com' " +
                "OR email LIKE '%@example.com'");
        }
    }

    // ========================= CREATE OPERATION TESTS =========================

    @Test
    @Order(1)
    @DisplayName("Test user registration with valid data")
    void testRegisterUser_ValidData_Success() throws SQLException {
        // Act
        User createdUser = userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Assert
        assertNotNull(createdUser, "User should be created successfully");
        assertTrue(createdUser.getUserId() > 0, "User ID should be generated");
        assertEquals(TEST_USERNAME, createdUser.getUsername());
        assertEquals(TEST_EMAIL, createdUser.getEmail());
        assertNotNull(createdUser.getPasswordHash(), "Password should be hashed");
        assertNotEquals(TEST_PASSWORD, createdUser.getPasswordHash(), "Password should be hashed");
        assertFalse(createdUser.isAdmin(), "New user should not be admin by default");
        
        testUser = createdUser; // Store for cleanup
    }

    @Test
    @Order(2)
    @DisplayName("Test user creation with User object")
    void testCreateUser_ValidUser_Success() throws SQLException {
        // Arrange
        String hashedPassword = PasswordHasher.hashPassword(TEST_PASSWORD);
        User user = new User(TEST_USERNAME, hashedPassword, TEST_EMAIL);

        // Act
        User createdUser = userDAO.createUser(user);

        // Assert
        assertNotNull(createdUser, "User should be created successfully");
        assertTrue(createdUser.getUserId() > 0, "User ID should be generated");
        assertEquals(TEST_USERNAME, createdUser.getUsername());
        assertEquals(TEST_EMAIL, createdUser.getEmail());
        assertEquals(hashedPassword, createdUser.getPasswordHash());
        
        testUser = createdUser; // Store for cleanup
    }

    @Test
    @Order(3)
    @DisplayName("Test user registration with duplicate username")
    void testRegisterUser_DuplicateUsername_Failure() throws SQLException {
        // Arrange - create first user
        userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Act & Assert
        assertThrows(SQLException.class, () -> {
            userDAO.registerUser(TEST_USERNAME, "differentpassword", "different@email.com");
        }, "Should throw SQLException for duplicate username");
    }

    @Test
    @Order(4)
    @DisplayName("Test user registration with duplicate email")
    void testRegisterUser_DuplicateEmail_Failure() throws SQLException {
        // Arrange - create first user
        userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Act & Assert
        assertThrows(SQLException.class, () -> {
            userDAO.registerUser("differentuser", TEST_PASSWORD, TEST_EMAIL);
        }, "Should throw SQLException for duplicate email");
    }

    // ========================= READ OPERATION TESTS =========================

    @Test
    @Order(5)
    @DisplayName("Test find user by ID")
    void testFindById_ExistingUser_Success() throws SQLException {
        // Arrange
        User createdUser = userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Act
        User foundUser = userDAO.findById(createdUser.getUserId());

        // Assert
        assertNotNull(foundUser, "User should be found");
        assertEquals(createdUser.getUserId(), foundUser.getUserId());
        assertEquals(TEST_USERNAME, foundUser.getUsername());
        assertEquals(TEST_EMAIL, foundUser.getEmail());
    }

    @Test
    @Order(6)
    @DisplayName("Test find user by non-existent ID")
    void testFindById_NonExistentUser_ReturnsNull() throws SQLException {
        // Act
        User foundUser = userDAO.findById(99999);

        // Assert
        assertNull(foundUser, "Should return null for non-existent user");
    }

    @Test
    @Order(7)
    @DisplayName("Test find user by username")
    void testFindByUsername_ExistingUser_Success() throws SQLException {
        // Arrange
        userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Act
        User foundUser = userDAO.findByUsername(TEST_USERNAME);

        // Assert
        assertNotNull(foundUser, "User should be found");
        assertEquals(TEST_USERNAME, foundUser.getUsername());
        assertEquals(TEST_EMAIL, foundUser.getEmail());
    }

    @Test
    @Order(8)
    @DisplayName("Test find user by non-existent username")
    void testFindByUsername_NonExistentUser_ReturnsNull() throws SQLException {
        // Act
        User foundUser = userDAO.findByUsername("nonexistentuser");

        // Assert
        assertNull(foundUser, "Should return null for non-existent username");
    }

    @Test
    @Order(9)
    @DisplayName("Test find user by email")
    void testFindByEmail_ExistingUser_Success() throws SQLException {
        // Arrange
        userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Act
        User foundUser = userDAO.findByEmail(TEST_EMAIL);

        // Assert
        assertNotNull(foundUser, "User should be found");
        assertEquals(TEST_USERNAME, foundUser.getUsername());
        assertEquals(TEST_EMAIL, foundUser.getEmail());
    }

    @Test
    @Order(10)
    @DisplayName("Test get all users")
    void testGetAllUsers_Success() throws SQLException {
        // Arrange
        userDAO.registerUser("testuser1", TEST_PASSWORD, "test1@example.com");
        userDAO.registerUser("testuser2", TEST_PASSWORD, "test2@example.com");

        // Act
        List<User> allUsers = userDAO.getAllUsers();

        // Assert
        assertNotNull(allUsers, "Should return list of users");
        assertTrue(allUsers.size() >= 2, "Should return at least 2 test users");
    }

    @Test
    @Order(11)
    @DisplayName("Test get all users with pagination")
    void testGetAllUsers_WithPagination_Success() throws SQLException {
        // Arrange
        userDAO.registerUser("testuser1", TEST_PASSWORD, "test1@example.com");
        userDAO.registerUser("testuser2", TEST_PASSWORD, "test2@example.com");
        userDAO.registerUser("testuser3", TEST_PASSWORD, "test3@example.com");

        // Act
        List<User> firstPage = userDAO.getAllUsers(0, 2);
        List<User> secondPage = userDAO.getAllUsers(2, 2);

        // Assert
        assertNotNull(firstPage, "First page should not be null");
        assertNotNull(secondPage, "Second page should not be null");
        assertTrue(firstPage.size() <= 2, "First page should have at most 2 users");
        assertTrue(secondPage.size() >= 1, "Second page should have at least 1 user");
    }

    @Test
    @Order(12)
    @DisplayName("Test search users by username pattern")
    void testSearchByUsername_ValidPattern_Success() throws SQLException {
        // Arrange
        userDAO.registerUser("testuser1", TEST_PASSWORD, "test1@example.com");
        userDAO.registerUser("testuser2", TEST_PASSWORD, "test2@example.com");
        userDAO.registerUser("anotheruser", TEST_PASSWORD, "another@example.com");

        // Act
        List<User> searchResults = userDAO.searchByUsername("%testuser%");

        // Assert
        assertNotNull(searchResults, "Search results should not be null");
        assertEquals(2, searchResults.size(), "Should find 2 users matching pattern");
        assertTrue(searchResults.stream().allMatch(u -> u.getUsername().contains("testuser")));
    }

    // ========================= UPDATE OPERATION TESTS =========================

    @Test
    @Order(13)
    @DisplayName("Test update user information")
    void testUpdateUser_ValidData_Success() throws SQLException {
        // Arrange
        User createdUser = userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        createdUser.setUsername("updateduser");
        createdUser.setEmail("updated@example.com");

        // Act
        boolean updated = userDAO.updateUser(createdUser);

        // Assert
        assertTrue(updated, "User update should be successful");
        
        User foundUser = userDAO.findById(createdUser.getUserId());
        assertEquals("updateduser", foundUser.getUsername());
        assertEquals("updated@example.com", foundUser.getEmail());
    }

    @Test
    @Order(14)
    @DisplayName("Test update user password")
    void testUpdatePassword_ValidData_Success() throws SQLException {
        // Arrange
        User createdUser = userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        String newPassword = "newpassword123";

        // Act
        boolean updated = userDAO.updatePassword(createdUser.getUserId(), newPassword);

        // Assert
        assertTrue(updated, "Password update should be successful");
        
        // Verify authentication with new password
        User authenticatedUser = userDAO.authenticateUser(TEST_USERNAME, newPassword);
        assertNotNull(authenticatedUser, "Should authenticate with new password");
    }

    @Test
    @Order(15)
    @DisplayName("Test update user email")
    void testUpdateEmail_ValidData_Success() throws SQLException {
        // Arrange
        User createdUser = userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        String newEmail = "newemail@example.com";

        // Act
        boolean updated = userDAO.updateEmail(createdUser.getUserId(), newEmail);

        // Assert
        assertTrue(updated, "Email update should be successful");
        
        User foundUser = userDAO.findById(createdUser.getUserId());
        assertEquals(newEmail, foundUser.getEmail());
    }

    @Test
    @Order(16)
    @DisplayName("Test update username")
    void testUpdateUsername_ValidData_Success() throws SQLException {
        // Arrange
        User createdUser = userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        String newUsername = "newusername";

        // Act
        boolean updated = userDAO.updateUsername(createdUser.getUserId(), newUsername);

        // Assert
        assertTrue(updated, "Username update should be successful");
        
        User foundUser = userDAO.findById(createdUser.getUserId());
        assertEquals(newUsername, foundUser.getUsername());
    }

    @Test
    @Order(17)
    @DisplayName("Test set admin status")
    void testSetAdminStatus_ValidData_Success() throws SQLException {
        // Arrange
        User createdUser = userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Act
        boolean updated = userDAO.setAdminStatus(createdUser.getUserId(), true);

        // Assert
        assertTrue(updated, "Admin status update should be successful");
        
        User foundUser = userDAO.findById(createdUser.getUserId());
        assertTrue(foundUser.isAdmin(), "User should be admin");
    }

    // ========================= DELETE OPERATION TESTS =========================

    @Test
    @Order(18)
    @DisplayName("Test delete user by ID")
    void testDeleteUser_ExistingUser_Success() throws SQLException {
        // Arrange
        User createdUser = userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Act
        boolean deleted = userDAO.deleteUser(createdUser.getUserId());

        // Assert
        assertTrue(deleted, "User deletion should be successful");
        
        User foundUser = userDAO.findById(createdUser.getUserId());
        assertNull(foundUser, "User should not be found after deletion");
    }

    @Test
    @Order(19)
    @DisplayName("Test delete user by username")
    void testDeleteUserByUsername_ExistingUser_Success() throws SQLException {
        // Arrange
        userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Act
        boolean deleted = userDAO.deleteUserByUsername(TEST_USERNAME);

        // Assert
        assertTrue(deleted, "User deletion should be successful");
        
        User foundUser = userDAO.findByUsername(TEST_USERNAME);
        assertNull(foundUser, "User should not be found after deletion");
    }

    // ========================= AUTHENTICATION TESTS =========================

    @Test
    @Order(20)
    @DisplayName("Test authenticate user with valid credentials")
    void testAuthenticateUser_ValidCredentials_Success() throws SQLException {
        // Arrange
        userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Act
        User authenticatedUser = userDAO.authenticateUser(TEST_USERNAME, TEST_PASSWORD);

        // Assert
        assertNotNull(authenticatedUser, "Authentication should be successful");
        assertEquals(TEST_USERNAME, authenticatedUser.getUsername());
    }

    @Test
    @Order(21)
    @DisplayName("Test authenticate user with invalid password")
    void testAuthenticateUser_InvalidPassword_ReturnsNull() throws SQLException {
        // Arrange
        userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Act
        User authenticatedUser = userDAO.authenticateUser(TEST_USERNAME, "wrongpassword");

        // Assert
        assertNull(authenticatedUser, "Authentication should fail with wrong password");
    }

    @Test
    @Order(22)
    @DisplayName("Test authenticate user by email")
    void testAuthenticateUserByEmail_ValidCredentials_Success() throws SQLException {
        // Arrange
        userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Act
        User authenticatedUser = userDAO.authenticateUserByEmail(TEST_EMAIL, TEST_PASSWORD);

        // Assert
        assertNotNull(authenticatedUser, "Authentication by email should be successful");
        assertEquals(TEST_EMAIL, authenticatedUser.getEmail());
    }

    // ========================= VALIDATION TESTS =========================

    @Test
    @Order(23)
    @DisplayName("Test username availability check")
    void testIsUsernameAvailable_Success() throws SQLException {
        // Test available username
        assertTrue(userDAO.isUsernameAvailable("availableusername"), 
                  "Should return true for available username");

        // Arrange - create user
        userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Test taken username
        assertFalse(userDAO.isUsernameAvailable(TEST_USERNAME), 
                   "Should return false for taken username");
    }

    @Test
    @Order(24)
    @DisplayName("Test email availability check")
    void testIsEmailAvailable_Success() throws SQLException {
        // Test available email
        assertTrue(userDAO.isEmailAvailable("available@example.com"), 
                  "Should return true for available email");

        // Arrange - create user
        userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Test taken email
        assertFalse(userDAO.isEmailAvailable(TEST_EMAIL), 
                   "Should return false for taken email");
    }

    @Test
    @Order(25)
    @DisplayName("Test user exists check")
    void testUserExists_Success() throws SQLException {
        // Test non-existent user
        assertFalse(userDAO.userExists(99999), 
                   "Should return false for non-existent user");

        // Arrange - create user
        User createdUser = userDAO.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

        // Test existing user
        assertTrue(userDAO.userExists(createdUser.getUserId()), 
                  "Should return true for existing user");
    }

    // ========================= STATISTICS TESTS =========================

    @Test
    @Order(26)
    @DisplayName("Test get total user count")
    void testGetTotalUserCount_Success() throws SQLException {
        // Arrange
        int initialCount = userDAO.getTotalUserCount();
        userDAO.registerUser("testuser1", TEST_PASSWORD, "test1@example.com");
        userDAO.registerUser("testuser2", TEST_PASSWORD, "test2@example.com");

        // Act
        int finalCount = userDAO.getTotalUserCount();

        // Assert
        assertEquals(initialCount + 2, finalCount, "User count should increase by 2");
    }

    @Test
    @Order(27)
    @DisplayName("Test get admin user count")
    void testGetAdminUserCount_Success() throws SQLException {
        // Arrange
        int initialAdminCount = userDAO.getAdminUserCount();
        User regularUser = userDAO.registerUser("testuser1", TEST_PASSWORD, "test1@example.com");
        User adminUser = userDAO.registerUser("testuser2", TEST_PASSWORD, "test2@example.com");
        userDAO.setAdminStatus(adminUser.getUserId(), true);

        // Act
        int finalAdminCount = userDAO.getAdminUserCount();

        // Assert
        assertEquals(initialAdminCount + 1, finalAdminCount, "Admin count should increase by 1");
    }

    @Test
    @Order(28)
    @DisplayName("Test get recent registration count")
    void testGetRecentRegistrationCount_Success() throws SQLException {
        // Arrange
        userDAO.registerUser("testuser1", TEST_PASSWORD, "test1@example.com");
        userDAO.registerUser("testuser2", TEST_PASSWORD, "test2@example.com");

        // Act
        int recentCount = userDAO.getRecentRegistrationCount(1); // Last 1 day

        // Assert
        assertTrue(recentCount >= 2, "Should return at least 2 recent registrations");
    }

    @Test
    @Order(29)
    @DisplayName("Test get admin users")
    void testGetAdminUsers_Success() throws SQLException {
        // Arrange
        User adminUser = userDAO.registerUser("testadmin", TEST_PASSWORD, "admin@example.com");
        userDAO.setAdminStatus(adminUser.getUserId(), true);

        // Act
        List<User> adminUsers = userDAO.getAdminUsers();

        // Assert
        assertNotNull(adminUsers, "Admin users list should not be null");
        assertTrue(adminUsers.stream().anyMatch(u -> u.getUserId() == adminUser.getUserId()), 
                  "Should include the test admin user");
        assertTrue(adminUsers.stream().allMatch(User::isAdmin), 
                  "All returned users should be admins");
    }

    @Test
    @Order(30)
    @DisplayName("Test get recently registered users")
    void testGetRecentlyRegistered_Success() throws SQLException {
        // Arrange
        userDAO.registerUser("testuser1", TEST_PASSWORD, "test1@example.com");
        userDAO.registerUser("testuser2", TEST_PASSWORD, "test2@example.com");

        // Act
        List<User> recentUsers = userDAO.getRecentlyRegistered(1, 10); // Last 1 day, max 10

        // Assert
        assertNotNull(recentUsers, "Recent users list should not be null");
        assertTrue(recentUsers.size() >= 2, "Should return at least 2 recent users");
    }
} 