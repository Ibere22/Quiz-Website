package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Comprehensive test suite for the Question model class
 * Tests all constructors, getters, setters, helper methods, JSON handling, and type validation
 */
public class QuestionTest {
    
    private Question question;
    
    @BeforeEach
    void setUp() {
        question = new Question();
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Default constructor should initialize with default values")
        void testDefaultConstructor() {
            Question defaultQuestion = new Question();
            
            assertEquals(0, defaultQuestion.getQuestionId());
            assertEquals(0, defaultQuestion.getQuizId());
            assertEquals(Question.TYPE_QUESTION_RESPONSE, defaultQuestion.getQuestionType());
            assertNull(defaultQuestion.getQuestionText());
            assertNull(defaultQuestion.getCorrectAnswer());
            assertNull(defaultQuestion.getChoicesJson());
            assertNull(defaultQuestion.getImageUrl());
            assertEquals(1, defaultQuestion.getOrderNum());
        }
        
        @Test
        @DisplayName("Simple constructor should set basic question fields")
        void testSimpleConstructor() {
            int quizId = 123;
            String questionType = Question.TYPE_FILL_IN_BLANK;
            String questionText = "What is 2 + 2?";
            String correctAnswer = "4";
            int orderNum = 3;
            
            Question simpleQuestion = new Question(quizId, questionType, questionText, correctAnswer, orderNum);
            
            assertEquals(0, simpleQuestion.getQuestionId()); // Not set in simple constructor
            assertEquals(quizId, simpleQuestion.getQuizId());
            assertEquals(questionType, simpleQuestion.getQuestionType());
            assertEquals(questionText, simpleQuestion.getQuestionText());
            assertEquals(correctAnswer, simpleQuestion.getCorrectAnswer());
            assertNull(simpleQuestion.getChoicesJson());
            assertNull(simpleQuestion.getImageUrl());
            assertEquals(orderNum, simpleQuestion.getOrderNum());
        }
        
        @Test
        @DisplayName("Full constructor should set all fields correctly")
        void testFullConstructor() {
            int questionId = 456;
            int quizId = 123;
            String questionType = Question.TYPE_MULTIPLE_CHOICE;
            String questionText = "Which of the following is correct?";
            String correctAnswer = "Option A";
            String choicesJson = "[\"Option A\", \"Option B\", \"Option C\"]";
            String imageUrl = "https://example.com/image.jpg";
            int orderNum = 2;
            
            Question fullQuestion = new Question(questionId, quizId, questionType, questionText, 
                                               correctAnswer, choicesJson, imageUrl, orderNum);
            
            assertEquals(questionId, fullQuestion.getQuestionId());
            assertEquals(quizId, fullQuestion.getQuizId());
            assertEquals(questionType, fullQuestion.getQuestionType());
            assertEquals(questionText, fullQuestion.getQuestionText());
            assertEquals(correctAnswer, fullQuestion.getCorrectAnswer());
            assertEquals(choicesJson, fullQuestion.getChoicesJson());
            assertEquals(imageUrl, fullQuestion.getImageUrl());
            assertEquals(orderNum, fullQuestion.getOrderNum());
        }
    }
    
    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {
        
        @Test
        @DisplayName("QuestionId getter and setter should work correctly")
        void testQuestionIdGetterSetter() {
            int expectedId = 789;
            question.setQuestionId(expectedId);
            assertEquals(expectedId, question.getQuestionId());
        }
        
        @Test
        @DisplayName("QuizId getter and setter should work correctly")
        void testQuizIdGetterSetter() {
            int expectedQuizId = 101;
            question.setQuizId(expectedQuizId);
            assertEquals(expectedQuizId, question.getQuizId());
        }
        
        @Test
        @DisplayName("QuestionType getter and setter should work correctly")
        void testQuestionTypeGetterSetter() {
            String expectedType = Question.TYPE_MULTIPLE_CHOICE;
            question.setQuestionType(expectedType);
            assertEquals(expectedType, question.getQuestionType());
        }
        
        @Test
        @DisplayName("QuestionText getter and setter should work correctly")
        void testQuestionTextGetterSetter() {
            String expectedText = "What is the capital of France?";
            question.setQuestionText(expectedText);
            assertEquals(expectedText, question.getQuestionText());
        }
        
        @Test
        @DisplayName("CorrectAnswer getter and setter should work correctly")
        void testCorrectAnswerGetterSetter() {
            String expectedAnswer = "Paris";
            question.setCorrectAnswer(expectedAnswer);
            assertEquals(expectedAnswer, question.getCorrectAnswer());
        }
        
        @Test
        @DisplayName("ChoicesJson getter and setter should work correctly")
        void testChoicesJsonGetterSetter() {
            String expectedJson = "[\"Paris\", \"London\", \"Berlin\", \"Madrid\"]";
            question.setChoicesJson(expectedJson);
            assertEquals(expectedJson, question.getChoicesJson());
        }
        
        @Test
        @DisplayName("ImageUrl getter and setter should work correctly")
        void testImageUrlGetterSetter() {
            String expectedUrl = "https://example.com/question-image.png";
            question.setImageUrl(expectedUrl);
            assertEquals(expectedUrl, question.getImageUrl());
        }
        
        @Test
        @DisplayName("OrderNum getter and setter should work correctly")
        void testOrderNumGetterSetter() {
            int expectedOrder = 5;
            question.setOrderNum(expectedOrder);
            assertEquals(expectedOrder, question.getOrderNum());
        }
        
        @Test
        @DisplayName("All fields can be set to null where appropriate")
        void testNullValues() {
            question.setQuestionText(null);
            question.setCorrectAnswer(null);
            question.setChoicesJson(null);
            question.setImageUrl(null);
            question.setQuestionType(null);
            
            assertNull(question.getQuestionText());
            assertNull(question.getCorrectAnswer());
            assertNull(question.getChoicesJson());
            assertNull(question.getImageUrl());
            assertNull(question.getQuestionType());
        }
    }
    
    @Nested
    @DisplayName("Question Type Constants Tests")
    class QuestionTypeConstantsTests {
        
        @Test
        @DisplayName("Question type constants should have correct values")
        void testQuestionTypeConstants() {
            assertEquals("question-response", Question.TYPE_QUESTION_RESPONSE);
            assertEquals("fill-in-blank", Question.TYPE_FILL_IN_BLANK);
            assertEquals("multiple-choice", Question.TYPE_MULTIPLE_CHOICE);
            assertEquals("picture-response", Question.TYPE_PICTURE_RESPONSE);
        }
        
        @Test
        @DisplayName("Question type constants should be unique")
        void testQuestionTypeConstantsAreUnique() {
            String[] types = {
                Question.TYPE_QUESTION_RESPONSE,
                Question.TYPE_FILL_IN_BLANK,
                Question.TYPE_MULTIPLE_CHOICE,
                Question.TYPE_PICTURE_RESPONSE
            };
            
            for (int i = 0; i < types.length; i++) {
                for (int j = i + 1; j < types.length; j++) {
                    assertNotEquals(types[i], types[j], 
                        "Question type constants should be unique");
                }
            }
        }
    }
    
    @Nested
    @DisplayName("Question Type Helper Methods Tests")
    class QuestionTypeHelperTests {
        
        @Test
        @DisplayName("isQuestionResponse should work correctly")
        void testIsQuestionResponse() {
            question.setQuestionType(Question.TYPE_QUESTION_RESPONSE);
            assertTrue(question.isQuestionResponse());
            assertFalse(question.isMultipleChoice());
            assertFalse(question.isFillInBlank());
            assertFalse(question.isPictureResponse());
        }
        
        @Test
        @DisplayName("isFillInBlank should work correctly")
        void testIsFillInBlank() {
            question.setQuestionType(Question.TYPE_FILL_IN_BLANK);
            assertTrue(question.isFillInBlank());
            assertFalse(question.isQuestionResponse());
            assertFalse(question.isMultipleChoice());
            assertFalse(question.isPictureResponse());
        }
        
        @Test
        @DisplayName("isMultipleChoice should work correctly")
        void testIsMultipleChoice() {
            question.setQuestionType(Question.TYPE_MULTIPLE_CHOICE);
            assertTrue(question.isMultipleChoice());
            assertFalse(question.isQuestionResponse());
            assertFalse(question.isFillInBlank());
            assertFalse(question.isPictureResponse());
        }
        
        @Test
        @DisplayName("isPictureResponse should work correctly")
        void testIsPictureResponse() {
            question.setQuestionType(Question.TYPE_PICTURE_RESPONSE);
            assertTrue(question.isPictureResponse());
            assertFalse(question.isQuestionResponse());
            assertFalse(question.isFillInBlank());
            assertFalse(question.isMultipleChoice());
        }
        
        @Test
        @DisplayName("Type helper methods should handle null questionType")
        void testTypeHelperMethodsWithNull() {
            question.setQuestionType(null);
            assertFalse(question.isQuestionResponse());
            assertFalse(question.isFillInBlank());
            assertFalse(question.isMultipleChoice());
            assertFalse(question.isPictureResponse());
        }
        
        @Test
        @DisplayName("Type helper methods should handle unknown questionType")
        void testTypeHelperMethodsWithUnknownType() {
            question.setQuestionType("unknown-type");
            assertFalse(question.isQuestionResponse());
            assertFalse(question.isFillInBlank());
            assertFalse(question.isMultipleChoice());
            assertFalse(question.isPictureResponse());
        }
    }
    
    @Nested
    @DisplayName("JSON Choices Handling Tests")
    class JsonChoicesTests {
        
        @Test
        @DisplayName("setChoices should convert list to JSON correctly")
        void testSetChoices() {
            List<String> choices = Arrays.asList("Apple", "Banana", "Cherry", "Date");
            question.setChoices(choices);
            
            String choicesJson = question.getChoicesJson();
            assertNotNull(choicesJson);
            assertTrue(choicesJson.contains("Apple"));
            assertTrue(choicesJson.contains("Banana"));
            assertTrue(choicesJson.contains("Cherry"));
            assertTrue(choicesJson.contains("Date"));
        }
        
        @Test
        @DisplayName("getChoices should convert JSON to list correctly")
        void testGetChoices() {
            String choicesJson = "[\"Red\", \"Green\", \"Blue\", \"Yellow\"]";
            question.setChoicesJson(choicesJson);
            
            List<String> choices = question.getChoices();
            assertNotNull(choices);
            assertEquals(4, choices.size());
            assertTrue(choices.contains("Red"));
            assertTrue(choices.contains("Green"));
            assertTrue(choices.contains("Blue"));
            assertTrue(choices.contains("Yellow"));
        }
        
        @Test
        @DisplayName("setChoices and getChoices should be symmetric")
        void testChoicesSymmetry() {
            List<String> originalChoices = Arrays.asList("Option 1", "Option 2", "Option 3");
            question.setChoices(originalChoices);
            
            List<String> retrievedChoices = question.getChoices();
            assertEquals(originalChoices, retrievedChoices);
        }
        
        @Test
        @DisplayName("getChoices should return null for null choicesJson")
        void testGetChoicesWithNull() {
            question.setChoicesJson(null);
            assertNull(question.getChoices());
        }
        
        @Test
        @DisplayName("getChoices should return null for empty choicesJson")
        void testGetChoicesWithEmpty() {
            question.setChoicesJson("");
            assertNull(question.getChoices());
            
            question.setChoicesJson("   ");
            assertNull(question.getChoices());
        }
        
        @Test
        @DisplayName("setChoices should handle empty list")
        void testSetChoicesWithEmptyList() {
            List<String> emptyChoices = new ArrayList<>();
            question.setChoices(emptyChoices);
            
            List<String> retrievedChoices = question.getChoices();
            assertNotNull(retrievedChoices);
            assertEquals(0, retrievedChoices.size());
        }
        
        @Test
        @DisplayName("setChoices should handle null list")
        void testSetChoicesWithNull() {
            question.setChoices(null);
            assertEquals("null", question.getChoicesJson());
        }
    }
    
    @Nested
    @DisplayName("Equality and HashCode Tests")
    class EqualityTests {
        
        @Test
        @DisplayName("Questions with same questionId should be equal")
        void testEqualQuestionsWithSameQuestionId() {
            Question question1 = new Question(1, 101, Question.TYPE_MULTIPLE_CHOICE, "Q1", "A1", null, null, 1);
            Question question2 = new Question(1, 102, Question.TYPE_FILL_IN_BLANK, "Q2", "A2", null, null, 2);
            
            assertEquals(question1, question2);
            assertEquals(question1.hashCode(), question2.hashCode());
        }
        
        @Test
        @DisplayName("Questions with different questionId should not be equal")
        void testUnequalQuestionsWithDifferentQuestionId() {
            Question question1 = new Question(1, 101, Question.TYPE_MULTIPLE_CHOICE, "Q1", "A1", null, null, 1);
            Question question2 = new Question(2, 101, Question.TYPE_MULTIPLE_CHOICE, "Q1", "A1", null, null, 1);
            
            assertNotEquals(question1, question2);
        }
        
        @Test
        @DisplayName("Question should be equal to itself")
        void testQuestionEqualToItself() {
            Question testQuestion = new Question(1, 101, Question.TYPE_QUESTION_RESPONSE, "Test?", "Answer", null, null, 1);
            assertEquals(testQuestion, testQuestion);
        }
        
        @Test
        @DisplayName("Question should not be equal to null")
        void testQuestionNotEqualToNull() {
            Question testQuestion = new Question(1, 101, Question.TYPE_QUESTION_RESPONSE, "Test?", "Answer", null, null, 1);
            assertNotEquals(testQuestion, null);
        }
        
        @Test
        @DisplayName("Question should not be equal to different class object")
        void testQuestionNotEqualToDifferentClass() {
            Question testQuestion = new Question(1, 101, Question.TYPE_QUESTION_RESPONSE, "Test?", "Answer", null, null, 1);
            String differentObject = "I am not a question";
            assertNotEquals(testQuestion, differentObject);
        }
        
        @Test
        @DisplayName("HashCode should be consistent")
        void testHashCodeConsistency() {
            Question testQuestion = new Question(42, 101, Question.TYPE_PICTURE_RESPONSE, "Test?", "Answer", null, "image.jpg", 1);
            int firstHash = testQuestion.hashCode();
            int secondHash = testQuestion.hashCode();
            assertEquals(firstHash, secondHash);
        }
        
        @Test
        @DisplayName("Default questions (questionId=0) should be equal")
        void testDefaultQuestionsEqual() {
            Question question1 = new Question();
            Question question2 = new Question();
            assertEquals(question1, question2);
            assertEquals(question1.hashCode(), question2.hashCode());
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("ToString should contain all fields except choicesJson")
        void testToStringContent() {
            Question testQuestion = new Question(123, 456, Question.TYPE_MULTIPLE_CHOICE, 
                                               "What is Java?", "Programming Language", 
                                               "[\"Language\", \"Coffee\", \"Island\"]", 
                                               "java-logo.png", 3);
            String toString = testQuestion.toString();
            
            assertTrue(toString.contains("questionId=123"));
            assertTrue(toString.contains("quizId=456"));
            assertTrue(toString.contains("questionType='multiple-choice'"));
            assertTrue(toString.contains("questionText='What is Java?'"));
            assertTrue(toString.contains("correctAnswer='Programming Language'"));
            assertTrue(toString.contains("imageUrl='java-logo.png'"));
            assertTrue(toString.contains("orderNum=3"));
            
            // choicesJson should NOT be in toString to keep it clean
            assertFalse(toString.contains("choicesJson"));
        }
        
        @Test
        @DisplayName("ToString should handle null values gracefully")
        void testToStringWithNullValues() {
            Question testQuestion = new Question();
            testQuestion.setQuestionId(1);
            testQuestion.setQuizId(2);
            testQuestion.setQuestionText(null);
            testQuestion.setCorrectAnswer(null);
            testQuestion.setImageUrl(null);
            testQuestion.setQuestionType(null);
            
            String toString = testQuestion.toString();
            
            assertTrue(toString.contains("questionId=1"));
            assertTrue(toString.contains("quizId=2"));
            assertTrue(toString.contains("questionType='null'"));
            assertTrue(toString.contains("questionText='null'"));
            assertTrue(toString.contains("correctAnswer='null'"));
            assertTrue(toString.contains("imageUrl='null'"));
        }
        
        @Test
        @DisplayName("ToString should not be null or empty")
        void testToStringNotNullOrEmpty() {
            Question testQuestion = new Question();
            String toString = testQuestion.toString();
            assertNotNull(toString);
            assertFalse(toString.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Data Integrity Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Question should handle negative questionId and quizId")
        void testNegativeIds() {
            question.setQuestionId(-1);
            question.setQuizId(-5);
            assertEquals(-1, question.getQuestionId());
            assertEquals(-5, question.getQuizId());
        }
        
        @Test
        @DisplayName("Question should handle very large IDs")
        void testLargeIds() {
            int largeId = Integer.MAX_VALUE;
            question.setQuestionId(largeId);
            question.setQuizId(largeId);
            assertEquals(largeId, question.getQuestionId());
            assertEquals(largeId, question.getQuizId());
        }
        
        @Test
        @DisplayName("Question should handle empty strings")
        void testEmptyStrings() {
            question.setQuestionText("");
            question.setCorrectAnswer("");
            question.setImageUrl("");
            question.setQuestionType("");
            
            assertEquals("", question.getQuestionText());
            assertEquals("", question.getCorrectAnswer());
            assertEquals("", question.getImageUrl());
            assertEquals("", question.getQuestionType());
        }
        
        @Test
        @DisplayName("Question should handle very long strings")
        void testLongStrings() {
            String longString = "a".repeat(1000);
            
            question.setQuestionText(longString);
            question.setCorrectAnswer(longString);
            question.setImageUrl(longString);
            question.setQuestionType(longString);
            
            assertEquals(longString, question.getQuestionText());
            assertEquals(longString, question.getCorrectAnswer());
            assertEquals(longString, question.getImageUrl());
            assertEquals(longString, question.getQuestionType());
        }
        
        @Test
        @DisplayName("OrderNum should handle zero and negative values")
        void testOrderNumEdgeCases() {
            question.setOrderNum(0);
            assertEquals(0, question.getOrderNum());
            
            question.setOrderNum(-10);
            assertEquals(-10, question.getOrderNum());
            
            question.setOrderNum(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, question.getOrderNum());
        }
    }
    
    @Nested
    @DisplayName("Real-world Scenario Tests")
    class ScenarioTests {
        
        @Test
        @DisplayName("Multiple choice question creation scenario")
        void testMultipleChoiceQuestionScenario() {
            // Create a multiple choice question
            Question mcQuestion = new Question(1, Question.TYPE_MULTIPLE_CHOICE, 
                                             "What is the largest planet in our solar system?", 
                                             "Jupiter", 1);
            
            // Set up choices
            List<String> choices = Arrays.asList("Mars", "Jupiter", "Saturn", "Earth");
            mcQuestion.setChoices(choices);
            
            // Verify setup
            assertTrue(mcQuestion.isMultipleChoice());
            assertEquals("Jupiter", mcQuestion.getCorrectAnswer());
            assertNotNull(mcQuestion.getChoices());
            assertEquals(4, mcQuestion.getChoices().size());
            assertTrue(mcQuestion.getChoices().contains("Jupiter"));
        }
        
        @Test
        @DisplayName("Picture response question scenario")
        void testPictureResponseQuestionScenario() {
            // Create a picture response question
            Question pictureQuestion = new Question(2, Question.TYPE_PICTURE_RESPONSE,
                                                  "What programming language logo is shown?",
                                                  "Java", 2);
            pictureQuestion.setImageUrl("https://example.com/java-logo.png");
            
            // Verify setup
            assertTrue(pictureQuestion.isPictureResponse());
            assertEquals("https://example.com/java-logo.png", pictureQuestion.getImageUrl());
            assertEquals("Java", pictureQuestion.getCorrectAnswer());
        }
        
        @Test
        @DisplayName("Fill-in-blank question scenario")
        void testFillInBlankQuestionScenario() {
            // Create a fill-in-blank question
            Question fillBlankQuestion = new Question(3, Question.TYPE_FILL_IN_BLANK,
                                                    "The capital of France is ______.",
                                                    "Paris", 3);
            
            // Verify setup
            assertTrue(fillBlankQuestion.isFillInBlank());
            assertEquals("Paris", fillBlankQuestion.getCorrectAnswer());
            assertNull(fillBlankQuestion.getChoices()); // No choices for fill-in-blank
        }
        
        @Test
        @DisplayName("Question ordering in quiz scenario")
        void testQuestionOrderingScenario() {
            // Create multiple questions with different order numbers
            Question q1 = new Question(1, Question.TYPE_QUESTION_RESPONSE, "Question 1", "Answer 1", 1);
            Question q2 = new Question(1, Question.TYPE_QUESTION_RESPONSE, "Question 2", "Answer 2", 2);
            Question q3 = new Question(1, Question.TYPE_QUESTION_RESPONSE, "Question 3", "Answer 3", 3);
            
            // Verify ordering
            assertTrue(q1.getOrderNum() < q2.getOrderNum());
            assertTrue(q2.getOrderNum() < q3.getOrderNum());
            assertEquals(1, q1.getQuizId());
            assertEquals(1, q2.getQuizId());
            assertEquals(1, q3.getQuizId());
        }
        
        @Test
        @DisplayName("Question update scenario")
        void testQuestionUpdateScenario() {
            // Create initial question
            Question question = new Question(1, 101, Question.TYPE_QUESTION_RESPONSE, 
                                           "Original question?", "Original answer", null, null, 1);
            
            // Update question content
            question.setQuestionText("Updated question?");
            question.setCorrectAnswer("Updated answer");
            question.setQuestionType(Question.TYPE_MULTIPLE_CHOICE);
            
            // Add choices for the now multiple choice question
            List<String> choices = Arrays.asList("Updated answer", "Wrong answer 1", "Wrong answer 2");
            question.setChoices(choices);
            
            // Verify updates
            assertEquals("Updated question?", question.getQuestionText());
            assertEquals("Updated answer", question.getCorrectAnswer());
            assertTrue(question.isMultipleChoice());
            assertFalse(question.isQuestionResponse());
            assertNotNull(question.getChoices());
            assertEquals(3, question.getChoices().size());
        }
    }
} 