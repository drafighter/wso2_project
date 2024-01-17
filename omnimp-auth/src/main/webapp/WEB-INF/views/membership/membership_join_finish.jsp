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
<title>회원가입 완료 | 옴니통합회원</title>
<common:meta/>
<common:css/>
<common:js auth="true" popup="false"/>
<tagging:google/>
<script type="text/javascript">
	$(document).ready(function() {
		$('#membership-join-finish').on('click', function() {
			location.href = OMNIEnv.ctx + '/membership/membership_start';
		});
	});

	var closeAction = function() {
		location.href = OMNIEnv.ctx + '/membership/membership_start';				
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
  	<common:header title="회원가입 완료" type="closeaction"/>
    <!-- container -->
    <section class="container">
		<div class="page_top_area">
        	<h2><c:out value="${name}"/>(<c:out value="${loginid}"/>) 님</h2>
        	<p>뷰티포인트 회원 가입이 완료되었습니다.</p>
      	</div>
      	<div class="img_conv">
        	<img src="<c:out value='${ctx}'/>/images/common/illust_07.png" alt="">
      	</div>
      	<div class="btn_submit">
        	<button type="button" class="btnA btn_blue" id='membership-join-finish'>확인</button>
      	</div>
    </section>
    <!-- //container -->
  	</div><!-- //wrap -->
</body>
</html>