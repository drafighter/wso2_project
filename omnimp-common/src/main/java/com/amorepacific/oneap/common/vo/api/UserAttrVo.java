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
 *    |_ UserAttrVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 10.
 * @version : 1.0
 * @author  : yjhan
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAttrVo {
	
	@ApiModelProperty(required = true,		value = "사용자 WEB ID 값", position = 0)
	private String uid;
	
	@ApiModelProperty(required = true,		value = "사용자명", position = 1)
	private String cn;
	
	@ApiModelProperty(required = true,		value = "통합고객번호", position = 1)
	private String incsNo;
	
	@ApiModelProperty(required = true,		value = "휴대폰번호", position = 1)
	private String mobile;

}
