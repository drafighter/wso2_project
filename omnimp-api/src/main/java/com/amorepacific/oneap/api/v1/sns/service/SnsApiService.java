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
 * Date   	          : 2020. 9. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.sns.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.amorepacific.oneap.api.common.service.RestApiService;
import com.amorepacific.oneap.api.exception.ApiBusinessException;
import com.amorepacific.oneap.api.v1.sns.validator.SnsApiValidator;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.sns.SnsConnectRequest;
import com.amorepacific.oneap.common.vo.sns.SnsProfileResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenVo;
import com.amorepacific.oneap.common.vo.sns.SnsUnlinkResponse;
import com.amorepacific.oneap.common.vo.sns.SnsUrl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.sns.service 
 *    |_ SnsApiService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 21.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class SnsApiService {

	@Autowired
	private RestApiService restApiService;

	@Autowired
	private SystemInfo systemInfo;

	private ConfigUtil config = ConfigUtil.getInstance();

	public SnsTokenResponse getNewToken(SnsTokenResponse response, final String snsType, final SnsTokenVo snsTokenVo) throws ApiBusinessException {

		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		log.debug("▶▶▶▶▶▶ system profile : {}", profile);

		response = SnsApiValidator.getNewToken(response, snsType, snsTokenVo, profile);
		
		if (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode())) {
			final String restApiKey = this.config.getSnsInfo(profile, snsType.toLowerCase(), "restkey");
			final String secretKey = this.config.getSnsInfo(profile, snsType.toLowerCase(), "secretkey");
			final String callbackUrl = this.config.getSnsInfo(profile, snsType.toLowerCase(), "callback");

			String tokenUrl = "";
			ResponseEntity<SnsTokenResponse> snsResponse = null;
			final HttpHeaders headers = new HttpHeaders();
			MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<String, String>();
			if ("KA".equals(snsType)) {
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				bodyMap.add("grant_type", "authorization_code");
				bodyMap.add("client_id", restApiKey);
				bodyMap.add("redirect_uri", callbackUrl);
				bodyMap.add("code", snsTokenVo.getCode());
				bodyMap.add("client_secret", secretKey);
				tokenUrl = SnsUrl.KA_NEW_TOKEN;
				snsResponse = this.restApiService.post(tokenUrl, headers, bodyMap, SnsTokenResponse.class);
			} else if ("NA".equals(snsType)) {
				tokenUrl = SnsUrl.NA_NEW_TOKEN;
				tokenUrl = tokenUrl.replace("{{app_key}}", restApiKey) //
						.replace("{{secret_key}}", secretKey) //
						.replace("{{redirect_uri}}", callbackUrl) //
						.replace("{{code}}", snsTokenVo.getCode()) //
						.replace("{{state}}", snsTokenVo.getState());

				snsResponse = this.restApiService.get(tokenUrl, new HttpHeaders(), SnsTokenResponse.class);
			} else if ("FB".equals(snsType)) {
				tokenUrl = SnsUrl.FB_NEW_TOKEN;
				tokenUrl = tokenUrl.replace("{{app_key}}", restApiKey) //
						.replace("{{secret_key}}", secretKey) //
						.replace("{{redirect_uri}}", callbackUrl) //
						.replace("{{code}}", snsTokenVo.getCode());
				snsResponse = this.restApiService.get(tokenUrl, new HttpHeaders(), SnsTokenResponse.class);
			}

			log.debug("▶▶▶▶▶▶ sns new token response : {}", StringUtil.printJson(snsResponse.getBody()));
			final String trxuuid = response.getTrxUuid();
			response = snsResponse.getBody();
			response.setTrxUuid(trxuuid);
		}
		
		return response;
	}

	public SnsUnlinkResponse unlink(SnsUnlinkResponse response, final String snsType, final SnsTokenVo snsTokenVo) throws ApiBusinessException {
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		log.debug("▶▶▶▶▶▶ system profile : {}", profile);

		response = SnsApiValidator.unlink(response, snsType, snsTokenVo, profile);
		
		if (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode())) {
			final String restApiKey = this.config.getSnsInfo(profile, snsType.toLowerCase(), "restkey");
			final String secretKey = this.config.getSnsInfo(profile, snsType.toLowerCase(), "secretkey");

			String tokenUrl;
			ResponseEntity<SnsUnlinkResponse> snsResponse = null;
			final HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + snsTokenVo.getAccessToken());
			if ("KA".equals(snsType)) {
				tokenUrl = SnsUrl.KA_UNLINK;
				snsResponse = this.restApiService.post(tokenUrl, headers, null, SnsUnlinkResponse.class);
			} else if ("NA".equals(snsType)) {
				tokenUrl = SnsUrl.NA_UNLINK;
				tokenUrl = tokenUrl.replace("{{app_key}}", restApiKey) //
						.replace("{{secret_key}}", secretKey) //
						.replace("{{access_token}}", snsTokenVo.getAccessToken()); //

				snsResponse = this.restApiService.get(tokenUrl, headers, SnsUnlinkResponse.class);
			} else if ("FB".equals(snsType)) {

				if (StringUtils.isEmpty(snsTokenVo.getUserId())) {
					throw new ApiBusinessException("not empty facebook user id!!!");
				}

				tokenUrl = SnsUrl.FB_UNLINK;
				tokenUrl = tokenUrl.replace("{{user_id}}", snsTokenVo.getUserId());
				snsResponse = this.restApiService.delete(tokenUrl, headers, null, SnsUnlinkResponse.class);
			}

			log.debug("▶▶▶▶▶▶ sns unlink response : {}", StringUtil.printJson(snsResponse.getBody()));
			final String trxuuid = response.getTrxUuid();
			response = snsResponse.getBody();
			response.setTrxUuid(trxuuid);
		}
		
		return response;
		
	}
	
	public SnsProfileResponse getProfile(SnsProfileResponse response, final String snsType, final SnsTokenVo snsTokenVo) throws ApiBusinessException {

		response = SnsApiValidator.getProfile(response, snsType, snsTokenVo);
		
		if (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode())) {
			String tokenUrl = "";
			ResponseEntity<SnsProfileResponse> snsResponse = null;
			final HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + snsTokenVo.getAccessToken());
			if ("KA".equals(snsType)) {
				tokenUrl = SnsUrl.KA_PROFILE;
				snsResponse = this.restApiService.post(tokenUrl, headers, null, SnsProfileResponse.class);
			} else if ("NA".equals(snsType)) {
				tokenUrl = SnsUrl.NA_PROFILE;
				snsResponse = this.restApiService.get(tokenUrl, headers, SnsProfileResponse.class);
			} else if ("FB".equals(snsType)) {
				tokenUrl = SnsUrl.FB_PROFILE;
				snsResponse = this.restApiService.get(tokenUrl, headers, SnsProfileResponse.class);
			}

			log.debug("▶▶▶▶▶▶ sns profile response : {}", StringUtil.printJson(snsResponse.getBody()));
			final String trxuuid = response.getTrxUuid();
			response = snsResponse.getBody();
			response.setTrxUuid(trxuuid);
		}
		
		return response;
	}
	
	public ApiBaseResponse joinOnSnsLinker(ApiBaseResponse response, final SnsConnectRequest snsConnectRequest) throws ApiBusinessException {
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		log.debug("▶▶▶▶▶▶ system profile : {}", profile);
		
		response.SetResponseInfo(ResultCode.SUCCESS);
		return response;

		/*
		 * response = SnsApiValidator.joinOnSnsLinker(response, snsConnectRequest, profile);
		 * 
		 * if (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode())) { final String apiUrl =
		 * this.config.getSnsInfo(profile, snsConnectRequest.getSnsType().toLowerCase(), "joinon");
		 * 
		 * final HttpHeaders headers = new HttpHeaders(); final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		 * headers.setContentType(mediaType); headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		 * 
		 * Gson gson = new GsonBuilder().disableHtmlEscaping().create(); String json = gson.toJson(snsConnectRequest);
		 * 
		 * // 00 = 정상처리 // ,99=ERROR // ,01=존재하지않는 통합고객번호입니다 // ,02=존재하지않는 WEB ID입니다 // ,03=WEBID와 통합고객번호가 매칭되지않습니다 // ,04=필수값이 누락되었습니다 // ,05=잘못된
		 * SNS타입 입니다
		 * 
		 * ResponseEntity<ApiBaseResponse> snsResponse = this.restApiService.post(apiUrl, headers, json, ApiBaseResponse.class);
		 * 
		 * log.debug("▶▶▶▶▶▶ joinon sns link response : {}", StringUtil.printJson(snsResponse.getBody())); response = snsResponse.getBody(); }
		 * 
		 * return response;
		 */
	}
}
