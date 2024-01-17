<%@page import="com.amorepacific.oneap.common.util.WebUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
	<common:meta/>
	<common:js auth="true" popup="true"/>
	<meta charset="UTF-8">
	<title>Insert title here</title>
	<script type="text/javascript">
	</script>
</head>
<body>
	<div style="text-align:center;">
		<img src='<c:out value="${ctx}"/>/qr-code'/>
	</div>	
</body>
</html>