package com.joysistvi.petstocks.cliview.modelview;

import com.joysistvi.petstocks.controller.PetTypeController;
import com.joysistvi.petstocks.model.PetType;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

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
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> viewAllPetTypes();
                case 2 -> CliViewUtility.pauseAfter(scanner, findPetTypeById());
                case 3 -> {
                    searchPetTypes();
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
                case 4 -> {
                    createPetType();
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
                case 5 -> CliViewUtility.pauseAfter(scanner, updatePetType());
                case 6 -> runArchiveAndDeleteMenu();
                case 0 -> System.out.println("Exiting PetStock...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        CliViewUtility.showHeader("Pet Type Management");
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
            CliViewUtility.showHeader("Active Pet Types");
            printPetTypes(petTypeController.handleViewAllPetTypes(sortBy));
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

    private boolean findPetTypeById() {
        CliViewUtility.showHeader("Find Pet Type By ID");
        int id = InputUtility.readInt(scanner, "Pet type ID (0 to cancel): ");
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
        CliViewUtility.showHeader("Search Pet Types");
        System.out.print("Enter name or description: ");
        String keyword = scanner.nextLine();
        printPetTypes(petTypeController.searchPetTypes(keyword));
    }

    private void createPetType() {
        CliViewUtility.showHeader("Create Pet Type");
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
        CliViewUtility.showHeader("Update Pet Type");
        printPetTypes(petTypeController.handleViewAllPetTypes("id"));

        int id = InputUtility.readInt(scanner, "Pet type ID to update (0 to cancel): ");
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
        System.out.print("New description [" + currentDescription +
                "] (Enter for none, type KEEP to retain): ");
        String descriptionInput = scanner.nextLine();
        String description = "KEEP".equalsIgnoreCase(descriptionInput.trim())
                ? current.getDescription() : descriptionInput;

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
        CliViewUtility.showHeader("Archive Pet Type");
        printPetTypes(petTypeController.handleViewAllPetTypes("id"));
        int id = InputUtility.readInt(scanner, "Pet type ID to archive (0 to cancel): ");
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
        CliViewUtility.showHeader("Restore Pet Type");
        printPetTypes(petTypeController.handleViewArchivedPetTypes("id"));
        int id = InputUtility.readInt(scanner, "Pet type ID to restore (0 to cancel): ");
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
            CliViewUtility.showHeader("Archived Pet Types");
            printPetTypes(petTypeController.handleViewArchivedPetTypes(sortBy));
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

    private boolean deletePetType() {
        CliViewUtility.showHeader("Delete Archived Pet Type");
        printPetTypes(petTypeController.handleViewArchivedPetTypes("id"));
        int id = InputUtility.readInt(scanner, "Archived pet type ID to delete permanently (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        if (!CliViewUtility.confirmExact(scanner, "This cannot be undone. Type DELETE to confirm: ", "DELETE")) {
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
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> CliViewUtility.pauseAfter(scanner, archivePetType());
                case 2 -> CliViewUtility.pauseAfter(scanner, restorePetType());
                case 3 -> viewAllArchivedPetTypes();
                case 4 -> CliViewUtility.pauseAfter(scanner, deletePetType());
                case 0 -> { }
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printArchiveAndDeleteMenu() {
        CliViewUtility.showHeader("Archiving / Deleting Pet Types");
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
                    ? "" : CliViewUtility.truncate(petType.getDescription(), DESCRIPTION_DISPLAY_WIDTH);
            String status = CliViewUtility.formatArchiveStatus(petType.isArchived());
            System.out.printf("| %-4d | %-25s | %-50s | %-10s |%n",
                    petType.getId(), CliViewUtility.truncate(petType.getName(), 25), description, status);
        }

        System.out.println(border);
    }

}
