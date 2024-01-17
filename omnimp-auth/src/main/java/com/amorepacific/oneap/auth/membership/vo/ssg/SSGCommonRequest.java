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

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * com.amorepacific.oneap.auth.membership.vo.ssg 
 *    |_ SSGCommonVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 3. 23.
 * @version : 1.0
 * @author  : hjw0228
 */

@ToString
@Getter
@Setter
public class SSGCommonRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2360740655797762513L;
	private String chCd;
	private String membershipId;
	private String apiKey;
	
	public SSGCommonRequest() {
		
	}
	
	public SSGCommonRequest(String chCd, String membershipId, String apiKey) {
		this.chCd = chCd;
		this.membershipId = membershipId;
		this.apiKey = apiKey; 
	}
}
