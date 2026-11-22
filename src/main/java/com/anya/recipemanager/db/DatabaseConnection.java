package com.anya.recipemanager.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String ENV_URL = "DB_URL";
    private static final String ENV_USER = "DB_USER";
    private static final String ENV_PASSWORD = "DB_PASSWORD";

    public Connection getConnection() throws SQLException {
        String url = System.getenv(ENV_URL);
        String user = System.getenv(ENV_USER);
        String password = System.getenv(ENV_PASSWORD);

        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Environment variable DB_URL is not set");
        }
        return DriverManager.getConnection(url, user, password);
    }
}
