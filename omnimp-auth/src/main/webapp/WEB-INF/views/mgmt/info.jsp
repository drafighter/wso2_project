<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>회원정보 관리 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	  $('#password-guide-msg').removeClass('is_error').hide();
	  //$('#password').focus();
		$('#password').on('touchend, keyup', function(e) {
			if ($(this).val() !== '' && ($(this).val().length >= 8 && $(this).val().length <= 16)) {
				$('#pwd-check').attr('disabled', false);
				if (!$('#pwd-check').is(':disabled')) {
					var keycode = e.keyCode || e.which;
					if (keycode === KeyCode.ENTER) {
						$('#pwd-check').trigger('click');
					}					
				}
			} else {
				$('#pwd-check').attr('disabled', true);
			}
			
		});	  
	  
	  
	  $('#pwd-check').on('click', function() {
		  
		var data = {
			encId: $('#xloginid').val(),	
			umUserPassword: $('#password').val()
			//encPwd: OMNI.auth.encode(OMNIEnv.pprs, $('#password').val()),
			//encConfirmPwd: OMNI.auth.encode(OMNIEnv.pprs, $('#password').val())
		};
		$.ajax({
			url:OMNIEnv.ctx + '/mgmt/pwdcheck',
			type:'post',
			data:JSON.stringify(data),
			dataType:'json',
			contentType : 'application/json; charset=utf-8',
			success: function(data) {
				if (data.status === 100) {
					location.href = OMNIEnv.ctx + '/mgmt/detail';
				} else {
					OMNI.popup.open({
						id: "pwd_check",
						closelabel: "확인",
						closeclass:'btn_blue',
						content: "비밀번호를 다시 확인해주세요."
					});
					$('.layer_wrap').focus();
				}
			},
			error: function() {
			}
			
		});		  
		  

	  });
	
  });
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="회원정보 관리"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>비밀번호 확인</h2>
        <p>소중한 개인정보 보호를 위해 비밀번호를 한번 더 확인하고 있습니다.</p>
        <span>(공용 PC 등 공공장소에서 사용 중 일 경우 타인에게 노출되지 않도록 주의해주시기 바랍니다.)</span>
      </div>
        <div class="user_info mb13">
          <strong class="st_txt">아이디: <c:out value="${loginid}" /></strong>
        </div>
        <div class="input_form">
          <span class="inp" id='password-span'>
            <input type="password" id='password' name='password' class="inp_text" maxlength="16" placeholder="비밀번호 (영문 소문자, 숫자, 특수문자 조합)"  title="비밀번호"/>
            <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
          </span>
          <p id="password-guide-msg" class="form_guide_txt is_success"></p>
        </div>
        <div class="btn_submit">
          <button type="button" class="btnA btn_blue" id='pwd-check' disabled>확인</button>
        </div>
    </section>
    <input type='hidden' id='xloginid' value='<c:out escapeXml="false" value="${xloginid}" />'/>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>