package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.model.Product;

import java.util.List;

public interface ProductRepo {
    List<Product> getAllProducts();
    List<Product> getAllProducts(String sortBy);
    Product getProductById(int id);
    List<Product> searchProducts(String keyword);
    boolean createProduct(Product product);
    boolean updateProduct(Product product);
    boolean archiveProduct(int id);
    boolean restoreProduct(int id);
    boolean deleteProduct(int id);
    List<Product> getAllArchivedProducts();
    List<Product> getAllArchivedProducts(String sortBy);
}
