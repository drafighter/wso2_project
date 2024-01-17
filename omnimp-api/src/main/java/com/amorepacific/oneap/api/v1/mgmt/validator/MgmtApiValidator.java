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
package com.amorepacific.oneap.api.v1.mgmt.validator;

import java.util.List;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChTermsResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChUserIncsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChgPwdJoinOnVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChkSnsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChkUserIdResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChnTermsCndVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.CryptoResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.CryptoVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.DupIdVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.IdSearchVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ModUserTermsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ReqTermsResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.SnsAssResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.SnsUnlinkResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.SnsUnlinkVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.SysncSnsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.VeriEntPwdVo;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.BpEditUserRequest;
import com.amorepacific.oneap.common.vo.api.ChangePasswordData;
import com.amorepacific.oneap.common.vo.api.ChangeWebIdData;
import com.amorepacific.oneap.common.vo.api.CheckSnsIdResponse;
import com.amorepacific.oneap.common.vo.api.CheckSnsIdVo;
import com.amorepacific.oneap.common.vo.api.CreateDupUserRequest;
import com.amorepacific.oneap.common.vo.api.CreateUserData;
import com.amorepacific.oneap.common.vo.api.InitPasswordData;
import com.amorepacific.oneap.common.vo.api.VeriPwdPlcyVo;
import com.amorepacific.oneap.common.vo.sns.SnsParam;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.validator 
 *    |_ ApiValidator.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 22.
 * @version : 1.0
 * @author  : takkies
 */
@Slf4j
@UtilityClass
public class MgmtApiValidator {

	private final String incsNoRegexp = "^[0-9]{9}$";
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : hkdang
	 * date     : 2020. 9. 22. 오후 12:25:29
	 * </pre>
	 * @param response
	 * @return
	 */
	private ApiBaseResponse setTrxUuid(final ApiBaseResponse response) {
		
		final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if (StringUtils.isEmpty(trxuuid)) {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));	
		} else {
			response.setTrxUuid(trxuuid);
		}
		
		return response;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 22. 오전 9:11:29
	 * </pre>
	 * @param response
	 * @param changePasswordData
	 * @return
	 */
	public ApiBaseResponse changePassword(final ApiBaseResponse response, final ChangePasswordData changePasswordData) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(changePasswordData.getChCd());
		empty |= StringUtils.isEmpty(changePasswordData.getLoginId());
		empty |= StringUtils.isEmpty(changePasswordData.getCurrentPassword());
		empty |= StringUtils.isEmpty(changePasswordData.getChangePassword());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.changepassword.Exception = requied param, empty parameter? {}", empty);
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(changePasswordData.getIncsNo()));
		incsno |= changePasswordData.getIncsNo() < 1;
		
		if (incsno) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.changepassword.Exception = check invalid incso no? {}", changePasswordData.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 22. 오전 9:41:55
	 * </pre>
	 * @param response
	 * @param modUserTermsVo
	 * @return
	 */
	public ApiBaseResponse modifyUserTerms(final ApiBaseResponse response, final ModUserTermsVo modUserTermsVo) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(modUserTermsVo.getChCd());
		empty |= (modUserTermsVo.getTerms() == null || modUserTermsVo.getTerms().length == 0);
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.modifyuserterms.Exception = requied param, empty parameter? {}", empty);
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(modUserTermsVo.getIncsNo()));
		incsno |= modUserTermsVo.getIncsNo() < 1;
		
		if (incsno) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.modifyuserterms.Exception = check invalid incso no ? {}", modUserTermsVo.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 22. 오전 9:46:58
	 * </pre>
	 * @param response
	 * @param idSearchVo
	 * @return
	 */
	public ChkUserIdResponse checkDuplicateId(final ChkUserIdResponse response, final IdSearchVo idSearchVo) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(idSearchVo.getName());
		empty |= StringUtils.isEmpty(idSearchVo.getMobile());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.checkuserid.Exception = requied param, empty parameter? {}", empty);			
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	public ApiBaseResponse verifyEntryPassword(final ApiBaseResponse response, final VeriEntPwdVo veriEntPwdVo) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);		
		
		boolean empty = StringUtils.isEmpty(veriEntPwdVo.getLoginId());
		empty |= StringUtils.isEmpty(veriEntPwdVo.getPassword());
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.verifypassword.Exception = requied param, empty parameter? {}", empty);
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(veriEntPwdVo.getIncsNo()));
		if (incsno) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.verifypassword.Exception = check invalid incso no ? {}", veriEntPwdVo.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	public SnsAssResponse checkSnsAssociated(final SnsAssResponse response, final ChkSnsVo chkSnsVo) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(chkSnsVo.getLoginId());
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.checkSnsAssociated.Exception = requied param, empty parameter? {}", empty);
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(chkSnsVo.getIncsNo()));
		if (incsno) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.checkSnsAssociated.Exception = check invalid incso no ? {}", chkSnsVo.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	public SnsUnlinkResponse disconnectSnsAssociated(final SnsUnlinkResponse response, final SnsUnlinkVo snsUnlinkVo) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(snsUnlinkVo.getLoginId());
		empty |= StringUtils.isEmpty(snsUnlinkVo.getSnsType());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.disconnectSnsAssociated.Exception = requied param, empty parameter? {}", empty);
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean invalid = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(snsUnlinkVo.getIncsNo()));
		if (invalid) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.disconnectSnsAssociated.Exception = check invalid incso no ? {}", snsUnlinkVo.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		invalid = !(snsUnlinkVo.getSnsType().equals("KA") || snsUnlinkVo.getSnsType().equals("NA") || snsUnlinkVo.getSnsType().equals("FB") || snsUnlinkVo.getSnsType().equals("AP"));
		if (invalid) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.disconnectSnsAssociated.Exception = check invalid snsType ? {}", snsUnlinkVo.getSnsType());
			response.SetResponseInfo(ResultCode.SNS_INVALID_TYPE);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	public ChTermsResponse checkChannelTermsCondition(final ChTermsResponse response, final ChnTermsCndVo chnTermsCndVo) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(chnTermsCndVo.getChCd());
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.checkChannelTermsCondition.Exception = requied param, empty parameter? {}", empty);			
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(chnTermsCndVo.getIncsNo()));
		if (incsno) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.checkChannelTermsCondition.Exception = check invalid incso no ? {}", chnTermsCndVo.getIncsNo());			
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	public ReqTermsResponse checkRequiredTermsCondition(final ReqTermsResponse response, final ChnTermsCndVo chnTermsCndVo) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(chnTermsCndVo.getChCd());
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.checkRequiredTermsCondition.Exception = requied param, empty parameter? {}", empty);
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(chnTermsCndVo.getIncsNo()));				
		if (incsno) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.checkRequiredTermsCondition.Exception = check invalid incso no ? {}", chnTermsCndVo.getIncsNo());	
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	public ApiBaseResponse checkDuplicateId(final ApiBaseResponse response, final DupIdVo dupIdVo) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(dupIdVo.getLoginId());
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.checkduplicateId.Exception = requied param, empty parameter? {}", empty);			
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}	
	
	public ApiBaseResponse verifyEntryPasswordPolicy(final ApiBaseResponse response, final VeriPwdPlcyVo veriPwdPlcyVo) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(veriPwdPlcyVo.getLoginId());
		empty |= StringUtils.isEmpty(veriPwdPlcyVo.getLoginPassword());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.verifyEntryPasswordPolicy.Exception = requied param, empty parameter? {}", empty);
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(veriPwdPlcyVo.getIncsNo()));
		if (incsno) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.verifyEntryPasswordPolicy.Exception = check invalid incso no ? {}", veriPwdPlcyVo.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}

	public ApiBaseResponse postSyncsnsassociated(final ApiBaseResponse response, final SysncSnsVo sysncSnsVo) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(sysncSnsVo.getLoginId());
		empty |= (sysncSnsVo.getSnsInfo() == null || sysncSnsVo.getSnsInfo().length == 0);		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.postSyncsnsassociated.Exception = requied param, empty parameter? {}", empty);
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(sysncSnsVo.getIncsNo()));
		if (incsno) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.postSyncsnsassociated.Exception = check invalid incso no ? {}", sysncSnsVo.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}	
	
	public ApiBaseResponse deleteSyncsnsassociated(final ApiBaseResponse response, final SysncSnsVo sysncSnsVo) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(sysncSnsVo.getLoginId());
		empty |= (sysncSnsVo.getSnsInfo() == null || sysncSnsVo.getSnsInfo().length == 0);		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.deleteSyncsnsassociated.Exception = requied param, empty parameter? {}", empty);			
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(sysncSnsVo.getIncsNo()));
		if (incsno) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.deleteSyncsnsassociated.Exception = check invalid incso no ? {}", sysncSnsVo.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	public ApiBaseResponse changePasswordJoinOn(final ApiBaseResponse response, final ChgPwdJoinOnVo chgPwdJoinOnVo) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(chgPwdJoinOnVo.getChCd());
		empty |= StringUtils.isEmpty(chgPwdJoinOnVo.getLoginId());
		empty |= StringUtils.isEmpty(chgPwdJoinOnVo.getPassword());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.changePasswordJoinOn.Exception = requied param, empty parameter? {}", empty);
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(chgPwdJoinOnVo.getIncsNo()));
		if (incsno) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.changePasswordJoinOn.Exception = check invalid incso no ? {}", chgPwdJoinOnVo.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	public ApiBaseResponse createUserBy030(final ApiBaseResponse response, final CreateUserData createUserData) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(createUserData.getCn());
		empty |= StringUtils.isEmpty(createUserData.getLoginId());
		empty |= StringUtils.isEmpty(createUserData.getPassword());
		empty |= StringUtils.isEmpty(createUserData.getJoinFlag());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.createUserBy030.Exception = requied param, empty parameter? {}", empty);
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean invalid = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(createUserData.getIncsNo()));
		invalid = !(createUserData.getJoinFlag().equalsIgnoreCase("J") || createUserData.getJoinFlag().equalsIgnoreCase("L")
				|| createUserData.getJoinFlag().equalsIgnoreCase("C"));
		if (invalid) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.createUserBy030.Exception = check invalid incso no ? {}, joinFlag ? {}", createUserData.getIncsNo(), createUserData.getJoinFlag());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	public ApiBaseResponse initializePassword(final ApiBaseResponse response, final InitPasswordData initPasswordData) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(initPasswordData.getChCd());
		empty |= StringUtils.isEmpty(initPasswordData.getLoginId());
		empty |= StringUtils.isEmpty(initPasswordData.getPassword());
		empty |= StringUtils.isEmpty(initPasswordData.getMustchange());
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.initializePassword.Exception = requied param, empty parameter? {}", empty);
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean invalid = !(initPasswordData.getMustchange().equalsIgnoreCase("Y") || initPasswordData.getMustchange().equalsIgnoreCase("N"));
		if(invalid) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.initializePassword.Exception = check invalid must change ? {}", initPasswordData.getMustchange());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		if(initPasswordData.getIncsNo() > 0) {
			invalid = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(initPasswordData.getIncsNo()));
			if (invalid) {
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.initializePassword.Exception = check invalid incso no ? {}", initPasswordData.getIncsNo());
				response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
				return response;
			}
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	public ApiBaseResponse changeWebId(final ApiBaseResponse response, final ChangeWebIdData changeWebIdData) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(changeWebIdData.getLoginId());
		empty |= StringUtils.isEmpty(changeWebIdData.getLoginIdNew());
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.changeWebId.Exception = requied param, empty parameter? {}", empty);
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		if(changeWebIdData.getIncsNo() > 0) {
			boolean invalid = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(changeWebIdData.getIncsNo()));
			if (invalid) {
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.changeWebId.Exception = check invalid incso no ? {}", changeWebIdData.getIncsNo());
				response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
				return response;
			}
		}
		else {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.changeWebId.Exception = check invalid incso no ? {}", changeWebIdData.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
//-----------------------------------------------------------------------------------
//			***		내부 사용 API	  ***
//-----------------------------------------------------------------------------------
	
	public ApiBaseResponse snsAssociate(final ApiBaseResponse response, final SnsParam snsParam) {
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(snsParam.getLoginId());
		empty |= StringUtils.isEmpty(snsParam.getSnsId());
		empty |= StringUtils.isEmpty(snsParam.getSnsType());
		empty |= StringUtils.isEmpty(snsParam.getIncsNo());
		if (empty) {
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean invalid = !(snsParam.getSnsType().equals("KA") || snsParam.getSnsType().equals("NA") || snsParam.getSnsType().equals("FB") || snsParam.getSnsType().equals("AP"));
		if (invalid) {
			log.debug("snsAssociate snsType = {}", snsParam.getSnsType());
			response.SetResponseInfo(ResultCode.SNS_INVALID_TYPE);
			return response;
		}

		invalid = !StringUtil.checkParameter(incsNoRegexp, snsParam.getIncsNo());
		if (invalid) {
			log.debug("snsAssociate incsoNo = {}", snsParam.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		return response;
	}
		
	public ApiBaseResponse snsDisconnect(final ApiBaseResponse response, final SnsParam snsParam) {
	
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(snsParam.getLoginId());
		empty |= StringUtils.isEmpty(snsParam.getIncsNo());
		empty |= StringUtils.isEmpty(snsParam.getSnsType());		
		if (empty) {
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean invalid = !StringUtil.checkParameter(incsNoRegexp, snsParam.getIncsNo());
		if (invalid) {
			log.debug("snsAssociate incsoNo = {}", snsParam.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		invalid = !(snsParam.getSnsType().equals("KA") || snsParam.getSnsType().equals("NA") || snsParam.getSnsType().equals("FB") || snsParam.getSnsType().equals("AP"));
		if (invalid) {
			log.debug("snsAssociate snsType = {}", snsParam.getSnsType());
			response.SetResponseInfo(ResultCode.SNS_INVALID_TYPE);
			return response;
		}
		
		return response;
	}
	
	public ApiBaseResponse checkCreateIntegateDupId(final ApiBaseResponse response, final CreateDupUserRequest createDupUserRequest) {
		
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = createDupUserRequest.getLoginId() == null || createDupUserRequest.getLoginId().length == 0;
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.intergateDuplicateId.Exception = requied param, empty parameter? {}", empty);			
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean invalid = createDupUserRequest.getIncsNo() <= 0;
		invalid &= createDupUserRequest.getIncsNoNew() <= 0;
		if (invalid) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.intergateDuplicateId.Exception = check invalid incso no ? {}", createDupUserRequest.getIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	public ApiBaseResponse checkBpUserInfo(final ApiBaseResponse response, final BpEditUserRequest bpEditUserRequest) {
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(bpEditUserRequest.getParamSiteCd());
		empty &= StringUtils.isEmpty(bpEditUserRequest.getAppChCd());
		empty &= StringUtils.isEmpty(bpEditUserRequest.getCstmId());
		empty &= StringUtils.isEmpty(bpEditUserRequest.getPswd());
		
		if (empty) {
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		return response;
	}
	
	public ApiBaseResponse updateUserIncsNo(final ApiBaseResponse response, final ChUserIncsVo chUserIncsVo) {
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(chUserIncsVo.getWebId());
		empty |= StringUtils.isEmpty(chUserIncsVo.getChCd());
		
		if (empty) {
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean incsno = !StringUtil.checkParameter(incsNoRegexp, Integer.toString(chUserIncsVo.getAsisIncsNo()));
		incsno |= chUserIncsVo.getAsisIncsNo() < 1;
		incsno |= !StringUtil.checkParameter(incsNoRegexp, Integer.toString(chUserIncsVo.getTobeIncsNo()));
		incsno |= chUserIncsVo.getTobeIncsNo() < 1;
		
		if (incsno) {
			log.debug("check invalid incso no? {} , {}", chUserIncsVo.getAsisIncsNo(), chUserIncsVo.getTobeIncsNo());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		return response;
	}
	
	public CheckSnsIdResponse checkSnsId(final CheckSnsIdResponse response, final CheckSnsIdVo checkSnsIdVo) {
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(checkSnsIdVo.getChCd());
		empty |= StringUtils.isEmpty(checkSnsIdVo.getSnsId());
		empty |= StringUtils.isEmpty(checkSnsIdVo.getSnsType());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.checkuserid.Exception = requied param, empty parameter? {}", empty);			
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean invalid = false;
		List<Object> snsTypes = config.snsTypes();
		for(Object snsType : snsTypes) {
			if(snsType.toString().equals(checkSnsIdVo.getSnsType())) invalid = true;
		}
		
		if(!invalid) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.intergateDuplicateId.Exception = check invalid sns type ? {}", checkSnsIdVo.getSnsType());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
		
		return response;
	}
	
	public CryptoResponse checkCrypto(final CryptoResponse response, final CryptoVo cryptoVo) {
		// AP B2C 표준 로그 설정
		LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
				LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
		
		setTrxUuid(response);
		
		boolean empty = StringUtils.isEmpty(cryptoVo.getChCd());
		empty |= StringUtils.isEmpty(cryptoVo.getValue());
		empty |= StringUtils.isEmpty(cryptoVo.getCryptoType());
		
		if (empty) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.error("api.checkCrypto.Exception = requied param, empty parameter? {}", empty);			
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		boolean invalid = !(cryptoVo.getCryptoType().toUpperCase().equals("E") || cryptoVo.getCryptoType().toUpperCase().equals("D"));
		if (invalid) {
			LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
			LogInfo.setLogInfoErr(OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL);
			LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
			log.debug("api.checkCrypto.Exception = check invalid Crypto Type ? {}", cryptoVo.getCryptoType());
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		return response;
	}
}
