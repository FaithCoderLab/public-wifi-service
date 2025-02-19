package com.zerobase.wifi.controller;

import com.google.gson.Gson;
import com.zerobase.wifi.model.dto.ApiResponse;
import com.zerobase.wifi.model.dto.HistoryDto;
import com.zerobase.wifi.service.HistoryService;
import com.zerobase.wifi.service.HistoryServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/history/*")
public class HistoryController extends HttpServlet {
    private final HistoryService historyService;
    private final Gson gson;

    public HistoryController() {
        this.historyService = HistoryServiceImpl.getInstance();
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        try {
            List<HistoryDto> historyList = historyService.getHistoryList();
            sendSuccessResponse(response, historyList);
        } catch (Exception e) {
            System.out.println("Error getting history list:" + e.getMessage());
            sendErrorResponse(response, "Error getting history list: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                sendErrorResponse(response, "Invalid path");
                return;
            }

            String idStr = pathInfo.substring(1);
            Long id = Long.parseLong(idStr);

            historyService.deleteHistory(id);
            sendSuccessResponse(response, "History deleted successfully");
        } catch (NumberFormatException e) {
            System.out.println("Invalid id format: " + e.getMessage());
            sendErrorResponse(response, "Invalid id format");
        } catch (Exception e) {
            System.out.println("Error deleting history: " + e.getMessage());
            sendErrorResponse(response, "Error deleting history: " + e.getMessage());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message)
    throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(ApiResponse.error(message)));
        out.flush();
    }

    private void sendSuccessResponse(HttpServletResponse response, Object data)
    throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(ApiResponse.success(data)));
        out.flush();
    }
}
