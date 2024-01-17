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
 * Date   	          : 2023. 2. 9..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v3.membership.validator;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.api.v3.membership.vo.ChkApUserResponse;
import com.amorepacific.oneap.api.v3.membership.vo.ChkApUserVo;
import com.amorepacific.oneap.api.v3.membership.vo.LinkMembershipResponse;
import com.amorepacific.oneap.api.v3.membership.vo.LinkMembershipVo;
import com.amorepacific.oneap.api.v3.membership.vo.UnLinkMembershipVo;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v3.membership.validator 
 *    |_ MemberShipOpenApiValidator.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 2. 9.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@UtilityClass
public class MemberShipOpenApiValidator {
	
	private ApiBaseResponse setTrxUuid(final ApiBaseResponse response) {
		
		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));	
		} else {
			response.setTrxUuid(trxuuid);
		}
		
		return response;
	}
	
	public ChkApUserResponse checkApUser(final ChkApUserResponse chkApUserResponse, final ChkApUserVo chkApUserVo) {
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(chkApUserResponse);
		
		boolean empty = StringUtils.isEmpty(chkApUserVo.getChCd());
		empty |= StringUtils.isEmpty(chkApUserVo.getMemberId());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.checkApUser.Exception = requied param, empty parameter? {}", empty);			
			chkApUserResponse.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return chkApUserResponse;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return chkApUserResponse;
	}
	
	public LinkMembershipResponse linkMembership(final LinkMembershipResponse linkMembershipResponse, final LinkMembershipVo linkMembershipVo) {
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(linkMembershipResponse);
		
		boolean empty = StringUtils.isEmpty(linkMembershipVo.getChCd());
		empty |= StringUtils.isEmpty(linkMembershipVo.getFullName());
		empty |= StringUtils.isEmpty(linkMembershipVo.getBirthDay());
		empty |= StringUtils.isEmpty(linkMembershipVo.getPhone());
		empty |= StringUtils.isEmpty(linkMembershipVo.getCiNo());
		empty |= StringUtils.isEmpty(linkMembershipVo.getMemberId());
		empty |= StringUtils.isEmpty(linkMembershipVo.getThrdPrtyTerm());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.linkMembership.Exception = requied param, empty parameter? {}", empty);			
			linkMembershipResponse.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return linkMembershipResponse;
		}
		
		boolean invalid = !(linkMembershipVo.getThrdPrtyTerm().equalsIgnoreCase("Y") || linkMembershipVo.getThrdPrtyTerm().equalsIgnoreCase("N"));
		if(invalid) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.linkMembership.Exception = check invalid thrdPrtyTerm ? {}", linkMembershipVo.getThrdPrtyTerm());
			linkMembershipResponse.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return linkMembershipResponse;
		}
		
		invalid = !DateUtil.isValidDateFormat(linkMembershipVo.getBirthDay(), "yyyyMMdd");
		if(invalid) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.linkMembership.Exception = check invalid birthday ? {}", linkMembershipVo.getBirthDay());
			linkMembershipResponse.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return linkMembershipResponse;
		}
		
		invalid = !StringUtil.isValidPhoneNumber(linkMembershipVo.getPhone());
		if(invalid) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.linkMembership.Exception = check invalid phone number ? {}", linkMembershipVo.getPhone());
			linkMembershipResponse.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return linkMembershipResponse;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return linkMembershipResponse;
	}
	
	public ApiBaseResponse unLinkMembership(final ApiBaseResponse apiBaseResponse, final UnLinkMembershipVo unLinkMembershipVo) {
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(apiBaseResponse);
		
		boolean empty = StringUtils.isEmpty(unLinkMembershipVo.getChCd());
		empty |= StringUtils.isEmpty(unLinkMembershipVo.getXincsNo());
		empty |= StringUtils.isEmpty(unLinkMembershipVo.getMemberId());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.unLinkMembership.Exception = requied param, empty parameter? {}", empty);			
			apiBaseResponse.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return apiBaseResponse;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return apiBaseResponse;
	}
}
