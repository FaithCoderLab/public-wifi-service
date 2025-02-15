package com.zerobase.wifi.service;

import com.zerobase.wifi.util.PropertiesUtil;
import org.junit.Test;
import com.zerobase.wifi.util.WifiApiUtil;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
}
