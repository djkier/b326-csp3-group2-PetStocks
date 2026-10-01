package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    List<User> getAllUsers(String sortBy);
    User getUserById(int id);
    User authenticate(String username, String plainPassword);
    List<User> searchUsers(String keyword);
    boolean createUser(User user, String plainPassword);
    boolean updateUsername(int id, String username);
    boolean updateRole(int id, String role);
    boolean changePassword(int id, String plainPassword);
}
