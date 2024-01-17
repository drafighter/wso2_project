<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="block" type="java.lang.Boolean"%>
<c:if test="${empty block}" >
	<c:set var="block" value="true" />
</c:if>
<!--<c:if test="${block}"> -->
<script type="text/javascript">
$(document).ready(function() {
	window.onpageshow = function(event) {
		if ( event.persisted || (window.performance && window.performance.navigation.type == 2)) {
			// Back Forward Cache로 브라우저가 로딩될 경우 혹은 브라우저 뒤로가기 했을 경우
			var chCd = "";
			if('<c:out escapeXml="false" value="${sessionScope.chCd}"/>' != null && '<c:out escapeXml="false" value="${sessionScope.chCd}"/>' != '') {
				chCd = '<c:out escapeXml="false" value="${sessionScope.chCd}"/>';
			} else if(getParameterByName("chCd") != null && getParameterByName("chCd") != '') {
				chCd = getParameterByName("chCd");
			} else if(getParameterByName("channelCd") != null && getParameterByName("channelCd") != '') {
				chCd = getParameterByName("channelCd");
			}
			if(getOneApMoveChannel() == 'true' && chCd != '031') { // APMall 에서는 History Forward 하지 않음
				history.forward();
			}
        }
	}	
});

function getOneApMoveChannel() {
	  const value = "; " + document.cookie;
	  const parts = value.split("; one-ap-move-channel=");
	  if (parts.length === 2) return parts.pop().split(";").shift();
}
//$(document).ready(function() {
	//history.pushState(null, null, location.href); //  history back 막기
	//window.history.back();
	//window.history.forward();
	//window.onpopstate = function () {
	//	history.go(1);
	//};
//});
//history.pushState(null, null, location.href); //  history back 막기
//window.history.back();
//window.history.forward();
//window.onpopstate = function () { history.go(1); };
//function disableBack() { window.history.forward(); }
//setTimeout(function() { disableBack();}, 0);
//window.onload = function() { disableBack(); }
//window.onunload = function(){ null };
//window.onpageshow = function(evt) { if (evt.persisted) disableBack() }
</script>
<!--</c:if>-->