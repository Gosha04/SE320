package com.SE320.therapy.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataManager {
    private static final DataManager INSTANCE = new DataManager();

    private static final String URL = System.getenv("MYSQL_URL");
    private static final String USER = "root";
    private static final String PASSWORD = System.getenv("MYSQL_PASSWORD");

    private DataManager() {
    }

    public static DataManager getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        if (URL == null || URL.isBlank()) {
            throw new SQLException("MYSQL_URL is not configured.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
