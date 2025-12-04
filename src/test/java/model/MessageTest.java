package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the Message model class.
 * Tests all constructors, getters/setters, helper methods, message types,
 * equality, toString, edge cases, and real-world messaging scenarios.
 */
@DisplayName("Message Model Tests")
public class MessageTest {

    private Message message;
    private Date testDate;

    @BeforeEach
    void setUp() {
        message = new Message();
        testDate = new Date();
    }

    @Nested
    @DisplayName("Message Type Constants Tests")
    class MessageTypeConstantsTests {

        @Test
        @DisplayName("Message type constants should have correct values")
        void testMessageTypeConstants() {
            assertEquals("friend_request", Message.TYPE_FRIEND_REQUEST);
            assertEquals("challenge", Message.TYPE_CHALLENGE);
            assertEquals("note", Message.TYPE_NOTE);
        }

        @Test
        @DisplayName("Message type constants should be static and accessible")
        void testMessageTypeConstantsAccessibility() {
            // Should be able to access without instance
            assertNotNull(Message.TYPE_FRIEND_REQUEST);
            assertNotNull(Message.TYPE_CHALLENGE);
            assertNotNull(Message.TYPE_NOTE);
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize with default values")
        void testDefaultConstructor() {
            Message msg = new Message();
            
            assertEquals(0, msg.getMessageId());
            assertEquals(0, msg.getSenderId());
            assertEquals(0, msg.getReceiverId());
            assertNull(msg.getMessageType());
            assertNull(msg.getContent());
            assertNull(msg.getQuizId());
            assertNotNull(msg.getDateSent());
            assertFalse(msg.isRead());
            
            // Verify date is recent (within last second)
            long timeDiff = new Date().getTime() - msg.getDateSent().getTime();
            assertTrue(timeDiff < 1000);
        }

        @Test
        @DisplayName("Note message constructor should set basic message fields")
        void testNoteMessageConstructor() {
            Message noteMsg = new Message(101, 201, Message.TYPE_NOTE, "Hello there!");
            
            assertEquals(0, noteMsg.getMessageId()); // Should be 0 (not set)
            assertEquals(101, noteMsg.getSenderId());
            assertEquals(201, noteMsg.getReceiverId());
            assertEquals(Message.TYPE_NOTE, noteMsg.getMessageType());
            assertEquals("Hello there!", noteMsg.getContent());
            assertNull(noteMsg.getQuizId()); // Should be null for note messages
            assertNotNull(noteMsg.getDateSent());
            assertFalse(noteMsg.isRead());
            
            // Verify date is recent
            long timeDiff = new Date().getTime() - noteMsg.getDateSent().getTime();
            assertTrue(timeDiff < 1000);
        }

        @Test
        @DisplayName("Challenge message constructor should set challenge fields")
        void testChallengeMessageConstructor() {
            Message challengeMsg = new Message(102, 202, "I challenge you to this quiz!", 456);
            
            assertEquals(0, challengeMsg.getMessageId()); // Should be 0 (not set)
            assertEquals(102, challengeMsg.getSenderId());
            assertEquals(202, challengeMsg.getReceiverId());
            assertEquals(Message.TYPE_CHALLENGE, challengeMsg.getMessageType());
            assertEquals("I challenge you to this quiz!", challengeMsg.getContent());
            assertEquals(456, challengeMsg.getQuizId());
            assertNotNull(challengeMsg.getDateSent());
            assertFalse(challengeMsg.isRead());
        }

        @Test
        @DisplayName("Full constructor should set all fields correctly")
        void testFullConstructor() {
            Date specificDate = new Date(System.currentTimeMillis() - 86400000); // Yesterday
            
            Message fullMsg = new Message(301, 103, 203, Message.TYPE_FRIEND_REQUEST, 
                                        "Let's be friends!", null, specificDate, true);
            
            assertEquals(301, fullMsg.getMessageId());
            assertEquals(103, fullMsg.getSenderId());
            assertEquals(203, fullMsg.getReceiverId());
            assertEquals(Message.TYPE_FRIEND_REQUEST, fullMsg.getMessageType());
            assertEquals("Let's be friends!", fullMsg.getContent());
            assertNull(fullMsg.getQuizId());
            assertEquals(specificDate, fullMsg.getDateSent());
            assertTrue(fullMsg.isRead());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("MessageId getter and setter should work correctly")
        void testMessageIdGetterSetter() {
            message.setMessageId(999);
            assertEquals(999, message.getMessageId());
        }

        @Test
        @DisplayName("SenderId getter and setter should work correctly")
        void testSenderIdGetterSetter() {
            message.setSenderId(123);
            assertEquals(123, message.getSenderId());
        }

        @Test
        @DisplayName("ReceiverId getter and setter should work correctly")
        void testReceiverIdGetterSetter() {
            message.setReceiverId(456);
            assertEquals(456, message.getReceiverId());
        }

        @Test
        @DisplayName("MessageType getter and setter should work correctly")
        void testMessageTypeGetterSetter() {
            message.setMessageType(Message.TYPE_NOTE);
            assertEquals(Message.TYPE_NOTE, message.getMessageType());
        }

        @Test
        @DisplayName("Content getter and setter should work correctly")
        void testContentGetterSetter() {
            String content = "This is a test message";
            message.setContent(content);
            assertEquals(content, message.getContent());
        }

        @Test
        @DisplayName("QuizId getter and setter should work correctly")
        void testQuizIdGetterSetter() {
            message.setQuizId(789);
            assertEquals(789, message.getQuizId());
            
            message.setQuizId(null);
            assertNull(message.getQuizId());
        }

        @Test
        @DisplayName("DateSent getter and setter should work correctly")
        void testDateSentGetterSetter() {
            message.setDateSent(testDate);
            assertEquals(testDate, message.getDateSent());
        }

        @Test
        @DisplayName("Read getter and setter should work correctly")
        void testReadGetterSetter() {
            message.setRead(true);
            assertTrue(message.isRead());
            
            message.setRead(false);
            assertFalse(message.isRead());
        }

        @Test
        @DisplayName("String fields can be set to null")
        void testNullStringValues() {
            message.setMessageType(null);
            message.setContent(null);
            
            assertNull(message.getMessageType());
            assertNull(message.getContent());
        }

        @Test
        @DisplayName("DateSent can be set to null")
        void testNullDateSent() {
            message.setDateSent(null);
            assertNull(message.getDateSent());
        }
    }

    @Nested
    @DisplayName("Helper Method Tests")
    class HelperMethodTests {

        @Test
        @DisplayName("isFriendRequest should return true for friend request messages")
        void testIsFriendRequest() {
            message.setMessageType(Message.TYPE_FRIEND_REQUEST);
            assertTrue(message.isFriendRequest());
            
            message.setMessageType(Message.TYPE_CHALLENGE);
            assertFalse(message.isFriendRequest());
            
            message.setMessageType(Message.TYPE_NOTE);
            assertFalse(message.isFriendRequest());
            
            message.setMessageType(null);
            assertFalse(message.isFriendRequest());
            
            message.setMessageType("invalid_type");
            assertFalse(message.isFriendRequest());
        }

        @Test
        @DisplayName("isChallenge should return true for challenge messages")
        void testIsChallenge() {
            message.setMessageType(Message.TYPE_CHALLENGE);
            assertTrue(message.isChallenge());
            
            message.setMessageType(Message.TYPE_FRIEND_REQUEST);
            assertFalse(message.isChallenge());
            
            message.setMessageType(Message.TYPE_NOTE);
            assertFalse(message.isChallenge());
            
            message.setMessageType(null);
            assertFalse(message.isChallenge());
            
            message.setMessageType("invalid_type");
            assertFalse(message.isChallenge());
        }

        @Test
        @DisplayName("isNote should return true for note messages")
        void testIsNote() {
            message.setMessageType(Message.TYPE_NOTE);
            assertTrue(message.isNote());
            
            message.setMessageType(Message.TYPE_FRIEND_REQUEST);
            assertFalse(message.isNote());
            
            message.setMessageType(Message.TYPE_CHALLENGE);
            assertFalse(message.isNote());
            
            message.setMessageType(null);
            assertFalse(message.isNote());
            
            message.setMessageType("invalid_type");
            assertFalse(message.isNote());
        }

        @Test
        @DisplayName("markAsRead should set isRead to true")
        void testMarkAsRead() {
            // Initially not read
            assertFalse(message.isRead());
            
            message.markAsRead();
            assertTrue(message.isRead());
            
            // Should remain true if called again
            message.markAsRead();
            assertTrue(message.isRead());
        }

        @Test
        @DisplayName("Helper methods should handle case sensitivity")
        void testHelperMethodsCaseSensitivity() {
            message.setMessageType("FRIEND_REQUEST"); // Wrong case
            assertFalse(message.isFriendRequest());
            
            message.setMessageType("Challenge"); // Wrong case
            assertFalse(message.isChallenge());
            
            message.setMessageType("NOTE"); // Wrong case
            assertFalse(message.isNote());
        }
    }

    @Nested
    @DisplayName("Message Type Validation Tests")
    class MessageTypeTests {

        @Test
        @DisplayName("Message should handle all valid message types")
        void testValidMessageTypes() {
            // Friend request
            message.setMessageType(Message.TYPE_FRIEND_REQUEST);
            assertEquals(Message.TYPE_FRIEND_REQUEST, message.getMessageType());
            assertTrue(message.isFriendRequest());
            
            // Challenge
            message.setMessageType(Message.TYPE_CHALLENGE);
            assertEquals(Message.TYPE_CHALLENGE, message.getMessageType());
            assertTrue(message.isChallenge());
            
            // Note
            message.setMessageType(Message.TYPE_NOTE);
            assertEquals(Message.TYPE_NOTE, message.getMessageType());
            assertTrue(message.isNote());
        }

        @Test
        @DisplayName("Message should handle custom message types")
        void testCustomMessageTypes() {
            String customType = "announcement";
            message.setMessageType(customType);
            assertEquals(customType, message.getMessageType());
            
            // Custom types should not match helper methods
            assertFalse(message.isFriendRequest());
            assertFalse(message.isChallenge());
            assertFalse(message.isNote());
        }
    }

    @Nested
    @DisplayName("Equality and HashCode Tests")
    class EqualityTests {

        @Test
        @DisplayName("Messages with same messageId should be equal")
        void testEqualMessagesWithSameMessageId() {
            Message msg1 = new Message();
            msg1.setMessageId(100);
            Message msg2 = new Message();
            msg2.setMessageId(100);
            
            assertEquals(msg1, msg2);
            assertEquals(msg1.hashCode(), msg2.hashCode());
        }

        @Test
        @DisplayName("Messages with different messageId should not be equal")
        void testUnequalMessagesWithDifferentMessageId() {
            Message msg1 = new Message();
            msg1.setMessageId(100);
            Message msg2 = new Message();
            msg2.setMessageId(200);
            
            assertNotEquals(msg1, msg2);
        }

        @Test
        @DisplayName("Message should be equal to itself")
        void testMessageEqualToItself() {
            assertEquals(message, message);
        }

        @Test
        @DisplayName("Message should not be equal to null")
        void testMessageNotEqualToNull() {
            assertNotEquals(message, null);
        }

        @Test
        @DisplayName("Message should not be equal to different class object")
        void testMessageNotEqualToDifferentClass() {
            String differentObject = "Not a Message";
            assertNotEquals(message, differentObject);
        }

        @Test
        @DisplayName("HashCode should be consistent")
        void testHashCodeConsistency() {
            message.setMessageId(123);
            int hashCode1 = message.hashCode();
            int hashCode2 = message.hashCode();
            assertEquals(hashCode1, hashCode2);
        }

        @Test
        @DisplayName("Default messages (messageId=0) should be equal")
        void testDefaultMessagesEqual() {
            Message msg1 = new Message();
            Message msg2 = new Message();
            assertEquals(msg1, msg2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("ToString should contain all fields")
        void testToStringContent() {
            message.setMessageId(789);
            message.setSenderId(123);
            message.setReceiverId(456);
            message.setMessageType(Message.TYPE_NOTE);
            message.setContent("Test message content");
            message.setQuizId(999);
            message.setDateSent(testDate);
            message.setRead(true);
            
            String result = message.toString();
            
            assertTrue(result.contains("789")); // messageId
            assertTrue(result.contains("123")); // senderId
            assertTrue(result.contains("456")); // receiverId
            assertTrue(result.contains("note")); // messageType
            assertTrue(result.contains("Test message content")); // content
            assertTrue(result.contains("999")); // quizId
            assertTrue(result.contains("true")); // isRead
            assertTrue(result.contains("Message"));
        }

        @Test
        @DisplayName("ToString should handle null values gracefully")
        void testToStringWithNullValues() {
            message.setMessageId(100);
            message.setSenderId(200);
            message.setReceiverId(300);
            message.setMessageType(null); // Null type
            message.setContent(null); // Null content
            message.setQuizId(null); // Null quizId
            message.setDateSent(null); // Null date
            message.setRead(false);
            
            String result = message.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("100"));
            assertTrue(result.contains("200"));
            assertTrue(result.contains("300"));
            assertTrue(result.contains("null")); // null values
            assertTrue(result.contains("false"));
        }

        @Test
        @DisplayName("ToString should not be null or empty")
        void testToStringNotNullOrEmpty() {
            String result = message.toString();
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.length() > 0);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Data Integrity Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Message should handle negative messageId and userIds")
        void testNegativeIds() {
            message.setMessageId(-1);
            message.setSenderId(-100);
            message.setReceiverId(-200);
            
            assertEquals(-1, message.getMessageId());
            assertEquals(-100, message.getSenderId());
            assertEquals(-200, message.getReceiverId());
        }

        @Test
        @DisplayName("Message should handle very large IDs")
        void testLargeIds() {
            message.setMessageId(Integer.MAX_VALUE);
            message.setSenderId(Integer.MAX_VALUE - 1);
            message.setReceiverId(Integer.MAX_VALUE - 2);
            message.setQuizId(Integer.MAX_VALUE - 3);
            
            assertEquals(Integer.MAX_VALUE, message.getMessageId());
            assertEquals(Integer.MAX_VALUE - 1, message.getSenderId());
            assertEquals(Integer.MAX_VALUE - 2, message.getReceiverId());
            assertEquals(Integer.MAX_VALUE - 3, message.getQuizId());
        }

        @Test
        @DisplayName("Message should handle empty strings")
        void testEmptyStrings() {
            message.setMessageType("");
            message.setContent("");
            
            assertEquals("", message.getMessageType());
            assertEquals("", message.getContent());
            
            // Empty strings should not match helper methods
            assertFalse(message.isFriendRequest());
            assertFalse(message.isChallenge());
            assertFalse(message.isNote());
        }

        @Test
        @DisplayName("Message should handle very long strings")
        void testLongStrings() {
            String longType = "a".repeat(1000);
            String longContent = "This is a very long message content. ".repeat(100);
            
            message.setMessageType(longType);
            message.setContent(longContent);
            
            assertEquals(longType, message.getMessageType());
            assertEquals(longContent, message.getContent());
        }

        @Test
        @DisplayName("DateSent should handle historical dates")
        void testHistoricalDateSent() {
            Date historicalDate = new Date(0); // January 1, 1970
            message.setDateSent(historicalDate);
            assertEquals(historicalDate, message.getDateSent());
        }

        @Test
        @DisplayName("DateSent should handle future dates")
        void testFutureDateSent() {
            Date futureDate = new Date(System.currentTimeMillis() + 86400000); // Tomorrow
            message.setDateSent(futureDate);
            assertEquals(futureDate, message.getDateSent());
        }
    }

    @Nested
    @DisplayName("Real-world Scenario Tests")
    class ScenarioTests {

        @Test
        @DisplayName("Friend request message scenario")
        void testFriendRequestMessageScenario() {
            // User 123 sends friend request to user 456
            Message friendRequest = new Message(123, 456, Message.TYPE_FRIEND_REQUEST, 
                                              "Hi! I'd like to be friends. We're in the same class.");
            
            // Verify the message was created correctly
            assertEquals(123, friendRequest.getSenderId());
            assertEquals(456, friendRequest.getReceiverId());
            assertEquals(Message.TYPE_FRIEND_REQUEST, friendRequest.getMessageType());
            assertTrue(friendRequest.getContent().contains("friends"));
            assertNull(friendRequest.getQuizId()); // No quiz for friend requests
            assertNotNull(friendRequest.getDateSent());
            assertFalse(friendRequest.isRead());
            
            // Verify helper methods
            assertTrue(friendRequest.isFriendRequest());
            assertFalse(friendRequest.isChallenge());
            assertFalse(friendRequest.isNote());
            
            // Simulate reading the message
            friendRequest.markAsRead();
            assertTrue(friendRequest.isRead());
        }

        @Test
        @DisplayName("Quiz challenge message scenario")
        void testQuizChallengeMessageScenario() {
            // User 789 challenges user 987 to quiz 555
            Message challenge = new Message(789, 987, "I challenge you to this Java quiz! Let's see who knows more.", 555);
            
            assertEquals(789, challenge.getSenderId());
            assertEquals(987, challenge.getReceiverId());
            assertEquals(Message.TYPE_CHALLENGE, challenge.getMessageType());
            assertTrue(challenge.getContent().contains("challenge"));
            assertEquals(555, challenge.getQuizId());
            assertNotNull(challenge.getDateSent());
            assertFalse(challenge.isRead());
            
            // Verify helper methods
            assertFalse(challenge.isFriendRequest());
            assertTrue(challenge.isChallenge());
            assertFalse(challenge.isNote());
        }

        @Test
        @DisplayName("Personal note message scenario")
        void testPersonalNoteMessageScenario() {
            // User 111 sends a note to user 222
            Message note = new Message(111, 222, Message.TYPE_NOTE, 
                                     "Great job on the last quiz! Your score was impressive.");
            
            assertEquals(111, note.getSenderId());
            assertEquals(222, note.getReceiverId());
            assertEquals(Message.TYPE_NOTE, note.getMessageType());
            assertTrue(note.getContent().contains("Great job"));
            assertNull(note.getQuizId()); // No quiz for notes
            assertNotNull(note.getDateSent());
            assertFalse(note.isRead());
            
            // Verify helper methods
            assertFalse(note.isFriendRequest());
            assertFalse(note.isChallenge());
            assertTrue(note.isNote());
        }

        @Test
        @DisplayName("Message inbox management scenario")
        void testMessageInboxManagementScenario() {
            // Create multiple messages
            Message msg1 = new Message(100, 200, Message.TYPE_NOTE, "Message 1");
            Message msg2 = new Message(300, 200, Message.TYPE_FRIEND_REQUEST, "Message 2");
            Message msg3 = new Message(400, 200, "Challenge message", 123);
            
            // Initially all unread
            assertFalse(msg1.isRead());
            assertFalse(msg2.isRead());
            assertFalse(msg3.isRead());
            
            // Mark first two as read
            msg1.markAsRead();
            msg2.markAsRead();
            
            assertTrue(msg1.isRead());
            assertTrue(msg2.isRead());
            assertFalse(msg3.isRead()); // Still unread
            
            // Verify message types
            assertTrue(msg1.isNote());
            assertTrue(msg2.isFriendRequest());
            assertTrue(msg3.isChallenge());
        }

        @Test
        @DisplayName("Message update scenario")
        void testMessageUpdateScenario() {
            // Create initial message
            Message msg = new Message(500, 600, Message.TYPE_NOTE, "Original content");
            
            // Verify initial state
            assertEquals(Message.TYPE_NOTE, msg.getMessageType());
            assertEquals("Original content", msg.getContent());
            assertFalse(msg.isRead());
            assertTrue(msg.isNote());
            
            // Update message (e.g., editing draft or changing type)
            msg.setMessageType(Message.TYPE_FRIEND_REQUEST);
            msg.setContent("Updated: Let's be friends!");
            msg.markAsRead();
            
            // Verify updated state
            assertEquals(Message.TYPE_FRIEND_REQUEST, msg.getMessageType());
            assertEquals("Updated: Let's be friends!", msg.getContent());
            assertTrue(msg.isRead());
            assertTrue(msg.isFriendRequest());
            assertFalse(msg.isNote());
        }

        @Test
        @DisplayName("Challenge message with quiz details scenario")
        void testChallengeMessageWithQuizDetailsScenario() {
            // Detailed challenge message
            String challengeContent = "Hey! I just scored 95% on the 'Advanced Java Concepts' quiz. " +
                                    "Think you can beat that? The quiz has 25 questions and covers " +
                                    "inheritance, polymorphism, and design patterns. Good luck!";
            
            Message detailedChallenge = new Message(777, 888, challengeContent, 999);
            
            assertEquals(777, detailedChallenge.getSenderId());
            assertEquals(888, detailedChallenge.getReceiverId());
            assertEquals(Message.TYPE_CHALLENGE, detailedChallenge.getMessageType());
            assertTrue(detailedChallenge.getContent().contains("95%"));
            assertTrue(detailedChallenge.getContent().contains("25 questions"));
            assertEquals(999, detailedChallenge.getQuizId());
            assertTrue(detailedChallenge.isChallenge());
            
            // Simulate recipient reading and accepting challenge
            assertFalse(detailedChallenge.isRead());
            detailedChallenge.markAsRead();
            assertTrue(detailedChallenge.isRead());
        }

        @Test
        @DisplayName("System message scenario")
        void testSystemMessageScenario() {
            // System-generated message (sender = 0 for system)
            Message systemMsg = new Message(0, 555, "system_notification", 
                                          "Congratulations! You've earned the 'Quiz Master' achievement!");
            
            assertEquals(0, systemMsg.getSenderId()); // System sender
            assertEquals(555, systemMsg.getReceiverId());
            assertEquals("system_notification", systemMsg.getMessageType());
            assertTrue(systemMsg.getContent().contains("achievement"));
            assertNull(systemMsg.getQuizId());
            
            // System messages have custom type, so helper methods return false
            assertFalse(systemMsg.isFriendRequest());
            assertFalse(systemMsg.isChallenge());
            assertFalse(systemMsg.isNote());
        }
    }
} 