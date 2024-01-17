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
 * Author	          : hjw0228
 * Date   	          : 2022. 3. 23..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.membership.vo.ssg;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 * com.amorepacific.oneap.auth.membership.vo.ssg 
 *    |_ SSGUserInfoResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 3. 23.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class SSGUserInfoResponse extends SSGCommonResponse {/**
	 * 
	 */
	private static final long serialVersionUID = -616788519437744741L;
	private UserInfoData data;
	private String name;
	private String ciNo;
	private String phoneNumber;
	
	@Data
	public static class UserInfoData {
		private String mbrId;
		private List<Support> supportList;
	}
	
	@Data
	public static class Support {
		private String supportType;
		private String supportValue;
	}
}
