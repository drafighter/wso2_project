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
package com.amorepacific.oneap.common.mask.actor.impl;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.amorepacific.oneap.common.mask.actor.MaskActor;
import com.amorepacific.oneap.common.validation.Phone;

/**
 * <pre>
 * com.apmorepacific.oneap.common.mask.actor.impl 
 *    |_ MobileMaskActor.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

public class MobileMaskActor implements MaskActor {

	/**
	 * <pre>
	 * <code>
	 *   String result = new Masker.Builder() //
	 *     .maskType(MaskActor.Type.MOBILE) //
	 *     .maskValue("+82 10-1234-5678") //
	 *     .countryCode("KR")
	 *     .build().masking()
	 *     // ==> 
	 * </code>
	 * </pre>
	 */
	@Override
	public String masking(final String maskValue, final String countryCode) {
		
		if (StringUtils.isEmpty(maskValue)) {
			return "";
		}
		
		String replaceString = maskValue;
		Phone phone = new Phone.Builder() //
				.phoneNumber(replaceString) //
				.countryCode(countryCode) //
				.build();
		final String phonenumber = phone.displayNationalPhoneNumber();
		Matcher matcher = null;
		if (phonenumber.contains("-")) {
			matcher = Pattern.compile("^(\\d{2,3})-?(\\d{3,4})-?(\\d{4})$").matcher(phonenumber);
		} else {
			matcher = Pattern.compile("^(\\d{2,3})?(\\d{3,4})?(\\d{4})$").matcher(phonenumber);
		}
		if (matcher.find()) {
			String value = matcher.group(2);
			char[] c = new char[value.length()];
			Arrays.fill(c, '*');
			return phonenumber.replace(value, String.valueOf(c));

		}
		return phonenumber;
	}

}
