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
		$(document).ready(function() {
			window.close();
			window.opener.location.reload();
			/*
			$('#cofirm').on('click', function() {
				window.close();
				window.opener.location.reload();
			});
			*/
		});
	</script>
</head>
<body>
	<div style="text-align:center;">
		<c:choose>
		    <c:when test="${snsType eq 'KA'}">
		       <h2>카카오</h2>	       
		    </c:when>
		    <c:when test="${snsType eq 'NA'}">
		       <h2>네이버</h2>	       
		    </c:when>
		    <c:otherwise>
		       <h2>페이스북</h2>
		    </c:otherwise>
		</c:choose>
		<c:choose>
		    <c:when test="${resultCode eq '0000'}">
		       <h3>연동 성공</h3>	       
		    </c:when>
		    <c:otherwise>
		       <h3>연동 실패 (<c:out value="${resultCode}" />)</h3>
		    </c:otherwise>
		</c:choose>
		<hr/>	
		<input type="button" id="cofirm" value="확인">
	</div>	
</body>
</html>