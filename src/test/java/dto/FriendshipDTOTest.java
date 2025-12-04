package dto;

import model.Friendship;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for FriendshipDTO.
 * Covers all methods, branches, and edge cases for 100% line and branch coverage.
 */
class FriendshipDTOTest {

    // Helper method to create a UserDTO for tests
    private UserDTO createUser(int id, String name) {
        return new UserDTO(id, name, name + "@email.com", new Date(), false);
    }

    // Helper method to create a Date for tests
    private Date createDate() {
        return new Date(System.currentTimeMillis() - 1000 * 60 * 60); // 1 hour ago
    }

    @Test
    @DisplayName("Test fromFriendship static factory method with valid input")
    void testFromFriendship() {
        Date now = createDate();
        Friendship friendship = new Friendship(10, 1, 2, Friendship.STATUS_ACCEPTED, now, now);
        UserDTO requester = createUser(1, "john");
        UserDTO receiver = createUser(2, "jane");
        FriendshipDTO dto = FriendshipDTO.fromFriendship(friendship, requester, receiver);
        assertNotNull(dto);
        assertEquals(friendship.getFriendshipId(), dto.friendshipId());
        assertEquals(requester, dto.requester());
        assertEquals(receiver, dto.receiver());
        assertEquals(friendship.getStatus(), dto.status());
        assertEquals(friendship.getDateRequested(), dto.dateRequested());
        assertEquals(friendship.getDateAccepted(), dto.dateAccepted());
    }

    @Test
    @DisplayName("Test fromFriendship static factory method with null input")
    void testFromFriendshipNull() {
        assertNull(FriendshipDTO.fromFriendship(null, null, null));
    }

    @Test
    @DisplayName("Test isPending, isAccepted, isDeclined, isBlocked")
    void testStatusCheckers() {
        UserDTO requester = createUser(1, "john");
        UserDTO receiver = createUser(2, "jane");
        FriendshipDTO pending = new FriendshipDTO(1, requester, receiver, FriendshipDTO.STATUS_PENDING, new Date(), null);
        FriendshipDTO accepted = new FriendshipDTO(2, requester, receiver, FriendshipDTO.STATUS_ACCEPTED, new Date(), new Date());
        FriendshipDTO declined = new FriendshipDTO(3, requester, receiver, FriendshipDTO.STATUS_DECLINED, new Date(), null);
        FriendshipDTO blocked = new FriendshipDTO(4, requester, receiver, FriendshipDTO.STATUS_BLOCKED, new Date(), null);
        FriendshipDTO unknown = new FriendshipDTO(5, requester, receiver, "unknown", new Date(), null);
        assertTrue(pending.isPending());
        assertTrue(accepted.isAccepted());
        assertTrue(declined.isDeclined());
        assertTrue(blocked.isBlocked());
        assertFalse(unknown.isPending());
        assertFalse(unknown.isAccepted());
        assertFalse(unknown.isDeclined());
        assertFalse(unknown.isBlocked());
    }

    @Test
    @DisplayName("Test getOtherUser, isRequester, isReceiver")
    void testRelationshipHelpers() {
        UserDTO requester = createUser(1, "john");
        UserDTO receiver = createUser(2, "jane");
        FriendshipDTO dto = new FriendshipDTO(10, requester, receiver, FriendshipDTO.STATUS_PENDING, new Date(), null);
        assertEquals(receiver, dto.getOtherUser(1));
        assertEquals(requester, dto.getOtherUser(2));
        assertNull(dto.getOtherUser(3));
        assertTrue(dto.isRequester(1));
        assertTrue(dto.isReceiver(2));
        assertFalse(dto.isRequester(2));
        assertFalse(dto.isReceiver(1));
    }

    @Test
    @DisplayName("Test equals, hashCode, and toString")
    void testEqualsHashCodeToString() {
        UserDTO requester = createUser(1, "john");
        UserDTO receiver = createUser(2, "jane");
        Date now = createDate();
        FriendshipDTO dto1 = new FriendshipDTO(10, requester, receiver, FriendshipDTO.STATUS_PENDING, now, null);
        FriendshipDTO dto2 = new FriendshipDTO(10, requester, receiver, FriendshipDTO.STATUS_PENDING, now, null);
        FriendshipDTO dto3 = new FriendshipDTO(11, requester, receiver, FriendshipDTO.STATUS_PENDING, now, null);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertTrue(dto1.toString().contains("FriendshipDTO"));
        assertTrue(dto1.toString().contains("john"));
    }

    @Test
    @DisplayName("Test immutability of FriendshipDTO")
    void testImmutability() {
        UserDTO requester = createUser(1, "john");
        UserDTO receiver = createUser(2, "jane");
        Date now = createDate();
        FriendshipDTO dto = new FriendshipDTO(10, requester, receiver, FriendshipDTO.STATUS_PENDING, now, null);
        // There are no setters, so fields cannot be changed
        assertEquals(10, dto.friendshipId());
        assertEquals(requester, dto.requester());
        assertEquals(receiver, dto.receiver());
        assertEquals(FriendshipDTO.STATUS_PENDING, dto.status());
        assertEquals(now, dto.dateRequested());
        assertNull(dto.dateAccepted());
    }

    @Test
    @DisplayName("Test null and edge cases for all fields")
    void testNullAndEdgeCases() {
        FriendshipDTO dto = new FriendshipDTO(0, null, null, null, null, null);
        assertEquals(0, dto.friendshipId());
        assertNull(dto.requester());
        assertNull(dto.receiver());
        assertNull(dto.status());
        assertNull(dto.dateRequested());
        assertNull(dto.dateAccepted());
    }

    @Test
    @DisplayName("Test FriendshipDTO with long and special character fields")
    void testLongAndSpecialFields() {
        String longStatus = "S".repeat(100);
        UserDTO requester = createUser(1, "john");
        UserDTO receiver = createUser(2, "jane");
        Date now = createDate();
        FriendshipDTO longDto = new FriendshipDTO(1, requester, receiver, longStatus, now, now);
        FriendshipDTO specialDto = new FriendshipDTO(2, requester, receiver, "测试_!@#", now, now);
        assertEquals(longStatus, longDto.status());
        assertEquals("测试_!@#", specialDto.status());
    }

    @Test
    @DisplayName("Test FriendshipDTO with future and past dates")
    void testDateEdgeCases() {
        Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365);
        Date past = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 365);
        UserDTO requester = createUser(1, "john");
        UserDTO receiver = createUser(2, "jane");
        FriendshipDTO futureDto = new FriendshipDTO(1, requester, receiver, FriendshipDTO.STATUS_PENDING, future, null);
        FriendshipDTO pastDto = new FriendshipDTO(2, requester, receiver, FriendshipDTO.STATUS_PENDING, past, null);
        assertEquals(future, futureDto.dateRequested());
        assertEquals(past, pastDto.dateRequested());
    }
} 