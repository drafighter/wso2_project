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
 * Date   	          : 2023. 9. 6..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.offline.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * com.amorepacific.oneap.auth.offline.vo 
 *    |_ OfflineLoginRequest.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 9. 6.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflineLoginRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4123173507051866687L;
	// private String S_CONTROLLER;
	// private String S_METHOD;
	private String chnCd;
	private String login_id;
	private String login_pw;
	// private String eqDiv;
}
