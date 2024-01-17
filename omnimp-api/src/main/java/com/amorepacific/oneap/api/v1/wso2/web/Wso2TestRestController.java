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
 * Date   	          : 2020. 8. 3..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.wso2.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.exception.ApiBusinessException;
import com.amorepacific.oneap.api.v1.wso2.service.Wso2Scim2RestApiService;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2RestApiCreateUserVo;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2RestApiGetUserVo;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2RestApiPatchUserVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.wso2.web 
 *    |_ WSo2TestRestController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 3.
 * @version : 1.0
 * @author : hjw0228
 */
@Profile({"local", "default"})
@Api(tags = { "API v1  > wso2 > REST API" })
@Slf4j
@RestController
@RequestMapping("/v1/wso2rest")
public class Wso2TestRestController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	Wso2Scim2RestApiService wso2Scim2RestApiService;

	@ApiOperation( //
			value = "WSO2 Rest API Get User 테스트", //
			notes = "WSO2 Rest API Get User 테스트입니다.", //
			httpMethod = "GET", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@GetMapping("/Users")
	public ResponseEntity<String> getUsers( //
			@ApiParam(name = "wso2RestApiGetUserVo", value = "WSO2 Rest API 파라미터", required = true) //
			final Wso2RestApiGetUserVo wso2RestApiGetUserVo) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", wso2RestApiGetUserVo.toString());

		return wso2Scim2RestApiService.getUser(wso2RestApiGetUserVo);
	}
	
	@ApiOperation( //
			value = "WSO2 Rest API Get User By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Rest API Get User By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "GET", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@GetMapping("/Users/umUserId/{umUserId}")
	public ResponseEntity<String> getUserByUmUserId( //
			@ApiParam(name = "umUserId", type = "String", value = "User WSO2 ID(um_user_id)", required = true) //
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", umUserId);

		return wso2Scim2RestApiService.getUserByUmUserId(umUserId);
	}

	@ApiOperation( //
			value = "WSO2 Rest API Get User By Web ID 테스트", //
			notes = "WSO2 Rest API Get User By Web ID 테스트입니다.", //
			httpMethod = "GET", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@GetMapping("/Users/username/{username}")
	public ResponseEntity<String> getUserByUsername( //
			@ApiParam(name = "username", type = "String", value = "사용자 Web ID", required = true) //
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", username);

		return wso2Scim2RestApiService.getUserByUsername(username);
	}

	@ApiOperation( //
			value = "WSO2 Rest API Get User By 통합고객번호 테스트", //
			notes = "WSO2 Rest API Get User By 통합고객번호 테스트입니다.", //
			httpMethod = "GET", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@GetMapping("/Users/incsNo/{incsNo}")
	public ResponseEntity<String> getUserByIncsNo( //
			@ApiParam(name = "incsNo", type = "String", value = "통합고객번호", required = true) //
			@PathVariable final String incsNo) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", incsNo);

		return wso2Scim2RestApiService.getUserByIncsNo(incsNo);
	}

	/*
	 * @ApiOperation( // value = "WSO2 Rest API Get User By 사용자CI 테스트", // notes = "WSO2 Rest API Get User By 사용자CI 테스트입니다.", // httpMethod =
	 * "GET", // produces = "application/scim+json", // response = ResponseEntity.class)
	 * 
	 * @GetMapping("/Users/userCi/{userCi}") public ResponseEntity<String> getUserByUserCi( //
	 * 
	 * @ApiParam(name = "userCi", type = "String", value = "사용자CI", required = true) //
	 * 
	 * @PathVariable final String userCi) throws ApiBusinessException, Exception { log.info("WSO2 SCIM2 Rest API Parameter ==== {}", userCi);
	 * 
	 * return wso2Scim2RestApiService.getUserByUserCi(userCi); }
	 */

	@ApiOperation( //
			value = "WSO2 Rest API Create User 테스트", //
			notes = "WSO2 Rest API Create User 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@PostMapping("/Users")
	public ResponseEntity<String> postUsers( //
			@ApiParam(name = "wso2RestApiCreateUserVo", value = "WSO2 Rest API 파라미터", required = true) //
			final Wso2RestApiCreateUserVo wso2RestApiCreateUserVo) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", wso2RestApiCreateUserVo.toString());

		return wso2Scim2RestApiService.postUser(wso2RestApiCreateUserVo);
	}
	
	@ApiOperation( //
			value = "WSO2 Rest API Patch User By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Rest API Patch User By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "PATCH", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@PatchMapping("/Users/umUserId/{umUserId}")
	public ResponseEntity<String> patchUserByUmUserId( //
			@ApiParam(name = "wso2RestApiPatchUserVo", value = "WSO2 Rest API 파라미터", required = true) //
			final Wso2RestApiPatchUserVo wso2RestApiPatchUserVo,
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", wso2RestApiPatchUserVo);

		return wso2Scim2RestApiService.patchUserByUmUserId(umUserId, wso2RestApiPatchUserVo);
	}

	@ApiOperation( //
			value = "WSO2 Rest API Patch User By Web ID 테스트", //
			notes = "WSO2 Rest API Patch User By Web ID 테스트입니다.", //
			httpMethod = "PATCH", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@PatchMapping("/Users/username/{username}")
	public ResponseEntity<String> patchUserByUsername( //
			@ApiParam(name = "wso2RestApiPatchUserVo", value = "WSO2 Rest API 파라미터", required = true) //
			final Wso2RestApiPatchUserVo wso2RestApiPatchUserVo,
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", wso2RestApiPatchUserVo);

		return wso2Scim2RestApiService.patchUserByUsername(username, wso2RestApiPatchUserVo);
	}
	
	@ApiOperation( //
			value = "WSO2 Rest API Delete User By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Rest API Delete User By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "DELETE", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@DeleteMapping("/Users/umUserId/{umUserId}")
	public ResponseEntity<String> deleteUserByUmUserId( //
			@ApiParam(name = "umUserId", type = "String", value = "User WSO2 ID(um_user_id)", required = true) //
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", umUserId);

		return wso2Scim2RestApiService.deleteUserByUmUserId(umUserId);
	}
	
	@ApiOperation( //
			value = "WSO2 Rest API Delete User By Web ID 테스트", //
			notes = "WSO2 Rest API Delete User By Web ID 테스트입니다.", //
			httpMethod = "DELETE", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@DeleteMapping("/Users/username/{username}")
	public ResponseEntity<String> deleteUserByUsername( //
			@ApiParam(name = "username", type = "String", value = "사용자 Web ID", required = true) //
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", username);

		return wso2Scim2RestApiService.deleteUserByUsername(username);
	}
	
	@ApiOperation( //
			value = "WSO2 Rest API Delete User Session By User No(um_id) 테스트", //
			notes = "WSO2 Rest API Delete User Session By User No(um_id) 테스트입니다.", //
			httpMethod = "DELETE", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@DeleteMapping("/Users/sessions/userNo/{userNo}")
	public ResponseEntity<String> deleteUserSessionByUserNo( //
			@ApiParam(name = "userNo", type = "int", value = "사용자 번호(um_id)", required = true) //
			@PathVariable final int userNo) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", userNo);
		return wso2Scim2RestApiService.removeSessionByUserNo(userNo);
	}
	
	@ApiOperation( //
			value = "WSO2 Rest API Delete User Session By User Id(um_user_id) 테스트", //
			notes = "WSO2 Rest API Delete User Session By User Id(um_user_id) 테스트입니다.", //
			httpMethod = "DELETE", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@DeleteMapping("/Users/sessions/userId/{userId}")
	public ResponseEntity<String> deleteUserSessionByUserId( //
			@ApiParam(name = "userId", type = "String", value = "사용자 아이디(um_user_name)", required = true) //
			@PathVariable final String userId) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", userId);
		return wso2Scim2RestApiService.removeSessionByUserId(userId);
	}
	
	@ApiOperation( //
			value = "WSO2 Rest API Delete User Session By User Login Id(um_user_name) 테스트", //
			notes = "WSO2 Rest API Delete User Session By User Login Id(um_user_name) 테스트입니다.", //
			httpMethod = "DELETE", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@DeleteMapping("/Users/sessions/userName/{userName}")
	public ResponseEntity<String> deleteUserSessionByUserName( //
			@ApiParam(name = "userName", type = "String", value = "사용자 로그인 아이디(um_user_name)", required = true) //
			@PathVariable final String userName) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", userName);
		return wso2Scim2RestApiService.removeSessionByUserName(userName);
	}
	
	@ApiOperation( //
			value = "WSO2 Rest API Delete User Session By incsno 테스트", //
			notes = "WSO2 Rest API Delete User Session By incsno 테스트입니다.", //
			httpMethod = "DELETE", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@DeleteMapping("/Users/sessions/iscsNo/{incsNo}")
	public ResponseEntity<String> deleteUserSessionByIncsNo( //
			@ApiParam(name = "incsNo", type = "String", value = "고객통합번호", required = true) //
			@PathVariable final String incsNo) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", incsNo);
		return wso2Scim2RestApiService.removeSessionByiscsNo(incsNo);
	}
	
	@ApiOperation( //
			value = "WSO2 Rest API GET User Session By User Id(um_user_id) 테스트", //
			notes = "WSO2 Rest API GET User Session By User Id(um_user_id) 테스트입니다.", //
			httpMethod = "GET", //
			produces = "application/scim+json", //
			response = ResponseEntity.class)
	@GetMapping("/Users/sessions/userId/{userId}")
	public ResponseEntity<String> getUserSessionByUserId( //
			@ApiParam(name = "userId", type = "String", value = "User Id(um_user_id)", required = true) //
			@PathVariable final String userId) throws ApiBusinessException, Exception {
		log.info("WSO2 SCIM2 Rest API Parameter ==== {}", userId);
		return wso2Scim2RestApiService.getSessionByUserId(userId);
	}
}
