package com.joysistvi.petstocks.repository;

import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserRepoImpl implements UserRepo {
    private static final String USER_SELECT = "SELECT id, username, role FROM users ";

    private final DBConnection dbConnection;

    public UserRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<User> getAllUsers() {
        return getAllUsers("id");
    }

    @Override
    public List<User> getAllUsers(String sortBy) {
        List<User> users = new ArrayList<>();
        String query = USER_SELECT + "ORDER BY " + getApprovedSortColumn(sortBy);

        try (Connection conn = dbConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            while (result.next()) {
                users.add(mapUser(result));
            }
        } catch (SQLException e) {
            System.err.println("Get All Users Error: " + e.getMessage());
        }

        return users;
    }

    @Override
    public User getUserById(int id) {
        String query = USER_SELECT + "WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);
            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return mapUser(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Read User By ID Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        String query = "SELECT id, username, password_hash, role FROM users WHERE username = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, username);
            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return new User(
                            result.getInt("id"),
                            result.getString("username"),
                            result.getString("password_hash"),
                            result.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Find User By Username Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        String query = USER_SELECT +
                "WHERE username LIKE ? OR role LIKE ? ORDER BY username";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            prep.setString(1, searchPattern);
            prep.setString(2, searchPattern);

            try (ResultSet result = prep.executeQuery()) {
                while (result.next()) {
                    users.add(mapUser(result));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search Users Error: " + e.getMessage());
        }

        return users;
    }

    @Override
    public boolean usernameExists(String username) {
        return usernameExists(username, 0, false);
    }

    @Override
    public boolean usernameExistsForAnotherUser(String username, int userId) {
        return usernameExists(username, userId, true);
    }

    @Override
    public boolean createUser(User user) {
        String query = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, user.getUsername());
            prep.setString(2, user.getPasswordHash());
            prep.setString(3, user.getRole());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Create User Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateUsername(int id, String username) {
        String query = "UPDATE users SET username = ? WHERE id = ?";
        return executeUpdate(query, username, id, "Update Username Error: ");
    }

    @Override
    public boolean updateRole(int id, String role) {
        String query = "UPDATE users SET role = ? WHERE id = ?";
        return executeUpdate(query, role, id, "Update User Role Error: ");
    }

    @Override
    public boolean updatePasswordHash(int id, String passwordHash) {
        String query = "UPDATE users SET password_hash = ? WHERE id = ?";
        return executeUpdate(query, passwordHash, id, "Change User Password Error: ");
    }

    private boolean usernameExists(String username, int userId, boolean excludeUser) {
        String query = excludeUser
                ? "SELECT 1 FROM users WHERE username = ? AND id <> ?"
                : "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, username);
            if (excludeUser) {
                prep.setInt(2, userId);
            }

            try (ResultSet result = prep.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            System.err.println("Check Username Error: " + e.getMessage());
        }

        return false;
    }

    private boolean executeUpdate(String query, String value, int id, String errorMessage) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, value);
            prep.setInt(2, id);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(errorMessage + e.getMessage());
        }

        return false;
    }

    private User mapUser(ResultSet result) throws SQLException {
        return new User(
                result.getInt("id"),
                result.getString("username"),
                null,
                result.getString("role")
        );
    }

    private String getApprovedSortColumn(String sortBy) {
        return switch (sortBy) {
            case "username" -> "username";
            case "role" -> "role, username";
            default -> "id";
        };
    }
}
