package dao;

import model.User;
import util.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for User entity
 * Provides full CRUD operations and user-specific functionality
 */
public class UserDAO {
    
    private Connection connection;
    
    // Constructor that takes a database connection
    public UserDAO(Connection connection) {
        this.connection = connection;
    }
    
    // ========================= CREATE OPERATIONS =========================
    
    /**
     * Create a new user account
     * @param user User object with username, email, and password hash
     * @return The created user with generated ID, or null if creation failed
     * @throws SQLException If database error occurs
     */
    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, email, created_date, is_admin) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setTimestamp(4, new Timestamp(user.getCreatedDate().getTime()));
            stmt.setBoolean(5, user.isAdmin());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                    return user;
                }
            }
        }
        return null;
    }
    
    /**
     * Register a new user with automatic password hashing
     * @param username The username
     * @param plainPassword The plain text password
     * @param email The email address
     * @return The created user, or null if registration failed
     * @throws SQLException If database error occurs
     */
    public User registerUser(String username, String plainPassword, String email) throws SQLException {
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);
        User user = new User(username, hashedPassword, email);
        return createUser(user);
    }
    
    // ========================= READ OPERATIONS =========================
    
    /**
     * Find user by ID
     * @param userId The user ID
     * @return User object or null if not found
     * @throws SQLException If database error occurs
     */
    public User findById(int userId) throws SQLException {
        String sql = "SELECT id, username, password_hash, email, created_date, is_admin FROM users WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find user by username
     * @param username The username
     * @return User object or null if not found
     * @throws SQLException If database error occurs
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, email, created_date, is_admin FROM users WHERE username = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find user by email
     * @param email The email address
     * @return User object or null if not found
     * @throws SQLException If database error occurs
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, username, password_hash, email, created_date, is_admin FROM users WHERE email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get all users with pagination
     * @param offset Starting position (0-based)
     * @param limit Maximum number of users to return
     * @return List of users
     * @throws SQLException If database error occurs
     */
    public List<User> getAllUsers(int offset, int limit) throws SQLException {
        String sql = "SELECT id, username, password_hash, email, created_date, is_admin FROM users ORDER BY username LIMIT ? OFFSET ?";
        List<User> users = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }
        }
        return users;
    }
    
    /**
     * Get all users (use with caution for large datasets)
     * @return List of all users
     * @throws SQLException If database error occurs
     */
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT id, username, password_hash, email, created_date, is_admin FROM users ORDER BY username";
        List<User> users = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }
        }
        return users;
    }
    
    /**
     * Search users by username pattern
     * @param usernamePattern Pattern to search for (use % for wildcards)
     * @return List of matching users
     * @throws SQLException If database error occurs
     */
    public List<User> searchByUsername(String usernamePattern) throws SQLException {
        String sql = "SELECT id, username, password_hash, email, created_date, is_admin FROM users WHERE username LIKE ? ORDER BY username";
        List<User> users = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usernamePattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }
        }
        return users;
    }
    
    /**
     * Get all admin users
     * @return List of admin users
     * @throws SQLException If database error occurs
     */
    public List<User> getAdminUsers() throws SQLException {
        String sql = "SELECT id, username, password_hash, email, created_date, is_admin FROM users WHERE is_admin = TRUE ORDER BY username";
        List<User> users = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }
        }
        return users;
    }
    
    /**
     * Get recently registered users
     * @param days Number of days to look back
     * @param limit Maximum number of users to return
     * @return List of recently registered users
     * @throws SQLException If database error occurs
     */
    public List<User> getRecentlyRegistered(int days, int limit) throws SQLException {
        String sql = "SELECT id, username, password_hash, email, created_date, is_admin FROM users " +
                     "WHERE created_date >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                     "ORDER BY created_date DESC LIMIT ?";
        List<User> users = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, days);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }
        }
        return users;
    }
    
    // ========================= UPDATE OPERATIONS =========================
    
    /**
     * Update user information
     * @param user User object with updated information
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password_hash = ?, email = ?, is_admin = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setBoolean(4, user.isAdmin());
            stmt.setInt(5, user.getUserId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update user's password
     * @param userId The user ID
     * @param newPassword The new plain text password
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        String hashedPassword = PasswordHasher.hashPassword(newPassword);
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update user's email
     * @param userId The user ID
     * @param newEmail The new email address
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateEmail(int userId, String newEmail) throws SQLException {
        String sql = "UPDATE users SET email = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newEmail);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Update user's username
     * @param userId The user ID
     * @param newUsername The new username
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateUsername(int userId, String newUsername) throws SQLException {
        String sql = "UPDATE users SET username = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newUsername);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Grant or revoke admin privileges
     * @param userId The user ID
     * @param isAdmin true to grant admin privileges, false to revoke
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean setAdminStatus(int userId, boolean isAdmin) throws SQLException {
        String sql = "UPDATE users SET is_admin = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isAdmin);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    // ========================= DELETE OPERATIONS =========================
    
    /**
     * Delete a user by ID
     * @param userId The user ID
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete a user by username
     * @param username The username
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean deleteUserByUsername(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    // ========================= AUTHENTICATION OPERATIONS =========================
    
    /**
     * Authenticate a user with username and password
     * @param username The username
     * @param plainPassword The plain text password
     * @return User object if authentication successful, null otherwise
     * @throws SQLException If database error occurs
     */
    public User authenticateUser(String username, String plainPassword) throws SQLException {
        User user = findByUsername(username);
        if (user != null && PasswordHasher.verifyPassword(plainPassword, user.getPasswordHash())) {
            return user;
        }
        return null;
    }
    
    /**
     * Authenticate a user with email and password
     * @param email The email address
     * @param plainPassword The plain text password
     * @return User object if authentication successful, null otherwise
     * @throws SQLException If database error occurs
     */
    public User authenticateUserByEmail(String email, String plainPassword) throws SQLException {
        User user = findByEmail(email);
        if (user != null && PasswordHasher.verifyPassword(plainPassword, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    
    // ========================= VALIDATION OPERATIONS =========================
    
    /**
     * Check if username is available
     * @param username The username to check
     * @return true if username is available, false if taken
     * @throws SQLException If database error occurs
     */
    public boolean isUsernameAvailable(String username) throws SQLException {
        return findByUsername(username) == null;
    }
    
    /**
     * Check if email is available
     * @param email The email to check
     * @return true if email is available, false if taken
     * @throws SQLException If database error occurs
     */
    public boolean isEmailAvailable(String email) throws SQLException {
        return findByEmail(email) == null;
    }
    
    /**
     * Check if user exists
     * @param userId The user ID
     * @return true if user exists, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean userExists(int userId) throws SQLException {
        return findById(userId) != null;
    }
    
    // ========================= STATISTICS OPERATIONS =========================
    
    /**
     * Get total number of users
     * @return Total user count
     * @throws SQLException If database error occurs
     */
    public int getTotalUserCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Get number of admin users
     * @return Admin user count
     * @throws SQLException If database error occurs
     */
    public int getAdminUserCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE is_admin = TRUE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Get number of users registered in the last N days
     * @param days Number of days to look back
     * @return User count
     * @throws SQLException If database error occurs
     */
    public int getRecentRegistrationCount(int days) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE created_date >= DATE_SUB(NOW(), INTERVAL ? DAY)";
        
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
    
    // ========================= HELPER METHODS =========================
    
    /**
     * Map a ResultSet row to a User object
     * @param rs The ResultSet positioned at a valid row
     * @return User object
     * @throws SQLException If database error occurs
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        
        Timestamp createdTimestamp = rs.getTimestamp("created_date");
        if (createdTimestamp != null) {
            user.setCreatedDate(new Date(createdTimestamp.getTime()));
        }
        
        user.setAdmin(rs.getBoolean("is_admin"));
        return user;
    }
} 