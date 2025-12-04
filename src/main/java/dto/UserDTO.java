package dto;

import java.util.Date;

/**
 * Data Transfer Object for User entity
 * Contains only safe, public information that can be displayed on the website
 * Excludes sensitive data like password hash
 */
public record UserDTO(
    int userId,
    String username,
    String email,
    Date createdDate,
    boolean isAdmin
) {
    // Static factory method for conversion from User model
    public static UserDTO fromUser(model.User user) {
        if (user == null) return null;
        return new UserDTO(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedDate(),
            user.isAdmin()
        );
    }
} 