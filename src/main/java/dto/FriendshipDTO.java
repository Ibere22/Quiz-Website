package dto;

import java.util.Date;

/**
 * Data Transfer Object for Friendship entity
 * Contains friendship information for display on the website
 * Includes both users as UserDTOs and friendship status
 */
public record FriendshipDTO(
    int friendshipId,
    UserDTO requester,
    UserDTO receiver,
    String status,
    Date dateRequested,
    Date dateAccepted
) {
    // Friendship status constants
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_DECLINED = "declined";
    public static final String STATUS_BLOCKED = "blocked";
    
    // Helper methods for status checking
    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }
    
    public boolean isAccepted() {
        return STATUS_ACCEPTED.equals(status);
    }
    
    public boolean isDeclined() {
        return STATUS_DECLINED.equals(status);
    }
    
    public boolean isBlocked() {
        return STATUS_BLOCKED.equals(status);
    }
    
    // Helper method to get the other user in the friendship
    public UserDTO getOtherUser(int currentUserId) {
        if (requester != null && requester.userId() == currentUserId) {
            return receiver;
        } else if (receiver != null && receiver.userId() == currentUserId) {
            return requester;
        }
        return null;
    }
    
    // Helper method to check if a user is the requester
    public boolean isRequester(int userId) {
        return requester != null && requester.userId() == userId;
    }
    
    // Helper method to check if a user is the receiver
    public boolean isReceiver(int userId) {
        return receiver != null && receiver.userId() == userId;
    }
    
    // Static factory method for conversion from Friendship model
    public static FriendshipDTO fromFriendship(model.Friendship friendship, UserDTO requester, UserDTO receiver) {
        if (friendship == null) return null;
        return new FriendshipDTO(
            friendship.getFriendshipId(),
            requester,
            receiver,
            friendship.getStatus(),
            friendship.getDateRequested(),
            friendship.getDateAccepted()
        );
    }
} 