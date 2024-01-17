<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="com.amorepacific.oneap.common.util.WebUtil"%>   
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<%@ taglib prefix="login" tagdir="/WEB-INF/tags/login" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ taglib prefix="fn" uri = "http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html><!-- ME-FO-A0206, ME-FO-A0401 -->
<html lang="ko">
<head>
  <title>휴대폰 로그인 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <script type="text/javascript" src="<c:out value="${ctx}" />/js/phone-login.js"></script>
  <script>
		window.fbAsyncInit = function() {
			FB.init({
				appId      : '<c:out escapeXml="false" value="${FBRestApiKey}" />', // 내 앱 ID를 입력한다.
				cookie     : true,
				xfbml      : true,
				version    : 'v12.0',
				status	   : true
			});
			FB.AppEvents.logPageView();   
		};
		
		//기존 로그인 상태를 가져오기 위해 Facebook에 대한 호출
		function statusChangeCallback(response){
			statusChangeCallback(response);
		}
  </script>  
  <tagging:google/>
</head>
<script async defer crossorigin='anonymous' src='https://connect.facebook.net/ko_KR/sdk.js#xfbml=1&version=v12.0&appId=<c:out escapeXml="false" value="${FBRestApiKey}" />&autoLogAppEvents=1' nonce='xbW0ijkt'></script>
<body>
  <tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="휴대폰 로그인" type="prv"/>
    <!-- container -->
    <section class="container">
      <div class="sec_login">
          <div class="input_form">
            <span class="inp">
              <input type="text" id="name" autocomplete="off" class="inp_text" placeholder="이름(두 자 이상 입력)" ap-click-area="휴대폰 로그인" ap-click-name="휴대폰 로그인 - 이름 입력란" ap-click-data="이름 입력"  title="이름 입력"/>
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
          </div>
          <div class="input_form">
            <span class="inp">
              <input type="tel" id="phone" autocomplete="off" class="inp_text" maxlength="11" placeholder="휴대폰 번호 입력 (‘-’ 생략)" ap-click-area="휴대폰 로그인" ap-click-name="휴대폰 로그인 - 휴대폰 번호 입력란" ap-click-data="휴대폰 번호 입력"  title="휴대폰 번호 입력"/>
              <button type="button" class="btn_del"><span class="blind">삭제</span></button>
              <input type='hidden' id='smsSeq'>
            </span>
          </div>
          <div class="btn_submit mt20">
            <button type="button" id="sendsms" class="btnA btn_blue" disabled ap-click-area="휴대폰 로그인" ap-click-name="휴대폰 로그인 - 인증번호 발송 버튼" ap-click-data="인증번호 발송">인증번호 발송</button>
            <!-- <button type="submit" class="btnA btn_blue" disabled>인증하고 로그인</button> -->
          </div>
        <c:if test="${(empty isAndroidApp and fn:length(login.idpAuthenticatorMapping) > 3) or (not empty isAndroidApp and fn:length(login.idpAuthenticatorMapping) > 4)}"> <!-- Andorid App 에서 Facebook Login 노출되지 않도록 임시로 조치 - 2021.11.24 -->
        	<login:other-login gaArea="로그인" mobilelogin="true" lastlogin="${lastlogin}" resp="${login}"/>
        </c:if>
        <c:if test="${(empty isAndroidApp and fn:length(login.idpAuthenticatorMapping) < 4) or (not empty isAndroidApp and fn:length(login.idpAuthenticatorMapping) < 5)}">
        	<login:other-login-simple gaArea="로그인" mobilelogin="true" lastlogin="${lastlogin}" resp="${login}"/>
        </c:if>		
      </div>
      <!-- //sec_login -->
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
  <input type='hidden' id='sessionDataKey' value='<c:out value="${login.sessionDataKey}" />'/>
  <input type='hidden' id='orderUrl' value='<c:out value="${orderurl}" />'/>
</body>

</html>