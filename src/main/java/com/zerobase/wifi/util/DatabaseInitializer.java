package com.zerobase.wifi.util;

import lombok.Getter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    @Getter
    public static DatabaseInitializer instance;
    private final DatabaseConfig dbConfig;

    private DatabaseInitializer() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public static synchronized DatabaseInitializer getInstance() {
        if (instance == null) {
            instance = new DatabaseInitializer();
        }
        return instance;
    }

    public void initializeDatabase() {
        try {
            if (isInitialized()) {
                System.out.println("Database already initialized");
                return;
            }

            dropTables();
            createTables();

            System.out.println("Database initialized");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private static final String CREATE_WIFI_INFO_TABLE  =
        "CREATE TABLE IF NOT EXISTS WIFI_INFO (\n" +
        "MGR_NO TEXT PRIMARY KEY, \n" +
        "DISTANCE REAL, \n" +
        "DISTRICT TEXT, \n" +
        "NAME TEXT, \n" +
        "ROAD_ADDRESS TEXT, \n" +
        "DETAIL_ADDRESS TEXT, \n" +
        "INSTALL_FLOOR TEXT, \n" +
        "INSTALL_TYPE TEXT, \n" +
        "INSTALL_AGENCY TEXT, \n" +
        "SERVICE_TYPE TEXT, \n" +
        "NET_TYPE TEXT, \n" +
        "INSTALL_YEAR TEXT, \n" +
        "IN_OUT_DOOR TEXT, \n" +
        "WIFI_ENVIRONMENT TEXT, \n" +
        "LAT REAL, \n" +
        "LNT REAL, \n" +
        "WORK_DATE TEXT \n" +
        ")";

    private static final String CREATE_LOCATION_HISTORY_TABLE =
        "CREATE TABLE IF NOT EXISTS LOCATION_HISTORY (\n" +
        "    ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
        "    LAT REAL,\n" +
        "    LNT REAL,\n" +
        "    SEARCH_DATE TEXT,\n" +
        "    DELETE_YN INTEGER\n" +
        ");";

    private static final String CREATE_BOOKMARK_GROUP_TABLE =
        "CREATE TABLE IF NOT EXISTS BOOKMARK_GROUP (\n" +
            "    ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    NAME TEXT,\n" +
            "    ORDER_NO INTEGER,\n" +
            "    REG_DATE TEXT,\n" +
            "    MOD_DATE TEXT\n" +
        ");";

    private static final String CREATE_BOOKMARK_TABLE =
        "CREATE TABLE IF NOT EXISTS BOOKMARK (\n" +
            "    ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    GROUP_ID INTEGER,\n" +
            "    WIFI_MGR_NO TEXT,\n" +
            "    REG_DATE TEXT,\n" +
            "    FOREIGN KEY (GROUP_ID) REFERENCES BOOKMARK_GROUP(ID),\n" +
            "    FOREIGN KEY (WIFI_MGR_NO) REFERENCES WIFI_INFO(MGR_NO)\n" +
        ");";

    public void createTables() throws SQLException {
        try (Connection conn = dbConfig.getConnection();
        Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(false);

            try {
                stmt.execute("PRAGMA foreign_keys = ON;");

                stmt.execute(CREATE_WIFI_INFO_TABLE);
                stmt.execute(CREATE_LOCATION_HISTORY_TABLE);
                stmt.execute(CREATE_BOOKMARK_TABLE);
                stmt.execute(CREATE_BOOKMARK_GROUP_TABLE);

                conn.commit();
                System.out.println("Tables created");
            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback();
                }
                System.err.println("Failed to create tables: " + e.getMessage());
                throw e;
            }
        }
    }

    public void dropTables() throws SQLException {
        try (Connection conn = dbConfig.getConnection();
        Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(false);

            try {
                stmt.execute("DROP TABLE IF EXISTS WIFI_INFO");
                stmt.execute("DROP TABLE IF EXISTS LOCATION_HISTORY");
                stmt.execute("DROP TABLE IF EXISTS BOOKMARK_GROUP");
                stmt.execute("DROP TABLE IF EXISTS BOOKMARK");

                conn.commit();
                System.out.println("Tables dropped");
            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback();
                }
                System.err.println("Failed to drop tables: " + e.getMessage());
                throw e;
            }
        }
    }

    public boolean isInitialized() {
        try (Connection conn = dbConfig.getConnection();
        Statement stmt = conn.createStatement()) {
            String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='WIFI_INFO'";
            ResultSet rs = stmt.executeQuery(sql);

            return rs.next();
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            return false;

        }
    }
}