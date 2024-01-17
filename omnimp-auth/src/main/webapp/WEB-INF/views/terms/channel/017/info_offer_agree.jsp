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
	<title>에뛰드 개인정보 제공동의</title>
	<link rel="stylesheet" type="text/css" href="<c:out value='${ctx}'/>/css/bp_common.css">
</head>

<body>
<div class="pop-wrap">
	<div class="agree-txt">
		<h3>개인정보 제공동의</h3>
		<p>1. 개인정보를 제공하는 자 : 아모레퍼시픽</p>
		<p>2. 제공받는자 : 에뛰드</p>
		<p>3. 개인정보를 제공하는 목적 : 뷰티포인트 고객에 대한 제공받는 자의 고유 CRM 활동, 재화나 서비스 홍보/안내/마케팅 제공</p>
		<p>4. 제공하는 개인정보의 항목 : 성명, 본인확인 값, 생년월일, 내/외국인여부, 성별, 휴대전화번호, (고객이 입력한 경우) 이메일 및 주소</p>
		<p>5. 보유 및 이용기간 : 동의 철회 시까지</p>
		<p>고객님께서는 개인정보 제공동의에 거부할 수 있습니다.다만 거부하는 경우 에뛰드 혜택 제공이 불가능 합니다.</p>		
	</div>
</div>
</body>
<script type="text/javascript">
	var uagent = navigator.userAgent.toLocaleLowerCase();
	
	function CloseTerms() {
		if(!(uagent.search("iphone")>-1||uagent.search("ipod")>-1||uagent.search("ipad")>-1)){
	    	close();
	    }else {
			history.back();
	    }
	}
</script>
</html>