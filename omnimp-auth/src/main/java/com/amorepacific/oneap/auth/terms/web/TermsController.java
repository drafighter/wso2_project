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
 * Date   	          : 2020. 9. 17..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.terms.web;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.api.vo.ovo.CustYnResponse;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.social.handler.SnsAuth;
import com.amorepacific.oneap.auth.terms.service.TermsService;
import com.amorepacific.oneap.auth.terms.vo.NaverUnLinkVo;
import com.amorepacific.oneap.auth.terms.vo.RetractionVo;
import com.amorepacific.oneap.auth.terms.vo.TermsVo;
import com.amorepacific.oneap.common.exception.OmniException;
import com.amorepacific.oneap.common.types.LoginType;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.join.ChannelParam;
import com.amorepacific.oneap.common.vo.sns.SnsType;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.terms.web 
 *    |_ TermsController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 17.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Controller
@RequestMapping("/terms")
public class TermsController {
	
	@Autowired
	private SnsAuth snsAuth;

	@Autowired
	private CommonService commonService;

	@Autowired
	private TermsService termsService;

	@Autowired
	private CustomerApiService customerApiService;

	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	private final String MEMBERSHIP = "usedSnsMembership"; // 멤버십

	// 경로 약관 동의
	@RequestMapping(value = "/ch", method = { /* RequestMethod.GET, */RequestMethod.POST })
	public String termChannel(final ChannelParam channelParam, final HttpServletRequest request, final Model model, final Locale locale) {

		log.debug("▶▶▶▶▶ [channel terms] method : {}, param : {}", request.getMethod(), StringUtil.printJson(channelParam));
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;

		if (channelParam == null) {
			return "redirect:/info?error=channel.param.empty";
		}

		if (StringUtils.isEmpty(channelParam.getRedirectUri())) {
			return "redirect:/info?error=channel.redirecturi.empty";
		}
		
		// 고객통합번호 평문 전송 체크용 (임시 2022-03-14)
		if(!StringUtils.isEmpty(channelParam.getIncsNo())) {
			log.info("▶▶▶▶▶ [channel terms plain text incsNo] param : {}", StringUtil.printJson(channelParam));
		}

		// 고객통합번호 암호화 로직은 우선 개발 서버만 반영해서 체크 2021-12-16 -> 고객통합번호 암호화 로직 적용
		/*
		 * if ("prod".equals(profile) && StringUtils.isEmpty(channelParam.getIncsNo()) && StringUtils.isEmpty(channelParam.getXincsNo())) { return
		 * "redirect:/info?error=channel.incsno.empty"; } else if (!"prod".equals(profile) && StringUtils.isEmpty(channelParam.getXincsNo())) {
		 * return "redirect:/info?error=channel.incsno.empty"; }
		 */
		if (StringUtils.isEmpty(channelParam.getXincsNo())) {
			return "redirect:/info?error=channel.incsno.empty";
		}

		if (StringUtils.isEmpty(channelParam.getChCd())) {
			return "redirect:/info?error=channel.chcd.empty";
		}

		final String chCd = channelParam.getChCd().trim();
		final String incsNo = StringUtils.isEmpty(channelParam.getXincsNo()) ? channelParam.getIncsNo().trim() : SecurityUtil.getXValue(channelParam.getXincsNo().trim());
		if(StringUtils.isEmpty(incsNo)) { // 현 단계에서 incsNo가 Null 인 경우 xincsNo 복호화 실패 -> 오류 페이지 이동
			return "redirect:/info?error=channel.incsno.error";
		}
		
		final String xincsNo = StringUtils.isEmpty(channelParam.getXincsNo()) ? SecurityUtil.setXyzValue(incsNo) : channelParam.getXincsNo().trim();
		final int numIncsNo = Integer.parseInt(incsNo);
		
		log.debug("▶▶▶▶▶ [channel terms] channel cd : {}, channel incsno : {}", chCd, incsNo);

		WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
		WebUtil.setSession(OmniConstants.INCS_NO_SESSION, numIncsNo);
		WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, xincsNo);

		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setIncsNo(incsNo);
		final Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);

		if (customer == null) {
			return "redirect:/info?error=user.empty";
		}

		final Channel channel = this.commonService.getChannel(chCd);
		
		if (!OmniUtil.redirectUriValidationCheck(channel, channelParam.getRedirectUri())) { // 허용된 경로 리다이렉션URL 여부 체크
			log.error("▶▶▶▶▶ [channel terms] channel cd : {}, redirectUri invalidate : {}", chCd, channelParam.getRedirectUri());
			return "redirect:/info?error=channel.redirecturi.unknown"; 
		}
		
		final String RD_URL = OmniUtil.getRedirectUriWithoutChannelDomain(channelParam.getRedirectUri());
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getLogoutWithRedirectUrl(channel, RD_URL));
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("home", OmniUtil.getLogoutWithRedirectUrl(channel, RD_URL));
		model.addAttribute("homeurl", OmniUtil.getLogoutWithRedirectUrl(channel, RD_URL)); //
		model.addAttribute("entry", OmniUtil.getOfflineParam());
		
		Object obj = WebUtil.getSession(OmniConstants.SSOPARAM);
		if (obj != null) {
			SSOParam ssoParam = (SSOParam) obj;
			ssoParam.setCancelUri(OmniUtil.getLogoutWithRedirectUrl(channel, RD_URL));
			ssoParam.setRedirectUri(OmniUtil.getChannelDomainWithRedirectUrl(channel, channelParam.getRedirectUri()));
			
			log.debug("▶▶▶▶▶ [channel terms] param : {}", StringUtil.printJson(ssoParam));
			WebUtil.setSession(OmniConstants.SSOPARAM, ssoParam);  
		}
		
		channelParam.setRedirectUri(OmniUtil.getChannelDomainWithRedirectUrl(channel, channelParam.getRedirectUri()));

		WebUtil.setSession("chcsWebId", null);
		
		log.debug("▶▶▶▶▶ [channel terms] omniUser info : {}", StringUtil.printJson(customer));

		String name = OmniUtil.maskUserName(customer.getCustNm(), locale);
		final String id = customer.getChcsNo(); // 약관동의에서 아이디는 마스킹하지 않음.

		if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
			final String mobileNo = StringUtil.mergeMobile(customer);
			final String mobile = OmniUtil.maskMobile(mobileNo, locale);
			model.addAttribute("mobile", mobile);
		} else {
			if (customer != null && "ICITSVCOM001".equals(customer.getRsltCd())) {
				log.debug("▶▶▶▶▶ [channel terms] {}", "검색된 사용자 없음.");
				return "redirect:/info?error=user.empty";
			}
		}

		model.addAttribute("chcd", chCd);
		model.addAttribute("incsno", incsNo);
		model.addAttribute("xincsno", xincsNo);
		model.addAttribute("name", name);
		model.addAttribute("id", id);
		model.addAttribute("xname", SecurityUtil.setXyzValue(customer.getCustNm()));
		model.addAttribute("xid", SecurityUtil.setXyzValue(id));

		if (StringUtils.hasText(customer.getMbrJoinDt())) {
			model.addAttribute("joindate", DateUtil.getBirthDate(customer.getMbrJoinDt()));
		} else {
			model.addAttribute("joindate", "");
		}

		UmOmniUser omniUser = new UmOmniUser();
		if (StringUtils.hasText(incsNo)) {
			omniUser.setIncsNo(incsNo); //
		}
		omniUser.setChCd(chCd);
		
		WebUtil.setSession(OmniConstants.CHANNELPARAM, channelParam);
		
		boolean isMarketing = StringUtil.isTrue(this.config.getChannelApi(chCd, "ismarketing", profile));
		// 해당 경로 미 가입 상태 시 문자 수신 동의 여부를 받는 경우 ex) APMall
		if(isMarketing) {
			custInfoVo.setChCd(chCd);

			CustYnResponse custYnResponse = this.customerApiService.getCustYn(custInfoVo);
			
			if(custYnResponse != null && "ICITSVCOM001".equals(custYnResponse.getRsltCd())) { // 경로 가입이 되어 있지 않은 경우
				log.debug("▶▶▶▶▶ [login step] 통합회원으로 진입 채널에 문자 수신동의가 되어있지 않은 경우");
				log.debug("▶▶▶▶▶ [login step] 통합회원전환, 약관동의 --> {}", LoginType.AGREE.getDesc());
				model.addAttribute("isMarketing", isMarketing);

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
//					model.addAttribute("terms", termsList2);
					for(TermsVo vo : termsList2) {
						if(vo.getMkSn()==-20) {
							model.addAttribute("terms_marketing", vo);
						}
					}
				}
				
				return "terms/login_terms_apply";
			}
		}
		
		if (this.termsService.hasTermsAgree(omniUser)) { // 이미 약관동의 완료
			// 플랫폼 오픈 후 채널 약관 동의 계정으로
			// 해당 채널에 최초 로그인 or SSO 몰이동
			TermsVo termsVo = new TermsVo();
			termsVo.setChCd(channel.getChCd());
			if (!this.termsService.hasCorpTermsAgree(omniUser)) {
				List<TermsVo> termsList = this.termsService.getCorpTerms(termsVo);
				model.addAttribute("terms", termsList);
				model.addAttribute("corpterms", true);
				return "terms/new_terms_ch_apply"; // ME-FO-A0214
			}
			return "redirect:/info?error=channel.terms.already";

		} else {
			// 진입 채널에 대한 약관 출력 정보 조회
			TermsVo termsVo = new TermsVo();
			termsVo.setChCd(channel.getChCd());
			if(channel.getChCd().equals(OmniConstants.OSULLOC_CHCD)) {
				termsVo.setIncsNo(omniUser.getIncsNo());
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
//			model.addAttribute("terms", termsList);
			for(TermsVo vo : termsList) {
				if(vo.getMkSn()==-20) {
					model.addAttribute("terms_marketing", vo);
				}
			}
		}
		
		return "terms/terms_ch_apply"; // ME-FO-A0105
	}

	// 네이버 스마트스토어를 통한 약관 철회 및 탈퇴 Gate Page
	@GetMapping("/naver")
	public String naverUnLinkStart(final HttpServletRequest request, final HttpServletResponse response, final Model model, final HttpSession session, final Locale locale) throws Exception {
		session.invalidate(); // 브릿지 페이지 진입 시 세션 초기화
		
		// 네아로 로그인을 위해 세션에 값 저장
		WebUtil.setSession(OmniConstants.SNS_USE_TYPE, MEMBERSHIP);
		WebUtil.setSession(OmniConstants.IS_UNLINK_MEMBERSHIP, "Y");
		
		model.addAttribute("authUrl", snsAuth.getAuthorizeUrl(SnsType.NAVER.getType(), ""));
		
		return "terms/naver_unlink_start";
	}
	
	// 네이버 스마트스토어를 통한 약관 철회 및 탈퇴 시 네아로 이후 Callback
	@SuppressWarnings("unchecked")
	@GetMapping("/naver/callback")
	public String naverUnLink(final HttpServletRequest request, final HttpServletResponse response, final Model model, final HttpSession session, final Locale locale) throws Exception {
		
		// 세션에서 네아로 로그인 파라미터 추출
		Map<String, String> param = (Map<String, String>) WebUtil.getSession(OmniConstants.SNSPARAM);
		log.debug("▶▶▶▶▶▶ [Naver UnLink membership page] sns param  : {}", StringUtil.printJson(param));
		
		if(param == null) {
			model.addAttribute("message","네이버 스마트 스토어 연동 해제시 오류가 발생하였습니다.");
			return "info/membership_error";
		}
		
		NaverUnLinkVo naverUnLinkVo = termsService.getNaverUnLinkUserInfo(param);
		
		if(StringUtils.hasText(naverUnLinkVo.getReturnUrl())) return naverUnLinkVo.getReturnUrl();
		
		String chCd = OmniConstants.NAVER_STORE_CHCD;
		WebUtil.setSession(OmniConstants.CH_CD_SESSION, chCd);
		WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, naverUnLinkVo.getXincsNo());
		
		model.addAttribute("rv", this.config.resourceVersion());
		model.addAttribute("chCd", chCd);
		model.addAttribute("naverUnLinkVo", naverUnLinkVo);
		
		return "terms/naver_unlink";
	}
	
	// 약관 동의 철회
	@PostMapping("/retraction")
	public String retractionTerms(final HttpServletRequest request, final HttpServletResponse response, final Model model, final HttpSession session, final Locale locale, final RetractionVo retractionVo) throws Exception {
		
		log.debug("▶▶▶▶▶▶ [retraction] vo  : {}", StringUtil.printJson(retractionVo));
		
		boolean success = termsService.retractionTerms(retractionVo);
		
		return "redirect:" + retractionVo.getReturnUrl();
	}
	
	// 경로 약관 상세
	@RequestMapping(value = "/detail", method = { RequestMethod.GET, RequestMethod.POST })
	public String bpAgreeDetail(final String chCd, final String type, final Model model) {
		log.info("chCd : {}, type : {}", chCd, type);
		
		if(StringUtils.isEmpty(type) || StringUtils.isEmpty(chCd)) {
			// 경로 약관 유형 및 채널코드는 필수 파라미터
			throw new OmniException("경로 약관 상세 조회를 위한 파라미터가 존재하지 않습니다.");
		}
		
		if (StringUtils.hasText(type)) {
			if ("S".equals(type)) {
				return "terms/channel/" + chCd + "/svc_use_terms"; // 서비스 이용 약관 terms/channel/${chCd}/svc_use_terms
			} else if ("P".equals(type)) {
				return "terms/channel/" + chCd + "/info_offer_agree"; // 개인정보 제공 동의 terms/channel/${chCd}/info_offer_agree
			} else if ("C".equals(type)) {
				return "terms/channel/" + chCd + "/info_use_agree"; // 개인정보 수집 및 이용동의 terms/channel/${chCd}/info_use_agree
			} else if ("M".equals(type)) { 
				return "terms/channel/" + chCd + "/info_agree_choice"; // 개인정보 수집 및 이용동의(마케팅) terms/channel/${chCd}/info_agree_choice 
			}
		}
		
		return "info/info_error";
	}
}
