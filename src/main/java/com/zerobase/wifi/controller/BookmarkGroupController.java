package com.zerobase.wifi.controller;

import com.google.gson.Gson;
import com.zerobase.wifi.model.dto.ApiResponse;
import com.zerobase.wifi.model.dto.BookmarkGroupDto;
import com.zerobase.wifi.service.BookmarkGroupService;
import com.zerobase.wifi.service.BookmarkGroupServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/bookmark-group/*")
public class BookmarkGroupController extends HttpServlet {
    private final BookmarkGroupService bookmarkGroupService;
    private final Gson gson;

    public BookmarkGroupController() {
        this.bookmarkGroupService = BookmarkGroupServiceImpl.getInstance();
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<BookmarkGroupDto> groupList = bookmarkGroupService.getBookmarkGroupList();
            sendSuccessResponse(response, groupList);
        } catch (Exception e) {
            System.out.println("Error getting bookmark group list: " + e.getMessage());
            sendErrorResponse(response, "Error getting bookmark group list: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            BufferedReader reader = request.getReader();
            BookmarkGroupRequest groupRequest = gson.fromJson(reader, BookmarkGroupRequest.class);

            if (groupRequest.getName() == null || groupRequest.getName().trim().isEmpty()) {
                sendErrorResponse(response, "Name is required");
                return;
            }

            bookmarkGroupService.addBookmarkGroup(
                    groupRequest.getName(),
                    groupRequest.getOrder()
            );

            sendSuccessResponse(response, "Bookmark group added successfully");
        } catch (Exception e) {
            System.out.println("Error adding bookmark group: " + e.getMessage());
            sendErrorResponse(response, "Error adding bookmark group: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                sendErrorResponse(response, "Invalid path");
                return;
            }

            String idStr = pathInfo.substring(1);
            Long id = Long.parseLong(idStr);

            BufferedReader reader = request.getReader();
            BookmarkGroupRequest groupRequest = gson.fromJson(reader, BookmarkGroupRequest.class);

            if (groupRequest.getName() == null || groupRequest.getName().trim().isEmpty()) {
                sendErrorResponse(response, "Name is required");
                return;
            }

            bookmarkGroupService.editBookmarkGroup(
                    id,
                    groupRequest.getName(),
                    groupRequest.getOrder()
            );

            sendSuccessResponse(response, "Bookmark group updated successfully");
        } catch (NumberFormatException e) {
            System.out.println("Invalid id format: " + e.getMessage());
            sendErrorResponse(response, "Invalid id format");
        } catch (Exception e) {
            System.out.println("Error updating bookmark group: " + e.getMessage());
            sendErrorResponse(response, "Error updating bookmark group: " + e.getMessage());
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

            bookmarkGroupService.deleteBookmarkGroup(id);
            sendSuccessResponse(response, "Bookmark group deleted successfully");
        } catch (NumberFormatException e) {
            System.out.println("Invalid id format: " + e.getMessage());
            sendErrorResponse(response, "Invalid id format");
        } catch (Exception e) {
            System.out.println("Error deleting bookmark group: " + e.getMessage());
            sendErrorResponse(response, "Error deleting bookmark group: " + e.getMessage());
        }
    }

    private void sendSuccessResponse(HttpServletResponse response, Object data)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(ApiResponse.success(data)));
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

    private static class BookmarkGroupRequest {
        private String name;
        private int order;

        public String getName() {
            return name;
        }

        public int getOrder() {
            return order;
        }
    }
}