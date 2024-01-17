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
 * Author	          : hjw0228
 * Date   	          : 2023. 9. 1..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.offline.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.offline.service.OfflineService;
import com.amorepacific.oneap.auth.offline.vo.OfflineLoginParam;
import com.amorepacific.oneap.auth.offline.vo.OfflineLoginResponse;
import com.amorepacific.oneap.auth.offline.vo.SendKakaoVo;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.exception.OmniException;
import com.amorepacific.oneap.common.types.LoginType;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.LoginStepVo;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.StatusCheckResponse;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.user.UserVo;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.offline.web 
 *    |_ OfflineController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 9. 1.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@Controller
@RequestMapping("/offline")
public class OfflineController {
	
	@Autowired
	private OfflineService offlineService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	@Value("${wso2.oneapurl}")
	private String oneApUrl;

	// 오프라인 매장용 로그인 페이지
	@GetMapping("/login")
	public String login( //
			final @RequestParam(value = "chCd", required = true) String chCd,
			final HttpServletRequest request, //
			final HttpServletResponse res, 
			final HttpSession session, //
			final Model model, //
			final RedirectAttributes redirectAttr) throws UnsupportedEncodingException {
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		session.invalidate(); // 로그인 페이지 진입 시 세션 초기화

		if (StringUtils.isEmpty(chCd)) {
			log.error("▶▶▶▶▶▶ [Offline login page] chCd is null");
			throw new OmniException("경로코드가 존재하지 않습니다.");
		} else {
			List<Channel> channels = this.commonService.getChannels();
			List<String> channelCds = new ArrayList<>();
			for (Channel channel : channels) {
				channelCds.add(channel.getChCd());
			}

			if (!channelCds.contains(chCd)) {
				log.error("▶▶▶▶▶▶ [Offline login page] chCd is invalid : {}", chCd);
				throw new OmniException("경로코드가 올바르지 않습니다. 정확한 경로인지 확인하세요.[" + chCd.replaceAll("(?i)script|object|applet|embed|form|alert|href|cookie|input|src|fromcharcode|encodeuri|encodeuricomponent|expression|iframe|window|location|style|eval","").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>","").replaceAll("\r|\n|&nbsp;","") + "]");
			}
			
			if (!config.isOfflineLogin(chCd, profile)) {
				log.error("▶▶▶▶▶▶ [Offline login page] Offline Login Page not enable chCd : {}", chCd);
				throw new OmniException("오프라인 매장 로그인 페이지 사용가능한 경로코드가 아닙니다.[" + chCd.replaceAll("(?i)script|object|applet|embed|form|alert|href|cookie|input|src|fromcharcode|encodeuri|encodeuricomponent|expression|iframe|window|location|style|eval","").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>","").replaceAll("\r|\n|&nbsp;","") + "]");
			}

			WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
		}
		
		final Channel channel = this.commonService.getChannel(chCd);
		
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		
		return "offline/login";
	}
	
	@PostMapping("/statuscheck")
	@ResponseBody
	public StatusCheckResponse statusCheck(@RequestBody final UserVo userVo) {
		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		if(StringUtils.isEmpty(chCd)) {
			chCd = userVo.getChCd();
			WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
		}
		
		final String encLoginid = userVo.getEncId();
		final String encLoginpw = userVo.getEncPwd();

		if (StringUtils.isEmpty(encLoginid) || StringUtils.isEmpty(encLoginpw)) {
			// LoginId Null Check & LoginPw Null Check - 2022-01-18 hjw0228
			log.error("▶▶▶▶▶▶ [/offline/statuscheck] LoginId or LoginPw is null");
			final LoginStepVo loginStepVo = new LoginStepVo(LoginType.PWDFAIL, null, null, null);

			final int type = loginStepVo.getLoginType().getType();

			StatusCheckResponse response = new StatusCheckResponse();
			response.setStatus(type);
			response.setResultCode(Integer.toString(type));
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));

			return response;
		}

		log.debug("▶▶▶▶▶▶ [offline login status check] status check(id,pwd) : {}, {}", encLoginid, encLoginpw);

		final String loginId = SecurityUtil.getXValue(encLoginid);
		final String loginPwd = SecurityUtil.getXValue(encLoginpw);

		final LoginStepVo loginStepVo = offlineService.loginStatusCheck(chCd, loginId, loginPwd);
		LoginType loginType = loginStepVo.getLoginType();
		log.debug("▶▶▶▶▶▶ [offline login status check] login type : {}, login step vo : {}", LoginType.get(loginType.getType()), StringUtil.printJson(loginStepVo));
		
		final int type = loginType.getType();

		StatusCheckResponse response = new StatusCheckResponse();
		response.setStatus(type);
		response.setResultCode(Integer.toString(type));
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		
		return response;
	}
	
	@PostMapping("/login/step")
	public String loginStep ( //
			final OfflineLoginParam param,
			final HttpServletRequest request, //
			final HttpServletResponse response, //
			final HttpSession session, //
			final Model model, //
			final Locale locale) throws UnsupportedEncodingException{
		
		WebUtil.setSession(OmniConstants.CH_CD_SESSION, param.getChCd());
		
		final OfflineLoginResponse offlineLoginResponse = (OfflineLoginResponse) WebUtil.getSession(OmniConstants.OFFLINE_LOGIN_RESPONSE);
		log.info("▶▶▶▶▶▶ [Offline login step] Offline Login chCd : {}, Response : {}", param.getChCd(), StringUtil.printJson(offlineLoginResponse));
		
		if(offlineLoginResponse == null || !"1".equals(offlineLoginResponse.getResultCode())) {
			log.error("▶▶▶▶▶▶ [Offline login step] Offline Login Failed chCd : {}", param.getChCd());
			model.addAttribute(OmniConstants.CH_CD, param.getChCd());
			model.addAttribute("message", "오프라인 매장 로그인이 실패하였습니다.<br/>다시 시도해주세요.");
			
			return "offline/info_error";
		} else {
			final Channel channel = this.commonService.getChannel(param.getChCd());
			final String joinUrl = "/entry?chCd=" + param.getChCd() + "&chnCd=" + param.getChCd() + 
					"&joinPrtnId=" + offlineLoginResponse.getStorecd() + "&storeCd=" + offlineLoginResponse.getStorecd() + 
					"&joinPrtnNm=" + offlineLoginResponse.getStorenm() + "&storenm=" + offlineLoginResponse.getStorenm() +
					"&user_id=" + offlineLoginResponse.getUser_id();
			
			model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
			model.addAttribute(OmniConstants.CH_CD, param.getChCd());
			model.addAttribute(OmniConstants.OFFLINE_LOGIN_RESPONSE, offlineLoginResponse);
			model.addAttribute("joinUrl", joinUrl);
			
			return "offline/login_complete";
		}
	}
	
	@GetMapping("/send/kakao")
	public String sendKakao( //
			final @RequestParam(value = "chCd", required = true) String chCd,
			final HttpServletRequest request, //
			final HttpServletResponse res, 
			final HttpSession session, //
			final Model model, //
			final RedirectAttributes redirectAttr) throws UnsupportedEncodingException {
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		final OfflineLoginResponse offlineLoginResponse = (OfflineLoginResponse) WebUtil.getSession(OmniConstants.OFFLINE_LOGIN_RESPONSE);
		log.info("▶▶▶▶▶▶ [Offline login send kakao] Offline Login chCd : {}, Response : {}", chCd, StringUtil.printJson(offlineLoginResponse));
		
		if(offlineLoginResponse == null || !"1".equals(offlineLoginResponse.getResultCode())) {
			log.error("▶▶▶▶▶▶ [Offline login step] Offline Login Failed cdCd : {}", chCd);
			model.addAttribute(OmniConstants.CH_CD, chCd);
			model.addAttribute("message", "오프라인 매장 메세지 전송 페이지 접근에 실패하였습니다.<br/>다시 시도해주세요.");
			
			return "offline/info_error";
		} else {
			final Channel channel = this.commonService.getChannel(chCd);
			
			model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
			model.addAttribute(OmniConstants.CH_CD, chCd);
			model.addAttribute(OmniConstants.OFFLINE_LOGIN_RESPONSE, offlineLoginResponse);
			
			return "offline/send_kakao";
		}
	}

	@PostMapping("/send/kakao")
	@ResponseBody
	public SendKakaoVo sendSms(@RequestBody SendKakaoVo sendKakaoVo) {

		log.debug("▶▶▶▶▶▶ [Offline send kakao notice] sendkakao vo : {}", StringUtil.printJson(sendKakaoVo));
		
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final OfflineLoginResponse offlineLoginResponse = (OfflineLoginResponse) WebUtil.getSession(OmniConstants.OFFLINE_LOGIN_RESPONSE);
		log.info("▶▶▶▶▶▶ [Offline send kakao notice] Offline Login chCd : {}, Response : {}", chCd, StringUtil.printJson(offlineLoginResponse));
		
		// 세션 만료 체크
		if(StringUtils.isEmpty(chCd) || offlineLoginResponse == null) {
			sendKakaoVo.setResultCode(ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode());
			sendKakaoVo.setMessage(ResultCode.REQ_REQUIRED_PARAM_EMPTY.message());

			return sendKakaoVo;
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(oneApUrl);
		stringBuilder.append("/auth/entry?chCd=").append(SecurityUtil.clearXSSNormal(chCd));
		stringBuilder.append("&chnCd=").append(SecurityUtil.clearXSSNormal(chCd));
		
		if (StringUtils.hasText(offlineLoginResponse.getStorecd())) {
			try {
				stringBuilder.append("&joinPrtnId=").append(URLEncoder.encode(offlineLoginResponse.getStorecd(), StandardCharsets.UTF_8.name())); // joinPrtnId
				stringBuilder.append("&storeCd=").append(URLEncoder.encode(offlineLoginResponse.getStorecd(), StandardCharsets.UTF_8.name())); // storeCd
			} catch (UnsupportedEncodingException e) {
				// NO PMD
			}
		}
		
		if (StringUtils.hasText(offlineLoginResponse.getStorenm())) {
			try {
				stringBuilder.append("&joinPrtnNm=").append(URLEncoder.encode(offlineLoginResponse.getStorenm(), StandardCharsets.UTF_8.name())); // joinPrtnNm
				stringBuilder.append("&storenm=").append(URLEncoder.encode(offlineLoginResponse.getStorenm(), StandardCharsets.UTF_8.name())); // storenm
			} catch (UnsupportedEncodingException e) {
				// NO PMD
			}
		}
		
		if (StringUtils.hasText(offlineLoginResponse.getUser_id())) {
			try {
				stringBuilder.append("&user_id=").append(URLEncoder.encode(offlineLoginResponse.getUser_id(), StandardCharsets.UTF_8.name())); // user_id
			} catch (UnsupportedEncodingException e) {
				// NO PMD
			}
		}
		
		final String joinUrl = stringBuilder.toString();
		
		ApiBaseResponse apiBaseResponse = offlineService.sendKakaoNotice(sendKakaoVo, chCd, joinUrl);
		
		sendKakaoVo.setResultCode(apiBaseResponse.getResultCode());
		sendKakaoVo.setMessage(apiBaseResponse.getMessage());

		return sendKakaoVo;
	}
}
