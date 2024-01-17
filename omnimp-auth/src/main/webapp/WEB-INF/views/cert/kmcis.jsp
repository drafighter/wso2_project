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
  <title>본인인증 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	

  });
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="본인인증"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>뷰티포인트 통합회원 전환을 위한 본인인증 입니다.</h2>
      </div>
        <div class="input_form">
          <span class="inp">
            <input type="text" class="inp_text" placeholder="이름(실명으로 입력해주세요)"  title="이름"/>
            <button type="button" class="btn_del"><span class="blind">삭제</span></button>
          </span>
          <!-- <p class="form_guide_txt">비밀번호와 비밀번호 재입력이 일치하지 않습니다.</p> -->
        </div>
        <div class="input_form">
          <span class="inp">
            <input type="text" class="inp_text" placeholder="생년월일"  title="생년월일"/>
            <button type="button" class="btn_del"><span class="blind">삭제</span></button>
          </span>
          <!-- <p class="form_guide_txt">비밀번호와 비밀번호 재입력이 일치하지 않습니다.</p> -->
        </div> 
        <select id="telecom" name="mobileCorp" title="통신사" title="통신사 선택">
			<option value="">통신사 선택</option>
			<option value="SKT">SKT</option>
			<option value="KTF">KT</option>
			<option value="LGT">LG U+</option>
			<option value="SKM">SKT 알뜰폰</option>
			<option value="KTM">KT 알뜰폰</option>
			<option value="LGM">LG U+ 알뜰폰</option>
		</select>  
        <div class="input_form">
          <span class="inp">
            <input type="text" class="inp_text" placeholder="전화번호" title="전화번호" />
            <button type="button" class="btn_del"><span class="blind">삭제</span></button>
          </span>
          <!-- <p class="form_guide_txt">비밀번호와 비밀번호 재입력이 일치하지 않습니다.</p> -->
        </div> 						     
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>