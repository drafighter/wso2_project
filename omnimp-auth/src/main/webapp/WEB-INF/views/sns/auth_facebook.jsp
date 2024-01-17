<%@page import="com.amorepacific.oneap.common.util.WebUtil"%>
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
<common:meta/>
	<common:js auth="true" popup="true"/>
	<tagging:google/>
	<meta charset="UTF-8">
	
	<script type="text/javascript">
	
	function openPopup(){
	    var win = window.open('', 'win', 'width=1, height=1, scrollbars=yes, resizable=yes');
		if (win == null || typeof(win) == "undefined" || (win == null && win.outerWidth == 0) || (win != null && win.outerHeight == 0) || win.test == "undefined") {
			alert("팝업 차단 기능이 설정되어있습니다\n\n차단 기능을 해제(팝업허용) 한 후 다시 이용해 주십시오.\n\n만약 팝업 차단 기능을 해제하지 않으면\n정상적인 주문이 이루어지지 않습니다.");
			if(win){
				win.close();
			}
			return;
		} else if (win) {
			if (win.innerWidth === 0){
				alert("팝업 차단 기능이 설정되어있습니다\n\n차단 기능을 해제(팝업허용) 한 후 다시 이용해 주십시오.\n\n만약 팝업 차단 기능을 해제하지 않으면\n정상적인 주문이 이루어지지 않습니다.");
			}
		} else {
	    	return;
		}
		
		if(win){    // 팝업창이 떠있다면 close();
	    	win.close();
		}
	}    // 함수 끝  

	window.onload = function(){      // 페이지 로딩 후 즉시 함수 실행(window.onload)
	    // openPopup()
	}
	
	window.fbAsyncInit = function() {
		FB.init({
			appId      : '<c:out escapeXml="false" value="${restApiKey}" />', // 내 앱 ID를 입력한다.
			cookie     : true,
			xfbml      : true,
			version    : 'v12.0',
			status	   : true
		});
		FB.AppEvents.logPageView();   
	};	
	
	$(document).ready(function() {
		console.log('Facebook SDK Initialize Start');
	});
	
	//기존 로그인 상태를 가져오기 위해 Facebook에 대한 호출
	function statusChangeCallback(response){
		statusChangeCallback(response);
	}
	
	function fnFacebookLogin() {
		// Android App 에서 이슈 해결을 위해 분기 처리
		var UserAgent = navigator.userAgent;
		var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		var isAndroidApp = UserAgent.match(/APTRACK_ANDROID/i) != null;
		if(isMobile && isAndroidApp) { // Android App 일 경우 로그인 팝업 호출 (User Agent에 APTRACK_ANDROID 포함인 경우)
			FB.login(function(response) {
				if (response.status === 'connected') {
					location.href='<c:out escapeXml="false" value="${callback}" />?accessToken=' + response.authResponse.accessToken;
				}
			}, {scope: 'public_profile', return_scopes: true});	
		} else {
			FB.getLoginStatus(function(response) {
				if (response.status === 'connected') {
					location.href='<c:out escapeXml="false" value="${callback}" />?accessToken=' + response.authResponse.accessToken;
				} else {
					location.href = encodeURI('https://www.facebook.com/dialog/oauth?client_id=<c:out escapeXml="false" value="${restApiKey}" />&redirect_uri=<c:out escapeXml="false" value="${callback}" />');
				}
			}, {scope: 'public_profile', return_scopes: true});							
		}		
	} 
	</script>
</head>
<body onload="fnFacebookLogin();">
<div id="fb-root"></div>
<script async defer crossorigin='anonymous' src='https://connect.facebook.net/ko_KR/sdk.js#xfbml=1&version=v12.0&appId=<c:out escapeXml="false" value="${restApiKey}" />&autoLogAppEvents=1' nonce='xbW0ijkt'></script>
</body>
</html>