package com.zerobase.wifi.repository;

import com.zerobase.wifi.model.entity.Wifi;

import java.util.List;

public interface WIfiRepository {

    void save(Wifi wifi);
    Wifi findByMgrNo(String mgrNo);
    List<Wifi> findAll();
    void deleteByMgrNo(String mgrNo);

    List<Wifi> findNearbyWifi(double lat, double lnt, int limit);
    int saveAll(List<Wifi> wifiList);

    void deleteAll();
    boolean exists(String mgrNo);
}
