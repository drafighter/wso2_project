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
 * Date   	          : 2020. 10. 12..
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
 *    |_ ChUserVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 10. 12.
 * @version : 1.0
 * @author  : hkdang
 */

@Getter
@Setter
public class ChUserIncsVo {
	@ApiModelProperty(required = true,		value = "경로구분코드",		 position = 0)
	private String chCd; 
	
	@ApiModelProperty(required = true,		value = "경로아이디",		 position = 1)
	private String webId;
	
	@ApiModelProperty(required = true,		value = "현재통합고객번호",		 position = 2)
	private int asisIncsNo;
	
	@ApiModelProperty(required = true,		value = "변경할통합고객번호", 	 position = 3)
	private int tobeIncsNo;
	
}
