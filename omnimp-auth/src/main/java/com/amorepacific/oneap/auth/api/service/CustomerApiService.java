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
 * Date   	          : 2020. 8. 7..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.api.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.auth.api.vo.ivo.AbusingLockVo;
import com.amorepacific.oneap.auth.api.vo.ivo.AuthKeyVo;
import com.amorepacific.oneap.auth.api.vo.ivo.CicuehTncListResponse;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustChannelRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustVO;
import com.amorepacific.oneap.auth.api.vo.ivo.CustMarketingRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.DormancyCustVo;
import com.amorepacific.oneap.auth.api.vo.ivo.ReleaseDormancyVo;
import com.amorepacific.oneap.auth.api.vo.ivo.UpdateCustCiVo;
import com.amorepacific.oneap.auth.api.vo.ivo.UpdateCustCino;
import com.amorepacific.oneap.auth.api.vo.ovo.CreateCustChannelJoinResponse;
import com.amorepacific.oneap.auth.api.vo.ovo.CreateCustResponse;
import com.amorepacific.oneap.auth.api.vo.ovo.CustResponse;
import com.amorepacific.oneap.auth.api.vo.ovo.CustYnResponse;
import com.amorepacific.oneap.auth.api.vo.ovo.DormancyCustResponse;
import com.amorepacific.oneap.auth.api.vo.ovo.ReleaseDormancyResponse;
import com.amorepacific.oneap.auth.api.vo.ovo.UpdateAbusingCiResponse;
import com.amorepacific.oneap.auth.api.vo.ovo.UpdateCustCiResponse;
import com.amorepacific.oneap.auth.common.service.ApiService;
import com.amorepacific.oneap.auth.common.service.AsyncApiService;
import com.amorepacific.oneap.auth.join.service.JoinService;
import com.amorepacific.oneap.auth.join.vo.JoinData;
import com.amorepacific.oneap.auth.join.vo.JoinRequest;
import com.amorepacific.oneap.auth.login.vo.Web2AppVo;
import com.amorepacific.oneap.auth.membership.vo.MembershipUserInfo;
import com.amorepacific.oneap.auth.membership.vo.naver.Contents;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipResponse;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGAccessTokenRequest;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGAccessTokenResponse;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGAccessTokenResponse.TokenData;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGCommonResponse;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGMbrLinkResponse;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGUserInfoRequest;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGUserInfoResponse;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGUserInfoResponse.Support;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGUserInfoResponse.UserInfoData;
import com.amorepacific.oneap.auth.offline.vo.OfflineLoginRequest;
import com.amorepacific.oneap.auth.offline.vo.OfflineLoginResponse;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.exception.OmniException;
import com.amorepacific.oneap.common.sec.SecurityEncoder;
import com.amorepacific.oneap.common.sec.SecurityFactory;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Marketing;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.ApiCustomer;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.BpEditUserRequest;
import com.amorepacific.oneap.common.vo.api.BpEditUserResponse;
import com.amorepacific.oneap.common.vo.api.BpUserData;
import com.amorepacific.oneap.common.vo.api.ChTermsVo;
import com.amorepacific.oneap.common.vo.api.ChangePasswordData;
import com.amorepacific.oneap.common.vo.api.CicuedCuChQcVo;
import com.amorepacific.oneap.common.vo.api.CipAthtVo;
import com.amorepacific.oneap.common.vo.api.CreateChCustRequest;
import com.amorepacific.oneap.common.vo.api.CreateUserData;
import com.amorepacific.oneap.common.vo.api.CuoptiResponse;
import com.amorepacific.oneap.common.vo.api.CuoptiVo;
import com.amorepacific.oneap.common.vo.api.CustChListResponse;
import com.amorepacific.oneap.common.vo.api.CustChResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.CustbyChCsNoResponse;
import com.amorepacific.oneap.common.vo.api.CustbyChCsNoVo;
import com.amorepacific.oneap.common.vo.api.DeleteCustChRequest;
import com.amorepacific.oneap.common.vo.api.InitPasswordData;
import com.amorepacific.oneap.common.vo.api.OfflineChannelApiRequest;
import com.amorepacific.oneap.common.vo.api.OfflineChannelApiRequest.CicuedCuChCsTcVo;
import com.amorepacific.oneap.common.vo.api.OfflineChannelApiRequest.CicuemCuOptiCsTcVo;
import com.amorepacific.oneap.common.vo.api.SearchChCustRequest;
import com.amorepacific.oneap.common.vo.api.SearchChCustResponse;
import com.amorepacific.oneap.common.vo.api.SnsVo;
import com.amorepacific.oneap.common.vo.api.UpdateCustResponse;
import com.amorepacific.oneap.common.vo.api.UpdateCustVo;
import com.amorepacific.oneap.common.vo.api.UserInfo;
import com.amorepacific.oneap.common.vo.api.VeriPwdPlcyVo;
import com.amorepacific.oneap.common.vo.dormancy.DormancyResponse;
import com.amorepacific.oneap.common.vo.kakao.KakaoNoticeRequest;
import com.amorepacific.oneap.common.vo.kakao.KakaoNoticeResponse;
import com.amorepacific.oneap.common.vo.sns.ApplePublicKeysResponse;
import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.amorepacific.oneap.common.vo.sns.SnsPlusFriendsResponse;
import com.amorepacific.oneap.common.vo.sns.SnsProfileResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTermsResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenVo;
import com.amorepacific.oneap.common.vo.sns.SnsUnlinkVo;
import com.amorepacific.oneap.common.vo.sns.SnsUrl;
import com.amorepacific.oneap.common.vo.user.CicuedCuChArrayTcVo;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.CustomerData;
import com.amorepacific.oneap.common.vo.user.PointResponse;
import com.amorepacific.oneap.common.vo.user.PointVo;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.WithdrawResponse;
import com.amorepacific.oneap.common.vo.user.WithdrawVo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.api.service 
 *    |_ CustomerApiService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 7.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class CustomerApiService {

	@Autowired
	private ApiEndPoint apiEndpoint;

	// REST API 호출 서비스
	@Autowired
	private ApiService apiService;
	
	// ASYNC REST API 호출 서비스
	@Autowired
	private AsyncApiService asyncApiService;

	@Autowired
	private JoinService joinService;

	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 14. 오후 7:27:59
	 * </pre>
	 * 
	 * @param veriPwdPlcyVo
	 * @return
	 */
	public ApiBaseResponse verifyPasswordPolicy(final VeriPwdPlcyVo veriPwdPlcyVo) {

		log.debug("▶▶▶▶▶▶ {}", StringUtil.printJson(veriPwdPlcyVo));

		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());

		ResponseEntity<ApiBaseResponse> response = this.apiService.post(this.apiEndpoint.getVerifyPasswordPolicy(), headers, veriPwdPlcyVo, ApiBaseResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			ApiBaseResponse ar = new ApiBaseResponse();
			ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			return ar;
		}

		return response.getBody();

	}

	/**
	 * 
	 * <pre>
	 * comment  : 고객목록조회(이름, 전화번호, 고객생일, CI)
	 * 
	 * /cip/cit/custmgnt/custmgnt/svc/custinfrcommgnt/getcicumentcuinfralllist/v1.00
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 7. 오후 7:02:27
	 * </pre>
	 * 
	 * @param custInfoVo
	 * @return
	 */
	public CustInfoResponse getCustList(final CustInfoVo custInfoVo) {
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Map<String, Object> params = new HashMap<>();

		if (StringUtils.hasText(custInfoVo.getCiNo())) {
			params.put("ciNo", custInfoVo.getCiNo());
		} else {
			if (StringUtils.isEmpty(custInfoVo.getCustMobile())) {
				if (StringUtils.hasText(custInfoVo.getIncsNo())) {
					params.put("incsNo", custInfoVo.getIncsNo());
					
					CipAthtVo cipAthtVo = CipAthtVo.builder().build();
					params.put("cipAthtVo", cipAthtVo);

					Gson gson = new GsonBuilder().disableHtmlEscaping().create();
					String json = gson.toJson(params);
					
					log.debug("▶▶▶▶▶▶ getCicuemcuInfrByIncsNo: {}", StringUtil.printJson(params));
					
					ResponseEntity<Customer> response = this.apiService.post(this.apiEndpoint.getGetcicuemcuinfrbyincsno(), headers, json, Customer.class);
					
					if (response.getStatusCode() != HttpStatus.OK) {
						// AP B2C 표준 로그 설정
						LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CICUEMCU_INFR_BY_INCS_NO_SERVER_ERROR, null, null, null,
								LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
						LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
						LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
						log.error("api.getCicuemcuInfrByIncsNo.Exception = {}", response.getBody());
						LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
						
						CustInfoResponse cr = new CustInfoResponse();
						cr.setRsltCd("ICITSVCOM999");
						return cr;
					}
					
					Customer customer = response.getBody();

					if ("ICITSVCOM001".equals(customer.getRsltCd())) { // ICITSVCOM001 : 통합고객이 존재하지 않습니다
						CustInfoResponse cr = new CustInfoResponse();
						cr.setRsltCd("ICITSVCOM999");
						return cr;
					}
					
					if ("Y".equalsIgnoreCase(customer.getDrccCd())) { // 휴면고객
						String chCd = custInfoVo.getChCd();
						if (StringUtils.isEmpty(chCd)) {
							chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
						}
//						String name = joinService.releaseDormancyCustomerName(customer.getIncsNo(), chCd);
						DormancyResponse dormancyResponse = joinService.releaseDormancyCustomerName1(customer.getIncsNo(), chCd);
						if (dormancyResponse != null) {
							String rtnCode = dormancyResponse.getRESPONSE().getHEADER().getRTN_CODE();
							rtnCode = StringUtils.isEmpty(rtnCode) ? dormancyResponse.getRESPONSE().getHEADER().getRTN_TYPE() : rtnCode;
							
							if (rtnCode.equals(OmniConstants.SEND_SMS_EAI_SUCCESS)) {
								/*
								 * String name = dormancyResponse.getRESPONSE().getHEADER().getCSTMNM(); if (StringUtils.hasText(name)) { customer.setCustNm(name); }
								 */
								
								// 휴면 복구 성공 시 API 다시 조회
								response = this.apiService.post(this.apiEndpoint.getGetcicuemcuinfrbyincsno(), headers, json, Customer.class);
								
								if (response.getStatusCode() != HttpStatus.OK) {
									// AP B2C 표준 로그 설정
									LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CICUEMCU_INFR_BY_INCS_NO_SERVER_ERROR, null, null, null,
											LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
									LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
									LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
									log.error("api.getCicuemcuInfrByIncsNo.Exception = {}", response.getBody());
									LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
									
									CustInfoResponse cr = new CustInfoResponse();
									cr.setRsltCd("ICITSVCOM999");
									return cr;
								}
								
								customer = response.getBody();

								if ("ICITSVCOM001".equals(customer.getRsltCd())) { // ICITSVCOM001 : 통합고객이 존재하지 않습니다
									CustInfoResponse cr = new CustInfoResponse();
									cr.setRsltCd("ICITSVCOM999");
									return cr;
								}
								
							} else if (rtnCode.equals("E") // 휴면 복구 시 에러 메시지 [RA-01403: no data found] : 휴면 DB에 사용자 없는 경우 확인 신규 가입 진행
									&& dormancyResponse.getRESPONSE().getHEADER().getRTN_MSG().equals("[ORA-01403: no data found]")) {
								CustInfoResponse cr = new CustInfoResponse();
								cr.setRsltCd("ICITSVCOM001"); // 신규 가입 진행
								return cr;
							} else {
								log.debug("▶▶▶▶▶▶ [getCustList] 휴면 해제 EAI 오류");
								CustInfoResponse cr = new CustInfoResponse();
								cr.setRsltCd("ICITSVCOM999");
								return cr;
							}
						} else {
							log.debug("▶▶▶▶▶▶ [getCustList] 휴면 해제 EAI 오류");
							CustInfoResponse cr = new CustInfoResponse();
							cr.setRsltCd("ICITSVCOM999");
							return cr;
						}
					}
					
					CustInfoResponse cr = new CustInfoResponse();
					Customer[] customers = new Customer[1];
					cr.setRsltCd(customer.getRsltCd());
					cr.setRsltMsg(customer.getRsltMsg());
					cr.setUserId("");
					cr.setJoincnt("1");
					
					if("ICITSVCOM000".equals(customer.getRsltCd())) {
						customer.setRsltCd("");
					}
					customers[0] = customer;
					
					cr.setCicuemCuInfTcVo(customers);
					cr.setCicuemCuInfQcVo(customers);
					
					return cr;
				}
			} else {
				try {
					String mobile[] = StringUtil.splitMobile(custInfoVo.getCustMobile());
					if (StringUtils.hasText(custInfoVo.getAthtDtbr())) {
						params.put("athtDtbr", custInfoVo.getAthtDtbr().length() == 8 ? custInfoVo.getAthtDtbr().substring(2) : custInfoVo.getAthtDtbr()); // 고객생일
						params.put("custNm", custInfoVo.getCustName());
						params.put("cellTlsn", mobile[2]); // 끝 4자리
					} else {
						params.put("custNm", custInfoVo.getCustName());
						params.put("cellTidn", mobile[0]);
						params.put("cellTexn", mobile[1]);
						params.put("cellTlsn", mobile[2]);
					}
				} catch (Exception e) {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CUST_LIST_SERVER_ERROR, null, null, null,
							LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_WARN_CD);
					LogInfo.setLogInfoErr(OmniStdLogConstants.GET_CUST_LIST_MOBILE_PHONE_NUMBER_ERROR);
					LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb");
					log.warn("휴대폰 번호 처리 중 오류가 발생하였습니다.");
					LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
					throw new OmniException("휴대폰 번호 처리 중 오류가 발생하였습니다.");
				}
			}
		}
		
		CipAthtVo cipAthtVo = CipAthtVo.builder().build();
		params.put("cipAthtVo", cipAthtVo);

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);

		ResponseEntity<CustInfoResponse> response = this.apiService.post(this.apiEndpoint.getGetcicuemcuinfrlist(), headers, json, CustInfoResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CUST_LIST_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.GET_CUST_LIST_SERVER_ERROR);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.getCustList.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			CustInfoResponse cr = new CustInfoResponse();
			cr.setRsltCd("ICITSVCOM999");
			return cr;
		}
		CustInfoResponse custInfoResponse = response.getBody();

		Customer customers[] = custInfoResponse.getCicuemCuInfQcVo();
		if (customers != null && customers.length > 0) {
			Customer customer = customers[0]; // 중요) 첫번째 데이터가 최신임.
			if ("Y".equalsIgnoreCase(customer.getDrccCd())) { // 휴면고객
				String chCd = custInfoVo.getChCd();
				if (StringUtils.isEmpty(chCd)) {
					chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
				}
//				String name = joinService.releaseDormancyCustomerName(customer.getIncsNo(), chCd);
				DormancyResponse dormancyResponse = joinService.releaseDormancyCustomerName1(customer.getIncsNo(), chCd);
				if (dormancyResponse != null) {
					String rtnCode = dormancyResponse.getRESPONSE().getHEADER().getRTN_CODE();
					rtnCode = StringUtils.isEmpty(rtnCode) ? dormancyResponse.getRESPONSE().getHEADER().getRTN_TYPE() : rtnCode;
					
					if (rtnCode.equals(OmniConstants.SEND_SMS_EAI_SUCCESS)) {
						String name = dormancyResponse.getRESPONSE().getHEADER().getCSTMNM();
						if (StringUtils.hasText(name)) {
							customer.setCustNm(name);
						}
						
						for (Customer cust : customers) {
							cust.setCustNm(name);
						}
						
						response = this.apiService.post(this.apiEndpoint.getGetcicuemcuinfrlist(), headers, json, CustInfoResponse.class);
						
						if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
							// AP B2C 표준 로그 설정
							LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CUST_LIST_SERVER_ERROR, null, null, null,
									LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
							LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
							LogInfo.setLogInfoErr(OmniStdLogConstants.GET_CUST_LIST_SERVER_ERROR);
							LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
							log.error("api.getCustList.Exception = {}", response.getBody());
							LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
							
							CustInfoResponse cr = new CustInfoResponse();
							cr.setRsltCd("ICITSVCOM999");
							return cr;
						}
						custInfoResponse = response.getBody();
						
					} else if (rtnCode.equals("E") // 휴면 복구 시 에러 메시지 [RA-01403: no data found] : 휴면 DB에 사용자 없는 경우 확인 신규 가입 진행
							&& dormancyResponse.getRESPONSE().getHEADER().getRTN_MSG().equals("[ORA-01403: no data found]")) {
						CustInfoResponse cr = new CustInfoResponse();
						cr.setRsltCd("ICITSVCOM001"); // 신규 가입 진행
						return cr;
					} else {
						log.debug("▶▶▶▶▶▶ [getCustList] 휴면 해제 EAI 오류");
						CustInfoResponse cr = new CustInfoResponse();
						cr.setRsltCd("ICITSVCOM999");
						return cr;
					}
				} else {
					log.debug("▶▶▶▶▶▶ [getCustList] 휴면 해제 EAI 오류");
					CustInfoResponse cr = new CustInfoResponse();
					cr.setRsltCd("ICITSVCOM999");
					return cr;
				}
			}
			
			custInfoResponse.setUserId("");
			custInfoResponse.setJoincnt(Integer.toString(custInfoResponse.getCicuemCuInfQcVo().length));
			custInfoResponse.setCicuemCuInfTcVo(custInfoResponse.getCicuemCuInfQcVo());
		}
		
		return custInfoResponse;
	}
	
	// 제휴사 멤버십 조회 용 (휴면 복구 제외)
	public CustInfoResponse getCustListByMembership(final CustInfoVo custInfoVo) {
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Map<String, Object> params = new HashMap<>();

		if (StringUtils.hasText(custInfoVo.getCiNo())) {
			params.put("ciNo", custInfoVo.getCiNo());
		} else {
			try {
				String mobile[] = StringUtil.splitMobile(custInfoVo.getCustMobile());
				if (StringUtils.hasText(custInfoVo.getAthtDtbr())) {
					params.put("athtDtbr", custInfoVo.getAthtDtbr().length() == 8 ? custInfoVo.getAthtDtbr().substring(2) : custInfoVo.getAthtDtbr()); // 고객생일
					params.put("custNm", custInfoVo.getCustName());
					params.put("cellTlsn", mobile[2]); // 끝 4자리
				} else {
					params.put("custNm", custInfoVo.getCustName());
					params.put("cellTidn", mobile[0]);
					params.put("cellTexn", mobile[1]);
					params.put("cellTlsn", mobile[2]);
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CUST_LIST_SERVER_ERROR, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_WARN_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.GET_CUST_LIST_MOBILE_PHONE_NUMBER_ERROR);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb");
				log.warn("휴대폰 번호 처리 중 오류가 발생하였습니다.");
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				throw new OmniException("휴대폰 번호 처리 중 오류가 발생하였습니다.");
			}
		}
		
		CipAthtVo cipAthtVo = CipAthtVo.builder().build();
		params.put("cipAthtVo", cipAthtVo);

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);

		ResponseEntity<CustInfoResponse> response = this.apiService.post(this.apiEndpoint.getGetcicuemcuinfrlist(), headers, json, CustInfoResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CUST_LIST_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.GET_CUST_LIST_SERVER_ERROR);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.getCustList.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			CustInfoResponse cr = new CustInfoResponse();
			cr.setRsltCd("ICITSVCOM999");
			return cr;
		}
		CustInfoResponse custInfoResponse = response.getBody();
		
		Customer customers[] = custInfoResponse.getCicuemCuInfQcVo();
		custInfoResponse.setCicuemCuInfTcVo(customers);
		
		return custInfoResponse;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 고객상세조회
	 *            
	 * /cip/cit/custmgnt/custmgnt/svc/custinfrcommgnt/getcicuemcuinfrbyincsno/v1.00
	 *            
	 * author   : takkies
	 * date     : 2020. 8. 11. 오후 7:06:44
	 * </pre>
	 * 
	 * @param custInfoVo
	 * @return
	 */
	public Customer getCicuemcuInfrByIncsNo(final CustInfoVo custInfoVo) {
		
		// log.debug("▶▶▶▶▶▶ InfrByIncsNo : {}", StringUtil.printJson(custInfoVo));

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.hasText(custInfoVo.getIncsNo())) {
			params.put("incsNo", custInfoVo.getIncsNo());
		}
		if (StringUtils.hasText(custInfoVo.getCiNo())) {
			// params.put("ciNo", custInfoVo.getCiNo());
			CustInfoResponse custInfoResponse = this.getCustList(custInfoVo);
			if(custInfoResponse == null || custInfoResponse.getCicuemCuInfTcVo() == null || custInfoResponse.getCicuemCuInfTcVo().length == 0) {
				Customer customer = new Customer();
				customer.setCiNo(""); // 없는 사용자이면
				customer.setRsltCd("ICITSVCOM999");
				return customer;
			}
			return custInfoResponse.getCicuemCuInfTcVo()[0];
		}
		
		// incsNo 값이 null 일 경우 0으로 변환
		if (StringUtils.isEmpty(custInfoVo.getIncsNo())) {
			params.put("incsNo", "0");
		}
		
		// 경로코드를 입력을 안할 경우 '000'채널 정보를 조회.
		// 탈퇴여부가 'N'인 경우만 조회됨.
		// As Is인 경우 '000' 경로가 가입시 바로 탈퇴로 만들기 때문에
		// As Is 데이터는 경로코드를 입력을 안할 경우 조회가 안될 수 있음.
		
		CipAthtVo cipAthtVo = CipAthtVo.builder().build();
		params.put("cipAthtVo", cipAthtVo);
		
		// if (StringUtils.hasText(custInfoVo.getChCd())) {
		//   params.put("chCd", custInfoVo.getChCd());
		// }
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);

		log.debug("▶▶▶▶▶▶ getCicuemcuInfrByIncsNo: {}", StringUtil.printJson(params));

		ResponseEntity<Customer> response = this.apiService.post(this.apiEndpoint.getGetcicuemcuinfrbyincsno(), headers, json, Customer.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CICUEMCU_INFR_BY_INCS_NO_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.getCicuemcuInfrByIncsNo.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			Customer customer = new Customer();
			customer.setRsltCd("ICITSVCOM999");
			return customer;
		}

		Customer customer = response.getBody();

		if ("ICITSVCOM001".equals(customer.getRsltCd())) { // ICITSVCOM001 : 통합고객이 존재하지 않습니다
			customer.setCiNo(""); // 없는 사용자이면
			customer.setRsltCd("ICITSVCOM999");
			return customer;
		}
		String chCd = custInfoVo.getChCd();
		if (StringUtils.isEmpty(chCd)) {
			chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		}
		if ("Y".equalsIgnoreCase(customer.getDrccCd())) { // 휴면고객
			String name = joinService.releaseDormancyCustomerName(customer.getIncsNo(), chCd);
			if (StringUtils.hasText(name)) {
				// 휴면 복구 성공 시 API 다시 조회
				response = this.apiService.post(this.apiEndpoint.getGetcicuemcuinfrbyincsno(), headers, json, Customer.class);
				
				customer = response.getBody();

				if ("ICITSVCOM001".equals(customer.getRsltCd())) { // ICITSVCOM001 : 통합고객이 존재하지 않습니다
					customer.setCiNo(""); // 없는 사용자이면
					customer.setRsltCd("ICITSVCOM999");
					return customer;
				}
				
				// customer.setCustNm(name); // 휴면고객인 경우 처리된 사용자명을 전달해야함.
			}
			else { // 휴면 해제 EAI 오류
				customer.setRsltCd("ICITSVCOM999");
			}
		}

		return customer;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 29. 오후 8:31:14
	 * </pre>
	 * 
	 * @param chCd
	 * @param incsNo
	 * @param ciNo
	 * @return
	 */
	public String getApiCustomerName(final String chCd, final String incsNo, final String ciNo, final String defaultCustomerName) {
		ApiCustomer apiCustomer = getApiCustomer(chCd, incsNo, ciNo);
		if (apiCustomer != null) {
			return apiCustomer.getCustNm();
		}
		return defaultCustomerName;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 28. 오후 4:23:53
	 * </pre>
	 * 
	 * @param chCd
	 * @param incsNo
	 * @param ciNo
	 * @return
	 */
	public ApiCustomer getApiCustomer(final String chCd, final String incsNo, final String ciNo) {

		if (StringUtils.isEmpty(incsNo) || "0".equals(incsNo)) {
			log.debug("▶▶▶▶▶▶ incs no is empty!!!");
			return null;
		}

		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setChCd(chCd);
		custInfoVo.setIncsNo(incsNo);
		if (StringUtils.hasText(ciNo)) {
			custInfoVo.setCiNo(ciNo);
		}
		Customer customer = getCicuemcuInfrByIncsNo(custInfoVo);
		if (customer != null && "ICITSVCOM000".equals(customer.getRsltCd())) {
			ApiCustomer apiCustomer = new ApiCustomer();
			apiCustomer.setCiNo(customer.getCiNo());
			apiCustomer.setCustNm(customer.getCustNm());
			apiCustomer.setGender(customer.getSxclCd());
			apiCustomer.setBirthDt(customer.getAthtDtbr());
			apiCustomer.setCardNo(customer.getIncsCardNoEc());
			apiCustomer.setJoinDt(customer.getMbrJoinDt());
			apiCustomer.setMobile(StringUtil.mergeMobile(customer));
			apiCustomer.setNational(customer.getFrclCd());
			apiCustomer.setWebId(customer.getChcsNo());
			apiCustomer.setWtDt(customer.getCustWtDttm());
			apiCustomer.setWtYn(customer.getCustWtYn());
			return apiCustomer;
		}
		return null;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 고객상세조회(약관동의,수신동의 배열)
	 * 
	 * /cip/cit/custmgnt/custmgnt/svc/custinfrcommgnt/getcicuemcuinfrarrayincsno/v1.00
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 12. 오후 3:18:46
	 * </pre>
	 * 
	 * @param custInfoVo
	 * @return
	 */
	public CustResponse getCicuemcuInfrArrayIncsNo(final CustInfoVo custInfoVo) {
		
		// log.debug("▶▶▶▶▶▶ {}", StringUtil.printJson(custInfoVo));

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		// headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Map<String, String> params = new HashMap<String, String>();
		params.put("incsNo", custInfoVo.getIncsNo());

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);

		log.debug("▶▶▶▶▶▶ getCicuemcuInfrArrayIncsNo: {}", StringUtil.printJson(params));

		ResponseEntity<CustResponse> response = this.apiService.post(this.apiEndpoint.getGetcicuemcuinfrarrayincsno(), headers, json, CustResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CICUEMCU_INFR_ARRAY_INCS_NO_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.getCicuemcuInfrArrayIncsNo.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			CustResponse cr = new CustResponse();
			cr.setRsltCd("ICITSVCOM999");
			return cr;
		}
		
		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 고객여부조회(통합고객번호, CI번호)
	 * 
	 * /cip/cit/custmgnt/custmgnt/svc/custinfrcommgnt/getcicuemcuyn/v1.00
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 7. 오후 5:59:10
	 * </pre>
	 * 
	 * @param custInfoVo
	 * @return
	 */
	public CustYnResponse getCustYn(final CustInfoVo custInfoVo) {
		
		// log.debug("▶▶▶▶▶▶ {}", StringUtil.printJson(custInfoVo));

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Map<String, String> params = new HashMap<String, String>();
		if (StringUtils.hasText(custInfoVo.getIncsNo())) {
			params.put("incsNo", custInfoVo.getIncsNo());
		}
		if (StringUtils.hasText(custInfoVo.getCiNo())) {
			params.put("ciNo", custInfoVo.getCiNo());
		}
		params.put("chCd", custInfoVo.getChCd());

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);

		log.debug("▶▶▶▶▶▶ getCustYn: {}", StringUtil.printJson(params));

		ResponseEntity<CustYnResponse> response = this.apiService.post(this.apiEndpoint.getGetcicuemcuyn(), headers, json, CustYnResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CUST_YN_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.getCustYn.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			CustYnResponse cr = new CustYnResponse();
			cr.setRsltCd("ICITSVCOM999");
			return cr;
		}
		
		return response.getBody();
	}

	/**
	 * <pre>
	 * comment  : 통합 고객 등록 
	 * author   : mcjan
	 * date     : 2020. 8. 11. 오후 1:29:36
	 * </pre>
	 * 
	 * @param createCustVo
	 * @return
	 */
	public CreateCustResponse createCust(final CreateCustVO createCustVo) {
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(createCustVo);

		log.debug("▶▶▶▶▶▶ createCust: {}", StringUtil.printJson(createCustVo));

		ResponseEntity<CreateCustResponse> response = this.apiService.post(this.apiEndpoint.getCreatecicuemcuinfrjoin(), headers, json, CreateCustResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.CREATE_CUST_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.createCust.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			CreateCustResponse cr = new CreateCustResponse();
			cr.setRsltCd("ICITSVCOM999");
			return cr;
		}
		
		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 8. 오후 5:54:20
	 * </pre>
	 * 
	 * @param updateCustVo
	 * @return
	 */
	public UpdateCustResponse updateCust(final UpdateCustVo updateCustVo) {
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(updateCustVo);

		log.debug("▶▶▶▶▶▶ updateCust: {}", StringUtil.printJson(updateCustVo));
		ResponseEntity<UpdateCustResponse> response = this.apiService.post(this.apiEndpoint.getUpdatecicuemcuinfrfull(), headers, json, UpdateCustResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.UPDATE_CUST_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.updateCust.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			UpdateCustResponse ur = new UpdateCustResponse();
			ur.setRsltCd("ICITSVCOM999");
			return ur;
		}
		
		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 통합고객 CI 업데이트
	 * author   : takkies
	 * date     : 2020. 9. 4. 오후 2:25:13
	 * </pre>
	 * 
	 * @param updateCustCiVo
	 * @return
	 */
	public UpdateCustCiResponse updateCustCiNo(UpdateCustCiVo updateCustCiVo) {
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(updateCustCiVo);

		log.debug("▶▶▶▶▶▶ updateCustCiNo: {}", StringUtil.printJson(updateCustCiVo));

		ResponseEntity<UpdateCustCiResponse> response = this.apiService.post(this.apiEndpoint.getUpdatecustcino(), headers, json, UpdateCustCiResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.UPDATE_CUST_CI_NO_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.updateCustCiNo.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			UpdateCustCiResponse ur = new UpdateCustCiResponse();
			ur.setRsltCd("ICITSVCOM999");
			return ur;
		}
		
		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 서비스약관동의/철회 다건 저장
	 * author   : takkies
	 * date     : 2020. 9. 14. 오후 4:00:58
	 * </pre>
	 * 
	 * @param custTncaRequest
	 * @return
	 */
	public ApiResponse savecicuedcutnca(final CustTncaRequest custTncaRequest) {
		
		log.debug("▶▶▶▶▶▶ [save term] request : {}", StringUtil.printJson(custTncaRequest));

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(custTncaRequest);

		ResponseEntity<ApiResponse> response = this.apiService.post(this.apiEndpoint.getSavecicuedcutnca(), headers, json, ApiResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.SAVE_CICUED_CUTNCA_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.savecicuedcutnca.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			ApiResponse ur = new ApiResponse();
			ur.setRsltCd("ICITSVCOM999");
			return ur;
		}
		
		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 14. 오후 4:11:11
	 * </pre>
	 * 
	 * @param custMarketingRequest
	 * @return
	 */
	public ApiResponse savecicuemcuoptilist(final CustMarketingRequest custMarketingRequest) {
		
		log.debug("▶▶▶▶▶▶ [save marketing] request : {}", StringUtil.printJson(custMarketingRequest));

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(custMarketingRequest);

		ResponseEntity<ApiResponse> response = this.apiService.post(this.apiEndpoint.getSavecicuemcuoptilist(), headers, json, ApiResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.SAVE_CICUEM_CUOPTI_LIST_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.savecicuemcuoptilist.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			ApiResponse ur = new ApiResponse();
			ur.setRsltCd("ICITSVCOM999");
			return ur;
		}
		
		return response.getBody();

	}

	/**
	 * <pre>
	 * comment  : 휴면 고객 조회 
	 * author   : mcjan
	 * date     : 2020. 8. 11. 오후 1:29:24
	 * </pre>
	 * 
	 * @param dormancyCustVo
	 * @return
	 */
	public DormancyCustResponse getDormancyCust(final DormancyCustVo dormancyCustVo) {
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(dormancyCustVo);
		log.debug("▶▶▶▶▶▶ getDormancyCust: {}", StringUtil.printJson(dormancyCustVo));

		ResponseEntity<DormancyCustResponse> response = this.apiService.post(this.apiEndpoint.getGetcustdrcsinq(), headers, json, DormancyCustResponse.class);
		return response.getBody();
	}

	/**
	 * <pre>
	 * comment  : 휴면 해제 신청 
	 * author   : mcjan
	 * date     : 2020. 8. 11. 오후 3:50:43
	 * </pre>
	 * 
	 * @param releaseDormancyVo
	 * @return
	 */
	public ReleaseDormancyResponse releaseDormancyCust(final ReleaseDormancyVo releaseDormancyVo) {
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(releaseDormancyVo);
		log.debug("▶▶▶▶▶▶ releaseDormancyCust: {}", StringUtil.printJson(releaseDormancyVo));

		ResponseEntity<ReleaseDormancyResponse> response = this.apiService.post(this.apiEndpoint.getCreatecicueldrcsrstrq(), headers, json, ReleaseDormancyResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			ReleaseDormancyResponse rr = new ReleaseDormancyResponse();
			rr.setRsltCd("ICITSVCOM999");
			return rr;
		}

		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 사용자 휴면 해제하기
	 * author   : takkies
	 * date     : 2020. 9. 8. 오후 2:38:02
	 * </pre>
	 * 
	 * @param omniUser
	 * @return
	 */
	public boolean releaseDormancy(final String incsNo, final String chCd) {

		log.debug("▶▶▶▶▶▶ release dormancy : {} {}", incsNo, chCd);

		boolean success = true;

		success &= this.joinService.releaseDormancyCustomer(incsNo, chCd);

		return success;
	}

	/**
	 * <pre>
	 * comment  : 경로가입 
	 * author   : mcjan
	 * date     : 2020. 8. 11. 오후 3:51:57
	 * </pre>
	 * 
	 * @param createCustChannelJoinVo
	 * @return
	 */
	public CreateCustChannelJoinResponse createCustChannelMember(final CreateCustChannelRequest chCustRequest) {
		
		// 브랜드 사이트일 경우 고객통합 플랫폼에 경로 가입 처리 하지 않음
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		if(config.isBrandSite(chCustRequest.getCicuedCuChTcVo().get(0).getChCd(), profile)) {
			CreateCustChannelJoinResponse cr = new CreateCustChannelJoinResponse();
			cr.setRsltCd("ICITSVCOM000");
			return cr;
		}

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(chCustRequest);
		log.debug("▶▶▶▶▶▶ create custChannel member: {}", StringUtil.printJson(chCustRequest));
		ResponseEntity<CreateCustChannelJoinResponse> response = this.apiService.post(this.apiEndpoint.getCreatecustchnjoin(), headers, json, CreateCustChannelJoinResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.CREATE_CUST_CHANNEL_MEMBER_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.createCustChannelMember.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			CreateCustChannelJoinResponse cr = new CreateCustChannelJoinResponse();
			cr.setRsltCd("ICITSVCOM999");
			return cr;
		}
		
		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 비밀번호 변경 API 호출
	 * author   : takkies
	 * date     : 2020. 8. 26. 오후 12:44:39
	 * </pre>
	 * 
	 * @param chgPwdVo
	 * @return
	 */
	public ApiBaseResponse changePassword(final ChangePasswordData chgPwdVo) {
		log.debug("▶▶▶▶▶▶ change password : {}", StringUtil.printJson(chgPwdVo));
		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());

		ResponseEntity<ApiBaseResponse> response = this.apiService.post(this.apiEndpoint.getChangePassword(), headers, chgPwdVo, ApiBaseResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			ApiBaseResponse ar = new ApiBaseResponse();
			ar.setResultCode("ICITSVCOM999");
			return ar;
		}

		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 비밀번호 초기화 API 호출 
	 * author   : hkdang
	 * date     : 2020. 9. 25. 오후 1:47:14
	 * </pre>
	 * 
	 * @param initPwdVo
	 * @return
	 */
	public ApiBaseResponse initPassword(final InitPasswordData initPwdVo) {

		log.debug("▶▶▶▶▶▶ init password : {}", StringUtil.printJson(initPwdVo));
		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());

		ResponseEntity<ApiBaseResponse> response = this.apiService.post(this.apiEndpoint.getInitPassword(), headers, initPwdVo, ApiBaseResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			ApiBaseResponse ar = new ApiBaseResponse();
			ar.setResultCode("ICITSVCOM999");
			return ar;
		}

		return response.getBody();
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 비밀번호 초기화 API 호출 (기존 패스워드로 업데이트)
	 * author   : hjw0228
	 * date     : 2022. 8. 3. 오후 1:47:14
	 * </pre>
	 * 
	 * @param initPwdVo
	 * @return
	 */
	public ApiBaseResponse initPasswordCurrentPassword(final InitPasswordData initPwdVo) {

		log.debug("▶▶▶▶▶▶ init password current password : {}", StringUtil.printJson(initPwdVo));
		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());

		ResponseEntity<ApiBaseResponse> response = this.apiService.post(this.apiEndpoint.getInitPasswordCurrentPassword(), headers, initPwdVo, ApiBaseResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			ApiBaseResponse ar = new ApiBaseResponse();
			ar.setResultCode("ICITSVCOM999");
			return ar;
		}

		return response.getBody();
	}	

	/**
	 * 
	 * <pre>
	 * comment  : OMNI 통합회원 등록
	 * author   : takkies
	 * date     : 2020. 8. 28. 오후 2:39:30
	 * </pre>
	 * 
	 * @param joinUserData
	 * @return
	 */
	public ApiBaseResponse createUser(final CreateUserData joinUserData) {

		log.debug("▶▶▶▶▶▶ create user : {}", StringUtil.printJson(joinUserData));
		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());

		ResponseEntity<ApiBaseResponse> apiResponse = this.apiService.post(this.apiEndpoint.getCreateUser(), headers, joinUserData, ApiBaseResponse.class);
		return apiResponse.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 17. 오후 1:24:32
	 * </pre>
	 * 
	 * @param withdrawVo
	 * @return
	 */
	public ApiBaseResponse withdrawIntegratedUser(final WithdrawVo withdrawVo) {
		ApiBaseResponse apiResponse = new ApiBaseResponse();
		log.debug("▶▶▶▶▶▶ withdraw user : {}", StringUtil.printJson(withdrawVo));
		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());

		ResponseEntity<WithdrawResponse> response = this.apiService.post(this.apiEndpoint.getCreatecicuelcuwt(), headers, withdrawVo, WithdrawResponse.class);

		WithdrawResponse withdrawResponse = response.getBody();
		if (withdrawResponse != null && "ICITSVCOM000".equals(withdrawResponse.getRsltCd())) {

			/*
			 * final String loginId = withdrawVo.getLoginId(); if (StringUtils.hasText(loginId)) { apiResponse = this.disableWso2User(loginId);
			 * 
			 * if (ResultCode.SUCCESS.getCode().equals(apiResponse.getResultCode())) { apiResponse.SetResponseInfo(ResultCode.SUCCESS); } else {
			 * apiResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR); }
			 * 
			 * }
			 */

			apiResponse.SetResponseInfo(ResultCode.SUCCESS);
		} else {
			apiResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
		}
		return apiResponse;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 17. 오후 1:42:35
	 * </pre>
	 * 
	 * @param pointVo
	 * @return
	 */
	public PointResponse getPointSearch(final PointVo pointVo) {
		log.debug("▶▶▶▶▶▶ point user : {}", StringUtil.printJson(pointVo));
		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		ResponseEntity<PointResponse> response = this.apiService.post(this.apiEndpoint.getGetptinq(), headers, pointVo, PointResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			PointResponse ar = new PointResponse();
			ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
			return ar;
		}
		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 뷰티포인트 회원가입
	 * 
	 * <code>
	 * {        
	 *  "incsNo":"200000277",
	 *  "cstmid":"jjc070",
	 *  "pswd":"1q2w3e4r5t"
	 * }
	 * </code>
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 28. 오후 3:11:13
	 * </pre>
	 * 
	 * @param userData
	 * @return
	 */
	public ApiResponse createBpUser(final BpUserData userData, final JoinRequest joinRequest) {
		
		// STG 환경에서는 뷰티포인트 API 호출 시 성공 처리 (고객통합에는 030 채널을 포함한 경로 가입 처리)
		// → Join On Sunset 에 따라 고객통합플랫폼에는 모든 환경에서 고객통합플랫폼에 030을 포함한 경로 가입 처리 2022-12-29 
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		try {
			CreateCustChannelRequest chCustRequest = JoinData.buildIntegrated030ChannelCustomerData(joinRequest);
			log.debug("▷▷▷▷▷▷ integrated 030 channel customer api data : {}", StringUtil.printJson(chCustRequest));
			
			final HttpHeaders headers = new HttpHeaders();
			final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			headers.setContentType(mediaType);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			String json = gson.toJson(chCustRequest);
			ResponseEntity<CreateCustChannelJoinResponse> response = this.apiService.post(this.apiEndpoint.getCreatecustchnjoin(), headers, json, CreateCustChannelJoinResponse.class);
			
			if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				ApiResponse ar = new ApiResponse();
				ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
				ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				return ar;
			} 
		} catch (Exception e) {
			log.error(e.getMessage());
			ApiResponse ar = new ApiResponse();
			ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
			ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			return ar;
		}
			
		// if("stg".equals(profile) || "dev".equals(profile) || "local".equals(profile)) {
			ApiResponse ar = new ApiResponse();
			ar.setRsltCd(ResultCode.BP_SUCCESS.getCode());
			ar.setResultCode(ResultCode.BP_SUCCESS.getCode());
			return ar;
		// }
		
		/*
		 * log.debug("▶▶▶▶▶▶ [create bp] user : {}", StringUtil.printJson(userData)); final HttpHeaders headers = new HttpHeaders(); MediaType
		 * mediaType = new MediaType("application", "json", StandardCharsets.UTF_8); headers.setContentType(mediaType);
		 * headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		 * 
		 * ResponseEntity<ApiResponse> response = this.apiService.post(this.apiEndpoint.getCreateBpUser(), headers, userData, ApiResponse.class);
		 * 
		 * if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) { // AP B2C 표준 로그 설정 LogInfo.setLogInfo(OmniStdLogConstants.OMNI,
		 * OmniStdLogConstants.BP_API, OmniStdLogConstants.CREATE_BEAUTY_POINT_USER_SERVER_ERROR, null, null, null,
		 * LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null); LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
		 * LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
		 * log.error("api.createBpUser.Exception = {}", response.getBody()); LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		 * 
		 * ApiResponse ar = new ApiResponse(); ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode()); ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		 * return ar; }
		 * 
		 * return response.getBody();
		 */
	}

	/**
	 * 
	 * <pre>
	 * comment  : 뷰티포인트 온라인 회원가입 망취소
	 * apiKey : 호출날짜(yyyy-MM-dd  + chcd) 를 SHA-512로 암호화
	 * 가입중 호출이 되어야 되는 내용으로 온라인 회원가입 호출후 5분이내에만 유효
	 * 
	 * <code>
	 * {        
	 *  "incsNo":"200000277",
	 *  "cstmid":"jjc070",
	 *  "chcd":"051",
	 *  "apiKey":"7953a06d565f068c69affa1c8d221c773573094b676cfb1ed16907c42a94f21fa4d541492fe4d8f92a4b43e547f0dd7a54c928e60df8b52ed4da69f388e113e2"
	 * }
	 * </code>
	 * 
	 * author   : takkies
	 * date     : 2020. 9. 9. 오전 8:49:55
	 * </pre>
	 * 
	 * @param userData
	 * @return
	 */
	public ApiResponse cancelBpUser(final BpUserData userData) {
		
		// STG 환경에서는 뷰티포인트 API 호출 시 성공 처리
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		//뷰티포인트 API url 삭제로 성공 처리-20230117
		// if("stg".equals(profile) || "dev".equals(profile) || "local".equals(profile)) {
			ApiResponse ar = new ApiResponse();
			ar.setRsltCd(ResultCode.SUCCESS.getCode());
			ar.setResultCode(ResultCode.SUCCESS.getCode());
			return ar;
		// }
		
		/*
		 * log.debug("▶▶▶▶▶▶ [cancel bp] user : {}", StringUtil.printJson(userData)); final HttpHeaders headers = new HttpHeaders(); MediaType
		 * mediaType = new MediaType("application", "json", StandardCharsets.UTF_8); headers.setContentType(mediaType);
		 * headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		 * 
		 * if (StringUtils.isEmpty(userData.getApiKey())) { final String key =
		 * DateUtil.getCurrentDateString("yyyy-MM-dd").concat(userData.getChcd());
		 * 
		 * SecurityFactory securityFactory = SecurityFactory.getInstance(); SecurityEncoder encoder =
		 * securityFactory.getEncoder(SecurityFactory.SHA); try { String sha512apiKey = encoder.encode(key); userData.setApiKey(sha512apiKey); }
		 * catch (Exception e) { // NO PMD } }
		 * 
		 * ResponseEntity<ApiResponse> response = this.apiService.post(this.apiEndpoint.getCancelBpUser(), headers, userData, ApiResponse.class);
		 * 
		 * if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) { // AP B2C 표준 로그 설정 LogInfo.setLogInfo(OmniStdLogConstants.OMNI,
		 * OmniStdLogConstants.BP_API, OmniStdLogConstants.CANCEL_BEAUTY_POINT_USER_SERVER_ERROR, null, null, null,
		 * LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null); LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
		 * LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
		 * log.error("api.cancelBpUser.Exception = {}", response.getBody()); LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		 * 
		 * ApiResponse ar = new ApiResponse(); ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode()); ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		 * return ar; }
		 * 
		 * return response.getBody();
		 */
	}

	/**
	 * 
	 * <pre>
	 * comment  : 뷰티포인트 온라인 ID 유효성 체크
	 * 
	 * <code>
	 * {        
	 *  "incsNo":"200000277",
	 *  "cstmid":"jjc070"
	 * }
	 * </code>
	 * 
	 * author   : takkies
	 * date     : 2020. 9. 9. 오전 8:49:51
	 * </pre>
	 * 
	 * @param userData
	 * @return
	 */
	public ApiResponse checkBpOnlineId(final BpUserData userData) {
		
		// STG 환경에서는 뷰티포인트 API 호출 시 성공 처리(뷰티포인트 API url 삭제로 성공 처리-20230117)
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		// if("stg".equals(profile) || "dev".equals(profile) || "local".equals(profile)) {
			ApiResponse ar = new ApiResponse();
			ar.setRsltCd(ResultCode.BP_SUCCESS.getCode());
			ar.setResultCode(ResultCode.BP_SUCCESS.getCode());
			return ar;
		// }
		
		/*
		 * log.debug("▶▶▶▶▶▶ [check bp] online user : {}", StringUtil.printJson(userData)); final HttpHeaders headers = new HttpHeaders(); MediaType
		 * mediaType = new MediaType("application", "json", StandardCharsets.UTF_8); headers.setContentType(mediaType);
		 * headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		 * 
		 * ResponseEntity<ApiResponse> response = this.apiService.post(this.apiEndpoint.getCheckBpOnlineId(), headers, userData, ApiResponse.class);
		 * 
		 * if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) { // AP B2C 표준 로그 설정 LogInfo.setLogInfo(OmniStdLogConstants.OMNI,
		 * OmniStdLogConstants.BP_API, OmniStdLogConstants.CHECK_BEAUTY_POINT_ONLINE_ID_SERVER_ERROR, null, null, null,
		 * LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null); LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
		 * LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
		 * log.error("api.checkBpOnlineId.Exception = {}", response.getBody()); LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		 * 
		 * ApiResponse ar = new ApiResponse(); ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode()); ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		 * return ar; }
		 * 
		 * return response.getBody();
		 */
	}

	/**
	 * 
	 * <pre>
	 * comment  : 뷰티포인트 아이디 중복 체크
	 * 
	 * <code>
	 * {
	 *  "id":"jjc070"
	 * }
	 * </code>
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 28. 오후 3:58:02
	 * </pre>
	 * 
	 * @param userData
	 * @return
	 */
	public ApiResponse checkBpUserId(final BpUserData userData) {
		
		// STG 환경에서는 뷰티포인트 API 호출 시 성공 처리
		// 뷰티포인트 API url 삭제로 성공 처리-20230117
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		// if("stg".equals(profile) || "dev".equals(profile) || "local".equals(profile)) {
			ApiResponse ar = new ApiResponse();
			ar.setRsltCd(ResultCode.SUCCESS.getCode());
			ar.setResultCode(ResultCode.SUCCESS.getCode());
			return ar;
		// }
		
		/*
		 * log.debug("▶▶▶▶▶▶ [check bp] user : {}", StringUtil.printJson(userData)); final HttpHeaders headers = new HttpHeaders(); MediaType
		 * mediaType = new MediaType("application", "json", StandardCharsets.UTF_8); headers.setContentType(mediaType); //
		 * headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		 * 
		 * ResponseEntity<ApiResponse> response = this.apiService.post(this.apiEndpoint.getCheckBpUserId(), headers, userData, ApiResponse.class);
		 * 
		 * if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) { // AP B2C 표준 로그 설정 LogInfo.setLogInfo(OmniStdLogConstants.OMNI,
		 * OmniStdLogConstants.BP_API, OmniStdLogConstants.CHECK_BEAUTY_POINT_USER_ID_SERVER_ERROR, null, null, null,
		 * LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null); LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
		 * LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
		 * log.error("api.checkBpUserId.Exception = {}", response.getBody()); LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		 * 
		 * ApiResponse ar = new ApiResponse(); ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode()); ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		 * return ar; }
		 * 
		 * return response.getBody();
		 */

	}

	public ApiResponse editBpUser(final BpEditUserRequest bpEditUserRequest) {
		
		// 뷰티포인트 API url 삭제로 성공 처리-20230117
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		// if("dev".equals(profile) || "local".equals(profile)) {
			ApiResponse ar = new ApiResponse();
			ar.setRsltCd(ResultCode.BP_SUCCESS.getCode());
			ar.setResultCode(ResultCode.BP_SUCCESS.getCode());
			return ar;
		// }

		/*
		 * log.debug("▶▶▶▶▶▶ [edit bp] user request : {}", StringUtil.printJson(bpEditUserRequest)); final HttpHeaders headers = new HttpHeaders();
		 * MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8); headers.setContentType(mediaType);
		 * headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); headers.add("Referer", "ecp.amorepacific.com");
		 * 
		 * ApiResponse ar = new ApiResponse(); ResponseEntity<String> response = this.apiService.post(this.apiEndpoint.getCheckBpEditUser(),
		 * headers, bpEditUserRequest, String.class);
		 * 
		 * if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) { ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
		 * ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode()); return ar; }
		 * 
		 * Gson gson = new GsonBuilder().disableHtmlEscaping().create(); BpEditUserResponse bpResponse = gson.fromJson(response.getBody(),
		 * BpEditUserResponse.class);
		 * 
		 * log.debug("▶▶▶▶▶▶ [edit bp] user response : {}", StringUtil.printJson(bpResponse));
		 * 
		 * if (!"SUCCESS".equals(bpResponse.getRESULT())) { ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
		 * ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode()); ar.setMessage(bpResponse.getMESSAGE()); ar.setMsgCode(bpResponse.getCODE()); return
		 * ar; } ar.SetResponseInfo(ResultCode.SUCCESS); return ar;
		 */
	}

	/**
	 * 
	 * <pre>
	 * comment  : 경로회원 등록
	 * author   : takkies
	 * date     : 2020. 9. 21. 오후 1:40:04
	 * </pre>
	 * 
	 * @param createChCustRequest
	 * @return
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public ApiBaseResponse createChannelUser(final String chCd, final CreateChCustRequest createChCustRequest, final JoinRequest joinRequest) {
		
		ApiBaseResponse response = new ApiBaseResponse();

		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		final String apiKey = this.apiEndpoint.getChannelApiKey(chCd, profile);
		final String apiUrl = this.apiEndpoint.getChannelApiUrl(chCd, profile);
		final String apiCheck = this.apiEndpoint.getChannelApiCheck(chCd, profile);
		log.debug("▶▶▶▶▶▶ [createChannelUser] apiCheck : {}, chCd : {}", apiCheck, chCd);
		
		if (StringUtils.isEmpty(apiUrl)) { // apiUrl = null 이면 성공처리 2021-05-21 hjw0228
			response.SetResponseInfo(ResultCode.SUCCESS);
			response.setResultCode(ResultCode.SUCCESS.getCode());
			return response;
		}
		
		if (!StringUtils.isEmpty(apiCheck) && apiCheck.equals("true")) {
			// 고객통합 API 검색 후 - 고객이 있으면 : return, 없으면 : 기존과 동일하게 create
			log.debug("▶▶▶▶▶▶ [createChannelUser] apiCheck : {}, chCd : {}", apiCheck, chCd);

			List<CicuedCuChArrayTcVo> cicuedCuChArrayTcVo = (List<CicuedCuChArrayTcVo>) WebUtil.getSession(OmniConstants.EXIST_CUSTOMER);
			if (cicuedCuChArrayTcVo != null && cicuedCuChArrayTcVo.size() > 0) {
				log.debug("▶▶▶▶▶▶ [createChannelUser] 22 apiCheck : {}, chCd : {}", apiCheck, chCd);
				for (CicuedCuChArrayTcVo CicuedCuChTcVo : cicuedCuChArrayTcVo) {
					if (CicuedCuChTcVo.getChCd().equals(chCd)) {
						log.debug("▶▶▶▶▶▶ [createChannelUser] {} 가입 고객 ", chCd);
						response.SetResponseInfo(ResultCode.SUCCESS);
						response.setResultCode(ResultCode.SUCCESS.getCode());
						return response; 
					}
				}
			}
//			CustInfoVo custInfoVo = new CustInfoVo();
//			custInfoVo.setChCd(chCd);
//			custInfoVo.setCiNo(createChCustRequest.getUser().getCi());
//			custInfoVo.setIncsNo(createChCustRequest.getUser().getIncsNo());
//			CustYnResponse custynResponse = getCustYn(custInfoVo);
//			log.debug("▶▶▶▶▶▶ [createChannelUser] 고객 가입 여부 조회 : {} {} RsltCd : {}", chCd, custInfoVo.getCiNo(), custynResponse.getRsltCd());
//			
//			if ("ICITSVCOM000".equals(custynResponse.getRsltCd())) {
//				// 경로에 있음.
//				log.debug("▶▶▶▶▶▶ [createChannelUser] 가입 고객 ");
//				response.SetResponseInfo(ResultCode.SUCCESS);
//				return response; 
//			}
		}
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);

		headers.setContentType(mediaType);
		
		if (StringUtils.hasText(apiKey)) {
			headers.add(OmniConstants.XAPIKEY, apiKey);
		}
		
		// 경로시스템 사용자 아이디 중 HTML Code로 변환된 값 원복
		String chLoginId = createChCustRequest.getUser().getChLoginId();
		String unEscapedChLoginId = StringEscapeUtils.unescapeHtml4(chLoginId);
		log.debug("chLoginId : {}, unEscapedChLoginId : {}", chLoginId, unEscapedChLoginId);
		createChCustRequest.getUser().setChLoginId(unEscapedChLoginId);
		
		// ECP API 인 경우 temrs 가 nul 일 경우 null Array 가 아닌 빈 terms object 생성하여 호출
		final boolean isEcpApi = this.apiEndpoint.isEcpApi(chCd, profile);
		if(isEcpApi && (createChCustRequest.getTerms() == null || createChCustRequest.getTerms().size() == 0)) {
			ChTermsVo chTermsVo = new ChTermsVo();
			chTermsVo.setIncsNo("");
			chTermsVo.setTcatCd("");
			chTermsVo.setTncaDttm("");
			chTermsVo.setTncAgrYn("");
			chTermsVo.setTncvNo("");
			List<ChTermsVo> terms = new ArrayList<ChTermsVo>();
			terms.add(chTermsVo);
			createChCustRequest.setTerms(terms);
			log.debug("createChCustRequest >>>>> {}", StringUtil.printJson(createChCustRequest));
		}
		// 오설록 Mall, 오설록 티하우스, 백화점 오설록에서 회원 가입 시 chCd는 가입 시도한 chCd로 수정
		String joinChCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		if(OmniConstants.OSULLOC_CHCD.equals(joinChCd) 
				|| OmniConstants.OSULLOC_OFFLINE_CHCD.equals(joinChCd) 
				|| OmniConstants.OSULLOC_DEPARTMENT_CHCD.equals(joinChCd)) {
			
			createChCustRequest.setChCd(joinChCd);
		}
		// 오설록 Mall에서 회원 가입 시 joinPrtnId, joinEmpId 값은 고정 값
		if(OmniConstants.OSULLOC_CHCD.equals(joinChCd)) {
			createChCustRequest.getUser().setJoinPrtnId(config.getJoinPrtnCode(joinChCd));
			createChCustRequest.getUser().setJoinEmpId(config.getJoinEmpCode(joinChCd));
		}		
		//20230425 오설록 백화점/백화점/설화수FSS api 변경으로 분기처리 request 추가
		ResponseEntity<ApiBaseResponse> apiResponse = null;
		OfflineChannelApiRequest offlineChannelRequest = new OfflineChannelApiRequest();
		offlineChannelRequest.setIncsNo(Integer.parseInt(joinRequest.getIncsno()));
		offlineChannelRequest.setCustNm(joinRequest.getUnm());
		offlineChannelRequest.setJoinChCd(createChCustRequest.getChCd());
		offlineChannelRequest.setJndvCd(joinRequest.getDeviceType());
//		offlineChannelRequest.setAtclCd(""); 
		offlineChannelRequest.setAthtDtbr(joinRequest.getBirth());
		offlineChannelRequest.setFrclCd(joinRequest.getNational());
		offlineChannelRequest.setSxclCd(joinRequest.getGender());
		if (StringUtils.hasText(joinRequest.getPhone())) {
			String phones[] = StringUtil.splitMobile(joinRequest.getPhone());
			if (phones != null && phones.length == 3) {
				offlineChannelRequest.setCellTidn(phones[0]); // 휴대폰식별전화번호
				offlineChannelRequest.setCellTexn(phones[1]); // 휴대폰국전화번호
				offlineChannelRequest.setCellTlsn(phones[2]); // 휴대폰끝전화번호
			}
		}
		offlineChannelRequest.setCiNo(joinRequest.getCi());
		offlineChannelRequest.setFscrId("OCP");
		offlineChannelRequest.setLschId("OCP");
//		offlineChannelRequest.setJoinPrtnId(joinRequest.getJoinPrtnId());
//		offlineChannelRequest.setJoinEmpId(joinRequest.getJoinEmpId());
		offlineChannelRequest.setJoinPrtnId(createChCustRequest.getUser().getJoinPrtnId());
		offlineChannelRequest.setJoinEmpId(createChCustRequest.getUser().getJoinEmpId());
		
		CicuedCuChCsTcVo CicuedCuChCsTcVo = new CicuedCuChCsTcVo();
		if(chCd.equals(OmniConstants.OSULLOC_OFFLINE_CHCD) || chCd.equals(OmniConstants.OSULLOC_CHCD) || chCd.equals(OmniConstants.OSULLOC_DEPARTMENT_CHCD)) {
			//오설록몰 또는 오설록 오프라인에서 가입 시, 008 api는 008로 호출
			CicuedCuChCsTcVo.setChCd(OmniConstants.OSULLOC_DEPARTMENT_CHCD);
		}else {
			CicuedCuChCsTcVo.setChCd(createChCustRequest.getChCd());
		}
		// Mandatory
		CicuedCuChCsTcVo.setChcsNo(joinRequest.getIncsno());
		CicuedCuChCsTcVo.setFscrId("OCP");
		CicuedCuChCsTcVo.setLschId("OCP");
		// As Is Legacy는 입력하면 오류 발생, 경로(030)
		// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 userPwdEc 가 웹 비밀번호가 됨.
		// 비밀번호 넣을 경우 채널코드 관계없이 "패스워드 자리수 오류" 발생
		if (StringUtils.hasText(joinRequest.getLoginpassword())) {
			CicuedCuChCsTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword()));
		} else {
			CicuedCuChCsTcVo.setUserPwdEc("");
		}
		// Optional
		if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
			CicuedCuChCsTcVo.setPrtnNm(joinRequest.getJoinPrtnNm());
		} else {
			CicuedCuChCsTcVo.setPrtnNm(config.getJoinPrtnName(joinRequest.getChcd()));
		}
		offlineChannelRequest.setCicuedCuChCsTcVo(CicuedCuChCsTcVo);
		
		CicuemCuOptiCsTcVo cicuemCuOptiTcVo = new CicuemCuOptiCsTcVo();
		// Mandatory
		if(chCd.equals(OmniConstants.OSULLOC_OFFLINE_CHCD) || chCd.equals(OmniConstants.OSULLOC_CHCD) || chCd.equals(OmniConstants.OSULLOC_DEPARTMENT_CHCD)) {
			cicuemCuOptiTcVo.setChCd(OmniConstants.OSULLOC_DEPARTMENT_CHCD);
		}else {
			cicuemCuOptiTcVo.setChCd(createChCustRequest.getChCd());
		}
		// Optional
		// isMarketingSyncBpEnable 면 000 채널 수신동의 여부와 동기화
		List<Marketing> joinMarketings = joinRequest.getMarketings();

		if(config.isMarketingSyncBpEnable(joinRequest.getChcd(), profile)) {
			// join-on 가입하는 API에 수신동의 항목 추가
			if (joinMarketings != null && !joinMarketings.isEmpty()) {
				for (Marketing joinMarketing : joinMarketings) {
					if ("000".equals(joinMarketing.getChCd())) {
						cicuemCuOptiTcVo.setSmsOptiYn(joinMarketing.getSmsAgree()); // SMS수신동의여부
					}
				}
			}
			cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // SMS수신동의일자
		} else {
			cicuemCuOptiTcVo.setSmsOptiYn("N"); // SMS수신동의여부
			cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // SMS수신동의일자
		}
		cicuemCuOptiTcVo.setEmlOptiYn("N"); // 이메일수신동의여부
		cicuemCuOptiTcVo.setEmlOptiDt(DateUtil.getCurrentDate());
		cicuemCuOptiTcVo.setDmOptiYn("N"); // DM수신동의여부
		cicuemCuOptiTcVo.setDmOptiDt(DateUtil.getCurrentDate());
		cicuemCuOptiTcVo.setTmOptiYn("N"); // TM수신동의여부
		cicuemCuOptiTcVo.setTmOptiDt(DateUtil.getCurrentDate());
		cicuemCuOptiTcVo.setIntlOptiYn("N"); // 알림톡수신동의여부
		cicuemCuOptiTcVo.setIntlOptiDt(DateUtil.getCurrentDate());
		cicuemCuOptiTcVo.setFscrId("OCP");
		cicuemCuOptiTcVo.setLschId("OCP");
		offlineChannelRequest.setCicuemCuOptiCsTcVo(cicuemCuOptiTcVo);
		if(config.isOfflineLiveApi(joinRequest.getChcd(), profile)) { //20230425 오설록 백화점/백화점/설화수 FSS api 변경 로직 추가
			log.debug("▶▶▶▶▶▶offlineChannelRequest user data : {}", StringUtil.printJson(offlineChannelRequest));
			apiResponse = this.apiService.post(apiUrl, headers, offlineChannelRequest, ApiBaseResponse.class);
		}else {
			apiResponse = this.apiService.post(apiUrl, headers, createChCustRequest, ApiBaseResponse.class);
		}

//		ResponseEntity<ApiBaseResponse> apiResponse = this.apiService.post(apiUrl, headers, createChCustRequest, ApiBaseResponse.class);
		log.debug("Body : {}", apiResponse.getBody());
		
		if(chCd.equals(OmniConstants.OSULLOC_CHCD)) { // 오설록 Mall 에서 가입 시 30일 제한 걸리는 경우 강제 리턴
			if(apiResponse.getBody() != null && !apiResponse.getBody().toString().equals("") && ResultCode.CHANNEL_WITHDRAW.getCode().equals(apiResponse.getBody().getResultCode())) {
				response.setResultCode(apiResponse.getBody().getResultCode());
				response.setMessage(apiResponse.getBody().getMessage());
				return response;
			}
		}
		
		if (chCd.equals(OmniConstants.OSULLOC_OFFLINE_CHCD) || chCd.equals(OmniConstants.OSULLOC_CHCD)) { // 오설록 Mall or 오설록 티하우스 채널 코드 039,012 : 008 채널 추가
			// 오설록 티하우스에서 회원 가입 시 joinPrtnId, joinEmpId 값은 고정 값
			if(OmniConstants.OSULLOC_OFFLINE_CHCD.equals(joinChCd)) {
				createChCustRequest.getUser().setJoinPrtnId("옴니"+joinChCd);
				createChCustRequest.getUser().setJoinEmpId("옴니"+joinChCd);
			}
			
			String osullocApiUrl = this.apiEndpoint.getChannelApiUrl(OmniConstants.OSULLOC_DEPARTMENT_CHCD, profile);
			if (!StringUtils.isEmpty(osullocApiUrl)) {
				apiResponse = this.apiService.post(osullocApiUrl, headers, offlineChannelRequest, ApiBaseResponse.class);
//				apiResponse = this.apiService.post(osullocApiUrl, headers, createChCustRequest, ApiBaseResponse.class);
				log.debug("Body : {}", apiResponse.getBody());
			}
		} else if (chCd.equals(OmniConstants.OSULLOC_DEPARTMENT_CHCD)) { // 백화점 오설록 채널코드 008 : 012 채널 추가
			// 백화점 오설록에서 회원 가입 시 joinPrtnId, joinEmpId 값은 고정 값
			if(OmniConstants.OSULLOC_DEPARTMENT_CHCD.equals(joinChCd)) {
				createChCustRequest.getUser().setJoinPrtnId("옴니"+joinChCd);
				createChCustRequest.getUser().setJoinEmpId("옴니"+joinChCd);
			}
			
			String osullocApiUrl = this.apiEndpoint.getChannelApiUrl(OmniConstants.OSULLOC_OFFLINE_CHCD, profile);
			if (!StringUtils.isEmpty(osullocApiUrl)) {
				apiResponse = this.apiService.post(osullocApiUrl, headers, createChCustRequest, ApiBaseResponse.class);	
				log.debug("Body : {}", apiResponse.getBody());
			}
		}

		if (apiResponse == null || apiResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) { // AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CHANNEL_API, OmniStdLogConstants.CHANNEL_API_COMMON, null, null, null, LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.CREATE_CHANNEL_USER_SERVER_ERROR);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정 log.error("api.createChannelUser.Exception = {}", apiResponse.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화

			ApiBaseResponse cr = new ApiBaseResponse();
			cr.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			cr.setMessage("오류가 발생하였습니다.");
			cr.setTrxUuid(response.getTrxUuid());
			return cr;
		}

		if (apiResponse.getBody() == null || apiResponse.getBody().toString().equals("")) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CHANNEL_API, OmniStdLogConstants.CHANNEL_API_COMMON, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.CREATE_CHANNEL_USER_RETURN_NULL);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.createChannelUser.Exception Return null");
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			ApiBaseResponse cr = new ApiBaseResponse();
			cr.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			cr.setMessage("오류가 발생하였습니다. empty!!!");
			cr.setTrxUuid(response.getTrxUuid());
			return cr;
		}
		
		ApiBaseResponse result = apiResponse.getBody();

		if (ResultCode.SUCCESS.getCode().equals(result.getResultCode())) {
			response.SetResponseInfo(ResultCode.SUCCESS);
		} else {
			response.setResultCode(result.getResultCode());
			response.setMessage(result.getMessage());
		}
		
		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 21. 오후 3:35:28
	 * </pre>
	 * 
	 * @param chCd
	 * @param searchCustRequest
	 * @return
	 */
	public UserInfo searchChannelUserInfo(final String chCd, final SearchChCustRequest searchCustRequest) {
		SearchChCustResponse response = searchChannelUser(chCd, searchCustRequest);
		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {
			UserInfo userinfo[] = response.getUserInfo();
			if (userinfo != null && userinfo.length > 0) {
				return userinfo[0];
			}
		}
		return null;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 21. 오후 3:28:36
	 * </pre>
	 * 
	 * @param chCd
	 * @param searchCustRequest
	 * @return
	 */
	public SearchChCustResponse searchChannelUser(final String chCd, final SearchChCustRequest searchCustRequest) {
		
		SearchChCustResponse response = new SearchChCustResponse();

		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		final String apiKey = this.apiEndpoint.getChannelApiKey(chCd, profile);
		final String apiUrl = this.apiEndpoint.getChannelSearchApi(chCd, profile);

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		if (StringUtils.hasText(apiKey)) {
			headers.add(OmniConstants.XAPIKEY, apiKey);
		}

		// 검색 api url이 있는 경우
		if (StringUtils.hasText(apiUrl)) {
			ResponseEntity<SearchChCustResponse> apiResponse = this.apiService.post(apiUrl, headers, searchCustRequest, SearchChCustResponse.class);
			if (apiResponse == null || apiResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CHANNEL_API, OmniStdLogConstants.CHANNEL_API_COMMON, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.GET_CHANNEL_USER_SERVER_ERROR);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.searchChannelUser.Exception = {}", apiResponse.getBody());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				SearchChCustResponse cr = new SearchChCustResponse();
				cr.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				cr.setMessage("오류가 발생하였습니다.");
				cr.setTrxUuid(response.getTrxUuid());
				return cr;
			}

			response = apiResponse.getBody();

		} else {
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(searchCustRequest.getIncsNo());
			if (StringUtils.hasText(searchCustRequest.getCi())) {
				custInfoVo.setCiNo(searchCustRequest.getCi());
			}
			Customer customer = getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null) {
				response.setResultCode(ResultCode.SUCCESS.getCode());
				UserInfo userInfo = new UserInfo();
				userInfo.setChCd(customer.getChCd());
				userInfo.setCi(customer.getCiNo());
				userInfo.setIncsNo(Integer.parseInt(customer.getIncsNo()));
				userInfo.setName(customer.getCustNm());
				userInfo.setPhone(StringUtil.mergeMobile(customer));
				userInfo.setWebId(customer.getChcsNo());
				List<UserInfo> list = new ArrayList<>();
				list.add(userInfo);
				// response.setUserinfo(list.toArray(new UserInfo[list.size()]));
				response.addUserInfo(list);
			} else {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CHANNEL_API, OmniStdLogConstants.CHANNEL_API_COMMON, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.GET_CHANNEL_USER_RETURN_NULL);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.searchChannelUser.Exception Return null");
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				SearchChCustResponse cr = new SearchChCustResponse();
				cr.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				cr.setMessage("오류가 발생하였습니다. empty!!!");
				cr.setTrxUuid(response.getTrxUuid());
				return cr;
			}
		}
		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 경로자체회원 ID 조회
	 * author   : takkies
	 * date     : 2020. 9. 7. 오후 4:31:44
	 * </pre>
	 * 
	 * @param userinfo
	 * @return
	 */
	public SearchChCustResponse getChannelUser(final String chCd, final SearchChCustRequest searchCustRequest) {
		
		SearchChCustResponse response = new SearchChCustResponse();

		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		log.debug("channel({}) data : {}", chCd, StringUtil.printJson(searchCustRequest));

		final String apiKey = this.apiEndpoint.getChannelApiKey(chCd, profile);
		final String apiUrl = this.apiEndpoint.getChannelSearchApi(chCd, profile);

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		if (StringUtils.hasText(apiKey)) {
			headers.add(OmniConstants.XAPIKEY, apiKey);
		}

		// 검색 api url이 있는 경우(없으면 통합고객검색)
		if (StringUtils.hasText(apiUrl)) {
			ResponseEntity<SearchChCustResponse> apiResponse = this.apiService.post(apiUrl, headers, searchCustRequest, SearchChCustResponse.class);
			if (apiResponse == null || apiResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CHANNEL_API, OmniStdLogConstants.CHANNEL_API_COMMON, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.GET_CHANNEL_USER_SERVER_ERROR);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.searchChannelUser.Exception = {}", apiResponse.getBody());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setIncsNo(searchCustRequest.getIncsNo());
				if (StringUtils.hasText(searchCustRequest.getCi())) {
					custInfoVo.setCiNo(searchCustRequest.getCi());
				}
				Customer customer = getCicuemcuInfrByIncsNo(custInfoVo);
				if (customer != null) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
					UserInfo userInfo = new UserInfo();
					userInfo.setChCd(customer.getChCd());
					userInfo.setCi(customer.getCiNo());
					userInfo.setIncsNo(Integer.parseInt(customer.getIncsNo()));
					userInfo.setName(customer.getCustNm());
					userInfo.setPhone(StringUtil.mergeMobile(customer));
					userInfo.setWebId(customer.getChcsNo());
					List<UserInfo> list = new ArrayList<>();
					list.add(userInfo);
					response.addUserInfo(list);
					return response;
				} else {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CHANNEL_API, OmniStdLogConstants.CHANNEL_API_COMMON, null, null, null,
							LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoErr(OmniStdLogConstants.GET_CHANNEL_USER_RETURN_NULL);
					LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					log.error("api.searchChannelUser.Exception Return null");
					LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
					
					SearchChCustResponse cr = new SearchChCustResponse();
					cr.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
					cr.setMessage("오류가 발생하였습니다.");
					cr.setTrxUuid(response.getTrxUuid());
					return cr;
				}
			}

			SearchChCustResponse result = apiResponse.getBody();

			if (apiResponse.getBody() == null || apiResponse.getBody().toString().equals("")) {
				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setIncsNo(searchCustRequest.getIncsNo());
				if (StringUtils.hasText(searchCustRequest.getCi())) {
					custInfoVo.setCiNo(searchCustRequest.getCi());
				}
				Customer customer = getCicuemcuInfrByIncsNo(custInfoVo);
				if (customer != null) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
					UserInfo userInfo = new UserInfo();
					userInfo.setChCd(customer.getChCd());
					userInfo.setCi(customer.getCiNo());
					userInfo.setIncsNo(Integer.parseInt(customer.getIncsNo()));
					userInfo.setName(customer.getCustNm());
					userInfo.setPhone(StringUtil.mergeMobile(customer));
					userInfo.setWebId(customer.getChcsNo());
					List<UserInfo> list = new ArrayList<>();
					list.add(userInfo);
					response.addUserInfo(list);
					return response;
				} else {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CHANNEL_API, OmniStdLogConstants.CHANNEL_API_COMMON, null, null, null,
							LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoErr(OmniStdLogConstants.GET_CHANNEL_USER_RETURN_NULL);
					LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					log.error("api.searchChannelUser.Exception Return null");
					LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
					
					SearchChCustResponse cr = new SearchChCustResponse();
					cr.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
					cr.setMessage("오류가 발생하였습니다. empty!!!");
					cr.setTrxUuid(response.getTrxUuid());
					return cr;
				}
			}

			if (ResultCode.SUCCESS.getCode().equals(result.getResultCode())) {
				response.setResultCode(ResultCode.SUCCESS.getCode());
				response.setUserInfo(result.getUserInfo());
			} else {
				response.setResultCode(result.getResultCode());
				response.setMessage(result.getMessage());
			}
		} else {
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(searchCustRequest.getIncsNo());
			if (StringUtils.hasText(searchCustRequest.getCi())) {
				custInfoVo.setCiNo(searchCustRequest.getCi());
			}
			
			if(StringUtils.isEmpty(searchCustRequest.getIncsNo()) && StringUtils.isEmpty(searchCustRequest.getCi())) {
				custInfoVo.setIncsNo("0");
			}
			
			Customer customer = getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null) {
				response.setResultCode(ResultCode.SUCCESS.getCode());
				UserInfo userInfo = new UserInfo();
				userInfo.setChCd(customer.getChCd());
				userInfo.setCi(customer.getCiNo());
				userInfo.setIncsNo(Integer.parseInt(customer.getIncsNo()));
				userInfo.setName(customer.getCustNm());
				userInfo.setPhone(StringUtil.mergeMobile(customer));
				userInfo.setWebId(customer.getChcsNo());
				List<UserInfo> list = new ArrayList<>();
				list.add(userInfo);
				response.addUserInfo(list);
			} else {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CHANNEL_API, OmniStdLogConstants.CHANNEL_API_COMMON, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.GET_CHANNEL_USER_RETURN_NULL);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.searchChannelUser.Exception Return null");
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				SearchChCustResponse cr = new SearchChCustResponse();
				cr.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				cr.setMessage("오류가 발생하였습니다. empty!!!");
				cr.setTrxUuid(response.getTrxUuid());
				return cr;
			}

		}
		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 경로자체회원 ID 조회에서 사용자명과 아이디만 가져오기.
	 * author   : takkies
	 * date     : 2020. 9. 7. 오후 4:40:19
	 * </pre>
	 * 
	 * @param user
	 * @return
	 */
	public Map<String, String> getChannelUser(UmChUser user) {
		Map<String, String> chuser = new HashMap<>();
		SearchChCustRequest searchCustRequest = new SearchChCustRequest();

		if (StringUtils.hasText(user.getChcsWebId())) {
			searchCustRequest.setWebId(user.getChcsWebId());
		}

		if (StringUtils.hasText(user.getChCd())) {
			searchCustRequest.setChCd(user.getChCd());
		}

		if (user.getIncsNo() > 0) {
			searchCustRequest.setIncsNo(Integer.toString(user.getIncsNo()));
		}

		SearchChCustResponse response = getChannelUser(user.getChCd(), searchCustRequest);

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {

			UserInfo userinfos[] = response.getUserInfo();
			if (userinfos != null && userinfos.length > 0) {
				chuser.put("chcd", user.getChCd());
				chuser.put("id", userinfos[0].getWebId());
				chuser.put("name", this.getApiCustomerName(user.getChCd(), Integer.toString(userinfos[0].getIncsNo()), null, userinfos[0].getName()));
				chuser.put("incsno", Integer.toString(userinfos[0].getIncsNo()));
			} else {
				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setChCd(user.getChCd());
				custInfoVo.setIncsNo(Integer.toString(user.getIncsNo()));
				Customer customer = this.getCicuemcuInfrByIncsNo(custInfoVo);
				if (customer != null) {
					chuser.put("chcd", user.getChCd());
					chuser.put("id", customer.getChcsNo());
					chuser.put("name", customer.getCustNm());
					chuser.put("incsno", customer.getIncsNo());
				}
			}
		} else {
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setChCd(user.getChCd());
			custInfoVo.setIncsNo(Integer.toString(user.getIncsNo()));
			Customer customer = this.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null) {
				chuser.put("chcd", user.getChCd());
				chuser.put("id", customer.getChcsNo());
				chuser.put("name", customer.getCustNm());
				chuser.put("incsno", customer.getIncsNo());
			}
		}
		return chuser;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 경로자체 API 로만 사용자 검색 
	 * author   : hkdang
	 * date     : 2020. 10. 16. 오후 12:04:40
	 * </pre>
	 * 
	 * @param chCd
	 * @param searchCustRequest
	 * @return
	 */
	public SearchChCustResponse getOnlyChannelUser(final String chCd, final SearchChCustRequest searchCustRequest) {
		
		SearchChCustResponse response = new SearchChCustResponse();

		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		log.debug("channel({}) data : {}", chCd, StringUtil.printJson(searchCustRequest));

		final String apiKey = this.apiEndpoint.getChannelApiKey(chCd, profile);
		final String apiUrl = this.apiEndpoint.getChannelSearchApi(chCd, profile);

		// 검색 api url이 있는 경우(없으면 통합고객검색)
		if (StringUtils.hasText(apiUrl)) {
			final HttpHeaders headers = new HttpHeaders();
			final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			headers.setContentType(mediaType);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			if (StringUtils.hasText(apiKey)) {
				headers.add(OmniConstants.XAPIKEY, apiKey);
			}

			ResponseEntity<SearchChCustResponse> apiResponse = this.apiService.post(apiUrl, headers, searchCustRequest, SearchChCustResponse.class);
			if (apiResponse == null || apiResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CHANNEL_API, OmniStdLogConstants.CHANNEL_API_COMMON, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.GET_ONLY_CHANNEL_USER_SERVER_ERROR);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.getOnlyChannelUser.Exception = {}", apiResponse.getBody());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				response.setMessage("오류가 발생하였습니다. empty!!!");
			} else {
				if (apiResponse.getBody().getUserInfo() != null && apiResponse.getBody().getUserInfo().length > 0) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
					response.setUserInfo(apiResponse.getBody().getUserInfo());
				} else {
					response.setResultCode(ResultCode.USER_NOT_FOUND.getCode());
				}
			}
		} else {
			response.setResultCode(ResultCode.REQ_INVALID_PARAM.getCode());
			response.setMessage("경로 검색 API URL이 없습니다. empty!!!");
		}

		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 강제로 인증 페이지를 호출
	 * 호출하여 실패했을 경우 sessionDataKey가 무효화됨
	 *  
	 * author   : takkies
	 * date     : 2020. 9. 9. 오전 11:39:00
	 * </pre>
	 * 
	 * @param loginId
	 * @param loginPwd
	 */
	public void callWso2CommonAuthPageForced(final String loginId, final String loginPwd) {

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

		String sessionDataKey = WebUtil.getStringSession(OmniConstants.SESSION_DATA_KEY_SESSION);
		sessionDataKey = StringUtils.isEmpty(sessionDataKey) ? "xxx" : sessionDataKey;

		Map<String, String> params = new HashMap<String, String>();

		try {
			params.put("username", URLEncoder.encode(SecurityUtil.setXyzValue(loginId), StandardCharsets.UTF_8.name()));  // - 2022.09.13 ID / PW 암호화하여 로그인 처리
			params.put("password", URLEncoder.encode(SecurityUtil.setXyzValue(loginPwd), StandardCharsets.UTF_8.name())); // 틀린 비밀번호가 들어오면 오류 - 2022.09.13 ID / PW 암호화하여 로그인 처리
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.put("isEncryption", "true");
		params.put("sessionDataKey", sessionDataKey);

		this.asyncApiService.post(this.apiEndpoint.getSsocommonauthurl(), headers, null, String.class, params);

	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 11. 오후 3:05:56
	 * </pre>
	 * 
	 * @param loginId
	 * @return
	 */
	public ApiBaseResponse disableWso2User(final String loginId) {

		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());

		ResponseEntity<ApiBaseResponse> response = this.apiService.post(this.apiEndpoint.getDisableUser(), headers, loginId, ApiBaseResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			ApiBaseResponse ar = new ApiBaseResponse();
			ar.setResultCode("ICITSVCOM999");
			return ar;
		}

		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 25. 오후 12:08:09
	 * </pre>
	 * 
	 * @param loginId
	 * @return
	 */
	public ApiBaseResponse enableWso2User(final String loginId) {

		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());

		ResponseEntity<ApiBaseResponse> response = this.apiService.post(this.apiEndpoint.getEndableUser(), headers, loginId, ApiBaseResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			ApiBaseResponse ar = new ApiBaseResponse();
			ar.setResultCode("ICITSVCOM999");
			return ar;
		}

		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 사용자 - sns 맵핑
	 * author   : hkdang
	 * date     : 2020. 9. 14. 오후 7:36:33
	 * </pre>
	 * 
	 * @param snsParam
	 */
	public ApiResponse doSnsAssociate(final SnsParam snsParam) {

		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());

		ResponseEntity<ApiResponse> response = this.apiService.post(this.apiEndpoint.getSnsAssociate(), headers, snsParam, ApiResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			ApiResponse ar = new ApiResponse();
			ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
			ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			return ar;
		}

		log.debug("◀◀◀◀◀◀ [doSnsAssociate] response : {}", StringUtil.printJson(response.getBody()));

		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 사용자 SNS Token 정보
	 * author   : hkdang
	 * date     : 2020. 9. 16. 오후 7:32:51
	 * </pre>
	 * 
	 * @param snsType
	 * @param snsTokenVo
	 * @return
	 */
	public SnsTokenResponse getSnsToken(final String snsType, final SnsTokenVo snsTokenVo) {

		SnsTokenResponse response = new SnsTokenResponse();

		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;

		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		} else {
			response.setTrxUuid(trxuuid);
		}

		final String restApiKey = this.apiEndpoint.getSnsRestApikey(snsType, profile);
		final String secretKey = this.apiEndpoint.getSnsSecretKey(snsType, profile);
		final String callbackUrl = this.apiEndpoint.getSnsCallback(snsType, profile);

		boolean empty = StringUtils.isEmpty(snsType);
		empty |= StringUtils.isEmpty(restApiKey);
		empty |= StringUtils.isEmpty(secretKey);
		empty |= StringUtils.isEmpty(callbackUrl);
		empty |= StringUtils.isEmpty(snsTokenVo.getCode());

		if (empty) {
			response.setResultCode(ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode());
			return response;
		}

		if (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode())) {

			String tokenUrl = "";
			ResponseEntity<SnsTokenResponse> snsResponse = null;
			final HttpHeaders headers = new HttpHeaders();
			MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<String, String>();
			if ("KA".equals(snsType)) {
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				bodyMap.add("grant_type", "authorization_code");
				bodyMap.add("client_id", restApiKey);
				bodyMap.add("redirect_uri", callbackUrl);
				bodyMap.add("code", snsTokenVo.getCode());
				bodyMap.add("client_secret", secretKey);
				tokenUrl = SnsUrl.KA_NEW_TOKEN;
				snsResponse = this.apiService.post(tokenUrl, headers, bodyMap, SnsTokenResponse.class);
			} else if ("NA".equals(snsType)) {
				tokenUrl = SnsUrl.NA_NEW_TOKEN;
				tokenUrl = tokenUrl.replace("{{app_key}}", restApiKey) //
						.replace("{{secret_key}}", secretKey) //
						.replace("{{redirect_uri}}", callbackUrl) //
						.replace("{{code}}", snsTokenVo.getCode()) //
						.replace("{{state}}", snsTokenVo.getState());

				snsResponse = this.apiService.get(tokenUrl, new HttpHeaders(), SnsTokenResponse.class);
			} else if ("FB".equals(snsType)) {
				tokenUrl = SnsUrl.FB_NEW_TOKEN;
				tokenUrl = tokenUrl.replace("{{app_key}}", restApiKey) //
						.replace("{{secret_key}}", secretKey) //
						.replace("{{redirect_uri}}", callbackUrl) //
						.replace("{{code}}", snsTokenVo.getCode());
				snsResponse = this.apiService.get(tokenUrl, new HttpHeaders(), SnsTokenResponse.class);
			} else if ("FBT".equals(snsType)) {
				tokenUrl = SnsUrl.FB_NEW_TOKEN;
				tokenUrl = tokenUrl.replace("{{app_key}}", restApiKey) //
						.replace("{{secret_key}}", secretKey) //
						.replace("{{redirect_uri}}", callbackUrl) //
						.replace("{{code}}", snsTokenVo.getCode());
				snsResponse = this.apiService.get(tokenUrl, new HttpHeaders(), SnsTokenResponse.class);
			}

			log.debug("▶▶▶▶▶▶ sns new token response : {}", StringUtil.printJson(snsResponse.getBody()));
			response = snsResponse.getBody();
		}

		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 사용자 SNS 프로필
	 * author   : hkdang
	 * date     : 2020. 9. 16. 오후 7:33:04
	 * </pre>
	 * 
	 * @param snsType
	 * @param snsTokenVo
	 * @return
	 */
	public SnsProfileResponse getSnsProfile(final String snsType, final SnsTokenVo snsTokenVo) {

		SnsProfileResponse response = new SnsProfileResponse();

		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		} else {
			response.setTrxUuid(trxuuid);
		}

		boolean empty = StringUtils.isEmpty(snsType);
		empty |= StringUtils.isEmpty(snsTokenVo.getAccessToken());

		if (empty) {
			response.setResultCode(ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode());
			return response;
		}

		if (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode())) {
			String tokenUrl = "";
			ResponseEntity<SnsProfileResponse> snsResponse = null;
			final HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + snsTokenVo.getAccessToken());
			if ("KA".equals(snsType)) {
				tokenUrl = SnsUrl.KA_PROFILE;
				snsResponse = this.apiService.post(tokenUrl, headers, null, SnsProfileResponse.class);
			} else if ("NA".equals(snsType)) {
				tokenUrl = SnsUrl.NA_PROFILE;
				snsResponse = this.apiService.get(tokenUrl, headers, SnsProfileResponse.class);
			} else if ("FB".equals(snsType)) {
				tokenUrl = SnsUrl.FB_PROFILE;
				snsResponse = this.apiService.get(tokenUrl, headers, SnsProfileResponse.class);
			} else if ("FBT".equals(snsType)) {
				tokenUrl = SnsUrl.FB_PROFILE;
				snsResponse = this.apiService.get(tokenUrl, headers, SnsProfileResponse.class);
			}

			log.debug("▶▶▶▶▶▶ sns profile response : {}", StringUtil.printJson(snsResponse.getBody()));
			response = snsResponse.getBody();
		}

		return response;
	}

	/**
	 * <pre>
	 * comment  : SNS 동의한 약관 받기 (현재 카카오만 사용) 
	 * author   : hkdang
	 * date     : 2020. 10. 29. 오후 12:00:56
	 * </pre>
	 * 
	 * @param snsType
	 * @return
	 */
	public SnsTermsResponse getSnsTerms(final String snsType, final SnsTokenVo snsTokenVo) {

		SnsTermsResponse response = new SnsTermsResponse();

		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		} else {
			response.setTrxUuid(trxuuid);
		}

		boolean empty = StringUtils.isEmpty(snsType);
		empty |= StringUtils.isEmpty(snsTokenVo.getAccessToken());

		if (empty) {
			response.setResultCode(ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode());
			return response;
		}

		if (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode())) {
			String reqUrl = "";
			ResponseEntity<SnsTermsResponse> snsResponse = null;
			final HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + snsTokenVo.getAccessToken());
			if ("KA".equals(snsType)) {
				reqUrl = SnsUrl.KA_TERMS;
				snsResponse = this.apiService.post(reqUrl, headers, null, SnsTermsResponse.class);
			}

			response = snsResponse.getBody();
			log.debug("▶▶▶▶▶▶ sns terms response : {}", StringUtil.printJson(response));
		}

		return response;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 카카오톡 플러스 프렌드 조회
	 * 카카오톡 채널수신동의에 대한 동의 여부 체크 위한 API
	 * plus_friend_public_id 값이 있는 경우 카카오톡 채널 수신동의한 것으로 간주
	 * 
	 * author   : takkies
	 * date     : 2020. 12. 10. 오전 11:40:30
	 * </pre>
	 * @param snsTokenVo
	 * @return
	 */
	public SnsPlusFriendsResponse getSnsChannelPlusFriends(final SnsTokenVo snsTokenVo) {
		SnsPlusFriendsResponse response = new SnsPlusFriendsResponse();
		
		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		} else {
			response.setTrxUuid(trxuuid);
		}
		
		final HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + snsTokenVo.getAccessToken());
		
		final String reqUrl = SnsUrl.KA_PLUSFRIENDS;
		
		ResponseEntity<String> snsResponse = this.apiService.get(reqUrl, headers, String.class);
		
		if (snsResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			SnsPlusFriendsResponse cr = new SnsPlusFriendsResponse();
			cr.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			cr.setTrxUuid(response.getTrxUuid());
			log.debug("▶▶▶▶▶▶ kakao plus friends response error: {}", StringUtil.printJson(cr));
			return cr;
		}
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		SnsPlusFriendsResponse plusResponse = gson.fromJson(snsResponse.getBody(), SnsPlusFriendsResponse.class);
		if (plusResponse != null) {
			return plusResponse;
		}
		
		return response;
	}
	
	public ApplePublicKeysResponse getApplePublicKeys() {
		
		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "x-www-form-urlencoded", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
		
		String publicKeysUrl = SnsUrl.AP_PUBLIC_KEY;

		ResponseEntity<ApplePublicKeysResponse> response = this.apiService.get(publicKeysUrl, headers, ApplePublicKeysResponse.class);
		
		if(response.getStatusCode() != HttpStatus.OK) {
			ApplePublicKeysResponse applePublicKeysResponse = new ApplePublicKeysResponse();
			applePublicKeysResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		
		return response.getBody();
	}

	/**
	 * 
	 * <pre>
	 * comment  : 사용자 - sns 연동 해제 
	 * author   : hkdang
	 * date     : 2020. 9. 23. 오후 6:11:39
	 * </pre>
	 * 
	 * @param snsParam
	 * @return
	 */
	public ApiResponse doSnsDisconnect(final SnsParam snsParam) {

		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());

		ResponseEntity<ApiResponse> response = this.apiService.post(this.apiEndpoint.getSnsDisconnect(), headers, snsParam, ApiResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			ApiResponse ar = new ApiResponse();
			ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
			ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			return ar;
		}

		log.debug("◀◀◀◀◀◀ [doSnsDisconnect] response : {}", StringUtil.printJson(response.getBody()));

		return response.getBody();
	}
	
	public ApiResponse doDisconnectSnsAssociated(final SnsUnlinkVo snsUnlinkVo) {

		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());

		ResponseEntity<ApiResponse> response = this.apiService.post(this.apiEndpoint.getDscnctsnsass(), headers, snsUnlinkVo, ApiResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			ApiResponse ar = new ApiResponse();
			ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
			ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			return ar;
		}

		log.debug("◀◀◀◀◀◀ [doDisconnectSnsAssociated] response : {}", StringUtil.printJson(response.getBody()));

		return response.getBody();
	}
	
	public ApiResponse joinOnSnsLinker(final SnsVo snsVo) {
		ApiResponse response = new ApiResponse();
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		log.debug("▶▶▶▶▶▶ system profile : {}", profile);
		response.setRsltCd("00");
		return response;

		/*
		 * final String apiUrl = this.apiEndpoint.getSnsJoinonLinker(profile, snsVo.getSnsType());
		 * 
		 * boolean empty = StringUtils.isEmpty(snsVo.getConnectYN()); empty |= StringUtils.isEmpty(snsVo.getUcstmid()); empty |=
		 * StringUtils.isEmpty(snsVo.getCstmid()); empty |= StringUtils.isEmpty(snsVo.getSnsAuthkey()); empty |=
		 * StringUtils.isEmpty(snsVo.getSnsType()); empty |= StringUtils.isEmpty(apiUrl);
		 * 
		 * if (empty) { response.setResultCode(ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode()); return response; }
		 * 
		 * if (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode())) {
		 * 
		 * final HttpHeaders headers = new HttpHeaders(); final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		 * headers.setContentType(mediaType); headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		 * 
		 * Gson gson = new GsonBuilder().disableHtmlEscaping().create(); String json = gson.toJson(snsVo);
		 * 
		 * // 00 = 정상처리 // ,99=ERROR // ,01=존재하지않는 통합고객번호입니다 // ,02=존재하지않는 WEB ID입니다 // ,03=WEBID와 통합고객번호가 매칭되지않습니다 // ,04=필수값이 누락되었습니다 // ,05=잘못된
		 * SNS타입 입니다
		 * 
		 * ResponseEntity<ApiResponse> snsResponse = this.apiService.post(apiUrl, headers, json, ApiResponse.class);
		 * 
		 * log.debug("▶▶▶▶▶▶ joinon sns link response : {}", StringUtil.printJson(snsResponse.getBody())); response = snsResponse.getBody(); }
		 * 
		 * return response;
		 */
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 고객상세조회
	 *            
	 * /cip/cit/custmgnt/custmgnt/svc/custinfrcommgnt/getcicuemcuinfrbyincsno/v1.00
	 *            
	 * author   : jsjang
	 * date     : 2021. 6. 21. 오전 10:27:33
	 * </pre>
	 * @param custInfoVo
	 * @param getSubInfo : 고객이 가입한 채널 정보 get
	 * @return
	 */
	public void getCicuemcuInfrByIncsNo(final CustInfoVo custInfoVo, boolean getSubInfo) {
		
		/*
		 * final HttpHeaders headers = new HttpHeaders(); final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		 * headers.setContentType(mediaType); headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		 * 
		 * Map<String, String> params = new HashMap<String, String>(); if (StringUtils.hasText(custInfoVo.getIncsNo())) { params.put("incsNo",
		 * custInfoVo.getIncsNo()); }
		 * 
		 * Gson gson = new GsonBuilder().disableHtmlEscaping().create(); String json = gson.toJson(params);
		 * 
		 * log.debug("▶▶▶▶▶▶ getCicuemcuInfrByIncsNo: {}", StringUtil.printJson(params));
		 * 
		 * ResponseEntity<CustomerData> response = this.apiService.post(this.apiEndpoint.getGetcicuemcuinfrbyincsno(), headers, json,
		 * CustomerData.class);
		 * 
		 * if (response.getStatusCode() != HttpStatus.OK) { // AP B2C 표준 로그 설정 LogInfo.setLogInfo(OmniStdLogConstants.OMNI,
		 * OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CICUEMCU_INFR_BY_INCS_NO_SERVER_ERROR, null, null, null,
		 * LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null); LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
		 * LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
		 * log.error("api.getCicuemcuInfrByIncsNo.Exception = {}", response.getBody()); LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		 * 
		 * Customer customer = new Customer(); customer.setRsltCd("ICITSVCOM999"); return; }
		 * 
		 * CustomerData customer = response.getBody(); if (getSubInfo) { List<CicuedCuChArrayTcVo> cicuedCuChArrayTcVo =
		 * customer.getCicuedCuChArrayTcVo(); if (cicuedCuChArrayTcVo != null && cicuedCuChArrayTcVo.size() > 0) {
		 * log.debug("▶▶▶▶▶ [getCicuemcuInfrByIncsNo] cicuedCuChArrayTcVo.size() : {}", cicuedCuChArrayTcVo.size());
		 * 
		 * for (CicuedCuChArrayTcVo cicuedCuChTcVo : cicuedCuChArrayTcVo) { String incsNo = cicuedCuChTcVo.getIncsNo(); String chCd =
		 * cicuedCuChTcVo.getChCd(); log.debug("▶▶▶▶▶ [getCicuemcuInfrByIncsNo] incsNo : {} chCd : {}", incsNo, chCd); }
		 * 
		 * WebUtil.setSession(OmniConstants.EXIST_CUSTOMER, cicuedCuChArrayTcVo); } }
		 */
		
		CustChListResponse custChListResponse = this.getCustChList(custInfoVo);
		if (getSubInfo) {
			List<CicuedCuChQcVo> cicuedCuChQcVoList = custChListResponse.getCicuedCuChQcVo();
			List<CicuedCuChArrayTcVo> cicuedCuChArrayTcVoList = new ArrayList<CicuedCuChArrayTcVo>();
			if (cicuedCuChQcVoList != null && cicuedCuChQcVoList.size() > 0) {
				log.debug("▶▶▶▶▶ [getCicuemcuInfrByIncsNo] cicuedCuChQcVoList.size() : {}", cicuedCuChQcVoList.size());
				
				for (CicuedCuChQcVo cicuedCuChQcVo : cicuedCuChQcVoList) {
					if(!"Y".equals(cicuedCuChQcVo.getDelYn())) {
						CicuedCuChArrayTcVo cicuedCuChArrayTcVo = new CicuedCuChArrayTcVo();
						String incsNo = cicuedCuChQcVo.getIncsNo();
						String chCd = cicuedCuChQcVo.getChCd();
						cicuedCuChArrayTcVo.setIncsNo(incsNo);
						cicuedCuChArrayTcVo.setChCd(chCd);
						log.debug("▶▶▶▶▶ [getCicuemcuInfrByIncsNo] incsNo : {} chCd : {}", incsNo, chCd);
						cicuedCuChArrayTcVoList.add(cicuedCuChArrayTcVo);
					}
				}
				
				WebUtil.setSession(OmniConstants.EXIST_CUSTOMER, cicuedCuChArrayTcVoList);
			}
		}		
	}

	public CuoptiResponse getCicuemcuoptiList(final CuoptiVo cuoptiVo) {
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		// headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		CipAthtVo cipAthtVo = CipAthtVo.builder().build();
		cuoptiVo.setCipAthtVo(cipAthtVo);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(cuoptiVo);
		
		log.debug("▶▶▶▶▶▶ getCicuemcuoptiList: {}", StringUtil.printJson(cuoptiVo));
		
		ResponseEntity<CuoptiResponse> response = this.apiService.post(this.apiEndpoint.getGetcicuemcuoptilist(), headers, json, CuoptiResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CICUEMCU_OPTI_LIST_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.getCicuemcuoptiList.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			CuoptiResponse cr = new CuoptiResponse();
			cr.setRsltCd("ICITSVCOM999");
			return cr;
		}
		
		return response.getBody();
	}
	
	public CustChListResponse getCustChList(final CustInfoVo custInfoVo) {
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		// headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.hasText(custInfoVo.getIncsNo())) {
			params.put("incsNo", custInfoVo.getIncsNo());
		}
		
		// 통합고객가입경로정보조회 API 의 경우 v1.10만 권한 정보 입력
		if(this.apiEndpoint.getGetcustchlist().contains("v1.10")) {
			CipAthtVo cipAthtVo = CipAthtVo.builder().build();
			params.put("cipAthtVo", cipAthtVo);	
		}
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		log.debug("▶▶▶▶▶▶ getCustChList: {}", StringUtil.printJson(params));
		
		ResponseEntity<CustChListResponse> response = this.apiService.post(this.apiEndpoint.getGetcustchlist(), headers, json, CustChListResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CUST_CH_LIST_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.getCustChList.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			CustChListResponse cr = new CustChListResponse();
			cr.setRsltCd("ICITSVCOM999");
			return cr;
		}
		
		if(response.getBody().getCicuedCuChQcVo() == null && response.getBody().getCicuedCuChTcVo() != null) response.getBody().setCicuedCuChQcVo(response.getBody().getCicuedCuChTcVo());
		
		return response.getBody();
	}
	
	public SSGAccessTokenResponse getSSGAccessToken(final SSGAccessTokenRequest accessTokenRequest) {
		log.debug("▶▶▶▶▶▶ getSsgAccessToken : {}", StringUtil.printJson(accessTokenRequest));
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		String url = this.apiEndpoint.getChannelAuthCodeUrl(accessTokenRequest.getChCd(), profile);
		
		if(!StringUtils.isEmpty(accessTokenRequest.getMembershipId())) {
			url += "?membershipId=" + accessTokenRequest.getMembershipId();
		} else {
			// 없으면 강제로 주입
			url += "?membershipId=" + config.getMembershipId(OmniConstants.SSG_CHCD, profile);
		}
		
		if(!StringUtils.isEmpty(accessTokenRequest.getApiKey())) {
			url += "&apiKey=" + accessTokenRequest.getApiKey();
		} else {
			// 없으면 강제로 주입
			url += "&apiKey=" + this.apiEndpoint.getChannelApiKey(OmniConstants.SSG_CHCD, profile);
		}
		
		if(!StringUtils.isEmpty(accessTokenRequest.getAuthCode())) {
			url += "&authCode=" + accessTokenRequest.getAuthCode();
		} else {
			// 없으면 에러 리턴
			
		}
		
		// Local Sample Data
		if("local".equals(profile)) {
			SSGAccessTokenResponse ssgAccessTokenResponse = new SSGAccessTokenResponse();
			ssgAccessTokenResponse.setStatus(HttpStatus.OK.value());
			ssgAccessTokenResponse.setMessage("OK");
			TokenData data = new TokenData();
			data.setAccessToken("mYOAwbUbND_u0w6wFUGzKm9PkMT__CghGUmKuRE18VTWSGzBQX0Phlgqum7HPUTlPxwBkooGIeIsrcuLedSMasans0Ub4s8qpyuCruXrMp7szFX1YriCRj7E3jk5kGs8G478uZIF1fPSm5sxGolaGr2Mb2J_9sU9tdqpnFpXS67_8udpYZKDgX7IevRpX608yDUor0HPz8p3v4khaO0hwINi8LShxq71z7AsXo5pE");
			data.setTokenType("Bearer");
			data.setExpireDate("20220530235959");
			ssgAccessTokenResponse.setData(data);
			return ssgAccessTokenResponse;
		}
		
		ResponseEntity<SSGAccessTokenResponse> response = this.apiService.get(url, headers, SSGAccessTokenResponse.class);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.SSG_API, OmniStdLogConstants.GET_SSG_ACCESS_TOKEN_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.getSsgAccessToken.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			SSGAccessTokenResponse accessTokenResponse = new SSGAccessTokenResponse();
			accessTokenResponse.setStatus(response.getStatusCode().value());
			
			return accessTokenResponse;
		}
		
		SSGAccessTokenResponse accessTokenResponse = response.getBody();
		
		return accessTokenResponse;
	}
	
	public SSGUserInfoResponse getSSGUserInfo(final SSGUserInfoRequest userInfoRequest) {
		log.debug("▶▶▶▶▶▶ getSsgUserInfo : {}", StringUtil.printJson(userInfoRequest));
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setBearerAuth(userInfoRequest.getAccessToken());
		
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		String url = this.apiEndpoint.getChannelUserInfoUrl(userInfoRequest.getChCd(), profile);
		
		if(!StringUtils.isEmpty(userInfoRequest.getMembershipId())) {
			url += "?membershipId=" + userInfoRequest.getMembershipId();
		} else {
			// 없으면 강제로 주입
			url += "?membershipId=" + config.getMembershipId(OmniConstants.SSG_CHCD, profile);
		}
		
		if(!StringUtils.isEmpty(userInfoRequest.getApiKey())) {
			url += "&apiKey=" + userInfoRequest.getApiKey();
		} else {
			// 없으면 강제로 주입
			url += "&apiKey=" + this.apiEndpoint.getChannelApiKey(OmniConstants.SSG_CHCD, profile);
		}
		
		// Local Sample Data
		if("local".equals(profile)) {
			String key = config.getChannelApi(userInfoRequest.getChCd(), "aeskey", profile);
			String iv = config.getChannelApi(userInfoRequest.getChCd(), "iv", profile);
			
			SSGUserInfoResponse ssgUserInfoResponse = new SSGUserInfoResponse();
			ssgUserInfoResponse.setStatus(HttpStatus.OK.value());
			ssgUserInfoResponse.setMessage("OK");
			
			List<Support> supportList = new ArrayList<Support>();
			try {
				Support support = new Support();
				support.setSupportType("NAME");
				support.setSupportValue(SecurityUtil.encryptionAESKey("옴니신세계449", key, iv));
				supportList.add(support);
				support = new Support();
				support.setSupportType("CI_NO");
				support.setSupportValue(SecurityUtil.encryptionAESKey("hF7Sl01sDkJElsGJ3lveVHsLK0BvhvGOCPVwTt2oHmfNcE2s34MLJGXw0xYM1ytCaHD3seWDNDEupb37hec6LI==", key, iv));
				supportList.add(support);
				support = new Support();
				support.setSupportType("PHONE_NUMBER");
				support.setSupportValue(SecurityUtil.encryptionAESKey("010-1000-1000", key, iv));
				supportList.add(support);
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			UserInfoData data = new UserInfoData();
			data.setMbrId("42987815");
			data.setSupportList(supportList);
			ssgUserInfoResponse.setData(data);
			return ssgUserInfoResponse;
		}
		
		ResponseEntity<SSGUserInfoResponse> response = this.apiService.get(url, headers, SSGUserInfoResponse.class);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.SSG_API, OmniStdLogConstants.GET_SSG_ACCESS_TOKEN_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.getSsgAccessToken.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			SSGUserInfoResponse userInfoResponse = new SSGUserInfoResponse();
			userInfoResponse.setStatus(response.getStatusCode().value());
			
			return userInfoResponse;
		}
		
		SSGUserInfoResponse userInfoResponse = response.getBody();
		
		return userInfoResponse;
	}
	
	public SSGMbrLinkResponse setSSGMembershipLink(final MembershipUserInfo membershipUserInfo) {
		log.debug("▶▶▶▶▶▶ setSSGMembershipLink : {}", StringUtil.printJson(membershipUserInfo));
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		Map<String, String> params = new HashMap<String, String>();
		try {
			params.put("mbrId", membershipUserInfo.getMbrId());
			params.put("membershipUserId", SecurityUtil.encryptionAESKey(membershipUserInfo.getIncsNo()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SSGMbrLinkResponse ssgMbrLinkResponse = new SSGMbrLinkResponse();
			ssgMbrLinkResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			
			return ssgMbrLinkResponse;
		}
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		String url = this.apiEndpoint.getChannelLinkUrl(membershipUserInfo.getChCd(), profile);
		
		if(!StringUtils.isEmpty(membershipUserInfo.getMembershipId())) {
			url += "?membershipId=" + membershipUserInfo.getMembershipId();
		} else {
			// 없으면 강제로 주입
			url += "?membershipId=" + config.getMembershipId(OmniConstants.SSG_CHCD, profile);
		}
		
		if(!StringUtils.isEmpty(membershipUserInfo.getApiKey())) {
			url += "&apiKey=" + membershipUserInfo.getApiKey();
		} else {
			// 없으면 강제로 주입
			url += "&apiKey=" + this.apiEndpoint.getChannelApiKey(OmniConstants.SSG_CHCD, profile);
		}
		
		// Local Sample Data
		if("local".equals(profile)) {
			SSGMbrLinkResponse ssgMbrLinkResponse = new SSGMbrLinkResponse();
			ssgMbrLinkResponse.setStatus(HttpStatus.OK.value());
			ssgMbrLinkResponse.setMessage("OK");
			
			return ssgMbrLinkResponse;
		}
		
		ResponseEntity<SSGMbrLinkResponse> response = this.apiService.post(url, headers, json, SSGMbrLinkResponse.class);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.SSG_API, OmniStdLogConstants.GET_SSG_ACCESS_TOKEN_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.getSsgAccessToken.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			SSGMbrLinkResponse ssgMbrLinkResponse = new SSGMbrLinkResponse();
			ssgMbrLinkResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			
			return ssgMbrLinkResponse;
		}
		
		return response.getBody();
	}
	
	public CustbyChCsNoResponse getCustbyChCsNo(final CustbyChCsNoVo custbyChCsNoVo) {
		log.debug("▶▶▶▶▶▶  getCustbyChCsNo : {}", StringUtil.printJson(custbyChCsNoVo));
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		// headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(custbyChCsNoVo);
		
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		ResponseEntity<CustbyChCsNoResponse> response = this.apiService.post(this.apiEndpoint.getGetCustbyChCsNo(), headers, json, CustbyChCsNoResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.CUST_INTG_API, OmniStdLogConstants.GET_CUSTBY_CH_CS_NO_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.getCustChList.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			CustbyChCsNoResponse cr = new CustbyChCsNoResponse();
			cr.setRsltCd("ICITSVCOM999");
			return cr;
		}
		
		return response.getBody();
	}
	
	public CustInfoResponse getCustInfoList(final String ciNo, final String name, final String phoneNumber, final String birthday) {
		
		// 1. CusInfoList Request 생성
		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setCiNo(ciNo);
		custInfoVo.setCustName(name);
		custInfoVo.setCustMobile(phoneNumber);
		custInfoVo.setAthtDtbr(birthday);
		
		// 2. CustInfoList 호출
		CustInfoResponse custInfoResponse = this.getCustListByMembership(custInfoVo);
		
		// 3. CustInfoResponse 검증
		if(custInfoResponse != null) {
			
			if("ICITSVCOM999".equals(custInfoResponse.getRsltCd())) {
				custInfoResponse.setRsltMsg(ResultCode.SYSTEM_ERROR.message());
				custInfoResponse.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
				
				return custInfoResponse;
			} else if ("ICITSVCOM001".equals(custInfoResponse.getRsltCd()) || "ICITSVCOM002".equals(custInfoResponse.getRsltCd())) { // ICITSVCOM001, ICITSVCOM002 : 통합고객이 존재하지 않습니다
				custInfoResponse.setRsltMsg(ResultCode.USER_NOT_FOUND.message());
				custInfoResponse.setRsltCd(ResultCode.USER_NOT_FOUND.getCode());
				
				return custInfoResponse;
			} else if ("ICITSVBIZ155".equals(custInfoResponse.getRsltCd())) { // ICITSVBIZ155 : 휴면고객정보가 존재
				custInfoResponse.setRsltMsg(ResultCode.USER_DORMANCY.message());
				custInfoResponse.setRsltCd(ResultCode.USER_DORMANCY.getCode());
				
				return custInfoResponse;
			} else if ("ICITSVBIZ152".equals(custInfoResponse.getRsltCd())) { // ICITSVBIZ152 : 탈퇴 후 30일 내 재 가입이 불가합니다.
				custInfoResponse.setRsltMsg(ResultCode.USER_JOIN_IMPOSSIBLE_30DAYS.message());
				custInfoResponse.setRsltCd(ResultCode.USER_JOIN_IMPOSSIBLE_30DAYS.getCode());
				
				return custInfoResponse;				
			}
			
			custInfoResponse.setRsltMsg(ResultCode.SUCCESS.message());
			custInfoResponse.setRsltCd(ResultCode.SUCCESS.getCode());
		}
		
		return custInfoResponse;
	}
	
	public CustbyChCsNoResponse getCustbyChCsNo(final String chcsNo, final String chCd) {
		// 1. CustbyChCsNo Reqeust 생성
		CustbyChCsNoVo custbyChCsNoVo = new CustbyChCsNoVo();
		custbyChCsNoVo.setChcsNo(chcsNo);
		custbyChCsNoVo.setChCd(chCd);
		
		// 2. CustbyChCsNo 호출
		CustbyChCsNoResponse custbyChCsNoResponse = this.getCustbyChCsNo(custbyChCsNoVo);
		
		// 3. CustbyChCsNoResponse 검증 - 여기서는 해당하는 고객 정보 유무만 조회
		if(custbyChCsNoResponse != null) {
			if("ICITSVCOM000".equals(custbyChCsNoResponse.getRsltCd())) { // ICITSVCOM000 : 정상처리 (연결된 정보 있음)
				custbyChCsNoResponse.setRsltMsg(ResultCode.SUCCESS.message());
				custbyChCsNoResponse.setRsltCd(ResultCode.SUCCESS.getCode());
				
				return custbyChCsNoResponse;
			}
		}
		
		return custbyChCsNoResponse;
	}

	public CustChResponse deleteCustChannelMember(final DeleteCustChRequest deleteCustChRequest) {
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(deleteCustChRequest);
		log.debug("▶▶▶▶▶▶ delete custChannel member: {}", StringUtil.printJson(deleteCustChRequest));
		ResponseEntity<CustChResponse> response = this.apiService.post(this.apiEndpoint.getGetDeletecicuedcuchcustwt(), headers, json, CustChResponse.class);
		
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			CustChResponse cr = new CustChResponse();
			cr.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
			return cr;
		}
		
		return response.getBody();
	}	
	
	public NaverMembershipResponse getNaverMembershipUserInfo(final String token) {
		log.debug("▶▶▶▶▶▶ getNaverMembershipUserInfo : {}", StringUtil.printJson(token));

		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String stmApiKey = this.apiEndpoint.getChannelApiKey(OmniConstants.NAVER_STORE_CHCD, profile);
		headers.set("stm-api-key", stmApiKey);
		
		String url = this.apiEndpoint.getChannelUserInfoUrl(OmniConstants.NAVER_STORE_CHCD, profile);
		
		if(!StringUtils.isEmpty(token)) {
			url += "?token=" + token;
		}
		
		if("local".equals(profile)) {
			NaverMembershipResponse response = new NaverMembershipResponse();
			Contents contents = new Contents();
			contents.setInterlockMemberIdNo("C123456123456");
			contents.setInterlockSellerNo("sG8bYodTTH2-xTaT1jt-wQ");
			response.setContents(contents);
			response.setOperationResult("SUCCESS");
			
			return response;
		}
		
		ResponseEntity<NaverMembershipResponse> response = this.apiService.get(url, headers, NaverMembershipResponse.class);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.NAVER_STORE_API, OmniStdLogConstants.GET_NAVER_STORE_USER_INFO_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.getNaverMembershipUserInfo.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			NaverMembershipResponse userInfoResponse = new NaverMembershipResponse();
			userInfoResponse.setOperationResult("FAIL");
			
			return userInfoResponse;
		}
		
		NaverMembershipResponse naverMembershipReponse = response.getBody();
		
		return naverMembershipReponse;
	}
	
	public NaverMembershipResponse setNaverMembershipLink(final String interlockSellerNo, final String token, final String affiliateMemberIdNo) {
		log.debug("▶▶▶▶▶▶ setNaverMembershipLink interlockSellerNo : {}, token : {}, affiliateMemberIdNo : {}", StringUtil.printJson(interlockSellerNo), StringUtil.printJson(token), StringUtil.printJson(affiliateMemberIdNo));

		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String stmApiKey = this.apiEndpoint.getChannelApiKey(OmniConstants.NAVER_STORE_CHCD, profile);
		headers.set("stm-api-key", stmApiKey);
		headers.set("affiliate-seller-key", interlockSellerNo);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("affiliateMemberIdNo", affiliateMemberIdNo);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		String url = this.apiEndpoint.getChannelLinkUrl(OmniConstants.NAVER_STORE_CHCD, profile);
		
		ResponseEntity<NaverMembershipResponse> response = this.apiService.post(url, headers, json, NaverMembershipResponse.class);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.NAVER_STORE_API, OmniStdLogConstants.SET_NAVER_STORE_MEMBERSHIP_LINK_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.setNaverMembershipLink.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			NaverMembershipResponse userInfoResponse = new NaverMembershipResponse();
			userInfoResponse.setOperationResult("FAIL");
			
			return userInfoResponse;
		}
		
		NaverMembershipResponse naverMembershipReponse = response.getBody();
		
		return naverMembershipReponse;
	}
	
	public NaverMembershipResponse deleteNaverMembership(final String interlockSellerNo, final String interlockMemberIdNo, final String affiliateMemberIdNo) {
		log.debug("▶▶▶▶▶▶ deleteNaverMembership interlockSellerNo : {}, interlockMemberIdNo : {}, affiliateMemberIdNo : {}", StringUtil.printJson(interlockSellerNo), StringUtil.printJson(interlockMemberIdNo), StringUtil.printJson(affiliateMemberIdNo));

		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String stmApiKey = this.apiEndpoint.getChannelApiKey(OmniConstants.NAVER_STORE_CHCD, profile);
		headers.set("stm-api-key", stmApiKey);
		headers.set("affiliate-seller-key", interlockSellerNo);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("interlockMemberIdNo", interlockMemberIdNo);
		params.put("affiliateMemberIdNo", affiliateMemberIdNo);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		String url = this.apiEndpoint.getChannelUnLinkUrl(OmniConstants.NAVER_STORE_CHCD, profile);
		
		ResponseEntity<NaverMembershipResponse> response = this.apiService.delete(url, headers, json, NaverMembershipResponse.class);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.NAVER_STORE_API, OmniStdLogConstants.SET_NAVER_STORE_MEMBERSHIP_UNLINK_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.setNaverMembershipLink.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			NaverMembershipResponse userInfoResponse = new NaverMembershipResponse();
			userInfoResponse.setOperationResult("FAIL");
			
			return userInfoResponse;
		}
		
		NaverMembershipResponse naverMembershipReponse = response.getBody();
		
		return naverMembershipReponse;
	}	
	
	public NaverMembershipResponse withdrawNaverMembershipLink(final String interlockSellerNo, final String interlockMemberIdNo, final String affiliateMemberIdNo) {
		log.debug("▶▶▶▶▶▶ withdrawNaverMembershipLink interlockSellerNo : {}, interlockMemberIdNo : {}, affiliateMemberIdNo : {}", StringUtil.printJson(interlockSellerNo), StringUtil.printJson(interlockMemberIdNo), StringUtil.printJson(affiliateMemberIdNo));

		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String stmApiKey = this.apiEndpoint.getChannelApiKey(OmniConstants.NAVER_STORE_CHCD, profile);
		headers.set("stm-api-key", stmApiKey);
		headers.set("affiliate-seller-key", interlockSellerNo);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("interlockMemberIdNo", interlockMemberIdNo);
		params.put("affiliateMemberIdNo", affiliateMemberIdNo);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		String url = this.apiEndpoint.getChannelWithdrawUrl(OmniConstants.NAVER_STORE_CHCD, profile);
		
		ResponseEntity<NaverMembershipResponse> response = this.apiService.put(url, headers, json, NaverMembershipResponse.class);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.NAVER_STORE_API, OmniStdLogConstants.SET_NAVER_STORE_MEMBERSHIP_WITHDRAW_SERVER_ERROR, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.setNaverMembershipLink.Exception = {}", response.getBody());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			NaverMembershipResponse userInfoResponse = new NaverMembershipResponse();
			userInfoResponse.setOperationResult("FAIL");
			
			return userInfoResponse;
		}
		
		NaverMembershipResponse naverMembershipReponse = response.getBody();
		
		return naverMembershipReponse;
	}	
	
	public ApiBaseResponse sendKakaoNoticeTalkEai(final KakaoNoticeRequest request) {
		log.debug("▶▶▶▶▶▶ kakao notice talk eai request : {}", StringUtil.printJson(request));
		
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		final String jsonBody = gson.toJson(request);
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		if(!"dev".equals(profile)) {
			headers.add("Authorization", this.apiEndpoint.getKakaoNoticeAuthorization());
		}	
		
		ResponseEntity<KakaoNoticeResponse> resp = this.apiService.post(this.apiEndpoint.getKakaoNoticeUrl(), headers, jsonBody, KakaoNoticeResponse.class);
		if (resp == null || (resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)) {
			ApiBaseResponse response = new ApiBaseResponse();
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			return response;
		}
		KakaoNoticeResponse kakaoNoticeResponse = resp.getBody();
		log.debug("▶▶▶▶▶▶ kakao notice talk eai response : {}", StringUtil.printJson(kakaoNoticeResponse));
		
		if(kakaoNoticeResponse.getResponse() == null && kakaoNoticeResponse.getTgtSMS_KKO_MSG_IInput() != null && "LMS".equals(kakaoNoticeResponse.getTgtSMS_KKO_MSG_IInput().getFailed_type())) { // 카카오 알림톡 발송 실패 후 LMS 발송은 성공으로 처리
			ApiBaseResponse response = new ApiBaseResponse();
			response.SetResponseInfo(ResultCode.SUCCESS);
			return response;
		}
		
		String resultType =  kakaoNoticeResponse.getResponse().getOUTPUT().getTYPE();
		resultType = StringUtils.isEmpty(resultType) ? kakaoNoticeResponse.getResponse().getOUTPUT().getMESSAGE() : resultType;
		
		if(!OmniConstants.SEND_KAKAO_NOTICE_EAI_SUCCESS.equals(resultType) || resultType.contains("Success")) {
			ApiBaseResponse response = new ApiBaseResponse();
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			return response;
		}
		
		ApiBaseResponse response = new ApiBaseResponse();
		response.SetResponseInfo(ResultCode.SUCCESS);
		
		return response;	
	}	

	public CicuehTncListResponse getcicuehtncalist(final Customer customer) {
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		// headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.hasText(customer.getIncsNo())) {
			params.put("incsNo", customer.getIncsNo());
		}
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		log.debug("▶▶▶▶▶▶ getcicuehtncalist: {}", StringUtil.printJson(params));
		
		ResponseEntity<CicuehTncListResponse> response = this.apiService.post(this.apiEndpoint.getGetcicuehtncalist(), headers, json, CicuehTncListResponse.class);
		
		return response.getBody();
	}
	
	public ApiBaseResponse lockUserCheck(final AbusingLockVo abusingLockVo) {
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		final String jsonBody = gson.toJson(abusingLockVo);
		
																		  
		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
																   
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());
		
		ResponseEntity<ApiBaseResponse> resp = this.apiService.post(this.apiEndpoint.getLockUserCheck(), headers, jsonBody, ApiBaseResponse.class);
		if (resp == null || (resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)) {
			ApiBaseResponse response = new ApiBaseResponse();
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			return response;
		}
		
		log.debug("▶▶▶▶▶▶ lock abusing User Check response : {}", StringUtil.printJson(resp.getBody()));
		
		ApiBaseResponse response = (ApiBaseResponse)resp.getBody();
		
		return response;	
	}	
	
	public UpdateAbusingCiResponse updateAbusingCustCino(final UpdateCustCino updateCustCino) {
		log.debug("▶▶▶▶▶▶ update Abusing CustCino : {}", StringUtil.printJson(updateCustCino));
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		final String jsonBody = gson.toJson(updateCustCino);
		
		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());
		
		ResponseEntity<UpdateAbusingCiResponse> resp = this.apiService.post(this.apiEndpoint.getUpdateAbusingCustcino(), headers, jsonBody, UpdateAbusingCiResponse.class);
		
		log.debug("▶▶▶▶▶▶ update Abusing CustCino response : {}", StringUtil.printJson(resp.getBody()));
		UpdateAbusingCiResponse response = (UpdateAbusingCiResponse)resp.getBody();
		return response;	
	}	
	
	public ApiBaseResponse lockUserOmniUpdate(final AbusingLockVo abusingLockVo) {
		log.debug("▶▶▶▶▶▶lock User Omni Update : {}", StringUtil.printJson(abusingLockVo));
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		final String jsonBody = gson.toJson(abusingLockVo);
		
		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());
		
		ResponseEntity<ApiBaseResponse> resp = this.apiService.post(this.apiEndpoint.getUpdateAbusingOmni(), headers, jsonBody, ApiBaseResponse.class);
		if (resp == null || (resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)) {
			ApiBaseResponse response = new ApiBaseResponse();
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			return response;
		}
		log.debug("▶▶▶▶▶▶ lock User Omni Update response : {}", StringUtil.printJson(resp.getBody()));
		ApiBaseResponse response = (ApiBaseResponse)resp.getBody();
		return response;	
	}
	
	public com.amorepacific.oneap.auth.wso2.vo.TokenData getTokenByPasswordGrant(final String username, final String password, final String credential) {
		
		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "x-www-form-urlencoded", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setBasicAuth(credential);
		
		String tokenUrl = this.apiEndpoint.getOneApUrl().concat("/oauth2/token");
		tokenUrl = tokenUrl.concat("?").concat("grant_type=password&username=").concat(username).concat("&password=").concat(password);
		
		ResponseEntity<com.amorepacific.oneap.auth.wso2.vo.TokenData> resp = this.apiService.post(tokenUrl, headers, null, com.amorepacific.oneap.auth.wso2.vo.TokenData.class);
		
		if (resp == null || (resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)) {
			com.amorepacific.oneap.auth.wso2.vo.TokenData tokenData = new com.amorepacific.oneap.auth.wso2.vo.TokenData();
			return tokenData;
		}
		log.debug("▶▶▶▶▶▶ getTokenByPasswordGrant response : {}", StringUtil.printJson(resp.getBody()));
		com.amorepacific.oneap.auth.wso2.vo.TokenData tokenData = (com.amorepacific.oneap.auth.wso2.vo.TokenData)resp.getBody();
		return tokenData;
	}
	
	public ApiBaseResponse sendWeb2AppAuthKeyApi(final AuthKeyVo web2AppVo) {
		log.debug("▶▶▶▶▶▶ sendWeb2AppAuthKeyApi : {}", StringUtil.printJson(web2AppVo));
//		
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("uuid", web2AppVo.getWeb2appid());
//		params.put("accessToken", web2AppVo.getAccesstoken());
//
//		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//		String json = gson.toJson(params);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		final String jsonBody = gson.toJson(web2AppVo);
//		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.apiEndpoint.getApikey());
		
		ResponseEntity<String> resp = this.apiService.post("https://dev-one-ap.amorepacific.com/api/v1/mgmt/web2app/sendauthkey", headers, jsonBody, String.class);
		if (resp == null || (resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)) {
			ApiBaseResponse response = new ApiBaseResponse();
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			return response;
		}
		
		log.debug("▶▶▶▶▶▶ Web2App Test response : {}", StringUtil.printJson(resp.getBody()));
		
		ApiBaseResponse response = new ApiBaseResponse();
		response.SetResponseInfo(ResultCode.SUCCESS);
		
		return response;	
	}
	
	public OfflineLoginResponse getOfflineLoginInfo(final String chCd, final String key, final OfflineLoginRequest offlineLoginRequest) {
		log.debug("▶▶▶▶▶▶ getOfflineLoginInfo : {}", StringUtil.printJson(offlineLoginRequest));
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		final String jsonBody = gson.toJson(offlineLoginRequest);
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		//if("local".equals(profile)) { // 로컬에서 테스트를 위해 예외처리
		//	OfflineLoginResponse offlineLoginResponse = new OfflineLoginResponse();
		//	offlineLoginResponse.setStorenm("아리따움 양재역점");
		//	offlineLoginResponse.setStorecd("11007253");
		//	offlineLoginResponse.setUser_id("1100725301");
		//	offlineLoginResponse.setResultCode("1");
		//	offlineLoginResponse.setUser_nm("테스트");
		//	offlineLoginResponse.setLogin_yn("Y");
		//	return offlineLoginResponse;
		//}
		
		String url = this.apiEndpoint.getChannelOfflineLoginUrl(chCd, profile);
		
		ResponseEntity<String> response = this.apiService.post(url, headers, jsonBody, String.class);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			OfflineLoginResponse offlineLoginResponse = new OfflineLoginResponse();
			offlineLoginResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			return offlineLoginResponse;
		}
		
		// 정상 리턴인 경우 복호화 후 리턴
		String value = response.getBody();
		String decrypt = SecurityUtil.decrypt(value, key);
		
		// 복호화 실패 시 오류 리턴
		if(StringUtils.isEmpty(decrypt)) {
			OfflineLoginResponse offlineLoginResponse = new OfflineLoginResponse();
			offlineLoginResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			return offlineLoginResponse;
		}
		
		log.debug("▶▶▶▶▶▶ getOfflineLoginInfo result decrypt : {}", decrypt);
		OfflineLoginResponse offlineLoginResponse = gson.fromJson(decrypt, OfflineLoginResponse.class);
		log.debug("▶▶▶▶▶▶ getOfflineLoginInfo result offlineLoginResponse : {}", StringUtil.printJson(offlineLoginResponse));
		
		return offlineLoginResponse;
	}
}
