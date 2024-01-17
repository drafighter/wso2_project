<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<!DOCTYPE html><!-- 500 -->
<html lang="ko">
<head>
  <title>Error page | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="false" popup="false"/>
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
  	<common:header title="Error page" type="prv"/>
    <!-- container -->
    <section class="container">
      <div class="error_wrap">
        <h2>오류가 발생하였습니다.</h2>
        <p class="txt">처리 중 오류가 발생하였습니다. 잠시 후 이용해주시기 바랍니다.</p>
        <p><c:out value='${message}'/></p>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
<%--  
  <!-- 
  request_uri : <c:out value="${requestScope['javax.servlet.error.request_uri']}"/>
  status_code : <c:out value="${requestScope['javax.servlet.error.status_code']}"/>
  servlet_name : <c:out value="${requestScope['javax.servlet.error.servlet_name']}"/>
  exception : <c:out value="${requestScope['javax.servlet.error.exception']}"/>
  servlet_name : <c:out value="${requestScope['javax.servlet.error.servlet_name']}"/>
  message : <c:out value="${requestScope['javax.servlet.error.message']}"/>
  -->
--%>  
</body>
<common:backblock block="false"/>
</html>