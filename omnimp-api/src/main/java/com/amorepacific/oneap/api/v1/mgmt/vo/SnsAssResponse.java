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
 * Author	          : yjhan
 * Date   	          : 2020. 8. 4..
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
 *    |_ SnsAsssResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 4.
 * @version : 1.0
 * @author  : yjhan
 */
@Getter
@Setter
public class SnsAssResponse extends ApiBaseResponse {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 70434131773131528L;

	@ApiModelProperty(required = true, value = "통합고객번호", position = 0)
	private int incsNo;
	
	@ApiModelProperty(value = "회원아이디", position = 1)
	private String loginId;
	
	@ApiModelProperty(value = "SNS 연동 정보", position = 2)
	private SearchSnsVo[] snsInfos;
}
