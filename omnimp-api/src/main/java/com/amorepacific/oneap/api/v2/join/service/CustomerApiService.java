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
package com.amorepacific.oneap.api.v2.join.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.api.common.service.RestApiService;
import com.amorepacific.oneap.api.v2.join.vo.CreateCustChannelJoinResponse;
import com.amorepacific.oneap.api.v2.join.vo.CreateCustChannelRequest;
import com.amorepacific.oneap.api.v2.join.vo.CreateCustResponse;
import com.amorepacific.oneap.api.v2.join.vo.CreateCustVO;
import com.amorepacific.oneap.api.v2.join.vo.CustMarketingRequest;
import com.amorepacific.oneap.api.v2.join.vo.CustTncaRequest;
import com.amorepacific.oneap.api.v2.join.vo.JoinData;
import com.amorepacific.oneap.api.v2.join.vo.JoinRequest;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.exception.OmniException;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Marketing;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.BpEditUserRequest;
import com.amorepacific.oneap.common.vo.api.BpEditUserResponse;
import com.amorepacific.oneap.common.vo.api.BpUserData;
import com.amorepacific.oneap.common.vo.api.ChTermsVo;
import com.amorepacific.oneap.common.vo.api.CicuedCuChQcVo;
import com.amorepacific.oneap.common.vo.api.CipAthtVo;
import com.amorepacific.oneap.common.vo.api.CreateChCustRequest;
import com.amorepacific.oneap.common.vo.api.CreateUserData;
import com.amorepacific.oneap.common.vo.api.CuoptiResponse;
import com.amorepacific.oneap.common.vo.api.CuoptiVo;
import com.amorepacific.oneap.common.vo.api.CustChListResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.OfflineChannelApiRequest;
import com.amorepacific.oneap.common.vo.api.OfflineChannelApiRequest.CicuedCuChCsTcVo;
import com.amorepacific.oneap.common.vo.api.OfflineChannelApiRequest.CicuemCuOptiCsTcVo;
import com.amorepacific.oneap.common.vo.dormancy.DormancyResponse;
import com.amorepacific.oneap.common.vo.user.CicuedCuChArrayTcVo;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.PointResponse;
import com.amorepacific.oneap.common.vo.user.PointVo;
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
	private RestApiService apiService;
	
	@Autowired
	private JoinService joinService;

	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();

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

		ResponseEntity<ApiResponse> response = this.apiService.postJoinOn(this.apiEndpoint.getSavecicuedcutnca(), headers, json, ApiResponse.class);
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

		ResponseEntity<ApiResponse> response = this.apiService.postJoinOn(this.apiEndpoint.getSavecicuemcuoptilist(), headers, json, ApiResponse.class);
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

		ResponseEntity<PointResponse> response = this.apiService.postJoinOn(this.apiEndpoint.getGetptinq(), headers, pointVo, PointResponse.class);

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
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		// if("stg".equals(profile)) {
			try {
				CreateCustChannelRequest chCustRequest = JoinData.buildIntegrated030ChannelCustomerData(joinRequest);
				log.debug("▷▷▷▷▷▷ integrated 030 channel customer api data : {}", StringUtil.printJson(chCustRequest));
				
				final HttpHeaders headers = new HttpHeaders();
				final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
				headers.setContentType(mediaType);
				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				String json = gson.toJson(chCustRequest);
				ResponseEntity<CreateCustChannelJoinResponse> response = this.apiService.postJoinOn(this.apiEndpoint.getCreatecustchnjoin(), headers, json, CreateCustChannelJoinResponse.class);
				
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
		 * ResponseEntity<ApiResponse> response = this.apiService.postJoinOn(this.apiEndpoint.getCreateBpUser(), headers, userData,
		 * ApiResponse.class);
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
		
		// STG 환경에서는 뷰티포인트 API 호출 시 성공 처리
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		// if("stg".equals(profile)) {
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
		 * ResponseEntity<ApiResponse> response = this.apiService.postJoinOn(this.apiEndpoint.getCheckBpOnlineId(), headers, userData,
		 * ApiResponse.class); if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) { // AP B2C 표준 로그 설정
		 * LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.BP_API, OmniStdLogConstants.CHECK_BEAUTY_POINT_ONLINE_ID_SERVER_ERROR,
		 * null, null, null, LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null); LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
		 * LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
		 * log.error("api.checkBpOnlineId.Exception = {}", response.getBody()); LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		 * 
		 * ApiResponse ar = new ApiResponse(); ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode()); ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		 * return ar; }
		 * 
		 * return response.getBody();
		 */
	}

	public ApiResponse editBpUser(final BpEditUserRequest bpEditUserRequest) {
		
		ApiResponse ar = new ApiResponse();
		ar.setRsltCd(ResultCode.BP_SUCCESS.getCode());
		ar.setResultCode(ResultCode.BP_SUCCESS.getCode());
		return ar;

		/*
		 * log.debug("▶▶▶▶▶▶ [edit bp] user request : {}", StringUtil.printJson(bpEditUserRequest)); final HttpHeaders headers = new HttpHeaders();
		 * MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8); headers.setContentType(mediaType);
		 * headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); headers.add("Referer", "ecp.amorepacific.com");
		 * 
		 * ApiResponse ar = new ApiResponse(); ResponseEntity<String> response = this.apiService.postJoinOn(this.apiEndpoint.getCheckBpEditUser(),
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
		
		//20230425 오설록 백화점/백화점/설화수FSS api 변경 로직 추가
				ResponseEntity<ApiBaseResponse> apiResponse = null;
				OfflineChannelApiRequest offlineChannelRequest = new OfflineChannelApiRequest();
				offlineChannelRequest.setIncsNo(Integer.parseInt(joinRequest.getIncsno()));
				offlineChannelRequest.setCustNm(joinRequest.getUnm());
				offlineChannelRequest.setJoinChCd(joinRequest.getChcd());
				offlineChannelRequest.setJndvCd(joinRequest.getDeviceType());
//				offlineChannelRequest.setAtclCd(""); 
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
//				offlineChannelRequest.setJoinPrtnId(joinRequest.getJoinPrtnId());
//				offlineChannelRequest.setJoinEmpId(joinRequest.getJoinEmpId());
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
				
				CicuemCuOptiCsTcVo CicuemCuOptiTcVo = new CicuemCuOptiCsTcVo();
				// Mandatory
				if(chCd.equals(OmniConstants.OSULLOC_OFFLINE_CHCD) || chCd.equals(OmniConstants.OSULLOC_CHCD) || chCd.equals(OmniConstants.OSULLOC_DEPARTMENT_CHCD)) {
					CicuemCuOptiTcVo.setChCd(OmniConstants.OSULLOC_DEPARTMENT_CHCD);
				}else {
					CicuemCuOptiTcVo.setChCd(createChCustRequest.getChCd());
				}
				// Optional
				// isMarketingSyncBpEnable 면 000 채널 수신동의 여부와 동기화
				List<Marketing> joinMarketings = joinRequest.getMarketings();

				if(config.isMarketingSyncBpEnable(joinRequest.getChcd(), profile)) {
					// join-on 가입하는 API에 수신동의 항목 추가
					if (joinMarketings != null && !joinMarketings.isEmpty()) {
						for (Marketing joinMarketing : joinMarketings) {
							if ("000".equals(joinMarketing.getChCd())) {
								CicuemCuOptiTcVo.setSmsOptiYn(joinMarketing.getSmsAgree()); // SMS수신동의여부
							}
						}
					}
					CicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // SMS수신동의일자
				} else {
					CicuemCuOptiTcVo.setSmsOptiYn("N"); // SMS수신동의여부
					CicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // SMS수신동의일자
				}
				CicuemCuOptiTcVo.setEmlOptiYn("N"); // 이메일수신동의여부
				CicuemCuOptiTcVo.setEmlOptiDt(DateUtil.getCurrentDate());
				CicuemCuOptiTcVo.setDmOptiYn("N"); // DM수신동의여부
				CicuemCuOptiTcVo.setDmOptiDt(DateUtil.getCurrentDate());
				CicuemCuOptiTcVo.setTmOptiYn("N"); // TM수신동의여부
				CicuemCuOptiTcVo.setTmOptiDt(DateUtil.getCurrentDate());
				CicuemCuOptiTcVo.setIntlOptiYn("N"); // 알림톡수신동의여부
				CicuemCuOptiTcVo.setIntlOptiDt(DateUtil.getCurrentDate());
				CicuemCuOptiTcVo.setFscrId("OCP");
				CicuemCuOptiTcVo.setLschId("OCP");
				offlineChannelRequest.setCicuemCuOptiCsTcVo(CicuemCuOptiTcVo);
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
		
		CipAthtVo cipAthtVo = CipAthtVo.builder().build();
		params.put("cipAthtVo", cipAthtVo);
		
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
		
		return response.getBody();
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
		
		ResponseEntity<CuoptiResponse> response = this.apiService.postJoinOn(this.apiEndpoint.getGetcicuemcuoptilist(), headers, json, CuoptiResponse.class);
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
	
	/**
	 * 
	 * <pre>
	 * comment  : 사용자 휴면 해제하기
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


	
	
}
