// ★★★★★★★★★★★★★★★★★★★★★★★
// ★★★★★    해당 script 사용시 주의 사항 
// 
// 로그인 버튼  id = dojoin
// 로그인 버튼 활성화 체크 function name = disabledOnOff() 
// ★★★★★★★★★★★★★★★★★★★★★★★

$(document).ready(function() {
	
	// ready -> loginId 에 포커싱
	//$('#loginid').focus();
	
	// 안내 메시지 다 지워놓고
	$('#loginid-guide-msg').removeClass('is_error').hide();
	$('#password-guide-msg').removeClass('is_error').hide();
	$('#confirm-password-guide-msg').removeClass('is_error').hide();
	
	// X 버튼 이벤트 등록
	$('.inp .btn_del').each(function () {
		var $target_inp = $(this).parent('.inp').find('.inp_text');
		//var $guide = $(this).parent('.inp').next($('p'));
		$(this).toggle(Boolean($target_inp.val()));
		$(this).click(function () {
			$(this).hide(); // X 버튼 숨기고
			
			$target_inp.val('').focus(); // 해당 Input 에 포커싱
			
			//$("#dojoin").attr('disabled', 'disabled');
			if ($.isFunction(window.disabledAction)) {
				window.disabledAction();
			} else {
				console.error('not defined function.......');
			}			
		});
	});
	
	
	//-------------------------------------------------------------------------------------------
	// focus 
	//-------------------------------------------------------------------------------------------
	
	// 아이디
	$('#loginid').on('focus', function() {
		if ($(this).val() === '') {
			updateUI("id", "loginid.valid.error.emp", "fail");
		}
	});
	
	// 비밀번호
	$('#loginpassword').on('focus', function() {
		if ($(this).val() === '') {
			updateUI("pwd", "password.valid.error.emp", "fail");
			OMNI.auth.setPasswordStrength('password-guide-strength', 'password-guide-msg', -1);
		} 
	});
	
	// 비밀번호 확인
	$('#loginconfirmpassword').on('focus', function() {
		if ($(this).val() === '') {
			updateUI("cpwd", "password.valid.error.emp_re", "fail");
			OMNI.auth.setPasswordStrength('confirmpassword-guide-strength', 'confirm-password-guide-msg', -1);
		} 
	});
	
	//-------------------------------------------------------------------------------------------
	// touchend, keyup 
	//-------------------------------------------------------------------------------------------
	
	$('#loginid').on('touchend, keyup', function(e) {
		checkId(e);
	});
	
	$('#loginpassword').on('touchend, keyup', function(e) {
		checkPwd(e);
		disabledOnOff(); // 가입 버튼 체크
	});
	
	$('#loginconfirmpassword').on('touchend, keyup', function(e) {
		checkConfirmPwd(e);
		disabledOnOff(); // 가입 버튼 체크
	});
	
	
	$('#loginid').on('blur', function() {
		loginIdAvaiable($(this).val().trim());
	});
});

//-------------------------------------------------------
// UI Update Function [ type(id, pwd, cpwd), result(success, fail) ] 
//-------------------------------------------------------
var updateUI = function(type, msgKey, result) {
	
	var inputId = "";
	var guideId = "";
	
	if(type === "id") {
		inputId = "#loginid";
		guideId = "#loginid-guide-msg";
	} else if(type === "pwd") {
		inputId = "#loginpassword";
		guideId = "#password-guide-msg";
	} else if(type === "cpwd") {
		inputId = "#loginconfirmpassword";
		guideId = "#confirm-password-guide-msg";
	}
	
	$(guideId).empty();
	
	if(result === "success") {
		$(inputId).removeClass('is_error').addClass('is_success');
		$(guideId).removeClass('is_error').addClass('is_success');
	} else if(result === "fail"){
		$(inputId).removeClass('is_success').addClass('is_error');
		$(guideId).removeClass('is_success').addClass('is_error');
	}	
	
	$(guideId).html((new Function('return ' + msgKey))()).show();
};

//-------------------------------------------------------
// back-end id check
//-------------------------------------------------------
var loginIdAvaiable = function(id) {
	if (id === '' || typeof id === 'undefined') {
		updateUI("id", "loginid.valid.error.emp", "fail");
		return;
	}		
	
	if (id.length >= 4 && id.length <= 12) {
		if (id === $('#loginpassword').val() || id === $('#loginconfirmpassword').val()) {
			updateUI("id", "loginid.valid.error.include", "fail");
			return;
		}			
		var data = {
			encId: OMNI.auth.encode(OMNIEnv.pprs, id)
		};
		$.ajax({
			url:OMNIEnv.ctx + '/idcheck',
			type:'post',
			data:JSON.stringify(data),
			dataType:'json',
			global: false,
			contentType : 'application/json; charset=utf-8',
			success: function(data) {
				// 비밀번호 input(포커싱 상태) -> id input 영역의 삭제 버튼 클릭시 
				// blur 이벤트로 인해 해당 함수가 call 되면, 삭제 버튼 동작 하기전에 ajax 동작함. 
				// response 받기 전에   btn_del click 이벤트에서 id value 를 지워버려서 guide-msg만 남는 현상이 발생함. 
				// response 에서 length 체크 추가함
				if($('#loginid').val().length > 0) {  
					var msgkey = OMNI.auth.validationdMsgKey('loginid', data.result);
					console.log(msgkey);
					if (data.status === 100) { // success
						updateUI("id", msgkey, "success");
						
						// 비밀번호 + 비밀번호확인 체크
						if( $('#loginpassword').val() !== '' && $('#loginid').val() !== $('#loginpassword').val() ) {
							checkPwd();
						}
						
						if( $('#loginconfirmpassword').val() !== '' && $('#loginid').val() !== $('#loginconfirmpassword').val() ) {
							checkConfirmPwd();
						}
						
					} else {
						updateUI("id", msgkey, "fail");
					}
					
					disabledOnOff();
				}
			},
			error: function() {
			}
		});
	}
};

var checkId = function(e) {
	
	$('#loginid').val($('#loginid').val().trim());	// 공백제거
	// 1. valid check
	var valid = OMNI.auth.validLoginId($('#loginid').val(), {checkPassword:$('#loginpassword').val()});
	var msgkey = OMNI.auth.validationdMsgKey('loginid', valid.key);
	if (valid.code > 0) {
		updateUI("id", msgkey, "success");
		// 로그인 가입버튼 체크는 blur 이벤트 발생시 back-end에서 idcheck 후에 한다.
		
		if(e !== null && e !== undefined) {
			var keycode = e.keyCode || e.which;
			if (keycode === KeyCode.ENTER) {
				$('#loginpassword').focus();
			}
		}
	} else {
		updateUI("id", msgkey, "fail");
	}
};

var checkPwd = function(e) {
	console.log("check pwd");
	
	var valid = OMNI.auth.validPassword($('#loginpassword').val(), {checkId:$('#loginid').val()});
	var msgkey = OMNI.auth.validationdMsgKey('password', valid.key);
	if (valid.code > 0) {
		// UI Update + 강도 체크
		updateUI("pwd", msgkey, "success");
		OMNI.auth.setPasswordStrength('password-guide-strength', 'password-guide-msg', valid.strength);
		
		// 비밀번호 체크 성공시, 비밀번호와 같으면 비밀번호도 성공 체크
		if ( $('#loginpassword').val() === $('#loginconfirmpassword').val() /*&& $('#loginconfirmpassword').hasClass('is_error')*/) {
			msgkey = 'password.valid.success_re';
			updateUI("cpwd", msgkey, "success");
			OMNI.auth.setPasswordStrength('confirm-password-guide-strength', 'confirm-password-guide-msg', valid.strength);
		} else {
			updateUI("cpwd", "password.valid.error.same", "fail");
		}
		
		// 엔터키 대응
		if(e !== null && e !== undefined) {
			var keycode = e.keyCode || e.which;
			if (keycode === KeyCode.ENTER) {
				$('#loginconfirmpassword').focus();
			}	
		}
	} else {
		updateUI("pwd", msgkey, "fail");
		OMNI.auth.setPasswordStrength('password-guide-strength', 'password-guide-msg', valid.strength);
	}
}

var checkConfirmPwd = function(e) {
	console.log("check confirm pwd");
	
	var valid = OMNI.auth.validPassword($('#loginconfirmpassword').val(), {checkId:$('#loginid').val(), confirmId:'loginpassword', isConfirm:true});
	var msgkey = OMNI.auth.validationdMsgKey('password', valid.key);
	if (valid.code > 0) {
		// UI Update + 강도 체크
		updateUI("cpwd", msgkey, "success"); 
		OMNI.auth.setPasswordStrength('confirm-password-guide-strength', 'confirm-password-guide-msg', valid.strength);
		
		// 비밀번호체크가 성공했을때, 비밀번호와 같으면 비밀번호도 성공 체크
		if ( $('#loginconfirmpassword').val() === $('#loginpassword').val() /* && $('#loginpassword').hasClass('is_error')*/) {
			msgkey = 'password.valid.success';
			updateUI("pwd", msgkey, "success");
			OMNI.auth.setPasswordStrength('password-guide-strength', 'password-guide-msg', valid.strength);
		} else {
			
		}
		
		if(e !== null && e !== undefined) {
			var keycode = e.keyCode || e.which;
			if (keycode === KeyCode.ENTER) {
				
				if ($.isFunction(window.executeAction)) {
					window.executeAction();
				} else {
					console.error('not defined function.......');
				}
				
			}
		}
		
	} else {
		if (valid.code < -4) { msgkey = 'password.valid.error.invalid'; }
		
		updateUI("cpwd", msgkey, "fail");
		OMNI.auth.setPasswordStrength('confirm-password-guide-strength', 'confirm-password-guide-msg', valid.strength);
	}
};