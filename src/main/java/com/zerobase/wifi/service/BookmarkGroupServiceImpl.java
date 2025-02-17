package com.zerobase.wifi.service;

import com.zerobase.wifi.model.dto.BookmarkGroupDto;
import com.zerobase.wifi.model.entity.BookmarkGroup;
import com.zerobase.wifi.repository.BookmarkGroupRepository;
import com.zerobase.wifi.repository.BookmarkGroupRepositoryImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BookmarkGroupServiceImpl implements BookmarkGroupService {
    private final BookmarkGroupRepository bookmarkGroupRepository;
    private static BookmarkGroupServiceImpl instance;

    private BookmarkGroupServiceImpl() {
        this.bookmarkGroupRepository = BookmarkGroupRepositoryImpl.getInstance();
    }

    public static synchronized BookmarkGroupServiceImpl getInstance() {
        if (instance == null) {
            instance = new BookmarkGroupServiceImpl();
        }
        return instance;
    }

    @Override
    public void addBookmarkGroup(String name, int order) {
        BookmarkGroup bookmarkGroup = BookmarkGroup.builder()
                .name(name)
                .orderNo(order)
                .regDate(LocalDateTime.now().toString())
                .modDate(LocalDateTime.now().toString())
                .build();

        bookmarkGroupRepository.save(bookmarkGroup);
    }

    @Override
    public List<BookmarkGroupDto> getBookmarkGroupList() {
        return bookmarkGroupRepository.findAll().stream()
                .map(BookmarkGroupDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public void editBookmarkGroup(Long id, String name, int order) {
        BookmarkGroup bookmarkGroup = BookmarkGroup.builder()
                .id(id)
                .name(name)
                .orderNo(order)
                .regDate(bookmarkGroupRepository.findById(id).getRegDate())
                .modDate(LocalDateTime.now().toString())
                .build();

        bookmarkGroupRepository.update(bookmarkGroup);
    }

    @Override
    public void deleteBookmarkGroup(Long id) {
        bookmarkGroupRepository.deleteById(id);
    }
}
