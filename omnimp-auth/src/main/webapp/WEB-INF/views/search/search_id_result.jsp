<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri = "http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="otl" uri="/WEB-INF/tlds/oneap-taglibs.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html><!-- ME-FO-A0209 -->
<html lang="ko">
<head>
  <title>아이디 찾기 결과 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
	$(document).ready(function() {

		<c:if test="${userSize == 0}">
		OMNI.popup.open({
			id:'search-id-no-result',
			content: '가입한 회원이 아니거나,<br/>입력하신 정보가 일치하지 않습니다.',
			oklabel:'확인',
			ok:function() {
				OMNI.popup.close({ id: 'search-id-no-result' });
				location.href = OMNIEnv.ctx + '/search/id';
			},
			closelabel:''
		});			
		$('.layer_wrap').focus();
		</c:if>
		
		var num = $('input[name="loginids"]').length;
		if(num > 1) {
			$('input:radio[name=loginids]')[0].checked = true;
		}
		
		$('#do-login').on('click', function() {
			
			goAction();
			
		});
		
		$('#send-userid').on('click', function() {
			
			$(this).attr('disabled', 'disabled');
			
			var selectedId = '';
			var idsize = $("input[name=loginids]").length;
			if (idsize === 1) {
				selectedId = $("input[name=loginids]").eq(0).val();
			} else {
				selectedId = $('input[name="loginids"]:checked').val();
			}
			if (selectedId === '' || typeof selectedId == 'undefined') {
				OMNI.popup.open({
					id:'phone-send-waring',
					content: '전송할 아이디가 선택되지 않았습니다.',
					closelabel:'확인',
					closeclass:'btn_blue'
				});
				$('.layer_wrap').focus();
				$('#send-userid').removeAttr('disabled');
				return;
			}
			
			var params = {
				userName: selectedId, // 선택된 아이디	
				userPhone:$('#mobileno').val() // 암호화된 휴대폰 번호
			}; 
			// 아이디 전송 요청 SMS 발송
			$.ajax({
				url:OMNIEnv.ctx + '/cert/sendsms/id',
				type:'post',
				data:JSON.stringify(params),
				dataType:'json',
				contentType : 'application/json; charset=utf-8',
				success: function(data) {
					$('#smsSeq').val(data.smsAthtSendNo);
					if (data.status === 1) {
						// SMS 발송 성공
						OMNI.popup.open({
							id:'phone-send-id',
							content: '인증하신 휴대폰 번호로<br/>아이디를 전송 했습니다.',
							oklabel:'로그인 하기',
							ok:function() {
								OMNI.popup.close({ id: 'phone-send-id' });
								//OMNI.auth.setCookie({cookieName:'one-ap-loginid', cookieValue: params.userName});
								OMNI.auth.setCookie({cookieName:'one-ap-loginid', cookieValue: ''});
								location.href = OMNIEnv.ctx + '/entry?<c:out escapeXml="false" value="${ssoparam}" />';
							},
							closelabel:'닫기'
						});
					} else { // 발송 실패
						OMNI.popup.open({
							id:'phone-send-waring',
							content: '아이디 전송에 실패하였습니다.<br/>잠시 후 다시 시도해주세요.',
							closelabel:'확인',
							closeclass:'btn_blue'
						});
					}	
					$('.layer_wrap').focus();
				},
				error: function() {
					OMNI.popup.error();
					$('.layer_wrap').focus();
				}
		
			});		  
		});
	  
	});
	
	var goAction = function() {
		
		var selectedId = '';
		var idsize = $("input[name=loginids]").length;
		if (idsize === 1) {
			selectedId = $("input[name=loginids]").eq(0).val();
		} else {
			selectedId = $('input[name="loginids"]:checked').val();
		}
		
		if (selectedId === '' || typeof selectedId == 'undefined') {
		} else {
			$('#uid').val('');
			$('#loginFrm').attr('action', OMNIEnv.ctx + '/go-login').submit();
		}
		
	};
	
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="아이디 찾기 결과" type="goaction" />
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>고객님의 아이디 정보입니다.</h2>
        <p>개인정보 보호를 위해 일부 별표(*) 표기 됩니다.</p>
      </div>
      <div class="sec_join">
      <c:if test="${fn:length(response.searchOmniUsers) == 1}">
      <c:forEach var="omniUser" items="${response.searchOmniUsers}" varStatus="omnistatus">
      	<p class="tit_id">뷰티포인트 통합 아이디</p>
        <div class="user_info">
        	<c:if test="${userSize == 1}">
        		<input type='hidden' id="i_userid_1" name='loginids' value='<c:out value="${omniUser.xpassId}" />'/>
        		<strong class="st_txt">아이디: <c:out value="${omniUser.loginId}"/></strong>
        	</c:if>
        	<c:if test="${userSize > 1}">
        		<span class="radioA">
	        		<input type='radio' id="i_userid_<c:out value='${omnistatus.index}' />" name='loginids' value='<c:out value="${omniUser.xpassId}" />'/>
	          		<label for="i_userid_<c:out value='${omnistatus.index}' />" ap-click-area="아이디 찾기 결과" ap-click-name="아이디 찾기 결과 - 통합 아이디 목록 (<c:out value='${omnistatus.index}' />)" ap-click-data="통합 아이디">
	          		<strong class="st_txt">아이디: <c:out value="${omniUser.loginId}"/></strong>
	          		</label>
	        	</span>
        	</c:if>
        </div>
      </c:forEach>
      </c:if>
      
      <c:if test="${fn:length(response.searchOmniUsers) > 1}">
      <c:forEach var="omniUsers" items="${response.searchOmniUsers}" varStatus="omnistatusmulti">
      		<p class="tit_id">뷰티포인트 통합 아이디</p>
        <div class="user_info bg_white">
         <span class="radioA">
            <input type="radio" id="i_userid_<c:out value="${omnistatusmulti.index}" />" name="loginids" value='<c:out value="${omniUsers.xpassId}" />' title="통합 아이디">
            <label for="i_userid_<c:out value="${omnistatusmulti.index}" />" ap-click-area="아이디 찾기 결과" ap-click-name="아이디 찾기 결과 - 통합 아이디 목록 (<c:out value="${omnistatusmulti.index}" />)" ap-click-data="통합 아이디">
            <strong class="st_txt">아이디: <c:out value="${omniUsers.loginId}"/></strong>
            </label>
          </span>
        </div>
      </c:forEach>
      </c:if>
      
      <c:if test="${fn:length(response.searchChannelUsers) == 1}">
      <c:forEach var="chUser" items="${response.searchChannelUsers}" varStatus="chstatus">
      	<p class="tit_id"><c:out value="${chUser.chCdName}"/> 아이디</p>
        <div class="user_info">
	    	<c:if test="${userSize == 1}">
	    		<input type='hidden' id="i_chid_1" name='loginids' value='<c:out value="${chUser.xpassId}" />'/>
	    		<strong class="st_txt">아이디: <c:out value="${chUser.loginId}"/></strong>
	      	</c:if>
	      	<c:if test="${userSize > 1}">
	       		<span class="radioA">
	          		<input type='radio' id="i_chid_<c:out value="${chstatus.index}" />" name='loginids' value='<c:out value="${chUser.xpassId}" />'/>
	          		<label for="i_chid_<c:out value="${chstatus.index}" />" ap-click-area="아이디 찾기 결과" ap-click-name="아이디 찾기 결과 - 통합 아이디 목록 (<c:out value="${chstatus.index}" />)" ap-click-data="통합 아이디">
	          		<strong class="st_txt">아이디: <c:out value="${chUser.loginId}"/></strong>
	          		</label>
	          	</span>
	      	</c:if>
        </div>
      </c:forEach>
      </c:if>
      
      <c:if test="${fn:length(response.searchChannelUsers) > 1}">
      <c:forEach var="chUsers" items="${response.searchChannelUsers}" varStatus="chstatusmulti">
      	<p class="tit_id"><c:out value="${chUsers.chCdName}"/> 아이디</p>
        <div class="user_info bg_white">
         <span class="radioA">
            <input type="radio" id="i_chid_<c:out value="${chstatusmulti.index}" />" name="loginids" value='<c:out value="${chUsers.passId}" />'  title="통합 아이디">
            <label for="i_chid_<c:out value="${chstatusmulti.index}" />" ap-click-area="아이디 찾기 결과" ap-click-name="아이디 찾기 결과 - 통합 아이디 목록 (<c:out value="${chstatusmulti.index}" />)" ap-click-data="통합 아이디">
            <strong class="st_txt">아이디: <c:out value="${chUsers.loginId}"/></strong>
            </label>
          </span>
        </div>
      </c:forEach>
      </c:if>
      
        <div class="send_id_area">
          <button type="button" class="btnA btn_white" id='send-userid' ap-click-area="아이디 찾기 결과" ap-click-name="아이디 찾기 결과 - 아이디 전송 요청 버튼" ap-click-data="아이디 전송 요청">아이디 전송 요청</button>
          <p>가입 시 등록한 휴대폰 번호로 아이디를 발송해 드립니다.</p>
          <input type='hidden' id='mobileno' value='<c:out value="${response.mobile}" />'/>
          <input type='hidden' id='smsSeq'>
        </div>
        <div class="btn_submit">
          <button type="button" class="btnA btn_blue" id='do-login' ap-click-area="아이디 찾기 결과" ap-click-name="아이디 찾기 결과 - 로그인 하기 버튼" ap-click-data="로그인 하기">로그인 하기</button>
        </div>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
  <form id='loginFrm' method='post'>
  	<input type='hidden' name='uid' id='uid'/>
  </form>
</body>

</html>