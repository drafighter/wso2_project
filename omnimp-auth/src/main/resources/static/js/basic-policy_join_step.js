	/**
	 * 로그인 아이디, 비밀번호, 비밀번호 확인 정책 공통
	 */
	$(document).ready(function() {
	
		/**
		 * 로그인 아이디 키입력이나 터치 시 동작
		 * 
		 * 정책
		 *  - 영문, 숫자 4~12, 중복 미허용
		 *  
		 * 동작
		 *  - 입력란에 미입력, 4자 미만 : 아이디는 4~12자 영문 또는 숫자를 사용하여 입력해주세요.
		 *  - 정책기준 ID 형식이 올바르지 않음 : 사용할 수 없는 아이디입니다.
		 *  - 이미 등록된 ID : 이미 사용중인 아이디입니다.
		 *  - 정상 입력 : 사용 가능한 아이디 입니다.
		 *  
		 */
		$('#loginid').on('touchend, keyup', function(e) {
			$(this).val($(this).val().trim());
			var valid = OMNI.auth.validLoginId($(this).val(), {checkPassword:$('#loginpassword').val()});
			var msgkey = OMNI.auth.validationdMsgKey('loginid', valid.key);
			if (valid.code > 0) {
				$(this).removeClass('is_error').addClass('is_success');
				$('#loginid-guide-msg').empty();
				$('#loginid-guide-msg').removeClass('is_error').addClass('is_success');
				$('#loginid-guide-msg').html((new Function('return ' + msgkey))()).show();
				if ($(this).val() !== '' && ($('#loginpassword').val() != '' && !$('#loginpassword').hasClass('is_error'))) {
					$('#loginpassword').removeClass('is_error').addClass('is_success');
					if ($('#password-guide-msg').length > 0) {
						$('#password-guide-msg').empty();
						$('#password-guide-msg').removeClass('is_error').addClass('is_success');
						$('#password-guide-msg').hide();
					}
				}
				if ($(this).val() !== '' && ($('#loginconfirmpassword').val() !== '' && !$('#loginconfirmpassword').hasClass('is_error'))) {
					$('#loginconfirmpassword').removeClass('is_error').addClass('is_success');
					if ($('#confirm-password-guide-msg').length > 0) {
						$('#confirm-password-guide-msg').empty();
						$('#confirm-password-guide-msg').removeClass('is_error').addClass('is_success');
						$('#confirm-password-guide-msg').hide();
					}
				}
				var keycode = e.keyCode || e.which;
				if (keycode === KeyCode.ENTER) {
					$('#loginpassword').focus();
				}
			} else {
				$(this).removeClass('is_success').addClass('is_error');
				$('#loginid-guide-msg').empty();
				$('#loginid-guide-msg').removeClass('is_success').addClass('is_error');
				$('#loginid-guide-msg').html((new Function('return ' + msgkey))()).show();
				$('#dojoin').attr('disabled', true);
			}
			//disabledOnOff(); -> ajax response success 로 이동
			//passswordEmptyCheck();
		});
		
		/**
		 * 로그인 비밀번호 키입력이나 터치 시 동작
		 * 
		 * 정책
		 *  - 영문소문자, 숫자, 특수문자 중 최소 2가지 이상 조합 8~16
		 *  - 아이디와 동일하게 생성할 수 없음, 공백 사용할 수 없음.
		 *  - 사용 가능한 특수문자 32자 : ! " # $ % & ' ( ) * + , --. / : ; < = > ? @ [ ＼ ] ^ _ ` { | }
		 * 
		 * 동작
		 *  - 입력란에 미입력 : 비밀번호는 영문(소문자), 숫자, 특수문자 중 최소 2가지 이상의 문자 조합 8~16자로 입력해주세요.
		 *  - 정책기준 올바르지 않은 형식 : 사용 할 수 없는 비밀번호입니다.(불가)
		 *  - 정상적으로 입력된 경우 비밀번호 복잡도 위험 , 보통 , 안정 : 사용가능한 비밀번호 입니다 . (위험) (보통) (안전)
		 * 
		 */
		$('#loginpassword').on('touchend, keyup', function(e) {
			var valid = OMNI.auth.validPassword($(this).val(), {checkId:$('#loginid').val()});
			var msgkey = OMNI.auth.validationdMsgKey('password', valid.key);
			if (valid.code > 0) {
				$(this).removeClass('is_error').addClass('is_success');
				$('#password-guide-msg').empty();
				$('#password-guide-msg').removeClass('is_error').addClass('is_success');
				$('#password-guide-msg').html((new Function('return ' + msgkey))()).show();
				OMNI.auth.setPasswordStrength('login-guide-strength', 'password-guide-msg', valid.strength);
				
				if($(this).val() === $('#loginconfirmpassword').val()) {
					$('#loginconfirmpassword').removeClass('is_error').addClass('is_success');
					$('#confirm-password-guide-msg').empty();
					$('#confirm-password-guide-msg').removeClass('is_error').addClass('is_success');
					$('#confirm-password-guide-msg').html((new Function('return ' + msgkey))()).show();
					OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', valid.strength);
				} else {
					msgkey = 'password.valid.error.same';
					if( $('#loginconfirmpassword').val() === '') {
						msgkey = 'password.valid.error.emp_re';	
					}
					$('#loginconfirmpassword').removeClass('is_success').addClass('is_error');
					$('#confirm-password-guide-msg').empty();
					$('#confirm-password-guide-msg').removeClass('is_success').addClass('is_error');
					$('#confirm-password-guide-msg').html((new Function('return ' + msgkey))()).show();
					OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', valid.strength);
				}
				
				if (!$('#dojoin').is(':disabled')) {
					var keycode = e.keyCode || e.which;
					if (keycode === KeyCode.ENTER) {
						$('#loginconfirmpassword').focus();
					}					
				}
			} else {
				$(this).removeClass('is_success').addClass('is_error');
				$('#password-guide-msg').empty();
				$('#password-guide-msg').removeClass('is_success').addClass('is_error');
				$('#password-guide-msg').html((new Function('return ' + msgkey))()).show();
				OMNI.auth.setPasswordStrength('login-guide-strength', 'password-guide-msg', valid.strength);
				
				if($('#loginconfirmpassword').val().length > 0) {
					//$('#loginconfirmpassword').removeClass('is_success').addClass('is_error');
					//$('#confirm-password-guide-msg').empty();
					//$('#confirm-password-guide-msg').removeClass('is_success').addClass('is_error');
					//$('#confirm-password-guide-msg').html((new Function('return ' + msgkey))()).show();
					//OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', valid.strength);
				} else {
					$('#loginconfirmpassword').removeClass('is_success is_error');
					$('#confirm-password-guide-msg').empty();
					$('#confirm-password-guide-msg').removeClass('is_success is_error');
					$('#confirm-password-guide-msg').hide();
				}
			}
			disabledOnOff();
		});		
		
		/**
		 * 로그인 비밀번호확인 키입력이나 터치 시 동작
		 * 
		 * 정책
		 *  - 비밀번호와 정책 동일
		 * 
		 * 동작
		 *  - 비밀번호 입력 값과 비밀번호 확인 값이 일치하지 않을 경우 : 비밀번호와 비밀번호 확인이 일치하지 않습니다.
		 *  - 해당 영역 터치 후 아무것도 입력하지 않은 경우 : 비밀번호를 한번 더 입력해주세요.
		 *  - 비밀번호 입력 값과 비밀번호 확인 값이 일치하는 경우 : 비밀번호와 비밀번호 확인이 일치 합니다.
		 *  - 비밀번호 입력 값이 형식에 올바르지 않은 상태에서 동일하게 비밀번호 확인을 입력한 경우 : 사용 할 수 없는 비밀번호 입니다.
		 *   
		 */
		$('#loginconfirmpassword').on('touchend, keyup', function(e) {
			var valid = OMNI.auth.validPassword($('#loginconfirmpassword').val(), {checkId:$('#loginid').val(), confirmId:'loginpassword', isConfirm:true});
			var msgkey = OMNI.auth.validationdMsgKey('password', valid.key);
			if (valid.code > 0) {
				$(this).removeClass('is_error').addClass('is_success');
				if ($('#loginpassword').hasClass('is_error')) {
					$('#loginpassword').removeClass('is_error').addClass('is_success');
					$('#password-guide-msg').empty();
					$('#password-guide-msg').removeClass('is_error').addClass('is_success');
				}
				$('#confirm-password-guide-msg').empty();
				$('#confirm-password-guide-msg').removeClass('is_error').addClass('is_success');
				$('#confirm-password-guide-msg').html((new Function('return ' + msgkey))()).show();
				OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', valid.strength);
				var keycode = e.keyCode || e.which;
				if (keycode === KeyCode.ENTER) {
					$('#dojoin').trigger('click');
				}
			} else {
				if (valid.code < -4) { msgkey = 'password.valid.error.invalid'; }
				$(this).removeClass('is_success').addClass('is_error');
				$('#confirm-password-guide-msg').empty();
				$('#confirm-password-guide-msg').removeClass('is_success').addClass('is_error');
				$('#confirm-password-guide-msg').html((new Function('return ' + msgkey))()).show();
				OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', valid.strength);
			}
			disabledOnOff();
		});		
		
		/**
		 * 로그인 아이디 입력란을 빠져나올 경우 아이디 중복여부 체크
		 * @returns
		 */
		$('#loginid').on('blur', function() {
			loginIdAvaiable($('#loginid').val().trim());
		});
		
		/**
		 * 로그인 아이디에 포커스가 갈 경우 빈값인지 체크
		 * @param e
		 * @returns
		 */
		$('#loginid').on('focus', function(e) {
			if ($(this).val() === '') {
				msgkey = 'loginid.valid.error.emp';
				$(this).removeClass('is_success').addClass('is_error');
				$('#loginid-guide-msg').empty();
				$('#loginid-guide-msg').removeClass('is_success').addClass('is_error');
				$('#loginid-guide-msg').html((new Function('return ' + msgkey))()).show();
				$('#dojoin').attr('disabled', true);
			}
		});	
		
		/**
		 * 로그인 비밀번호, 비밀번호확인에 포커스 가거나 빠져나올 경우
		 * - 아이디 중복여부 체크
		 * - 비밀번호와 비밀번호확인 빈값인지 체크
		 * - 로그인 아이디 빈값인지 체크
		 * 
		 * @returns
		 */
		$('#loginpassword, #loginconfirmpassword').on('focus blur', function() {
			loginIdAvaiable($('#loginid').val().trim());
			loginpwdandconfirmcheck($(this).attr('id'));
			loginidemptycheck($('#loginid').val());
		});			
		
	});

	/**
	 * 로그인 아이디 빈값인지 체크
	 */
	var loginidemptycheck = function(idValue) {
		if(idValue === undefined || idValue === null || idValue === '') {
			$('#loginid').removeClass('is_success').addClass('is_error');
			$('#loginid-guide-msg').empty();
			$('#loginid-guide-msg').removeClass('is_success').addClass('is_error');
			$('#loginid-guide-msg').html((new Function('return loginid.valid.error.emp'))()).show();
		}
	};
	
	/**
	 * 비밀번호와 비밀번호확인 빈값인지 체크
	 */
	var loginpwdandconfirmcheck = function(id) {
		var val = $('#' + id).val();
		if (id === 'loginpassword') {
			if (val === '') {
				$('#loginpassword').removeClass('is_success').addClass('is_error');
				$('#password-guide-msg').empty();
				$('#password-guide-msg').removeClass('is_success').addClass('is_error');
				$('#password-guide-msg').html((new Function('return password.valid.error.emp'))()).show();
				OMNI.auth.setPasswordStrength('login-guide-strength', 'password-guide-msg', -1);
			}
		} else if (id === 'loginconfirmpassword') {
			if (val === '') {
				$('#loginconfirmpassword').removeClass('is_success').addClass('is_error');
				$('#confirm-password-guide-msg').empty();
				$('#confirm-password-guide-msg').removeClass('is_success').addClass('is_error');
				$('#confirm-password-guide-msg').html((new Function('return password.valid.error.emp_re'))()).show();
				OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', -1);
			}
		}
	};