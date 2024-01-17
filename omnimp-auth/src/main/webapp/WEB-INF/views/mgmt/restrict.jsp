<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.amorepacific.oneap.common.util.StringUtil"%>
<%@page import="com.amorepacific.oneap.common.util.ConfigUtil"%>
<%@page import="com.amorepacific.oneap.common.util.WebUtil"%>
<%@page import="com.amorepacific.oneap.common.util.OmniUtil"%>
<%@page import="com.amorepacific.oneap.common.vo.OmniConstants"%>
<%@page import="org.springframework.util.StringUtils"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<%	boolean isMobileApp = OmniUtil.isMobileApp(request);
	String hideHeader = WebUtil.getStringSession("hideHeader");
	if (StringUtils.isEmpty(hideHeader)) {
		if (WebUtil.isMobile() || isMobileApp) {
			hideHeader = WebUtil.getStringParameter("hh", "N");
			WebUtil.setSession("hideHeader", hideHeader);
		} else {
			hideHeader = "N"; // 웹일 경우는 항상 보여줌.
		}
	} else {
		if (!WebUtil.isMobile()) {
			hideHeader = "N"; // 웹일 경우는 항상 보여줌.
		}
	}
	
	// 경로구분코드가 이니스프리몰로 확인 (WSO2의 클라이언트ID로 식별), 
	// 이니스프리앱에서 호출된 경우 dt=A 파라미터 확인 
	// 앱 내 X 버튼의 히스토리백 기능 대체 필요
	// window.location = “innimemapp://go_back”
	boolean isInniMobileBackAction = OmniUtil.isInniMobileBackAction(request);
	boolean isBeautyAngelMobileBackAction = OmniUtil.isBeautyAngelMobileBackAction(request);
	boolean isAmoreMallAOS = OmniUtil.isAmoreMallAOS(request);
	boolean isAmoreMallIOS = OmniUtil.isAmoreMallIOS(request);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>가입 제한 안내 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	$('#go-home').on('click', function() {
		//location.href = OMNIEnv.ctx + '/go-join';
		goAction();
	});
  });
  var goAction = function() {
	if('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>' == null || '<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>' == "") {
		<% if (isAmoreMallAOS) { %>
			window.apmall.closeWebview();
		<% } else if (isAmoreMallIOS) { %>
			window.location.href='apmall://closeWebview';
		<% } else { %>
			location.href = '<c:out escapeXml="false" value="${homeurl}" />';
		<% } %>
	} else {
		<% if (isAmoreMallAOS) { %>
			window.apmall.closeWebview();
		<% } else if (isAmoreMallIOS) { %>
			window.location.href='apmall://closeWebview';
		<% } else { %>
			location.href = decodeURIComponent('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>');
		<% } %>
	}
  };
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="가입 제한 안내" type="goaction"/>
    <!-- container -->
    <section class="container">
    <c:if test="${not empty withdrawDate}">
      <div class="page_top_area">
        <h2>회원님은 탈퇴 후 30일이 경과되지 않으셨습니다.</h2>
        <p>탈퇴 후 30일이 지나면 다시 회원가입이 가능합니다.</p>
      </div>
      <div class="user_info">
        <strong class="st_txt"><span>탈퇴일자</span> <c:out value="${withdrawDate}" /></strong>
      </div>
    </c:if>
    <c:if test="${not empty joinrestrict}">
      <div class="page_top_area">
        <h2>뷰티포인트 통합회원은 만 14세 이상 부터 가입 가능합니다.</h2>
        <p>고객님의 양해 부탁드리며, 다음에 다시 이용해주시기 바랍니다.</p>
      </div>
    </c:if>  
      <div class="btn_submit mt40">
        <button type="button" class="btnA btn_blue" id='go-home'>확인</button>
      </div>
      
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>