package com.zerobase.wifi.repository;

import com.zerobase.wifi.model.entity.History;

import java.util.List;

public interface HistoryRepository {
    void save(History history);
    List<History> findAll();
    void deleteById(Long id);
    void deleteAll();
}
