package dao;

import model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Question entity
 * Provides full CRUD operations and question-specific functionality
 */
public class QuestionDAO {
    
    private Connection connection;
    
    // Constructor that takes a database connection
    public QuestionDAO(Connection connection) {
        this.connection = connection;
    }
    
    // ========================= CREATE OPERATIONS =========================
    
    /**
     * Create a new question
     * @param question Question object with all necessary fields
     * @return The created question with generated ID, or null if creation failed
     * @throws SQLException If database error occurs
     */
    public Question createQuestion(Question question) throws SQLException {
        String sql = "INSERT INTO questions (quiz_id, question_type, question_text, correct_answer, choices_json, image_url, order_num) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, question.getQuizId());
            stmt.setString(2, question.getQuestionType());
            stmt.setString(3, question.getQuestionText());
            stmt.setString(4, question.getCorrectAnswer());
            stmt.setString(5, question.getChoicesJson());
            stmt.setString(6, question.getImageUrl());
            stmt.setInt(7, question.getOrderNum());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    question.setQuestionId(generatedKeys.getInt(1));
                    return question;
                }
            }
        }
        return null;
    }
    
    /**
     * Create a simple question (question-response or fill-in-blank)
     * @param quizId ID of the quiz this question belongs to
     * @param questionType Type of question
     * @param questionText The question text
     * @param correctAnswer The correct answer
     * @param orderNum Order within the quiz
     * @return Created question with generated ID
     * @throws SQLException If database error occurs
     */
    public Question createSimpleQuestion(int quizId, String questionType, String questionText, String correctAnswer, int orderNum) throws SQLException {
        Question question = new Question(quizId, questionType, questionText, correctAnswer, orderNum);
        return createQuestion(question);
    }
    
    /**
     * Create a multiple choice question
     * @param quizId ID of the quiz this question belongs to
     * @param questionText The question text
     * @param correctAnswer The correct answer
     * @param choices List of choice options
     * @param orderNum Order within the quiz
     * @return Created question with generated ID
     * @throws SQLException If database error occurs
     */
    public Question createMultipleChoiceQuestion(int quizId, String questionText, String correctAnswer, List<String> choices, int orderNum) throws SQLException {
        Question question = new Question(quizId, Question.TYPE_MULTIPLE_CHOICE, questionText, correctAnswer, orderNum);
        question.setChoices(choices);
        return createQuestion(question);
    }
    
    /**
     * Create a picture-response question
     * @param quizId ID of the quiz this question belongs to
     * @param questionText The question text
     * @param correctAnswer The correct answer
     * @param imageUrl URL of the image
     * @param orderNum Order within the quiz
     * @return Created question with generated ID
     * @throws SQLException If database error occurs
     */
    public Question createPictureQuestion(int quizId, String questionText, String correctAnswer, String imageUrl, int orderNum) throws SQLException {
        Question question = new Question(quizId, Question.TYPE_PICTURE_RESPONSE, questionText, correctAnswer, orderNum);
        question.setImageUrl(imageUrl);
        return createQuestion(question);
    }
    
    // ========================= READ OPERATIONS =========================
    
    /**
     * Find a question by its ID
     * @param questionId The question ID to search for
     * @return Question object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public Question findById(int questionId) throws SQLException {
        String sql = "SELECT id, quiz_id, question_type, question_text, correct_answer, choices_json, image_url, order_num FROM questions WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToQuestion(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get all questions for a specific quiz
     * @param quizId The quiz ID
     * @return List of questions ordered by order_num
     * @throws SQLException If database error occurs
     */
    public List<Question> getQuestionsByQuizId(int quizId) throws SQLException {
        String sql = "SELECT id, quiz_id, question_type, question_text, correct_answer, choices_json, image_url, order_num FROM questions WHERE quiz_id = ? ORDER BY order_num ASC";
        List<Question> questions = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapRowToQuestion(rs));
                }
            }
        }
        return questions;
    }
    
    /**
     * Get questions by type
     * @param questionType The question type to filter by
     * @return List of questions of the specified type
     * @throws SQLException If database error occurs
     */
    public List<Question> getQuestionsByType(String questionType) throws SQLException {
        String sql = "SELECT id, quiz_id, question_type, question_text, correct_answer, choices_json, image_url, order_num FROM questions WHERE question_type = ? ORDER BY quiz_id, order_num";
        List<Question> questions = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, questionType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapRowToQuestion(rs));
                }
            }
        }
        return questions;
    }
    
    /**
     * Get questions by quiz ID and type
     * @param quizId The quiz ID
     * @param questionType The question type to filter by
     * @return List of questions of the specified type in the quiz
     * @throws SQLException If database error occurs
     */
    public List<Question> getQuestionsByQuizAndType(int quizId, String questionType) throws SQLException {
        String sql = "SELECT id, quiz_id, question_type, question_text, correct_answer, choices_json, image_url, order_num FROM questions WHERE quiz_id = ? AND question_type = ? ORDER BY order_num ASC";
        List<Question> questions = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            stmt.setString(2, questionType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapRowToQuestion(rs));
                }
            }
        }
        return questions;
    }
    
    /**
     * Search questions by text content
     * @param searchTerm Search term to match against question text
     * @return List of questions containing the search term
     * @throws SQLException If database error occurs
     */
    public List<Question> searchQuestionsByText(String searchTerm) throws SQLException {
        String sql = "SELECT id, quiz_id, question_type, question_text, correct_answer, choices_json, image_url, order_num FROM questions WHERE question_text LIKE ? ORDER BY quiz_id, order_num";
        List<Question> questions = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapRowToQuestion(rs));
                }
            }
        }
        return questions;
    }
    
    /**
     * Get all questions with pagination
     * @param offset Starting position (0-based)
     * @param limit Maximum number of questions to return
     * @return List of questions
     * @throws SQLException If database error occurs
     */
    public List<Question> getAllQuestions(int offset, int limit) throws SQLException {
        String sql = "SELECT id, quiz_id, question_type, question_text, correct_answer, choices_json, image_url, order_num FROM questions ORDER BY quiz_id, order_num LIMIT ? OFFSET ?";
        List<Question> questions = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapRowToQuestion(rs));
                }
            }
        }
        return questions;
    }
    
    /**
     * Get all questions
     * @return List of all questions
     * @throws SQLException If database error occurs
     */
    public List<Question> getAllQuestions() throws SQLException {
        String sql = "SELECT id, quiz_id, question_type, question_text, correct_answer, choices_json, image_url, order_num FROM questions ORDER BY quiz_id, order_num";
        List<Question> questions = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapRowToQuestion(rs));
                }
            }
        }
        return questions;
    }
    
    // ========================= UPDATE OPERATIONS =========================
    
    /**
     * Update a question
     * @param question Question object with updated information
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateQuestion(Question question) throws SQLException {
        String sql = "UPDATE questions SET quiz_id = ?, question_type = ?, question_text = ?, correct_answer = ?, choices_json = ?, image_url = ?, order_num = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, question.getQuizId());
            stmt.setString(2, question.getQuestionType());
            stmt.setString(3, question.getQuestionText());
            stmt.setString(4, question.getCorrectAnswer());
            stmt.setString(5, question.getChoicesJson());
            stmt.setString(6, question.getImageUrl());
            stmt.setInt(7, question.getOrderNum());
            stmt.setInt(8, question.getQuestionId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update question text
     * @param questionId The question ID
     * @param newQuestionText New question text
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateQuestionText(int questionId, String newQuestionText) throws SQLException {
        String sql = "UPDATE questions SET question_text = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newQuestionText);
            stmt.setInt(2, questionId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update correct answer
     * @param questionId The question ID
     * @param newCorrectAnswer New correct answer
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateCorrectAnswer(int questionId, String newCorrectAnswer) throws SQLException {
        String sql = "UPDATE questions SET correct_answer = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newCorrectAnswer);
            stmt.setInt(2, questionId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update question choices (for multiple choice questions)
     * @param questionId The question ID
     * @param choices New list of choices
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateQuestionChoices(int questionId, List<String> choices) throws SQLException {
        Question question = findById(questionId);
        if (question != null) {
            question.setChoices(choices);
            String sql = "UPDATE questions SET choices_json = ? WHERE id = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, question.getChoicesJson());
                stmt.setInt(2, questionId);
                
                return stmt.executeUpdate() > 0;
            }
        }
        return false;
    }
    
    /**
     * Update question image URL
     * @param questionId The question ID
     * @param newImageUrl New image URL
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateImageUrl(int questionId, String newImageUrl) throws SQLException {
        String sql = "UPDATE questions SET image_url = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newImageUrl);
            stmt.setInt(2, questionId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update question order
     * @param questionId The question ID
     * @param newOrderNum New order number
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateQuestionOrder(int questionId, int newOrderNum) throws SQLException {
        String sql = "UPDATE questions SET order_num = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newOrderNum);
            stmt.setInt(2, questionId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Reorder all questions in a quiz
     * @param quizId The quiz ID
     * @param questionIds List of question IDs in the desired order
     * @return true if reordering was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean reorderQuestions(int quizId, List<Integer> questionIds) throws SQLException {
        String sql = "UPDATE questions SET order_num = ? WHERE id = ? AND quiz_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < questionIds.size(); i++) {
                stmt.setInt(1, i + 1); // Order numbers start from 1
                stmt.setInt(2, questionIds.get(i));
                stmt.setInt(3, quizId);
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            // Check if all updates were successful
            for (int result : results) {
                if (result == Statement.EXECUTE_FAILED) {
                    return false;
                }
            }
            return true;
        }
    }
    
    // ========================= DELETE OPERATIONS =========================
    
    /**
     * Delete a question by ID
     * @param questionId The question ID to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean deleteQuestion(int questionId) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete all questions for a specific quiz
     * @param quizId The quiz ID
     * @return Number of questions deleted
     * @throws SQLException If database error occurs
     */
    public int deleteQuestionsByQuizId(int quizId) throws SQLException {
        String sql = "DELETE FROM questions WHERE quiz_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            return stmt.executeUpdate();
        }
    }
    
    // ========================= VALIDATION AND UTILITY METHODS =========================
    
    /**
     * Check if a question exists
     * @param questionId The question ID to check
     * @return true if question exists, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean questionExists(int questionId) throws SQLException {
        String sql = "SELECT 1 FROM questions WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Get the count of questions in a quiz
     * @param quizId The quiz ID
     * @return Number of questions in the quiz
     * @throws SQLException If database error occurs
     */
    public int getQuestionCountByQuiz(int quizId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions WHERE quiz_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Get the count of questions by type
     * @param questionType The question type
     * @return Number of questions of the specified type
     * @throws SQLException If database error occurs
     */
    public int getQuestionCountByType(String questionType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions WHERE question_type = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, questionType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Get total question count
     * @return Total number of questions in the database
     * @throws SQLException If database error occurs
     */
    public int getTotalQuestionCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Get next order number for a quiz
     * @param quizId The quiz ID
     * @return Next available order number
     * @throws SQLException If database error occurs
     */
    public int getNextOrderNumber(int quizId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(order_num), 0) + 1 FROM questions WHERE quiz_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 1; // Default to 1 if no questions exist
    }
    
    /**
     * Get questions in random order for a quiz
     * @param quizId The quiz ID
     * @return List of questions in random order
     * @throws SQLException If database error occurs
     */
    public List<Question> getQuestionsRandomOrder(int quizId) throws SQLException {
        String sql = "SELECT id, quiz_id, question_type, question_text, correct_answer, choices_json, image_url, order_num FROM questions WHERE quiz_id = ? ORDER BY RAND()";
        List<Question> questions = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapRowToQuestion(rs));
                }
            }
        }
        return questions;
    }
    
    /**
     * Map a ResultSet row to a Question object
     * @param rs The ResultSet positioned at a valid row
     * @return Question object
     * @throws SQLException If database error occurs
     */
    private Question mapRowToQuestion(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setQuestionId(rs.getInt("id"));
        question.setQuizId(rs.getInt("quiz_id"));
        question.setQuestionType(rs.getString("question_type"));
        question.setQuestionText(rs.getString("question_text"));
        question.setCorrectAnswer(rs.getString("correct_answer"));
        question.setChoicesJson(rs.getString("choices_json"));
        question.setImageUrl(rs.getString("image_url"));
        question.setOrderNum(rs.getInt("order_num"));
        return question;
    }
} 