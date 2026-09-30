package com.joysistvi.petstocks.controller;

import com.joysistvi.petstocks.model.User;
import com.joysistvi.petstocks.service.UserService;

import java.util.List;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public List<User> handleViewAllUsers() {
        return userService.getAllUsers();
    }

    public List<User> handleViewAllUsers(String sortBy) {
        return userService.getAllUsers(sortBy);
    }

    public User handleFindUserById(int id) {
        return userService.getUserById(id);
    }

    public List<User> searchUsers(String keyword) {
        return userService.searchUsers(keyword);
    }

    public boolean handleCreateUser(User user, String plainPassword) {
        return userService.createUser(user, plainPassword);
    }

    public boolean handleUpdateUsername(int id, String username) {
        return userService.updateUsername(id, username);
    }

    public boolean handleUpdateRole(int id, String role) {
        return userService.updateRole(id, role);
    }

    public boolean handleChangePassword(int id, String plainPassword) {
        return userService.changePassword(id, plainPassword);
    }
}
