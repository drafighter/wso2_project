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
package com.amorepacific.oneap.common.vo;

/**
 * <pre>
 * com.amorepacific.oneap.auth.vo 
 *    |_ AuthConstants.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 29.
 * @version : 1.1
 * @author  : takkies
 * @author  : kspark01
 *
 */

public class OmniConstants {
	
	/** transaction uuid */
	public static final String TRX_UUID = "trx-uuid";
	
	
	public static final String RESIDENT_IDP_RESERVED_NAME = "LOCAL";
	public static final String STATUS = "status";
	public static final String STATUS_MSG = "statusMsg";
	public static final String MISSING_CLAIMS = "missingClaims";
	
	public static final String CONFIGURATION_ERROR = "configuration.error";
	public static final String AUTHENTICATION_MECHANISM_NOT_CONFIGURED = "authentication.mechanism.not.configured";
	public static final String REQUEST_PARAM_SP = "sp";
	
	public static final String REQUEST_PARAM_APPLICATION = "application";
	public static final String REQUEST_PARAM_AUTHENTICATORS = "authenticators";
	public static final String REQUEST_PARAM_HRD = "hrd";
	public static final String REQUEST_PARAM_TYPE = "type";
	public static final String REQUEST_PARAM_REFERRER = "referer";
	public static final String QUERY_SEPARATOR = "&";
	public static final String EQUAL = "=";

	public static final String CODE = "code";
	public static final String SAMLSSO = "samlsso";
	public static final String OPENID = "openid";
	public static final String PASSIVESTS = "passivests";
	public static final String OAUTH2 = "oauth2";
	public static final String OIDC = "oidc";
	public static final String FIDO = "fido";

	public static final String URI_LOGIN = "login";
	public static final String URI_SAMLSSO_LOGIN = "samlsso_login.do";
	public static final String URI_OPENID_LOGIN = "openid_login.do";
	public static final String URI_PASSIVESTS_LOGIN = "passivests_login.do";
	public static final String ATTRIBUTE_SKIP_PROPERTY = "skip";
	
	public static final String IDP_AUTHENTICATOR_MAP = "idpAuthenticatorMap";
	
	public static final String FIDO_AUTHENTICATOR = "FIDOAuthenticator";
	public static final String IWA_AUTHENTICATOR = "IwaNTLMAuthenticator";
	public static final String IS_SAAS_APP = "isSaaSApp";
	public static final String BASIC_AUTHENTICATOR = "BasicAuthenticator";
	public static final String IDENTIFIER_EXECUTOR = "IdentifierExecutor";
	public static final String OPEN_ID_AUTHENTICATOR = "OpenIDAuthenticator";
	public static final String JWT_BASIC_AUTHENTICATOR = "JWTBasicAuthenticator";
	public static final String X509_CERTIFICATE_AUTHENTICATOR = "x509CertificateAuthenticator";

	public static final String AUTHENTICATOR_NAME = "mobileVerifier";
    public static final String AUTHENTICATOR_FRIENDLY_NAME = "mobileVerifierAuthenticator";
    public static final String INPUT_NAME = "fullName";
    public static final String INPUT_MOBILE = "mobile";
    public static final int SUPER_TENANT_ID = -1234;
    public static final String CLAIM_FULLNAME = "http://wso2.amorepacific.com/fullname";
    public static final String CLAIM_MOBILE = "http://wso2.org/claims/mobile";
    public static final String CUSTOM_LOGIN_PAGE = "plogin";
    public static final String DEFAULT_LOGIN_PAGE = "/plogin";
    public static final String SESSION_DATA_KEY = "sessionDataKey";	
	
	public static final String SAMLSSO_TYPE = "samlsso";
	public static final String OAUTH2_TYPE = "oauth2";
	
	public static final String TOKEN_NAME = "idToken";
	
	public static final String ACCOUNT_STATE_LOCK = "LOCKED";
	public static final String ACCOUNT_STATE_UNLOCK = "UNLOCKED";
	public static final String ACCOUNT_PASSWORD_RESET = "R";
	
	public static final String AP_CHCD = "000"; // 아모레퍼시픽
	public static final String INNISFREE_ONLINE_CHCD = "036"; // 이니스프리 쇼핑몰
	public static final String INNISFREE_OFFLINE_CHCD = "006"; // 이니스프리
	public static final String ESPOIR_ONLINE_CHCD = "042"; // 에스쁘아쇼핑몰
	public static final String ESPOIR_OFFLINE_CHCD = "016"; // 에스쁘아
	public static final String AESTURA_ONLINE_CHCD = "099"; // 에스트라 쇼핑몰
	public static final String JOINON_CHCD = "030"; // 조인온, 뷰티포인트
	public static final String ECRIS_OFFLINE_CHCD = "101"; // eCris 오프라인
	public static final String BEAUTY_ANGEL_ONLINE_CHCD = "BAA"; // 뷰티엔젤 온라인
	public static final String OSULLOC_CHCD = "039"; // 오설록온라인몰
	public static final String OSULLOC_OFFLINE_CHCD = "012"; // 오설록티하우스
	public static final String OSULLOC_DEPARTMENT_CHCD = "008"; // 백화점 오설록
	public static final String IOPE_CHCD = "AGK"; // 아이오페 온라인
	public static final String SSG_CHCD = "202"; // SSG
	public static final String APMALL_CHCD = "031"; // SSG
	public static final String NAVER_STORE_CHCD = "401"; // 네이버 스마트 스토어

	public static final String SAVE_ID_COOKIE_NAME = "one-ap-save-username";
	public static final String SAVE_AUTO_LOGIN_COOKIE_NAME = "one-ap-auto-login";
	public static final String LOGIN_ID_COOKIE_NAME = "one-ap-loginid";
	public static final String LAST_LOGIN_TYPE = "one-ap-last-login";
	public static final String COMMON_AUTH_ID_COOKIE = "commonAuthId";
	public static final String ONE_AP_MOVE_CHANNEL = "one-ap-move-channel";
	public static final String ONE_AP_OFFLINE_PARAM = "one-ap-offline-param";
	public static final String ONE_AP_CERTIFICATION_TYPE = "one-ap-certification-type";
	public static final String LOGOUT_REDIRECT_URI_COOKIE = "logoutRedirectUri";
			
	public static final String UID = "uid";
	public static final String INCS_NO = "incsNo";
	public static final String CH_CD = "chCd";
	public static final String CH_NM = "chNm";
	public static final String RD_URL = "rdUrl";
	
	public static final String SMS_AUTH_STATUS = "smsAuthStatus";
	public static final int SMS_AUTH_SUCCESS = 1;
	public static final int SMS_AUTH_SEND_FAIL = 0;
	public static final int SMS_AUTH_NOTFOUND_INCSNO = -5;
	public static final int SMS_AUTH_NOTFOUND_USER = -10;
	public static final int SMS_AUTH_LIMIT_EXCEEDED = -15;
	public static final int SMS_AUTH_PHONELOGIN_DUPLICATE = -20;
	public static final int SMS_AUTH_WITHDRAW_USER = -25;
	
	public static final String INCS_NO_SESSION = "incsNo";
	public static final String USERNM_SESSION = "userName";
	
	public static final String CH_CD_SESSION = "chCd";
	public static final String CH_NM_SESSION = "chNm";
	public static final String CERT_RESULT_SESSION = "cert";
	public static final String SMS_RESULT_SESSION = "sms";
	public static final String MOBILENO_SESSION = "mobile";
	public static final String SESSION_DATA_KEY_SESSION = "sessionDataKey";
	
	public static final String XINCS_NO_SESSION = "xincsno";
	public static final String XMOBILE_NO_SESSION = "xmobile";
	public static final String XID_SESSION = "xid";
	public static final String XPW_SESSION = "xpw";
	public static final String XNM_SESSION = "xnm";
	public static final String XID_MSESSION = "xmid";
	public static final String XPW_MSESSION = "xmpw";
	public static final String AUTO_LOGIN = "autologin";
	public static final String AUTO_LOGIN_SESSION = "autologin";
	public static final String CHATBOT_LOGIN_SESSION = "chatbotlogin";
	
	public static final String SSOPARAM = "ssoParam";
	public static final String OFFLINEPARAM = "offlineParam";
	public static final String SNSPARAM = "snsParam";
	public static final String CHATBOTPARAM = "chatbotParam";
	public static final String SNS_OFFLINEPARAM = "snsOfflineParam";
	public static final String CHANNELPARAM = "channelParam";
	public static final String AUTH_PARAM = "authParam";
	public static final String NAVER_MEMBERSHIP_PARAM = "naverMembershipParam";
	public static final String MEMBERSHIP_USERINFO = "membershipUserInfo";
	public static final String MEMBERSHIP_AFTER_REDIRECT_URL = "membershipAfterRedirectUrl";
	public static final String IS_MEMBERSHIP = "isMembership";
	public static final String IS_UNLINK_MEMBERSHIP = "isUnLinkMembership";
	public static final String IS_CREATE_CUST_CHANNEL_MEMBER = "isCreateCustChannelMember";
	public static final String IS_ENCRYPTION = "isEncryption";
	// public static final String IS_SNS_LOGIN_COMPLETE = "isSnsLoginComplete";
	public static final String IS_LOGIN_COMPLETE = "isLoginComplete";
	
	public static final String MBR_ID_SESSION = "mbrId";
	public static final String XMBR_ID_SESSION = "xmbrId";
	
	public static final String SNS_USE_TYPE = "snsUseType";
	public static final String SNS_ACCESS_TOKEN = "snsAccessToken";
	public static final String SNS_AUTH_CODE = "snsAuthCode";
	
	public static final String SNS_FIRST_MAPPING_SNS_ID = "snsFirstMappingSnsId";
	public static final String SNS_FIRST_MAPPING_SNS_TYPE = "snsFirstMappingSnsType";
	public static final String SNS_TERMS_AFTER_AUTH_URL = "snsTermsAfterAuthUrl";
	
	public static final String SNS_MAPPING_TRY_LOGIN_ID = "snsMappingTryLoginId";
	public static final String SNS_MAPPING_TRY_INCS_NO = "snsMappingTryIncsNo";
	public static final String SNS_MAPPING_AFTER_REDIRECT_URL = "snsMappingAfterRedirectUrl";
	public static final String SNS_MAPPING_IS_POPUP = "snsMappingIsPopup";
	
	public static final String NAVER_REPROMPT = "naverReprompt";
	public static final String NAVER_MEMBERSHIP_TOKEN = "naverMembershipToken";
	public static final String NAVER_MEMBERSHIP_USER_INFO = "naverMembershipUserInfo";
		
	public static final String SEND_SMS_EAI_SUCCESS = "S";
	public static final String SEND_KAKAO_NOTICE_EAI_SUCCESS = "S";
	
	public static final String LOGIN_MOBILE = "MOBILE";
	public static final String LOGIN_MOBILE_TYPE = "mlogin";
	
	public static final String SEARCH_CERT_USER_LIST = "searchCertUserList";
	public static final String SEARCH_PWD_ID = "searchPwdId";
	
	public static final int JOIN_DIV_TYPE_JOIN = 1;
	public static final int JOIN_DIV_TYPE_CHANNEL_JOIN = 0;
	public static final int JOIN_DIV_TYPE_EXIST = -5;
	public static final int JOIN_DIV_TYPE_NAME_MISMATCH = -10;
	public static final int JOIN_DIV_TYPE_DORMANCY = -15;
	public static final int JOIN_DIV_TYPE_DUPLICATE = -20;
	public static final int JOIN_DIV_TYPE_OMISSION = -25;
	public static final int JOIN_DIV_TYPE_INFO_MISMATCH = -30;
	public static final int JOIN_DIV_TYPE_WITHDRAW = -35;
	public static final int JOIN_DIV_TYPE_LOCK = -37;
	public static final int JOIN_DIV_TYPE_CONVERSION = -40;
	public static final int JOIN_DIV_TYPE_ERROR = -9999;

	public static final int JOIN_TYPE_JOIN = 5;
	public static final int JOIN_TYPE_CHANNEL = 10;
	public static final int JOIN_TYPE_JOINED = 15;
	public static final int JOIN_TYPE_CONVERSION = 20;
	public static final int JOIN_TYPE_JOINED_OMNI_CH = 25;
	public static final int JOIN_TYPE_JOINED_OMNI = 30;
	public static final int JOIN_TYPE_JOINED_OFF = 35;
	public static final int JOIN_TYPE_JOINED_AGREE = 40;
	public static final int JOIN_TYPE_JOINED_CH_OFF = 45;
	public static final int JOIN_TYPE_JOINED_CH_AGREE = 50;
	public static final int JOIN_TYPE_JOIN_OFF = 55;
	public static final int JOIN_TYPE_JOIN_STEP_OFF = 60;
	public static final int JOIN_TYPE_CHANNEL_OFF = 65;
	
	
	public static final int LOGIN_TYPE_LOGIN = 1;
	public static final int LOGIN_TYPE_CS = 5;
	public static final int LOGIN_TYPE_OMNI_JOIN = 10;
	public static final int LOGIN_TYPE_AGREE = 15;
	public static final int LOGIN_TYPE_CORPAGREE = 17;
	public static final int LOGIN_TYPE_TRNS_JOIN = 20;
	public static final int LOGIN_TYPE_INTG_JOIN = 25;
	public static final int LOGIN_TYPE_CONV_JOIN = 27;
	public static final int LOGIN_TYPE_TRNS_BP = 30;
	public static final int LOGIN_TYPE_TRNS_CH_MINE = 40;
	public static final int LOGIN_TYPE_TRNS_CH_OTHER = 50;
	public static final int LOGIN_TYPE_NEW_BPTERMS = 60;
	public static final int LOGIN_TYPE_ALREADY_TRNS_CH = -50;
	
	
	public static final String XAPIKEY = "X-API-KEY";
	
	public static final String ID_SIZE_FAIL = "SIZE_FAIL";
	public static final String ID_VALIDATION_FAIL = "VALIDATION_FAIL";
	
	public static final String PASSWORD_COMPARE_FAIL = "COMPARE_FAIL";
	public static final String PASSWORD_CONFIRM_FAIL = "CONFIRM_FAIL";
	public static final String PASSWORD_SIZE_FAIL = "SIZE_FAIL";
	public static final String PASSWORD_COMBINATION_FAIL = "COMBINATION_FAIL";
	public static final String PASSWORD_INCLUDE_WHITESPACE = "INCLUDE_WHITESPACE";
	
	public static final int PASSWORD_STENGTH_HIGH = 2;
	public static final int PASSWORD_STENGTH_MID = 1;
	public static final int PASSWORD_STENGTH_LOW = 0;
	
	public static final String PROCESS_REQUEST = "processRequest";
	public static final String PROCESS_RESPONSE = "processResponse";
	public static final String PROCESS_STOPWATCH = "processStopWatch";
	public static final String PROCESS_LOGGING_ID = "processLoggingId"; 
	
	public static final String OMNI_JOIN_FLAG = "J";
	public static final String OMNI_WITHDRAW_FLAG = "L";
	
	public static final String TRNS_TYPE_INTEGRATE = "integrate";
	public static final String TRNS_TYPE_TRANSFORM = "transform";
	
	public static final String CANCEL_URI = "cancelUri";
	public static final String POPUP = "popup";
	
	public static final String CANCEL_URL = "cancelUrl";
	public static final String RETURN_URL = "returnUrl";
	
	public static final String EXIST_CUSTOMER = "existCustomer";
	
	public static final String CIP_ATHT_CH_CD = "OCP";
	public static final String CIP_ATHT_SYS_CD = "OCP";
	
	public static final String EMP_NUMBER_AUTH_CD = "001";
	public static final String EMP_MAIL_AUTH_CD = "002";
	
	public static final String AUTH_CODE = "authCode";
	public static final String RET_URL = "retUrl";
	
	public static final String Client_IP = "client_ip";
	public static final String Client_Agent = "client_agent";
	
	public static final String Web2App_Id = "web2app_id";
	
	public static final String OFFLINE_LOGIN_RESPONSE = "offlineLoginResponse";
	
	public enum PasswordStrength {
		HIGH2(3, "STRONG RISK"), //
		HIGH(2, "HIGH_RISK"), //
		MID(1, "MID_RISK"), //
		LOW(0, "LOW_RISK");
		
		private int risk;
		private String desc;
		
		PasswordStrength(final int risk, final String desc) {
			this.risk = risk;
			this.desc = desc;
		}
		
		public int getRisk() {
			return this.risk;
		}
		public String getDesc() {
			return this.desc;
		}
		
		public static PasswordStrength get(int risk) {
			PasswordStrength[] passwordRisks = PasswordStrength.values();
			for (PasswordStrength passwordRisk : passwordRisks) {
				if (passwordRisk.getRisk() == risk) {
					return passwordRisk;
				}
			}
			return null;
		}
	}
	
}
