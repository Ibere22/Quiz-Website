package dto;

import model.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for QuestionDTO.
 * Covers all methods, branches, and edge cases for 100% line and branch coverage.
 */
class QuestionDTOTest {

    // Helper method to create a Date for tests
    private Date createDate() {
        return new Date(System.currentTimeMillis() - 1000 * 60 * 60); // 1 hour ago
    }

    @Test
    @DisplayName("Test fromQuestion static factory method with valid input")
    void testFromQuestion() {
        Question question = new Question(3, 2, Question.TYPE_MULTIPLE_CHOICE, "2+2?", "4", null, null, 1);
        QuestionDTO dto = QuestionDTO.fromQuestion(question);
        assertNotNull(dto);
        assertEquals(question.getQuestionId(), dto.questionId());
        assertEquals(question.getQuizId(), dto.quizId());
        assertEquals(question.getQuestionType(), dto.questionType());
        assertEquals(question.getQuestionText(), dto.questionText());
        assertEquals(question.getChoicesJson(), dto.choicesJson());
        assertEquals(question.getImageUrl(), dto.imageUrl());
        assertEquals(question.getOrderNum(), dto.orderNum());
    }

    @Test
    @DisplayName("Test fromQuestion static factory method with null input")
    void testFromQuestionNull() {
        assertNull(QuestionDTO.fromQuestion(null));
    }

    @Test
    @DisplayName("Test helper methods for question type and choices")
    void testHelperMethods() {
        String choicesJson = "[\"A\",\"B\",\"C\"]";
        QuestionDTO mcq = new QuestionDTO(1, 1, QuestionDTO.TYPE_MULTIPLE_CHOICE, "Q?", choicesJson, null, 1);
        QuestionDTO fib = new QuestionDTO(2, 1, QuestionDTO.TYPE_FILL_IN_BLANK, "Q?", null, null, 2);
        QuestionDTO pic = new QuestionDTO(3, 1, QuestionDTO.TYPE_PICTURE_RESPONSE, "Q?", null, "img.png", 3);
        QuestionDTO qr = new QuestionDTO(4, 1, QuestionDTO.TYPE_QUESTION_RESPONSE, "Q?", null, null, 4);
        QuestionDTO unknown = new QuestionDTO(5, 1, "unknown_type", "Q?", null, null, 5);
        assertTrue(mcq.isMultipleChoice());
        assertFalse(mcq.isFillInBlank());
        assertFalse(mcq.isPictureResponse());
        assertFalse(mcq.isQuestionResponse());
        assertTrue(fib.isFillInBlank());
        assertTrue(pic.isPictureResponse());
        assertTrue(qr.isQuestionResponse());
        assertFalse(unknown.isMultipleChoice());
        assertFalse(unknown.isFillInBlank());
        assertFalse(unknown.isPictureResponse());
        assertFalse(unknown.isQuestionResponse());
        List<String> choices = mcq.getChoices();
        assertEquals(Arrays.asList("A", "B", "C"), choices);
        assertNull(fib.getChoices());
    }

    @Test
    @DisplayName("Test equals, hashCode, and toString")
    void testEqualsHashCodeToString() {
        QuestionDTO dto1 = new QuestionDTO(1, 1, "type", "text", null, null, 1);
        QuestionDTO dto2 = new QuestionDTO(1, 1, "type", "text", null, null, 1);
        QuestionDTO dto3 = new QuestionDTO(2, 1, "type", "text", null, null, 1);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertTrue(dto1.toString().contains("QuestionDTO"));
        assertTrue(dto1.toString().contains("text"));
    }

    @Test
    @DisplayName("Test immutability of QuestionDTO")
    void testImmutability() {
        QuestionDTO dto = new QuestionDTO(1, 1, "type", "text", null, null, 1);
        // There are no setters, so fields cannot be changed
        assertEquals(1, dto.questionId());
        assertEquals(1, dto.quizId());
        assertEquals("type", dto.questionType());
        assertEquals("text", dto.questionText());
        assertNull(dto.choicesJson());
        assertNull(dto.imageUrl());
        assertEquals(1, dto.orderNum());
    }

    @Test
    @DisplayName("Test null and edge cases for all fields")
    void testNullAndEdgeCases() {
        QuestionDTO dto = new QuestionDTO(0, 0, null, null, null, null, 0);
        assertEquals(0, dto.questionId());
        assertEquals(0, dto.quizId());
        assertNull(dto.questionType());
        assertNull(dto.questionText());
        assertNull(dto.choicesJson());
        assertNull(dto.imageUrl());
        assertEquals(0, dto.orderNum());
        assertNull(dto.getChoices());
    }

    @Test
    @DisplayName("Test QuestionDTO with long and special character fields")
    void testLongAndSpecialFields() {
        String longText = "T".repeat(200);
        String specialText = "Q_测试_!@#";
        QuestionDTO longDto = new QuestionDTO(1, 1, "type", longText, null, null, 1);
        QuestionDTO specialDto = new QuestionDTO(2, 1, "type", specialText, null, null, 1);
        assertEquals(longText, longDto.questionText());
        assertEquals(specialText, specialDto.questionText());
    }

    @Test
    @DisplayName("Test QuestionDTO with all orderNum values")
    void testOrderNumValues() {
        for (int i = 0; i < 10; i++) {
            QuestionDTO dto = new QuestionDTO(i, 1, "type", "text", null, null, i);
            assertEquals(i, dto.orderNum());
        }
    }

    @Test
    @DisplayName("Test QuestionDTO with imageUrl and choicesJson edge cases")
    void testImageUrlAndChoicesJson() {
        String imageUrl = "http://example.com/image.png";
        String choicesJson = "[\"X\",\"Y\"]";
        QuestionDTO dto = new QuestionDTO(1, 1, QuestionDTO.TYPE_PICTURE_RESPONSE, "Q?", choicesJson, imageUrl, 1);
        assertEquals(imageUrl, dto.imageUrl());
        assertEquals(Arrays.asList("X", "Y"), dto.getChoices());
    }

    @Test
    @DisplayName("Test QuestionDTO with future and past orderNum values")
    void testOrderNumEdgeCases() {
        QuestionDTO futureDto = new QuestionDTO(1, 1, "type", "text", null, null, Integer.MAX_VALUE);
        QuestionDTO pastDto = new QuestionDTO(2, 1, "type", "text", null, null, Integer.MIN_VALUE);
        assertEquals(Integer.MAX_VALUE, futureDto.orderNum());
        assertEquals(Integer.MIN_VALUE, pastDto.orderNum());
    }
} 