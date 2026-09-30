package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.model.ProductPetType;

import java.util.List;

public interface ProductPetTypeRepo {
    boolean assignPetTypeToProduct(ProductPetType productPetType);
    boolean removePetTypeFromProduct(int productId, int petTypeId);
    List<ProductPetType> getPetTypesByProductId(int productId);
    List<ProductPetType> getProductsByPetTypeId(int petTypeId);
    boolean relationshipExists(int productId, int petTypeId);
}
