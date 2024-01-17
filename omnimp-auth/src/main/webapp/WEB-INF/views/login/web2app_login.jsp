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
  <title>인증처리 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="false" popup="true"/>
  <script type="text/javascript">
  $(document).ready(function() {
  
	  OMNI.loading.show();
	  
	  if ($('#authKey').val() === '') {
		location.href = OMNIEnv.ctx + '/go-login';  
	  } else {
	 	$('#moveon').submit();
	  }

  });

  </script>   
</head>

<body>
	<form id='moveon' method='post' action='<c:out value="${actionurl}"/>'>
		<input type="hidden" name="authKey" value="<c:out value="${authKey}"/>">
		<input type='hidden' name='chkRemember' value='on'/>
		<input type='hidden' name='sessionDataKey' value='<c:out value="${sessionDataKey}" />'/>
	</form>
</body>

</html>