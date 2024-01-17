<%@page import="com.amorepacific.oneap.common.util.WebUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
	<common:meta/>
  	<common:css/>
	<common:js auth="true" popup="true"/>
	<tagging:google/>
	<meta charset="UTF-8">
	<title>SNS 카카오 | 옴니통합회원</title>
	<style>
		div {
			text-align: center;
		}
		
		button {
			background-color: #ffffff;
    		border-color: #ffffff; /* #fdff9d; */
    	    border-style: solid;
		}
	</style>
	
	<script src="https://developers.kakao.com/sdk/js/kakao.js"></script>
	<script type="text/javascript">
		Kakao.init('<c:out escapeXml="false" value="${sdkKey}" />');
		
		var checkMobile = function() {
			var UserAgent = navigator.userAgent;
			if (UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null 
					|| UserAgent.match(/LG|SAMSUNG|Samsung/) != null) {
				return true;
			}
	
			return false;
		}
	
		$(document).ready(function() {
			var kakaoInit = Kakao.isInitialized();
			
			setTimeout(function() {
				if(kakaoInit) {
					console.log('isMobile? ', checkMobile());
					var isMobile  = checkMobile();
					if(isMobile) {
						Kakao.Auth.authorize({
							redirectUri: '<c:out escapeXml="false" value="${callback}" />',
							state : '<c:out escapeXml="false" value="${state}" />' + '&isMobile=true',	// 모바일에는 채널 + m 붙여서 간다
							serviceTerms: '<c:out escapeXml="false" value="${termTags}" />',
							channelPublicId: '<c:out escapeXml="false" value="${chPublicIds}" />' // TODO
							//throughTalk: false // 간편로그인 사용 플래그 (true 시 모바일에서 앱으로). 모바일에서 테스트 용도 
						});
					} else {
						Kakao.Auth.authorize({
							redirectUri: '<c:out escapeXml="false" value="${callback}" />',
							state : '<c:out escapeXml="false" value="${state}" />',
							serviceTerms: '<c:out escapeXml="false" value="${termTags}" />',
							channelPublicId: '<c:out escapeXml="false" value="${chPublicIds}" />'
						});
					}
				} else {
					console.log('Kakao SDK Initialize Fail');
					console.log('isMobile? ', checkMobile());
					
					OMNI.popup.open({
						id:'kakao-notice',
						content: '카카오 간편 로그인 서비스 점검 중 입니다.<br/>불편하시더라도 다른 로그인이나<br/>회원가입 수단을 이용해주세요.',
						closelabel:'확인',
						closeclass:'btn_blue',
						close: function() {
							OMNI.popup.close({id:'kakao-notice'});
							window.history.back();
						}
					});
					$('.layer_wrap').focus();
				}
			}, 1000);
		});
	</script>	
</head>
</html>