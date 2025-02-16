package com.zerobase.wifi.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DatabaseInitializerTest {
    private DatabaseInitializer initializer;

    @Before
    public void setUp() {
        initializer = DatabaseInitializer.getInstance();
    }

    @Test
    public void testGetInstance() {
        assertNotNull(initializer);

        DatabaseInitializer anotherInstance = DatabaseInitializer.getInstance();
        assertSame(initializer, anotherInstance);
    }

    @Test
    public void testInitializeDatabase() {
        try {
            initializer.dropTables();
            assertFalse(initializer.isInitialized());

            initializer.initializeDatabase();

            assertTrue(initializer.isInitialized());
        } catch (SQLException e) {
            fail("Failed to initialize database: " + e.getMessage());
        }
    }

    @Test
    public void testCreateAndDropTables() {
        try {
            initializer.dropTables();
            assertFalse(initializer.isInitialized());

            initializer.createTables();
            assertTrue(initializer.isInitialized());

            initializer.dropTables();
            assertFalse(initializer.isInitialized());
        } catch (SQLException e) {
            fail("Failed to initialize database: " + e.getMessage());
        }
    }

    @Test
    public void testIsInitialized() {
        try {
            initializer.dropTables();
            assertFalse(initializer.isInitialized());

            initializer.createTables();
            assertTrue(initializer.isInitialized());
        } catch (SQLException e) {
            fail("Failed to initialize database: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        try {
            initializer.dropTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
