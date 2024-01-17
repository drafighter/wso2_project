<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- ME-FO-A0102 apply finish offline -->
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
	  
		$('#redirect-on').on('click', function() {
			$("#redirect-on").attr('disabled', true);
			goAction();
		});

	});
  
  // 버튼 선택 시 오프라인 가입 최초 화면으로 이동 채널에 따라 지정된 랜딩 화면 분기 처리
  var goAction = function() {
	  $('#offForm')
		.attr('action', '<c:out escapeXml="false" value="${homeurl}" />')
		.submit(); 
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
       	<button type="button" class="btnA btn_blue" id='go-main' ap-click-area="약관동의 완료" ap-click-name="약관동의 완료 - 확인 버튼" ap-click-data="확인">확인</button>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
    <form id='offForm' method='post'>
    	<input type='hidden' name='incsNo' value='<c:out escapeXml="false" value="${incsNo}" />'/>
    	<input type='hidden' name='chnCd' value='<c:out escapeXml="false" value="${chnCd}" />'/>
    	<input type='hidden' name='storeCd' value='<c:out escapeXml="false" value="${storeCd}" />'/>
    	<input type='hidden' name='storenm' value='<c:out escapeXml="false" value="${storenm}" />'/>
    	<input type='hidden' name='user_id' value='<c:out escapeXml="false" value="${user_id}" />'/>
    </form>
<common:backblock block="true"/>    
</html>