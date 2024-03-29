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
	<title>개인정보 수집 이용 동의</title>
	<link rel="stylesheet" type="text/css" href="<c:out value='${ctx}'/>/css/bp_common.css">
</head>

<body>
<div class="pop-wrap">
	<div class="agree-txt">
		<h3>개인정보 수집 이용 동의</h3>
		<p class="agree-preamble">회사가 회원가입 시 수집하는 개인정보 항목과 그 수집ㆍ이용의 주된 목적은 아래와 같습니다.</p>
		<h6>* 뷰티포인트 통합멤버십 온라인 서비스</h6>
		<table>
			<caption>뷰티포인트 통합멤버십 온라인 서비스</caption>
			<colgroup>
				<col width="20%"/>
				<col width="*"/>
				<col width="20%"/>
			</colgroup>
			<thead>
				<tr>
					<th scope="col">수집항목</th>
					<th scope="col">수집ㆍ이용 목적</th>
					<th scope="col">이용ㆍ보유 기간</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>성명, 본인확인 값, 생년월일, 내/외국인 여부, ID, PW, 성별</td>
					<td>서비스 이용에 따른 본인식별 / 가입연령 확인 / 회원의 부정이용 방지</td>
					<td rowspan="3" class="agree_txt_emp">회원 탈퇴 시까지<br>(처리방침 제5조 참조)</td>
				</tr>
				<tr>
					<td>휴대전화번호</td>
					<td>공지사항 전달 / 본인의사확인, 불만처리 등을 위한 의사소통 경로 확보</td>
				</tr>
				<tr>
					<td>구매 거래내역, 뷰티포인트 내역</td>
					<td>이용자의 서비스 이용 및 상품구매에 따른 멤버십 회원관리</td>
				</tr>
			</tbody>
		</table>
		<p>
			&#8251; 고객님께서는 개인정보 수집 및 이용(필수) 동의에 거부할 수 있습니다. <br>단, 거부할 경우 뷰티포인트 멤버십 가입이 불가능 합니다.<br>
			&#8251; 본인확인값(CI)은 회원의 무분별한 회원탈퇴 및 재가입으로 인한 부정이용 및 피해방지를 위해 30일간 보관됩니다.
		</p>
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