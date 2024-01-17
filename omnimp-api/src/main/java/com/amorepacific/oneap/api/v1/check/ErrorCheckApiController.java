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
 * Date   	          : 2021. 5. 14..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.check;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.exception.ApiBusinessException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.check 
 *    |_ ErrorCheckApiController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 5. 14.
 * @version : 1.0
 * @author  : hjw0228
 */

@Api(tags = { "오류체크" })
@Slf4j
@RestController
@RequestMapping("/v1/errorcheck")
public class ErrorCheckApiController {
	
	@SuppressWarnings("static-access")
	@ApiOperation( //
			value = "Return Code 테스트", //
			notes = "Return Code 테스트입니다.", //
			httpMethod = "GET", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@GetMapping("/returnCode/{returnCode}")
	public ResponseEntity<String> returnCode( //
			@ApiParam(name = "returnCode", type = "String", value = "return code", required = true) //
			@PathVariable final String returnCode) throws ApiBusinessException, Exception {
		log.info("Omni Error Check - Return Code Parameter ==== {}", returnCode);
		
		try {
			for(HttpStatus httpStatus : HttpStatus.values()) {
				if(httpStatus.value() == Integer.parseInt(returnCode)) {
					return new ResponseEntity<>(httpStatus.name(), httpStatus);
				}
			}
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND.name(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ApiOperation( //
			value = "Timeout 테스트", //
			notes = "Timeout 테스트입니다.", //
			httpMethod = "GET", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@GetMapping("/timeout/{timeout}")
	public ResponseEntity<String> timeout( //
			@ApiParam(name = "timeout", type = "String", value = "time out", required = true) //
			@PathVariable final String timeout) throws ApiBusinessException, Exception {
		log.info("Omni Error Check - Timeout Parameter ==== {}", timeout);
		
		try {
			
			TimeUnit.MILLISECONDS.sleep(Integer.parseInt(timeout));
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		
		return new ResponseEntity<>(HttpStatus.OK.name(), HttpStatus.OK);
	}

}
