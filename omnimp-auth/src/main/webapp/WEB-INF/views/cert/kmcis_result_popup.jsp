<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>본인인증 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	var UserAgent = navigator.userAgent;
	var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
	<c:if test="${mobile}">
	isMobile = true;
	</c:if>
	var move_page_url = OMNIEnv.ctx + '/cert/kmcis-result';
	var certdata = '<c:out value="${certdata}"/>';
	var certnum = '<c:out value="${certnum}"/>';

	if (certdata === '' || certnum === '') {
		OMNI.popup.open({
			id:'kmcis-error',
			content: '인증에 실패하였습니다.',
			closelabel:'확인',
			closeclass:'btn_blue',
			close: function() {
				OMNI.popup.close({ id: 'kmcis-error' });
				document.form_kmcis_cert_popup.action = move_page_url;
				if (isMobile) {
					move_page_url = OMNIEnv.ctx + '/go-login';
					document.form_kmcis_cert_popup.submit();
				} else {
					document.form_kmcis_cert_popup.target = window.opener != null ? window.opener.name : '';
					document.form_kmcis_cert_popup.submit();
					self.close();
				}
			}
		});		
	} else {
		// 결과 페이지 경로 셋팅
		document.form_kmcis_cert_popup.action = move_page_url;
		<c:choose>
			<c:when test="${mobile}">
		document.form_kmcis_cert_popup.submit();
			</c:when>
			<c:otherwise>
		document.form_kmcis_cert_popup.target = window.opener != null ? window.opener.name : '';
		document.form_kmcis_cert_popup.submit();
		self.close();	
			</c:otherwise>
		</c:choose>
	}
		
  });
  </script>   
</head>
<body>
	<tagging:google noscript="true"/>
	<form name='form_kmcis_cert_popup' id='form_ipin_cert' method='post'>
		<input type="hidden" name="certdata" value="<c:out value='${certdata}'/>">
		<input type="hidden" name="certnum" value="<c:out value='${certnum}'/>">
	</form> 
</body>
</html>