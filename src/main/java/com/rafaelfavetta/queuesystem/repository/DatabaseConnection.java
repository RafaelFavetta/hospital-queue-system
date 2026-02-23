package com.rafaelfavetta.queuesystem.repository;

import java.sql.Connection;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5433/queue_system";

    private static final String USER = "queue";
    private static final String PASSWORD = "system";

    public static Connection getConnection() {
        try {
            return java.sql.DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Error connecting to database", e);
        }
    }
}