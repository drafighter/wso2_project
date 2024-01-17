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
 * Author	          : judahye
 * Date   	          : 2022. 10. 7..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v2.joinon.vo;

import org.springframework.util.StringUtils;

import com.amorepacific.oneap.common.code.ResultCode;

/**
 * <pre>
 * com.amorepacific.oneap.api.v2.joinon.vo 
 *    |_ JoinOnResultCode.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 10. 7.
 * @version : 1.0
 * @author  : judahye
 */

public enum JoinOnResultCode {
	JOINON_SUCCESS("00", "api.msg.joinon.success", "성공"),
	JOINON_ID_INVALID("01", "api.msg.joinon.id.invalid", "아이디가 존재하지않습니다"),
	JOINON_API_KEY("02", "api.msg.joinon.error.apikey", "잘못된 apiKey"),
	UNKNOWN_ERROR("99", "api.msg.joinon.unknown", "error"),
	
	JOINON_JOIN_SUCCESS("000", "api.msg.joinon.success", "정상처리되었습니다"),
//	JOINON_JOIN_REQ_TERMS("010", "api.msg.joinon.allAgreeReqTerms", "약관 미동의"),
	JOINON_JOIN_USER_DISABLED("020", "api.msg.joinon.disabled", "1달 이내 탈퇴 이력 有"),
//	JOINON_JOIN_ONLINE_EXIST("030", "api.msg.joinon.onlineExist", "온라인 회원 정보 존재"),
//	JOINON_JOIN_NOTAGREE_SMS("040", "api.msg.joinon.notAgreeSMS", "SMS 수신 미동의"),
	JOINON_JOIN_ALREADY_EXIST("050", "api.msg.joinon.alreadyExist", "이미 온라인에 가입된 통합회원입니다"),
	JOINON_JOIN_NOTFOUND("060", "api.msg.joinon.notFound", "존재하지 않는 통합고객번호입니다"),
	JOINON_JOIN_ID_ALREADY_EXIST("070", "api.msg.joinon.id.alreadyExist", "ID 중복"),
	JOINON_JOIN_ID_VALIDCHECK_FAIL("080", "api.msg.joinon.validCheckFail", "ID 형식이 맞지 않음"),
	JOINON_JOIN_NOTMATCH("090", "api.msg.joinon.notmatch", "통합회원정보와 실명 인증 정보가 일치하지 않습니다"),
	JOINON_JOIN_SYSTEM_ERROR("100", "api.msg.joinon.error.system", "회원가입 중 에러가 발생되었습니다."),
	
	JOINON_JOIN_ID_NOTEXIST("010", "api.msg.joinon.id,notExist", "존재하지 않는 ID"),
	JOINON_JOIN_NOTFOUNDS("020", "api.msg.joinon.notFound", "존재하지 않는 통합번호"),
	JOINON_JOIN_INVALID_PARAM("030", "api.msg.joinon.invalidParam", "잘못된 파라미터입니다"),
	JOINON_JOIN_ALREADYEXIST("040", "api.msg.joinon.alreadyExist", "이미 가입된 통합회원입니다"),
	JOINON_JOIN_PARAM_EMPTY("050", "api.msg.joinon.requiredParamEmpty", "입력 값이 없습니다"),
	JOINON_JOIN_INVALID_PARAMS("060", "api.msg.joinon.invalidParam", "잘못된 파라미터입니다"),
	
	JOINON_JOIN_ID_EXIST("010", "api.msg.joinon.id.alreadyExist", "ID 중복"),
	JOINON_JOIN_VALIDCHECK_FAIL("030", "api.msg.joinon.validCheckFail", "ID 형식이 맞지 않음"),
	
	JOINON_JOIN_ID_OK("OK", "api.msg.joinon.id.ok", "중복된 아이디 없음"),
	JOINON_JOIN_ID_NOK("NOK", "api.msg.joinon.id.nok", "중복된 아이디 있음"),
	
	JOINON_JOIN_REQ_PARAM_EMPTY("400", "api.msg.joinon.requiredParamEmpty", "필수 파라미터 부족"),
	JOINON_JOIN_FAIL_SAME("401", "api.msg.joinon.failSame", "기 등록된 패스워드"),
	JOINON_JOIN_FAIL_CONFIRM("402", "api.msg.joinon.failConfirm", "패스워드 불일치"),
	JOINON_JOIN_PWD_ERROR("900", "api.msg.joinon.pwd.error", "비밀번호 변경 중 오류 발생 or 존재하지 않는 아이디")	;
	private String code;
	private String msgCode;
	private String message;
	
	JoinOnResultCode(final String code, final String msgCode, final String message) {
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
