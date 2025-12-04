package model;

import java.util.Date;

/**
 * Achievement model representing user achievements
 * Corresponds to the 'achievements' table in the database
 */
public class Achievement {
    private int achievementId;
    private int userId;
    private String achievementType;
    private Date dateEarned;
    private String description;
    
    // Achievement type constants
    public static final String AMATEUR_AUTHOR = "amateur_author";          // Created 1 quiz
    public static final String PROLIFIC_AUTHOR = "prolific_author";        // Created 5 quizzes
    public static final String PRODIGIOUS_AUTHOR = "prodigious_author";    // Created 10 quizzes
    public static final String QUIZ_MACHINE = "quiz_machine";              // Took 10 quizzes
    public static final String I_AM_THE_GREATEST = "i_am_the_greatest";    // Highest score on a quiz
    public static final String PRACTICE_MAKES_PERFECT = "practice_makes_perfect"; // Took a quiz in practice mode
    
    // Default constructor
    public Achievement() {
        this.dateEarned = new Date();
    }
    
    // Constructor for new achievement
    public Achievement(int userId, String achievementType, String description) {
        this();
        this.userId = userId;
        this.achievementType = achievementType;
        this.description = description;
    }
    
    // Full constructor
    public Achievement(int achievementId, int userId, String achievementType, Date dateEarned, String description) {
        this.achievementId = achievementId;
        this.userId = userId;
        this.achievementType = achievementType;
        this.dateEarned = dateEarned;
        this.description = description;
    }
    
    // Getters and Setters
    public int getAchievementId() {
        return achievementId;
    }
    
    public void setAchievementId(int achievementId) {
        this.achievementId = achievementId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getAchievementType() {
        return achievementType;
    }
    
    public void setAchievementType(String achievementType) {
        this.achievementType = achievementType;
    }
    
    public Date getDateEarned() {
        return dateEarned;
    }
    
    public void setDateEarned(Date dateEarned) {
        this.dateEarned = dateEarned;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    // Helper methods to get display names and descriptions
    public String getDisplayName() {
        switch (achievementType) {
            case AMATEUR_AUTHOR:
                return "Amateur Author";
            case PROLIFIC_AUTHOR:
                return "Prolific Author";
            case PRODIGIOUS_AUTHOR:
                return "Prodigious Author";
            case QUIZ_MACHINE:
                return "Quiz Machine";
            case I_AM_THE_GREATEST:
                return "I am the Greatest";
            case PRACTICE_MAKES_PERFECT:
                return "Practice Makes Perfect";
            default:
                return achievementType;
        }
    }
    
    public String getDefaultDescription() {
        switch (achievementType) {
            case AMATEUR_AUTHOR:
                return "Created your first quiz!";
            case PROLIFIC_AUTHOR:
                return "Created 5 quizzes!";
            case PRODIGIOUS_AUTHOR:
                return "Created 10 quizzes!";
            case QUIZ_MACHINE:
                return "Took 10 quizzes!";
            case I_AM_THE_GREATEST:
                return "Achieved the highest score on a quiz!";
            case PRACTICE_MAKES_PERFECT:
                return "Took a quiz in practice mode!";
            default:
                return description;
        }
    }
    
    @Override
    public String toString() {
        return "Achievement{" +
                "achievementId=" + achievementId +
                ", userId=" + userId +
                ", achievementType='" + achievementType + '\'' +
                ", dateEarned=" + dateEarned +
                ", description='" + description + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Achievement that = (Achievement) obj;
        return achievementId == that.achievementId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(achievementId);
    }
} 