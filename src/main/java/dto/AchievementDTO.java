package dto;

import java.util.Date;

/**
 * Data Transfer Object for Achievement entity
 * Contains achievement information for display on the website
 * Includes user who earned it and achievement details
 */
public record AchievementDTO(
    int achievementId,
    UserDTO user,
    String achievementType,
    Date dateEarned,
    String description
) {
    // Achievement type constants
    public static final String TYPE_AMATEUR_AUTHOR = "amateur_author";
    public static final String TYPE_PROLIFIC_AUTHOR = "prolific_author";
    public static final String TYPE_PRODIGIOUS_AUTHOR = "prodigious_author";
    public static final String TYPE_QUIZ_MACHINE = "quiz_machine";
    public static final String TYPE_I_AM_THE_GREATEST = "i_am_the_greatest";
    public static final String TYPE_PRACTICE_MAKES_PERFECT = "practice_makes_perfect";
    
    // Helper methods for achievement type checking
    public boolean isAmateurAuthor() {
        return TYPE_AMATEUR_AUTHOR.equals(achievementType);
    }
    
    public boolean isProlificAuthor() {
        return TYPE_PROLIFIC_AUTHOR.equals(achievementType);
    }
    
    public boolean isProdigiousAuthor() {
        return TYPE_PRODIGIOUS_AUTHOR.equals(achievementType);
    }
    
    public boolean isQuizMachine() {
        return TYPE_QUIZ_MACHINE.equals(achievementType);
    }
    
    public boolean isIAmTheGreatest() {
        return TYPE_I_AM_THE_GREATEST.equals(achievementType);
    }
    
    public boolean isPracticeMakesPerfect() {
        return TYPE_PRACTICE_MAKES_PERFECT.equals(achievementType);
    }
    
    // Helper method to get achievement display name
    public String getDisplayName() {
        return switch (achievementType) {
            case TYPE_AMATEUR_AUTHOR -> "Amateur Author";
            case TYPE_PROLIFIC_AUTHOR -> "Prolific Author";
            case TYPE_PRODIGIOUS_AUTHOR -> "Prodigious Author";
            case TYPE_QUIZ_MACHINE -> "Quiz Machine";
            case TYPE_I_AM_THE_GREATEST -> "I am the Greatest";
            case TYPE_PRACTICE_MAKES_PERFECT -> "Practice Makes Perfect";
            default -> "Unknown Achievement";
        };
    }
    
    // Helper method to get achievement icon (you can customize this)
    public String getIconClass() {
        return switch (achievementType) {
            case TYPE_AMATEUR_AUTHOR -> "achievement-icon amateur-author";
            case TYPE_PROLIFIC_AUTHOR -> "achievement-icon prolific-author";
            case TYPE_PRODIGIOUS_AUTHOR -> "achievement-icon prodigious-author";
            case TYPE_QUIZ_MACHINE -> "achievement-icon quiz-machine";
            case TYPE_I_AM_THE_GREATEST -> "achievement-icon greatest";
            case TYPE_PRACTICE_MAKES_PERFECT -> "achievement-icon practice";
            default -> "achievement-icon default";
        };
    }
    
    // Helper method to format date earned
    public String getFormattedDateEarned() {
        if (dateEarned == null) return "";
        
        long now = System.currentTimeMillis();
        long earnedTime = dateEarned.getTime();
        long diffInSeconds = (now - earnedTime) / 1000;
        
        if (diffInSeconds < 60) {
            return "Just earned";
        } else if (diffInSeconds < 3600) {
            long minutes = diffInSeconds / 60;
            return "Earned " + minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (diffInSeconds < 86400) {
            long hours = diffInSeconds / 3600;
            return "Earned " + hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else {
            long days = diffInSeconds / 86400;
            return "Earned " + days + " day" + (days > 1 ? "s" : "") + " ago";
        }
    }
    
    // Static factory method for conversion from Achievement model
    public static AchievementDTO fromAchievement(model.Achievement achievement, UserDTO user) {
        if (achievement == null) return null;
        return new AchievementDTO(
            achievement.getAchievementId(),
            user,
            achievement.getAchievementType(),
            achievement.getDateEarned(),
            achievement.getDescription()
        );
    }
} 