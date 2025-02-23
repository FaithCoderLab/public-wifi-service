package com.zerobase.wifi.repository;

import com.zerobase.wifi.model.entity.Wifi;
import com.zerobase.wifi.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class WifiRepositoryImpl implements WifiRepository {
    private final DatabaseConfig dbConfig;
    private static WifiRepositoryImpl instance;

    private static final String SQL_INSERT =
            "INSERT INTO WIFI_INFO (MGR_NO, DISTANCE, DISTRICT, NAME, ROAD_ADDRESS, " +
                    "DETAIL_ADDRESS, INSTALL_FLOOR, INSTALL_TYPE, INSTALL_AGENCY, SERVICE_TYPE, " +
                    "NET_TYPE, INSTALL_YEAR, IN_OUT_DOOR, LAT, LNT, WORK_DATE) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_BY_MGR_NO =
            "SELECT * FROM WIFI_INFO WHERE MGR_NO = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT * FROM WIFI_INFO";

    private static final String SQL_DELETE_BY_MGR_NO =
            "DELETE FROM WIFI_INFO WHERE MGR_NO = ?";

    private static final String SQL_NEARBY_WIFI =
            "SELECT *, " +
                    "((LAT - ?) * (LAT - ?) + (LNT - ?) * (LNT - ?)) AS DISTANCE " +
                    "FROM WIFI_INFO " +
                    "ORDER BY DISTANCE " +
                    "LIMIT ?";

    private static final String SQL_EXISTS =
            "SELECT 1 FROM WIFI_INFO WHERE MGR_NO = ?";

    private static final String SQL_DELETE_ALL =
            "DELETE FROM WIFI_INFO";

    private WifiRepositoryImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    public static synchronized WifiRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new WifiRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void save(Wifi wifi) {
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
            setWifiParameters(pstmt, wifi);
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
            throw new RuntimeException("Error saving wifi");
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


    private void setWifiParameters(PreparedStatement pstmt, Wifi wifi) throws SQLException {
        pstmt.setString(1, wifi.getMgrNo());
        pstmt.setDouble(2, wifi.getDistance());
        pstmt.setString(3, wifi.getDistrict());
        pstmt.setString(4, wifi.getName());
        pstmt.setString(5, wifi.getRoadAddress());
        pstmt.setString(6, wifi.getDetailAddress());
        pstmt.setString(7, wifi.getInstallFloor());
        pstmt.setString(8, wifi.getInstallType());
        pstmt.setString(9, wifi.getInstallAgency());
        pstmt.setString(10, wifi.getServiceType());
        pstmt.setString(11, wifi.getNetType());
        pstmt.setString(12, wifi.getInstallYear());
        pstmt.setString(13, wifi.getInOutDoor());
        pstmt.setDouble(14, wifi.getLat());
        pstmt.setDouble(15, wifi.getLnt());
        pstmt.setString(16, wifi.getWorkDate());
    }

    @Override
    public Wifi findByMgrNo(String mgrNo) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_MGR_NO)) {

            pstmt.setString(1, mgrNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWifi(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching wifi", e);
        }
        return null;
    }

    @Override
    public List<Wifi> findAll() {
        List<Wifi> wifiList = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                wifiList.add(mapResultSetToWifi(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching wifi", e);
        }

        return wifiList;
    }

    @Override
    public void deleteByMgrNo(String mgrNo) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_BY_MGR_NO)) {

            pstmt.setString(1, mgrNo);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting wifi", e);
        }
    }

    @Override
    public List<Wifi> findNearbyWifi(double lat, double lnt, int limit) {
        List<Wifi> nearbyWifi = new ArrayList<>();

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_NEARBY_WIFI)) {

            pstmt.setDouble(1, lat);
            pstmt.setDouble(2, lat);
            pstmt.setDouble(3, lnt);
            pstmt.setDouble(4, lnt);
            pstmt.setInt(5, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Wifi wifi = mapResultSetToWifi(rs);
                    wifi.setDistance(rs.getDouble("DISTANCE"));
                    nearbyWifi.add(wifi);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching nearby wifi", e);
        }

        return nearbyWifi;
    }

    @Override
    public int saveAll(List<Wifi> wifiList) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int count = 0;
        boolean wasInTransaction = false;

        try {
            conn = dbConfig.getConnection();
            wasInTransaction = dbConfig.isInTransaction();

            if (!wasInTransaction) {
                dbConfig.beginTransaction();
            }

            pstmt = conn.prepareStatement(SQL_INSERT);

            for (Wifi wifi : wifiList) {
                setWifiParameters(pstmt, wifi);
                pstmt.addBatch();
                count++;
            }

            pstmt.executeBatch();

            if (!wasInTransaction) {
                dbConfig.commit();
            }

            return count;
        } catch (SQLException e) {
            try {
                if (!wasInTransaction) {
                    dbConfig.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException("Error rolling back transaction", ex);
            }
            throw new RuntimeException("Error saving wifi list", e);
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
            throw new RuntimeException("Error deleting all wifi list", e);
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

    @Override
    public boolean exists(String mgrNo) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_EXISTS)) {

            pstmt.setString(1, mgrNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching wifi", e);
        }
    }

    private Wifi mapResultSetToWifi(ResultSet rs) throws SQLException {
        return Wifi.builder()
                .mgrNo(rs.getString("MGR_NO"))
                .distance(rs.getDouble("DISTANCE"))
                .district(rs.getString("DISTRICT"))
                .name(rs.getString("NAME"))
                .roadAddress(rs.getString("ROAD_ADDRESS"))
                .detailAddress(rs.getString("DETAIL_ADDRESS"))
                .installFloor(rs.getString("INSTALL_FLOOR"))
                .installType(rs.getString("INSTALL_AGENCY"))
                .serviceType(rs.getString("SERVICE_TYPE"))
                .netType(rs.getString("NET_TYPE"))
                .installYear(rs.getString("INSTALL_YEAR"))
                .inOutDoor(rs.getString("IN_OUT_DOOR"))
                .lat(rs.getDouble("LAT"))
                .lnt(rs.getDouble("LNT"))
                .workDate(rs.getString("WORK_DATE"))
                .build();
    }
}