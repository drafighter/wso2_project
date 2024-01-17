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

import com.amorepacific.oneap.common.mask.actor.MaskActor;

/**
 * <pre>
 * com.apmorepacific.oneap.common.mask.actor.impl 
 *    |_ EmailMaskActor.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

public class EmailMaskActor implements MaskActor {

	/**
	 * <pre>
	 * <code>
	 *   String result = new Masker.Builder() //
	 *     .maskType(MaskActor.Type.EMAIL) //
	 *     .maskValue("beyondoubt@hotmail.com") //
	 *     .build().masking()
	 *     // ==> 
	 * </code>
	 * </pre>
	 */
	@Override
	public String masking(final String maskValue, final String countryCode) {
		String replaceString = maskValue;
		final String regex = "\\b(\\S+)+@(\\S+.\\S+)";
		Matcher useridmatcher = Pattern.compile(regex).matcher(replaceString);
		if (useridmatcher.find()) {
			String id = useridmatcher.group(1); // 마스킹 처리할 부분인 userId
			if (id.length() < 5) {
				return id.replaceAll(LAST_1_CHAR_PATTERN, "*").concat("@").concat(useridmatcher.group(2));
			} else {
				Matcher matcher = Pattern.compile("^(....)(.*)([@]{1})(.*)$").matcher(maskValue);
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
			}
		}
		return replaceString;
	}

}
