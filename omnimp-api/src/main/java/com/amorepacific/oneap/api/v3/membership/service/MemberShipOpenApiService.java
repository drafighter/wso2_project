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
 * Date   	          : 2023. 2. 10..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v3.membership.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.api.common.service.CommonService;
import com.amorepacific.oneap.api.common.service.CommonCustomerApiService;
import com.amorepacific.oneap.api.exception.ApiBusinessException;
import com.amorepacific.oneap.api.v3.membership.mapper.MemberShipOpenApiMapper;
import com.amorepacific.oneap.api.v3.membership.vo.ApUserVo;
import com.amorepacific.oneap.api.v3.membership.vo.ChkApUserResponse;
import com.amorepacific.oneap.api.v3.membership.vo.ChkApUserVo;
import com.amorepacific.oneap.api.v3.membership.vo.LinkMembershipResponse;
import com.amorepacific.oneap.api.v3.membership.vo.LinkMembershipVo;
import com.amorepacific.oneap.api.v3.membership.vo.UnLinkMembershipVo;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.CreateCustChRequest;
import com.amorepacific.oneap.common.vo.api.CreateCustChRequest.CicuemCuOptiTcVo;
import com.amorepacific.oneap.common.vo.api.CustChResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.CustbyChCsNoResponse;
import com.amorepacific.oneap.common.vo.api.DeleteCustChRequest;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v3.membership.service 
 *    |_ MemberShipOpenApiService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 2. 10.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@Service
public class MemberShipOpenApiService {
	
	// 제휴사 회원 가입 URL
	@Value("${omni.auth.endpoint.membershipjoinurl}")
	private String getMembershipJoinUrl;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private CommonCustomerApiService customerApiService;
	
	@Autowired
	private MemberShipOpenApiMapper memberShipOpenApiMapper;
	
	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	public ChkApUserResponse checkApUser(ChkApUserVo chkApUserVo) {
		ChkApUserResponse response = new ChkApUserResponse();
		
		List<Channel> channels = commonService.getChannels();
		List<String> channelCds = new ArrayList<>();
		for (Channel channel : channels) {
			channelCds.add(channel.getChCd());
		}

		if (!channelCds.contains(chkApUserVo.getChCd())) {
			log.error("api.checkApUser.Exception = check invalid channel code ? {}", chkApUserVo.getChCd());			
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		if (!config.isMembershipOpenApi(chkApUserVo.getChCd(), profile)) {
			log.error("api.checkApUser.Exception = check invalid channel code ? {}", chkApUserVo.getChCd());			
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		// 1. memberId 로 고객통합플랫폼에 가입 여부 조회
		CustbyChCsNoResponse custbyChCsNoResponse = customerApiService.getCustbyChCsNo(chkApUserVo.getChCd(), chkApUserVo.getMemberId());
		
		if(custbyChCsNoResponse == null || ResultCode.SYSTEM_ERROR.getCode().equals(custbyChCsNoResponse.getRsltCd())) {
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			
			return response;
		}
		
		if("ICITSVCOM001".equals(custbyChCsNoResponse.getRsltCd()) || "ICITSVCOM002".equals(custbyChCsNoResponse.getRsltCd())) { // 존재하지 않는 사용자 리턴
			response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
			
			return response;
		}
		
		// 2. 리턴된 통합고객번호로 고객통합플랫폼 사용자 조회
		String incsNo = custbyChCsNoResponse.getIncsNo();
		Customer customer = new Customer();
		if(StringUtils.hasText(incsNo)) {
			customer = customerApiService.getCicuemcuInfrByIncsNo(incsNo);
			
			if(customer == null || ResultCode.SYSTEM_ERROR.getCode().equals(customer.getRsltCd())) {
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				
				return response;
			} else if ("ICITSVCOM001".equals(customer.getRsltCd()) || "ICITSVCOM002".equals(customer.getRsltCd())) {
				response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
				
				return response;
			}
		}
		
		// 3. 리턴된 통합고객번호로 옴니회원플랫폼 사용자 조회
		if(StringUtils.hasText(incsNo) && customer != null) {
			ApUserVo apUserVo = ApUserVo.builder().build();
			apUserVo.setXincsNo(SecurityUtil.encryptionAESKey(incsNo));
			apUserVo.setIsChannelJoin("true");
			
			UmOmniUser umOmniUser = memberShipOpenApiMapper.getApUserByIncsNo(incsNo);
			log.info("umOmniUser : {}", StringUtil.printJson(umOmniUser));
			
			if(umOmniUser == null ) { // 옴니에 계정이 존재하지 않는 경우 System Error 리턴
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				
				return response;
			}
			
			if("true".equals(umOmniUser.getAccountDisabled())) {
				apUserVo.setAccountDisabled("true");
				apUserVo.setDisabledDate(umOmniUser.getDisabledDate());
				response.setUserVo(apUserVo);
				response.SetResponseInfo(ResultCode.USER_DISABLED);
				
				return response;
			}
			
			if("Y".equals(umOmniUser.getUmUserDormancy())) {
				apUserVo.setUserDormancy("true");
				apUserVo.setUserName(umOmniUser.getUmUserName());
				apUserVo.setCreateDate(umOmniUser.getCreatedDate());
				response.setUserVo(apUserVo);
				response.SetResponseInfo(ResultCode.USER_DORMANCY);
				
				return response;
			}
			
			apUserVo.setUserName(umOmniUser.getUmUserName());
			apUserVo.setFullName(umOmniUser.getFullName());
			apUserVo.setCreateDate(umOmniUser.getCreatedDate());
			response.setUserVo(apUserVo);
			response.SetResponseInfo(ResultCode.SUCCESS);
		} else {
			response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
		}
		
		return response;
	}
	
	public LinkMembershipResponse linkMembership(LinkMembershipVo linkMembershipVo) {
		LinkMembershipResponse response = new LinkMembershipResponse();
		
		List<Channel> channels = commonService.getChannels();
		List<String> channelCds = new ArrayList<>();
		for (Channel channel : channels) {
			channelCds.add(channel.getChCd());
		}

		if (!channelCds.contains(linkMembershipVo.getChCd())) {
			log.error("api.linkMembership.Exception = check invalid channel code ? {}", linkMembershipVo.getChCd());			
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		if (!config.isMembershipOpenApi(linkMembershipVo.getChCd(), profile)) {
			log.error("api.linkMembership.Exception = check invalid channel code ? {}", linkMembershipVo.getChCd());			
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		// 1. memberId 로 고객통합플랫폼에 가입 여부 조회
		CustbyChCsNoResponse custbyChCsNoResponse = customerApiService.getCustbyChCsNo(linkMembershipVo.getChCd(), linkMembershipVo.getMemberId());
		
		if(custbyChCsNoResponse == null || ResultCode.SYSTEM_ERROR.getCode().equals(custbyChCsNoResponse.getRsltCd())) {
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			
			return response;
		}
		
		// 2. 이미 제휴사에 연동된 사용자 여부 체크
		if("ICITSVCOM000".equals(custbyChCsNoResponse.getRsltCd())) {
			response.SetResponseInfo(ResultCode.MEMBERSHIP_ALREADY_LINKED);
			
			return response;
		}
		
		// 3. 연동되지 않은 사용자의 경우 전달된 CI로 고객통합플랫폼에 사용자 조회
		Customer customer = new Customer();
		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setCiNo(linkMembershipVo.getCiNo());
		
		customer = customerApiService.getCicuemcuInfrList(custInfoVo);
		
		 // 4. CI로 조회 시 존재하지 않는 경우 이름, 생년월일, 휴대폰으로 다시 조회
		if(ResultCode.USER_NOT_FOUND.getCode().equals(customer.getRsltCd())) {
			CustInfoVo newCustInfoVo = new CustInfoVo();
			newCustInfoVo.setCustName(linkMembershipVo.getFullName());
			newCustInfoVo.setAthtDtbr(linkMembershipVo.getBirthDay());
			newCustInfoVo.setCustMobile(linkMembershipVo.getPhone());
			
			customer = customerApiService.getCicuemcuInfrList(newCustInfoVo);
			
		} 
		
		if (ResultCode.SYSTEM_ERROR.getCode().equals(customer.getRsltCd())) {
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			
			return response;
		} else if ("ICITSVBIZ152".equals(customer.getRsltCd())) { // 5. 탈퇴 후 30일이 지나지 않은 경우 재 가입 불가 안내
			response.SetResponseInfo(ResultCode.USER_JOIN_IMPOSSIBLE_30DAYS);
			
			return response;
		} else if (ResultCode.USER_NOT_FOUND.getCode().equals(customer.getRsltCd())) { // 6. 통합 고객이 존재하지 않는 경우 회원 가입 페이지 리턴
			String joinUrl = makeMembershipJoinUrl(linkMembershipVo);
			response.setJoinUrl(joinUrl);
			response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
			
			return response;
		} else if ("Y".equals(customer.getDrccCd()) || "ICITSVBIZ155".equals(customer.getRsltCd())) { // 6. 휴면인 경우 휴면 안내 및 회원 가입 페이지 리턴
			String joinUrl = makeMembershipJoinUrl(linkMembershipVo);
			response.setJoinUrl(joinUrl);
			response.SetResponseInfo(ResultCode.USER_DORMANCY);
			
			return response;
		} else if (!linkMembershipVo.getCiNo().equals(customer.getCiNo())) { // 7. 전달된 CI 값과 고객통합에 저장된 CI 값이 불일치 할 경우 오류 리턴
			response.SetResponseInfo(ResultCode.USER_INVALID);
			
			return response;
		}
		
		if(customer != null && !"0".equals(customer.getIncsNo())) { // 8. 사용자 존재하고 휴면이 아닌 경우 경로 가입 처리
			try {
				final Channel channel = commonService.getChannel(linkMembershipVo.getChCd());
				
				CreateCustChRequest createCustChRequest = buildIntegratedChannelCustomerData(channel, customer.getIncsNo(), linkMembershipVo.getMemberId(), null);
				
				log.debug("▶▶▶▶▶▶ integrated channel customer api data : {}", StringUtil.printJson(createCustChRequest));
				
				CustChResponse custChResponse = customerApiService.createCustChannelMember(createCustChRequest);
				log.debug("▶▶▶▶▶▶ integrated channel {} customer api response : {}", channel.getChCdNm(), StringUtil.printJson(custChResponse));

				// 경로 고객 존재하는 경우도 성공으로 판단
				boolean success = "ICITSVCOM000".equals(custChResponse.getRsltCd()) || "ICITSVBIZ157".equals(custChResponse.getRsltCd());
				
				if(!success) {
					response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				} else {
					String xincsNo = SecurityUtil.encryptionAESKey(customer.getIncsNo());
					response.setXincsNo(xincsNo);
					response.SetResponseInfo(ResultCode.SUCCESS);
				}
			} catch (ApiBusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}
		
		return response;
	}
	
	public ApiBaseResponse unLinkMembership(final UnLinkMembershipVo unLinkMembershipVo) {
		ApiBaseResponse response = new ApiBaseResponse();
		
		List<Channel> channels = commonService.getChannels();
		List<String> channelCds = new ArrayList<>();
		for (Channel channel : channels) {
			channelCds.add(channel.getChCd());
		}

		if (!channelCds.contains(unLinkMembershipVo.getChCd())) {
			log.error("api.unLinkMembership.Exception = check invalid channel code ? {}", unLinkMembershipVo.getChCd());			
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		if (!config.isMembershipOpenApi(unLinkMembershipVo.getChCd(), profile)) {
			log.error("api.unLinkMembership.Exception = check invalid channel code ? {}", unLinkMembershipVo.getChCd());			
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		// 1. memberId 로 고객통합플랫폼에 가입 여부 조회
		CustbyChCsNoResponse custbyChCsNoResponse = customerApiService.getCustbyChCsNo(unLinkMembershipVo.getChCd(), unLinkMembershipVo.getMemberId());
		
		if(custbyChCsNoResponse == null || ResultCode.SYSTEM_ERROR.getCode().equals(custbyChCsNoResponse.getRsltCd())) {
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			
			return response;
		}
		
		// 2. 제휴사에 연동된 사용자 여부 체크
		if(!"ICITSVCOM000".equals(custbyChCsNoResponse.getRsltCd())) {
			response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
			
			return response;
		}
		
		String incsNo = SecurityUtil.decryptionAESKey(unLinkMembershipVo.getXincsNo());
		if(StringUtils.hasText(incsNo) && custbyChCsNoResponse.getIncsNo().equals(incsNo)) { // 3. 전달된 xincsNo 복호화 후 고객통합플랫폼에 저장된 통합고객번호와 일치여부 체크 -> 일치하는 경우만 경로 탈퇴 API 호출
			DeleteCustChRequest deleteCustChRequest = new DeleteCustChRequest();
			com.amorepacific.oneap.common.vo.api.DeleteCustChRequest.CicuedCuChTcVo cicuedCuChTcVo = new com.amorepacific.oneap.common.vo.api.DeleteCustChRequest.CicuedCuChTcVo();
			
			// Mandatory
			cicuedCuChTcVo.setIncsNo(incsNo); // 필수
			cicuedCuChTcVo.setChCd(unLinkMembershipVo.getChCd()); // 필수
			
			deleteCustChRequest.addCicuedCuChTcVo(cicuedCuChTcVo);
			
			CustChResponse custChResponse = customerApiService.deleteCustChannelMember(deleteCustChRequest);
			
			if(custChResponse == null || ResultCode.SYSTEM_ERROR.getCode().equals(custChResponse.getRsltCd())) {
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			} else {
				response.SetResponseInfo(ResultCode.SUCCESS);
			}
			
		} else if (StringUtils.isEmpty(incsNo)) { // xincsNo 복호화 실패 시 오류 리턴
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
		} else { // 통합고객번호 불일치 시 User Not Found 리턴
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
		}
		
		return response;
	}
	
	public String makeMembershipJoinUrl(final LinkMembershipVo linkMembershipVo) {
		String joinUrl = getMembershipJoinUrl;
		
		joinUrl += "?";
		
		return joinUrl;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 경로 가입 시 수신 동의 없는 케이스 처리 (Default N)
	 * 
	 * 
	 * author   : hjw0228
	 * date     : 2021. 06. 10. 오후 4:43:55
	 * </pre>
	 * @param onoffline
	 * @param channel
	 * @param joinRequest
	 * @return
	 */
	public CreateCustChRequest buildIntegratedChannelCustomerData(final Channel channel, final String incsno, final String loginId, final String loginPwd) {
		CreateCustChRequest createCustChRequest = new CreateCustChRequest();

		com.amorepacific.oneap.common.vo.api.CreateCustChRequest.CicuedCuChTcVo cicuedCuChTcVo = new com.amorepacific.oneap.common.vo.api.CreateCustChRequest.CicuedCuChTcVo();
		// Mandatory
		cicuedCuChTcVo.setIncsNo(incsno); // 필수
		cicuedCuChTcVo.setChCd(channel.getChCd()); // 필수

		if (StringUtils.hasText(loginId)) { // 경로에서 별도 관리되는 고객번호
			cicuedCuChTcVo.setChcsNo(loginId); // 필수, 선택한 로그인 아이디, 경로에서 별도 관리되는 고객번호
		} else {
			cicuedCuChTcVo.setChcsNo(incsno);
		}

		if (StringUtils.hasText(loginPwd)) { // 필수, 선택한 로그인 아이디 비밀번호, 웹경로일경우 필수
			if(SecurityUtil.isBase64(loginPwd)) {
					try {
						cicuedCuChTcVo.setUserPwdEc(new String(SecurityUtil.hex(SecurityUtil.base64(loginPwd))));
					} catch (DecoderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						cicuedCuChTcVo.setUserPwdEc(null);
					}
			} else {
				cicuedCuChTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(loginPwd));
			}
		}

		cicuedCuChTcVo.setFstCnttPrtnId(config.getJoinPrtnCode(channel.getChCd())); // 필수, 최초접촉거래처ID
		cicuedCuChTcVo.setPrtnNm(config.getJoinPrtnName(channel.getChCd())); // 필수, 거래처 명

		cicuedCuChTcVo.setFscrId("OCP");
		cicuedCuChTcVo.setLschId("OCP");

		CicuemCuOptiTcVo cicuemCuOptiTcVo = cicuedCuChTcVo.getCicuemCuOptiTcVo();

		cicuemCuOptiTcVo.setChCd(channel.getChCd());
		
		// Mandatory
		cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수
		cicuemCuOptiTcVo.setSmsOptiYn("N"); // 필수 SMS수신동의여부
		cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // 필수 SMS수신동의일자
		cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수 이메일수신동의여부
		cicuemCuOptiTcVo.setDmOptiYn("N"); // 필수 DM수신동의여부
		cicuemCuOptiTcVo.setTmOptiYn("N"); // 필수 TM수신동의여부
		cicuemCuOptiTcVo.setIntlOptiYn("N"); // 필수 알림톡수신동의여부
		cicuemCuOptiTcVo.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
		cicuemCuOptiTcVo.setFscrId("OCP");
		cicuemCuOptiTcVo.setLschId("OCP");
		// Optional
		cicuemCuOptiTcVo.setEmlOptiDt("");
		cicuemCuOptiTcVo.setDmOptiDt("");
		cicuemCuOptiTcVo.setTmOptiDt("");
		cicuemCuOptiTcVo.setIntlOptiDt("");
		cicuemCuOptiTcVo.setKkoIntlOptiDt("");
		cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);	

		createCustChRequest.addCicuedCuChTcVo(cicuedCuChTcVo);
		
		return createCustChRequest;
	}	
}
