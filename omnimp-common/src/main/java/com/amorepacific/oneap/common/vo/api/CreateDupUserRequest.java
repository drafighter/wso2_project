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
 * Date   	          : 2020. 11. 9..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ CreateDupUserRequest.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 9.
 * @version : 1.0
 * @author  : takkies
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateDupUserRequest {

	@ApiModelProperty(required = true, value = "탈퇴 통합고객번호", position = 0)
	private int incsNo;
	@ApiModelProperty(required = true, value = "신규 가입 통합고객번호", position = 1)
	private int incsNoNew;	
	@ApiModelProperty(required = true, value = "회원아이디 배열", position = 2)
	private String[] loginId;	
	@ApiModelProperty(required = true, value = "경로코드", position = 3)
	private String chCd;

}
