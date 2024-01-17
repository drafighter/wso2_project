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
 * Date   	          : 2020. 8. 12..
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.exception.ApiBusinessException;
import com.amorepacific.oneap.api.v1.wso2.service.Wso2CmmsSoapApiService;
import com.amorepacific.oneap.api.v1.wso2.service.Wso2RusmSoapApiService;
import com.amorepacific.oneap.api.v1.wso2.service.Wso2UpmSoapApiService;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2SoapApiCreateAssociatedVo;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2SoapApiMergeAssociatedVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.wso2.web 
 *    |_ Wso2TestSoapController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 12.
 * @version : 1.0
 * @author  : hjw0228
 */
@Profile({"local", "default"})
@Api(tags = { "API v1  > wso2 > SOAP API" })
@Slf4j
@RestController
@RequestMapping("/v1/wso2soap")
public class Wso2TestSoapController {

	@Autowired
	MessageSource messageSource;
	
	@Autowired
	Wso2UpmSoapApiService wso2UpmSoapApiService;
	
	@Autowired
	Wso2RusmSoapApiService wso2RusmSoapApiService;
	
	@Autowired
	Wso2CmmsSoapApiService wso2CmmsSoapApiService;
	
	@ApiOperation( //
			value = "WSO2 Soap API Get Associated SNS Information By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Soap API Get Associated SNS Information By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "GET", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@GetMapping("/associatedIDs/umUserId/{umUserId}")
	public ResponseEntity<String> getAssociatedIdsByUmUserId( //
			@ApiParam(name = "umUserId", type = "String", value = "User WSO2 ID(um_user_id)", required = true) //
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", umUserId);

		return wso2UpmSoapApiService.getAssociatedIdsByUmUserId(umUserId);
	}

	@ApiOperation( //
			value = "WSO2 Soap API Get Associated SNS Information By Web ID 테스트", //
			notes = "WSO2 Soap API Get Associated SNS Information By Web ID 테스트입니다.", //
			httpMethod = "GET", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@GetMapping("/associatedIDs/username/{username}")
	public ResponseEntity<String> getAssociatedIdsByUsername( //
			@ApiParam(name = "username", type = "String", value = "사용자 Web ID", required = true) //
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", username);

		return wso2UpmSoapApiService.getAssociatedIdsByUsername(username);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Create Associated SNS Information By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Soap API Create Associated SNS Information By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/associatedIDs/umUserId/{umUserId}")
	public ResponseEntity<String> postAssociatedIdsByUmUserId( //
			@ApiParam(name = "wso2SoapApiCreateAssociatedVo", value = "WSO2 Soap API 파라미터", required = true) //
			final Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo,
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", wso2SoapApiCreateAssociatedVo);

		return wso2UpmSoapApiService.postAssociatedIdsByUmUserId(umUserId, wso2SoapApiCreateAssociatedVo);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Create Associated SNS Information By Web ID 테스트", //
			notes = "WSO2 Soap API Create Associated SNS Information By Web ID 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/associatedIDs/username/{username}")
	public ResponseEntity<String> postAssociatedIdsByUsername( //
			@ApiParam(name = "wso2SoapApiCreateAssociatedVo", value = "WSO2 Soap API 파라미터", required = true) //
			final Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo,
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", wso2SoapApiCreateAssociatedVo);

		return wso2UpmSoapApiService.postAssociatedIdsByUsername(username, wso2SoapApiCreateAssociatedVo);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Merge Associated SNS Information By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Soap API Merge Associated SNS Information By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "PATCH", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PatchMapping("/associatedIDs/umUserId/{umUserId}")
	public ResponseEntity<String> patchAssociatedIdsByUmUserId( //
			@ApiParam(name = "wso2SoapApiMergeAssociatedVo", value = "WSO2 Soap API 파라미터", required = true) //
			final Wso2SoapApiMergeAssociatedVo wso2SoapApiMergeAssociatedVo,
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", wso2SoapApiMergeAssociatedVo);

		return wso2UpmSoapApiService.patchAssociatedIdsByUmUserId(umUserId, wso2SoapApiMergeAssociatedVo);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Merge Associated SNS Information By Web ID 테스트", //
			notes = "WSO2 Soap API Merge Associated SNS Information By Web ID 테스트입니다.", //
			httpMethod = "PATCH", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PatchMapping("/associatedIDs/username/{username}")
	public ResponseEntity<String> patchAssociatedIdsByUsername( //
			@ApiParam(name = "wso2SoapApiMergeAssociatedVo", value = "WSO2 Soap API 파라미터", required = true) //
			final Wso2SoapApiMergeAssociatedVo wso2SoapApiMergeAssociatedVo,
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", wso2SoapApiMergeAssociatedVo);

		return wso2UpmSoapApiService.patchAssociatedIdsByUsername(username, wso2SoapApiMergeAssociatedVo);
	}	
	
	@ApiOperation( //
			value = "WSO2 Soap API Delete Associated SNS Information By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Soap API Delete Associated SNS Information By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "DELETE", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@DeleteMapping("/associatedIDs/umUserId/{umUserId}")
	public ResponseEntity<String> deleteAssociatedIdsByUmUserId( //
			@ApiParam(name = "wso2SoapApiCreateAssociatedVo", value = "WSO2 Soap API 파라미터", required = true) //
			final Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo,
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", wso2SoapApiCreateAssociatedVo);

		return wso2UpmSoapApiService.deleteAssociatedIdsByUmUserId(umUserId, wso2SoapApiCreateAssociatedVo);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Delete Associated SNS Information By Web ID 테스트", //
			notes = "WSO2 Soap API Delete Associated SNS Information By Web ID 테스트입니다.", //
			httpMethod = "DELETE", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@DeleteMapping("/associatedIDs/username/{username}")
	public ResponseEntity<String> deleteAssociatedIdsByUsername( //
			@ApiParam(name = "wso2SoapApiCreateAssociatedVo", value = "WSO2 Soap API 파라미터", required = true) //
			final Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo,
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", wso2SoapApiCreateAssociatedVo);

		return wso2UpmSoapApiService.deleteAssociatedIdsByUsername(username, wso2SoapApiCreateAssociatedVo);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Update Password By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Soap API Update Password By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/password/umUserId/{umUserId}")
	public ResponseEntity<String> postPasswordByUmUserId( //
			@ApiParam(name = "oldPassword", value = "WSO2 Soap API 파라미터 (Old Password)", required = true) //
			final @RequestParam String oldPassword,
			@ApiParam(name = "newPassword", value = "WSO2 Soap API 파라미터 (New Password)", required = true) //
			final @RequestParam String newPassword,
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}, {}", oldPassword, newPassword);

		return wso2RusmSoapApiService.postPasswordByUmUserId(umUserId, oldPassword, newPassword);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Update Password By Web ID 테스트", //
			notes = "WSO2 Soap API Update Password By Web ID 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/password/username/{username}")
	public ResponseEntity<String> postPasswordByUsername( //
			@ApiParam(name = "oldPassword", value = "WSO2 Soap API 파라미터 (Old Password)", required = true) //
			final @RequestParam String oldPassword,
			@ApiParam(name = "newPassword", value = "WSO2 Soap API 파라미터 (New Password)", required = true) //
			final @RequestParam String newPassword,
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}, {}", oldPassword, newPassword);

		return wso2RusmSoapApiService.postPasswordByUsername(username, oldPassword, newPassword);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Reset Password By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Soap API Reset Password By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "PATCH", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PatchMapping("/password/umUserId/{umUserId}")
	public ResponseEntity<String> patchPasswordByUmUserId( //
			@ApiParam(name = "newPassword", value = "WSO2 Soap API 파라미터 (New Password)", required = true) //
			final @RequestParam String newPassword,
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", newPassword);

		return wso2RusmSoapApiService.patchPasswordByUmUserId(umUserId, newPassword);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Reset Password By Web ID 테스트", //
			notes = "WSO2 Soap API Reset Password By Web ID 테스트입니다.", //
			httpMethod = "PATCH", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PatchMapping("/password/username/{username}")
	public ResponseEntity<String> patchPasswordByUsername( //
			@ApiParam(name = "newPassword", value = "WSO2 Soap API 파라미터 (New Password)", required = true) //
			final @RequestParam String newPassword,
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", newPassword);

		return wso2RusmSoapApiService.patchPasswordByUsername(username, newPassword);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Lock User By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Soap API Lock User By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/lock/umUserId/{umUserId}")
	public ResponseEntity<String> postLockByUmUserId( //
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", umUserId);

		return wso2RusmSoapApiService.postLockByUmUserId(umUserId);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Lock User By Web ID 테스트", //
			notes = "WSO2 Soap API Lock User By Web ID 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/lock/username/{username}")
	public ResponseEntity<String> postLockByUsername( //
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", username);

		return wso2RusmSoapApiService.postLockByUsername(username);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API UnLock User By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Soap API UnLock User By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/unlock/umUserId/{umUserId}")
	public ResponseEntity<String> postUnLockByUmUserId( //
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", umUserId);

		return wso2RusmSoapApiService.postUnLockByUmUserId(umUserId);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API UnLock User By Web ID 테스트", //
			notes = "WSO2 Soap API UnLock User By Web ID 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/unlock/username/{username}")
	public ResponseEntity<String> postUnLockByUsername( //
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", username);

		return wso2RusmSoapApiService.postUnLockByUsername(username);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Disable User By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Soap API Disable User By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/disable/umUserId/{umUserId}")
	public ResponseEntity<String> postDisableByUmUserId( //
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", umUserId);

		return wso2RusmSoapApiService.postDisableByUmUserId(umUserId);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Disable User By Web ID 테스트", //
			notes = "WSO2 Soap API Disable User By Web ID 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/disable/username/{username}")
	public ResponseEntity<String> postDisableByUsername( //
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", username);

		return wso2RusmSoapApiService.postDisableByUsername(username);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Enable User By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Soap API Enable User By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/enable/umUserId/{umUserId}")
	public ResponseEntity<String> postEnableByUmUserId( //
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", umUserId);

		return wso2RusmSoapApiService.postEnableByUmUserId(umUserId);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Enable User By Web ID 테스트", //
			notes = "WSO2 Soap API Enable User By Web ID 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/enable/username/{username}")
	public ResponseEntity<String> postEnableByUsername( //
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", username);

		return wso2RusmSoapApiService.postEnableByUsername(username);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Update User failedLoginAttemptsBeforeSuccess By User WSO2 ID(um_user_id) 테스트", //
			notes = "WSO2 Soap API Update User failedLoginAttemptsBeforeSuccess By User WSO2 ID(um_user_id) 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/failedLoginAttemptsBeforeSuccess/umUserId/{umUserId}")
	public ResponseEntity<String> postFailedLoginAttemptsBeforeSuccessByUmUserId( //
			@ApiParam(name = "count", value = "WSO2 Soap API 파라미터 (Fail Count)", required = true) //
			final @RequestParam(value="count") int count,
			@PathVariable final String umUserId) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", umUserId);

		return wso2RusmSoapApiService.postFailedLoginAttemptsBeforeSuccessByUmUserId(umUserId, count);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Update User failedLoginAttemptsBeforeSuccess By Web ID 테스트", //
			notes = "WSO2 Soap API Update User failedLoginAttemptsBeforeSuccess By Web ID 테스트입니다.", //
			httpMethod = "POST", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@PostMapping("/failedLoginAttemptsBeforeSuccess/username/{username}")
	public ResponseEntity<String> postFailedLoginAttemptsBeforeSuccessByUsername( //
			@ApiParam(name = "count", value = "WSO2 Soap API 파라미터 (Fail Count)", required = true) //
			final @RequestParam(value="count") int count,			
			@PathVariable final String username) throws ApiBusinessException, Exception {
		log.info("WSO2 UserProfileMgtService Soap API Parameter ==== {}", username);

		return wso2RusmSoapApiService.postFailedLoginAttemptsBeforeSuccessByUsername(username, count);
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Get OIDCDialect Nodes 테스트", //
			notes = "WSO2 Soap API Get OIDCDialect Nodes 테스트입니다.", //
			httpMethod = "GET", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@GetMapping("/OIDCDialectNodes")
	public ResponseEntity<String> getOIDCDialectNodes() throws ApiBusinessException, Exception {
		return wso2CmmsSoapApiService.getOIDCDialectNodes();
	}
	
	@ApiOperation( //
			value = "WSO2 Soap API Get Local Claim Nodes 테스트", //
			notes = "WSO2 Soap API Get Local Claim Nodes 테스트입니다.", //
			httpMethod = "GET", //
			produces = "application/json", //
			response = ResponseEntity.class)
	@GetMapping("/localClaimNodes")
	public ResponseEntity<String> getLocalClaimNodes() throws ApiBusinessException, Exception {
		return wso2CmmsSoapApiService.getLocalClaimNodes();
	}
}
