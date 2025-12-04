package model;

import java.util.Date;

/**
 * Friendship model representing friendship relationships between users
 * Corresponds to the 'friendships' table in the database
 */
public class Friendship {
    private int friendshipId;
    private int requesterId;    // User who sent the friend request
    private int receiverId;     // User who received the friend request
    private String status;      // pending, accepted, declined, blocked
    private Date dateRequested;
    private Date dateAccepted;
    
    // Status constants
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_DECLINED = "declined";
    public static final String STATUS_BLOCKED = "blocked";
    
    // Default constructor
    public Friendship() {
        this.dateRequested = new Date();
        this.status = STATUS_PENDING;
    }
    
    // Constructor for new friend request
    public Friendship(int requesterId, int receiverId) {
        this();
        this.requesterId = requesterId;
        this.receiverId = receiverId;
    }
    
    // Full constructor
    public Friendship(int friendshipId, int requesterId, int receiverId, String status, 
                     Date dateRequested, Date dateAccepted) {
        this.friendshipId = friendshipId;
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.status = status;
        this.dateRequested = dateRequested;
        this.dateAccepted = dateAccepted;
    }
    
    // Getters and Setters
    public int getFriendshipId() {
        return friendshipId;
    }
    
    public void setFriendshipId(int friendshipId) {
        this.friendshipId = friendshipId;
    }
    
    public int getRequesterId() {
        return requesterId;
    }
    
    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }
    
    public int getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getDateRequested() {
        return dateRequested;
    }
    
    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }
    
    public Date getDateAccepted() {
        return dateAccepted;
    }
    
    public void setDateAccepted(Date dateAccepted) {
        this.dateAccepted = dateAccepted;
    }
    
    // Helper methods
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
    
    public void accept() {
        this.status = STATUS_ACCEPTED;
        this.dateAccepted = new Date();
    }
    
    public void decline() {
        this.status = STATUS_DECLINED;
    }
    
    public void block() {
        this.status = STATUS_BLOCKED;
    }
    
    /**
     * Get the friend's user ID relative to the given user ID
     */
    public int getFriendId(int userId) {
        return (userId == requesterId) ? receiverId : requesterId;
    }
    
    /**
     * Check if the given user is involved in this friendship
     */
    public boolean involves(int userId) {
        return userId == requesterId || userId == receiverId;
    }
    
    @Override
    public String toString() {
        return "Friendship{" +
                "friendshipId=" + friendshipId +
                ", requesterId=" + requesterId +
                ", receiverId=" + receiverId +
                ", status='" + status + '\'' +
                ", dateRequested=" + dateRequested +
                ", dateAccepted=" + dateAccepted +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Friendship that = (Friendship) obj;
        return friendshipId == that.friendshipId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(friendshipId);
    }
} 