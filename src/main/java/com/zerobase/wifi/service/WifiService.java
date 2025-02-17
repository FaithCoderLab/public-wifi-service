package com.zerobase.wifi.service;

import com.zerobase.wifi.model.dto.WifiDto;

import java.util.List;

public interface WifiService {
    int loadWifiData();
    List<WifiDto> findNearbyWifi(double lat, double lnt);
    WifiDto getWifiDetail(String mgrNo);
}
