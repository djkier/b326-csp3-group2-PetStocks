package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.controller.UserController;
import com.joysistvi.petstocks.model.User;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.util.Scanner;

public class LoginView {
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
            InputUtility.pressEnterToContinue(scanner, "Press Enter to try again...");
        }
    }

    public void showWelcome(User user) {
        System.out.println();
        System.out.println("Welcome " + user.getUsername()
                + " to PetStock Inventory Management System");
        InputUtility.pressEnterToContinue(scanner);
    }

    private void printMenu() {
        CliViewUtility.showScreen("Login");
        System.out.println("Enter 0 as the username to go back.");
    }
}
