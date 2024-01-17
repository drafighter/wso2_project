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
package com.amorepacific.oneap.auth.common;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.join.service.JoinService;
import com.amorepacific.oneap.auth.join.vo.JoinResponse;
import com.amorepacific.oneap.auth.mgmt.service.MgmtService;
import com.amorepacific.oneap.auth.terms.service.TermsService;
import com.amorepacific.oneap.auth.terms.vo.TermsVo;
import com.amorepacific.oneap.auth.util.CertUtil;
import com.amorepacific.oneap.common.types.JoinDivisionType;
import com.amorepacific.oneap.common.types.JoinType;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.LocaleUtil;
import com.amorepacific.oneap.common.util.ObjectUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.JoinStepVo;
import com.amorepacific.oneap.common.vo.ManualParam;
import com.amorepacific.oneap.common.vo.OAuth2Error;
import com.amorepacific.oneap.common.vo.OfflineParam;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.QrCode;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.TermsType;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.common 
 *    |_ CommonController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 3.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Controller
public class CommonController {

	@Autowired
	private CommonService commonService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private JoinService joinService;

	@Autowired
	private TermsService termsService;

	@Autowired
	private MgmtService mgmtService;

	@Autowired
	private CustomerApiService customerApiService;
	
	@Autowired
	private SystemInfo systemInfo;
	
	private static ConfigUtil config = ConfigUtil.getInstance();

	// http://127.0.0.1:8081/entry?chCd=006&joinPrtnId=S1004&joinPrtnNm=매장명&returnUrl=home.jsp&&addInfo=Y

	@RequestMapping(value = "/entry", method = { RequestMethod.GET, RequestMethod.POST })
	public String entry(final HttpServletRequest request, final HttpServletResponse response, final OfflineParam offlineParam, final Model model) throws UnsupportedEncodingException {
		log.info("▶▶▶▶▶▶ [entry] request : {}", StringUtil.printJson(offlineParam));
		if (StringUtils.hasText(offlineParam.getChCd())) {
			
			request.getSession(true).invalidate(); // 오프라인 진입 시 세션 초기화
			
			final String chCd = offlineParam.getChCd();
			final Channel channel = this.commonService.getChannel(chCd);
			log.debug("▶▶▶▶▶▶ [entry] channel info : {}", StringUtil.printJson(channel));
			boolean offline = OmniUtil.isOffline(channel);
			if (offline) {
				WebUtil.setSession(OmniConstants.CH_CD_SESSION, channel.getChCd());
				WebUtil.setSession(OmniConstants.OFFLINEPARAM, offlineParam);
				
				log.info("▶▶▶▶▶▶ [Request URI = /entry], Session ID : {}, User-Agent : {}", WebUtil.getRequest().getSession().getId(), request.getHeader("user-agent"));
				
				// 오프라인 진입 시 세션 타입아웃 방지를 위해 쿠키에 파라미터 저장 - 2021-04-14 hjw0228
				/*
				 * for(Field field : offlineParam.getClass().getDeclaredFields()) { field.setAccessible(true);
				 * 
				 * if(java.lang.String.class.equals(field.getType())) { try { // Field Value를 참조한다. Object value = field.get(offlineParam); if(value !=
				 * null) { WebUtil.setCookies(response, OmniConstants.ONE_AP_OFFLINE_PARAM +"-"+field.getName().toString(),
				 * SecurityUtil.setXyzValue(value.toString())); } else { WebUtil.setCookies(response, OmniConstants.ONE_AP_OFFLINE_PARAM
				 * +"-"+field.getName().toString(), null); }
				 * 
				 * } catch (IllegalAccessException e) { log.info("Reflection Error. {}", e); } } }
				 */

				if (HttpMethod.GET.name().equals(request.getMethod())) {
					return "redirect:/join" + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
				} else if (HttpMethod.POST.name().equals(request.getMethod())) {
					StringBuilder query = new StringBuilder();
					Map<String, Object> params = ObjectUtil.convertRequestToMap(request);
					int idx = 0;
					for (Map.Entry<String, Object> elem : params.entrySet()) {
						if (idx == 0) {
							query.append("?").append(elem.getKey()).append("=").append(URLEncoder.encode(elem.getValue().toString(), StandardCharsets.UTF_8.name()));
						} else {
							query.append("&").append(elem.getKey()).append("=").append(URLEncoder.encode(elem.getValue().toString(), StandardCharsets.UTF_8.name()));
						}
						idx++;
					}
					return "redirect:/join" + query.toString();
				}

			}
		}

		if (HttpMethod.POST.name().equals(request.getMethod())) {
			StringBuilder query = new StringBuilder();
			Map<String, Object> params = ObjectUtil.convertRequestToMap(request);
			int idx = 0;
			for (Map.Entry<String, Object> elem : params.entrySet()) {
				if (idx == 0) {
					query.append("?").append(elem.getKey()).append("=").append(URLEncoder.encode(elem.getValue().toString(), StandardCharsets.UTF_8.name()));
				} else {
					query.append("&").append(elem.getKey()).append("=").append(URLEncoder.encode(elem.getValue().toString(), StandardCharsets.UTF_8.name()));
				}
				idx++;
			}
			return "redirect:/login" + query.toString();
		}
		return "redirect:/login" + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
	}

	@GetMapping("/info")
	public String info(final HttpServletRequest request, final Model model, final Locale locale) {
		final String error = WebUtil.getStringParameter("error", "fail.message");
		if (StringUtils.hasText(error)) {
			String msg = this.messageSource.getMessage(error, null, locale);
			model.addAttribute("message", msg);
		}
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		return "info/info_error";
	}

	@GetMapping("/info-svc")
	public String infoSystem(final Model model) {
		// final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		// model.addAttribute(OmniConstants.CH_CD_SESSION, chCd);
		// final Channel channel = this.commonService.getChannel(chCd);
		// model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		// model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		// model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		// model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		return "info/info_service";
	}

	@GetMapping("/info-exist")
	public String infoExist(final Model model) {
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, channel.getHmpgUrl());
		return "info/info_exist";
	}
	
	@GetMapping("/info-api")
	public String infoApiError(final Model model) {
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;
				model.addAttribute("offline", true);
				model.addAttribute("home", OmniUtil.getOfflineRedirectUrl(channel));
				model.addAttribute("homeurl", OmniUtil.getOfflineRedirectUrl(channel));
				model.addAttribute(OmniConstants.RD_URL, OmniUtil.getOfflineRedirectUrl(channel));
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
		} else {
			model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		}
		
		return "info/api_page";
	}

	@RequestMapping(value = "/omni-terms-detail", method = { RequestMethod.GET, RequestMethod.POST })
	public String agreeDetail(final TermsType type, final Model model) {
		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		log.info("chCd : {}", chCd);
		log.info("type : {}", StringUtil.printJson(type));
		
		if (StringUtils.hasText(type.getChCd())) {
			chCd = type.getChCd();
		}
		List<String> channelCds = new ArrayList<>();
		final List<Channel> channels = this.commonService.getChannels();
		for (Channel channel : channels) {
			channelCds.add(channel.getChCd());
		}
		if (!channelCds.contains(chCd)) {
			chCd = "";
		} else {
			Channel channel = this.commonService.getChannel(chCd);
			if (OmniUtil.isOffline(channel)) {
				chCd = ChannelPairs.getOnlineCd(chCd);
				channel = this.commonService.getChannel(chCd);
			}
			model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
			model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		}
		
		log.debug("▶▶▶▶▶▶ {} agree detail {}", chCd, StringUtil.printJson(type));
		if (StringUtils.hasText(type.getType())) {
			if (type.getType().equals("C")) {
				return "info/info_use_agree"; // 개인정보 수집 이용 동의 info/info_use_agree C
			} else if (type.getType().equals("CO")) {
				return "info/info_use_agree_optional"; // 개인정보 수집 이용 동의 (선택) info/info_use_agree_optional CO
			} else if (type.getType().equals("P")) {
				return "info/info_offer_agree" + chCd; // 개인정보 제공동의 info/info_offer_agree P
			} else if (type.getType().equals("T")) {
				return "info/info_trans_agree"; // 국외이전 동의 info/info_trans_agree T
			} else if (type.getType().contentEquals("A")) { // 개인정보 제3자동의
				return "info/info_offer_agree";
			} else if (type.getType().contentEquals("M")) { // 개인정보 수집 및 이용 동의(마케팅) (선택)
				return "info/info_agree_choice";
			}else if (type.getType().contentEquals("N")) { // 개인정보 수집 및 이용동의 (마케팅)
				return "info/info_offer_choice";
			} else if (type.getType().contentEquals("O")) { // 개인정보 수집 및 이용동의 (마케팅)
				return "info/info_affi_agree";
			} else if (type.getType().contentEquals("Z")) { // 개인정보 수집 및 이용동의 (마케팅)
				return "info/usetermspersonalagree_channel";
			}  else {
				if ("030".equals(chCd)) {
					return "info/info_offer_agree030";
				}
			}
		}

		return "info/svc_use_terms"; // S 서비스이용약관
	}
	
	@RequestMapping(value = "/bp-terms-detail", method = { RequestMethod.GET, RequestMethod.POST })
	public String bpAgreeDetail(final String type, final Model model) {
		log.info("type : {}", type);
		
		if (StringUtils.hasText(type)) {
			if ("S".equals(type)) {
				return "info/bp_svc_use_terms"; // 서비스 이용 약관 info/bp_svc_use_terms S
			} else if ("C".equals(type)) {
				return "info/bp_info_use_agree"; // 개인정보 수집 이용 동의 info/bp_info_use_agree C
			} else if ("T".equals(type)) {
				return "info/bp_info_trans_agree"; // 국외이전 동의 info/bp_info_trans_agree T
			} else if ("P".equals(type)) { // 개인정보 제공동의 info/bp_info_offer_agree P
				return "info/bp_info_offer_agree";
			}
		}
		
		return "info/info_error";
	}

	// 본인인증 스킵하는 고객등록 테스트
	@GetMapping("/manual")
	public String manualEntry(final Model model) {
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		
		List<Channel> channels = this.commonService.getChannels();
		model.addAttribute("channels", channels);

		return "cert/manual-entry";
	}

	// 본인인증 정보를 수동으로 입력하고 고객가입
	@PostMapping("/manual-join")
	public String menualJoin(final ManualParam manualParam, final HttpSession session, final Model model, final RedirectAttributes redirectAttributes, final Locale locale) {

		SSOParam ssoParam = new SSOParam();
		if (WebUtil.getSession(OmniConstants.SSOPARAM) != null) {
			ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
			if (StringUtils.isEmpty(ssoParam.getRedirectUri())) {
				ssoParam.setRedirectUri("/home.jsp");
			}
		} else {
			ssoParam = null;
		}

		OfflineParam offlineParam = new OfflineParam();
		if (WebUtil.getSession(OmniConstants.OFFLINEPARAM) != null) {
			offlineParam = (OfflineParam) WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (StringUtils.isEmpty(offlineParam.getReturnUrl())) {
				offlineParam.setReturnUrl("/home.jsp");
			}
		} else {
			offlineParam = null;
		}

		session.invalidate();

		if (StringUtils.isEmpty(manualParam.getChCd())) {
			model.addAttribute("oauthErrorCode", "9999");
			model.addAttribute("oauthErrorMsg", "경로코드가 존재하지 않습니다.");

			OAuth2Error oauth2Error = new OAuth2Error();
			oauth2Error.setOauthErrorCode("9999");
			oauth2Error.setOauthErrorMsg("경로코드가 존재하지 않습니다.");
			model.addAttribute("message", "경로코드가 존재하지 않습니다.");

			return "wso2/oauth2_error";
		} else {

			List<Channel> channels = this.commonService.getChannels();
			List<String> channelCds = new ArrayList<>();
			for (Channel channel : channels) {
				channelCds.add(channel.getChCd());
			}

			if (!channelCds.contains(manualParam.getChCd())) {
				OAuth2Error oauth2Error = new OAuth2Error();
				oauth2Error.setOauthErrorCode("9999");
				oauth2Error.setOauthErrorMsg("경로코드가 올바르지 않습니다. 정확한 경로인지 확인 바랍니다.[" + manualParam.getChCd().replaceAll("(?i)script|object|applet|embed|form|alert|href|cookie|input|src|fromcharcode|encodeuri|encodeuricomponent|expression|iframe|window|location|style|eval","").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>","").replaceAll("\r|\n|&nbsp;","") + "]");

				model.addAttribute("message", "경로코드가 올바르지 않습니다. 정확한 경로인지 확인 바랍니다.[" + manualParam.getChCd().replaceAll("(?i)script|object|applet|embed|form|alert|href|cookie|input|src|fromcharcode|encodeuri|encodeuricomponent|expression|iframe|window|location|style|eval","").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>","").replaceAll("\r|\n|&nbsp;","") + "]");

				return "wso2/oauth2_error";
			}

			WebUtil.setSession(OmniConstants.CH_CD_SESSION, manualParam.getChCd());
		}

		final String chCd = manualParam.getChCd();

		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		// offline - 이니스프리, 에스쁘아 POS
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute("chcd", channel.getChCd());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		
		// 본인인증 에서 전달 받은 값
		CertResult certResult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
		if (certResult == null) {

			// 수동으로 본인인증 정보 입력
			certResult = new CertResult();
			certResult.setBirth(manualParam.getUserBirth());
			certResult.setChCd(manualParam.getChCd());
			certResult.setCiNo(manualParam.getUserCi());
			certResult.setForeigner(manualParam.getUserForeigner());
			certResult.setGender(manualParam.getUserGender());
			certResult.setGenderCode(manualParam.getUserGender());
			certResult.setName(manualParam.getUserName());
			certResult.setPhone(manualParam.getUserMobile());

			// 가입 제한 연령(14세 미만) 체크
			boolean restrict = DateUtil.isJoinRestrictByAuth(certResult);
			if (restrict) {
				model.addAttribute("home", channel.getHmpgUrl());
				model.addAttribute("homeurl", channel.getHmpgUrl());
				model.addAttribute(OmniConstants.RD_URL, channel.getHmpgUrl());
				return "mgmt/restrict_age";
			}

			if (OmniUtil.isOffline(channel)) {

				log.debug("▶▶▶▶▶▶ offline param set start ");
				if (offlineParam != null) {
					WebUtil.setSession(OmniConstants.OFFLINEPARAM, offlineParam);
				} else {
					if (StringUtils.isEmpty(manualParam.getReturnUrl())) {
						manualParam.setReturnUrl("/home.jsp");
					}
					OfflineParam offlineParamManual = new OfflineParam();
					offlineParamManual.setAddInfo("Y");
					offlineParamManual.setChCd(manualParam.getChCd());
					offlineParamManual.setJoinPrtnId(manualParam.getJoinPrtnId());
					offlineParamManual.setJoinPrtnNm(manualParam.getJoinPrtnNm());
					offlineParamManual.setJoinEmpId(manualParam.getJoinEmpId());
					offlineParamManual.setReturnUrl(manualParam.getReturnUrl());
					WebUtil.setSession(OmniConstants.OFFLINEPARAM, offlineParamManual);
				}

				log.debug("▶▶▶▶▶▶ offline param set end ");

			} else {

				log.debug("▶▶▶▶▶▶ online sso param set start ");

				if (ssoParam != null) {
					WebUtil.setSession(OmniConstants.SSOPARAM, ssoParam);
				} else {
					SSOParam ssoParamManual = new SSOParam();
					ssoParamManual.setAuthenticators("");
					ssoParamManual.setAuthFailure("");
					ssoParamManual.setAuthFailureMsg("");
					ssoParamManual.setChannelCd(manualParam.getChCd());
					ssoParamManual.setClient_id("");
					ssoParamManual.setCode("");
					ssoParamManual.setCommonAuthCallerPath("");
					ssoParamManual.setQueryString("");
					ssoParamManual.setRedirect_uri("");
					ssoParamManual.setRedirectUri(manualParam.getReturnUrl());
					ssoParamManual.setScope("");
					ssoParamManual.setSessionDataKey("SSDK-1000000");
					ssoParamManual.setTenantDomain("");
					ssoParamManual.setSp("");
					ssoParamManual.setJoin("false");
					WebUtil.setSession(OmniConstants.SSOPARAM, ssoParamManual);
				}

				log.debug("▶▶▶▶▶▶ online sso param set end ");

			}

			WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certResult);

		}

		model.addAttribute("unm", SecurityUtil.setXyzValue(certResult.getName()));

		log.debug("▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
		log.debug("▶▶▶▶▶▶ joinstep cert result : {}", StringUtil.printJson(certResult));
		log.debug("▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲");

		// 이 결과에 따라서 분기 처리
		final JoinResponse response = this.joinService.checkJoinCondition(certResult, OmniUtil.isOffline(channel));

		if (response != null) {

			JoinStepVo joinStepVo = response.getJoinStep();

			model.addAttribute("joinStepType", joinStepVo.getJoinType().getType());
			model.addAttribute("joinType", response.getType()); // 가입 타입 설정

			String incsNo = joinStepVo.getIncsNo();
			if (StringUtils.hasText(incsNo)) {
				if ("0".equals(incsNo)) {
					incsNo = "";
				} else {
					model.addAttribute(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));
				}
			} else {
				response.setType(JoinDivisionType.CHANNEL_JOIN.getType());
			}

			if (StringUtils.hasText(incsNo)) {

				model.addAttribute("incsno", incsNo);
				model.addAttribute(OmniConstants.INCS_NO_SESSION, Integer.parseInt(incsNo));

			}
			log.debug("▶▶▶▶▶▶ join response : {}", StringUtil.printJson(response));

			// 통합고객이 있는 경우
			if (response.getType() == JoinDivisionType.EXIST.getType()) { // 가입사실안내 A0103

				// JOINED_OMNI_CH 25 : A0103(온라인-로그인하기, 오프라인-확인) --> 가입사실(중복체크) - 옴니조회
				// JOINED_OMNI 30 : A0103 --> A0105
				// JOINED_OFF 35 : A0103 --> A0207
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
				
				if ((joinStepVo.getJoinType() == JoinType.JOINED_OMNI_CH) // 25 : O O O A0103(온라인-로그인하기, 오프라인-확인) --> 가입사실(중복체크) - 옴니조회
						|| (joinStepVo.getJoinType() == JoinType.JOINED_OMNI) // 30 : O O x A0103 --> A0105
						|| (joinStepVo.getJoinType() == JoinType.JOINED_OFF) // 35 : O X X : A0103 --> A0207(id_regist) 타오프라인 경로 자체
				) {
					List<Customer> customers = new ArrayList<>();
					if (omniUsers != null && omniUsers.size() > 0) {
						log.debug("▶▶▶▶▶▶ [joined] customers :  {}", StringUtil.printJson(omniUsers));

						for (UmOmniUser user : omniUsers) {
							Customer customer = new Customer();
							customer.setChcsNo(user.getUmUserName());
							customer.setCustNm(user.getFullName());
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
					String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
					model.addAttribute("joinAditor", joinAditor);
					String headerType = config.isHeaderType(channel.getChCd(), profile);
					model.addAttribute("headertype", headerType);
					
					return "mgmt/joined";

				} else if (joinStepVo.getJoinType() == JoinType.COVERSION) { // 전환 A0202, A0203, A0204 --> A0205
					if (omniUserSize == 0 && chUserSize > 0) { // 경로에만
						int incsNoInt = Integer.parseInt(incsNo);

						// 옴니에 동일 아이디가 있는지 체크
						UmChUser sameIdChUser = new UmChUser();
						sameIdChUser.setChCd(chCd);
						sameIdChUser.setIncsNo(incsNoInt);
						final boolean same = this.mgmtService.hasSameLoginId(sameIdChUser);

						log.debug("[joined] use same login id already ? {}", same);

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
									Map<String, String> loginMap = this.customerApiService.getChannelUser(chUser);
									if (loginMap == null || loginMap.isEmpty()) {
										for (UmChUser user : chUsers) {
											Map<String, String> chLoginMap = new HashMap<>();
											chLoginMap.put("chcd", chCd);
											chLoginMap.put("id", user.getChcsWebId());
											//chLoginMap.put("name", certResult.getName());
											chLoginMap.put("name", loginMap.get("name") == null ? certResult.getName() : loginMap.get("name").toString());
											loginData.add(chLoginMap);
										}
									} else {
										loginData.add(loginMap);
									}
									model.addAttribute("chusers", loginData);
								}
							}
							TermsVo termsVo = new TermsVo();
							termsVo.setChCd(channel.getChCd());
							List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
							model.addAttribute("corptermslist", termsList);
							return "convs/conversion_a0203";
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
								Map<String, String> loginMap = this.customerApiService.getChannelUser(chUser);
								if (loginMap == null || loginMap.isEmpty()) {
									for (UmChUser user : chUsers) {
										Map<String, String> chLoginMap = new HashMap<>();
										chLoginMap.put("chcd", chCd);
										chLoginMap.put("id", user.getChcsWebId());
										//chLoginMap.put("name", certResult.getName());
										chLoginMap.put("name", loginMap.get("name") == null ? certResult.getName() : loginMap.get("name").toString());
										loginData.add(chLoginMap);
									}
								} else {
									loginData.add(loginMap);
								}
								model.addAttribute("chusers", loginData);
							}
							
							TermsVo termsVo = new TermsVo();
							termsVo.setChCd(channel.getChCd());
							List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
							model.addAttribute("corptermslist", termsList);
							
							return "convs/conversion_a0202";
						}

					} else if (omniUserSize > 0 && chUserSize > 0) { // 중복 : A0204
						model.addAttribute("trnsType", OmniConstants.TRNS_TYPE_INTEGRATE);
						model.addAttribute("omniusers", omniUsers); // 통합 아이디 2 개 이상 체크
						model.addAttribute("chusers", chUsers);
						List<Map<String, String>> omniData = new ArrayList<>();

						for (UmOmniUser user : omniUsers) {
							Map<String, String> loginMap = new HashMap<>();
							loginMap.put("chcd", OmniConstants.JOINON_CHCD);
							loginMap.put("id", user.getUmUserName());
							loginMap.put("name", user.getFullName());
							omniData.add(loginMap);
						}
						model.addAttribute("bpLength", omniData.size());
						model.addAttribute("bpusers", omniData);

						List<Map<String, String>> chData = new ArrayList<>();
						for (UmChUser user : chUsers) {
							Map<String, String> loginMap = this.customerApiService.getChannelUser(user);
							chData.add(loginMap);

						}
						model.addAttribute("chLength", chData.size());
						model.addAttribute("chusers", chData);
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
							for(TermsVo vo : termsList2) {
								if(vo.getMkSn()==-20) {
									model.addAttribute("terms_marketing", vo);
								}
							}
						}
						
						return "convs/conversion_a0204";
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
				log.debug("▶▶▶▶▶▶ join type : {}, {}", response.getType(), "약관동의");
				model.addAttribute("certType", ""); // 인증타입(ipin, kmcis)
				if (!OmniUtil.isOffline(channel)) {
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;

					// 경로고객인 경우 약관 동의했으면 스킵
					UmOmniUser omniUser = new UmOmniUser();
					if (StringUtils.hasText(incsNo)) {
						omniUser.setIncsNo(incsNo);
					}
					omniUser.setChCd(chCd);

					if (!this.termsService.hasTermsAgree(omniUser)) { // 진입 채널에 대한 약관 출력 정보 조회
						TermsVo termsVo = new TermsVo();
						termsVo.setChCd(channel.getChCd());
						if(chCd.equals(OmniConstants.OSULLOC_CHCD)) {
							termsVo.setIncsNo(incsNo);
						}
						List<TermsVo> termsList = this.termsService.getTerms(termsVo);
						model.addAttribute("terms", termsList);
					}
					
					TermsVo corpTermsVo = new TermsVo();
					corpTermsVo.setChCd(channel.getChCd());
					List<TermsVo> corpTermsList = this.termsService.getCorpTerms(corpTermsVo);
					model.addAttribute("corptermslist", corpTermsList);
					
					//20230404 채널 문자 수신 동의
					final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
					WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
					TermsVo corpTermsVo2 = new TermsVo();
					corpTermsVo2.setChCd(onlineChCd);
					UmOmniUser omniUser2 = new UmOmniUser();
					omniUser2.setChCd(onlineChCd);
					if (!this.termsService.hasTermsAgree(omniUser2)) { // 진입 채널에 대한 약관 출력 정보 조회
						TermsVo termsVo = new TermsVo();
						termsVo.setChCd(onlineChCd);
						List<TermsVo> termsList = this.termsService.getTermsChoice(termsVo);
//						model.addAttribute("terms", termsList);
						for(TermsVo vo : termsList) {
							if(vo.getMkSn()==-20) {
								model.addAttribute("terms_marketing", vo);
							}
						}
					}
					
					String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
					model.addAttribute("joinAditor", joinAditor);
					String headerType = config.isHeaderType(channel.getChCd(), profile);
					model.addAttribute("headertype", headerType);
					
					return "join/join_step";
				} else {
					
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					model.addAttribute("homeurl", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					model.addAttribute("home", OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
					
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
					
					return "join/join_step_off";
				}
			} else if (response.getType() == JoinDivisionType.INFO_MISMATCH.getType() || response.getType() == JoinDivisionType.NAME_MISMATCH.getType()) { // 고객센터
				log.debug("▶▶▶▶▶▶ join type : {}, {}", response.getType(), "고객센터");
				redirectAttributes.addFlashAttribute("types", response.getType());
				WebUtil.setSession("csTypes", response.getType());
				return WebUtil.getRedirectUrlWithContextPath("/mgmt/csinfo");
			} else if (response.getType() == JoinDivisionType.WITHDRAW.getType()) { // 탈퇴 가입제한
				log.debug("▶▶▶▶▶▶ join type : {}, {}", response.getType(), "탈퇴 가입제한");
				String withdrawDate = "";
				Customer customer = response.getCustomer();
				if (customer == null) {
					CustInfoVo custInfoVo = new CustInfoVo();
					custInfoVo.setChCd(chCd);
					custInfoVo.setIncsNo(incsNo);
					customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
					log.debug("▶▶▶▶▶▶ API call Customer for WithrawInfo : {}", StringUtil.printJson(customer));
					withdrawDate = customer.getCustWtDttm();
				} else {
					withdrawDate = customer.getCustWtDttm();
				}
				if (StringUtils.isEmpty(withdrawDate)) {
					List<UmOmniUser> omniUsers = this.mgmtService.getOmniUserList(Integer.parseInt(incsNo));
					if (omniUsers != null) {
						UmOmniUser omniUser = omniUsers.get(0);
						withdrawDate = omniUser.getDisabledDate();
					}
				}
				
				model.addAttribute("withdrawDate", DateUtil.getBirthDate(withdrawDate));
				model.addAttribute("home", channel.getHmpgUrl());
				model.addAttribute("homeurl", channel.getHmpgUrl());
				return "mgmt/restrict_withdraw";
				
			}

		}
		TermsVo termsVo = new TermsVo();
		termsVo.setChCd(channel.getChCd());
		List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
		model.addAttribute("corptermslist", termsList);
		
		//20230323 채널 문자 수신 동의
		final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
		TermsVo corpTermsVo = new TermsVo();
		corpTermsVo.setChCd(onlineChCd);
		UmOmniUser omniUser = new UmOmniUser();
		omniUser.setChCd(onlineChCd);
		if (!this.termsService.hasTermsAgree(omniUser)) { // 진입 채널에 대한 약관 출력 정보 조회
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
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
		model.addAttribute("joinAditor", joinAditor);
		String headerType = config.isHeaderType(channel.getChCd(), profile);
		model.addAttribute("headertype", headerType);
		
		return "join/join_step";
	}

	// 경로 가입 시 탈퇴회원인 경우 처리
	@GetMapping("/channel-withdraw")
	public String channelWithdraw(final Model model) {

		final String wtDt = WebUtil.getStringSession("channelWtdt");
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);

		model.addAttribute("channelWtdt", wtDt);
		
		
		if (OmniUtil.isOffline(channel)) {
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
				model.addAttribute(OmniConstants.RD_URL, OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
			} else {
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				model.addAttribute("home", OmniUtil.getRedirectOfflineInitUrl(channel, profile));
				model.addAttribute("homeurl", OmniUtil.getRedirectOfflineInitUrl(channel, profile));
			}
		} else {
			model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
			//model.addAttribute("home", channel.getHmpgUrl());
			//model.addAttribute("homeurl", channel.getHmpgUrl());
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		}
		
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		

		return "info/channel_withdraw";
	}

	@GetMapping("/channel-cache-clear")
	public String channelCacheClear() {
		
		this.commonService.channelCacheClear();
		
		return "sample/cache_clear";
	}
	
	@GetMapping("/qr-code")
	public String grcode() {
		
		return "qrcode/qr_code_url";
	}
	
	
	@PostMapping("/qr-gen")
	public void grcode(final QrCode qrCode, final HttpServletResponse response) throws UnsupportedEncodingException {
		
		String encodeQrUrl = qrCode.getQrurl();
		log.debug("encoded qrcode url : {}", encodeQrUrl);
		if (StringUtils.hasText(encodeQrUrl)) {
			//String decodeQrUrl = URLDecoder.decode(encodeQrUrl, StandardCharsets.UTF_8.name());
			//log.debug("decoded qrcode url : {}", decodeQrUrl);
			CertUtil.getQrCode(encodeQrUrl, response);
		}

	}
	
	@RequestMapping(value = "/omni-join-start", method = { RequestMethod.GET, RequestMethod.POST })
	public String omniJoinStart(final HttpServletRequest request, final HttpServletResponse response, final Model model) throws UnsupportedEncodingException {
		Object offlineParam = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
		log.debug("▶▶▶▶▶▶ [omni-join-start] request : {}", StringUtil.printJson(offlineParam));
		
		model.addAttribute("entry", OmniUtil.getOfflineParam());
		
		return "/join/join_start";
	}
}
