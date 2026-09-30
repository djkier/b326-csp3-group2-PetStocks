package com.joysistvi.petstocks.cliview;

import com.joysistvi.petstocks.controller.SupplierController;
import com.joysistvi.petstocks.model.Supplier;

import java.util.List;
import java.util.Scanner;

public class SupplierView {
    private static final int NAME_DISPLAY_WIDTH = 25;
    private static final int ADDRESS_DISPLAY_WIDTH = 40;
    private static final int CONTACT_DISPLAY_WIDTH = 20;
    private static final int EMAIL_DISPLAY_WIDTH = 30;

    private final SupplierController supplierController;
    private final Scanner scanner;

    public SupplierView(SupplierController supplierController, Scanner scanner) {
        this.supplierController = supplierController;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> viewAllSuppliers();
                case 2 -> pauseAfter(findSupplierById());
                case 3 -> {
                    searchSuppliers();
                    pressEnterToContinue();
                }
                case 4 -> {
                    createSupplier();
                    pressEnterToContinue();
                }
                case 5 -> pauseAfter(updateSupplier());
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
        showHeader("Supplier Management");
        System.out.println("1. View All Active Suppliers");
        System.out.println("2. Find Supplier by ID");
        System.out.println("3. Search Suppliers");
        System.out.println("4. Create Supplier");
        System.out.println("5. Update Supplier");
        System.out.println("6. Archiving / Deleting Suppliers");
        System.out.println("0. Back");
    }

    private void viewAllSuppliers() {
        String sortBy = "id";
        int choice;

        do {
            showHeader("Active Suppliers");
            printSuppliers(supplierController.handleViewAllSuppliers(sortBy));
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

    private boolean findSupplierById() {
        showHeader("Find Supplier By ID");
        int id = promptInt("Supplier ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Supplier supplier = supplierController.handleGetSupplierById(id);

        if (supplier != null) {
            printSuppliers(List.of(supplier));
        }
        return true;
    }

    private void searchSuppliers() {
        showHeader("Search Suppliers");
        System.out.print("Enter name, address, contact number, or email: ");
        String keyword = scanner.nextLine();
        printSuppliers(supplierController.searchSuppliers(keyword));
    }

    private void createSupplier() {
        showHeader("Create Supplier");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Contact number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        Supplier supplier = new Supplier(name, address, contactNumber, email);
        boolean isSuccess = supplierController.handleCreateSupplier(supplier);
        System.out.println(isSuccess
                ? "Supplier created successfully."
                : "Failed to create supplier.");

        if (isSuccess) {
            System.out.println();
            printSuppliers(supplierController.handleViewAllSuppliers("id"));
        }
    }

    private boolean updateSupplier() {
        showHeader("Update Supplier");
        printSuppliers(supplierController.handleViewAllSuppliers("id"));

        int id = promptInt("Supplier ID to update (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Supplier current = supplierController.handleGetSupplierById(id);

        if (current == null) {
            return true;
        }
        if (current.isArchived()) {
            System.out.println("Restore this supplier before updating it.");
            return true;
        }

        System.out.print("New name [" + current.getName() + "] (Enter to keep): ");
        String name = keepCurrentIfBlank(scanner.nextLine(), current.getName());

        System.out.print("New address [" + current.getAddress() + "] (Enter to keep): ");
        String address = keepCurrentIfBlank(scanner.nextLine(), current.getAddress());

        System.out.print("New contact number [" + current.getContactNumber() +
                "] (Enter to keep): ");
        String contactNumber = keepCurrentIfBlank(scanner.nextLine(), current.getContactNumber());

        System.out.print("New email [" + current.getEmail() + "] (Enter to keep): ");
        String email = keepCurrentIfBlank(scanner.nextLine(), current.getEmail());

        Supplier supplier = new Supplier(id, name, address, contactNumber, email);
        boolean isSuccess = supplierController.handleUpdateSupplier(supplier);
        System.out.println(isSuccess
                ? "Supplier updated successfully."
                : "Failed to update supplier.");

        if (isSuccess) {
            System.out.println();
            printSuppliers(supplierController.handleViewAllSuppliers("id"));
        }
        return true;
    }

    private boolean archiveSupplier() {
        showHeader("Archive Supplier");
        printSuppliers(supplierController.handleViewAllSuppliers("id"));
        int id = promptInt("Supplier ID to archive (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        boolean isSuccess = supplierController.handleArchiveSupplier(id);
        System.out.println(isSuccess
                ? "Supplier archived successfully."
                : "Failed to archive supplier. Check that the ID is active.");
        return true;
    }

    private boolean restoreSupplier() {
        showHeader("Restore Supplier");
        printSuppliers(supplierController.handleViewArchivedSuppliers("id"));
        int id = promptInt("Supplier ID to restore (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        boolean isSuccess = supplierController.handleRestoreSupplier(id);
        System.out.println(isSuccess
                ? "Supplier restored successfully."
                : "Failed to restore supplier. Check that the ID is archived.");
        return true;
    }

    private void viewAllArchivedSuppliers() {
        String sortBy = "id";
        int choice;

        do {
            showHeader("Archived Suppliers");
            printSuppliers(supplierController.handleViewArchivedSuppliers(sortBy));
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

    private boolean deleteSupplier() {
        showHeader("Delete Archived Supplier");
        printSuppliers(supplierController.handleViewArchivedSuppliers("id"));
        int id = promptInt("Archived supplier ID to delete permanently (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        System.out.print("This cannot be undone. Type DELETE to confirm: ");
        if (!"DELETE".equals(scanner.nextLine())) {
            System.out.println("Delete cancelled.");
            return true;
        }

        boolean isSuccess = supplierController.handleDeleteSupplier(id);
        System.out.println(isSuccess
                ? "Supplier deleted successfully."
                : "Failed to delete supplier. It must be archived and not referenced by a restock.");
        return true;
    }

    private void runArchiveAndDeleteMenu() {
        int choice;

        do {
            printArchiveAndDeleteMenu();
            choice = promptInt("Choice: ");

            switch (choice) {
                case 1 -> pauseAfter(archiveSupplier());
                case 2 -> pauseAfter(restoreSupplier());
                case 3 -> viewAllArchivedSuppliers();
                case 4 -> pauseAfter(deleteSupplier());
                case 0 -> { }
                default -> {
                    System.out.println("Invalid menu selection.");
                    pressEnterToContinue();
                }
            }
        } while (choice != 0);
    }

    private void printArchiveAndDeleteMenu() {
        showHeader("Archiving / Deleting Suppliers");
        System.out.println("1. Archive Supplier");
        System.out.println("2. Restore Supplier");
        System.out.println("3. View Archived Suppliers");
        System.out.println("4. Delete Archived Supplier");
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

    public void printSuppliers(List<Supplier> suppliers) {
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers found.");
            return;
        }

        String border = "+" + "-".repeat(6) + "+" + "-".repeat(NAME_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(ADDRESS_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(CONTACT_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(EMAIL_DISPLAY_WIDTH + 2) + "+" + "-".repeat(12) + "+";

        System.out.println(border);
        System.out.printf("| %-4s | %-25s | %-40s | %-20s | %-30s | %-10s |%n",
                "ID", "Name", "Address", "Contact Number", "Email", "Status");
        System.out.println(border);

        for (Supplier supplier : suppliers) {
            String status = supplier.isArchived() ? "Archived" : "Active";
            System.out.printf("| %-4d | %-25s | %-40s | %-20s | %-30s | %-10s |%n",
                    supplier.getId(),
                    truncate(supplier.getName(), NAME_DISPLAY_WIDTH),
                    truncate(supplier.getAddress(), ADDRESS_DISPLAY_WIDTH),
                    truncate(supplier.getContactNumber(), CONTACT_DISPLAY_WIDTH),
                    truncate(supplier.getEmail(), EMAIL_DISPLAY_WIDTH),
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
