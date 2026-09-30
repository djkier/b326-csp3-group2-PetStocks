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
                case 2 -> findPetTypeById();
                case 3 -> searchPetTypes();
                case 4 -> createPetType();
                case 5 -> updatePetType();
                case 6 -> archivePetType();
                case 7 -> restorePetType();
                case 8 -> viewAllArchivedPetTypes();
                case 9 -> deletePetType();
                case 0 -> System.out.println("Exiting PetStock...");
                default -> System.out.println("Invalid menu selection.");
            }

            if (choice != 0) {
                pressEnterToContinue();
            }
        } while (choice != 0);
    }

    private void printMenu() {
        showHeader("Pet Type Management");
        System.out.println("1. View All Active Pet Types");
        System.out.println("2. Find Pet Type By ID");
        System.out.println("3. Search Pet Types");
        System.out.println("4. Create Pet Type");
        System.out.println("5. Update Pet Type");
        System.out.println("6. Archive Pet Type");
        System.out.println("7. Restore Pet Type");
        System.out.println("8. View Archived Pet Types");
        System.out.println("9. Delete Archived Pet Type");
        System.out.println("0. Exit");
    }

    private void viewAllPetTypes() {
        showHeader("Active Pet Types");
        printPetTypes(petTypeController.handleViewAllPetTypes());
    }

    private void findPetTypeById() {
        showHeader("Find Pet Type By ID");
        int id = promptInt("Pet type ID: ");
        PetType petType = petTypeController.handleGetPetTypeById(id);

        if (petType != null) {
            printPetTypes(List.of(petType));
        }
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
            viewAllPetTypes();
        }
    }

    private void updatePetType() {
        showHeader("Update Pet Type");
        printPetTypes(petTypeController.handleViewAllPetTypes());

        int id = promptInt("Pet type ID to update: ");
        PetType current = petTypeController.handleGetPetTypeById(id);

        if (current == null) {
            return;
        }
        if (current.isArchived()) {
            System.out.println("Restore this pet type before updating it.");
            return;
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
            viewAllPetTypes();
        }
    }

    private void archivePetType() {
        showHeader("Archive Pet Type");
        printPetTypes(petTypeController.handleViewAllPetTypes());
        int id = promptInt("Pet type ID to archive: ");

        boolean isSuccess = petTypeController.handleArchivePetType(id);
        System.out.println(isSuccess
                ? "Pet type archived successfully."
                : "Failed to archive pet type. Check that the ID is active.");
    }

    private void restorePetType() {
        showHeader("Restore Pet Type");
        printPetTypes(petTypeController.handleViewArchivedPetTypes());
        int id = promptInt("Pet type ID to restore: ");

        boolean isSuccess = petTypeController.handleRestorePetType(id);
        System.out.println(isSuccess
                ? "Pet type restored successfully."
                : "Failed to restore pet type. Check that the ID is archived.");
    }

    private void viewAllArchivedPetTypes() {
        showHeader("Archived Pet Types");
        printPetTypes(petTypeController.handleViewArchivedPetTypes());
    }

    private void deletePetType() {
        showHeader("Delete Archived Pet Type");
        printPetTypes(petTypeController.handleViewArchivedPetTypes());
        int id = promptInt("Archived pet type ID to delete permanently: ");

        System.out.print("This cannot be undone. Type DELETE to confirm: ");
        if (!"DELETE".equals(scanner.nextLine())) {
            System.out.println("Delete cancelled.");
            return;
        }

        boolean isSuccess = petTypeController.handleDeletePetType(id);
        System.out.println(isSuccess
                ? "Pet type deleted successfully."
                : "Failed to delete pet type. It must be archived and not referenced by a product.");
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
