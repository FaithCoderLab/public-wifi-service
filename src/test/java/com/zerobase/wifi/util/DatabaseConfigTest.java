package com.zerobase.wifi.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DatabaseConfigTest {
    @Before
    public void setUp() {
        DatabaseConfig.getInstance().closeConnection();
    }

    @Test
    public void getInstance_ShouldReturnSameInstance() {
        DatabaseConfig config1 = DatabaseConfig.getInstance();
        DatabaseConfig config2 = DatabaseConfig.getInstance();

        assertSame(config1, config2);
    }

    @Test
    public void getConnection_ShouldCreateNewConnection() {
        DatabaseConfig config = DatabaseConfig.getInstance();
        Connection connection = config.getConnection();
        assertNotNull(connection);
        assertTrue(config.isConnected());
    }

    @Test
    public void closeConnection_ShouldCloseAndClearConnection() {
        DatabaseConfig config = DatabaseConfig.getInstance();
        config.getConnection();
        config.closeConnection();
        assertFalse(config.isConnected());
    }

    @After
    public void tearDown() throws Exception {
        DatabaseConfig.getInstance().closeConnection();

    }
}
