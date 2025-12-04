package model;

import java.util.Date;

/**
 * Quiz model representing a quiz in the system
 * Corresponds to the 'quizzes' table in the database
 */
public class Quiz {
    private int quizId;
    private String title;
    private String description;
    private int creatorId;
    private boolean randomOrder;
    private boolean onePage;
    private boolean immediateCorrection;
    private boolean practiceMode;
    private Date createdDate;
    
    // Default constructor
    public Quiz() {
        this.createdDate = new Date();
        this.randomOrder = false;
        this.onePage = true;
        this.immediateCorrection = false;
        this.practiceMode = false;
    }
    
    // Constructor for creating new quiz
    public Quiz(String title, String description, int creatorId) {
        this();
        this.title = title;
        this.description = description;
        this.creatorId = creatorId;
    }
    
    // Full constructor
    public Quiz(int quizId, String title, String description, int creatorId, 
               boolean randomOrder, boolean onePage, boolean immediateCorrection, 
               boolean practiceMode, Date createdDate) {
        this.quizId = quizId;
        this.title = title;
        this.description = description;
        this.creatorId = creatorId;
        this.randomOrder = randomOrder;
        this.onePage = onePage;
        this.immediateCorrection = immediateCorrection;
        this.practiceMode = practiceMode;
        this.createdDate = createdDate;
    }
    
    // Getters and Setters
    public int getQuizId() {
        return quizId;
    }
    
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }
    
    public boolean isRandomOrder() {
        return randomOrder;
    }
    
    public void setRandomOrder(boolean randomOrder) {
        this.randomOrder = randomOrder;
    }
    
    public boolean isOnePage() {
        return onePage;
    }
    
    public void setOnePage(boolean onePage) {
        this.onePage = onePage;
    }
    
    public boolean isImmediateCorrection() {
        return immediateCorrection;
    }
    
    public void setImmediateCorrection(boolean immediateCorrection) {
        this.immediateCorrection = immediateCorrection;
    }
    
    public boolean isPracticeMode() {
        return practiceMode;
    }
    
    public void setPracticeMode(boolean practiceMode) {
        this.practiceMode = practiceMode;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    @Override
    public String toString() {
        return "Quiz{" +
                "quizId=" + quizId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", creatorId=" + creatorId +
                ", randomOrder=" + randomOrder +
                ", onePage=" + onePage +
                ", immediateCorrection=" + immediateCorrection +
                ", practiceMode=" + practiceMode +
                ", createdDate=" + createdDate +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Quiz quiz = (Quiz) obj;
        return quizId == quiz.quizId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(quizId);
    }
} 