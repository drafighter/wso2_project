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
 * Author	          : hkdang
 * Date   	          : 2020. 9. 18..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.sns;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.sns 
 *    |_ SnsKakaoResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 18.
 * @version : 1.0
 * @author  : hkdang
 */

@Data
public class SnsKakaoResponse {
	
	private String token_type;
	private String access_token;
	private int expires_in; // Access 토큰 만료 시간(초)
	private String refresh_token;
	private int refresh_token_expires_in; // Refresh 토큰 만료 시간(초)
	private String scope;

}
