package com.joysistvi.petstocks;

import com.joysistvi.petstocks.cliview.modelview.CategoryView;
import com.joysistvi.petstocks.cliview.modelview.DispatchView;
import com.joysistvi.petstocks.cliview.modelview.InventoryView;
import com.joysistvi.petstocks.cliview.modelview.PetTypeView;
import com.joysistvi.petstocks.cliview.modelview.ProductView;
import com.joysistvi.petstocks.cliview.modelview.RestockView;
import com.joysistvi.petstocks.cliview.modelview.SupplierView;
import com.joysistvi.petstocks.cliview.modelview.UserView;
import com.joysistvi.petstocks.cliview.publicview.AdminMainView;
import com.joysistvi.petstocks.cliview.publicview.AdminManagementView;
import com.joysistvi.petstocks.cliview.publicview.LandingView;
import com.joysistvi.petstocks.cliview.publicview.LoginView;
import com.joysistvi.petstocks.cliview.publicview.MonitorView;
import com.joysistvi.petstocks.cliview.publicview.RegistrationView;
import com.joysistvi.petstocks.cliview.publicview.StaffInventoryView;
import com.joysistvi.petstocks.cliview.publicview.StaffMainView;
import com.joysistvi.petstocks.cliview.publicview.StockMovementView;
import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.controller.CategoryController;
import com.joysistvi.petstocks.controller.DispatchController;
import com.joysistvi.petstocks.controller.InventoryController;
import com.joysistvi.petstocks.controller.PetTypeController;
import com.joysistvi.petstocks.controller.ProductController;
import com.joysistvi.petstocks.controller.RestockController;
import com.joysistvi.petstocks.controller.SupplierController;
import com.joysistvi.petstocks.controller.UserController;
import com.joysistvi.petstocks.model.User;
import com.joysistvi.petstocks.repository.CategoryRepo;
import com.joysistvi.petstocks.repository.CategoryRepoImpl;
import com.joysistvi.petstocks.repository.DispatchRepo;
import com.joysistvi.petstocks.repository.DispatchRepoImpl;
import com.joysistvi.petstocks.repository.InventoryRepo;
import com.joysistvi.petstocks.repository.InventoryRepoImpl;
import com.joysistvi.petstocks.repository.PetTypeRepo;
import com.joysistvi.petstocks.repository.PetTypeRepoImpl;
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
            AdminManagementView adminManagementView = new AdminManagementView(
                    productView, categoryView, supplierView, petTypeView, userView, scanner);
            StaffMainView staffMainView = new StaffMainView(
                    staffInventoryView, restockView, dispatchView,
                    stockMovementView, monitorView, scanner);
            AdminMainView adminMainView = new AdminMainView(
                    staffInventoryView, restockView, dispatchView,
                    stockMovementView, monitorView, adminManagementView, scanner);
            LoginView loginView = new LoginView(userController, scanner);
            RegistrationView registrationView = new RegistrationView(userController, scanner);
            LandingView landingView = new LandingView(scanner);

            runApplication(
                    landingView, loginView, registrationView, staffMainView, adminMainView);
        }
    }

    private static void runApplication(LandingView landingView, LoginView loginView,
                                       RegistrationView registrationView,
                                       StaffMainView staffMainView,
                                       AdminMainView adminMainView) {
        while (true) {
            LandingView.Selection selection = landingView.promptForSelection();

            switch (selection) {
                case LOGIN -> runLoginWorkflow(loginView, staffMainView, adminMainView);
                case REGISTER -> registrationView.run();
                case EXIT -> {
                    System.out.println("Exiting PetStock...");
                    return;
                }
            }
        }
    }

    private static void runLoginWorkflow(LoginView loginView,
                                         StaffMainView staffMainView,
                                         AdminMainView adminMainView) {
        while (true) {
            User currentUser = loginView.promptForLogin();
            if (currentUser == null) {
                return;
            }

            loginView.showWelcome(currentUser);
            switch (currentUser.getRole()) {
                case "STAFF" -> staffMainView.run(currentUser);
                case "ADMIN" -> adminMainView.run(currentUser);
                default -> System.out.println("Invalid username or password.");
            }

            currentUser = null;
        }
    }
}
