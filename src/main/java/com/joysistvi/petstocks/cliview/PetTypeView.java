package com.joysistvi.petstocks.cliview;

import com.joysistvi.petstocks.controller.PetTypeController;
import com.joysistvi.petstocks.model.PetType;

import java.util.List;
import java.util.Scanner;

public class PetTypeView {
    private static final int DESCRIPTION_DISPLAY_WIDTH = 50;

    private final PetTypeController petTypeController;
    private final Scanner scanner;

    public PetTypeView(PetTypeController petTypeController, Scanner scanner) {
        this.petTypeController = petTypeController;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> viewAllPetTypes();
                case 2 -> pauseAfter(findPetTypeById());
                case 3 -> {
                    searchPetTypes();
                    pressEnterToContinue();
                }
                case 4 -> {
                    createPetType();
                    pressEnterToContinue();
                }
                case 5 -> pauseAfter(updatePetType());
                case 6 -> runArchiveAndDeleteMenu();
                case 0 -> System.out.println("Exiting PetStock...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    pressEnterToContinue();
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        showHeader("Pet Type Management");
        System.out.println("1. View All Active Pet Types");
        System.out.println("2. Find Pet Type by ID");
        System.out.println("3. Search Pet Types");
        System.out.println("4. Create Pet Type");
        System.out.println("5. Update Pet Type");
        System.out.println("6. Archiving / Deleting Pet Types");
        System.out.println("0. Exit");
    }

    private void viewAllPetTypes() {
        String sortBy = "id";
        int choice;

        do {
            showHeader("Active Pet Types");
            printPetTypes(petTypeController.handleViewAllPetTypes(sortBy));
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

    private boolean findPetTypeById() {
        showHeader("Find Pet Type By ID");
        int id = promptInt("Pet type ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        PetType petType = petTypeController.handleGetPetTypeById(id);

        if (petType != null) {
            printPetTypes(List.of(petType));
        }
        return true;
    }

    private void searchPetTypes() {
        showHeader("Search Pet Types");
        System.out.print("Enter name or description: ");
        String keyword = scanner.nextLine();
        printPetTypes(petTypeController.searchPetTypes(keyword));
    }

    private void createPetType() {
        showHeader("Create Pet Type");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Description (optional): ");
        String description = scanner.nextLine();

        PetType petType = new PetType(name, description);
        boolean isSuccess = petTypeController.handleCreatePetType(petType);
        System.out.println(isSuccess
                ? "Pet type created successfully."
                : "Failed to create pet type.");

        if (isSuccess) {
            System.out.println();
            printPetTypes(petTypeController.handleViewAllPetTypes("id"));
        }
    }

    private boolean updatePetType() {
        showHeader("Update Pet Type");
        printPetTypes(petTypeController.handleViewAllPetTypes("id"));

        int id = promptInt("Pet type ID to update (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        PetType current = petTypeController.handleGetPetTypeById(id);

        if (current == null) {
            return true;
        }
        if (current.isArchived()) {
            System.out.println("Restore this pet type before updating it.");
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

        PetType petType = new PetType(id, name, description);
        boolean isSuccess = petTypeController.handleUpdatePetType(petType);
        System.out.println(isSuccess
                ? "Pet type updated successfully."
                : "Failed to update pet type.");

        if (isSuccess) {
            System.out.println();
            printPetTypes(petTypeController.handleViewAllPetTypes("id"));
        }
        return true;
    }

    private boolean archivePetType() {
        showHeader("Archive Pet Type");
        printPetTypes(petTypeController.handleViewAllPetTypes("id"));
        int id = promptInt("Pet type ID to archive (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        boolean isSuccess = petTypeController.handleArchivePetType(id);
        System.out.println(isSuccess
                ? "Pet type archived successfully."
                : "Failed to archive pet type. Check that the ID is active.");
        return true;
    }

    private boolean restorePetType() {
        showHeader("Restore Pet Type");
        printPetTypes(petTypeController.handleViewArchivedPetTypes("id"));
        int id = promptInt("Pet type ID to restore (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        boolean isSuccess = petTypeController.handleRestorePetType(id);
        System.out.println(isSuccess
                ? "Pet type restored successfully."
                : "Failed to restore pet type. Check that the ID is archived.");
        return true;
    }

    private void viewAllArchivedPetTypes() {
        String sortBy = "id";
        int choice;

        do {
            showHeader("Archived Pet Types");
            printPetTypes(petTypeController.handleViewArchivedPetTypes(sortBy));
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

    private boolean deletePetType() {
        showHeader("Delete Archived Pet Type");
        printPetTypes(petTypeController.handleViewArchivedPetTypes("id"));
        int id = promptInt("Archived pet type ID to delete permanently (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        System.out.print("This cannot be undone. Type DELETE to confirm: ");
        if (!"DELETE".equals(scanner.nextLine())) {
            System.out.println("Delete cancelled.");
            return true;
        }

        boolean isSuccess = petTypeController.handleDeletePetType(id);
        System.out.println(isSuccess
                ? "Pet type deleted successfully."
                : "Failed to delete pet type. It must be archived and not referenced by a product.");
        return true;
    }

    private void runArchiveAndDeleteMenu() {
        int choice;

        do {
            printArchiveAndDeleteMenu();
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> pauseAfter(archivePetType());
                case 2 -> pauseAfter(restorePetType());
                case 3 -> viewAllArchivedPetTypes();
                case 4 -> pauseAfter(deletePetType());
                case 0 -> { }
                default -> {
                    System.out.println("Invalid menu selection.");
                    pressEnterToContinue();
                }
            }
        } while (choice != 0);
    }

    private void printArchiveAndDeleteMenu() {
        showHeader("Archiving / Deleting Pet Types");
        System.out.println("1. Archive Pet Type");
        System.out.println("2. Restore Pet Type");
        System.out.println("3. View Archived Pet Types");
        System.out.println("4. Delete Archived Pet Type");
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

    public void printPetTypes(List<PetType> petTypes) {
        if (petTypes.isEmpty()) {
            System.out.println("No pet types found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27)
                + "+" + "-".repeat(DESCRIPTION_DISPLAY_WIDTH + 2) + "+" + "-".repeat(12) + "+";

        System.out.println(border);
        System.out.printf("| %-4s | %-25s | %-50s | %-10s |%n",
                "ID", "Name", "Description", "Status");
        System.out.println(border);

        for (PetType petType : petTypes) {
            String description = petType.getDescription() == null
                    ? "" : truncate(petType.getDescription(), DESCRIPTION_DISPLAY_WIDTH);
            String status = petType.isArchived() ? "Archived" : "Active";
            System.out.printf("| %-4d | %-25s | %-50s | %-10s |%n",
                    petType.getId(), truncate(petType.getName(), 25), description, status);
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
