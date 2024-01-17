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
  <title>안내 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
	  <c:if test="${not empty message}">
		OMNI.popup.open({
			id:'omni-waring',
			content: '<c:out value="${message}"/>',
			closelabel:'확인',
			closeclass:'btn_blue'
		});				  
		$('.layer_wrap').focus();
	  </c:if>	
	  
	  $('#btn_login').on('click', function() {
		  OMNI.loading.show();
		  var auth = OMNI.auth.getWso2AuthData('<c:out value="${chCd}" />');
		  
		  var authParam = "channelCd=" + auth.channelCd;
			authParam += "&client_id=" + auth.client_id;
			authParam += "&redirectUri=" + encodeURIComponent(auth.redirectUri);
			authParam += "&redirect_uri=" + encodeURIComponent(auth.redirect_uri);
			authParam += "&response_type=" + auth.response_type;
			authParam += "&scope=" + auth.scope;
			authParam += "&state=" + encodeURIComponent(auth.state);
			authParam += "&type=" + auth.type;
			authParam += "&join=" + auth.join;
			authParam += "&vt=" + auth.vt;
			
			OMNI.auth.clearWso2AuthData();
		  
		  location.href = '<c:out escapeXml="false" value="${authurl}" />' + '?' + authParam;		  
	  });
	  
  });
  </script>   
</head>
<body>
	<tagging:google noscript="true"/>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="안내" type="close"/>
    <!-- container -->
    <section class="container">
      <div class="error_wrap">
        <h2>고객님 불편을 드려 죄송합니다.</h2>
        <p class="txt">방문 주소가 잘못 입력되었거나, 변경 혹은 삭제되어 <br />이용하실 수가 없습니다. 다시 한번 확인해주시거나, <br />잠시 후 이용해주시기 바랍니다.</p>
        <p><c:out value="${message}"/></p>
        <div class="btn_submit">
          <button type="button" class="btnA btn_blue" id='btn_login'>로그인 화면으로</button>
        </div>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>