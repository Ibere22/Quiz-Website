package dto;

import java.util.Date;

/**
 * Data Transfer Object for Message entity
 * Contains message information for display on the website
 * Includes sender and receiver as UserDTOs and quiz information for challenge messages
 */
public record MessageDTO(
    int messageId,
    UserDTO sender,
    UserDTO receiver,
    String messageType,
    String content,
    QuizDTO quiz,
    Date dateSent,
    boolean isRead
) {
    // Message type constants
    public static final String TYPE_FRIEND_REQUEST = "friend_request";
    public static final String TYPE_CHALLENGE = "challenge";
    public static final String TYPE_NOTE = "note";
    
    // Helper methods for message type checking
    public boolean isFriendRequest() {
        return TYPE_FRIEND_REQUEST.equals(messageType);
    }
    
    public boolean isChallenge() {
        return TYPE_CHALLENGE.equals(messageType);
    }
    
    public boolean isNote() {
        return TYPE_NOTE.equals(messageType);
    }
    
    // Helper method to get a short preview of the content
    public String getContentPreview(int maxLength) {
        if (content == null) return "";
        if (content.length() <= maxLength) return content;
        return content.substring(0, maxLength) + "...";
    }
    
    // Helper method to format date sent
    public String getFormattedDateSent() {
        if (dateSent == null) return "";
        
        long now = System.currentTimeMillis();
        long messageTime = dateSent.getTime();
        long diffInSeconds = (now - messageTime) / 1000;
        
        if (diffInSeconds < 60) {
            return "Just now";
        } else if (diffInSeconds < 3600) {
            long minutes = diffInSeconds / 60;
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (diffInSeconds < 86400) {
            long hours = diffInSeconds / 3600;
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else {
            long days = diffInSeconds / 86400;
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        }
    }
    
    // Static factory method for conversion from Message model
    public static MessageDTO fromMessage(model.Message message, UserDTO sender, UserDTO receiver, QuizDTO quiz) {
        if (message == null) return null;
        return new MessageDTO(
            message.getMessageId(),
            sender,
            receiver,
            message.getMessageType(),
            message.getContent(),
            quiz,
            message.getDateSent(),
            message.isRead()
        );
    }
} 