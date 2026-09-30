package com.joysistvi.petstocks.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private final static String URL =
            "jdbc:mysql://localhost:3306/petstock_db";

    private final static String USER = "root";
    private final static String PASSWORD = "";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
