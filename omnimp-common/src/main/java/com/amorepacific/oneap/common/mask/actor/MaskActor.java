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
package com.amorepacific.oneap.common.mask.actor;

/**
 * <pre>
 * com.apmorepacific.oneap.common.mask.actor 
 *    |_ MaskActor.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

public interface MaskActor {
	
	public static final String LAST_1_CHAR_PATTERN = "(.{1}$)";

	public static final String FIRSTNAME_PATTERN = "(?<=.{5}).";

	public static final String LASTNAME_PATTERN = "(?<=.{0}).";

	public static final String ID_PATTERN = "(?<=.{4}).";
	
	public enum Type {
		EMAIL, USERNAME, USERID, MOBILE, PHONE, ADDRESS, CREDIT, BANK, BIRTH;
	}

	public String masking(final String maskValue, final String countryCode);
	
}
