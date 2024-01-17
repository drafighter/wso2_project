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
 * Date   	          : 2023. 3. 24..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v4.membership.validator;

import org.springframework.util.StringUtils;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.api.v3.membership.validator.MemberShipOpenApiValidator;
import com.amorepacific.oneap.api.v4.membership.vo.NaverRequest;
import com.amorepacific.oneap.api.v4.membership.vo.NaverResponse;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v4.membership.validator 
 *    |_ NaverMemberShipApiValidator.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 3. 24.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@UtilityClass
public class NaverMemberShipApiValidator {
	
	private final String naverChCd = "401";
	
	public boolean checkApUser(final NaverRequest naverRequest) {
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		
		boolean empty = StringUtils.isEmpty(naverRequest.getInterlockMemberIdNo());
		empty |= StringUtils.isEmpty(naverRequest.getInterlockSellerNo());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.naver.checkApUser.Exception = requied param, empty parameter? {}", empty);			
			return false;
		}
		
		if (!naverChCd.equals(naverRequest.getInterlockSellerNo())) {
			log.error("api.naver.checkApUser.Exception = check invalid channel code ? {}", naverRequest.getInterlockSellerNo());			
			return false;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return true;
	}
	
	public boolean unLinkMembership(final NaverRequest naverRequest) {
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		boolean empty = StringUtils.isEmpty(naverRequest.getInterlockMemberIdNo());
		empty |= StringUtils.isEmpty(naverRequest.getInterlockSellerNo());
		empty |= StringUtils.isEmpty(naverRequest.getAffiliateMemberIdNo());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.naver.unLinkMembership.Exception = requied param, empty parameter? {}", empty);			
			return false;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return true;
	}
	
	public boolean unLinkNaverMembership(final String chCd, final String incsNo) {
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		boolean empty = StringUtils.isEmpty(chCd);
		empty |= StringUtils.isEmpty(incsNo);
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.naver.unLinkNaverMembership.Exception = requied param, empty parameter? {}", empty);			
			return false;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return true;		
	}
}
