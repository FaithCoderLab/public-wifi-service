package com.zerobase.wifi.service;

import com.zerobase.wifi.model.dto.HistoryDto;
import com.zerobase.wifi.model.entity.History;
import com.zerobase.wifi.repository.HistoryRepository;
import com.zerobase.wifi.repository.HistoryRepositoryImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryServiceImpl implements HistoryService {
    private final HistoryRepository historyRepository;
    private static HistoryServiceImpl instance;

    private HistoryServiceImpl() {
        this.historyRepository = HistoryRepositoryImpl.getInstance();
    }

    public static synchronized HistoryServiceImpl getInstance() {
        if (instance == null) {
            instance = new HistoryServiceImpl();
        }
        return instance;
    }

    @Override
    public void saveHistory(double lat, double lnt) {
        History history = History.builder()
                .lat(lat)
                .lnt(lnt)
                .searchDate(LocalDateTime.now().toString())
                .deleteYn(false)
                .build();

        historyRepository.save(history);
    }

    @Override
    public List<HistoryDto> getHistoryList() {
        return historyRepository.findAll().stream()
                .map(HistoryDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteHistory(Long id) {
        historyRepository.deleteById(id);
    }
}
