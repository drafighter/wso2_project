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
 * Date   	          : 2020. 9. 16..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.sns;

import com.amorepacific.oneap.common.vo.CommonVo;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.sns 
 *    |_ SnsTokenResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 16.
 * @version : 1.0
 * @author  : takkies
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SnsTokenResponse extends CommonVo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8876712054640412834L;
	private @JsonProperty("token_type") String tokenType;
	private @JsonProperty("access_token") String accessToken;
	private @JsonProperty("expires_in") int expiresIn; // Access 토큰 만료 시간(초)
	private @JsonProperty("refresh_token") String refreshToken;
	private @JsonProperty("refresh_token_expires_in") int refreshTokenExpiresIn; // Refresh 토큰 만료 시간(초)
	private @JsonProperty("scope") String scope;
	
	// FB
	private String expirationDate;
	private String id;
	
	//NA
	private String bearer;
	
	private String error;
	private String errorDescription;
	private String error_description;
	
	private SnsKakaoResponse body;
}
