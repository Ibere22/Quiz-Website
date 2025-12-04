package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

/**
 * Comprehensive test suite for the Quiz model class
 * Tests all constructors, getters, setters, boolean configurations, and quiz behaviors
 */
public class QuizTest {
    
    private Quiz quiz;
    private Date testDate;
    
    @BeforeEach
    void setUp() {
        quiz = new Quiz();
        testDate = new Date();
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Default constructor should initialize with default values")
        void testDefaultConstructor() {
            Quiz defaultQuiz = new Quiz();
            
            assertEquals(0, defaultQuiz.getQuizId());
            assertNull(defaultQuiz.getTitle());
            assertNull(defaultQuiz.getDescription());
            assertEquals(0, defaultQuiz.getCreatorId());
            assertFalse(defaultQuiz.isRandomOrder());
            assertTrue(defaultQuiz.isOnePage());
            assertFalse(defaultQuiz.isImmediateCorrection());
            assertFalse(defaultQuiz.isPracticeMode());
            assertNotNull(defaultQuiz.getCreatedDate());
            
            // Created date should be very recent (within last second)
            long timeDiff = Math.abs(new Date().getTime() - defaultQuiz.getCreatedDate().getTime());
            assertTrue(timeDiff < 1000, "Created date should be set to current time");
        }
        
        @Test
        @DisplayName("Creation constructor should set basic quiz fields")
        void testCreationConstructor() {
            String title = "Java Programming Quiz";
            String description = "Test your Java knowledge";
            int creatorId = 123;
            
            Quiz creationQuiz = new Quiz(title, description, creatorId);
            
            assertEquals(0, creationQuiz.getQuizId()); // Not set in creation constructor
            assertEquals(title, creationQuiz.getTitle());
            assertEquals(description, creationQuiz.getDescription());
            assertEquals(creatorId, creationQuiz.getCreatorId());
            assertFalse(creationQuiz.isRandomOrder());
            assertTrue(creationQuiz.isOnePage());
            assertFalse(creationQuiz.isImmediateCorrection());
            assertFalse(creationQuiz.isPracticeMode());
            assertNotNull(creationQuiz.getCreatedDate());
        }
        
        @Test
        @DisplayName("Full constructor should set all fields correctly")
        void testFullConstructor() {
            int quizId = 456;
            String title = "Advanced Math Quiz";
            String description = "Complex mathematical problems";
            int creatorId = 789;
            boolean randomOrder = true;
            boolean onePage = false;
            boolean immediateCorrection = true;
            boolean practiceMode = true;
            Date createdDate = new Date(System.currentTimeMillis() - 86400000); // 1 day ago
            
            Quiz fullQuiz = new Quiz(quizId, title, description, creatorId, 
                                   randomOrder, onePage, immediateCorrection, 
                                   practiceMode, createdDate);
            
            assertEquals(quizId, fullQuiz.getQuizId());
            assertEquals(title, fullQuiz.getTitle());
            assertEquals(description, fullQuiz.getDescription());
            assertEquals(creatorId, fullQuiz.getCreatorId());
            assertTrue(fullQuiz.isRandomOrder());
            assertFalse(fullQuiz.isOnePage());
            assertTrue(fullQuiz.isImmediateCorrection());
            assertTrue(fullQuiz.isPracticeMode());
            assertEquals(createdDate, fullQuiz.getCreatedDate());
        }
    }
    
    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {
        
        @Test
        @DisplayName("QuizId getter and setter should work correctly")
        void testQuizIdGetterSetter() {
            int expectedId = 999;
            quiz.setQuizId(expectedId);
            assertEquals(expectedId, quiz.getQuizId());
        }
        
        @Test
        @DisplayName("Title getter and setter should work correctly")
        void testTitleGetterSetter() {
            String expectedTitle = "My Awesome Quiz";
            quiz.setTitle(expectedTitle);
            assertEquals(expectedTitle, quiz.getTitle());
        }
        
        @Test
        @DisplayName("Description getter and setter should work correctly")
        void testDescriptionGetterSetter() {
            String expectedDescription = "This quiz tests your knowledge on various topics.";
            quiz.setDescription(expectedDescription);
            assertEquals(expectedDescription, quiz.getDescription());
        }
        
        @Test
        @DisplayName("CreatorId getter and setter should work correctly")
        void testCreatorIdGetterSetter() {
            int expectedCreatorId = 555;
            quiz.setCreatorId(expectedCreatorId);
            assertEquals(expectedCreatorId, quiz.getCreatorId());
        }
        
        @Test
        @DisplayName("RandomOrder getter and setter should work correctly")
        void testRandomOrderGetterSetter() {
            // Test setting to true
            quiz.setRandomOrder(true);
            assertTrue(quiz.isRandomOrder());
            
            // Test setting to false
            quiz.setRandomOrder(false);
            assertFalse(quiz.isRandomOrder());
        }
        
        @Test
        @DisplayName("OnePage getter and setter should work correctly")
        void testOnePageGetterSetter() {
            // Test setting to false (default is true)
            quiz.setOnePage(false);
            assertFalse(quiz.isOnePage());
            
            // Test setting to true
            quiz.setOnePage(true);
            assertTrue(quiz.isOnePage());
        }
        
        @Test
        @DisplayName("ImmediateCorrection getter and setter should work correctly")
        void testImmediateCorrectionGetterSetter() {
            // Test setting to true
            quiz.setImmediateCorrection(true);
            assertTrue(quiz.isImmediateCorrection());
            
            // Test setting to false
            quiz.setImmediateCorrection(false);
            assertFalse(quiz.isImmediateCorrection());
        }
        
        @Test
        @DisplayName("PracticeMode getter and setter should work correctly")
        void testPracticeModeGetterSetter() {
            // Test setting to true
            quiz.setPracticeMode(true);
            assertTrue(quiz.isPracticeMode());
            
            // Test setting to false
            quiz.setPracticeMode(false);
            assertFalse(quiz.isPracticeMode());
        }
        
        @Test
        @DisplayName("CreatedDate getter and setter should work correctly")
        void testCreatedDateGetterSetter() {
            Date expectedDate = new Date(System.currentTimeMillis() - 3600000); // 1 hour ago
            quiz.setCreatedDate(expectedDate);
            assertEquals(expectedDate, quiz.getCreatedDate());
        }
        
        @Test
        @DisplayName("String fields can be set to null")
        void testNullStringValues() {
            quiz.setTitle(null);
            quiz.setDescription(null);
            
            assertNull(quiz.getTitle());
            assertNull(quiz.getDescription());
        }
        
        @Test
        @DisplayName("CreatedDate can be set to null")
        void testNullCreatedDate() {
            quiz.setCreatedDate(null);
            assertNull(quiz.getCreatedDate());
        }
    }
    
    @Nested
    @DisplayName("Boolean Configuration Tests")
    class BooleanConfigurationTests {
        
        @Test
        @DisplayName("Default boolean configuration should be correct")
        void testDefaultBooleanConfiguration() {
            Quiz defaultQuiz = new Quiz();
            
            // Default configuration for a typical quiz
            assertFalse(defaultQuiz.isRandomOrder(), "Random order should be false by default");
            assertTrue(defaultQuiz.isOnePage(), "One page should be true by default");
            assertFalse(defaultQuiz.isImmediateCorrection(), "Immediate correction should be false by default");
            assertFalse(defaultQuiz.isPracticeMode(), "Practice mode should be false by default");
        }
        
        @Test
        @DisplayName("All boolean flags can be toggled independently")
        void testIndependentBooleanToggles() {
            // Test all combinations of boolean flags
            quiz.setRandomOrder(true);
            quiz.setOnePage(false);
            quiz.setImmediateCorrection(true);
            quiz.setPracticeMode(false);
            
            assertTrue(quiz.isRandomOrder());
            assertFalse(quiz.isOnePage());
            assertTrue(quiz.isImmediateCorrection());
            assertFalse(quiz.isPracticeMode());
            
            // Change some flags
            quiz.setRandomOrder(false);
            quiz.setPracticeMode(true);
            
            assertFalse(quiz.isRandomOrder());
            assertFalse(quiz.isOnePage()); // Should remain unchanged
            assertTrue(quiz.isImmediateCorrection()); // Should remain unchanged
            assertTrue(quiz.isPracticeMode());
        }
        
        @Test
        @DisplayName("Multiple boolean state changes should work correctly")
        void testMultipleBooleanStateChanges() {
            // Start with defaults
            assertFalse(quiz.isRandomOrder());
            
            // Toggle multiple times
            quiz.setRandomOrder(true);
            assertTrue(quiz.isRandomOrder());
            
            quiz.setRandomOrder(false);
            assertFalse(quiz.isRandomOrder());
            
            quiz.setRandomOrder(true);
            assertTrue(quiz.isRandomOrder());
        }
    }
    
    @Nested
    @DisplayName("Equality and HashCode Tests")
    class EqualityTests {
        
        @Test
        @DisplayName("Quizzes with same quizId should be equal")
        void testEqualQuizzesWithSameQuizId() {
            Quiz quiz1 = new Quiz(1, "Quiz 1", "Description 1", 101, false, true, false, false, new Date());
            Quiz quiz2 = new Quiz(1, "Quiz 2", "Description 2", 102, true, false, true, true, new Date());
            
            assertEquals(quiz1, quiz2);
            assertEquals(quiz1.hashCode(), quiz2.hashCode());
        }
        
        @Test
        @DisplayName("Quizzes with different quizId should not be equal")
        void testUnequalQuizzesWithDifferentQuizId() {
            Quiz quiz1 = new Quiz(1, "Same Quiz", "Same Description", 101, false, true, false, false, new Date());
            Quiz quiz2 = new Quiz(2, "Same Quiz", "Same Description", 101, false, true, false, false, new Date());
            
            assertNotEquals(quiz1, quiz2);
        }
        
        @Test
        @DisplayName("Quiz should be equal to itself")
        void testQuizEqualToItself() {
            Quiz testQuiz = new Quiz(1, "Test Quiz", "Test Description", 101, true, false, true, false, new Date());
            assertEquals(testQuiz, testQuiz);
        }
        
        @Test
        @DisplayName("Quiz should not be equal to null")
        void testQuizNotEqualToNull() {
            Quiz testQuiz = new Quiz(1, "Test Quiz", "Test Description", 101, true, false, true, false, new Date());
            assertNotEquals(testQuiz, null);
        }
        
        @Test
        @DisplayName("Quiz should not be equal to different class object")
        void testQuizNotEqualToDifferentClass() {
            Quiz testQuiz = new Quiz(1, "Test Quiz", "Test Description", 101, true, false, true, false, new Date());
            String differentObject = "I am not a quiz";
            assertNotEquals(testQuiz, differentObject);
        }
        
        @Test
        @DisplayName("HashCode should be consistent")
        void testHashCodeConsistency() {
            Quiz testQuiz = new Quiz(42, "Test Quiz", "Test Description", 101, true, false, true, false, new Date());
            int firstHash = testQuiz.hashCode();
            int secondHash = testQuiz.hashCode();
            assertEquals(firstHash, secondHash);
        }
        
        @Test
        @DisplayName("Default quizzes (quizId=0) should be equal")
        void testDefaultQuizzesEqual() {
            Quiz quiz1 = new Quiz();
            Quiz quiz2 = new Quiz();
            assertEquals(quiz1, quiz2);
            assertEquals(quiz1.hashCode(), quiz2.hashCode());
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("ToString should contain all fields")
        void testToStringContent() {
            Quiz testQuiz = new Quiz(123, "Java Fundamentals", "Basic Java concepts", 456, 
                                   true, false, true, false, testDate);
            String toString = testQuiz.toString();
            
            assertTrue(toString.contains("quizId=123"));
            assertTrue(toString.contains("title='Java Fundamentals'"));
            assertTrue(toString.contains("description='Basic Java concepts'"));
            assertTrue(toString.contains("creatorId=456"));
            assertTrue(toString.contains("randomOrder=true"));
            assertTrue(toString.contains("onePage=false"));
            assertTrue(toString.contains("immediateCorrection=true"));
            assertTrue(toString.contains("practiceMode=false"));
            assertTrue(toString.contains("createdDate=" + testDate.toString()));
        }
        
        @Test
        @DisplayName("ToString should handle null values gracefully")
        void testToStringWithNullValues() {
            Quiz testQuiz = new Quiz();
            testQuiz.setQuizId(1);
            testQuiz.setCreatorId(2);
            testQuiz.setTitle(null);
            testQuiz.setDescription(null);
            testQuiz.setCreatedDate(null);
            
            String toString = testQuiz.toString();
            
            assertTrue(toString.contains("quizId=1"));
            assertTrue(toString.contains("creatorId=2"));
            assertTrue(toString.contains("title='null'"));
            assertTrue(toString.contains("description='null'"));
            assertTrue(toString.contains("createdDate=null"));
        }
        
        @Test
        @DisplayName("ToString should not be null or empty")
        void testToStringNotNullOrEmpty() {
            Quiz testQuiz = new Quiz();
            String toString = testQuiz.toString();
            assertNotNull(toString);
            assertFalse(toString.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Data Integrity Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Quiz should handle negative quizId and creatorId")
        void testNegativeIds() {
            quiz.setQuizId(-1);
            quiz.setCreatorId(-5);
            assertEquals(-1, quiz.getQuizId());
            assertEquals(-5, quiz.getCreatorId());
        }
        
        @Test
        @DisplayName("Quiz should handle very large IDs")
        void testLargeIds() {
            int largeId = Integer.MAX_VALUE;
            quiz.setQuizId(largeId);
            quiz.setCreatorId(largeId);
            assertEquals(largeId, quiz.getQuizId());
            assertEquals(largeId, quiz.getCreatorId());
        }
        
        @Test
        @DisplayName("Quiz should handle empty strings")
        void testEmptyStrings() {
            quiz.setTitle("");
            quiz.setDescription("");
            
            assertEquals("", quiz.getTitle());
            assertEquals("", quiz.getDescription());
        }
        
        @Test
        @DisplayName("Quiz should handle very long strings")
        void testLongStrings() {
            String longString = "a".repeat(1000);
            
            quiz.setTitle(longString);
            quiz.setDescription(longString);
            
            assertEquals(longString, quiz.getTitle());
            assertEquals(longString, quiz.getDescription());
        }
        
        @Test
        @DisplayName("CreatedDate should handle historical dates")
        void testHistoricalCreatedDate() {
            Date historicalDate = new Date(0); // Unix epoch
            quiz.setCreatedDate(historicalDate);
            assertEquals(historicalDate, quiz.getCreatedDate());
        }
        
        @Test
        @DisplayName("CreatedDate should handle future dates")
        void testFutureCreatedDate() {
            Date futureDate = new Date(System.currentTimeMillis() + 86400000); // Tomorrow
            quiz.setCreatedDate(futureDate);
            assertEquals(futureDate, quiz.getCreatedDate());
        }
    }
    
    @Nested
    @DisplayName("Real-world Scenario Tests")
    class ScenarioTests {
        
        @Test
        @DisplayName("Standard quiz creation scenario")
        void testStandardQuizCreationScenario() {
            // Create a standard quiz for a classroom
            String title = "Chapter 5 Review";
            String description = "Review questions for Chapter 5: Object-Oriented Programming";
            int teacherId = 101;
            
            Quiz standardQuiz = new Quiz(title, description, teacherId);
            
            // Verify default settings are appropriate for standard quiz
            assertEquals(title, standardQuiz.getTitle());
            assertEquals(description, standardQuiz.getDescription());
            assertEquals(teacherId, standardQuiz.getCreatorId());
            assertFalse(standardQuiz.isRandomOrder()); // Sequential for learning
            assertTrue(standardQuiz.isOnePage()); // Simple interface
            assertFalse(standardQuiz.isImmediateCorrection()); // Review after completion
            assertFalse(standardQuiz.isPracticeMode()); // Real assessment
        }
        
        @Test
        @DisplayName("Practice quiz configuration scenario")
        void testPracticeQuizConfigurationScenario() {
            // Configure a quiz for practice mode
            Quiz practiceQuiz = new Quiz("Practice Test", "Practice for the final exam", 102);
            
            // Configure for practice
            practiceQuiz.setPracticeMode(true);
            practiceQuiz.setImmediateCorrection(true);
            practiceQuiz.setRandomOrder(true);
            practiceQuiz.setOnePage(false); // Multiple pages for better focus
            
            // Verify practice configuration
            assertTrue(practiceQuiz.isPracticeMode());
            assertTrue(practiceQuiz.isImmediateCorrection());
            assertTrue(practiceQuiz.isRandomOrder());
            assertFalse(practiceQuiz.isOnePage());
        }
        
        @Test
        @DisplayName("Formal assessment quiz scenario")
        void testFormalAssessmentQuizScenario() {
            // Create a formal assessment quiz
            Quiz assessmentQuiz = new Quiz("Midterm Exam", "Midterm examination covering units 1-3", 103);
            
            // Configure for formal assessment
            assessmentQuiz.setRandomOrder(true); // Prevent cheating
            assessmentQuiz.setOnePage(true); // All questions visible
            assessmentQuiz.setImmediateCorrection(false); // No feedback during exam
            assessmentQuiz.setPracticeMode(false); // Real exam
            
            // Verify assessment configuration
            assertTrue(assessmentQuiz.isRandomOrder());
            assertTrue(assessmentQuiz.isOnePage());
            assertFalse(assessmentQuiz.isImmediateCorrection());
            assertFalse(assessmentQuiz.isPracticeMode());
        }
        
        @Test
        @DisplayName("Quiz update scenario")
        void testQuizUpdateScenario() {
            // Create initial quiz
            Quiz quiz = new Quiz(1, "Original Title", "Original Description", 104, 
                               false, true, false, false, new Date());
            
            // Update quiz settings
            quiz.setTitle("Updated Quiz Title");
            quiz.setDescription("Updated description with more details");
            quiz.setRandomOrder(true);
            quiz.setImmediateCorrection(true);
            
            // Verify updates
            assertEquals("Updated Quiz Title", quiz.getTitle());
            assertEquals("Updated description with more details", quiz.getDescription());
            assertTrue(quiz.isRandomOrder());
            assertTrue(quiz.isImmediateCorrection());
            
            // Verify unchanged fields
            assertEquals(1, quiz.getQuizId());
            assertEquals(104, quiz.getCreatorId());
            assertTrue(quiz.isOnePage()); // Should remain unchanged
            assertFalse(quiz.isPracticeMode()); // Should remain unchanged
        }
        
        @Test
        @DisplayName("Quiz configuration combinations scenario")
        void testQuizConfigurationCombinationsScenario() {
            Quiz quiz = new Quiz("Flexible Quiz", "Testing different configurations", 105);
            
            // Test different logical combinations
            
            // Combination 1: Interactive learning
            quiz.setRandomOrder(false);
            quiz.setOnePage(false);
            quiz.setImmediateCorrection(true);
            quiz.setPracticeMode(true);
            
            assertFalse(quiz.isRandomOrder());
            assertFalse(quiz.isOnePage());
            assertTrue(quiz.isImmediateCorrection());
            assertTrue(quiz.isPracticeMode());
            
            // Combination 2: Secure testing
            quiz.setRandomOrder(true);
            quiz.setOnePage(true);
            quiz.setImmediateCorrection(false);
            quiz.setPracticeMode(false);
            
            assertTrue(quiz.isRandomOrder());
            assertTrue(quiz.isOnePage());
            assertFalse(quiz.isImmediateCorrection());
            assertFalse(quiz.isPracticeMode());
        }
        
        @Test
        @DisplayName("Quiz ownership transfer scenario")
        void testQuizOwnershipTransferScenario() {
            // Original creator
            int originalCreatorId = 201;
            Quiz quiz = new Quiz("Shared Quiz", "Quiz to be transferred", originalCreatorId);
            assertEquals(originalCreatorId, quiz.getCreatorId());
            
            // Transfer to new creator
            int newCreatorId = 202;
            quiz.setCreatorId(newCreatorId);
            assertEquals(newCreatorId, quiz.getCreatorId());
            
            // Verify other fields remain unchanged
            assertEquals("Shared Quiz", quiz.getTitle());
            assertEquals("Quiz to be transferred", quiz.getDescription());
        }
    }
} 