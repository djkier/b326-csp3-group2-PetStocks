package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.cliview.modelview.InventoryView;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.util.Scanner;

public class StaffInventoryView {
    private static final int HEADER_WIDTH = 40;

    private final InventoryView inventoryView;
    private final Scanner scanner;

    public StaffInventoryView(InventoryView inventoryView, Scanner scanner) {
        this.inventoryView = inventoryView;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> inventoryView.viewAllInventory();
                case 2 -> CliViewUtility.pauseAfter(scanner, inventoryView.findInventoryById());
                case 3 -> {
                    inventoryView.searchInventory();
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
                case 4 -> CliViewUtility.pauseAfter(
                        scanner, inventoryView.viewLowStockInventory());
                case 5 -> CliViewUtility.pauseAfter(
                        scanner, inventoryView.viewExpiringInventory());
                case 0 -> { }
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
        System.out.println("Inventory");
        System.out.println("=".repeat(HEADER_WIDTH));
        System.out.println("1. View All Inventory");
        System.out.println("2. Find Inventory by ID");
        System.out.println("3. Search Inventory");
        System.out.println("4. View Low-Stock Inventory");
        System.out.println("5. View Expiring Inventory");
        System.out.println("0. Back");
    }
}
