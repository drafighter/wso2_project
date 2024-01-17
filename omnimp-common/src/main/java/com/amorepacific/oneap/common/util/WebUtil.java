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
 * Date   	          : 2020. 7. 15..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.WebUtils;

import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.OfflineParam;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.Types;
import com.amorepacific.oneap.common.vo.UserAgentAccessInfo;
import com.google.gson.JsonObject;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ua_parser.Client;
import ua_parser.Parser;

/**
 * <pre>
 * com.amorepacific.oneap.common.util 
 *    |_ WebUtil.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 15.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@UtilityClass
public class WebUtil {
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 13. 오후 1:43:28
	 * </pre>
	 * 
	 * @param dt
	 * @param op
	 * @return
	 */
	public Types getTypes(final String dt, final String op) {
		Types types = new Types();
		if (StringUtils.hasText(op)) {
			if ("W".equals(op)) {
				types.setOsType("04");
			} else if ("M".equals(op)) {
				types.setOsType("03");
			} else if ("A".equals(op)) {
				types.setOsType("01");
			} else if ("I".equals(op)) {
				types.setOsType("02");
			}
		} else {
			UserAgentAccessInfo ac = getUserAgentAccessInfo();
			String osType = ac.getOsName().toUpperCase();
			if (osType.contains("WINDOWS")) {
				types.setOsType("04");
			} else if (osType.contains("ANDROID")) {
				types.setOsType("01");
			} else if (osType.contains("MAC")) {
				types.setOsType("03");
			} else if (osType.contains("IOS")) {
				types.setOsType("02");
			}
		}

		if (StringUtils.hasText(dt)) {
			types.setDeviceType(dt);
		} else {
			boolean mobile = isMobile();
			if (mobile) {
				types.setDeviceType("M");
			} else {
				types.setDeviceType("W");
			}
		}
		return types;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : User Agent 에서 엑세스 정보 얻어옴
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 10:23:57
	 * </pre>
	 * 
	 * @param userAgent
	 * @return
	 * @throws IOException
	 */
	public UserAgentAccessInfo getUserAgentAccessInfo(final String userAgent) {
		final String useragentstr = userAgent;
		UserAgentAccessInfo userAgentAccessInfo = new UserAgentAccessInfo();
		if (StringUtils.hasText(useragentstr)) {
			try {
				Parser parser = new Parser();
				Client client = parser.parse(useragentstr);
				userAgentAccessInfo.setBrowserName(client.userAgent.family);
				userAgentAccessInfo.setBrowserMajor(client.userAgent.major);
				userAgentAccessInfo.setBrowserMinor(client.userAgent.minor);
				userAgentAccessInfo.setBrowserPatch(client.userAgent.patch);
				userAgentAccessInfo.setOsName(client.os.family);
				userAgentAccessInfo.setOsMajor(client.os.major);
				userAgentAccessInfo.setOsMinor(client.os.minor);
				userAgentAccessInfo.setOsPatch(client.os.patch);
				userAgentAccessInfo.setOsPatchMinor(client.os.patchMinor);
				userAgentAccessInfo.setDeviceName(client.device.family);
			} catch (IOException e) {
			}
			return userAgentAccessInfo;
		}
		return null;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : User Agent 에서 엑세스 정보 얻어옴
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 10:24:18
	 * </pre>
	 * 
	 * @return
	 * @throws IOException
	 */
	public UserAgentAccessInfo getUserAgentAccessInfo() {
		HttpServletRequest request = getRequest(); // ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return getUserAgentAccessInfo(request);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 4. 오전 11:45:58
	 * </pre>
	 * 
	 * @return
	 */
	public HttpServletRequest getRequest() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			ServletRequestAttributes requestattributes = (ServletRequestAttributes) attributes;
			if (requestattributes != null) {
				return requestattributes.getRequest();
			}
		}
		return null;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 4. 오후 7:23:31
	 * </pre>
	 * 
	 * @return
	 */
	public HttpServletResponse getResponse() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			ServletRequestAttributes requestattributes = (ServletRequestAttributes) attributes;
			if (requestattributes != null) {
				return requestattributes.getResponse();
			}
		}
		return null;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : User Agent 에서 엑세스 정보 얻어옴
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 10:24:23
	 * </pre>
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public UserAgentAccessInfo getUserAgentAccessInfo(final HttpServletRequest request) {
		final String userAgent = request.getHeader("user-agent");
		return getUserAgentAccessInfo(userAgent);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : User Agent 에서 모바일인지 체크하기
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 10:24:53
	 * </pre>
	 * 
	 * @param request
	 * @return
	 */
	public boolean isMobile(final HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		boolean Mobile1 = userAgent.matches(".*(Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson).*");
		boolean Mobile2 = userAgent.matches(".*(LG|SAMSUNG|Samsung).*");
		if (Mobile1 || Mobile2) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 22. 오후 1:40:51
	 * </pre>
	 * 
	 * @param userAgent
	 * @return
	 */
	public boolean isMobile(final String userAgent) {
		boolean Mobile1 = userAgent.matches(".*(Mobile|iP(hone|od)|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|IEMobile|Kindle|NetFront|Silk-Accelerated|(hpw|web)OS|Fennec|Minimo|Opera M(obi|ini)|Blazer|Dolfin|Dolphin|Skyfire|Zune|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson).*");
		boolean Mobile2 = userAgent.matches(".*(LG|SAMSUNG|Samsung).*");
		if (Mobile1 || Mobile2) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 22. 오후 1:37:08
	 * </pre>
	 * 
	 * @return
	 */
	public boolean isMobile() {
		final HttpServletRequest request = getRequest();
		if(request == null) return false;
		return isMobile(request);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : Client IP 를 가져오기 위한 method
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:27:53
	 * </pre>
	 * 
	 * @return
	 */
	public String getClientIp() {
		HttpServletRequest request = getRequest(); // ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return getClientIp(request);
	}

	/**
	 * 
	 * <pre>
	 * commnet  : Client IP 를 가져오기 위한 method
	 * author   : takkies
	 * date     : 2020. 7. 15. 오전 9:26:24
	 * </pre>
	 * 
	 * @param request
	 * @return
	 */
	public String getClientIp(final HttpServletRequest request) {
		String clientIp = request.getHeader("WL-Proxy-Client-IP");
		if (StringUtils.isEmpty(clientIp)) {
			clientIp = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(clientIp)) {
			clientIp = request.getHeader("X-Forwarded-For");
		}
		if (StringUtils.isEmpty(clientIp)) {
			clientIp = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StringUtils.isEmpty(clientIp)) {
			clientIp = request.getHeader("X-Real-IP");
		}
		if (StringUtils.isEmpty(clientIp)) {
			clientIp = request.getRemoteAddr();
		}

		if (clientIp.contains(",")) {
			clientIp = clientIp.substring(0, clientIp.indexOf(","));
		}

		return clientIp;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 2. 오전 8:27:58
	 * </pre>
	 * 
	 * @param redirectUrl
	 * @return
	 */
	public String getRedirectUrlWithContextPath(final String redirectUrl) {
		// return "redirect:".concat(getContextPath()).concat(redirectUrl);
		return "redirect:" + redirectUrl;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 2. 오전 8:26:04
	 * </pre>
	 * 
	 * @return
	 */
	public String getContextPath() {
		final HttpServletRequest request = getRequest();
		return getContextPath(request);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 2. 오전 8:26:00
	 * </pre>
	 * 
	 * @param request
	 * @return
	 */
	public String getContextPath(final HttpServletRequest request) {
		return request.getContextPath();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 5. 오후 6:01:51
	 * </pre>
	 * 
	 * @param name
	 * @param value
	 */
	public void setSession(final String name, final Object value) {
		HttpServletRequest request = getRequest(); // ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		setSession(request, name, value);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 5. 오후 6:01:47
	 * </pre>
	 * 
	 * @param request
	 * @param name
	 * @param value
	 */
	public void setSession(final HttpServletRequest request, final String name, final Object value) {
		WebUtils.setSessionAttribute(request, name, value);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 13. 오후 3:03:54
	 * </pre>
	 * 
	 * @param name
	 */
	public void removeSession(final String name) {
		HttpServletRequest request = getRequest(); // ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		removeSession(request, name);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 13. 오후 3:03:13
	 * </pre>
	 * 
	 * @param request
	 * @param name
	 */
	public void removeSession(final HttpServletRequest request, final String name) {
		setSession(request, name, null);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 12. 오후 2:57:58
	 * </pre>
	 * 
	 * @param name
	 * @return
	 */
	public String getStringSession(final String name) {
		HttpServletRequest request = getRequest(); // ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return getStringSession(request, name);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 12. 오후 2:59:22
	 * </pre>
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public String getStringSession(final HttpServletRequest request, final String name) {
		Object obj = getSession(request, name);
		if (obj == null) {
			return "";
		}
		if (obj instanceof Integer) {
			return Integer.toString((Integer) obj);
		} else {
			return obj.toString();
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 5. 오후 6:00:53
	 * </pre>
	 * 
	 * @param name
	 * @return
	 */
	public Object getSession(final String name) {
		HttpServletRequest request = getRequest(); // ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return getSession(request, name);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 5. 오후 5:46:45
	 * </pre>
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public Object getSession(final HttpServletRequest request, final String name) {
		return WebUtils.getSessionAttribute(request, name);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 7. 31. 오후 4:34:39
	 * </pre>
	 * 
	 * @param name
	 * @return
	 */
	public String getStringParameter(final String name) {
		return getStringParameter(name, "");
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 7. 31. 오후 4:34:43
	 * </pre>
	 * 
	 * @param name
	 * @param defaultvalue
	 * @return
	 */
	public String getStringParameter(final String name, final String defaultvalue) {
		HttpServletRequest request = getRequest(); // ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return getStringParameter(request, name, defaultvalue);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 7. 31. 오후 4:34:46
	 * </pre>
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public String getStringParameter(final HttpServletRequest request, final String name) {
		return getStringParameter(request, name, "");
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 7. 31. 오후 4:34:50
	 * </pre>
	 * 
	 * @param request
	 * @param name
	 * @param defalutvalue
	 * @return
	 */
	public String getStringParameter(final HttpServletRequest request, final String name, final String defalutvalue) {
		return ServletRequestUtils.getStringParameter(request, name, defalutvalue).trim();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 헤더에서 연결된 이전 URL 정보 조회
	 * author   : takkies
	 * date     : 2020. 7. 28. 오전 11:56:26
	 * </pre>
	 * 
	 * @param request
	 * @return
	 */
	public String getReferer(final HttpServletRequest request) {
		return getHeader(request, "referer");
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 7. 31. 오후 1:32:47
	 * </pre>
	 * 
	 * @return
	 */
	public String getReferer() {
		return getHeader("referer");
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 7. 31. 오후 1:32:51
	 * </pre>
	 * 
	 * @param request
	 * @param headername
	 * @return
	 */
	public String getHeader(final HttpServletRequest request, final String headername) {
		return request.getHeader(headername);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 7. 31. 오후 1:32:55
	 * </pre>
	 * 
	 * @param headername
	 * @return
	 */
	public String getHeader(final String headername) {
		HttpServletRequest request = getRequest(); // ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return getHeader(request, headername);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 현재 URL을 쿼리스트링까지 포함해서 생성
	 * author   : takkies
	 * date     : 2020. 7. 28. 오전 11:56:30
	 * </pre>
	 * 
	 * @param request
	 * @return
	 */
	public String getCurrentUrl(final HttpServletRequest request) {
		return getCurrentUrl(request, false);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 현재 URL을 쿼리스트링까지 포함해서 생성, 인코딩 필요시 URL을 인코딩함.
	 * author   : takkies
	 * date     : 2020. 7. 28. 오후 5:06:50
	 * </pre>
	 * 
	 * @param request
	 * @param encode
	 * @return
	 */
	public String getCurrentUrl(final HttpServletRequest request, final boolean encode) {
		final StringBuffer url = request.getRequestURL();
		final String query = request.getQueryString();
		String currenturl;
		if (StringUtils.hasText(query)) {
			currenturl = url.append("?").append(query).toString();
		} else {
			currenturl = url.toString();
		}
		if (encode) {
			try {
				return URLEncoder.encode(currenturl, StandardCharsets.UTF_8.name());
			} catch (UnsupportedEncodingException e) {
				return currenturl;
			}
		} else {
			return currenturl;
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 12. 오전 10:56:20
	 * </pre>
	 * 
	 * @return
	 */
	public HashMap<String, Object> convertRequestToMap() {
		final HttpServletRequest request = getRequest();
		return convertRequestToMap(request);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 7. 30. 오전 11:34:40
	 * </pre>
	 * 
	 * @param request
	 * @return
	 */
	public HashMap<String, Object> convertRequestToMap(final HttpServletRequest request) {
		HashMap<String, Object> hmap = new HashMap<String, Object>();
		String key;
		Enumeration<?> enums = request.getParameterNames();
		while (enums.hasMoreElements()) {
			key = (String) enums.nextElement();
			if (request.getParameterValues(key).length > 1) {
				hmap.put(key, request.getParameterValues(key));
			} else {
				hmap.put(key, request.getParameter(key));
			}
		}
		return hmap;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 20. 오전 8:32:15
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @param cookeName
	 */
	public void removeCookie(final HttpServletResponse response, final String cookeName) {

		Cookie ck = new Cookie(cookeName, null);
		ck.setMaxAge(0); // 유효시간을 0으로 설정
		ck.setPath("/");
		ck.setValue("");
		response.addCookie(ck); // 응답 헤더에 추가해서 없어지도록 함
	}

	public void removeCookie(final HttpServletResponse response, final String cookeName, final String path) {

		Cookie ck = new Cookie(cookeName, null);
		ck.setMaxAge(0); // 유효시간을 0으로 설정
		if (StringUtils.hasText(path)) {
			ck.setPath(path);
		} else {
			ck.setPath("/");
		}
		ck.setValue("");
		response.addCookie(ck); // 응답 헤더에 추가해서 없어지도록 함
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 7. 29. 오후 7:23:21
	 * </pre>
	 * 
	 * @param response
	 * @param cookeName
	 * @param cookeValue
	 */
	public void setCookies(final HttpServletResponse response, final String cookeName, final String cookeValue, final String domainName) {
		setCookies(response, cookeName, cookeValue, domainName, "/");
	}

	public void setCookies(final HttpServletResponse response, final String cookeName, final String cookeValue, final String domainName, final String path) {
		Cookie cookie = new Cookie(cookeName, cookeValue);
		if (StringUtils.hasText(domainName)) {
			cookie.setDomain(domainName);
		}
		if (StringUtils.hasText(path)) {
			cookie.setPath(path);
		} else {
			cookie.setPath("/");
		}
		cookie.setMaxAge(60 * 60 * 24 * 30);
		cookie.setSecure(false); // 클라이언트에도 저장되도록 false
		cookie.setHttpOnly(false); // 클라이언트에도 저장되도록 false

		// add cookie to response
		response.addCookie(cookie);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 7. 29. 오후 7:33:58
	 * </pre>
	 * 
	 * @param response
	 * @param cookeName
	 * @param cookeValue
	 */
	public void setCookies(final HttpServletResponse response, final String cookeName, final String cookeValue) {
		setCookies(response, cookeName, cookeValue, null);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 7. 29. 오후 7:23:25
	 * </pre>
	 * 
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public Cookie getCookies(final HttpServletRequest request, final String cookieName) {
		return WebUtils.getCookie(request, cookieName);
	}
	
	public void setLogoutUriCookie(final String profile, final HttpServletResponse response, final String cookeValue, final String domainName) {
		String cookieName = "prod".equals(profile) ? OmniConstants.LOGOUT_REDIRECT_URI_COOKIE : profile.concat("-").concat(OmniConstants.LOGOUT_REDIRECT_URI_COOKIE);
		String urlEncValue = cookeValue;
		try {
			urlEncValue = URLEncoder.encode(urlEncValue, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Cookie cookie = new Cookie(cookieName, urlEncValue);
		if (StringUtils.hasText(domainName)) {
			cookie.setDomain(domainName);
		}
		cookie.setPath("/");
		cookie.setMaxAge(60 * 10);
		cookie.setSecure(false); // 클라이언트에도 저장되도록 false
		cookie.setHttpOnly(false); // 클라이언트에도 저장되도록 false

		// add cookie to response
		response.addCookie(cookie);
	}
	
	public Cookie getLogoutUriCookie(final String profile, final HttpServletRequest request) {
		String cookieName = "prod".equals(profile) ? OmniConstants.LOGOUT_REDIRECT_URI_COOKIE : profile.concat("-").concat(OmniConstants.LOGOUT_REDIRECT_URI_COOKIE);
		
		return WebUtils.getCookie(request, cookieName);
	}
	
	public void removeLogoutUriCookie(final String profile, final HttpServletResponse response) {
		String cookieName = "prod".equals(profile) ? OmniConstants.LOGOUT_REDIRECT_URI_COOKIE : profile.concat("-").concat(OmniConstants.LOGOUT_REDIRECT_URI_COOKIE);
		
		removeCookie(response, cookieName);
	}

	/**
	 * 
	 * <pre>
	 * comment  : SSO 파라미터 조합하기
	 * author   : takkies
	 * date     : 2020. 9. 7. 오전 9:38:29
	 * </pre>
	 * 
	 * @return SSO 파라미터
	 */
	public String getSsoParams() {
		Object obj = WebUtil.getSession(OmniConstants.SSOPARAM);
		if (obj != null) {
			SSOParam ssoParam = (SSOParam) obj;
			return getSsoParams(ssoParam);
		}
		return "";
	}

	/**
	 * 
	 * <pre>
	 * comment  : SSO 파라미터 조합하기
	 * 각 파라미터 중에 인코딩대상에 대해서 개별적으로 try catch 하여
	 * 전체 데이터가 유실되는 경우 방지
	 * 
	 * author   : takkies
	 * date     : 2020. 10. 20. 오후 2:20:55
	 * </pre>
	 * 
	 * @param ssoParam
	 * @return SSO 파라미터
	 */
	public String getSsoParams(SSOParam ssoParam) {
		if (ssoParam != null) {
			StringBuilder s = new StringBuilder();
			s.append("channelCd=").append(SecurityUtil.clearXSSNormal(ssoParam.getChannelCd())); // channelCd
			s.append("&client_id=").append(SecurityUtil.clearXSSNormal(ssoParam.getClient_id())); // client_id
			if (StringUtils.hasText(ssoParam.getCommonAuthCallerPath())) {
				try {
					s.append("&commonAuthCallerPath=").append(URLEncoder.encode(ssoParam.getCommonAuthCallerPath(), StandardCharsets.UTF_8.name())); //
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}

			}
			s.append("&forceAuth=").append(SecurityUtil.clearXSSNormal(ssoParam.getForceAuth())); // forceAuth
			s.append("&passiveAuth=").append(SecurityUtil.clearXSSNormal(ssoParam.getPassiveAuth())); // passiveAuth
			if (StringUtils.hasText(ssoParam.getRedirectUri())) {
				try {
					s.append("&redirectUri=").append(URLEncoder.encode(ssoParam.getRedirectUri(), StandardCharsets.UTF_8.name())); // redirectUri
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			if (StringUtils.hasText(ssoParam.getCancelUri())) {
				try {
					s.append("&cancelUri=").append(URLEncoder.encode(ssoParam.getCancelUri(), StandardCharsets.UTF_8.name())); // cancelUri
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			if (StringUtils.hasText(ssoParam.getPopup())) {
				try {
					s.append("&popup=").append(URLEncoder.encode(ssoParam.getPopup(), StandardCharsets.UTF_8.name())); // popup
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}			
			if (StringUtils.hasText(ssoParam.getRedirect_uri())) {
				try {
					s.append("&redirect_uri=").append(URLEncoder.encode(ssoParam.getRedirect_uri(), StandardCharsets.UTF_8.name())); // redirect_uri
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			s.append("&response_type=").append(SecurityUtil.clearXSSNormal(ssoParam.getResponse_type())); // response_type
			s.append("&scope=").append(SecurityUtil.clearXSSNormal(ssoParam.getScope())); // scope
			if (StringUtils.hasText(ssoParam.getState())) {
				try {
					s.append("&state=").append(URLEncoder.encode(ssoParam.getState(), StandardCharsets.UTF_8.name()));
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			s.append("&tenantDomain=").append(SecurityUtil.clearXSSNormal(ssoParam.getTenantDomain())); // tenantDomain
			s.append("&sessionDataKey=").append(SecurityUtil.clearXSSNormal(ssoParam.getSessionDataKey())); // sessionDataKey
			s.append("&relyingParty=").append(SecurityUtil.clearXSSNormal(ssoParam.getRelyingParty())); // relyingParty
			s.append("&type=").append(SecurityUtil.clearXSSNormal(ssoParam.getType())); // type
			s.append("&sp=").append(SecurityUtil.clearXSSNormal(ssoParam.getSp())); // sp
			s.append("&isSaaSApp=").append(SecurityUtil.clearXSSNormal(ssoParam.getIsSaaSApp())); // isSaaSApp
			if (StringUtils.hasText(ssoParam.getAuthenticators())) {
				try {
					s.append("&authenticators=").append(URLEncoder.encode(ssoParam.getAuthenticators(), StandardCharsets.UTF_8.name())); // authenticators
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			if (StringUtils.hasText(ssoParam.getDt())) {
				s.append("&dt=").append(SecurityUtil.clearXSSNormal(ssoParam.getDt()));
			}
			if (StringUtils.hasText(ssoParam.getHh())) {
				s.append("&hh=").append(SecurityUtil.clearXSSNormal(ssoParam.getHh()));
			}
			if (StringUtils.hasText(ssoParam.getOt())) {
				s.append("&ot=").append(SecurityUtil.clearXSSNormal(ssoParam.getOt()));
			}
			s.append("&join=").append(SecurityUtil.clearXSSNormal(ssoParam.getJoin())); // join

			return s.toString();
		}

		return "";
	}

	/**
	 * 
	 * <pre>
	 * comment  : sso param 통짜로 URL 인코딩 
	 * author   : hkdang
	 * date     : 2020. 9. 21. 오후 5:55:28
	 * </pre>
	 * 
	 * @return 인코딩된 SSO 전체 파라미터
	 */
	public String getUrlEncodedSsoParams() {
		Object obj = WebUtil.getSession(OmniConstants.SSOPARAM);
		if (obj != null) {
			SSOParam ssoParam = (SSOParam) obj;
			if (ssoParam != null) {
				try {
					StringBuilder s = new StringBuilder();
					s.append("channelCd=").append(ssoParam.getChannelCd()); // channelCd
					s.append("&client_id=").append(ssoParam.getClient_id()); // client_id
					s.append("&commonAuthCallerPath=").append(ssoParam.getCommonAuthCallerPath()); // commonAuthCallerPath
					s.append("&forceAuth=").append(ssoParam.getForceAuth()); // forceAuth
					s.append("&passiveAuth=").append(ssoParam.getPassiveAuth()); // passiveAuth
					s.append("&redirectUri=").append(ssoParam.getRedirectUri()); // redirectUri
					if (StringUtils.hasText(ssoParam.getCancelUri())) {
						s.append("&cancelUri=").append(ssoParam.getCancelUri()); // cancelUri
					}
					if (StringUtils.hasText(ssoParam.getPopup())) {
						s.append("&popup=").append(ssoParam.getPopup()); // popup
					}					
					s.append("&redirect_uri=").append(ssoParam.getRedirect_uri()); // redirect_uri
					s.append("&response_type=").append(ssoParam.getResponse_type()); // response_type
					s.append("&scope=").append(ssoParam.getScope()); // scope
					s.append("&state=").append(ssoParam.getState());
					s.append("&tenantDomain=").append(ssoParam.getTenantDomain()); // tenantDomain
					s.append("&sessionDataKey=").append(ssoParam.getSessionDataKey()); // sessionDataKey
					s.append("&relyingParty=").append(ssoParam.getRelyingParty()); // relyingParty
					s.append("&type=").append(ssoParam.getType()); // type
					s.append("&sp=").append(ssoParam.getSp()); // sp
					s.append("&isSaaSApp=").append(ssoParam.getIsSaaSApp()); // isSaaSApp
					s.append("&authenticators=").append(ssoParam.getAuthenticators()); // authenticators
					if (StringUtils.hasText(ssoParam.getDt())) {
						s.append("&dt=").append(ssoParam.getDt());
					}
					if (StringUtils.hasText(ssoParam.getHh())) {
						s.append("&hh=").append(ssoParam.getHh());
					}
					if (StringUtils.hasText(ssoParam.getOt())) {
						s.append("&ot=").append(ssoParam.getOt());
					}
					s.append("&join=").append(ssoParam.getJoin()); // join

					return URLEncoder.encode(s.toString(), StandardCharsets.UTF_8.name());
				} catch (UnsupportedEncodingException e) {
					log.error(e.getMessage());
					return "";
				}
			}
		}
		return "";
	}

	/**
	 * 
	 * <pre>
	 * comment  : 인증 SSO 파라미터 조합하기
	 * author   : takkies
	 * date     : 2020. 9. 9. 오후 12:24:10
	 * </pre>
	 * 
	 * @return SSO 파라미터
	 */
	public String getSsoParamsAuth() {
		Object obj = WebUtil.getSession(OmniConstants.SSOPARAM);
		if (obj != null) {
			SSOParam ssoParam = (SSOParam) obj;
			return getSsoParamsAuth(ssoParam);
		}
		return "";
	}

	/**
	 * 
	 * <pre>
	 * comment  : 인증 SSO 파라미터 조합하기
	 * 각 파라미터 중에 인코딩대상에 대해서 개별적으로 try catch 하여
	 * 전체 데이터가 유실되는 경우 방지 
	 * 
	 * author   : takkies
	 * date     : 2020. 10. 20. 오후 2:20:48
	 * </pre>
	 * 
	 * @param ssoParam
	 * @return SSO 파라미터
	 */
	public String getSsoParamsAuth(SSOParam ssoParam) {

		if (ssoParam != null) {
			StringBuilder s = new StringBuilder();
			s.append("channelCd=").append(SecurityUtil.clearXSSNormal(ssoParam.getChannelCd())); // channelCd
			s.append("&client_id=").append(SecurityUtil.clearXSSNormal(ssoParam.getClient_id())); // client_id
			if (StringUtils.hasText(ssoParam.getRedirectUri())) {
				try {
					s.append("&redirectUri=").append(URLEncoder.encode(ssoParam.getRedirectUri(), StandardCharsets.UTF_8.name())); // redirectUri
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			if (StringUtils.hasText(ssoParam.getCancelUri())) {
				try {
					s.append("&cancelUri=").append(URLEncoder.encode(ssoParam.getCancelUri(), StandardCharsets.UTF_8.name())); // cancelUri
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			if (StringUtils.hasText(ssoParam.getPopup())) {
				try {
					s.append("&popup=").append(URLEncoder.encode(ssoParam.getPopup(), StandardCharsets.UTF_8.name())); // popup
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}			
			if (StringUtils.hasText(ssoParam.getRedirect_uri())) {
				try {
					s.append("&redirect_uri=").append(URLEncoder.encode(ssoParam.getRedirect_uri(), StandardCharsets.UTF_8.name())); // redirect_uri
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			s.append("&response_type=").append(SecurityUtil.clearXSSNormal(ssoParam.getResponse_type())); // response_type
			s.append("&scope=").append(SecurityUtil.clearXSSNormal(ssoParam.getScope())); // scope
			if (StringUtils.hasText(ssoParam.getState())) {
				try {
					s.append("&state=").append(URLEncoder.encode(ssoParam.getState(), StandardCharsets.UTF_8.name()));
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			s.append("&type=").append(SecurityUtil.clearXSSNormal(ssoParam.getType())); // type
			if (StringUtils.hasText(ssoParam.getDt())) {
				s.append("&dt=").append(SecurityUtil.clearXSSNormal(ssoParam.getDt()));
			}
			if (StringUtils.hasText(ssoParam.getHh())) {
				s.append("&hh=").append(SecurityUtil.clearXSSNormal(ssoParam.getHh()));
			}
			if (StringUtils.hasText(ssoParam.getOt())) {
				s.append("&ot=").append(SecurityUtil.clearXSSNormal(ssoParam.getOt()));
			}
			s.append("&join=").append(SecurityUtil.clearXSSNormal(ssoParam.getJoin())); // join
			
			boolean autologin = "Y".equals(WebUtil.getStringSession(OmniConstants.AUTO_LOGIN_SESSION));
			if (autologin) {
				s.append("&chkRemember=on");
			}
			if (StringUtils.hasText(ssoParam.getDrcLgnTp())) {
				s.append("&drcLgnTp=").append(SecurityUtil.clearXSSNormal(ssoParam.getDrcLgnTp()));
			}
			
			return s.toString();
		}
		return "";
	}

	/**
	 * 
	 * <pre>
	 * comment  : 로그인 SSO 파라미터 조합하기
	 * author   : takkies
	 * date     : 2020. 9. 10. 오후 5:28:52
	 * </pre>
	 * 
	 * @return SSO 파라미터
	 */
	public String getLoginSsoParamsAuth() {
		Object obj = WebUtil.getSession(OmniConstants.SSOPARAM);
		if (obj != null) {
			SSOParam ssoParam = (SSOParam) obj;
			return getLoginSsoParamsAuth(ssoParam);
		}
		return "";
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 11. 오후 8:17:07
	 * </pre>
	 * 
	 * @param offlineParam
	 * @return
	 */
	public String getOfflineParam(final OfflineParam offlineParam) {
		if (offlineParam != null) {
			StringBuilder param = new StringBuilder();
			param.append("addInfo=").append(offlineParam.getAddInfo());
			param.append("&chCd=").append(offlineParam.getChCd());
			param.append("&chnCd=").append(offlineParam.getChnCd());
			if (StringUtils.hasText(offlineParam.getDt())) {
				param.append("&dt=").append(offlineParam.getDt());
			}
			if (StringUtils.hasText(offlineParam.getDt())) {
				param.append("&hh=").append(offlineParam.getDt());
			}
			if (StringUtils.hasText(offlineParam.getOp())) {
				param.append("&op=").append(offlineParam.getOp());
			}
			param.append("&joinPrtnId=").append(offlineParam.getJoinPrtnId());
			if (StringUtils.hasText(offlineParam.getJoinPrtnNm())) {
				try {
					param.append("&joinPrtnNm=").append(URLEncoder.encode(offlineParam.getJoinPrtnNm(), StandardCharsets.UTF_8.name()));
				} catch (UnsupportedEncodingException e) {
				}
			}
			if (StringUtils.hasText(offlineParam.getJoinEmpId())) { // 2021-05-03 이크리스 추가 파라미터
				try {
					param.append("&joinEmpId=").append(URLEncoder.encode(offlineParam.getJoinEmpId(), StandardCharsets.UTF_8.name()));
				} catch (UnsupportedEncodingException e) {
				}
			}
			if (StringUtils.hasText(offlineParam.getReturnUrl())) {
				try {
					param.append("&returnUrl=").append(URLEncoder.encode(offlineParam.getReturnUrl(), StandardCharsets.UTF_8.name()));
				} catch (UnsupportedEncodingException e) {
				}
			}
			if (StringUtils.hasText(offlineParam.getCancelUrl())) {
				try {
					param.append("&cancelUrl=").append(URLEncoder.encode(offlineParam.getCancelUrl(), StandardCharsets.UTF_8.name()));
				} catch (UnsupportedEncodingException e) {
				}
			}
			param.append("&storeCd=").append(offlineParam.getStoreCd());
			if (StringUtils.hasText(offlineParam.getStorenm())) {
				try {
					param.append("&storenm=").append(URLEncoder.encode(offlineParam.getStorenm(), StandardCharsets.UTF_8.name()));
				} catch (UnsupportedEncodingException e) {
				}
			}
			param.append("&user_id=").append(offlineParam.getUser_id());
			return param.toString();
		}
		return "";
	}

	/**
	 * 
	 * <pre>
	 * comment  : 로그인 SSO 파라미터 조합하기
	 * 각 파라미터 중에 인코딩대상에 대해서 개별적으로 try catch 하여
	 * 전체 데이터가 유실되는 경우 방지
	 * author   : takkies
	 * date     : 2020. 10. 20. 오후 2:20:43
	 * </pre>
	 * 
	 * @param ssoParam
	 * @return SSO 파라미터
	 */
	public String getLoginSsoParamsAuth(SSOParam ssoParam) {
		if (ssoParam != null) {
			StringBuilder s = new StringBuilder();
			s.append("channelCd=").append(SecurityUtil.clearXSSNormal(ssoParam.getChannelCd())); // channelCd
			s.append("&client_id=").append(SecurityUtil.clearXSSNormal(ssoParam.getClient_id())); // client_id
			if (StringUtils.hasText(ssoParam.getRedirectUri())) {
				try {
					s.append("&redirectUri=").append(URLEncoder.encode(ssoParam.getRedirectUri(), StandardCharsets.UTF_8.name())); // redirectUri
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			if (StringUtils.hasText(ssoParam.getCancelUri())) {
				try {
					s.append("&cancelUri=").append(URLEncoder.encode(ssoParam.getCancelUri(), StandardCharsets.UTF_8.name())); // cancelUri
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			if (StringUtils.hasText(ssoParam.getPopup())) {
				try {
					s.append("&popup=").append(URLEncoder.encode(ssoParam.getPopup(), StandardCharsets.UTF_8.name())); // popup
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}			
			if (StringUtils.hasText(ssoParam.getRedirect_uri())) {
				try {
					s.append("&redirect_uri=").append(URLEncoder.encode(ssoParam.getRedirect_uri(), StandardCharsets.UTF_8.name())); // redirect_uri
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			s.append("&response_type=").append(SecurityUtil.clearXSSNormal(ssoParam.getResponse_type())); // response_type
			s.append("&scope=").append(SecurityUtil.clearXSSNormal(ssoParam.getScope())); // scope
			if (StringUtils.hasText(ssoParam.getState())) {
				try {
					// State 에서 redirectUri 비교 후 업데이트
					s.append("&state=").append(URLEncoder.encode(OmniUtil.getStateParamFromSSOParam(), StandardCharsets.UTF_8.name()));
				} catch (UnsupportedEncodingException e) {
					// NO PMD
				}
			}
			s.append("&type=").append(SecurityUtil.clearXSSNormal(ssoParam.getType())); // type
			if (StringUtils.hasText(ssoParam.getDt())) {
				s.append("&dt=").append(SecurityUtil.clearXSSNormal(ssoParam.getDt()));
			}
			if (StringUtils.hasText(ssoParam.getHh())) {
				s.append("&hh=").append(SecurityUtil.clearXSSNormal(ssoParam.getHh()));
			}
			if (StringUtils.hasText(ssoParam.getOt())) {
				s.append("&ot=").append(SecurityUtil.clearXSSNormal(ssoParam.getOt()));
			}
			if (StringUtils.hasText(ssoParam.getVt())) {
				s.append("&vt=").append(SecurityUtil.clearXSSNormal(ssoParam.getVt()));
			}			
			// s.append("&join=").append(ssoParam.getJoin()); // join
			return s.toString();
		}
		return "";
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 18. 오전 8:56:26
	 * </pre>
	 * 
	 * @param httpUrl
	 * @return
	 */
	public String encodeUriComponent(final String httpUrl) {
		return UriComponentsBuilder.fromUriString(httpUrl).build().encode(StandardCharsets.UTF_8).toString();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 24. 오전 9:07:06
	 * </pre>
	 * 
	 * @return
	 */
	public String getCtx() {
		final HttpServletRequest request = getRequest();
		return request.getContextPath();
	}
	
	public void setSSOParam(JsonObject stateParam) {
		SSOParam ssoParam = new SSOParam();
		
		ssoParam.setChannelCd(stateParam.get("channelCd").getAsString());
		WebUtil.setSession(OmniConstants.CH_CD_SESSION, stateParam.get("channelCd").getAsString());
		
		ssoParam.setClient_id(stateParam.get("client_id").getAsString());
		ssoParam.setDrcLgnTp(stateParam.get("drcLgnTp").getAsString());
		ssoParam.setScope(OmniConstants.OPENID);
		ssoParam.setResponse_type(OmniConstants.CODE);
		ssoParam.setType(stateParam.get("type").getAsString());
		ssoParam.setSp(stateParam.get("sp").getAsString());
		ssoParam.setRedirect_uri(stateParam.get("redirect_uri").getAsString());
		
		Map<String, Object> stateMap = new HashMap<String, Object>();
		
		stateMap.put("redirectUri", stateParam.get("redirectUri").getAsString());
		ssoParam.setRedirectUri(stateParam.get("redirectUri").getAsString());
		
		stateMap.put("cancelUri", stateParam.get("cancelUri").getAsString());
		ssoParam.setCancelUri(stateParam.get("cancelUri").getAsString());
		
		stateMap.put("join", "");
		stateMap.put("prompt", "");
		stateMap.put("popup", "");
		stateMap.put("gid", "");
		stateMap.put("cid", "");
		stateMap.put("ot", "");
		stateMap.put("vt", "");
		stateMap.put("channelCd", stateParam.get("channelCd").getAsString());
		
		ssoParam.setState(stateMap.toString());
		
		WebUtil.setSession(OmniConstants.SSOPARAM, ssoParam);
		log.info("▶▶▶▶▶▶▶▶▶▶ ssoParam : {}", StringUtil.printJson(ssoParam));
	}

	public boolean isAndroidApp() {
		final HttpServletRequest request = getRequest();
		if(request == null) return false;
		String userAgent = request.getHeader("user-agent");
		boolean AndroidApp = userAgent.matches(".*(APTRACK_ANDROID).*");
		if (AndroidApp) {
			return true;
		}
		return false;
	}
}
