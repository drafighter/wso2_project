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
 * Date   	          : 2020. 8. 5..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.cert.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.api.vo.ivo.UpdateCustCiVo;
import com.amorepacific.oneap.auth.api.vo.ovo.UpdateCustCiResponse;
import com.amorepacific.oneap.auth.cert.vo.CertData;
import com.amorepacific.oneap.auth.cert.vo.IpinData;
import com.amorepacific.oneap.auth.cert.vo.IpinResult;
import com.amorepacific.oneap.auth.cert.vo.KmcisData;
import com.amorepacific.oneap.auth.cert.vo.KmcisResult;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.common.service.SmsService;
import com.amorepacific.oneap.auth.login.vo.SmsAuthVo;
import com.amorepacific.oneap.auth.mgmt.service.MgmtService;
import com.amorepacific.oneap.auth.search.vo.SearchData;
import com.amorepacific.oneap.auth.search.vo.SearchResponse;
import com.amorepacific.oneap.auth.util.CertUtil;
import com.amorepacific.oneap.auth.validation.KmcisValidator;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.LocaleUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.UuidUtil;
import com.amorepacific.oneap.common.util.UuidUtil.Type;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.SearchChCustRequest;
import com.amorepacific.oneap.common.vo.api.SearchChCustResponse;
import com.amorepacific.oneap.common.vo.api.UserInfo;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.sms.SmsResponse;
import com.amorepacific.oneap.common.vo.sms.SmsVo;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UserData;
import com.icert.comm.secu.IcertSecuManager;

import Kisinfo.Check.IPIN2Client;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.cert 
 *    |_ CertService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 5.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class CertService {

	@Value("${nice.site.password}")
	private String niceSitePassword;

	@Value("${nice.ipin.cert.url}")
	private String niceIpinCertUrl;

	@Value("${nice.ipin.result.url}")
	private String niceIpinResultUrl;

	@Value("${kmcis.cert.url}")
	private String kmcisCertUrl;

	@Value("${kmcis.result.url}")
	private String kmcisResultUrl;

	@Autowired
	private CommonService commonService;

	@Autowired
	private SmsService smsService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CustomerApiService customerApiService;

	@Autowired
	private MgmtService mgmtService;
	
	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();

	/**
	 * 
	 * <pre>
	 * comment  : 본인인증 초기 정보 세팅
	 * 
	 * 휴대폰 : KMCIS
	 * 아이핀 : NICE
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 12. 오전 10:52:52
	 * </pre>
	 * 
	 * @return
	 */
	public CertData certInit(final String chCd) {

		CertData certData = new CertData();

		final String channelCd = StringUtils.isEmpty(chCd) ? OmniConstants.JOINON_CHCD : chCd;

		final Channel channel = this.commonService.getChannel(channelCd);

		// 1. 휴대폰 본인인증
		KmcisData kmcisData = new KmcisData();
		kmcisData.setCertNum(UuidUtil.getUuid(Type.ALPHANUMERIC)); // 본인인증 요청시 중복되지 않게 생성해야함. (예-시퀀스번호)
		IcertSecuManager scumngr = new IcertSecuManager();

		final String cert = new StringBuilder(channel.getKmcChnId()) // this.kmcisSiteCode cpId
				.append("/").append(channel.getKmcChnUrlCdVl()) // this.kmcisUrlCode urlCode
				.append("/") //
				.append(kmcisData.getCertNum()) // certNum
				.append("/") //
				.append(DateUtil.getCurrentDateString("yyyyMMddHHmmss")) // date
				.append("/") //
				.append("M") // certMet
				.append("/") //
				.append("") // birthDay
				.append("/") //
				.append("") // gender
				.append("/") //
				.append("")// name
				.append("/") //
				.append("") // phoneNo
				.append("/") //
				.append("") // phoneCorp
				.append("/") //
				.append("") // nation
				.append("/") //
				.append("") // plusInfo
				.append("/") //
				.append(CertUtil.KMCIS_EXPAND_VAR) // extendVar
				.toString();
		log.debug("▶▶▶▶▶▶ [cert] kmcis data cert : {}", cert);

		// 1차 암호화 (tr_cert 데이터변수 조합 후 암호화)
		final String kmcisencdata = scumngr.getEnc(cert, "");
		// 1차 암호화 데이터에 대한 위변조 검증값 생성 (HMAC)
		final String kmcishmacdata = scumngr.getMsg(kmcisencdata);
		// 2차 암호화 (1차 암호화 데이터, HMAC 데이터, extendVar 조합 후 암호화)
		final String kmciscertdata = scumngr.getEnc(kmcisencdata + "/" + kmcishmacdata + "/" + CertUtil.KMCIS_EXPAND_VAR, "");

		kmcisData.setCertData(kmciscertdata);
		kmcisData.setCertUrl(this.kmcisCertUrl);
		kmcisData.setResultUrl(this.kmcisResultUrl);

		log.debug("▶▶▶▶▶▶ [cert] kmcis data : {}", StringUtil.printJson(kmcisData));

		certData.setKmcisData(kmcisData);

		// 2. 아이핀 본인인증
		IpinData ipinData = new IpinData();
		ipinData.setCertUrl(this.niceIpinCertUrl);
		// 객체 생성
		IPIN2Client pClient = new IPIN2Client();

		final String sCPRequest = pClient.getRequestNO(); // this.niceSiteCode
		WebUtil.setSession("CPREQUEST", sCPRequest);

		ipinData.setCpRequest(sCPRequest);
		// Method 결과값(iRtn)에 따라, 프로세스 진행여부를 파악합니다.
		int rtn = pClient.fnRequest(channel.getIpinSiteCdVl(), this.niceSitePassword, sCPRequest, this.niceIpinResultUrl);
		ipinData.setResult(rtn);
		if (rtn == 0) {
			final String ipinencdata = pClient.getCipherData();
			ipinData.setEncData(ipinencdata);
		}

		// log.debug("▶▶▶▶▶▶ [cert] ipin data : {}", StringUtil.printJson(ipinData));

		certData.setIpinData(ipinData);

		return certData;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 아이핀 본인인증 결과 생성
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 12. 오전 10:52:45
	 * </pre>
	 * 
	 * @param ipinResult
	 * @return
	 */
	public IpinResult certIpinResult(final String chCd, final IpinResult ipinResult) {

		log.debug("▶▶▶▶▶▶ [cert] ipin result : {}", ipinResult.toString());

		final String channelCd = StringUtils.isEmpty(chCd) ? OmniConstants.JOINON_CHCD : chCd;
		final Channel channel = this.commonService.getChannel(channelCd);

		final String encData = CertUtil.sanitizeNiceData(ipinResult.getEnc_data(), CertUtil.NICE_ENCODE_DATA);
		ipinResult.setEnc_data(encData);
		final String param1 = CertUtil.sanitizeNiceData(ipinResult.getParam_r1(), "");
		ipinResult.setParam_r1(param1);
		final String param2 = CertUtil.sanitizeNiceData(ipinResult.getParam_r2(), "");
		ipinResult.setParam_r2(param2);
		final String param3 = CertUtil.sanitizeNiceData(ipinResult.getParam_r3(), "");
		ipinResult.setParam_r3(param3);

		ipinResult.setResult(-1);

		// 암호화된 사용자 정보가 존재하는 경우
		if (StringUtils.hasText(ipinResult.getEnc_data())) {
			String sCpRequest = (String) WebUtil.getSession("CPREQUEST");

			// 객체 생성
			IPIN2Client pClient = new IPIN2Client();

			int rtn = 0;
			if (StringUtils.hasText(sCpRequest)) {
				rtn = pClient.fnResponse(channel.getIpinSiteCdVl(), this.niceSitePassword, ipinResult.getEnc_data(), sCpRequest);
			} else {
				rtn = pClient.fnResponse(channel.getIpinSiteCdVl(), this.niceSitePassword, ipinResult.getEnc_data());
			}

			ipinResult.setResult(rtn);

			ipinResult.setVnNumber(pClient.getVNumber()); // 가상주민번호 (13자리이며, 숫자 또는 문자 포함)
			ipinResult.setName(pClient.getName()); // 이름
			ipinResult.setDupInfo(pClient.getDupInfo()); // 중복가입 확인값 (DI - 64 byte 고유값)
			ipinResult.setAgeCode(pClient.getAgeCode()); // 연령대 코드 (개발 가이드 참조)
			ipinResult.setGenderCode(pClient.getGenderCode()); // 성별 코드 (개발 가이드 참조)
			ipinResult.setBirthDate(pClient.getBirthDate()); // 생년월일 (YYYYMMDD)
			ipinResult.setNationalInfo(pClient.getNationalInfo()); // 내/외국인 정보 (개발 가이드 참조)
			ipinResult.setCpRequestNo(pClient.getCPRequestNO()); // CP 요청번호
			ipinResult.setAuthInfo(pClient.getAuthInfo()); // 본인확인 수단 (개발 가이드 참조)
			ipinResult.setCoInfo(pClient.getCoInfo1()); // 연계정보 확인값 (CI - 88 byte 고유값)
			ipinResult.setCiUpdate(pClient.getCIUpdate()); // CI 갱신정보
		}

		return ipinResult;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 휴대폰 본인인증 결과 생성
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 6. 오후 2:29:43
	 * </pre>
	 * 
	 * @param certdata
	 * @param certnum
	 * @return
	 */
	public KmcisResult certKmcisResult(final String certdata, final String certnum) {
		KmcisResult result = CertUtil.getKmcisData(certdata, certnum);
		log.debug("▶▶▶▶▶▶ [cert] kmcert result : {}", StringUtil.printJson(result));
		result = KmcisValidator.validate(result);
		log.debug("▶▶▶▶▶▶ [cert] kmcert validate result : {}", StringUtil.printJson(result));
		return result;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 21. 오후 12:12:47
	 * </pre>
	 * 
	 * @param smsAuthVo
	 * @return
	 */
	public SmsVo sendSms(SmsAuthVo smsAuthVo) {
		
		final StopWatch stopwatch = new StopWatch("Send SMS Service"); 

		log.debug("▶▶▶▶▶ [send sms] info : {}", StringUtil.printJson(smsAuthVo));

		// sms 발송
		SmsVo smsVo = new SmsVo();
		smsVo.setStatus(1);

		String incsNo = "";
		if (StringUtils.hasText(smsAuthVo.getType())) {
			if (!stopwatch.isRunning()) {
				stopwatch.start("ipin");
			}
			if ("ipin".equals(smsAuthVo.getType())) {

				incsNo = WebUtil.getStringSession("smsIncsNo");
				if (StringUtils.isEmpty(incsNo)) {
					incsNo = UuidUtil.getVirtualIncsNo();
					WebUtil.setSession("smsIncsNo", incsNo);
				}
				log.debug("▶▶▶▶▶ [send sms] 회원가입 IPIN 인증 시 -> 가상 통합고객번호 : {}", incsNo);
				smsVo.setFscrId("joinuser-ipin");
				smsVo.setPhoneNo(smsAuthVo.getUserPhone());
			}
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
		} else {
			if (!stopwatch.isRunning()) {
				stopwatch.start("search integration user");
			}
			int smsUserCount = 0;

			SearchResponse searchUsers = new SearchResponse();
			List<SearchData> searchOmniUsers = new ArrayList<>();
			List<SearchData> searchChannelUsers = new ArrayList<>();

			// 통합고객 조회
			//log.debug("▶▶▶▶▶ [send sms] 통합고객플랫폼 조회 사용자명 : {}, 휴대폰번호 : {}", smsAuthVo.getUserName(), smsAuthVo.getUserPhone());
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setCustName(smsAuthVo.getUserName());
			custInfoVo.setCustMobile(smsAuthVo.getUserPhone());

			// 통합 API 로 고객 정보 받아옴
			CustInfoResponse response = this.customerApiService.getCustList(custInfoVo);
			//log.debug("▶▶▶▶▶ [send sms] customer list = {}", StringUtil.printJson(response.getCicuemCuInfTcVo()));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			if (response != null) {

				if ("ICITSVBIZ152".equals(response.getRsltCd())) { // 탈퇴 고객
					smsVo.setStatus(OmniConstants.SMS_AUTH_WITHDRAW_USER);
					smsVo.setRsltCd(ResultCode.USER_NOT_FOUND.getCode());
					smsVo.setRsltMsg(ResultCode.USER_NOT_FOUND.message());
					return smsVo;
				}

				Customer users[] = response.getCicuemCuInfTcVo();
				if (users != null && users.length > 0) {

					// 현재 정의된 비지니스 로직 상으로는 휴대폰 인증 확인 후에 
					// 동일한 통합고객번호에 대해 ID가 2개 이상인 Case에 대해서만 Exception 처리 
					// 휴대폰 문자 인증 시에는 CI 값은 확인 불가한 내용임 (인증기관을 통한 본인인증 처리가 아님)
					// 휴대폰 인증 후에 고객통합번호가 2개 이상인 경우 Exception 처리하도록 고객 협의 후에 해당 내용 추가 반영 예정
					// 휴대폰 인증 시에 휴대폰번호와 이름으로 조회한 고객통합번호가 2개 이상인 경우
					
					if (smsAuthVo.isPhoneLogin()) {
						if (users.length > 1) {
							smsVo.setStatus(OmniConstants.SMS_AUTH_PHONELOGIN_DUPLICATE);
							smsVo.setRsltCd(ResultCode.USER_NOT_FOUND.getCode());
							smsVo.setRsltMsg(ResultCode.USER_NOT_FOUND.message());
							return smsVo;
						}
					}
					
					Customer user = users[0]; // 데이터가 최신순으로 정렬되어 전달되므로 첫번째 데이터선택하면 됨.
					log.debug("▶▶▶▶▶ [send sms] customer user : {}", StringUtil.printJson(user));
					
					if ("ICITSVBIZ152".equals(user.getRsltCd())) { // 탈퇴 고객
						smsVo.setStatus(OmniConstants.SMS_AUTH_WITHDRAW_USER);
						smsVo.setRsltCd(ResultCode.USER_NOT_FOUND.getCode());
						smsVo.setRsltMsg(ResultCode.USER_NOT_FOUND.message());
						return smsVo;
					}

					if (StringUtils.hasText(user.getIncsNo())) {
						if (!stopwatch.isRunning()) {
							stopwatch.start("search omni user");
						}
						UserData userData = new UserData();
						userData.setIncsNo(user.getIncsNo());
						List<UserData> omniUsers = this.mgmtService.getOmniUserLoginIdList(userData);
						if (stopwatch.isRunning()) {
							stopwatch.stop();
						}
						if (omniUsers != null && omniUsers.size() > 0) {
							smsUserCount++; // 정보 있으면 count++

							incsNo = user.getIncsNo();
							smsVo.setFscrId(response.getUserId());
							if (StringUtils.hasText(incsNo) && !"0".equals(incsNo)) {
								WebUtil.setSession(OmniConstants.INCS_NO_SESSION, incsNo);
							}

							String mobile = StringUtil.mergeMobile(user);
							smsVo.setPhoneNo(mobile);
							searchUsers.setMobile(mobile);

							for (UserData userdata : omniUsers) {
								SearchData searchUser = new SearchData();
								searchUser.setLoginId(OmniUtil.maskUserId(userdata.getLoginId()));
								searchUser.setPassId(userdata.getLoginId());
								searchUser.setXpassId(SecurityUtil.setXyzValue(userdata.getLoginId()));
								searchUser.setMobile(StringUtil.mergeMobile(user));
								searchUser.setIncsNo(userdata.getIncsNo());
								searchUser.setName(user.getCustNm());
								searchOmniUsers.add(searchUser);
							}
							searchUsers.setSearchOmniUsers(searchOmniUsers);

						} else {
							// 휴대폰 로그인 시 통합검색만 조회되는 경우 처리
							if (smsAuthVo.isPhoneLogin()) {
								smsUserCount++; // 정보 있으면 count++
								if (StringUtils.isEmpty(incsNo)) {
									incsNo = user.getIncsNo();
									WebUtil.setSession(OmniConstants.INCS_NO_SESSION, incsNo);
								}
								if (StringUtils.isEmpty(smsVo.getFscrId())) {
									smsVo.setFscrId(user.getChcsNo());
								}
								if (StringUtils.isEmpty(smsVo.getPhoneNo())) {
									smsVo.setPhoneNo(StringUtil.mergeMobile(user));
									searchUsers.setMobile(StringUtil.mergeMobile(user));
								}
								SearchData searchUser = new SearchData();
								searchUser.setLoginId(OmniUtil.maskUserId(user.getChcsNo()));
								searchUser.setPassId(user.getChcsNo());
								searchUser.setXpassId(SecurityUtil.setXyzValue(user.getChcsNo()));
								searchUser.setMobile(StringUtil.mergeMobile(user));
								searchUser.setIncsNo(user.getIncsNo());
								searchUser.setName(user.getCustNm());
								searchOmniUsers.add(searchUser);
								searchUsers.setSearchOmniUsers(searchOmniUsers);
							}
						}
					}
				}
			}

			// 휴대폰 로그인인 경우 통합고객이 있다는 전제이므로 경로 조회할 필요 없음.
			if (!smsAuthVo.isPhoneLogin()) {
				if (!stopwatch.isRunning()) {
					stopwatch.start("search channel user");
				}
				// 경로 회원 조회
				String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
				SearchChCustRequest searchCustRequest = new SearchChCustRequest();
				searchCustRequest.setChCd(chCd);
				searchCustRequest.setName(smsAuthVo.getUserName());
				searchCustRequest.setPhone(smsAuthVo.getUserPhone());

				// 경로 API 로 경로 정보 받아오고
				SearchChCustResponse chCustRespnose = this.customerApiService.getOnlyChannelUser(chCd, searchCustRequest);
				if (stopwatch.isRunning()) {
					stopwatch.stop();
				}
				if (chCustRespnose.getResultCode().equals(ResultCode.SUCCESS.getCode())) {
					for (UserInfo user : chCustRespnose.getUserInfo()) {

						// 사용자 정보 있으면 경로테이블에서 전환가입 하지 않은 사용자 조회
						UserData userData = new UserData();
						userData.setChCd(chCd);
						userData.setChLoginId(user.getWebId());
						//userData.setIncsNo(Integer.toString(user.getIncsNo()));
						List<UserData> chUsers = this.mgmtService.getChannelUserLoginIdByChId(userData);
						if (chUsers != null && chUsers.size() > 0) {
							smsUserCount++; // 정보 있으면 count++

							// 통합고객조회로 셋팅 안됐으면 경로 정보로 셋팅
							if (StringUtils.isEmpty(incsNo)) {
								incsNo = Integer.toString(user.getIncsNo());
								WebUtil.setSession(OmniConstants.INCS_NO_SESSION, incsNo);
							}
							if (StringUtils.isEmpty(smsVo.getFscrId())) {
								smsVo.setFscrId(user.getWebId());
							}
							if (StringUtils.isEmpty(smsVo.getPhoneNo())) {
								smsVo.setPhoneNo(user.getPhone());
								searchUsers.setMobile(user.getPhone());
							}

							final Channel channel = this.commonService.getChannel(chCd);
							for (UserData userdata : chUsers) {
								SearchData searchUser = new SearchData();
								searchUser.setLoginId(OmniUtil.maskUserId(userdata.getLoginId()));
								searchUser.setPassId(userdata.getLoginId());
								searchUser.setXpassId(SecurityUtil.setXyzValue(userdata.getLoginId()));
								searchUser.setMobile(user.getPhone());
								searchUser.setIncsNo(userdata.getIncsNo());
								searchUser.setName(user.getName());
								searchUser.setChCdName(channel.getChCdNm());
								searchChannelUsers.add(searchUser);
							}

							searchUsers.setSearchChannelUsers(searchChannelUsers);
						}
					}
				}
			}
			
			if (smsAuthVo.isPhoneLogin()) { // 휴대폰 로그인인 경우 통합고객번호, 아이디가 중복인 경우 안내팝업
				log.debug("Send SMS, Is Phone Login : {}, incsNo : {}, incsNo is Empty : {}", smsAuthVo.isPhoneLogin(), incsNo, StringUtils.isEmpty(incsNo));
				int omnicount = searchUsers.getSearchOmniUsers() == null ? 0 : searchUsers.getSearchOmniUsers().size();
				int chcount = searchUsers.getSearchChannelUsers() == null ? 0 : searchUsers.getSearchChannelUsers().size();
				if (omnicount + chcount > 1) {
					smsVo.setStatus(OmniConstants.SMS_AUTH_PHONELOGIN_DUPLICATE);
					smsVo.setRsltCd(ResultCode.USER_NOT_FOUND.getCode());
					smsVo.setRsltMsg(ResultCode.USER_NOT_FOUND.message());
					return smsVo;
				}
				
				if(StringUtils.isEmpty(incsNo) || "".equals(incsNo)) {
					log.debug("User incsNo Not found");
					SmsVo vo = new SmsVo();
					vo.setStatus(OmniConstants.SMS_AUTH_NOTFOUND_INCSNO);
					vo.setRsltCd(ResultCode.USER_NOT_FOUND.getCode());
					vo.setRsltMsg(ResultCode.USER_NOT_FOUND.message());
					return vo;
				}
			}

			// 조회된 사용자가 없으면 오류 처리 -> ID 찾기 시에만
			if (smsAuthVo.isSearchId() == true && smsUserCount == 0) {
				smsVo.setStatus(OmniConstants.SMS_AUTH_NOTFOUND_USER);
				smsVo.setRsltCd(ResultCode.USER_NOT_FOUND.getCode());
				smsVo.setRsltMsg(ResultCode.USER_NOT_FOUND.message());
				return smsVo;
			}

			log.debug("▶▶▶▶▶▶ [SMS Cert] Users = {}", StringUtil.printJson(searchUsers));
			WebUtil.setSession(OmniConstants.SEARCH_CERT_USER_LIST, searchUsers);
		}

		smsAuthVo.setIncsNo(incsNo);
		// 재전송할때 인증번호 넘겨서 이전인증발송 번호 삭제
		if (StringUtils.hasText(smsAuthVo.getSmsSeq())) {
			smsVo.setSmsAthtSendNo(Integer.parseInt(smsAuthVo.getSmsSeq()));
		}
		smsVo.setIncsNo(Integer.parseInt(incsNo)); // 통합고객번호

		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		String smsAuthNo = UuidUtil.getOtp(); // SMS 인증번호값 생성(distance 가 있으면 검증 구간 체크함)
		if (this.config.isSmsTestSend(profile)) {	// 테스트를 위해 SMS 번호는 고정
			smsAuthNo = "654321";
		}
		smsVo.setSmsAthtNoVl(smsAuthNo);

		final String sendmsg = this.messageSource.getMessage("sms.auth.message", new String[] { smsAuthNo }, LocaleUtil.getLocale());
		smsVo.setSendMessage(sendmsg);
		smsVo.setName(smsAuthVo.getUserName());
		if (!stopwatch.isRunning()) {
			stopwatch.start("send sms to eai");
		}
		SmsVo rtnSmsVo = this.smsService.sendSms(smsVo);
		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		
		log.info("\n" + stopwatch.prettyPrint());
		
		return rtnSmsVo;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 휴대폰 인증
	 * 휴대폰 인증완료 후 회원정보체크하여 분기 
	 * author   : takkies
	 * date     : 2020. 8. 13. 오후 4:31:32
	 * </pre>
	 * 
	 * @param smsVo
	 * @return
	 */
	public SmsVo authSms(SmsVo smsVo) {
		return this.smsService.authSms(smsVo);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 21. 오후 12:12:51
	 * </pre>
	 * 
	 * @param smsAuthVo
	 * @return
	 */
	public boolean invalidSms(SmsAuthVo smsAuthVo) {
		SmsVo smsVo = new SmsVo();
		smsVo.setStatus(-1);
		smsVo.setSmsAthtSendNo(Integer.parseInt(smsAuthVo.getSmsSeq()));
		return this.smsService.invalidSms(smsVo);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 21. 오후 12:12:41
	 * </pre>
	 * 
	 * @param phoneNo
	 * @param message
	 * @return
	 */
	public SmsResponse sendSmsEai(final String phoneNo, final String message) {
		return this.smsService.sendSmsEai(phoneNo, message);
	}

	public SmsVo sendSmsType(final String type, final SmsAuthVo smsAuthVo) {

		log.debug("▶▶▶▶▶ [send sms eai] sms auth info : {}", StringUtil.printJson(smsAuthVo));

		String sendmobile = smsAuthVo.getUserPhone();
		String sendmessage;
		String searchid = smsAuthVo.getUserName();
		log.debug("▶▶▶▶▶ [send sms eai] type : {} --> {}", type, searchid);
		// id 찾기에서 id 전송할 경우
		// 암호화된 값을 파라미터로 보냄(controller에서 복호화)
		if ("id".equals(type)) {
			if (StringUtils.hasText(searchid)) {
				sendmessage = this.messageSource.getMessage("sms.searchid.message", new String[] { searchid }, LocaleUtil.getLocale());
				if (StringUtils.hasText(sendmobile) && StringUtils.hasText(sendmessage)) {
					SmsResponse response = this.sendSmsEai(sendmobile, sendmessage);
					log.debug("▶▶▶▶▶ [send sms eai] response : {} --> {}", StringUtil.printJson(response));

					String rtnCode = response.getRESPONSE().getHEADER().getRTN_CODE();
					rtnCode = StringUtils.isEmpty(rtnCode) ? response.getRESPONSE().getHEADER().getRTN_TYPE() : rtnCode;
					if (OmniConstants.SEND_SMS_EAI_SUCCESS.equals(rtnCode)) {
						SmsVo vo = new SmsVo();
						vo.setStatus(1);
						return vo; // 발송 성공
					}
				} else {
					SmsVo vo = new SmsVo();
					vo.setStatus(-1);
					return vo; // 발송 INF 실패
				}
			}
		}
		SmsVo vo = new SmsVo();
		vo.setStatus(-1);
		return vo; // 발송 INF 실패
	}

	/**
	 * 
	 * <pre>
	 * comment  : 점유인증 CI 업데이트
	 * author   : takkies
	 * date     : 2020. 10. 21. 오전 9:03:25
	 * </pre>
	 * 
	 * @param customer
	 * @param certResult
	 * @return
	 */
	public boolean updateOccupationCi(final Customer customer, final CertResult certResult) {
		final String custCiApi = customer.getCiNo();
		final String custNameApi = customer.getCustNm();
		final String custBirthApi = customer.getAthtDtbr();
		final String custPhoneApi = StringUtil.mergeMobile(customer);

		// 통합DB에 존재하는 회원으로 판단 전환 프로세스 진행
		// 통합DB의 CI 교체 점유인증 활동 CASE로 판단 (통합DB에 있는 CI가 점유인증 CI인 경우에만)

		if (OmniUtil.isOccupationCiCert(custCiApi)) { // == 로 끝나지 않으면 점유인증임.

			log.debug("▶▶▶▶▶▶ [cert occupation ci] ci update case : {}", custCiApi);

			String mobiles[] = StringUtil.splitMobile(custPhoneApi);

			// 통합DB의 CI 교체
			UpdateCustCiVo updateCustCiVo = new UpdateCustCiVo();
			updateCustCiVo.setCustNm(custNameApi);
			updateCustCiVo.setAthtDtbr(custBirthApi);
			updateCustCiVo.setCellTidn(mobiles[0]);
			updateCustCiVo.setCellTexn(mobiles[1]);
			updateCustCiVo.setCellTlsn(mobiles[2]);
			updateCustCiVo.setChgChCd(customer.getChCd());
			updateCustCiVo.setCiNo(certResult.getCiNo());
			updateCustCiVo.setLschId("OCP");

			log.debug("▶▶▶▶▶▶ [cert occupation ci] ci update date vo : {}", StringUtil.printJson(updateCustCiVo));

//			ICITSVCOM000 : 정상
//			ICITSVBIZ102 : 고객명 미입력
//			ICITSVBIZ103 : 생년월일 미입력
//			ICITSVBIZ104 : 휴대폰 미입력
//			ICITSVBIZ153 : CI 번호를 확인 요망
//			ICITSVCOM003 : 필수항목 누락
//			ICITSVCOM001 : 통합고객이 존재하지 않습니다

			UpdateCustCiResponse ciResponse = this.customerApiService.updateCustCiNo(updateCustCiVo);

			log.debug("▶▶▶▶▶▶ [cert occupation ci] ci update response : {}", StringUtil.printJson(ciResponse));

			if ("ICITSVCOM000".equals(ciResponse.getRsltCd())) {
				return true;
			} else {
				return false;
			}
		} else {
			log.debug("▶▶▶▶▶▶ [cert occupation ci] ci update skip, normal ci : {}", custCiApi);
		}
		return true;

	}
}
