<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>와이파이 정보 구하기</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<h1>와이파이 정보 구하기</h1>
<div class="navigation">
    <a href="/">홈</a>
    <a href="/history.jsp">위치 히스토리 목록</a>
    <a href="/load-wifi.jsp">Open API 와이파이 정보 가져오기</a>
    <a href="/bookmark-list.jsp">북마크 보기</a>
    <a href="/bookmark-group.jsp">북마크 그룹 관리</a>
</div>
<div class="location-form">
    <form method="post" id="locationForm">
        <label for="lat">LAT:</label>
        <input type="text" id="lat" name="lat" placeholder="0.0">

        <label for="lnt">LNT:</label>
        <input type="text" id="lnt" name="lnt" placeholder="0.0"/>

        <button type="button" onclick="getLocation()">내 위치 가져오기</button>
        <button type="button" onclick="getNearbyWifi()">근처 WIFI 정보 보기</button>
    </form>
</div>
<div id="message">
    <p>위치 정보를 입력한 후에 조회해 주세요.</p>
</div>
<table id="wifi-info">
    <thead>
    <tr>
        <th>거리(Km)</th>
        <th>관리번호</th>
        <th>자치구</th>
        <th>와이파이명</th>
        <th>도로명주소</th>
        <th>상세주소</th>
        <th>설치위치(층)</th>
        <th>설치유형</th>
        <th>설치기관</th>
        <th>서비스구분</th>
        <th>망종류</th>
        <th>설치년도</th>
        <th>실내외구분</th>
        <th>X좌표</th>
        <th>Y좌표</th>
        <th>작업일자</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td colspan="17" class="empty-message">위치 정보를 입력한 후에 조회해 주세요.</td>
    </tr>
    </tbody>
</table>
</body>
<script>
    function getLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function (position) {
                    const lat = position.coords.latitude;
                    const lnt = position.coords.longitude;

                    console.log('Got coordinates:', {lat, lnt});

                    document.getElementById('lat').value = lat;
                    document.getElementById('lnt').value = lnt;
                },
                function (error) {
                    let errorMessage;
                    switch (error.code) {
                        case error.PERMISSION_DENIED:
                            errorMessage = "위치 정보 접근 권한이 없습니다.";
                            break;
                        case error.POSITION_UNAVAILABLE:
                            errorMessage = "위치 정보를 가져올 수 없습니다.";
                            break;
                        case error.TIMEOUT:
                            errorMessage = "위치 정보 요청 시간이 초과되었습니다.";
                            break;
                        default:
                            errorMessage = "위치 정보를 가져오는 중 오류가 발생했습니다.";
                    }
                    alert(errorMessage);
                }
            );
        } else {
            alert("이 브라우저에서는 위치 정보를 지원하지 않습니다.");
        }
    }

    async function getNearbyWifi() {
        const lat = document.getElementById('lat').value;
        const lnt = document.getElementById('lnt').value;

        console.log('Input values:', {lat, lnt});

        if (!lat || !lnt) {
            alert("위치 정보를 입력해주세요.");
            return;
        }

        if (isNaN(lat) || isNaN(lnt)) {
            alert("위치 정보는 숫자만 입력 가능합니다.");
            return;
        }

        try {
            const url = '/api/wifi/nearby?lat=' + lat + '&lnt=' + lnt;
            console.log('Requesting URL:', url)

            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            console.log('Raw response:', response);

            const result = await response.json();
            console.log('Parsed response:', result);

            if (result.success) {
                updateWifiTable(result.data);
            } else {
                alert('WIFI 정보 조회에 실패하였습니다: ' + result.message);
            }
        } catch (error) {
            alert('WIFI 정보 조회 중 오류가 발생했습니다.');
            console.error(error);
        }
    }

    async function loadWifiData() {
        try {
            const response = await fetch('/api/wifi/load', {method: 'POST'});
            const result = await response.json();

            if (result.success) {
                alert(`${result.data}개의 WIFI 정보를 정상적으로 로드하였습니다.`);
                location.reload();
            } else {
                alert('WIFI 정보 로드에 실패하였습니다: ' + result.message);
            }
        } catch (error) {
            alert('WIFI 정보 로드 중 오류가 발생했습니다.');
            console.error(error);
        }
    }

    function updateWifiTable(wifiList) {
        console.log('Updating table with:', wifiList);
        const tbody = document.querySelector('#wifi-info tbody');
        tbody.innerHTML = '';

        if (!wifiList || wifiList.length === 0) {
            tbody.innerHTML = '<tr><td colspan="17" class="empty-message">조회된 WIFI 정보가 없습니다.</td></tr>';
            return;
        }

        const lat = parseFloat(document.getElementById('lat').value);
        const lnt = parseFloat(document.getElementById('lnt').value);

        const dataArray = Array.isArray(wifiList) ? wifiList : [wifiList];

        dataArray.forEach(wifi => {
            const wifiLat = parseFloat(wifi.LAT);
            const wifiLnt = parseFloat(wifi.LNT);
            const distance = calculateDistance(lat, lnt, wifiLat, wifiLnt).toFixed(4); // 거리 계산

            const tr = document.createElement('tr');
            tr.innerHTML =
                '<td>' + distance + '</td>' + // 계산된 거리 값 추가
                '<td>' + wifi.X_SWIFI_MGR_NO + '</td>' +
                '<td>' + wifi.X_SWIFI_WRDOFC + '</td>' +
                '<td><a href="detail.jsp?mgrNo=' + wifi.X_SWIFI_MGR_NO + '">' + wifi.X_SWIFI_MAIN_NM + '</a></td>' +
                '<td>' + wifi.X_SWIFI_ADRES1 + '</td>' +
                '<td>' + wifi.X_SWIFI_ADRES2 + '</td>' +
                '<td>' + wifi.X_SWIFI_INSTL_FLOOR + '</td>' +
                '<td>' + wifi.X_SWIFI_INSTL_TY + '</td>' +
                '<td></td>' +
                '<td></td>' +
                '<td></td>' +
                '<td>' + wifi.X_SWIFI_CNSTC_YEAR + '</td>' +
                '<td>' + wifi.X_SWIFI_INOUT_DOOR + '</td>' +
                '<td>' + wifi.LAT + '</td>' +
                '<td>' + wifi.LNT + '</td>' +
                '<td>' + wifi.WORK_DTTM + '</td>';
            tbody.appendChild(tr);
        });

        document.getElementById('message').style.display = 'none';
    }


    function calculateDistance(lat1, lon1, lat2, lon2) {
        const R = 6371; // 지구 반지름 (단위: km)
        const dLat = (lat2 - lat1) * Math.PI / 180;
        const dLon = (lon2 - lon1) * Math.PI / 180;

        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);

        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 거리 반환 (단위: km)
    }

</script>
</html>