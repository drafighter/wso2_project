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
<spring:eval expression="@environment.getProperty('spring.profiles.active')" var="profile" />
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>로그인 완료  | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <common:backblock block="true"/>  
  <script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-login.js"></script>
  <script>
	  function checkSessionKey() {
	      $.ajax({
	          type: "GET",
	          url: "/logincontext?sessionDataKey=" + getParameterByName("sessionDataKey") + "&relyingParty=" + getParameterByName("relyingParty") + "&tenantDomain=" + getParameterByName("tenantDomain"),
	          success: function (data) {
	              if (data && data.status == 'redirect' && data.redirectUrl && data.redirectUrl.length > 0) {
	                  window.location.href = data.redirectUrl;
	              }
	          },
	          cache: false
	      });
	  }
	  
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
	  
	  $(document).ready(function() {

	  });

  </script>
</head>
<body>
<script async defer crossorigin='anonymous' src='https://connect.facebook.net/ko_KR/sdk.js#xfbml=1&version=v12.0&appId=<c:out escapeXml="false" value="${FBRestApiKey}" />&autoLogAppEvents=1' nonce='xbW0ijkt'></script>
  <tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="로그인 완료" type="gosystem"/>
    <!-- container -->
    <section class="container">
    <div class="login_guide_text">
	    <span>Facebook 로그인 완료 페이지</span>
    </div>
      <div class="sec_login">
        <login:keyboard/>
      	<div>
      		<p>SNS User ID : <c:out value="${snsId}"/></p>
      		<p>Name : <c:out value="${name}"/></p>
      	</div>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
  <input type='hidden' id='sessionDataKey' value='<c:out value="${login.sessionDataKey}" />'/>
  <input type='hidden' id='loginFormActionUrl' value='<c:out value="${login.loginFormActionUrl}" />'/>
  <input type='hidden' id='orderUrl' value='<c:out value="${orderurl}" />'/>
</body>

</html>