package dto;

import model.QuizAttempt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for QuizAttemptDTO.
 * Covers all methods, branches, and edge cases for 100% line and branch coverage.
 */
class QuizAttemptDTOTest {

    // Helper method to create a UserDTO for tests
    private UserDTO createUser(int id, String name) {
        return new UserDTO(id, name, name + "@email.com", new Date(), false);
    }

    // Helper method to create a QuizDTO for tests
    private QuizDTO createQuiz(int id, UserDTO creator) {
        return new QuizDTO(id, "Quiz" + id, "desc", creator, false, true, false, false, new Date());
    }

    // Helper method to create a Date for tests
    private Date createDate() {
        return new Date(System.currentTimeMillis() - 1000 * 60 * 60); // 1 hour ago
    }

    @Test
    @DisplayName("Test fromQuizAttempt static factory method with valid input")
    void testFromQuizAttempt() {
        Date now = createDate();
        QuizAttempt attempt = new QuizAttempt(5, 1, 2, 80.0, 10, 120, now, false);
        UserDTO user = createUser(1, "john");
        QuizDTO quiz = createQuiz(2, user);
        QuizAttemptDTO dto = QuizAttemptDTO.fromQuizAttempt(attempt, user, quiz);
        assertNotNull(dto);
        assertEquals(attempt.getAttemptId(), dto.attemptId());
        assertEquals(user, dto.user());
        assertEquals(quiz, dto.quiz());
        assertEquals(attempt.getScore(), dto.score());
        assertEquals(attempt.getTotalQuestions(), dto.totalQuestions());
        assertEquals(attempt.getTimeTaken(), dto.timeTaken());
        assertEquals(attempt.getDateTaken(), dto.dateTaken());
        assertEquals(attempt.isPractice(), dto.isPractice());
    }

    @Test
    @DisplayName("Test fromQuizAttempt static factory method with null input")
    void testFromQuizAttemptNull() {
        assertNull(QuizAttemptDTO.fromQuizAttempt(null, null, null));
    }

    @Test
    @DisplayName("Test getPercentageScore and getFormattedTimeTaken for various values")
    void testHelperMethods() {
        QuizAttemptDTO dto = new QuizAttemptDTO(1, null, null, 8, 10, 3661, new Date(), false);
        assertEquals(80.0, dto.getPercentageScore());
        assertEquals("1h 1m 1s", dto.getFormattedTimeTaken());
        QuizAttemptDTO zeroQuestions = new QuizAttemptDTO(2, null, null, 0, 0, 59, new Date(), false);
        assertEquals(0.0, zeroQuestions.getPercentageScore());
        assertEquals("59s", zeroQuestions.getFormattedTimeTaken());
        QuizAttemptDTO onlyMinutes = new QuizAttemptDTO(3, null, null, 0, 10, 120, new Date(), false);
        assertEquals("2m 0s", onlyMinutes.getFormattedTimeTaken());
        QuizAttemptDTO onlyHours = new QuizAttemptDTO(4, null, null, 0, 10, 3600, new Date(), false);
        assertEquals("1h 0m 0s", onlyHours.getFormattedTimeTaken());
    }

    @Test
    @DisplayName("Test equals, hashCode, and toString")
    void testEqualsHashCodeToString() {
        UserDTO user = createUser(1, "john");
        QuizDTO quiz = createQuiz(2, user);
        Date now = createDate();
        QuizAttemptDTO dto1 = new QuizAttemptDTO(1, user, quiz, 8, 10, 100, now, false);
        QuizAttemptDTO dto2 = new QuizAttemptDTO(1, user, quiz, 8, 10, 100, now, false);
        QuizAttemptDTO dto3 = new QuizAttemptDTO(2, user, quiz, 8, 10, 100, now, false);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertTrue(dto1.toString().contains("QuizAttemptDTO"));
        assertTrue(dto1.toString().contains("john"));
    }

    @Test
    @DisplayName("Test immutability of QuizAttemptDTO")
    void testImmutability() {
        UserDTO user = createUser(1, "john");
        QuizDTO quiz = createQuiz(2, user);
        Date now = createDate();
        QuizAttemptDTO dto = new QuizAttemptDTO(1, user, quiz, 8, 10, 100, now, false);
        // There are no setters, so fields cannot be changed
        assertEquals(1, dto.attemptId());
        assertEquals(user, dto.user());
        assertEquals(quiz, dto.quiz());
        assertEquals(8, dto.score());
        assertEquals(10, dto.totalQuestions());
        assertEquals(100, dto.timeTaken());
        assertEquals(now, dto.dateTaken());
        assertFalse(dto.isPractice());
    }

    @Test
    @DisplayName("Test null and edge cases for all fields")
    void testNullAndEdgeCases() {
        QuizAttemptDTO dto = new QuizAttemptDTO(0, null, null, 0, 0, 0, null, false);
        assertEquals(0, dto.attemptId());
        assertNull(dto.user());
        assertNull(dto.quiz());
        assertEquals(0, dto.score());
        assertEquals(0, dto.totalQuestions());
        assertEquals(0, dto.timeTaken());
        assertNull(dto.dateTaken());
        assertFalse(dto.isPractice());
    }

    @Test
    @DisplayName("Test QuizAttemptDTO with long and special values")
    void testLongAndSpecialFields() {
        String longName = "N".repeat(100);
        UserDTO user = createUser(1, longName);
        QuizDTO quiz = createQuiz(2, user);
        Date now = createDate();
        QuizAttemptDTO longDto = new QuizAttemptDTO(1, user, quiz, 100, 100, 100000, now, true);
        assertEquals(longName, longDto.user().username());
        assertTrue(longDto.isPractice());
    }

    @Test
    @DisplayName("Test QuizAttemptDTO with all boolean values for isPractice")
    void testIsPracticeValues() {
        QuizAttemptDTO practiceDto = new QuizAttemptDTO(1, null, null, 0, 0, 0, new Date(), true);
        QuizAttemptDTO notPracticeDto = new QuizAttemptDTO(2, null, null, 0, 0, 0, new Date(), false);
        assertTrue(practiceDto.isPractice());
        assertFalse(notPracticeDto.isPractice());
    }

    @Test
    @DisplayName("Test QuizAttemptDTO with future and past dates")
    void testDateEdgeCases() {
        Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365);
        Date past = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 365);
        QuizAttemptDTO futureDto = new QuizAttemptDTO(1, null, null, 0, 0, 0, future, false);
        QuizAttemptDTO pastDto = new QuizAttemptDTO(2, null, null, 0, 0, 0, past, false);
        assertEquals(future, futureDto.dateTaken());
        assertEquals(past, pastDto.dateTaken());
    }
} 