package com.joysistvi.petstocks;

import com.joysistvi.petstocks.cliview.modelview.CategoryView;
import com.joysistvi.petstocks.cliview.modelview.DispatchView;
import com.joysistvi.petstocks.cliview.modelview.InventoryView;
import com.joysistvi.petstocks.cliview.modelview.PetTypeView;
import com.joysistvi.petstocks.cliview.modelview.ProductPetTypeView;
import com.joysistvi.petstocks.cliview.modelview.ProductView;
import com.joysistvi.petstocks.cliview.modelview.RestockView;
import com.joysistvi.petstocks.cliview.modelview.SupplierView;
import com.joysistvi.petstocks.cliview.modelview.UserView;
import com.joysistvi.petstocks.cliview.publicview.LoginView;
import com.joysistvi.petstocks.cliview.publicview.MonitorView;
import com.joysistvi.petstocks.cliview.publicview.StaffInventoryView;
import com.joysistvi.petstocks.cliview.publicview.StaffMainView;
import com.joysistvi.petstocks.cliview.publicview.StockMovementView;
import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.controller.CategoryController;
import com.joysistvi.petstocks.controller.DispatchController;
import com.joysistvi.petstocks.controller.InventoryController;
import com.joysistvi.petstocks.controller.PetTypeController;
import com.joysistvi.petstocks.controller.ProductPetTypeController;
import com.joysistvi.petstocks.controller.ProductController;
import com.joysistvi.petstocks.controller.RestockController;
import com.joysistvi.petstocks.controller.SupplierController;
import com.joysistvi.petstocks.controller.UserController;
import com.joysistvi.petstocks.repository.CategoryRepo;
import com.joysistvi.petstocks.repository.CategoryRepoImpl;
import com.joysistvi.petstocks.repository.DispatchRepo;
import com.joysistvi.petstocks.repository.DispatchRepoImpl;
import com.joysistvi.petstocks.repository.InventoryRepo;
import com.joysistvi.petstocks.repository.InventoryRepoImpl;
import com.joysistvi.petstocks.repository.PetTypeRepo;
import com.joysistvi.petstocks.repository.PetTypeRepoImpl;
import com.joysistvi.petstocks.repository.ProductPetTypeRepo;
import com.joysistvi.petstocks.repository.ProductPetTypeRepoImpl;
import com.joysistvi.petstocks.repository.ProductRepo;
import com.joysistvi.petstocks.repository.ProductRepoImpl;
import com.joysistvi.petstocks.repository.RestockRepo;
import com.joysistvi.petstocks.repository.RestockRepoImpl;
import com.joysistvi.petstocks.repository.SupplierRepo;
import com.joysistvi.petstocks.repository.SupplierRepoImpl;
import com.joysistvi.petstocks.repository.UserRepo;
import com.joysistvi.petstocks.repository.UserRepoImpl;
import com.joysistvi.petstocks.service.CategoryService;
import com.joysistvi.petstocks.service.CategoryServiceImpl;
import com.joysistvi.petstocks.service.DispatchService;
import com.joysistvi.petstocks.service.DispatchServiceImpl;
import com.joysistvi.petstocks.service.InventoryService;
import com.joysistvi.petstocks.service.InventoryServiceImpl;
import com.joysistvi.petstocks.service.PetTypeService;
import com.joysistvi.petstocks.service.PetTypeServiceImpl;
import com.joysistvi.petstocks.service.ProductPetTypeService;
import com.joysistvi.petstocks.service.ProductPetTypeServiceImpl;
import com.joysistvi.petstocks.service.ProductService;
import com.joysistvi.petstocks.service.ProductServiceImpl;
import com.joysistvi.petstocks.service.RestockService;
import com.joysistvi.petstocks.service.RestockServiceImpl;
import com.joysistvi.petstocks.service.SupplierService;
import com.joysistvi.petstocks.service.SupplierServiceImpl;
import com.joysistvi.petstocks.service.UserService;
import com.joysistvi.petstocks.service.UserServiceImpl;

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

        ProductRepo productRepo = new ProductRepoImpl(dbConnection);
        ProductService productService = new ProductServiceImpl(productRepo, categoryRepo);
        ProductController productController = new ProductController(productService);

        ProductPetTypeRepo productPetTypeRepo = new ProductPetTypeRepoImpl(dbConnection);
        ProductPetTypeService productPetTypeService = new ProductPetTypeServiceImpl(
                productPetTypeRepo, productRepo, petTypeRepo);
        ProductPetTypeController productPetTypeController =
                new ProductPetTypeController(productPetTypeService);

        InventoryRepo inventoryRepo = new InventoryRepoImpl(dbConnection);
        InventoryService inventoryService = new InventoryServiceImpl(inventoryRepo, productRepo);
        InventoryController inventoryController = new InventoryController(inventoryService);

        RestockRepo restockRepo = new RestockRepoImpl(dbConnection);
        RestockService restockService = new RestockServiceImpl(
                restockRepo, inventoryRepo, supplierRepo);
        RestockController restockController = new RestockController(restockService);

        DispatchRepo dispatchRepo = new DispatchRepoImpl(dbConnection);
        DispatchService dispatchService = new DispatchServiceImpl(
                dispatchRepo, inventoryRepo, productRepo);
        DispatchController dispatchController = new DispatchController(dispatchService);

        UserRepo userRepo = new UserRepoImpl(dbConnection);
        UserService userService = new UserServiceImpl(userRepo);
        UserController userController = new UserController(userService);

        try (Scanner scanner = new Scanner(System.in)) {
            PetTypeView petTypeView = new PetTypeView(petTypeController, scanner);
            CategoryView categoryView = new CategoryView(categoryController, scanner);
            SupplierView supplierView = new SupplierView(supplierController, scanner);
            ProductView productView = new ProductView(productController, categoryController, scanner);
            ProductPetTypeView productPetTypeView = new ProductPetTypeView(
                    productPetTypeController, productController, petTypeController, scanner);
            InventoryView inventoryView = new InventoryView(
                    inventoryController, productController, scanner);
            RestockView restockView = new RestockView(
                    restockController, inventoryController, supplierController, scanner);
            DispatchView dispatchView = new DispatchView(
                    dispatchController, inventoryController, productController, scanner);
            UserView userView = new UserView(userController, scanner);
            StaffInventoryView staffInventoryView = new StaffInventoryView(
                    inventoryView, scanner);
            StockMovementView stockMovementView = new StockMovementView(
                    restockController, dispatchController, scanner);
            MonitorView monitorView = new MonitorView(inventoryView, scanner);
            StaffMainView staffMainView = new StaffMainView(
                    staffInventoryView, restockView, dispatchView,
                    stockMovementView, monitorView, scanner);
            LoginView loginView = new LoginView(scanner);

            runLoginMenu(loginView, staffMainView);
        }
    }

    private static void runLoginMenu(LoginView loginView, StaffMainView staffMainView) {
        while (true) {
            LoginView.Selection selection = loginView.promptForSelection();

            switch (selection) {
                case STAFF -> staffMainView.run();
                case ADMIN -> System.out.println("Administrator workflow selected.");
                case EXIT -> {
                    System.out.println("Exiting PetStock...");
                    return;
                }
            }
        }
    }

    private static void runDevelopmentMenu(Scanner scanner, PetTypeView petTypeView,
                                           CategoryView categoryView, SupplierView supplierView,
                                           ProductView productView,
                                           ProductPetTypeView productPetTypeView,
                                           InventoryView inventoryView,
                                           RestockView restockView,
                                           DispatchView dispatchView,
                                           UserView userView) {
        int choice;

        do {
            System.out.println();
            System.out.println("=".repeat(40));
            System.out.println("PetStock MVC Development Menu");
            System.out.println("=".repeat(40));
            System.out.println("1. Test Pet Type MVC");
            System.out.println("2. Test Category MVC");
            System.out.println("3. Test Supplier MVC");
            System.out.println("4. Test Product MVC");
            System.out.println("5. Test Product Pet Type MVC");
            System.out.println("6. Test Inventory MVC");
            System.out.println("7. Test Restock MVC");
            System.out.println("8. Test Dispatch MVC");
            System.out.println("9. Test User MVC");
            System.out.println("0. Exit");
            choice = promptInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> petTypeView.run();
                case 2 -> categoryView.run();
                case 3 -> supplierView.run();
                case 4 -> productView.run();
                case 5 -> productPetTypeView.run();
                case 6 -> inventoryView.run();
                case 7 -> restockView.run();
                case 8 -> dispatchView.run();
                case 9 -> userView.run();
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
