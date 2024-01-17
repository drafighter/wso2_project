<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>본인인증 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true" swipe="true"/>
  <common:backblock block="false"/>  
  <tagging:google/>
</head>
<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="본인인증" gaArea="통합회원 전환 인증 수단 선택" type="closeaction"/>
  <form name='form_ipin_cert' id='form_ipin_cert' method='post'>
  	<input type='hidden' name='m' value='pubmain'><!-- 필수 데이타로, 누락하시면 안됩니다. -->
  	<input type="hidden" name="enc_data" value='<c:out escapeXml="false" value="${certdata.ipinData.encData}" />'><!-- 업체정보를 암호화 한 데이타입니다. -->
  	<input type="hidden" name="param_r1" value='<c:out escapeXml="false" value="${certdata.ipinData.login.chCd}" />'><!-- 인증결과 응답시 해당 값을 그대로 송신 -->
  	<input type="hidden" name="param_r2" value='<c:out escapeXml="false" value="${certdata.ipinData.cpRequest}" />'><!-- 인증결과 응답시 해당 값을 그대로 송신 -->
  	<input type="hidden" name="param_r3" value=""><!-- 인증결과 응답시 해당 값을 그대로 송신 -->
  </form> 
  <form name='form_kmcis_cert' id='form_kmcis_cert' method='post'>
	<input type='hidden' name='tr_cert' value='<c:out escapeXml="false" value="${certdata.kmcisData.certData}" />'>
    <input type='hidden' name='tr_url' value='<c:out escapeXml="false" value="${certdata.kmcisData.resultUrl}" />'><!-- 본인인증서비스 결과수신 POPUP URL -->
    <input type='hidden' name='tr_add' value="N"><!-- IFrame사용여부 -->	  
  </form>
  <c:if test="${manualcert}">
  <form name='form_manual_cert' id='form_manual_cert' method='post'></form>
  </c:if>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>뷰티포인트 통합회원 전환을 위한 본인인증 입니다.</h2>
      </div>
      <div class="join_main">
        <strong>본인 확인을 위한 인증 방식을 선택해주세요.</strong>
        <div class="btn_joins bdb_n">
          <a href="javascript:;" rel="opener" id='phone-cert' ap-click-area="통합회원 전환 인증 수단 선택" ap-click-name="통합회원 전환 인증 수단 선택 - 통합회원 전환 인증 수단 선택 휴대폰 인증 버튼" ap-click-data="휴대폰 인증" title="새창열림">
            <i class="ico i_phone"></i>휴대폰 인증
          </a>
          <a href="javascript:;" rel="opener" id='ipin-cert' ap-click-area="통합회원 전환 인증 수단 선택" ap-click-name="통합회원 전환 인증 수단 선택 - 통합회원 전환 인증 수단 선택 아이핀 인증 버튼" ap-click-data="아이핀 인증" title="새창열림">
            <i class="ico i_ipin"></i>아이핀 인증
          </a>
        </div>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
  
	<script type="text/javascript">
		$(document).ready(function() {
	
			$('#ipin-cert').on('click', function() {
				var UserAgent = navigator.userAgent;
				var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		<c:if test='${mobile}'>
				isMobile = true;
		</c:if>
		<c:choose>
			<c:when test='${not empty certdata.ipinData.encData}'>
				if (isMobile) {
					document.form_ipin_cert.target = '';
				} else {
					var h = 550;
					var w = 450;
					var xPos = (document.body.offsetWidth/2) - (w/2); // 가운데 정렬
					xPos += window.screenLeft; // 듀얼 모니터일 때
					var yPos = (document.body.offsetHeight/2) - (h/2);
					window.name ="Parent_ipin_window";
					var objpop = window.open('', 'popupIPIN2', 'width=' + w + ', height=' + h + ', top=' + yPos + ', left=' + xPos + ', fullscreen=no, menubar=no, status=no, toolbar=no, titlebar=yes, location=no, scrollbar=no, rel=opener');
					if(objpop) {
						document.form_ipin_cert.target = "popupIPIN2";
					}				
				}
				document.form_ipin_cert.action = "<c:out value="${certdata.ipinData.certUrl}"/>";
				document.form_ipin_cert.submit();
			</c:when>
			<c:otherwise>
				OMNI.popup.open({id:'ipin-warning', closelabel: "닫기", closeclass:'btn_blue',content:'아이핀 인증을 사용할 수 없습니다.'});
			</c:otherwise>
		</c:choose>
			});
	
			$('#phone-cert').on('click', function() {
				var UserAgent = navigator.userAgent;
				var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		<c:if test="${mobile}">
				isMobile = true;
		</c:if>
		<c:choose>
		<c:when test="${not empty certdata.kmcisData.certData}">	
				if (isMobile) {
					document.form_kmcis_cert.target = '';
				} else {
					var h = 550;
					var w = 450;
					var xPos = (document.body.offsetWidth/2) - (w/2); // 가운데 정렬
					xPos += window.screenLeft; // 듀얼 모니터일 때
					var yPos = (document.body.offsetHeight/2) - (h/2);
					window.name ="Parent_kmcis_window";
					var objpop = window.open('', 'popupKMCIS2', 'width=' + w + ', height=' + h + ', top=' + yPos + ', left=' + xPos + ', fullscreen=no, menubar=no, status=no, toolbar=no, titlebar=yes, location=no, scrollbar=no, rel=opener');
					if(objpop) {
						document.form_kmcis_cert.target = "popupKMCIS2";
					}			
				}
				document.form_kmcis_cert.action = "<c:out value="${certdata.kmcisData.certUrl}"/>";
				document.form_kmcis_cert.submit();
		</c:when>
		<c:otherwise>
				OMNI.popup.open({id:'kmcis-warning', closelabel: "닫기", closeclass:'btn_blue',content:'휴대폰 인증을 사용할 수 없습니다.'});
		</c:otherwise>
		</c:choose>		
			});
	
	<c:if test="${manualcert}">
	<c:choose>
		<c:when test="${mobile}">
			$(document).on('swiped-up', function(e) {		
		</c:when>
		<c:otherwise>
			$(document).key('ctrl+shift+z', function(e) {		
		</c:otherwise>
	</c:choose>
				var UserAgent = navigator.userAgent;
				var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		<c:if test="${mobile}">
				isMobile = true;
		</c:if>
				if (isMobile) {
					document.form_manual_cert.target = '';
				} else {
					var h = 780;
					var w = 480;
					var xPos = (document.body.offsetWidth/2) - (w/2); // 가운데 정렬
					xPos += window.screenLeft; // 듀얼 모니터일 때
					var yPos = (document.body.offsetHeight/2) - (h/2);
					window.name ="Parent_manual_window";
					var objpop = window.open('', 'popupManual', 'width=' + w + ', height=' + h + ', top=' + yPos + ', left=' + xPos + ', fullscreen=no, menubar=no, status=no, toolbar=no, titlebar=yes, location=no, scrollbar=no, resizable=no, rel=opener');
					if(objpop) {
						document.form_manual_cert.target = "popupManual";
					}			
				}
				document.form_manual_cert.action = OMNIEnv.ctx + "/cert/manual-cert";
				document.form_manual_cert.submit();
			});
	</c:if>
	
		});
  
		var closeAction = function() {
			OMNI.popup.open({
				id:'next-warn',
				content: '통합회원 전환을 멈추고, 서비스<br/>화면으로 돌아가시겠습니까?<br/>(현재 아이디는 로그아웃됩니다.)',
				gaArea:'본인인증',
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
					
					if(!isMobile && '<c:out escapeXml="false" value="${wso2.popup}"/>' == 'true') {
						window.close();
					} else {
						if('<c:out escapeXml="false" value="${wso2.cancelUri}"/>' == null || '<c:out escapeXml="false" value="${wso2.cancelUri}"/>' == "") {
							if("<c:out value='${sessionScope.cancelUri}'/>" != null && "<c:out value='${sessionScope.cancelUri}'/>" != "") {
								location.href = decodeURIComponent("<c:out value='${sessionScope.cancelUri}'/>");
							} else {
								location.href = '<c:out escapeXml="false" value="${homeurl}"/>';	
							}
						} else {
							location.href = decodeURIComponent('<c:out escapeXml="false" value="${wso2.cancelUri}"/>');
						}				
					}
					// location.href = '<c:out escapeXml="false" value="${homeurl}" />';
				}
			});
			$('.layer_wrap').focus();
		};
  
	</script>     
</body>

</html>