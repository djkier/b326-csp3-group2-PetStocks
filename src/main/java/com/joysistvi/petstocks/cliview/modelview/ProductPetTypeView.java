package com.joysistvi.petstocks.cliview.modelview;

import com.joysistvi.petstocks.controller.PetTypeController;
import com.joysistvi.petstocks.controller.ProductController;
import com.joysistvi.petstocks.controller.ProductPetTypeController;
import com.joysistvi.petstocks.model.PetType;
import com.joysistvi.petstocks.model.Product;
import com.joysistvi.petstocks.model.ProductPetType;
import com.joysistvi.petstocks.utility.CliViewUtility;
import com.joysistvi.petstocks.utility.InputUtility;

import java.util.List;
import java.util.Scanner;

public class ProductPetTypeView {
    private final ProductPetTypeController productPetTypeController;
    private final ProductController productController;
    private final PetTypeController petTypeController;
    private final Scanner scanner;

    public ProductPetTypeView(ProductPetTypeController productPetTypeController,
                              ProductController productController,
                              PetTypeController petTypeController, Scanner scanner) {
        this.productPetTypeController = productPetTypeController;
        this.productController = productController;
        this.petTypeController = petTypeController;
        this.scanner = scanner;
    }

    public void run() {
        int choice;

        do {
            printMenu();
            choice = InputUtility.readInt(scanner, "Choice: ");

            switch (choice) {
                case 1 -> CliViewUtility.pauseAfter(scanner, assignPetTypeToProduct());
                case 2 -> CliViewUtility.pauseAfter(scanner, removePetTypeFromProduct());
                case 3 -> CliViewUtility.pauseAfter(scanner, viewPetTypesByProduct());
                case 4 -> CliViewUtility.pauseAfter(scanner, viewProductsByPetType());
                case 5 -> CliViewUtility.pauseAfter(scanner, checkRelationship());
                case 0 -> System.out.println("Returning to the development menu...");
                default -> {
                    System.out.println("Invalid menu selection.");
                    InputUtility.pressEnterToContinue(scanner, "Press Enter to continue...");
                }
            }
        } while (choice != 0);
    }

    private void printMenu() {
        CliViewUtility.showHeader("Product Pet Type Management");
        System.out.println("1. Assign Pet Type to Product");
        System.out.println("2. Remove Pet Type from Product");
        System.out.println("3. View Pet Types Assigned to a Product");
        System.out.println("4. View Products Assigned to a Pet Type");
        System.out.println("5. Check Product-Pet Type Relationship");
        System.out.println("0. Back");
    }

    private boolean assignPetTypeToProduct() {
        CliViewUtility.showHeader("Assign Pet Type to Product");

        Product product = selectActiveProduct();
        if (product == null) {
            return false;
        }

        PetType petType = selectActivePetType();
        if (petType == null) {
            return false;
        }

        boolean isSuccess = productPetTypeController.handleAssignPetTypeToProduct(
                product.getId(), petType.getId());
        System.out.println(isSuccess
                ? petType.getName() + " assigned to " + product.getName() + " successfully."
                : "Failed to assign the pet type to the product.");
        return true;
    }

    private boolean removePetTypeFromProduct() {
        CliViewUtility.showHeader("Remove Pet Type from Product");
        int productId = InputUtility.readInt(scanner, "Product ID (0 to cancel): ");
        if (productId == 0) {
            return false;
        }

        List<ProductPetType> relationships =
                productPetTypeController.handleViewPetTypesByProductId(productId);
        if (relationships.isEmpty()) {
            System.out.println("No pet types are assigned to that product.");
            return true;
        }

        CliViewUtility.browsePages(
                relationships, scanner, this::printRelationships);
        int petTypeId = InputUtility.readInt(scanner, "Pet type ID to remove (0 to cancel): ");
        if (petTypeId == 0) {
            return false;
        }

        boolean isSuccess = productPetTypeController.handleRemovePetTypeFromProduct(
                productId, petTypeId);
        System.out.println(isSuccess
                ? "Pet type removed from the product successfully."
                : "Failed to remove the pet type from the product.");
        return true;
    }

    private boolean viewPetTypesByProduct() {
        CliViewUtility.showHeader("Pet Types Assigned to Product");
        int productId = InputUtility.readInt(scanner, "Product ID (0 to cancel): ");
        if (productId == 0) {
            return false;
        }

        List<ProductPetType> relationships =
                productPetTypeController.handleViewPetTypesByProductId(productId);
        CliViewUtility.browsePages(
                relationships, scanner, this::printRelationships);
        return true;
    }

    private boolean viewProductsByPetType() {
        CliViewUtility.showHeader("Products Assigned to Pet Type");
        int petTypeId = InputUtility.readInt(scanner, "Pet type ID (0 to cancel): ");
        if (petTypeId == 0) {
            return false;
        }

        List<ProductPetType> relationships =
                productPetTypeController.handleViewProductsByPetTypeId(petTypeId);
        CliViewUtility.browsePages(
                relationships, scanner, this::printRelationships);
        return true;
    }

    private boolean checkRelationship() {
        CliViewUtility.showHeader("Check Product-Pet Type Relationship");
        int productId = InputUtility.readInt(scanner, "Product ID (0 to cancel): ");
        if (productId == 0) {
            return false;
        }

        int petTypeId = InputUtility.readInt(scanner, "Pet type ID (0 to cancel): ");
        if (petTypeId == 0) {
            return false;
        }

        boolean exists = productPetTypeController.handleCheckRelationship(productId, petTypeId);
        System.out.println(exists
                ? "The pet type is assigned to the product."
                : "The pet type is not assigned to the product.");
        return true;
    }

    private Product selectActiveProduct() {
        List<Product> products = productController.handleViewAllProducts("id");
        if (products.isEmpty()) {
            System.out.println("No active products are available.");
            return null;
        }

        CliViewUtility.browsePages(products, scanner, this::printProductChoices);
        while (true) {
            int productId = InputUtility.readInt(scanner, "Product ID (0 to cancel): ");
            if (productId == 0) {
                return null;
            }

            for (Product product : products) {
                if (product.getId() == productId) {
                    return product;
                }
            }
            System.out.println("Choose an ID from the active product list.");
        }
    }

    private PetType selectActivePetType() {
        List<PetType> petTypes = petTypeController.handleViewAllPetTypes("id");
        if (petTypes.isEmpty()) {
            System.out.println("No active pet types are available.");
            return null;
        }

        CliViewUtility.browsePages(petTypes, scanner, this::printPetTypeChoices);
        while (true) {
            int petTypeId = InputUtility.readInt(scanner, "Pet type ID (0 to cancel): ");
            if (petTypeId == 0) {
                return null;
            }

            for (PetType petType : petTypes) {
                if (petType.getId() == petTypeId) {
                    return petType;
                }
            }
            System.out.println("Choose an ID from the active pet type list.");
        }
    }

    private void printProductChoices(List<Product> products) {
        System.out.println("Active Products");
        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27)
                + "+" + "-".repeat(22) + "+" + "-".repeat(27) + "+";
        System.out.println(border);
        System.out.printf("| %-4s | %-25s | %-20s | %-25s |%n",
                "ID", "Product", "Brand", "Category");
        System.out.println(border);
        for (Product product : products) {
            System.out.printf("| %-4d | %-25s | %-20s | %-25s |%n",
                    product.getId(),
                    CliViewUtility.truncate(product.getName(), 25),
                    CliViewUtility.truncate(product.getBrand(), 20),
                    CliViewUtility.truncate(product.getCategory().getName(), 25));
        }
        System.out.println(border);
    }

    private void printPetTypeChoices(List<PetType> petTypes) {
        System.out.println();
        System.out.println("Active Pet Types");
        String border = "+" + "-".repeat(6) + "+" + "-".repeat(37) + "+";
        System.out.println(border);
        System.out.printf("| %-4s | %-35s |%n", "ID", "Pet Type");
        System.out.println(border);
        for (PetType petType : petTypes) {
            System.out.printf("| %-4d | %-35s |%n",
                    petType.getId(), CliViewUtility.truncate(petType.getName(), 35));
        }
        System.out.println(border);
    }

    public void printRelationships(List<ProductPetType> relationships) {
        String border = "+" + "-".repeat(6) + "+" + "-".repeat(27)
                + "+" + "-".repeat(22) + "+" + "-".repeat(27)
                + "+" + "-".repeat(27) + "+" + "-".repeat(12) + "+" + "-".repeat(12) + "+";
        System.out.println(border);
        System.out.printf("| %-4s | %-25s | %-20s | %-25s | %-25s | %-10s | %-10s |%n",
                "ID", "Product", "Brand", "Category", "Pet Type",
                "Product", "Pet Type");
        System.out.printf("| %-4s | %-25s | %-20s | %-25s | %-25s | %-10s | %-10s |%n",
                "", "", "", "", "", "Status", "Status");
        System.out.println(border);

        if (relationships.isEmpty()) {
            System.out.println("No product-pet type relationships found.");
            System.out.println(border);
            return;
        }

        for (ProductPetType relationship : relationships) {
            Product product = relationship.getProduct();
            PetType petType = relationship.getPetType();
            System.out.printf("| %-4d | %-25s | %-20s | %-25s | %-25s | %-10s | %-10s |%n",
                    relationship.getId(),
                    CliViewUtility.truncate(product.getName(), 25),
                    CliViewUtility.truncate(product.getBrand(), 20),
                    CliViewUtility.truncate(product.getCategory().getName(), 25),
                    CliViewUtility.truncate(petType.getName(), 25),
                    CliViewUtility.formatArchiveStatus(product.isArchived()),
                    CliViewUtility.formatArchiveStatus(petType.isArchived()));
        }
        System.out.println(border);
    }

}
