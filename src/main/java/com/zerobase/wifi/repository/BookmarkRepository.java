package com.zerobase.wifi.repository;

import com.zerobase.wifi.model.entity.Bookmark;

import java.util.List;

public interface BookmarkRepository {
    void save(Bookmark bookmark);
    Bookmark findById(Long id);
    List<Bookmark> findByGroupId(Long groupId);
    void deleteById(Long id);
    void deleteByGroupId(Long groupId);
    boolean exists(Long id);
}
