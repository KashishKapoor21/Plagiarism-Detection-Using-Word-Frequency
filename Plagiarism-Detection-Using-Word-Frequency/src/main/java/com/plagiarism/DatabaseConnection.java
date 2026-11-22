package com.plagiarism;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            System.getenv().getOrDefault(
                    "DB_URL",
                    "jdbc:mysql://localhost:3306/plagiarism_detector"
            );

    private static final String USER =
            System.getenv().getOrDefault(
                    "DB_USER",
                    "root"
            );

    private static final String PASSWORD =
            System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "Database password not configured. Set DB_PASSWORD environment variable."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void testConnection() {

        try (Connection connection = getConnection()) {

            if (connection != null) {
                System.out.println("Database connected successfully!");
            }

        } catch (SQLException e) {

            System.out.println("Database connection failed!");
            System.out.println(e.getMessage());

        }
    }
}