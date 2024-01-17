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
 * Date   	          : 2020. 9. 7..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import java.util.List;

import com.amorepacific.oneap.common.vo.CommonVo;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ ChUserResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 7.
 * @version : 1.0
 * @author  : takkies
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SearchChCustResponse extends CommonVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7078177450985807316L;

	@ApiModelProperty(value = "처리 결과 메시지", position = 2)
	private String message;
	
	@ApiModelProperty(value = "조회된 경로 사용자 정보", position = 2)
	private @JsonProperty("UserInfo") UserInfo userInfo[];
	
	public void addUserInfo(List<UserInfo> userinfos) {
		if (userInfo == null) {
			userInfo = new UserInfo[userinfos.size()];
		}
		
		userInfo = userinfos.toArray(new UserInfo[userinfos.size()]);
	}
	
}
