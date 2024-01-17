<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- ME-FO-A0301 -->
<html lang="ko">
<head>
  <title>회원정보 관리 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	
	$('#currpassword-guide-msg').hide();
	$('#newpassword-guide-msg').hide();
	$('#newconfirmpassword-guide-msg').hide();
	
	$('.inp .btn_del').each(function () {
		var $target_inp = $(this).parent('.inp').find('.inp_text');
		var $guide = $(this).parent('.inp').next($('p'));
		$(this).toggle(Boolean($target_inp.val()));
		$(this).click(function () {
			$target_inp.val('').removeClass('is_error is_success')/*.focus()*/; // 초기화시 그동안 처리했던 css 모두 삭제
			$(this).hide();
			$(this).focus(); // focus 이벤트 방지
			$guide.hide();
			disabledOnOff();
		});
	});
	
	$('#currentpassword').on('touchend, keyup', function(e) {
        if ($(this).val() !== '') {
        	var keycode = e.keyCode || e.which;	
    		if (keycode === KeyCode.ENTER) {
    			if ($('#loginpassword').length > 0) {
    				$('#loginpassword').focus();
    			}
    		}
        }
  	});
	
	$('#currentpassword').on('blur', function(){
		checkCurrPwd();
	});

	$('#loginpassword').on('focus', function(){
		if ($(this).val() === '') {
			$('#loginpassword').removeClass('is_success').addClass('is_error');
			$('#newpassword-guide-msg').empty();
			$('#newpassword-guide-msg').removeClass('is_success').addClass('is_error');
			$('#newpassword-guide-msg').html((new Function('return password.valid.error.emp'))()).show();
			OMNI.auth.setPasswordStrength('newpassword-guide-strength', 'newpassword-guide-msg', -1);
		}
	});
	
	$('#loginconfirmpassword').on('focus', function(){
		if ($(this).val() === '') {
			$('#loginconfirmpassword').removeClass('is_success').addClass('is_error');
			$('#newconfirmpassword-guide-msg').empty();
			$('#newconfirmpassword-guide-msg').removeClass('is_success').addClass('is_error');
			$('#newconfirmpassword-guide-msg').html((new Function('return password.valid.error.emp_re'))()).show();
			OMNI.auth.setPasswordStrength('newconfirmpassword-guide-strength', 'newconfirmpassword-password-guide-msg', -1);
		}
	});
	
	$('#loginpassword').on('touchend, keyup', function(e) {
		checkPwd(e);
		disabledOnOff();
	});
		
	$('#loginconfirmpassword').on('touchend, keyup', function(e) {
		checkConfirmPwd(e);
		disabledOnOff();
	});		
		
	$('#dochangepwd').on('click', function() {
		$("#isEncryption").val('');
		
		var data = {
			cpw: $('#currentpassword').val(), //OMNI.auth.encode(OMNIEnv.pprs, $('#currentpassword').val()),
			npw: $('#loginpassword').val(), //OMNI.auth.encode(OMNIEnv.pprs, $('#loginpassword').val()),
			ncpw: $('#loginconfirmpassword').val() //OMNI.auth.encode(OMNIEnv.pprs, $('#loginconfirmpassword').val())
		};

		$.ajax({
			url:OMNIEnv.ctx + '/mgmt/changeinfo/pwd',
			type:'post',
			data:JSON.stringify(data),
			dataType:'json',
			contentType : 'application/json; charset=utf-8',
			success: function(data) {
				if (data.resultCode === '0000') {
					$('#moveId').val( OMNI.auth.encode(OMNIEnv.pprs, '<c:out value="${loginId}"/>') ); //OMNI.auth.encode(OMNIEnv.pprs, $('#loginpassword').val())
					$('#movePw').val( OMNI.auth.encode(OMNIEnv.pprs, $('#loginpassword').val()) ); //OMNI.auth.encode(OMNIEnv.pprs, $('#loginpassword').val())
					
					OMNI.popup.open({
						id: "pwd_check",
						closelabel: "확인",
						closeclass:'btn_blue',
						content: "비밀번호가 변경되었습니다.",
						close:function() {
							OMNI.popup.close({ id: 'pwd_check' });
							$("#isEncryption").val('true');
							$('#moveOn').submit();
						}
					});
				} else {
					OMNI.popup.open({
						id: "pwd_check_fail",
						closelabel: "확인",
						closeclass:'btn_blue',
						content: data.message
					});
				}
				$('.layer_wrap').focus();
			},
			error: function() {
			}
		});
	});
	
	var checkCurrPwd = function() {
		if($('#currentpassword').val() === '') {
			$('#currentpassword').removeClass('is_success').addClass('is_error');
			
			$('#currpassword-guide-msg').empty();
			$('#currpassword-guide-msg').removeClass('is_success').addClass('is_error');
			$('#currpassword-guide-msg').html((new Function('return password.valid.error.emp'))()).show();
		}
		
		var data = {
			cpw: $('#currentpassword').val()
		};
		console.log("data : " + data);
		$.ajax({
			url:OMNIEnv.ctx + '/mgmt/changeinfo/checkPwd',
			type:'post',
			data:JSON.stringify(data),
			dataType:'json',
			global: false,
			contentType : 'application/json; charset=utf-8',
			success: function(data) {
				if (data.resultCode === '0000') {
					$('#currentpassword').removeClass('is_error').addClass('is_success');
					
					$('#currpassword-guide-msg').empty();
					$('#currpassword-guide-msg').removeClass('is_error').addClass('is_success');
					$('#currpassword-guide-msg').hide();
					
				} else {
					$('#currentpassword').removeClass('is_success').addClass('is_error');
					
					$('#currpassword-guide-msg').empty();
					$('#currpassword-guide-msg').removeClass('is_success').addClass('is_error');
					$('#currpassword-guide-msg').html((new Function('return password.valid.error.wrong'))()).show();
				}
				
				if($('#loginpassword').val() !== '') {
					checkPwd(null);	
				}
				
				if($('#loginconfirmpassword').val() !== '') {
					checkConfirmPwd(null);	
				}
				
				disabledOnOff();
			},
			error: function() {
			}
		});
	}
	
	var checkPwd = function(e) {
		var valid = OMNI.auth.validPassword($('#loginpassword').val(), {checkId:'<c:out value="${loginId}" />', prePasswordId:'currentpassword'});
		var msgkey = OMNI.auth.validationdMsgKey('password', valid.key);
		if (valid.code > 0) {
			$('#loginpassword').removeClass('is_error').addClass('is_success');
			$('#newpassword-guide-msg').empty();
			$('#newpassword-guide-msg').removeClass('is_error').addClass('is_success');
			$('#newpassword-guide-msg').html((new Function('return ' + msgkey))()).show();
			OMNI.auth.setPasswordStrength('newpassword-guide-strength', 'newpassword-guide-msg', valid.strength);
			
			if ($('#loginpassword').val() === $('#loginconfirmpassword').val()) {
				$('#loginconfirmpassword').removeClass('is_error').addClass('is_success');
				$('#newconfirmpassword-guide-msg').empty();
				$('#newconfirmpassword-guide-msg').removeClass('is_error').addClass('is_success');
				$('#newconfirmpassword-guide-msg').html((new Function('return ' + msgkey))()).show();
			} else {
				$('#loginconfirmpassword').removeClass('is_success').addClass('is_error');
				$('#newconfirmpassword-guide-msg').empty();
				$('#newconfirmpassword-guide-msg').removeClass('is_success').addClass('is_error');
				$('#newconfirmpassword-guide-msg').html((new Function('return password.valid.error.same'))()).show();
			}
			
			OMNI.auth.setPasswordStrength('newconfirmpassword-guide-strength', 'newconfirmpassword-guide-msg', valid.strength);
			
			if(e !== null && e !== undefined) {
				var keycode = e.keyCode || e.which;
				if (keycode === KeyCode.ENTER) {
					if ($('#loginconfirmpassword').length > 0) {
						$('#loginconfirmpassword').focus();
					}
				}
			}
			
		} else {
			$('#loginpassword').removeClass('is_success').addClass('is_error');
			$('#newpassword-guide-msg').empty();
			$('#newpassword-guide-msg').removeClass('is_success').addClass('is_error');
			$('#newpassword-guide-msg').html((new Function('return ' + msgkey))()).show();
			OMNI.auth.setPasswordStrength('newpassword-guide-strength', 'newpassword-guide-msg', valid.strength);
		}
		
		if ($('#loginconfirmpassword').val() === '') {
			$('#loginconfirmpassword').removeClass('is_success').addClass('is_error');
			$('#newconfirmpassword-guide-msg').empty();
			$('#newconfirmpassword-guide-msg').removeClass('is_success').addClass('is_error');
			$('#newconfirmpassword-guide-msg').html((new Function('return password.valid.error.emp_re'))()).show();
			OMNI.auth.setPasswordStrength('newconfirmpassword-guide-strength', 'newconfirmpassword-password-guide-msg', -1);
		}		
	}
	
	var checkConfirmPwd = function(e) {
		var valid = OMNI.auth.validPassword($('#loginconfirmpassword').val(), {confirmId:'loginpassword', checkId:'<c:out value="${loginId}" />', isConfirm:true, prePasswordId:'currentpassword'});
		var msgkey = OMNI.auth.validationdMsgKey('password', valid.key);
		if (valid.code > 0) {
			$('#loginconfirmpassword').removeClass('is_error').addClass('is_success');
			$('#newconfirmpassword-guide-msg').empty();
			$('#newconfirmpassword-guide-msg').removeClass('is_error').addClass('is_success');
			$('#newconfirmpassword-guide-msg').html((new Function('return ' + msgkey))()).show();
			OMNI.auth.setPasswordStrength('newconfirmpassword-guide-strength', 'newconfirmpassword-guide-msg', valid.strength);
			
			
			if ($('#loginconfirmpassword').val() === $('#loginpassword').val()) {
				$('#loginpassword').removeClass('is_error').addClass('is_success');
				$('#newpassword-guide-msg').empty();
				$('#newpassword-guide-msg').removeClass('is_error').addClass('is_success');
				$('#newpassword-guide-msg').html((new Function('return ' + msgkey))()).show();
				OMNI.auth.setPasswordStrength('newpassword-guide-strength', 'newpassword-guide-msg', valid.strength);
			}
			
			if(e !== null && e !== undefined) {
				if (!$('#dochangepwd').is(':disabled')) {
					var keycode = e.keyCode || e.which;
					if (keycode === KeyCode.ENTER) {
						$('#dochangepwd').trigger('click');
					}	
				}
			}
			
		} else {
			if (valid.code < -4) { msgkey = 'password.valid.error.invalid'; }
			$('#loginconfirmpassword').removeClass('is_success').addClass('is_error');
			$('#newconfirmpassword-guide-msg').empty();
			$('#newconfirmpassword-guide-msg').removeClass('is_success').addClass('is_error');
			$('#newconfirmpassword-guide-msg').html((new Function('return ' + msgkey))()).show();
			OMNI.auth.setPasswordStrength('newconfirmpassword-guide-strength', 'newconfirmpassword-password-guide-msg', valid.strength);
		}
	}
	
		/*
		$('#req-withdraw').on('click', function() {
			location.href = OMNIEnv.ctx + '/mgmt/withdraw';
		});
		
		  
		  $("#all_chk").change(function() {
			  termDisabledOnOff($(this).prop("checked"));
		  });
		
		  $("input[type=checkbox]:not(#all_chk)").change(function() {
			  termDisabledOnOff();
		  });
	  
		  $('#save-terms').on('click', function() {
			  $('#termsForm').find(':checkbox:not(:checked)').attr('value', 'off').prop('checked', true);
			  var data = $('#termsForm').serializeObject();
			  $.ajax({
					url:OMNIEnv.ctx + '/mgmt/changeinfo/terms',
					type:'post',
					data:JSON.stringify(data),
					dataType:'json',
					contentType : 'application/json; charset=utf-8',
					success: function(data) {
						if (data.resultCode === '0000') {
							OMNI.popup.open({
								id: "pwd_check",
								closelabel: "확인",
								closeclass:'btn_blue',
								content: "변경사항 저장에 성공하였습니다."
							});
						} else if (data.resultCode === '9000' || data.resultCode === '1000' || data.resultCode === '1010') {
							OMNI.popup.open({
								id:'agress-popup',
							 	content: data.message,
							 	closelabel:'닫기',
								closeclass:'btn_blue'
					 		});	
						} else {
							OMNI.popup.open({
								id:'agress-popup',
							 	content: '오류가 발생하였습니다.',
							 	closelabel:'닫기',
								closeclass:'btn_blue'
					 		});	
						}
					},
					error: function() {
					}
				});		  
		  });
          
          $('.sns_mapping').on('click', function(){
        	  var snsType = $(this).data('key');
        	  $('#snsType').val(snsType);
        	  
              var popupId = "snsAuthPopup";
              var ret = window.open("", popupId, "width=800, height=600, top=50, left=200");
              
              var form = document.forms['snsMappingForm'];
              form.action = '<c:out escapeXml="false" value="${mappingPageUrl}" />';
              form.target = popupId;
              form.submit();
          });
          
          $('.sns_unlink').on('click', function(){
              $.ajax({
                    url: OMNIEnv.ctx + '/mgmt/detail/snsunlink/' + $(this).data('key'),
                    type:'get',
                    dataType:'json',
                    contentType : 'application/json; charset=utf-8',
                    success: function(data) {
                    	if (data.resultCode === '0000') {
                        	window.location.reload();
                        	
                        } else {
                            OMNI.popup.open({
                                id:'sns-unlink-fail-popup',
                                content: data.message,
                                closelabel:'닫기',
                                closeclass:'btn_blue'
                            });
                        }
                    }, error: function() {
                    	OMNI.popup.open({
                        	id:'sns-unlink-error-popup',
                            content: "오류가 발생하였습니다.",
                            closelabel:'닫기',
                            closeclass:'btn_blue'
                        });
                    }
              });
          }); 
          
      	$('.btn_link_bp').on('click', function() {
    		$('#agree-contents').empty();
    		var data = {
    			type:$(this).data('type'),
    		};
    		if (data.type === '') {
    			return;
    		}
    		
    		$('#agree-contents').load(OMNIEnv.ctx + '/omni-terms-detail', data, function(response, status, xhr) {
    			if (status === 'success') {
    				var poptitle= '서비스이용약관';
    				if (data.type === 'C') {
    					poptitle = '개인정보 수집 이용 동의';
    				} else if (data.type === 'P') {
    					poptitle = '개인정보 제공동의';
    				} else if (data.type === 'T') {
    					poptitle = '국외이전 동의';
    				} 
    			 	OMNI.popup.open({
    					id:'agress-popup',
    				 	title:poptitle,
    				 	scroll:true,
    				 	content: $(this).html(),
    				 	closelabel:'닫기',
    					closeclass:'btn_blue'
    			 	});	
    			}
    			
    		});
    		
    	});          
  });
  
    var termDisabledOnOff = function(allChecked) {
    	var requiredCheckboxLength = $("input[type=checkbox].required").length;
		var checkedRequiredCheckboxLength = $("input[type=checkbox].required:checked").length;
	
		var condition = requiredCheckboxLength == checkedRequiredCheckboxLength;
	
		if (allChecked != undefined) {
			condition = allChecked;
		}
		
		if (condition) {
			$(".btn_changeterms").find("button[type=button]").removeAttr("disabled");
		} else {
			$(".btn_changeterms").find("button[type=button]").attr("disabled", "disabled");
		}
    }
    */
    
	});// 위 주석 해제시 삭제
    
	var disabledOnOff = function() {
		
		var condition = true;

		condition &= $("#currentpassword").hasClass('is_success');
		condition &= $("#loginpassword").hasClass('is_success');
		condition &= $("#loginconfirmpassword").hasClass('is_success');
		
		if (condition) {
			$("#dochangepwd").removeAttr("disabled");
		} else {
			$("#dochangepwd").attr("disabled", "disabled");
		}
	};
	
	</script>
</head>

<body>
	<tagging:google noscript="true"/>
	<div id='agree-contents' style='display:none'></div>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="회원정보 관리"/>
    <!-- container -->
    <section class="container">
      <div class="section_detail">
        <div class="info_box">
          <h3>비밀번호 변경</h3>
          <div class="input_form">
            <span class="inp" id='currentpassword-span'>
              <input type="password" id='currentpassword' class="inp_text" maxlength="16" placeholder="현재 비밀번호" title="현재 비밀번호">
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="currpassword-guide-msg" class="form_guide_txt is_success"></p>
          </div>
          <div class="input_form">
            <span class="inp" id='newpassword-span'>
              <input type="password" id='loginpassword' class="inp_text" maxlength="16" placeholder="신규 비밀번호 입력 (영문 소문자, 숫자, 특수문자 조합 8-16자)" title="신규 비밀번호 입력">
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="newpassword-guide-msg" class="form_guide_txt is_success"></p>
          </div>
          <div class="input_form">
            <span class="inp" id='newconfirmpassword-span'>
              <input type="password" id='loginconfirmpassword' class="inp_text" maxlength="16" placeholder="신규 비밀번호 확인" title="신규 비밀번호 확인">
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="newconfirmpassword-guide-msg" class="form_guide_txt is_success"></p>
            <common:password-notice/>
          </div>
          <div class="btn_submit btn_changepassword">
            <button type="button" class="btnA btn_blue" id='dochangepwd' disabled>비밀번호 변경</button>
          </div>
        </div>
        
        <%-- <div class="info_box">
          <div class="user_info mb13">
            <h3>기본정보 (필수)</h3>
            <dl>
              <dt>이름</dt>
              <dd>${name}</dd>
            </dl>
            <dl>
              <dt>생년월일 (성별)</dt>
              <dd>${birth} (${gender})</dd>
            </dl>
          </div>
          <div class="input_form mb0">
            <span class="inp">
              <input type="tel" id='mobile' class="inp_text" value="${mobile}" readonly="readonly" />
              <button type="button" class="btn_phone_change">변경</button>
            </span>
          </div>
        </div>
        <div class="info_box">
          <h3>
            <span class="checkboxA">
              <input type="checkbox" id="i_additional_info" />
              <label for="i_additional_info"><span class="checkbox_label">추가정보 (선택)</span></label>
            </span>
            <a href="javascript:;" class="btn_link"><span class="blind">자세히보기</span></a>
          </h3>
          <div class="input_form">
            <span class="inp">
              <input type="email" id='email' class="inp_text" placeholder="E-mail (@.도메인 까지 입력)" />
              <button type="button" class="btn_del"><span class="blind">삭제</span></button>
            </span>
            <!-- <p class="form_guide_txt">E-mail 형식이 맞지 않습니다.</p> -->
          </div>
          <div class="input_form">
            <span class="inp">
              <input type="text" class="inp_text" placeholder="기본 주소" value="05500" readonly="readonly" />
              <button type="button" class="btn_search_zip">주소 검색</button>
            </span>
          </div>
          <div class="input_form">
            <span class="inp">
              <input type="text" class="inp_text" value="서울시 용산구 용산동 100-10" readonly="readonly" />
            </span>
          </div>
          <div class="input_form">
            <span class="inp">
              <input type="text" class="inp_text" value="100호" />
            </span>
          </div>
          <div class="btn_submit mt20">
            <button type="submit" class="btnA btn_blue">추가정보 변경</button>
          </div>
        </div>
        <div class="info_box">
          <h3>SNS 계정 연동</h3>
          <ul class="my_sns">
            <li>
              <i class="ico"><img src="<c:out value='${ctx}'/>/images/common/btn_login_kakao.png" alt="kakao"></i>
              <strong>카카오</strong>
            <c:choose>
	            <c:when test="${not empty KAmappingTime}">
	               <p class="connection">${KAmappingTime} 에 연결 되었습니다.</p>
	               <button class="sns_unlink" type="button" data-key="KA">해제</button>
	            </c:when>
	            <c:otherwise>
	               <p>연결된 정보가 없습니다.</p>
	               <button class="sns_mapping" type="button" data-key="KA">연결</button>
	            </c:otherwise>
            </c:choose>
            </li>
            <li>
              <i class="ico"><img src="<c:out value='${ctx}'/>/images/common/btn_login_naver.png" alt="naver"></i>
              <strong>네이버</strong>
              <c:choose>
	            <c:when test="${not empty NAmappingTime}">
	               <p class="connection">${NAmappingTime} 에 연결 되었습니다.</p>
	               <button class="sns_unlink" type="button" data-key="NA">해제</button>
	            </c:when>
	            <c:otherwise>
	               <p>연결된 정보가 없습니다.</p>
	               <button class="sns_mapping" type="button" data-key="NA">연결</button>
	            </c:otherwise>
              </c:choose>
            </li>
            <li>
              <i class="ico"><img src="<c:out value='${ctx}'/>/images/common/btn_login_facebook.png" alt="facebook"></i>
              <strong>페이스북</strong>
              <c:choose>
	            <c:when test="${not empty FBmappingTime}">
	               <p class="connection">${FBmappingTime} 에 연결 되었습니다.</p>
	               <button class="sns_unlink" type="button" data-key="FB">해제</button>
	            </c:when>
	            <c:otherwise>
	               <p>연결된 정보가 없습니다.</p>
	               <button class="sns_mapping" type="button" data-key="FB">연결</button>
	            </c:otherwise>
              </c:choose>
            </li>
          </ul>
        </div>
        <div class="info_box">
          <div class="all_agree_box m0 is_open">
            <div class="all_chk">
              <span class="checkboxA">
                <input type="checkbox" id="all_chk" />
                <label for="all_chk"><span class="checkbox_label">모든 광고성 정보 수신 동의</span></label>
              </span>
              <button type="button" class="btn_all_view"><span class="blind">약관 닫기</span></button>
            </div>
            <form id='termsForm'>
            <div class="agree_list">
              <strong class="txt_t">뷰티포인트 수신 동의</strong>
              <ul>
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree2_1" name='marketing[0][tncAgrYn]'/>
                    <label for="i_agree2_1"><span class="checkbox_label">[선택] 뷰티포인트 문자 수신 동의</span></label>
                  </span>
                  <!-- <a href="javascript:;" class="btn_link"><span class="blind">자세히보기</span></a> -->
                </li>
                <input type='hidden' name='marketing[0][tcatCd]' value='000'/>
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree2_2" name='marketing[1][tncAgrYn]'/>
                    <label for="i_agree2_2"><span class="checkbox_label">[선택] 문자 수신 동의</span></label>
                  </span>
                </li>
                <input type='hidden' name='marketing[1][tcatCd]' value='030'/>
              </ul>
              <strong class="txt_t">‘${channelName}’ 수신 동의</strong>
              <ul>
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree2_3" name='marketing[2][tncAgrYn]'/>
                    <label for="i_agree2_3"><span class="checkbox_label">[선택] 문자 수신 동의</span></label>
                  </span>
                </li>
                <input type='hidden' name='marketing[2][tcatCd]' value='${chCd}'/>
              </ul>
              <strong class="txt_t">개인정보 수집 및 이용에 대한 동의</strong>
              <ul>
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree2_5" class="required" name="bpterms[0][tncAgrYn]"/>
                    <label for="i_agree2_5">
                      <span class="checkbox_label">[필수] 개인정보 제 3자 제공 동의</span>
                      <em class="sm">*외부 컨텐츠 마케팅 활용</em>
                    </label>
                  </span>
                  <a href="javascript:;" class="btn_link_bp" data-type='P'><span class="blind">자세히보기</span></a>
                </li>
                <input type='hidden' name="bpterms[0][tcatCd]" value="070"/>
                <input type='hidden' name='bpterms[0][tncvNo]' value='1.1'/>
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree2_6" class="required" name="bpterms[1][tncAgrYn]"/>
                    <label for="i_agree2_6"><span class="checkbox_label">[필수] 국외 이전 동의</span></label>
                  </span>
                  <a href="javascript:;" class="btn_link_bp" data-type='T'><span class="blind">자세히보기</span></a>
                </li>
                <input type='hidden' name="bpterms[1][tcatCd]" value="060"/>
                <input type='hidden' name='bpterms[1][tncvNo]' value='1.0'/>                
              </ul>
            </div>
          </div>
          </form>
          <div class="btn_submit mt20 btn_changeterms">
            <button type="button" class="btnA btn_blue" id='save-terms' disabled>변경사항 저장</button>
          </div>
        </div>
        <div class="info_box">
          <h3>회원탈퇴</h3>
          <div class="withdrawal_btnarea">
            <p>회원 탈퇴 신청을 합니다.</p>
            <button type="button" id='req-withdraw'>탈퇴신청</button>
          </div>
        </div> --%>
      </div> 
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
  
  <form id="snsMappingForm" name="snsMappingForm" method="POST"> <%-- action="<c:out value='${ctx}'/>/sns/auth" target="snsAuthPopup"> --%>
	  	<input type='hidden' id='snsType' name='snsType' value=''/>
	  	<!-- test value -->
	  	<input type='hidden' id='loginId' name='loginId' value='<c:out value="${loginId}" />'/>
	  	<input type='hidden' id='incsNo' name='incsNo' value='<c:out value="${incsNo}" />'/>
	  	<!-- end of test value -->
	  	<input type='hidden' id="redirectUrl" name='redirectUrl' value='/sns/finish'/>	  	
  </form>
  
	<form id='moveOn' name="moveOn" method='post' action='<c:out value="${actionurl}"/>'>
		<c:if test="${!offline}">
			<input type='hidden' id="moveId" name='username' value='<c:out value="${loginId}"/>'/>
			<input type='hidden' id="movePw" name='password'/>
			<input type='hidden' id="isEncryption" name='isEncryption'/>
			<c:if test="${autologin}">
				<input type='hidden' name='chkRemember' value='on'/>
			</c:if>
			<input type="hidden" name="sessionDataKey" value="<c:out value="${sessionDataKey}"/>">
		</c:if>	
	</form>
  
</body>

</html>