<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>휴대폰 번호 입력 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
	$(document).ready(function() {
		//$('#phone').focus();  
		// 휴대폰 key in 
		$('#phone').on('touchend, keyup', function(e) {
			var key = e.which || e.keyCode;
		    if (key === KeyCode.LEFT || key === KeyCode.RIGHT || key === KeyCode.DELETE) {
		    	return;
		    } else {
		    	e.target.value = e.target.value.replace(/[^0-9]/g,'');
		    }
		    var phone = e.target.value;
		    var phoneexp = /^01(?:0|1|[6-9])(?:\d{3}|\d{4})\d{4}$/;
		    if (phone.length > 9 && (phoneexp.test(phone))) {
		    	$('#sendsms').attr('disabled', false);
		    } else {
		    	$('#sendsms').attr('disabled', true);
		    }
		    var keycode = e.keyCode || e.which;
		    if (!$('#sendsms').is(':disabled')) {
		    	if (keycode === KeyCode.ENTER) {
		    		$('#sendsms').trigger('click');
		    	}
		    }
		});
		// sms 발송 버튼
		$('#sendsms').on('click', function() {
			var params = {
				userName: $('#name').val(),
				userPhone: $('#phone').val(),
				type:'ipin'
			};
			// SMS 발송
			$.ajax({
				url:OMNIEnv.ctx + '/cert/sendsms',
				type:'post',
				data:JSON.stringify(params),
				dataType:'json',
				contentType : 'application/json; charset=utf-8',
				success: function(data) {
					$('#smsSeq').val(data.smsAthtSendNo);
					if (data.status === 1) {
						$('#sendsms').attr('disabled', true);
						// SMS 발송 성공이면 호출
						OMNI.popup.mobileauth({id:'mobileauth', sendno:$('#smsSeq').val(), gaArea:'휴대폰 번호 입력',
							ok: function() {
								data.smsAthtNoVl = $('#sms_auth_no').val();
								data.smsNo = $('#sms_auth_no').val();
								data.phoneNo = OMNI.auth.encode(OMNIEnv.pprs, $('#phone').val());
								data.name = $('#name').val();
								authSms(data); // 인증하기
							},
							resend: function() {
								reSendsms(params.userName, params.userPhone); // SMS 발송, 타이머 다시 시작
							}
						});
						$('#sms_auth_no').focus();
					} else if (data.status === 0) { // 발송 실패
						OMNI.popup.open({
							id:'phone-send-waring',
							content: '휴대폰 인증번호 발송에 실패하였습니다.<br/>잠시 후 다시 시도해주세요.',
							closelabel:'확인',
							closeclass:'btn_blue'
						});
					} else if (data.status === -5) { // 통합고객번호 없음
						OMNI.popup.open({
							id:'phone-dup-waring',
							content: '휴대폰 인증 로그인을 할 수 없는 회원 정보 입니다.<br/>가입한 아이디로 로그인 하거나 회원가입 여부를 확인해주세요.',
							closelabel:'확인',
							closeclass:'btn_blue'
						});
					} else if (data.status === -10) { // 고객정보 없음
						OMNI.popup.open({
							id:'phone-waring',
							content: '가입한 회원이 아니거나 입력하신 정보가 일치하지 않습니다.',
							closelabel:'확인',
							closeclass:'btn_blue',
							close: function() {
								OMNI.popup.close({ id: 'phone-waring' });
								location.href = OMNIEnv.ctx + '/id-regist';
							}
						});
					} else if (data.status === -15) { // 5번 틀린지 30분 경과되지 않음.
						OMNI.popup.open({
							id:'phone-retry-waring',
							content: '인증번호 발송이 제한되었습니다.<br/>' + data.times + ' 후에 다시 시도해주세요.<br/>(<span id="auth-retry-timer">' + data.times + '</span>)',
							closelabel:'확인',
							closeclass:'btn_blue'
						});						
					} else {
						OMNI.popup.open({
							id:'phone-waring',
							content: '가입한 회원이 아니거나 입력하신 정보가 일치하지 않습니다.',
							closelabel:'확인',
							closeclass:'btn_blue',
							close: function() {
								OMNI.popup.close({ id: 'phone-waring' });
								location.href = OMNIEnv.ctx + '/id-regist';
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
	});
	// sms 인증하기
	var authSms = function(params) {
		params.smsAthtSendNo = $('#smsSeq').val();
		$('#btnOkAction').attr('disabled', true);
		$.ajax({
			url:OMNIEnv.ctx + '/cert/authsms',
			type:'post',
			data:JSON.stringify(params),
			dataType:'json',
			contentType : 'application/json; charset=utf-8',
			success: function(data) {
				if (data.status === 1) { // SMS 인증 성공이면 호출
					if (authTimer) {authTimer.stop();}
					OMNI.popup.close({id:'mobileauth'}); // 휴대폰 인증 창 닫기
					OMNI.popup.open({
						id:'phone-ipin-success',
						content: '인증이 완료되었습니다.',
						closelabel:'확인',
						closeclass:'btn_blue',
						close:function() {
							OMNI.popup.close({ id: 'phone-ipin-success' });
							location.href = OMNIEnv.ctx + '/join/step?type=ipin&itg=${itg}'; // 인증 성공 시 전화번호 전송		
						}
					});
					
				} else if (data.status === -15) { // 5회 제한 초과
					OMNI.popup.close({id:'mobileauth'}); // 휴대폰 인증 창 닫기
					OMNI.popup.open({
						id:'phone-limit-warning',
						content: '인증번호 5 회 오류 입니다.<br/>30분 후에 다시 시도해주세요.<br/>(<span id="auth-retry-timer">30:00</span>)',
						closelabel:'확인',
						closeclass:'btn_blue'
					});
				} else { // 실패
					$('#sms_send_msg').addClass('is_error').text('인증번호를 다시 확인해주세요. (' + data.smsAthtFailCnt + '/5)');
				}
				$('.layer_wrap').focus();
			},
			error: function() {
				OMNI.popup.error();
				$('.layer_wrap').focus();
			}
		
		});	
	};
	// sms 재전송
	var reSendsms = function(name, phone) {
		var data = {
			userName: name,
			userPhone: $('#phone').val(),
			type: 'ipin'
		};
		$.ajax({
			url:OMNIEnv.ctx + '/cert/sendsms',
			type:'post',
			data:JSON.stringify(data),
			dataType:'json',
			contentType : 'application/json; charset=utf-8',
			success: function(data) {
				$('#smsSeq').val(data.smsAthtSendNo);
				if (data.status === 1) {
					//$('#btnSmsResesnd').attr('disabled', true);
		        	$('#sms_send_msg').removeClass('is_error').text('인증번호를 전송하였습니다.');
		        	$('#sms_auth_no').focus();
				} else if (data.status === -1) { // 5번 틀린지 30분 경과되지 않음.
					OMNI.popup.close({id:'mobileauth'}); // 휴대폰 인증 창 닫기
					OMNI.popup.open({
						id:'phone-retry-waring',
						content: '인증번호 발송이 제한되었습니다.<br/>30분 후에 다시 시도해주세요.',
						closelabel:'확인',
						closeclass:'btn_blue'
					});						
				} else {
					OMNI.popup.close({id:'mobileauth'}); // 휴대폰 인증 창 닫기
					OMNI.popup.open({
						id:'phone-waring',
						content: '가입한 회원이 아니거나 입력하신 정보가 일치하지 않습니다.',
						closelabel:'확인',
						closeclass:'btn_blue'
					});
				}	
				$('.layer_wrap').focus();
			},
			error: function() {
				OMNI.popup.error();
				$('.layer_wrap').focus();
			}
		});		  
	};
	var closeAction = function() {
		location.href = OMNIEnv.ctx + '/join/step?type=ipin&itg='; // 인증 성공 시 전화번호 전송
	};
  </script>   
</head>
<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="휴대폰 번호 입력" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>고객님의 휴대폰 번호를 입력하세요.</h2>
      </div>
        <div class="input_form">
          <span class="inp">
            <input type="tel" id='phone' class="inp_text" maxlength=11 placeholder="휴대폰 번호 입력 (‘-’ 생략)" ap-click-area="휴대폰 번호 입력" ap-click-name="휴대폰 번호 입력 - 휴대폰 번호 입력란" ap-click-data="휴대폰 번호 입력" title="휴대폰 번호 입력"/>
            <input type='hidden' id='name' value='<c:out escapeXml="false" value="${name}" />'/>
            <input type='hidden' id='smsSeq'>
          </span>
        </div>
        <div class="btn_submit mt20">
          <button type="button" id='sendsms' ap-click-area="휴대폰 번호 입력" ap-click-name="휴대폰 번호 입력 - 인증번호 전송 버튼" ap-click-data="인증번호 전송" class="btnA btn_blue" disabled>인증번호 전송</button>
        </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->	
</body>
</html>