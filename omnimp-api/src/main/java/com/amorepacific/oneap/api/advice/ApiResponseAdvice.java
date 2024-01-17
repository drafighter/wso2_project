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
 * Date   	          : 2020. 7. 13..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.advice;

import javax.swing.text.View;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.amorepacific.oneap.common.vo.CommonVo;
import com.amorepacific.oneap.common.vo.OmniConstants;

/**
 * <pre>
 * com.amorepacific.oneap.api.advice 
 *    |_ ApiResponseAdvice.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 13.
 * @version : 1.0
 * @author : takkies
 */
@RestControllerAdvice("com.amorepacific.oneap.api")
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

	public static final String OK = "0000";
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		
		if (CommonVo.class.isAssignableFrom(returnType.getParameterType()) || void.class == returnType.getParameterType()) {
			return true;
		}
		
		if (ResponseEntity.class.isAssignableFrom(returnType.getParameterType()) || View.class.isAssignableFrom(returnType.getParameterType())) {
			return false;
		}
		throw new IllegalArgumentException("OMNI API response는 반드시 CommonVo를 상속하거나 void return type 이어야함.");
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		Object obj = body;
		
		if (!(obj instanceof CommonVo)) {
			obj = new CommonVo();
		}
		
		if (StringUtils.isEmpty(((CommonVo) obj).getResultCode())) {
			((CommonVo) obj).setResultCode(OK);
		}
		
		response.getHeaders().add(OmniConstants.TRX_UUID, MDC.get(OmniConstants.TRX_UUID));
		return obj;
	}

}
