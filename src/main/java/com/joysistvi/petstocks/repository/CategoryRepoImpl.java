package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepoImpl implements CategoryRepo {
    private final DBConnection dbConnection;

    public CategoryRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Category> getAllCategories() {
        return getAllCategories("id");
    }

    @Override
    public List<Category> getAllCategories(String sortBy) {
        List<Category> categories = new ArrayList<>();
        String sortColumn = getApprovedSortColumn(sortBy);
        String query = "SELECT id, name, description, is_archived " +
                "FROM categories WHERE is_archived = 0 ORDER BY " + sortColumn;

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                categories.add(mapCategory(result));
            }
        } catch (SQLException e) {
            System.err.println("Get All Categories Error: " + e.getMessage());
        }

        return categories;
    }

    @Override
    public Category getCategoryById(int id) {
        String query = "SELECT id, name, description, is_archived FROM categories WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);

            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return mapCategory(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Read Category By ID Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Category> searchCategories(String keyword) {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT id, name, description, is_archived FROM categories " +
                "WHERE is_archived = 0 AND (name LIKE ? OR description LIKE ?) ORDER BY name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            prep.setString(1, searchPattern);
            prep.setString(2, searchPattern);

            try (ResultSet result = prep.executeQuery()) {
                while (result.next()) {
                    categories.add(mapCategory(result));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search Categories Error: " + e.getMessage());
        }

        return categories;
    }

    @Override
    public boolean createCategory(Category category) {
        String query = "INSERT INTO categories (name, description, is_archived) VALUES (?, ?, 0)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, category.getName());
            setNullableDescription(prep, 2, category.getDescription());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Create Category Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateCategory(Category category) {
        String query = "UPDATE categories SET name = ?, description = ? " +
                "WHERE id = ? AND is_archived = 0";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, category.getName());
            setNullableDescription(prep, 2, category.getDescription());
            prep.setInt(3, category.getId());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Category Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean archiveCategory(int id) {
        return updateArchiveStatus(id, false, true, "Archive Category Error: ");
    }

    @Override
    public boolean restoreCategory(int id) {
        return updateArchiveStatus(id, true, false, "Restore Category Error: ");
    }

    @Override
    public boolean deleteCategory(int id) {
        String query = "DELETE FROM categories WHERE id = ? AND is_archived = 1";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete Category Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public List<Category> getAllArchivedCategories() {
        return getAllArchivedCategories("id");
    }

    @Override
    public List<Category> getAllArchivedCategories(String sortBy) {
        List<Category> categories = new ArrayList<>();
        String sortColumn = getApprovedSortColumn(sortBy);
        String query = "SELECT id, name, description, is_archived " +
                "FROM categories WHERE is_archived = 1 ORDER BY " + sortColumn;

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                categories.add(mapCategory(result));
            }
        } catch (SQLException e) {
            System.err.println("Get All Archived Categories Error: " + e.getMessage());
        }

        return categories;
    }

    private String getApprovedSortColumn(String sortBy) {
        return "name".equals(sortBy) ? "name" : "id";
    }

    private boolean updateArchiveStatus(int id, boolean currentStatus, boolean newStatus,
                                        String errorMessage) {
        String query = "UPDATE categories SET is_archived = ? WHERE id = ? AND is_archived = ?";

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

    private Category mapCategory(ResultSet result) throws SQLException {
        return new Category(
                result.getInt("id"),
                result.getString("name"),
                result.getString("description"),
                result.getBoolean("is_archived")
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
