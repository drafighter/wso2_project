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
package com.amorepacific.oneap.api.v1.wso2.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.amorepacific.oneap.api.v1.wso2.mapper.Wso2ApiMapper;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2RestApiCreateUserVo;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2RestApiGetUserVo;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2RestApiPatchUserVo;
import com.amorepacific.oneap.common.code.ResultCode;

import lombok.extern.slf4j.Slf4j;

//import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.wso2.service 
 *    |_ Wso2Scim2RestApiService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 3.
 * @version : 1.0
 * @author : hjw0228
 */

@Slf4j
@Service
public class Wso2Scim2RestApiService extends Wso2ApiCommonService {

	@Autowired
	private RestTemplate restTemplate;
	
	@SuppressWarnings("deprecation")
	@Autowired
	private AsyncRestTemplate asyncRestTemplate;

	//@Autowired
	//private RestTemplateMonitor restTemplateMonitor;

	@Autowired
	private Wso2ApiMapper wso2ApiMapper;

	@Value("${wso2.identityserverendpointcontexturl}")
	private String identityserverendpointcontexturl;

	@Value("${wso2.usersessionendpointurl}")
	private String usersessionendpointurl;
	
	private static final String WSO2_SCIM2_USERS = "/scim2/Users";
	private static final String SESSIONS = "sessions";

	public ResponseEntity<String> getUser(Wso2RestApiGetUserVo wso2RestApiGetUserVo) throws Exception {

		// 화면에 모니터링 로그 남기기
		//log.info(restTemplateMonitor.createHttpInfo());

		// Generate WSO2 Get User Query Filter
		wso2RestApiGetUserVo = generateGetUserFilter(wso2RestApiGetUserVo);

		// WSO2 ADMIN Request Entity 생성
		HttpEntity<?> requestEntity = createHttpEntity("application/scim+json", null);

		UriComponents builder = UriComponentsBuilder.fromHttpUrl(identityserverendpointcontexturl.concat(WSO2_SCIM2_USERS)).queryParam("attributes", wso2RestApiGetUserVo.getAttributes())
				.queryParam("excludedAttributes", wso2RestApiGetUserVo.getExcudedAttributes()).queryParam("filter", wso2RestApiGetUserVo.getFilter())
				.queryParam("startIndex", Integer.toString(wso2RestApiGetUserVo.getStartIndex())).queryParam("count", Integer.toString(wso2RestApiGetUserVo.getCount()))
				.queryParam("sortBy", wso2RestApiGetUserVo.getSortBy()).queryParam("sortOder", wso2RestApiGetUserVo.getSortOder()).queryParam("domain", wso2RestApiGetUserVo.getDomain()).build(false);

		ResponseEntity<String> response = null;

		try {
			response = restTemplate.exchange(builder.toString(), HttpMethod.GET, requestEntity, String.class);

			return response;
		} catch (Exception e) {
			// 화면에 모니터링 로그 남기기
			//log.error(restTemplateMonitor.createHttpInfo());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> getUserByUmUserId(String umUserId) throws Exception {

		// 화면에 모니터링 로그 남기기
		//log.info(restTemplateMonitor.createHttpInfo());

		// WSO2 ADMIN Request Entity 생성
		HttpEntity<?> requestEntity = createHttpEntity("application/scim+json", null);

		UriComponents builder = UriComponentsBuilder.fromHttpUrl(identityserverendpointcontexturl.concat(WSO2_SCIM2_USERS) + "/" + umUserId).build(false);

		ResponseEntity<String> response = null;

		try {
			response = restTemplate.exchange(builder.toString(), HttpMethod.GET, requestEntity, String.class);

			return response;
		} catch (Exception e) {
			// 화면에 모니터링 로그 남기기
			//log.error(restTemplateMonitor.createHttpInfo());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> getUserByUsername(String username) throws Exception {

		// Check User WSO2 ID(um_user_id) By username
		String umUserId = wso2ApiMapper.getUmUserIdByUsername(username);

		return this.getUserByUmUserId(umUserId);
	}

	public ResponseEntity<String> getUserByIncsNo(String incsNo) throws Exception {

		// Check User WSO2 ID(um_user_id) By incsNo
		String umUserId = wso2ApiMapper.getUmIdByIncsNo(incsNo);

		return this.getUserByUmUserId(umUserId);
	}

	/*
	 * public ResponseEntity<String> getUserByUserCi(String userCi) throws Exception {
	 * 
	 * // Check User WSO2 ID(um_user_id) By incsNo String umUserId = wso2ApiMapper.getUmUserIdByUserCi(userCi);
	 * 
	 * return this.getUserByUmUserId(umUserId); }
	 */

	public ResponseEntity<String> postUser(Wso2RestApiCreateUserVo wso2RestApiCreateUserVo) throws Exception {

		// 화면에 모니터링 로그 남기기
		//log.info(restTemplateMonitor.createHttpInfo());

		// Generate WSO2 Create User Parameters
		JSONObject result = new JSONObject();

		result.put("userName", wso2RestApiCreateUserVo.getUserName());
		result.put("password", wso2RestApiCreateUserVo.getUserPassword());
		// result.put("mobile", wso2RestApiCreateUserVo.getMobile());

		JSONObject EnterpriseUser = new JSONObject();
		EnterpriseUser.put("fullName", wso2RestApiCreateUserVo.getFullName());
		EnterpriseUser.put("incsNo", wso2RestApiCreateUserVo.getIncsNo());
		// EnterpriseUser.put("userCi", wso2RestApiCreateUserVo.getUserCi());

		result.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User", EnterpriseUser);

		/*
		 * JSONArray phoneNumbers = new JSONArray(); JSONObject mobile = new JSONObject(); mobile.put("type", "mobile"); mobile.put("value",
		 * wso2RestApiCreateUserVo.getMobile()); phoneNumbers.put(mobile);
		 * 
		 * result.put("phoneNumbers", phoneNumbers);
		 */

		// WSO2 ADMIN Request Entity 생성
		HttpEntity<?> requestEntity = createHttpEntity("application/scim+json", result.toString());

		UriComponents builder = UriComponentsBuilder.fromHttpUrl(identityserverendpointcontexturl.concat(WSO2_SCIM2_USERS)).build(false);

		ResponseEntity<String> response = null;

		try {
			response = restTemplate.exchange(builder.toString(), HttpMethod.POST, requestEntity, String.class);
			
			// um_user 테이블 um_user_incsno 업데이트
			wso2ApiMapper.updateUmUserIncsno(wso2RestApiCreateUserVo);

			return response;
		} catch (Exception e) {
			// 화면에 모니터링 로그 남기기
			//log.error(restTemplateMonitor.createHttpInfo());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> patchUserByUmUserId(String umUserId, Wso2RestApiPatchUserVo wso2RestApiPatchUserVo) throws Exception {

		// 화면에 모니터링 로그 남기기
		//log.info(restTemplateMonitor.createHttpInfo());

		// Generate WSO2 Patch User Parameters
		JSONObject result = new JSONObject();

		JSONArray schemas = new JSONArray();
		schemas.put("urn:ietf:params:scim:api:messages:2.0:PatchOp");

		JSONArray Operations = new JSONArray();

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("op", "replace");

		JSONObject value = new JSONObject();

		JSONObject EnterpriseUser = new JSONObject();
		if (!StringUtils.isEmpty(wso2RestApiPatchUserVo.getFullName()))
			EnterpriseUser.put("fullName", wso2RestApiPatchUserVo.getFullName());
		if (!StringUtils.isEmpty(wso2RestApiPatchUserVo.getIncsNo()))
			EnterpriseUser.put("incsNo", wso2RestApiPatchUserVo.getIncsNo());
		/* if(!StringUtils.isEmpty(wso2RestApiPatchUserVo.getUserCi())) EnterpriseUser.put("userCi", wso2RestApiPatchUserVo.getUserCi()); */

		value.put("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User", EnterpriseUser);

		/*
		 * JSONArray phoneNumbers = new JSONArray(); JSONObject mobile = new JSONObject(); mobile.put("type", "mobile"); mobile.put("value",
		 * wso2RestApiPatchUserVo.getMobile()); phoneNumbers.put(mobile);
		 * 
		 * value.put("phoneNumbers", phoneNumbers);
		 */

		jsonObj.put("value", value);

		Operations.put(jsonObj);

		result.put("schemas", schemas);
		result.put("Operations", Operations);

		// WSO2 ADMIN Request Entity 생성
		HttpEntity<?> requestEntity = createHttpEntity("application/scim+json", result.toString());

		UriComponents builder = UriComponentsBuilder.fromHttpUrl(identityserverendpointcontexturl.concat(WSO2_SCIM2_USERS) + "/" + umUserId).build(false);

		ResponseEntity<String> response = null;

		try {
			response = restTemplate.exchange(builder.toString(), HttpMethod.PATCH, requestEntity, String.class);

			return response;
		} catch (Exception e) {
			// 화면에 모니터링 로그 남기기
			//log.error(restTemplateMonitor.createHttpInfo());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> patchUserByUsername(String username, Wso2RestApiPatchUserVo wso2RestApiPatchUserVo) throws Exception {

		// Check User WSO2 ID(um_user_id) By username
		String umUserId = wso2ApiMapper.getUmUserIdByUsername(username);

		return this.patchUserByUmUserId(umUserId, wso2RestApiPatchUserVo);
	}

	public ResponseEntity<String> deleteUserByUmUserId(String umUserId) throws Exception {

		// 화면에 모니터링 로그 남기기
		//log.info(restTemplateMonitor.createHttpInfo());

		// WSO2 ADMIN Request Entity 생성
		HttpEntity<?> requestEntity = createHttpEntity("application/scim+json", null);

		UriComponents builder = UriComponentsBuilder.fromHttpUrl(identityserverendpointcontexturl.concat(WSO2_SCIM2_USERS) + "/" + umUserId).build(false);

		ResponseEntity<String> response = null;

		try {
			response = restTemplate.exchange(builder.toString(), HttpMethod.DELETE, requestEntity, String.class);

			return response;
		} catch (Exception e) {
			// 화면에 모니터링 로그 남기기
			//log.error(restTemplateMonitor.createHttpInfo());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> deleteUserByUsername(String username) throws Exception {

		// Check User WSO2 ID(um_user_id) By username
		String umUserId = wso2ApiMapper.getUmUserIdByUsername(username);

		return this.deleteUserByUmUserId(umUserId);
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public ResponseEntity<String> removeSessionByUserId(String userId) throws Exception {
			
		HttpEntity<?> requestEntity = createHttpEntity("application/scim+json", null);
		UriComponents builder = UriComponentsBuilder.fromHttpUrl(usersessionendpointurl.concat(userId) + "/" + SESSIONS).build(false);

		try {
			asyncRestTemplate.exchange(builder.toString(), HttpMethod.DELETE, requestEntity, Map.class);
			
			/*
			 * response.addCallback(result -> { log.debug("패스워드 변경 비동기 처리 - getStatusCode : {}\ngetBody : {}", result.getStatusCode(),
			 * result.getBody()); }, ex -> log.error("패스워드 비동기 처리 에러 - Message : {}", ex.getMessage()));
			 */
			
			// 비동기 처리로 변경되었음으로 성공으로 리턴
			return new ResponseEntity<>(null, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<String> removeSessionByUserNo(int userNo) throws Exception {
		
		String userId = null;
		try {
			userId = wso2ApiMapper.getUmUserIdByUserNo(userNo);
			if(userId.isEmpty()) {
				return new ResponseEntity<>(ResultCode.SYSTEM_ERROR.message(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		catch(Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return removeSessionByUserId(userId);
	}
	public ResponseEntity<String> removeSessionByUserName(String userName) throws Exception {
		
		String userId = null;
		try {
			userId = wso2ApiMapper.getUmUserIdByUsername(userName);
			if(userId.isEmpty()) {
				return new ResponseEntity<>(ResultCode.SYSTEM_ERROR.message(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		catch(Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return removeSessionByUserId(userId);
	}
	
	public ResponseEntity<String> removeSessionByiscsNo(String incsNo) throws Exception {
		
		String userId = null;
		try {
			userId = wso2ApiMapper.getUmUserIdByIncsNo(incsNo);
			if(userId.isEmpty()) {
				return new ResponseEntity<>(ResultCode.SYSTEM_ERROR.message(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		catch(Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return removeSessionByUserId(userId);
	}
	
	
	/*
	 HttpEntity<?> requestEntity = createHttpEntity("application/scim+json", null);
		UriComponents builder = UriComponentsBuilder.fromHttpUrl(usersessionendpointurl.concat(userId) + "/" + SESSIONS).queryParam("limit", wso2RestApiUserSessionVo.getLimit())
				.queryParam("offset", wso2RestApiUserSessionVo.getOffset()).queryParam("filter", wso2RestApiUserSessionVo.getFilter())
				.queryParam("sort", wso2RestApiUserSessionVo.getSort()).build(false);
	 * 
	 * */

	public ResponseEntity<String> getSessionByUserId(String userId) throws Exception {
			
		HttpEntity<?> requestEntity = createHttpEntity("application/scim+json", null);
		UriComponents builder = UriComponentsBuilder.fromHttpUrl(usersessionendpointurl.concat(userId) + "/" + SESSIONS).build(false);
		ResponseEntity<String> response = null;

		try {
			response = restTemplate.exchange(builder.toString(), HttpMethod.GET, requestEntity, String.class);
			return response;
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
