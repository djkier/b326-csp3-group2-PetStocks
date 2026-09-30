package com.joysistvi.petstocks.controller;

import com.joysistvi.petstocks.model.ProductPetType;
import com.joysistvi.petstocks.service.ProductPetTypeService;

import java.util.List;

public class ProductPetTypeController {
    private final ProductPetTypeService productPetTypeService;

    public ProductPetTypeController(ProductPetTypeService productPetTypeService) {
        this.productPetTypeService = productPetTypeService;
    }

    public boolean handleAssignPetTypeToProduct(int productId, int petTypeId) {
        return productPetTypeService.assignPetTypeToProduct(productId, petTypeId);
    }

    public boolean handleRemovePetTypeFromProduct(int productId, int petTypeId) {
        return productPetTypeService.removePetTypeFromProduct(productId, petTypeId);
    }

    public List<ProductPetType> handleViewPetTypesByProductId(int productId) {
        return productPetTypeService.getPetTypesByProductId(productId);
    }

    public List<ProductPetType> handleViewProductsByPetTypeId(int petTypeId) {
        return productPetTypeService.getProductsByPetTypeId(petTypeId);
    }

    public boolean handleCheckRelationship(int productId, int petTypeId) {
        return productPetTypeService.relationshipExists(productId, petTypeId);
    }
}
