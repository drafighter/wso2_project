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
	<title>에뛰드 서비스 이용약관</title>
	<link rel="stylesheet" type="text/css" href="<c:out value='${ctx}'/>/css/bp_common.css">
</head>

<body>
<div class="pop-wrap">
	<div class="agree-txt">
		<h3>서비스 이용약관 동의</h3>
            <h5>제 1 장 (총칙)</h5>
            <h6>제 1 조 (목적)</h6>
            <p>이 약관은 ㈜에뛰드(이하 &quot;회사&quot;라 함)가 운영하는 에뛰드하우스 쇼핑몰(www.etudehouse.com, www.etude.com, 이하 &quot;몰&quot;)에서 제공하는 관련 서비스(이하 &quot;서비스&quot;라 함)를 이용함에 있어 고객의 권리, 의무 및 책임 사항을 규정함을 목적으로 합니다. 스마트폰 등 이동통신기기를 통해 제공되는 모바일 어플리케이션 등 기타 전자상거래에 대해서도 그 목적과 성질에 반하지 않는 한 이 약관이 적용됩니다.</p>
            <h6>제 2 조 (정의)</h6>
            <p>이 약관에서 사용하는 용어의 정의는 다음과 같습니다.</p>
            <ul class="agree-list-num">
              <li>
                ①  &quot;몰 &quot;이란 회사가 재화 또는 용역(이하  &quot;재화 등 &quot;이라 함)을 고객에게 제공하기 위하여, 컴퓨터 등 정보통신설비를 이용하여 재화 등을 거래할 수 있도록 설정한 영업장을 말하며, 아울러 몰을 운영하는 사업자의 의미로도 사용합니다.
              </li>
              <li>
                ② &quot;고객&quot;이란 몰에 접속하거나 방문하여 이 약관에 따라 회사가 제공하는 서비스를 받는 회원 및 비회원을 말합니다.
              </li>
              <li>
                ③ &quot;서비스&quot;란 회사가 몰에서 제4조에서 정한 내용의 업무를 통하여 고객에게 제공하는 유ㆍ무형의 행위 등을 말합니다.
              </li>
              <li>
                ④ &quot;회원&quot;이라 함은 회사가 정한 회원 가입절차에 따라 개인정보 등을 제공하여 회원등록을 한 자로서, 회사 및 몰의 정보를 제공받으며, 회사가 제공하는 서비스를 계속적으로 이용할 수 있는 자를 말합니다.
              </li>
              <li>
                ⑤ &quot;비회원&quot;이라 함은 몰에 회원가입을 하지 않고 몰이 제공하는 서비스를 이용하는 자를 말합니다.
              </li>
              <li>
                ⑥ &quot;포인트&quot;라 함은 몰에서 활동 및 구매를 함으로써 일정 기준에 부합할 경우 적립, 사용 등이 가능 한 것으로 포인트 운영 정책에 따라 사용 가능합니다. 포인트는 온라인 상에서 이 약관에 정해진 바에 따라 회원가입 절차를 거친 회원에게 제공합니다. 단, 뷰티포인트는 회사가 ㈜아모레퍼시픽과 제휴 계약을 통해 운영하는 포인트로서 아모레퍼시픽 통합 맴버십 뷰티포인트 회원으로 가입한 고객에 한해, 뷰티포인트 제도에 따라 적립 또는 사용 등이 가능합니다.
              </li>
              <li>
                ⑦ &quot;입점몰 통신판매중개서비스&quot;라 함은 회사가 몰을 통하여 제공하는 통신판매중개서비스 및 관련 부가서비스 일체를 말합니다.
              </li>
              <li>
                ⑧ &quot;판매자&quot;라 함은 회사와 판매자 간에 입점몰 중개서비스 이용계약을 체결 및 이 약관을 승인한 후 몰을 통해 실제로 상품을 고객에게 판매하는 자를 말합니다.
              </li>
            </ul>
            <h6>제 3 조 (약관의 등의 명시와 설명 및 개정)</h6>
            <ul class="agree-list-num">
              <li>
                ① 회사는 이 약관의 내용과 상호 및 대표자 성명, 영업소 소재지 주소(고객의 불만을 처리할 수 있는 곳의 주소를 포함), 전화번호, 팩스전송번호, 전자우편주소, 사업자등록번호, 통신판매업신고번호, 개인정보 보호책임자 등을 고객이 쉽게 알 수 있도록 몰의 초기화면에 게시합니다. 다만, 약관의 구체적 내용은 고객이 연결화면을 통하여 볼 수 있도록 할 수 있습니다.
              </li>
              <li>
                ② 회사는 고객이 이 약관에 동의하기에 앞서 약관에 정하여져 있는 내용 중 청약철회, 배송책임, 환불조건 등과 같은 중요한 내용을 고객이 이해할 수 있도록 별도의 화면을 통해 제공할 수 있습니다.
              </li>
              <li>
                ③ 회사는 전자상거래 등에서의 소비자보호에 관한 법률, 약관의 규제에 관한 법률, 전자거래기본법, 전자서명법, 정보통신망 이용촉진 등에 관한 법률, 방문 판매 등에 관한 법률, 소비자보호법, 개인정보보호법 등 관련법을 위배하지 않는 범위에서 이 약관을 개정할 수 있습니다.
              </li>
              <li>
                ④ 회사가 이 약관을 개정할 경우에는 변경된 약관을 적용하고자 하는 날(이하 &quot;효력 발생일&quot;)로부터 7일(회원에게 불리한 변경의 경우 30일) 이전에 이 약관이 변경된다는 사실과 변경된 내용 등을 공지 또는 통지합니다. 이 경우 개정 전 내용과 개정 후 내용을 명확하게 비교하여 고객에게 알기 쉽도록 표시합니다.
              </li>
            </ul>
	</div>
</div>
</body>
<script type="text/javascript">
	var uagent = navigator.userAgent.toLocaleLowerCase();
	
	function CloseTerms() {
		if(!(uagent.search("iphone")>-1||uagent.search("ipod")>-1||uagent.search("ipad")>-1)){
	    	close();
	    }else {
			history.back();
	    }
	}
</script>
</html>