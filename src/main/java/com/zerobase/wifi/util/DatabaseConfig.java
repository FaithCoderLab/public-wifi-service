package com.zerobase.wifi.util;

import lombok.Getter;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private String getDBPath() {
        URL resource = getClass().getClassLoader().getResource("db/wifi.db");

        if (resource != null) {
            return resource.getPath();
        }
        throw new RuntimeException("Database file not found");
    }

    private static final String DB_FILE_PATH = new DatabaseConfig().getDBPath();
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_FILE_PATH;
    private static final int DEFAULT_TIMEOUT = 30000;
    private static final int MAX_RETRY_COUNT = 3;

    private static DatabaseConfig instance;
    private Connection connection;
    private boolean isInTransaction;

    @Getter
    private int retryCount;
    @Getter
    private int connectionTimeout;
    private boolean isConnected;


    private DatabaseConfig() {
        this.retryCount = MAX_RETRY_COUNT;
        this.connectionTimeout = DEFAULT_TIMEOUT;
        this.isConnected = false;
        this.isInTransaction = false;
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

                if (!isInTransaction) {
                    connection.setAutoCommit(true);
                }

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

    public void beginTransaction() throws SQLException {
        if (isInTransaction) {
            throw new SQLException("Transaction is already in progress");
        }

        Connection conn = getConnection();
        conn.setAutoCommit(false);
        isInTransaction = true;
    }

    public void commit() throws SQLException {
        if (!isInTransaction) {
            throw new SQLException("Transaction is not in progress");
        }

        try {
            Connection conn = getConnection();
            conn.commit();
            conn.setAutoCommit(true);
        } finally {
            isInTransaction = false;
        }
    }

    public void rollback() throws SQLException {
        if (!isInTransaction) {
            throw new SQLException("Transaction is not in progress");
        }

        try {
            Connection conn = getConnection();
            conn.rollback();
            conn.setAutoCommit(true);
        } finally {
            isInTransaction = false;
        }
    }

    public boolean isInTransaction() {
        return isInTransaction;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                if (isInTransaction) {
                    connection.rollback();
                }
                connection.close();
                isConnected = false;
                isInTransaction = false;
                connection = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}