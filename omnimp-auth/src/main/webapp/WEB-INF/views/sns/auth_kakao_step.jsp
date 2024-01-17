<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<common:meta/>
	<common:js auth="true" popup="true"/>
	<meta charset="UTF-8">
	<title>SNS 카카오 | 옴니통합회원</title>
	<script src="https://developers.kakao.com/sdk/js/kakao.js"></script>
	<script type="text/javascript">	
	Kakao.init('<c:out escapeXml="false" value="${sdkKey}" />');
	
	doCommonAuth();
	
	function doCommonAuth() {
		const token = getCookie('authorize-access-token')
		Kakao.Auth.setAccessToken(token);
		Kakao.Auth.getStatusInfo(({ status }) => {
			if(status === 'connected') {
				var data = {
						token: Kakao.Auth.getAccessToken()
					};
				
				$.ajax({
					url:OMNIEnv.ctx + '/sns/dokakaoaction',
					type:'post',
					data:JSON.stringify(data),
					dataType:'json',
					contentType : 'application/json; charset=utf-8',
					success: function(data) {
						if (data.resultCode === '0000') {
							location.href = data.message;
						} 
					},
					error: function() {
					}
				});
			} else {
				alert("KAKAO NOT AUTH");
			}
		})
	}
	
	function getCookie(name) {
		const value = "; " + document.cookie;
		const parts = value.split("; " + name + "=");
		if (parts.length === 2) return parts.pop().split(";").shift();
	}
	
	</script>
	
</head>
<body>
</body>
</html>