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
 * Date   	          : 2020. 9. 1..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.wso2.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.ga.GaTaggingUtils;
import com.amorepacific.oneap.auth.ga.OmniGaTaggingConstants;
import com.amorepacific.oneap.auth.ga.vo.GaTagData;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.LocaleUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.OAuth2Error;
import com.amorepacific.oneap.common.vo.OfflineParam;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.wso2.web 
 *    |_ AuthController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 1.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Controller
public class AuthController {

	@Value("${wso2.ssoauthorizeurl}")
	private String ssoAuthorizeUrl;

	@Value("${wso2.ssocommonauthurl}")
	private String commonAuthUrl;

	@Value("${wso2.displayscopes}")
	private boolean displayScopes;
	
	@Value("${wso2.oneapurl}")
	private String oneApUrl;

	@Autowired
	private CommonService commonService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private GaTaggingUtils gataggingUtils; // ga taagging util
	
	@Autowired
	private SystemInfo systemInfo;

	private ConfigUtil config = ConfigUtil.getInstance();

	@GetMapping("/oauth2_authz")
	public String authz(final Model model) {
//		final String app = WebUtil.getStringParameter("application");
		final String scopeString = WebUtil.getStringParameter("scope");

		if (this.displayScopes && StringUtils.hasText(scopeString)) {
			// Remove "openid" from the scope list to display.
			List<String> openIdScopes = Stream.of(scopeString.split(" ")).filter(x -> !org.apache.commons.lang3.StringUtils.equalsIgnoreCase(x, "openid")).collect(Collectors.toList());

			if (CollectionUtils.isNotEmpty(openIdScopes)) {
//				String scopesAsString = String.join(" ", openIdScopes);
//				try {
//					Set<Scope> scopes = new OAuth2ScopeService().getScopes(null, null, true, scopesAsString);
//					model.addAttribute("scopes", scopes);
//					for (Scope scope : scopes) {
//						openIdScopes.remove(scope.getName());
//					}
//					model.addAttribute("openIdScopes", openIdScopes);
//					model.addAttribute(Constants.SESSION_DATA_KEY_CONSENT, WebUtil.getStringParameter(Constants.SESSION_DATA_KEY_CONSENT));
//				} catch (IdentityOAuth2ScopeServerException e) {
//					log.error(e.getMessage(), e);
//				}

			}
		}

		return "wso2/authz";
	}

	// 로그인 실패시 sessionDataKey를 새로 발급 받음.
	@GetMapping("/reauthz")
	public void reAuthz(final HttpServletResponse response) throws IOException {
		final SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
		if (ssoParam != null) {
			final String ssoAuthUrl = this.ssoAuthorizeUrl.concat("?").concat(WebUtil.getSsoParamsAuth());
			response.sendRedirect(ssoAuthUrl);
			return;
		}
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		response.sendRedirect(WebUtil.getContextPath() + "/redirect-authz?chCd=" + chCd);
		return;
	}

	@GetMapping("/oauth2_error")
	public String oauth2Error(final OAuth2Error oauth2Error, final HttpServletRequest request, final Model model) {

		model.addAttribute("code", oauth2Error.getOauthErrorCode());
		model.addAttribute("message", oauth2Error.getOauthErrorMsg());

		Map<String, ?> redirectMap = RequestContextUtils.getInputFlashMap(request);
		if (redirectMap != null) {
			OAuth2Error oauth2ErrorObj = (OAuth2Error) redirectMap.get("oauth2Error");
			log.debug("▶▶▶▶▶▶ [wso2] oauth2 error : {} ", StringUtil.printJson(oauth2ErrorObj));
			if (oauth2ErrorObj != null) {
				model.addAttribute("code", oauth2ErrorObj.getOauthErrorCode());
				model.addAttribute("message", oauth2ErrorObj.getOauthErrorMsg());
			}
		}

		return "wso2/oauth2_error";
	}

	@GetMapping("/oauth2_consent")
	public String consent(final Model model) {

		return "";
	}

	@GetMapping("/oauth2_logout_consent")
	public String logoutConsent(final Model model) {

		return "";
	}

	// WSO2 Logout Redirect 처리
	@GetMapping("/oauth2_logout")
	public void logout(final HttpServletRequest request, final HttpServletResponse response) {
		Map<String, Object> state = new HashMap<String, Object>();

		log.debug("*** oauth2 logout referer : [{}]", request.getHeader("referer"));

		// 로그아웃 시 쿠키 값 제거 (one-ap-amore-channel)
		Cookie chCdCookie = WebUtil.getCookies(request, OmniConstants.ONE_AP_MOVE_CHANNEL);
		String chCd = "";
		if(chCdCookie != null) {
			chCd = (String) chCdCookie.getValue();
		}
		WebUtil.removeCookie(response, OmniConstants.ONE_AP_MOVE_CHANNEL);
		
		// 로그아웃 시 세션 값 제거 (isSnsLoginComplete)
		boolean isSnsLoginComplete = WebUtil.getSession(OmniConstants.IS_LOGIN_COMPLETE) == null ? false : (boolean) WebUtil.getSession(OmniConstants.IS_LOGIN_COMPLETE);
		if(isSnsLoginComplete) {
			WebUtil.removeSession(OmniConstants.IS_LOGIN_COMPLETE);
		}

		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String name = params.nextElement();
			log.debug("*** parameter \"{}\" : {}", name, request.getParameter(name));
		}

		String stateParam = WebUtil.getStringParameter(request, "state", "");
		log.debug("*** state parameter : [{}]", stateParam);
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;

		Properties props = new Properties();
		try {
			if (StringUtils.isEmpty(stateParam)) {
				String referer = this.getReferer(request, chCd);
				
				// 2022-08-22 state 파라미터가 없는 경우 쿠키에서 redirectUri 체크, 존재하면 쿠키 삭제 후 이동, 존재하지 않으면 referer 참조
				Cookie cookie = WebUtil.getLogoutUriCookie(profile, request);
				if (cookie != null) {
					String logoutRedirectUri = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.name());
					if(!logoutRedirectUri.equals(URLDecoder.decode(logoutRedirectUri, StandardCharsets.UTF_8.name()))) logoutRedirectUri = URLDecoder.decode(logoutRedirectUri, StandardCharsets.UTF_8.name());
					
					WebUtil.removeLogoutUriCookie(profile, response);
					try {
						// URL 디코딩 &amp; -> &
						logoutRedirectUri = logoutRedirectUri.replaceAll("&amp;", "&");
						StringBuffer sb = new StringBuffer();
						if(logoutRedirectUri.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
							String[] urlArr = logoutRedirectUri.split("\\?");
							for(int i=0;i<urlArr.length;i++) {
								if(i == 0) {
									sb.append(urlArr[i]).append("?");
								}
							}
							
							if(StringUtils.hasText(sb) && StringUtils.hasText(logoutRedirectUri.replaceAll(sb.toString(), ""))) {
								sb.append(URLEncoder.encode(logoutRedirectUri.replaceAll(sb.toString(), ""), StandardCharsets.UTF_8.name()));
							}
						
							logoutRedirectUri = sb.toString();
						}

						log.debug("logoutRedirectUri : " + logoutRedirectUri);
						response.sendRedirect(logoutRedirectUri);
						return;	
					} catch (MalformedURLException e) {
						log.error("error message {}",e.getMessage());
					}	
				} else {
					response.sendRedirect(referer);
					return;
				}
			} else {
				props.load(new StringReader(stateParam.substring(1, stateParam.length() - 1).replace(", ", "\n")));

				for (Map.Entry<Object, Object> e : props.entrySet()) {
					state.put((String) e.getKey(), (String) e.getValue());
				}

				if (state != null) {
					// 2022-09-01 멀티 도메인을 위한 로그아웃 처리
					String logoutDomain = (String) state.get("logoutDomain");
					
					if(StringUtils.isEmpty(logoutDomain)) { // logoutDomain 파라미터가 isEmpty 면 one-ap는 one3-ap를 one3-ap는 one-ap를 로그아웃 하기 위해 리다이렉트
						String redirectUri = oneApUrl;
						if(oneApUrl.contains("one-ap")) {
							state.put("logoutDomain", "one-ap");
							redirectUri = redirectUri.replace("one-ap", "one3-ap");
						} else if (oneApUrl.contains("one3-ap")) {
							state.put("logoutDomain", "one3-ap");
							redirectUri = redirectUri.replace("one3-ap", "one-ap");
						}
						redirectUri += "/oidc/logout?state=" + URLEncoder.encode(state.toString(), StandardCharsets.UTF_8.name());
						
						// URL 디코딩 &amp; -> &
						redirectUri = redirectUri.replaceAll("&amp;", "&");

						log.info("*** redirectUri : {} ", redirectUri);
						log.info("*** queryString : {}", request.getQueryString());
						
						WebUtil.setLogoutUriCookie(profile, response, (String) state.get("redirectUri"), "amorepacific.com");
						
						response.sendRedirect(redirectUri);
						return;
					} else {
						String redirectUri = state.get("redirectUri").toString();

						// URL 디코딩 &amp; -> &
						redirectUri = redirectUri.replaceAll("&amp;", "&");

						log.info("*** redirectUri : {} ", redirectUri);
						log.info("*** queryString : {}", request.getQueryString());
						
						WebUtil.removeLogoutUriCookie(profile, response);
						
						response.sendRedirect(redirectUri);
						return;
					}
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	@GetMapping("/samlsso_logout")
	public String samlssoLogout(final Model model) {

		return "";
	}

	@GetMapping("/samlsso_notification")
	public String samlssoNotification(final Model model) {

		return "";
	}

	@GetMapping("/retry")
	public void retry(final HttpServletRequest request, final HttpServletResponse response, final Model model) throws IOException {
		final SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
		if (ssoParam != null) {
			final String ssoAuthUrl = this.ssoAuthorizeUrl.concat("?").concat(WebUtil.getSsoParamsAuth());
			log.debug("▶▶▶▶▶▶ [wso2] oauth2 retry url : {} ", ssoAuthUrl);
			response.sendRedirect(ssoAuthUrl);
			return;
		}

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);

		try {
			if (Objects.isNull(chCd)) {

				String eventAction = null;

				if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.LOGIN_BASIC))) {
					eventAction = (String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_BASIC);
				}
				if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.LOGIN_HANDPHOME))) {
					eventAction = (String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_HANDPHOME);
				}
				if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.LOGIN_SNS))) {
					eventAction = (String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_SNS);
				}
				
				final Map<String,String> gaCookieMap = gataggingUtils.getGaCookieMap(request);	 

				log.debug("▶▶▶▶▶▶  GA Tagging LOGIN FAIL(/retry 회세션에 체널코드가 없습니다) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
						,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),eventAction,null,null);
				
				
				
				GaTagData gaTagDto = GaTagData.builder()
                           .cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
						   .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
						   .el(eventAction)
						   .loginType(eventAction)
						   .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
						   .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
						   .chCd((String) WebUtil.getSession(OmniGaTaggingConstants.CD22))
						   .chCdNm((String) WebUtil.getSession(OmniGaTaggingConstants.CD21))
						   .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_FAIL)
						   .errorMessage("고객통합 에러")
                           .sendFlag("Y")
                           .sessionId(request.getSession().getId())
						   .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
						   .loginId((ssoParam != null)?ssoParam.getSnsId():"")
						   .build();

				
				gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		response.sendRedirect(WebUtil.getContextPath() + "/redirect-authz?chCd=" + chCd);
		return;
	}

	@RequestMapping(value = "/go-login", method = { RequestMethod.GET, RequestMethod.POST })
	public void goLogin(final UmOmniUser user, final HttpServletRequest request, final HttpServletResponse response, final Model model) throws IOException {

		log.debug("▶▶▶▶▶▶ [wso2] loginid : {}", StringUtil.printJson(user));
		
		if (StringUtils.hasText(user.getUid())) {
			WebUtil.setCookies(response, OmniConstants.LOGIN_ID_COOKIE_NAME, URLEncoder.encode(user.getUid().trim(), StandardCharsets.UTF_8.name()));
		}
		
		log.debug("joinType : {}",WebUtil.getStringParameter(request,"joinStepType"));
		
	
		final SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
		
		
		final String isMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_MEMBERSHIP);
		boolean isMembership = OmniUtil.isMembership(isMembershipSession);
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		StringBuilder channelLoginUrl = new StringBuilder();
		channelLoginUrl.append(config.getChannelLoginUrl(ssoParam.getChannelCd(), profile));
		
		if(isMembership) { // 멤버십 연동 중 go-login 으로 이동 시 처리
			response.sendRedirect(ssoParam.getCancelUri());
		} else if (StringUtils.hasText(channelLoginUrl)) { // ChannelLoginUrl 이 존재할 경우 WSO2 거치지 않고 다이렉트로 이동, 이동 시 사용자 아이디는 암호화 처리
			String username = user.getUid();
			channelLoginUrl.append("?paramId=");
			
			if(StringUtils.hasText(username)) {
				channelLoginUrl.append(URLEncoder.encode(SecurityUtil.setXyzValue(username), StandardCharsets.UTF_8.name()));
			}
			
			response.sendRedirect(channelLoginUrl.toString());
		} else {
			if (ssoParam != null) {

				//joinType 이 없는 경우 
				 if(Objects.isNull(WebUtil.getStringParameter(request,"joinStepType"))) {		
					 
					try {
		
						String eventAction = null;
		
						if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.LOGIN_BASIC))) {
							eventAction = (String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_BASIC);
						}
						if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.LOGIN_HANDPHOME))) {
							eventAction = (String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_HANDPHOME);
						}
						
		               ///ga tagging 로그인 화면으로 접근시만 처리 ,회원가입에서 접근하는 로그인은 미 처리
						if (Objects.nonNull(eventAction)) {
							
							final Map<String,String> gaCookieMap = gataggingUtils.getGaCookieMap(request);	 
							
							log.debug("▶▶▶▶▶▶  GA Tagging LOGIN SUCCESS(/go-login) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
									,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),eventAction,WebUtil.getSession(OmniGaTaggingConstants.CD21),WebUtil.getSession(OmniGaTaggingConstants.CD22));
							
							
							GaTagData gaTagDto = GaTagData.builder()
			                           .cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
									   .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
									   .el(eventAction)
									   .loginType(eventAction)
									   .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
									   .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
									   .chCd((String) WebUtil.getSession(OmniGaTaggingConstants.CD22))
									   .chCdNm((String) WebUtil.getSession(OmniGaTaggingConstants.CD21))
									   .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_SUCCESS)
			                           .sendFlag("Y")
			                           .sessionId(request.getSession().getId())
									   .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
									   .loginId(user.getUmUserName())
									   .build();
							
							gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);

						}
					} catch (Exception ex) {
						log.error(ex.getMessage());
					}
				 }
				log.debug("▶▶▶▶▶▶ [wso2] oauth2 login url : {} ", StringUtil.printJson(ssoParam));
				final String ssoAuthUrl = this.ssoAuthorizeUrl.concat("?").concat(WebUtil.getLoginSsoParamsAuth());
				log.debug("▶▶▶▶▶▶ [wso2] oauth2 login url : {} ", ssoAuthUrl);
				response.sendRedirect(ssoAuthUrl);
				return;
			}

			log.debug("▶▶▶▶▶▶ [wso2] sso param is null");

			final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
			response.sendRedirect(WebUtil.getContextPath() + "/redirect-authz?chCd=" + chCd);
			return;
		}
	
	}

	@GetMapping("/go-join")
	public void goJoin(final HttpServletResponse response, final Model model) throws IOException {
		final SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
		if (ssoParam != null) {
			final String ssoAuthUrl = this.ssoAuthorizeUrl.concat("?").concat(WebUtil.getLoginSsoParamsAuth()).concat("&join=true");
			log.debug("▶▶▶▶▶▶ [wso2] oauth2 login url : {} ", ssoAuthUrl);
			response.sendRedirect(ssoAuthUrl);
			return;
		}

		log.debug("▶▶▶▶▶▶ [wso2] sso param is null");

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		response.sendRedirect(WebUtil.getContextPath() + "/redirect-authz?chCd=" + chCd);
		return;
	}

	@GetMapping("/go-join-param")
	public String goJoinParam(final HttpServletResponse response, final Model model) throws IOException {
		final SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
		String queryString = "";
		if (ssoParam != null) {
			queryString = ssoParam.getQueryString();
		}
		return "redirect:/join?" + queryString;
	}

	@GetMapping("/go-join-off")
	public String goJoinOffline(final HttpServletResponse response, final Model model) throws IOException {
		OfflineParam offlineParam = (OfflineParam) WebUtil.getSession(OmniConstants.OFFLINEPARAM);
		return "redirect:/join?" + WebUtil.getOfflineParam(offlineParam);
	}

	@GetMapping("/claims")
	public String claims(final Model model) {
		String missingClaimList[] = null;
		String appName = null;
		if (StringUtils.hasText(WebUtil.getStringParameter(OmniConstants.MISSING_CLAIMS))) {
			missingClaimList = StringUtils.isEmpty(WebUtil.getStringParameter(OmniConstants.MISSING_CLAIMS))?new String[1]:WebUtil.getStringParameter(OmniConstants.MISSING_CLAIMS).split(",");
		}
		if (StringUtils.hasText(WebUtil.getStringParameter(OmniConstants.REQUEST_PARAM_SP))) {
			appName = WebUtil.getStringParameter(OmniConstants.REQUEST_PARAM_SP);
		}
		log.debug("▶▶▶▶▶▶ [wso2] claims : {}, {}", missingClaimList, appName);

		return "";
	}

	@GetMapping("/redirect-authz")
	public String redirectAuthz(@RequestParam(value = "chCd", required = false) String chCd, final Model model) {
		chCd = StringUtils.isEmpty(chCd) ? WebUtil.getStringSession(OmniConstants.CH_CD_SESSION) : chCd;
		model.addAttribute("authurl", this.ssoAuthorizeUrl);
		final Channel channel = this.commonService.getChannel(chCd);
		
		// channell Object Null Check & chCd Null Check - 2022-01-18 hjw0228
		if(channel == null || StringUtils.isEmpty(channel.getChCd())) {
			log.error("▶▶▶▶▶▶ [/redirect-authz] Channel Code is null");
			model.addAttribute("oauthErrorCode", "9999");
			model.addAttribute("oauthErrorMsg", "경로코드가 존재하지 않습니다.");
			model.addAttribute("message", "현재 서비스가 원할하지 않습니다.<br>잠시 후 다시 시도해주시기 바랍니다.");

			return "wso2/oauth2_error";
		}
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		return "wso2/redirect-auth";
	}

	@GetMapping("/redirect-authz-msg")
	public String redirectAuthzMessage(@RequestParam(value = "chCd", required = false) String chCd, @RequestParam("msg") String msg, final Model model) {
		chCd = StringUtils.isEmpty(chCd) ? WebUtil.getStringSession(OmniConstants.CH_CD_SESSION) : chCd;
		model.addAttribute("authurl", this.ssoAuthorizeUrl);
		final Channel channel = this.commonService.getChannel(chCd);
		
		if(Objects.nonNull(channel)) {
			model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
			model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		}else {
			log.info("session expired!!!");
			model.addAttribute("oauthErrorCode", "9999");
			model.addAttribute("oauthErrorMsg", "session data key must have value.");
			model.addAttribute("message", "현재 서비스가 원할하지 않습니다.<br>잠시 후 다시 시도해주시기 바랍니다.");

			return "wso2/oauth2_error";
		}
		
	
		if (StringUtils.hasText(msg)) {
			msg = this.messageSource.getMessage(msg, null, LocaleUtil.getLocale());
			model.addAttribute("message", msg);
		}
		return "wso2/redirect-auth-msg";
	}

	@GetMapping("/redirect-logout-home")
	public void redirectLogoutAndHome(final HttpServletResponse response, final HttpSession session) {

		String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chcd);
		try {
			response.sendRedirect(OmniUtil.getRedirectUrl(channel));
			return;
		} catch (IOException e) {
			// NO PMD
		}
	}

	@RequestMapping(value = "/moveauth", method = { RequestMethod.GET, RequestMethod.POST })
	public String moveAuth(final HttpServletRequest request, final HttpServletResponse response, final Model model) throws UnsupportedEncodingException {

		String mlogin = WebUtil.getStringSession(OmniConstants.LOGIN_MOBILE_TYPE);
		String mobile = "";
		if (StringUtils.hasText(mlogin)) {
			mobile = SecurityUtil.getXValue(mlogin, false);
			log.debug("▶▶▶▶▶▶ [moveauth] mobile login type : {}", mobile);
		}
		final String authType = this.config.commonAuthType();
		log.debug("▶▶▶▶▶▶ [moveauth] wso2 common auth type : {}", authType);
		final String encloginnm = WebUtil.getStringSession(OmniConstants.XNM_SESSION);
		final String encloginid = WebUtil.getStringSession(OmniConstants.XID_SESSION);
		final String encloginpw = WebUtil.getStringSession(OmniConstants.XPW_SESSION);
		final String encincsno = WebUtil.getStringSession(OmniConstants.XINCS_NO_SESSION);
		model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));

		if (mobile.equals(OmniConstants.LOGIN_MOBILE)) {

			if (StringUtils.isEmpty(encincsno)) {
				return "redirect:/go-login";
			}
			model.addAttribute(OmniConstants.XID_SESSION, SecurityUtil.getXValue(encloginnm, false));
			model.addAttribute(OmniConstants.XPW_SESSION, SecurityUtil.getXValue(encincsno, false));

			WebUtil.setCookies(response, OmniConstants.LAST_LOGIN_TYPE, "mobile");

			if (authType.equals(HttpMethod.GET.name())) {
				StringBuilder authurl = new StringBuilder();
				authurl.append(this.commonAuthUrl); //
				if (StringUtils.hasText(encloginnm)) {
					authurl.append("?fullName=").append(URLEncoder.encode(SecurityUtil.getXValue(encloginnm, false), StandardCharsets.UTF_8.name()));
				} else {
					authurl.append("?fullName=Dummy");
				}

				boolean autologin = "Y".equals(WebUtil.getStringSession(OmniConstants.AUTO_LOGIN_SESSION));
				model.addAttribute(OmniConstants.AUTO_LOGIN, autologin);
				if (autologin) {
					authurl.append("&chkRemember=on");
				}

				authurl.append("&incsNo=").append(URLEncoder.encode(SecurityUtil.getXValue(encincsno, false), StandardCharsets.UTF_8.name()));
				authurl.append("&sessionDataKey=").append(URLEncoder.encode(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION), StandardCharsets.UTF_8.name()));
				// log.debug("▶▶▶▶▶▶ [moveauth mobile] wso2 common auth url : {}", authurl.toString());
				return "redirect:" + authurl.toString();
			} else {
				model.addAttribute("actionurl", this.commonAuthUrl);
				return "cert/mobile-moveon";
			}
		} else {
			// log.debug("▶▶▶▶▶▶ [moveauth] wso2 common encloginid : {}, {}", SecurityUtil.getXValue(encloginid, false),
			// SecurityUtil.getXValue(encloginpw, false));
			if (StringUtils.isEmpty(encloginid) || StringUtils.isEmpty(encloginpw)) {
				return "redirect:/go-login";
			}

			model.addAttribute(OmniConstants.XID_SESSION, SecurityUtil.getXValue(encloginid, false));
			model.addAttribute(OmniConstants.XPW_SESSION, SecurityUtil.getXValue(encloginpw, false));

			boolean autologin = "Y".equals(WebUtil.getStringSession(OmniConstants.AUTO_LOGIN_SESSION));
			model.addAttribute(OmniConstants.AUTO_LOGIN, autologin);

			WebUtil.setCookies(response, OmniConstants.LAST_LOGIN_TYPE, "basic");

			if (authType.equals(HttpMethod.GET.name())) {

				StringBuilder authurl = new StringBuilder();
				authurl.append(this.commonAuthUrl); //
				authurl.append("?username=").append(URLEncoder.encode(SecurityUtil.getXValue(encloginid, false), StandardCharsets.UTF_8.name()));
				authurl.append("&password=").append(URLEncoder.encode(SecurityUtil.getXValue(encloginpw, false), StandardCharsets.UTF_8.name()));
				if (autologin) {
					authurl.append("&chkRemember=on");
				}
				authurl.append("&sessionDataKey=").append(URLEncoder.encode(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION), StandardCharsets.UTF_8.name()));
				// log.debug("▶▶▶▶▶▶ [moveauth] wso2 common auth url : {}", authurl.toString());
				return "redirect:" + authurl.toString();
			} else {
				model.addAttribute("actionurl", this.commonAuthUrl);
				model.addAttribute(OmniConstants.XID_SESSION, encloginid); // - 2022.08.25 ID / PW 암호화하여 로그인 처리
				model.addAttribute(OmniConstants.XPW_SESSION, encloginpw); // - 2022.08.25 ID / PW 암호화하여 로그인 처리
				model.addAttribute(OmniConstants.IS_ENCRYPTION, "true");
				WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
				return "cert/moveon";
			}
		}

	}

	public String getReferer(final HttpServletRequest request, final String chCd) {
		String referer = "";

		if (request.getHeader("referer") != null) {
			referer = request.getHeader("referer").toString();
		} else {
			if (request.getRequestURI().startsWith(request.getContextPath().concat("/oauth2AuthorizeUser")) || request.getRequestURI().startsWith(request.getContextPath().concat("/oauth2client"))
					|| request.getRequestURI().startsWith(request.getContextPath().concat("/bclogout"))) {
				try {
					// referer = new URL(request.getRequestURL().toString()).getPath();
					URL url = new URL(request.getRequestURL().toString());
					String scheme = url.getProtocol();
					String host = url.getHost();
					String port = String.valueOf(url.getPort());
					String context = request.getContextPath();

					referer = scheme + "://" + host + ("-1".equals(port) ? "" : ":" + port) + context;
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					log.error("error message {}",e.getMessage());
				}
			} else {
				if(StringUtils.isEmpty(chCd)) {
					referer = null;
				} else {
					final Channel channel = this.commonService.getChannel(chCd);
					referer = OmniUtil.getRedirectUrl(channel);
				}
			}
		}

		return referer;
	}

}
