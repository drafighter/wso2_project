jQuery.i18n.properties({ 
	name:'message', 
	path:OMNIEnv.ctx + '/messages/', 
	mode:'both', 
	language:'ko', 
	checkAvailableLanguages: true,
	callback: function () { 
		// console.log(sample.message1);
	}
});

$(document).ready(function() {
	// disable drag
	$(document).on('mousedown', function(e) { $(document).on('mousemove', function(e) { if (e.target.nodeName !== 'INPUT' && e.target.nodeName !== 'TEXTAREA') { e.preventDefault(); return false; } }); });   
	// disable context menu
	$(document).on('contextmenu', function(e) { if (e.target.nodeName !== 'INPUT' && e.target.nodeName !== 'TEXTAREA') { e.preventDefault(); return false; } });
	// disable copy event
	$(document).key('ctrl+a', function(e) { if (e.target.nodeName !== 'INPUT' && e.target.nodeName !== 'TEXTAREA') { e.preventDefault(); return false; } });
	$(document).key('ctrl+r', function(e) { e.preventDefault(); return false; });
	$(document).key('ctrl+shift+r', function(e) { e.preventDefault(); return false; });
	$(document).key('f5', function(e) { e.preventDefault(); return false; });
	// 로딩바 시작
    $(document).ajaxStart(function () {
    	OMNI.loading.show();
    });
    // 로딩바 닫기
    $(document).ajaxStop(function () {
    	OMNI.loading.hide();
    });
    
	// PC 키보드 보기
	$('.btn_keyboard').on('click', function () {
		$keyboardBox = $('.view_keyboard');
		var isCheck = $keyboardBox.hasClass('is_open');
		if (isCheck) {
			$keyboardBox.removeClass('is_open');
		} else {
			$keyboardBox.addClass('is_open');
		}
	});
    
	// input clear 1
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
			$guide.hide();
			$('#sendsms').attr('disabled', true);
			$('#dochangepwd').attr('disabled', true);
		});
	});
	
	// notice toggle 1
	$('.btn_open_info').on('click', function () {
		var $layer_T = $(this).parent('.info_notice').find('.layer_info'),
		isShowCheck = $layer_T.is(':visible');
		if (!isShowCheck) {
			$layer_T.stop().show();
		} else {
			$layer_T.stop().hide();
		}
	});

	// notice toggle 2
	$('.close_layer_info').on('click', function () {
		$(this).parent('.layer_info').stop().hide();
	});
	
	// all agree view
	$('.btn_all_view').on('click', function () {
		var $all_T = $(this).parents('.all_agree_box'),
		isViewCheck = $all_T.hasClass('is_open');
		if (!isViewCheck) {
			$all_T.addClass('is_open');
		} else {
			$all_T.removeClass('is_open');
		}
	});
	
	
	// tooltip close
	$(".btn_tooltip_close").on("click", function () {
		$(this).parent(".login_tootip").hide();
	});
	
	// 휴대폰 로그인
	$('#mobile-login').on('click', function() {
		location.href = OMNIEnv.ctx + '/plogin-param';
	});
	
	// 휴대폰 로그인 더보기
	$('#more-login-phone').on('click', function() {
		OMNI.popup.loginWay({
			id: 'more_login-phone', 
			mobile:false,
			lastlogin:OMNIData.lastlogin,
			closelabel:'닫기',
			closeclass:'btn_blue'
		});
	});
	
	// 로그인 더보기
	$('#more-login').on('click', function() {
		OMNI.popup.loginWay({
			id: 'more_login', 
			mobile:true, 
			lastlogin:OMNIData.lastlogin,
			closelabel:'닫기',
			closeclass:'btn_blue'
		});
	});	

});

// ESC 팝업닫기(휴대폰 인증일 경우는 닫지 않음)
$(document).on('keyup',function(e) {
	var code = e.keyCode || e.which;
    if(code === KeyCode.ESCAPE) { 
    	if ($('.layer_wrap').length > 0 && $('.mobileauth').length <= 0) {
    		$('.layer_wrap').each(function(idx, elm) {
    			var layerid = $(elm).attr('id');
    			// OMNI.popup.close({id : layerid}); // 예외가 많아 처리하지 않는 방향으로...
    		});
    	}
    	if (authRetryTimer) {authRetryTimer.stop();}
    }
});

// 로딩처리
OMNI.loading = {};
(function($, undefined){
	this.show = function(type) {
		type = (type === null || typeof(type) === 'undefined') ? 'loading' : type;
		var loadinglayer = '<div class="' + type + '" style="display: block;"></div>';
		if ($('.' + type).length > 0) {
			return;
	    }
		$(loadinglayer).appendTo($('body'));
	};
	this.hide = function(type) {
		type = (type === null || typeof(type) === 'undefined') ? 'loading' : type;
		if ($('.' + type).length > 0) {
			$('.' + type).remove();
	    }
	};
	
}).call(OMNI.loading = OMNI.loading || {}, jQuery);

// Timer function
function AuthTimer(){ };

AuthTimer.prototype = {
    interval : ''
    ,callback : function(){}
    ,authtimer : ''
    ,domId: ''
    ,timer: function() {
    	var min = Math.floor(this.interval / 60);
    	// min = min < 10 ? '0' + min : min;
    	var sec = (this.interval % 60);
    	sec = sec < 10 ? '0' + sec : sec
        var m = min + ':' + sec;	// 남은 시간 계산
        this.interval--;			// 1초씩 감소
        // this.domId.innerText = m;
        this.domId.text(m);
        if (this.interval < 0) {			// 시간이 종료 되었으면..
            clearInterval(this.authtimer);	// 타이머 해제
            this.callback();
        }
    },
    stop: function(){
        clearInterval(this.authtimer);
    }    
};

(function($){
    $.fn.serializeObject = function(){
        var self = this,
            json = {},
            push_counters = {},
            patterns = {
                "validate": /^[a-zA-Z][a-zA-Z0-9_]*(?:\[(?:\d*|[a-zA-Z0-9_]+)\])*$/,
                "key":      /[a-zA-Z0-9_]+|(?=\[\])/g,
                "push":     /^$/,
                "fixed":    /^\d+$/,
                "named":    /^[a-zA-Z0-9_]+$/
            };
        this.build = function(base, key, value){
            base[key] = value;
            return base;
        };
        this.push_counter = function(key){
            if(push_counters[key] === undefined){
                push_counters[key] = 0;
            }
            return push_counters[key]++;
        };
        $.each($(this).serializeArray(), function(){
            // Skip invalid keys
            if(!patterns.validate.test(this.name)){
                return;
            }
            var k,
                keys = this.name.match(patterns.key),
                merge = this.value,
                reverse_key = this.name;
            while((k = keys.pop()) !== undefined){
                // Adjust reverse_key
                reverse_key = reverse_key.replace(new RegExp("\\[" + k + "\\]$"), '');
                // Push
                if(k.match(patterns.push)){
                    merge = self.build([], self.push_counter(reverse_key), merge);
                }
                // Fixed
                else if(k.match(patterns.fixed)){
                    merge = self.build([], k, merge);
                }
                // Named
                else if(k.match(patterns.named)){
                    merge = self.build({}, k, merge);
                }
            }
            json = $.extend(true, json, merge);
        });
        return json;
    };
})(jQuery);

String.prototype.trim = function() { 
	return this.replace(/^\s+|\s+$/g,""); 
};

/* string java type endsWith - start */
String.prototype.endsWith = function (suffix) {
	return (this.substr(this.length - suffix.length) === suffix);
};
/* string java type endsWith - end */

/* string java type startsWith - start */
String.prototype.startsWith = function(prefix) {
	return (this.substr(0, prefix.length) === prefix);
};
/* string java type startsWith - end */

/* utf-8 byte size */

var calcByte = {
	getByteLength : function(s) {
		if (s == null || s.length == 0) {
			return 0;
		}
		var size = 0;
		for ( var i = 0; i < s.length; i++) {
			size += this.charByteSize(s.charAt(i));
		}
		return size;
	},
	cutByteLength : function(s, len) {
		if (s == null || s.length == 0) {
			return 0;
		}
		var size = 0;
		var rIndex = s.length;
		for ( var i = 0; i < s.length; i++) {
			size += this.charByteSize(s.charAt(i));
			if( size == len ) {
				rIndex = i + 1;
				break;
			} else if( size > len ) {
				rIndex = i;
				break;
			}
		}
		return s.substring(0, rIndex);
	},
	charByteSize : function(ch) {
		if (ch == null || ch.length == 0) {
			return 0;
		}
		var charCode = ch.charCodeAt(0);
		if (charCode <= 0x00007F) {
			return 1;
		} else if (charCode <= 0x0007FF) {
			return 2;
		} else if (charCode <= 0x00FFFF) {
			return 3;
		} else {
			return 4;
		}
	}
};

var KeyCode = {
    BACKSPACE: 8,
    COMMA: 188,
    DELETE: 46,
    DOWN: 40,
    END: 35,
    ENTER: 13,
    ESCAPE: 27,
    HOME: 36,
    LEFT: 37,
    CTRL: 17,
    NUMPAD_ADD: 107,
    NUMPAD_DECIMAL: 110,
    NUMPAD_DIVIDE: 111,
    NUMPAD_ENTER: 108,
    NUMPAD_MULTIPLY: 106,
    NUMPAD_SUBTRACT: 109,
    PAGE_DOWN: 34,
    PAGE_UP: 33,
    PERIOD: 190,
    RIGHT: 39,
    SPACE: 32,
    TAB: 9,
    UP: 38,
    F5: 116,
    R: 82
};
