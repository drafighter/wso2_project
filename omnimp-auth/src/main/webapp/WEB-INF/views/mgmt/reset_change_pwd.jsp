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
  <title>비밀번호 변경 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	  $('#loginid-guide-msg').hide();
	  $('#password-guide-msg').hide();
	  $('#confirm-password-guide-msg').hide(); 
	  
		$('#password').on('touchend, keyup', function(e) {
			var valid = OMNI.auth.validPassword($(this).val(), {confirmId:'confirmpassword', checkId:'<c:out value="${checkId}" />'});
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
					if (p === cp) {
						$('#dochangepwd').attr('disabled', false);
						$(this).removeClass('is_error').addClass('is_success');
						$('#password-guide-msg').empty();
						$('#password-guide-msg').removeClass('is_error').addClass('is_success');
						$('#password-guide-msg').html((new Function('return ' + msgkey))()).show();
						OMNI.auth.setPasswordStrength('password-guide-strength', 'password-guide-msg', valid.strength);
					} else {
						$('#dochangepwd').attr('disabled', true);
						$(this).removeClass('is_success').addClass('is_error');
						$('#password-guide-msg').empty();
						$('#password-guide-msg').removeClass('is_success').addClass('is_error');
						$('#password-guide-msg').html((new Function('return ' + msgkey))()).show();
						OMNI.auth.setPasswordStrength('password-guide-strength', 'password-guide-msg', valid.strength);
					}
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
				$('#dochangepwd').attr('disabled', true);
			}
		});
			
		$('#confirmpassword').on('touchend, keyup', function(e) {
			var valid = OMNI.auth.validPassword($(this).val(), {confirmId:'password', checkId:'<c:out escapeXml="false" value="${checkId}" />', isConfirm:true});
			var msgkey = OMNI.auth.validationdMsgKey('password', valid.key);
			if (valid.code > 0) {
				$(this).removeClass('is_error').addClass('is_success');
				
				if ($('#password').hasClass('is_error')) {
					$('#password').removeClass('is_error').addClass('is_success');
					$('#password-guide-msg').removeClass('is_error').empty();	
				}
				
				$('#confirm-password-guide-msg').empty();
				$('#confirm-password-guide-msg').removeClass('is_error').addClass('is_success');
				$('#confirm-password-guide-msg').html((new Function('return ' + msgkey))()).show();
				OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', valid.strength);
				var cp = $(this).val();
				var p = $('#password').val();
				if (p !== '') {
					if (p === cp) {
						$('#dochangepwd').attr('disabled', false);
						$(this).removeClass('is_error').addClass('is_success');
						$('#confirm-password-guide-msg').empty();
						$('#confirm-password-guide-msg').removeClass('is_error').addClass('is_success');
						$('#confirm-password-guide-msg').html((new Function('return ' + msgkey))()).show();
						OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', valid.strength);				
					} else {
						$('#dochangepwd').attr('disabled', true);
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
				$('#dochangepwd').attr('disabled', true);
			}
		});
	  
		$('#dochangepwd').on('click', function() {
			
			if($('#password').val() === '' || $('#confirmpassword').val() === '') {
				OMNI.popup.open({ id: "rest_pwd_check_fail", closelabel: "확인", closeclass:'btn_blue',  content: "비밀번호와 비밀번호 확인 값을 입력해 주세요."});
				$('.layer_wrap').focus();
				return;
			}
			
			$('#xpw').val(OMNI.auth.encode(OMNIEnv.pprs, $('#password').val()));
			$('#xcpw').val(OMNI.auth.encode(OMNIEnv.pprs, $('#confirmpassword').val()));
			
			var data = {
				encId: $('#xid').val(),
				umUserPassword: $('#password').val(),
				encConfirmPwd: $('#confirmpassword').val()
			};
			
			$.ajax({
				url:OMNIEnv.ctx + '/mgmt/pwdcheck',
				type:'post',
				data:JSON.stringify(data),
				async: false,
				dataType:'json',
				contentType : 'application/json; charset=utf-8',
				success: function(data) {
					if (data.status === 100) {
						$('#pwd-reset-form')
						.attr('action', OMNIEnv.ctx + '/mgmt/do-reset-pwd')
						.submit();
					} else {
						switch(data.status) {
						case -5: // EMPTY
						case -20: // WRONG
							OMNI.popup.open({ id: "rest_pwd_check_fail", closelabel: "확인", closeclass:'btn_blue',  content: "비밀번호를 다시 확인해주세요."});
							$('.layer_wrap').focus();
							break;
						case -10: // SAME
							OMNI.popup.open({ id: "rest_pwd_check_fail", closelabel: "확인", closeclass:'btn_blue',  content: "비밀번호와 비밀번호 확인이 일치하지 않습니다." });
							$('.layer_wrap').focus();
							break;
						default:
							OMNI.popup.open({ id: "rest_pwd_check_fail", closelabel: "확인", closeclass:'btn_blue',  content: "비밀번호를 다시 확인해주세요."});
						$('.layer_wrap').focus();
							break;
						}
					}
				},
				error: function() {
					
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
						location.href = OMNIEnv.ctx + '/retry';
					}
				}
			);
			$('.layer_wrap').focus();
		});
  });
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="비밀번호 변경"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>새로운 비밀번호를 입력해주세요.</h2>
      </div>
      <form id='pwd-reset-form' method='post' action=''>
      	<input type='hidden' name='xid' id='xid' value='<c:out escapeXml="false" value="${xid}" />'/>
      	<input type='hidden' name='xincsno' id='xincsno' value='<c:out escapeXml="false" value="${xincsno}" />'/>
       	<input type='hidden' name='xpw' id='xpw'/>
      	<input type='hidden' name='xcpw' id='xcpw'/>
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
          <input type='hidden' id='incsno' value='<c:out value="${response.incsNo}" />'/>
          <input type='hidden' id='custname' value='<c:out value="${response.name}" />'/>
          <input type='hidden' id='custphone' value='<c:out value="${response.mobile}" />'/>
        </div>
      </form>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>