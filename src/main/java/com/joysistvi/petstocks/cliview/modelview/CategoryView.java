package com.joysistvi.petstocks.cliview.modelview;

import com.joysistvi.petstocks.controller.CategoryController;
import com.joysistvi.petstocks.model.Category;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

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
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> viewAllCategories();
                case 2 -> CliViewUtility.pauseAfter(scanner, findCategoryById());
                case 3 -> {
                    searchCategories();
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
                case 4 -> {
                    createCategory();
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
                case 5 -> CliViewUtility.pauseAfter(scanner, updateCategory());
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
        CliViewUtility.showHeader("Category Management");
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
            CliViewUtility.showHeader("Active Categories");
            printCategories(categoryController.handleViewAllCategories(sortBy));
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

    private boolean findCategoryById() {
        CliViewUtility.showHeader("Find Category By ID");
        int id = InputUtility.readInt(scanner, "Category ID (0 to cancel): ");
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
        CliViewUtility.showHeader("Search Categories");
        System.out.print("Enter name or description: ");
        String keyword = scanner.nextLine();
        printCategories(categoryController.searchCategories(keyword));
    }

    private void createCategory() {
        CliViewUtility.showHeader("Create Category");
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
        CliViewUtility.showHeader("Update Category");
        printCategories(categoryController.handleViewAllCategories("id"));

        int id = InputUtility.readInt(scanner, "Category ID to update (0 to cancel): ");
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
        CliViewUtility.showHeader("Archive Category");
        printCategories(categoryController.handleViewAllCategories("id"));
        int id = InputUtility.readInt(scanner, "Category ID to archive (0 to cancel): ");
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
        CliViewUtility.showHeader("Restore Category");
        printCategories(categoryController.handleViewArchivedCategories("id"));
        int id = InputUtility.readInt(scanner, "Category ID to restore (0 to cancel): ");
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
            CliViewUtility.showHeader("Archived Categories");
            printCategories(categoryController.handleViewArchivedCategories(sortBy));
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

    private boolean deleteCategory() {
        CliViewUtility.showHeader("Delete Archived Category");
        printCategories(categoryController.handleViewArchivedCategories("id"));
        int id = InputUtility.readInt(scanner, "Archived category ID to delete permanently (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        if (!CliViewUtility.confirmExact(scanner, "This cannot be undone. Type DELETE to confirm: ", "DELETE")) {
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
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> CliViewUtility.pauseAfter(scanner, archiveCategory());
                case 2 -> CliViewUtility.pauseAfter(scanner, restoreCategory());
                case 3 -> viewAllArchivedCategories();
                case 4 -> CliViewUtility.pauseAfter(scanner, deleteCategory());
                case 0 -> { }
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printArchiveAndDeleteMenu() {
        CliViewUtility.showHeader("Archiving / Deleting Categories");
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
                    ? "" : CliViewUtility.truncate(category.getDescription(), DESCRIPTION_DISPLAY_WIDTH);
            String status = CliViewUtility.formatArchiveStatus(category.isArchived());
            System.out.printf("| %-4d | %-25s | %-50s | %-10s |%n",
                    category.getId(), CliViewUtility.truncate(category.getName(), 25), description, status);
        }

        System.out.println(border);
    }

}
