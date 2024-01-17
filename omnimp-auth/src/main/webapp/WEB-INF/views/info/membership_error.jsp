<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<%@page import="com.amorepacific.oneap.common.util.StringUtil"%>
<%@page import="com.amorepacific.oneap.common.util.ConfigUtil"%>
<%@page import="com.amorepacific.oneap.common.util.WebUtil"%>
<%@page import="com.amorepacific.oneap.common.util.OmniUtil"%>
<%@page import="com.amorepacific.oneap.common.vo.OmniConstants"%>
<%@page import="org.springframework.util.StringUtils"%>
<%	// 경로구분코드가 이니스프리몰로 확인 (WSO2의 클라이언트ID로 식별), 
	// 이니스프리앱에서 호출된 경우 dt=A 파라미터 확인 
	// 앱 내 X 버튼의 히스토리백 기능 대체 필요
	// window.location = “innimemapp://go_back”
	boolean isInniMobileBackAction = OmniUtil.isInniMobileBackAction(request);
	boolean isBeautyAngelMobileBackAction = OmniUtil.isBeautyAngelMobileBackAction(request);
%>
<!DOCTYPE html><!-- ME-FO-A0801 -->
<html lang="ko">
<head>
  <title>뷰티포인트 멤버십 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="false" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
	$(document).ready(function() {
		$('#btn_prev').on('click', function() {
			if('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>' == null || '<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>' == "") {
				window.history.back();
			} else {
				location.href = decodeURIComponent('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>');
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
  	<common:header title="안내" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="error_wrap">
        <h2>잠시 후 다시 이용해주세요.</h2>
        <p class="txt">죄송합니다.<br />현재 뷰티포인트 멤버십 연결이 원할하지 않습니다.<br />불편하시겠지만 잠시 후 다시 이용 해주세요.</p>
        <p><c:out value="${message}"/></p>
        <div class="btn_submit">
          <button type="button" class="btnA btn_blue" id='btn_prev'>확인</button>
        </div>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>