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
 * Author	          : jspark2
 * Date   	          : 2021. 1. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.sns.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.commons.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amorepacific.oneap.api.v1.mgmt.vo.SnsUnlinkVo;
import com.amorepacific.oneap.api.v1.sns.mapper.SnsApiMapper;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.sns.SnsFacebookCallback;
import com.amorepacific.oneap.common.vo.sns.SnsFacebookInfo;
import com.amorepacific.oneap.common.vo.sns.SnsFacebookRequest;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.sns.service 
 *    |_ SnsCallbackApiService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 1. 21.
 * @version : 1.0
 * @author  : jspark2
 */

@Slf4j
@Service
public class SnsCallbackApiService {
	
	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	@Autowired
	private SnsApiMapper snsApiMapper;
	
	public String getSystemProfile() {
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		log.debug("▶▶▶▶▶▶  [SnsAuth] system profile : {}", profile);
		
		return profile;
	}
	
	public String getKey(String snsType, String keyName) {		
		
		final String key = this.config.getSnsInfo(getSystemProfile(), snsType.toLowerCase(), keyName);
		log.debug("▶▶▶▶▶▶  [SnsAuth getKey] keyName : {}, key : {}", keyName, key);
		
		return key;
	}

	
	public SnsFacebookInfo verificationFBCallbackRequest(String signedRequest, String snsType) {
		
		
		String url = this.config.getFacebookRemoveCallbackUrl(getSystemProfile(), "rmcallback");
		SnsFacebookCallback snsFacebookCallback = new SnsFacebookCallback();
		Gson gson = new Gson();
		SnsFacebookInfo snsFacebookInfo = new SnsFacebookInfo();
		
		snsFacebookCallback.setUrl(url);
		snsFacebookInfo.setCallback(snsFacebookCallback);
		
		if(signedRequest == null) {
			log.debug("▶▶▶▶▶ [verificationFBCallbackRequest] signedRequest is NULL");
			snsFacebookInfo.setIsValied(false);
			return snsFacebookInfo;
		}
		
		int requestIndex = signedRequest.indexOf(".");
		String encodedSig = signedRequest.substring(0, requestIndex) + "=";
		String payload = signedRequest.substring(requestIndex+1);
		String secretKey = this.getKey(snsType, "secretkey");

		
		if(CommonUtils.isEmpty(encodedSig) || CommonUtils.isEmpty(payload)) {
			log.debug("▶▶▶▶▶ [verificationFBCallbackRequest] signedRequest Info is NULL");
			snsFacebookInfo.setIsValied(false);
		}
		else {
			snsFacebookInfo.setData(gson.fromJson(new String(SecurityUtil.base64(payload)), SnsFacebookRequest.class));
			log.debug("▶▶▶▶▶ [verificationFBCallbackRequest] SNS Facebook Info : {}", snsFacebookInfo.getData());
			
			// HMAC-SHA256 검증
			if("HMAC-SHA256".equals(snsFacebookInfo.getData().getAlgorithm())) {
				// hash 검증
				String sig = SecurityUtil.base64(encodedSig.replace("_", "/").replace("-", "+").getBytes());
				String expectedSig = SecurityUtil.base64(SecurityUtil.HmacSHA256(secretKey, payload).getBytes());
				
				log.info("▶▶▶▶▶▶ [verificationFBCallbackRequest] sig : {}, expectedSig : {}, sig==expectedSig >>>> {}", sig, expectedSig, sig.equals(expectedSig));
				
				if(sig.equals(expectedSig)) {
					snsFacebookInfo.setIsValied(true);
				}
				else {
					snsFacebookInfo.setIsValied(false);
				}
			}
			else {
				snsFacebookInfo.setIsValied(false);
			}
		}
		
		return snsFacebookInfo;
	}
	
	public SnsUnlinkVo getIDNAssociatedIdIncsNo(Map<String, String> param) {
		return this.snsApiMapper.getIDNAssociatedIdIncsNo(param);
	}
}
