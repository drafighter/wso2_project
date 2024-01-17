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
  <common:js auth="true" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
  	$(document).ready(function() {
		var UserAgent = navigator.userAgent;
		var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		var move_page_url = '';

		if("<c:out value='${restrict}'/>" == "true") {
			move_page_url = OMNIEnv.ctx + '/mgmt/restrict?restrict=Y';
			if (move_page_url.indexOf('?') > 0) {
				move_page_url += '&type=kmcis';
			} else {
				move_page_url += '?type=kmcis'
			}
			document.form_kmcis_cert.action = move_page_url;
		} else {
			if("<c:out value='${certiType}'/>" == "convs" || "<c:out value='${sessionScope.certiType}'/>" == "convs") {
				move_page_url = OMNIEnv.ctx + '/convs/check';
			} else if("<c:out value='${certiType}'/>" == "mbrs" || "<c:out value='${sessionScope.certiType}'/>" == "mbrs") {
				move_page_url = OMNIEnv.ctx + '/join/step';
			} else if("<c:out value='${certiType}'/>" == "spws" || "<c:out value='${sessionScope.certiType}'/>" == "spws") {
				move_page_url = OMNIEnv.ctx + '/mgmt/changepwd';
			}  else if("<c:out value='${certiType}'/>" == "lockcheck" || "<c:out value='${sessionScope.certiType}'/>" == "lockcheck") {
				move_page_url = OMNIEnv.ctx + '/lock_cert';
			} else {
				move_page_url = OMNIEnv.ctx + '/go-login';
			}
			
			if (move_page_url.indexOf('?') > 0) {
				move_page_url += '&type=kmcis';
			} else {
				move_page_url += '?type=kmcis'
			}
			
			document.form_kmcis_cert.action = move_page_url;
		}
  });
  </script>   
</head>
<body>
	<tagging:google noscript="true"/>
	<form name='form_kmcis_cert' id='form_ipin_cert' method='post'></form>
	
 	<script type="text/javascript">
  		$(document).ready(function() {
  			var UserAgent = navigator.userAgent;
  			var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
			setTimeout(function() {
				if(isMobile == "true") {
					document.form_kmcis_cert.submit();
				} else {
					document.form_kmcis_cert.target = window.opener != null ? window.opener.name : '';
					document.form_kmcis_cert.submit();
					self.close();
				}
			}, 500);
		});
 	</script> 	
</body>
</html>