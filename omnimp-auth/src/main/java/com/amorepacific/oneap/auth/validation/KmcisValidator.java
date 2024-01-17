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
 * Date   	          : 2020. 8. 12..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.cert.vo.KmcisResult;
import com.amorepacific.oneap.common.util.LocaleUtil;
import com.amorepacific.oneap.common.util.StringUtil;

/**
 * <pre>
 * com.amorepacific.oneap.auth.validation 
 *    |_ KmcisValidator.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 12.
 * @version : 1.0
 * @author : takkies
 */
public class KmcisValidator {

	public static KmcisResult validate(final KmcisResult result) {
		if (StringUtils.isEmpty(result.getCertNum()) || result.getCertNum().length() > 40) {
			return new KmcisResult(-2, "요청번호 비정상");
		}

		if (StringUtils.hasText(result.getDate()) && (result.getDate().length() != 14 || !StringUtil.checkParameter("[0-9]*", result.getDate()))) {
			return new KmcisResult(-3, "요청일시 비정상");
		}

		if (StringUtils.hasText(result.getCertMet()) && (result.getCertMet().length() != 1 || !StringUtil.checkParameter("[A-Z]*", result.getCertMet()))) {
			return new KmcisResult(-4, "본인인증방법 비정상");
		}

		if (StringUtils.hasText(result.getPhoneNo()) && ((result.getPhoneNo().length() != 10 && result.getPhoneNo().length() != 11) || !StringUtil.checkParameter("[0-9]*", result.getPhoneNo()))) {
			return new KmcisResult(-5, "휴대폰번호 비정상");
		}

		if (StringUtils.hasText(result.getPhoneCorp()) && (result.getPhoneCorp().length() != 3 || !StringUtil.checkParameter("[A-Z]*", result.getPhoneCorp()))) {
			return new KmcisResult(-6, "이동통신사 비정상");
		}

		if (StringUtils.hasText(result.getBirthDay()) && (result.getBirthDay().length() != 8 || !StringUtil.checkParameter("[0-9]*", result.getBirthDay()))) {
			return new KmcisResult(-7, "생년월일 비정상");
		}

		if (StringUtils.hasText(result.getGender()) && (result.getGender().length() != 1 || !StringUtil.checkParameter("[0-9]*", result.getGender()))) {
			return new KmcisResult(-8, "성별 비정상");
		}

		if (StringUtils.hasText(result.getNation()) && (result.getNation().length() != 1 || !StringUtil.checkParameter("[0-9]*", result.getNation()))) {
			return new KmcisResult(-9, "내/외국인 비정상");
		}

		if (StringUtils.hasText(result.getName()) && (result.getName().length() > 60 || !StringUtil.checkParameter("[\\sA-Za-z가-힣.,-]*", result.getName()))) {
			return new KmcisResult(-10, "성명 비정상");
		}

		if (StringUtils.hasText(result.getResult()) && (result.getResult().length() != 1 || !StringUtil.checkParameter("[A-Z]*", result.getResult()))) {
			return new KmcisResult(-11, "결과값 비정상");
		}

		if (StringUtils.hasText(result.getMName()) && (result.getMName().length() > 60 || !StringUtil.checkParameter("[\\sA-Za-z가-?.,-]*", result.getMName()))) {
			return new KmcisResult(-12, "미성년자 성명 비정상");
		}

		if (StringUtils.hasText(result.getMBirthDay()) && (result.getMBirthDay().length() != 8 || !StringUtil.checkParameter("[0-9]*", result.getMBirthDay()))) {
			return new KmcisResult(-13, "미성년자 생년월일 비정상");
		}

		if (StringUtils.hasText(result.getMGender()) && (result.getMGender().length() != 1 || !StringUtil.checkParameter("[0-9]*", result.getMGender()))) {
			return new KmcisResult(-14, "미성년자 성별 비정상");
		}

		if (StringUtils.hasText(result.getMNation()) && (result.getMNation().length() != 1 || !StringUtil.checkParameter("[0-9]*", result.getMNation()))) {
			return new KmcisResult(-15, "미성년자 내/외국인 비정상");
		}
		if (StringUtils.hasText(result.getDate())) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", LocaleUtil.getLocale()); // 현재 서버 시각 구하기
			final String strCurrentTime = formatter.format(new Date());
			try {
				Date toDate = formatter.parse(strCurrentTime);
				Date fromDate = formatter.parse(result.getDate());
				long timediff = toDate.getTime() - fromDate.getTime();
				if (timediff < -30 * 60 * 1000 || 30 * 60 * 100 < timediff) {
					return new KmcisResult(-16, "비정상적인 접근(요청시간경과)");
				}
			} catch (ParseException e) {
				// NO PMD
			}
		}

		return result;
	}
}
