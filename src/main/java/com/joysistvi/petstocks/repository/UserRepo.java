package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.model.User;

import java.util.List;

public interface UserRepo {
    List<User> getAllUsers();
    List<User> getAllUsers(String sortBy);
    User getUserById(int id);
    User getUserByUsername(String username);
    List<User> searchUsers(String keyword);
    boolean usernameExists(String username);
    boolean usernameExistsForAnotherUser(String username, int userId);
    boolean createUser(User user);
    boolean updateUsername(int id, String username);
    boolean updateRole(int id, String role);
    boolean updatePasswordHash(int id, String passwordHash);
}
