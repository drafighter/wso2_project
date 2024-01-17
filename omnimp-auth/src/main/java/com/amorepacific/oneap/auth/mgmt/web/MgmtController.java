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
package com.amorepacific.oneap.auth.mgmt.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.api.vo.ivo.CustMarketingRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustMarketingVo;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaVo;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.ga.GaTaggingUtils;
import com.amorepacific.oneap.auth.join.vo.JoinResponse;
import com.amorepacific.oneap.auth.login.vo.AuthVo;
import com.amorepacific.oneap.auth.login.vo.LoginResponse;
import com.amorepacific.oneap.auth.mgmt.service.MgmtService;
import com.amorepacific.oneap.auth.mgmt.vo.NaverWithdrawVo;
import com.amorepacific.oneap.auth.search.service.SearchService;
import com.amorepacific.oneap.auth.search.vo.SearchData;
import com.amorepacific.oneap.auth.search.vo.SearchResponse;
import com.amorepacific.oneap.auth.social.handler.SnsAuth;
import com.amorepacific.oneap.common.code.ResultCode;
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
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.OfflineParam;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.PasswordVo;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.ChangePasswordData;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.InitPasswordData;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.amorepacific.oneap.common.vo.user.ChangeInfoVo;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.PointResponse;
import com.amorepacific.oneap.common.vo.user.PointVo;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;
import com.amorepacific.oneap.common.vo.user.UserData;
import com.amorepacific.oneap.common.vo.user.UserVo;
import com.amorepacific.oneap.common.vo.user.WithdrawVo;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.mgmt 
 *    |_ MgmtController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 27.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Controller
@RequestMapping("/mgmt")
public class MgmtController {

	@Value("${wso2.ssoauthorizeurl}")
	private String ssoAuthorizeUrl;

	@Value("${wso2.ssocommonauthurl}")
	private String commonAuthUrl;

	@Value("${sns.common.mappingpageurl}")
	private String snsMappingPageUrl;

	@Autowired
	private CustomerApiService customerApiService;

	@Autowired
	private MgmtService mgmtService;

	@Autowired
	private SearchService searchService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SnsAuth snsAuth;
	
	@Autowired
	private GaTaggingUtils googleGataggingUtils; // ga taagging util
	
	@Autowired
	private SystemInfo systemInfo;

	private ConfigUtil config = ConfigUtil.getInstance();

	// A0500 고객센터 연결 안내
	// @GetMapping("/csinfo")
	@RequestMapping(value = "/csinfo", method = { RequestMethod.GET, RequestMethod.POST })
	public String csInfo(final Model model) {

		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chcd);
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

		String title = "";
		String msg = "";
		Map<String, Object> md = model.asMap();
		if (md != null) {
			Object obj = md.get("types");
			log.debug("object types : {}", obj);
			if (obj instanceof Integer) {
				int types = (int) obj;

				log.debug("*** int types : {}", types);

				if (types == JoinDivisionType.INFO_MISMATCH.getType()) { // -30
					title = this.messageSource.getMessage("join.cs.mismatch.title", null, LocaleUtil.getLocale());
					// 본인인증 결과와 회원정보가 일치하지 않습니다
					msg = this.messageSource.getMessage("join.cs.mismatch.msg", null, LocaleUtil.getLocale());
				} else if (types == JoinDivisionType.EXIST.getType()) { // -5
					// 동일한 인증 정보를 지닌 회원정보들이 있습니다
					title = this.messageSource.getMessage("join.cs.exist.title", null, LocaleUtil.getLocale());
					msg = this.messageSource.getMessage("join.cs.exist.msg", null, LocaleUtil.getLocale());
				} else if (types == LoginType.CS.getType()) { // 5
					// 동일한 인증 정보를 지닌 회원정보들이 있습니다
					title = this.messageSource.getMessage("join.cs.exist.title", null, LocaleUtil.getLocale());
					msg = this.messageSource.getMessage("join.cs.exist.msg", null, LocaleUtil.getLocale());
				}
			} else {
				String types = obj.toString();

				log.debug("*** string types : {}", types);

				if (types.equals("INFO_MISMATCH")) { // -30
					title = this.messageSource.getMessage("join.cs.mismatch.title", null, LocaleUtil.getLocale());
					// 본인인증 결과와 회원정보가 일치하지 않습니다
					msg = this.messageSource.getMessage("join.cs.mismatch.msg", null, LocaleUtil.getLocale());
				} else if (types.equals("EXIST")) { // -5
					// 동일한 인증 정보를 지닌 회원정보들이 있습니다
					title = this.messageSource.getMessage("join.cs.exist.title", null, LocaleUtil.getLocale());
					msg = this.messageSource.getMessage("join.cs.exist.msg", null, LocaleUtil.getLocale());
				} else if (types.equals("CS")) { // 5
					// 동일한 인증 정보를 지닌 회원정보들이 있습니다
					title = this.messageSource.getMessage("join.cs.exist.title", null, LocaleUtil.getLocale());
					msg = this.messageSource.getMessage("join.cs.exist.msg", null, LocaleUtil.getLocale());
				}
			}
		} else {
			int cstypes = WebUtil.getSession("csTypes") != null ? (int) WebUtil.getSession("csTypes") : 0;

			log.debug("*** session types : {}", cstypes);

			if (cstypes == JoinDivisionType.INFO_MISMATCH.getType()) { // -30
				title = this.messageSource.getMessage("join.cs.mismatch.title", null, LocaleUtil.getLocale());
				// 본인인증 결과와 회원정보가 일치하지 않습니다
				msg = this.messageSource.getMessage("join.cs.mismatch.msg", null, LocaleUtil.getLocale());
			} else if (cstypes == JoinDivisionType.EXIST.getType()) { // -5
				// 동일한 인증 정보를 지닌 회원정보들이 있습니다
				title = this.messageSource.getMessage("join.cs.exist.title", null, LocaleUtil.getLocale());
				msg = this.messageSource.getMessage("join.cs.exist.msg", null, LocaleUtil.getLocale());
			} else if (cstypes == LoginType.CS.getType()) { // 5
				// 동일한 인증 정보를 지닌 회원정보들이 있습니다
				title = this.messageSource.getMessage("join.cs.exist.title", null, LocaleUtil.getLocale());
				msg = this.messageSource.getMessage("join.cs.exist.msg", null, LocaleUtil.getLocale());
			}

		}

		model.addAttribute("title", title);
		model.addAttribute("msg", msg);

		WebUtil.removeSession("csTypes");

		return "mgmt/cs_info";
	}

	// A0301 통합회원 정보관리 상세
	@GetMapping("/detail")
	public String memberDetailInfo(final HttpServletRequest request, final Model model) {

		// 회원정보 관리 화면에서 비밀번호 변경부분만 사용
		final String encloginId = WebUtil.getStringSession(OmniConstants.XID_SESSION);
		final String loginId = SecurityUtil.getXValue(encloginId, false);

		model.addAttribute("loginId", loginId);
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

		// 비밀번호 변경 완료시 SSO에 필요한 파라미터
		model.addAttribute("actionurl", this.commonAuthUrl);
		model.addAttribute("sessionDataKey", WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));
		model.addAttribute("autologin", OmniUtil.getAutoLogin(request, loginId));

		/*
		 * final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION); final Channel channel = this.commonService.getChannel(chcd);
		 * model.addAttribute("channelName", channel.getChCdNm()); model.addAttribute("chCd", channel.getChCd()); final String encincsNo =
		 * WebUtil.getStringSession(OmniConstants.XINCS_NO_SESSION); final String incsNo = SecurityUtil.getXValue(encincsNo, false);
		 * model.addAttribute("incsNo", incsNo);
		 * 
		 * CustInfoVo custInfoVo = new CustInfoVo(); custInfoVo.setIncsNo(incsNo); Customer customer =
		 * this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo); if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
		 * log.debug("◆◆◆◆◆ [memberdetailinfo] customer : {}", StringUtil.printJson(customer));
		 * 
		 * model.addAttribute("name", customer.getCustNm()); model.addAttribute("birth", DateUtil.getBirthDate(customer.getAthtDtbr())); final
		 * String gender = customer.getSxclCd().equals("M") ? "남자" : "여자"; model.addAttribute("gender", gender); model.addAttribute("mobile",
		 * StringUtil.mergeMobile(customer)); }
		 * 
		 * Iterator<SnsType> it = EnumSet.allOf(SnsType.class).iterator(); while (it.hasNext()) { String type = ((SnsType) it.next()).getType();
		 * String mt = this.mgmtService.getSnsMappingTime(type, incsNo); if (StringUtils.isEmpty(mt)) { continue; }
		 * 
		 * String attributeName = type + "mappingTime"; log.debug("◆◆◆◆◆ [memberdetailinfo] mapping: {} - {}", attributeName, mt);
		 * model.addAttribute(attributeName, mt); }
		 * 
		 * model.addAttribute("mappingPageUrl", snsMappingPageUrl);
		 */

		return "mgmt/member_detail_info";
	}

	@GetMapping("/detail/snsunlink/{type}")
	@ResponseBody
	public ApiResponse detailSnsUnlink(@PathVariable String type) {

		log.debug("◆◆◆◆◆ [detailSnsUnlink] snsType: {}", type);

		final String encincsNo = WebUtil.getStringSession(OmniConstants.XINCS_NO_SESSION);
		final String incsNo = SecurityUtil.getXValue(encincsNo, false);
		final String encloginId = WebUtil.getStringSession(OmniConstants.XID_SESSION);
		final String loginId = SecurityUtil.getXValue(encloginId, false);

		SnsParam snsParam = new SnsParam();
		snsParam.setLoginId(loginId);
		snsParam.setIncsNo(incsNo);
		snsParam.setSnsType(type);

		ApiResponse response = this.customerApiService.doSnsDisconnect(snsParam);

		log.debug("◀◀◀◀◀◀ [detailSnsUnlink] response : {}", StringUtil.printJson(response));

		return response;
	}

	// A0302 통합회원 탈퇴신청
	@GetMapping("/withdraw")
	public String memberWithdraw(final Model model, final Locale locale) {

		final String encincsno = WebUtil.getStringSession(OmniConstants.XINCS_NO_SESSION);
		log.debug("◆◆◆◆◆ [memberwithdraw] encoded incsNo : {}", encincsno);

		final String incsno = SecurityUtil.getXValue(encincsno, false);
		log.debug("◆◆◆◆◆ [memberwithdraw] decoded incsNo : {}", incsno);

		model.addAttribute(OmniConstants.XINCS_NO_SESSION, encincsno);
		model.addAttribute("xincsNo", encincsno);
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		PointVo pointVo = new PointVo();
		pointVo.setIncsNo(incsno);
		PointResponse response = this.customerApiService.getPointSearch(pointVo);

		String point = response.getRmnPt();
		double value = Double.parseDouble(point);

		DecimalFormat formatter = new DecimalFormat("###,###");
		model.addAttribute("point", formatter.format(value));
		log.debug("◆◆◆◆◆ [memberwithdraw] remian Point : {}", formatter.format(value));
		
		if(OmniConstants.NAVER_STORE_CHCD.equals(chCd)) { // 네이버 스마트 스토어를 통한 회원탈퇴 시 분기 처리
			return "mgmt/naver_member_withdraw";
		} else {
			return "mgmt/member_withdraw";
		}
	}
	
	// 네이버 스마트 스토어를 통한 탈퇴 요청 처리
	@PostMapping("/withdraw/naver")
	public String naverWithdrawProcess(final Model model, final Locale locale, final NaverWithdrawVo naverWithdrawVo) {

		final String encincsno = WebUtil.getStringSession(OmniConstants.XINCS_NO_SESSION);
		log.debug("◆◆◆◆◆ [naverWithdrawProcess] encoded xincsNo : {}", encincsno);

		final String incsno = SecurityUtil.getXValue(encincsno, false);
		log.debug("◆◆◆◆◆ [naverWithdrawProcess] decoded incsNo : {}", incsno);

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		
		log.debug("◆◆◆◆◆ [naverWithdrawProcess] naverWithdrawVo : {}", StringUtil.printJson(naverWithdrawVo));
		
		WithdrawVo withdrawVo = new WithdrawVo();
		withdrawVo.setIncsNo(Integer.parseInt(incsno));
		withdrawVo.setWtpsCd("10"); // 진행상태코드 요청 : 10
		withdrawVo.setWtrqIp(WebUtil.getClientIp());
		withdrawVo.setWtrqDttm(DateUtil.getCurrentDate());
		withdrawVo.setWtrdCd("W");
		withdrawVo.setWtrqChCd(chCd);
		withdrawVo.setWtrqPrtnId(config.getJoinPrtnCode(chCd));
		withdrawVo.setWtrsCd(naverWithdrawVo.getSelectReason());
		if (StringUtils.hasText(naverWithdrawVo.getSelectContent())) {
			withdrawVo.setWtrsTxt(naverWithdrawVo.getSelectContent());
		}

		ApiBaseResponse apiResponse = this.customerApiService.withdrawIntegratedUser(withdrawVo);
		
		return "redirect:/mgmt/withdrawfinish";
	}

	// A0303 통합회원 탈퇴완료
	@GetMapping("/withdrawfinish")
	public String memberWithdrawFinish(final Model model) {

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

		if(OmniConstants.NAVER_STORE_CHCD.equals(chCd)) { // 네이버 스마트 스토어를 통한 회원탈퇴 시 분기 처리
			return "mgmt/naver_member_withdraw_finish";			
		} else {
			return "mgmt/member_withdraw_finish";
		}
	}

	@GetMapping("/reset-pwd")
	public String resetPassword(final Model model) {

		final String loginid = WebUtil.getStringSession(OmniConstants.XID_SESSION);
		model.addAttribute(OmniConstants.XID_SESSION, loginid);
		model.addAttribute("checkId", SecurityUtil.getXValue(loginid, false));
		final String incsno = WebUtil.getStringSession(OmniConstants.XINCS_NO_SESSION);
		model.addAttribute(OmniConstants.XINCS_NO_SESSION, incsno);
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

		return "mgmt/reset_change_pwd";
	}

	@PostMapping("/do-reset-pwd")
	public String doResetPassword(final PasswordVo passwordVo, final HttpServletRequest request, final HttpServletResponse response, final HttpSession session, final Model model) throws UnsupportedEncodingException {

		log.debug("▶▶▶▶▶▶ [reset password] password : {}", StringUtil.printJson(passwordVo));
		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chcd);
		final String password = SecurityUtil.getXValue(passwordVo.getXpw(), false);
		final String confirmpassword = SecurityUtil.getXValue(passwordVo.getXcpw(), false);
		final String loginid = SecurityUtil.getXValue(passwordVo.getXid(), false);
		final String incsno = SecurityUtil.getXValue(passwordVo.getXincsno(), false);
		UserData userData = new UserData();
		userData.setChCd(chcd);
		userData.setLoginId(loginid);
		userData.setPassword(password); // 변경 비밀번호
		userData.setConfirmPassword(confirmpassword);
		userData.setIncsNo(incsno); // 통합고객번호

		boolean rtn = false;
		final UmOmniUser userinfo = this.searchService.getOmniUserInfo(userData);
		if (userinfo != null) {
			userData.setLoginId(userinfo.getUmUserName()); // DB 조회한 로그인 정보 설정
			final String dbpassword = userinfo.getUmUserPassword(); // DB 조회한 비밀번호
			rtn = SecurityUtil.compareWso2Password(dbpassword, userData.getPassword()); // 같으면 true
		}
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		// 이전 비밀번호와 같더라도 갱신시간등 이슈때문에 무조건 업데이트
		this.mgmtService.updateOmniUserPassword(userData);

		log.debug("▶▶▶▶▶▶ [reset password] password update result : {}", rtn);

		model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));

		log.debug("▶▶▶▶▶▶ [reset password] move on sso param id : {}", loginid);

		if (OmniUtil.isOffline(channel)) { // 오프라인인 경우
			model.addAttribute("actionurl", OmniUtil.getRedirectUrl(channel));
		} else {
			model.addAttribute("actionurl", this.commonAuthUrl);
		}

		String mlogin = WebUtil.getStringSession(OmniConstants.LOGIN_MOBILE_TYPE);
		String mobile = "";
		if (StringUtils.hasText(mlogin)) {
			mobile = SecurityUtil.getXValue(mlogin, false);
			log.debug("▶▶▶▶▶▶ [reset password] mobile login type : {}", mobile);
		}

		// 모바일 로그인인 경우
		if (OmniConstants.LOGIN_MOBILE.equals(mobile)) {

			final String username = SecurityUtil.getXValue(WebUtil.getStringSession(OmniConstants.XNM_SESSION), false);
			model.addAttribute(OmniConstants.XID_SESSION, username);
			model.addAttribute(OmniConstants.XPW_SESSION, incsno);

			final String authType = this.config.commonAuthType();
			log.debug("▶▶▶▶▶▶ [reset password] wso2 common auth type : {}", authType);

			boolean autologin = "Y".equals(WebUtil.getStringSession(OmniConstants.AUTO_LOGIN_SESSION));
			model.addAttribute(OmniConstants.AUTO_LOGIN, autologin);
			
			WebUtil.setCookies(response, OmniConstants.LAST_LOGIN_TYPE, "mobile");

			if (authType.equals(HttpMethod.GET.name())) {
				StringBuilder authurl = new StringBuilder();
				authurl.append(this.commonAuthUrl); //
				if (StringUtils.hasText(username)) {
					authurl.append("?fullName=").append(URLEncoder.encode(username, StandardCharsets.UTF_8.name()));
				} else {
					authurl.append("?fullName=Dummy");
				}
				authurl.append("&incsNo=").append(incsno);
				authurl.append("&sessionDataKey=").append(URLEncoder.encode(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION), StandardCharsets.UTF_8.name()));
				if (autologin) {
					authurl.append("&chkRemember=on");
				}
				//log.debug("▶▶▶▶▶▶ [reset password] wso2 common auth url : {}", authurl.toString());
				return "redirect:" + authurl.toString();
			} else {
				model.addAttribute("actionurl", this.commonAuthUrl);
				WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
				return "cert/mobile-moveon";
			}
		} else {

			WebUtil.setCookies(response, OmniConstants.LAST_LOGIN_TYPE, "basic");

			model.addAttribute(OmniConstants.XID_SESSION, loginid);
			model.addAttribute(OmniConstants.XPW_SESSION, password);
			boolean autologin = OmniUtil.getAutoLogin(request, loginid);
			model.addAttribute(OmniConstants.AUTO_LOGIN, autologin);
			final String authType = this.config.commonAuthType();
			log.debug("▶▶▶▶▶▶ wso2 common auth type : {}", authType);
			if (authType.equals(HttpMethod.GET.name())) {
				StringBuilder authurl = new StringBuilder();
				authurl.append(this.commonAuthUrl); //
				authurl.append("?username=").append(URLEncoder.encode(loginid, StandardCharsets.UTF_8.name()));
				authurl.append("&password=").append(URLEncoder.encode(password, StandardCharsets.UTF_8.name()));
				if (autologin) {
					authurl.append("&chkRemember=on");
				}
				authurl.append("&sessionDataKey=").append(URLEncoder.encode(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION), StandardCharsets.UTF_8.name()));
				//log.debug("▶▶▶▶▶▶ [sso move] wso2 common auth url : {}", authurl.toString());
				return "redirect:" + authurl.toString();
			} else {
				model.addAttribute("actionurl", this.commonAuthUrl);
				model.addAttribute(OmniConstants.XID_SESSION, passwordVo.getXid()); // - 2022.08.25 ID / PW 암호화하여 로그인 처리
				model.addAttribute(OmniConstants.XPW_SESSION, passwordVo.getXpw()); // - 2022.08.25 ID / PW 암호화하여 로그인 처리
				model.addAttribute(OmniConstants.IS_ENCRYPTION, "true");
				WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
				return "cert/moveon";
			}

		}

	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : hkdang
	 * date     : 2020. 10. 14. 오후 2:31:24
	 * </pre>
	 * 
	 * @param p
	 * @param model
	 * @return
	 */
	// A0211 비밀번호 변경
	// @GetMapping("/changepwd")
	@RequestMapping(value = "/changepwd", method = { RequestMethod.GET, RequestMethod.POST })
	public String changePassword(@RequestParam(value = "p", required = false) final String p, final Model model, final HttpServletResponse servletResponse) {
		servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		servletResponse.setHeader("Pragma", "no-cache");
		servletResponse.setDateHeader("Expires", 0);
		
		final String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		// final Channel channel = this.commonService.getChannel(chcd);
		if (StringUtils.hasText(p)) {
			String param = p.replaceAll("\\s+", "+"); // 로그인 아이디
			log.debug("▶▶▶▶▶▶ [changepassword] param : {}", param);
		}

		// 인증 결과 세션 정보
		CertResult certresult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
		if (certresult != null) {
			String searchPwdId = WebUtil.getStringSession(OmniConstants.SEARCH_PWD_ID);
			model.addAttribute("searchId", searchPwdId);
			log.debug("▶▶▶▶▶▶ [changepassword] cert result : {}, searchPwdId : {}", StringUtil.printJson(certresult), searchPwdId);

			// 인증받은 값으로 사용자 조회
			SearchResponse response = this.mgmtService.searchPasswordResult(certresult, searchPwdId);

			List<SearchData> omniUsers = response.getSearchOmniUsers() == null ? Collections.emptyList() : response.getSearchOmniUsers();
			List<SearchData> chUsers = response.getSearchChannelUsers() == null ? Collections.emptyList() : response.getSearchChannelUsers();

			int userSize = 0;

			if (omniUsers != null && omniUsers.size() > 0) {
				UserData userResponse = new UserData();
				for (SearchData user : omniUsers) {
					if (!searchPwdId.equals(user.getLoginId())) {
						log.debug("▶▶▶▶▶▶ [changepassword] Try Omni searchId: {}, loginId: {}", searchPwdId, user.getLoginId());
						continue;
					}
					userResponse.setLoginId(SecurityUtil.setXyzValue(user.getLoginId()));
					userResponse.setIncsNo(SecurityUtil.setXyzValue(user.getIncsNo()));
					userResponse.setName(SecurityUtil.setXyzValue(user.getName()));
					userResponse.setMobile(SecurityUtil.setXyzValue(user.getMobile()));
					userSize++;
				}

				log.debug("▶▶▶▶▶▶ [changepassword] Try omni user : {}", StringUtil.printJson(userResponse));

				model.addAttribute("omniResponse", userResponse);
			}

			if (chUsers != null && chUsers.size() > 0) {
				UserData userResponse = new UserData();
				for (SearchData user : chUsers) {
					if (!searchPwdId.equals(user.getLoginId())) {
						log.debug("▶▶▶▶▶▶ [changepassword] Try Channel searchId: {}, loginId: {}", searchPwdId, user.getLoginId());
						continue;
					}
					userResponse.setLoginId(SecurityUtil.setXyzValue(user.getLoginId()));
					userResponse.setIncsNo(SecurityUtil.setXyzValue(user.getIncsNo()));
					userResponse.setName(SecurityUtil.setXyzValue(user.getName()));
					userResponse.setMobile(SecurityUtil.setXyzValue(user.getMobile()));
					userSize++;
				}

				log.debug("▶▶▶▶▶▶ [changepassword] Try channel user : {}", StringUtil.printJson(userResponse));

				model.addAttribute("chResponse", userResponse);
			}

			model.addAttribute("userSize", userSize);

			// model.addAttribute("logoutlogin", OmniUtil.getLogoutRedirectLogin(channel, this.ssoAuthorizeUrl));
		} else {
			// return "redirect:/redirect-authz?chCd=" + chcd; // 인증 결과 세션 정보 없는 경우
			return "redirect:/redirect-authz-msg?chCd=" + chcd + "&msg=cert.info.empty";
		}
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		return "mgmt/change_pwd";
	}

	// A0212 비밀번호 변경 캠페인
	@GetMapping("/pwdcampaign")
	public String changePasswordCampaign(final AuthVo authVo, 
			final HttpServletRequest request, 
			final HttpServletResponse response, 
			final Model model) throws UnsupportedEncodingException {

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
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

		final String sessionDataKey = WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION);
		final String encLoginid = WebUtil.getStringSession(OmniConstants.XID_SESSION);
		final String encLoginpw = WebUtil.getStringSession(OmniConstants.XPW_SESSION);
		final String encIncsNo = WebUtil.getStringSession(OmniConstants.XINCS_NO_SESSION);

		model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, sessionDataKey);
		model.addAttribute(OmniConstants.XID_SESSION, encLoginid);
		model.addAttribute(OmniConstants.XPW_SESSION, encLoginpw);
		model.addAttribute(OmniConstants.XINCS_NO_SESSION, encIncsNo);
		
		// 아이디 저장 옵션
		// TODO 전환 사용자인 경우 아이디 선택에 따라서 아이디 저장 쿠키 삭제할 필요있음.
		// 암호화 하여 사용
		final String loginid = SecurityUtil.getXValue(encLoginid, false);
		final String cookieloginid = URLEncoder.encode(loginid.trim(), StandardCharsets.UTF_8.name());
		
		if (StringUtils.hasText(authVo.getIdSaveOption()) && "Y".equals(authVo.getIdSaveOption())) {
			WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, cookieloginid);
			// WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, SecurityUtil.setXyzValue(loginid.trim()));
			
			boolean isMobile = WebUtil.isMobile();
			
			if(isMobile) {
				if (StringUtils.hasText(authVo.getAutoLoginOption()) && "Y".equals(authVo.getAutoLoginOption())) {
					WebUtil.setCookies(response, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + cookieloginid, authVo.getAutoLoginOption());
					WebUtil.setSession(OmniConstants.AUTO_LOGIN_SESSION, "Y");
				}
			}
		} else {
			WebUtil.removeCookie(response, OmniConstants.SAVE_ID_COOKIE_NAME);
			WebUtil.removeCookie(response, OmniConstants.SAVE_AUTO_LOGIN_COOKIE_NAME + "-" + cookieloginid);
			WebUtil.setSession(OmniConstants.AUTO_LOGIN_SESSION, "N");
		}

		return "mgmt/change_pwd_campaign";
	}

	@PostMapping("/pwdstatus-next")
	public String updateLastPasswordSkipNext(final AuthVo authVo, final HttpServletRequest request, final HttpServletResponse servletResponse, final HttpSession session, final Model model) throws UnsupportedEncodingException {

		log.debug("▶▶▶▶▶▶ [update last password] update date : {}", StringUtil.printJson(authVo));
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final String loginid = SecurityUtil.getXValue(authVo.getXid(), false);
		final String password = SecurityUtil.getXValue(authVo.getXpw(), false);
		final String sessionDataKey = authVo.getSessionDataKey();
		
		

		final Channel channel = this.commonService.getChannel(chCd);
		
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

		model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, sessionDataKey);
		model.addAttribute(OmniConstants.XID_SESSION, loginid);
		model.addAttribute(OmniConstants.XPW_SESSION, password);

		if (OmniUtil.isOffline(channel)) { // 오프라인인 경우 로그인은 없음.
			SSOParam ssoParams = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
			model.addAttribute("actionurl", ssoParams.getRedirectUri());
		} else {
			model.addAttribute("actionurl", this.commonAuthUrl);
		}

		boolean autologin = OmniUtil.getAutoLogin(request, loginid);
		model.addAttribute(OmniConstants.AUTO_LOGIN, autologin);

		final String authType = this.config.commonAuthType();
		log.debug("▶▶▶▶▶▶ [update last password] wso2 common auth type : {}", authType);

		WebUtil.setCookies(servletResponse, OmniConstants.LAST_LOGIN_TYPE, "basic");

		if (authType.equals(HttpMethod.GET.name())) {

			StringBuilder authurl = new StringBuilder();
			authurl.append(this.commonAuthUrl); //
			authurl.append("?username=").append(URLEncoder.encode(loginid, StandardCharsets.UTF_8.name()));
			authurl.append("&password=").append(URLEncoder.encode(password, StandardCharsets.UTF_8.name()));
			if (autologin) {
				authurl.append("&chkRemember=on");
			}
			authurl.append("&sessionDataKey=").append(URLEncoder.encode(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION), StandardCharsets.UTF_8.name()));
			//log.debug("▶▶▶▶▶▶ [update last password] wso2 common auth url : {}", authurl.toString());
			return "redirect:" + authurl.toString();
		} else {
			model.addAttribute("actionurl", this.commonAuthUrl);
			model.addAttribute(OmniConstants.XID_SESSION, authVo.getXid()); // - 2022.08.25 ID / PW 암호화하여 로그인 처리
			model.addAttribute(OmniConstants.XPW_SESSION, authVo.getXpw()); // - 2022.08.25 ID / PW 암호화하여 로그인 처리
			model.addAttribute(OmniConstants.IS_ENCRYPTION, "true");
			WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
			return "cert/moveon";
		}
	}

	@PostMapping("/pwdstatus")
	public String updateLastPassword(final AuthVo authVo, final HttpServletRequest request, final HttpServletResponse servletResponse, final HttpSession session, final Model model) throws UnsupportedEncodingException {

		log.debug("▶▶▶▶▶▶ [update last password] update date : {}", StringUtil.printJson(authVo));
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final String loginid = SecurityUtil.getXValue(authVo.getXid(), false);
		final String password = SecurityUtil.getXValue(authVo.getXpw(), false);
		final String incsno = SecurityUtil.getXValue(authVo.getXincsno(), false);
		final String sessionDataKey = authVo.getSessionDataKey();

		final Channel channel = this.commonService.getChannel(chCd);
		
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
		
		// 휴대폰 인증 후 패스워드 변경 일자 90일 경과 시 기존 패스워드 저장
		UserData userData = new UserData();
		userData.setLoginId(loginid);
		userData.setIncsNo(incsno);

		final String dbPw = this.mgmtService.getOminUserPasswordByIncsNo(userData);
		userData.setPassword(dbPw);

		// 경로구분코드 - 필수아님
		InitPasswordData initPwdVo = new InitPasswordData();
		initPwdVo.setChCd(chCd);
		initPwdVo.setIncsNo(Integer.parseInt(incsno));
		initPwdVo.setLoginId(loginid);
		initPwdVo.setPassword(password);
		initPwdVo.setMustchange("N");

		// 20220804 Mobile 로그인과 구분 처리 - hjw0228
		// ApiBaseResponse response = this.customerApiService.initPassword(initPwdVo);
		// log.debug("▶▶▶▶▶▶ [update last password] Password Change Response : {}", StringUtil.printJson(response));
		
		final String loginMobileType = SecurityUtil.getXValue(WebUtil.getStringSession(OmniConstants.LOGIN_MOBILE_TYPE), false);
		
		// 20230522 패스워드 3개월 뒤 변경 선택 시 기존 세션들 유지하기 위해 패스워드 변경하지 않고 lastPasswordUpdate 값만 DB에 업데이트
		this.mgmtService.updateLastPasswordUpdateNow(userData);
		
		// 동일 비밀번호로 초기화 하여 비밀번호 변경 기간을 갱신만 하므로 성공여부 체크 불필요
		//if (response.getResultCode().equals(ResultCode.SUCCESS.getCode())) {
		//} else {
		//	return "redirect:/go-login";
		//}
		if(StringUtils.hasText(loginMobileType) && "MOBILE".equals(loginMobileType)) {
			initPwdVo.setPassword(dbPw);
			// 20230522 패스워드 3개월 뒤 변경 선택 시 기존 세션들 유지하기 위해 패스워드 변경하지 않고 lastPasswordUpdate 값만 DB에 업데이트
			// ApiBaseResponse response = this.customerApiService.initPasswordCurrentPassword(initPwdVo);
			// log.debug("▶▶▶▶▶▶ [update last password] Password Change Response : {}", StringUtil.printJson(response));
			
			// 휴대폰 인증 후 패스워드 변경 일자 90일 경과 시 처리를 위해 기존 패스워드로 한번 더 업데이트
			// this.mgmtService.updateUmUserPassword(userData);
			
			String username = SecurityUtil.getXValue(WebUtil.getStringSession(OmniConstants.XNM_SESSION), false);
			
			model.addAttribute("actionurl", this.commonAuthUrl);
			model.addAttribute(OmniConstants.XID_SESSION, username);
			model.addAttribute(OmniConstants.XPW_SESSION, incsno);
			model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));

			// sns 맵핑
			if (!OmniUtil.isOffline(channel)) {
				SnsParam snsParam = (SnsParam) WebUtil.getSession(OmniConstants.SNSPARAM);
				if (snsParam == null) {
					snsParam = new SnsParam();
				}
				snsParam.setLoginId(username);
				snsParam.setIncsNo(incsno);
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
				authurl.append("&incsNo=").append(incsno);
				authurl.append("&sessionDataKey=").append(URLEncoder.encode(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION), StandardCharsets.UTF_8.name()));
				if (autologin) {
					authurl.append("&chkRemember=on");
				}
				//log.debug("▶▶▶▶▶ [mobile login step] sso auth url : {}", authurl.toString());
				return "redirect:" + authurl.toString();
			} else {
				model.addAttribute("actionurl", this.commonAuthUrl);
				WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
				return "cert/mobile-moveon";
			}
		} else {
			// 20230522 패스워드 3개월 뒤 변경 선택 시 기존 세션들 유지하기 위해 패스워드 변경하지 않고 lastPasswordUpdate 값만 DB에 업데이트
			// ApiBaseResponse response = this.customerApiService.initPassword(initPwdVo);
			// log.debug("▶▶▶▶▶▶ [update last password] Password Change Response : {}", StringUtil.printJson(response));
			
			model.addAttribute(OmniConstants.SESSION_DATA_KEY_SESSION, sessionDataKey);
			model.addAttribute(OmniConstants.XID_SESSION, loginid);
			model.addAttribute(OmniConstants.XPW_SESSION, password);

			if (OmniUtil.isOffline(channel)) { // 오프라인인 경우 로그인은 없음.
				SSOParam ssoParams = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
				model.addAttribute("actionurl", ssoParams.getRedirectUri());
			} else {
				model.addAttribute("actionurl", this.commonAuthUrl);
			}

			boolean autologin = OmniUtil.getAutoLogin(request, loginid);
			model.addAttribute(OmniConstants.AUTO_LOGIN, autologin);

			final String authType = this.config.commonAuthType();
			log.debug("▶▶▶▶▶▶ [update last password] wso2 common auth type : {}", authType);

			WebUtil.setCookies(servletResponse, OmniConstants.LAST_LOGIN_TYPE, "basic");

			if (authType.equals(HttpMethod.GET.name())) {

				StringBuilder authurl = new StringBuilder();
				authurl.append(this.commonAuthUrl); //
				authurl.append("?username=").append(URLEncoder.encode(loginid, StandardCharsets.UTF_8.name()));
				authurl.append("&password=").append(URLEncoder.encode(password, StandardCharsets.UTF_8.name()));
				if (autologin) {
					authurl.append("&chkRemember=on");
				}
				authurl.append("&sessionDataKey=").append(WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION));
				//log.debug("▶▶▶▶▶▶ [update last password] wso2 common auth url : {}", authurl.toString());
				return "redirect:" + authurl.toString();
			} else {
				model.addAttribute("actionurl", this.commonAuthUrl);
				model.addAttribute(OmniConstants.XID_SESSION, authVo.getXid()); // - 2022.08.25 ID / PW 암호화하여 로그인 처리
				model.addAttribute(OmniConstants.XPW_SESSION, authVo.getXpw()); // - 2022.08.25 ID / PW 암호화하여 로그인 처리
				model.addAttribute(OmniConstants.IS_ENCRYPTION, "true");
				WebUtil.setSession(OmniConstants.IS_LOGIN_COMPLETE, true);
				if (autologin) {
					model.addAttribute(OmniConstants.AUTO_LOGIN, autologin);
				}
				return "cert/moveon";
			}	
		}
	}

	// A0307 휴대폰 번호 변경
	@GetMapping("/changephone")
	public String changePhoneNumber(final Model model) {

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

		return "/mgmt/change_phone";
	}

	// A0103 가입사실안내_01
	// @GetMapping("/joined")
	@RequestMapping(value = "/joined", method = { RequestMethod.GET, RequestMethod.POST })
	public String joinAlreday(final Model model, final Locale locale, final HttpServletRequest request) {

		final String channelCode = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(channelCode);
		model.addAttribute("offline", OmniUtil.isOffline(channel));
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());

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

		Object obj = WebUtil.getSession("response");
		Map<String, Object> md = model.asMap();
		obj = obj == null ? md.get("response") : obj;
		if (obj != null) {
			WebUtil.setSession("response", obj);
			JoinResponse response = (JoinResponse) obj;
			log.debug("▶▶▶▶▶▶ [joined] response :  {}", StringUtil.printJson(response));
			if (response != null) {
				Customer customers[] = response.getCustomerList();
				log.debug("▶▶▶▶▶▶ [joined] customers :  {}", StringUtil.printJson(customers));
				model.addAttribute("users", customers);
				model.addAttribute("multiflag", customers.length == 1 ? false : customers.length > 1 ? true : false);
			}
		}
		model.addAttribute("locale", LocaleUtil.getLocale());
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		String joinAditor=(String)WebUtil.getSession("joinAditor"); //에딧샵 App 분기 처리
		model.addAttribute("joinAditor", joinAditor);	
		String headerType = config.isHeaderType(channel.getChCd(), profile);
		model.addAttribute("headertype", headerType);
		
		return "mgmt/joined";
	}

	// A0104 가입 제한 안내
	// @GetMapping("/restrict")
	@RequestMapping(value = "/restrict", method = { RequestMethod.GET, RequestMethod.POST })
	public String joinRestrict(@RequestParam(value = "restrict", required = false) String restrict, final Model model) {
		String channelCode = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		if (StringUtils.isEmpty(channelCode)) {
			Object ssoObj = WebUtil.getSession(OmniConstants.SSOPARAM);
			log.debug("▶▶▶▶▶▶ [restrict] channelCode is null ssoObj :  {}", StringUtil.printJson(ssoObj));
			if (ssoObj != null) {
				channelCode = ((SSOParam) ssoObj).getChannelCd();
				WebUtil.setSession(OmniConstants.CH_CD_SESSION, channelCode);
			}
		}

		final Channel channel = this.commonService.getChannel(channelCode);
		model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		log.debug("▶▶▶▶▶▶ [restrict] channelCode: {}, homeurl : {}", channelCode, OmniUtil.getRedirectUrl(channel));

		Map<String, Object> md = model.asMap();
		Object obj = md.get("customer");
		if (obj != null) {
			Customer customer = (Customer) obj;
			log.debug("▶▶▶▶▶▶ [restrict] customer :  {}", StringUtil.printJson(customer));
			model.addAttribute("withdrawDate", DateUtil.getBirthDate(customer.getCustWtDttm()));
		}

		if (StringUtils.hasText(restrict)) {
			if ("Y".equalsIgnoreCase(restrict)) {
				model.addAttribute("homeurl", channel.getHmpgUrl());
				model.addAttribute("joinrestrict", "Y");
			}
		}
		model.addAttribute("homeurl", channel.getHmpgUrl());
		return "mgmt/restrict";
	}

	// A0300 통합회원 정보관리 PW 입력
	@RequestMapping(value = "/info", method = { RequestMethod.GET, RequestMethod.POST })
	public String info(final AuthVo authVo, final Model model) {

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

		final String encloginid = authVo.getXid();
		String loginid = SecurityUtil.getXValue(encloginid, false);
		final String encincsno = authVo.getXincsno();
		String incsno = SecurityUtil.getXValue(encincsno, false);

		if (StringUtils.isEmpty(loginid)) {
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(incsno);
			Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null) {
				loginid = customer.getChcsNo();
			}
		}

		// model.addAttribute("loginid", OmniUtil.maskUserId(loginid));
		model.addAttribute("loginid", loginid);
		model.addAttribute("xloginid", SecurityUtil.setXyzValue(loginid));

		return "mgmt/info";
	}

	/**
	 * <pre>
	 * comment  : 현재 비밀번호가 맞는지 체크 
	 * author   : hkdang
	 * date     : 2020. 10. 22. 오전 10:48:15
	 * </pre>
	 * 
	 * @param userVo
	 * @return
	 */
	@PostMapping("/pwdcheck")
	@ResponseBody
	public LoginResponse passwordCheck(@RequestBody final UserVo userVo) {

		LoginResponse response = new LoginResponse();
		final String loginid = SecurityUtil.getXValue(userVo.getEncId(), false);
		String loginpwd = userVo.getUmUserPassword();
		String cpwd = userVo.getEncConfirmPwd(); // 확인 비밀번호 필요시 여기에 매핑해서 쓴다. 암호화된 값은 아님

		log.debug("▶▶▶▶▶▶ [mgmt passwordCheck] user : {}, loginid : {} ", StringUtil.printJson(userVo), loginid);

		if (StringUtils.isEmpty(loginid) || StringUtils.isEmpty(loginpwd)) {
			response.setResult("EMPTY");
			response.setStatus(-5);
		} else {
			if (StringUtils.hasText(cpwd)) {
				if (!loginpwd.equals(cpwd)) {
					response.setResult("SAME");
					response.setStatus(-10);
					return response;
				}

				response.setResult("VALID");
				response.setStatus(100);

			} else { // 확인비밀번호 없으면 마이페이지 접근용 -> 현재 패스워드와 비교
				String xincsno = WebUtil.getStringSession(OmniConstants.XINCS_NO_SESSION);
				String incsNo = SecurityUtil.getXValue(xincsno, false);

				UserData userData = new UserData();
				userData.setLoginId(loginid);
				userData.setIncsNo(incsNo);

				String dbPw = this.mgmtService.getOminUserPasswordByIncsNo(userData);
				if (SecurityUtil.compareWso2Password(dbPw, userVo.getUmUserPassword())) {
					response.setResult("VALID");
					response.setStatus(100);
				} else {
					response.setResult("WRONG");
					response.setStatus(-20);
				}
			}
		}

		log.debug("▶▶▶▶▶ [mgmt pwd check] response : {}", StringUtil.printJson(response));
		return response;
	}

	@PostMapping("/changeinfo/{type}")
	@ResponseBody
	public BaseResponse changeInfo(@PathVariable("type") final String type, @RequestBody ChangeInfoVo changeInfoVo) {
		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final String encloginid = WebUtil.getStringSession(OmniConstants.XID_SESSION);
		final String loginid = SecurityUtil.getXValue(encloginid, false);
		log.info("encloginid : {}", encloginid);
		log.info("loginid : {}", loginid);
		final String encincsno = WebUtil.getStringSession(OmniConstants.XINCS_NO_SESSION);
		final String incsno = SecurityUtil.getXValue(encincsno, false);
		BaseResponse response = new BaseResponse();

		if ("checkPwd".equals(type)) {
			UserData userData = new UserData();
			userData.setLoginId(loginid);
			userData.setIncsNo(incsno);

			String dbPw = this.mgmtService.getOminUserPasswordByIncsNo(userData);
			if (!SecurityUtil.compareWso2Password(dbPw, changeInfoVo.getCpw())) {
				response.SetResponseInfo(ResultCode.PWD_WRONG);
			} else {
				response.SetResponseInfo(ResultCode.SUCCESS);
			}

			return response;

		} else if ("pwd".equals(type)) {
			// 모두 평문 (입력값)
			final String currentPwd = changeInfoVo.getCpw();
			final String newPwd = changeInfoVo.getNpw();
			final String newConfirmPwd = changeInfoVo.getNcpw();

			log.debug("▶▶▶▶▶▶  [mgmt changeinfo pwd] loginId : {}, incsNo : {} changeInfoVo : {}", loginid, incsno, StringUtil.printJson(changeInfoVo));

			// 아이디와 동일한 비밀번호 인지 체크
			if (loginid.equals(newPwd)) {
				response.SetResponseInfo(ResultCode.PWD_SAME_ID);
				return response;
			}

			// 입력값 체크
			UserData userData = new UserData();
			userData.setLoginId(loginid);
			userData.setIncsNo(incsno);

			String dbPw = this.mgmtService.getOminUserPasswordByIncsNo(userData);
			if (!SecurityUtil.compareWso2Password(dbPw, currentPwd)) {
				response.SetResponseInfo(ResultCode.PWD_WRONG);
				return response;
			}

			// 이전 비밀번호와 동일한 비밀번호 체크
			if (newPwd.equals(currentPwd)) {
				response.SetResponseInfo(ResultCode.PWD_FAIL_SAME);
				return response;
			}

			// 비밀번호 확인값 체크
			if (!newPwd.equals(newConfirmPwd)) {
				response.SetResponseInfo(ResultCode.PWD_FAIL_CONFIRM);
				return response;
			}

			// API call
			ChangePasswordData chgPwdVo = new ChangePasswordData();
			chgPwdVo.setIncsNo(Integer.parseInt(incsno)); // 통합고객번호
			chgPwdVo.setLoginId(loginid); // 회원아이디
			chgPwdVo.setCurrentPassword(currentPwd); // 현재 비밀번호
			chgPwdVo.setChangePassword(newPwd); // 변경 비밀번호
			chgPwdVo.setConfirmPassword(newConfirmPwd); // 변경 비밀번호 확인

			ApiBaseResponse apiResponse = this.customerApiService.changePassword(chgPwdVo);
			response.setResultCode(apiResponse.getResultCode());
			response.setMessage(apiResponse.getMessage());

			return response;

		} else if ("widhdraw".equals(type)) {

			final String enccurrentpwd = changeInfoVo.getCpw();
			final String currentpwd = SecurityUtil.getXValue(enccurrentpwd, false);
			final String wso2pwd = SecurityUtil.getEncodedWso2Password(currentpwd);

			boolean match = false;
			List<UmOmniUser> userList = this.mgmtService.getOmniUserList(Integer.parseInt(incsno));
			if (userList != null && !userList.isEmpty()) {

				for (UmOmniUser user : userList) {
					if (user.getUmUserPassword().equals(wso2pwd)) {
						match = true;
						break;
					}
				}

			}

			if (!match) {
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				return response;
			}

			WithdrawVo withdrawVo = new WithdrawVo();
			withdrawVo.setIncsNo(Integer.parseInt(incsno));
			withdrawVo.setWtpsCd("10"); // 진행상태코드 요청 : 10
			withdrawVo.setWtrqIp(WebUtil.getClientIp());
			withdrawVo.setWtrqDttm(DateUtil.getCurrentDate());
			withdrawVo.setWtrdCd("W");
			withdrawVo.setWtrqChCd(chCd);
			withdrawVo.setWtrsCd(changeInfoVo.getReason());
			if (StringUtils.hasText(changeInfoVo.getContent())) {
				withdrawVo.setWtrsTxt(changeInfoVo.getContent());
			}

			ApiBaseResponse apiResponse = this.customerApiService.withdrawIntegratedUser(withdrawVo);
			if (apiResponse.getResultCode().equals(ResultCode.SUCCESS.getCode())) {
				response.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				return response;
			}
		} else if ("terms".equals(type)) {

			CustInfoVo custInfoVo = new CustInfoVo();
			// 통합고객번호 있을경우 회원 여부 체크
			if (StringUtils.hasText(incsno)) {
				custInfoVo.setIncsNo(incsno);
			}

			// 통합고객번호로 사용자를 다시 찾기
			Customer customerincsno = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customerincsno != null && StringUtils.hasText(customerincsno.getCiNo())) {
				custInfoVo.setCiNo(customerincsno.getCiNo());
			} else {
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				return response; // CI 가 없으면 오류로 처리(아래 진행하면 문제 발생)
			}

			CustInfoResponse custinfoResponse = this.customerApiService.getCustList(custInfoVo);

			log.debug("▶▶▶▶▶▶ ① integrated customer exist check response : {}", StringUtil.printJson(custinfoResponse));
//			ICITSVCOM000 : 정상
//			ICITSVCOM003 : 필수항목 누락
//			ICITSVCOM001 : 통합고객이 존재하지 않습니다
//			ICITSVBIZ155 : 휴면고객정보가 존재
//			ECOMSVVAL004 : 중복된 {0} 입니다.   (멤버십카드번호, CI번호)
//			ICITSVCOM999 : 시스템오류
//			ICITSVBIZ135: 성명정보 불일치 
			if (custinfoResponse.getRsltCd().equals("ICITSVCOM000")) { // 있으면 수정 프로세스
				response.setResultCode(custinfoResponse.getRsltCd());
			} else {
				log.debug("▶▶▶▶▶▶ ① customer check : {}, {}", custinfoResponse.getRsltCd(), custinfoResponse.getRsltMsg());
				response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
				return response;
			}

			Customer customers[] = custinfoResponse.getCicuemCuInfTcVo();
			if (customers != null && customers.length > 0) {
				Customer customer = customers[0]; // 첫번째 데이터가 최신임.
				//if ("Y".equalsIgnoreCase(customer.getCustWtYn())) { // 탈퇴사용자
				if (StringUtils.hasText(customer.getCustWtDttm())) {
					log.debug("▶▶▶▶▶▶ ① customer check withdraw : {}[{}]", customer.getCustWtYn(), customer.getCustWtDttm());
					// response.setResultCode("ICITSVCOM001"); // ICITSVCOM002
					response.SetResponseInfo(ResultCode.USER_DISABLED);
					return response;
				}
			}

			boolean success = true;

			List<ChangeInfoVo.ChangeTerms> bpterms = changeInfoVo.getBpterms();

			if (bpterms != null && !bpterms.isEmpty()) {
				CustTncaRequest custTncaRequest = new CustTncaRequest();
				List<CustTncaVo> custTncaVos = new ArrayList<>();
				for (ChangeInfoVo.ChangeTerms term : bpterms) {
					log.debug(">>>> bpterms {}", StringUtil.printJson(term));
					CustTncaVo terms = new CustTncaVo();
					terms.setTcatCd(term.getTcatCd());
					terms.setIncsNo(incsno);
					terms.setTncvNo(term.getTncvNo());
					terms.setTncAgrYn(term.getTncAgrYn().equals("on") ? "Y" : "N");
					terms.setLschId("OCP");
					terms.setChgChCd(chCd);
					terms.setChCd(chCd);
					custTncaVos.add(terms);
				}
				CustTncaVo custTncaVoArr[] = custTncaVos.toArray(new CustTncaVo[custTncaVos.size()]);
				custTncaRequest.setCicuedCuTncaTcVo(custTncaVoArr);
				ApiResponse termResponse = this.customerApiService.savecicuedcutnca(custTncaRequest);
				success &= "ICITSVCOM000".equals(termResponse.getRsltCd());

				if (success) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
				} else {
					response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				}
			}

			List<ChangeInfoVo.ChangeTerms> marketings = changeInfoVo.getMarketing();

			// 수신 동의 처리
			if (marketings != null && !marketings.isEmpty()) {
				CustMarketingRequest custMarketingRequest = new CustMarketingRequest();
				List<CustMarketingVo> custMarketingVos = new ArrayList<>();
				for (ChangeInfoVo.ChangeTerms joinMarketing : marketings) {
					CustMarketingVo marketing = new CustMarketingVo();
					marketing.setChCd(chCd);
					marketing.setIncsNo(incsno);
					marketing.setEmlOptiYn("N");
					marketing.setSmsOptiYn(joinMarketing.getTncAgrYn().equals("on") ? "Y" : "N");
					marketing.setDmOptiYn("N");
					marketing.setTmOptiYn("N");
					marketing.setKkoIntlOptiYn("N");
					marketing.setFscrId("OCP");
					marketing.setLschId("OCP");
					custMarketingVos.add(marketing);
				}
				CustMarketingVo cicuemCuOptiTcVoArr[] = custMarketingVos.toArray(new CustMarketingVo[custMarketingVos.size()]);
				custMarketingRequest.setCicuemCuOptiTcVo(cicuemCuOptiTcVoArr);
				ApiResponse marketingResponse = this.customerApiService.savecicuemcuoptilist(custMarketingRequest);

				success &= "ICITSVCOM000".equals(marketingResponse.getRsltCd());
				if (success) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
				} else {
					response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				}
			}
		}
		return response;
	}

}
