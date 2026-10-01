package com.joysistvi.petstocks.cliview.publicview;

import com.joysistvi.petstocks.cliview.modelview.CategoryView;
import com.joysistvi.petstocks.cliview.modelview.PetTypeView;
import com.joysistvi.petstocks.cliview.modelview.ProductView;
import com.joysistvi.petstocks.cliview.modelview.SupplierView;
import com.joysistvi.petstocks.cliview.modelview.UserView;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.util.Scanner;

public class AdminManagementView {
    private final ProductView productView;
    private final CategoryView categoryView;
    private final SupplierView supplierView;
    private final PetTypeView petTypeView;
    private final UserView userView;
    private final Scanner scanner;

    public AdminManagementView(ProductView productView,
                               CategoryView categoryView,
                               SupplierView supplierView,
                               PetTypeView petTypeView,
                               UserView userView,
                               Scanner scanner) {
        this.productView = productView;
        this.categoryView = categoryView;
        this.supplierView = supplierView;
        this.petTypeView = petTypeView;
        this.userView = userView;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> productView.run();
                case 2 -> categoryView.run();
                case 3 -> supplierView.run();
                case 4 -> petTypeView.run();
                case 5 -> userView.run();
                case 0 -> { }
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        CliViewUtility.showScreen("Admin Management");
        System.out.println("1. Manage Products");
        System.out.println("2. Manage Categories");
        System.out.println("3. Manage Suppliers");
        System.out.println("4. Manage Pet Types");
        System.out.println("5. Manage Users");
        System.out.println("0. Back");
    }
}
