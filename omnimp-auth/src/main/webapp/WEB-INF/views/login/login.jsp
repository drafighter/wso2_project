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
<%@ taglib prefix="fn" uri = "http://java.sun.com/jsp/jstl/functions"%>
<spring:eval expression="@environment.getProperty('spring.profiles.active')" var="profile" />
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>로그인 | 옴니통합회원</title>
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
	
	  function getParameterByName(name, url) {
	      if (!url) {
	          url = window.location.href;
	      }
	      name = name.replace(/[\[\]]/g, '\\$&');
	      var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
	      results = regex.exec(url);
	      if (!results) return null;
	      if (!results[2]) return "";
	      return decodeURIComponent(results[2].replace(/\+/g, ' '));
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
			var UserAgent = navigator.userAgent;
			var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
			<c:if test="${mobile}">
				isMobile = true;
			</c:if>
			
			if(isMobile && '<c:out escapeXml="false" value="${sessionScope.popup}"/>' == 'true') {
				opener.location.href = window.location.href;
				window.close();
			}
			
	  });
	  
	  window.onload = function () {
			if($('#loginid').val().trim() != '' && $('#loginpassword').val().trim() != '') {
				$('#dologin').removeAttr('disabled');
			}
			
			$.ajax({url:'https://tagmanager.amorepacific.com/currentip',type:'get',success:function(_data){
			   if(_data){	
				if(_data.ip) {  
				  //  console.log(_data.ip);
					$.ajax({url:OMNIEnv.ctx + '/ga/tagging/cookie?uip=' + _data.ip,type:'get'});	
				}
			   }
			}});
			
			<c:if test="${isSmsSystemCheck}">
				$("#mobile-login").off("click");
				$('#mobile-login').on('click', function() {
					OMNI.popup.open({
						id:'sms-systemcheck',
						content: '02:20~05:00 까지 서비스 점검 진행으로 이용이 어렵습니다.<br/>고객님께 불편 드리는 점 양해 부탁드립니다.',
						closelabel:'확인',
						closeclass:'btn_blue'
					});					
				});
				
				$("#search_id").off("click");
				$('#search_id').on('click', function() {
					OMNI.popup.open({
						id:'sms-systemcheck',
						content: '02:20~05:00 까지 서비스 점검 진행으로 이용이 어렵습니다.<br/>고객님께 불편 드리는 점 양해 부탁드립니다.',
						closelabel:'확인',
						closeclass:'btn_blue'
					});					
				});
			</c:if>
	  }
  </script>
  <tagging:google/>  
</head>
<body>
<script async defer crossorigin='anonymous' src='https://connect.facebook.net/ko_KR/sdk.js#xfbml=1&version=v12.0&appId=<c:out escapeXml="false" value="${FBRestApiKey}" />&autoLogAppEvents=1' nonce='xbW0ijkt'></script>
<script type="text/javascript" src="https://appleid.cdn-apple.com/appleauth/static/jsapi/appleid/1/en_US/appleid.auth.js"></script>
  <tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<c:if test="${isLoginPageHeader}">
  		<common:header title="로그인" type="gosystem"/>
  	</c:if>
  	<c:if test="${!isLoginPageHeader}">
  		<common:header title="로그인" type="no"/>
  	</c:if>
    <!-- container -->
    <section class="container">
    <c:choose>
    	<c:when test="${mobile and not empty appLogin}">
		    <div class="applogin_guide">
		     <c:choose>
    			<c:when test="${not empty appLoginPathChCd}">
    				<div class="applogin_title_log" style="background-image: url(<c:out value='${ctx}'/>/images/common/App-Logo-${appLoginPathChCd}.png) ;"></div>
    			</c:when>
    			<c:otherwise>
    				<div class="applogin_title_log" style="background-image: url(<c:out value='${ctx}'/>/images/common/App-Logo-${chCd}.png) ;"></div>
    			</c:otherwise>
    		</c:choose>
		        <p class="app_login_guide_text">${appLoginChNm} 앱으로 간편하게 로그인 하세요.</p>
		        <button type="button" id="doApplogin" class="btnA btn_white apploginbtn" data-chnm="${appLoginChNm}" data-applink="${appLogin}" ap-click-area="앱로그인" ap-click-name="로그인 - 로그인 버튼" ap-click-data="로그인">
		        <div class="app_login_btn_lay">
		        	<c:choose>
		    			<c:when test="${not empty appLoginPathChCd}">
		    				<div class="app_login_btn_log" style="background-image: url(<c:out value='${ctx}'/>/images/common/Icon-App-btn-${appLoginPathChCd}.png) ;"></div>
		    			</c:when>
		    			<c:otherwise>
		    				<div class="app_login_btn_log" style="background-image: url(<c:out value='${ctx}'/>/images/common/Icon-App-btn-${chCd}.png) ;"></div>
		    			</c:otherwise>
		    		</c:choose>
			        <div class="padding_top_span">${appLoginChNm} 앱 로그인</div>
		        </div>
		        </button>
		    	<p class="app_login_guide_text app_login_text">뷰티포인트 통합회원 아이디와 비밀번호로<br>로그인할 수 있어요.</p>
		    </div>
    	</c:when>
    	<c:otherwise>
		    <div class="login_guide_text">
			    <span>아모레퍼시픽 뷰티포인트 통합회원<br>아이디로 로그인해주세요.</span>
		    </div>
    	</c:otherwise>
    </c:choose>
      <div class="sec_login">
        <login:keyboard/>
        <form onsubmit="return false">
          <div class="input_form">
            <span class="inp" id="loginid-span">
              <input type="text" id="loginid" autocomplete="off" class="inp_text" placeholder="아이디 입력" value="<c:out value="${login.username}"/>" ap-click-area="로그인" ap-click-name="로그인 - 아이디 입력란" ap-click-data="아이디 입력"   title="아이디 입력"/>
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="loginid-guide-msg" class="form_guide_txt is_success"></p>
          </div>
          <div class="input_form">
            <span class="inp" id="password-span">
              <input type="password" id="loginpassword" autocomplete="off" class="inp_text" placeholder="비밀번호 입력 (영문, 숫자, 특수문자 조합)" ap-click-area="로그인" ap-click-name="로그인 - 비밀번호 입력란" ap-click-data="비밀번호 입력"  title="비밀번호 입력"/>
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="password-guide-msg" class="form_guide_txt is_success"></p>
          </div>
          <div id='login-noti-panel'><p id="login-noti-msg" class="form_guide_txt" style='display:none;'></p></div>
          <div class="btn_submit mt20">
            <c:if test="${mobile}">
            <span class="checkboxA pr58">
              <input type="checkbox" id="i_autologin" name="i_autologin" checked title="자동로그인"/>
              <label for="i_autologin" ap-click-area="로그인" ap-click-name="로그인 - 자동로그인 체크 박스" ap-click-data="자동로그인"><span class="checkbox_label">자동로그인</span></label>
            </span>
            </c:if>
            <span class="checkboxA">
              <input type="checkbox" id="i_saveid" name="i_saveid" checked title="아이디 저장"/>
              <label for="i_saveid" ap-click-area="로그인" ap-click-name="로그인 - 아이디 저장 체크 박스" ap-click-data="아이디 저장"><span class="checkbox_label">아이디 저장</span></label>
            </span>
          </div>
          <div class="login_opt">
            <button type="button" id="dologin" class="btnA btn_blue loginbtn" disabled ap-click-area="로그인" ap-click-name="로그인 - 로그인 버튼" ap-click-data="로그인">로그인</button>
          </div>
        </form>
        <c:if test="${(empty isAndroidApp and fn:length(login.idpAuthenticatorMapping) > 3) or (not empty isAndroidApp and fn:length(login.idpAuthenticatorMapping) > 4)}"> <!-- Andorid App 에서 Facebook Login 노출되지 않도록 임시로 조치 - 2021.11.24 -->
        	<login:other-login gaArea="로그인" mobilelogin="false" lastlogin="${lastlogin}" resp="${login}"/>
        </c:if>
        <c:if test="${(empty isAndroidApp and fn:length(login.idpAuthenticatorMapping) < 4) or (not empty isAndroidApp and fn:length(login.idpAuthenticatorMapping) < 5)}">
        	<login:other-login-simple gaArea="로그인" mobilelogin="false" lastlogin="${lastlogin}" resp="${login}"/>
        </c:if>
		
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
  <input type='hidden' id='sessionDataKey' value='<c:out value="${login.sessionDataKey}" />'/>
  <input type='hidden' id='loginFormActionUrl' value='<c:out value="${login.loginFormActionUrl}" />'/>
  <input type='hidden' id='orderUrl' value='<c:out value="${orderurl}" />'/>
</body>

</html>