package com.zerobase.wifi.controller;

import com.google.gson.Gson;
import com.zerobase.wifi.model.dto.ApiResponse;
import com.zerobase.wifi.model.dto.WifiDto;
import com.zerobase.wifi.service.WifiService;
import com.zerobase.wifi.service.WifiServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/wifi/*")
public class WifiController extends HttpServlet {
    private final WifiService wifiService;
    private final Gson gson;

    public WifiController() {
        this.wifiService = WifiServiceImpl.getInstance();
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                sendErrorResponse(response, "Invalid request path");
                return;
            }

            if (pathInfo.equals("/nearby")) {
                handleNearbyWifi(request, response);
                return;
            } else {
                String mgrNo = pathInfo.substring(1);
                handleWifiDetail(mgrNo, response);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            sendErrorResponse(response, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo.equals("/load")) {
                handleLoadWifi(response);
            } else {
                sendErrorResponse(response, "Invalid request path");
            }
        } catch (Exception e) {
            sendErrorResponse(response, e.getMessage());
        }
    }

    private void handleLoadWifi(HttpServletResponse response)
            throws IOException {
        try {
            System.out.println("Loading wifi");
            int count = wifiService.loadWifiData();
            System.out.println("Loaded " + count + " wifi data");
            sendSuccessResponse(response, count);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, "Error loading wifi data: " + e.getMessage());
        }
    }

    private void handleWifiDetail(String mgrNo, HttpServletResponse response)
            throws IOException {
        try {
            WifiDto wifi = wifiService.getWifiDetail(mgrNo);
            if (wifi != null) {
                sendSuccessResponse(response, wifi);
            } else {
                sendErrorResponse(response, "Wifi not found");
            }
        } catch (Exception e) {
            sendErrorResponse(response, "Error getting wifi detail: " + e.getMessage());
        }
    }

    private void handleNearbyWifi(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String latStr = request.getParameter("lat");
        String lntStr = request.getParameter("lnt");

        System.out.println("latStr = " + latStr);
        System.out.println("lntStr = " + lntStr);

        if (latStr == null || lntStr == null || latStr.trim().isEmpty() || lntStr.trim().isEmpty()) {
            sendErrorResponse(response, "Invalid request parameters");
            return;
        }

        try {
            double lat = Double.parseDouble(request.getParameter("lat"));
            double lnt = Double.parseDouble(request.getParameter("lnt"));

            System.out.println("lat = " + lat + ", lnt = " + lnt);

            if (lat < -90 || lat > 90 || lnt < -180 || lnt > 180) {
                sendErrorResponse(response, "Invalid request parameters");
                return;
            }

            List<WifiDto> nearbyWifi = wifiService.findNearbyWifi(lat, lnt);
            sendSuccessResponse(response, nearbyWifi);
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: " + e.getMessage());
            sendErrorResponse(response, "Invalid coordinates format");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            sendErrorResponse(response, "Error finding nearby wifi: " + e.getMessage());
        }
    }

    private void sendSuccessResponse(HttpServletResponse response, Object data)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = gson.toJson(ApiResponse.success(data));
        System.out.println("Response JSON: " + jsonResponse);

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }


    private void sendErrorResponse(HttpServletResponse response, String message)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(ApiResponse.error(message)));
        out.flush();
    }

}
