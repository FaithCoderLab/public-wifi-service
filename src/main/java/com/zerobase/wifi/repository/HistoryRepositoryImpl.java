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
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean wasInTransaction = false;

        try {
            conn = dbConfig.getConnection();
            wasInTransaction = dbConfig.isInTransaction();

            if (!wasInTransaction) {
                dbConfig.beginTransaction();
            }

            pstmt = conn.prepareStatement(SQL_INSERT);
            pstmt.setDouble(1, history.getLat());
            pstmt.setDouble(2, history.getLnt());
            pstmt.setString(3, history.getSearchDate());
            pstmt.setBoolean(4, history.isDeleteYn());

            pstmt.executeUpdate();

            if (!wasInTransaction) {
                dbConfig.commit();
            }
        } catch (SQLException e) {
            try {
                if (!wasInTransaction) {
                    dbConfig.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException("Error rolling back transaction", ex);
            }
            throw new RuntimeException("Error saving history", e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null && !wasInTransaction) {
                    dbConfig.closeConnection();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean wasInTransaction = false;

        try {
            conn = dbConfig.getConnection();
            wasInTransaction = dbConfig.isInTransaction();

            if (!wasInTransaction) {
                dbConfig.beginTransaction();
            }

            pstmt = conn.prepareStatement(SQL_DELETE_BY_ID);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();

            if (!wasInTransaction) {
                dbConfig.commit();
            }
        } catch (SQLException e) {
            try {
                if (!wasInTransaction) {
                    dbConfig.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException("Error rolling back transaction", ex);
            }
            throw new RuntimeException("Error deleting history", e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null && !wasInTransaction) {
                    dbConfig.closeConnection();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void deleteAll() {
        Connection conn = null;
        Statement stmt = null;
        boolean wasInTransaction = false;

        try {
            conn = dbConfig.getConnection();
            wasInTransaction = dbConfig.isInTransaction();

            if (!wasInTransaction) {
                dbConfig.beginTransaction();
            }

            stmt = conn.createStatement();
            stmt.executeUpdate(SQL_DELETE_ALL);

            if (!wasInTransaction) {
                dbConfig.commit();
            }
        } catch (SQLException e) {
            try {
                if (!wasInTransaction) {
                    dbConfig.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException("Error rolling back transaction", ex);
            }
            throw new RuntimeException("Error deleting history", e);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null && !wasInTransaction) {
                    dbConfig.closeConnection();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
