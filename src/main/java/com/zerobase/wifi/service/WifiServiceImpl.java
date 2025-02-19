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

    public static WifiService getInstance() {
        return new WifiServiceImpl();
    }

    @Override
    public int loadWifiData() {
        System.out.println("Loading wifi data");
        int totalCount = getTotalWifiCount();
        System.out.println("Total wifi count: " + totalCount);

        int processedCount = 0;
        int pageSize = 1000;

        System.out.println("=== Initializing repository and database ===");
        WifiRepository wifiRepository = WifiRepositoryImpl.getInstance();
        DatabaseInitializer initializer = DatabaseInitializer.getInstance();
        WifiApiUtil wifiApiUtil = WifiApiUtil.getInstance();

        try {
            System.out.println("=== Initializing database ===");
            initializer.initializeDatabase();
            System.out.println("=== Database initialized ===");

            System.out.println("=== Deleting existing data ===");
            wifiRepository.deleteAll();
            System.out.println("=== Existing data deleted ===");

            for (int start = 1; start <= totalCount; start += pageSize) {
                int end = Math.min(start + pageSize - 1, totalCount);
                System.out.println("=== Processing data from " + start + " to " + end + " ===");

                try {
                    ToPublicWifiInfoDto wifiInfo = wifiApiUtil.getPublicWifiInfo(start, end);
                    List<WifiDto> wifiDtoList = wifiInfo.getRow();
                    System.out.println("Fetched " + wifiDtoList.size() + " records from API");

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

                    System.out.println("Saving " + wifiList.size() + " records to database");
                    int savedCount = wifiRepository.saveAll(wifiList);
                    processedCount += savedCount;
                    System.out.println("Successfully saved " + savedCount + " records");
                    System.out.println("Total processed count: " + processedCount);

                } catch (IOException e) {
                    System.err.println("Error fetching data from API: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("Error processing batch: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("=== Data loading completed ===");
            System.out.println("Total records processed: " + processedCount);
            return processedCount;

        } catch (Exception e) {
            System.err.println("=== Error during data loading process ===");
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load wifi data", e);
        }
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
