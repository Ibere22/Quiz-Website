package dao;

import model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Message entity
 * Provides full CRUD operations and message-specific functionality
 * Supports friend requests, challenges, and notes
 */
public class MessageDAO {
    
    private Connection connection;
    
    // Constructor that takes a database connection
    public MessageDAO(Connection connection) {
        this.connection = connection;
    }
    
    // ========================= CREATE OPERATIONS =========================
    
    /**
     * Create a new message
     * @param message Message object with all necessary fields
     * @return The created message with generated ID, or null if creation failed
     * @throws SQLException If database error occurs
     */
    public Message createMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_type, content, quiz_id, date_sent, is_read) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getReceiverId());
            stmt.setString(3, message.getMessageType());
            stmt.setString(4, message.getContent());
            
            // Handle nullable quiz_id
            if (message.getQuizId() != null) {
                stmt.setInt(5, message.getQuizId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.setTimestamp(6, new Timestamp(message.getDateSent().getTime()));
            stmt.setBoolean(7, message.isRead());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    message.setMessageId(generatedKeys.getInt(1));
                    return message;
                }
            }
        }
        return null;
    }
    
    /**
     * Send a note message
     * @param senderId ID of the sender
     * @param receiverId ID of the receiver
     * @param content Message content
     * @return Created message with generated ID
     * @throws SQLException If database error occurs
     */
    public Message sendNote(int senderId, int receiverId, String content) throws SQLException {
        Message message = new Message(senderId, receiverId, Message.TYPE_NOTE, content);
        return createMessage(message);
    }
    
    /**
     * Send a friend request message
     * @param senderId ID of the sender
     * @param receiverId ID of the receiver
     * @param content Request message content
     * @return Created message with generated ID
     * @throws SQLException If database error occurs
     */
    public Message sendFriendRequest(int senderId, int receiverId, String content) throws SQLException {
        Message message = new Message(senderId, receiverId, Message.TYPE_FRIEND_REQUEST, content);
        return createMessage(message);
    }
    
    /**
     * Send a challenge message
     * @param senderId ID of the sender
     * @param receiverId ID of the receiver
     * @param content Challenge message content
     * @param quizId ID of the quiz being challenged
     * @return Created message with generated ID
     * @throws SQLException If database error occurs
     */
    public Message sendChallenge(int senderId, int receiverId, String content, int quizId) throws SQLException {
        Message message = new Message(senderId, receiverId, content, quizId);
        return createMessage(message);
    }
    
    // ========================= READ OPERATIONS =========================
    
    /**
     * Find a message by its ID
     * @param messageId The message ID to search for
     * @return Message object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public Message findById(int messageId) throws SQLException {
        String sql = "SELECT id, sender_id, receiver_id, message_type, content, quiz_id, date_sent, is_read FROM messages WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMessage(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get all messages received by a user
     * @param receiverId The receiver's user ID
     * @return List of messages received by the user, ordered by date_sent DESC
     * @throws SQLException If database error occurs
     */
    public List<Message> getReceivedMessages(int receiverId) throws SQLException {
        String sql = "SELECT m.id, m.sender_id, m.receiver_id, m.message_type, m.content, m.quiz_id, m.date_sent, m.is_read, u.username as sender_username " +
                     "FROM messages m JOIN users u ON m.sender_id = u.id WHERE m.receiver_id = ? ORDER BY m.date_sent DESC";
        List<Message> messages = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRowToMessage(rs));
                }
            }
        }
        return messages;
    }
    
    /**
     * Get all messages sent by a user
     * @param senderId The sender's user ID
     * @return List of messages sent by the user, ordered by date_sent DESC
     * @throws SQLException If database error occurs
     */
    public List<Message> getSentMessages(int senderId) throws SQLException {
        String sql = "SELECT id, sender_id, receiver_id, message_type, content, quiz_id, date_sent, is_read FROM messages WHERE sender_id = ? ORDER BY date_sent DESC";
        List<Message> messages = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRowToMessage(rs));
                }
            }
        }
        return messages;
    }
    
    /**
     * Get conversation between two users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return List of messages between the two users, ordered by date_sent ASC
     * @throws SQLException If database error occurs
     */
    public List<Message> getConversation(int userId1, int userId2) throws SQLException {
        String sql = "SELECT id, sender_id, receiver_id, message_type, content, quiz_id, date_sent, is_read FROM messages " +
                    "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                    "ORDER BY date_sent ASC";
        List<Message> messages = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRowToMessage(rs));
                }
            }
        }
        return messages;
    }
    
    /**
     * Get messages by type for a user
     * @param receiverId The receiver's user ID
     * @param messageType Type of messages to retrieve
     * @return List of messages of the specified type
     * @throws SQLException If database error occurs
     */
    public List<Message> getMessagesByType(int receiverId, String messageType) throws SQLException {
        String sql = "SELECT id, sender_id, receiver_id, message_type, content, quiz_id, date_sent, is_read FROM messages " +
                    "WHERE receiver_id = ? AND message_type = ? ORDER BY date_sent DESC";
        List<Message> messages = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.setString(2, messageType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRowToMessage(rs));
                }
            }
        }
        return messages;
    }
    
    /**
     * Get unread messages for a user
     * @param receiverId The receiver's user ID
     * @return List of unread messages
     * @throws SQLException If database error occurs
     */
    public List<Message> getUnreadMessages(int receiverId) throws SQLException {
        String sql = "SELECT id, sender_id, receiver_id, message_type, content, quiz_id, date_sent, is_read FROM messages " +
                    "WHERE receiver_id = ? AND is_read = FALSE ORDER BY date_sent DESC";
        List<Message> messages = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRowToMessage(rs));
                }
            }
        }
        return messages;
    }
    
    /**
     * Get messages with pagination
     * @param receiverId The receiver's user ID
     * @param offset Starting position (0-based)
     * @param limit Maximum number of messages to return
     * @return List of messages
     * @throws SQLException If database error occurs
     */
    public List<Message> getReceivedMessages(int receiverId, int offset, int limit) throws SQLException {
        String sql = "SELECT id, sender_id, receiver_id, message_type, content, quiz_id, date_sent, is_read FROM messages " +
                    "WHERE receiver_id = ? ORDER BY date_sent DESC LIMIT ? OFFSET ?";
        List<Message> messages = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRowToMessage(rs));
                }
            }
        }
        return messages;
    }
    
    /**
     * Get recent messages in the last N days
     * @param receiverId The receiver's user ID
     * @param days Number of days to look back
     * @param limit Maximum number of messages to return
     * @return List of recent messages
     * @throws SQLException If database error occurs
     */
    public List<Message> getRecentMessages(int receiverId, int days, int limit) throws SQLException {
        String sql = "SELECT id, sender_id, receiver_id, message_type, content, quiz_id, date_sent, is_read FROM messages " +
                    "WHERE receiver_id = ? AND date_sent >= DATE_SUB(NOW(), INTERVAL ? DAY) ORDER BY date_sent DESC LIMIT ?";
        List<Message> messages = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.setInt(2, days);
            stmt.setInt(3, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRowToMessage(rs));
                }
            }
        }
        return messages;
    }
    
    // ========================= UPDATE OPERATIONS =========================
    
    /**
     * Mark a message as read
     * @param messageId The message ID to mark as read
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean markAsRead(int messageId) throws SQLException {
        String sql = "UPDATE messages SET is_read = TRUE WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Mark multiple messages as read
     * @param messageIds List of message IDs to mark as read
     * @return Number of messages marked as read
     * @throws SQLException If database error occurs
     */
    public int markMultipleAsRead(List<Integer> messageIds) throws SQLException {
        if (messageIds == null || messageIds.isEmpty()) {
            return 0;
        }
        
        StringBuilder sql = new StringBuilder("UPDATE messages SET is_read = TRUE WHERE id IN (");
        for (int i = 0; i < messageIds.size(); i++) {
            sql.append("?");
            if (i < messageIds.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < messageIds.size(); i++) {
                stmt.setInt(i + 1, messageIds.get(i));
            }
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Mark all messages as read for a receiver
     * @param receiverId The receiver's user ID
     * @return Number of messages marked as read
     * @throws SQLException If database error occurs
     */
    public int markAllAsRead(int receiverId) throws SQLException {
        String sql = "UPDATE messages SET is_read = TRUE WHERE receiver_id = ? AND is_read = FALSE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Update message content
     * @param messageId The message ID
     * @param newContent New content
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateMessageContent(int messageId, String newContent) throws SQLException {
        String sql = "UPDATE messages SET content = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newContent);
            stmt.setInt(2, messageId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    // ========================= DELETE OPERATIONS =========================
    
    /**
     * Delete a message by ID
     * @param messageId The message ID to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean deleteMessage(int messageId) throws SQLException {
        String sql = "DELETE FROM messages WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete all messages sent by a user
     * @param senderId The sender's user ID
     * @return Number of messages deleted
     * @throws SQLException If database error occurs
     */
    public int deleteMessagesBySender(int senderId) throws SQLException {
        String sql = "DELETE FROM messages WHERE sender_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Delete all messages received by a user
     * @param receiverId The receiver's user ID
     * @return Number of messages deleted
     * @throws SQLException If database error occurs
     */
    public int deleteMessagesByReceiver(int receiverId) throws SQLException {
        String sql = "DELETE FROM messages WHERE receiver_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Delete conversation between two users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Number of messages deleted
     * @throws SQLException If database error occurs
     */
    public int deleteConversation(int userId1, int userId2) throws SQLException {
        String sql = "DELETE FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Delete messages by type for a user
     * @param receiverId The receiver's user ID
     * @param messageType Type of messages to delete
     * @return Number of messages deleted
     * @throws SQLException If database error occurs
     */
    public int deleteMessagesByType(int receiverId, String messageType) throws SQLException {
        String sql = "DELETE FROM messages WHERE receiver_id = ? AND message_type = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.setString(2, messageType);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Delete old messages older than specified days
     * @param days Number of days (messages older than this will be deleted)
     * @return Number of messages deleted
     * @throws SQLException If database error occurs
     */
    public int deleteOldMessages(int days) throws SQLException {
        String sql = "DELETE FROM messages WHERE date_sent < DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, days);
            return stmt.executeUpdate();
        }
    }
    
    // ========================= VALIDATION AND UTILITY METHODS =========================
    
    /**
     * Check if a message exists
     * @param messageId The message ID to check
     * @return true if message exists, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean messageExists(int messageId) throws SQLException {
        String sql = "SELECT 1 FROM messages WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Get count of unread messages for a user
     * @param receiverId The receiver's user ID
     * @return Number of unread messages
     * @throws SQLException If database error occurs
     */
    public int getUnreadMessageCount(int receiverId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM messages WHERE receiver_id = ? AND is_read = FALSE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Get count of messages by type for a user
     * @param receiverId The receiver's user ID
     * @param messageType Type of messages to count
     * @return Number of messages of the specified type
     * @throws SQLException If database error occurs
     */
    public int getMessageCountByType(int receiverId, String messageType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM messages WHERE receiver_id = ? AND message_type = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.setString(2, messageType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Get total message count for a user (sent + received)
     * @param userId The user ID
     * @return Total number of messages
     * @throws SQLException If database error occurs
     */
    public int getTotalMessageCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM messages WHERE sender_id = ? OR receiver_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Get message statistics for a user
     * @param userId The user ID
     * @return Array with [total received, total sent, unread received]
     * @throws SQLException If database error occurs
     */
    public int[] getMessageStats(int userId) throws SQLException {
        int received = getReceivedMessages(userId).size();
        int sent = getSentMessages(userId).size();
        int unread = getUnreadMessageCount(userId);
        
        return new int[]{received, sent, unread};
    }
    
    /**
     * Check if there are pending friend requests between users
     * @param senderId The sender's user ID
     * @param receiverId The receiver's user ID
     * @return true if there's a pending friend request, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean hasPendingFriendRequest(int senderId, int receiverId) throws SQLException {
        String sql = "SELECT 1 FROM messages WHERE sender_id = ? AND receiver_id = ? AND message_type = ? AND is_read = FALSE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, Message.TYPE_FRIEND_REQUEST);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Get latest message between two users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Latest message in the conversation, or null if no messages exist
     * @throws SQLException If database error occurs
     */
    public Message getLatestMessage(int userId1, int userId2) throws SQLException {
        String sql = "SELECT id, sender_id, receiver_id, message_type, content, quiz_id, date_sent, is_read FROM messages " +
                    "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                    "ORDER BY date_sent DESC LIMIT 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMessage(rs);
                }
            }
        }
        return null;
    }
    
    // ========================= HELPER METHODS =========================
    
    /**
     * Map a ResultSet row to a Message object
     * @param rs The ResultSet positioned at a valid row
     * @return Message object
     * @throws SQLException If database error occurs
     */
    private Message mapRowToMessage(ResultSet rs) throws SQLException {
        Message message = new Message(
            rs.getInt("id"),
            rs.getInt("sender_id"),
            rs.getInt("receiver_id"),
            rs.getString("message_type"),
            rs.getString("content"),
            (rs.getObject("quiz_id") != null ? rs.getInt("quiz_id") : null),
            rs.getTimestamp("date_sent"),
            rs.getBoolean("is_read")
        );
        try {
            String senderUsername = rs.getString("sender_username");
            message.setSenderUsername(senderUsername);
        } catch (SQLException e) {
            // sender_username column may not exist in some queries
        }
        return message;
    }
} 