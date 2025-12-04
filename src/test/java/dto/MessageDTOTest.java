package dto;

import model.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for MessageDTO.
 * Covers all methods, branches, and edge cases for 100% line and branch coverage.
 */
class MessageDTOTest {

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
    @DisplayName("Test fromMessage static factory method with valid input")
    void testFromMessage() {
        Date now = createDate();
        Message message = new Message(7, 1, 2, Message.TYPE_CHALLENGE, "Take this quiz!", 3, now, false);
        UserDTO sender = createUser(1, "john");
        UserDTO receiver = createUser(2, "jane");
        QuizDTO quiz = createQuiz(3, sender);
        MessageDTO dto = MessageDTO.fromMessage(message, sender, receiver, quiz);
        assertNotNull(dto);
        assertEquals(message.getMessageId(), dto.messageId());
        assertEquals(sender, dto.sender());
        assertEquals(receiver, dto.receiver());
        assertEquals(message.getMessageType(), dto.messageType());
        assertEquals(message.getContent(), dto.content());
        assertEquals(quiz, dto.quiz());
        assertEquals(message.getDateSent(), dto.dateSent());
        assertEquals(message.isRead(), dto.isRead());
    }

    @Test
    @DisplayName("Test fromMessage static factory method with null input")
    void testFromMessageNull() {
        assertNull(MessageDTO.fromMessage(null, null, null, null));
    }

    @Test
    @DisplayName("Test isFriendRequest, isChallenge, isNote")
    void testTypeCheckers() {
        MessageDTO friendReq = new MessageDTO(1, null, null, MessageDTO.TYPE_FRIEND_REQUEST, "", null, new Date(), false);
        MessageDTO challenge = new MessageDTO(2, null, null, MessageDTO.TYPE_CHALLENGE, "", null, new Date(), false);
        MessageDTO note = new MessageDTO(3, null, null, MessageDTO.TYPE_NOTE, "", null, new Date(), false);
        MessageDTO unknown = new MessageDTO(4, null, null, "unknown_type", "", null, new Date(), false);
        assertTrue(friendReq.isFriendRequest());
        assertFalse(friendReq.isChallenge());
        assertFalse(friendReq.isNote());
        assertTrue(challenge.isChallenge());
        assertTrue(note.isNote());
        assertFalse(unknown.isFriendRequest());
        assertFalse(unknown.isChallenge());
        assertFalse(unknown.isNote());
    }

    @Test
    @DisplayName("Test getContentPreview for various lengths and null content")
    void testGetContentPreview() {
        MessageDTO dto = new MessageDTO(1, null, null, MessageDTO.TYPE_NOTE, "Hello, friend!", null, new Date(), false);
        assertEquals("Hello...", dto.getContentPreview(5));
        assertEquals("Hello, friend!", dto.getContentPreview(100));
        MessageDTO nullContent = new MessageDTO(2, null, null, MessageDTO.TYPE_NOTE, null, null, new Date(), false);
        assertEquals("", nullContent.getContentPreview(5));
    }

    @Test
    @DisplayName("Test getFormattedDateSent for various time differences and null date")
    void testGetFormattedDateSent() throws InterruptedException {
        // Just now
        MessageDTO justNow = new MessageDTO(1, null, null, MessageDTO.TYPE_NOTE, "", null, new Date(), false);
        assertTrue(justNow.getFormattedDateSent().contains("Just now") || justNow.getFormattedDateSent().contains("minute"));
        // 2 minutes ago
        Date twoMinutesAgo = new Date(System.currentTimeMillis() - 2 * 60 * 1000);
        MessageDTO twoMin = new MessageDTO(2, null, null, MessageDTO.TYPE_NOTE, "", null, twoMinutesAgo, false);
        assertTrue(twoMin.getFormattedDateSent().contains("minute"));
        // 2 hours ago
        Date twoHoursAgo = new Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000);
        MessageDTO twoHr = new MessageDTO(3, null, null, MessageDTO.TYPE_NOTE, "", null, twoHoursAgo, false);
        assertTrue(twoHr.getFormattedDateSent().contains("hour"));
        // 2 days ago
        Date twoDaysAgo = new Date(System.currentTimeMillis() - 2L * 24 * 60 * 60 * 1000);
        MessageDTO twoDay = new MessageDTO(4, null, null, MessageDTO.TYPE_NOTE, "", null, twoDaysAgo, false);
        assertTrue(twoDay.getFormattedDateSent().contains("day"));
        // Null date
        MessageDTO nullDate = new MessageDTO(5, null, null, MessageDTO.TYPE_NOTE, "", null, null, false);
        assertEquals("", nullDate.getFormattedDateSent());
    }

    @Test
    @DisplayName("Test equals, hashCode, and toString")
    void testEqualsHashCodeToString() {
        UserDTO sender = createUser(1, "john");
        UserDTO receiver = createUser(2, "jane");
        QuizDTO quiz = createQuiz(3, sender);
        Date now = createDate();
        MessageDTO dto1 = new MessageDTO(1, sender, receiver, MessageDTO.TYPE_NOTE, "Hi", quiz, now, false);
        MessageDTO dto2 = new MessageDTO(1, sender, receiver, MessageDTO.TYPE_NOTE, "Hi", quiz, now, false);
        MessageDTO dto3 = new MessageDTO(2, sender, receiver, MessageDTO.TYPE_NOTE, "Hi", quiz, now, false);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertTrue(dto1.toString().contains("MessageDTO"));
        assertTrue(dto1.toString().contains("john"));
    }

    @Test
    @DisplayName("Test immutability of MessageDTO")
    void testImmutability() {
        UserDTO sender = createUser(1, "john");
        UserDTO receiver = createUser(2, "jane");
        QuizDTO quiz = createQuiz(3, sender);
        Date now = createDate();
        MessageDTO dto = new MessageDTO(1, sender, receiver, MessageDTO.TYPE_NOTE, "Hi", quiz, now, false);
        // There are no setters, so fields cannot be changed
        assertEquals(1, dto.messageId());
        assertEquals(sender, dto.sender());
        assertEquals(receiver, dto.receiver());
        assertEquals(MessageDTO.TYPE_NOTE, dto.messageType());
        assertEquals("Hi", dto.content());
        assertEquals(quiz, dto.quiz());
        assertEquals(now, dto.dateSent());
        assertFalse(dto.isRead());
    }

    @Test
    @DisplayName("Test null and edge cases for all fields")
    void testNullAndEdgeCases() {
        MessageDTO dto = new MessageDTO(0, null, null, null, null, null, null, false);
        assertEquals(0, dto.messageId());
        assertNull(dto.sender());
        assertNull(dto.receiver());
        assertNull(dto.messageType());
        assertNull(dto.content());
        assertNull(dto.quiz());
        assertNull(dto.dateSent());
        assertFalse(dto.isRead());
    }

    @Test
    @DisplayName("Test MessageDTO with long and special character fields")
    void testLongAndSpecialFields() {
        String longContent = "C".repeat(500);
        String specialContent = "Msg_测试_!@#";
        UserDTO sender = createUser(1, "john");
        UserDTO receiver = createUser(2, "jane");
        QuizDTO quiz = createQuiz(3, sender);
        Date now = createDate();
        MessageDTO longDto = new MessageDTO(1, sender, receiver, MessageDTO.TYPE_NOTE, longContent, quiz, now, false);
        MessageDTO specialDto = new MessageDTO(2, sender, receiver, MessageDTO.TYPE_NOTE, specialContent, quiz, now, false);
        assertEquals(longContent, longDto.content());
        assertEquals(specialContent, specialDto.content());
    }

    @Test
    @DisplayName("Test MessageDTO with all boolean values for isRead")
    void testIsReadValues() {
        MessageDTO readDto = new MessageDTO(1, null, null, MessageDTO.TYPE_NOTE, "", null, new Date(), true);
        MessageDTO unreadDto = new MessageDTO(2, null, null, MessageDTO.TYPE_NOTE, "", null, new Date(), false);
        assertTrue(readDto.isRead());
        assertFalse(unreadDto.isRead());
    }
} 