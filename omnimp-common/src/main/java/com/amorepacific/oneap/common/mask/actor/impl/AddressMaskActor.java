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

import java.util.Locale;

import com.amorepacific.oneap.common.mask.actor.MaskActor;

/**
 * <pre>
 * com.apmorepacific.oneap.common.mask.actor.impl 
 *    |_ AddressMaskActor.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

public class AddressMaskActor implements MaskActor {

	/**
	 * <pre>
	 * <code>
	 *   String result = new Masker.Builder() //
	 *     .maskType(MaskActor.Type.ADDRESS) //
	 *     .maskValue("서울시 용산구 한강대로 100") //
	 *     .countryCode("KR") //
	 *     .build().masking()
	 *     // ==> 
	 * </code>
	 * </pre>
	 */
	@Override
	public String masking(final String maskValue, final String countryCode) {
		String replaceString = maskValue;
		StringBuilder builder = new StringBuilder();
		if (countryCode.equals(Locale.KOREA.getCountry())) {
			boolean chk01 = false;
			boolean chk02 = false;
			String addrArr[] = replaceString.split(" ");

			for (String addrs : addrArr) {
				String addr = addrs.concat(" ");
				String suffix = "";
				if (addr.contains("읍 ")) {
					suffix = "* ";
					chk01 = true;
				} else if (addr.contains("면 ")) {
					suffix = "* ";
					chk01 = true;
				} else if (addr.contains("동 ")) {
					suffix = "* ";
					chk01 = true;
				} else if (addr.contains("리 ")) {
					suffix = "* ";
					chk01 = true;
				} else if (addr.contains("가 ")) {
					suffix = "* ";
					chk01 = true;
				} else if (addr.contains("로 ")) {
					suffix = "* ";
					chk01 = true;
				} else if (addr.contains("길 ")) {
					suffix = "* ";
					chk01 = true;
				}
				if (chk01 && chk02) {
					builder.append(addr.replaceAll("[^ (),]", "*"));
				} else if (chk01 && !chk02) {
					builder.append(addr.replaceAll("[^ ]+([읍면동리가로길])", "***").trim());
					builder.append(suffix);
					chk02 = true;
				} else if (!chk01 && !chk02) {
					builder.append(addr);
				}
			}
		} else {
			builder.append(replaceString);
		}
		return builder.toString();
	}

}
