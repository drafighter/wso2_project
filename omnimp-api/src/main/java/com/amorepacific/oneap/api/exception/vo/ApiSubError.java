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
 * Date   	          : 2020. 7. 10..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.exception.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.exception 
 *    |_ ApiSubError.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 10.
 * @version : 1.0
 * @author : takkies
 */
@Setter
@Getter
public class ApiSubError {
	@ApiModelProperty(value = "FieldError 발생 객체", position = 0)
	private String object;
	@ApiModelProperty(value = "FieldError 발생 필드", position = 1)
	private String field;
	@ApiModelProperty(value = "FieldError 발생 세부 메시지", position = 2)
	private String message;
	@ApiModelProperty(value = "FieldError 발생 사유 객체", position = 3)
	private Object rejectedValue;

	public Object getRejectedValue() {
		return rejectedValue;
	}

	public ApiSubError(String object, String message) {
		this.object = object;
		this.message = message;
	}

	public ApiSubError(String object, String field, Object rejectedValue, String message) {
		this.object = object;
		this.field = field;
		this.rejectedValue = rejectedValue;
		this.message = message;
	}

}
