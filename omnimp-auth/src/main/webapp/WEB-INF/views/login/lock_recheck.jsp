<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging"%>
<spring:eval expression="@environment.getProperty('spring.profiles.active')" var="profile" />
<!DOCTYPE html>
<!-- member -->
<html lang="ko">
<head>
<title>통합회원 계정 본인 인증 | 옴니통합회원</title>
<common:meta />
<common:css />
<common:js auth="true" popup="true" swipe="true" authCategory="true"/>
<common:backblock block="false" />
<tagging:google />
</head>
<body>
	<tagging:google noscript="true" />
	<!-- wrap -->
	<div id="wrap" class="wrap">
		<common:header title="통합회원 계정 본인 인증" gaArea="통합회원 계정 본인 인증 수단 선택" type="prvclose" />
		<form name='form_ipin_cert' id='form_ipin_cert' method='post'>
			<input type='hidden' name='m' value='pubmain'>
			<% 	// 필수 데이타로, 누락하시면 안됩니다. 	%>
			<input type="hidden" name="enc_data"
				value="<c:out escapeXml="false" value="${certdata.ipinData.encData}" />">
			<% 	// 업체정보를 암호화 한 데이타입니다. 	%>
			<input type="hidden" name="param_r1"
				value="<c:out value="${certdata.ipinData.login.chCd}" />">
			<% 	// 인증결과 응답시 해당 값을 그대로 송신 %>
			<input type="hidden" name="param_r2"
				value="<c:out value="${certdata.ipinData.cpRequest}" />">
			<% // 인증결과 응답시 해당 값을 그대로 송신 %>
			<input type="hidden" name="param_r3" value="">
			<% // 인증결과 응답시 해당 값을 그대로 송신 %>
		</form>
		<form name='form_kmcis_cert' id='form_kmcis_cert' method='post'>
			<input type='hidden' name='tr_cert'
				value="<c:out escapeXml="false" value="${certdata.kmcisData.certData}" />">
			<input type='hidden' name='tr_url'
				value="<c:out value="${certdata.kmcisData.resultUrl}" />">
			<% // 본인인증서비스 결과수신 POPUP URL %>
			<input type='hidden' name='tr_add' value="N">
			<% // IFrame사용여부 %>
		</form>
		<form id='offForm' method='post'>
			<input type='hidden' name='incsNo'
				value='<c:out value="${incsNo}" />' /> <input type='hidden'
				name='chnCd' value='<c:out value="${chnCd}" />' /> <input
				type='hidden' name='storeCd' value='<c:out value="${storeCd}" />' />
			<input type='hidden' name='storenm'
				value='<c:out value="${storenm}" />' /> <input type='hidden'
				name='user_id' value='<c:out value="${user_id}" />' />
		</form>
		<c:if test="${manualcert}">
			<form name='form_manual_cert' id='form_manual_cert' method='post'></form>
		</c:if>
		<!-- container -->
		<section class="container">
			<div class="page_top_area">
				<h2><c:out value="${name}"/>(<c:out value="${loginid}"/>) 님</h2>
				<p>
					로그인을 위해서 뷰티포인트 통합회원 계정에 대한 본인 인증이 필요합니다.
				</p>
			</div>
			<div class="join_main">
				<strong>인증 방식을 선택해주세요.</strong>
				<div class="btn_joins">
					<a href="javascript:;" rel="opener" id='phone-cert'
						ap-click-area="통합회원 가입 수단 선택"
						ap-click-name="통합회원 가입 수단 선택 - 휴대폰 인증 버튼" ap-click-data="휴대폰 인증" title="새창열림">
						<i class="ico i_phone"></i>휴대폰 인증
					</a> <a href="javascript:;" rel="opener" id='ipin-cert'
						ap-click-area="통합회원 가입 수단 선택"
						ap-click-name="통합회원 가입 수단 선택 - 아이핀 인증 버튼" ap-click-data="아이핀 인증" title="새창열림">
						<i class="ico i_ipin"></i>아이핀 인증
					</a>
				</div>
				<div class="login_opt">
		            <button type="button" id="recheckcancel" class="btnA btn_white" ap-click-area="로그인" ap-click-name="로그인 - 로그인 버튼" ap-click-data="로그인">인증 취소</button>
		         </div>
		          <p class="txt_c">인증 취소 시 로그아웃 됩니다.</p>
			</div>
		</section>
		<!-- //container -->
	</div>
	<!-- //wrap -->
	<script type="text/javascript">
		$(document).ready(function() {
	
			<c:if test="${offline and profile ne 'prod'}">
			    // 로컬, 개발 환경에서 오프라인을 통한 접근 시 1분뒤 페이지 리다이렉트
				setTimeout(function() {
					console.log('<c:out escapeXml="false" value="${offline}"/>');
					$('#offForm').attr('action', '<c:out escapeXml="false" value="${home}" />').submit();
				}, 60000);
			</c:if>
			<c:if test="${offline and profile eq 'prod'}">
			// 운영 환경에서 오프라인을 통한 접근 시 5분뒤 페이지 리다이렉트
				setTimeout(function() {
					console.log('<c:out escapeXml="false" value="${offline}"/>');
					$('#offForm').attr('action', '<c:out escapeXml="false" value="${home}" />').submit();
				}, 300000);
			</c:if>			
			
			var UserAgent = navigator.userAgent;
			var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
			<c:if test="${mobile}">
				isMobile = true;
			</c:if>
			
			if(isMobile && '<c:out escapeXml="false" value="${sessionScope.popup}"/>' == 'true') {
				opener.location.href = window.location.href;
				window.close();
			}			
			
			$('#ipin-cert').on('click', function() {
				var UserAgent = navigator.userAgent;
				var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		<c:if test="${mobile}">
				isMobile = true;
		</c:if>
		<c:choose>
			<c:when test="${not empty certdata.ipinData.encData}">
				window.AP_SIGNUP_AUTH = '아이핀';
				dataLayer.push({event: 'signup_start'});
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
				$('.layer_wrap').focus();
			</c:otherwise>
		</c:choose>
			});
			$('#recheckcancel').on('click', function(){
				prevAction();
			})
			$('#phone-cert').on('click', function() {
				var UserAgent = navigator.userAgent;
				var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		<c:choose>
		<c:when test="${not empty certdata.kmcisData.certData}">
				window.AP_SIGNUP_AUTH = '휴대폰';
				dataLayer.push({event: 'signup_start'});		
				if (isMobile) {
					document.form_kmcis_cert.target = '';
				} else {
					var h = 550;
					var w = 480;
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
				$('.layer_wrap').focus();
		</c:otherwise>
		</c:choose>		
			});
			
			/* if(!navigator.cookieEnabled){ 													// 쿠키 차단되었을 경우 로그인 불가 처리
				$('#phone-cert').attr('disabled', 'disabled')								// 휴대폰 인증 버튼 Disabled;
				.addClass('is_disabled').off('click');
				$('#ipin-cert').attr('disabled', 'disabled')								// 휴대폰 인증 버튼 Disabled
				.addClass('is_disabled').off('click'); 		
				$('.btn_join_kakao').attr('disabled', 'disabled')							// 카카오 인증 버튼 Disabled
				.removeClass('btn_join_kakao').addClass('btn_join_kakao_disabled').removeAttr('href');					
				OMNI.popup.open({
					id: "cookie_disabled",
					closelabel: "확인",
					closeclass:'btn_blue',
					content: "서비스 이용을 위하여 웹/모바일 환경설정의<br/>'쿠키차단'을 '허용'으로 변경 해주시기<br/>바랍니다.<br/>고객상담센터 : 080-023-5454"
				});
		    } */
	
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
	
	<c:if test="${param.error eq 'norequired'}">
	// [취소] 버튼 선택시 팝업 닫히고 현재 화면 유지
			window.AP_SIGNUP_TYPE = '중단';
			dataLayer.push({event: 'signup_complete'});	
			$.ajax({url:OMNIEnv.ctx + '/ga/tagging/join/stop/' + encodeURIComponent("경로전환"),type:'get'});

			OMNI.popup.open({
				id: "pop_no_required_kakao",
				closelabel: "확인",
				closeclass:'btn_blue',
				content: "회원가입이 불가능한 계정입니다.<br/>카카오 계정 정보를 확인해 주시거나,<br/>뷰티포인트 통합회원 가입을 통해<br/>가입해주세요."
			});
			$('.layer_wrap').focus();
	</c:if>	
	
			var wso2data = {channelCd:'<c:out escapeXml="false" value="${wso2.channelCd}"/>',client_id:'<c:out escapeXml="false" value="${wso2.client_id}"/>',redirectUri:'<c:out escapeXml="false" value="${wso2.redirectUri}"/>',cancelUri:'<c:out escapeXml="false" value="${wso2.cancelUri}"/>',redirect_uri:'<c:out escapeXml="false" value="${wso2.redirect_uri}"/>',response_type:'<c:out escapeXml="false" value="${wso2.response_type}"/>',scope:'<c:out escapeXml="false" value="${wso2.scope}"/>',state:'<c:out escapeXml="false" value="${wso2.state}"/>',type:'<c:out escapeXml="false" value="${wso2.type}"/>',join:'<c:out escapeXml="false" value="${wso2.join}"/>',vt:'<c:out escapeXml="false" value="${wso2.vt}"/>',popup:'<c:out escapeXml="false" value="${wso2.popup}"/>'};
			OMNI.auth.setWso2AuthData('<c:out escapeXml="false" value="${wso2.channelCd}"/>', wso2data);
			$('.btn_join_kakao').on('click', function() {
				window.AP_SIGNUP_AUTH = 'SNS';
				dataLayer.push({event: 'signup_start'});
				/* var url = OMNIEnv.ctx + "/sns/auth/kakao";
        		window.open(url, "_blank","left=0, top=0, toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=yes, width=400, height=800"); */
				
        		var urlparameter= location.search;
        		document.location = OMNIEnv.ctx + '/sns/kakaosync_join'+urlparameter;
        		//location.replace(OMNIEnv.ctx + '/sns/kakaosync_join');
			});				
		});
			
		var prevAction = function() {
			<c:choose>
			<c:when test="${innimobileAction}">
			window.location = "innimemapp://go_back";			
			</c:when>
			<c:when test="${beautyAngelmobileAction}">
			window.location = "toapp://go_back";
			</c:when>
			<c:when test="${isMembership}">
			window.history.back();
			</c:when>
			<c:otherwise>
			var UserAgent = navigator.userAgent;
			var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
 			var params = { certiType:'spws' };
			$.ajax({
				url:OMNIEnv.ctx + '/cert/cert-type',
				type:'post',
				data:JSON.stringify(params),
				dataType:'json',
				global:false,
				contentType : 'application/json; charset=utf-8',
				success: function(data) {
					window.location = OMNIEnv.ctx + '/go-login';
				},
				error: function() {
					window.location = OMNIEnv.ctx + '/go-login';
				}
			});		
			</c:otherwise>
		</c:choose>			
		};
		var closeAction = function() {
			/* 
			 var undefined;
			 window.AP_SIGNUP_AUTH = undefined;
			 window.AP_SIGNUP_TYPE = '중단';
			 dataLayer.push({event: 'signup_complete'});
			*/
			//location.href = OMNIEnv.ctx + "/go-login";
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
					location.href = '<c:out escapeXml="false" value="${home}"/>';
				} else {
					location.href = decodeURIComponent('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>');
				}				
			}
		   </c:otherwise>
		 </c:choose>
		};
		
		window.onload = function () {
		  $.ajax({url:'https://tagmanager.amorepacific.com/currentip',type:'get',success:function(_data){
				   if(_data){	
					if(_data.ip) {  
					  //  console.log(_data.ip);
						$.ajax({url:OMNIEnv.ctx + '/ga/tagging/cookie?uip=' + _data.ip,type:'get'});	
					}
				   }
		   }});	
	  }		
	</script>
</body>
</html>