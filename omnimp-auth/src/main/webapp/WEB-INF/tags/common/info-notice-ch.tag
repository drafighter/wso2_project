<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ tag	import="com.amorepacific.oneap.common.util.OmniUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="gaArea" type="java.lang.String"%>
<%@ attribute name="channelName" type="java.lang.String"%>
<%@ attribute name="channelCd" type="java.lang.String"%>
<%	
	// 경로구분코드가 이니스프리몰로 확인 (WSO2의 클라이언트ID로 식별), 
	// 이니스프리앱에서 호출된 경우 dt=A 파라미터 확인 
	// 앱 내 X 버튼의 히스토리백 기능 대체 필요
	// window.location = “innimemapp://go_back”
	boolean isOsullocMobileBackAction = OmniUtil.isOsullocMobileBackAction(request);
%>
<c:if test="${not empty channelName}" >
	<c:set var="channelName" value="(${channelName})" />
</c:if>
<c:choose>
	<c:when test="${channelCd eq '036' || channelCd eq '006'}">
                  <div class="info_notice">
                    <button type="button" class="btn_open_info" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 개인정보 처리 및 위탁에 대한 안내 버튼 <c:out value="${channelName}" />" ap-click-data="개인정보 처리 및 위탁에 대한 안내 <c:out value="${channelName}" />"><span>개인정보 처리 위탁에 대한 안내</span></button>
                    <div class="layer_info">
                      <strong>개인정보 처리 위탁에 대한 안내</strong>
                      <p>이니스프리는 서비스 향상 및 원활한 전산 처리 등을 위하여 이용자의 개인정보 관리를 외부 전문업체에 위탁하고 있습니다. 
                      이니스프리의 업무를 위탁 받는 자 및 업무의 내용은 이니스프리 홈페이지 개인정보처리방침(<a href="http://images.innisfree.co.kr/resources/common/privacy-txt-omni.html" target="_blank">http://images.innisfree.co.kr/resources/common/privacy-txt-omni.html</a>)에서 확인 하실 수 있습니다.</p>
                      <button type="button" class="close_layer_info"><span class="blind">닫기</span></button>
                    </div>
                  </div>	
	</c:when>
	<c:when test="${channelCd eq '042' || channelCd eq '016'}">
                  <div class="info_notice">
                    <button type="button" class="btn_open_info" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 개인정보 처리 및 위탁에 대한 안내 버튼 <c:out value="${channelName}" />" ap-click-data="개인정보 처리 및 위탁에 대한 안내 <c:out value="${channelName}" />"><span>개인정보 처리 위탁에 대한 안내</span></button>
                    <div class="layer_info">
                      <strong>개인정보 처리 위탁에 대한 안내</strong>
                      <p>에스쁘아는 서비스 향상 및 원활한 전산 처리 등을 위하여 이용자의 개인정보 관리를 외부 전문업체에 위탁하고 있습니다. 에스쁘아의 업무를 위탁받는 자 및 업무의 내용은 에스쁘아 홈페이지 개인정보처리방침(<a href="https://www.espoir.com/ko/html/html_footer_private.do" target="_blank">https://www.espoir.com/ko/html/html_footer_private.do</a>)에서 확인 하실 수 있습니다.</p>
                      <button type="button" class="close_layer_info"><span class="blind">닫기</span></button>
                    </div>
                  </div>	
	</c:when>
	<c:when test="${channelCd eq '099'}">
                  <div class="info_notice">
                    <button type="button" class="btn_open_info" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 개인정보 처리 및 위탁에 대한 안내 버튼 <c:out value="${channelName}" />" ap-click-data="개인정보 처리 및 위탁에 대한 안내 <c:out value="${channelName}" />"><span>개인정보 처리 위탁에 대한 안내</span></button>
                    <div class="layer_info">
                      <strong>개인정보 처리 위탁에 대한 안내</strong>
                      <p>에스트라는 서비스 향상 및 원활한 전산 처리 등을 위하여 이용자의 개인정보 관리를 외부 전문업체에 위탁하고 있습니다. 에스트라의 업무를 위탁받는 자 및 업무의 내용은 에스트라 홈페이지 개인정보처리방침(<a href="https://www.aestura.com/web/cscenter/privacy.do" target="_blank">https://www.aestura.com/web/cscenter/privacy.do</a>)에서 확인 하실 수 있습니다.</p>
                      <button type="button" class="close_layer_info"><span class="blind">닫기</span></button>
                    </div>
                  </div>	
	</c:when>
	<c:otherwise>
                  <div class="info_notice">
                    <button type="button" class="btn_open_info" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 개인정보 처리 및 위탁에 대한 안내 버튼 <c:out value="${channelName}" />" ap-click-data="개인정보 처리 및 위탁에 대한 안내 <c:out value="${channelName}" />"><span>개인정보 처리 위탁에 대한 안내</span></button>
                    <div class="layer_info">
                      <strong>개인정보 처리 위탁에 대한 안내</strong>
                      	<% if (isOsullocMobileBackAction) { // 오설록 App 으로 접근 시 target="_blank" 제거 %>
                      		<p>아모레퍼시픽은 서비스 향상 및 원활한 전산 처리 등을 위하여 이용자의 개인정보 관리를 외부 전문업체에 위탁하고 있습니다. 아모레퍼시픽의 업무를 위탁받는 자 및 업무의 내용은 아모레퍼시픽 홈페이지 <a href="https://www.amoremall.com/kr/ko/beautypoint/footer/privacy.do">https://www.amoremall.com/kr/ko/beautypoint/footer/privacy.do</a> 에서 확인 하실 수 있습니다.</p>
                      	<% } else { %>
                      		<p>아모레퍼시픽은 서비스 향상 및 원활한 전산 처리 등을 위하여 이용자의 개인정보 관리를 외부 전문업체에 위탁하고 있습니다. 아모레퍼시픽의 업무를 위탁받는 자 및 업무의 내용은 아모레퍼시픽 홈페이지 <a href="https://https://www.amoremall.com/kr/ko/beautypoint/footer/privacy.do" target="_blank">https://www.amoremall.com/kr/ko/beautypoint/footer/privacy.do</a> 에서 확인 하실 수 있습니다.</p>
                      	<% } %>	
                      <button type="button" class="close_layer_info"><span class="blind">닫기</span></button>
                    </div>
                  </div>	
	</c:otherwise>
</c:choose>