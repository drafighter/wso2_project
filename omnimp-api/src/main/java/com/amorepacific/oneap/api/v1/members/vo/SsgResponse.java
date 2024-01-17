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
 * Author	          : judahye
 * Date   	          : 2022. 3. 10..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.members.vo;

import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.vo.CommonVo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.member.vo 
 *    |_ SsgResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 3. 10.
 * @version : 1.0
 * @author  : judahye
 */
@Getter
@Setter
public class SsgResponse extends CommonVo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1236836274853571095L;
	
	@ApiModelProperty(value = "처리 결과 메시지", position = 2)
	private String message;
	public void SetResponseInfo(ResultCode resultCode) {
		this.setResultCode(resultCode.getCode()); // add result code
		this.setMessage(resultCode.message());
	}
	
	private String trxUuid;
	
	private UserVo userVo;
}
