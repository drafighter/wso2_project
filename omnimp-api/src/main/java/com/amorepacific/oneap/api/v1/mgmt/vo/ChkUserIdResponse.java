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
 * Date   	          : 2020. 8. 5..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.mgmt.vo;

import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.mgmt.vo 
 *    |_ ChkUserIdResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 5.
 * @version : 1.0
 * @author  : yjhan
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChkUserIdResponse extends ApiBaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4493837341691974205L;
	@ApiModelProperty(required = false,		value = "고객 웹 아이디",	 	 position = 0) 
	private String custWebId;
}
