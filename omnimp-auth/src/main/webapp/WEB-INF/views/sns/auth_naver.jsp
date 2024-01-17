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
	<title>네이버 인증 | 옴니통합회원</title>	
	<style>	
		body {
			background: #f5f6f7;
		}
	
		div {			
			text-align: center;
		}
		
		button {
			background-color: #f5f6f7;
    		border-color: #f5f6f7;
    	    border-style: solid;
		}
		
		hr {
			width: 400px;
		}		
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			location.replace('<c:out escapeXml="false" value="${authUrl}" />');
		});		
	</script>
</head>
<body>
	<div>
		<h1>간편 로그인</h1>
	</div>
	<hr/>
	<div>
		<p>네이버 계정으로 간편하게 로그인 할 수 있습니다.</p>
		<p>네이버 로그인을 선택 해 주세요.</p>
	</div>
	<br/>
	<div>
		<button id="naverAuth">
			<image src="<c:out value='${ctx}'/>/images/common/btn_login_na.png" alt="NAVER"></image>
			<h2>네이버 로그인</h2>
		</button>
	</div>
</body>
</html>