package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.controller.UserController;
import com.joysistvi.petstocks.model.User;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.util.Scanner;

public class RegistrationView {
    private static final String PUBLIC_ROLE = "STAFF";

    private final UserController userController;
    private final Scanner scanner;

    public RegistrationView(UserController userController, Scanner scanner) {
        this.userController = userController;
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            CliViewUtility.showHeader("Register Staff Account");
            System.out.print("Username (0 to cancel): ");
            String username = scanner.nextLine();
            if ("0".equals(username.trim())) {
                return;
            }

            System.out.print("Password (0 to cancel): ");
            String password = scanner.nextLine();
            if ("0".equals(password)) {
                return;
            }

            System.out.print("Confirm Password: ");
            String confirmation = scanner.nextLine();
            if (!password.equals(confirmation)) {
                password = null;
                confirmation = null;
                System.out.println("Passwords do not match.");
                InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                continue;
            }

            User user = new User(username, PUBLIC_ROLE);
            boolean isSuccess = userController.handleCreateUser(user, password);
            password = null;
            confirmation = null;

            if (isSuccess) {
                System.out.println("Staff account registered successfully.");
                InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                return;
            }

            System.out.println("Registration failed.");
            InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
        }
    }

}
