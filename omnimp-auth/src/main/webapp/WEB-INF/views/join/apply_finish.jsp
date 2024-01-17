<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>약관동의 완료 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true" authCategory="true"/>
  <tagging:google/>
  <script type="text/javascript">
	$(document).ready(function() {
		window.AP_SIGNUP_TYPE = '회원가입';
		dataLayer.push({event: 'signup_complete'});
		$('#go-main').on('click', function() {
			$("#go-main").attr('disabled', true);
			goAction();
		});
	});
	
	var goAction = function() {
		<c:choose>
			<c:when test="${mlogin eq 'MOBILE'}">
			location.href = OMNIEnv.ctx + '/join/mobile-move-on';
			</c:when>
			<c:otherwise>
			location.href = OMNIEnv.ctx + '/join/move-on';
			</c:otherwise>
		</c:choose>
	 };
  </script>
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="약관동의 완료" type="goaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2><c:out value="${nameid}"/> 님</h2>
        <p>지금부터 뷰티포인트 통합 아이디로 <c:out value="${channelName}" /> 서비스를 이용하실 수 있습니다.</p>
      </div>
      <div class="img_conv">
        <img src="<c:out value='${ctx}'/>/images/common/illust_02.png" alt="">
      </div>
      <div class="btn_submit">
      	<c:if test="${!isConfirmBtn}">
        	<button type="button" class="btnA btn_blue" id='go-main' ap-click-area="약관동의 완료" ap-click-name="약관동의 완료 - 메인으로 이동 버튼" ap-click-data="메인으로 이동">메인으로 이동</button>
        </c:if>
        <c:if test="${isConfirmBtn}">
        	<button type="button" class="btnA btn_blue" id='go-main' ap-click-area="약관동의 완료" ap-click-name="약관동의 완료 - 확인 버튼" ap-click-data="확인">확인</button>
        </c:if>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>