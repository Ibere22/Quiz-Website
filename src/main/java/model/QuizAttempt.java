package model;

import java.util.Date;

/**
 * QuizAttempt model representing a user's attempt at taking a quiz
 * Corresponds to the 'quiz_attempts' table in the database
 */
public class QuizAttempt {
    private int attemptId;
    private int userId;
    private int quizId;
    private double score;         // Percentage score
    private int totalQuestions;   // Total questions in the quiz
    private long timeTaken;       // Time taken in seconds
    private Date dateTaken;
    private boolean isPractice;   // Whether this was a practice attempt
    
    // Default constructor
    public QuizAttempt() {
        this.dateTaken = new Date();
        this.isPractice = false;
    }
    
    // Constructor for new attempt
    public QuizAttempt(int userId, int quizId, double score, int totalQuestions, long timeTaken, boolean isPractice) {
        this();
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timeTaken = timeTaken;
        this.isPractice = isPractice;
    }
    
    // Full constructor
    public QuizAttempt(int attemptId, int userId, int quizId, double score, int totalQuestions, 
                      long timeTaken, Date dateTaken, boolean isPractice) {
        this.attemptId = attemptId;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timeTaken = timeTaken;
        this.dateTaken = dateTaken;
        this.isPractice = isPractice;
    }
    
    // Getters and Setters
    public int getAttemptId() {
        return attemptId;
    }
    
    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getQuizId() {
        return quizId;
    }
    
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public int getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public long getTimeTaken() {
        return timeTaken;
    }
    
    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }
    
    public Date getDateTaken() {
        return dateTaken;
    }
    
    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }
    
    public boolean isPractice() {
        return isPractice;
    }
    
    public void setPractice(boolean practice) {
        isPractice = practice;
    }
    
    // Helper methods
    public int getCorrectAnswers() {
        return (int) Math.round((score / 100.0) * totalQuestions);
    }
    
    public String getFormattedTime() {
        long minutes = timeTaken / 60;
        long seconds = timeTaken % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    @Override
    public String toString() {
        return "QuizAttempt{" +
                "attemptId=" + attemptId +
                ", userId=" + userId +
                ", quizId=" + quizId +
                ", score=" + score +
                ", totalQuestions=" + totalQuestions +
                ", timeTaken=" + timeTaken +
                ", dateTaken=" + dateTaken +
                ", isPractice=" + isPractice +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        QuizAttempt that = (QuizAttempt) obj;
        return attemptId == that.attemptId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(attemptId);
    }
} 