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
  <title>회원탈퇴 완료 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	$('#go-main').on('click', function() {
		location.href = '<c:out escapeXml="false" value="${homeurl}" />';
	});
  });
  </script>
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="회원탈퇴 완료"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>회원탈퇴가 정상적으로 완료 되었습니다.</h2>
        <p>그동안 아모레퍼시픽을 이용해 주셔서 감사합니다. <br />(재가입 신청은 30일 이후에 가능합니다.)</p>
      </div>
      <div class="img_conv">
        <img src="<c:out value='${ctx}'/>/images/common/illust_03.png" alt="">
      </div>
      <div class="btn_submit">
        <button type="button" class="btnA btn_blue" id='go-main'>확인</button>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>