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
<!DOCTYPE html><!-- ME-FO-A0205 finish -->
<html lang="ko">
<head>
  	<c:choose>
      <c:when test="${isTransform}">
    <title>뷰티포인트 통합회원 전환 완료 | 옴니통합회원</title>
      </c:when>
      <c:when test="${isIntegrated}">
    <title>뷰티포인트 회원 정보 통합 완료 | 옴니통합회원</title>
      </c:when>
      <c:otherwise>
	<title>뷰티포인트 회원 정보 통합 완료 | 옴니통합회원</title>     
      </c:otherwise>
    </c:choose>  
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {

	window.AP_SIGNUP_TYPE = '회원가입';
	dataLayer.push({event: 'signup_complete'});	  
	  
	$('#convs-finish').on('click', function() {
		location.href = OMNIEnv.ctx + '/go-login';	
	});
	
	
  });
  var goAction = function() {

	  location.href = OMNIEnv.ctx + '/go-login';
	  
  };
  </script>
</head>

<body>
  <tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<c:choose>
      <c:when test="${isTransform}">
    <common:header title="뷰티포인트 통합회원 전환 완료" type="goaction" gaArea="통합회원 전환 완료 안내" gaName="로그인 화면 이동"/>
      </c:when>
      <c:when test="${isIntegrated}">
    <common:header title="뷰티포인트 회원 정보 통합 완료" type="goaction"/>
      </c:when>
      <c:otherwise>
	<common:header title="뷰티포인트 회원 정보 통합 완료" type="goaction"/>
      </c:otherwise>
    </c:choose>
    
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
      <c:choose>
      	<c:when test="${isTransform}">
      	<h2>뷰티포인트 통합회원으로 전환되었습니다.</h2>
        <p><c:out value="${channelName}" /> 아이디(<c:out value="${previd}"/>)가 뷰티포인트 통합 아이디로 전환되었습니다.</p>
      	</c:when>
      	<c:when test="${isIntegrated}">
      	<h2>뷰티포인트 회원 정보가 통합 되었습니다.</h2>
        <p><c:out value="${channelName}" /> 아이디(<c:out value="${previd}"/>)가 뷰티포인트 통합 아이디 (<c:out value="${newid}" />)로 통합되었습니다.</p>
      	</c:when>
      	<c:otherwise>
      	<h2>뷰티포인트 회원 정보가 통합 되었습니다.</h2>
        <p><c:out value="${channelName}" /> 아이디(<c:out value="${previd}"/>)가 뷰티포인트 통합 아이디 (<c:out value="${newid}" />)로 통합되었습니다.</p>
      	</c:otherwise>
      </c:choose>
      </div>
      <div class="user_info">
      	<c:choose>
	      <c:when test="${isTransform}">
	    <h3><em class="tit tit_w27">아이디</em> <c:out value="${newid}" /></h3>
	      </c:when>
	      <c:when test="${isIntegrated}">
	    <h3><em class="tit tit_w27">통합 아이디</em> <c:out value="${newid}" /></h3>
	      </c:when>
	      <c:otherwise>
	    <h3><em class="tit tit_w27">통합 아이디</em> <c:out value="${newid}" /></h3>
	      </c:otherwise>
	    </c:choose>
        <dl class="dt_w27">
          <dt>이름</dt>
          <dd><c:out value="${name}" /></dd>
        </dl>
        <dl class="dt_w27">
  	<c:choose>
      <c:when test="${isTransform}">
			<dt>전환일자</dt>
      </c:when>
      <c:when test="${isIntegrated}">
			<dt>통합일자</dt>
      </c:when>
      <c:otherwise>
      		<dt>통합일자</dt>
      </c:otherwise>
    </c:choose>        
          <dd><c:out value="${processedDate}" /></dd>
        </dl>
      </div>
      <c:choose>
      	<c:when test="${isTransform}">
      	<p class="txt_c">* 뷰티포인트 통합 아이디와 비밀번호로만 로그인 가능합니다.</p>
      	</c:when>
      	<c:when test="${isIntegrated}">
      	<p class="txt_l">* 뷰티포인트 통합 아이디와 비밀번호로만 로그인 가능하며, 서비스 이용을 위해 다시 한번 로그인 해주세요.</p>
      	</c:when>
      	<c:otherwise>
      	<p class="txt_l">* 뷰티포인트 통합 아이디와 비밀번호로만 로그인 가능하며, 서비스 이용을 위해 다시 한번 로그인 해주세요.</p>
      	</c:otherwise>
      </c:choose>
      
      <div class="btn_submit">
        <button type="button" class="btnA btn_blue" ap-click-area="통합회원 전환 완료 안내" ap-click-name="통합회원 전환 완료 안내 - 로그인 하기 버튼 (로그인 화면 이동)" ap-click-data="로그인 하기" id='convs-finish'>로그인 하기</button>
      </div>
      <p class="txt_c">로그인 화면으로 이동합니다.</p>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
  <form id='authForm' method='post' action=''></form>
</body>
<common:backblock block="true"/>
</html>