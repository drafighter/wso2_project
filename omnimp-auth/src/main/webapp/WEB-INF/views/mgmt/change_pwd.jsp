<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html><!-- ME-FO-A0211 -->
<html lang="ko">
<head>
  <title>비밀번호 변경 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <common:backblock block="false"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	  
		<c:if test="${userSize == 0}">
		OMNI.popup.open({
			id:'search-id-no-result',
			content: '가입된 회원 아이디가 아닙니다.',
			oklabel:'확인',
			ok:function() {
				OMNI.popup.close({ id: 'search-id-no-result' });
				location.href = OMNIEnv.ctx + '/search/pwd';
			},
			closelabel:''
		});
		$('.layer_wrap').focus();
		</c:if>
		$('.inp .btn_del').each(function () {
			var $target_inp = $(this).parent('.inp').find('.inp_text');
			//var $guide = $(this).parent('.inp').next($('p'));
			$(this).toggle(Boolean($target_inp.val()));
			$(this).click(function () {
				$(this).hide(); // X 버튼 숨기고
				
				$target_inp.val('').focus(); // 해당 Input 에 포커싱
				
				$('#dochangepwd').attr('disabled', true);
			});
		});
	  
		//$('#password').focus();
		
		$('#password').on('focus', function() {
			if ($(this).val() === '') {
				$(this).removeClass('is_success').addClass('is_error');
				$('#password-guide-msg').empty();
				$('#password-guide-msg').removeClass('is_success').addClass('is_error');
				$('#password-guide-msg').html((new Function('return password.valid.error.emp'))()).show();
				OMNI.auth.setPasswordStrength('password-guide-strength', 'password-guide-msg', -1);
			} 
		});
		
		// 비밀번호 확인
		$('#confirmpassword').on('focus', function() {
			if ($(this).val() === '') {
				$(this).removeClass('is_success').addClass('is_error');
				$('#confirm-password-guide-msg').empty();
				$('#confirm-password-guide-msg').removeClass('is_success').addClass('is_error');
				$('#confirm-password-guide-msg').html((new Function('return password.valid.error.emp_re'))()).show();
				OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', -1);
			} 
		});
      
		$('#password').on('touchend, keyup', function(e) {
			var valid = OMNI.auth.validPassword($(this).val(), {checkId:'<c:out escapeXml="false" value="${searchId}" />'});
			var msgkey = OMNI.auth.validationdMsgKey('password', valid.key);
			if (valid.code > 0) {
				$(this).removeClass('is_error').addClass('is_success');
				$('#password-guide-msg').empty();
				$('#password-guide-msg').removeClass('is_error').addClass('is_success');
				$('#password-guide-msg').html((new Function('return ' + msgkey))()).show();
				OMNI.auth.setPasswordStrength('password-guide-strength', 'password-guide-msg', valid.strength);
				
				var p = $(this).val();
				var cp = $('#confirmpassword').val();
				if (cp !== '') {
					if (p === cp && $('#confirmpassword').hasClass('is_error')) {
						$('#confirmpassword').removeClass('is_error').addClass('is_success');
						$('#confirm-password-guide-msg').empty();
						$('#confirm-password-guide-msg').removeClass('is_error').addClass('is_success');
						$('#confirm-password-guide-msg').html((new Function('return ' + msgkey))()).show();
					} else {
						$('#confirmpassword').removeClass('is_success').addClass('is_error');
						$('#confirm-password-guide-msg').empty();
						$('#confirm-password-guide-msg').removeClass('is_success').addClass('is_error');
						$('#confirm-password-guide-msg').html((new Function('return password.valid.error.same'))()).show();
					}
					OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', valid.strength);
				}
				var keycode = e.keyCode || e.which;
				if (keycode === KeyCode.ENTER) {
					$('#confirmpassword').focus();
				}
			} else {
				$(this).removeClass('is_success').addClass('is_error');
				$('#password-guide-msg').empty();
				$('#password-guide-msg').removeClass('is_success').addClass('is_error');
				$('#password-guide-msg').html((new Function('return ' + msgkey))()).show();
				OMNI.auth.setPasswordStrength('password-guide-strength', 'password-guide-msg', valid.strength);
			}
			
			disabledOnOff();
		});
			
		$('#confirmpassword').on('touchend, keyup', function(e) {
			var valid = OMNI.auth.validPassword($(this).val(), {confirmId:'password', checkId:'<c:out escapeXml="false" value="${searchId}" />', isConfirm:true});
			var msgkey = OMNI.auth.validationdMsgKey('password', valid.key);
			if (valid.code > 0) {
				$(this).removeClass('is_error').addClass('is_success');
				$('#confirm-password-guide-msg').empty();
				$('#confirm-password-guide-msg').removeClass('is_error').addClass('is_success');
				$('#confirm-password-guide-msg').html((new Function('return ' + msgkey))()).show();
				OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', valid.strength);
				
				var cp = $(this).val();
				var p = $('#password').val();
				if (p !== '') {
					if (p === cp && $('#password').hasClass('is_error') ) {
						$('#password').removeClass('is_error').addClass('is_success');
						$('#password-guide-msg').empty();
						$('#password-guide-msg').removeClass('is_error').addClass('is_success');
						$('#password-guide-msg').html((new Function('return ' + msgkey))()).show();
						OMNI.auth.setPasswordStrength('password-guide-strength', 'password-guide-msg', valid.strength);
					} 
				}
				
				if (!$('#dochangepwd').is(':disabled')) {
					var keycode = e.keyCode || e.which;
					if (keycode === KeyCode.ENTER) {
						$('#dochangepwd').trigger('click');
					}	
				}
			} else {
				$(this).removeClass('is_success').addClass('is_error');
				$('#confirm-password-guide-msg').empty();
				$('#confirm-password-guide-msg').removeClass('is_success').addClass('is_error');
				$('#confirm-password-guide-msg').html((new Function('return ' + msgkey))()).show();
				OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', valid.strength);
			}
			
			disabledOnOff();
		});
	  
		$('#dochangepwd').on('click', function() {
			var params = {
			<c:if test="${not empty omniResponse}">
				omniLoginId:$('#omniloginid').val(),
				omniIncsNo:$('#omniincsno').val(),
				omniName:$('#omnicustname').val(),
				omniMobile:$('#omnicustphone').val(),
			</c:if>
			<c:if test="${not empty chResponse}">
				chLoginId:$('#chloginid').val(),
				chIncsNo:$('#chincsno').val(),
				chName:$('#chcustname').val(),
				chMobile:$('#chcustphone').val(),
			</c:if>
				password:OMNI.auth.encode(OMNIEnv.pprs, $('#password').val()),
				confirmPassword:OMNI.auth.encode(OMNIEnv.pprs, $('#confirmpassword').val())
			};
			$.ajax({
				url:OMNIEnv.ctx + '/search/pwd-check-update',
				type:'post',
				data:JSON.stringify(params),
				dataType:'json',
				contentType : 'application/json; charset=utf-8',
				success: function(data) {
					if (data.status === 1) {
						OMNI.popup.open({
							id:'change-pwd', 
							content:'비밀번호가 변경되었습니다.<br/>다시 한번 로그인 해주세요.',
							closelabel:'확인',
							closeclass:'btn_blue',
							close:function() {
								OMNI.popup.close({ id: 'change-pwd' });
								location.href = OMNIEnv.ctx + '/go-login';
							}
						});
					} else {
						if (data.status === -5) {
							OMNI.popup.open({id:'change-pwd', content:'사용자 정보가 올바르지 않습니다.',closelabel:'확인',closeclass:'btn_blue'});
						} else if (data.status === -10) {
							OMNI.popup.open({id:'change-pwd', content:'비밀번호 변경에 실패하였습니다.',closelabel:'확인',closeclass:'btn_blue'});
						} else if (data.status === -15) {
							OMNI.popup.open({id:'change-pwd', content:'기존 비밀번호는 사용이 불가합니다.',closelabel:'확인',closeclass:'btn_blue'});
						} else if (data.status === -20) {
							OMNI.popup.open({id:'change-pwd', content:'비밀번호와 비밀번호 재입력이 일치하지 않습니다.',closelabel:'확인',closeclass:'btn_blue'});
						} else if (data.status === -25) {
							OMNI.popup.open({id:'change-pwd', content:'아이디와 비밀번호가 동일하여<br/>사용이 불가합니다.',closelabel:'확인',closeclass:'btn_blue'});
						} else if (data.status === -30) {
							OMNI.popup.open({id:'change-pwd', content:'가입하지 않은 사용자 입니다.',closelabel:'확인',closeclass:'btn_blue', 
												close:function() {
													OMNI.popup.close({ id: 'change-pwd' });
													location.href = OMNIEnv.ctx + '/search/pwd';
												}
											});
						} else {
							OMNI.popup.error();
							$('.layer_wrap').focus();
						}
					}
					$('.layer_wrap').focus();
				},
				error: function() {
					OMNI.popup.error();
					$('.layer_wrap').focus();
				}
			
			});	
		});
		
		$('#change-pwd-cancel').on('click', function() {
			
			OMNI.popup.open(
				{
					id:'change-pwd-warn', 
					content:'비밀번호 변경을 멈추고 로그인<br/>화면으로 돌아가시겠습니까?<br/>(현재 계정은 로그아웃 됩니다.)',
					oklabel:'취소',
					okclass:'btn_white',
					ok:function() {
						OMNI.popup.close({id:'change-pwd-warn'});
					},
					closelabel:'확인',
					closeclass:'btn_blue',
					close:function() {
						OMNI.popup.close({id:'change-pwd-warn'});
						location.href = OMNIEnv.ctx + '/go-login';
					}
				}
			);
			$('.layer_wrap').focus();
		});
  });
  var closeAction = function() {
	  
	OMNI.popup.open({
		id:'change-pwd-warn', 
		content:'비밀번호 변경을 멈추고 로그인<br/>화면으로 돌아가시겠습니까?<br/>(현재 계정은 로그아웃 됩니다.)',
		gaArea:'비밀번호 변경',
		gaOkName:'확인 버튼 (중단 팝업)',
		gaCancelName:'취소 버튼 (중단 팝업)',
		oklabel:'취소',
		okclass:'btn_white',
		ok:function() {
			OMNI.popup.close({id:'change-pwd-warn'});
		},
		closelabel:'확인',
		closeclass:'btn_blue',
		close:function() {
			window.AP_SIGNUP_TYPE = '중단';
			dataLayer.push({event: 'signup_complete'});	
			OMNI.popup.close({ id: 'change-pwd-warn' });
			//$.ajax({url:OMNIEnv.ctx + '/ga/tagging/join/stop/' + encodeURIComponent("경로전환"),type:'get'});
			location.href = OMNIEnv.ctx + '/go-login';
		}
	});
	$('.layer_wrap').focus();
  };
  var disabledOnOff = function() {
		var condition = true;
		
		condition &= $("#password").val() !== '';
		condition &= $("#confirmpassword").val() !== '';
		condition &= $("#password").val() === $("#confirmpassword").val();
		condition &= $(".is_error").length === 0;
		
		if (condition) {
            $('#dochangepwd').removeAttr('disabled');
		} else {
            $('#dochangepwd').attr('disabled', true);
		}
	  };
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="비밀번호 변경" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>새로운 비밀번호를 입력해주세요.</h2>
      </div>
        <div class="input_form">
          <span class="inp" id='password-span'>
            <input type="password" class="inp_text" id="password" autocomplete="off" maxlength="16" placeholder="비밀번호 (영문 소문자, 숫자, 특수문자 조합 8-16자)" ap-click-area="비밀번호 변경" ap-click-name="비밀번호 변경 - 비밀번호 입력란" ap-click-data="비밀번호 입력"  title="비밀번호 입력"/>
            <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
          </span>
          <p id="password-guide-msg" class="form_guide_txt is_success"></p>
        </div>
        <div class="input_form">
          <span class="inp" id='confirm-password-span'>
            <input type="password" class="inp_text" id="confirmpassword" autocomplete="off" maxlength="16" placeholder="비밀번호 확인" ap-click-area="비밀번호 변경" ap-click-name="비밀번호 변경 - 비밀번호 확인 입력란" ap-click-data="비밀번호 확인 입력"   title="비밀번호 확인 입력"/>
            <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
          </span>
          <p id="confirm-password-guide-msg" class="form_guide_txt is_success"></p>
          <common:password-notice gaArea="비밀번호 변경"/>
        </div>
        <p class="txt_l">*비밀번호 변경 시 자동로그인 설정이 해제 됩니다.</p>
        <div class="btn_submit ver2">
          <button type="button" class="btnA btn_white" id='change-pwd-cancel' ap-click-area="비밀번호 변경" ap-click-name="비밀번호 변경 - 취소 버튼" ap-click-data="취소">취소</button>
          <button type="button" id='dochangepwd' class="btnA btn_blue" disabled ap-click-area="비밀번호 변경" ap-click-name="비밀번호 변경 - 비밀번호 변경 버튼" ap-click-data="비밀번호 변경">비밀번호 변경</button>
<c:if test="${not empty omniResponse}">
          <input type='hidden' id='omniincsno' value='<c:out escapeXml="false" value="${omniResponse.incsNo}" />'/>
          <input type='hidden' id='omnicustname' value='<c:out escapeXml="false" value="${omniResponse.name}" />'>
          <input type='hidden' id='omnicustphone' value='<c:out escapeXml="false" value="${omniResponse.mobile}" />'/>
          <input type='hidden' id='omniloginid' value='<c:out escapeXml="false" value="${omniResponse.loginId}" />'/>
</c:if>
<c:if test="${not empty chResponse}">
          <input type='hidden' id='chincsno' value='<c:out escapeXml="false" value="${chResponse.incsNo}" />'/>
          <input type='hidden' id='chcustname' value='<c:out escapeXml="false" value="${chResponse.name}" />'/>
          <input type='hidden' id='chcustphone' value='<c:out escapeXml="false" value="${chResponse.mobile}" />'/>
          <input type='hidden' id='chloginid' value='<c:out escapeXml="false" value="${chResponse.loginId}" />'/>
</c:if>
        </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>