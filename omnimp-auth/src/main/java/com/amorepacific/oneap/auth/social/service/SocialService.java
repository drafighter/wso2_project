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
 * Author	          : hkdang
 * Date   	          : 2020. 11. 2..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.social.service;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustChannelRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustChannelRequest.CicuedCuChTcVo;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustChannelRequest.CicuemCuOptiTcVo;
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
import com.amorepacific.oneap.auth.social.mapper.SocialMapper;
import com.amorepacific.oneap.common.code.ResultCode;
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
import com.amorepacific.oneap.common.vo.Marketing;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OnOffline;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.ChTermsVo;
import com.amorepacific.oneap.common.vo.api.ChUserVo;
import com.amorepacific.oneap.common.vo.api.CreateChCustRequest;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.sns.ApplePublicKeysResponse;
import com.amorepacific.oneap.common.vo.sns.ApplePublicKeysResponse.Key;
import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.amorepacific.oneap.common.vo.sns.SnsProfileResponse;
import com.amorepacific.oneap.common.vo.sns.SnsType;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.social 
 *    |_ SocialService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 2.
 * @version : 1.0
 * @author  : hkdang
 */

@Service
@Slf4j
public class SocialService {
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private CustomerApiService customerApiService;
	
	@Autowired
	private JoinService joinServie;
	
	@Autowired
	private MgmtService mgmtService;
	
	@Autowired
	private SocialMapper socialMapper;
	
	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	// 통합 가입
	public BaseResponse registIntegratedCustomerCallApi(JoinRequest joinRequest) {
		BaseResponse response = new BaseResponse();
		CustInfoVo custInfoVo = new CustInfoVo();
		
		try {
			// 통합고객번호 있을경우 회원 여부 체크
			if (StringUtils.hasText(joinRequest.getIncsno())) {
				if (!"0".equals(joinRequest.getIncsno())) {
					custInfoVo.setIncsNo(joinRequest.getIncsno());
				}
			}

			// 본인인증 CI값이 있을 경우 회원 여부 체크
			if (StringUtils.hasText(joinRequest.getCi())) {
				custInfoVo.setCiNo(joinRequest.getCi());
			}

			CustInfoResponse custinfoResponse = customerApiService.getCustList(custInfoVo);

			if ("ICITSVCOM001".equals(custinfoResponse.getRsltCd()) || "ICITSVCOM002".equals(custinfoResponse.getRsltCd())) { // 없으면 등록 프로세스
				response.setResultCode(custinfoResponse.getRsltCd());
			} else {
				if ("ICITSVCOM000".equals(custinfoResponse.getRsltCd()) || "ECOMSVVAL004".equals(custinfoResponse.getRsltCd())) {
					Customer customer = null;
					Customer customers[] = custinfoResponse.getCicuemCuInfTcVo();
					if (customers != null && customers.length > 0) {
						customer = customers[0]; // 중요) 첫번째 데이터가 최신임.
						if ("ICITSVCOM001".equals(customer.getRsltCd())) { // 없으면 등록 프로세스
							response.setResultCode(customer.getRsltCd());
						} else if ("ICITSVCOM004".equals(customer.getRsltCd())) { // 존재하면 업데이트만
							
							if (StringUtils.hasText(customer.getIncsNo()) && !"0".equals(customer.getIncsNo())) {
								joinRequest.setIncsno(customer.getIncsNo()); // 등록하면 통합고객번호 생김.
								WebUtil.setSession(OmniConstants.INCS_NO_SESSION, Integer.parseInt(joinRequest.getIncsno()));
							}
							//response = updateIntegratedCustomerCallApi(joinRequest); // 경로 '가입' 에만 사용하므로 고객통합 업데이트 필요 없음
							response.setResultCode(ResultCode.SUCCESS.getCode()); // 존재하면 success 로 넘김 업데이트 할것 없음
							log.debug("▶▶▶▶▶▶ ① integrated customer exist check, update : {}", StringUtil.printJson(response));

							return response;
						} else {
							if(StringUtils.hasText(customer.getCustWtDttm())) { // 탈퇴사용자 : orgin code = ICITSVBIZ152
								log.debug("▶▶▶▶▶▶ ① customer check withdraw : {}[{}]", customer.getCustWtYn(), customer.getCustWtDttm());
								response.setResultCode("ICITSVCOM001"); // ICITSVCOM002
								return response;
							}

							if (StringUtils.isEmpty(joinRequest.getIncsno())) {
								joinRequest.setIncsno(customer.getIncsNo());
								WebUtil.setSession(OmniConstants.INCS_NO_SESSION, Integer.parseInt(joinRequest.getIncsno()));
							}

							log.debug("1. 고객통합 등록 API 오프라인 가입 : {}", StringUtil.printJson(joinRequest));

							return response;
						}
					}
				}
			}

			final Channel channel = commonService.getChannel(joinRequest.getChcd());

			log.debug("2. 고객통합 등록 API 오프라인 가입 : {}", StringUtil.printJson(joinRequest));
			// 고객통합 입력 데이터 구성
			CreateCustVO createCustVo = JoinData.buildIntegratedOnlineCreateCustomerData(joinRequest, channel);
			CreateCustResponse custResponse = customerApiService.createCust(createCustVo);

			log.debug("▶▶▶▶▶▶ ① integrated customer regist response : {}", StringUtil.printJson(custResponse));

			if ("ICITSVCOM004".equals(custResponse.getRsltCd())) { // 존재하면 업데이트만

				if (StringUtils.hasText(custResponse.getIncsNo()) && !"0".equals(custResponse.getIncsNo())) {
					WebUtil.setSession(OmniConstants.INCS_NO_SESSION, null);
					joinRequest.setIncsno(custResponse.getIncsNo()); // 등록하면 통합고객번호 생김.
					WebUtil.setSession(OmniConstants.INCS_NO_SESSION, Integer.parseInt(joinRequest.getIncsno()));

					log.debug("1-1. 등록하면 통합고객번호 생김 incsno {}", WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION));

				}
				//response = updateIntegratedCustomerCallApi(joinRequest); // 경로 '가입' 에만 사용하므로 고객통합 업데이트 필요 없음
				response.setResultCode(ResultCode.SUCCESS.getCode()); // 존재하면 success 로 넘김 업데이트 할것 없음

				log.debug("▶▶▶▶▶▶ ① integrated customer exist check, update : {}", StringUtil.printJson(response));

				return response;
			}

			boolean success = "ICITSVCOM000".equals(custResponse.getRsltCd()); // || "ICITSVCOM004".equals(custResponse.getRsltCd()); // 존재하는 고객인 경우 성공
			if (success) {

				if (StringUtils.hasText(custResponse.getIncsNo()) && !"0".equals(custResponse.getIncsNo())) {
					joinRequest.setIncsno(custResponse.getIncsNo()); // 등록하면 통합고객번호 생김.
					log.debug("2. 등록하면 통합고객번호 생김 incsno {}", joinRequest.getIncsno());
					WebUtil.setSession(OmniConstants.INCS_NO_SESSION, Integer.parseInt(joinRequest.getIncsno()));
					log.debug("2-1. 등록하면 통합고객번호 생김 incsno {}", WebUtil.getStringSession(OmniConstants.INCS_NO_SESSION));
				}

				response.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				response.setResultCode(custResponse.getRsltCd());
				response.setMessage(custResponse.getRsltMsg());
			}
		} catch (Exception e) {
			log.error(e.getMessage());

			response.setMessage(ResultCode.SYSTEM_ERROR.message());
			response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}

		return response;
	}
	
	// 고객통합 수정 API 호출
	public BaseResponse updateIntegratedCustomerCallApi(JoinRequest joinRequest) {
		BaseResponse response = new BaseResponse();
		ApiResponse apiResponse = null;
		
		//String loggingId = OmniUtil.getLoggingId(request);
		try {
			CustInfoVo custInfoVo = new CustInfoVo();
			// 통합고객번호 있을경우 회원 여부 체크
			if (StringUtils.hasText(joinRequest.getIncsno())) {
				custInfoVo.setIncsNo(joinRequest.getIncsno());
			}

			// 통합고객번호로 사용자를 다시 찾기
			// 휴면고객인 경우는 CI가 없음.
			Customer customerincsno = customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);

			if (customerincsno != null && StringUtils.hasText(customerincsno.getCiNo())) {
				custInfoVo.setCiNo(customerincsno.getCiNo());
			} else {

				log.debug("① integrated customer dormancy ? {}", customerincsno.getDrccCd());

				if ("Y".equals(customerincsno.getDrccCd())) { // 휴면인 경우 복원 필요
					String name = joinServie.releaseDormancyCustomerName(customerincsno.getIncsNo(), joinRequest.getChcd());
					if (StringUtils.hasText(name)) {
						joinRequest.setUnm(name);
					}
				}
			}

			CustInfoResponse custinfoResponse = customerApiService.getCustList(custInfoVo);

			if (custinfoResponse.getRsltCd().equals("ICITSVCOM000")) { // 있으면 수정 프로세스
				response.setResultCode(custinfoResponse.getRsltCd());
			} else {
				log.debug("▶▶▶▶▶▶ ① customer check : {}, {}", custinfoResponse.getRsltCd(), custinfoResponse.getRsltMsg());
				response.setResultCode(custinfoResponse.getRsltCd());
				
				return response;
			}

			Customer customers[] = custinfoResponse.getCicuemCuInfTcVo();
			if (customers != null && customers.length > 0) {
				Customer customer = customers[0]; // 첫번째 데이터가 최신임.
				if ("Y".equalsIgnoreCase(customer.getCustWtYn())) { // 탈퇴사용자
					log.debug("▶▶▶▶▶▶ ① customer check withdraw : {}[{}]", customer.getCustWtYn(), customer.getCustWtDttm());
					response.setResultCode("ICITSVCOM001"); // ICITSVCOM002
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

				/*
				if (!success) {
					commonAuth.insertServiceLog(loggingId, //
							"고객통합 수정 API(약관동의)", //
							customerApiService.getClass().getCanonicalName(), //
							"savecicuedcutnca", //
							"U", //
							custTncaRequest.toString(), // StringUtil.printJson(createCustVo);
							apiResponse.toString(), // StringUtil.printJson(custResponse);
							apiResponse.getRsltCd(), //
							apiResponse.getRsltMsg());
				}
				*/
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
			log.error(e.getMessage());
			response.setMessage(ResultCode.SYSTEM_ERROR.message());
			response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		log.debug(OmniUtil.getApiResultCode(response, "RegistOfflineCustomerProcess.updateIntegratedCustomerCallApi"));
		
		return response;
	}
	
	// 고객통합 취소 API
	public BaseResponse cancelIntegratedCustomerCallApi(JoinRequest joinRequest) {
		BaseResponse response = new BaseResponse();
		
		try {
			// 고객통합 입력 데이터 구성
			final Channel channel = commonService.getChannel(joinRequest.getChcd());
			CreateCustVO createCustVo = JoinData.buildIntegratedOnlineCreateCustomerData(joinRequest, channel);
			createCustVo.setJoinCnclYn("Y"); // 회원가입망취소시 'Y'
			CreateCustResponse custcreateResponse = customerApiService.createCust(createCustVo);

			// 성공(ICITSVCOM000) 이거나 망취소 대상이 없으면(ICITSVCOM009) SUCCESS
			boolean success = "ICITSVCOM000".equals(custcreateResponse.getRsltCd()) || "ICITSVCOM009".equals(custcreateResponse.getRsltCd());
			if (success) {
				response.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				response.setResultCode(custcreateResponse.getRsltCd());
				response.setMessage(custcreateResponse.getRsltMsg());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			
			response.setMessage(ResultCode.SYSTEM_ERROR.message());
			response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		log.debug(OmniUtil.getApiResultCode(response, "RegistOfflineCustomerProcess.cancelIntegratedCustomerCallApi"));
		
		return response;
	}
	
	// 경로 off line
	public BaseResponse registIntegrateOfflineChannelCustomer(JoinRequest joinRequest) {
		
		BaseResponse response = new BaseResponse();
		
		String offlineChannelCd = ChannelPairs.getOfflineCd(joinRequest.getChcd());
			
		if (StringUtils.isEmpty(offlineChannelCd)) {
			response.setResultCode(ResultCode.NOT_EXIST_OFFLINE.getCode());
			return response;
		}				
			
			joinRequest.setChcd(offlineChannelCd);
		
		final Channel channel = commonService.getChannel(joinRequest.getChcd());
		
		try {
			
			if (StringUtils.isEmpty(joinRequest.getChcd())) {
				joinRequest.setChcd(WebUtil.getStringSession(OmniConstants.CH_CD_SESSION));
			}

			CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedOfflineChannelCustomerData(OnOffline.Offline, channel, joinRequest);

			log.debug("▶▶▶▶▶▶ ③ integrated channel customer api data : {}", StringUtil.printJson(chCustRequest));

			CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);

			log.debug("▶▶▶▶▶▶ ③ integrated channel customer api response : {}", StringUtil.printJson(chjoinResponse));
			// 경로 고객 존재하는 경우도 성공으로 판단
			boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse.getRsltCd());

			if (success) {
				response.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				response.setResultCode(chjoinResponse.getRsltCd());
				response.setMessage(chjoinResponse.getRsltMsg());
			}
			
			//20230922 012추가 시, 008도 추가
			if(OmniConstants.OSULLOC_OFFLINE_CHCD.equals(joinRequest.getChcd())) {
				boolean channel008 = true;
				for(CicuedCuChTcVo vo : chCustRequest.getCicuedCuChTcVo()) {
					if(OmniConstants.OSULLOC_DEPARTMENT_CHCD.equals(vo.getChCd())) channel008=false;
				}
				if(channel008 == true) {
					CicuedCuChTcVo cicuedCuChTcVo = new CicuedCuChTcVo();
					cicuedCuChTcVo.setIncsNo(chCustRequest.getCicuedCuChTcVo().get(0).getIncsNo().toString());
					cicuedCuChTcVo.setChCd(OmniConstants.OSULLOC_DEPARTMENT_CHCD);
					cicuedCuChTcVo.setChcsNo(chCustRequest.getCicuedCuChTcVo().get(0).getChcsNo().toString());
					cicuedCuChTcVo.setFstCnttPrtnId(chCustRequest.getCicuedCuChTcVo().get(0).getFstCnttPrtnId().toString());
					cicuedCuChTcVo.setPrtnNm(chCustRequest.getCicuedCuChTcVo().get(0).getPrtnNm().toString());
					cicuedCuChTcVo.setFscrId("OCP");
					cicuedCuChTcVo.setLschId("OCP");
					
					CicuemCuOptiTcVo cicuemCuOptiTcVo = new CicuemCuOptiTcVo();
					cicuemCuOptiTcVo.setChCd(OmniConstants.OSULLOC_DEPARTMENT_CHCD);
					cicuemCuOptiTcVo.setEmlOptiYn("N");
					cicuemCuOptiTcVo.setSmsOptiYn(chCustRequest.getCicuedCuChTcVo().get(0).getCicuemCuOptiTcVo().getSmsOptiYn().toString());
					cicuemCuOptiTcVo.setDmOptiYn("N");
					cicuemCuOptiTcVo.setTmOptiYn("N");
					cicuemCuOptiTcVo.setEmlOptiDt("");
					cicuemCuOptiTcVo.setSmsOptiDt(chCustRequest.getCicuedCuChTcVo().get(0).getCicuemCuOptiTcVo().getSmsOptiDt().toString());
					cicuemCuOptiTcVo.setDmOptiDt("");
					cicuemCuOptiTcVo.setTmOptiDt("");
					cicuemCuOptiTcVo.setIntlOptiYn("N");
					cicuemCuOptiTcVo.setIntlOptiDt("");
					cicuemCuOptiTcVo.setKkoIntlOptiYn("N");
					cicuemCuOptiTcVo.setKkoIntlOptiDt("");
					cicuemCuOptiTcVo.setFscrId("OCP");
					cicuemCuOptiTcVo.setLschId("OCP");
					
					cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);
					
					CreateCustChannelRequest chCustRequest2 = new CreateCustChannelRequest();
					chCustRequest2.addCicuedCuChTcVo(cicuedCuChTcVo);
					log.debug("▶▶▶▶▶▶ ③ integrated channel customer api data 008 : {}", StringUtil.printJson(chCustRequest2));
					CreateCustChannelJoinResponse chjoinResponse2 = customerApiService.createCustChannelMember(chCustRequest2);
					log.debug("▶▶▶▶▶▶ ③ integrated channel customer api response 008 : {}", StringUtil.printJson(chjoinResponse2));
					boolean success2 = "ICITSVCOM000".equals(chjoinResponse2.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse2.getRsltCd());
					if (success2) {
						response.setResultCode(ResultCode.SUCCESS.getCode());
					} else {
						response.setResultCode(chjoinResponse2.getRsltCd());
						response.setMessage(chjoinResponse2.getRsltMsg());
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			//this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
			response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		log.debug(OmniUtil.getApiResultCode(response, "RegistCustomerProcess.registIntegrateChannelCustomer"));
		return response;	
	}
	
	public BaseResponse registOfflineChannelCustomer(JoinRequest joinRequest) {
		BaseResponse response = new BaseResponse();
		
		final String orgChannelCd = joinRequest.getChcd();
		// 온라인 가입 시 오프라인것도 가입해주기 위해서 경로코드 변경처리
		String offlineChannelCd = ChannelPairs.getOfflineCd(joinRequest.getChcd());
		
		if (StringUtils.isEmpty(offlineChannelCd)) {
			response.setResultCode(ResultCode.SUCCESS.getCode());
			return response;
		}				
			
		joinRequest.setChcd(offlineChannelCd);
	
		try {
			
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
			
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(joinRequest.getIncsno());
			Customer customer = customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null) {
				chUserVo.setIncsCardNoEc(customer.getIncsCardNoEc());
			}
			
			if (StringUtils.isEmpty(joinRequest.getJoinPrtnId())) {
				if (StringUtils.hasText(config.getJoinPrtnCode(joinRequest.getChcd()))) {
					chUserVo.setJoinPrtnId(config.getJoinPrtnCode(joinRequest.getChcd())); // 필수, 최초접촉거래처ID
				} else {
					chUserVo.setJoinPrtnId(""); // 필수, 최초접촉거래처ID
				}
			} else {
				chUserVo.setJoinPrtnId(joinRequest.getJoinPrtnId());
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
			
			// espoirPOS만 가입된 휴면 고객이 espoir몰 가입 시 espoirPOS 재가입하지 않기 위해 
			// 아래 함수에서 가입된 고객 채널정보 리스트를 OmniConstants.EXIST_CUSTOMER에 추가한다. 
			customerApiService.getCicuemcuInfrByIncsNo(custInfoVo, true);

			log.debug("▶▶▶▶▶▶ ④ channel customer create user data : {}", StringUtil.printJson(createChCustRequest));
			ApiBaseResponse custApiResponse = customerApiService.createChannelUser(joinRequest.getChcd(), createChCustRequest, joinRequest);
			log.debug("▶▶▶▶▶▶ ④ channel customer create user response : {}", StringUtil.printJson(custApiResponse));

			if (StringUtils.hasText(orgChannelCd)) {
				joinRequest.setChcd(orgChannelCd);
			}
			
			if (ResultCode.SUCCESS.getCode().equals(custApiResponse.getResultCode())) {
				response.SetResponseInfo(ResultCode.SUCCESS);
			} else {
				
				if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(custApiResponse.getResultCode())) {
					if (DateUtil.isValidDateFormat(custApiResponse.getMessage())) {
						/*
						joinRequest.setWithdraw(true);
						joinRequest.setWithdrawCode(ResultCode.CHANNEL_WITHDRAW.getCode());
						joinRequest.setWithdrawDate(custApiResponse.getMessage());
						joinRequest.setChcd(orgChannelCd);
						this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
						*/
						
						response.setResultCode(ResultCode.USER_DISABLED.getCode());
						response.setMessage(custApiResponse.getMessage());
					}
				} else {
					response.setResultCode(custApiResponse.getResultCode());
					response.setMessage(custApiResponse.getMessage());
				}
				
				/*
				// TODO 채널 탈퇴사용자의 경우 여기서 탈퇴정보 전달하기
				if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(custApiResponse.getResultCode())) {
					if (DateUtil.isValidDateFormat(custApiResponse.getMessage())) {
						joinRequest.setWithdraw(true);
						joinRequest.setWithdrawCode(ResultCode.CHANNEL_WITHDRAW.getCode());
						joinRequest.setWithdrawDate(custApiResponse.getMessage());
						joinRequest.setChcd(orgChannelCd);
						this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
					}
				}
				*/
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			//this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
			response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		log.debug(OmniUtil.getApiResultCode(response, "RegistCustomerProcess.registChannelCustomer"));
		
		return response;
	}
	
	public BaseResponse cancelIntegrateOfflineChannelCustomer(JoinRequest joinRequest) {
		
		BaseResponse response = new BaseResponse();

		// 온라인 가입 시 오프라인것도 가입해주기 위해서 경로코드 변경처리
		final String orgChannelCd = joinRequest.getChcd();
		String offlineChannelCd = ChannelPairs.getOfflineCd(joinRequest.getChcd());
		
		if (StringUtils.isEmpty(offlineChannelCd)) {
			response.setResultCode(ResultCode.SUCCESS.getCode());
			return response;
		}
			
		joinRequest.setChcd(offlineChannelCd);
		
		final Channel channel = commonService.getChannel(joinRequest.getChcd());
		
		try {
			CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedOnlineChannelCustomerData(OnOffline.Offline, channel, joinRequest);
			chCustRequest.setJoinCnclYn("Y"); // 망취소 처리
			log.debug("▶▶▶▶▶▶ ③ integrated channel customer api data : {}", StringUtil.printJson(chCustRequest));
			CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
			log.debug("▶▶▶▶▶▶ ③ integrated channel customer api response : {}", StringUtil.printJson(chjoinResponse));
			
			// 성공(ICITSVCOM000) 이거나 망취소 대상이 없으면(ICITSVCOM009) SUCCESS
			boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVCOM009".equals(chjoinResponse.getRsltCd());
			if (StringUtils.hasText(orgChannelCd)) {
				joinRequest.setChcd(orgChannelCd);
			}
			
			if (success) {
				response.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				response.setResultCode(chjoinResponse.getRsltCd());
				response.setMessage(chjoinResponse.getRsltMsg());
			}
			
			//20230922 012추가 시, 008도 추가
			if(OmniConstants.OSULLOC_OFFLINE_CHCD.equals(joinRequest.getChcd())) {
				boolean channel008 = true;
				for(CicuedCuChTcVo vo : chCustRequest.getCicuedCuChTcVo()) {
					if(OmniConstants.OSULLOC_DEPARTMENT_CHCD.equals(vo.getChCd())) channel008=false;
				}
				if(channel008 == true) {
					CicuedCuChTcVo cicuedCuChTcVo = new CicuedCuChTcVo();
					cicuedCuChTcVo.setIncsNo(chCustRequest.getCicuedCuChTcVo().get(0).getIncsNo().toString());
					cicuedCuChTcVo.setChCd(OmniConstants.OSULLOC_DEPARTMENT_CHCD);
					cicuedCuChTcVo.setChcsNo(chCustRequest.getCicuedCuChTcVo().get(0).getChcsNo().toString());
					cicuedCuChTcVo.setFstCnttPrtnId(chCustRequest.getCicuedCuChTcVo().get(0).getFstCnttPrtnId().toString());
					cicuedCuChTcVo.setPrtnNm(chCustRequest.getCicuedCuChTcVo().get(0).getPrtnNm().toString());
					cicuedCuChTcVo.setFscrId("OCP");
					cicuedCuChTcVo.setLschId("OCP");
					
					CicuemCuOptiTcVo cicuemCuOptiTcVo = new CicuemCuOptiTcVo();
					cicuemCuOptiTcVo.setChCd(OmniConstants.OSULLOC_DEPARTMENT_CHCD);
					cicuemCuOptiTcVo.setEmlOptiYn("N");
					cicuemCuOptiTcVo.setSmsOptiYn(chCustRequest.getCicuedCuChTcVo().get(0).getCicuemCuOptiTcVo().getSmsOptiYn().toString());
					cicuemCuOptiTcVo.setDmOptiYn("N");
					cicuemCuOptiTcVo.setTmOptiYn("N");
					cicuemCuOptiTcVo.setEmlOptiDt("");
					cicuemCuOptiTcVo.setSmsOptiDt(chCustRequest.getCicuedCuChTcVo().get(0).getCicuemCuOptiTcVo().getSmsOptiDt().toString());
					cicuemCuOptiTcVo.setDmOptiDt("");
					cicuemCuOptiTcVo.setTmOptiDt("");
					cicuemCuOptiTcVo.setIntlOptiYn("N");
					cicuemCuOptiTcVo.setIntlOptiDt("");
					cicuemCuOptiTcVo.setKkoIntlOptiYn("N");
					cicuemCuOptiTcVo.setKkoIntlOptiDt("");
					cicuemCuOptiTcVo.setFscrId("OCP");
					cicuemCuOptiTcVo.setLschId("OCP");
					
					cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);
					
					CreateCustChannelRequest chCustRequest2 = new CreateCustChannelRequest();
					
					chCustRequest2.addCicuedCuChTcVo(cicuedCuChTcVo);
					log.debug("▶▶▶▶▶▶ ③ integrated channel customer api data 008 : {}", StringUtil.printJson(chCustRequest2));
					CreateCustChannelJoinResponse chjoinResponse2 = customerApiService.createCustChannelMember(chCustRequest2);
					log.debug("▶▶▶▶▶▶ ③ integrated channel customer api response 008 : {}", StringUtil.printJson(chjoinResponse2));
					boolean success2 = "ICITSVCOM000".equals(chjoinResponse2.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse2.getRsltCd());
					if (success2) {
						response.setResultCode(ResultCode.SUCCESS.getCode());
					} else {
						response.setResultCode(chjoinResponse2.getRsltCd());
						response.setMessage(chjoinResponse2.getRsltMsg());
					}
				}
			}
			
			// TODO 채널 탈퇴사용자의 경우 여기서 탈퇴정보 돌려주기
			if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(joinRequest.getWithdrawCode()) //
					&& joinRequest.isWithdraw() //
					&& StringUtils.hasText(joinRequest.getWithdrawDate()) //
					&& DateUtil.isValidDateFormat(joinRequest.getWithdrawDate())) {

				response.setResultCode(ResultCode.CHANNEL_WITHDRAW.getCode());
				response.setMessage(joinRequest.getWithdrawDate());
				log.debug("▶▶▶▶▶▶ ③ integrated channel customer api withdraw user", StringUtil.printJson(response));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			//this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
			response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		log.debug(OmniUtil.getApiResultCode(response, "RegistCustomerProcess.cancelIntegrateChannelCustomer"));
		
		return response;
	}
	
	// on line
	public BaseResponse registIntegrateOnlineChannelCustomer(JoinRequest joinRequest) {
		BaseResponse response = new BaseResponse();
		
		final String orgChannelCd = joinRequest.getChcd();

		Channel channel = commonService.getChannel(joinRequest.getChcd());
		
		try {
			
			CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedOnlineChannelCustomerData(OnOffline.Online, channel, joinRequest);

			log.debug("▶▶▶▶▶▶ ③ integrated Online channel customer api data : {}", StringUtil.printJson(chCustRequest));

			CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
			log.debug("▶▶▶▶▶▶ ③ integrated Online channel customer api response : {}", StringUtil.printJson(chjoinResponse));
			// 경로 고객 존재하는 경우도 성공으로 판단
			boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse.getRsltCd());

			if (StringUtils.hasText(orgChannelCd)) {
				joinRequest.setChcd(orgChannelCd);
			}
			
			if (success) {
				response.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				response.setResultCode(chjoinResponse.getRsltCd());
				response.setMessage(chjoinResponse.getRsltMsg());
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			// this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
			response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		log.debug(OmniUtil.getApiResultCode(response, "RegistCustomerProcess.registIntegrateChannelCustomer"));

		return response;
	}
	
	public BaseResponse registOnlineChannelCustomer(JoinRequest joinRequest) {
		BaseResponse response = new BaseResponse();
		
		try {
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
			
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(joinRequest.getIncsno());
			Customer customer = customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null) {
				chUserVo.setIncsCardNoEc(customer.getIncsCardNoEc());
			}
			
			if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
				chUserVo.setJoinPrtnId(joinRequest.getJoinPrtnId());
			} else {
				chUserVo.setJoinPrtnId("");
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
				response.SetResponseInfo(ResultCode.SUCCESS);
			} else {
				
				// TODO 채널 탈퇴사용자의 경우 여기서 탈퇴정보 전달하기
				if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(custApiResponse.getResultCode())) {
					if (DateUtil.isValidDateFormat(custApiResponse.getMessage())) {
						/*
						joinRequest.setWithdraw(true);
						joinRequest.setWithdrawCode(ResultCode.CHANNEL_WITHDRAW.getCode());
						joinRequest.setWithdrawDate(custApiResponse.getMessage());
						joinRequest.setChcd(orgChannelCd);
						this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
						*/
						
						response.setResultCode(ResultCode.USER_DISABLED.getCode());
						response.setMessage(custApiResponse.getMessage());
					}
				} else {
					response.setResultCode(custApiResponse.getResultCode());
					response.setMessage(custApiResponse.getMessage());	
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			//this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
			response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		log.debug(OmniUtil.getApiResultCode(response, "RegistCustomerProcess.registChannelCustomer"));
		
		return response;
	}
	
	public BaseResponse cancelIntegrateOnlineChannelCustomer(JoinRequest joinRequest) {
		
		BaseResponse response = new BaseResponse();
		
		final Channel channel = commonService.getChannel(joinRequest.getChcd());
		
		try {
			CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedOnlineChannelCustomerData(OnOffline.Online, channel, joinRequest);
			chCustRequest.setJoinCnclYn("Y"); // 망취소 처리
			log.debug("▶▶▶▶▶▶ ③ integrated channel Online customer api data : {}", StringUtil.printJson(chCustRequest));
			CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
			log.debug("▶▶▶▶▶▶ ③ integrated channel Online customer api response : {}", StringUtil.printJson(chjoinResponse));
			
			// 성공(ICITSVCOM000) 이거나 망취소 대상이 없으면(ICITSVCOM009) SUCCESS
			boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVCOM009".equals(chjoinResponse.getRsltCd());
			if (success) {
				response.setResultCode(ResultCode.SUCCESS.getCode());
			} else {
				response.setResultCode(chjoinResponse.getRsltCd());
				response.setMessage(chjoinResponse.getRsltMsg());
			}
			// TODO 채널 탈퇴사용자의 경우 여기서 탈퇴정보 돌려주기
			if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(joinRequest.getWithdrawCode()) //
					&& joinRequest.isWithdraw() //
					&& StringUtils.hasText(joinRequest.getWithdrawDate()) //
					&& DateUtil.isValidDateFormat(joinRequest.getWithdrawDate())) {

				response.setResultCode(ResultCode.CHANNEL_WITHDRAW.getCode());
				response.setMessage(joinRequest.getWithdrawDate());
				log.debug("▶▶▶▶▶▶ ③ integrated channel Online customer api withdraw user", StringUtil.printJson(response));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			//this.request.put(OmniConstants.PROCESS_REQUEST, joinRequest);
			response.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
		}
		log.debug(OmniUtil.getApiResultCode(response, "RegistCustomerProcess.cancelIntegrateChannelCustomer"));
		
		return response;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 12. 2. 오전 11:48:13
	 * </pre>
	 * @param snsParam
	 * @return
	 */
	public UmOmniUser getSnsMappingIncsNo(final SnsParam snsParam) {
		return this.socialMapper.getSnsMappingIncsNo(snsParam);
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 12. 2. 오후 12:25:06
	 * </pre>
	 * @param snsParam
	 * @return
	 */
	public boolean updateSnsMapping(SnsParam snsParam) {
		return this.socialMapper.updateSnsMapping(snsParam) > 0;
	}
	
	public SnsProfileResponse getProfileFromAppleIdToken(String idToken) {
		
		if(StringUtils.isEmpty(idToken)) {
			log.error("ID Token is null");
			return null;
		}
		
		SnsProfileResponse snsProfileResponse = new SnsProfileResponse();
		
		String[] jwt = idToken.split("[.]");
		
		Map<String, Object> idTokenMap = new HashMap<String, Object>();
		idTokenMap.put("idToken", jwt);
		if(jwt == null || jwt.length <= 2) {
			log.error("ID Token is invalid - {}", idToken);
			return null;
		}
		
	    SignedJWT signedJWT;
		try {
			signedJWT = SignedJWT.parse(idToken);
			JWTClaimsSet payload = signedJWT.getJWTClaimsSet();
			
			idTokenMap.put("header", signedJWT.getHeader());
			idTokenMap.put("payload", signedJWT.getJWTClaimsSet());
			idTokenMap.put("siganture", signedJWT.getSignature());
			log.debug("▶▶▶▶▶▶ getProfileFromAppleIdToken - ID Token : {}", StringUtil.printJson(idTokenMap));
			
			ApplePublicKeysResponse applePublicKeysResponse = customerApiService.getApplePublicKeys();
			
			if(!ResultCode.SYSTEM_ERROR.getCode().equals(applePublicKeysResponse.getResultCode())) { // Apple Public Key 를 가져오기 성공했을 경우만 검증
				ObjectMapper objectMapper = new ObjectMapper();
				
				boolean signature = false;
				for(Key key : applePublicKeysResponse.getKeys()) {
		        	 RSAKey rsaKey = (RSAKey) JWK.parse(objectMapper.writeValueAsString(key));
		             RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
		             JWSVerifier verifier = new RSASSAVerifier(publicKey);
		             if (signedJWT.verify(verifier)) {
		            	 signature = true;
		            	 log.debug("ID Token 복호화 성공");
		             }
				}
				
				if(!signature) {
					log.error("ID Token 복호화 실패");
					return null;
				}
			}
			
			Date currentTime = new Date(System.currentTimeMillis());
		       
	        String aud = payload.getAudience().get(0);
	        String iss = payload.getIssuer();
	        
	        snsProfileResponse.setEmail((String) payload.getClaim("email"));
	        snsProfileResponse.setId((String) payload.getClaim("sub"));
	        
			String profile = this.systemInfo.getActiveProfiles()[0];
			profile = StringUtils.isEmpty(profile) ? "dev" : profile;
			profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
			
			String identifier = this.config.getSnsInfo(profile, SnsType.APPLE.getType().toLowerCase(), "restkey");
	        
	        if (!currentTime.before(payload.getExpirationTime()) || !aud.equals(identifier) || !iss.equals("https://appleid.apple.com")) {
	        	log.error("ID Token 검증 실패 - payload : {}", StringUtil.printJson(signedJWT.getJWTClaimsSet()));
	        	return null;
	        }
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			return null;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			return null;
		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			return null;
		}
		
		return snsProfileResponse;
	}
}
