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
 * Date   	          : 2020. 9. 16..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.sns;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.sns 
 *    |_ snsTokenVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 16.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class SnsTokenVo {

	@ApiModelProperty(value = "SNS 코드", position = 0)
	private String code;
	@ApiModelProperty(value = "SNS 상태값", position = 1)
	private String state;
	@ApiModelProperty(value = "SNS 엑세스 토큰", position = 2)
	private String accessToken;
	@ApiModelProperty(value = "SNS 페이스북 사용자 아이디", position = 3)
	private String userId;
}
