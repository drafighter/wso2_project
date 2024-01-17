$(document).ready(function() {
		
		OMNI.auth.autoLoginOption();
		
		$('#loginid-guide-msg').removeClass('is_error').hide();  
		$('#password-guide-msg').removeClass('is_error').hide();
		$('#confirm-password-guide-msg').removeClass('is_error').hide();		
		
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
				$('#dologin').attr('disabled', 'disabled');
			});
		});
		
		var username = OMNI.auth.getCookie();
		username = username ? username.trim() : '';
		if (username !== '') {
			$('#loginid').val(username); // 로그인 아이디 쿠키값
			$('#i_saveid').attr('checked', 'checked');
			//$('#loginpassword').focus();
		} else {
			//$('#loginid').focus();
		}
		
		var setloginid = OMNI.auth.getCookie('one-ap-loginid'); // 로그인으로 전달 시 일회 사용
		setloginid = setloginid ? setloginid.trim() : '';
		if (setloginid !== '') {
			$('#loginid').val(setloginid); // 로그인 아이디 쿠키값
			$('#i_saveid').attr('checked', 'checked');
			//$('#loginpassword').focus();
			OMNI.auth.removeCookie('one-ap-loginid'); // 1회 사용이므로 바로 삭제
		} else {
			//$('#loginid').focus();
		}
		
		if (OMNIData.authFailureMsg === 'true') {
			OMNI.popup.open({
				id:'login-fail-waring',
				content:OMNIData.authFailureMsg,
				closelabel:'확인',
				closeclass:'btn_blue',
				close:function() {
					OMNI.popup.close({id:'login-fail-waring'});
				}
			});
			$('.layer_wrap').focus();
		}
		
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
			if (id !== '' && pw !== '') {
				OMNI.auth.login({
					loginid:id,
					loginpw:pw,
					action:OMNIEnv.ctx + '/login/step',
					input:{login:'xid',password:'xpw'}
				});
			} else {
				
				if (id === '' || pw === '') {
					OMNI.auth.loginFailNotiMsg('아이디와 비밀번호를 입력해주세요.');
				}
			}
		});
		// sns click	
		$('.sns-btn').on('click', function(e) {
			
			var urlparameter= location.search;
			var key = $(this).data('key');
			var requestUri = '';
			
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
				requestUri = OMNIEnv.ctx + '/sns/login_start'+urlparameter+ '&snsType=' + key;
				if($('#i_autologin').prop('checked')) {  // 자동 로그인 체크
					requestUri = requestUri + '&autoLoginOption=Y';
				}
				document.location = requestUri;
			}
			
			/*if ($('#i_autologin').prop('checked')) { // 자동 로그인 체크
				if(snsType === 'FB') {
					
				} else {
					document.location = OMNIEnv.ctx + '/sns/login_start?snsType=' + snsType + '&autoLoginOption=Y';
				}
				
			} else {
				document.location = OMNIEnv.ctx + '/sns/login_start'+urlparameter+ '&snsType=' + snsType;
			}*/
            
			/*
            var snsType = $(this).data('key');
            if(snsType === 'KA') {
                var url = OMNIEnv.ctx + "/sns/auth/kakao?type=login";
                window.open(url, "_blank","left=0, top=0, toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=yes, width=400, height=800");
            } else {
                document.location = OMNIEnv.ctx + '/sns/login_start?snsType=' + snsType;
            }
            */
		});
		// search id
		$('#search_id').on('click', function() {
			location.href = OMNIEnv.ctx + '/search/id?channelCd='+OMNIData.chCd;
		});
		// search password
		$('#search_pwd').on('click', function() {
			location.href = OMNIEnv.ctx + '/search/pwd?channelCd='+OMNIData.chCd;
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
		
		OMNI.auth.setWso2AuthData(OMNIData.chCd, Wso2Data);
		
		if (OMNIData.mappingNotice === 'true') {
			OMNI.popup.snsPopup({
				id: "mapping_notice",
				oklabel: "확인"
			});
		}
		
		var web2appStateFocus = false; 
		var web2appId;
		var web2appPopup = null;
		$('#doApplogin').on('click', function(data) {		
			var chNm = $(this).data('chnm');
			var appLink = $(this).data('applink');
			
			OMNI.popup.open({
				id:'applogin_popup',
			 	content:chNm+" 앱을 열겠습니까?",
			 	oklabel:'열기',
				okclass:'btn_blue',
				ok: function() {
					var urlParams = new URL(location.href).searchParams;
					var client_id = urlParams.get('client_id');
					var datajson = {"consumerkey" : client_id};
					web2appPopup = window.open("about:blank", "_blank");
					
					$.ajax({
						url:OMNIEnv.ctx + '/web2App/step',
						type:'post',
						data:JSON.stringify(datajson),
						dataType:'text',
						contentType : 'application/json; charset=utf-8',
						success: function(data) {
							web2appId=data;
							web2appStateFocus = true;
							
							var UserAgent = navigator.userAgent;
							var isMobileIOS = UserAgent.match(/iP(hone|od|ad)/) != null;

							// window.location.href=appLink+data;
							// var web2appPopup = window.open(appLink+encodeURIComponent(data), "_blank","left=0, top=0, toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=yes, width=400, height=800");
							if(web2appPopup != null) {
								web2appPopup.location = appLink+encodeURIComponent(data);
								web2appPopup.focus();
								document.blur();
							}
							
						},
						error: function(e) {
							console.log(e);
						}
					});		
					OMNI.popup.close({id:'applogin_popup'});
					
					var hidden, visibilitychange;
					if (typeof document.hidden !== "undefined") {
						hidden = "hidden";
						visibilitychange = "visibilitychange";
					} else if (typeof document.msHidden !== "undefined") {
						hidden = "msHidden";
						visibilitychange = "msvisibilitychange";
					} else if (typeof document.webkitHidden !== "undefined") {
						hidden = "webkitHidden";
						visibilitychange = "webkitvisibilitychange";
					}
					
					// 이벤트 체크를 위한 이벤트 리스너 등록
					document.addEventListener(visibilitychange, () => {
						if(!document[hidden]) {
							setTimeout(function() {
								web2appStateFocus = false;
								//DB 조회 
								var datajson = {"web2appId" : web2appId};
								$.ajax({
									url:OMNIEnv.ctx + '/web2App/step/check',
									type:'post',
									data:JSON.stringify(datajson),
									dataType:'text',
									contentType : 'application/json; charset=utf-8',
									success: function(data) {
										if(data) {
											location.href = OMNIEnv.ctx + '/login/web2app/callback';
										} else if("failed" === data){
											OMNI.popup.open({
												id:'applogin_result',
												content: '앱 설치 및 로그인<br>여부를 확인 후<br>다시 시도 해주세요.',
												closelabel:'확인',
												closeclass:'btn_blue'
											});
										} else if("cancel" === data) {
											OMNI.popup.open({
												id:'applogin_result',
												content: '앱 로그인이 취소 되었습니다.<br>재시도 하시겠습니까?',
												oklabel:'재시도',
												okclass:'btn_blue',
												ok: function() {
													OMNI.popup.close({id:'applogin_result'});
													$('#doApplogin').click();
												},
											 	closelabel:'취소',
												closeclass:'btn_white',
												close:function() {
													OMNI.popup.close({id:'applogin_result'});
												}
											});
										} else {
											OMNI.popup.open({
												id:'applogin_result',
												content: '앱 설치 및 업데이트와<br>로그인 여부를 확인 후<br>다시 시도 해주세요.',
												closelabel:'확인',
												closeclass:'btn_blue'
											});
										}
									},
									error: function(e) {
										console.log(e);
									}
								});
							}, 500);
						}
					});					
				},
			 	closelabel:'취소',
				closeclass:'btn_white',
				close:function() {
					OMNI.popup.close({id:'applogin_popup'});
				}
		 	});	
		});
		
	});