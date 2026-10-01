package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.model.User;
import com.joysistvi.petstocks.utility.InputUtility;

import java.util.Scanner;

public class AdminMainView {
    private static final int HEADER_WIDTH = 40;

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
        int choice;

        do {
            printMenu();
            choice = InputUtility.readInt(scanner, "Choice: ");

            if (choice == 0) {
                System.out.println("Logging out...");
            } else if (choice == 5) {
                adminManagementView.run();
            } else if (!staffMainView.handleOperationalSelection(choice, currentUser)) {
                System.out.println("Invalid menu selection.");
                InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
            }
        } while (choice != 0);
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=".repeat(HEADER_WIDTH));
        System.out.println("Administrator Menu");
        System.out.println("=".repeat(HEADER_WIDTH));
        staffMainView.printOperationalOptions();
        System.out.println("5. Admin Management");
        System.out.println("0. Logout");
    }
}
