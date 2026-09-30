package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.model.Category;
import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.model.Product;
import com.joysistvi.petstocks.model.Restock;
import com.joysistvi.petstocks.model.Supplier;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestockRepoImpl implements RestockRepo {
    private static final String RESTOCK_SELECT =
            "SELECT r.id AS restock_id, r.datetime_delivered, r.quantity_delivered, " +
                    "r.user_id, u.username, " +
                    "i.id AS inventory_id, i.quantity AS inventory_quantity, " +
                    "i.expiration, i.batch_code, i.remark, " +
                    "p.id AS product_id, p.name AS product_name, p.brand, " +
                    "p.description AS product_description, " +
                    "p.is_archived AS product_is_archived, " +
                    "c.id AS category_id, c.name AS category_name, " +
                    "c.description AS category_description, " +
                    "c.is_archived AS category_is_archived, " +
                    "s.id AS supplier_id, s.name AS supplier_name, s.address, " +
                    "s.contact_number, s.email, s.is_archived AS supplier_is_archived " +
                    "FROM restocks r " +
                    "JOIN inventories i ON r.inventory_id = i.id " +
                    "JOIN products p ON i.product_id = p.id " +
                    "JOIN categories c ON p.category_id = c.id " +
                    "JOIN suppliers s ON r.supplier_id = s.id " +
                    "JOIN users u ON r.user_id = u.id ";

    private final DBConnection dbConnection;

    public RestockRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Restock> getAllRestocks() {
        return getAllRestocks("date");
    }

    @Override
    public List<Restock> getAllRestocks(String sortBy) {
        List<Restock> restocks = new ArrayList<>();
        String query = RESTOCK_SELECT + "ORDER BY " + getApprovedSortColumn(sortBy);

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                restocks.add(mapRestock(result));
            }
        } catch (SQLException e) {
            System.err.println("Get Restock History Error: " + e.getMessage());
        }

        return restocks;
    }

    @Override
    public Restock getRestockById(int id) {
        String query = RESTOCK_SELECT + "WHERE r.id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);
            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return mapRestock(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Read Restock By ID Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Restock> getRestocksByInventoryId(int inventoryId) {
        return getRestocksById(
                "WHERE r.inventory_id = ? ORDER BY r.datetime_delivered DESC", inventoryId,
                "Get Restocks By Inventory Error: ");
    }

    @Override
    public List<Restock> getRestocksBySupplierId(int supplierId) {
        return getRestocksById(
                "WHERE r.supplier_id = ? ORDER BY r.datetime_delivered DESC", supplierId,
                "Get Restocks By Supplier Error: ");
    }

    @Override
    public List<Restock> getRestocksByUserId(int userId) {
        return getRestocksById(
                "WHERE r.user_id = ? ORDER BY r.datetime_delivered DESC", userId,
                "Get Restocks By User Error: ");
    }

    @Override
    public List<Restock> getRestocksByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Restock> restocks = new ArrayList<>();
        String query = RESTOCK_SELECT +
                "WHERE r.datetime_delivered >= ? AND r.datetime_delivered < ? " +
                "ORDER BY r.datetime_delivered DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
            prep.setTimestamp(2, Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));
            addResults(prep, restocks);
        } catch (SQLException e) {
            System.err.println("Get Restocks By Date Range Error: " + e.getMessage());
        }

        return restocks;
    }

    @Override
    public Map<Integer, String> getAllUsers() {
        Map<Integer, String> users = new LinkedHashMap<>();
        String query = "SELECT id, username FROM users ORDER BY username";

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                users.put(result.getInt("id"), result.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("Get Users For Restock Error: " + e.getMessage());
        }

        return users;
    }

    @Override
    public String getUsernameById(int userId) {
        String query = "SELECT username FROM users WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, userId);
            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return result.getString("username");
                }
            }
        } catch (SQLException e) {
            System.err.println("Read Restock User Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean recordStockIn(Restock restock) {
        String insertRestock = "INSERT INTO restocks " +
                "(inventory_id, supplier_id, datetime_delivered, quantity_delivered, user_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        String increaseInventory =
                "UPDATE inventories SET quantity = quantity + ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement insert = conn.prepareStatement(
                    insertRestock, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement update = conn.prepareStatement(increaseInventory)) {

                insert.setInt(1, restock.getInventory().getId());
                insert.setInt(2, restock.getSupplier().getId());
                insert.setTimestamp(3, Timestamp.valueOf(restock.getDatetimeDelivered()));
                insert.setInt(4, restock.getQuantityDelivered());
                insert.setInt(5, restock.getUserId());

                if (insert.executeUpdate() != 1) {
                    throw new SQLException("Restock record was not created.");
                }

                update.setInt(1, restock.getQuantityDelivered());
                update.setInt(2, restock.getInventory().getId());
                if (update.executeUpdate() != 1) {
                    throw new SQLException("Inventory quantity was not updated.");
                }

                try (ResultSet keys = insert.getGeneratedKeys()) {
                    if (keys.next()) {
                        restock.setId(keys.getInt(1));
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                rollback(conn);
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Record Stock In Error: " + e.getMessage());
        }

        return false;
    }

    private List<Restock> getRestocksById(String condition, int id, String errorMessage) {
        List<Restock> restocks = new ArrayList<>();
        String query = RESTOCK_SELECT + condition;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);
            addResults(prep, restocks);
        } catch (SQLException e) {
            System.err.println(errorMessage + e.getMessage());
        }

        return restocks;
    }

    private void addResults(PreparedStatement prep, List<Restock> restocks) throws SQLException {
        try (ResultSet result = prep.executeQuery()) {
            while (result.next()) {
                restocks.add(mapRestock(result));
            }
        }
    }

    private Restock mapRestock(ResultSet result) throws SQLException {
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

        Date sqlExpiration = result.getDate("expiration");
        Inventory inventory = new Inventory(
                result.getInt("inventory_id"),
                product,
                result.getInt("inventory_quantity"),
                sqlExpiration == null ? null : sqlExpiration.toLocalDate(),
                result.getString("batch_code"),
                result.getString("remark")
        );

        Supplier supplier = new Supplier(
                result.getInt("supplier_id"),
                result.getString("supplier_name"),
                result.getString("address"),
                result.getString("contact_number"),
                result.getString("email"),
                result.getBoolean("supplier_is_archived")
        );

        return new Restock(
                result.getInt("restock_id"),
                inventory,
                supplier,
                result.getTimestamp("datetime_delivered").toLocalDateTime(),
                result.getInt("quantity_delivered"),
                result.getInt("user_id"),
                result.getString("username")
        );
    }

    private String getApprovedSortColumn(String sortBy) {
        return switch (sortBy) {
            case "product" -> "p.name, r.datetime_delivered DESC";
            case "supplier" -> "s.name, r.datetime_delivered DESC";
            case "user" -> "u.username, r.datetime_delivered DESC";
            case "date" -> "r.datetime_delivered DESC";
            default -> "r.id";
        };
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException rollbackError) {
            System.err.println("Restock Rollback Error: " + rollbackError.getMessage());
        }
    }
}
