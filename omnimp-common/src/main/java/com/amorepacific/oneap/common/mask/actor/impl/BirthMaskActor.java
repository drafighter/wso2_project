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

import com.amorepacific.oneap.common.mask.actor.MaskActor;

/**
 * <pre>
 * com.apmorepacific.oneap.common.mask.actor.impl 
 *    |_ BirthMaskActor.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

public class BirthMaskActor implements MaskActor {

	/**
	 * <pre>
	 * <code>
	 *   String result = new Masker.Builder() //
	 *     .maskType(MaskActor.Type.BIRTH) //
	 *     .maskValue("850123") //
	 *     .build().masking()
	 *     // ==> 
	 * </code>
	 * </pre>
	 */
	@Override
	public String masking(final String maskValue, final String countryCode) {
		String replaceString = maskValue;
		replaceString = replaceString.replaceAll("[^\\d]", "");
		return replaceString.replaceAll("[\\d]", "*");
	}

}
