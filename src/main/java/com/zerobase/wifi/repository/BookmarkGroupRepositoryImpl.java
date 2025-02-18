package com.zerobase.wifi.repository;

import com.zerobase.wifi.model.entity.BookmarkGroup;
import com.zerobase.wifi.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookmarkGroupRepositoryImpl implements BookmarkGroupRepository {
    private final DatabaseConfig dbConfig;
    private static BookmarkGroupRepositoryImpl instance;

    private static final String SQL_INSERT =
            "INSERT INTO BOOKMARK_GROUP (NAME, ORDER_NO, REG_DATE, MOD_DATE) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM BOOKMARK_GROUP WHERE ID = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT * FROM BOOKMARK_GROUP";

    private static final String SQL_UPDATE =
            "UPDATE BOOKMARK_GROUP SET NAME = ?, ORDER_NO = ?, MOD_DATE = ? WHERE ID = ?";

    private static final String SQL_DELETE_BY_ID =
            "DELETE FROM BOOKMARK_GROUP WHERE ID = ?";

    private static final String SQL_EXISTS =
            "SELECT 1 FROM BOOKMARK_GROUP WHERE ID = ?";

    private BookmarkGroupRepositoryImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public static synchronized BookmarkGroupRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new BookmarkGroupRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void save(BookmarkGroup bookmarkGroup) {
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
            pstmt.setString(1, bookmarkGroup.getName());
            pstmt.setInt(2, bookmarkGroup.getOrderNo());
            pstmt.setString(3, bookmarkGroup.getRegDate());
            pstmt.setString(4, bookmarkGroup.getModDate());

            pstmt.executeUpdate();

            if (wasInTransaction) {
                dbConfig.commit();
            }
        } catch (SQLException e) {
            try {
                if (!wasInTransaction) {
                    dbConfig.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException("Error while trying to rollback transaction", ex);
            }
            throw new RuntimeException("Error saving BookmarkGroup", e);
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
    public BookmarkGroup findById(Long id) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBookmarkGroup(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding BookmarkGroup", e);
        }
        return null;
    }

    @Override
    public List<BookmarkGroup> findAll() {
        List<BookmarkGroup> groupList = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                groupList.add(mapResultSetToBookmarkGroup(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding BookmarkGroup", e);
        }

        return groupList;
    }

    @Override
    public void update(BookmarkGroup bookmarkGroup) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean wasInTransaction = false;

        try {
            conn = dbConfig.getConnection();
            wasInTransaction = dbConfig.isInTransaction();

            if (!wasInTransaction) {
                dbConfig.beginTransaction();
            }

            pstmt = conn.prepareStatement(SQL_UPDATE);
            pstmt.setString(1, bookmarkGroup.getName());
            pstmt.setInt(2, bookmarkGroup.getOrderNo());
            pstmt.setString(3, bookmarkGroup.getModDate());
            pstmt.setLong(4, bookmarkGroup.getId());

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
                throw new RuntimeException("Error while trying to rollback transaction", ex);
            }
            throw new RuntimeException("Error updating BookmarkGroup", e);
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

            BookmarkRepository bookmarkRepository = BookmarkRepositoryImpl.getInstance();
            bookmarkRepository.deleteByGroupId(id);

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
                throw new RuntimeException("Error while trying to rollback transaction", ex);
            }
            throw new RuntimeException("Error deleting BookmarkGroup", e);
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
            throw new RuntimeException("Error while checking existence of BookmarkGroup", e);
        }
    }

    private BookmarkGroup mapResultSetToBookmarkGroup(ResultSet rs) throws SQLException {
        return BookmarkGroup.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .orderNo(rs.getInt("ORDER_NO"))
                .regDate(rs.getString("REG_DATE"))
                .modDate(rs.getString("MOD_DATE"))
                .build();
    }
}
