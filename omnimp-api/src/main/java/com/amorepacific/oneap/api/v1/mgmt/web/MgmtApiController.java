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
package com.amorepacific.oneap.api.v1.mgmt.web;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.api.exception.ApiBusinessException;
import com.amorepacific.oneap.api.v1.channel.service.ChannelApiService;
import com.amorepacific.oneap.api.v1.mgmt.service.MgmtApiService;
import com.amorepacific.oneap.api.v1.mgmt.validator.MgmtApiValidator;
import com.amorepacific.oneap.api.v1.mgmt.vo.AbusingLockVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.AbusingUserVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.AuthKeyVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChTermsResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChUserIncsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChUserVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.CheckUserInfoVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChgPwdJoinOnVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChkSnsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChkUserIdResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChnTermsCndVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.CryptoResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.CryptoVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.DupIdVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.IdSearchVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.MappingIdSearchVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ModUserTermsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.OmniSearchResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.ReqTermsResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.SearchSnsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.SnsAssResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.SnsMappingVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.SnsUnlinkResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.SnsUnlinkVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.SysncSnsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.TermsResponseVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.TermsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.UserVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.VeriEntPwdVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.VerifyPwdVo;
import com.amorepacific.oneap.api.v1.sns.service.SnsApiService;
import com.amorepacific.oneap.api.v1.wso2.service.Wso2RusmSoapApiService;
import com.amorepacific.oneap.api.v1.wso2.service.Wso2Scim2RestApiService;
import com.amorepacific.oneap.api.v1.wso2.service.Wso2UpmSoapApiService;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2RestApiCreateUserVo;
import com.amorepacific.oneap.api.v1.wso2.vo.Wso2SoapApiCreateAssociatedVo;
import com.amorepacific.oneap.common.check.CheckResponse;
import com.amorepacific.oneap.common.check.Checker;
import com.amorepacific.oneap.common.check.actor.CheckActor;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.BpChangePasswordRequest;
import com.amorepacific.oneap.common.vo.api.BpChangePasswordResponse;
import com.amorepacific.oneap.common.vo.api.BpEditUserRequest;
import com.amorepacific.oneap.common.vo.api.ChangePasswordData;
import com.amorepacific.oneap.common.vo.api.ChangeWebIdData;
import com.amorepacific.oneap.common.vo.api.CheckSnsIdResponse;
import com.amorepacific.oneap.common.vo.api.CheckSnsIdUserVo;
import com.amorepacific.oneap.common.vo.api.CheckSnsIdVo;
import com.amorepacific.oneap.common.vo.api.CreateDupUserRequest;
import com.amorepacific.oneap.common.vo.api.CreateUserData;
import com.amorepacific.oneap.common.vo.api.CreateUserRequest;
import com.amorepacific.oneap.common.vo.api.InitPasswordData;
import com.amorepacific.oneap.common.vo.api.TermsRequest;
import com.amorepacific.oneap.common.vo.api.TermsResponse;
import com.amorepacific.oneap.common.vo.api.VeriPwdPlcyVo;
import com.amorepacific.oneap.common.vo.sns.SnsConnectRequest;
import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.amorepacific.oneap.common.vo.sns.SnsType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.mgmt.web 
 *    |_ MgmtController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 31.
 * @version : 1.0
 * @author : takkies
 */
@Api(tags = { "회원관리" })
@Slf4j
@RestController
@RequestMapping("/v1/mgmt")
public class MgmtApiController {

	@Value("${omni.auth.domain}")
	private String omniAuthDomain;
	
	@Autowired
	private MgmtApiService mgmtApiService;

	@Autowired
	private Wso2RusmSoapApiService wso2RusmSoapApiService;

	@Autowired
	private Wso2Scim2RestApiService wso2Scim2RestApiService;

	@Autowired
	private Wso2UpmSoapApiService wso2UpmSoapApiService;

	@Autowired
	private ChannelApiService channelApiService;
	
	@Autowired
	private SnsApiService snsApiService;
	
	@Autowired
	private SystemInfo systemInfo;

	/**
	 * 
	 * <pre>
	 * comment  : 필수값 관련 코드 (REQ_REQUIRED_PARAM_EMPTY, REQ_INVALID_PARAM)는  공통 체크
	 * author   : hkdang
	 * date     : 2020. 9. 22. 오후 12:26:12
	 * </pre>
	 * 
	 * @param response
	 * @return
	 */
	private boolean checkCommonValidation(ApiBaseResponse response) {
		return (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode()) && !ResultCode.REQ_INVALID_PARAM.getCode().equals(response.getResultCode()));
	}

	/* API 1 */
	@ApiOperation(value = "비밀번호 변경", //
			notes = "경로 시스템에서 패스워드 변경 시, 옴니회원 플랫품으로 패스워드 변경 API입니다.\r\n" //
					+ "경로 시스템에서 패스워드 변경 시, 옴니회원 플랫폼으로 패스워드 변경 요청\r\n" //
					+ "회원 비밀번호 변경 (마이페이지와 같은 공통 화면으로 제공 고려)", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class)
	@PostMapping("/changepassword")
	public ApiBaseResponse changePassword(@ApiParam(name = "ChangePasswordData", value = "비밀번호 변경 파라미터") final @RequestBody ChangePasswordData changePasswordData) {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.changePassword(response, changePasswordData);

		if (checkCommonValidation(response)) {
			try {
				// 비밀번호 유효성 검증 및 복잡도 확인
				CheckResponse chkResponse = new Checker.Builder() //
						.checkType(CheckActor.Type.valueOf("PASSWORD")) //
						.checkValue(changePasswordData.getChangePassword()) //
						.compareValue(StringUtils.isEmpty(changePasswordData.getConfirmPassword()) ? changePasswordData.getChangePassword() : changePasswordData.getConfirmPassword())
						.beforeValue(changePasswordData.getCurrentPassword()) //
						.build()//
						.check();

				log.debug("[changepassword] 비밀번호 유효성 검증 및 복잡도 확인 : {}", StringUtil.printJson(chkResponse));

				if (chkResponse.getStatus() != CheckActor.SUCCESS) {
					switch (chkResponse.getCode()) {
					case OmniConstants.PASSWORD_CONFIRM_FAIL:
						response.SetResponseInfo(ResultCode.PWD_FAIL_CONFIRM);
						break;
					case OmniConstants.PASSWORD_COMPARE_FAIL:
						response.SetResponseInfo(ResultCode.PWD_FAIL_SAME);
						break;
					case OmniConstants.PASSWORD_SIZE_FAIL:
						response.SetResponseInfo(ResultCode.PWD_INVALID_LENGTH);
						break;
					case OmniConstants.PASSWORD_COMBINATION_FAIL:
						response.SetResponseInfo(ResultCode.PWD_COMBINATION_FAIL);
						break;
					case OmniConstants.PASSWORD_INCLUDE_WHITESPACE:
						response.SetResponseInfo(ResultCode.PWD_UNUSABLE_CHAR);
						break;
					default:
						response.SetResponseInfo(ResultCode.PWD_INVALID_POLICY);
						break;
					}
				} else {
					
					// 존재하는 사용자인지 체크
					UserVo userVo = new UserVo();
					userVo.setUserName( changePasswordData.getLoginId() ); 
					userVo.setIncsNo( Integer.toString(changePasswordData.getIncsNo()) );
					
					UserVo user = this.mgmtApiService.getUser(userVo);
					if (user != null) {
						
						// 탈퇴 사용자인지 체크
						boolean isDisabled = this.mgmtApiService.isDisabledUser(changePasswordData.getLoginId());
						if (!isDisabled) {
							
							// 현재 비밀번호 확인
							String currPw = changePasswordData.getCurrentPassword();
							String dbPw = this.mgmtApiService.getPassword(changePasswordData.getLoginId());
							
							if (!SecurityUtil.compareWso2Password(dbPw, currPw)) {
								// AP B2C 표준 로그 설정
								LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL, null, null, null,
										LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
								LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
								LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
								log.error("[changepassword] input PW not same current PW");
								LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
								response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
								return response;
							}
							
							ResponseEntity<String> pwdResponse;
							
							if (StringUtils.hasText(changePasswordData.getCurrentPassword())) { // 이전 비밀번호있을 경우 -> 이쪽으로 타야함 
								pwdResponse = this.wso2RusmSoapApiService.postPasswordByUsername(changePasswordData.getLoginId(), changePasswordData.getCurrentPassword(), changePasswordData.getChangePassword());
							} else {
								pwdResponse = this.wso2RusmSoapApiService.patchPasswordByUsername(changePasswordData.getLoginId(), changePasswordData.getChangePassword());
							}

							// WSO2 API 성공 후
							if (pwdResponse.getStatusCode() == HttpStatus.OK) {

								// 고객통합 비밀번호 변경하기
								// 고객통합 비밀번호 변경하면 뷰티포인트에도 전파하나 시차가 있음.
								boolean success = this.mgmtApiService.changeIntegratedUserPassword(changePasswordData);
								log.debug("[changepassword] 고객통합 비밀번호 변경 res = {}", success);
								if (success) {

									// 뷰티포인트 비밀번호 변경하기
									BpChangePasswordResponse bpResponse = new BpChangePasswordResponse();
									bpResponse.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));

									BpChangePasswordRequest bpChangePasswordRequest = new BpChangePasswordRequest();
									bpChangePasswordRequest.setCstmId(changePasswordData.getLoginId());
									bpChangePasswordRequest.setIncsNo(Integer.toString(changePasswordData.getIncsNo()));
									bpChangePasswordRequest.setPasswd(changePasswordData.getCurrentPassword());
									bpChangePasswordRequest.setPasswd_new(changePasswordData.getChangePassword());

									BpChangePasswordResponse res = this.channelApiService.beautyPointChangePassword(bpResponse, bpChangePasswordRequest);
									
									if ("000".equalsIgnoreCase(res.getRsltCd())) {
										log.debug("[changepassword] 뷰티포인트 비밀번호 변경 성공 : {}", res.getRsltMsg());
									} else {
										log.debug("[changepassword] 뷰티포인트 비밀번호 변경 실패 : {}", StringUtil.printJson(res));
									}
									
									/*
									 * if ("SUCCESS".equalsIgnoreCase(res.getResult())) { response.SetResponseInfo(ResultCode.SUCCESS); } else {
									 * response.SetResponseInfo(ResultCode.PWD_JOINON_SYSNC_FAIL); }
									 */
								}
								
								String profile = this.systemInfo.getActiveProfiles()[0];
								profile = StringUtils.isEmpty(profile) ? "dev" : profile;
								profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
								
								// if("dev".equals(profile) || "local".equals(profile)) {
									ResponseEntity<String> sessionResponse = this.wso2Scim2RestApiService.removeSessionByUserName(user.getUserName());
									if(sessionResponse.getStatusCode() == HttpStatus.OK || sessionResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
										log.debug("[changepassword] 사용자 세션 삭제 성공");
										log.info("▶▶▶▶▶ [changepassword] chCd : {}, incsNo : {}, loginId : {}", changePasswordData.getChCd(), changePasswordData.getIncsNo(), changePasswordData.getLoginId());
									}
									else {
										log.error("[changepassword] 사용자 세션 삭제 실패");
									}

								// }
								
								response.SetResponseInfo(ResultCode.SUCCESS);

								if (chkResponse.getStrength() == 0) {
									response.setMessage(ResultCode.SUCCESS.message() + "(비밀번호 복잡도 : 안전)");
								} else if (chkResponse.getStrength() == 1) {
									response.setMessage(ResultCode.SUCCESS.message() + "(비밀번호 복잡도 : 보통)");
								} else if (chkResponse.getStrength() == 2) {
									response.setMessage(ResultCode.SUCCESS.message() + "(비밀번호 복잡도 : 위험)");
								} else if (chkResponse.getStrength() == 3) {
									response.setMessage(ResultCode.SUCCESS.message() + "(비밀번호 복잡도 : 위험)");
								}

							} else {
								// AP B2C 표준 로그 설정
								LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
										LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
								LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
								LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
								log.error("api.changepassword.Exception = {}", pwdResponse.getBody());
								LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
								response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
							}
						} else {
							response.SetResponseInfo(ResultCode.USER_DISABLED);
						}
					} else {
						response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
					}
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.API_SERVER_ERROR);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.changepassword.Exception = {}", e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}

		return response;
	}

	/* API 2 */
	@ApiOperation(value = "약관 동의 및 철회", //
			notes = "경로 시스템에서 접속 후, 약관 동의 시 API 호출\r\n" //
					+ "경로 시스템에서 탈퇴 시, 탈퇴 처리 후 옴니회원 약관 철회 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class)
	@PostMapping("/modifyuserterms")
	public ApiBaseResponse modifyUserTerms(@ApiParam(name = "modUserTermsVo", value = "약관 동의 및 철회 파라미터") final @RequestBody ModUserTermsVo modUserTermsVo) {
		
		log.debug("[modifyuserterms] request : {}", StringUtil.printJson(modUserTermsVo));
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.modifyUserTerms(response, modUserTermsVo);

		if (checkCommonValidation(response)) {
			int rtnSize = 0;
			boolean isWithdraw = true; // terms.getTncAgrYn 이 모두 D(철회) 이면 탈퇴로 판단.
			for (TermsVo terms : modUserTermsVo.getTerms()) {

				if (StringUtils.isEmpty(terms.getTcatCd()) || StringUtils.isEmpty(terms.getTncAgrYn())) {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL, null, null, null,
							LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);					
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					log.error("api.modifyuserterms.Exception = requied param, empty parameter? true");
					LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
					response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
				} else if ((terms.getTncAgrYn().equals("A") || terms.getTncAgrYn().equals("D")) == false) {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL, null, null, null,
							LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);										
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					log.error("api.modifyuserterms.Exception = check invalid tncArrYn ? {}", terms.getTncAgrYn());
					LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
					response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
				} else {

					terms.setChCd(modUserTermsVo.getChCd());
					terms.setIncsNo(modUserTermsVo.getIncsNo());

					if(terms.getTncAgrYn().equals("A")) {
						isWithdraw = false;
					}
					
					// 약관 존재여부 확인
					boolean rtn = this.mgmtApiService.isTermsExist(terms);
					if (rtn) {
						rtnSize++;

						try {

							this.mgmtApiService.mergeTermYn(terms); // 약관 insert or update
							this.mgmtApiService.inserOccuCustTncHist(terms); // 약관 이력 insert

						} catch (ApiBusinessException e) {
							// AP B2C 표준 로그 설정
							LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
									LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);																	
							LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
							LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
							log.error("api.modifyuserterms.ApiBusinessException = {}", e.getMessage());
							LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
							response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						}
					} else {
						response.SetResponseInfo(ResultCode.TERMS_NOT_FOUND);
					}
				}
			}

			// 위 로직에서 response 처리가 안됐으면 (= 정상이면)
			if (StringUtils.isEmpty(response.getResultCode())) {
				if (rtnSize == modUserTermsVo.getTerms().length) {
					response.SetResponseInfo(ResultCode.SUCCESS);
					
					if(isWithdraw) { // 탈퇴시 제3자 제공동의도 철회
						try { 
							TermsVo terms = new TermsVo();
							terms.setChCd(modUserTermsVo.getChCd());
							terms.setIncsNo(modUserTermsVo.getIncsNo());
							terms.setTcatCd( this.mgmtApiService.getCorpTermsCode(modUserTermsVo.getChCd()) );
							terms.setTncAgrYn("D");
							log.debug("▶▶▶▶▶▶ [modifyuserterms] update CorpTerms info : {}", StringUtil.printJson(terms));
						
							this.mgmtApiService.mergeTermYn(terms);
							this.mgmtApiService.inserOccuCustTncHist(terms);
							
							// 임시 테이블 사용자 제거
							boolean isDeleteChUser = this.mgmtApiService.deleteChannelUser(terms);
							log.debug("▶▶▶▶▶▶ [modifyuserterms] isDeleteChUser? : {}", isDeleteChUser);
							
							/* 
							// 카카오싱크 언링크 추가 -> createUserBy030 탈퇴시로 이동
							BaseResponse snsResponse = this.mgmtApiService.doSnsUnlink(modUserTermsVo.getIncsNo());
							log.debug("▶▶▶▶▶▶ [API/modifyuserterms] SNS Unlink Response : {}", StringUtil.printJson(snsResponse));
							*/
							
						} catch (ApiBusinessException e) {
							// AP B2C 표준 로그 설정
							LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
									LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);																	
							LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
							LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
							log.error("api.modifyuserterms.ApiBusinessException = {}", e.getMessage());
							LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
							response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						}
					}
				} else if (rtnSize > 0) { // 일부만 업데이트
					response.SetResponseInfo(ResultCode.TERMS_MODIFY_LITTLE);
					log.debug("▶▶▶▶▶▶ [modifyuserterms] some update : {}", response.getResultCode());
				} else {
					response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
					log.debug("▶▶▶▶▶▶ [modifyuserterms] update error : {}", response.getResultCode());
				}
			}
		}

		return response;
	}

	/* API 3 */
	@ApiOperation(value = "회원 아이디 조회", //
			notes = "회원 아이디 조회", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ChkUserIdResponse.class)
	@PostMapping("/checkuserid")
	public ChkUserIdResponse checkUserId(@ApiParam(name = "idSearchVo", value = "조회 파라미터", required = true) final @RequestBody IdSearchVo idSearchVo) {
		
		ChkUserIdResponse response = new ChkUserIdResponse();
		response = MgmtApiValidator.checkDuplicateId(response, idSearchVo);

		if (checkCommonValidation(response)) {
			try {
				String userName = this.mgmtApiService.checkUserId(idSearchVo);
				if (userName == null || userName.isEmpty()) {
					response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
				} else {
					boolean rtn = this.mgmtApiService.isDisabledUser(userName);
					if (rtn) {
						response.SetResponseInfo(ResultCode.USER_DISABLED);
					} else {
						response.setCustWebId(userName);
						response.SetResponseInfo(ResultCode.SUCCESS);
					}
				}
				
				log.debug("▶▶▶▶▶▶[checkuserid] response code : {}", response.getResultCode());
			} catch (ApiBusinessException e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.checkuserid.ApiBusinessException = {}", e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}

		return response;
	}

	/* API 4 */
	@ApiOperation(value = "경로 시스템에서 마이페이지 접근 시, 비밀번호 확인 및 검증", //
			notes = "경로 시스템에서 마이페이지 접근 시, 비밀번호 확인 및 검증용 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class)
	@PostMapping("/verifypassword")
	public ApiBaseResponse verifyEntryPassword(@ApiParam(name = "veriEntPwdVo", value = "검증 파라미터", required = true) final @RequestBody VeriEntPwdVo veriEntPwdVo) throws ApiBusinessException {
		
		log.debug("[verifypassword] request : {}", StringUtil.printJson(veriEntPwdVo));
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.verifyEntryPassword(response, veriEntPwdVo);

		log.debug("[verifypassword] validate : {}", StringUtil.printJson(response));
		
		if (checkCommonValidation(response)) {
			try {
				
				UserVo userVo = new UserVo();
				userVo.setUserName( veriEntPwdVo.getLoginId() ); 
				userVo.setIncsNo( Integer.toString(veriEntPwdVo.getIncsNo()) );
				
				log.debug("[verifypassword] exist user request : {}", StringUtil.printJson(userVo));
				
				UserVo user = this.mgmtApiService.getUser(userVo);
				if (user != null) {
					boolean rtn = this.mgmtApiService.isDisabledUser(veriEntPwdVo.getLoginId());
					if (rtn) {
						response.SetResponseInfo(ResultCode.USER_DISABLED);
					} else {
						final VerifyPwdVo verifyVo = new VerifyPwdVo();
						verifyVo.setId(user.getUserName());
						verifyVo.setPassword(SecurityUtil.getEncodedWso2Password(veriEntPwdVo.getPassword()));

						rtn = this.mgmtApiService.verifyPassword(verifyVo);
						if (rtn) {
							response.SetResponseInfo(ResultCode.SUCCESS);
						} else {
							response.SetResponseInfo(ResultCode.PWD_FAIL_CONFIRM);
						}
					}
				} else {
					response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
				}
				
				log.debug("[verifypassword] response code : {}", response.getResultCode());
			} catch (ApiBusinessException e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.verifypassword.ApiBusinessException = {}", e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		} else {
			log.debug("[verifypassword] validate : {}", StringUtil.printJson(response));
		}

		return response;
	}

	/* API 5 */
	@ApiOperation(value = "SNS 연동 정보 조회", //
			notes = "SNS 연동 정보 조회(마이페이지와 같은 공통 화면으로 제공 고려) 입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = SnsAssResponse.class)
	@PostMapping("/checksnsass")
	public SnsAssResponse checkSnsAssociated(@ApiParam(name = "chkSnsVo", value = "조회 파라미터", required = true) final @RequestBody ChkSnsVo chkSnsVo) throws ApiBusinessException {
		
		SnsAssResponse response = new SnsAssResponse();
		response = MgmtApiValidator.checkSnsAssociated(response, chkSnsVo);

		if (checkCommonValidation(response)) {
			try {
				
				UserVo userVo = new UserVo();
				userVo.setUserName( chkSnsVo.getLoginId() ); 
				userVo.setIncsNo( Integer.toString(chkSnsVo.getIncsNo()) );
				
				UserVo user = this.mgmtApiService.getUser(userVo);
				if (user != null) {
					List<SearchSnsVo> snsInfoList = this.mgmtApiService.getSnsInfoList(user.getUserName());
					if (snsInfoList.size() > 0) {
						response.SetResponseInfo(ResultCode.SUCCESS);
						
						Iterator<SnsType> it = EnumSet.allOf(SnsType.class).iterator();
						while(it.hasNext()) {
							String snsType = ((SnsType)it.next()).getType();
						
							boolean isAlreadyAssociated = false;
							for(SearchSnsVo snsVo : snsInfoList) {
								if(snsVo.getSnsType().equalsIgnoreCase(snsType)) {
									isAlreadyAssociated = true;
								}
							}
							
							// 연동되지 않은 SNS 도 빈값으로 담아 준다 -> 경로 요청 사항
							if(isAlreadyAssociated == false) {
								SearchSnsVo searchSnsVo = new SearchSnsVo();
								searchSnsVo.setSnsId("");
								searchSnsVo.setSnsName("");
								searchSnsVo.setSnsType(snsType);
								searchSnsVo.setCustWebId(user.getUserName());
								searchSnsVo.setCreateDate("");
								
								snsInfoList.add(searchSnsVo);
							}
						}
						
						response.setIncsNo(chkSnsVo.getIncsNo());
						response.setLoginId(chkSnsVo.getLoginId());
						response.setSnsInfos(snsInfoList.toArray(new SearchSnsVo[snsInfoList.size()]));
					} else {
						response.SetResponseInfo(ResultCode.SNS_NOT_FOUND_ASSO);
					}
				} else {
					response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
				}
				
				log.debug("[checksnsass] response code : {}", response.getResultCode());
			} catch (ApiBusinessException e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.checksnsass.ApiBusinessException = {}", e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}

		return response;
	}

	/* API 6 */
	/*
	 * @ApiOperation(value = "SNS 연동 정보 연결", // notes = "SNS 연동 정보 연결 기능(마이페이지와 같은 공통 화면으로 제공 고려) 입니다.", // httpMethod = "POST", // consumes =
	 * MediaType.APPLICATION_JSON_VALUE, // produces = MediaType.APPLICATION_JSON_VALUE, // response = SnsAssResponse.class, // hidden = true)
	 * 
	 * @PostMapping("/cnctsnsass") public SnsAssResponse connetSnsAssociated(@ApiParam(name = "snsAssVo", value = "SNS 연동 정보 파라미터")
	 * final @RequestBody SnsAssVo snsAssVo) throws ApiBusinessException {
	 * 
	 * SnsAssResponse response = new SnsAssResponse(); response.setTrxUuid(WebUtil.getHeader(OmniConstants.TRX_UUID));
	 * 
	 * if(StringUtils.isEmpty(snsAssVo.getLoginId())) {
	 * response.setMessage(this.messageSource.getMessage("api.connetSnsAssociated.empty.snsAssVo.loginId", null, LocaleUtil.getLocale())); }
	 * else if (StringUtil.checkParameter(Integer.toString(snsAssVo.getIncsNo()), "^[0-9]{8}$") == false || snsAssVo.getIncsNo() < 1) {
	 * response.setMessage(this.messageSource.getMessage("api.connetSnsAssociated.empty.snsAssVo.incsNo", null, LocaleUtil.getLocale())); } else
	 * if (snsAssVo.getSnsInfo() != null) { // SnsVo Validation 체크 SnsVo snsInfo = snsAssVo.getSnsInfo(); if
	 * (StringUtils.isEmpty(snsInfo.getSnsId())) { response.setMessage(this.messageSource.getMessage("api.param.snsVo.empty.snsId", null,
	 * LocaleUtil.getLocale())); } else if (StringUtils.isEmpty(snsInfo.getSnsType())) {
	 * response.setMessage(this.messageSource.getMessage("api.param.snsVo.empty.snsType", null, LocaleUtil.getLocale())); } else if
	 * (StringUtils.isEmpty(snsInfo.getCustWebId())) { response.setMessage(this.messageSource.getMessage("api.param.snsVo.empty.custWebId",
	 * null, LocaleUtil.getLocale())); } else if (StringUtils.isEmpty(snsInfo.getAssociationId())) {
	 * response.setMessage(this.messageSource.getMessage("api.param.snsVo.empty.associationId", null, LocaleUtil.getLocale())); } } else {
	 * 
	 * UserVo userVo = new UserVo(); userVo.setUserName(snsAssVo.getLoginId()); userVo.setIncsNo(Integer.toString(snsAssVo.getIncsNo()));
	 * 
	 * boolean rtn = this.mgmtApiService.isUserExist(userVo); if(rtn) {
	 * 
	 * //Wso2SoapApiCreateAssociatedVo caVo = new Wso2SoapApiCreateAssociatedVo(snsAssVo.getSnsInfo().getSnsId(),
	 * snsAssVo.getSnsInfo().getAssociationId()); //this.wso2UpmSoapApiService.patchAssociatedIdsByUmUserId(umUserId,
	 * wso2SoapApiMergeAssociatedVo)
	 * 
	 * // 페이지로 개발
	 * 
	 * } }
	 * 
	 * return response; }
	 */

	/* API 7 */
	@ApiOperation(value = "SNS 연동 정보 해제", //
			notes = "SNS 연동 정보 해제 기능 입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = SnsUnlinkResponse.class)
	@PostMapping("/dscnctsnsass")
	public SnsUnlinkResponse disconnectSnsAssociated(@ApiParam(name = "snsAssVo", value = "SNS 연동 정보 파라미터") final @RequestBody SnsUnlinkVo snsUnlinkVo) throws ApiBusinessException {
		
		SnsUnlinkResponse response = new SnsUnlinkResponse();
		response = MgmtApiValidator.disconnectSnsAssociated(response, snsUnlinkVo);

		if (checkCommonValidation(response) && !ResultCode.SNS_INVALID_TYPE.getCode().equals(response.getResultCode())) {
			// 유효한 사용자인지 체크
			UserVo userVo = new UserVo();
			userVo.setUserName( snsUnlinkVo.getLoginId() ); 
			userVo.setIncsNo( Integer.toString(snsUnlinkVo.getIncsNo()) );
			
			UserVo user = this.mgmtApiService.getUser(userVo);
			if (user == null) {
				response.SetResponseInfo(ResultCode.USER_INVALID);
				return response;
			}

			// 탈퇴 사용자 체크
			boolean rtn = this.mgmtApiService.isDisabledUser(snsUnlinkVo.getLoginId());
			if (rtn) {
				response.SetResponseInfo(ResultCode.USER_DISABLED);
				return response;
			}

			// 여러 SNS 계정을 연동해놓은 (ex)각 경로별로 다른 SNS 아이디에 매핑 가능) 아이디가 존재할 수 있어서 컬렉션으로 받는다.
			List<String> idpUserIds = this.mgmtApiService.getAssociatedSnsId(snsUnlinkVo);
			if (idpUserIds != null && idpUserIds.size() > 0) {
				try {
					for(String snsId : idpUserIds) {
						Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo = new Wso2SoapApiCreateAssociatedVo(snsUnlinkVo.getSnsType(), snsId);
						ResponseEntity<String> unlinkResponse = wso2UpmSoapApiService.deleteAssociatedIdsByUsername(snsUnlinkVo.getLoginId(), wso2SoapApiCreateAssociatedVo);

						if (unlinkResponse.getStatusCode() == HttpStatus.OK) {
							response.setIncsNo(snsUnlinkVo.getIncsNo());
							response.setLoginId(snsUnlinkVo.getLoginId());
							response.setSnsType(snsUnlinkVo.getSnsType());

							response.SetResponseInfo(ResultCode.SUCCESS);
							
							// join-on 해제
							String profile = this.systemInfo.getActiveProfiles()[0];
							profile = StringUtils.isEmpty(profile) ? "dev" : profile;
							profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
							
							SnsConnectRequest snsConnectRequest = new SnsConnectRequest();
							snsConnectRequest.setConnectYN("N");
							snsConnectRequest.setUcstmid(Integer.toString(snsUnlinkVo.getIncsNo()));
							snsConnectRequest.setCstmid(snsUnlinkVo.getLoginId());
							snsConnectRequest.setSnsAuthkey(snsId);
							snsConnectRequest.setSnsType(snsUnlinkVo.getSnsType());
							log.debug("▶▶▶▶▶▶ [Join-On SNS Delete Sync] snsConnectRequest = {}", StringUtil.printJson(snsConnectRequest));
							
							ApiBaseResponse joinOnSyncRes = new ApiBaseResponse();
							joinOnSyncRes = this.snsApiService.joinOnSnsLinker(joinOnSyncRes, snsConnectRequest);
							log.debug("▶▶▶▶▶▶ [Join-On SNS Delete Sync] Res = {}", joinOnSyncRes);

						} else { // if(unlinkResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
							// AP B2C 표준 로그 설정
							LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
									LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
							LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
							LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
							log.error("api.disconnectSnsAssociated.Exception = {}", unlinkResponse.getBody());
							LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
							response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						}
					}
				} catch (Exception e) { // wso2 api exception
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
							LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					log.error("api.disconnectSnsAssociated.Exception = {}", e.getMessage());
					LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
					response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				}
			} else {
				response.SetResponseInfo(ResultCode.SNS_NOT_FOUND_ASSO);
			}
		}

		return response;
	}

	/* API 8 */
	@ApiOperation(value = "경로별 약관 동의 내역 조회", //
			notes = "로그인한 회원의 해당 경로의 약관 동의 내역 정보 제공 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ChTermsResponse.class)
	@PostMapping("/checkchntermscnd")
	public ChTermsResponse checkChannelTermsCondition(

			@ApiParam(name = "chnTermsCndVo", value = "경로별 약관 동의 파라미터") final @RequestBody ChnTermsCndVo chnTermsCndVo

	) throws ApiBusinessException {
		
		ChTermsResponse response = new ChTermsResponse();
		response = MgmtApiValidator.checkChannelTermsCondition(response, chnTermsCndVo);

		if (checkCommonValidation(response)) {
			try {
				UserVo user = this.mgmtApiService.getUserByIncsNo(Integer.toString(chnTermsCndVo.getIncsNo()));
				if (user != null) {

					boolean rtn = this.mgmtApiService.isDisabledUser(user.getUserName());
					if (rtn) {
						response.SetResponseInfo(ResultCode.USER_DISABLED);
					} else {
						List<TermsResponseVo> termsList = this.mgmtApiService.getTermsList(chnTermsCndVo);
						if (termsList.size() > 0) {
							response.setChCd(chnTermsCndVo.getChCd());
							response.setIncsNo(chnTermsCndVo.getIncsNo());
							response.setLoginId(user.getUserName());
							response.setTerms(termsList.toArray(new TermsResponseVo[termsList.size()]));

							response.SetResponseInfo(ResultCode.SUCCESS);
						} else {
							response.SetResponseInfo(ResultCode.TERMS_NOT_HIST);
						}
					}
				} else {
					response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
				}

				log.debug("[check channel terms condition] response code : {}", response.getResultCode());
			} catch (ApiBusinessException e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.checkChannelTermsCondition.Exception = check invalid incso no ? {}", e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}

		return response;
	}

	/* API 9 */
	@ApiOperation(value = "미동의 필수 약관 조회", //
			notes = "로그인한 회원의 해당 경로의 미동의 필수 약관 정보 제공 API 입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ReqTermsResponse.class)
	@PostMapping("/checkreqtermscnd")
	public ReqTermsResponse checkRequiredTermsCondition(@ApiParam(name = "reqTermsCndVo", value = "필수 약관 동의 파라미터") final @RequestBody ChnTermsCndVo chnTermsCndVo

	) throws ApiBusinessException {

		ReqTermsResponse response = new ReqTermsResponse();
		response = MgmtApiValidator.checkRequiredTermsCondition(response, chnTermsCndVo);

		if (checkCommonValidation(response)) {
			try {
				
				UserVo user = this.mgmtApiService.getUserByIncsNo(Integer.toString(chnTermsCndVo.getIncsNo()));
				if (user != null) {
					boolean rtn = this.mgmtApiService.isDisabledUser(user.getUserName());
					if (rtn) {
						response.SetResponseInfo(ResultCode.USER_DISABLED);
					} else {

						List<TermsResponseVo> reqTermsList = this.mgmtApiService.getRequiredTermsList(chnTermsCndVo);
						if (reqTermsList.size() > 0) {
							response.setChCd(chnTermsCndVo.getChCd());
							response.setIncsNo(chnTermsCndVo.getIncsNo());
							response.setTerms(reqTermsList.toArray(new TermsResponseVo[reqTermsList.size()]));
							response.setTermsCnt(reqTermsList.size());

							response.SetResponseInfo(ResultCode.SUCCESS);
						} else {
							response.SetResponseInfo(ResultCode.TERMS_NOT_EXIST_REQ_TERMS);
						}
					}
				} else {
					response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
				}
				
				log.debug("[check channel Required terms condition] response code : {}", response.getResultCode());
			} catch (ApiBusinessException e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.checkRequiredTermsCondition.ApiBusinessException = {}", e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}

		return response;
	}

	/* API 10 */
	@ApiOperation(value = "ID 사용가능여부 체크", //
			notes = "신규 회원 가입 시, ID 중복 및 유효성 체크용 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class)
	@PostMapping("/checkdupid")
	public ApiBaseResponse checkDuplicateId(

			@ApiParam(name = "dupIdVo", value = "ID 사용가능여부 체크 파라미터") final @RequestBody DupIdVo dupIdVo

	) throws ApiBusinessException {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.checkDuplicateId(response, dupIdVo);

		if (checkCommonValidation(response)) {

			boolean rtn = this.mgmtApiService.isDisabledUser(dupIdVo.getLoginId());
			if (rtn) {
				log.debug("◆◆◆◆◆◆ [checkdupid] disabled user id : {}", dupIdVo.getLoginId());
				response.SetResponseInfo(ResultCode.ID_ALREADY_EXIST);
				return response;
			}

			CheckResponse chkResponse = new Checker.Builder().checkType(CheckActor.Type.valueOf("ID")).checkValue(dupIdVo.getLoginId()).build().check();

			if (chkResponse.getStatus() == CheckActor.SUCCESS) {
				try {
					rtn = this.mgmtApiService.checkDuplicateId(dupIdVo);
					if (!rtn) {
						response.SetResponseInfo(ResultCode.SUCCESS);
					} else {
						response.SetResponseInfo(ResultCode.ID_ALREADY_EXIST);
					}
				} catch (ApiBusinessException e) {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
							LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					log.error("api.checkduplicateId.ApiBusinessException = {}", e.getMessage());
					LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
					response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				}
			} else {
				// 체크실패
				if (chkResponse.getCode().equalsIgnoreCase(OmniConstants.ID_SIZE_FAIL)) {
					response.SetResponseInfo(ResultCode.ID_INVALID_LENGTH);
				} else { // if(chkResponse.getCode().equalsIgnoreCase(OmniConstants.ID_VALIDATION_FAIL))
					response.SetResponseInfo(ResultCode.ID_VALIDCHECK_FAIL);
				}
			}
		}

		log.debug("[check duplicate ID] response code : {}", response.getResultCode());
		return response;
	}

	/* API 11 */
	@ApiOperation(value = "비밀번호 유효성 체크", //
			notes = "비밀번호 변경을 위해 입력한 비밀번호의 정책 유효성 체크 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class)
	@PostMapping("/verifypasswordpolicy")
	public ApiBaseResponse verifyEntryPasswordPolicy(@ApiParam(name = "veriPwdPlcyVo", value = "비밀번호 유효성 체크 파라미터") final @RequestBody VeriPwdPlcyVo veriPwdPlcyVo) throws ApiBusinessException {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.verifyEntryPasswordPolicy(response, veriPwdPlcyVo);

		if (checkCommonValidation(response)) {
			CheckResponse chkResponse = new Checker.Builder().checkType(CheckActor.Type.valueOf("PASSWORD")).checkValue(veriPwdPlcyVo.getLoginPassword()).build().check();

			if (chkResponse.getStatus() == CheckActor.SUCCESS) {
				UserVo userVo = new UserVo();
				userVo.setUserName(veriPwdPlcyVo.getLoginId());
				userVo.setIncsNo(Integer.toString(veriPwdPlcyVo.getIncsNo()));
				
				UserVo user = this.mgmtApiService.getUser(userVo);
				if (user == null) {
					response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
					return response;
				}
				
				// 현재 사용중인 비밀번호는 암호화 되어 있기때문에 checker로 사용하지 않고 체크할 비밀번호를 암호화해서 비교
				String currPw = this.mgmtApiService.getPassword(veriPwdPlcyVo.getLoginId());
				if(StringUtils.isEmpty(currPw)) {
					log.debug("▶▶▶▶▶▶ [verifypasswordpolicy current password is null] loginId : {}", veriPwdPlcyVo.getLoginId());
					response.SetResponseInfo(ResultCode.USER_INVALID);
					return response;
				}
				
				String checkPw = SecurityUtil.getEncodedWso2Password(veriPwdPlcyVo.getLoginPassword());
				if (!currPw.isEmpty()) {
					if (currPw.equals(checkPw)) { // 현재 사용중인 비밀번호와 체크할 비밀번호가 동일함
						response.SetResponseInfo(ResultCode.PWD_FAIL_SAME);
					} else {
						response.SetResponseInfo(ResultCode.SUCCESS);
					}
				} else { // 현재 사용중인 비밀번호 없음 -> 성공
					response.SetResponseInfo(ResultCode.SUCCESS);
				}
			} else { // 유효성 & 복잡도 검증 통과 실패
				switch (chkResponse.getCode()) {
				case OmniConstants.PASSWORD_COMPARE_FAIL:
					response.SetResponseInfo(ResultCode.PWD_FAIL_SAME);
					break;
				case OmniConstants.PASSWORD_SIZE_FAIL:
					response.SetResponseInfo(ResultCode.PWD_INVALID_LENGTH);
					break;
				case OmniConstants.PASSWORD_COMBINATION_FAIL:
					response.SetResponseInfo(ResultCode.PWD_COMBINATION_FAIL);
					break;
				case OmniConstants.PASSWORD_INCLUDE_WHITESPACE:
					response.SetResponseInfo(ResultCode.PWD_UNUSABLE_CHAR);
					break;
				default:
					response.SetResponseInfo(ResultCode.PWD_INVALID_POLICY);
					break;
				}
			}
		}

		return response;
	}
	
	/* API 12 */
	@ApiOperation(value = "SNS 매핑 해제 내역 동기화", //
		notes = "옴니회원에서 SNS 연동 정보 해제 시, 동기화 처리 기능 API입니다.", //
		httpMethod = "POST", //
		consumes = MediaType.APPLICATION_JSON_VALUE, //
		produces = MediaType.APPLICATION_JSON_VALUE, //
		response = ApiBaseResponse.class)
	@PostMapping("/syncsnsdisassociate")
	public ApiBaseResponse syncSnsDisassociated(
	
		@ApiParam(name = "sysncSnsVo", value = "SNS 매핑 내역 동기화 파라미터") final @RequestBody SysncSnsVo sysncSnsVo
	
	) throws ApiBusinessException {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.deleteSyncsnsassociated(response, sysncSnsVo);
		
		if (checkCommonValidation(response)) {
			boolean rtn = this.mgmtApiService.isUserExistByLoginId(sysncSnsVo.getLoginId()); //this.mgmtApiService.isUserExist( Integer.toString(sysncSnsVo.getIncsNo()) );
			if (rtn) {
				rtn = this.mgmtApiService.isDisabledUser(sysncSnsVo.getLoginId());
				if (rtn) {
					response.SetResponseInfo(ResultCode.USER_DISABLED);
				} else {
					for (SnsMappingVo snsVo : sysncSnsVo.getSnsInfo()) {
						if (StringUtils.isEmpty(snsVo.getSnsId()) || StringUtils.isEmpty(snsVo.getSnsType()) || StringUtils.isEmpty(snsVo.getCustWebId())) {
							// AP B2C 표준 로그 설정
							LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL, null, null, null,
									LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);							
							LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
							LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
							log.error("api.deleteSyncsnsassociated.Exception = requied param, empty parameter? true");
							LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
							response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
							break;
						} else if ((snsVo.getSnsType().equalsIgnoreCase("NA") || snsVo.getSnsType().equalsIgnoreCase("KA") || snsVo.getSnsType().equalsIgnoreCase("FB") || snsVo.getSnsType().equalsIgnoreCase("AP")) == false) {
							// AP B2C 표준 로그 설정
							LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL, null, null, null,
									LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
							LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
							LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
							log.error("api.deleteSyncsnsassociated.Exception = check invalid sns type ? {}", snsVo.getSnsType());
							LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
							response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
							break;
						} else {
							MappingIdSearchVo mappingIdSearchVo = new MappingIdSearchVo();
							mappingIdSearchVo.setLoginId(sysncSnsVo.getLoginId());
							mappingIdSearchVo.setSnsType(snsVo.getSnsType());
							mappingIdSearchVo.setSnsId(snsVo.getSnsId());
		
							rtn = this.mgmtApiService.isMappingExsist(mappingIdSearchVo);
							if (rtn) {
								try {
									Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo = new Wso2SoapApiCreateAssociatedVo(mappingIdSearchVo.getSnsType(), snsVo.getSnsId());
		
									ResponseEntity<String> delResponse = this.wso2UpmSoapApiService.deleteAssociatedIdsByUsername(mappingIdSearchVo.getLoginId(), wso2SoapApiCreateAssociatedVo);
									if (delResponse.getStatusCode() == HttpStatus.OK) {
										response.SetResponseInfo(ResultCode.SUCCESS);
									} else {
										// AP B2C 표준 로그 설정
										LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
												LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
										LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
										LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
										log.error("api.deleteSyncsnsassociated.Exception = {}", delResponse.getBody());
										LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
										response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
										break;
									}
									
								} catch (Exception e) {
									// AP B2C 표준 로그 설정
									LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
											LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
									LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
									LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
									log.error("api.deleteSyncsnsassociated.delete.Exception = {}", e.getMessage());
									LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
									response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
									break;
								}
							} else {
								response.SetResponseInfo(ResultCode.SNS_NOT_FOUND_ASSO_DISCONECT_FAIL);
								break;
							}
						}
					} // end of loop, sysncSnsVo.getSnsInfos()
				}
			} else {
				response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
			}
		}
		
		log.debug("[sync sns disassociate] response code : {}", response.getResultCode());
		return response;
	}

	/* API 13 */
	@ApiOperation(value = "SNS 매핑 내역 동기화", //
			notes = "옴니회원에서 SNS 연동 정보 연결 시, 동기화 처리 기능 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class)
	@PostMapping("/syncsnsassociated")
	public ApiBaseResponse postSyncsnsassociated(

			@ApiParam(name = "sysncSnsVo", value = "SNS 매핑 내역 동기화 파라미터") final @RequestBody SysncSnsVo sysncSnsVo

	) throws ApiBusinessException {
		
		log.debug("[sync sns associated] : {}", StringUtil.printJson(sysncSnsVo));

		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.postSyncsnsassociated(response, sysncSnsVo);

		if (checkCommonValidation(response)) {
			
			boolean rtn = this.mgmtApiService.isUserExistByLoginId(sysncSnsVo.getLoginId()); // this.mgmtApiService.isUserExist( Integer.toString(sysncSnsVo.getIncsNo()) );
			if (rtn) {

				rtn = this.mgmtApiService.isDisabledUser(sysncSnsVo.getLoginId());
				if (rtn) {
					response.SetResponseInfo(ResultCode.USER_DISABLED);
				} else {
					for (SnsMappingVo snsVo : sysncSnsVo.getSnsInfo()) {

						if (StringUtils.isEmpty(snsVo.getSnsId()) || StringUtils.isEmpty(snsVo.getSnsType()) || StringUtils.isEmpty(snsVo.getCustWebId())) {
							// AP B2C 표준 로그 설정
							LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL, null, null, null,
									LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
							LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
							LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
							log.error("api.postSyncsnsassociated.Exception = requied param, empty parameter? true");
							LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
							response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
							break;
						} else if ((snsVo.getSnsType().equalsIgnoreCase("NA") || snsVo.getSnsType().equalsIgnoreCase("KA") || snsVo.getSnsType().equalsIgnoreCase("FB") || snsVo.getSnsType().equalsIgnoreCase("AP")) == false) {
							// AP B2C 표준 로그 설정
							LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_REQUIRED_VALUE_VALIDATION_FAIL, null, null, null,
									LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
							LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
							LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
							log.error("api.postSyncsnsassociated.Exception = check invalid sns type ? {}", snsVo.getSnsType());
							LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
							response.SetResponseInfo(ResultCode.SNS_NOT_FOUND_TYPE);
							break;
						} else {
							MappingIdSearchVo mappingIdSearchVo = new MappingIdSearchVo();
							mappingIdSearchVo.setLoginId(sysncSnsVo.getLoginId());
							mappingIdSearchVo.setSnsType(snsVo.getSnsType());
							mappingIdSearchVo.setSnsId(snsVo.getSnsId());
							// 같은 아이디로 이미 매핑되어 있는지
							rtn = this.mgmtApiService.isMappingExsist(mappingIdSearchVo);
							if (!rtn) {								
								// 다른 아이디로 이미 매핑되어 있는지
								rtn = this.mgmtApiService.isMappingOther(mappingIdSearchVo);
								if(rtn) {									
									response.SetResponseInfo(ResultCode.SNS_ALREADY_ID);
									return response;
								}
								
								try {
									
									log.debug("[sync sns associated] mapping vo : {}", StringUtil.printJson(mappingIdSearchVo));
									
									Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo = new Wso2SoapApiCreateAssociatedVo(mappingIdSearchVo.getSnsType(), mappingIdSearchVo.getSnsId());

									ResponseEntity<String> postResponse = this.wso2UpmSoapApiService.postAssociatedIdsByUsername(mappingIdSearchVo.getLoginId(), wso2SoapApiCreateAssociatedVo);
									if (postResponse.getStatusCode() == HttpStatus.OK) {
										response.SetResponseInfo(ResultCode.SUCCESS);
									} else {
										// AP B2C 표준 로그 설정
										LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
												LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
										LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
										LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
										log.error("api.syncsnsdisassociate.Exception = {}", postResponse.getBody());
										LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
										response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
										break;
									}
								} catch (Exception e) {
									// AP B2C 표준 로그 설정
									LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
											LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
									LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
									LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
									log.error("api.syncsnsassociated.post.Exception = {}", e.getMessage());
									LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
									response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
									break;
								}
							} else {
								response.SetResponseInfo(ResultCode.SNS_ALREADY_ASSO);
								break;
							}
						}
					} // end of loop, sysncSnsVo.getSnsInfos()
				}

			} else {
				response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
			}
		}

		log.debug("[sync sns associate] response code : {}", response.getResultCode());
		return response;
	}

	/* API 14 */
	// Delete Method 미사용 권장
	@ApiOperation(value = "SNS 매핑 내역 동기화", //
			notes = "옴니회원에서 SNS 연동 정보 해제 시, 동기화 처리 기능 API입니다.", //
			httpMethod = "DELETE", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class,
			hidden = true )
	@DeleteMapping("/syncsnsassociated")
	public ApiBaseResponse deleteSyncsnsassociated(

			@ApiParam(name = "sysncSnsVo", value = "SNS 매핑 내역 동기화 파라미터") final @RequestBody SysncSnsVo sysncSnsVo

	) throws ApiBusinessException {

		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.deleteSyncsnsassociated(response, sysncSnsVo);

		if (checkCommonValidation(response)) {
			boolean rtn = this.mgmtApiService.isUserExistByLoginId(sysncSnsVo.getLoginId()); //this.mgmtApiService.isUserExist( Integer.toString(sysncSnsVo.getIncsNo()) );
			if (rtn) {
				rtn = this.mgmtApiService.isDisabledUser(sysncSnsVo.getLoginId());
				if (rtn) {
					response.SetResponseInfo(ResultCode.USER_DISABLED);
				} else {
					for (SnsMappingVo snsVo : sysncSnsVo.getSnsInfo()) {
						if (StringUtils.isEmpty(snsVo.getSnsId()) || StringUtils.isEmpty(snsVo.getSnsType()) || StringUtils.isEmpty(snsVo.getCustWebId())) {
							response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
							break;
						} else if ((snsVo.getSnsType().equalsIgnoreCase("NA") || snsVo.getSnsType().equalsIgnoreCase("KA") || snsVo.getSnsType().equalsIgnoreCase("FB") || snsVo.getSnsType().equalsIgnoreCase("AP")) == false) {
							response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
							break;
						} else {
							MappingIdSearchVo mappingIdSearchVo = new MappingIdSearchVo();
							mappingIdSearchVo.setLoginId(sysncSnsVo.getLoginId());
							mappingIdSearchVo.setSnsType(snsVo.getSnsType());
							mappingIdSearchVo.setSnsId(snsVo.getSnsId());
							
							rtn = this.mgmtApiService.isMappingExsist(mappingIdSearchVo);
							if (rtn) {
								try {
									Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo = new Wso2SoapApiCreateAssociatedVo(mappingIdSearchVo.getSnsType(), snsVo.getSnsId());

									ResponseEntity<String> delResponse = this.wso2UpmSoapApiService.deleteAssociatedIdsByUsername(mappingIdSearchVo.getLoginId(), wso2SoapApiCreateAssociatedVo);
									if (delResponse.getStatusCode() == HttpStatus.OK) {
										response.SetResponseInfo(ResultCode.SUCCESS);
									} else {
										response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
										break;
									}
								} catch (Exception e) {
									log.error("api.syncsnsassociated.delete.Exception = {}", e.getMessage());
									response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
									break;
								}
							} else {
								response.SetResponseInfo(ResultCode.SNS_NOT_FOUND_ASSO_DISCONECT_FAIL);
								break;
							}
						}
					} // end of loop, sysncSnsVo.getSnsInfos()
				}
			} else {
				response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
			}
		}
		return response;
	}

	/* API 15 */
	@ApiOperation(value = "기존 JOIN-ON 비밀번호 변경 & 옴니회원 연계", //
			notes = "Join-On 에서 변경한 비밀 번호를 옴니회원의 비밀번호와 동기화 처리하는 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class)
	@PostMapping("/chgpwdjoinon")
	public ApiBaseResponse changePasswordJoinOn(

			@ApiParam(name = "chgPwdJoinOnVo", value = "비밀번호 변경 파라미터") final @RequestBody ChgPwdJoinOnVo chgPwdJoinOnVo

	) throws ApiBusinessException {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.changePasswordJoinOn(response, chgPwdJoinOnVo);

		if (checkCommonValidation(response)) {
			try {
				UserVo userVo = new UserVo();
				userVo.setIncsNo(Integer.toString(chgPwdJoinOnVo.getIncsNo()));
				userVo.setUserName(chgPwdJoinOnVo.getLoginId());
				
				log.debug("[chgpwdjoinon] exist user : {}", StringUtil.printJson(userVo));

				boolean rtn = this.mgmtApiService.isUserExistByLoginId(chgPwdJoinOnVo.getLoginId()); //this.mgmtApiService.isUserExist(userVo.getIncsNo());
				if (rtn) {
					rtn = this.mgmtApiService.isDisabledUser(chgPwdJoinOnVo.getLoginId());
					if (rtn) {
						response.SetResponseInfo(ResultCode.USER_DISABLED);
					} else {
						ResponseEntity<String> pwdResponse = this.wso2RusmSoapApiService.patchPasswordByUsername(chgPwdJoinOnVo.getLoginId(), "CR030");

						if (pwdResponse.getStatusCode() == HttpStatus.OK) {
							String encodedPw = SecurityUtil.convertSHA512EncodedPasswordToWso2Format(chgPwdJoinOnVo.getPassword());
							userVo.setUserPassword(encodedPw);
							this.mgmtApiService.updatePassword(userVo);
							
							String profile = this.systemInfo.getActiveProfiles()[0];
							profile = StringUtils.isEmpty(profile) ? "dev" : profile;
							profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
							
							// if("dev".equals(profile)) {
								ResponseEntity<String> sessionResponse = this.wso2Scim2RestApiService.removeSessionByUserName(userVo.getUserName());
								if(sessionResponse.getStatusCode() == HttpStatus.OK || sessionResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
									log.debug("[chgpwdjoinon] 사용자 세션 삭제 성공");
									log.info("▶▶▶▶▶ [chgpwdjoinon] chCd : {}, incsNo : {}, loginId : {}", chgPwdJoinOnVo.getChCd(), chgPwdJoinOnVo.getIncsNo(), chgPwdJoinOnVo.getLoginId());
								}
								else {
									log.error("[chgpwdjoinon] 사용자 세션 삭제 실패");
								}
							// }
							
							response.SetResponseInfo(ResultCode.SUCCESS);
						} else {
							// AP B2C 표준 로그 설정
							LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
									LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
							LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
							LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
							log.error("api.changePasswordJoinOn.Exception = {}", pwdResponse.getBody());
							LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
							response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						}
					}
				} else {
					response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
				}
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoErr(OmniStdLogConstants.API_SERVER_ERROR);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.changePasswordJoinOn.Exception = {}", e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}

		log.debug("[chg pwd joinon] response code : {}", response.getResultCode());
		return response;
	}

	/* API 16 */
	@ApiOperation(value = "경로 가입/탈퇴", //
			notes = "경로 가입/탈퇴 API입니다." //
					+ "Join-On 에서 가입 시, Join-On 에서 통합고객에 등록 처리됨에 따라서\r\n" //
					+ "처리된 내역을 통합고객 --> 옴니회원으로 동기화 처리", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class)
	@PostMapping("/createuserby030")
	public ApiBaseResponse createUserBy030(

			@ApiParam(name = "creUserBy030Vo", value = "경로 가입/탈퇴 파라미터") final @RequestBody CreateUserData creUserBy030Vo

	) throws ApiBusinessException {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.createUserBy030(response, creUserBy030Vo);

		if (checkCommonValidation(response)) {

			boolean isDisabled = this.mgmtApiService.isDisabledUser(creUserBy030Vo.getLoginId());
			if (isDisabled && !"C".equalsIgnoreCase(creUserBy030Vo.getJoinFlag())) {
				response.SetResponseInfo(ResultCode.USER_DISABLED);
			} else {
				try {
					
					log.debug("[createUserBy030Vo] create or withdraw user request : {}", StringUtil.printJson(creUserBy030Vo));
					
					UserVo userVo = new UserVo();
					userVo.setUserName(creUserBy030Vo.getLoginId());
					userVo.setIncsNo(Integer.toString(creUserBy030Vo.getIncsNo()));

					boolean rtn = this.mgmtApiService.isUserExistByLoginId(creUserBy030Vo.getLoginId()); //this.mgmtApiService.isUserExist(userVo.getIncsNo());

					if (creUserBy030Vo.getJoinFlag().equalsIgnoreCase("J")) {
						
						CheckResponse chkResponse = new Checker.Builder().checkType(CheckActor.Type.valueOf("IDCHAR")).checkValue(creUserBy030Vo.getLoginId()).build().check(); 
						
						if (!rtn) { // 사용자 없을때 가입 처리
							
							if(chkResponse.getStatus() == CheckActor.SUCCESS) { // ID Validation 체크 후 WSO2 API 호출
								Wso2RestApiCreateUserVo wso2RestApiCreateUserVo = new Wso2RestApiCreateUserVo( //
										creUserBy030Vo.getLoginId(), //
										"CR030", // WSO2 API에는 평문으로 줘야함 (임시값(CR030) 사용 후 DB업데이트 하는 방식으로 개발)
										creUserBy030Vo.getCn(), //
										Integer.toString(creUserBy030Vo.getIncsNo()), //
										/*null,*/ null, null);

								ResponseEntity<String> creResponse = this.wso2Scim2RestApiService.postUser(wso2RestApiCreateUserVo);
								if (creResponse.getStatusCode() == HttpStatus.CREATED) {
									String encodedPw = SecurityUtil.convertSHA512EncodedPasswordToWso2Format(creUserBy030Vo.getPassword()); // SecurityUtil.base64(creUserBy030Vo.getPassword().getBytes());
									userVo.setUserPassword(encodedPw);
									rtn = this.mgmtApiService.updatePassword(userVo);

									response.SetResponseInfo(ResultCode.SUCCESS);
									/*
									 * if(rtn) { response.SetResponseInfo(ResultCode.SUCCESS); } else { // encoded password update fail }
									 */
								} else {
									// AP B2C 표준 로그 설정
									LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
											LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
									LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
									LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
									log.error("api.createUserBy030Vo.Exception = {}", creResponse.getBody());
									LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
									response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
								}
							} else { // 체크 실패
								if (chkResponse.getCode().equalsIgnoreCase(OmniConstants.ID_SIZE_FAIL)) {
									response.SetResponseInfo(ResultCode.ID_INVALID_LENGTH);
									log.debug("[createUserBy030Vo] Join Error Message : {}", response.getMessage());
								} else {
									response.SetResponseInfo(ResultCode.ID_VALIDCHECK_FAIL);
									log.debug("[createUserBy030Vo] Join Error Message : {}", response.getMessage());
								}
							}
							
						} else {
							response.SetResponseInfo(ResultCode.USER_ALREADY_EXIST);
							log.debug("[createUserBy030Vo] Join Error Message : {}", response.getMessage());
						}
						
						log.debug("[createUserBy030Vo] Join response code : {}", response.getResultCode());
					} else if (creUserBy030Vo.getJoinFlag().equalsIgnoreCase("L")) {
						if (rtn) { // 사용자 있을때 탈퇴 처리
							ResponseEntity<String> delResponse = this.wso2RusmSoapApiService.postDisableByUsername(creUserBy030Vo.getLoginId());
							// ResponseEntity<String> delResponse = this.wso2Scim2RestApiService.deleteUserByUsername(creUserBy030Vo.getLoginId());
							if (delResponse.getStatusCode() == HttpStatus.OK) {
								response.SetResponseInfo(ResultCode.SUCCESS);
								
								// 매핑 정보 삭제
								boolean rtnsnsmapping = this.mgmtApiService.deleteSnsMapping(Integer.toString(creUserBy030Vo.getIncsNo()));
								log.debug("[disasbledUserBy030Vo] delete sns mapping : {}", rtnsnsmapping);
								
								// 카카오싱크 언링크
								BaseResponse snsResponse = this.mgmtApiService.doSnsUnlink(creUserBy030Vo.getIncsNo());
								log.debug("[disasbledUserBy030Vo] unlink sns response : {}", StringUtil.printJson(snsResponse));
								
							} else {
								// AP B2C 표준 로그 설정
								LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
										LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
								LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
								LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
								log.error("api.disasbledUserBy030Vo.Exception = {}", delResponse.getBody());
								LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
								response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
							}
						} else {
							response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
							log.debug("[disasbledUserBy030Vo] Join Error Message : {}", response.getMessage());
						}
						
						log.debug("[disasbledUserBy030Vo] withdraw response code : {}", response.getResultCode());
					}
					// 망취소 요청
					else if (creUserBy030Vo.getJoinFlag().equalsIgnoreCase("C")) {
						if (rtn) { // 사용자 있을 경우 삭제
							
							// 카카오싱크 언링크
							BaseResponse snsResponse = this.mgmtApiService.doSnsUnlink(creUserBy030Vo.getIncsNo());
							log.debug("[creUserBy030Vo] unlink sns response : {}", StringUtil.printJson(snsResponse));
							
							// 사용자 정보 삭제
							ResponseEntity<String> delResponse = this.wso2Scim2RestApiService.deleteUserByUsername(userVo.getUserName());
							if(delResponse.getStatusCode() == HttpStatus.OK || delResponse.getStatusCode() == HttpStatus.NO_CONTENT ) {
								response.SetResponseInfo(ResultCode.SUCCESS);
							}
							else {
								// AP B2C 표준 로그 설정
								LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
										LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
								LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
								LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
								log.error("api.cancelUserBy030Vo.Exception = {}", delResponse.getBody());
								LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
								response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
							}
						}
						else {
							response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
							log.debug("[cancelUserBy030Vo] user cancel Message : {}", response.getMessage());
						}
						log.debug("[cancelUserBy030Vo] user cancel response code : {}", response.getResultCode());
					}
				} catch (Exception e) {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
							LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					log.error("[JoinFlag = {}] api.creUserBy030Vo.Exception = {}", creUserBy030Vo.getJoinFlag(), e.getMessage());
					LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
					response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				}
			}
		}

		return response;
	}

	/* API 17 */
	@ApiOperation(value = "비밀번호 초기화", //
			notes = "비밀번호 초기화 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class)
	@PostMapping("/initializepassword")
	public ApiBaseResponse initializePassword(

			@ApiParam(name = "initPasswordData", value = "비밀번호 초기화 파라미터") final @RequestBody InitPasswordData initPasswordData

	) throws ApiBusinessException {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.initializePassword(response, initPasswordData);

		if (checkCommonValidation(response)) {
			log.debug("[initializepassword] isOmniUser = {}, chCd = {}", initPasswordData.getChCd().equals("030"), initPasswordData.getChCd());
			
			int updateCnt = 0;
			
			try {
				if(initPasswordData.getLoginId().equals(initPasswordData.getPassword())) {
					response.SetResponseInfo(ResultCode.PWD_SAME_ID);
					return response;
				}
								
				UserVo userVo = new UserVo();
				userVo.setUserName(initPasswordData.getLoginId());
				userVo.setIncsNo(Integer.toString(initPasswordData.getIncsNo()));
				
				UserVo user = this.mgmtApiService.getUser(userVo);
				if(user != null) {	// 옴니 유저 일때
					
					//TODO wso2 password 자리수 체크 min 5 ~ max 30 작업 예정(단위 테스트는 swgger) 2022.02.14 kyungseo
					if(Objects.nonNull(initPasswordData.getPassword())) {
					
						if(org.apache.commons.lang3.StringUtils.length(initPasswordData.getPassword()) < 5) {
							response.SetResponseInfo(ResultCode.PWD_WRONG);
							return response;
							
						}else if(org.apache.commons.lang3.StringUtils.length(initPasswordData.getPassword()) > 30) {
							response.SetResponseInfo(ResultCode.PWD_WRONG);
							return response;
							
						}
					}
					
					// 탈퇴여부 확인 
					if (this.mgmtApiService.isDisabledUser(user.getUserName())) {
						response.SetResponseInfo(ResultCode.USER_DISABLED);
						return response;
					}
					
					// 잠김/휴면 상태면 상태해제
					if (this.mgmtApiService.isUsableUser(user.getUserName()) == false) {
						ResponseEntity<String> unlockResponse = this.wso2RusmSoapApiService.postUnLockByUsername(user.getUserName());
						if (unlockResponse.getStatusCode() != HttpStatus.OK) {
							response.SetResponseInfo(ResultCode.USER_STATUS_CHANGE_FAIL);
							// AP B2C 표준 로그 설정
							LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
									LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
							LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
							LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
							log.error("api.initializepassword.User Status Change Fail.Exception = {}", unlockResponse.getBody());
							LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
							return response;
						}
					}
					
					
				
					// password update
					ResponseEntity<String> pwdResponse = this.wso2RusmSoapApiService.patchPasswordByUsername(initPasswordData.getLoginId(), initPasswordData.getPassword());
					if (pwdResponse.getStatusCode() == HttpStatus.OK) {
						if(initPasswordData.getMustchange().equalsIgnoreCase("Y")) {
							boolean resetRst = this.mgmtApiService.updatePasswordReset(initPasswordData.getLoginId());
							log.debug("[initializepassword] omni wso2 update password result : {}", resetRst);
						}
						
						updateCnt++;
					} else {
						// AP B2C 표준 로그 설정
						LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
								LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
						LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
						LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
						log.error("api.initializepassword.Password Update Fail.Exception = {}", pwdResponse.getBody());
						LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
						response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
					}
				}
				
				// 채널코드+통합고객번호로 경로회원 조회
				// 경로회원이면서 전환하지 않은 계정은 비밀번호 업데이트
				ChUserVo tempVo = new ChUserVo();
				tempVo.setChCd(initPasswordData.getChCd());
				tempVo.setIncsNo(Integer.toString(initPasswordData.getIncsNo()));
				tempVo.setWebId(initPasswordData.getLoginId());
				
				ChUserVo chUserVo = this.mgmtApiService.getChUser(tempVo);
				if(chUserVo != null && chUserVo.getSwtYN().equalsIgnoreCase("N")) {
					chUserVo.setPassword( SecurityUtil.getEncodedWso2Password(initPasswordData.getPassword()) );
					int rtn = this.mgmtApiService.updateChUserPassword(chUserVo);
					if(rtn > 0) {
						log.debug("◆◆◆◆◆◆ [initializepassword] channel user update password result : {}", rtn);
						updateCnt++;
					}
				}
				
				if(updateCnt > 0) {
					response.SetResponseInfo(ResultCode.SUCCESS);
					log.debug("[initializepassword] user update password count : {}", updateCnt);
					
					// 고객통합 비밀번호 변경
					ChangePasswordData changePasswordData = new ChangePasswordData();
					changePasswordData.setIncsNo(initPasswordData.getIncsNo());
					changePasswordData.setChCd(initPasswordData.getChCd());
					changePasswordData.setLoginId(initPasswordData.getLoginId());
					changePasswordData.setChangePassword(initPasswordData.getPassword());
					
					boolean success = this.mgmtApiService.changeIntegratedUserPassword(changePasswordData);
					log.debug("[initializepassword] 고객통합 비밀번호 변경 동기화 result : {}", success);
					
					if(user != null) {	// 옴니 유저 일때만 뷰티포인트 비밀번호 변경 API 호출
						// 뷰티포인트 비밀번호 변경
						BpEditUserRequest bpEditUserRequest = new BpEditUserRequest();
						bpEditUserRequest.setParamSiteCd("OCP");
						bpEditUserRequest.setCstmId(initPasswordData.getLoginId());
						bpEditUserRequest.setPswd( SecurityUtil.getEncodedSHA512Password(initPasswordData.getPassword()) );
						
						ApiBaseResponse bpReseponse = this.mgmtApiService.changeBeautyPointUserInfo(bpEditUserRequest);
						log.debug("[initializepassword] 뷰티포인트 비밀번호 동기화 result : {}", bpReseponse.getMessage());	
					}
					
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					
					// if("dev".equals(profile)) {
						ResponseEntity<String> sessionResponse = this.wso2Scim2RestApiService.removeSessionByUserName(user.getUserName());
						if(sessionResponse.getStatusCode() == HttpStatus.OK || sessionResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
							log.debug("[initializepassword] 사용자 세션 삭제 성공");
							log.info("▶▶▶▶▶ [initializepassword] chCd : {}, incsNo : {}, loginId : {}", initPasswordData.getChCd(), initPasswordData.getIncsNo(), initPasswordData.getLoginId());
						}
						else {
							log.error("[initializepassword] 사용자 세션 삭제 실패");
						}
					// }
					
				} else if(response.getMessage() == null) {		// 메시지 없으면 셋팅 안된것임
					response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
					log.warn("[initializepassword] {}", response.getMessage());
				}
				
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.initializepassword.Exception = {} {}", response.getMessage(), e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}

		return response;
	}
	
	/* API 17 - 1*/
	@ApiOperation(value = "비밀번호 초기화", //
			notes = "비밀번호 초기화 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class)
	@PostMapping("/initpasswordcurrentpassword")
	public ApiBaseResponse initpasswordcurrentpassword(

			@ApiParam(name = "initPasswordData", value = "비밀번호 초기화 파라미터") final @RequestBody InitPasswordData initPasswordData

	) throws ApiBusinessException {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.initializePassword(response, initPasswordData);

		if (checkCommonValidation(response)) {
			log.debug("[initpasswordcurrentpassword] isOmniUser = {}, chCd = {}", initPasswordData.getChCd().equals("030"), initPasswordData.getChCd());
			
			int updateCnt = 0;
			
			try {
				if(initPasswordData.getLoginId().equals(initPasswordData.getPassword())) {
					response.SetResponseInfo(ResultCode.PWD_SAME_ID);
					return response;
				}
								
				UserVo userVo = new UserVo();
				userVo.setUserName(initPasswordData.getLoginId());
				userVo.setIncsNo(Integer.toString(initPasswordData.getIncsNo()));
				
				UserVo user = this.mgmtApiService.getUser(userVo);
				if(user != null) {	// 옴니 유저 일때
					
					// 탈퇴여부 확인 
					if (this.mgmtApiService.isDisabledUser(user.getUserName())) {
						response.SetResponseInfo(ResultCode.USER_DISABLED);
						return response;
					}
					
					// 잠김/휴면 상태면 상태해제
					if (this.mgmtApiService.isUsableUser(user.getUserName()) == false) {
						ResponseEntity<String> unlockResponse = this.wso2RusmSoapApiService.postUnLockByUsername(user.getUserName());
						if (unlockResponse.getStatusCode() != HttpStatus.OK) {
							response.SetResponseInfo(ResultCode.USER_STATUS_CHANGE_FAIL);
							// AP B2C 표준 로그 설정
							LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
									LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
							LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
							LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
							log.error("api.initializepassword.User Status Change Fail.Exception = {}", unlockResponse.getBody());
							LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
							return response;
						}
					}
					
					
				
					// password update - 해당 API에서 Password 는 기존 Password를 DB에 업데이트 하기 때문에 오류 방지를 위해 고객통합번호로 API 호출
					ResponseEntity<String> pwdResponse = this.wso2RusmSoapApiService.patchPasswordByUsername(initPasswordData.getLoginId(), Integer.toString(initPasswordData.getIncsNo()));
					if (pwdResponse.getStatusCode() == HttpStatus.OK) {
						if(initPasswordData.getMustchange().equalsIgnoreCase("Y")) {
							boolean resetRst = this.mgmtApiService.updatePasswordReset(initPasswordData.getLoginId());
							log.debug("[initializepassword] omni wso2 update password result : {}", resetRst);
						}
						
						updateCnt++;
					} else {
						// AP B2C 표준 로그 설정
						LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
								LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
						LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
						LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
						log.error("api.initializepassword.Password Update Fail.Exception = {}", pwdResponse.getBody());
						LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
						response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
					}
				}
				
				if(updateCnt > 0) {
					response.SetResponseInfo(ResultCode.SUCCESS);
					log.debug("[initpasswordcurrentpassword] user update password count : {}", updateCnt);
					
					if(user != null) {	// 옴니 유저 일때만 뷰티포인트 비밀번호 변경 API 호출
						// 뷰티포인트 비밀번호 변경
						BpEditUserRequest bpEditUserRequest = new BpEditUserRequest();
						bpEditUserRequest.setParamSiteCd("OCP");
						bpEditUserRequest.setCstmId(initPasswordData.getLoginId());
						bpEditUserRequest.setPswd( SecurityUtil.decodeBase64(initPasswordData.getPassword()) );
						
						ApiBaseResponse bpReseponse = this.mgmtApiService.changeBeautyPointUserInfo(bpEditUserRequest);
						log.debug("[initpasswordcurrentpassword] 뷰티포인트 비밀번호 동기화 result : {}", bpReseponse.getMessage());	
					}
					
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					
					// if("dev".equals(profile)) {
						ResponseEntity<String> sessionResponse = this.wso2Scim2RestApiService.removeSessionByUserName(user.getUserName());
						if(sessionResponse.getStatusCode() == HttpStatus.OK || sessionResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
							log.debug("[initpasswordcurrentpassword] 사용자 세션 삭제 성공");
							log.info("▶▶▶▶▶ [initpasswordcurrentpassword] chCd : {}, incsNo : {}, loginId : {}", initPasswordData.getChCd(), initPasswordData.getIncsNo(), initPasswordData.getLoginId());
						}
						else {
							log.error("[initpasswordcurrentpassword] 사용자 세션 삭제 실패");
						}
					// }
					
				} else if(response.getMessage() == null) {		// 메시지 없으면 셋팅 안된것임
					response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
					log.warn("[initpasswordcurrentpassword] {}", response.getMessage());
				}
				
			} catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.initpasswordcurrentpassword.Exception = {} {}", response.getMessage(), e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}

		return response;
	}
	
	/* API 19 */
	@ApiOperation(value = "WebID 변경", //
			notes = "WebID 변경 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class)
	@PostMapping("/changewebid")
	public ApiBaseResponse changeWebId(

			@ApiParam(name = "changeWebIdData", value = "WebID 변경 파라미터") final @RequestBody ChangeWebIdData changeWebIdData

	) throws ApiBusinessException {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.changeWebId(response, changeWebIdData);

		if (checkCommonValidation(response)) {
			log.debug("[changeWebId] loginId = {}, loginIdNew = {}", changeWebIdData.getLoginId(), changeWebIdData.getLoginIdNew());
			
			try {
				// 사용 중인 아이디 검증
				boolean isUseId = this.mgmtApiService.isUserExistByLoginId(changeWebIdData.getLoginIdNew());
				if(isUseId) {
					response.SetResponseInfo(ResultCode.ID_ALREADY_EXIST);
					log.debug("[changeWebId] Check Id Message : {}", response.getMessage());
					return response;
				}
				
				UserVo userVo = new UserVo();
				userVo.setUserName(changeWebIdData.getLoginId());
				userVo.setIncsNo(Integer.toString(changeWebIdData.getIncsNo()));
				UserVo newUserVo = this.mgmtApiService.getUserByIncsNoAndUserName(userVo);
				
				// 기존 사용자 아이디 검증
				if(newUserVo == null) {
					response.SetResponseInfo(ResultCode.ID_INVALID);
					log.debug("[changeWebId] Check Id Message : {}", response.getMessage());
					return response;
				}
				
				// 변경할 ID Validation 체크
				CheckResponse chkResponse = new Checker.Builder().checkType(CheckActor.Type.valueOf("ID")).checkValue(changeWebIdData.getLoginIdNew()).build().check();
				if(chkResponse.getStatus() != CheckActor.SUCCESS) {
					if (chkResponse.getCode().equalsIgnoreCase(OmniConstants.ID_SIZE_FAIL)) {
						response.SetResponseInfo(ResultCode.ID_INVALID_LENGTH);
						log.debug("[changeWebId] Check Id Message : {}", response.getMessage());
					} else {
						response.SetResponseInfo(ResultCode.ID_VALIDCHECK_FAIL);
						log.debug("[changeWebId] Check Id  Message : {}", response.getMessage());
					}
					return response;
				}
				
				// cn attr이 없을 경우 고객통합을 조회하여 매핑
				if(StringUtils.isEmpty(newUserVo.getCn())) {
					log.debug("[changeWebId] cn is empty");
					String custCn = mgmtApiService.getCustUser(changeWebIdData);
					if(StringUtils.isEmpty(custCn)) {
						log.debug("[changeWebId] cust cn is empty");
					}
					else {
						newUserVo.setCn(custCn);
					}
				}
				
				// 기존 사용자 탈퇴 처리 - wso2에서 사용자 아이디를 key 값으로  사용자를 관리하기 때문에 사용자 아이디를 변경하기 위해서는 새로운 사용자를 생성하고 기존 사용자의 정보를 주입하는 방식으로 진행
				ResponseEntity<String> disableResponse = this.wso2RusmSoapApiService.postDisableByUsername(changeWebIdData.getLoginId());
				if (disableResponse.getStatusCode() == HttpStatus.OK) {
					
						newUserVo.setUserName(changeWebIdData.getLoginIdNew()); // 변경할 아이디로 사용자 생성
						Wso2RestApiCreateUserVo wso2RestApiCreateUserVo = new Wso2RestApiCreateUserVo( //
								newUserVo.getUserName(), //
								"CR030", // WSO2 API에는 평문으로 줘야함 (임시값(CR030) 사용 후 DB업데이트 하는 방식으로 개발)
								newUserVo.getCn(), //
								Integer.toString(changeWebIdData.getIncsNo()), //
								/*null,*/ null, null);

						// 변경할 WebId로 사용자 생성
						ResponseEntity<String> creResponse = this.wso2Scim2RestApiService.postUser(wso2RestApiCreateUserVo);
						if (creResponse.getStatusCode() == HttpStatus.CREATED) {
							// 임시 비밀번호를 기존비밀번호로 업데이트
							boolean pwdResponse = this.mgmtApiService.updatePassword(newUserVo);
							
							if (pwdResponse) {
								
								ChkSnsVo chkSnsVo = new ChkSnsVo();
								chkSnsVo.setIncsNo(changeWebIdData.getIncsNo());
								chkSnsVo.setLoginId(changeWebIdData.getLoginId());

								
								// SNS 매핑 상태 확인 후, 매핑되어 있으면 변경 후 기존 사용자 삭제 / 매핑되어 있지 않으면 기존 사용자 삭제  
								SnsAssResponse snsAssResponse = this.checkSnsAssociated(chkSnsVo);
								log.debug("[changeWebId] snsAssResponse = {}", snsAssResponse.toString());
								if(ResultCode.SUCCESS.getCode().equals(snsAssResponse.getResultCode())) {
									
									// sns 매핑 변경 
									boolean rtn = this.mgmtApiService.updateSnsUserName(changeWebIdData.getLoginId(), changeWebIdData.getLoginIdNew());
									
									if(rtn) {
										ResponseEntity<String> delResponse = this.wso2Scim2RestApiService.deleteUserByUsername(changeWebIdData.getLoginId());
										if(delResponse.getStatusCode() == HttpStatus.OK || delResponse.getStatusCode() == HttpStatus.NO_CONTENT ) {
											response.SetResponseInfo(ResultCode.SUCCESS);
											log.debug("[changeWebId] result = {}", response.getMessage());
										}
										else {
											// AP B2C 표준 로그 설정
											LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
													LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
											LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
											LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
											log.error("api.changeWebId.delete Error Message : {}", delResponse.getBody());
											LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
											response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
										}
									}
									else {
										// AP B2C 표준 로그 설정
										LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
												LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
										LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
										LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
										log.debug("api.changeWebId.SNS Update Error Message : {}", snsAssResponse.getMessage());
										LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
										response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
									}
								}
								else if(ResultCode.SNS_NOT_FOUND_ASSO.getCode().equals(snsAssResponse.getResultCode())) {
									ResponseEntity<String> delResponse = this.wso2Scim2RestApiService.deleteUserByUsername(changeWebIdData.getLoginId());
									if(delResponse.getStatusCode() == HttpStatus.OK || delResponse.getStatusCode() == HttpStatus.NO_CONTENT ) {
										response.SetResponseInfo(ResultCode.SUCCESS);
										log.debug("[changeWebId] result = {}", response.getMessage());
									}
									else {
										// AP B2C 표준 로그 설정
										LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
												LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
										LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
										LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
										log.error("api.changeWebId.delete Error Message : {}", delResponse.getBody());
										LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
										response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
									}
								}
								else {
									// AP B2C 표준 로그 설정
									LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
											LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
									LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
									LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
									log.error("api.changeWebId.SNS Error Error Message : {}", snsAssResponse.getMessage());
									LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
									response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
								}
							}
							else {
								response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
								log.debug("[changeWebId] PW Error Message : {}", response.getMessage());
							}
						} else {
							// AP B2C 표준 로그 설정
							LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
									LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
							LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
							LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
							log.error("api.changeWebId.User Error Message : {}", creResponse.getBody());
							LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
							response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						}
				}
				else {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
							LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					log.error("api.changeWebId.User Error Message : {}", disableResponse.getBody());
					LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
					response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				}
			}catch (Exception e) {
				// AP B2C 표준 로그 설정
				LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
						LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
				LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
				LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
				log.error("api.changewebid.Exception = {}", e.getMessage());
				LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
			
		}

		return response;
	}
	

//-----------------------------------------------------------------------------------
//		***		내부 사용 API	  ***
//-----------------------------------------------------------------------------------

	/* API 18 */
	@ApiOperation(value = "회원가입", //
			notes = "옴니 통합 회원가입 API입니다.", httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class, //
			hidden = true)
	@PostMapping("/createuser")
	public ApiBaseResponse createUser(

			@ApiParam(name = "creUserVo", value = "회원가입") final @RequestBody CreateUserData creUserVo

	) throws ApiBusinessException {

		ApiBaseResponse response = new ApiBaseResponse();
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID)); // response.setTrxUuid(WebUtil.getHeader(OmniConstants.TRX_UUID));

		/*
		 * if (StringUtils.isEmpty(creUserBy030Vo.getChCd())) { response.setMessage(this.messageSource.getMessage("api.param.empty.chCd", null,
		 * LocaleUtil.getLocale())); } else if (StringUtil.checkParameter(Integer.toString(creUserBy030Vo.getIncsNo()), "^[0-9]{9}$") == false ||
		 * creUserBy030Vo.getIncsNo() < 1) { response.setMessage(this.messageSource.getMessage("api.param.empty.incsNo", null,
		 * LocaleUtil.getLocale())); } else if (StringUtils.isEmpty(creUserBy030Vo.getPassword())) {
		 * response.setMessage(this.messageSource.getMessage("api.param.empty.password", null, LocaleUtil.getLocale())); } else {
		 */
		if (creUserVo.getIncsNo() < 1 || StringUtils.isEmpty(creUserVo.getCn())) {
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
		} else {
			boolean rtn = this.mgmtApiService.isUserExistByLoginId(creUserVo.getLoginId()); //this.mgmtApiService.isUserExist( Integer.toString(creUserVo.getIncsNo()) );

			log.debug("omni user[{}] exist ? {}", creUserVo.getIncsNo(), rtn);
			
			try {
				if (creUserVo.getJoinFlag().equalsIgnoreCase("J")) {
					if (!rtn) { // 사용자 없을때 가입 처리
						Wso2RestApiCreateUserVo wso2RestApiCreateUserVo = new Wso2RestApiCreateUserVo( //
								creUserVo.getLoginId(), //
								creUserVo.getPassword(), //
								creUserVo.getCn(), //
								Integer.toString(creUserVo.getIncsNo()), //
								/*null,*/ null, null);

						ResponseEntity<String> creResponse = this.wso2Scim2RestApiService.postUser(wso2RestApiCreateUserVo);
						if (creResponse.getStatusCode() == HttpStatus.CREATED) {
							response.SetResponseInfo(ResultCode.SUCCESS);
						} else {
							response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						}
					} else {
						response.SetResponseInfo(ResultCode.USER_ALREADY_EXIST);
					}
				} else if (creUserVo.getJoinFlag().equalsIgnoreCase("L")) {
					if (rtn) { // 사용자 있을때 탈퇴 처리
						ResponseEntity<String> delResponse = this.wso2Scim2RestApiService.deleteUserByUsername(creUserVo.getLoginId());
						if (delResponse.getStatusCode() == HttpStatus.OK) {
							response.SetResponseInfo(ResultCode.SUCCESS);
						} else {
							response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						}
					} else {
						response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
					}
				}
			} catch (Exception e) {
				log.error("[JoinFlag = {}] api.chgpwdjoinon.Exception = {}", creUserVo.getJoinFlag(), e.getMessage());
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}
		// }

		log.debug("omni create user response : {}", StringUtil.printJson(response));
		
		return response;
	}

	@ApiOperation(value = "옴니 회원 disable", //
			notes = "옴니 통합 disable API 입니다.", httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class, //
			hidden = true)
	@PostMapping("/disableuser")
	public ApiBaseResponse disableUser(@ApiParam(name = "loginId", value = "회원로그인아이디") final @RequestBody String loginId) {
		ApiBaseResponse response = new ApiBaseResponse();
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));

		try {
			ResponseEntity<String> disableResponse = this.wso2RusmSoapApiService.postDisableByUsername(loginId);

			if (disableResponse.getStatusCode() == HttpStatus.OK) {
				response.SetResponseInfo(ResultCode.SUCCESS);
			} else {
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		} catch (Exception e) {
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
		}
		return response;
	}
	
	@ApiOperation(value = "옴니 회원 enable", //
			notes = "옴니 통합 enable API 입니다.", httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class, //
			hidden = true)
	@PostMapping("/enableuser")
	public ApiBaseResponse enableUser(@ApiParam(name = "loginId", value = "회원로그인아이디") final @RequestBody String loginId) {
		ApiBaseResponse response = new ApiBaseResponse();
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));

		try {
			ResponseEntity<String> disableResponse = this.wso2RusmSoapApiService.postEnableByUsername(loginId);

			if (disableResponse.getStatusCode() == HttpStatus.OK) {
				response.SetResponseInfo(ResultCode.SUCCESS);
			} else {
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		} catch (Exception e) {
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
		}
		return response;
	}

	@ApiOperation(value = "사용자-SNS Mapping API", //
			notes = "사용자-SNS Mapping API 입니다.", httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class, //
			hidden = true)
	@PostMapping("/snsassociate")
	public ApiBaseResponse snsAssociate(@ApiParam(name = "snsParam", value = "sns정보") final @RequestBody SnsParam snsParam) {
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.snsAssociate(response, snsParam);

		log.debug("▶▶▶▶▶▶ [SNS Associate] Request to sns param : {}", StringUtil.printJson(snsParam));
		
		if (checkCommonValidation(response) && !ResultCode.SNS_INVALID_TYPE.getCode().equals(response.getResultCode())) {
			try {
				// 유효한 사용자인지 체크
				UserVo userVo = new UserVo();
				userVo.setIncsNo(snsParam.getIncsNo());
				userVo.setUserName(snsParam.getLoginId());
				
				UserVo user = this.mgmtApiService.getUser(userVo);
				if (user == null) {
					response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
					return response;
				}

				// 탈퇴 사용자 체크
				boolean rtn = this.mgmtApiService.isDisabledUser(snsParam.getLoginId());
				if (rtn) {
					response.SetResponseInfo(ResultCode.USER_DISABLED);
					return response;
				}

				// 깉은 아이디로 이미 매핑되어 있는지
				MappingIdSearchVo mappingIdSearchVo = new MappingIdSearchVo();
				mappingIdSearchVo.setLoginId(snsParam.getLoginId());
				mappingIdSearchVo.setSnsId(snsParam.getSnsId());
				mappingIdSearchVo.setSnsType(snsParam.getSnsType());

				rtn = this.mgmtApiService.isMappingExsist(mappingIdSearchVo);
				if (!rtn) {
					
					// 다른 아이디로 이미 매핑되어 있는지
					rtn = this.mgmtApiService.isMappingOther(mappingIdSearchVo);
					if(rtn) {						
						response.SetResponseInfo(ResultCode.SNS_ALREADY_ID);
						return response;
					}
					
					log.debug("▶▶▶▶▶▶ [SNS Associate] Request to WSO2, id : {}, type : {}", snsParam.getSnsId(), snsParam.getSnsType());
					
					Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo = new Wso2SoapApiCreateAssociatedVo(snsParam.getSnsType(), snsParam.getSnsId());

					ResponseEntity<String> assResponse = this.wso2UpmSoapApiService.postAssociatedIdsByUsername(snsParam.getLoginId(), wso2SoapApiCreateAssociatedVo);
					log.debug("▶▶▶▶▶▶ [SNS Associate] Response from WSO2, status : {}", assResponse.getStatusCode());
					
					if (assResponse.getStatusCode() == HttpStatus.OK) {
						response.SetResponseInfo(ResultCode.SUCCESS);
						
						// join-on mapping
						String profile = this.systemInfo.getActiveProfiles()[0];
						profile = StringUtils.isEmpty(profile) ? "dev" : profile;
						profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
						
						SnsConnectRequest snsConnectRequest = new SnsConnectRequest();
						snsConnectRequest.setConnectYN("Y");
						snsConnectRequest.setUcstmid(snsParam.getIncsNo());
						snsConnectRequest.setCstmid(mappingIdSearchVo.getLoginId());
						snsConnectRequest.setSnsAuthkey(snsParam.getSnsId());
						snsConnectRequest.setSnsType(mappingIdSearchVo.getSnsType());
						log.debug("▶▶▶▶▶▶ [Join-On SNS Post Sync] snsConnectRequest = {}", StringUtil.printJson(snsConnectRequest));
						
						ApiBaseResponse joinOnSyncRes = new ApiBaseResponse();
						joinOnSyncRes = this.snsApiService.joinOnSnsLinker(joinOnSyncRes, snsConnectRequest);
						log.debug("▶▶▶▶▶▶ [Join-On SNS Post Sync] Res = {}", joinOnSyncRes);
						
					} else {
						response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
					}
				} else {
					response.SetResponseInfo(ResultCode.SNS_ALREADY_ASSO);
				}

			} catch (Exception e) {
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}

		return response;
	}

	@ApiOperation(value = "사용자-SNS UnLink API", //
			notes = "사용자-SNS UnLink API 입니다.", httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class, //
			hidden = true)
	@PostMapping("/snsdisconnect")
	public ApiBaseResponse snsDisconnect(@ApiParam(name = "snsParam", value = "sns정보") final @RequestBody SnsParam snsParam) {
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.snsDisconnect(response, snsParam);

		if (checkCommonValidation(response) && !ResultCode.SNS_INVALID_TYPE.getCode().equals(response.getResultCode())) {
			try {
				// 유효한 사용자인지 체크
				UserVo userVo = new UserVo();
				userVo.setIncsNo(snsParam.getIncsNo());
				userVo.setUserName(snsParam.getLoginId());
				
				UserVo user = this.mgmtApiService.getUser(userVo);
				if (user == null) {
					response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
					return response;
				}

				// 탈퇴 사용자 체크
				boolean rtn = this.mgmtApiService.isDisabledUser(snsParam.getLoginId());
				if (rtn) {
					response.SetResponseInfo(ResultCode.USER_DISABLED);
					return response;
				}

				SnsUnlinkVo snsUnlinkVo = new SnsUnlinkVo();
				snsUnlinkVo.setLoginId(snsParam.getLoginId());
				snsUnlinkVo.setSnsType(snsParam.getSnsType());

				List<String> idpUserIds = this.mgmtApiService.getAssociatedSnsId(snsUnlinkVo);
				if (idpUserIds != null && idpUserIds.size() > 0) {
					for(String snsId : idpUserIds) {
						Wso2SoapApiCreateAssociatedVo wso2SoapApiCreateAssociatedVo = new Wso2SoapApiCreateAssociatedVo(snsUnlinkVo.getSnsType(), snsId);
						ResponseEntity<String> unlinkResponse = wso2UpmSoapApiService.deleteAssociatedIdsByUsername(snsUnlinkVo.getLoginId(), wso2SoapApiCreateAssociatedVo);

						if (unlinkResponse.getStatusCode() == HttpStatus.OK) {
							response.SetResponseInfo(ResultCode.SUCCESS);
							
							// join-on 해제
							String profile = this.systemInfo.getActiveProfiles()[0];
							profile = StringUtils.isEmpty(profile) ? "dev" : profile;
							profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
							
							SnsConnectRequest snsConnectRequest = new SnsConnectRequest();
							snsConnectRequest.setConnectYN("N");
							snsConnectRequest.setUcstmid(snsParam.getIncsNo());
							snsConnectRequest.setCstmid(snsUnlinkVo.getLoginId());
							snsConnectRequest.setSnsAuthkey(snsId);
							snsConnectRequest.setSnsType(snsUnlinkVo.getSnsType());
							log.debug("▶▶▶▶▶▶ [Join-On SNS Delete Sync] snsConnectRequest = {}", StringUtil.printJson(snsConnectRequest));
							
							ApiBaseResponse joinOnSyncRes = new ApiBaseResponse();
							joinOnSyncRes = this.snsApiService.joinOnSnsLinker(joinOnSyncRes, snsConnectRequest);
							log.debug("▶▶▶▶▶▶ [Join-On SNS Delete Sync] Res = {}", joinOnSyncRes);
							
						} else {
							response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						}
					}
				} else {
					response.SetResponseInfo(ResultCode.SNS_NOT_FOUND_ASSO);
				}
			} catch (Exception e) {
				log.error("snsdisconnect.exception = {}", e.getMessage());
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}

		return response;
	}

	@ApiOperation(value = "채널 약관 동의여부 확인 API", //
			notes = "채널 약관 동의여부 확인 API 입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = TermsResponse.class, //
			hidden = true)
	@PostMapping("/terms")
	public TermsResponse checkTerms( //
			@ApiParam(name = "chTermsRequest", value = "채널약관동의 체크 파라미터") //
			final @RequestBody TermsRequest chTermsRequest) throws UnsupportedEncodingException {
		
		StopWatch stopWatch = new StopWatch("필수 약관 동의여부 확인 API");
		stopWatch.start("필수 약관 동의여부 체크");

		TermsResponse response = new TermsResponse();
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		response.setResultCode("NONE");
		response.setAgreeType("");
		response.setAgreeUrl("");
		response.setXincsNo("");
		
		log.debug("▶▶▶▶▶ [channel terms] request : {}", StringUtil.printJson(chTermsRequest));

		final String chCd = chTermsRequest.getChCd();
		final int incsNo = chTermsRequest.getIncsNo();
		// 통합회원으로 로그인 시 진입 채널에 약관동의가 되어있지 않은 경우 제공

		// 1. 경로약관 미동의 상태 -> 채널 약관 동의
		// --->
		// 2. 통합회원 고객 번호 존재, 채널 약관 동의 상태, 오픈 후 최초 로그인
		// ---> 전사약관동의

		
		boolean exist = this.mgmtApiService.isUserExist(Integer.toString(incsNo));
		if (exist) { // 통합회원 존재번호 존재

			ChnTermsCndVo reqTermsCndVo = new ChnTermsCndVo();
			reqTermsCndVo.setChCd(chCd);
			reqTermsCndVo.setIncsNo(incsNo);

			boolean chAgree = this.mgmtApiService.hasTermsAgree(reqTermsCndVo);
			if (!chAgree) { // 경로약관 미동의 상태 ME-FO-A0105
				log.debug("▶▶▶▶▶ [channel terms] 경로약관 미동의 상태");
				response.setResultCode(ResultCode.SUCCESS.getCode());
				response.setResultMessage(ResultCode.SUCCESS.message() + " (경로약관 미동의 상태)");
				response.setAgreeType("CHAGREE");
				response.setAgreeUrl(omniAuthDomain + "/terms/ch");
				response.setXincsNo(SecurityUtil.setXyzValue(Integer.toString(incsNo)));
			} else { // 경로약관 동의 상태

				boolean corpAgree = this.mgmtApiService.hasCorpTermsAgree(reqTermsCndVo);

				if (!corpAgree) { // 전사약관 미동의 상태
					log.debug("▶▶▶▶▶ [channel terms] 전사약관 미동의 상태");
					response.setResultCode(ResultCode.SUCCESS.getCode());
					response.setResultMessage(ResultCode.SUCCESS.message() + " (전사약관 미동의 상태)");
					response.setAgreeType("CORPAGREE");
					response.setAgreeUrl(omniAuthDomain + "/terms/ch");
					response.setXincsNo(SecurityUtil.setXyzValue(Integer.toString(incsNo)));
				}
			}
			log.debug("▶▶▶▶▶ [channel terms] response : {}", StringUtil.printJson(response));
		} else {
			response.setResultCode(ResultCode.USER_NOT_FOUND.getCode());
			response.setAgreeType("");
			response.setAgreeUrl("");
			response.setXincsNo("");
		}
		
		stopWatch.stop();
		log.info(stopWatch.prettyPrint());
		
		return response;
	}

	@ApiOperation(value = "배치용 회원가입(비밀번호 + 가입일)", //
			notes = "배치용 옴니 통합 회원가입 API입니다.", httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class, //
			hidden = true)
	@PostMapping("/createuserbatch")
	public ApiBaseResponse createUserForBatch( //
			@ApiParam(name = "creUserVo", value = "회원가입") //
			final @RequestBody CreateUserRequest creUserVo) {
		ApiBaseResponse response = new ApiBaseResponse();
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID)); // response.setTrxUuid(WebUtil.getHeader(OmniConstants.TRX_UUID));

		log.debug("배치용 회원가입 request : {}", StringUtil.printJson(creUserVo));

		if (creUserVo.getIncsNo() < 1 || StringUtils.isEmpty(creUserVo.getCn())) {
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
		} else {
			UserVo userVo = new UserVo();
			userVo.setUserName(creUserVo.getLoginId());
			userVo.setIncsNo(Integer.toString(creUserVo.getIncsNo()));

			boolean rtn = this.mgmtApiService.isUserExistByLoginId(creUserVo.getLoginId()); //this.mgmtApiService.isUserExist(userVo.getIncsNo());

			try {
				if (!rtn) { // 사용자 없을때 가입 처리
					Wso2RestApiCreateUserVo wso2RestApiCreateUserVo = new Wso2RestApiCreateUserVo( //
							creUserVo.getLoginId(), // 대문자가 포함되니까 에러가 발생함
							"CR030", // WSO2 API에는 평문으로 줘야함 (임시값(CR030) 사용 후 DB업데이트 하는 방식으로 개발)
							creUserVo.getCn(), //
							Integer.toString(creUserVo.getIncsNo()), //
							/*null, *///
							null, //
							null);

					log.debug("배치용 회원가입 create vo : {}", StringUtil.printJson(wso2RestApiCreateUserVo));

					ResponseEntity<String> creResponse = this.wso2Scim2RestApiService.postUser(wso2RestApiCreateUserVo);
					log.debug("배치용 회원가입 create response : {}", StringUtil.printJson(creResponse));
					if (creResponse.getStatusCode() == HttpStatus.CREATED) {
						String encodedPw = SecurityUtil.convertSHA512EncodedPasswordToWso2Format(creUserVo.getPassword());// SecurityUtil.base64(creUserVo.getPassword().getBytes());
						userVo.setUserPassword(encodedPw);
						rtn = this.mgmtApiService.updatePassword(userVo);
						if (rtn) {
							userVo.setJoinDate(creUserVo.getJoinDate());
							rtn = this.mgmtApiService.updateJoinDate(userVo);
						}

						response.SetResponseInfo(ResultCode.SUCCESS);
					} else {
						response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
					}
				} else {
					response.SetResponseInfo(ResultCode.USER_ALREADY_EXIST);
				}
			} catch (Exception e) {
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}
		log.debug("배치용 회원가입 response : {}", StringUtil.printJson(response));
		return response;
	}
	
	
	@ApiOperation(value = "중복고객통합", //
			notes = "동일한 로그인 ID에 대해 기존 고객통합번호에서 탈퇴 후 신규 번호로 재가입하는 프로세스 처리 (by 고객통합)", httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class, //
			hidden = false)
	@PostMapping("/integratedupid")
	public ApiBaseResponse createIntegateDupId( //
			@ApiParam(name = "creDupUserVo", value = "신규재가입 사용자 정보") //
			final @RequestBody CreateDupUserRequest creDupUserVo) {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response.SetResponseInfo(ResultCode.SUCCESS);
		
		response = MgmtApiValidator.checkCreateIntegateDupId(response, creDupUserVo);
		
		return this.mgmtApiService.createIntegateDupId(creDupUserVo);
		
	}
	
	@ApiOperation(value = "뷰티포인트 고객 비밀번호 수정 API", //
			notes = "뷰티포인트 고객 비밀번호 수정 API", httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class, //
			hidden = false)
	@PostMapping("/bpchangepassword")
	public ApiBaseResponse editBpUser( //
			@ApiParam(name = "bpEditUserRequest", value = "뷰티포인트 고객 비밀번호 수정 정보") //
	final @RequestBody BpEditUserRequest bpEditUserRequest) throws Exception {
		
		ApiBaseResponse response = this.mgmtApiService.changeBeautyPointUserInfo(bpEditUserRequest);
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		// if("dev".equals(profile)) {
			ResponseEntity<String> sessionResponse = this.wso2Scim2RestApiService.removeSessionByUserName(bpEditUserRequest.getCstmId());
			if(sessionResponse.getStatusCode() == HttpStatus.OK || sessionResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
				log.debug("[bpchangepassword] 사용자 세션 삭제 성공");
				log.info("▶▶▶▶▶ [bpchangepassword] paramSiteCd : {}, appChCd : {}, cstmId : {}", bpEditUserRequest.getParamSiteCd(), bpEditUserRequest.getAppChCd(), bpEditUserRequest.getCstmId());
			}
			else {
				log.error("[bpchangepassword] 사용자 세션 삭제 실패");
			}
		// }
		
		return response;
	}
	
	@ApiOperation(value = "경로 시스템에서 기존 통합고객번호 변경 시, 통합고객번호 변경", //
			notes = "경로 시스템에서 기존 통합고객번호 변경 시, 통합고객번호 변경용 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class)
	@PostMapping("/updateUserIncsNo")
	public ApiBaseResponse updateUserIncsNo(@ApiParam(name = "chUserIncsVo", value = "뷰티포인트 고객통합번호 수정", required = true) final @RequestBody ChUserIncsVo chUserIncsVo) throws ApiBusinessException {
		
		log.debug("[updateUserIncsNo] request : {}", StringUtil.printJson(chUserIncsVo));
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.updateUserIncsNo(response, chUserIncsVo);

		if (checkCommonValidation(response)) {
			// 존재하는 사용자인지 체크
//			UserVo userVo = new UserVo();
//			userVo.setUserName(chUserIncsVo.getWebId()); 
//			userVo.setIncsNo(Integer.toString(chUserIncsVo.getAsisIncsNo()));
//			
//			log.debug("[updateUserIncsNo] exist user : {}", StringUtil.printJson(userVo));
			
			ChUserVo tempVo = new ChUserVo();
			tempVo.setChCd(chUserIncsVo.getChCd());
			tempVo.setIncsNo(Integer.toString(chUserIncsVo.getAsisIncsNo()));
			tempVo.setWebId(chUserIncsVo.getWebId());
			
			log.debug("[updateUserIncsNo] exist user : {}", StringUtil.printJson(tempVo));
			
			ChUserVo chUserVo = this.mgmtApiService.getChUser(tempVo);
			
//			UserVo user = this.mgmtApiService.getUser(userVo);
			if (chUserVo != null) {
				// 탈퇴 사용자인지 체크
				boolean isDisabled = this.mgmtApiService.isDisabledUser(chUserIncsVo.getWebId());
				
				if (!isDisabled) {
					boolean rtn = this.mgmtApiService.updateUserIncsNo(chUserIncsVo.getChCd(), chUserIncsVo.getWebId(), Integer.toString(chUserIncsVo.getAsisIncsNo()), Integer.toString(chUserIncsVo.getTobeIncsNo()));
					
					if (rtn) {
						response.SetResponseInfo(ResultCode.SUCCESS);
						log.debug("[changeWebId] result = {}", response.getMessage());
					} else {
						response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						log.error("[changeWebId] SNS Update Error Message : {}", response.getMessage());
					}
				} else {
					response.SetResponseInfo(ResultCode.USER_DISABLED);
				}
			} else {
				response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
			}
		}
		
		return response;
	}
	
	@ApiOperation(value = "SNS ID로 사용자 정보 조회", //
			notes = "SNS ID로 사용자 정보 조회용 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = CheckSnsIdResponse.class)
	@PostMapping("/checksnsid")
	public CheckSnsIdResponse checkSnsId(@ApiParam(name = "CheckSnsIdVo", value = "SNS ID로 사용자 정보 조회", required = true) final @RequestBody CheckSnsIdVo checkSnsIdVo) throws ApiBusinessException {
		
		CheckSnsIdResponse response = new CheckSnsIdResponse();
		
		try {
			response = MgmtApiValidator.checkSnsId(response, checkSnsIdVo);
			
			if (checkCommonValidation(response)) {
				CheckSnsIdUserVo checkSnsIdUserVo = this.mgmtApiService.getUserBySnsId(checkSnsIdVo);
				log.debug("◆◆◆◆◆◆ [checkSnsId] UserInfo : {}", StringUtil.printJson(checkSnsIdUserVo));
				
				if(checkSnsIdUserVo == null) {
					response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
				} else if(checkSnsIdUserVo != null && checkSnsIdUserVo.isAccountDisabled()) {
					response.SetResponseInfo(ResultCode.USER_DISABLED);
				} else {
					response.SetResponseInfo(ResultCode.SUCCESS);
				}
				
				response.setUserVo(checkSnsIdUserVo);
			}
		} catch (Exception e) {
			log.error("checkSnsId.exception = {}", e.getMessage());
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
		}
		
		log.debug("[check SNS ID] response code : {}", response.getResultCode());
		return response;
	}
	
	@ApiOperation(value = "문자열 암/복호화", //
			notes = "문자열 암/복호화 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = CheckSnsIdResponse.class)
	@PostMapping("/omnicryptovalue")
	public CryptoResponse omniCryptoValue(@ApiParam(name = "CryptoVo", value = "문자열 암/복호화", required = true) final @RequestBody CryptoVo cryptoVo) throws ApiBusinessException {
		
		CryptoResponse response = new CryptoResponse();
		
		try {
			response = MgmtApiValidator.checkCrypto(response, cryptoVo);
			
			if (checkCommonValidation(response)) {
				String resultValue = "";
				if("E".equals(cryptoVo.getCryptoType().toUpperCase())) {
					resultValue = SecurityUtil.setXyzValue(cryptoVo.getValue());
					
					if(StringUtils.isEmpty(resultValue) || !StringUtils.hasText(resultValue)) { // 값이 null 이면 변환 실패
						response.SetResponseInfo(ResultCode.ENCRYPTION_ERROR);
						return response;
					}
					
					response.SetResponseInfo(ResultCode.SUCCESS);
					response.setResultValue(resultValue);
					log.debug("◆◆◆◆◆◆ [Encryption Value] value : {}, result value : {}", cryptoVo.getValue(), resultValue);
				} else if("D".equals(cryptoVo.getCryptoType().toUpperCase())) {
					resultValue = SecurityUtil.getXValue(cryptoVo.getValue());
					
					if(StringUtils.isEmpty(resultValue) || !StringUtils.hasText(resultValue)) { // 값이 null 이면 변환 실패
						response.SetResponseInfo(ResultCode.DECRYPTION_ERROR);
						return response;
					}
					
					response.SetResponseInfo(ResultCode.SUCCESS);
					response.setResultValue(resultValue);
					log.debug("◆◆◆◆◆◆ [Decryption Value] value : {}, result value : {}", cryptoVo.getValue(), resultValue);
				}

			}
		} catch (Exception e) {
			log.error("omnicryptovalue.exception = {}", e.getMessage());
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
		}
		
		log.debug("[Omni Crypto Value] response code : {}", response.getResultCode());
		return response;
	}	
	
	@ApiOperation(value = "ID / PW 로 사용자 조회", //
			notes = "ID / PW 로 옴니회원 플랫폼에 저장된 사용자 정보를 조회하는 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = CheckSnsIdResponse.class)
	@PostMapping("/checkuserinfo")
	public OmniSearchResponse checkuserinfo(@ApiParam(name = "CheckUserInfoVo", value = "사용자 정보 조회", required = true) final @RequestBody CheckUserInfoVo checkUserInfoVo) {
		
		return this.mgmtApiService.checkuserinfo(checkUserInfoVo);
		
	}
	
	@ApiOperation(value = "재인증 대상 조회", //
			notes = "고객통합번호로 Lock Table에 재인증 대상인지 조회", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = CheckSnsIdResponse.class)
	@PostMapping("/lockusercheck")
	public ApiBaseResponse lockusercheck(@ApiParam(name = "AbusingLockVo", value = "어뷰징 lock vo", required = true) final @RequestBody AbusingLockVo abusingLockVo) {
		
		return this.mgmtApiService.lockusercheck(abusingLockVo);
		
	}
	
	@ApiOperation(value = "lock User 등록", //
			notes = "lock 고객 DB 등록 API", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = CheckSnsIdResponse.class)
	@PostMapping("/lockuserinsert")
	public ApiBaseResponse lockuserinsert(@ApiParam(name = "AbusingUserVo", value = "어뷰징 User vo", required = true) final @RequestBody AbusingUserVo abusingUserVo) {
		
		return this.mgmtApiService.lockuserinsert(abusingUserVo);
		
	}
	
	@ApiOperation(value = "lock 사용자 테이블 update", //
			notes = "고객 lock 해제를 위한 update api", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = CheckSnsIdResponse.class)
	@PostMapping("/lockuserupdate")
	public ApiBaseResponse lockuserupdate(@ApiParam(name = "AbusingLockVo", value = "어뷰징 lock vo", required = true) final @RequestBody AbusingLockVo abusingLockVo) {
		
		return this.mgmtApiService.lockUpdateUser(abusingLockVo);
		
	}
	
	@ApiOperation(value = "Web2App 인증을 위한 인증키 전송", //
			notes = "Web2App 인증을 위한 인증키 전송(uuid/accessToken 암호화)", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = CheckSnsIdResponse.class)
	@PostMapping("/web2app/sendauthkey")
	public ApiBaseResponse web2AppSendAuthKey(@ApiParam(name = "authKey", value = "String", required = true) final @RequestBody AuthKeyVo authKey) {
		
		return this.mgmtApiService.web2AppSendAuthKey(authKey);
		
	}
	@ApiOperation(value = "ID / PW 로 사용자 조회(비밀번호 실패 7회)", //
			notes = "ID / PW 로 옴니회원 플랫폼에 저장된 사용자 정보를 조회하는 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = CheckSnsIdResponse.class)
	@PostMapping("/checkuserinfopwd")
	public OmniSearchResponse checkUserInfoPwd(@ApiParam(name = "CheckUserInfoVo", value = "사용자 정보 조회", required = true) final @RequestBody CheckUserInfoVo checkUserInfoVo) {
		
		return this.mgmtApiService.checkUserInfoPwd(checkUserInfoVo);
		
	}
	
}
