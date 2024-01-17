<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<%@ taglib prefix="login" tagdir="/WEB-INF/tags/login" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>로그인 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="false" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
	  $(document).ready(function() {
		$('#authLogin').submit();
	  });
  </script>   
</head>

<body>
	<form method="post" id="authLogin" action="<c:out value="${auth.authUrl}"/>">
		<input id="token" name="token" type="hidden" value="<c:out value="${auth.token}"/>">
		<input id="sessionDataKey" name="sessionDataKey" type="hidden" value="<c:out value="${auth.sessionDataKey}"/>">
	</form> 
</body>

</html>