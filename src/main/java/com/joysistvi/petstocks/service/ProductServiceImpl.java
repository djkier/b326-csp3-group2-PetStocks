package com.joysistvi.petstocks.service;

import com.joysistvi.petstocks.model.Category;
import com.joysistvi.petstocks.model.Product;
import com.joysistvi.petstocks.repository.CategoryRepo;
import com.joysistvi.petstocks.repository.ProductRepo;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_BRAND_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 250;

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;

    public ProductServiceImpl(ProductRepo productRepo, CategoryRepo categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public List<Product> getAllProducts() {
        return getAllProducts("id");
    }

    @Override
    public List<Product> getAllProducts(String sortBy) {
        return productRepo.getAllProducts(getApprovedSortValue(sortBy));
    }

    @Override
    public Product getProductById(int id) {
        if (!isValidId(id, "find")) {
            return null;
        }

        Product product = productRepo.getProductById(id);
        if (product == null) {
            System.out.println("Product not found.");
        }
        return product;
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return List.of();
        }

        return productRepo.searchProducts(keyword.trim());
    }

    @Override
    public boolean createProduct(Product product) {
        if (!validateProduct(product, false) || !assignActiveCategory(product)) {
            return false;
        }

        normalizeProduct(product);
        return productRepo.createProduct(product);
    }

    @Override
    public boolean updateProduct(Product product) {
        if (!validateProduct(product, true) || !assignActiveCategory(product)) {
            return false;
        }

        normalizeProduct(product);
        return productRepo.updateProduct(product);
    }

    @Override
    public boolean archiveProduct(int id) {
        return isValidId(id, "archive") && productRepo.archiveProduct(id);
    }

    @Override
    public boolean restoreProduct(int id) {
        if (!isValidId(id, "restore")) {
            return false;
        }

        Product product = productRepo.getProductById(id);
        if (product == null) {
            System.out.println("Product not found.");
            return false;
        }
        if (product.getCategory().isArchived()) {
            System.out.println("Restore the product's category before restoring this product.");
            return false;
        }

        return productRepo.restoreProduct(id);
    }

    @Override
    public boolean deleteProduct(int id) {
        return isValidId(id, "delete") && productRepo.deleteProduct(id);
    }

    @Override
    public List<Product> getAllArchivedProducts() {
        return getAllArchivedProducts("id");
    }

    @Override
    public List<Product> getAllArchivedProducts(String sortBy) {
        return productRepo.getAllArchivedProducts(getApprovedSortValue(sortBy));
    }

    private String getApprovedSortValue(String sortBy) {
        return "name".equals(sortBy) ? "name" : "id";
    }

    private boolean validateProduct(Product product, boolean requireId) {
        if (product == null) {
            System.out.println("Product object cannot be null.");
            return false;
        }
        if (requireId && product.getId() <= 0) {
            System.out.println("Invalid product ID for update.");
            return false;
        }
        if (!validateRequiredField(product.getName(), "Product name", MAX_NAME_LENGTH)) {
            return false;
        }
        if (!validateRequiredField(product.getBrand(), "Product brand", MAX_BRAND_LENGTH)) {
            return false;
        }
        if (product.getDescription() != null
                && product.getDescription().trim().length() > MAX_DESCRIPTION_LENGTH) {
            System.out.println("Product description cannot exceed 250 characters.");
            return false;
        }
        if (product.getCategory() == null || product.getCategory().getId() <= 0) {
            System.out.println("A valid category is required.");
            return false;
        }
        return true;
    }

    private boolean validateRequiredField(String value, String fieldName, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            System.out.println(fieldName + " is required.");
            return false;
        }
        if (value.trim().length() > maxLength) {
            System.out.println(fieldName + " cannot exceed " + maxLength + " characters.");
            return false;
        }
        return true;
    }

    private boolean assignActiveCategory(Product product) {
        Category category = categoryRepo.getCategoryById(product.getCategory().getId());
        if (category == null) {
            System.out.println("Selected category was not found.");
            return false;
        }
        if (category.isArchived()) {
            System.out.println("Selected category is archived.");
            return false;
        }

        product.setCategory(category);
        return true;
    }

    private void normalizeProduct(Product product) {
        product.setName(product.getName().trim());
        product.setBrand(product.getBrand().trim());

        if (product.getDescription() != null) {
            String description = product.getDescription().trim();
            product.setDescription(description.isEmpty() ? null : description);
        }
    }

    private boolean isValidId(int id, String operation) {
        if (id <= 0) {
            System.out.println("Invalid product ID for " + operation + ".");
            return false;
        }
        return true;
    }
}
