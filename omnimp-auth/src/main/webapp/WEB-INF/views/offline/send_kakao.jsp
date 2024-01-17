<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- ME-FO-A0214 login new terms apply -->
<html lang="ko">
<head>
<title>뷰티포인트 X <c:out value="${chNm}"/> | 옴니통합회원</title>
<common:meta/>
<common:css/>
<common:js auth="true" popup="true" authCategory="true"/>
<script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-offline-login.js"></script>
<tagging:google/>
<script type="text/javascript">
	$(document).ready(function() {
		// 발송 버튼 클릭
		$('#sendkakao').on('click', function() {
			var params = {
				userPhone: $('#phone').val()
			};
			// 카카오 알림톡 발송
			$.ajax({
				url:OMNIEnv.ctx + '/offline/send/kakao',
				type:'post',
				data:JSON.stringify(params),
				dataType:'json',
				contentType : 'application/json; charset=utf-8',
				success: function(data) {
					console.log('sendkakao ', JSON.stringify(data, null, 2));
					if (data.resultCode === '0000') { // 발송 성공
						OMNI.popup.open({
							id:'kakao-send-success',
							content: '입력하신 휴대전화 번호로 가입메세지를 발송 했습니다.',
							closelabel:'확인',
							closeclass:'btn_blue',
							close: function() {
								OMNI.popup.close({ id: 'kakao-send-success' });
								$('#loginForm').attr('action', OMNIEnv.ctx + '/offline/login/step').submit();
							}							
						});
					} else if (data.resultCode === '0100') { // 세션 만료
						OMNI.popup.open({
							id:'kakao-send-waring',
							content: '오프라인 매장 로그인 정보가 만료되었습니다. 다시 시도해주세요.',
							closelabel:'확인',
							closeclass:'btn_blue',
							close: function() {
								OMNI.popup.close({ id: 'kakao-send-waring' });
								location.href = OMNIEnv.ctx + '/offline/login?chCd=<c:out escapeXml="false" value="${chCd}"/>';
							}
						});
					} else { // 발송 실패
						OMNI.popup.open({
							id:'kakao-send-waring',
							content: '가입 메세지 전송에 실패 했습니다.<br/>다시 시도 해주세요.',
							closelabel:'확인',
							closeclass:'btn_blue',
							close: function() {
								OMNI.popup.close({ id: 'kakao-send-waring' });
								$('#loginForm').attr('action', OMNIEnv.ctx + '/offline/login/step').submit();
							}
						});
					}
					$('.layer_wrap').focus();
				},
				error: function() {
					OMNI.popup.error();
					$('.layer_wrap').focus();
				}
				
			});
		});
		
		// 휴대폰 key in 
		$('#phone').on('touchend, keyup', function(e) {
		    var key = e.which || e.keyCode;
		    if (key === KeyCode.LEFT || key === KeyCode.RIGHT || key === KeyCode.DELETE) {
		    	return;
		    } else {
		    	e.target.value = e.target.value.replace(/[^0-9]/g,'');
		    }
		    
		    updateUI();
		    disabledOnOff();
		});
		
		$('#i_send_kakao_agree').change(function() {
			disabledOnOff();
		});
		
	  	var disabledOnOff = function() {
	  		var checkedSendKakaoCheckBox = $('#i_send_kakao_agree').is(':checked');
			var phone = $('#phone').val();
			var phoneexp = /^01(?:0|1|[6-9])(?:\d{3}|\d{3,4})\d{3,4}$/;
	  		
		    if (checkedSendKakaoCheckBox && phone.length > 9 && (phoneexp.test(phone))) {
		    	$('#sendkakao').attr('disabled', false);
		    } else {
		    	$('#sendkakao').attr('disabled', true);
		    }
		    var keycode = e.keyCode || e.which;
		    if (!$('#sendkakao').is(':disabled')) {
		    	if (keycode === KeyCode.ENTER) {
		    		$('#sendkakao').trigger('click');
		    	}
		    }
	  	}
	  	
	  	var updateUI = function() {
	  		var phone = $('#phone').val();
			var phoneexp = /^01(?:0|1|[6-9])(?:\d{3}|\d{3,4})\d{3,4}$/;
		    if (phone.length > 9 && (phoneexp.test(phone))) {
		    	$("#phonenumber-guide-msg").hide();
		    } else {
		    	$("#phonenumber-guide-msg").show();
		    }
	  	};	  	
	});

</script>
</head>

<body>
	<tagging:google noscript="true"/>
	<!-- wrap -->
	<div id="wrap" class="wrap">
		<form id='loginForm' method='post' action=''>
			<input type="hidden" id="chCd" name="chCd" value="<c:out value='${chCd}'/>">
		</form>
		<common:header title="" type="prv"/>
	    <!-- container -->
	    <section class="container">
	    	<div class="page_top_area">
	    		<h2 style="text-align: center;">뷰티포인트 X <c:out value="${chNm}"/></h2>
	    		<p style="text-align: center;">고객님의 휴대전화 번호로<br>통합회원 가입 메세지를 발송합니다.</p>
	    	</div>
	    	<div class="sec_login">
				<div class="input_form">
					<span class="inp">
					    <input type="tel" id="phone" autocomplete="off" class="inp_text" maxlength="11" placeholder="휴대폰 번호 입력 (‘-’ 생략)" ap-click-area="휴대폰 로그인" ap-click-name="휴대폰 로그인 - 휴대폰 번호 입력란" ap-click-data="휴대폰 번호 입력"  title="휴대폰 번호 입력"/>
					    <button type="button" class="btn_del"><span class="blind">삭제</span></button>
					    <input type='hidden' id='smsSeq'>
					</span>
					<p id="phonenumber-guide-msg" class="form_guide_txt is_error" style="display: none;">휴대전화 번호를 확인해 주세요</p>
				</div>
          	</div>
			<div class="offline_area">
	            <div class="box box3">
            		<ul>
	              		<li>입력하신 휴대전화 번호로 발송되는 링크를 선택하여 회원 가입을 완료한 후  뷰티포인트의 다양한 혜택을 누려보세요.</li>
	              		<li>아모레퍼시픽의 전자상거래에 의하여, 만 14세 미만의 어린이/학생의 회원가입을 제한합니다.</li>
	              		<li>휴대폰을 이용한 본인인증 방법은 모두 무료이며, 한국모바일인증에서 제공합니다.</li>
	            	</ul>
	            </div>
				<div class="agree_title">
					<ul>
						<li>
		         			<h3>개인정보 수집 및 이용 동의</h3>					
						</li>
					</ul>	         			
         		</div>
         		<div class="user_info">
					<dl class="dt_w33">
		          		<dt>수집 항목</dt>
		          		<dd>휴대전화 번호</dd>
		       	 	</dl>
		       	 	<dl class="dt_w33">
		          		<dt>수집 및 이용 목적</dt>
		          		<dd>회원가입을 위한 URL 발송</dd>
		       	 	</dl>
		       	 	<dl class="dt_w33">
		          		<dt>보유 및 이용기간</dt>
		          		<dd>가입 메세지 발송 후 즉시 삭제</dd>
		       	 	</dl>
         		</div>	 
				<p class="txt_l">*고객님께서는 개인정보 수집 및 이용 동의에 거부할 수 있습니다. 다만, 거부하는 경우 가입 메세지 발송이 불가하여 태블릿을 통한 가입을 부탁드립니다.</p>
				<div class="send_kakao_agree">
           			<span class="checkboxA">
           				<input type="checkbox" id="i_send_kakao_agree" />
           				<label for="i_send_kakao_agree"><span class="checkbox_label">동의합니다.</span></label>
           			</span>
            	</div>
        	</div>
        	<div class="btn_submit mt20">
          		<button type="button" class="btnA btn_blue" disabled id='sendkakao'>회원가입 메세지 발송</button>
       		</div>        	
		</section>
	    <!-- //container -->
	</div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>