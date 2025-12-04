package model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Question model representing a question in a quiz
 * Corresponds to the 'questions' table in the database
 * Supports 4 question types: Question-Response, Fill-in-Blank, Multiple Choice, Picture-Response
 */
public class Question {
    private int questionId;
    private int quizId;
    private String questionType;
    private String questionText;
    private String correctAnswer;
    private String choicesJson; // JSON string for multiple choice options
    private String imageUrl;    // For picture-response questions
    private int orderNum;       // Order within the quiz
    
    // Question type constants
    public static final String TYPE_QUESTION_RESPONSE = "question-response";
    public static final String TYPE_FILL_IN_BLANK = "fill-in-blank";
    public static final String TYPE_MULTIPLE_CHOICE = "multiple-choice";
    public static final String TYPE_PICTURE_RESPONSE = "picture-response";
    
    private static final Gson gson = new Gson();
    
    // Default constructor
    public Question() {
        this.questionType = TYPE_QUESTION_RESPONSE;
        this.orderNum = 1;
    }
    
    // Constructor for simple question-response and fill-in-blank
    public Question(int quizId, String questionType, String questionText, String correctAnswer, int orderNum) {
        this();
        this.quizId = quizId;
        this.questionType = questionType;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.orderNum = orderNum;
    }
    
    // Full constructor
    public Question(int questionId, int quizId, String questionType, String questionText, 
                   String correctAnswer, String choicesJson, String imageUrl, int orderNum) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.questionType = questionType;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.choicesJson = choicesJson;
        this.imageUrl = imageUrl;
        this.orderNum = orderNum;
    }
    
    // Helper methods for multiple choice questions
    public void setChoices(List<String> choices) {
        this.choicesJson = gson.toJson(choices);
    }
    
    public List<String> getChoices() {
        if (choicesJson == null || choicesJson.trim().isEmpty()) {
            return null;
        }
        Type listType = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(choicesJson, listType);
    }
    
    // Getters and Setters
    public int getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
    
    public int getQuizId() {
        return quizId;
    }
    
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
    
    public String getQuestionType() {
        return questionType;
    }
    
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    
    public String getChoicesJson() {
        return choicesJson;
    }
    
    public void setChoicesJson(String choicesJson) {
        this.choicesJson = choicesJson;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public int getOrderNum() {
        return orderNum;
    }
    
    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }
    
    // Helper methods for question type validation
    public boolean isMultipleChoice() {
        return TYPE_MULTIPLE_CHOICE.equals(questionType);
    }
    
    public boolean isFillInBlank() {
        return TYPE_FILL_IN_BLANK.equals(questionType);
    }
    
    public boolean isPictureResponse() {
        return TYPE_PICTURE_RESPONSE.equals(questionType);
    }
    
    public boolean isQuestionResponse() {
        return TYPE_QUESTION_RESPONSE.equals(questionType);
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", quizId=" + quizId +
                ", questionType='" + questionType + '\'' +
                ", questionText='" + questionText + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", orderNum=" + orderNum +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Question question = (Question) obj;
        return questionId == question.questionId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(questionId);
    }
} 