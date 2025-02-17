package com.zerobase.wifi.service;

import com.zerobase.wifi.model.dto.BookmarkGroupDto;

import java.util.List;

public interface BookmarkGroupService {
    void addBookmarkGroup(String name, int order);
    List<BookmarkGroupDto> getBookmarkGroupList();
    void editBookmarkGroup(Long id, String name, int order);
    void deleteBookmarkGroup(Long id);
}
