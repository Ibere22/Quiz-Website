package model;

import java.util.Date;

/**
 * Message model representing messages in the internal mail system
 * Corresponds to the 'messages' table in the database
 * Supports friend requests, challenges, and notes
 */
public class Message {
    private int messageId;
    private int senderId;
    private int receiverId;
    private String messageType;
    private String content;
    private Integer quizId;      // Only for challenge messages
    private Date dateSent;
    private boolean isRead;
    private String senderUsername;
    private String quizName;
    
    // Message type constants
    public static final String TYPE_FRIEND_REQUEST = "friend_request";
    public static final String TYPE_CHALLENGE = "challenge";
    public static final String TYPE_NOTE = "note";
    
    // Default constructor
    public Message() {
        this.dateSent = new Date();
        this.isRead = false;
    }
    
    // Constructor for note messages
    public Message(int senderId, int receiverId, String messageType, String content) {
        this();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.content = content;
    }
    
    // Constructor for challenge messages
    public Message(int senderId, int receiverId, String content, int quizId) {
        this();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = TYPE_CHALLENGE;
        this.content = content;
        this.quizId = quizId;
    }
    
    // Full constructor
    public Message(int messageId, int senderId, int receiverId, String messageType, 
                  String content, Integer quizId, Date dateSent, boolean isRead) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.content = content;
        this.quizId = quizId;
        this.dateSent = dateSent;
        this.isRead = isRead;
    }
    
    // Getters and Setters
    public int getMessageId() {
        return messageId;
    }
    
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
    
    public int getSenderId() {
        return senderId;
    }
    
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
    
    public int getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getQuizId() {
        return quizId;
    }
    
    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }
    
    public Date getDateSent() {
        return dateSent;
    }
    
    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
    
    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }
    
    // Helper methods
    public boolean isFriendRequest() {
        return TYPE_FRIEND_REQUEST.equals(messageType);
    }
    
    public boolean isChallenge() {
        return TYPE_CHALLENGE.equals(messageType);
    }
    
    public boolean isNote() {
        return TYPE_NOTE.equals(messageType);
    }
    
    public void markAsRead() {
        this.isRead = true;
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", messageType='" + messageType + '\'' +
                ", content='" + content + '\'' +
                ", quizId=" + quizId +
                ", dateSent=" + dateSent +
                ", isRead=" + isRead +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Message message = (Message) obj;
        return messageId == message.messageId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(messageId);
    }
} 