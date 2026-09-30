package com.joysistvi.petstocks.cliview;

import com.joysistvi.petstocks.controller.CategoryController;
import com.joysistvi.petstocks.controller.ProductController;
import com.joysistvi.petstocks.model.Category;
import com.joysistvi.petstocks.model.Product;

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
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> viewAllProducts();
                case 2 -> pauseAfter(findProductById());
                case 3 -> {
                    searchProducts();
                    pressEnterToContinue();
                }
                case 4 -> pauseAfter(createProduct());
                case 5 -> pauseAfter(updateProduct());
                case 6 -> runArchiveAndDeleteMenu();
                case 0 -> System.out.println("Returning to the development menu...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    pressEnterToContinue();
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        showHeader("Product Management");
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
            showHeader("Active Products");
            printProducts(productController.handleViewAllProducts(sortBy));
            printSortOptions();
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> sortBy = "id";
                case 2 -> sortBy = "name";
                case 0 -> { }
                default -> System.out.println("Invalid sort selection.");
            }
        } while (choice != 0);
    }

    private boolean findProductById() {
        showHeader("Find Product By ID");
        int id = promptInt("Product ID (0 to cancel): ");
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
        showHeader("Search Products");
        System.out.print("Enter product name, brand, description, or category: ");
        String keyword = scanner.nextLine();
        printProducts(productController.searchProducts(keyword));
    }

    private boolean createProduct() {
        showHeader("Create Product");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Brand: ");
        String brand = scanner.nextLine();
        System.out.print("Description (optional): ");
        String description = scanner.nextLine();

        Category category = selectActiveCategory(null);
        if (category == null) {
            return false;
        }

        Product product = new Product(name, brand, description, category);
        boolean isSuccess = productController.handleCreateProduct(product);
        System.out.println(isSuccess
                ? "Product created successfully."
                : "Failed to create product.");

        if (isSuccess) {
            System.out.println();
            printProducts(productController.handleViewAllProducts("id"));
        }
        return true;
    }

    private boolean updateProduct() {
        showHeader("Update Product");
        printProducts(productController.handleViewAllProducts("id"));

        int id = promptInt("Product ID to update (0 to cancel): ");
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
        String name = keepCurrentIfBlank(scanner.nextLine(), current.getName());

        System.out.print("New brand [" + current.getBrand() + "] (Enter to keep): ");
        String brand = keepCurrentIfBlank(scanner.nextLine(), current.getBrand());

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
        showHeader("Archive Product");
        printProducts(productController.handleViewAllProducts("id"));
        int id = promptInt("Product ID to archive (0 to cancel): ");
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
        showHeader("Restore Product");
        printProducts(productController.handleViewArchivedProducts("id"));
        int id = promptInt("Product ID to restore (0 to cancel): ");
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
            showHeader("Archived Products");
            printProducts(productController.handleViewArchivedProducts(sortBy));
            printSortOptions();
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> sortBy = "id";
                case 2 -> sortBy = "name";
                case 0 -> { }
                default -> System.out.println("Invalid sort selection.");
            }
        } while (choice != 0);
    }

    private boolean deleteProduct() {
        showHeader("Delete Archived Product");
        printProducts(productController.handleViewArchivedProducts("id"));
        int id = promptInt("Archived product ID to delete permanently (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        System.out.print("This cannot be undone. Type DELETE to confirm: ");
        if (!"DELETE".equals(scanner.nextLine())) {
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
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> pauseAfter(archiveProduct());
                case 2 -> pauseAfter(restoreProduct());
                case 3 -> viewAllArchivedProducts();
                case 4 -> pauseAfter(deleteProduct());
                case 0 -> { }
                default -> {
                    System.out.println("Invalid menu selection.");
                    pressEnterToContinue();
                }
            }
        } while (choice != 0);
    }

    private void printArchiveAndDeleteMenu() {
        showHeader("Archiving / Deleting Products");
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
                    category.getId(), truncate(category.getName(), 35));
        }
        System.out.println(border);
    }

    private void printSortOptions() {
        System.out.println();
        System.out.println("Sort by: [1] ID        [2] Name        [0] Back");
    }

    private void pauseAfter(boolean shouldPause) {
        if (shouldPause) {
            pressEnterToContinue();
        }
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
            String status = product.isArchived() ? "Archived" : "Active";
            System.out.printf("| %-4d | %-25s | %-20s | %-40s | %-25s | %-10s |%n",
                    product.getId(),
                    truncate(product.getName(), NAME_DISPLAY_WIDTH),
                    truncate(product.getBrand(), BRAND_DISPLAY_WIDTH),
                    truncate(description, DESCRIPTION_DISPLAY_WIDTH),
                    truncate(categoryName, CATEGORY_DISPLAY_WIDTH),
                    status);
        }

        System.out.println(border);
    }

    private String keepCurrentIfBlank(String value, String currentValue) {
        return value.trim().isEmpty() ? currentValue : value;
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
