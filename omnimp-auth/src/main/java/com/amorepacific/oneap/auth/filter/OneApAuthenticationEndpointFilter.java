/*
 * <pre>
 * Copyright (c) 2020 Amore Pacific.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of Amore
 * Pacific. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Amore Pacific.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author	          : takkies
 * Date   	          : 2020. 8. 5..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.filter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
//import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;

/**
 * <pre>
 * com.amorepacific.oneap.auth.filter 
 *    |_ OneApAuthenticationEndpointFilter.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 5.
 * @version : 1.0
 * @author : takkies
 */
public class OneApAuthenticationEndpointFilter implements Filter {

//	private ServletContext context = null;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
//		context = filterConfig.getServletContext();
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		Object skipFilterAttribute = servletRequest.getAttribute(OmniConstants.ATTRIBUTE_SKIP_PROPERTY);
		if (skipFilterAttribute != null) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

//		String redirectUrl = null;
//		String appSpecificCustomPageConfigKey = null;
//		String refererHeader = ((HttpServletRequest) servletRequest).getHeader(OmniConstants.REQUEST_PARAM_REFERRER);

//		String serviceProviderName = null;
//		if (servletRequest.getParameter(OmniConstants.REQUEST_PARAM_SP) != null) {
//			serviceProviderName = servletRequest.getParameter(OmniConstants.REQUEST_PARAM_SP);
//		} else if (servletRequest.getParameter(OmniConstants.REQUEST_PARAM_APPLICATION) != null) {
//			serviceProviderName = servletRequest.getParameter(OmniConstants.REQUEST_PARAM_APPLICATION);
//		} else if (refererHeader != null) {
//			String[] queryParams = refererHeader.split(OmniConstants.QUERY_SEPARATOR);
//			for (String queryParam : queryParams) {
//				if (queryParam.contains(OmniConstants.REQUEST_PARAM_SP + OmniConstants.EQUAL) || queryParam.contains(OmniConstants.REQUEST_PARAM_APPLICATION + OmniConstants.EQUAL)) {
//					serviceProviderName = queryParam.substring(queryParam.lastIndexOf(OmniConstants.EQUAL) + 1);
//					break;
//				}
//			}
//		}
		
//		log.debug("service provider name : {}", serviceProviderName);
		
		String relativePath = ((HttpServletRequest) servletRequest).getRequestURI().substring(((HttpServletRequest) servletRequest).getContextPath().length());
		
//		log.debug("relativePath : {}", relativePath);
		
//		if (StringUtils.hasText(serviceProviderName)) {
//			appSpecificCustomPageConfigKey = AuthenticationEndpointUtil.getApplicationSpecificCustomPageConfigKey(serviceProviderName, relativePath);
//		}

//		if (appSpecificCustomPageConfigKey != null) {
//			// Check for application specific custom page mappings matching the request uri.
//			redirectUrl = AuthenticationEndpointUtil.getCustomPageRedirectUrl(context.getInitParameter(appSpecificCustomPageConfigKey), ((HttpServletRequest) servletRequest).getQueryString());
//		}

//		if (redirectUrl == null) {
//			// No application specific custom page mappings.
//			// Check for global custom page mappings matching the request uri.
//			redirectUrl = AuthenticationEndpointUtil.getCustomPageRedirectUrl(context.getInitParameter(relativePath), ((HttpServletRequest) servletRequest).getQueryString());
//		}

//		if (redirectUrl != null) {
//			// There is a custom configuration matching the request uri. Redirect.
//			if (log.isDebugEnabled()) {
//				log.debug("There is a custom configuration matching the request uri. Redirecting to : " + redirectUrl);
//			}
//			((HttpServletResponse) servletResponse).sendRedirect(redirectUrl);
//			return;
//		}

//		log.debug("redirectUrl : {}", redirectUrl);
		
//		log.debug("request uri : {} = {}", ((HttpServletRequest) servletRequest).getRequestURI(), OmniConstants.URI_LOGIN);
		
		if (((HttpServletRequest) servletRequest).getRequestURI().contains(OmniConstants.URI_LOGIN)) {
			String hrdParam = servletRequest.getParameter(OmniConstants.REQUEST_PARAM_HRD);
			if (hrdParam != null && "true".equalsIgnoreCase(hrdParam)) {
				servletRequest.getRequestDispatcher("domain.jsp").forward(servletRequest, servletResponse);
				return;
			}

			Map<String, String> idpAuthenticatorMapping = new LinkedHashMap<>();
			String authenticators = servletRequest.getParameter(OmniConstants.REQUEST_PARAM_AUTHENTICATORS);
			if (authenticators != null) {
				String[] authenticatorIdPMappings = authenticators.split(";");
				for (String authenticatorIdPMapping : authenticatorIdPMappings) {
					String[] authenticatorIdPMapArr = authenticatorIdPMapping.split(":");
					for (int i = 1; i < authenticatorIdPMapArr.length; i++) {
						if (idpAuthenticatorMapping.containsKey(authenticatorIdPMapArr[i])) {
							idpAuthenticatorMapping.put(authenticatorIdPMapArr[i], idpAuthenticatorMapping.get(authenticatorIdPMapArr[i]) + "," + authenticatorIdPMapArr[0]);
						} else {
							idpAuthenticatorMapping.put(authenticatorIdPMapArr[i], authenticatorIdPMapArr[0]);
						}
					}
				}
			}
			
			if (!idpAuthenticatorMapping.isEmpty()) {
				// Map 형태는 정렬을 보장하지 않기 때문에 LinkedHashMap으로 정렬하도록 변경.
				// 확인결과) 제품에서 정렬하여 내려주지 않는 것으로 보임.
				// 관리자에서 authenticator 입력한 순서로 강재로 재정렬하기
				// config-static.xml 에 순서에 맞게 List 형태로 구성(common.sns.type)
				// SNS authenticator 가 추가될 경우 config-static.xml 에 정렬에 맞춰서 추가해야함.
				// 현재는 KA, AppleID, NA, FB, LOCAL 순으로 정렬
				idpAuthenticatorMapping = OmniUtil.getIdpAuthenticatorMappingSort(idpAuthenticatorMapping);
				
				servletRequest.setAttribute(OmniConstants.IDP_AUTHENTICATOR_MAP, idpAuthenticatorMapping);
			}

			String loadPage;
			String protocolType = servletRequest.getParameter(OmniConstants.REQUEST_PARAM_TYPE);
			//log.debug("▶▶▶▶▶▶ protocol type : {}", protocolType);
			if (OmniConstants.SAMLSSO.equals(protocolType)) {
				loadPage = OmniConstants.URI_SAMLSSO_LOGIN;
			} else if (OmniConstants.OPENID.equals(protocolType)) {
				loadPage = OmniConstants.URI_OPENID_LOGIN;
			} else if (OmniConstants.PASSIVESTS.equals(protocolType)) {
				loadPage = OmniConstants.URI_PASSIVESTS_LOGIN;
			} else if (OmniConstants.OAUTH2.equals(protocolType) || OmniConstants.OIDC.equals(protocolType)) {
				loadPage = relativePath; //URI_OAUTH2_LOGIN;
			} else if (OmniConstants.FIDO.equals(protocolType)) {
				loadPage = "fido-auth.jsp";
			} else {
				// loadPage = "login.jsp";
				loadPage = relativePath; //"/login";
//				log.debug("loadPage : {}", loadPage);
			}
//			log.debug("loadPage : {}", loadPage);
			// This is done to prevent the recursive dispatching of the filter
			servletRequest.setAttribute(OmniConstants.ATTRIBUTE_SKIP_PROPERTY, true);
			servletRequest.getRequestDispatcher(loadPage).forward(servletRequest, servletResponse);
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
		}

	}

}
