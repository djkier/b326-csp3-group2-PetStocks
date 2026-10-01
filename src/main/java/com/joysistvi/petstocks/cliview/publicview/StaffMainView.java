package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.cliview.modelview.DispatchView;
import com.joysistvi.petstocks.cliview.modelview.RestockView;
import com.joysistvi.petstocks.model.User;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.util.Scanner;

public class StaffMainView {
    private static final int HEADER_WIDTH = 40;

    private final StaffInventoryView staffInventoryView;
    private final RestockView restockView;
    private final DispatchView dispatchView;
    private final StockMovementView stockMovementView;
    private final Scanner scanner;

    public StaffMainView(StaffInventoryView staffInventoryView,
                         RestockView restockView,
                         DispatchView dispatchView,
                         StockMovementView stockMovementView,
                         Scanner scanner) {
        this.staffInventoryView = staffInventoryView;
        this.restockView = restockView;
        this.dispatchView = dispatchView;
        this.stockMovementView = stockMovementView;
        this.scanner = scanner;
    }

    public void run(User currentUser) {
        int choice;

        do {
            printMenu();
            choice = InputUtility.readInt(scanner, "Choice: ");

            if (choice == 0) {
                System.out.println("Logging out...");
            } else if (!handleOperationalSelection(choice, currentUser)) {
                System.out.println("Invalid menu selection.");
                InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
            }
        } while (choice != 0);
    }

    public boolean handleOperationalSelection(int choice, User currentUser) {
        switch (choice) {
            case 1 -> staffInventoryView.run();
            case 2 -> CliViewUtility.pauseAfter(
                    scanner, restockView.recordStockIn(currentUser));
            case 3 -> CliViewUtility.pauseAfter(
                    scanner, dispatchView.recordStockOut(currentUser));
            case 4 -> stockMovementView.run();
            default -> {
                return false;
            }
        }
        return true;
    }

    public void printOperationalOptions() {
        System.out.println("1. Inventory");
        System.out.println("2. Record Stock In");
        System.out.println("3. Record Stock Out");
        System.out.println("4. Stock Movement History");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=".repeat(HEADER_WIDTH));
        System.out.println("Staff Menu");
        System.out.println("=".repeat(HEADER_WIDTH));
        printOperationalOptions();
        System.out.println("0. Logout");
    }
}
