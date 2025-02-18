package com.zerobase.wifi.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DatabaseConfigTest {
    private DatabaseConfig config;

    @Before
    public void setUp() {
        config = DatabaseConfig.getInstance();
        config.closeConnection();
    }

    @Test
    public void getInstance_ShouldReturnSameInstance() {
        DatabaseConfig config1 = DatabaseConfig.getInstance();
        DatabaseConfig config2 = DatabaseConfig.getInstance();

        assertSame(config1, config2);
    }

    @Test
    public void getConnection_ShouldCreateNewConnection() {
        Connection connection = config.getConnection();
        assertNotNull(connection);
        assertTrue(config.isConnected());
    }

    @Test
    public void closeConnection_ShouldCloseAndClearConnection() {
        config.getConnection();
        config.closeConnection();
        assertFalse(config.isConnected());
    }

    @Test
    public void testTransactionManagement() throws SQLException {
        assertFalse(config.isInTransaction());
        config.beginTransaction();
        assertTrue(config.isInTransaction());

        config.commit();
        assertFalse(config.isInTransaction());

        config.beginTransaction();
        assertTrue(config.isInTransaction());
        config.rollback();
        assertFalse(config.isInTransaction());
    }

    @Test(expected = SQLException.class)
    public void beginTransaction_ShouldThrowException_WhenAlreadyInTransaction() throws SQLException {
        config.beginTransaction();
        config.beginTransaction();
    }

    @Test(expected = SQLException.class)
    public void commit_ShouldThrowException_WhenNoTransaction() throws SQLException {
        config.commit();
    }

    @Test(expected = SQLException.class)
    public void rollback_ShouldThrowException_WhenNoTransaction() throws SQLException {
        config.rollback();
    }

    @Test
    public void closeConnection_ShouldRollbackOpenTransaction() throws SQLException {
        config.beginTransaction();
        assertTrue(config.isInTransaction());
        config.closeConnection();
        assertFalse(config.isInTransaction());
        assertFalse(config.isConnected());
    }

    @After
    public void tearDown() throws Exception {
        DatabaseConfig.getInstance().closeConnection();

    }
}
