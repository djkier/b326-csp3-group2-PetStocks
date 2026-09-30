package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.model.PetType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PetTypeRepoImpl implements PetTypeRepo {
    private final DBConnection dbConnection;

    public PetTypeRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<PetType> getAllPetTypes() {
        return getAllPetTypes("id");
    }

    @Override
    public List<PetType> getAllPetTypes(String sortBy) {
        List<PetType> petTypes = new ArrayList<>();
        String sortColumn = getApprovedSortColumn(sortBy);
        String query = "SELECT id, name, description, is_archived " +
                "FROM pet_types WHERE is_archived = 0 ORDER BY " + sortColumn;

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                petTypes.add(mapPetType(result));
            }
        } catch (SQLException e) {
            System.err.println("Get All Pet Types Error: " + e.getMessage());
        }

        return petTypes;
    }

    @Override
    public PetType getPetTypeById(int id) {
        String query = "SELECT id, name, description, is_archived FROM pet_types WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);

            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return mapPetType(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Read Pet Type By ID Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<PetType> searchPetTypes(String keyword) {
        List<PetType> petTypes = new ArrayList<>();
        String query = "SELECT id, name, description, is_archived FROM pet_types " +
                "WHERE is_archived = 0 AND (name LIKE ? OR description LIKE ?) ORDER BY name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            prep.setString(1, searchPattern);
            prep.setString(2, searchPattern);

            try (ResultSet result = prep.executeQuery()) {
                while (result.next()) {
                    petTypes.add(mapPetType(result));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search Pet Types Error: " + e.getMessage());
        }

        return petTypes;
    }

    @Override
    public boolean createPetType(PetType petType) {
        String query = "INSERT INTO pet_types (name, description, is_archived) VALUES (?, ?, 0)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, petType.getName());
            setNullableDescription(prep, 2, petType.getDescription());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Create Pet Type Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updatePetType(PetType petType) {
        String query = "UPDATE pet_types SET name = ?, description = ? " +
                "WHERE id = ? AND is_archived = 0";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, petType.getName());
            setNullableDescription(prep, 2, petType.getDescription());
            prep.setInt(3, petType.getId());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update Pet Type Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean archivePetType(int id) {
        return updateArchiveStatus(id, false, true, "Archive Pet Type Error: ");
    }

    @Override
    public boolean restorePetType(int id) {
        return updateArchiveStatus(id, true, false, "Restore Pet Type Error: ");
    }

    @Override
    public boolean deletePetType(int id) {
        String query = "DELETE FROM pet_types WHERE id = ? AND is_archived = 1";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete Pet Type Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public List<PetType> getAllArchivedPetTypes() {
        return getAllArchivedPetTypes("id");
    }

    @Override
    public List<PetType> getAllArchivedPetTypes(String sortBy) {
        List<PetType> petTypes = new ArrayList<>();
        String sortColumn = getApprovedSortColumn(sortBy);
        String query = "SELECT id, name, description, is_archived " +
                "FROM pet_types WHERE is_archived = 1 ORDER BY " + sortColumn;

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                petTypes.add(mapPetType(result));
            }
        } catch (SQLException e) {
            System.err.println("Get All Archived Pet Types Error: " + e.getMessage());
        }

        return petTypes;
    }

    private String getApprovedSortColumn(String sortBy) {
        return "name".equals(sortBy) ? "name" : "id";
    }

    private boolean updateArchiveStatus(int id, boolean currentStatus, boolean newStatus,
                                        String errorMessage) {
        String query = "UPDATE pet_types SET is_archived = ? WHERE id = ? AND is_archived = ?";

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

    private PetType mapPetType(ResultSet result) throws SQLException {
        return new PetType(
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
