package com.zerobase.wifi.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DatabaseInitializerTest {
    private DatabaseInitializer initializer;
    private DatabaseConfig dbConfig;

    @Before
    public void setUp() {
        initializer = DatabaseInitializer.getInstance();
        dbConfig = DatabaseConfig.getInstance();
    }

    @Test
    public void testGetInstance() {
        DatabaseInitializer instance1 = DatabaseInitializer.getInstance();
        DatabaseInitializer instance2 = DatabaseInitializer.getInstance();

        assertNotNull("Instance should not be null", instance1);
        assertSame("Should return the same instance", instance1, instance2);
    }

    @Test
    public void testInitializeDatabase() throws SQLException {
        assertFalse("Database should not be initialized initially", initializer.isInitialized());

        initializer.initializeDatabase();

        assertTrue("Database should be initialized after initialization", initializer.isInitialized());

        assertTrue("WIFI_INFO table should exist", tableExists("WIFI_INFO"));
        assertTrue("LOCATION_HISTORY table should exist", tableExists("LOCATION_HISTORY"));
        assertTrue("BOOKMARK_GROUP table should exist", tableExists("BOOKMARK_GROUP"));
        assertTrue("BOOKMARK table should exist", tableExists("BOOKMARK"));
    }

    @Test
    public void testReInitializeDatabase() throws SQLException {
        initializer.initializeDatabase();
        assertTrue("Database should be initialized", initializer.isInitialized());

        initializer.initializeDatabase();
        assertTrue("Database should still be initialized", initializer.isInitialized());

        String[] expectedTables = {"WIFI_INFO", "LOCATION_HISTORY", "BOOKMARK_GROUP", "BOOKMARK"};
        for (String tableName : expectedTables) {
            assertTrue(tableName + " table should exist", tableExists(tableName));
        }
    }

    @Test
    public void testTableStructure() throws SQLException {
        initializer.initializeDatabase();

        List<String> wifiInfoColumns = getTableColumns("WIFI_INFO");
        assertTrue(wifiInfoColumns.containsAll(Arrays.asList(
                "MGR_NO", "DISTANCE", "DISTRICT", "NAME", "ROAD_ADDRESS",
                "DETAIL_ADDRESS", "INSTALL_FLOOR", "INSTALL_TYPE", "INSTALL_AGENCY",
                "SERVICE_TYPE", "NET_TYPE", "INSTALL_YEAR", "IN_OUT_DOOR",
                "WIFI_ENVIRONMENT", "LAT", "LNT", "WORK_DATE"
        )));

        List<String> historyColumns = getTableColumns("LOCATION_HISTORY");
        assertTrue(historyColumns.containsAll(Arrays.asList(
                "ID", "LAT", "LNT", "SEARCH_DATE", "DELETE_YN"
        )));

        List<String> groupColumns = getTableColumns("BOOKMARK_GROUP");
        assertTrue(groupColumns.containsAll(Arrays.asList(
                "ID", "NAME", "ORDER_NO", "REG_DATE", "MOD_DATE"
        )));

        List<String> bookmarkColumns = getTableColumns("BOOKMARK");
        assertTrue(bookmarkColumns.containsAll(Arrays.asList(
                "ID", "GROUP_ID", "WIFI_MGR_NO", "REG_DATE"
        )));
    }

    @Test
    public void testForeignKeyConstraints() throws SQLException {
        initializer.initializeDatabase();

        List<ForeignKey> foreignKeys = getForeignKeys("BOOKMARK");

        boolean hasGroupIdFK = false;
        boolean hasWifiMgrNoFK = false;

        for (ForeignKey fk : foreignKeys) {
            if ("GROUP_ID".equals(fk.fromColumn) && "BOOKMARK_GROUP".equals(fk.toTable)) {
                hasGroupIdFK = true;
            }
            if ("WIFI_MGR_NO".equals(fk.fromColumn) && "WIFI_INFO".equals(fk.toTable)) {
                hasWifiMgrNoFK = true;
            }
        }

        assertTrue("BOOKMARK table should have foreign key to BOOKMARK_GROUP", hasGroupIdFK);
        assertTrue("BOOKMARK table should have foreign key to WIFI_INFO", hasWifiMgrNoFK);
    }

    @Test
    public void testDropTables() throws SQLException {
        initializer.initializeDatabase();
        assertTrue("Database should be initialized", initializer.isInitialized());

        initializer.dropTables();
        assertFalse("Database should not be initialized after dropping tables", initializer.isInitialized());

        String[] tables = {"WIFI_INFO", "LOCATION_HISTORY", "BOOKMARK_GROUP", "BOOKMARK"};
        for (String tableName : tables) {
            assertFalse(tableName + " table should not exist", tableExists(tableName));
        }
    }

    private boolean tableExists(String tableName) throws SQLException {
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                return rs.next();
            }
        }
    }

    private List<String> getTableColumns(String tableName) throws SQLException {
        List<String> columns = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = String.format("PRAGMA table_info(%s)", tableName);
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    columns.add(rs.getString("name"));
                }
            }
        }
        return columns;
    }

    private static class ForeignKey {
        String fromColumn;
        String toTable;
        String toColumn;

        ForeignKey(String fromColumn, String toTable, String toColumn) {
            this.fromColumn = fromColumn;
            this.toTable = toTable;
            this.toColumn = toColumn;
        }
    }

    private List<ForeignKey> getForeignKeys(String tableName) throws SQLException {
        List<ForeignKey> foreignKeys = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = String.format("PRAGMA foreign_key_list(%s)", tableName);
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    foreignKeys.add(new ForeignKey(
                            rs.getString("from"),
                            rs.getString("table"),
                            rs.getString("to")
                    ));
                }
            }
        }
        return foreignKeys;
    }

    @After
    public void tearDown() {
        try {
            initializer.dropTables();
            dbConfig.closeConnection();
        } catch (SQLException e) {

        }
    }
}