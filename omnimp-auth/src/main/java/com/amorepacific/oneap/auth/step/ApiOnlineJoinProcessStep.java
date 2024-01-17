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
 * Date   	          : 2020. 10. 24..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.step;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustChannelRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustVO;
import com.amorepacific.oneap.auth.api.vo.ivo.CustMarketingRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustMarketingVo;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaVo;
import com.amorepacific.oneap.auth.api.vo.ovo.CreateCustChannelJoinResponse;
import com.amorepacific.oneap.auth.api.vo.ovo.CreateCustResponse;
import com.amorepacific.oneap.auth.common.CommonAuth;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.join.service.JoinService;
import com.amorepacific.oneap.auth.join.vo.JoinData;
import com.amorepacific.oneap.auth.join.vo.JoinRequest;
import com.amorepacific.oneap.auth.mgmt.service.MgmtService;
import com.amorepacific.oneap.auth.terms.service.TermsService;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.UuidUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.Marketing;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.OnOffline;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.BpUserData;
import com.amorepacific.oneap.common.vo.api.ChTermsVo;
import com.amorepacific.oneap.common.vo.api.ChUserVo;
import com.amorepacific.oneap.common.vo.api.CreateChCustRequest;
import com.amorepacific.oneap.common.vo.api.CreateUserData;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmChUser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.step 
 *    |_ ApiOnlineJoinProcessStep.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 10. 24.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Component
public class ApiOnlineJoinProcessStep {

	@Autowired
	private CustomerApiService customerApiService;

	@Autowired
	private TermsService termsService;

	@Autowired
	private MgmtService mgmtService;

	@Autowired
	private JoinService joinService;

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private CommonAuth commonAuth;

	private ConfigUtil config = ConfigUtil.getInstance();

	public BaseResponse joinPhoneRegistProcess(final JoinRequest joinRequest) {
		final StopWatch stopwatch = new StopWatch("joinPhoneRegistProcess");
		Map<String, Object> object = new HashMap<>();

		object.put(OmniConstants.PROCESS_REQUEST, joinRequest);
		object.put(OmniConstants.PROCESS_STOPWATCH, stopwatch);
		object.put(OmniConstants.PROCESS_LOGGING_ID, UuidUtil.getIdByDate("P"));

		JoinPhoneRegistProcess crp = new JoinPhoneRegistProcess(object);

		// 1. 고객통합 등록 API 성공 --> 뷰포 등록 API, 고객통합 등록 API 실패 --> 종료
		BaseResponse success = crp.joinRegistStep(crp::updateIntegratedCustomerCallApi, null);

		// 2. 뷰포 API , 실패 --> 고객통합 취소 API
		if (ResultCode.SUCCESS.getCode().equals(success.getResultCode())) { // 고객통합 등록 API 성공 --> 뷰포 등록 API
			// 고객통합 등록 API 성공 -------------> [ 뷰포 등록 API ], 실패 -------------> [ 고객통합 취소 API ]
			success = crp.joinRegistStep(crp::registBeautyPointCustomerCallApi, null /* crp::cancelIntegratedCustomerCallApi*/);
		}

		// 3. 고객통합 채널 오프라인 경로 등록, 실패 --> 고객통합 채널 오프라인 경로 취소 API
		if (ResultCode.SUCCESS.getCode().equals(success.getResultCode())) {
			success = crp.joinRegistStep(crp::registIntegrateOfflineChannelCustomer, crp::cancelIntegrateOfflineChannelCustomer);
		}

		// 4. 오프라인 채널 경로 등록, 이건 끝, 실패 --> 고객통합 채널 오프라인 경로 취소 API
		if (ResultCode.SUCCESS.getCode().equals(success.getResultCode())) {
			success = crp.joinRegistStep(crp::registOfflineChannelCustomer, crp::cancelIntegrateOfflineChannelCustomer);
		}

		// 5. 고객통합 채널 온라인 경로 등록, 실패 --> 고객통합 채널 온라인 경로 취소 API
		if (ResultCode.SUCCESS.getCode().equals(success.getResultCode())) {
			success = crp.joinRegistStep(crp::registIntegrateOnlineChannelCustomer, crp::cancelIntegrateOnlineChannelCustomer);
		}

		// 6. 온라인 채널 경로 등록, 이건 끝, 실패 --> 고객통합 채널 온라인 경로 취소 API
		if (ResultCode.SUCCESS.getCode().equals(success.getResultCode())) {
			success = crp.joinRegistStep(crp::registOnlineChannelCustomer, crp::cancelIntegrateOnlineChannelCustomer);
		}

		// 7. 옴니 WSO2 사용자 등록
		if (ResultCode.SUCCESS.getCode().equals(success.getResultCode())) {
			success = crp.joinRegistStep(crp::registOmniCustomer, null);
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		log.info("\n" + stopwatch.prettyPrint());
		commonAuth.insertTaskLog(object, stopwatch);
		return success;
	}

	private class JoinPhoneRegistProcess {

		private Map<String, Object> request;

		private JoinPhoneRegistProcess(final Map<String, Object> request) {
			this.request = request;
		}

		public BaseResponse joinRegistStep(Function<Map<String, Object>, BaseResponse> func, Function<Map<String, Object>, BaseResponse> failed) {
			BaseResponse success = func.apply(request);
			if (!ResultCode.SUCCESS.getCode().equals(success.getResultCode()) && failed != null) {
				return failed.apply(request);
			}
			return success;
		}

		// 고객통합 수정 API 호출
		public BaseResponse updateIntegratedCustomerCallApi(final Map<String, Object> request) {
			BaseResponse response = new BaseResponse();
			final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
			if (!stopwatch.isRunning()) {
				stopwatch.start("updateIntegratedCustomerCallApi");
			}
			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			log.debug("updateIntegratedCustomerCallApi {}", StringUtil.printJson(joinRequest));
			String loggingId = OmniUtil.getLoggingId(request);
			ApiResponse apiResponse = null;
			try {
				boolean success = true;

				CustInfoVo custInfoVo = new CustInfoVo();
				// 통합고객번호 있을경우 회원 여부 체크
				if (StringUtils.hasText(joinRequest.getIncsno())) {
					custInfoVo.setIncsNo(joinRequest.getIncsno());

					// 통합고객번호로 사용자를 다시 찾기
					Customer customerincsno = customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
					if (customerincsno != null && "ICITSVCOM000".equals(customerincsno.getRsltCd())) {
						if (customerincsno != null && StringUtils.hasText(customerincsno.getCiNo())) {
							custInfoVo.setCiNo(customerincsno.getCiNo());
						} else {
							log.debug("① conversion customer dormancy ? {}", customerincsno.getDrccCd());
							if ("Y".equals(customerincsno.getDrccCd())) { // 휴면인 경우 복원 필요
								String name = joinService.releaseDormancyCustomerName(customerincsno.getIncsNo(), joinRequest.getChcd());
								if (StringUtils.hasText(name)) {
									joinRequest.setUnm(name);
								}
							}
						}
					} else {
						response.setResultCode(customerincsno.getRsltCd());
						if (stopwatch.isRunning()) {
							stopwatch.stop();
						}
						return response;
					}

					CustInfoResponse custinfoResponse = customerApiService.getCustList(custInfoVo);
					log.debug("▶▶▶▶▶▶ ① conversion customer exist check response : {}", StringUtil.printJson(custinfoResponse));
					if (custinfoResponse.getRsltCd().equals("ICITSVCOM000")) { // 있으면 수정 프로세스
						response.setResultCode(custinfoResponse.getRsltCd());
					} else {
						log.debug("▶▶▶▶▶▶ ① conversion customer check : {}, {}", custinfoResponse.getRsltCd(), custinfoResponse.getRsltMsg());
						response.setResultCode(custinfoResponse.getRsltCd());
						if (stopwatch.isRunning()) {
							stopwatch.stop();
						}
						return response;
					}
					Customer customers[] = custinfoResponse.getCicuemCuInfTcVo();
					if (customers != null && customers.length > 0) {
						Customer customer = customers[0]; // 첫번째 데이터가 최신임.
						//if (StringUtils.hasText(customer.getJoinPrtnId())) {
						//	joinRequest.setJoinPrtnId(customer.getJoinPrtnId());
						//}
						//if ("Y".equalsIgnoreCase(customer.getCustWtYn())) { // 탈퇴사용자
						if (StringUtils.hasText(customer.getCustWtDttm())) {
							log.debug("▶▶▶▶▶▶ ① conversion customer check withdraw : {}[{}]", customer.getCustWtYn(), customer.getCustWtDttm());
							response.setResultCode("ICITSVCOM001"); // ICITSVCOM002
							if (stopwatch.isRunning()) {
								stopwatch.stop();
							}
							return response;
						}
						
						// 존재하더라도 본인인증 정보가 불일치 할 경우 등록 프로세스(회원가입 단계에서 신규 가입인 경우)
						CertResult certResult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
						if(certResult != null && "5".equals(joinRequest.getJoinType()) &&
								(!certResult.getPhone().equals(StringUtil.mergeMobile(customer)) || !certResult.getCiNo().equals(customer.getCiNo()) || !certResult.getBirth().equals(customer.getAthtDtbr()) || !certResult.getName().equals(customer.getCustNm()))) {
							response.setResultCode("ICITSVCOM001"); // ICITSVCOM002
							if (stopwatch.isRunning()) {
								stopwatch.stop();
							}
							return response;
						}

						if ("Y".equalsIgnoreCase(customer.getDrccCd())) {
							String name = joinService.releaseDormancyCustomerName(customerincsno.getIncsNo(), joinRequest.getChcd());
							if (StringUtils.hasText(name)) {
								joinRequest.setUnm(name);
							}
						}
					}

					// 경로 추가 API 호출 - 2022.12.29 추가
					BaseResponse baseResponse = registIntegrateChannelCustomer(OnOffline.Online, request);
					if(ResultCode.SUCCESS.getCode().equals(baseResponse.getResultCode())) { // 성공 시 약관 정보 입력
						// 중요) 경로 약관은 옴니에만 저장함.
						// 약관수정, 뷰티포인트 약관 동의
						List<Terms> joinBpTerms = joinRequest.getBpterms();
						if (joinBpTerms != null && !joinBpTerms.isEmpty()) {
							CustTncaRequest custTncaRequest = new CustTncaRequest();
							List<CustTncaVo> custTncaVos = new ArrayList<>();
							for (Terms joinTerm : joinBpTerms) {
								CustTncaVo terms = new CustTncaVo();
								terms.setTcatCd(joinTerm.getTcatCd());
								terms.setIncsNo(joinRequest.getIncsno());
								terms.setTncvNo(joinTerm.getTncvNo());
								terms.setTncAgrYn(joinTerm.getTncAgrYn());
								terms.setLschId("OCP");
								terms.setChgChCd(joinTerm.getChgChCd());
								terms.setChCd(joinRequest.getChcd());
								custTncaVos.add(terms);
							}
							CustTncaVo custTncaVoArr[] = custTncaVos.toArray(new CustTncaVo[custTncaVos.size()]);
							custTncaRequest.setCicuedCuTncaTcVo(custTncaVoArr);
							apiResponse = customerApiService.savecicuedcutnca(custTncaRequest);
							success &= "ICITSVCOM000".equals(apiResponse.getRsltCd());
							if (!success) {
								commonAuth.insertProcessLog(loggingId, //
										"고객통합 수정 API(약관동의)", //
										customerApiService.getClass().getCanonicalName(), //
										"savecicuedcutnca", //
										"U", //
										custTncaRequest.toString(), // StringUtil.printJson(createCustVo);
										apiResponse.toString(), // StringUtil.printJson(custResponse);
										apiResponse.getRsltCd(), //
										apiResponse.getRsltMsg());
							}
						}

						// 수신 동의 처리
						List<Marketing> joinMarketings = joinRequest.getMarketings();
						if (joinMarketings != null && !joinMarketings.isEmpty()) {
							CustMarketingRequest custMarketingRequest = new CustMarketingRequest();
							List<CustMarketingVo> custMarketingVos = new ArrayList<>();
							for (Marketing joinMarketing : joinMarketings) {
								CustMarketingVo marketing = new CustMarketingVo();
								marketing.setChCd(joinRequest.getChcd());
								marketing.setIncsNo(joinRequest.getIncsno());
								marketing.setEmlOptiYn("N");
								marketing.setSmsOptiYn(joinMarketing.getSmsAgree());
								marketing.setDmOptiYn("N");
								marketing.setTmOptiYn("N");
								marketing.setKkoIntlOptiYn("N");
								marketing.setFscrId("OCP");
								marketing.setLschId("OCP");
								custMarketingVos.add(marketing);
							}

							CustMarketingVo cicuemCuOptiTcVoArr[] = custMarketingVos.toArray(new CustMarketingVo[custMarketingVos.size()]);
							custMarketingRequest.setCicuemCuOptiTcVo(cicuemCuOptiTcVoArr);
							apiResponse = customerApiService.savecicuemcuoptilist(custMarketingRequest);

							success &= "ICITSVCOM000".equals(apiResponse.getRsltCd());
							if (!success) {
								commonAuth.insertProcessLog(loggingId, //
										"고객통합 수정 API(마케팅수신동의)", //
										customerApiService.getClass().getCanonicalName(), //
										"savecicuemcuoptilist", //
										"U", //
										custMarketingRequest.toString(), // StringUtil.printJson(createCustVo);
										apiResponse.toString(), // StringUtil.printJson(custResponse);
										apiResponse.getRsltCd(), //
										apiResponse.getRsltMsg());
							}
						}						
					}
					
				}

				if (success) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
				} else {
					if (apiResponse != null) {
						response.setResultCode(apiResponse.getRsltCd());
						response.setMessage(apiResponse.getRsltMsg());
					} else {
						response.setResultCode("ICITSVCOM999");
					}
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정 
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_UPDATE_INTEGRATED_CUSTOMER_CALL_API_FAIL, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				log.error(e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				response.setMessage(ResultCode.SYSTEM_ERROR.message());
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				commonAuth.insertProcessLog(loggingId, //
						"고객통합 수정 API", //
						this.getClass().getCanonicalName(), //
						"updateIntegratedCustomerCallApi", //
						"F", //
						joinRequest.toString(), // StringUtil.printJson(createCustVo);
						e.getMessage(), // StringUtil.printJson(custResponse);
						response.getResultCode(), //
						response.getMessage());
			}
			log.debug(OmniUtil.getApiResultCode(response, "JoinPhoneRegistProcess.updateIntegratedCustomerCallApi"));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			return response;
		}

		// 고객통합 취소 API
		public BaseResponse cancelIntegratedCustomerCallApi(final Map<String, Object> request) {
			BaseResponse response = new BaseResponse();
			final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
			if (!stopwatch.isRunning()) {
				stopwatch.start("cancelIntegratedCustomerCallApi");
			}
			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			log.debug("cancelIntegratedCustomerCallApi {}", StringUtil.printJson(joinRequest));
			String loggingId = OmniUtil.getLoggingId(request);
			try {
				// 고객통합 입력 데이터 구성
				final Channel channel = commonService.getChannel(joinRequest.getChcd());
				CreateCustVO createCustVo = JoinData.buildIntegratedOnlineCreateCustomerData(joinRequest, channel);
				createCustVo.setJoinCnclYn("Y"); // 회원가입망취소시 'Y'
				CreateCustResponse custcreateResponse = customerApiService.createCust(createCustVo);

				log.debug("▶▶▶▶▶▶ ① integrated customer cancel api response : {}", StringUtil.printJson(custcreateResponse));

				// 고객통합 취소 API 호출 후 Process 종료되어야 함.
				boolean success = "ICITSVCOM000".equals(custcreateResponse.getRsltCd()) || "ICITSVCOM009".equals(custcreateResponse.getRsltCd());
				if (success) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
				} else {
					response.setResultCode(custcreateResponse.getRsltCd());
					response.setMessage(custcreateResponse.getRsltMsg());
					// 프로세스 로그 기록
					commonAuth.insertProcessLog(loggingId, //
							"고객통합 취소 API(망취소)", //
							customerApiService.getClass().getCanonicalName(), //
							"createCust", //
							"D", //
							createCustVo.toString(), // StringUtil.printJson(createCustVo);
							custcreateResponse.toString(), // StringUtil.printJson(custResponse);
							custcreateResponse.getRsltCd(), //
							custcreateResponse.getRsltMsg());
				}
				log.debug("2cancelIntegratedCustomerCallApi {}", StringUtil.printJson(joinRequest));
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_CANCEL_INTEGRATED_CUSTOMER_CALL_API_FAIL, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				log.error(e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				response.setMessage(ResultCode.SYSTEM_ERROR.message());
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				commonAuth.insertProcessLog(loggingId, //
						"고객통합 취소 API(망취소)", //
						this.getClass().getCanonicalName(), //
						"cancelIntegratedCustomerCallApi", //
						"F", //
						joinRequest.toString(), // StringUtil.printJson(createCustVo);
						e.getMessage(), // StringUtil.printJson(custResponse);
						response.getResultCode(), //
						response.getMessage());
			}
			log.debug(OmniUtil.getApiResultCode(response, "JoinPhoneRegistProcess.cancelIntegratedCustomerCallApi"));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			return response;
		}

		// 뷰포인트 등록 API
		// 오프라인인 경우 아이디 등록인 경우 처리하고 아닌 경우 스킵
		public BaseResponse registBeautyPointCustomerCallApi(final Map<String, Object> request) {
			BaseResponse response = new BaseResponse();
			final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
			if (!stopwatch.isRunning()) {
				stopwatch.start("registBeautyPointCustomerCallApi");
			}
			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			log.debug("registBeautyPointCustomerCallApi {}", StringUtil.printJson(joinRequest));
			String loggingId = OmniUtil.getLoggingId(request);
			try {
				BpUserData searchUserData = new BpUserData();
				searchUserData.setIncsNo(joinRequest.getIncsno()); // custResponse.getIncsNo()
				searchUserData.setCstmid(joinRequest.getLoginid());

				log.debug("▶▶▶▶▶▶ ② beautypoint customer check id api data : {}", StringUtil.printJson(searchUserData));

				// 온라인 회원ID 중복 체크
				ApiResponse createResponse = customerApiService.checkBpOnlineId(searchUserData);

				log.debug("▶▶▶▶▶▶ ② beautypoint customer check id api response : {}", StringUtil.printJson(createResponse));

				boolean success = "000".equals(createResponse.getRsltCd());

				if (success) {

					BpUserData userData = new BpUserData();
					userData.setIncsNo(joinRequest.getIncsno()); // custResponse.getIncsNo()
					userData.setCstmid(joinRequest.getLoginid());
					userData.setPswd(joinRequest.getLoginpassword());
					// join-on 가입하는 API에 수신동의 항목 추가
					List<Marketing> joinMarketings = joinRequest.getMarketings();
					if (joinMarketings != null && !joinMarketings.isEmpty()) {
						for (Marketing joinMarketing : joinMarketings) {
							if ("000".equals(joinMarketing.getChCd())) {
								userData.setSmsReceiveType(joinMarketing.getSmsAgree());
							}
						}
					}

					createResponse = customerApiService.createBpUser(userData, joinRequest); // 뷰티포인트 등록
					log.debug("▶▶▶▶▶▶ ② beautypoint customer regist api response : {}", StringUtil.printJson(createResponse));

					// 이미 가입된 회원의 경우도 성공으로 처리할 필요있음
					success &= "000".equals(createResponse.getRsltCd()) || "050".equals(createResponse.getRsltCd()); //

					if (!success) {
						commonAuth.insertProcessLog(loggingId, //
								"뷰티포인트 등록 API", //
								customerApiService.getClass().getCanonicalName(), //
								"createBpUser", //
								"C", //
								userData.toString(), // StringUtil.printJson(createCustVo);
								createResponse.toString(), // StringUtil.printJson(custResponse);
								createResponse.getRsltCd(), //
								createResponse.getRsltMsg());
					}
					
				} else {

					if (success) {
						response.setResultCode(ResultCode.SUCCESS.getCode());
					} else {
						response.setResultCode(createResponse.getRsltCd());
						response.setMessage(createResponse.getRsltMsg());
					}
					
					commonAuth.insertProcessLog(loggingId, //
							"뷰티포인트 조회 API", //
							customerApiService.getClass().getCanonicalName(), //
							"checkBpOnlineId", //
							"S", //
							searchUserData.toString(), // StringUtil.printJson(createCustVo);
							createResponse.toString(), // StringUtil.printJson(custResponse);
							createResponse.getRsltCd(), //
							createResponse.getRsltMsg());
				}

				if (success) {
					this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
					response.setResultCode(ResultCode.SUCCESS.getCode());
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_REGIST_BEAUTY_POINT_CALL_API_FAIL, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				log.error(e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				response.setMessage(ResultCode.SYSTEM_ERROR.message());
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				commonAuth.insertProcessLog(loggingId, //
						"뷰티포인트 API", //
						this.getClass().getCanonicalName(), //
						"registBeautyPointCustomerCallApi", //
						"F", //
						joinRequest.toString(), // StringUtil.printJson(createCustVo);
						e.getMessage(), // StringUtil.printJson(custResponse);
						response.getResultCode(), //
						response.getMessage());
			}
			log.debug(OmniUtil.getApiResultCode(response, "JoinPhoneRegistProcess.registBeautyPointCustomerCallApi"));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			return response;
		}

		// 고객통합 오프라인 경로 등록 API 호출
		public BaseResponse registIntegrateOfflineChannelCustomer(final Map<String, Object> request) {

			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			if ("099".equals(joinRequest.getChcd())) { // 에스트라는 오프라인 없음.
				BaseResponse response = new BaseResponse();
				response.setResultCode(ResultCode.SUCCESS.getCode());
				return response;
			}
			return registIntegrateChannelCustomer(OnOffline.Offline, request);
		}

		// 고객통합 오프라인 경로 취소 API 호출
		public BaseResponse cancelIntegrateOfflineChannelCustomer(Map<String, Object> request) {
			return cancelIntegrateChannelCustomer(OnOffline.Offline, request);
		}

		// 고객통합 온라인 경로 등록 API 호출
		public BaseResponse registIntegrateOnlineChannelCustomer(final Map<String, Object> request) {
			return registIntegrateChannelCustomer(OnOffline.Online, request);
		}

		// 고객통합 온라인 경로 취소 API 호출
		public BaseResponse cancelIntegrateOnlineChannelCustomer(Map<String, Object> request) {
			return cancelIntegrateChannelCustomer(OnOffline.Online, request);
		}

		// 고객통합 온라인/오프라인 등록 API
		public BaseResponse registIntegrateChannelCustomer(final OnOffline onoffline, final Map<String, Object> request) {
			BaseResponse response = new BaseResponse();
			
			// 외부 제휴몰에서 뷰티멤버십 연동을 통한 회원 가입 시 채널 등록은 후순위로 수행
			final String isMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_MEMBERSHIP);
			boolean isMembership = OmniUtil.isMembership(isMembershipSession);
			if(isMembership) {
				response.setMessage(ResultCode.SUCCESS.message());
				response.setResultCode(ResultCode.SUCCESS.getCode());
				
				return response;
			}

			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			
			log.debug("registIntegrateChannelCustomer {}", StringUtil.printJson(joinRequest));
			String loggingId = OmniUtil.getLoggingId(request);
			final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
			final String orgChannelCd = joinRequest.getChcd();
			// 온라인 가입 시 오프라인것도 가입해주기 위해서 경로코드 변경처리
			Channel channel = null;
			if (onoffline == OnOffline.Offline) {
				String offlineChannelCd = ChannelPairs.getOfflineCd(joinRequest.getChcd());

				if (StringUtils.isEmpty(offlineChannelCd)) {
					log.debug("▶▶▶▶▶▶ ③ integrated channel {} is empty channel, skip process", onoffline.name());
					response.setResultCode(ResultCode.SUCCESS.getCode());
					if (stopwatch.isRunning()) {
						stopwatch.stop();
					}
					return response;
				}
				channel = commonService.getChannel(offlineChannelCd);
				if (!stopwatch.isRunning()) {
					stopwatch.start("registIntegrateChannelCustomer " + onoffline.name() + " " + channel.getChCdNm() + "(" + channel.getChCd() + ")");
				}
				joinRequest.setChcd(offlineChannelCd);
			} else {
				channel = commonService.getChannel(joinRequest.getChcd());
				if (!stopwatch.isRunning()) {
					stopwatch.start("registIntegrateChannelCustomer " + onoffline.name() + " " + channel.getChCdNm() + "(" + channel.getChCd() + ")");
				}
			}

			try {
				// 수신 동의 처리 시 해당 경로시스템의 수신 동의 여부가 없을 경우 N 으로 처리
				/*
				 * List<Marketing> joinMarketings = joinRequest.getMarketings(); if (joinMarketings == null || joinMarketings.isEmpty()) { if
				 * (StringUtils.hasText(orgChannelCd)) { joinRequest.setChcd(orgChannelCd); } this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				 * response.setResultCode(ResultCode.SUCCESS.getCode()); if (stopwatch.isRunning()) { stopwatch.stop(); } return response; }
				 */

				if (StringUtils.isEmpty(joinRequest.getChcd())) {
					joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
				}

				CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedOnlineChannelCustomerData(onoffline, channel, joinRequest);
				log.debug("▷▷▷▷▷▷ ③ integrated channel {} customer api data : {}", onoffline.name(), StringUtil.printJson(chCustRequest));
				CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
				log.debug("▶▶▶▶▶▶ ③ integrated channel {} customer api response : {}", onoffline.name(), StringUtil.printJson(chjoinResponse));

				// 경로 고객 존재하는 경우도 성공으로 판단
				boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse.getRsltCd());

				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				log.info("▶▶▶▶▶▶ ③ integrated channel result : {} ---> {}", chjoinResponse.getRsltCd(), success);
				
				if (onoffline == OnOffline.Offline) {
					success = true; // 온라인 가입 시 오프라인 처리인 경우 무조건 성공처리해야 온라인처리가 오프라인과 관계없이 처리 가능함.
					log.info("▶▶▶▶▶▶ ③ integrated channel result : {} ---> {}", "온라인 가입 시 오프라인 처리인 경우 성공처리 ---> 온라인처리 정상처리 가능", success);
				}
				
				if (StringUtils.hasText(orgChannelCd)) {
					joinRequest.setChcd(orgChannelCd);
				}
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				if (success) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
				} else {
					response.setResultCode(chjoinResponse.getRsltCd());
					response.setMessage(chjoinResponse.getRsltMsg());
					commonAuth.insertProcessLog(loggingId, //
							String.format("고객통합 %s 경로 등록 API", onoffline.name()), //
							customerApiService.getClass().getCanonicalName(), //
							"createCustChannelMember", //
							"C", //
							chCustRequest.toString(), // StringUtil.printJson(createCustVo);
							chjoinResponse.toString(), // StringUtil.printJson(custResponse);
							chjoinResponse.getRsltCd(), //
							chjoinResponse.getRsltMsg());
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_REGIST_INTEGRATED_CHANNEL_CUSTOMER_CALL_API_FAIL, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				log.error(e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				response.setMessage(ResultCode.SYSTEM_ERROR.message());
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				commonAuth.insertProcessLog(loggingId, //
						String.format("고객통합 %s 경로 등록 API", onoffline.name()), //
						this.getClass().getCanonicalName(), //
						"registIntegrateChannelCustomer", //
						"F", //
						joinRequest.toString(), // StringUtil.printJson(createCustVo);
						e.getMessage(), // StringUtil.printJson(custResponse);
						response.getResultCode(), //
						response.getMessage());
			}
			log.debug(OmniUtil.getApiResultCode(response, "JoinPhoneRegistProcess.registIntegrateChannelCustomer"));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			return response;
		}

		// 고객통합 온라인/오프라인 경로 취소 API
		public BaseResponse cancelIntegrateChannelCustomer(final OnOffline onoffline, final Map<String, Object> request) {
			BaseResponse response = new BaseResponse();

			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			String loggingId = OmniUtil.getLoggingId(request);
			final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
			final String orgChannelCd = joinRequest.getChcd();
			// 온라인 가입 시 오프라인것도 가입해주기 위해서 경로코드 변경처리
			Channel channel = null;
			if (onoffline == OnOffline.Offline) {
				String offlineChannelCd = ChannelPairs.getOfflineCd(joinRequest.getChcd());

				if (StringUtils.isEmpty(offlineChannelCd)) {
					log.debug("▶▶▶▶▶▶ ③ integrated channel {} is empty channel, skip process", onoffline.name());
					response.setResultCode(ResultCode.SUCCESS.getCode());
					if (stopwatch.isRunning()) {
						stopwatch.stop();
					}
					return response;
				}
				channel = commonService.getChannel(offlineChannelCd);
				if (!stopwatch.isRunning()) {
					stopwatch.start("cancelIntegrateChannelCustomer " + onoffline.name() + " " + channel.getChCdNm() + "(" + channel.getChCd() + ")");
				}
				joinRequest.setChcd(offlineChannelCd);
			} else {
				channel = commonService.getChannel(joinRequest.getChcd());
				if (!stopwatch.isRunning()) {
					stopwatch.start("cancelIntegrateChannelCustomer " + onoffline.name() + " " + channel.getChCdNm() + "(" + channel.getChCd() + ")");
				}
			}
			
			// TODO 채널 탈퇴사용자의 경우 여기서 탈퇴정보 돌려주기
			if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(joinRequest.getWithdrawCode()) //
					&& joinRequest.isWithdraw() //
					&& StringUtils.hasText(joinRequest.getWithdrawDate()) //
					&& DateUtil.isValidDateFormat(joinRequest.getWithdrawDate())) {

				response.setResultCode(ResultCode.CHANNEL_WITHDRAW.getCode());
				response.setMessage(joinRequest.getWithdrawDate());
				log.debug("▶▶▶▶▶▶ ③ integrated channel {} customer api withdraw user", onoffline.name(), StringUtil.printJson(response));
				if (stopwatch.isRunning()) {
					stopwatch.stop();
				}
				return response;
			}			

			try {
				CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedOnlineChannelCustomerData(onoffline, channel, joinRequest);

				chCustRequest.setJoinCnclYn("Y"); // 망취소 처리

				log.debug("▶▶▶▶▶▶ ③ integrated channel {} customer api data : {}", onoffline.name(), StringUtil.printJson(chCustRequest));

				CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
				log.debug("▶▶▶▶▶▶ ③ integrated channel {} customer api response : {}", onoffline.name(), StringUtil.printJson(chjoinResponse));

				boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVCOM009".equals(chjoinResponse.getRsltCd());
				if (StringUtils.hasText(orgChannelCd)) {
					joinRequest.setChcd(orgChannelCd);
				}
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				if (success) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
				} else {
					response.setResultCode(chjoinResponse.getRsltCd());
					response.setMessage(chjoinResponse.getRsltMsg());
					
					commonAuth.insertProcessLog(loggingId, //
							String.format("고객통합 %s 경로 취소 API", onoffline.name()), //
							customerApiService.getClass().getCanonicalName(), //
							"createCustChannelMember", //
							"D", //
							chCustRequest.toString(), // StringUtil.printJson(createCustVo);
							chjoinResponse.toString(), // StringUtil.printJson(custResponse);
							chjoinResponse.getRsltCd(), //
							chjoinResponse.getRsltMsg());
					
					if (onoffline == OnOffline.Offline) {
						LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
						log.info("▶▶▶▶▶▶ ③ integrated channel original result : {} ---> {}", onoffline, StringUtil.printJson(response));
						success = true; // 온라인 가입 시 오프라인 처리인 경우 무조건 성공처리해야 온라인처리가 오프라인과 관계없이 처리 가능함.
						response.SetResponseInfo(ResultCode.SUCCESS);
						log.info("▶▶▶▶▶▶ ③ integrated channel result : {} ---> {}", "온라인 가입 시 오프라인 처리인 경우 성공처리 ---> 온라인처리 정상처리 가능", success);
					}					
					
				}
				
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_CANCEL_INTEGRATED_CHANNEL_CUSTOMER_CALL_API_FAIL, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				log.error(e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				response.setMessage(ResultCode.SYSTEM_ERROR.message());
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				commonAuth.insertProcessLog(loggingId, //
						String.format("고객통합 %s 경로 취소 API", onoffline.name()), //
						this.getClass().getCanonicalName(), //
						"cancelIntegrateChannelCustomer", //
						"F", //
						joinRequest.toString(), // StringUtil.printJson(createCustVo);
						e.getMessage(), // StringUtil.printJson(custResponse);
						response.getResultCode(), //
						response.getMessage());
			}
			log.debug(OmniUtil.getApiResultCode(response, "JoinPhoneRegistProcess.cancelIntegrateChannelCustomer"));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			return response;
		}

		// 온라인 경로 등록 API 호출
		public BaseResponse registOnlineChannelCustomer(Map<String, Object> request) {
			return registChannelCustomer(OnOffline.Online, request);
		}

		// 오프라인 경로 등록 API 호출
		public BaseResponse registOfflineChannelCustomer(Map<String, Object> request) {
			return registChannelCustomer(OnOffline.Offline, request);

		}

		// 채널 온라인/오프라인 등록 API
		public BaseResponse registChannelCustomer(final OnOffline onoffline, final Map<String, Object> request) {
			BaseResponse response = new BaseResponse();

			CreateCustResponse custResponse = (CreateCustResponse) request.get(OmniConstants.PROCESS_RESPONSE);
			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			String loggingId = OmniUtil.getLoggingId(request);
			final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
			final String orgChannelCd = joinRequest.getChcd();
			// 온라인 가입 시 오프라인것도 가입해주기 위해서 경로코드 변경처리
			Channel channel = null;
			if (onoffline == OnOffline.Offline) {
				String offlineChannelCd = ChannelPairs.getOfflineCd(joinRequest.getChcd());

				if (StringUtils.isEmpty(offlineChannelCd)) {
					log.debug("▶▶▶▶▶▶ ④ integrated channel {} is empty channel, skip process", onoffline.name());
					response.setResultCode(ResultCode.SUCCESS.getCode());
					if (stopwatch.isRunning()) {
						stopwatch.stop();
					}
					return response;
				}
				channel = commonService.getChannel(offlineChannelCd);
				if (!stopwatch.isRunning()) {
					stopwatch.start("registChannelCustomer " + onoffline.name() + " " + channel.getChCdNm() + "(" + channel.getChCd() + ")");
				}
				joinRequest.setChcd(offlineChannelCd);
			} else {
				channel = commonService.getChannel(joinRequest.getChcd());
				if (!stopwatch.isRunning()) {
					stopwatch.start("registChannelCustomer " + onoffline.name() + " " + channel.getChCdNm() + "(" + channel.getChCd() + ")");
				}
			}

			try {
				// 전사약관인 경우는 처리하지 않음.
				boolean skipProcess = joinRequest.isCorpTerms(); // false;

				if (skipProcess) { // 전사약관은 처리하지 않고 바로 성공으로
					if (StringUtils.hasText(orgChannelCd)) {
						joinRequest.setChcd(orgChannelCd);
					}
					this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
					log.debug("▶▶▶▶▶▶ ④ channel customer {} api request : {}", onoffline.name(), "skip channel customer regist, corp terms!!!");
					response.setResultCode(ResultCode.SUCCESS.getCode());
					if (stopwatch.isRunning()) {
						stopwatch.stop();
					}
					return response;
				}

				if (StringUtils.isEmpty(joinRequest.getChcd())) {
					joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
				}

				log.debug("▶▶▶▶▶▶ ④ channel customer {} api request : {}", onoffline.name(), StringUtil.printJson(joinRequest));

				// 경로 회원 검색 처리
				String chWebId = WebUtil.getStringSession("chcsWebId"); // WebUtil.getStringSession(OmniConstants.XID_SESSION);
				String chLoginId = mgmtService.searchChannelWebId( //
						joinRequest.getTrnsType(), //
						joinRequest.getChcd(), //
						joinRequest.getIncsno(), //
						chWebId);

				log.debug("▶▶▶▶▶▶ ④ channel trans customer : {} --> trns type :  {}, chloginid : {}", joinRequest.isTransCustomer(), joinRequest.getTrnsType(), chLoginId);

				if (StringUtils.isEmpty(joinRequest.getLoginid())) {
					List<UmChUser> chUsers = mgmtService.getChannelUserList(Integer.parseInt(joinRequest.getIncsno()), joinRequest.getChcd());
					if (chUsers != null && chUsers.size() > 0) {
						UmChUser chUser = chUsers.get(0);
						if (chUser != null && StringUtils.hasText(chUser.getChcsWebId())) {
							joinRequest.setLoginid(chUser.getChcsWebId());
						}
					}
				}

				if (StringUtils.isEmpty(joinRequest.getLoginid())) {
					String loginid = WebUtil.getStringSession(OmniConstants.XID_SESSION);
					if (StringUtils.hasText(loginid)) {
						joinRequest.setLoginid(SecurityUtil.getXValue(loginid, false));
					}
				}

				CreateChCustRequest createChCustRequest = new CreateChCustRequest();
				createChCustRequest.setIncsNo(joinRequest.getIncsno());

				ChUserVo chUserVo = new ChUserVo();
				chUserVo.setIncsNo(joinRequest.getIncsno());

				if (StringUtils.hasText(joinRequest.getLoginid())) {
					chUserVo.setWebId(joinRequest.getLoginid());
				}

				chUserVo.setName(joinRequest.getUnm());
				chUserVo.setBirth(joinRequest.getBirth());
				// 이니스프리 : 전화번호는 - 으로 3개가 분리
				if (joinRequest.getChcd().equals("036")) {
					chUserVo.setPhone(StringUtil.getNationalMobile(joinRequest.getPhone()));
				} else {
					chUserVo.setPhone(joinRequest.getPhone());
				}

				if (joinRequest.getGender().equals("1")) {
					chUserVo.setGender("M");
				} else if (joinRequest.getGender().equals("0")) {
					chUserVo.setGender("F");
				} else {
					chUserVo.setGender(joinRequest.getGender());
				}
				chUserVo.setForeigner(joinRequest.getNational());
				chUserVo.setCi(joinRequest.getCi());

				if (custResponse != null && StringUtils.hasText(custResponse.getIncsCardNo())) {
					chUserVo.setIncsCardNoEc(custResponse.getIncsCardNo());
				} else {
					CustInfoVo custInfoVo = new CustInfoVo();
					custInfoVo.setIncsNo(joinRequest.getIncsno());
					Customer customer = customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
					if (customer != null) {
						chUserVo.setIncsCardNoEc(customer.getIncsCardNoEc());
					}
				}

				if (onoffline == OnOffline.Offline) {
					if (StringUtils.isEmpty(joinRequest.getJoinPrtnId())) {
						if (StringUtils.hasText(config.getJoinPrtnCode(joinRequest.getChcd()))) {
							chUserVo.setJoinPrtnId(config.getJoinPrtnCode(joinRequest.getChcd())); // 필수, 최초접촉거래처ID
						} else {
							chUserVo.setJoinPrtnId(""); // 필수, 최초접촉거래처ID
						}
					} else {
						chUserVo.setJoinPrtnId(joinRequest.getJoinPrtnId());
					}
				} else {
					if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
						chUserVo.setJoinPrtnId(joinRequest.getJoinPrtnId());
					} else {
						chUserVo.setJoinPrtnId(config.getJoinPrtnCode(joinRequest.getChcd()));
					}
				}
				
				if (StringUtils.isEmpty(joinRequest.getJoinEmpId())) { // 2021-05-03 이크리스 추가 파라미터
					if (StringUtils.hasText(config.getJoinEmpCode(joinRequest.getChcd()))) {
						chUserVo.setJoinEmpId(config.getJoinEmpCode(joinRequest.getChcd()));
					} else {
						chUserVo.setJoinEmpId("");
					}
				} else {
					chUserVo.setJoinEmpId(joinRequest.getJoinEmpId());
				}

				chUserVo.setEmailConsent("N");

				List<Terms> terms = joinRequest.getTerms();
				if (terms != null && !terms.isEmpty()) {
					List<ChTermsVo> chTerms = new ArrayList<>();
					for (Terms term : terms) {
						ChTermsVo chterm = new ChTermsVo();
						chterm.setIncsNo(joinRequest.getIncsno());
						chterm.setTcatCd(term.getTcatCd());
						chterm.setTncaDttm(term.getTncaDttm() == null ? DateUtil.getCurrentDateTime() : term.getTncaDttm());
						chterm.setTncAgrYn(term.getTncAgrYn());
						chterm.setTncvNo(term.getTncvNo());
						chTerms.add(chterm);
					}
					createChCustRequest.setTerms(chTerms);
				} else {
					createChCustRequest.setTerms(Collections.emptyList());
				}

				List<Marketing> joinMarketings = joinRequest.getMarketings();
				if (joinMarketings != null && !joinMarketings.isEmpty()) {
					for (Marketing joinMarketing : joinMarketings) {
						chUserVo.setSmsConsent(joinMarketing.getSmsAgree() == null ? "N" : joinMarketing.getSmsAgree()); // SMS수신동의여부
					}
				}

				if (StringUtils.isEmpty(chUserVo.getSmsConsent())) {
					chUserVo.setSmsConsent("N");
				}
				chUserVo.setPostConsent("N");
				chUserVo.setTmConsent("N");
				chUserVo.setJoinRoute("W");

				// 자체회원아이디
				if (StringUtils.hasText(chLoginId)) {
					chUserVo.setChLoginId(chLoginId);
				} else {
					chUserVo.setChLoginId("");
				}

				chUserVo.setEmail("");

				createChCustRequest.setUser(chUserVo);
				createChCustRequest.setChCd(joinRequest.getChcd());

				log.debug("▶▶▶▶▶▶ ④ channel customer create user data : {}", StringUtil.printJson(createChCustRequest));
				ApiBaseResponse custApiResponse = customerApiService.createChannelUser(joinRequest.getChcd(), createChCustRequest, joinRequest);
				log.debug("▶▶▶▶▶▶ ④ channel customer create user response : {}", StringUtil.printJson(custApiResponse));

				if (StringUtils.hasText(orgChannelCd)) {
					joinRequest.setChcd(orgChannelCd);
				}
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				if (ResultCode.SUCCESS.getCode().equals(custApiResponse.getResultCode())) {
					response.SetResponseInfo(ResultCode.SUCCESS);
				} else {
					response.setResultCode(custApiResponse.getResultCode());
					response.setMessage(custApiResponse.getMessage());
					
					commonAuth.insertProcessLog(loggingId, //
							String.format("채널 %s 등록 API", onoffline.name()), //
							customerApiService.getClass().getCanonicalName(), //
							"createChannelUser", //
							"C", //
							createChCustRequest.toString(), // StringUtil.printJson(createCustVo);
							custApiResponse.toString(), // StringUtil.printJson(custResponse);
							custApiResponse.getResultCode(), //
							custApiResponse.getMessage());
					
					// TODO 채널 탈퇴사용자의 경우 여기서 탈퇴정보 전달하기
					if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(custApiResponse.getResultCode())) {
						if (DateUtil.isValidDateFormat(custApiResponse.getMessage())) {
							joinRequest.setWithdraw(true);
							joinRequest.setWithdrawCode(ResultCode.CHANNEL_WITHDRAW.getCode());
							joinRequest.setWithdrawDate(custApiResponse.getMessage());
							joinRequest.setChcd(orgChannelCd);
							this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
							if (stopwatch.isRunning()) {
								stopwatch.stop();
							}
							return response;
						}
						
						LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
						log.info("▶▶▶▶▶▶ ④ channel customer result : {}", custApiResponse.getResultCode());
						
					}
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_REGIST_CHANNEL_CUSTOMER_CALL_API_FAIL, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				log.error(e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				response.setMessage(ResultCode.SYSTEM_ERROR.message());
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				commonAuth.insertProcessLog(loggingId, //
						String.format("채널 %s 등록 API", onoffline.name()), //
						this.getClass().getCanonicalName(), //
						"registChannelCustomer", //
						"F", //
						joinRequest.toString(), // StringUtil.printJson(createCustVo);
						e.getMessage(), // StringUtil.printJson(custResponse);
						response.getResultCode(), //
						response.getMessage());
			}
			log.debug(OmniUtil.getApiResultCode(response, "JoinPhoneRegistProcess.registChannelCustomer"));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			return response;
		}

		public BaseResponse registOmniCustomer(final Map<String, Object> request) {
			BaseResponse response = new BaseResponse();
			final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
			if (!stopwatch.isRunning()) {
				stopwatch.start("registOmniCustomer");
			}
			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			String loggingId = OmniUtil.getLoggingId(request);
			try {
				final CreateUserData joinUserData = new CreateUserData();
				joinUserData.setIncsNo(Integer.parseInt(joinRequest.getIncsno())); // custResponse.getIncsNo()
				joinUserData.setLoginId(joinRequest.getLoginid());
				joinUserData.setPassword(joinRequest.getLoginpassword());
				joinUserData.setJoinFlag(OmniConstants.OMNI_JOIN_FLAG);
				joinUserData.setCn(joinRequest.getUnm());

				ApiBaseResponse createResponse = customerApiService.createUser(joinUserData); // 옴니회원등록

				log.debug("▶▶▶▶▶▶ ⑤ omni wso2 customer regist api response : {}", StringUtil.printJson(createResponse));

				boolean success = ResultCode.SUCCESS.getCode().equals(createResponse.getResultCode()) || ResultCode.USER_ALREADY_EXIST.getCode().equals(createResponse.getResultCode());

				if (!ResultCode.SUCCESS.getCode().equals(createResponse.getResultCode())) {
					commonAuth.insertProcessLog(loggingId, //
							"옴니 뷰티포인트 등록 API", //
							customerApiService.getClass().getCanonicalName(), //
							"createUser", //
							"C", //
							joinUserData.toString(), // StringUtil.printJson(createCustVo);
							createResponse.toString(), // StringUtil.printJson(custResponse);
							createResponse.getResultCode(), //
							createResponse.getMessage());
				}
				
				// 8. 약관동의 처리
				if (success) {
					if (joinRequest.getTerms() != null && !joinRequest.getTerms().isEmpty()) {
						List<Terms> joinTerms = joinRequest.getTerms();
						for (Terms term : joinTerms) {
							final String onlineChCd = ChannelPairs.getOnlineCd(joinRequest.getChcd());
							term.setChgChCd(onlineChCd);
							term.setIncsNo(Integer.parseInt(joinRequest.getIncsno())); // 고객통합 플랫폼 처리 결과 통합고객번호로 세팅하기
							term.setTncAgrYn(term.getTncAgrYn().equals("Y") ? "A" : "D");
							term.setTncaChgDt(DateUtil.getCurrentDate());
							if (termsService.existTerms(term)) {
								termsService.mergeTerms(term);
								termsService.insertTermHist(term);
							}
						}
					}
				} else {
					success = ResultCode.USER_ALREADY_EXIST.getCode().equals(createResponse.getResultCode());
				}

				log.debug("▶▶▶▶▶▶ ⑤ omni trans flag : {} --> trns type :  {}", joinRequest.isTransCustomer(), joinRequest.getTrnsType());

				// 전환가입자이면 전환플래그 처리
				String incsNo = joinRequest.getIncsno();
				if (StringUtils.hasText(incsNo) && !"0".equals(incsNo)) {
					List<UmChUser> chUsers = mgmtService.getChannelConversionUserList(joinRequest.getChcd(), incsNo);
					if (chUsers == null || chUsers.isEmpty()) {

						final String encChloginid = WebUtil.getStringSession(OmniConstants.XID_SESSION);
						final String chLoginid = SecurityUtil.getXValue(encChloginid, false);

						log.debug("▶▶▶▶▶▶ ⑤ channel customer 통합고객번호가 없는 경로 자체 고객 {}", chLoginid);

						UmChUser umChUser = new UmChUser();
						umChUser.setChCd(joinRequest.getChcd());
						umChUser.setChcsWebId(chLoginid);
						umChUser.setIncsNo(Integer.parseInt(incsNo));
						boolean convs = mgmtService.updateConversionCompleteById(umChUser);
						if (!convs) {
							commonAuth.insertProcessLog(loggingId, //
									String.format("전환 처리 API, 웹아이디(%s), 통합고객번호(%s)", chLoginid, incsNo), //
									mgmtService.getClass().getCanonicalName(), //
									"updateConversionCompleteById", //
									"U", //
									umChUser.toString(), // StringUtil.printJson(createCustVo);
									null);// StringUtil.printJson(custResponse);

						}

					} else {

						log.debug("▶▶▶▶▶▶ ⑤ channel customer 통합고객번호가 있는 경로 고객");

						UmChUser umChUser = new UmChUser();
						umChUser.setChCd(joinRequest.getChcd());
						umChUser.setIncsNo(Integer.parseInt(incsNo));
						boolean convs = mgmtService.updateConversionComplete(umChUser);
						if (!convs) {
							commonAuth.insertProcessLog(loggingId, //
									String.format("전환 처리 API, 통합고객번호(%s)", incsNo), //
									mgmtService.getClass().getCanonicalName(), //
									"updateConversionComplete", //
									"U", //
									umChUser.toString(), // StringUtil.printJson(createCustVo);
									null); // StringUtil.printJson(custResponse);

						}
					}
				}

				if (success) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
				} else {
					response.setResultCode(createResponse.getResultCode());
					response.setMessage(createResponse.getMessage());
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_REGIST_BEAUTY_POINT_CALL_API_FAIL, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				log.error(e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				response.setMessage(ResultCode.SYSTEM_ERROR.message());
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				commonAuth.insertProcessLog(loggingId, //
						"옴니 뷰티포인트 등록 API", //
						customerApiService.getClass().getCanonicalName(), //
						"createUser", //
						"C", //
						joinRequest.toString(), // StringUtil.printJson(createCustVo);
						e.getMessage(), // StringUtil.printJson(custResponse);
						response.getResultCode(), //
						response.getMessage());
			}
			log.debug(OmniUtil.getApiResultCode(response, "JoinPhoneRegistProcess.registOmniCustomer"));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			return response;
		}
	}

	public BaseResponse joinBpRegistProcess(final JoinRequest joinRequest) {
		final StopWatch stopwatch = new StopWatch("joinBpRegistProcess");
		Map<String, Object> object = new HashMap<>();

		object.put(OmniConstants.PROCESS_REQUEST, joinRequest);
		object.put(OmniConstants.PROCESS_STOPWATCH, stopwatch);
		object.put(OmniConstants.PROCESS_LOGGING_ID, UuidUtil.getIdByDate("P"));

		JoinBpRegistProcess crp = new JoinBpRegistProcess(object);

		// 1. 고객통합 등록 API 성공 --> 뷰포 등록 API, 고객통합 등록 API 실패 --> 종료
		BaseResponse success = crp.joinBpRegistStep(crp::updateIntegratedCustomerCallApi, null);

		// 2. 뷰포 API , 실패 --> 고객통합 취소 API
		if (ResultCode.SUCCESS.getCode().equals(success.getResultCode())) { // 고객통합 등록 API 성공 --> 뷰포 등록 API
			// 고객통합 등록 API 성공 -------------> [ 뷰포 등록 API ], 실패 -------------> [ 고객통합 취소 API ]
			success = crp.joinBpRegistStep(crp::registBeautyPointCustomerCallApi, crp::cancelIntegratedCustomerCallApi);
		}

		// 7. 옴니 WSO2 사용자 등록
		if (ResultCode.SUCCESS.getCode().equals(success.getResultCode())) {
			success = crp.joinBpRegistStep(crp::registOmniCustomer, null);
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		log.info("\n" + stopwatch.prettyPrint());
		commonAuth.insertTaskLog(object, stopwatch);
		return success;
	}

	private class JoinBpRegistProcess {

		private Map<String, Object> request;

		private JoinBpRegistProcess(final Map<String, Object> request) {
			this.request = request;
		}

		public BaseResponse joinBpRegistStep(Function<Map<String, Object>, BaseResponse> func, Function<Map<String, Object>, BaseResponse> failed) {
			BaseResponse success = func.apply(request);
			if (!ResultCode.SUCCESS.getCode().equals(success.getResultCode()) && failed != null) {
				return failed.apply(request);
			}
			return success;
		}

		// 고객통합 수정 API 호출
		public BaseResponse updateIntegratedCustomerCallApi(final Map<String, Object> request) {
			BaseResponse response = new BaseResponse();
			ApiResponse apiResponse = null;
			final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
			if (!stopwatch.isRunning()) {
				stopwatch.start("updateIntegratedCustomerCallApi");
			}
			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			String loggingId = OmniUtil.getLoggingId(request);

			try {
				boolean success = true;

				CustInfoVo custInfoVo = new CustInfoVo();
				// 통합고객번호 있을경우 회원 여부 체크
				if (StringUtils.hasText(joinRequest.getIncsno())) {
					custInfoVo.setIncsNo(joinRequest.getIncsno());

					// 통합고객번호로 사용자를 다시 찾기
					Customer customerincsno = customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
					if (customerincsno != null && "ICITSVCOM000".equals(customerincsno.getRsltCd())) {

						if (customerincsno != null && StringUtils.hasText(customerincsno.getCiNo())) {
							custInfoVo.setCiNo(customerincsno.getCiNo());
						} else {
							log.debug("① conversion customer dormancy ? {}", customerincsno.getDrccCd());
							if ("Y".equals(customerincsno.getDrccCd())) { // 휴면인 경우 복원 필요
								String name = joinService.releaseDormancyCustomerName(customerincsno.getIncsNo(), joinRequest.getChcd());
								if (StringUtils.hasText(name)) {
									joinRequest.setUnm(name);
								}
							}
						}
					} else {
						response.setResultCode(customerincsno.getRsltCd());
						if (stopwatch.isRunning()) {
							stopwatch.stop();
						}
						return response;
					}

					CustInfoResponse custinfoResponse = customerApiService.getCustList(custInfoVo);
					log.debug("▶▶▶▶▶▶ ① conversion customer exist check response : {}", StringUtil.printJson(custinfoResponse));
					if (custinfoResponse.getRsltCd().equals("ICITSVCOM000")) { // 있으면 수정 프로세스
						response.setResultCode(custinfoResponse.getRsltCd());
					} else {
						log.debug("▶▶▶▶▶▶ ① conversion customer check : {}, {}", custinfoResponse.getRsltCd(), custinfoResponse.getRsltMsg());
						response.setResultCode(custinfoResponse.getRsltCd());
						if (stopwatch.isRunning()) {
							stopwatch.stop();
						}
						return response;
					}
					Customer customers[] = custinfoResponse.getCicuemCuInfTcVo();
					if (customers != null && customers.length > 0) {
						Customer customer = customers[0]; // 첫번째 데이터가 최신임.
						//if (StringUtils.hasText(customer.getJoinPrtnId())) {
						//	joinRequest.setJoinPrtnId(customer.getJoinPrtnId());
						//}
						//if ("Y".equalsIgnoreCase(customer.getCustWtYn())) { // 탈퇴사용자
						if (StringUtils.hasText(customer.getCustWtDttm())) {
							log.debug("▶▶▶▶▶▶ ① conversion customer check withdraw : {}[{}]", customer.getCustWtYn(), customer.getCustWtDttm());
							response.setResultCode("ICITSVCOM001"); // ICITSVCOM002
							if (stopwatch.isRunning()) {
								stopwatch.stop();
							}
							return response;
						}
						
						// 존재하더라도 본인인증 정보가 불일치 할 경우 등록 프로세스(회원가입 단계에서 신규 가입인 경우)
						CertResult certResult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
						if(certResult != null && "5".equals(joinRequest.getJoinType()) &&
								(!certResult.getPhone().equals(StringUtil.mergeMobile(customer)) || !certResult.getCiNo().equals(customer.getCiNo()) || !certResult.getBirth().equals(customer.getAthtDtbr()) || !certResult.getName().equals(customer.getCustNm()))) {
							response.setResultCode("ICITSVCOM001"); // ICITSVCOM002
							if (stopwatch.isRunning()) {
								stopwatch.stop();
							}
							return response;
						}

						if ("Y".equalsIgnoreCase(customer.getDrccCd())) {
							String name = joinService.releaseDormancyCustomerName(customerincsno.getIncsNo(), joinRequest.getChcd());
							if (StringUtils.hasText(name)) {
								joinRequest.setUnm(name);
							}
						}
					}

					// 경로 추가 API 호출 - 2022.12.29 추가
					BaseResponse baseResponse = registIntegrateChannelCustomer(OnOffline.Online, request);
					if(ResultCode.SUCCESS.getCode().equals(baseResponse.getResultCode())) { // 성공 시 약관 정보 입력
						// 중요) 경로 약관은 옴니에만 저장함.
						// 약관수정, 뷰티포인트 약관 동의
						List<Terms> joinBpTerms = joinRequest.getBpterms();
						if (joinBpTerms != null && !joinBpTerms.isEmpty()) {
							CustTncaRequest custTncaRequest = new CustTncaRequest();
							List<CustTncaVo> custTncaVos = new ArrayList<>();
							for (Terms joinTerm : joinBpTerms) {
								CustTncaVo terms = new CustTncaVo();
								terms.setTcatCd(joinTerm.getTcatCd());
								terms.setIncsNo(joinRequest.getIncsno());
								terms.setTncvNo(joinTerm.getTncvNo());
								terms.setTncAgrYn(joinTerm.getTncAgrYn());
								terms.setLschId("OCP");
								terms.setChgChCd(joinTerm.getChgChCd());
								terms.setChCd(joinRequest.getChcd());
								custTncaVos.add(terms);
							}
							CustTncaVo custTncaVoArr[] = custTncaVos.toArray(new CustTncaVo[custTncaVos.size()]);
							custTncaRequest.setCicuedCuTncaTcVo(custTncaVoArr);
							apiResponse = customerApiService.savecicuedcutnca(custTncaRequest);
							success &= "ICITSVCOM000".equals(apiResponse.getRsltCd());
							if (!success) {
								commonAuth.insertProcessLog(loggingId, //
										"고객통합 수정 API(약관동의)", //
										customerApiService.getClass().getCanonicalName(), //
										"savecicuedcutnca", //
										"U", //
										custTncaRequest.toString(), // StringUtil.printJson(createCustVo);
										apiResponse.toString(), // StringUtil.printJson(custResponse);
										apiResponse.getRsltCd(), //
										apiResponse.getRsltMsg());
							}
						}

						// 수신 동의 처리
						List<Marketing> joinMarketings = joinRequest.getMarketings();
						if (joinMarketings != null && !joinMarketings.isEmpty()) {
							CustMarketingRequest custMarketingRequest = new CustMarketingRequest();
							List<CustMarketingVo> custMarketingVos = new ArrayList<>();
							for (Marketing joinMarketing : joinMarketings) {
								CustMarketingVo marketing = new CustMarketingVo();
								// marketing.setChCd(joinMarketing.getChCd());
								marketing.setChCd(joinRequest.getChcd());
								marketing.setIncsNo(joinRequest.getIncsno());
								marketing.setEmlOptiYn("N");
								marketing.setSmsOptiYn(joinMarketing.getSmsAgree());
								marketing.setDmOptiYn("N");
								marketing.setTmOptiYn("N");
								marketing.setKkoIntlOptiYn("N");
								marketing.setFscrId("OCP");
								marketing.setLschId("OCP");
								custMarketingVos.add(marketing);
							}

							CustMarketingVo cicuemCuOptiTcVoArr[] = custMarketingVos.toArray(new CustMarketingVo[custMarketingVos.size()]);
							custMarketingRequest.setCicuemCuOptiTcVo(cicuemCuOptiTcVoArr);
							apiResponse = customerApiService.savecicuemcuoptilist(custMarketingRequest);
							success &= "ICITSVCOM000".equals(apiResponse.getRsltCd());
							if (!success) {
								commonAuth.insertProcessLog(loggingId, //
										"고객통합 수정 API(마케팅수신동의)", //
										customerApiService.getClass().getCanonicalName(), //
										"savecicuemcuoptilist", //
										"U", //
										custMarketingRequest.toString(), // StringUtil.printJson(createCustVo);
										apiResponse.toString(), // StringUtil.printJson(custResponse);
										apiResponse.getRsltCd(), //
										apiResponse.getRsltMsg());
							}
						}
					}
					
				}

				if (success) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
				} else {
					if (apiResponse != null) {
						response.setResultCode(apiResponse.getRsltCd());
						response.setMessage(apiResponse.getRsltMsg());
					} else {
						response.setResultCode("ICITSVCOM999");
					}
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정 
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_UPDATE_INTEGRATED_CUSTOMER_CALL_API_FAIL, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				log.error(e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				response.setMessage(ResultCode.SYSTEM_ERROR.message());
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				commonAuth.insertProcessLog(loggingId, //
						"고객통합 수정 API", //
						this.getClass().getCanonicalName(), //
						"updateIntegratedCustomerCallApi", //
						"F", //
						joinRequest.toString(), // StringUtil.printJson(createCustVo);
						e.getMessage(), // StringUtil.printJson(custResponse);
						response.getResultCode(), //
						response.getMessage());
			}
			log.debug(OmniUtil.getApiResultCode(response, "JoinBpRegistProcess.updateIntegratedCustomerCallApi"));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			return response;
		}
		
		// 고객통합 온라인/오프라인 등록 API
		public BaseResponse registIntegrateChannelCustomer(final OnOffline onoffline, final Map<String, Object> request) {
			BaseResponse response = new BaseResponse();
			
			// 외부 제휴몰에서 뷰티멤버십 연동을 통한 회원 가입 시 채널 등록은 후순위로 수행
			final String isMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_MEMBERSHIP);
			boolean isMembership = OmniUtil.isMembership(isMembershipSession);
			if(isMembership) {
				response.setMessage(ResultCode.SUCCESS.message());
				response.setResultCode(ResultCode.SUCCESS.getCode());
				
				return response;
			}

			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			
			log.debug("registIntegrateChannelCustomer {}", StringUtil.printJson(joinRequest));
			String loggingId = OmniUtil.getLoggingId(request);
			final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
			final String orgChannelCd = joinRequest.getChcd();
			// 온라인 가입 시 오프라인것도 가입해주기 위해서 경로코드 변경처리
			Channel channel = null;
			if (onoffline == OnOffline.Offline) {
				String offlineChannelCd = ChannelPairs.getOfflineCd(joinRequest.getChcd());

				if (StringUtils.isEmpty(offlineChannelCd)) {
					log.debug("▶▶▶▶▶▶ ③ integrated channel {} is empty channel, skip process", onoffline.name());
					response.setResultCode(ResultCode.SUCCESS.getCode());
					if (stopwatch.isRunning()) {
						stopwatch.stop();
					}
					return response;
				}
				channel = commonService.getChannel(offlineChannelCd);
				if (!stopwatch.isRunning()) {
					stopwatch.start("registIntegrateChannelCustomer " + onoffline.name() + " " + channel.getChCdNm() + "(" + channel.getChCd() + ")");
				}
				joinRequest.setChcd(offlineChannelCd);
			} else {
				channel = commonService.getChannel(joinRequest.getChcd());
				if (!stopwatch.isRunning()) {
					stopwatch.start("registIntegrateChannelCustomer " + onoffline.name() + " " + channel.getChCdNm() + "(" + channel.getChCd() + ")");
				}
			}

			try {
				// 수신 동의 처리 시 해당 경로시스템의 수신 동의 여부가 없을 경우 N 으로 처리
				/*
				 * List<Marketing> joinMarketings = joinRequest.getMarketings(); if (joinMarketings == null || joinMarketings.isEmpty()) { if
				 * (StringUtils.hasText(orgChannelCd)) { joinRequest.setChcd(orgChannelCd); } this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				 * response.setResultCode(ResultCode.SUCCESS.getCode()); if (stopwatch.isRunning()) { stopwatch.stop(); } return response; }
				 */

				if (StringUtils.isEmpty(joinRequest.getChcd())) {
					joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
				}

				CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedOnlineChannelCustomerData(onoffline, channel, joinRequest);
				log.debug("▷▷▷▷▷▷ ③ integrated channel {} customer api data : {}", onoffline.name(), StringUtil.printJson(chCustRequest));
				CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
				log.debug("▶▶▶▶▶▶ ③ integrated channel {} customer api response : {}", onoffline.name(), StringUtil.printJson(chjoinResponse));

				// 경로 고객 존재하는 경우도 성공으로 판단
				boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse.getRsltCd());

				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				log.info("▶▶▶▶▶▶ ③ integrated channel result : {} ---> {}", chjoinResponse.getRsltCd(), success);
				
				if (onoffline == OnOffline.Offline) {
					success = true; // 온라인 가입 시 오프라인 처리인 경우 무조건 성공처리해야 온라인처리가 오프라인과 관계없이 처리 가능함.
					log.info("▶▶▶▶▶▶ ③ integrated channel result : {} ---> {}", "온라인 가입 시 오프라인 처리인 경우 성공처리 ---> 온라인처리 정상처리 가능", success);
				}
				
				if (StringUtils.hasText(orgChannelCd)) {
					joinRequest.setChcd(orgChannelCd);
				}
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				if (success) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
				} else {
					response.setResultCode(chjoinResponse.getRsltCd());
					response.setMessage(chjoinResponse.getRsltMsg());
					commonAuth.insertProcessLog(loggingId, //
							String.format("고객통합 %s 경로 등록 API", onoffline.name()), //
							customerApiService.getClass().getCanonicalName(), //
							"createCustChannelMember", //
							"C", //
							chCustRequest.toString(), // StringUtil.printJson(createCustVo);
							chjoinResponse.toString(), // StringUtil.printJson(custResponse);
							chjoinResponse.getRsltCd(), //
							chjoinResponse.getRsltMsg());
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_REGIST_INTEGRATED_CHANNEL_CUSTOMER_CALL_API_FAIL, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				log.error(e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				response.setMessage(ResultCode.SYSTEM_ERROR.message());
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				commonAuth.insertProcessLog(loggingId, //
						String.format("고객통합 %s 경로 등록 API", onoffline.name()), //
						this.getClass().getCanonicalName(), //
						"registIntegrateChannelCustomer", //
						"F", //
						joinRequest.toString(), // StringUtil.printJson(createCustVo);
						e.getMessage(), // StringUtil.printJson(custResponse);
						response.getResultCode(), //
						response.getMessage());
			}
			log.debug(OmniUtil.getApiResultCode(response, "JoinPhoneRegistProcess.registIntegrateChannelCustomer"));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			return response;
		}

		// 고객통합 취소 API
		public BaseResponse cancelIntegratedCustomerCallApi(final Map<String, Object> request) {
			BaseResponse response = new BaseResponse();
			final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
			if (!stopwatch.isRunning()) {
				stopwatch.start("cancelIntegratedCustomerCallApi");
			}
			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			String loggingId = OmniUtil.getLoggingId(request);
			try {
				// 고객통합 입력 데이터 구성
				final Channel channel = commonService.getChannel(joinRequest.getChcd());
				CreateCustVO createCustVo = JoinData.buildIntegratedOnlineCreateCustomerData(joinRequest, channel);
				createCustVo.setJoinCnclYn("Y"); // 회원가입망취소시 'Y'
				CreateCustResponse custcreateResponse = customerApiService.createCust(createCustVo);

				log.debug("▶▶▶▶▶▶ ① integrated customer cancel api response : {}", StringUtil.printJson(custcreateResponse));

				// 고객통합 취소 API 호출 후 Process 종료되어야 함.
				boolean success = "ICITSVCOM000".equals(custcreateResponse.getRsltCd()) || "ICITSVCOM009".equals(custcreateResponse.getRsltCd());
				if (success) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
				} else {
					response.setResultCode(custcreateResponse.getRsltCd());
					response.setMessage(custcreateResponse.getRsltMsg());
					commonAuth.insertProcessLog(loggingId, //
							"고객통합 취소 API(망취소)", //
							customerApiService.getClass().getCanonicalName(), //
							"createCust", //
							"D", //
							createCustVo.toString(), // StringUtil.printJson(createCustVo);
							custcreateResponse.toString(), // StringUtil.printJson(custResponse);
							custcreateResponse.getRsltCd(), //
							custcreateResponse.getRsltMsg());
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_CANCEL_INTEGRATED_CUSTOMER_CALL_API_FAIL, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				log.error(e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				response.setMessage(ResultCode.SYSTEM_ERROR.message());
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				commonAuth.insertProcessLog(loggingId, //
						"고객통합 취소 API(망취소)", //
						this.getClass().getCanonicalName(), //
						"cancelIntegratedCustomerCallApi", //
						"F", //
						joinRequest.toString(), // StringUtil.printJson(createCustVo);
						e.getMessage(), // StringUtil.printJson(custResponse);
						response.getResultCode(), //
						response.getMessage());
			}
			log.debug(OmniUtil.getApiResultCode(response, "JoinBpRegistProcess.cancelIntegratedCustomerCallApi"));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			return response;
		}

		// 뷰포인트 등록 API
		// 오프라인인 경우 아이디 등록인 경우 처리하고 아닌 경우 스킵
		public BaseResponse registBeautyPointCustomerCallApi(final Map<String, Object> request) {
			BaseResponse response = new BaseResponse();
			final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
			if (!stopwatch.isRunning()) {
				stopwatch.start("registBeautyPointCustomerCallApi");
			}
			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			String loggingId = OmniUtil.getLoggingId(request);
			try {
				BpUserData searchUserData = new BpUserData();
				searchUserData.setIncsNo(joinRequest.getIncsno()); // custResponse.getIncsNo()
				searchUserData.setCstmid(joinRequest.getLoginid());

				log.debug("▶▶▶▶▶▶ ② beautypoint customer check id api data : {}", StringUtil.printJson(searchUserData));

				// 온라인 회원ID 중복 체크
				ApiResponse createResponse = customerApiService.checkBpOnlineId(searchUserData);

				log.debug("▶▶▶▶▶▶ ② beautypoint customer check id api response : {}", StringUtil.printJson(createResponse));

				boolean success = "000".equals(createResponse.getRsltCd());

				if (success) {

					BpUserData userData = new BpUserData();
					userData.setIncsNo(joinRequest.getIncsno()); // custResponse.getIncsNo()
					userData.setCstmid(joinRequest.getLoginid());
					userData.setPswd(joinRequest.getLoginpassword());
					// join-on 가입하는 API에 수신동의 항목 추가
					List<Marketing> joinMarketings = joinRequest.getMarketings();
					if (joinMarketings != null && !joinMarketings.isEmpty()) {
						for (Marketing joinMarketing : joinMarketings) {
							if ("000".equals(joinMarketing.getChCd())) {
								userData.setSmsReceiveType(joinMarketing.getSmsAgree());
							}
						}
					}

					createResponse = customerApiService.createBpUser(userData, joinRequest); // 뷰티포인트 등록
					log.debug("▶▶▶▶▶▶ ② beautypoint customer regist api response : {}", StringUtil.printJson(createResponse));

					// 이미 가입된 회원의 경우도 성공으로 처리할 필요있음
					success &= "000".equals(createResponse.getRsltCd()) || "050".equals(createResponse.getRsltCd()); //
					if (!success) {
						commonAuth.insertProcessLog(loggingId, //
								"뷰티포인트 등록 API", //
								customerApiService.getClass().getCanonicalName(), //
								"createBpUser", //
								"C", //
								userData.toString(), // StringUtil.printJson(createCustVo);
								createResponse.toString(), // StringUtil.printJson(custResponse);
								createResponse.getRsltCd(), //
								createResponse.getRsltMsg());
					}
				} else {

					if (success) {
						response.setResultCode(ResultCode.SUCCESS.getCode());
					} else {
						response.setResultCode(createResponse.getRsltCd());
						response.setMessage(createResponse.getRsltMsg());
					}
					commonAuth.insertProcessLog(loggingId, //
							"뷰티포인트 조회 API", //
							customerApiService.getClass().getCanonicalName(), //
							"checkBpOnlineId", //
							"S", //
							searchUserData.toString(), // StringUtil.printJson(createCustVo);
							createResponse.toString(), // StringUtil.printJson(custResponse);
							createResponse.getRsltCd(), //
							createResponse.getRsltMsg());
				}

				if (success) {
					this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
					response.setResultCode(ResultCode.SUCCESS.getCode());
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_REGIST_BEAUTY_POINT_CALL_API_FAIL, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				log.error(e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				response.setMessage(ResultCode.SYSTEM_ERROR.message());
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				commonAuth.insertProcessLog(loggingId, //
						"뷰티포인트 API", //
						this.getClass().getCanonicalName(), //
						"registBeautyPointCustomerCallApi", //
						"F", //
						joinRequest.toString(), // StringUtil.printJson(createCustVo);
						e.getMessage(), // StringUtil.printJson(custResponse);
						response.getResultCode(), //
						response.getMessage());
			}
			log.debug(OmniUtil.getApiResultCode(response, "JoinBpRegistProcess.registBeautyPointCustomerCallApi"));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			return response;
		}

		public BaseResponse registOmniCustomer(final Map<String, Object> request) {
			BaseResponse response = new BaseResponse();
			final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
			if (!stopwatch.isRunning()) {
				stopwatch.start("registOmniCustomer");
			}
			JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
			String loggingId = OmniUtil.getLoggingId(request);
			try {
				final CreateUserData joinUserData = new CreateUserData();
				joinUserData.setIncsNo(Integer.parseInt(joinRequest.getIncsno())); // custResponse.getIncsNo()
				joinUserData.setLoginId(joinRequest.getLoginid());
				joinUserData.setPassword(joinRequest.getLoginpassword());
				joinUserData.setJoinFlag(OmniConstants.OMNI_JOIN_FLAG);
				joinUserData.setCn(joinRequest.getUnm());

				ApiBaseResponse createResponse = customerApiService.createUser(joinUserData); // 옴니회원등록

				log.debug("▶▶▶▶▶▶ ⑤ omni wso2 customer regist api response : {}", StringUtil.printJson(createResponse));

				boolean success = ResultCode.SUCCESS.getCode().equals(createResponse.getResultCode()) || ResultCode.USER_ALREADY_EXIST.getCode().equals(createResponse.getResultCode());
				if (!ResultCode.SUCCESS.getCode().equals(createResponse.getResultCode())) {
					commonAuth.insertProcessLog(loggingId, //
							"옴니 뷰티포인트 등록 API", //
							customerApiService.getClass().getCanonicalName(), //
							"createUser", //
							"C", //
							joinUserData.toString(), // StringUtil.printJson(createCustVo);
							createResponse.toString(), // StringUtil.printJson(custResponse);
							createResponse.getResultCode(), //
							createResponse.getMessage());
				}
				// 8. 약관동의 처리
				if (success) {
					if (joinRequest.getTerms() != null && !joinRequest.getTerms().isEmpty()) {
						List<Terms> joinTerms = joinRequest.getTerms();
						for (Terms term : joinTerms) {
							final String onlineChCd = ChannelPairs.getOnlineCd(joinRequest.getChcd());
							term.setChgChCd(onlineChCd);
							term.setIncsNo(Integer.parseInt(joinRequest.getIncsno())); // 고객통합 플랫폼 처리 결과 통합고객번호로 세팅하기
							term.setTncAgrYn(term.getTncAgrYn().equals("Y") ? "A" : "D");
							term.setTncaChgDt(DateUtil.getCurrentDate());
							if (termsService.existTerms(term)) {
								termsService.mergeTerms(term);
								termsService.insertTermHist(term);
							}
						}
					}
				} else {
					success = ResultCode.USER_ALREADY_EXIST.getCode().equals(createResponse.getResultCode());
				}

				log.debug("▶▶▶▶▶▶ ⑤ omni trans flag : {} --> trns type :  {}", joinRequest.isTransCustomer(), joinRequest.getTrnsType());

				// 전환가입자이면 전환플래그 처리
				String incsNo = joinRequest.getIncsno();
				if (StringUtils.hasText(incsNo) && !"0".equals(incsNo)) {
					List<UmChUser> chUsers = mgmtService.getChannelConversionUserList(joinRequest.getChcd(), incsNo);
					if (chUsers == null || chUsers.isEmpty()) {

						final String encChloginid = WebUtil.getStringSession(OmniConstants.XID_SESSION);
						final String chLoginid = SecurityUtil.getXValue(encChloginid, false);

						log.debug("▶▶▶▶▶▶ ⑤ channel customer 통합고객번호가 없는 경로 자체 고객 {}", chLoginid);

						UmChUser umChUser = new UmChUser();
						umChUser.setChCd(joinRequest.getChcd());
						umChUser.setChcsWebId(chLoginid);
						umChUser.setIncsNo(Integer.parseInt(incsNo));
						boolean convs = mgmtService.updateConversionCompleteById(umChUser);
						if (!convs) {
							commonAuth.insertProcessLog(loggingId, //
									String.format("전환 처리 API, 웹아이디(%s), 통합고객번호(%s)", chLoginid, incsNo), //
									mgmtService.getClass().getCanonicalName(), //
									"updateConversionCompleteById", //
									"U", //
									umChUser.toString(), // StringUtil.printJson(createCustVo);
									null);// StringUtil.printJson(custResponse);

						}

					} else {

						log.debug("▶▶▶▶▶▶ ⑤ channel customer 통합고객번호가 있는 경로 고객");

						UmChUser umChUser = new UmChUser();
						umChUser.setChCd(joinRequest.getChcd());
						umChUser.setIncsNo(Integer.parseInt(incsNo));
						boolean convs = mgmtService.updateConversionComplete(umChUser);
						if (!convs) {
							commonAuth.insertProcessLog(loggingId, //
									String.format("전환 처리 API, 통합고객번호(%s)", incsNo), //
									mgmtService.getClass().getCanonicalName(), //
									"updateConversionComplete", //
									"U", //
									umChUser.toString(), // StringUtil.printJson(createCustVo);
									null); // StringUtil.printJson(custResponse);

						}
					}
				}

				if (success) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
				} else {
					response.setResultCode(createResponse.getResultCode());
					response.setMessage(createResponse.getMessage());
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.JOIN, OmniStdLogConstants.AUTH_REGIST_OMNI_CUSTOMER_CALL_API_FAIL, null, null, null,
						LogInfoConstants.LOG_APPSIDE_FRONT_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				
				log.error(e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				response.setMessage(ResultCode.SYSTEM_ERROR.message());
				response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				commonAuth.insertProcessLog(loggingId, //
						"옴니 뷰티포인트 등록 API", //
						customerApiService.getClass().getCanonicalName(), //
						"createUser", //
						"C", //
						joinRequest.toString(), // StringUtil.printJson(createCustVo);
						e.getMessage(), // StringUtil.printJson(custResponse);
						response.getResultCode(), //
						response.getMessage());
			}
			log.debug(OmniUtil.getApiResultCode(response, "JoinBpRegistProcess.registOmniCustomer"));
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			return response;
		}
	}

}
