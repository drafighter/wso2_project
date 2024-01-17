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
 * Date   	          : 2023. 2. 19..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v3.membership.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.v3.membership.vo 
 *    |_ UnLinkMembershipVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 2. 19.
 * @version : 1.0
 * @author  : hjw0228
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnLinkMembershipVo {
	
	@ApiModelProperty(required = true,		value = "제휴사 경로구분코드",		position = 0)
	private String chCd;
	
	@ApiModelProperty(required = true,		value = "암호화된 통합고객번호",		position = 1)
	private String xincsNo;

	@ApiModelProperty(required = true,		value = "제휴사 고객 Key",		position = 2)
	private String memberId;
}
