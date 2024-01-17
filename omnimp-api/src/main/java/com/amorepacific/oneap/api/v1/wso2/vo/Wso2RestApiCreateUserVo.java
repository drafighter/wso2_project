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
 *    |_ Wso2RestApiCreateUserVo.java
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
public class Wso2RestApiCreateUserVo {

	@ApiModelProperty(value = "Amore Pacific User Web Login ID", dataType = "java.lang.String", position = 0)
	private String userName;
	
	@ApiModelProperty(value = "Amore Pacific User Password", dataType = "java.lang.String", position = 1)
	private String userPassword;
	
	@ApiModelProperty(value = "Amore Pacific User Full Name", dataType = "java.lang.String", position = 2)
	private String fullName;
	
	@ApiModelProperty(value = "Amore Pacific 통합 고객 번호", dataType = "java.lang.String", position = 3)
	private String incsNo;
	
	/*
	 * @ApiModelProperty(value = "Amore Pacific 휴대폰 번호", dataType = "java.lang.String", position = 4) private String mobile;
	 */
	
	/*
	 * @ApiModelProperty(value = "Amore Pacific User CI", dataType = "java.lang.String", position = 4) private String userCi;
	 * 
	 * @ApiModelProperty(value = "Amore Pacific Account State", dataType = "java.lang.String", position = 5) private String accountState;
	 * 
	 * @ApiModelProperty(value = "Amore Pacific Account Locked", dataType = "java.lang.String", position = 6) private String accountLocked;
	 * 
	 * @ApiModelProperty(value = "Amore Pacific Account Disabled", dataType = "java.lang.String", position = 7) private String accountDisabled;
	 */
	
	@ApiModelProperty(value = "SCIM defined attributes parameter.", dataType = "java.lang.String", position = 10, hidden = true)
	private String attributes;
	
	@ApiModelProperty(value = "The name of the user store where filtering needs to be applied.", dataType = "java.lang.String", position = 17, example = "PRIMARY", hidden = true)
	private String domain;
}
