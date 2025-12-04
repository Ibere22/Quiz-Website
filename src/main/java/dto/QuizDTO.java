package dto;

import java.util.Date;

/**
 * Data Transfer Object for Quiz entity
 * Contains quiz information for display on the website
 * Includes creator information as UserDTO instead of just creator ID
 */
public record QuizDTO(
    int quizId,
    String title,
    String description,
    UserDTO creator,
    boolean randomOrder,
    boolean onePage,
    boolean immediateCorrection,
    boolean practiceMode,
    Date createdDate
) {
    // Static factory method for conversion from Quiz model
    public static QuizDTO fromQuiz(model.Quiz quiz, UserDTO creator) {
        if (quiz == null) return null;
        return new QuizDTO(
            quiz.getQuizId(),
            quiz.getTitle(),
            quiz.getDescription(),
            creator,
            quiz.isRandomOrder(),
            quiz.isOnePage(),
            quiz.isImmediateCorrection(),
            quiz.isPracticeMode(),
            quiz.getCreatedDate()
        );
    }
} 