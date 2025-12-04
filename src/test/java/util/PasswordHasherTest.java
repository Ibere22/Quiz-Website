package util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for PasswordHasher class
 * Tests all hashing methods, verification logic, and edge cases
 */
class PasswordHasherTest {

    // ========================= BASIC HASHING TESTS =========================

    @Test
    @DisplayName("Test basic password hashing produces consistent results")
    void testHashPassword_ConsistentResults() {
        // Arrange
        String password = "testpassword";

        // Act
        String hash1 = PasswordHasher.hashPassword(password);
        String hash2 = PasswordHasher.hashPassword(password);

        // Assert
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertEquals(hash1, hash2, "Same password should produce same hash");
        assertNotEquals(password, hash1, "Hash should be different from original password");
    }

    @Test
    @DisplayName("Test different passwords produce different hashes")
    void testHashPassword_DifferentPasswords() {
        // Arrange
        String password1 = "password1";
        String password2 = "password2";

        // Act
        String hash1 = PasswordHasher.hashPassword(password1);
        String hash2 = PasswordHasher.hashPassword(password2);

        // Assert
        assertNotEquals(hash1, hash2, "Different passwords should produce different hashes");
    }

    @Test
    @DisplayName("Test hash password with empty string")
    void testHashPassword_EmptyString() {
        // Act
        String hash = PasswordHasher.hashPassword("");

        // Assert
        assertNotNull(hash);
        assertFalse(hash.isEmpty(), "Empty password should still produce a hash");
    }

    @Test
    @DisplayName("Test hash password with special characters")
    void testHashPassword_SpecialCharacters() {
        // Arrange
        String password = "p@ssw0rd!#$%^&*()";

        // Act
        String hash = PasswordHasher.hashPassword(password);

        // Assert
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
        assertTrue(hash.matches("[0-9a-f]+"), "Hash should be hexadecimal");
    }

    @Test
    @DisplayName("Test hash password with unicode characters")
    void testHashPassword_UnicodeCharacters() {
        // Arrange
        String password = "pässwörd测试";

        // Act
        String hash = PasswordHasher.hashPassword(password);

        // Assert
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    // ========================= SALTED HASHING TESTS =========================

    @Test
    @DisplayName("Test salted password hashing produces different results each time")
    void testHashPasswordWithSalt_DifferentResults() {
        // Arrange
        String password = "testpassword";

        // Act
        String hash1 = PasswordHasher.hashPasswordWithSalt(password);
        String hash2 = PasswordHasher.hashPasswordWithSalt(password);

        // Assert
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2, "Salted hashes should be different each time");
        assertTrue(hash1.contains(":"), "Salted hash should contain colon separator");
        assertTrue(hash2.contains(":"), "Salted hash should contain colon separator");
    }

    @Test
    @DisplayName("Test salted hash format is correct")
    void testHashPasswordWithSalt_CorrectFormat() {
        // Arrange
        String password = "testpassword";

        // Act
        String saltedHash = PasswordHasher.hashPasswordWithSalt(password);

        // Assert
        assertNotNull(saltedHash);
        assertTrue(saltedHash.contains(":"), "Salted hash should contain colon");
        String[] parts = saltedHash.split(":");
        assertEquals(2, parts.length, "Salted hash should have exactly 2 parts");
        
        String salt = parts[0];
        String hash = parts[1];
        
        assertEquals(8, salt.length(), "Salt should be 8 characters long");
        assertFalse(hash.isEmpty(), "Hash part should not be empty");
        assertTrue(hash.matches("[0-9a-f]+"), "Hash should be hexadecimal");
    }

    @Test
    @DisplayName("Test salted hash uses valid character set for salt")
    void testHashPasswordWithSalt_ValidCharacterSet() {
        // Arrange
        String password = "testpassword";
        String validChars = "abcdefghijklmnopqrstuvwxyz0123456789.,-!";

        // Act
        String saltedHash = PasswordHasher.hashPasswordWithSalt(password);
        String salt = saltedHash.split(":")[0];

        // Assert
        for (char c : salt.toCharArray()) {
            assertTrue(validChars.indexOf(c) >= 0, 
                "Salt character '" + c + "' should be in valid character set");
        }
    }

    // ========================= PASSWORD VERIFICATION TESTS =========================

    @Test
    @DisplayName("Test verify password with simple hash")
    void testVerifyPassword_SimpleHash_Success() {
        // Arrange
        String password = "testpassword";
        String hash = PasswordHasher.hashPassword(password);

        // Act
        boolean isValid = PasswordHasher.verifyPassword(password, hash);

        // Assert
        assertTrue(isValid, "Password should verify against its hash");
    }

    @Test
    @DisplayName("Test verify password with wrong password")
    void testVerifyPassword_WrongPassword_Failure() {
        // Arrange
        String correctPassword = "correctpassword";
        String wrongPassword = "wrongpassword";
        String hash = PasswordHasher.hashPassword(correctPassword);

        // Act
        boolean isValid = PasswordHasher.verifyPassword(wrongPassword, hash);

        // Assert
        assertFalse(isValid, "Wrong password should not verify");
    }

    @Test
    @DisplayName("Test verify password with salted hash")
    void testVerifyPassword_SaltedHash_Success() {
        // Arrange
        String password = "testpassword";
        String saltedHash = PasswordHasher.hashPasswordWithSalt(password);

        // Act
        boolean isValid = PasswordHasher.verifyPassword(password, saltedHash);

        // Assert
        assertTrue(isValid, "Password should verify against its salted hash");
    }

    @Test
    @DisplayName("Test verify password with salted hash - wrong password")
    void testVerifyPassword_SaltedHash_WrongPassword() {
        // Arrange
        String correctPassword = "correctpassword";
        String wrongPassword = "wrongpassword";
        String saltedHash = PasswordHasher.hashPasswordWithSalt(correctPassword);

        // Act
        boolean isValid = PasswordHasher.verifyPassword(wrongPassword, saltedHash);

        // Assert
        assertFalse(isValid, "Wrong password should not verify against salted hash");
    }

    @Test
    @DisplayName("Test verify password with malformed salted hash")
    void testVerifyPassword_MalformedSaltedHash_Failure() {
        // Arrange
        String password = "testpassword";
        String malformedHash = "salt:hash:extra";

        // Act
        boolean isValid = PasswordHasher.verifyPassword(password, malformedHash);

        // Assert
        assertFalse(isValid, "Malformed salted hash should not verify");
    }

    @Test
    @DisplayName("Test verify password with invalid salted hash format")
    void testVerifyPassword_InvalidSaltedHashFormat_Failure() {
        // Arrange
        String password = "testpassword";
        String invalidHash = "noseparator";

        // Act
        boolean isValid = PasswordHasher.verifyPassword(password, invalidHash);

        // Assert
        assertFalse(isValid, "Invalid hash format should not verify");
    }

    @Test
    @DisplayName("Test verify password with empty hash")
    void testVerifyPassword_EmptyHash_Failure() {
        // Arrange
        String password = "testpassword";
        String emptyHash = "";

        // Act
        boolean isValid = PasswordHasher.verifyPassword(password, emptyHash);

        // Assert
        assertFalse(isValid, "Empty hash should not verify");
    }

    // ========================= HEX CONVERSION TESTS =========================

    @Test
    @DisplayName("Test hex to string conversion")
    void testHexToString_ValidBytes() {
        // Arrange
        byte[] bytes = {(byte) 0x12, (byte) 0x34, (byte) 0xAB, (byte) 0xCD};

        // Act
        String hex = PasswordHasher.hexToString(bytes);

        // Assert
        assertEquals("1234abcd", hex, "Hex conversion should be correct");
    }

    @Test
    @DisplayName("Test hex to string with single digit bytes")
    void testHexToString_SingleDigitBytes() {
        // Arrange
        byte[] bytes = {(byte) 0x01, (byte) 0x0A, (byte) 0x0F};

        // Act
        String hex = PasswordHasher.hexToString(bytes);

        // Assert
        assertEquals("010a0f", hex, "Single digit bytes should have leading zeros");
    }

    @Test
    @DisplayName("Test hex to string with negative bytes")
    void testHexToString_NegativeBytes() {
        // Arrange
        byte[] bytes = {(byte) 0xFF, (byte) 0x80};

        // Act
        String hex = PasswordHasher.hexToString(bytes);

        // Assert
        assertEquals("ff80", hex, "Negative bytes should be handled correctly");
    }

    @Test
    @DisplayName("Test hex to string with empty array")
    void testHexToString_EmptyArray() {
        // Arrange
        byte[] bytes = {};

        // Act
        String hex = PasswordHasher.hexToString(bytes);

        // Assert
        assertEquals("", hex, "Empty byte array should produce empty string");
    }

    // ========================= EDGE CASE TESTS =========================

    @Test
    @DisplayName("Test character set constant")
    void testCharacterSetConstant() {
        // Assert
        assertNotNull(PasswordHasher.CHARS);
        assertEquals("abcdefghijklmnopqrstuvwxyz0123456789.,-!", new String(PasswordHasher.CHARS));
    }

    @Test
    @DisplayName("Test hash password with very long password")
    void testHashPassword_VeryLongPassword() {
        // Arrange
        StringBuilder longPassword = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longPassword.append("a");
        }

        // Act
        String hash = PasswordHasher.hashPassword(longPassword.toString());

        // Assert
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    @DisplayName("Test consistency between hash and verify")
    void testHashAndVerify_Consistency() {
        // Test multiple passwords to ensure consistency
        String[] passwords = {
            "simple",
            "complex!@#$%",
            "",
            "unicode测试",
            "very_long_password_with_many_characters_to_test_edge_cases"
        };

        for (String password : passwords) {
            // Simple hash
            String hash = PasswordHasher.hashPassword(password);
            assertTrue(PasswordHasher.verifyPassword(password, hash), 
                "Password '" + password + "' should verify against its hash");

            // Salted hash
            String saltedHash = PasswordHasher.hashPasswordWithSalt(password);
            assertTrue(PasswordHasher.verifyPassword(password, saltedHash),
                "Password '" + password + "' should verify against its salted hash");
        }
    }

    @Test
    @DisplayName("Test known hash value for compatibility")
    void testKnownHashValue() {
        // This test ensures the hashing algorithm hasn't changed
        // Using a known input-output pair
        String password = "hello";
        String expectedHash = "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d"; // SHA-1 of "hello"

        // Act
        String actualHash = PasswordHasher.hashPassword(password);

        // Assert
        assertEquals(expectedHash, actualHash, "Hash should match known SHA-1 value");
    }
} 