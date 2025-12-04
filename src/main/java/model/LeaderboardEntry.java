package model;

import java.sql.Timestamp;

public class LeaderboardEntry {
    private int quizId;
    private String quizTitle;
    private int userId;
    private String username;
    private double bestScore;
    private int mostCorrect;
    private Timestamp mostRecent;

    public LeaderboardEntry(int quizId, String quizTitle, int userId, String username, double bestScore, int mostCorrect, Timestamp mostRecent) {
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.userId = userId;
        this.username = username;
        this.bestScore = bestScore;
        this.mostCorrect = mostCorrect;
        this.mostRecent = mostRecent;
    }

    public int getQuizId() { return quizId; }
    public String getQuizTitle() { return quizTitle; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public double getBestScore() { return bestScore; }
    public int getMostCorrect() { return mostCorrect; }
    public Timestamp getMostRecent() { return mostRecent; }

    @Override
    public String toString() {
        return "LeaderboardEntry{" +
                "quizId=" + quizId +
                ", quizTitle='" + quizTitle + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", bestScore=" + bestScore +
                ", mostCorrect=" + mostCorrect +
                ", mostRecent=" + mostRecent +
                '}';
    }
} 