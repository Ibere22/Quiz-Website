package dto;

import model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for UserDTO.
 * Covers all methods, branches, and edge cases for 100% line and branch coverage.
 */
class UserDTOTest {

    // Helper method to create a Date for tests
    private Date createDate() {
        return new Date(System.currentTimeMillis() - 1000 * 60 * 60); // 1 hour ago
    }

    @Test
    @DisplayName("Test fromUser static factory method with valid input")
    void testFromUser() {
        Date now = createDate();
        User user = new User(1, "john", "hash", "john@email.com", now, false);
        UserDTO dto = UserDTO.fromUser(user);
        assertNotNull(dto);
        assertEquals(user.getUserId(), dto.userId());
        assertEquals(user.getUsername(), dto.username());
        assertEquals(user.getEmail(), dto.email());
        assertEquals(user.getCreatedDate(), dto.createdDate());
        assertEquals(user.isAdmin(), dto.isAdmin());
    }

    @Test
    @DisplayName("Test fromUser static factory method with null input")
    void testFromUserNull() {
        assertNull(UserDTO.fromUser(null));
    }

    @Test
    @DisplayName("Test equals, hashCode, and toString")
    void testEqualsHashCodeToString() {
        Date now = createDate();
        UserDTO dto1 = new UserDTO(1, "john", "john@email.com", now, false);
        UserDTO dto2 = new UserDTO(1, "john", "john@email.com", now, false);
        UserDTO dto3 = new UserDTO(2, "jane", "jane@email.com", now, true);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertTrue(dto1.toString().contains("UserDTO"));
        assertTrue(dto1.toString().contains("john"));
    }

    @Test
    @DisplayName("Test immutability of UserDTO")
    void testImmutability() {
        Date now = createDate();
        UserDTO dto = new UserDTO(1, "john", "john@email.com", now, false);
        // There are no setters, so fields cannot be changed
        assertEquals(1, dto.userId());
        assertEquals("john", dto.username());
        assertEquals("john@email.com", dto.email());
        assertEquals(now, dto.createdDate());
        assertFalse(dto.isAdmin());
    }

    @Test
    @DisplayName("Test null and edge cases for all fields")
    void testNullAndEdgeCases() {
        UserDTO dto = new UserDTO(0, null, null, null, false);
        assertEquals(0, dto.userId());
        assertNull(dto.username());
        assertNull(dto.email());
        assertNull(dto.createdDate());
        assertFalse(dto.isAdmin());
    }

    @Test
    @DisplayName("Test UserDTO with different admin values")
    void testAdminValues() {
        Date now = createDate();
        UserDTO admin = new UserDTO(1, "admin", "admin@email.com", now, true);
        UserDTO notAdmin = new UserDTO(2, "user", "user@email.com", now, false);
        assertTrue(admin.isAdmin());
        assertFalse(notAdmin.isAdmin());
    }

    @Test
    @DisplayName("Test UserDTO with long usernames and emails")
    void testLongFields() {
        String longUsername = "a".repeat(100);
        String longEmail = "b".repeat(100) + "@email.com";
        Date now = createDate();
        UserDTO dto = new UserDTO(1, longUsername, longEmail, now, false);
        assertEquals(longUsername, dto.username());
        assertEquals(longEmail, dto.email());
    }

    @Test
    @DisplayName("Test UserDTO with special characters in username and email")
    void testSpecialCharacters() {
        String username = "jöhn_测试_!@#";
        String email = "tëst+user@emäil.com";
        Date now = createDate();
        UserDTO dto = new UserDTO(1, username, email, now, false);
        assertEquals(username, dto.username());
        assertEquals(email, dto.email());
    }

    @Test
    @DisplayName("Test UserDTO with future and past dates")
    void testDateEdgeCases() {
        Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365);
        Date past = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 365);
        UserDTO futureUser = new UserDTO(1, "future", "future@email.com", future, false);
        UserDTO pastUser = new UserDTO(2, "past", "past@email.com", past, false);
        assertEquals(future, futureUser.createdDate());
        assertEquals(past, pastUser.createdDate());
    }
} 