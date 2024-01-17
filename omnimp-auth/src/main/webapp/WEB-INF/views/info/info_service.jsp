<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html><!-- ME-FO-A0802 -->
<html lang="ko">
<head>
  <title>안내 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="false" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
	  $(document).ready(function() {
		  
		$('#btn_prev').on('click', function() {
			window.history.back();	
		});
		
	  });
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="안내" type="close"/>
    <!-- container -->
    <section class="container">
      <div class="error_wrap error_02">
        <h2>서비스 점검 안내</h2>
        <p class="txt">고객님께 더 나은 서비스를 제공하기 위하여 <br />현재 서비스 점검을 진행 중 입니다. <br />불편하시더라도 조금만 양해 부탁드립니다.</p>
 		<!-- <p>*서비스 점검 시간 (hh:mm~hh:mm)</p> -->
        <div class="btn_submit">
          <button type="button" class="btnA btn_blue" id='btn_prev' ap-click-area="서비스 점검 안내" ap-click-name="서비스 점검 안내 - 이전 화면으로 버튼" ap-click-data="이전 화면으로 이동">이전 화면으로</button>
        </div>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>