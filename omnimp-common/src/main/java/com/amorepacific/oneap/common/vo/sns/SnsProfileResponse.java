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
 *    |_ SnsProfileResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 16.
 * @version : 1.0
 * @author  : takkies
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SnsProfileResponse extends CommonVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3590612476494083521L;

	private String id;
	
	// KA	
	private @JsonProperty("kakao_account") SnsKakaoAccount kakaoAccount;
	
	private String msg;
	private int code;
	
	// NA
	private String resultcode;
	private String message;
	private SnsNaverAccountResponse response;
	
	// FB
	private String birthday;
	private String email;
	private String firstName;
	private String lastName;
	private String name;
	
	private String gender;
}
