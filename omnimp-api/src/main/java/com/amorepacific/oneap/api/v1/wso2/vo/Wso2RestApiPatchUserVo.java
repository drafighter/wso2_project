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
 * Date   	          : 2020. 8. 11..
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
 *    |_ Wso2RestApiPatchUserVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 11.
 * @version : 1.0
 * @author  : hjw0228
 */

@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Wso2RestApiPatchUserVo {

	@ApiModelProperty(value = "Amore Pacific User Web Login ID", dataType = "java.lang.String", position = 0)
	private String userName;
	
	@ApiModelProperty(value = "Amore Pacific User Full Name", dataType = "java.lang.String", position = 2)
	private String fullName;
	
	@ApiModelProperty(value = "Amore Pacific 통합 고객 번호", dataType = "java.lang.String", position = 3)
	private String incsNo;
	
	/*
	 * @ApiModelProperty(value = "Amore Pacific 휴대폰 번호", dataType = "java.lang.String", position = 4) private String mobile;
	 */
	
	/*
	 * @ApiModelProperty(value = "Amore Pacific User CI", dataType = "java.lang.String", position = 4) private String userCi;
	 */
}
