<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- ME-FO-A0214 new terms ch apply -->
<html lang="ko">
<head>
  <title>약관동의 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true" authCategory="true"/>
  <tagging:google/>
  <script type="text/javascript">
	$(document).ready(function() {
		
		disabledOnOff();
		
		$('#not-set-terms').show();
		$('#set-terms').hide();
 		
		$("#terms-agree-next").on('click' ,function() {
			closeAction();
		});

		$("#all_chk").change(function() {
			disabledOnOff($(this).prop("checked"));
		});

		$("input[type=checkbox]:not(#all_chk)").change(function() {
			disabledOnOff();
		});
		
		// 활성화 된 버튼 선택 시 채널 약관동의 처리 후 약관동의 완료 화면 ME-FO-A0106으로 이동
		$('#term-agree-ok').on('click', function() {
			
			$(this).attr('disabled', 'disabled');
			
			$('#termsForm').find(':checkbox:not(:checked)').attr('value', 'off'); //.prop('checked', true);
			$('#termCount').val($("input[name^='terms']").length);
			
			var checkedbuff = '';
			$('#termsForm').find(':checkbox').each(function(idx, val) {
				
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
			$('#termsForm').append(checkedbuff);
			
			OMNI.loading.show('processing');
			
			$('#termsForm')
			.attr('action', OMNIEnv.ctx + '/join/finish/applycorp_ch')
			.submit();
		});
	});
	
	var disabledOnOff = function(allChecked) {
		var requiredCheckboxLength = $("input[type=checkbox].required").length;
		var checkedRequiredCheckboxLength = $("input[type=checkbox].required:checked").length;
		
		var condition = requiredCheckboxLength == checkedRequiredCheckboxLength;
		
		if (allChecked != undefined) {
			condition = allChecked;
		}
		
		if (condition) {
			$('#not-set-terms').hide();
			$('#set-terms').show();
			$('#term-agree-ok').removeAttr("disabled");
		}
		else {
			$('#not-set-terms').show();
			$('#set-terms').hide();
			$('#term-agree-ok').attr("disabled", "disabled");
		}
	};
	  var closeAction = function() {
			
			OMNI.popup.open({
				id:'next-warn',
				content: '약관동의를 멈추고, 서비스<br/>화면으로 돌아가시겠습니까?<br/>(현재 아이디는 로그아웃됩니다.)',
				gaArea:'약관동의',
				gaOkName:'확인 버튼 (약관 동의 중단 팝업)',
				gaCancelName:'취소 버튼 (약관 동의 중단 팝업)',
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
					OMNI.popup.close({id:'next-warn'});
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
  </script>
  <script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-terms.js?ver=<c:out value="${rv}"/>"></script>
</head>
<body>
	<div id='agree-contents' style='display:none'></div>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="약관동의" type="closeaction"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>아모레퍼시픽 뷰티포인트 통합회원 안내</h2>
        <p>새로운 뷰티포인트 통합회원 약관에 동의하시면 <br class="w320">통합 아이디로 모든 서비스를 이용하실 수 있습니다.</p>
      </div>
      <div class="">
        <div class="user_info mb13">
          <c:if test="${not empty id}">
          <h3><em class="tit tit_w33">아이디</em> <c:out value="${id}" /></h3>
          </c:if>
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
        
        <form id='termsForm' method="post" action="">
          <input type='hidden' name='uid' value='<c:out value="${xid}" />'/>	
          <input type='hidden' name='unm' value='<c:out value="${xname}" />'/>
          <input type="hidden" name="incsno" value="<c:out value="${incsno}" />" />
          <input type="hidden" name="xincsno" value="<c:out value="${xincsno}" />" />
          <input type="hidden" name="chcd" value="<c:out value="${chcd}" />" />
          <input type='hidden' name='termCount' id='termCount'/>
          <input type='hidden' name='mlogin' value='<c:out escapeXml="false" value="${mlogin}" />'/>
          <input type='hidden' name='corpterms' value='<c:out value="${corpterms}" />'/>
          <c:if test="${not empty terms}">
 			<div class="all_agree_box m0 is_open">
            <div class="agree_list bdt_n">
              <strong class="txt_t">뷰티포인트 통합회원 약관</strong>
              <ul>
              
             	<c:forEach items="${terms}" var="term" varStatus="status">
                <li>
                  <span class="checkboxA">
                    <input type="checkbox" id="i_agree_<c:out value="${term.chCd}" /><c:out value="${status.count}" />" <c:if test="${term.tncAgrMandYn eq 'Y'}">class="required"</c:if> name="terms_check" data-cd="<c:out escapeXml="false" value="${term.tcatCd}" />" data-no="<c:out escapeXml="false" value="${term.tncvNo}" />" title="(<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />'"/>
                    <label for="i_agree_<c:out value="${term.chCd}" /><c:out value="${status.count}" />" ap-click-area="약관동의" ap-click-name="약관동의 - (<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />' 체크 박스" ap-click-data="(<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />'"><span class="checkbox_label">[<c:out value="${term.tncAgrMandYnTxt}" />] <c:out value="${term.tncTtl}" /></span></label>
                  </span>
                  <a href="javascript:;" ap-click-area="약관동의" ap-click-name="약관동의 - (<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />' 상세보기 버튼" ap-click-data="(<c:out value="${term.tncAgrMandYnTxt}" />) '<c:out value="${term.tncTtl}" />' 상세보기" class="btn_link" data-lnk="<c:out escapeXml="false" value="${term.tncTxtUrl}"/>" data-chcd="<c:out escapeXml="false" value="${term.chCd}"/>" data-lnkcd="<c:out escapeXml="false" value="${term.tcatCd}"/>" data-lnkno="<c:out escapeXml="false" value="${term.tncvNo}"/>"><span class="blind">자세히보기</span></a>
                </li>
                <input type='hidden' name="tcatCds" value="<c:out escapeXml="false" value="${term.tcatCd}"/>"/>
                <input type='hidden' name="tncvNos" value="<c:out escapeXml="false" value="${term.tncvNo}"/>"/>
                </c:forEach>
                
				<li>
                  <common:info-notice-ch gaArea="약관동의" channelCd="${chcd}" channelName="${channelName}"/>
                </li>                
              </ul>
            </div>
          </div>
          </c:if>
          <div class="btn_submit ver2">
            <button type="button" class="btnA btn_white" id='terms-agree-next' ap-click-area="약관동의" ap-click-name="약관동의 - 다음에 하기 버튼" ap-click-data="다음에 하기">다음에 하기</button>
            <button type="button" class="btnA btn_blue" disabled="disabled" id='term-agree-ok' ap-click-area="약관동의" ap-click-name="약관동의 - 동의하고 서비스 이용 버튼" ap-click-data="동의하고 서비스 이용">동의하고 서비스 이용</button>
          </div>
        </form>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>