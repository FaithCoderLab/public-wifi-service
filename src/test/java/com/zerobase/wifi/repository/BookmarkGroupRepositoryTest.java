package com.zerobase.wifi.repository;

import com.zerobase.wifi.model.entity.BookmarkGroup;
import com.zerobase.wifi.util.DatabaseInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class BookmarkGroupRepositoryTest {
    private BookmarkGroupRepository bookmarkGroupRepository;
    private DatabaseInitializer initializer;

    @Before
    public void setUp() {
        bookmarkGroupRepository = BookmarkGroupRepositoryImpl.getInstance();
        initializer = DatabaseInitializer.getInstance();
        initializer.initializeDatabase();
    }

    @Test
    public void testSave() {
        BookmarkGroup bookmarkGroup = BookmarkGroup.builder()
                .name("즐겨찾기 그룹1")
                .orderNo(1)
                .regDate("2024-02-17 14:30:30")
                .modDate("2024-02-17 14:30:30")
                .build();

        bookmarkGroupRepository.save(bookmarkGroup);

        List<BookmarkGroup> groups = bookmarkGroupRepository.findAll();
        assertFalse(groups.isEmpty());
        BookmarkGroup saved = groups.get(0);
        assertEquals("즐겨찾기 그룹1", saved.getName());
        assertEquals(1, saved.getOrderNo());
    }

    @Test
    public void testFindById() {
        BookmarkGroup bookmarkGroup = BookmarkGroup.builder()
                .name("테스트 그룹")
                .orderNo(1)
                .regDate("2024-02-17 14:30:30")
                .modDate("2024-02-17 14:30:30")
                .build();

        bookmarkGroupRepository.save(bookmarkGroup);
        List<BookmarkGroup> groups = bookmarkGroupRepository.findAll();
        Long savedId = groups.get(0).getId();

        BookmarkGroup found = bookmarkGroupRepository.findById(savedId);

        assertNotNull(found);
        assertEquals("테스트 그룹", found.getName());
        assertEquals(1, found.getOrderNo());

        BookmarkGroup notFound = bookmarkGroupRepository.findById(9999L);
        assertNull(notFound);
    }

    @Test
    public void testFindAll() {
        BookmarkGroup group1 = BookmarkGroup.builder()
                .name("그룹1")
                .orderNo(1)
                .regDate("2024-02-17 14:30:30")
                .modDate("2024-02-17 14:30:30")
                .build();

        BookmarkGroup group2 = BookmarkGroup.builder()
                .name("그룹2")
                .orderNo(2)
                .regDate("2024-02-17 14:30:30")
                .modDate("2024-02-17 14:30:30")
                .build();

        bookmarkGroupRepository.save(group1);
        bookmarkGroupRepository.save(group2);

        List<BookmarkGroup> groups = bookmarkGroupRepository.findAll();

        assertNotNull(groups);
        assertEquals(2, groups.size());
        assertEquals("그룹1", groups.get(0).getName());
        assertEquals("그룹2", groups.get(1).getName());
    }

    @Test
    public void testUpdate() {
        BookmarkGroup group = BookmarkGroup.builder()
                .name("원래이름")
                .orderNo(1)
                .regDate("2024-02-17 14:30:30")
                .modDate("2024-02-17 14:30:30")
                .build();

        bookmarkGroupRepository.save(group);
        List<BookmarkGroup> groups = bookmarkGroupRepository.findAll();
        Long savedId = groups.get(0).getId();

        BookmarkGroup updateGroup = BookmarkGroup.builder()
                .id(savedId)
                .name("변경이름")
                .orderNo(2)
                .regDate("2024-02-17 14:30:30")
                .modDate("2025-02-17 14:30:00")
                .build();

        bookmarkGroupRepository.update(updateGroup);

        BookmarkGroup updated = bookmarkGroupRepository.findById(savedId);
        assertNotNull(updated);
        assertEquals("변경이름", updated.getName());
        assertEquals(2, updated.getOrderNo());
        assertEquals("2025-02-17 14:30:00", updated.getModDate());
    }

    @Test
    public void testDeleteById() {
        BookmarkGroup group = BookmarkGroup.builder()
                .name("삭제그룹")
                .orderNo(1)
                .regDate("2024-02-17 14:30:00")
                .modDate("2024-02-17 14:30:30")
                .build();

        bookmarkGroupRepository.save(group);
        List<BookmarkGroup> groups = bookmarkGroupRepository.findAll();
        Long savedId = groups.get(0).getId();

        bookmarkGroupRepository.deleteById(savedId);
        assertNull(bookmarkGroupRepository.findById(savedId));
    }

    @Test
    public void testExists() {
        BookmarkGroup group = BookmarkGroup.builder()
                .name("존재확인그룹")
                .orderNo(1)
                .regDate("2024-02-17 14:30:30")
                .modDate("2024-02-17 14:30:30")
                .build();

        bookmarkGroupRepository.save(group);
        List<BookmarkGroup> groups = bookmarkGroupRepository.findAll();
        Long savedId = groups.get(0).getId();

        assertTrue(bookmarkGroupRepository.exists(savedId));
        assertFalse(bookmarkGroupRepository.exists(savedId + 1));
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
