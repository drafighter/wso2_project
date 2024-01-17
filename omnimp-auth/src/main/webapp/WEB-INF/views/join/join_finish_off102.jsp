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
<!DOCTYPE html><!-- ME-FO-A0102 join finish offline 102 -->
<html lang="ko">
<head>
  <title>회원가입 완료 | 옴니통합회원</title>
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
  var goAction = function() {
	  $('#offForm')
		.attr('action', '<c:out escapeXml="false" value="${home}" />')
		.submit(); 
  };
  </script>   
</head>
<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="회원가입 완료" type="no"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2><c:out value="${name}"/> 님</h2>
        <p>회원 가입이 완료되었습니다.</p>
      </div>
      <div class="img_conv">
        <img src="<c:out value='${ctx}'/>/images/common/illust_02.png" alt="">
      </div>
      <div class="btn_submit">
      		<button type="button" class="btnA btn_blue" id='redirect-on' ap-click-area="회원 가입 완료" ap-click-name="회원 가입 완료 - 확인 버튼" ap-click-data="확인">확인</button>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->  
    <form id='offForm' method='post'>
    	<input type='hidden' name='incsNo' value='<c:out value="${incsNo}" />'/>
    	<input type='hidden' name='chnCd' value='<c:out value="${chnCd}" />'/>
    	<input type='hidden' name='storeCd' value='<c:out value="${storeCd}" />'/>
    	<input type='hidden' name='storenm' value='<c:out value="${storenm}" />'/>
    	<input type='hidden' name='user_id' value='<c:out value="${user_id}" />'/>
    </form>
</body>
<common:backblock block="true"/>
</html>