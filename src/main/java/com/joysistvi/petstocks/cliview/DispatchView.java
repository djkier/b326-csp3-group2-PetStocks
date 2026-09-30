package com.joysistvi.petstocks.cliview;

import com.joysistvi.petstocks.controller.DispatchController;
import com.joysistvi.petstocks.controller.InventoryController;
import com.joysistvi.petstocks.controller.ProductController;
import com.joysistvi.petstocks.model.Dispatch;
import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.model.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DispatchView {
    private static final DateTimeFormatter DATE_TIME_INPUT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_TIME_DISPLAY =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final DispatchController dispatchController;
    private final InventoryController inventoryController;
    private final ProductController productController;
    private final Scanner scanner;

    public DispatchView(DispatchController dispatchController,
                        InventoryController inventoryController,
                        ProductController productController, Scanner scanner) {
        this.dispatchController = dispatchController;
        this.inventoryController = inventoryController;
        this.productController = productController;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> pauseAfter(recordStockOut());
                case 2 -> viewDispatchHistory();
                case 3 -> pauseAfter(findDispatchById());
                case 4 -> pauseAfter(viewDispatchesByInventory());
                case 5 -> pauseAfter(viewDispatchesByProduct());
                case 6 -> pauseAfter(viewDispatchesByUser());
                case 7 -> pauseAfter(viewDispatchesByDateRange());
                case 0 -> System.out.println("Returning to the development menu...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    pressEnterToContinue();
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        showHeader("Dispatch / Stock-Out Management");
        System.out.println("1. Record Stock Out");
        System.out.println("2. View Dispatch History");
        System.out.println("3. Find Dispatch by ID");
        System.out.println("4. View Dispatches by Inventory");
        System.out.println("5. View Dispatches by Product");
        System.out.println("6. View Dispatches by User");
        System.out.println("7. View Dispatches by Date / Date Range");
        System.out.println("0. Back");
    }

    private boolean recordStockOut() {
        showHeader("Record Stock Out");

        Inventory inventory = selectInventory();
        if (inventory == null) {
            return false;
        }

        Integer userId = selectUser();
        if (userId == null) {
            return false;
        }

        int quantityDispatched = promptInt("Quantity dispatched (0 to cancel): ");
        if (quantityDispatched == 0) {
            return false;
        }

        DateTimeInput dispatchInput = promptDispatchDateTime();
        if (dispatchInput.cancelled()) {
            return false;
        }

        Dispatch dispatch = new Dispatch(
                inventory, dispatchInput.value(), quantityDispatched, userId);
        boolean isSuccess = dispatchController.handleRecordStockOut(dispatch);
        System.out.println(isSuccess
                ? "Stock-out recorded and inventory quantity decreased successfully."
                : "Failed to record stock-out. No changes were committed.");

        if (isSuccess) {
            Dispatch recorded = dispatchController.handleFindDispatchById(dispatch.getId());
            if (recorded != null) {
                System.out.println();
                printDispatches(List.of(recorded));
            }
        }
        return true;
    }

    private void viewDispatchHistory() {
        String sortBy = "date";
        int choice;

        do {
            showHeader("Dispatch History");
            printDispatches(dispatchController.handleViewDispatchHistory(sortBy));
            System.out.println();
            System.out.println("Sort by: [1] Date  [2] ID  [3] Product  [4] User  [0] Back");
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> sortBy = "date";
                case 2 -> sortBy = "id";
                case 3 -> sortBy = "product";
                case 4 -> sortBy = "user";
                case 0 -> { }
                default -> System.out.println("Invalid sort selection.");
            }
        } while (choice != 0);
    }

    private boolean findDispatchById() {
        showHeader("Find Dispatch By ID");
        int id = promptInt("Dispatch ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Dispatch dispatch = dispatchController.handleFindDispatchById(id);
        if (dispatch != null) {
            printDispatches(List.of(dispatch));
        }
        return true;
    }

    private boolean viewDispatchesByInventory() {
        showHeader("Dispatches By Inventory");
        printInventoryChoices(inventoryController.handleViewAllInventory("id"));
        int inventoryId = promptInt("Inventory ID (0 to cancel): ");
        if (inventoryId == 0) {
            return false;
        }

        printDispatches(dispatchController.handleViewDispatchesByInventory(inventoryId));
        return true;
    }

    private boolean viewDispatchesByProduct() {
        showHeader("Dispatches By Product");
        List<Product> products = new ArrayList<>(productController.handleViewAllProducts("id"));
        products.addAll(productController.handleViewArchivedProducts("id"));
        printProductChoices(products);

        int productId = promptInt("Product ID (0 to cancel): ");
        if (productId == 0) {
            return false;
        }

        printDispatches(dispatchController.handleViewDispatchesByProduct(productId));
        return true;
    }

    private boolean viewDispatchesByUser() {
        showHeader("Dispatches By User");
        Map<Integer, String> users = dispatchController.handleGetAvailableUsers();
        printUserChoices(users);
        int userId = promptInt("User ID (0 to cancel): ");
        if (userId == 0) {
            return false;
        }

        printDispatches(dispatchController.handleViewDispatchesByUser(userId));
        return true;
    }

    private boolean viewDispatchesByDateRange() {
        showHeader("Dispatches By Date / Date Range");
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

        printDispatches(dispatchController.handleViewDispatchesByDateRange(startDate, endDate));
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

    private Integer selectUser() {
        Map<Integer, String> users = dispatchController.handleGetAvailableUsers();
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

    private void printDispatches(List<Dispatch> dispatches) {
        if (dispatches.isEmpty()) {
            System.out.println("No dispatch records found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(22)
                + "+" + "-".repeat(16) + "+" + "-".repeat(16)
                + "+" + "-".repeat(18) + "+" + "-".repeat(12)
                + "+" + "-".repeat(11) + "+";
        String rowFormat = "| %-4s | %-20s | %-14s | %-14s | %-16s | %-10s | %-9s |%n";

        System.out.println(border);
        System.out.printf(rowFormat, "ID", "Product", "Batch Code", "User",
                "Dispatched", "Quantity", "Remaining");
        System.out.println(border);
        for (Dispatch dispatch : dispatches) {
            System.out.printf(rowFormat,
                    dispatch.getId(),
                    truncate(dispatch.getInventory().getProduct().getName(), 20),
                    truncate(dispatch.getInventory().getBatchCode(), 14),
                    truncate(dispatch.getUsername(), 14),
                    dispatch.getDatetimeDispatched().format(DATE_TIME_DISPLAY),
                    dispatch.getQuantityDispatched(),
                    dispatch.getInventory().getQuantity());
        }
        System.out.println(border);
        System.out.println("Remaining shows the inventory batch's current quantity.");
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

    private void printProductChoices(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        System.out.printf("%-6s %-28s %-10s%n", "ID", "Product", "Status");
        for (Product product : products) {
            System.out.printf("%-6d %-28s %-10s%n",
                    product.getId(), truncate(product.getName(), 28),
                    product.isArchived() ? "Archived" : "Active");
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

    private DateTimeInput promptDispatchDateTime() {
        while (true) {
            System.out.print("Dispatch date/time (YYYY-MM-DD HH:mm, Enter for now, 0 to cancel): ");
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
