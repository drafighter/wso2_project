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
 * Date   	          : 2020. 11. 5..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.step;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustChannelRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustVO;
import com.amorepacific.oneap.auth.api.vo.ivo.CustMarketingRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustMarketingVo;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaVo;
import com.amorepacific.oneap.auth.api.vo.ovo.CreateCustChannelJoinResponse;
import com.amorepacific.oneap.auth.api.vo.ovo.CreateCustResponse;
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
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.Marketing;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OnOffline;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.ApiProcessResponse;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.BpUserData;
import com.amorepacific.oneap.common.vo.api.ChTermsVo;
import com.amorepacific.oneap.common.vo.api.ChUserVo;
import com.amorepacific.oneap.common.vo.api.CreateChCustRequest;
import com.amorepacific.oneap.common.vo.api.CreateUserData;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmChUser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.step 
 *    |_ ApiProcessService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 5.
 * @version : 1.0
 * @author  : takkies
 */
@Slf4j
@Component
public class ApiProcessor {

	@Autowired
	private CustomerApiService customerApiService;

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private JoinService joinService;
	
	@Autowired
	private MgmtService mgmtService;
	
	@Autowired
	private TermsService termsService;
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 5. 오전 9:45:03
	 * </pre>
	 * @param request
	 * @return
	 */
	public ApiProcessResponse registIntegratedCustomer(final Map<String, Object> request) {
		ApiProcessResponse response = new ApiProcessResponse();
		BaseResponse baseResponse = new BaseResponse();
		CreateCustResponse custResponse = (CreateCustResponse) request.get(OmniConstants.PROCESS_RESPONSE);
		final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
		if (!stopwatch.isRunning()) {
			stopwatch.start("registIntegratedCustomer");
		}
		JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);

		try {

			CustInfoVo custInfoVo = new CustInfoVo();

			// 통합고객번호 있을경우 회원 여부 체크
			if (StringUtils.hasText(joinRequest.getIncsno())) {
				if (!joinRequest.getIncsno().equals("0") && StringUtils.hasText(joinRequest.getIncsno())) {
					custInfoVo.setIncsNo(joinRequest.getIncsno());
				}
			}

			// 본인인증 CI값이 있을 경우 회원 여부 체크
			if (StringUtils.hasText(joinRequest.getCi())) {
				custInfoVo.setCiNo(joinRequest.getCi());
			}

			CustInfoResponse custinfoResponse = customerApiService.getCustList(custInfoVo);

			log.debug("▶▶▶▶▶▶ ① integrated customer exist check response : {}", StringUtil.printJson(custinfoResponse));

			if ("ICITSVCOM001".equals(custinfoResponse.getRsltCd()) || "ICITSVCOM002".equals(custinfoResponse.getRsltCd())) { // 없으면 등록 프로세스
				baseResponse.setResultCode(custinfoResponse.getRsltCd());
			} else {
				if ("ICITSVCOM000".equals(custinfoResponse.getRsltCd()) || "ECOMSVVAL004".equals(custinfoResponse.getRsltCd())) {
					Customer customer = null;
					Customer customers[] = custinfoResponse.getCicuemCuInfTcVo();
					if (customers != null && customers.length > 0) {
						customer = customers[0]; // 중요) 첫번째 데이터가 최신임.
						log.debug("▶▶▶▶▶▶ ① customer : {}", StringUtil.printJson(customer));
						if ("ICITSVCOM001".equals(customer.getRsltCd())) { // 없으면 등록 프로세스
							baseResponse.setResultCode(customer.getRsltCd());
						} else if ("ICITSVCOM004".equals(customer.getRsltCd())) { // 존재하면 업데이트만

							if (StringUtils.hasText(customer.getIncsNo()) && !"0".equals(customer.getIncsNo())) {
								WebUtil.setSession(OmniConstants.INCS_NO_SESSION, null);
								joinRequest.setIncsno(customer.getIncsNo()); // 등록하면 통합고객번호 생김.
								WebUtil.setSession(OmniConstants.INCS_NO_SESSION, Integer.parseInt(joinRequest.getIncsno()));
							}

							response = updateIntegratedCustomer(request);

							log.debug("▶▶▶▶▶▶ ① integrated customer exist check, update : {}", StringUtil.printJson(response));
							if (stopwatch.isRunning()) {
								stopwatch.stop();
							}
							
							return response;
						} else {
							//if ("Y".equalsIgnoreCase(customer.getCustWtYn())) { // 탈퇴사용자
							if (StringUtils.hasText(customer.getCustWtDttm())) {
								log.debug("▶▶▶▶▶▶ ① customer check withdraw : {} [{}]", customer.getCustWtYn(), customer.getCustWtDttm());
								baseResponse.setResultCode("ICITSVCOM001"); // ICITSVCOM002
								response.setBaseResponse(baseResponse);
								if (stopwatch.isRunning()) {
									stopwatch.stop();
								}								
								return response;
							}
							if ("Y".equalsIgnoreCase(customer.getDrccCd())) { // 휴면고객
								String name = joinService.releaseDormancyCustomerName(customer.getIncsNo(), joinRequest.getChcd());
								if (StringUtils.hasText(name)) {
									joinRequest.setUnm(name);
								}
							}
							
							// 고객이 있을 경우 후속 프로세스를 위해 반드시 고객통합번호를 던져주어야함.
							custResponse.setIncsNo(customer.getIncsNo());
							if (StringUtils.isEmpty(joinRequest.getIncsno())) {
								WebUtil.setSession(OmniConstants.INCS_NO_SESSION, null);
								joinRequest.setIncsno(customer.getIncsNo());
								WebUtil.setSession(OmniConstants.INCS_NO_SESSION, Integer.parseInt(joinRequest.getIncsno()));
							}

							response.setInscNo(customer.getIncsNo());
							baseResponse.setResultCode(ResultCode.SUCCESS.getCode());
							response.setBaseResponse(baseResponse);
							if (stopwatch.isRunning()) {
								stopwatch.stop();
							}
							return response;
						}

					}

				}

			}

			final Channel channel = commonService.getChannel(joinRequest.getChcd());

			// 고객통합 입력 데이터 구성
			CreateCustVO createCustVo = JoinData.buildIntegratedOnlineCreateCustomerData(joinRequest, channel);

			log.debug("▶▶▶▶▶▶ ① integrated customer build user data : {}", StringUtil.printJson(createCustVo));

			custResponse = customerApiService.createCust(createCustVo);

			log.debug("▶▶▶▶▶▶ ① integrated customer regist response : {}", StringUtil.printJson(custResponse));

			if ("ICITSVCOM004".equals(custResponse.getRsltCd())) { // 존재하면 업데이트만

				if (StringUtils.hasText(custResponse.getIncsNo()) && !"0".equals(custResponse.getIncsNo())) {
					WebUtil.setSession(OmniConstants.INCS_NO_SESSION, null);
					response.setInscNo(custResponse.getIncsNo()); // 등록하면 통합고객번호 생김.
					WebUtil.setSession(OmniConstants.INCS_NO_SESSION, custResponse.getIncsNo());
				}

				response = updateIntegratedCustomer(request);

				log.debug("▶▶▶▶▶▶ ① integrated customer exist check, update : {}", StringUtil.printJson(response));
				if (stopwatch.isRunning()) {
					stopwatch.stop();
				}
				return response;
			}

			boolean success = "ICITSVCOM000".equals(custResponse.getRsltCd());
			if (success) {

				if (StringUtils.hasText(custResponse.getIncsNo()) && !"0".equals(custResponse.getIncsNo())) {
					response.setInscNo(custResponse.getIncsNo()); // 등록하면 통합고객번호 생김.
					WebUtil.setSession(OmniConstants.INCS_NO_SESSION, custResponse.getIncsNo());
				}

				baseResponse.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				baseResponse.setResultCode(custResponse.getRsltCd());
				baseResponse.setMessage(custResponse.getRsltMsg());
			}
			response.setBaseResponse(baseResponse);
			
		} catch (Exception e) {
			log.error(e.getMessage());
			baseResponse.setMessage(ResultCode.SYSTEM_ERROR.message());
			baseResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		log.debug(OmniUtil.getApiResultCode(baseResponse, "registIntegratedCustomer"));
		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		return response;
	}
	
	
	/**
	 * 
	 * <pre>
	 * comment  : 고객통합 수정 API 호출
	 * author   : takkies
	 * date     : 2020. 11. 5. 오전 9:44:48
	 * </pre>
	 * @param request
	 * @return
	 */
	public ApiProcessResponse updateIntegratedCustomer(final Map<String, Object> request) {
		
		ApiProcessResponse response = new ApiProcessResponse();
		BaseResponse baseResponse = new BaseResponse();
		ApiResponse apiResponse = null;
		
		final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
		if (!stopwatch.isRunning()) {
			stopwatch.start("updateIntegratedCustomer");
		}
		JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);

		try {
			CustInfoVo custInfoVo = new CustInfoVo();
			// 통합고객번호 있을경우 회원 여부 체크
			if (StringUtils.hasText(joinRequest.getIncsno())) {
				custInfoVo.setIncsNo(joinRequest.getIncsno());
			}

			// 통합고객번호로 사용자를 다시 찾기
			// 휴면고객인 경우는 CI가 없음.
			Customer customerincsno = customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);

			log.debug("▶▶▶▶▶▶ ① integrated customer : {}", StringUtil.printJson(customerincsno));

			if (customerincsno != null && StringUtils.hasText(customerincsno.getCiNo())) {
				custInfoVo.setCiNo(customerincsno.getCiNo());
			} else {

				log.debug("① integrated customer dormancy ? {}", customerincsno.getDrccCd());

				if ("Y".equals(customerincsno.getDrccCd())) { // 휴면인 경우 복원 필요
					String name = joinService.releaseDormancyCustomerName(customerincsno.getIncsNo(), joinRequest.getChcd());
					if (StringUtils.hasText(name)) {
						joinRequest.setUnm(name);
					}
				}

			}

			CustInfoResponse custinfoResponse = customerApiService.getCustList(custInfoVo);

			log.debug("▶▶▶▶▶▶ ① integrated customer exist check response : {}", StringUtil.printJson(custinfoResponse));
			if (custinfoResponse.getRsltCd().equals("ICITSVCOM000")) { // 있으면 수정 프로세스
				baseResponse.setResultCode(custinfoResponse.getRsltCd());
			} else {
				log.debug("▶▶▶▶▶▶ ① customer check : {}, {}", custinfoResponse.getRsltCd(), custinfoResponse.getRsltMsg());
				baseResponse.setResultCode(custinfoResponse.getRsltCd());
				response.setBaseResponse(baseResponse);
				
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
					log.debug("▶▶▶▶▶▶ ① customer check withdraw : {}[{}]", customer.getCustWtYn(), customer.getCustWtDttm());
					baseResponse.setResultCode("ICITSVCOM001"); // ICITSVCOM002
					
					response.setBaseResponse(baseResponse);
					
					if (stopwatch.isRunning()) {
						stopwatch.stop();
					}
					return response;
				}
			}

			boolean success = true;

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
			}

			if (success) {
				baseResponse.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				if (apiResponse != null) {
					baseResponse.setResultCode(apiResponse.getRsltCd());
					baseResponse.setMessage(apiResponse.getRsltMsg());
				} else {
					baseResponse.setResultCode("ICITSVCOM999");
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			baseResponse.setMessage(ResultCode.SYSTEM_ERROR.message());
			baseResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		log.debug(OmniUtil.getApiResultCode(baseResponse, "updateIntegratedCustomer"));
		
		response.setBaseResponse(baseResponse);
		
		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		return response;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 5. 오후 1:30:44
	 * </pre>
	 * @param request
	 * @return
	 */
	public ApiProcessResponse cancelIntegratedCustomer(final Map<String, Object> request) {
		ApiProcessResponse response = new ApiProcessResponse();
		BaseResponse baseResponse = new BaseResponse();
		
		final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
		if (!stopwatch.isRunning()) {
			stopwatch.start("cancelIntegratedCustomer");
		}
		JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);

		try {

			// 고객통합 입력 데이터 구성
			final Channel channel = commonService.getChannel(joinRequest.getChcd());
			CreateCustVO createCustVo = JoinData.buildIntegratedOnlineCreateCustomerData(joinRequest, channel);
			createCustVo.setJoinCnclYn("Y"); // 회원가입망취소시 'Y'
			CreateCustResponse custcreateResponse = customerApiService.createCust(createCustVo);

			log.debug("▶▶▶▶▶▶ ① integrated customer cancel api response : {}", StringUtil.printJson(custcreateResponse));

			// 고객통합 취소 API 호출 후 Process 종료되어야 함., 망취소 대상에 없는 경우도 성공으로
			boolean success = "ICITSVCOM000".equals(custcreateResponse.getRsltCd()) || "ICITSVCOM009".equals(custcreateResponse.getRsltCd());
			if (success) {
				baseResponse.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				baseResponse.setResultCode(custcreateResponse.getRsltCd());
				baseResponse.setMessage(custcreateResponse.getRsltMsg());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			baseResponse.setMessage(ResultCode.SYSTEM_ERROR.message());
			baseResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		
		response.setBaseResponse(baseResponse);
		
		log.debug(OmniUtil.getApiResultCode(baseResponse, "cancelIntegratedCustomer"));
		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		return response;
	}

	
	/**
	 * 
	 * <pre>
	 * comment  : 뷰포인트 등록 API
	 * author   : takkies
	 * date     : 2020. 11. 5. 오전 9:53:06
	 * </pre>
	 * @param request
	 * @return
	 */
	public ApiProcessResponse registBeautyPointCustomer(final Map<String, Object> request) {
		
		ApiProcessResponse response = new ApiProcessResponse();
		BaseResponse baseResponse = new BaseResponse();
		
		final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
		if (!stopwatch.isRunning()) {
			stopwatch.start("registBeautyPointCustomer");
		}
		JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);

		try {
			BpUserData searchUserData = new BpUserData();
			searchUserData.setIncsNo(joinRequest.getIncsno());
			searchUserData.setCstmid(joinRequest.getLoginid());

			// 온라인 회원ID 중복 체크
			ApiResponse createResponse = customerApiService.checkBpOnlineId(searchUserData);

			boolean success = "000".equals(createResponse.getRsltCd());

			if (success) {

				BpUserData userData = new BpUserData();
				userData.setIncsNo(joinRequest.getIncsno());
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

				success &= "000".equals(createResponse.getRsltCd()); //

			} else {
				if (success) {
					baseResponse.setResultCode(ResultCode.SUCCESS.getCode());
				} else {
					baseResponse.setResultCode(createResponse.getRsltCd());
					baseResponse.setMessage(createResponse.getRsltMsg());
				}
			}

			if (success) {
				baseResponse.setResultCode(ResultCode.SUCCESS.getCode());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			baseResponse.setMessage(ResultCode.SYSTEM_ERROR.message());
			baseResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		
		response.setBaseResponse(baseResponse);
		
		log.debug(OmniUtil.getApiResultCode(baseResponse, "registBeautyPointCustomer"));
		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		return response;
	}
	
	
	/**
	 * 
	 * <pre>
	 * comment  : 오프라인 경로 등록
	 * author   : takkies
	 * date     : 2020. 11. 5. 오전 9:54:44
	 * </pre>
	 * @param request
	 * @return
	 */
	public ApiProcessResponse registOfflineChannelCustomer(final Map<String, Object> request) {
		
		ApiProcessResponse response = this.registIntegrateChannelCustomer(OnOffline.Offline, request);
		
		if (ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {
			response = this.registChannelCustomer(OnOffline.Offline, request);
		}
		
		if (!ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {
			response = this.cancelIntegrateChannelCustomer(OnOffline.Offline, request);
		}
		
		return response;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 5. 오후 1:34:43
	 * </pre>
	 * @param request
	 * @return
	 */
	public ApiProcessResponse registOnlineChannelCustomer(final Map<String, Object> request) {
		
		ApiProcessResponse response = this.registIntegrateChannelCustomer(OnOffline.Online, request);
		
		if (ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {
			response = this.registChannelCustomer(OnOffline.Online, request);
		}
		
		if (!ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {
			response = this.cancelIntegrateChannelCustomer(OnOffline.Online, request);
		}
		
		return response;
	}
	
	// 고객통합 온라인/오프라인 등록 API
	public ApiProcessResponse registIntegrateChannelCustomer(final OnOffline onoffline, final Map<String, Object> request) {
		ApiProcessResponse response = new ApiProcessResponse();
		BaseResponse baseResponse = new BaseResponse();

		JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
		final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
		Channel channel = commonService.getChannel(joinRequest.getChcd());
		try {
			// 수신 동의 처리 시 해당 경로시스템의 수신 동의 여부가 없을 경우 N 으로 처리
			/*
			 * List<Marketing> joinMarketings = joinRequest.getMarketings(); if (joinMarketings == null || joinMarketings.isEmpty()) {
			 * baseResponse.setResultCode(ResultCode.SUCCESS.getCode()); response.setBaseResponse(baseResponse); if (stopwatch.isRunning()) {
			 * stopwatch.stop(); } return response; }
			 */

			if (StringUtils.isEmpty(joinRequest.getChcd())) {
				joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
			}

			CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedOnlineChannelCustomerData(onoffline, channel, joinRequest);

			log.debug("▶▶▶▶▶▶ ③ integrated channel {} customer api data : {}", onoffline.name(), StringUtil.printJson(chCustRequest));

			CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
			log.debug("▶▶▶▶▶▶ ③ integrated channel {} customer api response : {}", onoffline.name(), StringUtil.printJson(chjoinResponse));
			// 경로 고객 존재하는 경우도 성공으로 판단
			boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse.getRsltCd());


			if (success) {
				baseResponse.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				baseResponse.setResultCode(chjoinResponse.getRsltCd());
				baseResponse.setMessage(chjoinResponse.getRsltMsg());
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			baseResponse.setMessage(ResultCode.SYSTEM_ERROR.message());
			baseResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		response.setBaseResponse(baseResponse);
		log.debug(OmniUtil.getApiResultCode(baseResponse, "registIntegrateChannelCustomer"));
		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		return response;
	}
	
	// 고객통합 온라인/오프라인 경로 취소 API
	public ApiProcessResponse cancelIntegrateChannelCustomer(final OnOffline onoffline, final Map<String, Object> request) {
		ApiProcessResponse response = new ApiProcessResponse();
		BaseResponse baseResponse = new BaseResponse();

		JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
		final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
		// 온라인 가입 시 오프라인것도 가입해주기 위해서 경로코드 변경처리
		Channel channel = commonService.getChannel(joinRequest.getChcd());

		try {
			CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedOnlineChannelCustomerData(onoffline, channel, joinRequest);
			chCustRequest.setJoinCnclYn("Y"); // 망취소 처리
			log.debug("▶▶▶▶▶▶ ③ integrated channel {} customer api data : {}", onoffline.name(), StringUtil.printJson(chCustRequest));
			CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
			log.debug("▶▶▶▶▶▶ ③ integrated channel {} customer api response : {}", onoffline.name(), StringUtil.printJson(chjoinResponse));
			boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVCOM009".equals(chjoinResponse.getRsltCd());
			
			if (success) {
				baseResponse.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				baseResponse.setResultCode(chjoinResponse.getRsltCd());
				baseResponse.setMessage(chjoinResponse.getRsltMsg());
			}
			// TODO 채널 탈퇴사용자의 경우 여기서 탈퇴정보 돌려주기
			if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(joinRequest.getWithdrawCode()) //
					&& joinRequest.isWithdraw() //
					&& StringUtils.hasText(joinRequest.getWithdrawDate()) //
					&& DateUtil.isValidDateFormat(joinRequest.getWithdrawDate())) {

				baseResponse.setResultCode(ResultCode.CHANNEL_WITHDRAW.getCode());
				baseResponse.setMessage(joinRequest.getWithdrawDate());
				log.debug("▶▶▶▶▶▶ ③ integrated channel {} customer api withdraw user", onoffline.name(), StringUtil.printJson(response));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			baseResponse.setMessage(ResultCode.SYSTEM_ERROR.message());
			baseResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		
		response.setBaseResponse(baseResponse);
		
		log.debug(OmniUtil.getApiResultCode(baseResponse, "RegistCustomerProcess.cancelIntegrateChannelCustomer"));
		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		return response;
	}
	
	// 채널 온라인/오프라인 등록 API
	public ApiProcessResponse registChannelCustomer(final OnOffline onoffline, final Map<String, Object> request) {
		ApiProcessResponse response = new ApiProcessResponse();
		BaseResponse baseResponse = new BaseResponse();
		
		CreateCustResponse custResponse = (CreateCustResponse) request.get(OmniConstants.PROCESS_RESPONSE);
		JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);
		final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);

		try {
			// 전사약관인 경우는 처리하지 않음.
			boolean skipProcess = joinRequest.isCorpTerms(); // false;

			if (skipProcess) { // 전사약관은 처리하지 않고 바로 성공으로
				log.debug("▶▶▶▶▶▶ ④ channel customer {} api request : {}", onoffline.name(), "skip channel customer regist, corp terms!!!");
				baseResponse.setResultCode(ResultCode.SUCCESS.getCode());
				if (stopwatch.isRunning()) {
					stopwatch.stop();
				}
				return response;
			}

			if (StringUtils.isEmpty(joinRequest.getChcd())) {
				joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
			}

			// 경로 회원 검색 처리
			String chWebId = WebUtil.getStringSession("chcsWebId");
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
					chUserVo.setJoinPrtnId("");
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

			if (ResultCode.SUCCESS.getCode().equals(custApiResponse.getResultCode())) {
				baseResponse.SetResponseInfo(ResultCode.SUCCESS);
			} else {
				baseResponse.setResultCode(custApiResponse.getResultCode());
				baseResponse.setMessage(custApiResponse.getMessage());
				// TODO 채널 탈퇴사용자의 경우 여기서 탈퇴정보 전달하기
				if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(custApiResponse.getResultCode())) {
					if (DateUtil.isValidDateFormat(custApiResponse.getMessage())) {
						response.setWithdraw(true);
						response.setWithdrawCode(ResultCode.CHANNEL_WITHDRAW.getCode());
						response.setWithdrawDate(custApiResponse.getMessage());
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			baseResponse.setMessage(ResultCode.SYSTEM_ERROR.message());
			baseResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		
		response.setBaseResponse(baseResponse);
		
		log.debug(OmniUtil.getApiResultCode(baseResponse, "RegistCustomerProcess.registChannelCustomer"));
		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		return response;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 5. 오전 11:52:15
	 * </pre>
	 * @param request
	 * @return
	 */
	public ApiProcessResponse registOmniCustomer(final Map<String, Object> request) {
		ApiProcessResponse response = new ApiProcessResponse();
		BaseResponse baseResponse = new BaseResponse();
		
		final StopWatch stopwatch = (StopWatch) request.get(OmniConstants.PROCESS_STOPWATCH);
		if (!stopwatch.isRunning()) {
			stopwatch.start("registOmniCustomer");
		}
		JoinRequest joinRequest = (JoinRequest) request.get(OmniConstants.PROCESS_REQUEST);

		try {
			final CreateUserData joinUserData = new CreateUserData();
			joinUserData.setIncsNo(Integer.parseInt(joinRequest.getIncsno()));
			joinUserData.setLoginId(joinRequest.getLoginid());
			joinUserData.setPassword(joinRequest.getLoginpassword());
			joinUserData.setJoinFlag(OmniConstants.OMNI_JOIN_FLAG);
			joinUserData.setCn(joinRequest.getUnm());

			ApiBaseResponse createResponse = customerApiService.createUser(joinUserData); // 옴니회원등록

			log.debug("▶▶▶▶▶▶ ⑤ omni wso2 customer regist api response : {}", StringUtil.printJson(createResponse));

			boolean success = ResultCode.SUCCESS.getCode().equals(createResponse.getResultCode()) || ResultCode.USER_ALREADY_EXIST.getCode().equals(createResponse.getResultCode());

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
					mgmtService.updateConversionCompleteById(umChUser);

				} else {
					log.debug("▶▶▶▶▶▶ ⑤ channel customer 통합고객번호가 있는 경로 고객");
					UmChUser umChUser = new UmChUser();
					umChUser.setChCd(joinRequest.getChcd());
					umChUser.setIncsNo(Integer.parseInt(incsNo));
					mgmtService.updateConversionComplete(umChUser);
				}
			}

			if (success) {
				baseResponse.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				baseResponse.setResultCode(createResponse.getResultCode());
				baseResponse.setMessage(createResponse.getMessage());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			baseResponse.setMessage(ResultCode.SYSTEM_ERROR.message());
			baseResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		response.setBaseResponse(baseResponse);
		log.debug(OmniUtil.getApiResultCode(baseResponse, "RegistCustomerProcess.registOmniCustomer"));
		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		return response;
	}

}
