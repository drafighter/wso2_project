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
  <title>통합회원 계정 본인 인증 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	$('#btn_prev').on('click', function() {
		if('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>' == null || '<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>' == "") {
			/* window.history.back(); */
			location.href = OMNIEnv.ctx + '/go-login';
		} else {
			location.href = decodeURIComponent('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>');
		}
	});
  });
  
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
  
  var closeAction = function() {
		var UserAgent = navigator.userAgent;
		var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		<c:if test="${mobile}">
			isMobile = true;
		</c:if>
		if(!isMobile && getParameterByName('popup') == 'true'){ 
			window.close();

		}else{
			if("${cancelUri}" != "" && "${cancelUri}" != null){
				location.href=decodeURIComponent("<c:out value='${sessionScope.cancelUri}'/>");
			}else{
				if(getParameterByName('cancelUri') == null || getParameterByName('cancelUri') == "") {
					if("<c:out value='${sessionScope.cancelUri}'/>" != null && "<c:out value='${sessionScope.cancelUri}'/>" != "") {
						location.href = decodeURIComponent("<c:out value='${sessionScope.cancelUri}'/>");
					} else {
						location.href = '${url}';	
					}
				} else {
					location.href = decodeURIComponent(getParameterByName('cancelUri'));
				}
			}
		}
  };
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
      	<h2>본인 인증 실패</h2>
      	<p class="txt">통합회원 계정 정보와 인증 정보가 일치하지 않습니다.<br>다시 확인해 주시기 바랍니다.</p>
      </div>
      <!-- <div class="btn_submit"> -->
      <div class="join_main">
          <button type="button" class="btnA btn_blue" id='btn_prev'>확인</button>
      </div>
      <div class="cs_cont">
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