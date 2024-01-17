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
package com.amorepacific.oneap.common.code;

import org.springframework.util.StringUtils;

/**
 * <pre>
 * com.amorepacific.oneap.common.code 
 *    |_ ResultCode.java
 * </pre>
 *
 * @desc    : Api Response ResultCode
 * @date    : 2020. 8. 26.
 * @version : 1.0
 * @author  : takkies
 */
public enum ResultCode {

	// common
	UNKNOWN_ERROR("9999", "api.msg.unknown", "알수없는 오류가 발생하였습니다."),
	SUCCESS("0000", "api.msg.success", "정상 처리 되었습니다."),
	BP_SUCCESS("000", "api.msg.success", "정상 처리 되었습니다."),
	REQ_REQUIRED_PARAM_EMPTY("0100", "api.msg.requiredParamEmpty", "필수 값이 누락 되었습니다."),
	REQ_INVALID_PARAM("0101", "api.msg.invalidParam", "필수 값이 유효하지 않습니다."),
	AUTH_API_KEY("8000", "api.msg.error.apikey", "API 호출을 위한 키 정보 인증 오류가 발생하였습니다."),
	NOT_EXIST_OFFLINE("8100","api.msg.error.notExistOffline", "오프라인이 존재하지 않는 채널 입니다."),
	SYSTEM_ERROR("9000", "api.msg.error.system", "시스템 오류가 발생하였습니다. 관리자에게 문의하시기 바랍니다."),
	// user
	USER_DISABLED("1000", "api.msg.user.disabled", "탈퇴 회원입니다."),
	USER_ALREADY_EXIST("1010", "api.msg.user.alreadyExist", "이미 등록된 온라인 회원입니다."),
	USER_NOT_FOUND("1010", "api.msg.user.notFound", "온라인 회원정보가 존재하지 않습니다."),
	USER_INVALID("1010", "api.msg.user.invalid", "입력한 회원정보가 유효하지 않습니다."),
	USER_SEARCH_NOT_FOUND("1010", "api.msg.user.invalid", "온라인 회원 정보 미존재"),
	USER_STATUS_CHANGE_FAIL("1020", "api.msg.user.statusChangeFail", "사용자 상태(잠김/휴면) 변경에 실패 하였습니다."),
	USER_DORMANCY("1030", "api.msg.user.dormancy", "휴면 회원입니다."),
	USER_JOIN_IMPOSSIBLE_30DAYS("1040", "api.msg.user.Impossible.30days", "회원가입 불가능 회원입니다.(탈퇴 후 30일 이내)"),
	USER_PWD_FAILED("1050", "api.msg.user.unlock", "아이디 또는 비밀번호가 맞지 않습니다. (7회 실패 시 접근이 제한됩니다.)"),
	USER_UNLOCK("1060", "api.msg.user.unlock", "로그인 7회 실패하였습니다.XX분 XX초 후에 다시 시도해주세요."),
	USER_UNLOCK_FAILED("1060", "api.msg.user.unlock", "로그인을 계속 실패하여 접근이 제한 되었습니다. 비밀번호 찾기를 하여 본인인증 후 다시 설정해주시기 바랍니다."),
	USER_UNLOCK_TRUE("1070", "api.msg.user.unlock", "로그인 연속 실패 이력이 있어서 접근이 제한된 상태입니다. 제한 해제를 위해서 본인인증을 해주세요."),
	// id
	ID_VALIDCHECK_FAIL("1000", "api.msg.user.validCheckFail", "ID는 영문 및 숫자만 허용됩니다."),
	ID_INVALID_LENGTH("1010", "api.msg.user.invalidLength", "ID는 최소 4자리, 최대 12자리 허용됩니다."),
	ID_ALREADY_EXIST("1020", "api.msg.id.alreadyExist", "이미 사용 중인 ID 입니다."),
	ID_INVALID("1030", "api.msg.id.invalid", "기존 아이디가 유효하지 않습니다."),
	// pw
	PWD_FAIL_SAME("2000", "api.msg.password.failSame", "이전 비밀번호와 동일한 비밀번호 입니다."),
	PWD_INVALID_POLICY("2010", "api.msg.password.invalidPolicy", "정책에 위반된 비밀번호 입니다."),
	PWD_UNUSABLE_CHAR("2020", "api.msg.password.unusableChar", "비밀번호에 사용 불가능한 문자가 포함되었습니다."),
	PWD_INVALID_LENGTH("2030", "api.msg.password.invalidLength", "비밀번호는 최소 8자리, 최대 16자리 허용됩니다."),
	PWD_COMBINATION_FAIL("2040", "api.msg.password.combinationFail", "비밀번호는 소문자,숫자,특수문자 중 최소 2가지 이상 조합 되어야 합니다."),
	PWD_FAIL_CONFIRM("2050", "api.msg.password.failConfirm", "입력 비밀번호와 확인 비밀번호가 불일치 합니다."),
	PWD_SAME_ID("2060", "api.msg.password.sameId", "아이디와 동일한 비밀번호는 사용 할 수 없습니다."),
	PWD_WRONG("2070", "api.msg.password.wrong", "비밀번호를 다시 확인해주세요."),
	// terms
	TERMS_NOT_FOUND("2000", "api.msg.terms.notFound", "약관동의 유형코드가 존재하지 않습니다."),
	TERMS_NOT_HIST("2000", "api.msg.terms.notHistory", "경로시스템의 약관동의 내역이 존재하지 않습니다."),
	TERMS_NOT_EXIST_REQ_TERMS("2000", "api.msg.terms.allAgreeReqTerms", "미동의 필수 약관이 존재하지 않습니다."),
	TERMS_MODIFY_LITTLE("2010", "api.msg.terms.modifyLittle", "일부 약관만 처리 되었습니다."),
	// sns
	SNS_NOT_FOUND_ASSO("2000", "api.msg.sns.notFoundAsso", "SNS 연동 내역이 존재하지 않습니다."),
	SNS_INVALID_TYPE("2010", "api.msg.sns.invalidType", "SNS 타입이 유효하지 않습니다."),
	SNS_NOT_FOUND_TYPE("2000", "api.msg.sns.notFoundType", "SNS 타입이 유효하지 않습니다."),	//or 등록되지 않은 SNS 타입 입니다.
	SNS_ALREADY_ASSO("2010", "api.msg.sns.alreadyAsso", "이미 SNS에 연동되어 있습니다."),
	SNS_NOT_FOUND_ASSO_DISCONECT_FAIL("2010", "api.msg.sns.notFoundAssoDisconnectFail", "SNS 연동 내역이 존재하지 않아 해제 할 수 없습니다."),
	SNS_ALREADY_ID("2020", "api.msg.sns.alreadyId", "이미 연결 된 SNS 아이디 입니다."),
	// search
	SEARCH_INVALID_PAGE("2000", "api.msg.search.invalidPage", "페이지 정보가 올바르지 않습니다."),
	// channel
	CHANNEL_WITHDRAW("1000", "api.msg.channel.withdraw", "경로 탈퇴 사용자입니다."),
	// membership
	EXPIRED_ACCESS_TOKEN("3000", "membership.msg.accessToken.expired", "만료된 인증 입니다."),
	MEMBERSHIP_ALREADY_LINKED("3010", "membership.msg.membership.already.linked", "이미 연동된 계정입니다."),
	MEMBERSHIP_NOT_LINKED("3020", "membership.msg.membership.not.linked", "연동 정보를 조회할 수 없습니다."),
	MEMBERSHIP_USER_DORMANCY("3030", "membership.msg.membership.user.dormancy", "고객님은 아모레퍼시픽 휴면고객이십니다. 아모레몰(www.amoremall.com)에 방문하여서 휴면 해지 후 다시 시도해주세요."),
	MEMBERSHIP_INVALID_PARAM("3040", "membership.msg.membership.invalid.param", "연결에 실패하였습니다. 네이버 고객센터에 문의 후 다시 시도해주세요."),
	MEMBERSHIP_USER_NOT_FOUND("3050", "membership.msg.membership.user.not.found", "연결에 실패하였습니다. 아모레퍼시픽 고객센터에 문의부탁드립니다.<br/>www.amoremall.com<br/>080-023-5454."),
	MEMBERSHIP_UNLINK_FAIL("3060", "membership.msg.membership.unlink.fail", "네이버 스마트스토어 x 뷰티포인트 연동회원 여부가 확인되지 않습니다.<br/>아모레퍼시픽 고객센터(080-023-5454)로 연락해 주세요."),
	MEMBERSHIP_RESTRICT_AGE("3070", "membership.msg.membership.restrict.age", "뷰티포인트 통합회원은 만 14세 이상 부터 가입 가능합니다.<br/>고객님의 양해 부탁드리며, 다음에 다시 이용해주시기 바랍니다."),
	MEMBERSHIP_SYSTEM_ERROR("3090", "membership.msg.membership.error.system", "서버 오류입니다. 잠시 후 다시 시도해주세요."),
	// crypto
	ENCRYPTION_ERROR("4000", "api.msg.encryption.error", "암호화 중 오류가 발생하였습니다."),
	DECRYPTION_ERROR("4001", "api.msg.decryption.error", "복호화 중 오류가 발생하였습니다."),
	//abusing
	LOCK_ACCESS_LIMIT_USER("2000","api.msg.abusing.lock.limit","어뷰징 Lock 접근 제한 계정입니다."),
	LOCK_TRUE_USER("1000","api.msg.abusing.lock.user","어뷰징 Lock 계정입니다."),
	LOCK_CLEAR_USER("0000","api.msg.abusing.clear.user","정상 계정입니다.")
	;
	
	private String code;
	private String msgCode;
	private String message;
	
	ResultCode(final String code, final String msgCode, final String message) {
		this.code = code;
		this.msgCode = msgCode;
		this.message = message;
	}
	
	public String getCode() {
		return this.code;
	}
	public String getMsgCode() {
		return this.msgCode;
	}
	public String message() {
		return this.message;
	}
	
	public static ResultCode get(String code) {
		if ( StringUtils.isEmpty(code) ) {
			return ResultCode.UNKNOWN_ERROR;
		}
		ResultCode[] resultCodes = ResultCode.values();
		for ( ResultCode resultCode : resultCodes ) {
			if ( resultCode.getCode().equals(code) ) {
				return resultCode;
			}
		}
		return ResultCode.UNKNOWN_ERROR;
	}
}
