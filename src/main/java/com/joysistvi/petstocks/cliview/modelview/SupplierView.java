package com.joysistvi.petstocks.cliview.modelview;

import com.joysistvi.petstocks.controller.SupplierController;
import com.joysistvi.petstocks.model.Supplier;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

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
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> viewAllSuppliers();
                case 2 -> CliViewUtility.pauseAfter(scanner, findSupplierById());
                case 3 -> {
                    searchSuppliers();
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
                case 4 -> {
                    createSupplier();
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
                case 5 -> CliViewUtility.pauseAfter(scanner, updateSupplier());
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
        CliViewUtility.showScreen("Supplier Management");
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
        int currentPage = 0;

        while (true) {
            List<Supplier> suppliers = supplierController.handleViewAllSuppliers(sortBy);
            currentPage = CliViewUtility.normalizePage(currentPage, suppliers.size());
            CliViewUtility.showScreen("Active Suppliers");
            printSuppliers(CliViewUtility.page(suppliers, currentPage));
            CliViewUtility.printPagination(currentPage, suppliers.size());
            printSortOptions();
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1" -> { sortBy = "id"; currentPage = 0; }
                case "2" -> { sortBy = "name"; currentPage = 0; }
                case "P" -> currentPage = CliViewUtility.previousPage(currentPage, scanner);
                case "N" -> currentPage = CliViewUtility.nextPage(currentPage, suppliers.size(), scanner);
                case "0" -> { return; }
                default -> {
                    System.out.println("Invalid sort selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        }
    }

    private boolean findSupplierById() {
        CliViewUtility.showScreen("Find Supplier By ID");
        int id = InputUtility.readInt(scanner, "Supplier ID (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        Supplier supplier = supplierController.handleGetSupplierById(id);

        if (supplier != null) {
            CliViewUtility.browsePages(List.of(supplier), scanner, this::printSuppliers);
        }
        return true;
    }

    private void searchSuppliers() {
        CliViewUtility.showScreen("Search Suppliers");
        System.out.print("Enter name, address, contact number, or email: ");
        String keyword = scanner.nextLine();
        CliViewUtility.browsePages(
                supplierController.searchSuppliers(keyword), scanner, this::printSuppliers);
    }

    private void createSupplier() {
        Supplier supplier = promptForNewSupplier("Create Supplier", false);
        boolean isSuccess = saveNewSupplier(supplier);

        if (isSuccess) {
            System.out.println();
            CliViewUtility.browsePages(
                    supplierController.handleViewAllSuppliers("id"), scanner, this::printSuppliers);
        }
    }

    public Supplier createSupplierForStockIn() {
        while (true) {
            Supplier supplier = promptForNewSupplier("Add Custom Supplier", true);
            if (supplier == null) {
                return null;
            }
            if (saveNewSupplier(supplier)) {
                return supplier;
            }
            if (!promptToRetrySupplierCreation()) {
                return null;
            }
        }
    }

    private Supplier promptForNewSupplier(String title, boolean allowImmediateCancel) {
        CliViewUtility.showScreen(title);
        System.out.print(allowImmediateCancel ? "Name (0 to cancel): " : "Name: ");
        String name = scanner.nextLine();
        if (allowImmediateCancel && "0".equals(name.trim())) {
            return null;
        }
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Contact number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Email (optional): ");
        String email = scanner.nextLine();
        return new Supplier(name, address, contactNumber, email);
    }

    private boolean saveNewSupplier(Supplier supplier) {
        boolean isSuccess = supplierController.handleCreateSupplier(supplier);
        System.out.println(isSuccess
                ? "Supplier created successfully."
                : "Failed to create supplier.");
        return isSuccess;
    }

    private boolean promptToRetrySupplierCreation() {
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

    private boolean updateSupplier() {
        CliViewUtility.showScreen("Update Supplier");
        CliViewUtility.browsePages(
                supplierController.handleViewAllSuppliers("id"), scanner, this::printSuppliers);

        int id = InputUtility.readInt(scanner, "Supplier ID to update (0 to cancel): ");
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
        String name = CliViewUtility.keepCurrentIfBlank(scanner.nextLine(), current.getName());

        System.out.print("New address [" + current.getAddress() + "] (Enter to keep): ");
        String address = CliViewUtility.keepCurrentIfBlank(scanner.nextLine(), current.getAddress());

        System.out.print("New contact number [" + current.getContactNumber() +
                "] (Enter to keep): ");
        String contactNumber = CliViewUtility.keepCurrentIfBlank(scanner.nextLine(), current.getContactNumber());

        String currentEmail = current.getEmail() == null ? "" : current.getEmail();
        System.out.print("New email [" + currentEmail +
                "] (Enter for none, type KEEP to retain): ");
        String emailInput = scanner.nextLine();
        String email = "KEEP".equalsIgnoreCase(emailInput.trim())
                ? current.getEmail() : emailInput;

        Supplier supplier = new Supplier(id, name, address, contactNumber, email);
        boolean isSuccess = supplierController.handleUpdateSupplier(supplier);
        System.out.println(isSuccess
                ? "Supplier updated successfully."
                : "Failed to update supplier.");

        if (isSuccess) {
            System.out.println();
            CliViewUtility.browsePages(
                    supplierController.handleViewAllSuppliers("id"), scanner, this::printSuppliers);
        }
        return true;
    }

    private boolean archiveSupplier() {
        CliViewUtility.showScreen("Archive Supplier");
        CliViewUtility.browsePages(
                supplierController.handleViewAllSuppliers("id"), scanner, this::printSuppliers);
        int id = InputUtility.readInt(scanner, "Supplier ID to archive (0 to cancel): ");
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
        CliViewUtility.showScreen("Restore Supplier");
        CliViewUtility.browsePages(
                supplierController.handleViewArchivedSuppliers("id"), scanner, this::printSuppliers);
        int id = InputUtility.readInt(scanner, "Supplier ID to restore (0 to cancel): ");
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
        int currentPage = 0;

        while (true) {
            List<Supplier> suppliers = supplierController.handleViewArchivedSuppliers(sortBy);
            currentPage = CliViewUtility.normalizePage(currentPage, suppliers.size());
            CliViewUtility.showScreen("Archived Suppliers");
            printSuppliers(CliViewUtility.page(suppliers, currentPage));
            CliViewUtility.printPagination(currentPage, suppliers.size());
            printSortOptions();
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1" -> { sortBy = "id"; currentPage = 0; }
                case "2" -> { sortBy = "name"; currentPage = 0; }
                case "P" -> currentPage = CliViewUtility.previousPage(currentPage, scanner);
                case "N" -> currentPage = CliViewUtility.nextPage(currentPage, suppliers.size(), scanner);
                case "0" -> { return; }
                default -> {
                    System.out.println("Invalid sort selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        }
    }

    private boolean deleteSupplier() {
        CliViewUtility.showScreen("Delete Archived Supplier");
        CliViewUtility.browsePages(
                supplierController.handleViewArchivedSuppliers("id"), scanner, this::printSuppliers);
        int id = InputUtility.readInt(scanner, "Archived supplier ID to delete permanently (0 to cancel): ");
        if (id == 0) {
            return false;
        }

        if (!CliViewUtility.confirmExact(scanner, "This cannot be undone. Type DELETE to confirm: ", "DELETE")) {
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
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> CliViewUtility.pauseAfter(scanner, archiveSupplier());
                case 2 -> CliViewUtility.pauseAfter(scanner, restoreSupplier());
                case 3 -> viewAllArchivedSuppliers();
                case 4 -> CliViewUtility.pauseAfter(scanner, deleteSupplier());
                case 0 -> { }
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printArchiveAndDeleteMenu() {
        CliViewUtility.showScreen("Archiving / Deleting Suppliers");
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

    public void printSuppliers(List<Supplier> suppliers) {
        String border = "+" + "-".repeat(6) + "+" + "-".repeat(NAME_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(ADDRESS_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(CONTACT_DISPLAY_WIDTH + 2)
                + "+" + "-".repeat(EMAIL_DISPLAY_WIDTH + 2) + "+" + "-".repeat(12) + "+";

        System.out.println(border);
        System.out.printf("| %-4s | %-25s | %-40s | %-20s | %-30s | %-10s |%n",
                "ID", "Name", "Address", "Contact Number", "Email", "Status");
        System.out.println(border);

        if (suppliers.isEmpty()) {
            System.out.println("No suppliers found.");
            System.out.println(border);
            return;
        }

        for (Supplier supplier : suppliers) {
            String status = CliViewUtility.formatArchiveStatus(supplier.isArchived());
            System.out.printf("| %-4d | %-25s | %-40s | %-20s | %-30s | %-10s |%n",
                    supplier.getId(),
                    CliViewUtility.truncate(supplier.getName(), NAME_DISPLAY_WIDTH),
                    CliViewUtility.truncate(supplier.getAddress(), ADDRESS_DISPLAY_WIDTH),
                    CliViewUtility.truncate(supplier.getContactNumber(), CONTACT_DISPLAY_WIDTH),
                    CliViewUtility.truncate(supplier.getEmail(), EMAIL_DISPLAY_WIDTH),
                    status);
        }

        System.out.println(border);
    }

}
