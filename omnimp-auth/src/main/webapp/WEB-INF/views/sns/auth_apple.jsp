<%@page import="com.amorepacific.oneap.common.util.WebUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
 <c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
<common:meta/>
	<common:js auth="true" popup="true"/>
	<tagging:google/>
	<meta charset="UTF-8">
	
	<script type="text/javascript">

		window.onload = function(){      // 페이지 로딩 후 즉시 함수 실행(window.onload)
		    
		}
	
		$(document).ready(function() {
			
		});
		
		function fnAppleLogin() {
			location.href = encodeURI('https://appleid.apple.com/auth/authorize?client_id=<c:out escapeXml="false" value="${restApiKey}"/>&redirect_uri=<c:out escapeXml="false" value="${callback}"/>&state=<c:out escapeXml="false" value="${state}"/>&response_type=code id_token&response_mode=form_post&scope=name');
		}
	</script>
</head>
<body onload="fnAppleLogin();">
<div id="ap-root"></div>
	<script type="text/javascript" src="https://appleid.cdn-apple.com/appleauth/static/jsapi/appleid/1/en_US/appleid.auth.js"></script>
	<script type="text/javascript">
	    AppleID.auth.init({
	        clientId : '<c:out escapeXml="false" value="${restApiKey}" />',
	        scope : 'name',
	        redirectURI : '<c:out escapeXml="false" value="${callback}" />',
	        state : '<c:out escapeXml="false" value="${state}" />',
	        nonce : '',
	        usePopup : false //or false defaults to false
	    });
	</script>
</body>
</html>