// ★★★★★★★★★★★★★★★★★★★★★★★
// ★★★★★    해당 script 사용시 주의 사항 
// 
// 로그인 버튼  id = dojoin
// 로그인 버튼 활성화 체크 function name = disabledOnOff() 
// ★★★★★★★★★★★★★★★★★★★★★★★

$(document).ready(function() {
	
	// 안내 메시지 다 지워놓고
	$('#password-guide-msg').removeClass('is_error').hide();
	$('#confirm-password-guide-msg').removeClass('is_error').hide();
	//$('#loginpassword').focus();
	
	// X 버튼 이벤트 등록
	$('.inp .btn_del').each(function () {
		var $target_inp = $(this).parent('.inp').find('.inp_text');
		//var $guide = $(this).parent('.inp').next($('p'));
		$(this).toggle(Boolean($target_inp.val()));
		$(this).click(function () {
			$(this).hide(); // X 버튼 숨기고
			
			$target_inp.val('').focus(); // 해당 Input 에 포커싱
			
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
	
	$('#loginpassword').on('touchend, keyup', function(e) {
		checkPwd(e);
		disabledOnOff(); // 가입 버튼 체크
	});
	
	$('#loginconfirmpassword').on('touchend, keyup', function(e) {
		checkConfirmPwd(e);
		disabledOnOff(); // 가입 버튼 체크
	});
	
	
});

//-------------------------------------------------------
// UI Update Function [ type(id, pwd, cpwd), result(success, fail) ] 
//-------------------------------------------------------
var updateUI = function(type, msgKey, result) {
	
	var inputId = "";
	var guideId = "";
	
	if(type === "pwd") {
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

var checkPwd = function(e) {
	console.log("check pwd");
	
	var valid = OMNI.auth.validPassword($('#loginpassword').val(), {checkId:$('#loginid').val()});
	var msgkey = OMNI.auth.validationdMsgKey('password', valid.key);
	if (valid.code > 0) {
		// UI Update + 강도 체크
		updateUI("pwd", msgkey, "success");
		OMNI.auth.setPasswordStrength('password-guide-strength', 'password-guide-msg', valid.strength);
		
		// 비밀번호 체크 성공시, 비밀번호와 같으면 비밀번호도 성공 체크
		if ( $('#loginpassword').val() === $('#loginconfirmpassword').val() && $('#loginconfirmpassword').hasClass('is_error')) {
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
	
	var valid = OMNI.auth.validPassword($('#loginconfirmpassword').val(), {confirmId:'loginpassword', isConfirm:true, checkId:$('#loginid').val()});
	var msgkey = OMNI.auth.validationdMsgKey('password', valid.key);
	if (valid.code > 0) {
		// UI Update + 강도 체크
		updateUI("cpwd", msgkey, "success"); 
		OMNI.auth.setPasswordStrength('confirm-password-guide-strength', 'confirm-password-guide-msg', valid.strength);
		
		// 비밀번호체크가 성공했을때, 비밀번호와 같으면 비밀번호도 성공 체크
		if ( $('#loginconfirmpassword').val() === $('#loginpassword').val() && $('#loginpassword').hasClass('is_error')) {
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
}