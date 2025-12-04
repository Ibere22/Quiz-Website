package dto;

import java.util.Date;

/**
 * Data Transfer Object for QuizAttempt entity
 * Contains quiz attempt information for display on the website
 * Includes user and quiz information as DTOs instead of just IDs
 */
public record QuizAttemptDTO(
    int attemptId,
    UserDTO user,
    QuizDTO quiz,
    double score,
    int totalQuestions,
    long timeTaken,
    Date dateTaken,
    boolean isPractice
) {
    // Helper method to calculate percentage score
    public double getPercentageScore() {
        if (totalQuestions == 0) return 0.0;
        return (score / totalQuestions) * 100.0;
    }
    
    // Helper method to format time taken
    public String getFormattedTimeTaken() {
        long hours = timeTaken / 3600;
        long minutes = (timeTaken % 3600) / 60;
        long seconds = timeTaken % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
    
    // Static factory method for conversion from QuizAttempt model
    public static QuizAttemptDTO fromQuizAttempt(model.QuizAttempt attempt, UserDTO user, QuizDTO quiz) {
        if (attempt == null) return null;
        return new QuizAttemptDTO(
            attempt.getAttemptId(),
            user,
            quiz,
            attempt.getScore(),
            attempt.getTotalQuestions(),
            attempt.getTimeTaken(),
            attempt.getDateTaken(),
            attempt.isPractice()
        );
    }
} 