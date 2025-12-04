package dao;

import model.QuizAttempt;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for QuizAttemptDAO class
 * Tests all CRUD operations, search functionality, validation methods, and statistics
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuizAttemptDAOTest {

    private static Connection connection;
    private static QuizAttemptDAO quizAttemptDAO;
    private static final int TEST_USER_ID = 1;
    private static final int TEST_USER_ID_2 = 2;
    private static final int TEST_QUIZ_ID = 1;
    private static final int TEST_QUIZ_ID_2 = 2;
    private static final double TEST_SCORE = 85.5;
    private static final int TEST_TOTAL_QUESTIONS = 10;
    private static final long TEST_TIME_TAKEN = 300; // 5 minutes

    @BeforeAll
    static void setUpClass() throws SQLException {
        connection = DatabaseConnection.getConnection();
        quizAttemptDAO = new QuizAttemptDAO(connection);
        
        // Clean up any existing test data
        cleanUpTestData();
        
        // Create test users and quizzes if they don't exist
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
        cleanUpTestAttempts();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up after each test
        cleanUpTestAttempts();
    }

    private static void cleanUpTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Delete quiz attempts from test users/quizzes first (due to foreign keys)
            stmt.executeUpdate("DELETE FROM quiz_attempts WHERE user_id IN (1, 2) OR quiz_id IN (1, 2)");
            // Delete test quizzes
            stmt.executeUpdate("DELETE FROM quizzes WHERE id IN (1, 2) OR creator_id IN (1, 2)");
            // Note: We don't delete test users as they might be used by other tests
        }
    }

    private static void cleanUpTestAttempts() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Delete ALL quiz attempts to ensure clean state
            stmt.executeUpdate("DELETE FROM quiz_attempts");
        }
    }

    private static void createTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Insert test users if they don't exist
            stmt.executeUpdate("INSERT IGNORE INTO users (id, username, password_hash, email) VALUES " +
                "(1, 'testuser1', 'hash1', 'test1@example.com'), " +
                "(2, 'testuser2', 'hash2', 'test2@example.com')");
            
            // Insert test quizzes if they don't exist
            stmt.executeUpdate("INSERT IGNORE INTO quizzes (id, title, description, creator_id) VALUES " +
                "(1, 'Test Quiz 1', 'Description 1', 1), " +
                "(2, 'Test Quiz 2', 'Description 2', 2)");
        }
    }

    // ========================= CREATE OPERATION TESTS =========================

    @Test
    @Order(1)
    @DisplayName("Test create quiz attempt with object")
    void testCreateQuizAttempt_Success() throws SQLException {
        // Arrange
        QuizAttempt attempt = new QuizAttempt(TEST_USER_ID, TEST_QUIZ_ID, TEST_SCORE, TEST_TOTAL_QUESTIONS, TEST_TIME_TAKEN, false);

        // Act
        QuizAttempt createdAttempt = quizAttemptDAO.createQuizAttempt(attempt);

        // Assert
        assertNotNull(createdAttempt, "Quiz attempt should be created successfully");
        assertTrue(createdAttempt.getAttemptId() > 0, "Attempt ID should be generated");
        assertEquals(TEST_USER_ID, createdAttempt.getUserId());
        assertEquals(TEST_QUIZ_ID, createdAttempt.getQuizId());
        assertEquals(TEST_SCORE, createdAttempt.getScore(), 0.01);
        assertEquals(TEST_TOTAL_QUESTIONS, createdAttempt.getTotalQuestions());
        assertEquals(TEST_TIME_TAKEN, createdAttempt.getTimeTaken());
        assertFalse(createdAttempt.isPractice());
        assertNotNull(createdAttempt.getDateTaken());
    }

    @Test
    @Order(2)
    @DisplayName("Test create simple quiz attempt")
    void testCreateSimpleAttempt_Success() throws SQLException {
        // Act
        QuizAttempt createdAttempt = quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, TEST_SCORE, TEST_TOTAL_QUESTIONS, TEST_TIME_TAKEN);

        // Assert
        assertNotNull(createdAttempt, "Quiz attempt should be created successfully");
        assertTrue(createdAttempt.getAttemptId() > 0, "Attempt ID should be generated");
        assertEquals(TEST_USER_ID, createdAttempt.getUserId());
        assertEquals(TEST_QUIZ_ID, createdAttempt.getQuizId());
        assertEquals(TEST_SCORE, createdAttempt.getScore(), 0.01);
        assertFalse(createdAttempt.isPractice(), "Should not be practice attempt");
    }

    @Test
    @Order(3)
    @DisplayName("Test create practice quiz attempt")
    void testCreatePracticeAttempt_Success() throws SQLException {
        // Act
        QuizAttempt createdAttempt = quizAttemptDAO.createPracticeAttempt(TEST_USER_ID, TEST_QUIZ_ID, TEST_SCORE, TEST_TOTAL_QUESTIONS, TEST_TIME_TAKEN);

        // Assert
        assertNotNull(createdAttempt, "Practice quiz attempt should be created successfully");
        assertTrue(createdAttempt.getAttemptId() > 0, "Attempt ID should be generated");
        assertEquals(TEST_USER_ID, createdAttempt.getUserId());
        assertEquals(TEST_QUIZ_ID, createdAttempt.getQuizId());
        assertTrue(createdAttempt.isPractice(), "Should be practice attempt");
    }

    // ========================= READ OPERATION TESTS =========================

    @Test
    @Order(4)
    @DisplayName("Test find quiz attempt by ID")
    void testFindById_ExistingAttempt_Success() throws SQLException {
        // Arrange
        QuizAttempt createdAttempt = quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, TEST_SCORE, TEST_TOTAL_QUESTIONS, TEST_TIME_TAKEN);

        // Act
        QuizAttempt foundAttempt = quizAttemptDAO.findById(createdAttempt.getAttemptId());

        // Assert
        assertNotNull(foundAttempt, "Quiz attempt should be found");
        assertEquals(createdAttempt.getAttemptId(), foundAttempt.getAttemptId());
        assertEquals(TEST_USER_ID, foundAttempt.getUserId());
        assertEquals(TEST_QUIZ_ID, foundAttempt.getQuizId());
        assertEquals(TEST_SCORE, foundAttempt.getScore(), 0.01);
        assertEquals(TEST_TOTAL_QUESTIONS, foundAttempt.getTotalQuestions());
        assertEquals(TEST_TIME_TAKEN, foundAttempt.getTimeTaken());
    }

    @Test
    @Order(5)
    @DisplayName("Test find quiz attempt by non-existent ID")
    void testFindById_NonExistentAttempt_ReturnsNull() throws SQLException {
        // Act
        QuizAttempt foundAttempt = quizAttemptDAO.findById(99999);

        // Assert
        assertNull(foundAttempt, "Should return null for non-existent attempt");
    }

    @Test
    @Order(6)
    @DisplayName("Test get attempts by user")
    void testGetAttemptsByUser_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 90.0, 8, 200);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID_2, TEST_QUIZ_ID, 75.0, 10, 300);

        // Act
        List<QuizAttempt> user1Attempts = quizAttemptDAO.getAttemptsByUser(TEST_USER_ID);
        List<QuizAttempt> user2Attempts = quizAttemptDAO.getAttemptsByUser(TEST_USER_ID_2);

        // Assert
        assertEquals(2, user1Attempts.size(), "Should return 2 attempts for user 1");
        assertEquals(1, user2Attempts.size(), "Should return 1 attempt for user 2");
        
        // Verify all attempts belong to the correct user
        assertTrue(user1Attempts.stream().allMatch(a -> a.getUserId() == TEST_USER_ID));
        assertTrue(user2Attempts.stream().allMatch(a -> a.getUserId() == TEST_USER_ID_2));
    }

    @Test
    @Order(7)
    @DisplayName("Test get attempts by quiz")
    void testGetAttemptsByQuiz_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID_2, TEST_QUIZ_ID, 90.0, 10, 200);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 75.0, 8, 300);

        // Act
        List<QuizAttempt> quiz1Attempts = quizAttemptDAO.getAttemptsByQuiz(TEST_QUIZ_ID);
        List<QuizAttempt> quiz2Attempts = quizAttemptDAO.getAttemptsByQuiz(TEST_QUIZ_ID_2);

        // Assert
        assertEquals(2, quiz1Attempts.size(), "Should return 2 attempts for quiz 1");
        assertEquals(1, quiz2Attempts.size(), "Should return 1 attempt for quiz 2");
        
        // Verify all attempts belong to the correct quiz
        assertTrue(quiz1Attempts.stream().allMatch(a -> a.getQuizId() == TEST_QUIZ_ID));
        assertTrue(quiz2Attempts.stream().allMatch(a -> a.getQuizId() == TEST_QUIZ_ID_2));
    }

    @Test
    @Order(8)
    @DisplayName("Test get attempts by user and quiz")
    void testGetAttemptsByUserAndQuiz_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 85.0, 10, 240);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 90.0, 8, 200);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID_2, TEST_QUIZ_ID, 75.0, 10, 300);

        // Act
        List<QuizAttempt> attempts = quizAttemptDAO.getAttemptsByUserAndQuiz(TEST_USER_ID, TEST_QUIZ_ID);

        // Assert
        assertEquals(2, attempts.size(), "Should return 2 attempts for user 1 on quiz 1");
        assertTrue(attempts.stream().allMatch(a -> a.getUserId() == TEST_USER_ID && a.getQuizId() == TEST_QUIZ_ID));
    }

    @Test
    @Order(9)
    @DisplayName("Test get all attempts without pagination")
    void testGetAllAttempts_WithoutPagination_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 85.0, 8, 240);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID_2, TEST_QUIZ_ID, 90.0, 10, 200);

        // Act
        List<QuizAttempt> allAttempts = quizAttemptDAO.getAllAttempts();

        // Assert
        assertTrue(allAttempts.size() >= 3, "Should return at least 3 attempts");
        
        // Verify attempts are sorted by date_taken DESC (most recent first)
        for (int i = 0; i < allAttempts.size() - 1; i++) {
            assertTrue(allAttempts.get(i).getDateTaken().compareTo(allAttempts.get(i + 1).getDateTaken()) >= 0,
                "Attempts should be sorted by date taken in descending order");
        }
    }

    @Test
    @Order(10)
    @DisplayName("Test get all attempts with pagination")
    void testGetAllAttempts_WithPagination_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 85.0, 8, 240);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID_2, TEST_QUIZ_ID, 90.0, 10, 200);

        // Act
        List<QuizAttempt> firstPage = quizAttemptDAO.getAllAttempts(0, 2);
        List<QuizAttempt> secondPage = quizAttemptDAO.getAllAttempts(2, 2);

        // Assert
        assertEquals(2, firstPage.size(), "First page should have 2 attempts");
        assertTrue(secondPage.size() >= 1, "Second page should have at least 1 attempt");
    }

    @Test
    @Order(11)
    @DisplayName("Test get practice attempts only")
    void testGetPracticeAttempts_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createPracticeAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 85.0, 8, 240);
        quizAttemptDAO.createPracticeAttempt(TEST_USER_ID, TEST_QUIZ_ID, 90.0, 10, 200);

        // Act
        List<QuizAttempt> practiceAttempts = quizAttemptDAO.getPracticeAttempts();

        // Assert
        assertEquals(2, practiceAttempts.size(), "Should return 2 practice attempts");
        assertTrue(practiceAttempts.stream().allMatch(QuizAttempt::isPractice));
    }

    @Test
    @Order(12)
    @DisplayName("Test get graded attempts only")
    void testGetGradedAttempts_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID_2, TEST_QUIZ_ID_2, 85.0, 8, 240);
        quizAttemptDAO.createPracticeAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 90.0, 10, 200);

        // Act
        List<QuizAttempt> gradedAttempts = quizAttemptDAO.getGradedAttempts();

        // Assert
        assertEquals(2, gradedAttempts.size(), "Should return 2 graded attempts");
        assertTrue(gradedAttempts.stream().noneMatch(QuizAttempt::isPractice));
    }

    @Test
    @Order(13)
    @DisplayName("Test get top scores for quiz")
    void testGetTopScoresForQuiz_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 350);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID_2, TEST_QUIZ_ID, 95.0, 10, 200);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 90.0, 10, 250);

        // Act
        List<QuizAttempt> topScores = quizAttemptDAO.getTopScoresForQuiz(TEST_QUIZ_ID, 2, false);

        // Assert
        assertEquals(2, topScores.size(), "Should return 2 top scores");
        assertTrue(topScores.get(0).getScore() >= topScores.get(1).getScore(), "Should be sorted by score descending");
        // Top score should be 95.0
        assertEquals(95.0, topScores.get(0).getScore(), 0.01);
    }

    @Test
    @Order(14)
    @DisplayName("Test get recent attempts")
    void testGetRecentAttempts_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID_2, TEST_QUIZ_ID_2, 85.0, 8, 240);

        // Act
        List<QuizAttempt> recentAttempts = quizAttemptDAO.getRecentAttempts(7, 10); // Last 7 days, max 10 results

        // Assert
        assertTrue(recentAttempts.size() >= 2, "Should return at least 2 recent attempts");
    }

    @Test
    @Order(15)
    @DisplayName("Test get attempts by user with pagination")
    void testGetAttemptsByUser_WithPagination_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 85.0, 8, 240);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 90.0, 10, 200);

        // Act
        List<QuizAttempt> firstPage = quizAttemptDAO.getAttemptsByUser(TEST_USER_ID, 0, 2);
        List<QuizAttempt> secondPage = quizAttemptDAO.getAttemptsByUser(TEST_USER_ID, 2, 2);

        // Assert
        assertEquals(2, firstPage.size(), "First page should have 2 attempts");
        assertEquals(1, secondPage.size(), "Second page should have 1 attempt");
    }

    // ========================= UPDATE OPERATION TESTS =========================

    @Test
    @Order(16)
    @DisplayName("Test update quiz attempt")
    void testUpdateQuizAttempt_Success() throws SQLException {
        // Arrange
        QuizAttempt createdAttempt = quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, TEST_SCORE, TEST_TOTAL_QUESTIONS, TEST_TIME_TAKEN);
        
        createdAttempt.setScore(95.0);
        createdAttempt.setTotalQuestions(12);
        createdAttempt.setTimeTaken(400);
        createdAttempt.setPractice(true);

        // Act
        boolean updated = quizAttemptDAO.updateQuizAttempt(createdAttempt);

        // Assert
        assertTrue(updated, "Quiz attempt update should be successful");
        
        QuizAttempt foundAttempt = quizAttemptDAO.findById(createdAttempt.getAttemptId());
        assertEquals(95.0, foundAttempt.getScore(), 0.01);
        assertEquals(12, foundAttempt.getTotalQuestions());
        assertEquals(400, foundAttempt.getTimeTaken());
        assertTrue(foundAttempt.isPractice());
    }

    @Test
    @Order(17)
    @DisplayName("Test update attempt score")
    void testUpdateAttemptScore_Success() throws SQLException {
        // Arrange
        QuizAttempt createdAttempt = quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, TEST_SCORE, TEST_TOTAL_QUESTIONS, TEST_TIME_TAKEN);

        // Act
        boolean updated = quizAttemptDAO.updateAttemptScore(createdAttempt.getAttemptId(), 92.5);

        // Assert
        assertTrue(updated, "Attempt score update should be successful");
        
        QuizAttempt foundAttempt = quizAttemptDAO.findById(createdAttempt.getAttemptId());
        assertEquals(92.5, foundAttempt.getScore(), 0.01);
    }

    @Test
    @Order(18)
    @DisplayName("Test update attempt time")
    void testUpdateAttemptTime_Success() throws SQLException {
        // Arrange
        QuizAttempt createdAttempt = quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, TEST_SCORE, TEST_TOTAL_QUESTIONS, TEST_TIME_TAKEN);

        // Act
        boolean updated = quizAttemptDAO.updateAttemptTime(createdAttempt.getAttemptId(), 450);

        // Assert
        assertTrue(updated, "Attempt time update should be successful");
        
        QuizAttempt foundAttempt = quizAttemptDAO.findById(createdAttempt.getAttemptId());
        assertEquals(450, foundAttempt.getTimeTaken());
    }

    @Test
    @Order(19)
    @DisplayName("Test toggle practice mode")
    void testTogglePracticeMode_Success() throws SQLException {
        // Arrange
        QuizAttempt createdAttempt = quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, TEST_SCORE, TEST_TOTAL_QUESTIONS, TEST_TIME_TAKEN);
        boolean originalPracticeMode = createdAttempt.isPractice();

        // Act
        boolean updated = quizAttemptDAO.togglePracticeMode(createdAttempt.getAttemptId());

        // Assert
        assertTrue(updated, "Practice mode toggle should be successful");
        
        QuizAttempt foundAttempt = quizAttemptDAO.findById(createdAttempt.getAttemptId());
        assertEquals(!originalPracticeMode, foundAttempt.isPractice(), "Practice mode should be toggled");
    }

    // ========================= DELETE OPERATION TESTS =========================

    @Test
    @Order(20)
    @DisplayName("Test delete quiz attempt")
    void testDeleteAttempt_Success() throws SQLException {
        // Arrange
        QuizAttempt createdAttempt = quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, TEST_SCORE, TEST_TOTAL_QUESTIONS, TEST_TIME_TAKEN);

        // Act
        boolean deleted = quizAttemptDAO.deleteAttempt(createdAttempt.getAttemptId());

        // Assert
        assertTrue(deleted, "Quiz attempt deletion should be successful");
        
        QuizAttempt foundAttempt = quizAttemptDAO.findById(createdAttempt.getAttemptId());
        assertNull(foundAttempt, "Quiz attempt should not be found after deletion");
    }

    @Test
    @Order(21)
    @DisplayName("Test delete attempts by user")
    void testDeleteAttemptsByUser_Success() throws SQLException {
        // Arrange - ensure user has no attempts first
        quizAttemptDAO.deleteAttemptsByUser(TEST_USER_ID);
        
        // Create exactly 2 attempts for this test
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 85.0, 8, 240);

        // Verify we have exactly 2 attempts before deletion
        int countBeforeDeletion = quizAttemptDAO.getAttemptCountByUser(TEST_USER_ID);
        assertEquals(2, countBeforeDeletion, "Should have exactly 2 attempts before deletion");

        // Act
        int deletedCount = quizAttemptDAO.deleteAttemptsByUser(TEST_USER_ID);

        // Assert
        assertEquals(2, deletedCount, "Should delete exactly 2 attempts");
        
        List<QuizAttempt> remainingAttempts = quizAttemptDAO.getAttemptsByUser(TEST_USER_ID);
        assertEquals(0, remainingAttempts.size(), "No attempts should remain for the user");
    }

    @Test
    @Order(22)
    @DisplayName("Test delete attempts by quiz")
    void testDeleteAttemptsByQuiz_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID_2, TEST_QUIZ_ID, 85.0, 10, 240);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 90.0, 8, 200);

        // Act
        int deletedCount = quizAttemptDAO.deleteAttemptsByQuiz(TEST_QUIZ_ID);

        // Assert
        assertEquals(2, deletedCount, "Should delete 2 attempts for quiz 1");
        
        List<QuizAttempt> remainingAttempts = quizAttemptDAO.getAttemptsByQuiz(TEST_QUIZ_ID);
        assertEquals(0, remainingAttempts.size(), "No attempts should remain for the quiz");
    }

    @Test
    @Order(23)
    @DisplayName("Test delete practice attempts by user")
    void testDeletePracticeAttemptsByUser_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createPracticeAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 85.0, 8, 240);
        quizAttemptDAO.createPracticeAttempt(TEST_USER_ID, TEST_QUIZ_ID, 90.0, 10, 200);

        // Act
        int deletedCount = quizAttemptDAO.deletePracticeAttemptsByUser(TEST_USER_ID);

        // Assert
        assertEquals(2, deletedCount, "Should delete 2 practice attempts");
        
        List<QuizAttempt> remainingAttempts = quizAttemptDAO.getAttemptsByUser(TEST_USER_ID);
        assertEquals(1, remainingAttempts.size(), "Should have 1 graded attempt remaining");
        assertFalse(remainingAttempts.get(0).isPractice(), "Remaining attempt should not be practice");
    }

    // ========================= VALIDATION AND UTILITY TESTS =========================

    @Test
    @Order(24)
    @DisplayName("Test attempt exists check")
    void testAttemptExists_Success() throws SQLException {
        // Test non-existent attempt
        assertFalse(quizAttemptDAO.attemptExists(99999), "Should return false for non-existent attempt");

        // Arrange - create attempt
        QuizAttempt createdAttempt = quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, TEST_SCORE, TEST_TOTAL_QUESTIONS, TEST_TIME_TAKEN);

        // Test existing attempt
        assertTrue(quizAttemptDAO.attemptExists(createdAttempt.getAttemptId()), "Should return true for existing attempt");
    }

    @Test
    @Order(25)
    @DisplayName("Test has user attempted quiz check")
    void testHasUserAttemptedQuiz_Success() throws SQLException {
        // Test before attempt
        assertFalse(quizAttemptDAO.hasUserAttemptedQuiz(TEST_USER_ID, TEST_QUIZ_ID), "Should return false before attempt");

        // Arrange - create attempt
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, TEST_SCORE, TEST_TOTAL_QUESTIONS, TEST_TIME_TAKEN);

        // Test after attempt
        assertTrue(quizAttemptDAO.hasUserAttemptedQuiz(TEST_USER_ID, TEST_QUIZ_ID), "Should return true after attempt");
        assertFalse(quizAttemptDAO.hasUserAttemptedQuiz(TEST_USER_ID_2, TEST_QUIZ_ID), "Should return false for different user");
    }

    @Test
    @Order(26)
    @DisplayName("Test get attempt count by user")
    void testGetAttemptCountByUser_Success() throws SQLException {
        // Arrange
        int initialCount = quizAttemptDAO.getAttemptCountByUser(TEST_USER_ID);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 85.0, 8, 240);

        // Act
        int finalCount = quizAttemptDAO.getAttemptCountByUser(TEST_USER_ID);

        // Assert
        assertEquals(initialCount + 2, finalCount, "Attempt count should increase by 2");
    }

    @Test
    @Order(27)
    @DisplayName("Test get attempt count by quiz")
    void testGetAttemptCountByQuiz_Success() throws SQLException {
        // Arrange
        int initialCount = quizAttemptDAO.getAttemptCountByQuiz(TEST_QUIZ_ID);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID_2, TEST_QUIZ_ID, 85.0, 10, 240);

        // Act
        int finalCount = quizAttemptDAO.getAttemptCountByQuiz(TEST_QUIZ_ID);

        // Assert
        assertEquals(initialCount + 2, finalCount, "Quiz attempt count should increase by 2");
    }

    @Test
    @Order(28)
    @DisplayName("Test get best score")
    void testGetBestScore_Success() throws SQLException {
        // Test non-existent attempts
        assertEquals(-1, quizAttemptDAO.getBestScore(TEST_USER_ID, TEST_QUIZ_ID, false), "Should return -1 for no attempts");

        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 95.0, 10, 240);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 85.0, 10, 260);

        // Act
        double bestScore = quizAttemptDAO.getBestScore(TEST_USER_ID, TEST_QUIZ_ID, false);

        // Assert
        assertEquals(95.0, bestScore, 0.01, "Should return the best score");
    }

    @Test
    @Order(29)
    @DisplayName("Test get average score")
    void testGetAverageScore_Success() throws SQLException {
        // Test non-existent attempts
        assertEquals(-1, quizAttemptDAO.getAverageScore(TEST_USER_ID, false), "Should return -1 for no attempts");

        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 90.0, 8, 240);

        // Act
        double averageScore = quizAttemptDAO.getAverageScore(TEST_USER_ID, false);

        // Assert
        assertEquals(85.0, averageScore, 0.01, "Should return the average score");
    }

    @Test
    @Order(30)
    @DisplayName("Test get best attempt")
    void testGetBestAttempt_Success() throws SQLException {
        // Test non-existent attempts
        assertNull(quizAttemptDAO.getBestAttempt(TEST_USER_ID, TEST_QUIZ_ID, false), "Should return null for no attempts");

        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 350);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 95.0, 10, 240);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 95.0, 10, 300); // Same score, higher time

        // Act
        QuizAttempt bestAttempt = quizAttemptDAO.getBestAttempt(TEST_USER_ID, TEST_QUIZ_ID, false);

        // Assert
        assertNotNull(bestAttempt, "Should return the best attempt");
        assertEquals(95.0, bestAttempt.getScore(), 0.01, "Should have the best score");
        assertEquals(240, bestAttempt.getTimeTaken(), "Should have the shortest time for best score");
    }

    @Test
    @Order(31)
    @DisplayName("Test comprehensive statistics")
    void testComprehensiveStatistics_Success() throws SQLException {
        // Arrange
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 80.0, 10, 250);
        quizAttemptDAO.createPracticeAttempt(TEST_USER_ID_2, TEST_QUIZ_ID_2, 85.0, 8, 240);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 90.0, 8, 200);

        // Act & Assert
        int totalCount = quizAttemptDAO.getTotalAttemptCount();
        assertTrue(totalCount >= 3, "Total attempt count should be at least 3");

        int practiceCount = quizAttemptDAO.getPracticeAttemptCount();
        assertTrue(practiceCount >= 1, "Practice attempt count should be at least 1");

        int gradedCount = quizAttemptDAO.getGradedAttemptCount();
        assertTrue(gradedCount >= 2, "Graded attempt count should be at least 2");

        int recentCount = quizAttemptDAO.getRecentAttemptCount(7);
        assertTrue(recentCount >= 3, "Recent attempt count should be at least 3");

        double quizAverage = quizAttemptDAO.getQuizAverageScore(TEST_QUIZ_ID, false);
        assertTrue(quizAverage > 0, "Quiz average score should be positive");
    }

    @Test
    @Order(32)
    @DisplayName("Test get leaderboard data (best score per user per quiz)")
    void testGetLeaderboardData() throws SQLException {
        // Clean up before test
        cleanUpTestAttempts();

        // Arrange: user 1 takes quiz 1 twice, user 2 takes quiz 1 once, user 1 takes quiz 2 once
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 60.0, 10, 200);
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID, 95.0, 10, 180); // best for user 1, quiz 1
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID_2, TEST_QUIZ_ID, 88.0, 10, 210); // best for user 2, quiz 1
        quizAttemptDAO.createSimpleAttempt(TEST_USER_ID, TEST_QUIZ_ID_2, 77.0, 10, 190); // best for user 1, quiz 2

        // Act
        List<model.LeaderboardEntry> leaderboard = quizAttemptDAO.getLeaderboardData();

        // Assert
        // Should have 3 entries: (user1,quiz1), (user2,quiz1), (user1,quiz2)
        assertEquals(3, leaderboard.size(), "Should return one entry per user per quiz");
        boolean foundUser1Quiz1 = false, foundUser2Quiz1 = false, foundUser1Quiz2 = false;
        for (model.LeaderboardEntry entry : leaderboard) {
            if (entry.getQuizId() == TEST_QUIZ_ID && entry.getUserId() == TEST_USER_ID) {
                foundUser1Quiz1 = true;
                assertEquals(95.0, entry.getBestScore(), 0.01);
            }
            if (entry.getQuizId() == TEST_QUIZ_ID && entry.getUserId() == TEST_USER_ID_2) {
                foundUser2Quiz1 = true;
                assertEquals(88.0, entry.getBestScore(), 0.01);
            }
            if (entry.getQuizId() == TEST_QUIZ_ID_2 && entry.getUserId() == TEST_USER_ID) {
                foundUser1Quiz2 = true;
                assertEquals(77.0, entry.getBestScore(), 0.01);
            }
        }
        assertTrue(foundUser1Quiz1, "User 1, Quiz 1 should be present");
        assertTrue(foundUser2Quiz1, "User 2, Quiz 1 should be present");
        assertTrue(foundUser1Quiz2, "User 1, Quiz 2 should be present");
    }
} 