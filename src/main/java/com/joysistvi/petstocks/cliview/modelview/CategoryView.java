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
        CliViewUtility.showScreen("Category Management");
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
        int currentPage = 0;

        while (true) {
            List<Category> categories = categoryController.handleViewAllCategories(sortBy);
            currentPage = CliViewUtility.normalizePage(currentPage, categories.size());
            CliViewUtility.showScreen("Active Categories");
            printCategories(CliViewUtility.page(categories, currentPage));
            CliViewUtility.printPagination(currentPage, categories.size());
            printSortOptions();
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1" -> { sortBy = "id"; currentPage = 0; }
                case "2" -> { sortBy = "name"; currentPage = 0; }
                case "P" -> currentPage = CliViewUtility.previousPage(currentPage, scanner);
                case "N" -> currentPage = CliViewUtility.nextPage(currentPage, categories.size(), scanner);
                case "0" -> { return; }
                default -> {
                    System.out.println("Invalid sort selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        }
    }

    private boolean findCategoryById() {
        CliViewUtility.showScreen("Find Category By ID");
        int id = InputUtility.readInt(scanner, "Category ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Category category = categoryController.handleGetCategoryById(id);

        if (category != null) {
            CliViewUtility.browsePages(List.of(category), scanner, this::printCategories);
        }
        return true;
    }

    private void searchCategories() {
        CliViewUtility.showScreen("Search Categories");
        System.out.print("Enter name or description: ");
        String keyword = scanner.nextLine();
        CliViewUtility.browsePages(
                categoryController.searchCategories(keyword), scanner, this::printCategories);
    }

    private void createCategory() {
        CliViewUtility.showScreen("Create Category");
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
            CliViewUtility.browsePages(
                    categoryController.handleViewAllCategories("id"), scanner, this::printCategories);
        }
    }

    private boolean updateCategory() {
        CliViewUtility.showScreen("Update Category");
        CliViewUtility.browsePages(
                categoryController.handleViewAllCategories("id"), scanner, this::printCategories);

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
        System.out.print("New description [" + currentDescription +
                "] (Enter for none, type KEEP to retain): ");
        String descriptionInput = scanner.nextLine();
        String description = "KEEP".equalsIgnoreCase(descriptionInput.trim())
                ? current.getDescription() : descriptionInput;

        Category category = new Category(id, name, description);
        boolean isSuccess = categoryController.handleUpdateCategory(category);
        System.out.println(isSuccess
                ? "Category updated successfully."
                : "Failed to update category.");

        if (isSuccess) {
            System.out.println();
            CliViewUtility.browsePages(
                    categoryController.handleViewAllCategories("id"), scanner, this::printCategories);
        }
        return true;
    }

    private boolean archiveCategory() {
        CliViewUtility.showScreen("Archive Category");
        CliViewUtility.browsePages(
                categoryController.handleViewAllCategories("id"), scanner, this::printCategories);
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
        CliViewUtility.showScreen("Restore Category");
        CliViewUtility.browsePages(
                categoryController.handleViewArchivedCategories("id"), scanner, this::printCategories);
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
        int currentPage = 0;

        while (true) {
            List<Category> categories = categoryController.handleViewArchivedCategories(sortBy);
            currentPage = CliViewUtility.normalizePage(currentPage, categories.size());
            CliViewUtility.showScreen("Archived Categories");
            printCategories(CliViewUtility.page(categories, currentPage));
            CliViewUtility.printPagination(currentPage, categories.size());
            printSortOptions();
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1" -> { sortBy = "id"; currentPage = 0; }
                case "2" -> { sortBy = "name"; currentPage = 0; }
                case "P" -> currentPage = CliViewUtility.previousPage(currentPage, scanner);
                case "N" -> currentPage = CliViewUtility.nextPage(currentPage, categories.size(), scanner);
                case "0" -> { return; }
                default -> {
                    System.out.println("Invalid sort selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        }
    }

    private boolean deleteCategory() {
        CliViewUtility.showScreen("Delete Archived Category");
        CliViewUtility.browsePages(
                categoryController.handleViewArchivedCategories("id"), scanner, this::printCategories);
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
        CliViewUtility.showScreen("Archiving / Deleting Categories");
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
        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27)
                + "+" + "-".repeat(DESCRIPTION_DISPLAY_WIDTH + 2) + "+" + "-".repeat(12) + "+";

        System.out.println(border);
        System.out.printf("| %-4s | %-25s | %-50s | %-10s |%n",
                "ID", "Name", "Description", "Status");
        System.out.println(border);

        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            System.out.println(border);
            return;
        }

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
