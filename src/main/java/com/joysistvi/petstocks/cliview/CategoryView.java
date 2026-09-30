package com.joysistvi.petstocks.cliview;

import com.joysistvi.petstocks.controller.CategoryController;
import com.joysistvi.petstocks.model.Category;

import java.util.List;
import java.util.Scanner;

public class CategoryView {
    private static final int DESCRIPTION_DISPLAY_WIDTH = 50;

    private final CategoryController categoryController;
    private final Scanner scanner;

    public CategoryView(CategoryController categoryController, Scanner scanner) {
        this.categoryController = categoryController;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> viewAllCategories();
                case 2 -> pauseAfter(findCategoryById());
                case 3 -> {
                    searchCategories();
                    pressEnterToContinue();
                }
                case 4 -> {
                    createCategory();
                    pressEnterToContinue();
                }
                case 5 -> pauseAfter(updateCategory());
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
        showHeader("Category Management");
        System.out.println("1. View All Active Categories");
        System.out.println("2. Find Category by ID");
        System.out.println("3. Search Categories");
        System.out.println("4. Create Category");
        System.out.println("5. Update Category");
        System.out.println("6. Archiving / Deleting Categories");
        System.out.println("0. Back");
    }

    private void viewAllCategories() {
        String sortBy = "id";
        int choice;

        do {
            showHeader("Active Categories");
            printCategories(categoryController.handleViewAllCategories(sortBy));
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

    private boolean findCategoryById() {
        showHeader("Find Category By ID");
        int id = promptInt("Category ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Category category = categoryController.handleGetCategoryById(id);

        if (category != null) {
            printCategories(List.of(category));
        }
        return true;
    }

    private void searchCategories() {
        showHeader("Search Categories");
        System.out.print("Enter name or description: ");
        String keyword = scanner.nextLine();
        printCategories(categoryController.searchCategories(keyword));
    }

    private void createCategory() {
        showHeader("Create Category");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Description (optional): ");
        String description = scanner.nextLine();

        Category category = new Category(name, description);
        boolean isSuccess = categoryController.handleCreateCategory(category);
        System.out.println(isSuccess
                ? "Category created successfully."
                : "Failed to create category.");

        if (isSuccess) {
            System.out.println();
            printCategories(categoryController.handleViewAllCategories("id"));
        }
    }

    private boolean updateCategory() {
        showHeader("Update Category");
        printCategories(categoryController.handleViewAllCategories("id"));

        int id = promptInt("Category ID to update (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Category current = categoryController.handleGetCategoryById(id);

        if (current == null) {
            return true;
        }
        if (current.isArchived()) {
            System.out.println("Restore this category before updating it.");
            return true;
        }

        System.out.print("New name [" + current.getName() + "] (Enter to keep): ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = current.getName();
        }

        String currentDescription = current.getDescription() == null
                ? "" : current.getDescription();
        System.out.print("New description [" + currentDescription + "] (Enter to keep): ");
        String description = scanner.nextLine();
        if (description.trim().isEmpty()) {
            description = current.getDescription();
        }

        Category category = new Category(id, name, description);
        boolean isSuccess = categoryController.handleUpdateCategory(category);
        System.out.println(isSuccess
                ? "Category updated successfully."
                : "Failed to update category.");

        if (isSuccess) {
            System.out.println();
            printCategories(categoryController.handleViewAllCategories("id"));
        }
        return true;
    }

    private boolean archiveCategory() {
        showHeader("Archive Category");
        printCategories(categoryController.handleViewAllCategories("id"));
        int id = promptInt("Category ID to archive (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        boolean isSuccess = categoryController.handleArchiveCategory(id);
        System.out.println(isSuccess
                ? "Category archived successfully."
                : "Failed to archive category. Check that the ID is active.");
        return true;
    }

    private boolean restoreCategory() {
        showHeader("Restore Category");
        printCategories(categoryController.handleViewArchivedCategories("id"));
        int id = promptInt("Category ID to restore (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        boolean isSuccess = categoryController.handleRestoreCategory(id);
        System.out.println(isSuccess
                ? "Category restored successfully."
                : "Failed to restore category. Check that the ID is archived.");
        return true;
    }

    private void viewAllArchivedCategories() {
        String sortBy = "id";
        int choice;

        do {
            showHeader("Archived Categories");
            printCategories(categoryController.handleViewArchivedCategories(sortBy));
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

    private boolean deleteCategory() {
        showHeader("Delete Archived Category");
        printCategories(categoryController.handleViewArchivedCategories("id"));
        int id = promptInt("Archived category ID to delete permanently (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        System.out.print("This cannot be undone. Type DELETE to confirm: ");
        if (!"DELETE".equals(scanner.nextLine())) {
            System.out.println("Delete cancelled.");
            return true;
        }

        boolean isSuccess = categoryController.handleDeleteCategory(id);
        System.out.println(isSuccess
                ? "Category deleted successfully."
                : "Failed to delete category. It must be archived and not referenced by a product.");
        return true;
    }

    private void runArchiveAndDeleteMenu() {
        int choice;

        do {
            printArchiveAndDeleteMenu();
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> pauseAfter(archiveCategory());
                case 2 -> pauseAfter(restoreCategory());
                case 3 -> viewAllArchivedCategories();
                case 4 -> pauseAfter(deleteCategory());
                case 0 -> { }
                default -> {
                    System.out.println("Invalid menu selection.");
                    pressEnterToContinue();
                }
            }
        } while (choice != 0);
    }

    private void printArchiveAndDeleteMenu() {
        showHeader("Archiving / Deleting Categories");
        System.out.println("1. Archive Category");
        System.out.println("2. Restore Category");
        System.out.println("3. View Archived Categories");
        System.out.println("4. Delete Archived Category");
        System.out.println("0. Back");
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

    public void printCategories(List<Category> categories) {
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27)
                + "+" + "-".repeat(DESCRIPTION_DISPLAY_WIDTH + 2) + "+" + "-".repeat(12) + "+";

        System.out.println(border);
        System.out.printf("| %-4s | %-25s | %-50s | %-10s |%n",
                "ID", "Name", "Description", "Status");
        System.out.println(border);

        for (Category category : categories) {
            String description = category.getDescription() == null
                    ? "" : truncate(category.getDescription(), DESCRIPTION_DISPLAY_WIDTH);
            String status = category.isArchived() ? "Archived" : "Active";
            System.out.printf("| %-4d | %-25s | %-50s | %-10s |%n",
                    category.getId(), truncate(category.getName(), 25), description, status);
        }

        System.out.println(border);
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
