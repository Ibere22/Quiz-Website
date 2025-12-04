package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the Achievement model class.
 * Tests all constructors, getters/setters, achievement type constants, helper methods,
 * equality, toString, edge cases, and real-world achievement scenarios.
 */
@DisplayName("Achievement Model Tests")
public class AchievementTest {

    private Achievement achievement;
    private Date testDate;

    @BeforeEach
    void setUp() {
        achievement = new Achievement();
        testDate = new Date();
    }

    @Nested
    @DisplayName("Achievement Type Constants Tests")
    class AchievementTypeConstantsTests {

        @Test
        @DisplayName("Achievement type constants should have correct values")
        void testAchievementTypeConstants() {
            assertEquals("amateur_author", Achievement.AMATEUR_AUTHOR);
            assertEquals("prolific_author", Achievement.PROLIFIC_AUTHOR);
            assertEquals("prodigious_author", Achievement.PRODIGIOUS_AUTHOR);
            assertEquals("quiz_machine", Achievement.QUIZ_MACHINE);
            assertEquals("i_am_the_greatest", Achievement.I_AM_THE_GREATEST);
            assertEquals("practice_makes_perfect", Achievement.PRACTICE_MAKES_PERFECT);
        }

        @Test
        @DisplayName("Achievement type constants should be static and accessible")
        void testAchievementTypeConstantsAccessibility() {
            // Should be able to access without instance
            assertNotNull(Achievement.AMATEUR_AUTHOR);
            assertNotNull(Achievement.PROLIFIC_AUTHOR);
            assertNotNull(Achievement.PRODIGIOUS_AUTHOR);
            assertNotNull(Achievement.QUIZ_MACHINE);
            assertNotNull(Achievement.I_AM_THE_GREATEST);
            assertNotNull(Achievement.PRACTICE_MAKES_PERFECT);
        }

        @Test
        @DisplayName("All achievement type constants should be unique")
        void testAchievementTypeConstantsUniqueness() {
            String[] types = {
                Achievement.AMATEUR_AUTHOR,
                Achievement.PROLIFIC_AUTHOR,
                Achievement.PRODIGIOUS_AUTHOR,
                Achievement.QUIZ_MACHINE,
                Achievement.I_AM_THE_GREATEST,
                Achievement.PRACTICE_MAKES_PERFECT
            };
            
            // Check that all types are different
            for (int i = 0; i < types.length; i++) {
                for (int j = i + 1; j < types.length; j++) {
                    assertNotEquals(types[i], types[j], 
                        "Achievement types should be unique: " + types[i] + " vs " + types[j]);
                }
            }
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize with default values")
        void testDefaultConstructor() {
            Achievement ach = new Achievement();
            
            assertEquals(0, ach.getAchievementId());
            assertEquals(0, ach.getUserId());
            assertNull(ach.getAchievementType());
            assertNotNull(ach.getDateEarned());
            assertNull(ach.getDescription());
            
            // Verify date is recent (within last second)
            long timeDiff = new Date().getTime() - ach.getDateEarned().getTime();
            assertTrue(timeDiff < 1000);
        }

        @Test
        @DisplayName("New achievement constructor should set basic achievement fields")
        void testNewAchievementConstructor() {
            Achievement ach = new Achievement(123, Achievement.AMATEUR_AUTHOR, "Created your first quiz!");
            
            assertEquals(0, ach.getAchievementId()); // Should be 0 (not set)
            assertEquals(123, ach.getUserId());
            assertEquals(Achievement.AMATEUR_AUTHOR, ach.getAchievementType());
            assertNotNull(ach.getDateEarned());
            assertEquals("Created your first quiz!", ach.getDescription());
            
            // Verify date is recent
            long timeDiff = new Date().getTime() - ach.getDateEarned().getTime();
            assertTrue(timeDiff < 1000);
        }

        @Test
        @DisplayName("Full constructor should set all fields correctly")
        void testFullConstructor() {
            Date specificDate = new Date(System.currentTimeMillis() - 86400000); // Yesterday
            
            Achievement ach = new Achievement(456, 789, Achievement.QUIZ_MACHINE, 
                                            specificDate, "Completed 10 quizzes!");
            
            assertEquals(456, ach.getAchievementId());
            assertEquals(789, ach.getUserId());
            assertEquals(Achievement.QUIZ_MACHINE, ach.getAchievementType());
            assertEquals(specificDate, ach.getDateEarned());
            assertEquals("Completed 10 quizzes!", ach.getDescription());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("AchievementId getter and setter should work correctly")
        void testAchievementIdGetterSetter() {
            achievement.setAchievementId(999);
            assertEquals(999, achievement.getAchievementId());
        }

        @Test
        @DisplayName("UserId getter and setter should work correctly")
        void testUserIdGetterSetter() {
            achievement.setUserId(123);
            assertEquals(123, achievement.getUserId());
        }

        @Test
        @DisplayName("AchievementType getter and setter should work correctly")
        void testAchievementTypeGetterSetter() {
            achievement.setAchievementType(Achievement.PROLIFIC_AUTHOR);
            assertEquals(Achievement.PROLIFIC_AUTHOR, achievement.getAchievementType());
        }

        @Test
        @DisplayName("DateEarned getter and setter should work correctly")
        void testDateEarnedGetterSetter() {
            achievement.setDateEarned(testDate);
            assertEquals(testDate, achievement.getDateEarned());
        }

        @Test
        @DisplayName("Description getter and setter should work correctly")
        void testDescriptionGetterSetter() {
            String description = "Great achievement!";
            achievement.setDescription(description);
            assertEquals(description, achievement.getDescription());
        }

        @Test
        @DisplayName("String fields can be set to null")
        void testNullStringValues() {
            achievement.setAchievementType(null);
            achievement.setDescription(null);
            
            assertNull(achievement.getAchievementType());
            assertNull(achievement.getDescription());
        }

        @Test
        @DisplayName("DateEarned can be set to null")
        void testNullDateEarned() {
            achievement.setDateEarned(null);
            assertNull(achievement.getDateEarned());
        }
    }

    @Nested
    @DisplayName("Helper Method Tests")
    class HelperMethodTests {

        @Test
        @DisplayName("getDisplayName should return correct display names for all achievement types")
        void testGetDisplayName() {
            achievement.setAchievementType(Achievement.AMATEUR_AUTHOR);
            assertEquals("Amateur Author", achievement.getDisplayName());
            
            achievement.setAchievementType(Achievement.PROLIFIC_AUTHOR);
            assertEquals("Prolific Author", achievement.getDisplayName());
            
            achievement.setAchievementType(Achievement.PRODIGIOUS_AUTHOR);
            assertEquals("Prodigious Author", achievement.getDisplayName());
            
            achievement.setAchievementType(Achievement.QUIZ_MACHINE);
            assertEquals("Quiz Machine", achievement.getDisplayName());
            
            achievement.setAchievementType(Achievement.I_AM_THE_GREATEST);
            assertEquals("I am the Greatest", achievement.getDisplayName());
            
            achievement.setAchievementType(Achievement.PRACTICE_MAKES_PERFECT);
            assertEquals("Practice Makes Perfect", achievement.getDisplayName());
        }

        @Test
        @DisplayName("getDisplayName should return achievement type for unknown types")
        void testGetDisplayNameUnknownType() {
            achievement.setAchievementType("unknown_achievement");
            assertEquals("unknown_achievement", achievement.getDisplayName());
            
            achievement.setAchievementType("custom_achievement");
            assertEquals("custom_achievement", achievement.getDisplayName());
        }

        @Test
        @DisplayName("getDisplayName should handle null achievement type")
        void testGetDisplayNameNullType() {
            achievement.setAchievementType(null);
            assertThrows(NullPointerException.class, () -> achievement.getDisplayName());
        }

        @Test
        @DisplayName("getDefaultDescription should return correct descriptions for all achievement types")
        void testGetDefaultDescription() {
            achievement.setAchievementType(Achievement.AMATEUR_AUTHOR);
            assertEquals("Created your first quiz!", achievement.getDefaultDescription());
            
            achievement.setAchievementType(Achievement.PROLIFIC_AUTHOR);
            assertEquals("Created 5 quizzes!", achievement.getDefaultDescription());
            
            achievement.setAchievementType(Achievement.PRODIGIOUS_AUTHOR);
            assertEquals("Created 10 quizzes!", achievement.getDefaultDescription());
            
            achievement.setAchievementType(Achievement.QUIZ_MACHINE);
            assertEquals("Took 10 quizzes!", achievement.getDefaultDescription());
            
            achievement.setAchievementType(Achievement.I_AM_THE_GREATEST);
            assertEquals("Achieved the highest score on a quiz!", achievement.getDefaultDescription());
            
            achievement.setAchievementType(Achievement.PRACTICE_MAKES_PERFECT);
            assertEquals("Took a quiz in practice mode!", achievement.getDefaultDescription());
        }

        @Test
        @DisplayName("getDefaultDescription should return custom description for unknown types")
        void testGetDefaultDescriptionUnknownType() {
            achievement.setAchievementType("unknown_achievement");
            achievement.setDescription("Custom description");
            assertEquals("Custom description", achievement.getDefaultDescription());
        }

        @Test
        @DisplayName("getDefaultDescription should handle null values")
        void testGetDefaultDescriptionNullValues() {
            achievement.setAchievementType("unknown_achievement");
            achievement.setDescription(null);
            assertNull(achievement.getDefaultDescription());
            
            achievement.setAchievementType(null);
            achievement.setDescription("Some description");
            assertThrows(NullPointerException.class, () -> achievement.getDefaultDescription());
        }

        @Test
        @DisplayName("Helper methods should handle case sensitivity")
        void testHelperMethodsCaseSensitivity() {
            // Wrong case should not match
            achievement.setAchievementType("AMATEUR_AUTHOR");
            assertEquals("AMATEUR_AUTHOR", achievement.getDisplayName()); // Returns as-is
            
            achievement.setAchievementType("Amateur_Author");
            assertEquals("Amateur_Author", achievement.getDisplayName()); // Returns as-is
        }
    }

    @Nested
    @DisplayName("Achievement Type Validation Tests")
    class AchievementTypeTests {

        @Test
        @DisplayName("Achievement should handle all valid achievement types")
        void testValidAchievementTypes() {
            String[] validTypes = {
                Achievement.AMATEUR_AUTHOR,
                Achievement.PROLIFIC_AUTHOR,
                Achievement.PRODIGIOUS_AUTHOR,
                Achievement.QUIZ_MACHINE,
                Achievement.I_AM_THE_GREATEST,
                Achievement.PRACTICE_MAKES_PERFECT
            };
            
            for (String type : validTypes) {
                achievement.setAchievementType(type);
                assertEquals(type, achievement.getAchievementType());
                assertNotNull(achievement.getDisplayName());
                assertNotNull(achievement.getDefaultDescription());
            }
        }

        @Test
        @DisplayName("Achievement should handle custom achievement types")
        void testCustomAchievementTypes() {
            String customType = "master_quiz_taker";
            achievement.setAchievementType(customType);
            achievement.setDescription("Took 100 quizzes!");
            
            assertEquals(customType, achievement.getAchievementType());
            assertEquals(customType, achievement.getDisplayName()); // Returns type as display name
            assertEquals("Took 100 quizzes!", achievement.getDefaultDescription()); // Returns custom description
        }
    }

    @Nested
    @DisplayName("Equality and HashCode Tests")
    class EqualityTests {

        @Test
        @DisplayName("Achievements with same achievementId should be equal")
        void testEqualAchievementsWithSameAchievementId() {
            Achievement ach1 = new Achievement();
            ach1.setAchievementId(100);
            Achievement ach2 = new Achievement();
            ach2.setAchievementId(100);
            
            assertEquals(ach1, ach2);
            assertEquals(ach1.hashCode(), ach2.hashCode());
        }

        @Test
        @DisplayName("Achievements with different achievementId should not be equal")
        void testUnequalAchievementsWithDifferentAchievementId() {
            Achievement ach1 = new Achievement();
            ach1.setAchievementId(100);
            Achievement ach2 = new Achievement();
            ach2.setAchievementId(200);
            
            assertNotEquals(ach1, ach2);
        }

        @Test
        @DisplayName("Achievement should be equal to itself")
        void testAchievementEqualToItself() {
            assertEquals(achievement, achievement);
        }

        @Test
        @DisplayName("Achievement should not be equal to null")
        void testAchievementNotEqualToNull() {
            assertNotEquals(achievement, null);
        }

        @Test
        @DisplayName("Achievement should not be equal to different class object")
        void testAchievementNotEqualToDifferentClass() {
            String differentObject = "Not an Achievement";
            assertNotEquals(achievement, differentObject);
        }

        @Test
        @DisplayName("HashCode should be consistent")
        void testHashCodeConsistency() {
            achievement.setAchievementId(123);
            int hashCode1 = achievement.hashCode();
            int hashCode2 = achievement.hashCode();
            assertEquals(hashCode1, hashCode2);
        }

        @Test
        @DisplayName("Default achievements (achievementId=0) should be equal")
        void testDefaultAchievementsEqual() {
            Achievement ach1 = new Achievement();
            Achievement ach2 = new Achievement();
            assertEquals(ach1, ach2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("ToString should contain all fields")
        void testToStringContent() {
            achievement.setAchievementId(789);
            achievement.setUserId(123);
            achievement.setAchievementType(Achievement.AMATEUR_AUTHOR);
            achievement.setDateEarned(testDate);
            achievement.setDescription("Test achievement description");
            
            String result = achievement.toString();
            
            assertTrue(result.contains("789")); // achievementId
            assertTrue(result.contains("123")); // userId
            assertTrue(result.contains("amateur_author")); // achievementType
            assertTrue(result.contains("Test achievement description")); // description
            assertTrue(result.contains("Achievement"));
        }

        @Test
        @DisplayName("ToString should handle null values gracefully")
        void testToStringWithNullValues() {
            achievement.setAchievementId(100);
            achievement.setUserId(200);
            achievement.setAchievementType(null); // Null type
            achievement.setDateEarned(null); // Null date
            achievement.setDescription(null); // Null description
            
            String result = achievement.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("100"));
            assertTrue(result.contains("200"));
            assertTrue(result.contains("null")); // null values
        }

        @Test
        @DisplayName("ToString should not be null or empty")
        void testToStringNotNullOrEmpty() {
            String result = achievement.toString();
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.length() > 0);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Data Integrity Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Achievement should handle negative achievementId and userId")
        void testNegativeIds() {
            achievement.setAchievementId(-1);
            achievement.setUserId(-100);
            
            assertEquals(-1, achievement.getAchievementId());
            assertEquals(-100, achievement.getUserId());
        }

        @Test
        @DisplayName("Achievement should handle very large IDs")
        void testLargeIds() {
            achievement.setAchievementId(Integer.MAX_VALUE);
            achievement.setUserId(Integer.MAX_VALUE - 1);
            
            assertEquals(Integer.MAX_VALUE, achievement.getAchievementId());
            assertEquals(Integer.MAX_VALUE - 1, achievement.getUserId());
        }

        @Test
        @DisplayName("Achievement should handle empty strings")
        void testEmptyStrings() {
            achievement.setAchievementType("");
            achievement.setDescription("");
            
            assertEquals("", achievement.getAchievementType());
            assertEquals("", achievement.getDescription());
            assertEquals("", achievement.getDisplayName()); // Empty string returns empty string
            assertEquals("", achievement.getDefaultDescription()); // Empty description
        }

        @Test
        @DisplayName("Achievement should handle very long strings")
        void testLongStrings() {
            String longType = "a".repeat(1000);
            String longDescription = "This is a very long achievement description. ".repeat(100);
            
            achievement.setAchievementType(longType);
            achievement.setDescription(longDescription);
            
            assertEquals(longType, achievement.getAchievementType());
            assertEquals(longDescription, achievement.getDescription());
        }

        @Test
        @DisplayName("DateEarned should handle historical dates")
        void testHistoricalDateEarned() {
            Date historicalDate = new Date(0); // January 1, 1970
            achievement.setDateEarned(historicalDate);
            assertEquals(historicalDate, achievement.getDateEarned());
        }

        @Test
        @DisplayName("DateEarned should handle future dates")
        void testFutureDateEarned() {
            Date futureDate = new Date(System.currentTimeMillis() + 86400000); // Tomorrow
            achievement.setDateEarned(futureDate);
            assertEquals(futureDate, achievement.getDateEarned());
        }
    }

    @Nested
    @DisplayName("Real-world Scenario Tests")
    class ScenarioTests {

        @Test
        @DisplayName("First quiz creation achievement scenario")
        void testFirstQuizCreationAchievementScenario() {
            // User 123 creates their first quiz
            Achievement firstQuiz = new Achievement(123, Achievement.AMATEUR_AUTHOR, 
                                                  "Congratulations on creating your first quiz!");
            
            // Verify the achievement was created correctly
            assertEquals(123, firstQuiz.getUserId());
            assertEquals(Achievement.AMATEUR_AUTHOR, firstQuiz.getAchievementType());
            assertEquals("Congratulations on creating your first quiz!", firstQuiz.getDescription());
            assertNotNull(firstQuiz.getDateEarned());
            
            // Verify display properties
            assertEquals("Amateur Author", firstQuiz.getDisplayName());
            assertEquals("Created your first quiz!", firstQuiz.getDefaultDescription());
        }

        @Test
        @DisplayName("Quiz taking machine achievement scenario")
        void testQuizTakingMachineAchievementScenario() {
            // User 456 becomes a quiz machine by taking 10 quizzes
            Achievement quizMachine = new Achievement(456, Achievement.QUIZ_MACHINE, 
                                                    "You've taken 10 quizzes! You're a quiz machine!");
            
            assertEquals(456, quizMachine.getUserId());
            assertEquals(Achievement.QUIZ_MACHINE, quizMachine.getAchievementType());
            assertTrue(quizMachine.getDescription().contains("10 quizzes"));
            
            // Verify display properties
            assertEquals("Quiz Machine", quizMachine.getDisplayName());
            assertEquals("Took 10 quizzes!", quizMachine.getDefaultDescription());
        }

        @Test
        @DisplayName("Highest score achievement scenario")
        void testHighestScoreAchievementScenario() {
            // User 789 achieves the highest score on a quiz
            Achievement greatestScore = new Achievement(789, Achievement.I_AM_THE_GREATEST, 
                                                      "You achieved the highest score on 'Java Fundamentals' with 98%!");
            
            assertEquals(789, greatestScore.getUserId());
            assertEquals(Achievement.I_AM_THE_GREATEST, greatestScore.getAchievementType());
            assertTrue(greatestScore.getDescription().contains("98%"));
            
            // Verify display properties
            assertEquals("I am the Greatest", greatestScore.getDisplayName());
            assertEquals("Achieved the highest score on a quiz!", greatestScore.getDefaultDescription());
        }

        @Test
        @DisplayName("Practice mode achievement scenario")
        void testPracticeModeAchievementScenario() {
            // User 321 tries practice mode for the first time
            Achievement practiceMode = new Achievement(321, Achievement.PRACTICE_MAKES_PERFECT, 
                                                     "Great job on trying practice mode!");
            
            assertEquals(321, practiceMode.getUserId());
            assertEquals(Achievement.PRACTICE_MAKES_PERFECT, practiceMode.getAchievementType());
            assertEquals("Great job on trying practice mode!", practiceMode.getDescription());
            
            // Verify display properties
            assertEquals("Practice Makes Perfect", practiceMode.getDisplayName());
            assertEquals("Took a quiz in practice mode!", practiceMode.getDefaultDescription());
        }

        @Test
        @DisplayName("Progressive author achievements scenario")
        void testProgressiveAuthorAchievementsScenario() {
            Date baseDate = new Date();
            
            // User progresses through author achievements
            Achievement amateur = new Achievement(100, 555, Achievement.AMATEUR_AUTHOR, 
                                                new Date(baseDate.getTime() - 86400000 * 7), // 1 week ago
                                                "Created first quiz!");
            
            Achievement prolific = new Achievement(200, 555, Achievement.PROLIFIC_AUTHOR, 
                                                 new Date(baseDate.getTime() - 86400000 * 3), // 3 days ago
                                                 "Created 5 quizzes!");
            
            Achievement prodigious = new Achievement(300, 555, Achievement.PRODIGIOUS_AUTHOR, 
                                                   baseDate, // Today
                                                   "Created 10 quizzes!");
            
            // Verify progression
            assertEquals(555, amateur.getUserId());
            assertEquals(555, prolific.getUserId());
            assertEquals(555, prodigious.getUserId());
            
            assertEquals("Amateur Author", amateur.getDisplayName());
            assertEquals("Prolific Author", prolific.getDisplayName());
            assertEquals("Prodigious Author", prodigious.getDisplayName());
            
            // Verify chronological order
            assertTrue(amateur.getDateEarned().before(prolific.getDateEarned()));
            assertTrue(prolific.getDateEarned().before(prodigious.getDateEarned()));
        }

        @Test
        @DisplayName("Custom achievement scenario")
        void testCustomAchievementScenario() {
            // School creates a custom achievement for excellent students
            Achievement customAchievement = new Achievement(888, "perfect_student", 
                                                          "Achieved 100% on all quizzes this semester!");
            
            assertEquals(888, customAchievement.getUserId());
            assertEquals("perfect_student", customAchievement.getAchievementType());
            assertEquals("Achieved 100% on all quizzes this semester!", customAchievement.getDescription());
            
            // Custom achievements return type as display name and description as default description
            assertEquals("perfect_student", customAchievement.getDisplayName());
            assertEquals("Achieved 100% on all quizzes this semester!", customAchievement.getDefaultDescription());
        }

        @Test
        @DisplayName("Achievement update scenario")
        void testAchievementUpdateScenario() {
            // Initial achievement
            Achievement ach = new Achievement(999, Achievement.AMATEUR_AUTHOR, "Created first quiz!");
            
            // Verify initial state
            assertEquals(Achievement.AMATEUR_AUTHOR, ach.getAchievementType());
            assertEquals("Created first quiz!", ach.getDescription());
            assertEquals("Amateur Author", ach.getDisplayName());
            
            // Update achievement (e.g., admin correction or enhancement)
            ach.setAchievementType(Achievement.PROLIFIC_AUTHOR);
            ach.setDescription("Actually created 5 quizzes - upgraded achievement!");
            
            // Verify updated state
            assertEquals(Achievement.PROLIFIC_AUTHOR, ach.getAchievementType());
            assertEquals("Actually created 5 quizzes - upgraded achievement!", ach.getDescription());
            assertEquals("Prolific Author", ach.getDisplayName());
            assertEquals("Created 5 quizzes!", ach.getDefaultDescription());
        }

        @Test
        @DisplayName("Achievement system integration scenario")
        void testAchievementSystemIntegrationScenario() {
            // Simulate a complete user achievement journey
            int userId = 777;
            Date startDate = new Date(System.currentTimeMillis() - 86400000 * 30); // 30 days ago
            
            // Create achievements in chronological order
            Achievement[] userAchievements = {
                new Achievement(101, userId, Achievement.AMATEUR_AUTHOR, startDate, "Created first quiz"),
                new Achievement(102, userId, Achievement.PRACTICE_MAKES_PERFECT, 
                               new Date(startDate.getTime() + 86400000 * 5), "Tried practice mode"),
                new Achievement(103, userId, Achievement.PROLIFIC_AUTHOR, 
                               new Date(startDate.getTime() + 86400000 * 15), "Created 5 quizzes"),
                new Achievement(104, userId, Achievement.QUIZ_MACHINE, 
                               new Date(startDate.getTime() + 86400000 * 20), "Took 10 quizzes"),
                new Achievement(105, userId, Achievement.I_AM_THE_GREATEST, 
                               new Date(startDate.getTime() + 86400000 * 25), "Highest score achieved"),
                new Achievement(106, userId, Achievement.PRODIGIOUS_AUTHOR, 
                               new Date(startDate.getTime() + 86400000 * 30), "Created 10 quizzes")
            };
            
            // Verify all achievements belong to the same user
            for (Achievement ach : userAchievements) {
                assertEquals(userId, ach.getUserId());
                assertNotNull(ach.getAchievementType());
                assertNotNull(ach.getDescription());
                assertNotNull(ach.getDateEarned());
                assertNotNull(ach.getDisplayName());
                assertNotNull(ach.getDefaultDescription());
            }
            
            // Verify achievement progression makes sense
            assertTrue(userAchievements[0].getAchievementType().equals(Achievement.AMATEUR_AUTHOR));
            assertTrue(userAchievements[2].getAchievementType().equals(Achievement.PROLIFIC_AUTHOR));
            assertTrue(userAchievements[5].getAchievementType().equals(Achievement.PRODIGIOUS_AUTHOR));
        }
    }
} 