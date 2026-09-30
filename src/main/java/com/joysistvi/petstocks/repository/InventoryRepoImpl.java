package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.model.Category;
import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.model.Product;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepoImpl implements InventoryRepo {
    private static final String INVENTORY_SELECT =
            "SELECT i.id AS inventory_id, i.quantity, i.expiration, i.batch_code, i.remark, " +
                    "p.id AS product_id, p.name AS product_name, p.brand, " +
                    "p.description AS product_description, p.is_archived AS product_is_archived, " +
                    "c.id AS category_id, c.name AS category_name, " +
                    "c.description AS category_description, " +
                    "c.is_archived AS category_is_archived " +
                    "FROM inventories i " +
                    "JOIN products p ON i.product_id = p.id " +
                    "JOIN categories c ON p.category_id = c.id ";

    private final DBConnection dbConnection;

    public InventoryRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Inventory> getAllInventory() {
        return getAllInventory("id");
    }

    @Override
    public List<Inventory> getAllInventory(String sortBy) {
        List<Inventory> inventory = new ArrayList<>();
        String sortColumn = getApprovedSortColumn(sortBy);
        String query = INVENTORY_SELECT + "ORDER BY " + sortColumn;

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                inventory.add(mapInventory(result));
            }
        } catch (SQLException e) {
            System.err.println("Get All Inventory Error: " + e.getMessage());
        }

        return inventory;
    }

    @Override
    public Inventory getInventoryById(int id) {
        String query = INVENTORY_SELECT + "WHERE i.id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);

            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return mapInventory(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Read Inventory By ID Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Inventory> getInventoryByProductId(int productId) {
        List<Inventory> inventory = new ArrayList<>();
        String query = INVENTORY_SELECT +
                "WHERE i.product_id = ? ORDER BY i.expiration, i.batch_code";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, productId);
            addResults(prep, inventory);
        } catch (SQLException e) {
            System.err.println("Get Inventory By Product ID Error: " + e.getMessage());
        }

        return inventory;
    }

    @Override
    public List<Inventory> searchInventory(String keyword) {
        List<Inventory> inventory = new ArrayList<>();
        String query = INVENTORY_SELECT +
                "WHERE i.batch_code LIKE ? OR i.remark LIKE ? " +
                "OR p.name LIKE ? OR p.brand LIKE ? " +
                "ORDER BY p.name, i.expiration";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            prep.setString(1, searchPattern);
            prep.setString(2, searchPattern);
            prep.setString(3, searchPattern);
            prep.setString(4, searchPattern);
            addResults(prep, inventory);
        } catch (SQLException e) {
            System.err.println("Search Inventory Error: " + e.getMessage());
        }

        return inventory;
    }

    @Override
    public List<Inventory> getLowStockInventory(int maximumQuantity) {
        List<Inventory> inventory = new ArrayList<>();
        String query = INVENTORY_SELECT +
                "WHERE i.quantity <= ? ORDER BY i.quantity, p.name, i.expiration";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, maximumQuantity);
            addResults(prep, inventory);
        } catch (SQLException e) {
            System.err.println("Get Low Stock Inventory Error: " + e.getMessage());
        }

        return inventory;
    }

    @Override
    public List<Inventory> getExpiringInventory(LocalDate startDate, LocalDate endDate) {
        List<Inventory> inventory = new ArrayList<>();
        String query = INVENTORY_SELECT +
                "WHERE i.expiration BETWEEN ? AND ? ORDER BY i.expiration, p.name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setDate(1, Date.valueOf(startDate));
            prep.setDate(2, Date.valueOf(endDate));
            addResults(prep, inventory);
        } catch (SQLException e) {
            System.err.println("Get Expiring Inventory Error: " + e.getMessage());
        }

        return inventory;
    }

    @Override
    public List<Inventory> getInventoryByBatchCode(String batchCode) {
        List<Inventory> inventory = new ArrayList<>();
        String query = INVENTORY_SELECT +
                "WHERE i.batch_code = ? ORDER BY i.expiration, p.name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, batchCode);
            addResults(prep, inventory);
        } catch (SQLException e) {
            System.err.println("Get Inventory By Batch Code Error: " + e.getMessage());
        }

        return inventory;
    }

    @Override
    public boolean createInventory(Inventory inventory) {
        String query = "INSERT INTO inventories " +
                "(product_id, quantity, expiration, batch_code, remark) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, inventory.getProduct().getId());
            prep.setInt(2, inventory.getQuantity());
            prep.setDate(3, Date.valueOf(inventory.getExpiration()));
            prep.setString(4, inventory.getBatchCode());
            setNullableRemark(prep, 5, inventory.getRemark());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Create Inventory Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateInventory(Inventory inventory) {
        String query = "UPDATE inventories SET product_id = ?, quantity = ?, expiration = ?, " +
                "batch_code = ?, remark = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, inventory.getProduct().getId());
            prep.setInt(2, inventory.getQuantity());
            prep.setDate(3, Date.valueOf(inventory.getExpiration()));
            prep.setString(4, inventory.getBatchCode());
            setNullableRemark(prep, 5, inventory.getRemark());
            prep.setInt(6, inventory.getId());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Inventory Error: " + e.getMessage());
        }

        return false;
    }

    private String getApprovedSortColumn(String sortBy) {
        return switch (sortBy) {
            case "product" -> "p.name";
            case "quantity" -> "i.quantity";
            case "expiration" -> "i.expiration";
            default -> "i.id";
        };
    }

    private void addResults(PreparedStatement prep, List<Inventory> inventory) throws SQLException {
        try (ResultSet result = prep.executeQuery()) {
            while (result.next()) {
                inventory.add(mapInventory(result));
            }
        }
    }

    private Inventory mapInventory(ResultSet result) throws SQLException {
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

        return new Inventory(
                result.getInt("inventory_id"),
                product,
                result.getInt("quantity"),
                result.getDate("expiration").toLocalDate(),
                result.getString("batch_code"),
                result.getString("remark")
        );
    }

    private void setNullableRemark(PreparedStatement prep, int parameterIndex,
                                   String remark) throws SQLException {
        if (remark == null) {
            prep.setNull(parameterIndex, Types.VARCHAR);
        } else {
            prep.setString(parameterIndex, remark);
        }
    }
}
