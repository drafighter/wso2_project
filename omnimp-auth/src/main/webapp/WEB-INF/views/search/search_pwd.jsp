<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html><!-- ME-FO-A0210-->
<html lang="ko">
<head>
  <title>비밀번호 찾기 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true" swipe="true"/>
  <tagging:google/>
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="비밀번호 찾기" type="prvaction"/>
  <form name='form_ipin_cert' id='form_ipin_cert' method='post'>
  	<input type='hidden' name='m' value='pubmain'><!-- 필수 데이타로, 누락하시면 안됩니다. -->
  	<input type="hidden" name="enc_data" value="<c:out value="${certdata.ipinData.encData}" />"><!-- 업체정보를 암호화 한 데이타입니다. -->
  	<input type="hidden" name="param_r1" value="<c:out value="${certdata.ipinData.login.chCd}" />"><!-- 인증결과 응답시 해당 값을 그대로 송신 -->
  	<input type="hidden" name="param_r2" value="<c:out value="${certdata.ipinData.cpRequest}" />"><!-- 인증결과 응답시 해당 값을 그대로 송신 -->
  	<input type="hidden" name="param_r3" value=""><!-- 인증결과 응답시 해당 값을 그대로 송신 -->
  </form> 
  <form name='form_kmcis_cert' id='form_kmcis_cert' method='post'>
	<input type='hidden' name='tr_cert' value="<c:out value="${certdata.kmcisData.certData}" />">
    <input type='hidden' name='tr_url' value="<c:out value="${certdata.kmcisData.resultUrl}" />"><!-- 본인인증서비스 결과수신 POPUP URL -->
    <input type='hidden' name='tr_add' value="N"><!-- IFrame사용여부 -->	  
  </form>
  <c:if test="${manualcert}">
  <form name='form_manual_cert' id='form_manual_cert' method='post'>
  	<input type='hidden' id='loginId' name='loginId'/>
  </form>
  </c:if>  
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>가입 시 입력한 정보로 비밀번호를 찾아보세요.</h2>
        <p>아이디를 입력 후 본인인증 방식을 선택해주세요.</p>
      </div>
      <div class="input_form inp_id">
        <span class="inp">
          <input type="text" id="userid" autocomplete="off" class="inp_text" placeholder="아이디 입력" ap-click-area="비밀번호 찾기" ap-click-name="비밀번호 찾기 - 아이디 입력란" ap-click-data="아이디 입력" title="아이디 입력"/>
          <button type="button" class="btn_del"><span class="blind">삭제</span></button>
        </span>
      </div>
      <div class="btn_joins bdb_n">
        <a href="javascript:;" rel="opener" class="is_disabled" id='phone-cert' disabled ap-click-area="비밀번호 찾기" ap-click-name="비밀번호 찾기 - 휴대폰 인증 버튼" ap-click-data="휴대폰 인증" title="새창열림"  tabindex="-1">
          <i class="ico i_phone"></i>휴대폰 인증
        </a>
        <a href="javascript:;" rel="opener" class="is_disabled" id='ipin-cert' disabled ap-click-area="비밀번호 찾기" ap-click-name="비밀번호 찾기 - 아이핀 인증 버튼" ap-click-data="아이핀 인증" title="새창열림" tabindex="-1">
          <i class="ico i_ipin"></i>아이핀 인증
        </a>
      </div>
      <button class="btnA btn_white btn_join_membership mt40" ap-click-area="비밀번호 찾기" ap-click-name="비밀번호 찾기 - 회원가입 버튼" ap-click-data="회원가입">
        <span>아직 회원이 아니세요?</span>
        <em>회원가입</em>
      </button>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
  <input type='hidden' id='p'/>
	<script type="text/javascript">
		$(document).ready(function() {
	  
			window.onpageshow = function(event) {
				if ( event.persisted || (window.performance && window.performance.navigation.type == 2)) {
					// Back Forward Cache로 브라우저가 로딩될 경우 혹은 브라우저 뒤로가기 했을 경우
					$('#userid').val('');
					$(this).removeClass('is_success').addClass('is_error');
					$('#phone-cert').prop('disabled', 'disabled').addClass('is_disabled');
					$('#ipin-cert').prop('disabled', 'disabled').addClass('is_disabled');
		        }	
			}
			
			if($('#userid').val() == null || $('#userid').val() == "") {
				$(this).removeClass('is_success').addClass('is_error');
				$('#phone-cert').prop('disabled', 'disabled').addClass('is_disabled');
				$('#ipin-cert').prop('disabled', 'disabled').addClass('is_disabled');
			}
			
			// X 버튼 이벤트 등록
			$('.inp .btn_del').each(function () {
				$(this).click(function () {
					$(this).removeClass('is_success').addClass('is_error');
					$('#phone-cert').prop('disabled', 'disabled').addClass('is_disabled');
					$('#ipin-cert').prop('disabled', 'disabled').addClass('is_disabled');
					$('#phone-cert').prop('tabindex', '-1');
					$('#ipin-cert').prop('tabindex', '-1');
				});
			});			
			
			$('#userid').on('touchend, keyup', function(e) {
				var name = $(this).val();
				if (name.length > 3) {
					$(this).removeClass('is_error').addClass('is_success');
					$('#phone-cert').removeAttr('disabled').removeClass('is_disabled');
					$('#ipin-cert').removeAttr('disabled').removeClass('is_disabled');
					$('#phone-cert').removeAttr('tabindex');
					$('#ipin-cert').removeAttr('tabindex');
				} else {
					$(this).removeClass('is_success').addClass('is_error');
					$('#phone-cert').prop('disabled', 'disabled').addClass('is_disabled');
					$('#ipin-cert').prop('disabled', 'disabled').addClass('is_disabled');
					$('#phone-cert').prop('tabindex', '-1');
					$('#ipin-cert').prop('tabindex', '-1');
				}
			});
			
			$('#userid').on('paste', $('body'), function(e) {
		    	var data = e.originalEvent.clipboardData.getData('Text');
		    	if (data.length > 3) {
		    		$(this).removeClass('is_error').addClass('is_success');
					$('#phone-cert').removeAttr('disabled').removeClass('is_disabled');
					$('#ipin-cert').removeAttr('disabled').removeClass('is_disabled');
		    	} else {
		    		$(this).removeClass('is_success').addClass('is_error');
					$('#phone-cert').prop('disabled', 'disabled').addClass('is_disabled');
					$('#ipin-cert').prop('disabled', 'disabled').addClass('is_disabled');
		    	}
		    });
  
			$('#ipin-cert').on('click', function() {
				if ($(this).hasClass('is_disabled')) { return false; }
				var params = {loginId:$('#userid').val()};
				$.ajax({
					url:OMNIEnv.ctx + '/search/id-check',
					type:'post',
					data:JSON.stringify(params),
					dataType:'json',
					contentType : 'application/json; charset=utf-8',
					success: function(data) {
						if (data.status === 1) {
							$('#p').val(data.resultCode);
					<c:choose>
					<c:when test="${not empty certdata.ipinData.encData}">
							var UserAgent = navigator.userAgent;
							var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
					<c:if test="${mobile}">
							isMobile = true;
					</c:if>
							if (isMobile) {
								document.form_ipin_cert.target = '';
							} else {
								var h = 550;
								var w = 450;
								var xPos = (document.body.offsetWidth/2) - (w/2); // 가운데 정렬
								xPos += window.screenLeft; // 듀얼 모니터일 때
								var yPos = (document.body.offsetHeight/2) - (h/2);
								window.name ="Parent_ipin_window";
								var objpop = window.open('', 'popupIPIN2', 'width=' + w + ', height=' + h + ', top=' + yPos + ', left=' + xPos + ', fullscreen=no, menubar=no, status=no, toolbar=no, titlebar=yes, location=no, scrollbar=no, rel=opener');
								if(objpop) {
									document.form_ipin_cert.target = "popupIPIN2";
								}
							}
							document.form_ipin_cert.action = "<c:out value="${certdata.ipinData.certUrl}"/>";
							document.form_ipin_cert.submit();		
					</c:when>
					<c:otherwise>
							OMNI.popup.open({id:'ipin-warning', content:'아이핀인증을 사용할 수 없습니다.',closelabel:'확인',closeclass:'btn_blue'});
					</c:otherwise>
					</c:choose>							
						} else {
							OMNI.popup.open({id:'not-exist-user', content:'가입된 회원 아이디가 아닙니다.',closelabel:'확인',closeclass:'btn_blue'});
						}
						$('.layer_wrap').focus();
					},
					error: function() {
						OMNI.popup.error();
						$('.layer_wrap').focus();
					}
				});
				
			});
	
			$('#phone-cert').on('click', function() {
				if ($(this).hasClass('is_disabled')) { return false; }
				var params = {loginId:$('#userid').val()};
				$.ajax({
					url:OMNIEnv.ctx + '/search/id-check',
					type:'post',
					data:JSON.stringify(params),
					dataType:'json',
					contentType : 'application/json; charset=utf-8',
					success: function(data) {
						if (data.status === 1) {
							$('#p').val(data.resultCode);
		<c:choose>
		<c:when test="${not empty certdata.kmcisData.certData}">
							var UserAgent = navigator.userAgent;
							var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		<c:if test="${mobile}">
							isMobile = true;
		</c:if>
							if (isMobile) {
								document.form_kmcis_cert.target = '';
							} else {
								var h = 550;
								var w = 450;
								var xPos = (document.body.offsetWidth/2) - (w/2); // 가운데 정렬
								xPos += window.screenLeft; // 듀얼 모니터일 때
								var yPos = (document.body.offsetHeight/2) - (h/2);
								window.name ="Parent_kmcis_window";
								var objpop = window.open('', 'popupKMCIS2', 'width=' + w + ', height=' + h + ', top=' + yPos + ', left=' + xPos + ', fullscreen=no, menubar=no, status=no, toolbar=no, titlebar=yes, location=no, scrollbar=no, rel=opener');
								if(objpop) {
									document.form_kmcis_cert.target = "popupKMCIS2";
								}
							}
							document.form_kmcis_cert.action = "<c:out value="${certdata.kmcisData.certUrl}"/>";
							document.form_kmcis_cert.submit();
		</c:when>
		<c:otherwise>
							OMNI.popup.open({id:'kmcis-warning', content:'휴대폰 인증을 사용할 수 없습니다.',closelabel:'확인',closeclass:'btn_blue'});
		</c:otherwise>
		</c:choose>		
						} else {
							OMNI.popup.open({id:'not-exist-user', content:'가입된 회원 아이디가 아닙니다.',closelabel:'확인',closeclass:'btn_blue'});
						}
						$('.layer_wrap').focus();
					},
					error: function() {
						OMNI.popup.error();
						$('.layer_wrap').focus();
					}
				});			
			});
	
			$(".btn_join_membership").on('click', function() {
				location.href = OMNIEnv.ctx + '/go-join-param';
			});
	
	<c:if test="${manualcert}">
  	<c:choose>
	<c:when test="${mobile}">
			$(document).on('swiped-up', function(e) {		
	</c:when>
	<c:otherwise>
			$(document).key('ctrl+shift+z', function(e) {		
	</c:otherwise>
	</c:choose>
				if ($('#phone-cert').hasClass('is_disabled')) { return; }
				$('#loginId').val($('#userid').val());
				var UserAgent = navigator.userAgent;
				var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
		<c:if test="${mobile}">
				isMobile = true;
		</c:if>
				if (isMobile) {
					document.form_manual_cert.target = '';
				} else {
					var h = 780;
					var w = 480;
					var xPos = (document.body.offsetWidth/2) - (w/2); // 가운데 정렬
					xPos += window.screenLeft; // 듀얼 모니터일 때
					var yPos = (document.body.offsetHeight/2) - (h/2);
					window.name ="Parent_manual_window";
					var objpop = window.open('', 'popupManual', 'width=' + w + ', height=' + h + ', top=' + yPos + ', left=' + xPos + ', fullscreen=no, menubar=no, status=no, toolbar=no, titlebar=yes, location=no, scrollbar=no, resizable=no, rel=opener');
					if(objpop) {
						document.form_manual_cert.target = "popupManual";
					}
				}
				document.form_manual_cert.action = OMNIEnv.ctx + "/cert/manual-cert";
				document.form_manual_cert.submit();
			});
	</c:if>	
	
		});
		var prevAction = function() {
			location.href = OMNIEnv.ctx + '/go-login';
		};
	</script>  
</body>

</html>