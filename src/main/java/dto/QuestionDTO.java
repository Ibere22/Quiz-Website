package dto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Data Transfer Object for Question entity
 * Contains question information for display on the website
 * Excludes correct answer to prevent cheating
 */
public record QuestionDTO(
    int questionId,
    int quizId,
    String questionType,
    String questionText,
    String choicesJson,
    String imageUrl,
    int orderNum
) {
    // Question type constants
    public static final String TYPE_QUESTION_RESPONSE = "question-response";
    public static final String TYPE_FILL_IN_BLANK = "fill-in-blank";
    public static final String TYPE_MULTIPLE_CHOICE = "multiple-choice";
    public static final String TYPE_PICTURE_RESPONSE = "picture-response";
    
    private static final Gson gson = new Gson();
    
    // Helper methods for question type checking
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
    
    // Helper method to get choices for multiple choice questions
    public List<String> getChoices() {
        if (choicesJson == null || choicesJson.trim().isEmpty()) {
            return null;
        }
        Type listType = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(choicesJson, listType);
    }
    
    // Static factory method for conversion from Question model
    public static QuestionDTO fromQuestion(model.Question question) {
        if (question == null) return null;
        return new QuestionDTO(
            question.getQuestionId(),
            question.getQuizId(),
            question.getQuestionType(),
            question.getQuestionText(),
            question.getChoicesJson(),
            question.getImageUrl(),
            question.getOrderNum()
        );
    }
} 