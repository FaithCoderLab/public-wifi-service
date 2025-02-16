package com.zerobase.wifi.util;

import lombok.Getter;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private String getDBPath() {
        URL resource = getClass().getClassLoader().getResource("db/wifi.db");
        System.out.println("Resource: " + resource);
        System.out.println("Current directory: " + System.getProperty("user.dir"));

        System.out.println("ClassLoader: " + getClass().getClassLoader());

        if (resource != null) {
            String path = resource.getPath();
            System.out.println("Database path: " + path);
            return path;
        }
        throw new RuntimeException("Database file not found");
    }

    private static final String DB_FILE_PATH = new DatabaseConfig().getDBPath();
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_FILE_PATH;
    private static final int DEFAULT_TIMEOUT = 30000;
    private static final int MAX_RETRY_COUNT = 3;

    private static DatabaseConfig instance;
    private Connection connection;

    @Getter
    private int retryCount;
    @Getter
    private int connectionTimeout;
    private boolean isConnected;


    private DatabaseConfig() {
        this.retryCount = MAX_RETRY_COUNT;
        this.connectionTimeout = DEFAULT_TIMEOUT;
        this.isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    public Connection getConnection() {
        for (int i = 0; i < retryCount; i++) {
            try {
                if (connection != null && !connection.isClosed()) {
                    return connection;
                }
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(JDBC_URL);
                isConnected = true;
                return connection;
            } catch (SQLException | ClassNotFoundException e) {
                if (i < retryCount - 1) {
                    continue;
                }
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                isConnected = false;
                connection = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}