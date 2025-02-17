package com.zerobase.wifi.repository;

import com.zerobase.wifi.model.entity.Wifi;
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
    private DatabaseInitializer initializer;

    @Before
    public void setUp() {
        wifiRepository = WifiRepositoryImpl.getInstance();
        initializer = DatabaseInitializer.getInstance();
        initializer.initializeDatabase();
    }

    @Test
    public void testSave() {
        Wifi wifi = Wifi.builder()
                .mgrNo("TEST001")
                .distance(0.0)
                .district("강남구")
                .name("테스트 와이파이")
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

        wifiRepository.save(wifi);

        Wifi found = wifiRepository.findByMgrNo("TEST001");
        assertNotNull(found);
        assertEquals("TEST001", found.getMgrNo());
        assertEquals("테스트 와이파이", found.getName());
        assertEquals(37.5665, found.getLat(), 0.0001);
        assertEquals(126.9780, found.getLnt(), 0.0001);
    }

    @Test
    public void testFindByMgrNo() {
        String mgrNo = "TEST002";
        Wifi wifi = Wifi.builder()
                .mgrNo(mgrNo)
                .distance(0.0)
                .district("서초구")
                .name("서초 와이파이")
                .roadAddress("서초로 123")
                .detailAddress("1층")
                .installFloor("1층")
                .installType("기둥")
                .installAgency("서초구청")
                .serviceType("공공Wifi")
                .netType("임대망")
                .installYear("2024")
                .inOutDoor("실외")
                .wifiEnvironment("일반")
                .lat(37.4869)
                .lnt(127.0506)
                .workDate("2024-01-01")
                .build();

        wifiRepository.save(wifi);

        Wifi found = wifiRepository.findByMgrNo(mgrNo);

        assertNotNull("WiFi not found", found);
        assertEquals(mgrNo, found.getMgrNo());
        assertEquals("서초 와이파이", found.getName());
        assertEquals("서초구", found.getDistrict());
        assertEquals(37.4869, found.getLat(), 0.0001);
        assertEquals(127.0506, found.getLnt(), 0.0001);

        Wifi notFound = wifiRepository.findByMgrNo("NOT_EXISTS");

        assertNull("Non-existent MGR_NO should not be found", notFound);
    }

    @Test
    public void testFindNearbyWifi() {
        Wifi wifi1 = Wifi.builder()
                .mgrNo("TEST003")
                .name("강남역 1번출구 와이파이")
                .lat(37.4987)
                .lnt(127.0273)
                .build();

        Wifi wifi2 = Wifi.builder()
                .mgrNo("TEST004")
                .name("강남역 2번출구 와이파이")
                .lat(37.4982)
                .lnt(127.0278)
                .build();

        Wifi wifi3 = Wifi.builder()
                .mgrNo("TEST005")
                .name("강남역 먼곳 와이파이")
                .lat(37.4962)
                .lnt(127.0288)
                .build();

        wifiRepository.save(wifi1);
        wifiRepository.save(wifi2);
        wifiRepository.save(wifi3);

        double myLat = 37.4987;
        double myLnt = 127.0273;
        List<Wifi> nearbyWifi = wifiRepository.findNearbyWifi(myLat, myLnt, 3);

        assertNotNull(nearbyWifi);
        assertEquals(3, nearbyWifi.size());

        assertEquals("강남역 1번출구 와이파이", nearbyWifi.get(0).getName());
        assertEquals("강남역 2번출구 와이파이", nearbyWifi.get(1).getName());
        assertEquals("강남역 먼곳 와이파이", nearbyWifi.get(2).getName());

        List<Wifi> limitedWifi = wifiRepository.findNearbyWifi(myLat, myLnt, 2);
        assertEquals(2, limitedWifi.size());
    }

    @Test
    public void testSaveAll() {
        Wifi wifi1 = Wifi.builder()
                .mgrNo("BATCH001")
                .name("일괄저장 와이파이1")
                .lat(37.5665)
                .lnt(126.9780)
                .build();

        Wifi wifi2 = Wifi.builder()
                .mgrNo("BATCH002")
                .name("일괄저장 와이파이2")
                .lat(37.5668)
                .lnt(126.9789)
                .build();

        List<Wifi> wifiList = Arrays.asList(wifi1, wifi2);

        int savedCount = wifiRepository.saveAll(wifiList);

        assertEquals(2, savedCount);
        assertTrue(wifiRepository.exists("BATCH001"));
        assertTrue(wifiRepository.exists("BATCH002"));
    }

    @Test
    public void testExists() {
        Wifi wifi = Wifi.builder()
                .mgrNo("EXISTS001")
                .name("존재확인 와이파이")
                .lat(37.5665)
                .lnt(126.9780)
                .build();

        wifiRepository.save(wifi);

        assertTrue(wifiRepository.exists("EXISTS001"));
        assertFalse(wifiRepository.exists("EXISTS002"));
    }

    @Test
    public void testDeleteByMgrNo() {
        Wifi wifi = Wifi.builder()
                .mgrNo("DELETE001")
                .name("삭제테스트 와이파이")
                .lat(37.5665)
                .lnt(126.9780)
                .build();

        wifiRepository.save(wifi);
        assertTrue(wifiRepository.exists("DELETE001"));

        wifiRepository.deleteByMgrNo("DELETE001");
        assertFalse(wifiRepository.exists("DELETE001"));
    }

    @Test
    public void testDeleteAll() {
        Wifi wifi1 = Wifi.builder()
                .mgrNo("ALL001")
                .name("전체삭제 와이파이1")
                .lat(37.5665)
                .lnt(126.9780)
                .build();

        Wifi wifi2 = Wifi.builder()
                .mgrNo("ALL002")
                .name("전체삭제 와이파이2")
                .lat(37.5887)
                .lnt(126.9783)
                .build();

        wifiRepository.save(wifi1);
        wifiRepository.save(wifi2);

        List<Wifi> beforeDelete = wifiRepository.findAll();
        assertFalse(beforeDelete.isEmpty());

        wifiRepository.deleteAll();

        List<Wifi> afterDelete = wifiRepository.findAll();
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    public void testFindAll() {
        Wifi wifi1 = Wifi.builder()
                .mgrNo("LIST001")
                .name("전체조회 와이파이1")
                .lat(37.5665)
                .lnt(126.9780)
                .build();

        Wifi wifi2 = Wifi.builder()
                .mgrNo("LIST002")
                .name("전체조회 와이파이2")
                .lat(37.5668)
                .lnt(126.9780)
                .build();

        wifiRepository.save(wifi1);
        wifiRepository.save(wifi2);

        List<Wifi> wifiList = wifiRepository.findAll();

        assertNotNull(wifiList);
        assertEquals(2, wifiList.size());
        assertTrue(wifiList.stream()
                .anyMatch(wifi -> wifi.getMgrNo().equals("LIST001")));
        assertTrue(wifiList.stream()
                .anyMatch(wifi -> wifi.getMgrNo().equals("LIST002")));
    }

    @After
    public void tearDown() {
        try {
            initializer.dropTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
