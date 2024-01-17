<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html><!-- ME-FO-A0801 -->
<html lang="ko">
<head>
  <title>안내 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="false" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
	  $(document).ready(function() {
		  
		$('#btn_prev').on('click', function() {
			closeAction();
		});		
		
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
					location.href = '<c:out escapeXml="false" value="${home}"/>';
				} else {
					location.href = decodeURIComponent('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>');
				}				
			}
		  // location.href = '<c:out escapeXml="false" value="${home}" />';
	  };
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
        <h2>고객님 불편을 드려 죄송합니다.</h2>
        <p class="txt">방문 주소가 잘못 입력되었거나, 변경 혹은 삭제되어 <br />이용하실 수가 없습니다. 다시 한번 확인해주시거나, <br />잠시 후 이용해주시기 바랍니다.</p>
        <p><c:out value="${errormsg}"/> <c:out value="${errorcode}"/></p>
        <div class="btn_submit">
          <button type="button" class="btnA btn_blue" id='btn_prev' ap-click-area="페이지 에러" ap-click-name="페이지 에러 - 이전 화면으로 버튼" ap-click-data="이전 화면으로 이동">이전 화면으로</button>
        </div>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
    <form id='offForm' method='post'>
    	<input type='hidden' name='incsNo' value='<c:out value="${incsNo}" />'/>
    	<input type='hidden' name='chnCd' value='<c:out value="${chnCd}" />'/>
    	<input type='hidden' name='storeCd' value='<c:out value="${storeCd}" />'/>
    	<input type='hidden' name='storenm' value='<c:out value="${storenm}" />'/>
    	<input type='hidden' name='user_id' value='<c:out value="${user_id}" />'/>    	
    </form>  
</body>
<common:backblock block="true"/>
</html>