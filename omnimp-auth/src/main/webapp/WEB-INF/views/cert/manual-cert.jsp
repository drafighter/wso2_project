<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>본인인증 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	  
	  $('#do-cert').on('click', function() {
		  
		  $('#certForm')
		  .attr('action', OMNIEnv.ctx + '/cert/manual-cert-result').submit();
		  
	  });
	  
	  $('.inp_text').on('keyup', function() {
		  
		  disabledOnOff();
	  });
	  
  });
  var disabledOnOff = function() {
	  var condition = true;
	  condition &= $("#chCd").val() !== '';
	  condition &= $("#userName").val() !== '';
	  condition &= $("#userBirth").val() !== '';
	  //condition &= $("#userMobile").val() !== ''; // ipin인 경우는 휴대폰 없음.
	  condition &= $("#userGender").val() !== '';
	  condition &= $("#userForeigner").val() !== '';
	  condition &= $("#userCi").val() !== '';
	  if (condition) {
		  $('#do-cert').removeAttr('disabled');
	  } else {
		  $('#do-cert').attr('disabled', 'disabled');
	  }
  };
  </script>
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="본인 인증정보 수동 처리" type="close"/>
    <!-- container -->
    <section class="container">
    <form id='certForm' method='post' action=''>
      <input type='hidden' name='certiType' value='<c:out value="${certiType}" />'/>
      <div class="user_info">
        <h3>본인 인증정보</h3>
        <dl class="dt_w20">
          <dt>채널코드</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="text" id="chCd" name="chCd" class="inp_text" placeholder="로그인 채널코드" value="<c:out value="${chCd}" />" title="로그인 채널코드"/>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>        
        <dl class="dt_w20">
          <dt>이름</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="text" id="userName" name="userName" class="inp_text" placeholder="이름"  title="이름"/>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>
		<dl class="dt_w20">
          <dt>생일</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="text" id="userBirth" name="userBirth" class="inp_text" placeholder="생일"  title="생일"/>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>
        <dl class="dt_w20">
          <dt>휴대폰</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="tel" minlength=10 maxlength=11 id="userMobile" name="userMobile" class="inp_text" placeholder="휴대폰"  title="휴대폰"/>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>        
		<dl class="dt_w20">
          <dt>성별</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="text" id="userGender" name="userGender" class="inp_text" placeholder="성별" value="M" title="성별"/>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>
		<dl class="dt_w20">
          <dt>외국인</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="text" id="userForeigner" name="userForeigner" class="inp_text" placeholder="외국인" value="K" title="외국인"/>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>
		<dl class="dt_w20">
          <dt>CI</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<textarea type="text" id="userCi" name="userCi" class="inp_text" placeholder="CI" title="CI"></textarea>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>                                                        
      </div>
      </form>
	  <p class="txt_l"></p>
      <div class="btn_submit">
        <button type="button" class="btnA btn_blue" id='do-cert' disabled>본인인증수동처리</button>
      </div>            
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
</html>