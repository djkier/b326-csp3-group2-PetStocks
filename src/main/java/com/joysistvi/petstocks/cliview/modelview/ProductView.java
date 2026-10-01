package com.joysistvi.petstocks.cliview.modelview;

import com.joysistvi.petstocks.controller.CategoryController;
import com.joysistvi.petstocks.controller.ProductController;
import com.joysistvi.petstocks.model.Category;
import com.joysistvi.petstocks.model.Product;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.util.List;
import java.util.Scanner;

public class ProductView {
    private static final int NAME_DISPLAY_WIDTH = 25;
    private static final int BRAND_DISPLAY_WIDTH = 20;
    private static final int DESCRIPTION_DISPLAY_WIDTH = 40;
    private static final int CATEGORY_DISPLAY_WIDTH = 25;

    private final ProductController productController;
    private final CategoryController categoryController;
    private final Scanner scanner;

    public ProductView(ProductController productController, CategoryController categoryController,
                       Scanner scanner) {
        this.productController = productController;
        this.categoryController = categoryController;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> viewAllProducts();
                case 2 -> CliViewUtility.pauseAfter(scanner, findProductById());
                case 3 -> {
                    searchProducts();
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
                case 4 -> CliViewUtility.pauseAfter(scanner, createProduct());
                case 5 -> CliViewUtility.pauseAfter(scanner, updateProduct());
                case 6 -> runArchiveAndDeleteMenu();
                case 0 -> System.out.println("Returning to the development menu...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        CliViewUtility.showHeader("Product Management");
        System.out.println("1. View All Active Products");
        System.out.println("2. Find Product by ID");
        System.out.println("3. Search Products");
        System.out.println("4. Create Product");
        System.out.println("5. Update Product");
        System.out.println("6. Archiving / Deleting Products");
        System.out.println("0. Back");
    }

    private void viewAllProducts() {
        String sortBy = "id";
        int choice;

        do {
            CliViewUtility.showHeader("Active Products");
            printProducts(productController.handleViewAllProducts(sortBy));
            printSortOptions();
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> sortBy = "id";
                case 2 -> sortBy = "name";
                case 0 -> { }
                default -> System.out.println("Invalid sort selection.");
            }
        } while (choice != 0);
    }

    private boolean findProductById() {
        CliViewUtility.showHeader("Find Product By ID");
        int id = InputUtility.readInt(scanner, "Product ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Product product = productController.handleGetProductById(id);
        if (product != null) {
            printProducts(List.of(product));
        }
        return true;
    }

    private void searchProducts() {
        CliViewUtility.showHeader("Search Products");
        System.out.print("Enter product name, brand, description, or category: ");
        String keyword = scanner.nextLine();
        printProducts(productController.searchProducts(keyword));
    }

    private boolean createProduct() {
        Product product = promptForNewProduct("Create Product", false);
        if (product == null) {
            return false;
        }

        boolean isSuccess = saveNewProduct(product);

        if (isSuccess) {
            System.out.println();
            printProducts(productController.handleViewAllProducts("id"));
        }
        return true;
    }

    public Product createProductForStockIn() {
        while (true) {
            Product product = promptForNewProduct("Add New Product", true);
            if (product == null) {
                return null;
            }
            if (saveNewProduct(product)) {
                return product;
            }
            if (!promptToRetry("Product creation failed.")) {
                return null;
            }
        }
    }

    private Product promptForNewProduct(String title, boolean allowImmediateCancel) {
        CliViewUtility.showHeader(title);
        System.out.print(allowImmediateCancel ? "Name (0 to cancel): " : "Name: ");
        String name = scanner.nextLine();
        if (allowImmediateCancel && "0".equals(name.trim())) {
            return null;
        }

        System.out.print("Brand: ");
        String brand = scanner.nextLine();
        System.out.print("Description (optional): ");
        String description = scanner.nextLine();

        Category category = selectActiveCategory(null);
        return category == null ? null : new Product(name, brand, description, category);
    }

    private boolean saveNewProduct(Product product) {
        boolean isSuccess = productController.handleCreateProduct(product);
        System.out.println(isSuccess
                ? "Product created successfully."
                : "Failed to create product.");
        return isSuccess;
    }

    private boolean promptToRetry(String message) {
        while (true) {
            System.out.println(message);
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

    private boolean updateProduct() {
        CliViewUtility.showHeader("Update Product");
        printProducts(productController.handleViewAllProducts("id"));

        int id = InputUtility.readInt(scanner, "Product ID to update (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Product current = productController.handleGetProductById(id);
        if (current == null) {
            return true;
        }
        if (current.isArchived()) {
            System.out.println("Restore this product before updating it.");
            return true;
        }

        System.out.print("New name [" + current.getName() + "] (Enter to keep): ");
        String name = CliViewUtility.keepCurrentIfBlank(scanner.nextLine(), current.getName());

        System.out.print("New brand [" + current.getBrand() + "] (Enter to keep): ");
        String brand = CliViewUtility.keepCurrentIfBlank(scanner.nextLine(), current.getBrand());

        String currentDescription = current.getDescription() == null
                ? "" : current.getDescription();
        System.out.print("New description [" + currentDescription + "] (Enter to keep): ");
        String description = scanner.nextLine();
        if (description.trim().isEmpty()) {
            description = current.getDescription();
        }

        Category category = selectActiveCategory(current.getCategory());
        if (category == null) {
            return false;
        }

        Product product = new Product(id, name, brand, description, category);
        boolean isSuccess = productController.handleUpdateProduct(product);
        System.out.println(isSuccess
                ? "Product updated successfully."
                : "Failed to update product.");

        if (isSuccess) {
            System.out.println();
            printProducts(productController.handleViewAllProducts("id"));
        }
        return true;
    }

    private boolean archiveProduct() {
        CliViewUtility.showHeader("Archive Product");
        printProducts(productController.handleViewAllProducts("id"));
        int id = InputUtility.readInt(scanner, "Product ID to archive (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        boolean isSuccess = productController.handleArchiveProduct(id);
        System.out.println(isSuccess
                ? "Product archived successfully."
                : "Failed to archive product. Check that the ID is active.");
        return true;
    }

    private boolean restoreProduct() {
        CliViewUtility.showHeader("Restore Product");
        printProducts(productController.handleViewArchivedProducts("id"));
        int id = InputUtility.readInt(scanner, "Product ID to restore (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        boolean isSuccess = productController.handleRestoreProduct(id);
        System.out.println(isSuccess
                ? "Product restored successfully."
                : "Failed to restore product. Check the product and category status.");
        return true;
    }

    private void viewAllArchivedProducts() {
        String sortBy = "id";
        int choice;

        do {
            CliViewUtility.showHeader("Archived Products");
            printProducts(productController.handleViewArchivedProducts(sortBy));
            printSortOptions();
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> sortBy = "id";
                case 2 -> sortBy = "name";
                case 0 -> { }
                default -> System.out.println("Invalid sort selection.");
            }
        } while (choice != 0);
    }

    private boolean deleteProduct() {
        CliViewUtility.showHeader("Delete Archived Product");
        printProducts(productController.handleViewArchivedProducts("id"));
        int id = InputUtility.readInt(scanner, "Archived product ID to delete permanently (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        if (!CliViewUtility.confirmExact(scanner, "This cannot be undone. Type DELETE to confirm: ", "DELETE")) {
            System.out.println("Delete cancelled.");
            return true;
        }

        boolean isSuccess = productController.handleDeleteProduct(id);
        System.out.println(isSuccess
                ? "Product deleted successfully."
                : "Failed to delete product. It must be archived and have no dependent records.");
        return true;
    }

    private void runArchiveAndDeleteMenu() {
        int choice;

        do {
            printArchiveAndDeleteMenu();
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> CliViewUtility.pauseAfter(scanner, archiveProduct());
                case 2 -> CliViewUtility.pauseAfter(scanner, restoreProduct());
                case 3 -> viewAllArchivedProducts();
                case 4 -> CliViewUtility.pauseAfter(scanner, deleteProduct());
                case 0 -> { }
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printArchiveAndDeleteMenu() {
        CliViewUtility.showHeader("Archiving / Deleting Products");
        System.out.println("1. Archive Product");
        System.out.println("2. Restore Product");
        System.out.println("3. View Archived Products");
        System.out.println("4. Delete Archived Product");
        System.out.println("0. Back");
    }

    private Category selectActiveCategory(Category currentCategory) {
        List<Category> categories = categoryController.handleViewAllCategories("id");
        if (categories.isEmpty()) {
            System.out.println("No active categories are available.");
            return null;
        }

        printCategoryChoices(categories);

        while (true) {
            if (currentCategory == null) {
                System.out.print("Category ID (0 to cancel): ");
            } else {
                System.out.print("Category ID [" + currentCategory.getName() +
                        "] (Enter to keep, 0 to cancel): ");
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty() && currentCategory != null) {
                Category activeCurrent = findCategory(categories, currentCategory.getId());
                if (activeCurrent != null) {
                    return activeCurrent;
                }
                System.out.println("The current category is archived. Choose an active category.");
                continue;
            }

            try {
                int categoryId = Integer.parseInt(input);
                if (categoryId == 0) {
                    return null;
                }

                Category selected = findCategory(categories, categoryId);
                if (selected != null) {
                    return selected;
                }
                System.out.println("Choose an ID from the active category list.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private Category findCategory(List<Category> categories, int categoryId) {
        for (Category category : categories) {
            if (category.getId() == categoryId) {
                return category;
            }
        }
        return null;
    }

    private void printCategoryChoices(List<Category> categories) {
        System.out.println();
        System.out.println("Active Categories");
        String border = "+" + "-".repeat(6) + "+" + "-".repeat(37) + "+";
        System.out.println(border);
        System.out.printf("| %-4s | %-35s |%n", "ID", "Name");
        System.out.println(border);
        for (Category category : categories) {
            System.out.printf("| %-4d | %-35s |%n",
                    category.getId(), CliViewUtility.truncate(category.getName(), 35));
        }
        System.out.println(border);
    }

    private void printSortOptions() {
        System.out.println();
        System.out.println("Sort by: [1] ID        [2] Name        [0] Back");
    }

    public void printProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(NAME_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(BRAND_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(DESCRIPTION_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(CATEGORY_DISPLAY_WIDTH + 2) + "+" + "-".repeat(12) + "+";

        System.out.println(border);
        System.out.printf("| %-4s | %-25s | %-20s | %-40s | %-25s | %-10s |%n",
                "ID", "Name", "Brand", "Description", "Category", "Status");
        System.out.println(border);

        for (Product product : products) {
            String description = product.getDescription() == null ? "" : product.getDescription();
            String categoryName = product.getCategory() == null ? "" : product.getCategory().getName();
            String status = CliViewUtility.formatArchiveStatus(product.isArchived());
            System.out.printf("| %-4d | %-25s | %-20s | %-40s | %-25s | %-10s |%n",
                    product.getId(),
                    CliViewUtility.truncate(product.getName(), NAME_DISPLAY_WIDTH),
                    CliViewUtility.truncate(product.getBrand(), BRAND_DISPLAY_WIDTH),
                    CliViewUtility.truncate(description, DESCRIPTION_DISPLAY_WIDTH),
                    CliViewUtility.truncate(categoryName, CATEGORY_DISPLAY_WIDTH),
                    status);
        }

        System.out.println(border);
    }

}
