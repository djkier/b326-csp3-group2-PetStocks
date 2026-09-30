package com.joysistvi.petstocks.migration;

import com.joysistvi.petstocks.config.DBConnection;
import com.joysistvi.petstocks.utility.PasswordUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class PasswordHashMigration {
    private static final String RUN_ARGUMENT = "--run";

    private final DBConnection dbConnection;

    public PasswordHashMigration(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public static void main(String[] args) {
        if (args.length != 1 || !RUN_ARGUMENT.equals(args[0])) {
            System.out.println("Password migration was not run. Use --run to execute it explicitly.");
            return;
        }

        PasswordHashMigration migration = new PasswordHashMigration(new DBConnection());
        try {
            MigrationResult result = migration.migrate();
            System.out.println("Password migration completed.");
            System.out.println("Users scanned: " + result.usersScanned());
            System.out.println("Passwords migrated: " + result.passwordsMigrated());
            System.out.println("Already hashed and skipped: " + result.alreadyHashed());
            System.out.println("Verification passed: all stored passwords use BCrypt.");
        } catch (SQLException e) {
            System.err.println("Password migration failed. Database changes were rolled back.");
            System.exit(1);
        }
    }

    public MigrationResult migrate() throws SQLException {
        int usersScanned = 0;
        int passwordsMigrated = 0;
        int alreadyHashed = 0;

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                List<StoredPassword> storedPasswords = readAndLockPasswords(conn);
                usersScanned = storedPasswords.size();

                String updateQuery = "UPDATE users SET password_hash = ? " +
                        "WHERE id = ? AND password_hash = ?";
                try (PreparedStatement update = conn.prepareStatement(updateQuery)) {
                    for (StoredPassword storedPassword : storedPasswords) {
                        if (PasswordUtility.isBcryptHash(storedPassword.value())) {
                            alreadyHashed++;
                            continue;
                        }
                        if (storedPassword.value() == null) {
                            throw new SQLException("A user has a null password value.");
                        }

                        update.setString(1, PasswordUtility.hashPassword(storedPassword.value()));
                        update.setInt(2, storedPassword.userId());
                        update.setString(3, storedPassword.value());
                        if (update.executeUpdate() != 1) {
                            throw new SQLException("A password changed while migration was running.");
                        }
                        passwordsMigrated++;
                    }
                }

                verifyAllPasswordsAreHashed(conn);
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                rollback(conn);
                if (e instanceof SQLException sqlException) {
                    throw sqlException;
                }
                throw new SQLException("Password hashing failed.", e);
            }
        }

        return new MigrationResult(usersScanned, passwordsMigrated, alreadyHashed);
    }

    private List<StoredPassword> readAndLockPasswords(Connection conn) throws SQLException {
        List<StoredPassword> storedPasswords = new ArrayList<>();
        String query = "SELECT id, password_hash FROM users FOR UPDATE";

        try (Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {
            while (result.next()) {
                storedPasswords.add(new StoredPassword(
                        result.getInt("id"), result.getString("password_hash")));
            }
        }
        return storedPasswords;
    }

    private void verifyAllPasswordsAreHashed(Connection conn) throws SQLException {
        int usersScanned = 0;
        int unhashedPasswords = 0;
        String query = "SELECT password_hash FROM users";

        try (Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {
            while (result.next()) {
                usersScanned++;
                if (!PasswordUtility.isBcryptHash(result.getString("password_hash"))) {
                    unhashedPasswords++;
                }
            }
        }

        if (unhashedPasswords > 0) {
            throw new SQLException("Verification found unhashed passwords among " +
                    usersScanned + " users.");
        }
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException rollbackError) {
            System.err.println("Password migration rollback failed.");
        }
    }

    public record MigrationResult(int usersScanned, int passwordsMigrated, int alreadyHashed) {
    }

    private record StoredPassword(int userId, String value) {
    }
}
