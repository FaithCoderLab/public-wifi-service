package com.zerobase.wifi.repository;

import com.google.gson.internal.bind.util.ISO8601Utils;
import com.zerobase.wifi.model.entity.Bookmark;
import com.zerobase.wifi.util.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookmarkRepositoryImpl implements BookmarkRepository {
    private final DatabaseConfig dbConfig;
    private static BookmarkRepositoryImpl instance;

    private static final String SQL_INSERT =
            "INSERT INTO BOOKMARK (GROUP_ID, WIFI_MGR_NO, REG_DATE) " +
                    "VALUES (?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM BOOKMARK WHERE ID = ?";

    private static final String SQL_SELECT_BY_GROUP_ID =
            "SELECT * FROM BOOKMARK WHERE GROUP_ID = ?";

    private static final String SQL_DELETE_BY_ID =
            "DELETE FROM BOOKMARK WHERE ID = ?";

    private static final String SQL_DELETE_BY_GROUP_ID =
            "DELETE FROM BOOKMARK WHERE GROUP_ID = ?";

    private static final String SQL_EXISTS =
            "SELECT 1 FROM BOOKMARK WHERE ID = ?";

    private BookmarkRepositoryImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public static synchronized BookmarkRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new BookmarkRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void save(Bookmark bookmark) {
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
            pstmt.setLong(1, bookmark.getGroupId());
            pstmt.setString(2, bookmark.getWifiMgrNo());
            pstmt.setString(3, bookmark.getRegDate());

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
            throw new RuntimeException("Error saving bookmark", e);
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
    public Bookmark findById(Long id) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBookmark(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting bookmark", e);
        }
        return null;
    }

    @Override
    public List<Bookmark> findByGroupId(Long groupId) {
        List<Bookmark> bookmarks = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_GROUP_ID)) {

            pstmt.setLong(1, groupId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookmarks.add(mapResultSetToBookmark(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting bookmark", e);
        }

        return bookmarks;
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
            throw new RuntimeException("Error deleting bookmark", e);
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
    public void deleteByGroupId(Long groupId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean wasInTransaction = false;

        try {
            conn = dbConfig.getConnection();
            wasInTransaction = dbConfig.isInTransaction();

            if (!wasInTransaction) {
                dbConfig.beginTransaction();
            }

            pstmt = conn.prepareStatement(SQL_DELETE_BY_GROUP_ID);
            pstmt.setLong(1, groupId);
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
            throw new RuntimeException("Error deleting bookmark", e);
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
    public boolean exists(Long id) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_EXISTS)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting bookmark", e);
        }
    }

    private Bookmark mapResultSetToBookmark(ResultSet rs) throws SQLException {
        return Bookmark.builder()
                .id(rs.getLong("ID"))
                .groupId(rs.getLong("GROUP_ID"))
                .wifiMgrNo(rs.getString("WIFI_MGR_NO"))
                .regDate(rs.getString("REG_DATE"))
                .build();
    }
}
