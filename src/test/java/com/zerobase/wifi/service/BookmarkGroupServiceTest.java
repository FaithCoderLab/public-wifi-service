package com.zerobase.wifi.service;

import com.zerobase.wifi.model.dto.BookmarkGroupDto;
import com.zerobase.wifi.model.entity.BookmarkGroup;
import com.zerobase.wifi.util.DatabaseInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class BookmarkGroupServiceTest {
    private BookmarkGroupService bookmarkGroupService;
    private DatabaseInitializer initializer;

    @Before
    public void setUp() {
        bookmarkGroupService = BookmarkGroupServiceImpl.getInstance();
        initializer = DatabaseInitializer.getInstance();
        initializer.initializeDatabase();
    }

    @Test
    public void testAddAndGetBookmarkGroup() {
        String name = "즐겨찾기 그룹1";
        int order = 1;

        bookmarkGroupService.addBookmarkGroup(name, order);
        List<BookmarkGroupDto> groupList = bookmarkGroupService.getBookmarkGroupList();

        assertFalse(groupList.isEmpty());
        assertEquals(1, groupList.size());
        assertEquals(name, groupList.get(0).getName());
        assertEquals(order, groupList.get(0).getOrderNo());
    }

    @Test
    public void testEditBookmarkGroup() {
        bookmarkGroupService.addBookmarkGroup("원래이름", 1);
        List<BookmarkGroupDto> groups = bookmarkGroupService.getBookmarkGroupList();
        Long groupId = groups.get(0).getId();

        String newName = "변경된이름";
        int newOrder = 2;
        bookmarkGroupService.editBookmarkGroup(groupId, newName, newOrder);

        List<BookmarkGroupDto> updatedGroups = bookmarkGroupService.getBookmarkGroupList();
        BookmarkGroupDto updated = updatedGroups.get(0);

        assertEquals(newName, updated.getName());
        assertEquals(newOrder, updated.getOrderNo());
        assertNotEquals(updated.getRegDate(), updated.getModDate());
    }

    @Test
    public void testDeleteBookmarkGroup() {
        bookmarkGroupService.addBookmarkGroup("삭제될그룹", 1);
        List<BookmarkGroupDto> beforeDelete = bookmarkGroupService.getBookmarkGroupList();
        Long groupId = beforeDelete.get(0).getId();

        bookmarkGroupService.deleteBookmarkGroup(groupId);
        List<BookmarkGroupDto> afterDelete = bookmarkGroupService.getBookmarkGroupList();

        assertFalse(beforeDelete.isEmpty());
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    public void testMultipleGroupOrdering() {
        bookmarkGroupService.addBookmarkGroup("그룹1", 1);
        bookmarkGroupService.addBookmarkGroup("그룹2", 2);
        bookmarkGroupService.addBookmarkGroup("그룹3", 3);

        List<BookmarkGroupDto> groups = bookmarkGroupService.getBookmarkGroupList();

        assertEquals(3, groups.size());
        assertEquals(1, groups.get(0).getOrderNo());
        assertEquals(2, groups.get(1).getOrderNo());
        assertEquals(3, groups.get(2).getOrderNo());
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
