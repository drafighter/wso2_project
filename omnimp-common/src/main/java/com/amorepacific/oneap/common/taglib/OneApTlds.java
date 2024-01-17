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
package com.amorepacific.oneap.common.taglib;

import java.util.Locale;

import org.springframework.util.StringUtils;

import com.amorepacific.oneap.common.mask.Masker;
import com.amorepacific.oneap.common.mask.actor.MaskActor;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.LocaleUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.validation.Phone;
import com.amorepacific.oneap.common.vo.user.Customer;

/**
 * <pre>
 * com.amorepacific.oneap.common.taglib 
 *    |_ OneApTld.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 12.
 * @version : 1.0
 * @author  : takkies
 */
public class OneApTlds {
	
	public static String getMergePhoneNumber(final Customer customer, final Locale _locale) {
		if (customer == null) {
			return "";
		}
		return getPhoneNumber(StringUtil.mergeMobile(customer), _locale);
	}
	
	public static String getMergeMaskingPhoneNumber(final Customer customer, final Locale _locale) {
		final String phoneno = getMergePhoneNumber(customer, _locale);
		return getPhoneMask(phoneno, _locale);
	}

	public static String getPhoneNumber(final String phoneno, final Locale _locale) {
		if (StringUtils.isEmpty(phoneno)) {
			return "";
		}
		Locale locale = _locale == null ? LocaleUtil.getLocale() : _locale;
		Phone phone = new Phone.Builder() //
				.phoneNumber(phoneno) //
				.countryCode(locale.getCountry()) //
				.build();
		return phone.displayNationalPhoneNumber();
	}
	
	public static String getBirthDate(final String date) {
		if (StringUtils.isEmpty(date)) {
			return "";
		}
		return DateUtil.getBirthDate(date);
	}
	
	public static String getNameMask(final String name, final Locale _locale) {
		if (StringUtils.isEmpty(name)) {
			return "";
		}
		Locale locale = _locale == null ? LocaleUtil.getLocale() : _locale;
		return new Masker.Builder().maskType(MaskActor.Type.USERNAME) //
		.maskValue(name).countryCode(locale.getCountry()) //
		.build().masking();
	}
	
	public static String getPhoneMask(final String phone, final Locale _locale) {
		if (StringUtils.isEmpty(phone)) {
			return "";
		}
		Locale locale = _locale == null ? LocaleUtil.getLocale() : _locale;
		return new Masker.Builder(MaskActor.Type.MOBILE, phone, locale.getCountry()) //
		.build().masking();
	}
	
	public static String getIdMask(final String id) {
		if (StringUtils.isEmpty(id)) {
			return "";
		}
		return new Masker.Builder().maskType(MaskActor.Type.USERID) //
		.maskValue(id) //
		.build().masking();
	}
}
