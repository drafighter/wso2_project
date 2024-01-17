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
 * Date   	          : 2022. 3. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.membership.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.api.service.ApiEndPoint;
import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustChannelRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustVO;
import com.amorepacific.oneap.auth.api.vo.ivo.CustMarketingRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustMarketingVo;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaRequest;
import com.amorepacific.oneap.auth.api.vo.ivo.CustTncaVo;
import com.amorepacific.oneap.auth.api.vo.ovo.CreateCustChannelJoinResponse;
import com.amorepacific.oneap.auth.api.vo.ovo.CreateCustResponse;
import com.amorepacific.oneap.auth.cert.service.CertService;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.join.service.JoinService;
import com.amorepacific.oneap.auth.join.vo.JoinData;
import com.amorepacific.oneap.auth.join.vo.JoinRequest;
import com.amorepacific.oneap.auth.membership.mapper.MembershipMapper;
import com.amorepacific.oneap.auth.membership.vo.MembershipUserInfo;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipConstants;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipOptiVo;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipParam;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipResponse;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipTermsVo;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipVo;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGAccessTokenRequest;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGAccessTokenResponse;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGCommonResponse;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGMbrLinkResponse;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGUserInfoRequest;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGUserInfoResponse;
import com.amorepacific.oneap.auth.membership.vo.ssg.SSGUserInfoResponse.Support;
import com.amorepacific.oneap.auth.mgmt.service.MgmtService;
import com.amorepacific.oneap.auth.social.handler.SnsAuth;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.LocaleUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.Marketing;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OnOffline;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.CicuedCuChQcVo;
import com.amorepacific.oneap.common.vo.api.CustChListResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.CustbyChCsNoResponse;
import com.amorepacific.oneap.common.vo.api.CustbyChCsNoVo;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.dormancy.DormancyResponse;
import com.amorepacific.oneap.common.vo.kakao.KakaoNoticeRequest;
import com.amorepacific.oneap.common.vo.sns.SnsProfileResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenVo;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.membership.service 
 *    |_ MembershipService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 3. 21.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@Service
public class MembershipService {
	
	@Autowired
	private SnsAuth snsAuth;
	
	@Autowired
	private ApiEndPoint apiEndpoint;
	
	@Autowired
	private CertService certService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private CustomerApiService customerApiService;
	
	@Autowired
	private JoinService joinService;
	
	@Autowired
	private MgmtService mgmtService;
	
	@Autowired
	private MembershipMapper membershipMapper;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();

	public MembershipUserInfo startMembershipProcess(final String chCd, final Map<String, Object> paramMap) throws Exception {
		
		MembershipUserInfo membershipUserInfo = MembershipUserInfo.builder().chCd(chCd).build();
		
		// 뷰티 멤버십 연동 시작 (제휴사별로 분기 처리)
		if(OmniConstants.SSG_CHCD.equals(chCd)) { // SSG
			
			// 1. Access Token 발급
			SSGAccessTokenResponse accessTokenResponse = getSSGAccessToken(chCd, paramMap);
			
			if(ResultCode.SUCCESS.getCode() != accessTokenResponse.getResultCode()) {
				membershipUserInfo.setResultMessage(accessTokenResponse.getResultMessage());
				membershipUserInfo.setResultCode(accessTokenResponse.getResultCode());
				
				return membershipUserInfo;
			}
			
			// 2. 사용자 정보 조회 (SSG 사용자 조회 API)
			String tokenType = accessTokenResponse.getData().getTokenType();
			String accessToken = accessTokenResponse.getData().getAccessToken();
			
			SSGUserInfoResponse userInfoResponse = getSSGUserInfo(chCd, tokenType, accessToken);
			
			if(ResultCode.SUCCESS.getCode() != userInfoResponse.getResultCode()) {
				membershipUserInfo.setResultMessage(userInfoResponse.getResultMessage());
				membershipUserInfo.setResultCode(userInfoResponse.getResultCode());
				
				return membershipUserInfo;
			}
			
			membershipUserInfo.setCiNo(userInfoResponse.getCiNo());
			membershipUserInfo.setPhoneNumber(userInfoResponse.getPhoneNumber());
			membershipUserInfo.setMbrId(userInfoResponse.getData().getMbrId());
			membershipUserInfo.setXmbrId(SecurityUtil.setXyzValue(userInfoResponse.getData().getMbrId()));
			
			// 3. 사용자 정보 조회 (고객통합플랫폼 조회 API)
			CustInfoResponse custInfoResponse = customerApiService.getCustInfoList(userInfoResponse.getCiNo(), userInfoResponse.getName(), userInfoResponse.getPhoneNumber(), null);
			
			if(ResultCode.SUCCESS.getCode() != custInfoResponse.getRsltCd()) {
				membershipUserInfo.setResultMessage(custInfoResponse.getRsltMsg());
				membershipUserInfo.setResultCode(custInfoResponse.getRsltCd());
				
				return membershipUserInfo;
			}
			
			// 4. 옴니회원 플랫폼 사용자 여부 조회
			Customer customers[] = custInfoResponse.getCicuemCuInfQcVo();
			if (customers != null && customers.length > 0) {
				Customer customer = customers[0]; // 중요) 첫번째 데이터가 최신임.
				
				List<UmOmniUser> omniUsers = this.mgmtService.getOmniUserList(Integer.parseInt(customer.getIncsNo()));
				log.info("omniUsers : {}", StringUtil.printJson(omniUsers));
				omniUsers = (omniUsers == null) ? java.util.Collections.emptyList() : omniUsers;
				final int omnicount = omniUsers.size();
				
				if(omnicount > 0) {
					for(UmOmniUser omniUser : omniUsers) {
						final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
						final String accountDormancy = omniUser.getUmUserDormancy(); // 휴면 사용자
						
						if (!StringUtil.isTrue(accountDisabled) && !StringUtil.isTrue(accountDormancy)) { // 탈퇴, 휴면 사용자가 아닌 계정만 추가
							membershipUserInfo.setId(omniUser.getUmUserName());
							membershipUserInfo.setName(customer.getCustNm());
							membershipUserInfo.setIncsNo(customer.getIncsNo());
							membershipUserInfo.setXincsNo(SecurityUtil.setXyzValue(customer.getIncsNo()));
							
							membershipUserInfo.setResultMessage(ResultCode.SUCCESS.message());
							membershipUserInfo.setResultCode(ResultCode.SUCCESS.getCode());
						}
					}
				} else { // 온라인 계정 없는 경우 회원 가입 유도
					membershipUserInfo.setResultMessage(ResultCode.USER_NOT_FOUND.message());
					membershipUserInfo.setResultCode(ResultCode.USER_NOT_FOUND.getCode());
					
					return membershipUserInfo;
				}
				
				if(StringUtils.isEmpty(membershipUserInfo.getId())) { // 온라인 계정 없는 경우 회원 가입 유도
					membershipUserInfo.setResultMessage(ResultCode.USER_NOT_FOUND.message());
					membershipUserInfo.setResultCode(ResultCode.USER_NOT_FOUND.getCode());
					
					return membershipUserInfo;
				}
			}
			
			// 5. 제휴사 멤버십 연동 여부 조회 (고객통합플랫폼 조회 API)
			CustbyChCsNoResponse custbyChCsNoResponse = customerApiService.getCustbyChCsNo(membershipUserInfo.getMbrId(), chCd);
			
			if(ResultCode.SUCCESS.getCode() == custbyChCsNoResponse.getRsltCd()) { // 이미 연결된 정보가 존재할 경우만 리턴
				membershipUserInfo.setResultMessage(ResultCode.MEMBERSHIP_ALREADY_LINKED.message());
				membershipUserInfo.setResultCode(ResultCode.MEMBERSHIP_ALREADY_LINKED.getCode());
				
				return membershipUserInfo;
			}
		}
		
		return membershipUserInfo;
	}
	
	public MembershipUserInfo startMembershipLink(MembershipUserInfo membershipUserInfo) throws Exception {
		
		// 사전 작업 (휴면 복구 후 연동 프로세스 실행 시 사용자 정보 업데이트 필요
		if(StringUtils.isEmpty(membershipUserInfo.getId()) || StringUtils.isEmpty(membershipUserInfo.getIncsNo())) {
			
			// 사용자 정보 조회 (고객통합플랫폼 조회 API)
			CustInfoResponse custInfoResponse = customerApiService.getCustInfoList(membershipUserInfo.getCiNo(), membershipUserInfo.getName(), membershipUserInfo.getPhoneNumber(), null);
			
			if(ResultCode.SUCCESS.getCode() != custInfoResponse.getRsltCd()) {
				membershipUserInfo.setResultMessage(custInfoResponse.getRsltMsg());
				membershipUserInfo.setResultCode(custInfoResponse.getRsltCd());
				
				return membershipUserInfo;
			}
			
			// 4. 옴니회원 플랫폼 사용자 여부 조회
			Customer customers[] = custInfoResponse.getCicuemCuInfQcVo();
			if (customers != null && customers.length > 0) {
				Customer customer = customers[0]; // 중요) 첫번째 데이터가 최신임.
				
				List<UmOmniUser> omniUsers = this.mgmtService.getOmniUserList(Integer.parseInt(customer.getIncsNo()));
				log.info("omniUsers : {}", StringUtil.printJson(omniUsers));
				omniUsers = (omniUsers == null) ? java.util.Collections.emptyList() : omniUsers;
				final int omnicount = omniUsers.size();
				
				if(omnicount > 0) {
					for(UmOmniUser omniUser : omniUsers) {
						final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
						final String accountDormancy = omniUser.getUmUserDormancy(); // 휴면 사용자
						
						if (!StringUtil.isTrue(accountDisabled) && !StringUtil.isTrue(accountDormancy)) { // 탈퇴, 휴면 사용자가 아닌 계정만 추가
							membershipUserInfo.setId(omniUser.getUmUserName());
							membershipUserInfo.setName(customer.getCustNm());
							membershipUserInfo.setIncsNo(customer.getIncsNo());
							membershipUserInfo.setXincsNo(SecurityUtil.setXyzValue(customer.getIncsNo()));
							
							membershipUserInfo.setResultMessage(ResultCode.SUCCESS.message());
							membershipUserInfo.setResultCode(ResultCode.SUCCESS.getCode());
						}
					}
				} else { // 온라인 계정 없는 경우 회원 가입 유도
					membershipUserInfo.setResultMessage(ResultCode.USER_NOT_FOUND.message());
					membershipUserInfo.setResultCode(ResultCode.USER_NOT_FOUND.getCode());
					
					return membershipUserInfo;
				}
				
				if(StringUtils.isEmpty(membershipUserInfo.getId())) { // 온라인 계정 없는 경우 오류 리턴
					membershipUserInfo.setResultMessage(ResultCode.USER_NOT_FOUND.message());
					membershipUserInfo.setResultCode(ResultCode.USER_NOT_FOUND.getCode());
					
					return membershipUserInfo;
				}
			}
		}	
		
		// 1. 고객통합플랫폼 경로 가입 API 호출
		final Channel channel = commonService.getChannel(membershipUserInfo.getChCd());
		log.debug("고객통합 뷰티포인트 멤버십 경로 등록 API {} : {}", membershipUserInfo.getChCd(), membershipUserInfo.getId());
		
		UmOmniUser UmOmniUser = this.mgmtService.getOmniUserByLoginUserName(membershipUserInfo.getId());
		
		CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedChannelCustomerData(channel, membershipUserInfo.getIncsNo(), membershipUserInfo.getMbrId(), UmOmniUser.getUmUserPassword());
		
		log.debug("▶▶▶▶▶▶ integrated channel customer api data : {}", StringUtil.printJson(chCustRequest));

		CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
		log.debug("▶▶▶▶▶▶ integrated channel {} customer api response : {}", channel.getChCdNm(), StringUtil.printJson(chjoinResponse));

		// 경로 고객 존재하는 경우도 성공으로 판단
		boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse.getRsltCd());
		
		// ICITSVCOM000 일 경우만 세션에 성공 여부 저장하여 SSG API 호출 오류 시 망 취소 처리
		if("ICITSVCOM000".equals(chjoinResponse.getRsltCd())) {
			WebUtil.setSession(OmniConstants.IS_CREATE_CUST_CHANNEL_MEMBER, "true");
		} else {
			WebUtil.removeSession(OmniConstants.IS_CREATE_CUST_CHANNEL_MEMBER); // ICITSVCOM000 가 아닌 경우 세션에 성공 여부 삭제하여 SSG API 호출 오류 시 망 취소 처리하지 않음
		}

		log.info("▶▶▶▶▶▶ integrated channel result : {} ---> {}", chjoinResponse.getRsltCd(), success);
		
		if(!success) {
			membershipUserInfo.setResultMessage(ResultCode.SYSTEM_ERROR.message());
			membershipUserInfo.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			
			return membershipUserInfo;
		}
		
		// 2. SSG 멤버십 회원 연동 요청
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		membershipUserInfo.setApiKey(this.apiEndpoint.getChannelApiKey(membershipUserInfo.getChCd(), profile));
		membershipUserInfo.setMembershipId(config.getMembershipId(membershipUserInfo.getChCd(), profile));
		
		SSGMbrLinkResponse ssgMbrLinkResponse = customerApiService.setSSGMembershipLink(membershipUserInfo);
		
		boolean isCreateCustChannelMember = StringUtil.isTrue((String) WebUtil.getSession(OmniConstants.IS_CREATE_CUST_CHANNEL_MEMBER));
		
		if(HttpStatus.OK.value() != ssgMbrLinkResponse.getStatus() && isCreateCustChannelMember) { // SSG 멤버십 회원 연동 실패 시 고객통합플랫폼 경로 가입 망취소 API 호출
			chCustRequest.setJoinCnclYn("Y");
			
			log.debug("▶▶▶▶▶▶ integrated channel customer api data : {}", StringUtil.printJson(chCustRequest));

			CreateCustChannelJoinResponse chjoinCnclResponse = customerApiService.createCustChannelMember(chCustRequest);
			log.debug("▶▶▶▶▶▶ integrated channel {} customer api response : {}", channel.getChCdNm(), StringUtil.printJson(chjoinCnclResponse));

			membershipUserInfo.setResultMessage(ResultCode.SYSTEM_ERROR.message());
			membershipUserInfo.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			
			return membershipUserInfo;
		}
		
		membershipUserInfo.setResultMessage(ResultCode.SUCCESS.message());
		membershipUserInfo.setResultCode(ResultCode.SUCCESS.getCode());
		
		return membershipUserInfo; 
	}
	
	public SSGAccessTokenResponse getSSGAccessToken(final String chCd, final Map<String, Object> paramMap) {
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		// 1. Access Token 발급 Request 생성
		SSGAccessTokenRequest accessTokenRequest = SSGAccessTokenRequest.builder()
				.chCd(chCd)
				.apiKey(this.apiEndpoint.getChannelApiKey(chCd, profile))
				.authCode((String) paramMap.get("authCode"))
				.build();
		log.debug("▶▶▶▶▶▶ [SSG Get AccessToken Request Parameter : {}", StringUtil.printJson(accessTokenRequest));
		
		// 2. Access Token 발급 API 호출
		SSGAccessTokenResponse accessTokenResponse = customerApiService.getSSGAccessToken(accessTokenRequest);
		log.debug("▶▶▶▶▶▶ [SSG Get AccessToken Response Object : {}", StringUtil.printJson(accessTokenResponse));
		
		// 3. Access Token 검증
		if(HttpStatus.OK.value() != accessTokenResponse.getStatus()) {
			accessTokenResponse.setResultMessage(ResultCode.SYSTEM_ERROR.message());
			accessTokenResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			
			return accessTokenResponse;
		}
		
		if(accessTokenResponse.getData() == null || StringUtils.isEmpty(accessTokenResponse.getData().getAccessToken())) {
			log.error("▶▶▶▶▶▶ [SSG Get AccessToken is Empty] Response  : {}", StringUtil.printJson(accessTokenResponse));
			
			accessTokenResponse.setResultMessage(ResultCode.SYSTEM_ERROR.message());
			accessTokenResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			
			return accessTokenResponse;
		}
		
		String current = DateUtil.getCurrentDateString("yyyyMMddHHmmss");
		
		if(accessTokenResponse.getData() != null && accessTokenResponse.getData().getExpireDate() != null 
				&& Long.parseLong(current) > Long.parseLong(accessTokenResponse.getData().getExpireDate())) {
			log.error("▶▶▶▶▶▶ [SSG Get AccessToken is Expired] Response  : {}", StringUtil.printJson(accessTokenResponse));
			
			
			accessTokenResponse.setResultMessage(ResultCode.EXPIRED_ACCESS_TOKEN.message());
			accessTokenResponse.setResultCode(ResultCode.EXPIRED_ACCESS_TOKEN.getCode());
			
			return accessTokenResponse;				
		}
		
		accessTokenResponse.setResultMessage(ResultCode.SUCCESS.message());
		accessTokenResponse.setResultCode(ResultCode.SUCCESS.getCode());
		
		return accessTokenResponse;
	}
	
	public SSGUserInfoResponse getSSGUserInfo(final String chCd, final String tokenType, final String accessToken) throws Exception {
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		// 1. UserInfo Request 생성
		SSGUserInfoRequest userInfoRequest = SSGUserInfoRequest.builder()
				.chCd(chCd)
				.apiKey(this.apiEndpoint.getChannelApiKey(chCd, profile))
				.tokenType(tokenType)
				.accessToken(accessToken)
				.build();
		log.debug("▶▶▶▶▶▶ [SSG Get UserInfo Request Parameter : {}", StringUtil.printJson(userInfoRequest));
		
		// 2. UserInfo 조회 API 호출
		SSGUserInfoResponse userInfoResponse = customerApiService.getSSGUserInfo(userInfoRequest);
		log.debug("▶▶▶▶▶▶ [SSG Get UserInfo Response Object : {}", StringUtil.printJson(userInfoResponse));
		
		// 3. UserInfo 검증
		if(HttpStatus.OK.value() != userInfoResponse.getStatus()) {
			userInfoResponse.setResultMessage(ResultCode.SYSTEM_ERROR.message());
			userInfoResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			
			return userInfoResponse;
		}
		
		if(userInfoResponse.getData() == null || userInfoResponse.getData().getSupportList() == null || StringUtils.isEmpty(userInfoResponse.getData().getMbrId())) {
			log.error("▶▶▶▶▶▶ [SSG Get UserInfo is Empty] Response  : {}", StringUtil.printJson(userInfoResponse));
			
			userInfoResponse.setResultMessage(ResultCode.SYSTEM_ERROR.message());
			userInfoResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
			
			return userInfoResponse;
		}
		
		String name = "";
		String ciNo = "";
		String phoneNumber = "";
		
		String key = config.getChannelApi(chCd, "aeskey", profile);
		String iv = config.getChannelApi(chCd, "iv", profile);
		
		for(Support support : userInfoResponse.getData().getSupportList()) {
			if("NAME".equals(support.getSupportType())) name = support.getSupportValue();
			if("CI_NO".equals(support.getSupportType())) ciNo = support.getSupportValue();
			if("PHONE_NUM".equals(support.getSupportType())) phoneNumber = support.getSupportValue();
		}
		
		if(StringUtils.isEmpty(ciNo)) {
			if(StringUtils.isEmpty(name) || StringUtils.isEmpty(phoneNumber)) {
				log.error("▶▶▶▶▶▶ [SSG Get UserInfo is Empty] Response  : {}", StringUtil.printJson(userInfoResponse));
				
				userInfoResponse.setResultMessage(ResultCode.SYSTEM_ERROR.message());
				userInfoResponse.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				
				return userInfoResponse;
			}
		}
		
		// 4. UserInfo 복호화
		userInfoResponse.setName(SecurityUtil.decryptionAESKey(name, key, iv)); // 복호화 해서 입력
		userInfoResponse.setCiNo(SecurityUtil.decryptionAESKey(ciNo, key, iv)); // 복호화 해서 입력
		userInfoResponse.setPhoneNumber(SecurityUtil.decryptionAESKey(phoneNumber, key, iv)); // 복호화 해서 입력
		
		log.debug("name : {}, ciNo : {}, phoneNumber : {}", userInfoResponse.getName(), userInfoResponse.getCiNo(), userInfoResponse.getPhoneNumber());
		
		userInfoResponse.setResultMessage(ResultCode.SUCCESS.message());
		userInfoResponse.setResultCode(ResultCode.SUCCESS.getCode());
		
		return userInfoResponse;
	}
	
	public NaverMembershipResponse getNaverMembershipUserInfo(final String token) {
		
		// 1. Token은 세션에 저장 (NIF-0002 매핑 연동 시 사용)
		WebUtil.setSession(OmniConstants.NAVER_MEMBERSHIP_TOKEN, token);
				
		// 2. UserInfo 조회 API 호출
		NaverMembershipResponse naverMembershipReponse = customerApiService.getNaverMembershipUserInfo(token);
		
		return naverMembershipReponse;
	}
	
	public boolean isRequiredTermsCheck(final NaverMembershipParam naverMembershipParam) {
		return membershipMapper.isRequiredTermsCheck(naverMembershipParam);
	}
	
	public NaverMembershipVo startNaverMembershipProcess(final Map<String, String> param) throws Exception {
		
		NaverMembershipVo naverMembershipVo = new NaverMembershipVo();
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		StopWatch stopWatch = new StopWatch("SNS(authenticated get action)");
		
		String snsType = "NA";
		boolean reprompt = WebUtil.getSession(OmniConstants.NAVER_REPROMPT) == null ? false : true;
		NaverMembershipResponse userInfoResponse = (NaverMembershipResponse) WebUtil.getSession(OmniConstants.NAVER_MEMBERSHIP_USER_INFO);
		String chCd =  this.getChCdByInterlockSellerNo(userInfoResponse.getContents().getInterlockSellerNo());
		String returnUrl = config.getChannelApi(chCd, "url", profile);
		
		// vo set
		SnsTokenVo snsTokenVo = new SnsTokenVo();
		snsTokenVo.setCode(param.get("code"));
		snsTokenVo.setState(SnsAuth.getNaverState());
		
		String accessToken = "";
		
		stopWatch.start(snsType+ " Token");
		
		// token + profile API call
		SnsTokenResponse snsTokenResponse = customerApiService.getSnsToken(snsType, snsTokenVo);
		
		if(snsTokenResponse == null || snsTokenResponse.getAccessToken() == null) {
			naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_SYSTEM_ERROR.getCode());
			naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_SYSTEM_ERROR.message());
			naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");
			return naverMembershipVo;
		}
		accessToken = snsTokenResponse.getAccessToken();
		snsTokenVo.setAccessToken(accessToken);
		
		stopWatch.stop();
		log.debug(stopWatch.prettyPrint());
		
		// 토큰 발급 실패시 진행 불가
		log.debug("▶▶▶▶▶▶ [SNS AccessToken] = {} ", accessToken);
		if (StringUtils.isEmpty(accessToken)) {
			log.info("▶▶▶▶▶▶ [Get SNS AccessToken Fail]"); // 실패 메시지 보내주면 좋겠다
			naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_SYSTEM_ERROR.getCode());
			naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_SYSTEM_ERROR.message());
			naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");
			return naverMembershipVo;
		}
		WebUtil.setSession(OmniConstants.SNS_ACCESS_TOKEN, accessToken);
		
		stopWatch.start("SNS Profile");
		SnsProfileResponse snsProfileResponse = customerApiService.getSnsProfile(snsType, snsTokenVo);

		stopWatch.stop();
		log.debug(stopWatch.prettyPrint());
		
		// CI 항목이 Null 인 경우 auth_type=reprompt 파라미터 추가하여 네아로 로그인 다시 시도
		if(!reprompt && snsProfileResponse.getResponse().getCi() == null) {
			WebUtil.setSession(OmniConstants.NAVER_REPROMPT, true); // 네아로 무한 루프를 방지하기 위해 세션에 값 저장
			naverMembershipVo.setReturnUrl(snsAuth.getAuthorizeUrl(snsType, chCd) + "&auth_type=reprompt");
			return naverMembershipVo;			
		}
		
		// 이름, 생년월일, 연락처, CI 값 중 누락데이터 존재할 경우 연동실패로 네이버에 리턴 
		if(snsProfileResponse == null || snsProfileResponse.getResponse() == null
				|| StringUtils.isEmpty(snsProfileResponse.getResponse().getName()) || StringUtils.isEmpty(snsProfileResponse.getResponse().getBirthyear()) || StringUtils.isEmpty(snsProfileResponse.getResponse().getBirthday())
				|| StringUtils.isEmpty(snsProfileResponse.getResponse().getMobile()) || StringUtils.isEmpty(snsProfileResponse.getResponse().getCi()) || StringUtils.isEmpty(snsProfileResponse.getResponse().getGender())) {
			naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_INVALID_PARAM.getCode());
			naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_INVALID_PARAM.message());
			naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");
			return naverMembershipVo;
		}
		
		String name = snsProfileResponse.getResponse().getName();
		String birthday = snsProfileResponse.getResponse().getBirthyear().concat(snsProfileResponse.getResponse().getBirthday().replaceAll("-", ""));
		String phone = snsProfileResponse.getResponse().getMobile().replaceAll("-", "");
		String ciNo = snsProfileResponse.getResponse().getCi();
		
		// 14세 미만인 경우 가입 제한
		if (DateUtil.isJoinRestrictByAuth(birthday)) { // brith = YYYYMMDD
			log.info("▶▶▶▶▶▶ [Naver membership process] Customer restrict age : {}", birthday);
			naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_RESTRICT_AGE.getCode());
			naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_RESTRICT_AGE.message());
			naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");
			return naverMembershipVo;
		}
		
		// 1. CI로 고객통합플랫폼 조회
		CustInfoResponse custInfoResponse = customerApiService.getCustInfoList(ciNo, name, phone, birthday);
		
		if(custInfoResponse == null || ResultCode.SYSTEM_ERROR.getCode().equals(custInfoResponse.getRsltCd())) { // 오류 발생 시 오류 리턴
			log.info("▶▶▶▶▶▶ [Naver membership process] Customer List API Error. custInfoResponse : {}", StringUtil.printJson(custInfoResponse));
			naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_SYSTEM_ERROR.getCode());
			naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_SYSTEM_ERROR.message());
			naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");
			return naverMembershipVo;
		} else if(ResultCode.USER_NOT_FOUND.getCode().equals(custInfoResponse.getRsltCd())) { // CI 불일치 → 이름, 생년월일, 휴대폰으로 다시 조회
			custInfoResponse = customerApiService.getCustInfoList(null, name, phone, birthday);
			
			if(custInfoResponse == null || ResultCode.SYSTEM_ERROR.getCode().equals(custInfoResponse.getRsltCd())) { // 오류 발생 시 오류 리턴
				log.info("▶▶▶▶▶▶ [Naver membership process] Customer List API Error. custInfoResponse : {}", StringUtil.printJson(custInfoResponse));
				naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_SYSTEM_ERROR.getCode());
				naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_SYSTEM_ERROR.message());
				naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");
				return naverMembershipVo;
			}
		}
		Customer customer = custInfoResponse.getCicuemCuInfTcVo() == null || custInfoResponse.getCicuemCuInfTcVo().length == 0 ? new Customer() : custInfoResponse.getCicuemCuInfTcVo()[0];
		
		boolean success = false;
		
		if(ResultCode.USER_DORMANCY.getCode().equals(custInfoResponse.getRsltCd()) || "Y".equalsIgnoreCase(customer.getDrccCd())) { // 휴면 고객일 경우 휴면 복구 후 후속 프로세스 진행
			log.info("▶▶▶▶▶▶ [Naver membership process] Customer is dormancy : {}", customer.getDrccCd());
			DormancyResponse dormancyResponse = joinService.releaseDormancyCustomerName1(customer.getIncsNo(), chCd);
			
			if (dormancyResponse != null) {
				String rtnCode = dormancyResponse.getRESPONSE().getHEADER().getRTN_CODE();
				rtnCode = StringUtils.isEmpty(rtnCode) ? dormancyResponse.getRESPONSE().getHEADER().getRTN_TYPE() : rtnCode;
				
				if (rtnCode.equals(OmniConstants.SEND_SMS_EAI_SUCCESS)) {
					// 휴면 복구 성공 시 API 다시 조회
					custInfoResponse = customerApiService.getCustInfoList(ciNo, name, phone, birthday);
					
					customer = custInfoResponse.getCicuemCuInfTcVo() == null || custInfoResponse.getCicuemCuInfTcVo().length == 0 ? new Customer() : custInfoResponse.getCicuemCuInfTcVo()[0];

				} else if (rtnCode.equals("E")  // 휴면 복구 시 에러 메시지 [RA-01403: no data found] : 휴면 DB에 사용자 없는 경우 오류 리턴
						&& dormancyResponse.getRESPONSE().getHEADER().getRTN_MSG().equals("[ORA-01403: no data found]")) {
						naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_SYSTEM_ERROR.getCode());
						naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_SYSTEM_ERROR.message());
						naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");
						return naverMembershipVo;
				} else {
					naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_SYSTEM_ERROR.getCode());
					naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_SYSTEM_ERROR.message());
					naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");
					return naverMembershipVo;
				}
			} else {
				naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_SYSTEM_ERROR.getCode());
				naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_SYSTEM_ERROR.message());
				naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");
				return naverMembershipVo;
			}
		} 
		
		if (ResultCode.USER_JOIN_IMPOSSIBLE_30DAYS.getCode().equals(custInfoResponse.getRsltCd()) || "Y".equalsIgnoreCase(customer.getCustWtYn())) { // 탈퇴 후 30일 이내 재 가입인 경우 오류 리턴
			log.info("▶▶▶▶▶▶ [Naver membership process] Customer is withdraw : {}[{}]", customer.getCustWtYn(), customer.getCustWtDttm());
			naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_USER_NOT_FOUND.getCode());
			naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_USER_NOT_FOUND.message());
			naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");
			return naverMembershipVo;
		} else if(ResultCode.USER_NOT_FOUND.getCode().equals(custInfoResponse.getRsltCd())) { // 통합고객이 존재하지 않는 경우 회원 가입 프로세스 진행 후 네이버 스마트 스토어 멤버십 연동 진행
			// 고객통합 플랫폼에 신규 가입 API 호출을 위한 파라미터 생성
			JoinRequest joinRequest = setNaverMembershipJoinRequest(userInfoResponse, snsProfileResponse, customer);
			log.debug("1. 고객통합 등록 API 오프라인 가입 : {}", StringUtil.printJson(joinRequest));
			
			// 고객통합 플랫폼에 신규 가입 API 호출을 위한 Request 생성
			final Channel channel = commonService.getChannel(chCd);
			CreateCustVO createCustVo = JoinData.buildIntegratedOfflineCreateCustomerData(joinRequest, channel);
			log.debug("▶▶▶▶▶▶ integrated channel customer api data : {}", StringUtil.printJson(createCustVo));
			
			// 고객통합 플랫폼에 경로가입 API 호출
			CreateCustResponse custResponse = customerApiService.createCust(createCustVo);
			log.debug("▶▶▶▶▶▶ integrated customer regist response : {}", StringUtil.printJson(custResponse));
			
			// 경로 고객 존재하는 경우도 성공으로 판단
			success = "ICITSVCOM000".equals(custResponse.getRsltCd());
			
			if(!success) {
				naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_SYSTEM_ERROR.getCode());
				naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_SYSTEM_ERROR.message());
				naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");  // 경로 가입 API 호출 실패 시 오류 리턴
				return naverMembershipVo;
			}
			else {
				if (StringUtils.hasText(custResponse.getIncsNo()) && !"0".equals(custResponse.getIncsNo())) {
					joinRequest.setIncsno(custResponse.getIncsNo()); // 등록하면 통합고객번호 생김.
					log.debug("2. 등록하면 통합고객번호 생김 incsno {}", joinRequest.getIncsno());
				}
			}
			
		} else if(ResultCode.SUCCESS.getCode().equals(custInfoResponse.getRsltCd())) { // 통합고객이 존재하는 경우 데이터 검증 후 네이버 스마트 스토어 멤버십 연동 진행
			log.info("▶▶▶▶▶▶ [cert join] 고객통합api조회이름(2차)=네이버인증이름({}={}), {}", customer.getCustNm(), name, customer.getCustNm().equals(name));
			log.info("▶▶▶▶▶▶ [cert join] 고객통합api생년월일(2차)=네이버인증생년월일({}={}), {}", customer.getAthtDtbr(), birthday, customer.getAthtDtbr().equals(birthday));
			log.info("▶▶▶▶▶▶ [cert join] 고객통합api휴대폰(2차)=네이버인증휴대폰({}={}), {}", StringUtil.mergeMobile(customer), phone, StringUtil.mergeMobile(customer).equals(phone));
			if (!customer.getCiNo().equals(ciNo) && customer.getCustNm().equals(name) && customer.getAthtDtbr().equals(birthday) && StringUtil.mergeMobile(customer).equals(phone)) {
				CertResult certResult = new CertResult();
				certResult.setCiNo(ciNo);
				certResult.setName(name);
				certResult.setPhone(phone);
				certResult.setBirth(birthday);
				certService.updateOccupationCi(customer, certResult); // CI 불일치, 이름, 생년월일, 휴대폰 일치하고 점유인증 CI 인 경우 고객통합에 Update API 호출
			}
			
			// 고객통합 플랫폼에 네이버 스마트 스토어(401) 경로 가입 여부 조회
			boolean isNaverLinked = false;
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(customer.getIncsNo());
			CustChListResponse custChListResponse = customerApiService.getCustChList(custInfoVo);
			
			if(custChListResponse == null || custChListResponse.getCicuedCuChQcVo() == null || custChListResponse.getCicuedCuChQcVo().size() == 0 || ResultCode.SYSTEM_ERROR.getCode().equals(custChListResponse.getRsltCd())) { // 오류 발생 시 오류 리턴
				naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_SYSTEM_ERROR.getCode());
				naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_SYSTEM_ERROR.message());
				naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");  // 경로 가입 API 호출 실패 시 오류 리턴
				return naverMembershipVo;
			}
			
			List<CicuedCuChQcVo> cicuedCuChQcVoList = custChListResponse.getCicuedCuChQcVo();
			
			for (CicuedCuChQcVo cicuedCuChQcVo : cicuedCuChQcVoList) { 
				if(!"Y".equals(cicuedCuChQcVo.getDelYn())) {
					if(chCd.equals(cicuedCuChQcVo.getChCd())) { // 401 채널에 가입된 경우 네이버 연동된 상태로 체크
						isNaverLinked = true;
					}
				}
			}
			
			// 고객통합 플랫폼에 경로 가입 API 호출을 위한 파라미터 생성
			JoinRequest joinRequest = setNaverMembershipJoinRequest(userInfoResponse, snsProfileResponse, customer);
			log.debug("1. 고객통합 등록 API 오프라인 가입 : {}", StringUtil.printJson(joinRequest));
			
			// 고객통합 플랫폼에 경로 가입 API 호출을 위한 Request 생성
			final Channel channel = commonService.getChannel(chCd);
			CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedNaverCustomerData(OnOffline.Offline, channel, joinRequest);
			log.debug("▶▶▶▶▶▶ integrated channel customer api data : {}", StringUtil.printJson(chCustRequest));
			
			// 고객통합 플랫폼에 경로가입 API 호출
			CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
			log.debug("▶▶▶▶▶▶ integrated channel {} customer api response : {}", channel.getChCdNm(), StringUtil.printJson(chjoinResponse));
			
			// 경로 고객 존재하는 경우도 성공으로 판단
			success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse.getRsltCd());
			
			if(!success) {
				naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_SYSTEM_ERROR.getCode());
				naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_SYSTEM_ERROR.message());
				naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");  // 경로 가입 API 호출 실패 시 오류 리턴
				return naverMembershipVo;
			}
			
			// 고객통합 플랫폼에 전달된 약관 정보 업데이트
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
				ApiResponse apiResponse = customerApiService.savecicuedcutnca(custTncaRequest);
			}
			
			// 수신 동의 처리
			List<Marketing> joinMarketings = joinRequest.getMarketings();
			if (joinMarketings != null && !joinMarketings.isEmpty()) {
				CustMarketingRequest custMarketingRequest = new CustMarketingRequest();
				List<CustMarketingVo> custMarketingVos = new ArrayList<>();
				for (Marketing joinMarketing : joinMarketings) {
					CustMarketingVo marketing = new CustMarketingVo();
					marketing.setChCd(joinMarketing.getChCd());
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
				ApiResponse apiResponse = customerApiService.savecicuemcuoptilist(custMarketingRequest);
			}
		}
		
		// 고객통합플랫폼에 사용자 정보 조회
		custInfoResponse = customerApiService.getCustInfoList(ciNo, name, phone, birthday);
		customer = custInfoResponse.getCicuemCuInfTcVo() == null || custInfoResponse.getCicuemCuInfTcVo().length == 0 ? new Customer() : custInfoResponse.getCicuemCuInfTcVo()[0];
		
		// 고객통합플랫폼에 신규 가입/경로 가입 성공 시 카카오 알림톡 발송
		// 가입 경로가 401 채널이 아닌 계열사 인 경우 401 채널 카카오 알림톡 발송 후 계열사 알림톡 발송 
		if(!OmniConstants.NAVER_STORE_CHCD.equals(chCd)) {
			final Channel channel = commonService.getChannel(OmniConstants.NAVER_STORE_CHCD);
			ApiBaseResponse apiBaseResponse = this.sendKakaoNotice(customer, channel);
		}
		final Channel channel = commonService.getChannel(chCd);
		ApiBaseResponse apiBaseResponse = this.sendKakaoNotice(customer, channel);

		
		// 카카오 알림톡 발송 후 네이버 스마트 스토어에 NIF-0002 회원연동매핑요청 API 호출
		// 네이버 스마트 스토어에는 통합고객번호 SHA-512 암호화 후 전달
		final String affiliateMemberIdNo = SecurityUtil.getEncodedSHA512Password(customer.getIncsNo());
		final String naverMembershipToken = (String) WebUtil.getSession(OmniConstants.NAVER_MEMBERSHIP_TOKEN);
		final NaverMembershipResponse naverMembershipUserInfo = (NaverMembershipResponse) WebUtil.getSession(OmniConstants.NAVER_MEMBERSHIP_USER_INFO);
		final String interlockSellerNo = naverMembershipUserInfo.getContents().getInterlockSellerNo();
		final String channelUid = this.config.getChannelApi(chCd, "channeluid", profile);
		
		NaverMembershipResponse naverMembershipReponse = customerApiService.setNaverMembershipLink(interlockSellerNo, naverMembershipToken, affiliateMemberIdNo);
		
		if("FAIL".equals(naverMembershipReponse.getOperationResult())) {
			naverMembershipVo.setResultCode(ResultCode.MEMBERSHIP_INVALID_PARAM.getCode());
			naverMembershipVo.setResultMessage(ResultCode.MEMBERSHIP_INVALID_PARAM.message());
			naverMembershipVo.setReturnUrl(returnUrl + "?operationResult=FAIL");  // 경로 가입 API 호출 실패 시 오류 리턴
			return naverMembershipVo;
		}
		
		// 가입 경로가 401 채널이 아닌 계열사 인 경우 401 채널에 대하여 네이버 스마트 스토어에 가입 API 호출
		if(!OmniConstants.NAVER_STORE_CHCD.equals(chCd)) {
			final String naverInterlockSellerNo = this.config.getChannelApi(OmniConstants.NAVER_STORE_CHCD, "sellerno", profile);
			
			naverMembershipReponse = customerApiService.setNaverMembershipLink(naverInterlockSellerNo, naverMembershipToken, affiliateMemberIdNo);
		}
		
		returnUrl += "?token=" + naverMembershipToken + "&channelUid=" + channelUid;
		
		// 2023-10-31 useHomeLanding 파라미터 존재하는 경우 returnUrl에 추가
		NaverMembershipParam naverMembershipParam = (NaverMembershipParam) WebUtil.getSession(OmniConstants.NAVER_MEMBERSHIP_PARAM);
		if(!StringUtils.isEmpty(naverMembershipParam.getUseHomeLanding())) {
			returnUrl += "&useHomeLanding=" + naverMembershipParam.getUseHomeLanding();
		}
		
		log.debug("▶▶▶▶▶▶ [Naver membership process] Complete, Callback URL : {}", returnUrl);
		
		naverMembershipVo.setResultCode(ResultCode.SUCCESS.getCode());
		naverMembershipVo.setResultMessage(ResultCode.SUCCESS.message());
		naverMembershipVo.setReturnUrl(returnUrl);  // 경로 가입 API 호출 실패 시 오류 리턴
		return naverMembershipVo;
	}
	
	public JoinRequest setNaverMembershipJoinRequest(final NaverMembershipResponse userInfoResponse, final SnsProfileResponse snsProfileResponse, final Customer customer) {
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		String name = snsProfileResponse.getResponse().getName();
		String birthday = snsProfileResponse.getResponse().getBirthyear().concat(snsProfileResponse.getResponse().getBirthday().replaceAll("-", ""));
		String phone = snsProfileResponse.getResponse().getMobile().replaceAll("=", "");
		String ciNo = snsProfileResponse.getResponse().getCi();
		String gender = snsProfileResponse.getResponse().getGender();
		String foreign = "N".equals(snsProfileResponse.getResponse().getForeign()) ? "K" : "F";
		String storeNo = userInfoResponse.getContents().getInterlockSellerNo();
		String chCd = membershipMapper.getChCdByInterlockSellerNo(userInfoResponse.getContents().getInterlockSellerNo());
		final Channel channel = commonService.getChannel(chCd);
		
		JoinRequest joinRequest = new JoinRequest();
		
		joinRequest.setUnm(name);
		joinRequest.setGender(gender);
		joinRequest.setPhone(phone);
		joinRequest.setBirth(birthday);
		joinRequest.setCi(ciNo);
		joinRequest.setNational(foreign);
		joinRequest.setChcd(chCd);
		joinRequest.setOffLine(OmniUtil.isOffline(channel));
		if(customer != null && StringUtils.hasText(customer.getIncsNo())) joinRequest.setIncsno(customer.getIncsNo());
		joinRequest.setIntegrateid("false");
		joinRequest.setLoginid(userInfoResponse.getContents().getInterlockMemberIdNo());
		joinRequest.setLoginpassword(userInfoResponse.getContents().getInterlockMemberIdNo());
		
		joinRequest.setJoinPrtnId(config.getJoinPrtnCode(chCd));
		joinRequest.setJoinPrtnNm(config.getJoinPrtnName(chCd));
		
		joinRequest.setPrcnLnkgStorNo(storeNo);
		
		NaverMembershipParam naverMembershipParam = (NaverMembershipParam) WebUtil.getSession(OmniConstants.NAVER_MEMBERSHIP_PARAM);
		log.info("▶▶▶▶▶▶ [Naver membership callback page] naverMembershipParam : {}", StringUtil.printJson(naverMembershipParam));
		
		String[] provisionIds = naverMembershipParam.getProvisionIds();
		String[] optionIds = naverMembershipParam.getOptionIds();
		
		List<NaverMembershipTermsVo> naverMembershipTermsVos = membershipMapper.getNaverMembershipTerms(userInfoResponse.getContents().getInterlockSellerNo()); 
		
		// 약관 컬렉션
		List<Terms> joinBpTerms = new ArrayList<>();
		List<Marketing> joinMarketings = new ArrayList<>();
		boolean isAgree050 = false;		// 050 약관 동의 여부 
		boolean isAgreeAflt = false;	// 계열사의 약관 동의 여부
		boolean isAgreeSms = false;		// 401 채널 문자 수신동의 여부
		boolean isAgreeAfltSms = false; // 계열사 문자 수신동의 여부
		String afltCode = ""; 			// 계열사 약관 동의 코드
		
		NaverMembershipTermsVo optionalTermVo = membershipMapper.getNaverMembershipOptionalTerm(userInfoResponse.getContents().getInterlockSellerNo());
		
		// 약관은 provisionIds, optionsIds 값에서 추출하여 처리
		for(String provisionId : provisionIds) {
			
			for(NaverMembershipTermsVo naverMembershipTermsVo : naverMembershipTermsVos) {
				if(provisionId.equals(naverMembershipTermsVo.getPrcnTncCd())) {
					Terms bpTerm = new Terms();
					bpTerm.setTncAgrYn("Y");
					bpTerm.setTncaChgDt(DateUtil.getCurrentDate());
					bpTerm.setTcatCd(naverMembershipTermsVo.getPrcnTcatCd());
					bpTerm.setTncvNo(naverMembershipTermsVo.getTncvNo());
					bpTerm.setChgChCd(naverMembershipTermsVo.getChCd());
					joinBpTerms.add(bpTerm);
					if("050".equals(naverMembershipTermsVo.getPrcnTcatCd())) { // 수신 동의 체크를 위해 050 약관 체크
						isAgree050 = true;
					}
					if(optionalTermVo != null && optionalTermVo.getPrcnTcatCd().equals(naverMembershipTermsVo.getPrcnTcatCd())) {		// 계열사 수신 동의 체크를 위해 계열사 약관 체크 
						isAgreeAflt = true;
						afltCode = naverMembershipTermsVo.getPrcnTcatCd();
					}
				}
			}
		}
			
		// 선택 약관 체크 해제 처리
		if(!isAgree050) {
			// 050 [선택] 개인정보 수집 및 이용동의
			Terms bpInfoTerm = new Terms();
			bpInfoTerm = new Terms();
			bpInfoTerm.setTncAgrYn("N");
			bpInfoTerm.setTncaChgDt(DateUtil.getCurrentDate());
			bpInfoTerm.setTcatCd("050");
			bpInfoTerm.setTncvNo("1.0");
			bpInfoTerm.setChgChCd(OmniConstants.NAVER_STORE_CHCD);
			joinBpTerms.add(bpInfoTerm);
		} else {
			NaverMembershipOptiVo naverMembershipOptiVo = new NaverMembershipOptiVo();
			naverMembershipOptiVo.setInterlockSellerNo(userInfoResponse.getContents().getInterlockSellerNo());
			naverMembershipOptiVo.setPrcnOptiItemCd("SMS");
			naverMembershipOptiVo.setChCd(OmniConstants.NAVER_STORE_CHCD);
			naverMembershipOptiVo = membershipMapper.getNaverMembershipOpti(naverMembershipOptiVo);
			
			for(String provisionId : provisionIds) {
				// 뷰티포인트 문자 수신 동의 & 경로 문자 수신 동의
				if(provisionId.equals(naverMembershipOptiVo.getUppPrcnOptiTncCd())) {
					for(String optionId : optionIds) {
						if(optionId.equals(naverMembershipOptiVo.getPrcnOptiTncCd())) { // SMS 문자 수신동의까지 체크
							isAgreeSms = true;
							// 뷰티포인트 문자 수신 동의
							Marketing bpMarketing = new Marketing();
							bpMarketing.setChCd("000");
							bpMarketing.setSmsAgree("Y");
							joinMarketings.add(bpMarketing);

							// 401 경로 문자 수신 동의
							Marketing chMarketing = new Marketing();
							chMarketing.setChCd(OmniConstants.NAVER_STORE_CHCD);
							chMarketing.setSmsAgree("Y");
							joinMarketings.add(chMarketing);
						}
					}
				}
			}
		}
		
		// 계열사인 경우 계열사 선택 약관 체크 해제 처리
		if(!chCd.equals(OmniConstants.NAVER_STORE_CHCD) && !isAgreeAflt) {
			// 계열사 [선택] 개인정보 수집 및 이용동의
			if(StringUtils.isEmpty(afltCode)) afltCode = optionalTermVo.getPrcnTcatCd();
			Terms bpInfoTerm = new Terms();
			bpInfoTerm = new Terms();
			bpInfoTerm.setTncAgrYn("N");
			bpInfoTerm.setTncaChgDt(DateUtil.getCurrentDate());
			bpInfoTerm.setTcatCd(afltCode);
			bpInfoTerm.setTncvNo("1.0");
			bpInfoTerm.setChgChCd(chCd);
			joinBpTerms.add(bpInfoTerm);
		} else if(!chCd.equals(OmniConstants.NAVER_STORE_CHCD) && isAgreeAflt) {
			NaverMembershipOptiVo naverMembershipOptiVo = new NaverMembershipOptiVo();
			naverMembershipOptiVo.setInterlockSellerNo(userInfoResponse.getContents().getInterlockSellerNo());
			naverMembershipOptiVo.setPrcnOptiItemCd("SMS");
			naverMembershipOptiVo.setChCd(chCd);
			naverMembershipOptiVo = membershipMapper.getNaverMembershipOpti(naverMembershipOptiVo);
			
			for(String provisionId : provisionIds) {
				// 뷰티포인트 문자 수신 동의 & 경로 문자 수신 동의
				if(provisionId.equals(naverMembershipOptiVo.getUppPrcnOptiTncCd())) {
					for(String optionId : optionIds) {
						if(optionId.equals(naverMembershipOptiVo.getPrcnOptiTncCd())) { // SMS 문자 수신동의까지 체크
							isAgreeAfltSms = true;

							// 계열사 경로 문자 수신 동의
							Marketing chMarketing = new Marketing();
							chMarketing.setChCd(chCd);
							chMarketing.setSmsAgree("Y");
							joinMarketings.add(chMarketing);
						}
					}
				}
			}			
		}
		
		if(!isAgreeSms) {
			// 뷰티포인트 문자 수신 동의
			Marketing bpMarketing = new Marketing();
			bpMarketing.setChCd("000");
			bpMarketing.setSmsAgree("N");
			joinMarketings.add(bpMarketing);

			// 401 경로 문자 수신 동의
			Marketing chMarketing = new Marketing();
			chMarketing.setChCd(OmniConstants.NAVER_STORE_CHCD);
			chMarketing.setSmsAgree("N");
			joinMarketings.add(chMarketing);
		}
		
		if(!chCd.equals(OmniConstants.NAVER_STORE_CHCD) && !isAgreeAfltSms) {
			// 경로 문자 수신 동의
			Marketing chMarketing = new Marketing();
			chMarketing.setChCd(chCd);
			chMarketing.setSmsAgree("N");
			joinMarketings.add(chMarketing);
		}
		
		joinRequest.setBpterms(joinBpTerms);
		joinRequest.setMarketings(joinMarketings);
		
		return joinRequest;
	}
	
	public ApiBaseResponse sendKakaoNotice(final Customer customer, final Channel channel) {
		KakaoNoticeRequest request = KakaoNoticeRequest.builder()
				.ID(this.apiEndpoint.getKakaoNoticeId())
				.STATUS("1")
				.CALLBACK(this.apiEndpoint.getKakaoNoticeCallback())
				.TEMPLATE_CODE(this.apiEndpoint.getKakaoNoticeTemplateCode())
				.FAILED_TYPE(this.apiEndpoint.getKakaoNoticeFailedType())
				.FAILED_SUBJECT(this.apiEndpoint.getKakaoNoticeFailedSubject())
				.PROFILE_KEY(this.apiEndpoint.getKakaoNoticeProfileKey())
				.APPL_CL_CD(this.apiEndpoint.getKakaoNoticeApplClCd())
				.PLTF_CL_CD(this.apiEndpoint.getKakaoNoticePltfClCd()).build();
		
		// 카카오 알림톡 발송될 휴대폰번호 설정 (네아로 휴대전화번호)
		request.setPHONE(StringUtil.mergeMobile(customer));
		
		// 카카오 알림톡 메세지 발송 시간 설정
		request.setREQDATE(DateUtil.getTimestampAfterSecond("yyyy-MM-dd HH:mm:ss", 60));
		
		// 수집 항목은 계열사의 경우 분기 처리
		String category = "401".equals(channel.getChCd()) ? this.messageSource.getMessage("naver.membership.kakao.notice.talk.category", null, LocaleUtil.getLocale()) : this.messageSource.getMessage("naver.membership.kakao.notice.talk.affiliate.category", null, LocaleUtil.getLocale());
		
		// 카카오 알림톡 메세지 설정
		String msg = "안녕하세요, 아모레퍼시픽 뷰티포인트입니다.\r\n"
				+ "\r\n"
				+ "㈜아모레퍼시픽은 " + channel.getChCdNm() + "(으)로부터 고객님의 개인정보를 제공받았으며, 개인정보보호법 제20조에 의거하여 아래와 같이 개인정보 수집 출처를 안내해 드립니다.\r\n"
				+ "\r\n"
				+ "- 개인정보 수집 출처: " + channel.getChCdNm() + "\r\n"
				+ "- 개인정보 수집 항목: " + category + "\r\n"
				+ "- 개인정보 처리 목적: " + this.messageSource.getMessage("naver.membership.kakao.notice.talk.purpose", null, LocaleUtil.getLocale()) + "\r\n"
				+ "- 개인정보 보유 및 이용기간: " + DateUtil.getCurrentDateString("yyyy-MM-dd") + "부터 철회일까지" + "\r\n"
				+ "\r\n"
				+ "개인정보 처리를 원치 않는 경우 아모레퍼시픽 개인정보 처리 동의 철회 페이지(" + this.messageSource.getMessage("naver.membership.kakao.notice.talk.url", null, LocaleUtil.getLocale()) + ")를 통해 개인정보 처리 정지를 요청하실 수 있습니다.";
		request.setMSG(msg);
		request.setFAILED_MSG(msg);
		
		return customerApiService.sendKakaoNoticeTalkEai(request);
	}
	
	public String getChCdByInterlockSellerNo(String interlockSellerNo) {
		return membershipMapper.getChCdByInterlockSellerNo(interlockSellerNo);
	}
}
