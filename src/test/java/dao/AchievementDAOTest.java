package dao;

import model.Achievement;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AchievementDAO class
 * Tests all CRUD operations, achievement-specific functionality, validation methods, and statistics
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AchievementDAOTest {

    private static Connection connection;
    private static AchievementDAO achievementDAO;
    private static final int TEST_USER_ID = 1;
    private static final int TEST_USER_ID_2 = 2;
    private static final String TEST_ACHIEVEMENT_TYPE = Achievement.AMATEUR_AUTHOR;
    private static final String TEST_ACHIEVEMENT_TYPE_2 = Achievement.QUIZ_MACHINE;
    private static final String TEST_DESCRIPTION = "Test achievement description";

    @BeforeAll
    static void setUpClass() throws SQLException {
        connection = DatabaseConnection.getConnection();
        achievementDAO = new AchievementDAO(connection);
        
        // Clean up any existing test data
        cleanUpTestData();
        
        // Create test users if they don't exist
        createTestData();
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
        cleanUpTestAchievements();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up after each test
        cleanUpTestAchievements();
    }

    private static void cleanUpTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Delete achievements from test users first
            stmt.executeUpdate("DELETE FROM achievements WHERE user_id IN (1, 2)");
            // Note: We don't delete test users as they might be used by other tests
        }
    }

    private static void cleanUpTestAchievements() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Delete ALL achievements for a truly clean state
            stmt.executeUpdate("DELETE FROM achievements");
        }
    }

    private static void createTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Insert test users if they don't exist
            stmt.executeUpdate("INSERT IGNORE INTO users (id, username, password_hash, email) VALUES " +
                "(1, 'testuser1', 'hash1', 'test1@example.com'), " +
                "(2, 'testuser2', 'hash2', 'test2@example.com')");
        }
    }

    // ========================= CREATE OPERATION TESTS =========================

    @Test
    @Order(1)
    @DisplayName("Test create achievement with object")
    void testCreateAchievement_Success() throws SQLException {
        // Arrange
        Achievement achievement = new Achievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, TEST_DESCRIPTION);

        // Act
        Achievement createdAchievement = achievementDAO.createAchievement(achievement);

        // Assert
        assertNotNull(createdAchievement, "Achievement should be created successfully");
        assertTrue(createdAchievement.getAchievementId() > 0, "Achievement ID should be generated");
        assertEquals(TEST_USER_ID, createdAchievement.getUserId());
        assertEquals(TEST_ACHIEVEMENT_TYPE, createdAchievement.getAchievementType());
        assertEquals(TEST_DESCRIPTION, createdAchievement.getDescription());
        assertNotNull(createdAchievement.getDateEarned());
    }

    @Test
    @Order(2)
    @DisplayName("Test create simple achievement")
    void testCreateSimpleAchievement_Success() throws SQLException {
        // Act
        Achievement createdAchievement = achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, TEST_DESCRIPTION);

        // Assert
        assertNotNull(createdAchievement, "Achievement should be created successfully");
        assertTrue(createdAchievement.getAchievementId() > 0, "Achievement ID should be generated");
        assertEquals(TEST_USER_ID, createdAchievement.getUserId());
        assertEquals(TEST_ACHIEVEMENT_TYPE, createdAchievement.getAchievementType());
        assertEquals(TEST_DESCRIPTION, createdAchievement.getDescription());
    }

    @Test
    @Order(3)
    @DisplayName("Test award achievement - first time")
    void testAwardAchievement_FirstTime_Success() throws SQLException {
        // Act
        Achievement awardedAchievement = achievementDAO.awardAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE);

        // Assert
        assertNotNull(awardedAchievement, "Achievement should be awarded successfully");
        assertTrue(awardedAchievement.getAchievementId() > 0, "Achievement ID should be generated");
        assertEquals(TEST_USER_ID, awardedAchievement.getUserId());
        assertEquals(TEST_ACHIEVEMENT_TYPE, awardedAchievement.getAchievementType());
        assertNotNull(awardedAchievement.getDescription(), "Description should be set");
    }

    @Test
    @Order(4)
    @DisplayName("Test award achievement - duplicate attempt")
    void testAwardAchievement_Duplicate_ReturnsNull() throws SQLException {
        // Arrange - create achievement first
        achievementDAO.awardAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE);

        // Act - try to award same achievement again
        Achievement duplicateAchievement = achievementDAO.awardAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE);

        // Assert
        assertNull(duplicateAchievement, "Should return null for duplicate achievement");
    }

    // ========================= READ OPERATION TESTS =========================

    @Test
    @Order(5)
    @DisplayName("Test find achievement by ID")
    void testFindById_ExistingAchievement_Success() throws SQLException {
        // Arrange
        Achievement createdAchievement = achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, TEST_DESCRIPTION);

        // Act
        Achievement foundAchievement = achievementDAO.findById(createdAchievement.getAchievementId());

        // Assert
        assertNotNull(foundAchievement, "Achievement should be found");
        assertEquals(createdAchievement.getAchievementId(), foundAchievement.getAchievementId());
        assertEquals(TEST_USER_ID, foundAchievement.getUserId());
        assertEquals(TEST_ACHIEVEMENT_TYPE, foundAchievement.getAchievementType());
        assertEquals(TEST_DESCRIPTION, foundAchievement.getDescription());
    }

    @Test
    @Order(6)
    @DisplayName("Test find achievement by non-existent ID")
    void testFindById_NonExistentAchievement_ReturnsNull() throws SQLException {
        // Act
        Achievement foundAchievement = achievementDAO.findById(99999);

        // Assert
        assertNull(foundAchievement, "Should return null for non-existent achievement");
    }

    @Test
    @Order(7)
    @DisplayName("Test get achievements by user")
    void testGetAchievementsByUser_Success() throws SQLException {
        // Arrange
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE_2, "Achievement 2");
        achievementDAO.createSimpleAchievement(TEST_USER_ID_2, TEST_ACHIEVEMENT_TYPE, "Achievement 3");

        // Act
        List<Achievement> user1Achievements = achievementDAO.getAchievementsByUser(TEST_USER_ID);
        List<Achievement> user2Achievements = achievementDAO.getAchievementsByUser(TEST_USER_ID_2);

        // Assert
        assertEquals(2, user1Achievements.size(), "Should return 2 achievements for user 1");
        assertEquals(1, user2Achievements.size(), "Should return 1 achievement for user 2");
        
        // Verify all achievements belong to the correct user
        assertTrue(user1Achievements.stream().allMatch(a -> a.getUserId() == TEST_USER_ID));
        assertTrue(user2Achievements.stream().allMatch(a -> a.getUserId() == TEST_USER_ID_2));
    }

    @Test
    @Order(8)
    @DisplayName("Test get achievements by type")
    void testGetAchievementsByType_Success() throws SQLException {
        // Arrange
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID_2, TEST_ACHIEVEMENT_TYPE, "Achievement 2");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE_2, "Achievement 3");

        // Act
        List<Achievement> type1Achievements = achievementDAO.getAchievementsByType(TEST_ACHIEVEMENT_TYPE);
        List<Achievement> type2Achievements = achievementDAO.getAchievementsByType(TEST_ACHIEVEMENT_TYPE_2);

        // Assert
        assertEquals(2, type1Achievements.size(), "Should return 2 achievements for type 1");
        assertEquals(1, type2Achievements.size(), "Should return 1 achievement for type 2");
        
        // Verify all achievements belong to the correct type
        assertTrue(type1Achievements.stream().allMatch(a -> a.getAchievementType().equals(TEST_ACHIEVEMENT_TYPE)));
        assertTrue(type2Achievements.stream().allMatch(a -> a.getAchievementType().equals(TEST_ACHIEVEMENT_TYPE_2)));
    }

    @Test
    @Order(9)
    @DisplayName("Test get all achievements without pagination")
    void testGetAllAchievements_WithoutPagination_Success() throws SQLException {
        // Arrange
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID_2, TEST_ACHIEVEMENT_TYPE_2, "Achievement 2");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, Achievement.PRACTICE_MAKES_PERFECT, "Achievement 3");

        // Act
        List<Achievement> allAchievements = achievementDAO.getAllAchievements();

        // Assert
        assertTrue(allAchievements.size() >= 3, "Should return at least 3 achievements");
        
        // Verify achievements are sorted by date_earned DESC (most recent first)
        for (int i = 0; i < allAchievements.size() - 1; i++) {
            assertTrue(allAchievements.get(i).getDateEarned().compareTo(allAchievements.get(i + 1).getDateEarned()) >= 0,
                "Achievements should be sorted by date earned in descending order");
        }
    }

    @Test
    @Order(10)
    @DisplayName("Test get all achievements with pagination")
    void testGetAllAchievements_WithPagination_Success() throws SQLException {
        // Arrange
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID_2, TEST_ACHIEVEMENT_TYPE_2, "Achievement 2");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, Achievement.PRACTICE_MAKES_PERFECT, "Achievement 3");

        // Act
        List<Achievement> firstPage = achievementDAO.getAllAchievements(0, 2);
        List<Achievement> secondPage = achievementDAO.getAllAchievements(2, 2);

        // Assert
        assertEquals(2, firstPage.size(), "First page should have 2 achievements");
        assertTrue(secondPage.size() >= 1, "Second page should have at least 1 achievement");
    }

    @Test
    @Order(11)
    @DisplayName("Test get achievements by user with pagination")
    void testGetAchievementsByUser_WithPagination_Success() throws SQLException {
        // Arrange
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE_2, "Achievement 2");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, Achievement.PRACTICE_MAKES_PERFECT, "Achievement 3");

        // Act
        List<Achievement> firstPage = achievementDAO.getAchievementsByUser(TEST_USER_ID, 0, 2);
        List<Achievement> secondPage = achievementDAO.getAchievementsByUser(TEST_USER_ID, 2, 2);

        // Assert
        assertEquals(2, firstPage.size(), "First page should have 2 achievements");
        assertEquals(1, secondPage.size(), "Second page should have 1 achievement");
    }

    @Test
    @Order(12)
    @DisplayName("Test get recent achievements")
    void testGetRecentAchievements_Success() throws SQLException {
        // Arrange
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Recent Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID_2, TEST_ACHIEVEMENT_TYPE_2, "Recent Achievement 2");

        // Act
        List<Achievement> recentAchievements = achievementDAO.getRecentAchievements(7, 10); // Last 7 days, max 10 results

        // Assert
        assertTrue(recentAchievements.size() >= 2, "Should return at least 2 recent achievements");
    }

    @Test
    @Order(13)
    @DisplayName("Test get users with achievement")
    void testGetUsersWithAchievement_Success() throws SQLException {
        // Arrange
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID_2, TEST_ACHIEVEMENT_TYPE, "Achievement 2");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE_2, "Achievement 3");

        // Act
        List<Integer> usersWithType1 = achievementDAO.getUsersWithAchievement(TEST_ACHIEVEMENT_TYPE, 10);
        List<Integer> usersWithType2 = achievementDAO.getUsersWithAchievement(TEST_ACHIEVEMENT_TYPE_2, 10);

        // Assert
        assertEquals(2, usersWithType1.size(), "Should return 2 users with type 1 achievement");
        assertEquals(1, usersWithType2.size(), "Should return 1 user with type 2 achievement");
        assertTrue(usersWithType1.contains(TEST_USER_ID));
        assertTrue(usersWithType1.contains(TEST_USER_ID_2));
        assertTrue(usersWithType2.contains(TEST_USER_ID));
    }

    // ========================= UPDATE OPERATION TESTS =========================

    @Test
    @Order(14)
    @DisplayName("Test update achievement")
    void testUpdateAchievement_Success() throws SQLException {
        // Arrange
        Achievement createdAchievement = achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, TEST_DESCRIPTION);
        
        createdAchievement.setDescription("Updated description");

        // Act
        boolean updated = achievementDAO.updateAchievement(createdAchievement);

        // Assert
        assertTrue(updated, "Achievement update should be successful");
        
        Achievement foundAchievement = achievementDAO.findById(createdAchievement.getAchievementId());
        assertEquals("Updated description", foundAchievement.getDescription());
    }

    @Test
    @Order(15)
    @DisplayName("Test update achievement description")
    void testUpdateAchievementDescription_Success() throws SQLException {
        // Arrange
        Achievement createdAchievement = achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, TEST_DESCRIPTION);

        // Act
        boolean updated = achievementDAO.updateAchievementDescription(createdAchievement.getAchievementId(), "New description");

        // Assert
        assertTrue(updated, "Achievement description update should be successful");
        
        Achievement foundAchievement = achievementDAO.findById(createdAchievement.getAchievementId());
        assertEquals("New description", foundAchievement.getDescription());
    }

    // ========================= DELETE OPERATION TESTS =========================

    @Test
    @Order(16)
    @DisplayName("Test delete achievement")
    void testDeleteAchievement_Success() throws SQLException {
        // Arrange
        Achievement createdAchievement = achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, TEST_DESCRIPTION);

        // Act
        boolean deleted = achievementDAO.deleteAchievement(createdAchievement.getAchievementId());

        // Assert
        assertTrue(deleted, "Achievement deletion should be successful");
        
        Achievement foundAchievement = achievementDAO.findById(createdAchievement.getAchievementId());
        assertNull(foundAchievement, "Achievement should not be found after deletion");
    }

    @Test
    @Order(17)
    @DisplayName("Test delete achievements by user")
    void testDeleteAchievementsByUser_Success() throws SQLException {
        // Arrange - ensure user has no achievements first
        achievementDAO.deleteAchievementsByUser(TEST_USER_ID);
        
        // Create exactly 2 achievements for this test
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE_2, "Achievement 2");

        // Verify we have exactly 2 achievements before deletion
        int countBeforeDeletion = achievementDAO.getAchievementCountByUser(TEST_USER_ID);
        assertEquals(2, countBeforeDeletion, "Should have exactly 2 achievements before deletion");

        // Act
        int deletedCount = achievementDAO.deleteAchievementsByUser(TEST_USER_ID);

        // Assert
        assertEquals(2, deletedCount, "Should delete exactly 2 achievements");
        
        List<Achievement> remainingAchievements = achievementDAO.getAchievementsByUser(TEST_USER_ID);
        assertEquals(0, remainingAchievements.size(), "No achievements should remain for the user");
    }

    @Test
    @Order(18)
    @DisplayName("Test delete achievements by type")
    void testDeleteAchievementsByType_Success() throws SQLException {
        // Arrange
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID_2, TEST_ACHIEVEMENT_TYPE, "Achievement 2");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE_2, "Achievement 3");

        // Act
        int deletedCount = achievementDAO.deleteAchievementsByType(TEST_ACHIEVEMENT_TYPE);

        // Assert
        assertEquals(2, deletedCount, "Should delete 2 achievements of type 1");
        
        List<Achievement> remainingType1 = achievementDAO.getAchievementsByType(TEST_ACHIEVEMENT_TYPE);
        assertEquals(0, remainingType1.size(), "No achievements of type 1 should remain");
        
        List<Achievement> remainingType2 = achievementDAO.getAchievementsByType(TEST_ACHIEVEMENT_TYPE_2);
        assertEquals(1, remainingType2.size(), "Type 2 achievements should remain");
    }

    @Test
    @Order(19)
    @DisplayName("Test delete user achievement")
    void testDeleteUserAchievement_Success() throws SQLException {
        // Arrange
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE_2, "Achievement 2");

        // Act
        boolean deleted = achievementDAO.deleteUserAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE);

        // Assert
        assertTrue(deleted, "User achievement deletion should be successful");
        
        assertFalse(achievementDAO.hasUserEarnedAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE), "User should no longer have the achievement");
        assertTrue(achievementDAO.hasUserEarnedAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE_2), "Other achievements should remain");
    }

    // ========================= VALIDATION AND UTILITY TESTS =========================

    @Test
    @Order(20)
    @DisplayName("Test achievement exists check")
    void testAchievementExists_Success() throws SQLException {
        // Test non-existent achievement
        assertFalse(achievementDAO.achievementExists(99999), "Should return false for non-existent achievement");

        // Arrange - create achievement
        Achievement createdAchievement = achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, TEST_DESCRIPTION);

        // Test existing achievement
        assertTrue(achievementDAO.achievementExists(createdAchievement.getAchievementId()), "Should return true for existing achievement");
    }

    @Test
    @Order(21)
    @DisplayName("Test has user earned achievement check")
    void testHasUserEarnedAchievement_Success() throws SQLException {
        // Test before earning achievement
        assertFalse(achievementDAO.hasUserEarnedAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE), "Should return false before earning achievement");

        // Arrange - create achievement
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, TEST_DESCRIPTION);

        // Test after earning achievement
        assertTrue(achievementDAO.hasUserEarnedAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE), "Should return true after earning achievement");
        assertFalse(achievementDAO.hasUserEarnedAchievement(TEST_USER_ID_2, TEST_ACHIEVEMENT_TYPE), "Should return false for different user");
    }

    @Test
    @Order(22)
    @DisplayName("Test get achievement count by user")
    void testGetAchievementCountByUser_Success() throws SQLException {
        // Arrange
        int initialCount = achievementDAO.getAchievementCountByUser(TEST_USER_ID);
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE_2, "Achievement 2");

        // Act
        int finalCount = achievementDAO.getAchievementCountByUser(TEST_USER_ID);

        // Assert
        assertEquals(initialCount + 2, finalCount, "Achievement count should increase by 2");
    }

    @Test
    @Order(23)
    @DisplayName("Test get achievement count by type")
    void testGetAchievementCountByType_Success() throws SQLException {
        // Arrange
        int initialCount = achievementDAO.getAchievementCountByType(TEST_ACHIEVEMENT_TYPE);
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID_2, TEST_ACHIEVEMENT_TYPE, "Achievement 2");

        // Act
        int finalCount = achievementDAO.getAchievementCountByType(TEST_ACHIEVEMENT_TYPE);

        // Assert
        assertEquals(initialCount + 2, finalCount, "Type achievement count should increase by 2");
    }

    @Test
    @Order(24)
    @DisplayName("Test get total achievement count")
    void testGetTotalAchievementCount_Success() throws SQLException {
        // Arrange
        int initialTotal = achievementDAO.getTotalAchievementCount();
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID_2, TEST_ACHIEVEMENT_TYPE_2, "Achievement 2");

        // Act
        int finalTotal = achievementDAO.getTotalAchievementCount();

        // Assert
        assertEquals(initialTotal + 2, finalTotal, "Total achievement count should increase by 2");
    }

    @Test
    @Order(25)
    @DisplayName("Test get recent achievement count")
    void testGetRecentAchievementCount_Success() throws SQLException {
        // Arrange
        int initialCount = achievementDAO.getRecentAchievementCount(7);
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Recent Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID_2, TEST_ACHIEVEMENT_TYPE_2, "Recent Achievement 2");

        // Act
        int finalCount = achievementDAO.getRecentAchievementCount(7);

        // Assert
        assertEquals(initialCount + 2, finalCount, "Recent achievement count should increase by 2");
    }

    @Test
    @Order(26)
    @DisplayName("Test get user achievement stats")
    void testGetUserAchievementStats_Success() throws SQLException {
        // Arrange
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE_2, "Achievement 2");

        // Act
        int[] stats = achievementDAO.getUserAchievementStats(TEST_USER_ID);

        // Assert
        assertEquals(2, stats.length, "Stats array should have 2 elements");
        assertEquals(2, stats[0], "Total achievements should be 2");
        assertEquals(2, stats[1], "Recent achievements should be 2");
    }

    @Test
    @Order(27)
    @DisplayName("Test get available achievement types")
    void testGetAvailableAchievementTypes_Success() throws SQLException {
        // Arrange
        achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "Achievement 1");
        achievementDAO.createSimpleAchievement(TEST_USER_ID_2, TEST_ACHIEVEMENT_TYPE_2, "Achievement 2");
        achievementDAO.createSimpleAchievement(TEST_USER_ID, Achievement.PRACTICE_MAKES_PERFECT, "Achievement 3");

        // Act
        List<String> types = achievementDAO.getAvailableAchievementTypes();

        // Assert
        assertTrue(types.size() >= 3, "Should return at least 3 achievement types");
        assertTrue(types.contains(TEST_ACHIEVEMENT_TYPE), "Should contain test achievement type 1");
        assertTrue(types.contains(TEST_ACHIEVEMENT_TYPE_2), "Should contain test achievement type 2");
        assertTrue(types.contains(Achievement.PRACTICE_MAKES_PERFECT), "Should contain practice achievement type");
    }

    @Test
    @Order(28)
    @DisplayName("Test get latest achievement")
    void testGetLatestAchievement_Success() throws SQLException {
        // Ensure clean state for this test
        cleanUpTestAchievements();
        
        // Test user with no achievements
        assertNull(achievementDAO.getLatestAchievement(TEST_USER_ID), "Should return null for user with no achievements");

        // Arrange - create first achievement
        Achievement firstAchievement = achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE, "First Achievement");
        assertNotNull(firstAchievement, "First achievement should be created");
        
        // Verify only one achievement exists
        Achievement latestAfterFirst = achievementDAO.getLatestAchievement(TEST_USER_ID);
        assertNotNull(latestAfterFirst, "Should have one achievement");
        assertEquals(TEST_ACHIEVEMENT_TYPE, latestAfterFirst.getAchievementType(), "Should be the first achievement type");
        
        // Add sufficient delay to ensure different timestamps
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        // Create second achievement
        Achievement secondAchievement = achievementDAO.createSimpleAchievement(TEST_USER_ID, TEST_ACHIEVEMENT_TYPE_2, "Latest Achievement");
        assertNotNull(secondAchievement, "Second achievement should be created");

        // Act - get latest achievement
        Achievement latestAchievement = achievementDAO.getLatestAchievement(TEST_USER_ID);

        // Assert
        assertNotNull(latestAchievement, "Should return the latest achievement");
        assertEquals(TEST_ACHIEVEMENT_TYPE_2, latestAchievement.getAchievementType(), "Should return the most recent achievement type");
        assertEquals("Latest Achievement", latestAchievement.getDescription(), "Should return the most recent achievement description");
        
        // Verify the latest achievement is indeed the second one created
        assertTrue(latestAchievement.getDateEarned().after(firstAchievement.getDateEarned()), 
            "Latest achievement should have a later date than the first achievement");
            
        // Verify we have exactly 2 achievements for this user
        assertEquals(2, achievementDAO.getAchievementCountByUser(TEST_USER_ID), "Should have exactly 2 achievements");
    }

    @Test
    @Order(29)
    @DisplayName("Test edge cases and null handling")
    void testEdgeCases_Success() throws SQLException {
        // Test empty results
        List<Achievement> emptyUserAchievements = achievementDAO.getAchievementsByUser(99999);
        assertEquals(0, emptyUserAchievements.size(), "Should return empty list for non-existent user");

        List<Achievement> emptyTypeAchievements = achievementDAO.getAchievementsByType("non_existent_type");
        assertEquals(0, emptyTypeAchievements.size(), "Should return empty list for non-existent type");

        // Test counts for non-existent data
        assertEquals(0, achievementDAO.getAchievementCountByUser(99999), "Should return 0 for non-existent user");
        assertEquals(0, achievementDAO.getAchievementCountByType("non_existent_type"), "Should return 0 for non-existent type");
    }

    @Test
    @Order(30)
    @DisplayName("Test comprehensive achievement workflow")
    void testComprehensiveWorkflow_Success() throws SQLException {
        // Test complete workflow: create, read, update, validate, delete
        
        // 1. Create multiple achievements
        Achievement achievement1 = achievementDAO.awardAchievement(TEST_USER_ID, Achievement.AMATEUR_AUTHOR);
        Achievement achievement2 = achievementDAO.awardAchievement(TEST_USER_ID, Achievement.QUIZ_MACHINE);
        Achievement achievement3 = achievementDAO.awardAchievement(TEST_USER_ID_2, Achievement.PRACTICE_MAKES_PERFECT);

        assertNotNull(achievement1, "First achievement should be created");
        assertNotNull(achievement2, "Second achievement should be created");
        assertNotNull(achievement3, "Third achievement should be created");

        // 2. Verify reads work correctly
        assertEquals(2, achievementDAO.getAchievementCountByUser(TEST_USER_ID), "User 1 should have 2 achievements");
        assertEquals(1, achievementDAO.getAchievementCountByUser(TEST_USER_ID_2), "User 2 should have 1 achievement");
        assertTrue(achievementDAO.hasUserEarnedAchievement(TEST_USER_ID, Achievement.AMATEUR_AUTHOR), "User should have amateur author achievement");

        // 3. Update achievement
        boolean updated = achievementDAO.updateAchievementDescription(achievement1.getAchievementId(), "Updated workflow description");
        assertTrue(updated, "Achievement should be updated");

        // 4. Verify statistics
        int[] user1Stats = achievementDAO.getUserAchievementStats(TEST_USER_ID);
        assertEquals(2, user1Stats[0], "User 1 should have 2 total achievements");

        // 5. Delete specific achievement
        boolean deleted = achievementDAO.deleteUserAchievement(TEST_USER_ID, Achievement.QUIZ_MACHINE);
        assertTrue(deleted, "Achievement should be deleted");
        assertEquals(1, achievementDAO.getAchievementCountByUser(TEST_USER_ID), "User 1 should have 1 achievement after deletion");

        // 6. Verify integrity
        assertTrue(achievementDAO.hasUserEarnedAchievement(TEST_USER_ID, Achievement.AMATEUR_AUTHOR), "User should still have amateur author achievement");
        assertFalse(achievementDAO.hasUserEarnedAchievement(TEST_USER_ID, Achievement.QUIZ_MACHINE), "User should no longer have quiz machine achievement");
    }
} 