<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>    
<%@ page import="com.amorepacific.oneap.common.util.WebUtil"%>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- ME-FO-A0207 plogin id regist bp -->
<html lang="ko">
<head>
  <title>통합 아이디 등록 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true" authCategory="true"/>
  <script type="text/javascript">
  $(document).ready(function() {

		$('#do-regist-login').on('click', function() {
		
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
				$('#joinform').find(':checkbox:not(:checked)').attr('value', 'off');  //.prop('checked', true);
				
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
				.attr('action', OMNIEnv.ctx + '/join/plogin-regist-bp')
				.submit();
				
			} else {
				OMNI.popup.open({
					id:'regist-error-popup',
				 	content:'통합 아이디 등록 정보가 올바르지 않습니다.',
				 	closelabel:'닫기',
					closeclass:'btn_blue'
			 	});	
				$('.layer_wrap').focus();
			}		
			
		});
		
		$('#do-next').on('click', function() {
			goAction();
		});
		
		$("#all_chk").on('click', function() {
			disabledOnOff($(this).prop("checked"));
			marketing_disabledOnOff_channel($(this).prop("checked"));
		});
		
		$("input[type=checkbox]:not(#all_chk)").on('change', function() {
			disabledOnOff();
		});
		
		$("#i_agree_bp_top_channel").on('click', function() {
			marketing_disabledOnOff_channel($(this).prop("checked"));
		});
	  });
	  
		var goAction = function() {
			OMNI.popup.open(
					{
						id:'regist-id-warn', 
						content:'통합 아이디 등록을 멈추고, 서비스<br/>화면으로 돌아가시겠습니까?<br/>(현재 계정은 로그아웃 됩니다.)',
						gaArea:'통합 아이디 등록',
						gaOkName:'확인 버튼 (중단 팝업)',
						gaCancelName:'취소 버튼 (중단 팝업)',
						oklabel:'취소',
						okclass:'btn_white',
						ok:function() {
							OMNI.popup.close({id:'regist-id-warn'});
						},
						closelabel:'확인',
						closeclass:'btn_blue',
						close:function() {
							window.AP_SIGNUP_TYPE = '중단';
							dataLayer.push({event: 'signup_complete'});	
							OMNI.popup.close({id:'regist-id-warn'});
							$.ajax({url:OMNIEnv.ctx + '/ga/tagging/join/stop/' + encodeURIComponent("경로전환"),type:'get'});
							//location.href = '<c:out escapeXml="false" value="${homeurl}" />';
							location.href = OMNIEnv.ctx + '/go-login';
						}
					}
				);  		
			$('.layer_wrap').focus();
	  	};
	  	
		var disabledOnOff = function(allChecked) {
			var requiredCheckboxLength = $("input[type=checkbox].required").length;
			var checkedRequiredCheckboxLength = $("input[type=checkbox].required:checked").length;
			var condition = requiredCheckboxLength == checkedRequiredCheckboxLength;
			if (allChecked !== undefined) {
				condition = allChecked;
			}
			
			condition &= $("#loginid").val() !== '';
			condition &= $("#loginpassword").val() !== '';
			condition &= $("#loginconfirmpassword").val() !== '';
			condition &= $("#loginpassword").val() === $("#loginconfirmpassword").val();
			condition &= $(".is_error").length === 0;
			
			if (condition) {
				$("#do-regist-login").removeAttr('disabled');
			} else {
				$("#do-regist-login").attr('disabled', 'disabled');
			}
		};
		
		  var executeAction = function() {
			  if(!$('#do-regist-login').is(':disabled')) {
			  	$('#do-regist-login').trigger('click');
			  }
		  };
		  var disabledAction = function() {
			  $("#do-regist-login").attr('disabled', 'disabled');
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
  <tagging:google/>
</head>
<body>
	<tagging:google noscript="true"/>
	<div id='agree-contents' style='display:none'></div>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="통합 아이디 등록" type="goaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>뷰티포인트 통합 아이디를 등록해주세요.</h2>
        <p>회원님은 <c:out value="${channelName}" /> 가입 회원입니다.<br class="w320" />뷰티포인트 통합 아이디를 등록하면 아모레퍼시픽 브랜드의 온라인 서비스를 이용할 수 있습니다.</p>
      </div>
      <div class="">
        <div class="user_info mb13">
          <dl class="dt_w33">
            <dt>이름</dt>
            <dd><c:out value="${name}" /></dd>
          </dl>
          <c:if test="${not empty mobile}">
          <dl class="dt_w33">
            <dt>휴대폰 번호</dt>
            <dd><c:out value="${mobile}" /></dd>
          </dl>
          </c:if>
          <dl class="dt_w33">
            <dt>가입일</dt>
            <dd><c:out value="${joindate}" /></dd>
          </dl>
        </div>
      <form id='joinform' method='post' action=''>
      		<input type='hidden' name='unm' id='unm' value='<c:out value="${unm}" />'/>
			<input type='hidden' name='uid' id='uid'/>
			<input type='hidden' name='upw' id='upw'/>
			<input type='hidden' name='ucpw' id='ucpw'/>
			<input type='hidden' name='incsno' value='<c:out escapeXml="false" value="${xincsno}" />'/>
        <div class="input_form">
          <span class="inp" id="loginid-span">
            <input type="text" oninput="maxLengthCheck(this)" id="loginid" class="inp_text" maxlength="12" placeholder="아이디 (영문 또는 숫자 4-12자)" ap-click-area="통합 아이디 등록" ap-click-name="통합 아이디 등록 - 아이디 입력란" ap-click-data="아이디 입력"  title="아이디 입력"/>
            <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
          </span>
          <p id="loginid-guide-msg" class="form_guide_txt"></p>
        </div>
        <div class="input_form">
          <span class="inp" id="password-span">
            <input type="password" id="loginpassword" class="inp_text" maxlength="16" placeholder="비밀번호 (영문 소문자, 숫자, 특수문자 조합 8-16자)" ap-click-area="통합 아이디 등록" ap-click-name="통합 아이디 등록 - 비밀번호 입력란" ap-click-data="비밀번호 입력"   title="비밀번호 입력"/>
            <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
          </span>
          <p id="password-guide-msg" class="form_guide_txt"></p>
        </div>
        <div class="input_form">
          <span class="inp" id="confirm-password-span">
            <input type="password" id="loginconfirmpassword" class="inp_text" maxlength="16" placeholder="비밀번호 확인" ap-click-area="통합 아이디 등록" ap-click-name="통합 아이디 등록 - 비밀번호 확인 입력란" ap-click-data="비밀번호 확인 입력"   title="비밀번호 확인 입력"/>
            <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
          </span>
          <p id="confirm-password-guide-msg" class="form_guide_txt"></p>
          <common:password-notice gaArea="통합 아이디 등록"/>
        </div>
        <c:if test="${not empty corptermslist || not empty terms}">
        <div class="all_agree_box is_open">
          <div class="all_chk">
            <span class="checkboxA">
              <input type="checkbox" id="all_chk" ap-click-area="통합 아이디 등록" ap-click-name="통합 아이디 등록 - 모든 약관 수신 동의 체크 박스" ap-click-data="모든 약관 수신 동의" title="모든 약관 수신 동의"/>
              <label for="all_chk"><span class="checkbox_label">모든 약관 및 정보 수신 동의</span></label>
            </span>
            <button type="button" class="btn_all_view" ap-click-area="통합 아이디 등록" ap-click-name="통합 아이디 등록 - 약관 항목 접힘/펼침 버튼" ap-click-data="약관 항목 접힘/펼침">
            	<span class="blind">약관 닫기</span>
            </button>
          </div>
          <div class="agree_list">
          <c:if test="${not empty corptermslist}">
            <strong class="txt_t">뷰티포인트 통합회원 약관</strong>
            <ul>
              <c:forEach items="${corptermslist}" var="corpterm" varStatus="corpstatus">
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_<c:out value="${corpstatus.count}" />" name="bpterms_check" <c:if test="${corpterm.tncAgrMandYn eq 'Y'}">class="required"</c:if>title="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />'" />
                    <label for="i_agree_<c:out value="${corpstatus.count}" />" ap-click-area="통합 아이디 등록" ap-click-name="통합 아이디 등록 - (<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 체크 박스" ap-click-data="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />'"><span class="checkbox_label">[<c:out value="${corpterm.tncAgrMandYnTxt}" />] <c:out value="${corpterm.tncTtl}" /></span></label>
                  </span>
                  <a href="javascript:;" ap-click-name="통합 아이디 등록 - (<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 상세 보기 버튼" ap-click-data="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 상세 보기" class="btn_link_bp" data-type="P" data-lnk="<c:out escapeXml="false" value="${corpterm.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${corpterm.chCd}"/>" data-lnkcd="<c:out escapeXml="false" value="${corpterm.tcatCd}"/>" data-lnkno="<c:out escapeXml="false" value="${corpterm.tncvNo}" />"><span class="blind">자세히보기</span></a>
                </li>
                <input type='hidden' name="bpTcatCds" value="<c:out escapeXml="false" value="${corpterm.tcatCd}"/>"/>
                <input type='hidden' name='bpTncvNos' value='<c:out escapeXml="false" value="${corpterm.tncvNo}" />'/>
              </c:forEach>                
            </ul>
            </c:if>
            <c:if test="${not empty terms}">
            <strong class="txt_t"><c:out value="${channelName}" /> 회원 약관</strong>
            <ul>
        	<c:forEach items="${terms}" var="term" varStatus="status">
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_<c:out value="${term.chCd}" /><c:out value="${status.count}" />" <c:if test="${term.tncAgrMandYn eq 'Y'}">class="required"</c:if> name="terms_check" data-cd="<c:out escapeXml="false" value="${term.tcatCd}" />" data-no="<c:out escapeXml="false" value="${term.tncvNo}" />" title="(<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />'"/>
                    <label for="i_agree_<c:out value="${term.chCd}" /><c:out value="${status.count}" />" ap-click-area="통합 아이디 등록" ap-click-name="통합 아이디 등록 - (<c:out value="${term.tncAgrMandYnTxt}" />] '<c:out value="${term.tncTtl}" />' 체크 박스" ap-click-data="(<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />'"><span class="checkbox_label">[<c:out value="${term.tncAgrMandYnTxt}" />] <c:out value="${term.tncTtl}" /></span></label>
                  </span>
                  <a href="javascript:;" ap-click-area="통합 아이디 등록" ap-click-name="통합 아이디 등록 - (<c:out value="${term.tncAgrMandYnTxt}" />] '<c:out value="${term.tncTtl}" />' 상세보기 버튼" ap-click-data="(<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />' 상세보기" class="btn_link" data-lnk="<c:out escapeXml="false" value="${term.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${term.chCd}" />" data-lnkcd="<c:out escapeXml="false" value="${term.tcatCd}" />" data-lnkno="<c:out escapeXml="false" value="${term.tncvNo}" />"><span class="blind">자세히보기</span></a>
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
		                  <common:info-notice-ch gaArea="통합 아이디 등록" channelCd="${chcd}" channelName="${channelName}"/>
		                </li>              
				     </ul> 
				</li> 
            </ul>
            </c:if>
          </div>
        </div>
        </c:if>
        <p class="txt_l">* 기존에 사용하시던 <c:out value="${channelName}" /> 아이디는 사용이 제한되며 지금 등록하시는 뷰티포인트 통합 아이디로 로그인 가능합니다.</p>
        <div class="btn_submit ver2">
          <button type="button" class="btnA btn_white" data-pop="btn-open-pop" data-target="#pop_stop_regist" ap-click-area="통합 아이디 등록" ap-click-name="통합 아이디 등록 - 다음에 하기 버튼" ap-click-data="다음에 하기" id='do-next'>다음에 하기</button>
          <button type="button" class="btnA btn_blue" id='do-regist-login' disabled ap-click-area="통합 아이디 등록" ap-click-name="통합 아이디 등록 - 등록하고 로그인 버튼" ap-click-data="등록하고 로그인">등록하고 로그인</button>
        </div>
      </form>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>