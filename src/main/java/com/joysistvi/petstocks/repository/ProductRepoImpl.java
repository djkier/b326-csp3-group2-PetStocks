package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.model.Category;
import com.joysistvi.petstocks.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ProductRepoImpl implements ProductRepo {
    private static final String PRODUCT_SELECT =
            "SELECT p.id AS product_id, p.name AS product_name, p.brand, " +
                    "p.description AS product_description, p.is_archived AS product_is_archived, " +
                    "c.id AS category_id, c.name AS category_name, " +
                    "c.description AS category_description, " +
                    "c.is_archived AS category_is_archived " +
                    "FROM products p " +
                    "JOIN categories c ON p.category_id = c.id ";

    private final DBConnection dbConnection;

    public ProductRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Product> getAllProducts() {
        return getAllProducts("id");
    }

    @Override
    public List<Product> getAllProducts(String sortBy) {
        List<Product> products = new ArrayList<>();
        String sortColumn = getApprovedSortColumn(sortBy);
        String query = PRODUCT_SELECT +
                "WHERE p.is_archived = 0 ORDER BY " + sortColumn;

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                products.add(mapProduct(result));
            }
        } catch (SQLException e) {
            System.err.println("Get All Products Error: " + e.getMessage());
        }

        return products;
    }

    @Override
    public Product getProductById(int id) {
        String query = PRODUCT_SELECT + "WHERE p.id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);

            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return mapProduct(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Read Product By ID Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String query = PRODUCT_SELECT +
                "WHERE p.is_archived = 0 " +
                "AND (p.name LIKE ? OR p.brand LIKE ? OR p.description LIKE ? OR c.name LIKE ?) " +
                "ORDER BY p.name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            prep.setString(1, searchPattern);
            prep.setString(2, searchPattern);
            prep.setString(3, searchPattern);
            prep.setString(4, searchPattern);

            try (ResultSet result = prep.executeQuery()) {
                while (result.next()) {
                    products.add(mapProduct(result));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search Products Error: " + e.getMessage());
        }

        return products;
    }

    @Override
    public boolean createProduct(Product product) {
        String query = "INSERT INTO products " +
                "(name, brand, description, is_archived, category_id) VALUES (?, ?, ?, 0, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, product.getName());
            prep.setString(2, product.getBrand());
            setNullableDescription(prep, 3, product.getDescription());
            prep.setInt(4, product.getCategory().getId());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Create Product Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateProduct(Product product) {
        String query = "UPDATE products SET name = ?, brand = ?, description = ?, category_id = ? " +
                "WHERE id = ? AND is_archived = 0";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, product.getName());
            prep.setString(2, product.getBrand());
            setNullableDescription(prep, 3, product.getDescription());
            prep.setInt(4, product.getCategory().getId());
            prep.setInt(5, product.getId());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Product Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean archiveProduct(int id) {
        return updateArchiveStatus(id, false, true, "Archive Product Error: ");
    }

    @Override
    public boolean restoreProduct(int id) {
        return updateArchiveStatus(id, true, false, "Restore Product Error: ");
    }

    @Override
    public boolean deleteProduct(int id) {
        String query = "DELETE FROM products WHERE id = ? AND is_archived = 1";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete Product Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public List<Product> getAllArchivedProducts() {
        return getAllArchivedProducts("id");
    }

    @Override
    public List<Product> getAllArchivedProducts(String sortBy) {
        List<Product> products = new ArrayList<>();
        String sortColumn = getApprovedSortColumn(sortBy);
        String query = PRODUCT_SELECT +
                "WHERE p.is_archived = 1 ORDER BY " + sortColumn;

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                products.add(mapProduct(result));
            }
        } catch (SQLException e) {
            System.err.println("Get All Archived Products Error: " + e.getMessage());
        }

        return products;
    }

    private String getApprovedSortColumn(String sortBy) {
        return "name".equals(sortBy) ? "p.name" : "p.id";
    }

    private boolean updateArchiveStatus(int id, boolean currentStatus, boolean newStatus,
                                        String errorMessage) {
        String query = "UPDATE products SET is_archived = ? WHERE id = ? AND is_archived = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setBoolean(1, newStatus);
            prep.setInt(2, id);
            prep.setBoolean(3, currentStatus);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(errorMessage + e.getMessage());
        }

        return false;
    }

    private Product mapProduct(ResultSet result) throws SQLException {
        Category category = new Category(
                result.getInt("category_id"),
                result.getString("category_name"),
                result.getString("category_description"),
                result.getBoolean("category_is_archived")
        );

        return new Product(
                result.getInt("product_id"),
                result.getString("product_name"),
                result.getString("brand"),
                result.getString("product_description"),
                result.getBoolean("product_is_archived"),
                category
        );
    }

    private void setNullableDescription(PreparedStatement prep, int parameterIndex,
                                        String description) throws SQLException {
        if (description == null) {
            prep.setNull(parameterIndex, Types.VARCHAR);
        } else {
            prep.setString(parameterIndex, description);
        }
    }
}
