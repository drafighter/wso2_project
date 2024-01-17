 <%@ tag language="java" pageEncoding="UTF-8" body-content="empty" trimDirectiveWhitespaces="true"%>
 <%@ tag import="com.amorepacific.oneap.common.util.StringUtil,
 				 com.amorepacific.oneap.common.util.ConfigUtil,
 				 com.amorepacific.oneap.common.validation.SystemInfo,
 				 org.springframework.context.ApplicationContext,
 				 org.springframework.web.servlet.support.RequestContextUtils,
 				 org.springframework.util.StringUtils"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <c:set var="ctx" value="${pageContext.request.contextPath}"/>
 <%@ attribute name="auth" type="java.lang.Boolean"%>
 <%@ attribute name="popup" type="java.lang.Boolean" %>
 <%@ attribute name="aes" type="java.lang.Boolean" %>
 <%@ attribute name="off" type="java.lang.Boolean" %>
 <%@ attribute name="swipe" type="java.lang.Boolean" %>
 <%@ attribute name="authCategory" type="java.lang.Boolean" %>
 <% auth = auth == null ? false : auth; 
 	popup = popup == null ? false : popup; 
 	aes = aes == null ? false : aes;
 	off = off == null ? false : off;
 	swipe = swipe == null ? false: swipe;
 	authCategory = authCategory == null ? false: authCategory;
 	ConfigUtil omniConfig = ConfigUtil.getInstance();
 	final int aesiteration = omniConfig.getInt("security.aes.iteration", 1000);
 	final int aeskeysize = omniConfig.getInt("security.aes.keysize", 128);
 	final String aessalt = omniConfig.getString("security.aes.salt");
 	final String aesiv = omniConfig.getString("security.aes.iv");
 	final String passphrase = omniConfig.getDecryptPassphrase(); 
 	final boolean paramencoding = omniConfig.isParamEncoding();
 	String chCd = (String) request.getAttribute("chCd");
 	chCd = StringUtils.isEmpty(chCd) ? omniConfig.getSessionChCd() : chCd;
 	chCd = StringUtils.isEmpty(chCd) ? (String) request.getParameter("chCd") : chCd;
 	chCd = StringUtils.isEmpty(chCd) ? (String) request.getParameter("channelCd") : chCd;
 	ApplicationContext applicationContext = RequestContextUtils.findWebApplicationContext(request);
 	SystemInfo systemInfo = (SystemInfo) applicationContext.getBean("systemInfoBean");
 	String profile = systemInfo.getActiveProfiles()[0];
 	profile = StringUtils.isEmpty(profile) ? "dev" : profile;
	profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
 	final boolean isBrandSite = StringUtils.isEmpty(chCd) ? false : omniConfig.isBrandSite(chCd, profile);%>
  <!--[if lt IE 9]>
  <script type="text/javascript" src="<c:out value='${ctx}'/>/js/lib/html5.js"></script>
  <![endif]-->
  <script type="text/javascript">
  	<%
  		if (isBrandSite) {
  	%>
  		window.AP_SIGNUP_CHANNEL = '030-<c:out value="${chCd}"/>';	
  	<%
  		} else {
	%>
		window.AP_SIGNUP_CHANNEL = '<c:out value="${chCd}"/>';
	<%
  		}
	%>
	window.AP_SIGNUP_CHANNELNAME = '<c:out value="${chNm}"/>';
	window.AP_SIGNUP_REDIRECT = '<c:out value="${rdUrl}"/>';
	window.AP_CHANNEL_URL = '<c:out value="${url}"/>';
	window.AP_DATA_COUNTRY = 'kr';
	window.AP_DATA_LANG = 'ko';
  
    OMNI = {};
  	OMNIEnv = { ctx:'<c:out value="${ctx}"/>', sp:'<%= StringUtil.dot() %>', pprs:'<%= passphrase %>', aes:{ksz:<%= aeskeysize %>,itr:<%= aesiteration %>,slt:'<%= aessalt %>',ivs:'<%= aesiv %>'},pe:<%= paramencoding %> };
  	OMNIData = {
  		authFailure:'<c:out value="${authFailure}"/>',
  		authFailureMsg:'<c:out value="${authFailureMsg}"/>',
  		mappingNotice:'<c:out value="${mappingNotice}"/>',
  		snsError: '<c:out value="${snsError}"/>',
  		chCd:'<c:out value="${chCd}"/>',
  		offline:'<c:out value="${offline}"/>',
  		lastlogin:'<c:out value="${lastlogin}"/>',
  		mtype:'<c:out value="${mobile}"/>',
  		mappingSnsType:'<c:out value="${mappingSnsType}"/>'
  	};
  	Wso2Data = {channelCd:'<c:out value="${login.ssoParam.channelCd}"/>',client_id:'<c:out value="${login.ssoParam.client_id}"/>',redirectUri:'<c:out value="${login.ssoParam.redirectUri}"/>',cancelUri:'<c:out value="${login.ssoParam.cancelUri}"/>',redirect_uri:'<c:out value="${login.ssoParam.redirect_uri}"/>',response_type:'<c:out value="${login.ssoParam.response_type}"/>',scope:'<c:out value="${login.ssoParam.scope}"/>',state:'<c:out value="${login.ssoParam.state}"/>',type:'<c:out value="${login.ssoParam.type}"/>',join:'<c:out value="${login.ssoParam.join}"/>',popup:'<c:out value="${login.ssoParam.popup}"/>'};
  </script>  
  <c:if test="${authCategory}">
	  <script type="text/javascript">
	  	if('${category}' != '') {
	  		window.AP_SIGNUP_AUTH = '<c:out value="${category}" />'
	  	}
	  	if('${param.category}' != '') {
	  		window.AP_SIGNUP_AUTH = '<c:out value="${param.category}" />'		
	  	}
  </script>
  </c:if>
  <!-- js -->
  <script type="text/javascript" src="<c:out value='${ctx}'/>/js/lib/jquery-3.3.1.min.js"></script>
  <script type="text/javascript" src="<c:out value='${ctx}'/>/js/lib/jquery-ui-1.12.1.min.js"></script>
  <script type="text/javascript" src="<c:out value='${ctx}'/>/js/lib/jquery.easing.1.3.min.js"></script>
  <script type="text/javascript" src="<c:out value='${ctx}'/>/js/lib/jquery.i18n.properties-1.2.7.min.js"></script>
  <script type="text/javascript" src="<c:out value='${ctx}'/>/js/lib/jquery-key.0.2.js"></script>
  <c:if test="${swipe}">
  <script type="text/javascript" src="<c:out value='${ctx}'/>/js/lib/swiped-events-1.1.4.min.js"></script>
  </c:if>  
  <c:if test="${auth}">
  <script type="text/javascript" src="<c:out value='${ctx}'/>/js/lib/aes.min.js"></script>
  </c:if>
  <c:choose>
  	<c:when test="${off}">
  <script type="text/javascript" src="<c:out value='${ctx}'/>/js/core/oneap-common-off.js?ver=<%= omniConfig.resourceVersion() %>"></script>   	
  	</c:when>
  	<c:otherwise>
  <script type="text/javascript" src="<c:out value='${ctx}'/>/js/core/oneap-common.js?ver=<%= omniConfig.resourceVersion() %>"></script> 	
  	</c:otherwise>
  </c:choose>
  <c:if test="${auth}">
  <script type="text/javascript" src="<c:out value='${ctx}'/>/js/core/oneap-auth.js?ver=<%= omniConfig.resourceVersion() %>"></script>
  </c:if>
  <c:if test="${popup}">
  <script type="text/javascript" src="<c:out value='${ctx}'/>/js/core/oneap-popup.js?ver=<%= omniConfig.resourceVersion() %>"></script>
  </c:if>