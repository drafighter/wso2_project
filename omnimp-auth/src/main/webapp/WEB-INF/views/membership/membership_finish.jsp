<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- ME-FO-A0214 login new terms apply -->
<html lang="ko">
<head>
<title>뷰티포인트 멤버십 | 옴니통합회원</title>
<common:meta/>
<common:css/>
<common:js auth="true" popup="true" authCategory="true"/>
<tagging:google/>
<script type="text/javascript">
	$(document).ready(function() {
		$('#redirect-on').on('click', function() {
			if('<c:out escapeXml="false" value="${sessionScope.membershipAfterRedirectUrl}"/>' == null || '<c:out escapeXml="false" value="${sessionScope.membershipAfterRedirectUrl}"/>' == "") {
				location.href = decodeURIComponent('<c:out escapeXml="false" value="${membershipAfterRedirectUrl}"/>');
			} else {
				location.href = decodeURIComponent('<c:out escapeXml="false" value="${sessionScope.membershipAfterRedirectUrl}"/>');
			}
		});
  	});
	
	var closeAction = function() {
		var UserAgent = navigator.userAgent;
		var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		<c:if test="${mobile}">
			isMobile = true;
		</c:if>
		if("${cancelUri}" != "" && "${cancelUri}" != null){
			location.href=decodeURIComponent("<c:out value='${sessionScope.cancelUri}'/>");
		} else{
			if(getParameterByName('cancelUri') == null || getParameterByName('cancelUri') == "") {
				if("<c:out value='${sessionScope.cancelUri}'/>" != null && "<c:out value='${sessionScope.cancelUri}'/>" != "") {
					location.href = decodeURIComponent("<c:out value='${sessionScope.cancelUri}'/>");
				} else {
					window.history.back();
				}
			} else {
				location.href = decodeURIComponent(getParameterByName('cancelUri'));
			}
		}				
	};
	
	function getParameterByName(name, url) {
	    if (!url) {
	        url = window.location.href;
	    }
	    name = name.replace(/[\[\]]/g, '\\$&');
	    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
	    results = regex.exec(url);
	    if (!results) return null;
	    if (!results[2]) return "";
	    return decodeURIComponent(results[2].replace(/\+/g, ' '));
	}
</script>
</head>

<body>
	<tagging:google noscript="true"/>
	<!-- wrap -->
	<div id="wrap" class="wrap">
	  	<common:header title="뷰티포인트 멤버십" type="closeaction"/>
	    <!-- container -->
	    <section class="container">
			<div class="page_top_area">
				<h2>뷰티포인트 멤버십 연동 완료</h2>
				<p>아모레퍼시픽 뷰티포인트 멤버십 연동이 완료되었습니다.</p>
	      	</div>
		    <div class="img_conv">
		      <img src="<c:out value='${ctx}'/>/images/common/illust_07.png" alt="">
		    </div>
         		<div class="btn_submit">
           		<button type="button" class="btnA btn_blue" id='redirect-on'>확인</button>
         		</div>
		</section>
	    <!-- //container -->
	</div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>