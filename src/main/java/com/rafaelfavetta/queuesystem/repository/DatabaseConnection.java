package com.rafaelfavetta.queuesystem.repository;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Log4j2
public class DatabaseConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                URL = props.getProperty("db.url", "jdbc:postgresql://localhost:5433/queue_system");
                USER = props.getProperty("db.user", "queue");
                PASSWORD = props.getProperty("db.password", "system");
            } else {
                log.warn("database.properties not found, using default values");
                URL = "jdbc:postgresql://localhost:5433/queue_system";
                USER = "queue";
                PASSWORD = "system";
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading database properties", e);
        }
    }

    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            log.debug("Database connection created");
            return connection;
        } catch (SQLException e) {
            log.error("Error connecting to database: {}", e.getMessage());
            throw new RuntimeException("Error connecting to database", e);
        }
    }
}