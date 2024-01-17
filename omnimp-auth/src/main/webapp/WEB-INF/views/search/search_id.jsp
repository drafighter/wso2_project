<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- ME-FO-A0208 -->
<html lang="ko">
<head>
  <title>아이디 찾기 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
	$(document).ready(function() {
		
		window.onpageshow = function(event) {
			if ( event.persisted || (window.performance && window.performance.navigation.type == 2)) {
				// Back Forward Cache로 브라우저가 로딩될 경우 혹은 브라우저 뒤로가기 했을 경우
				$('#name').val('');
				$('#phone').val('');
				$('#name').removeClass('is_disabled').removeClass('is_success');
				$('#phone').removeClass('is_disabled').removeClass('is_success');
	        }	
		}
	  
		//$('#name').focus();
		
		// 이름 key in  
		$('#name').on('touchend, keyup', function(e) {
			$(this).val($(this).val().trim());
			var numexp = /^[0-9]*$/;
			var name = $(this).val();
			var phone = $('#phone').val();
			if (name.length > 1) {
				$(this).removeClass('is_error').addClass('is_success');
			} else {
				$(this).removeClass('is_success').addClass('is_error');
			}
			if (name.length > 1 && (numexp.test(phone) && phone.length > 10)) {
				$('#sendsms').attr('disabled', false);
			} else {
				$('#sendsms').attr('disabled', true);
			}
			var keycode = e.keyCode || e.which;
			if (name.length > 1 && keycode === KeyCode.ENTER) {
				$('#phone').focus();
			}
		});
		$('#name').on('paste', $('body'), function(e) {
			var numexp = /^[0-9]*$/;
			var phone = $('#phone').val();
	    	var data = e.originalEvent.clipboardData.getData('Text');
	    	if (data.length > 1) {
	    		$(this).removeClass('is_error').addClass('is_success');
	    	} else {
	    		$(this).removeClass('is_success').addClass('is_error');
	    	}
	    	if (data.length > 1 && (numexp.test(phone) && phone.length > 10)) {
				$('#sendsms').attr('disabled', false);
			} else {
				$('#sendsms').attr('disabled', true);
			}
	    });
		// 휴대폰 key in 
		$('#phone').on('touchend, keyup', function(e) {
		    var key = e.which || e.keyCode;
		    if (key === KeyCode.LEFT || key === KeyCode.RIGHT || key === KeyCode.DELETE) {
		    	return;
		    } else {
		    	$(this).removeClass('is_error').addClass('is_success');
		    	e.target.value = e.target.value.replace(/[^0-9]/g,'');
		    }
		    var name = $('#name').val();
		    var phone = e.target.value;
		    var phoneexp = /^01(?:0|1|[6-9])(?:\d{3}|\d{4})\d{4}$/;
		    if (phone.length > 10 && (phoneexp.test(phone))) {
		    	$(this).removeClass('is_error').addClass('is_success');
		    } else {
		    	$(this).removeClass('is_success').addClass('is_error');
		    }
		    
		    if (name.length > 1 && phone.length > 10 && (phoneexp.test(phone))) {
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
		$('#phone').on('paste', $('body'), function(e) {
			var name = $('#name').val();
			var phoneexp = /^01(?:0|1|[6-9])(?:\d{3}|\d{4})\d{4}$/;
	    	var data = e.originalEvent.clipboardData.getData('Text');
	    	if (name.length > 1 && data.length > 10 && (phoneexp.test(data))) {
	    		$(this).removeClass('is_error').addClass('is_success');
		    	$('#sendsms').attr('disabled', false);
	    	} else {
	    		$(this).removeClass('is_success').addClass('is_error');
		    	$('#sendsms').attr('disabled', true);
	    	}
	    });
		// sms 발송 버튼
		$('#sendsms').on('click', function() {
			var params = {
				userName: $('#name').val(),
				userPhone: $('#phone').val(),
				searchId: true
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
						// SMS 발송 성공이면 호출
						OMNI.popup.mobileauth({
							id:'mobileauth', 
							sendno:$('#smsSeq').val(),
							gaArea:'아이디 찾기',
							content:'인증 5회 실패시 30분 동안 휴대폰 인증이 제한됩니다.',
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
					} else if (data.status === -5) { // 통합고객번호없음
						OMNI.popup.open({
							id:'phone-dup-waring',
							content: '휴대폰 인증 로그인을 할 수 없는<br/>회원 정보 입니다.<br/>가입한 아이디로 로그인 하거나,<br/>회원가입 여부를 확인해주세요.',
							closelabel:'확인',
							closeclass:'btn_blue'
						});
					} else if (data.status === -10) { // 고객정보 없음
						OMNI.popup.open({
							id:'phone-waring',
							content: '가입한 회원이 아니거나 입력하신 정보가 일치하지 않습니다.',
							closelabel:'확인',
							closeclass:'btn_blue'
						});
					} else if (data.status === -15) { // 5번 틀린지 30분 경과되지 않음.
						OMNI.popup.open({
							id:'phone-retry-waring',
							content: '인증번호 5회 오류입니다.<br/>' + data.times + ' 후에 다시 시도해주세요.<br/>(<span id="auth-retry-timer">' + data.times + '</span>)',
							closelabel:'확인',
							closeclass:'btn_blue'
						});						
					} else {
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
			
		});		
		
		$(".btn_join_membership").on('click', function() {
			location.href = OMNIEnv.ctx + '/go-join-param';
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
							// location.href = OMNIEnv.ctx + '/search/id-result'; // 
							$('#search-id-name').val($('#name').val());
							$('#search-id-phone').val($('#phone').val());
							$('#search-id-result-form')
							.submit();	
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
			userPhone: phone,
			searchId: true
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
						content: '인증번호 5회 오류 입니다.<br/>30분 후에 다시 시도해주세요.',
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
	var prevAction = function() {
		location.href = OMNIEnv.ctx + '/go-login';
	};	
  </script>   
</head>

<body>
<form id='search-id-result-form' method='post' action='<c:out value='${ctx}'/>/search/id-result'>
	<input type='hidden' id='search-id-name' name='name'/>
	<input type='hidden' id='search-id-phone' name='phone'/>
</form>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="아이디 찾기" type="prvaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>가입 시 입력한 정보로 아이디를 찾아보세요.</h2>
      </div>
      <div class="sec_login">
          <div class="input_form">
            <span class="inp">
              <input type="text" id="name" autocomplete="off" class="inp_text" placeholder="이름(두 자 이상 입력)" ap-click-area="아이디 찾기" ap-click-name="아이디 찾기 - 이름 입력란" ap-click-data="이름 입력"  title="이름 입력"/>
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
          </div>
          <div class="input_form">
            <span class="inp">
              <input type="tel" id="phone" autocomplete="off" class="inp_text" maxlength="11" placeholder="휴대폰 번호 입력 (‘-’ 생략)" ap-click-area="아이디 찾기" ap-click-name="아이디 찾기 - 휴대폰 번호 입력란" ap-click-data="휴대폰 번호 입력" title="휴대폰 번호 입력" />
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
              <input type='hidden' id='smsSeq'>
            </span>
          </div>
          <div class="btn_submit mt20">
            <button type="button" id="sendsms" class="btnA btn_blue" disabled ap-click-area="아이디 찾기" ap-click-name="아이디 찾기 - 인증번호 전송 버튼" ap-click-data="인증번호 발송">인증번호 전송</button>
          </div>
        <button class="btnA btn_white btn_join_membership mt40" ap-click-area="아이디 찾기" ap-click-name="아이디 찾기 - 회원가입 버튼" ap-click-data="회원가입">
          <span>아직 회원이 아니세요?</span>
          <em>회원가입</em>
        </button>
      </div>
      <!-- //sec_login -->
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>