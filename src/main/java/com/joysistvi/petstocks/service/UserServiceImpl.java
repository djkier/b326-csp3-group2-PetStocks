package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.User;
import com.joysistvi.petstocks.repository.UserRepo;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class UserServiceImpl implements UserService {
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_BCRYPT_PASSWORD_BYTES = 72;
    private static final Set<String> VALID_ROLES = Set.of("ADMIN", "STAFF");

    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public List<User> getAllUsers() {
        return getAllUsers("id");
    }

    @Override
    public List<User> getAllUsers(String sortBy) {
        return userRepo.getAllUsers(getApprovedSortValue(sortBy));
    }

    @Override
    public User getUserById(int id) {
        if (!isValidId(id)) {
            return null;
        }

        User user = userRepo.getUserById(id);
        if (user == null) {
            System.out.println("User not found.");
        }
        return user;
    }

    @Override
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return List.of();
        }
        return userRepo.searchUsers(keyword.trim());
    }

    @Override
    public boolean createUser(User user, String plainPassword) {
        if (user == null) {
            System.out.println("User object cannot be null.");
            return false;
        }

        String username = normalizeUsername(user.getUsername());
        String role = normalizeRole(user.getRole());
        if (!validateUsername(username) || role == null || !validatePassword(plainPassword)) {
            return false;
        }
        if (userRepo.usernameExists(username)) {
            System.out.println("Username is already in use.");
            return false;
        }

        user.setUsername(username);
        user.setRole(role);
        user.setPasswordHash(hashPassword(plainPassword));
        return userRepo.createUser(user);
    }

    @Override
    public boolean updateUsername(int id, String username) {
        if (!isValidId(id)) {
            return false;
        }
        if (userRepo.getUserById(id) == null) {
            System.out.println("User not found.");
            return false;
        }

        String normalizedUsername = normalizeUsername(username);
        if (!validateUsername(normalizedUsername)) {
            return false;
        }
        if (userRepo.usernameExistsForAnotherUser(normalizedUsername, id)) {
            System.out.println("Username is already in use.");
            return false;
        }
        return userRepo.updateUsername(id, normalizedUsername);
    }

    @Override
    public boolean updateRole(int id, String role) {
        if (!isValidId(id)) {
            return false;
        }
        if (userRepo.getUserById(id) == null) {
            System.out.println("User not found.");
            return false;
        }

        String normalizedRole = normalizeRole(role);
        if (normalizedRole == null) {
            return false;
        }
        return userRepo.updateRole(id, normalizedRole);
    }

    @Override
    public boolean changePassword(int id, String plainPassword) {
        if (!isValidId(id)) {
            return false;
        }
        if (userRepo.getUserById(id) == null) {
            System.out.println("User not found.");
            return false;
        }
        if (!validatePassword(plainPassword)) {
            return false;
        }
        return userRepo.updatePasswordHash(id, hashPassword(plainPassword));
    }

    private boolean validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            System.out.println("Username is required.");
            return false;
        }
        if (username.length() > MAX_USERNAME_LENGTH) {
            System.out.println("Username cannot exceed 50 characters.");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String plainPassword) {
        if (plainPassword == null || plainPassword.length() < MIN_PASSWORD_LENGTH) {
            System.out.println("Password must contain at least 8 characters.");
            return false;
        }
        if (plainPassword.getBytes(StandardCharsets.UTF_8).length > MAX_BCRYPT_PASSWORD_BYTES) {
            System.out.println("Password cannot exceed 72 bytes.");
            return false;
        }
        return true;
    }

    private String normalizeUsername(String username) {
        return username == null ? null : username.trim();
    }

    private String normalizeRole(String role) {
        if (role == null) {
            System.out.println("Role is required.");
            return null;
        }

        String normalizedRole = role.trim().toUpperCase(Locale.ROOT);
        if (!VALID_ROLES.contains(normalizedRole)) {
            System.out.println("Role must be ADMIN or STAFF.");
            return null;
        }
        return normalizedRole;
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    private String getApprovedSortValue(String sortBy) {
        return switch (sortBy) {
            case "username", "role" -> sortBy;
            default -> "id";
        };
    }

    private boolean isValidId(int id) {
        if (id <= 0) {
            System.out.println("Invalid user ID.");
            return false;
        }
        return true;
    }
}
