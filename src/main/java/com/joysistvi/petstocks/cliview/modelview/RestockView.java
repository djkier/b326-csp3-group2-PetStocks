package com.joysistvi.petstocks.cliview.modelview;

import com.joysistvi.petstocks.controller.InventoryController;
import com.joysistvi.petstocks.controller.RestockController;
import com.joysistvi.petstocks.controller.SupplierController;
import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.model.Restock;
import com.joysistvi.petstocks.model.Supplier;
import com.joysistvi.petstocks.model.User;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> CliViewUtility.pauseAfter(scanner, recordStockIn());
                case 2 -> viewRestockHistory();
                case 3 -> CliViewUtility.pauseAfter(scanner, findRestockById());
                case 4 -> CliViewUtility.pauseAfter(scanner, viewRestocksByInventory());
                case 5 -> CliViewUtility.pauseAfter(scanner, viewRestocksBySupplier());
                case 6 -> CliViewUtility.pauseAfter(scanner, viewRestocksByUser());
                case 7 -> CliViewUtility.pauseAfter(scanner, viewRestocksByDateRange());
                case 0 -> System.out.println("Returning to the development menu...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        CliViewUtility.showHeader("Restock / Stock-In Management");
        System.out.println("1. Record Stock In");
        System.out.println("2. View Restock History");
        System.out.println("3. Find Restock by ID");
        System.out.println("4. View Restocks by Inventory");
        System.out.println("5. View Restocks by Supplier");
        System.out.println("6. View Restocks by User");
        System.out.println("7. View Restocks by Date / Date Range");
        System.out.println("0. Back");
    }

    public boolean recordStockIn() {
        return recordStockIn(null, true);
    }

    public boolean recordStockIn(User currentUser) {
        if (currentUser == null || currentUser.getId() <= 0) {
            System.out.println("A valid logged-in user is required to record stock in.");
            return false;
        }
        return recordStockIn(currentUser, false);
    }

    private boolean recordStockIn(User currentUser, boolean promptForUser) {
        CliViewUtility.showHeader("Record Stock In");

        Inventory inventory = selectInventory();
        if (inventory == null) {
            return false;
        }

        Supplier supplier = selectActiveSupplier();
        if (supplier == null) {
            return false;
        }

        int userId;
        if (promptForUser) {
            Integer selectedUserId = selectUser();
            if (selectedUserId == null) {
                return false;
            }
            userId = selectedUserId;
        } else {
            userId = currentUser.getId();
        }

        int quantityDelivered = InputUtility.readInt(scanner, "Quantity delivered (0 to cancel): ");
        if (quantityDelivered == 0) {
            return false;
        }

        LocalDateTime datetimeDelivered = InputUtility.readDateTimeOrNow(
                scanner,
                "Delivery date/time (YYYY-MM-DD HH:mm, Enter for now, 0 to cancel): ",
                DATE_TIME_INPUT,
                "YYYY-MM-DD HH:mm");
        if (datetimeDelivered == null) {
            return false;
        }

        Restock restock = new Restock(
                inventory, supplier, datetimeDelivered, quantityDelivered, userId);
        boolean isSuccess = restockController.handleRecordStockIn(restock);
        System.out.println(isSuccess
                ? "Stock-in recorded and inventory quantity increased successfully."
                : "Failed to record stock-in. No changes were committed.");

        if (isSuccess) {
            if (!promptForUser) {
                System.out.println("Recorded by: " + currentUser.getUsername());
            }
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
            CliViewUtility.showHeader("Restock History");
            printRestocks(restockController.handleViewRestockHistory(sortBy));
            System.out.println();
            System.out.println("Sort by: [1] Date  [2] ID  [3] Product  " +
                    "[4] Supplier  [5] User  [0] Back");
            choice = InputUtility.readInt(scanner, "Choice: ");

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
        CliViewUtility.showHeader("Find Restock By ID");
        int id = InputUtility.readInt(scanner, "Restock ID (0 to cancel): ");
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
        CliViewUtility.showHeader("Restocks By Inventory");
        printInventoryChoices(inventoryController.handleViewAllInventory("id"));
        int inventoryId = InputUtility.readInt(scanner, "Inventory ID (0 to cancel): ");
        if (inventoryId == 0) {
            return false;
        }

        printRestocks(restockController.handleViewRestocksByInventory(inventoryId));
        return true;
    }

    private boolean viewRestocksBySupplier() {
        CliViewUtility.showHeader("Restocks By Supplier");
        List<Supplier> suppliers = new ArrayList<>(
                supplierController.handleViewAllSuppliers("id"));
        suppliers.addAll(supplierController.handleViewArchivedSuppliers("id"));
        printSupplierChoices(suppliers);

        int supplierId = InputUtility.readInt(scanner, "Supplier ID (0 to cancel): ");
        if (supplierId == 0) {
            return false;
        }

        printRestocks(restockController.handleViewRestocksBySupplier(supplierId));
        return true;
    }

    private boolean viewRestocksByUser() {
        CliViewUtility.showHeader("Restocks By User");
        Map<Integer, String> users = restockController.handleGetAvailableUsers();
        printUserChoices(users);
        int userId = InputUtility.readInt(scanner, "User ID (0 to cancel): ");
        if (userId == 0) {
            return false;
        }

        printRestocks(restockController.handleViewRestocksByUser(userId));
        return true;
    }

    private boolean viewRestocksByDateRange() {
        CliViewUtility.showHeader("Restocks By Date / Date Range");
        LocalDate startDate = InputUtility.readDate(
                scanner, "Start date (YYYY-MM-DD, 0 to cancel): ");
        if (startDate == null) {
            return false;
        }

        System.out.print("End date (YYYY-MM-DD, Enter for same date, 0 to cancel): ");
        String endInput = scanner.nextLine().trim();
        if ("0".equals(endInput)) {
            return false;
        }

        LocalDate endDate = endInput.isEmpty()
                ? startDate : InputUtility.parseDateOrNull(endInput);
        while (endDate == null) {
            System.out.print("Enter a valid end date (YYYY-MM-DD, 0 to cancel): ");
            endInput = scanner.nextLine().trim();
            if ("0".equals(endInput)) {
                return false;
            }
            endDate = InputUtility.parseDateOrNull(endInput);
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
            int id = InputUtility.readInt(scanner, "Inventory ID (0 to cancel): ");
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
            int id = InputUtility.readInt(scanner, "Supplier ID (0 to cancel): ");
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
            int id = InputUtility.readInt(scanner, "User ID (0 to cancel): ");
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
                    CliViewUtility.truncate(restock.getInventory().getProduct().getName(), 20),
                    CliViewUtility.truncate(restock.getInventory().getBatchCode(), 14),
                    CliViewUtility.truncate(restock.getSupplier().getName(), 20),
                    CliViewUtility.truncate(restock.getUsername(), 14),
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
                    CliViewUtility.truncate(item.getProduct().getName(), 22),
                    CliViewUtility.truncate(item.getBatchCode(), 16),
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
                    supplier.getId(), CliViewUtility.truncate(supplier.getName(), 28),
                    CliViewUtility.formatArchiveStatus(supplier.isArchived()));
        }
    }

    private void printUserChoices(Map<Integer, String> users) {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.printf("%-6s %-30s%n", "ID", "Username");
        users.forEach((id, username) ->
                System.out.printf("%-6d %-30s%n", id, CliViewUtility.truncate(username, 30)));
    }

}
