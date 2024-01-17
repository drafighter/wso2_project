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
 * Author	          : jspark2
 * Date   	          : 2021. 2. 17..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.wso2.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.wso2.vo 
 *    |_ Wso2RestApiUserVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 2. 17.
 * @version : 1.0
 * @author  : jspark2
 */

@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Wso2RestApiUserSessionVo {
	@ApiModelProperty(value = "Amore Pacific User Session limit", dataType = "java.lang.Integer", position = 0)
	private Integer limit;
	
	@ApiModelProperty(value = "Amore Pacific User Session offset", dataType = "java.lang.Integer", position = 1)
	private Integer offset;
	
	@ApiModelProperty(value = "Amore Pacific User Session filter", dataType = "java.lang.String", position = 2)
	private String filter;
	
	@ApiModelProperty(value = "Amore Pacific User Session sort", dataType = "java.lang.String", position = 3)
	private String sort;
}
