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
 * Date   	          : 2020. 7. 23..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.join.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.WebUtils;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.cert.service.CertService;
import com.amorepacific.oneap.auth.cert.vo.CertData;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.ga.GaTaggingUtils;
import com.amorepacific.oneap.auth.ga.OmniGaTaggingConstants;
import com.amorepacific.oneap.auth.ga.vo.GaTagData;
import com.amorepacific.oneap.auth.join.service.JoinService;
import com.amorepacific.oneap.auth.join.vo.JoinRequest;
import com.amorepacific.oneap.auth.join.vo.JoinResponse;
import com.amorepacific.oneap.auth.membership.service.MembershipService;
import com.amorepacific.oneap.auth.membership.vo.MembershipUserInfo;
import com.amorepacific.oneap.auth.mgmt.service.MgmtService;
import com.amorepacific.oneap.auth.social.handler.SnsAuth;
import com.amorepacific.oneap.auth.social.vo.SnsTermsVo;
import com.amorepacific.oneap.auth.step.ApiOfflineProcessStep;
import com.amorepacific.oneap.auth.step.ApiOnlineConvTemsProcessStep;
import com.amorepacific.oneap.auth.step.ApiOnlineJoinProcessStep;
import com.amorepacific.oneap.auth.step.ApiOnlineProcessStep;
import com.amorepacific.oneap.auth.step.ApiOnlineTermsProcessStep;
import com.amorepacific.oneap.auth.terms.service.TermsService;
import com.amorepacific.oneap.auth.terms.vo.TermsVo;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.exception.OmniException;
import com.amorepacific.oneap.common.types.JoinDivisionType;
import com.amorepacific.oneap.common.types.JoinType;
import com.amorepacific.oneap.common.types.LoginType;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.LocaleUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.ChatbotParam;
import com.amorepacific.oneap.common.vo.JoinStepVo;
import com.amorepacific.oneap.common.vo.Marketing;
import com.amorepacific.oneap.common.vo.OfflineParam;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.Types;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.join.ChannelParam;
import com.amorepacific.oneap.common.vo.join.JoinApplyRequest;
import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.amorepacific.oneap.common.vo.sns.SnsType;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;
import com.amorepacific.oneap.common.vo.user.UserData;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.join 
 *    |_ JoinController.java
 *    
 *    
 *    
 * 회원가입 - 온라인
 * X X X 탈퇴 후 30 일 이내 A0104
 * X X X 신규고객 A0101
 * X X O 자체고객 A0101
 * O X X 타오프라인 경로 자체 ) 가입 고객  A0103 -> A0207 채널약관 동의 목록 노출 화면
 * O X O 해당 경로 자체 만 가입된 고객 A0103 -> A0202 경로 자체 고객 ID 사용가능
 * O X O 해당 경로 자체 만 가입된 고객 A0103 -> A0203 경로 자체 고객 ID 타인 사용
 * O O X 경로 자체 ) 첫 방문 뷰티포인트 고객 A0103 -> A0105
 * O O O 이미 가입된 고객 A0103
 * 
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 23.
 * @version : 1.0
 * @author : takkies
 */
@Controller
@RequestMapping("/join")
@Slf4j
public class JoinController {

	@Value("#{${sns.types}}")
	private Map<String, String> snsTypes;

	@Value("${wso2.ssocommonauthurl}")
	private String commonAuthUrl;

	@Autowired
	private JoinService joinService;

	@Autowired
	private CertService certService;

	@Autowired
	private TermsService termsService;

	@Autowired
	private MgmtService mgmtService;

	@Autowired
	private CustomerApiService customerApiService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private ApiOnlineProcessStep apiOnlineProcessStep;

	@Autowired
	private ApiOfflineProcessStep apiOfflineProcessStep;

	@Autowired
	private ApiOnlineJoinProcessStep apiOnlineJoinProcessStep;

	@Autowired
	private ApiOnlineTermsProcessStep apiOnlineTermsProcessStep;

	@Autowired
	private ApiOnlineConvTemsProcessStep apiOnlineConvTemsProcessStep;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private SnsAuth snsAuth;

	@Autowired
	private SystemInfo systemInfo;
	
	@Autowired
	private GaTaggingUtils gataggingUtils; // ga taagging util

	@Autowired
	private MembershipService membershipService;

	private ConfigUtil config = ConfigUtil.getInstance();
	
	@Value("${omni.auth.domain}")
	private String oneApAuthUrl;

	// A0100 통합회원 가입, A0100 통합회원 가입 (오프라인)
	@SuppressWarnings({ "unchecked", "unlikely-arg-type" })
	// @GetMapping({ "", "/member" })
	@RequestMapping(value = { "", "/member" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String join(final SSOParam ssoParam, final OfflineParam offlineParam, final HttpServletRequest request, final HttpServletResponse response, final Model model, final HttpSession session, final RedirectAttributes redirectAttr
			) {
				 
		// 오프라인으로 진입하는 경우 SSO PARAM에 채널코드가 없어 오류 발생
		if (StringUtils.hasText(offlineParam.getChCd())) {
			ssoParam.setChannelCd(offlineParam.getChCd());
			WebUtil.setSession(OmniConstants.CH_CD_SESSION, ssoParam.getChannelCd());
		} else { // 온라인으로 진입하는 경우 state 파라미터에서 redirectUri 및 cancelUri 추출 414 오류 대응
			Map<String, Object> state = new HashMap<String, Object>();
			String stateParam = WebUtil.getStringParameter(request, "state", "");
			log.debug("*** state parameter : [{}]", stateParam);
			
			Properties props = new Properties();
			try {
				if(!StringUtils.isEmpty(stateParam)) {
					props.load(new StringReader(stateParam.substring(1, stateParam.length() - 1).replace(", ", "\n")));

					for (Map.Entry<Object, Object> e : props.entrySet()) {
						state.put((String) e.getKey(), (String) e.getValue());
					}
				}
			} catch (IOException e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.OMNI_JOIN_COMMON, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.AUTH_INVALID_PARAMETER);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("Exception = {}", e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			}
			
			if(state.get("redirectUri") != null && StringUtils.isEmpty(ssoParam.getRedirectUri())) {
				ssoParam.setRedirectUri((String) state.get("redirectUri"));
			}
			if(state.get("cancelUri") != null && StringUtils.isEmpty(ssoParam.getCancelUri())) {
				ssoParam.setCancelUri((String) state.get("cancelUri"));
			}
		}
		
		// redirectUri URL 디코딩 &amp; -> &
		if(!StringUtils.isEmpty(ssoParam.getRedirectUri())) {
			ssoParam.setRedirectUri(ssoParam.getRedirectUri().replaceAll("&amp;", "&"));
		}

		WebUtil.setSession(OmniConstants.INCS_NO_SESSION, null);

		try {
			if (StringUtils.hasText(offlineParam.getReturnUrl())) {
				String returnUrl = URLDecoder.decode(offlineParam.getReturnUrl(), StandardCharsets.UTF_8.name());
				returnUrl = HtmlUtils.htmlUnescape(returnUrl);
				offlineParam.setReturnUrl(OmniUtil.getConvertUrl(returnUrl));
			} else { // returnUrl 이 null 이면 옴니 omni-join-start 페이지로 설정
				String returnUrl = oneApAuthUrl.concat("/omni-join-start");
				offlineParam.setReturnUrl(returnUrl);
			}
			if (StringUtils.hasText(offlineParam.getCancelUrl())) {
				String cancelUrl = URLDecoder.decode(offlineParam.getCancelUrl(), StandardCharsets.UTF_8.name());
				cancelUrl = HtmlUtils.htmlUnescape(cancelUrl);
				offlineParam.setCancelUrl(OmniUtil.getConvertUrl(cancelUrl));
			} else { // cancelUrl 이 null 이면 옴니 omni-join-start 페이지로 설정
				String cancelUrl = oneApAuthUrl.concat("/omni-join-start");
				offlineParam.setCancelUrl(cancelUrl);
			}
		} catch (UnsupportedEncodingException e) {
			// NO PMD
		}

		log.debug("▶▶▶▶▶▶ [join page] sso param  : {}", StringUtil.printJson(ssoParam));
		log.debug("▶▶▶▶▶▶ [join page] offline param : {}", StringUtil.printJson(offlineParam));

		if (StringUtils.hasText(ssoParam.getAuthFailure()) && StringUtil.isTrue(ssoParam.getAuthFailure())) {
			if (StringUtils.hasText(ssoParam.getAuthFailureMsg())) {
				final String msg = this.messageSource.getMessage(ssoParam.getAuthFailureMsg(), null, LocaleUtil.getLocale());
				model.addAttribute("authFailure", ssoParam.getAuthFailure());
				model.addAttribute("authFailureMsg", msg);
			}
		}

		if (StringUtils.isEmpty(ssoParam.getChannelCd())) {

			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.OMNI_JOIN_COMMON, null, null, null,
					LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.AUTH_EMPTY_CHANNEL_CODE);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
		
			throw new OmniException("경로코드가 존재하지 않습니다.");

		} else {

			List<Channel> channels = this.commonService.getChannels();
			List<String> channelCds = new ArrayList<>();
			for (Channel channel : channels) {
				channelCds.add(channel.getChCd());
			}

			if (!channelCds.contains(ssoParam.getChannelCd())) {
				
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.OMNI_JOIN_COMMON, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.AUTH_INVALID_PARAMETER);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
                          
             
             throw new OmniException("경로코드가 올바르지 않습니다. 정확한 경로인지 확인하세요.[" + ssoParam.getChannelCd().replaceAll("(?i)script|object|applet|embed|form|alert|href|cookie|input|src|fromcharcode|encodeuri|encodeuricomponent|expression|iframe|window|location|style|eval","").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>","").replaceAll("\r|\n|&nbsp;","") + "]");

			}
			
			log.debug("JoinPrtnId Required : {}, JoinEmpId Required : {}", this.config.isJoinPrtnCodeRequired(ssoParam.getChannelCd()), this.config.isJoinEmpCodeRequired(ssoParam.getChannelCd()));
			
			if(this.config.isJoinPrtnCodeRequired(ssoParam.getChannelCd())) { // joinPrtnId 필수 체크인 경로시스템인 경우 (ex: eCris)
				if(StringUtils.isEmpty(offlineParam.getJoinPrtnId())) {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.OMNI_JOIN_COMMON, null, null, null,
							LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoErr(OmniStdLogConstants.AUTH_EMPTY_JOIN_PRTN_ID);
					LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
					throw new OmniException("매장ID가 존재하지 않습니다.");
				}
			}
			
			if(this.config.isJoinEmpCodeRequired(ssoParam.getChannelCd())) { // joinEmpId 필수 체크인 경로시스템인 경우 (ex: eCris)
				if(StringUtils.isEmpty(offlineParam.getJoinEmpId())) {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.OMNI_JOIN_COMMON, null, null, null,
							LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoErr(OmniStdLogConstants.AUTH_EMPTY_JOIN_EMP_ID);
					LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
					throw new OmniException("엔젤ID가 존재하지 않습니다.");
				}
			}

			WebUtil.setSession(OmniConstants.CH_CD_SESSION, ssoParam.getChannelCd());
		}

		model.addAttribute("rv", this.config.resourceVersion());

		final Channel channel = this.commonService.getChannel(ssoParam.getChannelCd());
		log.debug("▶▶▶▶▶▶ [join page] channel : {}", StringUtil.printJson(channel));

		boolean isOffline = OmniUtil.isOffline(channel); // 오프라인인 경우 로그인하지 않으므로 체크 불필요
		
		boolean isMembership = OmniUtil.isMembership(ssoParam.getIsMembership());
		model.addAttribute("isMembership", isMembership);

		// 세션데이터키가 없으면 로그인 할 수 없으므로 이 값에 대해서 반드시 validation 체크해야함.
		// 중요) 이 값은 WSO2에서 받아오지 않은 값이면 SSO 처리가 되지 않음.
		if (!isOffline && StringUtils.isEmpty(ssoParam.getSessionDataKey()) && !isMembership) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.OMNI_JOIN_COMMON, null, null, null,
					LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.AUTH_EMPTY_SESSION_DATA_KEY);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			throw new OmniException("인증을 위한 정보가 존재하지 않습니다.");

		}

		Map<String, String> idpAuthenticatorMapping = null;
		if (request.getAttribute(OmniConstants.IDP_AUTHENTICATOR_MAP) != null) {
			idpAuthenticatorMapping = (Map<String, String>) request.getAttribute(OmniConstants.IDP_AUTHENTICATOR_MAP);
		}
		// SNS Authenticator를 못받아올 경우 config에서 설정
		if (idpAuthenticatorMapping == null || idpAuthenticatorMapping.isEmpty()) {
			idpAuthenticatorMapping = this.snsTypes;
		}

		final String queryString = request.getQueryString();
		if (StringUtils.hasText(queryString)) {
			ssoParam.setQueryString(queryString);
		}

		WebUtil.setSession(OmniConstants.SESSION_DATA_KEY_SESSION, ssoParam.getSessionDataKey());
		WebUtil.setSession(OmniConstants.SSOPARAM, ssoParam);
		WebUtil.setSession(OmniConstants.OFFLINEPARAM, offlineParam); // 오프라인 파라미터
		
		// 오프라인 진입 시 세션 타입아웃 방지를 위해 쿠키에 파라미터 저장 - 2021-04-14 hjw0228
		/*
		 * if(isOffline) { for(Field field : offlineParam.getClass().getDeclaredFields()) { field.setAccessible(true);
		 * 
		 * if(java.lang.String.class.equals(field.getType())) { try { // Field Value를 참조한다. Object value = field.get(offlineParam); if(value !=
		 * null) { WebUtil.setCookies(response, OmniConstants.ONE_AP_OFFLINE_PARAM +"-"+field.getName().toString(),
		 * SecurityUtil.setXyzValue(value.toString())); } else { WebUtil.setCookies(response, OmniConstants.ONE_AP_OFFLINE_PARAM
		 * +"-"+field.getName().toString(), null); }
		 * 
		 * } catch (IllegalAccessException e) { log.info("Reflection Error. {}", e); } } } }
		 */
		
		final SSOParam responsSSoParam = SecurityUtil.clearXssSsoParam(ssoParam);
		model.addAttribute("wso2", responsSSoParam); // localStorage 에 저장위해 필요
		model.addAttribute("offline", isOffline); // 오프라인인 경우 카카오싱크 하지 않음.
		if (OmniUtil.isOffline(channel)) {
			String profile = this.systemInfo.getActiveProfiles()[0];
			profile = StringUtils.isEmpty(profile) ? "dev" : profile;
			profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
			model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
			model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
			model.addAttribute("chnCd", offlineParam.getChnCd());
			model.addAttribute("storeCd", offlineParam.getStoreCd());
			model.addAttribute("storenm", offlineParam.getStorenm());
			model.addAttribute("user_id", offlineParam.getUser_id());
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
			model.addAttribute("isKakaoSyncEnable", config.isOfflineKakaoSyncEnable(channel.getChCd(), profile));
		} else {
			// - 512 보다 크거나 같으면 SNS 인증 화면 Disable
		    // - 512 보다 작으면 SNS 인증 화면 Enable
			String strVt = StringUtils.isEmpty(ssoParam.getVt()) ? "500" : ssoParam.getVt();
			int vt = StringUtil.isNumeric(strVt) ? Integer.parseInt(strVt) : 500;
			model.addAttribute("vtdisable", vt >= 512 ? true : false);
			
			model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		}
				
		// 카카오 임베디드로 파라미터를 달고 가는 경우 2021-08-04 hjw0228
		// oauth2AuthorizeUser 를 거치면서 kakaoEmbedded=true를 붙여서 전송
		if (StringUtils.hasText(ssoParam.getKakaoEmbedded()) && StringUtil.isTrue(ssoParam.getKakaoEmbedded())) {
			return "redirect:/sns/login_start?snsType=KA&kakaoEmbedded=true";
		}

		// 인증방식 초기 데이터 로딩(KMICS PHONE, NICE IPIN)
		final CertData certData = this.certService.certInit(channel.getChCd());
		model.addAttribute("certdata", certData);

		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		model.addAttribute("manualcert", this.config.avaiableManualCert(profile));

		model.addAttribute("type", "mbrs");
		model.addAttribute("mobile", WebUtil.isMobile());
		model.addAttribute(OmniConstants.CH_CD, SecurityUtil.clearXSSNormal(ssoParam.getChannelCd()));
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		WebUtil.setSession("certiType", "mbrs");
		WebUtil.setCookies(response, OmniConstants.ONE_AP_CERTIFICATION_TYPE, "mbrs");
		
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
		
		String headerType = config.isHeaderType(channel.getChCd(), profile); //에딧샵 에디터 가입 분기처리
		model.addAttribute("headertype", headerType);
		String joinAditor=ssoParam.getJoinAditor();
		WebUtil.setSession("joinAditor", joinAditor);
		model.addAttribute("joinAditor", joinAditor);
		WebUtil.setSession("cancelUri", ssoParam.getCancelUri());
		WebUtil.setSession("redirectUri", ssoParam.getRedirectUri());

		return "join/member";
	}

	/**
	 * 
	 * <pre>
	 * 회원가입 - 온라인
	 * X X X 탈퇴 후 30 일 이내 A0104
	 * X X X 신규고객 A0101
	 * X X O 자체고객 A0101
	 * O X X 타오프라인 경로 자체 ) 가입 고객  A0103 -> A0207 채널약관 동의 목록 노출 화면
	 * O X O 해당 경로 자체 만 가입된 고객 A0103 -> A0202 경로 자체 고객 ID 사용가능
	 * O X O 해당 경로 자체 만 가입된 고객 A0103 -> A0203 경로 자체 고객 ID 타인 사용
	 * O O X 경로 자체 ) 첫 방문 뷰티포인트 고객 A0103 -> A0105
	 * O O O 이미 가입된 고객 A0103
	 * </pre>
	 * 
	 * <pre>
	 * GA Tagging 기준 정리
	 * 신규:   X X X , X X O ( X X X 탈퇴 고객은 신규에서 제외 )
	 * 경로:   O X X , O O X
	 * 기가입 : O X O , O O O 
	 * </pre>
	 */
	// @GetMapping("/step")
	@RequestMapping(value = "/step", method = { RequestMethod.GET, RequestMethod.POST })
	public String joinStep(final HttpServletRequest request, final Model model, //
			@RequestParam(required = false) final String type, //
			@RequestParam(required = false) final String itg, //
			final HttpServletResponse servletResponse, //
			final RedirectAttributes redirectAttributes, //
			final Locale locale) {

		// servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		// servletResponse.setHeader("Pragma", "no-cache");
		// servletResponse.setDateHeader("Expires", 0);

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		// 채널 코드가 Null 일 경우 세션이 아닌 쿠키에서 값 추출 (오프라인 파라미터인 경우에 한함)
		/*
		 * if(StringUtils.isEmpty(chCd)) { Cookie cookie = WebUtil.getCookies(request, OmniConstants.ONE_AP_OFFLINE_PARAM+"-"+OmniConstants.CH_CD);
		 * if(cookie != null) { log.info("▶▶▶▶▶▶ [Channel Code from Cookies] : {}", chCd); WebUtil.setSession(OmniConstants.CH_CD_SESSION,
		 * SecurityUtil.getXValue(cookie.toString(), false)); OfflineParam offlineParam = new OfflineParam();
		 * 
		 * for(Field field : offlineParam.getClass().getDeclaredFields()) { field.setAccessible(true);
		 * 
		 * if(java.lang.String.class.equals(field.getType())) { try { Cookie cookieValue = WebUtil.getCookies(request,
		 * OmniConstants.ONE_AP_OFFLINE_PARAM+"-"+field.getName().toString());
		 * 
		 * if(cookieValue != null) { field.set(offlineParam, SecurityUtil.getXValue(cookieValue.toString(), false)); }
		 * 
		 * } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) { log.info("Reflection Error. {}", e); } } }
		 * log.debug("▶▶▶▶▶▶ [join page] offline param from cookies : {}", StringUtil.printJson(offlineParam));
		 * WebUtil.setSession(OmniConstants.OFFLINEPARAM, offlineParam); } }
		 */
		
		final Channel channel = this.commonService.getChannel(chCd);
		
		if(Objects.nonNull(channel)) {
			model.addAttribute("offline", OmniUtil.isOffline(channel));
			model.addAttribute("channelName", channel.getChCdNm());
			model.addAttribute("chcd", channel.getChCd());
			model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
			model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		}else {
			log.info("session expired!!!");
			model.addAttribute("oauthErrorCode", "9999");
			model.addAttribute("oauthErrorMsg", "session data key must have value.");
			model.addAttribute("message", "현재 서비스가 원할하지 않습니다. 잠시 후 다시 시도해주시기 바랍니다.");

			return "wso2/oauth2_error";
		}
		
		
		// 본인인증 에서 전달 받은 값
		CertResult certResult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
		if (certResult == null) {
			return "redirect:/join?" + WebUtil.getSsoParams();
		}
		
		final String isMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_MEMBERSHIP);
		boolean isMembership = OmniUtil.isMembership(isMembershipSession);
		
		model.addAttribute("category", certResult.getCategory());

		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;
				model.addAttribute("home", OmniUtil.getOfflineRedirectUrl(channel));
				model.addAttribute("homeurl", OmniUtil.getOfflineRedirectUrl(channel));
				model.addAttribute(OmniConstants.RD_URL, OmniUtil.getOfflineRedirectUrl(channel));
				model.addAttribute("chnCd", offlineParam.getChnCd());
				model.addAttribute("storeCd", offlineParam.getStoreCd());
				model.addAttribute("storenm", offlineParam.getStorenm());
				model.addAttribute("user_id", offlineParam.getUser_id());

			}
		} else {
			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		}

		model.addAttribute("unm", SecurityUtil.setXyzValue(certResult.getName()));
		model.addAttribute("rv", this.config.resourceVersion());

		final JoinResponse response = this.joinService.checkJoinCondition(certResult, OmniUtil.isOffline(channel));

				
		if ("ipin".equals(type)) {
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, null);
		}

		if (response != null) {
			
			
			
			JoinStepVo joinStepVo = response.getJoinStep();
			model.addAttribute("joinStepType", joinStepVo.getJoinType().getType());
			model.addAttribute("joinType", response.getType()); // 가입 타입 설정
			WebUtil.setSession("joinStepType", joinStepVo.getJoinType().getType());
			
			//GA 태깅 변수 setting value 2022/03/15 pks
			final Map<String,String> gaCookieMap = gataggingUtils.getGaCookieMap(request);	//ga tagging get cookie value
			
			String eventLabel = OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE;
			String totalJoinCnt = "1";
			String channelJoinCnt ="0";
			String joinCase = OmniGaTaggingConstants.GA_SIGNUP_CASE_INTERGRATED;
			
			if ("ipin".equals(type)) {
				eventLabel= OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN;
			}else if ("kmcis".equals(type)) {
				eventLabel=OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE;
			}
			
			WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_STEP_TYPE, joinStepVo.getJoinType());
		    WebUtil.setSession(OmniGaTaggingConstants.EL, eventLabel);
			WebUtil.setSession(OmniGaTaggingConstants.CD21, channel.getChCdNm());
			WebUtil.setSession(OmniGaTaggingConstants.CD22, channel.getChCd());
			  
			//GA setting end
			
			
			String incsNo = joinStepVo.getIncsNo();
			if (StringUtils.hasText(incsNo)) {
				if ("0".equals(incsNo)) {
					incsNo = "";
				} else {
					model.addAttribute(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));
					WebUtil.setSession(OmniConstants.INCS_NO_SESSION, Integer.parseInt(incsNo));
				}
			} else {
				model.addAttribute(OmniConstants.XINCS_NO_SESSION, null);
				WebUtil.setSession(OmniConstants.INCS_NO_SESSION, null);
				response.setType(JoinDivisionType.CHANNEL_JOIN.getType());
			}

			if (StringUtils.hasText(incsNo)) {
				model.addAttribute("incsno", incsNo);
				model.addAttribute(OmniConstants.INCS_NO_SESSION, Integer.parseInt(incsNo));
				WebUtil.setSession(OmniConstants.INCS_NO_SESSION, Integer.parseInt(incsNo));
			}

			log.debug("▶▶▶▶▶▶ [join step] join response : {}({}) {}", response.getJoinStep().getJoinType().getDesc(), response.getJoinStep().getJoinType().getType(), StringUtil.printJson(response));
						
			
			// 통합고객이 있는 경우
			if (response.getType() == JoinDivisionType.EXIST.getType()) { // 가입사실안내 A0103
			
				// JOINED_OMNI_CH 25 : A0103(온라인-로그인하기, 오프라인-확인) --> 가입사실(중복체크) - 옴니조회
				// JOINED_OMNI 30 : A0103 --> A0105
				// JOINED_OFF 35 : A0103 --> A0207 (경로 가입)
				// COVERSION 20 : 전환 A0202, A0203, A0204 --> A0205
				model.addAttribute("joinStepType", joinStepVo.getJoinType().getType());

				model.addAttribute("locale", LocaleUtil.getLocale());

				List<UmOmniUser> omniUsers = joinStepVo.getOmniUsers() == null ? Collections.emptyList() : joinStepVo.getOmniUsers();
				List<UmChUser> chUsers = joinStepVo.getChUsers() == null ? Collections.emptyList() : joinStepVo.getChUsers();
				final int omniUserSize = omniUsers.size();
				final int chUserSize = chUsers.size();
				Customer apiCustomer = response.getCustomer();

				if (StringUtils.hasText(apiCustomer.getCustNm())) {
					CertResult certResultData = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
					if (certResultData != null) {
						certResultData.setName(apiCustomer.getCustNm());
						WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certResultData);
						model.addAttribute("unm", SecurityUtil.setXyzValue(certResultData.getName()));
					}
				}
				
				// TODO GA 가입 타입 Setting 
				if (joinStepVo.getJoinType() == JoinType.JOINED_OMNI_CH) { // O O O 뷰포 있음, 채널 있음
					
					joinCase = OmniGaTaggingConstants.GA_SIGNUP_CASE_MEMBER;
					model.addAttribute(OmniGaTaggingConstants.GA_SIGNUP_CASE, OmniGaTaggingConstants.GA_SIGNUP_CASE_MEMBER_CODE);	
					totalJoinCnt = "0";
					channelJoinCnt = "0";
					WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL, totalJoinCnt); //통합회원
					WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL, channelJoinCnt); //경로회원
					log.debug("▶▶▶▶▶▶  GA Tagging JOIN STEP TYPE : JoinType.JOINED_OMNI_CH ");
					
				} else if (joinStepVo.getJoinType() == JoinType.JOINED_OMNI) { // O O X 뷰포 있음, 채널 없음 -> 경로 자체 첫 방문 뷰티포인트 고객 온라인 A0103 -> A0105
					
					totalJoinCnt = "0";
					channelJoinCnt = "1";
					model.addAttribute(OmniGaTaggingConstants.GA_SIGNUP_CASE, OmniGaTaggingConstants.GA_SIGNUP_CASE_CHANNEL_CODE);
					WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL, totalJoinCnt); //통합회원
					WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL, channelJoinCnt); //경로회원
					log.debug("▶▶▶▶▶▶  GA Tagging JOIN STEP TYPE : JoinType.JOINED_OMNI ");
					
				} else if (joinStepVo.getJoinType() == JoinType.JOINED_OFF) { // O X X 뷰포 없음, 채널 없음 -> 타 오프라인 경로(자체) 가입고객 103 -> 207

					totalJoinCnt = "0";
					channelJoinCnt = "1";
					model.addAttribute(OmniGaTaggingConstants.GA_SIGNUP_CASE, OmniGaTaggingConstants.GA_SIGNUP_CASE_CHANNEL_CODE);
					WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL, totalJoinCnt); //통합회원
					WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL, channelJoinCnt); //경로회원
					log.debug("▶▶▶▶▶▶  GA Tagging JOIN STEP TYPE : JoinType.JOINED_OFF ");
					
				} else if (joinStepVo.getJoinType() == JoinType.JOINED_STEP_OFF) { // OFFLINE O X X 772라인
					
					totalJoinCnt = "0";
					channelJoinCnt = "1";
					model.addAttribute(OmniGaTaggingConstants.GA_SIGNUP_CASE, OmniGaTaggingConstants.GA_SIGNUP_CASE_CHANNEL_CODE);
					WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL, totalJoinCnt); //통합회원
					WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL, channelJoinCnt); //경로회원
					log.debug("▶▶▶▶▶▶  GA Tagging JOIN STEP TYPE : JoinType.JOINED_STEP_OFF ");
				
				}else if (joinStepVo.getJoinType() == JoinType.JOIN_OFF) { // O O X A0216
					totalJoinCnt = "0";
					channelJoinCnt = "1";
					joinCase = OmniGaTaggingConstants.GA_SIGNUP_CASE_CHANNEL_CODE;
					model.addAttribute(OmniGaTaggingConstants.GA_SIGNUP_CASE, OmniGaTaggingConstants.GA_SIGNUP_CASE_CHANNEL_CODE);
					
					WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL, totalJoinCnt); //통합회원
					WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL, channelJoinCnt); //경로회원
					log.debug("▶▶▶▶▶▶  GA Tagging JOIN STEP TYPE : JoinType.JOIN_OFF ");
	
				} else if (joinStepVo.getJoinType() == JoinType.COVERSION) { // O X O 옴니만 없는 경우
					if (omniUserSize == 0 && chUserSize > 0) {
						totalJoinCnt = "0";
						channelJoinCnt = "0";
						joinCase = OmniGaTaggingConstants.GA_SIGNUP_CASE_ONLINE_MEMBER;
						model.addAttribute(OmniGaTaggingConstants.GA_SIGNUP_CASE, OmniGaTaggingConstants.GA_SIGNUP_CASE_ONLINE_CODE);
						WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL, totalJoinCnt); //통합회원
						WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL, channelJoinCnt); //경로회원
						log.debug("▶▶▶▶▶▶  GA Tagging JOIN STEP TYPE : JoinType.COVERSION ");
					}					  
			     }else if (joinStepVo.getJoinType() == JoinType.JOINED_AGREE_CH_OFF) { // 오프라인 경로약관동의 A0105  O O O 4490라인
						
					if (omniUsers != null && omniUsers.size() > 0) {
						totalJoinCnt = "0";
						channelJoinCnt = "0";
						joinCase = OmniGaTaggingConstants.GA_SIGNUP_CASE_MEMBER;
						model.addAttribute(OmniGaTaggingConstants.GA_SIGNUP_CASE, OmniGaTaggingConstants.GA_SIGNUP_CASE_MEMBER_CODE);
						WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL, totalJoinCnt); //통합회원
						WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL, channelJoinCnt); //경로회원
						log.debug("▶▶▶▶▶▶  GA Tagging JOIN STEP TYPE : JoinType.JOINED_AGREE_CH_OFF ");
					}
						
			     }
				/*
				 * ####################### ga tagging 회원가입 시작 처리 pks 2022.03.10 위치 변경  ###################
				 * 
				 * 처리 유형 : O O O 기가입 , O O X 기가입 , O X X 경로가입 , O X O 기가입
				 */
				
				log.debug("▶▶▶▶▶▶  GA Tagging JOIN STEP TYPE : {}({})",response.getJoinStep().getJoinType().getDesc(), response.getJoinStep().getJoinType().getType());
				log.debug("▶▶▶▶▶▶  GA Tagging JOIN DATA : 가입타입:{}, 회원가입:{} ,경로가입:{} ",joinCase,totalJoinCnt,channelJoinCnt);
				
				try {
											
					 log.debug("▶▶▶▶▶▶  GA Tagging JOIN START(/step) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
								,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),eventLabel,channel.getChCdNm(),channel.getChCd());
					 
				 	// 회원가입 중복 입력 방지를 위해 세션에 eventAction 및 EL 값 저장
				 	WebUtil.setSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_START_CHECK_KEY, "true");
				 	WebUtil.setSession(OmniGaTaggingConstants.GA_JOIN_EVENT_LABEL, eventLabel);
				 
					GaTagData gaTagDto = GaTagData.builder()
							.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
                            .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
                            .el(eventLabel)
                            .loginType(eventLabel)
                            .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
                            .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
                            .chCd(channel.getChCd())
                            .chCdNm(channel.getChCdNm())
                            .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_START)
                            .joinType(joinCase)
                            .totalJoinCnt(totalJoinCnt)
                            .channelJoinCnt(channelJoinCnt)
                            .sendFlag("Y")
                            .incsNo(incsNo)
                            .sessionId(request.getSession().getId())
                            .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]").build();
//						  
					   gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);		
				
				}catch(Exception ex) {
					log.error(ex.getMessage());
				}
  
				if ((joinStepVo.getJoinType() == JoinType.JOINED_OMNI_CH) // 25 : O O O A0103(온라인-로그인하기, 오프라인-확인) --> 가입사실(중복체크) - 옴니조회
						|| (joinStepVo.getJoinType() == JoinType.JOINED_OMNI) // 30 : O O x A0103 --> A0105
						|| (joinStepVo.getJoinType() == JoinType.JOINED_OFF) // 35 : O X X : A0103 --> A0207(id_regist) 타오프라인 경로 자체
				) {
					List<Customer> customers = new ArrayList<>();
					if (omniUsers != null && omniUsers.size() > 0) {
						log.debug("▶▶▶▶▶▶ [join step] customers :  {}", StringUtil.printJson(omniUsers));

						for (UmOmniUser user : omniUsers) {
							Customer customer = new Customer();
							customer.setChcsNo(user.getUmUserName());
							customer.setCustNm(apiCustomer.getCustNm());
							customer.setCellTexn(apiCustomer.getCellTexn());
							customer.setCellTidn(apiCustomer.getCellTidn());
							customer.setCellTlsn(apiCustomer.getCellTlsn());
							customer.setMbrJoinDt(user.getCreatedDate());
							customers.add(customer);
						}
						model.addAttribute("users", customers);
						model.addAttribute("multiflag", customers.size() == 1 ? false : customers.size() > 1 ? true : false);
					} else { // 타오프라인
						customers.add(apiCustomer);
						model.addAttribute("users", customers);
						model.addAttribute("multiflag", customers.size() == 1 ? false : customers.size() > 1 ? true : false);
					}
		
					if (joinStepVo.getJoinType() == JoinType.JOINED_OMNI_CH) { // O O O 뷰포 있음, 채널 있음
						
						// 멤버십 연동일 경우 연동 페이지로
						if(isMembership) {
							MembershipUserInfo membershipUserInfo = (MembershipUserInfo) WebUtil.getSession(OmniConstants.MEMBERSHIP_USERINFO);
							
							// 뷰티 멤버십 연동을 통한 회원 가입일 경우 CI 값 검증
							String ci = certResult.getCiNo();
							if(membershipUserInfo != null && !StringUtils.isEmpty(ci)) {
								if(!membershipUserInfo.getCiNo().equals(ci)) {
									log.debug("▶▶▶▶▶▶ [membership join] CI불일치  : ");
									log.debug("▶▶▶▶▶▶ [membership CiNo]  : {}", membershipUserInfo.getCiNo());
									log.debug("▶▶▶▶▶▶ [certResult CiNo]  : {}", ci);
									
									return "info/membership_ci_mismatch";
								}
							}
							
							String name = OmniUtil.maskUserName(apiCustomer.getCustNm(), locale);
							String id = OmniUtil.maskUserId(omniUsers.get(0).getUmUserName());
							
							model.addAttribute("chCd", chCd);
							model.addAttribute("id", id);
							model.addAttribute("name", name);
							model.addAttribute("xname", SecurityUtil.setXyzValue(apiCustomer.getCustNm()));
							model.addAttribute("xid", SecurityUtil.setXyzValue(omniUsers.get(0).getUmUserName()));
							model.addAttribute("xincsNo", SecurityUtil.setXyzValue(apiCustomer.getIncsNo()));
							model.addAttribute("mbrId", membershipUserInfo.getMbrId());
							
							return "membership/membership_start"; // 멤버십 연동 페이지 이동
						}						
						String profile = this.systemInfo.getActiveProfiles()[0];
						profile = StringUtils.isEmpty(profile) ? "dev" : profile;
						profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
						String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
						model.addAttribute("joinAditor", joinAditor);
						String headerType = config.isHeaderType(channel.getChCd(), profile);
						model.addAttribute("headertype", headerType);

						return "mgmt/joined"; // 로그인하기
						
					} else if (joinStepVo.getJoinType() == JoinType.JOINED_OMNI) { // O O X 뷰포 있음, 채널 없음 -> 경로 자체 첫 방문 뷰티포인트 고객 온라인 A0103 -> A0105

					} else if (joinStepVo.getJoinType() == JoinType.JOINED_OFF) { // O X X 뷰포 없음, 채널 없음 -> 타 오프라인 경로(자체) 가입고객 103 -> 207
							
						return "redirect:/join/go-idregist";
					} else if (joinStepVo.getJoinType() == JoinType.JOINED_STEP_OFF) { // OFFLINE O X X
                       
					}
					
					// 멤버십 연동일 경우 연동 페이지로
					if(isMembership) {
						MembershipUserInfo membershipUserInfo = (MembershipUserInfo) WebUtil.getSession(OmniConstants.MEMBERSHIP_USERINFO);
						
						// 뷰티 멤버십 연동을 통한 회원 가입일 경우 CI 값 검증
						String ci = certResult.getCiNo();
						if(membershipUserInfo != null && !StringUtils.isEmpty(ci)) {
							if(!membershipUserInfo.getCiNo().equals(ci)) {
								log.debug("▶▶▶▶▶▶ [membership join] CI불일치  : ");
								log.debug("▶▶▶▶▶▶ [membership CiNo]  : {}", membershipUserInfo.getCiNo());
								log.debug("▶▶▶▶▶▶ [certResult CiNo]  : {}", ci);
								
								return "info/membership_ci_mismatch";
							}
						}
						
						String name = OmniUtil.maskUserName(apiCustomer.getCustNm(), locale);
						String id = OmniUtil.maskUserId(omniUsers.get(0).getUmUserName());
						
						model.addAttribute("chCd", chCd);
						model.addAttribute("id", id);
						model.addAttribute("name", name);
						model.addAttribute("xname", SecurityUtil.setXyzValue(apiCustomer.getCustNm()));
						model.addAttribute("xid", SecurityUtil.setXyzValue(omniUsers.get(0).getUmUserName()));
						model.addAttribute("xincsNo", SecurityUtil.setXyzValue(apiCustomer.getIncsNo()));
						model.addAttribute("mbrId", membershipUserInfo.getMbrId());
						
						return "membership/membership_start"; // 멤버십 연동 페이지 이동
					}
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
					model.addAttribute("joinAditor", joinAditor);
					String headerType = config.isHeaderType(channel.getChCd(), profile);
					model.addAttribute("headertype", headerType);
					
					return "mgmt/joined";

				} else if (joinStepVo.getJoinType() == JoinType.JOINED_STEP_OFF) { // OFFLINE O X X (
					model.addAttribute("certType", type); // 인증타입(ipin, kmcis)
					model.addAttribute("itgType", itg); // 인증타입(ipin, kmcis)
					String name = OmniUtil.maskUserName(apiCustomer.getCustNm(), locale);
					model.addAttribute("name", name);
					final String mobileNo = StringUtil.mergeMobile(apiCustomer);
					final String mobile = OmniUtil.maskMobile(mobileNo, locale);
					model.addAttribute("mobile", mobile);
					model.addAttribute("joindate", DateUtil.getBirthDate(apiCustomer.getMbrJoinDt()));

					model.addAttribute("name", name);
					model.addAttribute("xname", SecurityUtil.setXyzValue(name));

					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
					if (obj != null) {
						OfflineParam offlineParam = (OfflineParam) obj;
						model.addAttribute(OmniConstants.RD_URL, OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
						model.addAttribute("chnCd", offlineParam.getChnCd());
						model.addAttribute("storeCd", offlineParam.getStoreCd());
						model.addAttribute("storenm", offlineParam.getStorenm());
						model.addAttribute("user_id", offlineParam.getUser_id());

					}
					
					// 개인정보 제3자 제공 동의는 아모레퍼시픽 자회사는 처리하지 않음
					boolean isThirdPartyConsent = this.config.isThirdPartyConsent(channel.getChCd(), profile);
					model.addAttribute("isThirdPartyConsent", isThirdPartyConsent);
					
					//20230323 채널 문자 수신 동의
					UmOmniUser omniUser = new UmOmniUser();
					WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
					final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
					omniUser.setChCd(onlineChCd);
					if (!this.termsService.hasTermsAgree(omniUser)) { // 진입 채널에 대한 약관 출력 정보 조회
						TermsVo termsVo = new TermsVo();
						termsVo.setChCd(onlineChCd);
						List<TermsVo> termsList = this.termsService.getTermsChoice(termsVo);
						for(TermsVo vo : termsList) {
							if(vo.getMkSn()==-10) {
								model.addAttribute("terms_marketing", vo);
							}
							if(vo.getMkSn()==-20) {
								model.addAttribute("terms_marketing_online", vo);
							}
						}
					}
					// return "login/id_regist_01_off";

					return "join/join_step_01_off";
				} else if (joinStepVo.getJoinType() == JoinType.COVERSION) { // 전환 A0202, A0203, A0204 --> A0205
					if (omniUserSize == 0 && chUserSize > 0) { // 경로에만 O X O
						int incsNoInt = Integer.parseInt(incsNo);

						// 옴니에 동일 아이디가 있는지 체크
						UmChUser sameIdChUser = new UmChUser();
						sameIdChUser.setChCd(chCd);
						sameIdChUser.setIncsNo(incsNoInt);
						final boolean same = this.mgmtService.hasSameLoginId(sameIdChUser);

						log.debug("▶▶▶▶▶▶ [join step] use same login id already ? {}", same);

						if (same) { // 동일 아이디 타인 사용 : A0203
							model.addAttribute("trnsType", OmniConstants.TRNS_TYPE_TRANSFORM);
							model.addAttribute("name", certResult.getName());
							if (chUsers != null && !chUsers.isEmpty()) {
								UmChUser chUser = chUsers.get(0);
								model.addAttribute("loginid", chUser.getChcsWebId());
								if (chUser.getIncsNo() > 0) {
									model.addAttribute("xincsno", SecurityUtil.setXyzValue(Integer.toString(chUser.getIncsNo())));
								}
								if (chUser != null) {
									List<Map<String, String>> loginData = new ArrayList<>();
									List<Customer> customers = new ArrayList<>();
									Map<String, String> loginMap = this.customerApiService.getChannelUser(chUser);
									for (UmChUser user : chUsers) {
										Customer customer = new Customer();
										customer.setChcsNo(user.getChcsWebId() == null ? apiCustomer.getChcsNo() : user.getChcsWebId());
										customer.setCustNm(loginMap.get("name") == null ? certResult.getName() : loginMap.get("name").toString());
										customer.setCellTexn(apiCustomer.getCellTexn());
										customer.setCellTidn(apiCustomer.getCellTidn());
										customer.setCellTlsn(apiCustomer.getCellTlsn());
										customer.setMbrJoinDt(apiCustomer.getMbrJoinDt());
										customers.add(customer);
										Map<String, String> chLoginMap = new HashMap<>();
										chLoginMap.put("chcd", chCd);
										chLoginMap.put("id", user.getChcsWebId());
										// chLoginMap.put("name", certResult.getName());
										chLoginMap.put("name", loginMap.get("name") == null ? certResult.getName() : loginMap.get("name").toString());
										loginData.add(chLoginMap);
									}
									model.addAttribute("chusers", loginData);
									model.addAttribute("users", customers);
									model.addAttribute("multiflag", customers.size() == 1 ? false : customers.size() > 1 ? true : false);
								}
							}
							String profile = this.systemInfo.getActiveProfiles()[0];
							profile = StringUtils.isEmpty(profile) ? "dev" : profile;
							profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
							String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
							model.addAttribute("joinAditor", joinAditor);
							String headerType = config.isHeaderType(channel.getChCd(), profile);
							model.addAttribute("headertype", headerType);
							
							return "mgmt/joined";
							// return "convs/conversion_a0203"; // O X O 아이디 타인 사용
						} else { // 동일 아이디 사용 : A0202
							model.addAttribute("trnsType", OmniConstants.TRNS_TYPE_TRANSFORM);
							final String username = OmniUtil.maskUserName(certResult.getName(), locale);
							model.addAttribute("name", username);
							UmChUser chUser = chUsers.get(0);
							model.addAttribute("loginid", chUser.getChcsWebId());
							if (chUser.getIncsNo() > 0) {
								model.addAttribute("xincsno", SecurityUtil.setXyzValue(Integer.toString(chUser.getIncsNo())));
							}
							if (chUser != null) {
								List<Map<String, String>> loginData = new ArrayList<>();
								List<Customer> customers = new ArrayList<>();
								Map<String, String> loginMap = this.customerApiService.getChannelUser(chUser);
								for (UmChUser user : chUsers) {
									Customer customer = new Customer();
									customer.setChcsNo(user.getChcsWebId() == null ? apiCustomer.getChcsNo() : user.getChcsWebId());
									customer.setCustNm(loginMap.get("name") == null ? certResult.getName() : loginMap.get("name").toString());
									customer.setCellTexn(apiCustomer.getCellTexn());
									customer.setCellTidn(apiCustomer.getCellTidn());
									customer.setCellTlsn(apiCustomer.getCellTlsn());
									customer.setMbrJoinDt(apiCustomer.getMbrJoinDt());
									customers.add(customer);
									Map<String, String> chLoginMap = new HashMap<>();
									chLoginMap.put("chcd", chCd);
									chLoginMap.put("id", user.getChcsWebId());
									// chLoginMap.put("name", certResult.getName());
									chLoginMap.put("name", loginMap.get("name") == null ? certResult.getName() : loginMap.get("name").toString());
									loginData.add(chLoginMap);
								}
								model.addAttribute("chusers", loginData);
								model.addAttribute("users", customers);
								model.addAttribute("multiflag", customers.size() == 1 ? false : customers.size() > 1 ? true : false);
							}
							String profile = this.systemInfo.getActiveProfiles()[0];
							profile = StringUtils.isEmpty(profile) ? "dev" : profile;
							profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
							String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
							model.addAttribute("joinAditor", joinAditor);
							String headerType = config.isHeaderType(channel.getChCd(), profile);
							model.addAttribute("headertype", headerType);
							
							return "mgmt/joined";
							// return "convs/conversion_a0202"; // O X O 아이디 사용가능
						}

					} else if (omniUserSize > 0 && chUserSize > 0) { // 중복 : A0204
						model.addAttribute("trnsType", OmniConstants.TRNS_TYPE_INTEGRATE);
						model.addAttribute("omniusers", omniUsers); // 통합 아이디 2 개 이상 체크
						model.addAttribute("chusers", chUsers);
						List<Customer> customers = new ArrayList<>();
						List<Map<String, String>> omniData = new ArrayList<>();

						for (UmOmniUser user : omniUsers) {
							Map<String, String> loginMap = new HashMap<>();
							loginMap.put("chcd", OmniConstants.JOINON_CHCD);
							loginMap.put("id", user.getUmUserName());
							loginMap.put("name", apiCustomer.getCustNm());
							omniData.add(loginMap);

							Customer customer = new Customer();
							customer.setChcsNo(user.getUmUserName());
							customer.setCustNm(apiCustomer.getCustNm());
							customer.setCellTexn(apiCustomer.getCellTexn());
							customer.setCellTidn(apiCustomer.getCellTidn());
							customer.setCellTlsn(apiCustomer.getCellTlsn());
							customer.setMbrJoinDt(apiCustomer.getMbrJoinDt());
							customers.add(customer);

						}
						model.addAttribute("bpLength", omniData.size());
						model.addAttribute("bpusers", omniData);

						List<Map<String, String>> chData = new ArrayList<>();
						for (UmChUser user : chUsers) {
							Map<String, String> loginMap = this.customerApiService.getChannelUser(user);
							chData.add(loginMap);
							Customer customer = new Customer();
							customer.setChcsNo(user.getChcsWebId() == null ? apiCustomer.getChcsNo() : user.getChcsWebId());
							customer.setCustNm(loginMap.get("name") == null ? certResult.getName() : loginMap.get("name").toString());
							customer.setCellTexn(apiCustomer.getCellTexn());
							customer.setCellTidn(apiCustomer.getCellTidn());
							customer.setCellTlsn(apiCustomer.getCellTlsn());
							customer.setMbrJoinDt(apiCustomer.getMbrJoinDt());
							customers.add(customer);
						}
						model.addAttribute("chLength", chData.size());
						model.addAttribute("chusers", chData);
						model.addAttribute("users", customers);
						model.addAttribute("multiflag", customers.size() == 1 ? false : customers.size() > 1 ? true : false);
						
						String profile = this.systemInfo.getActiveProfiles()[0];
						profile = StringUtils.isEmpty(profile) ? "dev" : profile;
						profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
						String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
						model.addAttribute("joinAditor", joinAditor);
						String headerType = config.isHeaderType(channel.getChCd(), profile);
						model.addAttribute("headertype", headerType);
						
						return "mgmt/joined";
						// return "convs/conversion_a0204";
					}
				} else if (joinStepVo.getJoinType() == JoinType.JOIN_OFF) { // 오프라인 통합아이디 등록 A0216 O O X

					CustInfoVo custInfoVo = new CustInfoVo();
					custInfoVo.setIncsNo(response.getJoinStep().getIncsNo());
					final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
					log.debug("▶▶▶▶▶ [join step] customer info : {}", StringUtil.printJson(customer));
					if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
						String name = OmniUtil.maskUserName(customer.getCustNm(), locale);
						model.addAttribute("name", name);
						final String mobileNo = StringUtil.mergeMobile(customer);
						log.debug("▶▶▶▶▶ [login step] mobile no[1] : {}", mobileNo);
						final String mobile = OmniUtil.maskMobile(mobileNo, locale);
						log.debug("▶▶▶▶▶ [login step] mobile no[2] : {}", mobile);
						model.addAttribute("mobile", mobile);
						if (StringUtils.hasText(customer.getMbrJoinDt())) {
							model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
						}

						model.addAttribute("incsno", customer.getIncsNo());
						model.addAttribute("incsNo", customer.getIncsNo());
						model.addAttribute("name", name);
						model.addAttribute("xname", SecurityUtil.setXyzValue(name));

					}
					log.info("chCd : {}", chCd);
					final String onlineChCd = ChannelPairs.getOnlineCd(chCd);

					log.debug("▶▶▶▶▶▶ [join step off] online channel code : {}", onlineChCd);

					// 오프라인인 경우 온라인 경로 약관 호출
					TermsVo termsVo = new TermsVo();
					termsVo.setChCd(onlineChCd);
					if(onlineChCd.equals(OmniConstants.OSULLOC_CHCD)) {
						termsVo.setIncsNo(response.getJoinStep().getIncsNo());
					}
					List<TermsVo> termsList = this.termsService.getTerms(termsVo);
					model.addAttribute("terms", termsList);

					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
					if (obj != null) {
						OfflineParam offlineParam = (OfflineParam) obj;
						model.addAttribute(OmniConstants.RD_URL, OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
						model.addAttribute("chnCd", offlineParam.getChnCd());
						model.addAttribute("storeCd", offlineParam.getStoreCd());
						model.addAttribute("storenm", offlineParam.getStorenm());
						model.addAttribute("user_id", offlineParam.getUser_id());

					}
					TermsVo corpTermsVo = new TermsVo();
					corpTermsVo.setChCd(onlineChCd);
					List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
					model.addAttribute("corptermslist", corpTermsList);
					
					//20230406 채널 문자 수신 동의 
					final String onlineChCd2 = ChannelPairs.getOnlineCd(chCd);
					UmOmniUser omniUser = new UmOmniUser();
					omniUser.setChCd(onlineChCd2);
					if (!this.termsService.hasTermsAgree(omniUser)) { // 진입 채널에 대한 약관 출력 정보 조회
						TermsVo termsVo2 = new TermsVo();
						termsVo2.setChCd(onlineChCd2);
						List<TermsVo> termsList2 = this.termsService.getTermsChoice(termsVo2);
						for(TermsVo vo : termsList2) {
							if(vo.getMkSn()==-20) {
								model.addAttribute("terms_marketing", vo);
							}
						}
					}
					
					return "login/id_regist_off";
				} else if (joinStepVo.getJoinType() == JoinType.JOINED_CH_OFF) { // 오프라인 가입 사일 안내 A0103
					List<Customer> customers = new ArrayList<>();
					if (omniUsers != null && omniUsers.size() > 0) {
						log.debug("▶▶▶▶▶▶ [join step] customers :  {}", StringUtil.printJson(omniUsers));

						for (UmOmniUser user : omniUsers) {
							Customer customer = new Customer();
							customer.setChcsNo(user.getUmUserName());
							customer.setCustNm(apiCustomer.getCustNm());
							customer.setCellTexn(apiCustomer.getCellTexn());
							customer.setCellTidn(apiCustomer.getCellTidn());
							customer.setCellTlsn(apiCustomer.getCellTlsn());
							customer.setMbrJoinDt(user.getCreatedDate());
							customers.add(customer);
						}
						model.addAttribute("users", customers);
						model.addAttribute("multiflag", customers.size() == 1 ? false : customers.size() > 1 ? true : false);
					} else { // 타오프라인
						customers.add(apiCustomer);
						model.addAttribute("users", customers);
						model.addAttribute("multiflag", customers.size() == 1 ? false : customers.size() > 1 ? true : false);
					}

					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					model.addAttribute("loginurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
					if (obj != null) {
						OfflineParam offlineParam = (OfflineParam) obj;
						model.addAttribute(OmniConstants.RD_URL, OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
						model.addAttribute("chnCd", offlineParam.getChnCd());
						model.addAttribute("storeCd", offlineParam.getStoreCd());
						model.addAttribute("storenm", offlineParam.getStorenm());
						model.addAttribute("user_id", offlineParam.getUser_id());

					}
					return "mgmt/joined_off";
				} else if (joinStepVo.getJoinType() == JoinType.JOINED_AGREE_CH_OFF) { // 오프라인 경로약관동의 A0105
					String certname = certResult.getName();
					String name = OmniUtil.maskUserName(certname, locale);

					if (omniUsers != null && omniUsers.size() > 0) {

						if (omniUsers.size() == 1) {
							log.debug("▶▶▶▶▶▶ [join step] customers :  {}", StringUtil.printJson(omniUsers));
							UmOmniUser omniUser = omniUsers.get(0);
							final String id = omniUser.getUmUserName(); // 약관동의에서 아이디는 마스킹하지 않음.
							model.addAttribute("id", id);
							// 휴대전화번호는 고객통합을 조회
							CustInfoVo custInfoVo = new CustInfoVo();
							custInfoVo.setIncsNo(omniUser.getIncsNo());
							final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
							log.debug("▶▶▶▶▶ [login step] customer info : {}", StringUtil.printJson(customer));
							if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
								name = OmniUtil.maskUserName(customer.getCustNm(), locale);
								final String mobileNo = StringUtil.mergeMobile(customer);
								log.debug("▶▶▶▶▶ [login step] mobile no[1] : {}", mobileNo);
								final String mobile = OmniUtil.maskMobile(mobileNo, locale);
								log.debug("▶▶▶▶▶ [login step] mobile no[2] : {}", mobile);
								model.addAttribute("mobile", mobile);
							}
							
							// 오설록 티하우스, 백화점 오설록에서 회원 가입 시 오설록 Mall 미동의 약관 조회 후 가입 처리
							if(chCd.equals(OmniConstants.OSULLOC_OFFLINE_CHCD) || chCd.equals(OmniConstants.OSULLOC_DEPARTMENT_CHCD)) {
								final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
								omniUser.setChCd(onlineChCd);
								if (!this.termsService.hasCorpTermsAgree(omniUser)) { // 전사약관 미동의상태 체크
									log.debug("▶▶▶▶▶ [login step] 통합회원 고객 번호 존재, 채널 약관 동의 상태, 오픈 후 최초 로그인");
									log.debug("▶▶▶▶▶ [login step] 전사 약관동의 --> {}", LoginType.CORPAGREE.getDesc());

									TermsVo termsVo = new TermsVo();
									termsVo.setChCd(onlineChCd);
									termsVo.setIncsNo(incsNo);
									List<TermsVo> termsList = this.termsService.getTerms(termsVo);
									model.addAttribute("terms", termsList);
									
									TermsVo corpTermsVo = new TermsVo();
									corpTermsVo.setChCd(onlineChCd);
									List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
									model.addAttribute("corptermslist", corpTermsList);
								}								
							}

							model.addAttribute("incsno", omniUser.getIncsNo());
							model.addAttribute("incsNo", omniUser.getIncsNo());
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

							String profile = this.systemInfo.getActiveProfiles()[0];
							profile = StringUtils.isEmpty(profile) ? "dev" : profile;
							profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
							model.addAttribute("loginurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
							model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
							model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
							Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
							if (obj != null) {
								OfflineParam offlineParam = (OfflineParam) obj;
								model.addAttribute(OmniConstants.RD_URL, OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
								model.addAttribute("chnCd", offlineParam.getChnCd());
								model.addAttribute("storeCd", offlineParam.getStoreCd());
								model.addAttribute("storenm", offlineParam.getStorenm());
								model.addAttribute("user_id", offlineParam.getUser_id());

							}
							
							//20230323 채널 문자 수신 동의
							UmOmniUser omniUser2 = new UmOmniUser();
							WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
							final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
							omniUser2.setChCd(onlineChCd);
							if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
								TermsVo termsVo = new TermsVo();
								termsVo.setChCd(onlineChCd);
								List<TermsVo> termsList = this.termsService.getTermsChoice(termsVo);
								for(TermsVo vo : termsList) {
									if(vo.getMkSn()==-10) {
										model.addAttribute("terms_marketing", vo);
									}
									if(vo.getMkSn()==-20) {
										model.addAttribute("terms_marketing_online", vo);
									}
								}
							}
							
							return "terms/terms_apply_off";
						} else {
							List<Customer> customers = new ArrayList<>();
							for (UmOmniUser user : omniUsers) {
								Customer customer = new Customer();
								customer.setChcsNo(user.getUmUserName());
								customer.setCustNm(apiCustomer.getCustNm());
								customer.setCellTexn(apiCustomer.getCellTexn());
								customer.setCellTidn(apiCustomer.getCellTidn());
								customer.setCellTlsn(apiCustomer.getCellTlsn());
								customer.setMbrJoinDt(user.getCreatedDate());
								customers.add(customer);
							}
							model.addAttribute("users", customers);
							model.addAttribute("multiflag", customers.size() == 1 ? false : customers.size() > 1 ? true : false);

							String profile = this.systemInfo.getActiveProfiles()[0];
							profile = StringUtils.isEmpty(profile) ? "dev" : profile;
							profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
							model.addAttribute("loginurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
							model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
							model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
							Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
							if (obj != null) {
								OfflineParam offlineParam = (OfflineParam) obj;
								model.addAttribute(OmniConstants.RD_URL, OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
								model.addAttribute("chnCd", offlineParam.getChnCd());
								model.addAttribute("storeCd", offlineParam.getStoreCd());
								model.addAttribute("storenm", offlineParam.getStorenm());
								model.addAttribute("user_id", offlineParam.getUser_id());

							}

							return "mgmt/joined_off_01";
						}

					}

				}
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
				model.addAttribute("joinAditor", joinAditor);
				String headerType = config.isHeaderType(channel.getChCd(), profile);
				model.addAttribute("headertype", headerType);
				
				return "mgmt/joined";
			} else if (response.getType() == JoinDivisionType.CHANNEL_JOIN.getType()) { // 약관동의 A0101
				
				/*
				 * ####################### ga tagging 회원가입 시작 처리 pks 2022.03.10 위치 변경  ###################
				 * 
				 * 처리 유형 : X X X , X X O
				 */
				
				log.debug("▶▶▶▶▶▶  GA Tagging JOIN STEP TYPE : {}({})",response.getJoinStep().getJoinType().getDesc(), response.getJoinStep().getJoinType().getType());
				log.debug("▶▶▶▶▶▶  GA Tagging JOIN DATA : 가입타입:{},통합회원가입:{},경로회원가입:{} ",joinCase,totalJoinCnt,channelJoinCnt);
				
				WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL, totalJoinCnt); //통합회원
				WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL, channelJoinCnt); //경로회원
				
				try {
							
				 	log.debug("▶▶▶▶▶▶  GA Tagging JOIN START(/step 처리유형 : 신규고객 ) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
								,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),eventLabel,channel.getChCdNm(),channel.getChCd());
					
				 	// 회원가입 중복 입력 방지를 위해 세션에 eventAction 및 EL 값 저장
				 	WebUtil.setSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_START_CHECK_KEY, "true");
				 	WebUtil.setSession(OmniGaTaggingConstants.GA_JOIN_EVENT_LABEL, eventLabel);
						  
					GaTagData gaTagDto = GaTagData.builder()
															.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
							                                .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
							                                .el(eventLabel)
							                                .loginType(eventLabel)
							                                .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
							                                .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
							                                .chCd(channel.getChCd())
							                                .chCdNm(channel.getChCdNm())
							                                .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_START)
							                                .joinType(joinCase)
							                                .totalJoinCnt(totalJoinCnt)
							                                .channelJoinCnt(channelJoinCnt)
							                                .sendFlag("Y")
						                                	.incsNo(incsNo)
						                                	.sessionId(request.getSession().getId())						                   
						                                	.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]").build();
									
					  
					gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);
					
				}catch(Exception ex) {
					log.error(ex.getMessage());
				}
				
				
				model.addAttribute("certType", type); // 인증타입(ipin, kmcis)
				model.addAttribute("itgType", itg); // 인증타입(ipin, kmcis)
				if (response.getJoinStep().getJoinType() == JoinType.CHANNEL_OFF) {

					servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
					servletResponse.setHeader("Pragma", "no-cache");
					servletResponse.setDateHeader("Expires", 0);

					String name = OmniUtil.maskUserName(certResult.getName(), locale);
					model.addAttribute("name", name);
					final String mobileNo = certResult.getPhone();
					log.debug("▶▶▶▶▶ [login step] mobile no[1] : {}", mobileNo);
					final String mobile = OmniUtil.maskMobile(mobileNo, locale);
					log.debug("▶▶▶▶▶ [login step] mobile no[2] : {}", mobile);
					model.addAttribute("mobile", mobile);
					model.addAttribute("joindate", DateUtil.getBirthDate(certResult.getBirth()));

					model.addAttribute("name", name);
					model.addAttribute("xname", SecurityUtil.setXyzValue(name));

					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
					if (obj != null) {
						OfflineParam offlineParam = (OfflineParam) obj;
						model.addAttribute(OmniConstants.RD_URL, OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
						model.addAttribute("chnCd", offlineParam.getChnCd());
						model.addAttribute("storeCd", offlineParam.getStoreCd());
						model.addAttribute("storenm", offlineParam.getStorenm());
						model.addAttribute("user_id", offlineParam.getUser_id());

					}
					final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
					TermsVo corpTermsVo = new TermsVo();
					corpTermsVo.setChCd(onlineChCd);
					List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
					model.addAttribute("corptermslist", corpTermsList);
					
					//20230323 채널 문자 수신 동의
					UmOmniUser omniUser = new UmOmniUser();
					WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
					omniUser.setChCd(onlineChCd);
					if (!this.termsService.hasTermsAgree(omniUser)) { // 진입 채널에 대한 약관 출력 정보 조회
						TermsVo termsVo = new TermsVo();
						termsVo.setChCd(onlineChCd);
						List<TermsVo> termsList = this.termsService.getTermsChoice(termsVo);
						for(TermsVo vo : termsList) {
							if(vo.getMkSn()==-10) {
								model.addAttribute("terms_marketing", vo);
							}
							if(vo.getMkSn()==-20) {
								model.addAttribute("terms_marketing_online", vo);
							}
						}
					}
					
					// 개인정보 제3자 제공 동의는 아모레퍼시픽 자회사는 처리하지 않음
					boolean isThirdPartyConsent = this.config.isThirdPartyConsent(chCd, profile);
					model.addAttribute("isThirdPartyConsent", isThirdPartyConsent);

					// return "login/id_regist_01_off";
					return "join/join_step_off";
				}

				log.debug("▶▶▶▶▶▶ [join step] join type : {}, {}", response.getType(), "약관동의");
				model.addAttribute("certType", type); // 인증타입(ipin, kmcis)
				model.addAttribute("itgType", itg); // 인증타입(ipin, kmcis)
				if (!OmniUtil.isOffline(channel)) {

					// 경로고객인 경우 약관 동의했으면 스킵
					UmOmniUser omniUser = new UmOmniUser();
					if (StringUtils.hasText(incsNo)) {
						omniUser.setIncsNo(incsNo);
					}
					omniUser.setChCd(chCd);

					if (!this.termsService.hasTermsAgree(omniUser)) { // 진입 채널에 대한 약관 출력 정보 조회
						TermsVo termsVo = new TermsVo();
						termsVo.setChCd(channel.getChCd());
						if(channel.getChCd().equals(OmniConstants.OSULLOC_CHCD)) {
							termsVo.setIncsNo(incsNo);
						}
						List<TermsVo> termsList = this.termsService.getTerms(termsVo);
						model.addAttribute("terms", termsList);
					}
					TermsVo termsVo2 = new TermsVo();
					termsVo2.setChCd(channel.getChCd());
					if(channel.getChCd().equals(OmniConstants.OSULLOC_CHCD)) {
						termsVo2.setIncsNo(incsNo);
					}
					List<TermsVo> termsList = this.termsService.getTerms(termsVo2);
					model.addAttribute("terms", termsList);

					TermsVo corpTermsVo = new TermsVo();
					corpTermsVo.setChCd(channel.getChCd());
					List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
					model.addAttribute("corptermslist", corpTermsList);
					
					//20230323 채널 문자 수신 동의
					final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
					WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
					TermsVo corpTermsVo2 = new TermsVo();
					corpTermsVo2.setChCd(onlineChCd);
					UmOmniUser omniUser2 = new UmOmniUser();
					omniUser2.setChCd(onlineChCd);
					if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
						TermsVo termsVo = new TermsVo();
						termsVo.setChCd(onlineChCd);
						List<TermsVo> termsList2 = this.termsService.getTermsChoice(termsVo);
//						model.addAttribute("terms", termsList2);
						for(TermsVo vo : termsList2) {
							if(vo.getMkSn()==-20) {
								model.addAttribute("terms_marketing", vo);
							}
						}
					}
					
					// Brand Site 중 신규 가입 시 연관된 타 채널 시스템의 광고성 수신 동의 여부를 받아야 하는 경우 (ex: 뷰티엔젤) 2021-08-03 hjw0228
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					boolean isBrandSite = config.isBrandSite(chCd, profile);
					String marketingChCd = config.getMarketingChCd(chCd, profile);
					if(isBrandSite && StringUtils.hasText(marketingChCd)) {
						model.addAttribute("isBrandSite", isBrandSite);
						final Channel marketingChannel = this.commonService.getChannel(marketingChCd);
						model.addAttribute("marketingChannel", marketingChannel);
					}

					servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
					servletResponse.setHeader("Pragma", "no-cache");
					servletResponse.setDateHeader("Expires", 0);
					
					log.info("chCd : {}", WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
					
					String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
					model.addAttribute("joinAditor", joinAditor);
					String headerType = config.isHeaderType(channel.getChCd(), profile);
					model.addAttribute("headertype", headerType);

					return "join/join_step";
				} else {

					servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
					servletResponse.setHeader("Pragma", "no-cache");
					servletResponse.setDateHeader("Expires", 0);

					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
					if (obj != null) {
						OfflineParam offlineParam = (OfflineParam) obj;
						model.addAttribute(OmniConstants.RD_URL, OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
						model.addAttribute("chnCd", offlineParam.getChnCd());
						model.addAttribute("storeCd", offlineParam.getStoreCd());
						model.addAttribute("storenm", offlineParam.getStorenm());
						model.addAttribute("user_id", offlineParam.getUser_id());
					}
					
					// 개인정보 제3자 제공 동의는 아모레퍼시픽 자회사는 처리하지 않음
					boolean isThirdPartyConsent = this.config.isThirdPartyConsent(channel.getChCd(), profile);
					model.addAttribute("isThirdPartyConsent", isThirdPartyConsent);
					
					//20230404 채널 문자 수신 동의
					final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
					TermsVo corpTermsVo = new TermsVo();
					corpTermsVo.setChCd(onlineChCd);
					UmOmniUser omniUser = new UmOmniUser();
					omniUser.setChCd(onlineChCd);
					if (!this.termsService.hasTermsAgree(omniUser)) { // 진입 채널에 대한 약관 출력 정보 조회
						TermsVo termsVo = new TermsVo();
						termsVo.setChCd(onlineChCd);
						List<TermsVo> termsList = this.termsService.getTermsChoice(termsVo);
//						model.addAttribute("terms", termsList);
						for(TermsVo vo : termsList) {
							if(vo.getMkSn()==-10) {
								model.addAttribute("terms_marketing", vo);
							}
							if(vo.getMkSn()==-20) {
								model.addAttribute("terms_marketing_online", vo);
							}
						}
					}
					
					// 오프라인 경우 웹아이디 등록 시 온라인 약관을 뿌려주어야 하므로 화면에서 처리
					return "join/join_step_off";
				}
			} else if (response.getType() == JoinDivisionType.INFO_MISMATCH.getType() || response.getType() == JoinDivisionType.NAME_MISMATCH.getType()) { // 고객센터
				log.debug("▶▶▶▶▶▶ [join step] join type : {}, {}", response.getType(), "고객센터");
				redirectAttributes.addFlashAttribute("types", response.getType());
				WebUtil.setSession("csTypes", response.getType());
				return WebUtil.getRedirectUrlWithContextPath("/mgmt/csinfo");
			} else if (response.getType() == JoinDivisionType.WITHDRAW.getType()) { // 탈퇴 가입제한
				log.debug("▶▶▶▶▶▶ [join step] join type : {}, {}", response.getType(), "탈퇴 가입제한");
				Customer customer = response.getCustomer();
				if (customer == null) {
					CustInfoVo custInfoVo = new CustInfoVo();
					custInfoVo.setIncsNo(incsNo);
					customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
					log.debug("▶▶▶▶▶▶ [join step] customer for withdraw to incsno api : {}", StringUtil.printJson(customer));
				}

				log.debug("▶▶▶▶▶▶ [join step] customer for withdraw to api : {}", StringUtil.printJson(customer));
				String withdrawDate = customer.getCustWtDttm();

				if (StringUtils.isEmpty(withdrawDate)) {
					List<UmOmniUser> omniUsers = this.mgmtService.getOmniUserList(Integer.parseInt(incsNo));
					if (omniUsers != null) {
						UmOmniUser omniUser = omniUsers.get(0);
						withdrawDate = omniUser.getDisabledDate();
					}
				}
				model.addAttribute("withdrawDate", DateUtil.getBirthDate(withdrawDate));

				if (OmniUtil.isOffline(channel)) {
					model.addAttribute("offline", true);
					Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
					if (obj != null) {
						String profile = this.systemInfo.getActiveProfiles()[0];
						profile = StringUtils.isEmpty(profile) ? "dev" : profile;
						profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
						OfflineParam offlineParam = (OfflineParam) obj;
						model.addAttribute("chCd", chCd);
						model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
						model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
						model.addAttribute("chnCd", offlineParam.getChnCd());
						model.addAttribute("storeCd", offlineParam.getStoreCd());
						model.addAttribute("storenm", offlineParam.getStorenm());
						model.addAttribute("user_id", offlineParam.getUser_id());
					} else {
						String profile = this.systemInfo.getActiveProfiles()[0];
						profile = StringUtils.isEmpty(profile) ? "dev" : profile;
						profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
						model.addAttribute("home", OmniUtil.getRedirectOfflineInitUrl(channel, profile));
						model.addAttribute("homeurl", OmniUtil.getRedirectOfflineInitUrl(channel, profile));
					}
				} else {
					model.addAttribute("offline", false);
					model.addAttribute("home", channel.getHmpgUrl());
					model.addAttribute("homeurl", channel.getHmpgUrl());
				}

				return "mgmt/restrict_withdraw";
			} else if (response.getType() == JoinDivisionType.LOCK.getType()) { // 잠김사용자

			}
			else if (response.getType() == JoinDivisionType.ERROR.getType()) { // 고객통합 API 에러
				
				throw new OmniException("시스템 오류가 발생하였습니다. 관리자에게 문의하시기 바랍니다.");
			}

		}
		TermsVo termsVo = new TermsVo();
		termsVo.setChCd(channel.getChCd());
		List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
		model.addAttribute("corptermslist", termsList);
		
		//20230404 문자 수신 동의
		final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
		WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
		TermsVo corpTermsVo = new TermsVo();
		corpTermsVo.setChCd(onlineChCd);
		UmOmniUser omniUser = new UmOmniUser();
		omniUser.setChCd(onlineChCd);
		if (!this.termsService.hasTermsAgree(omniUser)) { // 진입 채널에 대한 약관 출력 정보 조회
			TermsVo termsVo2 = new TermsVo();
			termsVo2.setChCd(onlineChCd);
			List<TermsVo> termsList2 = this.termsService.getTermsChoice(termsVo2);
//							model.addAttribute("terms", termsList2);
			for(TermsVo vo : termsList2) {
				if(vo.getMkSn()==-20) {
					model.addAttribute("terms_marketing", vo);
				}
			}
		}
		
		// Brand Site 중 신규 가입 시 연관된 타 채널 시스템의 광고성 수신 동의 여부를 받아야 하는 경우 (ex: 뷰티엔젤) 2021-08-03 hjw0228
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		boolean isBrandSite = config.isBrandSite(chCd, profile);
		String marketingChCd = config.getMarketingChCd(chCd, profile);
		if(isBrandSite && StringUtils.hasText(marketingChCd)) {
			model.addAttribute("isBrandSite", isBrandSite);
			final Channel marketingChannel = this.commonService.getChannel(marketingChCd);
			model.addAttribute("marketingChannel", marketingChannel);
		}
		

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);
		
		String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
		model.addAttribute("joinAditor", joinAditor);
		String headerType = config.isHeaderType(channel.getChCd(), profile);
		model.addAttribute("headertype", headerType);
		
		return "join/join_step";
	}

	@PostMapping("/setting")
	public String joinSetting(final JoinApplyRequest joinApplyRequest, final HttpServletRequest servletRequest, final HttpServletResponse servletResponse, final Locale locale, final HttpSession session, final Model model) {

		StopWatch stopWatch = new StopWatch("회원가입(setting post action)");
		
		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);

		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		String incsNo = SecurityUtil.getXValue(joinApplyRequest.getIncsno(), false);

		if (StringUtils.hasText(incsNo)) {
			if ("0".equals(incsNo)) {
				incsNo = "";
			}
		}

		if (StringUtils.hasText(incsNo)) {
			WebUtil.setSession(OmniConstants.INCS_NO_SESSION, Integer.parseInt(incsNo));
		}

		CertResult certResult = this.commonService.getCertResult(incsNo);

		final Map<String,String> gaCookieMap = gataggingUtils.getGaCookieMap(servletRequest);	//ga tagging get cookie value
		
		if (certResult == null) {

			try {

				log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(인증세션만료) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID), gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),
						WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

				if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
						|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
						|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

					GaTagData gaTagDto = GaTagData.builder()
							.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
							.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
							.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
							.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
							.chCd(channel.getChCd())
							.chCdNm(channel.getChCdNm())
							.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
							.joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.errorMessage("인증세션만료")
							.sendFlag("Y")
							.incsNo(incsNo)
							.sessionId(servletRequest.getSession().getId())
							.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
							.build();

					gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);
				}
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}

			throw new OmniException("인증을 위한 정보가 존재하지 않습니다.");

		}
		
		model.addAttribute("category", certResult.getCategory());

		JoinRequest joinRequest = new JoinRequest();

		joinRequest.setJoinType(joinApplyRequest.getJoinStepType()); // 35(O X X) 일때는 경로도 등록해야함.

		stopWatch.start("채널 등록");
		
		if (StringUtils.isEmpty(certResult.getName())) {
			String username = this.customerApiService.getApiCustomerName(chcd, incsNo, null, SecurityUtil.getXValue(joinApplyRequest.getUnm(), false));
			joinRequest.setUnm(username);
		} else {
			joinRequest.setUnm(certResult.getName());
		}
		final String loginid = StringUtils.hasText(SecurityUtil.getXValue(joinApplyRequest.getUid(), false)) ? SecurityUtil.getXValue(joinApplyRequest.getUid(), false).replaceAll(" ", "").toLowerCase() : ""; // 2023-07-12 loginId 공백 제거 및 소문자 처리
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(SecurityUtil.getXValue(joinApplyRequest.getUpw(), false));
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(chcd);
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (StringUtils.hasText(incsNo)) {
			joinRequest.setIncsno(incsNo);
		}
		joinRequest.setIntegrateid(joinApplyRequest.getIntegrateid());
		if (StringUtils.hasText(incsNo)) {
			joinApplyRequest.setIncsno(incsNo);
		}
		
		stopWatch.stop();
		
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [join setting] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());
				Types types = WebUtil.getTypes(offlineParam.getDt(), offlineParam.getOp());
				if (types != null) {
					joinRequest.setDeviceType(types.getDeviceType());
					joinRequest.setOsType(types.getOsType());
				}
			}
		}

		List<String> bpterms = joinApplyRequest.getBpterms();
		List<String> bptcatcds = joinApplyRequest.getBpTcatCds();
		List<String> bptncvnos = joinApplyRequest.getBpTncvNos();
		List<String> terms = joinApplyRequest.getTerms();
		List<String> tcatcds = joinApplyRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = joinApplyRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = joinApplyRequest.getMarketing();
		List<String> marketingChcds = joinApplyRequest.getMarketingChcd();

		String marketingsarr[] = OmniUtil.getListToArray(marketings);
		String marketingchcdsarr[] = OmniUtil.getListToArray(marketingChcds);

		// 수신동의
		List<Marketing> joinMarketings = new ArrayList<>();
		if (marketings != null && !marketings.isEmpty()) {
			Marketing agree = null;
			for (int i = 0; i < marketingsarr.length; i++) {
				agree = new Marketing();
				agree.setChCd(marketingchcdsarr[i]);
				agree.setSmsAgree(marketingsarr[i].equals("on") ? "Y" : "N");
				joinMarketings.add(agree);
			}
		}

		joinRequest.setMarketings(joinMarketings);

		// 뷰포 약관동의
		List<Terms> joinBpTerms = new ArrayList<>();
		if (bpterms != null && !bpterms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < bptermsarr.length; i++) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn(bptermsarr[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(bptcatcdsarrs[i]);
				term.setTncvNo(bptncvnosarrs[i]);
				term.setChgChCd(OmniConstants.JOINON_CHCD);
				joinBpTerms.add(term);
			}
		}
		joinRequest.setBpterms(joinBpTerms);

		// 경로약관동의
		List<Terms> joinTerms = new ArrayList<>();
		if (terms != null && !terms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < termarrs.length; i++) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn(termarrs[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(tcatcdarrs[i]);
				term.setTncvNo(tncvnosarrs[i]);
				term.setChgChCd(channel.getChCd());
				joinTerms.add(term);
			}
		}

		// 통합약관도 등록
		TermsVo termsVo = new TermsVo();
		if (OmniUtil.isOffline(channel)) {
			termsVo.setChCd(ChannelPairs.getOnlineCd(channel.getChCd()));
		} else {
			termsVo.setChCd(channel.getChCd());
		}
		List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
		if (corpTerms != null && !corpTerms.isEmpty()) {
			Terms term = null;
			for (TermsVo corpterm : corpTerms) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn("Y");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(corpterm.getTcatCd());
				term.setTncvNo(corpterm.getTncvNo());
				term.setChgChCd(corpterm.getChCd());
				if (joinTerms != null && !joinTerms.isEmpty()) {
					boolean termsTrue=true;
					for(int i = 0; i < joinTerms.size(); i++){
						if(joinTerms.get(i).getTcatCd().equals(term.getTcatCd())) {
							termsTrue=false;
						}
					}
					if(termsTrue==true){
						joinTerms.add(term);
					}
				}else {
					joinTerms.add(term);
				}
			}
		}

		joinRequest.setTerms(joinTerms);

		log.debug("▶▶▶▶▶▶ [join setting] request settings : {}", StringUtil.printJson(joinRequest));

		stopWatch.start("고객통합-뷰티-옴니-채널 등록");
		
		BaseResponse response = null;
		if (OmniUtil.isOffline(channel)) {
			response = this.apiOfflineProcessStep.registOfflineCustomerProcess(joinRequest);
		} else {
			response = this.apiOnlineProcessStep.registCustomerProcess(joinRequest);
		}

		stopWatch.stop();

		log.debug(stopWatch.prettyPrint());
		
		log.debug("▶▶▶▶▶▶ [join setting] result : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {
			if (StringUtils.hasText(joinRequest.getLoginid())) {
				certResult.setId(joinRequest.getLoginid());
			}

			WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certResult); // 사용자 아이디까지 포함하여 저장
			WebUtil.setSession(OmniConstants.XID_SESSION, joinApplyRequest.getUid()); // 성공하면 로그인 아이디 세션 생성(나중에 로그인할때 쓰기 위해)
			WebUtil.setSession(OmniConstants.XPW_SESSION, joinApplyRequest.getUpw()); // 성공하면 로그인 비.밀.번.호 세션 생성(나중에 로그인할때 쓰기 위해)

			if (!OmniUtil.isOffline(channel)) {
				// sns 맵핑
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(joinRequest.getLoginid());
				snsParam.setIncsNo(joinRequest.getIncsno());

				snsAuth.doSnsMapping(snsParam);
			}

			if (StringUtils.isEmpty(incsNo)) {
				incsNo = WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION);
			}

			if (OmniUtil.isOffline(channel)) {
				Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
				if (obj != null) {
					OfflineParam offlineParam = (OfflineParam) obj;
					model.addAttribute("incsNo", incsNo);
					model.addAttribute("incsno", incsNo);
					model.addAttribute("home", OmniUtil.getOfflineRedirectUrl(channel));
					model.addAttribute("homeurl", OmniUtil.getOfflineRedirectUrl(channel));

					model.addAttribute("chnCd", offlineParam.getChnCd());
					model.addAttribute("storeCd", offlineParam.getStoreCd());
					model.addAttribute("storenm", offlineParam.getStorenm());
					model.addAttribute("user_id", offlineParam.getUser_id());
				}
			}

			if (OmniUtil.isOffline(channel)) {

				if (StringUtils.hasText(joinRequest.getIntegrateid()) && !StringUtil.isTrue(joinRequest.getIntegrateid())) {
					joinRequest.setLoginid(null);
				}

				model.addAttribute("intguserid", joinRequest.getLoginid());

				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setIncsNo(incsNo);
				Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
				if (customer != null) {
					if (StringUtils.hasText(joinRequest.getUnm())) {
						model.addAttribute("name", joinRequest.getUnm());
					} else {
						model.addAttribute("name", customer.getCustNm());
					}

					if (StringUtils.hasText(customer.getMbrJoinDt())) {
						model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
					}
				}

			//회원가입 ga tagging 중복 처리 방지용 저장된 값이 없으면 실행
			if(Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY)) && !Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_START_CHECK_KEY))){
					 
			
				//####################### ga tagging 회원가입 완료처리 ###################
				try {
					 
					 log.debug("▶▶▶▶▶▶ GA Tagging JOIN SUCCESS(/setting) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
								,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());
					 
					 log.debug("▶▶▶▶▶▶  GA Tagging JOIN SUCCESS(/setting) : [통합회원가입:{},경로회원가입:{}]"
								,WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL),WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL));
					 
					 
					 GaTagData gaTagDto = GaTagData.builder()
							 .cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
                             .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
                             .el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
                             .loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
                             .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
                             .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
                             .chCd(channel.getChCd())
                             .chCdNm(channel.getChCdNm())
                             .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS)
                             .joinType(OmniGaTaggingConstants.GA_SIGNUP_CASE_INTERGRATED)
                             .totalJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL))
                             .channelJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL))
                             .sendFlag("Y")
                             .incsNo(incsNo)
                             .sessionId(servletRequest.getSession().getId())
                             .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]").build();
					 
					 gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);					 
					
					 WebUtil.setSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY,OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS);	
					 
				}catch(Exception ex) {
					log.error(ex.getMessage());
				}
		    }
				// addinfo Y로 넘어오면 바로 POS 화면전환
				Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
				if (obj != null) {
					OfflineParam offlineParam = (OfflineParam) obj;
					if (offlineParam != null) {
						String addinfo = offlineParam.getAddInfo();
						if ("Y".equals(addinfo)) {
							return "join/join_finish_off_redirect";
						}
					}
				}


				if (StringUtils.hasText(joinRequest.getLoginid())) {
					return "join/join_finish_off";
				}

				return "join/join_finish_off102";
			} else {
				return WebUtil.getRedirectUrlWithContextPath("/join/finish");
			}
		} else {//ga error tagging 추가필요

			if (OmniUtil.isOffline(channel)) {
				model.addAttribute("offline", true);
				Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
				if (obj != null) {
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					OfflineParam offlineParam = (OfflineParam) obj;
					model.addAttribute("chCd", chcd);
					model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("chnCd", offlineParam.getChnCd());
					model.addAttribute("storeCd", offlineParam.getStoreCd());
					model.addAttribute("storenm", offlineParam.getStorenm());
					model.addAttribute("user_id", offlineParam.getUser_id());
				} else {
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					return "redirect:" + OmniUtil.getRedirectOfflineInitUrl(channel, profile);
				}
				if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
					model.addAttribute("channelWtdt", response.getMessage());
					return "info/channel_withdraw";
				}
				model.addAttribute("message", response.getMessage());
				model.addAttribute("errormsg", response.getMessage());
				model.addAttribute("errorcode", response.getResultCode());
				
				try {

					log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(고객통합 에러) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID), gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),
							WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

					if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

						GaTagData gaTagDto = GaTagData.builder()
								.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
								.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
								.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
								.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
								.chCd(channel.getChCd())
								.chCdNm(channel.getChCdNm())
								.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
								.joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								.errorMessage("고객통합 에러")
								.sendFlag("Y")
								.incsNo(incsNo)
								.sessionId(servletRequest.getSession().getId())
								.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
								.build();

						gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

					}
				} catch (Exception ex) {
					log.error(ex.getMessage());
				}	
				
				return "info/api_page";
			} else {
				model.addAttribute("offline", false);
				model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
				model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
				model.addAttribute("channelName", channel.getChCdNm());
				if ("ICITSVCOM004".equals(response.getResultCode())) { // 존재하는 고객
					
					try {

						log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(존재하는 고객) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID), gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),
								WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

						if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

							GaTagData gaTagDto = GaTagData.builder()
									.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
									.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
									.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
									.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
									.chCd(channel.getChCd())
									.chCdNm(channel.getChCdNm())
									.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
									.joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.errorMessage("존재하는고객")
									.sendFlag("Y")
									.incsNo(incsNo)
									.sessionId(servletRequest.getSession().getId())
									.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
									.build();

							gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);
						}
					} catch (Exception ex) {
						log.error(ex.getMessage());
					}

					return "redirect:/info-exist"; // 존재하는 고객이면 알림 주고 로그인으로 이동
				} else if ("ICITSVCOM008".equals(response.getResultCode())) { // 망취소
					model.addAttribute("errormsg", response.getMessage());
					model.addAttribute("errorcode", response.getResultCode());
					
					try {

						log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(망취소) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID), gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),
								WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

						if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

							GaTagData gaTagDto = GaTagData.builder()
									.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
									.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
									.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
									.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
									.chCd(channel.getChCd())
									.chCdNm(channel.getChCdNm())
									.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
									.joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.errorMessage("망취소[ICITSVCOM008]")
									.sendFlag("Y")
									.incsNo(incsNo)
									.sessionId(servletRequest.getSession().getId())
									.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
									.build();

							gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);
						}
					} catch (Exception ex) {
						log.error(ex.getMessage());
					}	
					
					
					return "info/api_error";
				} else if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
					model.addAttribute("channelWtdt", response.getMessage());
					
					try {

						log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(시스템 에러) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID), gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),
								WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

						if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

							GaTagData gaTagDto = GaTagData.builder()
									.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
									.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
									.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
									.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
									.chCd(channel.getChCd())
									.chCdNm(channel.getChCdNm())
									.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
									.joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.errorMessage("시스템 에러")
									.sendFlag("Y")
									.incsNo(incsNo)
									.sessionId(servletRequest.getSession().getId())
									.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
									.build();

							gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);
						}
					} catch (Exception ex) {
						log.error(ex.getMessage());
					}

					return "info/channel_withdraw";
				} else {
					model.addAttribute("errormsg", response.getMessage());
					model.addAttribute("errorcode", response.getResultCode());
					
					try {

						log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(시스템 에러) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID), gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),
								WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

						if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

							GaTagData gaTagDto = GaTagData.builder()
									.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
									.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
									.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
									.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
									.chCd(channel.getChCd())
									.chCdNm(channel.getChCdNm())
									.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
									.joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.errorMessage("시스템 에러")
									.sendFlag("Y")
									.incsNo(incsNo).sessionId(servletRequest.getSession().getId())
									.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
									.build();

							gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

						}

					} catch (Exception ex) {
						log.error(ex.getMessage());
					}	
					
					return "info/api_error";
				}
			}

		}
		// 가입처리 끝
	}

	@PostMapping("/setcomplete")
	public String joinSettingAndComplete(final JoinApplyRequest joinApplyRequest, final HttpServletRequest servletRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) {

		StopWatch stopWatch = new StopWatch("회원가입(setComplete post action)");
		
		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);

		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		String incsNo = SecurityUtil.getXValue(joinApplyRequest.getIncsno(), false);

		if (StringUtils.hasText(incsNo)) {
			if ("0".equals(incsNo)) {
				incsNo = "";
			}
		}

		if (StringUtils.hasText(incsNo)) {
			WebUtil.setSession(OmniConstants.INCS_NO_SESSION, Integer.parseInt(incsNo));
		}

		CertResult certResult = this.commonService.getCertResult(incsNo);

		if (certResult == null) {
			throw new OmniException("인증을 위한 정보가 존재하지 않습니다.");
		}

		model.addAttribute("category", certResult.getCategory());
		
		JoinRequest joinRequest = new JoinRequest();

		joinRequest.setJoinType(joinApplyRequest.getJoinStepType()); // 35(O X X) 일때는 경로도 등록해야함.

		stopWatch.start("채널등록");
		
		if (StringUtils.isEmpty(certResult.getName())) {
			String username = this.customerApiService.getApiCustomerName(chcd, incsNo, null, SecurityUtil.getXValue(joinApplyRequest.getUnm(), false));
			joinRequest.setUnm(username);
		} else {
			joinRequest.setUnm(certResult.getName());
		}
		
		stopWatch.stop();
		
		final String loginid = StringUtils.hasText(SecurityUtil.getXValue(joinApplyRequest.getUid(), false)) ? SecurityUtil.getXValue(joinApplyRequest.getUid(), false).replaceAll(" ", "").toLowerCase() : ""; // 2023-07-12 loginId 공백 제거 및 소문자 처리
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(SecurityUtil.getXValue(joinApplyRequest.getUpw(), false));
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(chcd);
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (StringUtils.hasText(incsNo)) {
			joinRequest.setIncsno(incsNo);
		}
		joinRequest.setIntegrateid(joinApplyRequest.getIntegrateid());
		if (StringUtils.hasText(incsNo)) {
			joinApplyRequest.setIncsno(incsNo);
		}

		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [join setting] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());
				Types types = WebUtil.getTypes(offlineParam.getDt(), offlineParam.getOp());
				if (types != null) {
					joinRequest.setDeviceType(types.getDeviceType());
					joinRequest.setOsType(types.getOsType());
				}

			}
		}

		List<String> bpterms = joinApplyRequest.getBpterms();
		List<String> bptcatcds = joinApplyRequest.getBpTcatCds();
		List<String> bptncvnos = joinApplyRequest.getBpTncvNos();
		List<String> terms = joinApplyRequest.getTerms();
		List<String> tcatcds = joinApplyRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = joinApplyRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = joinApplyRequest.getMarketing();
		List<String> marketingChcds = joinApplyRequest.getMarketingChcd();

		String marketingsarr[] = OmniUtil.getListToArray(marketings);
		String marketingchcdsarr[] = OmniUtil.getListToArray(marketingChcds);

		// 수신동의
		List<Marketing> joinMarketings = new ArrayList<>();
		if (marketings != null && !marketings.isEmpty()) {
			Marketing agree = null;
			for (int i = 0; i < marketingsarr.length; i++) {
				agree = new Marketing();
				agree.setChCd(marketingchcdsarr[i]);
				agree.setSmsAgree(marketingsarr[i].equals("on") ? "Y" : "N");
				joinMarketings.add(agree);
			}
		}

		joinRequest.setMarketings(joinMarketings);

		stopWatch.start("뷰티약관동의");
		
		// 뷰포 약관동의
		List<Terms> joinBpTerms = new ArrayList<>();
		if (bpterms != null && !bpterms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < bptermsarr.length; i++) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn(bptermsarr[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(bptcatcdsarrs[i]);
				term.setTncvNo(bptncvnosarrs[i]);
				term.setChgChCd(OmniConstants.JOINON_CHCD);
				joinBpTerms.add(term);
			}
		}
		joinRequest.setBpterms(joinBpTerms);
		
		stopWatch.stop();
		
		stopWatch.start("경로약관동의");
		
		// 경로약관동의
		List<Terms> joinTerms = new ArrayList<>();
		if (terms != null && !terms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < termarrs.length; i++) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn(termarrs[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(tcatcdarrs[i]);
				term.setTncvNo(tncvnosarrs[i]);
				term.setChgChCd(channel.getChCd());
				joinTerms.add(term);
			}
		}

		// 통합약관도 등록
		TermsVo termsVo = new TermsVo();
		if (OmniUtil.isOffline(channel)) {
			termsVo.setChCd(ChannelPairs.getOnlineCd(channel.getChCd()));
		} else {
			termsVo.setChCd(channel.getChCd());
		}
		List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
		if (corpTerms != null && !corpTerms.isEmpty()) {
			Terms term = null;
			for (TermsVo corpterm : corpTerms) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn("Y");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(corpterm.getTcatCd());
				term.setTncvNo(corpterm.getTncvNo());
				term.setChgChCd(corpterm.getChCd());
				joinTerms.add(term);
			}
		}

		joinRequest.setTerms(joinTerms);

		stopWatch.stop();
		
		log.debug("▶▶▶▶▶▶ [join setting] request settings : {}", StringUtil.printJson(joinRequest));

		stopWatch.start("고객통합-뷰티-옴니-채널 등록");
		
		BaseResponse response = null;
		if (OmniUtil.isOffline(channel)) {
			response = this.apiOfflineProcessStep.registOfflineCustomerProcess(joinRequest);
		} else {
			//TODO non-block api call test pks 2022.02.17
			response = this.apiOnlineProcessStep.registCustomerProcess(joinRequest);
		}

		stopWatch.stop();
		log.debug(stopWatch.prettyPrint());
		
		log.debug("▶▶▶▶▶▶ [join setting] result : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {
			if (StringUtils.hasText(joinRequest.getLoginid())) {
				certResult.setId(joinRequest.getLoginid());
			}

			WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certResult); // 사용자 아이디까지 포함하여 저장
			WebUtil.setSession(OmniConstants.XID_SESSION, joinApplyRequest.getUid()); // 성공하면 로그인 아이디 세션 생성(나중에 로그인할때 쓰기 위해)
			WebUtil.setSession(OmniConstants.XPW_SESSION, joinApplyRequest.getUpw()); // 성공하면 로그인 비.밀.번.호 세션 생성(나중에 로그인할때 쓰기 위해)

			if (!OmniUtil.isOffline(channel)) {
				// sns 맵핑
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(joinRequest.getLoginid());
				snsParam.setIncsNo(joinRequest.getIncsno());

				snsAuth.doSnsMapping(snsParam);
			}

			if (StringUtils.isEmpty(incsNo)) {
				incsNo = WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION);
			}

			if (OmniUtil.isOffline(channel)) {
				Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
				if (obj != null) {
					OfflineParam offlineParam = (OfflineParam) obj;
					model.addAttribute("incsNo", incsNo);
					model.addAttribute("incsno", incsNo);
					model.addAttribute("home", OmniUtil.getOfflineRedirectUrl(channel));
					model.addAttribute("homeurl", OmniUtil.getOfflineRedirectUrl(channel));

					model.addAttribute("chnCd", offlineParam.getChnCd());
					model.addAttribute("storeCd", offlineParam.getStoreCd());
					model.addAttribute("storenm", offlineParam.getStorenm());
					model.addAttribute("user_id", offlineParam.getUser_id());

				}
			}
		
			//회원가입 ga tagging 중복 처리 방지용 저장된 값이 없으면 실행
			if(Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY)) && !Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_START_CHECK_KEY))){
			/* ####################### ga tagging 회원가입 완료처리(경로가입 처리) ###################
			 * O O X 
			 */
				try {

					Map<String, String> gaCookieMap = gataggingUtils.getGaCookieMap(servletRequest);

					log.debug("▶▶▶▶▶▶ GA Tagging JOIN SUCCESS(/setcomplete) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID),
							gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

					log.debug("▶▶▶▶▶▶  GA Tagging JOIN SUCCESS(/setcomplete) : [통합회원가입:{},경로회원가입:{}]", WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL),
							WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL));

					GaTagData gaTagDto = GaTagData.builder()
							.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
							.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
							.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
							.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
							.chCd(channel.getChCd())
							.chCdNm(channel.getChCdNm())
							.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS)
							.joinType(OmniGaTaggingConstants.GA_SIGNUP_CASE_INTERGRATED)
							.totalJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL))
							.channelJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL))
							.sendFlag("Y")
							.incsNo(incsNo)
							.sessionId(servletRequest.getSession().getId())
							.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
							.build();

					gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

					WebUtil.setSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY, OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS);

				} catch (Exception ex) {
					log.error(ex.getMessage());
				}			
			}
			
			if (OmniUtil.isOffline(channel)) {

				if (StringUtils.hasText(joinRequest.getIntegrateid()) && !StringUtil.isTrue(joinRequest.getIntegrateid())) {
					joinRequest.setLoginid(null);
				}

				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setIncsNo(incsNo);
				Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
				if (customer != null) {

					if (StringUtils.hasText(joinRequest.getUnm())) {
						model.addAttribute("name", joinRequest.getUnm());
					} else {
						model.addAttribute("name", customer.getCustNm());
					}

					if (StringUtils.hasText(joinRequest.getLoginid())) {
						model.addAttribute("intguserid", joinRequest.getLoginid());
					} else {
						model.addAttribute("intguserid", customer.getChcsNo());
					}

					if (StringUtils.hasText(customer.getMbrJoinDt())) {
						model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
					}
				}

				// addinfo Y로 넘어오면 바로 POS 화면 전환
				Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
				if (obj != null) {
					OfflineParam offlineParam = (OfflineParam) obj;
					if (offlineParam != null) {
						String addinfo = offlineParam.getAddInfo();
						if ("Y".equals(addinfo)) {
							return "join/join_finish_off_redirect";
						}
					}
				}

				
				
				
				
				if (StringUtils.hasText(joinRequest.getLoginid())) {
					return "join/join_finish_off";
				}

				return "join/join_finish_off102";
			} else {
				return WebUtil.getRedirectUrlWithContextPath("/join/complete");
			}
		} else {

			if (OmniUtil.isOffline(channel)) {
				model.addAttribute("offline", true);
				Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
				if (obj != null) {
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					OfflineParam offlineParam = (OfflineParam) obj;
					model.addAttribute("chCd", chcd);
					model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("chnCd", offlineParam.getChnCd());
					model.addAttribute("storeCd", offlineParam.getStoreCd());
					model.addAttribute("storenm", offlineParam.getStorenm());
					model.addAttribute("user_id", offlineParam.getUser_id());
				} else {
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					return "redirect:" + OmniUtil.getRedirectOfflineInitUrl(channel, profile);
				}
				if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
					model.addAttribute("channelWtdt", response.getMessage());
					return "info/channel_withdraw";
				}
				model.addAttribute("message", response.getMessage());
				model.addAttribute("errormsg", response.getMessage());
				model.addAttribute("errorcode", response.getResultCode());
				return "info/api_page";
			} else {
				model.addAttribute("offline", false);
				model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
				model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
				model.addAttribute("channelName", channel.getChCdNm());
				if ("ICITSVCOM004".equals(response.getResultCode())) { // 존재하는 고객
					return "redirect:/info-exist"; // 존재하는 고객이면 알림 주고 로그인으로 이동
				} else if ("ICITSVCOM008".equals(response.getResultCode())) { // 망취소
					model.addAttribute("errormsg", response.getMessage());
					model.addAttribute("errorcode", response.getResultCode());
					return "info/api_error";
				} else if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
					model.addAttribute("channelWtdt", response.getMessage());
					return "info/channel_withdraw";
				} else {
					model.addAttribute("errormsg", response.getMessage());
					model.addAttribute("errorcode", response.getResultCode());
					return "info/api_error";
				}
			}
		}
		// 가입처리 끝
	}

	// ME-FO-A0207 O X O
	@PostMapping("/plogin-regist-bp")
	public String joinPhoneRegistBp(final JoinApplyRequest joinApplyRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);

		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		final String incsNo = SecurityUtil.getXValue(joinApplyRequest.getIncsno(), false);

		CertResult certResult = this.commonService.getCertResult(incsNo);

		if (certResult == null) {
			throw new OmniException("인증을 위한 정보가 존재하지 않습니다.");
		}

		model.addAttribute("category", certResult.getCategory());
		
		JoinRequest joinRequest = new JoinRequest();
		if (StringUtils.isEmpty(certResult.getName())) {
			String username = this.customerApiService.getApiCustomerName(chcd, incsNo, null, SecurityUtil.getXValue(joinApplyRequest.getUnm(), false));
			joinRequest.setUnm(username);
		} else {
			joinRequest.setUnm(certResult.getName());
		}
		final String loginid = StringUtils.hasText(SecurityUtil.getXValue(joinApplyRequest.getUid(), false)) ? SecurityUtil.getXValue(joinApplyRequest.getUid(), false).replaceAll(" ", "").toLowerCase() : ""; // 2023-07-12 loginId 공백 제거 및 소문자 처리
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(SecurityUtil.getXValue(joinApplyRequest.getUpw(), false));
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(chcd);
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (StringUtils.hasText(incsNo)) {
			joinRequest.setIncsno(incsNo);
		}
		joinRequest.setIntegrateid(joinApplyRequest.getIntegrateid());
		if (StringUtils.hasText(incsNo)) {
			joinApplyRequest.setIncsno(incsNo);
		}

		model.addAttribute("intguserid", joinRequest.getLoginid());
		WebUtil.setSession("intguserid", joinRequest.getLoginid());

		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [mobile regist] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());
				Types types = WebUtil.getTypes(offlineParam.getDt(), offlineParam.getOp());
				if (types != null) {
					joinRequest.setDeviceType(types.getDeviceType());
					joinRequest.setOsType(types.getOsType());
				}
			}
		}

		List<String> bpterms = joinApplyRequest.getBpterms();
		List<String> bptcatcds = joinApplyRequest.getBpTcatCds();
		List<String> bptncvnos = joinApplyRequest.getBpTncvNos();
		List<String> terms = joinApplyRequest.getTerms();
		List<String> tcatcds = joinApplyRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = joinApplyRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = joinApplyRequest.getMarketing();
		List<String> marketingChcds = joinApplyRequest.getMarketingChcd();

		String marketingsarr[] = OmniUtil.getListToArray(marketings);
		String marketingchcdsarr[] = OmniUtil.getListToArray(marketingChcds);

		// 수신동의
		List<Marketing> joinMarketings = new ArrayList<>();
		if (marketings != null && !marketings.isEmpty()) {
			Marketing agree = null;
			for (int i = 0; i < marketingsarr.length; i++) {
				agree = new Marketing();
				agree.setChCd(marketingchcdsarr[i]);
				agree.setSmsAgree(marketingsarr[i].equals("on") ? "Y" : "N");
				joinMarketings.add(agree);
			}
		}

		joinRequest.setMarketings(joinMarketings);

		// 뷰포 약관동의
		List<Terms> joinBpTerms = new ArrayList<>();
		if (bpterms != null && !bpterms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < bptermsarr.length; i++) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn(bptermsarr[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(bptcatcdsarrs[i]);
				term.setTncvNo(bptncvnosarrs[i]);
				term.setChgChCd(OmniConstants.JOINON_CHCD);
				joinBpTerms.add(term);
			}
		}
		joinRequest.setBpterms(joinBpTerms);

		// 경로약관동의
		List<Terms> joinTerms = new ArrayList<>();
		if (terms != null && !terms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < termarrs.length; i++) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn(termarrs[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(tcatcdarrs[i]);
				term.setTncvNo(tncvnosarrs[i]);
				term.setChgChCd(channel.getChCd());
				joinTerms.add(term);
			}
		}

		// 통합약관도 등록
		TermsVo termsVo = new TermsVo();
		if (OmniUtil.isOffline(channel)) {
			termsVo.setChCd(ChannelPairs.getOnlineCd(channel.getChCd()));
		} else {
			termsVo.setChCd(channel.getChCd());
		}
		List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
		if (corpTerms != null && !corpTerms.isEmpty()) {
			Terms term = null;
			for (TermsVo corpterm : corpTerms) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn("Y");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(corpterm.getTcatCd());
				term.setTncvNo(corpterm.getTncvNo());
				term.setChgChCd(corpterm.getChCd());
				joinTerms.add(term);
			}
		}

		joinRequest.setTerms(joinTerms);

		log.debug("▶▶▶▶▶▶ [mobile regist] request settings : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = this.apiOnlineJoinProcessStep.joinBpRegistProcess(joinRequest);

		log.debug("▶▶▶▶▶▶ [mobile regist] result : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {
			if (StringUtils.hasText(joinRequest.getLoginid())) {
				certResult.setId(joinRequest.getLoginid());
			}

			WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certResult); // 사용자 아이디까지 포함하여 저장
			WebUtil.setSession(OmniConstants.XID_SESSION, joinApplyRequest.getUid()); // 성공하면 로그인 아이디 세션 생성(나중에 로그인할때 쓰기 위해)
			WebUtil.setSession(OmniConstants.XPW_SESSION, joinApplyRequest.getUpw()); // 성공하면 로그인 비.밀.번.호 세션 생성(나중에 로그인할때 쓰기 위해)

			// sns 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(joinRequest.getLoginid());
				snsParam.setIncsNo(joinRequest.getIncsno());

				snsAuth.doSnsMapping(snsParam);
			}
			servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			servletResponse.setHeader("Pragma", "no-cache");
			servletResponse.setDateHeader("Expires", 0);
			// return WebUtil.getRedirectUrlWithContextPath("/join/finish");
			return WebUtil.getRedirectUrlWithContextPath("/join/complete");

		} else {
			model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("channelName", channel.getChCdNm());
			if ("ICITSVCOM004".equals(response.getResultCode())) { // 존재하는 고객
				return "redirect:/info-exist"; // 존재하는 고객이면 알림 주고 로그인으로 이동
			} else if ("ICITSVCOM008".equals(response.getResultCode())) { // 망취소
				model.addAttribute("errormsg", response.getMessage());
				model.addAttribute("errorcode", response.getResultCode());
				return "info/api_error";
			} else if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
				model.addAttribute("channelWtdt", response.getMessage());
				return "info/channel_withdraw";
			} else {
				model.addAttribute("errormsg", response.getMessage());
				model.addAttribute("errorcode", response.getResultCode());
				return "info/api_error";
			}

		}
		// 가입처리 끝
	}

	@PostMapping("/plogin-regist")
	public String joinPhoneLoginRegist(final JoinApplyRequest joinApplyRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);

		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		final String incsNo = SecurityUtil.getXValue(joinApplyRequest.getIncsno(), false);

		CertResult certResult = this.commonService.getCertResult(incsNo);

		if (certResult == null) {
			throw new OmniException("인증을 위한 정보가 존재하지 않습니다.");
		}
		
		model.addAttribute("category", certResult.getCategory());

		JoinRequest joinRequest = new JoinRequest();
		if (StringUtils.isEmpty(certResult.getName())) {
			String username = this.customerApiService.getApiCustomerName(chcd, incsNo, null, SecurityUtil.getXValue(joinApplyRequest.getUnm(), false));
			joinRequest.setUnm(username);
		} else {
			joinRequest.setUnm(certResult.getName());
		}
		final String loginid = StringUtils.hasText(SecurityUtil.getXValue(joinApplyRequest.getUid(), false)) ? SecurityUtil.getXValue(joinApplyRequest.getUid(), false).replaceAll(" ", "").toLowerCase() : ""; // 2023-07-12 loginId 공백 제거 및 소문자 처리
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(SecurityUtil.getXValue(joinApplyRequest.getUpw(), false));
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(chcd);
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (StringUtils.hasText(incsNo)) {
			joinRequest.setIncsno(incsNo);
		}
		joinRequest.setIntegrateid(joinApplyRequest.getIntegrateid());
		if (StringUtils.hasText(incsNo)) {
			joinApplyRequest.setIncsno(incsNo);
		}

		model.addAttribute("intguserid", joinRequest.getLoginid());
		WebUtil.setSession("intguserid", joinRequest.getLoginid());

		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [mobile regist] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());
				Types types = WebUtil.getTypes(offlineParam.getDt(), offlineParam.getOp());
				if (types != null) {
					joinRequest.setDeviceType(types.getDeviceType());
					joinRequest.setOsType(types.getOsType());
				}

			}
		}

		List<String> bpterms = joinApplyRequest.getBpterms();
		List<String> bptcatcds = joinApplyRequest.getBpTcatCds();
		List<String> bptncvnos = joinApplyRequest.getBpTncvNos();
		List<String> terms = joinApplyRequest.getTerms();
		List<String> tcatcds = joinApplyRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = joinApplyRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = joinApplyRequest.getMarketing();
		List<String> marketingChcds = joinApplyRequest.getMarketingChcd();

		String marketingsarr[] = OmniUtil.getListToArray(marketings);
		String marketingchcdsarr[] = OmniUtil.getListToArray(marketingChcds);

		// 수신동의
		List<Marketing> joinMarketings = new ArrayList<>();
		if (marketings != null && !marketings.isEmpty()) {
			Marketing agree = null;
			for (int i = 0; i < marketingsarr.length; i++) {
				agree = new Marketing();
				agree.setChCd(marketingchcdsarr[i]);
				agree.setSmsAgree(marketingsarr[i].equals("on") ? "Y" : "N");
				joinMarketings.add(agree);
			}
		}

		joinRequest.setMarketings(joinMarketings);

		// 뷰포 약관동의
		List<Terms> joinBpTerms = new ArrayList<>();
		if (bpterms != null && !bpterms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < bptermsarr.length; i++) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn(bptermsarr[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(bptcatcdsarrs[i]);
				term.setTncvNo(bptncvnosarrs[i]);
				term.setChgChCd(OmniConstants.JOINON_CHCD);
				joinBpTerms.add(term);
			}
		}
		joinRequest.setBpterms(joinBpTerms);

		// 경로약관동의
		List<Terms> joinTerms = new ArrayList<>();
		if (terms != null && !terms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < termarrs.length; i++) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn(termarrs[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(tcatcdarrs[i]);
				term.setTncvNo(tncvnosarrs[i]);
				term.setChgChCd(channel.getChCd());
				joinTerms.add(term);
			}
		}

		// 통합약관도 등록
		TermsVo termsVo = new TermsVo();
		if (OmniUtil.isOffline(channel)) {
			termsVo.setChCd(ChannelPairs.getOnlineCd(channel.getChCd()));
		} else {
			termsVo.setChCd(channel.getChCd());
		}
		List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
		if (corpTerms != null && !corpTerms.isEmpty()) {
			Terms term = null;
			for (TermsVo corpterm : corpTerms) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn("Y");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(corpterm.getTcatCd());
				term.setTncvNo(corpterm.getTncvNo());
				term.setChgChCd(corpterm.getChCd());
				joinTerms.add(term);
			}
		}

		joinRequest.setTerms(joinTerms);

		log.debug("▶▶▶▶▶▶ [mobile regist] request settings : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = this.apiOnlineJoinProcessStep.joinPhoneRegistProcess(joinRequest);

		log.debug("▶▶▶▶▶▶ [mobile regist] result : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {
			if (StringUtils.hasText(joinRequest.getLoginid())) {
				certResult.setId(joinRequest.getLoginid());
			}

			WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certResult); // 사용자 아이디까지 포함하여 저장
			WebUtil.setSession(OmniConstants.XID_SESSION, joinApplyRequest.getUid()); // 성공하면 로그인 아이디 세션 생성(나중에 로그인할때 쓰기 위해)
			WebUtil.setSession(OmniConstants.XPW_SESSION, joinApplyRequest.getUpw()); // 성공하면 로그인 비.밀.번.호 세션 생성(나중에 로그인할때 쓰기 위해)

			// sns 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(joinRequest.getLoginid());
				snsParam.setIncsNo(joinRequest.getIncsno());

				snsAuth.doSnsMapping(snsParam);
			}
			// return WebUtil.getRedirectUrlWithContextPath("/join/finish");
			return WebUtil.getRedirectUrlWithContextPath("/join/complete");
		} else {
			model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("channelName", channel.getChCdNm());
			if ("ICITSVCOM004".equals(response.getResultCode())) { // 존재하는 고객
				log.debug("▶▶▶▶▶▶ [conversion terms-finish-202-ch] customer : {}", "존재하는 고객");
				return "redirect:/info-exist"; // 존재하는 고객이면 알림 주고 로그인으로 이동
			} else if ("ICITSVCOM008".equals(response.getResultCode())) { // 망취소
				model.addAttribute("errormsg", response.getMessage());
				model.addAttribute("errorcode", response.getResultCode());
				return "info/api_error";
			} else if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
				model.addAttribute("channelWtdt", response.getMessage());
				return "info/channel_withdraw";
			} else {
				model.addAttribute("errormsg", response.getMessage());
				model.addAttribute("errorcode", response.getResultCode());
				return "info/api_error";
			}

		}
		// 가입처리 끝
	}

	@RequestMapping(value = "/finish", method = { RequestMethod.GET, RequestMethod.POST })
	public String joinEnd(final Model model, final HttpServletRequest servletRequest, final HttpServletResponse servletResponse, final Locale locale) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		String incsNo = WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION);
		model.addAttribute("incsno", incsNo);
		model.addAttribute("incsNo", incsNo);
		CertResult certResult = this.commonService.getCertResult(incsNo);

		if (certResult != null) {
			String name = certResult.getName();
			final String username = OmniUtil.maskUserName(name, locale);
			StringBuilder nameid = new StringBuilder(username);
			String id = certResult.getId();
			if (StringUtils.hasText(id)) {
				final String userid = OmniUtil.maskUserId(id);
				nameid.append("(").append(userid).append(")");
			}
			model.addAttribute("category", certResult.getCategory());
			model.addAttribute("nameid", nameid.toString());
		}

		final Channel channel = this.commonService.getChannel(chcd);
		
		if(channel == null || StringUtils.isEmpty(channel.getChCd())) {
			log.error("▶▶▶▶▶▶ [/redirect-authz] Channel Code is null");
			model.addAttribute("oauthErrorCode", "9999");
			model.addAttribute("oauthErrorMsg", "경로코드가 존재하지 않습니다.");
			model.addAttribute("message", "현재 서비스가 원할하지 않습니다.<br>잠시 후 다시 시도해주시기 바랍니다.");

			return "wso2/oauth2_error";
		}
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());

		model.addAttribute("offline", OmniUtil.isOffline(channel));

		if (OmniUtil.isOffline(channel)) {

			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;
				model.addAttribute("incsNo", incsNo);
				model.addAttribute("incsno", incsNo);
				model.addAttribute("home", OmniUtil.getOfflineRedirectUrl(channel));
				model.addAttribute("homeurl", OmniUtil.getOfflineRedirectUrl(channel));
				model.addAttribute(OmniConstants.RD_URL, OmniUtil.getOfflineRedirectUrl(channel));
				model.addAttribute("chnCd", offlineParam.getChnCd());
				model.addAttribute("storeCd", offlineParam.getStoreCd());
				model.addAttribute("storenm", offlineParam.getStorenm());
				model.addAttribute("user_id", offlineParam.getUser_id());

			}

		} else {
			model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		}

		model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));

	    if(Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) 
				  && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String)WebUtil.getSession(OmniGaTaggingConstants.EL))
						  || OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String)WebUtil.getSession(OmniGaTaggingConstants.EL)) 
						  || OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String)WebUtil.getSession(OmniGaTaggingConstants.EL)))){
			 
			//회원가입 ga tagging 중복 처리 방지용 저장된 값이 없으면 실행
			if(Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY)) && !Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_START_CHECK_KEY))){
				
				// ####################### ga tagging 회원가입 완료처리 ###################
				try {

					Map<String, String> gaCookieMap = gataggingUtils.getGaCookieMap(servletRequest);
					String joinType = (String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE);
					String joinCase = OmniGaTaggingConstants.GA_SIGNUP_CASE_INTERGRATED;

					if ("omni".equals(joinType)) {
						joinCase = OmniGaTaggingConstants.GA_SIGNUP_CASE_MEMBER;
					}

					log.debug("▶▶▶▶▶▶ GA Tagging JOIN SUCCESS(/finish) : [joinType:{}, cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", joinType, gaCookieMap.get(OmniGaTaggingConstants.CID),
							gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

					log.debug("▶▶▶▶▶▶  GA Tagging JOIN SUCCESS(/finish) : [통합회원가입:{},경로회원가입:{}]", WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL),
							WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL));

					GaTagData gaTagDto = GaTagData.builder()
							.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
							.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
							.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
							.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
							.chCd(channel.getChCd())
							.chCdNm(channel.getChCdNm())
							.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS)
							.joinType(joinCase)
							.totalJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL))
							.channelJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL))
							.sendFlag("Y")
							.incsNo(incsNo).sessionId(servletRequest.getSession().getId())
							.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
							.build();

					gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

					WebUtil.setSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY, OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS);

				} catch (Exception ex) {
					log.error(ex.getMessage());
				}
			}
	    }	
		// 뷰티 멤버십 연동을 통한 회원가입 시 멤버십 연동 완료 화면으로 이동
		final String isMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_MEMBERSHIP);
		boolean isMembership = OmniUtil.isMembership(isMembershipSession);
		if(isMembership) {
			try {
				MembershipUserInfo membershipUserInfo = (MembershipUserInfo) WebUtil.getSession(OmniConstants.MEMBERSHIP_USERINFO);
				if(StringUtils.isEmpty(membershipUserInfo.getId()) || StringUtils.isEmpty(membershipUserInfo.getIncsNo()) || StringUtils.isEmpty(membershipUserInfo.getName())) {
					if(StringUtils.isEmpty(membershipUserInfo.getId())) {
						String id = certResult.getId();
						if (StringUtils.hasText(id)) {
							membershipUserInfo.setId(id);
						}	
					}
					
					if(StringUtils.isEmpty(membershipUserInfo.getIncsNo())) {
						membershipUserInfo.setIncsNo(incsNo);
					}
					
					if(StringUtils.isEmpty(membershipUserInfo.getName())) {
						String name = certResult.getName();
						if (StringUtils.hasText(name)) {
							membershipUserInfo.setName(name);
						}
					}
					
					WebUtil.setSession(OmniConstants.MEMBERSHIP_USERINFO, membershipUserInfo);
				}
				
				if (certResult != null) {
					String name = certResult.getName();
					model.addAttribute("name", OmniUtil.maskUserName(name, locale));
					String loginid = certResult.getId();
					if (StringUtils.hasText(loginid)) {
						model.addAttribute("loginid", OmniUtil.maskUserId(loginid));
					}
				}
				
				return "membership/membership_join_finish";	
			} catch (Exception e) {
				log.error("message : {}", e.getMessage());
				model.addAttribute("message", ResultCode.SYSTEM_ERROR.message());
				return "info/membership_error";
			}
		}

		if (OmniUtil.isOffline(channel)) {
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(incsNo);
			Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null) {
				
				model.addAttribute("name", customer.getCustNm());
				model.addAttribute("intguserid", customer.getChcsNo());

				if (StringUtils.hasText(customer.getMbrJoinDt())) {
					model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
				}
			} else {
				if (certResult != null && StringUtils.hasText(certResult.getName())) {
					model.addAttribute("name", certResult.getName());
				}
				if (certResult != null && StringUtils.hasText(certResult.getId())) {
					model.addAttribute("intguserid", certResult.getId());
				}
			}

			// addinfo Y로 넘어오면 바로 POS 화면 전환
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;
				if (offlineParam != null) {
					String addinfo = offlineParam.getAddInfo();
					if ("Y".equals(addinfo)) {
						return "join/join_finish_off_redirect";
					}
				}
			}
			
			
			// return "join/join_finish_off102";
			return "join/join_finish_off";

		} else {
			String profile = this.systemInfo.getActiveProfiles()[0];
			profile = StringUtils.isEmpty(profile) ? "dev" : profile;
			profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
			
			model.addAttribute("isConfirmBtn", this.config.isConfirmBtn(chcd, profile));
			
			if(!StringUtils.isEmpty(WebUtil.getSession("joinAditor"))) {
				return "redirect:/join/move-on";
			}
			return "join/join_finish";
		}

	}

	@RequestMapping(value = "/complete", method = { RequestMethod.GET, RequestMethod.POST })
	public String joinComplete(final Model model, final HttpServletResponse servletResponse, final Locale locale) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		String incsNo = WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION);
		model.addAttribute("incsno", incsNo);
		model.addAttribute("incsNo", incsNo);
		CertResult certResult = this.commonService.getCertResult(incsNo);

		if (certResult != null) {

			model.addAttribute("category", certResult.getCategory());
			model.addAttribute("name", certResult.getName());
			/*
			 * String name = certResult.getName(); final String username = OmniUtil.maskUserName(name, locale); model.addAttribute("name", username);
			 * 
			 * StringBuilder nameid = new StringBuilder(username); String id = certResult.getId(); if (StringUtils.hasText(id)) { final String userid =
			 * OmniUtil.maskUserId(id); nameid.append("(").append(userid).append(")"); } model.addAttribute("nameid", nameid.toString());
			 */
		}

		String intguserid = WebUtil.getStringSession("intguserid");

		if (StringUtils.hasText(intguserid)) {
			model.addAttribute("intguserid", intguserid);
		} else {
			String id = certResult.getId();
			if (StringUtils.hasText(id)) {
				model.addAttribute("intguserid", id);
			}
		}

		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));

		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setIncsNo(incsNo);
		Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
		if (customer != null) {
			if (StringUtils.hasText(customer.getMbrJoinDt())) {
				model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
			}
		}
		
		final String isMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_MEMBERSHIP);
		boolean isMembership = OmniUtil.isMembership(isMembershipSession);
		if(isMembership) {
			MembershipUserInfo membershipUserInfo = (MembershipUserInfo) WebUtil.getSession(OmniConstants.MEMBERSHIP_USERINFO);
			if(StringUtils.isEmpty(membershipUserInfo.getId()) || StringUtils.isEmpty(membershipUserInfo.getIncsNo()) || StringUtils.isEmpty(membershipUserInfo.getName())) {
				if(StringUtils.isEmpty(membershipUserInfo.getId())) {
					if (StringUtils.hasText(intguserid)) {
						membershipUserInfo.setId(intguserid);
					} else {
						String id = certResult.getId();
						if (StringUtils.hasText(id)) {
							membershipUserInfo.setId(id);
						}
					}
				}
				
				if(StringUtils.isEmpty(membershipUserInfo.getIncsNo())) {
					membershipUserInfo.setIncsNo(customer.getIncsNo());
				}
				
				if(StringUtils.isEmpty(membershipUserInfo.getName())) {
					String name = certResult.getName();
					if (StringUtils.hasText(name)) {
						membershipUserInfo.setName(name);
					}
				}
				
				WebUtil.setSession(OmniConstants.MEMBERSHIP_USERINFO, membershipUserInfo);
			}
			
			try {
				if (certResult != null) {
					String name = certResult.getName();
					model.addAttribute("name", OmniUtil.maskUserName(name, locale));
					String loginid = certResult.getId();
					if (StringUtils.hasText(loginid)) {
						model.addAttribute("loginid", OmniUtil.maskUserId(loginid));
					}
				}
				
				return "membership/membership_join_finish";	
			} catch (Exception e) {
				log.error("message : {}", e.getMessage());
				model.addAttribute("message", ResultCode.SYSTEM_ERROR.message());
				return "info/membership_error";
			}
		}
		if(!StringUtils.isEmpty(WebUtil.getSession("joinAditor"))) {
			return "redirect:/join/move-on";
		}
		return "join/join_finish_bp";
	}

	@RequestMapping(value = "/terms/apply", method = { RequestMethod.GET, RequestMethod.POST })
	public String joinTermsAgreeApply(JoinApplyRequest joinApplyRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model, final Locale locale, final HttpServletRequest request) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		String incsNo = "0";
		
		Map<String,String> gaCookieMap = gataggingUtils.getGaCookieMap(request);	//ga tagging get cookie 
		
		if (StringUtils.hasText(joinApplyRequest.getIncsno())) {
			incsNo = joinApplyRequest.getIncsno();
			
			if (StringUtils.hasText(joinApplyRequest.getXincsno()) && !incsNo.equals(SecurityUtil.getXValue(joinApplyRequest.getXincsno(), false))) { // 고객통합 번호 위변조 체크 2021-08-12 hjw0228
				log.error("▶▶▶▶▶ [terms apply] 고객통합번호 불일치, incsNo : {}, xincsNo : {}", incsNo, SecurityUtil.getXValue(joinApplyRequest.getXincsno(), false));
				
				try {	

					log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(/terms/apply 고객통합번호 불일치) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID),
							gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), WebUtil.getSession(OmniGaTaggingConstants.EL), WebUtil.getSession(OmniGaTaggingConstants.CD21),
							WebUtil.getSession(OmniGaTaggingConstants.CD22));

					if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

						GaTagData gaTagDto = GaTagData.builder()
								.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
								.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
								.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
								.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
								.chCd((String) WebUtil.getSession(OmniGaTaggingConstants.CD22))
								.chCdNm((String) WebUtil.getSession(OmniGaTaggingConstants.CD21))
								.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
								.joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								.errorMessage("고객통합에러").sendFlag("Y").incsNo(incsNo)
								.sessionId(request.getSession().getId())
								.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
								.build();

						gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);
					}
				} catch (Exception ex) {
					log.error(ex.getMessage());
				}	
				return "redirect:/info?error=fail.message";
			} else if (StringUtils.hasText(WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION)) && !"0".equals(WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION)) && !incsNo.equals(WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION))) { // 고객통합 번호 위변조 체크 2021-08-12 hjw0228
				log.error("▶▶▶▶▶ [terms apply] 고객통합번호 불일치, incsNo : {}, session incsNo : {}", incsNo, WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION));
				
				try {	
					
					log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(/terms/apply 고객통합번호 불일치) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
							,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),WebUtil.getSession(OmniGaTaggingConstants.CD21),WebUtil.getSession(OmniGaTaggingConstants.CD22));
				
					if(Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) 
							  && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String)WebUtil.getSession(OmniGaTaggingConstants.EL))
									  || OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String)WebUtil.getSession(OmniGaTaggingConstants.EL)) 
									  || OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String)WebUtil.getSession(OmniGaTaggingConstants.EL)))){		
						
				
					 GaTagData gaTagDto = GaTagData.builder()
							 .cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
	                         .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
	                         .el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
	                         .loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
	                         .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
	                         .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
	                         .chCd((String) WebUtil.getSession(OmniGaTaggingConstants.CD22))
	                         .chCdNm((String) WebUtil.getSession(OmniGaTaggingConstants.CD21))
	                         .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
	                         .joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
	                         .errorMessage("고객통합에러")
                             .sendFlag("Y")
                             .incsNo(incsNo)
                             .sessionId(request.getSession().getId())
                             .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]").build();
					 	   				
				      gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

					}
				} catch (Exception ex) {
					log.error(ex.getMessage());
				}	
				return "redirect:/info?error=fail.message";
			}
		} else {
			// sns 회원 가입시 매핑없는 경우 전달
			Map<String, Object> md = model.asMap();
			if (md != null) {
				Object obj = md.get("joinApplyRequest");
				if (obj != null) {
					joinApplyRequest = (JoinApplyRequest) obj;
					incsNo = joinApplyRequest.getIncsno();
				}
			}
		}

		// sns 매핑상태에서 약관 미동의시 전달
		Object snsObj = WebUtil.getSession(OmniConstants.SNSPARAM);
		if (snsObj != null) {
			SnsParam snsParam = (SnsParam) snsObj;

			log.debug("▶▶▶▶▶▶ [join terms apply] sns param : {}", StringUtil.printJson(snsParam));

			joinApplyRequest.setUnm(SecurityUtil.setXyzValue(snsParam.getUserName()));
			joinApplyRequest.setUid(SecurityUtil.setXyzValue(snsParam.getLoginId()));
		}

		log.debug("▶▶▶▶▶▶ [join terms apply] request param : {}", StringUtil.printJson(joinApplyRequest));
		String name = SecurityUtil.getXValue(joinApplyRequest.getUnm(), false);

		log.debug("▶▶▶▶▶▶ [join terms apply] name : {}", name);

		String paramuid = SecurityUtil.getXValue(joinApplyRequest.getUid(), false);

		CertResult certResult = this.commonService.getCertResult(incsNo);

		if (certResult == null) {
			
			try {

				log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(/terms/apply 인증을 위한 정보가 미존재) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID),
						gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), WebUtil.getSession(OmniGaTaggingConstants.EL), WebUtil.getSession(OmniGaTaggingConstants.CD21),
						WebUtil.getSession(OmniGaTaggingConstants.CD22));

				if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
						|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
						|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

					GaTagData gaTagDto = GaTagData.builder()
							.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
							.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
							.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
							.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
							.chCd((String) WebUtil.getSession(OmniGaTaggingConstants.CD22))
							.chCdNm((String) WebUtil.getSession(OmniGaTaggingConstants.CD21))
							.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
							.joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.errorMessage("인증세션만료")
							.sendFlag("Y")
							.incsNo(incsNo).sessionId(request.getSession().getId())
							.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
							.build();

					gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

				}
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}
			throw new OmniException("인증을 위한 정보가 존재하지 않습니다.");
		}
		model.addAttribute("category", certResult.getCategory());
		
		final String username = OmniUtil.maskUserName(name, locale);
		final String userid = OmniUtil.maskUserId(paramuid);
		StringBuilder nameid = new StringBuilder(username);
		nameid.append("(").append(userid).append(")");
		model.addAttribute("nameid", nameid.toString());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		// 약관동의 처리 : 수정프로세스
		JoinRequest joinRequest = new JoinRequest();
		if (StringUtils.hasText(paramuid)) {
			joinRequest.setLoginid(paramuid);
		}
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setIncsno(joinApplyRequest.getIncsno());
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		joinRequest.setCorpTerms(StringUtil.isTrue(joinApplyRequest.getCorpterms()));

		joinRequest.setUnm(certResult.getName());
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());

		joinRequest.setAgreeType("CH");
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());
				Types types = WebUtil.getTypes(offlineParam.getDt(), offlineParam.getOp());
				if (types != null) {
					joinRequest.setDeviceType(types.getDeviceType());
					joinRequest.setOsType(types.getOsType());
				}
				model.addAttribute("incsNo", incsNo);
				model.addAttribute("incsno", incsNo);
				model.addAttribute("home", OmniUtil.getOfflineRedirectUrl(channel));
				model.addAttribute("homeurl", OmniUtil.getOfflineRedirectUrl(channel));

				// 이니스프리 추가 파라미터
				model.addAttribute("chnCd", offlineParam.getChnCd());
				model.addAttribute("storeCd", offlineParam.getStoreCd());
				model.addAttribute("storenm", offlineParam.getStorenm());
				model.addAttribute("user_id", offlineParam.getUser_id());
				
				// 이크리스 추가 파라미터
				model.addAttribute("joinEmpId", offlineParam.getJoinEmpId());
			}
		}
		List<String> bpterms = joinApplyRequest.getBpterms();
		List<String> bptcatcds = joinApplyRequest.getBpTcatCds();
		List<String> bptncvnos = joinApplyRequest.getBpTncvNos();
		List<String> terms = joinApplyRequest.getTerms();
		List<String> tcatcds = joinApplyRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = joinApplyRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = joinApplyRequest.getMarketing();
		List<String> marketingChcds = joinApplyRequest.getMarketingChcd();

		String marketingsarr[] = OmniUtil.getListToArray(marketings);
		String marketingchcdsarr[] = OmniUtil.getListToArray(marketingChcds);

		// 수신동의
		List<Marketing> joinMarketings = new ArrayList<>();
		if (marketings != null && !marketings.isEmpty()) {
			Marketing agree = null;
			for (int i = 0; i < marketingsarr.length; i++) {
				agree = new Marketing();
				agree.setChCd(marketingchcdsarr[i]); // chgChCd
				agree.setSmsAgree(marketingsarr[i].equals("on") ? "Y" : "N");
				joinMarketings.add(agree);
			}
		}
		joinRequest.setMarketings(joinMarketings);

		// 뷰포 약관동의
		List<Terms> joinBpTerms = new ArrayList<>();
		if (bpterms != null && !bpterms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < bptermsarr.length; i++) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) { // nullpointer
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn(bptermsarr[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(bptcatcdsarrs[i]);
				term.setTncvNo(bptncvnosarrs[i]);
				term.setChgChCd(chCd);
				joinBpTerms.add(term);
			}
		}
		joinRequest.setBpterms(joinBpTerms);
        
		
		// 경로약관동의
		List<Terms> joinTerms = new ArrayList<>();
		if (terms != null && !terms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < termarrs.length; i++) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn(termarrs[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(tcatcdarrs[i]);
				term.setTncvNo(tncvnosarrs[i]);
				term.setChgChCd(chCd);
				joinTerms.add(term);
			}
		}

		// 통합약관도 등록
		TermsVo termsVo = new TermsVo();
		if (OmniUtil.isOffline(channel)) {
			termsVo.setChCd(ChannelPairs.getOnlineCd(channel.getChCd()));
		} else {
			termsVo.setChCd(channel.getChCd());
		}
		List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
		if (corpTerms != null && !corpTerms.isEmpty()) {
			Terms term = null;
			for (TermsVo corpterm : corpTerms) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn("Y");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(corpterm.getTcatCd());
				term.setTncvNo(corpterm.getTncvNo());
				term.setChgChCd(corpterm.getChCd());
				joinTerms.add(term);
			}
		}

		joinRequest.setTerms(joinTerms);

		BaseResponse response = null;

		if (OmniUtil.isOffline(channel)) {
			response = apiOfflineProcessStep.applyOfflineCustomerTermsProcess(joinRequest);
		} else {
			response = apiOnlineTermsProcessStep.applyTermsProcess(joinRequest);
		}

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {
			WebUtil.setSession(OmniConstants.LOGIN_MOBILE_TYPE, joinApplyRequest.getMlogin()); // 모바일 타입 : 세션에 넣을때는 암호화해서 전달
			final String mlogin = SecurityUtil.getXValue(joinApplyRequest.getMlogin(), false);
			model.addAttribute(OmniConstants.LOGIN_MOBILE_TYPE, mlogin); // 모바일 타입 : 화면에는 복호화된 상태로 전달

			// SNS 맵핑 체크
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(paramuid);
				snsParam.setIncsNo(incsNo);

				snsAuth.doSnsMapping(snsParam);
			}
			
			
			if (OmniUtil.isOffline(channel)) {

				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setIncsNo(incsNo);
				Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
				if (customer != null) {

					if (StringUtils.hasText(joinRequest.getUnm())) {
						model.addAttribute("name", joinRequest.getUnm());
					} else {
						model.addAttribute("name", customer.getCustNm());
					}

					if (StringUtils.hasText(joinRequest.getLoginid())) {
						model.addAttribute("intguserid", joinRequest.getLoginid());
					} else {
						model.addAttribute("intguserid", customer.getChcsNo());
					}

					if (StringUtils.hasText(customer.getMbrJoinDt())) {
						model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
					}
				}

				// addinfo Y로 넘어오면 바로 POS 화면 전환
				Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
				if (obj != null) {
					OfflineParam offlineParam = (OfflineParam) obj;
					if (offlineParam != null) {
						String addinfo = offlineParam.getAddInfo();
						if ("Y".equals(addinfo)) {
							return "join/apply_finish_off_redirect";
						}
					}
				}

				if (StringUtils.hasText(joinRequest.getLoginid())) {
					return "join/apply_finish_off";
				}

				return "join/apply_finish_off102";
			} else {
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				
				model.addAttribute("isConfirmBtn", this.config.isConfirmBtn(chcd, profile));
				
				return "join/apply_finish";
			}
		} else {
			
			if (OmniUtil.isOffline(channel)) {
				
				try {	
					
					 log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(/terms/apply 고객통합 에러) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
								,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),WebUtil.getSession(OmniGaTaggingConstants.EL),channel.getChCdNm(),channel.getChCd());
					
					 if(Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) 
							  && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String)WebUtil.getSession(OmniGaTaggingConstants.EL))
									  || OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String)WebUtil.getSession(OmniGaTaggingConstants.EL)) 
									  || OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String)WebUtil.getSession(OmniGaTaggingConstants.EL)))){	
						 
					 GaTagData gaTagDto = GaTagData.builder()
                             .cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
							   .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
							   .el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							   .loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							   .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
							   .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
							   .chCd(channel.getChCd())
							   .chCdNm(channel.getChCdNm())
							   .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
							   .joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							   .errorMessage("고객통합 에러")
                               .sendFlag("Y")
                               .incsNo(incsNo)
                               .sessionId(request.getSession().getId())
                               .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]").build();
	   				
				      gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);
				      
					 }
				} catch (Exception ex) {
					log.error(ex.getMessage());
				}	
				
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				model.addAttribute("offline", true);
				Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
				if (obj != null) {
					OfflineParam offlineParam = (OfflineParam) obj;
					model.addAttribute("chCd", chCd);
					model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("chnCd", offlineParam.getChnCd());
					model.addAttribute("storeCd", offlineParam.getStoreCd());
					model.addAttribute("storenm", offlineParam.getStorenm());
					model.addAttribute("user_id", offlineParam.getUser_id());
				} else {
					return "redirect:" + OmniUtil.getRedirectOfflineInitUrl(channel, profile);
				}
				if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
					model.addAttribute("channelWtdt", response.getMessage());
					return "info/channel_withdraw";
				}
				model.addAttribute("message", response.getMessage());
				model.addAttribute("errormsg", response.getMessage());
				model.addAttribute("errorcode", response.getResultCode());
				return "info/api_page";
			} else {
				model.addAttribute("offline", false);
				model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
				model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
				model.addAttribute("channelName", channel.getChCdNm());
				if ("ICITSVCOM004".equals(response.getResultCode())) { // 존재하는 고객
					return "redirect:/info-exist"; // 존재하는 고객이면 알림 주고 로그인으로 이동
				} else if ("ICITSVCOM008".equals(response.getResultCode())) { // 망취소
					model.addAttribute("errormsg", response.getMessage());
					model.addAttribute("errorcode", response.getResultCode());
					
					try {

						log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(/terms/apply 망취소) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID),
								gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

						if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

							GaTagData gaTagDto = GaTagData.builder()
									.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
									.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
									.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
									.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
									.chCd(channel.getChCd())
									.chCdNm(channel.getChCdNm())
									.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
									.joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.errorMessage("고객통합에러")
									.sendFlag("Y")
									.incsNo(incsNo)
									.sessionId(request.getSession().getId())
									.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
									.build();

							gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

						}
					} catch (Exception ex) {
						log.error(ex.getMessage());
					}	
					
					
					return "info/api_error";
				} else if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
					model.addAttribute("channelWtdt", response.getMessage());
					
					try {

						log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(/terms/apply 망취소) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID),
								gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

						if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

							GaTagData gaTagDto = GaTagData.builder()
									.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
									.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
									.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
									.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
									.chCd(channel.getChCd())
									.chCdNm(channel.getChCdNm())
									.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
									.joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.errorMessage("고객통합에러")
									.sendFlag("Y")
									.incsNo(incsNo)
									.sessionId(request.getSession().getId())
									.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
									.build();

							gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);
						}
					} catch (Exception ex) {
						log.error(ex.getMessage());
					}
					
					
					return "info/channel_withdraw";
				} else {
					model.addAttribute("errormsg", response.getMessage());
					model.addAttribute("errorcode", response.getResultCode());
					
					try {
						log.debug("▶▶▶▶▶▶  GA Tagging JOIN FAIL(/terms/apply 망취소) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID),
								gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

						if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

							GaTagData gaTagDto = GaTagData.builder()
									.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
									.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
									.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
									.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
									.chCd(channel.getChCd())
									.chCdNm(channel.getChCdNm())
									.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
									.joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.errorMessage("고객통합에러")
									.sendFlag("Y")
									.incsNo(incsNo)
									.sessionId(request.getSession().getId()).sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
									.build();

							gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);
						}
					} catch (Exception ex) {
						log.error(ex.getMessage());
					}	
					
					return "info/api_error";
				}
			}

		}
	}

	@RequestMapping(value = "/terms/applycorp", method = { RequestMethod.GET, RequestMethod.POST })
	public String joinTermsAgreeApplyCorp(JoinApplyRequest joinApplyRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model, final Locale locale,
			final HttpServletRequest request) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		log.debug("▶▶▶▶▶▶ [join terms applycorp] request param : {}", StringUtil.printJson(joinApplyRequest));
		String incsNo = "0";
		if (StringUtils.hasText(joinApplyRequest.getIncsno())) {
			incsNo = joinApplyRequest.getIncsno();
		} else {
			// sns 회원 가입시 매핑없는 경우 전달
			Map<String, Object> md = model.asMap();
			if (md != null) {
				Object obj = md.get("joinApplyRequest");
				joinApplyRequest = (JoinApplyRequest) obj;
				incsNo = joinApplyRequest.getIncsno();
			}
		}

		// sns 매핑상태에서 약관 미동의시 전달
		Object snsObj = WebUtil.getSession(OmniConstants.SNSPARAM);
		if (snsObj != null) {
			SnsParam snsParam = (SnsParam) snsObj;

			log.debug("▶▶▶▶▶▶ [join terms applycorp] sns param : {}", StringUtil.printJson(snsParam));

			joinApplyRequest.setUnm(SecurityUtil.setXyzValue(snsParam.getUserName()));
			joinApplyRequest.setUid(SecurityUtil.setXyzValue(snsParam.getLoginId()));
		}

		log.debug("▶▶▶▶▶▶ [join terms applycorp] request param : {}", StringUtil.printJson(joinApplyRequest));
		String name = SecurityUtil.getXValue(joinApplyRequest.getUnm(), false);

		log.debug("▶▶▶▶▶▶ [join terms applycorp] name : {}", name);

		String paramuid = SecurityUtil.getXValue(joinApplyRequest.getUid(), false);

		CertResult certResult = this.commonService.getCertResult(incsNo);

		if (certResult == null) {
			throw new OmniException("인증을 위한 정보가 존재하지 않습니다.");
		}
		model.addAttribute("category", certResult.getCategory());
		
		final String username = OmniUtil.maskUserName(name, locale);
		final String userid = OmniUtil.maskUserId(paramuid);
		StringBuilder nameid = new StringBuilder(username);
		nameid.append("(").append(userid).append(")");
		model.addAttribute("nameid", nameid.toString());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));

		// 약관동의 처리 : 수정프로세스
		JoinRequest joinRequest = new JoinRequest();
		if (StringUtils.hasText(paramuid)) {
			joinRequest.setLoginid(paramuid);
		}
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setIncsno(joinApplyRequest.getIncsno());
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		joinRequest.setCorpTerms(StringUtil.isTrue(joinApplyRequest.getCorpterms()));

		joinRequest.setUnm(certResult.getName());
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());

		joinRequest.setAgreeType("CORP");// 전사약관동의
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;
				log.debug("▶▶▶▶▶▶ [join terms applycorp]  offline request : {}", StringUtil.printJson(offlineParam));
				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());

			}
		}
		List<String> bpterms = joinApplyRequest.getBpterms();
		List<String> bptcatcds = joinApplyRequest.getBpTcatCds();
		List<String> bptncvnos = joinApplyRequest.getBpTncvNos();
		List<String> terms = joinApplyRequest.getTerms();
		List<String> tcatcds = joinApplyRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = joinApplyRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = joinApplyRequest.getMarketing();
		List<String> marketingChcds = joinApplyRequest.getMarketingChcd();

		String marketingsarr[] = OmniUtil.getListToArray(marketings);
		String marketingchcdsarr[] = OmniUtil.getListToArray(marketingChcds);

		// 수신동의
		List<Marketing> joinMarketings = new ArrayList<>();
		if (marketings != null && !marketings.isEmpty()) {
			Marketing agree = null;
			for (int i = 0; i < marketingsarr.length; i++) {
				agree = new Marketing();
				agree.setChCd(marketingchcdsarr[i]); // chgChCd
				agree.setSmsAgree(marketingsarr[i].equals("on") ? "Y" : "N");
				joinMarketings.add(agree);
			}
		}
		joinRequest.setMarketings(joinMarketings);

		// 뷰포 약관동의
		List<Terms> joinBpTerms = new ArrayList<>();
		if (bpterms != null && !bpterms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < bptermsarr.length; i++) {
				term = new Terms();
				term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				term.setTncAgrYn(bptermsarr[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(bptcatcdsarrs[i]);
				term.setTncvNo(bptncvnosarrs[i]);
				term.setChgChCd(chCd);
				joinBpTerms.add(term);
			}
		}
		joinRequest.setBpterms(joinBpTerms);

		// 경로약관동의
		List<Terms> joinTerms = new ArrayList<>();
		if (terms != null && !terms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < termarrs.length; i++) {
				term = new Terms();
				term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				term.setTncAgrYn(termarrs[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(tcatcdarrs[i]);
				term.setTncvNo(tncvnosarrs[i]);
				term.setChgChCd(chCd);
				joinTerms.add(term);
			}
		}

		// 통합약관도 등록
		TermsVo termsVo = new TermsVo();
		if (OmniUtil.isOffline(channel)) {
			termsVo.setChCd(ChannelPairs.getOnlineCd(channel.getChCd()));
		} else {
			termsVo.setChCd(channel.getChCd());
		}
		List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
		if (corpTerms != null && !corpTerms.isEmpty()) {
			Terms term = null;
			for (TermsVo corpterm : corpTerms) {
				term = new Terms();
				term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				term.setTncAgrYn("Y");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(corpterm.getTcatCd());
				term.setTncvNo(corpterm.getTncvNo());
				term.setChgChCd(corpterm.getChCd());
				joinTerms.add(term);
			}
		}

		joinRequest.setTerms(joinTerms);

		BaseResponse response = null;

		if (OmniUtil.isOffline(channel)) {
			response = apiOfflineProcessStep.applyOfflineCustomerTermsProcess(joinRequest);
		} else {
			response = apiOnlineTermsProcessStep.termsProcess(joinRequest);
		}

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {
			// 2020.08.07 채널 약관 동의 정상 완료일 경우 약관 동의 완료 안내(ME-FO-A0106)페이지 노출

			WebUtil.setSession(OmniConstants.LOGIN_MOBILE_TYPE, joinApplyRequest.getMlogin()); // 모바일 타입 : 세션에 넣을때는 암호화해서 전달
			final String mlogin = SecurityUtil.getXValue(joinApplyRequest.getMlogin(), false);
			model.addAttribute(OmniConstants.LOGIN_MOBILE_TYPE, mlogin); // 모바일 타입 : 화면에는 복호화된 상태로 전달

			// 전사 신규 약관 동의 시는 바로 로그인

			final SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
			final String encpw = WebUtil.getStringSession(OmniConstants.XPW_SESSION);
			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(name));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(paramuid));
			WebUtil.setSession(OmniConstants.XPW_SESSION, encpw);
			WebUtil.setSession(OmniConstants.SESSION_DATA_KEY_SESSION, ssoParam.getSessionDataKey());

			// SNS 맵핑 체크
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(paramuid);
				snsParam.setIncsNo(incsNo);

				snsAuth.doSnsMapping(snsParam);
			}

			return "redirect:/moveauth";
		} else {
			model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("channelName", channel.getChCdNm());
			if ("ICITSVCOM004".equals(response.getResultCode())) { // 존재하는 고객
				log.debug("▶▶▶▶▶▶ [conversion terms-finish-202-ch] customer : {}", "존재하는 고객");
				return "redirect:/info-exist"; // 존재하는 고객이면 알림 주고 로그인으로 이동
			} else if ("ICITSVCOM008".equals(response.getResultCode())) { // 망취소
				model.addAttribute("errormsg", response.getMessage());
				model.addAttribute("errorcode", response.getResultCode());
				return "info/api_error";
			} else if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
				model.addAttribute("channelWtdt", response.getMessage());
				return "info/channel_withdraw";
			} else {
				model.addAttribute("errormsg", response.getMessage());
				model.addAttribute("errorcode", response.getResultCode());
				return "info/api_error";
			}

		}
	}

	@RequestMapping(value = "/terms/offlineidregist", method = { RequestMethod.GET, RequestMethod.POST })
	public String joinOfflineIdRegist(JoinApplyRequest joinApplyRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model, final Locale locale, final HttpServletRequest request) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		log.debug("▶▶▶▶▶▶ [join terms apply] request param : {}", StringUtil.printJson(joinApplyRequest));
		String incsNo = "0";
		if (StringUtils.hasText(joinApplyRequest.getIncsno())) {
			incsNo = joinApplyRequest.getIncsno();
		} else {
			// sns 회원 가입시 매핑없는 경우 전달
			Map<String, Object> md = model.asMap();
			if (md != null) {
				Object obj = md.get("joinApplyRequest");
				joinApplyRequest = (JoinApplyRequest) obj;
				incsNo = joinApplyRequest.getIncsno();
			}
		}

		log.debug("▶▶▶▶▶▶ [join terms offline apply] request param : {}", StringUtil.printJson(joinApplyRequest));
		String name = SecurityUtil.getXValue(joinApplyRequest.getUnm(), false);

		log.debug("▶▶▶▶▶▶ [join terms offline apply] name : {}", name);

		String paramuid = StringUtils.hasText(SecurityUtil.getXValue(joinApplyRequest.getUid(), false)) ? SecurityUtil.getXValue(joinApplyRequest.getUid(), false).replaceAll(" ", "").toLowerCase() : ""; // 2023-07-12 loginId 공백 제거 및 소문자 처리

		CertResult certResult = this.commonService.getCertResult(incsNo);

		if (certResult == null) {
			throw new OmniException("인증을 위한 정보가 존재하지 않습니다.");
		}
		model.addAttribute("category", certResult.getCategory());
		
		final String username = OmniUtil.maskUserName(name, locale);
		final String userid = OmniUtil.maskUserId(paramuid);
		StringBuilder nameid = new StringBuilder(username);
		nameid.append("(").append(userid).append(")");
		model.addAttribute("nameid", nameid.toString());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("offline", OmniUtil.isOffline(channel));

		Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
		if (obj != null) {
			model.addAttribute("home", OmniUtil.getOfflineRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getOfflineRedirectUrl(channel));
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getOfflineRedirectUrl(channel));
		}

		model.addAttribute("incsno", incsNo);
		model.addAttribute("incsNo", incsNo);

		// 약관동의 처리
		JoinRequest joinRequest = new JoinRequest();
		if (StringUtils.hasText(paramuid)) {
			joinRequest.setLoginid(paramuid);
		}
		if (StringUtils.isEmpty(certResult.getName())) {
			String custname = this.customerApiService.getApiCustomerName(chcd, incsNo, null, SecurityUtil.getXValue(joinApplyRequest.getUnm(), false));
			joinRequest.setUnm(custname);
		} else {
			joinRequest.setUnm(certResult.getName());
		}
		joinRequest.setLoginid(SecurityUtil.getXValue(joinApplyRequest.getUid(), false));
		joinRequest.setLoginpassword(SecurityUtil.getXValue(joinApplyRequest.getUpw(), false));
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setIncsno(joinApplyRequest.getIncsno());
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		joinRequest.setCorpTerms(StringUtil.isTrue(joinApplyRequest.getCorpterms()));

		joinRequest.setUnm(certResult.getName());
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setIntegrateid(joinApplyRequest.getIntegrateid());
		joinRequest.setAgreeType("CH");

		model.addAttribute("intguserid", joinRequest.getLoginid());
		WebUtil.setSession("intguserid", joinRequest.getLoginid());

		if (obj != null) {
			OfflineParam offlineParam = (OfflineParam) obj;
			log.debug("▶▶▶▶▶▶ [join terms offline apply]  offline request : {}", StringUtil.printJson(offlineParam));
			joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
			joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
			joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
			joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
			joinRequest.setAddInfo(offlineParam.getAddInfo());
			log.debug("offline param : {}", StringUtil.printJson(offlineParam));
			// 이니스프리 추가 파라미터
			model.addAttribute("chnCd", offlineParam.getChnCd());
			model.addAttribute("storeCd", offlineParam.getStoreCd());
			model.addAttribute("storenm", offlineParam.getStorenm());
			model.addAttribute("user_id", offlineParam.getUser_id());
			
			// 이크리스 추가 파라미터
			model.addAttribute("joinEmpId", offlineParam.getJoinEmpId());
		}

		List<String> bpterms = joinApplyRequest.getBpterms();
		List<String> bptcatcds = joinApplyRequest.getBpTcatCds();
		List<String> bptncvnos = joinApplyRequest.getBpTncvNos();
		List<String> terms = joinApplyRequest.getTerms();
		List<String> tcatcds = joinApplyRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = joinApplyRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = joinApplyRequest.getMarketing();
		List<String> marketingChcds = joinApplyRequest.getMarketingChcd();

		String marketingsarr[] = OmniUtil.getListToArray(marketings);
		String marketingchcdsarr[] = OmniUtil.getListToArray(marketingChcds);

		// 수신동의
		List<Marketing> joinMarketings = new ArrayList<>();
		if (marketings != null && !marketings.isEmpty()) {
			Marketing agree = null;
			for (int i = 0; i < marketingsarr.length; i++) {
				agree = new Marketing();
				agree.setChCd(marketingchcdsarr[i]); // chgChCd
				agree.setSmsAgree(marketingsarr[i].equals("on") ? "Y" : "N");
				joinMarketings.add(agree);
			}
		}
		joinRequest.setMarketings(joinMarketings);

		// 뷰포 약관동의
		List<Terms> joinBpTerms = new ArrayList<>();
		if (bpterms != null && !bpterms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < bptermsarr.length; i++) {
				term = new Terms();
				term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				term.setTncAgrYn(bptermsarr[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(bptcatcdsarrs[i]);
				term.setTncvNo(bptncvnosarrs[i]);
				term.setChgChCd(chCd);
				joinBpTerms.add(term);
			}
		}
		joinRequest.setBpterms(joinBpTerms);

		// 경로약관동의
		List<Terms> joinTerms = new ArrayList<>();
		if (terms != null && !terms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < termarrs.length; i++) {
				term = new Terms();
				term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				term.setTncAgrYn(termarrs[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(tcatcdarrs[i]);
				term.setTncvNo(tncvnosarrs[i]);
				term.setChgChCd(chCd);
				joinTerms.add(term);
			}
		}

		// 통합약관도 등록
		TermsVo termsVo = new TermsVo();
		if (OmniUtil.isOffline(channel)) {
			termsVo.setChCd(ChannelPairs.getOnlineCd(channel.getChCd()));
		} else {
			termsVo.setChCd(channel.getChCd());
		}
		List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
		if (corpTerms != null && !corpTerms.isEmpty()) {
			Terms term = null;
			for (TermsVo corpterm : corpTerms) {
				term = new Terms();
				term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				term.setTncAgrYn("Y");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(corpterm.getTcatCd());
				term.setTncvNo(corpterm.getTncvNo());
				term.setChgChCd(corpterm.getChCd());
				joinTerms.add(term);
			}
		}

		joinRequest.setTerms(joinTerms);

		BaseResponse response = apiOfflineProcessStep.applyOfflineBpCustomerTermsProcess(joinRequest); // .applyOfflineCustomerTermsProcess(joinRequest);

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(incsNo);
			Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null) {

				if (StringUtils.hasText(joinRequest.getUnm())) {
					model.addAttribute("name", joinRequest.getUnm());
				} else {
					model.addAttribute("name", customer.getCustNm());
				}

				if (StringUtils.hasText(joinRequest.getLoginid())) {
					model.addAttribute("intguserid", joinRequest.getLoginid());
				} else {
					model.addAttribute("intguserid", customer.getChcsNo());
				}

				if (StringUtils.hasText(customer.getMbrJoinDt())) {
					model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
				}
			}
		
			
			//회원가입 ga tagging 중복 처리 방지용 저장된 값이 없으면 실행
			if(Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY)) && !Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_START_CHECK_KEY))){
				 
				/* ####################### ga tagging 회원가입 완료처리(경로가입 처리) ###################
				 * O X X 
				 */
				try {

					Map<String, String> gaCookieMap = gataggingUtils.getGaCookieMap(request);

					log.debug("▶▶▶▶▶▶ GA Tagging JOIN SUCCESS(/terms/offlineidregist) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID),
							gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

					log.debug("▶▶▶▶▶▶  GA Tagging JOIN SUCCESS(/terms/offlineidregist) : [통합회원가입:{},경로회원가입:{}]", WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL),
							WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL));

					GaTagData gaTagDto = GaTagData.builder()
							.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
							.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
							.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
							.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
							.chCd(channel.getChCd())
							.chCdNm(channel.getChCdNm())
							.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS)
							.joinType(OmniGaTaggingConstants.GA_SIGNUP_CASE_INTERGRATED)
							.totalJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL))
							.channelJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL)).sendFlag("Y")
							.incsNo(incsNo)
							.sessionId(request.getSession().getId()).sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
							.build();

					gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

					WebUtil.setSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY, OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS);

				} catch (Exception ex) {
					log.error(ex.getMessage());
				}
			}
			// addinfo Y로 넘어오면 바로 POS 화면 전환
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;
				if (offlineParam != null) {
					String addinfo = offlineParam.getAddInfo();
					if ("Y".equals(addinfo)) {
						return "join/apply_finish_off_redirect";
					}
				}
			}

			return "join/apply_finish_off_complete";
		} else {
			if (obj != null) {
				model.addAttribute("offline", true);
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				OfflineParam offlineParam = (OfflineParam) obj;
				model.addAttribute("chCd", chCd);
				model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
				model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
				model.addAttribute("chnCd", offlineParam.getChnCd());
				model.addAttribute("storeCd", offlineParam.getStoreCd());
				model.addAttribute("storenm", offlineParam.getStorenm());
				model.addAttribute("user_id", offlineParam.getUser_id());
			} else {
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				return "redirect:" + OmniUtil.getRedirectOfflineInitUrl(channel, profile);
			}
			if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
				model.addAttribute("channelWtdt", response.getMessage());
				return "info/channel_withdraw";
			}
			model.addAttribute("message", response.getMessage());
			model.addAttribute("errormsg", response.getMessage());
			model.addAttribute("errorcode", response.getResultCode());
			return "info/api_page";
		}
	}

	@RequestMapping(value = "/terms/offlinebp", method = { RequestMethod.GET, RequestMethod.POST })
	public String joinOfflineTermsBpAgreeApply(JoinApplyRequest joinApplyRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model, final Locale locale,
			final HttpServletRequest request) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		log.debug("▶▶▶▶▶▶ [join terms apply] request param : {}", StringUtil.printJson(joinApplyRequest));
		String incsNo = "0";
		if (StringUtils.hasText(joinApplyRequest.getIncsno())) {
			incsNo = SecurityUtil.getXValue(joinApplyRequest.getIncsno(), false);
			if (StringUtils.hasText(incsNo)) {
				joinApplyRequest.setIncsno(incsNo);
			}
		} else {
			// sns 회원 가입시 매핑없는 경우 전달
			Map<String, Object> md = model.asMap();
			if (md != null) {
				Object obj = md.get("joinApplyRequest");
				joinApplyRequest = (JoinApplyRequest) obj;
				incsNo = joinApplyRequest.getIncsno();
			}
		}

		log.debug("▶▶▶▶▶▶ [join terms offline apply] request param : {}", StringUtil.printJson(joinApplyRequest));
		String name = SecurityUtil.getXValue(joinApplyRequest.getUnm(), false);

		log.debug("▶▶▶▶▶▶ [join terms offline apply] name : {}", name);

		String paramuid = StringUtils.hasText(SecurityUtil.getXValue(joinApplyRequest.getUid(), false)) ? SecurityUtil.getXValue(joinApplyRequest.getUid(), false).replaceAll(" ", "").toLowerCase() : ""; // 2023-07-12 loginId 공백 제거 및 소문자 처리
		String parampwd = SecurityUtil.getXValue(joinApplyRequest.getUpw(), false);

		CertResult certResult = this.commonService.getCertResult(incsNo);

		if (certResult == null) {
			throw new OmniException("인증을 위한 정보가 존재하지 않습니다.");
		}
		model.addAttribute("category", certResult.getCategory());
		
		final String username = OmniUtil.maskUserName(name, locale);
		final String userid = OmniUtil.maskUserId(paramuid);
		StringBuilder nameid = new StringBuilder(username);
		nameid.append("(").append(userid).append(")");
		model.addAttribute("nameid", nameid.toString());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());

		model.addAttribute("offline", OmniUtil.isOffline(channel));

		Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
		if (obj != null) {
			OfflineParam offlineParam = (OfflineParam) obj;
			model.addAttribute("home", OmniUtil.getOfflineRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getOfflineRedirectUrl(channel));
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getOfflineRedirectUrl(channel));
			// 이니스프리 추가 파라미터
			model.addAttribute("chnCd", offlineParam.getChnCd());
			model.addAttribute("storeCd", offlineParam.getStoreCd());
			model.addAttribute("storenm", offlineParam.getStorenm());
			model.addAttribute("user_id", offlineParam.getUser_id());

		} else {
			model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		}

		model.addAttribute("incsno", incsNo);
		model.addAttribute("incsNo", incsNo);

		// 약관동의 처리
		JoinRequest joinRequest = new JoinRequest();

		if (StringUtils.hasText(paramuid)) {
			joinRequest.setLoginid(paramuid);
		}

		if (StringUtils.hasText(parampwd)) {
			joinRequest.setLoginpassword(parampwd);
		}
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setIncsno(joinApplyRequest.getIncsno());
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		joinRequest.setCorpTerms(StringUtil.isTrue(joinApplyRequest.getCorpterms()));

		joinRequest.setUnm(certResult.getName());
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());

		joinRequest.setAgreeType("CH");
		joinRequest.setIntegrateid(joinApplyRequest.getIntegrateid());
		if (obj != null) {
			OfflineParam offlineParam = (OfflineParam) obj;
			log.debug("▶▶▶▶▶▶ [join terms offline apply]  offline request : {}", StringUtil.printJson(offlineParam));
			joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
			joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
			joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
			joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
			joinRequest.setAddInfo(offlineParam.getAddInfo());
		}
		List<String> bpterms = joinApplyRequest.getBpterms();
		List<String> bptcatcds = joinApplyRequest.getBpTcatCds();
		List<String> bptncvnos = joinApplyRequest.getBpTncvNos();
		List<String> terms = joinApplyRequest.getTerms();
		List<String> tcatcds = joinApplyRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = joinApplyRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = joinApplyRequest.getMarketing();
		List<String> marketingChcds = joinApplyRequest.getMarketingChcd();

		String marketingsarr[] = OmniUtil.getListToArray(marketings);
		String marketingchcdsarr[] = OmniUtil.getListToArray(marketingChcds);

		// 수신동의
		List<Marketing> joinMarketings = new ArrayList<>();
		if (marketings != null && !marketings.isEmpty()) {
			Marketing agree = null;
			for (int i = 0; i < marketingsarr.length; i++) {
				agree = new Marketing();
				agree.setChCd(marketingchcdsarr[i]); // chgChCd
				agree.setSmsAgree(marketingsarr[i].equals("on") ? "Y" : "N");
				joinMarketings.add(agree);
			}
		}
		joinRequest.setMarketings(joinMarketings);

		// 뷰포 약관동의
		List<Terms> joinBpTerms = new ArrayList<>();
		if (bpterms != null && !bpterms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < bptermsarr.length; i++) {
				term = new Terms();
				term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				term.setTncAgrYn(bptermsarr[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(bptcatcdsarrs[i]);
				term.setTncvNo(bptncvnosarrs[i]);
				term.setChgChCd(chCd);
				joinBpTerms.add(term);
			}
		}
		joinRequest.setBpterms(joinBpTerms);

		// 경로약관동의
		List<Terms> joinTerms = new ArrayList<>();
		if (terms != null && !terms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < termarrs.length; i++) {
				term = new Terms();
				term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				term.setTncAgrYn(termarrs[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(tcatcdarrs[i]);
				term.setTncvNo(tncvnosarrs[i]);
				term.setChgChCd(chCd);
				joinTerms.add(term);
			}
		}

		// 통합약관도 등록
		TermsVo termsVo = new TermsVo();
		if (OmniUtil.isOffline(channel)) {
			termsVo.setChCd(ChannelPairs.getOnlineCd(channel.getChCd()));
		} else {
			termsVo.setChCd(channel.getChCd());
		}
		List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
		if (corpTerms != null && !corpTerms.isEmpty()) {
			Terms term = null;
			for (TermsVo corpterm : corpTerms) {
				term = new Terms();
				term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				term.setTncAgrYn("Y");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(corpterm.getTcatCd());
				term.setTncvNo(corpterm.getTncvNo());
				term.setChgChCd(corpterm.getChCd());
				joinTerms.add(term);
			}
		}

		joinRequest.setTerms(joinTerms);

		String loginid = joinRequest.getLoginid();

		BaseResponse response = apiOfflineProcessStep.registOfflineBpCustomerProcess(joinRequest);

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(incsNo);
			Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null) {

				if (StringUtils.hasText(joinRequest.getUnm())) {
					model.addAttribute("name", joinRequest.getUnm());
				} else {
					model.addAttribute("name", customer.getCustNm());
				}

				if (StringUtils.hasText(loginid)) {
					model.addAttribute("intguserid", loginid);
				} else {
					model.addAttribute("intguserid", customer.getChcsNo());
				}

				if (StringUtils.hasText(customer.getMbrJoinDt())) {
					model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
				}
			}
		
			//회원가입 ga tagging 중복 처리 방지용 저장된 값이 없으면 실행
			if(Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY)) && !Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_START_CHECK_KEY))){
			 
				/* ####################### ga tagging 회원가입 완료처리(경로가입 처리) ###################
				 * O X X 
				 */
				try {

					Map<String, String> gaCookieMap = gataggingUtils.getGaCookieMap(request);

					log.debug("▶▶▶▶▶▶ GA Tagging JOIN SUCCESS(/offlinebp) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID),
							gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

					log.debug("▶▶▶▶▶▶  GA Tagging JOIN SUCCESS(/offlinebp) : [통합회원가입:{},경로회원가입:{}]", WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL),
							WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL));

					GaTagData gaTagDto = GaTagData.builder()
							.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
							.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
							.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
							.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
							.chCd(channel.getChCd())
							.chCdNm(channel.getChCdNm())
							.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS)
							.joinType(OmniGaTaggingConstants.GA_SIGNUP_CASE_INTERGRATED)
							.totalJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL))
							.channelJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL))
							.sendFlag("Y")
							.incsNo(incsNo)
							.sessionId(request.getSession().getId())
							.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
							.build();

					gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

					WebUtil.setSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY, OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS);

				} catch (Exception ex) {
					log.error(ex.getMessage());
				}
			}
			// addinfo Y로 넘어오면 바로 POS 화면 전환
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;
				if (offlineParam != null) {
					String addinfo = offlineParam.getAddInfo();
					if ("Y".equals(addinfo)) {
						return "join/apply_finish_off_redirect";
					}
				}
			}

			if (StringUtils.hasText(loginid)) {
				return "join/apply_finish_off";
			}

			return "join/apply_finish_off102";

		} else {
			if (obj != null) {
				model.addAttribute("offline", true);
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				OfflineParam offlineParam = (OfflineParam) obj;
				model.addAttribute("chCd", chCd);
				model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
				model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
				model.addAttribute("chnCd", offlineParam.getChnCd());
				model.addAttribute("storeCd", offlineParam.getStoreCd());
				model.addAttribute("storenm", offlineParam.getStorenm());
				model.addAttribute("user_id", offlineParam.getUser_id());
			} else {
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				return "redirect:" + OmniUtil.getRedirectOfflineInitUrl(channel, profile);
			}
			if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
				model.addAttribute("channelWtdt", response.getMessage());
				return "info/channel_withdraw";
			}
			model.addAttribute("message", response.getMessage());
			model.addAttribute("errormsg", response.getMessage());
			model.addAttribute("errorcode", response.getResultCode());
			return "info/api_page";
		}
	}

	@RequestMapping(value = "/terms/offline", method = { RequestMethod.GET, RequestMethod.POST })
	public String joinOfflineTermsAgreeApply(JoinApplyRequest joinApplyRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model, final Locale locale,
			final HttpServletRequest request) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		log.debug("▶▶▶▶▶▶ [join terms apply] request param : {}", StringUtil.printJson(joinApplyRequest));
		String incsNo = "0";
		if (StringUtils.hasText(joinApplyRequest.getIncsno())) {
			incsNo = joinApplyRequest.getIncsno();
		} else {
			// sns 회원 가입시 매핑없는 경우 전달
			Map<String, Object> md = model.asMap();
			if (md != null) {
				Object obj = md.get("joinApplyRequest");
				joinApplyRequest = (JoinApplyRequest) obj;
				incsNo = joinApplyRequest.getIncsno();
			}
		}
		
		if(StringUtils.isEmpty(incsNo) || "0".equals(incsNo)) {
			log.info("session data key must have value!!!");
			model.addAttribute("oauthErrorCode", "9999");
			model.addAttribute("oauthErrorMsg", "session data key must have value.");
			model.addAttribute("message", "세션이 만료되었습니다.재접속 해주세요!");

			return "wso2/oauth2_error";
		}

		log.debug("▶▶▶▶▶▶ [join terms offline apply] request param : {}", StringUtil.printJson(joinApplyRequest));
		String name = SecurityUtil.getXValue(joinApplyRequest.getUnm(), false);

		log.debug("▶▶▶▶▶▶ [join terms offline apply] name : {}", name);

		String paramuid = SecurityUtil.getXValue(joinApplyRequest.getUid(), false);

		CertResult certResult = this.commonService.getCertResult(incsNo);

		if (certResult == null) {
			throw new OmniException("인증을 위한 정보가 존재하지 않습니다.");
		}
		model.addAttribute("category", certResult.getCategory());
		
		final String username = OmniUtil.maskUserName(name, locale);
		final String userid = OmniUtil.maskUserId(paramuid);
		StringBuilder nameid = new StringBuilder(username);
		nameid.append("(").append(userid).append(")");
		model.addAttribute("nameid", nameid.toString());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("offline", OmniUtil.isOffline(channel));

		Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
		if (obj != null) {
			model.addAttribute("home", OmniUtil.getOfflineRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getOfflineRedirectUrl(channel));
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getOfflineRedirectUrl(channel));
		}

		model.addAttribute("incsno", incsNo);
		model.addAttribute("incsNo", incsNo);

		// 약관동의 처리
		JoinRequest joinRequest = new JoinRequest();
		if (StringUtils.hasText(paramuid)) {
			joinRequest.setLoginid(paramuid);
		}
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setIncsno(joinApplyRequest.getIncsno());
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		joinRequest.setCorpTerms(StringUtil.isTrue(joinApplyRequest.getCorpterms()));

		joinRequest.setUnm(certResult.getName());
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());

		joinRequest.setAgreeType("CH");
		joinRequest.setIntegrateid(joinApplyRequest.getIntegrateid());
		if (obj != null) {
			OfflineParam offlineParam = (OfflineParam) obj;
			log.debug("▶▶▶▶▶▶ [join terms offline apply]  offline request : {}", StringUtil.printJson(offlineParam));
			joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
			joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
			joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
			joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
			joinRequest.setAddInfo(offlineParam.getAddInfo());

			// 이니스프리 추가 파라미터
			model.addAttribute("chnCd", offlineParam.getChnCd());
			model.addAttribute("storeCd", offlineParam.getStoreCd());
			model.addAttribute("storenm", offlineParam.getStorenm());
			model.addAttribute("user_id", offlineParam.getUser_id());

		}
		List<String> bpterms = joinApplyRequest.getBpterms();
		List<String> bptcatcds = joinApplyRequest.getBpTcatCds();
		List<String> bptncvnos = joinApplyRequest.getBpTncvNos();
		List<String> terms = joinApplyRequest.getTerms();
		List<String> tcatcds = joinApplyRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = joinApplyRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = joinApplyRequest.getMarketing();
		List<String> marketingChcds = joinApplyRequest.getMarketingChcd();

		String marketingsarr[] = OmniUtil.getListToArray(marketings);
		String marketingchcdsarr[] = OmniUtil.getListToArray(marketingChcds);

		// 수신동의
		List<Marketing> joinMarketings = new ArrayList<>();
		if (marketings != null && !marketings.isEmpty()) {
			Marketing agree = null;
			for (int i = 0; i < marketingsarr.length; i++) {
				agree = new Marketing();
				agree.setChCd(marketingchcdsarr[i]); // chgChCd
				agree.setSmsAgree(marketingsarr[i].equals("on") ? "Y" : "N");
				joinMarketings.add(agree);
			}
		}
		joinRequest.setMarketings(joinMarketings);

		// 뷰포 약관동의
		List<Terms> joinBpTerms = new ArrayList<>();
		if (bpterms != null && !bpterms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < bptermsarr.length; i++) {
				term = new Terms();
				term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				term.setTncAgrYn(bptermsarr[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(bptcatcdsarrs[i]);
				term.setTncvNo(bptncvnosarrs[i]);
				term.setChgChCd(chCd);
				joinBpTerms.add(term);
			}
		}
		joinRequest.setBpterms(joinBpTerms);

		// 경로약관동의
		List<Terms> joinTerms = new ArrayList<>();
		if (terms != null && !terms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < termarrs.length; i++) {
				term = new Terms();
				term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				term.setTncAgrYn(termarrs[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(tcatcdarrs[i]);
				term.setTncvNo(tncvnosarrs[i]);
				term.setChgChCd(chCd);
				joinTerms.add(term);
			}
		}

		// 통합약관도 등록
		TermsVo termsVo = new TermsVo();
		if (OmniUtil.isOffline(channel)) {
			termsVo.setChCd(ChannelPairs.getOnlineCd(channel.getChCd()));
		} else {
			termsVo.setChCd(channel.getChCd());
		}
		List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
		if (corpTerms != null && !corpTerms.isEmpty()) {
			Terms term = null;
			for (TermsVo corpterm : corpTerms) {
				term = new Terms();
				term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				term.setTncAgrYn("Y");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(corpterm.getTcatCd());
				term.setTncvNo(corpterm.getTncvNo());
				term.setChgChCd(corpterm.getChCd());
				joinTerms.add(term);
			}
		}

		joinRequest.setTerms(joinTerms);

		final String loginid = joinRequest.getLoginid();

		// BaseResponse response = apiOfflineProcessStep.applyOfflineCustomerTermsProcess(joinRequest);
		BaseResponse response = null;
		if(OmniConstants.OSULLOC_OFFLINE_CHCD.equals(chCd) || OmniConstants.OSULLOC_DEPARTMENT_CHCD.equals(chCd)) {
			if(!StringUtils.isEmpty(loginid)) {
				joinRequest.setIntegrateid("true");

			}
			response = apiOfflineProcessStep.registOfflineOsullocCustomerProcess(joinRequest);
			
			// 약관동의 처리
			if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {
				if (joinRequest.getTerms() != null && !joinRequest.getTerms().isEmpty() && !StringUtils.isEmpty(loginid)) {
					for (Terms term : joinTerms) {
						final String onlineChCd = ChannelPairs.getOnlineCd(joinRequest.getChcd());
						term.setChgChCd(onlineChCd);
						term.setIncsNo(Integer.parseInt(joinRequest.getIncsno())); 
						term.setTncAgrYn(term.getTncAgrYn().equals("Y") ? "A" : "D");
						term.setTncaChgDt(DateUtil.getCurrentDate());
						if (termsService.existTerms(term)) {
							termsService.mergeTerms(term);
							termsService.insertTermHist(term);
						}
					}
				}
			}
		} else {
			response = apiOfflineProcessStep.applyOfflineCustomerTermsProcess(joinRequest);
		}

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(incsNo);
			Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null) {

				if (StringUtils.hasText(joinRequest.getUnm())) {
					model.addAttribute("name", joinRequest.getUnm());
				} else {
					model.addAttribute("name", customer.getCustNm());
				}

				if (StringUtils.hasText(loginid)) {
					model.addAttribute("intguserid", loginid);
				} else {
					model.addAttribute("intguserid", customer.getChcsNo());
				}

				if (StringUtils.hasText(customer.getMbrJoinDt())) {
					model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
				}
			}
			
			//회원가입 ga tagging 중복 처리 방지용 저장된 값이 없으면 실행
			if(Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY)) && !Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_START_CHECK_KEY))){
				
				/* ####################### ga tagging 회원가입 완료처리(기가입 처리) ###################
				 * O O O
				 */
				try {

					Map<String, String> gaCookieMap = gataggingUtils.getGaCookieMap(request);

					log.debug("▶▶▶▶▶▶ GA Tagging JOIN SUCCESS(/terms/offline) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID),
							gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

					log.debug("▶▶▶▶▶▶  GA Tagging JOIN SUCCESS(/terms/offline) : [통합회원가입:{},경로회원가입:{}]", WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL),
							WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL));

					GaTagData gaTagDto = GaTagData.builder()
							.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
							.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
							.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
							.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
							.chCd(channel.getChCd())
							.chCdNm(channel.getChCdNm())
							.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS)
							.joinType(OmniGaTaggingConstants.GA_SIGNUP_CASE_MEMBER)
							.totalJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL))
							.channelJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL))
							.sendFlag("Y")
							.incsNo(incsNo)
							.sessionId(request.getSession().getId())
							.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
							.build();

					gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

					WebUtil.setSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY, OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS);

				} catch (Exception ex) {
					log.error(ex.getMessage());
				}
			}
			// addinfo Y로 넘어오면 바로 POS 화면 전환
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;
				if (offlineParam != null) {
					String addinfo = offlineParam.getAddInfo();
					if ("Y".equals(addinfo)) { // 바로 이동하도록
						return "join/apply_finish_off_redirect";
					}
				}
			}

			if (StringUtils.hasText(loginid)) {
				// return "join/apply_finish_off";
			}

			return "join/apply_finish_off102";
		} else {
			if (obj != null) {
				model.addAttribute("offline", true);
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				OfflineParam offlineParam = (OfflineParam) obj;
				model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
				model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
				model.addAttribute(OmniConstants.RD_URL, OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
				model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
				model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
				model.addAttribute("chnCd", offlineParam.getChnCd());
				model.addAttribute("storeCd", offlineParam.getStoreCd());
				model.addAttribute("storenm", offlineParam.getStorenm());
				model.addAttribute("user_id", offlineParam.getUser_id());
			} else {
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				return "redirect:" + OmniUtil.getRedirectOfflineInitUrl(channel, profile);
			}
			if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
				model.addAttribute("channelWtdt", response.getMessage());
				return "info/channel_withdraw";
			}
			model.addAttribute("message", response.getMessage());
			model.addAttribute("errormsg", response.getMessage());
			model.addAttribute("errorcode", response.getResultCode());
			return "info/api_page";
		}
	}

	// A0102 통합회원 가입 완료
	// A0106 채널약관동의 완료
	@RequestMapping(value = "/finish/{type}", method = { RequestMethod.GET, RequestMethod.POST })
	public String joinFinish(JoinApplyRequest joinApplyRequest, //
			@RequestParam(required = false) final String uid, //
			@RequestParam(required = false) final String unm, //
			@PathVariable("type") final String type, //
			final HttpServletResponse servletResponse, //
			final HttpServletRequest request, //
			final HttpSession session, //
			final Model model, //
			final Locale locale) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);
				
		model.addAttribute("rv", this.config.resourceVersion());

		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);

		log.debug("▶▶▶▶▶▶ [join finish {}] request param : {}", type, StringUtil.printJson(joinApplyRequest));
		
		boolean chatbotlogin = WebUtil.getSession(OmniConstants.CHATBOT_LOGIN_SESSION) == null ? false : (boolean) WebUtil.getSession(OmniConstants.CHATBOT_LOGIN_SESSION);

		String incsNo = "0";
		if (StringUtils.hasText(joinApplyRequest.getIncsno())) {
			incsNo = joinApplyRequest.getIncsno();
			if (StringUtils.hasText(joinApplyRequest.getXincsno()) && !incsNo.equals(SecurityUtil.getXValue(joinApplyRequest.getXincsno(), false))) { // 고객통합 번호 위변조 체크 2021-08-12 hjw0228
				log.error("▶▶▶▶▶ [finish terms] 고객통합번호 불일치, incsNo : {}, xincsNo : {}", incsNo, SecurityUtil.getXValue(joinApplyRequest.getXincsno(), false));				
				return "redirect:/info?error=fail.message";
			} else if (StringUtils.hasText(WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION)) && !"0".equals(WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION)) && !incsNo.equals(WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION))) { // 고객통합 번호 위변조 체크 2021-08-12 hjw0228
				log.error("▶▶▶▶▶ [finish terms] 고객통합번호 불일치, incsNo : {}, session incsNo : {}", incsNo, WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION));
				return "redirect:/info?error=fail.message";
			}
		} else {
			// sns 회원 가입시 전달
			incsNo = SecurityUtil.getXValue(WebUtil.getStringSession(OmniConstants.XINCS_NO_SESSION), false);
		}
		
		final Map<String,String> gaCookieMap = gataggingUtils.getGaCookieMap(request);	//ga get cookie

		// 전사약관 동의시 처리 부분 추가
		if ("applycorp_ch".equals(type) || "apply_ch".equals(type)) { // 채널에서 직접 약관동의 처리할 경우

			WebUtil.setSession("chcsWebId", null); // 채널 직접 약관 동의는 O O X 이므로 채널은 신규

			Object obj = WebUtil.getSession(OmniConstants.CHANNELPARAM);
			if (obj != null) {
				ChannelParam channelParam = (ChannelParam) obj;
				log.debug("▶▶▶▶▶▶ [join finish {}] channel param : {}", type, StringUtil.printJson(channelParam));
				chCd = channelParam.getChCd();
				WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
				incsNo = channelParam.getIncsNo();
				if(StringUtils.isEmpty(incsNo) || "0".equals(incsNo)) incsNo = SecurityUtil.getXValue(WebUtil.getStringSession(OmniConstants.XINCS_NO_SESSION), false);
				// 허용된 경로 리다이렉션URL 여부 체크
				final Channel channel = this.commonService.getChannel(chCd);
				if (StringUtils.hasText(channelParam.getRedirectUri()) && !OmniUtil.redirectUriValidationCheck(channel, channelParam.getRedirectUri())) {
					log.error("▶▶▶▶▶ [channel terms] channel cd : {}, type : {}, redirectUri invalidate : {}", chCd, type, channelParam.getRedirectUri());
					return "redirect:/info?error=channel.redirecturi.unknown";
				} else if (!StringUtils.hasText(channelParam.getRedirectUri()) && !OmniUtil.redirectUriValidationCheck(channel, OmniUtil.getRedirectUrl(channel))) { // 허용된 경로 리다이렉션URL 여부 체크
					log.error("▶▶▶▶▶ [channel terms] channel cd : {}, type : {}, redirectUri invalidate : {}", chCd, type, OmniUtil.getRedirectUrl(channel));
					return "redirect:/info?error=channel.redirecturi.unknown";
				}
			}
		}

		model.addAttribute("incsno", incsNo);
		model.addAttribute("incsNo", incsNo);
		WebUtil.setSession(OmniConstants.INCS_NO_SESSION, incsNo);
		// sns 매핑상태에서 약관 미동의시 전달
		Object snsObj = WebUtil.getSession(OmniConstants.SNSPARAM);
		if (snsObj != null) {
			SnsParam snsParam = (SnsParam) snsObj;

			log.debug("▶▶▶▶▶▶ [join finish {}] sns param : {}", type, StringUtil.printJson(snsParam));

			joinApplyRequest.setUnm(SecurityUtil.setXyzValue(snsParam.getUserName()));
			joinApplyRequest.setUid(SecurityUtil.setXyzValue(snsParam.getLoginId()));
		}

		log.debug("▶▶▶▶▶▶ [join finish {}] request param : {}", type, StringUtil.printJson(joinApplyRequest));

		String name = "";
		if (StringUtils.isEmpty(unm)) {
			name = SecurityUtil.getXValue(joinApplyRequest.getUnm(), false);
		} else {
			name = SecurityUtil.getXValue(unm, false);
		}
		if (StringUtils.isEmpty(name)) { // 그래도 빈값이면 세션에서 한번 더 확인
			name = SecurityUtil.getXValue(WebUtil.getStringSession(OmniConstants.XNM_SESSION), false);
		}

		log.debug("▶▶▶▶▶▶ [join finish {}] name : {}", type, name);

		String paramuid = "";
		if (StringUtils.isEmpty(uid)) {
			paramuid = SecurityUtil.getXValue(joinApplyRequest.getUid(), false);
		} else {
			paramuid = SecurityUtil.getXValue(uid, false);
		}
		if (StringUtils.isEmpty(paramuid)) { // 그래도 빈값이면 세션에서 한번 더 확인
			paramuid = SecurityUtil.getXValue(WebUtil.getStringSession(OmniConstants.XID_SESSION), false);
		}

		log.debug("▶▶▶▶▶▶ [join finish {}] id : {}", type, paramuid);

		CertResult certResult = null;
		if (WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION) != null) {
			certResult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
			if (certResult != null) {
				name = certResult.getName();
				model.addAttribute("category", certResult.getCategory());
				WebUtil.setSession(OmniConstants.USERNM_SESSION, name);
			}
		} else {
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(incsNo);
			Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null) {

				certResult = new CertResult();
				certResult.setName(customer.getCustNm());
				name = certResult.getName();
				WebUtil.setSession(OmniConstants.USERNM_SESSION, name);
				certResult.setGender(customer.getSxclCd());
				certResult.setGenderCode(customer.getSxclCd());
				certResult.setPhone(StringUtil.mergeMobile(customer));
				certResult.setBirth(customer.getAthtDtbr());
				certResult.setCiNo(customer.getCiNo());
				certResult.setForeigner(customer.getFrclCd());
				
				if(StringUtils.hasText(customer.getMbrJoinDt())) {
					model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
				}
			}
		}

		final String username = OmniUtil.maskUserName(name, locale);
		final String userid = OmniUtil.maskUserId(paramuid);
		StringBuilder nameid = new StringBuilder(username);
		nameid.append("(").append(userid).append(")");
		model.addAttribute("nameid", nameid.toString());

		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute("offline", OmniUtil.isOffline(channel));

		if (OmniUtil.isOffline(channel)) {

			model.addAttribute("incsNo", incsNo);
			model.addAttribute("incsno", incsNo);
			model.addAttribute("home", OmniUtil.getOfflineRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getOfflineRedirectUrl(channel));
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getOfflineRedirectUrl(channel));
			
			model.addAttribute("name", name);
			model.addAttribute("intguserid", paramuid);

			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;
				log.debug("offline param : {}", StringUtil.printJson(offlineParam));
				model.addAttribute("chnCd", offlineParam.getChnCd());
				model.addAttribute("storeCd", offlineParam.getStoreCd());
				model.addAttribute("storenm", offlineParam.getStorenm());
				model.addAttribute("user_id", offlineParam.getUser_id());
			}
			
			return "join/join_finish_off";
		} else {
			if(chatbotlogin) { // 챗봇을 통한 회원 가입 일 경우 returnUrl로 리다이렉트
				Object chatbotParam = WebUtil.getSession(OmniConstants.CHATBOTPARAM);
				model.addAttribute("home", ((ChatbotParam) chatbotParam).getReturnUrl());
				model.addAttribute("homeurl", ((ChatbotParam) chatbotParam).getReturnUrl());
				model.addAttribute(OmniConstants.RD_URL, ((ChatbotParam) chatbotParam).getReturnUrl());
			} else {
				model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
				model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
				model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));	
			}
		}

 	
		if ("join".equals(type)) {
			model.addAttribute("category", "SNS");
						
			log.debug("▶▶▶▶▶▶   GA Tagging JOIN SESSION  GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY :{} ]",WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY));
			
			//회원가입 ga tagging 중복 처리 방지용 저장된 값이 없으면 실행 id,nam이 있는경우만
			if ((!"".equals(paramuid) && !"".equals(name)) && Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY)) && !Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_START_CHECK_KEY))) {

				if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
						|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
						|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

					// ####################### ga tagging 회원가입 완료처리 ###################
					try {

						log.debug("▶▶▶▶▶▶  GA Tagging JOIN SUCCESS(/finish/{}) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", type, gaCookieMap.get(OmniGaTaggingConstants.CID),
								gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

						log.debug("▶▶▶▶▶▶  GA Tagging JOIN SUCCESS(/finish/{}) : [통합회원가입:{},경로회원가입:{}]", type, WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL),
								WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL));

						GaTagData gaTagDto = GaTagData.builder()
								.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
								.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
								.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP)).
								ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
								.chCd(channel.getChCd())
								.chCdNm(channel.getChCdNm())
								.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS)
								.joinType(OmniGaTaggingConstants.GA_SIGNUP_CASE_INTERGRATED)
								.totalJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL))
								.channelJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL))
								.sendFlag("Y")
								.incsNo(incsNo)
								.sessionId(request.getSession().getId())
								.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
								.build();

						gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

						WebUtil.setSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY, OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS);

					} catch (Exception ex) {
						log.error(ex.getMessage());
					}
				}
			}
			
			// 뷰티 멤버십 연동을 통한 회원가입 시 멤버십 연동 완료 화면으로 이동
			final String isMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_MEMBERSHIP);
			boolean isMembership = OmniUtil.isMembership(isMembershipSession);
			if(isMembership) {
				try {
					MembershipUserInfo membershipUserInfo = (MembershipUserInfo) WebUtil.getSession(OmniConstants.MEMBERSHIP_USERINFO);
					if(StringUtils.isEmpty(membershipUserInfo.getId()) || StringUtils.isEmpty(membershipUserInfo.getIncsNo()) || StringUtils.isEmpty(membershipUserInfo.getName())) {
						if(StringUtils.isEmpty(membershipUserInfo.getId())) {
							String id = paramuid;
							if (StringUtils.hasText(id)) {
								membershipUserInfo.setId(id);
							}	
						}
						
						if(StringUtils.isEmpty(membershipUserInfo.getIncsNo())) {
							membershipUserInfo.setIncsNo(incsNo);
						}
						
						if(StringUtils.isEmpty(membershipUserInfo.getName())) {
							name = certResult.getName();
							if (StringUtils.hasText(name)) {
								membershipUserInfo.setName(name);
							}
						}
						
						WebUtil.setSession(OmniConstants.MEMBERSHIP_USERINFO, membershipUserInfo);
					}
					
					if (certResult != null) {
						name = certResult.getName();
						model.addAttribute("name", OmniUtil.maskUserName(name, locale));
						String loginid = paramuid;
						if (StringUtils.hasText(loginid)) {
							model.addAttribute("loginid", OmniUtil.maskUserId(loginid));
						}
					}
					
					return "membership/membership_join_finish";	
				} catch (Exception e) {
					log.error("message : {}", e.getMessage());
					model.addAttribute("message", ResultCode.SYSTEM_ERROR.message());
					return "info/membership_error";
				}
			}			
			
			String profile = this.systemInfo.getActiveProfiles()[0];
			profile = StringUtils.isEmpty(profile) ? "dev" : profile;
			profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
			
			model.addAttribute("isConfirmBtn", this.config.isConfirmBtn(channel.getChCd(), profile));

			if(!StringUtils.isEmpty(WebUtil.getSession("joinAditor"))) {
				return "redirect:/join/move-on";
			}
			
			return "join/join_finish";
		} else if ("apply".equals(type) //
				|| "apply_ch".equals(type) //
				|| "applycorp".equals(type) //
				|| "applycorp_ch".equals(type)) {

			log.debug("▶▶▶▶▶▶ [join finish apply] type : {}", type);
		
			
			// 약관동의 처리 : 수정프로세스
			JoinRequest joinRequest = new JoinRequest();
			if (StringUtils.hasText(paramuid)) {
				joinRequest.setLoginid(paramuid);
			}
			joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
			joinRequest.setIncsno(joinApplyRequest.getIncsno());
			joinRequest.setOffLine(OmniUtil.isOffline(channel));
			joinRequest.setCorpTerms(StringUtil.isTrue(joinApplyRequest.getCorpterms()));

			joinRequest.setUnm(certResult.getName());
			joinRequest.setGender(certResult.getGender());
			joinRequest.setPhone(certResult.getPhone());
			joinRequest.setBirth(certResult.getBirth());
			joinRequest.setCi(certResult.getCiNo());
			joinRequest.setNational(certResult.getForeigner());

			if (type.contains("applycorp")) { // 전사약관동의
				joinRequest.setAgreeType("CORP");
			} else {
				joinRequest.setAgreeType("CH");
			}
			if (OmniUtil.isOffline(channel)) {
				Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
				if (obj != null) {
					OfflineParam offlineParam = (OfflineParam) obj;

					log.debug("▶▶▶▶▶▶ [join finish {}]  offline request : {}", type, StringUtil.printJson(offlineParam));

					joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
					joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
					joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
					joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
					joinRequest.setAddInfo(offlineParam.getAddInfo());

					// 이니스프리 추가 파라미터
					model.addAttribute("chnCd", offlineParam.getChnCd());
					model.addAttribute("storeCd", offlineParam.getStoreCd());
					model.addAttribute("storenm", offlineParam.getStorenm());
					model.addAttribute("user_id", offlineParam.getUser_id());
					
					// 이크리스 추가 파라미터
					model.addAttribute("joinEmpId", offlineParam.getJoinEmpId());
				}
			}
			List<String> bpterms = joinApplyRequest.getBpterms();
			List<String> bptcatcds = joinApplyRequest.getBpTcatCds();
			List<String> bptncvnos = joinApplyRequest.getBpTncvNos();
			List<String> terms = joinApplyRequest.getTerms();
			List<String> tcatcds = joinApplyRequest.getTcatCds(); // 약관동의 내역 약관동의코드
			List<String> tncvnos = joinApplyRequest.getTncvNos(); // 약관동의 내역 약관번호

			String bptermsarr[] = OmniUtil.getListToArray(bpterms);
			String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
			String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

			String termarrs[] = OmniUtil.getListToArray(terms);
			String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
			String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

			List<String> marketings = joinApplyRequest.getMarketing();
			List<String> marketingChcds = joinApplyRequest.getMarketingChcd();

			String marketingsarr[] = OmniUtil.getListToArray(marketings);
			String marketingchcdsarr[] = OmniUtil.getListToArray(marketingChcds);

			// 수신동의
			List<Marketing> joinMarketings = new ArrayList<>();
			if (marketings != null && !marketings.isEmpty()) {
				Marketing agree = null;
				for (int i = 0; i < marketingsarr.length; i++) {
					agree = new Marketing();
					agree.setChCd(marketingchcdsarr[i]); // chgChCd
					agree.setSmsAgree(marketingsarr[i].equals("on") ? "Y" : "N");
					joinMarketings.add(agree);
				}
			}
			joinRequest.setMarketings(joinMarketings);

			// 뷰포 약관동의
			List<Terms> joinBpTerms = new ArrayList<>();
			if (bpterms != null && !bpterms.isEmpty()) {
				Terms term = null;
				for (int i = 0; i < bptermsarr.length; i++) {
					term = new Terms();
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
					term.setTncAgrYn(bptermsarr[i].equals("on") ? "Y" : "N");
					term.setTncaChgDt(DateUtil.getCurrentDate());
					term.setTcatCd(bptcatcdsarrs[i]);
					term.setTncvNo(bptncvnosarrs[i]);
					term.setChgChCd(chCd);
					joinBpTerms.add(term);
				}
			}
			joinRequest.setBpterms(joinBpTerms);

			// 경로약관동의
			List<Terms> joinTerms = new ArrayList<>();
			if (terms != null && !terms.isEmpty()) {
				Terms term = null;
				for (int i = 0; i < termarrs.length; i++) {
					term = new Terms();
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
					term.setTncAgrYn(termarrs[i].equals("on") ? "Y" : "N");
					term.setTncaChgDt(DateUtil.getCurrentDate());
					term.setTcatCd(tcatcdarrs[i]);
					term.setTncvNo(tncvnosarrs[i]);
					term.setChgChCd(chCd);
					joinTerms.add(term);
				}
			}

			// 통합약관도 등록
			TermsVo termsVo = new TermsVo();
			if (OmniUtil.isOffline(channel)) {
				termsVo.setChCd(ChannelPairs.getOnlineCd(channel.getChCd()));
			} else {
				termsVo.setChCd(channel.getChCd());
			}
			List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
			if (corpTerms != null && !corpTerms.isEmpty()) {
				Terms term = null;
				for (TermsVo corpterm : corpTerms) {
					term = new Terms();
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
					term.setTncAgrYn("Y");
					term.setTncaChgDt(DateUtil.getCurrentDate());
					term.setTcatCd(corpterm.getTcatCd());
					term.setTncvNo(corpterm.getTncvNo());
					term.setChgChCd(corpterm.getChCd());
					joinTerms.add(term);
				}
			}

			joinRequest.setTerms(joinTerms);

			BaseResponse response = null;

			if (OmniUtil.isOffline(channel)) {
				response = apiOfflineProcessStep.applyOfflineCustomerTermsProcess(joinRequest);
			} else {

				if (type.contains("applycorp")) { // 전사약관동의
					response = apiOnlineTermsProcessStep.termsProcess(joinRequest);
				} else {
					response = apiOnlineTermsProcessStep.applyCustomerTermsProcess(joinRequest);
				}

			}

			log.debug("▶▶▶▶▶▶ [join finish {}] terms prosess response : {}", type, StringUtil.printJson(response));

			 
			if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {
				
				//기가입 처리(네이버 회원가입 완료 때문에 주석처리) 2022.04.21				
				if ("apply".equals(type) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals(WebUtil.getSession(OmniGaTaggingConstants.EL))
						|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals(WebUtil.getSession(OmniGaTaggingConstants.EL))
						|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals(WebUtil.getSession(OmniGaTaggingConstants.EL)))) {
					// 회원가입 ga tagging 중복 처리 방지용 저장된 값이 없으면 실행
					if (Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY)) && !Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_START_CHECK_KEY))) {

						// ####################### ga tagging 회원가입 완료처리 ###################
						try {

							log.debug("▶▶▶▶▶▶  GA Tagging JOIN SUCCESS(/finish/apply) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID),
									gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

							log.debug("▶▶▶▶▶▶  GA Tagging JOIN SUCCESS(/finish/apply) : [통합회원가입:{},경로회원가입:{}]", WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL),
									WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL));

							GaTagData gaTagDto = GaTagData.builder().cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
									.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
									.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
									.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
									.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA)).chCd(channel.getChCd())
									.chCdNm(channel.getChCdNm())
									.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS)
									.joinType(OmniGaTaggingConstants.GA_SIGNUP_CASE_MEMBER)
									.totalJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL))
									.channelJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL))
									.sendFlag("Y")
									.incsNo(incsNo)
									.sessionId(request.getSession().getId())
									.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
									.build();

							gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

							WebUtil.setSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY, OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS);

						} catch (Exception ex) {
							log.error(ex.getMessage());
						}
					}
				}
				// 2020.08.07 채널 약관 동의 정상 완료일 경우 약관 동의 완료 안내(ME-FO-A0106)페이지 노출

				WebUtil.setSession(OmniConstants.LOGIN_MOBILE_TYPE, joinApplyRequest.getMlogin()); // 모바일 타입 : 세션에 넣을때는 암호화해서 전달
				final String mlogin = SecurityUtil.getXValue(joinApplyRequest.getMlogin(), false);
				model.addAttribute(OmniConstants.LOGIN_MOBILE_TYPE, mlogin); // 모바일 타입 : 화면에는 복호화된 상태로 전달

				// SNS 맵핑 체크
				if (!OmniUtil.isOffline(channel)) {
					SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
					if (snsParam == null) {
						snsParam = new SnsParam();
					}
					snsParam.setLoginId(paramuid);
					snsParam.setIncsNo(incsNo);

					snsAuth.doSnsMapping(snsParam);
				}

				// 약관 처리
				if ("applycorp_ch".equals(type) || "apply_ch".equals(type)) { // 채널에서 직접 약관동의 처리할 경우

					Object obj = WebUtil.getSession(OmniConstants.CHANNELPARAM);
					if (obj != null) {
						
						ChannelParam channelParam = (ChannelParam) obj;
						if (StringUtils.hasText(channelParam.getRedirectUri())) {
							log.debug("[channel param] applycorp type : {}, {}", type, channelParam.getRedirectUri());
							return "redirect:" + channelParam.getRedirectUri();
						} else {
							log.debug("[channel] applycorp type : {}, {}", type, OmniUtil.getRedirectUrl(channel));
							return "redirect:" + OmniUtil.getRedirectUrl(channel);
						}
					}

					log.debug("[channel param empty] applycorp type : {}, {}", type, OmniUtil.getRedirectUrl(channel));

					return "redirect:" + OmniUtil.getRedirectUrl(channel);
				} else if ("applycorp".equals(type)) { // 전사 신규 약관 동의 시는 바로 로그인

					final SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
					final String encpw = WebUtil.getStringSession(OmniConstants.XPW_SESSION);
					WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(name));
					WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(paramuid));
					WebUtil.setSession(OmniConstants.XPW_SESSION, encpw);
					WebUtil.setSession(OmniConstants.SESSION_DATA_KEY_SESSION, ssoParam.getSessionDataKey());

					return "redirect:/moveauth";
				} else {

					if (OmniUtil.isOffline(channel)) {

						CustInfoVo custInfoVo = new CustInfoVo();
						custInfoVo.setIncsNo(incsNo);
						Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
						if (customer != null) {

							if (StringUtils.hasText(joinRequest.getUnm())) {
								model.addAttribute("name", joinRequest.getUnm());
							} else {
								model.addAttribute("name", customer.getCustNm());
							}

							if (StringUtils.hasText(joinRequest.getLoginid())) {
								model.addAttribute("intguserid", joinRequest.getLoginid());
							} else {
								model.addAttribute("intguserid", customer.getChcsNo());
							}

							if (StringUtils.hasText(customer.getMbrJoinDt())) {
								model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
							}
						}

						Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
						if (obj != null) {
							OfflineParam offlineParam = (OfflineParam) obj;
							if (offlineParam != null) {
								String addinfo = offlineParam.getAddInfo();
								if ("Y".equals(addinfo)) { // 바로 이동하도록

									return "join/apply_finish_off_redirect";
								}
							}
						}

						if (StringUtils.hasText(joinRequest.getLoginid())) {
							
							
							return "join/apply_finish_off";
						}

						
						return "join/apply_finish_off102";

					} else {

						String loginuid = WebUtil.getStringSession(OmniConstants.XID_SESSION);
						String loginupw = WebUtil.getStringSession(OmniConstants.XPW_SESSION);

						if (StringUtils.isEmpty(loginuid) || StringUtils.isEmpty(loginupw)) {
							final SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
							model.addAttribute(OmniConstants.LOGIN_MOBILE_TYPE, "MOBILE");
							WebUtil.setSession(OmniConstants.XID_MSESSION, name);
							WebUtil.setSession(OmniConstants.XPW_MSESSION, incsNo);
							WebUtil.setSession(OmniConstants.SESSION_DATA_KEY_SESSION, ssoParam.getSessionDataKey());
						}

						String profile = this.systemInfo.getActiveProfiles()[0];
						profile = StringUtils.isEmpty(profile) ? "dev" : profile;
						profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
						
						model.addAttribute("isConfirmBtn", this.config.isConfirmBtn(channel.getChCd(), profile));
						
						return "join/apply_finish";
					}

				}

			} else {

				if (OmniUtil.isOffline(channel)) {
					model.addAttribute("offline", true);
					Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
					if (obj != null) {
						String profile = this.systemInfo.getActiveProfiles()[0];
						profile = StringUtils.isEmpty(profile) ? "dev" : profile;
						profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
						OfflineParam offlineParam = (OfflineParam) obj;
						model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
						model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
						model.addAttribute(OmniConstants.RD_URL, OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
						model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
						model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
						model.addAttribute("chnCd", offlineParam.getChnCd());
						model.addAttribute("storeCd", offlineParam.getStoreCd());
						model.addAttribute("storenm", offlineParam.getStorenm());
						model.addAttribute("user_id", offlineParam.getUser_id());
					} else {
						String profile = this.systemInfo.getActiveProfiles()[0];
						profile = StringUtils.isEmpty(profile) ? "dev" : profile;
						profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
						return "redirect:" + OmniUtil.getRedirectOfflineInitUrl(channel, profile);
					}
					if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
						model.addAttribute("channelWtdt", response.getMessage());
						return "info/channel_withdraw";
					}
					model.addAttribute("message", response.getMessage());
					model.addAttribute("errormsg", response.getMessage());
					model.addAttribute("errorcode", response.getResultCode());
					return "info/api_page";
				} else {
					model.addAttribute("offline", false);
					model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
					model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
					model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
					model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
					model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
					model.addAttribute("channelName", channel.getChCdNm());
					// 채널에서 직접 약관동의 처리할 경우
					if ("applycorp_ch".equals(type) || "apply_ch".equals(type)) { // 채널에서 직접 약관동의 처리할 경우
						Object obj = WebUtil.getSession(OmniConstants.CHANNELPARAM);
						if (obj != null) {
							ChannelParam channelParam = (ChannelParam) obj;
							final Channel channel2 = this.commonService.getChannel(channelParam.getChCd());
							model.addAttribute(OmniConstants.CH_CD, channel2.getChCd());
							model.addAttribute(OmniConstants.CH_NM, channel2.getChCdNm());
							model.addAttribute(OmniConstants.RD_URL, channelParam.getRedirectUri());
							model.addAttribute("home", channelParam.getRedirectUri());
							model.addAttribute("homeurl", channelParam.getRedirectUri());
						}
					}
					model.addAttribute("message", response.getMessage());
					model.addAttribute("errormsg", response.getMessage());
					model.addAttribute("errorcode", response.getResultCode());
					if ("ICITSVCOM004".equals(response.getResultCode())) { // 존재하는 고객
						return "redirect:/info-exist"; // 존재하는 고객이면 알림 주고 로그인으로 이동
					} else if ("ICITSVCOM008".equals(response.getResultCode())) { // 망취소
						model.addAttribute("errormsg", response.getMessage());
						model.addAttribute("errorcode", response.getResultCode());
						return "info/api_error";
					} else if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
						model.addAttribute("channelWtdt", response.getMessage());
						return "info/channel_withdraw";
					} else {
						model.addAttribute("errormsg", response.getMessage());
						model.addAttribute("errorcode", response.getResultCode());
						return "info/api_error";
					}
				}
			}
		} else {
			
						
			if (OmniUtil.isOffline(channel)) {

				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setIncsNo(incsNo);
				Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
				if (customer != null) {

					if (StringUtils.hasText(username)) {
						model.addAttribute("name", username);
					} else {
						model.addAttribute("name", customer.getCustNm());
					}

					if (StringUtils.hasText(paramuid)) {
						model.addAttribute("intguserid", paramuid);
					} else {
						model.addAttribute("intguserid", customer.getChcsNo());
					}

					if (StringUtils.hasText(customer.getMbrJoinDt())) {
						model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
					}
				}
				
				// addinfo Y로 넘어오면 바로 POS 화면 전환
				Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
				if (obj != null) {
					OfflineParam offlineParam = (OfflineParam) obj;
					if (offlineParam != null) {
						String addinfo = offlineParam.getAddInfo();
						if ("Y".equals(addinfo)) {
							
							return "join/join_finish_off_redirect";
						}
					}
				}

				
				return "join/join_finish_off";
			} else {
				
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				
				model.addAttribute("isConfirmBtn", this.config.isConfirmBtn(channel.getChCd(), profile));
				
				if(!StringUtils.isEmpty(WebUtil.getSession("joinAditor"))) {
					return "redirect:/join/move-on";
				}

				return "join/join_finish";
			}

		}
	}

	@GetMapping("/cancel/apply")
	public String cancelApply(final HttpSession session, final @RequestParam("to") String moveTo) {
		log.debug("▶▶▶▶▶▶ [cancel apply] move page url : {}", moveTo);
		if (!moveTo.startsWith("http")) {
			return WebUtil.getRedirectUrlWithContextPath("/errors");
		}
		return WebUtil.getRedirectUrlWithContextPath(moveTo);
	}

	// A0105 채널약관동의
	// A0105 채널약관동의 02
	@GetMapping("/apply")
	public String joinApply(final Model model, final Locale locale) {

		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);

		model.addAttribute("rv", this.config.resourceVersion());

		final Channel channel = this.commonService.getChannel(chCd);

		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("chcd", channel.getChCd());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("entry", OmniUtil.getOfflineParam());
		
		SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);

		TermsVo termsVo = new TermsVo();
		termsVo.setChCd(channel.getChCd());
		if(channel.getChCd().equals(OmniConstants.OSULLOC_CHCD)) {
			termsVo.setIncsNo(snsParam.getIncsNo());
		}
		List<TermsVo> corpsTermsList = this.termsService.getCorpTerms(termsVo);
		List<TermsVo> termsList = this.termsService.getTerms(termsVo);
		log.debug("▶▶▶▶▶▶ [join apply] termsList = {}", StringUtil.printJson(termsList));
		model.addAttribute("corpterms", corpsTermsList);
		model.addAttribute("terms", termsList);

		String name = OmniUtil.maskUserName(snsParam.getUserName(), locale);
		String id = OmniUtil.maskUserId(snsParam.getLoginId());
		String mobile = OmniUtil.maskMobile(snsParam.getPhone(), locale);

		model.addAttribute("incsno", snsParam.getIncsNo());
		model.addAttribute("incsNo", snsParam.getIncsNo());
		model.addAttribute("xincsno", SecurityUtil.setXyzValue(snsParam.getIncsNo()));
		model.addAttribute("name", name);
		model.addAttribute("id", id);
		model.addAttribute("mobile", mobile);
		model.addAttribute("joindate", snsParam.getJoinDate());
		
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
//			model.addAttribute("terms", termsList2);
			for(TermsVo vo : termsList2) {
				if(vo.getMkSn()==-20) {
					model.addAttribute("terms_marketing", vo);
				}
			}
		}

		return "join/apply_terms";
	}

	// A0700 SNS간편가입 ID/PW 등록
	@GetMapping("/regist")
	public String joinRegist(@RequestParam("to") String to, @RequestParam(required=false, value="category") String category, 
			@RequestParam(required=false, value="snsUseType") String snsUseType, final Model model,final HttpServletRequest request) {
				
		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		log.debug("▶▶▶▶▶▶ [joinRegist] channel : {}, to : {} , snsUseType : {}, category : {}", chCd, to, snsUseType, category);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("rv", this.config.resourceVersion());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, channel.getHmpgUrl());
		model.addAttribute("rv", this.config.resourceVersion());
		model.addAttribute("home", channel.getHmpgUrl());

		if("SNS".equals(category)) {
			model.addAttribute("category", "SNS");
		}
		
//		if ("usedSnsLogin".equals(snsUseType)) { // 카카오 로그인했는데, 회원 가입 안되어 있는 경우, 회원가입 통계 처리를 위한 GA 태깅 추가
//			log.debug("▶▶▶▶▶▶ [joinRegist]  usedSnsLogin");
//			model.addAttribute("snsUsedType", snsUseType);
//		}
		
//		
		model.addAttribute("joinTo", to);

		return "join/regist";
	}

	public boolean isAgreeTerms(List<String> allowedTermsList, String checkTerms) {

		for (String allowedTerms : allowedTermsList) {
			if (allowedTerms.equals(checkTerms)) {
				return true;
			}
		}

		return false;
	}

	@PostMapping("/regist")
	public String joinRegistAction(final SnsParam snsParam, final RedirectAttributes redirectAttributes, final Model model,final HttpServletRequest request) {

		
		StopWatch stopWatch = new StopWatch("회원가입(regist post action)");
		
		SnsParam authenticatedSnsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
		authenticatedSnsParam.setLoginId(snsParam.getNewId());
		authenticatedSnsParam.setPassword(snsParam.getPassword());
		authenticatedSnsParam.setChcd(snsParam.getChcd());
		boolean chatbotlogin = WebUtil.getSession(OmniConstants.CHATBOT_LOGIN_SESSION) == null ? false : (boolean) WebUtil.getSession(OmniConstants.CHATBOT_LOGIN_SESSION);

		if (StringUtils.isEmpty(authenticatedSnsParam.getChcd())) {
			authenticatedSnsParam.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		}
		model.addAttribute("rv", this.config.resourceVersion());
		log.debug("▶▶▶▶▶▶ [join sns regist] sns param : {}, joinTo : {}", StringUtil.printJson(authenticatedSnsParam), snsParam.getJoinTo());
		
		// Brand Site 중 신규 가입 시 연관된 타 채널 시스템의 광고성 수신 동의 여부를 받아야 하는 경우 (ex: 뷰티엔젤) 2021-08-03 hjw0228
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		boolean isBrandSite = config.isBrandSite(authenticatedSnsParam.getChcd(), profile);
		String marketingChCd = config.getMarketingChCd(authenticatedSnsParam.getChcd(), profile);

		final Channel channel = this.commonService.getChannel(authenticatedSnsParam.getChcd());
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setUnm(authenticatedSnsParam.getUserName());
		final String loginid = StringUtils.hasText(authenticatedSnsParam.getLoginId()) ? authenticatedSnsParam.getLoginId().replaceAll(" ", "").toLowerCase() : ""; // 2023-07-12 loginId 공백 제거 및 소문자 처리
		joinRequest.setLoginid(loginid);
		joinRequest.setIncsno(authenticatedSnsParam.getIncsNo());
		joinRequest.setLoginpassword(authenticatedSnsParam.getPassword());
		joinRequest.setGender(authenticatedSnsParam.getGender());
		joinRequest.setPhone(authenticatedSnsParam.getPhone());
		joinRequest.setBirth(authenticatedSnsParam.getBirth());
		joinRequest.setCi(authenticatedSnsParam.getCiNo());
		joinRequest.setNational("K");
		joinRequest.setChcd(authenticatedSnsParam.getChcd());
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		joinRequest.setIntegrateid("true");

		joinRequest.setSnsIdPrcnCd("KA");
		joinRequest.setKkoIntlOptiYn("Y");
		joinRequest.setKkoIntlOptiDt(DateUtil.getCurrentDate());
		
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [join regist] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());
				Types types = WebUtil.getTypes(offlineParam.getDt(), offlineParam.getOp());
				if (types != null) {
					joinRequest.setDeviceType(types.getDeviceType());
					joinRequest.setOsType(types.getOsType());
				}
			}
		}

		// 전사 약관 정보
		String bpTerms = this.config.getTermsTags();
		String bpMarketingTag = this.config.getMarketingTermsTag("030");
		String chMarketingTag = this.config.getMarketingTermsTag(joinRequest.getChcd());
		if(isBrandSite && StringUtils.hasText(marketingChCd)) { // Brand Site 중 신규 가입 시 연관된 타 채널 시스템의 광고성 수신 동의 여부를 받아야 하는 경우 (ex: 뷰티엔젤) 2021-10-19 hjw0228
			chMarketingTag = this.config.getMarketingTermsTag(marketingChCd);
		}
		String chPrivacyTag = this.config.getPrivacyTermsTag(joinRequest.getChcd());
		String[] bpTermsArray = bpTerms.split(",");

		// 약관 컬렉션
		List<Marketing> joinMarketings = new ArrayList<>();
		List<Terms> joinBpTerms = new ArrayList<>();
		List<Terms> joinTerms = new ArrayList<>();
		
		stopWatch.start("Social Login Step - Join  : KA Get SnsAllowedTerms");
		// 동의 약관 목록		
		List<String> allowedTermsList = snsAuth.getSnsAllowedTerms(SnsType.KAKAO.getType(), WebUtil.getStringSession(OmniConstants.SNS_ACCESS_TOKEN));
		
		stopWatch.stop();
		log.info(stopWatch.prettyPrint());
		
		Terms terms = null;
		// 전사 약관 설정
		for (int i = 0; i < bpTermsArray.length; i++) {
			terms = new Terms();
			terms.setTncAgrYn(isAgreeTerms(allowedTermsList, bpTermsArray[i]) ? "Y" : "N");
			terms.setTncaChgDt(DateUtil.getCurrentDate());
			terms.setTcatCd(config.getTermsCode(bpTermsArray[i]));
			terms.setTncvNo(config.getTermsVersion(bpTermsArray[i]));
			terms.setChgChCd(OmniConstants.JOINON_CHCD);
			joinBpTerms.add(terms);
		}
		
		if(StringUtils.hasText(chPrivacyTag)) { // 계열사 개인정보 제공동의 약관 존재하는 경우
			terms = new Terms();
			terms.setTncAgrYn(isAgreeTerms(allowedTermsList, chPrivacyTag) ? "Y" : "N");
			terms.setTncaChgDt(DateUtil.getCurrentDate());
			terms.setTcatCd(config.getTermsCode(chPrivacyTag));
			terms.setTncvNo(config.getTermsVersion(chPrivacyTag));
			terms.setChgChCd(joinRequest.getChcd());
			joinBpTerms.add(terms);
		}		

		joinRequest.setBpterms(joinBpTerms);

		// 뷰티포인트 문자 수신 동의
		Marketing bpMarketing = new Marketing();
		bpMarketing.setChCd("000");
		bpMarketing.setSmsAgree(isAgreeTerms(allowedTermsList, bpMarketingTag) ? "Y" : "N");
		joinMarketings.add(bpMarketing);

		// 경로 문자 수신 동의
		Marketing chMarketing = new Marketing();
		chMarketing.setChCd(channel.getChCd());
		if(isBrandSite && StringUtils.hasText(marketingChCd)) { // Brand Site 중 신규 가입 시 연관된 타 채널 시스템의 광고성 수신 동의 여부를 받아야 하는 경우 (ex: 뷰티엔젤) 2021-10-19 hjw0228
			chMarketing.setChCd(marketingChCd);
		}
		if(config.isMarketingSyncBpEnable(channel.getChCd(), profile)) { // isMarketingSyncBpEnable 면 000 채널 수신동의 여부와 동기화
			chMarketing.setSmsAgree(isAgreeTerms(allowedTermsList, bpMarketingTag) ? "Y" : "N");
		} else {
			chMarketing.setSmsAgree(isAgreeTerms(allowedTermsList, chMarketingTag) ? "Y" : "N");
		}
		
		joinMarketings.add(chMarketing);

		joinRequest.setMarketings(joinMarketings);

		// 경로 약관 설정
		// 경로 개인정보 제3자 동의 약관 동의
		TermsVo termsVo = new TermsVo();
		termsVo.setChCd(joinRequest.getChcd());
		if(isBrandSite && StringUtils.hasText(marketingChCd)) { // Brand Site 중 신규 가입 시 연관된 타 채널 시스템의 광고성 수신 동의 여부를 받아야 하는 경우 (ex: 뷰티엔젤) 2021-10-19 hjw0228
			termsVo.setChCd(marketingChCd);
		}
		List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
		if (corpTerms != null && !corpTerms.isEmpty()) {
			for (TermsVo vo : corpTerms) {
				terms = new Terms();
				terms.setTncAgrYn("Y");
				terms.setTncaChgDt(DateUtil.getCurrentDate());
				terms.setTcatCd(vo.getTcatCd());
				terms.setTncvNo(vo.getTncvNo());
				terms.setChgChCd(vo.getChCd());
				joinTerms.add(terms);
			}
		}

		SnsTermsVo snsTermsVo = new SnsTermsVo();
		snsTermsVo.setChCd(channel.getChCd());
		if(isBrandSite && StringUtils.hasText(marketingChCd)) { // Brand Site 중 신규 가입 시 연관된 타 채널 시스템의 광고성 수신 동의 여부를 받아야 하는 경우 (ex: 뷰티엔젤) 2021-10-19 hjw0228
			snsTermsVo.setChCd(marketingChCd);
		}
		
		final boolean isOffline = OmniUtil.isOffline(channel);
		final String onlineChCd = ChannelPairs.getOnlineCd(channel.getChCd());
		if(isOffline && StringUtils.hasText(onlineChCd)) { // 오프라인으로 회원 가입 시 온라인 약관 태그 목록도 같이 조회
			snsTermsVo.setChCd(onlineChCd);
		}
		
		snsTermsVo.setTermsTag(allowedTermsList);
		// 경로약관 테이블에서 정보 가져옴
		List<TermsVo> chTerms = this.termsService.getTermsByTags(snsTermsVo);
		if (chTerms != null && !chTerms.isEmpty()) {
			for (TermsVo vo : chTerms) {
				terms = new Terms();
				terms.setTncAgrYn(isAgreeTerms(allowedTermsList, vo.getKasyTncIdntVl()) ? "Y" : "N");
				terms.setTncaChgDt(DateUtil.getCurrentDate());
				terms.setTcatCd(vo.getTcatCd());
				terms.setTncvNo(vo.getTncvNo());
				terms.setChgChCd(vo.getChCd());
				joinTerms.add(terms);
			}
		}

		
		
		joinRequest.setTerms(joinTerms);
		log.debug("▶▶▶▶▶▶ [join sns regist] joinRequest : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = new BaseResponse();
		switch (snsParam.getJoinTo()) {
		case "all": // X X X -> 통합 고객 등록
			stopWatch.start("고객통합-뷰티-옴니-채널 등록");
			
			joinRequest.setJoinType("1"); // FIXME 타입 "35" 넣으면 경로 2번 들어가서 임시 처리
			if(joinRequest.isOffLine()) {
				response = this.apiOfflineProcessStep.registOfflineCustomerProcess(joinRequest);
			} else {
				response = this.apiOnlineProcessStep.registCustomerProcess(joinRequest);
			}
			
			stopWatch.stop();
			
			break;
		case "ch": // O X X -> 경로 + 옴니 등록
			
			// 고객통합 업데이트 - 뷰티포인트 등록 - 경로 가입 intergration + channel
			stopWatch.start("고객통합-뷰티-채널 등록");
			
			if(joinRequest.isOffLine()) {
				response = this.apiOfflineProcessStep.registOfflineBpCustomerProcess(joinRequest);
			} else {
				response = this.apiOnlineJoinProcessStep.joinPhoneRegistProcess(joinRequest);
			}
			
			stopWatch.stop();
			
			break;
		case "omni": // O X O -> 옴니 등록
			
			stopWatch.start("옴니 등록");
			
			if(joinRequest.isOffLine()) {
				response = this.apiOfflineProcessStep.applyOfflineBpCustomerTermsProcess(joinRequest);
			} else {
				response = this.apiOnlineConvTemsProcessStep.convsTerm202ChProcess(joinRequest);
			}

			stopWatch.stop();
			
			break;
			
		}
		
		log.info(stopWatch.prettyPrint());
		
		
		// 경로 탈퇴 체크
		if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
			model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
			model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

			if(chatbotlogin) { // 챗봇을 통한 회원 가입 일 경우 cancelUrl로 리다이렉트
				Object chatbotParam = WebUtil.getSession(OmniConstants.CHATBOTPARAM);
				model.addAttribute("home", ((ChatbotParam) chatbotParam).getCancelUrl());
				model.addAttribute("homeurl", ((ChatbotParam) chatbotParam).getCancelUrl());
			} else {
				model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
				model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));	
			}
			
			model.addAttribute("channelName", channel.getChCdNm());
			model.addAttribute("channelWtdt", response.getMessage());
			return "info/channel_withdraw";
		}
		
		
		boolean isSuccessRegist = ResultCode.SUCCESS.getCode().equals(response.getResultCode());
		log.debug("▶▶▶▶▶▶ [join sns regist] Regist Customer Process = IsSuccess? : {}, ResultCode : {}", isSuccessRegist, response.getResultCode());
		/*
		 * if (!isSuccessRegist) { // 통합 가입 API 실패 return "redirect:/join?" + WebUtil.getSsoParams() + "&error=norequired"; }
		 */

		
		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setCiNo(authenticatedSnsParam.getCiNo());
		Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
		
		log.debug("▶▶▶▶▶▶ [join sns regist] customer : {}", customer);

		if (customer.getRsltCd().equals("ICITSVCOM999")) { // 고객 정보 조회 실패
			log.debug("▶▶▶▶▶▶  [join sns regist] Customer search Fail, chCd : {}, loginId : {}", authenticatedSnsParam.getChcd(), authenticatedSnsParam.getLoginId());
			return "redirect:/join?" + WebUtil.getSsoParams() + "&error=norequired&category=SNS";
		}

		authenticatedSnsParam.setIncsNo(customer.getIncsNo());
		
		stopWatch.start("Social Login Step - Join  : KA Do Sns Associate");
		
		// sns 맵핑
		ApiResponse apiRes = this.customerApiService.doSnsAssociate(authenticatedSnsParam);
		
		stopWatch.stop();
		log.debug(stopWatch.prettyPrint());
		
		log.debug("▶▶▶▶▶▶ [join sns regist] SNS Mapping ResultCode : {}", apiRes.getResultCode());
		
		boolean isMapping = ResultCode.SUCCESS.getCode().equals(apiRes.getResultCode());
		if (!isMapping) { // 매핑실패 -> 정상적 데이터라면 실패날 이유가 없음
			log.debug("▶▶▶▶▶▶ !!!!! [join sns regist] SNS Mapping Fail!!!! ◆◆◆");
		}
		
		//ga tagging start logic 누락 추가 2022.03.25
		final Map<String,String> gaCookieMap = gataggingUtils.getGaCookieMap(request);	//ga tagging get cookie value
		String totalJoinCnt = "1";
		String channelJoinCnt = "0";
		  
		try {

			String joinCase = OmniGaTaggingConstants.GA_SIGNUP_CASE_INTERGRATED;

			if ("ch".equals(snsParam.getJoinTo())) {
				totalJoinCnt = "0";
				channelJoinCnt = "1";
			} else if ("omni".equals(snsParam.getJoinTo())) {
				totalJoinCnt = "0";
				joinCase = OmniGaTaggingConstants.GA_SIGNUP_CASE_ONLINE_MEMBER;
			}
			WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE, snsParam.getJoinTo());
			WebUtil.setSession(OmniGaTaggingConstants.EL, "카카오");
			WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL, totalJoinCnt); // 통합회원
			WebUtil.setSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL, channelJoinCnt); // 경로회원
			log.debug("▶▶▶▶▶▶  GA Tagging JOIN START(sns post regist) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID), gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),
					WebUtil.getSession(OmniGaTaggingConstants.EL), channel.getChCdNm(), channel.getChCd());

			// 회원가입 중복 입력 방지를 위해 세션에 eventAction 및 EL 값 저장
			WebUtil.setSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_START_CHECK_KEY, "true");
			WebUtil.setSession(OmniGaTaggingConstants.GA_JOIN_EVENT_LABEL, WebUtil.getSession(OmniGaTaggingConstants.EL));

			GaTagData gaTagDto = GaTagData.builder()
					.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
					.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
					.el((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
					.loginType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
					.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
					.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
					.chCd(channel.getChCd()).chCdNm(channel.getChCdNm())
					.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_START)
					.joinType(joinCase).totalJoinCnt(totalJoinCnt)
					.channelJoinCnt(channelJoinCnt)
					.sendFlag("Y")
					.incsNo(custInfoVo.getIncsNo())
					.sessionId(request.getSession().getId())
					.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
					.build();

			gataggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}

		WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(customer.getCustNm()));
		WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(authenticatedSnsParam.getIncsNo()));
		WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(authenticatedSnsParam.getLoginId()));
		WebUtil.setSession(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(authenticatedSnsParam.getPassword()));
		
		// 카카오를 통한 신규회원 가입인 경우 카카오 알림톡 발송
		if("all".equals(snsParam.getJoinTo()) && "KA".equalsIgnoreCase(authenticatedSnsParam.getSnsType())) {
			joinService.sendKakaoNotice(customer, channel);
		}
		
		return WebUtil.getRedirectUrlWithContextPath("/join/finish/join?chCd=" + authenticatedSnsParam.getChcd());
	}

	@GetMapping("/mobile-move-on")
	public String mobileMoveOn(final HttpServletResponse servletResponse, final Model model) throws UnsupportedEncodingException {

		final String xmid = WebUtil.getStringSession(OmniConstants.XID_MSESSION);
		model.addAttribute("actionurl", this.commonAuthUrl);
		model.addAttribute(OmniConstants.XID_SESSION, xmid);
		model.addAttribute(OmniConstants.XPW_SESSION, WebUtil.getStringSession(OmniConstants.XPW_MSESSION));
		model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));
		model.addAttribute("rv", this.config.resourceVersion());
		WebUtil.setCookies(servletResponse, OmniConstants.LAST_LOGIN_TYPE, "mobile");
		
		boolean chatbotlogin = WebUtil.getSession(OmniConstants.CHATBOT_LOGIN_SESSION) == null ? false : (boolean) WebUtil.getSession(OmniConstants.CHATBOT_LOGIN_SESSION);
		// 카카오 챗봇의 경우 returnUrl로 리다이렉트
		if(chatbotlogin) {
			Object chatbotParam = WebUtil.getSession(OmniConstants.CHATBOTPARAM);
			String returnUrl = ((ChatbotParam) chatbotParam).getReturnUrl();
			return "redirect:" + returnUrl;
		} 

		boolean autologin = "Y".equals(WebUtil.getStringSession(OmniConstants.AUTO_LOGIN_SESSION));
		model.addAttribute(OmniConstants.AUTO_LOGIN, autologin);

		final String authType = this.config.commonAuthType();
		log.debug("▶▶▶▶▶▶ [mobile sso move] wso2 common auth type : {}", authType);
		if (authType.equals(HttpMethod.GET.name())) {
			StringBuilder authurl = new StringBuilder();
			authurl.append(this.commonAuthUrl); //
			if (StringUtils.hasText(xmid)) {
				authurl.append("?fullName=").append(URLEncoder.encode(xmid, StandardCharsets.UTF_8.name()));
			} else {
				authurl.append("?fullName=Dummy");
			}
			if (autologin) {
				authurl.append("&chkRemember=on");
			}
			authurl.append("&incsNo=").append(URLEncoder.encode(WebUtil.getStringSession(OmniConstants.XPW_MSESSION), StandardCharsets.UTF_8.name()));
			authurl.append("&sessionDataKey=").append(URLEncoder.encode(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION), StandardCharsets.UTF_8.name()));
			// log.debug("▶▶▶▶▶▶ [mobile sso move] wso2 common auth url : {}", authurl.toString());
			return "redirect:" + authurl.toString();
		} else {
			model.addAttribute("actionurl", this.commonAuthUrl);
			WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
			return "cert/mobile-moveon";
		}
	}

	@GetMapping("/move-on")
	public String moveOn(final SSOParam ssoParam, final HttpServletResponse servletResponse, final HttpServletRequest request, final HttpSession session, final Model model) throws UnsupportedEncodingException {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("rv", this.config.resourceVersion());
		final String sessionDataKey = ssoParam.getSessionDataKey();
		
		boolean chatbotlogin = WebUtil.getSession(OmniConstants.CHATBOT_LOGIN_SESSION) == null ? false : (boolean) WebUtil.getSession(OmniConstants.CHATBOT_LOGIN_SESSION);
		
		if (StringUtils.isEmpty(sessionDataKey)) {
			log.debug("▶▶▶▶▶▶ [sso move] move on session data key from session : {}", WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));
			model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));
		} else {
			log.debug("▶▶▶▶▶▶ [sso move] move on session data key from sso param : {}", sessionDataKey);
			model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, sessionDataKey);
		}

		final String snsAuthUrl = WebUtil.getStringSession(OmniConstants.SNS_TERMS_AFTER_AUTH_URL);
		if (StringUtils.hasText(snsAuthUrl)) {
			log.debug("▶▶▶▶▶▶ [sso move] move on - SNS Auth ♥♥♥");
			WebUtil.removeSession(OmniConstants.SNS_TERMS_AFTER_AUTH_URL);
			return "redirect:" + snsAuthUrl;
		}

		final String loginid = WebUtil.getStringSession(OmniConstants.XID_SESSION); // 로그인 아이디 세션 생성(나중에 로그인할때 쓰기 위해)
		final String loginpw = WebUtil.getStringSession(OmniConstants.XPW_SESSION); // 로그인 비.밀.번.호 세션 생성(나중에 로그인할때 쓰기 위해)

		// model.addAttribute(OmniConstants.XID_SESSION, SecurityUtil.getXValue(loginid, false));
		// model.addAttribute(OmniConstants.XPW_SESSION, SecurityUtil.getXValue(loginpw, false));
		model.addAttribute(OmniConstants.XID_SESSION, loginid); // - 2022.08.25 ID / PW 암호화하여 로그인 처리
		model.addAttribute(OmniConstants.XPW_SESSION, loginpw); // - 2022.08.25 ID / PW 암호화하여 로그인 처리
		model.addAttribute(OmniConstants.IS_ENCRYPTION, "true");

		// log.debug("▶▶▶▶▶▶ [sso move] move on encoded ID : {}, login ID : {}", loginid, SecurityUtil.getXValue(loginid, false));
		// log.debug("▶▶▶▶▶▶ [sso move] move on login PW : {}", loginpw);
		

		if (OmniUtil.isOffline(channel)) { // 오프라인인 경우
			model.addAttribute("actionurl", ssoParam.getRedirectUri());
		} else {
			// 카카오 챗봇의 경우 returnUrl로 리다이렉트
			if(chatbotlogin) {
				Object chatbotParam = WebUtil.getSession(OmniConstants.CHATBOTPARAM);
				String returnUrl = ((ChatbotParam) chatbotParam).getReturnUrl();
				return "redirect:" + returnUrl;
			} else {
				model.addAttribute("actionurl", this.commonAuthUrl);
			}
		}

		if (StringUtils.hasText(ssoParam.getAuthFailure()) && StringUtil.isTrue(ssoParam.getAuthFailure())) {
			if (StringUtils.hasText(ssoParam.getAuthFailureMsg())) {
				final String msg = this.messageSource.getMessage(ssoParam.getAuthFailureMsg(), null, LocaleUtil.getLocale());
				model.addAttribute("authFailure", ssoParam.getAuthFailure());
				model.addAttribute("authFailureMsg", msg);
			}
		}

		boolean autologin = "Y".equals(WebUtil.getStringSession(OmniConstants.AUTO_LOGIN_SESSION));
		model.addAttribute(OmniConstants.AUTO_LOGIN, autologin);

		WebUtil.setCookies(servletResponse, OmniConstants.LAST_LOGIN_TYPE, "basic");

		final String authType = this.config.commonAuthType();
		log.debug("▶▶▶▶▶▶ [sso move] wso2 common auth type : {}", authType);
		
		// ChannelLoginUrl 이 존재할 경우 WSO2 거치지 않고 다이렉트로 이동, 이동 시 사용자 아이디는 암호화 처리
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		StringBuilder channelLoginUrl = new StringBuilder();
		channelLoginUrl.append(config.getChannelLoginUrl(channel.getChCd(), profile));
		if(StringUtils.hasText(channelLoginUrl)) {
			channelLoginUrl.append("?paramId=").append(URLEncoder.encode(loginid, StandardCharsets.UTF_8.name()));
			return "redirect:" + channelLoginUrl.toString();
		}
		if (authType.equals(HttpMethod.GET.name())) {
			String decloginid = SecurityUtil.getXValue(loginid, false);
			String decloginpw = SecurityUtil.getXValue(loginpw, false);
			StringBuilder authurl = new StringBuilder();
			authurl.append(this.commonAuthUrl);
			if (StringUtils.hasText(decloginid) && StringUtils.hasText(decloginpw)) {
				authurl.append("?username=").append(URLEncoder.encode(SecurityUtil.getXValue(loginid, false), StandardCharsets.UTF_8.name()));
				authurl.append("&password=").append(URLEncoder.encode(SecurityUtil.getXValue(loginpw, false), StandardCharsets.UTF_8.name()));
			} else {
				String username = WebUtil.getStringSession(OmniConstants.USERNM_SESSION);
				String incsno = WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION);
				if (StringUtils.hasText(username) && StringUtils.hasText(incsno)) {
					authurl.append("?fullName=").append(URLEncoder.encode(username, StandardCharsets.UTF_8.name()));
					authurl.append("&incsNo=").append(URLEncoder.encode(incsno, StandardCharsets.UTF_8.name()));
				} else {
					return "redirect:/go-login";
				}
			}
			if (autologin) {
				authurl.append("&chkRemember=on");
			}
			authurl.append("&sessionDataKey=").append(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));
			// log.debug("▶▶▶▶▶▶ [sso move] wso2 common auth url : {}", authurl.toString());
			return "redirect:" + authurl.toString();
		} else {
			model.addAttribute("actionurl", this.commonAuthUrl);
			WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
			return "cert/moveon";
		}
	}

	@PostMapping("/go-terms")
	public String goTermsApply(final UserData userData, final HttpServletResponse servletResponse, final Model model, final Locale locale) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);

		String encryptedIncsNo = "";
		if (userData == null || StringUtils.isEmpty(userData.getIncsNo())) {
			encryptedIncsNo = WebUtil.getStringSession(OmniConstants.XINCS_NO_SESSION);
		} else {
			encryptedIncsNo = userData.getIncsNo();
		}

		final String incsNo = SecurityUtil.getXValue(encryptedIncsNo, false);
		final int intIncsNo = Integer.parseInt(incsNo);

		List<UmOmniUser> omniUsers = this.mgmtService.getOmniUserList(intIncsNo);
		String mobileNo = "";
		UmOmniUser omniUser = null;
		if (omniUsers != null && omniUsers.size() > 0) {
			omniUser = omniUsers.get(0);
		} else {
			CertResult certResult = this.commonService.getCertResult(incsNo);
			if (certResult != null) {
				model.addAttribute("category", certResult.getCategory());
				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setCiNo(certResult.getCiNo());
				CustInfoResponse response = this.customerApiService.getCustList(custInfoVo);
				if (response != null) {
					Customer users[] = response.getCicuemCuInfTcVo();
					if (users != null && users.length > 0) {
						Customer user = users[0]; // 데이터가 최신순으로 정렬되어 전달되므로 첫번째 데이터선택하면 됨.
						log.debug("▶▶▶▶▶ [go terms] customer user : {}", StringUtil.printJson(user));
						omniUser = new UmOmniUser();
						omniUser.setFullName(user.getCustNm());
						omniUser.setUmUserName(user.getChcsNo());
						omniUser.setIncsNo(user.getIncsNo());
						mobileNo = StringUtil.mergeMobile(user);
						final String mobile = OmniUtil.maskMobile(mobileNo, locale);
						model.addAttribute("mobile", mobile);
						if (StringUtils.hasText(user.getMbrJoinDt())) {
							omniUser.setCreatedDate(user.getMbrJoinDt());
							model.addAttribute("joindate", DateUtil.getBirthDate(user.getMbrJoinDt()));
						}

					}
				}
			}
		}

		if (StringUtils.hasText(omniUser.getCreatedDate())) {
			model.addAttribute("joindate", DateUtil.getBirthDate(omniUser.getCreatedDate()));
		}

		log.debug("▶▶▶▶▶ [go terms] omniUser info : {}", StringUtil.printJson(omniUser));
		final Channel channel = this.commonService.getChannel(chCd);

		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("chcd", channel.getChCd());
		model.addAttribute("entry", OmniUtil.getOfflineParam());

		// 진입 채널에 대한 약관 출력 정보 조회

		TermsVo termsVo = new TermsVo();
		termsVo.setChCd(channel.getChCd());
		boolean isAgreeeTerms = this.termsService.hasTermsAgree(omniUser);
		log.debug("▶▶▶▶▶▶ [go terms] agree terms : {} --> {}", chCd, isAgreeeTerms);
		if(chCd.equals(OmniConstants.OSULLOC_CHCD)) {
			termsVo.setIncsNo(omniUser.getIncsNo());
		}
		List<TermsVo> termsList = this.termsService.getTerms(termsVo);
		model.addAttribute("terms", termsList);

		log.debug("▶▶▶▶▶ [go terms] name[1] : {}", omniUser.getFullName());
		String name = OmniUtil.maskUserName(omniUser.getFullName(), locale);
		log.debug("▶▶▶▶▶ [go terms] name[2] : {}", name);

		final String id = omniUser.getUmUserName(); // 약관동의에서 아이디는 마스킹하지 않음.

		if (StringUtils.hasText(omniUser.getIncsNo())) {
			model.addAttribute("incsno", omniUser.getIncsNo());
			model.addAttribute("xincsno", SecurityUtil.setXyzValue(omniUser.getIncsNo()));
			model.addAttribute(OmniConstants.INCS_NO_SESSION, omniUser.getIncsNo());
			model.addAttribute(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(omniUser.getIncsNo()));
		} else {
			model.addAttribute("incsno", incsNo);
			model.addAttribute("xincsno", SecurityUtil.setXyzValue(incsNo));
			model.addAttribute(OmniConstants.INCS_NO_SESSION, incsNo);
			model.addAttribute(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));
		}

		model.addAttribute("name", name);
		model.addAttribute("id", id);
		model.addAttribute("xname", SecurityUtil.setXyzValue(omniUser.getFullName()));
		model.addAttribute("xid", SecurityUtil.setXyzValue(id));

		model.addAttribute("home", channel.getHmpgUrl());
		model.addAttribute("homeurl", channel.getHmpgUrl());
		TermsVo corpTermsVo = new TermsVo();
		corpTermsVo.setChCd(channel.getChCd());
		List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
		model.addAttribute("corptermslist", corpTermsList);
		
		//20230404 개인정보 수집 및 이용 동의 (마케팅)
		UmOmniUser omniUser2 = new UmOmniUser();
		final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
		omniUser.setChCd(onlineChCd);
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
		
		return "terms/terms_apply"; // ME-FO-A0105
	}

	// @PostMapping("/go-idregist")
	@RequestMapping(value = "/go-idregist", method = { RequestMethod.GET, RequestMethod.POST })
	public String goIdRegist(final UserData userData, final HttpServletResponse servletResponse, final Model model, final Locale locale) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		String incsNo = SecurityUtil.getXValue(userData.getIncsNo(), false);

		if (StringUtils.isEmpty(incsNo)) {
			incsNo = WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION);
		}

		String joinStepType = WebUtil.getStringSession("joinStepType");

		model.addAttribute("joinStepType", joinStepType);

		// 본인인증 에서 전달 받은 값
		CertResult certResult = this.commonService.getCertResult(incsNo);
		if (certResult == null) {
			return "redirect:/join?" + WebUtil.getSsoParams();
		}
		
		model.addAttribute("category", certResult.getCategory());

		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("chcd", channel.getChCd());
		model.addAttribute(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));
		model.addAttribute("unm", SecurityUtil.setXyzValue(certResult.getName()));

		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setIncsNo(incsNo);
		final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
		if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
			String username = customer.getCustNm();
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
		if (StringUtils.hasText(incsNo)) {
			omniUser.setIncsNo(incsNo);
		}
		omniUser.setChCd(chcd);

		if (!this.termsService.hasTermsAgree(omniUser)) { // ME-FO-A0105, 진입 채널에 대한 약관 출력 정보 조회
			TermsVo termsVo = new TermsVo();
			termsVo.setChCd(chcd);
			if(chcd.equals(OmniConstants.OSULLOC_CHCD)) {
				termsVo.setIncsNo(omniUser.getIncsNo());
			}
			List<TermsVo> termsList = this.termsService.getTerms(termsVo);
			model.addAttribute("terms", termsList);
		}

		model.addAttribute("homeurl", channel.getHmpgUrl());
		model.addAttribute("home", channel.getHmpgUrl());
		TermsVo termsVo = new TermsVo();
		termsVo.setChCd(channel.getChCd());
		List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
		model.addAttribute("corptermslist", termsList);
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
//					model.addAttribute("terms", termsList2);
			for(TermsVo vo : termsList2) {
				if(vo.getMkSn()==-20) {
					model.addAttribute("terms_marketing", vo);
				}
			}
		}
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
		model.addAttribute("joinAditor", joinAditor);
		String headerType = config.isHeaderType(channel.getChCd(), profile);
		model.addAttribute("headertype", headerType);
		
		return "login/id_regist";
	}

	@GetMapping("/terms-content")
	public String getTermsContent(final Model model) {

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("chcd", channel.getChCd());
		String onlineChCd = ChannelPairs.getOnlineCd(chCd);
		
		log.debug("▶▶▶▶▶▶ [join step off] online channel code : {}", onlineChCd);
		// 오프라인인 경우 온라인 경로 약관 호출
		UmOmniUser omniUser = new UmOmniUser();
		omniUser.setChCd(onlineChCd);

		if (!this.termsService.hasTermsAgree(omniUser)) { // 진입 채널에 대한 약관 출력 정보 조회
			TermsVo termsVo = new TermsVo();
			termsVo.setChCd(onlineChCd);
			List<TermsVo> termsList = this.termsService.getTerms(termsVo);
			model.addAttribute("terms", termsList);
		}

		return "terms/terms_content";
	}
	
	@GetMapping("/thirdparty-terms-content")
	@ResponseBody
	public List<TermsVo> getThirdTermsContent(final Model model) {

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		String onlineChCd = ChannelPairs.getOnlineCd(chCd);
		
		log.debug("▶▶▶▶▶▶ [join step off] online channel code : {}", onlineChCd);
		
		TermsVo termsVo = new TermsVo();
		termsVo.setChCd(onlineChCd);
		
		List<TermsVo> corpTermsList = termsService.getCorpTerms(termsVo);

		return corpTermsList;
	}
}
