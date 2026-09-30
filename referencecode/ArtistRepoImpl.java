package com.joysistvi.recording.repository;

import com.joysistvi.recording.config.DBConnection;
import com.joysistvi.recording.model.Artist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistRepoImpl implements ArtistRepo{
    private final DBConnection dbConnection;

    public ArtistRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Artist> getAllArtists() {
        List<Artist> artists = new ArrayList<>();
        String query = "SELECT * FROM artists WHERE is_archived = 0";

        try (Connection conn = dbConnection.getConnection();
             Statement stmnt = conn.createStatement();
             ResultSet result = stmnt.executeQuery(query)) {

            while (result.next()) {
                artists.add(new Artist(
                        result.getInt("id"),
                        result.getString("name")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Get All Artists Error: " + e.getMessage());
        }

        return artists;
    }

    @Override
    public Artist getArtistById(int id) {
        String query = "SELECT * FROM artists WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);

            try (ResultSet res = prep.executeQuery()) {
                if (res.next()) {
                    return new Artist(res.getInt("id"), res.getString("name"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Read Artist By Id Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Artist> searchArtist(String keyword) {
        List<Artist> artists = new ArrayList<>();
        String query = "SELECT * FROM artists WHERE name LIKE ? AND is_archived = 0";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, "%" + keyword + "%");

            try (ResultSet res = prep.executeQuery()) {
                while (res.next()) {
                    artists.add(new Artist(
                            res.getInt("id"),
                            res.getString("name")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Search Artist Error: " + e.getMessage());
        }

        return artists;
    }

    @Override
    public boolean createArtist(Artist artist) {
        String query = "INSERT INTO artists (name) VALUES (?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, artist.getName());
            int rowsAffected = prep.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Create Artist Error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateArtist(Artist artist) {
        String query = "UPDATE artists SET name = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, artist.getName());
            prep.setInt(2, artist.getId());

            int rowsAffected = prep.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Update Artist Error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean archiveArtist(int id) {
        String query = "UPDATE artists SET is_archived = 1 WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);
            int rowsAffected = prep.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Archive Artist Error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean restoreArtist(int id) {
        String query = "UPDATE artists SET is_archived = 0 WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);
            int rowsAffected = prep.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Restore Artist Error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteArtist(int id) {
        String query = "DELETE FROM artists WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setInt(1, id);
            int rowsAffected = prep.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Delete Artist Error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Artist> getAllArchivedArtists() {
        List<Artist> artists = new ArrayList<>();
        String query = "SELECT * FROM artists WHERE is_archived = 1";

        try (Connection conn = dbConnection.getConnection();
             Statement stmnt = conn.createStatement();
             ResultSet result = stmnt.executeQuery(query)) {

            while (result.next()) {
                artists.add(new Artist(
                        result.getInt("id"),
                        result.getString("name")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Get All Archived Artists Error: " + e.getMessage());
        }

        return artists;
    }
}
