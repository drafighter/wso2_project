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
 * Date   	          : 2020. 11. 24..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.check.actor.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amorepacific.oneap.common.check.CheckResponse;
import com.amorepacific.oneap.common.check.actor.CheckActor;
import com.amorepacific.oneap.common.vo.OmniConstants;

/**
 * <pre>
 * com.amorepacific.oneap.common.check.actor.impl 
 *    |_ IdCharCheckActor.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 24.
 * @version : 1.0
 * @author  : takkies
 */

public class IdCharCheckActor implements CheckActor {

	@Override
	public CheckResponse check(String checkValue, String compareValue, String beforeValue) {
		CheckResponse response = new CheckResponse();
		
		// 영문(대소문자), 숫자
		Matcher match = Pattern.compile(ID_CHAR_PATTERN).matcher(checkValue);

		if (match.find()) {
			response.setCode(Integer.toString(SUCCESS));
			response.setStatus(SUCCESS);
			response.setMessage("success");
		} else {
			response.setCode(OmniConstants.ID_VALIDATION_FAIL);
			response.setStatus(FAIL);
			response.setMessage("fail");
		}
		return response;
	}

}
