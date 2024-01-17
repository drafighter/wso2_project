<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri = "http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ attribute name="mobilelogin" type="java.lang.Boolean"%>
<%@ attribute name="lastlogin" type="java.lang.String"%>
<%@ attribute name="resp" type="com.amorepacific.oneap.auth.login.vo.LoginResponse"%>
<%@ attribute name="gaArea" type="java.lang.String"%>
<spring:eval expression="@environment.getProperty('spring.profiles.active')" var="profile" />
<%	mobilelogin = mobilelogin == null ? false : mobilelogin;
	lastlogin = lastlogin == null ? "" : lastlogin; 
	String phoneetag = mobilelogin ? "-phone" : "";
	String phonecss = mobilelogin ? "v_btn2" : ""; %>
	<c:if test="${!vtdisable}">
        <ul class="etc_login <%= phonecss %>"><!-- 간편로그인 버튼 두개일때 v_btn2 클래스 추가 -->
          <c:if test="${not mobilelogin}">
          <li>
            <button type="button" id="mobile-login" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 휴대폰 로그인 버튼" ap-click-data="휴대폰 로그인"><img src="<c:out value='${ctx}'/>/images/common/btn_login_mobile.png" alt="">휴대폰<br />로그인</button>
            <c:if test="${lastlogin eq 'mobile' and mobile}">
            <span class="login_tootip verL">
              최근 로그인한 계정이에요
              <a href="javascript:;" class="btn_tooltip_close"><span class="blind">로그인툴팁 닫기</span></a>
            </span>
            </c:if>
          </li>
          </c:if>
		<c:if test="${not empty resp}">
			<c:forEach var="item" items="${resp.idpAuthenticatorMapping}" varStatus="status">
				<c:if test="${item.key ne 'LOCAL'}">
					<c:if test="${item.key ne 'FB' or (item.key eq 'FB' and empty isAndroidApp)}">
			          <li>
			            <button class='sns-btn' type="button" data-key='<c:out value="${item.key}"/>' data-val='<c:out value="${item.value}"/>' ap-click-area="<c:out value="${gaArea}" />" ap-click-name='<c:out value="${gaArea}" /> - <spring:message code="sns.${item.value}.title"/> 로그인 버튼' ap-click-data='<spring:message code="sns.${item.value}.title"/> 로그인'><img src="<c:out value='${ctx}'/>/images/common/btn_login_<c:out value="${fn:toLowerCase(item.key)}"/>.png" alt=""><spring:message code="sns.${item.value}.title"/><br />로그인</button>
			            <c:if test="${lastlogin eq fn:toLowerCase(item.key) and mobile}">
			            <span class="login_tootip verC">
			              최근 로그인한 계정이에요
			              <a href="javascript:;" class="btn_tooltip_close"><span class="blind">로그인툴팁 닫기</span></a>
			            </span>
			            </c:if>
			          </li>					
					</c:if>					
				</c:if>
			</c:forEach>
		</c:if>
        </ul>
	</c:if>
        <ul class="bottom_menu">
          <li><a href="javascript:;" id='search_id' ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 아이디 찾기 버튼" ap-click-data="아이디 찾기">아이디 찾기</a></li>
          <li><a href="javascript:;" id='search_pwd' ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 비밀번호 찾기" ap-click-data="비밀번호 찾기">비밀번호 찾기</a></li>
          <c:if test="${isNonMemberEnable}">
          	<c:if test="${empty nonMemberName}">
          		<li><a href="javascript:;" id='search_order' ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 비회원 주문/조회 버튼" ap-click-data="비회원 주문/조회">비회원 주문/조회</a></li>
          	</c:if>
          	<c:if test="${not empty nonMemberName}">
          		<li><a href="javascript:;" id='search_order' ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 비회원 주문/조회 버튼" ap-click-data="비회원 주문/조회"><c:out value="${nonMemberName}"/></a></li>
          	</c:if>
          </c:if>
        </ul>
        <button class="btnA btn_white btn_join_membership" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 회원가입" ap-click-data="회원가입">
          <span>아직 회원이 아니세요?</span>
          <em>회원가입</em>
        </button> 