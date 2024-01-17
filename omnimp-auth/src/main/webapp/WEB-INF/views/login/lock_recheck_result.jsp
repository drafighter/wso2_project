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
<title>통합회원 계정 본인 인증 | 옴니통합회원</title>
<common:meta/>
<common:css/>
<common:js auth="true" popup="true" authCategory="true"/>
<tagging:google/>
<script type="text/javascript">
	$(document).ready(function() {
		$('#btn_prev').on('click', function() {
			if('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>' == null || '<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>' == "") {
				/* window.history.back(); */
				<c:choose>
				<c:when test="${mlogin eq 'MOBILE'}">
				location.href = OMNIEnv.ctx + '/join/mobile-move-on';
				</c:when>
				<c:otherwise>
				location.href = OMNIEnv.ctx + '/join/move-on';
				</c:otherwise>
			</c:choose>
			} else {
				location.href = decodeURIComponent('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>');
			}
		});
		
		$('#relogin').on('click', function() {
			<c:choose>
				<c:when test="${mlogin eq 'MOBILE'}">
				location.href = OMNIEnv.ctx + '/join/mobile-move-on';
				</c:when>
				<c:otherwise>
				location.href = OMNIEnv.ctx + '/join/move-on';
				</c:otherwise>
			</c:choose>
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
	  	<common:header title="통합회원 계정 본인 인증" type="closeaction"/>
	    <!-- container -->
	    <section class="container">
			<div class="page_top_area">
				<h2>본인 인증 완료</h2>
				<p>통합회원 계정에 대한 본인 인증이 완료되었습니다.<br>이후 본 계정으로 정상 로그인이 가능합니다.</p>
	      	</div>
			<div>
	        	<div class="user_info">
		          <h3><em class="tit tit_w27">아이디</em> <c:out value="${id}" /></h3>
		          <dl class="dt_w27">
		            <dt>이름</dt>
		            <dd> <c:out value="${name}" /></dd>
		          </dl>
		        </div>
          		<div class="btn_submit">
            		<button type="button" class="btnA btn_blue" id='relogin'>확인</button>
          		</div>
	      	</div>
		</section>
	    <!-- //container -->
	</div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>