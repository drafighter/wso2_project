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
  <common:js auth="true" popup="false"/>
  <tagging:google/>
  <script type="text/javascript">
  $(document).ready(function() {
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
  </script>   
</head>

<body>
	<tagging:google noscript="true"/>
</body>

</html>