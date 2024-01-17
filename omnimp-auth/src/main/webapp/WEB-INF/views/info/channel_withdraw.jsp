<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html>
<!-- channel withdraw -->
<html lang="ko">
<head>
  <title>가입 제한 안내 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="false" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
	 $(document).ready(function() {
	  
		$('#go-main').on('click', function() {
			closeAction();
		});
		
	});
	
	var closeAction = function() {
		  <c:choose>
			<c:when test="${offline}">
			$('#offForm').attr('action', '<c:out escapeXml="false" value="${home}" />').submit();	
			</c:when>
			<c:otherwise>
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
			// location.href = '<c:out escapeXml="false" value="${homeurl}" />';
			</c:otherwise>
		</c:choose>	  	    
	};
  </script>
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
      <common:header title="가입 제한 안내" type="closeaction"/>
      <section class="container">
          <div class="page_top_area">
        	<h2>최근 <c:out escapeXml="false" value="${channelName}" /> 탈퇴 이력이 있습니다.</h2>
        	<p>탈퇴 후 30일이 지나면 다시 <c:out escapeXml="false" value="${channelName}" /> 회원가입이 가능합니다.</p>
          </div>
          <div class="sec_join">
              <div class="user_info">
                  <dl class="dt_w33">
                    <dt>탈퇴 일자</dt>
                    <dd><c:out escapeXml="false" value="${channelWtdt}" /></dd>
                  </dl>
              </div>    
          </div>
          <div class="btn_submit">
          	<button type="submit" class="btnA btn_blue" id='go-main' ap-click-area="가입 제한 안내" ap-click-name="가입 제한 안내 - 확인 버튼" ap-click-data="확인">확인</button>
          </div>
      </section>
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