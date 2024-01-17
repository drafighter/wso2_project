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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.exception 
 *    |_ ApiError.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 10.
 * @version : 1.0
 * @author : takkies
 */

@Getter
@Setter
public class ApiError {
	@JsonIgnore
	@ApiModelProperty(value = "오류 상태값(HttpStatus)", position = 0)
	private Integer status;
	@ApiModelProperty(value = "오류 명칭", position = 1)
	private String error;
	@ApiModelProperty(value = "오류 메시지", position = 2)
	private String message;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss.SSS", timezone="GMT+9")
	@ApiModelProperty(value = "오류 발생시간", position = 3)
	private Date timestamp;
	@JsonIgnore
	@ApiModelProperty(value = "오류 세부정보들", position = 4)
	private List<ApiSubError> errors;
	@JsonIgnore
	private HttpStatus httpStatus;

	public void addSubError(ApiSubError subError) {
		if (errors == null) {
			errors = new ArrayList<>();
		}
		errors.add(subError);
	}

	private ApiError() {
		this.timestamp = new Date();
	}

	public ApiError(HttpStatus httpStatus) {
		this();
		this.httpStatus = httpStatus;
		this.status = httpStatus.value();
		this.error = httpStatus.name();
	}

	public ApiError(HttpStatus httpStatus, Throwable ex) {
		this();
		this.httpStatus = httpStatus;
		this.status = httpStatus.value();
		this.error = httpStatus.name();
		this.message = "Unexpected Error";
	}

	public ApiError(HttpStatus httpStatus, String message, Throwable ex) {
		this();
		this.httpStatus = httpStatus;
		this.status = httpStatus.value();
		this.error = httpStatus.name();
		this.message = message;
	}

	public void addValidationErrors(List<FieldError> fieldErrors) {
		for (FieldError fieldError : fieldErrors) {
			this.addValidationError(fieldError);
		}
	}

	private void addValidationError(FieldError fieldError) {
		this.addValidationError(fieldError.getObjectName(), fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage());
	}

	private void addValidationError(String object, String field, Object rejectedValue, String message) {
		addSubError(new ApiSubError(object, field, rejectedValue, message));
	}

	private void addValidationError(ObjectError objectError) {
		this.addValidationError(objectError.getObjectName(), objectError.getDefaultMessage());
	}

	private void addValidationError(String object, String message) {
		addSubError(new ApiSubError(object, message));
	}

	public void addValidationError(List<ObjectError> globalErrors) {
		for (ObjectError objectError : globalErrors) {
			this.addValidationError(objectError);
		}
	}
}
