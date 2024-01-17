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
 * Date   	          : 2020. 9. 22..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.sns.validator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.sns.SnsConnectRequest;
import com.amorepacific.oneap.common.vo.sns.SnsProfileResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenVo;
import com.amorepacific.oneap.common.vo.sns.SnsUnlinkResponse;

import lombok.experimental.UtilityClass;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.sns.validator 
 *    |_ SnsApiValidator.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 22.
 * @version : 1.0
 * @author : takkies
 */
@UtilityClass
public class SnsApiValidator {

	private final ConfigUtil config = ConfigUtil.getInstance();

	public SnsTokenResponse getNewToken(SnsTokenResponse response, final String snsType, final SnsTokenVo snsTokenVo, final String profile) {

		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		} else {
			response.setTrxUuid(trxuuid);
		}

		final String restApiKey = config.getSnsInfo(profile, snsType.toLowerCase(), "restkey");
		final String secretKey = config.getSnsInfo(profile, snsType.toLowerCase(), "secretkey");
		final String callbackUrl = config.getSnsInfo(profile, snsType.toLowerCase(), "callback");

		boolean empty = StringUtils.isEmpty(snsType);
		empty |= StringUtils.isEmpty(restApiKey);
		empty |= StringUtils.isEmpty(secretKey);
		empty |= StringUtils.isEmpty(callbackUrl);
		empty |= StringUtils.isEmpty(snsTokenVo.getCode());

		if (empty) {
			response.setResultCode(ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode());
			return response;
		}

		return response;
	}

	public SnsUnlinkResponse unlink(SnsUnlinkResponse response, final String snsType, final SnsTokenVo snsTokenVo, final String profile) {

		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		} else {
			response.setTrxUuid(trxuuid);
		}

		final String restApiKey = config.getSnsInfo(profile, snsType.toLowerCase(), "restkey");

		boolean empty = StringUtils.isEmpty(snsType);
		empty |= StringUtils.isEmpty(snsTokenVo.getAccessToken());
		empty |= StringUtils.isEmpty(restApiKey);

		if (empty) {
			response.setResultCode(ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode());
			return response;
		}

		return response;
	}

	public SnsProfileResponse getProfile(SnsProfileResponse response, final String snsType, final SnsTokenVo snsTokenVo) {

		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		} else {
			response.setTrxUuid(trxuuid);
		}

		boolean empty = StringUtils.isEmpty(snsType);
		empty |= StringUtils.isEmpty(snsTokenVo.getAccessToken());

		if (empty) {
			response.setResultCode(ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode());
			return response;
		}

		return response;
	}
	
	public ApiBaseResponse joinOnSnsLinker(ApiBaseResponse response, final SnsConnectRequest snsConnectRequest, final String profile) {
		
		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		} else {
			response.setTrxUuid(trxuuid);
		}
		
		final String apiUrl = config.getSnsInfo(profile, snsConnectRequest.getSnsType().toLowerCase(), "joinon");
		
		boolean empty = StringUtils.isEmpty(snsConnectRequest.getConnectYN());
		empty |= StringUtils.isEmpty(snsConnectRequest.getUcstmid());
		empty |= StringUtils.isEmpty(snsConnectRequest.getCstmid());
		empty |= StringUtils.isEmpty(snsConnectRequest.getSnsAuthkey());
		empty |= StringUtils.isEmpty(snsConnectRequest.getSnsType());
		empty |= StringUtils.isEmpty(apiUrl);
		
		if (empty) {
			response.setResultCode(ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode());
			return response;
		}
		
		return response;
	}
}
