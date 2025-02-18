package com.zerobase.wifi.repository;

import com.zerobase.wifi.model.entity.Wifi;
import com.zerobase.wifi.util.DatabaseConfig;
import com.zerobase.wifi.util.DatabaseInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class WifiRepositoryTest {
    private WifiRepository wifiRepository;
    private DatabaseConfig dbConfig;
    private DatabaseInitializer initializer;

    @Before
    public void setUp() {
        wifiRepository = WifiRepositoryImpl.getInstance();
        dbConfig = DatabaseConfig.getInstance();
        initializer = DatabaseInitializer.getInstance();
        initializer.initializeDatabase();
    }

    @Test
    public void testTransactionCommit() throws SQLException {
        dbConfig.beginTransaction();

        Wifi wifi = createTestWifi("COMMIT001", "트랜잭션 커밋 테스트");
        wifiRepository.save(wifi);

        dbConfig.commit();

        Wifi found = wifiRepository.findByMgrNo("COMMIT001");
        assertNotNull(found);
        assertEquals("트랜잭션 커밋 테스트", found.getName());
    }

    @Test
    public void testTransactionRollback() throws SQLException {
        dbConfig.beginTransaction();

        Wifi wifi = createTestWifi("ROLLBACK001", "트랜잭션 롤백 테스트");
        wifiRepository.save(wifi);

        dbConfig.rollback();

        Wifi found = wifiRepository.findByMgrNo("ROLLBACK001");
        assertNull(found);
    }

    @Test
    public void testBatchSaveWithTransaction() throws SQLException {
        Wifi wifi1 = createTestWifi("BATCH001", "배치 저장 1");
        Wifi wifi2 = createTestWifi("BATCH002", "배치 저장 2");
        List<Wifi> wifiList = Arrays.asList(wifi1, wifi2);

        dbConfig.beginTransaction();
        int saveCount = wifiRepository.saveAll(wifiList);
        dbConfig.commit();

        assertEquals(2, saveCount);
        assertNotNull(wifiRepository.findByMgrNo("BATCH001"));
        assertNotNull(wifiRepository.findByMgrNo("BATCH002"));
    }

    @Test
    public void testBatchSaveRollback() throws SQLException {
        Wifi wifi1 = createTestWifi("BATCH003", "배치 롤백 1");
        Wifi wifi2 = createTestWifi("BATCH004", "배치 롤백 2");
        List<Wifi> wifiList = Arrays.asList(wifi1, wifi2);

        dbConfig.beginTransaction();
        wifiRepository.saveAll(wifiList);
        dbConfig.rollback();

        assertNull(wifiRepository.findByMgrNo("BATCH003"));
        assertNull(wifiRepository.findByMgrNo("BATCH004"));
    }

    @Test
    public void testNestedTransactionSupport() throws SQLException {
        dbConfig.beginTransaction();

        Wifi wifi1 = createTestWifi("NESTED001", "중첩 트랜잭션 1");
        wifiRepository.save(wifi1);

        Wifi wifi2 = createTestWifi("NESTED002", "중첩 트랜잭션 2");
        wifiRepository.save(wifi2);

        dbConfig.commit();

        assertNotNull(wifiRepository.findByMgrNo("NESTED001"));
        assertNotNull(wifiRepository.findByMgrNo("NESTED002"));
    }

    @Test
    public void testDeletedAllWithTransaction() throws SQLException {
        Wifi wifi1 = createTestWifi("DELETE001", "삭제 테스트 1");
        Wifi wifi2 = createTestWifi("DELETE002", "삭제 테스트 2");
        wifiRepository.save(wifi1);
        wifiRepository.save(wifi2);

        dbConfig.beginTransaction();
        wifiRepository.deleteAll();
        dbConfig.commit();

        List<Wifi> allWifi = wifiRepository.findAll();
        assertTrue(allWifi.isEmpty());
    }

    private Wifi createTestWifi(String mgrNo, String name) {
        return Wifi.builder()
                .mgrNo(mgrNo)
                .distance(0.0)
                .district("테스트구")
                .name(name)
                .roadAddress("테스트로 123")
                .detailAddress("2층")
                .installFloor("2층")
                .installType("벽면")
                .installAgency("테스트기관")
                .serviceType("공공Wifi")
                .netType("임대망")
                .installYear("2023")
                .inOutDoor("실내")
                .wifiEnvironment("일반")
                .lat(37.5665)
                .lnt(126.9780)
                .workDate("2023-12-31")
                .build();
    }

    @After
    public void tearDown() {
        try {
            if (dbConfig.isInTransaction()) {
                dbConfig.rollback();
            }
            initializer.dropTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
