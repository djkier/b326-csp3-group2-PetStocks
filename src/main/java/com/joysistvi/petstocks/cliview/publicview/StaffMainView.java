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
    private final MonitorView monitorView;
    private final Scanner scanner;

    public StaffMainView(StaffInventoryView staffInventoryView,
                         RestockView restockView,
                         DispatchView dispatchView,
                         StockMovementView stockMovementView,
                         MonitorView monitorView,
                         Scanner scanner) {
        this.staffInventoryView = staffInventoryView;
        this.restockView = restockView;
        this.dispatchView = dispatchView;
        this.stockMovementView = stockMovementView;
        this.monitorView = monitorView;
        this.scanner = scanner;
    }

    public void run(User currentUser) {
        int choice;

        do {
            printMenu();
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> staffInventoryView.run();
                case 2 -> CliViewUtility.pauseAfter(
                        scanner, restockView.recordStockIn(currentUser));
                case 3 -> CliViewUtility.pauseAfter(
                        scanner, dispatchView.recordStockOut(currentUser));
                case 4 -> stockMovementView.run();
                case 5 -> monitorView.run();
                case 0 -> System.out.println("Logging out...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=".repeat(HEADER_WIDTH));
        System.out.println("Staff Menu");
        System.out.println("=".repeat(HEADER_WIDTH));
        System.out.println("1. Inventory");
        System.out.println("2. Record Stock In");
        System.out.println("3. Record Stock Out");
        System.out.println("4. Stock Movement History");
        System.out.println("5. Monitor Inventory");
        System.out.println("0. Logout");
    }
}
