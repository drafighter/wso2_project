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
 * Date   	          : 2020. 7. 31..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

import com.amorepacific.oneap.common.code.ResultCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo 
 *    |_ BaseResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 31.
 * @version : 1.0
 * @author  : takkies
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse extends CommonVo {

	private static final long serialVersionUID = -6398530415025357829L;

	@ApiModelProperty(value = "처리 결과 상태값", position = 0)	
	private int status;	// 현재 미사용이나 ajax 에서 이미 사용하므로 jsonignore 제외해야함.
	
	@ApiModelProperty(value = "처리 결과 메시지 코드값", position = 1)
	private String msgCode;
	
	@ApiModelProperty(value = "처리 결과 메시지", position = 2)
	private String message;
	
	public void SetResponseInfo(ResultCode resultCode) {
		this.setResultCode(resultCode.getCode());
		this.setMsgCode(resultCode.getMsgCode());
		this.setMessage(resultCode.message());
	}
}
