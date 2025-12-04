package dao;

import model.Friendship;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Friendship entity
 * Provides full CRUD operations and friendship-specific functionality
 * Supports friend requests, acceptance, declining, blocking, and friendship management
 */
public class FriendshipDAO {

    private Connection connection;

    // Constructor that takes a database connection
    public FriendshipDAO(Connection connection) {
        this.connection = connection;
    }

    // ========================= CREATE OPERATIONS =========================

    /**
     * Create a new friendship request
     * @param friendship Friendship object with requester and receiver IDs
     * @return The created friendship with generated ID, or null if creation failed
     * @throws SQLException If database error occurs
     */
    public Friendship createFriendship(Friendship friendship) throws SQLException {
        String sql = "INSERT INTO friendships (requester_id, receiver_id, status, date_requested, date_accepted) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, friendship.getRequesterId());
            stmt.setInt(2, friendship.getReceiverId());
            stmt.setString(3, friendship.getStatus());
            stmt.setTimestamp(4, new Timestamp(friendship.getDateRequested().getTime()));

            // Handle nullable date_accepted
            if (friendship.getDateAccepted() != null) {
                stmt.setTimestamp(5, new Timestamp(friendship.getDateAccepted().getTime()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    friendship.setFriendshipId(generatedKeys.getInt(1));
                    return friendship;
                }
            }
        }
        return null;
    }

    /**
     * Send a friend request
     * @param requesterId ID of the user sending the request
     * @param receiverId ID of the user receiving the request
     * @return Created friendship with generated ID
     * @throws SQLException If database error occurs
     */
    public Friendship sendFriendRequest(int requesterId, int receiverId) throws SQLException {
        // Check if they are already friends
        if (areFriends(requesterId, receiverId)) {
            throw new SQLException("Users are already friends");
        }
        // Check if there's already a pending request in either direction
        if (hasPendingRequest(requesterId, receiverId)) {
            throw new SQLException("Friend request already sent");
        }
        if (hasPendingRequest(receiverId, requesterId)) {
            throw new SQLException("Friend request already received");
        }
        // Find any existing friendship in either direction
        Friendship existing = findFriendship(requesterId, receiverId);
        Friendship reverse = findFriendship(receiverId, requesterId);
        // If both exist, delete the reverse to avoid unique constraint violation
        if (existing != null && reverse != null && reverse.getFriendshipId() != existing.getFriendshipId()) {
            String sql = "DELETE FROM friendships WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, reverse.getFriendshipId());
                stmt.executeUpdate();
            }
        }
        // Now, update or create as before
        if (existing != null) {
            String sql = "UPDATE friendships SET requester_id = ?, receiver_id = ?, status = ?, date_requested = ?, date_accepted = NULL WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, requesterId);
                stmt.setInt(2, receiverId);
                stmt.setString(3, Friendship.STATUS_PENDING);
                stmt.setTimestamp(4, new Timestamp(new Date().getTime()));
                stmt.setInt(5, existing.getFriendshipId());
                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    existing.setRequesterId(requesterId);
                    existing.setReceiverId(receiverId);
                    existing.setStatus(Friendship.STATUS_PENDING);
                    existing.setDateRequested(new Date());
                    existing.setDateAccepted(null);
                    return existing;
                }
            }
            throw new SQLException("Failed to update existing friendship record");
        }
        // No existing record, create new
        Friendship friendship = new Friendship(requesterId, receiverId);
        return createFriendship(friendship);
    }

    // ========================= READ OPERATIONS =========================

    /**
     * Find a friendship by its ID
     * @param friendshipId The friendship ID to search for
     * @return Friendship object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public Friendship findById(int friendshipId) throws SQLException {
        String sql = "SELECT id, requester_id, receiver_id, status, date_requested, date_accepted FROM friendships WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, friendshipId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFriendship(rs);
                }
            }
        }
        return null;
    }

    /**
     * Find friendship between two users (bidirectional)
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Friendship object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public Friendship findFriendship(int userId1, int userId2) throws SQLException {
        String sql = "SELECT id, requester_id, receiver_id, status, date_requested, date_accepted FROM friendships " +
                "WHERE (requester_id = ? AND receiver_id = ?) OR (requester_id = ? AND receiver_id = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFriendship(rs);
                }
            }
        }
        return null;
    }

    /**
     * Get all friends of a user (accepted friendships only)
     * @param userId The user ID
     * @return List of accepted friendships
     * @throws SQLException If database error occurs
     */
    public List<Friendship> getFriends(int userId) throws SQLException {
        String sql = "SELECT id, requester_id, receiver_id, status, date_requested, date_accepted FROM friendships " +
                "WHERE (requester_id = ? OR receiver_id = ?) AND status = ? ORDER BY date_accepted DESC";
        List<Friendship> friendships = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setString(3, Friendship.STATUS_ACCEPTED);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    friendships.add(mapRowToFriendship(rs));
                }
            }
        }
        return friendships;
    }

    /**
     * Get friend requests sent by a user
     * @param requesterId The requester's user ID
     * @return List of friend requests sent by the user
     * @throws SQLException If database error occurs
     */
    public List<Friendship> getSentFriendRequests(int requesterId) throws SQLException {
        String sql = "SELECT id, requester_id, receiver_id, status, date_requested, date_accepted FROM friendships " +
                "WHERE requester_id = ? ORDER BY date_requested DESC";
        List<Friendship> friendships = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, requesterId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    friendships.add(mapRowToFriendship(rs));
                }
            }
        }
        return friendships;
    }

    /**
     * Get friend requests received by a user
     * @param receiverId The receiver's user ID
     * @return List of friend requests received by the user
     * @throws SQLException If database error occurs
     */
    public List<Friendship> getReceivedFriendRequests(int receiverId) throws SQLException {
        String sql = "SELECT id, requester_id, receiver_id, status, date_requested, date_accepted FROM friendships " +
                "WHERE receiver_id = ? ORDER BY date_requested DESC";
        List<Friendship> friendships = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    friendships.add(mapRowToFriendship(rs));
                }
            }
        }
        return friendships;
    }

    /**
     * Get pending friend requests received by a user
     * @param receiverId The receiver's user ID
     * @return List of pending friend requests
     * @throws SQLException If database error occurs
     */
    public List<Friendship> getPendingFriendRequests(int receiverId) throws SQLException {
        String sql = "SELECT id, requester_id, receiver_id, status, date_requested, date_accepted FROM friendships " +
                "WHERE receiver_id = ? AND status = ? ORDER BY date_requested DESC";
        List<Friendship> friendships = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.setString(2, Friendship.STATUS_PENDING);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    friendships.add(mapRowToFriendship(rs));
                }
            }
        }
        return friendships;
    }

    /**
     * Get friendships by status
     * @param userId The user ID
     * @param status The friendship status
     * @return List of friendships with the specified status
     * @throws SQLException If database error occurs
     */
    public List<Friendship> getFriendshipsByStatus(int userId, String status) throws SQLException {
        String sql = "SELECT id, requester_id, receiver_id, status, date_requested, date_accepted FROM friendships " +
                "WHERE (requester_id = ? OR receiver_id = ?) AND status = ? ORDER BY date_requested DESC";
        List<Friendship> friendships = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setString(3, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    friendships.add(mapRowToFriendship(rs));
                }
            }
        }
        return friendships;
    }

    /**
     * Get all friendships involving a user
     * @param userId The user ID
     * @return List of all friendships (sent and received)
     * @throws SQLException If database error occurs
     */
    public List<Friendship> getAllFriendships(int userId) throws SQLException {
        String sql = "SELECT id, requester_id, receiver_id, status, date_requested, date_accepted FROM friendships " +
                "WHERE requester_id = ? OR receiver_id = ? ORDER BY date_requested DESC";
        List<Friendship> friendships = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    friendships.add(mapRowToFriendship(rs));
                }
            }
        }
        return friendships;
    }

    /**
     * Get friendships with pagination
     * @param userId The user ID
     * @param offset Starting position (0-based)
     * @param limit Maximum number of friendships to return
     * @return List of friendships
     * @throws SQLException If database error occurs
     */
    public List<Friendship> getFriends(int userId, int offset, int limit) throws SQLException {
        String sql = "SELECT id, requester_id, receiver_id, status, date_requested, date_accepted FROM friendships " +
                "WHERE (requester_id = ? OR receiver_id = ?) AND status = ? ORDER BY date_accepted DESC LIMIT ? OFFSET ?";
        List<Friendship> friendships = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setString(3, Friendship.STATUS_ACCEPTED);
            stmt.setInt(4, limit);
            stmt.setInt(5, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    friendships.add(mapRowToFriendship(rs));
                }
            }
        }
        return friendships;
    }

    /**
     * Get mutual friends between two users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return List of mutual friendships
     * @throws SQLException If database error occurs
     */
    public List<Friendship> getMutualFriends(int userId1, int userId2) throws SQLException {
        String sql = "SELECT DISTINCT f1.id, f1.requester_id, f1.receiver_id, f1.status, f1.date_requested, f1.date_accepted " +
                "FROM friendships f1 " +
                "JOIN friendships f2 ON " +
                "  ((f1.requester_id = f2.requester_id OR f1.requester_id = f2.receiver_id OR " +
                "    f1.receiver_id = f2.requester_id OR f1.receiver_id = f2.receiver_id) AND " +
                "   f1.id != f2.id) " +
                "WHERE f1.status = ? AND f2.status = ? AND " +
                "  ((f1.requester_id = ? OR f1.receiver_id = ?) AND " +
                "   (f2.requester_id = ? OR f2.receiver_id = ?)) AND " +
                "  f1.requester_id != ? AND f1.receiver_id != ? AND " +
                "  f2.requester_id != ? AND f2.receiver_id != ?";

        List<Friendship> mutualFriends = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, Friendship.STATUS_ACCEPTED);
            stmt.setString(2, Friendship.STATUS_ACCEPTED);
            stmt.setInt(3, userId1);
            stmt.setInt(4, userId1);
            stmt.setInt(5, userId2);
            stmt.setInt(6, userId2);
            stmt.setInt(7, userId1);
            stmt.setInt(8, userId1);
            stmt.setInt(9, userId2);
            stmt.setInt(10, userId2);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mutualFriends.add(mapRowToFriendship(rs));
                }
            }
        }
        return mutualFriends;
    }

    // ========================= UPDATE OPERATIONS =========================

    /**
     * Accept a friend request
     * @param friendshipId The friendship ID to accept
     * @return true if acceptance was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean acceptFriendRequest(int friendshipId) throws SQLException {
        String sql = "UPDATE friendships SET status = ?, date_accepted = ? WHERE id = ? AND status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, Friendship.STATUS_ACCEPTED);
            stmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            stmt.setInt(3, friendshipId);
            stmt.setString(4, Friendship.STATUS_PENDING);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Decline a friend request
     * @param friendshipId The friendship ID to decline
     * @return true if decline was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean declineFriendRequest(int friendshipId) throws SQLException {
        String sql = "UPDATE friendships SET status = ? WHERE id = ? AND status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, Friendship.STATUS_DECLINED);
            stmt.setInt(2, friendshipId);
            stmt.setString(3, Friendship.STATUS_PENDING);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Block a user (update existing friendship to blocked status)
     * @param friendshipId The friendship ID to block
     * @return true if blocking was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean blockUser(int friendshipId) throws SQLException {
        String sql = "UPDATE friendships SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, Friendship.STATUS_BLOCKED);
            stmt.setInt(2, friendshipId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Update friendship status
     * @param friendshipId The friendship ID
     * @param newStatus New status
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean updateFriendshipStatus(int friendshipId, String newStatus) throws SQLException {
        String sql = "UPDATE friendships SET status = ?" +
                (Friendship.STATUS_ACCEPTED.equals(newStatus) ? ", date_accepted = ?" : "") +
                " WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newStatus);

            if (Friendship.STATUS_ACCEPTED.equals(newStatus)) {
                stmt.setTimestamp(2, new Timestamp(new Date().getTime()));
                stmt.setInt(3, friendshipId);
            } else {
                stmt.setInt(2, friendshipId);
            }

            return stmt.executeUpdate() > 0;
        }
    }

    // ========================= DELETE OPERATIONS =========================

    /**
     * Delete a friendship by ID
     * @param friendshipId The friendship ID to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean deleteFriendship(int friendshipId) throws SQLException {
        String sql = "DELETE FROM friendships WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, friendshipId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Remove friendship between two users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean removeFriendship(int userId1, int userId2) throws SQLException {
        String sql = "DELETE FROM friendships WHERE (requester_id = ? AND receiver_id = ?) OR (requester_id = ? AND receiver_id = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete all friendships for a user
     * @param userId The user ID
     * @return Number of friendships deleted
     * @throws SQLException If database error occurs
     */
    public int deleteAllFriendships(int userId) throws SQLException {
        String sql = "DELETE FROM friendships WHERE requester_id = ? OR receiver_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate();
        }
    }

    /**
     * Delete friendships by status for a user
     * @param userId The user ID
     * @param status Status of friendships to delete
     * @return Number of friendships deleted
     * @throws SQLException If database error occurs
     */
    public int deleteFriendshipsByStatus(int userId, String status) throws SQLException {
        String sql = "DELETE FROM friendships WHERE (requester_id = ? OR receiver_id = ?) AND status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setString(3, status);
            return stmt.executeUpdate();
        }
    }

    /**
     * Delete old declined/blocked friendships
     * @param days Number of days (friendships older than this will be deleted)
     * @return Number of friendships deleted
     * @throws SQLException If database error occurs
     */
    public int deleteOldFriendships(int days) throws SQLException {
        String sql = "DELETE FROM friendships WHERE status IN (?, ?) AND date_requested < DATE_SUB(NOW(), INTERVAL ? DAY)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, Friendship.STATUS_DECLINED);
            stmt.setString(2, Friendship.STATUS_BLOCKED);
            stmt.setInt(3, days);
            return stmt.executeUpdate();
        }
    }

    // ========================= VALIDATION AND UTILITY METHODS =========================

    /**
     * Check if a friendship exists between two users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return true if friendship exists, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean friendshipExists(int userId1, int userId2) throws SQLException {
        String sql = "SELECT 1 FROM friendships WHERE (requester_id = ? AND receiver_id = ?) OR (requester_id = ? AND receiver_id = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Check if two users are friends (accepted friendship)
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return true if users are friends, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean areFriends(int userId1, int userId2) throws SQLException {
        String sql = "SELECT 1 FROM friendships WHERE ((requester_id = ? AND receiver_id = ?) OR (requester_id = ? AND receiver_id = ?)) AND status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            stmt.setString(5, Friendship.STATUS_ACCEPTED);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Check if there's a pending friend request between users
     * @param requesterId The requester's user ID
     * @param receiverId The receiver's user ID
     * @return true if there's a pending request, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean hasPendingRequest(int requesterId, int receiverId) throws SQLException {
        String sql = "SELECT 1 FROM friendships WHERE requester_id = ? AND receiver_id = ? AND status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, requesterId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, Friendship.STATUS_PENDING);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Get friend count for a user
     * @param userId The user ID
     * @return Number of accepted friends
     * @throws SQLException If database error occurs
     */
    public int getFriendCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM friendships WHERE (requester_id = ? OR receiver_id = ?) AND status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setString(3, Friendship.STATUS_ACCEPTED);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Get pending friend request count for a user
     * @param receiverId The receiver's user ID
     * @return Number of pending friend requests
     * @throws SQLException If database error occurs
     */
    public int getPendingRequestCount(int receiverId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM friendships WHERE receiver_id = ? AND status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.setString(2, Friendship.STATUS_PENDING);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Get friendship statistics for a user
     * @param userId The user ID
     * @return Array with [friends count, pending received, pending sent, blocked]
     * @throws SQLException If database error occurs
     */
    public int[] getFriendshipStats(int userId) throws SQLException {
        int friends = getFriendCount(userId);
        int pendingReceived = getPendingRequestCount(userId);

        // Get pending sent count
        String sentSql = "SELECT COUNT(*) FROM friendships WHERE requester_id = ? AND status = ?";
        int pendingSent = 0;
        try (PreparedStatement stmt = connection.prepareStatement(sentSql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, Friendship.STATUS_PENDING);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pendingSent = rs.getInt(1);
                }
            }
        }

        // Get blocked count
        String blockedSql = "SELECT COUNT(*) FROM friendships WHERE (requester_id = ? OR receiver_id = ?) AND status = ?";
        int blocked = 0;
        try (PreparedStatement stmt = connection.prepareStatement(blockedSql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setString(3, Friendship.STATUS_BLOCKED);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    blocked = rs.getInt(1);
                }
            }
        }

        return new int[]{friends, pendingReceived, pendingSent, blocked};
    }

    /**
     * Get friendship status between two users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Friendship status or null if no friendship exists
     * @throws SQLException If database error occurs
     */
    public String getFriendshipStatus(int userId1, int userId2) throws SQLException {
        Friendship friendship = findFriendship(userId1, userId2);
        return friendship != null ? friendship.getStatus() : null;
    }

    /**
     * Get list of user IDs who are friends with the given user
     * @param userId The user ID
     * @return List of friend user IDs
     * @throws SQLException If database error occurs
     */
    public List<Integer> getFriendIds(int userId) throws SQLException {
        List<Friendship> friends = getFriends(userId);
        List<Integer> friendIds = new ArrayList<>();

        for (Friendship friendship : friends) {
            friendIds.add(friendship.getFriendId(userId));
        }

        return friendIds;
    }

    // ========================= HELPER METHODS =========================

    /**
     * Map a ResultSet row to a Friendship object
     * @param rs The ResultSet positioned at a valid row
     * @return Friendship object
     * @throws SQLException If database error occurs
     */
    private Friendship mapRowToFriendship(ResultSet rs) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setFriendshipId(rs.getInt("id"));
        friendship.setRequesterId(rs.getInt("requester_id"));
        friendship.setReceiverId(rs.getInt("receiver_id"));
        friendship.setStatus(rs.getString("status"));

        Timestamp dateRequestedTimestamp = rs.getTimestamp("date_requested");
        if (dateRequestedTimestamp != null) {
            friendship.setDateRequested(new Date(dateRequestedTimestamp.getTime()));
        }

        Timestamp dateAcceptedTimestamp = rs.getTimestamp("date_accepted");
        if (dateAcceptedTimestamp != null) {
            friendship.setDateAccepted(new Date(dateAcceptedTimestamp.getTime()));
        }

        return friendship;
    }
}