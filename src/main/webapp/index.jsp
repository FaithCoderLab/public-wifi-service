<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>와이파이 정보 구하기</title>
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
        <th>WIFI접속환경</th>
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
                function(position) {
                    document.getElementById('lat').value = position.coords.latitude;
                    document.getElementById('lnt').value = position.coords.longitude;
                },
                function(error) {
                    let errorMessage;
                    switch(error.code) {
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

    function getNearbyWifi() {
        const lat = document.getElementById('lat').value;
        const lnt = document.getElementById('lnt').value;

        if (!lat || !lnt) {
            alert("위치 정보를 입력해주세요.");
            return;
        }

        alert(`LAT: ${lat}, LNT: ${lnt} 위치의 와이파이 정보를 조회합니다.`)
    }
</script>
</html>