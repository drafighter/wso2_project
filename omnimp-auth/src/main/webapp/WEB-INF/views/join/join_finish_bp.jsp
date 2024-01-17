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
<!DOCTYPE html><!-- ME-FO-A0102 ME-FO-A0218 finish bp -->
<html lang="ko">
<head>
  <title>아이디 등록 완료 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true" authCategory="true"/>
  <tagging:google/>
  <script type="text/javascript">
	$(document).ready(function() {
	  
		window.AP_SIGNUP_TYPE = '회원가입';
		dataLayer.push({event: 'signup_complete'});
		
		/* OMNI.popup.processEnd({
			id:'id-int-pop',
			userid:'<c:out value="${intguserid}"/>',
			oklabel:'취소',
			okclass:'btn_white',
			ok: function() {
				OMNI.popup.close({id:'id-int-pop'});
				$("#redirect-on").attr('disabled', true);
				location.href = OMNIEnv.ctx + '/join/move-on';
			},
			closelabel:'확인',
			closeclass:'btn_blue'
		}); */
		
		$('#redirect-on').on('click', function() {
			$("#redirect-on").attr('disabled', true);
			goAction();
		});
	});
	var goAction = function() {
		location.href = OMNIEnv.ctx + '/join/move-on';
	};
  </script>   
</head>
<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="뷰티포인트 통합 아이디 등록 완료" type="goaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>뷰티포인트 통합회원 아이디 등록이 완료 되었습니다.</h2>
        <p>뷰티포인트 통합회원 아이디로 아모레퍼시픽 브랜드의 온라인 서비스를 이용하실 수 있습니다.</p>
      </div>
      <div class="">
        <div class="user_info mb13">
          <dl class="dt_w33">
            <dt>이름</dt>
            <dd><c:out value="${name}" /></dd>
          </dl>
          <dl class="dt_w33">
            <dt>통합 아이디</dt>
            <dd><c:out value="${intguserid}" /></dd>
          </dl>
          <dl class="dt_w33">
            <dt>등록일자</dt>
            <dd><c:out value="${joindate}" /></dd>
          </dl>
        </div>
      </div>
      <div class="btn_submit">
      		<button type="button" class="btnA btn_blue" id='redirect-on' ap-click-area="회원 가입 완료" ap-click-name="회원 가입 완료 - 확인 버튼" ap-click-data="확인">확인</button>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>