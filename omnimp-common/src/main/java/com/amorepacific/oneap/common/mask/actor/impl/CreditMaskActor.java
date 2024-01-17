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
 *    |_ CeditMaskActor.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

public class CreditMaskActor implements MaskActor {

	/**
	 * <pre>
	 * <code>
	 *   String result = new Masker.Builder() //
	 *     .maskType(MaskActor.Type.CREDIT) //
	 *     .maskValue("1234-5678-9876-5432") //
	 *     .build().masking()
	 *     // ==> 
	 * </code>
	 * </pre>
	 */
	@Override
	public String masking(final String maskValue, final String countryCode) {
		// 신용카드와 체크카드 번호는 XXXX-XXXX-XXXX-XXXX 형식으로 4자리씩 총 16자리로 구성
		String replaceString = maskValue;
		Matcher matcher = Pattern.compile("^(\\d{4})-?(\\d{4})-?(\\d{4})-?(\\d{4})$").matcher(replaceString);
		if (matcher.find()) {
			String a = matcher.group(3);
			char[] c = new char[a.length()];
			Arrays.fill(c, '*');
			return replaceString.replace(a, String.valueOf(c));
		}
		return null;
	}

}
