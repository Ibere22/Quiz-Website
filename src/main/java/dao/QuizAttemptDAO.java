package dao;

import model.QuizAttempt;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for QuizAttempt entity
 * Provides full CRUD operations and quiz attempt-specific functionality
 */
public class QuizAttemptDAO {
    
    private Connection connection;
    
    // Constructor that takes a database connection
    public QuizAttemptDAO(Connection connection) {
        this.connection = connection;
    }
    
    // ========================= CREATE OPERATIONS =========================
    
    /**
     * Create a new quiz attempt
     * @param quizAttempt QuizAttempt object with all necessary fields
     * @return The created quiz attempt with generated ID, or null if creation failed
     * @throws SQLException If database error occurs
     */
    public QuizAttempt createQuizAttempt(QuizAttempt quizAttempt) throws SQLException {
        String sql = "INSERT INTO quiz_attempts (user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, quizAttempt.getUserId());
            stmt.setInt(2, quizAttempt.getQuizId());
            stmt.setDouble(3, quizAttempt.getScore());
            stmt.setInt(4, quizAttempt.getTotalQuestions());
            stmt.setLong(5, quizAttempt.getTimeTaken());
            stmt.setTimestamp(6, new Timestamp(quizAttempt.getDateTaken().getTime()));
            stmt.setBoolean(7, quizAttempt.isPractice());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    quizAttempt.setAttemptId(generatedKeys.getInt(1));
                    return quizAttempt;
                }
            }
        }
        return null;
    }
    
    /**
     * Create a simple quiz attempt
     * @param userId ID of the user taking the quiz
     * @param quizId ID of the quiz being attempted
     * @param score Score achieved (percentage)
     * @param totalQuestions Total number of questions in the quiz
     * @param timeTaken Time taken in seconds
     * @return Created quiz attempt with generated ID
     * @throws SQLException If database error occurs
     */
    public QuizAttempt createSimpleAttempt(int userId, int quizId, double score, int totalQuestions, long timeTaken) throws SQLException {
        QuizAttempt attempt = new QuizAttempt(userId, quizId, score, totalQuestions, timeTaken, false);
        return createQuizAttempt(attempt);
    }
    
    /**
     * Create a practice quiz attempt
     * @param userId ID of the user taking the quiz
     * @param quizId ID of the quiz being attempted
     * @param score Score achieved (percentage)
     * @param totalQuestions Total number of questions in the quiz
     * @param timeTaken Time taken in seconds
     * @return Created practice quiz attempt with generated ID
     * @throws SQLException If database error occurs
     */
    public QuizAttempt createPracticeAttempt(int userId, int quizId, double score, int totalQuestions, long timeTaken) throws SQLException {
        QuizAttempt attempt = new QuizAttempt(userId, quizId, score, totalQuestions, timeTaken, true);
        return createQuizAttempt(attempt);
    }
    
    // ========================= READ OPERATIONS =========================
    
    /**
     * Find a quiz attempt by its ID
     * @param attemptId The attempt ID to search for
     * @return QuizAttempt object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public QuizAttempt findById(int attemptId) throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToQuizAttempt(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get all attempts by user
     * @param userId The user ID
     * @return List of quiz attempts by the user
     * @throws SQLException If database error occurs
     */
    public List<QuizAttempt> getAttemptsByUser(int userId) throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts WHERE user_id = ? ORDER BY date_taken DESC";
        List<QuizAttempt> attempts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(mapRowToQuizAttempt(rs));
                }
            }
        }
        return attempts;
    }
    
    /**
     * Get all attempts for a specific quiz
     * @param quizId The quiz ID
     * @return List of quiz attempts for the quiz
     * @throws SQLException If database error occurs
     */
    public List<QuizAttempt> getAttemptsByQuiz(int quizId) throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts WHERE quiz_id = ? ORDER BY date_taken DESC";
        List<QuizAttempt> attempts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(mapRowToQuizAttempt(rs));
                }
            }
        }
        return attempts;
    }
    
    /**
     * Get attempts by user for a specific quiz
     * @param userId The user ID
     * @param quizId The quiz ID
     * @return List of attempts by the user for the specific quiz
     * @throws SQLException If database error occurs
     */
    public List<QuizAttempt> getAttemptsByUserAndQuiz(int userId, int quizId) throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts WHERE user_id = ? AND quiz_id = ? ORDER BY date_taken DESC";
        List<QuizAttempt> attempts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(mapRowToQuizAttempt(rs));
                }
            }
        }
        return attempts;
    }
    
    /**
     * Get all quiz attempts with pagination
     * @param offset Starting position (0-based)
     * @param limit Maximum number of attempts to return
     * @return List of quiz attempts
     * @throws SQLException If database error occurs
     */
    public List<QuizAttempt> getAllAttempts(int offset, int limit) throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts ORDER BY date_taken DESC LIMIT ? OFFSET ?";
        List<QuizAttempt> attempts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(mapRowToQuizAttempt(rs));
                }
            }
        }
        return attempts;
    }
    
    /**
     * Get all quiz attempts
     * @return List of all quiz attempts
     * @throws SQLException If database error occurs
     */
    public List<QuizAttempt> getAllAttempts() throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts ORDER BY date_taken DESC";
        List<QuizAttempt> attempts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(mapRowToQuizAttempt(rs));
                }
            }
        }
        return attempts;
    }
    
    /**
     * Get practice attempts only
     * @return List of practice quiz attempts
     * @throws SQLException If database error occurs
     */
    public List<QuizAttempt> getPracticeAttempts() throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts WHERE is_practice = TRUE ORDER BY date_taken DESC";
        List<QuizAttempt> attempts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(mapRowToQuizAttempt(rs));
                }
            }
        }
        return attempts;
    }
    
    /**
     * Get non-practice (graded) attempts only
     * @return List of graded quiz attempts
     * @throws SQLException If database error occurs
     */
    public List<QuizAttempt> getGradedAttempts() throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts WHERE is_practice = FALSE ORDER BY date_taken DESC";
        List<QuizAttempt> attempts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(mapRowToQuizAttempt(rs));
                }
            }
        }
        return attempts;
    }
    
    /**
     * Get attempts by user with pagination
     * @param userId The user ID
     * @param offset Starting position (0-based)
     * @param limit Maximum number of attempts to return
     * @return List of quiz attempts by the user
     * @throws SQLException If database error occurs
     */
    public List<QuizAttempt> getAttemptsByUser(int userId, int offset, int limit) throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts WHERE user_id = ? ORDER BY date_taken DESC LIMIT ? OFFSET ?";
        List<QuizAttempt> attempts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(mapRowToQuizAttempt(rs));
                }
            }
        }
        return attempts;
    }
    
    /**
     * Get top scores for a quiz
     * @param quizId The quiz ID
     * @param limit Maximum number of top scores to return
     * @param practiceOnly Whether to include only practice attempts
     * @return List of top scoring attempts for the quiz
     * @throws SQLException If database error occurs
     */
    public List<QuizAttempt> getTopScoresForQuiz(int quizId, int limit, boolean practiceOnly) throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts WHERE quiz_id = ?";
        if (practiceOnly) {
            sql += " AND is_practice = TRUE";
        } else {
            sql += " AND is_practice = FALSE";
        }
        sql += " ORDER BY score DESC, time_taken ASC LIMIT ?";
        
        List<QuizAttempt> attempts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(mapRowToQuizAttempt(rs));
                }
            }
        }
        return attempts;
    }
    
    /**
     * Get top scores for a quiz in the last day
     * @param quizId The quiz ID
     * @param limit Maximum number of results
     * @param practiceOnly Whether to include only practice attempts
     * @return List of top quiz attempts in the last day
     * @throws SQLException If database error occurs
     */
    public List<QuizAttempt> getTopScoresForQuizInLastDay(int quizId, int limit, boolean practiceOnly) throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice " +
                     "FROM quiz_attempts WHERE quiz_id = ? AND date_taken >= DATE_SUB(NOW(), INTERVAL 1 DAY) ";
        if (practiceOnly) {
            sql += " AND is_practice = TRUE";
        } else {
            sql += " AND is_practice = FALSE";
        }
        sql += " ORDER BY score DESC, time_taken ASC LIMIT ?";
        List<QuizAttempt> attempts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(mapRowToQuizAttempt(rs));
                }
            }
        }
        return attempts;
    }
    
    /**
     * Get recent attempts in the last N days
     * @param days Number of days to look back
     * @param limit Maximum number of attempts to return
     * @return List of recent attempts
     * @throws SQLException If database error occurs
     */
    public List<QuizAttempt> getRecentAttempts(int days, int limit) throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts WHERE date_taken >= DATE_SUB(NOW(), INTERVAL ? DAY) ORDER BY date_taken DESC LIMIT ?";
        List<QuizAttempt> attempts = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, days);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(mapRowToQuizAttempt(rs));
                }
            }
        }
        return attempts;
    }
    
    /**
     * Get the most recent quiz attempts for a user
     * @param userId The user ID
     * @param limit Maximum number of attempts to return
     * @return List of recent quiz attempts
     * @throws SQLException If database error occurs
     */
    public List<QuizAttempt> getRecentAttemptsForUser(int userId, int limit) throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts WHERE user_id = ? ORDER BY date_taken DESC LIMIT ?";
        List<QuizAttempt> attempts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attempts.add(mapRowToQuizAttempt(rs));
                }
            }
        }
        return attempts;
    }
    
    // ========================= UPDATE OPERATIONS =========================
    
    /**
     * Update a quiz attempt
     * @param quizAttempt QuizAttempt object with updated information
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateQuizAttempt(QuizAttempt quizAttempt) throws SQLException {
        String sql = "UPDATE quiz_attempts SET score = ?, total_questions = ?, time_taken = ?, is_practice = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, quizAttempt.getScore());
            stmt.setInt(2, quizAttempt.getTotalQuestions());
            stmt.setLong(3, quizAttempt.getTimeTaken());
            stmt.setBoolean(4, quizAttempt.isPractice());
            stmt.setInt(5, quizAttempt.getAttemptId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update attempt score
     * @param attemptId The attempt ID
     * @param newScore New score
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateAttemptScore(int attemptId, double newScore) throws SQLException {
        String sql = "UPDATE quiz_attempts SET score = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, newScore);
            stmt.setInt(2, attemptId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update attempt time taken
     * @param attemptId The attempt ID
     * @param timeTaken New time taken in seconds
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateAttemptTime(int attemptId, long timeTaken) throws SQLException {
        String sql = "UPDATE quiz_attempts SET time_taken = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, timeTaken);
            stmt.setInt(2, attemptId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Toggle practice mode for an attempt
     * @param attemptId The attempt ID
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean togglePracticeMode(int attemptId) throws SQLException {
        String sql = "UPDATE quiz_attempts SET is_practice = NOT is_practice WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    // ========================= DELETE OPERATIONS =========================
    
    /**
     * Delete a quiz attempt by ID
     * @param attemptId The attempt ID to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean deleteAttempt(int attemptId) throws SQLException {
        String sql = "DELETE FROM quiz_attempts WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete all attempts by a specific user
     * @param userId The user ID
     * @return Number of attempts deleted
     * @throws SQLException If database error occurs
     */
    public int deleteAttemptsByUser(int userId) throws SQLException {
        String sql = "DELETE FROM quiz_attempts WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Delete all attempts for a specific quiz
     * @param quizId The quiz ID
     * @return Number of attempts deleted
     * @throws SQLException If database error occurs
     */
    public int deleteAttemptsByQuiz(int quizId) throws SQLException {
        String sql = "DELETE FROM quiz_attempts WHERE quiz_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Delete practice attempts by user
     * @param userId The user ID
     * @return Number of practice attempts deleted
     * @throws SQLException If database error occurs
     */
    public int deletePracticeAttemptsByUser(int userId) throws SQLException {
        String sql = "DELETE FROM quiz_attempts WHERE user_id = ? AND is_practice = TRUE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate();
        }
    }
    
    // ========================= VALIDATION AND UTILITY METHODS =========================
    
    /**
     * Check if an attempt exists
     * @param attemptId The attempt ID to check
     * @return true if attempt exists, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean attemptExists(int attemptId) throws SQLException {
        String sql = "SELECT 1 FROM quiz_attempts WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Check if a user has attempted a specific quiz
     * @param userId The user ID
     * @param quizId The quiz ID
     * @return true if user has attempted the quiz, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean hasUserAttemptedQuiz(int userId, int quizId) throws SQLException {
        String sql = "SELECT 1 FROM quiz_attempts WHERE user_id = ? AND quiz_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Get the count of attempts by user
     * @param userId The user ID
     * @return Number of attempts by the user
     * @throws SQLException If database error occurs
     */
    public int getAttemptCountByUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Get the count of attempts for a quiz
     * @param quizId The quiz ID
     * @return Number of attempts for the quiz
     * @throws SQLException If database error occurs
     */
    public int getAttemptCountByQuiz(int quizId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE quiz_id = ?";
        
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
     * Get total attempt count
     * @return Total number of attempts in the database
     * @throws SQLException If database error occurs
     */
    public int getTotalAttemptCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM quiz_attempts";
        
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
     * Get count of practice attempts
     * @return Number of practice attempts
     * @throws SQLException If database error occurs
     */
    public int getPracticeAttemptCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE is_practice = TRUE";
        
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
     * Get count of graded (non-practice) attempts
     * @return Number of graded attempts
     * @throws SQLException If database error occurs
     */
    public int getGradedAttemptCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE is_practice = FALSE";
        
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
     * Get count of recent attempts in the last N days
     * @param days Number of days to look back
     * @return Attempt count
     * @throws SQLException If database error occurs
     */
    public int getRecentAttemptCount(int days) throws SQLException {
        String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE date_taken >= DATE_SUB(NOW(), INTERVAL ? DAY)";
        
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
     * Get best score for a user on a specific quiz
     * @param userId The user ID
     * @param quizId The quiz ID
     * @param practiceOnly Whether to consider only practice attempts
     * @return Best score, or -1 if no attempts found
     * @throws SQLException If database error occurs
     */
    public double getBestScore(int userId, int quizId, boolean practiceOnly) throws SQLException {
        String sql = "SELECT MAX(score) FROM quiz_attempts WHERE user_id = ? AND quiz_id = ?";
        if (practiceOnly) {
            sql += " AND is_practice = TRUE";
        } else {
            sql += " AND is_practice = FALSE";
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double score = rs.getDouble(1);
                    return rs.wasNull() ? -1 : score;
                }
            }
        }
        return -1;
    }
    
    /**
     * Get average score for a user
     * @param userId The user ID
     * @param practiceOnly Whether to consider only practice attempts
     * @return Average score, or -1 if no attempts found
     * @throws SQLException If database error occurs
     */
    public double getAverageScore(int userId, boolean practiceOnly) throws SQLException {
        String sql = "SELECT AVG(score) FROM quiz_attempts WHERE user_id = ?";
        if (practiceOnly) {
            sql += " AND is_practice = TRUE";
        } else {
            sql += " AND is_practice = FALSE";
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double score = rs.getDouble(1);
                    return rs.wasNull() ? -1 : score;
                }
            }
        }
        return -1;
    }
    
    /**
     * Get average score for a quiz
     * @param quizId The quiz ID
     * @param practiceOnly Whether to consider only practice attempts
     * @return Average score for the quiz, or -1 if no attempts found
     * @throws SQLException If database error occurs
     */
    public double getQuizAverageScore(int quizId, boolean practiceOnly) throws SQLException {
        String sql = "SELECT AVG(score) FROM quiz_attempts WHERE quiz_id = ?";
        if (practiceOnly) {
            sql += " AND is_practice = TRUE";
        } else {
            sql += " AND is_practice = FALSE";
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double score = rs.getDouble(1);
                    return rs.wasNull() ? -1 : score;
                }
            }
        }
        return -1;
    }
    
    /**
     * Get best attempt by user for a specific quiz
     * @param userId The user ID
     * @param quizId The quiz ID
     * @param practiceOnly Whether to consider only practice attempts
     * @return Best attempt, or null if no attempts found
     * @throws SQLException If database error occurs
     */
    public QuizAttempt getBestAttempt(int userId, int quizId, boolean practiceOnly) throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, time_taken, date_taken, is_practice FROM quiz_attempts WHERE user_id = ? AND quiz_id = ?";
        if (practiceOnly) {
            sql += " AND is_practice = TRUE";
        } else {
            sql += " AND is_practice = FALSE";
        }
        sql += " ORDER BY score DESC, time_taken ASC LIMIT 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToQuizAttempt(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get leaderboard data: for each quiz, the best score per user (one entry per user per quiz)
     * @return List of LeaderboardEntry objects
     * @throws SQLException If database error occurs
     */
    public List<model.LeaderboardEntry> getLeaderboardData() throws SQLException {
        String sql = "SELECT q.id AS quiz_id, q.title AS quiz_title, u.id AS user_id, u.username, MAX(qa.score) AS best_score " +
                "FROM quiz_attempts qa " +
                "JOIN users u ON qa.user_id = u.id " +
                "JOIN quizzes q ON qa.quiz_id = q.id " +
                "WHERE qa.is_practice = FALSE " +
                "GROUP BY q.id, u.id, q.title, u.username " +
                "ORDER BY best_score DESC";
        List<model.LeaderboardEntry> leaderboard = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int quizId = rs.getInt("quiz_id");
                    String quizTitle = rs.getString("quiz_title");
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    double bestScore = rs.getDouble("best_score");
                    leaderboard.add(new model.LeaderboardEntry(quizId, quizTitle, userId, username, bestScore, 0, null));
                }
            }
        }
        return leaderboard;
    }

    /**
     * Delete all quiz attempts (admin cleanup function)
     * @return Number of attempts deleted
     * @throws SQLException If database error occurs
     */
    public int deleteAllAttempts() throws SQLException {
        String sql = "DELETE FROM quiz_attempts";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            return stmt.executeUpdate();
        }
    }

    // ========================= HELPER METHODS =========================
    
    /**
     * Map a ResultSet row to a QuizAttempt object
     * @param rs The ResultSet positioned at a valid row
     * @return QuizAttempt object
     * @throws SQLException If database error occurs
     */
    private QuizAttempt mapRowToQuizAttempt(ResultSet rs) throws SQLException {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setAttemptId(rs.getInt("id"));
        attempt.setUserId(rs.getInt("user_id"));
        attempt.setQuizId(rs.getInt("quiz_id"));
        attempt.setScore(rs.getDouble("score"));
        attempt.setTotalQuestions(rs.getInt("total_questions"));
        attempt.setTimeTaken(rs.getLong("time_taken"));
        attempt.setPractice(rs.getBoolean("is_practice"));
        
        Timestamp dateTakenTimestamp = rs.getTimestamp("date_taken");
        if (dateTakenTimestamp != null) {
            attempt.setDateTaken(new Date(dateTakenTimestamp.getTime()));
        }
        
        return attempt;
    }
} 