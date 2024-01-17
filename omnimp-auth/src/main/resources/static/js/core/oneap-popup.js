
var authTimer;
var authRetryTimer;

OMNI.popup = {};
(function($, undefined){
	this.processEnd = function(options) {
		var settings = $.extend({
			id:'pop_intg_account_guidance',
			oklabel:'',
			okclass:'btn_blue',
			userid:'',
			ok:function() {
				OMNI.popup.close(options);
			}
		}, options);
		if ($('#' + settings.id).length > 0) {
	    	$('#' + settings.id).remove();
	    }		
		
		var poplayer = '';
		poplayer += '<div class="layer_wrap pop_recent_end" id="' + settings.id + '" tabIndex=0>';
		poplayer += '	<div class="layer_dialog">';
		poplayer += '		<h2 class="layer_title">뷰티포인트 통합 아이디 등록이 완료 되었습니다.<br/>온라인에서 로그인해 주세요.</h2>';
		poplayer += '		<div class="user_info">';
		poplayer += '			<dl class="dt_w33">';
		poplayer += '				<dt>아이디</dt>';
		poplayer += '				<dd>' + settings.userid + '</dd>';
		poplayer += '			</dl>';
		poplayer += '		</div>';
		poplayer += '		<div class="layer_msg">';
		poplayer += '		</div>';
		poplayer += '		<div class="layer_buttons">';
		poplayer += '			<span class="layer_btn">';
		poplayer += '				<button type="button" id="btnOkAction" data-pop="btn-close-pop" data-target="#pop_recent_withdrawals" class="btnA btn_blue" ap-click-area="통합 아이디 등록" ap-click-name="통합 아이디 등록 -확인 버튼 (팝업)" ap-click-data="확인">확인</button>';
		poplayer += '			</span>';
		poplayer += '		</div>';
		poplayer += '	</div>';
		poplayer += '</div>';
		$(poplayer).appendTo($('body'));
	    
	    $('#btnOkAction').on('click', $('body'), function() {
	    	settings.ok();
	    });
	    
	    /*$scrollTop = $(window).scrollTop();
	    $('html').addClass("is-scr-block");
	    $(window).scrollTop(0);
	    $('.wrap').scrollTop($scrollTop);*/
	    $('#' + settings.id).addClass("is_show");   	    
	};
	this.snsPopup = function(options) {
		var settings = $.extend({
			id:'pop_sns_account_interlink_guidance',
			oklabel:'',
			okclass:'btn_blue',
			ok:function() {
				OMNI.popup.close(options);
			}
		}, options);
		if ($('#' + settings.id).length > 0) {
	    	$('#' + settings.id).remove();
	    }
		var poplayer = '';
		poplayer += '<div class="layer_wrap" id="' + settings.id + '" tabIndex=0>';
		poplayer += '  <div class="layer_dialog">';
		if(OMNIData.mappingSnsType == 'AP') {
			poplayer += '    <h2 class="layer_title">애플 계정 연동 안내</h2>';
			poplayer += '    <i class="ico_interlink01"><img src="' + OMNIEnv.ctx + '/images/common/illust_ap.png" alt="sns"></i>';
			poplayer += '<div class="layer_msg">';
			poplayer += '애플 계정 연동을 위해 최초 1회<br />';			
		} else {
			poplayer += '    <h2 class="layer_title">SNS 계정 연동 안내</h2>';
			poplayer += '    <i class="ico_interlink01"><img src="' + OMNIEnv.ctx + '/images/common/illust_sns.png" alt="sns"></i>';
			poplayer += '<div class="layer_msg">';
			poplayer += 'SNS 계정 연동을 위해 최초 1회<br />';
		}
		poplayer += '통합 회원 로그인을 해주시기 바랍니다.<br />';
		poplayer += '(로그인 후 자동으로 연동됩니다.)';
		poplayer += '</div>';
		if (settings.oklabel !== '') {
			poplayer += '    <div class="layer_buttons">';
			poplayer += '        <span class="layer_btn">';
			poplayer += '          <button type="button" id="btnOkAction" data-pop="btn-close-pop" data-target="#pop_sns_account_interlink_guidance" class="btnA btn_blue" ap-click-area="로그인" ap-click-name="로그인 - 확인 버튼 (SNS 계정 연동 안내)" ap-click-data="확인">확인</button>';
			poplayer += '        </span>';
			poplayer += '    </div>';
			poplayer += '  </div>';
		}
		poplayer += '</div>';
		$(poplayer).appendTo($('body'));
	    
	    $('#btnOkAction').on('click', $('body'), function() {
	    	settings.ok();
	    });
	    
	    /*$scrollTop = $(window).scrollTop();
	    $('html').addClass("is-scr-block");
	    $(window).scrollTop(0);
	    $('.wrap').scrollTop($scrollTop);*/
	    $('#' + settings.id).addClass("is_show");   
	};
	// 로그인 더보기
	this.loginWay = function(options) {
		var settings = $.extend({
			id:'',
			title:'',
			mobile:true,
			lastlogin:'basic',
			mtype:'pc',
			oklabel:'',
			okclass:'btn_blue',
			ok:function() {
				console.log('loginWay default ok action, ', options.id);
				OMNI.popup.close(options);
			},
			closelabel:'',
			closeclass:'btn_white',
			close:function() {
				console.log('loginWay default close action, ', options.id);
				OMNI.popup.close(options);
				if (authTimer) {authTimer.stop();}
			}
		}, options);
		
		console.log('loginWay popup id : ', settings.id)
	    if ($('#' + settings.id).length > 0) {
	    	$('#' + settings.id).remove();
	    }
	    var poplayer = '';
	    poplayer += '<div class="layer_wrap" id="' + settings.id + '" tabIndex=0>';
	    poplayer += '  <div class="layer_dialog">';
	    poplayer += '    <h2 class="layer_title">간편 로그인</h2>';
	    poplayer += '    <ul class="simple_login">';
	    
	    var gaArea = '';
	    
	    if (settings.mobile) {
	    	gaArea = '로그인';
		    poplayer += '      <li>';
		    poplayer += '        <button type="button" id="mobile-login_pop" ap-click-area="' + gaArea + '" ap-click-name="' + gaArea + ' - 휴대폰 로그인 버튼 (팝업)" ap-click-data="휴대폰 로그인">';
		    poplayer += '          <i class="ico"><img src="' + OMNIEnv.ctx + '/images/common/btn_login_mobile.png" alt="휴대폰"></i>';
		    poplayer += '          <strong>휴대폰 로그인</strong>';
		    if (settings.lastlogin === 'mobile' && settings.mtype === 'true') {
		    	poplayer += '          <span class="txt">최근 로그인한 계정이에요.</span>';
		    }
		    poplayer += '        </button>';
		    poplayer += '      </li>';
	    } else {
	    	gaArea = '휴대폰 로그인';
	    }
	    
		$('.sns-btn').each(function(idx, elm) {
			var key = $(elm).data('key');
			var val = $(elm).data('val');
			var snsMsgkey = 'sns.' + val + '.title';
		    poplayer += '      <li>';
		    poplayer += '        <button class="sns-btn-way" data-key="' + key + '" data-val="' + val + '" type="button" id="login_' + key.toLowerCase() + '_pop" ap-click-area="' + gaArea + '" ap-click-name="' + gaArea + ' - ' + (new Function('return ' + snsMsgkey))() + ' 로그인 버튼 (팝업)" ap-click-data="' + (new Function('return ' + snsMsgkey))() + ' 로그인">';
		    poplayer += '          <i class="ico"><img src="' + OMNIEnv.ctx + '/images/common/btn_login_' + key.toLowerCase() + '.png" alt="' + key + '"></i>';
		    poplayer += '          <strong>' + (new Function('return ' + snsMsgkey))() + ' 로그인</strong>';
		    if (settings.lastlogin === key.toLowerCase() && settings.mtype === 'true') {
		    	poplayer += '          <span class="txt">최근 로그인한 계정이에요.</span>';
		    }
		    poplayer += '        </button>';
		    poplayer += '      </li>';			
			
		});

	    poplayer += '    </ul>';
	    poplayer += '    <div class="layer_buttons mt20">';
	    if (settings.closelabel !== '') {
		    poplayer += '      <span class="layer_btn">';
		    poplayer += '        <button type="button" data-pop="btn-close-pop" id="btnCloseAction" data-target="#btnCloseAction" ap-click-area="' + gaArea + '" ap-click-name="' + gaArea + ' - 닫기 버튼 (팝업)" ap-click-data="닫기 버튼 (팝업)" class="btnA ' + settings.closeclass + '">' + settings.closelabel + '</button>';
		    poplayer += '      </span>';
	    }	  	    
	    poplayer += '    </div>';
	    poplayer += '  </div>';
	    poplayer += '</div>';
	    
	    $(poplayer).appendTo($('body'));
	    
	    $('#btnOkAction').on('click', $('body'), function() {
	    	settings.ok();
	    });
	    $('#btnCloseAction').on('click', $('body'), function() {
	    	settings.close();
	    	if (authTimer) {authTimer.stop();}
	    });
	    $('#mobile-login_pop').on('click', $('body'), function() {
	    	//var qs = $('#queryString').val();
	    	//qs = qs === '' ? '' : '?' + qs;
			//location.href = OMNIEnv.ctx + '/plogin' + qs;
	    	location.href = OMNIEnv.ctx + '/plogin-param';
	    });
	    $('.sns-btn-way').on('click', $('body'), function() {
	    	var key = $(this).data('key');
	    	var val = $(this).data('val');
	    	var sessiondatakey = $('#sessionDataKey').val();
			if (sessiondatakey === null || sessiondatakey === 'null') {
				OMNI.popup.error({contents:'SSO 정보가 올바르지 않습니다.<br/>다시 접속하시기 바랍니다.'});
				return;
			}
			
			if(key === 'FB') { // Facebook SDK 사용을 위해 Session 값 세팅은 Ajax 호출
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
				document.location = OMNIEnv.ctx + '/sns/login_start?snsType=' + key;
			}
			// document.location = OMNIEnv.ctx + '/sns/login_start?snsType=' + key;
			
			/*
            if(key === 'KA') {
                var url = OMNIEnv.ctx + "/sns/auth/kakao?type=login";
                window.open(url, "_blank","left=0, top=0, toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=yes, width=400, height=800");
            } else {
                document.location = OMNIEnv.ctx + '/sns/login_start?snsType=' + key;
            }
            */
	    });
	    
	    /*$scrollTop = $(window).scrollTop();
	    $('html').addClass("is-scr-block");
	    $(window).scrollTop(0);
	    $('.wrap').scrollTop($scrollTop);*/
	    $('#' + settings.id).addClass("is_show");
	    
	};
	// open layer popup 
	this.open = function(options) {
		var settings = $.extend({
			id:'',
			title:'',
			content:'',
			scroll:false,
			gaArea:'',
			gaOkName:'',
			gaCancelName:'',
			oklabel:'',
			okclass:'btn_blue',
			ok:function() {
				console.log('open default ok action, ', options.id);
				OMNI.popup.close(options);
			},
			closelabel:'Close',
			closeclass:'btn_white',
			close:function() {
				console.log('open default close action, ', options.id);
				OMNI.popup.close(options);
				if (authTimer) {authTimer.stop();}
				if (authRetryTimer) {authRetryTimer.stop();}
			},
			timerseconds: 1800
		}, options);
		
	    if ($('#' + settings.id).length > 0) {
	    	$('#' + settings.id).remove();
	    }
	    var poplayer = '';
	    poplayer += '<div class="layer_wrap" id="' + settings.id + '" tabIndex=0>';
	    poplayer += '  <div class="layer_dialog">';
	    if (settings.title !== '') {
	    	if (settings.scroll) {
	    		poplayer += '    <h2 class="layer_title ta_l">' + settings.title + '</h2>';
	    	} else {
	    		poplayer += '    <h2 class="layer_title">' + settings.title + '</h2>';
	    	}
	    }
	    if (settings.scroll) {
	    	poplayer += '    <div class="scroll_area h340">';
	    } else {
	    	poplayer += '    <div class="layer_msg">';
	    }
	    if (settings.content !== '') {
	    	poplayer += settings.content;
	    }
	    poplayer += '    </div>';
	    if (settings.scroll) {
	    	poplayer += '    <div class="layer_buttons mt20">';
	    } else {
	    	poplayer += '    <div class="layer_buttons">';
	    }
	    var gaTag = '';
	    if (settings.closelabel !== '') {
			if (settings.gaArea !== '') {
				gaTag = 'ap-click-area="' + settings.gaArea + '" ap-click-name="' + settings.gaArea + ' - ' + settings.gaCancelName + '" ap-click-data="' + settings.closelabel + '"'; 
			}
		    poplayer += '      <span class="layer_btn">';
		    poplayer += '        <button type="button" ' + gaTag + ' data-pop="btn-close-pop" id="btnCloseAction" data-target="#btnCloseAction" class="btnA ' + settings.closeclass + '">' + settings.closelabel + '</button>';
		    poplayer += '      </span>';	    	
	    }
	    if (settings.oklabel !== '') {
			if (settings.gaArea !== '') {
				gaTag = 'ap-click-area="' + settings.gaArea + '" ap-click-name="' + settings.gaArea + ' - ' + settings.gaOkName + '" ap-click-data="' + settings.oklabel + '"'; 
			}	    	
	    	poplayer += '      <span class="layer_btn">';
	    	poplayer += '        <button type="button" ' + gaTag + '  data-pop="btn-ok-pop" id="btnOkAction" data-target="#btnOkAction" class="btnA ' + settings.okclass + '">' + settings.oklabel + '</button>';
	    	poplayer += '      </span>';
	    }
	    poplayer += '    </div>';
	    poplayer += '  </div>';
	    poplayer += '</div>';
	    
	    $(poplayer).appendTo($('body'));
	    
	    $('#btnOkAction').on('click', $('body'), function() {
	    	var donext = document.getElementById("do-next");
	    	if(donext){
	    		donext.focus();
	    	}
	    	settings.ok();
	    });
	    $('#btnCloseAction').on('click', $('body'), function() {
	    	settings.close();
	    	if (authTimer) {authTimer.stop();}
	    	if (authRetryTimer) {authRetryTimer.stop();}
	    	
	    });
	    
//	    $scrollTop = $(window).scrollTop();
//	    $('html').addClass("is-scr-block");
//	    $(window).scrollTop(0);
//	    $('.wrap').scrollTop($scrollTop);
	    $('#' + settings.id).addClass("is_show");   
	    
	    // 5번 실패시 타이머
	    if ($('#auth-retry-timer').length > 0) {
	    	var timer_minute = $('#auth-retry-timer').text().split(":")[0];
	    	var timer_second = $('#auth-retry-timer').text().split(":")[1];
    		settings.timerseconds = parseInt(timer_minute * 60) + parseInt(timer_second);
	    	
	    	authRetryTimer = new AuthTimer();
	    	authRetryTimer.interval = settings.timerseconds; //1800;
	    	authRetryTimer.callback = function(){
	    		authRetryTimer.stop();
	    		location.reload(); // 타이머 종료 시 처리기능(현재는 페이지 리로드)
			};
			authRetryTimer.authtimer = setInterval(function(){authRetryTimer.timer()}, 1000);
			authRetryTimer.domId = $("#auth-retry-timer");
	    }
	};
	// open mobile auth
	this.mobileauth = function(options) {
		var settings = $.extend({
			id:'',
			sendno:'',
			gaArea:'',
			gaOkName:'',
			gaCancelName:'',
			content:'휴대폰 인증 후 아이디 존재 시 자동 로그인 됩니다. <br />(인증 5회 실패 시 30분 동안 휴대폰 인증이 제한됩니다.)',
			ok:function() {
				OMNI.popup.close(options);
			},
			close:function() {
				OMNI.popup.close(options);
				if (authTimer) {authTimer.stop();}
			},
			resend:function() {}
		}, options);
	    if ($('#' + settings.id).length > 0) {
	    	$('#' + settings.id).remove();
	    }
		var poplayer = '';
		poplayer += '<div class="layer_wrap mobileauth" id="' + settings.id + '" tabIndex=0>';
		poplayer += '  <div class="layer_dialog">';
		poplayer +='    <div class="input_form input_code">';
		poplayer +='     <p id="sms_send_msg" class="msg_code">인증번호를 전송하였습니다.</p>';
		poplayer +='      <span class="inp" id="sms_wrap">';
		poplayer +='        <input type="tel" id="sms_auth_no" name="sms_auth_no" ap-click-area="' + settings.gaArea + '" ap-click-name="' + settings.gaArea + ' - 인증 번호 입력란 (팝업)" ap-click-data="인증 번호 입력" autocomplete="off" class="inp_text" maxlength="6" placeholder="인증번호를 입력하세요." />';
		poplayer +='        <button type="button" id="btnDelAction" class="btn_del"><span class="blind">삭제</span></button>';
		poplayer +='        <button type="button" id="btnSmsResesnd" ap-click-area="' + settings.gaArea + '" ap-click-name="' + settings.gaArea + ' - 재전송 버튼 (팝업)" ap-click-data="재전송" class="btn_resend">재전송</button>';
		poplayer +='        <span class="timer" id="timer">3:00</span>';
		poplayer +='      </span>';
		poplayer +='      <p class="guide_txt">' + settings.content + '</p>';
		poplayer +='    </div>';
		poplayer +='    <div class="layer_buttons">';
		poplayer +='      <span class="layer_btn">';
		poplayer +='        <button type="button" ap-click-area="' + settings.gaArea + '" ap-click-name="' + settings.gaArea + ' - 인증 취소 버튼 (팝업)" ap-click-data="취소" data-pop="btn-close-pop" data-target="#btnCloseAction" id="btnCloseAction" class="btnA btn_white">취소</button>';
		poplayer +='      </span>';
		poplayer +='      <span class="layer_btn">';
		poplayer +='        <button type="button" ap-click-area="' + settings.gaArea + '" ap-click-name="' + settings.gaArea + ' - 인증 하기 버튼 (팝업)" ap-click-data="인증하기" data-pop="btn-ok-pop" data-target="#btnOkAction" id="btnOkAction" class="btnA btn_blue" disabled>인증하기</button>';
		poplayer +='      </span>';
		poplayer +='    </div>';
		poplayer +='  </div>';
		poplayer +='</div>';	
		
	    $(poplayer).appendTo($('body'));
	    
		$('.inp .inp_text').each(function () {
			$(this).keyup(function () {
				$(this).parent('.inp').find('.btn_del').toggle(Boolean($(this).val()));
			});
		});
	  
		// input clear 2
		$('.inp .btn_del').each(function () {
			var $target_inp = $(this).parent('.inp').find('.inp_text');
			var $guide = $(this).parent('.inp').next($('p'));
			$(this).toggle(Boolean($target_inp.val()));
			$(this).click(function () {
				$target_inp.val('').removeClass('is_error is_success').focus(); // 초기화시 그동안 처리했던 css 모두 삭제
				$(this).hide();
				if ($guide.hasClass('form_guide_txt')) {
					$guide.hide();
				}
				//$('#sendsms').attr('disabled', true);
				//$('#dochangepwd').attr('disabled', true);
			});
		});
	    
	    // start timer
	    authTimer = new AuthTimer();
		authTimer.interval = 180;
		authTimer.callback = function(){
	        // SMS인증발송번호에 해당하는 데이터 삭제
	        // smsSeq input hidden 빈값 -> 재전송 누를때 채워주기
	        OMNI.popup.smsResetSended();
		};
		authTimer.authtimer = setInterval(function(){authTimer.timer()}, 1000);
		authTimer.domId = $("#timer");
	    
	    $('#btnOkAction').on('click', $('body'), function() {
	    	settings.ok();
	    });
	    $('#sms_auth_no').on('paste', $('body'), function(e) {
	    	var data = e.originalEvent.clipboardData.getData('Text');
	    	if (data.length === 6) {
	    		$('#btnOkAction').attr('disabled', false);
	    	} else {
	    		$('#btnOkAction').attr('disabled', true);
	    	}
	    });
	    $('#sms_auth_no').on('touchend, keyup', $('body'), function(e) {
	    	var key = e.which | e.keyCode;
	    	if (key === KeyCode.LEFT || key === KeyCode.RIGHT || key === KeyCode.DELETE) {
	    		return;
	    	} else {
	    		e.target.value = e.target.value.replace(/[^0-9]/g,'');
	    	}
	    	$(this).val($(this).val().trim());
	    	if ($(this).val().length === 6 && $('#smsSeq').val() !== '') {
	    		$('#btnOkAction').attr('disabled', false);
	    	} else {
	    		$('#btnOkAction').attr('disabled', true);
	    	}
	    	var keycode = e.keyCode || e.which;
		    if (!$('#btnOkAction').is(':disabled')) {
		    	if (keycode === KeyCode.ENTER) {
		    		$('#btnOkAction').trigger('click');
		    	}
		    }
	    });
	    $('#sms_auth_no').on('change', $('body'), function(e) {
	    	$(this).val($(this).val().trim());
	    	var data = $(this).val();
	    	if (data.length === 6) {
	    		$('#btnOkAction').attr('disabled', false);
	    	} else {
	    		$('#btnOkAction').attr('disabled', true);
	    	}
	    });
	    $('#btnCloseAction').on('click', $('body'), function() {
	    	settings.close();
	    	if (authTimer) {authTimer.stop();}
	    });
	    $('#btnSmsResesnd').on('click', $('body'), function() {
	    	authTimer.stop();
	    	authTimer.interval = 180;
	    	authTimer.authtimer = setInterval(function(){authTimer.timer()}, 1000);
			authTimer.domId = $("#timer");
			//$('#btnSmsResesnd').attr('disabled', true);
	        $('#sms_send_msg').removeClass('is_error').text('인증번호를 전송하였습니다.');
	    	settings.resend(authTimer);
	    });
	    $('#btnDelAction').on('click', $('body'), function() {
	    	$('#sms_auth_no').val("");
	    });
	    
	    /*$scrollTop = $(window).scrollTop();
	    $('html').addClass("is-scr-block");
	    $(window).scrollTop(0);
	    $('.wrap').scrollTop($scrollTop);*/
	    $('#' + settings.id).addClass("is_show");
	};
	this.channelpopup = function(options) {
		var settings = $.extend({
			id:'channel-popup',
			wtdt:'',
			chname:'',
			ok:function() {
				console.log('channel popup default ok action!!!');
				OMNI.popup.close(options);
			},
		}, options);
		if ($('#' + settings.id).length > 0) {
	    	$('#' + settings.id).remove();
	    }
		
		var poplayer = '';
		poplayer += '<div class="layer_wrap pop_recent_withdrawals" id="' + settings.id + '" tabIndex=0>';
		poplayer += '  <div class="layer_dialog">';
		poplayer += '    <h2 class="layer_title">최근 ' + settings.chname + ' 탈퇴 이력이 있습니다.</h2>';
		poplayer += '    <div class="user_info">';
		poplayer += '      <dl class="dt_w33">';
		poplayer += '        <dt>탈퇴 일자</dt>';
		poplayer += '            <dd>' + settings.wtdt + '</dd>';
		poplayer += '      </dl>';
		poplayer += '    </div>';
		poplayer += '    <div class="layer_msg">';
		poplayer += '    탈퇴 후 30일이 지나면 <br/>다시 ' + settings.chname + ' 회원가입이 가능합니다.';
		poplayer += '    </div>';
		poplayer += '    <div class="layer_buttons">';
		poplayer += '      <span class="layer_btn">';
		poplayer += '        <button type="button" id="btnOkAction" data-pop="btn-close-pop" data-target="#pop_recent_withdrawals" class="btnA btn_blue" ap-click-area="약관동의" ap-click-name="약관동의 -확인 버튼 (팝업)" ap-click-data="확인">확인</button>';
		poplayer += '      </span>';
		poplayer += '    </div>';
		poplayer += '  </div>';
		poplayer += '</div>';
		
		$(poplayer).appendTo($('body'));
		
	    $('#btnOkAction').on('click', $('body'), function() {
	    	settings.ok();
	    });
	    
	    $scrollTop = $(window).scrollTop();
	    $('html').addClass("is-scr-block");
	    $(window).scrollTop(0);
	    $('.wrap').scrollTop($scrollTop);
	    $('#' + settings.id).addClass("is_show");
	};
	// close layer popup
	this.close = function(options) {
		var settings = $.extend({
			id:''
		}, options);
		$('#' + settings.id).queue(function () {
			$(this).removeClass("is_show");
			$(this).dequeue();
			$scrollTop = $(window).scrollTop();
			$('html').removeClass("is-scr-block");
			$(window).scrollTop($scrollTop);
			$('.wrap').scrollTop(0);
		    if ($('#' + settings.id).length > 0) {
		    	$('#' + settings.id).remove();
		    }
		});
		if (authTimer) {authTimer.stop();}
	};
	// sms reset 
	this.smsResetSended = function() {
        var data = {
	        smsSeq: $('#smsSeq').val()
	 	};
		$.ajax({
			url:OMNIEnv.ctx + '/cert/invalidsms',
			type:'post',
			data:JSON.stringify(data),
			dataType:'json',
			contentType : 'application/json; charset=utf-8',
			success: function(data) {
				console.log('smsResetSended ', JSON.stringify(data, null, 2));
				$('#sms_auth_no').val(''); // 인증번호값 비우기
				$('#btnOkAction').attr('disabled', true); // 인증하기 버튼 disable 처리
				$('#smsSeq').val(''); // 초기화 -> 재전송하면 채워줌
				//$('#btnSmsResesnd').attr('disabled', false);
		        $('#sms_send_msg').addClass('is_error').text('인증번호를 다시 전송해주세요.');
			},
			error: function() {
				OMNI.popup.error();
				$('.layer_wrap').focus();
			}
		});
	};
	this.error = function(options) {
		var settings = $.extend({
			id:'error-popup',
			contents:'오류가 발생하였습니다.<br/>잠시후에 다시 시도해주세요.'
		}, options);		
		$('.layer_wrap').each(function(idx, elm) {
			OMNI.popup.close({id: $(elm).attr('id')});	
		});
		
		OMNI.popup.open({
			id:settings.id,
			content:settings.contents,
			closelabel:'닫기',
			closeclass:'btn_blue'
		});						
	};
}).call(OMNI.popup = OMNI.popup || {}, jQuery);
