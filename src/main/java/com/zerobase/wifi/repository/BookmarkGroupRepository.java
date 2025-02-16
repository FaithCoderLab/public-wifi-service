package com.zerobase.wifi.repository;

import com.zerobase.wifi.model.entity.BookmarkGroup;

import java.util.List;

public interface BookmarkGroupRepository {
    void save(BookmarkGroup bookmarkGroup);
    BookmarkGroup findById(Long id);
    List<BookmarkGroup> findAll();
    void update(BookmarkGroup bookmarkGroup);
    void deleteById(Long id);
    boolean exists(Long id);
}
