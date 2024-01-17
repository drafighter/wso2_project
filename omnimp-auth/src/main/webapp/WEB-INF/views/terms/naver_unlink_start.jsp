<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- terms apply -->
<html lang="ko">
<head>
  <title>뷰티포인트 X 네이버 스마트 스토어 개인정보 동의철회 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true" authCategory="true"/>
  <tagging:google/>
  <script type="text/javascript">
	$(document).ready(function() {
		$('.btn_join_naver').on('click', function() {
			location.href = '<c:out escapeXml="false" value="${authUrl}" />';
		});		
	});
	var closeAction = function(e) {
		window.close();
	};	
  </script>
</head>
<body>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2 style="text-align: center;">뷰티포인트 X 네이버 스마트 스토어<br/>개인정보 동의철회</h2>
        <p>네이버 스마트 스토어에서 뷰티포인트 멤버십을 연결한 고객이 연결을 해제하거나 동의한 내역을 확인하고 철회 할 수 있습니다.</p>
      </div>
      <div class="sec_join">
			<div class="join_main">
				<a href="javascript:;" class="btn_join_naver"
						ap-click-area="통합회원 가입 수단 선택"
						ap-click-name="통합회원 가입 수단 선택 - 네이버 가입 버튼"
						ap-click-data="네이버 계정 로그인">네이버 계정 로그인</a>
			</div>
      </div>
    </section>
  </div>
</body>
<common:backblock block="true"/>
</html>