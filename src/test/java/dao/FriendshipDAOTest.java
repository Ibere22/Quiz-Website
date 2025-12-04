package dao;

import model.Friendship;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FriendshipDAO class
 * Tests all CRUD operations, friendship-specific functionality, validation methods, and statistics
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FriendshipDAOTest {

    private static Connection connection;
    private static FriendshipDAO friendshipDAO;
    private static final int TEST_REQUESTER_ID = 1;
    private static final int TEST_RECEIVER_ID = 2;
    private static final int TEST_USER3_ID = 3;
    private static final int TEST_USER4_ID = 4;

    @BeforeAll
    static void setUpClass() throws SQLException {
        connection = DatabaseConnection.getConnection();
        friendshipDAO = new FriendshipDAO(connection);
        
        // Clean up any existing test data
        cleanUpTestData();
        
        // Create test users if they don't exist
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
        cleanUpTestFriendships();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up after each test
        cleanUpTestFriendships();
    }

    private static void cleanUpTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Delete friendships from test users first
            stmt.executeUpdate("DELETE FROM friendships WHERE requester_id IN (1, 2, 3, 4) OR receiver_id IN (1, 2, 3, 4)");
            // Note: We don't delete test users as they might be used by other tests
        }
    }

    private static void cleanUpTestFriendships() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Delete ALL friendships from test users to ensure clean state
            stmt.executeUpdate("DELETE FROM friendships WHERE requester_id IN (1, 2, 3, 4) OR receiver_id IN (1, 2, 3, 4)");
        }
    }

    private static void createTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Insert test users if they don't exist
            stmt.executeUpdate("INSERT IGNORE INTO users (id, username, password_hash, email) VALUES " +
                "(1, 'testuser1', 'hash1', 'test1@example.com'), " +
                "(2, 'testuser2', 'hash2', 'test2@example.com'), " +
                "(3, 'testuser3', 'hash3', 'test3@example.com'), " +
                "(4, 'testuser4', 'hash4', 'test4@example.com')");
        }
    }

    // ========================= CREATE OPERATION TESTS =========================

    @Test
    @Order(1)
    @DisplayName("Test create friendship with object")
    void testCreateFriendship_Success() throws SQLException {
        // Arrange
        Friendship friendship = new Friendship(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Act
        Friendship createdFriendship = friendshipDAO.createFriendship(friendship);

        // Assert
        assertNotNull(createdFriendship, "Friendship should be created successfully");
        assertTrue(createdFriendship.getFriendshipId() > 0, "Friendship ID should be generated");
        assertEquals(TEST_REQUESTER_ID, createdFriendship.getRequesterId());
        assertEquals(TEST_RECEIVER_ID, createdFriendship.getReceiverId());
        assertEquals(Friendship.STATUS_PENDING, createdFriendship.getStatus());
        assertNotNull(createdFriendship.getDateRequested());
        assertNull(createdFriendship.getDateAccepted());
    }

    @Test
    @Order(2)
    @DisplayName("Test send friend request")
    void testSendFriendRequest_Success() throws SQLException {
        // Act
        Friendship sentRequest = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Assert
        assertNotNull(sentRequest, "Friend request should be sent successfully");
        assertTrue(sentRequest.getFriendshipId() > 0, "Friendship ID should be generated");
        assertEquals(TEST_REQUESTER_ID, sentRequest.getRequesterId());
        assertEquals(TEST_RECEIVER_ID, sentRequest.getReceiverId());
        assertEquals(Friendship.STATUS_PENDING, sentRequest.getStatus());
        assertNotNull(sentRequest.getDateRequested());
        assertNull(sentRequest.getDateAccepted());
    }

    @Test
    @Order(3)
    @DisplayName("Test send friend request when friendship already exists")
    void testSendFriendRequest_AlreadyExists_ThrowsException() throws SQLException {
        // Clean up any existing friendship
        Friendship existing = friendshipDAO.findFriendship(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        if (existing != null) {
            friendshipDAO.removeFriendship(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        }
        // Arrange - create existing friendship
        friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, () -> {
            friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        });

        // Accept either message for already friends or already pending
        String msg = exception.getMessage();
        boolean valid = "Users are already friends".equals(msg) || "Friend request already sent".equals(msg) || "Friend request already received".equals(msg);
        assertTrue(valid, "Exception message should indicate existing friendship or pending request, but was: " + msg);
    }

    @Test
    @Order(4)
    @DisplayName("Test create friendship with accepted status")
    void testCreateFriendship_AcceptedStatus_Success() throws SQLException {
        // Arrange
        Friendship friendship = new Friendship(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        friendship.setStatus(Friendship.STATUS_ACCEPTED);
        friendship.setDateAccepted(new Date());

        // Act
        Friendship createdFriendship = friendshipDAO.createFriendship(friendship);

        // Assert
        assertNotNull(createdFriendship, "Friendship should be created successfully");
        assertEquals(Friendship.STATUS_ACCEPTED, createdFriendship.getStatus());
        assertNotNull(createdFriendship.getDateAccepted());
    }

    // ========================= READ OPERATION TESTS =========================

    @Test
    @Order(5)
    @DisplayName("Test find friendship by ID")
    void testFindById_ExistingFriendship_Success() throws SQLException {
        // Arrange
        Friendship createdFriendship = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Act
        Friendship foundFriendship = friendshipDAO.findById(createdFriendship.getFriendshipId());

        // Assert
        assertNotNull(foundFriendship, "Friendship should be found");
        assertEquals(createdFriendship.getFriendshipId(), foundFriendship.getFriendshipId());
        assertEquals(TEST_REQUESTER_ID, foundFriendship.getRequesterId());
        assertEquals(TEST_RECEIVER_ID, foundFriendship.getReceiverId());
        assertEquals(Friendship.STATUS_PENDING, foundFriendship.getStatus());
    }

    @Test
    @Order(6)
    @DisplayName("Test find friendship by non-existent ID")
    void testFindById_NonExistentFriendship_ReturnsNull() throws SQLException {
        // Act
        Friendship foundFriendship = friendshipDAO.findById(99999);

        // Assert
        assertNull(foundFriendship, "Should return null for non-existent friendship");
    }

    @Test
    @Order(7)
    @DisplayName("Test find friendship between two users")
    void testFindFriendship_ExistingFriendship_Success() throws SQLException {
        // Arrange
        Friendship createdFriendship = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Act - test both directions
        Friendship foundFriendship1 = friendshipDAO.findFriendship(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        Friendship foundFriendship2 = friendshipDAO.findFriendship(TEST_RECEIVER_ID, TEST_REQUESTER_ID);

        // Assert
        assertNotNull(foundFriendship1, "Friendship should be found (direction 1)");
        assertNotNull(foundFriendship2, "Friendship should be found (direction 2)");
        assertEquals(createdFriendship.getFriendshipId(), foundFriendship1.getFriendshipId());
        assertEquals(createdFriendship.getFriendshipId(), foundFriendship2.getFriendshipId());
    }

    @Test
    @Order(8)
    @DisplayName("Test find friendship between non-connected users")
    void testFindFriendship_NonExistentFriendship_ReturnsNull() throws SQLException {
        // Act
        Friendship foundFriendship = friendshipDAO.findFriendship(TEST_REQUESTER_ID, TEST_USER3_ID);

        // Assert
        assertNull(foundFriendship, "Should return null for non-existent friendship");
    }

    @Test
    @Order(9)
    @DisplayName("Test get friends (accepted friendships only)")
    void testGetFriends_Success() throws SQLException {
        // Arrange
        Friendship pending = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        Friendship accepted1 = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_USER3_ID);
        Friendship accepted2 = friendshipDAO.sendFriendRequest(TEST_USER4_ID, TEST_REQUESTER_ID);
        
        // Accept some friendships
        friendshipDAO.acceptFriendRequest(accepted1.getFriendshipId());
        friendshipDAO.acceptFriendRequest(accepted2.getFriendshipId());

        // Act
        List<Friendship> friends = friendshipDAO.getFriends(TEST_REQUESTER_ID);

        // Assert
        assertEquals(2, friends.size(), "Should return 2 accepted friendships");
        
        // Verify all friendships involve the user and are accepted
        for (Friendship friendship : friends) {
            assertTrue(friendship.involves(TEST_REQUESTER_ID), "Friendship should involve the user");
            assertEquals(Friendship.STATUS_ACCEPTED, friendship.getStatus(), "Friendship should be accepted");
        }
    }

    @Test
    @Order(10)
    @DisplayName("Test get sent friend requests")
    void testGetSentFriendRequests_Success() throws SQLException {
        // Arrange
        friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_USER3_ID);
        friendshipDAO.sendFriendRequest(TEST_USER4_ID, TEST_REQUESTER_ID); // Different requester

        // Act
        List<Friendship> sentRequests = friendshipDAO.getSentFriendRequests(TEST_REQUESTER_ID);

        // Assert
        assertEquals(2, sentRequests.size(), "Should return 2 sent friend requests");
        
        // Verify all requests are from the correct requester
        for (Friendship friendship : sentRequests) {
            assertEquals(TEST_REQUESTER_ID, friendship.getRequesterId(), "All requests should be from the correct requester");
        }
    }

    @Test
    @Order(11)
    @DisplayName("Test get received friend requests")
    void testGetReceivedFriendRequests_Success() throws SQLException {
        // Arrange
        friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        friendshipDAO.sendFriendRequest(TEST_USER3_ID, TEST_RECEIVER_ID);
        friendshipDAO.sendFriendRequest(TEST_RECEIVER_ID, TEST_USER4_ID); // Different receiver

        // Act
        List<Friendship> receivedRequests = friendshipDAO.getReceivedFriendRequests(TEST_RECEIVER_ID);

        // Assert
        assertEquals(2, receivedRequests.size(), "Should return 2 received friend requests");
        
        // Verify all requests are to the correct receiver
        for (Friendship friendship : receivedRequests) {
            assertEquals(TEST_RECEIVER_ID, friendship.getReceiverId(), "All requests should be to the correct receiver");
        }
    }

    @Test
    @Order(12)
    @DisplayName("Test get pending friend requests")
    void testGetPendingFriendRequests_Success() throws SQLException {
        // Arrange
        Friendship pending1 = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        Friendship pending2 = friendshipDAO.sendFriendRequest(TEST_USER3_ID, TEST_RECEIVER_ID);
        Friendship accepted = friendshipDAO.sendFriendRequest(TEST_USER4_ID, TEST_RECEIVER_ID);
        
        // Accept one request
        friendshipDAO.acceptFriendRequest(accepted.getFriendshipId());

        // Act
        List<Friendship> pendingRequests = friendshipDAO.getPendingFriendRequests(TEST_RECEIVER_ID);

        // Assert
        assertEquals(2, pendingRequests.size(), "Should return 2 pending friend requests");
        
        // Verify all requests are pending
        for (Friendship friendship : pendingRequests) {
            assertEquals(Friendship.STATUS_PENDING, friendship.getStatus(), "All requests should be pending");
            assertEquals(TEST_RECEIVER_ID, friendship.getReceiverId(), "All requests should be to the correct receiver");
        }
    }

    @Test
    @Order(13)
    @DisplayName("Test get friendships by status")
    void testGetFriendshipsByStatus_Success() throws SQLException {
        // Arrange
        Friendship pending = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        Friendship accepted = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_USER3_ID);
        Friendship declined = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_USER4_ID);
        
        // Update statuses
        friendshipDAO.acceptFriendRequest(accepted.getFriendshipId());
        friendshipDAO.declineFriendRequest(declined.getFriendshipId());

        // Act
        List<Friendship> pendingFriendships = friendshipDAO.getFriendshipsByStatus(TEST_REQUESTER_ID, Friendship.STATUS_PENDING);
        List<Friendship> acceptedFriendships = friendshipDAO.getFriendshipsByStatus(TEST_REQUESTER_ID, Friendship.STATUS_ACCEPTED);
        List<Friendship> declinedFriendships = friendshipDAO.getFriendshipsByStatus(TEST_REQUESTER_ID, Friendship.STATUS_DECLINED);

        // Assert
        assertEquals(1, pendingFriendships.size(), "Should have 1 pending friendship");
        assertEquals(1, acceptedFriendships.size(), "Should have 1 accepted friendship");
        assertEquals(1, declinedFriendships.size(), "Should have 1 declined friendship");
        
        // Verify statuses
        assertEquals(Friendship.STATUS_PENDING, pendingFriendships.get(0).getStatus());
        assertEquals(Friendship.STATUS_ACCEPTED, acceptedFriendships.get(0).getStatus());
        assertEquals(Friendship.STATUS_DECLINED, declinedFriendships.get(0).getStatus());
    }

    @Test
    @Order(14)
    @DisplayName("Test get all friendships")
    void testGetAllFriendships_Success() throws SQLException {
        // Arrange
        friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        friendshipDAO.sendFriendRequest(TEST_USER3_ID, TEST_REQUESTER_ID);
        friendshipDAO.sendFriendRequest(TEST_USER4_ID, TEST_RECEIVER_ID); // Not involving TEST_REQUESTER_ID

        // Act
        List<Friendship> allFriendships = friendshipDAO.getAllFriendships(TEST_REQUESTER_ID);

        // Assert
        assertEquals(2, allFriendships.size(), "Should return 2 friendships involving the user");
        
        // Verify all friendships involve the user
        for (Friendship friendship : allFriendships) {
            assertTrue(friendship.involves(TEST_REQUESTER_ID), "All friendships should involve the user");
        }
    }

    @Test
    @Order(15)
    @DisplayName("Test get friends with pagination")
    void testGetFriends_WithPagination_Success() throws SQLException {
        // Arrange - create multiple accepted friendships
        Friendship f1 = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        Friendship f2 = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_USER3_ID);
        Friendship f3 = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_USER4_ID);
        
        // Accept all friendships
        friendshipDAO.acceptFriendRequest(f1.getFriendshipId());
        friendshipDAO.acceptFriendRequest(f2.getFriendshipId());
        friendshipDAO.acceptFriendRequest(f3.getFriendshipId());

        // Act
        List<Friendship> firstPage = friendshipDAO.getFriends(TEST_REQUESTER_ID, 0, 2);
        List<Friendship> secondPage = friendshipDAO.getFriends(TEST_REQUESTER_ID, 2, 2);

        // Assert
        assertEquals(2, firstPage.size(), "First page should have 2 friendships");
        assertEquals(1, secondPage.size(), "Second page should have 1 friendship");
        
        // Verify all are accepted
        for (Friendship friendship : firstPage) {
            assertEquals(Friendship.STATUS_ACCEPTED, friendship.getStatus());
        }
        for (Friendship friendship : secondPage) {
            assertEquals(Friendship.STATUS_ACCEPTED, friendship.getStatus());
        }
    }

    @Test
    @Order(16)
    @DisplayName("Test get mutual friends")
    void testGetMutualFriends_Success() throws SQLException {
        // This is a complex test that would require more sophisticated setup
        // For now, we'll test that the method doesn't throw an exception
        assertDoesNotThrow(() -> {
            List<Friendship> mutualFriends = friendshipDAO.getMutualFriends(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
            assertNotNull(mutualFriends, "Should return a list (even if empty)");
        });
    }

    // ========================= UPDATE OPERATION TESTS =========================

    @Test
    @Order(17)
    @DisplayName("Test accept friend request")
    void testAcceptFriendRequest_Success() throws SQLException {
        // Arrange
        Friendship createdFriendship = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        assertEquals(Friendship.STATUS_PENDING, createdFriendship.getStatus());

        // Act
        boolean accepted = friendshipDAO.acceptFriendRequest(createdFriendship.getFriendshipId());

        // Assert
        assertTrue(accepted, "Friend request should be accepted successfully");
        
        Friendship updatedFriendship = friendshipDAO.findById(createdFriendship.getFriendshipId());
        assertEquals(Friendship.STATUS_ACCEPTED, updatedFriendship.getStatus());
        assertNotNull(updatedFriendship.getDateAccepted());
    }

    @Test
    @Order(18)
    @DisplayName("Test accept non-pending friend request")
    void testAcceptFriendRequest_NonPending_ReturnsFalse() throws SQLException {
        // Arrange
        Friendship createdFriendship = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        friendshipDAO.acceptFriendRequest(createdFriendship.getFriendshipId()); // Already accepted

        // Act
        boolean accepted = friendshipDAO.acceptFriendRequest(createdFriendship.getFriendshipId());

        // Assert
        assertFalse(accepted, "Should return false for already accepted request");
    }

    @Test
    @Order(19)
    @DisplayName("Test decline friend request")
    void testDeclineFriendRequest_Success() throws SQLException {
        // Arrange
        Friendship createdFriendship = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Act
        boolean declined = friendshipDAO.declineFriendRequest(createdFriendship.getFriendshipId());

        // Assert
        assertTrue(declined, "Friend request should be declined successfully");
        
        Friendship updatedFriendship = friendshipDAO.findById(createdFriendship.getFriendshipId());
        assertEquals(Friendship.STATUS_DECLINED, updatedFriendship.getStatus());
    }

    @Test
    @Order(20)
    @DisplayName("Test block user")
    void testBlockUser_Success() throws SQLException {
        // Arrange
        Friendship createdFriendship = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Act
        boolean blocked = friendshipDAO.blockUser(createdFriendship.getFriendshipId());

        // Assert
        assertTrue(blocked, "User should be blocked successfully");
        
        Friendship updatedFriendship = friendshipDAO.findById(createdFriendship.getFriendshipId());
        assertEquals(Friendship.STATUS_BLOCKED, updatedFriendship.getStatus());
    }

    @Test
    @Order(21)
    @DisplayName("Test update friendship status")
    void testUpdateFriendshipStatus_Success() throws SQLException {
        // Arrange
        Friendship createdFriendship = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Act - update to accepted (should set date_accepted)
        boolean updated1 = friendshipDAO.updateFriendshipStatus(createdFriendship.getFriendshipId(), Friendship.STATUS_ACCEPTED);
        
        // Verify
        assertTrue(updated1, "Status should be updated successfully");
        Friendship updatedFriendship1 = friendshipDAO.findById(createdFriendship.getFriendshipId());
        assertEquals(Friendship.STATUS_ACCEPTED, updatedFriendship1.getStatus());
        assertNotNull(updatedFriendship1.getDateAccepted());

        // Act - update to blocked (should not affect date_accepted)
        boolean updated2 = friendshipDAO.updateFriendshipStatus(createdFriendship.getFriendshipId(), Friendship.STATUS_BLOCKED);
        
        // Verify
        assertTrue(updated2, "Status should be updated successfully");
        Friendship updatedFriendship2 = friendshipDAO.findById(createdFriendship.getFriendshipId());
        assertEquals(Friendship.STATUS_BLOCKED, updatedFriendship2.getStatus());
    }

    // ========================= DELETE OPERATION TESTS =========================

    @Test
    @Order(22)
    @DisplayName("Test delete friendship")
    void testDeleteFriendship_Success() throws SQLException {
        // Arrange
        Friendship createdFriendship = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Act
        boolean deleted = friendshipDAO.deleteFriendship(createdFriendship.getFriendshipId());

        // Assert
        assertTrue(deleted, "Friendship deletion should be successful");
        
        Friendship foundFriendship = friendshipDAO.findById(createdFriendship.getFriendshipId());
        assertNull(foundFriendship, "Friendship should not be found after deletion");
    }

    @Test
    @Order(23)
    @DisplayName("Test remove friendship between users")
    void testRemoveFriendship_Success() throws SQLException {
        // Arrange
        friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Act
        boolean removed = friendshipDAO.removeFriendship(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Assert
        assertTrue(removed, "Friendship removal should be successful");
        
        Friendship foundFriendship = friendshipDAO.findFriendship(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        assertNull(foundFriendship, "Friendship should not be found after removal");
    }

    @Test
    @Order(24)
    @DisplayName("Test delete all friendships for user")
    void testDeleteAllFriendships_Success() throws SQLException {
        // Arrange - ensure user has no friendships first
        friendshipDAO.deleteAllFriendships(TEST_REQUESTER_ID);
        
        // Create exactly 3 friendships for this user
        friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_USER3_ID);
        friendshipDAO.sendFriendRequest(TEST_USER4_ID, TEST_REQUESTER_ID);

        // Verify we have exactly 3 friendships before deletion
        int countBeforeDeletion = friendshipDAO.getAllFriendships(TEST_REQUESTER_ID).size();
        assertEquals(3, countBeforeDeletion, "Should have exactly 3 friendships before deletion");

        // Act
        int deletedCount = friendshipDAO.deleteAllFriendships(TEST_REQUESTER_ID);

        // Assert
        assertEquals(3, deletedCount, "Should delete exactly 3 friendships");
        
        List<Friendship> remainingFriendships = friendshipDAO.getAllFriendships(TEST_REQUESTER_ID);
        assertEquals(0, remainingFriendships.size(), "No friendships should remain for the user");
    }

    @Test
    @Order(25)
    @DisplayName("Test delete friendships by status")
    void testDeleteFriendshipsByStatus_Success() throws SQLException {
        // Arrange
        Friendship pending1 = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        Friendship pending2 = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_USER3_ID);
        Friendship accepted = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_USER4_ID);
        
        // Accept one friendship
        friendshipDAO.acceptFriendRequest(accepted.getFriendshipId());

        // Act
        int deletedCount = friendshipDAO.deleteFriendshipsByStatus(TEST_REQUESTER_ID, Friendship.STATUS_PENDING);

        // Assert
        assertEquals(2, deletedCount, "Should delete 2 pending friendships");
        
        List<Friendship> remainingFriendships = friendshipDAO.getAllFriendships(TEST_REQUESTER_ID);
        assertEquals(1, remainingFriendships.size(), "Should have 1 remaining friendship");
        assertEquals(Friendship.STATUS_ACCEPTED, remainingFriendships.get(0).getStatus(), "Remaining friendship should be accepted");
    }

    @Test
    @Order(26)
    @DisplayName("Test delete old friendships")
    void testDeleteOldFriendships_Success() throws SQLException {
        // This test is more of a functional test - we can't easily create "old" friendships
        // without manipulating timestamps, so we'll just verify the method doesn't fail
        
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> {
            int deletedCount = friendshipDAO.deleteOldFriendships(365); // Delete friendships older than 1 year
            assertTrue(deletedCount >= 0, "Should return non-negative count");
        });
    }

    // ========================= VALIDATION AND UTILITY TESTS =========================

    @Test
    @Order(27)
    @DisplayName("Test friendship exists check")
    void testFriendshipExists_Success() throws SQLException {
        // Test non-existent friendship
        assertFalse(friendshipDAO.friendshipExists(TEST_REQUESTER_ID, TEST_USER3_ID), 
            "Should return false for non-existent friendship");

        // Arrange - create friendship
        friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Test existing friendship
        assertTrue(friendshipDAO.friendshipExists(TEST_REQUESTER_ID, TEST_RECEIVER_ID), 
            "Should return true for existing friendship");
        assertTrue(friendshipDAO.friendshipExists(TEST_RECEIVER_ID, TEST_REQUESTER_ID), 
            "Should return true for existing friendship (reverse direction)");
    }

    @Test
    @Order(28)
    @DisplayName("Test are friends check")
    void testAreFriends_Success() throws SQLException {
        // Test non-friends
        assertFalse(friendshipDAO.areFriends(TEST_REQUESTER_ID, TEST_RECEIVER_ID), 
            "Should return false for non-friends");

        // Arrange - create and accept friendship
        Friendship createdFriendship = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        
        // Test pending friendship (not friends yet)
        assertFalse(friendshipDAO.areFriends(TEST_REQUESTER_ID, TEST_RECEIVER_ID), 
            "Should return false for pending friendship");

        // Accept friendship
        friendshipDAO.acceptFriendRequest(createdFriendship.getFriendshipId());

        // Test accepted friendship
        assertTrue(friendshipDAO.areFriends(TEST_REQUESTER_ID, TEST_RECEIVER_ID), 
            "Should return true for accepted friendship");
        assertTrue(friendshipDAO.areFriends(TEST_RECEIVER_ID, TEST_REQUESTER_ID), 
            "Should return true for accepted friendship (reverse direction)");
    }

    @Test
    @Order(29)
    @DisplayName("Test has pending request check")
    void testHasPendingRequest_Success() throws SQLException {
        // Test no pending request
        assertFalse(friendshipDAO.hasPendingRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID), 
            "Should return false when no pending request exists");

        // Arrange - send friend request
        Friendship createdFriendship = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Test pending request exists
        assertTrue(friendshipDAO.hasPendingRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID), 
            "Should return true when pending request exists");

        // Test reverse direction (should be false)
        assertFalse(friendshipDAO.hasPendingRequest(TEST_RECEIVER_ID, TEST_REQUESTER_ID), 
            "Should return false for reverse direction");

        // Accept request and test again
        friendshipDAO.acceptFriendRequest(createdFriendship.getFriendshipId());
        assertFalse(friendshipDAO.hasPendingRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID), 
            "Should return false when request is accepted");
    }

    @Test
    @Order(30)
    @DisplayName("Test get friend count")
    void testGetFriendCount_Success() throws SQLException {
        // Test initial count
        assertEquals(0, friendshipDAO.getFriendCount(TEST_REQUESTER_ID), "Should have 0 friends initially");

        // Arrange - create and accept friendships
        Friendship f1 = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        Friendship f2 = friendshipDAO.sendFriendRequest(TEST_USER3_ID, TEST_REQUESTER_ID);
        Friendship f3 = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_USER4_ID); // Keep pending
        
        // Accept some friendships
        friendshipDAO.acceptFriendRequest(f1.getFriendshipId());
        friendshipDAO.acceptFriendRequest(f2.getFriendshipId());

        // Act
        int friendCount = friendshipDAO.getFriendCount(TEST_REQUESTER_ID);

        // Assert
        assertEquals(2, friendCount, "Should have 2 accepted friends");
    }

    @Test
    @Order(31)
    @DisplayName("Test get pending request count")
    void testGetPendingRequestCount_Success() throws SQLException {
        // Test initial count
        assertEquals(0, friendshipDAO.getPendingRequestCount(TEST_RECEIVER_ID), 
            "Should have 0 pending requests initially");

        // Arrange - send friend requests to receiver
        friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        friendshipDAO.sendFriendRequest(TEST_USER3_ID, TEST_RECEIVER_ID);
        Friendship accepted = friendshipDAO.sendFriendRequest(TEST_USER4_ID, TEST_RECEIVER_ID);
        
        // Accept one request
        friendshipDAO.acceptFriendRequest(accepted.getFriendshipId());

        // Act
        int pendingCount = friendshipDAO.getPendingRequestCount(TEST_RECEIVER_ID);

        // Assert
        assertEquals(2, pendingCount, "Should have 2 pending requests");
    }

    @Test
    @Order(32)
    @DisplayName("Test get friendship statistics")
    void testGetFriendshipStats_Success() throws SQLException {
        // Clean up any existing friendships first to ensure accurate counts
        friendshipDAO.deleteAllFriendships(TEST_REQUESTER_ID);
        friendshipDAO.deleteAllFriendships(TEST_RECEIVER_ID);
        friendshipDAO.deleteAllFriendships(TEST_USER3_ID);
        friendshipDAO.deleteAllFriendships(TEST_USER4_ID);
        
        // Arrange - create distinct friendships to avoid conflicts
        // TEST_REQUESTER_ID (1) sends to TEST_RECEIVER_ID (2) - will be accepted
        Friendship accepted1 = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        
        // TEST_USER3_ID (3) sends to TEST_REQUESTER_ID (1) - will be accepted  
        Friendship accepted2 = friendshipDAO.sendFriendRequest(TEST_USER3_ID, TEST_REQUESTER_ID);
        
        // TEST_USER4_ID (4) sends to TEST_REQUESTER_ID (1) - will stay pending (received)
        Friendship pendingReceived = friendshipDAO.sendFriendRequest(TEST_USER4_ID, TEST_REQUESTER_ID);
        
        // We already have enough relationships for this user, so let's not create pendingSent
        // to avoid conflicts. We'll test with 2 friends, 1 pending received, 0 pending sent
        
        // Accept the first two friendships
        friendshipDAO.acceptFriendRequest(accepted1.getFriendshipId());
        friendshipDAO.acceptFriendRequest(accepted2.getFriendshipId());

        // Act
        int[] stats = friendshipDAO.getFriendshipStats(TEST_REQUESTER_ID);

        // Assert
        assertEquals(4, stats.length, "Stats array should have 4 elements");
        assertEquals(2, stats[0], "Should have 2 friends");
        assertEquals(1, stats[1], "Should have 1 pending received request");
        assertEquals(0, stats[2], "Should have 0 pending sent requests");
        assertEquals(0, stats[3], "Should have 0 blocked relationships for this user");
    }

    @Test
    @Order(33)
    @DisplayName("Test get friendship status")
    void testGetFriendshipStatus_Success() throws SQLException {
        // Test no friendship
        assertNull(friendshipDAO.getFriendshipStatus(TEST_REQUESTER_ID, TEST_USER3_ID), 
            "Should return null when no friendship exists");

        // Arrange - create friendship
        Friendship createdFriendship = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);

        // Test pending status
        assertEquals(Friendship.STATUS_PENDING, 
            friendshipDAO.getFriendshipStatus(TEST_REQUESTER_ID, TEST_RECEIVER_ID), 
            "Should return pending status");

        // Accept and test again
        friendshipDAO.acceptFriendRequest(createdFriendship.getFriendshipId());
        assertEquals(Friendship.STATUS_ACCEPTED, 
            friendshipDAO.getFriendshipStatus(TEST_REQUESTER_ID, TEST_RECEIVER_ID), 
            "Should return accepted status");
    }

    @Test
    @Order(34)
    @DisplayName("Test get friend IDs")
    void testGetFriendIds_Success() throws SQLException {
        // Arrange - create and accept friendships
        Friendship f1 = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        Friendship f2 = friendshipDAO.sendFriendRequest(TEST_USER3_ID, TEST_REQUESTER_ID);
        friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_USER4_ID); // Keep pending
        
        friendshipDAO.acceptFriendRequest(f1.getFriendshipId());
        friendshipDAO.acceptFriendRequest(f2.getFriendshipId());

        // Act
        List<Integer> friendIds = friendshipDAO.getFriendIds(TEST_REQUESTER_ID);

        // Assert
        assertEquals(2, friendIds.size(), "Should have 2 friend IDs");
        assertTrue(friendIds.contains(TEST_RECEIVER_ID), "Should contain receiver ID");
        assertTrue(friendIds.contains(TEST_USER3_ID), "Should contain user 3 ID");
        assertFalse(friendIds.contains(TEST_USER4_ID), "Should not contain user 4 ID (pending)");
    }

    @Test
    @Order(35)
    @DisplayName("Test edge cases and null handling")
    void testEdgeCases_Success() throws SQLException {
        // Test empty results
        List<Friendship> emptyFriends = friendshipDAO.getFriends(99999);
        assertEquals(0, emptyFriends.size(), "Should return empty list for non-existent user");

        List<Friendship> emptyByStatus = friendshipDAO.getFriendshipsByStatus(99999, Friendship.STATUS_ACCEPTED);
        assertEquals(0, emptyByStatus.size(), "Should return empty list for non-existent user");

        // Test counts for non-existent data
        assertEquals(0, friendshipDAO.getFriendCount(99999), "Should return 0 for non-existent user");
        assertEquals(0, friendshipDAO.getPendingRequestCount(99999), "Should return 0 for non-existent user");

        // Test operations on non-existent friendships
        assertFalse(friendshipDAO.acceptFriendRequest(99999), "Should return false for non-existent friendship");
        assertFalse(friendshipDAO.declineFriendRequest(99999), "Should return false for non-existent friendship");
        assertFalse(friendshipDAO.deleteFriendship(99999), "Should return false for non-existent friendship");
    }

    @Test
    @Order(36)
    @DisplayName("Test comprehensive friendship workflow")
    void testComprehensiveFriendshipWorkflow_Success() throws SQLException {
        // Test complete workflow: send request, check status, accept, verify, remove
        
        // 1. Initial state - no friendship
        assertFalse(friendshipDAO.friendshipExists(TEST_REQUESTER_ID, TEST_RECEIVER_ID));
        assertFalse(friendshipDAO.areFriends(TEST_REQUESTER_ID, TEST_RECEIVER_ID));
        assertNull(friendshipDAO.getFriendshipStatus(TEST_REQUESTER_ID, TEST_RECEIVER_ID));

        // 2. Send friend request
        Friendship sentRequest = friendshipDAO.sendFriendRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        assertNotNull(sentRequest, "Friend request should be sent");
        assertEquals(Friendship.STATUS_PENDING, sentRequest.getStatus());

        // 3. Verify request state
        assertTrue(friendshipDAO.friendshipExists(TEST_REQUESTER_ID, TEST_RECEIVER_ID));
        assertFalse(friendshipDAO.areFriends(TEST_REQUESTER_ID, TEST_RECEIVER_ID)); // Not friends until accepted
        assertTrue(friendshipDAO.hasPendingRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID));
        assertEquals(Friendship.STATUS_PENDING, friendshipDAO.getFriendshipStatus(TEST_REQUESTER_ID, TEST_RECEIVER_ID));

        // 4. Check counts
        assertEquals(1, friendshipDAO.getPendingRequestCount(TEST_RECEIVER_ID));
        assertEquals(0, friendshipDAO.getFriendCount(TEST_REQUESTER_ID));
        assertEquals(0, friendshipDAO.getFriendCount(TEST_RECEIVER_ID));

        // 5. Accept friendship
        boolean accepted = friendshipDAO.acceptFriendRequest(sentRequest.getFriendshipId());
        assertTrue(accepted, "Friendship should be accepted");

        // 6. Verify accepted state
        assertTrue(friendshipDAO.areFriends(TEST_REQUESTER_ID, TEST_RECEIVER_ID));
        assertFalse(friendshipDAO.hasPendingRequest(TEST_REQUESTER_ID, TEST_RECEIVER_ID));
        assertEquals(Friendship.STATUS_ACCEPTED, friendshipDAO.getFriendshipStatus(TEST_REQUESTER_ID, TEST_RECEIVER_ID));

        // 7. Check updated counts
        assertEquals(0, friendshipDAO.getPendingRequestCount(TEST_RECEIVER_ID));
        assertEquals(1, friendshipDAO.getFriendCount(TEST_REQUESTER_ID));
        assertEquals(1, friendshipDAO.getFriendCount(TEST_RECEIVER_ID));

        // 8. Verify friendship appears in lists
        List<Friendship> requesterFriends = friendshipDAO.getFriends(TEST_REQUESTER_ID);
        List<Friendship> receiverFriends = friendshipDAO.getFriends(TEST_RECEIVER_ID);
        assertEquals(1, requesterFriends.size());
        assertEquals(1, receiverFriends.size());
        assertEquals(sentRequest.getFriendshipId(), requesterFriends.get(0).getFriendshipId());
        assertEquals(sentRequest.getFriendshipId(), receiverFriends.get(0).getFriendshipId());

        // 9. Get friend IDs
        List<Integer> requesterFriendIds = friendshipDAO.getFriendIds(TEST_REQUESTER_ID);
        List<Integer> receiverFriendIds = friendshipDAO.getFriendIds(TEST_RECEIVER_ID);
        assertEquals(1, requesterFriendIds.size());
        assertEquals(1, receiverFriendIds.size());
        assertEquals(TEST_RECEIVER_ID, requesterFriendIds.get(0).intValue());
        assertEquals(TEST_REQUESTER_ID, receiverFriendIds.get(0).intValue());

        // 10. Remove friendship
        boolean removed = friendshipDAO.removeFriendship(TEST_REQUESTER_ID, TEST_RECEIVER_ID);
        assertTrue(removed, "Friendship should be removed");

        // 11. Verify removal
        assertFalse(friendshipDAO.friendshipExists(TEST_REQUESTER_ID, TEST_RECEIVER_ID));
        assertFalse(friendshipDAO.areFriends(TEST_REQUESTER_ID, TEST_RECEIVER_ID));
        assertNull(friendshipDAO.getFriendshipStatus(TEST_REQUESTER_ID, TEST_RECEIVER_ID));
        assertEquals(0, friendshipDAO.getFriendCount(TEST_REQUESTER_ID));
        assertEquals(0, friendshipDAO.getFriendCount(TEST_RECEIVER_ID));
    }
} 