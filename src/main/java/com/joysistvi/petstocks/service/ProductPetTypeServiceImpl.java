package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.PetType;
import com.joysistvi.petstocks.model.Product;
import com.joysistvi.petstocks.model.ProductPetType;
import com.joysistvi.petstocks.repository.PetTypeRepo;
import com.joysistvi.petstocks.repository.ProductPetTypeRepo;
import com.joysistvi.petstocks.repository.ProductRepo;

import java.util.List;

public class ProductPetTypeServiceImpl implements ProductPetTypeService {
    private final ProductPetTypeRepo productPetTypeRepo;
    private final ProductRepo productRepo;
    private final PetTypeRepo petTypeRepo;

    public ProductPetTypeServiceImpl(ProductPetTypeRepo productPetTypeRepo,
                                     ProductRepo productRepo, PetTypeRepo petTypeRepo) {
        this.productPetTypeRepo = productPetTypeRepo;
        this.productRepo = productRepo;
        this.petTypeRepo = petTypeRepo;
    }

    @Override
    public boolean assignPetTypeToProduct(int productId, int petTypeId) {
        if (!areValidIds(productId, petTypeId)) {
            return false;
        }

        Product product = productRepo.getProductById(productId);
        if (product == null) {
            System.out.println("Product not found.");
            return false;
        }
        if (product.isArchived()) {
            System.out.println("Cannot assign a pet type to an archived product.");
            return false;
        }

        PetType petType = petTypeRepo.getPetTypeById(petTypeId);
        if (petType == null) {
            System.out.println("Pet type not found.");
            return false;
        }
        if (petType.isArchived()) {
            System.out.println("Cannot assign an archived pet type.");
            return false;
        }

        if (productPetTypeRepo.relationshipExists(productId, petTypeId)) {
            System.out.println("This pet type is already assigned to the product.");
            return false;
        }

        return productPetTypeRepo.assignPetTypeToProduct(
                new ProductPetType(product, petType));
    }

    @Override
    public boolean removePetTypeFromProduct(int productId, int petTypeId) {
        if (!areValidIds(productId, petTypeId)) {
            return false;
        }
        if (!productPetTypeRepo.relationshipExists(productId, petTypeId)) {
            System.out.println("The selected product-pet type relationship does not exist.");
            return false;
        }

        return productPetTypeRepo.removePetTypeFromProduct(productId, petTypeId);
    }

    @Override
    public List<ProductPetType> getPetTypesByProductId(int productId) {
        if (!isValidId(productId, "product")) {
            return List.of();
        }
        if (productRepo.getProductById(productId) == null) {
            System.out.println("Product not found.");
            return List.of();
        }

        return productPetTypeRepo.getPetTypesByProductId(productId);
    }

    @Override
    public List<ProductPetType> getProductsByPetTypeId(int petTypeId) {
        if (!isValidId(petTypeId, "pet type")) {
            return List.of();
        }
        if (petTypeRepo.getPetTypeById(petTypeId) == null) {
            System.out.println("Pet type not found.");
            return List.of();
        }

        return productPetTypeRepo.getProductsByPetTypeId(petTypeId);
    }

    @Override
    public boolean relationshipExists(int productId, int petTypeId) {
        return areValidIds(productId, petTypeId)
                && productPetTypeRepo.relationshipExists(productId, petTypeId);
    }

    private boolean areValidIds(int productId, int petTypeId) {
        return isValidId(productId, "product") && isValidId(petTypeId, "pet type");
    }

    private boolean isValidId(int id, String entityName) {
        if (id <= 0) {
            System.out.println("Invalid " + entityName + " ID.");
            return false;
        }
        return true;
    }
}
