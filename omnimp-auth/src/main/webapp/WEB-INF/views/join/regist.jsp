<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- regist -->
<html lang="ko">
<head>
  <title>회원가입 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true" authCategory="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	<c:if test="${not empty snsUsedType && snsUsedType eq 'usedSnsLogin'}">
		window.AP_SIGNUP_AUTH = 'SNS';
		dataLayer.push({event: 'signup_start'});
	</c:if>
	
	$('#do-cancel').on('click', function () {
		$("#do-cancel").attr('disabled', true);
		closeAction();
	});
	
	$('#dojoin').on('click', function () {
		$("#dojoin").attr('disabled', true);
		
		OMNI.loading.show('processing');
		
		$('#form-regist').submit();
	});
	
/* 	if(!navigator.cookieEnabled){ 							// 쿠키 차단되었을 경우 로그인 불가 처리
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
    }	 */
  });
  
	var disabledOnOff = function () {
		
		var condition = $("#loginid").val() !== "";
		condition &= $("#loginpassword").val() !== "";
		condition &= $("#loginconfirmpassword").val() !== "";
		condition &= $("#loginpassword").val() === $("#loginconfirmpassword").val();
		condition &= $(".is_error").length === 0;
		
		if (condition) {
			$("#dojoin").removeAttr("disabled");
		}
		else {
			$("#dojoin").attr("disabled", "disabled");
		}
	};
	  
	  var closeAction = function() {
			
		OMNI.popup.open({
			id:'next-warn',
			content: '회원가입을 멈추고,<br />서비스 화면으로 돌아가시겠습니까?<br />(지금까지 가입절차는 모두 취소됩니다.)',
			gaArea:'회원가입',
			gaOkName:'확인 버튼 (중단 팝업)',
			gaCancelName:'취소 버튼 (중단 팝업)',
			oklabel:'취소',
			okclass:'btn_white',
			ok: function() {
				$("#do-cancel").removeAttr("disabled");
				OMNI.popup.close({id:'next-warn'});
			},
			closelabel:'확인',
			closeclass:'btn_blue',
			close: function() {
				<c:if test="${category eq 'SNS'}">
					window.AP_SIGNUP_TYPE = '중단';
					dataLayer.push({event: 'signup_complete'});
					$.ajax({url:OMNIEnv.ctx + '/ga/tagging/join/stop/' + encodeURIComponent("통합 아이디"),type:'get'});
				</c:if>
				OMNI.popup.close({ id: 'next-warn' });
				
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
			}
		});
		$('.layer_wrap').focus();
	}; 	
	
	var executeAction = function() {
  		if(!$('#dojoin').is(':disabled')) {
			$('#dojoin').trigger('click');
		}
  	};
  	
    var disabledAction = function() {
    	$("#dojoin").attr('disabled', 'disabled');
    };
    
  </script>
  <script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-idpwd-check.js?ver=<c:out value="${rv}"/>"></script>
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="회원가입" gaArea="SNS 간편가입 아이디 등록" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>뷰티포인트 통합 아이디를 등록해주세요.</h2>
        <p>뷰티포인트 회원가입을 위하여 통합 아이디와 비밀번호를 등록해주세요.</p>
        <p>통합 아이디로 아모레퍼시픽의 모든 브랜드의 온/오프 매장 서비스를 이용하실 수 있습니다.</p>
      </div>
      <form method="post" id="form-regist" action="<c:out value='${ctx}'/>/join/regist">
      	<input type="hidden" name="chcd" value="<c:out value="${chCd}"/>" />
      	<input type="hidden" name="joinTo" value='<c:out value="${joinTo}" />'>
        <div class="input_form">
          <span class="inp" id="loginid-span">
            <input type="text" oninput="maxLengthCheck(this)" id="loginid" name="newId" class="inp_text" maxlength="12" placeholder="아이디 (영문 또는 숫자 4-12자)" ap-click-area="SNS 간편가입 아이디 등록" ap-click-name="SNS 간편가입 아이디 등록 - 아이디 입력란" ap-click-data="아이디 입력" title="아이디 입력"/>
            <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
          </span>
          <p id="loginid-guide-msg" class="form_guide_txt"></p>
        </div>
        <div class="input_form">
          <span class="inp" id="password-span">
            <input type="password" id="loginpassword" name="password" class="inp_text" maxlength="16" placeholder="비밀번호 (영문 소문자, 숫자, 특수문자 조합 8-16자)" ap-click-area="SNS 간편가입 아이디 등록" ap-click-name="SNS 간편가입 아이디 등록 - 비밀번호 입력란" ap-click-data="비밀번호 입력"  title="비밀번호 입력"/>
            <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
          </span>
          <p id="password-guide-msg" class="form_guide_txt"></p>
        </div>
        <div class="input_form">
          <span class="inp" id="confirmpassword-span">
            <input type="password" id="loginconfirmpassword" name="confirmPassword" class="inp_text" maxlength="16" placeholder="비밀번호 확인" ap-click-area="SNS 간편가입 아이디 등록" ap-click-name="SNS 간편가입 아이디 등록 - 비밀번호 확인 입력란" ap-click-data="비밀번호 확인 입력"  title="비밀번호 확인 입력"/>
            <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
          </span>
          <p id="confirm-password-guide-msg" class="form_guide_txt"></p>
          <common:password-notice gaArea="SNS 간편가입 아이디 등록"/>
        </div>
        <div class="btn_submit ver2">
          <button type="button" class="btnA btn_white" id="do-cancel" ap-click-area="SNS 간편가입 아이디 등록" ap-click-name="SNS 간편가입 아이디 등록 - 다음에 하기 버튼" ap-click-data="다음에 하기">다음에 하기</button>
          <button type="button" class="btnA btn_blue" id="dojoin" disabled ap-click-area="SNS 간편가입 아이디 등록" ap-click-name="SNS 간편가입 아이디 등록 - 등록하고 회원가입 버튼" ap-click-data="등록하고 회원가입">등록하고 회원가입</button>
        </div>
      </form>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>