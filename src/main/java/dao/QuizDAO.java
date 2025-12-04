package dao;

import model.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Quiz entity
 * Provides full CRUD operations and quiz-specific functionality
 */
public class QuizDAO {
    
    private Connection connection;
    
    // Constructor that takes a database connection
    public QuizDAO(Connection connection) {
        this.connection = connection;
    }
    
    // ========================= CREATE OPERATIONS =========================
    
    /**
     * Create a new quiz
     * @param quiz Quiz object with all necessary fields
     * @return The created quiz with generated ID, or null if creation failed
     * @throws SQLException If database error occurs
     */
    public Quiz createQuiz(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (title, description, creator_id, random_order, one_page, immediate_correction, practice_mode, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, quiz.getTitle());
            stmt.setString(2, quiz.getDescription());
            stmt.setInt(3, quiz.getCreatorId());
            stmt.setBoolean(4, quiz.isRandomOrder());
            stmt.setBoolean(5, quiz.isOnePage());
            stmt.setBoolean(6, quiz.isImmediateCorrection());
            stmt.setBoolean(7, quiz.isPracticeMode());
            stmt.setTimestamp(8, new Timestamp(quiz.getCreatedDate().getTime()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    quiz.setQuizId(generatedKeys.getInt(1));
                    return quiz;
                }
            }
        }
        return null;
    }
    
    /**
     * Create a simple quiz with basic information
     * @param title Quiz title
     * @param description Quiz description
     * @param creatorId ID of the quiz creator
     * @return Created quiz with generated ID
     * @throws SQLException If database error occurs
     */
    public Quiz createSimpleQuiz(String title, String description, int creatorId) throws SQLException {
        Quiz quiz = new Quiz(title, description, creatorId);
        return createQuiz(quiz);
    }
    
    /**
     * Create a quiz with custom settings
     * @param title Quiz title
     * @param description Quiz description
     * @param creatorId ID of the quiz creator
     * @param randomOrder Whether questions should be in random order
     * @param onePage Whether quiz should be displayed on one page
     * @param immediateCorrection Whether to show immediate correction
     * @param practiceMode Whether this is a practice quiz
     * @return Created quiz with generated ID
     * @throws SQLException If database error occurs
     */
    public Quiz createCustomQuiz(String title, String description, int creatorId, 
                               boolean randomOrder, boolean onePage, boolean immediateCorrection, boolean practiceMode) throws SQLException {
        Quiz quiz = new Quiz(title, description, creatorId);
        quiz.setRandomOrder(randomOrder);
        quiz.setOnePage(onePage);
        quiz.setImmediateCorrection(immediateCorrection);
        quiz.setPracticeMode(practiceMode);
        return createQuiz(quiz);
    }
    
    // ========================= READ OPERATIONS =========================
    
    /**
     * Find a quiz by its ID
     * @param quizId The quiz ID to search for
     * @return Quiz object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public Quiz findById(int quizId) throws SQLException {
        String sql = "SELECT id, title, description, creator_id, random_order, one_page, immediate_correction, practice_mode, created_date FROM quizzes WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToQuiz(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find a quiz by its unique title
     * @param title The quiz title to search for
     * @return Quiz object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public Quiz findByTitle(String title) throws SQLException {
        String sql = "SELECT * FROM quizzes WHERE title = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToQuiz(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get all quizzes by creator
     * @param creatorId The creator user ID
     * @return List of quizzes created by the user
     * @throws SQLException If database error occurs
     */
    public List<Quiz> getQuizzesByCreator(int creatorId) throws SQLException {
        String sql = "SELECT id, title, description, creator_id, random_order, one_page, immediate_correction, practice_mode, created_date FROM quizzes WHERE creator_id = ? ORDER BY created_date DESC";
        List<Quiz> quizzes = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, creatorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(mapRowToQuiz(rs));
                }
            }
        }
        return quizzes;
    }
    
    /**
     * Get all quizzes with pagination
     * @param offset Starting position (0-based)
     * @param limit Maximum number of quizzes to return
     * @return List of quizzes
     * @throws SQLException If database error occurs
     */
    public List<Quiz> getAllQuizzes(int offset, int limit) throws SQLException {
        String sql = "SELECT id, title, description, creator_id, random_order, one_page, immediate_correction, practice_mode, created_date FROM quizzes ORDER BY created_date DESC LIMIT ? OFFSET ?";
        List<Quiz> quizzes = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(mapRowToQuiz(rs));
                }
            }
        }
        return quizzes;
    }
    
    /**
     * Get all quizzes
     * @return List of all quizzes
     * @throws SQLException If database error occurs
     */
    public List<Quiz> getAllQuizzes() throws SQLException {
        String sql = "SELECT id, title, description, creator_id, random_order, one_page, immediate_correction, practice_mode, created_date FROM quizzes ORDER BY created_date DESC";
        List<Quiz> quizzes = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(mapRowToQuiz(rs));
                }
            }
        }
        return quizzes;
    }
    
    /**
     * Search quizzes by title
     * @param searchTerm Search term to match against quiz title
     * @return List of quizzes containing the search term in title
     * @throws SQLException If database error occurs
     */
    public List<Quiz> searchQuizzesByTitle(String searchTerm) throws SQLException {
        String sql = "SELECT id, title, description, creator_id, random_order, one_page, immediate_correction, practice_mode, created_date FROM quizzes WHERE title LIKE ? ORDER BY created_date DESC";
        List<Quiz> quizzes = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(mapRowToQuiz(rs));
                }
            }
        }
        return quizzes;
    }
    
    /**
     * Search quizzes by title or description
     * @param searchTerm Search term to match against quiz title or description
     * @return List of quizzes containing the search term
     * @throws SQLException If database error occurs
     */
    public List<Quiz> searchQuizzes(String searchTerm) throws SQLException {
        String sql = "SELECT id, title, description, creator_id, random_order, one_page, immediate_correction, practice_mode, created_date FROM quizzes WHERE title LIKE ? OR description LIKE ? ORDER BY created_date DESC";
        List<Quiz> quizzes = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(mapRowToQuiz(rs));
                }
            }
        }
        return quizzes;
    }
    
    /**
     * Get practice mode quizzes
     * @return List of quizzes in practice mode
     * @throws SQLException If database error occurs
     */
    public List<Quiz> getPracticeQuizzes() throws SQLException {
        String sql = "SELECT id, title, description, creator_id, random_order, one_page, immediate_correction, practice_mode, created_date FROM quizzes WHERE practice_mode = TRUE ORDER BY created_date DESC";
        List<Quiz> quizzes = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(mapRowToQuiz(rs));
                }
            }
        }
        return quizzes;
    }
    
    /**
     * Get quizzes by creator with pagination
     * @param creatorId The creator user ID
     * @param offset Starting position (0-based)
     * @param limit Maximum number of quizzes to return
     * @return List of quizzes created by the user
     * @throws SQLException If database error occurs
     */
    public List<Quiz> getQuizzesByCreator(int creatorId, int offset, int limit) throws SQLException {
        String sql = "SELECT id, title, description, creator_id, random_order, one_page, immediate_correction, practice_mode, created_date FROM quizzes WHERE creator_id = ? ORDER BY created_date DESC LIMIT ? OFFSET ?";
        List<Quiz> quizzes = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, creatorId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(mapRowToQuiz(rs));
                }
            }
        }
        return quizzes;
    }
    
    /**
     * Get recently created quizzes
     * @param days Number of days to look back
     * @param limit Maximum number of quizzes to return
     * @return List of recently created quizzes
     * @throws SQLException If database error occurs
     */
    public List<Quiz> getRecentQuizzes(int days, int limit) throws SQLException {
        String sql = "SELECT id, title, description, creator_id, random_order, one_page, immediate_correction, practice_mode, created_date FROM quizzes WHERE created_date >= DATE_SUB(NOW(), INTERVAL ? DAY) ORDER BY created_date DESC LIMIT ?";
        List<Quiz> quizzes = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, days);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(mapRowToQuiz(rs));
                }
            }
        }
        return quizzes;
    }
    
    /**
     * Get the most popular quizzes by number of attempts
     * @param limit Maximum number of quizzes to return
     * @return List of popular quizzes
     * @throws SQLException If database error occurs
     */
    public List<Quiz> getPopularQuizzes(int limit) throws SQLException {
        String sql = "SELECT q.id, q.title, q.description, q.creator_id, q.random_order, q.one_page, q.immediate_correction, q.practice_mode, q.created_date " +
                     "FROM quizzes q " +
                     "LEFT JOIN quiz_attempts a ON q.id = a.quiz_id " +
                     "GROUP BY q.id " +
                     "ORDER BY COUNT(a.id) DESC, q.created_date DESC " +
                     "LIMIT ?";
        List<Quiz> quizzes = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(mapRowToQuiz(rs));
                }
            }
        }
        return quizzes;
    }
    
    // ========================= UPDATE OPERATIONS =========================
    
    /**
     * Update a quiz
     * @param quiz Quiz object with updated information
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateQuiz(Quiz quiz) throws SQLException {
        String sql = "UPDATE quizzes SET title = ?, description = ?, random_order = ?, one_page = ?, immediate_correction = ?, practice_mode = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, quiz.getTitle());
            stmt.setString(2, quiz.getDescription());
            stmt.setBoolean(3, quiz.isRandomOrder());
            stmt.setBoolean(4, quiz.isOnePage());
            stmt.setBoolean(5, quiz.isImmediateCorrection());
            stmt.setBoolean(6, quiz.isPracticeMode());
            stmt.setInt(7, quiz.getQuizId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update quiz title
     * @param quizId The quiz ID
     * @param newTitle New title
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateQuizTitle(int quizId, String newTitle) throws SQLException {
        String sql = "UPDATE quizzes SET title = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newTitle);
            stmt.setInt(2, quizId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update quiz description
     * @param quizId The quiz ID
     * @param newDescription New description
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateQuizDescription(int quizId, String newDescription) throws SQLException {
        String sql = "UPDATE quizzes SET description = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newDescription);
            stmt.setInt(2, quizId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update quiz settings
     * @param quizId The quiz ID
     * @param randomOrder Whether questions should be in random order
     * @param onePage Whether quiz should be displayed on one page
     * @param immediateCorrection Whether to show immediate correction
     * @param practiceMode Whether this is a practice quiz
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateQuizSettings(int quizId, boolean randomOrder, boolean onePage, 
                                    boolean immediateCorrection, boolean practiceMode) throws SQLException {
        String sql = "UPDATE quizzes SET random_order = ?, one_page = ?, immediate_correction = ?, practice_mode = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, randomOrder);
            stmt.setBoolean(2, onePage);
            stmt.setBoolean(3, immediateCorrection);
            stmt.setBoolean(4, practiceMode);
            stmt.setInt(5, quizId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Toggle practice mode for a quiz
     * @param quizId The quiz ID
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean togglePracticeMode(int quizId) throws SQLException {
        String sql = "UPDATE quizzes SET practice_mode = NOT practice_mode WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    // ========================= DELETE OPERATIONS =========================
    
    /**
     * Delete a quiz by ID
     * @param quizId The quiz ID to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean deleteQuiz(int quizId) throws SQLException {
        String sql = "DELETE FROM quizzes WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete all quizzes by a specific creator
     * @param creatorId The creator user ID
     * @return Number of quizzes deleted
     * @throws SQLException If database error occurs
     */
    public int deleteQuizzesByCreator(int creatorId) throws SQLException {
        String sql = "DELETE FROM quizzes WHERE creator_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, creatorId);
            return stmt.executeUpdate();
        }
    }
    
    // ========================= VALIDATION AND UTILITY METHODS =========================
    
    /**
     * Check if a quiz exists
     * @param quizId The quiz ID to check
     * @return true if quiz exists, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean quizExists(int quizId) throws SQLException {
        String sql = "SELECT 1 FROM quizzes WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Check if a user is the creator of a quiz
     * @param quizId The quiz ID
     * @param userId The user ID
     * @return true if user is the creator, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean isQuizCreator(int quizId, int userId) throws SQLException {
        String sql = "SELECT 1 FROM quizzes WHERE id = ? AND creator_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            stmt.setInt(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Get the count of quizzes by creator
     * @param creatorId The creator user ID
     * @return Number of quizzes created by the user
     * @throws SQLException If database error occurs
     */
    public int getQuizCountByCreator(int creatorId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM quizzes WHERE creator_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, creatorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Get total quiz count
     * @return Total number of quizzes in the database
     * @throws SQLException If database error occurs
     */
    public int getTotalQuizCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM quizzes";
        
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
     * Get count of practice mode quizzes
     * @return Number of practice mode quizzes
     * @throws SQLException If database error occurs
     */
    public int getPracticeQuizCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM quizzes WHERE practice_mode = TRUE";
        
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
     * Get count of quizzes created in the last N days
     * @param days Number of days to look back
     * @return Quiz count
     * @throws SQLException If database error occurs
     */
    public int getRecentQuizCount(int days) throws SQLException {
        String sql = "SELECT COUNT(*) FROM quizzes WHERE created_date >= DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, days);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Get quiz with question count
     * @param quizId The quiz ID
     * @return Quiz object with additional information, or null if not found
     * @throws SQLException If database error occurs
     */
    public Quiz getQuizWithQuestionCount(int quizId) throws SQLException {
        String sql = "SELECT q.id, q.title, q.description, q.creator_id, q.random_order, q.one_page, " +
                     "q.immediate_correction, q.practice_mode, q.created_date, " +
                     "COUNT(qt.id) as question_count " +
                     "FROM quizzes q LEFT JOIN questions qt ON q.id = qt.quiz_id " +
                     "WHERE q.id = ? GROUP BY q.id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Quiz quiz = mapRowToQuiz(rs);
                    // Note: You might want to add a questionCount field to Quiz model if needed
                    return quiz;
                }
            }
        }
        return null;
    }
    
    // ========================= HELPER METHODS =========================
    
    /**
     * Map a ResultSet row to a Quiz object
     * @param rs The ResultSet positioned at a valid row
     * @return Quiz object
     * @throws SQLException If database error occurs
     */
    private Quiz mapRowToQuiz(ResultSet rs) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setQuizId(rs.getInt("id"));
        quiz.setTitle(rs.getString("title"));
        quiz.setDescription(rs.getString("description"));
        quiz.setCreatorId(rs.getInt("creator_id"));
        quiz.setRandomOrder(rs.getBoolean("random_order"));
        quiz.setOnePage(rs.getBoolean("one_page"));
        quiz.setImmediateCorrection(rs.getBoolean("immediate_correction"));
        quiz.setPracticeMode(rs.getBoolean("practice_mode"));
        
        Timestamp createdTimestamp = rs.getTimestamp("created_date");
        if (createdTimestamp != null) {
            quiz.setCreatedDate(new Date(createdTimestamp.getTime()));
        }
        
        return quiz;
    }
} 