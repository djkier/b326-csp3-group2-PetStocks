package com.joysistvi.petstocks.cliview.modelview;

import com.joysistvi.petstocks.controller.InventoryController;
import com.joysistvi.petstocks.controller.ProductController;
import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.model.Product;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class InventoryView {
    private static final int ID_DISPLAY_WIDTH = 4;
    private static final int PRODUCT_DISPLAY_WIDTH = 20;
    private static final int BRAND_DISPLAY_WIDTH = 12;
    private static final int CATEGORY_DISPLAY_WIDTH = 18;
    private static final int QUANTITY_DISPLAY_WIDTH = 8;
    private static final int EXPIRATION_DISPLAY_WIDTH = 12;
    private static final int BATCH_DISPLAY_WIDTH = 14;
    private static final int REMARK_DISPLAY_WIDTH = 20;
    private static final int STATUS_DISPLAY_WIDTH = 10;

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
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> viewAllInventory();
                case 2 -> CliViewUtility.pauseAfter(scanner, findInventoryById());
                case 3 -> CliViewUtility.pauseAfter(scanner, viewInventoryByProduct());
                case 4 -> {
                    searchInventory();
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
                case 5 -> viewLowStockInventory();
                case 6 -> viewExpiringInventory();
                case 7 -> {
                    viewInventoryByBatchCode();
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
                case 8 -> CliViewUtility.pauseAfter(scanner, createInventory());
                case 9 -> CliViewUtility.pauseAfter(scanner, updateInventory());
                case 0 -> System.out.println("Returning to the development menu...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        CliViewUtility.showHeader("Inventory Management");
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

    public void viewAllInventory() {
        String sortBy = "id";
        int choice;

        do {
            CliViewUtility.showHeader("All Inventory");
            printInventory(inventoryController.handleViewAllInventory(sortBy));
            printSortOptions();
            choice = InputUtility.readInt(scanner, "Choice: ");

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

    public boolean findInventoryById() {
        CliViewUtility.showHeader("Find Inventory By ID");
        int id = InputUtility.readInt(scanner, "Inventory ID (0 to cancel): ");
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
        CliViewUtility.showHeader("Inventory By Product");
        int productId = InputUtility.readInt(scanner, "Product ID (0 to cancel): ");
        if (productId == 0) {
            return false;
        }

        printInventory(inventoryController.handleViewInventoryByProductId(productId));
        return true;
    }

    public void searchInventory() {
        CliViewUtility.showHeader("Search Inventory");
        System.out.print("Enter product, brand, batch code, or remark: ");
        String keyword = scanner.nextLine();
        printInventory(inventoryController.searchInventory(keyword));
    }

    public void viewLowStockInventory() {
        LowStockDisplay display = LowStockDisplay.LOW_STOCK;

        while (true) {
            switch (display) {
                case LOW_STOCK -> {
                    CliViewUtility.showHeader("Low-Stock Inventory");
                    System.out.println("Showing inventory with stock quantities below 10.");
                    printInventory(inventoryController.handleViewLowStockInventory());
                }
                case OUT_OF_STOCK -> {
                    CliViewUtility.showHeader("Out-of-Stock Inventory");
                    System.out.println("Showing out-of-stock inventory.");
                    printInventory(inventoryController.handleViewOutOfStockInventory());
                }
            }

            printLowStockOptions();
            int choice = InputUtility.readInt(scanner, "Choice: ");
            switch (choice) {
                case 1 -> display = LowStockDisplay.OUT_OF_STOCK;
                case 2 -> display = LowStockDisplay.LOW_STOCK;
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid menu selection.");
            }
        }
    }

    public void viewExpiringInventory() {
        ExpirationDisplay display = ExpirationDisplay.EXPIRING;

        while (true) {
            switch (display) {
                case EXPIRED -> {
                    CliViewUtility.showHeader("Expired Inventory");
                    printInventory(inventoryController.handleViewExpiredInventory());
                }
                case EXPIRING -> {
                    CliViewUtility.showHeader("Inventory Expiring Within 60 Days");
                    printInventory(inventoryController.handleViewExpiringInventory());
                }
            }

            printExpirationOptions();
            int choice = InputUtility.readInt(scanner, "Choice: ");
            switch (choice) {
                case 1 -> display = ExpirationDisplay.EXPIRED;
                case 2 -> display = ExpirationDisplay.EXPIRING;
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid menu selection.");
            }
        }
    }

    private void viewInventoryByBatchCode() {
        CliViewUtility.showHeader("Inventory By Batch Code");
        System.out.print("Batch code: ");
        String batchCode = scanner.nextLine();
        printInventory(inventoryController.handleViewInventoryByBatchCode(batchCode));
    }

    private boolean createInventory() {
        CliViewUtility.showHeader("Create Inventory Record (Testing)");
        Product product = selectActiveProduct(null);
        if (product == null) {
            return false;
        }

        int quantity = InputUtility.readInt(scanner, "Quantity: ");
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

    public Inventory createInitialInventory(Product product) {
        if (product == null || product.getId() <= 0) {
            System.out.println("A successfully created product is required.");
            return null;
        }

        while (true) {
            CliViewUtility.showHeader("Create Initial Inventory Batch");
            System.out.println("Product: " + product.getName());
            System.out.print("Batch code (0 to cancel): ");
            String batchCode = scanner.nextLine();
            if ("0".equals(batchCode.trim())) {
                return null;
            }

            DateInput expirationInput = promptExpiration(
                    "Expiration (YYYY-MM-DD, Enter for N/A, 0 to cancel): ", null, false);
            if (expirationInput.cancelled()) {
                return null;
            }

            System.out.print("Remark (optional): ");
            String remark = scanner.nextLine();
            Inventory inventory = new Inventory(
                    product, 0, expirationInput.expiration(), batchCode, remark);

            if (inventoryController.handleCreateInventory(inventory)) {
                System.out.println("Initial inventory batch created with quantity 0.");
                return inventory;
            }

            System.out.println("Failed to create the initial inventory batch.");
            if (!promptToRetryInventoryCreation()) {
                return null;
            }
        }
    }

    private boolean promptToRetryInventoryCreation() {
        while (true) {
            System.out.println("[1] Retry    [0] Cancel");
            int choice = InputUtility.readInt(scanner, "Choice: ");
            if (choice == 1) {
                return true;
            }
            if (choice == 0) {
                return false;
            }
            System.out.println("Invalid menu selection.");
        }
    }

    private boolean updateInventory() {
        CliViewUtility.showHeader("Update Inventory Record (Testing)");
        printInventory(inventoryController.handleViewAllInventory("id"));

        int id = InputUtility.readInt(scanner, "Inventory ID to update (0 to cancel): ");
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

        int quantity = InputUtility.readOptionalInt(scanner,
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
        String batchCode = CliViewUtility.keepCurrentIfBlank(scanner.nextLine(), current.getBatchCode());

        String currentRemark = current.getRemark() == null ? "" : current.getRemark();
        System.out.print("New remark [" + currentRemark +
                "] (Enter for none, type KEEP to retain): ");
        String remarkInput = scanner.nextLine();
        String remark = "KEEP".equalsIgnoreCase(remarkInput.trim())
                ? current.getRemark() : remarkInput;

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
                    CliViewUtility.truncate(product.getName(), 25),
                    CliViewUtility.truncate(product.getBrand(), 20),
                    CliViewUtility.truncate(product.getCategory().getName(), 25));
        }
        System.out.println(border);
    }

    public void printInventory(List<Inventory> inventory) {
        String border = "+" + "-".repeat(ID_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(PRODUCT_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(BRAND_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(CATEGORY_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(QUANTITY_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(EXPIRATION_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(BATCH_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(REMARK_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(STATUS_DISPLAY_WIDTH + 2) + "+";

        String rowFormat = "| %-" + ID_DISPLAY_WIDTH + "s | %-"
                + PRODUCT_DISPLAY_WIDTH + "s | %-" + BRAND_DISPLAY_WIDTH + "s | %-"
                + CATEGORY_DISPLAY_WIDTH + "s | %-" + QUANTITY_DISPLAY_WIDTH + "s | %-"
                + EXPIRATION_DISPLAY_WIDTH + "s | %-" + BATCH_DISPLAY_WIDTH + "s | %-"
                + REMARK_DISPLAY_WIDTH + "s | %-" + STATUS_DISPLAY_WIDTH + "s |%n";

        System.out.println(border);
        System.out.printf(rowFormat,
                "ID", "Product", "Brand", "Category", "Quantity", "Expiration",
                "Batch Code", "Remark", "Status");
        System.out.println(border);

        if (inventory.isEmpty()) {
            System.out.println("No Inventory Records Found.");
            System.out.println(border);
            return;
        }

        for (Inventory item : inventory) {
            Product product = item.getProduct();
            String expiration = formatExpiration(item.getExpiration());
            System.out.printf(rowFormat,
                    item.getId(),
                    CliViewUtility.truncate(product.getName(), PRODUCT_DISPLAY_WIDTH),
                    CliViewUtility.truncate(product.getBrand(), BRAND_DISPLAY_WIDTH),
                    CliViewUtility.truncate(product.getCategory().getName(), CATEGORY_DISPLAY_WIDTH),
                    item.getQuantity(),
                    CliViewUtility.truncate(expiration, EXPIRATION_DISPLAY_WIDTH),
                    CliViewUtility.truncate(item.getBatchCode(), BATCH_DISPLAY_WIDTH),
                    CliViewUtility.truncate(item.getRemark(), REMARK_DISPLAY_WIDTH),
                    CliViewUtility.formatArchiveStatus(product.isArchived()));
        }
        System.out.println(border);
    }

    public void printStockMovementInventory(List<Inventory> inventory) {
        String border = "+" + "-".repeat(ID_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(PRODUCT_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(BRAND_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(QUANTITY_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(BATCH_DISPLAY_WIDTH + 2) + "+";
        String rowFormat = "| %-" + ID_DISPLAY_WIDTH + "s | %-"
                + PRODUCT_DISPLAY_WIDTH + "s | %-" + BRAND_DISPLAY_WIDTH + "s | %-"
                + QUANTITY_DISPLAY_WIDTH + "s | %-" + BATCH_DISPLAY_WIDTH + "s |%n";

        System.out.println(border);
        System.out.printf(rowFormat, "ID", "Product", "Brand", "Quantity", "Batch Code");
        System.out.println(border);

        if (inventory.isEmpty()) {
            System.out.println("No Inventory Records Found.");
            System.out.println(border);
            return;
        }

        for (Inventory item : inventory) {
            Product product = item.getProduct();
            System.out.printf(rowFormat,
                    item.getId(),
                    CliViewUtility.truncate(product.getName(), PRODUCT_DISPLAY_WIDTH),
                    CliViewUtility.truncate(product.getBrand(), BRAND_DISPLAY_WIDTH),
                    item.getQuantity(),
                    CliViewUtility.truncate(item.getBatchCode(), BATCH_DISPLAY_WIDTH));
        }
        System.out.println(border);
    }

    private void printSortOptions() {
        System.out.println();
        System.out.println("Sort by: [1] ID    [2] Product    [3] Quantity    [4] Expiration    [0] Back");
    }

    private void printLowStockOptions() {
        System.out.println();
        System.out.println(
                "View: [1] Out-of-Stock        [2] View Low-Stock Inventory        [0] Back");
    }

    private void printExpirationOptions() {
        System.out.println();
        System.out.println(
                "View: [1] Expired        [2] Expiring Within 60 Days        [0] Back");
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

    private enum LowStockDisplay {
        LOW_STOCK,
        OUT_OF_STOCK
    }

    private enum ExpirationDisplay {
        EXPIRED,
        EXPIRING
    }

}
