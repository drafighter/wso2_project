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
 * Date   	          : 2022. 5. 3..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.members.vo;

import java.io.Serializable;

import com.amorepacific.oneap.common.vo.CommonVo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.members.vo 
 *    |_ SsgUnLinkResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 5. 3.
 * @version : 1.0
 * @author  : hjw0228
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SsgUnLinkResponse extends CommonVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3711534939844160101L;
	
	@ApiModelProperty(value = "Status", hidden = false, position = 2)
	private int status;
	@ApiModelProperty(value = "Message", hidden = false, position = 3)
	private String message;
	@ApiModelProperty(value = "Data", hidden = false, position = 4)
	private String data;
}
