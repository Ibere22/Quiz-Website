package dao;

import model.Quiz;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for QuizDAO class
 * Tests all CRUD operations, search functionality, validation methods, and statistics
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuizDAOTest {

    private static Connection connection;
    private static QuizDAO quizDAO;
    private static final int TEST_CREATOR_ID = 1; // Assuming user ID 1 exists
    private static final int TEST_CREATOR_ID_2 = 2; // Assuming user ID 2 exists  
    private static final String TEST_QUIZ_TITLE = "Test Quiz Title";
    private static final String TEST_QUIZ_DESCRIPTION = "Test Quiz Description";

    @BeforeAll
    static void setUpClass() throws SQLException {
        connection = DatabaseConnection.getConnection();
        quizDAO = new QuizDAO(connection);
        
        // Clean up any existing test data
        cleanUpTestData();
        
        // Create test users if they don't exist
        createTestUsers();
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
            // Delete ALL quizzes from test creators to ensure clean state
            stmt.executeUpdate("DELETE FROM quizzes WHERE creator_id IN (1, 2)");
        }
    }

    private static void createTestUsers() throws SQLException {
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
    @DisplayName("Test create simple quiz")
    void testCreateSimpleQuiz_Success() throws SQLException {
        // Act
        Quiz createdQuiz = quizDAO.createSimpleQuiz(TEST_QUIZ_TITLE, TEST_QUIZ_DESCRIPTION, TEST_CREATOR_ID);

        // Assert
        assertNotNull(createdQuiz, "Quiz should be created successfully");
        assertTrue(createdQuiz.getQuizId() > 0, "Quiz ID should be generated");
        assertEquals(TEST_QUIZ_TITLE, createdQuiz.getTitle());
        assertEquals(TEST_QUIZ_DESCRIPTION, createdQuiz.getDescription());
        assertEquals(TEST_CREATOR_ID, createdQuiz.getCreatorId());
        assertNotNull(createdQuiz.getCreatedDate());
        // Test default values
        assertFalse(createdQuiz.isRandomOrder());
        assertTrue(createdQuiz.isOnePage());
        assertFalse(createdQuiz.isImmediateCorrection());
        assertFalse(createdQuiz.isPracticeMode());
    }

    @Test
    @Order(2)
    @DisplayName("Test create custom quiz with settings")
    void testCreateCustomQuiz_Success() throws SQLException {
        // Act
        Quiz createdQuiz = quizDAO.createCustomQuiz(
            "Custom Quiz", "Custom Description", TEST_CREATOR_ID,
            true, false, true, true
        );

        // Assert
        assertNotNull(createdQuiz, "Quiz should be created successfully");
        assertEquals("Custom Quiz", createdQuiz.getTitle());
        assertEquals("Custom Description", createdQuiz.getDescription());
        assertTrue(createdQuiz.isRandomOrder());
        assertFalse(createdQuiz.isOnePage());
        assertTrue(createdQuiz.isImmediateCorrection());
        assertTrue(createdQuiz.isPracticeMode());
    }

    @Test
    @Order(3)
    @DisplayName("Test create quiz with Quiz object")
    void testCreateQuiz_WithObject_Success() throws SQLException {
        // Arrange
        Quiz quiz = new Quiz("Object Quiz", "Object Description", TEST_CREATOR_ID);
        quiz.setRandomOrder(true);
        quiz.setPracticeMode(true);

        // Act
        Quiz createdQuiz = quizDAO.createQuiz(quiz);

        // Assert
        assertNotNull(createdQuiz, "Quiz should be created successfully");
        assertTrue(createdQuiz.getQuizId() > 0, "Quiz ID should be generated");
        assertEquals("Object Quiz", createdQuiz.getTitle());
        assertTrue(createdQuiz.isRandomOrder());
        assertTrue(createdQuiz.isPracticeMode());
    }

    // ========================= READ OPERATION TESTS =========================

    @Test
    @Order(4)
    @DisplayName("Test find quiz by ID")
    void testFindById_ExistingQuiz_Success() throws SQLException {
        // Arrange
        Quiz createdQuiz = quizDAO.createSimpleQuiz(TEST_QUIZ_TITLE, TEST_QUIZ_DESCRIPTION, TEST_CREATOR_ID);

        // Act
        Quiz foundQuiz = quizDAO.findById(createdQuiz.getQuizId());

        // Assert
        assertNotNull(foundQuiz, "Quiz should be found");
        assertEquals(createdQuiz.getQuizId(), foundQuiz.getQuizId());
        assertEquals(TEST_QUIZ_TITLE, foundQuiz.getTitle());
        assertEquals(TEST_QUIZ_DESCRIPTION, foundQuiz.getDescription());
        assertEquals(TEST_CREATOR_ID, foundQuiz.getCreatorId());
    }

    @Test
    @Order(5)
    @DisplayName("Test find quiz by non-existent ID")
    void testFindById_NonExistentQuiz_ReturnsNull() throws SQLException {
        // Act
        Quiz foundQuiz = quizDAO.findById(99999);

        // Assert
        assertNull(foundQuiz, "Should return null for non-existent quiz");
    }

    @Test
    @Order(6)
    @DisplayName("Test get quizzes by creator")
    void testGetQuizzesByCreator_Success() throws SQLException {
        // Arrange
        quizDAO.createSimpleQuiz("Quiz 1", "Description 1", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Quiz 2", "Description 2", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Quiz 3", "Description 3", TEST_CREATOR_ID_2);

        // Act
        List<Quiz> creator1Quizzes = quizDAO.getQuizzesByCreator(TEST_CREATOR_ID);
        List<Quiz> creator2Quizzes = quizDAO.getQuizzesByCreator(TEST_CREATOR_ID_2);

        // Assert
        assertEquals(2, creator1Quizzes.size(), "Should return 2 quizzes for creator 1");
        assertEquals(1, creator2Quizzes.size(), "Should return 1 quiz for creator 2");
        
        // Verify all quizzes belong to the correct creator
        assertTrue(creator1Quizzes.stream().allMatch(q -> q.getCreatorId() == TEST_CREATOR_ID));
        assertTrue(creator2Quizzes.stream().allMatch(q -> q.getCreatorId() == TEST_CREATOR_ID_2));
    }

    @Test
    @Order(7)
    @DisplayName("Test get all quizzes with pagination")
    void testGetAllQuizzes_WithPagination_Success() throws SQLException {
        // Arrange
        quizDAO.createSimpleQuiz("Quiz 1", "Description 1", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Quiz 2", "Description 2", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Quiz 3", "Description 3", TEST_CREATOR_ID);

        // Act
        List<Quiz> firstPage = quizDAO.getAllQuizzes(0, 2);
        List<Quiz> secondPage = quizDAO.getAllQuizzes(2, 2);

        // Assert
        assertEquals(2, firstPage.size(), "First page should have 2 quizzes");
        assertTrue(secondPage.size() >= 1, "Second page should have at least 1 quiz");
    }

    @Test
    @Order(8)
    @DisplayName("Test search quizzes by title")
    void testSearchQuizzesByTitle_Success() throws SQLException {
        // Arrange
        quizDAO.createSimpleQuiz("Math Quiz", "Mathematics questions", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Science Quiz", "Science questions", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("History Test", "History questions", TEST_CREATOR_ID);

        // Act
        List<Quiz> searchResults = quizDAO.searchQuizzesByTitle("Quiz");

        // Assert
        assertEquals(2, searchResults.size(), "Should find 2 quizzes containing 'Quiz' in title");
        assertTrue(searchResults.stream().allMatch(q -> q.getTitle().contains("Quiz")));
    }

    @Test
    @Order(9)
    @DisplayName("Test search quizzes by title or description")
    void testSearchQuizzes_Success() throws SQLException {
        // Use unique quiz titles for this test run
        String mathTitle = "Math Test " + System.currentTimeMillis();
        String scienceTitle = "Science Quiz " + System.currentTimeMillis();
        String historyTitle = "History Test " + System.currentTimeMillis();
        // Arrange
        quizDAO.createSimpleQuiz(mathTitle, "Mathematics quiz questions", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz(scienceTitle, "Science questions", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz(historyTitle, "History questions", TEST_CREATOR_ID);

        // Act
        List<Quiz> searchResults = quizDAO.searchQuizzes("Test");

        // Assert
        assertTrue(searchResults.size() >= 2, "Should find at least 2 quizzes containing 'quiz' in title or description");
    }

    @Test
    @Order(10)
    @DisplayName("Test get practice quizzes")
    void testGetPracticeQuizzes_Success() throws SQLException {
        // Arrange
        quizDAO.createCustomQuiz("Regular Quiz", "Description", TEST_CREATOR_ID, false, true, false, false);
        quizDAO.createCustomQuiz("Practice Quiz 1", "Description", TEST_CREATOR_ID, false, true, false, true);
        quizDAO.createCustomQuiz("Practice Quiz 2", "Description", TEST_CREATOR_ID, false, true, false, true);

        // Act
        List<Quiz> practiceQuizzes = quizDAO.getPracticeQuizzes();

        // Assert
        assertEquals(2, practiceQuizzes.size(), "Should return 2 practice quizzes");
        assertTrue(practiceQuizzes.stream().allMatch(Quiz::isPracticeMode));
    }

    @Test
    @Order(11)
    @DisplayName("Test get recent quizzes")
    void testGetRecentQuizzes_Success() throws SQLException {
        // Arrange
        quizDAO.createSimpleQuiz("Recent Quiz 1", "Description 1", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Recent Quiz 2", "Description 2", TEST_CREATOR_ID);

        // Act
        List<Quiz> recentQuizzes = quizDAO.getRecentQuizzes(7, 10); // Last 7 days, max 10 results

        // Assert
        assertTrue(recentQuizzes.size() >= 2, "Should return at least 2 recent quizzes");
    }

    @Test
    @Order(12)
    @DisplayName("Test get quizzes by creator with pagination")
    void testGetQuizzesByCreator_WithPagination_Success() throws SQLException {
        // Arrange
        quizDAO.createSimpleQuiz("Quiz 1", "Description 1", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Quiz 2", "Description 2", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Quiz 3", "Description 3", TEST_CREATOR_ID);

        // Act
        List<Quiz> firstPage = quizDAO.getQuizzesByCreator(TEST_CREATOR_ID, 0, 2);
        List<Quiz> secondPage = quizDAO.getQuizzesByCreator(TEST_CREATOR_ID, 2, 2);

        // Assert
        assertEquals(2, firstPage.size(), "First page should have 2 quizzes");
        assertEquals(1, secondPage.size(), "Second page should have 1 quiz");
    }

    // ========================= UPDATE OPERATION TESTS =========================

    @Test
    @Order(13)
    @DisplayName("Test update quiz")
    void testUpdateQuiz_Success() throws SQLException {
        // Arrange
        Quiz createdQuiz = quizDAO.createSimpleQuiz(TEST_QUIZ_TITLE, TEST_QUIZ_DESCRIPTION, TEST_CREATOR_ID);
        
        createdQuiz.setTitle("Updated Title");
        createdQuiz.setDescription("Updated Description");
        createdQuiz.setRandomOrder(true);
        createdQuiz.setPracticeMode(true);

        // Act
        boolean updated = quizDAO.updateQuiz(createdQuiz);

        // Assert
        assertTrue(updated, "Quiz update should be successful");
        
        Quiz foundQuiz = quizDAO.findById(createdQuiz.getQuizId());
        assertEquals("Updated Title", foundQuiz.getTitle());
        assertEquals("Updated Description", foundQuiz.getDescription());
        assertTrue(foundQuiz.isRandomOrder());
        assertTrue(foundQuiz.isPracticeMode());
    }

    @Test
    @Order(14)
    @DisplayName("Test update quiz title")
    void testUpdateQuizTitle_Success() throws SQLException {
        // Arrange
        Quiz createdQuiz = quizDAO.createSimpleQuiz(TEST_QUIZ_TITLE, TEST_QUIZ_DESCRIPTION, TEST_CREATOR_ID);

        // Act
        boolean updated = quizDAO.updateQuizTitle(createdQuiz.getQuizId(), "New Title");

        // Assert
        assertTrue(updated, "Quiz title update should be successful");
        
        Quiz foundQuiz = quizDAO.findById(createdQuiz.getQuizId());
        assertEquals("New Title", foundQuiz.getTitle());
    }

    @Test
    @Order(15)
    @DisplayName("Test update quiz description")
    void testUpdateQuizDescription_Success() throws SQLException {
        // Arrange
        Quiz createdQuiz = quizDAO.createSimpleQuiz(TEST_QUIZ_TITLE, TEST_QUIZ_DESCRIPTION, TEST_CREATOR_ID);

        // Act
        boolean updated = quizDAO.updateQuizDescription(createdQuiz.getQuizId(), "New Description");

        // Assert
        assertTrue(updated, "Quiz description update should be successful");
        
        Quiz foundQuiz = quizDAO.findById(createdQuiz.getQuizId());
        assertEquals("New Description", foundQuiz.getDescription());
    }

    @Test
    @Order(16)
    @DisplayName("Test update quiz settings")
    void testUpdateQuizSettings_Success() throws SQLException {
        // Arrange
        Quiz createdQuiz = quizDAO.createSimpleQuiz(TEST_QUIZ_TITLE, TEST_QUIZ_DESCRIPTION, TEST_CREATOR_ID);

        // Act
        boolean updated = quizDAO.updateQuizSettings(createdQuiz.getQuizId(), true, false, true, true);

        // Assert
        assertTrue(updated, "Quiz settings update should be successful");
        
        Quiz foundQuiz = quizDAO.findById(createdQuiz.getQuizId());
        assertTrue(foundQuiz.isRandomOrder());
        assertFalse(foundQuiz.isOnePage());
        assertTrue(foundQuiz.isImmediateCorrection());
        assertTrue(foundQuiz.isPracticeMode());
    }

    @Test
    @Order(17)
    @DisplayName("Test toggle practice mode")
    void testTogglePracticeMode_Success() throws SQLException {
        // Arrange
        Quiz createdQuiz = quizDAO.createSimpleQuiz(TEST_QUIZ_TITLE, TEST_QUIZ_DESCRIPTION, TEST_CREATOR_ID);
        boolean originalPracticeMode = createdQuiz.isPracticeMode();

        // Act
        boolean updated = quizDAO.togglePracticeMode(createdQuiz.getQuizId());

        // Assert
        assertTrue(updated, "Practice mode toggle should be successful");
        
        Quiz foundQuiz = quizDAO.findById(createdQuiz.getQuizId());
        assertEquals(!originalPracticeMode, foundQuiz.isPracticeMode(), "Practice mode should be toggled");
    }

    // ========================= DELETE OPERATION TESTS =========================

    @Test
    @Order(18)
    @DisplayName("Test delete quiz")
    void testDeleteQuiz_Success() throws SQLException {
        // Arrange
        Quiz createdQuiz = quizDAO.createSimpleQuiz(TEST_QUIZ_TITLE, TEST_QUIZ_DESCRIPTION, TEST_CREATOR_ID);

        // Act
        boolean deleted = quizDAO.deleteQuiz(createdQuiz.getQuizId());

        // Assert
        assertTrue(deleted, "Quiz deletion should be successful");
        
        Quiz foundQuiz = quizDAO.findById(createdQuiz.getQuizId());
        assertNull(foundQuiz, "Quiz should not be found after deletion");
    }

    @Test
    @Order(19)
    @DisplayName("Test delete quizzes by creator")
    void testDeleteQuizzesByCreator_Success() throws SQLException {
        // Arrange - ensure creator has no quizzes first
        quizDAO.deleteQuizzesByCreator(TEST_CREATOR_ID);
        
        // Create exactly 2 quizzes for this test
        quizDAO.createSimpleQuiz("Quiz 1", "Description 1", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Quiz 2", "Description 2", TEST_CREATOR_ID);

        // Verify we have exactly 2 quizzes before deletion
        int countBeforeDeletion = quizDAO.getQuizCountByCreator(TEST_CREATOR_ID);
        assertEquals(2, countBeforeDeletion, "Should have exactly 2 quizzes before deletion");

        // Act
        int deletedCount = quizDAO.deleteQuizzesByCreator(TEST_CREATOR_ID);

        // Assert
        assertEquals(2, deletedCount, "Should delete exactly 2 quizzes");
        
        List<Quiz> remainingQuizzes = quizDAO.getQuizzesByCreator(TEST_CREATOR_ID);
        assertEquals(0, remainingQuizzes.size(), "No quizzes should remain for the creator");
    }

    // ========================= VALIDATION AND UTILITY TESTS =========================

    @Test
    @Order(20)
    @DisplayName("Test quiz exists check")
    void testQuizExists_Success() throws SQLException {
        // Test non-existent quiz
        assertFalse(quizDAO.quizExists(99999), "Should return false for non-existent quiz");

        // Arrange - create quiz
        Quiz createdQuiz = quizDAO.createSimpleQuiz(TEST_QUIZ_TITLE, TEST_QUIZ_DESCRIPTION, TEST_CREATOR_ID);

        // Test existing quiz
        assertTrue(quizDAO.quizExists(createdQuiz.getQuizId()), "Should return true for existing quiz");
    }

    @Test
    @Order(21)
    @DisplayName("Test is quiz creator check")
    void testIsQuizCreator_Success() throws SQLException {
        // Arrange
        Quiz createdQuiz = quizDAO.createSimpleQuiz(TEST_QUIZ_TITLE, TEST_QUIZ_DESCRIPTION, TEST_CREATOR_ID);

        // Act & Assert
        assertTrue(quizDAO.isQuizCreator(createdQuiz.getQuizId(), TEST_CREATOR_ID), 
            "Should return true for actual creator");
        assertFalse(quizDAO.isQuizCreator(createdQuiz.getQuizId(), TEST_CREATOR_ID_2), 
            "Should return false for different user");
    }

    @Test
    @Order(22)
    @DisplayName("Test get quiz count by creator")
    void testGetQuizCountByCreator_Success() throws SQLException {
        // Arrange
        int initialCount = quizDAO.getQuizCountByCreator(TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Quiz 1", "Description 1", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Quiz 2", "Description 2", TEST_CREATOR_ID);

        // Act
        int finalCount = quizDAO.getQuizCountByCreator(TEST_CREATOR_ID);

        // Assert
        assertEquals(initialCount + 2, finalCount, "Quiz count should increase by 2");
    }

    @Test
    @Order(23)
    @DisplayName("Test get total quiz count")
    void testGetTotalQuizCount_Success() throws SQLException {
        // Arrange
        int initialCount = quizDAO.getTotalQuizCount();
        quizDAO.createSimpleQuiz("Quiz 1", "Description 1", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Quiz 2", "Description 2", TEST_CREATOR_ID_2);

        // Act
        int finalCount = quizDAO.getTotalQuizCount();

        // Assert
        assertEquals(initialCount + 2, finalCount, "Total quiz count should increase by 2");
    }

    @Test
    @Order(24)
    @DisplayName("Test get practice quiz count")
    void testGetPracticeQuizCount_Success() throws SQLException {
        // Arrange
        int initialCount = quizDAO.getPracticeQuizCount();
        quizDAO.createCustomQuiz("Practice Quiz", "Description", TEST_CREATOR_ID, false, true, false, true);

        // Act
        int finalCount = quizDAO.getPracticeQuizCount();

        // Assert
        assertEquals(initialCount + 1, finalCount, "Practice quiz count should increase by 1");
    }

    @Test
    @Order(25)
    @DisplayName("Test get recent quiz count")
    void testGetRecentQuizCount_Success() throws SQLException {
        // Arrange
        int initialCount = quizDAO.getRecentQuizCount(7); // Last 7 days
        quizDAO.createSimpleQuiz("Recent Quiz", "Description", TEST_CREATOR_ID);

        // Act
        int finalCount = quizDAO.getRecentQuizCount(7);

        // Assert
        assertEquals(initialCount + 1, finalCount, "Recent quiz count should increase by 1");
    }

    @Test
    @Order(26)
    @DisplayName("Test get quiz with question count")
    void testGetQuizWithQuestionCount_Success() throws SQLException {
        // Arrange
        Quiz createdQuiz = quizDAO.createSimpleQuiz(TEST_QUIZ_TITLE, TEST_QUIZ_DESCRIPTION, TEST_CREATOR_ID);

        // Act
        Quiz quizWithCount = quizDAO.getQuizWithQuestionCount(createdQuiz.getQuizId());

        // Assert
        assertNotNull(quizWithCount, "Quiz should be found");
        assertEquals(createdQuiz.getQuizId(), quizWithCount.getQuizId());
        assertEquals(TEST_QUIZ_TITLE, quizWithCount.getTitle());
    }

    @Test
    @Order(27)
    @DisplayName("Test get all quizzes without pagination")
    void testGetAllQuizzes_WithoutPagination_Success() throws SQLException {
        // Arrange
        quizDAO.createSimpleQuiz("Quiz 1", "Description 1", TEST_CREATOR_ID);
        quizDAO.createSimpleQuiz("Quiz 2", "Description 2", TEST_CREATOR_ID_2);

        // Act
        List<Quiz> allQuizzes = quizDAO.getAllQuizzes();

        // Assert
        assertNotNull(allQuizzes, "All quizzes list should not be null");
        assertTrue(allQuizzes.size() >= 2, "Should return at least 2 quizzes");
    }

    @Test
    @Order(28)
    @DisplayName("Test update non-existent quiz")
    void testUpdateQuiz_NonExistent_ReturnsFalse() throws SQLException {
        // Arrange
        Quiz nonExistentQuiz = new Quiz("Non-existent", "Description", TEST_CREATOR_ID);
        nonExistentQuiz.setQuizId(99999);

        // Act
        boolean updated = quizDAO.updateQuiz(nonExistentQuiz);

        // Assert
        assertFalse(updated, "Update should return false for non-existent quiz");
    }

    @Test
    @Order(29)
    @DisplayName("Test delete non-existent quiz")
    void testDeleteQuiz_NonExistent_ReturnsFalse() throws SQLException {
        // Act
        boolean deleted = quizDAO.deleteQuiz(99999);

        // Assert
        assertFalse(deleted, "Delete should return false for non-existent quiz");
    }

    @Test
    @Order(30)
    @DisplayName("Test quiz with null description")
    void testCreateQuiz_WithNullDescription_Success() throws SQLException {
        // Arrange
        Quiz quiz = new Quiz("Quiz with null description", null, TEST_CREATOR_ID);

        // Act
        Quiz createdQuiz = quizDAO.createQuiz(quiz);

        // Assert
        assertNotNull(createdQuiz, "Quiz should be created successfully");
        assertEquals("Quiz with null description", createdQuiz.getTitle());
        assertNull(createdQuiz.getDescription(), "Description should be null");
    }
} 