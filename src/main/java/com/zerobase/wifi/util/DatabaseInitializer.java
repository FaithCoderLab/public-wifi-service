package com.zerobase.wifi.util;

import lombok.Getter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class DatabaseInitializer {
    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class.getName());

    @Getter
    private static DatabaseInitializer instance;
    private final DatabaseConfig dbConfig;
    private static final int MAX_RETRY_COUNT = 3;
    private static final int RETRY_DELAY_MS = 1000;

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
        for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
            try {
                logger.info("Initializing database (attempt " + attempt + " of " + MAX_RETRY_COUNT + ")");

                if (isInitialized()) {
                    logger.info("Database already initialized");
                    return;
                }

                logger.info("Starting database initialization process");
                dropTables();
                createTables();

                if (verifyTables()) {
                    logger.info("Database initialization completed successfully");
                    return;
                }

                logger.warning("Database verification failed after initialization");

                if (attempt < MAX_RETRY_COUNT) {
                    Thread.sleep(RETRY_DELAY_MS);
                }

            } catch (SQLException e) {
                logger.severe("Database initialization failed: " + e.getMessage());
                if (attempt == MAX_RETRY_COUNT) {
                    throw new RuntimeException("Failed to initialize database after " + MAX_RETRY_COUNT + " attempts", e);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Database initialization interrupted", e);
            }
        }
    }

    private static final String CREATE_WIFI_INFO_TABLE =
            "CREATE TABLE IF NOT EXISTS WIFI_INFO (" +
                    "    MGR_NO TEXT PRIMARY KEY," +
                    "    DISTANCE REAL," +
                    "    DISTRICT TEXT," +
                    "    NAME TEXT," +
                    "    ROAD_ADDRESS TEXT," +
                    "    DETAIL_ADDRESS TEXT," +
                    "    INSTALL_FLOOR TEXT," +
                    "    INSTALL_TYPE TEXT," +
                    "    INSTALL_AGENCY TEXT," +
                    "    SERVICE_TYPE TEXT," +
                    "    NET_TYPE TEXT," +
                    "    INSTALL_YEAR TEXT," +
                    "    IN_OUT_DOOR TEXT," +
                    "    WIFI_ENVIRONMENT TEXT," +
                    "    LAT REAL," +
                    "    LNT REAL," +
                    "    WORK_DATE TEXT" +
                    ")";

    private static final String CREATE_LOCATION_HISTORY_TABLE =
            "CREATE TABLE IF NOT EXISTS LOCATION_HISTORY (" +
                    "    ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    LAT REAL," +
                    "    LNT REAL," +
                    "    SEARCH_DATE TEXT," +
                    "    DELETE_YN INTEGER" +
                    ")";

    private static final String CREATE_BOOKMARK_GROUP_TABLE =
            "CREATE TABLE IF NOT EXISTS BOOKMARK_GROUP (" +
                    "    ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    NAME TEXT," +
                    "    ORDER_NO INTEGER," +
                    "    REG_DATE TEXT," +
                    "    MOD_DATE TEXT" +
                    ")";

    private static final String CREATE_BOOKMARK_TABLE =
            "CREATE TABLE IF NOT EXISTS BOOKMARK (" +
                    "    ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    GROUP_ID INTEGER," +
                    "    WIFI_MGR_NO TEXT," +
                    "    REG_DATE TEXT," +
                    "    FOREIGN KEY (GROUP_ID) REFERENCES BOOKMARK_GROUP(ID)," +
                    "    FOREIGN KEY (WIFI_MGR_NO) REFERENCES WIFI_INFO(MGR_NO)" +
                    ")";

    private static final String[] TABLE_NAMES = {
            "WIFI_INFO", "LOCATION_HISTORY", "BOOKMARK_GROUP", "BOOKMARK"
    };

    public void createTables() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        boolean wasInTransaction = false;

        try {
            conn = dbConfig.getConnection();
            wasInTransaction = dbConfig.isInTransaction();

            if (!wasInTransaction) {
                dbConfig.beginTransaction();
            }

            stmt = conn.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");

            stmt.execute(CREATE_WIFI_INFO_TABLE);
            stmt.execute(CREATE_LOCATION_HISTORY_TABLE);
            stmt.execute(CREATE_BOOKMARK_GROUP_TABLE);
            stmt.execute(CREATE_BOOKMARK_TABLE);

            if (!wasInTransaction) {
                dbConfig.commit();
            }

            logger.info("Tables created successfully");

        } catch (SQLException e) {
            logger.severe("Error creating tables: " + e.getMessage());
            try {
                if (!wasInTransaction && dbConfig.isInTransaction()) {
                    dbConfig.rollback();
                }
            } catch (SQLException ex) {
                logger.severe("Error rolling back transaction: " + ex.getMessage());
            }
            throw e;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null && !wasInTransaction) {
                    dbConfig.closeConnection();
                }
            } catch (SQLException e) {
                logger.severe("Error closing resources: " + e.getMessage());
            }
        }
    }

    public void dropTables() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        boolean wasInTransaction = false;

        try {
            conn = dbConfig.getConnection();
            wasInTransaction = dbConfig.isInTransaction();

            if (!wasInTransaction) {
                dbConfig.beginTransaction();
            }

            stmt = conn.createStatement();

            stmt.execute("DROP TABLE IF EXISTS BOOKMARK");
            stmt.execute("DROP TABLE IF EXISTS BOOKMARK_GROUP");
            stmt.execute("DROP TABLE IF EXISTS LOCATION_HISTORY");
            stmt.execute("DROP TABLE IF EXISTS WIFI_INFO");

            if (!wasInTransaction) {
                dbConfig.commit();
            }

            logger.info("Tables dropped successfully");

        } catch (SQLException e) {
            logger.severe("Error dropping tables: " + e.getMessage());
            try {
                if (!wasInTransaction && dbConfig.isInTransaction()) {
                    dbConfig.rollback();
                }
            } catch (SQLException ex) {
                logger.severe("Error rolling back transaction: " + ex.getMessage());
            }
            throw e;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null && !wasInTransaction) {
                    dbConfig.closeConnection();
                }
            } catch (SQLException e) {
                logger.severe("Error closing resources: " + e.getMessage());
            }
        }
    }

    public boolean isInitialized() {
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if all required tables exist
            for (String tableName : TABLE_NAMES) {
                String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    if (!rs.next()) {
                        logger.info("Table " + tableName + " not found");
                        return false;
                    }
                }
            }

            return true;

        } catch (SQLException e) {
            logger.severe("Error checking database initialization: " + e.getMessage());
            return false;
        }
    }

    private boolean verifyTables() {
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Verify all tables were created
            for (String tableName : TABLE_NAMES) {
                String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    if (!rs.next()) {
                        logger.warning("Verification failed: Table " + tableName + " not found");
                        return false;
                    }
                }
            }

            String sql = "PRAGMA foreign_key_list(BOOKMARK)";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                boolean hasGroupIdFK = false;
                boolean hasWifiMgrNoFK = false;

                while (rs.next()) {
                    String col = rs.getString("from");
                    String ref = rs.getString("table");

                    if ("GROUP_ID".equals(col) && "BOOKMARK_GROUP".equals(ref)) {
                        hasGroupIdFK = true;
                    }
                    if ("WIFI_MGR_NO".equals(col) && "WIFI_INFO".equals(ref)) {
                        hasWifiMgrNoFK = true;
                    }
                }

                if (!hasGroupIdFK || !hasWifiMgrNoFK) {
                    logger.warning("Verification failed: Missing foreign key constraints in BOOKMARK table");
                    return false;
                }
            }

            return true;

        } catch (SQLException e) {
            logger.severe("Error verifying tables: " + e.getMessage());
            return false;
        }
    }
}