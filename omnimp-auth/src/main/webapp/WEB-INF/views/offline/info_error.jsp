<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<%@page import="com.amorepacific.oneap.common.util.StringUtil"%>
<%@page import="com.amorepacific.oneap.common.util.ConfigUtil"%>
<%@page import="com.amorepacific.oneap.common.util.WebUtil"%>
<%@page import="com.amorepacific.oneap.common.util.OmniUtil"%>
<%@page import="com.amorepacific.oneap.common.vo.OmniConstants"%>
<%@page import="org.springframework.util.StringUtils"%>
<%	// 경로구분코드가 이니스프리몰로 확인 (WSO2의 클라이언트ID로 식별), 
	// 이니스프리앱에서 호출된 경우 dt=A 파라미터 확인 
	// 앱 내 X 버튼의 히스토리백 기능 대체 필요
	// window.location = “innimemapp://go_back”
	boolean isInniMobileBackAction = OmniUtil.isInniMobileBackAction(request);
	boolean isBeautyAngelMobileBackAction = OmniUtil.isBeautyAngelMobileBackAction(request);
	boolean isAmoreMallAOS = OmniUtil.isAmoreMallAOS(request);
	boolean isAmoreMallIOS = OmniUtil.isAmoreMallIOS(request);
%>
<!DOCTYPE html><!-- ME-FO-A0801 -->
<html lang="ko">
<head>
  <title>안내 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="false" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
	  $(document).ready(function() {
		  
		$('#btn_prev').on('click', function() {
			<% if (isInniMobileBackAction) { %>
				window.location = "innimemapp://go_back";
			<% } else if (isBeautyAngelMobileBackAction) { %>
				window.location = "toapp://go_back";
			<% } else if (isAmoreMallAOS) { %>
				window.apmall.closeWebview();
			<% } else if (isAmoreMallIOS) { %>
				window.location.href='apmall://closeWebview';				
			<% } else { %>
				window.history.back();
			<% } %>	
		});
		
	  });
	  
		var closeAction = function() {
			location.href = OMNIEnv.ctx + '/offline/login?chCd=<c:out escapeXml="false" value="${chCd}"/>';
		};		  
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="안내" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="error_wrap">
        <h2>고객님 불편을 드려 죄송합니다.</h2>
        <p class="txt">처리 중 장애가 발생하여 이용하실 수가 없습니다.<br />다시 한번 확인해주시거나, <br />잠시 후 이용해주시기 바랍니다.</p>
        <p><c:out escapeXml="false" value="${message}"/></p>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>