<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- conversion -->
<html lang="ko">
<head>
  <title>뷰티포인트 통합회원 전환 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {

	$('#do-next').on('click', function() {
		
		closeAction();
		
	});
	
	$('#do-cert').on('click', function() {
		location.href = OMNIEnv.ctx + '/cert';
	});
	
  });
  var closeAction = function() {
		OMNI.popup.open({
			id:'next-warn',
			content: '통합회원 전환을 멈추고, 서비스<br/>화면으로 돌아가시겠습니까?<br/>(현재 아이디는 로그아웃됩니다.)',
			gaArea:'통합회원 전환 안내',
			gaOkName:'확인 버튼 (중단 팝업)',
			gaCancelName:'취소 버튼 (중단 팝업)',		
			oklabel:'취소',
			okclass:'btn_white',
			ok: function() {
				OMNI.popup.close({id:'next-warn'});
			},
			closelabel:'확인',
			closeclass:'btn_blue',
			close: function() {
				window.AP_SIGNUP_TYPE = '중단';
				dataLayer.push({event: 'signup_complete'});	
				OMNI.popup.close({ id: 'next-warn' });
				$.ajax({url:OMNIEnv.ctx + '/ga/tagging/join/stop/' + encodeURIComponent("경로전환"),type:'get'});

				
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
			}
			
		});	  
		$('.layer_wrap').focus();
  };
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="뷰티포인트 통합회원 전환" gaArea="통합회원 전환 안내" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2><c:out value="${nameid}" /> 님</h2>
        <p class="fs14">온라인 서비스 이용을 위해서 로그인한 <c:out value="${channelName}" /> 아이디를 뷰티포인트 통합회원으로 전환합니다.</p>
        <p class="fs14">통합회원 전환 후 아모레퍼시픽 모든 브랜드의 온/오프 매장 서비스를 이용하실 수 있습니다.</p>
      </div>
      <div class="img_conv">
        <img src="<c:out value='${ctx}'/>/images/common/illust_01.png" alt="">
      </div>
      <div class="btn_submit ver2">
        <button type="button" class="btnA btn_white" ap-click-area="통합회원 전환 안내" ap-click-name="통합회원 전환 안내 - 다음에 하기 버튼" ap-click-data="다음에 하기" id='do-next'>다음에 하기</button>
        <button type="button" class="btnA btn_blue" ap-click-area="통합회원 전환 안내" ap-click-name="통합회원 전환 안내 - 통합회원 전환 버튼" ap-click-data="통합회원 전환" id='do-cert'>통합회원 전환</button>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>