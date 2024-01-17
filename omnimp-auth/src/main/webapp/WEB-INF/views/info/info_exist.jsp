<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html><!-- ME-FO-A0801 -->
<html lang="ko">
<head>
  <title>안내 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="false" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
	  $(document).ready(function() {
		  
		$('#btn_login').on('click', function() {
			location.href = OMNIEnv.ctx + '/redirect-authz?chCd=<c:out value="${chCd}" />';
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
      <div class="error_wrap">
        <h2>고객님은 이미 뷰티포인트에 가입되어 있습니다.</h2>
        <p class="txt">로그인하여 이용해주시기 바랍니다.</p>
        <div class="btn_submit">
          <button type="button" class="btnA btn_blue" id='btn_login' ap-click-area="페이지 에러" ap-click-name="페이지 에러 - 로그인 화면으로 버튼" ap-click-data="로그인 화면으로 이동">로그인 화면으로</button>
        </div>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>