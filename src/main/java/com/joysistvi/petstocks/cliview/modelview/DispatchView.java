package com.joysistvi.petstocks.cliview.modelview;

import com.joysistvi.petstocks.controller.DispatchController;
import com.joysistvi.petstocks.controller.InventoryController;
import com.joysistvi.petstocks.controller.ProductController;
import com.joysistvi.petstocks.model.Dispatch;
import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.model.Product;
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

public class DispatchView {
    private static final DateTimeFormatter DATE_TIME_DISPLAY =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final DispatchController dispatchController;
    private final InventoryController inventoryController;
    private final ProductController productController;
    private final InventoryView inventoryView;
    private final Scanner scanner;

    public DispatchView(DispatchController dispatchController,
                        InventoryController inventoryController,
                        ProductController productController,
                        InventoryView inventoryView,
                        Scanner scanner) {
        this.dispatchController = dispatchController;
        this.inventoryController = inventoryController;
        this.productController = productController;
        this.inventoryView = inventoryView;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> CliViewUtility.pauseAfter(scanner, recordStockOut());
                case 2 -> viewDispatchHistory();
                case 3 -> CliViewUtility.pauseAfter(scanner, findDispatchById());
                case 4 -> CliViewUtility.pauseAfter(scanner, viewDispatchesByInventory());
                case 5 -> CliViewUtility.pauseAfter(scanner, viewDispatchesByProduct());
                case 6 -> CliViewUtility.pauseAfter(scanner, viewDispatchesByUser());
                case 7 -> CliViewUtility.pauseAfter(scanner, viewDispatchesByDateRange());
                case 0 -> System.out.println("Returning to the development menu...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        CliViewUtility.showHeader("Dispatch / Stock-Out Management");
        System.out.println("1. Record Stock Out");
        System.out.println("2. View Dispatch History");
        System.out.println("3. Find Dispatch by ID");
        System.out.println("4. View Dispatches by Inventory");
        System.out.println("5. View Dispatches by Product");
        System.out.println("6. View Dispatches by User");
        System.out.println("7. View Dispatches by Date / Date Range");
        System.out.println("0. Back");
    }

    public boolean recordStockOut() {
        return recordStockOut(null, true);
    }

    public boolean recordStockOut(User currentUser) {
        if (currentUser == null || currentUser.getId() <= 0) {
            System.out.println("A valid logged-in user is required to record stock out.");
            return false;
        }
        return recordStockOut(currentUser, false);
    }

    private boolean recordStockOut(User currentUser, boolean promptForUser) {
        CliViewUtility.showHeader("Record Stock Out");

        Inventory inventory = selectInventory();
        if (inventory == null) {
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

        int quantityDispatched = InputUtility.readInt(scanner, "Quantity dispatched (0 to cancel): ");
        if (quantityDispatched == 0) {
            return false;
        }

        CliViewUtility.showHeader("Dispatch Date and Time");
        LocalDateTime datetimeDispatched = InputUtility.readDateTimeSelection(scanner);
        if (datetimeDispatched == null) {
            return false;
        }

        Dispatch dispatch = new Dispatch(
                inventory, datetimeDispatched, quantityDispatched, userId);
        boolean isSuccess = dispatchController.handleRecordStockOut(dispatch);
        System.out.println(isSuccess
                ? "Stock-out recorded and inventory quantity decreased successfully."
                : "Failed to record stock-out. No changes were committed.");

        if (isSuccess) {
            if (!promptForUser) {
                System.out.println("Recorded by: " + currentUser.getUsername());
            }
            Dispatch recorded = dispatchController.handleFindDispatchById(dispatch.getId());
            if (recorded != null) {
                System.out.println();
                CliViewUtility.browsePages(
                        List.of(recorded), scanner, this::printDispatches);
            }
        }
        return true;
    }

    private void viewDispatchHistory() {
        String sortBy = "date";
        int currentPage = 0;

        while (true) {
            List<Dispatch> dispatches = dispatchController.handleViewDispatchHistory(sortBy);
            currentPage = CliViewUtility.normalizePage(currentPage, dispatches.size());
            CliViewUtility.showHeader("Dispatch History");
            printDispatches(CliViewUtility.page(dispatches, currentPage));
            CliViewUtility.printPagination(currentPage, dispatches.size());
            System.out.println("Sort by: [1] Date  [2] ID  [3] Product  [4] User  [0] Back");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1" -> { sortBy = "date"; currentPage = 0; }
                case "2" -> { sortBy = "id"; currentPage = 0; }
                case "3" -> { sortBy = "product"; currentPage = 0; }
                case "4" -> { sortBy = "user"; currentPage = 0; }
                case "P" -> currentPage = CliViewUtility.previousPage(currentPage);
                case "N" -> currentPage = CliViewUtility.nextPage(currentPage, dispatches.size());
                case "0" -> { return; }
                default -> System.out.println("Invalid sort selection.");
            }
        }
    }

    private boolean findDispatchById() {
        CliViewUtility.showHeader("Find Dispatch By ID");
        int id = InputUtility.readInt(scanner, "Dispatch ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Dispatch dispatch = dispatchController.handleFindDispatchById(id);
        if (dispatch != null) {
            CliViewUtility.browsePages(List.of(dispatch), scanner, this::printDispatches);
        }
        return true;
    }

    private boolean viewDispatchesByInventory() {
        CliViewUtility.showHeader("Dispatches By Inventory");
        CliViewUtility.browsePages(
                inventoryController.handleViewAllInventory("id"),
                scanner, this::printInventoryChoices);
        int inventoryId = InputUtility.readInt(scanner, "Inventory ID (0 to cancel): ");
        if (inventoryId == 0) {
            return false;
        }

        CliViewUtility.browsePages(
                dispatchController.handleViewDispatchesByInventory(inventoryId),
                scanner, this::printDispatches);
        return true;
    }

    private boolean viewDispatchesByProduct() {
        CliViewUtility.showHeader("Dispatches By Product");
        List<Product> products = new ArrayList<>(productController.handleViewAllProducts("id"));
        products.addAll(productController.handleViewArchivedProducts("id"));
        CliViewUtility.browsePages(products, scanner, this::printProductChoices);

        int productId = InputUtility.readInt(scanner, "Product ID (0 to cancel): ");
        if (productId == 0) {
            return false;
        }

        CliViewUtility.browsePages(
                dispatchController.handleViewDispatchesByProduct(productId),
                scanner, this::printDispatches);
        return true;
    }

    private boolean viewDispatchesByUser() {
        CliViewUtility.showHeader("Dispatches By User");
        Map<Integer, String> users = dispatchController.handleGetAvailableUsers();
        CliViewUtility.browsePages(
                new ArrayList<>(users.entrySet()), scanner, this::printUserChoices);
        int userId = InputUtility.readInt(scanner, "User ID (0 to cancel): ");
        if (userId == 0) {
            return false;
        }

        CliViewUtility.browsePages(
                dispatchController.handleViewDispatchesByUser(userId),
                scanner, this::printDispatches);
        return true;
    }

    private boolean viewDispatchesByDateRange() {
        CliViewUtility.showHeader("Dispatches By Date / Date Range");
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

        CliViewUtility.browsePages(
                dispatchController.handleViewDispatchesByDateRange(startDate, endDate),
                scanner, this::printDispatches);
        return true;
    }

    private Inventory selectInventory() {
        List<Inventory> inventory = inventoryController.handleViewAllInventory("id");
        if (inventory.isEmpty()) {
            System.out.println("No inventory records are available.");
            return null;
        }

        CliViewUtility.browsePages(
                inventory, scanner, inventoryView::printStockMovementInventory);
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

    private Integer selectUser() {
        Map<Integer, String> users = dispatchController.handleGetAvailableUsers();
        if (users.isEmpty()) {
            System.out.println("No users are available.");
            return null;
        }

        CliViewUtility.browsePages(
                new ArrayList<>(users.entrySet()), scanner, this::printUserChoices);
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

    private void printDispatches(List<Dispatch> dispatches) {
        String border = "+" + "-".repeat(6) + "+" + "-".repeat(22)
                + "+" + "-".repeat(16) + "+" + "-".repeat(16)
                + "+" + "-".repeat(18) + "+" + "-".repeat(12)
                + "+" + "-".repeat(11) + "+";
        String rowFormat = "| %-4s | %-20s | %-14s | %-14s | %-16s | %-10s | %-9s |%n";

        System.out.println(border);
        System.out.printf(rowFormat, "ID", "Product", "Batch Code", "User",
                "Dispatched", "Quantity", "Remaining");
        System.out.println(border);
        if (dispatches.isEmpty()) {
            System.out.println("No dispatch records found.");
            System.out.println(border);
            return;
        }
        for (Dispatch dispatch : dispatches) {
            System.out.printf(rowFormat,
                    dispatch.getId(),
                    CliViewUtility.truncate(dispatch.getInventory().getProduct().getName(), 20),
                    CliViewUtility.truncate(dispatch.getInventory().getBatchCode(), 14),
                    CliViewUtility.truncate(dispatch.getUsername(), 14),
                    dispatch.getDatetimeDispatched().format(DATE_TIME_DISPLAY),
                    dispatch.getQuantityDispatched(),
                    dispatch.getInventory().getQuantity());
        }
        System.out.println(border);
        System.out.println("Remaining shows the inventory batch's current quantity.");
    }

    private void printInventoryChoices(List<Inventory> inventory) {
        System.out.printf("%-6s %-22s %-16s %-10s%n", "ID", "Product", "Batch Code", "Stock");
        if (inventory.isEmpty()) {
            System.out.println("No inventory records found.");
            return;
        }

        for (Inventory item : inventory) {
            System.out.printf("%-6d %-22s %-16s %-10d%n",
                    item.getId(),
                    CliViewUtility.truncate(item.getProduct().getName(), 22),
                    CliViewUtility.truncate(item.getBatchCode(), 16),
                    item.getQuantity());
        }
    }

    private void printProductChoices(List<Product> products) {
        System.out.printf("%-6s %-28s %-10s%n", "ID", "Product", "Status");
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        for (Product product : products) {
            System.out.printf("%-6d %-28s %-10s%n",
                    product.getId(), CliViewUtility.truncate(product.getName(), 28),
                    CliViewUtility.formatArchiveStatus(product.isArchived()));
        }
    }

    private void printUserChoices(List<Map.Entry<Integer, String>> users) {
        System.out.printf("%-6s %-30s%n", "ID", "Username");
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        users.forEach(user -> System.out.printf("%-6d %-30s%n",
                user.getKey(), CliViewUtility.truncate(user.getValue(), 30)));
    }

}
