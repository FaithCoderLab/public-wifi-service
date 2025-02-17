package com.zerobase.wifi.service;

import com.zerobase.wifi.model.dto.BookmarkDto;
import com.zerobase.wifi.model.entity.Bookmark;
import com.zerobase.wifi.model.entity.BookmarkGroup;
import com.zerobase.wifi.model.entity.Wifi;
import com.zerobase.wifi.repository.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkGroupRepository bookmarkGroupRepository;
    private final WifiRepository wifiRepository;
    private static BookmarkServiceImpl instance;

    private BookmarkServiceImpl() {
        this.bookmarkRepository = BookmarkRepositoryImpl.getInstance();
        this.bookmarkGroupRepository = BookmarkGroupRepositoryImpl.getInstance();
        this.wifiRepository = WifiRepositoryImpl.getInstance();
    }

    public static synchronized BookmarkServiceImpl getInstance() {
        if (instance == null) {
            instance = new BookmarkServiceImpl();
        }
        return instance;
    }

    @Override
    public void addBookmark(Long groupId, String wifiMgrNo) {
        Bookmark bookmark = Bookmark.builder()
                .groupId(groupId)
                .wifiMgrNo(wifiMgrNo)
                .regDate(LocalDateTime.now().toString())
                .build();

        bookmarkRepository.save(bookmark);
    }

    @Override
    public List<BookmarkDto> getBookmarkList(Long groupId) {
        return bookmarkRepository.findByGroupId(groupId).stream()
                .map(bookmark -> {
                    BookmarkGroup group = bookmarkGroupRepository.findById(bookmark.getGroupId());
                    Wifi wifi = wifiRepository.findByMgrNo(bookmark.getWifiMgrNo());
                    return BookmarkDto.from(bookmark, group.getName(), wifi.getName());
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBookmark(Long id) {
        bookmarkRepository.deleteById(id);
    }

    @Override
    public BookmarkDto getBookmarkDetail(Long id) {
        Bookmark bookmark = bookmarkRepository.findById(id);
        if (bookmark == null) {
            return null;
        }

        BookmarkGroup group = bookmarkGroupRepository.findById(bookmark.getGroupId());
        Wifi wifi = wifiRepository.findByMgrNo(bookmark.getWifiMgrNo());

        return BookmarkDto.from(bookmark, group, wifi);
    }
}
