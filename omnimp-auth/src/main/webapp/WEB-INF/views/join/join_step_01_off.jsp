<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="otl" uri="/WEB-INF/tlds/oneap-taglibs.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% Boolean offline = (Boolean) request.getAttribute("offline"); %>
<!DOCTYPE html><!-- join step 01 off -->
<html lang="ko">
<head>
  <title>정보입력 및 약관동의 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true" off="true" authCategory="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	  $('#not-set-terms').show();
	  $('#set-terms').hide();
	  
		$('#dojoin').on('click', function(e) {
			
			$(this).attr('disabled', 'disabled');
			
			var id = $('#loginid').val().trim();
			var pw = $('#loginpassword').val();
			var cpw = $('#loginconfirmpassword').val();
			if ($('#integrateidcheck').is(':checked')) {
				$('#integrateid').val('true');
				var validId = OMNI.auth.validLoginId(id, {checkPassword:pw, serverCheck:true});
				var validPw = OMNI.auth.validPassword(pw, {checkId:id,confirmId:'loginconfirmpassword', serverCheck:true});
				$('#uid').val(OMNI.auth.encode(OMNIEnv.pprs, id));
				$('#upw').val(OMNI.auth.encode(OMNIEnv.pprs, pw));
				$('#ucpw').val(OMNI.auth.encode(OMNIEnv.pprs, cpw));
			} else { // 체크하지 않으면 오프라인만 등록함.
				var validId = {code: 100}; // 통합아이디 입력 SKIP
				var validPw = {code: 100}; // 통합아이디 입력 SKIP
				$('#uid').val('');
				$('#upw').val('');
				$('#ucpw').val('');
			}
			
			if (validId.code > 0 && validPw.code > 0) {
				$('#joinform').find(':checkbox:not(:checked)').attr('value', 'off'); //.prop('checked', true);
				
				var checkedbuff = '';
				$('#joinform').find(':checkbox').each(function(idx, val) {
					
					var id = $(this).attr('id');
					if (id === 'all_chk' || id === 'integrateidcheck') {
						return true; // continue;
					}
					var name = $(this).attr('name');
					var checkname = name.replace(/_check/gi, '');
					var checked = $(this).is(':checked');
					if (checked) {
						checkedbuff += '<input type="hidden" name="' + checkname + '" value="on"/>';
					} else {
						checkedbuff += '<input type="hidden" name="' + checkname + '" value="off"/>';
					}
					
				});
				
				$('#joinform').append(checkedbuff);
				
				$('#loginpassword').val('');
				$('#loginconfirmpassword').val('');
				
				OMNI.loading.show('processing');
				
				$('#joinform')
				.attr('action', OMNIEnv.ctx + '/join/terms/offlinebp')
				.submit();
				
			} else {
				$('#dojoin').removeAttr('disabled');
				return;
			}
		});
		
		$('.agree_list').on('click', $('input[id^=i_agree_]'), function() {
			disabledOnOff();
		});
		
		//$('body').on('click', $("#all_chk"), function() {
		$("#all_chk").on('click', function() {	
			if ($(this).prop("checked")) {
				$(document).find('.all_agree_box').removeClass('is_open');
				$(document).find('.all_agree_box').find('.agree_list').find('input[type=checkbox]').prop('checked', true);
			} else {
				$(document).find('.all_agree_box').addClass('is_open');
				$(document).find('.all_agree_box').find('.agree_list').find('input[type=checkbox]').prop('checked', false);
			}
			
			disabledOnOff($(this).prop("checked"));
			marketing_disabledOnOff($(this).prop("checked"));
			marketing_disabledOnOff_channel($(this).prop("checked"));
		});
		$("#i_agree_bp_top").on('click', function() {
			marketing_disabledOnOff($(this).prop("checked"));
		});
		$("input[type=checkbox]:not(#all_chk, #integrateidcheck)").on('change', function() {
			disabledOnOff();
		});
		
		$(document).on('change', $("input[type=checkbox]:not(#all_chk, #integrateidcheck)"), function(e) {
			var check_list = $(document).find('.all_agree_box').find('.agree_list').find('input[type=checkbox]');
			var checked_list = $(document).find('.all_agree_box').find('.agree_list').find('input[type=checkbox]:checked');
			if (check_list.length == checked_list.length) {
				$('#all_chk').prop('checked', true);
			} else {
				$('#all_chk').prop('checked', false);
			}
		});

		$('#phone-cert').on('click', function() {
			location.href = OMNIEnv.ctx + '/cert/ipin-phone';	
		});
		
		// 오프라인인 경우 통합아이디 등록 (선택)
		$("#integrateidcheck").each(function() {
		  if ($(this).prop("checked")) {
		    $('#info_int_id_reg').hide();
		    $('#area_int_id_reg').show();
		  } else {
			$('#loginid').blur();
		    $('#info_int_id_reg').show();
		    $('#area_int_id_reg').hide();
		  }
		});
		
		$("#integrateidcheck").change(function() {
			
		  if ($('#integrateidcheck').prop("checked")) {
			
			getTermContent();
			
		    $('#info_int_id_reg').hide();
		    $('#area_int_id_reg').show();
		    if ($('#loginid').length > 0) { $('#loginid').focus(); }
		    
		    var termchecked = $('#all_chk').is(':checked') ? 'checked' : '';
		    
		    <c:if test="${isThirdPartyConsent}">
		  		$.ajax({ 
		  			url: OMNIEnv.ctx + '/join/thirdparty-terms-content', 
		  			type: "GET", 
		  			dataType: "json", 
		  			global: false, 
		  			success: function (data) { 
					    for(var i=0;i<data.length;i++) {
			  				var html = '<span class="checkboxA">';
				            html += '<input type="checkbox" id="i_agree_bpterms_2" name="bpterms_check" class="required" ' + termchecked + ' title="(필수) \'개인정보 제3자 제공 동의 (<c:out value="${channelName}" />)\' 체크 박스"/>';
				            html += '<label for="i_agree_bpterms_2" ap-click-area="약관동의" ap-click-name="약관동의 - (필수) \'개인정보 제3자 제공 동의 (<c:out value="${channelName}" />)\' 체크 박스" ap-click-data="(필수) \'개인정보 제3자 제공 동의 (<c:out value="${channelName}" />)\' 체크 박스"><span class="checkbox_label">[필수] 개인정보 제3자 제공 동의 (<c:out value="${channelName}" />)</span></label>';
				            html += '</span>';
				            html += '<a href="javascript:;" class="btn_link_bp" data-type="P"><span class="blind">자세히보기</span></a>';
				            
						    $('#bptermsOff').html(html).show();
					    
						    var inputs = '<input type="hidden" id="bpTcatCds_id" name="bpTcatCds" value="' + data[0].tcatCd + '"/>';
				            inputs += '<input type="hidden" id="bpTncvNos_id" name="bpTncvNos" value="' + data[0].tncvNo + '"/>';
				            $('#bptermsOff').after(inputs);
					    }
		  			}, 
		  			error: function (data) { console.log(data); } 
		  			});
		    </c:if>
		  } else {
			$('#loginid').blur();
			clearTermContent();
			  
		    $('#info_int_id_reg').show();
		    $('#area_int_id_reg').hide();
		    
		    $('#bptermsOff').empty();
		    $('#bpTcatCds_id').remove();
		    $('#bpTncvNos_id').remove();
		    
		    $('#loginid').val('').removeClass('is_error');
		    $('#loginpassword').val('').removeClass('is_error');
		    $('#loginconfirmpassword').val('').removeClass('is_error');
		    $('#loginid-guide-msg').removeClass('is_error').hide();
			$('#password-guide-msg').removeClass('is_error').hide();
			$('#confirm-password-guide-msg').removeClass('is_error').hide();	  
		    
		  }
		  disabledOnOff();
		});		
		$("#i_agree_bp_top_channel").on('click', function() {
			marketing_disabledOnOff_channel($(this).prop("checked"));
		});
		
	});
  
  	var getTermContent = function() {
  		$.ajax({ 
  			url: OMNIEnv.ctx + '/join/terms-content', 
  			type: "GET", 
  			dataType: "html", 
  			global: false, 
  			success: function (data) { 
  				content = data;
  	  			$('#bp-mrk').after(content);
  	  			
  			    if ($('#all_chk').is(':checked')) {
  			    	var check_list = $(document).find('.all_agree_box').find('.agree_list').find('input[type=checkbox]');
  			    	check_list.prop('checked', true);
  			    }
  			  <c:if test="${not empty terms_marketing }">
  				$('.bp-trms').show();
  			  </c:if>
  			}, 
  			error: function (data) { console.log(data); } 
  			});
  		$('.terms_marketing_li').each(function() {
			$(this).remove();
		});
  	//관계사 개인정보 수집 및 이용동의 약관
	    var html2 = "<li class='terms_marketing_li'>";
	    html2 += '<span class="checkboxA">';
	    if ($('#all_chk').is(':checked')) {
	    	html2 += "<input type='checkbox' id='i_agree_bp_top_channel' checked name='terms_check' title='(선택) '개인정보 수집 및 이용동의 (마케팅)'' onClick='idcheckOnline($(this));'/>";
	    }else{
	    	html2 += "<input type='checkbox' id='i_agree_bp_top_channel' name='terms_check' title='(선택) '개인정보 수집 및 이용동의 (마케팅)'' onClick='idcheckOnline($(this));'/>";
	    	if(!$('#i_agree_bp_top_channel').is(':checked') && !$('#i_agree_bp_top').is(':checked')){
		    	$("input[id^='i_agree_chagree_mrk']").prop('checked', false);
				$("input[id='i_agree_chagree_mrk']").removeAttr('checked');
				$("input[id='i_agree_chagree_mrk']").attr('disabled', 'disabled');
	    	}
	    }
	    html2 += '<label for="i_agree_bp_top_channel" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) "개인정보 수집 및 이용동의 (마케팅)" 체크 박스" ap-click-data="(선택) "개인정보 수집 및 이용동의 (마케팅)""><span class="checkbox_label">[선택] 개인정보 수집 및 이용동의 (마케팅)(<c:out value="${channelName}" />)</span><em class="sm">*개인정보 수집 및 이용(마케팅)에 동의 하셔야 문자 수신 동의가 가능합니다.</em></label>';
	    html2 += "</span>";
	    html2 += '<a href="javascript:;" class="btn_link" data-lnk="<c:out escapeXml="false" value="${terms_marketing_online.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${chCd}" />" data-lnkcd="<c:out escapeXml="false" value="${terms_marketing_online.tcatCd}" />" data-lnkno="<c:out escapeXml="false" value="${terms_marketing_online.tncvNo}" />" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - <c:out value="${channelName}" /> (<c:out value="${terms_marketing_online.tncAgrMandYnTxt}" />) "<c:out value="${terms_marketing_online.tncTtl}" />" 상세 보기 버튼" ap-click-data="(<c:out value="${terms_marketing_online.tncAgrMandYnTxt}" />) "<c:out value="${terms_marketing_online.tncTtl}" />" 상세 보기 버튼"><span class="blind">자세히보기</span></a>';
	    html2 += '<input type="hidden" name="tcatCds" value="<c:out escapeXml="false" value="${terms_marketing_online.tcatCd}" />"/>';
	    html2 += '<input type="hidden" name="tncvNos" value="<c:out escapeXml="false" value="${terms_marketing_online.tncvNo}" />"/>';
	    html2 += "</li>";
	    $('#mrk_pannel_online_under').html(html2).show();
  		
  	};
	var clearTermContent = function() {
		$('.terms').each(function() {
			$(this).remove();
		});
		$('.bp-trms').hide();
		
		$('.terms_marketing_li').each(function() {
			$(this).remove();
		});
		//관계사 개인정보 제 3자 제공 동의
		 var html2 = "<li class='terms_marketing_li'>";
	    html2 += '<span class="checkboxA">';
	    if ($('#all_chk').is(':checked')) {
	    	 html2 += "<input type='checkbox' id='i_agree_bp_top_channel' checked name='bpterms_check' title='(선택) '개인정보 제 3자 제공 동의'' onClick='idcheckOnline($(this));'/>";
	    }else{
	    	 html2 += "<input type='checkbox' id='i_agree_bp_top_channel' name='bpterms_check' title='(선택) '개인정보 제 3자 제공 동의'' onClick='idcheckOnline($(this));'/>";
	    	 if(!$('#i_agree_bp_top_channel').is(':checked') && !$('#i_agree_bp_top').is(':checked')){
			    	$("input[id^='i_agree_chagree_mrk']").prop('checked', false);
					$("input[id='i_agree_chagree_mrk']").removeAttr('checked');
					$("input[id='i_agree_chagree_mrk']").attr('disabled', 'disabled');
		    	}
	    }
	    html2 += '<label for="i_agree_bp_top_channel" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) "개인정보 제 3자 제공 동의" 체크 박스" ap-click-data="(선택) "개인정보 제 3자 제공 동의""><span class="checkbox_label">[선택] 개인정보 제 3자 제공 동의(<c:out value="${channelName}" />)</span><em class="sm">*개인정보 수집 및 이용(마케팅)에 동의 하셔야 문자 수신 동의가 가능합니다.</em></label>';
	    html2 += "</span>";
	    html2 += '<a href="javascript:;" ap-click-area="약관동의" ap-click-name="약관동의 - (<c:out value="${terms_marketing.tncAgrMandYnTxt}" />) "<c:out value="${terms_marketing.tncTtl}" />" 상세보기 버튼" ap-click-data="(<c:out value="${terms_marketing.tncAgrMandYnTxt}" />) "<c:out value="${terms_marketing.tncTtl}" />" 상세보기" class="btn_link_bp" data-lnk="<c:out escapeXml="false" value="${terms_marketing.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${terms_marketing.chCd}" />"  data-lnkcd="<c:out escapeXml="false" value="${terms_marketing.tcatCd}" />" data-type="N" data-lnkno="<c:out escapeXml="false" value="${terms_marketing.tncvNo}" />"><span class="blind">자세히보기</span></a>';
	    html2 += '<input type="hidden" name="bpTcatCds" value="<c:out escapeXml="false" value="${terms_marketing.tcatCd}" />"/>';
	    html2 += '<input type="hidden" name="bpTncvNos" value="<c:out escapeXml="false" value="${terms_marketing.tncvNo}" />"/>';
	    html2 += "</li>";
	    $('#mrk_pannel_online_under').html(html2).show();
		
	};  
	var idcheckOnline = function(a){ //관계사 약관 체크
		marketing_disabledOnOff_channel(a.prop("checked"));
	}
	  var closeAction = function() {
			
		  $('#offForm').attr('action', '<c:out escapeXml="false" value="${home}"/>').submit();
		  /*
			OMNI.popup.open({
				id:'next-warn',
				content: '회원가입을 중단하시겠습니까?<br/>(입력한 정보는 모두 초기화 됩니다.)',
				gaArea:'정보입력 및 약관동의',
				gaOkName:'확인 버튼 (중단 팝업)',
				gaCancelName:'취소 버튼 (중단 팝업)',
				oklabel:'취소',
				okclass:'btn_white',
				ok: function() {
					OMNI.popup.close({id:'next-warn'});
				},
				closelabel:'확인',
				closeclass:'btn_blue',
				close: function() {
					window.AP_SIGNUP_TYPE = '중단';
					dataLayer.push({event: 'signup_complete'});	
					OMNI.popup.close({ id: 'next-warn' });
					//location.href = OMNIEnv.ctx + '/go-join-off';
					$('#offForm').attr('action', '<c:out escapeXml="false" value="${home}"/>').submit();
				}
				
			});*/
		  
	};	
    // 오프라인인 경우 아이디 등록 안할 수 있음
  	var disabledOnOff = function(allChecked) {
  		var requiredCheckboxLength = $("input[type=checkbox].required").length;
		var checkedRequiredCheckboxLength = $("input[type=checkbox].required:checked").length;
		
		var condition = requiredCheckboxLength == checkedRequiredCheckboxLength;
		if (allChecked !== undefined) {
			condition = allChecked;
		}
		
		<c:if test="${certType eq 'ipin'}">
		condition &= $("#phone").val() !== '';
		</c:if>
		
		if ($('#integrateidcheck').is(':checked')) {
			condition &= $("#loginid").val() !== '';
			condition &= $("#loginpassword").val() !== '';
			condition &= $("#loginconfirmpassword").val() !== '';
			condition &= $("#loginpassword").val() === $("#loginconfirmpassword").val();
			condition &= $(".is_error").length === 0;
		} else {
			condition = checkedRequiredCheckboxLength === 0;  // 선택만 있는 경우
		}
		
		if (condition) {
			$("#dojoin").removeAttr('disabled');
			$(".agreeY").show();
			$(".agreeN").hide();
			$('#not-set-terms').hide();
			$('#set-terms').show();
		} else {
			$("#dojoin").attr('disabled', 'disabled');
			$(".agreeN").show();
			$(".agreeY").hide();
			$('#not-set-terms').show();
			$('#set-terms').hide();
		}
		
  	};
	
	  var executeAction = function() {
	  };
	  var disabledAction = function() {
		  $("#dojoin").attr('disabled', 'disabled');
	  }; 	 
	  
		//뷰티포인트 광고성 정보 수신 동의
	  	var marketing_disabledOnOff = function(allChecked) {
	  		var requiredCheckboxLength = $("input[id^='i_agree_bp_marketing']").length;
			var checkedRequiredCheckboxLength = $("input[id^='i_agree_bp_marketing']:checked").length;
			var condition = requiredCheckboxLength == checkedRequiredCheckboxLength;
			if (allChecked !== undefined) {
				condition = allChecked;
			}
			if (condition) {
				$("input[id^='i_agree_bp_marketing']").removeAttr('disabled');
				$("input[id='i_agree_bp_marketing_2']").removeAttr('disabled');
				<c:if test ="${empty terms_marketing }">
					$("input[id^='i_agree_chagree_mrk']").removeAttr('disabled');
				</c:if>
			} else {
				$("input[id='i_agree_bp_marketing']").removeAttr('checked');
				$("input[id^='i_agree_bp_marketing']").prop('checked', false);
				$("input[id^='i_agree_bp_marketing']").attr('disabled', 'disabled');
				$("input[id='i_agree_bp_marketing_2']").removeAttr('checked');
				$("input[id='i_agree_bp_marketing_2']").prop('checked', false);
				$("input[id='i_agree_bp_marketing_2']").attr('disabled', 'disabled');
				<c:if test ="${empty terms_marketing }">
					$("input[id='i_agree_chagree_mrk']").removeAttr('checked');
					$("input[id^='i_agree_chagree_mrk']").prop('checked', false);
					$("input[id^='i_agree_chagree_mrk']").attr('disabled', 'disabled');
				</c:if>
			}
	  	};
	//채널 정보 수신 동의
	  	var marketing_disabledOnOff_channel = function(allChecked) {
	  		var requiredCheckboxLength = $("input[id='i_agree_chagree_mrk']").length;
			var checkedRequiredCheckboxLength = $("input[id='i_agree_chagree_mrk']:checked").length;
			var condition = requiredCheckboxLength == checkedRequiredCheckboxLength;
			if (allChecked !== undefined) {
				condition = allChecked;
			}
			if (condition) {
				$("input[id='i_agree_chagree_mrk']").removeAttr('disabled');
				$("input[id='i_agree_bp_bpterms']").attr('checked', 'checked');
			} else {
				$("input[id='i_agree_chagree_mrk']").removeAttr('checked');
				$("input[id^='i_agree_chagree_mrk']").prop('checked', false);
				$("input[id='i_agree_bp_bpterms']").removeAttr('checked');
				
				<c:if test ="${not empty terms_marketing }">
					$("input[id='i_agree_chagree_mrk']").attr('disabled', 'disabled');
				</c:if>
			}
	  	};
  </script>
  <script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-idpwd-check.js?ver=<c:out value="${rv}"/>"></script>
  <script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-terms_off.js?ver=<c:out value="${rv}"/>"></script>  
</head>

<body>
	<tagging:google noscript="true"/>
	<div id='agree-contents' style='display:none'></div>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="정보입력 및 약관동의" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="sec_join">
        <div class="user_info">
          <h3>기본정보 (필수)</h3>
          <dl>
            <dt>이름</dt>
            <dd><c:out value="${sessionScope.cert.name}"/></dd>
          </dl>
          <c:if test="${not empty sessionScope.cert.phone}">
          <dl>
            <dt>휴대폰 번호</dt>
            <dd><c:out value="${otl:phn(sessionScope.cert.phone, locale)}"/></dd>
          </dl>
          </c:if>
          <dl>
            <dt>가입일</dt>
            <dd><c:out value="${otl:bdt(joindate)}"/></dd>
          </dl>
        </div>
        <c:if test="${certType eq 'ipin'}">
        <div class="input_form">
         	<span class="inp">
              <input type="tel" id="phone" class="inp_text" value="<c:out value="${otl:phn(sessionScope.cert.phone, locale)}"/>" placeholder="휴대폰 번호" readonly="readonly" title="휴대폰 번호">
              <button type="button" class="btn_phone_change" id='phone-cert'><c:if test="${not empty sessionScope.cert.phone}">변경</c:if><c:if test="${empty sessionScope.cert.phone}">입력</c:if></button>
            </span>
        </div>
        </c:if>        
		<div class="integrateid">
          <span class="checkboxA">
            <input type="checkbox" id="integrateidcheck" name="integrateidcheck"  title="통합 아이디 등록 (선택)"/>
            <label for="integrateidcheck"><span class="checkbox_label">통합 아이디 등록 (선택)</span></label>
          </span>
        </div>
        <div id="info_int_id_reg" class="user_info">
          <p>지금 아이디와 비밀번호를 등록하시면 온라인 매장 서비스도 동시에 이용하실 수 있습니다.</p>
        </div>    
		<form method='post' action='' id='joinform'>
        	<input type='hidden' name='unm' id='unm' value='<c:out value="${unm}" />'/>
			<input type='hidden' name='uid' id='uid'/>
			<input type='hidden' name='upw' id='upw'/>
			<input type='hidden' name='ucpw' id='ucpw'/>
			<input type='hidden' name='incsno' value='<c:out escapeXml="false" value="${xincsno}" />'/>  
			<input type='hidden' name='integrateid' id='integrateid' value='false'/>
        <div id="area_int_id_reg" style="display: none;">
          <div class="input_form">
            <span class="inp" id="loginid-span">
              <input type="text" oninput="maxLengthCheck(this)" id="loginid" name="loginid" autocomplete="off" class="inp_text" maxlength="12" placeholder="아이디 (영문 또는 숫자 4-12자)" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - 아이디 입력란" ap-click-data="아이디 입력"  title="아이디 입력"/>
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="loginid-guide-msg" class="form_guide_txt is_success"></p>
          </div>
          <div class="input_form">
            <span class="inp" id="password-span">
              <input type="password" id="loginpassword" name="loginpassword" autocomplete="off" class="inp_text" maxlength="16" placeholder="비밀번호 (영문 소문자, 숫자, 특수문자 조합 8-16자)" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - 비밀번호 입력란" ap-click-data="비밀번호 입력"  title="비밀번호 입력"/>
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="password-guide-msg" class="form_guide_txt is_success"></p>
          </div>
          <div class="input_form">
            <span class="inp" id="confirm-password-span">
              <input type="password" id="loginconfirmpassword" name="loginconfirmpassword" autocomplete="off" class="inp_text" maxlength="16" placeholder="비밀번호 확인" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - 비밀번호 확인 입력란" ap-click-data="비밀번호 확인 입력"  title="비밀번호 확인 입력"/>
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="confirm-password-guide-msg" class="form_guide_txt is_success"></p>
            <common:password-notice gaArea="정보입력 및 약관동의"/>
          </div>
        </div>
          <div class="all_agree_box is_open">
            <div class="all_chk">
              <span class="checkboxA">
                <input type="checkbox" id="all_chk" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - 모든 약관 수신 동의 체크 박스" ap-click-data="모든 약관 및 정보 수신 동의" title="모든 약관 및 정보 수신 동의"/>
                <label for="all_chk"><span class="checkbox_label">모든 약관 및 정보 수신 동의</span></label>
              </span>
              <button type="button" class="btn_all_view" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - 약관 항목 접힘/펼침 버튼" ap-click-data="약관 항목 접힘/펼침 버튼"><span class="blind">약관 닫기</span></button>
            </div>
            <div class="agree_list">
              <strong class="txt_t bp-trms" style='display:none'>뷰티포인트 통합회원 약관</strong>
              <ul id='bp-mrk' class='bp-trms' style='display:none'>
                <li id='bptermsOff' style='display:none'>
                </li>
              </ul>

		      <c:choose>
			      <c:when test="${not empty terms_marketing }">
			      <strong class="txt_t terms_mrk" id='chmrk-pannel-header'><c:out value="${channelName}" /> 광고성 정보 수신 동의</strong>
		    	  <ul class='mrk_pannel'>
				      <li id='mrk_pannel_online_under' style='display:none'></li>
				       	<li class="terms_marketing_li">
				          <span class="checkboxA">
				          	<input type="checkbox" id="i_agree_bp_top_channel" name="bpterms_check" title="(선택) '개인정보 제 3자 제공 동의'"/>
		                    <label for="i_agree_bp_top_channel" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '개인정보 제 3자 제공 동의' 체크 박스" ap-click-data="(선택) '개인정보 제 3자 제공 동의'"><span class="checkbox_label">[선택] 개인정보 제 3자 제공 동의(<c:out value="${channelName}" />)</span><em class="sm">*개인정보 수집 및 이용(마케팅)에 동의 하셔야 문자 수신 동의가 가능합니다.</em></label>
		                  </span>
			                <a href="javascript:;" ap-click-area="약관동의" ap-click-name="약관동의 - (<c:out value="${terms_marketing.tncAgrMandYnTxt}" />) '<c:out value="${terms_marketing.tncTtl}" />' 상세보기 버튼" ap-click-data="(<c:out value="${terms_marketing.tncAgrMandYnTxt}" />) '<c:out value="${terms_marketing.tncTtl}" />' 상세보기" class="btn_link_bp" data-lnk="<c:out escapeXml="false" value="${terms_marketing.tncTxtUrl}"/>" data-chcd="${chCd}"  data-lnkcd="<c:out escapeXml="false" value="${terms_marketing.tcatCd}" />" data-type="N" data-lnkno="<c:out escapeXml="false" value="${terms_marketing.tncvNo}" />"><span class="blind">자세히보기</span></a>
			                <input type='hidden' name="bpTcatCds" value="<c:out escapeXml="false" value="${terms_marketing.tcatCd}" />"/>
			         		<input type='hidden' name="bpTncvNos" value="<c:out escapeXml="false" value="${terms_marketing.tncvNo}" />"/>
		                </li>
		                <li>
		                <ul>
			                <li>
			                  <span class="checkboxA select_agree_low">
			                    <input type="checkbox" id="i_agree_chagree_mrk" name='marketing_check' disabled='disabled' title="(선택) '문자 수신 동의'"/>
			                    <label for="i_agree_chagree_mrk" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '문자 수신 동의' 체크 박스" ap-click-data="(선택) '문자 수신 동의'"><span class="checkbox_label">[선택] 문자 수신 동의</span></label>
			                  </span>
		                	<input type='hidden' name='marketingChcd' value='<c:out value="${chcd}" />'/>
			                </li>
			                <li>
			                  <common:info-notice-ch gaArea="정보입력 및 약관동의" channelCd="${chcd}" channelName="${channelName}"/>
			                </li> 
		              	</ul>
		              	</li>	
		             </ul>
			      </c:when>
			      <c:otherwise>
			      <strong class="txt_t">뷰티포인트 광고성 정보 수신 동의</strong>
		              <ul id='bp-mrk'>
		                <li>
		                  <span class="checkboxA">
		                  	<input type="checkbox" id="i_agree_bp_top" name="bpterms_check" title="(선택) '개인정보 수집 및 이용 동의 (마케팅)'"/>
		                    <label for="i_agree_bp_top" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '개인정보 수집 및 이용 동의 (마케팅)' 체크 박스" ap-click-data="(선택) '개인정보 수집 및 이용 동의 (마케팅)'"><span class="checkbox_label">[선택] 개인정보 수집 및 이용 동의 (마케팅)</span><em class="sm">*개인정보 수집 및 이용(마케팅)에 동의 하셔야 문자 수신 동의가 가능합니다.</em></label>
		                  </span>
		                  <a href="javascript:;" class="btn_link_bp" data-type='M' data-chcd='<c:out escapeXml="false" value="${chCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '개인정보 수집 및 이용 동의 (마케팅)' 상세 보기 버튼" ap-click-data="(선택) '개인정보 수집 및 이용 동의 (마케팅)' 상세 보기 버튼">
		                  <span class="blind">자세히보기</span></a>
		                <!-- <input type='hidden' name='marketingChcd' value='080'/> -->
			                <input type='hidden' name="bpTcatCds" value="050"/>
			                <input type='hidden' name='bpTncvNos' value='1.0'/>
		                </li>
		                <li>
			                <ul>
					            <c:if test="${empty terms_marketing }">
					            	<li>
							          <span class="checkboxA select_agree_low">
							            <input type="checkbox" id="i_agree_chagree_mrk" name='marketing_check'  disabled='disabled'  title="(선택) '문자 수신 동의'"/>
							            <label for="i_agree_chagree_mrk" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '문자 수신 동의' 체크 박스" ap-click-data="(선택) '문자 수신 동의'"><span class="checkbox_label">[선택] ${channelName} 문자 수신 동의</span></label>
							          </span>
							          <input type='hidden' name='marketingChcd' value='<c:out value="${chcd}" />'/>
							        </li>
					            </c:if>
				                <li>
				                  <common:info-notice gaArea="약관동의" channelName="뷰티포인트"/>
				                </li>                   
			              </ul> 
		              </li>                      
		              </ul>
				  </c:otherwise>
		      </c:choose>

		                    
            </div>
          </div>
          <div class="btn_submit">
            <button type="button" id="dojoin" class="btnA btn_blue" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - 동의하고 가입 버튼" ap-click-data="동의하고 가입">동의하고 가입</button>
            <input type='hidden' name='joinType' id='joinType' value='<c:out value="${joinType}" />'/>
          </div>
          <p class="txt_c" id='not-set-terms'>가입 필수 정보 및 약관을 모두 확인해주세요.</p>
          <p class="txt_c" id='set-terms'>만 14세 이상이며, 가입약관에 동의합니다.</p>
      </div>
      </form>
      <form id='offForm' method='post' action=''>
  	  	<input type='hidden' name='incsNo' value='<c:out escapeXml="false" value="${incsNo}"/>'/>
    	<input type='hidden' name='chnCd' value='<c:out escapeXml="false" value="${chnCd}"/>'/>
    	<input type='hidden' name='storeCd' value='<c:out escapeXml="false" value="${storeCd}"/>'/>
    	<input type='hidden' name='storenm' value='<c:out escapeXml="false" value="${storenm}"/>'/>
    	<input type='hidden' name='user_id' value='<c:out escapeXml="false" value="${user_id}"/>'/>
      </form>      
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>