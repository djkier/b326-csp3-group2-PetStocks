package com.joysistvi.petstocks.cliview;

import com.joysistvi.petstocks.controller.InventoryController;
import com.joysistvi.petstocks.controller.ProductController;
import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.model.Product;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class InventoryView {
    private static final int PRODUCT_DISPLAY_WIDTH = 25;
    private static final int BRAND_DISPLAY_WIDTH = 18;
    private static final int CATEGORY_DISPLAY_WIDTH = 20;
    private static final int BATCH_DISPLAY_WIDTH = 20;
    private static final int REMARK_DISPLAY_WIDTH = 30;

    private final InventoryController inventoryController;
    private final ProductController productController;
    private final Scanner scanner;

    public InventoryView(InventoryController inventoryController,
                         ProductController productController, Scanner scanner) {
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
                case 1 -> viewAllInventory();
                case 2 -> pauseAfter(findInventoryById());
                case 3 -> pauseAfter(viewInventoryByProduct());
                case 4 -> {
                    searchInventory();
                    pressEnterToContinue();
                }
                case 5 -> pauseAfter(viewLowStockInventory());
                case 6 -> pauseAfter(viewExpiringInventory());
                case 7 -> {
                    viewInventoryByBatchCode();
                    pressEnterToContinue();
                }
                case 8 -> pauseAfter(createInventory());
                case 9 -> pauseAfter(updateInventory());
                case 0 -> System.out.println("Returning to the development menu...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    pressEnterToContinue();
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        showHeader("Inventory Management");
        System.out.println("1. View All Inventory");
        System.out.println("2. Find Inventory by ID");
        System.out.println("3. View Inventory by Product");
        System.out.println("4. Search Inventory");
        System.out.println("5. View Low-Stock Inventory");
        System.out.println("6. View Expiring Inventory");
        System.out.println("7. View Inventory by Batch Code");
        System.out.println("8. Create Inventory Record (Testing)");
        System.out.println("9. Update Inventory Record (Testing)");
        System.out.println("0. Back");
    }

    private void viewAllInventory() {
        String sortBy = "id";
        int choice;

        do {
            showHeader("All Inventory");
            printInventory(inventoryController.handleViewAllInventory(sortBy));
            printSortOptions();
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> sortBy = "id";
                case 2 -> sortBy = "product";
                case 3 -> sortBy = "quantity";
                case 4 -> sortBy = "expiration";
                case 0 -> { }
                default -> System.out.println("Invalid sort selection.");
            }
        } while (choice != 0);
    }

    private boolean findInventoryById() {
        showHeader("Find Inventory By ID");
        int id = promptInt("Inventory ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Inventory inventory = inventoryController.handleGetInventoryById(id);
        if (inventory != null) {
            printInventory(List.of(inventory));
        }
        return true;
    }

    private boolean viewInventoryByProduct() {
        showHeader("Inventory By Product");
        int productId = promptInt("Product ID (0 to cancel): ");
        if (productId == 0) {
            return false;
        }

        printInventory(inventoryController.handleViewInventoryByProductId(productId));
        return true;
    }

    private void searchInventory() {
        showHeader("Search Inventory");
        System.out.print("Enter product, brand, batch code, or remark: ");
        String keyword = scanner.nextLine();
        printInventory(inventoryController.searchInventory(keyword));
    }

    private boolean viewLowStockInventory() {
        showHeader("Low-Stock Inventory");
        int maximumQuantity = promptInt(
                "Maximum batch quantity (0 includes out-of-stock, -1 to cancel): ");
        if (maximumQuantity == -1) {
            return false;
        }

        printInventory(inventoryController.handleViewLowStockInventory(maximumQuantity));
        return true;
    }

    private boolean viewExpiringInventory() {
        showHeader("Expiring Inventory");
        int daysAhead = promptInt("Show batches expiring within how many days? (-1 to cancel): ");
        if (daysAhead == -1) {
            return false;
        }

        printInventory(inventoryController.handleViewExpiringInventory(daysAhead));
        return true;
    }

    private void viewInventoryByBatchCode() {
        showHeader("Inventory By Batch Code");
        System.out.print("Batch code: ");
        String batchCode = scanner.nextLine();
        printInventory(inventoryController.handleViewInventoryByBatchCode(batchCode));
    }

    private boolean createInventory() {
        showHeader("Create Inventory Record (Testing)");
        Product product = selectActiveProduct(null);
        if (product == null) {
            return false;
        }

        int quantity = promptInt("Quantity: ");
        DateInput expirationInput = promptExpiration(
                "Expiration (YYYY-MM-DD, Enter for N/A, 0 to cancel): ", null, false);
        if (expirationInput.cancelled()) {
            return false;
        }
        LocalDate expiration = expirationInput.expiration();

        System.out.print("Batch code: ");
        String batchCode = scanner.nextLine();
        System.out.print("Remark (optional): ");
        String remark = scanner.nextLine();

        Inventory inventory = new Inventory(product, quantity, expiration, batchCode, remark);
        boolean isSuccess = inventoryController.handleCreateInventory(inventory);
        System.out.println(isSuccess
                ? "Inventory record created successfully."
                : "Failed to create inventory record.");

        if (isSuccess) {
            System.out.println();
            printInventory(inventoryController.handleViewAllInventory("id"));
        }
        return true;
    }

    private boolean updateInventory() {
        showHeader("Update Inventory Record (Testing)");
        printInventory(inventoryController.handleViewAllInventory("id"));

        int id = promptInt("Inventory ID to update (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Inventory current = inventoryController.handleGetInventoryById(id);
        if (current == null) {
            return true;
        }

        Product product = selectActiveProduct(current.getProduct());
        if (product == null) {
            return false;
        }

        int quantity = promptOptionalInt(
                "New quantity [" + current.getQuantity() + "] (Enter to keep): ",
                current.getQuantity());
        DateInput expirationInput = promptExpiration(
                "New expiration [" + formatExpiration(current.getExpiration()) +
                        "] (Enter for N/A, KEEP to retain, 0 to cancel): ",
                current.getExpiration(), true);
        if (expirationInput.cancelled()) {
            return false;
        }
        LocalDate expiration = expirationInput.expiration();

        System.out.print("New batch code [" + current.getBatchCode() + "] (Enter to keep): ");
        String batchCode = keepCurrentIfBlank(scanner.nextLine(), current.getBatchCode());

        String currentRemark = current.getRemark() == null ? "" : current.getRemark();
        System.out.print("New remark [" + currentRemark +
                "] (Enter to keep, type NONE to clear): ");
        String remarkInput = scanner.nextLine();
        String remark = "NONE".equalsIgnoreCase(remarkInput.trim())
                ? null : keepCurrentIfBlank(remarkInput, current.getRemark());

        Inventory inventory = new Inventory(
                id, product, quantity, expiration, batchCode, remark);
        boolean isSuccess = inventoryController.handleUpdateInventory(inventory);
        System.out.println(isSuccess
                ? "Inventory record updated successfully."
                : "Failed to update inventory record.");

        if (isSuccess) {
            System.out.println();
            Inventory updated = inventoryController.handleGetInventoryById(id);
            if (updated != null) {
                printInventory(List.of(updated));
            }
        }
        return true;
    }

    private Product selectActiveProduct(Product currentProduct) {
        List<Product> products = productController.handleViewAllProducts("id");
        if (products.isEmpty()) {
            System.out.println("No active products are available.");
            return null;
        }

        printProductChoices(products);
        while (true) {
            if (currentProduct == null) {
                System.out.print("Product ID (0 to cancel): ");
            } else {
                System.out.print("Product ID [" + currentProduct.getName() +
                        "] (Enter to keep, 0 to cancel): ");
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty() && currentProduct != null) {
                Product activeCurrent = findProduct(products, currentProduct.getId());
                if (activeCurrent != null) {
                    return activeCurrent;
                }
                System.out.println("The current product is archived. Choose an active product.");
                continue;
            }

            try {
                int productId = Integer.parseInt(input);
                if (productId == 0) {
                    return null;
                }

                Product selected = findProduct(products, productId);
                if (selected != null) {
                    return selected;
                }
                System.out.println("Choose an ID from the active product list.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private Product findProduct(List<Product> products, int productId) {
        for (Product product : products) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }

    private void printProductChoices(List<Product> products) {
        System.out.println("Active Products");
        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27)
                + "+" + "-".repeat(22) + "+" + "-".repeat(27) + "+";
        System.out.println(border);
        System.out.printf("| %-4s | %-25s | %-20s | %-25s |%n",
                "ID", "Product", "Brand", "Category");
        System.out.println(border);
        for (Product product : products) {
            System.out.printf("| %-4d | %-25s | %-20s | %-25s |%n",
                    product.getId(),
                    truncate(product.getName(), 25),
                    truncate(product.getBrand(), 20),
                    truncate(product.getCategory().getName(), 25));
        }
        System.out.println(border);
    }

    public void printInventory(List<Inventory> inventory) {
        if (inventory.isEmpty()) {
            System.out.println("No inventory records found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(PRODUCT_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(BRAND_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(CATEGORY_DISPLAY_WIDTH + 2) + "+" + "-".repeat(10)
                + "+" + "-".repeat(12) + "+" + "-".repeat(BATCH_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(REMARK_DISPLAY_WIDTH + 2) + "+" + "-".repeat(12) + "+";

        System.out.println(border);
        System.out.printf("| %-4s | %-25s | %-18s | %-20s | %-8s | %-10s | %-20s | %-30s | %-10s |%n",
                "ID", "Product", "Brand", "Category", "Quantity", "Expiration",
                "Batch Code", "Remark", "Product");
        System.out.printf("| %-4s | %-25s | %-18s | %-20s | %-8s | %-10s | %-20s | %-30s | %-10s |%n",
                "", "", "", "", "", "", "", "", "Status");
        System.out.println(border);

        for (Inventory item : inventory) {
            Product product = item.getProduct();
            String remark = item.getRemark() == null ? "" : item.getRemark();
            String expiration = formatExpiration(item.getExpiration());
            System.out.printf("| %-4d | %-25s | %-18s | %-20s | %-8d | %-10s | %-20s | %-30s | %-10s |%n",
                    item.getId(),
                    truncate(product.getName(), PRODUCT_DISPLAY_WIDTH),
                    truncate(product.getBrand(), BRAND_DISPLAY_WIDTH),
                    truncate(product.getCategory().getName(), CATEGORY_DISPLAY_WIDTH),
                    item.getQuantity(),
                    expiration,
                    truncate(item.getBatchCode(), BATCH_DISPLAY_WIDTH),
                    truncate(remark, REMARK_DISPLAY_WIDTH),
                    product.isArchived() ? "Archived" : "Active");
        }
        System.out.println(border);
    }

    private void printSortOptions() {
        System.out.println();
        System.out.println("Sort by: [1] ID    [2] Product    [3] Quantity    [4] Expiration    [0] Back");
    }

    private DateInput promptExpiration(String prompt, LocalDate currentValue,
                                       boolean allowKeep) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if ("0".equals(input)) {
                return new DateInput(null, true);
            }
            if (input.isEmpty() || "N/A".equalsIgnoreCase(input)
                    || "NONE".equalsIgnoreCase(input)) {
                return new DateInput(null, false);
            }
            if (allowKeep && "KEEP".equalsIgnoreCase(input)) {
                return new DateInput(currentValue, false);
            }

            try {
                return new DateInput(LocalDate.parse(input), false);
            } catch (DateTimeParseException e) {
                String options = allowKeep ? ", KEEP, or 0" : " or 0";
                System.out.println("Enter a valid date in YYYY-MM-DD format, leave blank for N/A" +
                        options + ".");
            }
        }
    }

    private String formatExpiration(LocalDate expiration) {
        return expiration == null ? "N/A" : expiration.toString();
    }

    private record DateInput(LocalDate expiration, boolean cancelled) {
    }

    private int promptOptionalInt(String prompt, int currentValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return currentValue;
            }

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number or press Enter to keep.");
            }
        }
    }

    private String keepCurrentIfBlank(String value, String currentValue) {
        return value.trim().isEmpty() ? currentValue : value;
    }

    private void pauseAfter(boolean shouldPause) {
        if (shouldPause) {
            pressEnterToContinue();
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

    private String truncate(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}
