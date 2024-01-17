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
package com.amorepacific.oneap.common.validation;

import java.util.Locale;

import org.springframework.util.StringUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * <pre>
 * com.apmorepacific.oneap.common.validation 
 *    |_ Phone.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

public class Phone {

	private String phoneNumber;
	private String countryCode;

	public static class Builder {
		private String phoneNumber;
		private String countryCode;

		public Builder phoneNumber(final String phoneNumber) {
			this.phoneNumber = phoneNumber;
			return this;
		}

		public Builder countryCode(final String countryCode) {
			this.countryCode = countryCode;
			return this;
		}

		public Phone build() {
			return new Phone(this);
		}
	}

	private Phone(Builder builder) {
		this.phoneNumber = builder.phoneNumber;
		if (StringUtils.isEmpty(builder.countryCode)) {
			this.countryCode = Locale.KOREA.getCountry();
		} else {
			this.countryCode = builder.countryCode;
		}
	}

	public PhoneInfo getPhoneInfo() {
		String countrycd = this.countryCode;
		if (StringUtils.isEmpty(countrycd)) {
			countrycd = Locale.KOREA.getCountry();
		}
		PhoneInfo phonevo = new PhoneInfo();
		final PhoneNumberUtil phonenumberutil = PhoneNumberUtil.getInstance();
		try {
			final PhoneNumber phone = phonenumberutil.parse(this.phoneNumber, countrycd);
			phonevo.setValidNumber(phonenumberutil.isValidNumber(phone)); // 유효 전화번호인지 체크
			phonevo.setInternationalFormatNumber(phonenumberutil.format(phone, PhoneNumberFormat.INTERNATIONAL)); // 국가별 국제 전화번호(포맷)
			phonevo.setInternaitonalNoFormatNumber(phonenumberutil.format(phone, PhoneNumberFormat.E164)); // 국가별 국제 전화번호(포맷없음)
			phonevo.setCountryCode(phone.getCountryCode()); // 전화번호 국가코드 번호
			phonevo.setNationalFormatNumber(phonenumberutil.format(phone, PhoneNumberFormat.NATIONAL)); // 지역 전화번호(포맷)
			phonevo.setNationalNoFormatNumber(phone.getNationalNumber()); // 지역 전화번호(포맷없음, 첫번째 0 빠짐)
			phonevo.setLeadingZeros(phone.getNumberOfLeadingZeros()); // 0을 포함하는 숫자길이
			PhoneNumberUtil.PhoneNumberType phoneType = phonenumberutil.getNumberType(phone);
			phonevo.setNumberType(phoneType.name());
		} catch (NumberParseException e) {
			phonevo.setValidNumber(false);
			phonevo.setInternationalFormatNumber(this.phoneNumber);
			phonevo.setInternaitonalNoFormatNumber(this.phoneNumber);
			phonevo.setNationalFormatNumber(this.phoneNumber);
			phonevo.setNumberType(PhoneNumberUtil.PhoneNumberType.UNKNOWN.name());
		}
		return phonevo;
	}

	public boolean isValidatePhoneNumber() {
		final PhoneInfo phone = getPhoneInfo();
		return phone.isValidNumber();
	}

	public String displayInternationalPhoneNumber() {
		final PhoneInfo phone = getPhoneInfo();
		try {
			return phone.getInternationalFormatNumber();
		} catch (Exception e) {
			return this.phoneNumber;
		}
//		if (phone.isValidNumber()) {
//			return phone.getInternationalFormatNumber();
//		} else {
//			return this.phoneNumber;
//		}
	}

	public String internationalPhoneNumber() {
		final PhoneInfo phone = getPhoneInfo();
		try {
			return phone.getInternaitonalNoFormatNumber();
		} catch (Exception e) {
			return this.phoneNumber;
		}
		// if (phone.isValidNumber()) {
		// return phone.getInternaitonalNoFormatNumber();
		// } else {
		// return this.phoneNumber;
		// }
	}

	public String displayNationalPhoneNumber() {
		final PhoneInfo phone = getPhoneInfo();
		try {
			return phone.getNationalFormatNumber();
		} catch (Exception e) {
			return this.phoneNumber;
		}
//		if (phone.isValidNumber()) {
//			return phone.getNationalFormatNumber();
//		} else {
//			// return this.phoneNumber;
//			return phone.getNationalFormatNumber();
//		}
	}

}
