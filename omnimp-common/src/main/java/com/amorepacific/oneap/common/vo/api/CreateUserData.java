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
 * Date   	          : 2020. 8. 10..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.mgmt.vo 
 *    |_ CreUserBy030Vo.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 10.
 * @version : 1.0
 * @author : yjhan
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserData {

	@ApiModelProperty(required = true, value = "통합고객번호", position = 0)
	private int incsNo;

	@ApiModelProperty(required = true, value = "회원아이디", position = 1)
	private String loginId;

	@ApiModelProperty(required = true, value = "비밀번호", position = 2)	
	private String password;
	
	@ApiModelProperty(required = true, value = "회원이름", position = 3)
	private String cn;

	/*
	@ApiModelProperty(required = true, value = "사용자 추가 속성 정보", position = 3)
	private UserAttrVo userAttribute;
	*/

	@ApiModelProperty(required = true, value = "가입(J)/탈퇴(L) 여부", position = 4)
	private String joinFlag;
}