package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the Friendship model class.
 * Tests all constructors, getters/setters, status constants, helper methods,
 * friendship management, equality, toString, edge cases, and real-world friendship scenarios.
 */
@DisplayName("Friendship Model Tests")
public class FriendshipTest {

    private Friendship friendship;
    private Date testDate;

    @BeforeEach
    void setUp() {
        friendship = new Friendship();
        testDate = new Date();
    }

    @Nested
    @DisplayName("Friendship Status Constants Tests")
    class FriendshipStatusConstantsTests {

        @Test
        @DisplayName("Friendship status constants should have correct values")
        void testFriendshipStatusConstants() {
            assertEquals("pending", Friendship.STATUS_PENDING);
            assertEquals("accepted", Friendship.STATUS_ACCEPTED);
            assertEquals("declined", Friendship.STATUS_DECLINED);
            assertEquals("blocked", Friendship.STATUS_BLOCKED);
        }

        @Test
        @DisplayName("Friendship status constants should be static and accessible")
        void testFriendshipStatusConstantsAccessibility() {
            // Should be able to access without instance
            assertNotNull(Friendship.STATUS_PENDING);
            assertNotNull(Friendship.STATUS_ACCEPTED);
            assertNotNull(Friendship.STATUS_DECLINED);
            assertNotNull(Friendship.STATUS_BLOCKED);
        }

        @Test
        @DisplayName("All friendship status constants should be unique")
        void testFriendshipStatusConstantsUniqueness() {
            String[] statuses = {
                Friendship.STATUS_PENDING,
                Friendship.STATUS_ACCEPTED,
                Friendship.STATUS_DECLINED,
                Friendship.STATUS_BLOCKED
            };
            
            // Check that all statuses are different
            for (int i = 0; i < statuses.length; i++) {
                for (int j = i + 1; j < statuses.length; j++) {
                    assertNotEquals(statuses[i], statuses[j], 
                        "Friendship statuses should be unique: " + statuses[i] + " vs " + statuses[j]);
                }
            }
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should initialize with default values")
        void testDefaultConstructor() {
            Friendship fs = new Friendship();
            
            assertEquals(0, fs.getFriendshipId());
            assertEquals(0, fs.getRequesterId());
            assertEquals(0, fs.getReceiverId());
            assertEquals(Friendship.STATUS_PENDING, fs.getStatus());
            assertNotNull(fs.getDateRequested());
            assertNull(fs.getDateAccepted());
            
            // Verify date is recent (within last second)
            long timeDiff = new Date().getTime() - fs.getDateRequested().getTime();
            assertTrue(timeDiff < 1000);
        }

        @Test
        @DisplayName("Friend request constructor should set requester and receiver")
        void testFriendRequestConstructor() {
            Friendship fs = new Friendship(123, 456);
            
            assertEquals(0, fs.getFriendshipId()); // Should be 0 (not set)
            assertEquals(123, fs.getRequesterId());
            assertEquals(456, fs.getReceiverId());
            assertEquals(Friendship.STATUS_PENDING, fs.getStatus());
            assertNotNull(fs.getDateRequested());
            assertNull(fs.getDateAccepted());
            
            // Verify date is recent
            long timeDiff = new Date().getTime() - fs.getDateRequested().getTime();
            assertTrue(timeDiff < 1000);
        }

        @Test
        @DisplayName("Full constructor should set all fields correctly")
        void testFullConstructor() {
            Date requestDate = new Date(System.currentTimeMillis() - 86400000); // Yesterday
            Date acceptDate = new Date(); // Today
            
            Friendship fs = new Friendship(789, 123, 456, Friendship.STATUS_ACCEPTED, 
                                         requestDate, acceptDate);
            
            assertEquals(789, fs.getFriendshipId());
            assertEquals(123, fs.getRequesterId());
            assertEquals(456, fs.getReceiverId());
            assertEquals(Friendship.STATUS_ACCEPTED, fs.getStatus());
            assertEquals(requestDate, fs.getDateRequested());
            assertEquals(acceptDate, fs.getDateAccepted());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("FriendshipId getter and setter should work correctly")
        void testFriendshipIdGetterSetter() {
            friendship.setFriendshipId(999);
            assertEquals(999, friendship.getFriendshipId());
        }

        @Test
        @DisplayName("RequesterId getter and setter should work correctly")
        void testRequesterIdGetterSetter() {
            friendship.setRequesterId(123);
            assertEquals(123, friendship.getRequesterId());
        }

        @Test
        @DisplayName("ReceiverId getter and setter should work correctly")
        void testReceiverIdGetterSetter() {
            friendship.setReceiverId(456);
            assertEquals(456, friendship.getReceiverId());
        }

        @Test
        @DisplayName("Status getter and setter should work correctly")
        void testStatusGetterSetter() {
            friendship.setStatus(Friendship.STATUS_ACCEPTED);
            assertEquals(Friendship.STATUS_ACCEPTED, friendship.getStatus());
        }

        @Test
        @DisplayName("DateRequested getter and setter should work correctly")
        void testDateRequestedGetterSetter() {
            friendship.setDateRequested(testDate);
            assertEquals(testDate, friendship.getDateRequested());
        }

        @Test
        @DisplayName("DateAccepted getter and setter should work correctly")
        void testDateAcceptedGetterSetter() {
            friendship.setDateAccepted(testDate);
            assertEquals(testDate, friendship.getDateAccepted());
        }

        @Test
        @DisplayName("String fields can be set to null")
        void testNullStringValues() {
            friendship.setStatus(null);
            assertNull(friendship.getStatus());
        }

        @Test
        @DisplayName("Date fields can be set to null")
        void testNullDateValues() {
            friendship.setDateRequested(null);
            friendship.setDateAccepted(null);
            
            assertNull(friendship.getDateRequested());
            assertNull(friendship.getDateAccepted());
        }
    }

    @Nested
    @DisplayName("Status Helper Method Tests")
    class StatusHelperMethodTests {

        @Test
        @DisplayName("isPending should return true for pending status")
        void testIsPending() {
            friendship.setStatus(Friendship.STATUS_PENDING);
            assertTrue(friendship.isPending());
            
            friendship.setStatus(Friendship.STATUS_ACCEPTED);
            assertFalse(friendship.isPending());
            
            friendship.setStatus(Friendship.STATUS_DECLINED);
            assertFalse(friendship.isPending());
            
            friendship.setStatus(Friendship.STATUS_BLOCKED);
            assertFalse(friendship.isPending());
        }

        @Test
        @DisplayName("isAccepted should return true for accepted status")
        void testIsAccepted() {
            friendship.setStatus(Friendship.STATUS_ACCEPTED);
            assertTrue(friendship.isAccepted());
            
            friendship.setStatus(Friendship.STATUS_PENDING);
            assertFalse(friendship.isAccepted());
            
            friendship.setStatus(Friendship.STATUS_DECLINED);
            assertFalse(friendship.isAccepted());
            
            friendship.setStatus(Friendship.STATUS_BLOCKED);
            assertFalse(friendship.isAccepted());
        }

        @Test
        @DisplayName("isDeclined should return true for declined status")
        void testIsDeclined() {
            friendship.setStatus(Friendship.STATUS_DECLINED);
            assertTrue(friendship.isDeclined());
            
            friendship.setStatus(Friendship.STATUS_PENDING);
            assertFalse(friendship.isDeclined());
            
            friendship.setStatus(Friendship.STATUS_ACCEPTED);
            assertFalse(friendship.isDeclined());
            
            friendship.setStatus(Friendship.STATUS_BLOCKED);
            assertFalse(friendship.isDeclined());
        }

        @Test
        @DisplayName("isBlocked should return true for blocked status")
        void testIsBlocked() {
            friendship.setStatus(Friendship.STATUS_BLOCKED);
            assertTrue(friendship.isBlocked());
            
            friendship.setStatus(Friendship.STATUS_PENDING);
            assertFalse(friendship.isBlocked());
            
            friendship.setStatus(Friendship.STATUS_ACCEPTED);
            assertFalse(friendship.isBlocked());
            
            friendship.setStatus(Friendship.STATUS_DECLINED);
            assertFalse(friendship.isBlocked());
        }

        @Test
        @DisplayName("Status helper methods should handle null status")
        void testStatusHelperMethodsWithNullStatus() {
            friendship.setStatus(null);
            
            assertFalse(friendship.isPending());
            assertFalse(friendship.isAccepted());
            assertFalse(friendship.isDeclined());
            assertFalse(friendship.isBlocked());
        }

        @Test
        @DisplayName("Status helper methods should handle custom status")
        void testStatusHelperMethodsWithCustomStatus() {
            friendship.setStatus("custom_status");
            
            assertFalse(friendship.isPending());
            assertFalse(friendship.isAccepted());
            assertFalse(friendship.isDeclined());
            assertFalse(friendship.isBlocked());
        }

        @Test
        @DisplayName("Status helper methods should be case sensitive")
        void testStatusHelperMethodsCaseSensitivity() {
            friendship.setStatus("PENDING");
            assertFalse(friendship.isPending()); // Should be case sensitive
            
            friendship.setStatus("Accepted");
            assertFalse(friendship.isAccepted()); // Should be case sensitive
        }
    }

    @Nested
    @DisplayName("Friendship Action Method Tests")
    class FriendshipActionMethodTests {

        @Test
        @DisplayName("accept should set status to accepted and set dateAccepted")
        void testAccept() {
            friendship.setStatus(Friendship.STATUS_PENDING);
            friendship.setDateAccepted(null);
            
            Date beforeAccept = new Date();
            friendship.accept();
            Date afterAccept = new Date();
            
            assertEquals(Friendship.STATUS_ACCEPTED, friendship.getStatus());
            assertNotNull(friendship.getDateAccepted());
            assertTrue(friendship.isAccepted());
            
            // Verify dateAccepted is recent
            assertTrue(friendship.getDateAccepted().compareTo(beforeAccept) >= 0);
            assertTrue(friendship.getDateAccepted().compareTo(afterAccept) <= 0);
        }

        @Test
        @DisplayName("decline should set status to declined")
        void testDecline() {
            friendship.setStatus(Friendship.STATUS_PENDING);
            Date originalDateAccepted = friendship.getDateAccepted();
            
            friendship.decline();
            
            assertEquals(Friendship.STATUS_DECLINED, friendship.getStatus());
            assertTrue(friendship.isDeclined());
            // dateAccepted should not be modified by decline
            assertEquals(originalDateAccepted, friendship.getDateAccepted());
        }

        @Test
        @DisplayName("block should set status to blocked")
        void testBlock() {
            friendship.setStatus(Friendship.STATUS_PENDING);
            Date originalDateAccepted = friendship.getDateAccepted();
            
            friendship.block();
            
            assertEquals(Friendship.STATUS_BLOCKED, friendship.getStatus());
            assertTrue(friendship.isBlocked());
            // dateAccepted should not be modified by block
            assertEquals(originalDateAccepted, friendship.getDateAccepted());
        }

        @Test
        @DisplayName("Accept should work from any status")
        void testAcceptFromAnyStatus() {
            String[] statuses = {
                Friendship.STATUS_PENDING,
                Friendship.STATUS_DECLINED,
                Friendship.STATUS_BLOCKED,
                "custom_status"
            };
            
            for (String status : statuses) {
                friendship.setStatus(status);
                friendship.setDateAccepted(null);
                
                friendship.accept();
                
                assertEquals(Friendship.STATUS_ACCEPTED, friendship.getStatus());
                assertNotNull(friendship.getDateAccepted());
                assertTrue(friendship.isAccepted());
            }
        }
    }

    @Nested
    @DisplayName("Friendship Relationship Helper Tests")
    class FriendshipRelationshipHelperTests {

        @Test
        @DisplayName("getFriendId should return correct friend ID for requester")
        void testGetFriendIdForRequester() {
            friendship.setRequesterId(123);
            friendship.setReceiverId(456);
            
            // If current user is the requester, friend is the receiver
            assertEquals(456, friendship.getFriendId(123));
        }

        @Test
        @DisplayName("getFriendId should return correct friend ID for receiver")
        void testGetFriendIdForReceiver() {
            friendship.setRequesterId(123);
            friendship.setReceiverId(456);
            
            // If current user is the receiver, friend is the requester
            assertEquals(123, friendship.getFriendId(456));
        }


        @Test
        @DisplayName("involves should return true when user is requester")
        void testInvolvesRequester() {
            friendship.setRequesterId(123);
            friendship.setReceiverId(456);
            
            assertTrue(friendship.involves(123));
        }

        @Test
        @DisplayName("involves should return true when user is receiver")
        void testInvolvesReceiver() {
            friendship.setRequesterId(123);
            friendship.setReceiverId(456);
            
            assertTrue(friendship.involves(456));
        }

        @Test
        @DisplayName("involves should return false when user is not involved")
        void testInvolvesNonInvolvedUser() {
            friendship.setRequesterId(123);
            friendship.setReceiverId(456);
            
            assertFalse(friendship.involves(789));
            assertFalse(friendship.involves(0));
            assertFalse(friendship.involves(-1));
        }

        @Test
        @DisplayName("Relationship helper methods should work with zero IDs")
        void testRelationshipHelpersWithZeroIds() {
            friendship.setRequesterId(0);
            friendship.setReceiverId(456);
            
            assertTrue(friendship.involves(0));
            assertTrue(friendship.involves(456));
            assertEquals(456, friendship.getFriendId(0));
            assertEquals(0, friendship.getFriendId(456));
        }
    }

    @Nested
    @DisplayName("Equality and HashCode Tests")
    class EqualityTests {

        @Test
        @DisplayName("Friendships with same friendshipId should be equal")
        void testEqualFriendshipsWithSameFriendshipId() {
            Friendship fs1 = new Friendship();
            fs1.setFriendshipId(100);
            Friendship fs2 = new Friendship();
            fs2.setFriendshipId(100);
            
            assertEquals(fs1, fs2);
            assertEquals(fs1.hashCode(), fs2.hashCode());
        }

        @Test
        @DisplayName("Friendships with different friendshipId should not be equal")
        void testUnequalFriendshipsWithDifferentFriendshipId() {
            Friendship fs1 = new Friendship();
            fs1.setFriendshipId(100);
            Friendship fs2 = new Friendship();
            fs2.setFriendshipId(200);
            
            assertNotEquals(fs1, fs2);
        }

        @Test
        @DisplayName("Friendship should be equal to itself")
        void testFriendshipEqualToItself() {
            assertEquals(friendship, friendship);
        }

        @Test
        @DisplayName("Friendship should not be equal to null")
        void testFriendshipNotEqualToNull() {
            assertNotEquals(friendship, null);
        }

        @Test
        @DisplayName("Friendship should not be equal to different class object")
        void testFriendshipNotEqualToDifferentClass() {
            String differentObject = "Not a Friendship";
            assertNotEquals(friendship, differentObject);
        }

        @Test
        @DisplayName("HashCode should be consistent")
        void testHashCodeConsistency() {
            friendship.setFriendshipId(123);
            int hashCode1 = friendship.hashCode();
            int hashCode2 = friendship.hashCode();
            assertEquals(hashCode1, hashCode2);
        }

        @Test
        @DisplayName("Default friendships (friendshipId=0) should be equal")
        void testDefaultFriendshipsEqual() {
            Friendship fs1 = new Friendship();
            Friendship fs2 = new Friendship();
            assertEquals(fs1, fs2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("ToString should contain all fields")
        void testToStringContent() {
            friendship.setFriendshipId(789);
            friendship.setRequesterId(123);
            friendship.setReceiverId(456);
            friendship.setStatus(Friendship.STATUS_ACCEPTED);
            friendship.setDateRequested(testDate);
            friendship.setDateAccepted(testDate);
            
            String result = friendship.toString();
            
            assertTrue(result.contains("789")); // friendshipId
            assertTrue(result.contains("123")); // requesterId
            assertTrue(result.contains("456")); // receiverId
            assertTrue(result.contains("accepted")); // status
            assertTrue(result.contains("Friendship"));
        }

        @Test
        @DisplayName("ToString should handle null values gracefully")
        void testToStringWithNullValues() {
            friendship.setFriendshipId(100);
            friendship.setRequesterId(200);
            friendship.setReceiverId(300);
            friendship.setStatus(null); // Null status
            friendship.setDateRequested(null); // Null request date
            friendship.setDateAccepted(null); // Null accept date
            
            String result = friendship.toString();
            
            assertNotNull(result);
            assertTrue(result.contains("100"));
            assertTrue(result.contains("200"));
            assertTrue(result.contains("300"));
            assertTrue(result.contains("null")); // null values
        }

        @Test
        @DisplayName("ToString should not be null or empty")
        void testToStringNotNullOrEmpty() {
            String result = friendship.toString();
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.length() > 0);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Data Integrity Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Friendship should handle negative friendshipId and userIds")
        void testNegativeIds() {
            friendship.setFriendshipId(-1);
            friendship.setRequesterId(-100);
            friendship.setReceiverId(-200);
            
            assertEquals(-1, friendship.getFriendshipId());
            assertEquals(-100, friendship.getRequesterId());
            assertEquals(-200, friendship.getReceiverId());
        }

        @Test
        @DisplayName("Friendship should handle very large IDs")
        void testLargeIds() {
            friendship.setFriendshipId(Integer.MAX_VALUE);
            friendship.setRequesterId(Integer.MAX_VALUE - 1);
            friendship.setReceiverId(Integer.MAX_VALUE - 2);
            
            assertEquals(Integer.MAX_VALUE, friendship.getFriendshipId());
            assertEquals(Integer.MAX_VALUE - 1, friendship.getRequesterId());
            assertEquals(Integer.MAX_VALUE - 2, friendship.getReceiverId());
        }

        @Test
        @DisplayName("Friendship should handle empty status string")
        void testEmptyStatus() {
            friendship.setStatus("");
            
            assertEquals("", friendship.getStatus());
            assertFalse(friendship.isPending());
            assertFalse(friendship.isAccepted());
            assertFalse(friendship.isDeclined());
            assertFalse(friendship.isBlocked());
        }

        @Test
        @DisplayName("Friendship should handle very long status string")
        void testLongStatus() {
            String longStatus = "very_long_custom_status_".repeat(100);
            friendship.setStatus(longStatus);
            
            assertEquals(longStatus, friendship.getStatus());
        }

        @Test
        @DisplayName("Date fields should handle historical dates")
        void testHistoricalDates() {
            Date historicalDate = new Date(0); // January 1, 1970
            friendship.setDateRequested(historicalDate);
            friendship.setDateAccepted(historicalDate);
            
            assertEquals(historicalDate, friendship.getDateRequested());
            assertEquals(historicalDate, friendship.getDateAccepted());
        }

        @Test
        @DisplayName("Date fields should handle future dates")
        void testFutureDates() {
            Date futureDate = new Date(System.currentTimeMillis() + 86400000); // Tomorrow
            friendship.setDateRequested(futureDate);
            friendship.setDateAccepted(futureDate);
            
            assertEquals(futureDate, friendship.getDateRequested());
            assertEquals(futureDate, friendship.getDateAccepted());
        }

        @Test
        @DisplayName("Self-friendship should work (same requester and receiver)")
        void testSelfFriendship() {
            friendship.setRequesterId(123);
            friendship.setReceiverId(123);
            
            assertTrue(friendship.involves(123));
            assertEquals(123, friendship.getFriendId(123)); // Returns receiverId since userId == requesterId
        }
    }

    @Nested
    @DisplayName("Real-world Scenario Tests")
    class ScenarioTests {

        @Test
        @DisplayName("Friend request scenario")
        void testFriendRequestScenario() {
            // User 123 sends friend request to user 456
            Friendship friendRequest = new Friendship(123, 456);
            
            // Verify initial state
            assertEquals(123, friendRequest.getRequesterId());
            assertEquals(456, friendRequest.getReceiverId());
            assertEquals(Friendship.STATUS_PENDING, friendRequest.getStatus());
            assertTrue(friendRequest.isPending());
            assertNotNull(friendRequest.getDateRequested());
            assertNull(friendRequest.getDateAccepted());
            
            // Verify relationships
            assertTrue(friendRequest.involves(123));
            assertTrue(friendRequest.involves(456));
            assertFalse(friendRequest.involves(789));
            assertEquals(456, friendRequest.getFriendId(123)); // 123's friend is 456
            assertEquals(123, friendRequest.getFriendId(456)); // 456's friend is 123
        }

        @Test
        @DisplayName("Friend request acceptance scenario")
        void testFriendRequestAcceptanceScenario() {
            // Initial friend request
            Friendship friendRequest = new Friendship(123, 456);
            Date requestDate = friendRequest.getDateRequested();
            
            // Verify pending state
            assertTrue(friendRequest.isPending());
            assertNull(friendRequest.getDateAccepted());
            
            // Accept the request
            friendRequest.accept();
            
            // Verify accepted state
            assertEquals(Friendship.STATUS_ACCEPTED, friendRequest.getStatus());
            assertTrue(friendRequest.isAccepted());
            assertFalse(friendRequest.isPending());
            assertNotNull(friendRequest.getDateAccepted());
            assertEquals(requestDate, friendRequest.getDateRequested()); // Should not change
            
            // Verify acceptance date is recent
            long timeDiff = new Date().getTime() - friendRequest.getDateAccepted().getTime();
            assertTrue(timeDiff < 1000);
        }

        @Test
        @DisplayName("Friend request decline scenario")
        void testFriendRequestDeclineScenario() {
            // Initial friend request
            Friendship friendRequest = new Friendship(123, 456);
            Date requestDate = friendRequest.getDateRequested();
            
            // Decline the request
            friendRequest.decline();
            
            // Verify declined state
            assertEquals(Friendship.STATUS_DECLINED, friendRequest.getStatus());
            assertTrue(friendRequest.isDeclined());
            assertFalse(friendRequest.isPending());
            assertFalse(friendRequest.isAccepted());
            assertNull(friendRequest.getDateAccepted()); // Should remain null
            assertEquals(requestDate, friendRequest.getDateRequested()); // Should not change
        }

        @Test
        @DisplayName("User blocking scenario")
        void testUserBlockingScenario() {
            // User 123 blocks user 456
            Friendship blocking = new Friendship(123, 456);
            blocking.block();
            
            // Verify blocked state
            assertEquals(Friendship.STATUS_BLOCKED, blocking.getStatus());
            assertTrue(blocking.isBlocked());
            assertFalse(blocking.isPending());
            assertFalse(blocking.isAccepted());
            assertFalse(blocking.isDeclined());
            assertNull(blocking.getDateAccepted());
        }

        @Test
        @DisplayName("Friendship status transition scenario")
        void testFriendshipStatusTransitionScenario() {
            Friendship fs = new Friendship(123, 456);
            
            // Initial state: pending
            assertTrue(fs.isPending());
            assertFalse(fs.isAccepted());
            assertFalse(fs.isDeclined());
            assertFalse(fs.isBlocked());
            
            // Decline
            fs.decline();
            assertFalse(fs.isPending());
            assertFalse(fs.isAccepted());
            assertTrue(fs.isDeclined());
            assertFalse(fs.isBlocked());
            
            // Accept after decline
            fs.accept();
            assertFalse(fs.isPending());
            assertTrue(fs.isAccepted());
            assertFalse(fs.isDeclined());
            assertFalse(fs.isBlocked());
            assertNotNull(fs.getDateAccepted());
            
            // Block after accept
            fs.block();
            assertFalse(fs.isPending());
            assertFalse(fs.isAccepted());
            assertFalse(fs.isDeclined());
            assertTrue(fs.isBlocked());
        }

        @Test
        @DisplayName("Mutual friendship scenario")
        void testMutualFriendshipScenario() {
            // User 123 and 456 are friends
            Friendship friendship1 = new Friendship(123, 456);
            friendship1.accept();
            
            // Both users should see each other as friends
            assertTrue(friendship1.involves(123));
            assertTrue(friendship1.involves(456));
            assertEquals(456, friendship1.getFriendId(123));
            assertEquals(123, friendship1.getFriendId(456));
            assertTrue(friendship1.isAccepted());
        }

        @Test
        @DisplayName("Friendship management system scenario")
        void testFriendshipManagementSystemScenario() {
            // Create multiple friendships for user 123
            Friendship[] friendships = {
                new Friendship(123, 456), // User 123 -> 456 (pending)
                new Friendship(789, 123), // User 789 -> 123 (pending) 
                new Friendship(111, 123), // User 111 -> 123 (will be accepted)
                new Friendship(123, 222), // User 123 -> 222 (will be declined)
                new Friendship(333, 123)  // User 333 -> 123 (will be blocked)
            };
            
            // Accept friendship from user 111
            friendships[2].accept();
            
            // Decline friendship to user 222
            friendships[3].decline();
            
            // Block user 333
            friendships[4].block();
            
            // Verify user 123 is involved in all friendships
            for (Friendship fs : friendships) {
                assertTrue(fs.involves(123), "User 123 should be involved in all friendships");
            }
            
            // Verify status states
            assertTrue(friendships[0].isPending()); // 123 -> 456
            assertTrue(friendships[1].isPending()); // 789 -> 123
            assertTrue(friendships[2].isAccepted()); // 111 -> 123
            assertTrue(friendships[3].isDeclined()); // 123 -> 222
            assertTrue(friendships[4].isBlocked()); // 333 -> 123
            
            // Verify friend relationships for user 123
            assertEquals(456, friendships[0].getFriendId(123)); // Friend: 456
            assertEquals(789, friendships[1].getFriendId(123)); // Friend: 789
            assertEquals(111, friendships[2].getFriendId(123)); // Friend: 111
            assertEquals(222, friendships[3].getFriendId(123)); // Friend: 222
            assertEquals(333, friendships[4].getFriendId(123)); // Friend: 333
            
            // Only accepted friendship should have dateAccepted
            assertNull(friendships[0].getDateAccepted());
            assertNull(friendships[1].getDateAccepted());
            assertNotNull(friendships[2].getDateAccepted()); // Accepted
            assertNull(friendships[3].getDateAccepted());
            assertNull(friendships[4].getDateAccepted());
        }

        @Test
        @DisplayName("Friendship data persistence scenario")
        void testFriendshipDataPersistenceScenario() {
            // Create friendship with specific data
            Date requestDate = new Date(System.currentTimeMillis() - 86400000); // Yesterday
            Date acceptDate = new Date(); // Today
            
            Friendship originalFs = new Friendship(100, 123, 456, Friendship.STATUS_ACCEPTED, 
                                                 requestDate, acceptDate);
            
            // Verify all data is preserved
            assertEquals(100, originalFs.getFriendshipId());
            assertEquals(123, originalFs.getRequesterId());
            assertEquals(456, originalFs.getReceiverId());
            assertEquals(Friendship.STATUS_ACCEPTED, originalFs.getStatus());
            assertEquals(requestDate, originalFs.getDateRequested());
            assertEquals(acceptDate, originalFs.getDateAccepted());
            
            // Simulate data retrieval (copy constructor pattern)
            Friendship retrievedFs = new Friendship(
                originalFs.getFriendshipId(),
                originalFs.getRequesterId(),
                originalFs.getReceiverId(),
                originalFs.getStatus(),
                originalFs.getDateRequested(),
                originalFs.getDateAccepted()
            );
            
            // Verify data integrity
            assertEquals(originalFs.getFriendshipId(), retrievedFs.getFriendshipId());
            assertEquals(originalFs.getRequesterId(), retrievedFs.getRequesterId());
            assertEquals(originalFs.getReceiverId(), retrievedFs.getReceiverId());
            assertEquals(originalFs.getStatus(), retrievedFs.getStatus());
            assertEquals(originalFs.getDateRequested(), retrievedFs.getDateRequested());
            assertEquals(originalFs.getDateAccepted(), retrievedFs.getDateAccepted());
            assertEquals(originalFs, retrievedFs); // Should be equal by friendshipId
        }
    }
} 