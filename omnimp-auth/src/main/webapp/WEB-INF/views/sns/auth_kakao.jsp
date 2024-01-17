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
	<title>카카오 계정 연동 | 옴니통합회원</title>
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
</head>
<body>
	<c:choose>
		<c:when test="${snsUseType eq 'usedSnsMapping'}">
			<c:if test="${snsMappingIsPopup eq 'true' || empty snsMappingIsPopup}">
				<common:header title="카카오 계정 연동" type="close"/>
			</c:if>
			<c:if test="${snsMappingIsPopup eq 'false'}">
				<common:header title="카카오 계정 연동" type="prvaction"/>
			</c:if>
		</c:when>
		<c:when test="${snsUseType eq 'usedSnsJoinOff'}">
	<common:header title="카카오 계정 연동" type="no"/>
		</c:when>
		<c:otherwise>
	<common:header title="카카오 계정 연동" type="prvaction"/>
		</c:otherwise>
	</c:choose>
	
	<section class="container" id="container" <c:if test="${kakaoEmbedded}">style="display: none;"</c:if>>
      <div class="page_top_area ver_kakao">
        <h2>카카오 계정 연동 안내</h2>
        <p>카카오 계정으로 간편하게 회원가입 하거나,<br/>사용 중이던 뷰티포인트 통합회원 계정과 연동하여<br/>로그인 할 수 있습니다.</p>
      </div>
      <div class="join_main">
      	<a href="javascript:;" id="kakaoAuth" class="btn_join_kakao" ap-click-area="카카오 계정 연동" ap-click-name="카카오 계정 연동 - 카카오 가입 버튼" ap-click-data="카카오 가입">카카오 계정 확인</a>
      </div>
    </section>
	
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
		
		var isEnableClick = false;
	
		$(document).ready(function() {
			if('<c:out escapeXml="false" value="${kakaoEmbedded}" />' == 'true') {
				if(Kakao.isInitialized()) {
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
					
					isEnableClick = false;
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
							$("#container").show();
						}
					});
					$('.layer_wrap').focus();
				}
			} else {
				$("#container").show();
			}
						
			$(function() {
				if(isEnableClick) {
					$('#kakaoAuth').css('backgroundColor', '#ffe500');
				} else {
					$('#kakaoAuth').css('backgroundColor', '#ced4da');	
				}
			});
			
			if(Kakao.isInitialized()) {
				isEnableClick = true;
			} else {				
				console.log('Kakao SDK Initialize Fail');
				console.log('isMobile? ', checkMobile());
				
				// 카카오 점검시 사용
				$('#kakaoAuth').on('click', function() {
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
				});
			}
			
			$('#kakaoAuth').on('click', function() {
				if(isEnableClick) {
					var isMobile  = checkMobile();
					
					if(isMobile) {
						Kakao.Auth.authorize({
							redirectUri: '<c:out escapeXml="false" value="${callback}" />',
							state : '<c:out escapeXml="false" value="${state}" />' + '&isMobile=true',	// 모바일에는 채널 + m 붙여서 간다
							serviceTerms: '<c:out escapeXml="false" value="${termTags}" />',
							channelPublicId: '<c:out escapeXml="false" value="${chPublicIds}" />' // TODO
							//throughTalk: false // 간편로그인 사용 플래그 (true 시 모바일에서 앱으로). 모바일에서 테스트 용도 
						})
					} else {
						Kakao.Auth.authorize({
							redirectUri: '<c:out escapeXml="false" value="${callback}" />',
							state : '<c:out escapeXml="false" value="${state}" />',
							serviceTerms: '<c:out escapeXml="false" value="${termTags}" />',
							channelPublicId: '<c:out escapeXml="false" value="${chPublicIds}" />'
						})
					}

					isEnableClick = false;	
				}
			});
			
			window.onpageshow = function(event) {
				if ( event.persisted || (window.performance && window.performance.navigation.type == 2)) {
					// Back Forward Cache로 브라우저가 로딩될 경우 혹은 브라우저 뒤로가기 했을 경우 카카오 SDK 초기화
					if(Kakao.isInitialized()) {
						isEnableClick = true;
					} else {				
						console.log('Kakao SDK Initialize Fail');
						console.log('isMobile? ', checkMobile());
						
						// 카카오 점검시 사용
						$('#kakaoAuth').on('click', function() {
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
						});
					}
		        }
			}
		});
		var prevAction = function() {
			//location.href = OMNIEnv.ctx + '/go-join-param'; // join back
			window.history.back();
		};
	</script>
</body>
</html>