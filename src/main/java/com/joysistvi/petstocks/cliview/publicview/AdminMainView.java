package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.cliview.modelview.DispatchView;
import com.joysistvi.petstocks.cliview.modelview.RestockView;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.util.Scanner;

public class AdminMainView {
    private static final int HEADER_WIDTH = 40;

    private final StaffInventoryView staffInventoryView;
    private final RestockView restockView;
    private final DispatchView dispatchView;
    private final StockMovementView stockMovementView;
    private final MonitorView monitorView;
    private final AdminManagementView adminManagementView;
    private final Scanner scanner;

    public AdminMainView(StaffInventoryView staffInventoryView,
                         RestockView restockView,
                         DispatchView dispatchView,
                         StockMovementView stockMovementView,
                         MonitorView monitorView,
                         AdminManagementView adminManagementView,
                         Scanner scanner) {
        this.staffInventoryView = staffInventoryView;
        this.restockView = restockView;
        this.dispatchView = dispatchView;
        this.stockMovementView = stockMovementView;
        this.monitorView = monitorView;
        this.adminManagementView = adminManagementView;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> staffInventoryView.run();
                case 2 -> CliViewUtility.pauseAfter(scanner, restockView.recordStockIn());
                case 3 -> CliViewUtility.pauseAfter(scanner, dispatchView.recordStockOut());
                case 4 -> stockMovementView.run();
                case 5 -> monitorView.run();
                case 6 -> adminManagementView.run();
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
        System.out.println("Administrator Menu");
        System.out.println("=".repeat(HEADER_WIDTH));
        System.out.println("1. Inventory");
        System.out.println("2. Record Stock In");
        System.out.println("3. Record Stock Out");
        System.out.println("4. Stock Movement History");
        System.out.println("5. Monitor Inventory");
        System.out.println("6. Admin Management");
        System.out.println("0. Logout");
    }
}
