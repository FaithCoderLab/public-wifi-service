package com.zerobase.wifi.service;

import com.zerobase.wifi.model.dto.HistoryDto;
import com.zerobase.wifi.model.entity.History;

import java.util.List;

public interface HistoryService {
    void saveHistory(double lat, double lnt);
    List<HistoryDto> getHistoryList();
    void deleteHistory(Long id);
}
