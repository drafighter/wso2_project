	  $(document).ready(function() {
		  
		//$('#name').focus();
		  
		// 이름 key in  
		$('#name').on('touchend, keyup', function(e) {
			var numexp = /^[0-9]*$/;
			var name = $(this).val();
			var phone = $('#phone').val();
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
		// 휴대폰 key in 
		$('#phone').on('touchend, keyup', function(e) {
		    var key = e.which || e.keyCode;
		    if (key === KeyCode.LEFT || key === KeyCode.RIGHT || key === KeyCode.DELETE) {
		    	return;
		    } else {
		    	e.target.value = e.target.value.replace(/[^0-9]/g,'');
		    }
		    var name = $('#name').val();
		    var phone = e.target.value;
		    var phoneexp = /^01(?:0|1|[6-9])(?:\d{3}|\d{3,4})\d{3,4}$/;
		    if (name.length > 1 && phone.length > 9 && (phoneexp.test(phone))) {
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
				//userName: OMNI.auth.encode(OMNIEnv.pprs, $('#name').val()),
				userName: $('#name').val(),	
				userPhone: $('#phone').val(),
				searchId: true,
				phoneLogin: true
			};
			// SMS 발송
			$.ajax({
				url:OMNIEnv.ctx + '/cert/sendsms',
				type:'post',
				data:JSON.stringify(params),
				dataType:'json',
				contentType : 'application/json; charset=utf-8',
				success: function(data) {
					console.log('sendsms ', JSON.stringify(data, null, 2));
					$('#smsSeq').val(data.smsAthtSendNo);
					if (data.status === 1) {
						// SMS 발송 성공이면 호출
						OMNI.popup.mobileauth({
							id:'mobileauth', 
							sendno:$('#smsSeq').val(),
							gaArea:'휴대폰 로그인',
							ok: function() {
								data.smsAthtNoVl = $('#sms_auth_no').val();
								data.smsNo = $('#sms_auth_no').val();
								data.name = OMNI.auth.encode(OMNIEnv.pprs, $('#name').val());
								data.phoneNo = OMNI.auth.encode(OMNIEnv.pprs, data.phoneNo);
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
							content: '휴대폰 인증 로그인을 할 수 없는<br/>회원 정보 입니다.<br/>가입한 아이디로 로그인 하거나,<br/>회원가입 여부를 확인해주세요.',
							closelabel:'확인',
							closeclass:'btn_blue'
						});
					} else if (data.status === -10) { // 고객정보 없음
						OMNI.popup.open({
							id:'phone-waring',
							content: '가입한 회원이 아니거나,<br/>입력하신 정보가 일치하지 않습니다.',
							closelabel:'확인',
							closeclass:'btn_blue'
						});
					} else if (data.status === -15) { // 5번 틀린지 30분 경과되지 않음.
						OMNI.popup.open({
							id:'phone-retry-waring',
							content: '인증번호 5회 오류 입니다.<br/>' + data.times + ' 후에 다시 시도해주세요.(<span id="auth-retry-timer">' + data.times + '</span>)',
							closelabel:'확인',
							closeclass:'btn_blue'
						});		
					} else if (data.status === -20) { // 인증 결과 통합 고객번호, 아이디 중복인 경우
						OMNI.popup.open({
							id:'phone-retry-waring',
							content: '휴대폰 인증 로그인을 할 수 없는<br/>회원 정보 입니다.<br/>가입한 아이디로 로그인 하거나,<br/>회원가입 여부를 확인해주세요.',
							closelabel:'확인',
							closeclass:'btn_blue'
						});	
					} else if (data.status === -25) { // 탈퇴회원
						OMNI.popup.open({
							id:'phone-waring',
							content: '휴대폰 인증 로그인을 할 수 없는<br/>회원 정보 입니다.<br/>가입한 아이디로 로그인 하거나,<br/>회원가입 여부를 확인해주세요.',
							closelabel:'확인',
							closeclass:'btn_blue'
						});
					} else {
						OMNI.popup.open({
							id:'phone-waring',
							content: '가입한 회원이 아니거나,<br/>입력하신 정보가 일치하지 않습니다.',
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
		
		// sns click	
		$('.sns-btn').on('click', function(e) {
			var urlparameter= location.search;
			var key = $(this).data('key');
			
			if(key === 'FB') {
				$.ajax({
					url:OMNIEnv.ctx + '/sns/auth/facebook',
					type:'post',
					dataType:'json',
					contentType : 'application/json; charset=utf-8',
					success: function(data) {
						// Android App 에서 이슈 해결을 위해 분기 처리
						var UserAgent = navigator.userAgent;
						var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
						var isAndroidApp = UserAgent.match(/APTRACK_ANDROID/i) != null;
						if(isMobile && isAndroidApp) { // Android App 일 경우 로그인 팝업 호출 (User Agent에 APTRACK_ANDROID 포함인 경우)
							FB.login(function(response) {
								if (response.status === 'connected') {
									location.href=data.callback + '?accessToken=' + response.authResponse.accessToken;
								}
							}, {scope: 'public_profile', return_scopes: true});	
						} else {
							FB.getLoginStatus(function(response) {
								if (response.status === 'connected') {
									location.href=data.callback + '?accessToken=' + response.authResponse.accessToken;
								} else {
									location.href = encodeURI('https://www.facebook.com/dialog/oauth?client_id='+data.restApiKey+'&redirect_uri='+data.callback);
								}
							}, {scope: 'public_profile', return_scopes: true});							
						}	
					},
					error: function() {
						OMNI.popup.error({contents:'오류가 발생하였습니다.'});
					}
					
				});				
			} else if (key === 'AP') {
				$.ajax({
					url:OMNIEnv.ctx + '/sns/auth/apple',
					type:'post',
					dataType:'json',
					contentType : 'application/json; charset=utf-8',
					success: function(data) {
						location.href = encodeURI('https://appleid.apple.com/auth/authorize?client_id='+data.restApiKey+'&redirect_uri='+data.callback+'&state='+data.state+'&response_type=code id_token&response_mode=form_post&scope=name');
					},
					error: function() {
						OMNI.popup.error({contents:'오류가 발생하였습니다.'});
					}
					
				});
			} else {
				location.href = OMNIEnv.ctx + '/sns/login_start?snsType=' + $(this).data('key');
			}
			// location.href = OMNIEnv.ctx + '/sns/login_start?snsType=' + $(this).data('key');
		});
		// search id
		$('#search_id').on('click', function() {
			//location.href = OMNIEnv.ctx + '/search/id' + OMNIData.loginQueryString;
			location.href = OMNIEnv.ctx + '/search/id';
		});
		// search password
		$('#search_pwd').on('click', function() {
			//location.href = OMNIEnv.ctx + '/search/pwd' + OMNIData.loginQueryString;
			location.href = OMNIEnv.ctx + '/search/pwd';
		});		
		// search guest order
		$('#search_order').on('click', function() {
			var UserAgent = navigator.userAgent;
			var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
			
			if(!isMobile && Wso2Data.popup == 'true') {
				var orderUrl = $('#orderUrl').val();
				if (orderUrl !== '') {
					opener.location.href = orderUrl;
				}
				window.close();	
			} else {
				var orderUrl = $('#orderUrl').val();
				if (orderUrl !== '') {
					location.href = orderUrl;
				}				
			}
		});	
		$(".btn_join_membership").on('click', function() {
			location.href = OMNIEnv.ctx + '/go-join-param';
		});		
		
	  });
	  
	  // sms 인증하기
	  var authSms = function(params) {
		params.smsAthtSendNo = $('#smsSeq').val();  
		$('#btnOkAction').attr('disabled', true);
		console.log('authSms ', JSON.stringify(params, null, 2));
		$.ajax({
			url:OMNIEnv.ctx + '/cert/authsms',
			type:'post',
			data:JSON.stringify(params),
			dataType:'json',
			contentType : 'application/json; charset=utf-8',
			success: function(data) {
				console.log('authsms : ', JSON.stringify(data, null, 2));
				if (data.status === 1) { // SMS 인증 성공이면 호출
					if (authTimer) {authTimer.stop();}
					
					window.AP_LOGIN_TYPE = '휴대폰';
					dataLayer.push({event: 'login_success'});					
					
					OMNI.popup.close({id:'mobileauth'}); // 휴대폰 인증 창 닫기
					OMNI.popup.open({
						id:'phone-ipin-success',
						content: '인증이 완료되었습니다.',
						closelabel:'확인',
						closeclass:'btn_blue',
						close:function() {
							OMNI.popup.close({ id: 'phone-ipin-success' });
							location.href = OMNIEnv.ctx + '/plogin/step';		
						}
					});
					
					
				} else if (data.status === -15) { // 5회 제한 초과
					
					window.AP_LOGIN_TYPE = '휴대폰';
					window.AP_LOGIN_FAIL_MESSAGE = '인증번호 5 회 오류 입니다.';
					dataLayer.push({event: 'login_failed'});					
					
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
			searchId: true,
			phoneLogin: true
		};		  
		$.ajax({
			url:OMNIEnv.ctx + '/cert/sendsms',
			type:'post',
			data:JSON.stringify(data),
			dataType:'json',
			contentType : 'application/json; charset=utf-8',
			success: function(data) {
				console.log(JSON.stringify(data, null, 2));
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