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
	<script type="text/javascript" src="<c:out value='${ctx}'/>/js/lib/kakao-1.39.7.min.js"></script>
	<tagging:google/>
	<meta charset="UTF-8">
	<title>SNS 카카오 테스트 | 옴니통합회원</title>
	
	<script type="text/javascript">
		$(document).ready(function() {
			console.log('Kakao SDK Initialize Start');
			
			Kakao.init('<c:out escapeXml="false" value="${sdkKey}" />');
			if(!Kakao.isInitialized()) {
				alert('Kakao SDK Initialize Fail');
				return;
			}
			
			console.log('Kakao SDK Initialize Success');
			
			$('#kakaoAuth').on('click', function() {
				Kakao.Auth.authorize({
					redirectUri: '<c:out escapeXml="false" value="${callback}" />'
				})
			});	
		});
		
		displayToken();
		
		function displayToken() {
		  const token = getCookie('authorize-access-token')
		  if(token) {
		    Kakao.Auth.setAccessToken(token);
		    Kakao.Auth.getStatusInfo(({ status }) => {
		      if(status === 'connected') {
		        document.getElementById('token-result').innerText = 'login success. token = ' + Kakao.Auth.getAccessToken();
		      } else {
		        Kakao.Auth.setAccessToken(null);
		        document.getElementById('token-result').innerText = 'login Error = ' + status;
		      }
		    })
		  }
		  
		  document.getElementById('token-error').innerText = 'callback Error = ${error}';
		}
		
		function getCookie(name) {
		  const value = "; " + document.cookie;
		  const parts = value.split("; " + name + "=");
		  if (parts.length === 2) return parts.pop().split(";").shift();
		}
	</script>
</head>
<body>
<div>
	<h1>간편 로그인</h1>
	</div>
	<hr/>
	<div>
		<p>카카오 계정으로 간편하게 로그인 할 수 있습니다.</p>
		<p>카카오 로그인을 선택 해 주세요.</p>
	</div>
	<br/>
	<div>
		<button id="kakaoAuth">
			<image src="<c:out value='${ctx}'/>/images/common/btn_login_ka.png" alt="KAKAO"></image>
			<h2>카카오</h2>
		</button>
	</div>
	<div>
		<p id="token-result"></p>
		<p id="token-error"></p>
	</div>
</body>
</html>