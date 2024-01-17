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
		  
		$('#btn_web2app_login').on('click', function() {
			//uuid와 accessToke이 있으면
			location.href = OMNIEnv.ctx + '/login/web2app/complete';
		});
		
		$('#login_cancel').on('click', function() {
			location.href = OMNIEnv.ctx + '/login/web2app/cancel';
		});
		
	  });
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
    <!-- container -->
    <section class="container">
      <div class="error_wrap_check">
        <h2>${web2App_userId } 계정으로<br>로그인 연동 하시겠습니까?</h2>
        <p></p>
        <div class="btn_submit">
          <button type="button" class="btnA btn_blue" id='btn_web2app_login' ap-click-area="페이지 로그인 연동하기" ap-click-name="페이지 로그인 연동하기- 로그인 연동하기 버튼" ap-click-data="로그인 연동하기">로그인 연동하기</button>
        </div>
        <div class="botton_cancel">
	        <a href="javascript:;" id="login_cancel">연동 취소하기</a>
        </div>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>