<%@page import="com.amorepacific.oneap.common.util.WebUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common" %>
<%@ taglib prefix="tagging" tagdir="/WEB-INF/tags/tagging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <title>QR 코드 생성 | 옴니통합회원</title>
  <common:meta/>
  <common:css/>
  <common:js auth="true" popup="true"/>
	<script type="text/javascript">
	$(document).ready(function() {
		$('#qr-gen').on('click', function() {
			if ($('#url').val() === '') {
				return;
			}
			
			//var encodeurl = encodeURI($('#url').val());
			var encodeurl = $('#url').val();
			$('#qrurl').val(encodeurl);
			$('#qrform').attr('action', OMNIEnv.ctx + '/qr-gen').submit();
			
		});
	});
	</script>
</head>
<body>
  <!-- wrap -->
  <div id="wrap" class="wrap">
  	<common:header title="QR 코드 생성" type="no"/>
    <!-- container -->
    <section class="container">
      <div class="sec_login">
          <div class="input_form">
            <span class="inp" id="loginid-span">
              <input type="text" id="url" autocomplete="off" class="inp_text" placeholder="QR 코드 URL" title="QR 코드 URL" />
              <button type="button" class="btn_del" ><span class="blind">삭제</span></button>
            </span>
          </div>
      </div>
      <div class="btn_submit ver2">
          <button type="button" id='qr-gen' class="btnA btn_blue" >QR코드 생성</button>
      </div>      
    </section>
    <!-- //container -->
  </div><!-- //wrap -->
  <form id='qrform' method='post' action=''>
  	<input type='hidden' id='qrurl' name='qrurl'/>
  </form>
</body>
</html>