package com.zerobase.wifi.repository;

import com.zerobase.wifi.model.entity.History;
import com.zerobase.wifi.util.DatabaseInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class HistoryRepositoryTest {
    private HistoryRepository historyRepository;
    private DatabaseInitializer initializer;

    @Before
    public void setUp() {
        historyRepository = HistoryRepositoryImpl.getInstance();
        initializer = DatabaseInitializer.getInstance();
        initializer.initializeDatabase();
    }

    @Test
    public void testSave() {
        History history = History.builder()
                .lat(37.5665)
                .lnt(126.9870)
                .searchDate("2024-02-17 14:30:00")
                .deleteYn(false)
                .build();

        historyRepository.save(history);

        List<History> histories = historyRepository.findAll();
        assertFalse(histories.isEmpty());
        History saved = histories.get(0);
        assertEquals(37.5665, saved.getLat(), 0.0001);
        assertEquals(126.9870, saved.getLnt(), 0.0001);
        assertEquals("2024-02-17 14:30:00", saved.getSearchDate());
        assertFalse(saved.isDeleteYn());
    }

    @Test
    public void testFindAll() {
        History history1 = History.builder()
                .lat(37.5665)
                .lnt(126.9870)
                .searchDate("2024-02-17 14:30:00")
                .deleteYn(false)
                .build();

        History history2 = History.builder()
                .lat(37.5668)
                .lnt(126.9872)
                .searchDate("2024-02-17 14:35:00")
                .deleteYn(false)
                .build();

        historyRepository.save(history1);
        historyRepository.save(history2);

        List<History> histories = historyRepository.findAll();

        assertNotNull(histories);
        assertEquals(2, histories.size());
        assertEquals("2024-02-17 14:35:00", histories.get(0).getSearchDate());
        assertEquals("2024-02-17 14:30:00", histories.get(1).getSearchDate());
    }

    @Test
    public void testDeleteById() {
        History history = History.builder()
                .lat(37.5665)
                .lnt(126.9780)
                .searchDate("2024-02-17 14:30:00")
                .deleteYn(false)
                .build();

        historyRepository.save(history);
        List<History> histories = historyRepository.findAll();
        assertFalse(histories.isEmpty());
        Long savedId = histories.get(0).getId();

        historyRepository.deleteById(savedId);

        List<History> afterDelete = historyRepository.findAll();
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    public void testDeleteAll() {
        History history1 = History.builder()
                .lat(37.5665)
                .lnt(126.9780)
                .searchDate("2024-02-17 14:30:00")
                .deleteYn(false)
                .build();

        History history2 = History.builder()
                .lat(37.5668)
                .lnt(126.9783)
                .searchDate("2024-02-17 14:35:00")
                .deleteYn(false)
                .build();

        historyRepository.save(history1);
        historyRepository.save(history2);

        List<History> beforeDelete = historyRepository.findAll();
        assertEquals(2, beforeDelete.size());

        historyRepository.deleteAll();

        List<History> afterDelete = historyRepository.findAll();
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
