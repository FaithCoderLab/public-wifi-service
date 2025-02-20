<%--
  Created by IntelliJ IDEA.
  User: kimsumin
  Date: 2025. 2. 20.
  Time: 오전 7:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>와이파이 정보 구하기</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<h1>북마크 목록</h1>
<div class="navigation">
    <a href="/">홈</a>
    <a href="/history.jsp">위치 히스토리 목록</a>
    <a href="/load-wifi.jsp">Open API 와이파이 정보 가져오기</a>
    <a href="/bookmark-list.jsp">북마크 보기</a>
    <a href="/bookmark-group.jsp">북마크 그룹 관리</a>
</div>

<table id="bookmark-table">
    <thead>
    <tr>
        <th>ID</th>
        <th>북마크 이름</th>
        <th>와이파이명</th>
        <th>등록일자</th>
        <th>비고</th>
    </tr>
    </thead>
    <tbody id="bookmark-table-tbody">
    <tr>
        <td colspan="5" class="empty-message">북마크 정보가 존재하지 않습니다.</td>
    </tr>
    </tbody>
</table>

<script>
    window.onload = function () {
        loadBookmarkList();
    };

    async function loadBookmarkList() {
        try {
            const response = await fetch('/api/bookmark', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const result = await response.json();

            if (result.success) {
                updateBookmarkTable(result.data);
            } else {
                alert('북마크 목록 조회에 실패했습니다: ' + result.message);
            }
        } catch (error) {
            alert('북마크 목록 조회 중 오류가 발생했습니다.');
            console.error(error);
        }
    }

    async function deleteBookmark(id) {
        if (!confirm('북마크를 삭제하시겠습니까?')) {
            return;
        }

        try {
            const response = await fetch(`/api/bookmark/${id}`, {
                method: 'DELETE'
            });

            const result = await response.json();

            if (result.success) {
                alert('북마크가 삭제되었습니다.');
                loadBookmarkList();
            } else {
                alert('북마크 삭제에 실패했습니다: ' + result.message);
            }
        } catch (error) {
            alert('북마크 삭제 중 오류가 발생했습니다.');
            console.error(error);
        }
    }

    function updateBookmarkTable(bookmarkList) {
        const tbody = document.querySelector('#bookmark-table-tbody');
        tbody.innerHTML = '';

        if (!bookmarkList || bookmarkList.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-message">북마크 정보가 존재하지 않습니다.</td></tr>';
            return;
        }

        bookmarkList.forEach(bookmark => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
            <td>${bookmark.id}</td>
            <td>${bookmark.groupName}</td>
            <td><a href="/detail.jsp?mgrNo=${bookmark.wifiMgrNo}">${bookmark.wifiName}</a></td>
            <td>${bookmark.regDate}</td>
            <td>
            <button onclick="deleteBookmark(${bookmark.id})">삭제</button>
            </td>
            `;
            tbody.appendChild(tr);
        });
    }
</script>
</body>
</html>
