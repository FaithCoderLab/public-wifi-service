<%--
  Created by IntelliJ IDEA.
  User: kimsumin
  Date: 2025. 2. 20.
  Time: 오전 7:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>와이파이 정보 구하기</title>
  <style>
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 20px;
    }

    th, td {
      border: 1px solid #ddd;
      padding: 8px;
      text-align: center;
    }

    th {
      background-color: #04AA6D;
      color: white;
    }

    tr:nth-child(even) {
      background-color: #f2f2f2;
    }

    .navigation {
      margin-bottom: 20px;
    }

    .navigation a {
      margin-right: 10px;
    }

    .empty-message {
      text-align: center;
      padding: 20px;
    }

    .button-container {
      margin: 20px 0;
    }
  </style>
</head>
<body>
<h1>북마크 그룹 관리</h1>
<div class="navigation">
  <a href="/">홈</a>
  <a href="/history.jsp">위치 히스토리 목록</a>
  <a href="/load-wifi.jsp">Open API 와이파이 정보 가져오기</a>
  <a href="/bookmark-list.jsp">북마크 보기</a>
  <a href="/bookmark-group.jsp">북마크 그룹 관리</a>
</div>

<div class="button-container">
  <button onclick="showAddBookmarkGroupForm()">북마크 그룹 추가</button>
</div>

<table id="bookmark-group-table">
  <thead>
  <tr>
    <th>ID</th>
    <th>북마크 이름</th>
    <th>순서</th>
    <th>등록일자</th>
    <th>수정일자</th>
    <th>비고</th>
  </tr>
  </thead>
  <tbody id="bookmark-group-table-tbody">
  <tr>
    <td colspan="6" class="empty-message">북마크 그룹이 존재하지 않습니다.</td>
  </tr>
  </tbody>
</table>

<script>
  window.onload = function() {
    loadBookmarkGroupList();
  };

  async function loadBookmarkGroupList() {
    try {
      const response = await fetch('/api/bookmark-group', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      const result = await response.json();

      if (result.success) {
        updateBookmarkGroupTable(result.data);
      } else {
        alert('북마크 그룹 목록 조회에 실패했습니다: ' + result.message);
      }
    } catch (error) {
      alert('북마크 그룹 목록 조회 중 오류가 발생했습니다.');
      console.error(error);
    }
  }

  function showAddBookmarkGroupForm() {
    const name = prompt('북마크 그룹 이름을 입력하세요:');
    if (!name) return;

    const order = prompt('순서를 입력하세요:');
    if (!order) return;

    addBookmarkGroup(name, parseInt(order));
  }

  async function addBookmarkGroup(name, order) {
    try {
      const response = await fetch('/api/bookmark-group', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({name, order})
      });

      const result = await response.json();

      if (result.success) {
        alert('북마크 그룹이 추가되었습니다.');
        loadBookmarkGroupList();
      } else {
        alert('북마크 그룹 추가에 실패했습니다: ' + result.message);
      }
    } catch (error) {
      alert('북마크 그룹 추가 중 오류가 발생했습니다.');
      console.error(error);
    }
  }

  async function editBookmarkGroup(id, currentName, currentOrder) {
    const name = prompt('수정할 북마크 그룹 이름을 입력하세요:', currentName);
    if (!name) return;

    const order = prompt('수정할 순서를 입력하세요:', currentOrder);
    if (!order) return;

    try {
      const response = await fetch(`/api/bookmark-group/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({name, order: parseInt(order)})
      });

      const result = await response.json();

      if (result.success) {
        alert('북마크 그룹이 수정되었습니다.');
        loadBookmarkGroupList();
      } else {
        alert('북마크 그룹 수정에 실패했습니다: ' + result.message);
      }
    } catch (error) {
      alert('북마크 그룹 수정 중 오류가 발생했습니다.');
      console.error(error);
    }
  }

  async function deleteBookmarkGroup(id) {
    if (!confirm('북마크 그룹을 삭제하시겠습니까?')) {
      return;
    }

    try {
      const response = await fetch(`/api/bookmark-group/${id}`, {
        method: 'DELETE'
      });

      const result = await response.json();

      if (result.success) {
        alert('북마크 그룹이 삭제되었습니다.');
        loadBookmarkGroupList();
      } else {
        alert('북마크 그룹 삭제에 실패했습니다: ' + result.message);
      }
    } catch (error) {
      alert('북마크 그룹 삭제 중 오류가 발생했습니다.');
      console.error(error);
    }
  }

  function updateBookmarkGroupTable(groupList) {
    const tbody = document.querySelector('#bookmark-group-table-tbody');
    tbody.innerHTML = '';

    if (!groupList || groupList.length === 0) {
      tbody.innerHTML = '<tr><td colspan="6" class="empty-message">북마크 그룹이 존재하지 않습니다.</td></tr>';
      return;
    }

    groupList.forEach(group => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
                <td>${group.id}</td>
                <td>${group.name}</td>
                <td>${group.orderNo}</td>
                <td>${group.regDate}</td>
                <td>${group.modDate}</td>
                <td>
                    <button onclick="editBookmarkGroup(${group.id}, '${group.name}', ${group.orderNo})">수정</button>
                    <button onclick="deleteBookmarkGroup(${group.id})">삭제</button>
                </td>
            `;
      tbody.appendChild(tr);
    });
  }
</script>
</body>
</html>