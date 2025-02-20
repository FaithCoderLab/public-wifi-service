<%--
  Created by IntelliJ IDEA.
  User: kimsumin
  Date: 2025. 2. 14.
  Time: 오전 1:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>와이파이 정보 구하기</title>
  <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<h1>위치 히스토리 목록</h1>
<div class="navigation">
  <a href="/">홈</a>
  <a href="/history.jsp">위치 히스토리 목록</a>
  <a href="/load-wifi.jsp">Open API 와이파이 정보 가져오기</a>
  <a href="/bookmark-list.jsp">북마크 보기</a>
  <a href="/bookmark-group.jsp">북마크 그룹 관리</a>
</div>

<table id="history-table">
  <thead>
  <tr>
    <th>ID</th>
    <th>X좌표</th>
    <th>Y좌표</th>
    <th>조회일자</th>
    <th>비고</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td colspan="5" class="empty-message">위치 정보 조회 내역이 없습니다.</td>
  </tr>
  </tbody>
</table>

<script>
  window.onload = function () {
    loadHistoryList();
  };

  async function loadHistoryList() {
    try {
      const response = await fetch('/api/history', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      const result = await response.json();

      if (result.success) {
        updateHistoryTable(result.data);
      } else {
        alert('히스토리 목록 조회에 실패했습니다: ' + result.message);
      }
    } catch (error) {
      alert('히스토리 목록 조회 중 오류가 발생했습니다.');
      console.error(error);
    }
  }

  async function deleteHistory(id) {
    if (!confirm('히스토리를 삭제하시겠습니까?')) {
      return;
    }

    try {
      const response = await fetch(`api/history/${id}`, {
        method: 'DELETE'
      });

      const result = await response.json();

      if (result.success) {
        alert('히스토리가 삭제되었습니다.');
        loadHistoryList();
      } else {
        alert('히스토리 삭제에 실패했습니다: ' + result.message);
      }
    } catch (error) {
      alert('히스토리 삭제 중 오류가 발생했습니다.');
      console.error(error);
    }
  }

  function updateHistoryTable(historyList) {
    const tbody = document.querySelector('#history-table-tbody');
    tbody.innerHTML = '';

    if (historyList.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5" class="empty-message">위치 정보 조회 내역이 없습니다.</td></tr>';
      return;
    }

    historyList.forEach(history => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
            <td>${history.id}</td>
            <td>${history.lat}</td>
            <td>${history.lnt}</td>
            <td>${history.searchDate}</td>
            <td><button onclick="deleteHistory(${history.id})">삭제</button></td>
            `;
      tbody.appendChild(tr);
    });
  }
</script>
</body>
</html>
