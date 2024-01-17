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
 * Date   	          : 2022. 3. 18..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.membership.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.membership.service.MembershipService;
import com.amorepacific.oneap.auth.membership.vo.MembershipUserInfo;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipParam;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipResponse;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipVo;
import com.amorepacific.oneap.auth.social.handler.SnsAuth;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.membership.MembershipParam;
import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.membership.web 
 *    |_ MembershipController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 3. 18.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@Controller
@RequestMapping("/membership")
public class MembershipController {
	
	@Autowired
	private SnsAuth snsAuth;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private MembershipService membershipService;
	
	private final String MEMBERSHIP = "usedSnsMembership"; // 멤버십
	
	// 제휴사 연동을 위한 Bridge Page
	@RequestMapping(value = {"" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String membershipStart(final MembershipParam membershipParam, final HttpServletRequest request, final HttpServletResponse response, final Model model, final HttpSession session, final Locale locale) throws Exception {
		
		session.invalidate(); // 브릿지 페이지 진입 시 세션 초기화
		
		// 필수 파라미터 체크
		if(StringUtils.isEmpty(membershipParam.getChCd())) { // 채널 코드
			model.addAttribute("message","뷰티포인트 멤버십 경로코드가 존재하지 않습니다.");
			return "info/membership_error";
		} else {
			List<Channel> channels = this.commonService.getChannels();
			List<String> channelCds = new ArrayList<>();
			for (Channel channel : channels) {
				channelCds.add(channel.getChCd());
			}

			if (!channelCds.contains(membershipParam.getChCd())) {
				model.addAttribute("message","뷰티포인트 멤버십 경로코드가 올바르지 않습니다. 정확한 경로인지 확인하세요.[" + membershipParam.getChCd() + "]");
				return "info/membership_error";
			}

			WebUtil.setSession(OmniConstants.CH_CD_SESSION, membershipParam.getChCd());
		}
		
		String chCd = membershipParam.getChCd();
		
		if(StringUtils.isEmpty(membershipParam.getRedirectUrl())) { // redirectUri
			model.addAttribute("message","뷰티포인트 멤버십 연결을 위한 필수 값이 존재하지 않습니다.");
			return "info/membership_error";
		} else {
			WebUtil.setSession(OmniConstants.MEMBERSHIP_AFTER_REDIRECT_URL, membershipParam.getRedirectUrl());
		}
		
		if(StringUtils.isEmpty(membershipParam.getCancelUrl())) { // cancelUri
			model.addAttribute("message","뷰티포인트 멤버십 연결을 위한 필수 값이 존재하지 않습니다.");
			return "info/membership_error";
		} else {
			WebUtil.setSession(OmniConstants.CANCEL_URI, membershipParam.getCancelUrl());
		}
		
		// Bridge Page 파라미터 조회
		log.info("▶▶▶▶▶▶ [membership page] membership param  : {}", StringUtil.printJson(membershipParam));
		
		// SSOParam 생성
		SSOParam ssoParam = new SSOParam();
		ssoParam.setChannelCd(membershipParam.getChCd());
		ssoParam.setRedirectUri(membershipParam.getRedirectUrl());
		ssoParam.setCancelUri(membershipParam.getCancelUrl());
		ssoParam.setIsMembership("Y");
		
		// 세션에 값 저장
		WebUtil.setSession(OmniConstants.SSOPARAM, ssoParam);
		WebUtil.setSession(OmniConstants.IS_MEMBERSHIP, ssoParam.getIsMembership());
		
		// 파라미터 추출
		Map<String, Object> paramMap = WebUtil.convertRequestToMap(request);
		
		// 제휴사별 필수 파라미터 체크
		if(OmniConstants.SSG_CHCD.equals(chCd)) { // SSG
			if(paramMap == null || StringUtils.isEmpty((String) paramMap.get("authCode"))) {
				model.addAttribute("message","뷰티포인트 멤버십 연결을 위한 필수 값이 존재하지 않습니다.");
				return "info/membership_error";
			}
		}
		
		// 연동 프로세스 수행
		MembershipUserInfo membershipUserInfo = membershipService.startMembershipProcess(chCd, paramMap);
		log.debug("▶▶▶▶▶▶ [membership page] membership process result : {}", StringUtil.printJson(membershipUserInfo));
		
		// 세션에 사용자 정보 저장
		WebUtil.setSession(OmniConstants.MEMBERSHIP_USERINFO, membershipUserInfo);
		WebUtil.setSession(OmniConstants.INCS_NO_SESSION, membershipUserInfo.getIncsNo());
		WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, membershipUserInfo.getXincsNo());
		WebUtil.setSession(OmniConstants.MBR_ID_SESSION, membershipUserInfo.getMbrId());
		WebUtil.setSession(OmniConstants.XMBR_ID_SESSION, membershipUserInfo.getXmbrId());
		
		// Result Code 에 따라 분기 처리
		if(ResultCode.SYSTEM_ERROR.getCode().equals(membershipUserInfo.getResultCode())) { // 에러 페이지 이동
			model.addAttribute("message",membershipUserInfo.getResultMessage());
			return "info/membership_error";
		} else if (ResultCode.EXPIRED_ACCESS_TOKEN.getCode().equals(membershipUserInfo.getResultCode())) { // Access Token 만료 안내 페이지 이동
			model.addAttribute("message",membershipUserInfo.getResultMessage());
			return "info/membership_error";
		} else if (ResultCode.USER_NOT_FOUND.getCode().equals(membershipUserInfo.getResultCode()) 
				|| ResultCode.USER_DORMANCY.getCode().equals(membershipUserInfo.getResultCode())
				|| ResultCode.USER_JOIN_IMPOSSIBLE_30DAYS.getCode().equals(membershipUserInfo.getResultCode())) { // 회원 가입 페이지 이동
			return "membership/membership_join";
		} else if (ResultCode.MEMBERSHIP_ALREADY_LINKED.getCode().equals(membershipUserInfo.getResultCode())){ // 이미 연동된 계정일 경우
			String name = OmniUtil.maskUserName(membershipUserInfo.getName(), locale);
			String id = OmniUtil.maskUserId(membershipUserInfo.getId());
			
			model.addAttribute("id", id);
			model.addAttribute("name", name);
			
			// 이미 연동이 되어 있어도 SSG 에 연동 요청 Process 진행 (방어로직)
			membershipUserInfo = membershipService.startMembershipLink(membershipUserInfo);
			
			if(!ResultCode.SUCCESS.getCode().equals(membershipUserInfo.getResultCode())) {
				model.addAttribute("message",membershipUserInfo.getResultMessage());
				return "info/membership_error";
			}
			
			return "membership/membership_already_linked";
		} else if (StringUtils.isEmpty(membershipUserInfo.getResultCode())){ // Result Code is Null 일 경우 에러 페이지 이동
			log.info("예외 발생 : ▶▶▶▶▶▶ membershipUserInfo : {}", StringUtil.printJson(membershipUserInfo));
			model.addAttribute("message",ResultCode.UNKNOWN_ERROR.message());			
			return "info/membership_error";
		}
		
		String name = OmniUtil.maskUserName(membershipUserInfo.getName(), locale);
		String id = OmniUtil.maskUserId(membershipUserInfo.getId());
		
		model.addAttribute("chCd", membershipUserInfo.getChCd());
		model.addAttribute("id", id);
		model.addAttribute("name", name);
		model.addAttribute("xname", SecurityUtil.setXyzValue(membershipUserInfo.getName()));
		model.addAttribute("xid", SecurityUtil.setXyzValue(membershipUserInfo.getId()));
		model.addAttribute("xincsNo", membershipUserInfo.getXincsNo());
		model.addAttribute("mbrId", membershipUserInfo.getMbrId());
		
		return "membership/membership_start"; // 멤버십 연동 페이지 이동
	}
	
	// 신규 회원 가입 및 ID 등록 후 멤버십 연동 페이지 이동
	@GetMapping("/membership_start")
	public String membershipStart(final HttpServletRequest request, final HttpServletResponse response, final Model model, final HttpSession session, final Locale locale) throws Exception {
		MembershipUserInfo membershipUserInfo = new MembershipUserInfo();
		// MembershipUserInfo 검증
		if(WebUtil.getSession(OmniConstants.MEMBERSHIP_USERINFO) == null) { // 세션에 값이 없으면 세션 만료로 에러 페이지 호출
			model.addAttribute("message","세션이 만료되었습니다.");
			return "info/membership_error";
		} else {
			membershipUserInfo = (MembershipUserInfo) WebUtil.getSession(OmniConstants.MEMBERSHIP_USERINFO);
			
			// 뷰티 멤버십 연동을 통한 회원 가입일 경우 CI 값 검증
			CertResult certResult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
			SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
			String ci = (certResult == null || certResult.getCiNo().isEmpty()) ? snsParam.getCiNo() : certResult.getCiNo(); 
			if(membershipUserInfo != null && !StringUtils.isEmpty(ci)) {
				if(!membershipUserInfo.getCiNo().equals(ci)) {
					log.debug("▶▶▶▶▶▶ [membership join] CI불일치  : ");
					log.debug("▶▶▶▶▶▶ [membership CiNo]  : {}", membershipUserInfo.getCiNo());
					log.debug("▶▶▶▶▶▶ [certResult CiNo]  : {}", ci);
					
					return "info/membership_ci_mismatch";
				}
			}
			
			String name = OmniUtil.maskUserName(membershipUserInfo.getName(), locale);
			String id = OmniUtil.maskUserId(membershipUserInfo.getId());
			
			model.addAttribute("chCd", membershipUserInfo.getChCd());
			model.addAttribute("id", id);
			model.addAttribute("name", name);
			model.addAttribute("xname", SecurityUtil.setXyzValue(membershipUserInfo.getName()));
			model.addAttribute("xid", SecurityUtil.setXyzValue(membershipUserInfo.getId()));
			model.addAttribute("xincsNo", membershipUserInfo.getXincsNo());
			model.addAttribute("mbrId", membershipUserInfo.getMbrId());
		}
		
		return "membership/membership_start";
	}
	
	// 제휴사 멤버십 연동
	@PostMapping("/link")
	public String membershipLink(MembershipUserInfo membershipUserInfo, final HttpServletRequest request, final HttpServletResponse response, final Model model, final HttpSession session, final Locale locale) throws Exception {
		
		// MembershipUserInfo 검증
		if(WebUtil.getSession(OmniConstants.MEMBERSHIP_USERINFO) == null) { // 세션에 값이 없으면 세션 만료로 에러 페이지 호출
			model.addAttribute("message","세션이 만료되었습니다.");
			return "info/membership_error";
		} else {
			membershipUserInfo = (MembershipUserInfo) WebUtil.getSession(OmniConstants.MEMBERSHIP_USERINFO);
			
			membershipUserInfo = membershipService.startMembershipLink(membershipUserInfo);
			
			if(!ResultCode.SUCCESS.getCode().equals(membershipUserInfo.getResultCode())) {
				model.addAttribute("message",membershipUserInfo.getResultMessage());
				return "info/membership_error";
			}
		}
		
		return "membership/membership_finish";
	}
	
	// 네이버 스마트스토어 연동을 위한 Bridge Page
	@SuppressWarnings("deprecation")
	@PostMapping("/naver")
	public String naverMembershipStart(final NaverMembershipParam naverMembershipParam, final HttpServletRequest request, final HttpServletResponse response, final Model model, final HttpSession session, final Locale locale) throws Exception {
		
		session.invalidate(); // 브릿지 페이지 진입 시 세션 초기화
		
		String chCd = "";
		String snsType = "NA";
		
		// Bridge Page 파라미터 조회
		log.debug("▶▶▶▶▶▶ [Naver membership page] membership param  : {}", StringUtil.printJson(naverMembershipParam));
		
		// 필수 파라미터 체크
		if(StringUtils.isEmpty(naverMembershipParam.getCode()) || naverMembershipParam.getState() == null) {
			model.addAttribute("message","네이버 스마트스토어 연동을 위한 필수값이 존재하지 않습니다.");
			return "info/membership_error";
		}
		
		// 파라미터에서 state 값 추출 후 BASE64 디코딩
		try {
			String state = new String(SecurityUtil.base64(naverMembershipParam.getState()));
			JsonObject jsonObj = new JsonParser().parse(state).getAsJsonObject();
			log.debug("▶▶▶▶▶▶ [Naver membership page] membership state json object : {}", jsonObj);
			
			if(jsonObj.get("token") == null || jsonObj.get("provisionIds") == null) {
				model.addAttribute("message","네이버 스마트스토어 연동을 위한 필수값이 존재하지 않습니다.");
				return "info/membership_error";
			}
			
			String token = jsonObj.get("token").getAsString();
			String[] provisionIds = jsonObj.get("provisionIds").getAsString().split(",");
			String[] optionIds = jsonObj.get("optionIds").getAsString().split(",");
			naverMembershipParam.setToken(token);
			naverMembershipParam.setProvisionIds(provisionIds);
			naverMembershipParam.setOptionIds(optionIds);
			
			// 2023-11-01 useHomeLanding 파라미터 추가
			if(jsonObj.get("useHomeLanding") != null) {
				String useHomeLanding = jsonObj.get("useHomeLanding").getAsString();
				naverMembershipParam.setUseHomeLanding(useHomeLanding);
			}
			
			log.info("▶▶▶▶▶▶ [Naver membership page] naverMembershipParam : {}", StringUtil.printJson(naverMembershipParam));
		} catch (Exception e) {
			log.error("State parameter decoding failure - state : {}", naverMembershipParam.getState());
			model.addAttribute("message","네이버 스마트 스토어 연동시 오류가 발생하였습니다.");
			return "info/membership_error";
		}
		
		// 1. 네이버 스마트 스토어 회원정보 조회 API 호출
		NaverMembershipResponse userInfoResponse = membershipService.getNaverMembershipUserInfo(naverMembershipParam.getToken());
		
		// 2. 회원정보 조회 API 실패 시 오류 리턴
		if(userInfoResponse == null || userInfoResponse.getContents() == null || "FAIL".equals(userInfoResponse.getOperationResult())) {
			model.addAttribute("message","네이버 스마트 스토어 연동시 오류가 발생하였습니다.");
			return "info/membership_error";
		}
		
		// 3. interlockMemberIdNo 값으로 채널 코드 추출
		chCd = membershipService.getChCdByInterlockSellerNo(userInfoResponse.getContents().getInterlockSellerNo());
		if(StringUtils.isEmpty(chCd)) {
			model.addAttribute("message","네이버 스마트 스토어 연동시 오류가 발생하였습니다.");
			return "info/membership_error";
		}
		
		// 4. 필수 약관에 모두 동의처리하지 않으면 오류 리턴
		naverMembershipParam.setInterlockSellerNo(userInfoResponse.getContents().getInterlockSellerNo());
		if(!membershipService.isRequiredTermsCheck(naverMembershipParam)) {
			model.addAttribute("message","네이버 스마트 스토어 연동에 필요한 필수약관 동의가 필요합니다.");
			return "info/membership_error";
		}
		
		// 5. 네아로 로그인을 위해 세션에 값 저장
		WebUtil.setSession(OmniConstants.NAVER_MEMBERSHIP_USER_INFO, userInfoResponse);
		WebUtil.setSession(OmniConstants.NAVER_MEMBERSHIP_PARAM, naverMembershipParam);
		WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
		WebUtil.setSession(OmniConstants.SNS_USE_TYPE, MEMBERSHIP);
		WebUtil.setSession(OmniConstants.IS_MEMBERSHIP, "Y");
		
		// 6. 네아로 로그인 요청
		return "redirect:" + snsAuth.getAuthorizeUrl(snsType, chCd);
	}
	
	// 네이버 스마트스토어 연동 시 네아로 이후 Callback
	@SuppressWarnings("unchecked")
	@GetMapping("/naver/callback")
	public String naverMembershipCallback(final HttpServletRequest request, final HttpServletResponse response, final Model model, final HttpSession session, final Locale locale) throws Exception {
		
		// 세션에서 네아로 로그인 파라미터 추출
		Map<String, String> param = (Map<String, String>) WebUtil.getSession(OmniConstants.SNSPARAM);
		log.debug("▶▶▶▶▶▶ [Naver membership page] sns param  : {}", StringUtil.printJson(param));
		
		if(param == null) {
			model.addAttribute("message","네이버 스마트 스토어 연동시 오류가 발생하였습니다.");
			return "info/membership_error";
		}
		
		NaverMembershipVo naverMembershipVo = membershipService.startNaverMembershipProcess(param); 
		model.addAttribute("naverMembershipVo", naverMembershipVo);
		
		return "membership/naver_membership_finish";
	}
}
