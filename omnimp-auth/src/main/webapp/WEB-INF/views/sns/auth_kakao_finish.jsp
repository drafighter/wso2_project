<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="otl" uri="/WEB-INF/tlds/oneap-taglibs.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<title>가입된 회원 | 옴니통합회원</title>
	<common:meta/>
	<common:css/>
	<common:js />
	<tagging:google/>
	<script type="text/javascript">
		window.AP_SIGNUP_TYPE = '기가입';
		window.AP_SIGNUP_AUTH = 'SNS';
		dataLayer.push({event: 'signup_complete'});  
		location.href = '${authUrl}';
	</script>
</head>
<body>
</body>
</html>


