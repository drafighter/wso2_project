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
 * Date   	          : 2020. 8. 26..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.mgmt.vo;

import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.mgmt.vo 
 *    |_ SnsUnlinkResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 26.
 * @version : 1.0
 * @author  : hkdang
 */

@Getter
@Setter
public class SnsUnlinkResponse extends ApiBaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3977380750798960564L;

	@ApiModelProperty(required = true, value = "통합고객번호", position = 0)
	private int incsNo;
	
	@ApiModelProperty(required = true, value = "회원아이디", position = 1)
	private String loginId;
	
	@ApiModelProperty(required = true, value = "해제된 SNS 연동 타입", position = 2)
	private String snsType;
	
}