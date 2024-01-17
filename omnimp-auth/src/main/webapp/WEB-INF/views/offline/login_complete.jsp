<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- ME-FO-A0214 login new terms apply -->
<html lang="ko">
<head>
<title><c:out value="${chNm}"/> 매장에 오신 것을 환영합니다. | 옴니통합회원</title>
<common:meta/>
<common:css/>
<common:js auth="true" popup="true" authCategory="true"/>
<script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-offline-login.js"></script>
<tagging:google/>
<script type="text/javascript">
	$(document).ready(function() {
		$('#do-mobile-join').on('click', function() {
			location.href = OMNIEnv.ctx + '/offline/send/kakao?chCd=<c:out escapeXml="false" value="${chCd}"/>';
		});		

		$('#do-tablet-join').on('click', function() {
			location.href = OMNIEnv.ctx + '<c:out escapeXml="false" value='${joinUrl}'/>';
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
	    	<div class="offline_wrap">
	    		<h2><c:out value="${chNm}"/> 매장에 오신 것을 환영합니다.</h2>
	    		<p class="txt">뷰티포인트 X <c:out value="${chNm}"/><br>통합회원으로 가입 해보세요.</p>
	    	</div>
	    	<div class="sec_login">
				<div class="input_form mt70">
					<button type="button" id="do-mobile-join" class="btnA btn_white">모바일 회원가입</button>
				</div>
	          	<div class="btn_submit mt13">
	            	<button type="button" id="do-tablet-join" class="btnA btn_white">태블릿 회원가입</button>
	          	</div>				
          	</div>
		</section>
	    <!-- //container -->
	</div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>