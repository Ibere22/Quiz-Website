package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

/**
 * Comprehensive test suite for the User model class
 * Tests all constructors, getters, setters, equals/hashCode, and toString methods
 */
public class UserTest {
    
    private User user;
    private Date testDate;
    
    @BeforeEach
    void setUp() {
        user = new User();
        testDate = new Date();
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Default constructor should initialize with default values")
        void testDefaultConstructor() {
            User defaultUser = new User();
            
            assertEquals(0, defaultUser.getUserId());
            assertNull(defaultUser.getUsername());
            assertNull(defaultUser.getPasswordHash());
            assertNull(defaultUser.getEmail());
            assertNotNull(defaultUser.getCreatedDate());
            assertFalse(defaultUser.isAdmin());
            
            // Created date should be very recent (within last second)
            long timeDiff = Math.abs(new Date().getTime() - defaultUser.getCreatedDate().getTime());
            assertTrue(timeDiff < 1000, "Created date should be set to current time");
        }
        
        @Test
        @DisplayName("Registration constructor should set basic user fields")
        void testRegistrationConstructor() {
            String username = "testuser";
            String passwordHash = "hashedpassword123";
            String email = "test@example.com";
            
            User registrationUser = new User(username, passwordHash, email);
            
            assertEquals(0, registrationUser.getUserId());
            assertEquals(username, registrationUser.getUsername());
            assertEquals(passwordHash, registrationUser.getPasswordHash());
            assertEquals(email, registrationUser.getEmail());
            assertNotNull(registrationUser.getCreatedDate());
            assertFalse(registrationUser.isAdmin());
        }
        
        @Test
        @DisplayName("Full constructor should set all fields correctly")
        void testFullConstructor() {
            int userId = 123;
            String username = "fulluser";
            String passwordHash = "fullhash456";
            String email = "full@example.com";
            Date createdDate = new Date(System.currentTimeMillis() - 86400000); // 1 day ago
            boolean isAdmin = true;
            
            User fullUser = new User(userId, username, passwordHash, email, createdDate, isAdmin);
            
            assertEquals(userId, fullUser.getUserId());
            assertEquals(username, fullUser.getUsername());
            assertEquals(passwordHash, fullUser.getPasswordHash());
            assertEquals(email, fullUser.getEmail());
            assertEquals(createdDate, fullUser.getCreatedDate());
            assertTrue(fullUser.isAdmin());
        }
    }
    
    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {
        
        @Test
        @DisplayName("UserId getter and setter should work correctly")
        void testUserIdGetterSetter() {
            int expectedId = 42;
            user.setUserId(expectedId);
            assertEquals(expectedId, user.getUserId());
        }
        
        @Test
        @DisplayName("Username getter and setter should work correctly")
        void testUsernameGetterSetter() {
            String expectedUsername = "myusername";
            user.setUsername(expectedUsername);
            assertEquals(expectedUsername, user.getUsername());
        }
        
        @Test
        @DisplayName("Username can be set to null")
        void testUsernameCanBeNull() {
            user.setUsername(null);
            assertNull(user.getUsername());
        }
        
        @Test
        @DisplayName("PasswordHash getter and setter should work correctly")
        void testPasswordHashGetterSetter() {
            String expectedHash = "sha256hash12345";
            user.setPasswordHash(expectedHash);
            assertEquals(expectedHash, user.getPasswordHash());
        }
        
        @Test
        @DisplayName("PasswordHash can be set to null")
        void testPasswordHashCanBeNull() {
            user.setPasswordHash(null);
            assertNull(user.getPasswordHash());
        }
        
        @Test
        @DisplayName("Email getter and setter should work correctly")
        void testEmailGetterSetter() {
            String expectedEmail = "user@domain.com";
            user.setEmail(expectedEmail);
            assertEquals(expectedEmail, user.getEmail());
        }
        
        @Test
        @DisplayName("Email can be set to null")
        void testEmailCanBeNull() {
            user.setEmail(null);
            assertNull(user.getEmail());
        }
        
        @Test
        @DisplayName("CreatedDate getter and setter should work correctly")
        void testCreatedDateGetterSetter() {
            Date expectedDate = new Date(System.currentTimeMillis() - 3600000); // 1 hour ago
            user.setCreatedDate(expectedDate);
            assertEquals(expectedDate, user.getCreatedDate());
        }
        
        @Test
        @DisplayName("CreatedDate can be set to null")
        void testCreatedDateCanBeNull() {
            user.setCreatedDate(null);
            assertNull(user.getCreatedDate());
        }
        
        @Test
        @DisplayName("IsAdmin getter and setter should work correctly")
        void testIsAdminGetterSetter() {
            // Test setting to true
            user.setAdmin(true);
            assertTrue(user.isAdmin());
            
            // Test setting to false
            user.setAdmin(false);
            assertFalse(user.isAdmin());
        }
    }
    
    @Nested
    @DisplayName("Equality and HashCode Tests")
    class EqualityTests {
        
        @Test
        @DisplayName("Users with same userId should be equal")
        void testEqualUsersWithSameUserId() {
            User user1 = new User(1, "user1", "hash1", "email1@test.com", new Date(), false);
            User user2 = new User(1, "user2", "hash2", "email2@test.com", new Date(), true);
            
            assertEquals(user1, user2);
            assertEquals(user1.hashCode(), user2.hashCode());
        }
        
        @Test
        @DisplayName("Users with different userId should not be equal")
        void testUnequalUsersWithDifferentUserId() {
            User user1 = new User(1, "user", "hash", "email@test.com", new Date(), false);
            User user2 = new User(2, "user", "hash", "email@test.com", new Date(), false);
            
            assertNotEquals(user1, user2);
        }
        
        @Test
        @DisplayName("User should be equal to itself")
        void testUserEqualToItself() {
            User testUser = new User(1, "test", "hash", "test@example.com", new Date(), false);
            assertEquals(testUser, testUser);
        }
        
        @Test
        @DisplayName("User should not be equal to null")
        void testUserNotEqualToNull() {
            User testUser = new User(1, "test", "hash", "test@example.com", new Date(), false);
            assertNotEquals(testUser, null);
        }
        
        @Test
        @DisplayName("User should not be equal to different class object")
        void testUserNotEqualToDifferentClass() {
            User testUser = new User(1, "test", "hash", "test@example.com", new Date(), false);
            String differentObject = "I am not a user";
            assertNotEquals(testUser, differentObject);
        }
        
        @Test
        @DisplayName("HashCode should be consistent")
        void testHashCodeConsistency() {
            User testUser = new User(42, "test", "hash", "test@example.com", new Date(), false);
            int firstHash = testUser.hashCode();
            int secondHash = testUser.hashCode();
            assertEquals(firstHash, secondHash);
        }
        
        @Test
        @DisplayName("Default users (userId=0) should be equal")
        void testDefaultUsersEqual() {
            User user1 = new User();
            User user2 = new User();
            assertEquals(user1, user2);
            assertEquals(user1.hashCode(), user2.hashCode());
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("ToString should contain all fields except password hash")
        void testToStringContent() {
            User testUser = new User(123, "testuser", "secrethash", "test@example.com", testDate, true);
            String toString = testUser.toString();
            
            assertTrue(toString.contains("userId=123"));
            assertTrue(toString.contains("username='testuser'"));
            assertTrue(toString.contains("email='test@example.com'"));
            assertTrue(toString.contains("isAdmin=true"));
            assertTrue(toString.contains("createdDate=" + testDate.toString()));
            
            // Password hash should NOT be in toString for security
            assertFalse(toString.contains("secrethash"));
        }
        
        @Test
        @DisplayName("ToString should handle null values gracefully")
        void testToStringWithNullValues() {
            User testUser = new User();
            testUser.setUserId(1);
            testUser.setUsername(null);
            testUser.setEmail(null);
            testUser.setCreatedDate(null);
            
            String toString = testUser.toString();
            
            assertTrue(toString.contains("userId=1"));
            assertTrue(toString.contains("username='null'"));
            assertTrue(toString.contains("email='null'"));
            assertTrue(toString.contains("createdDate=null"));
            assertTrue(toString.contains("isAdmin=false"));
        }
        
        @Test
        @DisplayName("ToString should not be null or empty")
        void testToStringNotNullOrEmpty() {
            User testUser = new User();
            String toString = testUser.toString();
            assertNotNull(toString);
            assertFalse(toString.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Data Integrity Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("User should handle negative userId")
        void testNegativeUserId() {
            user.setUserId(-1);
            assertEquals(-1, user.getUserId());
        }
        
        @Test
        @DisplayName("User should handle very large userId")
        void testLargeUserId() {
            int largeId = Integer.MAX_VALUE;
            user.setUserId(largeId);
            assertEquals(largeId, user.getUserId());
        }
        
        @Test
        @DisplayName("User should handle empty strings")
        void testEmptyStrings() {
            user.setUsername("");
            user.setPasswordHash("");
            user.setEmail("");
            
            assertEquals("", user.getUsername());
            assertEquals("", user.getPasswordHash());
            assertEquals("", user.getEmail());
        }
        
        @Test
        @DisplayName("User should handle very long strings")
        void testLongStrings() {
            String longString = "a".repeat(1000);
            
            user.setUsername(longString);
            user.setPasswordHash(longString);
            user.setEmail(longString);
            
            assertEquals(longString, user.getUsername());
            assertEquals(longString, user.getPasswordHash());
            assertEquals(longString, user.getEmail());
        }
        
        @Test
        @DisplayName("CreatedDate should handle historical dates")
        void testHistoricalCreatedDate() {
            Date historicalDate = new Date(0); // Unix epoch
            user.setCreatedDate(historicalDate);
            assertEquals(historicalDate, user.getCreatedDate());
        }
        
        @Test
        @DisplayName("CreatedDate should handle future dates")
        void testFutureCreatedDate() {
            Date futureDate = new Date(System.currentTimeMillis() + 86400000); // Tomorrow
            user.setCreatedDate(futureDate);
            assertEquals(futureDate, user.getCreatedDate());
        }
        
        @Test
        @DisplayName("Multiple admin status changes should work correctly")
        void testMultipleAdminStatusChanges() {
            assertFalse(user.isAdmin()); // Default is false
            
            user.setAdmin(true);
            assertTrue(user.isAdmin());
            
            user.setAdmin(false);
            assertFalse(user.isAdmin());
            
            user.setAdmin(true);
            assertTrue(user.isAdmin());
        }
    }
    
    @Nested
    @DisplayName("Real-world Scenario Tests")
    class ScenarioTests {
        
        @Test
        @DisplayName("Complete user registration flow")
        void testUserRegistrationFlow() {
            // Simulate user registration
            String username = "newuser123";
            String passwordHash = "hashed_password_from_registration";
            String email = "newuser@example.com";
            
            User newUser = new User(username, passwordHash, email);
            
            // Verify initial state
            assertEquals(username, newUser.getUsername());
            assertEquals(passwordHash, newUser.getPasswordHash());
            assertEquals(email, newUser.getEmail());
            assertFalse(newUser.isAdmin());
            assertNotNull(newUser.getCreatedDate());
            assertEquals(0, newUser.getUserId()); // Not yet saved to database
            
            // Simulate setting database ID after save
            newUser.setUserId(101);
            assertEquals(101, newUser.getUserId());
        }
        
        @Test
        @DisplayName("Admin promotion scenario")
        void testAdminPromotionScenario() {
            // Start with regular user
            User regularUser = new User("regularuser", "hash123", "regular@example.com");
            assertFalse(regularUser.isAdmin());
            
            // Promote to admin
            regularUser.setAdmin(true);
            assertTrue(regularUser.isAdmin());
            
            // Verify other fields unchanged
            assertEquals("regularuser", regularUser.getUsername());
            assertEquals("hash123", regularUser.getPasswordHash());
            assertEquals("regular@example.com", regularUser.getEmail());
        }
        
        @Test
        @DisplayName("User profile update scenario")
        void testUserProfileUpdateScenario() {
            // Create initial user
            User user = new User(1, "oldusername", "oldhash", "old@example.com", new Date(), false);
            
            // Update profile information
            user.setUsername("newusername");
            user.setEmail("new@example.com");
            
            // Verify updates
            assertEquals("newusername", user.getUsername());
            assertEquals("new@example.com", user.getEmail());
            
            // Verify unchanged fields
            assertEquals(1, user.getUserId());
            assertEquals("oldhash", user.getPasswordHash()); // Password not changed
            assertFalse(user.isAdmin());
        }
    }
} 