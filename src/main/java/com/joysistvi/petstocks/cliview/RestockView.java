package com.joysistvi.petstocks.cliview;

import com.joysistvi.petstocks.controller.InventoryController;
import com.joysistvi.petstocks.controller.RestockController;
import com.joysistvi.petstocks.controller.SupplierController;
import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.model.Restock;
import com.joysistvi.petstocks.model.Supplier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RestockView {
    private static final DateTimeFormatter DATE_TIME_INPUT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_TIME_DISPLAY =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final RestockController restockController;
    private final InventoryController inventoryController;
    private final SupplierController supplierController;
    private final Scanner scanner;

    public RestockView(RestockController restockController,
                       InventoryController inventoryController,
                       SupplierController supplierController, Scanner scanner) {
        this.restockController = restockController;
        this.inventoryController = inventoryController;
        this.supplierController = supplierController;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> pauseAfter(recordStockIn());
                case 2 -> viewRestockHistory();
                case 3 -> pauseAfter(findRestockById());
                case 4 -> pauseAfter(viewRestocksByInventory());
                case 5 -> pauseAfter(viewRestocksBySupplier());
                case 6 -> pauseAfter(viewRestocksByUser());
                case 7 -> pauseAfter(viewRestocksByDateRange());
                case 0 -> System.out.println("Returning to the development menu...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    pressEnterToContinue();
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        showHeader("Restock / Stock-In Management");
        System.out.println("1. Record Stock In");
        System.out.println("2. View Restock History");
        System.out.println("3. Find Restock by ID");
        System.out.println("4. View Restocks by Inventory");
        System.out.println("5. View Restocks by Supplier");
        System.out.println("6. View Restocks by User");
        System.out.println("7. View Restocks by Date / Date Range");
        System.out.println("0. Back");
    }

    private boolean recordStockIn() {
        showHeader("Record Stock In");

        Inventory inventory = selectInventory();
        if (inventory == null) {
            return false;
        }

        Supplier supplier = selectActiveSupplier();
        if (supplier == null) {
            return false;
        }

        Integer userId = selectUser();
        if (userId == null) {
            return false;
        }

        int quantityDelivered = promptInt("Quantity delivered (0 to cancel): ");
        if (quantityDelivered == 0) {
            return false;
        }

        DateTimeInput deliveryInput = promptDeliveryDateTime();
        if (deliveryInput.cancelled()) {
            return false;
        }

        Restock restock = new Restock(
                inventory, supplier, deliveryInput.value(), quantityDelivered, userId);
        boolean isSuccess = restockController.handleRecordStockIn(restock);
        System.out.println(isSuccess
                ? "Stock-in recorded and inventory quantity increased successfully."
                : "Failed to record stock-in. No changes were committed.");

        if (isSuccess) {
            Restock recorded = restockController.handleFindRestockById(restock.getId());
            if (recorded != null) {
                System.out.println();
                printRestocks(List.of(recorded));
            }
        }
        return true;
    }

    private void viewRestockHistory() {
        String sortBy = "date";
        int choice;

        do {
            showHeader("Restock History");
            printRestocks(restockController.handleViewRestockHistory(sortBy));
            System.out.println();
            System.out.println("Sort by: [1] Date  [2] ID  [3] Product  " +
                    "[4] Supplier  [5] User  [0] Back");
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> sortBy = "date";
                case 2 -> sortBy = "id";
                case 3 -> sortBy = "product";
                case 4 -> sortBy = "supplier";
                case 5 -> sortBy = "user";
                case 0 -> { }
                default -> System.out.println("Invalid sort selection.");
            }
        } while (choice != 0);
    }

    private boolean findRestockById() {
        showHeader("Find Restock By ID");
        int id = promptInt("Restock ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Restock restock = restockController.handleFindRestockById(id);
        if (restock != null) {
            printRestocks(List.of(restock));
        }
        return true;
    }

    private boolean viewRestocksByInventory() {
        showHeader("Restocks By Inventory");
        printInventoryChoices(inventoryController.handleViewAllInventory("id"));
        int inventoryId = promptInt("Inventory ID (0 to cancel): ");
        if (inventoryId == 0) {
            return false;
        }

        printRestocks(restockController.handleViewRestocksByInventory(inventoryId));
        return true;
    }

    private boolean viewRestocksBySupplier() {
        showHeader("Restocks By Supplier");
        List<Supplier> suppliers = new ArrayList<>(
                supplierController.handleViewAllSuppliers("id"));
        suppliers.addAll(supplierController.handleViewArchivedSuppliers("id"));
        printSupplierChoices(suppliers);

        int supplierId = promptInt("Supplier ID (0 to cancel): ");
        if (supplierId == 0) {
            return false;
        }

        printRestocks(restockController.handleViewRestocksBySupplier(supplierId));
        return true;
    }

    private boolean viewRestocksByUser() {
        showHeader("Restocks By User");
        Map<Integer, String> users = restockController.handleGetAvailableUsers();
        printUserChoices(users);
        int userId = promptInt("User ID (0 to cancel): ");
        if (userId == 0) {
            return false;
        }

        printRestocks(restockController.handleViewRestocksByUser(userId));
        return true;
    }

    private boolean viewRestocksByDateRange() {
        showHeader("Restocks By Date / Date Range");
        LocalDate startDate = promptDate("Start date (YYYY-MM-DD, 0 to cancel): ");
        if (startDate == null) {
            return false;
        }

        System.out.print("End date (YYYY-MM-DD, Enter for same date, 0 to cancel): ");
        String endInput = scanner.nextLine().trim();
        if ("0".equals(endInput)) {
            return false;
        }

        LocalDate endDate = endInput.isEmpty() ? startDate : parseDate(endInput);
        while (endDate == null) {
            System.out.print("Enter a valid end date (YYYY-MM-DD, 0 to cancel): ");
            endInput = scanner.nextLine().trim();
            if ("0".equals(endInput)) {
                return false;
            }
            endDate = parseDate(endInput);
        }

        printRestocks(restockController.handleViewRestocksByDateRange(startDate, endDate));
        return true;
    }

    private Inventory selectInventory() {
        List<Inventory> inventory = inventoryController.handleViewAllInventory("id");
        if (inventory.isEmpty()) {
            System.out.println("No inventory records are available.");
            return null;
        }

        printInventoryChoices(inventory);
        while (true) {
            int id = promptInt("Inventory ID (0 to cancel): ");
            if (id == 0) {
                return null;
            }
            for (Inventory item : inventory) {
                if (item.getId() == id) {
                    return item;
                }
            }
            System.out.println("Choose an ID from the inventory list.");
        }
    }

    private Supplier selectActiveSupplier() {
        List<Supplier> suppliers = supplierController.handleViewAllSuppliers("id");
        if (suppliers.isEmpty()) {
            System.out.println("No active suppliers are available.");
            return null;
        }

        printSupplierChoices(suppliers);
        while (true) {
            int id = promptInt("Supplier ID (0 to cancel): ");
            if (id == 0) {
                return null;
            }
            for (Supplier supplier : suppliers) {
                if (supplier.getId() == id) {
                    return supplier;
                }
            }
            System.out.println("Choose an ID from the active supplier list.");
        }
    }

    private Integer selectUser() {
        Map<Integer, String> users = restockController.handleGetAvailableUsers();
        if (users.isEmpty()) {
            System.out.println("No users are available.");
            return null;
        }

        printUserChoices(users);
        while (true) {
            int id = promptInt("User ID (0 to cancel): ");
            if (id == 0) {
                return null;
            }
            if (users.containsKey(id)) {
                return id;
            }
            System.out.println("Choose an ID from the user list.");
        }
    }

    private void printRestocks(List<Restock> restocks) {
        if (restocks.isEmpty()) {
            System.out.println("No restock records found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(22)
                + "+" + "-".repeat(16) + "+" + "-".repeat(22)
                + "+" + "-".repeat(16) + "+" + "-".repeat(18)
                + "+" + "-".repeat(11) + "+";
        String rowFormat = "| %-4s | %-20s | %-14s | %-20s | %-14s | %-16s | %-9s |%n";

        System.out.println(border);
        System.out.printf(rowFormat, "ID", "Product", "Batch Code", "Supplier", "User",
                "Delivered", "Quantity");
        System.out.println(border);
        for (Restock restock : restocks) {
            System.out.printf(rowFormat,
                    restock.getId(),
                    truncate(restock.getInventory().getProduct().getName(), 20),
                    truncate(restock.getInventory().getBatchCode(), 14),
                    truncate(restock.getSupplier().getName(), 20),
                    truncate(restock.getUsername(), 14),
                    restock.getDatetimeDelivered().format(DATE_TIME_DISPLAY),
                    restock.getQuantityDelivered());
        }
        System.out.println(border);
    }

    private void printInventoryChoices(List<Inventory> inventory) {
        if (inventory.isEmpty()) {
            System.out.println("No inventory records found.");
            return;
        }

        System.out.printf("%-6s %-22s %-16s %-10s%n", "ID", "Product", "Batch Code", "Stock");
        for (Inventory item : inventory) {
            System.out.printf("%-6d %-22s %-16s %-10d%n",
                    item.getId(),
                    truncate(item.getProduct().getName(), 22),
                    truncate(item.getBatchCode(), 16),
                    item.getQuantity());
        }
    }

    private void printSupplierChoices(List<Supplier> suppliers) {
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers found.");
            return;
        }

        System.out.printf("%-6s %-28s %-10s%n", "ID", "Supplier", "Status");
        for (Supplier supplier : suppliers) {
            System.out.printf("%-6d %-28s %-10s%n",
                    supplier.getId(), truncate(supplier.getName(), 28),
                    supplier.isArchived() ? "Archived" : "Active");
        }
    }

    private void printUserChoices(Map<Integer, String> users) {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.printf("%-6s %-30s%n", "ID", "Username");
        users.forEach((id, username) ->
                System.out.printf("%-6d %-30s%n", id, truncate(username, 30)));
    }

    private DateTimeInput promptDeliveryDateTime() {
        while (true) {
            System.out.print("Delivery date/time (YYYY-MM-DD HH:mm, Enter for now, 0 to cancel): ");
            String input = scanner.nextLine().trim();
            if ("0".equals(input)) {
                return new DateTimeInput(null, true);
            }
            if (input.isEmpty()) {
                return new DateTimeInput(LocalDateTime.now(), false);
            }

            try {
                return new DateTimeInput(LocalDateTime.parse(input, DATE_TIME_INPUT), false);
            } catch (DateTimeParseException e) {
                System.out.println("Enter a valid date and time in YYYY-MM-DD HH:mm format.");
            }
        }
    }

    private LocalDate promptDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if ("0".equals(input)) {
                return null;
            }

            LocalDate date = parseDate(input);
            if (date != null) {
                return date;
            }
            System.out.println("Enter a valid date in YYYY-MM-DD format.");
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private int promptInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    private void pauseAfter(boolean shouldPause) {
        if (shouldPause) {
            pressEnterToContinue();
        }
    }

    private void pressEnterToContinue() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private void showHeader(String title) {
        System.out.println();
        System.out.println("=".repeat(72));
        System.out.println(title);
        System.out.println("=".repeat(72));
    }

    private record DateTimeInput(LocalDateTime value, boolean cancelled) {
    }
}
