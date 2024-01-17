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
 * Author	          : mcjan
 * Date   	          : 2020. 8. 6..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.social.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.WebUtils;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustChannelRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaVo;
import com.amorepacific.oneap.auth.api.vo.ovo.CreateCustChannelJoinResponse;
import com.amorepacific.oneap.auth.api.vo.ovo.CustYnResponse;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.ga.GaTaggingUtils;
import com.amorepacific.oneap.auth.ga.OmniGaTaggingConstants;
import com.amorepacific.oneap.auth.ga.vo.GaTagData;
import com.amorepacific.oneap.auth.join.service.JoinService;
import com.amorepacific.oneap.auth.join.vo.JoinData;
import com.amorepacific.oneap.auth.join.vo.JoinRequest;
import com.amorepacific.oneap.auth.login.service.WSO2Service;
import com.amorepacific.oneap.auth.membership.vo.MembershipUserInfo;
import com.amorepacific.oneap.auth.mgmt.service.MgmtService;
import com.amorepacific.oneap.auth.social.handler.SnsAuth;
import com.amorepacific.oneap.auth.social.service.SocialService;
import com.amorepacific.oneap.auth.social.vo.SnsTermsVo;
import com.amorepacific.oneap.auth.terms.service.TermsService;
import com.amorepacific.oneap.auth.terms.vo.TermsVo;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.exception.OmniException;
import com.amorepacific.oneap.common.types.LoginType;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.ObjectUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.Phone;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.ChatbotParam;
import com.amorepacific.oneap.common.vo.Marketing;
import com.amorepacific.oneap.common.vo.OfflineParam;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.Types;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.SnsVo;
import com.amorepacific.oneap.common.vo.sns.SnsEnvVo;
import com.amorepacific.oneap.common.vo.sns.SnsKakaoAccount;
import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.amorepacific.oneap.common.vo.sns.SnsProfileResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenVo;
import com.amorepacific.oneap.common.vo.sns.SnsType;
import com.amorepacific.oneap.common.vo.user.CicuedCuChArrayTcVo;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.social 
 *    |_ SnsAuthenticatedCallbackController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 6.
 * @version : 1.0
 * @author : mcjan
 */
@Controller
@Slf4j
public class SnsAuthenticatedCallbackController {

	@Autowired
	private SnsAuth snsAuth;

	@Autowired
	private WSO2Service wso2Service;

	@Autowired
	private JoinService joinService;

	@Autowired
	private CustomerApiService customerApiService;

	@Autowired
	private MgmtService mgmtService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private TermsService termsService;

	@Autowired
	private SocialService socialService;
	
	@Value("${wso2.ssoauthorizeurl}")
	private String ssoAuthorizeUrl;

	@Value("${wso2.commonauthurl}")
	private String commonAuthUrl;

	@Autowired
	private SystemInfo systemInfo;
	
	@Autowired
	private GaTaggingUtils gataggingUtils; //ga taagging util
	
	private ConfigUtil config = ConfigUtil.getInstance();

	// callback을 하나의 url로 받아 로직 분기용 - SNS 사용처
	private final String JOIN = "usedSnsJoin"; // 회원가입
	private final String JOIN_OFF = "usedSnsJoinOff"; // 회원가입(오프라인)
	private final String LOGIN = "usedSnsLogin"; // 로그인
	private final String MAPPING = "usedSnsMapping"; // 매핑
	private final String MEMBERSHIP = "usedSnsMembership"; // 멤버십

	/**
	 * <pre>
	 * comment  : sns authenticated callback 
	 * author   : hkdang
	 * date     : 2020. 9. 16. 오후 3:19:33
	 * </pre>
	 * 
	 * @param snsDesc
	 * @param param
	 * @return
	 */
	@SuppressWarnings({ "deprecation"})
	@RequestMapping(value = "/authenticated/sns/{snsDesc}", method = { RequestMethod.GET, RequestMethod.POST })
	public String snsAuthenticate(@PathVariable String snsDesc, @RequestParam Map<String, String> param, final Locale locale, final Model model, final HttpServletResponse response, final HttpServletRequest request, final HttpSession session) {
		
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		log.debug("▶▶▶▶▶▶ [ CALLBACK SUCCESS - authenticated SNS] param: {}", param);
		String snsUseType = WebUtil.getStringSession(OmniConstants.SNS_USE_TYPE);		
		log.info("▶▶▶▶▶▶  [snsAuthenticate] sns use type : {}", snsUseType);
		
		// 2023-03-28 네이버 스마트 스토어 멤버십 연동일 경우 리턴
		if (snsUseType.equals(MEMBERSHIP)) {
			final String isMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_MEMBERSHIP);
			boolean isMembership = OmniUtil.isMembership(isMembershipSession);
			final String isUnLinkMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_UNLINK_MEMBERSHIP);
			boolean isUnLinkMembership = OmniUtil.isUnLinkMembership(isUnLinkMembershipSession);
			
			WebUtil.setSession(OmniConstants.SNSPARAM, param);
			
			if(isMembership) {
				return "redirect:/membership/naver/callback";	
			} else if(isUnLinkMembership) {
				return "redirect:/terms/naver/callback";
			}
		}
		
		// 2022-09-27 SNS 로그인 완료 후 History Back 통해 재 로그인 시 임시 조치 (naver, facebook)
		boolean isSnsLoginComplete = WebUtil.getSession(OmniConstants.IS_LOGIN_COMPLETE) == null ? false : (boolean) WebUtil.getSession(OmniConstants.IS_LOGIN_COMPLETE);
		if(isSnsLoginComplete && !MAPPING.equals(snsUseType)) {
			return "redirect:/retry";
		}
		
		// 2022-10-18 Permalink 관련 수정 건
		if(param != null && !StringUtils.isEmpty(param.get("state"))) {
			String state = StringEscapeUtils.unescapeHtml4(param.get("state"));
			JsonObject stateParam = ObjectUtil.convertStringToJsonObject(state);
			if(stateParam != null) {
				if("true".equals(stateParam.get("permalink").getAsString())) {
					session.invalidate();
					log.info("▶▶▶▶▶▶▶▶▶▶ stateParam : {}", stateParam);
					// Permalink 로 진입 시 SSOParam Setting
					WebUtil.setSSOParam(stateParam);
					String ssoAuthUrl = this.ssoAuthorizeUrl.concat("?").concat(WebUtil.getSsoParamsAuth());
					return "redirect:" + ssoAuthUrl;
				}	
			}
		}

		StopWatch stopWatch = new StopWatch("SNS(authenticated get action)");
		
		// 에러시 진행 불가
		String error = param.get("error");
		if (StringUtils.hasText(error)) {
			log.info("▶▶▶▶▶▶ [authenticated SNS] error: {}", error);

			// offline check
			SnsOfflineParam snsOfflineParam = (SnsOfflineParam) WebUtil.getSession(OmniConstants.SNS_OFFLINEPARAM);
			if (snsOfflineParam != null) {
				return this.snsKakaoBridge(request, model, snsOfflineParam);
			}
			
			// Mapping 으로 인입한 경우 redirectUrl로 리턴
			if (snsUseType.equals(MAPPING)) {
				return "redirect:" + WebUtil.getStringSession(OmniConstants.SNS_MAPPING_AFTER_REDIRECT_URL);
			}

			if (snsUseType.equals(JOIN)) {
				return "redirect:/join?" + WebUtil.getSsoParams();
			}
			return "redirect:/login?" + WebUtil.getSsoParams(); // + "&snsError=" + error;
		}
		
		// Apple 로그인은 에러 시 code is null
		if ("apple".equals(snsDesc.toLowerCase()) && StringUtils.isEmpty(param.get("code"))) {
			log.info("▶▶▶▶▶▶ [authenticated SNS] error : code is null");
			
			// Mapping 으로 인입한 경우 redirectUrl로 리턴
			if (snsUseType.equals(MAPPING)) {
				return "redirect:" + WebUtil.getStringSession(OmniConstants.SNS_MAPPING_AFTER_REDIRECT_URL);
			}

			if (snsUseType.equals(JOIN)) {
				return "redirect:/join?" + WebUtil.getSsoParams();
			}
			return "redirect:/login?" + WebUtil.getSsoParams(); // + "&snsError=" + error;
		}
		
        //TODO 경로 가입시 완료처리가 ga push 됨 버그 수정해야됨 20211208
		// sns 타입 체크
		String snsType = "";
		snsDesc = snsDesc.toLowerCase();
		if (snsDesc.equals("kakao")) {
			snsType = SnsType.KAKAO.getType();
			WebUtil.setSession(OmniGaTaggingConstants.EL, "카카오");
		} else if (snsDesc.equals("naver")) {
			snsType = SnsType.NAVER.getType();
			WebUtil.setSession(OmniGaTaggingConstants.EL, "네이버");
		} else if (snsDesc.equals("facebook")) {
			snsType = SnsType.FACEBOOK.getType();
			WebUtil.setSession(OmniGaTaggingConstants.EL, "페이스북");
		} else if (snsDesc.equals("apple")) {
			snsType = SnsType.APPLE.getType();
			WebUtil.setSession(OmniGaTaggingConstants.EL, "애플");
		}
		


		boolean validType = false;
		for (SnsType type : SnsType.values()) {
			if (type.getType().equalsIgnoreCase(snsType)) {
				validType = true;
				break;
			}
		}

		if (validType == false || snsType.isEmpty()) {
			log.info("▶▶▶▶▶▶ [Invalid SNS Type] snsType : {}", snsType);
			if (snsUseType.equals(JOIN)) {
				return "redirect:/join?" + WebUtil.getSsoParams();
			}
			return "redirect:/login?" + WebUtil.getSsoParams();
		}

		// vo set
		SnsTokenVo snsTokenVo = new SnsTokenVo();
		snsTokenVo.setCode(param.get("code"));
		snsTokenVo.setState(SnsAuth.getNaverState());

		String token = "";
		// Facebook SDK 사용으로 인하여 분기 처리
		if(!SnsType.FACEBOOK.getType().equals(snsType) && !SnsType.APPLE.getType().equals(snsType)) {
			stopWatch.start("Social Login Step - 01  : " + snsType + " Get Token");
			
			// token + profile API call
			SnsTokenResponse snsTokenResponse = this.customerApiService.getSnsToken(snsType, snsTokenVo);
			
			if(snsTokenResponse == null || snsTokenResponse.getAccessToken() == null) {
				model.addAttribute("oauthErrorCode", "9999");
				model.addAttribute("oauthErrorMsg", "경로코드가 존재하지 않습니다.");
				model.addAttribute("message", "현재 서비스가 원할하지 않습니다.<br>잠시 후 다시 시도해주시기 바랍니다.");

				return "wso2/oauth2_error";
			}
			token = snsTokenResponse.getAccessToken();
			snsTokenVo.setAccessToken(token);
			
			stopWatch.stop();
		} else if (SnsType.FACEBOOK.getType().equals(snsType)) {
			stopWatch.start("Social Login Step - 01  : " + snsType + " Get Token");
			// param.getCode is not null
			if(param != null && !StringUtils.isEmpty(param.get("code"))) {
				// token + profile API call
				SnsTokenResponse snsTokenResponse = this.customerApiService.getSnsToken(snsType, snsTokenVo);
				token = snsTokenResponse.getAccessToken();
				snsTokenVo.setAccessToken(token);
			} else {
				token = (String) param.get("accessToken");
				snsTokenVo.setAccessToken(token);	
			}
			
			stopWatch.stop();
		} else if (SnsType.APPLE.getType().equals(snsType)) {
			stopWatch.start("Social Login Step - 01  : " + snsType + " Get Token");
			
			if(param.get("id_token") != null) {
				token = param.get("id_token"); // Apple의 경우 Access Token 대신 ID Token으로 처리
				snsTokenVo.setAccessToken(token);
			}
			
			stopWatch.stop();
		}
		
		// 세션에 sns_auth_code 저장
		WebUtil.setSession(OmniConstants.SNS_AUTH_CODE, snsTokenVo.getCode());
		
		log.info(stopWatch.prettyPrint());
		
		// 토큰 발급 실패시 진행 불가
		log.debug("▶▶▶▶▶▶ [SNS AccessToken] = {} ", token);
		
		if (StringUtils.isEmpty(token)) {
			log.info("▶▶▶▶▶▶ [Get SNS AccessToken Fail]"); // 실패 메시지 보내주면 좋겠다

			// offline check
			SnsOfflineParam snsOfflineParam = (SnsOfflineParam) WebUtil.getSession(OmniConstants.SNS_OFFLINEPARAM);
			if (snsOfflineParam != null) {
				return this.snsKakaoBridge(request, model, snsOfflineParam);
			}
			
			// Mapping 으로 인입한 경우 redirectUrl로 리턴
			if (snsUseType.equals(MAPPING)) {
				return "redirect:" + WebUtil.getStringSession(OmniConstants.SNS_MAPPING_AFTER_REDIRECT_URL);
			}

			if (snsUseType.equals(JOIN)) {
				return "redirect:/join?" + WebUtil.getSsoParams();
			}
			return "redirect:/login?" + WebUtil.getSsoParams();
		}
		WebUtil.setSession(OmniConstants.SNS_ACCESS_TOKEN, token);
		// WebUtil.setCookies(response, "authorize-access-token", token);
		
		stopWatch.start("Social Login Step - 02  : " + snsType + " Get SNS Profile");
		SnsProfileResponse snsProfileResponse = null;
		if(!SnsType.APPLE.getType().equals(snsType)) {
			snsProfileResponse = this.customerApiService.getSnsProfile(snsType, snsTokenVo);
		} else {
			snsProfileResponse = this.socialService.getProfileFromAppleIdToken(token);
			
			if(snsProfileResponse == null) { // ID Token 복호화 실패 혹은 검증 실패
				log.info("▶▶▶▶▶▶ [Get Apple ID Token Parse Fail]"); // 실패 메시지 보내주면 좋겠다
				
				// Mapping 으로 인입한 경우 redirectUrl로 리턴
				if (snsUseType.equals(MAPPING)) {
					return "redirect:" + WebUtil.getStringSession(OmniConstants.SNS_MAPPING_AFTER_REDIRECT_URL);
				}

				if (snsUseType.equals(JOIN)) {
					return "redirect:/join?" + WebUtil.getSsoParams();
				}
				return "redirect:/login?" + WebUtil.getSsoParams();
			}
		}
	
		String snsId = snsType.equalsIgnoreCase(SnsType.NAVER.getType()) ? snsProfileResponse.getResponse().getId() : snsProfileResponse.getId();
		log.debug("▶▶▶▶▶▶  [snsAuthenticate] snsId : {}", snsId);

		stopWatch.stop();
		log.info(stopWatch.prettyPrint());
		
		/*
		 * String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION); if(StringUtils.isEmpty(chCd) && param.get("state") != null) { String
		 * tempCh = param.get("state"); log.info("▶▶▶▶▶▶ [snsAuthenticate] Have not 'chCd' In Session, callback state : {}", tempCh);
		 * WebUtil.setSession(OmniConstants.CH_CD_SESSION, tempCh.replace("m", "")); }
		 */

		// sns 정보 셋팅
		SnsAuthUserVo snsAuthUserVo = new SnsAuthUserVo();
		snsAuthUserVo.setSnsId(snsId);
		snsAuthUserVo.setSnsType(snsType);
		snsAuthUserVo.setToken(token);
		snsAuthUserVo.setProfileResponse(snsProfileResponse);
		snsAuthUserVo.setMobileAuth(false);

		String state = param.get("state");
		if (StringUtils.hasText(state)) {
			if (state.contains("isMobile")) {
				snsAuthUserVo.setMobileAuth(true);
			}
		}

		log.debug("▶▶▶▶▶▶  [snsAuthenticate] SnsAuthUserVo : {}", StringUtil.printJson(snsAuthUserVo));
		// WebUtil.removeSession(OmniConstants.SNS_USE_TYPE);
		switch (snsUseType) {
		case JOIN:
			log.info("■ □ ■ □ ■  [KAKAOSYNC JOIN Start]  □ ■ □ ■ □");
			model.addAttribute("snsUseType", JOIN);
			return snsJoin(snsAuthUserVo, locale, model, request, response);
		case JOIN_OFF:
			log.info("■ □ ■ □ ■  [KAKAOSYNC JOIN Offline Start]  □ ■ □ ■ □");
			return snsJoinOffline(snsAuthUserVo, locale, model);
		case LOGIN:
			if (snsType.equals(SnsType.KAKAO.getType())) { // 카카오 로그인도 회원가입과 동일하게 동작. but 취소나 실패 했을때 보내는 페이지가 로그인/회원가입 분리되어 있어서 usetype == 로그인 시 카카오 따로 분기
				log.info("■ □ ■ □ ■  [KAKAOSYNC LOGIN Start]  □ ■ □ ■ □");
				
				return snsJoin(snsAuthUserVo, locale, model,request, response);
			}

			log.info("■ □ ■ □ ■  [SNS Login Start]  □ ■ □ ■ □");
			return snsLogin(snsAuthUserVo, locale, model, request, response);
		case MAPPING:
			log.info("■ □ ■ □ ■  [SNS Mapping Start]  □ ■ □ ■ □");
			return snsMapping(snsAuthUserVo, locale);
		default:
			return "redirect:/login?" + WebUtil.getSsoParams();
		}
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : hkdang
	 * date     : 2020. 9. 23. 오후 12:08:11
	 * </pre>
	 * 
	 * @param snsProfileResponse
	 * @param locale
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String snsJoin(SnsAuthUserVo snsAuthUserVo, final Locale locale, final Model model, final HttpServletRequest request, final HttpServletResponse response) {

		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		String kakaoType = SnsType.KAKAO.getType();
		boolean chatbotlogin = WebUtil.getSession(OmniConstants.CHATBOT_LOGIN_SESSION) == null ? false : (boolean) WebUtil.getSession(OmniConstants.CHATBOT_LOGIN_SESSION);
		
		StopWatch stopWatch = new StopWatch("SNS(authenticated get action)");

		SnsKakaoAccount kakaoAccount = snsAuthUserVo.getProfileResponse().getKakaoAccount();

		String snsId = snsAuthUserVo.getSnsId();
		String ci = kakaoAccount.getCi(); // (String) kakaoAccount.get("ci");
		String realName = kakaoAccount.getLegal_name(); // (String) kakaoAccount.get("legal_name");
		String nickName = kakaoAccount.getProfile().getNickname();
		String gender = kakaoAccount.getLegal_gender().equalsIgnoreCase("male") ? "M" : "F"; // (String) kakaoAccount.get("legal_gender");
		String birth = kakaoAccount.getLegal_birth_date().replace("-", "").replace(" ", "").replace(":", ""); // (String) kakaoAccount.get("legal_birth_date");
		String phoneNumber = kakaoAccount.getPhone_number(); // (String) kakaoAccount.get("phone_number");
		String email = kakaoAccount.getEmail(); // (String) kakaoAccount.get("email");

		log.debug("▶▶▶▶▶▶ [SNS Join KakaoSnyc] snsId: {}, ci: {}, realName: {}, nickName: {}, gender: {}, birth: {}, phoneNumber: {}, email: {}", snsId, ci, realName, nickName, gender, birth, phoneNumber, email);

		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
	
		final String isMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_MEMBERSHIP);
		boolean isMembership = OmniUtil.isMembership(isMembershipSession);
		
		// 만 14세 이상 가입 가능
		if (DateUtil.isJoinRestrictByAuth(birth)) { // brith = YYYYMMDD
			if(chatbotlogin) { // 챗봇을 통한 회원 가입 일 경우 cancelUrl로 리다이렉트
				Object chatbotParam = WebUtil.getSession(OmniConstants.CHATBOTPARAM);
				model.addAttribute("home", ((ChatbotParam) chatbotParam).getCancelUrl());
				model.addAttribute("homeurl", ((ChatbotParam) chatbotParam).getCancelUrl());
			} else if(isMembership) {
				model.addAttribute("home", WebUtil.getSession(OmniConstants.CANCEL_URI));
				model.addAttribute("homeurl", WebUtil.getSession(OmniConstants.CANCEL_URI));
			} else {
				model.addAttribute("home", channel.getHmpgUrl());
				model.addAttribute("homeurl", channel.getHmpgUrl());
			}
			return "mgmt/restrict_age";
		}

		Object ssoObj = WebUtil.getSession(OmniConstants.SSOPARAM);
		if (ssoObj == null) {
			log.info("▶▶▶▶▶▶ [sns join sso parameter is null]");
			return "redirect:" + OmniUtil.getRedirectUrl(channel);
		}

		boolean isValidData = !StringUtils.isEmpty(ci);
		isValidData &= !StringUtils.isEmpty(snsId);
		isValidData &= !StringUtils.isEmpty(realName);
		isValidData &= !StringUtils.isEmpty(gender);
		isValidData &= !StringUtils.isEmpty(birth);
		isValidData &= !StringUtils.isEmpty(phoneNumber);

		final Map<String,String> gaCookieMap = gataggingUtils.getGaCookieMap(request);	//ga tagging get cookie value
		
		// 카카오 계정이 있으나 회원가입이 불가능한 계정(이름, 성별, 연락처 필수 정보가 없는 계정)인 경우
		if (!isValidData) {
			log.info("KAKAO INFO IS INVALID : ci? {}, realName? {}, gender? {}, birth? {}, phoneNumber? {} ", ci, realName, gender, birth, phoneNumber);
			/*
			 * // 회원가입 성공률 데이터 수집을 위해 카카오 회원가입 시 GA태깅 if(JOIN.equals(model.getAttribute("snsUseType"))) { return "redirect:/join?" +
			 * WebUtil.getSsoParams() + "&error=norequired&category=SNS"; } else { return "redirect:/join?" + WebUtil.getSsoParams() +
			 * "&error=norequired"; }
			 */
			
			try {
				
				WebUtil.setSession(OmniGaTaggingConstants.EL,"카카오");
				
				log.debug("▶▶▶▶▶▶  GA Tagging LOGIN START FAIL( snsJoin SNS({}) 시스템 오류 : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",snsAuthUserVo.getSnsType(),gaCookieMap.get(OmniGaTaggingConstants.CID)
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
							   .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_FAIL)
							   .joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
							   .errorMessage("시스템 오류")
	                           .sendFlag("Y")
	                           .sessionId(request.getSession().getId())
							   .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
							   .loginId(snsAuthUserVo.getLoginId())
							   .build();
				
		        gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);
				    	
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}
		}

		Phone phone = new Phone.Builder() //
				.phoneNumber(phoneNumber) //
				.countryCode(locale.getCountry()) //
				.build();
		phoneNumber = phone.displayNationalPhoneNumber();

		SnsParam snsParam = new SnsParam();
		snsParam.setSnsType(kakaoType);
		snsParam.setSnsId(snsId);
		snsParam.setUserName(realName);
		snsParam.setBirth(birth);
		snsParam.setPhone(phoneNumber.replaceAll("-", ""));
		snsParam.setGender(gender);
		snsParam.setCiNo(ci);

		CustInfoVo custCiVo = new CustInfoVo();
		custCiVo.setCiNo(ci);
		
		// 통합 회원 가입여부 조회
		stopWatch.start("Social Login Step - 03  : " + kakaoType + " Get CustList");
		Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custCiVo);
		stopWatch.stop();
		log.info(stopWatch.prettyPrint());
		
		// 오류 발생 시 오류 페이지 리턴
		if (customer == null) {
			String snsUseType = WebUtil.getStringSession(OmniConstants.SNS_USE_TYPE);
			if (snsUseType.equals(JOIN)) {
				return "redirect:/join?" + WebUtil.getSsoParams();
			}
			return "redirect:/login?" + WebUtil.getSsoParams();
		}
		
		if (customer.getRsltCd().equals("ICITSVCOM999")) {
			/*
			 * // 이름 +생년월일+전화번호 로 한번더 조회 ? CustInfoVo custInfoVo = new CustInfoVo(); custInfoVo.setCustName(realName); custInfoVo.setAthtDtbr(birth);
			 * custInfoVo.setCustMobile(phoneNumber.replaceAll("-", ""));
			 * 
			 * if(customer == null || customer.getRsltCd().equals("ICITSVCOM999") {
			 */

			// SNS(카카오) 정보 들고
			WebUtil.setSession(OmniConstants.SNSPARAM, snsParam);

			// ID/PW 등록
			if(JOIN.equals(model.getAttribute("snsUseType"))
					/*|| LOGIN.equals(model.getAttribute("snsUseType"))*/) { // 카카오 로그인했는데, 회원 가입 안되어 있는 경우, 회원가입 통계 처리를 위한 GA 태깅 추가
			
				return WebUtil.getRedirectUrlWithContextPath("/join/regist?to=all&category=SNS");				
			}
			else {
				return WebUtil.getRedirectUrlWithContextPath("/join/regist?to=all");				
			}
		}

		// 뷰티포인트 탈퇴 고객 조회
		if (StringUtils.hasText(customer.getCustWtDttm())) { // customer.getCustWtYn().equals("Y") -> "N"은 주는데 "Y"는 안줌
			model.addAttribute("withdrawDate", DateUtil.getBirthDate(customer.getCustWtDttm()));
			if(chatbotlogin) { // 챗봇을 통한 회원 가입 일 경우 cancelUrl로 리다이렉트
				Object chatbotParam = WebUtil.getSession(OmniConstants.CHATBOTPARAM);
				model.addAttribute("home", ((ChatbotParam) chatbotParam).getCancelUrl());
				model.addAttribute("homeurl", ((ChatbotParam) chatbotParam).getCancelUrl());
			} else if(isMembership) {
				model.addAttribute("home", WebUtil.getSession(OmniConstants.CANCEL_URI));
				model.addAttribute("homeurl", WebUtil.getSession(OmniConstants.CANCEL_URI));
			} else {
				model.addAttribute("home", channel.getHmpgUrl());
				model.addAttribute("homeurl", channel.getHmpgUrl());				
			}

			if(JOIN.equals(model.getAttribute("snsUseType"))) {
				model.addAttribute("category", "SNS");
			}
			return "mgmt/restrict_withdraw";
		}

		log.debug("▶▶▶▶▶▶ [SNS Join] Customer : {}", StringUtil.printJson(customer));
		
		// espoirPOS만 가입된 휴면 고객이 espoir몰 가입 시 espoirPOS 재가입하지 않기 위해 
		// 아래 함수에서 가입된 고객 채널정보 리스트를 OmniConstants.EXIST_CUSTOMER에 추가한다.
		CustInfoVo custVo = new CustInfoVo();
		custVo.setIncsNo(customer.getIncsNo());
		stopWatch.start("Social Login Step - 04  : " + kakaoType + " Get CicuemcuInfrByIncsNo");
		customerApiService.getCicuemcuInfrByIncsNo(custVo, true);
		stopWatch.stop();
		log.info(stopWatch.prettyPrint());
		
		// 옴니 가입 여부 조회
		UmOmniUser omniUser = new UmOmniUser();
		omniUser.setUmAttrName("incsNo");
		omniUser.setUmAttrValue(customer.getIncsNo());

		/**
		 * 아래 경우 체크 -> ID/PW 등록 화면으로 이동 X X X : to=all O X X : to=ch O X O : to=omni
		 */
		// 옴니회원 정보가 없으면
		List<UmOmniUser> omniUserList = this.wso2Service.getWso2UserInfo(omniUser);
		if (omniUserList == null || omniUserList.size() == 0 || omniUserList.get(0) == null) {
			// SNS(카카오) 정보 세션에 저장
			snsParam.setIncsNo(customer.getIncsNo());
			snsParam.setLoginId(customer.getChcsNo());
			WebUtil.setSession(OmniConstants.SNSPARAM, snsParam);

			// 경로 유저 탐색
			UmChUser chUser = new UmChUser();
			chUser.setIncsNo(Integer.parseInt(customer.getIncsNo()));

			// ID/PW 등록
			List<UmChUser> chUserList = this.wso2Service.getChannelUserInfo(chUser);
			if (chUserList == null || chUserList.size() == 0 || chUserList.get(0) == null) {
				if(JOIN.equals(model.getAttribute("snsUseType"))
						/*|| LOGIN.equals(model.getAttribute("snsUseType"))*/) { // 카카오 로그인했는데, 회원 가입 안되어 있는 경우, 회원가입 통계 처리를 위한 GA 태깅 추가
							
										
					return WebUtil.getRedirectUrlWithContextPath("/join/regist?to=ch&category=SNS");				
				}
				else {
					return WebUtil.getRedirectUrlWithContextPath("/join/regist?to=ch");				
				}
			}

			if(JOIN.equals(model.getAttribute("snsUseType"))
					/*|| LOGIN.equals(model.getAttribute("snsUseType"))*/) { // 카카오 로그인했는데, 회원 가입 안되어 있는 경우, 회원가입 통계 처리를 위한 GA 태깅 추가
				
				return WebUtil.getRedirectUrlWithContextPath("/join/regist?to=omni&category=SNS");				
			}
			else {
							 
				return WebUtil.getRedirectUrlWithContextPath("/join/regist?to=omni");				
			}
		}

		omniUser = omniUserList.get(0);
		log.debug("▶▶▶▶▶▶ [SNS Join] omniUser : {}", StringUtil.printJson(omniUser));

		// 옴니 탈퇴 고객 조회
		if ((StringUtils.hasText(omniUser.getAccountDisabled()) && omniUser.getAccountDisabled().equalsIgnoreCase("true"))) {
			model.addAttribute("withdrawDate", DateUtil.getBirthDate(omniUser.getDisabledDate()));
			if(chatbotlogin) { // 챗봇을 통한 회원 가입 일 경우 cancelUrl로 리다이렉트
				Object chatbotParam = WebUtil.getSession(OmniConstants.CHATBOTPARAM);
				model.addAttribute("home", ((ChatbotParam) chatbotParam).getCancelUrl());
				model.addAttribute("homeurl", ((ChatbotParam) chatbotParam).getCancelUrl());
			} else if (isMembership) {
				model.addAttribute("home", WebUtil.getSession(OmniConstants.CANCEL_URI));
				model.addAttribute("homeurl", WebUtil.getSession(OmniConstants.CANCEL_URI));			
			} else {
				model.addAttribute("home", channel.getHmpgUrl());
				model.addAttribute("homeurl", channel.getHmpgUrl());	
			}
			if(JOIN.equals(model.getAttribute("snsUseType"))) {
				model.addAttribute("category", "SNS");
			}
			return "mgmt/restrict_withdraw";
		}

		String incsNo = omniUser.getIncsNo();
		String loginId = omniUser.getUmUserName();

		// 고객 휴면 상태 조회
		boolean isDormancy = joinService.isDormancyCustomer(incsNo);
		if (isDormancy) {
			stopWatch.start("Social Login Step : " + kakaoType + " Release Dormancy Customer");
			int rtn = this.joinService.releaseDormancyCustomer1(incsNo, chCd);
			stopWatch.stop();
			log.info(stopWatch.prettyPrint());
			
			if (rtn == 0 || rtn == 1) {
				;
			} else if (rtn == 2) { // 휴면 복구 시 에러 메시지 [RA-01403: no data found] : 휴면 DB에 사용자 없는 경우 확인 신규 가입 진행
				log.info("▶▶▶▶▶▶ [SNS Join] 신규가입");
				
				// SNS(카카오) 정보 들고
				WebUtil.setSession(OmniConstants.SNSPARAM, snsParam);

				// ID/PW 등록
				if (JOIN.equals(model.getAttribute("snsUseType"))
						/*|| LOGIN.equals(model.getAttribute("snsUseType"))*/) { // 카카오 로그인했는데, 회원 가입 안되어 있는 경우, 회원가입 통계 처리를 위한 GA 태깅 추가
					
					return WebUtil.getRedirectUrlWithContextPath("/join/regist?to=all&category=SNS");				
				} else {
					return WebUtil.getRedirectUrlWithContextPath("/join/regist?to=all");				
				}
			} else {
				

					try {
						
						WebUtil.setSession(OmniGaTaggingConstants.EL,"카카오");
						
						log.debug("▶▶▶▶▶▶  GA Tagging LOGIN START FAIL(snsJoin SNS({}) 시스템 오류 : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",snsParam.getSnsType(),gaCookieMap.get(OmniGaTaggingConstants.CID)
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
								   .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_FAIL)
								   .joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
								   .errorMessage("시스템 오류")
		                           .sendFlag("Y")
		                           .sessionId(request.getSession().getId())
								   .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]").build();
												
						    	gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);
						    	
					} catch (Exception ex) {
						log.error(ex.getMessage());
					}
				
				throw new OmniException("시스템 오류가 발생하였습니다. 관리자에게 문의하시기 바랍니다.");
			}
		}

		// 기존 SNS 연결 회원 조회
		snsParam.setIncsNo(incsNo);
		snsParam.setLoginId(loginId);

		String mappingId = this.wso2Service.getUserNameBySnsInfo(kakaoType, snsId);
		if (StringUtils.isEmpty(mappingId)) { // 옴니 회원 이지만 기존 SNS 연결 회원이 아니면 맵핑 처리
			stopWatch.start("Social Login Step - 05  : " + kakaoType + " Do Sns Associate");
			ApiResponse apiResponse = customerApiService.doSnsAssociate(snsParam);
			log.info("▶▶▶▶▶▶ [regist kakao SNS Associate result] response : {}", StringUtil.printJson(apiResponse));
			stopWatch.stop();
			log.info(stopWatch.prettyPrint());
		}

		// snsAuthUserVo의 CI로 가져온 incsNo
		// sns 매핑 테이블에서 가져온 incsNo
		// 같으면 진행
		// 다르면 업데이트(삭제 -> 추가)
		// join on 매핑 날리고 joinOnSnsLinker setConnect = "N"
		// join on 매핑 추가하고 joinOnSnsLinker setConnect = "Y"
		// CI 값으로 가져온 정보로 매핑 테이블 user name 업데이트

		snsParam.setSnsId(snsId);
		snsParam.setSnsType(kakaoType);

		final UmOmniUser umOmniUser = this.socialService.getSnsMappingIncsNo(snsParam);
		if (incsNo.equals(umOmniUser.getIncsNo())) {
			log.debug("▶▶▶▶▶▶ incs number same!!!");
		} else {
			// join on 매핑 날리고 joinOnSnsLinker setConnect = "N"
			SnsVo snsVo = new SnsVo();
			snsVo.setConnectYN("N");
			snsVo.setCstmid(umOmniUser.getUmUserName());
			snsVo.setSnsAuthkey(snsParam.getSnsId());
			snsVo.setSnsType(kakaoType);
			snsVo.setUcstmid(umOmniUser.getIncsNo());
			ApiResponse apiresponse = this.customerApiService.joinOnSnsLinker(snsVo);
			log.info("▶▶▶▶▶▶ join on sns un-mapping : {}, {}", StringUtil.printJson(snsVo), StringUtil.printJson(apiresponse));
			if ("00".equals(apiresponse.getRsltCd())) {
				// join on 매핑 추가하고 joinOnSnsLinker setConnect = "Y"
				snsVo.setConnectYN("Y");
				snsVo.setCstmid(snsParam.getLoginId());
				snsVo.setSnsAuthkey(snsParam.getSnsId());
				snsVo.setSnsType(kakaoType);
				snsVo.setUcstmid(snsParam.getIncsNo());
				apiresponse = this.customerApiService.joinOnSnsLinker(snsVo);
				log.info("▶▶▶▶▶▶ join on sns mapping : {}, {}", StringUtil.printJson(snsVo), StringUtil.printJson(apiresponse));

			}

			boolean rtn = this.socialService.updateSnsMapping(snsParam);
			log.info("▶▶▶▶▶▶ omni update sns mapping : {}, {}", StringUtil.printJson(snsParam), rtn);
			
		}

		// 약관 체크
		snsAuthUserVo.setIncsNo(incsNo);
		snsAuthUserVo.setLoginId(loginId);

		String withdrawJsp = doSnsTermsSync(snsAuthUserVo, model, locale);
		if (StringUtils.hasText(withdrawJsp)) {
			return withdrawJsp;
		}
		
		// 카카오싱크 전환가입 업데이트
		if (snsAuthUserVo.getSnsType().equalsIgnoreCase(SnsType.KAKAO.getType())) {
			List<UmChUser> chUsers = mgmtService.getChannelConversionUserList(chCd, incsNo);
			if (chUsers != null && !chUsers.isEmpty()) {
				UmChUser umChUser = new UmChUser();
				umChUser.setChCd(chCd);
				umChUser.setIncsNo(Integer.parseInt(incsNo));
				log.debug("▶▶▶▶▶▶ [SNS Join] chCd : {}, incsNo : {]", chCd, incsNo);
				boolean convs = mgmtService.updateConversionComplete(umChUser);
				log.debug("convs : {}", convs);
			}
		}

		// 세션에 셋팅 후 auth
		WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginId));
		WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

		String authenticatorList = ((SSOParam) ssoObj).getAuthenticators();
		String authenticator = OmniUtil.getAuthenticator(authenticatorList, kakaoType);

		String authUrl = commonAuthUrl + "?idp=" + kakaoType + "&authenticator=" + authenticator + "&sessionDataKey=" + WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION) + "&multiOptionURI="
				+ WebUtil.getUrlEncodedSsoParams();
		
		// 카카오 챗봇의 경우 returnUrl로 리다이렉트
		if(chatbotlogin) {
			authUrl = ((SSOParam) ssoObj).getRedirectUri();			
		} else if(isMembership) { // 뷰티 멤버십 연동을 통한 회원가입 시 멤버십 연동 완료 화면으로 이동
			MembershipUserInfo membershipUserInfo = (MembershipUserInfo) WebUtil.getSession(OmniConstants.MEMBERSHIP_USERINFO);
			if(StringUtils.isEmpty(membershipUserInfo.getId()) || StringUtils.isEmpty(membershipUserInfo.getIncsNo()) || StringUtils.isEmpty(membershipUserInfo.getName())) {
				if(StringUtils.isEmpty(membershipUserInfo.getId())) {
					String id = loginId;
					if (StringUtils.hasText(id)) {
						membershipUserInfo.setId(id);
					}	
				}
				
				if(StringUtils.isEmpty(membershipUserInfo.getIncsNo())) {
					membershipUserInfo.setIncsNo(incsNo);
				}
				
				if(StringUtils.isEmpty(membershipUserInfo.getName())) {
					String name = realName;
					if (StringUtils.hasText(name)) {
						membershipUserInfo.setName(name);
					}
				}
				
				WebUtil.setSession(OmniConstants.MEMBERSHIP_USERINFO, membershipUserInfo);
			}
			
			// 뷰티 멤버십 연동을 통한 회원 가입일 경우 CI 값 검증
			if(membershipUserInfo != null && !StringUtils.isEmpty(ci)) {
				if(!membershipUserInfo.getCiNo().equals(ci)) {
					log.debug("▶▶▶▶▶▶ [membership join] CI불일치  : ");
					log.debug("▶▶▶▶▶▶ [membership CiNo]  : {}", membershipUserInfo.getCiNo());
					log.debug("▶▶▶▶▶▶ [certResult CiNo]  : {}", ci);
					
					return "info/membership_ci_mismatch";
				}
			}
			
			String name = OmniUtil.maskUserName(realName, locale);
			String id = OmniUtil.maskUserId(loginId);
			
			model.addAttribute("chCd", chCd);
			model.addAttribute("id", id);
			model.addAttribute("name", name);
			model.addAttribute("xname", SecurityUtil.setXyzValue(realName));
			model.addAttribute("xid", SecurityUtil.setXyzValue(loginId));
			model.addAttribute("xincsNo", SecurityUtil.setXyzValue(incsNo));
			model.addAttribute("mbrId", membershipUserInfo.getMbrId());
			
			return "membership/membership_start"; // 멤버십 연동 페이지 이동
		} else {
			// 모바일 + 카카오 일때 SSO 임시 처리
			if (snsAuthUserVo.isMobileAuth() && snsAuthUserVo.getSnsType().equalsIgnoreCase(SnsType.KAKAO.getType())) {
				
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				
				boolean kakaoAuthenticator = StringUtil.isTrue(this.config.getSnsInfo(profile, kakaoType, "authenticator"));
				
				log.info("is kakao authenticator? {}", kakaoAuthenticator);
				
				StringBuilder authurl = new StringBuilder();
				authurl.append(this.commonAuthUrl); //
				
				if (kakaoAuthenticator) {
					authurl.append("?snsId=").append(snsAuthUserVo.getSnsId()); 
					authurl.append("&idp=").append(kakaoType);
					authurl.append("&sessionDataKey=").append(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));
				} else {
					try {
						String xmid = snsAuthUserVo.getProfileResponse().getKakaoAccount().getLegal_name();
						if (StringUtils.hasText(xmid)) {
							authurl.append("?fullName=").append(URLEncoder.encode(xmid, StandardCharsets.UTF_8.name()));
						} else {
							authurl.append("?fullName=Dummy");
						}
						authurl.append("&incsNo=").append(URLEncoder.encode(omniUser.getIncsNo(), StandardCharsets.UTF_8.name()));
						authurl.append("&sessionDataKey=").append(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));

					} catch (UnsupportedEncodingException e) {
						log.error("▶▶▶▶▶▶ [regist kakao] UnsupportedEncodingException : {}", e);
					}
				}
				authUrl = authurl.toString();
			}	
		}

		boolean autologin = "Y".equals(WebUtil.getStringSession(OmniConstants.AUTO_LOGIN_SESSION));
		if (autologin) {
			authUrl = authUrl + "&chkRemember=on";
		}
		
		if(OmniUtil.isOffline(channel)) {
			String profile = this.systemInfo.getActiveProfiles()[0];
			profile = StringUtils.isEmpty(profile) ? "dev" : profile;
			profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
			String homeurl = OmniUtil.getCancelRedirectOfflineUrl(channel, profile);
			List<Customer> customers = new ArrayList<>();
			customers.add(customer);
			model.addAttribute("offline", OmniUtil.isOffline(channel));
			model.addAttribute("users", customers);
			model.addAttribute("homeurl", homeurl);
			
			final String username = OmniUtil.maskUserName(customer.getCustNm(), locale);
			final String userid = OmniUtil.maskUserId(loginId);
			StringBuilder nameid = new StringBuilder(username);
			nameid.append("(").append(userid).append(")");
			model.addAttribute("nameid", nameid.toString());

			boolean channeljoin = (Boolean) WebUtils.getSessionAttribute(request, OmniConstants.IS_CREATE_CUST_CHANNEL_MEMBER);
			
			if(channeljoin) {
				return "join/apply_finish_off106";
			} else {
				return "mgmt/joined";
			}
		}

		WebUtil.setCookies(response, OmniConstants.LAST_LOGIN_TYPE, kakaoType.toLowerCase());
		log.debug("▶▶▶▶▶▶ [kakao sync join complete] userId : {}, incsNo : {} ", loginId, incsNo);

		// 해당 경로시스템의 수신 동의 여부가 없을 경우 고객통합에 경로 가입 여부 확인 후 경로 가입 처리 hjw0228
		// 브랜드 사이트의 경우 처리하지 않음 2021-08-03 hjw0228
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
			
		if(!config.isBrandSite(channel.getChCd(), profile)) {
			TermsVo termsVo = new TermsVo();
			termsVo.setChCd(chCd);
			List<TermsVo> termsVos = this.termsService.getCorpTerms(termsVo);
			if(termsVos == null || termsVos.size() == 0) {
				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setIncsNo(incsNo);
				custInfoVo.setChCd(chCd);

				CustYnResponse custYnResponse = this.customerApiService.getCustYn(custInfoVo);
							
				if(custYnResponse != null && "ICITSVCOM001".equals(custYnResponse.getRsltCd())) { // 경로 가입 되어 있지 않으면 경로 가입 API 호출
					
					log.debug("고객통합 경로 등록 API {} : {}", chCd, loginId);

					CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedChannelCustomerData(channel, incsNo, loginId, umOmniUser.getUmUserPassword());

					log.debug("▶▶▶▶▶▶ integrated channel customer api data : {}", StringUtil.printJson(chCustRequest));

					CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
					log.debug("▶▶▶▶▶▶ integrated channel {} customer api response : {}", channel.getChCdNm(), StringUtil.printJson(chjoinResponse));

					// 경로 고객 존재하는 경우도 성공으로 판단
					boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse.getRsltCd());

					log.info("▶▶▶▶▶▶ integrated channel result : {} ---> {}", chjoinResponse.getRsltCd(), success);
				}
			}
		}

		
		if(JOIN.equals(model.getAttribute("snsUseType"))) {
			WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
			model.addAttribute("authUrl", authUrl);
			return "sns/auth_kakao_finish";
		}else {
			
			if (snsAuthUserVo.getSnsType().equalsIgnoreCase(SnsType.KAKAO.getType())) {
			 
			 WebUtil.setSession(OmniGaTaggingConstants.EL,"카카오");
				
               try {
					//임시 3.17 순서 확인해서 위치 이동해야됨.
					log.debug("▶▶▶▶▶▶  GA Tagging LOGIN START (/authenticated/sns/ SNS({}) [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",snsParam.getSnsType(),gaCookieMap.get(OmniGaTaggingConstants.CID)
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
		                            .joinType(OmniGaTaggingConstants.GA_SIGNUP_CASE_INTERGRATED)
		                            .totalJoinCnt("1")
		                            .channelJoinCnt("0")
		                            .sendFlag("Y")
		                            .sessionId(request.getSession().getId())
		                            .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]").build();
			
			     
				gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);

			
				}catch(Exception ex) {
					log.error(ex.getMessage());
				}
				
				
				//TODO 카카오 로그인 성공
				 //####################### ga tagging 로그인 완료처리 ###################
				try {
					
					log.debug("▶▶▶▶▶▶  GA Tagging LOGIN SUCCESS (/authenticated/sns/ SNS({}) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",snsParam.getSnsType(),gaCookieMap.get(OmniGaTaggingConstants.CID)
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
                            .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_SUCCESS)
                            .sendFlag("Y")
                            .incsNo(incsNo)
                            .loginId(loginId)
                            .sessionId(request.getSession().getId())
                            .sourcePath("[JoinController.joinStep:899]").build();
					
				gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);

			
				}catch(Exception ex) {
					log.error(ex.getMessage());
				}
			}
		}
		
		WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
		
		return "redirect:" + authUrl;
	}

	/**
	 * 
	 * <pre>
	 * comment  : SNS offline 가입 (카카오만 해당) 
	 * author   : hkdang
	 * date     : 2020. 11. 23. 오전 11:32:45
	 * </pre>
	 * 
	 * @param snsAuthUserVo
	 * @param state (ex) )
	 * @param locale
	 * @return
	 */
	public boolean isAgreeTerms(List<String> allowedTermsList, String checkTerms) {

		for (String allowedTerms : allowedTermsList) {
			if (allowedTerms.equals(checkTerms)) {
				return true;
			}
		}

		return false;
	}

	public String snsJoinOffline(SnsAuthUserVo snsAuthUserVo, final Locale locale, final Model model) {

		// 카카오정보 기반으로 개인정보 셋팅할것임
		SnsKakaoAccount kakaoAccount = snsAuthUserVo.getProfileResponse().getKakaoAccount();
		String ci = kakaoAccount.getCi();
		String realName = kakaoAccount.getLegal_name();
		String gender = kakaoAccount.getLegal_gender().equalsIgnoreCase("male") ? "M" : "F";
		String birth = kakaoAccount.getLegal_birth_date().replace("-", "").replace(" ", "").replace(":", "");
		String phoneNumber = kakaoAccount.getPhone_number();

		Phone phone = new Phone.Builder() //
				.phoneNumber(phoneNumber) //
				.countryCode(locale.getCountry()) //
				.build();
		phoneNumber = phone.displayNationalPhoneNumber().replaceAll("-", "");

		// 데이터 셋팅
		SnsOfflineParam snsOfflineParam = (SnsOfflineParam) WebUtil.getSession(OmniConstants.SNS_OFFLINEPARAM);
		log.debug("▶▶▶▶▶▶ [SNS Join Offline] snsOfflineParam : {}", StringUtil.printJson(snsOfflineParam));

		String successUrl = snsOfflineParam.getReturnUrl();
		String failUrl = StringUtils.isEmpty(snsOfflineParam.getCancelUrl()) ? successUrl : snsOfflineParam.getCancelUrl();
		log.debug("▶▶▶▶▶▶ [SNS Join Offline] returnURL : {}, cancelURL : {}", successUrl, failUrl);

		String chCd = snsOfflineParam.getChCd();
		if (StringUtils.isEmpty(chCd)) {
			chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		}

		// 이미 오프라인 회원인지 체크
		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setChCd(chCd);
		custInfoVo.setCiNo(ci);

		CustYnResponse custynResponse = this.customerApiService.getCustYn(custInfoVo);
		if ("ICITSVCOM000".equals(custynResponse.getRsltCd())) { // 이미 가입

			String incsNo = custynResponse.getCicuemCuYnVo().getIncsNo();
			log.debug("▶▶▶▶▶▶ [SNS Join Offline] Already Join incsNo : {}, redirectUrl : {}", incsNo, successUrl);

			/*
			 * model.addAttribute("incsNo", incsNo); model.addAttribute("home", successUrl);
			 * 
			 * return "join/apply_finish_off_redirect";
			 */

			return "redirect:" + successUrl;
		}

		// model.addAttribute("offParam", snsOfflineParam);

		// SNS 약관 동의 목록 기반으로 약관 셋팅
		// 체크 약관 (뷰티포인트 + 마케팅(뷰티포인트+경로))
		List<String> allowedTermsList = snsAuth.getSnsAllowedTerms(snsAuthUserVo.getSnsType(), snsAuthUserVo.getToken());

		// 체크 약관 (뷰티포인트 + 마케팅(뷰티포인트+경로))
		String bpTerms = this.config.getTermsTags();
		String bpMarketingTag = this.config.getMarketingTermsTag("030");
		String chMarketingTag = this.config.getMarketingTermsTag(chCd);
		String chPrivacyTag = this.config.getPrivacyTermsTag(chCd);
		String[] bpTermsArray = bpTerms.split(",");

		// 약관 컬렉션
		List<Marketing> joinMarketings = new ArrayList<>();
		List<Terms> joinBpTerms = new ArrayList<>();

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
			terms.setChgChCd(chCd);
			joinBpTerms.add(terms);
		}

		// 뷰티포인트 문자 수신 동의
		Marketing bpMarketing = new Marketing();
		bpMarketing.setChCd("000");
		bpMarketing.setSmsAgree(isAgreeTerms(allowedTermsList, bpMarketingTag) ? "Y" : "N");
		joinMarketings.add(bpMarketing);

		// 경로 문자 수신 동의
		Marketing chMarketing = new Marketing();
		chMarketing.setChCd(chCd);
		chMarketing.setSmsAgree(isAgreeTerms(allowedTermsList, chMarketingTag) ? "Y" : "N");
		joinMarketings.add(chMarketing);

		// joinRequest Setting
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setBpterms(joinBpTerms);
		joinRequest.setMarketings(joinMarketings);

		joinRequest.setUnm(realName);
		// joinRequest.setIncsno(snsAuthUserVo.getIncsNo());
		// joinRequest.setLoginid(snsAuthUserVo.getLoginId());
		joinRequest.setGender(gender);
		joinRequest.setPhone(phoneNumber);
		joinRequest.setBirth(birth);
		joinRequest.setCi(ci);
		joinRequest.setChcd(chCd);
		joinRequest.setNational("K");

		// TODO 카카오톡 채널 수신동의(공개 ID 기준)
		// default로 2개의 선택항목으로 구성되어 있습니다.
		// 그 중 1개는 위와 같이 "아모레퍼시픽 뷰티포인트의 광고와 마케팅 메시지를 카카오톡으로 받습니다" 이며,
		// 다른 1개는 진입한 채널에 따라 다릅니다.
		joinRequest.setSnsIdPrcnCd("KA");
		joinRequest.setKkoIntlOptiYn("Y");
		joinRequest.setKkoIntlOptiDt(DateUtil.getCurrentDate());

		log.debug("▶▶▶▶▶▶ [SNS Join Offline] joinRequest : {}", StringUtil.printJson(joinRequest));

		// 통합 가입 API
		BaseResponse response = this.socialService.registIntegratedCustomerCallApi(joinRequest);

		// 통합 가입 실패 체크
		if (!response.getResultCode().equals(ResultCode.SUCCESS.getCode())) {

			String result = response.getResultCode();
			response = this.socialService.cancelIntegratedCustomerCallApi(joinRequest);
			log.info("▶▶▶▶▶▶ [SNS Join Offline] Integrated Customer Fail : {}, cancel response : {}, cancelUrl : {}", result, response.getResultCode(), failUrl);

			/*
			 * model.addAttribute("home", failUrl); return "join/apply_finish_off_redirect";
			 */

			return "redirect:" + failUrl;
		}

		// 경로 offline 가입 API call
		response = this.socialService.registIntegrateOfflineChannelCustomer(joinRequest); // offline intergrate
		if (response.getResultCode().equals(ResultCode.SUCCESS.getCode())) {
			
			List<CicuedCuChArrayTcVo> cicuedCuChArrayTcVoList = new ArrayList<CicuedCuChArrayTcVo>();
			cicuedCuChArrayTcVoList = (List<CicuedCuChArrayTcVo>) WebUtil.getSession(OmniConstants.EXIST_CUSTOMER);
			boolean channeljoin = true;
			for (CicuedCuChArrayTcVo cicuedCuChArrayTcVo : cicuedCuChArrayTcVoList) {
				if(!StringUtils.isEmpty(cicuedCuChArrayTcVo.getChCd()) && joinRequest.getChcd().equals(cicuedCuChArrayTcVo.getChCd())) { //경로 가입이 안되어 있을 경우 api호출
					channeljoin=false;
				}
			}
			if(channeljoin) {
				response = this.socialService.registOfflineChannelCustomer(joinRequest); // offline create
			}
//			response = this.socialService.registOfflineChannelCustomer(joinRequest); // offline create
			if (response.getResultCode().equals(ResultCode.SUCCESS.getCode())) {

				// 가입 성공
				Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
				log.debug("▶▶▶▶▶▶ [SNS Join Offline] Create Success : {}, returnUrl : {}", StringUtil.printJson(customer), successUrl);

			if(!StringUtils.isEmpty(snsOfflineParam.getAddInfo()))	
				if (StringUtils.hasText(snsOfflineParam.getAddInfo())) {
					if (snsOfflineParam.getAddInfo().equalsIgnoreCase("Y")) {
						// return "join/apply_finish_off_redirect";
						return "redirect:" + successUrl;
					}
				}

				model.addAttribute("home", successUrl);
				model.addAttribute("incsNo", customer.getIncsNo());
				model.addAttribute("name", customer.getCustNm());
				return "join/apply_finish_off102";
			}
		}

		// 가입 실패 - 망취소 + 실패 후 이동 할 페이지
		BaseResponse cancelRes = this.socialService.cancelIntegrateOnlineChannelCustomer(joinRequest);
		log.info("▶▶▶▶▶▶ [SNS Join Offline] Create FailCode : {}, CancelCode : {}", response.getResultCode(), cancelRes.getResultCode());
		/*
		 * model.addAttribute("home", failUrl); return "join/apply_finish_off_redirect";
		 */

		return "redirect:" + failUrl;
	}

	/**
	 * 
	 * <pre>
	 * comment  : SNS Login
	 * author   : hkdang
	 * date     : 2020. 9. 17. 오후 6:31:02
	 * </pre>
	 * 
	 * @param snsType
	 * @param param
	 * @return
	 */
	public String snsLogin(SnsAuthUserVo snsAuthUserVo, final Locale locale, final Model model, final HttpServletRequest request, final HttpServletResponse response) {

		SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
		log.debug("▶▶▶▶▶▶ [SNS Login] sns Param : {}", StringUtil.printJson(ssoParam));
		
		
		if(Objects.isNull(ssoParam)) {
				
		    log.error("▶▶▶▶▶▶   SSOParam Object is null");
			model.addAttribute("oauthErrorCode", "9999");
			model.addAttribute("oauthErrorMsg", "경로코드가 존재하지 않습니다.");
			model.addAttribute("message", "현재 서비스가 원할하지 않습니다.<br>잠시 후 다시 시도해주시기 바랍니다.");

			return "wso2/oauth2_error";
		}

		final Channel channel = this.commonService.getChannel(ssoParam.getChannelCd());
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		
		final Map<String,String> gaCookieMap = gataggingUtils.getGaCookieMap(request); //ga get cookie	 
		
		final String snsId = snsAuthUserVo.getSnsId();
		final String snsType = snsAuthUserVo.getSnsType();

		String mappingId = this.wso2Service.getUserNameBySnsInfo(snsType, snsId);
		// 매핑 되어 있으면 (해당 로직은 네이버,페이스북, 애플만 체크 - 카카오는 로그인시에도 가입체크 해야함)
		if (!StringUtils.isEmpty(mappingId)) {
			
			try {

				log.debug("▶▶▶▶▶▶  GA Tagging LOGIN START (/authenticated/sns/ SNS({}) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",snsAuthUserVo.getSnsType(),gaCookieMap.get(OmniGaTaggingConstants.CID)
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
                           .sessionId(request.getSession().getId())
						   .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
						   .loginId(snsAuthUserVo.getLoginId())
						   .build();
				
				
				gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);
  
			} catch (Exception ex) {
				log.error(ex.getMessage());
			}
			
			log.debug("▶▶▶▶▶▶ [already mapping] snsType: {}, snsId: {}, mappingId: {}", snsType, snsId, mappingId);

			String authenticatorList = ssoParam.getAuthenticators();
			String authenticator = OmniUtil.getAuthenticator(authenticatorList, snsType);

			String authUrl = this.commonAuthUrl + "?idp=" + snsType + "&authenticator=" + authenticator + "&sessionDataKey=" + WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION) + "&multiOptionURI="
					+ WebUtil.getUrlEncodedSsoParams();

			boolean autologin = "Y".equals(WebUtil.getStringSession(OmniConstants.AUTO_LOGIN_SESSION));
			if (autologin) {
				authUrl = authUrl + "&chkRemember=on";
			}
			
			if("AP".equals(snsType)) { // 애플로그인은 code 값으로 로그인
				String snsAuthCode = (String) WebUtil.getSession(OmniConstants.SNS_AUTH_CODE);
				if(StringUtils.isEmpty(snsAuthCode)) {
					return "redirect:/login?" + WebUtil.getSsoParams();
				}
				try {
					authUrl += "&code=" + URLEncoder.encode(SecurityUtil.setXyzValue(snsAuthCode), StandardCharsets.UTF_8.name()) + "&isEncryption=true";
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// 추가 정보 세팅
			UmOmniUser omniUser = this.mgmtService.getOmniUserByLoginUserName(mappingId);
			log.debug("▶▶▶▶▶▶ [SNS Login] omniUser: {}", StringUtil.printJson(omniUser));

			// 탈퇴 계정 체크
			if (StringUtils.hasText(omniUser.getAccountDisabled()) && omniUser.getAccountDisabled().equalsIgnoreCase("true")) {
				model.addAttribute("withdrawDate", DateUtil.getBirthDate(omniUser.getDisabledDate()));
				model.addAttribute("home", channel.getHmpgUrl());
				model.addAttribute("homeurl", channel.getHmpgUrl());
				return "mgmt/restrict_withdraw";
			}

			// 세션에 ID 셋팅 후 auth
			String incsNo = omniUser.getIncsNo();
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(mappingId));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

			String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
			
			String profile = this.systemInfo.getActiveProfiles()[0];
			profile = StringUtils.isEmpty(profile) ? "dev" : profile;
			profile = profile.equalsIgnoreCase("default") ? "dev" : profile;

			// 휴면계정 활성화
			boolean isDormancy = this.joinService.isDormancyCustomer(incsNo);
			if (isDormancy) {
				boolean rtn = this.joinService.releaseDormancyCustomer(incsNo, chCd);
				if(!rtn) {
					
					try {
						
						log.debug("▶▶▶▶▶▶  GA Tagging LOGIN START FAIL (/authenticated/sns/ SNS({}) 시스템 오류 : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",snsAuthUserVo.getSnsType(),gaCookieMap.get(OmniGaTaggingConstants.CID)
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
								   .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_FAIL)
								   .errorMessage("시스템 오류")
		                           .sendFlag("Y")
		                           .incsNo(incsNo)
		                           .sessionId(request.getSession().getId())
								   .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]").build();

						gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);
					
					} catch (Exception ex) {
						log.error(ex.getMessage());
					}
					
					throw new OmniException("시스템 오류가 발생하였습니다. 관리자에게 문의하시기 바랍니다.");
				}
			}

			// 최근 로그인
			WebUtil.setCookies(response, OmniConstants.LAST_LOGIN_TYPE, snsType.toLowerCase());

			// 약관 체크
			if (snsType.equalsIgnoreCase(SnsType.KAKAO.getType())) {

				snsAuthUserVo.setIncsNo(incsNo);
				snsAuthUserVo.setLoginId(mappingId);

				String withdrawJsp = doSnsTermsSync(snsAuthUserVo, model, locale);
				if (StringUtils.hasText(withdrawJsp)) {
					return withdrawJsp;
				}

			} else { // 카카오 아니면 우리 페이지 띄워주자
				String termsUrl = getChTermsUrl(omniUser, locale, model);
				if (StringUtils.hasText(termsUrl)) {
					log.debug("▶▶▶▶▶▶ [SNS Login] terms URL : {}", termsUrl);

					SnsParam snsParam = new SnsParam();
					snsParam.setSnsId(snsId);
					snsParam.setSnsType(snsType);
					snsParam.setChcd(chCd);
					snsParam.setIncsNo(incsNo);
					snsParam.setLoginId(mappingId);
					snsParam.setUserName(omniUser.getFullName());

					WebUtil.setSession(OmniConstants.SNSPARAM, snsParam);
					WebUtil.setSession(OmniConstants.SNS_FIRST_MAPPING_SNS_ID, snsId);
					WebUtil.setSession(OmniConstants.SNS_FIRST_MAPPING_SNS_TYPE, snsType);

					WebUtil.setSession(OmniConstants.SNS_TERMS_AFTER_AUTH_URL, authUrl);

					return termsUrl;
				}
			}
			
			// 해당 경로시스템의 수신 동의 여부가 없을 경우 고객통합에 경로 가입 여부 확인 후 경로 가입 처리 hjw0228
			// 브랜드 사이트의 경우 처리하지 않음 2021-08-03 hjw0228
			if(!config.isBrandSite(channel.getChCd(), profile)) {
				TermsVo termsVo = new TermsVo();
				termsVo.setChCd(chCd);
				List<TermsVo> termsVos = this.termsService.getCorpTerms(termsVo);
				if(termsVos == null || termsVos.size() == 0) {
					CustInfoVo custInfoVo = new CustInfoVo();
					custInfoVo.setIncsNo(incsNo);
					custInfoVo.setChCd(chCd);

					CustYnResponse custYnResponse = this.customerApiService.getCustYn(custInfoVo);
					
					if(custYnResponse != null && "ICITSVCOM001".equals(custYnResponse.getRsltCd())) { // 경로 가입 되어 있지 않으면 경로 가입 API 호출
						log.debug("고객통합 경로 등록 API {} : {}", chCd, mappingId);

						CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedChannelCustomerData(channel, incsNo, mappingId, omniUser.getUmUserPassword());

						log.debug("▶▶▶▶▶▶ integrated channel customer api data : {}", StringUtil.printJson(chCustRequest));

						CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
						log.debug("▶▶▶▶▶▶ integrated channel {} customer api response : {}", channel.getChCdNm(), StringUtil.printJson(chjoinResponse));

						// 경로 고객 존재하는 경우도 성공으로 판단
						boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse.getRsltCd());

						log.info("▶▶▶▶▶▶ integrated channel result : {} ---> {}", chjoinResponse.getRsltCd(), success);
					}
				}	
			}

			log.debug("▶▶▶▶▶▶ [SNS Login] Complete mappingId : {}", mappingId);
			log.debug("▶▶▶▶▶▶ [SNS Login] Complete authUrl : {}", authUrl);
			
			
			 //####################### ga tagging 네이버/페북 로그인 완료처리 ###################
			try {
				
				log.debug("▶▶▶▶▶▶  GA Tagging LOGIN START SUCCESS (/authenticated/sns/ SNS({}) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",snsAuthUserVo.getSnsType(),gaCookieMap.get(OmniGaTaggingConstants.CID)
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
                        .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_SUCCESS)
                        .sendFlag("Y")
                        .incsNo(incsNo)
                        .sessionId(request.getSession().getId())
                        .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]").build();
				
			    
				gataggingUtils.googleGaTaggingDirectLoginPushApi(gaTagDto);
		
			}catch(Exception ex) {
				log.error(ex.getMessage());
			}

			// wso2 common auth
			WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
			return "redirect:" + authUrl;
		}

		/*
		 * // 로그인페이지로 넘기면(/login) session.invalidate() 이후에 SSOParam 에 snsId, snsType 매핑되어 세션에 넣어준다.
		 * WebUtil.setSession(OmniConstants.SNS_FIRST_MAPPING_SNS_ID , snsId); WebUtil.setSession(OmniConstants.SNS_FIRST_MAPPING_SNS_TYPE,
		 * snsType);
		 */
		log.debug("▶▶▶▶▶▶ [SNS Login] NOT MAPPING snsType : {} snsId: {}", snsType, snsId);
		
		String snsAccesstoken = (String) WebUtil.getSession(OmniConstants.SNS_ACCESS_TOKEN);

		// 최근 로그인
		WebUtil.setCookies(response, OmniConstants.LAST_LOGIN_TYPE, snsType.toLowerCase());
		return "redirect:/login?" + WebUtil.getSsoParams() + "&snsId=" + snsId + "&mappingSnsType=" + snsType + "&snsAccesstoken=" + snsAccesstoken;
	}

	/**
	 * 
	 * <pre>
	 * comment  : SNS Mapping
	 * author   : hkdang
	 * date     : 2020. 9. 17. 오후 6:47:14
	 * </pre>
	 * 
	 * @param snsType
	 * @param param
	 * @param model
	 * @return
	 */
	public String snsMapping(SnsAuthUserVo snsAuthUserVo, final Locale locale) {

		final String snsId = snsAuthUserVo.getSnsId();
		final String snsType = snsAuthUserVo.getSnsType();

		// snsParam set
		SnsParam snsParam = new SnsParam();
		snsParam.setSnsType(snsType);
		snsParam.setSnsId(snsId);
		snsParam.setLoginId(WebUtil.getStringSession(OmniConstants.SNS_MAPPING_TRY_LOGIN_ID));
		snsParam.setIncsNo(WebUtil.getStringSession(OmniConstants.SNS_MAPPING_TRY_INCS_NO));

		String redirectUrl = WebUtil.getStringSession(OmniConstants.SNS_MAPPING_AFTER_REDIRECT_URL);
		String resultCode = ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode();
		if (!StringUtils.isEmpty(redirectUrl)) { // redirectUrl 은 api내에서 validationCheck 하지 않으므로 여기서 체크
			// mapping wso2Api call
			ApiResponse response = customerApiService.doSnsAssociate(snsParam);
			resultCode = response.getResultCode();
		}

		// session data remove
		WebUtil.removeSession(OmniConstants.SNS_MAPPING_TRY_LOGIN_ID);
		WebUtil.removeSession(OmniConstants.SNS_MAPPING_TRY_INCS_NO);
		WebUtil.removeSession(OmniConstants.SNS_MAPPING_AFTER_REDIRECT_URL);

		log.debug("▶▶▶▶▶▶ [snsMapping] snsType = {}, resultCode = {} ", snsType, resultCode);

		String queryStr = ((redirectUrl.indexOf("?") == -1) ? "?" : "&") + "snsType=" + snsType + "&resultCode=" + resultCode;
		log.debug("▶▶▶▶▶▶ [snsMapping response query] queryStr = {}", queryStr);

		return "redirect:" + redirectUrl + queryStr;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 마이페이지에서 SNS 연동 
	 * author   : hkdang
	 * date     : 2020. 9. 17. 오전 9:48:30
	 * </pre>
	 * 
	 * @param snsType
	 * @param loginId
	 * @return
	 */
	@PostMapping("/sns/auth")
	public String snsAuthenticate(final String snsType, final String loginId, final int incsNo, final String redirectUrl, final String isPopup, final Model model) {

		log.debug("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
		log.debug("▶▶▶▶▶▶ [snsAuthenticated] snsType : {}", snsType);
		log.debug("▶▶▶▶▶▶ [snsAuthenticated] loginId : {}", loginId);
		log.debug("▶▶▶▶▶▶ [snsAuthenticated] incsNo : {}", incsNo);
		log.debug("▶▶▶▶▶▶ [snsAuthenticated] redirectUrl : {}", redirectUrl);
		log.debug("▶▶▶▶▶▶ [snsAuthenticated] isPopup : {}", isPopup);
		log.debug("▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲");

		boolean validType = false;
		for (SnsType type : SnsType.values()) {
			if (type.getType().equalsIgnoreCase(snsType)) {
				validType = true;
				break;
			}
		}

		if (validType == false) {
			log.info("▶▶▶▶▶▶ [snsType validation fail] snsType : {}", snsType);
			return WebUtil.getRedirectUrlWithContextPath(redirectUrl);
		}

		WebUtil.setSession(OmniConstants.SNS_USE_TYPE, MAPPING);

		WebUtil.setSession(OmniConstants.SNS_MAPPING_TRY_LOGIN_ID, loginId);
		WebUtil.setSession(OmniConstants.SNS_MAPPING_TRY_INCS_NO, Integer.toString(incsNo));
		WebUtil.setSession(OmniConstants.SNS_MAPPING_AFTER_REDIRECT_URL, redirectUrl);
		WebUtil.setSession(OmniConstants.SNS_MAPPING_IS_POPUP, isPopup);
		WebUtil.setSession(OmniConstants.CANCEL_URI, redirectUrl);

		log.debug("▶▶▶▶▶▶ [snsAuthenticate] snsType : {}", snsType);

		switch (snsType) {
		case "NA":
			return WebUtil.getRedirectUrlWithContextPath("/sns/auth/naver");
		case "KA":
			return WebUtil.getRedirectUrlWithContextPath("/sns/auth/kakao");
		default:
			return WebUtil.getRedirectUrlWithContextPath(snsAuth.getAuthorizeUrl(snsType, WebUtil.getStringSession(OmniConstants.CH_CD_SESSION)));
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 네이버는 리퍼러 체크 이슈가 있다. 앱에 설정한 도메인에서 auth를 요청해야한다.
	 *   		   즉, 경로페이지에서 바로 auth 호출이 어려워 아래 브릿지페이지를 거쳐가도록 한다. 
	 * author   : hkdang
	 * date     : 2020. 10. 30. 오전 10:07:28
	 * </pre>
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/sns/auth/naver")
	public String snsNaverBridge(final Model model) {

		model.addAttribute("authUrl", snsAuth.getAuthorizeUrl(SnsType.NAVER.getType(), ""));
		
		return "sns/auth_naver";
	}

	/**
	 * 
	 * <pre>
	 * comment  : 퍼머링크 발급 받을 브릿지페이지 (QR코드, 모바일 대응) 
	 * author   : hkdang
	 * date     : 2020. 11. 25. 오후 8:58:26
	 * </pre>
	 * 
	 * @param model
	 * @param query
	 * @return
	 */
	@GetMapping("/sns/auth/kakao")
	public String snsKakaoBridge(final HttpServletRequest request, final Model model, SnsOfflineParam snsOfflineParam) {

		String chCd = "";

		if (StringUtils.hasText(snsOfflineParam.getChCd())) { // SnsOfflineParam 넘어오면 오프라인
			WebUtil.setSession(OmniConstants.SNS_USE_TYPE, JOIN_OFF);

			log.debug("▶▶▶▶▶▶ [SNS Offline Join] snsOfflineParam : {}", StringUtil.printJson(snsOfflineParam));
			WebUtil.setSession(OmniConstants.SNS_OFFLINEPARAM, snsOfflineParam);

			chCd = SecurityUtil.clearXSSNormal(snsOfflineParam.getChCd());
			WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);

			model.addAttribute("state", snsOfflineParam.getChCd());
			model.addAttribute("chCd", snsOfflineParam.getChCd());
		} else {

			chCd = SecurityUtil.clearXSSNormal(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
			model.addAttribute("state", chCd);
			model.addAttribute("chCd", chCd);
		}

		log.info("▶▶▶▶▶▶ [SNS KAKAO Bridge page] access chCd : {}", chCd);
		
		// 카카오 임베디드 파라미터체크 2021-09-01
		if(!StringUtils.isEmpty(request.getParameter("kakaoEmbedded"))) {
			model.addAttribute("kakaoEmbedded", request.getParameter("kakaoEmbedded"));
		}
		
		model.addAttribute("sdkKey", snsAuth.getKey(SnsType.KAKAO.getType(), "sdkkey"));
		model.addAttribute("callback", snsAuth.getKey(SnsType.KAKAO.getType(), "callback"));
		model.addAttribute("termTags", snsAuth.getTermTagListString(chCd));
		model.addAttribute("chPublicIds", snsAuth.getChPublicIds(chCd));
		model.addAttribute("snsUseType", WebUtil.getStringSession(OmniConstants.SNS_USE_TYPE));
		model.addAttribute("snsMappingIsPopup", WebUtil.getStringSession(OmniConstants.SNS_MAPPING_IS_POPUP));

		// 카카오 SDK 로 호출
		return "sns/auth_kakao";

		// kauth URL -> 모바일웹 TO 카카오 앱 이슈 때문에 SDK 사용
		// return "redirect:" + snsAuth.getAuthorizeUrl( SnsType.KAKAO.getType(), chCd );
	}
	
	/**
	 * <pre>
	 * comment  : Facebook SDK 호출을 위한 처리 페이지
	 * author   : hjw0228
	 * date     : 2021. 11. 9. 오전 10:44:29
	 * </pre>
	 * @param request
	 * @param model
	 * @return
	 */
	@PostMapping("/sns/auth/facebook")
	@ResponseBody
	public SnsEnvVo snsFacebookBridge(final HttpServletRequest request, final Model model) {
		
		WebUtil.setSession(OmniConstants.SNS_USE_TYPE, LOGIN);
		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		log.debug("▶▶▶▶▶▶ [SNS Login Setting Complete] snsType : FB, chCd : {}", chCd);
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		SnsEnvVo snsEnvVo = new SnsEnvVo();
		snsEnvVo.setRestApiKey(this.config.getSnsInfo(profile, SnsType.FACEBOOK.getType().toLowerCase(), "restkey"));
		snsEnvVo.setCallback(this.config.getSnsInfo(profile, SnsType.FACEBOOK.getType().toLowerCase(), "callback"));
		snsEnvVo.setSnsUseType(WebUtil.getStringSession(OmniConstants.SNS_USE_TYPE));
		log.debug("▶▶▶▶▶▶  [callback] : {}", snsEnvVo.getCallback());
		
		return snsEnvVo;
	}
	
	@GetMapping("/sns/auth/facebook")
	public String snsFacebookBridget(final HttpServletRequest request, final Model model) {
		
		WebUtil.setSession(OmniConstants.SNS_USE_TYPE, LOGIN);
		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		log.debug("▶▶▶▶▶▶ [SNS Login Setting Complete] snsType : FB, chCd : {}", chCd);
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		model.addAttribute("restApiKey",this.config.getSnsInfo(profile, SnsType.FACEBOOK.getType().toLowerCase(), "restkey"));
		model.addAttribute("callback",this.config.getSnsInfo(profile, SnsType.FACEBOOK.getType().toLowerCase(), "callback"));
		model.addAttribute("snsUseType",WebUtil.getStringSession(OmniConstants.SNS_USE_TYPE));
		model.addAttribute("chCd", chCd);
		
		log.debug("▶▶▶▶▶▶  [callback] : {}", this.config.getSnsInfo(profile, SnsType.FACEBOOK.getType().toLowerCase(), "callback"));
		
		return "sns/auth_facebook";
	}
	
	/**
	 * <pre>
	 * comment  : Apple SDK 호출을 위한 처리 페이지
	 * author   : hjw0228
	 * date     : 2022. 12. 16. 오후 15:41:21
	 * </pre>
	 * @param request
	 * @param model
	 * @return
	 */
	@PostMapping("/sns/auth/apple")
	@ResponseBody
	public SnsEnvVo snsAppleSDK(final HttpServletRequest request, final Model model) {
		
		WebUtil.setSession(OmniConstants.SNS_USE_TYPE, LOGIN);
		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		log.debug("▶▶▶▶▶▶ [SNS Login Setting Complete] snsType : AP, chCd : {}", chCd);
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		SnsEnvVo snsEnvVo = new SnsEnvVo();
		snsEnvVo.setRestApiKey(this.config.getSnsInfo(profile, SnsType.APPLE.getType().toLowerCase(), "restkey"));
		snsEnvVo.setState(this.config.getSnsInfo(profile, SnsType.APPLE.getType().toLowerCase(), "state"));
		snsEnvVo.setCallback(this.config.getSnsInfo(profile, SnsType.APPLE.getType().toLowerCase(), "callback"));
		snsEnvVo.setSnsUseType(WebUtil.getStringSession(OmniConstants.SNS_USE_TYPE));
		log.debug("▶▶▶▶▶▶  [callback] : {}", snsEnvVo.getCallback());
		
		return snsEnvVo;
	}	
	
	@GetMapping("/sns/auth/apple")
	public String snsAppleBridge(final HttpServletRequest request, final Model model) {
		
		WebUtil.setSession(OmniConstants.SNS_USE_TYPE, LOGIN);
		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		log.debug("▶▶▶▶▶▶ [SNS Login Setting Complete] snsType : AP, chCd : {}", chCd);
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		model.addAttribute("restApiKey",this.config.getSnsInfo(profile, SnsType.APPLE.getType().toLowerCase(), "restkey"));
		model.addAttribute("state",this.config.getSnsInfo(profile, SnsType.APPLE.getType().toLowerCase(), "state"));
		model.addAttribute("callback",this.config.getSnsInfo(profile, SnsType.APPLE.getType().toLowerCase(), "callback"));
		model.addAttribute("snsUseType",WebUtil.getStringSession(OmniConstants.SNS_USE_TYPE));
		model.addAttribute("chCd", chCd);
		
		return "sns/auth_apple";
	}

	/**
	 * 
	 * <pre>
	 * comment  : 로그인 페이지에서 SNS 로그인
	 * author   : hkdang
	 * date     : 2020. 9. 17. 오후 7:46:03
	 * </pre>
	 * 
	 * @param snsType
	 * @return
	 */
	@GetMapping("/sns/login_start")
	public String loginSns(final Model model, @RequestParam(value = "snsType") String snsType, @RequestParam(value = "autoLoginOption", required = false) String autoLoginOption, @RequestParam(value = "kakaoEmbedded", required = false) String kakaoEmbedded, 
			final HttpServletRequest request
			) {
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		// 모바일인 경우만 처리 - 자동로그인 옵션 선택 - commonauth 에 자동 로그인 처리옵션 추가
		boolean isMobile = WebUtil.isMobile();
		log.info("▶▶▶▶▶ [sns login] auto login isMobile : {}, autoLoginOption : {}", isMobile, autoLoginOption);
		if (isMobile) {
			if (StringUtils.hasText(autoLoginOption) && "Y".equals(autoLoginOption)) {
				log.info("▶▶▶▶▶ [sns login] auto login set...");
				WebUtil.setSession(OmniConstants.AUTO_LOGIN_SESSION, "Y");
			}
		}

		//TODO 로그인
		WebUtil.setSession(OmniConstants.SNS_USE_TYPE, LOGIN);
		
		//googleGataggingUtils.gaTaggingResetSetSessions("휴대폰","휴대폰"); //reset el :이벤트타입,cd25 : 로그인 방법 

		String chCd = SecurityUtil.clearXSSNormal(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		log.debug("▶▶▶▶▶▶ [SNS Login Setting Complete] snsType : {}, chCd : {}", snsType, chCd);
		
		
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
			log.error(e.getMessage(), e);
		}
		
		
		String queryString = "";

		if(StringUtils.hasText((String) state.get("cancelUri"))) {
			queryString+= "&cancelUri="+(String) state.get("cancelUri");
		}
		if(StringUtils.hasText(request.getParameter("popup"))){
			queryString+="&popup="+request.getParameter("popup");
		}
		if(StringUtils.hasText(kakaoEmbedded)) {
			queryString+="&kakaoEmbedded="+kakaoEmbedded;
		}
		
		// 카카오 SDK 로 호출
		if (snsType.equalsIgnoreCase(SnsType.KAKAO.getType())) {
			if(StringUtils.hasText(queryString)) {
				return WebUtil.getRedirectUrlWithContextPath("/sns/auth/kakao?channelCd="+chCd+queryString);				
			}else {
				return WebUtil.getRedirectUrlWithContextPath("/sns/auth/kakao?channelCd="+chCd);
			}
		}
		
		// 페이스북 SDK 로 호출 (2021.11.08)
		if (snsType.equalsIgnoreCase(SnsType.FACEBOOK.getType())) {
			if(StringUtils.hasText(queryString)) {
				return WebUtil.getRedirectUrlWithContextPath("/sns/auth/facebook?channelCd="+chCd+queryString);				
			} else {
				return WebUtil.getRedirectUrlWithContextPath("/sns/auth/facebook?channelCd="+chCd);
			}
		}
		
		// 애플 SDK 로 호출 (2022.08.22)
		if (snsType.equalsIgnoreCase(SnsType.APPLE.getType())) {
			if(StringUtils.hasText(queryString)) {
				return WebUtil.getRedirectUrlWithContextPath("/sns/auth/apple?channelCd="+chCd+queryString);				
			} else {
				return WebUtil.getRedirectUrlWithContextPath("/sns/auth/apple?channelCd="+chCd);
			}			
		}

		return "redirect:" + snsAuth.getAuthorizeUrl(snsType, chCd);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 회원가입 페이지에서 카카오싱크로 가입
	 * author   : hkdang
	 * date     : 2020. 9. 23. 오전 11:54:15
	 * </pre>
	 * 
	 * @return
	 */
	@GetMapping("/sns/kakaosync_join")
	public String kakaosyncJoin(final Model model,final HttpServletRequest request) {

		WebUtil.setSession(OmniConstants.SNS_USE_TYPE, JOIN);

		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		log.debug("▶▶▶▶▶▶ [SNS JOIN KAKAO-SYNC] chCd : {}", chCd);
		
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
			log.error(e.getMessage(), e);
		}
		String queryString = "";
		if(StringUtils.hasText((String) state.get("cancelUri"))) {
			queryString+= "&cancelUri="+(String) state.get("cancelUri");
		}
		if(StringUtils.hasText(request.getParameter("popup"))){
			queryString+="&popup="+request.getParameter("popup");
		}
		

		// sns/auth/kakao 페이지 팝업으로 바로 띄워주는것으로 변경
		if(StringUtils.hasText(queryString)) {
			return WebUtil.getRedirectUrlWithContextPath("/sns/auth/kakao?channelCd="+chCd+queryString);
		}else {
			return WebUtil.getRedirectUrlWithContextPath("/sns/auth/kakao?channelCd="+chCd);
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : SNS에서 동의한 약관 DB 동기화 
	 * author   : hkdang
	 * date     : 2020. 10. 29. 오후 6:12:34
	 * </pre>
	 * 
	 * @param token
	 * @param incsNo
	 * @param snsType
	 * @return
	 */
	public String doSnsTermsSync(SnsAuthUserVo snsAuthUserVo, final Model model, final Locale locale) {

		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = commonService.getChannel(chCd);
		log.debug("▶▶▶▶▶▶ [doSnsTermsSync] chCd : {}, SnsAuthUserVo : {}", chCd, StringUtil.printJson(snsAuthUserVo));
		StopWatch stopWatch = new StopWatch("SNS(authenticated get action)");

		UmOmniUser umOmniUser = new UmOmniUser();
		umOmniUser.setChCd(chCd);
		umOmniUser.setIncsNo(snsAuthUserVo.getIncsNo());

		// 필수약관 모두 동의시 이미 가입으로 간주 return
		boolean isTerms = this.termsService.hasTermsAgree(umOmniUser);
		boolean isCorp = this.termsService.hasCorpTermsAgree(umOmniUser);
		if (isTerms && isCorp && !OmniUtil.isOffline(channel)) {
			return null;
		}

		// 계정 정보
		SnsKakaoAccount kakaoAccount = snsAuthUserVo.getProfileResponse().getKakaoAccount();

		// long snsId = profile.getId();
		String ci = kakaoAccount.getCi();
		String realName = kakaoAccount.getLegal_name();
		String gender = kakaoAccount.getLegal_gender().equalsIgnoreCase("male") ? "M" : "F";
		String birth = kakaoAccount.getLegal_birth_date().replace("-", "").replace(" ", "").replace(":", "");
		String phoneNumber = kakaoAccount.getPhone_number();
		// String email = kakaoAccount.getEmail();

		Phone phone = new Phone.Builder() //
				.phoneNumber(phoneNumber) //
				.countryCode(locale.getCountry()) //
				.build();
		phoneNumber = phone.displayNationalPhoneNumber().replaceAll("-", "");

		// 동의하지 않은 약관이 있으면, SNS 약관 동의 목록 기반으로 약관 셋팅
		stopWatch.start("Social Login Step - 06  : KA Get SnsAllowedTerms");
		List<String> termTagsList = snsAuth.getSnsAllowedTerms(snsAuthUserVo.getSnsType(), snsAuthUserVo.getToken());
		stopWatch.stop();
		log.info(stopWatch.prettyPrint());

		// 경로 개인정보 제3자 동의 약관 동의
		// List<Terms> joinBpTerms = new ArrayList<>();
		List<Terms> joinTerms = new ArrayList<>();
		TermsVo termsVo = new TermsVo();
		termsVo.setChCd(chCd);
		List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
		if (corpTerms != null && !corpTerms.isEmpty()) {
			for (TermsVo vo : corpTerms) {
				Terms terms = new Terms();
				terms.setIncsNo(Integer.parseInt(snsAuthUserVo.getIncsNo()));
				terms.setTncAgrYn("Y");
				terms.setTncaChgDt(DateUtil.getCurrentDate());
				terms.setTcatCd(vo.getTcatCd());
				terms.setTncvNo(vo.getTncvNo());
				terms.setChgChCd(vo.getChCd());
				joinTerms.add(terms);
			}
		}

		// 경로 약관
		SnsTermsVo snsTermsVo = new SnsTermsVo();
		final boolean isOffline = OmniUtil.isOffline(channel);
		final String onlineChCd = ChannelPairs.getOnlineCd(channel.getChCd());
		if(isOffline && StringUtils.hasText(onlineChCd)) { // 오프라인으로 회원 가입 시 온라인 약관 태그 목록도 같이 조회
			snsTermsVo.setChCd(onlineChCd);
		} else {
			snsTermsVo.setChCd(chCd);
		}
		
		snsTermsVo.setTermsTag(termTagsList);

		List<TermsVo> chTermsList = this.termsService.getTermsByTags(snsTermsVo);
		if (chTermsList != null && !chTermsList.isEmpty()) {
			for (TermsVo chTerms : chTermsList) {
				Terms terms = new Terms();
				terms.setIncsNo(Integer.parseInt(snsAuthUserVo.getIncsNo()));
				terms.setTncAgrYn("Y");
				terms.setTncaChgDt(DateUtil.getCurrentDate());
				terms.setTcatCd(chTerms.getTcatCd());
				terms.setTncvNo(chTerms.getTncvNo());
				terms.setChgChCd(chTerms.getChCd());

				joinTerms.add(terms);
			}
		}

		// 마케팅 약관
		List<Marketing> marketings = new ArrayList<>();
		boolean isMarketingAgree = false;
		String marketingTag = ConfigUtil.getInstance().getMarketingTermsTag(chCd);
		for (String tag : termTagsList) {
			if (tag.equals(marketingTag)) {
				isMarketingAgree = true;
				break;
			}
		}

		Marketing marketing = new Marketing();
		marketing.setChCd(chCd);
		marketing.setSmsAgree(isMarketingAgree == true ? "Y" : "N");
		marketings.add(marketing);

		// 경로 가입
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setTerms(joinTerms);
		// joinRequest.setBpterms(joinBpTerms); // terms 에 다 넣음
		joinRequest.setMarketings(marketings);

		joinRequest.setUnm(realName);
		joinRequest.setIncsno(snsAuthUserVo.getIncsNo());
		joinRequest.setLoginid(snsAuthUserVo.getLoginId());
		joinRequest.setGender(gender);
		joinRequest.setPhone(phoneNumber);
		joinRequest.setBirth(birth);
		joinRequest.setCi(ci);
		joinRequest.setChcd(chCd);
		joinRequest.setNational("K");

		joinRequest.setSnsIdPrcnCd("KA");
		joinRequest.setKkoIntlOptiYn("Y");
		joinRequest.setKkoIntlOptiDt(DateUtil.getCurrentDate());
		
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		joinRequest.setIntegrateid("true");
		
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

		log.info("▶▶▶▶▶▶▶▶ [DO SNS Login Terms Sync] ChannelCreate Try Info : {}", StringUtil.printJson(joinRequest));

		// 오프라인 등록
		stopWatch.start("Social Login Step - 07  : KA registIntegrateOfflineChannelCustomer - offline");
		BaseResponse response = this.socialService.registIntegrateOfflineChannelCustomer(joinRequest); // offline intergrate
		if (response.getResultCode().equals(ResultCode.SUCCESS.getCode()) || response.getResultCode().equals(ResultCode.NOT_EXIST_OFFLINE.getCode())) {
			
			List<CicuedCuChArrayTcVo> cicuedCuChArrayTcVoList = new ArrayList<CicuedCuChArrayTcVo>();
			cicuedCuChArrayTcVoList = (List<CicuedCuChArrayTcVo>) WebUtil.getSession(OmniConstants.EXIST_CUSTOMER);
			boolean channeljoin = true;
			for (CicuedCuChArrayTcVo cicuedCuChArrayTcVo : cicuedCuChArrayTcVoList) {
				if(!StringUtils.isEmpty(cicuedCuChArrayTcVo.getChCd()) && joinRequest.getChcd().equals(cicuedCuChArrayTcVo.getChCd())) { //경로 가입이 안되어 있을 경우 api호출
					channeljoin=false;
				}
			}
			WebUtil.setSession(OmniConstants.IS_CREATE_CUST_CHANNEL_MEMBER, channeljoin);
			if(channeljoin) {
				response = this.socialService.registOfflineChannelCustomer(joinRequest); // offline create
			}
//			response = this.socialService.registOfflineChannelCustomer(joinRequest); // offline create
			if (!response.getResultCode().equals(ResultCode.SUCCESS.getCode())) {
				this.socialService.cancelIntegrateOfflineChannelCustomer(joinRequest); // offline create 실패시 망취소
			}

		} else {
			this.socialService.cancelIntegrateOfflineChannelCustomer(joinRequest); // offline intergrate 실패시 망취소
		}
		log.info("▶▶▶▶▶▶▶▶ [SNS Login] regist OFFline ChannelCustomer result : {} ", response.getResultCode());
		stopWatch.stop();
		log.info(stopWatch.prettyPrint());
		// 오프라인 성공 여부와 상관없이 온라인 등록
		// 오프라인 등록 이후에 채널코드 온라인껄로 다시 셋팅
		String onlineChannelCd = ChannelPairs.getOnlineCd(joinRequest.getChcd());
		if(!StringUtils.isEmpty(onlineChannelCd)) { //오프라인 채널만 존재하는 경로는 해당 값 null
			joinRequest.setChcd(onlineChannelCd);
			// joinRequest.setChcd(chCd);
			
			stopWatch.start("Social Login Step - 08  : KA registIntegrateOnlineChannelCustomer");
			response = this.socialService.registIntegrateOnlineChannelCustomer(joinRequest); // online intergrate
			stopWatch.stop();
			log.info(stopWatch.prettyPrint());
		}
		if (response.getResultCode().equals(ResultCode.SUCCESS.getCode())) {
			
			List<CicuedCuChArrayTcVo> cicuedCuChArrayTcVoList = new ArrayList<CicuedCuChArrayTcVo>();
			cicuedCuChArrayTcVoList = (List<CicuedCuChArrayTcVo>) WebUtil.getSession(OmniConstants.EXIST_CUSTOMER);
			boolean channeljoin = true;
			for (CicuedCuChArrayTcVo cicuedCuChArrayTcVo : cicuedCuChArrayTcVoList) {
				if(!StringUtils.isEmpty(cicuedCuChArrayTcVo.getChCd()) && joinRequest.getChcd().equals(cicuedCuChArrayTcVo.getChCd())) { //경로 가입이 안되어 있을 경우 api호출
					channeljoin=false;
				}
			}
			if(channeljoin) {
				stopWatch.start("Social Login Step - 09  : KA registIntegrateOfflineChannelCustomer - online");
				response = this.socialService.registOnlineChannelCustomer(joinRequest); // online create
				stopWatch.stop();
				log.info(stopWatch.prettyPrint());
			}
//			response = this.socialService.registOnlineChannelCustomer(joinRequest); // online create
			
			if (response.getResultCode().equals(ResultCode.SUCCESS.getCode())) { // 성공 후 약관 처리
				for (Terms term : joinRequest.getTerms()) {
					if (this.termsService.existTerms(term)) {
						log.info("▶▶▶▶▶▶▶▶ [SNS Terms] exist term : {}", StringUtil.printJson(term));
						term.setTncAgrYn("A"); // Y -> A 로 컨버팅
						this.termsService.mergeTerms(term);
						this.termsService.insertTermHist(term);
					}
				}
			} else {
				this.socialService.cancelIntegrateOnlineChannelCustomer(joinRequest); // online create 실패시 망취소
			}
		} else {
			this.socialService.cancelIntegrateOnlineChannelCustomer(joinRequest); // online intergrate 실패시 망취소
		}
		
		// 계열사의 경우 고객통합플랫폼에 개인정보 수집 및 이용동의(마케팅) 약관 정보 업데이트
		String chPrivacyTag = this.config.getPrivacyTermsTag(chCd);
		if(StringUtils.hasText(chPrivacyTag)) { // 
			CustTncaRequest custTncaRequest = new CustTncaRequest();
			List<CustTncaVo> custTncaVos = new ArrayList<>();
			CustTncaVo terms = new CustTncaVo();
			terms.setTcatCd(config.getTermsCode(chPrivacyTag));
			terms.setIncsNo(joinRequest.getIncsno());
			terms.setTncvNo(config.getTermsVersion(chPrivacyTag));
			terms.setTncAgrYn(isAgreeTerms(termTagsList, chPrivacyTag) ? "Y" : "N");
			terms.setLschId("OCP");
			terms.setChgChCd(joinRequest.getChcd());
			terms.setChCd(joinRequest.getChcd());
			custTncaVos.add(terms);
			CustTncaVo custTncaVoArr[] = custTncaVos.toArray(new CustTncaVo[custTncaVos.size()]);
			custTncaRequest.setCicuedCuTncaTcVo(custTncaVoArr);
			ApiResponse apiResponse = customerApiService.savecicuedcutnca(custTncaRequest);
		}

		log.info("▶▶▶▶▶▶▶▶ [SNS Login] regist ONline ChannelCustomer result : {} ", response.getResultCode());

		// 실패 이유가 탈퇴회원 인지
		if (response.getResultCode().equals(ResultCode.USER_DISABLED.getCode())) {
			log.debug("▶▶▶▶▶▶▶▶ [SNS Login] regist ONline withdraw date : {}", response.getMessage());

			model.addAttribute("home", channel.getHmpgUrl());
			model.addAttribute("homeurl", channel.getHmpgUrl());
			model.addAttribute("withdrawDate", DateUtil.getBirthDate(response.getMessage()));
			return "mgmt/restrict_withdraw";
		}

		return null;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 이미 매핑되어 있는 아이디의 경로 약관 체크 - 우리 페이지
	 * author   : hkdang
	 * date     : 2020. 10. 13. 오후 1:38:16
	 * </pre>
	 * 
	 * @return
	 */
	public String getChTermsUrl(UmOmniUser omniUser, final Locale locale, final Model model) {

		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		omniUser.setChCd(chCd);

		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, channel.getHmpgUrl());
		model.addAttribute("chcd", channel.getChCd());
		model.addAttribute("home", channel.getHmpgUrl());
		model.addAttribute("homeurl", channel.getHmpgUrl());

		log.debug("▶▶▶▶▶ [SNS Terms Checker] omniUser info : {}", StringUtil.printJson(omniUser));

		String username = "";
		if (!this.termsService.hasTermsAgree(omniUser)) { // 경로약관 미동의 상태 ME-FO-A0105
			log.debug("▶▶▶▶▶ [SNS Channel Terms] 통합회원으로 로그인 시 진입 채널에 약관동의가 되어있지 않은 경우, 통합회원전환, 약관동의 --> {}", LoginType.AGREE.getDesc());

			// 진입 채널에 대한 약관 출력 정보 조회
			TermsVo termsVo = new TermsVo();
			termsVo.setChCd(chCd);
			if(channel.getChCd().equals(OmniConstants.OSULLOC_CHCD)) {
				termsVo.setIncsNo(omniUser.getIncsNo());
			}
			List<TermsVo> termsList = this.termsService.getTerms(termsVo);
			model.addAttribute("terms", termsList);

			// 진입 채널 전사 양관 출력 정보 조회
			List<TermsVo> corpTermsList = this.termsService.getCorpTerms(termsVo);
			model.addAttribute("corptermslist", corpTermsList);

			username = omniUser.getFullName();
			log.debug("▶▶▶▶▶ [SNS Channel Terms] user full name : {}", username);
			String name = OmniUtil.maskUserName(username, locale);
			log.debug("▶▶▶▶▶ [SNS Channel Terms] user masking name : {}", name);

			final String id = omniUser.getUmUserName(); // 약관동의에서 아이디는 마스킹하지 않음.

			// 휴대전화번호는 고객통합을 조회
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(omniUser.getIncsNo());
			final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			log.debug("▶▶▶▶▶ [SNS Channel Terms] customer info : {}", StringUtil.printJson(customer));
			if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
				username = customer.getCustNm();
				name = OmniUtil.maskUserName(username, locale);
				final String mobileNo = StringUtil.mergeMobile(customer);
				log.debug("▶▶▶▶▶ [SNS Channel Terms] user mobile : {}", mobileNo);
				final String mobile = OmniUtil.maskMobile(mobileNo, locale);
				log.debug("▶▶▶▶▶ [SNS Channel Terms] user masking mobile : {}", mobile);
				model.addAttribute("mobile", mobile);
			} else {
				if (customer != null && "ICITSVCOM001".equals(customer.getRsltCd())) {
					log.debug("▶▶▶▶▶ [SNS Channel Terms] {}", "검색된 사용자 없음.");
					return null;
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

			WebUtil.setSession(OmniConstants.USERNM_SESSION, omniUser.getFullName());
			
			//20230404 개인정보 수집 및 이용 동의 (마케팅)
			UmOmniUser omniUser2 = new UmOmniUser();
			final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
			omniUser.setChCd(onlineChCd);
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

			return "terms/terms_apply"; // ME-FO-A0105
		}

		// 경로약관 동의 상태
		// ME-FO-A0214 오픈 후 최초 통합회원 로그인 > 신규 약관 동의 안내
		// 통합회원 고객 번호 존재, 채널 약관 동의 상태, 오픈 후 최초 로그인
		if (!this.termsService.hasCorpTermsAgree(omniUser)) { // 전사약관 미동의상태
			log.debug("▶▶▶▶▶ [SNS Corp Terms] 통합회원 고객 번호 존재, 채널 약관 동의 상태, 오픈 후 최초 로그인, 전사 약관동의 --> {}", LoginType.CORPAGREE.getDesc());

			// 진입 채널에 대한 약관 출력 정보 조회
			TermsVo termsVo = new TermsVo();
			termsVo.setChCd(chCd);
			List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
			model.addAttribute("terms", termsList);
			model.addAttribute("corpterms", true);

			log.debug("▶▶▶▶▶ [SNS Corp Terms] user full name : {}", omniUser.getFullName());
			String name = OmniUtil.maskUserName(omniUser.getFullName(), locale);
			log.debug("▶▶▶▶▶ [SNS Corp Terms] user masking name : {}", name);

			final String id = omniUser.getUmUserName(); // 약관동의에서 아이디는 마스킹하지 않음.

			// 휴대전화번호는 고객통합을 조회
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(omniUser.getIncsNo());
			final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			log.debug("▶▶▶▶▶ [SNS Corp Terms] customer info : {}", StringUtil.printJson(customer));
			if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
				username = customer.getCustNm();
				name = OmniUtil.maskUserName(username, locale);
				final String mobileNo = StringUtil.mergeMobile(customer);
				log.debug("▶▶▶▶▶ [SNS Corp Terms] user mobile : {}", mobileNo);
				final String mobile = OmniUtil.maskMobile(mobileNo, locale);
				log.debug("▶▶▶▶▶ [SNS Corp Terms] user masking mobile : {}", mobile);
				model.addAttribute("mobile", mobile);
			} else {
				if (customer != null && "ICITSVCOM001".equals(customer.getRsltCd())) {
					log.debug("▶▶▶▶▶ [SNS Corp Terms] {}", "검색된 사용자 없음.");
					return null;
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

			WebUtil.setSession(OmniConstants.USERNM_SESSION, omniUser.getFullName());

			return "terms/new_terms_apply"; // ME-FO-A0214
		}

		return null;
	}
	
	@GetMapping("/sns/confirm/facebook/callback/deletion")
	public String removeFacebookCallbackConfirm(final Model model, @RequestParam final String result) {
		
		log.debug("▶▶▶▶▶ [SNS Facebook Remove Confirm] customer info : {}", result);
		
		model.addAttribute("result", result);

		return "/sns/callback_facebook_rm";
	}
	
	@RequestMapping(value = "/sns/kakao/chatbot", method = { RequestMethod.GET, RequestMethod.POST })
	public String kakaoChatbot(final HttpServletRequest request, final HttpServletResponse response, final Model model, final ChatbotParam chatbotParam) throws UnsupportedEncodingException {
		log.info("▶▶▶▶▶▶ [/sns/kakao/chatbot] request : {}", StringUtil.printJson(chatbotParam));
		
		if (StringUtils.hasText(chatbotParam.getChCd())) {
			request.getSession(true).invalidate(); // 챗봇 진입 시 세션 초기화
			
			final String chCd = chatbotParam.getChCd();
			List<Channel> channels = this.commonService.getChannels();
			List<String> channelCds = new ArrayList<>();
			for (Channel channel : channels) {
				channelCds.add(channel.getChCd());
			}

			if (!channelCds.contains(chCd)) {
				
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.OMNI_JOIN_COMMON, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.AUTH_INVALID_PARAMETER);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정

				throw new OmniException("경로코드가 올바르지 않습니다. 정확한 경로인지 확인하세요.[" + chCd + "]");

			}
			
			final Channel channel = this.commonService.getChannel(chCd);
			log.debug("▶▶▶▶▶▶ [/sns/kakao/chatbot] channel info : {}", StringUtil.printJson(channel));
			
			WebUtil.setSession(OmniConstants.CH_CD_SESSION, channel.getChCd());
			WebUtil.setSession(OmniConstants.CHATBOT_LOGIN_SESSION, true);
			WebUtil.setSession(OmniConstants.CHATBOTPARAM, chatbotParam);
			
			if (StringUtils.hasText(chatbotParam.getReturnUrl())) {
				String returnUrl = URLDecoder.decode(chatbotParam.getReturnUrl(), StandardCharsets.UTF_8.name());
				returnUrl = HtmlUtils.htmlUnescape(returnUrl);
				chatbotParam.setReturnUrl(OmniUtil.getConvertUrl(returnUrl));
			} else {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.OMNI_JOIN_COMMON, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.AUTH_INVALID_PARAMETER);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				throw new OmniException("Return URL 파라미터가 누락되었습니다.");
			}
			if (StringUtils.hasText(chatbotParam.getCancelUrl())) {
				String cancelUrl = URLDecoder.decode(chatbotParam.getCancelUrl(), StandardCharsets.UTF_8.name());
				cancelUrl = HtmlUtils.htmlUnescape(cancelUrl);
				chatbotParam.setCancelUrl(OmniUtil.getConvertUrl(cancelUrl));
			} else {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.OMNI_JOIN_COMMON, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.AUTH_INVALID_PARAMETER);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				throw new OmniException("Cancel URL 파라미터가 누락되었습니다.");
			}
			
			log.debug("JoinPrtnId Required : {}, JoinEmpId Required : {}", this.config.isJoinPrtnCodeRequired(chatbotParam.getChCd()), this.config.isJoinEmpCodeRequired(chatbotParam.getChCd()));
			
			if(this.config.isJoinPrtnCodeRequired(chatbotParam.getChCd())) { // joinPrtnId 필수 체크인 경로시스템인 경우 (ex: eCris)
				if(StringUtils.isEmpty(chatbotParam.getJoinPrtnId())) {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.OMNI_JOIN_COMMON, null, null, null,
							LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoErr(OmniStdLogConstants.AUTH_EMPTY_JOIN_PRTN_ID);
					LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					
					throw new OmniException("매장ID가 존재하지 않습니다.");
				}
			}
			
			if(this.config.isJoinEmpCodeRequired(chatbotParam.getChCd())) { // joinEmpId 필수 체크인 경로시스템인 경우 (ex: eCris)
				if(StringUtils.isEmpty(chatbotParam.getJoinEmpId())) {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.OMNI_JOIN_COMMON, null, null, null,
							LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoErr(OmniStdLogConstants.AUTH_EMPTY_JOIN_EMP_ID);
					LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					
					throw new OmniException("엔젤ID가 존재하지 않습니다.");
				}
			}
			
			WebUtil.setSession(OmniConstants.SNS_USE_TYPE, JOIN);
			final SSOParam ssoParam = new SSOParam();
			ssoParam.setChannelCd(chCd);
			ssoParam.setRedirectUri(chatbotParam.getReturnUrl());
			ssoParam.setCancelUri(chatbotParam.getCancelUrl());
			WebUtil.setSession(OmniConstants.SSOPARAM, ssoParam);
			WebUtil.setSession(OmniConstants.CANCEL_URI, chatbotParam.getCancelUrl());
			
			model.addAttribute("sdkKey", snsAuth.getKey(SnsType.KAKAO.getType(), "sdkkey"));
			model.addAttribute("callback", snsAuth.getKey(SnsType.KAKAO.getType(), "callback"));
			model.addAttribute("termTags", snsAuth.getTermTagListString(chCd));
			model.addAttribute("chPublicIds", snsAuth.getChPublicIds(chCd));
			
		} else {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.OMNI_JOIN_COMMON, null, null, null,
					LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.AUTH_EMPTY_CHANNEL_CODE);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			
			throw new OmniException("경로코드가 존재하지 않습니다.");
		}
		
		return "/sns/chatbot_kakao";
	}
	
	
	//페이스북 테스트
	@GetMapping("/authenticated/sns/facebooksu")
	public void facebookSample(final HttpServletRequest request, final HttpServletResponse response, final Model model) throws IOException {

		// 비회원 주문/조회 활성화 여부 false면 비활성화
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		// Facebook SDK 사용을 위한 FBRestApiKey 전달
		model.addAttribute("FBRestApiKey", this.config.getSnsInfo(profile, SnsType.FACEBOOK.getType().toLowerCase(), "restkey"));
		
		String url = "";
		
		if("local".equals(profile)) {
			url ="https://localhost:9443/oauth2/authorize?channelCd=036&DrcLgnTp=fb&response_type=code&client_id=eOncFSQi66bAZaXofoanD5_8ZQoa&scope=openid&redirect_uri=https%3A%2F%2Fd2veos.innisfree.com%3A8901%2Foauth2client&state=%7Bdt%3Dnull%2C+redirectUri%3Dhttps%3A%2F%2Fd2veos.innisfree.com%3A8901%2Fkr%2Fko%2FMain.do%2C+popup%3Dnull%2C+gid%3Dnull%2C+channelCd%3D036%2C+ot%3Dnull%2C+cancelUri%3Dhttps%3A%2F%2Fd2veos.innisfree.com%3A8901%2Fkr%2Fko%2FMain.do%2C+kakaoEmbedded%3Dnull%2C+join%3Dnull%2C+prompt%3Dnull%2C+vt%3Dnull%2C+cid%3Dnull%7D";
		} else if ("dev".equals(profile)) {
			url ="https://dev-one-ap.amorepacific.com/oauth2/authorize?channelCd=036&DrcLgnTp=fb&response_type=code&client_id=eOncFSQi66bAZaXofoanD5_8ZQoa&scope=openid&redirect_uri=https%3A%2F%2Fd2veos.innisfree.com%3A8901%2Foauth2client&state=%7Bdt%3Dnull%2C+redirectUri%3Dhttps%3A%2F%2Fd2veos.innisfree.com%3A8901%2Fkr%2Fko%2FMain.do%2C+popup%3Dnull%2C+gid%3Dnull%2C+channelCd%3D036%2C+ot%3Dnull%2C+cancelUri%3Dhttps%3A%2F%2Fd2veos.innisfree.com%3A8901%2Fkr%2Fko%2FMain.do%2C+kakaoEmbedded%3Dnull%2C+join%3Dnull%2C+prompt%3Dnull%2C+vt%3Dnull%2C+cid%3Dnull%7D";
		} else if ("stg".equals(profile)) {
			url ="https://stg-one-ap.amorepacific.com/oauth2/authorize?channelCd=099&DrcLgnTp=fb&response_type=code&client_id=fEkulUL19EsdXFM1SQfifwBO4hYa&scope=openid&redirect_uri=https%3A%2F%2Fdev-www.aestura.com%2Foauth2client&state=%7Bdt%3Dnull%2C+redirectUri%3Dhttps%3A%2F%2Fdev-www.aestura.com%2Fweb%2Fmain.do%2C+popup%3Dnull%2C+channelCd%3D099%2C+ot%3Dnull%2C+cancelUri%3D%2C+join%3Dnull%2C+prompt%3Dnull%2C+vt%3Dnull%7D";
		} else if ("prod".equals(profile)) {
			url ="https://one-ap.amorepacific.com/oauth2/authorize?channelCd=036&DrcLgnTp=fb&response_type=code&client_id=rvC_4XPbX4P2y7nD9x5H67d4oX8a&scope=openid&redirect_uri=https%3A%2F%2Fwww.innisfree.com%2Foauth2client&state=%7Bdt%3Dnull%2C+redirectUri%3Dhttps%3A%2F%2Fwww.innisfree.com%2Fkr%2Fko%2FMain.do%2C+popup%3Dnull%2C+gid%3Dnull%2C+channelCd%3D036%2C+ot%3Dnull%2C+cancelUri%3Dhttps%3A%2F%2Fwww.innisfree.com%2Fkr%2Fko%2FMain.do%2C+kakaoEmbedded%3Dnull%2C+join%3Dnull%2C+prompt%3Dnull%2C+vt%3Dnull%2C+cid%3Dnull%7D";
		}
		
		response.sendRedirect(url);
	}
	
	
	@GetMapping("/sns/auth/fbsu")
	public String snsFacebookBridgetTest(final HttpServletRequest request, final Model model) {
		
		WebUtil.setSession(OmniConstants.SNS_USE_TYPE, LOGIN);
		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		log.debug("▶▶▶▶▶▶ [SNS Login Setting Complete] snsType : FBtest, chCd : {}", chCd);
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		model.addAttribute("restApiKey",this.config.getSnsInfo(profile, SnsType.FACEBOOK.getType().toLowerCase(), "restkey"));
		model.addAttribute("callback",this.config.getSnsInfo(profile, "fbt".toLowerCase(), "callback"));
		model.addAttribute("snsUseType",WebUtil.getStringSession(OmniConstants.SNS_USE_TYPE));
		model.addAttribute("chCd", chCd);
		return "sns/auth_facebook";
	}
	
	@GetMapping("/authenticated/sns/facebookfinish")
	public String facebookSampleFinish(final Model model, final HttpServletRequest request, @RequestParam Map<String, String> param) {
		String snsType = "FBT";
		
		SnsTokenVo snsTokenVo = new SnsTokenVo();
		snsTokenVo.setCode(param.get("code"));
		log.debug("▶▶▶▶▶▶ [param code] = {} ", param.get("code"));
		snsTokenVo.setState(SnsAuth.getNaverState());
		String token = "";
		
		if(param != null && !StringUtils.isEmpty(param.get("code"))) {
			// token + profile API call
			SnsTokenResponse snsTokenResponse = this.customerApiService.getSnsToken(snsType, snsTokenVo);
			token = snsTokenResponse.getAccessToken();
			snsTokenVo.setAccessToken(token);
		} else {
			token = (String) WebUtil.getSession(OmniConstants.SNS_ACCESS_TOKEN);
			snsTokenVo.setAccessToken(token);	
		}
		
//		token = snsTokenResponse.getAccessToken();
//		snsTokenVo.setAccessToken(token);
		log.debug("▶▶▶▶▶▶ [SNS AccessToken] = {} ", token);
		SnsProfileResponse snsProfileResponse = this.customerApiService.getSnsProfile(snsType, snsTokenVo);
		String snsId = snsType.equalsIgnoreCase(SnsType.NAVER.getType()) ? snsProfileResponse.getResponse().getId() : snsProfileResponse.getId();
		String name = snsType.equalsIgnoreCase(SnsType.NAVER.getType()) ? snsProfileResponse.getResponse().getName() : snsProfileResponse.getName();
		
		model.addAttribute("snsId", snsId);
		model.addAttribute("name", name);
		return "/sns/facebook_finish";
	}
}