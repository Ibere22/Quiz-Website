package dao;

import model.Question;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for QuestionDAO class
 * Tests all CRUD operations, validation methods, and question-specific functionality
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuestionDAOTest {

    private static Connection connection;
    private static QuestionDAO questionDAO;
    private static final int TEST_QUIZ_ID = 1; // Assuming quiz ID 1 exists
    private static final int TEST_QUIZ_ID_2 = 2; // For testing multiple quizzes
    private static final String TEST_QUESTION_TEXT = "What is the capital of France?";
    private static final String TEST_CORRECT_ANSWER = "Paris";
    private static final String TEST_IMAGE_URL = "https://example.com/image.jpg";

    @BeforeAll
    static void setUpClass() throws SQLException {
        connection = DatabaseConnection.getConnection();
        questionDAO = new QuestionDAO(connection);
        
        // Clean up any existing test data
        cleanUpTestData();
        
        // Create test quizzes if they don't exist
        createTestQuizzes();
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
            // Delete ALL questions from test quizzes to ensure clean state
            stmt.executeUpdate("DELETE FROM questions WHERE quiz_id IN (1, 2)");
        }
    }

    private static void createTestQuizzes() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Insert test quizzes if they don't exist
            stmt.executeUpdate("INSERT IGNORE INTO quizzes (id, title, description, creator_id) VALUES " +
                "(1, 'Test Quiz 1', 'Test Description 1', 1), " +
                "(2, 'Test Quiz 2', 'Test Description 2', 1)");
        }
    }

    // ========================= CREATE OPERATION TESTS =========================

    @Test
    @Order(1)
    @DisplayName("Test create simple question-response question")
    void testCreateSimpleQuestion_QuestionResponse_Success() throws SQLException {
        // Act
        Question createdQuestion = questionDAO.createSimpleQuestion(
            TEST_QUIZ_ID, 
            Question.TYPE_QUESTION_RESPONSE, 
            TEST_QUESTION_TEXT, 
            TEST_CORRECT_ANSWER, 
            1
        );

        // Assert
        assertNotNull(createdQuestion, "Question should be created successfully");
        assertTrue(createdQuestion.getQuestionId() > 0, "Question ID should be generated");
        assertEquals(TEST_QUIZ_ID, createdQuestion.getQuizId());
        assertEquals(Question.TYPE_QUESTION_RESPONSE, createdQuestion.getQuestionType());
        assertEquals(TEST_QUESTION_TEXT, createdQuestion.getQuestionText());
        assertEquals(TEST_CORRECT_ANSWER, createdQuestion.getCorrectAnswer());
        assertEquals(1, createdQuestion.getOrderNum());
        assertTrue(createdQuestion.isQuestionResponse());
    }

    @Test
    @Order(2)
    @DisplayName("Test create fill-in-blank question")
    void testCreateSimpleQuestion_FillInBlank_Success() throws SQLException {
        // Act
        Question createdQuestion = questionDAO.createSimpleQuestion(
            TEST_QUIZ_ID, 
            Question.TYPE_FILL_IN_BLANK, 
            "The capital of France is ____", 
            "Paris", 
            1
        );

        // Assert
        assertNotNull(createdQuestion, "Question should be created successfully");
        assertEquals(Question.TYPE_FILL_IN_BLANK, createdQuestion.getQuestionType());
        assertTrue(createdQuestion.isFillInBlank());
    }

    @Test
    @Order(3)
    @DisplayName("Test create multiple choice question")
    void testCreateMultipleChoiceQuestion_Success() throws SQLException {
        // Arrange
        List<String> choices = Arrays.asList("Paris", "London", "Berlin", "Madrid");

        // Act
        Question createdQuestion = questionDAO.createMultipleChoiceQuestion(
            TEST_QUIZ_ID, 
            TEST_QUESTION_TEXT, 
            "Paris", 
            choices, 
            1
        );

        // Assert
        assertNotNull(createdQuestion, "Question should be created successfully");
        assertEquals(Question.TYPE_MULTIPLE_CHOICE, createdQuestion.getQuestionType());
        assertTrue(createdQuestion.isMultipleChoice());
        assertNotNull(createdQuestion.getChoices(), "Choices should be set");
        assertEquals(4, createdQuestion.getChoices().size());
        assertEquals("Paris", createdQuestion.getChoices().get(0));
    }

    @Test
    @Order(4)
    @DisplayName("Test create picture-response question")
    void testCreatePictureQuestion_Success() throws SQLException {
        // Act
        Question createdQuestion = questionDAO.createPictureQuestion(
            TEST_QUIZ_ID, 
            "What city is shown in this picture?", 
            "Paris", 
            TEST_IMAGE_URL, 
            1
        );

        // Assert
        assertNotNull(createdQuestion, "Question should be created successfully");
        assertEquals(Question.TYPE_PICTURE_RESPONSE, createdQuestion.getQuestionType());
        assertTrue(createdQuestion.isPictureResponse());
        assertEquals(TEST_IMAGE_URL, createdQuestion.getImageUrl());
    }

    @Test
    @Order(5)
    @DisplayName("Test create question with Question object")
    void testCreateQuestion_WithObject_Success() throws SQLException {
        // Arrange
        Question question = new Question(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question with object", "Test answer", 1);

        // Act
        Question createdQuestion = questionDAO.createQuestion(question);

        // Assert
        assertNotNull(createdQuestion, "Question should be created successfully");
        assertTrue(createdQuestion.getQuestionId() > 0, "Question ID should be generated");
        assertEquals("Test question with object", createdQuestion.getQuestionText());
        assertEquals("Test answer", createdQuestion.getCorrectAnswer());
    }

    // ========================= READ OPERATION TESTS =========================

    @Test
    @Order(6)
    @DisplayName("Test find question by ID")
    void testFindById_ExistingQuestion_Success() throws SQLException {
        // Arrange
        Question createdQuestion = questionDAO.createSimpleQuestion(
            TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, TEST_QUESTION_TEXT, TEST_CORRECT_ANSWER, 1);

        // Act
        Question foundQuestion = questionDAO.findById(createdQuestion.getQuestionId());

        // Assert
        assertNotNull(foundQuestion, "Question should be found");
        assertEquals(createdQuestion.getQuestionId(), foundQuestion.getQuestionId());
        assertEquals(TEST_QUESTION_TEXT, foundQuestion.getQuestionText());
        assertEquals(TEST_CORRECT_ANSWER, foundQuestion.getCorrectAnswer());
    }

    @Test
    @Order(7)
    @DisplayName("Test find question by non-existent ID")
    void testFindById_NonExistentQuestion_ReturnsNull() throws SQLException {
        // Act
        Question foundQuestion = questionDAO.findById(99999);

        // Assert
        assertNull(foundQuestion, "Should return null for non-existent question");
    }

    @Test
    @Order(8)
    @DisplayName("Test get questions by quiz ID")
    void testGetQuestionsByQuizId_Success() throws SQLException {
        // Arrange
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 1", "Answer 1", 1);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_FILL_IN_BLANK, 
            "Test question 2", "Answer 2", 2);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID_2, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 3", "Answer 3", 1);

        // Act
        List<Question> questions = questionDAO.getQuestionsByQuizId(TEST_QUIZ_ID);

        // Assert
        assertNotNull(questions, "Questions list should not be null");
        assertEquals(2, questions.size(), "Should return 2 questions for quiz 1");
        assertEquals(1, questions.get(0).getOrderNum(), "First question should have order 1");
        assertEquals(2, questions.get(1).getOrderNum(), "Second question should have order 2");
    }

    @Test
    @Order(9)
    @DisplayName("Test get questions by type")
    void testGetQuestionsByType_Success() throws SQLException {
        // Clean up any existing questions for the test quizzes
        questionDAO.deleteQuestionsByQuizId(TEST_QUIZ_ID);
        questionDAO.deleteQuestionsByQuizId(TEST_QUIZ_ID_2);
        // Arrange
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 1", "Answer 1", 1);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_FILL_IN_BLANK, 
            "Test question 2", "Answer 2", 2);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID_2, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 3", "Answer 3", 1);

        // Act
        List<Question> responseQuestions = questionDAO.getQuestionsByType(Question.TYPE_QUESTION_RESPONSE);
        List<Question> fillInQuestions = questionDAO.getQuestionsByType(Question.TYPE_FILL_IN_BLANK);

        // Assert
        assertTrue(responseQuestions.size() >= 2, "Should return at least 2 question-response questions");
        assertTrue(fillInQuestions.size() >= 1, "Should return at least 1 fill-in-blank question");
    }

    @Test
    @Order(10)
    @DisplayName("Test get questions by quiz and type")
    void testGetQuestionsByQuizAndType_Success() throws SQLException {
        // Arrange
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 1", "Answer 1", 1);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_FILL_IN_BLANK, 
            "Test question 2", "Answer 2", 2);

        // Act
        List<Question> questions = questionDAO.getQuestionsByQuizAndType(
            TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE);

        // Assert
        assertEquals(1, questions.size(), "Should return 1 question-response question for quiz 1");
        assertEquals(Question.TYPE_QUESTION_RESPONSE, questions.get(0).getQuestionType());
    }

    @Test
    @Order(11)
    @DisplayName("Test search questions by text")
    void testSearchQuestionsByText_Success() throws SQLException {
        // Arrange
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "What is the capital of France?", "Paris", 1);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "What is Java programming?", "Programming language", 2);

        // Act
        List<Question> searchResults = questionDAO.searchQuestionsByText("capital");

        // Assert
        assertEquals(1, searchResults.size(), "Should find 1 question containing 'capital'");
        assertTrue(searchResults.get(0).getQuestionText().contains("capital"));
    }

    @Test
    @Order(12)
    @DisplayName("Test get all questions with pagination")
    void testGetAllQuestions_WithPagination_Success() throws SQLException {
        // Arrange
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 1", "Answer 1", 1);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 2", "Answer 2", 2);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 3", "Answer 3", 3);

        // Act
        List<Question> firstPage = questionDAO.getAllQuestions(0, 2);
        List<Question> secondPage = questionDAO.getAllQuestions(2, 2);

        // Assert
        assertEquals(2, firstPage.size(), "First page should have 2 questions");
        assertTrue(secondPage.size() >= 1, "Second page should have at least 1 question");
    }

    @Test
    @Order(13)
    @DisplayName("Test get questions in random order")
    void testGetQuestionsRandomOrder_Success() throws SQLException {
        // Arrange
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 1", "Answer 1", 1);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 2", "Answer 2", 2);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 3", "Answer 3", 3);

        // Act
        List<Question> randomQuestions = questionDAO.getQuestionsRandomOrder(TEST_QUIZ_ID);

        // Assert
        assertEquals(3, randomQuestions.size(), "Should return all 3 questions");
        // Note: We can't test randomness reliably, but we can test that all questions are returned
    }

    // ========================= UPDATE OPERATION TESTS =========================

    @Test
    @Order(14)
    @DisplayName("Test update question")
    void testUpdateQuestion_Success() throws SQLException {
        // Arrange
        Question createdQuestion = questionDAO.createSimpleQuestion(
            TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, TEST_QUESTION_TEXT, TEST_CORRECT_ANSWER, 1);
        
        createdQuestion.setQuestionText("Updated question text");
        createdQuestion.setCorrectAnswer("Updated answer");

        // Act
        boolean updated = questionDAO.updateQuestion(createdQuestion);

        // Assert
        assertTrue(updated, "Question update should be successful");
        
        Question foundQuestion = questionDAO.findById(createdQuestion.getQuestionId());
        assertEquals("Updated question text", foundQuestion.getQuestionText());
        assertEquals("Updated answer", foundQuestion.getCorrectAnswer());
    }

    @Test
    @Order(15)
    @DisplayName("Test update question text")
    void testUpdateQuestionText_Success() throws SQLException {
        // Arrange
        Question createdQuestion = questionDAO.createSimpleQuestion(
            TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, TEST_QUESTION_TEXT, TEST_CORRECT_ANSWER, 1);

        // Act
        boolean updated = questionDAO.updateQuestionText(createdQuestion.getQuestionId(), "New question text");

        // Assert
        assertTrue(updated, "Question text update should be successful");
        
        Question foundQuestion = questionDAO.findById(createdQuestion.getQuestionId());
        assertEquals("New question text", foundQuestion.getQuestionText());
    }

    @Test
    @Order(16)
    @DisplayName("Test update correct answer")
    void testUpdateCorrectAnswer_Success() throws SQLException {
        // Arrange
        Question createdQuestion = questionDAO.createSimpleQuestion(
            TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, TEST_QUESTION_TEXT, TEST_CORRECT_ANSWER, 1);

        // Act
        boolean updated = questionDAO.updateCorrectAnswer(createdQuestion.getQuestionId(), "New correct answer");

        // Assert
        assertTrue(updated, "Correct answer update should be successful");
        
        Question foundQuestion = questionDAO.findById(createdQuestion.getQuestionId());
        assertEquals("New correct answer", foundQuestion.getCorrectAnswer());
    }

    @Test
    @Order(17)
    @DisplayName("Test update question choices")
    void testUpdateQuestionChoices_Success() throws SQLException {
        // Arrange
        List<String> originalChoices = Arrays.asList("A", "B", "C", "D");
        Question createdQuestion = questionDAO.createMultipleChoiceQuestion(
            TEST_QUIZ_ID, "Test multiple choice question", "A", originalChoices, 1);
        
        List<String> newChoices = Arrays.asList("Option 1", "Option 2", "Option 3", "Option 4");

        // Act
        boolean updated = questionDAO.updateQuestionChoices(createdQuestion.getQuestionId(), newChoices);

        // Assert
        assertTrue(updated, "Question choices update should be successful");
        
        Question foundQuestion = questionDAO.findById(createdQuestion.getQuestionId());
        assertEquals(newChoices, foundQuestion.getChoices());
    }

    @Test
    @Order(18)
    @DisplayName("Test update image URL")
    void testUpdateImageUrl_Success() throws SQLException {
        // Arrange
        Question createdQuestion = questionDAO.createPictureQuestion(
            TEST_QUIZ_ID, "Test picture question", "Test answer", TEST_IMAGE_URL, 1);

        // Act
        boolean updated = questionDAO.updateImageUrl(createdQuestion.getQuestionId(), "https://newimage.com/pic.jpg");

        // Assert
        assertTrue(updated, "Image URL update should be successful");
        
        Question foundQuestion = questionDAO.findById(createdQuestion.getQuestionId());
        assertEquals("https://newimage.com/pic.jpg", foundQuestion.getImageUrl());
    }

    @Test
    @Order(19)
    @DisplayName("Test update question order")
    void testUpdateQuestionOrder_Success() throws SQLException {
        // Arrange
        Question createdQuestion = questionDAO.createSimpleQuestion(
            TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, TEST_QUESTION_TEXT, TEST_CORRECT_ANSWER, 1);

        // Act
        boolean updated = questionDAO.updateQuestionOrder(createdQuestion.getQuestionId(), 5);

        // Assert
        assertTrue(updated, "Question order update should be successful");
        
        Question foundQuestion = questionDAO.findById(createdQuestion.getQuestionId());
        assertEquals(5, foundQuestion.getOrderNum());
    }

    @Test
    @Order(20)
    @DisplayName("Test reorder questions")
    void testReorderQuestions_Success() throws SQLException {
        // Arrange
        Question q1 = questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 1", "Answer 1", 1);
        Question q2 = questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 2", "Answer 2", 2);
        Question q3 = questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 3", "Answer 3", 3);
        
        List<Integer> newOrder = Arrays.asList(q3.getQuestionId(), q1.getQuestionId(), q2.getQuestionId());

        // Act
        boolean reordered = questionDAO.reorderQuestions(TEST_QUIZ_ID, newOrder);

        // Assert
        assertTrue(reordered, "Question reordering should be successful");
        
        List<Question> reorderedQuestions = questionDAO.getQuestionsByQuizId(TEST_QUIZ_ID);
        assertEquals(3, reorderedQuestions.size(), "Should have exactly 3 questions after reordering");
        assertEquals(q3.getQuestionId(), reorderedQuestions.get(0).getQuestionId());
        assertEquals(q1.getQuestionId(), reorderedQuestions.get(1).getQuestionId());
        assertEquals(q2.getQuestionId(), reorderedQuestions.get(2).getQuestionId());
    }

    // ========================= DELETE OPERATION TESTS =========================

    @Test
    @Order(21)
    @DisplayName("Test delete question")
    void testDeleteQuestion_Success() throws SQLException {
        // Arrange
        Question createdQuestion = questionDAO.createSimpleQuestion(
            TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, TEST_QUESTION_TEXT, TEST_CORRECT_ANSWER, 1);

        // Act
        boolean deleted = questionDAO.deleteQuestion(createdQuestion.getQuestionId());

        // Assert
        assertTrue(deleted, "Question deletion should be successful");
        
        Question foundQuestion = questionDAO.findById(createdQuestion.getQuestionId());
        assertNull(foundQuestion, "Question should not be found after deletion");
    }

    @Test
    @Order(22)
    @DisplayName("Test delete questions by quiz ID")
    void testDeleteQuestionsByQuizId_Success() throws SQLException {
        // Arrange - ensure quiz is clean first
        questionDAO.deleteQuestionsByQuizId(TEST_QUIZ_ID);
        
        // Create exactly 2 questions for this test
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 1", "Answer 1", 1);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 2", "Answer 2", 2);

        // Verify we have exactly 2 questions before deletion
        int countBeforeDeletion = questionDAO.getQuestionCountByQuiz(TEST_QUIZ_ID);
        assertEquals(2, countBeforeDeletion, "Should have exactly 2 questions before deletion");

        // Act
        int deletedCount = questionDAO.deleteQuestionsByQuizId(TEST_QUIZ_ID);

        // Assert
        assertEquals(2, deletedCount, "Should delete exactly 2 questions");
        
        List<Question> remainingQuestions = questionDAO.getQuestionsByQuizId(TEST_QUIZ_ID);
        assertEquals(0, remainingQuestions.size(), "No questions should remain for the quiz");
    }

    // ========================= VALIDATION AND UTILITY TESTS =========================

    @Test
    @Order(23)
    @DisplayName("Test question exists check")
    void testQuestionExists_Success() throws SQLException {
        // Test non-existent question
        assertFalse(questionDAO.questionExists(99999), "Should return false for non-existent question");

        // Arrange - create question
        Question createdQuestion = questionDAO.createSimpleQuestion(
            TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, TEST_QUESTION_TEXT, TEST_CORRECT_ANSWER, 1);

        // Test existing question
        assertTrue(questionDAO.questionExists(createdQuestion.getQuestionId()), 
            "Should return true for existing question");
    }

    @Test
    @Order(24)
    @DisplayName("Test get question count by quiz")
    void testGetQuestionCountByQuiz_Success() throws SQLException {
        // Arrange
        int initialCount = questionDAO.getQuestionCountByQuiz(TEST_QUIZ_ID);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 1", "Answer 1", 1);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 2", "Answer 2", 2);

        // Act
        int finalCount = questionDAO.getQuestionCountByQuiz(TEST_QUIZ_ID);

        // Assert
        assertEquals(initialCount + 2, finalCount, "Question count should increase by 2");
    }

    @Test
    @Order(25)
    @DisplayName("Test get question count by type")
    void testGetQuestionCountByType_Success() throws SQLException {
        // Arrange
        int initialCount = questionDAO.getQuestionCountByType(Question.TYPE_MULTIPLE_CHOICE);
        List<String> choices = Arrays.asList("A", "B", "C", "D");
        questionDAO.createMultipleChoiceQuestion(TEST_QUIZ_ID, "Test MC question", "A", choices, 1);

        // Act
        int finalCount = questionDAO.getQuestionCountByType(Question.TYPE_MULTIPLE_CHOICE);

        // Assert
        assertEquals(initialCount + 1, finalCount, "Multiple choice count should increase by 1");
    }

    @Test
    @Order(26)
    @DisplayName("Test get total question count")
    void testGetTotalQuestionCount_Success() throws SQLException {
        // Arrange
        int initialCount = questionDAO.getTotalQuestionCount();
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 1", "Answer 1", 1);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID_2, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 2", "Answer 2", 1);

        // Act
        int finalCount = questionDAO.getTotalQuestionCount();

        // Assert
        assertEquals(initialCount + 2, finalCount, "Total question count should increase by 2");
    }

    @Test
    @Order(27)
    @DisplayName("Test get next order number")
    void testGetNextOrderNumber_Success() throws SQLException {
        // Test for empty quiz
        assertEquals(1, questionDAO.getNextOrderNumber(TEST_QUIZ_ID), 
            "Should return 1 for empty quiz");

        // Add questions and test
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 1", "Answer 1", 1);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 2", "Answer 2", 2);

        assertEquals(3, questionDAO.getNextOrderNumber(TEST_QUIZ_ID), 
            "Should return 3 for quiz with 2 questions");
    }

    @Test
    @Order(28)
    @DisplayName("Test get all questions without pagination")
    void testGetAllQuestions_WithoutPagination_Success() throws SQLException {
        // Arrange
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 1", "Answer 1", 1);
        questionDAO.createSimpleQuestion(TEST_QUIZ_ID_2, Question.TYPE_QUESTION_RESPONSE, 
            "Test question 2", "Answer 2", 1);

        // Act
        List<Question> allQuestions = questionDAO.getAllQuestions();

        // Assert
        assertNotNull(allQuestions, "All questions list should not be null");
        assertTrue(allQuestions.size() >= 2, "Should return at least 2 questions");
    }

    @Test
    @Order(29)
    @DisplayName("Test question with null image URL")
    void testCreateQuestion_WithNullImageUrl_Success() throws SQLException {
        // Arrange
        Question question = new Question(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question without image", "Test answer", 1);
        question.setImageUrl(null);

        // Act
        Question createdQuestion = questionDAO.createQuestion(question);

        // Assert
        assertNotNull(createdQuestion, "Question should be created successfully");
        assertNull(createdQuestion.getImageUrl(), "Image URL should be null");
    }

    @Test
    @Order(30)
    @DisplayName("Test question with null choices JSON")
    void testCreateQuestion_WithNullChoicesJson_Success() throws SQLException {
        // Arrange
        Question question = new Question(TEST_QUIZ_ID, Question.TYPE_QUESTION_RESPONSE, 
            "Test question without choices", "Test answer", 1);
        question.setChoicesJson(null);

        // Act
        Question createdQuestion = questionDAO.createQuestion(question);

        // Assert
        assertNotNull(createdQuestion, "Question should be created successfully");
        assertNull(createdQuestion.getChoicesJson(), "Choices JSON should be null");
        assertNull(createdQuestion.getChoices(), "Choices list should be null");
    }
} 