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
 * Date   	          : 2020. 8. 26..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.terms.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.api.vo.ivo.CicuedCuTncaTcVo;
import com.amorepacific.oneap.auth.api.vo.ivo.CicuehTncListResponse;
import com.amorepacific.oneap.auth.api.vo.ivo.CustMarketingRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustMarketingVo;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaVo;
import com.amorepacific.oneap.auth.join.service.JoinService;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipResponse;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipTermsVo;
import com.amorepacific.oneap.auth.social.handler.SnsAuth;
import com.amorepacific.oneap.auth.social.vo.SnsTermsVo;
import com.amorepacific.oneap.auth.terms.mapper.TermsMapper;
import com.amorepacific.oneap.auth.terms.vo.NaverUnLinkVo;
import com.amorepacific.oneap.auth.terms.vo.RetractionVo;
import com.amorepacific.oneap.auth.terms.vo.TermsVo;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.CicuedCuChQcVo;
import com.amorepacific.oneap.common.vo.api.CuoptiResponse;
import com.amorepacific.oneap.common.vo.api.CuoptiResponse.CicuemCuOptiQcVo;
import com.amorepacific.oneap.common.vo.dormancy.DormancyResponse;
import com.amorepacific.oneap.common.vo.api.CuoptiVo;
import com.amorepacific.oneap.common.vo.api.CustChListResponse;
import com.amorepacific.oneap.common.vo.api.CustChResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.DeleteCustChRequest;
import com.amorepacific.oneap.common.vo.sns.SnsProfileResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenVo;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.terms.service 
 *    |_ TermsService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 26.
 * @version : 1.0
 * @author  : takkies
 */
@Slf4j
@Service
public class TermsService {

	@Autowired
	private CustomerApiService customerApiService;
	
	@Autowired
	private JoinService joinService;
	
	@Autowired
	private SnsAuth snsAuth;
	
	@Autowired
	private TermsMapper termsMapper;
	
	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	public List<TermsVo> getCorpTerms(final TermsVo termsVo) {
		return this.termsMapper.getCorpTerms(termsVo);
	}
	
	public List<TermsVo> getTerms(final TermsVo termsVo) {
		return this.termsMapper.getTerms(termsVo);
	}
	
	public List<TermsVo> getTermsChoice(final TermsVo termsVo) {
		return this.termsMapper.getTermsChoice(termsVo);
	}
	
	public List<TermsVo> getTermsByTags(final SnsTermsVo snsTermsVo) {
		return this.termsMapper.getTermsByTags(snsTermsVo);
	}
	
	public boolean existTerms(final Terms terms) {
		return this.termsMapper.existTerms(terms) > 0;
	}
	
	public boolean insertTerms(final Map<String, List<Terms>> joinTerm) {
		return this.termsMapper.insertTerms(joinTerm) > 0;
	}
	
	public boolean mergeTerms(final Terms terms) {
		return this.termsMapper.mergeTerms(terms) > 0;
	}
	
	public boolean insertTermsHist(final Map<String, List<Terms>> joinTerm) {
		return this.termsMapper.insertTermsHist(joinTerm) > 0;
	}
	
	public boolean insertTermHist(final Terms terms) {
		return this.termsMapper.insertTermHist(terms) > 0;
	}
	
	public boolean insertTermsWithHist(final Map<String, List<Terms>> joinTerm) {
		boolean rtn = insertTerms(joinTerm);
		if (rtn) {
			rtn = insertTermsHist(joinTerm);
		}
		return rtn;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 약관동의 여부
	 * author   : takkies
	 * date     : 2020. 9. 4. 오전 11:59:14
	 * </pre>
	 * @param umOmniUser
	 * @return
	 */
	public boolean hasTermsAgree(final UmOmniUser umOmniUser) {
		
		if (StringUtils.isEmpty(umOmniUser.getIncsNo()) || "0".equals(umOmniUser.getIncsNo())) {
			return false;
		}
		
		return this.termsMapper.hasTermsAgree(umOmniUser) > 0; // 1 이면 필수약관동의 완료
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 전사약관동의 여부 
	 * author   : takkies
	 * date     : 2020. 9. 17. 오후 2:51:36
	 * </pre>
	 * @param umOmniUser
	 * @return
	 */
	public boolean hasCorpTermsAgree(final UmOmniUser umOmniUser) {
		
		if (StringUtils.isEmpty(umOmniUser.getIncsNo()) || "0".equals(umOmniUser.getIncsNo())) {
			return false;
		}
		
		return this.termsMapper.hasCorpTermsAgree(umOmniUser) > 0; // 1 이면 필수약관 동의 완료
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 8. 오후 6:49:13
	 * </pre>
	 * @param umOmniUser
	 * @return
	 */
	public boolean deleteCustTerms(final UmOmniUser umOmniUser) {
		return this.termsMapper.deleteCustTerms(umOmniUser) > 0;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 18. 오후 12:43:45
	 * </pre>
	 * @param umOmniUser
	 * @return
	 */
	public boolean deleteCustCorpTerms(final UmOmniUser umOmniUser) {
		return this.termsMapper.deleteCustCorpTerms(umOmniUser) > 0;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 약관 태그 목록
	 * author   : hkdang
	 * date     : 2020. 10. 29. 오후 2:27:45
	 * </pre>
	 * @param chCd
	 * @return String List
	 */
	public List<String> getTermsTagList(final String chCd) {
		return this.termsMapper.getTermsTagList(chCd);
	}
	
	public NaverUnLinkVo getNaverUnLinkUserInfo(final Map<String, String> param) {
		
		NaverUnLinkVo naverUnLinkVo = new NaverUnLinkVo();
		
		// 가입된 네이버 스마트 스토어 채널 목록
		List<String> afltChCdList = new ArrayList<String>();
		// 가입된 네이버 스마트 스토어의 수신 동의 목록
		List<CicuemCuOptiQcVo> afltChCicuemCuOptiQcVoList = new ArrayList<CicuemCuOptiQcVo>();
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		String chCd = OmniConstants.NAVER_STORE_CHCD;
		String snsType = "NA";
		boolean reprompt = WebUtil.getSession(OmniConstants.NAVER_REPROMPT) == null ? false : true;
		
		// vo set
		SnsTokenVo snsTokenVo = new SnsTokenVo();
		snsTokenVo.setCode(param.get("code"));
		snsTokenVo.setState(SnsAuth.getNaverState());
		
		String accessToken = "";
		
		// token + profile API call
		SnsTokenResponse snsTokenResponse = customerApiService.getSnsToken(snsType, snsTokenVo);
		
		if(snsTokenResponse == null || snsTokenResponse.getAccessToken() == null) {
			log.debug("▶▶▶▶▶▶ [SNS Token Response] = {} ", snsTokenResponse);
			
			if(!StringUtils.isEmpty(snsTokenResponse.getError()) && "no valid data in session".equals(snsTokenResponse.getError_description())) { // 세션 만료로 오류 리턴 시 다시 네아로 호출
				WebUtil.setSession(OmniConstants.NAVER_REPROMPT, true); // 네아로 무한 루프를 방지하기 위해 세션에 값 저장
				naverUnLinkVo.setReturnUrl("redirect:" + snsAuth.getAuthorizeUrl(snsType, chCd));
				
				return naverUnLinkVo;
			}
			
			naverUnLinkVo.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			naverUnLinkVo.setResultMessage(ResultCode.SYSTEM_ERROR.message());
			
			return naverUnLinkVo;
		}
		accessToken = snsTokenResponse.getAccessToken();
		snsTokenVo.setAccessToken(accessToken);
		
		// 토큰 발급 실패시 진행 불가
		log.debug("▶▶▶▶▶▶ [SNS AccessToken] = {} ", accessToken);
		if (StringUtils.isEmpty(accessToken)) {
			log.info("▶▶▶▶▶▶ [Get SNS AccessToken Fail]"); // 실패 메시지 보내주면 좋겠다
			naverUnLinkVo.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			naverUnLinkVo.setResultMessage(ResultCode.SYSTEM_ERROR.message());
			
			return naverUnLinkVo;
		}
		WebUtil.setSession(OmniConstants.SNS_ACCESS_TOKEN, accessToken);
		
		SnsProfileResponse snsProfileResponse = customerApiService.getSnsProfile(snsType, snsTokenVo);
		
		// CI 항목이 Null 인 경우 auth_type=reprompt 파라미터 추가하여 네아로 로그인 다시 시도
		if(!reprompt && snsProfileResponse.getResponse().getCi() == null) {
			WebUtil.setSession(OmniConstants.NAVER_REPROMPT, true); // 네아로 무한 루프를 방지하기 위해 세션에 값 저장
			naverUnLinkVo.setReturnUrl("redirect:" + snsAuth.getAuthorizeUrl(snsType, chCd) + "&auth_type=reprompt");
			
			return naverUnLinkVo;
		}
		
		// 이름, 생년월일, 연락처, CI 값 중 누락데이터 존재할 경우 연동실패로 네이버에 리턴 
		if(snsProfileResponse == null || snsProfileResponse.getResponse() == null
				|| StringUtils.isEmpty(snsProfileResponse.getResponse().getName()) || StringUtils.isEmpty(snsProfileResponse.getResponse().getBirthyear()) || StringUtils.isEmpty(snsProfileResponse.getResponse().getBirthday())
				|| StringUtils.isEmpty(snsProfileResponse.getResponse().getMobile()) || StringUtils.isEmpty(snsProfileResponse.getResponse().getCi()) || StringUtils.isEmpty(snsProfileResponse.getResponse().getGender())) {
			naverUnLinkVo.setResultCode(ResultCode.MEMBERSHIP_UNLINK_FAIL.getCode());
			naverUnLinkVo.setResultMessage(ResultCode.MEMBERSHIP_UNLINK_FAIL.message());
			
			return naverUnLinkVo;
		}
		
		String name = snsProfileResponse.getResponse().getName();
		String birthday = snsProfileResponse.getResponse().getBirthyear().concat(snsProfileResponse.getResponse().getBirthday().replaceAll("-", ""));
		String phone = snsProfileResponse.getResponse().getMobile().replaceAll("-", "");
		String ciNo = snsProfileResponse.getResponse().getCi();
		
		// CI로 고객통합플랫폼 조회
		CustInfoResponse custInfoResponse = customerApiService.getCustInfoList(ciNo, name, phone, birthday);
		
		if(custInfoResponse == null || ResultCode.SYSTEM_ERROR.getCode().equals(custInfoResponse.getRsltCd())) { // 오류 발생 시 오류 리턴
			log.info("▶▶▶▶▶▶ [Naver membership UnLink process] Customer List API Error. custInfoResponse : {}", StringUtil.printJson(custInfoResponse));
			naverUnLinkVo.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			naverUnLinkVo.setResultMessage(ResultCode.SYSTEM_ERROR.message());
			
			return naverUnLinkVo;
		} else if(ResultCode.USER_NOT_FOUND.getCode().equals(custInfoResponse.getRsltCd())) { // CI 불일치 → 이름, 생년월일, 휴대폰으로 다시 조회
			custInfoResponse = customerApiService.getCustInfoList(null, name, phone, birthday);
			
			if(custInfoResponse == null || ResultCode.SYSTEM_ERROR.getCode().equals(custInfoResponse.getRsltCd())) { // 오류 발생 시 오류 리턴
				log.info("▶▶▶▶▶▶ [Naver membership UnLink process] Customer List API Error. custInfoResponse : {}", StringUtil.printJson(custInfoResponse));
				naverUnLinkVo.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				naverUnLinkVo.setResultMessage(ResultCode.SYSTEM_ERROR.message());
				
				return naverUnLinkVo;
			}
		} else if(ResultCode.USER_JOIN_IMPOSSIBLE_30DAYS.getCode().equals(custInfoResponse.getRsltCd())) { // 30일 이내 재 가입 시
			naverUnLinkVo.setResultCode(ResultCode.MEMBERSHIP_UNLINK_FAIL.getCode());
			naverUnLinkVo.setResultMessage(ResultCode.MEMBERSHIP_UNLINK_FAIL.message());
			
			return naverUnLinkVo;
		}
		Customer customer = custInfoResponse.getCicuemCuInfTcVo() == null || custInfoResponse.getCicuemCuInfTcVo().length == 0 ? new Customer() : custInfoResponse.getCicuemCuInfTcVo()[0];
		
		if(customer == null || ResultCode.USER_NOT_FOUND.getCode().equals(custInfoResponse.getRsltCd())) {
			naverUnLinkVo.setResultCode(ResultCode.MEMBERSHIP_UNLINK_FAIL.getCode());
			naverUnLinkVo.setResultMessage(ResultCode.MEMBERSHIP_UNLINK_FAIL.message());
			
			return naverUnLinkVo;
		} else if(ResultCode.USER_DORMANCY.getCode().equals(custInfoResponse.getRsltCd()) || "Y".equalsIgnoreCase(customer.getDrccCd())) { // 휴면 고객일 경우 휴면 복구 후 탈퇴 프로세스 진행
			DormancyResponse dormancyResponse = joinService.releaseDormancyCustomerName1(customer.getIncsNo(), chCd);
			
			if (dormancyResponse != null) {
				String rtnCode = dormancyResponse.getRESPONSE().getHEADER().getRTN_CODE();
				rtnCode = StringUtils.isEmpty(rtnCode) ? dormancyResponse.getRESPONSE().getHEADER().getRTN_TYPE() : rtnCode;
				
				if (rtnCode.equals(OmniConstants.SEND_SMS_EAI_SUCCESS)) {
					// 휴면 복구 성공 시 API 다시 조회
					custInfoResponse = customerApiService.getCustInfoList(ciNo, name, phone, birthday);
					
					customer = custInfoResponse.getCicuemCuInfTcVo() == null || custInfoResponse.getCicuemCuInfTcVo().length == 0 ? new Customer() : custInfoResponse.getCicuemCuInfTcVo()[0];

					if(customer == null || ResultCode.USER_NOT_FOUND.getCode().equals(custInfoResponse.getRsltCd())) {
						naverUnLinkVo.setResultCode(ResultCode.MEMBERSHIP_UNLINK_FAIL.getCode());
						naverUnLinkVo.setResultMessage(ResultCode.MEMBERSHIP_UNLINK_FAIL.message());
						
						return naverUnLinkVo;
					}
					
				} else if (rtnCode.equals("E") // 휴면 복구 시 에러 메시지 [RA-01403: no data found] : 휴면 DB에 사용자 없는 경우 확인 신규 가입 진행
						&& dormancyResponse.getRESPONSE().getHEADER().getRTN_MSG().equals("[ORA-01403: no data found]")) {
					naverUnLinkVo.setResultCode(ResultCode.MEMBERSHIP_UNLINK_FAIL.getCode());
					naverUnLinkVo.setResultMessage(ResultCode.MEMBERSHIP_UNLINK_FAIL.message());
					
					return naverUnLinkVo;
				} else {
					naverUnLinkVo.setResultCode(ResultCode.MEMBERSHIP_UNLINK_FAIL.getCode());
					naverUnLinkVo.setResultMessage(ResultCode.MEMBERSHIP_UNLINK_FAIL.message());
					
					return naverUnLinkVo;
				}
			} else {
				naverUnLinkVo.setResultCode(ResultCode.MEMBERSHIP_UNLINK_FAIL.getCode());
				naverUnLinkVo.setResultMessage(ResultCode.MEMBERSHIP_UNLINK_FAIL.message());
				
				return naverUnLinkVo;
			}
		}
		
		// 고객통합플랫폼에 네이버 스마트 스토어 경로 가입 여부 조회
		boolean isNaverLinked = false;
		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setIncsNo(customer.getIncsNo());
		CustChListResponse custChListResponse = customerApiService.getCustChList(custInfoVo);
		
		if(custChListResponse == null || custChListResponse.getCicuedCuChQcVo() == null || custChListResponse.getCicuedCuChQcVo().size() == 0 || ResultCode.SYSTEM_ERROR.getCode().equals(custChListResponse.getRsltCd())) { // 오류 발생 시 오류 리턴
			log.info("▶▶▶▶▶▶ [Naver membership UnLink process] Customer Channel List API Error. custInfoResponse : {}", StringUtil.printJson(custChListResponse));
			naverUnLinkVo.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			naverUnLinkVo.setResultMessage(ResultCode.SYSTEM_ERROR.message());
			
			return naverUnLinkVo;
		} 
		
		List<CicuedCuChQcVo> cicuedCuChQcVoList = custChListResponse.getCicuedCuChQcVo();
		
		for (CicuedCuChQcVo cicuedCuChQcVo : cicuedCuChQcVoList) { 
			if(!"Y".equals(cicuedCuChQcVo.getDelYn())) {
				if(chCd.equals(cicuedCuChQcVo.getChCd())) { // 401 채널에 가입된 경우 네이버 연동된 상태로 체크
					isNaverLinked = true;
					afltChCdList.add(cicuedCuChQcVo.getChCd());
				}
				
				// 제휴사는 별도의 채널 목록으로 저장
				for(Object afltChCd : config.getNaverAfltChannelCodes()) {
					if(afltChCd != null && afltChCd.toString().equals(cicuedCuChQcVo.getChCd())) afltChCdList.add(afltChCd.toString());
				}
			}
		}
		
		naverUnLinkVo.setAfltChCdList(afltChCdList);
		
		if(!isNaverLinked) {
			naverUnLinkVo.setResultCode(ResultCode.MEMBERSHIP_UNLINK_FAIL.getCode());
			naverUnLinkVo.setResultMessage(ResultCode.MEMBERSHIP_UNLINK_FAIL
					.message());
			
			return naverUnLinkVo;
		}
		
		naverUnLinkVo.setNaverLinked(isNaverLinked);
		
		// 고객통합플랫폼에 동의 약관 목록 조회
		CicuehTncListResponse cicuehTncListResponse = customerApiService.getcicuehtncalist(customer);
		
		if(cicuehTncListResponse == null || !"ICITSVCOM000".equals(cicuehTncListResponse.getRsltCd())) {
			log.info("▶▶▶▶▶▶ [Naver membership UnLink process] Customer Channel List API Error. custInfoResponse : {}", StringUtil.printJson(custChListResponse));
			naverUnLinkVo.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			naverUnLinkVo.setResultMessage(ResultCode.SYSTEM_ERROR.message());
			
			return naverUnLinkVo;			
		}
		
		// 동의 약관 상세 정보 Query
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<String> prcnTcatCdList = new ArrayList<String>();
		paramMap.put("prcnChCd", this.config.getChannelApi(OmniConstants.NAVER_STORE_CHCD, "sellerno", profile)); // 네이버 스마트 스토어 401 채널 interlockSellerNo
		for(CicuedCuTncaTcVo cicuedCuTncaTcVo : cicuehTncListResponse.getCicuedCuTncaTcVo()) {
			prcnTcatCdList.add(cicuedCuTncaTcVo.getTcatCd());
		}
		paramMap.put("prcnTcatCdList", prcnTcatCdList);
		List<NaverMembershipTermsVo> naverMembershipTermsVoList = termsMapper.getNaverMembershipAgreeTerms(paramMap);
		naverUnLinkVo.setNaverMembershipTermsVoList(naverMembershipTermsVoList);
		naverUnLinkVo.setCicuedCuTncaTcVo(cicuehTncListResponse.getCicuedCuTncaTcVo());
		
		CuoptiVo cuoptiVo = new CuoptiVo();
		cuoptiVo.setIncsNo(customer.getIncsNo());
		
		// 고객통합플랫폼에 마케팅정보 수신 동의 조회
		CuoptiResponse cuoptiResponse = customerApiService.getCicuemcuoptiList(cuoptiVo);
		
		if(!"ICITSVCOM999".equals(cuoptiResponse.getRsltCd())) {
			// 네이버 스마트 스토어 채널 마케팅 정보 수신 동의 여부 체크
			// for(CicuemCuOptiQcVo cicuemCuOptiQcVo : cuoptiResponse.getCicuemCuOptiQcVo()) {
				//if(chCd.equals(cicuemCuOptiQcVo.getChCd())) { 
				//	final String smsOptiYn = StringUtils.isEmpty(cicuemCuOptiQcVo.getSmsOptiYn()) ? "N" : cicuemCuOptiQcVo.getSmsOptiYn();
				//	naverUnLinkVo.setSmsOptiYn(smsOptiYn);
				//	naverUnLinkVo.setSmsOptiDt(cicuemCuOptiQcVo.getSmsOptiDt());
				// } 
			// }
			for(CicuemCuOptiQcVo cicuemCuOptiQcVo : cuoptiResponse.getCicuemCuOptiQcVo()) {
				for(String afltChCd : afltChCdList) { // 네이버 스마트 스토어 제휴사 포함 마케팅 수신 동의 체크
					if(cicuemCuOptiQcVo.getChCd().equals(afltChCd)) {
						final String smsOptiYn = StringUtils.isEmpty(cicuemCuOptiQcVo.getSmsOptiYn()) ? "N" : cicuemCuOptiQcVo.getSmsOptiYn();
						cicuemCuOptiQcVo.setSmsOptiYn(smsOptiYn);
						cicuemCuOptiQcVo.setSmsOptiDt(cicuemCuOptiQcVo.getSmsOptiDt());
						afltChCicuemCuOptiQcVoList.add(cicuemCuOptiQcVo);
					}
				}
			}
			naverUnLinkVo.setAfltChCicuemCuOptiQcVoList(afltChCicuemCuOptiQcVoList);
		}
		
		naverUnLinkVo.setIncsNo(customer.getIncsNo());
		naverUnLinkVo.setXincsNo(SecurityUtil.setXyzValue(customer.getIncsNo()));
		naverUnLinkVo.setResultCode(ResultCode.SUCCESS.getCode());
		naverUnLinkVo.setResultMessage(ResultCode.SUCCESS.message());
		
		log.debug("▶▶▶▶▶▶ [Naver membership UnLink process] Naver UnLink VO Result : {}", StringUtil.printJson(naverUnLinkVo));
		
		return naverUnLinkVo;
	}
	
	public boolean retractionTerms(final RetractionVo retractionVo) {
		boolean success = false;
		String incsNo = SecurityUtil.getXValue(retractionVo.getXincsNo());
		String chCd = retractionVo.getChCd();
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		if(OmniConstants.NAVER_STORE_CHCD.equals(chCd)) { // 네이버 스마트 스토어를 통한 약관 철회 시
			if("C".equals(retractionVo.getDataType())) { // [필수] 개인정보 제공 동의 (네이버 → 아모레퍼시픽) 철회
				// 가입된 네이버 스마트 스토어 채널 목록
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
								
								// 고객통합플랫폼에 네이버 스마트 스토어 제휴사 경로 chcsNo 값 조회
								String affiliateSellerKey = this.config.getChannelApi(afltChCd.toString(), "sellerno", profile);
								String affiliateMemberIdNo = SecurityUtil.getEncodedSHA512Password(incsNo);
								String interlockMemberIdNo = cicuedCuChQcVo.getChcsNo();
								
								if(custChResponse == null || !"ICITSVCOM000".equals(custChResponse.getRsltCd())) {
									return false;
								} else {
									// 네이버 스마트 스토어에 NIF-0004 연동정보삭제 API API 호출
									NaverMembershipResponse naverMembershipResponse = customerApiService.deleteNaverMembership(affiliateSellerKey, interlockMemberIdNo, affiliateMemberIdNo);	
								}
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
							
							// 고객통합플랫폼에 네이버 스마트 스토어 제휴사 경로 chcsNo 값 조회
							String affiliateSellerKey = this.config.getChannelApi(chCd, "sellerno", profile);
							String affiliateMemberIdNo = SecurityUtil.getEncodedSHA512Password(incsNo);
							String interlockMemberIdNo = cicuedCuChQcVo.getChcsNo();
							
							if(custChResponse == null || !"ICITSVCOM000".equals(custChResponse.getRsltCd())) {
								return false;
							} else {
								// 네이버 스마트 스토어에 NIF-0004 연동정보삭제 API API 호출
								NaverMembershipResponse naverMembershipResponse = customerApiService.deleteNaverMembership(affiliateSellerKey, interlockMemberIdNo, affiliateMemberIdNo);	
							}
						}
					}
				}
				
				// 네이버 스마트 스토어를 통한 필수 약관 철회 시 제휴사 약관도 철회 처리
				List<NaverMembershipTermsVo> naverMembershipOptionalTerms = termsMapper.getNaverMembershipAffiliateTerms(afltChCdList);
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
				
				return true;
			} else if("CO".equals(retractionVo.getDataType())) { // [선택] 이벤트/프로모션 안내 철회
				
				// 고객통합플랫폼에 050 약관 철회 API 호출
				CustTncaRequest custTncaRequest = new CustTncaRequest();
				List<CustTncaVo> custTncaVos = new ArrayList<>();
				
				CustTncaVo terms = new CustTncaVo();
				terms.setTcatCd("050");
				terms.setIncsNo(incsNo);
				terms.setTncvNo("1.0");
				terms.setTncAgrYn("N");
				terms.setLschId("OCP");
				terms.setChgChCd(chCd);
				terms.setChCd(chCd);
				custTncaVos.add(terms);
				
				CustTncaVo custTncaVoArr[] = custTncaVos.toArray(new CustTncaVo[custTncaVos.size()]);
				custTncaRequest.setCicuedCuTncaTcVo(custTncaVoArr);
				ApiResponse apiResponse = customerApiService.savecicuedcutnca(custTncaRequest);
				
				if(apiResponse == null || !"ICITSVCOM000".equals(apiResponse.getRsltCd())) {
					return false;
				} else {
					// 401 채널 SMS 수신동의 철회
					CustMarketingRequest custMarketingRequest = new CustMarketingRequest();
					List<CustMarketingVo> custMarketingVos = new ArrayList<>();
					
					CustMarketingVo marketing = new CustMarketingVo();
					marketing.setChCd(chCd);
					marketing.setIncsNo(incsNo);
					marketing.setEmlOptiYn("N");
					marketing.setSmsOptiYn("N");
					marketing.setDmOptiYn("N");
					marketing.setTmOptiYn("N");
					marketing.setKkoIntlOptiYn("N");
					marketing.setFscrId("OCP");
					marketing.setLschId("OCP");
					custMarketingVos.add(marketing);
					
					CustMarketingVo cicuemCuOptiTcVoArr[] = custMarketingVos.toArray(new CustMarketingVo[custMarketingVos.size()]);
					custMarketingRequest.setCicuemCuOptiTcVo(cicuemCuOptiTcVoArr);
					apiResponse = customerApiService.savecicuemcuoptilist(custMarketingRequest);
					
					return true;
				}
			} else if("smsOptiYn".equals(retractionVo.getDataType())) { // [선택] 광고성 정보 SMS 수신 동의
				// 고객통합플랫폼에 네이버 스마트스토어 SMS 수신 동의 업데이트 API 호출
				CustMarketingRequest custMarketingRequest = new CustMarketingRequest();
				List<CustMarketingVo> custMarketingVos = new ArrayList<>();
				CustMarketingVo marketing = new CustMarketingVo();
				marketing.setChCd(chCd);
				marketing.setIncsNo(incsNo);
				marketing.setEmlOptiYn("N");
				marketing.setSmsOptiYn("N");
				marketing.setDmOptiYn("N");
				marketing.setTmOptiYn("N");
				marketing.setKkoIntlOptiYn("N");
				marketing.setFscrId("OCP");
				marketing.setLschId("OCP");
				custMarketingVos.add(marketing);
				
				CustMarketingVo cicuemCuOptiTcVoArr[] = custMarketingVos.toArray(new CustMarketingVo[custMarketingVos.size()]);
				custMarketingRequest.setCicuemCuOptiTcVo(cicuemCuOptiTcVoArr);
				ApiResponse apiResponse = customerApiService.savecicuemcuoptilist(custMarketingRequest);
				
				if(apiResponse == null || !"ICITSVCOM000".equals(apiResponse.getRsltCd())) {
					return false;
				} else {
					return true;
				}				
			}
		} else {
			// 네이버 스마트 스토어 제휴사일 경우 별도 처리
			for(Object afltChCd : config.getNaverAfltChannelCodes()) {
				if(chCd.equals(afltChCd.toString())) {
					if("O".equals(retractionVo.getDataType())) { // [필수] 개인정보 제공 동의 (제휴사 → AP) 철회
						// 고객통합플랫폼에 네이버 스마트 스토어 제휴사 경로 chcsNo 값 조회
						String affiliateSellerKey = this.config.getChannelApi(chCd, "sellerno", profile);
						String affiliateMemberIdNo = "";
						String interlockMemberIdNo = "";
						
						CustInfoVo custInfoVo = new CustInfoVo();
						custInfoVo.setIncsNo(incsNo);
						
						CustChListResponse custChListResponse = customerApiService.getCustChList(custInfoVo);
						
						for(CicuedCuChQcVo cicuedCuChQcVo : custChListResponse.getCicuedCuChQcVo()) {
							if(chCd.equals(cicuedCuChQcVo.getChCd())) {
								affiliateMemberIdNo = SecurityUtil.getEncodedSHA512Password(incsNo);
								interlockMemberIdNo = cicuedCuChQcVo.getChcsNo();
							}
						}
						
						// 고객통합플랫폼에 약관 철회 API 호출 (필수 약관 철회 시 전체 약관 철회)
						List<String> afltChCdList = Arrays.asList(chCd);
						List<NaverMembershipTermsVo> naverMembershipOptionalTerms = termsMapper.getNaverMembershipAffiliateTerms(afltChCdList);
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

						// 고객통합플랫폼에 네이버 스마트스토어 SMS 수신 동의 업데이트 API 호출
						CustMarketingRequest custMarketingRequest = new CustMarketingRequest();
						List<CustMarketingVo> custMarketingVos = new ArrayList<>();
						CustMarketingVo marketing = new CustMarketingVo();
						marketing.setChCd(chCd);
						marketing.setIncsNo(incsNo);
						marketing.setEmlOptiYn("N");
						marketing.setSmsOptiYn("N");
						marketing.setDmOptiYn("N");
						marketing.setTmOptiYn("N");
						marketing.setKkoIntlOptiYn("N");
						marketing.setFscrId("OCP");
						marketing.setLschId("OCP");
						custMarketingVos.add(marketing);
						
						CustMarketingVo cicuemCuOptiTcVoArr[] = custMarketingVos.toArray(new CustMarketingVo[custMarketingVos.size()]);
						custMarketingRequest.setCicuemCuOptiTcVo(cicuemCuOptiTcVoArr);
						ApiResponse apiResponse = customerApiService.savecicuemcuoptilist(custMarketingRequest);
						
						if(apiResponse == null || !"ICITSVCOM000".equals(apiResponse.getRsltCd())) {
							return false;
						} else {
							// 고객통합플랫폼에 제휴사 경로 삭제
							DeleteCustChRequest deleteCustChRequest = new DeleteCustChRequest();
							com.amorepacific.oneap.common.vo.api.DeleteCustChRequest.CicuedCuChTcVo cicuedCuChTcVo = new com.amorepacific.oneap.common.vo.api.DeleteCustChRequest.CicuedCuChTcVo();
							
							// Mandatory
							cicuedCuChTcVo.setIncsNo(incsNo); // 필수
							cicuedCuChTcVo.setChCd(chCd); // 필수
							
							deleteCustChRequest.addCicuedCuChTcVo(cicuedCuChTcVo);
							
							CustChResponse custChResponse = customerApiService.deleteCustChannelMember(deleteCustChRequest);
							
							if(custChResponse == null || !"ICITSVCOM000".equals(custChResponse.getRsltCd())) {
								return false;
							} else {
								// 네이버 스마트 스토어에 NIF-0004 연동정보삭제 API API 호출
								NaverMembershipResponse naverMembershipResponse = customerApiService.deleteNaverMembership(affiliateSellerKey, interlockMemberIdNo, affiliateMemberIdNo);	
								
								return true;
							}
						}
						
					} else if("N".equals(retractionVo.getDataType())) { // [선택] AP → 제휴사 안내 철회
						
						// 고객통합플랫폼에 약관 철회 API 호출
						CustTncaRequest custTncaRequest = new CustTncaRequest();
						List<CustTncaVo> custTncaVos = new ArrayList<>();
						
						CustTncaVo terms = new CustTncaVo();
						terms.setTcatCd(retractionVo.getTcatCd());
						terms.setIncsNo(incsNo);
						terms.setTncvNo("1.0");
						terms.setTncAgrYn("N");
						terms.setLschId("OCP");
						terms.setChgChCd(chCd);
						terms.setChCd(chCd);
						custTncaVos.add(terms);
						
						CustTncaVo custTncaVoArr[] = custTncaVos.toArray(new CustTncaVo[custTncaVos.size()]);
						custTncaRequest.setCicuedCuTncaTcVo(custTncaVoArr);
						ApiResponse apiResponse = customerApiService.savecicuedcutnca(custTncaRequest);
						
						// 고객통합플랫폼에 네이버 스마트스토어 SMS 수신 동의 업데이트 API 호출
						CustMarketingRequest custMarketingRequest = new CustMarketingRequest();
						List<CustMarketingVo> custMarketingVos = new ArrayList<>();
						CustMarketingVo marketing = new CustMarketingVo();
						marketing.setChCd(chCd);
						marketing.setIncsNo(incsNo);
						marketing.setEmlOptiYn("N");
						marketing.setSmsOptiYn("N");
						marketing.setDmOptiYn("N");
						marketing.setTmOptiYn("N");
						marketing.setKkoIntlOptiYn("N");
						marketing.setFscrId("OCP");
						marketing.setLschId("OCP");
						custMarketingVos.add(marketing);
						
						CustMarketingVo cicuemCuOptiTcVoArr[] = custMarketingVos.toArray(new CustMarketingVo[custMarketingVos.size()]);
						custMarketingRequest.setCicuemCuOptiTcVo(cicuemCuOptiTcVoArr);
						apiResponse = customerApiService.savecicuemcuoptilist(custMarketingRequest);
						
						if(apiResponse == null || !"ICITSVCOM000".equals(apiResponse.getRsltCd())) {
							return false;
						} else {
							return true;
						}
					} else if("smsOptiYn".equals(retractionVo.getDataType())) { // [선택] 광고성 정보 SMS 수신 동의
						// 고객통합플랫폼에 네이버 스마트스토어 SMS 수신 동의 업데이트 API 호출
						CustMarketingRequest custMarketingRequest = new CustMarketingRequest();
						List<CustMarketingVo> custMarketingVos = new ArrayList<>();
						CustMarketingVo marketing = new CustMarketingVo();
						marketing.setChCd(chCd);
						marketing.setIncsNo(incsNo);
						marketing.setEmlOptiYn("N");
						marketing.setSmsOptiYn("N");
						marketing.setDmOptiYn("N");
						marketing.setTmOptiYn("N");
						marketing.setKkoIntlOptiYn("N");
						marketing.setFscrId("OCP");
						marketing.setLschId("OCP");
						custMarketingVos.add(marketing);
						
						CustMarketingVo cicuemCuOptiTcVoArr[] = custMarketingVos.toArray(new CustMarketingVo[custMarketingVos.size()]);
						custMarketingRequest.setCicuemCuOptiTcVo(cicuemCuOptiTcVoArr);
						ApiResponse apiResponse = customerApiService.savecicuemcuoptilist(custMarketingRequest);
						
						if(apiResponse == null || !"ICITSVCOM000".equals(apiResponse.getRsltCd())) {
							return false;
						} else {
							return true;
						}				
					}
				}
			}
		}
		
		return success;
	}
}
