
OMNI.auth = {};
(function($, undefined){
	/**
	 * 비.밀.번.호 오류 메시지 
	 */
	this.validationdMsgKey = function(type, code) {
		return {
			'VALID':type + '.valid.success',
			'VALID_RE':type + '.valid.success_re',
			'EMPTY':type + '.valid.error.emp',
			'EMPTY_RE':type + '.valid.error.emp_re',
			'INVALID':type + '.valid.error.invalid',
			'EXIST':type + '.valid.error.exist',
			'SAME':type + '.valid.error.same',
			'INCLUDE':type + '.valid.error.include',
			'SPACEBAR':type + '.valid.error.spacebar',
			'MIN':type + '.valid.error.exceed.min',
			'MAX':type + '.valid.error.exceed.max',
			'UNKNOWN':type + '.valid.error.unknown',
			'WRONG':type + '.valid.error.wrong',
			'USED':type + '.valid.error.used'
		}[code];
	};
	/**
	 * 비.밀.번.호 강도 체크 메시지
	 */
	this.strengthMsgKey = function(code) {
		return {
			'ST-1':'password.strength.impossible',
			'ST0':'password.strength.safe',
			'ST1':'password.strength.normal',
			'ST2':'password.strength.danger'
		}[code];
	}
	/**
	 * Random 문자열 생성
	 */
	this.generateGuid = function() {
		return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
	};
	/**
	 * Random 문자열 가져오기
	 */
	this.getGuid = function (length, special, tmstamp) {
		var iteration = 0;
		var guid = '';
		var randomNumber;
		if(special == undefined){
			var special = false;
		}
		while(iteration < length){
			randomNumber = (Math.floor((Math.random() * 100)) % 94) + 33;
			if(!special){
				if ((randomNumber >=33) && (randomNumber <=47)) { continue; }
				if ((randomNumber >=58) && (randomNumber <=64)) { continue; }
				if ((randomNumber >=91) && (randomNumber <=96)) { continue; }
				if ((randomNumber >=123) && (randomNumber <=126)) { continue; }
			}
			iteration++;
			guid += String.fromCharCode(randomNumber);
		}
		return (tmstamp) ? this.makeTimestamp() + guid : guid;
	};
	/**
	 * UTC 타임스탬프값 생성
	 */
	this.makeTimestamp = function() {
		var d = new Date();
		var y = d.getUTCFullYear(),
		M = d.getUTCMonth() + 1,
		D = d.getUTCDate(),
		h = d.getUTCHours(),
		m = d.getUTCMinutes(),
		s = d.getUTCSeconds(),
		i = d.getUTCMilliseconds(),
		pad = function(x) {
			x = x + '';
			if (x.length === 1) {
				return '0' + x;
			}
			return x;
		};
		return y + pad(M) + pad(D) + pad(h) + pad(m) + pad(s) + i;
	};
	/**
	 * 로그이 아이디 유효성 체크
	 */
	this.validLoginId = function(loginid, options) {
		var settings = $.extend({
			serverCheck:false,
			checkPassword:'',
			length: [4, 12]
		}, options);
		// console.log('loginid : ', loginid);
		if (settings.serverCheck) { // ID 중복체크, keystroke event에서는 사용하면 안됨.
			//console.log('loginid server validation check, ajax call');
			var data = {
				encId: OMNI.auth.encode(OMNIEnv.pprs, loginid)
			};
			var result = {code:-99,key:'UNKNOWN'};
			$.ajax({
				url:OMNIEnv.ctx + '/idcheck',
				type:'post',
				data:JSON.stringify(data),
				async: false,
				dataType:'json',
				contentType : 'application/json; charset=utf-8',
				success: function(data) {
					// console.log(JSON.stringify(data, null, 2));
					if (data.status === 100) {
						result = OMNI.auth.validLoginIdCheck(loginid, settings);
					}
				},
				error: function() {
				}
				
			});
			return result;
		} else {
			return OMNI.auth.validLoginIdCheck(loginid, settings);
		}
	};
	/**
	 * 로그인 아이디 체크
	 */
	this.validLoginIdCheck = function(loginid, options) {
		var re = { 
			eng: /[a-zA-Z]/g, 
			numeric: /[0-9]/g,
			engNum:/^[A-Za-z0-9+]*$/g
		};
		
		if (loginid === '') {
			$('#loginid-guide-msg').empty();
		}
		
		var checkEnglish = (loginid.match(re['eng']) || []).length > 0 ? 1 : 0;
		var checkNumber = (loginid.match(re['numeric']) || []).length > 0 ? 1 : 0; 
		var checkEngNum = (loginid.match(re['engNum']) || []).length > 0 ? 0 : -1; 
		var checkAllCombination = checkEnglish + checkNumber;
		if (loginid === '') {
			return {code:-1,key:'EMPTY'};
		}
		if (checkEngNum === -1) {
			return {code:-2,key:'INVALID'};
		}
		//if(options.checkPassword !== '' && options.checkPassword.search(loginid) > -1) { // 비밀번호에 아이디가 포함
		if(options.checkPassword !== '' && options.checkPassword === loginid) { // 비밀번호에 아이디가 포함		
			return {code:-3,key:'INCLUDE'};
		}
		// spacebar check
		if (/\s/g.test(loginid)) {
			return {code:-4,key:'SPACEBAR'};
		}
		// loginid length check
		if (loginid.length < options.length[0]) {
			return {code:-5,key:'MIN'};
		}
		if (loginid.length > options.length[1]) {
			return {code:-6,key:'MAX'};
		}
		if (checkAllCombination >= 1) {
			return {code:1,key:'VALID'};
		}
		return {code:-99,key:'UNKNOWN'};
	};
	/**
	 * 비밀번호 유효성 확인
	 */
	this.validPassword = function(password, options) {
		var settings = $.extend({
			prePasswordId: 'xxx', // 이전 비밀번호 확인 ID attribute value
			confirmId:'xxx', // 비밀번호 확인 ID attribute value
			checkId:'', // 비밀번호에 아이디가 포함 되었는지 체크용
			serverCheck:false,
			length: [8, 16], 
			spaceBarCheck: true, 
			noSequential: false,
			isConfirm: false // 비밀번호 확인인지
		}, options);
		// console.log('password : ', password);
		if (password === '') {
			var vkey = settings.isConfirm? 'EMPTY_RE' : 'EMPTY'; 
			return {code:-1,key:vkey,strength:-1};
		}
		
		// 비밀번호에 아이디가 포함
		//if(settings.checkId !== '' && password.search(settings.checkId) > -1) { 
		if(settings.checkId !== '' && password === settings.checkId) {
			return {code:-4,key:'INCLUDE',strength:-1};
		}
		
		// 비밀번호와 비밀번호 확인이 같은지 비교
		if (settings.confirmId !== 'xxx') {
			var confirmpwd = $('#' + settings.confirmId).val();
			if(confirmpwd !== undefined && confirmpwd !== null && confirmpwd !== '') {
				if (confirmpwd !== password) {
					return {code:-4,key:'SAME',strength:-1};
				}
			}
		}
		
		// 이전 패스워드와 같은지 비교
		if(settings.prePasswordId !== 'xxx') {
			if( $('#' + settings.prePasswordId).val() === password) {
				return {code:-1,key:'USED',strength:-1}
			}
		}
		
		// 비밀번호 체크
		var result = {code:-99,key:'UNKNOWN',strength:-1};
		
		if (settings.serverCheck) {
			//console.log('loginpassword server validation check, ajax call');
			var data = {
				encPwd: password, // OMNI.auth.encode(OMNIEnv.pprs, password)
				encConfirmPwd: $('#' + settings.confirmId).val()
			};
			
			$.ajax({
				url:OMNIEnv.ctx + '/pwdcheck',
				type:'post',
				data:JSON.stringify(data),
				async: false,
				dataType:'json',
				contentType : 'application/json; charset=utf-8',
				success: function(data) {
					// console.log(JSON.stringify(data, null, 2));
					if (data.status === 100) {
						result = OMNI.auth.validLoginPasswordCheck(password, settings);
					}
				},
				error: function() {
					result.code = data.status;
					result.key = data.result;
				}
			});
		} else {
			result = OMNI.auth.validLoginPasswordCheck(password, settings);
		}
		
		// 비밀번호 확인인지 체크
		if(result.key === 'VALID') {
			if(settings.isConfirm) {
				result.key = 'VALID_RE';
			}
		}
		
		return result;
	};
	
	/**
	 * 로그인 비.밀.번.호 체크
	 * 비.밀.번.호 강도체크 추가
	 */
	this.validLoginPasswordCheck = function(password, options) {
		var re = { 
			lower: /[a-z]/g, 
			upper: /[A-Z]/g,
			numeric: /[0-9]/g, 
			special: /[!"#$%&`()*+,-./:;<=>?@\[\]\\^_'{|}~]/g 
		};
		
		if (password === '') {
			$('#password-guide-msg').empty();
			$('#confirm-password-guide-msg').empty();
		}
		
		var checkUpperEnglish = (password.match(re['upper']) || []).length > 0 ? 1 : 0;
		var checkNumber = (password.match(re['numeric']) || []).length > 0 ? 1 : 0; 
		var checkLowerEnglish = (password.match(re['lower']) || []).length > 0 ? 1 : 0; 
		var checkSpecial = (password.match(re['special']) || []).length > 0 ? 1 : 0;
		
		var passwordLength = password.length;
		if ( checkUpperEnglish == 1 ) {
			return {code:-4,key:'INVALID',strength:-1}; // 대문자는 허용않음.
		}
		
		var removeLowerEnglish = password.replace(re['lower'], ""); // 소문자 제거
		console.log("removeLowerEnglish : " + removeLowerEnglish);
		var removeNumber = removeLowerEnglish.replace(re['numeric'], ""); // 숫자 제거
		console.log("removeNumber : " + removeNumber);
		var removeSpecial = removeNumber.replace(re['special'], ""); // 숫자 제거
		console.log("removeSpecial : " + removeSpecial);
		
		if(removeSpecial != '') { // 허용되지 않은 문자 입력 시
			return {code:-4,key:'INVALID',strength:-1};
		}
		
		// 비밀번호는 영문의 경우 소문자만 허용하도록 해야함.
		//var checkCombination = checkUpperEnglish + checkLowerEnglish + checkNumber + checkSpecial;
		var checkCombination = checkLowerEnglish + checkNumber + checkSpecial;
		
		//if (checkAllCombination === 3) { // 2 가지 조합만 허용, 3가지 조합은 허용않음.
		// 	return {code:-2,key:'INVALID'};
		//}
		
		if(options.checkId !== '' && password === options.checkId) { // 비밀번호에 아이디가 포함		
			return {code:-4,key:'INCLUDE',strength:-1};
		}
		if (password.length < options.length[0]) {
			return {code:-7,key:'MIN',strength:-1};
		}
		if (password.length > options.length[1]) {
			return {code:-8,key:'MAX',strength:-1};
		}
		// spacebar check
		if (options.spaceBarCheck && /\s/g.test(password)) {
			return {code:-6,key:'SPACEBAR',strength:-1};
		}
		
		if (checkCombination < 2 || (options.spaceBarCheck && /\s/g.test(password))) {
			return {code:-3,key:'INVALID',strength:-1}; // 2 가지 조합을 해야 허용
		} else { // password length check
			var passwordStrength = OMNI.auth.getPasswordStrength(checkCombination, password);
			return {code:1,key:'VALID',strength:passwordStrength};
		}
		
		var cp = $('#' + options.confirmId);
		if (cp.length > 0) {
			if (cp.val() !== '' && password !== cp.val()) {
				return {code:-5,key:'SAME',strength:-1};
			}			
		}		
		
		return {code:-99,key:'UNKNOWN',strength:-1};
	};
	this.loginFailNotiMsgInit = function() {
		
		$('#login-noti-msg').empty().hide();
		
	};
	this.loginFailNotiMsg = function(msg) {
		
		$('#login-noti-msg').empty();
		$('#login-noti-msg').addClass('is_error');
		$('#login-noti-msg').html(msg).show();
		
	};
	/**
	 * 로그인 처리
	 */
	this.login = function(options) {
		var settings = $.extend({
			id:'loginForm',
			action:'',
			loginid:'',
			loginpw:'',
			web2AppType:'',
			input:{login: 'username',password:'password',confirmpassword:'confirmpassword'}
		}, options);
		
		OMNI.auth.loginFailNotiMsgInit();
		
	    if ($('#' + settings.id).length > 0) {
	    	$('#' + settings.id).remove();
	    }
		// WSO2로 인증정보(ID/PWD)를 POST로 전송해야 하므로 평문 파라미터로 전송!
		var loginForm = $('<form></form>');
		loginForm.attr('name', settings.id);
		loginForm.attr('id', settings.id);
		loginForm.attr('method', 'post');
		loginForm.attr('action', settings.action);
		loginForm.attr('target', '_self');
		
		var id = OMNI.auth.encode(OMNIEnv.pprs, settings.loginid);
		var pw = OMNI.auth.encode(OMNIEnv.pprs, settings.loginpw);
		
		var data = {
			encId:id,
			encPwd:pw
		};
		$.ajax({
			url:OMNIEnv.ctx + '/statuscheck',
			type:'post',
			data:JSON.stringify(data),
			async: false,
			dataType:'json',
			contentType : 'application/json; charset=utf-8',
			success: function(data) {
				
				console.log('### login status ', JSON.stringify(data, null, 2));
				
				if (data.status <= 0) {
					if (data.status === 0) { // 존재하지 않는 회원.
						
						window.AP_LOGIN_TYPE = '아이디';
						window.AP_LOGIN_FAIL_MESSAGE = '아이디 또는 비밀번호가 맞지 않습니다. (7회 실패 시 접근이 제한됩니다.)';
						dataLayer.push({event: 'login_failed'});
					
						OMNI.auth.loginFailNotiMsg('아이디 또는 비밀번호가 맞지 않습니다. (7회 실패 시 접근이 제한됩니다.)');
					} else if (data.status === -5) { // 잠김
						
						window.AP_LOGIN_TYPE = '아이디';
						window.AP_LOGIN_FAIL_MESSAGE = '로그인 7회 실패하였습니다.';
						dataLayer.push({event: 'login_failed'});
						
						var remainunlocktime = data.remainUnLockTime;
						remainunlocktime = remainunlocktime === '' ? '30:00' : remainunlocktime;
						var timerseconds = data.remainUnLockSeconds;
						if(data.correctPwd==1){
							OMNI.popup.open({
								id:'login-lock-waring',
								timerseconds:timerseconds,
								content: '로그인 연속 실패 이력이 있어서<br/> 접근이 제한된 상태입니다.<br/> 제한 해제를 위해서 본인인증을 해주세요.',
								closelabel:'닫기',
								closeclass:'btn_white',
								oklabel:'본인인증 하기',
								okclass:'btn_blue',
								ok: function() {
									OMNI.popup.close({id:'login-lock-waring'});
									location.href = OMNIEnv.ctx + '/search/pwd?channelCd='+OMNIData.chCd;
								}
							});
						}else{
							OMNI.popup.open({
								id:'login-lock-waring',
								timerseconds:timerseconds,
								//content: '로그인 7회 실패하였습니다.<br/>' + OMNI.auth.formatDisplayTime(remainunlocktime) + ' 후에 다시 시도해주세요.<br/>(<span id="auth-retry-timer">' + remainunlocktime + '</span>)',
								content: '로그인을 계속 실패하여<br/> 접근이 제한 되었습니다.<br/> 비밀번호 찾기를 하여 본인인증 후<br/> 다시 설정해주시기 바랍니다.',
								closelabel:'닫기',
								closeclass:'btn_white',
								oklabel:'비밀번호 찾기',
								okclass:'btn_blue',
								ok: function() {
									OMNI.popup.close({id:'login-lock-waring'});
									location.href = OMNIEnv.ctx + '/search/pwd?channelCd='+OMNIData.chCd;
								}
							});
						}
					} else if (data.status === -10) { // 탈퇴
						
						window.AP_LOGIN_TYPE = '아이디';
						window.AP_LOGIN_FAIL_MESSAGE = '탈퇴된 사용자입니다.';
						dataLayer.push({event: 'login_failed'});
						
						OMNI.auth.loginFailNotiMsg('탈퇴된 사용자입니다.');
					} else if (data.status === -15) { // 비밀번호 초기화 
						location.href = OMNIEnv.ctx + '/mgmt/reset-pwd';
					} else if (data.status === -20) { // 비밀번호 틀림
						
						window.AP_LOGIN_TYPE = '아이디';
						window.AP_LOGIN_FAIL_MESSAGE = '아이디 또는 비밀번호가 맞지 않습니다. (7회 실패 시 접근이 제한됩니다.)';
						dataLayer.push({event: 'login_failed'});
						
						OMNI.auth.loginFailNotiMsg('아이디 또는 비밀번호가 맞지 않습니다. (7회 실패 시 접근이 제한됩니다.)');
					} else if (data.status === -30) { // 비밀번호 캠페인
						
						window.AP_LOGIN_TYPE = '아이디';
						window.AP_LOGIN_FAIL_MESSAGE = '3개월 동안 비밀번호를 변경하지 않으셨습니다.';
						dataLayer.push({event: 'login_failed'});
						
						OMNI.popup.open({
							id:'pwd-change-waring',
							content:'3개월 동안 비밀번호를 변경하지 않으셨습니다.<br/>비밀번호를 변경해주세요.',
							closelabel:'확인',
							closeclass:'btn_blue',
							close:function() { 
								OMNI.popup.close({ id: 'pwd-change-waring' });
								if ($('#i_saveid').prop('checked')) { // 아이디 저장여부 체크
									if ($('#i_autologin').prop('checked')) { // 자동 로그인 체크
										location.href = OMNIEnv.ctx + '/mgmt/pwdcampaign?idSaveOption=Y&autoLoginOption=Y';
									} else {
										location.href = OMNIEnv.ctx + '/mgmt/pwdcampaign?idSaveOption=Y';
									}
									
								} else {
									if ($('#i_autologin').prop('checked')) { // 자동 로그인 체크
										location.href = OMNIEnv.ctx + '/mgmt/pwdcampaign?idSaveOption=N&autoLoginOption=Y';
									} else {
										location.href = OMNIEnv.ctx + '/mgmt/pwdcampaign?idSaveOption=N';
									}
								}
							}
						});	
					} else if (data.status === -40) {
						
						window.AP_LOGIN_TYPE = '아이디';
						window.AP_LOGIN_FAIL_MESSAGE = '아이디 또는 비밀번호가 맞지 않습니다. (7회 실패 시 접근이 제한됩니다.)';
						dataLayer.push({event: 'login_failed'});
						
						OMNI.auth.loginFailNotiMsg('아이디 또는 비밀번호가 맞지 않습니다. (7회 실패 시 접근이 제한됩니다.)');
					} else if (data.status === -50) {
						
						window.AP_LOGIN_TYPE = '아이디';
						window.AP_LOGIN_FAIL_MESSAGE = '뷰티포인트 회원으로 통합된 ' + data.channelName + ' 아이디입니다.';
						dataLayer.push({event: 'login_failed'});
						
						OMNI.auth.loginFailNotiMsg('뷰티포인트 회원으로 통합된 ' + data.channelName + ' 아이디입니다.<br />뷰티포인트 아이디와 비밀번호로 로그인 해주세요.');
					} else if (data.status === -60) { 
						
						window.AP_LOGIN_TYPE = '아이디';
						window.AP_LOGIN_FAIL_MESSAGE = '정보통신망 이용촉진 및 정보보호 등에 관한 법률 제49조의2(속이는 행위에 의한 정보의 수집금지 등)」를 위반한 정황이 확인되어 고객님 계정에 대한 서비스 제공을 중지합니다.';
						dataLayer.push({event: 'login_failed'});
						
						OMNI.popup.open({
							id:'login-lock-access-limit',
							timerseconds:timerseconds,
							content: '「정보통신망 이용촉진 및 정보보호 등에 관한 법률 제49조의2(속이는 행위에 의한 정보의 수집금지 등)」를 위반한 정황이 확인되어 고객님 계정에 대한 서비스 제공을 중지합니다.'
								+ '<br/><br/>고객님은 해당 제한 조치에 대해 이의제기를 할 수 있으며, 이의제기는 아래 연락처를 통해 진행해 주시기 바랍니다.'
								+ '<br/><br/>▪ 아모레퍼시픽 고객센터: 080-023-5454 (수신자요금부담)'
								+ '<br/>(상담시간: 월~금요일 09:00~18:00, 점심 12:00~13:00, 공휴일 제외)',
							closelabel:'확인',
							closeclass:'btn_blue'
						});
					} else {
						
						window.AP_LOGIN_TYPE = '아이디';
						window.AP_LOGIN_FAIL_MESSAGE = '아이디 또는 비밀번호가 맞지 않습니다. (7회 실패 시 접근이 제한됩니다.)';
						dataLayer.push({event: 'login_failed'});
						
						OMNI.auth.loginFailNotiMsg('아이디 또는 비밀번호가 맞지 않습니다. (7회 실패 시 접근이 제한됩니다.)');
					}
				} else {
					
					window.AP_LOGIN_TYPE = '아이디';
					dataLayer.push({event: 'login_success'});
					
					loginForm.append($('<input/>', {type:'hidden', name:settings.input.login, value: id}));
					loginForm.append($('<input/>', {type:'hidden', name:settings.input.password, value: pw}));
					if (settings.sessionkey !== '') { // SSO 처리 시 이 값 없으면 SSO 안됨.
						loginForm.append($('<input/>', {type:'hidden', name:'sessionDataKey', value: settings.sessionkey}));
					}
					if ($('#i_saveid').prop('checked')) { // 아이디 저장여부 체크	 
						loginForm.append($('<input/>', {type:'hidden', name:'idSaveOption', value: 'Y'}));
					}
					if ($('#i_autologin').prop('checked')) { // 자동 로그인 체크
						loginForm.append($('<input/>', {type:'hidden', name:'autoLoginOption', value: 'Y'}));
					}
					if (settings.web2AppType != '') {
						loginForm.append($('<input/>', {type:'hidden', name:'web2AppType', value: settings.web2AppType}));
					}
					
					loginForm.appendTo($('body'));
					loginForm.submit(); // $('#' + settings.id).submit();						
				}
			},
			error: function() {
				//OMNI.popup.error({contents:'로그인 정보를 확인하세요.'});
				OMNI.auth.loginFailNotiMsg('아이디 또는 비밀번호가 맞지 않습니다.');
			}
		});
	};
	/**
	 * 오프라인 매장 로그인 처리
	 */
	this.offlinelogin = function(options) {
		var settings = $.extend({
			id:'loginForm',
			action:'',
			loginid:'',
			loginpw:'',
			loginchcd:'',
			input:{login: 'username',password:'password',confirmpassword:'confirmpassword'}
		}, options);
		
		OMNI.auth.loginFailNotiMsgInit();
		
	    if ($('#' + settings.id).length > 0) {
	    	$('#' + settings.id).remove();
	    }
	    
		var loginForm = $('<form></form>');
		loginForm.attr('name', settings.id);
		loginForm.attr('id', settings.id);
		loginForm.attr('method', 'post');
		loginForm.attr('action', settings.action);
		loginForm.attr('target', '_self');
		
		var id = OMNI.auth.encode(OMNIEnv.pprs, settings.loginid);
		var pw = OMNI.auth.encode(OMNIEnv.pprs, settings.loginpw);
		var chCd = settings.loginchcd;
		loginForm.append($('<input/>', {type:'hidden', name:'chCd', value: chCd}));
		
		var data = {
			encId:id,
			encPwd:pw,
			chCd:chCd
		};
		$.ajax({
			url:OMNIEnv.ctx + '/offline/statuscheck',
			type:'post',
			data:JSON.stringify(data),
			async: false,
			dataType:'json',
			contentType : 'application/json; charset=utf-8',
			success: function(data) {
				
				console.log('### login status ', JSON.stringify(data, null, 2));
				
				if (data.status <= 0) {
					if (data.status === -20) { // 비밀번호 틀림
						
						window.AP_LOGIN_TYPE = '아이디';
						window.AP_LOGIN_FAIL_MESSAGE = '아이디 또는 비밀번호가 맞지 않습니다.';
						dataLayer.push({event: 'login_failed'});
						
						OMNI.auth.loginFailNotiMsg('아이디 또는 비밀번호가 맞지 않습니다.');
					} else {
						
						window.AP_LOGIN_TYPE = '아이디';
						window.AP_LOGIN_FAIL_MESSAGE = '오류가 발생하였습니다.';
						dataLayer.push({event: 'login_failed'});
						
						OMNI.auth.loginFailNotiMsg('오류가 발생하였습니다.');
					}
				} else {
					
					window.AP_LOGIN_TYPE = '아이디';
					dataLayer.push({event: 'login_success'});
					
					loginForm.append($('<input/>', {type:'hidden', name:settings.input.login, value: id}));
					loginForm.append($('<input/>', {type:'hidden', name:settings.input.password, value: pw}));
					
					loginForm.appendTo($('body'));
					loginForm.submit(); // $('#' + settings.id).submit();						
				}
			},
			error: function() {
				//OMNI.popup.error({contents:'로그인 정보를 확인하세요.'});
				OMNI.auth.loginFailNotiMsg('아이디 또는 비밀번호가 맞지 않습니다.');
			}
		});
	};	
	/**
	 * 문자열 암호화
	 * javascript aes encrypt --> java aes decrypt
	 */
	this.encode = function(passphrase, plaintext) {
		var aes = new AesUtil(OMNIEnv.aes.ksz, OMNIEnv.aes.itr);
		return aes.encrypt(OMNIEnv.aes.slt, OMNIEnv.aes.ivs, passphrase, this.generateGuid() + OMNIEnv.sp + plaintext + OMNIEnv.sp + this.makeTimestamp());
	};
	/**
	 * 암호화 값 복호화
	 * java aes encrypt --> javascript aes decrypt
	 */
	this.decode = function(passphrase, ciphertext) {
		var aes = new AesUtil(OMNIEnv.aes.ksz, OMNIEnv.aes.itr);
		return aes.decrypt(OMNIEnv.aes.slt, OMNIEnv.aes.ivs, passphrase, ciphertext);
	};
	/**
	 * 쿠키 설정
	 */
	this.setCookie = function(options) {
		var settings = $.extend({
			cookieName:'one-ap-save-username',
			cookieValue:'',
			expires: 7
		}, options);
		var expdate = new Date();
		expdate.setTime(expdate.getTime() + settings.expires * 24 * 60 * 60 * 1000); // day
		document.cookie = escape(settings.cookieName) + '=' + escape(settings.cookieValue)+";path=/;expires="+expdate.toGMTString();
	};
	/**
	 * 쿠키 조회하기
	 */
	this.getCookie = function(cookieName) {
		cookieName = cookieName || 'one-ap-save-username';
		//var cookieValue = document.cookie.match('(^|;) ?' + cookieName + '=([^;]*)(;|$)');
		//return cookieValue ? unescape(cookieValue[2]) : null;
		var cookieValue = null;
		if (document.cookie) {
	        var cookiearray = document.cookie.split((escape(cookieName)+'='));
	        if(cookiearray.length >= 2){
	            var arraySub = cookiearray[1].split(';');
	            cookieValue = decodeURIComponent(arraySub[0]);
	            cookieValue = unescape(cookieValue);
	        }
	    }
		return cookieValue;
	};
	
	this.checkAutoLoginOption = function() {
		var id = OMNI.auth.getCookie('one-ap-save-username');
		if (id !== '') {
			var autologin = OMNI.auth.getCookie('one-ap-auto-login-' + id);
			if (autologin === 'Y') {
				$('#i_autologin').prop('checked', true);
				//$('#i_saveid').prop('checked', true);
			}
		}
	}
	/**
	 * 쿠키 삭제
	 */
	this.removeCookie = function(cookieName) {
		cookieName = cookieName || 'one-ap-save-username';
		document.cookie = escape(cookieName) + '=;path=/;expires=Thu, 01 Jan 1970 00:00:10 GMT;Max-Age=-99999999;';
	};
	this.autoLoginOption = function() {
		OMNI.auth.checkAutoLoginOption();
		$('#i_autologin').on('change', function() {
			//if ($(this).prop('checked')) {
			//	$('#i_saveid').prop('checked', true);
			//} else {
				//$('#i_saveid').prop('checked', false);
			//}
		});
		$('#i_saveid').on('change', function() {
			//if (!$(this).prop('checked')) {
			//	$('#i_autologin').prop('checked', false);
			//}
		});
	};
	/**
	 * 
	 */
	this.restrict = function() {
		location.href = OMNIEnv.ctx + '/mgmt/restrict?restrict=Y';
	};
	/**
	 * IPIN 결과 처리 후 페이지 이동 처리
	 */
	this.ipincert = function(target) {
		target = target || OMNIEnv.ctx + '/join/step';
		//if ($.isFunction(window.ipinredirect)) {
		//	target = window.ipinredirect();
		//}
		//if (target.indexOf('?') > 0) {
		//	target += '&type=ipin';
		//} else {
		//	target += '?type=ipin'
		//}
		location.href = target;
	};
	/**
	 * KMCIS 결과 처리 후 페이지 이동 처리
	 */
	this.kmciscert = function(target) {
		target = target || OMNIEnv.ctx + '/join/step';
		//if ($.isFunction(window.kmcisredirect)) {
		//	target = window.kmcisredirect();
		//}
		//if (target.indexOf('?') > 0) {
		//	target += '&type=kmcis';
		//} else {
		//	target += '?type=kmcis'
		//}		
		location.href = target;
	};

	/**
	 * 비.밀.번.호 강도 체크 설정
	 * 2 보다크면 -> 위험
	 * 1 이면 -> 보통
	 * 0 이면 -> 안전
	 */
	this.setPasswordStrength = function(id, msgpanel, strength) {
		if (strength > 2) { strength = 2; }
		var msgkey = OMNI.auth.strengthMsgKey('ST' + strength);
		$('#' + msgpanel).append('<span id="' + id + '" class="i_security is_safety">' + (new Function('return ' + msgkey))() + '</span>');
		var arrs = ['is_safety', 'is_normal', 'is_danger', 'is_impossible'];
		$.each(arrs, function(idx, item) {
			$('#' + id).removeClass(item);
		});
		if (strength === -1) {
			$('#' + id).addClass(arrs[3]);
		} else if (strength >= 2) {
			$('#' + id).addClass(arrs[2]);
		} else {
			$('#' + id).addClass(arrs[strength]);	
		}
	};
	/**
	 * 비.밀.번.호 강도 체크 값 얻어오기
	 * 기존 JOIN-ON 비.밀.번.호 체크 로직 유지(서버로직과 동일)
	 */
	this.getPasswordStrength = function(checkCount, password) {
		var strength = 0;
		var sameCount = OMNI.auth.checkSameCount(password); // 동일문자 카운트
		var continueCount = OMNI.auth.checkContinueCnt(password); // 반복된문자 카운트
		var removeDupCount = OMNI.auth.checkRemoveDupCount(password); // 중복제거문자 카운트
		// 1. 4자리 이상 반복/연속한 숫자/문자 포함
		if (sameCount > 2 || continueCount > 2) {
			strength += 1;
		}
		
		// 2. 중복 제거 문자 수 3개 이하
		if (removeDupCount >= 3) {
			strength += 1;
		}
		// 3. 비밀번호 10자리 미만 and 문자조합(영문 대,소문자, 숫자, 특수문자) 중
		// 3개 미만 구성 낮음 이하 레벨은 그냥 사용불가임
		if ((password.length < 10) && (checkCount < 3)) {
			strength += 1;
		}
		// 2 보다크면 -> 위험
		// 1 이면 -> 보통
		// 0 이면 -> 안전
		return strength;
	};
	/**
	 * 비.밀.번.호 동일문자 반복 체크
	 */
	this.checkSameCount = function(password) {
		var cnt_same = 0;
		var max_count = 0;
		for (var i = 0; i < password.length - 1; i++) {
			if (password.substring(i, (i + 1)) === password.substring((i + 1), (i + 2))) {
				cnt_same++;
			} else {
				if (cnt_same > max_count) {
					max_count = cnt_same;
				}
				cnt_same = 0;
			}
		}
		if (cnt_same > max_count) {
			max_count = cnt_same;
		}
		return max_count;
	};
	/**
	 * 비.밀.번.호 반복문자 체크
	 */
	this.checkContinueCnt = function(password) {
		var count = 0;
		var max_cnt = 0;
		for (var i = 0; i < password.length - 1; i++) {
			if (password.charCodeAt(i) === (password.charCodeAt(i + 1) - 1)) {
				count++;
			} else {
				if (count > max_cnt) {
					max_cnt = count;
				}
				count = 0;
			}
		}
		if (count > max_cnt) {
			max_cnt = count;
		}
		return max_cnt;
	};
	/**
	 * 비.밀.번.호 중복제거문자 체크
	 */
	this.checkRemoveDupCount = function(password) {
		var resultArray = [];
		var array = password.split('');
		var dupCount = 0;
		for (var i = 0; i < array.length; ++i) {
			var value = array[i];
			if (value === '') {
				continue;
			}
			if (resultArray.indexOf(value) === -1) {
				resultArray.push(value);    
			} else {
				dupCount++;
			}
		}
		return dupCount;
	};
	/**
	 * WSO2 인증 필수 정보 저장
	 */
	this.setWso2AuthData = function(chcd, values) {
		OMNI.auth.clearWso2AuthData();
		localStorage.removeItem('wso2-chcd' + chcd);
		if (typeof values === 'object') {
			localStorage.setItem('wso2-chcd' + chcd, JSON.stringify(values));	
		}
	};
	/**
	 * WSO2 인증 필수 정보 조회(키별)
	 */
	this.getWso2AuthDataByKey = function(chcd, key) {
		var data = JSON.parse(localStorage.getItem('wso2-chcd' + chcd));
		if (data !== null) {
			return data[key];
		}
		return '';
	};
	/**
	 *  WSO2 인증 필수 정보 조회
	 */
	this.getWso2AuthData = function(chcd) {
		var key = '';
		if (chcd === '') {
			key = OMNI.auth.getWso2AuthKey();
		} else {
			key = 'wso2-chcd' + chcd;
		}
		var data = JSON.parse(localStorage.getItem(key));
		if (data !== null) {
			return data;
		}
		return '{}';
	};
	
	this.getWso2AuthKey = function() {
		for (var i = 0; i < localStorage.length; i++) {
			key = localStorage.key(i);
			if (key.indexOf('030') > 0) {
				continue;
			}
			return key;
		}
		return '';
	};
	
	this.clearWso2AuthData = function() {
		localStorage.clear();
	};
	
	this.formatDisplayTime = function(time) {
		var date = '';
		if (time.indexOf(':') > 0) {
			var arr = time.split(':');
			
			if (arr[0] !== '' && parseInt(arr[0], 10) > 0) {
				date += parseInt(arr[0], 10) + '분 ';
			}
			if (arr[1] !== '' && parseInt(arr[1], 10) > 0) {
				date += parseInt(arr[1], 10) + '초';
			}
		}
		return date;
	};
	
}).call(OMNI.auth = OMNI.auth || {}, jQuery);
