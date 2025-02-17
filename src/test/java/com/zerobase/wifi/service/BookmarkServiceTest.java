package com.zerobase.wifi.service;

import com.zerobase.wifi.model.dto.BookmarkDto;
import com.zerobase.wifi.model.entity.Wifi;
import com.zerobase.wifi.repository.WifiRepository;
import com.zerobase.wifi.repository.WifiRepositoryImpl;
import com.zerobase.wifi.util.DatabaseInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class BookmarkServiceTest {
    private BookmarkService bookmarkService;
    private BookmarkGroupService bookmarkGroupService;
    private WifiRepository wifiRepository;
    private DatabaseInitializer initializer;

    @Before
    public void setUp() {
        bookmarkService = BookmarkServiceImpl.getInstance();
        bookmarkGroupService = BookmarkGroupServiceImpl.getInstance();
        wifiRepository = WifiRepositoryImpl.getInstance();
        initializer = DatabaseInitializer.getInstance();
        initializer.initializeDatabase();

        setupTestData();
    }

    private void setupTestData() {
        Wifi wifi = Wifi.builder()
                .mgrNo("TEST001")
                .name("테스트 와이파이")
                .lat(37.5665)
                .lnt(126.9780)
                .build();
        wifiRepository.save(wifi);

        bookmarkGroupService.addBookmarkGroup("테스트 그룹", 1);
    }

    @Test
    public void testAddAndGetBookmark() {
        List<BookmarkDto> beforeAdd = bookmarkService.getBookmarkList(1L);

        bookmarkService.addBookmark(1L, "TEST001");
        List<BookmarkDto> afterAdd = bookmarkService.getBookmarkList(1L);

        assertTrue(beforeAdd.isEmpty());
        assertFalse(afterAdd.isEmpty());
        assertEquals(1, afterAdd.size());
        assertEquals("테스트 그룹", afterAdd.get(0).getGroupName());
        assertEquals("테스트 와이파이", afterAdd.get(0).getWifiName());
    }

    @Test
    public void testGetBookmarkDetail() {
        bookmarkService.addBookmark(1L, "TEST001");
        List<BookmarkDto> bookmarks = bookmarkService.getBookmarkList(1L);
        Long bookmarkId = bookmarks.get(0).getId();

        BookmarkDto detail = bookmarkService.getBookmarkDetail(bookmarkId);

        assertNotNull(detail);
        assertEquals("테스트 그룹", detail.getGroupName());
        assertEquals("테스트 와이파이", detail.getWifiName());
        assertEquals("TEST001", detail.getWifiMgrNo());
    }

    @Test
    public void testDeleteBookmark() {
        bookmarkService.addBookmark(1L, "TEST001");
        List<BookmarkDto> beforeDelete = bookmarkService.getBookmarkList(1L);
        Long bookmarkId = beforeDelete.get(0).getId();

        bookmarkService.deleteBookmark(bookmarkId);
        List<BookmarkDto> afterDelete = bookmarkService.getBookmarkList(1L);

        assertFalse(beforeDelete.isEmpty());
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    public void testGetBookmarkByGroup() {
        bookmarkGroupService.addBookmarkGroup("테스트 그룹2", 2);

        bookmarkService.addBookmark(1L, "TEST001");
        bookmarkService.addBookmark(2L, "TEST001");

        List<BookmarkDto> group1Bookmarks = bookmarkService.getBookmarkList(1L);
        List<BookmarkDto> group2Bookmarks = bookmarkService.getBookmarkList(2L);

        assertEquals(1, group1Bookmarks.size());
        assertEquals(1, group2Bookmarks.size());
        assertEquals("테스트 그룹", group1Bookmarks.get(0).getGroupName());
        assertEquals("테스트 그룹2", group2Bookmarks.get(0).getGroupName());
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
