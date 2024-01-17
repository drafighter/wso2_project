<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html><!-- ME-FO-A0500 -->
<html lang="ko">
<head>
  <title>고객센터 안내 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {

  });
  var closeAction = function() {
		var UserAgent = navigator.userAgent;
		var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		<c:if test="${mobile}">
			isMobile = true;
		</c:if>
		
		if(!isMobile && '<c:out escapeXml="false" value="${sessionScope.popup}"/>' == 'true') {
			window.close();
		} else {
			if('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>' == null || '<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>' == "") {
				location.href = '<c:out escapeXml="false" value="${homeurl}"/>';
			} else {
				location.href = decodeURIComponent('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>');
			}				
		}	  
	// location.href = '<c:out escapeXml="false" value="${homeurl}"/>';
  };
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="고객센터 안내" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
      	<c:if test="${not empty title}">
      	<h2>‘<c:out value="${title}"/>’</h2>
      	</c:if>
        <c:if test="${not empty msg}">
        <p>‘<c:out value="${msg}"/>’</p>
        </c:if>
      </div>
      <div class="cs_cont">
        <p class="txt">불편하시겠지만 고객센터로 연락바랍니다.</p>
        <a href="tel:0800235454" class="cs_phone" ap-click-area="고객센터 안내" ap-click-name="고객센터 안내 - 고객상담센터 통화 버튼" ap-click-data="고객상담센터 통화">
          <strong>080-023-5454 (고객상담센터)</strong>
          평일 09:00-18:00 ㅣ 점심: 12:00-13:00 <br />(주말/공휴일 휴무)
        </a>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>