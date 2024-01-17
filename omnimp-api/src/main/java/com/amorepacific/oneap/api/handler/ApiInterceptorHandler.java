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
 * Date   	          : 2020. 7. 31..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.handler;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.api.common.service.ChannelService;
import com.amorepacific.oneap.api.common.service.CommonService;
import com.amorepacific.oneap.api.exception.ApiSecurityException;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.LocaleUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.handler 
 *    |_ ApiInterceptorHandler.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 31.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
public class ApiInterceptorHandler extends HandlerInterceptorAdapter {

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.OMNI_API_COMMON, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);

		String apikey = WebUtil.getHeader(OmniConstants.XAPIKEY);
		apikey = StringUtils.isEmpty(apikey) ? "" : apikey;
		log.debug("▶▶▶▶▶▶ default api key : {} ---> {}", apikey, request.getRequestURL().toString());
		if (apikey.equals(this.config.defaultApiKey())) { // 내부 API 호출인 경우
			return true;
		}

		
		if (StringUtils.isEmpty(apikey)) {
			
			String profile = this.systemInfo.getActiveProfiles()[0];
			profile = StringUtils.isEmpty(profile) ? "dev" : profile;
			profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
			
			String facebookRmUrl = config.getFacebookRemoveCallbackUrl(profile, "rmrequesturl");
			
			
			// 페이스북이 삭제 요청 시 x-api-key를 헤더에 담아 요청할 수 없고 식별할 수 있는 header, body, parameter가 없기 때문에 url 베이스로 api를 허가한다.  
			if(facebookRmUrl.equals(request.getRequestURL().toString())) {
				return true;
				
				// x-www.form-urlencoded의 signed_request 값의 유무에 따라 허가(signed_request를 읽는 이슈로 인해 주석)
//				try {
//					StringBuffer body = new StringBuffer();
//					String line = null;
//					
//					BufferedReader reader = request.getReader();
//			        while((line = reader.readLine()) != null) {
//			        	body.append(line);
//			        }
//			 
//			        
//			        Gson gson = new Gson();
//			        SnsFacebookSignedRequest signedRequest = gson.fromJson(new String(body), SnsFacebookSignedRequest.class);
//			        log.debug("▶▶▶▶▶▶  signedRequest : {}", signedRequest.getSignedRequest());
//			        
//			        // body에 signed_request 값이 있을 경우 허가
//			        if(signedRequest.getSignedRequest() != null) {
//			        	return true;
//			        }
//				}
//				catch(Exception e){
//			    	final String msgcode = "api.error.".concat(Integer.toString(HttpStatus.NOT_FOUND.value()));
//			    	throw new ApiSecurityException(this.messageSource.getMessage(msgcode, new String[] { "error occurred." }, LocaleUtil.getLocale()), ApiSecurityException.API_KEY_INVALID);
//			    }
			}
			
			if(request.getRequestURL().toString().contains("/v2/joinon")) {
				return true;
			}
			
			if(request.getRequestURL().toString().contains("/v1/errorcheck")) return true; // 에러체크 API 인 경우 예외 처리
			
			if(request.getRequestURL().toString().contains("/v3/api-docs")) return true; // Swagger 3.0 업데이트에 따른 예외 처리
			
			final String msgcode = "api.error.".concat(Integer.toString(HttpStatus.NOT_FOUND.value()));
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.OMNI_API_KEY_NOT_FOUND);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			throw new ApiSecurityException(this.messageSource.getMessage(msgcode, new String[] { "error occurred." }, LocaleUtil.getLocale()), ApiSecurityException.API_KEY_INVALID);
		} else {
			
			// 각 사이트별 API KEY를 DB, 또는 설정 에 넣어놓고 조회
			try {
				boolean validApiKey = false;
				
				for (Channel channel : this.channelService.getChannels()) {
					if (channel.getApiCallChnAthtVl().equals(apikey)) {
						validApiKey = true;
						break;
					}
				}
				
				final String decodedApiKey = SecurityUtil.getDecodedJasypt(apikey);
				if (decodedApiKey.equals("api-call")) { // 내부 API 호출인 경우
					log.debug("▶▶▶▶▶▶ internal api call key : {} ---> {}", apikey, request.getRequestURL().toString());
					return true;
				}
				
				if (!validApiKey) {
					final String msgcode = "api.error.".concat(Integer.toString(HttpStatus.BAD_REQUEST.value()));
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoErr(OmniStdLogConstants.OMNI_API_KEY_INVALID);
					LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					throw new ApiSecurityException(this.messageSource.getMessage(msgcode, new String[] { "error occurred." }, LocaleUtil.getLocale()), ApiSecurityException.API_KEY_INVALID);
				}
				
				log.debug("▶▶▶▶▶▶ decoded api key : {}", decodedApiKey);
				Channel channel = this.commonService.getChannel(decodedApiKey);
				if (channel != null) {
					log.debug("▶▶▶▶▶▶ {}({}) external api key : {} ---> {}", channel.getChCdNm(), decodedApiKey, apikey, request.getRequestURL().toString());
					WebUtil.setSession(OmniConstants.CH_CD_SESSION, channel.getChCd());
				}
			} catch (Exception e) {
				final String msgcode = "api.error.".concat(Integer.toString(HttpStatus.UNAUTHORIZED.value()));
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.OMNI_API_KEY_UNAUTHORIZED);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				throw new ApiSecurityException(this.messageSource.getMessage(msgcode, new String[] { "error occurred." }, LocaleUtil.getLocale()), ApiSecurityException.API_KEY_INVALID);
			}
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화

		return true;
	}

}
