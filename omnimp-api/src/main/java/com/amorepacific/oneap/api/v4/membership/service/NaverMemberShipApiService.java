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
 * Author	          : hjw0228
 * Date   	          : 2023. 3. 24..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v4.membership.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.api.common.service.CommonCustomerApiService;
import com.amorepacific.oneap.api.v2.join.vo.CustMarketingRequest;
import com.amorepacific.oneap.api.v2.join.vo.CustMarketingVo;
import com.amorepacific.oneap.api.v2.join.vo.CustTncaRequest;
import com.amorepacific.oneap.api.v2.join.vo.CustTncaVo;
import com.amorepacific.oneap.api.v4.membership.mapper.NaverMemberShipApiMapper;
import com.amorepacific.oneap.api.v4.membership.vo.Contents;
import com.amorepacific.oneap.api.v4.membership.vo.NaverMembershipTermsVo;
import com.amorepacific.oneap.api.v4.membership.vo.NaverRequest;
import com.amorepacific.oneap.api.v4.membership.vo.NaverResponse;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.CicuedCuChQcVo;
import com.amorepacific.oneap.common.vo.api.CustChListResponse;
import com.amorepacific.oneap.common.vo.api.CustChResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.CustbyChCsNoResponse;
import com.amorepacific.oneap.common.vo.api.DeleteCustChRequest;
import com.amorepacific.oneap.common.vo.user.Customer;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v4.membership.service 
 *    |_ NaverMemberShipApiService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 3. 24.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@Service
public class NaverMemberShipApiService {
	
	private ConfigUtil config = ConfigUtil.getInstance();

	@Autowired
	private SystemInfo systemInfo;
	
	@Autowired
	private CommonCustomerApiService customerApiService;
	
	@Autowired
	private NaverMemberShipApiMapper naverMemberShipApiMapper;
	
	private final String success = "SUCCESS";
	private final String fail = "FAIL";
	
	public NaverResponse checkApUser(final NaverRequest naverRequest) {
		String chCd = naverMemberShipApiMapper.getChCdByInterlockSellerNo(naverRequest.getInterlockSellerNo());
		
		NaverResponse naverResponse = new NaverResponse();
		
		// 1. memberId 로 고객통합플랫폼에 가입 여부 조회
		CustbyChCsNoResponse custbyChCsNoResponse = customerApiService.getCustbyChCsNo(chCd, naverRequest.getInterlockMemberIdNo());
		
		if(custbyChCsNoResponse == null || ResultCode.SYSTEM_ERROR.getCode().equals(custbyChCsNoResponse.getRsltCd())) {
			naverResponse.setOperationResult(fail);
			return naverResponse;
		}
		
		if("ICITSVCOM001".equals(custbyChCsNoResponse.getRsltCd()) || "ICITSVCOM002".equals(custbyChCsNoResponse.getRsltCd())) { // 존재하지 않는 사용자 리턴
			naverResponse.setOperationResult(fail);
			return naverResponse;
		}
		
		// 2. 리턴된 통합고객번호로 고객통합플랫폼 사용자 조회
		String incsNo = custbyChCsNoResponse.getIncsNo();
		Customer customer = new Customer();
		if(StringUtils.hasText(incsNo)) {
			customer = customerApiService.getCicuemcuInfrByIncsNo(incsNo);
			
			if(customer == null || ResultCode.SYSTEM_ERROR.getCode().equals(customer.getRsltCd())) {
				naverResponse.setOperationResult(fail);
				return naverResponse;
			} else if ("ICITSVCOM001".equals(customer.getRsltCd()) || "ICITSVCOM002".equals(customer.getRsltCd())) {
				naverResponse.setOperationResult(fail);
				return naverResponse;
			}
		}
		
		// 3. 리턴된 통합고객번호를 SHA-512로 암호화 후 affiliateMemberIdNo 파라미터와 비교, 불일치 시 오류 리턴
		String affiliateMemberIdNo = naverRequest.getAffiliateMemberIdNo();
		if(!affiliateMemberIdNo.equals(SecurityUtil.getEncodedSHA512Password(incsNo))) {
			naverResponse.setOperationResult(fail);
			return naverResponse;
		}
		
		Contents contents = new Contents();
		contents.setAffiliateMemberIdNo(affiliateMemberIdNo);
		contents.setInterlockMemberIdNo(custbyChCsNoResponse.getChcsNo());
		contents.setInterlockSellerNo(naverRequest.getInterlockSellerNo());
		contents.setInterlock(true);
		
		naverResponse.setContents(contents);
		naverResponse.setOperationResult(success);
		
		log.debug("BeautyPoint Membership Naver API - Check AP User Result ==== {}", StringUtil.printJson(naverResponse));
		
		return naverResponse;
	}
	
	public NaverResponse unLinkMembership(final NaverRequest naverRequest) {
		String chCd = naverMemberShipApiMapper.getChCdByInterlockSellerNo(naverRequest.getInterlockSellerNo());
		NaverResponse naverResponse = new NaverResponse();
		
		// 1. memberId 로 고객통합플랫폼에 가입 여부 조회
		CustbyChCsNoResponse custbyChCsNoResponse = customerApiService.getCustbyChCsNo(chCd, naverRequest.getInterlockMemberIdNo());
		
		if(custbyChCsNoResponse == null || ResultCode.SYSTEM_ERROR.getCode().equals(custbyChCsNoResponse.getRsltCd())) {
			naverResponse.setOperationResult(fail);
			return naverResponse;
		}
		
		// 2. 제휴사에 연동된 사용자 여부 체크
		if(!"ICITSVCOM000".equals(custbyChCsNoResponse.getRsltCd())) {
			naverResponse.setOperationResult(fail);
			return naverResponse;
		}
		
		String incsNo = custbyChCsNoResponse.getIncsNo();
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		if(StringUtils.hasText(naverRequest.getAffiliateMemberIdNo()) && SecurityUtil.getEncodedSHA512Password(incsNo).equals(naverRequest.getAffiliateMemberIdNo())) { // 3. 전달된 xincsNo 복호화 후 고객통합플랫폼에 저장된 통합고객번호와 일치여부 체크 -> 일치하는 경우만 경로 탈퇴 API 호출
			
			// 전달된 채널이 401 채널인 경우 제휴사 포함 경로 삭제 및 약관, 수신 동의 철회
			if(OmniConstants.NAVER_STORE_CHCD.equals(chCd)) {
				List<String> afltChCdList = new ArrayList<String>();
				
				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setIncsNo(incsNo);
				
				CustChListResponse custChListResponse = customerApiService.getCustChList(custInfoVo);
				
				for(CicuedCuChQcVo cicuedCuChQcVo : custChListResponse.getCicuedCuChQcVo()) {
					if(!"Y".equals(cicuedCuChQcVo.getDelYn())) {
						for(Object afltChCd : config.getNaverAfltChannelCodes()) {
							if(afltChCd != null && afltChCd.toString().equals(cicuedCuChQcVo.getChCd())) { // 고객통합플랫폼에 연동된 네이버 스마트 스토어 관계사 경로 탈퇴
								afltChCdList.add(afltChCd.toString());
								DeleteCustChRequest deleteCustChRequest = new DeleteCustChRequest();
								com.amorepacific.oneap.common.vo.api.DeleteCustChRequest.CicuedCuChTcVo cicuedCuChTcVo = new com.amorepacific.oneap.common.vo.api.DeleteCustChRequest.CicuedCuChTcVo();	
								
								// Mandatory
								cicuedCuChTcVo.setIncsNo(incsNo); // 필수
								cicuedCuChTcVo.setChCd(afltChCd.toString()); // 필수
								
								deleteCustChRequest.addCicuedCuChTcVo(cicuedCuChTcVo);
								
								CustChResponse custChResponse = customerApiService.deleteCustChannelMember(deleteCustChRequest);
								
								// 네이버 스마트 스토어에 NIF-0004 연동정보삭제 API API 호출
								NaverRequest afltNaverRequest = new NaverRequest();
								afltNaverRequest.setInterlockMemberIdNo(cicuedCuChQcVo.getChcsNo());
								afltNaverRequest.setAffiliateMemberIdNo( SecurityUtil.getEncodedSHA512Password(incsNo));
								
								// stm-api-key 초기화
								String stmApiKey = this.config.getChannelApi(afltChCd.toString(), "apikey", profile);
								// affiliate-seller-key (현재는 고정 값)
								String affiliateSellerKey = this.config.getChannelApi(afltChCd.toString(), "sellerno", profile);
								// unLink URL
								String url = this.config.getChannelApi(chCd, "unlinkurl", profile);
								
								naverResponse = customerApiService.deleteNaverMembership(url, stmApiKey, affiliateSellerKey, naverRequest);
							}
						}
						
						if(chCd.equals(cicuedCuChQcVo.getChCd())) { // 마지막으로 네이버 스마트 스토어 401 채널 경로 삭제
							afltChCdList.add(chCd);
							DeleteCustChRequest deleteCustChRequest = new DeleteCustChRequest();
							com.amorepacific.oneap.common.vo.api.DeleteCustChRequest.CicuedCuChTcVo cicuedCuChTcVo = new com.amorepacific.oneap.common.vo.api.DeleteCustChRequest.CicuedCuChTcVo();	
							
							// Mandatory
							cicuedCuChTcVo.setIncsNo(incsNo); // 필수
							cicuedCuChTcVo.setChCd(chCd); // 필수
							
							deleteCustChRequest.addCicuedCuChTcVo(cicuedCuChTcVo);
							
							CustChResponse custChResponse = customerApiService.deleteCustChannelMember(deleteCustChRequest);
						}
					}
				}
				
				// 네이버 스마트 스토어를 통한 필수 약관 철회 시 제휴사 약관도 철회 처리
				List<NaverMembershipTermsVo> naverMembershipOptionalTerms = naverMemberShipApiMapper.getNaverMembershipAffiliateTerms(afltChCdList);
				if(naverMembershipOptionalTerms != null && !naverMembershipOptionalTerms.isEmpty()) {
					CustTncaRequest custTncaRequest = new CustTncaRequest();
					List<CustTncaVo> custTncaVos = new ArrayList<>();
					for(NaverMembershipTermsVo naverMembershipTermsVo : naverMembershipOptionalTerms) {
						CustTncaVo terms = new CustTncaVo();
						terms.setTcatCd(naverMembershipTermsVo.getPrcnTcatCd());
						terms.setIncsNo(incsNo);
						terms.setTncvNo(naverMembershipTermsVo.getTncvNo());
						terms.setTncAgrYn("N");
						terms.setLschId("OCP");
						terms.setChgChCd(chCd);
						terms.setChCd(naverMembershipTermsVo.getChCd());
						custTncaVos.add(terms);
					}
					CustTncaVo custTncaVoArr[] = custTncaVos.toArray(new CustTncaVo[custTncaVos.size()]);
					custTncaRequest.setCicuedCuTncaTcVo(custTncaVoArr);
					ApiResponse apiResponse = customerApiService.savecicuedcutnca(custTncaRequest);					
				}
				
				// 네이버 스마트 스토어를 통한 필수 약관 철회 시 SMS 수신 동의도 철회 처리
				if(afltChCdList != null && !afltChCdList.isEmpty()) {
					CustMarketingRequest custMarketingRequest = new CustMarketingRequest();
					List<CustMarketingVo> custMarketingVos = new ArrayList<>();					
					for(String afltChCd : afltChCdList) {
						CustMarketingVo marketing = new CustMarketingVo();
						marketing.setChCd(afltChCd);
						marketing.setIncsNo(incsNo);
						marketing.setEmlOptiYn("N");
						marketing.setSmsOptiYn("N");
						marketing.setDmOptiYn("N");
						marketing.setTmOptiYn("N");
						marketing.setKkoIntlOptiYn("N");
						marketing.setFscrId("OCP");
						marketing.setLschId("OCP");
						custMarketingVos.add(marketing);
					}
					CustMarketingVo cicuemCuOptiTcVoArr[] = custMarketingVos.toArray(new CustMarketingVo[custMarketingVos.size()]);
					custMarketingRequest.setCicuemCuOptiTcVo(cicuemCuOptiTcVoArr);
					ApiResponse apiResponse = customerApiService.savecicuemcuoptilist(custMarketingRequest);
				}
				
				naverResponse.setOperationResult(success);
				return naverResponse;
			} else { // 401 채널이 아닌 경우 해당 제휴사만 경로 삭제 및 약관, 수신 동의 철회
				List<String> afltChCdList = Arrays.asList(chCd);
				
				List<NaverMembershipTermsVo> naverMembershipOptionalTerms = naverMemberShipApiMapper.getNaverMembershipAffiliateTerms(afltChCdList);
				if(naverMembershipOptionalTerms != null && !naverMembershipOptionalTerms.isEmpty()) {
					CustTncaRequest custTncaRequest = new CustTncaRequest();
					List<CustTncaVo> custTncaVos = new ArrayList<>();
					for(NaverMembershipTermsVo naverMembershipTermsVo : naverMembershipOptionalTerms) {
						CustTncaVo terms = new CustTncaVo();
						terms.setTcatCd(naverMembershipTermsVo.getPrcnTcatCd());
						terms.setIncsNo(incsNo);
						terms.setTncvNo(naverMembershipTermsVo.getTncvNo());
						terms.setTncAgrYn("N");
						terms.setLschId("OCP");
						terms.setChgChCd(chCd);
						terms.setChCd(naverMembershipTermsVo.getChCd());
						custTncaVos.add(terms);
					}
					CustTncaVo custTncaVoArr[] = custTncaVos.toArray(new CustTncaVo[custTncaVos.size()]);
					custTncaRequest.setCicuedCuTncaTcVo(custTncaVoArr);
					ApiResponse apiResponse = customerApiService.savecicuedcutnca(custTncaRequest);					
				}
				
				// 네이버 스마트 스토어를 통한 필수 약관 철회 시 SMS 수신 동의도 철회 처리
				if(afltChCdList != null && !afltChCdList.isEmpty()) {
					CustMarketingRequest custMarketingRequest = new CustMarketingRequest();
					List<CustMarketingVo> custMarketingVos = new ArrayList<>();					
					for(String afltChCd : afltChCdList) {
						CustMarketingVo marketing = new CustMarketingVo();
						marketing.setChCd(afltChCd);
						marketing.setIncsNo(incsNo);
						marketing.setEmlOptiYn("N");
						marketing.setSmsOptiYn("N");
						marketing.setDmOptiYn("N");
						marketing.setTmOptiYn("N");
						marketing.setKkoIntlOptiYn("N");
						marketing.setFscrId("OCP");
						marketing.setLschId("OCP");
						custMarketingVos.add(marketing);
					}
					CustMarketingVo cicuemCuOptiTcVoArr[] = custMarketingVos.toArray(new CustMarketingVo[custMarketingVos.size()]);
					custMarketingRequest.setCicuemCuOptiTcVo(cicuemCuOptiTcVoArr);
					ApiResponse apiResponse = customerApiService.savecicuemcuoptilist(custMarketingRequest);
				}
				
				DeleteCustChRequest deleteCustChRequest = new DeleteCustChRequest();
				com.amorepacific.oneap.common.vo.api.DeleteCustChRequest.CicuedCuChTcVo cicuedCuChTcVo = new com.amorepacific.oneap.common.vo.api.DeleteCustChRequest.CicuedCuChTcVo();
				
				// Mandatory
				cicuedCuChTcVo.setIncsNo(incsNo); // 필수
				cicuedCuChTcVo.setChCd(chCd); // 필수
				
				deleteCustChRequest.addCicuedCuChTcVo(cicuedCuChTcVo);
				
				CustChResponse custChResponse = customerApiService.deleteCustChannelMember(deleteCustChRequest);
				
				if(custChResponse == null || ResultCode.SYSTEM_ERROR.getCode().equals(custChResponse.getRsltCd())) {
					naverResponse.setOperationResult(fail);
					return naverResponse;
				} else {
					naverResponse.setOperationResult(success);
					return naverResponse;
				}
			}
		} else if (StringUtils.isEmpty(naverRequest.getAffiliateMemberIdNo())) { 
			naverResponse.setOperationResult(fail);
			return naverResponse;
		} else { // 통합고객번호 불일치 시 User Not Found 리턴
			naverResponse.setOperationResult(fail);
			return naverResponse;
		}
	}

	public NaverResponse unLinkNaverMembership(final String chCd, final String incsNo) {
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		NaverRequest naverRequest = new NaverRequest();
		NaverResponse naverResponse = new NaverResponse();
		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setIncsNo(incsNo);
		
		// 1. incsNo로 고객통합플랫폼에 경로 가입 여부 및 chcsNo 값 조회
		CustChListResponse custChListResponse = customerApiService.getCustChList(custInfoVo);
		
		if(custChListResponse == null || ResultCode.SYSTEM_ERROR.getCode().equals(custChListResponse.getRsltCd()) || custChListResponse.getCicuedCuChQcVo() == null) {
			naverResponse.setOperationResult(fail);
			return naverResponse;
		}
		
		boolean flag = false; // 가입 채널 목록에서 가입 여부 조회
		
		for(CicuedCuChQcVo cicuedCuChQcVo : custChListResponse.getCicuedCuChQcVo()) {
			if(chCd.equals(cicuedCuChQcVo.getChCd())) {
				flag = true;
				naverRequest.setInterlockMemberIdNo(cicuedCuChQcVo.getChcsNo());
				naverRequest.setAffiliateMemberIdNo(SecurityUtil.getEncodedSHA512Password(incsNo));
			}
		}
		
		if(!flag) {
			naverResponse.setOperationResult(fail);
			return naverResponse;
		}
		
		// stm-api-key 초기화
		String stmApiKey = this.config.getChannelApi(chCd, "apikey", profile);
		// affiliate-seller-key (현재는 고정 값)
		String affiliateSellerKey = this.config.getChannelApi(chCd, "sellerno", profile);
		// unLink URL
		String url = this.config.getChannelApi(chCd, "unlinkurl", profile);
		
		naverResponse = customerApiService.deleteNaverMembership(url, stmApiKey, affiliateSellerKey, naverRequest);
		
		return naverResponse;
	}
}
