package com.joysistvi.petstocks.controller;

import com.joysistvi.petstocks.model.Product;
import com.joysistvi.petstocks.service.ProductService;

import java.util.List;

public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public List<Product> handleViewAllProducts() {
        return productService.getAllProducts();
    }

    public List<Product> handleViewAllProducts(String sortBy) {
        return productService.getAllProducts(sortBy);
    }

    public Product handleGetProductById(int id) {
        return productService.getProductById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return productService.searchProducts(keyword);
    }

    public boolean handleCreateProduct(Product product) {
        return productService.createProduct(product);
    }

    public boolean handleUpdateProduct(Product product) {
        return productService.updateProduct(product);
    }

    public boolean handleArchiveProduct(int id) {
        return productService.archiveProduct(id);
    }

    public boolean handleRestoreProduct(int id) {
        return productService.restoreProduct(id);
    }

    public boolean handleDeleteProduct(int id) {
        return productService.deleteProduct(id);
    }

    public List<Product> handleViewArchivedProducts() {
        return productService.getAllArchivedProducts();
    }

    public List<Product> handleViewArchivedProducts(String sortBy) {
        return productService.getAllArchivedProducts(sortBy);
    }
}
