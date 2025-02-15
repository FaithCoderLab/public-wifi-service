package com.zerobase.wifi.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class WifiApiUtil {
    private static final String BASE_URL = "http://openapi.seoul.go.kr:8088";
    private static final String DATA_TYPE = "json";
    private static final String SERVICE_NAME = "TbPublicWifiInfo";

    private static WifiApiUtil instance;
    private final String apiKey;
    private final OkHttpClient client;

    public WifiApiUtil() {
        this.apiKey = PropertiesUtil.getProperties().getProperty("api.wifi.key");
        this.client = new OkHttpClient();
    }
    public static WifiApiUtil getInstance() {
        if (instance == null) {
            instance = new WifiApiUtil();
        }
        return instance;
    }

    private String buildUrl(int start, int end) {
        StringBuilder url = new StringBuilder(BASE_URL);
        url.append("/")
                .append(apiKey)
                .append("/")
                .append(DATA_TYPE)
                .append("/")
                .append(SERVICE_NAME)
                .append("/")
                .append(start)
                .append("/")
                .append(end);

        return url.toString();
    }

    public String getWifiInfo(int start, int end) throws IOException {
        String url = buildUrl(start, end);
        System.out.println("Request: " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code());
            }

            return response.body() != null ? response.body().string() : "";
        }
    }

    public String testBuildUrl(int start, int end) {
        return buildUrl(start, end);
    }
}
