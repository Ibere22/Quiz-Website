package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the QuizAttempt model class.
 * Tests all constructors, getters/setters, helper methods, equality, toString,
 * edge cases, and real-world scenarios.
 */
@DisplayName("QuizAttempt Model Tests")
public class QuizAttemptTest {

    private QuizAttempt quizAttempt;
    private Date testDate;

    @BeforeEach
    void setUp() {
        quizAttempt = new QuizAttempt();
        testDate = new Date();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize with default values")
        void testDefaultConstructor() {
            QuizAttempt attempt = new QuizAttempt();
            
            assertEquals(0, attempt.getAttemptId());
            assertEquals(0, attempt.getUserId());
            assertEquals(0, attempt.getQuizId());
            assertEquals(0.0, attempt.getScore());
            assertEquals(0, attempt.getTotalQuestions());
            assertEquals(0L, attempt.getTimeTaken());
            assertNotNull(attempt.getDateTaken());
            assertFalse(attempt.isPractice());
            
            // Verify date is recent (within last second)
            long timeDiff = new Date().getTime() - attempt.getDateTaken().getTime();
            assertTrue(timeDiff < 1000);
        }

        @Test
        @DisplayName("New attempt constructor should set basic attempt fields")
        void testNewAttemptConstructor() {
            QuizAttempt attempt = new QuizAttempt(101, 201, 85.5, 20, 600L, true);
            
            assertEquals(0, attempt.getAttemptId()); // Should be 0 (not set)
            assertEquals(101, attempt.getUserId());
            assertEquals(201, attempt.getQuizId());
            assertEquals(85.5, attempt.getScore());
            assertEquals(20, attempt.getTotalQuestions());
            assertEquals(600L, attempt.getTimeTaken());
            assertNotNull(attempt.getDateTaken());
            assertTrue(attempt.isPractice());
            
            // Verify date is recent
            long timeDiff = new Date().getTime() - attempt.getDateTaken().getTime();
            assertTrue(timeDiff < 1000);
        }

        @Test
        @DisplayName("Full constructor should set all fields correctly")
        void testFullConstructor() {
            Date specificDate = new Date(System.currentTimeMillis() - 86400000); // Yesterday
            
            QuizAttempt attempt = new QuizAttempt(301, 102, 202, 92.0, 25, 
                                                1200L, specificDate, false);
            
            assertEquals(301, attempt.getAttemptId());
            assertEquals(102, attempt.getUserId());
            assertEquals(202, attempt.getQuizId());
            assertEquals(92.0, attempt.getScore());
            assertEquals(25, attempt.getTotalQuestions());
            assertEquals(1200L, attempt.getTimeTaken());
            assertEquals(specificDate, attempt.getDateTaken());
            assertFalse(attempt.isPractice());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("AttemptId getter and setter should work correctly")
        void testAttemptIdGetterSetter() {
            quizAttempt.setAttemptId(999);
            assertEquals(999, quizAttempt.getAttemptId());
        }

        @Test
        @DisplayName("UserId getter and setter should work correctly")
        void testUserIdGetterSetter() {
            quizAttempt.setUserId(123);
            assertEquals(123, quizAttempt.getUserId());
        }

        @Test
        @DisplayName("QuizId getter and setter should work correctly")
        void testQuizIdGetterSetter() {
            quizAttempt.setQuizId(456);
            assertEquals(456, quizAttempt.getQuizId());
        }

        @Test
        @DisplayName("Score getter and setter should work correctly")
        void testScoreGetterSetter() {
            quizAttempt.setScore(78.5);
            assertEquals(78.5, quizAttempt.getScore());
        }

        @Test
        @DisplayName("TotalQuestions getter and setter should work correctly")
        void testTotalQuestionsGetterSetter() {
            quizAttempt.setTotalQuestions(15);
            assertEquals(15, quizAttempt.getTotalQuestions());
        }

        @Test
        @DisplayName("TimeTaken getter and setter should work correctly")
        void testTimeTakenGetterSetter() {
            quizAttempt.setTimeTaken(1800L);
            assertEquals(1800L, quizAttempt.getTimeTaken());
        }

        @Test
        @DisplayName("DateTaken getter and setter should work correctly")
        void testDateTakenGetterSetter() {
            quizAttempt.setDateTaken(testDate);
            assertEquals(testDate, quizAttempt.getDateTaken());
        }

        @Test
        @DisplayName("Practice getter and setter should work correctly")
        void testPracticeGetterSetter() {
            quizAttempt.setPractice(true);
            assertTrue(quizAttempt.isPractice());
            
            quizAttempt.setPractice(false);
            assertFalse(quizAttempt.isPractice());
        }

        @Test
        @DisplayName("DateTaken can be set to null")
        void testNullDateTaken() {
            quizAttempt.setDateTaken(null);
            assertNull(quizAttempt.getDateTaken());
        }
    }

    @Nested
    @DisplayName("Helper Method Tests")
    class HelperMethodTests {

        @Test
        @DisplayName("getCorrectAnswers should calculate correctly for various scores")
        void testGetCorrectAnswers() {
            // Perfect score
            quizAttempt.setScore(100.0);
            quizAttempt.setTotalQuestions(20);
            assertEquals(20, quizAttempt.getCorrectAnswers());
            
            // 80% score
            quizAttempt.setScore(80.0);
            quizAttempt.setTotalQuestions(25);
            assertEquals(20, quizAttempt.getCorrectAnswers());
            
            // 50% score
            quizAttempt.setScore(50.0);
            quizAttempt.setTotalQuestions(10);
            assertEquals(5, quizAttempt.getCorrectAnswers());
            
            // Zero score
            quizAttempt.setScore(0.0);
            quizAttempt.setTotalQuestions(15);
            assertEquals(0, quizAttempt.getCorrectAnswers());
        }

        @Test
        @DisplayName("getCorrectAnswers should handle fractional results correctly")
        void testGetCorrectAnswersFractional() {
            // 85% of 20 = 17
            quizAttempt.setScore(85.0);
            quizAttempt.setTotalQuestions(20);
            assertEquals(17, quizAttempt.getCorrectAnswers());
            
            // 75% of 15 = 11.25 → rounds to 11
            quizAttempt.setScore(75.0);
            quizAttempt.setTotalQuestions(15);
            assertEquals(11, quizAttempt.getCorrectAnswers());
            
            // 66.67% of 9 = 6.0003 → rounds to 6
            quizAttempt.setScore(66.67);
            quizAttempt.setTotalQuestions(9);
            assertEquals(6, quizAttempt.getCorrectAnswers());
        }

        @Test
        @DisplayName("getFormattedTime should format time correctly")
        void testGetFormattedTime() {
            // 1 minute
            quizAttempt.setTimeTaken(60L);
            assertEquals("1:00", quizAttempt.getFormattedTime());
            
            // 5 minutes 30 seconds
            quizAttempt.setTimeTaken(330L);
            assertEquals("5:30", quizAttempt.getFormattedTime());
            
            // 0 seconds
            quizAttempt.setTimeTaken(0L);
            assertEquals("0:00", quizAttempt.getFormattedTime());
            
            // 59 seconds
            quizAttempt.setTimeTaken(59L);
            assertEquals("0:59", quizAttempt.getFormattedTime());
            
            // 1 hour 23 minutes 45 seconds
            quizAttempt.setTimeTaken(5025L);
            assertEquals("83:45", quizAttempt.getFormattedTime());
        }

        @Test
        @DisplayName("getFormattedTime should handle large time values")
        void testGetFormattedTimeLargeValues() {
            // 2 hours = 120 minutes
            quizAttempt.setTimeTaken(7200L);
            assertEquals("120:00", quizAttempt.getFormattedTime());
            
            // Very large time
            quizAttempt.setTimeTaken(36000L); // 10 hours
            assertEquals("600:00", quizAttempt.getFormattedTime());
        }
    }

    @Nested
    @DisplayName("Score and Performance Tests")
    class ScoreTests {

        @Test
        @DisplayName("Score should handle decimal values correctly")
        void testDecimalScores() {
            quizAttempt.setScore(87.56);
            assertEquals(87.56, quizAttempt.getScore());
            
            quizAttempt.setScore(0.1);
            assertEquals(0.1, quizAttempt.getScore());
            
            quizAttempt.setScore(99.99);
            assertEquals(99.99, quizAttempt.getScore());
        }

        @Test
        @DisplayName("Score should handle boundary values")
        void testScoreBoundaryValues() {
            // Minimum score
            quizAttempt.setScore(0.0);
            assertEquals(0.0, quizAttempt.getScore());
            
            // Maximum score
            quizAttempt.setScore(100.0);
            assertEquals(100.0, quizAttempt.getScore());
            
            // Above 100% (extra credit scenarios)
            quizAttempt.setScore(105.0);
            assertEquals(105.0, quizAttempt.getScore());
        }
    }

    @Nested
    @DisplayName("Equality and HashCode Tests")
    class EqualityTests {

        @Test
        @DisplayName("Quiz attempts with same attemptId should be equal")
        void testEqualQuizAttemptsWithSameAttemptId() {
            QuizAttempt attempt1 = new QuizAttempt();
            attempt1.setAttemptId(100);
            QuizAttempt attempt2 = new QuizAttempt();
            attempt2.setAttemptId(100);
            
            assertEquals(attempt1, attempt2);
            assertEquals(attempt1.hashCode(), attempt2.hashCode());
        }

        @Test
        @DisplayName("Quiz attempts with different attemptId should not be equal")
        void testUnequalQuizAttemptsWithDifferentAttemptId() {
            QuizAttempt attempt1 = new QuizAttempt();
            attempt1.setAttemptId(100);
            QuizAttempt attempt2 = new QuizAttempt();
            attempt2.setAttemptId(200);
            
            assertNotEquals(attempt1, attempt2);
        }

        @Test
        @DisplayName("Quiz attempt should be equal to itself")
        void testQuizAttemptEqualToItself() {
            assertEquals(quizAttempt, quizAttempt);
        }

        @Test
        @DisplayName("Quiz attempt should not be equal to null")
        void testQuizAttemptNotEqualToNull() {
            assertNotEquals(quizAttempt, null);
        }

        @Test
        @DisplayName("Quiz attempt should not be equal to different class object")
        void testQuizAttemptNotEqualToDifferentClass() {
            String differentObject = "Not a QuizAttempt";
            assertNotEquals(quizAttempt, differentObject);
        }

        @Test
        @DisplayName("HashCode should be consistent")
        void testHashCodeConsistency() {
            quizAttempt.setAttemptId(123);
            int hashCode1 = quizAttempt.hashCode();
            int hashCode2 = quizAttempt.hashCode();
            assertEquals(hashCode1, hashCode2);
        }

        @Test
        @DisplayName("Default quiz attempts (attemptId=0) should be equal")
        void testDefaultQuizAttemptsEqual() {
            QuizAttempt attempt1 = new QuizAttempt();
            QuizAttempt attempt2 = new QuizAttempt();
            assertEquals(attempt1, attempt2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("ToString should contain all fields")
        void testToStringContent() {
            quizAttempt.setAttemptId(789);
            quizAttempt.setUserId(123);
            quizAttempt.setQuizId(456);
            quizAttempt.setScore(88.5);
            quizAttempt.setTotalQuestions(20);
            quizAttempt.setTimeTaken(900L);
            quizAttempt.setDateTaken(testDate);
            quizAttempt.setPractice(true);
            
            String result = quizAttempt.toString();
            
            assertTrue(result.contains("789")); // attemptId
            assertTrue(result.contains("123")); // userId
            assertTrue(result.contains("456")); // quizId
            assertTrue(result.contains("88.5")); // score
            assertTrue(result.contains("20")); // totalQuestions
            assertTrue(result.contains("900")); // timeTaken
            assertTrue(result.contains("true")); // isPractice
            assertTrue(result.contains("QuizAttempt"));
        }

        @Test
        @DisplayName("ToString should handle null values gracefully")
        void testToStringWithNullValues() {
            quizAttempt.setAttemptId(100);
            quizAttempt.setUserId(200);
            quizAttempt.setQuizId(300);
            quizAttempt.setScore(75.0);
            quizAttempt.setTotalQuestions(15);
            quizAttempt.setTimeTaken(600L);
            quizAttempt.setDateTaken(null); // Null date
            quizAttempt.setPractice(false);
            
            String result = quizAttempt.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("100"));
            assertTrue(result.contains("200"));
            assertTrue(result.contains("300"));
            assertTrue(result.contains("75.0"));
            assertTrue(result.contains("15"));
            assertTrue(result.contains("600"));
            assertTrue(result.contains("null")); // null dateTaken
            assertTrue(result.contains("false"));
        }

        @Test
        @DisplayName("ToString should not be null or empty")
        void testToStringNotNullOrEmpty() {
            String result = quizAttempt.toString();
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.length() > 0);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Data Integrity Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Quiz attempt should handle negative attemptId and userId")
        void testNegativeIds() {
            quizAttempt.setAttemptId(-1);
            quizAttempt.setUserId(-100);
            quizAttempt.setQuizId(-200);
            
            assertEquals(-1, quizAttempt.getAttemptId());
            assertEquals(-100, quizAttempt.getUserId());
            assertEquals(-200, quizAttempt.getQuizId());
        }

        @Test
        @DisplayName("Quiz attempt should handle very large IDs")
        void testLargeIds() {
            quizAttempt.setAttemptId(Integer.MAX_VALUE);
            quizAttempt.setUserId(Integer.MAX_VALUE - 1);
            quizAttempt.setQuizId(Integer.MAX_VALUE - 2);
            
            assertEquals(Integer.MAX_VALUE, quizAttempt.getAttemptId());
            assertEquals(Integer.MAX_VALUE - 1, quizAttempt.getUserId());
            assertEquals(Integer.MAX_VALUE - 2, quizAttempt.getQuizId());
        }

        @Test
        @DisplayName("Quiz attempt should handle negative scores")
        void testNegativeScore() {
            quizAttempt.setScore(-10.5);
            assertEquals(-10.5, quizAttempt.getScore());
        }

        @Test
        @DisplayName("Quiz attempt should handle zero and negative total questions")
        void testZeroAndNegativeTotalQuestions() {
            quizAttempt.setTotalQuestions(0);
            assertEquals(0, quizAttempt.getTotalQuestions());
            
            quizAttempt.setTotalQuestions(-5);
            assertEquals(-5, quizAttempt.getTotalQuestions());
        }

        @Test
        @DisplayName("Quiz attempt should handle very large time values")
        void testLargeTimeValues() {
            quizAttempt.setTimeTaken(Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, quizAttempt.getTimeTaken());
        }

        @Test
        @DisplayName("DateTaken should handle historical dates")
        void testHistoricalDateTaken() {
            Date historicalDate = new Date(0); // January 1, 1970
            quizAttempt.setDateTaken(historicalDate);
            assertEquals(historicalDate, quizAttempt.getDateTaken());
        }

        @Test
        @DisplayName("DateTaken should handle future dates")
        void testFutureDateTaken() {
            Date futureDate = new Date(System.currentTimeMillis() + 86400000); // Tomorrow
            quizAttempt.setDateTaken(futureDate);
            assertEquals(futureDate, quizAttempt.getDateTaken());
        }
    }

    @Nested
    @DisplayName("Real-world Scenario Tests")
    class ScenarioTests {

        @Test
        @DisplayName("Student takes a regular quiz scenario")
        void testStudentTakesRegularQuizScenario() {
            // Student 456 takes quiz 789 and scores 85%
            QuizAttempt attempt = new QuizAttempt(456, 789, 85.0, 20, 1200L, false);
            
            // Verify the attempt was created correctly
            assertEquals(456, attempt.getUserId());
            assertEquals(789, attempt.getQuizId());
            assertEquals(85.0, attempt.getScore());
            assertEquals(20, attempt.getTotalQuestions());
            assertEquals(1200L, attempt.getTimeTaken());
            assertFalse(attempt.isPractice());
            assertNotNull(attempt.getDateTaken());
            
            // Verify calculated values
            assertEquals(17, attempt.getCorrectAnswers()); // 85% of 20
            assertEquals("20:00", attempt.getFormattedTime()); // 1200 seconds = 20 minutes
        }

        @Test
        @DisplayName("Student takes a practice quiz scenario")
        void testStudentTakesPracticeQuizScenario() {
            // Student 123 takes a practice quiz
            QuizAttempt practiceAttempt = new QuizAttempt(123, 456, 70.0, 15, 900L, true);
            
            assertEquals(123, practiceAttempt.getUserId());
            assertEquals(456, practiceAttempt.getQuizId());
            assertEquals(70.0, practiceAttempt.getScore());
            assertEquals(15, practiceAttempt.getTotalQuestions());
            assertEquals(900L, practiceAttempt.getTimeTaken());
            assertTrue(practiceAttempt.isPractice());
            
            // Practice attempts should still calculate correct answers
            assertEquals(11, practiceAttempt.getCorrectAnswers()); // 70% of 15 rounded
            assertEquals("15:00", practiceAttempt.getFormattedTime());
        }

        @Test
        @DisplayName("Perfect score quick completion scenario")
        void testPerfectScoreQuickCompletionScenario() {
            // Excellent student completes quiz perfectly in minimal time
            QuizAttempt perfectAttempt = new QuizAttempt(999, 111, 100.0, 25, 300L, false);
            
            assertEquals(999, perfectAttempt.getUserId());
            assertEquals(111, perfectAttempt.getQuizId());
            assertEquals(100.0, perfectAttempt.getScore());
            assertEquals(25, perfectAttempt.getTotalQuestions());
            assertEquals(300L, perfectAttempt.getTimeTaken());
            assertFalse(perfectAttempt.isPractice());
            
            // Perfect score calculations
            assertEquals(25, perfectAttempt.getCorrectAnswers()); // 100% of 25
            assertEquals("5:00", perfectAttempt.getFormattedTime()); // 5 minutes
        }

        @Test
        @DisplayName("Quiz attempt update scenario")
        void testQuizAttemptUpdateScenario() {
            // Initially failed attempt
            QuizAttempt attempt = new QuizAttempt(200, 300, 45.0, 20, 1800L, false);
            
            // Verify initial state
            assertEquals(45.0, attempt.getScore());
            assertEquals(9, attempt.getCorrectAnswers()); // 45% of 20 rounded
            assertEquals("30:00", attempt.getFormattedTime());
            
            // Update after re-grading or correction
            attempt.setScore(78.0);
            attempt.setTimeTaken(1500L);
            
            // Verify updated state
            assertEquals(78.0, attempt.getScore());
            assertEquals(16, attempt.getCorrectAnswers()); // 78% of 20 rounded
            assertEquals("25:00", attempt.getFormattedTime());
        }

        @Test
        @DisplayName("Long quiz attempt scenario")
        void testLongQuizAttemptScenario() {
            // Student takes a comprehensive exam
            QuizAttempt longAttempt = new QuizAttempt(555, 666, 92.5, 50, 7200L, false);
            
            assertEquals(555, longAttempt.getUserId());
            assertEquals(666, longAttempt.getQuizId());
            assertEquals(92.5, longAttempt.getScore());
            assertEquals(50, longAttempt.getTotalQuestions());
            assertEquals(7200L, longAttempt.getTimeTaken());
            assertFalse(longAttempt.isPractice());
            
            // Long quiz calculations
            assertEquals(46, longAttempt.getCorrectAnswers()); // 92.5% of 50 rounded
            assertEquals("120:00", longAttempt.getFormattedTime()); // 2 hours
        }

        @Test
        @DisplayName("Quiz attempt with partial credit scenario")
        void testQuizAttemptWithPartialCreditScenario() {
            // Quiz that allows partial credit resulting in fractional percentage
            QuizAttempt partialAttempt = new QuizAttempt(777, 888, 76.67, 30, 2100L, false);
            
            assertEquals(777, partialAttempt.getUserId());
            assertEquals(888, partialAttempt.getQuizId());
            assertEquals(76.67, partialAttempt.getScore());
            assertEquals(30, partialAttempt.getTotalQuestions());
            assertEquals(2100L, partialAttempt.getTimeTaken());
            
            // Partial credit calculations
            assertEquals(23, partialAttempt.getCorrectAnswers()); // 76.67% of 30 = 23.001 rounded to 23
            assertEquals("35:00", partialAttempt.getFormattedTime());
        }
    }
} 