package com.zerobase.wifi.service;

import com.zerobase.wifi.model.dto.ToPublicWifiInfoDto;
import com.zerobase.wifi.model.dto.WifiDto;
import com.zerobase.wifi.util.PropertiesUtil;
import org.junit.Test;
import com.zerobase.wifi.util.WifiApiUtil;

import java.io.IOException;

import static org.junit.Assert.*;

public class WifiServiceTest {
    @Test
    public void testGetWifiInfo() throws IOException {
        WifiApiUtil wifiApiUtil = WifiApiUtil.getInstance();

        String apiKey = PropertiesUtil.getProperties().getProperty("api.wifi.key");
        System.out.println("API Key: " + apiKey);

        String result = wifiApiUtil.getWifiInfo(1, 5);
        System.out.println(result);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testGetPublicWifiInfo() throws IOException {
        WifiApiUtil wifiApiUtil = WifiApiUtil.getInstance();
        ToPublicWifiInfoDto result = wifiApiUtil.getPublicWifiInfo(1, 5);

        assertNotNull(result);
        assertNotNull(result.getResult());
        assertEquals("INFO-000", result.getResult().getCode());
        assertNotNull(result.getRow());
        assertFalse(result.getRow().isEmpty());

        WifiDto firstWifi = result.getRow().get(0);
        assertNotNull(firstWifi.getMgrNo());
        assertNotNull(firstWifi.getName());
    }
}
