<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- ME-FO-A0212 -->
<html lang="ko">
<head>
  <title>비밀번호 변경 캠페인 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	
	$('#do-change').on('click', function() {
		
		 $('#cpForm').attr('action', OMNIEnv.ctx + '/mgmt/info').submit();	
		
	});
	
	$('#do-next').on('click', function() {

		$('#cpForm').attr('action', OMNIEnv.ctx + '/mgmt/pwdstatus').submit();
		 
	});
	
  });
  var goAction = function() {
	  $('#nextForm').attr('action', OMNIEnv.ctx + '/mgmt/pwdstatus-next').submit();
  };
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="비밀번호 변경 캠페인" type="goaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>회원님, 주기적인 비밀번호 변경으로 소중한 개인정보를 지켜주세요.</h2>
        <p>회원님은 3개월 동안 비밀번호를 변경하지 않으셨습니다. 안전한 정보보호를 위해 비밀번호 변경을 권장 드립니다.</p>
      </div>
      <div class="img_conv">
        <img src="<c:out value='${ctx}'/>/images/common/illust_04.png" alt="">
      </div>
      <div class="btn_submit">
        <button type="button" class="btnA btn_white" id='do-next' ap-click-area="비밀번호 변경 캠페인" ap-click-name="비밀번호 변경 캠페인 - 3개월 뒤 변경 버튼" ap-click-data="3개월 뒤 변경">3개월 뒤 변경</button>
        <button type="button" class="btnA btn_blue" id='do-change' ap-click-area="비밀번호 변경 캠페인" ap-click-name="비밀번호 변경 캠페인 - 비밀번호 변경 버튼" ap-click-data="비밀번호 변경">비밀번호 변경</button>
      </div>
  	  <form id='cpForm' method='post' action=''>
  		<input type='hidden' id='xid' name='xid' value='<c:out escapeXml="false" value="${xid}" />'/>
  		<input type='hidden' id='xpw' name='xpw' value='<c:out escapeXml="false" value="${xpw}" />'/>
  		<input type='hidden' id='xincsno' name='xincsno' value='<c:out escapeXml="false" value="${xincsno}" />'/>
  		<input type='hidden' id='sessionDataKey' name='sessionDataKey' value='<c:out escapeXml="false" value="${sessionDataKey}" />'/>
  	  </form>
  	  <form id='nextForm' method='post' action=''>
  		<input type='hidden' id='xid' name='xid' value='<c:out escapeXml="false" value="${xid}" />'/>
  		<input type='hidden' id='xpw' name='xpw' value='<c:out escapeXml="false" value="${xpw}" />'/>
  		<input type='hidden' id='xincsno' name='xincsno' value='<c:out escapeXml="false" value="${xincsno}" />'/>
  		<input type='hidden' id='sessionDataKey' name='sessionDataKey' value='<c:out escapeXml="false" value="${sessionDataKey}" />'/>  	  
  	  </form>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>