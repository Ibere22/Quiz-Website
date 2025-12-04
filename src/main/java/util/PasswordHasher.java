package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Password hashing utility class exactly like HW4 Cracker style
 * Uses SHA-1 hashing with optional salt for compatibility
 */
public class PasswordHasher {
    
    // Same character set as HW4 Cracker
    public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();
    private static final int SALT_LENGTH = 8;
    
    /**
     * Hash a password exactly like HW4 Cracker (SHA-1, no salt)
     * @param password The plain text password
     * @return The SHA-1 hash of the password
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] hash = md.digest(password.getBytes());
            return hexToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA algorithm not available", e);
        }
    }
    
    /**
     * Hash a password with salt (more secure version)
     * @param password The plain text password
     * @return The salted and hashed password in format: salt:hash
     */
    public static String hashPasswordWithSalt(String password) {
        String salt = generateSalt();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] hash = md.digest((salt + password).getBytes());
            return salt + ":" + hexToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA algorithm not available", e);
        }
    }
    
    /**
     * Verify a password against a stored hash
     * @param password The plain text password to verify
     * @param storedHash The stored hash (either simple hash or salt:hash format)
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Check if it's salted (contains colon)
            if (storedHash.contains(":")) {
                String[] parts = storedHash.split(":");
                if (parts.length != 2) return false;
                
                String salt = parts[0];
                String originalHash = parts[1];
                MessageDigest md = MessageDigest.getInstance("SHA");
                byte[] hash = md.digest((salt + password).getBytes());
                String newHash = hexToString(hash);
                return originalHash.equals(newHash);
            } else {
                // Simple hash like HW4 Cracker
                return storedHash.equals(hashPassword(password));
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Generate a random salt for password hashing using HW4 Cracker character set
     * @return A random salt string
     */
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        StringBuilder salt = new StringBuilder();
        
        for (int i = 0; i < SALT_LENGTH; i++) {
            int index = random.nextInt(CHARS.length);
            salt.append(CHARS[index]);
        }
        
        return salt.toString();
    }
    
    /**
     * Convert byte array to hex string exactly like HW4 Cracker
     * Given a byte[] array, produces a hex String, such as "234a6f". 
     * with 2 chars for each byte in the array.
     */
    public static String hexToString(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            int val = bytes[i];
            val = val & 0xff;  // remove higher bits, sign
            if (val < 16) buff.append('0'); // leading 0
            buff.append(Integer.toString(val, 16));
        }
        return buff.toString();
    }
} 