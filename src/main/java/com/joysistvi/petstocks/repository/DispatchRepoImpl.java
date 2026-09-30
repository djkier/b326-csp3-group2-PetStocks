package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.model.Category;
import com.joysistvi.petstocks.model.Dispatch;
import com.joysistvi.petstocks.model.Inventory;
import com.joysistvi.petstocks.model.Product;

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

public class DispatchRepoImpl implements DispatchRepo {
    private static final String DISPATCH_SELECT =
            "SELECT d.id AS dispatch_id, d.datetime_dispatched, d.quantity_dispatched, " +
                    "d.user_id, u.username, " +
                    "i.id AS inventory_id, i.quantity AS inventory_quantity, " +
                    "i.expiration, i.batch_code, i.remark, " +
                    "p.id AS product_id, p.name AS product_name, p.brand, " +
                    "p.description AS product_description, " +
                    "p.is_archived AS product_is_archived, " +
                    "c.id AS category_id, c.name AS category_name, " +
                    "c.description AS category_description, " +
                    "c.is_archived AS category_is_archived " +
                    "FROM dispatches d " +
                    "JOIN inventories i ON d.inventory_id = i.id " +
                    "JOIN products p ON i.product_id = p.id " +
                    "JOIN categories c ON p.category_id = c.id " +
                    "JOIN users u ON d.user_id = u.id ";

    private final DBConnection dbConnection;

    public DispatchRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Dispatch> getAllDispatches() {
        return getAllDispatches("date");
    }

    @Override
    public List<Dispatch> getAllDispatches(String sortBy) {
        List<Dispatch> dispatches = new ArrayList<>();
        String query = DISPATCH_SELECT + "ORDER BY " + getApprovedSortColumn(sortBy);

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                dispatches.add(mapDispatch(result));
            }
        } catch (SQLException e) {
            System.err.println("Get Dispatch History Error: " + e.getMessage());
        }

        return dispatches;
    }

    @Override
    public Dispatch getDispatchById(int id) {
        String query = DISPATCH_SELECT + "WHERE d.id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);
            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return mapDispatch(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Read Dispatch By ID Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Dispatch> getDispatchesByInventoryId(int inventoryId) {
        return getDispatchesById(
                "WHERE d.inventory_id = ? ORDER BY d.datetime_dispatched DESC", inventoryId,
                "Get Dispatches By Inventory Error: ");
    }

    @Override
    public List<Dispatch> getDispatchesByProductId(int productId) {
        return getDispatchesById(
                "WHERE p.id = ? ORDER BY d.datetime_dispatched DESC", productId,
                "Get Dispatches By Product Error: ");
    }

    @Override
    public List<Dispatch> getDispatchesByUserId(int userId) {
        return getDispatchesById(
                "WHERE d.user_id = ? ORDER BY d.datetime_dispatched DESC", userId,
                "Get Dispatches By User Error: ");
    }

    @Override
    public List<Dispatch> getDispatchesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Dispatch> dispatches = new ArrayList<>();
        String query = DISPATCH_SELECT +
                "WHERE d.datetime_dispatched >= ? AND d.datetime_dispatched < ? " +
                "ORDER BY d.datetime_dispatched DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
            prep.setTimestamp(2, Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));
            addResults(prep, dispatches);
        } catch (SQLException e) {
            System.err.println("Get Dispatches By Date Range Error: " + e.getMessage());
        }

        return dispatches;
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
            System.err.println("Get Users For Dispatch Error: " + e.getMessage());
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
            System.err.println("Read Dispatch User Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean recordStockOut(Dispatch dispatch) {
        String lockInventory = "SELECT quantity FROM inventories WHERE id = ? FOR UPDATE";
        String insertDispatch = "INSERT INTO dispatches " +
                "(inventory_id, datetime_dispatched, quantity_dispatched, user_id) " +
                "VALUES (?, ?, ?, ?)";
        String decreaseInventory =
                "UPDATE inventories SET quantity = quantity - ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement lock = conn.prepareStatement(lockInventory);
                 PreparedStatement insert = conn.prepareStatement(
                         insertDispatch, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement update = conn.prepareStatement(decreaseInventory)) {

                lock.setInt(1, dispatch.getInventory().getId());
                try (ResultSet result = lock.executeQuery()) {
                    if (!result.next()) {
                        throw new SQLException("Inventory record was not found.");
                    }
                    if (result.getInt("quantity") < dispatch.getQuantityDispatched()) {
                        throw new SQLException("Insufficient inventory quantity.");
                    }
                }

                insert.setInt(1, dispatch.getInventory().getId());
                insert.setTimestamp(2, Timestamp.valueOf(dispatch.getDatetimeDispatched()));
                insert.setInt(3, dispatch.getQuantityDispatched());
                insert.setInt(4, dispatch.getUserId());
                if (insert.executeUpdate() != 1) {
                    throw new SQLException("Dispatch record was not created.");
                }

                update.setInt(1, dispatch.getQuantityDispatched());
                update.setInt(2, dispatch.getInventory().getId());
                if (update.executeUpdate() != 1) {
                    throw new SQLException("Inventory quantity was not updated.");
                }

                try (ResultSet keys = insert.getGeneratedKeys()) {
                    if (keys.next()) {
                        dispatch.setId(keys.getInt(1));
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                rollback(conn);
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Record Stock Out Error: " + e.getMessage());
        }

        return false;
    }

    private List<Dispatch> getDispatchesById(String condition, int id, String errorMessage) {
        List<Dispatch> dispatches = new ArrayList<>();
        String query = DISPATCH_SELECT + condition;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);
            addResults(prep, dispatches);
        } catch (SQLException e) {
            System.err.println(errorMessage + e.getMessage());
        }

        return dispatches;
    }

    private void addResults(PreparedStatement prep, List<Dispatch> dispatches)
            throws SQLException {
        try (ResultSet result = prep.executeQuery()) {
            while (result.next()) {
                dispatches.add(mapDispatch(result));
            }
        }
    }

    private Dispatch mapDispatch(ResultSet result) throws SQLException {
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

        return new Dispatch(
                result.getInt("dispatch_id"),
                inventory,
                result.getTimestamp("datetime_dispatched").toLocalDateTime(),
                result.getInt("quantity_dispatched"),
                result.getInt("user_id"),
                result.getString("username")
        );
    }

    private String getApprovedSortColumn(String sortBy) {
        return switch (sortBy) {
            case "product" -> "p.name, d.datetime_dispatched DESC";
            case "user" -> "u.username, d.datetime_dispatched DESC";
            case "date" -> "d.datetime_dispatched DESC";
            default -> "d.id";
        };
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException rollbackError) {
            System.err.println("Dispatch Rollback Error: " + rollbackError.getMessage());
        }
    }
}
