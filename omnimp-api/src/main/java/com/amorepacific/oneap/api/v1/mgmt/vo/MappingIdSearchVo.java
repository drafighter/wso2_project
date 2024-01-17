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
 * Date   	          : 2020. 8. 27..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.mgmt.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.mgmt.vo 
 *    |_ MappingIdSearchVo.java
 * </pre>
 *
 * @desc    : 회원 - sns계정 맵핑 검색 vo
 * @date    : 2020. 8. 27.
 * @version : 1.0
 * @author  : hkdang
 */

@Getter
@Setter
public class MappingIdSearchVo {
	
	@ApiModelProperty(required = false,	value = "회원 아이디",	position = 0) 
	private String loginId;
	
	@ApiModelProperty(required = true,	value = "SNS 타입 (NA, CA, FB)",	position = 1)
	private String snsType;
	
	@ApiModelProperty(required = false,	value = "SNS ID",	position = 2)
	private String snsId;
}
