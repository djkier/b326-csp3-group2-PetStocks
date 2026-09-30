package com.joysistvi.petstocks;

import com.joysistvi.petstocks.config.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello Group 2!!!");

        DBConnection dbConnection = new DBConnection();

        try (Connection connection = dbConnection.getConnection()) {

            if (connection != null && !connection.isClosed()) {
                System.out.println("Database connection successful!");
            }

        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            System.out.println("Error: " + e.getMessage());
        }

    }
}
