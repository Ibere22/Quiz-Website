package model;

import java.util.Date;

/**
 * User model representing a user in the quiz system
 * Corresponds to the 'users' table in the database
 */
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String email;
    private Date createdDate;
    private boolean isAdmin;
    
    // Default constructor
    public User() {
        this.createdDate = new Date();
        this.isAdmin = false;
    }
    
    // Constructor for new user registration
    public User(String username, String passwordHash, String email) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }
    
    // Full constructor
    public User(int userId, String username, String passwordHash, String email, Date createdDate, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.createdDate = createdDate;
        this.isAdmin = isAdmin;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdDate=" + createdDate +
                ", isAdmin=" + isAdmin +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(userId);
    }
} 