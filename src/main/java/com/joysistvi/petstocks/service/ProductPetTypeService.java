package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.ProductPetType;

import java.util.List;

public interface ProductPetTypeService {
    boolean assignPetTypeToProduct(int productId, int petTypeId);
    boolean removePetTypeFromProduct(int productId, int petTypeId);
    List<ProductPetType> getPetTypesByProductId(int productId);
    List<ProductPetType> getProductsByPetTypeId(int petTypeId);
    boolean relationshipExists(int productId, int petTypeId);
}
