<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="otl" uri="/WEB-INF/tlds/oneap-taglibs.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- conversion a203 -->
<html lang="ko">
<head>
  <title>뷰티포인트 통합회원 전환 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	  
	  $("#all_chk").change(function() {
		disabledOnOff($(this).prop("checked"));
	  });
	
	  $("input[type=checkbox]:not(#all_chk)").change(function() {
		disabledOnOff();
	  });
	  
		$('#do-convs').on('click', function() { // A0205 통합회원전환완료
		  
			$(this).attr('disabled', 'disabled');
	  
			var data = {
				id: OMNI.auth.encode(OMNIEnv.pprs, $('#loginid').val().trim()),
				pw: OMNI.auth.encode(OMNIEnv.pprs, $('#loginpassword').val()),
				cpw: OMNI.auth.encode(OMNIEnv.pprs, $('#loginconfirmpassword').val())
			};
		  
			$.ajax({
				url:OMNIEnv.ctx + '/convs/statuscheck',
				type:'post',
				data:JSON.stringify(data),
				async: false,
				dataType:'json',
				contentType : 'application/json; charset=utf-8',
				success: function(data) {
					if (data.resultCode === '1') {
						$('#id').val(OMNI.auth.encode(OMNIEnv.pprs, $('#loginid').val()));
						$('#pw').val(OMNI.auth.encode(OMNIEnv.pprs, $('#loginpassword').val()));  
						$('#cpw').val(OMNI.auth.encode(OMNIEnv.pprs, $('#loginconfirmpassword').val()));
						$('#convsType').val(OMNI.auth.encode(OMNIEnv.pprs, '203'));
						$('#trnsForm').find(':checkbox:not(:checked)').attr('value', 'off');
						var checkedbuff = '';
						$('#trnsForm').find(':checkbox').each(function(idx, val) {
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
						$('#trnsForm').append(checkedbuff);
						
						OMNI.loading.show('processing');
						
						$('#trnsForm')
					  	.attr('action', OMNIEnv.ctx + '/convs/finish')
					  	.submit();
					} else if (data.resultCode === '-2') {
						$('#do-convs').removeAttr('disabled');
						OMNI.popup.error({contents:'아이디가 규칙에 맞지 않습니다.'});
					} else if (data.resultCode === '-4') {
						$('#do-convs').removeAttr('disabled');
						OMNI.popup.error({contents:'비밀번호가 규칙에 맞지 않습니다.'});
					} else {
						$('#do-convs').removeAttr('disabled');
						OMNI.popup.error({contents:'정보를 확인하세요.'});
					}
				},
				error: function() {
					OMNI.popup.error({contents:'정보를 확인하세요.'});
				}
			});  
		  
		});
		
		$('#phone-cert').on('click', function() {
			location.href = OMNIEnv.ctx + '/cert/convs-ipin-phone';	
		});
		
	});
  
  var disabledOnOff = function(allChecked) {
		var requiredCheckboxLength = $("input[type=checkbox].required").length;
		var checkedRequiredCheckboxLength = $("input[type=checkbox].required:checked").length;
		
		var condition = requiredCheckboxLength == checkedRequiredCheckboxLength;
		
		if (allChecked != undefined) {
			condition = allChecked;
		}
		
		<c:if test="${certType eq 'ipin'}">
		condition &= $("#phone").val() !== '';
		</c:if>
		
		condition &= $("#loginid").val() !== '';
		condition &= $("#loginpassword").val() !== '';
		condition &= $("#loginconfirmpassword").val() !== '';
		condition &= $("#loginpassword").val() === $("#loginconfirmpassword").val();
		
		if (condition) {
			$(".btn_submit").find("button[type=button]").removeAttr("disabled");
			$(".agreeY").show();
			$(".agreeN").hide();
		}
		else {
			$(".btn_submit").find("button[type=button]").attr("disabled", "disabled");
			$(".agreeN").show();
			$(".agreeY").hide();
		}
	  };

	  var closeAction = function() {
			
		OMNI.popup.open({
			id:'next-warn',
			content: '통합회원 전환을 멈추고, 서비스<br/>화면으로 돌아가시겠습니까?<br/>(현재 아이디는 로그아웃됩니다.)',
			gaArea:'통합회원 전환 안내',
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
				$.ajax({url:OMNIEnv.ctx + '/ga/tagging/join/stop/' + encodeURIComponent("경로전환"),type:'get'});

				
				var UserAgent = navigator.userAgent;
				var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
				<c:if test="${mobile}">
					isMobile = true;
				</c:if>
				
				if(!isMobile && '<c:out escapeXml="false" value="${sessionScope.popup}"/>' == 'true') {
					window.close();
				} else {
					if('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>' == null || '<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>' == "") {
						location.href = '<c:out escapeXml="false" value="${home}"/>';
					} else {
						location.href = decodeURIComponent('<c:out escapeXml="false" value="${sessionScope.cancelUri}"/>');
					}				
				}
				// location.href = '<c:out escapeXml="false" value="${home}" />';
			}
			
		});
		$('.layer_wrap').focus();
	  };
	  var executeAction = function() {
	  };
	  var disabledAction = function() {
		  $("#do-convs").attr('disabled', 'disabled');
	  }; 	  
  </script> 
  <script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-idpwd-check.js?ver=<c:out value="${rv}"/>"></script>
  <script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-terms.js?ver=<c:out value="${rv}"/>"></script>
</head>

<body>
	<div id='agree-contents' style='display:none'></div>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="뷰티포인트 통합회원 전환" gaArea="통합회원 전환 01 (자체)" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>동일한 아이디가 이미 존재합니다. 새로운 아이디와 비밀번호를 설정해 주세요.</h2>
      </div>
      <div class="sec_join">
        <div class="user_info">
          <h3><em class="tit tit_w20">아이디</em> <c:out value="${loginid}" /> <span class="fc_red">(ID 변경 필요)</span></h3>
          <dl class="dt_w20">
            <dt>이름</dt>
            <dd><c:out value="${name}" /></dd>
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
        <form id='trnsForm' method='post' action=''>
        	<input type='hidden' id='previd' name='previd' value='<c:out escapeXml="false" value="${loginid}" />'/>
        	<input type='hidden' id='id' name='id'/>
        	<input type='hidden' id='pw' name='pw'/>
        	<input type='hidden' id='cpw' name='cpw'/>
        	<input type='hidden' id='xno' name='xno' value='<c:out escapeXml="false" value="${xincsno}" />'/>
        	<input type='hidden' id='convsType' name='convsType'/>
          <div class="input_form">
            <span class="inp" id="loginid-span">
              <input type="text" oninput="maxLengthCheck(this)" id='loginid' class="inp_text" maxlength="12" placeholder="아이디 (영문 또는 숫자 4-12자)" ap-click-area="통합회원 전환 01 (자체)" ap-click-name="통합회원 전환 01 (자체) - 아이디 입력란" ap-click-data="아이디 입력"  title="아이디 입력"/>
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="loginid-guide-msg" class="form_guide_txt"></p>
          </div>
          <div class="input_form">
            <span class="inp" id="password-span">
              <input type="password" id='loginpassword' class="inp_text" maxlength="16" placeholder="비밀번호 (영문 소문자, 숫자, 특수문자 조합 8-16자)" ap-click-area="통합회원 전환 01 (자체)" ap-click-name="통합회원 전환 01 (자체) - 비밀번호 입력란" ap-click-data="비밀번호 입력"  title="비밀번호 입력"/>
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="password-guide-msg" class="form_guide_txt is_success"></p>
          </div>
          <div class="input_form">
            <span class="inp" id="confirm-password-span">
              <input type="password" id='loginconfirmpassword' class="inp_text" maxlength="16" placeholder="비밀번호 확인" ap-click-area="통합회원 전환 01 (자체)" ap-click-name="통합회원 전환 01 (자체) - 비밀번호 확인 입력란" ap-click-data="비밀번호 확인 입력"   title="비밀번호 확인 입력"/>
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
            <p id="confirm-password-guide-msg" class="form_guide_txt is_success"></p>
            <common:password-notice gaArea="통합회원 전환 01 (자체)"/>
          </div>
          <div class="all_agree_box is_open">
            <div class="all_chk">
              <span class="checkboxA">
                <input type="checkbox" id="all_chk"  title="모든 약관 및 정보 수신 동의"/>
                <label for="all_chk" ap-click-area="통합회원 전환 01 (자체)" ap-click-name="통합회원 전환 01 (자체) - 모든 약관 및 정보 수신 동의 체크 박스" ap-click-data="모든 약관 및 정보 수신 동의"><span class="checkbox_label">모든 약관 및 정보 수신 동의</span></label>
              </span>
              <button type="button" class="btn_all_view" ap-click-area="통합회원 전환 01 (자체)" ap-click-name="통합회원 전환 01 (자체) - 약관 항목 접힘/펼침 버튼" ap-click-data="약관 항목 접힘/펼침"><span class="blind">약관 닫기</span></button>
            </div>
            <div class="agree_list">
            <c:if test="${not empty corptermslist}">
              <strong class="txt_t">뷰티포인트 통합회원 약관</strong>
              <ul>
              <c:forEach items="${corptermslist}" var="corpterm" varStatus="corpstatus">
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_bp<c:out value="${corpterm.chCd}" /><c:out value="${corpstatus.count}" />" name="bpterms_check" <c:if test="${corpterm.tncAgrMandYn eq 'Y'}">class="required"</c:if> title="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />'"/>
                    <label for="i_agree_bp<c:out value="${corpterm.chCd}" /><c:out value="${corpstatus.count}" />" ap-click-area="통합회원 전환 01 (자체)" ap-click-name="통합회원 전환 01 (자체) - (<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 체크 박스" ap-click-data="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />'"><span class="checkbox_label">[<c:out value="${corpterm.tncAgrMandYnTxt}" />] <c:out value="${corpterm.tncTtl}" /></span></label>
                  </span>
                  <a href="javascript:;" ap-click-name="통합회원 전환 01 (자체) - (<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 상세 보기 버튼" ap-click-data="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 상세 보기" class="btn_link_bp" data-type="P" data-lnk="<c:out escapeXml="false" value="${corpterm.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${corpterm.chCd}"/>" data-lnkcd="<c:out escapeXml="false" value="${corpterm.tcatCd}"/>" data-lnkno="<c:out escapeXml="false" value="${corpterm.tncvNo}" />"><span class="blind">자세히보기</span></a>
                </li>
                <input type='hidden' name="bpTcatCds" value="<c:out escapeXml="false" value="${corpterm.tcatCd}"/>"/>
                <input type='hidden' name='bpTncvNos' value='<c:out escapeXml="false" value="${corpterm.tncvNo}" />'/>
              </c:forEach>
              </ul>
              </c:if>
            </div>
          </div>
          <div class="btn_submit">
            <button type="button" class="btnA btn_blue" ap-click-area="통합회원 전환 01 (자체)" ap-click-name="통합회원 전환 01 (자체) - 동의하고 통합회원 전환 버튼" ap-click-data="동의하고 통합회원 전환" id='do-convs' disabled>동의하고 통합회원 전환</button>
            <input type='hidden' name='trnsType' value='<c:out escapeXml="false" value="${trnsType}" />'/>
          </div>
          <p class="txt_c agreeN">필수 정보 및 약관을 모두 확인해주세요.</p>
          <p class="txt_c agreeY" style="display:none;">약관을 확인했고, 동의합니다.</p>
        </form>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>