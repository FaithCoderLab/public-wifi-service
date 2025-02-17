package com.zerobase.wifi.repository;

import com.zerobase.wifi.model.entity.Bookmark;
import com.zerobase.wifi.model.entity.BookmarkGroup;
import com.zerobase.wifi.model.entity.Wifi;
import com.zerobase.wifi.util.DatabaseInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class BookmarkRepositoryTest {
    private BookmarkRepository bookmarkRepository;
    private DatabaseInitializer initializer;

    @Before
    public void setUp() {
        bookmarkRepository = BookmarkRepositoryImpl.getInstance();
        initializer = DatabaseInitializer.getInstance();
        initializer.initializeDatabase();
    }

    @Test
    public void testSave() {
        Bookmark bookmark = Bookmark.builder()
                .groupId(1L)
                .wifiMgrNo("TEST001")
                .regDate("2024-02-17 14:30:00")
                .build();

        bookmarkRepository.save(bookmark);

        Bookmark saved = bookmarkRepository.findById(1L);
        assertNotNull(saved);
        assertEquals(Long.valueOf(1L), saved.getGroupId());
        assertEquals("TEST001", saved.getWifiMgrNo());
        assertEquals("2024-02-17 14:30:00", saved.getRegDate());
    }

    @Test
    public void testFindById() {
        Bookmark bookmark = Bookmark.builder()
                .groupId(1L)
                .wifiMgrNo("TEST002")
                .regDate("2024-02-17 14:30:00")
                .build();

        bookmarkRepository.save(bookmark);

        Bookmark found = bookmarkRepository.findById(1L);

        assertNotNull("Bookmark should not be null", found);
        assertEquals(Long.valueOf(1L), found.getGroupId());
        assertEquals("TEST002", found.getWifiMgrNo());

        Bookmark notFound = bookmarkRepository.findById(2L);
        assertNull("Non-existend ID should return null", notFound);
    }

    @Test
    public void testFindByWifiMgrNo() {
        BookmarkGroup group = BookmarkGroup.builder()
                .name("테스트 그룹")
                .orderNo(1)
                .regDate("2024-02-17 14:30:00")
                .modDate("2024-02-17 14:30:00")
                .build();

        BookmarkGroupRepository groupRepository = BookmarkGroupRepositoryImpl.getInstance();
        groupRepository.save(group);

        WifiRepository wifiRepository = WifiRepositoryImpl.getInstance();
        Wifi wifi1 = Wifi.builder()
                .mgrNo("TEST003")
                .name("테스트 와이파이1")
                .lat(37.5665)
                .lnt(126.9780)
                .build();

        Wifi wifi2 = Wifi.builder()
                .mgrNo("TEST004")
                .name("테스트 와이파이2")
                .lat(37.5665)
                .lnt(126.9780)
                .build();

        wifiRepository.save(wifi1);
        wifiRepository.save(wifi2);

        Bookmark bookmark1 = Bookmark.builder()
                .groupId(1L)
                .wifiMgrNo("TEST003")
                .regDate("2024-02-17 14:30:00")
                .build();

        Bookmark bookmark2 = Bookmark.builder()
                .groupId(1L)
                .wifiMgrNo("TEST004")
                .regDate("2025-02-17 14:30:00")
                .build();

        bookmarkRepository.save(bookmark1);
        bookmarkRepository.save(bookmark2);

        List<Bookmark> bookmarks = bookmarkRepository.findByGroupId(1L);

        assertNotNull(bookmarks);
        assertEquals(2, bookmarks.size());
        assertTrue(bookmarks.stream()
                .anyMatch(bookmark -> bookmark.getWifiMgrNo().equals("TEST003")));
        assertTrue(bookmarks.stream()
                .anyMatch(bookmark -> bookmark.getWifiMgrNo().equals("TEST004")));

        List<Bookmark> emptyGroup = bookmarkRepository.findByGroupId(2L);
        assertTrue(emptyGroup.isEmpty());
    }

    @Test
    public void testDeleteById() {
        Bookmark bookmark = Bookmark.builder()
                .groupId(1L)
                .wifiMgrNo("TEST005")
                .regDate("2024-02-17 14:30:00")
                .build();

        bookmarkRepository.save(bookmark);
        assertTrue(bookmarkRepository.exists(1L));

        bookmarkRepository.deleteById(1L);
        assertFalse(bookmarkRepository.exists(1L));
    }

    @Test
    public void testDeleteByGroupId() {
        Bookmark bookmark1 = Bookmark.builder()
                .groupId(1L)
                .wifiMgrNo("TEST006")
                .regDate("2024-02-17 14:30:00")
                .build();

        Bookmark bookmark2 = Bookmark.builder()
                .groupId(1L)
                .wifiMgrNo("TEST007")
                .regDate("2025-02-17 14:30:00")
                .build();
        bookmarkRepository.save(bookmark1);
        bookmarkRepository.save(bookmark2);

        List<Bookmark> beforeDelete = bookmarkRepository.findByGroupId(1L);
        assertEquals(2, beforeDelete.size());

        bookmarkRepository.deleteById(1L);

        List<Bookmark> afterDelete = bookmarkRepository.findByGroupId(1L);
        assertFalse(afterDelete.isEmpty());
    }

    @Test
    public void testExists() {
        Bookmark bookmark = Bookmark.builder()
                .groupId(1L)
                .wifiMgrNo("TEST008")
                .regDate("2024-02-17 14:30:00")
                .build();

        bookmarkRepository.save(bookmark);

        assertTrue(bookmarkRepository.exists(1L));
        assertFalse(bookmarkRepository.exists(2L));
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
