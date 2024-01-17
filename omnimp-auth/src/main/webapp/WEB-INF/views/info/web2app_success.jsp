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
		  var UserAgentCheck = navigator.userAgent.toLowerCase();
		  if ( UserAgentCheck.indexOf('android') > -1) {
			  $('.login_tootip_left_top').hide();
		    }else{
		    	$('.btn_submit').hide();
		    }
		  
		  $('#btn_web2app_close').on('click', function() {
			  window.Android.closeApp();
			});
	  });
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<%-- <common:header title="안내" type="close"/> --%>
    <!-- container -->
    <section class="container">
    <span class="login_tootip_left_top verL">
	        상단의 버튼을 선택해서 이동하세요.
	  </span>
      <div class="error_wrap error_wrap_success">
        <h2>${web2App_userId } 계정으로<br>로그인 연동이 <span>완료</span> 되었습니다.</h2>
        <p class="txt">로그인을 요청한 브라우저 또는 앱으로 이동해주세요.</p>
      </div>
      <div class="btn_submit">
          <button type="button" class="btnA btn_blue" id='btn_web2app_close' ap-click-area="페이지 로그인 연동하기" ap-click-name="페이지 로그인 연동하기- 로그인 연동하기 버튼" ap-click-data="로그인 연동하기">앱 종료하고 이동하기</button>
        </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>