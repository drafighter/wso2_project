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
 * Date   	          : 2020. 8. 6..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.mgmt.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.login.vo.Web2AppVo;
import com.amorepacific.oneap.auth.mgmt.mapper.MgmtMapper;
import com.amorepacific.oneap.auth.mgmt.vo.WithdrawUserVo;
import com.amorepacific.oneap.auth.search.vo.SearchData;
import com.amorepacific.oneap.auth.search.vo.SearchResponse;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.UuidUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.IncsRcvData;
import com.amorepacific.oneap.common.vo.api.InitPasswordData;
import com.amorepacific.oneap.common.vo.api.SearchChCustRequest;
import com.amorepacific.oneap.common.vo.api.SearchChCustResponse;
import com.amorepacific.oneap.common.vo.api.UserInfo;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;
import com.amorepacific.oneap.common.vo.user.UserData;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.mgmt.service 
 *    |_ MgmtService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 6.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class MgmtService {

	@Autowired
	private MgmtMapper mgmtMapper;

	@Autowired
	private CustomerApiService customerApiService;

	public List<String> getIntegratedExtraId(final String loginid) {
		return this.mgmtMapper.getIntegratedExtraId(loginid);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 8. 오후 8:14:28
	 * </pre>
	 * 
	 * @param userData
	 * @return
	 */
	public List<UserData> getUserLoginInfoListByLoginId(final UserData userData) {
		return this.mgmtMapper.getUserLoginInfoListByLoginId(userData);
	}
	
	
	
	public List<UserData> getOmniUserDataList(final UserData userData) {
		return this.mgmtMapper.getOmniUserDataList(userData);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 4. 오후 5:38:45
	 * </pre>
	 * 
	 * @param incsNo
	 * @return
	 */
	public List<UmOmniUser> getOmniConversionUserList(final String incsNo) {
		
		if ("0".equals(incsNo)) {
			return Collections.emptyList();
		}
 		
		UmOmniUser umOmniUser = new UmOmniUser();
		umOmniUser.setUmAttrValue(incsNo);
		
		return this.mgmtMapper.getOmniConversionUserList(umOmniUser);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 4. 오후 5:38:49
	 * </pre>
	 * 
	 * @param chCd
	 * @param incsNo
	 * @return
	 */
	public List<UmChUser> getChannelConversionUserList(final String chCd, final String incsNo) {
		
		if (StringUtils.isEmpty(incsNo) || "0".equals(incsNo)) {
			return Collections.emptyList();
		}
		
		UmChUser umChUser = new UmChUser();
		umChUser.setChCd(chCd);
		umChUser.setIncsNo(Integer.parseInt(incsNo));

		return this.mgmtMapper.getChannelConversionUserList(umChUser);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 4. 오후 6:01:14
	 * </pre>
	 * 
	 * @param loginid
	 * @return
	 */
	public List<UmChUser> getChannelUserIdList(final String chCd, final String loginid) {
		UmChUser umChUser = new UmChUser();
		umChUser.setChCd(chCd);
		umChUser.setChcsWebId(loginid);

		return this.mgmtMapper.getChannelUserIdList(umChUser);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 뷰티포인트 고객 로그인을 위한 사용자 정보 조회
	 * author   : takkies
	 * date     : 2020. 8. 19. 오후 7:20:06
	 * </pre>
	 * 
	 * @param loginId
	 * @param loginPwd
	 * @return
	 */
	public List<UmOmniUser> getOmniLoginUserList(final String loginId, final String loginPwd) {

		UmOmniUser umOmniUser = new UmOmniUser();
		umOmniUser.setUmUserName(loginId);
		umOmniUser.setUmUserPassword(SecurityUtil.getEncodedWso2Password(loginPwd));
		umOmniUser.setUmAttrName(OmniConstants.UID);
		umOmniUser.setUmAttrValue(loginId);

		return this.mgmtMapper.getOmniLoginUserList(umOmniUser);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 경로 고객 로그인을 위한 사용자 정보 조회
	 * author   : takkies
	 * date     : 2020. 8. 19. 오후 7:20:10
	 * </pre>
	 * 
	 * @param chCd
	 * @param loginId
	 * @param loginPwd
	 * @return
	 */
	public List<UmChUser> getChannelLoginUserList(final String chCd, final String loginId, final String loginPwd) {
		UmChUser umChUser = new UmChUser();
		umChUser.setChCd(chCd);
		umChUser.setChcsWebId(loginId);
		umChUser.setLinPwdEc(SecurityUtil.getEncodedWso2Password(loginPwd));
		return this.mgmtMapper.getChannelLoginUserList(umChUser);
	}
	
	public List<UmChUser> getChannelLoginUserListByFlag(final String chCd, final String loginId, final String loginPwd) {
		UmChUser umChUser = new UmChUser();
		umChUser.setChCd(chCd);
		umChUser.setChcsWebId(loginId);
		umChUser.setLinPwdEc(SecurityUtil.getEncodedWso2Password(loginPwd));
		return this.mgmtMapper.getChannelLoginUserListByFlag(umChUser);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 옴니 통합고객조회(회원가입, 휴대폰 로그인 시)
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 13. 오후 5:28:01
	 * </pre>
	 * 
	 * @param incsNo
	 * @return
	 */
	public List<UmOmniUser> getOmniUserList(final int incsNo) {
		
		if (incsNo <= 0) {
			return Collections.emptyList();
		}
		
		UmOmniUser umWso2User = new UmOmniUser();
		umWso2User.setUmAttrName(OmniConstants.INCS_NO);
		umWso2User.setUmAttrValue(Integer.toString(incsNo));
		return this.mgmtMapper.getOmniJoinUserList(umWso2User);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 임시 채널고객조회(회원가입, 휴대폰 로그인 시)
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 13. 오후 5:28:06
	 * </pre>
	 * 
	 * @param incsNo
	 * @param chCd
	 * @return
	 */
	public List<UmChUser> getChannelUserList(final int incsNo, final String chCd) {
		
		if (incsNo <= 0) {
			return Collections.emptyList();
		}
		
		UmChUser umChUser = new UmChUser();
		umChUser.setIncsNo(incsNo);
		umChUser.setChCd(chCd);
		return this.mgmtMapper.getChannelJoinUserList(umChUser);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 11. 오후 7:14:23
	 * </pre>
	 * @param loginId
	 * @param loginPwd
	 * @param chCd
	 * @return
	 */
	public List<UmChUser> getChannelTransferUserList(final String loginId, final String loginPwd, final String chCd) {
		UmChUser umChUser = new UmChUser();
		umChUser.setChcsWebId(loginId);
		umChUser.setLinPwdEc(SecurityUtil.getEncodedWso2Password(loginPwd));
		umChUser.setChCd(chCd);
		return this.mgmtMapper.getChannelTransferUserList(umChUser);
	}
	
	/**
	 * 
	 * <pre>
	 * comment  :
	 * 1. 회원 탙퇴(통합 탈퇴, 경로 탈퇴) 시 회원 상태를 탈퇴로 변경하고 30일 간 개인정보 포함 유지 가능함
	 * (현재 APWEBDB는 퇼퇴 시점 분리 보관 후 Row 삭제하고 있으나 그럴 필요는 없음)
	 * ﻿==> 회원 탈퇴 시, SSO DB (UM_USER, UM_USER_ATTRIBUTE) 상에서는 Disabled로 처리하여 로그인 불가 처리
	 * ==> 기존 회원 데이터 마이그레이션 시에는 UM_USER table에만 login Web ID 와 임의의 password를 포함한 값만 migration 처리  
	 *  (탈퇴된 회원의 ID는 재사용이 불가함에 따라, 중복 방지를 위해 UM_USER table에 데이터 생성 필요)
	 * 2. 30일 이후 개인정보는 삭제해야 하고 ID 정보는 삭제하지 않아도 됨
	 * (저희는 탈퇴 시점에 고객명을 지우든(****) 30일 이후 지우든(*****) 상관 없을 거 같습니다)
	 * ==> 30일 이후, 해당 회원에 대한 UM_USER_ATTRIBUTE 테이블 삭제 처리, UM_USER table 내에 Password 값도 Not Null 임에 따라
	 * 임의의 값으로 초기화 처리
	 * ==> 탈퇴일자는 UM_USER_ATTRIBUTE table의 ROW 수가 너무 많을 듯 함에 따라, UM_USER table에 alter 명령어로 탈퇴일자 추가 예정  
	 * author   : takkies
	 * date     : 2020. 8. 11. 오전 9:22:11
	 * </pre>
	 * 
	 * @return
	 */
	public boolean doWithdraw() {
		boolean result = true;
		List<WithdrawUserVo> userlist = this.mgmtMapper.getWithdrawedUserList();

		if (userlist != null && !userlist.isEmpty()) {
			WithdrawUserVo withdrawuser;
			for (WithdrawUserVo user : userlist) {
				log.debug("[withdraw scheule] {} [{}] -----> Period after withdrawal : {}", user.getUmUserName(), user.getUmId(), user.getTerms());

				withdrawuser = new WithdrawUserVo();
				withdrawuser.setUmId(user.getUmId());
				withdrawuser.setUmUserId(user.getUmUserId());
				withdrawuser.setUmUserName(user.getUmUserName());
				withdrawuser.setUmUserPassword(UuidUtil.getUuidByDate());

				// 비밀번호는 그냥 유지하도록하고 플래그 처리만 함.
				// int withdraw = this.mgmtMapper.updateWithdrawedUser(withdrawuser); // 탈퇴 사용자 로그인 방지용 로그인정보 업데이트
				// log.debug("[withdraw scheule] update account login blocking, password reset : {}", withdraw);

				int exist = this.mgmtMapper.existWithdrawFlagUser(withdrawuser);
				log.debug("[withdraw scheule] exist accountDisabled attribute : {}", exist);
				if (exist > 0) {
					int update = this.mgmtMapper.updateWithdrawFlagUser(withdrawuser);
					log.debug("[withdraw scheule] accountDisabled update : {}", update);
				} else {
					int insert = this.mgmtMapper.insertWithdrawFlagUser(withdrawuser);
					log.debug("[withdraw scheule] accountDisabled insert : {}", insert);
				}

			}
		}

		return result;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 21. 오후 5:39:17
	 * </pre>
	 * 
	 * @param userData
	 * @return
	 */
	public ApiBaseResponse updateOmniUserPassword(UserData userData) {

		log.debug("▶▶▶▶▶▶▶▶▶▶▶ [change user password] data : {}", StringUtil.printJson(userData));

		InitPasswordData initPwdVo = new InitPasswordData();
		initPwdVo.setChCd(userData.getChCd());
		initPwdVo.setIncsNo(Integer.parseInt(userData.getIncsNo()));
		initPwdVo.setLoginId(userData.getLoginId());
		initPwdVo.setPassword(userData.getPassword());
		initPwdVo.setMustchange("N");

		ApiBaseResponse response = this.customerApiService.initPassword(initPwdVo);

		log.debug("▶▶▶▶▶▶▶▶▶▶▶ [change user password] response : {}", StringUtil.printJson(response));

		if (response.getResultCode().equals(ResultCode.SUCCESS.getCode())) {
			// 비밀번호 초기화 플래그 리셋
			UmOmniUser omniUser = new UmOmniUser();
			omniUser.setUmUserName(userData.getLoginId()); // 회원아이디
			omniUser.setUmUserPassword(SecurityUtil.getEncodedWso2Password(userData.getPassword())); // 비밀번호(암호화)
			omniUser.setIncsNo(userData.getIncsNo()); // 통합고객번호
			boolean success = this.updateUserPasswordResetFlagInit(omniUser);
			log.debug("▶▶▶▶▶▶▶▶▶▶▶ [change user password] flag reset result : {}", success);
		}

		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 채널 회원 전환 완료 처리
	 * author   : takkies
	 * date     : 2020. 9. 5. 오후 3:52:46
	 * </pre>
	 * 
	 * @param umChUser
	 * @return
	 */
	public boolean updateConversionComplete(UmChUser umChUser) {
		
		if (umChUser.getIncsNo() <= 0) {
			return true;
		}
		
		return this.mgmtMapper.updateConversionComplete(umChUser) > 0;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 20. 오후 9:13:19
	 * </pre>
	 * 
	 * @param umChUser
	 * @return
	 */
	public boolean updateConversionCompleteById(UmChUser umChUser) {
		
		if (umChUser.getIncsNo() <= 0) {
			return true;
		}
		
		return this.mgmtMapper.updateConversionCompleteById(umChUser) > 0;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 통합고객번호로 회원 로그인아이디 조회하기
	 * author   : takkies
	 * date     : 2020. 9. 7. 오후 7:51:50
	 * </pre>
	 * 
	 * @param userData
	 * @return
	 */
	public List<UserData> getOmniUserLoginIdList(final UserData userData) {
		
		if (StringUtils.isEmpty(userData.getIncsNo()) || "0".equals(userData.getIncsNo())) {
			return Collections.emptyList();
		}
		
		return this.mgmtMapper.getOmniUserLoginIdList(userData);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 채널코드로 회원 로그인아이디 조회하기
	 * author   : takkies
	 * date     : 2020. 9. 28. 오후 4:50:52
	 * </pre>
	 * 
	 * @param userData
	 * @return
	 */
	public List<UserData> getChannelUserLoginIdList(final UserData userData) {
		if (StringUtils.isEmpty(userData.getIncsNo()) || "0".equals(userData.getIncsNo())) {
			return Collections.emptyList();
		}
		return this.mgmtMapper.getChannelUserLoginIdList(userData);
	}

	public List<UserData> getChannelUserLoginIdByChId(final UserData userData) {
		return this.mgmtMapper.getChannelUserLoginIdByChId(userData);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 자체고객의 자체회원아이디 조회
	 * 
	 * author   : takkies
	 * date     : 2020. 10. 22. 오후 3:02:15
	 * </pre>
	 * 
	 * @param chCd
	 * @param incsNo
	 * @param sessionEncLoginId
	 * @return
	 */
	public String searchChannelWebId(final String trnsType, final String chCd, final String incsNo, final String sessionEncLoginId) {

		log.debug("▶▶▶▶▶▶ [search channel webid] trnsType : {}, chCd : {}, incsNo : {}, sessionwebid : {}", trnsType, chCd, incsNo, sessionEncLoginId);

		if (StringUtils.hasText(incsNo) && !"0".equals(incsNo)) {
			List<UmChUser> chUsers = getChannelConversionUserList(chCd, incsNo);
			if (chUsers == null || chUsers.isEmpty()) {
				log.debug("▶▶▶▶▶▶ [search channel webid] channel session login id : {}", sessionEncLoginId);
				return sessionEncLoginId;
			} else {
				return chUsers.get(0).getChcsWebId();
			}
		}
		return "";
	}

	/**
	 * 
	 * <pre>
	 * comment  : 고객 휴면 정보 해제하기
	 * author   : takkies
	 * date     : 2020. 9. 8. 오후 2:58:14
	 * </pre>
	 * 
	 * @param omniUser
	 * @return
	 */
	public boolean updateUserDormancyRelease(UmOmniUser omniUser) {
		return this.mgmtMapper.updateUserDormancyRelease(omniUser) > 0;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 13. 오후 4:27:45
	 * </pre>
	 * 
	 * @param omniUser
	 * @return
	 */
	public boolean updateUserPasswordResetFlagInit(UmOmniUser omniUser) {
		return this.mgmtMapper.updateUserPasswordResetFlagInit(omniUser) > 0;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : hkdang
	 * date     : 2020. 9. 23. 오후 2:26:39
	 * </pre>
	 * 
	 * @param snsType
	 * @param incsNo
	 * @return
	 */
	public String getSnsMappingTime(String snsType, String incsNo) {

		SnsParam snsParam = new SnsParam();
		snsParam.setSnsType(snsType);
		snsParam.setIncsNo(incsNo);

		String mappingTime = this.mgmtMapper.getSnsMappingTime(snsParam);
		if (!StringUtils.isEmpty(mappingTime)) {
			mappingTime = mappingTime.substring(0, 10); // YYYY-MM-DD HH:MM:SS 에서 YYYY-MM-DD 만 사용
			mappingTime = mappingTime.replace("-", "."); // YYYY-MM-DD 에서 YYYY.MM.DD 로 변경
		}

		return mappingTime;
	}

	public UmOmniUser getOmniUserByLoginUserName(final String userName) {
		return this.mgmtMapper.getOmniUserByLoginUserName(userName);
	}

	public SearchResponse searchPasswordResult(final CertResult certresult, final String searchPwdId) {

		SearchResponse response = new SearchResponse();
		List<SearchData> searchOmniUsers = new ArrayList<>();
		List<SearchData> searchChannelUsers = new ArrayList<>();
		final String ciNo = certresult.getCiNo();

		// CI로 고객통합정보 조회
		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setCiNo(ciNo);
		CustInfoResponse custResponse = this.customerApiService.getCustList(custInfoVo);
		if (custResponse != null && !"ICITSVBIZ152".equals(custResponse.getRsltCd())) { // 탈퇴회원인 경우 스킵
			Customer customers[] = custResponse.getCicuemCuInfTcVo();
			if (customers != null && customers.length > 0) {

				SearchData searchUser;
				Customer customer = customers[0]; // 가장 최신 사용자

				if (!"ICITSVBIZ152".equals(customer.getRsltCd())) { // 탈퇴 사용자 아닌 경우

					if (StringUtils.hasText(searchPwdId)) { // 검색 ID가 넘어오면 비교해서 찾음
						for (Customer c : customers) {
							if (c.getChcsNo().equals(searchPwdId)) {
								customer = c;
								break;
							}
						}
					}

					log.debug("▶▶▶▶▶▶ [changepassword search] customer api : {}", StringUtil.printJson(customer));

					// + 통합고객번호로 사용자 찾음
					UserData userData = new UserData();
					// userData.setChCd(customer.getChCd());
					userData.setIncsNo(customer.getIncsNo());
					List<UserData> userlist = getOmniUserLoginIdList(userData);
					if (userlist != null && !userlist.isEmpty()) {
						for (UserData userdata : userlist) {
							searchUser = new SearchData();
							searchUser.setLoginId(userdata.getLoginId());
							searchUser.setPassId(userdata.getLoginId());
							searchUser.setMobile(StringUtil.mergeMobile(customer));
							searchUser.setIncsNo(userdata.getIncsNo());
							searchUser.setName(customer.getCustNm());
							log.debug("▶▶▶▶▶▶ [changepassword search] omni user : {}", StringUtil.printJson(searchUser));
							searchOmniUsers.add(searchUser);
						}
						response.setSearchOmniUsers(searchOmniUsers);
					}
				}
			}
		}

		SearchChCustRequest searchCustRequest = new SearchChCustRequest();
		searchCustRequest.setCi(ciNo);
		searchCustRequest.setChCd(certresult.getChCd());
		SearchChCustResponse chCustRespnose = this.customerApiService.getChannelUser(certresult.getChCd(), searchCustRequest);
		if (chCustRespnose != null && (chCustRespnose.getUserInfo() != null && chCustRespnose.getUserInfo().length > 0)) {
			UserInfo userinfos[] = chCustRespnose.getUserInfo();
			log.debug("▶▶▶▶▶▶▶▶▶▶▶ search channel customer api : {}", StringUtil.printJson(userinfos));

			SearchData searchUser;
			UserInfo userinfo = userinfos[0];
			boolean flag = false;

			if (StringUtils.hasText(searchPwdId)) { // 검색 ID가 넘어오면 비교해서 다시 찾음
				for (UserInfo u : userinfos) {
					if (u.getWebId().equals(searchPwdId)) {
						userinfo = u;
						flag = true;
						break;
					}
				}
			}
			
			if(flag) {
				UserData userData = new UserData();
				userData.setChCd(certresult.getChCd());
				userData.setChLoginId(searchPwdId);
				// userData.setIncsNo(Integer.toString(userinfo.getIncsNo())); // 통합고객번호가 없는 경로사용자가 있어서 넘어온ID로 사용자 찾음
				List<UserData> userlist = getChannelUserLoginIdByChId(userData); // ID + 미전환
				if (userlist != null && !userlist.isEmpty()) {
					for (UserData userdata : userlist) {
						searchUser = new SearchData();
						searchUser.setLoginId(userdata.getLoginId());
						searchUser.setPassId(userdata.getLoginId());
						searchUser.setMobile(userinfo.getPhone());
						searchUser.setIncsNo(userdata.getIncsNo());
						searchUser.setName(userinfo.getName());
						log.debug("▶▶▶▶▶▶ [changepassword search] channel user : {}", StringUtil.printJson(searchUser));
						searchChannelUsers.add(searchUser);
					}
					response.setSearchChannelUsers(searchChannelUsers);
				}
			}

		}

		return response;
	}

	public boolean updateChannelPassword(final UserData userData) {

		/*
		if (StringUtils.isEmpty(userData.getIncsNo()) || "0".equals(userData.getIncsNo())) {
			return true;
		}
		*/
		
		if (StringUtils.isEmpty(userData.getLoginId())) {
			return false;
		}
		
		final String password = userData.getPassword();
		if (StringUtils.hasText(password)) {
			userData.setPassword(SecurityUtil.getEncodedWso2Password(password));
			return this.mgmtMapper.updateChannelPassword(userData) > 0;
		}

		return false;
	}

	public boolean hasSameLoginId(final UmChUser chUser) {
		
		if (chUser.getIncsNo() <= 0) {
			return true;
		}
		
		return this.mgmtMapper.hasSameLoginId(chUser) > 0;
	}

	public boolean hasSameLoginIdByLoginId(final UmChUser chUser) {
		return this.mgmtMapper.hasSameLoginIdByLoginId(chUser) > 0;
	}

	public boolean existRcvData(final IncsRcvData incsRcvData) {
		if (incsRcvData.getIncsNo() <= 0) {
			return false;
		}
		
		return this.mgmtMapper.existRcvData(incsRcvData) > 0;
	}
	
	public boolean insertRcvName(final IncsRcvData incsRcvData) {
		
		if (incsRcvData.getIncsNo() <= 0) {
			return true;
		}
		
		return this.mgmtMapper.insertRcvName(incsRcvData) > 0;
	}
	
	public boolean updateRcvName(final IncsRcvData incsRcvData) {
		if (incsRcvData.getIncsNo() <= 0) {
			return true;
		}
		
		return this.mgmtMapper.updateRcvName(incsRcvData) > 0;
	}
	
	public boolean insertRcvDormancy(final IncsRcvData incsRcvData) {
		
		if (incsRcvData.getIncsNo() <= 0) {
			return true;
		}
		
		return this.mgmtMapper.insertRcvDormancy(incsRcvData) > 0;
	}
	
	public boolean updateRcvDormancy(final IncsRcvData incsRcvData) {
		if (incsRcvData.getIncsNo() <= 0) {
			return true;
		}
		
		return this.mgmtMapper.updateRcvDormancy(incsRcvData) > 0;
	}

	public String getOminUserPasswordByIncsNo(final UserData userData) {
		
		if (StringUtils.isEmpty(userData.getIncsNo()) || "0".equals(userData.getIncsNo())) {
			return null;
		}
		
		return this.mgmtMapper.getOminUserPasswordByIncsNo(userData);
	}
	
	public boolean updateUmUserPassword(final UserData userData) {
		
		if (Integer.parseInt(userData.getIncsNo()) <= 0) {
			return true;
		}
		
		return this.mgmtMapper.updateUmUserPassword(userData) > 0;
	}
	
	public boolean updateLastPasswordUpdateNow(final UserData userData) {
		
		if (Integer.parseInt(userData.getIncsNo()) <= 0) {
			return true;
		}
		
		return this.mgmtMapper.updateLastPasswordUpdateNow(userData) > 0;
	}
	
	public boolean insertWeb2AppData(Web2AppVo web2AppVo) {
		return this.mgmtMapper.insertWeb2AppData(web2AppVo)>0;
	}
	
	public Web2AppVo selectWeb2AppData(String web2app_id) {
		return this.mgmtMapper.selectWeb2AppData(web2app_id);
	}
	
	public HashMap<String, String> selectTokenValid(Web2AppVo webAppResponse) {
		return this.mgmtMapper.selectTokenValid(webAppResponse);
	}
	
	public int updateWeb2AppData(Web2AppVo web2AppVo) {
		return this.mgmtMapper.updateWeb2AppData(web2AppVo);
	}
	
	public int updateWeb2AppAuthKey(Web2AppVo web2AppVo) {
		return this.mgmtMapper.updateWeb2AppAuthKey(web2AppVo);
	}
	
	public int selectConsumerAppId(String consumerkey) {
		return this.mgmtMapper.selectConsumerAppId(consumerkey);
	}
}
