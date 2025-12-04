package dao;

import model.Achievement;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Achievement entity
 * Provides full CRUD operations and achievement-specific functionality
 */
public class AchievementDAO {
    
    private Connection connection;
    
    // Constructor that takes a database connection
    public AchievementDAO(Connection connection) {
        this.connection = connection;
    }
    
    // ========================= CREATE OPERATIONS =========================
    
    /**
     * Create a new achievement
     * @param achievement Achievement object with all necessary fields
     * @return The created achievement with generated ID, or null if creation failed
     * @throws SQLException If database error occurs
     */
    public Achievement createAchievement(Achievement achievement) throws SQLException {
        String sql = "INSERT INTO achievements (user_id, achievement_type, date_earned, description) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, achievement.getUserId());
            stmt.setString(2, achievement.getAchievementType());
            stmt.setTimestamp(3, new Timestamp(achievement.getDateEarned().getTime()));
            stmt.setString(4, achievement.getDescription());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    achievement.setAchievementId(generatedKeys.getInt(1));
                    return achievement;
                }
            }
        }
        return null;
    }
    
    /**
     * Create a simple achievement
     * @param userId ID of the user earning the achievement
     * @param achievementType Type of achievement
     * @param description Achievement description
     * @return Created achievement with generated ID
     * @throws SQLException If database error occurs
     */
    public Achievement createSimpleAchievement(int userId, String achievementType, String description) throws SQLException {
        Achievement achievement = new Achievement(userId, achievementType, description);
        return createAchievement(achievement);
    }
    
    /**
     * Award an achievement to a user if they don't already have it
     * @param userId ID of the user
     * @param achievementType Type of achievement
     * @return Created achievement or null if user already has this achievement
     * @throws SQLException If database error occurs
     */
    public Achievement awardAchievement(int userId, String achievementType) throws SQLException {
        // Check if user already has this achievement
        if (hasUserEarnedAchievement(userId, achievementType)) {
            return null; // User already has this achievement
        }
        
        // Create achievement with default description
        Achievement achievement = new Achievement(userId, achievementType, "");
        achievement.setDescription(achievement.getDefaultDescription());
        return createAchievement(achievement);
    }
    
    // ========================= READ OPERATIONS =========================
    
    /**
     * Find an achievement by its ID
     * @param achievementId The achievement ID to search for
     * @return Achievement object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public Achievement findById(int achievementId) throws SQLException {
        String sql = "SELECT id, user_id, achievement_type, date_earned, description FROM achievements WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, achievementId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAchievement(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get all achievements by user
     * @param userId The user ID
     * @return List of achievements earned by the user
     * @throws SQLException If database error occurs
     */
    public List<Achievement> getAchievementsByUser(int userId) throws SQLException {
        String sql = "SELECT id, user_id, achievement_type, date_earned, description FROM achievements WHERE user_id = ? ORDER BY date_earned DESC";
        List<Achievement> achievements = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achievements.add(mapRowToAchievement(rs));
                }
            }
        }
        return achievements;
    }
    
    /**
     * Get all achievements of a specific type
     * @param achievementType The achievement type
     * @return List of achievements of the specified type
     * @throws SQLException If database error occurs
     */
    public List<Achievement> getAchievementsByType(String achievementType) throws SQLException {
        String sql = "SELECT id, user_id, achievement_type, date_earned, description FROM achievements WHERE achievement_type = ? ORDER BY date_earned DESC";
        List<Achievement> achievements = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, achievementType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achievements.add(mapRowToAchievement(rs));
                }
            }
        }
        return achievements;
    }
    
    /**
     * Get all achievements with pagination
     * @param offset Starting position (0-based)
     * @param limit Maximum number of achievements to return
     * @return List of achievements
     * @throws SQLException If database error occurs
     */
    public List<Achievement> getAllAchievements(int offset, int limit) throws SQLException {
        String sql = "SELECT id, user_id, achievement_type, date_earned, description FROM achievements ORDER BY date_earned DESC LIMIT ? OFFSET ?";
        List<Achievement> achievements = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achievements.add(mapRowToAchievement(rs));
                }
            }
        }
        return achievements;
    }
    
    /**
     * Get all achievements
     * @return List of all achievements
     * @throws SQLException If database error occurs
     */
    public List<Achievement> getAllAchievements() throws SQLException {
        String sql = "SELECT id, user_id, achievement_type, date_earned, description FROM achievements ORDER BY date_earned DESC";
        List<Achievement> achievements = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achievements.add(mapRowToAchievement(rs));
                }
            }
        }
        return achievements;
    }
    
    /**
     * Get achievements by user with pagination
     * @param userId The user ID
     * @param offset Starting position (0-based)
     * @param limit Maximum number of achievements to return
     * @return List of achievements by the user
     * @throws SQLException If database error occurs
     */
    public List<Achievement> getAchievementsByUser(int userId, int offset, int limit) throws SQLException {
        String sql = "SELECT id, user_id, achievement_type, date_earned, description FROM achievements WHERE user_id = ? ORDER BY date_earned DESC LIMIT ? OFFSET ?";
        List<Achievement> achievements = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achievements.add(mapRowToAchievement(rs));
                }
            }
        }
        return achievements;
    }
    
    /**
     * Get recent achievements in the last N days
     * @param days Number of days to look back
     * @param limit Maximum number of achievements to return
     * @return List of recent achievements
     * @throws SQLException If database error occurs
     */
    public List<Achievement> getRecentAchievements(int days, int limit) throws SQLException {
        String sql = "SELECT id, user_id, achievement_type, date_earned, description FROM achievements WHERE date_earned >= DATE_SUB(NOW(), INTERVAL ? DAY) ORDER BY date_earned DESC LIMIT ?";
        List<Achievement> achievements = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, days);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achievements.add(mapRowToAchievement(rs));
                }
            }
        }
        return achievements;
    }
    
    /**
     * Get users who have earned a specific achievement
     * @param achievementType The achievement type
     * @param limit Maximum number of users to return
     * @return List of user IDs who have earned the achievement
     * @throws SQLException If database error occurs
     */
    public List<Integer> getUsersWithAchievement(String achievementType, int limit) throws SQLException {
        String sql = "SELECT DISTINCT user_id FROM achievements WHERE achievement_type = ? LIMIT ?";
        List<Integer> userIds = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, achievementType);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }
        }
        return userIds;
    }
    
    // ========================= UPDATE OPERATIONS =========================
    
    /**
     * Update an achievement
     * @param achievement Achievement object with updated information
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateAchievement(Achievement achievement) throws SQLException {
        String sql = "UPDATE achievements SET description = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, achievement.getDescription());
            stmt.setInt(2, achievement.getAchievementId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update achievement description
     * @param achievementId The achievement ID
     * @param newDescription New description
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateAchievementDescription(int achievementId, String newDescription) throws SQLException {
        String sql = "UPDATE achievements SET description = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newDescription);
            stmt.setInt(2, achievementId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    // ========================= DELETE OPERATIONS =========================
    
    /**
     * Delete an achievement by ID
     * @param achievementId The achievement ID to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean deleteAchievement(int achievementId) throws SQLException {
        String sql = "DELETE FROM achievements WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, achievementId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete all achievements by a specific user
     * @param userId The user ID
     * @return Number of achievements deleted
     * @throws SQLException If database error occurs
     */
    public int deleteAchievementsByUser(int userId) throws SQLException {
        String sql = "DELETE FROM achievements WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Delete all achievements of a specific type
     * @param achievementType The achievement type
     * @return Number of achievements deleted
     * @throws SQLException If database error occurs
     */
    public int deleteAchievementsByType(String achievementType) throws SQLException {
        String sql = "DELETE FROM achievements WHERE achievement_type = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, achievementType);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Delete a specific achievement for a user
     * @param userId The user ID
     * @param achievementType The achievement type
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean deleteUserAchievement(int userId, String achievementType) throws SQLException {
        String sql = "DELETE FROM achievements WHERE user_id = ? AND achievement_type = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, achievementType);
            return stmt.executeUpdate() > 0;
        }
    }
    
    // ========================= VALIDATION AND UTILITY METHODS =========================
    
    /**
     * Check if an achievement exists
     * @param achievementId The achievement ID to check
     * @return true if achievement exists, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean achievementExists(int achievementId) throws SQLException {
        String sql = "SELECT 1 FROM achievements WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, achievementId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Check if a user has earned a specific achievement
     * @param userId The user ID
     * @param achievementType The achievement type
     * @return true if user has earned the achievement, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean hasUserEarnedAchievement(int userId, String achievementType) throws SQLException {
        String sql = "SELECT 1 FROM achievements WHERE user_id = ? AND achievement_type = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, achievementType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Get the count of achievements by user
     * @param userId The user ID
     * @return Number of achievements earned by the user
     * @throws SQLException If database error occurs
     */
    public int getAchievementCountByUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM achievements WHERE user_id = ?";
        
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
     * Get the count of achievements by type
     * @param achievementType The achievement type
     * @return Number of achievements of the specified type
     * @throws SQLException If database error occurs
     */
    public int getAchievementCountByType(String achievementType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM achievements WHERE achievement_type = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, achievementType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Get total achievement count
     * @return Total number of achievements in the database
     * @throws SQLException If database error occurs
     */
    public int getTotalAchievementCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM achievements";
        
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
     * Get count of recent achievements in the last N days
     * @param days Number of days to look back
     * @return Achievement count
     * @throws SQLException If database error occurs
     */
    public int getRecentAchievementCount(int days) throws SQLException {
        String sql = "SELECT COUNT(*) FROM achievements WHERE date_earned >= DATE_SUB(NOW(), INTERVAL ? DAY)";
        
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
     * Get achievement statistics for a user
     * @param userId The user ID
     * @return Array with [total achievements, recent achievements (7 days)]
     * @throws SQLException If database error occurs
     */
    public int[] getUserAchievementStats(int userId) throws SQLException {
        int total = getAchievementCountByUser(userId);
        
        String sql = "SELECT COUNT(*) FROM achievements WHERE user_id = ? AND date_earned >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
        int recent = 0;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    recent = rs.getInt(1);
                }
            }
        }
        
        return new int[]{total, recent};
    }
    
    /**
     * Get all available achievement types
     * @return List of achievement types that exist in the database
     * @throws SQLException If database error occurs
     */
    public List<String> getAvailableAchievementTypes() throws SQLException {
        String sql = "SELECT DISTINCT achievement_type FROM achievements ORDER BY achievement_type";
        List<String> types = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    types.add(rs.getString("achievement_type"));
                }
            }
        }
        return types;
    }
    
    /**
     * Get the most recent achievement for a user
     * @param userId The user ID
     * @return Most recent achievement, or null if user has no achievements
     * @throws SQLException If database error occurs
     */
    public Achievement getLatestAchievement(int userId) throws SQLException {
        String sql = "SELECT id, user_id, achievement_type, date_earned, description FROM achievements WHERE user_id = ? ORDER BY date_earned DESC LIMIT 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAchievement(rs);
                }
            }
        }
        return null;
    }
    
    // ========================= HELPER METHODS =========================
    
    /**
     * Map a ResultSet row to an Achievement object
     * @param rs The ResultSet positioned at a valid row
     * @return Achievement object
     * @throws SQLException If database error occurs
     */
    private Achievement mapRowToAchievement(ResultSet rs) throws SQLException {
        Achievement achievement = new Achievement();
        achievement.setAchievementId(rs.getInt("id"));
        achievement.setUserId(rs.getInt("user_id"));
        achievement.setAchievementType(rs.getString("achievement_type"));
        achievement.setDescription(rs.getString("description"));
        
        Timestamp dateEarnedTimestamp = rs.getTimestamp("date_earned");
        if (dateEarnedTimestamp != null) {
            achievement.setDateEarned(new Date(dateEarnedTimestamp.getTime()));
        }
        
        return achievement;
    }
} 