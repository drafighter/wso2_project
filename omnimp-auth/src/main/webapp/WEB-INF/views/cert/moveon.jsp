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
	  
		<c:if test="${authFailure eq 'true'}">
		OMNI.popup.open({
			id:'login-fail-waring',
			content:'<c:out value="${authFailureMsg}"/>',
			closelabel:'확인',
			closeclass:'btn_blue',
			close:function() {
				OMNI.popup.close({ id: 'login-fail-waring' });
				location.href = OMNIEnv.ctx + '/reauthz';
			}
		});
		$('.layer_wrap').focus();
		</c:if>	  
	  OMNI.loading.show();
	  
	  if ($('#username').val() === '' || $('#password').val() === '') {
		location.href = OMNIEnv.ctx + '/go-login';  
	  } else {
		setOneApMoveChannel();
	 	$('#moveon').submit();
	  }

  });
  
  var setOneApMoveChannel = function() {
	  var date = new Date();
	  date.setTime(date.getTime() + 24*60*60*1000);
	  document.cookie = 'one-ap-move-channel=true;expires=' + date.toUTCString() + ';path=/';
  };
  </script>   
</head>

<body>
	<form id='moveon' method='post' action='<c:out value="${actionurl}"/>'>
	<c:if test="${!offline}">
		<c:if test="${isEncryption}">
			<input type='hidden' id='username' name='username' value='<c:out value="${xid}"/>'/>
			<input type='hidden' id='password' name='password' value='<c:out value="${xpw}"/>'/>
			<input type='hidden' id='isEncryption' name='isEncryption' value='<c:out value="${isEncryption}"/>'/>
		</c:if>
		<c:if test="${!isEncryption}">
			<input type='hidden' id='username' name='username' value='<c:out value="${xid}"/>'/>
			<input type='hidden' id='password' name='password' value='<c:out value="${xpw}"/>'/>
		</c:if>
		<c:if test="${autologin}">
		<input type='hidden' name='chkRemember' value='on'/>
		</c:if>
		<input type="hidden" name="sessionDataKey" value="<c:out value="${sessionDataKey}"/>">
	</c:if>	
	</form>
</body>

</html>