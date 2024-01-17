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
<!DOCTYPE html><!-- ME-FO-A0102 join finish -->
<html lang="ko">
<head>
  <title>회원가입 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true" authCategory="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	  
	$('#redirect-on').on('click', function() {
		$("#redirect-on").attr('disabled', true);
		goAction();
	});
  });
  var goAction = function() {
	  
	  location.href = OMNIEnv.ctx + '/entry<c:out escapeXml="false" value="${entry}" />';
  };
  </script>   
</head>
<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="회원가입" type="no"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>뷰티포인트 회원 가입</h2>
        <p>뷰티포인트 통합회원으로 가입하여, 아모레퍼시픽의<br> 모든 브랜드 서비스를 이용해보세요.</p>
      </div>
      <div class="img_conv">
        <img src="<c:out value='${ctx}'/>/images/common/illust_02.png" alt="">
      </div>
      <div class="btn_submit">
      		<button type="button" class="btnA btn_blue" id='redirect-on'>통합회원 가입</button>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>