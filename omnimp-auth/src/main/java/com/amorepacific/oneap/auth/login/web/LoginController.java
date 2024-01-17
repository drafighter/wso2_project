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
 * Date   	          : 2020. 7. 16..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.login.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.api.vo.ivo.AbusingLockVo;
import com.amorepacific.oneap.auth.api.vo.ivo.AuthKeyVo;
import com.amorepacific.oneap.auth.api.vo.ivo.UpdateCustCino;
import com.amorepacific.oneap.auth.api.vo.ovo.UpdateAbusingCiResponse;
import com.amorepacific.oneap.auth.cert.service.CertService;
import com.amorepacific.oneap.auth.cert.vo.CertData;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.ga.GaTaggingUtils;
import com.amorepacific.oneap.auth.ga.OmniGaTaggingConstants;
import com.amorepacific.oneap.auth.ga.vo.GaTagData;
import com.amorepacific.oneap.auth.join.service.JoinService;
import com.amorepacific.oneap.auth.login.service.LoginService;
import com.amorepacific.oneap.auth.login.vo.AuthVo;
import com.amorepacific.oneap.auth.login.vo.LoginResponse;
import com.amorepacific.oneap.auth.login.vo.LoginStatus;
import com.amorepacific.oneap.auth.login.vo.LoginVo;
import com.amorepacific.oneap.auth.login.vo.Web2AppCallBackVo;
import com.amorepacific.oneap.auth.login.vo.Web2AppVo;
import com.amorepacific.oneap.auth.mgmt.service.MgmtService;
import com.amorepacific.oneap.auth.social.handler.SnsAuth;
import com.amorepacific.oneap.auth.terms.service.TermsService;
import com.amorepacific.oneap.auth.terms.vo.TermsVo;
import com.amorepacific.oneap.auth.wso2.vo.TokenData;
import com.amorepacific.oneap.common.check.CheckResponse;
import com.amorepacific.oneap.common.check.Checker;
import com.amorepacific.oneap.common.check.actor.CheckActor;
import com.amorepacific.oneap.common.exception.OmniException;
import com.amorepacific.oneap.common.types.JoinDivisionType;
import com.amorepacific.oneap.common.types.LoginType;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.LocaleUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.LoginStepVo;
import com.amorepacific.oneap.common.vo.OfflineParam;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.StatusCheckResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.UpdateCustResponse;
import com.amorepacific.oneap.common.vo.api.UpdateCustVo;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.amorepacific.oneap.common.vo.sns.SnsType;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;
import com.amorepacific.oneap.common.vo.user.UserVo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.login 
 *    |_ LoginController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 16.
 * @version : 1.0
 * @author : takkies
 */

@Slf4j
@Controller
public class LoginController {

	@Value("${wso2.ssoauthorizeurl}")
	private String ssoAuthorizeUrl;

	@Value("${wso2.ssocommonauthurl}")
	private String commonAuthUrl;

	@Value("#{${sns.types}}")
	private Map<String, String> snsTypes;
	
	@Value("#{${sns.types.iosapp}}")
	private Map<String, String> snsTypesIOSApp;

	@Autowired
	private SnsAuth snsAuth;

	@Autowired
	private LoginService loginService;

	@Autowired
	private JoinService joinService;

	@Autowired
	private TermsService termsService;

	@Autowired
	private MgmtService mgmtService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private CustomerApiService customerApiService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private SystemInfo systemInfo;
	
	@Autowired
	private CertService certService;

	@Autowired
	private GaTaggingUtils gataggingUtils; // ga taagging util

	private ConfigUtil config = ConfigUtil.getInstance();

	// A0200 통합회원 로그인
	@SuppressWarnings("unchecked")
	@GetMapping({ "", "/login" })
	public String login( //
			final SSOParam ssoParam, //
			final OfflineParam offlineParam, //
			final HttpServletRequest request, //
			final HttpServletResponse res, final @CookieValue(value = OmniConstants.SAVE_ID_COOKIE_NAME, required = false) Cookie saveIdCookie, //
			final @CookieValue(value = OmniConstants.LOGIN_ID_COOKIE_NAME, required = false) Cookie loginIdCookie, //
			final @CookieValue(value = OmniConstants.LAST_LOGIN_TYPE, required = false) Cookie lastLloginCookie, final HttpSession session, //
			final Model model, //
			final RedirectAttributes redirectAttr) throws UnsupportedEncodingException {
		
		// 2022-09-27 SNS 로그인 완료 후 History Back 통해 재 로그인 시 임시 조치 (naver, facebook)
		boolean isSnsLoginComplete = WebUtil.getSession(OmniConstants.IS_LOGIN_COMPLETE) == null ? false : (boolean) WebUtil.getSession(OmniConstants.IS_LOGIN_COMPLETE);
		if(isSnsLoginComplete) {
			WebUtil.removeSession(OmniConstants.IS_LOGIN_COMPLETE);
			return "redirect:/redirect-authz?chCd=" + ssoParam.getChannelCd();
		}
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		session.invalidate(); // 로그인 페이지 진입 시 세션 초기화

		// TODO cookie 삭제로직이 있어 ga 태깅 에러
		// 로그인 페이지 진입 시 쿠키 값 제거 (one-ap-amore-channel)
		WebUtil.removeCookie(res, OmniConstants.ONE_AP_MOVE_CHANNEL);
		
		// redirectUri URL 디코딩 &amp; -> &
		if(!StringUtils.isEmpty(ssoParam.getRedirectUri())) {
			ssoParam.setRedirectUri(ssoParam.getRedirectUri().replaceAll("&amp;", "&"));
		}

		// 오프라인으로 진입하는 경우 SSO PARAM에 채널코드가 없어 오류 발생
		if (offlineParam != null && StringUtils.hasText(offlineParam.getChCd())) {
			ssoParam.setChannelCd(offlineParam.getChCd());
		} else { // 온라인으로 진입하는 경우 state 파라미터에서 redirectUri 및 cancelUri 추출 414 오류 대응
			Map<String, Object> state = new HashMap<String, Object>();
			String stateParam = WebUtil.getStringParameter(request, "state", "");
			log.debug("*** state parameter : [{}]", stateParam);

			Properties props = new Properties();
			try {
				if (!StringUtils.isEmpty(stateParam)) {
					props.load(new StringReader(stateParam.substring(1, stateParam.length() - 1).replace(", ", "\n")));

					for (Map.Entry<Object, Object> e : props.entrySet()) {
						state.put((String) e.getKey(), (String) e.getValue());
					}
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}

			if (state.get("redirectUri") != null && StringUtils.isEmpty(ssoParam.getRedirectUri())) {
				ssoParam.setRedirectUri(OmniUtil.getRedirectUrlFromStateMapAfterLogin(ssoParam.getChannelCd(), profile, state));
			}
			if (state.get("cancelUri") != null && StringUtils.isEmpty(ssoParam.getCancelUri())) {
				ssoParam.setCancelUri((String) state.get("cancelUri"));
			}
		}

		log.info("▶▶▶▶▶▶ [login page] sso param  : {}", StringUtil.printJson(ssoParam));
		log.info("▶▶▶▶▶▶ [login page] offline param : {}", StringUtil.printJson(offlineParam));

		// 파라미터에서 cancelUri 및 popup 여부 체크해서 세션에 저장 - 2021.02.03 허정환
		if (StringUtils.isEmpty(ssoParam.getCancelUri()))
			WebUtil.setSession(OmniConstants.CANCEL_URI, null);
		else
			WebUtil.setSession(OmniConstants.CANCEL_URI, ssoParam.getCancelUri());

		if (StringUtils.isEmpty(ssoParam.getPopup()))
			WebUtil.setSession(OmniConstants.POPUP, null);
		else
			WebUtil.setSession(OmniConstants.POPUP, ssoParam.getPopup());

		// 회원가입으로 파라미터를 달고 가는 경우,
		// oauth2AuthorizeUser 를 거치면서 join=true를 붙혀서 전송
		if (StringUtils.hasText(ssoParam.getJoin()) && StringUtil.isTrue(ssoParam.getJoin())) {

			log.info("################# [login page] sso join  : {}", ssoParam.getJoin());

			return "redirect:/join" + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
		}

		model.addAttribute("rv", this.config.resourceVersion());

		if (StringUtils.hasText(ssoParam.getAuthFailure()) && StringUtil.isTrue(ssoParam.getAuthFailure())) {
			if (StringUtils.hasText(ssoParam.getAuthFailureMsg())) {
				final String msg = this.messageSource.getMessage(ssoParam.getAuthFailureMsg(), null, LocaleUtil.getLocale());
				model.addAttribute("authFailure", SecurityUtil.clearXSSNormal(ssoParam.getAuthFailure()));
				model.addAttribute("authFailureMsg", SecurityUtil.clearXSSNormal(msg));
			}
		}

		// sns 정보 check
		if (StringUtils.hasText(ssoParam.getSnsId()) && StringUtils.hasText(ssoParam.getMappingSnsType())) {

			WebUtil.setSession(OmniConstants.SNS_FIRST_MAPPING_SNS_ID, ssoParam.getSnsId());
			WebUtil.setSession(OmniConstants.SNS_FIRST_MAPPING_SNS_TYPE, ssoParam.getMappingSnsType());

			model.addAttribute("mappingNotice", true);
			model.addAttribute("mappingSnsType", ssoParam.getMappingSnsType());
			String snsAcessToken = ssoParam.getSnsAccesstoken();
			WebUtil.setSession(OmniConstants.SNS_ACCESS_TOKEN, snsAcessToken);
		} else if (StringUtils.hasText(ssoParam.getSnsError())) {
			model.addAttribute("snsError", SecurityUtil.clearXSSNormal(ssoParam.getSnsError()));
		}

		if (StringUtils.isEmpty(ssoParam.getChannelCd())) {

			throw new OmniException("경로코드가 존재하지 않습니다.");

		} else {

			List<Channel> channels = this.commonService.getChannels();
			List<String> channelCds = new ArrayList<>();
			for (Channel channel : channels) {
				channelCds.add(channel.getChCd());
			}

			if (!channelCds.contains(ssoParam.getChannelCd())) {

				throw new OmniException("경로코드가 올바르지 않습니다. 정확한 경로인지 확인하세요.[" + ssoParam.getChannelCd().replaceAll("(?i)script|object|applet|embed|form|alert|href|cookie|input|src|fromcharcode|encodeuri|encodeuricomponent|expression|iframe|window|location|style|eval","").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>","").replaceAll("\r|\n|&nbsp;","") + "]");
			}

			WebUtil.setSession(OmniConstants.CH_CD_SESSION, ssoParam.getChannelCd());
		}

		final Channel channel = this.commonService.getChannel(ssoParam.getChannelCd());

		WebUtil.setSession(OmniGaTaggingConstants.CD21, channel.getChCdNm());
		WebUtil.setSession(OmniGaTaggingConstants.CD22, channel.getChCd());

		WebUtil.setSession(OmniConstants.Client_IP, request.getRemoteAddr());
		WebUtil.setSession(OmniConstants.Client_Agent, request.getHeader("user-agent"));

		log.debug("▶▶▶▶▶▶ [login page] channel info : {}", StringUtil.printJson(channel));
		boolean isOffline = OmniUtil.isOffline(channel); // 오프라인인 경우 로그인하지 않으므로 체크 불필요
		
		model.addAttribute("orderurl", OmniUtil.getOrderUrl(channel, profile));
		model.addAttribute("url", OmniUtil.getRedirectUrl(channel));

		// 세션데이터키가 없으면 로그인 할 수 없으므로 이 값에 대해서 반드시 validation 체크해야함.
		// 중요) 이 값은 WSO2에서 받아오지 않은 값이면 SSO 처리가 되지 않음.
		if (!isOffline && StringUtils.isEmpty(ssoParam.getSessionDataKey())) {

			throw new OmniException("인증을 위한 정보가 존재하지 않습니다.");

		}

		Map<String, String> idpAuthenticatorMapping = null;
		if (request.getAttribute(OmniConstants.IDP_AUTHENTICATOR_MAP) != null) {
			idpAuthenticatorMapping = (LinkedHashMap<String, String>) request.getAttribute(OmniConstants.IDP_AUTHENTICATOR_MAP);
		}
		// SNS Authenticator를 못받아올 경우 config에서 설정
		if (idpAuthenticatorMapping == null || idpAuthenticatorMapping.isEmpty()) {
			idpAuthenticatorMapping = this.snsTypes;
		}
		
		// Apple 심사를 위하여 iOS App 인경우 idpAuthenticatorMapping 순서 변경
		if (OmniUtil.isIOSApp(request) && idpAuthenticatorMapping.containsKey("AP")) {
			idpAuthenticatorMapping = this.snsTypesIOSApp;
			//apple 디자인 변경
			model.addAttribute("isiosapp", true);
		}

		final String queryString = request.getQueryString();
		if (StringUtils.hasText(queryString)) {
			// ssoParam.setQueryString(queryString);
			ssoParam.setQueryString(queryString);
		}

		final LoginResponse response = this.loginService.loginCheck(ssoParam, idpAuthenticatorMapping);

		if (response != null) {
			WebUtil.setSession(OmniConstants.SESSION_DATA_KEY_SESSION, ssoParam.getSessionDataKey());
			WebUtil.setSession(OmniConstants.SSOPARAM, ssoParam);
			// response.setSessionDataKey(ssoParam.getSessionDataKey()); // 화면에 sessionDataKey를 전달(로그인시 필수값)
			response.setSessionDataKey(SecurityUtil.clearXSSNormal(ssoParam.getSessionDataKey())); // 화면에 sessionDataKey를 전달(로그인시 필수값)
		}
		if (StringUtils.hasText(request.getQueryString())) {
			response.setQueryString("?" + request.getQueryString());
		}

		boolean isMobile = WebUtil.isMobile();
		if (isMobile) {

			// cookie 정보에 아이디 저장 정보가 있으면 해당 정보로 자동 로그인 기능도 있는지 체크
			if (saveIdCookie != null) {
				final String saveIdCookieVal = URLDecoder.decode(saveIdCookie.getValue(), StandardCharsets.UTF_8.name());
				if (StringUtils.hasText(saveIdCookieVal)) {
					response.setSaveId(SecurityUtil.getXValue(saveIdCookieVal));
					response.setUsername(saveIdCookieVal);
					// final Cookie cookie = WebUtil.getCookies(request, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + saveIdCookieVal);
					// if (cookie != null) {
					// final String autologin = cookie.getValue();
					// if (StringUtils.hasText(autologin) && "Y".equals(autologin)) {
					// response.setAutoLoginOption(true);
					// }
					// }
				}
			}

		} else {
			if (saveIdCookie != null) {
				final String saveIdCookieVal = URLDecoder.decode(saveIdCookie.getValue(), StandardCharsets.UTF_8.name());
				if (StringUtils.hasText(saveIdCookieVal)) {
					response.setUsername(saveIdCookieVal);
				}
				
				// 로그인 페이지 진입 시 쿠키 값 제거 (one-ap_auto-login-saveIdCookieValsaveIdCookieVal)
				final Cookie cookie = WebUtil.getCookies(request, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + saveIdCookieVal);
				if (cookie != null) {
					WebUtil.removeCookie(res, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + saveIdCookieVal);
				}
			}
		}

		// 최근 로그인 타입
		if (lastLloginCookie != null) {
			final String lastLoginCookieVal = lastLloginCookie.getValue();
			if (StringUtils.hasText(lastLoginCookieVal)) {
				model.addAttribute("lastlogin", lastLoginCookieVal); // 최근 로그인
			}
		}

		// 비회원 주문/조회 활성화 여부 false면 비활성화
		model.addAttribute("isNonMemberEnable", config.isChannelNonMemberEnable(ssoParam.getChannelCd(), profile));
		
		// 비회원 주문/조회 명칭 변경이 필요한 경우
		model.addAttribute("nonMemberName", config.isChannelNonMemberName(ssoParam.getChannelCd(), profile));
		
		// 로그인 페이지 헤더 활성화 여부 false면 비활성화
		model.addAttribute("isLoginPageHeader", config.isLoginPageHeader(ssoParam.getChannelCd(), profile));
		
		// SMS 서버 점검 시 아이디 찾기, 휴대폰 로그인 점검 안내 팝업
		model.addAttribute("isSmsSystemCheck", config.isSmsSystemCheck(profile));

		model.addAttribute("mobile", isMobile); // 모바일 - 자동로그인

		// log.debug("▶▶▶▶▶▶ [login page] loginIdCookie : {}", StringUtil.printJson(loginIdCookie));
		if (loginIdCookie != null) {
			final String loginidCookieVal = URLDecoder.decode(loginIdCookie.getValue(), StandardCharsets.UTF_8.name());
			if (StringUtils.hasText(loginidCookieVal)) {
				model.addAttribute("settingid", loginidCookieVal);
				response.setUsername(loginidCookieVal);
			}
		}
		final SSOParam responsSSoParam = SecurityUtil.clearXssSsoParam(ssoParam);
		response.setSsoParam(responsSSoParam); // localStorage 에 저장위해 필요

		// log.debug("▶▶▶▶▶▶ [login page] response : {}", StringUtil.printJson(response));

		model.addAttribute("login", response);
		model.addAttribute("offline", isOffline);
		model.addAttribute(OmniConstants.CH_CD, SecurityUtil.clearXSSNormal(ssoParam.getChannelCd()));
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("qs", request.getQueryString());

		// - 512 보다 크거나 같으면 SNS 인증 화면 Disable
		// - 512 보다 작으면 SNS 인증 화면 Enable
		String strVt = StringUtils.isEmpty(ssoParam.getVt()) ? "500" : ssoParam.getVt();
		int vt = StringUtil.isNumeric(strVt) ? Integer.parseInt(strVt) : 500;
		model.addAttribute("vtdisable", vt >= 512 ? true : false);

		String cookiequeryString = "";

		if (StringUtils.hasText(ssoParam.getChannelCd())) {
			cookiequeryString += "&channelCd=" + ssoParam.getChannelCd();
		}
		if (StringUtils.hasText(ssoParam.getCancelUri())) {
			cookiequeryString += "&cancelUri=" + ssoParam.getCancelUri();
		}
		
		if (StringUtils.hasText(ssoParam.getIdSearch()) && StringUtil.isTrue(ssoParam.getIdSearch())) { // ID 찾기 파라미터 존재할 경우
			return "redirect:/search/id?channelCd=" + ssoParam.getChannelCd();
		}
		
		if (StringUtils.hasText(ssoParam.getPwSearch()) && StringUtil.isTrue(ssoParam.getPwSearch())) { // PW 찾기 파라미터 존재할 경우
			return "redirect:/search/pwd?channelCd=" + ssoParam.getChannelCd();
		}
		
		// ChannelLoginUrl 이 존재할 경우 WSO2 거치지 않고 다이렉트로 이동, 이동 시 사용자 아이디는 암호화 처리
		StringBuilder channelLoginUrl = new StringBuilder();
		channelLoginUrl.append(config.getChannelLoginUrl(ssoParam.getChannelCd(), profile));
		if (StringUtils.hasText(channelLoginUrl)) { // ChannelLoginUrl 이 존재할 경우 WSO2 거치지 않고 다이렉트로 이동, 이동 시 사용자 아이디는 암호화 처리
			String username = "";
			if(loginIdCookie != null) username = URLDecoder.decode(loginIdCookie.getValue(), StandardCharsets.UTF_8.name());
			if(saveIdCookie != null) username = URLDecoder.decode(saveIdCookie.getValue(), StandardCharsets.UTF_8.name());
			
			channelLoginUrl.append("?paramId=");
			
			if(StringUtils.hasText(username)) {
				channelLoginUrl.append(URLEncoder.encode(SecurityUtil.setXyzValue(username), StandardCharsets.UTF_8.name()));
			}
			
			return "redirect:" + channelLoginUrl.toString();
		}

		// 카카오 임베디드로 파라미터를 달고 가는 경우 2021-08-04 hjw0228
		// oauth2AuthorizeUser 를 거치면서 kakaoEmbedded=true를 붙여서 전송
		if (StringUtils.hasText(ssoParam.getKakaoEmbedded()) && StringUtil.isTrue(ssoParam.getKakaoEmbedded())) {
			if (StringUtils.hasText(cookiequeryString)) {
				return "redirect:/sns/login_start?snsType=KA&kakaoEmbedded=true" + cookiequeryString;
			}
			return "redirect:/sns/login_start?snsType=KA&kakaoEmbedded=true";
		}

		if (response.getStatus() == LoginStatus.REDIRECT.getCode()) {

			return "redirect:" + response.getRedirectUrl();
		} else if (response.getStatus() == LoginStatus.FAIL.getCode()) {

			return "redirect:/errors";
		} else if (response.getStatus() == LoginStatus.EXISTUSER.getCode()) {

		}

		// Facebook SDK 사용을 위한 FBRestApiKey 전달
		model.addAttribute("FBRestApiKey", this.config.getSnsInfo(profile, SnsType.FACEBOOK.getType().toLowerCase(), "restkey"));

		// Andorid App 에서 Facebook Login 노출되지 않도록 임시로 조치 - 2021.11.24
		boolean isAndroidApp = WebUtil.isAndroidApp();
		if (isMobile && isAndroidApp) {
			model.addAttribute("isAndroidApp", true);
		}
		
		if(StringUtils.hasText(ssoParam.getDrcLgnTp())) {
			if("mobile".equals(ssoParam.getDrcLgnTp().toLowerCase())) { // 모바일 일 경우 Mobile 로그인 페이지로
				return "redirect:/plogin-param";
			} else { // 모바일이 아닌 경우 SNS 로그인 페이지로
				return "redirect:/sns/login_start?" + ssoParam.getQueryString() + "&snsType=" + ssoParam.getDrcLgnTp().toUpperCase();
			}
		} 		
		
		boolean isInniMobileBackAction = OmniUtil.isInniMobileBackAction(request);
		model.addAttribute("innimobileAction", isInniMobileBackAction);
		boolean isBeautyAngelMobileBackAction = OmniUtil.isBeautyAngelMobileBackAction(request);
		model.addAttribute("beautyAngelmobileAction", isBeautyAngelMobileBackAction);
		boolean isAmoreMallAOS = OmniUtil.isAmoreMallAOS(request);
		model.addAttribute("isAmoreMallAOS", isAmoreMallAOS);
		boolean isAmoreMallIOS = OmniUtil.isAmoreMallIOS(request);
		model.addAttribute("isAmoreMallIOS", isAmoreMallIOS);
		
		model.addAttribute("actionurl", this.commonAuthUrl);
		if(!(OmniUtil.isMobileApp(request) || OmniUtil.isIOSApp(request) || WebUtil.isAndroidApp() || OmniUtil.isAmoreMallAOS(request))) {   //앱에서는 web2app 로그인 버튼 안나오게
			if(StringUtils.isEmpty(config.isAppLoginPath(ssoParam.getChannelCd(), profile))) {
				String isAppLogin = config.isAppLogin(ssoParam.getChannelCd(), profile);
				model.addAttribute("appLogin", isAppLogin);
				model.addAttribute("appLoginChNm",channel.getChCdNm());
			}else {
				//앱이 없는 채널 : 지정한 채널의 앱으로
				String isAppLogin = config.isAppLogin(config.isAppLoginPath(ssoParam.getChannelCd(), profile), profile);
				final Channel appchannel = this.commonService.getChannel(config.isAppLoginPath(ssoParam.getChannelCd(), profile));
				model.addAttribute("appLoginChNm",appchannel.getChCdNm());
				model.addAttribute("appLogin", isAppLogin);
				model.addAttribute("appLoginPathChCd", config.isAppLoginPath(ssoParam.getChannelCd(), profile));
			}
		}

		return "login/login";
	}

	/**
	 * 
	 * <pre>
	 * O O O 뷰티포인트 정상 로그인 처리
	 * O O O 경로 자체 고객 뷰티포인트 ID 가 1 개 A0204 단일 계정 화면 약관 비노출
	 * O O O 경로 자체 고객 뷰티포인트 ID 가 2 개이상 A0204 복수 계정 화면 약관 비노출
	 * O O X 뷰티포인트 A0105
	 * O X O 경로 자체 고객 동일 ID 사용가능 A0202
	 * O X O 경로 자체 고객 동일 ID 타인사용 A0203
	 * O X X 휴대폰 휴대폰로그인 A0207 채널약관 동의 목록 노출 화면
	 * O X O 휴대폰 휴대폰로그인 A0207
	 * X X O 경로 자체 고객 경로 자체 고객자체회원 A0201 전환가입 화면 전환가입으로 케이스로 이동
	 * </pre>
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping("/login/step")
	// @RequestMapping(value = "/login/step", method = { RequestMethod.GET, RequestMethod.POST })
	public String loginStep( //
			final AuthVo authVo, //
			final SSOParam ssoParam, //
			final HttpServletRequest request, //
			final HttpServletResponse response, //
			final HttpSession session, //
			final Model model, //
			final Locale locale) throws UnsupportedEncodingException {

		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final String sessionDataKey = WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION);
		if (StringUtils.isEmpty(sessionDataKey)) {
//			throw new IllegalArgumentException("session data key must have value!!!");

			log.info("session data key must have value!!!");
			model.addAttribute("oauthErrorCode", "9999");
			model.addAttribute("oauthErrorMsg", "session data key must have value.");
			model.addAttribute("message", "세션이 만료되었습니다.재접속 해주세요!");

			return "wso2/oauth2_error";

		} else {
			// authVo.setSessionDataKey(sessionDataKey);
		}

		log.debug("▶▶▶▶▶▶ [login step] authVo : {}", StringUtil.printJson(authVo));

		final String xid = StringUtils.hasText(authVo.getXid()) ? authVo.getXid() : WebUtil.getStringSession(OmniConstants.XID_SESSION);
		final String xpw = StringUtils.hasText(authVo.getXpw()) ? authVo.getXpw() : WebUtil.getStringSession(OmniConstants.XPW_SESSION);

		if (StringUtils.hasText(xid)) {
			WebUtil.setSession(OmniConstants.XID_SESSION, xid);
		}
		if (StringUtils.hasText(xpw)) {
			WebUtil.setSession(OmniConstants.XPW_SESSION, xpw);
		}

		model.addAttribute("rv", this.config.resourceVersion());

		final String loginid = SecurityUtil.getXValue(xid, false);
		final String loginpw = SecurityUtil.getXValue(xpw, false);

		final String cookieloginid = URLEncoder.encode(loginid.trim(), StandardCharsets.UTF_8.name());

		// 아이디 저장 옵션
		// TODO 전환 사용자인 경우 아이디 선택에 따라서 아이디 저장 쿠키 삭제할 필요있음.
		// 암호화 하여 사용
		if (StringUtils.hasText(authVo.getIdSaveOption()) && "Y".equals(authVo.getIdSaveOption())) {
			WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, cookieloginid);
			// WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, SecurityUtil.setXyzValue(loginid.trim()));
		} else {
			WebUtil.removeCookie(response, OmniConstants.SAVE_ID_COOKIE_NAME);
			WebUtil.removeCookie(response, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + cookieloginid);
			WebUtil.setSession(OmniConstants.AUTO_LOGIN_SESSION, "N");
		}

		// 모바일인 경우만 처리 - 자동로그인 옵션 선택 - commonauth 에 자동 로그인 처리옵션 추가
		boolean isMobile = WebUtil.isMobile();

		if (isMobile) {

			if (StringUtils.hasText(authVo.getAutoLoginOption()) && "Y".equals(authVo.getAutoLoginOption())) {
				if (StringUtils.hasText(authVo.getIdSaveOption()) && "Y".equals(authVo.getIdSaveOption())) {
					WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, cookieloginid);
					// WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, SecurityUtil.setXyzValue(loginid.trim()));
				} else {
					WebUtil.removeCookie(response, OmniConstants.SAVE_ID_COOKIE_NAME);
				}
				log.info("▶▶▶▶▶▶ [login step] auto login set...");
				WebUtil.setCookies(response, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + cookieloginid, authVo.getAutoLoginOption());
				WebUtil.setSession(OmniConstants.AUTO_LOGIN_SESSION, "Y");
			} else {
				if (StringUtils.hasText(authVo.getIdSaveOption()) && "Y".equals(authVo.getIdSaveOption())) {
					WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, cookieloginid);
					// WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, SecurityUtil.setXyzValue(loginid.trim()));
				} else {
					WebUtil.removeCookie(response, OmniConstants.SAVE_ID_COOKIE_NAME);
				}
				WebUtil.removeCookie(response, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + cookieloginid);
				WebUtil.setSession(OmniConstants.AUTO_LOGIN_SESSION, "N");
			}

		} else {
			WebUtil.removeCookie(response, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + cookieloginid); // 모바일이 아닌 경우는 자동로그인 없음.
		}

		if (StringUtils.isEmpty(loginid.trim()) || StringUtils.isEmpty(loginpw)) {
			log.warn("▶▶▶▶▶▶ [login step] login info(id or pws) is null.....");
			final String ssoParams = WebUtil.getSsoParams();
			final String entryPage = "/entry?" + ssoParams;
			WebUtil.removeCookie(response, OmniConstants.SAVE_ID_COOKIE_NAME);
			WebUtil.removeCookie(response, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + cookieloginid);
			return "redirect:" + entryPage;
		}

		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel)); // add
		model.addAttribute("chcd", channel.getChCd());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("entry", OmniUtil.getOfflineParam());

		/*
		 * ga tagging dto setup basic setup <pre> 아이디 로그인 처리 </pre>
		 */
		WebUtil.setSession(OmniGaTaggingConstants.EL, "아이디");
		WebUtil.setSession(OmniGaTaggingConstants.LOGIN_BASIC, "아이디");
		// 로그인 경로 약관 등록 중단 때문에 정의
		WebUtil.setSession(OmniGaTaggingConstants.CD21, channel.getChCdNm());
		WebUtil.setSession(OmniGaTaggingConstants.CD22, channel.getChCd());

		/**
		 * 
		 * <pre>
		 * O O O 뷰티포인트 정상 로그인 처리
		 * O O O 경로 자체 고객 뷰티포인트 ID 가 1 개 A0204 단일 계정 화면 약관 비노출
		 * O O O 경로 자체 고객 뷰티포인트 ID 가 2 개이상 A0204 복수 계정 화면 약관 비노출
		 * O O X 뷰티포인트 A0105
		 * O X O 경로 자체 고객 동일 ID 사용가능 A0202
		 * O X O 경로 자체 고객 동일 ID 타인사용 A0203
		 * O X X 휴대폰 휴대폰로그인 A0207 채널약관 동의 목록 노출 화면
		 * O X O 휴대폰 휴대폰로그인 A0207
		 * X X O 경로 자체 고객 경로 자체 고객자체회원 A0201 전환가입 화면 전환가입으로 케이스로 이동
		 * </pre>
		 */

		final LoginStepVo loginstep = this.loginService.loginStep(chCd, loginid.trim(), loginpw, false);

		LoginType loginType = loginstep.getLoginType();
		final List<UmOmniUser> omniUsers = loginstep.getOmniUsers();
		final List<UmChUser> chUsers = loginstep.getChUsers();
		final String loginIncsNo = loginstep.getIncsNo();
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;

		log.debug("▶▶▶▶▶▶ [login step] lognin type : {}", StringUtil.printJson(loginstep));
		
		final Map<String,String> gaCookieMap = gataggingUtils.getGaCookieMap(request); //ga get cookie	 

		if (loginType == LoginType.PWDCHANGE) {

			model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, sessionDataKey);
			model.addAttribute(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginid.trim()));
			model.addAttribute(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(loginpw));
			model.addAttribute(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(loginIncsNo));

			return "mgmt/change_pwd_campaign";
		} else if (loginType == LoginType.PWDRESET) {

			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(omniUsers.get(0).getFullName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(omniUsers.get(0).getUmUserName()));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(loginIncsNo));

			return "redirect:/mgmt/reset-pwd";
		} else if (loginType == LoginType.LOGIN) {

			if (omniUsers != null && omniUsers.size() > 0) {
				UmOmniUser omniUser = omniUsers.get(0);
				omniUser.setChCd(chCd);
				if (!this.termsService.hasCorpTermsAgree(omniUser)) { // 전사약관 미동의상태
					// 진입 채널에 대한 약관 출력 정보 조회
					TermsVo termsVo = new TermsVo();
					termsVo.setChCd(channel.getChCd());
					List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
					model.addAttribute("terms", termsList);
					model.addAttribute("corpterms", true);
					String name = OmniUtil.maskUserName(omniUser.getFullName(), locale);
					final String id = omniUser.getUmUserName(); // 약관동의에서 아이디는 마스킹하지 않음.
					// 휴대전화번호는 고객통합을 조회
					CustInfoVo custInfoVo = new CustInfoVo();
					custInfoVo.setIncsNo(omniUser.getIncsNo());

					final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);

					if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
						omniUser.setFullName(customer.getCustNm());
						name = OmniUtil.maskUserName(customer.getCustNm(), locale);
						final String mobileNo = StringUtil.mergeMobile(customer);
						final String mobile = OmniUtil.maskMobile(mobileNo, locale);
						model.addAttribute("mobile", mobile);
					} else {
						if (customer != null && "ICITSVCOM001".equals(customer.getRsltCd())) {

							throw new OmniException("회원 정보가 존재하지 않습니다.");
						}
					}

					model.addAttribute("incsno", omniUser.getIncsNo());
					model.addAttribute("incsNo", omniUser.getIncsNo());
					model.addAttribute("xincsno", SecurityUtil.setXyzValue(omniUser.getIncsNo()));
					model.addAttribute("name", name);
					model.addAttribute("id", id);
					model.addAttribute("xname", SecurityUtil.setXyzValue(omniUser.getFullName()));
					model.addAttribute("xid", SecurityUtil.setXyzValue(id));

					if (StringUtils.hasText(customer.getMbrJoinDt())) {
						model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
					} else {
						if (StringUtils.hasText(omniUser.getCreatedDate())) {
							model.addAttribute("joindate", DateUtil.getBirthDate(omniUser.getCreatedDate()));
						}
					}

					model.addAttribute("home", channel.getHmpgUrl());
					model.addAttribute("homeurl", channel.getHmpgUrl());

					return "terms/login_new_terms_apply"; // ME-FO-A0214
				}
			}

			model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, sessionDataKey);
			model.addAttribute(OmniConstants.XID_SESSION, loginid.trim());
			model.addAttribute(OmniConstants.XPW_SESSION, loginpw);
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(loginIncsNo));

			model.addAttribute("actionurl", this.commonAuthUrl);

			if (StringUtils.hasText(ssoParam.getAuthFailure()) && StringUtil.isTrue(ssoParam.getAuthFailure())) {
				if (StringUtils.hasText(ssoParam.getAuthFailureMsg())) {
					final String msg = this.messageSource.getMessage(ssoParam.getAuthFailureMsg(), null, LocaleUtil.getLocale());
					model.addAttribute("authFailure", ssoParam.getAuthFailure());
					model.addAttribute("authFailureMsg", msg);
				}
			}

			// sns 맵핑
			String snsType = "";
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				} else {
					snsType = snsParam.getSnsType();
				}
				snsParam.setLoginId(loginid);
				snsParam.setIncsNo(loginIncsNo);

				snsAuth.doSnsMapping(snsParam);
			}
			
			// 2022-07-08 Facebook의 테스트 계정으로 유입 시 강제 리다이렉트
			if(StringUtils.isEmpty(snsType)) {
				Object ssoObj = WebUtil.getSession(OmniConstants.SSOPARAM);
				if (ssoObj != null) {
					snsType = ((SSOParam) ssoObj).getMappingSnsType();
				}
				
			}

			// 자동 로그인 처리되면 로그인 정보(commonAuthId)가 컴퓨터에 저장됨.

			boolean autologin = OmniUtil.getAutoLogin(request, loginid);

			if (autologin) {
				log.debug("▶▶▶▶▶ [login step] save auto login? {}", autologin);
				WebUtil.setSession(OmniConstants.AUTO_LOGIN_SESSION, "Y");
				model.addAttribute(OmniConstants.AUTO_LOGIN, true);
			}
			final String authType = this.config.commonAuthType();
			log.debug("▶▶▶▶▶▶ [login step] wso2 common auth type : {}", authType);

			WebUtil.setCookies(response, OmniConstants.LAST_LOGIN_TYPE, "basic");

			try {

				if (!autologin) { // 자동로그인 처리된 경우 Ga Tagging 미처리 cid값이 없기 때문에 제외 처리
					
					log.debug("▶▶▶▶▶▶  GA Tagging LOGIN START(/login/step) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
							,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());
					
					GaTagData gaTagDto = GaTagData.builder()
                            .cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
                            .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
                            .el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
                            .loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
                            .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
                            .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
                            .chCd(channel.getChCd())
                            .chCdNm(channel.getChCdNm())
                            .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_START)
                            .sendFlag("Y")
                            .incsNo(loginIncsNo)
                            .sessionId(session.getId())
                            .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
                            .loginId(loginid).build();
					
					gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);
				}else {
					log.debug("▶▶▶▶▶▶ [login step] auto login type ga tagging skip login id : {}", loginid);
				}

			} catch (Exception ex) {
				log.error(ex.getMessage());
			}

			if (authType.equals(HttpMethod.GET.name())) {
				StringBuilder authurl = new StringBuilder();
				authurl.append(this.commonAuthUrl); //
				authurl.append("?username=").append(URLEncoder.encode(loginid, StandardCharsets.UTF_8.name()));
				authurl.append("&password=").append(URLEncoder.encode(loginpw, StandardCharsets.UTF_8.name()));
				if (autologin) {
					authurl.append("&chkRemember=on");
				}
				authurl.append("&sessionDataKey=").append(URLEncoder.encode(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION), StandardCharsets.UTF_8.name()));
				// log.debug("▶▶▶▶▶▶ [login step] wso2 common auth url : {}", authurl.toString());
				try {

					
					if (!autologin) { // 자동로그인 처리된 경우 Ga Tagging 미처리 cid값이 없기 때문에 제외 처리

						log.debug("▶▶▶▶▶▶  GA Tagging LOGIN SUCCESS(/login/step 자동로그인이 아니면 ) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
								,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());
						
						GaTagData gaTagDto = GaTagData.builder()
	                            .cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
	                            .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
	                            .el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
	                            .loginType((String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_BASIC))
	                            .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
	                            .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
	                            .chCd(channel.getChCd())
	                            .chCdNm(channel.getChCdNm())
	                            .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_SUCCESS)
	                            .sendFlag("Y")
	                            .incsNo(loginIncsNo)
	                            .sessionId(session.getId())
	                            .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
	                            .loginId(loginid).build();

						gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);

					}else {
						log.debug("▶▶▶▶▶▶ [login step] auto login type ga tagging skip login id : {}", loginid);
					}
				} catch (Exception ex) {
					log.error(ex.getMessage());
				}
				return "redirect:" + authurl.toString();
			} else {
				try {

					if (!autologin) { // 자동로그인 처리된 경우 Ga Tagging 미처리 cid값이 없기 때문에 제외 처리
						
					
					log.debug("▶▶▶▶▶▶  GA Tagging LOGIN SUCCESS(/login/step 아이디:cert/moveon ) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
							,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());
					
					
					GaTagData gaTagDto = GaTagData.builder()
                            .cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
                            .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
                            .el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
                            .loginType((String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_BASIC))
                            .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
                            .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
                            .chCd(channel.getChCd())
                            .chCdNm(channel.getChCdNm())
                            .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_SUCCESS)
                            .sendFlag("Y")
                            .incsNo(loginIncsNo)
                            .sessionId(session.getId())
                            .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
                            .loginId(loginid).build();
					
					gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);
					
					}else {
						log.debug("▶▶▶▶▶▶ [login step] auto login type ga tagging skip login id : {}", loginid);
					}
					
				} catch (Exception ex) {
					log.error(ex.getMessage());
				}
				model.addAttribute("actionurl", this.commonAuthUrl);
				model.addAttribute(OmniConstants.XID_SESSION, xid); // - 2022.08.25 ID / PW 암호화하여 로그인 처리
				model.addAttribute(OmniConstants.XPW_SESSION, xpw); // - 2022.08.25 ID / PW 암호화하여 로그인 처리
				model.addAttribute(OmniConstants.IS_ENCRYPTION, "true");
				WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
				return "cert/moveon";
			}
		} else if (loginType == LoginType.CONV_JOIN) { // A0201 X X O 전환가입

			if (chUsers != null && !chUsers.isEmpty()) {
				UmChUser chUser = chUsers.get(0);
				if (chUser != null) {
					String id = chUser.getChcsWebId();
					WebUtil.setSession("chcsWebId", id); // 자체회원인 경우 이 값으로 경로 임시 테이블 검색해야함.
					String name = null;

					Map<String, String> loginMap = this.customerApiService.getChannelUser(chUser);

					if (loginMap != null && !loginMap.isEmpty()) {
						name = loginMap.get("name");
						if (StringUtils.hasText(name)) {
							name = OmniUtil.maskUserName(name, locale);
						}
					}
					StringBuilder nameid = new StringBuilder(name);
					if (StringUtils.hasText(id)) {
						id = OmniUtil.maskUserId(id);
						nameid.append("(").append(id).append(")");
					}
					model.addAttribute("nameid", nameid.toString());
				}
			}

			// 다음에 하기 시 home 페이지로 이동
			model.addAttribute("home", channel.getHmpgUrl());
			model.addAttribute("homeurl", channel.getHmpgUrl());

			return "convs/conversion";
		} else if (loginType == LoginType.INTG_JOIN) { // 통합가입 A0204 O O O


			List<UmOmniUser> chUserList = new ArrayList<>();
			for (UmChUser chusr : chUsers) {
				UmOmniUser ou = new UmOmniUser();
				ou.setUmUserName(chusr.getChcsWebId());
				ou.setFullName(omniUsers.get(0).getFullName());
				chUserList.add(ou);
			}

			model.addAttribute("xincsno", SecurityUtil.setXyzValue(omniUsers.get(0).getIncsNo()));
			model.addAttribute("bpuserlist", omniUsers);
			model.addAttribute("bpusersize", omniUsers.size());
			model.addAttribute("chuserlist", chUserList);
			model.addAttribute("trnsType", OmniConstants.TRNS_TYPE_INTEGRATE);
			TermsVo termsVo = new TermsVo();
			termsVo.setChCd(channel.getChCd());
			List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
			model.addAttribute("corptermslist", termsList);
			
			//20230323 채널 문자 수신 동의
			final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
			WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
			TermsVo corpTermsVo2 = new TermsVo();
			corpTermsVo2.setChCd(onlineChCd);
			UmOmniUser omniUser2 = new UmOmniUser();
			omniUser2.setChCd(onlineChCd);
			if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
				TermsVo termsVo2 = new TermsVo();
				termsVo2.setChCd(onlineChCd);
				List<TermsVo> termsList2 = this.termsService.getTermsChoice(termsVo2);
//				model.addAttribute("terms", termsList2);
				for(TermsVo vo : termsList2) {
					if(vo.getMkSn()==-20) {
						model.addAttribute("terms_marketing", vo);
					}
				}
			}
			
			return "convs/conversion_terms_a0204";

		} else if (loginType == LoginType.TRNS_JOIN) { // 전환가입 A0202, A0203 O X O

			model.addAttribute("trnsType", OmniConstants.TRNS_TYPE_TRANSFORM);
			// 옴니에 동일 아이디가 있는지 체크
			UmChUser sameIdChUser = new UmChUser();
			sameIdChUser.setChCd(chCd);
			sameIdChUser.setIncsNo(Integer.parseInt(loginIncsNo));
			final boolean same = this.mgmtService.hasSameLoginId(sameIdChUser);

			log.debug("[login step] use same login id already ? {}", same);

			if (same) { // 동일 아이디 타인 사용 : A0203


				if (chUsers != null && !chUsers.isEmpty()) {
					UmChUser chUser = chUsers.get(0);
					model.addAttribute("loginid", chUser.getChcsWebId());
					if (chUser.getIncsNo() > 0) {
						model.addAttribute("xincsno", SecurityUtil.setXyzValue(Integer.toString(chUser.getIncsNo())));
					}
					if (chUser != null) {
						String name = null;

						Map<String, String> loginMap = this.customerApiService.getChannelUser(chUser);

						if (loginMap != null && !loginMap.isEmpty()) {
							name = loginMap.get("name");
						}
						model.addAttribute("name", name);
					}
				}
				TermsVo termsVo = new TermsVo();
				termsVo.setChCd(channel.getChCd());
				List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
				model.addAttribute("corptermslist", termsList);

				return "convs/conversion_terms_a0203";

			} else { // 동일 아이디 사용 : A0202

				boolean includeSpcLoginId = false;
				if (chUsers != null && !chUsers.isEmpty()) {
					UmChUser chUser = chUsers.get(0);
					model.addAttribute("loginid", chUser.getChcsWebId());
					includeSpcLoginId = OmniUtil.includeSpecialCharInLoginId(chUser.getChcsWebId());

					// TODO 1. 로그인 아이디중 특수문자 체크
					log.debug("1) O X O A0202 경로자체고객 동일 아이디 conversion_terms_a0202_id loginid : {}, special char : {}", chUser.getChcsWebId(), includeSpcLoginId);

					if (chUser.getIncsNo() > 0) {
						model.addAttribute("xincsno", SecurityUtil.setXyzValue(Integer.toString(chUser.getIncsNo())));
					}
					if (chUser != null) {
						String name = null;

						Map<String, String> loginMap = this.customerApiService.getChannelUser(chUser);

						if (loginMap != null && !loginMap.isEmpty()) {
							name = loginMap.get("name");
						}
						final String username = OmniUtil.maskUserName(name, locale);
						model.addAttribute("name", username);
					}
				}
				TermsVo termsVo = new TermsVo();
				termsVo.setChCd(channel.getChCd());
				List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
				model.addAttribute("corptermslist", termsList);

				if (includeSpcLoginId) {
					return "convs/conversion_terms_a0202_id";
				} else {
					return "convs/conversion_terms_a0202";
				}
			}

		} else if (loginType == LoginType.AGREE) { // 통합아이디 등록 약관동의 A0105

			UmOmniUser omniUser = omniUsers.get(0);

			log.debug("▶▶▶▶▶ [login step] omniUser info : {}", StringUtil.printJson(omniUser));
			omniUser.setChCd(chCd);

			if (!this.termsService.hasTermsAgree(omniUser)) { // 경로약관 미동의 상태 ME-FO-A0105
				log.debug("▶▶▶▶▶ [login step] 통합회원으로 로그인 시 진입 채널에 약관동의가 되어있지 않은 경우");
				log.debug("▶▶▶▶▶ [login step] 통합회원전환, 약관동의 --> {}", LoginType.AGREE.getDesc());

				// 진입 채널에 대한 약관 출력 정보 조회
				TermsVo termsVo = new TermsVo();
				termsVo.setChCd(channel.getChCd());
				if (chCd.equals(OmniConstants.OSULLOC_CHCD)) { // 오설록 Mall에서 로그인 시 미동의 약관 조회 후 가입 처리
					termsVo.setIncsNo(omniUser.getIncsNo());
				}

				List<TermsVo> termsList = this.termsService.getTerms(termsVo);
				model.addAttribute("terms", termsList);
				String name = OmniUtil.maskUserName(omniUser.getFullName(), locale);
				final String id = omniUser.getUmUserName(); // 약관동의에서 아이디는 마스킹하지 않음.
				// 휴대전화번호는 고객통합을 조회
				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setIncsNo(omniUser.getIncsNo());

				final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);

				if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
					omniUser.setFullName(customer.getCustNm());
					name = OmniUtil.maskUserName(customer.getCustNm(), locale);
					final String mobileNo = StringUtil.mergeMobile(customer);
					final String mobile = OmniUtil.maskMobile(mobileNo, locale);
					model.addAttribute("mobile", mobile);
				} else {
					if (customer != null && "ICITSVCOM001".equals(customer.getRsltCd())) {
						try {

							log.debug("▶▶▶▶▶▶  GA Tagging Login FAIL(/login/step 고객통합 정보 미존재 ) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
									,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());
							
							GaTagData gaTagDto = GaTagData.builder()
		                               .cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
									   .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
									   .el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									   .loginType((String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_BASIC))
									   .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
									   .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
									   .chCd(channel.getChCd())
									   .chCdNm(channel.getChCdNm())
									   .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_FAIL)
									   .errorMessage("고객통합 정보 미존재")
									   .sendFlag("Y")
									   .incsNo(loginIncsNo)
			                           .sessionId(session.getId())
									   .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
									   .loginId(loginid).build();


							gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);
						} catch (Exception ex) {
							log.error(ex.getMessage());
						}

						throw new OmniException("회원 정보가 존재하지 않습니다.");
					}
				}

				model.addAttribute("incsno", omniUser.getIncsNo());
				model.addAttribute("incsNo", omniUser.getIncsNo());
				if (Integer.parseInt(omniUser.getIncsNo()) > 0) {
					model.addAttribute("xincsno", SecurityUtil.setXyzValue(omniUser.getIncsNo()));
				}
				model.addAttribute("name", name);
				model.addAttribute("id", id);
				model.addAttribute("xname", SecurityUtil.setXyzValue(omniUser.getFullName()));
				model.addAttribute("xid", SecurityUtil.setXyzValue(id));

				if (StringUtils.hasText(customer.getMbrJoinDt())) {
					model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
				} else {
					if (StringUtils.hasText(omniUser.getCreatedDate())) {
						model.addAttribute("joindate", DateUtil.getBirthDate(omniUser.getCreatedDate()));
					}
				}

				model.addAttribute("home", channel.getHmpgUrl());
				model.addAttribute("homeurl", channel.getHmpgUrl());
				TermsVo corpTermsVo = new TermsVo();
				corpTermsVo.setChCd(channel.getChCd());
				List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
				model.addAttribute("corptermslist", corpTermsList);
				
				//20230331 개인정보 수집 및 이용 동의 (마케팅)
				UmOmniUser omniUser2 = new UmOmniUser();
				final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
				omniUser.setChCd(onlineChCd);
				if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
					TermsVo termsVo2 = new TermsVo();
					termsVo2.setChCd(onlineChCd);
					List<TermsVo> termsList2 = this.termsService.getTermsChoice(termsVo2);
					for(TermsVo vo : termsList2) {
						if(vo.getMkSn()==-20) {
							model.addAttribute("terms_marketing", vo);
						}
					}
				}
				
				return "terms/login_terms_apply"; // ME-FO-A0105

			} else { // 경로약관 동의 상태
				// ME-FO-A0214 오픈 후 최초 통합회원 로그인 > 신규 약관 동의 안내
				// - 통합회원 고객 번호 존재, 채널 약관 동의 상태, 오픈 후 최초 로그인

				if (!this.termsService.hasCorpTermsAgree(omniUser)) { // 전사약관 미동의상태
					log.debug("▶▶▶▶▶ [login step] 통합회원 고객 번호 존재, 채널 약관 동의 상태, 오픈 후 최초 로그인");
					log.debug("▶▶▶▶▶ [login step] 전사 약관동의 --> {}", LoginType.CORPAGREE.getDesc());

					// 진입 채널에 대한 약관 출력 정보 조회
					TermsVo termsVo = new TermsVo();
					termsVo.setChCd(channel.getChCd());
					List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
					model.addAttribute("terms", termsList);
					model.addAttribute("corpterms", true);
					String name = OmniUtil.maskUserName(omniUser.getFullName(), locale);
					final String id = omniUser.getUmUserName(); // 약관동의에서 아이디는 마스킹하지 않음.
					// 휴대전화번호는 고객통합을 조회
					CustInfoVo custInfoVo = new CustInfoVo();
					custInfoVo.setIncsNo(omniUser.getIncsNo());

					final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);

					if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
						name = OmniUtil.maskUserName(customer.getCustNm(), locale);
						final String mobileNo = StringUtil.mergeMobile(customer);
						final String mobile = OmniUtil.maskMobile(mobileNo, locale);
						model.addAttribute("mobile", mobile);
					} else {
						if (customer != null && "ICITSVCOM001".equals(customer.getRsltCd())) {

							try {

								log.debug("▶▶▶▶▶▶  GA Tagging Login FAIL(/login/step 고객통합 정보 미존재 ) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
										,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());
								
								GaTagData gaTagDto = GaTagData.builder()
			                             .cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
										   .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
										   .el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
										   .loginType((String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_BASIC))
										   .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
										   .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
										   .chCd(channel.getChCd())
										   .chCdNm(channel.getChCdNm())
										   .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_FAIL)
										   .errorMessage("고객통합 정보 미존재")
										   .sendFlag("Y")
										   .incsNo(loginIncsNo)
				                           .sessionId(session.getId())
										   .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
										   .loginId(loginid).build();

								gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);

							} catch (Exception ex) {
								log.error(ex.getMessage());
							}

							throw new OmniException("회원 정보가 존재하지 않습니다.");
						}
					}

					model.addAttribute("incsno", omniUser.getIncsNo());
					model.addAttribute("incsNo", omniUser.getIncsNo());
					model.addAttribute("xincsno", SecurityUtil.setXyzValue(omniUser.getIncsNo()));
					model.addAttribute("name", name);
					model.addAttribute("id", id);
					model.addAttribute("xname", SecurityUtil.setXyzValue(omniUser.getFullName()));
					model.addAttribute("xid", SecurityUtil.setXyzValue(id));

					if (StringUtils.hasText(customer.getMbrJoinDt())) {
						model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
					} else {
						if (StringUtils.hasText(omniUser.getCreatedDate())) {
							model.addAttribute("joindate", DateUtil.getBirthDate(omniUser.getCreatedDate()));
						}
					}

					model.addAttribute("home", channel.getHmpgUrl());
					model.addAttribute("homeurl", channel.getHmpgUrl());

					return "terms/login_new_terms_apply"; // ME-FO-A0214

				}
				
				boolean isMarketing = StringUtil.isTrue(this.config.getChannelApi(chCd, "ismarketing", profile));
				// 해당 경로 미 가입 상태 시 문자 수신 동의 여부를 받는 경우 ex) APMall
				if(isMarketing) {
					log.debug("▶▶▶▶▶ [login step] 통합회원으로 로그인 시 진입 채널에 문자 수신동의가 되어있지 않은 경우");
					log.debug("▶▶▶▶▶ [login step] 통합회원전환, 약관동의 --> {}", LoginType.AGREE.getDesc());
					model.addAttribute("isMarketing", isMarketing);

					String name = OmniUtil.maskUserName(omniUser.getFullName(), locale);
					final String id = omniUser.getUmUserName(); // 약관동의에서 아이디는 마스킹하지 않음.
					// 휴대전화번호는 고객통합을 조회
					CustInfoVo custInfoVo = new CustInfoVo();
					custInfoVo.setIncsNo(omniUser.getIncsNo());

					final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);

					if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
						omniUser.setFullName(customer.getCustNm());
						name = OmniUtil.maskUserName(customer.getCustNm(), locale);
						final String mobileNo = StringUtil.mergeMobile(customer);
						final String mobile = OmniUtil.maskMobile(mobileNo, locale);
						model.addAttribute("mobile", mobile);
					} else {
						if (customer != null && "ICITSVCOM001".equals(customer.getRsltCd())) {
							try {

								log.debug("▶▶▶▶▶▶  GA Tagging Login FAIL(/login/step 고객통합 정보 미존재 ) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
										,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());
								
								GaTagData gaTagDto = GaTagData.builder()
			                             .cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
										   .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
										   .el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
										   .loginType((String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_BASIC))
										   .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
										   .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
										   .chCd(channel.getChCd())
										   .chCdNm(channel.getChCdNm())
										   .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_FAIL)
										   .errorMessage("고객통합 정보 미존재")
										   .sendFlag("Y")
										   .incsNo(loginIncsNo)
				                           .sessionId(session.getId())
										   .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
										   .loginId(loginid).build();
								
								gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);
							} catch (Exception ex) {
								log.error(ex.getMessage());
							}

							throw new OmniException("회원 정보가 존재하지 않습니다.");
						}
					}

					model.addAttribute("incsno", omniUser.getIncsNo());
					model.addAttribute("incsNo", omniUser.getIncsNo());
					if (Integer.parseInt(omniUser.getIncsNo()) > 0) {
						model.addAttribute("xincsno", SecurityUtil.setXyzValue(omniUser.getIncsNo()));
					}
					model.addAttribute("name", name);
					model.addAttribute("id", id);
					model.addAttribute("xname", SecurityUtil.setXyzValue(omniUser.getFullName()));
					model.addAttribute("xid", SecurityUtil.setXyzValue(id));

					if (StringUtils.hasText(customer.getMbrJoinDt())) {
						model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
					} else {
						if (StringUtils.hasText(omniUser.getCreatedDate())) {
							model.addAttribute("joindate", DateUtil.getBirthDate(omniUser.getCreatedDate()));
						}
					}

					model.addAttribute("home", channel.getHmpgUrl());
					model.addAttribute("homeurl", channel.getHmpgUrl());
					
					//20230404 개인정보 수집 및 이용 동의 (마케팅)
					UmOmniUser omniUser2 = new UmOmniUser();
					final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
					omniUser.setChCd(onlineChCd);
					if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
						TermsVo termsVo2 = new TermsVo();
						termsVo2.setChCd(onlineChCd);
						List<TermsVo> termsList2 = this.termsService.getTermsChoice(termsVo2);
						for(TermsVo vo : termsList2) {
							if(vo.getMkSn()==-20) {
								model.addAttribute("terms_marketing", vo);
							}
						}
					}
					
					return "terms/login_terms_apply"; // ME-FO-A0105
				}
			}

		} else if (loginType == LoginType.NEW) { // 회원가입
			String ssoAuthUrl = this.ssoAuthorizeUrl.concat("?").concat(WebUtil.getSsoParamsAuth());
			return "redirect:" + ssoAuthUrl;
		} else if (loginType == LoginType.LOCK_ABUSING) { // Lock 어뷰징 처리 20230411
			UmOmniUser omniUser = omniUsers.get(0);
			model.addAttribute("name", OmniUtil.maskUserName(omniUser.getFullName(), locale));
			model.addAttribute("loginid", OmniUtil.maskUserId(omniUser.getUmUserName()));
			final CertData certData = this.certService.certInit(channel.getChCd());
			model.addAttribute("certdata", certData);
		
			profile = StringUtils.isEmpty(profile) ? "dev" : profile;
			profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
			model.addAttribute("manualcert", this.config.avaiableManualCert(profile));

			model.addAttribute("type", "mbrs");
			model.addAttribute("mobile", WebUtil.isMobile());
			model.addAttribute(OmniConstants.CH_CD, SecurityUtil.clearXSSNormal(ssoParam.getChannelCd()));
			model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
			WebUtil.setSession("certiType", "lockcheck");
			WebUtil.setCookies(response, OmniConstants.ONE_AP_CERTIFICATION_TYPE, "lockcheck");
			
			boolean isSsnsSignUpEnable = config.isSnsSignUpEnable(channel.getChCd(), profile);
			model.addAttribute("isSsnsSignUpEnable", isSsnsSignUpEnable);

			boolean isInniMobileBackAction = OmniUtil.isInniMobileBackAction(request);
			model.addAttribute("innimobileAction", isInniMobileBackAction);
			boolean isBeautyAngelMobileBackAction = OmniUtil.isBeautyAngelMobileBackAction(request);
			model.addAttribute("beautyAngelmobileAction", isBeautyAngelMobileBackAction);
			boolean isAmoreMallAOS = OmniUtil.isAmoreMallAOS(request);
			model.addAttribute("isAmoreMallAOS", isAmoreMallAOS);
			boolean isAmoreMallIOS = OmniUtil.isAmoreMallIOS(request);
			model.addAttribute("isAmoreMallIOS", isAmoreMallIOS);
			model.addAttribute("referer", WebUtil.getHeader("Referer")); // add referer
			
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(loginIncsNo));
			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(omniUsers.get(0).getFullName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(omniUsers.get(0).getUmUserName()));
			
			return "login/lock_recheck";
		}
		return "login/login_step";
	}

	@GetMapping("/plogin-param")
	public String phoneLoginParam(final @RequestParam(value = "autoLoginOption", required = false) String autoLoginOption, final Model model) {

		// 모바일인 경우만 처리 - 자동로그인 옵션 선택 - commonauth 에 자동 로그인 처리옵션 추가
		boolean isMobile = WebUtil.isMobile();
		if (isMobile) {
			if (StringUtils.hasText(autoLoginOption) && "Y".equals(autoLoginOption)) {
				log.info("▶▶▶▶▶ [phone login] auto login set...");
				WebUtil.setSession(OmniConstants.AUTO_LOGIN_SESSION, "Y");
			}
		}

		String queryString = "";
		Object obj = WebUtil.getSession(OmniConstants.SSOPARAM);
		if (obj != null) {
			SSOParam ssoParam = (SSOParam) obj;
			queryString = ssoParam.getQueryString();
		}

		return "redirect:/plogin?" + queryString;
	}

	// A0206 휴대폰 로그인 01
	@SuppressWarnings("unchecked")
	@GetMapping("/plogin")
	public String phoneLogin( //
			final SSOParam loginParam, //
			final LoginVo loginVo, //
			final HttpServletRequest request, //
			final HttpSession session, //
			final @CookieValue(value = OmniConstants.LAST_LOGIN_TYPE, required = false) Cookie lastLloginCookie, //
			final Model model, //
			final RedirectAttributes redirectAttr) {
		log.debug("▶▶▶▶▶▶ [phone login page] sso param : {}", StringUtil.printJson(loginParam));

		if (StringUtils.isEmpty(loginParam.getChannelCd())) {

			throw new OmniException("경로코드가 존재하지 않습니다.");

		} else {
			WebUtil.setSession(OmniConstants.CH_CD_SESSION, loginParam.getChannelCd());
		}

		final Channel channel = this.commonService.getChannel(loginParam.getChannelCd());
		log.debug("▶▶▶▶▶▶ [mobile login page] channel info : {}", StringUtil.printJson(channel));
		boolean isOffline = OmniUtil.isOffline(channel); // 오프라인인 경우 로그인하지 않으므로 체크 불필요
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		model.addAttribute("orderurl", OmniUtil.getOrderUrl(channel, profile));

		// 세션데이터키가 없으면 로그인 할 수 없으므로 이 값에 대해서 반드시 validation 체크해야함.
		// 중요) 이 값은 WSO2에서 받아오지 않은 값이면 SSO 처리가 되지 않음.
		if (!isOffline && StringUtils.isEmpty(loginParam.getSessionDataKey())) {

			throw new OmniException("인증정보가 존재하지 않습니다.");

		}

		model.addAttribute("rv", this.config.resourceVersion());

		/*
		 * ga tagging dto setup basic setup <pre> 아이디 로그인 처리 </pre>
		 */
		
		WebUtil.setSession(OmniGaTaggingConstants.EL, "휴대폰");
		WebUtil.setSession(OmniGaTaggingConstants.LOGIN_HANDPHOME, "휴대폰");
		
		
		Map<String, String> idpAuthenticatorMapping = null;
		if (request.getAttribute(OmniConstants.IDP_AUTHENTICATOR_MAP) != null) {
			idpAuthenticatorMapping = (Map<String, String>) request.getAttribute(OmniConstants.IDP_AUTHENTICATOR_MAP);
		}

		if (idpAuthenticatorMapping == null || idpAuthenticatorMapping.isEmpty()) {
			idpAuthenticatorMapping = this.snsTypes;
		}
		
		// Apple 심사를 위하여 iOS App 인경우 idpAuthenticatorMapping 순서 변경
		if (OmniUtil.isIOSApp(request) && idpAuthenticatorMapping.containsKey("AP")) {
			idpAuthenticatorMapping = this.snsTypesIOSApp;
			//apple 디자인 변경
			model.addAttribute("isiosapp", true);
		}

		final LoginResponse response = new LoginResponse();
		response.setIdpAuthenticatorMapping(idpAuthenticatorMapping);
		final String queryString = request.getQueryString();
		if (StringUtils.hasText(queryString)) {
			loginParam.setQueryString(queryString);
		}
		response.setSsoParam(loginParam); // localStorage 에 저장위해 필요

		// 최근 로그인 타입
		if (lastLloginCookie != null) {
			final String lastLoginCookieVal = lastLloginCookie.getValue();
			if (StringUtils.hasText(lastLoginCookieVal)) {
				model.addAttribute("lastlogin", lastLoginCookieVal); // 최근 로그인
			}
		}

		WebUtil.setSession(OmniConstants.SESSION_DATA_KEY_SESSION, loginParam.getSessionDataKey());
		WebUtil.setSession(OmniConstants.SSOPARAM, loginParam);
		response.setSessionDataKey(loginParam.getSessionDataKey()); // 화면에 sessionDataKey를 전달(로그인시 필수값)
		if (StringUtils.hasText(request.getQueryString())) {
			response.setQueryString("?" + request.getQueryString());
		}
		model.addAttribute(OmniConstants.CH_CD, SecurityUtil.clearXSSNormal(loginParam.getChannelCd()));
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("login", response);
		model.addAttribute("qs", request.getQueryString());

		// - 512 보다 크거나 같으면 SNS 인증 화면 Disable
		// - 512 보다 작으면 SNS 인증 화면 Enable
		String strVt = StringUtils.isEmpty(loginParam.getVt()) ? "500" : loginParam.getVt();
		int vt = StringUtil.isNumeric(strVt) ? Integer.parseInt(strVt) : 500;
		model.addAttribute("vtdisable", vt >= 512 ? true : false);

		// Facebook SDK 사용을 위한 FBRestApiKey 전달
		model.addAttribute("profile", profile);
		model.addAttribute("FBRestApiKey", this.config.getSnsInfo(profile, SnsType.FACEBOOK.getType().toLowerCase(), "restkey"));

		// Andorid App 에서 Facebook Login 노출되지 않도록 임시로 조치 - 2021.11.24
		boolean isMobile = WebUtil.isMobile();
		boolean isAndroidApp = WebUtil.isAndroidApp();
		if (isMobile && isAndroidApp) {
			model.addAttribute("isAndroidApp", true);
		}

		return "login/phone_login";
	}

	// @GetMapping("/plogin/step")
	@RequestMapping(value = "/plogin/step", method = { RequestMethod.GET, RequestMethod.POST })
	public String phoneLoginStep(final Model model, final HttpServletRequest servletRequest, 
			final HttpServletResponse servletResponse, final Locale locale, final RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
		WebUtil.setSession(OmniConstants.LOGIN_MOBILE_TYPE, SecurityUtil.setXyzValue("MOBILE"));
		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		String incsNo = WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION);
		
		// 2022-12-08 모의해킹/앱스캔 조치 결과에 따라 세션에서 인증 결과값이 성공이 아닐 경우 잘못된 접근으로 예외 처리
		final int smsAuthStatus = StringUtils.isEmpty(WebUtil.getSession(OmniConstants.SMS_AUTH_STATUS)) ? 0 : (int) WebUtil.getSession(OmniConstants.SMS_AUTH_STATUS);
		if(smsAuthStatus != OmniConstants.SMS_AUTH_SUCCESS) {
			log.error("ID 찾기 인증 결과 실패 : {}", smsAuthStatus);
			throw new OmniException("인증에 실패하였습니다.");
		}

		int incsno = 0;
		if (StringUtils.hasText(incsNo)) {
			incsno = Integer.parseInt(incsNo);
		}

		model.addAttribute("rv", this.config.resourceVersion());

		String username = SecurityUtil.getXValue(WebUtil.getStringSession(OmniConstants.XNM_SESSION), false);

		LoginStepVo loginStep = this.loginService.phoneLoginStep(chcd, incsno);
		redirectAttributes.addFlashAttribute("loginStep", loginStep);
		LoginType loginType = loginStep.getLoginType();
		log.debug("▶▶▶▶▶▶ [mobile login step] : login type : {}, {}", LoginType.get(loginType.getType()), StringUtil.printJson(loginStep));

		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("chcd", channel.getChCd());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

		final List<UmOmniUser> omniUsers = loginStep.getOmniUsers();

		final Map<String,String> gaCookieMap = gataggingUtils.getGaCookieMap(servletRequest);	 
		
		
		GaTagData gaTagDto = GaTagData.builder()
                   .cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
				   .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
				   .el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
				   .loginType((String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_HANDPHOME))
				   .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
				   .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
				   .chCd(channel.getChCd())
				   .chCdNm(channel.getChCdNm())
				   .sendFlag("Y")
				   .incsNo(incsNo)
                   .sessionId(servletRequest.getSession().getId())
				   .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
				   .loginId(username).build();
		

		if (loginType == LoginType.DORMANCYFAIL) {

			throw new OmniException("시스템 오류가 발생하였습니다. 관리자에게 문의하시기 바랍니다.");
		}

		if (loginType == LoginType.PWDCHANGE) {
			if (omniUsers != null && omniUsers.size() > 0) {
				UmOmniUser omniUser = omniUsers.get(0);
				WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(omniUser.getUmUserName()));
				model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));
				model.addAttribute(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(omniUser.getUmUserName()));
				model.addAttribute(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(Integer.toString(incsno)));
				model.addAttribute(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(Integer.toString(incsno)));
			}

			return "mgmt/change_pwd_campaign";
		} else if (loginType == LoginType.LOGIN) {

			if (omniUsers != null && omniUsers.size() > 0) {
				
				try {

					log.debug("▶▶▶▶▶▶  GA Tagging LOGIN START(/plogin/step 휴대폰) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
							,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());
					
					gaTagDto.setEventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_START);
					gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);

				} catch (Exception ex) {
					log.error(ex.getMessage());
				}

				
				UmOmniUser omniUser = omniUsers.get(0);
				omniUser.setChCd(chcd);
				if (!this.termsService.hasCorpTermsAgree(omniUser)) { // 전사약관 미동의상태
					// 진입 채널에 대한 약관 출력 정보 조회
					TermsVo termsVo = new TermsVo();
					termsVo.setChCd(channel.getChCd());
					List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
					model.addAttribute("terms", termsList);
					model.addAttribute("corpterms", true);

					String name = OmniUtil.maskUserName(omniUser.getFullName(), locale);
					final String id = omniUser.getUmUserName(); // 약관동의에서 아이디는 마스킹하지 않음.

					// 휴대전화번호는 고객통합을 조회
					CustInfoVo custInfoVo = new CustInfoVo();
					custInfoVo.setIncsNo(omniUser.getIncsNo());
					final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
					if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
						name = OmniUtil.maskUserName(customer.getCustNm(), locale);
						final String mobileNo = StringUtil.mergeMobile(customer);
						final String mobile = OmniUtil.maskMobile(mobileNo, locale);
						model.addAttribute("mobile", mobile);
					} else {
						if (customer != null && "ICITSVCOM001".equals(customer.getRsltCd())) {
							try {


								log.debug("▶▶▶▶▶▶  GA Tagging LOGIN FAIL(/plogin/step 휴대폰 고객통합 정보 미존재) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
										,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());
								
								gaTagDto.setEventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_FAIL);
								gaTagDto.setErrorMessage("고객통합 정보 미존재");
								gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);
							} catch (Exception ex) {
								log.error(ex.getMessage());
							}
							throw new OmniException("회원 정보가 존재하지 않습니다.");
						}
					}

					model.addAttribute("incsno", omniUser.getIncsNo());
					model.addAttribute("incsNo", omniUser.getIncsNo());
					model.addAttribute("xincsno", SecurityUtil.setXyzValue(omniUser.getIncsNo()));
					model.addAttribute("name", name);
					model.addAttribute("id", id);
					model.addAttribute("xname", SecurityUtil.setXyzValue(omniUser.getFullName()));
					model.addAttribute("xid", SecurityUtil.setXyzValue(id));

					if (StringUtils.hasText(customer.getMbrJoinDt())) {
						model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
					} else {
						if (StringUtils.hasText(omniUser.getCreatedDate())) {
							model.addAttribute("joindate", DateUtil.getBirthDate(omniUser.getCreatedDate()));
						}
					}

					model.addAttribute("home", channel.getHmpgUrl());
					model.addAttribute("homeurl", channel.getHmpgUrl());

					return "terms/login_new_terms_apply"; // ME-FO-A0214
				}
			}

			model.addAttribute("actionurl", this.commonAuthUrl);
			model.addAttribute(OmniConstants.XID_SESSION, username);
			model.addAttribute(OmniConstants.XPW_SESSION, Integer.toString(incsno));
			model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));

			// sns 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(username);
				snsParam.setIncsNo(Integer.toString(incsno));
				snsAuth.doSnsMapping(snsParam);
			}

			boolean autologin = "Y".equals(WebUtil.getStringSession(OmniConstants.AUTO_LOGIN_SESSION));
			model.addAttribute(OmniConstants.AUTO_LOGIN, autologin);

			WebUtil.setCookies(servletResponse, OmniConstants.LAST_LOGIN_TYPE, "mobile");

			final String authType = this.config.commonAuthType();
			if (authType.equals(HttpMethod.GET.name())) {
				StringBuilder authurl = new StringBuilder();
				authurl.append(this.commonAuthUrl); //
				if (StringUtils.hasText(username)) {
					authurl.append("?fullName=").append(URLEncoder.encode(username, StandardCharsets.UTF_8.name()));
				} else {
					authurl.append("?fullName=Dummy");
				}
				authurl.append("&incsNo=").append(Integer.toString(incsno));
				authurl.append("&sessionDataKey=").append(URLEncoder.encode(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION), StandardCharsets.UTF_8.name()));
				if (autologin) {
					authurl.append("&chkRemember=on");
				}
				try {

					log.debug("▶▶▶▶▶▶  GA Tagging LOGIN SUCCESS(/plogin/step 휴대폰)  : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
							,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());
					
					gaTagDto.setEventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_SUCCESS);
					gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);

				} catch (Exception ex) {
					log.error(ex.getMessage());
				}
				// log.debug("▶▶▶▶▶ [mobile login step] sso auth url : {}", authurl.toString());
				return "redirect:" + authurl.toString();
			} else {
				try {

					log.debug("▶▶▶▶▶▶  GA Tagging LOGIN SUCCESS(/plogin/step 휴대폰) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
							,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());					

					gaTagDto.setEventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_SUCCESS);
					gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);

				} catch (Exception ex) {
					log.error(ex.getMessage());
				}
				model.addAttribute("actionurl", this.commonAuthUrl);
				WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
				return "cert/mobile-moveon";
			}

		} else if (loginType == LoginType.CS) {
			redirectAttributes.addFlashAttribute("types", loginType.getType());
			return "redirect:/mgmt/csinfo";
		} else if (loginType == LoginType.CONV_JOIN // A0217 O X X
				|| loginType == LoginType.TRNS_JOIN // A0207 O X O
		) { // A0217
			WebUtil.setSession(OmniConstants.XID_MSESSION, username);
			WebUtil.setSession(OmniConstants.XPW_MSESSION, Integer.toString(incsno));
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(Integer.toString(incsno));
			final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
				username = customer.getCustNm();
				String name = OmniUtil.maskUserName(username, locale);
				model.addAttribute("name", name);
				final String mobileNo = StringUtil.mergeMobile(customer);
				final String mobile = OmniUtil.maskMobile(mobileNo, locale);
				model.addAttribute("mobile", mobile);
				if (StringUtils.hasText(customer.getMbrJoinDt())) {
					model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
				}
			}

			// 채널 약관 동의여부 확인
			UmOmniUser omniUser = new UmOmniUser();
			if (incsno > 0) {
				omniUser.setIncsNo(Integer.toString(incsno));
			}
			omniUser.setChCd(chcd);

			if (!this.termsService.hasTermsAgree(omniUser)) { // ME-FO-A0217, 진입 채널에 대한 약관 출력 정보 조회
				TermsVo termsVo = new TermsVo();
				termsVo.setChCd(chcd);
				if (chcd.equals(OmniConstants.OSULLOC_CHCD)) {
					termsVo.setIncsNo(omniUser.getIncsNo());
				}
				List<TermsVo> termsList = this.termsService.getTerms(termsVo);
				model.addAttribute("terms", termsList);
			}
			TermsVo corpTermsVo = new TermsVo();
			corpTermsVo.setChCd(channel.getChCd());
			List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
			model.addAttribute("corptermslist", corpTermsList);
			
			//20230323 채널 문자 수신 동의
			final String onlineChCd = ChannelPairs.getOnlineCd(chcd);
			WebUtil.setSession(OmniConstants.CH_CD_SESSION, chcd);
			TermsVo corpTermsVo2 = new TermsVo();
			corpTermsVo2.setChCd(onlineChCd);
			UmOmniUser omniUser2 = new UmOmniUser();
			omniUser2.setChCd(onlineChCd);
			if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
				TermsVo termsVo2 = new TermsVo();
				termsVo2.setChCd(onlineChCd);
				List<TermsVo> termsList2 = this.termsService.getTermsChoice(termsVo2);
//						model.addAttribute("terms", termsList2);
				for(TermsVo vo : termsList2) {
					if(vo.getMkSn()==-20) {
						model.addAttribute("terms_marketing", vo);
					}
				}
			}
			
			if (loginType == LoginType.CONV_JOIN) { // A0217 O X X

				model.addAttribute("home", channel.getHmpgUrl());
				model.addAttribute("homeurl", channel.getHmpgUrl());

				return "login/plogin_id_regist";
			} else { // loginType == LoginType.TRNS_JOIN) { // A0207 O X O

				model.addAttribute("home", channel.getHmpgUrl());
				model.addAttribute("homeurl", channel.getHmpgUrl());

				return "login/plogin_id_regist_bp";
			}

		} else if (loginType == LoginType.AGREE) { // // 통합아이디 등록 약관동의 A0105

			WebUtil.setSession(OmniConstants.XID_MSESSION, username);
			WebUtil.setSession(OmniConstants.XPW_MSESSION, Integer.toString(incsno));
			UmOmniUser omniUser = omniUsers.get(0);
			omniUser.setChCd(chcd);
			username = omniUser.getFullName();
			String name = OmniUtil.maskUserName(username, locale);
			if (StringUtils.hasText(omniUser.getCreatedDate())) {
				model.addAttribute("joindate", DateUtil.getBirthDate(omniUser.getCreatedDate()));
			}

			if (StringUtils.hasText(omniUser.getUmUserName())) {
				final String id = omniUser.getUmUserName(); // 약관동의에서 아이디는 마스킹하지 않음.
				model.addAttribute("id", id);
				model.addAttribute("xid", SecurityUtil.setXyzValue(id));
			}

			String mobileno = SecurityUtil.getXValue(WebUtil.getStringSession(OmniConstants.XMOBILE_NO_SESSION), false);

			if (StringUtils.isEmpty(mobileno)) {
				// 휴대전화번호는 고객통합을 조회
				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setIncsNo(omniUser.getIncsNo());
				final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
				if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
					username = customer.getCustNm();
					mobileno = StringUtil.mergeMobile(customer);
				} else {
					try {
						
						log.debug("▶▶▶▶▶▶  GA Tagging Login FAIL(/plogin/step 휴대폰 회원 정보가 미존재) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
								,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());
						
						gaTagDto.setEventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_FAIL);
						gaTagDto.setErrorMessage("고객통합 정보 미존재");
						gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);

					} catch (Exception ex) {
						log.error(ex.getMessage());
					}
					throw new OmniException("회원 정보가 존재하지 않습니다.");
				}
			}

			model.addAttribute("mobile", OmniUtil.maskMobile(mobileno, locale));
			model.addAttribute("incsno", Integer.toString(incsno));
			if (incsno > 0) {
				model.addAttribute("xincsno", SecurityUtil.setXyzValue(Integer.toString(incsno)));
			}
			model.addAttribute("name", name);
			model.addAttribute("xname", SecurityUtil.setXyzValue(username));
			model.addAttribute(OmniConstants.LOGIN_MOBILE_TYPE, SecurityUtil.setXyzValue("MOBILE")); // 휴대폰 로그인

			// 약관동의여부 체크
			if (!this.termsService.hasTermsAgree(omniUser)) { // 경로약관 미동의 상태 ME-FO-A0105


				// 진입 채널에 대한 약관 출력 정보 조회
				TermsVo termsVo = new TermsVo();
				termsVo.setChCd(channel.getChCd());
				if (channel.getChCd().equals(OmniConstants.OSULLOC_CHCD)) {
					termsVo.setIncsNo(omniUser.getIncsNo());
				}
				List<TermsVo> termsList = this.termsService.getTerms(termsVo);
				model.addAttribute("terms", termsList);

				model.addAttribute("home", channel.getHmpgUrl());
				model.addAttribute("homeurl", channel.getHmpgUrl());
				TermsVo corpTermsVo = new TermsVo();
				corpTermsVo.setChCd(channel.getChCd());
				List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
				model.addAttribute("corptermslist", corpTermsList);
				
				//20230404 개인정보 수집 및 이용 동의 (마케팅)
				UmOmniUser omniUser2 = new UmOmniUser();
				final String onlineChCd = ChannelPairs.getOnlineCd(channel.getChCd());
				omniUser.setChCd(onlineChCd);
				if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
					TermsVo termsVo2 = new TermsVo();
					termsVo2.setChCd(onlineChCd);
					List<TermsVo> termsList2 = this.termsService.getTermsChoice(termsVo2);
					for(TermsVo vo : termsList2) {
						if(vo.getMkSn()==-20) {
							model.addAttribute("terms_marketing", vo);
						}
					}
				}
				
				return "terms/login_terms_apply"; // ME-FO-A0105
			} else {
				if (!this.termsService.hasCorpTermsAgree(omniUser)) { // 전사약관 미동의상태
					log.debug("▶▶▶▶▶ [mobile login step] 통합회원 고객 번호 존재, 채널 약관 동의 상태, 오픈 후 최초 로그인");
					log.debug("▶▶▶▶▶ [mobile login step] 전사 약관동의 --> {}", LoginType.CORPAGREE.getDesc());
					// return new LoginStepVo(LoginType.CORPAGREE, omniUsers, chUsers, omniIncsNo);

					// 진입 채널에 대한 약관 출력 정보 조회
					TermsVo termsVo = new TermsVo();
					termsVo.setChCd(channel.getChCd());
					List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
					model.addAttribute("terms", termsList);
					model.addAttribute("corpterms", true);
					name = OmniUtil.maskUserName(username, locale);
					final String id = omniUser.getUmUserName(); // 약관동의에서 아이디는 마스킹하지 않음.

					// 휴대전화번호는 고객통합을 조회
					CustInfoVo custInfoVo = new CustInfoVo();
					custInfoVo.setIncsNo(omniUser.getIncsNo());
					final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
					log.debug("▶▶▶▶▶ [mobile login step] customer info : {}", StringUtil.printJson(customer));
					if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
						username = customer.getCustNm();
						name = OmniUtil.maskUserName(customer.getCustNm(), locale);
						final String mobileNo = StringUtil.mergeMobile(customer);
						final String mobile = OmniUtil.maskMobile(mobileNo, locale);
						model.addAttribute("mobile", mobile);
					} else {
						if (customer != null && "ICITSVCOM001".equals(customer.getRsltCd())) {

							throw new OmniException("회원 정보가 존재하지 않습니다.");
						}
					}

					model.addAttribute("incsno", omniUser.getIncsNo());
					model.addAttribute("xincsno", SecurityUtil.setXyzValue(omniUser.getIncsNo()));
					model.addAttribute("name", name);
					model.addAttribute("id", id);
					model.addAttribute("xname", SecurityUtil.setXyzValue(username));
					model.addAttribute("xid", SecurityUtil.setXyzValue(id));

					if (StringUtils.hasText(customer.getMbrJoinDt())) {
						model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
					} else {
						if (StringUtils.hasText(omniUser.getCreatedDate())) {
							model.addAttribute("joindate", DateUtil.getBirthDate(omniUser.getCreatedDate()));
						}
					}

					model.addAttribute("home", channel.getHmpgUrl());
					model.addAttribute("homeurl", channel.getHmpgUrl());

					return "terms/login_new_terms_apply"; // ME-FO-A0214
				}
				
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				boolean isMarketing = StringUtil.isTrue(this.config.getChannelApi(chcd, "ismarketing", profile));
				// 해당 경로 미 가입 상태 시 문자 수신 동의 여부를 받는 경우 ex) APMall
				if(isMarketing) {
					log.debug("▶▶▶▶▶ [login step] 통합회원으로 로그인 시 진입 채널에 문자 수신동의가 되어있지 않은 경우");
					log.debug("▶▶▶▶▶ [login step] 통합회원전환, 약관동의 --> {}", LoginType.AGREE.getDesc());
					model.addAttribute("isMarketing", isMarketing);

					// 휴대전화번호는 고객통합을 조회
					CustInfoVo custInfoVo = new CustInfoVo();
					custInfoVo.setIncsNo(omniUser.getIncsNo());

					final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);

					if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
						omniUser.setFullName(customer.getCustNm());
						name = OmniUtil.maskUserName(customer.getCustNm(), locale);
						final String mobileNo = StringUtil.mergeMobile(customer);
						final String mobile = OmniUtil.maskMobile(mobileNo, locale);
						model.addAttribute("mobile", mobile);
					} else {
						if (customer != null && "ICITSVCOM001".equals(customer.getRsltCd())) {
							throw new OmniException("회원 정보가 존재하지 않습니다.");
						}
					}

					model.addAttribute("incsno", omniUser.getIncsNo());
					model.addAttribute("incsNo", omniUser.getIncsNo());
					if (Integer.parseInt(omniUser.getIncsNo()) > 0) {
						model.addAttribute("xincsno", SecurityUtil.setXyzValue(omniUser.getIncsNo()));
					}
					model.addAttribute("name", name);
					model.addAttribute("id", omniUser.getUmUserName());
					model.addAttribute("xname", SecurityUtil.setXyzValue(omniUser.getFullName()));
					model.addAttribute("xid", SecurityUtil.setXyzValue(omniUser.getUmUserName()));

					if (StringUtils.hasText(customer.getMbrJoinDt())) {
						model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
					} else {
						if (StringUtils.hasText(omniUser.getCreatedDate())) {
							model.addAttribute("joindate", DateUtil.getBirthDate(omniUser.getCreatedDate()));
						}
					}

					model.addAttribute("home", channel.getHmpgUrl());
					model.addAttribute("homeurl", channel.getHmpgUrl());
					
					//20230404 개인정보 수집 및 이용 동의 (마케팅)
					UmOmniUser omniUser2 = new UmOmniUser();
					final String onlineChCd = ChannelPairs.getOnlineCd(chcd);
					omniUser.setChCd(onlineChCd);
					if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
						TermsVo termsVo2 = new TermsVo();
						termsVo2.setChCd(onlineChCd);
						List<TermsVo> termsList2 = this.termsService.getTermsChoice(termsVo2);
						for(TermsVo vo : termsList2) {
							if(vo.getMkSn()==-20) {
								model.addAttribute("terms_marketing", vo);
							}
						}
					}
					
					return "terms/login_terms_apply"; // ME-FO-A0105
				}
			}

			model.addAttribute("home", channel.getHmpgUrl());
			model.addAttribute("homeurl", channel.getHmpgUrl());
			TermsVo corpTermsVo = new TermsVo();
			corpTermsVo.setChCd(channel.getChCd());
			List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
			model.addAttribute("corptermslist", corpTermsList);
			
			//20230404 개인정보 수집 및 이용 동의 (마케팅)
			UmOmniUser omniUser2 = new UmOmniUser();
			final String onlineChCd = ChannelPairs.getOnlineCd(chcd);
			omniUser.setChCd(onlineChCd);
			if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
				TermsVo termsVo2 = new TermsVo();
				termsVo2.setChCd(onlineChCd);
				List<TermsVo> termsList2 = this.termsService.getTermsChoice(termsVo2);
				for(TermsVo vo : termsList2) {
					if(vo.getMkSn()==-20) {
						model.addAttribute("terms_marketing", vo);
					}
				}
			}
			
			return "terms/login_terms_apply"; // ME-FO-A0105
		} else if (loginType == LoginType.NEW) { // A0207

			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("channelName", channel.getChCdNm());
			model.addAttribute("offline", OmniUtil.isOffline(channel));
			model.addAttribute("chcd", channel.getChCd());
			model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
			model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

			if (incsno > 0) {
				model.addAttribute(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(Integer.toString(incsno)));
			}

			// 휴대전화번호는 고객통합을 조회
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(Integer.toString(incsno));
			final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
				String name = OmniUtil.maskUserName(customer.getCustNm(), locale);
				final String mobileNo = StringUtil.mergeMobile(customer);
				final String mobile = OmniUtil.maskMobile(mobileNo, locale);
				model.addAttribute("mobile", mobile);
				model.addAttribute("name", name);
				model.addAttribute("id", customer.getChcsNo());
				model.addAttribute("xname", SecurityUtil.setXyzValue(customer.getCustNm()));
				model.addAttribute("xid", SecurityUtil.setXyzValue(customer.getChcsNo()));
			}

			model.addAttribute("incsno", Integer.toString(incsno));
			model.addAttribute("incsNo", Integer.toString(incsno));
			model.addAttribute("xincsno", SecurityUtil.setXyzValue(Integer.toString(incsno)));

			if (StringUtils.hasText(customer.getMbrJoinDt())) {
				model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
			}

			// 채널 약관 동의여부 확인
			UmOmniUser omniUser = new UmOmniUser();
			if (incsno > 0) {
				omniUser.setIncsNo(Integer.toString(incsno));
			}
			omniUser.setChCd(chcd);

			if (!this.termsService.hasTermsAgree(omniUser)) { // ME-FO-A0105, 진입 채널에 대한 약관 출력 정보 조회
				TermsVo termsVo = new TermsVo();
				termsVo.setChCd(chcd);
				if (chcd.equals(OmniConstants.OSULLOC_CHCD)) {
					termsVo.setIncsNo(omniUser.getIncsNo());
				}
				List<TermsVo> termsList = this.termsService.getTerms(termsVo);
				model.addAttribute("terms", termsList);
			}
			TermsVo corpTermsVo = new TermsVo();
			corpTermsVo.setChCd(channel.getChCd());
			List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
			model.addAttribute("corptermslist", corpTermsList);
			model.addAttribute("home", channel.getHmpgUrl());
			model.addAttribute("homeurl", channel.getHmpgUrl());
			
			//20230404 채널 문자 수신 동의
			final String onlineChCd = ChannelPairs.getOnlineCd(chcd);
			WebUtil.setSession(OmniConstants.CH_CD_SESSION, chcd);
			TermsVo corpTermsVo2 = new TermsVo();
			corpTermsVo2.setChCd(onlineChCd);
			UmOmniUser omniUser2 = new UmOmniUser();
			omniUser2.setChCd(onlineChCd);
			if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
				TermsVo termsVo = new TermsVo();
				termsVo.setChCd(onlineChCd);
				List<TermsVo> termsList = this.termsService.getTermsChoice(termsVo);
//				model.addAttribute("terms", termsList);
				for(TermsVo vo : termsList) {
					if(vo.getMkSn()==-20) {
						model.addAttribute("terms_marketing", vo);
					}
				}
			}

			return "login/plogin_id_regist";
		} else {

			String ssoAuthUrl = this.ssoAuthorizeUrl.concat("?").concat(WebUtil.getSsoParamsAuth());
			return "redirect:" + ssoAuthUrl;
		}

	}

	@PostMapping("/idcheck")
	@ResponseBody
	public LoginResponse idCheck(@RequestBody final UserVo userVo) {
		LoginResponse response = new LoginResponse();
		final String loginid = StringUtils.hasText(SecurityUtil.getXValue(userVo.getEncId())) ? SecurityUtil.getXValue(userVo.getEncId()).replaceAll(" ", "").toLowerCase() : ""; // 2023-07-12 idcheck 시 공백 제거 및 소문자 처리
		log.debug("▶▶▶▶▶ [login id check] : {}", loginid);
		if (StringUtils.isEmpty(loginid)) {
			response.setResult("INVALID");
			response.setStatus(-1);
			response.setMessage("empty id");
		} else {

			if (StringUtils.hasText(loginid)) {
				CheckResponse checkresp = new Checker.Builder() //
						.checkType(CheckActor.Type.ID) //
						.checkValue(loginid) //
						.build() //
						.check();

				int cnt = this.joinService.getCountAvaiableUserId(loginid);
				if (cnt > 0) {
					response.setResult("EXIST");
					response.setStatus(-1);
					response.setMessage("exist id");
				} else {
					if (checkresp.getCode().equals("100")) {
						response.setResult("VALID");
					} else {
						response.setResult("INVALID");
					}
					response.setStatus(checkresp.getStatus());
					response.setMessage(checkresp.getMessage());
				}
			} else {
				response.setResult("VALID");
			}

		}
		log.debug("▶▶▶▶▶ [login id check] response : {}", StringUtil.printJson(response));
		return response;
	}

	@PostMapping("/pwdcheck")
	@ResponseBody
	public LoginResponse passwordCheck(@RequestBody final UserVo userVo) {
		LoginResponse response = new LoginResponse();
		final String loginpwd = userVo.getEncPwd();
		final String loginCpwd = userVo.getEncConfirmPwd();
		if (StringUtils.isEmpty(loginpwd)) {
			response.setResult("empty login parameters");
			response.setStatus(0);
			response.setMessage("empty login parameters");
		} else {
			CheckResponse checkresp = new Checker.Builder() //
					.checkType(CheckActor.Type.PASSWORD) //
					.checkValue(loginpwd) //
					.compareValue(loginCpwd) //
					.build() //
					.check();
			response.setResult(checkresp.getCode());
			response.setStatus(checkresp.getStatus());
			response.setMessage(checkresp.getMessage());
		}
		log.debug("▶▶▶▶▶ [login pwd check] response : {}", StringUtil.printJson(response));
		return response;
	}

	@PostMapping("/statuscheck")
	@ResponseBody
	public StatusCheckResponse statusCheck(@RequestBody final UserVo userVo) {
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final String encLoginid = userVo.getEncId();
		final String encLoginpw = userVo.getEncPwd();

		if (StringUtils.isEmpty(encLoginid) || StringUtils.isEmpty(encLoginpw)) {
			// LoginId Null Check & LoginPw Null Check - 2022-01-18 hjw0228
			log.error("▶▶▶▶▶▶ [/statuscheck] LoginId or LoginPw is null");
			final LoginStepVo loginStepVo = new LoginStepVo(LoginType.PWDFAIL, null, null, null);

			final int type = loginStepVo.getLoginType().getType();

			StatusCheckResponse response = new StatusCheckResponse();
			response.setStatus(type);
			response.setResultCode(Integer.toString(type));
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));

			return response;
		}

		log.debug("▶▶▶▶▶▶ [login status check] status check(id,pwd) : {}, {}", encLoginid, encLoginpw);

		final String loginId = SecurityUtil.getXValue(encLoginid);
		final String loginPwd = SecurityUtil.getXValue(encLoginpw);

		final LoginStepVo loginStepVo = this.loginService.loginStatusCheck(chCd, loginId, loginPwd);
		LoginType loginType = loginStepVo.getLoginType();
		log.debug("▶▶▶▶▶▶ [login status check] login type : {}, login step vo : {}", LoginType.get(loginType.getType()), StringUtil.printJson(loginStepVo));

		if (loginType == LoginType.DORMANCYFAIL) {
			throw new OmniException("시스템 오류가 발생하였습니다. 관리자에게 문의하시기 바랍니다.");
		}

		if (loginType == LoginType.PWDRESET) {
			WebUtil.setSession(OmniConstants.XID_SESSION, encLoginid);
			WebUtil.setSession(OmniConstants.XPW_SESSION, encLoginpw);
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(loginStepVo.getIncsNo()));
		} else if (loginType == LoginType.PWDCHANGE) {
			if (WebUtil.getSession(OmniConstants.SSOPARAM) != null) {
				SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
				WebUtil.setSession(OmniConstants.XID_SESSION, encLoginid);
				WebUtil.setSession(OmniConstants.XPW_SESSION, encLoginpw);
				WebUtil.setSession(OmniConstants.SESSION_DATA_KEY_SESSION, ssoParam.getSessionDataKey());
				WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(loginStepVo.getIncsNo()));
			}
		} else if (loginType == LoginType.LOGIN) {
			if (WebUtil.getSession(OmniConstants.SSOPARAM) != null) {
				SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
				WebUtil.setSession(OmniConstants.XID_SESSION, encLoginid);
				WebUtil.setSession(OmniConstants.XPW_SESSION, encLoginpw);
				WebUtil.setSession(OmniConstants.SESSION_DATA_KEY_SESSION, ssoParam.getSessionDataKey());
				WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(loginStepVo.getIncsNo()));
			}
		}

		final int type = loginType.getType();

		StatusCheckResponse response = new StatusCheckResponse();
		response.setStatus(type);
		response.setResultCode(Integer.toString(type));
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		response.setXincsno(SecurityUtil.setXyzValue(loginStepVo.getIncsNo()));
		if (loginType == LoginType.ALREADY_TRNS_CH) {
			UmChUser chUser = loginStepVo.getChUsers().get(0);
			if (chUser != null) {
				final Channel channel = this.commonService.getChannel(chUser.getChCd());
				response.setChannelName(channel.getChCdNm());
			}
		}

		if (loginType == LoginType.LOCK) {
			List<UmOmniUser> omniUsers = loginStepVo.getOmniUsers();
			if (omniUsers != null && omniUsers.size() > 0) {
				UmOmniUser omniUser = omniUsers.get(0);
				if (omniUser != null) {
					response.setRemainUnLockTime(omniUser.getUnlockTime());
					if (StringUtils.hasText(omniUser.getFailedLoginAttempts())) {
						response.setRemainUnLockSeconds(Integer.parseInt(omniUser.getFailedLoginAttempts()));
					}
				}
			}
			if(SecurityUtil.compareWso2Password(omniUsers.get(0).getUmUserPassword(),SecurityUtil.getXValue(encLoginpw))){ //올바른 패스워드 입력 시
				response.setCorrectPwd(1);
			}
		}

		log.debug("▶▶▶▶▶▶ [login status check] login type : {}, login response vo : {}", LoginType.get(loginType.getType()), StringUtil.printJson(response));
		return response;
	}

	// A0207 ID/PW 등록 01, A0207 ID/PW 등록 02 ( 채널약관 동의 여부 체크 )
	@GetMapping("/id-regist")
	public String regist(final Model model) {

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final String incsno = WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION);

		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("chcd", channel.getChCd());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("rv", this.config.resourceVersion());

		if (StringUtils.hasText(incsno)) {
			model.addAttribute(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsno));
		}

		// 채널 약관 동의여부 확인
		UmOmniUser omniUser = new UmOmniUser();
		if (StringUtils.hasText(incsno)) {
			omniUser.setIncsNo(incsno);
		}
		omniUser.setChCd(chcd);

		// if (!this.termsService.hasTermsAgree(omniUser)) { // ME-FO-A0105, 진입 채널에 대한 약관 출력 정보 조회
		TermsVo termsVo = new TermsVo();
		termsVo.setChCd(chcd);
		if (chcd.equals(OmniConstants.OSULLOC_CHCD)) {
			termsVo.setIncsNo(omniUser.getIncsNo());
		}
		List<TermsVo> termsList = this.termsService.getTerms(termsVo);
		model.addAttribute("terms", termsList);
		// }

		model.addAttribute("homeurl", channel.getHmpgUrl());
		model.addAttribute("home", channel.getHmpgUrl());
		TermsVo corpTermsVo = new TermsVo();
		corpTermsVo.setChCd(channel.getChCd());
		List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
		model.addAttribute("corptermslist", corpTermsList);
		// A0207 ID/PW 등록 01, A0207 ID/PW 등록 02 (채널약관 동의 여부 체크)
		
		//20230323 채널 문자 수신 동의
		final String onlineChCd = ChannelPairs.getOnlineCd(chcd);
		WebUtil.setSession(OmniConstants.CH_CD_SESSION, chcd);
		TermsVo corpTermsVo2 = new TermsVo();
		corpTermsVo2.setChCd(onlineChCd);
		UmOmniUser omniUser2 = new UmOmniUser();
		omniUser2.setChCd(onlineChCd);
		if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
			TermsVo termsVo2 = new TermsVo();
			termsVo2.setChCd(onlineChCd);
			List<TermsVo> termsList2 = this.termsService.getTermsChoice(termsVo2);
			for(TermsVo vo : termsList2) {
				if(vo.getMkSn()==-20) {
					model.addAttribute("terms_marketing", vo);
				}
			}
		}
		
		return "login/id_regist";
	}

	@GetMapping("/plogin-id-regist")
	public String ploginRegist(final Model model) {

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final String incsno = WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION);

		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("chcd", channel.getChCd());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("rv", this.config.resourceVersion());

		if (StringUtils.hasText(incsno)) {
			model.addAttribute(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsno));
		}

		// 채널 약관 동의여부 확인
		UmOmniUser omniUser = new UmOmniUser();
		if (StringUtils.hasText(incsno)) {
			omniUser.setIncsNo(incsno);
		}
		omniUser.setChCd(chcd);

		// if (!this.termsService.hasTermsAgree(omniUser)) { // ME-FO-A0105, 진입 채널에 대한 약관 출력 정보 조회
		TermsVo termsVo = new TermsVo();
		termsVo.setChCd(chcd);
		if (chcd.equals(OmniConstants.OSULLOC_CHCD)) {
			termsVo.setIncsNo(omniUser.getIncsNo());
		}
		List<TermsVo> termsList = this.termsService.getTerms(termsVo);
		model.addAttribute("terms", termsList);
		// }
		TermsVo corpTermsVo = new TermsVo();
		corpTermsVo.setChCd(channel.getChCd());
		List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
		model.addAttribute("corptermslist", corpTermsList);
		model.addAttribute("home", channel.getHmpgUrl());
		model.addAttribute("homeurl", channel.getHmpgUrl());
		
		//20230404 채널 문자 수신 동의
		final String onlineChCd = ChannelPairs.getOnlineCd(chcd);
		WebUtil.setSession(OmniConstants.CH_CD_SESSION, chcd);
		TermsVo corpTermsVo2 = new TermsVo();
		corpTermsVo2.setChCd(onlineChCd);
		UmOmniUser omniUser2 = new UmOmniUser();
		omniUser2.setChCd(onlineChCd);
		if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
			TermsVo termsVo2 = new TermsVo();
			termsVo2.setChCd(onlineChCd);
			List<TermsVo> termsList2 = this.termsService.getTermsChoice(termsVo2);
//			model.addAttribute("terms", termsList2);
			for(TermsVo vo : termsList2) {
				if(vo.getMkSn()==-20) {
					model.addAttribute("terms_marketing", vo);
				}
			}
		}

		// A0207 ID/PW 등록 01, A0207 ID/PW 등록 02 (채널약관 동의 여부 체크)
		return "login/plogin_id_regist";
	}

	@GetMapping("/cookie_info")
	public String cookiecheck(final SSOParam ssoParam, //
			final HttpServletRequest request, final Model model) throws UnsupportedEncodingException {

		ssoParam.setChannelCd(request.getParameter("channelCd"));
		ssoParam.setState(request.getParameter("state"));

		Map<String, Object> state = new HashMap<String, Object>();
		String stateParam = WebUtil.getStringParameter(request, "state", "");

		log.debug("*** state parameter : [{}]", stateParam);

		Properties props = new Properties();
		try {
			if (!StringUtils.isEmpty(stateParam)) {
				props.load(new StringReader(stateParam.substring(1, stateParam.length() - 1).replace(", ", "\n")));

				for (Map.Entry<Object, Object> e : props.entrySet()) {
					state.put((String) e.getKey(), (String) e.getValue());
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		if (state.get("cancelUri") != null && StringUtils.isEmpty(ssoParam.getCancelUri())) {
			ssoParam.setCancelUri((String) state.get("cancelUri"));
		}

		log.info("▶▶▶▶▶▶ [login page] sso param  : {}", StringUtil.printJson(ssoParam));

		final Channel channel = this.commonService.getChannel(ssoParam.getChannelCd());
		log.debug("▶▶▶▶▶▶ [login page] channel info : {}", StringUtil.printJson(channel));

		model.addAttribute("url", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("cancelUri", ssoParam.getCancelUri());

		boolean isMobile = WebUtil.isMobile();

		model.addAttribute("mobile", isMobile);

		return "info/cookie_info";
	}

	@GetMapping("/cross_site_info")
	public String crossSiteCheck(final SSOParam ssoParam, final HttpServletRequest request, final Model model) throws UnsupportedEncodingException {

		ssoParam.setChannelCd(request.getParameter("channelCd"));
		ssoParam.setState(request.getParameter("state"));

		Map<String, Object> state = new HashMap<String, Object>();
		String stateParam = WebUtil.getStringParameter(request, "state", "");

		log.debug("*** state parameter : [{}]", stateParam);

		Properties props = new Properties();
		try {
			if (!StringUtils.isEmpty(stateParam)) {
				props.load(new StringReader(stateParam.substring(1, stateParam.length() - 1).replace(", ", "\n")));

				for (Map.Entry<Object, Object> e : props.entrySet()) {
					state.put((String) e.getKey(), (String) e.getValue());
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		if (state.get("cancelUri") != null && StringUtils.isEmpty(ssoParam.getCancelUri())) {
			ssoParam.setCancelUri((String) state.get("cancelUri"));
		}

		log.info("▶▶▶▶▶▶ [login page] sso param  : {}", StringUtil.printJson(ssoParam));

		final Channel channel = this.commonService.getChannel(ssoParam.getChannelCd());
		log.debug("▶▶▶▶▶▶ [login page] channel info : {}", StringUtil.printJson(channel));

		model.addAttribute("url", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("cancelUri", ssoParam.getCancelUri());

		boolean isMobile = WebUtil.isMobile();

		model.addAttribute("mobile", isMobile);

		return "info/cross_site_info";
	}

	@GetMapping("/ga-window-close")
	public void gaWindowClosePush(final Model model) {

	}

	/**
	 * <pre>
	 * comment  : lock 계정 인증 후 ci 재설정 및 검사
	 * author   : judahye
	 * date     : 2023. 4. 11. 오후 5:14:23
	 * </pre>
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/lock_cert", method = { RequestMethod.GET, RequestMethod.POST })
	public String lock_cert(@RequestParam(required = false) final String type, //
				@RequestParam(required = false) final String itg, //
				final HttpServletResponse servletResponse, //
				final RedirectAttributes redirectAttributes, //
				final Locale locale,
				HttpServletRequest request,
				Model model) {
		
		CertResult certResult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
		if (certResult == null) {
			return "redirect:/login?" + WebUtil.getSsoParams();
		}
		String incsNo = (String) SecurityUtil.getXValue((String) WebUtil.getSession(OmniConstants.XINCS_NO_SESSION));

//		String name = OmniUtil.maskUserName((String) SecurityUtil.getXValue((String) WebUtil.getSession(OmniConstants.XNM_SESSION)),locale);
		String loginid = OmniUtil.maskUserId((String) SecurityUtil.getXValue((String) WebUtil.getSession(OmniConstants.XID_SESSION)));
		
		String xid = (String) WebUtil.getSession(OmniConstants.XID_SESSION);
		String xpw = (String) WebUtil.getSession(OmniConstants.XPW_SESSION);
		model.addAttribute("xpw", xpw);
		model.addAttribute("xid", xid);
		model.addAttribute("incsNo", incsNo); 
		model.addAttribute("name", certResult.getName());
		model.addAttribute("id", loginid);

		//고객통합에 ci 업데이트 요청
		//고객통합 호출 시, 정상 아니면 info/lock_recheck_failed 화면으로 이동
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		UpdateCustCino updateCustCino = new UpdateCustCino();
		updateCustCino.setChgChCd(chCd);
		updateCustCino.setIncsNo(Integer.parseInt(incsNo));
		updateCustCino.setLschId("OCP");
		updateCustCino.setCiNo(certResult.getCiNo());
		
		UpdateAbusingCiResponse response= customerApiService.updateAbusingCustCino(updateCustCino);
		log.debug("▶▶▶▶▶▶ [lock_cert] update Abusing CustCino response : {}", StringUtil.printJson(response));
		//응답 별 화면 분기처리
		if("ICITSVCOM000".equals(response.getRsltCd())) {
			//
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(incsNo);
			final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
				String mobile[] = StringUtil.splitMobile(certResult.getPhone());
				log.debug("custPhoneCert : {}, incsNo : {}", certResult.getPhone(), incsNo);
				
				//cert 계정 정보와 고객통합 계정 정보가 일치하지 않는 경우 => 20231220 ci 변경 성공 시로 순서변경
				//if(!certResult.getCiNo().equals(customer.getCiNo())) {
				// 인증받은 휴대폰 번호로 update
				UpdateCustVo updateCustVo = new UpdateCustVo();
				updateCustVo.setIncsNo(incsNo);
				updateCustVo.setCustNm(certResult.getName());
				updateCustVo.setAthtDtbr(certResult.getBirth());
				updateCustVo.setCellTidn(mobile[0]);
				updateCustVo.setCellTexn(mobile[1]);
				updateCustVo.setCellTlsn(mobile[2]);
				
				String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
				chcd = "000";
				log.debug("▶▶▶▶▶▶ [cert join] chcd : {}", chcd);
				updateCustVo.setChCd(chcd);
				updateCustVo.setChgChCd(chcd);
				updateCustVo.setLschId("OCP");
				
				UpdateCustResponse updateCustResponse = this.customerApiService.updateCust(updateCustVo);
				
				if ("ICITSVCOM999".equals(updateCustResponse.getRsltCd())) {
					// TODO : update error
					final String updateCustRsltCd = updateCustResponse.getRsltCd();
					JoinDivisionType updateJoinType = JoinDivisionType.getByCode(updateCustRsltCd);
					
					log.info("▶▶▶▶▶▶ [cert join] update customer erro : {}", StringUtil.printJson(updateJoinType));
				}
				//}
			} else {
				if (customer != null && "ICITSVCOM001".equals(customer.getRsltCd())) {
					throw new OmniException("회원 정보가 존재하지 않습니다.");
				}
			}
			//옴니도 변경 요청(DB lock 해제)
			AbusingLockVo abusingLockVo = new AbusingLockVo();
			abusingLockVo.setIncsNo(Integer.parseInt(incsNo));
			abusingLockVo.setAcctLockLogTpCd("ICITSVCOM000");
			abusingLockVo.setChCd(chCd);
			abusingLockVo.setLockCancImpsRsnCd("");
			abusingLockVo.setClntIp((String) WebUtil.getSession(OmniConstants.Client_IP));
			abusingLockVo.setClntUaVl((String) WebUtil.getSession(OmniConstants.Client_Agent));
			log.debug("▶▶▶▶▶▶ [lock_cert] lock User Omni Update : {}", StringUtil.printJson(abusingLockVo));
			customerApiService.lockUserOmniUpdate(abusingLockVo);
			return "login/lock_recheck_result";
		}else if ("ICITSVCOM001".equals(response.getRsltCd())) {
			return "info/lock_recheck_failed";
		}else if ("ICITSVCOM003".equals(response.getRsltCd())) {
			return "info/lock_recheck_failed";
		}else if ("ICITSVCOM007".equals(response.getRsltCd())) {
			//ci 값이 다른 계정에 있거나 최근 1개월 동안 동일 CI 값이 특정 계정에 업로드된 이력이 있는 경우
			return "info/lock_user_check";
		}

		return "login/lock_recheck_result";
	}
	
//	@PostMapping("/login/web2app/callback")
	public String loginWeb2AppStep(
	/* final String authkey */) throws UnsupportedEncodingException, ParseException {
		
		String web2appIdreq =(String) WebUtil.getSession(OmniConstants.Web2App_Id);
		
		String result = "failed";
		if(StringUtils.isEmpty(web2appIdreq)) {
			log.debug("▶▶▶▶▶▶ [web2AppLoginStepCheck] web2appId session null ");
			return result;
		}
		Web2AppVo web2AppVo = new Web2AppVo();
		web2AppVo.setWeb2appid(web2appIdreq);
		
		//세션에 저장된 uuid로 accessToken 조회
		Web2AppVo resutlWeb2AppVo = mgmtService.selectWeb2AppData(web2appIdreq);
		
		if(resutlWeb2AppVo == null || StringUtils.isEmpty(resutlWeb2AppVo.getAccesstoken())) {
			log.debug("▶▶▶▶▶▶ [loginWeb2AppStep] resutlWeb2AppVo : 필수 값 누락");
			return "cancel";
		}
		//state 값 required 가 아닐 때
		if(!"required".equals(resutlWeb2AppVo.getUserathtst())) {
			return "failed";
		}

		//uuid accessToken refreshToken DB에서 쿼리로 유효한 값인지 검증
		boolean token_valid = false;
		//idn_oauth2_access_token 테이블 토큰 검증
		HashMap<String, String> access_token_valid = mgmtService.selectTokenValid(resutlWeb2AppVo);
		try {
			log.debug("▶▶▶▶▶▶ [loginWeb2AppStep] access_token_valid : "+access_token_valid.get("token_state")+" // "+access_token_valid.get("authz_user"));
		} catch (Exception e) {
			//인증 실패(AccessToken 없음)
			resutlWeb2AppVo.setUserathtst("failed");
			mgmtService.updateWeb2AppData(resutlWeb2AppVo);
			return "failed";
		}
		log.debug("▶▶▶▶▶▶ [loginWeb2AppStep] access_token_valid : "+access_token_valid.get("token_state")+" // "+access_token_valid.get("authz_user"));
		if(!StringUtils.isEmpty(access_token_valid) && !StringUtils.isEmpty(access_token_valid.get("authz_user"))) {
			resutlWeb2AppVo.setUsername(access_token_valid.get("authz_user"));
			if("ACTIVE".equals(access_token_valid.get("token_state"))) {
				if("required".equals(resutlWeb2AppVo.getUserathtst())) {
					//uuid 있으면 값 업데이트
					Date currentDate = new Date();
					DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
					Date inputDate = dateFormat.parse(resutlWeb2AppVo.getExprdttm());
					long differenceInMillis = currentDate.getTime() - inputDate.getTime();
					if(differenceInMillis<0) {
						//인증 성공
						log.debug("▶▶▶▶▶▶ [loginWeb2AppStep] 인증 성공 completed : "+ resutlWeb2AppVo.getUsername());
						int updateResult = mgmtService.updateWeb2AppData(resutlWeb2AppVo);
						if(updateResult>0) {
							token_valid=true;
						}
					}else {
						//인증 만료
						resutlWeb2AppVo.setUserathtst("expired");
						mgmtService.updateWeb2AppData(resutlWeb2AppVo);
						return "cancel";
					}
				}else {
					//인증 실패(토큰 상태 rquired 가 아님)
					resutlWeb2AppVo.setUserathtst("failed");
					mgmtService.updateWeb2AppData(resutlWeb2AppVo);
				}
			}else if ("EXPIRED".equals(access_token_valid.get("token_state"))) {
				//인증 만료(access/refresh 토근 상태 != ACTIVE )
				resutlWeb2AppVo.setUserathtst("expired");
				mgmtService.updateWeb2AppData(resutlWeb2AppVo);
			}else {
				//인증 실패(토큰 상태 active/expired 가 아님)
				resutlWeb2AppVo.setUserathtst("failed");
				mgmtService.updateWeb2AppData(resutlWeb2AppVo);
			}
		}else if (StringUtils.isEmpty(access_token_valid)) {
			//인증 실패(토큰 상태 없음)
			resutlWeb2AppVo.setUserathtst("failed");
			mgmtService.updateWeb2AppData(resutlWeb2AppVo);
		}
		
		if(token_valid == false) {
			//로그인 이동 부분
//			return "redirect:/login?" + WebUtil.getSsoParams();
			result = "failed";
			return result;
		}else {
			//검증 true 일 경우
			result = "true";
			//다시 암호화 하여, wso2로 전달 (accesstoken,uuid,refreshtoken 을 json으로 암호화해서 전달)
			Map<String, Object> params = new HashMap<>();
			params.put("uuid", resutlWeb2AppVo.getWeb2appid());
			params.put("accessToken", resutlWeb2AppVo.getAccesstoken());
			
//			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//			String json = gson.toJson(params);
//			
//			log.debug("▶▶▶▶▶▶ [loginWeb2AppStep] params: {}", StringUtil.printJson(params));
//			
//			StringBuilder authurl = new StringBuilder();
//			authurl.append(this.commonAuthUrl); //
//			authurl.append("?authkey=").append(URLEncoder.encode(SecurityUtil.setXyzValue(json), StandardCharsets.UTF_8.name()));
//			return "redirect:" + authurl.toString();
			return result;
		}
	}
	
	@GetMapping("/login/web2app/callback")
	public String loginWeb2AppAuth(final Model model, RedirectAttributes redirect) throws UnsupportedEncodingException, ParseException {
		String web2appId =(String) WebUtil.getSession(OmniConstants.Web2App_Id);
		if(StringUtils.isEmpty(web2appId)) {
			log.debug("▶▶▶▶▶▶ [web2AppLoginStepCheck] web2appId session null ");
//			return "info/info_error";
			return "redirect:/login?" + WebUtil.getSsoParams();
		}
		Web2AppVo web2AppVo = new Web2AppVo();
		web2AppVo.setWeb2appid(web2appId);
		
		Web2AppVo resutlWeb2AppVo = mgmtService.selectWeb2AppData(web2appId);
		//다시 암호화 하여, wso2로 전달 (accesstoken,uuid,refreshtoken 을 json으로 암호화해서 전달)
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", resutlWeb2AppVo.getWeb2appid());
		params.put("accessToken", resutlWeb2AppVo.getAccesstoken());
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		log.debug("▶▶▶▶▶▶ [loginWeb2AppStep] params: "+json);
		
//		StringBuilder authurl = new StringBuilder();
//		authurl.append(this.commonAuthUrl); //
//		authurl.append("?authKey=").append(URLEncoder.encode(SecurityUtil.setXyzValue(json), StandardCharsets.UTF_8.name()));
//	    redirect.addFlashAttribute("authKey", SecurityUtil.setXyzValue(json));
//		return "redirect:" + authurl.toString();
		
		String authKey = SecurityUtil.setXyzValue(json);
		log.info("▶▶▶▶▶▶ [loginWeb2AppStep] authKey: " + authKey);
		model.addAttribute("authKey", authKey);
		model.addAttribute("actionurl", this.commonAuthUrl);
		final String sessionDataKey = WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION);
		model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, sessionDataKey);
		
		return "login/web2app_login";
	}
	
	@SuppressWarnings("deprecation")
	@PostMapping("/web2App/step")
	@ResponseBody
	public String web2AppLoginStep(final Model model, @RequestBody String consumerkey) {
		if(StringUtils.isEmpty(consumerkey)) {
			return null;
		}
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		String web2app_id = UUID.randomUUID().toString();
		Web2AppVo web2AppVo = new Web2AppVo();
		web2AppVo.setWeb2appid(web2app_id);
		web2AppVo.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		
		JsonObject jsonObj = new JsonParser().parse(consumerkey).getAsJsonObject();
		String consumer_key = jsonObj.get("consumerkey").getAsString();
		
		web2AppVo.setConsumerkeyid(mgmtService.selectConsumerAppId(consumer_key));
		log.debug("▶▶▶▶▶▶ [web2AppLoginStep] web2app_id : {} , consumerkey : {}, chCd : {}", StringUtil.printJson(web2app_id), web2AppVo.getConsumerkeyid(), web2AppVo.getChcd());
		
		WebUtil.setSession(OmniConstants.Web2App_Id, web2app_id);
		
		web2AppVo.setRequiretime(DateUtil.getCurrentDateTimeString());
		web2AppVo.setExpiretime(DateUtil.getDayMinute(DateUtil.getCurrentDateString("yyyy-MM-dd HH:mm:ss"), this.config.isWeb2AppExpireTime(profile), ""));
		
		//uuid 생성
		mgmtService.insertWeb2AppData(web2AppVo);
		return SecurityUtil.setXyzValue(web2app_id) ;
	}
	
	@PostMapping("/web2App/step/check")
	@ResponseBody
	public String web2AppLoginStepCheck() throws ParseException, UnsupportedEncodingException {
		
		String web2appId =(String) WebUtil.getSession(OmniConstants.Web2App_Id);
		String response = "true";
		if(StringUtils.isEmpty(web2appId)) {
			log.debug("▶▶▶▶▶▶ [web2AppLoginStepCheck] web2appId session null ");
			response = "failed";
			return response;
		}
		Web2AppVo web2AppVo = new Web2AppVo();
		web2AppVo.setWeb2appid(web2appId);
		
		Web2AppVo resutlWeb2AppVo = mgmtService.selectWeb2AppData(web2appId);
		//조건 : DB 값 없거나, token 없음
		if(resutlWeb2AppVo == null || StringUtils.isEmpty(resutlWeb2AppVo.getAccesstoken()) || StringUtils.isEmpty(resutlWeb2AppVo.getWeb2appid())) {
			response= "cancel";
			return response;
		}
		//state 값 required 가 아닐 때
		if(!"required".equals(resutlWeb2AppVo.getUserathtst())) {
			response= "failed";
			return response;
		}
		
		log.debug("▶▶▶▶▶▶ [web2AppLoginStepCheck] resutlWeb2AppVo : {} , result : {}", StringUtil.printJson(resutlWeb2AppVo), response);
		response = this.loginWeb2AppStep();
		return response;
	}
	
	@PostMapping("/login/web2app/test")
	// @RequestMapping(value = "/login/step", method = { RequestMethod.GET, RequestMethod.POST })
	public String loginWeb2AppTest( //
			final AuthVo authVo, //
			final SSOParam ssoParam, //
			final HttpServletRequest request, //
			final HttpServletResponse response, //
			final HttpSession session, //
			final Model model, //
			final Locale locale) throws UnsupportedEncodingException, ParseException {
		
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final String sessionDataKey = WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION);
		if (StringUtils.isEmpty(sessionDataKey)) {
//			throw new IllegalArgumentException("session data key must have value!!!");

			log.info("session data key must have value!!!");
			model.addAttribute("oauthErrorCode", "9999");
			model.addAttribute("oauthErrorMsg", "session data key must have value.");
			model.addAttribute("message", "세션이 만료되었습니다.재접속 해주세요!");

			return "wso2/oauth2_error";

		} else {
			// authVo.setSessionDataKey(sessionDataKey);
		}
		
		log.debug("▶▶▶▶▶▶ [web2app test step] authVo : {}", StringUtil.printJson(authVo));

		final String xid = StringUtils.hasText(authVo.getXid()) ? authVo.getXid() : WebUtil.getStringSession(OmniConstants.XID_SESSION);
		final String xpw = StringUtils.hasText(authVo.getXpw()) ? authVo.getXpw() : WebUtil.getStringSession(OmniConstants.XPW_SESSION);

		if (StringUtils.hasText(xid)) {
			WebUtil.setSession(OmniConstants.XID_SESSION, xid);
		}
		if (StringUtils.hasText(xpw)) {
			WebUtil.setSession(OmniConstants.XPW_SESSION, xpw);
		}

		model.addAttribute("rv", this.config.resourceVersion());

		final String loginid = SecurityUtil.getXValue(xid, false);
		final String loginpw = SecurityUtil.getXValue(xpw, false);

		final String cookieloginid = URLEncoder.encode(loginid.trim(), StandardCharsets.UTF_8.name());
		
		// 아이디 저장 옵션
		// TODO 전환 사용자인 경우 아이디 선택에 따라서 아이디 저장 쿠키 삭제할 필요있음.
		// 암호화 하여 사용
		if (StringUtils.hasText(authVo.getIdSaveOption()) && "Y".equals(authVo.getIdSaveOption())) {
			WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, cookieloginid);
			// WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, SecurityUtil.setXyzValue(loginid.trim()));
		} else {
			WebUtil.removeCookie(response, OmniConstants.SAVE_ID_COOKIE_NAME);
			WebUtil.removeCookie(response, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + cookieloginid);
			WebUtil.setSession(OmniConstants.AUTO_LOGIN_SESSION, "N");
		}
		
		// 모바일인 경우만 처리 - 자동로그인 옵션 선택 - commonauth 에 자동 로그인 처리옵션 추가
		boolean isMobile = WebUtil.isMobile();

		if (isMobile) {

			if (StringUtils.hasText(authVo.getAutoLoginOption()) && "Y".equals(authVo.getAutoLoginOption())) {
				if (StringUtils.hasText(authVo.getIdSaveOption()) && "Y".equals(authVo.getIdSaveOption())) {
					WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, cookieloginid);
					// WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, SecurityUtil.setXyzValue(loginid.trim()));
				} else {
					WebUtil.removeCookie(response, OmniConstants.SAVE_ID_COOKIE_NAME);
				}
				log.info("▶▶▶▶▶▶ [web2app test step] auto login set...");
				WebUtil.setCookies(response, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + cookieloginid, authVo.getAutoLoginOption());
				WebUtil.setSession(OmniConstants.AUTO_LOGIN_SESSION, "Y");
			} else {
				if (StringUtils.hasText(authVo.getIdSaveOption()) && "Y".equals(authVo.getIdSaveOption())) {
					WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, cookieloginid);
					// WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, SecurityUtil.setXyzValue(loginid.trim()));
				} else {
					WebUtil.removeCookie(response, OmniConstants.SAVE_ID_COOKIE_NAME);
				}
				WebUtil.removeCookie(response, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + cookieloginid);
				WebUtil.setSession(OmniConstants.AUTO_LOGIN_SESSION, "N");
			}

		} else {
			WebUtil.removeCookie(response, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + cookieloginid); // 모바일이 아닌 경우는 자동로그인 없음.
		}

		if (StringUtils.isEmpty(loginid.trim()) || StringUtils.isEmpty(loginpw)) {
			log.warn("▶▶▶▶▶▶ [web2app test step] login info(id or pws) is null.....");
			final String ssoParams = WebUtil.getSsoParams();
			final String entryPage = "/entry?" + ssoParams;
			WebUtil.removeCookie(response, OmniConstants.SAVE_ID_COOKIE_NAME);
			WebUtil.removeCookie(response, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + cookieloginid);
			return "redirect:" + entryPage;
		}
		
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel)); // add
		model.addAttribute("chcd", channel.getChCd());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("entry", OmniUtil.getOfflineParam());
		
		final LoginStepVo loginstep = this.loginService.loginStep(chCd, loginid.trim(), loginpw, false);

		LoginType loginType = loginstep.getLoginType();
		final List<UmOmniUser> omniUsers = loginstep.getOmniUsers();
		final String loginIncsNo = loginstep.getIncsNo();
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;

		log.debug("▶▶▶▶▶▶ [web2app test step] lognin type : {}", StringUtil.printJson(loginstep));
		
		if (loginType == LoginType.PWDCHANGE) {

			model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, sessionDataKey);
			model.addAttribute(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginid.trim()));
			model.addAttribute(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(loginpw));
			model.addAttribute(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(loginIncsNo));

			return "mgmt/change_pwd_campaign";
		} else if (loginType == LoginType.PWDRESET) {

			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(omniUsers.get(0).getFullName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(omniUsers.get(0).getUmUserName()));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(loginIncsNo));

			return "redirect:/mgmt/reset-pwd";
		} else if (loginType == LoginType.LOGIN) {

			model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, sessionDataKey);
			model.addAttribute(OmniConstants.XID_SESSION, loginid.trim());
			model.addAttribute(OmniConstants.XPW_SESSION, loginpw);
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(loginIncsNo));

			if (StringUtils.hasText(ssoParam.getAuthFailure()) && StringUtil.isTrue(ssoParam.getAuthFailure())) {
				if (StringUtils.hasText(ssoParam.getAuthFailureMsg())) {
					final String msg = this.messageSource.getMessage(ssoParam.getAuthFailureMsg(), null, LocaleUtil.getLocale());
					model.addAttribute("authFailure", ssoParam.getAuthFailure());
					model.addAttribute("authFailureMsg", msg);
				}
			}

			// 자동 로그인 처리되면 로그인 정보(commonAuthId)가 컴퓨터에 저장됨.

			boolean autologin = OmniUtil.getAutoLogin(request, loginid);

			if (autologin) {
				log.debug("▶▶▶▶▶ [web2app test step] save auto login? {}", autologin);
				WebUtil.setSession(OmniConstants.AUTO_LOGIN_SESSION, "Y");
				model.addAttribute(OmniConstants.AUTO_LOGIN, true);
			}
			final String authType = this.config.commonAuthType();
			log.debug("▶▶▶▶▶▶ [web2app test step] wso2 common auth type : {}", authType);
			
			// Web2App 테스트를 위해 password grant 호출
			String credential = "WWpoTWVSSEJnY1ZVeHRuTTZWZENJRzVNaTZNYTpsZWx6ajN1ajRzZ0VERDVhV2t1MnVtZGpfWXdh";
			TokenData tokenData = customerApiService.getTokenByPasswordGrant(loginid, loginpw, credential);
			
			// Web2App 테스트를 위해 uuid 생성
			Map<String, Object> params2 = new HashMap<>();
			params2.put("consumerkey", "YjhMeRHBgcVUxtnM6VdCIG5Mi6Ma");
			
			Gson gson2 = new GsonBuilder().disableHtmlEscaping().create();
			String json2 = gson2.toJson(params2);
			String uuidResult = this.web2AppLoginStep(model, json2);
			
			// Web2App 테스트를 위해 Web2App 인증키 전송 API 호출
			if(!authVo.getWeb2AppType().equals("2")) { //access_token authKey 미전송 케이스
				Map<String, Object> params = new HashMap<>();
				params.put("uuid", SecurityUtil.getXValue(uuidResult));
				params.put("accessToken", tokenData.getAccess_token());
				
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				String json = gson.toJson(params);
				
				log.debug("▶▶▶▶▶▶ [loginWeb2AppStepTest] params: "+json);
				
				AuthKeyVo keyVo = new AuthKeyVo();
				keyVo.setAuthKey(SecurityUtil.setXyzValue(json));
				
				this.customerApiService.sendWeb2AppAuthKeyApi(keyVo);
			}else {
				log.debug("▶▶▶▶▶▶ [loginWeb2AppStepTest] API 전송 안함");
			}
			
			// Web2App 테슽를 위해 Web2App 콜백 페이지 이동
			String loginResult = this.loginWeb2AppStep();
			if("true".equals(loginResult)) {
				log.debug("▶▶▶▶▶▶ [web2app test step]  : 로그인 성공");
				return "redirect:/login/web2app/callback";		
			}else {
				log.debug("▶▶▶▶▶▶ [web2app test step]  : 로그인 실패");
				return "redirect:/login?" + WebUtil.getSsoParams();
			}
		} 
		
		return "login/login_step";
	}
	
	@GetMapping("/login/web2app/complete")
	public String web2AppLoginSuccess(Model model) {
		String uuid = (String)WebUtil.getSession("Web2AppUUID");
		String accessToken = (String)WebUtil.getSession("Web2AppAccessToken");
		if(StringUtils.isEmpty(uuid) || StringUtils.isEmpty(accessToken)) {
			log.debug("▶▶▶▶▶ [web2App Login Success] Web2App Login uuid/accessToken session null");
			return "info/info_error";
		}
		
		log.debug("▶▶▶▶▶ [web2App Login Success] Web2App Login Success");
		Web2AppVo web2AppVo = new Web2AppVo();
		web2AppVo.setWeb2appid(uuid);
		web2AppVo.setAccesstoken(accessToken);
		log.debug("▶▶▶▶▶▶  [web2App Login Success] Web2App Login Check: {}", StringUtil.printJson(web2AppVo));
		
		int result=this.mgmtService.updateWeb2AppAuthKey(web2AppVo);
		//로그 적재 필요
		if(result > 0) {
			log.debug("▶▶▶▶▶▶  [web2App Login Success] Web2App Login Check : accessToken update");
		}else {
			log.debug("▶▶▶▶▶▶  [web2App Login Success] Web2App Login Check : 해당 uuid 미존재");
			return "info/info_error";
		}
		
		model.addAttribute("web2App_userId", WebUtil.getSession("web2App_userId"));

		return "/info/web2app_success";
	}
	
	@GetMapping("/login/web2app/cancel")
	public String web2AppLoginCancel(Model model) {
		log.debug("▶▶▶▶▶ [web2App Login Success] Web2App Login Cancel");
		
		model.addAttribute("web2App_userId", WebUtil.getSession("web2App_userId"));
		
		return "/info/web2app_cancel";
	}
	
	@GetMapping("/login/web2app/loginfailed")
	public String web2AppLoginFailed(final Model model, final String chCd) {
		log.debug("▶▶▶▶▶ [web2App Login Success] Web2App Login Failed");
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		if(StringUtils.isEmpty(chCd)) {
			return "info/info_error";
		}
		
		if(StringUtils.isEmpty(config.isAppLoginPath(chCd, profile))) {
			final Channel appchannel = this.commonService.getChannel(config.isAppLoginPath(chCd, profile));
			if(appchannel == null) {
				return "info/info_error";
			}
			model.addAttribute("appLoginChNm",appchannel.getChCdNm());
		}else {
			//앱이 없는 채널 : 지정한 채널의 앱으로 로그인
			final Channel appchannel = this.commonService.getChannel(config.isAppLoginPath(chCd, profile));
			if(appchannel == null) {
				return "info/info_error";
			}
			model.addAttribute("appLoginChNm",appchannel.getChCdNm());
		}
		return "/info/web2app_failed";
	}
	
	/**
	 * <pre>
	 * comment  : web2app login 분기 처리 화면
	 * author   : judahye
	 * date     : 2023. 9. 14. 오후 1:43:13
	 * </pre>
	 * @param authKey : 암호화 된 uuid
	 * @param accessToken : 암호화 되지 않은 accessToken
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@PostMapping("/login/web2app/logincheck")
	public String web2AppLoginCheck(final Web2AppCallBackVo web2AppCallBackVo, Model model) throws UnsupportedEncodingException {
		log.debug("▶▶▶▶▶ [web2App Login Success] Web2App Login Check authKey : {}, uuid : {}, accessToken : {}", web2AppCallBackVo.getAuthKey(), SecurityUtil.getXValue(web2AppCallBackVo.getAuthKey()), web2AppCallBackVo.getAccessToken());
		if(StringUtils.isEmpty(web2AppCallBackVo.getAuthKey()) || SecurityUtil.getXValue(web2AppCallBackVo.getAuthKey()) == null || StringUtils.isEmpty(SecurityUtil.getXValue(web2AppCallBackVo.getAuthKey()))	|| StringUtils.isEmpty(web2AppCallBackVo.getAccessToken())) {
			return "info/info_error";
		}
		
		Web2AppVo web2AppVo = new Web2AppVo();

		web2AppVo.setWeb2appid(SecurityUtil.getXValue(web2AppCallBackVo.getAuthKey()));
		web2AppVo.setAccesstoken(web2AppCallBackVo.getAccessToken());
		if(StringUtils.isEmpty(web2AppVo.getWeb2appid()) || StringUtils.isEmpty(web2AppVo.getAccesstoken())) {
			log.debug("▶▶▶▶▶▶  [web2App Login Success] Web2App Login Check 필수 값 누락");
			return "info/info_error";
		}
		log.debug("▶▶▶▶▶▶  [web2App Login Success] Web2App Login Check: {}", StringUtil.printJson(web2AppVo));
		
		Web2AppVo resutlWeb2AppVo = mgmtService.selectWeb2AppData(web2AppVo.getWeb2appid());
		
		if(resutlWeb2AppVo == null || StringUtils.isEmpty(resutlWeb2AppVo.getWeb2appid())) {
			log.debug("▶▶▶▶▶▶ [web2App Login Success] resutlWeb2AppVo : uuid에 해당하는 데이터 미존재");
			return "info/info_error";
		}
		resutlWeb2AppVo.setAccesstoken(web2AppVo.getAccesstoken());
		HashMap<String, String> access_token_valid = mgmtService.selectTokenValid(resutlWeb2AppVo);
		
		if(access_token_valid == null || access_token_valid.isEmpty()) {
			log.debug("▶▶▶▶▶▶  [web2App Login Success] access token 미존재");
			//return "info/info_error";
			return "redirect:/login/web2app/loginfailed?chCd=" + resutlWeb2AppVo.getChcd() ;
		}
		
		if(StringUtils.isEmpty(access_token_valid.get("token_state")) || !"ACTIVE".equals(access_token_valid.get("token_state"))) {
			log.debug("▶▶▶▶▶▶  [web2App Login Success] access_token state not active OR access_token empty");
			//return "info/info_error";
			return "redirect:/login/web2app/loginfailed?chCd=" +resutlWeb2AppVo.getChcd() ;
		}
		
		log.debug("▶▶▶▶▶▶ [web2AppLoginCheck] access_token User ID : "+access_token_valid.get("authz_user"));
		if(!StringUtils.isEmpty(access_token_valid.get("authz_user"))) {
			model.addAttribute("web2App_userId", access_token_valid.get("authz_user"));
			WebUtil.setSession("web2App_userId", access_token_valid.get("authz_user"));
		}else {
			log.debug("▶▶▶▶▶▶  [web2App Login Success] access_token user ID 미존재");
			return "info/info_error";
		}
		
		WebUtil.setSession("Web2AppUUID", web2AppVo.getWeb2appid());
		WebUtil.setSession("Web2AppAccessToken", web2AppVo.getAccesstoken());

		return "/info/web2app_check";
	}

}
