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
package com.amorepacific.oneap.auth.search.web;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amorepacific.oneap.auth.cert.service.CertService;
import com.amorepacific.oneap.auth.cert.vo.CertData;
import com.amorepacific.oneap.auth.common.service.ApiService;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.login.vo.AuthVo;
import com.amorepacific.oneap.auth.search.service.SearchService;
import com.amorepacific.oneap.auth.search.vo.SearchData;
import com.amorepacific.oneap.auth.search.vo.SearchResponse;
import com.amorepacific.oneap.auth.terms.service.TermsService;
import com.amorepacific.oneap.auth.terms.vo.TermsVo;
import com.amorepacific.oneap.common.exception.OmniException;
import com.amorepacific.oneap.common.matcher.AntUrlPathMatcher;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.user.UserData;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.search 
 *    |_ SearchController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 27.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Controller
@RequestMapping("/search")
public class SearchController {

	@Value("${omni.auth.domain}")
	private String authDomain;

	@Autowired
	private CommonService commonService;

	@Autowired
	private CertService certService;

	@Autowired
	private SearchService searchService;

	@Autowired
	private TermsService termsService;

	@Autowired
	private ApiService apiService;

	@Autowired
	private SystemInfo systemInfo;

	private ConfigUtil config = ConfigUtil.getInstance();

	// A0208 ID 찾기 01
	@GetMapping("/id")
	public String searchId(final HttpServletRequest request, final Model model) {
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

		final String prvUrl = WebUtil.getHeader("referer");
		List<Object> referers = (List<Object>) this.config.getList("login.referers");

		for (Object obj : referers) {
			boolean match = new AntUrlPathMatcher(obj.toString()).matches(prvUrl, null);
//			log.debug("referers : {} --> {}, match : {}", obj.toString(), prvUrl, match);
			if (match) {
				model.addAttribute("url", prvUrl);
				break;
			}
		}

		return "search/search_id";
	}

	// A0209 ID 찾기 결과 01 ~ 02
	@PostMapping("/id-result")
	public String searchIdResult(final CertResult certresult, final Model model) {

		log.debug("▶▶▶▶▶▶ [searchidresult] certresult : {}", StringUtil.printJson(certresult));
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		
		// 2022-12-08 모의해킹/앱스캔 조치 결과에 따라 세션에서 인증 결과값이 성공이 아닐 경우 잘못된 접근으로 예외 처리
		final int smsAuthStatus = StringUtils.isEmpty(WebUtil.getSession(OmniConstants.SMS_AUTH_STATUS)) ? 0 : (int) WebUtil.getSession(OmniConstants.SMS_AUTH_STATUS);
		if(smsAuthStatus != OmniConstants.SMS_AUTH_SUCCESS) {
			log.error("ID 찾기 인증 결과 실패 : {}", smsAuthStatus);
			throw new OmniException("인증에 실패하였습니다.");
		}
		
		/*
		 * final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION); // // 통합고객조회 플랫폼 API를 사용하고 // 해당 결과 통합고객번호로 통합고객번호로 뷰포와 채널의
		 * 아이디 조회 SearchResponse response = this.searchService.searchIdResult(certresult, chCd);
		 */

		// SMS 인증 받은 아이디 그대로 출력
		SearchResponse response = (SearchResponse) WebUtil.getSession(OmniConstants.SEARCH_CERT_USER_LIST);
		log.debug("▶▶▶▶▶▶ [searchidresult] SearchResponse : {}", StringUtil.printJson(response));
		//

		final String xmobileno = WebUtil.getStringSession(OmniConstants.XMOBILE_NO_SESSION);
		if (StringUtils.hasText(xmobileno)) {
			model.addAttribute(OmniConstants.XMOBILE_NO_SESSION, xmobileno);
		}
		if (StringUtils.hasText(xmobileno)) {
			response.setMobile(xmobileno);
		}

		List<SearchData> omniUsers = response.getSearchOmniUsers() == null ? Collections.emptyList() : response.getSearchOmniUsers();
		List<SearchData> chUsers = response.getSearchChannelUsers() == null ? Collections.emptyList() : response.getSearchChannelUsers();

		int userSize = omniUsers.size() + chUsers.size();

		model.addAttribute("userSize", userSize);
		model.addAttribute("response", response);
		model.addAttribute("ssoparam", WebUtil.getSsoParams());

		// 닫기버튼 눌렀을때 이동
		model.addAttribute("url", "../login?" + WebUtil.getSsoParams());

		return "search/search_id_result";
	}

	// A0210 비밀번호 찾기 01 ~ 02
	// @GetMapping("/pwd")
	@RequestMapping(value = "/pwd", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchPassword(final HttpServletRequest request, final HttpServletResponse response,final Model model) {
		final String channelCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);

		final Channel channel = this.commonService.getChannel(channelCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

		// 인증방식 초기 데이터 로딩(KMICS PHONE, NICE IPIN)
		final CertData certData = this.certService.certInit(channelCd);

		model.addAttribute("certdata", certData);

		final String prvUrl = WebUtil.getHeader("referer");
		List<Object> referers = (List<Object>) this.config.getList("login.referers");

		for (Object obj : referers) {
			boolean match = new AntUrlPathMatcher("*/" + obj.toString()).matches(prvUrl, null);
//			log.debug("▶▶▶▶▶▶ referers : {} --> {}, match : {}", obj.toString(), prvUrl, match);
			if (match) {
				model.addAttribute("url", prvUrl);
				break;
			}
		}
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		model.addAttribute("manualcert", this.config.avaiableManualCert(profile));

		model.addAttribute("type", "spws");
		model.addAttribute("mobile", WebUtil.isMobile());
		WebUtil.setSession("certiType", "spws");
		WebUtil.setCookies(response, OmniConstants.ONE_AP_CERTIFICATION_TYPE, "spws");

		return "search/search_pwd";
	}

	@PostMapping("/id-check")
	@ResponseBody
	public BaseResponse idCheck(@RequestBody final AuthVo authVo) {
		BaseResponse response = new BaseResponse();
		final String loginid = authVo.getLoginId();
		boolean rtn = this.searchService.hasLoginid(loginid);
		if (rtn) {
			response.setStatus(1);
			response.setResultCode(SecurityUtil.setXyzValue(loginid));

			WebUtil.setSession(OmniConstants.SEARCH_PWD_ID, loginid);
		} else {
			response.setStatus(-1);
		}
		return response;
	}

	@PostMapping("/pwd-check-update")
	@ResponseBody
	public BaseResponse passwordCheckAndUpdate(@RequestBody final UserData userData) {

		log.debug("▶▶▶▶▶▶ [pwd-check-update] Param {}", StringUtil.printJson(userData));

		// 사용자 정보 복호화 후 세팅
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);

		UserData omniUserData = new UserData();
		if (StringUtils.hasText(userData.getOmniLoginId()) //
				&& StringUtils.hasText(userData.getOmniIncsNo()) //
				&& StringUtils.hasText(userData.getPassword()) //
				&& StringUtils.hasText(userData.getConfirmPassword()) //
		) {
			omniUserData.setChCd(chCd);
			omniUserData.setLoginId(SecurityUtil.getXValue(userData.getOmniLoginId(), false));
			omniUserData.setIncsNo(SecurityUtil.getXValue(userData.getOmniIncsNo(), false));
			omniUserData.setName(SecurityUtil.getXValue(userData.getOmniName(), false));
			// omniUserData.setMobile(SecurityUtil.getXValue(userData.getOmniMobile(), false));
			omniUserData.setPassword(SecurityUtil.getXValue(userData.getPassword(), false)); // 변경 비밀번호
			omniUserData.setConfirmPassword(SecurityUtil.getXValue(userData.getConfirmPassword(), false));
		} else {
			omniUserData = null;
		}

		log.debug("▶▶▶▶▶▶ [pwd-check-update] omniUserData : {}", StringUtil.printJson(omniUserData));

		UserData chUserData = new UserData();
		if (StringUtils.hasText(userData.getChLoginId()) //
				&& StringUtils.hasText(userData.getPassword()) //
				&& StringUtils.hasText(userData.getConfirmPassword()) //
		) {
			chUserData.setChCd(chCd);
			chUserData.setLoginId(SecurityUtil.getXValue(userData.getChLoginId(), false));
			chUserData.setName(SecurityUtil.getXValue(userData.getChName(), false));
			// chUserData.setMobile(SecurityUtil.getXValue(userData.getChMobile(), false));
			chUserData.setPassword(SecurityUtil.getXValue(userData.getPassword(), false)); // 변경 비밀번호
			chUserData.setConfirmPassword(SecurityUtil.getXValue(userData.getConfirmPassword(), false));
			
			if(StringUtils.hasText(userData.getChIncsNo())) {
				chUserData.setIncsNo(SecurityUtil.getXValue(userData.getChIncsNo(), false));
			}
			
		} else {
			chUserData = null;
		}

		log.debug("▶▶▶▶▶▶ [pwd-check-update] chUserData : {}", StringUtil.printJson(chUserData));

		return this.searchService.passwordCheckAndUpdate(omniUserData, chUserData);
	}

	// 약관상세 페이지 조회
	@PostMapping("/agree-detail")
	@ResponseBody
	public String agreeDetail(final TermsVo termsVo, final Model model) {
		log.debug("▶▶▶▶▶▶ [agree detail] : {}", StringUtil.printJson(termsVo));
		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		if (OmniUtil.isOffline(channel)) {
			chCd = ChannelPairs.getOnlineCd(chCd);
		}
		// occuem_tnc 에서 조회 tnc_txt_url 약관내용URL
		final String agreeurl = termsVo.getTncTxtUrl();

		final HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
		headers.setContentType(new MediaType("text", "html", Charset.forName("UTF-8")));
		headers.setAccept(Collections.singletonList(new MediaType("text", "html", Charset.forName("UTF-8"))));
		ResponseEntity<String> response = this.apiService.get(agreeurl, headers, String.class);
		log.debug("▶▶▶▶▶▶ [agree detail] term content http status : {}", response.getStatusCode());
		if (response.getStatusCode() == HttpStatus.OK) {
			return response.getBody();
		} else {
			return "";
		}
	}

	@GetMapping("/terms-test")
	public String termTest(final @RequestParam(value = "type", required = true) String type, final @RequestParam(value = "chCd", required = true) String chCd, final Model model) {

		TermsVo termsVo = new TermsVo();
		termsVo.setChCd(chCd);
		List<TermsVo> termsList = this.termsService.getTerms(termsVo);

		final HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
		headers.add(HttpHeaders.TRANSFER_ENCODING, "chunked");
		headers.setContentType(new MediaType("text", "html", Charset.forName("UTF-8")));
		headers.setAccept(Collections.singletonList(new MediaType("text", "html", Charset.forName("UTF-8"))));
		//headers.setContentLength(4294967295L);
		for (TermsVo terms : termsList) {
			if (terms.getTcatCd().contains(type)) {
				String agreeurl = terms.getTncTxtUrl();
				ResponseEntity<String> response = this.apiService.get(agreeurl, headers, String.class);
				
				log.info("▶▶▶▶▶▶ [terms-test] term url : {}, content {}, http status : {}", agreeurl, response.hasBody(), response.getStatusCode());
				log.info("▶▶▶▶▶▶ [terms-test] term content type {}, content length : {}", response.getHeaders().getContentType(), response.getHeaders().getContentLength());
				
				if (response.getStatusCode() == HttpStatus.OK) {
					model.addAttribute("term", response.getBody());
				}
			}
		}

		return "info/termtest";
	}

	@GetMapping("/terms-detail")
	public String termsDetail(final @RequestParam(value = "type") String type, @RequestParam(value = "chCd") String chCd, final Model model) {
		log.debug("▶▶▶▶▶▶ [terms detail] : {} {}", chCd, type);
		
		final Channel channel = this.commonService.getChannel(chCd);
		if (OmniUtil.isOffline(channel)) {
			chCd = ChannelPairs.getOnlineCd(chCd);
		}
		
		if ("use".equals(type)) {
			return "info/useterms" + chCd;
		} else if ("per".equals(type)) {
			return "info/usetermspersonal" + chCd;
		} else if ("per1".equals(type)) {
			return "info/usetermspersonalagree" + chCd;
		}
		return "info/useterms" + chCd;
	}

}
