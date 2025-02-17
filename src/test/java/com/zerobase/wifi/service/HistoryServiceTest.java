package com.zerobase.wifi.service;

import com.zerobase.wifi.model.dto.HistoryDto;
import com.zerobase.wifi.util.DatabaseInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class HistoryServiceTest {
    private HistoryService historyService;
    private DatabaseInitializer initializer;

    @Before
    public void setUp() {
        historyService = HistoryServiceImpl.getInstance();
        initializer = DatabaseInitializer.getInstance();
        initializer.initializeDatabase();
    }

    @Test
    public void testSaveAndGetHistory() {
        double lat = 37.5665;
        double lnt = 126.9780;

        historyService.saveHistory(lat, lnt);
        List<HistoryDto> historyList = historyService.getHistoryList();

        assertFalse(historyList.isEmpty());
        assertEquals(1, historyList.size());
        assertEquals(lat, historyList.get(0).getLat(), 0.0001);
        assertEquals(lnt, historyList.get(0).getLnt(), 0.0001);
    }

    @Test
    public void testDeleteHistory() {
        historyService.saveHistory(37.5665, 126.9780);
        List<HistoryDto> beforeDelete = historyService.getHistoryList();
        Long historyId = beforeDelete.get(0).getId();

        historyService.deleteHistory(historyId);
        List<HistoryDto> afterDelete = historyService.getHistoryList();

        assertFalse(beforeDelete.isEmpty());
        assertTrue(afterDelete.isEmpty());
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
