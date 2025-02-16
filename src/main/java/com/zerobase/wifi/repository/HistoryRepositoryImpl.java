package com.zerobase.wifi.repository;

import com.zerobase.wifi.model.entity.History;
import com.zerobase.wifi.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryRepositoryImpl implements HistoryRepository {
    private final DatabaseConfig dbConfig;
    private static HistoryRepositoryImpl instance;

    private static final String SQL_INSERT =
            "INSERT INTO LOCATION_HISTORY (LAT, LNT, SEARCH_DATE, DELETE_YN) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT_ALL =
            "SELECT * FROM LOCATION_HISTORY ORDER BY ID DESC";

    private static final String SQL_DELETE_BY_ID =
            "DELETE FROM LOCATION_HISTORY WHERE ID = ?";

    private static final String SQL_DELETE_ALL =
            "DELETE FROM LOCATION_HISTORY";

    public HistoryRepositoryImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public static synchronized HistoryRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new HistoryRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void save(History history) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT)) {

            pstmt.setDouble(1, history.getLat());
            pstmt.setDouble(2, history.getLnt());
            pstmt.setString(3, history.getSearchDate());
            pstmt.setBoolean(4, history.isDeleteYn());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving history", e);
        }
    }

    @Override
    public List<History> findAll() {
        List<History> historyList = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                historyList.add(mapResultSetToHistory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding history", e);
        }
        return historyList;
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_BY_ID)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting history", e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(SQL_DELETE_ALL);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting history", e);
        }
    }

    private History mapResultSetToHistory(ResultSet rs) throws SQLException {
        return History.builder()
                .id(rs.getLong("ID"))
                .lat(rs.getDouble("LAT"))
                .lnt(rs.getDouble("LNT"))
                .searchDate(rs.getString("SEARCH_DATE"))
                .deleteYn(rs.getBoolean("DELETE_YN"))
                .build();
    }
}
