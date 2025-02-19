package com.zerobase.wifi.controller;

import com.google.gson.Gson;
import com.zerobase.wifi.model.dto.ApiResponse;
import com.zerobase.wifi.model.dto.BookmarkDto;
import com.zerobase.wifi.service.BookmarkService;
import com.zerobase.wifi.service.BookmarkServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet
public class BookmarkController extends HttpServlet {
    private final BookmarkService bookmarkService;
    private final Gson gson;

    public BookmarkController() {
        this.bookmarkService = BookmarkServiceImpl.getInstance();
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetBookmarkList(request, response);
            } else {
                String idStr = pathInfo.substring(1);
                Long id = Long.parseLong(idStr);
                handleGetBookmarkDetail(id, response);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid id format: " + e.getMessage());
            sendErrorResponse(response, "Invalid id format");
        } catch (Exception e) {
            System.out.println("Error processing request: " + e.getMessage());
            sendErrorResponse(response, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String groupIdStr = request.getParameter("groupId");
            String wifiMgrNo = request.getParameter("wifiMgrNo");

            if (groupIdStr == null || wifiMgrNo == null) {
                sendErrorResponse(response,
                        "Missing required parameters");
                return;
            }

            Long groupId = Long.parseLong(groupIdStr);
            bookmarkService.addBookmark(groupId, wifiMgrNo);
            sendSuccessResponse(response,
                    "Bookmark added successfully");
        } catch (NumberFormatException e) {
            System.out.println("Invalid groupId: " + e.getMessage());
            sendErrorResponse(response, "Invalid groupId");
        } catch (Exception e) {
            System.out.println("Error adding bookmark: " + e.getMessage());
            sendErrorResponse(response,
                    "Error adding bookmark: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                sendErrorResponse(response, "Invalid pathInfo");
                return;
            }

            String idStr = pathInfo.substring(1);
            Long id = Long.parseLong(idStr);

            bookmarkService.deleteBookmark(id);
            sendSuccessResponse(response,
                    "Bookmark deleted successfully");
        } catch (NumberFormatException e) {
            System.out.println("Invalid id format: " + e.getMessage());
            sendErrorResponse(response, "Invalid id format");
        } catch (Exception e) {
            System.out.println("Error deleting bookmark: " + e.getMessage());
            sendErrorResponse(response, "Error deleting bookmark: "
                    + e.getMessage());
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

    private void sendErrorResponse(HttpServletResponse response,
                                   String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(ApiResponse.error(message)));
        out.flush();
    }

    private void handleGetBookmarkDetail(Long id,
                                         HttpServletResponse response)
            throws IOException {
        try {
            BookmarkDto bookmark = bookmarkService.getBookmarkDetail(id);
            if (bookmark != null) {
                sendSuccessResponse(response, bookmark);
            } else {
                sendErrorResponse(response, "Bookmark not found");
            }
        } catch (Exception e) {
            System.out.println("Error getting bookmark detail: " + e.getMessage());
            sendErrorResponse(response,
                    "Error getting bookmark detail: " + e.getMessage());
        }
    }

    private void handleGetBookmarkList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String groupIdStr = request.getParameter("groupId");
            List<BookmarkDto> bookmarkList;

            if (groupIdStr != null) {
                Long groupId = Long.parseLong(groupIdStr);
                bookmarkList = bookmarkService.getBookmarkList(groupId);
            } else {
                bookmarkList = bookmarkService.getBookmarkList(null);
            }

            sendSuccessResponse(response, bookmarkList);
        } catch (Exception e) {
            System.out.println("Error getting bookmark list: " + e.getMessage());
            sendErrorResponse(response,
                    "Error getting bookmark list: " + e.getMessage());
        }
    }
}
