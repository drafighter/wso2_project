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
	  $('#do-join').on('click', function() {
		  $('#joinForm')
		  .attr('action', OMNIEnv.ctx + '/manual-join').submit();
	  });
  });
  
  </script>
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="회원가입 테스트" type="no"/>
    <!-- container -->
    <section class="container">
    <form id='joinForm' method='post' action=''>
      <div class="user_info">
        <h3>로그인 정보</h3>
        <dl class="dt_w20">
          <dt>채널코드</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="text" name="chCd" class="inp_text" placeholder="로그인 채널코드" value="036" title="로그인 채널코드"/>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>   
        <dl class="dt_w20">
          <dt>매장코드</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="text" name="joinPrtnId" class="inp_text" placeholder="매장코드" value="S1004" title="매장코드"/>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>
        <dl class="dt_w20">
          <dt>매장명</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="text" name="joinPrtnNm" class="inp_text" placeholder="매장명" value="매장명" title="매장명"/>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl> 
        <dl class="dt_w20">
          <dt>리턴 URL</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="text" name="returnUrl" class="inp_text" placeholder="리턴 URL" value="http://10.155.8.24/tablet/inniJoin.do?storeCd=I00000&userId=I0000001" title="리턴Url"/>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>                            
        <dl class="dt_w20">
          <dt>아이디</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="text" name="loginId" class="inp_text" placeholder="로그인 아이디" value="test01357" title="로그인 아이디"/>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>
		<dl class="dt_w20">
          <dt>비밀번호</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="password" name="loginPassword" class="inp_text" placeholder="로그인 비밀번호" value="password0987" title="로그인 비밀번호"/>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>        
      </div>
      <div style='margin-top:7px'></div>
      <div class="user_info">
        <h3>본인 인증정보</h3>
        <dl class="dt_w20">
          <dt>이름</dt>
          <dd>
			<div class="input_form">
            	<span class="inp">
              	<input type="text" name="userName" class="inp_text" placeholder="이름"  title="이름"/>
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
              	<input type="text" name="userBirth" class="inp_text" placeholder="생일"  title="생일"/>
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
              	<input type="text" name="userMobile" class="inp_text" placeholder="휴대폰" />
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
              	<input type="text" name="userGender" class="inp_text" placeholder="성별" value="M" title="성별"/>
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
              	<input type="text" name="userForeigner" class="inp_text" placeholder="외국인" value="K" title="외국인"/>
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
              	<textarea type="text" name="userCi" class="inp_text" placeholder="CI" title="CI"></textarea>
              	<button type="button" class="btn_del" tabIndex=-1><span class="blind">삭제</span></button>
            	</span>
          	</div>          
          </dd>
        </dl>                                                        
      </div>
      </form>
	  <p class="txt_l"></p>
      <div class="btn_submit">
        <button type="button" class="btnA btn_blue" id='do-join'>회원가입</button>
      </div>            
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>
</html>