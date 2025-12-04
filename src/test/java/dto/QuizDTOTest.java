package dto;

import model.Quiz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for QuizDTO.
 * Covers all methods, branches, and edge cases for 100% line and branch coverage.
 */
class QuizDTOTest {

    // Helper method to create a UserDTO for tests
    private UserDTO createUser() {
        return new UserDTO(1, "john", "john@email.com", new Date(), false);
    }

    // Helper method to create a Date for tests
    private Date createDate() {
        return new Date(System.currentTimeMillis() - 1000 * 60 * 60); // 1 hour ago
    }

    @Test
    @DisplayName("Test fromQuiz static factory method with valid input")
    void testFromQuiz() {
        Date now = createDate();
        Quiz quiz = new Quiz(2, "Math Quiz", "A quiz about math", 1, true, false, true, false, now);
        UserDTO creator = createUser();
        QuizDTO dto = QuizDTO.fromQuiz(quiz, creator);
        assertNotNull(dto);
        assertEquals(quiz.getQuizId(), dto.quizId());
        assertEquals(quiz.getTitle(), dto.title());
        assertEquals(quiz.getDescription(), dto.description());
        assertEquals(creator, dto.creator());
        assertEquals(quiz.isRandomOrder(), dto.randomOrder());
        assertEquals(quiz.isOnePage(), dto.onePage());
        assertEquals(quiz.isImmediateCorrection(), dto.immediateCorrection());
        assertEquals(quiz.isPracticeMode(), dto.practiceMode());
        assertEquals(quiz.getCreatedDate(), dto.createdDate());
    }

    @Test
    @DisplayName("Test fromQuiz static factory method with null input")
    void testFromQuizNull() {
        assertNull(QuizDTO.fromQuiz(null, null));
    }

    @Test
    @DisplayName("Test equals, hashCode, and toString")
    void testEqualsHashCodeToString() {
        Date now = createDate();
        UserDTO creator = createUser();
        QuizDTO dto1 = new QuizDTO(2, "Math Quiz", "A quiz about math", creator, true, false, true, false, now);
        QuizDTO dto2 = new QuizDTO(2, "Math Quiz", "A quiz about math", creator, true, false, true, false, now);
        QuizDTO dto3 = new QuizDTO(3, "Science Quiz", "A quiz about science", creator, false, true, false, true, now);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertTrue(dto1.toString().contains("QuizDTO"));
        assertTrue(dto1.toString().contains("Math Quiz"));
    }

    @Test
    @DisplayName("Test immutability of QuizDTO")
    void testImmutability() {
        Date now = createDate();
        UserDTO creator = createUser();
        QuizDTO dto = new QuizDTO(2, "Math Quiz", "A quiz about math", creator, true, false, true, false, now);
        // There are no setters, so fields cannot be changed
        assertEquals(2, dto.quizId());
        assertEquals("Math Quiz", dto.title());
        assertEquals("A quiz about math", dto.description());
        assertEquals(creator, dto.creator());
        assertTrue(dto.randomOrder());
        assertFalse(dto.onePage());
        assertTrue(dto.immediateCorrection());
        assertFalse(dto.practiceMode());
        assertEquals(now, dto.createdDate());
    }

    @Test
    @DisplayName("Test null and edge cases for all fields")
    void testNullAndEdgeCases() {
        QuizDTO dto = new QuizDTO(0, null, null, null, false, false, false, false, null);
        assertEquals(0, dto.quizId());
        assertNull(dto.title());
        assertNull(dto.description());
        assertNull(dto.creator());
        assertFalse(dto.randomOrder());
        assertFalse(dto.onePage());
        assertFalse(dto.immediateCorrection());
        assertFalse(dto.practiceMode());
        assertNull(dto.createdDate());
    }

    @Test
    @DisplayName("Test QuizDTO with long and special character fields")
    void testLongAndSpecialFields() {
        String longTitle = "T".repeat(200);
        String longDesc = "D".repeat(500);
        String specialTitle = "Quiz_测试_!@#";
        UserDTO creator = createUser();
        Date now = createDate();
        QuizDTO longDto = new QuizDTO(1, longTitle, longDesc, creator, false, true, false, true, now);
        QuizDTO specialDto = new QuizDTO(2, specialTitle, "desc", creator, true, false, true, false, now);
        assertEquals(longTitle, longDto.title());
        assertEquals(longDesc, longDto.description());
        assertEquals(specialTitle, specialDto.title());
    }

    @Test
    @DisplayName("Test QuizDTO with all boolean combinations")
    void testAllBooleanCombinations() {
        UserDTO creator = createUser();
        Date now = createDate();
        QuizDTO dto1 = new QuizDTO(1, "Quiz1", "desc1", creator, true, true, true, true, now);
        QuizDTO dto2 = new QuizDTO(2, "Quiz2", "desc2", creator, false, false, false, false, now);
        assertTrue(dto1.randomOrder());
        assertTrue(dto1.onePage());
        assertTrue(dto1.immediateCorrection());
        assertTrue(dto1.practiceMode());
        assertFalse(dto2.randomOrder());
        assertFalse(dto2.onePage());
        assertFalse(dto2.immediateCorrection());
        assertFalse(dto2.practiceMode());
    }

    @Test
    @DisplayName("Test QuizDTO with future and past dates")
    void testDateEdgeCases() {
        Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365);
        Date past = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 365);
        UserDTO creator = createUser();
        QuizDTO futureQuiz = new QuizDTO(1, "future", "desc", creator, false, false, false, false, future);
        QuizDTO pastQuiz = new QuizDTO(2, "past", "desc", creator, false, false, false, false, past);
        assertEquals(future, futureQuiz.createdDate());
        assertEquals(past, pastQuiz.createdDate());
    }
} 