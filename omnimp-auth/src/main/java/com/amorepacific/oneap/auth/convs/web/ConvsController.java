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
 * Date   	          : 2020. 7. 27..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.convs.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.convs.vo.ConvsRequest;
import com.amorepacific.oneap.auth.ga.GaTaggingUtils;
import com.amorepacific.oneap.auth.join.service.JoinService;
import com.amorepacific.oneap.auth.join.vo.JoinRequest;
import com.amorepacific.oneap.auth.join.vo.JoinResponse;
import com.amorepacific.oneap.auth.social.handler.SnsAuth;
import com.amorepacific.oneap.auth.step.ApiOnlineConvTemsProcessStep;
import com.amorepacific.oneap.auth.step.ApiOnlineProcessStep;
import com.amorepacific.oneap.auth.step.AuthStep;
import com.amorepacific.oneap.auth.terms.service.TermsService;
import com.amorepacific.oneap.auth.terms.vo.TermsVo;
import com.amorepacific.oneap.common.check.CheckResponse;
import com.amorepacific.oneap.common.check.Checker;
import com.amorepacific.oneap.common.check.actor.CheckActor;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.types.JoinDivisionType;
import com.amorepacific.oneap.common.types.LoginType;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.LoginStepVo;
import com.amorepacific.oneap.common.vo.Marketing;
import com.amorepacific.oneap.common.vo.OfflineParam;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.conv 
 *    |_ ConvController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 27.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Controller
@RequestMapping("/convs")
public class ConvsController {

	@Autowired
	private AuthStep authStep;

	@Autowired
	private SnsAuth snsAuth;

	@Autowired
	private ApiOnlineProcessStep apiOnlineProcessStep;

	@Autowired
	private ApiOnlineConvTemsProcessStep apiOnlineConvTemsProcessStep;

	@Autowired
	private TermsService termsService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private JoinService joinService;

	@Autowired
	private CustomerApiService customerApiService;
	
	@Autowired
	private SystemInfo systemInfo;
	
	@Autowired
	private GaTaggingUtils googleGataggingUtils; // ga taagging util

	private ConfigUtil config = ConfigUtil.getInstance();

	// -----------------A0201 통합회원 전환 안내

	@PostMapping("/statuscheck")
	@ResponseBody
	public BaseResponse preCheck(@RequestBody final ConvsRequest convsRequest) {

		BaseResponse response = new BaseResponse();

		if (StringUtils.hasText(convsRequest.getId())) {
			final String loginid = StringUtils.hasText(SecurityUtil.getXValue(convsRequest.getId(), false)) ? SecurityUtil.getXValue(convsRequest.getId(), false).replaceAll(" ", "").toLowerCase() : ""; // 2023-07-12 loginId 공백 제거 및 소문자 처리

			if (StringUtils.hasText(loginid)) {

				// 전환인 경우 아이디 체크할 필요없음.
				int cnt = this.joinService.getCountAvaiableUserId(loginid);
				if (cnt > 0) {
					response.setResultCode("-1"); // EXIST
					return response;
				} else {

					CheckResponse checkresp = new Checker.Builder() //
							.checkType(CheckActor.Type.ID) //
							.checkValue(loginid) //
							.build() //
							.check();

					if (checkresp.getStatus() == 100) {
						response.setResultCode("1");
					} else {
						response.setResultCode("-2");
						return response;
					}
				}
			} else {
				response.setResultCode("1");
			}
		}

		final String loginpw = SecurityUtil.getXValue(convsRequest.getPw(), false);
		final String logincpw = SecurityUtil.getXValue(convsRequest.getCpw(), false);
		if (StringUtils.hasText(logincpw)) {
			CheckResponse checkpwdresp = new Checker.Builder() //
					.checkType(CheckActor.Type.PASSWORD) //
					.checkValue(loginpw) //
					.compareValue(logincpw) //
					.build() //
					.check();

			log.debug("▶▶▶▶▶▶ [conversion status check] pwd check : {}", StringUtil.printJson(checkpwdresp));

			if (checkpwdresp.getStatus() == 100) {
				response.setResultCode("1");
			} else {
				response.setResultCode("-4");
				return response;
			}
		} else {
			response.setResultCode("1");
		}
		return response;
	}

	@PostMapping("/terms-finish-202-01-ch")
	public String terms20201ChFinish(final ConvsRequest convsRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-202-01-ch] request : {}", StringUtil.printJson(convsRequest));

		final String loginid = SecurityUtil.getXValue(convsRequest.getId(), false); // 선택된 로그인 아이디
		final String loginpw = SecurityUtil.getXValue(convsRequest.getPw(), false);
		final String logincpw = SecurityUtil.getXValue(convsRequest.getCpw(), false);
		String incsNo = SecurityUtil.getXValue(convsRequest.getXno(), false);

		if (StringUtils.hasText(loginid)) {
			WebUtil.setSession("chcsWebId", loginid); // 자체회원인 경우 이 값으로 경로 임시 테이블 검색해야함.
		}

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.TRNS_TYPE_TRANSFORM, true);
		model.addAttribute("isTransform", true);

		if (StringUtils.hasText(logincpw) && StringUtils.hasText(loginpw)) {
			if (!loginpw.equals(logincpw)) {
				return "redirect:/redirect-authz-msg?chCd=" + chCd + "&msg=password.valid.error.same";
			}
		}

		CertResult certResult = this.commonService.getCertResult(incsNo);

		// 통합회원 전환
		// 채널 --> 뷰포아이디로 API 호출
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setUnm(certResult.getName());
		joinRequest.setIncsno(incsNo);
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(loginpw);
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setTransCustomer(false); // 전환처리자여부
		joinRequest.setConvsType(SecurityUtil.getXValue(convsRequest.getConvsType(), false));
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		joinRequest.setSwitchJoinYn("Y"); // 경로고객통합전환가입여부
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [conversion terms-finish-202-01-ch] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());

			}
		}

		joinRequest.setTrnsType(convsRequest.getTrnsType()); // 전환, 통합여부

		List<String> bpterms = convsRequest.getBpterms();
		List<String> bptcatcds = convsRequest.getBpTcatCds();
		List<String> bptncvnos = convsRequest.getBpTncvNos();
		List<String> terms = convsRequest.getTerms();
		List<String> tcatcds = convsRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = convsRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = convsRequest.getMarketing();
		List<String> marketingChcds = convsRequest.getMarketingChcd();

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
				term.setChgChCd(channel.getChCd());
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
		termsVo.setChCd(channel.getChCd());
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

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-202-01-ch] request settings : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = this.apiOnlineConvTemsProcessStep.convsTerm20201ChProcess(joinRequest);

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-202-01-ch] response : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			// 전환완료 - 로그인 시 사용위해 저장
			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(certResult.getName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginid));
			WebUtil.setSession(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(loginpw));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

			// SNS 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(loginid);
				snsParam.setIncsNo(incsNo);

				snsAuth.doSnsMapping(snsParam);
			}

		} else {
			model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("channelName", channel.getChCdNm());
			if ("ICITSVCOM004".equals(response.getResultCode())) { // 존재하는 고객
				log.debug("▶▶▶▶▶▶ [conversion terms-finish-202-01-ch] customer : {}", "존재하는 고객");
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

		model.addAttribute("name", certResult.getName());
		model.addAttribute("previd", convsRequest.getPrevid()); // 이전 채널아이디
		model.addAttribute("newid", loginid);
		model.addAttribute("processedDate", DateUtil.getBirthDate(DateUtil.getCurrentDate()));

		return "/convs/finish";
	}

	@PostMapping("/terms-finish-202-ch")
	public String terms202ChFinish(final ConvsRequest convsRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-202-ch] request : {}", StringUtil.printJson(convsRequest));

		final String loginid = SecurityUtil.getXValue(convsRequest.getId(), false); // 선택된 로그인 아이디
		final String loginpw = SecurityUtil.getXValue(convsRequest.getPw(), false);
		final String logincpw = SecurityUtil.getXValue(convsRequest.getCpw(), false);
		String incsNo = SecurityUtil.getXValue(convsRequest.getXno(), false);

		if (StringUtils.hasText(loginid)) {
			WebUtil.setSession("chcsWebId", loginid); // 자체회원인 경우 이 값으로 경로 임시 테이블 검색해야함.
		}

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.TRNS_TYPE_TRANSFORM, true);
		model.addAttribute("isTransform", true);

		if (StringUtils.hasText(logincpw) && StringUtils.hasText(loginpw)) {
			if (!loginpw.equals(logincpw)) {
				return "redirect:/redirect-authz-msg?chCd=" + chCd + "&msg=password.valid.error.same";
			}
		}

		CertResult certResult = this.commonService.getCertResult(incsNo);

		// 통합회원 전환
		// 채널 --> 뷰포아이디로 API 호출
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setUnm(certResult.getName());
		joinRequest.setIncsno(incsNo);
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(loginpw);
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setTransCustomer(false); // 전환처리자여부
		joinRequest.setConvsType(SecurityUtil.getXValue(convsRequest.getConvsType(), false));
		joinRequest.setSwitchJoinYn("Y"); // 경로고객통합전환가입여부
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [conversion terms-finish-202-ch] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());

			}
		}

		joinRequest.setTrnsType(convsRequest.getTrnsType()); // 전환, 통합여부

		List<String> bpterms = convsRequest.getBpterms();
		List<String> bptcatcds = convsRequest.getBpTcatCds();
		List<String> bptncvnos = convsRequest.getBpTncvNos();
		List<String> terms = convsRequest.getTerms();
		List<String> tcatcds = convsRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = convsRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = convsRequest.getMarketing();
		List<String> marketingChcds = convsRequest.getMarketingChcd();

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
				term.setChgChCd(channel.getChCd());
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
		termsVo.setChCd(channel.getChCd());
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

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-202-ch] request settings : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = this.apiOnlineConvTemsProcessStep.convsTerm202ChProcess(joinRequest); // this.apiOnlineTermsProcessStep.convsTermChProcess(joinRequest);

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-202-ch] response : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			// 전환완료 - 로그인 시 사용위해 저장
			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(certResult.getName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginid));
			WebUtil.setSession(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(loginpw));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

			// SNS 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(loginid);
				snsParam.setIncsNo(incsNo);

				snsAuth.doSnsMapping(snsParam);
			}

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

		model.addAttribute("name", certResult.getName());
		model.addAttribute("previd", convsRequest.getPrevid()); // 이전 채널아이디
		model.addAttribute("newid", loginid);
		model.addAttribute("processedDate", DateUtil.getBirthDate(DateUtil.getCurrentDate()));

		return "/convs/finish";
	}

	@PostMapping("/terms-finish-203-01-ch")
	public String terms20301ChFinish(final ConvsRequest convsRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-203-ch] request : {}", StringUtil.printJson(convsRequest));

		final String loginid = StringUtils.hasText(SecurityUtil.getXValue(convsRequest.getId(), false)) ? SecurityUtil.getXValue(convsRequest.getId(), false).replaceAll(" ", "").toLowerCase() : ""; // 선택된 로그인 아이디, 2023-07-12 loginId 공백 제거 및 소문자 처리
		final String loginpw = SecurityUtil.getXValue(convsRequest.getPw(), false);
		final String logincpw = SecurityUtil.getXValue(convsRequest.getCpw(), false);
		String incsNo = SecurityUtil.getXValue(convsRequest.getXno(), false);

		if (StringUtils.hasText(loginid)) {
			WebUtil.setSession("chcsWebId", convsRequest.getPrevid()); // 자체회원인 경우 이 값으로 경로 임시 테이블 검색해야함.
		}

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.TRNS_TYPE_TRANSFORM, true);
		model.addAttribute("isTransform", true);

		if (StringUtils.hasText(logincpw) && StringUtils.hasText(loginpw)) {
			if (!loginpw.equals(logincpw)) {
				return "redirect:/redirect-authz-msg?chCd=" + chCd + "&msg=password.valid.error.same";
			}
		}

		CertResult certResult = this.commonService.getCertResult(incsNo);

		// 통합회원 전환
		// 채널 --> 뷰포아이디로 API 호출
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setUnm(certResult.getName());
		joinRequest.setIncsno(incsNo);
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(loginpw);
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setTransCustomer(false); // 전환처리자여부
		joinRequest.setConvsType(SecurityUtil.getXValue(convsRequest.getConvsType(), false));
		joinRequest.setSwitchJoinYn("Y"); // 경로고객통합전환가입여부
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [conversion terms-finish-203-ch] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());

			}
		}

		joinRequest.setTrnsType(convsRequest.getTrnsType()); // 전환, 통합여부

		List<String> bpterms = convsRequest.getBpterms();
		List<String> bptcatcds = convsRequest.getBpTcatCds();
		List<String> bptncvnos = convsRequest.getBpTncvNos();
		List<String> terms = convsRequest.getTerms();
		List<String> tcatcds = convsRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = convsRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = convsRequest.getMarketing();
		List<String> marketingChcds = convsRequest.getMarketingChcd();

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
				term.setChgChCd(channel.getChCd());
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
		termsVo.setChCd(channel.getChCd());
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

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-203-ch] request settings : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = this.apiOnlineConvTemsProcessStep.convsTerm20201ChProcess(joinRequest); // this.apiOnlineTermsProcessStep.convsTermChProcess(joinRequest); // 202와 프로세스 동일함.
		log.debug("▶▶▶▶▶▶ [conversion terms-finish-203-ch] response : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			// 전환완료 - 로그인 시 사용위해 저장
			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(certResult.getName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginid));
			WebUtil.setSession(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(loginpw));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

			// SNS 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(loginid);
				snsParam.setIncsNo(incsNo);

				snsAuth.doSnsMapping(snsParam);
			}

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

		model.addAttribute("name", certResult.getName());
		model.addAttribute("previd", convsRequest.getPrevid()); // 이전 채널아이디
		model.addAttribute("newid", loginid);
		model.addAttribute("processedDate", DateUtil.getBirthDate(DateUtil.getCurrentDate()));

		return "/convs/finish";
	}

	@PostMapping("/terms-finish-203-ch")
	public String terms203ChFinish(final ConvsRequest convsRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-203-ch] request : {}", StringUtil.printJson(convsRequest));

		final String loginid = StringUtils.hasText(SecurityUtil.getXValue(convsRequest.getId(), false)) ? SecurityUtil.getXValue(convsRequest.getId(), false).replaceAll(" ", "").toLowerCase() : ""; // 선택된 로그인 아이디, 2023-07-12 loginId 공백 제거 및 소문자 처리
		final String loginpw = SecurityUtil.getXValue(convsRequest.getPw(), false);
		final String logincpw = SecurityUtil.getXValue(convsRequest.getCpw(), false);
		String incsNo = SecurityUtil.getXValue(convsRequest.getXno(), false);

		if (StringUtils.hasText(loginid)) {
			WebUtil.setSession("chcsWebId", convsRequest.getPrevid()); // 자체회원인 경우 이 값으로 경로 임시 테이블 검색해야함.
		}

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.TRNS_TYPE_TRANSFORM, true);
		model.addAttribute("isTransform", true);

		if (StringUtils.hasText(logincpw) && StringUtils.hasText(loginpw)) {
			if (!loginpw.equals(logincpw)) {
				return "redirect:/redirect-authz-msg?chCd=" + chCd + "&msg=password.valid.error.same";
			}
		}

		CertResult certResult = this.commonService.getCertResult(incsNo);

		// 통합회원 전환
		// 채널 --> 뷰포아이디로 API 호출
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setUnm(certResult.getName());
		joinRequest.setIncsno(incsNo);
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(loginpw);
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setTransCustomer(false); // 전환처리자여부
		joinRequest.setConvsType(SecurityUtil.getXValue(convsRequest.getConvsType(), false));
		joinRequest.setSwitchJoinYn("Y"); // 경로고객통합전환가입여부
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [conversion terms-finish-203-ch] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());

			}
		}

		joinRequest.setTrnsType(convsRequest.getTrnsType()); // 전환, 통합여부

		List<String> bpterms = convsRequest.getBpterms();
		List<String> bptcatcds = convsRequest.getBpTcatCds();
		List<String> bptncvnos = convsRequest.getBpTncvNos();
		List<String> terms = convsRequest.getTerms();
		List<String> tcatcds = convsRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = convsRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = convsRequest.getMarketing();
		List<String> marketingChcds = convsRequest.getMarketingChcd();

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
				term.setChgChCd(channel.getChCd());
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
		termsVo.setChCd(channel.getChCd());
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

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-203-ch] request settings : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = this.apiOnlineConvTemsProcessStep.convsTerm202ChProcess(joinRequest); // this.apiOnlineTermsProcessStep.convsTermChProcess(joinRequest); // 202와 프로세스 동일함.

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-203-ch] response : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			// 전환완료 - 로그인 시 사용위해 저장
			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(certResult.getName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginid));
			WebUtil.setSession(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(loginpw));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

			// SNS 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(loginid);
				snsParam.setIncsNo(incsNo);

				snsAuth.doSnsMapping(snsParam);
			}

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

		model.addAttribute("name", certResult.getName());
		model.addAttribute("previd", convsRequest.getPrevid()); // 이전 채널아이디
		model.addAttribute("newid", loginid);
		model.addAttribute("processedDate", DateUtil.getBirthDate(DateUtil.getCurrentDate()));

		return "/convs/finish";
	}

	@PostMapping("/terms-finish-203-ch-id")
	public String terms203ChIdFinish(final ConvsRequest convsRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-203-ch-id] request : {}", StringUtil.printJson(convsRequest));

		final String loginid = SecurityUtil.getXValue(convsRequest.getId(), false); // 선택된 로그인 아이디
		final String loginpw = SecurityUtil.getXValue(convsRequest.getPw(), false);
		final String logincpw = SecurityUtil.getXValue(convsRequest.getCpw(), false);
		String incsNo = SecurityUtil.getXValue(convsRequest.getXno(), false);

		if (StringUtils.hasText(loginid)) {
			WebUtil.setSession("chcsWebId", convsRequest.getPrevid()); // 자체회원인 경우 이 값으로 경로 임시 테이블 검색해야함.
		}

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.TRNS_TYPE_TRANSFORM, true);
		model.addAttribute("isTransform", true);

		if (StringUtils.hasText(logincpw) && StringUtils.hasText(loginpw)) {
			if (!loginpw.equals(logincpw)) {
				return "redirect:/redirect-authz-msg?chCd=" + chCd + "&msg=password.valid.error.same";
			}
		}

		CertResult certResult = this.commonService.getCertResult(incsNo);

		// 통합회원 전환
		// 채널 --> 뷰포아이디로 API 호출
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setUnm(certResult.getName());
		joinRequest.setIncsno(incsNo);
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(loginpw);
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setTransCustomer(false); // 전환처리자여부
		joinRequest.setConvsType(SecurityUtil.getXValue(convsRequest.getConvsType(), false));
		joinRequest.setSwitchJoinYn("Y"); // 경로고객통합전환가입여부
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [conversion terms-finish-203-ch-id] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());

			}
		}

		joinRequest.setTrnsType(convsRequest.getTrnsType()); // 전환, 통합여부

		List<String> bpterms = convsRequest.getBpterms();
		List<String> bptcatcds = convsRequest.getBpTcatCds();
		List<String> bptncvnos = convsRequest.getBpTncvNos();
		List<String> terms = convsRequest.getTerms();
		List<String> tcatcds = convsRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = convsRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = convsRequest.getMarketing();
		List<String> marketingChcds = convsRequest.getMarketingChcd();

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
				term.setChgChCd(channel.getChCd());
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
		termsVo.setChCd(channel.getChCd());
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

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-203-ch-id] request settings : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = this.apiOnlineConvTemsProcessStep.convsTerm203Process(joinRequest); // this.apiOnlineTermsProcessStep.convsTermChProcess(joinRequest); // 202와 프로세스 동일함.

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-203-ch-id] response : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			// 전환완료 - 로그인 시 사용위해 저장
			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(certResult.getName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginid));
			WebUtil.setSession(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(loginpw));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

			// SNS 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(loginid);
				snsParam.setIncsNo(incsNo);

				snsAuth.doSnsMapping(snsParam);
			}

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

		model.addAttribute("name", certResult.getName());
		model.addAttribute("previd", convsRequest.getPrevid()); // 이전 채널아이디
		model.addAttribute("newid", loginid);
		model.addAttribute("processedDate", DateUtil.getBirthDate(DateUtil.getCurrentDate()));

		return "/convs/finish";
	}

	@PostMapping("/terms-finish-204-ch")
	public String terms204ChFinish(final ConvsRequest convsRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) throws UnsupportedEncodingException {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-204-ch] request : {}", StringUtil.printJson(convsRequest));

		final String loginid = SecurityUtil.getXValue(convsRequest.getId(), false); // 선택된 로그인 아이디
		String loginpw = SecurityUtil.getXValue(convsRequest.getPw(), false);
		if (StringUtils.isEmpty(loginpw)) {
			loginpw = SecurityUtil.getXValue(WebUtil.getStringSession(OmniConstants.XPW_SESSION), false);
		}
		// final String logincpw = SecurityUtil.getXValue(convsRequest.getCpw(), false);
		String incsNo = SecurityUtil.getXValue(convsRequest.getXno(), false);

		if (StringUtils.hasText(loginid)) {
			WebUtil.setSession("chcsWebId", convsRequest.getPrevid()); // 자체회원인 경우 이 값으로 경로 임시 테이블 검색해야함.
		}

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.TRNS_TYPE_INTEGRATE, true);
		model.addAttribute("isIntegrated", true);

		CertResult certResult = this.commonService.getCertResult(incsNo);

		// 통합회원 전환
		// 채널 --> 뷰포아이디로 API 호출
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setUnm(certResult.getName());
		joinRequest.setIncsno(incsNo);
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(loginpw);
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setTransCustomer(false); // 전환처리자여부
		joinRequest.setConvsType(SecurityUtil.getXValue(convsRequest.getConvsType(), false));
		joinRequest.setSwitchJoinYn("Y"); // 경로고객통합전환가입여부
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [conversion terms-finish-204-ch] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());

			}
		}

		joinRequest.setTrnsType(convsRequest.getTrnsType()); // 전환, 통합여부

		List<String> bpterms = convsRequest.getBpterms();
		List<String> bptcatcds = convsRequest.getBpTcatCds();
		List<String> bptncvnos = convsRequest.getBpTncvNos();
		List<String> terms = convsRequest.getTerms();
		List<String> tcatcds = convsRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = convsRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = convsRequest.getMarketing();
		List<String> marketingChcds = convsRequest.getMarketingChcd();

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
				term.setChgChCd(channel.getChCd());
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
		termsVo.setChCd(channel.getChCd());
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

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-204-ch] request settings : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = this.apiOnlineConvTemsProcessStep.convsTerm204ChProcess(joinRequest); // this.apiOnlineTermsProcessStep.convsTermChProcess(joinRequest);

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-204-ch] response : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {
			log.debug("▶▶▶▶▶▶ [conversion terms-finish-204-ch] success");
			// 전환완료 - 로그인 시 사용위해 저장
			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(certResult.getName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginid));
			WebUtil.setSession(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(loginpw));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

			// SNS 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(loginid);
				snsParam.setIncsNo(incsNo);

				snsAuth.doSnsMapping(snsParam);
			}

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

		model.addAttribute("name", certResult.getName());
		model.addAttribute("previd", convsRequest.getPrevid()); // 이전 채널아이디
		model.addAttribute("newid", loginid);
		model.addAttribute("processedDate", DateUtil.getBirthDate(DateUtil.getCurrentDate()));
		model.addAttribute("convsType", joinRequest.getConvsType());
		if ("204".equals(convsRequest.getConvsType())) {
			if (StringUtils.hasText(loginid)) {
				log.debug("▶▶▶▶▶▶ {} {} {}", OmniConstants.LOGIN_ID_COOKIE_NAME, convsRequest.getConvsType(), loginid);
				WebUtil.setCookies(servletResponse, OmniConstants.LOGIN_ID_COOKIE_NAME, URLEncoder.encode(loginid.trim(), StandardCharsets.UTF_8.name()));
			}
		}

		return "/convs/finish";
	}

	@PostMapping("/terms-finish-202")
	public String terms202Finish(final ConvsRequest convsRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-202] request : {}", StringUtil.printJson(convsRequest));

		final String loginid = SecurityUtil.getXValue(convsRequest.getId(), false); // 선택된 로그인 아이디
		final String loginpw = SecurityUtil.getXValue(convsRequest.getPw(), false);
		final String logincpw = SecurityUtil.getXValue(convsRequest.getCpw(), false);
		String incsNo = SecurityUtil.getXValue(convsRequest.getXno(), false);

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.TRNS_TYPE_TRANSFORM, true);
		model.addAttribute("isTransform", true);
		if (StringUtils.hasText(loginid)) {
			WebUtil.setSession("chcsWebId", loginid); // 자체회원인 경우 이 값으로 경로 임시 테이블 검색해야함.
		}
		if (StringUtils.hasText(logincpw) && StringUtils.hasText(loginpw)) {
			if (!loginpw.equals(logincpw)) {
				return "redirect:/redirect-authz-msg?chCd=" + chCd + "&msg=password.valid.error.same";
			}
		}

		CertResult certResult = this.commonService.getCertResult(incsNo);

		// 통합회원 전환
		// 채널 --> 뷰포아이디로 API 호출
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setUnm(certResult.getName());
		joinRequest.setIncsno(incsNo);
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(loginpw);
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setTransCustomer(false); // 전환처리자여부
		joinRequest.setConvsType(SecurityUtil.getXValue(convsRequest.getConvsType(), false));
		joinRequest.setSwitchJoinYn("Y"); // 경로고객통합전환가입여부
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [conversion terms-finish-202] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());

			}
		}

		joinRequest.setTrnsType(convsRequest.getTrnsType()); // 전환, 통합여부

		List<String> bpterms = convsRequest.getBpterms();
		List<String> bptcatcds = convsRequest.getBpTcatCds();
		List<String> bptncvnos = convsRequest.getBpTncvNos();
		List<String> terms = convsRequest.getTerms();
		List<String> tcatcds = convsRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = convsRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = convsRequest.getMarketing();
		List<String> marketingChcds = convsRequest.getMarketingChcd();

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
				term.setChgChCd(channel.getChCd());
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
		termsVo.setChCd(channel.getChCd());
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

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-202] request settings : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = this.apiOnlineConvTemsProcessStep.convsTerm202Process(joinRequest);

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-202] response : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			// 전환완료 - 로그인 시 사용위해 저장
			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(certResult.getName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginid));
			WebUtil.setSession(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(loginpw));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

			// SNS 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(loginid);
				snsParam.setIncsNo(incsNo);

				snsAuth.doSnsMapping(snsParam);
			}

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

		model.addAttribute("name", certResult.getName());
		model.addAttribute("previd", convsRequest.getPrevid()); // 이전 채널아이디
		model.addAttribute("newid", loginid);
		model.addAttribute("processedDate", DateUtil.getBirthDate(DateUtil.getCurrentDate()));

		return "/convs/finish";
	}

	@PostMapping("/terms-finish-203")
	public String terms203Finish(final ConvsRequest convsRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-203] request : {}", StringUtil.printJson(convsRequest));

		final String loginid = StringUtils.hasText(SecurityUtil.getXValue(convsRequest.getId(), false)) ? SecurityUtil.getXValue(convsRequest.getId(), false).replaceAll(" ", "").toLowerCase() : ""; // 선택된 로그인 아이디, 2023-07-12 loginId 공백 제거 및 소문자 처리
		final String loginpw = SecurityUtil.getXValue(convsRequest.getPw(), false);
		final String logincpw = SecurityUtil.getXValue(convsRequest.getCpw(), false);
		String incsNo = SecurityUtil.getXValue(convsRequest.getXno(), false);

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.TRNS_TYPE_TRANSFORM, true);
		model.addAttribute("isTransform", true);
		if (StringUtils.hasText(loginid)) {
			WebUtil.setSession("chcsWebId", convsRequest.getPrevid()); // 자체회원인 경우 이 값으로 경로 임시 테이블 검색해야함.
		}
		if (StringUtils.hasText(logincpw) && StringUtils.hasText(loginpw)) {
			if (!loginpw.equals(logincpw)) {
				return "redirect:/redirect-authz-msg?chCd=" + chCd + "&msg=password.valid.error.same";
			}
		}

		CertResult certResult = this.commonService.getCertResult(incsNo);

		// 통합회원 전환
		// 채널 --> 뷰포아이디로 API 호출
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setUnm(certResult.getName());
		joinRequest.setIncsno(incsNo);
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(loginpw);
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setTransCustomer(false); // 전환처리자여부
		joinRequest.setConvsType(SecurityUtil.getXValue(convsRequest.getConvsType(), false));
		joinRequest.setSwitchJoinYn("Y"); // 경로고객통합전환가입여부
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [conversion terms-finish-203] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());

			}
		}

		joinRequest.setTrnsType(convsRequest.getTrnsType()); // 전환, 통합여부

		List<String> bpterms = convsRequest.getBpterms();
		List<String> bptcatcds = convsRequest.getBpTcatCds();
		List<String> bptncvnos = convsRequest.getBpTncvNos();
		List<String> terms = convsRequest.getTerms();
		List<String> tcatcds = convsRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = convsRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = convsRequest.getMarketing();
		List<String> marketingChcds = convsRequest.getMarketingChcd();

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
				term.setChgChCd(channel.getChCd());
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
		termsVo.setChCd(channel.getChCd());
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

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-203] request settings : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = this.apiOnlineConvTemsProcessStep.convsTerm202Process(joinRequest); // 202와 프로세스 동일함.

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-203] response : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			// 전환완료 - 로그인 시 사용위해 저장
			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(certResult.getName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginid));
			WebUtil.setSession(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(loginpw));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

			// SNS 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(loginid);
				snsParam.setIncsNo(incsNo);

				snsAuth.doSnsMapping(snsParam);
			}

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

		model.addAttribute("name", certResult.getName());
		model.addAttribute("previd", convsRequest.getPrevid()); // 이전 채널아이디
		model.addAttribute("newid", loginid);
		model.addAttribute("processedDate", DateUtil.getBirthDate(DateUtil.getCurrentDate()));
		
		return "/convs/finish";
	}

	// 통합
	@PostMapping("/terms-finish-204")
	public String terms204Finish(final ConvsRequest convsRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) throws UnsupportedEncodingException {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-204] request : {}", StringUtil.printJson(convsRequest));

		final String loginid = SecurityUtil.getXValue(convsRequest.getId(), false); // 선택된 로그인 아이디
		final String loginpw = SecurityUtil.getXValue(convsRequest.getPw(), false);
		final String logincpw = SecurityUtil.getXValue(convsRequest.getCpw(), false);
		String incsNo = SecurityUtil.getXValue(convsRequest.getXno(), false);

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.TRNS_TYPE_INTEGRATE, true);
		model.addAttribute("isIntegrated", true);

		if (StringUtils.hasText(loginid)) {
			WebUtil.setSession("chcsWebId", convsRequest.getPrevid()); // 자체회원인 경우 이 값으로 경로 임시 테이블 검색해야함.
		}

		if (StringUtils.hasText(logincpw) && StringUtils.hasText(loginpw)) {
			if (!loginpw.equals(logincpw)) {
				return "redirect:/redirect-authz-msg?chCd=" + chCd + "&msg=password.valid.error.same";
			}
		}

		CertResult certResult = this.commonService.getCertResult(incsNo);

		// 통합회원 전환
		// 채널 --> 뷰포아이디로 API 호출
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setUnm(certResult.getName());
		joinRequest.setIncsno(incsNo);
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(loginpw);
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setTransCustomer(false); // 전환처리자여부
		joinRequest.setConvsType(SecurityUtil.getXValue(convsRequest.getConvsType(), false));
		joinRequest.setSwitchJoinYn("Y"); // 경로고객통합전환가입여부
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [conversion terms-finish-204] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());

			}
		}

		joinRequest.setTrnsType(convsRequest.getTrnsType()); // 전환, 통합여부

		List<String> bpterms = convsRequest.getBpterms();
		List<String> bptcatcds = convsRequest.getBpTcatCds();
		List<String> bptncvnos = convsRequest.getBpTncvNos();
		List<String> terms = convsRequest.getTerms();
		List<String> tcatcds = convsRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = convsRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = convsRequest.getMarketing();
		List<String> marketingChcds = convsRequest.getMarketingChcd();

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
				term.setChgChCd(channel.getChCd());
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
		termsVo.setChCd(channel.getChCd());
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

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-204] request settings : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = this.apiOnlineConvTemsProcessStep.convsTerm204Process(joinRequest);

		log.debug("▶▶▶▶▶▶ [conversion terms-finish-204] response : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			// 전환완료 - 로그인 시 사용위해 저장
			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(certResult.getName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginid));
			WebUtil.setSession(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(loginpw));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

			// SNS 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(loginid);
				snsParam.setIncsNo(incsNo);

				snsAuth.doSnsMapping(snsParam);
			}

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

		model.addAttribute("name", certResult.getName());
		model.addAttribute("previd", convsRequest.getPrevid()); // 이전 채널아이디
		model.addAttribute("newid", loginid);
		model.addAttribute("processedDate", DateUtil.getBirthDate(DateUtil.getCurrentDate()));
		model.addAttribute("convsType", joinRequest.getConvsType());
		if (StringUtils.hasText(loginid)) {
			log.debug("▶▶▶▶▶▶ {} {}", OmniConstants.LOGIN_ID_COOKIE_NAME, loginid);
			WebUtil.setCookies(servletResponse, OmniConstants.LOGIN_ID_COOKIE_NAME, URLEncoder.encode(loginid.trim(), StandardCharsets.UTF_8.name()));
		}

		return "/convs/finish_end";
	}

	// A0205 통합회원 전환 완료 01 ~ 02
	@PostMapping("/finish")
	public String finish(final ConvsRequest convsRequest, final HttpServletResponse servletResponse, final HttpSession session, final Model model) throws UnsupportedEncodingException {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		log.debug("▶▶▶▶▶▶ [conversion finish] request : {}", StringUtil.printJson(convsRequest));

		final String loginid = StringUtils.hasText(SecurityUtil.getXValue(convsRequest.getId(), false)) ? SecurityUtil.getXValue(convsRequest.getId(), false).replaceAll(" ", "").toLowerCase() : ""; // 선택된 로그인 아이디, 2023-07-12 loginId 공백 제거 및 소문자 처리
		final String loginpw = SecurityUtil.getXValue(convsRequest.getPw(), false);
		final String logincpw = SecurityUtil.getXValue(convsRequest.getCpw(), false);
		String incsNo = SecurityUtil.getXValue(convsRequest.getXno(), false);

		if (StringUtils.isEmpty(incsNo)) {
			// incsNo = WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION);
		}

		final String transType = convsRequest.getTrnsType();
		boolean isTransform = true; // 전환
		boolean isIntegrated = false; // 통합

		if (transType.equals(OmniConstants.TRNS_TYPE_INTEGRATE)) {
			isIntegrated = true;
		} else if (transType.equals(OmniConstants.TRNS_TYPE_TRANSFORM)) {
			isTransform = true;
		}

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.TRNS_TYPE_TRANSFORM, isTransform);
		model.addAttribute(OmniConstants.TRNS_TYPE_INTEGRATE, isIntegrated);
		model.addAttribute("isTransform", isTransform);
		model.addAttribute("isIntegrated", isIntegrated);

		if (StringUtils.hasText(logincpw) && StringUtils.hasText(loginpw)) {
			if (!loginpw.equals(logincpw)) {
				return "redirect:/redirect-authz-msg?chCd=" + chCd + "&msg=password.valid.error.same";
			}
		}

		CertResult certResult = this.commonService.getCertResult(incsNo);

		// 통합회원 전환
		// 채널 --> 뷰포아이디로 API 호출
		JoinRequest joinRequest = new JoinRequest();
		joinRequest.setUnm(certResult.getName());
		joinRequest.setIncsno(incsNo);
		joinRequest.setLoginid(loginid);
		joinRequest.setLoginpassword(loginpw);
		joinRequest.setGender(certResult.getGender());
		joinRequest.setPhone(certResult.getPhone());
		joinRequest.setBirth(certResult.getBirth());
		joinRequest.setCi(certResult.getCiNo());
		joinRequest.setNational(certResult.getForeigner());
		joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
		joinRequest.setTransCustomer(isTransform); // 전환처리자여부
		joinRequest.setConvsType(SecurityUtil.getXValue(convsRequest.getConvsType(), false));
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if (OmniUtil.isOffline(channel)) {
			Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
			if (obj != null) {
				OfflineParam offlineParam = (OfflineParam) obj;

				log.debug("▶▶▶▶▶▶ [conversion finish] offline request : {}", StringUtil.printJson(offlineParam));

				joinRequest.setRedirectUrl(offlineParam.getReturnUrl());
				joinRequest.setJoinPrtnId(offlineParam.getJoinPrtnId());
				joinRequest.setJoinPrtnNm(offlineParam.getJoinPrtnNm());
				joinRequest.setJoinEmpId(offlineParam.getJoinEmpId()); // 2021-05-03 이크리스 추가 파라미터
				joinRequest.setAddInfo(offlineParam.getAddInfo());

			}
		}

		joinRequest.setTrnsType(convsRequest.getTrnsType()); // 전환, 통합여부 확인

		List<String> bpterms = convsRequest.getBpterms();
		List<String> bptcatcds = convsRequest.getBpTcatCds();
		List<String> bptncvnos = convsRequest.getBpTncvNos();
		List<String> terms = convsRequest.getTerms();
		List<String> tcatcds = convsRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = convsRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = convsRequest.getMarketing();
		List<String> marketingChcds = convsRequest.getMarketingChcd();

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
				term.setChgChCd(channel.getChCd());
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
		termsVo.setChCd(channel.getChCd());
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

		log.debug("▶▶▶▶▶▶ [conversion finish] request settings : {}", StringUtil.printJson(joinRequest));

		BaseResponse response = this.apiOnlineProcessStep.conversionCustomerProcess(joinRequest);

		log.debug("▶▶▶▶▶▶ [conversion finish] response : {}", StringUtil.printJson(response));

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			// 전환완료 - 로그인 시 사용위해 저장
			WebUtil.setSession(OmniConstants.XNM_SESSION, SecurityUtil.setXyzValue(certResult.getName()));
			WebUtil.setSession(OmniConstants.XID_SESSION, SecurityUtil.setXyzValue(loginid));
			WebUtil.setSession(OmniConstants.XPW_SESSION, SecurityUtil.setXyzValue(loginpw));
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

			// SNS 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(loginid);
				snsParam.setIncsNo(incsNo);
				
				snsAuth.doSnsMapping(snsParam);
			}

		} else {
			model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
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

		model.addAttribute("name", certResult.getName());
		model.addAttribute("previd", convsRequest.getPrevid()); // 이전 채널아이디
		model.addAttribute("newid", loginid);
		model.addAttribute("processedDate", DateUtil.getBirthDate(DateUtil.getCurrentDate()));
		model.addAttribute("convsType", joinRequest.getConvsType());

		if ("204".equals(convsRequest.getConvsType())) {
			if (StringUtils.hasText(loginid)) {
				log.debug("▶▶▶▶▶▶ {} {} {}", OmniConstants.LOGIN_ID_COOKIE_NAME, convsRequest.getConvsType(), loginid);
				WebUtil.setCookies(servletResponse, OmniConstants.LOGIN_ID_COOKIE_NAME, URLEncoder.encode(loginid.trim(), StandardCharsets.UTF_8.name()));
			}
		}

		return "/convs/finish";
	}

	/**
	 * <pre>
	 *  // CI 일치
	 *  		- 성명일치 -> 전환
	 *  		- 성명비일치, 휴대폰번호, 생년월일 일치 -> 전환(개명)
	 *  		- 성명비일치, 휴대폰번호, 생년월일 비일치 -> 고객센터(개명+휴대폰번호변경)
	 *  		- 2건 이상 조회 -> 이미가입된 회원
	 *  			마지막에 가입한 회원으로 전환가입
	 * 				동일한 CI의 고객이 여러명 있을시 최근에 가입한 정보부터 나오기 때문에
	 * 				제일 처음 나오는게 마지막 가입한 회원
	 * 				개인정보 판단도 해야합니다.
	 * 				여기에서 일치 정보가 2개 이상이라는건 CI/개인정보가 모두 일치하는(처음 케이스) 정보가 2개 이상일때를 의미
	 *  // CI 비일치
	 *  		- 성명, 휴대폰번호, 생년월일 일치 -> 전환 (CI 업데이트)
	 *  		- 성명, 휴대폰번호, 생년월일 비일치 -> 신규
	 * </pre>
	 */
	// @GetMapping("/check")
	@RequestMapping(value = "/check", method = { RequestMethod.GET, RequestMethod.POST })
	public String check( //
			@RequestParam(required = false) final String type, //
			@RequestParam(required = false) final String itg, //
			final HttpServletResponse servletResponse, //
			final RedirectAttributes redirectAttributes, //
			final Model model, //
			final Locale locale) {

		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);

		model.addAttribute("rv", this.config.resourceVersion());

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final String encloginid = WebUtil.getStringSession(OmniConstants.XID_SESSION);
		final String loginid = SecurityUtil.getXValue(encloginid, false);

		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute("home", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		model.addAttribute("channelName", channel.getChCdNm());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("chcd", channel.getChCd());
		model.addAttribute("loginid", loginid);
		model.addAttribute("certType", type); // 인증타입(ipin, kmcis)
		model.addAttribute("itgType", itg); // 인증타입(ipin, kmcis)
		
		if ("ipin".equals(type)) {
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, null);
		}
		
		if (WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION) != null) {
			CertResult certResult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
			log.debug("▶▶▶▶▶▶ [conversion check] check session cert result : {}", StringUtil.printJson(certResult));
			JoinResponse response = this.authStep.certConditionForConversion(certResult);
			log.debug("▶▶▶▶▶▶ [conversion check] check cert condition for conversion result : {}", response.toString());
			// 본인인증 절차 정상 완료 후 회원 계정 정보 DB 대조 결과에 따라
			// ME-FO-A0202, ME-FO-A0203, ME-FO-A0204 화면으로 분기하여 이동

			final int joinDivType = response.getType();

			JoinDivisionType joinType = JoinDivisionType.get(joinDivType);

			log.debug("▶▶▶▶▶▶ [conversion check] check join type : {}", StringUtil.printJson(JoinDivisionType.get(joinType.getType())));

			if (joinType == JoinDivisionType.WITHDRAW) {
				Customer customer = response.getCustomer();
				String withdrawDate = customer.getCustWtDttm();
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
			} else if (joinType == JoinDivisionType.CONVERSION) { // 전환가입
				Customer customer = response.getCustomer();
				if (customer != null) {

					final String incsNo = customer.getIncsNo();

					model.addAttribute(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(incsNo));

					UmOmniUser omniUser = new UmOmniUser();
					if (StringUtils.hasText(incsNo)) {
						omniUser.setIncsNo(incsNo);
					}
					omniUser.setChCd(chCd);

					// 회원 전환할때는 뷰티포인트 통합회원 약관을 동의
					// 통합회원 약관은 DB 관리하지 않기 때문에 하드코딩함.
					boolean isAgreeeTerms = this.termsService.hasTermsAgree(omniUser);
					log.debug("▶▶▶▶▶▶ [conversion check] agree terms : {} --> {}", chCd, isAgreeeTerms);

					if (!isAgreeeTerms) { // 약관동의하지 않았으면 약관을 뿌림

						TermsVo termsVo = new TermsVo();
						termsVo.setChCd(chCd);
						if(chCd.equals(OmniConstants.OSULLOC_CHCD)) {
							termsVo.setIncsNo(incsNo);
						}
						List<TermsVo> termsList = this.termsService.getTerms(termsVo);
						model.addAttribute("chterms", termsList);

					} else {
						model.addAttribute("chterms", Collections.emptyList());
					}

					LoginStepVo loginStepVo = this.authStep.conversionStep(chCd, incsNo, loginid);
					LoginType loginType = loginStepVo.getLoginType();

					log.debug("▶▶▶▶▶▶ [conversion check] conversion step typpe : {}", LoginType.get(loginType.getType()).toString());

					if (loginType == LoginType.TRNS_BP) { // ME-FO-A0204 -> 본인인증결과 통합회원으로 가입된 회원 정보는 아니나 , 중복 아이디가 존재할 경우
						log.debug("▶▶▶▶▶▶ [conversion check] A0204 -> 본인인증결과 통합회원으로 가입된 회원 정보는 아니나 , 중복 아이디가 존재할 경우");
						// 복수계정 체크는 화면에서 직접
						model.addAttribute("trnsType", OmniConstants.TRNS_TYPE_INTEGRATE);
						model.addAttribute("omniusers", loginStepVo.getOmniUsers()); // 통합 아이디 2 개 이상 체크
						model.addAttribute("chusers", loginStepVo.getChUsers());

						List<UmOmniUser> omniUsers = loginStepVo.getOmniUsers() == null ? Collections.emptyList() : loginStepVo.getOmniUsers();
						List<UmChUser> chUsers = loginStepVo.getChUsers() == null ? Collections.emptyList() : loginStepVo.getChUsers();
						boolean hastBpId = omniUsers.size() > 0;
						boolean hasChId = chUsers.size() > 0;
						if (hastBpId) {
							List<Map<String, String>> loginData = new ArrayList<>();

							for (UmOmniUser user : omniUsers) {
								Map<String, String> loginMap = new HashMap<>();
								loginMap.put("chcd", chCd);
								loginMap.put("id", user.getUmUserName());
								loginMap.put("name", customer.getCustNm());
								loginData.add(loginMap);
							}
							model.addAttribute("bpLength", loginData.size());
							model.addAttribute("bpusers", loginData);
						}

						List<Map<String, String>> loginData = new ArrayList<>();
						if (hasChId) {
							for (UmChUser user : chUsers) {
								Map<String, String> loginMap = this.customerApiService.getChannelUser(user);
								loginData.add(loginMap);

							}
							model.addAttribute("chLength", loginData.size());
							model.addAttribute("chusers", loginData);
						}

						List<UmOmniUser> chUserList = new ArrayList<>();
						for (UmChUser chusr : chUsers) {
							UmOmniUser ou = new UmOmniUser();
							ou.setUmUserName(chusr.getChcsWebId());
							if (loginData != null && !loginData.isEmpty()) {
								Map<String, String> loginMap = loginData.get(0);
								if (loginMap != null && !loginMap.isEmpty()) {
									ou.setFullName(loginMap.get("name"));	
								} else {
									ou.setFullName(customer.getCustNm());
								}
							} else {
								ou.setFullName(customer.getCustNm());
							}
							chUserList.add(ou);
						}

						model.addAttribute("xincsno", SecurityUtil.setXyzValue(omniUsers.get(0).getIncsNo()));
						model.addAttribute("bpuserlist", omniUsers);
						model.addAttribute("bpusersize", omniUsers.size());
						model.addAttribute("chuserlist", chUserList);
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
//							model.addAttribute("terms", termsList2);
							for(TermsVo vo : termsList2) {
								if(vo.getMkSn()==-20) {
									model.addAttribute("terms_marketing", vo);
								}
							}
						}
						
						return "convs/conversion_terms_a0204_ch"; // O O O
					} else if (loginType == LoginType.TRNS_CH_MINE) { // ME-FO-A0202 -> 본인인증결과 통합회원으로 가입된 계정 및 회원정보가 아닌 경우
						log.debug("▶▶▶▶▶▶ [conversion check] A0202 -> 본인인증결과 통합회원으로 가입된 계정 및 회원정보가 아닌 경우");
						model.addAttribute("trnsType", OmniConstants.TRNS_TYPE_TRANSFORM);
						boolean includeSpc = false;
						// 중복 아이디가 아닌 경우 이름 마스킹 처리;
						final String username = OmniUtil.maskUserName(certResult.getName(), locale);
						model.addAttribute("name", username);

						List<UmChUser> chUsers = loginStepVo.getChUsers();
						if (chUsers != null && !chUsers.isEmpty()) {
							UmChUser chUser = chUsers.get(0);
							if (chUser != null) {

								includeSpc = OmniUtil.includeSpecialCharInLoginId(chUser.getChcsWebId());
								// TODO 2. 로그인 아이디중 특수문자 체크
								log.debug("2) O X O 경로자체 고객 conversion_terms_a0202_ch_id loginid : {}, special char : {}", chUser.getChcsWebId(), includeSpc);

								if (chUser.getIncsNo() > 0) {
									model.addAttribute("xincsno", SecurityUtil.setXyzValue(Integer.toString(chUser.getIncsNo())));
								}
								List<Map<String, String>> loginData = new ArrayList<>();
								Map<String, String> loginMap = this.customerApiService.getChannelUser(chUser);
								loginData.add(loginMap);
								model.addAttribute("chusers", loginData);
							}
						}
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
//							model.addAttribute("terms", termsList2);
							for(TermsVo vo : termsList2) {
								if(vo.getMkSn()==-20) {
									model.addAttribute("terms_marketing", vo);
								}
							}
						}
						
						if (includeSpc) {
							return "convs/conversion_terms_a0202_ch_id"; // O X O

						} else {
							return "convs/conversion_terms_a0202_ch"; // O X O
						}

					} else if (loginType == LoginType.TRNS_CH_OTHER) { // ME-FO-A0203 -> 본인인증결과 통합회원으로 가입된 회원 정보는 아니나, 중복 아이디가 존재할 경우
						log.debug("▶▶▶▶▶▶ [conversion check] A0203 [ID변경필요]-> 본인인증결과 통합회원으로 가입된 회원 정보는 아니나, 중복 아이디가 존재할 경우");
						model.addAttribute("trnsType", OmniConstants.TRNS_TYPE_TRANSFORM);
						model.addAttribute("name", certResult.getName());
						List<UmChUser> chUsers = loginStepVo.getChUsers();
						if (chUsers != null && !chUsers.isEmpty()) {
							UmChUser chUser = chUsers.get(0);
							if (chUser != null) {
								if (chUser.getIncsNo() > 0) {
									model.addAttribute("xincsno", SecurityUtil.setXyzValue(Integer.toString(chUser.getIncsNo())));
								}
								List<Map<String, String>> loginData = new ArrayList<>();
								Map<String, String> loginMap = this.customerApiService.getChannelUser(chUser);
								loginData.add(loginMap);
								model.addAttribute("chusers", loginData);
							}
						}

						TermsVo termsVo = new TermsVo();
						termsVo.setChCd(chCd);
						if(chCd.equals(OmniConstants.OSULLOC_CHCD)) {
							termsVo.setIncsNo(incsNo);
						}
						List<TermsVo> termsList = this.termsService.getTerms(termsVo);
						model.addAttribute("chterms", termsList);

						List<TermsVo> corpTermsList = this.termsService.getCorpTerms(termsVo);
						model.addAttribute("corptermslist", corpTermsList);
						
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
//							model.addAttribute("terms", termsList2);
							for(TermsVo vo : termsList2) {
								if(vo.getMkSn()==-20) {
									model.addAttribute("terms_marketing", vo);
								}
							}
						}

						return "convs/conversion_terms_a0203_ch";
					} else {
						return "redirect:/login" + "?" + WebUtil.getSsoParams();
					}
				}
			} else if (joinType == JoinDivisionType.CHANNEL_JOIN) { // X X O 채널 만 있는 경우

				model.addAttribute("offline", OmniUtil.isOffline(channel));
				model.addAttribute("channelName", channel.getChCdNm());
				model.addAttribute("chcd", channel.getChCd());
				model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
				model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
				model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

				UmOmniUser omniUser = new UmOmniUser();
				omniUser.setChCd(chCd);

				// 회원 전환할때는 뷰티포인트 통합회원 약관을 동의
				// 통합회원 약관은 DB 관리하지 않기 때문에 하드코딩함.

				boolean isAgreeeTerms = this.termsService.hasTermsAgree(omniUser);
				log.debug("▶▶▶▶▶▶ [conversion check] agree terms : {} --> {}", chCd, isAgreeeTerms);

				if (!isAgreeeTerms) { // 약관동의하지 않았으면 약관을 뿌림

					TermsVo termsVo = new TermsVo();
					termsVo.setChCd(chCd);
					if(chCd.equals(OmniConstants.OSULLOC_CHCD)) {
						termsVo.setIncsNo(omniUser.getIncsNo());
					}
					List<TermsVo> termsList = this.termsService.getTerms(termsVo);
					model.addAttribute("chterms", termsList);

				} else {
					model.addAttribute("chterms", Collections.emptyList());
				}

				LoginStepVo loginStepVo = this.authStep.conversionStep(chCd, "", loginid);
				LoginType loginType = loginStepVo.getLoginType();

				if (loginType == LoginType.TRNS_CH_MINE) { // ME-FO-A0202 -> 본인인증결과 통합회원으로 가입된 계정 및 회원정보가 아닌 경우
					log.debug("▶▶▶▶▶▶ [conversion check] A0202 -> 본인인증결과 통합회원으로 가입된 계정 및 회원정보가 아닌 경우");
					model.addAttribute("trnsType", OmniConstants.TRNS_TYPE_TRANSFORM);
					boolean includeSpcId = OmniUtil.includeSpecialCharInLoginId(loginid);

					// TODO 3. 로그인 아이디중 특수문자 체크
					log.debug("3) X X O 경로자체 고객 conversion_terms_a0202_01_ch_id loginid : {}, special char : {}", loginid, includeSpcId);

					// 중복 아이디가 아닌 경우 이름 마스킹 처리;
					final String username = OmniUtil.maskUserName(certResult.getName(), locale);
					model.addAttribute("name", username);

					List<UmChUser> chUsers = loginStepVo.getChUsers();
					if (chUsers != null && !chUsers.isEmpty()) {
						UmChUser chUser = chUsers.get(0);
						if (chUser != null) {
							if (chUser.getIncsNo() > 0) {
								model.addAttribute("xincsno", SecurityUtil.setXyzValue(Integer.toString(chUser.getIncsNo())));
							}
							List<Map<String, String>> loginData = new ArrayList<>();
							Map<String, String> loginMap = this.customerApiService.getChannelUser(chUser);
							loginData.add(loginMap);
							model.addAttribute("chusers", loginData);
						}
					}
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
//						model.addAttribute("terms", termsList2);
						for(TermsVo vo : termsList2) {
							if(vo.getMkSn()==-20) {
								model.addAttribute("terms_marketing", vo);
							}
						}
					}
					
					if (includeSpcId) {
						return "convs/conversion_terms_a0202_01_ch_id"; // X X O
					} else {
						return "convs/conversion_terms_a0202_01_ch"; // X X O
					}
				} else if (loginType == LoginType.TRNS_CH_OTHER) { // ME-FO-A0203 -> 본인인증결과 통합회원으로 가입된 회원 정보는 아니나, 중복 아이디가 존재할 경우

					log.debug("▶▶▶▶▶▶ [conversion check] A0203 [ID변경필요]-> 본인인증결과 통합회원으로 가입된 회원 정보는 아니나, 중복 아이디가 존재할 경우");
					model.addAttribute("trnsType", OmniConstants.TRNS_TYPE_TRANSFORM);
					model.addAttribute("name", certResult.getName());
					List<UmChUser> chUsers = loginStepVo.getChUsers();
					if (chUsers != null && !chUsers.isEmpty()) {
						UmChUser chUser = chUsers.get(0);
						if (chUser != null) {
							if (chUser.getIncsNo() > 0) {
								model.addAttribute("xincsno", SecurityUtil.setXyzValue(Integer.toString(chUser.getIncsNo())));
							}
							List<Map<String, String>> loginData = new ArrayList<>();
							Map<String, String> loginMap = this.customerApiService.getChannelUser(chUser);
							loginData.add(loginMap);
							model.addAttribute("chusers", loginData);
						}
					}
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
					
					return "convs/conversion_terms_a0203_01_ch";
				} else {
					return "redirect:/go-login"; // return "redirect:/login?" + WebUtil.getSsoParams();
				}
			} else if (joinType == JoinDivisionType.INFO_MISMATCH) {

				log.debug("▶▶▶▶▶▶ join type : {}, {}", response.getType(), "고객센터");
				redirectAttributes.addFlashAttribute("types", joinType);
				WebUtil.setSession("csTypes", joinType);
				return WebUtil.getRedirectUrlWithContextPath("/mgmt/csinfo");

			} else {
				return "redirect:/redirect-authz-msg?chCd = " + chCd + "&msg=fail.message"; // return "redirect:/login" + "?" + WebUtil.getSsoParams();
			}
		}
		return "redirect:/errors";

	}

}
