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
 * Date   	          : 2021. 11. 10..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.mgmt.vo 
 *    |_ CheckSnsIdVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 11. 10.
 * @version : 1.0
 * @author  : hjw0228
 */

@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckSnsIdVo {
	
	@ApiModelProperty(required = true, 		value = "경로구분코드", 		position = 0)
	private String chCd;
	
	@ApiModelProperty(required = true, 		value = "SNS 아이디", 		position = 1)
	private String snsId;

	@ApiModelProperty(required = true, 		value = "SNS 타입", 		position = 2)
	private String snsType;	
}
