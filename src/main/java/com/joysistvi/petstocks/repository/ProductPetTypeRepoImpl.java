package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.model.Category;
import com.joysistvi.petstocks.model.PetType;
import com.joysistvi.petstocks.model.Product;
import com.joysistvi.petstocks.model.ProductPetType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductPetTypeRepoImpl implements ProductPetTypeRepo {
    private static final String RELATIONSHIP_SELECT =
            "SELECT ppt.id AS relationship_id, " +
                    "p.id AS product_id, p.name AS product_name, p.brand, " +
                    "p.description AS product_description, p.is_archived AS product_is_archived, " +
                    "c.id AS category_id, c.name AS category_name, " +
                    "c.description AS category_description, " +
                    "c.is_archived AS category_is_archived, " +
                    "pt.id AS pet_type_id, pt.name AS pet_type_name, " +
                    "pt.description AS pet_type_description, " +
                    "pt.is_archived AS pet_type_is_archived " +
                    "FROM product_pet_types ppt " +
                    "JOIN products p ON ppt.product_id = p.id " +
                    "JOIN categories c ON p.category_id = c.id " +
                    "JOIN pet_types pt ON ppt.pet_type_id = pt.id ";

    private final DBConnection dbConnection;

    public ProductPetTypeRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public boolean assignPetTypeToProduct(ProductPetType productPetType) {
        String query = "INSERT INTO product_pet_types (product_id, pet_type_id) VALUES (?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, productPetType.getProduct().getId());
            prep.setInt(2, productPetType.getPetType().getId());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Assign Pet Type To Product Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean removePetTypeFromProduct(int productId, int petTypeId) {
        String query = "DELETE FROM product_pet_types WHERE product_id = ? AND pet_type_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, productId);
            prep.setInt(2, petTypeId);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Remove Pet Type From Product Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public List<ProductPetType> getPetTypesByProductId(int productId) {
        List<ProductPetType> relationships = new ArrayList<>();
        String query = RELATIONSHIP_SELECT +
                "WHERE ppt.product_id = ? ORDER BY pt.name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, productId);

            try (ResultSet result = prep.executeQuery()) {
                while (result.next()) {
                    relationships.add(mapProductPetType(result));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get Pet Types By Product ID Error: " + e.getMessage());
        }

        return relationships;
    }

    @Override
    public List<ProductPetType> getProductsByPetTypeId(int petTypeId) {
        List<ProductPetType> relationships = new ArrayList<>();
        String query = RELATIONSHIP_SELECT +
                "WHERE ppt.pet_type_id = ? ORDER BY p.name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, petTypeId);

            try (ResultSet result = prep.executeQuery()) {
                while (result.next()) {
                    relationships.add(mapProductPetType(result));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get Products By Pet Type ID Error: " + e.getMessage());
        }

        return relationships;
    }

    @Override
    public boolean relationshipExists(int productId, int petTypeId) {
        String query = "SELECT 1 FROM product_pet_types " +
                "WHERE product_id = ? AND pet_type_id = ? LIMIT 1";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, productId);
            prep.setInt(2, petTypeId);

            try (ResultSet result = prep.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            System.err.println("Check Product Pet Type Relationship Error: " + e.getMessage());
        }

        return false;
    }

    private ProductPetType mapProductPetType(ResultSet result) throws SQLException {
        Category category = new Category(
                result.getInt("category_id"),
                result.getString("category_name"),
                result.getString("category_description"),
                result.getBoolean("category_is_archived")
        );

        Product product = new Product(
                result.getInt("product_id"),
                result.getString("product_name"),
                result.getString("brand"),
                result.getString("product_description"),
                result.getBoolean("product_is_archived"),
                category
        );

        PetType petType = new PetType(
                result.getInt("pet_type_id"),
                result.getString("pet_type_name"),
                result.getString("pet_type_description"),
                result.getBoolean("pet_type_is_archived")
        );

        return new ProductPetType(result.getInt("relationship_id"), product, petType);
    }
}
