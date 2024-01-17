<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="otl" uri="/WEB-INF/tlds/oneap-taglibs.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html><!-- ME-FO-A0103 joined off -->
<html lang="ko">
<head>
  <title>가입된 회원 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="false" authCategory="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	  
	  window.AP_SIGNUP_TYPE = '기가입';
	  dataLayer.push({event: 'signup_complete'});	  	  
	  
	  $.ajax({url:OMNIEnv.ctx + '/ga/tagging/join/<c:out value="${join_case}" />',type:'get'});
	  
	  $('#do-login-offline').on('click', function() {
		  
		goAction();
		
	  });
  });
  
  var goAction = function() {
	  
	 $('#offForm').attr('action', '<c:out escapeXml="false" value="${loginurl}" />').submit();
  };
  </script>
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="가입된 회원" type="goaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
      	<c:choose>
      		<c:when test="${multiflag}">
      	<h2>본인인증 결과 이미 가입된 뷰티포인트 <br />회원정보가 확인 되었습니다.</h2>  
      		</c:when>
      		<c:otherwise>
      	<h2>회원님은 이미 아모레퍼시픽 <br>뷰티포인트 회원가입을 하셨습니다.</h2>
      		</c:otherwise>
      	</c:choose>
      </div>
      <c:if test="${not empty users}">
      <c:choose>
      	<c:when test="${multiflag}">
	      <c:forEach var="user" items="${users}" varStatus="index">
      <div class="user_info mb13">      
      	<c:if test="${not empty user.chcsNo}">
      	<h3><em class="tit tit_w33">아이디</em><c:out value="${user.chcsNo}"/></h3>
      	</c:if>
        <dl class="dt_w33">
          <dt>이름</dt>
          <dd><c:out value="${otl:nmm(user.custNm, locale)}"/></dd>
        </dl>
        <dl class="dt_w33">
          <dt>휴대폰 번호</dt>
          	<dd><c:out value="${otl:mmp(user, locale)}"/></dd>
        </dl>
        <dl class="dt_w33">
          <dt>가입일</dt>
          <dd><c:out value="${otl:bdt(user.mbrJoinDt)}"/></dd>
        </dl>
      </div>          
    	  </c:forEach>      	
      	</c:when>
      	<c:otherwise>
	      <c:forEach var="user1" items="${users}">
      <div class="user_info mb13">
      	<c:if test="${not empty user1.chcsNo}">   
      	<h3><em class="tit tit_w33">아이디</em><c:out value="${user1.chcsNo}"/></h3>
      	</c:if>
        <dl class="dt_w33">
          <dt>이름</dt>
          <dd><c:out value="${otl:nmm(user1.custNm, locale)}"/></dd>
        </dl>
        <dl class="dt_w33">
          <dt>휴대폰 번호</dt>
          <dd><c:out value="${otl:mmp(user1, locale)}"/></dd>
        </dl>
        <dl class="dt_w33">
          <dt>가입일</dt>
          <dd><c:out value="${otl:bdt(user1.mbrJoinDt)}"/></dd>
        </dl>
      </div>          
    	  </c:forEach>
      	</c:otherwise>
      </c:choose>
      </c:if>
      
      <!-- 로그인화면이동 A0200 -->
      <div class="btn_submit mt40">
      	<button type="button" class="btnA btn_blue" id='do-login-offline' ap-click-area="가입된 회원" ap-click-name="가입된 회원 - 로그인 화면 이동 (확인) 버튼" ap-click-data="로그인 화면 이동 (offline)">확인</button>
      </div>
      
      	<form id='termsForm' method='post' action=''>
      	    <input type='hidden' id='joinStepType' value='<c:out value="${joinStepType}" />'/>
      		<input type='hidden' id='incsNo' name='incsNo' value='<c:out escapeXml="false" value="${xincsno}" />'/>
      	</form>
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
      	<form id='loginForm' method='post' action=''>
      		<input type='hidden' id='uid' name='uid'/>
      	</form>      
</body>

</html>