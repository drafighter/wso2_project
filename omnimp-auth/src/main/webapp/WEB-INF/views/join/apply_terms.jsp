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
<!DOCTYPE html><!-- apply_terms -->
<html lang="ko">
<head>
  <title>약관동의 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="false" authCategory="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	  
	  $('#not-set-terms').show();
	  $('#set-terms').hide();

	
	$("#all_chk").change(function() {
		disabledOnOff($(this).prop("checked"));
		marketing_disabledOnOff_channel($(this).prop("checked"));
	});
	$("#i_agree_bp_top_channel").on('click', function() {
		marketing_disabledOnOff_channel($(this).prop("checked"));
	});
	$("input[type=checkbox]:not(#all_chk)").change(function() {
		disabledOnOff();
	});
	
  });
  
	function disabledOnOff(allChecked) {
		var requiredCheckboxLength = $("input[type=checkbox].required").length;
		var checkedRequiredCheckboxLength = $("input[type=checkbox].required:checked").length;
		
		var condition = requiredCheckboxLength == checkedRequiredCheckboxLength;
		
		if (allChecked != undefined) {
			condition = allChecked;
		}
		
		if (condition) {
			$('#not-set-terms').hide();
			$('#set-terms').show();
			$(".btn_submit").find("button[type=submit]").removeAttr("disabled");
			$(".agreeY").show();
			$(".agreeN").hide();
		}
		else {
			$('#not-set-terms').show();
			$('#set-terms').hide();
			$(".btn_submit").find("button[type=submit]").attr("disabled", "disabled");
			$(".agreeN").show();
			$(".agreeY").hide();
		}
  };
	
  var closeAction = function() {
		
		OMNI.popup.open({
			id:'next-warn',
			content: '약관동의를 멈추고, 서비스<br/>화면으로 돌아가시겠습니까?<br/>(현재 아이디는 로그아웃됩니다.)',
			gaArea:'약관동의',
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

		<c:if test='<c:out escapeXml="false" value="${offline}" />'>
			$('#offForm')
			.attr('action', '<c:out escapeXml="false" value="${home}" />')
			.submit();
		</c:if>
		<c:if test='<c:out escapeXml="false" value="${!offline}" />'>		
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
				// location.href = '<c:out escapeXml="false" value="${home}" />'; // 온라인 가입 : 화면 진입 이전의 채널 화면으로 이동 (비로그인 상태로 이동)
		</c:if>
			}  
			
		});
		$('.layer_wrap').focus();
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
  <script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-terms.js?ver=<c:out value="${rv}"/>"></script>
</head>

<body>
	<tagging:google noscript="true"/>
	<div id='agree-contents' style='display:none'></div>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="약관동의" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>아모레퍼시픽 뷰티포인트 회원입니다.</h2>
        <p><c:out value="${channelName}" /> 약관에 동의하시면 통합 아이디로 모든 서비스를 이용하실 수 있습니다.</p>
      </div>
      <div class="sec_join">
        <div class="user_info">
          <c:if test="${not empty id}">
          <h3><em class="tit tit_w33">아이디</em><c:out value="${id}" /></h3>
          </c:if>
          <dl class="dt_w33">
            <dt>이름</dt>
            <dd><c:out value="${name}" /></dd>
          </dl>
          <dl class="dt_w33">
            <dt>휴대폰 번호</dt>
            <dd><c:out value="${mobile}" /></dd>
          </dl>
          <dl class="dt_w33">
            <dt>가입일</dt>
            <dd><c:out value="${joindate}" /></dd>
          </dl>
        </div>
        <form method="post" action="<c:out value="${ctx}" />/join/finish/apply">
          <input type="hidden" name="incsno" value="<c:out value="${incsno}" />" />
          <input type="hidden" name="xincsno" value="<c:out value="${xincsno}" />" />
          <input type="hidden" name="chcd" value="<c:out value="${chcd}" />" />
          
          <div class="all_agree_box m0 is_open">
            <div class="all_chk">
              <span class="checkboxA">
                <input type="checkbox" id="all_chk"  title="모든 약관 및 정보 수신 동의"/>
                <label for="all_chk"><span class="checkbox_label">모든 약관 및 정보 수신 동의</span></label>
              </span>
              <button type="button" class="btn_all_view"><span class="blind">약관 닫기</span></button>
            </div>
            <div class="agree_list">
            <c:if test="${not empty corptermslist}">
              <strong class="txt_t">뷰티포인트 통합회원 약관</strong>
              <ul>
              <c:forEach items="${corptermslist}" var="corpterm" varStatus="corpstatus">
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_<c:out value="${corpstatus.count}" />" name="bpterms_check" <c:if test="${corpterm.tncAgrMandYn eq 'Y'}">class="required"</c:if>title="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />'"/>
                    <label for="i_agree_<c:out value="${corpstatus.count}" />" ap-click-area="약관동의" ap-click-name="약관동의 - (<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 체크 박스" ap-click-data="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />'"><span class="checkbox_label">[<c:out value="${v.tncAgrMandYnTxt}" />] <c:out value="${corpterm.tncTtl}" /></span></label>
                  </span>
                  <a href="javascript:;" ap-click-name="약관동의 - (<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 상세 보기 버튼" ap-click-data="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 상세 보기" class="btn_link_bp" data-type="P" data-lnk="<c:out escapeXml="false" value="${corpterm.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${corpterm.chCd}"/>" data-lnkcd="<c:out escapeXml="false" value="${corpterm.tcatCd}"/>" data-lnkno="<c:out escapeXml="false" value="${corpterm.tncvNo}" />"><span class="blind">자세히보기</span></a>
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
	                    <input type="checkbox" id="i_agree_<c:out value='${status.count}' />" <c:if test="${term.tncAgrMandYn eq 'Y'}">class="required"</c:if> name="terms" data-cd='<c:out escapeXml="false" value="${term.tcatCd}" />' data-no='<c:out escapeXml="false" value="${term.tncvNo}" />' title="[<c:out value="${term.tncAgrMandYnTxt}" />] <c:out value="${term.tncTtl}" /> 체크 박스"/>
	                    <label for="i_agree_<c:out value='${status.count}' />" ap-click-area="약관동의" ap-click-name="약관동의 - [<c:out value="${term.tncAgrMandYnTxt}" />] '<c:out value="${term.tncTtl}" />' 체크 박스" ap-click-data="[<c:out value="${term.tncAgrMandYnTxt}" />] <c:out value="${term.tncTtl}" /> 체크 박스"><span class="checkbox_label">[<c:out value="${term.tncAgrMandYnTxt}" />] <c:out value="${term.tncTtl}" /></span></label>
	                  </span>
	                  <a href="javascript:;" class="btn_link" data-lnk="<c:out escapeXml="false" value="${term.tncTxtUrl}"/>" data-chcd='<c:out escapeXml="false" value="${term.chCd}" />' data-lnkcd='<c:out escapeXml="false" value="${term.tcatCd}" />' data-lnkno='<c:out escapeXml="false" value="${term.tncvNo}" />'><span class="blind">자세히보기</span></a>
	                </li>
	                <input type='hidden' name='tcatCds' value='<c:out escapeXml="false" value="${term.tcatCd}" />'/>
	                <input type='hidden' name='tncvNos' value='<c:out escapeXml="false" value="${term.tncvNo}" />'/>
	        	</c:forEach>
              </ul>
              </c:if>          
              <strong class="txt_t"><c:out value="${channelName}" /> 광고성 정보 수신 동의</strong>
              <ul>
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_bp_top_channel" name="terms_check" title="(선택) '개인정보 수집 및 이용동의 (마케팅)'"/>
                    <label for="i_agree_bp_top_channel" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - (선택) '개인정보 수집 및 이용동의 (마케팅)' 체크 박스" ap-click-data="(선택) '개인정보 수집 및 이용동의 (마케팅)'"><span class="checkbox_label">[선택] 개인정보 수집 및 이용동의 (마케팅)(<c:out value="${channelName}" />)</span><em class="sm">*개인정보 수집 및 이용(마케팅)에 동의 하셔야 문자 수신 동의가 가능합니다.</em></label>
                  </span>
                  <a href="javascript:;" class="btn_link" data-lnk="<c:out escapeXml="false" value="${terms_marketing.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${chCd}" />" data-lnkcd="<c:out escapeXml="false" value="${terms_marketing.tcatCd}" />" data-lnkno="<c:out escapeXml="false" value="${terms_marketing.tncvNo}" />" ap-click-area="정보입력 및 약관동의" ap-click-name="정보입력 및 약관동의 - <c:out value="${channelName}" /> (<c:out value="${terms_marketing.tncAgrMandYnTxt}" />) '<c:out value="${terms_marketing.tncTtl}" />' 상세 보기 버튼" ap-click-data="(<c:out value="${terms_marketing.tncAgrMandYnTxt}" />) '<c:out value="${terms_marketing.tncTtl}" />' 상세 보기 버튼"><span class="blind">자세히보기</span></a>
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
            </div>
          </div>
          
          <div class="btn_submit ver2">
            <button type="button" class="btnA btn_white">다음에 하기</button>
            <button type="submit" class="btnA btn_blue" disabled="disabled">동의하고 서비스 이용</button>
          </div>
          <p class="txt_c" id='not-set-terms'>가입 필수 정보 및 약관을 모두 확인해주세요.</p>
          <p class="txt_c" id='set-terms'>만 14세 이상이며, 가입약관에 동의합니다.</p>
        </form>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
  <form id='offForm' method='post' action=''>
  	<input type='hidden' name='incsNo' value='<c:out escapeXml="false" value="${incsNo}" />'/>
    <input type='hidden' name='chnCd' value='<c:out escapeXml="false" value="${chnCd}" />'/>
    <input type='hidden' name='storeCd' value='<c:out escapeXml="false" value="${storeCd}" />'/>
    <input type='hidden' name='storenm' value='<c:out escapeXml="false" value="${storenm}" />'/>
    <input type='hidden' name='user_id' value='<c:out escapeXml="false" value="${user_id}" />'/>
  </form>
</body>
<common:backblock block="true"/>
</html>