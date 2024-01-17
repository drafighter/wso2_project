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
<!DOCTYPE html><!-- join step -->
<html lang="ko">
<head>
  <title>정보입력 및 약관동의 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true" authCategory="true"/>
  
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

			var validId = OMNI.auth.validLoginId(id, {checkPassword:pw, serverCheck:true});
			var validPw = OMNI.auth.validPassword(pw, {checkId:id,confirmId:'loginconfirmpassword', serverCheck:true});
			$('#uid').val(OMNI.auth.encode(OMNIEnv.pprs, id));
			$('#upw').val(OMNI.auth.encode(OMNIEnv.pprs, pw));
			$('#ucpw').val(OMNI.auth.encode(OMNIEnv.pprs, cpw));
			if (validId.code > 0 && validPw.code > 0) {
				$('#joinform').find(':checkbox:not(:checked)').attr('value', 'off'); //.prop('checked', true);
				var checkedbuff = '';
				$('#joinform').find(':checkbox').each(function(idx, val) {
					var id = $(this).attr('id');
					if (id === 'all_chk') {
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
				
				$('#loginid').val('');
				$('#loginpassword').val('');
				$('#loginconfirmpassword').val('');
				
				OMNI.loading.show('processing');
				
				$('#joinform')
				.attr('action', OMNIEnv.ctx + '/join/setting')
				.submit();
				
			} else {
				$('#dojoin').removeAttr('disabled');
				return;
			}
		});

		
		$("#all_chk").on('click', function() {
			disabledOnOff($(this).prop("checked"));
			marketing_disabledOnOff($(this).prop("checked"));
			marketing_disabledOnOff_channel($(this).prop("checked"));
		});
		
		$("input[type=checkbox]:not(#all_chk)").on('change', function() {
			disabledOnOff();
		});

		$('#phone-cert').on('click', function() {
			location.href = OMNIEnv.ctx + '/cert/ipin-phone';	
		});
		$("#i_agree_bp_top").on('click', function() {
			marketing_disabledOnOff($(this).prop("checked"));
		});
		$("#i_agree_bp_top_channel").on('click', function() {
			marketing_disabledOnOff_channel($(this).prop("checked"));
		});
	});
  
  
  var closeAction = function() {
	  <c:choose>
		<c:when test="${headertype eq 'cancelbtn'}">
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
					$('.close-action').focus();
				},
				closelabel:'확인',
				closeclass:'btn_blue',
				close: function() {
					window.AP_SIGNUP_TYPE = '중단';
					dataLayer.push({event: 'signup_complete'});	
					OMNI.popup.close({ id: 'next-warn' });
					$.ajax({url:OMNIEnv.ctx + '/ga/tagging/join/stop/' + encodeURIComponent("회원가입"),type:'get'});
	
					if("<c:out value='${sessionScope.cancelUri}'/>" != null && "<c:out value='${sessionScope.cancelUri}'/>" != "") {
	    				location.href = decodeURIComponent(("<c:out value='${sessionScope.cancelUri}'/>").replace(/&amp;/g, "&"));	
	    			} else {
	    				location.href = '${url}';	
	    			}	
				}
				
			});
			$('.layer_wrap').focus();
		</c:when>
		<c:otherwise>
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
					$('.close-action').focus();
				},
				closelabel:'확인',
				closeclass:'btn_blue',
				close: function() {
					window.AP_SIGNUP_TYPE = '중단';
					dataLayer.push({event: 'signup_complete'});	
					OMNI.popup.close({ id: 'next-warn' });
					$.ajax({url:OMNIEnv.ctx + '/ga/tagging/join/stop/' + encodeURIComponent("회원가입"),type:'get'});
	
					location.href = OMNIEnv.ctx + '/go-join';
				}
				
			});
			$('.layer_wrap').focus();
		</c:otherwise>
	</c:choose>
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
		
		condition &= $("#loginid").val() !== '';
		condition &= $("#loginpassword").val() !== '';
		condition &= $("#loginconfirmpassword").val() !== '';
		condition &= $("#loginpassword").val() === $("#loginconfirmpassword").val();
		condition &= $(".is_error").length === 0;
		
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
  		if(!$('#dojoin').is(':disabled')) {
			$('#dojoin').trigger('click');
		}
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
			<c:if test ="${empty terms && not empty marketingChannel }">
				$("input[id^='i_agree_chagree_mrk']").removeAttr('disabled');
			</c:if>
		} else {
			$("input[id='i_agree_bp_marketing']").removeAttr('checked');
			$("input[id^='i_agree_bp_marketing']").prop('checked', false);
			$("input[id^='i_agree_bp_marketing']").attr('disabled', 'disabled');
			
			<c:if test ="${empty terms && not empty marketingChannel}">
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
		} else {
			$("input[id^='i_agree_chagree_mrk']").prop('checked', false);
			$("input[id='i_agree_chagree_mrk']").removeAttr('checked');
			$("input[id='i_agree_chagree_mrk']").attr('disabled', 'disabled');
		}
  	};
  </script>   
  <script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-idpwd-check.js?ver=<c:out value="${rv}"/>"></script>
  <script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-terms.js?ver=<c:out value="${rv}"/>"></script>
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
            <dt>생년월일 (성별)</dt>
            <dd><c:out value="${otl:bdt(sessionScope.cert.birth)}"/> (<c:choose><c:when test="${sessionScope.cert.gender eq 'M'}">남자</c:when><c:otherwise>여자</c:otherwise></c:choose>)</dd>
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
        <form method='post' action='' id='joinform'>
        	<input type='hidden' name='unm' id='unm' value='<c:out value="${unm}" />'/>
			<input type='hidden' name='uid' id='uid'/>
			<input type='hidden' name='upw' id='upw'/>
			<input type='hidden' name='ucpw' id='ucpw'/>
			<input type='hidden' name='incsno' value='<c:out escapeXml="false" value="${xincsno}" />'/>
			<input type='hidden' name='chCd' value='<c:out escapeXml="false" value="${chCd}" />'/>
			<input type='hidden' name='integrateid' value='false'/>
          <div class="input_form">
            <span class="inp" id="loginid-span">
              <input type="text" oninput="maxLengthCheck(this)" id="loginid" name="loginid" autocomplete="off" class="inp_text" maxlength="12" placeholder="아이디 (영문 또는 숫자 4-12자)" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - 아이디 입력란" ap-click-data="아이디 입력"  title="아이디 입력"/>
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="loginid-guide-msg" class="form_guide_txt"></p>
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
              <input type="password" id="loginconfirmpassword" name="loginconfirmpassword" autocomplete="off" class="inp_text" maxlength="16" placeholder="비밀번호 확인" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - 비밀번호 확인 입력란" ap-click-data="비밀번호 확인 입력"   title="비밀번호 확인 입력"/>
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="confirm-password-guide-msg" class="form_guide_txt is_success"></p>
            <common:password-notice gaArea="정보입력 및 약관동의"/>
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
              <strong class="txt_t">뷰티포인트 통합회원 약관</strong>
              <ul>
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_bpterms1" name="bpterms_check" class="required" title="(필수) '뷰티포인트 서비스 이용 약관'"/>
                    <label for="i_agree_bpterms1" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (필수) '뷰티포인트 서비스 이용 약관' 체크 박스" ap-click-data="(필수) '뷰티포인트 서비스 이용 약관'"><span class="checkbox_label">[필수] 뷰티포인트 서비스 이용 약관</span></label>
                  </span>
                  <a href="javascript:;" class="btn_link_bp" data-type='S' data-chcd='<c:out escapeXml="false" value="${chCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (필수) '뷰티포인트 서비스 이용 약관' 상세 보기 버튼" ap-click-data="(필수) '뷰티포인트 서비스 이용 약관' 상세 보기 버튼">
                  <span class="blind">자세히보기</span></a>
                <input type='hidden' name="bpTcatCds" value="010"/>
                <input type='hidden' name='bpTncvNos' value='1.0'/>
                </li>
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_bpterms2" name="bpterms_check" class="required" title="(필수) '개인정보 이용 및 수집에 대한 동의'"/>
                    <label for="i_agree_bpterms2" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (필수) '개인정보 이용 및 수집에 대한 동의' 체크 박스" ap-click-data="(필수) '개인정보 이용 및 수집에 대한 동의'"><span class="checkbox_label">[필수] 개인정보 이용 및 수집에 대한 동의</span></label>
                  </span>
                  <a href="javascript:;" class="btn_link_bp" data-type='C' data-chcd='<c:out escapeXml="false" value="${chCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (필수) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼" ap-click-data="(필수) '개인정보 이용 및 수집에 대한 동의' 상세 보기 버튼">
                  <span class="blind">자세히보기</span></a>
                <input type='hidden' name="bpTcatCds" value="030"/>
                <input type='hidden' name='bpTncvNos' value='1.0'/>
                </li>
              <c:forEach items="${corptermslist}" var="corpterm" varStatus="corpstatus">
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_<c:out value="${corpstatus.count}" />" name="bpterms_check" <c:if test="${corpterm.tncAgrMandYn eq 'Y'}">class="required"</c:if>title="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />'" />
                    <label for="i_agree_<c:out value="${corpstatus.count}" />" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 체크 박스" ap-click-data="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />'"><span class="checkbox_label">[<c:out value="${corpterm.tncAgrMandYnTxt}" />] <c:out value="${corpterm.tncTtl}" /></span></label>
                  </span>
                  <a href="javascript:;" ap-click-name="정보입력 및 약관동의 - (<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 상세 보기 버튼" ap-click-data="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 상세 보기" class="btn_link_bp" data-type='P' data-lnk="<c:out escapeXml="false" value="${corpterm.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${corpterm.chCd}"/>" data-lnkcd="<c:out escapeXml="false" value="${corpterm.tcatCd}"/>" data-lnkno="<c:out escapeXml="false" value="${corpterm.tncvNo}" />"><span class="blind">자세히보기</span></a>
                </li>
                <input type='hidden' name="bpTcatCds" value="<c:out escapeXml="false" value="${corpterm.tcatCd}"/>"/>
                <input type='hidden' name='bpTncvNos' value='<c:out escapeXml="false" value="${corpterm.tncvNo}" />'/>
              </c:forEach>                
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_bpterms4" name="bpterms_check" title="(선택) '개인정보 제 3자 제공 동의'"/>
                    <label for="i_agree_bpterms4" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '개인정보 제 3자 제공 동의' 체크 박스" ap-click-data="(선택) '개인정보 제 3자 제공 동의'"><span class="checkbox_label">[선택] 개인정보 제 3자 제공 동의</span><em class="sm">*외부 컨텐츠 마케팅 활용</em></label>
                  </span>
                  <a href="javascript:;" class="btn_link_bp" data-type='A' data-chcd='<c:out escapeXml="false" value="${chCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '개인정보 제 3자 제공 동의' 상세 보기 버튼" ap-click-data="(선택) '개인정보 제 3자 제공 동의' 상세 보기 버튼">
                  <span class="blind">자세히보기</span></a>
                <input type='hidden' name="bpTcatCds" value="070"/>
                <input type='hidden' name='bpTncvNos' value='1.1'/>
                </li>
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_bpterms5" name="bpterms_check" title="(선택) '국외 이전 동의'"/>
                    <label for="i_agree_bpterms5" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '국외 이전 동의' 체크 박스" ap-click-data="(선택) '국외 이전 동의'"><span class="checkbox_label">[선택] 국외 이전 동의</span></label>
                  </span>
                  <a href="javascript:;" class="btn_link_bp" data-type='T' data-chcd='<c:out escapeXml="false" value="${chCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '국외 이전 동의' 상세 보기 버튼" ap-click-data="(선택) '국외 이전 동의' 상세 보기 버튼">
                  <span class="blind">자세히보기</span></a>
                <input type='hidden' name="bpTcatCds" value="060"/>
                <input type='hidden' name='bpTncvNos' value='1.0'/>
                </li>
              </ul>
              <strong class="txt_t">뷰티포인트 광고성 정보 수신 동의</strong>
              <ul>
                <li>
                  <span class="checkboxA">
                  	<input type="checkbox" id="i_agree_bp_top" name="bpterms_check" title="(선택) '개인정보 수집 및 이용 동의 (마케팅)'"/>
                    <label for="i_agree_bp_top" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '개인정보 수집 및 이용 동의 (마케팅)' 체크 박스" ap-click-data="(선택) '개인정보 수집 및 이용 동의 (마케팅)'"><span class="checkbox_label">[선택] 개인정보 수집 및 이용 동의 (마케팅)</span><em class="sm">*개인정보 수집 및 이용(마케팅)에 동의 하셔야 문자 수신 동의가 가능합니다.</em></label>
                  </span>
                  <a href="javascript:;" class="btn_link_bp" data-type='M' data-chcd='<c:out escapeXml="false" value="${chCd}" />' ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '개인정보 수집 및 이용 동의 (마케팅)' 상세 보기 버튼" ap-click-data="(선택) '개인정보 수집 및 이용 동의 (마케팅)' 상세 보기 버튼">
                  <span class="blind">자세히보기</span></a>
	                <input type='hidden' name="bpTcatCds" value="050"/>
	                <input type='hidden' name='bpTncvNos' value='1.0'/>
                </li>
                <li>
	                <ul>
	                 <li>
	                  <span class="checkboxA select_agree_low">
	                    <input type="checkbox" id="i_agree_bp_marketing" name='marketing_check' disabled='disabled' title="(선택) '뷰티포인트 문자 수신 동의'"/>
	                    <label for="i_agree_bp_marketing" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '뷰티포인트 문자 수신 동의' 체크 박스" ap-click-data="(선택) '뷰티포인트 문자 수신 동의'"><span class="checkbox_label">[선택] 뷰티포인트 문자 수신 동의</span></label>
	                  </span>
	                <input type='hidden' name='marketingChcd' value='000'/>
	                </li>
	                <li>
	                <span class="checkboxA select_agree_low">
	                    <input type="checkbox" id="i_agree_bp_marketing_2" name='marketing_check' disabled='disabled' title="(선택) '온라인 사이트 문자 수신 동의'"/>
	                    <label for="i_agree_bp_marketing_2" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '온라인 사이트 문자 수신 동의' 체크 박스" ap-click-data="(선택) '온라인 사이트 문자 수신 동의'"><span class="checkbox_label">[선택] 온라인 사이트 문자 수신 동의</span></label>
	                  </span>
	                <input type='hidden' name='marketingChcd' value='030'/>
	                </li>
	                <li>
	                	<c:if test="${empty terms && not empty marketingChannel}">
	                		<ul class='mrk_pannel'>
						        <li>
						          <span class="checkboxA select_agree_low">
						            <input type="checkbox" id="i_agree_chagree_mrk" name='marketing_check' disabled='disabled'  title="(선택) '문자 수신 동의'"/>
						            <label for="i_agree_chagree_mrk" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '문자 수신 동의' 체크 박스" ap-click-data="(선택) '문자 수신 동의'"><span class="checkbox_label">[선택] ${marketingChannel.chCdNm} 문자 수신 동의</span></label>
						          </span>
						        <input type='hidden' name='marketingChcd' value='<c:out value="${marketingChannel.chCd}" />'/>
						        </li>
						      </ul>           
	                	</c:if>
	                </li>
	                <li>
	                  <common:info-notice gaArea="약관동의" channelName="뷰티포인트"/>
	                </li>                 
	              </ul>
              </li>
              </ul>
              
              <c:if test="${not empty terms}">
              <strong class="txt_t"><c:out value="${channelName}" /> 회원 약관</strong>
              <ul>
        		<c:forEach items="${terms}" var="term" varStatus="status">
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_${term.chCd}${status.count}" <c:if test="${term.tncAgrMandYn eq 'Y'}">class="required"</c:if> name="terms_check" data-cd="<c:out escapeXml="false" value="${term.tcatCd}" />" data-no="<c:out escapeXml="false" value="${term.tncvNo}" />"  title="(<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />'"/>
                    <label for="i_agree_${term.chCd}${status.count}" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - <c:out value="${channelName}" /> (<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />' 체크 박스" ap-click-data="(<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />'"><span class="checkbox_label">[<c:out value="${term.tncAgrMandYnTxt}" />] <c:out value="${term.tncTtl}" /></span></label>
                  </span>
                  <a href="javascript:;" class="btn_link" data-lnk="<c:out escapeXml="false" value="${term.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${term.chCd}" />" data-lnkcd="<c:out escapeXml="false" value="${term.tcatCd}" />" data-lnkno="<c:out escapeXml="false" value="${term.tncvNo}" />"
                  	ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - <c:out value="${channelName}" /> (<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />' 상세 보기 버튼" ap-click-data="(<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />' 상세 보기 버튼"><span class="blind">자세히보기</span></a>
                </li>
                <input type='hidden' name="tcatCds" value="<c:out escapeXml="false" value="${term.tcatCd}" />"/>
                <input type='hidden' name="tncvNos" value="<c:out escapeXml="false" value="${term.tncvNo}" />"/>
        		</c:forEach>
              </ul>
              <strong class="txt_t"><c:out value="${channelName}" /> 광고성 정보 수신 동의</strong>
              <ul>
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_bp_top_channel" name="terms_check" title="(선택) '개인정보 수집 및 이용동의 (마케팅)'"/>
                    <label for="i_agree_bp_top_channel" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '개인정보 수집 및 이용동의 (마케팅)' 체크 박스" ap-click-data="(선택) '개인정보 수집 및 이용동의 (마케팅)'"><span class="checkbox_label">[선택] 개인정보 수집 및 이용동의 (마케팅)(<c:out value="${channelName}" />)</span><em class="sm">*개인정보 수집 및 이용(마케팅)에 동의 하셔야 문자 수신 동의가 가능합니다.</em></label>
                  </span>
                  <a href="javascript:;" class="btn_link" data-lnk="<c:out escapeXml="false" value="${terms_marketing.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${chCd}" />" data-lnkcd="<c:out escapeXml="false" value="${terms_marketing.tcatCd}" />" data-lnkno="<c:out escapeXml="false" value="${terms_marketing.tncvNo}" />" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - <c:out value="${channelName}" /> (<c:out value="${terms_marketing.tncAgrMandYnTxt}" />) '<c:out value="${terms_marketing.tncTtl}" />' 상세 보기 버튼" ap-click-data="(<c:out value="${terms_marketing.tncAgrMandYnTxt}" />) '<c:out value="${terms_marketing.tncTtl}" />' 상세 보기 버튼"><span class="blind">자세히보기</span></a>
                  <%-- <a href="javascript:;" ap-click-area="약관동의" ap-click-name="약관동의 - (<c:out value="${terms_marketing.tncAgrMandYnTxt}" />) '<c:out value="${terms_marketing.tncTtl}" />' 상세보기 버튼" ap-click-data="(<c:out value="${terms_marketing.tncAgrMandYnTxt}" />) '<c:out value="${terms_marketing.tncTtl}" />' 상세보기" class="btn_link_bp"  data-chcd="${chCd}" data-lnk="<c:out escapeXml="false" value="${terms_marketing.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${terms_marketing.chCd}" />"  data-lnkcd="<c:out escapeXml="false" value="${terms_marketing.tcatCd}" />" data-type="Y" data-lnkno="<c:out escapeXml="false" value="${terms_marketing.tncvNo}" />"><span class="blind">자세히보기</span></a> --%>
                <input type='hidden' name="tcatCds" value="<c:out escapeXml="false" value="${terms_marketing.tcatCd}" />"/>
         		<input type='hidden' name="tncvNos" value="<c:out escapeXml="false" value="${terms_marketing.tncvNo}" />"/>
                </li>
                <li>
	                <ul>
		                <li>
		                  <span class="checkboxA select_agree_low">
		                    <input type="checkbox" id="i_agree_chagree_mrk" name='marketing_check' disabled='disabled' title="(선택) '문자 수신 동의'"/>
		                    <label for="i_agree_chagree_mrk" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '문자 수신 동의' 체크 박스" ap-click-data="(선택) '문자 수신 동의'"><span class="checkbox_label">[선택] <c:out value="${channelName}" />  문자 수신 동의</span></label>
		                  </span>
				        <input type='hidden' name='marketingChcd' value='<c:out value="${chcd}" />'/>
		                </li>
						<li>
		                  <common:info-notice-ch gaArea="정보입력 및 약관동의" channelCd="${chcd}" channelName="${channelName}"/>
		                </li>		        
				     </ul> 
				</li>             
              </ul>
              </c:if>
              <%-- <c:if test="${empty terms && not empty marketingChannel}">
		      	<strong class="txt_t terms_mrk" id='chmrk-pannel-header'><c:out value="${marketingChannel.chCdNm}" /> 광고성 정보 수신 동의</strong>
			      <ul class='mrk_pannel'>
			        <li>
			          <span class="checkboxA">
			            <input type="checkbox" id="i_agree_chagree_mrk" name='marketing_check' title="(선택) '문자 수신 동의'"/>
			            <label for="i_agree_chagree_mrk" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '문자 수신 동의' 체크 박스" ap-click-data="(선택) '문자 수신 동의'"><span class="checkbox_label">[선택] 문자 수신 동의</span></label>
			          </span>
			        <input type='hidden' name='marketingChcd' value='<c:out value="${marketingChannel.chCd}" />'/>
			        </li>
					<li>
	                  <common:info-notice-ch gaArea="정보입력 및 약관동의" channelCd="${marketingChannel.chCd}" channelName="${marketingChannel.chCdNm}"/>
	                </li>		        
			      </ul>              
              </c:if> --%>
            </div>
          </div>
          <div class="btn_submit">
	          <c:choose>
				<c:when test="${joinAditor}">
					<button type="button" id="dojoin" class="btnA btn_blue" disabled ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - 동의하고 가입 버튼" ap-click-data="동의하고 가입">동의하고 계속하기</button>
				</c:when>
				<c:otherwise>
					<button type="button" id="dojoin" class="btnA btn_blue" disabled ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - 동의하고 가입 버튼" ap-click-data="동의하고 가입">동의하고 가입</button>
				</c:otherwise>
			</c:choose>
            <input type='hidden' name='joinType' id='joinType' value='<c:out value="${joinType}" />'/>
            <input type='hidden' name='joinStepType' id='joinStepType' value='<c:out value="${joinStepType}" />'/>
          </div>
          <p class="txt_c" id='not-set-terms'>가입 필수 정보 및 약관을 모두 확인해주세요.</p>
          <p class="txt_c" id='set-terms'>만 14세 이상이며, 가입약관에 동의합니다.</p>
      </div>
      </form>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>