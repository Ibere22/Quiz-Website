package dao;

import model.Message;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MessageDAO class
 * Tests all CRUD operations, message-specific functionality, validation methods, and statistics
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageDAOTest {

    private static Connection connection;
    private static MessageDAO messageDAO;
    private static final int TEST_SENDER_ID = 1;
    private static final int TEST_RECEIVER_ID = 2;
    private static final int TEST_QUIZ_ID = 1;
    private static final String TEST_CONTENT = "Test message content";
    private static final String TEST_FRIEND_REQUEST_CONTENT = "Would you like to be friends?";
    private static final String TEST_CHALLENGE_CONTENT = "I challenge you to this quiz!";

    @BeforeAll
    static void setUpClass() throws SQLException {
        connection = DatabaseConnection.getConnection();
        messageDAO = new MessageDAO(connection);
        
        // Clean up any existing test data
        cleanUpTestData();
        
        // Create test users and quiz if they don't exist
        createTestData();
    }

    @AfterAll
    static void tearDownClass() throws SQLException {
        cleanUpTestData();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Ensure clean state before each test
        cleanUpTestMessages();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up after each test
        cleanUpTestMessages();
    }

    private static void cleanUpTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Delete messages from test users first
            stmt.executeUpdate("DELETE FROM messages WHERE sender_id IN (1, 2) OR receiver_id IN (1, 2)");
            // Note: We don't delete test users as they might be used by other tests
        }
    }

    private static void cleanUpTestMessages() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Delete ALL messages from test users to ensure clean state
            stmt.executeUpdate("DELETE FROM messages WHERE sender_id IN (1, 2) OR receiver_id IN (1, 2)");
        }
    }

    private static void createTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Insert test users if they don't exist
            stmt.executeUpdate("INSERT IGNORE INTO users (id, username, password_hash, email) VALUES " +
                "(1, 'testuser1', 'hash1', 'test1@example.com'), " +
                "(2, 'testuser2', 'hash2', 'test2@example.com')");
            
            // Insert test quiz if it doesn't exist
            stmt.executeUpdate("INSERT IGNORE INTO quizzes (id, title, description, creator_id) VALUES " +
                "(1, 'Test Quiz', 'Test quiz description', 1)");
        }
    }

    // ========================= CREATE OPERATION TESTS =========================

    @Test
    @Order(1)
    @DisplayName("Test create message with object")
    void testCreateMessage_Success() throws SQLException {
        // Arrange
        Message message = new Message(TEST_SENDER_ID, TEST_RECEIVER_ID, Message.TYPE_NOTE, TEST_CONTENT);

        // Act
        Message createdMessage = messageDAO.createMessage(message);

        // Assert
        assertNotNull(createdMessage, "Message should be created successfully");
        assertTrue(createdMessage.getMessageId() > 0, "Message ID should be generated");
        assertEquals(TEST_SENDER_ID, createdMessage.getSenderId());
        assertEquals(TEST_RECEIVER_ID, createdMessage.getReceiverId());
        assertEquals(Message.TYPE_NOTE, createdMessage.getMessageType());
        assertEquals(TEST_CONTENT, createdMessage.getContent());
        assertNotNull(createdMessage.getDateSent());
        assertFalse(createdMessage.isRead());
    }

    @Test
    @Order(2)
    @DisplayName("Test send note message")
    void testSendNote_Success() throws SQLException {
        // Act
        Message sentMessage = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, TEST_CONTENT);

        // Assert
        assertNotNull(sentMessage, "Note should be sent successfully");
        assertTrue(sentMessage.getMessageId() > 0, "Message ID should be generated");
        assertEquals(TEST_SENDER_ID, sentMessage.getSenderId());
        assertEquals(TEST_RECEIVER_ID, sentMessage.getReceiverId());
        assertEquals(Message.TYPE_NOTE, sentMessage.getMessageType());
        assertEquals(TEST_CONTENT, sentMessage.getContent());
        assertNull(sentMessage.getQuizId());
    }

    @Test
    @Order(3)
    @DisplayName("Test send friend request")
    void testSendFriendRequest_Success() throws SQLException {
        // Act
        Message sentMessage = messageDAO.sendFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID, TEST_FRIEND_REQUEST_CONTENT);

        // Assert
        assertNotNull(sentMessage, "Friend request should be sent successfully");
        assertTrue(sentMessage.getMessageId() > 0, "Message ID should be generated");
        assertEquals(TEST_SENDER_ID, sentMessage.getSenderId());
        assertEquals(TEST_RECEIVER_ID, sentMessage.getReceiverId());
        assertEquals(Message.TYPE_FRIEND_REQUEST, sentMessage.getMessageType());
        assertEquals(TEST_FRIEND_REQUEST_CONTENT, sentMessage.getContent());
        assertNull(sentMessage.getQuizId());
    }

    @Test
    @Order(4)
    @DisplayName("Test send challenge")
    void testSendChallenge_Success() throws SQLException {
        // Act
        Message sentMessage = messageDAO.sendChallenge(TEST_SENDER_ID, TEST_RECEIVER_ID, TEST_CHALLENGE_CONTENT, TEST_QUIZ_ID);

        // Assert
        assertNotNull(sentMessage, "Challenge should be sent successfully");
        assertTrue(sentMessage.getMessageId() > 0, "Message ID should be generated");
        assertEquals(TEST_SENDER_ID, sentMessage.getSenderId());
        assertEquals(TEST_RECEIVER_ID, sentMessage.getReceiverId());
        assertEquals(Message.TYPE_CHALLENGE, sentMessage.getMessageType());
        assertEquals(TEST_CHALLENGE_CONTENT, sentMessage.getContent());
        assertEquals(TEST_QUIZ_ID, sentMessage.getQuizId().intValue());
    }

    @Test
    @Order(5)
    @DisplayName("Test create message with null quiz ID")
    void testCreateMessage_NullQuizId_Success() throws SQLException {
        // Arrange
        Message message = new Message(TEST_SENDER_ID, TEST_RECEIVER_ID, Message.TYPE_NOTE, TEST_CONTENT);
        message.setQuizId(null);

        // Act
        Message createdMessage = messageDAO.createMessage(message);

        // Assert
        assertNotNull(createdMessage, "Message should be created successfully");
        assertNull(createdMessage.getQuizId(), "Quiz ID should be null");
    }

    // ========================= READ OPERATION TESTS =========================

    @Test
    @Order(6)
    @DisplayName("Test find message by ID")
    void testFindById_ExistingMessage_Success() throws SQLException {
        // Arrange
        Message createdMessage = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, TEST_CONTENT);

        // Act
        Message foundMessage = messageDAO.findById(createdMessage.getMessageId());

        // Assert
        assertNotNull(foundMessage, "Message should be found");
        assertEquals(createdMessage.getMessageId(), foundMessage.getMessageId());
        assertEquals(TEST_SENDER_ID, foundMessage.getSenderId());
        assertEquals(TEST_RECEIVER_ID, foundMessage.getReceiverId());
        assertEquals(Message.TYPE_NOTE, foundMessage.getMessageType());
        assertEquals(TEST_CONTENT, foundMessage.getContent());
    }

    @Test
    @Order(7)
    @DisplayName("Test find message by non-existent ID")
    void testFindById_NonExistentMessage_ReturnsNull() throws SQLException {
        // Act
        Message foundMessage = messageDAO.findById(99999);

        // Assert
        assertNull(foundMessage, "Should return null for non-existent message");
    }

    @Test
    @Order(8)
    @DisplayName("Test get received messages")
    void testGetReceivedMessages_Success() throws SQLException {
        // Arrange
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 1");
        messageDAO.sendFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 2");
        messageDAO.sendNote(TEST_RECEIVER_ID, TEST_SENDER_ID, "Message 3"); // Different receiver

        // Act
        List<Message> receivedMessages = messageDAO.getReceivedMessages(TEST_RECEIVER_ID);

        // Assert
        assertEquals(2, receivedMessages.size(), "Should return 2 messages for receiver");
        
        // Verify all messages belong to the correct receiver
        assertTrue(receivedMessages.stream().allMatch(m -> m.getReceiverId() == TEST_RECEIVER_ID));
        
        // Verify messages are sorted by date_sent DESC (most recent first)
        for (int i = 0; i < receivedMessages.size() - 1; i++) {
            assertTrue(receivedMessages.get(i).getDateSent().compareTo(receivedMessages.get(i + 1).getDateSent()) >= 0,
                "Messages should be sorted by date sent in descending order");
        }
    }

    @Test
    @Order(9)
    @DisplayName("Test get sent messages")
    void testGetSentMessages_Success() throws SQLException {
        // Arrange
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 1");
        messageDAO.sendFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 2");
        messageDAO.sendNote(TEST_RECEIVER_ID, TEST_SENDER_ID, "Message 3"); // Different sender

        // Act
        List<Message> sentMessages = messageDAO.getSentMessages(TEST_SENDER_ID);

        // Assert
        assertEquals(2, sentMessages.size(), "Should return 2 messages sent by sender");
        
        // Verify all messages belong to the correct sender
        assertTrue(sentMessages.stream().allMatch(m -> m.getSenderId() == TEST_SENDER_ID));
    }

    @Test
    @Order(10)
    @DisplayName("Test get conversation between two users")
    void testGetConversation_Success() throws SQLException {
        // Arrange
        Message msg1 = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 1");
        // Add delay to ensure different timestamps
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        Message msg2 = messageDAO.sendNote(TEST_RECEIVER_ID, TEST_SENDER_ID, "Message 2");
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        Message msg3 = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 3");

        // Act
        List<Message> conversation = messageDAO.getConversation(TEST_SENDER_ID, TEST_RECEIVER_ID);

        // Assert
        assertEquals(3, conversation.size(), "Should return 3 messages in conversation");
        
        // Verify messages are sorted by date_sent ASC (chronological order)
        for (int i = 0; i < conversation.size() - 1; i++) {
            assertTrue(conversation.get(i).getDateSent().compareTo(conversation.get(i + 1).getDateSent()) <= 0,
                "Conversation should be sorted by date sent in ascending order");
        }
        
        // Verify all messages involve the two users
        assertTrue(conversation.stream().allMatch(m -> 
            (m.getSenderId() == TEST_SENDER_ID && m.getReceiverId() == TEST_RECEIVER_ID) ||
            (m.getSenderId() == TEST_RECEIVER_ID && m.getReceiverId() == TEST_SENDER_ID)));
    }

    @Test
    @Order(11)
    @DisplayName("Test get messages by type")
    void testGetMessagesByType_Success() throws SQLException {
        // Arrange
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Note 1");
        messageDAO.sendFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID, "Friend Request 1");
        messageDAO.sendChallenge(TEST_SENDER_ID, TEST_RECEIVER_ID, "Challenge 1", TEST_QUIZ_ID);
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Note 2");

        // Act
        List<Message> noteMessages = messageDAO.getMessagesByType(TEST_RECEIVER_ID, Message.TYPE_NOTE);
        List<Message> friendRequestMessages = messageDAO.getMessagesByType(TEST_RECEIVER_ID, Message.TYPE_FRIEND_REQUEST);
        List<Message> challengeMessages = messageDAO.getMessagesByType(TEST_RECEIVER_ID, Message.TYPE_CHALLENGE);

        // Assert
        assertEquals(2, noteMessages.size(), "Should return 2 note messages");
        assertEquals(1, friendRequestMessages.size(), "Should return 1 friend request message");
        assertEquals(1, challengeMessages.size(), "Should return 1 challenge message");
        
        // Verify message types
        assertTrue(noteMessages.stream().allMatch(m -> m.getMessageType().equals(Message.TYPE_NOTE)));
        assertTrue(friendRequestMessages.stream().allMatch(m -> m.getMessageType().equals(Message.TYPE_FRIEND_REQUEST)));
        assertTrue(challengeMessages.stream().allMatch(m -> m.getMessageType().equals(Message.TYPE_CHALLENGE)));
    }

    @Test
    @Order(12)
    @DisplayName("Test get unread messages")
    void testGetUnreadMessages_Success() throws SQLException {
        // Arrange
        Message msg1 = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Unread Message 1");
        Message msg2 = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Unread Message 2");
        Message msg3 = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Read Message");
        
        // Mark one message as read
        messageDAO.markAsRead(msg3.getMessageId());

        // Act
        List<Message> unreadMessages = messageDAO.getUnreadMessages(TEST_RECEIVER_ID);

        // Assert
        assertEquals(2, unreadMessages.size(), "Should return 2 unread messages");
        
        // Verify all messages are unread
        assertTrue(unreadMessages.stream().allMatch(m -> !m.isRead()));
    }

    @Test
    @Order(13)
    @DisplayName("Test get received messages with pagination")
    void testGetReceivedMessages_WithPagination_Success() throws SQLException {
        // Arrange
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 1");
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 2");
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 3");
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 4");

        // Act
        List<Message> firstPage = messageDAO.getReceivedMessages(TEST_RECEIVER_ID, 0, 2);
        List<Message> secondPage = messageDAO.getReceivedMessages(TEST_RECEIVER_ID, 2, 2);

        // Assert
        assertEquals(2, firstPage.size(), "First page should have 2 messages");
        assertEquals(2, secondPage.size(), "Second page should have 2 messages");
    }

    @Test
    @Order(14)
    @DisplayName("Test get recent messages")
    void testGetRecentMessages_Success() throws SQLException {
        // Arrange
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Recent Message 1");
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Recent Message 2");

        // Act
        List<Message> recentMessages = messageDAO.getRecentMessages(TEST_RECEIVER_ID, 7, 10); // Last 7 days, max 10 results

        // Assert
        assertTrue(recentMessages.size() >= 2, "Should return at least 2 recent messages");
    }

    // ========================= UPDATE OPERATION TESTS =========================

    @Test
    @Order(15)
    @DisplayName("Test mark message as read")
    void testMarkAsRead_Success() throws SQLException {
        // Arrange
        Message createdMessage = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, TEST_CONTENT);
        assertFalse(createdMessage.isRead(), "Message should initially be unread");

        // Act
        boolean marked = messageDAO.markAsRead(createdMessage.getMessageId());

        // Assert
        assertTrue(marked, "Message should be marked as read successfully");
        
        Message foundMessage = messageDAO.findById(createdMessage.getMessageId());
        assertTrue(foundMessage.isRead(), "Message should be marked as read");
    }

    @Test
    @Order(16)
    @DisplayName("Test mark multiple messages as read")
    void testMarkMultipleAsRead_Success() throws SQLException {
        // Arrange
        Message msg1 = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 1");
        Message msg2 = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 2");
        Message msg3 = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 3");
        
        List<Integer> messageIds = Arrays.asList(msg1.getMessageId(), msg2.getMessageId(), msg3.getMessageId());

        // Act
        int markedCount = messageDAO.markMultipleAsRead(messageIds);

        // Assert
        assertEquals(3, markedCount, "Should mark 3 messages as read");
        
        // Verify all messages are marked as read
        assertTrue(messageDAO.findById(msg1.getMessageId()).isRead());
        assertTrue(messageDAO.findById(msg2.getMessageId()).isRead());
        assertTrue(messageDAO.findById(msg3.getMessageId()).isRead());
    }

    @Test
    @Order(17)
    @DisplayName("Test mark all messages as read")
    void testMarkAllAsRead_Success() throws SQLException {
        // Arrange
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 1");
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 2");
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 3");

        // Act
        int markedCount = messageDAO.markAllAsRead(TEST_RECEIVER_ID);

        // Assert
        assertEquals(3, markedCount, "Should mark 3 messages as read");
        
        // Verify no unread messages remain
        List<Message> unreadMessages = messageDAO.getUnreadMessages(TEST_RECEIVER_ID);
        assertEquals(0, unreadMessages.size(), "Should have no unread messages");
    }

    @Test
    @Order(18)
    @DisplayName("Test update message content")
    void testUpdateMessageContent_Success() throws SQLException {
        // Arrange
        Message createdMessage = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, TEST_CONTENT);
        String newContent = "Updated message content";

        // Act
        boolean updated = messageDAO.updateMessageContent(createdMessage.getMessageId(), newContent);

        // Assert
        assertTrue(updated, "Message content should be updated successfully");
        
        Message foundMessage = messageDAO.findById(createdMessage.getMessageId());
        assertEquals(newContent, foundMessage.getContent(), "Message content should be updated");
    }

    // ========================= DELETE OPERATION TESTS =========================

    @Test
    @Order(19)
    @DisplayName("Test delete message")
    void testDeleteMessage_Success() throws SQLException {
        // Arrange
        Message createdMessage = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, TEST_CONTENT);

        // Act
        boolean deleted = messageDAO.deleteMessage(createdMessage.getMessageId());

        // Assert
        assertTrue(deleted, "Message deletion should be successful");
        
        Message foundMessage = messageDAO.findById(createdMessage.getMessageId());
        assertNull(foundMessage, "Message should not be found after deletion");
    }

    @Test
    @Order(20)
    @DisplayName("Test delete messages by sender")
    void testDeleteMessagesBySender_Success() throws SQLException {
        // Arrange - ensure sender has no messages first
        messageDAO.deleteMessagesBySender(TEST_SENDER_ID);
        
        // Create exactly 3 messages from this sender
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 1");
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 2");
        messageDAO.sendNote(TEST_RECEIVER_ID, TEST_SENDER_ID, "Message 3"); // Different sender

        // Verify we have exactly 2 messages from TEST_SENDER_ID before deletion
        int countBeforeDeletion = messageDAO.getSentMessages(TEST_SENDER_ID).size();
        assertEquals(2, countBeforeDeletion, "Should have exactly 2 messages from sender before deletion");

        // Act
        int deletedCount = messageDAO.deleteMessagesBySender(TEST_SENDER_ID);

        // Assert
        assertEquals(2, deletedCount, "Should delete exactly 2 messages from sender");
        
        List<Message> remainingMessages = messageDAO.getSentMessages(TEST_SENDER_ID);
        assertEquals(0, remainingMessages.size(), "No messages should remain from the sender");
    }

    @Test
    @Order(21)
    @DisplayName("Test delete messages by receiver")
    void testDeleteMessagesByReceiver_Success() throws SQLException {
        // Arrange
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 1");
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 2");
        messageDAO.sendNote(TEST_RECEIVER_ID, TEST_SENDER_ID, "Message 3"); // Different receiver

        // Act
        int deletedCount = messageDAO.deleteMessagesByReceiver(TEST_RECEIVER_ID);

        // Assert
        assertEquals(2, deletedCount, "Should delete 2 messages for receiver");
        
        List<Message> remainingMessages = messageDAO.getReceivedMessages(TEST_RECEIVER_ID);
        assertEquals(0, remainingMessages.size(), "No messages should remain for the receiver");
    }

    @Test
    @Order(22)
    @DisplayName("Test delete conversation")
    void testDeleteConversation_Success() throws SQLException {
        // Arrange
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 1");
        messageDAO.sendNote(TEST_RECEIVER_ID, TEST_SENDER_ID, "Message 2");
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Message 3");

        // Act
        int deletedCount = messageDAO.deleteConversation(TEST_SENDER_ID, TEST_RECEIVER_ID);

        // Assert
        assertEquals(3, deletedCount, "Should delete 3 messages in conversation");
        
        List<Message> remainingConversation = messageDAO.getConversation(TEST_SENDER_ID, TEST_RECEIVER_ID);
        assertEquals(0, remainingConversation.size(), "No messages should remain in conversation");
    }

    @Test
    @Order(23)
    @DisplayName("Test delete messages by type")
    void testDeleteMessagesByType_Success() throws SQLException {
        // Arrange
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Note 1");
        messageDAO.sendFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID, "Friend Request");
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Note 2");

        // Act
        int deletedCount = messageDAO.deleteMessagesByType(TEST_RECEIVER_ID, Message.TYPE_NOTE);

        // Assert
        assertEquals(2, deletedCount, "Should delete 2 note messages");
        
        List<Message> remainingNotes = messageDAO.getMessagesByType(TEST_RECEIVER_ID, Message.TYPE_NOTE);
        assertEquals(0, remainingNotes.size(), "No note messages should remain");
        
        List<Message> remainingFriendRequests = messageDAO.getMessagesByType(TEST_RECEIVER_ID, Message.TYPE_FRIEND_REQUEST);
        assertEquals(1, remainingFriendRequests.size(), "Friend request should remain");
    }

    @Test
    @Order(24)
    @DisplayName("Test delete old messages")
    void testDeleteOldMessages_Success() throws SQLException {
        // This test is more of a functional test - we can't easily create "old" messages
        // without manipulating timestamps, so we'll just verify the method doesn't fail
        
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> {
            int deletedCount = messageDAO.deleteOldMessages(365); // Delete messages older than 1 year
            assertTrue(deletedCount >= 0, "Should return non-negative count");
        });
    }

    // ========================= VALIDATION AND UTILITY TESTS =========================

    @Test
    @Order(25)
    @DisplayName("Test message exists check")
    void testMessageExists_Success() throws SQLException {
        // Test non-existent message
        assertFalse(messageDAO.messageExists(99999), "Should return false for non-existent message");

        // Arrange - create message
        Message createdMessage = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, TEST_CONTENT);

        // Test existing message
        assertTrue(messageDAO.messageExists(createdMessage.getMessageId()), "Should return true for existing message");
    }

    @Test
    @Order(26)
    @DisplayName("Test get unread message count")
    void testGetUnreadMessageCount_Success() throws SQLException {
        // Arrange
        Message msg1 = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Unread 1");
        Message msg2 = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Unread 2");
        Message msg3 = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Read");
        
        // Mark one as read
        messageDAO.markAsRead(msg3.getMessageId());

        // Act
        int unreadCount = messageDAO.getUnreadMessageCount(TEST_RECEIVER_ID);

        // Assert
        assertEquals(2, unreadCount, "Should have 2 unread messages");
    }

    @Test
    @Order(27)
    @DisplayName("Test get message count by type")
    void testGetMessageCountByType_Success() throws SQLException {
        // Arrange
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Note 1");
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Note 2");
        messageDAO.sendFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID, "Friend Request");

        // Act
        int noteCount = messageDAO.getMessageCountByType(TEST_RECEIVER_ID, Message.TYPE_NOTE);
        int friendRequestCount = messageDAO.getMessageCountByType(TEST_RECEIVER_ID, Message.TYPE_FRIEND_REQUEST);
        int challengeCount = messageDAO.getMessageCountByType(TEST_RECEIVER_ID, Message.TYPE_CHALLENGE);

        // Assert
        assertEquals(2, noteCount, "Should have 2 note messages");
        assertEquals(1, friendRequestCount, "Should have 1 friend request message");
        assertEquals(0, challengeCount, "Should have 0 challenge messages");
    }

    @Test
    @Order(28)
    @DisplayName("Test get total message count")
    void testGetTotalMessageCount_Success() throws SQLException {
        // Arrange
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Sent message");
        messageDAO.sendNote(TEST_RECEIVER_ID, TEST_SENDER_ID, "Received message");

        // Act
        int totalCount = messageDAO.getTotalMessageCount(TEST_SENDER_ID);

        // Assert
        assertEquals(2, totalCount, "Should have 2 total messages (sent + received)");
    }

    @Test
    @Order(29)
    @DisplayName("Test get message statistics")
    void testGetMessageStats_Success() throws SQLException {
        // Arrange
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Received 1");
        messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Received 2");
        messageDAO.sendNote(TEST_RECEIVER_ID, TEST_SENDER_ID, "Sent 1");
        
        // Mark one received message as read
        List<Message> receivedMessages = messageDAO.getReceivedMessages(TEST_RECEIVER_ID);
        messageDAO.markAsRead(receivedMessages.get(0).getMessageId());

        // Act
        int[] stats = messageDAO.getMessageStats(TEST_RECEIVER_ID);

        // Assert
        assertEquals(3, stats.length, "Stats array should have 3 elements");
        assertEquals(2, stats[0], "Should have 2 received messages");
        assertEquals(1, stats[1], "Should have 1 sent message");
        assertEquals(1, stats[2], "Should have 1 unread message");
    }

    @Test
    @Order(30)
    @DisplayName("Test has pending friend request")
    void testHasPendingFriendRequest_Success() throws SQLException {
        // Test no pending request
        assertFalse(messageDAO.hasPendingFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID), 
            "Should return false when no pending request exists");

        // Arrange - send friend request
        Message friendRequest = messageDAO.sendFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID, TEST_FRIEND_REQUEST_CONTENT);

        // Test pending request exists
        assertTrue(messageDAO.hasPendingFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID), 
            "Should return true when pending request exists");

        // Mark as read and test again
        messageDAO.markAsRead(friendRequest.getMessageId());
        assertFalse(messageDAO.hasPendingFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID), 
            "Should return false when request is read");
    }

    @Test
    @Order(31)
    @DisplayName("Test get latest message")
    void testGetLatestMessage_Success() throws SQLException {
        // Test no messages
        assertNull(messageDAO.getLatestMessage(TEST_SENDER_ID, TEST_RECEIVER_ID), 
            "Should return null when no messages exist");

        // Arrange
        Message msg1 = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "First message");
        // Add delay to ensure different timestamps
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        Message msg2 = messageDAO.sendNote(TEST_RECEIVER_ID, TEST_SENDER_ID, "Latest message");

        // Act
        Message latestMessage = messageDAO.getLatestMessage(TEST_SENDER_ID, TEST_RECEIVER_ID);

        // Assert
        assertNotNull(latestMessage, "Should return the latest message");
        assertEquals("Latest message", latestMessage.getContent(), "Should return the most recent message");
        assertEquals(TEST_RECEIVER_ID, latestMessage.getSenderId(), "Should be from the correct sender");
    }

    @Test
    @Order(32)
    @DisplayName("Test edge cases and null handling")
    void testEdgeCases_Success() throws SQLException {
        // Test empty results
        List<Message> emptyReceived = messageDAO.getReceivedMessages(99999);
        assertEquals(0, emptyReceived.size(), "Should return empty list for non-existent user");

        List<Message> emptyByType = messageDAO.getMessagesByType(99999, Message.TYPE_NOTE);
        assertEquals(0, emptyByType.size(), "Should return empty list for non-existent user");

        // Test counts for non-existent data
        assertEquals(0, messageDAO.getUnreadMessageCount(99999), "Should return 0 for non-existent user");
        assertEquals(0, messageDAO.getMessageCountByType(99999, Message.TYPE_NOTE), "Should return 0 for non-existent user");

        // Test mark multiple with empty list
        assertEquals(0, messageDAO.markMultipleAsRead(Arrays.asList()), "Should return 0 for empty list");
        assertEquals(0, messageDAO.markMultipleAsRead(null), "Should return 0 for null list");
    }

    @Test
    @Order(33)
    @DisplayName("Test comprehensive message workflow")
    void testComprehensiveWorkflow_Success() throws SQLException {
        // Test complete workflow: create, read, update, validate, delete
        
        // 1. Create different types of messages
        Message note = messageDAO.sendNote(TEST_SENDER_ID, TEST_RECEIVER_ID, "Test note");
        Message friendRequest = messageDAO.sendFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID, "Friend request");
        Message challenge = messageDAO.sendChallenge(TEST_SENDER_ID, TEST_RECEIVER_ID, "Challenge", TEST_QUIZ_ID);

        assertNotNull(note, "Note should be created");
        assertNotNull(friendRequest, "Friend request should be created");
        assertNotNull(challenge, "Challenge should be created");
        assertEquals(TEST_QUIZ_ID, challenge.getQuizId().intValue(), "Challenge should have quiz ID");

        // 2. Verify reads work correctly
        List<Message> received = messageDAO.getReceivedMessages(TEST_RECEIVER_ID);
        assertEquals(3, received.size(), "Should have 3 received messages");

        List<Message> conversation = messageDAO.getConversation(TEST_SENDER_ID, TEST_RECEIVER_ID);
        assertEquals(3, conversation.size(), "Should have 3 messages in conversation");

        // 3. Test messaging functionality
        assertTrue(messageDAO.hasPendingFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID), 
            "Should have pending friend request");
        assertEquals(3, messageDAO.getUnreadMessageCount(TEST_RECEIVER_ID), "Should have 3 unread messages");

        // 4. Update messages
        boolean marked = messageDAO.markAsRead(note.getMessageId());
        assertTrue(marked, "Should mark message as read");
        assertEquals(2, messageDAO.getUnreadMessageCount(TEST_RECEIVER_ID), "Should have 2 unread messages after marking one as read");

        // 5. Update content
        boolean updated = messageDAO.updateMessageContent(note.getMessageId(), "Updated content");
        assertTrue(updated, "Should update message content");
        Message updatedMessage = messageDAO.findById(note.getMessageId());
        assertEquals("Updated content", updatedMessage.getContent(), "Content should be updated");

        // 6. Delete specific message
        boolean deleted = messageDAO.deleteMessage(friendRequest.getMessageId());
        assertTrue(deleted, "Should delete message");
        assertNull(messageDAO.findById(friendRequest.getMessageId()), "Message should be deleted");

        // 7. Verify integrity
        List<Message> finalReceived = messageDAO.getReceivedMessages(TEST_RECEIVER_ID);
        assertEquals(2, finalReceived.size(), "Should have 2 messages after deletion");
        assertFalse(messageDAO.hasPendingFriendRequest(TEST_SENDER_ID, TEST_RECEIVER_ID), 
            "Should no longer have pending friend request");
    }
} 