<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title></title>
  <common:meta/>
  <common:css/>
  <common:js auth="false" popup="true"/>
  <script type="text/javascript">
  $(document).ready(function() {
	  <c:choose>
	  	<c:when test="${naverMembershipVo.resultCode ne '0000' and !empty naverMembershipVo.resultMessage}">
		OMNI.popup.open({
			id:'naver-membership-fail-waring',
			content:'<c:out value="${naverMembershipVo.resultMessage}" escapeXml="false"/>',
			closelabel:'확인',
			closeclass:'btn_blue',
			close:function() {
				OMNI.popup.close({ id: 'naver-membership-fail-waring' });
				location.href = '<c:out value="${naverMembershipVo.returnUrl}" escapeXml="false"/>';
			}
		});	  	
	  	</c:when>
	  	<c:otherwise>
	  		location.href = '<c:out value="${naverMembershipVo.returnUrl}" escapeXml="false"/>';
	  	</c:otherwise>
	  </c:choose>
  });
  
  </script>   
</head>

<body>
</body>

</html>