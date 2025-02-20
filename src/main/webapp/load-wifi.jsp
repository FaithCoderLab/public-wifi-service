<%--
  Created by IntelliJ IDEA.
  User: kimsumin
  Date: 2025. 2. 18.
  Time: 오후 9:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>와이파이 정보 구하기</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div id="loading">
    <p>Open API 와이파이 정보를 가져오는 중입니다.</p>
</div>

<script>
  window.onload = async function() {
      try {
          const response = await fetch('/api/wifi/load', {
              method: 'POST'
          });
          const result = await response.json();

          if (result.success) {
              alert(`${result.data}개의 WIFI 정보를 성공적으로 가져왔습니다.`);
          } else {
              alert('WIFI 정보 가져오기를 실패했습니다:' + result.message);
          }
      } catch (error) {
          alert('WIFI 정보 가져오기 중 오류가 발생했습니다.');
          console.error(error);
      } finally {
          window.location.href = '/';
      }
  };
</script>
</body>
</html>
