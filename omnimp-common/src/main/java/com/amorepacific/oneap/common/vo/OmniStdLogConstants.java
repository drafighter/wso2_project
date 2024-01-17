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
 * Date   	          : 2021. 7. 13..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo 
 *    |_ OmniStdLogConstants.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 7. 13.
 * @version : 1.0
 * @author  : hjw0228
 */

public class OmniStdLogConstants {
	
	// 옴니회원 플랫폼 몰ID
	public static final String OMNI = "M17";
	
	// 업무코드
	public static final String OMNI_API = "OMNI_API";
	public static final String CUST_INTG_API = "CUST_INTG_API";
	public static final String BP_API = "BP_API";
	public static final String CHANNEL_API = "CHANNEL_API";
	public static final String DORMANCY_EAI = "DORMANCY_EAI";
	public static final String JOIN = "OMNI_JOIN";
	public static final String SSG_API = "SSG_API";
	public static final String NAVER_STORE_API = "NAVER_STORE_API";
	
	// API 공통
	public static final String OMNI_API_COMMON = "OMNI_API_COMMON";
	public static final String CUST_INTG_API_COMMON = "CUST_INTG_API_COMMON";
	public static final String BP_API_COMMON = "BP_API_COMMON";
	public static final String CHANNEL_API_COMMON = "CHANNEL_API_COMMON";
	public static final String DORMANCY_EAI_COMMON = "DORMANCY_EAI_COMMON";
	public static final String OMNI_API_KEY_NOT_FOUND = "OMNI_API_apiKey_notFound";
	public static final String OMNI_API_KEY_INVALID = "OMNI_API_apiKey_invalid";
	public static final String OMNI_API_KEY_UNAUTHORIZED = "OMNI_API_apiKey_unauthorized";
	public static final String OMNI_API_WSO_SERVER_ERROR = "OMNI_API_wso2_server_error";
	
	// AUTH 공통
	public static final String OMNI_JOIN_COMMON = "OMNI_JOIN_COMMON";
	
	// 옴니 API 오류 코드
	public static final String API_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_reqValMan_fail";
	public static final String API_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_reqValVls_fail";
	public static final String API_SERVER_ERROR = "OMNI_API_server_error";
	
	/**
	// 비밀번호 변경 API 오류 코드
	public static final String CHANGE_PASSWORD_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_chgPwd_reqValMsn_fail";
	public static final String CHANGE_PASSWORD_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_chgPwd_reqValVls_fail";
	public static final String CHANGE_PASSWORD_DISABLED_USER = "OMNI_API_chgPwd_dsbldUsr";
	public static final String CHANGE_PASSWORD_NOT_FOUND_USER = "OMNI_API_chgPwd_notFndUsr";
	public static final String CHANGE_PASSWORD_PREVIOUS_PASSWORD = "OMNI_API_chgPwd_prvsPwd";
	public static final String CHANGE_PASSWORD_POLICY_VIOLATIONS = "OMNI_API_chgPwd_plcyVltn";
	public static final String CHANGE_PASSWORD_CONFIRM_FAIL = "OMNI_API_chgPwd_cnfrm_fail";
	public static final String CHANGE_PASSWORD_SYNC_JOINON_FAIL = "OMNI_API_chgPwd_syncJoinOn_fail";
	public static final String CHANGE_PASSWORD_API_KEY_AUTHENTICATION_FAIL = "OMNI_API_chgPwd_apiKeyAthnt_fail";
	public static final String CHANGE_PASSWORD_SERVER_ERROR = "OMNI_API_chgPwd_server_error";
	
	// 약관 동의 및 철회 API 오류 코드
	public static final String MODIFY_USER_TERM_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_mdfyUsrTerm_reqValMan_fail";
	public static final String MODIFY_USER_TERM_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_mdfyUsrTerm_reqValVls_fail";
	public static final String MODIFY_USER_TERM_SERVER_ERROR = "OMNI_API_mdfyUsrTerm_server_error";
	
	// 회원 아이디 조회 API 오류 코드
	public static final String CHECK_USERID_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_chkUsrId_reqValMsn_fail";
	public static final String CHECK_USERID_SERVER_ERROR = "OMNI_API_chkUsrId_server_error";
	
	// 비밀번호 확인 (검증) API 오류 코드
	public static final String VERIFY_PASSWORD_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_vrfyPwd_reqValMsn_fail";
	public static final String VERIFY_PASSWORD_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_vrfyPwd_reqValVls_fail";
	public static final String VERIFY_PASSWORD_SERVER_ERROR = "OMNI_API_vrfyPwd_server_error";
	
	// SNS 연동 정보 조회 API 오류 코드
	public static final String CHECK_SNS_ASSOCIATED_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_chkSnsAsctd_reqValMsn_fail";
	public static final String CHECK_SNS_ASSOCIATED_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_chkSnsAsctd_reqValVls_fail";
	public static final String CHECK_SNS_ASSOCIATED_SERVER_ERROR = "OMNI_API_chkSnsAsctd_server_error";
	
	// SNS 연동 정보 해제 API 오류 코드
	public static final String DISCONNECT_SNS_ASSOCIATED_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_dscntSnsAsctd_reqValMsn_fail";
	public static final String DISCONNECT_SNS_ASSOCIATED_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_dscntSnsAsctd_reqValVls_fail";
	public static final String DISCONNECT_SNS_ASSOCIATED_SERVER_ERROR = "OMNI_API_dscntSnsAsctd_server_error";
	
	// 경로별 약관 동의 내역 조회 API 오류 코드
	public static final String CHECK_CHANNEL_TERMS_CONDITION_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_chkChTermsCnd_reqValMsn_fail";
	public static final String CHECK_CHANNEL_TERMS_CONDITION_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_chkChTermsCnd_reqValVls_fail";
	public static final String CHECK_CHANNEL_TERMS_CONDITION_SERVER_ERROR = "OMNI_API_chkChTermsCnd_server_error";
	
	// 필수 약관 동의 내역 조회 API 오류 코드
	public static final String CHECK_REQUIRED_TERMS_CONDITION_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_chkReqTermsCnd_reqValMsn_fail";
	public static final String CHECK_REQUIRED_TERMS_CONDITION_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_chkReqTermsCnd_reqValVls_fail";
	public static final String CHECK_REQUIRED_TERMS_CONDITION_SERVER_ERROR = "OMNI_API_chkReqTermsCnd_server_error";
	
	// ID 사용가능여부 체크 API 오류 코드
	public static final String CHECK_DUPLICATE_ID_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_chkDupId_reqValMsn_fail";
	public static final String CHECK_DUPLICATE_ID_SERVIER_ERROR = "OMNI_API_chkDupId_server_error";
	
	// 비밀번호 유효성 체크 API 오류 코드
	public static final String VERIFY_PASSWORD_POLICY_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_vrfyPwdPlcy_reqValMsn_fail";
	public static final String VERIFY_PASSWORD_POLICY_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_vrfyPwdPlcy_reqValVls_fail";
	public static final String VERIFY_PASSWORD_POLICY_SERVER_ERROR = "OMNI_API_vrfyPwdPlcy_server_error";
	
	// SNS 매핑 해제 내역 동기화 API 오류 코드
	public static final String SYNCHRONIZE_SNS_DISCONNECT_ASSOCIATED_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_syncSnsDscntAsctd_reqValMsn_fail";
	public static final String SYNCHRONIZE_SNS_DISCONNECT_ASSOCIATED_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_syncSnsDscntAsctd_reqValVls_fail";
	public static final String SYNCHRONIZE_SNS_DISCONNECT_ASSOCIATED_SERVER_ERROR = "OMNI_API_syncSnsDscntAsctd_server_error";
	
	// SNS 매핑 내역 동기화 API 오류 코드
	public static final String SYNCHRONIZE_SNS_ASSOCIATED_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_syncSnsAsctd_reqValMsn_fail";
	public static final String SYNCHRONIZE_SNS_ASSOCIATED_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_syncSnsAsctd_reqValVls_fail";
	public static final String SYNCHRONIZE_SNS_ASSOCIATED_SERVER_ERROR = "OMNI_API_syncSnsAsctd_server_error";
	
	// Join-On 패스워드 변경 API 오류 코드
	public static final String CHANGE_PASSWORD_JOIN_ON_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_chgPwdJoinOn_reqValMsn_fail";
	public static final String CHANGE_PASSWORD_JOIN_ON_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_chgPwdJoinOn_reqValVls_fail";
	public static final String CHANGE_PASSWORD_JOIN_ON_SERVER_ERROR = "OMNI_API_chgPwdJoinOn_server_error";
	
	// 030 경로 가입/탈퇴/망취소 API 오류 코드
	public static final String CREATE_USER_BY_030_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_crtUsrBy030_reqValMsn_fail";
	public static final String CREATE_USER_BY_030_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_crtUsrBy030_reqValVls_fail";
	public static final String CREATE_USER_BY_030_SERVER_ERROR = "OMNI_API_crtUsrBy030_server_error";
	public static final String DISABLED_USER_BY_030_SERVER_ERROR = "OMNI_API_dsbldUsrBy030_server_error";
	public static final String CANCEL_USER_BY_030_SERVER_ERROR = "OMNI_API_cnclUsrBy030_server_error";
	
	// 비밀번호 초기회 API 오류 코드
	public static final String INITIALIZE_PASSWORD_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_intlzPwd_reqValMsn_fail";
	public static final String INITIALIZE_PASSWORD_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_intlzPwd_reqValVls_fail";
	public static final String INITIALIZE_PASSWORD_SERVER_ERROR = "OMNI_API_intlzPwd_server_error";
	
	// 중복 고객 통합 처리 API 오류 코드
	public static final String INTEGRATED_DUPLICATE_ID_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_intgrDupId_reqValMsn_fail";
	public static final String INTEGRATED_DUPLICATE_ID_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_intgrDupId_reqValVls_fail";
	public static final String INTEGRATED_DUPLICATE_ID_SERVIER_ERROR = "OMNI_API_intgrDupId_server_error";
	
	// WEBID 변경 API 오류코드
	public static final String CHANGE_USERID_REQUIRED_VALUE_MISSING_FAIL = "OMNI_API_chgUsrId_reqValMsn_fail";
	public static final String CHANGE_USERID_REQUIRED_VALUE_VALIDATION_FAIL = "OMNI_API_chgUsrId_reqValVls_fail";
	public static final String CHANGE_USERID_SERVER_ERROR = "OMNI_API_chgUsrId_server_error";
	*/
	
	// 고객통합플랫폼 API 오류 코드
	public static final String GET_CUST_LIST_SERVER_ERROR = "CUST_INTG_API_getCustList_server_error";
	public static final String GET_CUST_LIST_MOBILE_PHONE_NUMBER_ERROR = "CUST_INTG_API_getCustList_mobPhNbr_error";
	public static final String GET_CICUEMCU_INFR_BY_INCS_NO_SERVER_ERROR = "CUST_INTG_API_getCicuemcuInfrByIncsNo_server_error";
	public static final String GET_CICUEMCU_INFR_ARRAY_INCS_NO_SERVER_ERROR = "CUST_INTG_API_getCicuemcuInfrArrayIncsNo_server_error";
	public static final String GET_CICUEMCU_OPTI_LIST_SERVER_ERROR = "CUST_INTG_API_getCicuemcuOptiList_server_error";
	public static final String GET_CUST_CH_LIST_SERVER_ERROR = "CUST_INTG_API_getCustChList_server_error";
	public static final String GET_CUST_YN_SERVER_ERROR = "CUST_INTG_API_getCustYn_server_error";
	public static final String GET_CUSTBY_CH_CS_NO_SERVER_ERROR = "CUST_INTG_API_getCustbyChCsNo_server_error";
	public static final String CREATE_CUST_SERVER_ERROR = "CUST_INTG_API_createCust_server_error";
	public static final String UPDATE_CUST_SERVER_ERROR = "CUST_INTG_API_updateCust_server_error";
	public static final String UPDATE_CUST_CI_NO_SERVER_ERROR = "CUST_INTG_API_updateCustCiNo_server_error";
	public static final String SAVE_CICUED_CUTNCA_SERVER_ERROR = "CUST_INTG_API_saveCicuedCutnca_server_error";
	public static final String SAVE_CICUEM_CUOPTI_LIST_SERVER_ERROR = "CUST_INTG_API_savecicuemcuoptilist_server_error";
	public static final String CREATE_CUST_CHANNEL_MEMBER_SERVER_ERROR = "CUST_INTG_API_createCustChannelMember_server_error";
	
	// 고객통합플랫폼 임직원 인증 API 오류 코드
	public static final String GET_EMP_INFR_INQ_SERVER_ERROR = "CUST_INTG_API_getEmpInfrInq_server_error";
	public static final String SEND_PRCN_EMP_ATHT_INFR_SERVER_ERROR = "CUST_INTG_API_sendPrcnEmpAthtInfr_server_error";
	public static final String HANDLE_AP_EMP_ATHT_SERVER_ERROR = "CUST_INTG_API_HandleApEmpAtht_server_error";
	public static final String HANDLE_PRCN_EMP_ATHT_SERVER_ERROR = "CUST_INTG_API_HandlePrcnEmpAtht_server_error";
	public static final String GET_EMP_CMPY_INFR_LIST_SERVER_ERROR = "CUST_INTG_API_GetEmpCmpyInfrList_server_error";
	
	// Beauty Point  API 오류 코드
	public static final String CHANGE_BEAUTY_POINT_PASSWORD_SERVER_ERROR = "BP_API_chgBpPwd_server_error";
	public static final String CREATE_BEAUTY_POINT_USER_SERVER_ERROR = "BP_API_crtBpUser_server_error";
	public static final String CANCEL_BEAUTY_POINT_USER_SERVER_ERROR = "BP_API_cnclBpUser_server_error";
	public static final String CHECK_BEAUTY_POINT_ONLINE_ID_SERVER_ERROR = "BP_API_chkBpOnlineId_server_error";
	public static final String CHECK_BEAUTY_POINT_USER_ID_SERVER_ERROR = "BP_API_chkBpUsrId_server_error";
	
	// 휴면해제 EAI 오류 코드
	public static final String RELEASE_DORMANCY_EAI_SERVER_ERROR = "RELASE_DORMANCY_EAI_server_error";
	
	// Channel API 오류 코드
	public static final String CREATE_CHANNEL_USER_SERVER_ERROR = "CHANNEL_API_crtChUsr_server_error";
	public static final String CREATE_CHANNEL_USER_RETURN_NULL = "CHANNEL_API_crtChUsr_server_error";
	public static final String GET_CHANNEL_USER_SERVER_ERROR = "CHANNEL_API_getChUsr_server_error";
	public static final String GET_CHANNEL_USER_RETURN_NULL = "CHANNEL_API_getChUsr_server_error";
	public static final String GET_ONLY_CHANNEL_USER_SERVER_ERROR = "CHANNEL_API_getOnlyChUsr_server_error";
	public static final String GET_ONLY_CHANNEL_USER_RETURN_NULL = "CHANNEL_API_getOnlyChUsr_server_error";
	
	// 옴니 AUTH 오류 코드
	public static final String AUTH_INVALID_PARAMETER = "OMNI_AUTH_invalid_parameter";
	public static final String AUTH_EMPTY_CHANNEL_CODE = "OMNI_AUTH_empty_channelCd";
	public static final String AUTH_EMPTY_JOIN_PRTN_ID = "OMNI_AUTH_empty_joinPrtnId";
	public static final String AUTH_EMPTY_JOIN_EMP_ID = "OMNI_AUTH_empty_joinEmpId";
	public static final String AUTH_EMPTY_SESSION_DATA_KEY = "OMNI_AUTH_empty_sessionDataKey";
	public static final String AUTH_KMC_CERTIFICATION_SERVER_ERROR = "OMNI_AUTH_kmc_certfd_server_error";
	public static final String AUTH_IPIN_CERTIFICATION_SERVER_ERROR = "OMNI_AUTH_ipin_certfd_server_error";
	public static final String AUTH_REGIST_INTEGRATED_CUSTOMER_CALL_API_FAIL = "OMNI_AUTH_registIntegratedCustomerCallApi_fail";
	public static final String AUTH_UPDATE_INTEGRATED_CUSTOMER_CALL_API_FAIL = "OMNI_AUTH_updateIntegratedCustomerCallApi_fail";
	public static final String AUTH_CANCEL_INTEGRATED_CUSTOMER_CALL_API_FAIL = "OMNI_AUTH_cancelIntegratedCustomerCallApi_fail";
	public static final String AUTH_REGIST_INTEGRATED_CHANNEL_CUSTOMER_CALL_API_FAIL = "OMNI_AUTH_registIntegrateChannelCustomer_fail";
	public static final String AUTH_CANCEL_INTEGRATED_CHANNEL_CUSTOMER_CALL_API_FAIL = "OMNI_AUTH_cancelIntegrateChannelCustomer_fail";
	public static final String AUTH_REGIST_BEAUTY_POINT_CALL_API_FAIL = "OMNI_AUTH_registBeautyPointCustomerCallApi_fail";
	public static final String AUTH_REGIST_CHANNEL_CUSTOMER_CALL_API_FAIL = "OMNI_AUTH_registChannelCustomer_fail";
	public static final String AUTH_REGIST_OMNI_CUSTOMER_CALL_API_FAIL = "OMNI_AUTH_registOmniCustomer_fail";
	
	// SSG API 오류 코드
	public static final String GET_SSG_ACCESS_TOKEN_SERVER_ERROR = "SSG_API_AccessToken_server_error";
	
	// 네이버 스마트 스토어 API 오류 코드
	public static final String GET_NAVER_STORE_USER_INFO_SERVER_ERROR = "NAVER_STORE_API_UserInfo_server_error";
	public static final String SET_NAVER_STORE_MEMBERSHIP_LINK_SERVER_ERROR = "NAVER_STORE_API_MembershipLink_server_error";
	public static final String SET_NAVER_STORE_MEMBERSHIP_UNLINK_SERVER_ERROR = "NAVER_STORE_API_MembershipUnLink_server_error";
	public static final String SET_NAVER_STORE_MEMBERSHIP_WITHDRAW_SERVER_ERROR = "NAVER_STORE_API_MembershipWithdraw_server_error";
}
