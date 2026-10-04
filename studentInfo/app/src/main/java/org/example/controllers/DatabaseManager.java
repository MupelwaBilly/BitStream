package org.example.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:bitstream.db";

    static {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS students ("
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " name TEXT NOT NULL,"
                    + " year INTEGER,"
                    + " program TEXT"
                    + ");";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}