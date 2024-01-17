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
	<common:meta/>
	<common:js auth="true" popup="true"/>
	<tagging:google/>
	<meta charset="UTF-8">
	<title>SNS 매핑 테스트 | 옴니통합회원</title>
	<style>
			
		table {			
			width: 500px;
			height: 200px;
		}
		
		th, td {
			border: 1px solid;
		}
		
		td, input {
			
			text-align: center;
		}
		
	</style>
	<script type="text/javascript">
		
		
		$(document).ready(function() {	
			
			console.log(OMNIEnv.ctx + '/sample/sns_mapping');
			
			$('.snsConnectBtn').on('click', function() {
				
				if($('#inputLoginId').val() === '' || $('#inputIncsNo').val() === '') {
					alert('회원 정보를 입력 하세요');
					return;
				} 
				
				var snsType = $(this).data('key');
				
				$('#snsType').val( snsType );
				$('#loginId').val( $('#inputLoginId').val() );
				$('#incsNo').val( $('#inputIncsNo').val() );
				
				$('#snsMappingForm').submit();
			});
			
			if($('#resultCode').val() !== '') {
				var selector = '#' + $('#snsType').val() + "code";
				var code = $('#resultCode').val();
				
				$(selector).val(code);
			}
		});
		
		  
	</script>
</head>
<body>
	<br/>
	<div align="center">
		<h2>SNS Mapping Test Page</h2>
		<hr/>
		
		<div>
			<span width="300px;"></span>
			<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			아이디 :&nbsp;&nbsp;</span><input type="text" id="inputLoginId"/>
		</div>
		<div>
			<span>통합고객번호 :&nbsp;&nbsp;</span><input type="text" id="inputIncsNo"/>
		</div>
				
		<br/>
		<table>
		    <tr>
		        <th>SNS</th>
		        <th>연동버튼</th>
		        <th>결과코드</th>
		    </tr>
		    <tr>
		        <td>카카오</td>
		        <td>
		        	<button class="snsConnectBtn" data-key="KA">연동 테스트</button>
		        </td>
		        <td>
		        	<input type="text" id="KAcode" disabled/>
		        </td>    
		    </tr>
		    <tr>
		    	<td>네이버</td>
		    	<td>
		    		<button class="snsConnectBtn" data-key="NA">연동 테스트</button>
		    	</td>
		    	<td>
		    		<input type="text" id="NAcode" disabled/>
		    	</td>
		    </tr>
		    <tr>
		    	<td>페이스북</td>
		    	<td>
		    		<button class="snsConnectBtn" data-key="FB">연동 테스트</button>
		    	</td>
		    	<td>
		    		<input type="text" id="FBcode" disabled/>
		    	</td>
		    </tr>	        
		</table>	
	</div>

	<form id="snsMappingForm" method="POST" action="<c:out value='${ctx}'/>/sns/auth">
	  	<input type='hidden' id='snsType' name='snsType' value='<c:out value="${snsType}" />'/>
	  	<!-- input value -->
	  	<input type='hidden' id='loginId' name='loginId' value=''/>
	  	<input type='hidden' id='incsNo' name='incsNo' value=''/>
	  	<!-- end of test value -->
	  	<input type='hidden' id="redirectUrl" name='redirectUrl' value="<c:out value='${ctx}'/>/sample/sns_mapping"/><!-- value='http://amorepacific.com:8081/sample/sns_mapping'/> -->
	  	<input type='hidden' id="resultCode" value='<c:out value="${resultCode}" />'/>
	</form>

</body>
</html>