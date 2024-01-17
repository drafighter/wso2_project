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
 * Date   	          : 2021. 2. 2..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ ChangeWebIdData.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 2. 2.
 * @version : 1.0
 * @author  : jspark2
 */
@Data
public class ChangeWebIdData {

	@ApiModelProperty(required = true,	value = "통합고객번호", position = 0) 
	private int incsNo; 
	
	@ApiModelProperty(required = true,	value = "기존 아이디", position = 1) 
	private String loginId;
	
	@ApiModelProperty(required = true,	value = "변경할 아이디", position = 2) 
	private String loginIdNew;
	
}
