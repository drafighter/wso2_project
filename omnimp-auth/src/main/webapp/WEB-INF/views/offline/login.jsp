<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- ME-FO-A0214 login new terms apply -->
<html lang="ko">
<head>
<title>매장 로그인 | 옴니통합회원</title>
<common:meta/>
<common:css/>
<common:js auth="true" popup="true" authCategory="true"/>
<script type="text/javascript" src="<c:out value="${ctx}" />/js/basic-offline-login.js"></script>
<tagging:google/>
<script type="text/javascript">
	$(document).ready(function() {

	});

	window.onload = function() {
		if ($('#loginid').val().trim() != '' && $('#loginpassword').val().trim() != '') {
			$('#dologin').removeAttr('disabled');
		}
	}
</script>
</head>

<body>
	<tagging:google noscript="true"/>
	<!-- wrap -->
	<div id="wrap" class="wrap">
	    <!-- container -->
	    <section class="container">
	    	<div class="offline_wrap">
	    		<h2>매장 로그인</h2>
	    		<p class="txt"><c:out value="${chNm}"/> 매장의 관리자 계정으로 로그인 해주세요.</p>
	    	</div>
	    	<div class="sec_login">
 				<form onsubmit="return false">
 					<input type="hidden" id="chCd" name="chCd" value="<c:out value='${param.chCd}'/>">
					<div class="input_form mt70">
						<span class="inp" id="loginid-span"> <input type="text" id="loginid" autocomplete="off" class="inp_text" placeholder="관리자 아이디 입력"/>
							<button type="button" class="btn_del" tabIndex=-1> <span class="blind">삭제</span> </button>
						</span>
					</div>
					<div class="input_form">
						<span class="inp" id="password-span"> 
							<input type="password" id="loginpassword" autocomplete="off" class="inp_text" placeholder="비밀번호 입력" />
							<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
						</span>
					</div>
					<div id='login-noti-panel'><p id="login-noti-msg" class="form_guide_txt" style='display:none;'></p></div>
					<div class="btn_submit mt20">
						<button type="button" id="dologin" class="btnA btn_blue loginbtn" disabled>로그인</button>
					</div>
				</form>	
          	</div>   	
		</section>
	    <!-- //container -->
	</div><!-- //wrap -->
</body>
<common:backblock block="true"/>
</html>