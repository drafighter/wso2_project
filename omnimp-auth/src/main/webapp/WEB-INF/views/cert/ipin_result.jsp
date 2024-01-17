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
  <common:js auth="false" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
		var UserAgent = navigator.userAgent;
		var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		<c:if test="${mobile}">
		isMobile = true;
		</c:if>
		var move_page_url = '';
	  <c:choose>
		<c:when test="${restrict}">
		//parent.opener.parent.OMNI.auth.restrict();
		
		move_page_url = OMNIEnv.ctx + '/mgmt/restrict?restrict=Y';
		if (move_page_url.indexOf('?') > 0) {
			move_page_url += '&type=ipin';
		} else {
			move_page_url += '?type=ipin'
		}
	  
		</c:when>
		<c:otherwise>
		//parent.opener.parent.OMNI.auth.ipincert();
		
		<c:choose>
			<c:when test="${certiType eq 'convs'}">
			move_page_url = OMNIEnv.ctx + '/convs/check';			
			</c:when>
			<c:when test="${certiType eq 'mbrs'}">
			move_page_url = OMNIEnv.ctx + '/join/step';			
			</c:when>			
			<c:when test="${certiType eq 'spws'}">
			move_page_url = OMNIEnv.ctx + '/mgmt/changepwd'; // ?p=' + encodeURIComponent($('#p').val());		
			</c:when>
			<c:when test="${certiType eq 'lockcheck'}">
			move_page_url = OMNIEnv.ctx + '/lock_cert'; // ?p=' + encodeURIComponent($('#p').val());		
			</c:when>
			<c:otherwise>
			</c:otherwise>
		</c:choose>
		if (move_page_url === '') {
			
			if (isMobile) {
				move_page_url = OMNIEnv.ctx + '/go-login';
			} else {
				self.close();	
			}
			
		}		 
		if (move_page_url.indexOf('?') > 0) {
			move_page_url += '&type=ipin';
		} else {
			move_page_url += '?type=ipin'
		}

		</c:otherwise>
	</c:choose>
		// 결과 페이지 경로 셋팅
		document.form_ipin_cert.action = move_page_url;
		<c:choose>
			<c:when test="${mobile}">
		document.form_ipin_cert.submit();
			</c:when>
			<c:otherwise>
		document.form_ipin_cert.target = window.opener != null ? window.opener.name : '';
		document.form_ipin_cert.submit();
		self.close();	
			</c:otherwise>
		</c:choose>
  });
  </script>   
</head>
<body>
	<form name='form_ipin_cert' id='form_ipin_cert' method='post'></form> 	
</body>
</html>