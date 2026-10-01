package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SupplierRepoImpl implements SupplierRepo {
    private final DBConnection dbConnection;

    public SupplierRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return getAllSuppliers("id");
    }

    @Override
    public List<Supplier> getAllSuppliers(String sortBy) {
        List<Supplier> suppliers = new ArrayList<>();
        String sortColumn = getApprovedSortColumn(sortBy);
        String query = "SELECT id, name, address, contact_number, email, is_archived " +
                "FROM suppliers WHERE is_archived = 0 ORDER BY " + sortColumn;

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                suppliers.add(mapSupplier(result));
            }
        } catch (SQLException e) {
            System.err.println("Get All Suppliers Error: " + e.getMessage());
        }

        return suppliers;
    }

    @Override
    public Supplier getSupplierById(int id) {
        String query = "SELECT id, name, address, contact_number, email, is_archived " +
                "FROM suppliers WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);

            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return mapSupplier(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Read Supplier By ID Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Supplier> searchSuppliers(String keyword) {
        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT id, name, address, contact_number, email, is_archived " +
                "FROM suppliers WHERE is_archived = 0 " +
                "AND (name LIKE ? OR address LIKE ? OR contact_number LIKE ? OR email LIKE ?) " +
                "ORDER BY name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            prep.setString(1, searchPattern);
            prep.setString(2, searchPattern);
            prep.setString(3, searchPattern);
            prep.setString(4, searchPattern);

            try (ResultSet result = prep.executeQuery()) {
                while (result.next()) {
                    suppliers.add(mapSupplier(result));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search Suppliers Error: " + e.getMessage());
        }

        return suppliers;
    }

    @Override
    public boolean createSupplier(Supplier supplier) {
        String query = "INSERT INTO suppliers " +
                "(name, address, contact_number, email, is_archived) VALUES (?, ?, ?, ?, 0)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(
                     query, Statement.RETURN_GENERATED_KEYS)) {

            prep.setString(1, supplier.getName());
            prep.setString(2, supplier.getAddress());
            prep.setString(3, supplier.getContactNumber());
            setNullableEmail(prep, 4, supplier.getEmail());
            if (prep.executeUpdate() != 1) {
                return false;
            }

            try (ResultSet keys = prep.getGeneratedKeys()) {
                if (keys.next()) {
                    supplier.setId(keys.getInt(1));
                }
            }
            return supplier.getId() > 0;
        } catch (SQLException e) {
            System.err.println("Create Supplier Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateSupplier(Supplier supplier) {
        String query = "UPDATE suppliers SET name = ?, address = ?, contact_number = ?, email = ? " +
                "WHERE id = ? AND is_archived = 0";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, supplier.getName());
            prep.setString(2, supplier.getAddress());
            prep.setString(3, supplier.getContactNumber());
            setNullableEmail(prep, 4, supplier.getEmail());
            prep.setInt(5, supplier.getId());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Supplier Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean archiveSupplier(int id) {
        return updateArchiveStatus(id, false, true, "Archive Supplier Error: ");
    }

    @Override
    public boolean restoreSupplier(int id) {
        return updateArchiveStatus(id, true, false, "Restore Supplier Error: ");
    }

    @Override
    public boolean deleteSupplier(int id) {
        String query = "DELETE FROM suppliers WHERE id = ? AND is_archived = 1";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete Supplier Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public List<Supplier> getAllArchivedSuppliers() {
        return getAllArchivedSuppliers("id");
    }

    @Override
    public List<Supplier> getAllArchivedSuppliers(String sortBy) {
        List<Supplier> suppliers = new ArrayList<>();
        String sortColumn = getApprovedSortColumn(sortBy);
        String query = "SELECT id, name, address, contact_number, email, is_archived " +
                "FROM suppliers WHERE is_archived = 1 ORDER BY " + sortColumn;

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                suppliers.add(mapSupplier(result));
            }
        } catch (SQLException e) {
            System.err.println("Get All Archived Suppliers Error: " + e.getMessage());
        }

        return suppliers;
    }

    private String getApprovedSortColumn(String sortBy) {
        return "name".equals(sortBy) ? "name" : "id";
    }

    private boolean updateArchiveStatus(int id, boolean currentStatus, boolean newStatus,
                                        String errorMessage) {
        String query = "UPDATE suppliers SET is_archived = ? WHERE id = ? AND is_archived = ?";

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

    private Supplier mapSupplier(ResultSet result) throws SQLException {
        return new Supplier(
                result.getInt("id"),
                result.getString("name"),
                result.getString("address"),
                result.getString("contact_number"),
                result.getString("email"),
                result.getBoolean("is_archived")
        );
    }

    private void setNullableEmail(PreparedStatement prep, int parameterIndex,
                                  String email) throws SQLException {
        if (email == null) {
            prep.setNull(parameterIndex, Types.VARCHAR);
        } else {
            prep.setString(parameterIndex, email);
        }
    }
}
