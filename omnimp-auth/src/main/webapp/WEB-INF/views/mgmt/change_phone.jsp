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
  <title>휴대폰 번호 변경 | 옴니통합회원</title>
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
  	<common:header title="휴대폰 번호 변경" type="prv"/>
    <!-- container -->
    <section class="container">
      <div class="page_top_area">
        <h2>변경할 휴대폰 번호를 입력하세요.</h2>
      </div>
      <div class="sec_login">
        <form>
          <div class="input_form">
            <span class="inp">
              <input type="tel" class="inp_text" maxlength="11" placeholder="휴대폰 번호 입력 (‘-’ 생략)"  title="휴대폰 번호 입력"/>
              <button type="button" class="btn_del"><span class="blind">삭제</span></button>
            </span>
          </div>
          <div class="btn_submit mt20">
            <button type="button" class="btnA btn_blue" ap-click-area="아이디 찾기" ap-click-name="아이디 찾기 - 인증번호 발송 버튼" ap-click-data="인증번호 발송">인증번호 발송</button>
            <!-- <button type="submit" class="btnA btn_blue" disabled>인증하고 변경</button> -->
          </div>
        </form>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>