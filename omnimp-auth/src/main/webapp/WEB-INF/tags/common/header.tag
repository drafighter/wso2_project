<%@ tag language="java" pageEncoding="UTF-8" body-content="empty"
	trimDirectiveWhitespaces="true"%>
<%@ tag	import="com.amorepacific.oneap.common.util.StringUtil,
 				com.amorepacific.oneap.common.util.ConfigUtil,
 				com.amorepacific.oneap.common.util.WebUtil,
 				com.amorepacific.oneap.common.util.OmniUtil,
 				com.amorepacific.oneap.common.vo.OmniConstants,
 				org.springframework.util.StringUtils"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="title" type="java.lang.String" required="true"%>
<%@ attribute name="type" type="java.lang.String"%>
<%@ attribute name="popupTarget" type="java.lang.String"%>
<%@ attribute name="url" type="java.lang.String"%>
<%@ attribute name="gaArea" type="java.lang.String"%>
<%@ attribute name="gaName" type="java.lang.String"%>
<%@ attribute name="gaData" type="java.lang.String"%>
<c:if test="${empty gaArea}" >
	<c:set var="gaArea" value="${title}" />
</c:if>
<%	type = type == null ? "back" : type;
	boolean isMobileApp = OmniUtil.isMobileApp(request);
	String hideHeader = WebUtil.getStringSession("hideHeader");
	if (StringUtils.isEmpty(hideHeader)) {
		if (WebUtil.isMobile() || isMobileApp) {
			hideHeader = WebUtil.getStringParameter("hh", "N");
			WebUtil.setSession("hideHeader", hideHeader);
		} else {
			hideHeader = "N"; // 웹일 경우는 항상 보여줌.
		}
	} else {
		if (!WebUtil.isMobile()) {
			hideHeader = "N"; // 웹일 경우는 항상 보여줌.
		}
	}
	
	// 경로구분코드가 이니스프리몰로 확인 (WSO2의 클라이언트ID로 식별), 
	// 이니스프리앱에서 호출된 경우 dt=A 파라미터 확인 
	// 앱 내 X 버튼의 히스토리백 기능 대체 필요
	// window.location = “innimemapp://go_back”
	boolean isInniMobileBackAction = OmniUtil.isInniMobileBackAction(request);
	boolean isBeautyAngelMobileBackAction = OmniUtil.isBeautyAngelMobileBackAction(request);
	boolean isAmoreMallAOS = OmniUtil.isAmoreMallAOS(request);
	boolean isAmoreMallIOS = OmniUtil.isAmoreMallIOS(request);
	// 모바일웹뷰에서는 타이틀 안보이도록 처리
	if (!"Y".equals(hideHeader)) { %>
<!-- header -->
<header class="header">
	<div class="headerBox">
		<div class="inner">
			<h1 class="page_tit"><c:out value="${title}" /></h1>
			<c:choose>
				<c:when test="${type eq 'prvclose'}">
					<button type="button" class="btn_prev_page prev-action" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 이전 버튼${gaName}" ap-click-data="이전"><span class="blind">이전</span></button>
					<button type="button" class="btn_page_close close-action" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 닫기 버튼${gaName}" ap-click-data="닫기"><span class="blind">닫기</span></button>
				</c:when>			
				<c:when test="${type eq 'prv'}">
					<button type="button" class="btn_prev_page header-redirect" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 이전 버튼${gaName}" ap-click-data="이전"><span class="blind">이전</span></button>
				</c:when>
				<c:when test="${type eq 'prvaction'}">
					<button type="button" class="btn_prev_page prv-action" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 이전 버튼${gaName}" ap-click-data="이전"><span class="blind">이전</span></button>
				</c:when>
				<c:when test="${type eq 'open'}">
					<button type="button" class="btn_page_close" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 닫기 버튼${gaName}" ap-click-data="닫기"><span class="blind">닫기</span></button>
				</c:when>
				<c:when test="${type eq 'close'}">
					<button type="button" class="btn_page_close header-close" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 닫기 버튼${gaName}" ap-click-data="닫기"><span class="blind">닫기</span></button>
				</c:when>
				<c:when test="${type eq 'closeaction'}">
					<button type="button"
						class="btn_page_close header-page-close close-action" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 닫기 버튼${gaName}" ap-click-data="닫기"><span class="blind">닫기</span></button>
				</c:when>
				<c:when test="${type eq 'goaction'}">
					<button type="button" class="btn_page_close header-go go-action" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 닫기 버튼${gaName}" ap-click-data="닫기"><span class="blind">닫기</span></button>
				</c:when>
				<c:when test="${type eq 'cancelbtn'}">
					<button type="button" class="btn_prev_page prev-action" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 이전 버튼${gaName}" ap-click-data="이전"><span class="blind">이전</span></button>
					<button type="button" class="btn_page_close close-action" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 닫기 버튼${gaName}" ap-click-data="닫기"><span class="blind">닫기</span></button>
				</c:when>
				<c:when test="${type eq 'no'}">
				</c:when>
				<c:otherwise>
					<button type="button" class="btn_page_close header-redirect" ap-click-area="<c:out value="${gaArea}" />" ap-click-name="<c:out value="${gaArea}" /> - 닫기 버튼${gaName}" ap-click-data="닫기"><span class="blind">닫기</span></button>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</header>
<!-- //header -->
<script>
    <c:choose>
    	<c:when test="${empty url}">
    	$(document).ready(function() {
    		<c:choose>
    		<c:when test="${type eq 'prvclose'}">
          	$('.prev-action').on('click', function() {
              	if ($.isFunction(window.prevAction)) {
        			window.prevAction();
        		}          		
          	});
          	$('.close-action').on('click', function() {
              	if ($.isFunction(window.closeAction)) {
              		<% if (isAmoreMallAOS) { %>
    					window.apmall.closeWebview();
    				<% } else if (isAmoreMallIOS) { %>
	    				window.location.href='apmall://closeWebview';
    				<% } else { %>
    					window.closeAction();	
    				<% } %>              		
        		}          		
          	});          	
    		</c:when>
          	<c:when test="${type eq 'closeaction'}">
          	$('.close-action').on('click', function() {
              	if ($.isFunction(window.closeAction)) {
        			window.closeAction();
        		}          		
          	});
          	</c:when>
          	<c:when test="${type eq 'goaction'}">
          	$('.go-action').on('click', function() {
              	if ($.isFunction(window.goAction)) {
        			window.goAction();
        		}          		
          	});
          	</c:when>
          	<c:when test="${type eq 'prvaction'}">
          	$('.prv-action').on('click', function() {
              	if ($.isFunction(window.prevAction)) {
        			window.prevAction();
        		}          		
          	});          	
          	</c:when>
          	<c:when test="${type eq 'cancelbtn'}">
          	$('.prev-action').on('click', function() {
          		if("<c:out value='${sessionScope.cancelUri}'/>" != null && "<c:out value='${sessionScope.cancelUri}'/>" != "") {
    				location.href = decodeURIComponent(("<c:out value='${sessionScope.cancelUri}'/>").replace(/&amp;/g, "&"));	
    			} else {
    				location.href = '${url}';	
    			}   
              	/* if ($.isFunction(window.prevAction)) {
              		window.history.back(); 
        		}     */      		
          	});
          	$('.close-action').on('click', function() {
          		if("<c:out value='${sessionScope.cancelUri}'/>" != null && "<c:out value='${sessionScope.cancelUri}'/>" != "") {
    				location.href = decodeURIComponent(("<c:out value='${sessionScope.cancelUri}'/>").replace(/&amp;/g, "&"));	
    			} else {
    				location.href = '${url}';	
    			}	
          	});
          	</c:when>
          	<c:when test="${type eq 'close'}">
          	$('.header-close').on('click', function() {
<% if (isInniMobileBackAction) { %>
				window.location = "innimemapp://go_back";
<% } else if (isBeautyAngelMobileBackAction) { %>
				window.location = "toapp://go_back";
<% } else if (isAmoreMallAOS) { %>
				window.apmall.closeWebview();
<% } else if (isAmoreMallIOS) { %>
				window.location.href='apmall://closeWebview';
<% } else { %>
				window.close();	
<% } %>
          	});
          	</c:when>
          	<c:otherwise>
		    $('.header-redirect').on('click', function() { 
<% if (isInniMobileBackAction) { %>
				window.location = "innimemapp://go_back";
<% } else if (isBeautyAngelMobileBackAction) { %>
				window.location = "toapp://go_back";
<% } else if (isAmoreMallAOS) { %>
				window.apmall.closeWebview();
<% } else if (isAmoreMallIOS) { %>
				window.location.href='apmall://closeWebview';
<% } else { %>
				window.history.back(); 
<% } %>
		    });
		</c:otherwise>
		</c:choose>

	});
	</c:when>
	<c:otherwise>
	$(document).ready(function() {
		<c:choose>
		<c:when test="${type eq 'prvclose'}">
      	$('.prev-action').on('click', function() {
          	if ($.isFunction(window.prevAction)) {
    			window.prevAction();
    		}          		
      	});
      	$('.close-action').on('click', function() {
          	if ($.isFunction(window.closeAction)) {
    			window.closeAction();
    		}          		
      	});          	
		</c:when>		
		<c:when test="${type eq 'closeaction'}">
		$('.close-action').on('click', function() {
			if ($.isFunction(window.closeAction)) {
				window.closeAction();
			}
		});
		</c:when>
		<c:when test="${type eq 'goaction'}">
		$('.go-action').on('click', function() {
			if ($.isFunction(window.goAction)) {
				window.goAction();
			}
		});
		</c:when>
      	<c:when test="${type eq 'prvaction'}">
      	$('.prv-action').on('click', function() {
          	if ($.isFunction(window.prevAction)) {
    			window.prevAction();
    		}          		
      	});          	
      	</c:when>
      	<c:when test="${type eq 'cancelbtn'}">
      	$('.prev-action').on('click', function() {
      		if("<c:out value='${sessionScope.cancelUri}'/>" != null && "<c:out value='${sessionScope.cancelUri}'/>" != "") {
				location.href = decodeURIComponent(("<c:out value='${sessionScope.cancelUri}'/>").replace(/&amp;/g, "&"));	
			} else {
				location.href = '${url}';	
			}   
          	/* if ($.isFunction(window.prevAction)) {
          		window.history.back(); 
    		}       */    		
      	});
      	$('.close-action').on('click', function() {
      		if("<c:out value='${sessionScope.cancelUri}'/>" != null && "<c:out value='${sessionScope.cancelUri}'/>" != "") {
				location.href = decodeURIComponent(("<c:out value='${sessionScope.cancelUri}'/>").replace(/&amp;/g, "&"));	
			} else {
				location.href = '${url}';	
			}      		
      	});
     	</c:when>
		<c:when test="${type eq 'close'}">
		$('.header-close').on('click', function() {
<% if (isInniMobileBackAction) { %>
			window.location = "innimemapp://go_back";
<% } else if (isBeautyAngelMobileBackAction) { %>
			window.location = "toapp://go_back";
<% } else if (isAmoreMallAOS) { %>
			window.apmall.closeWebview();
<% } else if (isAmoreMallIOS) { %>
			window.location.href='apmall://closeWebview';
<% } else { %>
			window.close();	
<% } %>
		});
		</c:when>
		<c:otherwise>
		$('.header-redirect').on('click', function() {
			var UserAgent = navigator.userAgent;
			var isMobile = UserAgent.match(/Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null || UserAgent.match(/LG|SAMSUNG|Samsung/) != null;
			<c:if test="${mobile}">
					isMobile = true;
			</c:if>			
			
			if(!isMobile && getParameterByName('popup') == 'true') {
				window.close();	
			} else {
				if(getParameterByName('cancelUri') == null || getParameterByName('cancelUri') == "") {
					if("<c:out value='${sessionScope.cancelUri}'/>" != null && "<c:out value='${sessionScope.cancelUri}'/>" != "") {
						<% if (isAmoreMallAOS) { %>
							window.apmall.closeWebview();
						<% } else if (isAmoreMallIOS) { %>
							window.location.href='apmall://closeWebview';
						<% } else { %>
							location.href = decodeURIComponent(("<c:out value='${sessionScope.cancelUri}'/>").replace(/&amp;/g, "&"));	
						<% } %>							
					} else {
						<% if (isAmoreMallAOS) { %>
							window.apmall.closeWebview();
						<% } else if (isAmoreMallIOS) { %>
							window.location.href='apmall://closeWebview';
						<% } else { %>
							location.href = '${url}';	
						<% } %>						
					}
				} else {
					<% if (isAmoreMallAOS) { %>
						window.apmall.closeWebview();
					<% } else if (isAmoreMallIOS) { %>
						window.location.href='apmall://closeWebview';
					<% } else { %>
						location.href = decodeURIComponent(getParameterByName('cancelUri'));
					<% } %>						
				}				
			}
			// location.href = '${url}';
		});
		</c:otherwise>
		</c:choose>
	});
	</c:otherwise>
	</c:choose>
</script>
<%	}	%>