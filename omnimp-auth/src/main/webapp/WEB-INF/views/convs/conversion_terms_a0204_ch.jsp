<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="otl" uri="/WEB-INF/tlds/oneap-taglibs.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- conversion terms 204 ch -->
<html lang="ko">
<head>
  <title>뷰티포인트 통합회원 전환 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	  
	  $("input[type=radio][name=useridList]").change(function() {
		  disabledOnOff();
	  });
	  
	  $("#all_chk").change(function() {
		disabledOnOff($(this).prop("checked"));
		marketing_disabledOnOff_channel($(this).prop("checked"));
	  });
	
	  $("input[type=checkbox]:not(#all_chk)").change(function() {
		disabledOnOff();
	  });
	  
		$('#do-convs').on('click', function() { // A0205 통합회원전환완료
		  
			$(this).attr('disabled', 'disabled');
	  
			if ($("input[type=radio][name=useridList]").length > 0) {
				$('#id').val(OMNI.auth.encode(OMNIEnv.pprs, $("input[type=radio][name=useridList]:checked").val()));
			} else {
				$('#id').val(OMNI.auth.encode(OMNIEnv.pprs, $("#loginid").val()));
			}
	  
			$('#previd').val($('#chloginid').val());
			$('#convsType').val(OMNI.auth.encode(OMNIEnv.pprs, '204'));
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
		  	.attr('action', OMNIEnv.ctx + '/convs/terms-finish-204-ch')
		  	.submit();
		  
		});
		
		$('#phone-cert').on('click', function() {
			location.href = OMNIEnv.ctx + '/cert/convs-ipin-phone';	
		});
		$("#i_agree_bp_top_channel").on('click', function() {
			marketing_disabledOnOff_channel($(this).prop("checked"));
		});
	});
  
  function disabledOnOff(allChecked) {
		var requiredCheckboxLength = $("input[type=checkbox].required").length;
		var checkedRequiredCheckboxLength = $("input[type=checkbox].required:checked").length;
		
		var condition = requiredCheckboxLength == checkedRequiredCheckboxLength;
		
		if (allChecked != undefined) {
			condition = allChecked;
		}
		
		<c:if test="${certType eq 'ipin'}">
		condition &= $("#phone").val() !== '';
		</c:if>
		
		<c:if test="${fn:length(bpuserlist) > 1}">
		condition &= $("input[type=radio][name=useridList]:checked").length > 0;
		</c:if>
		
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
			// location.href = '<c:out escapeXml="false" value="${home}"/>';
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
  	<common:header title="뷰티포인트 통합회원 전환" gaArea="통합회원 전환 02 (통합,경로)" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>회원님은 이미 뷰티포인트 통합 아이디를 보유하고 계십니다.</h2>
      </div>
      <div class="sec_join">
        <p class="tit_id">뷰티포인트 통합 아이디</p>
        <c:if test="${not empty bpuserlist}">
        	<c:forEach items="${bpuserlist}" var="bp" varStatus="index">
        <div class="user_info${fn:length(bpuserlist) > 1 ? ' bg_white' : ''}">
          <h3<c:if test="${fn:length(bpuserlist) == 1}"> class="fc_blue"</c:if>>
          		<c:choose>
          			<c:when test="${fn:length(bpuserlist) > 1}">
            <span class="radioA">
              <input type="radio" id="i_userid_${index.index}" <c:if test="${index.index == 0}"> checked</c:if> name="useridList" value="<c:out value='${bp.umUserName}' />"  title="아이디"/>
              <label for="i_userid_${index.index}" ap-click-area="통합회원 전환 02 (통합,경로)" ap-click-name="통합회원 전환 02 (통합,경로) - 통합 아이디 목록 (<c:out value='${index.index}' />)" ap-click-data="아이디"><em class="tit tit_w40">아이디</em> <c:out value='${bp.umUserName}' /></label>
            </span>
          			</c:when>
          			<c:otherwise>
			<em class="tit tit_w40">아이디</em> <c:out value='${bp.umUserName}' />
			<input type='hidden' name='loginid' id='loginid' value='<c:out value="${bp.umUserName}"/>'/>
          			</c:otherwise>
          		</c:choose>
            
          </h3>
          <dl class="dt_w40">
            <dt>이름</dt>
            <dd><c:out value='${bp.fullName}' /></dd>
          </dl>
        </div>
        	</c:forEach>
        </c:if>
        <c:if test="${not empty chuserlist}">
        	<c:forEach items="${chuserlist}" var="ch" varStatus="index">
        <p class="tit_id"><c:out value="${channelName}"/> 아이디</p>
        <div class="user_info">
          <h3><em class="tit tit_w40">아이디</em> <c:out value="${ch.umUserName}" /></h3>
          <input type='hidden' id='chloginid' value='<c:out value="${ch.umUserName}" />'/>
          <dl class="dt_w40">
            <dt>이름</dt>
            <dd> <c:out value="${ch.fullName}" /></dd>
          </dl>
        </div>
        	</c:forEach>
        </c:if> 
        <p class="txt_l fc_blue">
        	<c:choose>
        		<c:when test="${fn:length(bpuserlist) > 1}">
        	* 선택한 뷰티포인트 통합 아이디로 회원정보가 통합되고, 앞으로 뷰티포인트 통합 아이디와 비밀번호로만 로그인 가능합니다.
        		</c:when>
        		<c:otherwise>
        	* 뷰티포인트 통합 아이디로 회원정보가 통합되고, 앞으로 뷰티포인트 통합 아이디와 비밀번호로만 로그인 가능합니다.
        		</c:otherwise>
        	</c:choose>
        </p>
        <c:if test="${certType eq 'ipin'}">
        <div class="input_form">
         	<span class="inp">
              <input type="tel" id="phone" class="inp_text" value="<c:out value="${otl:phn(sessionScope.cert.phone, locale)}"/>" placeholder="휴대폰 번호" readonly="readonly" title="휴대폰 번호">
              <button type="button" class="btn_phone_change" id='phone-cert'><c:if test="${not empty sessionScope.cert.phone}">변경</c:if><c:if test="${empty sessionScope.cert.phone}">입력</c:if></button>
            </span>
        </div>
        </c:if>        
        <form id='trnsForm' method='post' action=''>
        	<input type='hidden' id='previd' name='previd' value='<c:out escapeXml="false" value="${loginid}"/>'/>
        	<input type='hidden' id='id' name='id'/>
        	<input type='hidden' id='pw' name='pw'/>
        	<input type='hidden' id='cpw' name='cpw'/>
        	<input type='hidden' id='xno' name='xno' value='<c:out escapeXml="false" value="${xincsno}"/>'/>
        	<input type='hidden' id='convsType' name='convsType'/>
          <div class="all_agree_box is_open">
            <div class="all_chk">
              <span class="checkboxA">
                <input type="checkbox" id="all_chk" title="모든 약관 및 정보 수신 동의"/>
                <label for="all_chk" ap-click-area="통합회원 전환 02 (통합,경로)" ap-click-name="통합회원 전환 02 (통합,경로) - 모든 약관 및 정보 수신 동의 체크 박스" ap-click-data="모든 약관 및 정보 수신 동의"><span class="checkbox_label">모든 약관 및 정보 수신 동의</span></label>
              </span>
              <button type="button" class="btn_all_view" ap-click-area="통합회원 전환 02 (통합,경로)" ap-click-name="통합회원 전환 02 (통합,경로) - 약관 항목 접힘/펼침 버튼" ap-click-data="약관 항목 접힘/펼침"><span class="blind">약관 닫기</span></button>
            </div>
            <div class="agree_list">
            <c:if test="${not empty corptermslist}">
              <strong class="txt_t">뷰티포인트 통합회원 약관</strong>
              <ul>
              <c:forEach items="${corptermslist}" var="corpterm" varStatus="corpstatus">
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_bp<c:out value="${corpterm.chCd}" /><c:out value="${corpstatus.count}" />" name="bpterms_check" <c:if test="${corpterm.tncAgrMandYn eq 'Y'}">class="required"</c:if> title="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />'"/>
                    <label for="i_agree_bp<c:out value="${corpterm.chCd}" /><c:out value="${corpstatus.count}" />" ap-click-area="통합회원 전환 02 (통합,경로)" ap-click-name="통합회원 전환 02 (통합,경로) - (<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 체크 박스" ap-click-data="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />'"><span class="checkbox_label">[<c:out value="${corpterm.tncAgrMandYnTxt}" />] <c:out value="${corpterm.tncTtl}" /></span></label>
                  </span>
                  <a href="javascript:;" ap-click-name="통합회원 전환 02 (통합,경로) - (<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 상세 보기 버튼" ap-click-data="(<c:out value="${corpterm.tncAgrMandYnTxt}" />) '<c:out value="${corpterm.tncTtl}" />' 상세 보기" class="btn_link_bp" data-type="P" data-lnk="<c:out escapeXml="false" value="${corpterm.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${corpterm.chCd}"/>" data-lnkcd="<c:out escapeXml="false" value="${corpterm.tcatCd}"/>" data-lnkno="<c:out escapeXml="false" value="${corpterm.tncvNo}" />"><span class="blind">자세히보기</span></a>
                </li>
                <input type='hidden' name="bpTcatCds" value="<c:out escapeXml="false" value="${corpterm.tcatCd}"/>"/>
                <input type='hidden' name='bpTncvNos' value='<c:out escapeXml="false" value="${corpterm.tncvNo}" />'/>
              </c:forEach>                
              </ul>
              </c:if>
              <c:if test="${not empty chterms}">
              <strong class="txt_t"><c:out value="${channelName}"/> 회원 약관</strong>
              <ul>
        	<c:forEach items="${chterms}" var="chterm" varStatus="status">
                <li>
                 <span class="checkboxA">
                    <input type="checkbox" id="i_agree_<c:out value="${chterm.chCd}"/><c:out value="${status.count}"/>" <c:if test="${chterm.tncAgrMandYn eq 'Y'}">class="required"</c:if> name="terms_check" data-cd="<c:out escapeXml="false" value="${chterm.tcatCd}"/>" data-no="<c:out escapeXml="false" value="${chterm.tncvNo}"/>" title="(<c:out value="${chterm.tncAgrMandYnTxt}"/>) '<c:out value="${chterm.tncTtl}"/>'"/>
                    <label for="i_agree_<c:out value="${chterm.chCd}"/><c:out value="${status.count}"/>" ap-click-area="통합회원 전환 02 (통합,경로)" ap-click-name="통합회원 전환 02 (통합,경로) - (<c:out value="${chterm.tncAgrMandYnTxt}"/>) '<c:out value="${chterm.tncTtl}"/>' 체크 박스" ap-click-data="(<c:out value="${chterm.tncAgrMandYnTxt}"/>) '<c:out value="${chterm.tncTtl}"/>'"><span class="checkbox_label">[${chterm.tncAgrMandYnTxt}] <c:out value="${chterm.tncTtl}"/></span></label>
                  </span>
                  <a href="javascript:;" ap-click-area="통합회원 전환 02 (통합,경로)" ap-click-name="통합회원 전환 02 (통합,경로) - (<c:out value="${chterm.tncAgrMandYnTxt}"/>) '<c:out value="${chterm.tncTtl}"/>' 상세보기 버튼" ap-click-data="(<c:out value="${chterm.tncAgrMandYnTxt}"/>) '<c:out value="${chterm.tncTtl}"/>' 상세보기" class="btn_link" data-lnk="<c:out escapeXml="false" value="${chterm.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${chterm.chCd}"/>" data-lnkcd="<c:out escapeXml="false" value="${chterm.tcatCd}"/>" data-lnkno="<c:out escapeXml="false" value="${chterm.tncvNo}"/>"><span class="blind">자세히보기</span></a>
                </li>
                <input type='hidden' name="tcatCds" value='<c:out escapeXml="false" value="${chterm.tcatCd}" />'/>
                <input type='hidden' name="tncvNos" value='<c:out escapeXml="false" value="${chterm.tncvNo}" />'/>
        	</c:forEach>
              </ul>
              <strong class="txt_t"><c:out value="${channelName}"/> 광고성 정보 수신 동의</strong>
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
		                  <common:info-notice-ch gaArea="통합회원 전환 02 (통합,경로)" channelCd="${chcd}" channelName="${channelName}"/>
		                </li>                
				     </ul> 
				</li> 
              </ul>
              </c:if>
            </div>
          </div>
          <div class="btn_submit">
            <button type="button" class="btnA btn_blue" ap-click-area="통합회원 전환 02 (통합,경로)" ap-click-name="통합회원 전환 02 (통합,경로) - 동의하고 통합회원 전환 버튼" ap-click-data="동의하고 통합회원 전환" id='do-convs' disabled>동의하고 통합회원 전환</button>
            <input type='hidden' name='trnsType' value='<c:out escapeXml="false" value="${trnsType}"/>'/>
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