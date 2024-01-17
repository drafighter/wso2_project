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
 * Date   	          : 2020. 7. 9..
 * Description 	  : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.check.actor.impl;

import org.springframework.util.StringUtils;

import com.amorepacific.oneap.common.check.CheckResponse;
import com.amorepacific.oneap.common.check.actor.CheckActor;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;

/**
 * <pre>
 * com.apmorepacific.oneap.common.check.actor.impl 
 *    |_ PasswordCheckActor.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

public class PasswordCheckActor implements CheckActor {

	@Override
	public CheckResponse check(final String checkValue, final String compareValue, final String beforeValue) {

		CheckResponse response = new CheckResponse();

		if (StringUtils.hasText(compareValue)) { // 비밀번호 확인 값과 비교
			if (checkValue.compareTo(compareValue) != 0) { // 같지 않으면
				response.setCode(OmniConstants.PASSWORD_CONFIRM_FAIL);
				response.setStatus(FAIL);
				response.setMessage("confirm password is different, compare fail");
				return response;
			}
		}

		if (StringUtils.hasText(beforeValue)) { // 이전 비밀번호와 비교
			if (checkValue.compareTo(beforeValue) == 0) { // 같으면
				response.setCode(OmniConstants.PASSWORD_COMPARE_FAIL);
				response.setStatus(FAIL);
				response.setMessage("before password is same, compare fail");
				return response;
			}
		}

		if (checkValue.length() < 8 || checkValue.length() > 16) {
			//response.setCode(OmniConstants.PASSWORD_SIZE_FAIL.concat("(").concat(Integer.toString(checkValue.length())).concat(")"));
			response.setCode(OmniConstants.PASSWORD_SIZE_FAIL);
			response.setStatus(FAIL);
			response.setMessage(String.format("password length fail, current password length : %d", checkValue.length()));
			return response;
		}

		int status = SUCCESS;

		// 비밀번호는 영문의 경우 소문자만 허용하도록 해야함.
		//int isUpperEngCnt = checkValue.matches(IS_UPPER_ENG_PATTERN) ? 1 : 0;
		int isLowerEngCnt = checkValue.matches(IS_LOWER_ENG_PATTERN) ? 1 : 0;
		int isNumberCnt = checkValue.matches(IS_NUMBER_PATTERN) ? 1 : 0;
		int isSpecialCharCnt = checkValue.matches(IS_SPECIAL_CHAR_PATTERN) ? 1 : 0;
		int isWhiteSpaceCnt = checkValue.matches(IS_WHITESPACE_PATTERN) ? 1 : 0;

		//int isCnt = isUpperEngCnt + isLowerEngCnt + isNumberCnt + isSpecialCharCnt;
		int isCnt = isLowerEngCnt + isNumberCnt + isSpecialCharCnt;

		if (isCnt < 2 || isWhiteSpaceCnt == 1) {
			status = -5;
			response.setCode(OmniConstants.PASSWORD_COMBINATION_FAIL);
			response.setStatus(status);
			response.setMessage(String.format("special, lower english, number character combination check fail, combination result : %d, status : %d", isCnt, status));
		} else { // 성공 --> 강도 체크 한번 더
			int passwordStrength = getPasswordStrength(isCnt, checkValue);
			response.setStatus(status);
			response.setCode(OmniConstants.PasswordStrength.get(passwordStrength).getDesc());
			response.setStrength(passwordStrength);
		}
		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 유효성 성공한 비.밀.번.호 strength 체크
	 * author   : takkies
	 * date     : 2020. 8. 24. 오전 10:28:58
	 * </pre>
	 * 
	 * @param checkCount 비.밀.번.호 체크한 체크 결과 카운트
	 * @param password 체크할 비.밀.번.호
	 * @return 비.밀.번.호 strength
	 */
	private int getPasswordStrength(final int checkCount, final String password) {
		int passwordStrength = 0;
		int sameCount = StringUtil.checkSameCount(password); // 동일문자 카운트
		int continueCount = StringUtil.checkContinueCnt(password); // 반복된문자 카운트
		int removeDupCount = StringUtil.checkRemoveDupCount(password); // 중복제거문자 카운트
		// 1. 4자리 이상 반복/연속한 숫자/문자 포함
		if (sameCount > 2 || continueCount > 2) {
			passwordStrength += 1;
		}
		// 2. 중복 제거 문자 수3개 이하
		if (removeDupCount >= 3) {
			passwordStrength += 1;
		}

		// 3. 비밀번호 10자리 미만 and 문자조합(영문 대,소문자, 숫자, 특수문자) 중
		// 3개 미만 구성 낮음 이하 레벨은 그냥 사용불가임
		if ((password.length() < 10) && (checkCount < 3)) {
			passwordStrength += 1;
		}
		// 2 보다크면 -> 위험
		// 1 이면 -> 보통
		// 0 이면 -> 안전
		return passwordStrength;
	}

}
