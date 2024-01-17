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
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.amorepacific.oneap.common.mask.actor.MaskActor;

/**
 * <pre>
 * com.apmorepacific.oneap.common.mask.actor.impl 
 *    |_ UserNameMaskActor.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

public class UserNameMaskActor implements MaskActor {

	/**
	 * <pre>
	 * <code>
	 *   String result = new Masker.Builder() //
	 *     .maskType(MaskActor.Type.USERNAME) //
	 *     .maskValue("홍길동") //
	 *     .countryCode("KR") //
	 *     .build().masking()
	 *     // ==> 홍*동
	 * </code>
	 * </pre>
	 */
	@Override
	public String masking(final String maskValue, final String countryCode) {
		
		if (StringUtils.isEmpty(maskValue)) {
			return "";
		}
		
		String replaceString = maskValue;
		if (countryCode.equals(Locale.KOREA.getCountry())) {
			String pattern = "";
			if (maskValue.length() == 2) {
				pattern = "^(.)(.+)$";
			} else {
				pattern = "^(.)(.+)(.)$";
			}
			Matcher matcher = Pattern.compile(pattern).matcher(maskValue);
			if (matcher.matches()) {
				replaceString = "";
				for (int i = 1; i <= matcher.groupCount(); i++) {
					String replaceTarget = matcher.group(i);
					if (i == 2) {
						char[] c = new char[replaceTarget.length()];
						Arrays.fill(c, '*');
						replaceString = replaceString.concat(String.valueOf(c));
					} else {
						replaceString = replaceString.concat(replaceTarget);
					}
				}
			}
		} else {
			final String names[] = maskValue.split(" ");
			if (names != null && names.length == 2) {
				final String firstname = names[0].replaceAll(FIRSTNAME_PATTERN, "*");
				final String lastname = names[1].replaceAll(LASTNAME_PATTERN, "*");
				replaceString = String.join("", new String[] { firstname, " ", lastname });
			} else {
				replaceString = maskValue.replaceAll(FIRSTNAME_PATTERN, "*");
			}
		}
		return replaceString;
	}

}
