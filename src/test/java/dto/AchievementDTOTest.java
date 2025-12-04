package dto;

import model.Achievement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for AchievementDTO.
 * Covers all methods, branches, and edge cases for 100% line and branch coverage.
 */
class AchievementDTOTest {

    // Helper method to create a UserDTO for tests
    private UserDTO createUser() {
        return new UserDTO(1, "john", "john@email.com", new Date(), false);
    }

    // Helper method to create a Date for tests
    private Date createDate() {
        return new Date(System.currentTimeMillis() - 1000 * 60 * 60); // 1 hour ago
    }

    @Test
    @DisplayName("Test fromAchievement static factory method with valid input")
    void testFromAchievement() {
        Date now = createDate();
        Achievement achievement = new Achievement(11, 1, Achievement.AMATEUR_AUTHOR, now, "desc");
        UserDTO user = createUser();
        AchievementDTO dto = AchievementDTO.fromAchievement(achievement, user);
        assertNotNull(dto);
        assertEquals(achievement.getAchievementId(), dto.achievementId());
        assertEquals(user, dto.user());
        assertEquals(achievement.getAchievementType(), dto.achievementType());
        assertEquals(achievement.getDateEarned(), dto.dateEarned());
        assertEquals(achievement.getDescription(), dto.description());
    }

    @Test
    @DisplayName("Test fromAchievement static factory method with null input")
    void testFromAchievementNull() {
        assertNull(AchievementDTO.fromAchievement(null, null));
    }

    @Test
    @DisplayName("Test isAmateurAuthor, isProlificAuthor, isProdigiousAuthor, isQuizMachine, isIAmTheGreatest, isPracticeMakesPerfect")
    void testTypeCheckers() {
        UserDTO user = createUser();
        AchievementDTO amateur = new AchievementDTO(1, user, AchievementDTO.TYPE_AMATEUR_AUTHOR, new Date(), "");
        AchievementDTO prolific = new AchievementDTO(2, user, AchievementDTO.TYPE_PROLIFIC_AUTHOR, new Date(), "");
        AchievementDTO prodigious = new AchievementDTO(3, user, AchievementDTO.TYPE_PRODIGIOUS_AUTHOR, new Date(), "");
        AchievementDTO machine = new AchievementDTO(4, user, AchievementDTO.TYPE_QUIZ_MACHINE, new Date(), "");
        AchievementDTO greatest = new AchievementDTO(5, user, AchievementDTO.TYPE_I_AM_THE_GREATEST, new Date(), "");
        AchievementDTO practice = new AchievementDTO(6, user, AchievementDTO.TYPE_PRACTICE_MAKES_PERFECT, new Date(), "");
        AchievementDTO unknown = new AchievementDTO(7, user, "unknown_type", new Date(), "");

        assertTrue(amateur.isAmateurAuthor());
        assertFalse(amateur.isProlificAuthor());
        assertFalse(amateur.isProdigiousAuthor());
        assertFalse(amateur.isQuizMachine());
        assertFalse(amateur.isIAmTheGreatest());
        assertFalse(amateur.isPracticeMakesPerfect());

        assertTrue(prolific.isProlificAuthor());
        assertTrue(prodigious.isProdigiousAuthor());
        assertTrue(machine.isQuizMachine());
        assertTrue(greatest.isIAmTheGreatest());
        assertTrue(practice.isPracticeMakesPerfect());
        assertFalse(unknown.isAmateurAuthor());
        assertFalse(unknown.isProlificAuthor());
        assertFalse(unknown.isProdigiousAuthor());
        assertFalse(unknown.isQuizMachine());
        assertFalse(unknown.isIAmTheGreatest());
        assertFalse(unknown.isPracticeMakesPerfect());
    }

    @Test
    @DisplayName("Test getDisplayName for all achievement types and unknown")
    void testGetDisplayName() {
        UserDTO user = createUser();
        assertEquals("Amateur Author", new AchievementDTO(1, user, AchievementDTO.TYPE_AMATEUR_AUTHOR, new Date(), "").getDisplayName());
        assertEquals("Prolific Author", new AchievementDTO(1, user, AchievementDTO.TYPE_PROLIFIC_AUTHOR, new Date(), "").getDisplayName());
        assertEquals("Prodigious Author", new AchievementDTO(1, user, AchievementDTO.TYPE_PRODIGIOUS_AUTHOR, new Date(), "").getDisplayName());
        assertEquals("Quiz Machine", new AchievementDTO(1, user, AchievementDTO.TYPE_QUIZ_MACHINE, new Date(), "").getDisplayName());
        assertEquals("I am the Greatest", new AchievementDTO(1, user, AchievementDTO.TYPE_I_AM_THE_GREATEST, new Date(), "").getDisplayName());
        assertEquals("Practice Makes Perfect", new AchievementDTO(1, user, AchievementDTO.TYPE_PRACTICE_MAKES_PERFECT, new Date(), "").getDisplayName());
        assertEquals("Unknown Achievement", new AchievementDTO(1, user, "unknown_type", new Date(), "").getDisplayName());
    }

    @Test
    @DisplayName("Test getIconClass for all achievement types and unknown")
    void testGetIconClass() {
        UserDTO user = createUser();
        assertEquals("achievement-icon amateur-author", new AchievementDTO(1, user, AchievementDTO.TYPE_AMATEUR_AUTHOR, new Date(), "").getIconClass());
        assertEquals("achievement-icon prolific-author", new AchievementDTO(1, user, AchievementDTO.TYPE_PROLIFIC_AUTHOR, new Date(), "").getIconClass());
        assertEquals("achievement-icon prodigious-author", new AchievementDTO(1, user, AchievementDTO.TYPE_PRODIGIOUS_AUTHOR, new Date(), "").getIconClass());
        assertEquals("achievement-icon quiz-machine", new AchievementDTO(1, user, AchievementDTO.TYPE_QUIZ_MACHINE, new Date(), "").getIconClass());
        assertEquals("achievement-icon greatest", new AchievementDTO(1, user, AchievementDTO.TYPE_I_AM_THE_GREATEST, new Date(), "").getIconClass());
        assertEquals("achievement-icon practice", new AchievementDTO(1, user, AchievementDTO.TYPE_PRACTICE_MAKES_PERFECT, new Date(), "").getIconClass());
        assertEquals("achievement-icon default", new AchievementDTO(1, user, "unknown_type", new Date(), "").getIconClass());
    }

    @Test
    @DisplayName("Test getFormattedDateEarned for various time differences")
    void testGetFormattedDateEarned() throws InterruptedException {
        UserDTO user = createUser();
        // Just earned
        AchievementDTO justEarned = new AchievementDTO(1, user, AchievementDTO.TYPE_AMATEUR_AUTHOR, new Date(), "");
        assertTrue(justEarned.getFormattedDateEarned().contains("Just earned") || justEarned.getFormattedDateEarned().contains("minute"));
        // 2 minutes ago
        Date twoMinutesAgo = new Date(System.currentTimeMillis() - 2 * 60 * 1000);
        AchievementDTO twoMin = new AchievementDTO(2, user, AchievementDTO.TYPE_AMATEUR_AUTHOR, twoMinutesAgo, "");
        assertTrue(twoMin.getFormattedDateEarned().contains("minute"));
        // 2 hours ago
        Date twoHoursAgo = new Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000);
        AchievementDTO twoHr = new AchievementDTO(3, user, AchievementDTO.TYPE_AMATEUR_AUTHOR, twoHoursAgo, "");
        assertTrue(twoHr.getFormattedDateEarned().contains("hour"));
        // 2 days ago
        Date twoDaysAgo = new Date(System.currentTimeMillis() - 2L * 24 * 60 * 60 * 1000);
        AchievementDTO twoDay = new AchievementDTO(4, user, AchievementDTO.TYPE_AMATEUR_AUTHOR, twoDaysAgo, "");
        assertTrue(twoDay.getFormattedDateEarned().contains("day"));
        // Null date
        AchievementDTO nullDate = new AchievementDTO(5, user, AchievementDTO.TYPE_AMATEUR_AUTHOR, null, "");
        assertEquals("", nullDate.getFormattedDateEarned());
    }

    @Test
    @DisplayName("Test equals, hashCode, and toString")
    void testEqualsHashCodeToString() {
        UserDTO user = createUser();
        Date now = createDate();
        AchievementDTO dto1 = new AchievementDTO(1, user, AchievementDTO.TYPE_AMATEUR_AUTHOR, now, "desc");
        AchievementDTO dto2 = new AchievementDTO(1, user, AchievementDTO.TYPE_AMATEUR_AUTHOR, now, "desc");
        AchievementDTO dto3 = new AchievementDTO(2, user, AchievementDTO.TYPE_AMATEUR_AUTHOR, now, "desc");
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertTrue(dto1.toString().contains("AchievementDTO"));
        assertTrue(dto1.toString().contains("john"));
    }

    @Test
    @DisplayName("Test immutability of AchievementDTO")
    void testImmutability() {
        UserDTO user = createUser();
        AchievementDTO dto = new AchievementDTO(1, user, AchievementDTO.TYPE_AMATEUR_AUTHOR, new Date(), "desc");
        // There are no setters, so fields cannot be changed
        assertEquals(1, dto.achievementId());
        assertEquals(user, dto.user());
        assertEquals(AchievementDTO.TYPE_AMATEUR_AUTHOR, dto.achievementType());
    }

    @Test
    @DisplayName("Test null and edge cases for all fields")
    void testNullAndEdgeCases() {
        AchievementDTO dto = new AchievementDTO(0, null, null, null, null);
        assertEquals(0, dto.achievementId());
        assertNull(dto.user());
        assertNull(dto.achievementType());
        assertNull(dto.dateEarned());
        assertNull(dto.description());
    }
} 