package com.zerobase.wifi.service;

import com.zerobase.wifi.model.dto.ToPublicWifiInfoDto;
import com.zerobase.wifi.model.dto.WifiDto;
import com.zerobase.wifi.model.entity.History;
import com.zerobase.wifi.model.entity.Wifi;
import com.zerobase.wifi.repository.HistoryRepository;
import com.zerobase.wifi.repository.HistoryRepositoryImpl;
import com.zerobase.wifi.repository.WifiRepository;
import com.zerobase.wifi.repository.WifiRepositoryImpl;
import com.zerobase.wifi.util.DatabaseConfig;
import com.zerobase.wifi.util.DatabaseInitializer;
import com.zerobase.wifi.util.WifiApiUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WifiServiceImpl implements WifiService {

    @Override
    public int loadWifiData() {
        int totalCount = getTotalWifiCount();
        int processedCount = 0;
        int pageSize = 1000;

        WifiRepository wifiRepository = WifiRepositoryImpl.getInstance();
        wifiRepository.deleteAll();

        WifiApiUtil wifiApiUtil = WifiApiUtil.getInstance();
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        DatabaseInitializer initializer = DatabaseInitializer.getInstance();
        initializer.initializeDatabase();

        for (int start = 1; start <= totalCount; start += pageSize) {
            int end = Math.min(start + pageSize - 1, totalCount);

            ToPublicWifiInfoDto wifiInfo = null;
            try {
                wifiInfo = wifiApiUtil.getPublicWifiInfo(start, end);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            List<WifiDto> wifiDtoList = wifiInfo.getRow();

            List<Wifi> wifiList = wifiDtoList.stream()
                    .map(dto -> Wifi.builder()
                            .mgrNo(dto.getMgrNo())
                            .district(dto.getDistrict())
                            .name(dto.getName())
                            .roadAddress(dto.getRoadAddress())
                            .detailAddress(dto.getDetailAddress())
                            .installFloor(dto.getInstallFloor())
                            .installType(dto.getInstallType())
                            .installAgency(dto.getInstallAgency())
                            .serviceType(dto.getServiceType())
                            .netType(dto.getNetType())
                            .installYear(dto.getInstallYear())
                            .inOutDoor(dto.getInOutDoor())
                            .lat(dto.getLat())
                            .lnt(dto.getLnt())
                            .workDate(dto.getWorkDate())
                            .build())
                    .collect(Collectors.toList());

            processedCount += wifiRepository.saveAll(wifiList);
        }

        return processedCount;
    }

    private int getTotalWifiCount() {
        try {
            WifiApiUtil wifiApiUtil = WifiApiUtil.getInstance();
            ToPublicWifiInfoDto infoDto = wifiApiUtil.getPublicWifiInfo(1, 1);
            return infoDto.getListTotalCount();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get total wifi count", e);
        }
    }

    @Override
    public List<WifiDto> findNearbyWifi(double lat, double lnt) {
        WifiRepository wifiRepository = WifiRepositoryImpl.getInstance();
        List<Wifi> nearbyWifi = wifiRepository.findNearbyWifi(lat, lnt, 20);

        History history = History.builder()
                .lat(lat)
                .lnt(lnt)
                .searchDate(LocalDateTime.now().toString())
                .deleteYn(false)
                .build();

        HistoryRepository historyRepository = HistoryRepositoryImpl.getInstance();
        historyRepository.save(history);

        return nearbyWifi.stream()
                .map(wifi -> {
                    return getWifiDto(wifi);
                })
                .collect(Collectors.toList());
    }

    @Override
    public WifiDto getWifiDetail(String mgrNo) {
        WifiRepository wifiRepository = WifiRepositoryImpl.getInstance();
        Wifi wifi = wifiRepository.findByMgrNo(mgrNo);

        if (wifi == null) {
            return null;
        }

        return getWifiDto(wifi);
    }

    @NotNull
    private WifiDto getWifiDto(Wifi wifi) {
        WifiDto dto = new WifiDto();
        dto.setMgrNo(wifi.getMgrNo());
        dto.setDistrict(wifi.getDistrict());
        dto.setName(wifi.getName());
        dto.setRoadAddress(wifi.getRoadAddress());
        dto.setDetailAddress(wifi.getDetailAddress());
        dto.setInstallFloor(wifi.getInstallFloor());
        dto.setInstallType(wifi.getInstallType());
        dto.setInstallAgency(wifi.getInstallAgency());
        dto.setInstallYear(wifi.getInstallYear());
        dto.setInOutDoor(wifi.getInOutDoor());
        dto.setLat(wifi.getLat());
        dto.setLnt(wifi.getLnt());
        dto.setWorkDate(wifi.getWorkDate());
        return dto;
    }
}
