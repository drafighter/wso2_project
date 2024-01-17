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
	<title>에뛰드 개인정보 수집 및 이용 동의(마케팅)</title>
	<link rel="stylesheet" type="text/css" href="<c:out value='${ctx}'/>/css/bp_common.css">
</head>

<body>
<div class="pop-wrap">
	<div class="agree-txt">
		<h3>개인정보 수집 및 이용 동의(마케팅)</h3>
		<p>1. 수집하는 개인정보의 항목 : 이메일, 주소, 휴대전화번호</p>
		<p>2. 수집 및 이용목적 : 본인 동의 시 회사 또는 제휴사의 서비스 / 사업 및 정책 / 기타 이벤트에 관한 정보 제공 및 그에 따른 경품 등 물품 배송</p>
		<p>3. 보유 및 이용기간 : 동의철회 시 혹은 회원탈퇴 시 까지</p>
		<p>* 고객님께서는 개인정보 수집 및 이용 동의에 거부할 수 있습니다. 다만, 거부하는 경우 할인, 마케팅, 프로모션 등의 정보를 받아볼 수 없습니다.</p>		
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