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
 * Date   	          : 2020. 9. 3..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.util;

import static org.apache.commons.lang3.StringUtils.rightPad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.amorepacific.oneap.common.check.CheckResponse;
import com.amorepacific.oneap.common.check.Checker;
import com.amorepacific.oneap.common.check.actor.CheckActor;
import com.amorepacific.oneap.common.mask.Masker;
import com.amorepacific.oneap.common.mask.actor.MaskActor;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.OfflineParam;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.SSOParam;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.common.util 
 *    |_ OmniUtil.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 3.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@UtilityClass
public class OmniUtil {

	private ConfigUtil config = ConfigUtil.getInstance();

	private String LINE_SEPERATOR = System.getProperty("line.separator");

	/**
	 * 
	 * <pre>
	 * comment  : 채널이 오프라인인지 여부 체크하기
	 * author   : takkies
	 * date     : 2020. 9. 3. 오후 4:14:57
	 * </pre>
	 * 
	 * @param channel
	 * @return
	 */
	public boolean isOffline(Channel channel) {
		if (channel == null) {
			return false;
		}
		return channel.getOflnChnYn().equalsIgnoreCase("Y");
	}

	public boolean isMembership(String isMembership) {
		if (StringUtils.isEmpty(isMembership)) {
			return false;
		} 
		return isMembership.equalsIgnoreCase("Y");
	}
	
	public boolean isUnLinkMembership(String isUnLinkMembership) {
		if (StringUtils.isEmpty(isUnLinkMembership)) {
			return false;
		} 
		return isUnLinkMembership.equalsIgnoreCase("Y");
	}	
	/**
	 * 
	 * <pre>
	 * comment  : CI가 점유인증 CI 여부 확인
	 * author   : takkies
	 * date     : 2020. 9. 4. 오후 2:41:45
	 * </pre>
	 * 
	 * @param ciNo
	 * @return
	 */
	public boolean isOccupationCiCert(final String ciNo) {
		return !ciNo.endsWith("==");// == 로 끝나지 않으면 점유인증임.
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 10. 오후 2:10:14
	 * </pre>
	 * 
	 * @return
	 */
	public String getSerialNumber() {
		return RandomStringUtils.randomNumeric(9);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 14. 오후 1:21:55
	 * </pre>
	 * 
	 * @param authenticators
	 * @return
	 */
	public Map<String, String> getAuthenticators(final String authenticators) {
		Map<String, String> idpAuthenticatorMapping = new HashMap<>();
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
		return idpAuthenticatorMapping;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 14. 오후 1:24:48
	 * </pre>
	 * 
	 * @param authenticators
	 * @param authKey
	 * @return
	 */
	public String getAuthenticator(final String authenticators, final String authKey) {
		Map<String, String> maps = getAuthenticators(authenticators);

		for (String key : maps.keySet()) {
			if (key.equals(authKey)) {
				return maps.get(key);
			}
		}

		return "";
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 15. 오후 1:30:09
	 * </pre>
	 * 
	 * @param name
	 * @param locale
	 * @return
	 */
	public String maskUserName(final String name, final Locale locale) {
		return new Masker.Builder().maskType(MaskActor.Type.USERNAME) //
				.maskValue(name).countryCode(locale.getCountry()) //
				.build().masking();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 15. 오후 1:30:59
	 * </pre>
	 * 
	 * @param id
	 * @return
	 */
	public String maskUserId(String id) {
		return new Masker.Builder().maskType(MaskActor.Type.USERID) //
				.maskValue(id) //
				.build().masking();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 15. 오후 1:33:28
	 * </pre>
	 * 
	 * @param mobile
	 * @param locale
	 * @return
	 */
	public String maskMobile(String mobile, Locale locale) {
		return new Masker.Builder(MaskActor.Type.MOBILE, mobile, locale.getCountry()) //
				.build().masking();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 21. 오전 10:42:25
	 * </pre>
	 * 
	 * @param list
	 * @return
	 */
	public String[] getListToArray(List<String> list) {
		if (list == null) {
			list = Collections.emptyList();
			return list.toArray(new String[list.size()]);
		} else {
			return list.toArray(new String[list.size()]);
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 21. 오후 6:44:46
	 * </pre>
	 * 
	 * @param request
	 * @param loginid
	 * @return
	 */
	public boolean getAutoLogin(final HttpServletRequest request, final String loginid) {
		Object obj = WebUtil.getSession(OmniConstants.AUTO_LOGIN_SESSION);
		if (obj != null) {
			String autologinStr = obj.toString();
			return  "Y".equals(autologinStr);
		} else {
			Cookie cookie = WebUtil.getCookies(request, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + loginid);
			if (cookie != null) {
				final String value = cookie.getValue();
				boolean autologin = StringUtils.isEmpty(value) ? false : value.equals("Y");
				return autologin;
			}
		}
		return false;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 비회원 주문 URL 
	 * author   : takkies
	 * date     : 2020. 9. 22. 오후 1:42:35
	 * </pre>
	 * 
	 * @param channel
	 * @return
	 */
	public String getOrderUrl(final Channel channel) {
		String orderUrl = "";
		
		if (WebUtil.isMobile()) {
			orderUrl = channel.getMblNmbrOrdUrl();
		} else {
			orderUrl = channel.getPcNmbrOrdUrl();
		}

		return orderUrl;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 비회원 주문 URL 
	 * author   : takkies
	 * date     : 2020. 9. 22. 오후 1:42:35
	 * </pre>
	 * 
	 * @param channel
	 * @return
	 */
	public String getOrderUrl(final Channel channel, final String profile) {
		String orderUrl = "";
		String redirectUri = OmniUtil.getRedirectUrl(channel);
		String cancelUri = (String) WebUtil.getSession(OmniConstants.CANCEL_URI);
		
		if (WebUtil.isMobile()) {
			orderUrl = channel.getMblNmbrOrdUrl();
		} else {
			orderUrl = channel.getPcNmbrOrdUrl();
		}
		
		final boolean isChannelNonMemberParam = config.isChannelNonMemberParam(channel.getChCd(), profile);
		
		if(!StringUtils.isEmpty(orderUrl) && isChannelNonMemberParam) {
			if (!StringUtils.isEmpty(redirectUri) && !orderUrl.contains("?")) {
				orderUrl += "?redirectUri=" + redirectUri;
			} else if (!StringUtils.isEmpty(redirectUri) && orderUrl.contains("?")) {
				orderUrl += "&redirectUri=" + redirectUri;
			}
			
			if (!StringUtils.isEmpty(cancelUri) && !orderUrl.contains("?")) {
				orderUrl += "?cancelUri=" + cancelUri;
			} else if (!StringUtils.isEmpty(cancelUri) && orderUrl.contains("?")) {
				orderUrl += "&cancelUri=" + cancelUri;
			}
		}

		return orderUrl;
	}	

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 3. 오후 6:24:07
	 * </pre>
	 * 
	 * @param channel
	 * @param profile
	 * @return
	 */
	public String getRedirectOfflineInitUrl(final Channel channel, final String profile) {
		return config.getChannelInitUrl(channel.getChCd(), profile);
	}

	public String getCancelRedirectOfflineUrl(final Channel channel, final String profile) {
		Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
		if (obj != null) {
			final OfflineParam offlineParam = (OfflineParam) obj;
			if (offlineParam != null) {
				if (StringUtils.hasText(offlineParam.getCancelUrl())) {
					return HtmlUtils.htmlUnescape(offlineParam.getCancelUrl());
				} else {
					String returnUrl = getRedirectUrl(channel);
					if (StringUtils.hasText(returnUrl)) {  // return getRedirectOfflineInitUrl(channel, profile);
						return HtmlUtils.htmlUnescape(returnUrl);
					} else {
						return getRedirectOfflineInitUrl(channel, profile);
					}
				}
			}
		}
		return getRedirectOfflineInitUrl(channel, profile);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 15. 오전 10:02:13
	 * </pre>
	 * 
	 * @param channel
	 * @param profile
	 * @return
	 */
	public String getRedirectOfflineInitWithParamUrl(final Channel channel, final String profile) {
		String url = config.getChannelInitUrl(channel.getChCd(), profile);
		OfflineParam offlineParam = (OfflineParam) WebUtil.getSession(OmniConstants.OFFLINEPARAM);
		return url + "?" + WebUtil.getOfflineParam(offlineParam);

	}

	/**
	 * 
	 * <pre>
	 * comment  : 채널 리다이렉트 URL 구하기
	 * author   : takkies
	 * date     : 2020. 9. 22. 오후 1:35:36
	 * </pre>
	 * 
	 * @param channel
	 * @return
	 */
	public String getRedirectUrl(final Channel channel) {
		try {
			if (isOffline(channel)) {
				return getOfflineRedirectUrl(channel);
			} else {
				return getOnlineRedirectUrl(channel);
			}
		} catch (UnsupportedEncodingException e) {

		}
		return channel.getHmpgUrl();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 이니스프리 POS 용 리다이렉트 URL 생성
	 * author   : takkies
	 * date     : 2020. 10. 8. 오후 3:00:38
	 * </pre>
	 * 
	 * @param channel
	 * @param offlineParam
	 * @param incsNo
	 * @return
	 */
	public String getRedirectUrlWithIncsNo(final Channel channel, final OfflineParam offlineParam, final String incsNo, final String chCd) {
		String redirectUrl = null;
		try {
			if (offlineParam != null) {
				if (StringUtils.hasText(offlineParam.getReturnUrl())) {
					redirectUrl = URLDecoder.decode(offlineParam.getReturnUrl(), StandardCharsets.UTF_8.name());
				}
			} else {
				if (StringUtils.hasText(channel.getHmpgUrl())) {
					redirectUrl = channel.getHmpgUrl();
				}
			}
		} catch (UnsupportedEncodingException e) {
			if (offlineParam != null) {
				redirectUrl = offlineParam.getReturnUrl();
			}
		}
		return redirectUrl;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 채널 온라인 리다이렉트 URL 구하기
	 * author   : takkies
	 * date     : 2020. 9. 22. 오후 1:35:26
	 * </pre>
	 * 
	 * @param channel
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getOnlineRedirectUrl(final Channel channel) throws UnsupportedEncodingException {
		Object obj = WebUtil.getSession(OmniConstants.SSOPARAM);
		if (obj != null) {
			SSOParam ssoParam = (SSOParam) obj;
			final String redirectUrl = ssoParam.getRedirectUri();
			
			boolean isMembership = OmniUtil.isMembership(ssoParam.getIsMembership());
			
			if(isMembership) {
				return (String) WebUtil.getSession("MEMBERSHIP_AFTER_REDIRECT_URL");
			}
			
			if (StringUtils.isEmpty(redirectUrl)) {
				return channel.getHmpgUrl();
			}
			return redirectUrl;
		} else {
			return channel.getHmpgUrl();
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 채널 오프라인 리다이렉트 URL 구하기
	 * author   : takkies
	 * date     : 2020. 9. 22. 오후 1:35:31
	 * </pre>
	 * 
	 * @param channel
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getOfflineRedirectUrl(final Channel channel) {
		Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
		if (obj != null) {
			OfflineParam offlineParam = (OfflineParam) obj;
			String redirectUrl = offlineParam.getReturnUrl();
			if (StringUtils.isEmpty(redirectUrl)) {
				redirectUrl = channel.getHmpgUrl();
			}
			return redirectUrl;
		} else {
			return channel.getHmpgUrl();
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 로그아웃과 리다이렉트 URL을 조합 
	 * author   : takkies
	 * date     : 2020. 9. 23. 오후 3:49:18
	 * </pre>
	 * 
	 * @param channel
	 * @return
	 */
	public String getLogoutWithRedirectUrl(final Channel channel, final String redirectUri) {
		final String logoutUrl = channel.getChnLoutUrl(); // 로그아웃 URL
		return logoutUrl.concat(redirectUri);
	}
	
	@SuppressWarnings({ "unused", "deprecation" })
	public String getChannelDomainWithRedirectUrl(final Channel channel, final String redirectUri) {
		String domain = "";
		try {
			URL url = new URL(URLDecoder.decode(redirectUri));
            domain = URLDecoder.decode(redirectUri);
		} catch (Exception e) { 
			log.debug("URL 변환 실패 - {}", redirectUri);
		}
		
		if(StringUtils.isEmpty(domain)) {
			try {
				URL url = new URL(channel.getHmpgUrl());
				
				String scheme = url.getProtocol();
	            String host = url.getHost();
	          
	            String port = String.valueOf(url.getPort());
				
	            domain = scheme + "://" + host + ("-1".equals(port) ? "" : ":" + port) + URLDecoder.decode(redirectUri);
			} catch (Exception e) { 
				log.debug("URL 변환 실패 - {}", redirectUri);
			}		
		}
		
		return domain;
	}
	
	@SuppressWarnings("deprecation")
	public String getRedirectUriWithoutChannelDomain(final String redirectUri) {
		try {
			URL url = new URL(URLDecoder.decode(redirectUri));
			
			return StringUtils.isEmpty(url.getQuery()) ? url.getPath() : url.getPath() + "?" + url.getQuery();
		} catch (Exception e) {
			log.debug("도메인 미포함 - {}", redirectUri);
			return redirectUri;
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean redirectUriValidationCheck(final Channel channel, final String redirectUri) {
		try {
			if(StringUtils.isEmpty(channel.getPrmsChnRdrcUrlLv()) || channel.getPrmsChnRdrcUrlList().length == 0) { // prms_chn_rdrc_url_lv 값이 없을 경우 true
				return true;
			}
			
			if(StringUtils.isEmpty(redirectUri)) { // redirectUri 값이 없을 경우 false
				return false;
			}
			
			URL url = new URL(URLDecoder.decode(redirectUri));
			log.debug("url : {}", url);
			
			for(String prmsChnRdrcUrl : channel.getPrmsChnRdrcUrlList()) {
				if(url.getHost().toLowerCase().equals(prmsChnRdrcUrl)) return true;
			}
			
			return false;
		} catch (Exception e) {
			log.debug("도메인 미포함 - {}", redirectUri);
			return true;
		}
	}

	public String getLogoutRedirectLogin(final Channel channel, final String ssoAuthorizeUrl) {
		final String logoutUrl = channel.getChnLoutUrl(); // 로그아웃 URL
		final SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
		if (ssoParam != null) {
			final String ssoAuthUrl = ssoAuthorizeUrl.concat("?").concat(WebUtil.getLoginSsoParamsAuth());
			return logoutUrl.concat(ssoAuthUrl);
		}
		return "";
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 23. 오후 4:50:25
	 * </pre>
	 * 
	 * @return
	 */
	public String getOfflineParam() {
		Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
		if (obj != null) {
			OfflineParam offlineParam = (OfflineParam) obj;
			StringBuilder query = new StringBuilder();
			query.append("?chCd=").append(offlineParam.getChCd());
			query.append("&joinPrtnId=").append(offlineParam.getJoinPrtnId());
			query.append("&joinPrtnNm=").append(offlineParam.getJoinPrtnNm());
			query.append("&joinEmpId=").append(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
			query.append("&returnUrl=").append(offlineParam.getReturnUrl());
			query.append("&addInfo=").append(offlineParam.getAddInfo());
			query.append("&chnCd=").append(offlineParam.getChnCd());
			query.append("&storeCd=").append(offlineParam.getStoreCd());
			query.append("&storenm=").append(offlineParam.getStorenm());
			query.append("&user_id=").append(offlineParam.getUser_id());
			if (StringUtils.hasText(offlineParam.getHh())) {
				query.append("&hh=").append(offlineParam.getHh());
			}
			if (StringUtils.hasText(offlineParam.getDt())) {
				query.append("&dt=").append(offlineParam.getDt());
			}
			if (StringUtils.hasText(offlineParam.getOp())) {
				query.append("&op=").append(offlineParam.getOp());
			}
			if (StringUtils.hasText(offlineParam.getCancelUrl())) {
				query.append("&cancelUrl=").append(offlineParam.getCancelUrl());
			}
			return query.toString();
		}
		return "";
	}

	/**
	 * 
	 * <pre>
	 * comment  : SNS Authenticator 를 화면에 보여줄때 정렬하여 보여주도록 재정렬하기
	 * author   : takkies
	 * date     : 2020. 10. 19. 오전 9:38:30
	 * </pre>
	 * 
	 * @param idpAuthenticatorMapping
	 * @return
	 */
	public Map<String, String> getIdpAuthenticatorMappingSort(Map<String, String> idpAuthenticatorMapping) {
		Map<String, String> idpMapping = new LinkedHashMap<>();
		if (!idpAuthenticatorMapping.isEmpty()) {
			List<Object> snsTypes = config.snsTypes();
			if (snsTypes == null || snsTypes.isEmpty()) {
				return idpAuthenticatorMapping;
			}
			for (Object obj : snsTypes) {
				String sns = obj.toString();
				String snsVal = idpAuthenticatorMapping.get(sns);
				if (StringUtils.hasText(sns) && StringUtils.hasText(snsVal)) {
					idpMapping.put(sns, idpAuthenticatorMapping.get(sns));
				}
			}
		}
		return idpMapping;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 30. 오전 8:51:47
	 * </pre>
	 * 
	 * @param baseResponse
	 * @return
	 */
	public String getApiResultCode(BaseResponse baseResponse, final String apiName) {
		int colsize = 70;
		StringBuilder result = new StringBuilder();
		result.append(LINE_SEPERATOR);
		result.append(rightPad(" API OMNIRESULT ", colsize, " ")).append(LINE_SEPERATOR);
		result.append("┌").append(rightPad("", colsize, "-")).append(LINE_SEPERATOR);
		result.append("│").append(rightPad(" api  : %s", colsize, " ")).append(LINE_SEPERATOR);
		result.append("│").append(rightPad(" code : %s", colsize, " ")).append(LINE_SEPERATOR);
		String msg = null;
		if (StringUtils.hasText(baseResponse.getMessage())) {
			msg = baseResponse.getMessage();
			result.append("│").append(rightPad(" msg  : %s", colsize, " ")).append(LINE_SEPERATOR);
		}
		result.append("└").append(rightPad("", colsize, "-"));
		if (StringUtils.hasText(msg)) {
			return String.format(result.toString(), apiName, baseResponse.getResultCode(), msg);
		} else {
			return String.format(result.toString(), apiName, baseResponse.getResultCode());
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 30. 오후 4:15:09
	 * </pre>
	 * 
	 * @param request
	 * @return
	 */
	public boolean isMobileApp(final HttpServletRequest request) {
		Object obj = WebUtil.getSession(OmniConstants.SSOPARAM);
		if (obj != null) {
			SSOParam ssoParam = (SSOParam) obj;
			if (ssoParam != null) {
				
				log.info("mobile application device type {}", ssoParam.getDt());
				
				return "A".equals(ssoParam.getDt()); // A app (MobileApplication)
			}
		}
		return "A".equals(WebUtil.getStringParameter(request, "dt"));
	}
	
	public boolean isAmoreMallAOS(final HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		boolean isAOS = userAgent.matches(".*(device=android).*");
		boolean isAmoreMallWeb = "031".equals(WebUtil.getStringSession(request, OmniConstants.CH_CD_SESSION));
		
		return isAOS && isAmoreMallWeb;
	}
	
	public boolean isAmoreMallIOS(final HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		boolean isIOS = userAgent.matches(".*(device=ios).*");
		boolean isAmoreMallWeb = "031".equals(WebUtil.getStringSession(request, OmniConstants.CH_CD_SESSION));
		
		return isIOS && isAmoreMallWeb;
	}
	
	public boolean isIOSApp(final HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		
		if("031".equals(WebUtil.getStringSession(request, OmniConstants.CH_CD_SESSION))) { // 아모레몰 iOS App
			return userAgent.matches(".*(device=ios).*");
		} else if ("036".equals(WebUtil.getStringSession(request, OmniConstants.CH_CD_SESSION))) { // 이니스프리몰 iOS App
			return userAgent.matches(".*(inniMemAppIOS).*");
		} else if ("107".equals(WebUtil.getStringSession(request, OmniConstants.CH_CD_SESSION))) { // CUSTOM.ME+ iOS App
			return userAgent.matches(".*(APTRACK_IOS).*");
		} else if ("BAA".equals(WebUtil.getStringSession(request, OmniConstants.CH_CD_SESSION))) { // 뷰티엔젤 iOS App
			return userAgent.matches(".*(device=ios).*");
		}
		
		return false;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 18. 오전 7:13:14
	 * </pre>
	 * 
	 * @param urlStr
	 * @return
	 */
	public String getContentsByUrl(final String urlStr) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlCon = null;

			URL testUrlHttps = new URL(urlStr);
			if (testUrlHttps.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();
				HttpsURLConnection.setFollowRedirects(false);
				HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
				https.setHostnameVerifier(new NoopHostnameVerifier());
				urlCon = https;
				//urlCon.setInstanceFollowRedirects(true); // you still need to handle redirect manully.
				//HttpURLConnection.setFollowRedirects(true);
			} else {
				HttpURLConnection.setFollowRedirects(false);
				urlCon = (HttpURLConnection) url.openConnection();
				//urlCon.setInstanceFollowRedirects(true); // you still need to handle redirect manully.
				//HttpURLConnection.setFollowRedirects(true);
			}

			urlCon.setRequestMethod("GET");
			int responseCode = urlCon.getResponseCode();
			log.info("Sending 'GET' request to URL : {}", urlStr);
			log.info("Response Code : {}", responseCode);

			boolean redirect = false;

			if (responseCode != HttpURLConnection.HTTP_OK) {
				if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP //
						|| responseCode == HttpURLConnection.HTTP_MOVED_PERM //
						|| responseCode == HttpURLConnection.HTTP_SEE_OTHER)

					redirect = true;
				log.info("redirect url? {}", redirect);

			}

			if (redirect) {
				// get redirect url from "location" header field
				
				String redirectUrl = urlCon.getHeaderField("Location");
				url = new URL(redirectUrl);
				testUrlHttps = new URL(redirectUrl);
				if (testUrlHttps.getProtocol().toLowerCase().equals("https")) {
					trustAllHosts();
					HttpsURLConnection.setFollowRedirects(false);
					HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
					https.setHostnameVerifier(new NoopHostnameVerifier());
					urlCon = https;
					//HttpsURLConnection.setFollowRedirects(true);
					//urlCon.setInstanceFollowRedirects(true); // you still need to handle redirect manully.
					//HttpURLConnection.setFollowRedirects(true);
				} else {
					HttpURLConnection.setFollowRedirects(false);
					urlCon = (HttpURLConnection) url.openConnection();
					//HttpURLConnection.setFollowRedirects(true);
					//urlCon.setInstanceFollowRedirects(true); // you still need to handle redirect manully.
					//HttpURLConnection.setFollowRedirects(true);
				}
				
				urlCon.setRequestMethod("GET");
				responseCode = urlCon.getResponseCode();
				log.info("Redirect Sending 'GET' request to URL : {}", redirectUrl);
				log.info("Redirect Response Code : {}", responseCode);
				
			}

			Charset charset = Charset.forName("UTF-8");
			BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), charset));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return response.toString();
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public String getHtmlContents2(final String url) {

		try {
			SSLContext sslcontext = SSLContexts.createDefault(); // .getInstance("TLS");
			sslcontext.init(null, null, null);
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(//
					sslcontext, //
					new String[] { "TLSv1.2" }, //
					null, //
					new NoopHostnameVerifier() //
			);
			CloseableHttpClient client = HttpClients.custom() //
					// .setRedirectStrategy(new LaxRedirectStrategy()) //
					.setSSLSocketFactory(socketFactory).build();

			// client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

			RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(true).build();// disable redirect

			HttpGet httpget = new HttpGet(url);

			httpget.setConfig(requestConfig);

			CloseableHttpResponse response = client.execute(httpget);
			log.info("##### Response Code (Apache): {}", response.getStatusLine().getStatusCode());
			HttpEntity entity = response.getEntity();
			log.info("#####{}", StringUtil.printJson(entity));
			if (entity != null) {
				return EntityUtils.toString(entity, StandardCharsets.UTF_8.name());
			}
			// } catch (NoSuchAlgorithmException e) {
			// log.error("Apache HTTP Client Failed", e);
		} catch (KeyManagementException e) {
			log.error("Apache HTTP Client Failed", e);
		} catch (Exception e) {
			log.error("Apache HTTP Client Failed", e);
		}
		return null;
	}

	/**
	 * 
	 * <pre>
	 * comment  : (약관과 같은) 외부 HTML정보 조회
	 * author   : takkies
	 * date     : 2020. 8. 13. 오후 2:43:04
	 * </pre>
	 * 
	 * @param url
	 * @return
	 */
	public String getHtmlContents(final String url) {
		try {
			log.info("#### {}", url);
			if (url.startsWith("https://")) {
				// TrustStrategy trustSelfSignedStrategy = new TrustSelfSignedStrategy();
				TrustStrategy trustAllStrategy = new TrustStrategy() {
					public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						return true;
					}
				};

				SSLContextBuilder builder = new SSLContextBuilder();
				builder.loadTrustMaterial(trustAllStrategy);
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());

				RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(true).build();// disable redirect
				
				HttpGet request = new HttpGet(url);
				request.setConfig(requestConfig);

				try (CloseableHttpClient httpclient = HttpClients.custom() //
						.setSSLSocketFactory(sslsf) //
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE) //
						.build(); //
						CloseableHttpResponse response = httpclient.execute(request)) {
					StatusLine status = response.getStatusLine();

					log.info("##### status code {}", status.getStatusCode());

					if (status.getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						if (entity != null) {
							return EntityUtils.toString(entity, StandardCharsets.UTF_8.name());
						}
					}
				}
			} else {
				HttpGet request = new HttpGet(url);
				try (CloseableHttpClient httpclient = HttpClients.custom() //
						.build(); //
						CloseableHttpResponse response = httpclient.execute(request)) {
					StatusLine status = response.getStatusLine();

					log.info("status code {}", status.getStatusCode());

					if (status.getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						if (entity != null) {
							return EntityUtils.toString(entity, StandardCharsets.UTF_8.name());
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 로그인 아이디에 특수문자가 포함되어 있는지 체크(사이즈 체크 안함) -> 2021.04.08 사이즈도 체크하도록 수정 - hjw0228
	 * author   : takkies
	 * date     : 2020. 11. 24. 오전 10:34:57
	 * </pre>
	 * @param loginId
	 * @return 특수문자 포함이면 true, 아니면 false
	 */
	public boolean includeSpecialCharInLoginId(final String loginId) {
		
		CheckResponse response = new Checker.Builder() //
				 			.checkType(CheckActor.Type.ID) //
				 			.checkValue(loginId) //
				 			.build() //
				 			.check();
		
		if (CheckActor.SUCCESS == response.getStatus()) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 이니스프리 모바일앱인지 체크하기
	 * author   : takkies
	 * date     : 2020. 11. 26. 오후 2:09:48
	 * </pre>
	 * @param request
	 * @return
	 */
	public boolean isInniMobileBackAction(HttpServletRequest request) {
		boolean isMobileApp = isMobileApp(request);
		boolean isInniWeb = "036".equals(WebUtil.getStringSession(request, OmniConstants.CH_CD_SESSION));
		return isMobileApp && isInniWeb;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 뷰티엔젤 모바일앱인지 체크하기
	 * author   : hjw0228
	 * date     : 2021. 09. 15. 오후 6:16:13
	 * </pre>
	 * @param request
	 * @return
	 */
	public boolean isBeautyAngelMobileBackAction(HttpServletRequest request) {
		boolean isMobileApp = isMobileApp(request);
		boolean isBeautyAngelWeb = "BAA".equals(WebUtil.getStringSession(request, OmniConstants.CH_CD_SESSION));
		return isMobileApp && isBeautyAngelWeb;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 오설록 모바일앱인지 체크하기
	 * author   : hjw0228
	 * date     : 2021. 09. 30. 오전 11:41:37
	 * </pre>
	 * @param request
	 * @return
	 */
	public boolean isOsullocMobileBackAction(HttpServletRequest request) {
		boolean isMobileApp = isMobileApp(request);
		boolean isOsullocWeb = "039".equals(WebUtil.getStringSession(request, OmniConstants.CH_CD_SESSION));
		return isMobileApp && isOsullocWeb;
	}
	
	public String getLoggingId(final Map<String, Object> request) {
		String loggingId = (String) request.get(OmniConstants.PROCESS_LOGGING_ID);
		if (StringUtils.isEmpty(loggingId)) {
			loggingId = UuidUtil.getIdByDate("P");
			request.put(OmniConstants.PROCESS_LOGGING_ID, loggingId);
		}
		return loggingId;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 12. 4. 오전 11:50:32
	 * </pre>
	 * @param urlStr
	 * @return
	 */
	public String getConvertUrl(final String urlStr) {
		try {
			URL url = new URL(urlStr);
			URI uri = new URI( //
					url.getProtocol(), //
					url.getUserInfo(), //
					url.getHost(), 
					url.getPort(), //
					url.getPath(), //
					url.getQuery(), //
					url.getRef() //
					);
			return uri.toASCIIString();
		} catch (MalformedURLException e) {
			return urlStr;
		} catch (URISyntaxException e) {
			return urlStr;
		}
	}
	
	public String getStateParamFromSSOParam() {
		Object obj = WebUtil.getSession(OmniConstants.SSOPARAM);
		Map<String, Object> state = new HashMap<String, Object>();
		if (obj != null) {
			SSOParam ssoParam = (SSOParam) obj;
			final String redirectUrl = ssoParam.getRedirectUri();
			if (!StringUtils.isEmpty(redirectUrl)) {
				
				Properties props = new Properties();
				try {
					if(!StringUtils.isEmpty(ssoParam.getState())) {
						props.load(new StringReader(ssoParam.getState().substring(1, ssoParam.getState().length() - 1).replace(", ", "\n")));

						for (Map.Entry<Object, Object> e : props.entrySet()) {
							state.put((String) e.getKey(), (String) e.getValue());
						}
					}
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
					
				state.put("redirectUri", redirectUrl);
			} else {
				Properties props = new Properties();
				try {
					if(!StringUtils.isEmpty(ssoParam.getState())) {
						props.load(new StringReader(ssoParam.getState().substring(1, ssoParam.getState().length() - 1).replace(", ", "\n")));

						for (Map.Entry<Object, Object> e : props.entrySet()) {
							state.put((String) e.getKey(), (String) e.getValue());
						}
					}
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		
		return state.toString();
	}
	
	public String getRedirectUrlFromStateMapAfterLogin(final String chCd, final String profile, final Map<String, Object> state) {
		if(state.get("redirectUri") == null) return "";
		
		String redirectUrl = (String) state.get("redirectUri");
		
		String param = config.getAddParam(chCd, profile, ".login.success");
		log.debug("param : {}", param);
		
		if(StringUtils.hasText(param)) {
			if(redirectUrl.contains("?")) {
				redirectUrl = redirectUrl.concat("&").concat(param);
			} else {
				redirectUrl = redirectUrl.concat("?").concat(param);
			}
		}
		log.debug("getRedirectUrlFromStateMapAfterLogin >>>>> asis : {}, tobe : {}", state.get("redirectUri"), redirectUrl);
		
		return redirectUrl;
	}
	
	public String getRedirectUrlFromStateMapAfterJoin(final String chCd, final String profile, final Map<String, Object> state) {
		if(state.get("redirectUri") == null) return "";
		
		String redirectUrl = (String) state.get("redirectUri");
		log.debug("getRedirectUrlFromStateMapAfterJoin >>>>> {}", redirectUrl);
		
		String param = config.getAddParam(chCd, profile, ".join.success");
		
		if(StringUtils.hasText(param)) {
			if(redirectUrl.contains("?")) {
				redirectUrl = redirectUrl.concat("&").concat(param);
			} else {
				redirectUrl = redirectUrl.concat("?").concat(param);
			}
		}
		log.debug("getRedirectUrlFromStateMapAfterJoin >>>>> asis : {}, tobe : {}", state.get("redirectUri"), redirectUrl);
		
		return redirectUrl;
	}	
}
