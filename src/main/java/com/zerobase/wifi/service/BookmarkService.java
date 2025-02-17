package com.zerobase.wifi.service;

import com.zerobase.wifi.model.dto.BookmarkDto;

import java.util.List;

public interface BookmarkService {
    void addBookmark(Long groupId, String wifiMgrNo);
    List<BookmarkDto> getBookmarkList(Long groupId);
    void deleteBookmark(Long id);
    BookmarkDto getBookmarkDetail(Long id);
}
