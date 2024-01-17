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
	<title>아모레퍼시픽 뷰티포인트 개인정보 제3자 제공 동의 약관 ※ 외부 컨텐츠 마케팅 활용</title>
	<link rel="stylesheet" type="text/css" href="<c:out value='${ctx}'/>/css/bp_common.css">
</head>

<body>
<div class="pop-wrap">
	<div class="agree-txt">
		<h3>개인정보 제공동의</h3>
		<p>1. 회사는 이용자의 동의가 있거나 관련법령의 규정에 의한 경우를 제외하고는 제2조에서 고지한 범위를 넘어 이용자의 개인정보를 이용하거나 제3자에게 제공하지 않습니다.</p>
		<p>2. 회사는 이용자의 개인정보 관리 및 보다 다양한 서비스 제공을 위하여 이용자의 별도 동의를 얻어 다음과 같이 개인정보를 제공합니다.</p>
		<table>
			<caption>개인정보를 제공받는자, 제공목적, 제공하는 개인정보 항목, 보유 및 이용기간</caption>
			<colgroup>
				<col width="20%"/>
				<col width="*"/>
				<col width="20%"/>
				<col width="20%"/>
			</colgroup>
			<thead>
				<tr>
					<th scope="col">제공받는 자</th>
					<th scope="col">제공 목적</th>
					<th scope="col">제공하는 개인정보 항목</th>
					<th scope="col">보유 및 이용기간</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="agree_txt_emp">Meta Platform, Inc.</td>
					<td class="agree_txt_emp">페이스북 제휴 마케팅</td>
					<td>이메일, 휴대폰 번호</td>
					<td class="agree_txt_emp">페이스북 캠페인 대상선정 이후 14일</td>
				</tr>
			</tbody>
		</table>
		<p>3. 다만, 다음 각 호의 경우는 이용자의 별도 동의 없이 제공될 수 있습니다.</p>
		<ul class="list-dash">
			<li>- 서비스 제공에 따른 요금정산을 위하여 필요한 경우</li>
			<li>- 통계작성, 학술연구 또는 시장조사를 위하여 필요한 경우로서 특정 개인을 알아볼 수 없는 형태로 가공하여 연구단체, 설문조사, 리서치 기관 등에 제공하는 경우</li>
			<li>- 개인정보보호법, 정보통신망 이용촉진 및 정보보호 등에 관한 법률, 통신비밀보호법, 국세기본법, 금융실명거래 및 비밀보장에 관한 법률, 신용정보의 이용 및 보호에 관한 법률, 전기통신기본법, 전기통신사업법, 지방세법, 소비자보호법, 형사소송법 등 법률상 특별한 규정이 있는 경우</li>
		</ul>
		<p>4. 이용자는 제3자에 대한 개인정보 제공 동의를 거부할 수 있으며, 동의를 거부할 때에는 제3자 제공에 따른 서비스 이용에 제한을 받을 수 있습니다.</p>
		<p>5. 회사는 개인정보를 국외의 제3자에게 제공할 때에는 이용자에게 내용을 알리고 동의를 받습니다.</p>
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