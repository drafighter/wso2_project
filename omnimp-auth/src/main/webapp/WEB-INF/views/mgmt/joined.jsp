<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="otl" uri="/WEB-INF/tlds/oneap-taglibs.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html><!-- ME-FO-A0103 joined -->
<html lang="ko">
<head>
  <title>가입된 회원 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="false" authCategory="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
		
	  window.AP_SIGNUP_TYPE = '기가입';
	  dataLayer.push({event: 'signup_complete'});  
	  
	  $('#do-login-online').on('click', function() {
		  
		  $.ajax({url:OMNIEnv.ctx + '/ga/tagging/join/<c:out value="${join_case}" />',type:'get'});

		  goAction();
		  
	  });
	  $('#do-login-offline').on('click', function() {

			  $('#offForm')
			  .attr('action', '<c:out escapeXml="false" value="${homeurl}" />')
			  .submit();

	  });
  });
  
  var goAction = function() {
	  var joinType = $('#joinStepType').val();
	  
	  if (joinType === '20'
  		|| joinType === '25'
  		|| joinType === '30' ) {
	  
	  //if (joinType === '25') { // JOINED_OMNI_CH 25 : A0103(온라인-로그인하기, 오프라인-확인) --> 가입사실(중복체크) - 옴니조회
		  
		var loginid = $("input[type=radio][name=i_userid_list]:checked").val();
	  	if ($("input[type=radio][name=i_userid_list]").length > 0) {
	  		$('#uid').val($("input[type=radio][name=i_userid_list]:checked").val());
	  	} else {
		  	if ($('#loginid').val() != '') {
		  		$('#uid').val($('#loginid').val());
		  	}		  		
	  	}
	  	
		$('#loginForm')
		  .attr('action', OMNIEnv.ctx + '/go-login')
		  .submit();
	  		
	  //} else if (joinType === '30') { // JOINED_OMNI 30 : A0103 --> A0105
	  //	  $('#termsForm').attr('action', OMNIEnv.ctx + '/join/go-terms').submit();
	  } else if (joinType === '35') { // JOINED_OFF 35 : A0103 --> A0207 
		  $('#termsForm').attr('action', OMNIEnv.ctx + '/join/go-idregist').submit();
	  } else {
		  location.href = OMNIEnv.ctx + '/go-login';
	  }
  };
  var closeAction = function() {
	  <c:choose>
		<c:when test="${headertype eq 'cancelbtn'}">
			if("<c:out value='${sessionScope.cancelUri}'/>" != null && "<c:out value='${sessionScope.cancelUri}'/>" != "") {
				location.href = decodeURIComponent(("<c:out value='${sessionScope.cancelUri}'/>").replace(/&amp;/g, "&"));	
			} else {
				location.href = '${url}';	
			}	
		</c:when>
		<c:otherwise>
			location.href = OMNIEnv.ctx + '/go-join';
		</c:otherwise>
	</c:choose>
};  
  </script>
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  <c:choose>
		<c:when test="${headertype eq 'cancelbtn'}">
			<common:header title="가입된 회원" type="closeaction"/>
		</c:when>
		<c:otherwise>
			<common:header title="가입된 회원" type="goaction"/>
		</c:otherwise>
	</c:choose>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
      	<c:choose>
      		<c:when test="${multiflag}">
      	<h2>본인인증 결과 이미 가입된 뷰티포인트 <br />회원정보가 확인 되었습니다.</h2>  
      		</c:when>
      		<c:when test="${joinAditor}">
      	<h2>회원님은 이미 아모레퍼시픽 <br />뷰티포인트 회원이시네요!<br><br />로그인 후 에디터 가입하기를 <br />진행해주세요.</h2>  
      		</c:when>
      		<c:otherwise>
      	<h2>회원님은 이미 아모레퍼시픽 <br>뷰티포인트 회원가입을 하셨습니다.</h2>
      		</c:otherwise>
      	</c:choose>
      </div>
      <c:if test="${not empty users}">
      <c:choose>
      	<c:when test="${multiflag}">
	      <c:forEach var="user" items="${users}" varStatus="index">
      <div class="user_info mb13">      
      	<c:if test="${not empty user.chcsNo}">
      	<h3>
          <span class="radioA">
            <input type="radio" id="i_userid_<c:out value="${index.index}" />" name="i_userid_list" <c:if test="${index.index == 0}">checked</c:if> value="<c:out value="${user.chcsNo}" />" title="로그인 아이디 선택"/>
            <label for="i_userid_<c:out value="${index.index}" />" ap-click-area="가입된 회원" ap-click-name="가입된 회원 - 로그인 아이디 선택 라디오 버튼" ap-click-data="로그인 아이디 선택"><em class="tit tit_w33">아이디</em><c:out value="${user.chcsNo}"/></label>
          </span>      	
      	</h3>
      	</c:if>
        <dl class="dt_w33">
          <dt>이름</dt>
          <c:choose>
          	<c:when test="${offline}">
          <dd><c:out value="${otl:nmm(user.custNm, locale)}"/></dd>
          	</c:when>
          	<c:otherwise>
          <dd><c:out value="${user.custNm}"/></dd>	
          	</c:otherwise>
          </c:choose>
        </dl>
        <dl class="dt_w33">
          <dt>휴대폰 번호</dt>
          <c:choose>
          	<c:when test="${offline}">
          	<dd><c:out value="${otl:mmp(user, locale)}"/></dd>
          	</c:when>
          	<c:otherwise>
          	<dd><c:out value="${otl:mpn(user, locale)}"/></dd>
          	</c:otherwise>
          </c:choose>
          
        </dl>
        <dl class="dt_w33">
          <dt>가입일</dt>
          <dd><c:out value="${otl:bdt(user.mbrJoinDt)}"/></dd>
        </dl>
      </div>          
    	  </c:forEach>      	
      	</c:when>
      	<c:otherwise>
	      <c:forEach var="user1" items="${users}">
      <div class="user_info mb13">
      	<c:if test="${not empty user1.chcsNo}">   
      	<h3><em class="tit tit_w33">아이디</em><c:out value="${user1.chcsNo}"/></h3>
      		<input type='hidden' id='loginid' value='${user1.chcsNo}'/>
      	</c:if>
        <dl class="dt_w33">
          <dt>이름</dt>
          <dd><c:out value="${otl:nmm(user1.custNm, locale)}"/></dd>
        </dl>
        <dl class="dt_w33">
          <dt>휴대폰 번호</dt>
          <dd><c:out value="${otl:mmp(user1, locale)}"/></dd>
        </dl>
        <dl class="dt_w33">
          <dt>가입일</dt>
          <dd><c:out value="${otl:bdt(user1.mbrJoinDt)}"/></dd>
        </dl>
      </div>          
    	  </c:forEach>
      	</c:otherwise>
      </c:choose>
      </c:if>
      <!-- <p class="txt_c">*로그인 후 본인인증 정보로 회원정보가 변경됩니다.</p> -->
      <!-- 로그인화면이동 A0200 -->
      <div class="btn_submit mt40">
       	<c:choose>
      		<c:when test="${!offline && !multiflag}"><button type="button" class="btnA btn_blue" id='do-login-online' ap-click-area="가입된 회원" ap-click-name="가입된 회원 - 로그인 화면 이동 (확인) 버튼" ap-click-data="로그인 처리 (online)">로그인 하기</button></c:when>
      		<c:when test="${!offline && multiflag}"><button type="button" class="btnA btn_blue" id='do-login-online' ap-click-area="가입된 회원" ap-click-name="가입된 회원 - 로그인 화면 이동 (확인) 버튼" ap-click-data="로그인 처리 (online)">로그인 하기</button></c:when>
      		<c:when test="${offline}"><button type="button" class="btnA btn_blue" id='do-login-offline' ap-click-area="가입된 회원" ap-click-name="가입된 회원 - 로그인 화면 이동 (확인) 버튼" ap-click-data="로그인 처리 (offline)">확인</button></c:when>
        </c:choose>
      </div>
      
      <c:if test="${!offline or !joinAditor} ">
        <p class="txt_c">로그인 화면으로 이동합니다.<br/>오프라인 가입 회원은 휴대폰 로그인을 해주세요.</p>
      </c:if>     	
      	<form id='termsForm' method='post' action=''>
      	    <input type='hidden' id='joinStepType' value='<c:out value="${joinStepType}" />'/>
      		<input type='hidden' id='incsNo' name='incsNo' value='<c:out escapeXml="false" value="${xincsno}" />'/>
      	</form>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
    <form id='offForm' method='post'>
    	<input type='hidden' name='incsNo' value='<c:out value="${incsNo}" />'/>
    	<input type='hidden' name='chnCd' value='<c:out value="${chnCd}" />'/>
    	<input type='hidden' name='storeCd' value='<c:out value="${storeCd}" />'/>
    	<input type='hidden' name='storenm' value='<c:out value="${storenm}" />'/>
    	<input type='hidden' name='user_id' value='<c:out value="${user_id}" />'/>
    </form>
    <form id='loginForm' method='post' action=''>
    	<input type='hidden' id='uid' name='uid'/>
    </form>      
</body>

</html>