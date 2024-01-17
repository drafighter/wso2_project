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
package com.amorepacific.oneap.auth.cert.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.auth.cert.service.CertService;
import com.amorepacific.oneap.auth.cert.vo.CertData;
import com.amorepacific.oneap.auth.cert.vo.IpinResult;
import com.amorepacific.oneap.auth.cert.vo.KmcisResult;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.login.vo.AuthVo;
import com.amorepacific.oneap.auth.login.vo.LoginVo;
import com.amorepacific.oneap.auth.login.vo.SmsAuthVo;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.ObjectUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ManualParam;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.cert.CertType;
import com.amorepacific.oneap.common.vo.sms.SmsVo;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.cert 
 *    |_ CertController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 27.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Controller
@RequestMapping("/cert")
public class CertController {

	@Autowired
	private CertService certService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private SystemInfo systemInfo;
	

	private ConfigUtil config = ConfigUtil.getInstance();

	// A0400 본인인증
	@GetMapping("")
	public String cert(final HttpServletRequest request, final HttpServletResponse response, final LoginVo loginVo, final Model model) {
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		log.info("▶▶▶▶▶▶ [Request URI = /cert], Session ID : {}, User-Agent : {}", WebUtil.getRequest().getSession().getId(), request.getHeader("user-agent"));

		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		
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
		
		// chCd = StringUtils.isEmpty(chCd) ? OmniConstants.JOINON_CHCD : chCd; - 채널 코드 기본값 없음 
		log.info("▶▶▶▶▶▶ [chCd] : {}", chCd);

		// 인증방식 초기 데이터 로딩(KMICS PHONE, NICE IPIN)
		final CertData certData = this.certService.certInit(chCd);

		model.addAttribute("certdata", certData);

		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		model.addAttribute("manualcert", this.config.avaiableManualCert(profile));

		model.addAttribute("type", "convs");
		model.addAttribute("mobile", WebUtil.isMobile());

		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute("homeurl", channel.getHmpgUrl());
		model.addAttribute("home", channel.getHmpgUrl());
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, channel.getHmpgUrl());
		WebUtil.setSession("certiType", "convs");
		WebUtil.setCookies(response, OmniConstants.ONE_AP_CERTIFICATION_TYPE, "convs");

		return "cert/certificate";
	}

	@GetMapping("/ipin-phone")
	public String ipinPhone(final Model model) {
		CertResult certResult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
		if (certResult != null) {
			model.addAttribute("name", certResult.getName());
		}

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

		if (OmniUtil.isOffline(channel)) {
			model.addAttribute("itg", "check");
		}

		return "cert/ipin_phone";
	}
	
	@GetMapping("/convs-ipin-phone")
	public String convsIpinPhone(final Model model) {
		CertResult certResult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
		if (certResult != null) {
			model.addAttribute("name", certResult.getName());
		}

		final String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

		if (OmniUtil.isOffline(channel)) {
			model.addAttribute("itg", "check");
		}

		return "cert/convs_ipin_phone";
	}

	@RequestMapping(value = "/ipin-result", method = { RequestMethod.GET, RequestMethod.POST })
	public String ipinCertResult(final HttpServletRequest request, final Model model) {
		
		try {
			String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
			// 채널 코드가 Null 일 경우 세션이 아닌 쿠키에서 값 추출 (오프라인 파라미터인 경우에 한함)
			/*
			 * if(StringUtils.isEmpty(chCd)) { Cookie cookie = WebUtil.getCookies(request, OmniConstants.ONE_AP_OFFLINE_PARAM+"-"+OmniConstants.CH_CD);
			 * if(cookie != null) { log.info("▶▶▶▶▶▶ [Channel Code from Cookies] : {}", chCd); WebUtil.setSession(OmniConstants.CH_CD_SESSION,
			 * SecurityUtil.getXValue(cookie.toString(), false)); } }
			 */
			final IpinResult ipinResultParam = ObjectUtil.convertMapToObject(WebUtil.convertRequestToMap(), IpinResult.class);
			final Channel channel = this.commonService.getChannel(chCd);
			model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
			model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));

			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));

			final IpinResult ipinResult = this.certService.certIpinResult(chCd, ipinResultParam);

			if (ipinResult.getResult() > 0) {

				CertResult certresult = new CertResult();
				certresult.setCiNo(ipinResult.getCoInfo());
				certresult.setName(ipinResult.getName());
				certresult.setGender(ipinResult.getGenderCode().equals("0") ? "F" : "M"); // 1: 남자. 0: 여자
				certresult.setGenderCode(ipinResult.getGenderCode());
				certresult.setBirth(ipinResult.getBirthDate());
				certresult.setChCd(chCd);
				certresult.setForeigner(ipinResult.getNationalInfo().equals("0") ? "K" : "F");
				certresult.setCategory("아이핀");

				log.debug("▶▶▶▶▶▶ [ipin result] : {}", StringUtil.printJson(certresult));

				// 가입 제한 연령(14세 미만) 체크
				boolean restrict = DateUtil.isJoinRestrictByAuth(certresult);
				if (restrict) {
					log.debug("▶▶▶▶▶▶ [ipin result] age : {}", certresult.getBirth());
					model.addAttribute("restrict", true);

				} else { // 만 14세 이상 가입 가능
					model.addAttribute("restrict", false);
				}

				// 중요 : 인증 결과 세션에 저장
				WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certresult);

			}

			model.addAttribute("ipinresult", ipinResult);
			if(!StringUtils.isEmpty(WebUtil.getStringSession("certiType"))) {
				model.addAttribute("certiType", WebUtil.getStringSession("certiType"));
				log.debug("▶▶▶▶▶▶ [ipin certiType from Session] : {}", WebUtil.getStringSession("certiType"));
			} else {
				Cookie cookie = WebUtil.getCookies(request, OmniConstants.ONE_AP_CERTIFICATION_TYPE);
				if(cookie != null) model.addAttribute("certiType", cookie.toString());
				log.debug("▶▶▶▶▶▶ [ipin certiType from Cookie] : {}", cookie);
			}
			model.addAttribute("mobile", WebUtil.isMobile());
		} catch (Exception e) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_IPIN_CERTIFICATION_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			
			log.error("Cert IPIN Result = {}", e.getMessage());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		}

		return "cert/ipin_result";
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/kmcis-result-popup", method = { RequestMethod.GET, RequestMethod.POST })
	public String kmcCertResultPopup(final HttpServletRequest request, final Model model) {
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		log.info("▶▶▶▶▶▶ [Request URI = /cert/kmcis-result-popup], Session ID : {}, User-Agent : {}", WebUtil.getRequest().getSession().getId(), request.getHeader("user-agent"));

		final String certdata = WebUtil.getStringParameter("rec_cert");
		final String certnum = WebUtil.getStringParameter("certNum");
		log.info("▶▶▶▶▶▶ /cert/kmcis-result-popup [certdata] : {}, [certnum] : {}", certdata, certnum);
		
		Enumeration params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String name = (String) params.nextElement();
			log.info("▶▶▶▶▶▶ /cert/kmcis-result-popup From Parameter ▶▶▶▶▶▶ " + name + " : " + request.getParameter(name) + "     ");
		}
		
		model.addAttribute("certdata", certdata);
		model.addAttribute("certnum", certnum);

		return "cert/kmcis_result_popup";
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/kmcis-result", method = { RequestMethod.GET, RequestMethod.POST })
	public String kmcCertResult(final HttpServletRequest request, final Model model) {
		
		try {
			String certdata = WebUtil.getStringParameter("rec_cert");
			String certnum = WebUtil.getStringParameter("certNum");
			log.debug("▶▶▶▶▶▶ /cert/kmcis-result [certdata] : {}, [certnum] : {}", certdata, certnum);
			Enumeration params = request.getParameterNames();
			while (params.hasMoreElements()) {
				String name = (String) params.nextElement();
				log.debug("▶▶▶▶▶▶ /cert/kmcis-result From Parameter ▶▶▶▶▶▶ " + name + " : " + request.getParameter(name) + "     ");
				if(name.equals(certdata) && StringUtils.isEmpty(certdata)) certdata = request.getParameter(name);
				if(name.equals(certnum) && StringUtils.isEmpty(certnum)) certdata = request.getParameter(name);
			}
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			log.info("▶▶▶▶▶▶ [Request URI = /cert/kmcis-result], Session ID : {}, User-Agent : {}", WebUtil.getRequest().getSession().getId(), request.getHeader("user-agent"));

			String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
			log.info("▶▶▶▶▶▶ [chCd] : {}", chCd);
			// 채널 코드가 Null 일 경우 세션이 아닌 쿠키에서 값 추출 (오프라인 파라미터인 경우에 한함)
			/*
			 * if(StringUtils.isEmpty(chCd)) { Cookie cookie = WebUtil.getCookies(request, OmniConstants.ONE_AP_OFFLINE_PARAM+"-"+OmniConstants.CH_CD);
			 * if(cookie != null) { log.info("▶▶▶▶▶▶ [Channel Code from Cookies] : {}", chCd); WebUtil.setSession(OmniConstants.CH_CD_SESSION,
			 * SecurityUtil.getXValue(cookie.toString(), false)); } }
			 */

			final Channel channel = this.commonService.getChannel(chCd);
			log.info("▶▶▶▶▶▶ [channel] : {}", channel);
			model.addAttribute("homeurl", OmniUtil.getRedirectUrl(channel));
			model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
			model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
			model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
			final KmcisResult kmcisResult = this.certService.certKmcisResult(certdata.trim(), certnum.trim());
			log.info("▶▶▶▶▶▶ [Request URI = /cert/kmcis-result], Session ID : {}, User-Agent : {}, User-CI : {}", WebUtil.getRequest().getSession().getId(), request.getHeader("user-agent"), kmcisResult.getCi());
			if (kmcisResult.getStatus() > 0) {
				CertResult certresult = new CertResult();
				certresult.setCiNo(kmcisResult.getCi());
				certresult.setName(kmcisResult.getName());
				certresult.setGender(kmcisResult.getGender().equals("0") ? "M" : "F"); // 성별 정보 ( ▪ “0” : 남자 ▪ “1” : 여자 )
				certresult.setGenderCode(kmcisResult.getGender());
				certresult.setBirth(kmcisResult.getBirthDay());
				certresult.setPhone(kmcisResult.getPhoneNo());
				certresult.setChCd(chCd);
				certresult.setForeigner(kmcisResult.getNation().equals("0") ? "K" : "F"); // 내•외국인 정보 ( ▪ “0” : 내국인 ▪ “1” : 외국인 )
				certresult.setCategory("휴대폰");

				log.debug("▶▶▶▶▶▶ [kmcis result] : {}", StringUtil.printJson(certresult));

				// 가입 제한 연령(14세 미만) 체크
				boolean restrict = DateUtil.isJoinRestrictByAuth(certresult);
				if (restrict) {
					log.debug("▶▶▶▶▶▶ [kmcis result] age :{}", certresult.getBirth());
					model.addAttribute("restrict", true);

				} else { // 만 14세 이상 가입 가능
					model.addAttribute("restrict", false);
				}

				// 중요 : 인증 결과 세션에 저장
				WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certresult);
			}

			model.addAttribute("kmcisresult", kmcisResult);
			if(!StringUtils.isEmpty(WebUtil.getStringSession("certiType"))) {
				model.addAttribute("certiType", WebUtil.getStringSession("certiType"));
				log.info("▶▶▶▶▶▶ [kmcis certiType from Session] : {}", WebUtil.getStringSession("certiType"));
			} else {
				Cookie cookie = WebUtil.getCookies(request, OmniConstants.ONE_AP_CERTIFICATION_TYPE);
				if(cookie != null) model.addAttribute("certiType", cookie.toString());
				log.info("▶▶▶▶▶▶ [kmcis certiType from Cookie] : {}", cookie);
			}
			
			model.addAttribute("mobile", WebUtil.isMobile());	
			log.debug("▶▶▶▶▶▶ [mobile] : {}", WebUtil.isMobile());
		} catch (Exception e) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_KMC_CERTIFICATION_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, request);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			
			log.error("Cert KMCIS Result = {}", e.getMessage());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		}

		return "cert/kmcis_result";
	}

	@PostMapping("/sendsms")
	@ResponseBody
	public SmsVo sendSms(@RequestBody final SmsAuthVo smsAuthVo) {

		// 회원가입할 경우 통합고객번호가 없으므로 가상으로 생성하고 처리해야함.
		// type=ipin값을 전송해서 이 값이 있으면 가상의 incsno 생성
		SmsVo vo = this.certService.sendSms(smsAuthVo);

		log.debug("▶▶▶▶▶▶ [send sms] set session incsno to sms : {}, sms vo : {}", vo.getIncsNo(), StringUtil.printJson(vo));

		// if (vo.getIncsNo() > 0) {
		// WebUtil.setSession(OmniConstants.INCS_NO_SESSION, vo.getIncsNo());
		// }

		if (vo.getStatus() == OmniConstants.SMS_AUTH_SUCCESS) { // 성공인 경우 다른 페이지에서 사용하도록 암호화된 휴대폰 번호 세션에 저장
			WebUtil.setSession(OmniConstants.XMOBILE_NO_SESSION, SecurityUtil.setXyzValue(smsAuthVo.getUserPhone()));
		}

		return vo;
	}

	@PostMapping("/invalidsms")
	@ResponseBody
	public boolean invalidSms(@RequestBody final SmsAuthVo smsAuthVo) {
		// 제한 시간 지나간 데이터는 삭제
		return this.certService.invalidSms(smsAuthVo);
	}

	@PostMapping("/sendsms/{type}")
	@ResponseBody
	public SmsVo sendSmsType(@PathVariable("type") final String type, @RequestBody final SmsAuthVo smsAuthVo, HttpServletResponse response) throws UnsupportedEncodingException {

		log.debug("▶▶▶▶▶▶ [send sms type({})] send sms eai : {}, {}", type, StringUtil.printJson(smsAuthVo));

		// 아이디 결과에서 선택한 아이디
		final String selectdLoginid = SecurityUtil.getXValue(smsAuthVo.getUserName()); // 2022-12-02 selectedLoginid 에 암호화 로직 적용 -> 복호화 후 처리
		
		smsAuthVo.setUserName(selectdLoginid);
		// SMS 발송 시 생성한 전화번호 세션이용
		final String mobileno = SecurityUtil.getXValue(smsAuthVo.getUserPhone(), false);
		smsAuthVo.setUserPhone(mobileno);

		if ("id".equals(type)) {
			if (StringUtils.hasText(selectdLoginid)) {
				WebUtil.setCookies(response, OmniConstants.LOGIN_ID_COOKIE_NAME, URLEncoder.encode(selectdLoginid.trim(), StandardCharsets.UTF_8.name()));
				WebUtil.setCookies(response, OmniConstants.SAVE_ID_COOKIE_NAME, URLEncoder.encode(selectdLoginid.trim(), StandardCharsets.UTF_8.name()));
			}
		}

		log.debug("▶▶▶▶▶▶ [send sms type({})] send sms loginid : {}, mobile no : {}", type, selectdLoginid, mobileno);

		return this.certService.sendSmsType(type, smsAuthVo);

	}

	@PostMapping("/authsms")
	@ResponseBody
	public SmsVo authSms(@RequestBody final SmsVo smsVo) {

		if (StringUtils.isEmpty(smsVo.getSmsAthtNoVl())) {
			smsVo.setSmsAthtNoVl(smsVo.getSmsNo());
		}

		log.debug("▶▶▶▶▶▶ [auth sms] vo : {}", StringUtil.printJson(smsVo));

		final String phoneno = SecurityUtil.getXValue(smsVo.getPhoneNo(), false);
		final String paramusername = smsVo.getName(); // final String paramusername = SecurityUtil.getXValue(smsVo.getName(), false);

		if (StringUtils.hasText(paramusername)) {
			WebUtil.setSession(OmniConstants.XNM_SESSION, smsVo.getName());
		}

		// 인증번호 정상 인증 시 로그인 처리 후 랜딩 화면으로 이동
		// 아이디 미등록 회원일 경우 ME-FO-A0207 화면으로 이동

		final SmsVo authSmsVo = this.certService.authSms(smsVo);

		log.debug("▶▶▶▶▶▶ [auth sms] response : {}", StringUtil.printJson(authSmsVo));

		// SMS 인증에 성공하면 다른 페이지에서 사용하도록 암호화된 통합고객번호 세션에 저장
		if (authSmsVo.getStatus() == OmniConstants.SMS_AUTH_SUCCESS) {

			// IPIN 본인인증은 휴대폰 번호내려주지 않음.
			// IPIN 인증 시 암호화된 휴대폰 번호 받아서 처리
			if (StringUtils.hasText(smsVo.getPhoneNo())) {

				WebUtil.removeSession("smsIncsNo");

				// 본인인증에서 인증번호 확인할 경우
				if (WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION) != null) {

					CertResult certresult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);

					log.debug("▶▶▶▶▶▶ [auth sms] session cert result : phone {}, {}", phoneno, StringUtil.printJson(certresult));

					certresult.setPhone(phoneno);
					certresult.setCategory("SNS");

					final String certusername = certresult.getName();
					log.debug("▶▶▶▶▶▶ [auth sms] param : {} == cert : {} ---> {}", paramusername, certusername, paramusername.equals(certusername));

					log.debug("▶▶▶▶▶▶ [auth sms] session cert result set phone : {}", StringUtil.printJson(certresult));
					WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certresult);
				} else {
					// 세션에 저장한 인증정보가 없을 경우 실패 처리(본인인증이 아닌 경우는 실패처리하면 오류발생)
					// authSmsVo.setStatus(OmniConstants.SMS_AUTH_SEND_FAIL);
				}
			}

			// 2022-12-08 모의해킹/앱스캔 조치 결과에 따라 SMS 인증에 성공하면 성공 여부 세션에 저장
			WebUtil.setSession(OmniConstants.SMS_AUTH_STATUS, authSmsVo.getStatus());
			
			WebUtil.setSession(OmniConstants.XINCS_NO_SESSION, SecurityUtil.setXyzValue(Integer.toString(authSmsVo.getIncsNo())));
			log.debug("▶▶▶▶▶▶ [auth sms] sms auth success, set session incs no : {}", WebUtil.getSession(OmniConstants.XINCS_NO_SESSION));

		}

		return authSmsVo;
	}

	@PostMapping("/manual-cert")
	public String manualCert(final AuthVo authVo, final Model model) {
		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		if (StringUtils.hasText(authVo.getLoginId())) {
			WebUtil.setSession(OmniConstants.SEARCH_PWD_ID, authVo.getLoginId());
		}
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, channel.getHmpgUrl());

		model.addAttribute("type", WebUtil.getStringSession("certiType"));
		model.addAttribute("mobile", WebUtil.isMobile());
		WebUtil.setSession("certiType", WebUtil.getStringSession("certiType"));

		return "cert/manual-cert";
	}

	@PostMapping("/manual-cert-result")
	public String manualCertResult(final ManualParam manualParam, final Model model) {

		CertResult certresult = new CertResult();
		certresult.setCiNo(manualParam.getUserCi().replaceAll("\r", "").replaceAll("\n", ""));
		certresult.setName(manualParam.getUserName());
		certresult.setGender(manualParam.getUserGender()); // 1: 남자. 0: 여자
		certresult.setGenderCode(manualParam.getUserGender());
		certresult.setBirth(manualParam.getUserBirth());
		certresult.setChCd(manualParam.getChCd());
		certresult.setForeigner(manualParam.getUserForeigner());
		certresult.setPhone(manualParam.getUserMobile());

		log.debug("▶▶▶▶▶▶ [manual cert result] : {}", StringUtil.printJson(certresult));

		if (StringUtils.isEmpty(certresult.getPhone())) {
			model.addAttribute("certType", "ipin");
		} else {
			model.addAttribute("certType", "kmcis");
		}

		// 가입 제한 연령(14세 미만) 체크
		boolean restrict = DateUtil.isJoinRestrictByAuth(certresult);
		if (restrict) {
			log.debug("▶▶▶▶▶▶ [manual cert result] ipin restrict age : {}", certresult.getBirth());
			model.addAttribute("restrict", true);
		} else { // 만 14세 이상 가입 가능
			model.addAttribute("restrict", false);
		}
		String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		final Channel channel = this.commonService.getChannel(chCd);
		model.addAttribute(OmniConstants.CH_CD, channel.getChCd());
		model.addAttribute(OmniConstants.CH_NM, channel.getChCdNm());
		model.addAttribute(OmniConstants.RD_URL, OmniUtil.getRedirectUrl(channel));
		model.addAttribute("mobile", WebUtil.isMobile());
		model.addAttribute("type", WebUtil.getStringSession("certiType"));
		model.addAttribute("certiType", WebUtil.getStringSession("certiType"));

		// 중요 : 인증 결과 세션에 저장
		WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certresult);

		return "cert/manual_cert_result";
	}
	
	@PostMapping("/cert-type")
	@ResponseBody
	public String setCertSession(@RequestBody final CertType certType) {
		
		//WebUtil.setSession("certiType", "convs");
		//WebUtil.setSession("certiType", "mbrs");
		//WebUtil.setSession("certiType", "spws");
		
		log.debug("▶▶▶▶▶▶ cert type : {}", StringUtil.printJson(certType));
		
		WebUtil.setSession("certiType", certType.getCertiType());
		
		return "1";
		
	}
	
	@PostMapping("/omnicryptovalue/encode")
	@ResponseBody
	public String encodeOmniCryptoValue(@RequestBody final String param) {
		String value = SecurityUtil.setXyzValue(param) ;
		return value;
	}
	
}
