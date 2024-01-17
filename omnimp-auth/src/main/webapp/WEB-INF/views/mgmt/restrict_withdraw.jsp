<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html>
<!-- restict withdraw -->
<html lang="ko">
<head>
  <title>가입 제한 안내 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="false" authCategory="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	<c:if test="${empty snsUseType || snsUseType ne 'usedSnsLogin'}">
	  window.AP_SIGNUP_TYPE = '중단';
	  dataLayer.push({event: 'signup_complete'});
	  $.ajax({url:OMNIEnv.ctx + '/ga/tagging/join/stop/' + encodeURIComponent("경로전환"),type:'get'});
	</c:if>
	  
	$('#go-home').on('click', function() {
		//location.href = OMNIEnv.ctx + '/go-join';
		goAction();
	});
  });
  var goAction = function() {
	  <c:choose>
		<c:when test="${offline}">
		$('#offForm').attr('action', '<c:out escapeXml="false" value="${home}" />').submit();	
		</c:when>
		<c:otherwise>
		location.href = '<c:out escapeXml="false" value="${homeurl}" />';
		</c:otherwise>
	</c:choose>	  
  };
  </script>
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="가입 제한 안내" type="goaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>회원님은 탈퇴 후 30일이 경과되지 않으셨습니다.</h2>
        <p>탈퇴 후 30일이 지나면 다시 회원가입이 가능합니다.</p>
      </div>
    <c:if test="${not empty withdrawDate}">
      <div class="user_info">
        <strong class="st_txt"><span>탈퇴일자</span> <c:out value="${withdrawDate}" /></strong>
      </div>
    </c:if>
      <div class="btn_submit mt40">
        <button type="button" class="btnA btn_blue" id='go-home' ap-click-area="가입 제한 안내" ap-click-name="가입 제한 안내 - 확인 버튼 (가입 제한 탈퇴 30일 미만)" ap-click-data="확인 버튼 (가입 제한 탈퇴 30일 미만)">확인</button>
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