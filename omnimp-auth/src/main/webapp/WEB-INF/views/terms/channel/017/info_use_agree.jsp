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
	<title>에뛰드 개인정보 수집 및 이용동의</title>
	<link rel="stylesheet" type="text/css" href="<c:out value='${ctx}'/>/css/bp_common.css">
</head>

<body>
<div class="pop-wrap">
	<div class="agree-txt">
		<h3>개인정보 수집 및 이용동의</h3>
		<p>1. 수집하는 개인정보의 항목 : 성명, 본인확인 값(CI), 성별, 생년월일, 내/외국인 구별, 아이디, 비밀번호, 휴대전화번호, 구매거래내역, 뷰티포인트 내역</p>
		<p>2. 수집 및 이용목적 : 서비스 이용에 따른 본인식별 / 가입연령 확인 / 불량회원의 부정이용 방지 / 공지사항 전달 / 본인 의사 확인 등을 위한 의사 소통 경로 확보</p>
		<p>3. 보유 및 이용기간 : 동의철회 시 혹은 회원탈퇴 시 까지</p>
		<p>* 고객님께서는 개인정보 수집 및 이용 동의에 거부할 수 있습니다. 다만, 거부하는 경우 회원가입이 불가능 합니다.</p>
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