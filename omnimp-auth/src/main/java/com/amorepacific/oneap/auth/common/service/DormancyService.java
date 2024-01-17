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
 * Date   	          : 2020. 9. 28..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.common.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.dormancy.DormancyIData;
import com.amorepacific.oneap.common.vo.dormancy.DormancyOData;
import com.amorepacific.oneap.common.vo.dormancy.DormancyRequest;
import com.amorepacific.oneap.common.vo.dormancy.DormancyRequestHeader;
import com.amorepacific.oneap.common.vo.dormancy.DormancyRequestInput;
import com.amorepacific.oneap.common.vo.dormancy.DormancyResponse;
import com.amorepacific.oneap.common.vo.dormancy.DormancyResponseHeader;
import com.amorepacific.oneap.common.vo.dormancy.DormancyVo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.common.service 
 *    |_ DormancyService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 28.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class DormancyService {

	@Value("${dormancy.url}")
	private String dormancyUrl;

	@Value("${dormancy.source}")
	private String dormancySource;

	@Value("${dormancy.username}")
	private String dormancyUsername;

	@Value("${dormancy.userpassword}")
	private String dormancyUserpassword;

	@Autowired
	private ApiService apiService;

	public DormancyResponse releaseDormancy(final DormancyVo dormancyVo) {
		
		try {
			final DormancyRequestHeader header = new DormancyRequestHeader(this.dormancySource);
			final DormancyRequestInput input = new DormancyRequestInput(dormancyVo.getIncsNo(), dormancyVo.getChCd());
			final DormancyIData idata = new DormancyIData(header, input);
			final DormancyRequest request = new DormancyRequest(idata);

			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			final String jsonBody = gson.toJson(request);

			MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(mediaType);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			final String auth = SecurityUtil.getBasicAuthorizationBase64(this.dormancyUsername, this.dormancyUserpassword);
			headers.add("Authorization", auth);
			ResponseEntity<String> resp = this.apiService.post(this.dormancyUrl, headers, jsonBody, String.class);
			log.debug("▶▶▶▶▶▶ [release dormancy] response : {}", StringUtil.printJson(resp));
			if (resp == null || (resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.DORMANCY_EAI, OmniStdLogConstants.DORMANCY_EAI_COMMON, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.RELEASE_DORMANCY_EAI_SERVER_ERROR);
				LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("eai.releaseDormancy.Exception = {}", resp.getBody());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				
				DormancyResponse response = new DormancyResponse();
				DormancyOData odata = new DormancyOData();
				DormancyResponseHeader respHeader = new DormancyResponseHeader();
				respHeader.setRTN_CODE("F");
				respHeader.setRTN_MSG("dormancy send fail");
				odata.setHEADER(respHeader);
				response.setRESPONSE(odata);
				return response;
			}
			return gson.fromJson(resp.getBody(), DormancyResponse.class);
		} catch (Exception e) {
			// AP B2C 표준 로그 설정
			LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.DORMANCY_EAI, OmniStdLogConstants.DORMANCY_EAI_COMMON, null, null, null,
					LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.RELEASE_DORMANCY_EAI_SERVER_ERROR);
			LogInfo.setLogInfoAttr("WEB", (WebUtil.isMobile()) ? "MobileWeb" : "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("eai.releaseDormancy.Exception = {}", e.getMessage());
			LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
			
			return null;
		}
	}
}
