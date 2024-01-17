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
 * Date   	          : 2020. 7. 29..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.login.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.login.vo.LoginResponse;
import com.amorepacific.oneap.auth.login.vo.LoginStatus;
import com.amorepacific.oneap.auth.mgmt.service.MgmtService;
import com.amorepacific.oneap.auth.step.AuthStep;
import com.amorepacific.oneap.common.types.LoginType;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.vo.LoginStepVo;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.login.service 
 *    |_ LoginService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 29.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class LoginService {

	@Value("${wso2.oneapurl}")
	private String oneApUrl;

	@Value("${wso2.commonauthurl}")
	private String commonAuthUrl;

	@Value("${wso2.samlssourl}")
	private String samlSsoUrl;

	@Value("${wso2.oauth2authorizeurl}")
	private String oauth2AuthorizeUrl;

	@Value("${wso2.identityserverendpointcontexturl}")
	private String identityServerEndpointContextUrl;

	@Value("${wso2.accountrecoveryrestendpointurl}")
	private String accountRecoveryRestEndpointUrl;

	@Value("${wso2.authenticationrestendpointurl}")
	private String authApiUrl;

	@Value("${wso2.enableauthenticationwithauthenticationrestapi}")
	private boolean enableAuthenticationWithAuthenticationRestApi;

	@Autowired
	private MgmtService mgmtService;

	@Autowired
	private CustomerApiService customerApiService;

	@Autowired
	private AuthStep authStep;

	public LoginResponse loginCheck(final SSOParam ssoParam, Map<String, String> idpAuthenticatorMapping) {

		LoginResponse response = new LoginResponse();
		response.setAuthUrl(this.commonAuthUrl);
		response.setStatus(LoginStatus.SUCCESS.getCode());

		List<String> localAuthenticatorNames = new ArrayList<>();
		if (idpAuthenticatorMapping != null && idpAuthenticatorMapping.get(OmniConstants.RESIDENT_IDP_RESERVED_NAME) != null) {
			String authList = idpAuthenticatorMapping.get(OmniConstants.RESIDENT_IDP_RESERVED_NAME);
			if (authList != null) {
				localAuthenticatorNames = Arrays.asList(authList.split(","));
				response.setLocalAuthenticatorNames(localAuthenticatorNames);
			}
		}

		if (localAuthenticatorNames.size() > 0) {
			if (localAuthenticatorNames.contains(OmniConstants.OPEN_ID_AUTHENTICATOR)) {
				response.setHasLocalLoginOptions(1); // true
			} else if (localAuthenticatorNames.contains(OmniConstants.IDENTIFIER_EXECUTOR)) {
				response.setHasLocalLoginOptions(1); // true
			} else if (localAuthenticatorNames.contains(OmniConstants.JWT_BASIC_AUTHENTICATOR) || localAuthenticatorNames.contains(OmniConstants.BASIC_AUTHENTICATOR)) {
				response.setHasLocalLoginOptions(1); // true
				response.setIncludeBasicAuth(1); // true
				if (localAuthenticatorNames.contains(OmniConstants.JWT_BASIC_AUTHENTICATOR)) {
					if (this.enableAuthenticationWithAuthenticationRestApi) {
						response.setHasLocalLoginOptions(1); // true
					} else {
						String redirectUrl = new StringBuilder("/errors?") //
								.append(OmniConstants.STATUS).append("=").append(OmniConstants.CONFIGURATION_ERROR).append("&") //
								.append(OmniConstants.STATUS_MSG).append("=").append(OmniConstants.AUTHENTICATION_MECHANISM_NOT_CONFIGURED).toString();
						response.setRedirectUrl(redirectUrl);
						return response;
					}
				} else if (localAuthenticatorNames.contains(OmniConstants.BASIC_AUTHENTICATOR)) {
					response.setIsBackChannelBasicAuth(0); // false
				}

			}
		}
		String loginFormActionUrl;
		if (response.getIsBackChannelBasicAuth() == 1) {
			loginFormActionUrl = "/authenticate";
			if (StringUtils.hasText(ssoParam.getQueryString())) {
				loginFormActionUrl = loginFormActionUrl.concat("?").concat(ssoParam.getQueryString());
			}
		} else {
			final String type = ssoParam.getType();
			if (OmniConstants.SAMLSSO_TYPE.equals(type)) {
				loginFormActionUrl = this.samlSsoUrl;
			} else if (OmniConstants.OAUTH2_TYPE.equals(type)) {
				loginFormActionUrl = this.oauth2AuthorizeUrl;
			} else {
				loginFormActionUrl = this.commonAuthUrl;
			}
		}

		response.setLoginFormActionUrl(loginFormActionUrl);
		if (idpAuthenticatorMapping != null) {
			response.setIdpAuthenticatorMapping(idpAuthenticatorMapping);
		}

		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 26. 오후 6:37:39
	 * 
	 * O O O 뷰티포인트 정상 로그인 처리
	 * O O O 경로 자체 고객 뷰티포인트 ID 가 1 개 A0204 단일 계정 화면 약관 비노출
	 * O O O 경로 자체 고객 뷰티포인트 ID 가 2 개이상 A0204 복수 계정 화면 약관 비노출
	 * O O X 뷰티포인트 A0105
	 * O X O 경로 자체 고객 동일 ID 사용가능 A0202
	 * O X O 경로 자체 고객 동일 ID 타인사용 A0203
	 * O X X 휴대폰 휴대폰로그인 A0207 채널약관 동의 목록 노출 화면
	 * O X O 휴대폰 휴대폰로그인 A0207
	 * X X O 경로 자체 고객 경로 자체 고객자체회원 A0201 전환가입 화면 전환가입으로 케이스로 이동
	 * </pre>
	 * 
	 * @param chCd
	 * @param loginId
	 * @param loginPwd
	 * @return
	 */
	public LoginStepVo loginStep(final String chCd, final String loginId, final String loginPwd, boolean status) {
		return this.authStep.loginStep(chCd, loginId, loginPwd, status);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 10. 오전 10:06:12
	 * </pre>
	 * 
	 * @param chCd
	 * @param incsNo
	 * @return
	 */
	public LoginStepVo phoneLoginStep(final String chCd, final int incsNo) {
		return this.authStep.loginPhoneStep(chCd, incsNo);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 로그인 시 로그인 상태를 체크
	 * author   : takkies
	 * date     : 2020. 9. 8. 오후 8:48:18
	 * </pre>
	 * 
	 * @param chCd
	 * @param loginId
	 * @param loginPwd
	 * @return
	 */
	public LoginStepVo loginStatusCheck(final String chCd, final String loginId, final String loginPwd) {

		// 옴니에 해당 사용자 조회
		List<UmOmniUser> omniUserList = this.mgmtService.getOmniLoginUserList(loginId, loginPwd);

		if (omniUserList == null || omniUserList.isEmpty()) {

			// 옴니에 없을 경우 로그인 아이디로 해당 사용자 로그인 정보 추출
			UmOmniUser omniUser = this.mgmtService.getOmniUserByLoginUserName(loginId.trim());

			if (omniUser != null) {
				// 로그인 불가능 정보 추출
				final String accountIncsNo = omniUser.getIncsNo();
				final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
				final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
				final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
				//final String unlockTime = omniUser.getUnlockTime() == null ? "0" : omniUser.getUnlockTime();
				log.debug("▶▶▶▶▶ [login status check] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
				if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자

					List<UmOmniUser> omniUsers = new ArrayList<>();
					omniUsers.add(omniUser);
					return new LoginStepVo(LoginType.LOCK, omniUsers, null, accountIncsNo);
					
					//20231106 비밀번호 실패 횟수 5->7 unlock time 제거
					// valid unlock time 이 아니면 lock 하지 않음.
//					if (DateUtil.isValidUnlockTime(unlockTime)) {
//
//						final String remainUnlockTime = DateUtil.getRemainedUnlockTime(unlockTime);
//						if (StringUtils.hasText(remainUnlockTime)) {
//							omniUser.setUnlockTime(remainUnlockTime);
//							omniUser.setFailedLoginAttempts(Integer.toString(DateUtil.getUnlockTimeTermSeconds(unlockTime)));
//							omniUsers.add(omniUser);
//						}
//
//						log.debug("▶▶▶▶▶ [login status check] user status under lock time : {}, current : {}", //
//								DateUtil.getDateTime(DateUtil.getUnixTimestampToDateString(unlockTime)), //
//								DateUtil.getCurrentDateTimeString());
//						return new LoginStepVo(LoginType.LOCK, omniUsers, null, accountIncsNo);
//					} else {
//						log.debug("▶▶▶▶▶ [login status check] user status passed lock time, do next process.");
//					}
				}
			}

			// 경로 임시테이블에 해당 사용자 조회
			List<UmChUser> chUserList = this.mgmtService.getChannelLoginUserListByFlag(chCd, loginId, loginPwd);

			if (chUserList == null || chUserList.isEmpty()) {

				log.debug("▶▶▶▶▶ [login status check] 회원(비밀번호다름) --> {}", LoginType.NEW.getDesc());
				this.customerApiService.callWso2CommonAuthPageForced(loginId.trim(), loginPwd); // 강제로 비밀번호 틀리게 호출
				return new LoginStepVo(LoginType.PWDFAIL, null, null, null);

			} else {

				UmChUser chUser = chUserList.get(0);
				String swtYn = chUser.getIncsWebIdSwtYn(); // 통합고객웹ID전환여부

				if ("Y".equals(swtYn)) {
					log.debug("▶▶▶▶▶ [login status check] 이미 전환가입한 사용자 --> {} {}", LoginType.ALREADY_TRNS_CH.getDesc(), StringUtil.printJson(chUser));
					return new LoginStepVo(LoginType.ALREADY_TRNS_CH, null, chUserList, Integer.toString(chUser.getIncsNo()));
				} else {
					return this.authStep.loginStep(chCd, loginId.trim(), loginPwd, true);
				}

			}

		} else { // 옴니에 사용자 정보 존재함.(중복된 데이터 존재 불가함)

			UmOmniUser omniUser = omniUserList.get(0);

			if (omniUser != null) {

				final String resetFlag = omniUser.getUmUserPasswordReset();
				final String incsNo = omniUser.getIncsNo();
				if (StringUtils.hasText(resetFlag)) {
					if (OmniConstants.ACCOUNT_PASSWORD_RESET.equals(resetFlag)) { // 비밀번호 초기화
						log.debug("▶▶▶▶▶ [login status check] 비밀번호 초기화 여부 : {}", resetFlag);
						return new LoginStepVo(LoginType.PWDRESET, null, null, incsNo);
					}
				}

				// 로그인 불가능 정보 추출
				final String accountIncsNo = omniUser.getIncsNo();
				final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
				final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
				final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
				//final String unlockTime = omniUser.getUnlockTime() == null ? "0" : omniUser.getUnlockTime();
				
				log.debug("▶▶▶▶▶ [login status check] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
				if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자

					List<UmOmniUser> omniUsers = new ArrayList<>();
					omniUsers.add(omniUser);
					return new LoginStepVo(LoginType.LOCK, omniUsers, null, accountIncsNo);
					
					//20231106 비밀번호 실패 횟수 5->7 unlock time 제거
					// valid unlock time 이 아니면 lock 하지 않음.
//					if (DateUtil.isValidUnlockTime(unlockTime)) {
//
//						final String remainUnlockTime = DateUtil.getRemainedUnlockTime(unlockTime);
//						if (StringUtils.hasText(remainUnlockTime)) {
//							omniUser.setUnlockTime(remainUnlockTime);
//							omniUser.setFailedLoginAttempts(Integer.toString(DateUtil.getUnlockTimeTermSeconds(unlockTime)));
//							omniUsers.add(omniUser);
//						}
//
//						log.debug("▶▶▶▶▶ [login status check] user status under lock time : {}, current : {}", //
//								DateUtil.getDateTime(DateUtil.getUnixTimestampToDateString(unlockTime)), //
//								DateUtil.getCurrentDateTimeString());
//						return new LoginStepVo(LoginType.LOCK, omniUsers, null, accountIncsNo);
//					} else {
//						log.debug("▶▶▶▶▶ [login status check] user status passed lock time, do next process.");
//					}
				}

				return this.authStep.loginStep(chCd, loginId.trim(), loginPwd,true);
			} else {
				return new LoginStepVo(LoginType.NEW, null, null, null);
			}

		}

	}
}
