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
  <title>약관조회 테스트</title>
  <common:meta/>
  <common:css/>
  <common:js auth="false" popup="true"/>
  <script type="text/javascript">
  </script>   
</head>

<body>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="약관조회 테스트" type="close"/>
    <!-- container -->
    <section class="container">
      <div class="sec_join">
        <div class="user_info">
      ${term}
      	</div>
      </div>
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
</body>

</html>