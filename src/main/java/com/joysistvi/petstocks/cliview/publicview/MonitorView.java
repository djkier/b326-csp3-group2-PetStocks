package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.cliview.modelview.InventoryView;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.util.Scanner;

public class MonitorView {
    private final InventoryView inventoryView;
    private final Scanner scanner;

    public MonitorView(InventoryView inventoryView, Scanner scanner) {
        this.inventoryView = inventoryView;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> inventoryView.viewLowStockInventory();
                case 2 -> inventoryView.viewExpiringInventory();
                case 0 -> { }
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        CliViewUtility.showScreen("Monitor Inventory");
        System.out.println("1. View Low-Stock Products");
        System.out.println("2. View Soon-Expiring Products");
        System.out.println("0. Back");
    }
}
