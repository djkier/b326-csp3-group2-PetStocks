package com.joysistvi.petstocks.cliview.modelview;

import com.joysistvi.petstocks.controller.UserController;
import com.joysistvi.petstocks.model.User;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

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
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> CliViewUtility.pauseAfter(scanner, findUserById());
                case 3 -> {
                    searchUsers();
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
                case 4 -> CliViewUtility.pauseAfter(scanner, createUser());
                case 5 -> CliViewUtility.pauseAfter(scanner, updateUsername());
                case 6 -> CliViewUtility.pauseAfter(scanner, updateRole());
                case 7 -> CliViewUtility.pauseAfter(scanner, changePassword());
                case 0 -> System.out.println("Returning to the development menu...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        CliViewUtility.showHeader("User Management");
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
        int currentPage = 0;

        while (true) {
            List<User> users = userController.handleViewAllUsers(sortBy);
            currentPage = CliViewUtility.normalizePage(currentPage, users.size());
            CliViewUtility.showHeader("All Users");
            printUsers(CliViewUtility.page(users, currentPage));
            CliViewUtility.printPagination(currentPage, users.size());
            System.out.println("Sort by: [1] ID  [2] Username  [3] Role  [0] Back");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1" -> { sortBy = "id"; currentPage = 0; }
                case "2" -> { sortBy = "username"; currentPage = 0; }
                case "3" -> { sortBy = "role"; currentPage = 0; }
                case "P" -> currentPage = CliViewUtility.previousPage(currentPage);
                case "N" -> currentPage = CliViewUtility.nextPage(currentPage, users.size());
                case "0" -> { return; }
                default -> System.out.println("Invalid sort selection.");
            }
        }
    }

    private boolean findUserById() {
        CliViewUtility.showHeader("Find User By ID");
        int id = InputUtility.readInt(scanner, "User ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        User user = userController.handleFindUserById(id);
        if (user != null) {
            CliViewUtility.browsePages(List.of(user), scanner, this::printUsers);
        }
        return true;
    }

    private void searchUsers() {
        CliViewUtility.showHeader("Search Users");
        System.out.print("Enter username or role: ");
        String keyword = scanner.nextLine();
        CliViewUtility.browsePages(
                userController.searchUsers(keyword), scanner, this::printUsers);
    }

    private boolean createUser() {
        CliViewUtility.showHeader("Create / Register User");
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
        CliViewUtility.showHeader("Update Username");
        CliViewUtility.browsePages(
                userController.handleViewAllUsers("id"), scanner, this::printUsers);
        int id = InputUtility.readInt(scanner, "User ID to update (0 to cancel): ");
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
        CliViewUtility.showHeader("Update User Role");
        CliViewUtility.browsePages(
                userController.handleViewAllUsers("id"), scanner, this::printUsers);
        int id = InputUtility.readInt(scanner, "User ID to update (0 to cancel): ");
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
        CliViewUtility.showHeader("Change User Password");
        CliViewUtility.browsePages(
                userController.handleViewAllUsers("id"), scanner, this::printUsers);
        int id = InputUtility.readInt(scanner, "User ID (0 to cancel): ");
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
            int choice = InputUtility.readInt(scanner, "Role: ");

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
        String border = "+" + "-".repeat(6)
                + "+" + "-".repeat(USERNAME_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(ROLE_DISPLAY_WIDTH + 2) + "+";
        String rowFormat = "| %-4s | %-" + USERNAME_DISPLAY_WIDTH + "s | %-"
                + ROLE_DISPLAY_WIDTH + "s |%n";

        System.out.println(border);
        System.out.printf(rowFormat, "ID", "Username", "Role");
        System.out.println(border);
        if (users.isEmpty()) {
            System.out.println("No users found.");
            System.out.println(border);
            return;
        }
        for (User user : users) {
            System.out.printf(rowFormat,
                    user.getId(),
                    CliViewUtility.truncate(user.getUsername(), USERNAME_DISPLAY_WIDTH),
                    CliViewUtility.truncate(user.getRole(), ROLE_DISPLAY_WIDTH));
        }
        System.out.println(border);
    }

}
