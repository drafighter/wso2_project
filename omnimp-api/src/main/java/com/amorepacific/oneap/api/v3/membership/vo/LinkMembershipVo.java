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
 * Date   	          : 2023. 2. 13..
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
 *    |_ LinkMembershipVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 2. 13.
 * @version : 1.0
 * @author  : hjw0228
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkMembershipVo {

	@ApiModelProperty(required = true,		value = "제휴사 경로구분코드",		position = 0)
	private String chCd;
	@ApiModelProperty(required = true,		value = "이름",		position = 1)
	private String fullName;
	@ApiModelProperty(required = true,		value = "생년월일",		position = 2)
	private String birthDay;
	@ApiModelProperty(required = true,		value = "휴대폰번호",		position = 3)
	private String phone;
	@ApiModelProperty(required = true,		value = "CI번호",		position = 4)
	private String ciNo;
	@ApiModelProperty(required = true,		value = "제휴사 고객 Key",		position = 5)
	private String memberId;
	@ApiModelProperty(required = true,		value = "개인정보 제3자 제공동의 여부",		position = 6)
	private String thrdPrtyTerm;
}
