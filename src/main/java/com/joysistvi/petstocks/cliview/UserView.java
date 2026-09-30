package com.joysistvi.petstocks.cliview;

import com.joysistvi.petstocks.controller.UserController;
import com.joysistvi.petstocks.model.User;

import java.util.List;
import java.util.Scanner;

public class UserView {
    private static final int USERNAME_DISPLAY_WIDTH = 30;
    private static final int ROLE_DISPLAY_WIDTH = 10;

    private final UserController userController;
    private final Scanner scanner;

    public UserView(UserController userController, Scanner scanner) {
        this.userController = userController;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> pauseAfter(findUserById());
                case 3 -> {
                    searchUsers();
                    pressEnterToContinue();
                }
                case 4 -> pauseAfter(createUser());
                case 5 -> pauseAfter(updateUsername());
                case 6 -> pauseAfter(updateRole());
                case 7 -> pauseAfter(changePassword());
                case 0 -> System.out.println("Returning to the development menu...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    pressEnterToContinue();
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        showHeader("User Management");
        System.out.println("1. View All Users");
        System.out.println("2. Find User by ID");
        System.out.println("3. Search Users");
        System.out.println("4. Create / Register User");
        System.out.println("5. Update Username");
        System.out.println("6. Update Role");
        System.out.println("7. Change Password");
        System.out.println("0. Back");
    }

    private void viewAllUsers() {
        String sortBy = "id";
        int choice;

        do {
            showHeader("All Users");
            printUsers(userController.handleViewAllUsers(sortBy));
            System.out.println();
            System.out.println("Sort by: [1] ID  [2] Username  [3] Role  [0] Back");
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> sortBy = "id";
                case 2 -> sortBy = "username";
                case 3 -> sortBy = "role";
                case 0 -> { }
                default -> System.out.println("Invalid sort selection.");
            }
        } while (choice != 0);
    }

    private boolean findUserById() {
        showHeader("Find User By ID");
        int id = promptInt("User ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        User user = userController.handleFindUserById(id);
        if (user != null) {
            printUsers(List.of(user));
        }
        return true;
    }

    private void searchUsers() {
        showHeader("Search Users");
        System.out.print("Enter username or role: ");
        String keyword = scanner.nextLine();
        printUsers(userController.searchUsers(keyword));
    }

    private boolean createUser() {
        showHeader("Create / Register User");
        System.out.print("Username (0 to cancel): ");
        String username = scanner.nextLine();
        if ("0".equals(username.trim())) {
            return false;
        }

        String role = promptRole();
        if (role == null) {
            return false;
        }

        String password = promptNewPassword();
        if (password == null) {
            return false;
        }

        User user = new User(username, role);
        boolean isSuccess = userController.handleCreateUser(user, password);
        System.out.println(isSuccess
                ? "User registered successfully."
                : "Failed to register user.");
        return true;
    }

    private boolean updateUsername() {
        showHeader("Update Username");
        printUsers(userController.handleViewAllUsers("id"));
        int id = promptInt("User ID to update (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        User current = userController.handleFindUserById(id);
        if (current == null) {
            return true;
        }

        System.out.print("New username [" + current.getUsername() + "] (0 to cancel): ");
        String username = scanner.nextLine();
        if ("0".equals(username.trim())) {
            return false;
        }

        boolean isSuccess = userController.handleUpdateUsername(id, username);
        System.out.println(isSuccess
                ? "Username updated successfully."
                : "Failed to update username.");
        return true;
    }

    private boolean updateRole() {
        showHeader("Update User Role");
        printUsers(userController.handleViewAllUsers("id"));
        int id = promptInt("User ID to update (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        User current = userController.handleFindUserById(id);
        if (current == null) {
            return true;
        }

        System.out.println("Current role: " + current.getRole());
        String role = promptRole();
        if (role == null) {
            return false;
        }

        boolean isSuccess = userController.handleUpdateRole(id, role);
        System.out.println(isSuccess
                ? "User role updated successfully."
                : "Failed to update user role.");
        return true;
    }

    private boolean changePassword() {
        showHeader("Change User Password");
        printUsers(userController.handleViewAllUsers("id"));
        int id = promptInt("User ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        if (userController.handleFindUserById(id) == null) {
            return true;
        }

        String password = promptNewPassword();
        if (password == null) {
            return false;
        }

        boolean isSuccess = userController.handleChangePassword(id, password);
        System.out.println(isSuccess
                ? "Password changed successfully."
                : "Failed to change password.");
        return true;
    }

    private String promptRole() {
        while (true) {
            System.out.println("1. ADMIN");
            System.out.println("2. STAFF");
            System.out.println("0. Cancel");
            int choice = promptInt("Role: ");

            switch (choice) {
                case 1:
                    return "ADMIN";
                case 2:
                    return "STAFF";
                case 0:
                    return null;
                default:
                    System.out.println("Invalid role selection.");
            }
        }
    }

    private String promptNewPassword() {
        System.out.print("New password (minimum 8 characters, 0 to cancel): ");
        String password = scanner.nextLine();
        if ("0".equals(password)) {
            return null;
        }

        System.out.print("Confirm password: ");
        String confirmation = scanner.nextLine();
        if (!password.equals(confirmation)) {
            System.out.println("Passwords do not match.");
            return null;
        }
        return password;
    }

    public void printUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        String border = "+" + "-".repeat(6)
                + "+" + "-".repeat(USERNAME_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(ROLE_DISPLAY_WIDTH + 2) + "+";
        String rowFormat = "| %-4s | %-" + USERNAME_DISPLAY_WIDTH + "s | %-"
                + ROLE_DISPLAY_WIDTH + "s |%n";

        System.out.println(border);
        System.out.printf(rowFormat, "ID", "Username", "Role");
        System.out.println(border);
        for (User user : users) {
            System.out.printf(rowFormat,
                    user.getId(),
                    truncate(user.getUsername(), USERNAME_DISPLAY_WIDTH),
                    truncate(user.getRole(), ROLE_DISPLAY_WIDTH));
        }
        System.out.println(border);
    }

    private int promptInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    private void pauseAfter(boolean shouldPause) {
        if (shouldPause) {
            pressEnterToContinue();
        }
    }

    private void pressEnterToContinue() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private void showHeader(String title) {
        System.out.println();
        System.out.println("=".repeat(72));
        System.out.println(title);
        System.out.println("=".repeat(72));
    }
}
