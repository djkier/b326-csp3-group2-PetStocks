package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.controller.UserController;
import com.joysistvi.petstocks.model.User;

import java.util.Scanner;

public class LoginView {
    private static final int HEADER_WIDTH = 40;

    private final UserController userController;
    private final Scanner scanner;

    public LoginView(UserController userController, Scanner scanner) {
        this.userController = userController;
        this.scanner = scanner;
    }

    public User promptForLogin() {
        while (true) {
            printMenu();
            System.out.print("Username: ");
            String username = scanner.nextLine();
            if ("0".equals(username.trim())) {
                return null;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();
            User authenticatedUser = userController.handleAuthenticate(username, password);
            password = null;

            if (authenticatedUser != null) {
                return authenticatedUser;
            }

            System.out.println("Invalid username or password.");
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=".repeat(HEADER_WIDTH));
        System.out.println("PetStock");
        System.out.println("=".repeat(HEADER_WIDTH));
        System.out.println("Enter 0 as the username to exit.");
    }
}
