<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><!-- regist -->
<html lang="ko">
<head>
  <title></title>
  <common:meta/>
  <common:css/>
  <tagging:google/>
  <script type="text/javascript">
  </script>
</head>

<body>
	<!-- wrap -->
	<div id="wrap" class="wrap">
		<common:header title="" type="no"/>
		<!-- container -->
		<section class="container">
			<form method="post" id="form-regist" action="<c:out value='${ctx}'/>/join/regist">
			<div class="input_form">
				<span class="inp" id="password-span">
					<input type="password" id="loginpassword" name="safety_password" class="inp_text is_success" maxlength="16" value="12345"/>
					<button type="button" class="btn_del" ><span class="blind">삭제</span></button>
				</span>
				<p id="password-guide-msg" class="form_guide_txt is_success">
					사용 가능한 비밀번호 입니다.
					<span id="password-guide-strength" class="i_security is_safety">안전</span>
				</p>
			</div>
			<div class="input_form">
				<span class="inp" id="password-span">
					<input type="password" id="loginpassword" name="normal_password" class="inp_text is_success" maxlength="16" value="12345"/>
					<button type="button" class="btn_del" ><span class="blind">삭제</span></button>
				</span>
				<p id="password-guide-msg" class="form_guide_txt is_success">
					사용 가능한 비밀번호 입니다.
					<span id="password-guide-strength" class="i_security is_normal">보통</span>
				</p>
			</div>
			<div class="input_form">
				<span class="inp" id="password-span">
					<input type="password" id="loginpassword" name="normal_password" class="inp_text is_success" maxlength="16" value="12345"/>
					<button type="button" class="btn_del" ><span class="blind">삭제</span></button>
				</span>
				<p id="password-guide-msg" class="form_guide_txt is_success">
					사용 가능한 비밀번호 입니다.
					<span id="password-guide-strength" class="i_security is_danger">위험</span>
				</p>
			</div>
			<div class="input_form">
				<span class="inp" id="password-span">
					<input type="password" id="loginpassword" name="normal_password" class="inp_text is_error" maxlength="16" value="12345"/>
					<button type="button" class="btn_del" ><span class="blind">삭제</span></button>
				</span>
				<p id="password-guide-msg" class="form_guide_txt is_error">
					사용 할 수 없는 비밀번호 입니다.
					<span id="password-guide-strength" class="i_security is_impossible">불가</span>
				</p>
			</div>
			</form>
		</section>
	  <!-- //container -->
	</div><!-- //wrap -->
</body>

</html>