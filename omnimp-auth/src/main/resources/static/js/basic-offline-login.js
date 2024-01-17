$(document).ready(function() {
		
		$('.inp .btn_del').each(function () {
			var $target_inp = $(this).parent('.inp').find('.inp_text');
			//var $guide = $(this).parent('.inp').next($('p'));
			$(this).toggle(Boolean($target_inp.val()));
			$(this).click(function () {
				$target_inp.empty().removeClass('is_error is_success')/*.focus()*/;
				$(this).hide();
				$('#login-noti-msg').empty();
				$('#login-noti-msg').removeClass('is_success is_error');
				$('#login-noti-msg').hide();				
				//$guide.hide();
				$('dologinn').attr('disabled', 'disabled');
			});
		});
		
		/**
		 * 로그인 아이디 key-in
		 */		
		$('#loginid').on('touchend change click keyup input paste', function(e) {
			$(this).val($(this).val().trim());
			if ($(this).val() !== '') {
				OMNI.auth.loginFailNotiMsgInit();
				var pwd = $('#loginpassword').val().trim();
				if (pwd === '') {
					$('#dologin').attr('disabled', 'disabled');
					$('#login-noti-msg').empty();
					$('#login-noti-msg').removeClass('is_success is_error');
					$('#login-noti-msg').hide();
				} else {
					$('#dologin').removeAttr('disabled');
					var keycode = e.keyCode || e.which;
					if (keycode === KeyCode.ENTER) {
						$('#dologin').trigger('click');
					}
				}
				var keycode = e.keyCode || e.which;
				if (keycode === KeyCode.ENTER) {
					//$('#loginpassword').focus();
				}
			} else {
				$('#dologin').attr('disabled', 'disabled');
			}
		});
		
		/**
		 * 비.밀.번.호 key-in
		 */		
		$('#loginpassword').on('touchend change click keyup input paste', function(e) {
			var id = $('#loginid').val().trim();
			if (id !== '' && $(this).val() !== '') {
				OMNI.auth.loginFailNotiMsgInit();
				$('#dologin').removeAttr('disabled');
				var keycode = e.keyCode || e.which;
				if (keycode === KeyCode.ENTER) {
					$('#dologin').trigger('click');
				}	
			} else {
				$('#login-noti-msg').empty();
				$('#login-noti-msg').removeClass('is_success is_error');
				$('#login-noti-msg').hide();
				$('#dologin').attr('disabled', 'disabled');
			}
		});
		
		// login submit	
		$('#dologin').on('click', function() {
			var id = $('#loginid').val().trim();
			var pw = $('#loginpassword').val();
			var chCd = $('#chCd').val();
			if (id !== '' && pw !== '') {
				OMNI.auth.offlinelogin({
					loginid:id,
					loginpw:pw,
					loginchcd:chCd,
					action:OMNIEnv.ctx + '/offline/login/step',
					input:{login:'xid',password:'xpw',chCd:'chCd'}
				});
			} else {
				
				if (id === '' || pw === '') {
					OMNI.auth.loginFailNotiMsg('아이디와 비밀번호를 입력해주세요.');
				}
			}
		});
	});