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
        <h2>페이지를 찾을 수 없습니다.</h2>
        <p>방문하려는 페이지의 주소가 잘못 입력되었거나, <br />페이지의 주소가 변경 혹은 삭제되어 요청하신 페이지를 <br />찾을수 없습니다. 입력하신 주소가 정확한지 <br />다시 한번 확인해 주시기 바랍니다.</p>
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
<common:backblock block="true"/>
</html>