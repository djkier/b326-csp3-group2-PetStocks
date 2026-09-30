package com.joysistvi.petstocks;

import com.joysistvi.petstocks.cliview.CategoryView;
import com.joysistvi.petstocks.cliview.PetTypeView;
import com.joysistvi.petstocks.cliview.SupplierView;
import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.controller.CategoryController;
import com.joysistvi.petstocks.controller.PetTypeController;
import com.joysistvi.petstocks.controller.SupplierController;
import com.joysistvi.petstocks.repository.CategoryRepo;
import com.joysistvi.petstocks.repository.CategoryRepoImpl;
import com.joysistvi.petstocks.repository.PetTypeRepo;
import com.joysistvi.petstocks.repository.PetTypeRepoImpl;
import com.joysistvi.petstocks.repository.SupplierRepo;
import com.joysistvi.petstocks.repository.SupplierRepoImpl;
import com.joysistvi.petstocks.service.CategoryService;
import com.joysistvi.petstocks.service.CategoryServiceImpl;
import com.joysistvi.petstocks.service.PetTypeService;
import com.joysistvi.petstocks.service.PetTypeServiceImpl;
import com.joysistvi.petstocks.service.SupplierService;
import com.joysistvi.petstocks.service.SupplierServiceImpl;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {

        DBConnection dbConnection = new DBConnection();
        PetTypeRepo petTypeRepo = new PetTypeRepoImpl(dbConnection);
        PetTypeService petTypeService = new PetTypeServiceImpl(petTypeRepo);
        PetTypeController petTypeController = new PetTypeController(petTypeService);

        CategoryRepo categoryRepo = new CategoryRepoImpl(dbConnection);
        CategoryService categoryService = new CategoryServiceImpl(categoryRepo);
        CategoryController categoryController = new CategoryController(categoryService);

        SupplierRepo supplierRepo = new SupplierRepoImpl(dbConnection);
        SupplierService supplierService = new SupplierServiceImpl(supplierRepo);
        SupplierController supplierController = new SupplierController(supplierService);

        try (Scanner scanner = new Scanner(System.in)) {
            PetTypeView petTypeView = new PetTypeView(petTypeController, scanner);
            CategoryView categoryView = new CategoryView(categoryController, scanner);
            SupplierView supplierView = new SupplierView(supplierController, scanner);

            runDevelopmentMenu(scanner, petTypeView, categoryView, supplierView);
        }
    }

    private static void runDevelopmentMenu(Scanner scanner, PetTypeView petTypeView,
                                           CategoryView categoryView, SupplierView supplierView) {
        int choice;

        do {
            System.out.println();
            System.out.println("=".repeat(40));
            System.out.println("PetStock MVC Development Menu");
            System.out.println("=".repeat(40));
            System.out.println("1. Test Pet Type MVC");
            System.out.println("2. Test Category MVC");
            System.out.println("3. Test Supplier MVC");
            System.out.println("0. Exit");
            choice = promptInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> petTypeView.run();
                case 2 -> categoryView.run();
                case 3 -> supplierView.run();
                case 0 -> System.out.println("Exiting PetStock...");
                default -> System.out.println("Invalid menu selection.");
            }
        } while (choice != 0);
    }

    private static int promptInt(Scanner scanner, String prompt) {
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
}
