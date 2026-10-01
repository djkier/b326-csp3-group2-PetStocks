package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.model.User;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.util.Scanner;

public class AdminMainView {
    private final StaffMainView staffMainView;
    private final AdminManagementView adminManagementView;
    private final Scanner scanner;

    public AdminMainView(StaffMainView staffMainView,
                         AdminManagementView adminManagementView,
                         Scanner scanner) {
        this.staffMainView = staffMainView;
        this.adminManagementView = adminManagementView;
        this.scanner = scanner;
    }

    public void run(User currentUser) {
        boolean logoutConfirmed = false;

        while (!logoutConfirmed) {
            printMenu();
            int choice = InputUtility.readInt(scanner, "Choice: ");

            if (choice == 0) {
                logoutConfirmed = CliViewUtility.confirmChoice(
                        scanner, "Logout Confirmation", "Are you sure you want to log out?");
                if (logoutConfirmed) {
                    System.out.println("Logging out...");
                }
            } else if (choice == 5) {
                adminManagementView.run();
            } else if (!staffMainView.handleOperationalSelection(choice, currentUser)) {
                System.out.println("Invalid menu selection.");
                InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
            }
        }
    }

    private void printMenu() {
        CliViewUtility.showScreen("Administrator Menu");
        staffMainView.printOperationalOptions();
        System.out.println("5. Admin Management");
        System.out.println("0. Logout");
    }
}
