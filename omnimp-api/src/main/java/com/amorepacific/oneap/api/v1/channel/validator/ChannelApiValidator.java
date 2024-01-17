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
package com.amorepacific.oneap.api.v1.channel.validator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.BpChangePasswordRequest;
import com.amorepacific.oneap.common.vo.api.BpChangePasswordResponse;
import com.amorepacific.oneap.common.vo.api.BpUserData;
import com.amorepacific.oneap.common.vo.api.CreateChCustRequest;
import com.amorepacific.oneap.common.vo.api.SearchChCustRequest;
import com.amorepacific.oneap.common.vo.api.SearchChCustResponse;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.channel.validator 
 *    |_ ChannelApiValidator.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 22.
 * @version : 1.0
 * @author  : takkies
 */
@Slf4j
@UtilityClass
public class ChannelApiValidator {

	private final ConfigUtil config = ConfigUtil.getInstance();
	
	private final String incsNoRegexp = "^[0-9]{9}$";
	
	public ApiBaseResponse registChannelOnline(ApiBaseResponse response, final String chCd, final CreateChCustRequest createChCustRequest, final String profile) {
		
		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));	
		} else {
			response.setTrxUuid(trxuuid);
		}
		
		final String apiUrl = config.getChannelApi(chCd, "url", profile);
		
		boolean empty = StringUtils.isEmpty(chCd);
		empty |= createChCustRequest.getUser() == null;
		empty |= StringUtils.isEmpty(apiUrl);
		
		if (empty) {
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			if (StringUtils.isEmpty(apiUrl)) {
				response.setMessage(String.format("해당 채널(%s)의 API 호출 URL 이 존재하지 않습니다.", chCd));
			}
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, createChCustRequest.getIncsNo());
		
		if (incsno) {
			log.debug("check invalid incso no? {}", incsno);
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		return response;
	}
	
	public SearchChCustResponse searchChannelCustomer(SearchChCustResponse response, final String chCd, final SearchChCustRequest searchCustRequest, final String profile) {
		
		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));	
		} else {
			response.setTrxUuid(trxuuid);
		}
		
		final String apiUrl = config.getChannelApi(chCd, "url", profile);
		
		boolean empty = StringUtils.isEmpty(chCd);
		// empty |= StringUtils.isEmpty(apiUrl); 채널에 따라 검색 제공하지 않음.
		empty |= (StringUtils.isEmpty(searchCustRequest.getName()) && StringUtils.isEmpty(searchCustRequest.getPhone()) && StringUtils.isEmpty(searchCustRequest.getCi()) && StringUtils.isEmpty(searchCustRequest.getWebId()));
		
		if (empty) {
			response.setResultCode(ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode());
			if (StringUtils.isEmpty(apiUrl)) {
				response.setMessage(String.format("해당 채널(%s)의 API 호출 URL 이 존재하지 않습니다.", chCd));
			}
			return response;
		}
		
		
		
		return response;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 22. 오전 11:59:22
	 * </pre>
	 * @param response
	 * @param userData
	 * @return
	 */
	public BpChangePasswordResponse beautyPointCheckOnlineId(BpChangePasswordResponse response, final BpUserData userData) {
		
		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));	
		} else {
			response.setTrxUuid(trxuuid);
		}
		
		boolean empty = StringUtils.isEmpty(userData.getIncsNo());
		empty |= StringUtils.isEmpty(userData.getCstmid());
		
		if (empty) {
			response.setResultCode(ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode());
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, userData.getIncsNo());
		
		if (incsno) {
			log.debug("check invalid incso no? {}", incsno);
			response.setResultCode(ResultCode.REQ_INVALID_PARAM.getCode());
			return response;
		}
		
		return response;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 22. 오전 11:59:27
	 * </pre>
	 * @param response
	 * @param bpChangePasswordRequest
	 * @return
	 */
	public BpChangePasswordResponse beautyPointChangePassword(BpChangePasswordResponse response, final BpChangePasswordRequest bpChangePasswordRequest) {
		
		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));	
		} else {
			response.setTrxUuid(trxuuid);
		}
		
		boolean empty = StringUtils.isEmpty(bpChangePasswordRequest.getCstmId());
		empty |= StringUtils.isEmpty(bpChangePasswordRequest.getPasswd());
		empty |= StringUtils.isEmpty(bpChangePasswordRequest.getPasswd_new());
		
		if (empty) {
			response.setResultCode(ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode());
			return response;
		}
		
		return response;
		
	}
	
}
