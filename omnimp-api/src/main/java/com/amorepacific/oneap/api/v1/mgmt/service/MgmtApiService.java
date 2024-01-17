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
package com.amorepacific.oneap.api.v1.mgmt.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.api.common.service.RestApiService;
import com.amorepacific.oneap.api.exception.ApiBusinessException;
import com.amorepacific.oneap.api.v1.mgmt.mapper.MgmtApiMapper;
import com.amorepacific.oneap.api.v1.mgmt.vo.AbusingLockVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.AbusingUserVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.AuthKeyVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChUserVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.CheckUserInfoVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChnTermsCndVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.DupIdVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.IdSearchVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.MappingIdSearchVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.OmniSearchResponse;
import com.amorepacific.oneap.api.v1.mgmt.vo.OmniUserVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.SearchSnsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.SnsUnlinkVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.TermsResponseVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.TermsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.UserVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.VerifyPwdVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.Web2AppVo;
import com.amorepacific.oneap.api.v1.wso2.mapper.Wso2ApiMapper;
import com.amorepacific.oneap.api.v1.wso2.service.Wso2RusmSoapApiService;
import com.amorepacific.oneap.api.v1.wso2.service.Wso2Scim2RestApiService;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.exception.OmniException;
import com.amorepacific.oneap.common.types.LoginType;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.LoginStepVo;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.StatusCheckResponse;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.BpEditUserRequest;
import com.amorepacific.oneap.common.vo.api.ChangePasswordData;
import com.amorepacific.oneap.common.vo.api.ChangeWebIdData;
import com.amorepacific.oneap.common.vo.api.CheckSnsIdUserVo;
import com.amorepacific.oneap.common.vo.api.CheckSnsIdVo;
import com.amorepacific.oneap.common.vo.api.CipAthtVo;
import com.amorepacific.oneap.common.vo.api.CreateDupUserRequest;
import com.amorepacific.oneap.common.vo.api.CreateDupUserResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.IncsRcvData;
import com.amorepacific.oneap.common.vo.api.UpdateCustResponse;
import com.amorepacific.oneap.common.vo.api.UpdateCustVo;
import com.amorepacific.oneap.common.vo.api.UpdateCustVo.CicuedCuChCsTcVo;
import com.amorepacific.oneap.common.vo.dormancy.DormancyIData;
import com.amorepacific.oneap.common.vo.dormancy.DormancyOData;
import com.amorepacific.oneap.common.vo.dormancy.DormancyRequest;
import com.amorepacific.oneap.common.vo.dormancy.DormancyRequestHeader;
import com.amorepacific.oneap.common.vo.dormancy.DormancyRequestInput;
import com.amorepacific.oneap.common.vo.dormancy.DormancyResponse;
import com.amorepacific.oneap.common.vo.dormancy.DormancyResponseHeader;
import com.amorepacific.oneap.common.vo.dormancy.DormancyVo;
import com.amorepacific.oneap.common.vo.sns.SnsUrl;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.mgmt 
 *    |_ MgmtApiService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 31.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class MgmtApiService {

	// 고객목록조회 API URL
	@Value("${external.cip.api.getcicuemcuinfrlist}")
	private String getcicuemcuinfrlist;
	
	// 통합 고객 변경
	@Value("${external.cip.api.updatecicuemcuinfrfull}")
	private String updatecicuemcuinfrfull;
	
	// 뷰티포인트 회원정보 수정 api url
	@Value("${external.bp.api.edit}")
	private String checkBpEditUserEndpoint; 
	
	// 고객상세조회 API URL
	@Value("${external.cip.api.getcicuemcuinfrbyincsno}")
	private String getcicuemcuinfrbyincsno;
	
	@Value("${dormancy.url}")
	private String dormancyUrl;

	@Value("${dormancy.source}")
	private String dormancySource;

	@Value("${dormancy.username}")
	private String dormancyUsername;

	@Value("${dormancy.userpassword}")
	private String dormancyUserpassword;
	
	@Value("${wso2.ssocommonauthurl}")
	private String ssocommonauthurl;
	
	@Autowired
	private MgmtApiMapper mgmtApiMapper;
	
	@Autowired
	private SystemInfo systemInfo;
	
	@Autowired
	private RestApiService restApiService;
	
	@Autowired
	private Wso2Scim2RestApiService wso2Scim2RestApiService;
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	@Autowired
	private Wso2RusmSoapApiService wso2SoapApiService;
	
	@Autowired
	private Wso2ApiMapper wso2ApiMapper ;
	/**
	 * 
	 * <pre>
	 * comment  : 회원아이디, 통합고객번호로 사용자존재여부 체크
	 * author   : takkies
	 * date     : 2020. 8. 26. 오전 9:30:14
	 * </pre>
	 * 
	 * @param userVo
	 * @return
	 */
	public boolean isUserExist(final String incsNo) {
		return this.mgmtApiMapper.isUserExist(incsNo) > 0;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 30. 오후 6:02:29
	 * </pre>
	 * @param userName
	 * @return
	 */
	public boolean isUserExistByLoginId(final String userName) {
		return this.mgmtApiMapper.isUserExistByLoginId(userName) > 0;
	}

	public boolean isDisabledUser(final String userName) {
		return this.mgmtApiMapper.isDisabledUser(userName) == null ? false : "true".equalsIgnoreCase(this.mgmtApiMapper.isDisabledUser(userName));
	}

	/**
	 * <pre>
	 * comment  : 잠김/휴면이 아닌 사용 가능한 아이디인지 체크  TODO 상태 확인 필요 테스트 못해봄
	 * author   : hkdang
	 * date     : 2020. 8. 31. 오후 6:50:11
	 * </pre>
	 * 
	 * @param userName
	 * @return
	 */
	public boolean isUsableUser(final String userName) {
		String isLocked = this.mgmtApiMapper.isLockedUser(userName);
		String isDormancyUser = this.mgmtApiMapper.isDormancyUser(userName);
		String state = this.mgmtApiMapper.getAccountState(userName);

		if (isLocked != null && "true".equalsIgnoreCase(isLocked)) { // 잠김
			return false;
		} else if (state != null && "lock".equalsIgnoreCase(state)) { // 잠김
			return false;
		} else if (isDormancyUser != null && StringUtil.isTrue(isDormancyUser)) { // 휴면
			return false;
		}

		return true;
	}

	public boolean isTermsExist(final TermsVo termsVo) {
		return this.mgmtApiMapper.isTermsExist(termsVo) > 0;
	}

	public List<String> getAssociatedSnsId(final SnsUnlinkVo snsUnlinkVo) {
		return this.mgmtApiMapper.getAssociatedSnsId(snsUnlinkVo);
	}

	public boolean isMappingExsist(final MappingIdSearchVo mappingIdSearchVo) {
		return this.mgmtApiMapper.isMappingExsist(mappingIdSearchVo) > 0;
	}
	
	public boolean isMappingOther(final MappingIdSearchVo mappingIdSearchVo) {
		return this.mgmtApiMapper.isMappingOther(mappingIdSearchVo) > 0;
	}
	
	public UserVo getUserByIncsNo(final String incsNo) throws ApiBusinessException {
		return this.mgmtApiMapper.getUserByIncsNo(incsNo);
	}
	
	public UserVo getUserByIncsNoAndUserName(final UserVo userVo) throws ApiBusinessException {
		return this.mgmtApiMapper.getUserByIncsNoAndUserName(userVo);
	}
	
	public UserVo getUser(UserVo userVo) throws ApiBusinessException {
		return this.mgmtApiMapper.getUser(userVo);
	}

	public void mergeTermYn(final TermsVo termsVo) throws ApiBusinessException {
		// insert or update 쿼리
		// insert 에서만 result 값을 주고, update 시 result 값을 주지 않기 때문에 결과 반환하지 않는다.
		this.mgmtApiMapper.mergeTermYn(termsVo);
	}

	public boolean checkDuplicateId(final DupIdVo dupIdVo) throws ApiBusinessException {
		return this.mgmtApiMapper.checkDuplicateId(dupIdVo) > 0;
	}

	public boolean verifyPassword(final VerifyPwdVo verifyPwdVo) throws ApiBusinessException {
		return this.mgmtApiMapper.verifyPassword(verifyPwdVo) > 0;
	}

	public String getPassword(final String loginId) {
		return this.mgmtApiMapper.getPassword(loginId);
	}
	
	public boolean updatePassword(final UserVo userVo) {
		return this.mgmtApiMapper.updatePassword(userVo) > 0;
	}

	public boolean updateJoinDate(final UserVo userVo) {
		return this.mgmtApiMapper.updateJoinDate(userVo) > 0;
	}

	public List<SearchSnsVo> getSnsInfoList(final String userName) throws ApiBusinessException {
		return this.mgmtApiMapper.getSnsInfoList(userName);
	}

	public List<TermsResponseVo> getTermsList(final ChnTermsCndVo chnTermsCndVo) throws ApiBusinessException {
		return this.mgmtApiMapper.getTermsList(chnTermsCndVo);
	}

	public List<TermsResponseVo> getRequiredTermsList(final ChnTermsCndVo chnTermsCndVo) throws ApiBusinessException {
		return this.mgmtApiMapper.getRequiredTermsList(chnTermsCndVo);
	}

	public boolean hasTermsAgree(final ChnTermsCndVo reqTermsCndVo) {
		return this.mgmtApiMapper.hasTermsAgree(reqTermsCndVo) > 0; // 1이면 약관 동의 완료
	}

	public boolean hasCorpTermsAgree(final ChnTermsCndVo reqTermsCndVo) {
		return this.mgmtApiMapper.hasCorpTermsAgree(reqTermsCndVo) > 0; // 1이면 약관 동의 완료
	}

	public String checkUserId(final IdSearchVo idSearchVo) throws ApiBusinessException {

		log.debug("◆◆◆◆◆◆ [check user id] by username and mobile request : {}", StringUtil.printJson(idSearchVo));
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		Map<String, Object> params = new HashMap<>();
		
		if (StringUtils.isEmpty(idSearchVo.getMobile())) {
			return "";
		}
		
		String mobile[] = StringUtil.splitMobile(idSearchVo.getMobile());
		
		params.put("custNm", idSearchVo.getName());
		params.put("cellTidn", mobile[0]);
		params.put("cellTexn", mobile[1]);
		params.put("cellTlsn", mobile[2]);
		
		CipAthtVo cipAthtVo = CipAthtVo.builder().build();
		params.put("cipAthtVo", cipAthtVo);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		ResponseEntity<CustInfoResponse> response = this.restApiService.post(this.getcicuemcuinfrlist, headers, json, CustInfoResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			return "";
		}
		
		if (response.getStatusCode() == HttpStatus.OK) {
			CustInfoResponse custResponse = response.getBody();
			log.debug("◆◆◆◆◆◆ [check user id] by username and mobile response : {}", StringUtil.printJson(custResponse));
			Customer customers[] = custResponse.getCicuemCuInfQcVo();
			if (customers != null && customers.length > 0) {
				Customer customer = customers[0];
				return customer.getChcsNo();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	public String getCustUser(final ChangeWebIdData changeWebIdData) throws ApiBusinessException {
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		Map<String, Object> params = new HashMap<>();
		params.put("incsNo", Integer.toString(changeWebIdData.getIncsNo()));
		
		CipAthtVo cipAthtVo = CipAthtVo.builder().build();
		params.put("cipAthtVo", cipAthtVo);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		ResponseEntity<Customer> response = this.restApiService.post(this.getcicuemcuinfrbyincsno, headers, json, Customer.class);
		
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			return "";
		}
		if (response.getStatusCode() == HttpStatus.OK) {
			Customer customer = response.getBody();
			log.debug("◆◆◆◆◆◆ [get cust user] by incsNo : {}", StringUtil.printJson(customer));
			
			if ("ICITSVCOM001".equals(customer.getRsltCd())) { // ICITSVCOM001 : 통합고객이 존재하지 않습니다
				return "";
			}
			if ("Y".equalsIgnoreCase(customer.getDrccCd())) { // 휴면고객
				return "";
			}
			if ("ICITSVCOM000".equals(customer.getRsltCd())) {
				return customer.getCustNm();
			} else {
				return "";
			}
		} else {
			return "";
		}
		
		/*
		 * ResponseEntity<CustInfoResponse> response = this.restApiService.post(this.getcicuemcuinfrlist, headers, json, CustInfoResponse.class); if
		 * (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) { return ""; } if (response.getStatusCode() == HttpStatus.OK) {
		 * CustInfoResponse custResponse = response.getBody(); log.debug("◆◆◆◆◆◆ [get cust user] by incsNo : {}",
		 * StringUtil.printJson(custResponse)); Customer customers[] = custResponse.getCicuemCuInfTcVo(); if (customers != null && customers.length
		 * > 0) { Customer customer = customers[0]; return customer.getCustNm(); } else { return ""; } } else { return ""; }
		 */
	}

	public boolean deleteAssociatedId(final SnsUnlinkVo snsUnlinkVo) {
		return this.mgmtApiMapper.deleteAssociatedId(snsUnlinkVo) > 0;
	}
	
	public boolean deleteChannelUser(final TermsVo termsVo) {
		return this.mgmtApiMapper.deleteChannelUser(termsVo) > 0;
	}
	
	public boolean inserOccuCustTncHist(final TermsVo termsVo) {
		return this.mgmtApiMapper.inserOccuCustTncHist(termsVo) > 0;
	}

	public boolean updatePasswordReset(final String userName) {
		return this.mgmtApiMapper.updatePasswordReset(userName) > 0;
	}
	
	public boolean changeIntegratedUserPassword(final ChangePasswordData changePasswordData) {
		
		log.debug("◆◆◆◆◆◆ [change password] integrated user request : {}", StringUtil.printJson(changePasswordData));
		
		UpdateCustVo updateCustVo = new UpdateCustVo();
		updateCustVo.setIncsNo(Integer.toString(changePasswordData.getIncsNo()));
		updateCustVo.setChCd(changePasswordData.getChCd());
		updateCustVo.setChgChCd(changePasswordData.getChCd());
		updateCustVo.setLschId("OCP");
		
		CicuedCuChCsTcVo cicuedCuChCsTcVo = new CicuedCuChCsTcVo();
		cicuedCuChCsTcVo.setIncsNo(changePasswordData.getIncsNo());
		cicuedCuChCsTcVo.setChCd(changePasswordData.getChCd());
		cicuedCuChCsTcVo.setChcsNo(changePasswordData.getLoginId());
		cicuedCuChCsTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(changePasswordData.getChangePassword()));
		cicuedCuChCsTcVo.setLschId("OCP");
		updateCustVo.addChannel(cicuedCuChCsTcVo);
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(updateCustVo);

		ResponseEntity<UpdateCustResponse> response = this.restApiService.post(this.updatecicuemcuinfrfull, headers, json, UpdateCustResponse.class);
		
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			return false;
		}
		
		UpdateCustResponse custResponse = response.getBody();
		log.debug("◆◆◆◆◆◆ [change password] integrated user response : {}", StringUtil.printJson(custResponse));
		if ("ICITSVCOM000".equals(custResponse.getRsltCd())) {
			return true;
		}
		return false;
	}
	
	public ChUserVo getChUser(final ChUserVo chUserVo) {
		return this.mgmtApiMapper.getChUser(chUserVo); 
	}
	
	public int updateChUserPassword(final ChUserVo chUserVo) {
		return this.mgmtApiMapper.updateChUserPassword(chUserVo); 
	}
	
	public String getCorpTermsCode(final String chCd) {
		return this.mgmtApiMapper.getCorpTermsCode(chCd);
	}
		
	/**
	 * 
	 * <pre>
	 * comment  : 탈퇴시 SNS 연결 해제 (현재 카카오만 적용) 
	 * author   : hkdang
	 * date     : 2020. 10. 30. 오후 9:08:55
	 * </pre>
	 * @param incsNo
	 * @return
	 */
	public BaseResponse doSnsUnlink(final int incsNo) {
		
		BaseResponse response = new BaseResponse();
				
		UserVo userVo = this.mgmtApiMapper.getUserByIncsNo(Integer.toString(incsNo));
		if(userVo == null) {
			response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
			return response;
		}
		
		List<SearchSnsVo> snsVoList = this.mgmtApiMapper.getSnsInfoList(userVo.getUserName());
		if(snsVoList == null || snsVoList.size() == 0) {
			response.SetResponseInfo(ResultCode.SNS_NOT_FOUND_ASSO_DISCONECT_FAIL);
			return response;
		}
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		log.debug("▶▶▶▶▶▶  [SnsAuth] system profile : {}", profile);
		
		final String kakaoKey = ConfigUtil.getInstance().getSnsInfo(profile, "ka", "adminkey");
		
		final HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK " + kakaoKey);
		
		for(SearchSnsVo snsVo : snsVoList) {
			if(!snsVo.getSnsType().equalsIgnoreCase("KA")) {
				response.SetResponseInfo(ResultCode.SNS_INVALID_TYPE);
				continue;
			}
			
			String unlinkUrl = SnsUrl.KA_UNLINK + "?target_id_type=user_id&target_id=" + snsVo.getSnsId();
			ResponseEntity<String> res = restApiService.post(unlinkUrl, headers, null, String.class);
			log.debug("▶▶▶▶▶▶▶▶▶▶ [doSnsUnlink] SNS ID : {}", res.getBody());
			if( StringUtils.hasText(res.getBody()) ) {
				response.SetResponseInfo(ResultCode.SUCCESS);
			} else {
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		}
		
		return response;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : UM_USER_ATTRIBUTE 의 고객통합번호를 신규 고객통합번호로 업데이트
	 * author   : takkies
	 * date     : 2020. 11. 9. 오후 4:55:15
	 * </pre>
	 * @param createDupUserRequest
	 * @return
	 */
	public boolean updateIncsNoByNewUserAttribute(final CreateDupUserRequest createDupUserRequest) {
		return this.mgmtApiMapper.updateIncsNoByNewUserAttribute(createDupUserRequest) > 0;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 이전 고객통합번호의 약관동의 내역을 신규 고객 통합번호로 insert 처리 하기 위해 조회
	 * author   : takkies
	 * date     : 2020. 11. 9. 오후 5:05:30
	 * </pre>
	 * @param createDupUserRequest
	 * @return
	 */
	public List<TermsResponseVo> getUserTermsListByIncsNo(final CreateDupUserRequest createDupUserRequest) {
		return this.mgmtApiMapper.getUserTermsListByIncsNo(createDupUserRequest);
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 중복고객통합, 통합한 사용자가 휴면이면 휴면 해제하기
	 * author   : takkies
	 * date     : 2020. 12. 8. 오후 6:01:11
	 * </pre>
	 * @param createDupUserRequest
	 * @return
	 */
	@Transactional(value = "transactionManager", rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	public ApiBaseResponse createIntegateDupId(final CreateDupUserRequest createDupUserRequest) {
		ApiBaseResponse response = new ApiBaseResponse();
		response.SetResponseInfo(ResultCode.SUCCESS);
		// 1. 해당 로그인 ID + 탈퇴 처리할 고객통합번호로 존재 여부 ( 미존재 시, 1010 오류 코드)
		
		List<String> loginIds = Arrays.asList(createDupUserRequest.getLoginId());
		Map<String, Object> params = new HashMap<>();
		params.put("incsNo", Integer.toString(createDupUserRequest.getIncsNo()));
		params.put("loginIds", loginIds);
		
		if (this.mgmtApiMapper.existUserByLoginidAndIncsNo(params) <= 0) {
			log.info("1-0.고객통합번호, 아이디로 사용자 없음 : {}", StringUtil.printJson(params));
			response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
			return response;
		}
		
		params.put("incsNoNew", Integer.toString(createDupUserRequest.getIncsNoNew()));
		// 1-1.신규 고객통합번호가 이미 있는지 체크 ( 있으면 등록하면 안됨. 오류 )
		if(this.mgmtApiMapper.existUserByLoginidAndIncsNoNew(params) > 0) {
			log.info("1-1.신규 고객통합번호가 이미 있음 : {}", StringUtil.printJson(params));
			response.SetResponseInfo(ResultCode.USER_ALREADY_EXIST);
			return response;
		}
		
		// 2. 해당 로그인 ID 가 이미 탙퇴된 사용자 인지 (이미 탈퇴된 경우 1000 오류 코드)
		final CreateDupUserResponse dupResponse = this.mgmtApiMapper.existDisabledUser(params);
		if (dupResponse != null &&(StringUtils.hasText(dupResponse.getAccountDisabled()) && "true".equals(dupResponse.getAccountDisabled()))) {
			log.info("2. 해당 로그인 ID 가 이미 탙퇴된 사용자 : {}", StringUtil.printJson(params));
			response.SetResponseInfo(ResultCode.USER_DISABLED);
			return response;
		}
		
		// 3. 필수값 유무 (0100), 4.정상 입력인 경우
		
		// 4-1) UM_USER의 고객통합번호를 신규 고객통합번호로 업데이트
		
		boolean success = this.mgmtApiMapper.updateIncsNoByNewUser(params) > 0;
		
		// 4-2) UM_USER_ATTRIBUTE 의 고객통합번호를 신규 고객통합번호로 업데이트
		if (success) {
			success = this.mgmtApiMapper.updateIncsNoByNewUserAttribute(createDupUserRequest) > 0;
		}
		
		// 4-3) 이전 고객통합번호의 약관동의 내역을 신규 고객 통합번호로 insert 처리
		// 4-4) 약관 History table ocp.occueh_cust_tnc_hist 에 신규 추가
		if (success) {
			List<TermsResponseVo> respTermsVo = this.mgmtApiMapper.getUserTermsListByIncsNo(createDupUserRequest);
			if (respTermsVo != null) {
				TermsVo terms = null;
				for (TermsResponseVo respTerm : respTermsVo) {
					terms = new TermsVo();
					terms.setChCd(respTerm.getChCd());
					terms.setIncsNo(createDupUserRequest.getIncsNoNew());
					terms.setTcatCd(respTerm.getTcatCd());
					terms.setTncaDttm(DateUtil.getCurrentDateString("yyyy-MM-dd HH:mm:ss"));
					terms.setTncAgrYn(respTerm.getTncAgrYn());
					
					this.mgmtApiMapper.mergeTermYn(terms);
					this.mgmtApiMapper.inserOccuCustTncHist(terms); // 약관 이력 insert
					
				}
			}
		}

		// 중복정보 통합할 때 변경 후 통합고객번호로 고객통합에서 이름, 휴면여부를 조회해서 반영
		// success = false; // TODO 일단 막고 추후에 해제하기
		if (success) {
			
			String incsNo = Integer.toString(createDupUserRequest.getIncsNoNew());
			String chCd = createDupUserRequest.getChCd(); // 이 값이 있어야 하므로 API 요청 파라미터 추가
			
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(incsNo);
			custInfoVo.setChCd(chCd);
			
			if (this.isDormancyCutomer(custInfoVo)) { // 휴면 사용자
				log.info("▶▶▶▶▶▶ [1]. 통합으로 변경된 사용자가 휴면 사용자 : {}", incsNo);
				DormancyVo dormancyVo = new DormancyVo();
				dormancyVo.setIncsNo(incsNo);
				dormancyVo.setChCd(chCd);
				
				DormancyResponse dormancyResp = this.releaseDormancy(dormancyVo);
				log.info("▶▶▶▶▶▶ [2]. 휴면 해제 API 호출 결과 : {}", StringUtil.printJson(dormancyResp));
				String rtnCode = dormancyResp.getRESPONSE().getHEADER().getRTN_CODE();
				rtnCode = StringUtils.isEmpty(rtnCode) ? dormancyResp.getRESPONSE().getHEADER().getRTN_TYPE() : rtnCode;
				
				if (rtnCode.equals(OmniConstants.SEND_SMS_EAI_SUCCESS)) {
					String custname = dormancyResp.getRESPONSE().getHEADER().getCSTMNM();
					
					IncsRcvData incsRcvData = new IncsRcvData();
					incsRcvData.setCustNm(custname);
					incsRcvData.setIncsNo(Integer.parseInt(incsNo));
					incsRcvData.setDrccCd("N"); // 휴면 해제

					if (this.existRcvDormancyData(incsRcvData)) { // 통합고객수신데이터가 존재할 경우
						log.info("▶▶▶▶▶▶ [3]. 통합고객수신데이터 업데이트");
						this.updateRcvDormancyName(incsRcvData); // 통합고객수신데이터 업데이트(사용자명, 휴면해제플래그)
					} else {
						log.info("▶▶▶▶▶▶ [3]. 통합고객수신데이터 인서트");
						this.insertRcvDormancyName(incsRcvData); // 통합고객수신데이터 인서트(사용자명, 휴면해제플래그)
					}
					log.info("▶▶▶▶▶▶ [4]. 옴니 사용자 휴면 해제");
					this.updateReleaseDormancyUser(incsNo); // 옴니 사용자 휴면 해제
					
				}
				else {
					response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				}
			}
		}
		
		return response;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 뷰티포인트 패스워드 변경 
	 * author   : hkdang
	 * date     : 2020. 11. 12. 오후 7:33:15
	 * </pre>
	 * @return
	 */
	public ApiBaseResponse changeBeautyPointUserInfo(BpEditUserRequest bpEditUserRequest) {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response.SetResponseInfo(ResultCode.SUCCESS);
		
		return response;
		
		/*
		 * response = MgmtApiValidator.checkBpUserInfo(response, bpEditUserRequest);
		 * 
		 * log.debug("▶▶▶▶▶▶ [edit bp] user request : {}", StringUtil.printJson(bpEditUserRequest));
		 * 
		 * final HttpHeaders headers = new HttpHeaders(); MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		 * headers.setContentType(mediaType); headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); headers.add("Referer",
		 * "ecp.amorepacific.com");
		 * 
		 * ResponseEntity<String> apiResponse = this.restApiService.post(this.checkBpEditUserEndpoint, headers, bpEditUserRequest, String.class);
		 * 
		 * if (apiResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) { response.SetResponseInfo(ResultCode.SYSTEM_ERROR); return
		 * response; }
		 * 
		 * Gson gson = new GsonBuilder().disableHtmlEscaping().create(); BpEditUserResponse bpResponse = gson.fromJson(apiResponse.getBody(),
		 * BpEditUserResponse.class);
		 * 
		 * log.debug("▶▶▶▶▶▶ [edit bp] user response : {}", StringUtil.printJson(bpResponse));
		 * 
		 * if (!"SUCCESS".equals(bpResponse.getRESULT())) { response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
		 * response.setResultCode(bpResponse.getCODE()); response.setMessage(bpResponse.getMESSAGE()); return response; }
		 * 
		 * response.SetResponseInfo(ResultCode.SUCCESS); return response;
		 */
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 25. 오후 9:19:38
	 * </pre>
	 * @param incsNo
	 * @return
	 */
	public boolean deleteSnsMapping(final String incsNo) {
		return this.mgmtApiMapper.deleteSnsMapping(incsNo) > 0;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 휴면 해제 하기 EAI
	 * author   : takkies
	 * date     : 2020. 12. 8. 오후 5:04:30
	 * </pre>
	 * @param dormancyVo
	 * @return
	 */
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
			
			ResponseEntity<String> resp = this.restApiService.post(this.dormancyUrl, headers, jsonBody, String.class);
			log.debug("▶▶▶▶▶▶ [release dormancy] response : {}", StringUtil.printJson(resp));
			if (resp == null || (resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)) {
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
			return null;
		}
	}
	
	public boolean isDormancyCutomer(final CustInfoVo custInfoVo) {
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.hasText(custInfoVo.getIncsNo())) {
			params.put("incsNo", custInfoVo.getIncsNo());
		}
		
		CipAthtVo cipAthtVo = CipAthtVo.builder().build();
		params.put("cipAthtVo", cipAthtVo);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		ResponseEntity<Customer> response = this.restApiService.post(this.getcicuemcuinfrbyincsno, headers, json, Customer.class);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			return false;
		}
		
		Customer customer = response.getBody();
		if ("ICITSVCOM001".equals(customer.getRsltCd())) { // ICITSVCOM001 : 통합고객이 존재하지 않습니다
			return false;
		}
		if ("Y".equalsIgnoreCase(customer.getDrccCd())) { // 휴면고객
			return true;
		}
		return false;
	}
	
	public boolean existRcvDormancyData(final IncsRcvData incsRcvData) {
		return this.mgmtApiMapper.existRcvDormancyData(incsRcvData) > 0;
	}
	
	public boolean insertRcvDormancyName(final IncsRcvData incsRcvData) {
		return this.mgmtApiMapper.insertRcvDormancyName(incsRcvData) > 0;
	}
	
	public boolean updateRcvDormancyName(final IncsRcvData incsRcvData) {
		return this.mgmtApiMapper.updateRcvDormancyName(incsRcvData) > 0;
	}
	
	public boolean updateReleaseDormancyUser(final String incsNo) {
		return this.mgmtApiMapper.updateReleaseDormancyUser(incsNo) > 0;
	}
	
	public boolean updateSnsUserName(final String userName, final String userNameNew) {
		return this.mgmtApiMapper.updateSnsUserName(userName, userNameNew) > 0;
	}
	
	public boolean updateUserIncsNo(final String chCd, final String webId, final String asisIncsNo, final String tobeIncsNo) {
		return this.mgmtApiMapper.updateUserIncsNo(chCd, webId, asisIncsNo, tobeIncsNo) > 0;
	}
	
	public CheckSnsIdUserVo getUserBySnsId(final CheckSnsIdVo checkSnsIdVo) throws ApiBusinessException {
		return this.mgmtApiMapper.getUserBySnsId(checkSnsIdVo);
	}

	/**
	 * <pre>
	 * comment  : ID / PW 로 옴니회원 플랫폼에 저장된 사용자 정보 조회
	 * author   : judahye
	 * date     : 2022. 11. 11. 오후 4:04:19
	 * </pre>
	 * @param checkUserInfoVo
	 * @return
	 */
	public OmniSearchResponse checkuserinfo(CheckUserInfoVo checkUserInfoVo) {
		log.info("▶▶▶▶▶ [checkuserinfo] checkUserInfoVo : {}", checkUserInfoVo);
		OmniSearchResponse response = new OmniSearchResponse();
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		
		if(StringUtils.isEmpty(checkUserInfoVo.getChCd()) || StringUtils.isEmpty(checkUserInfoVo.getLoginId()) || StringUtils.isEmpty(checkUserInfoVo.getLoginPw())) {
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		
		StatusCheckResponse statusResponse = statusCheck(checkUserInfoVo);
		log.info("▶▶▶▶▶ [checkuserinfo] statusResponse : {}", statusResponse);
		//-20/-40 비밀번호 틀림, 0 존재하지 않는 회원, -5 잠김, -10 탈퇴, -15 비밀번호 초기화, -30 비밀번호 캠페인
		if("-20".equals(statusResponse.getResultCode())) {
			response.SetResponseInfo(ResultCode.USER_PWD_FAILED);
			return response;
		}else if ("0".equals(statusResponse.getResultCode())) {
			response.SetResponseInfo(ResultCode.USER_SEARCH_NOT_FOUND);
			return response;
		}else if ("-5".equals(statusResponse.getResultCode())) {
			response.SetResponseInfo(ResultCode.USER_UNLOCK); 
			if (StringUtils.hasText(statusResponse.getRemainUnLockTime())) {
				String[] detailTime = statusResponse.getRemainUnLockTime().split(":");
				response.setMessage("로그인 5회 실패하였습니다. "+detailTime[0]+"분 "+detailTime[1]+"초 후에 다시 시도해주세요.");
			}
			return response;
		}
		String wso2encPwd = SecurityUtil.getEncodedWso2Password(SecurityUtil.getXyzValue(config.getDecryptPassphrase(),checkUserInfoVo.getLoginPw(),false));
		if(wso2encPwd == null) {
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		List<UmOmniUser> omniUsers = getOmniLoginUserList(checkUserInfoVo.getLoginId(), wso2encPwd);
		
		omniUsers = (omniUsers == null) ? Collections.emptyList() : omniUsers;
		final int omnicount = omniUsers.size();
		if(omnicount>0) {
			final UmOmniUser omniUser = omniUsers.get(0);
			
			final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
			final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
			final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
			final String accountDormancy = omniUser.getUmUserDormancy(); // 휴면 사용자
			final String unlockTime = omniUser.getUnlockTime() == null ? "0" : omniUser.getUnlockTime();
			log.info("▶▶▶▶▶ [checkuserinfo] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
			final String accountLastPasswordUpdate = omniUser.getLastPasswordUpdate(); // 마지막으로 비밀번호 변경한 날짜(UNIXTIME)
			if ((StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) &&  statusResponse.getRemainUnLockTime() != null) { // 잠김사용자
				// valid unlock time 이 아니면 lock 하지 않음.
				if (DateUtil.isValidUnlockTime(unlockTime)) {
				//if (!"0".equals(unlockTime)) {
					final String remainUnlockTime = DateUtil.getRemainedUnlockTime(unlockTime);
					response.SetResponseInfo(ResultCode.USER_UNLOCK); 
					if (StringUtils.hasText(remainUnlockTime)) {
						omniUser.setUnlockTime(remainUnlockTime);
						omniUser.setFailedLoginAttempts(Integer.toString(DateUtil.getUnlockTimeTermSeconds(unlockTime)));
						omniUsers.add(omniUser);
						log.info("▶▶▶▶▶ [checkuserinfo] user status under lock time : {}, current : {}", DateUtil.getUnixTimestampToDateString(unlockTime), DateUtil.getCurrentDateTimeString());
						String[] detailTime = statusResponse.getRemainUnLockTime().split(":");
						response.setMessage("로그인 5회 실패하였습니다. "+detailTime[0]+"분 "+detailTime[1]+"초 후에 다시 시도해주세요.");
					}
					return response;
				} else {
					log.info("▶▶▶▶▶ [checkuserinfo] user status passed lock time, do next process.");
				}
			}
			if (StringUtil.isTrue(accountDisabled)) { // 탈퇴사용자
				response.SetResponseInfo(ResultCode.USER_DISABLED);
				OmniUserVo omniUserVo = new OmniUserVo();
				omniUserVo.setDisabledDate(DateUtil.getUnixTimestampToDateString(omniUser.getDisabledDate(), DateUtil.DATETIME_FORMAT));
				omniUserVo.setAccountDisabled(omniUser.getAccountDisabled());
				omniUserVo.setDisabledDate(omniUser.getDisabledDate());
				response.setUserVo(omniUserVo);
				return response;
			}
			
			UserVo uservo = new UserVo();
			uservo.setUserName(checkUserInfoVo.getLoginId());
			uservo.setUserPassword(SecurityUtil.getEncodedWso2Password(SecurityUtil.getXyzValue(config.getDecryptPassphrase(),checkUserInfoVo.getLoginPw(),false)));
			OmniUserVo omniUserVo = this.mgmtApiMapper.getUserByUserId(uservo);
			log.debug("▶▶▶▶▶ [checkuserinfo] login process user dormancy ? {}", StringUtil.isTrue(accountDormancy));
			if (StringUtil.isTrue(accountDormancy)) { // 휴면 사용자
				omniUserVo.setUserDormancy("true");
				omniUserVo.setFullName("휴면고객");
			}
			if (StringUtils.hasText(accountLastPasswordUpdate)) {
				omniUserVo.setLastPasswordUpdate(DateUtil.getUnixTimestampToDateString(omniUser.getLastPasswordUpdate(), DateUtil.DATETIME_FORMAT));
			}
			log.info("▶▶▶▶▶ [checkuserinfo] omniUserVo : {}", StringUtil.printJson(omniUserVo));
			response.setUserVo(omniUserVo);
			response.SetResponseInfo(ResultCode.SUCCESS);
		}else {
			if("-5".equals(statusResponse.getResultCode())){
				String[] detailTime2 = statusResponse.getRemainUnLockTime().split(":");
				response.SetResponseInfo(ResultCode.USER_UNLOCK); 
				response.setMessage("로그인 5회 실패하였습니다. "+detailTime2[0]+"분 "+detailTime2[1]+"초 후에 다시 시도해주세요.");
				return response;
			}
			UmOmniUser omniUser = getOmniUserByLoginUserName(checkUserInfoVo.getLoginId().trim());
			if(omniUser != null) {
				response.SetResponseInfo(ResultCode.USER_PWD_FAILED);
				return response;
			}else {
				response.SetResponseInfo(ResultCode.USER_SEARCH_NOT_FOUND);
				return response;
			}
		}
		return response;
	}
	
	public StatusCheckResponse statusCheck(final CheckUserInfoVo checkUserInfoVo) {
		final String chCd = checkUserInfoVo.getChCd();
		final String encLoginid = checkUserInfoVo.getLoginId();
		final String encLoginpw = checkUserInfoVo.getLoginPw();

		log.debug("▶▶▶▶▶▶ [status check] status check(id,pwd) : {}, {}", encLoginid, encLoginpw);

		final String loginId = encLoginid;
		final String loginPwd = encLoginpw;
		
		final LoginStepVo loginStepVo = loginStatusCheck(chCd, loginId, loginPwd);
		LoginType loginType = loginStepVo.getLoginType();
		log.debug("▶▶▶▶▶▶ [status check] login type : {}, login step vo : {}", LoginType.get(loginType.getType()), StringUtil.printJson(loginStepVo));

		if (loginType == LoginType.DORMANCYFAIL) {
			throw new OmniException("시스템 오류가 발생하였습니다. 관리자에게 문의하시기 바랍니다.");
		}
		
		final int type = loginType.getType();
		StatusCheckResponse response = new StatusCheckResponse();
		response.setStatus(type);
		response.setResultCode(Integer.toString(type));
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		response.setXincsno(SecurityUtil.setXyzValue(loginStepVo.getIncsNo()));

		if (loginType == LoginType.LOCK) {
			List<UmOmniUser> omniUsers = loginStepVo.getOmniUsers();
			if (omniUsers != null && omniUsers.size() > 0) {
				UmOmniUser omniUser = omniUsers.get(0);
				if (omniUser != null) {
					response.setRemainUnLockTime(omniUser.getUnlockTime());
					if (StringUtils.hasText(omniUser.getFailedLoginAttempts())) {
						response.setRemainUnLockSeconds(Integer.parseInt(omniUser.getFailedLoginAttempts()));
					}
				}
			}
		}
		log.debug("▶▶▶▶▶▶ [status check] login type : {}, login response vo : {}", LoginType.get(loginType.getType()), StringUtil.printJson(response));
		return response;
	}
	/**
	 * <pre>
	 * comment  : 사용자 상태 체크
	 */
	public LoginStepVo loginStatusCheck(final String chCd, final String loginId, final String loginPwd) {
		// 옴니에 해당 사용자 조회
		String decloginPwd = SecurityUtil.getEncodedWso2Password(SecurityUtil.getXyzValue(config.getDecryptPassphrase(), loginPwd,false));
		List<UmOmniUser> omniUserList = getOmniLoginUserList(loginId, decloginPwd);
		if (omniUserList == null || omniUserList.isEmpty()) {
			// 옴니에 없을 경우 로그인 아이디로 해당 사용자 로그인 정보 추출
			UmOmniUser omniUser = getOmniUserByLoginUserName(loginId.trim());
			if (omniUser != null) {
				// 로그인 불가능 정보 추출
				final String accountIncsNo = omniUser.getIncsNo();
				final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
				final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
				final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
				final String unlockTime = omniUser.getUnlockTime() == null ? "0" : omniUser.getUnlockTime();
				log.debug("▶▶▶▶▶ [status check] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
				if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자
					List<UmOmniUser> omniUsers = new ArrayList<>();
					// valid unlock time 이 아니면 lock 하지 않음.
					if (DateUtil.isValidUnlockTime(unlockTime)) {
						final String remainUnlockTime = DateUtil.getRemainedUnlockTime(unlockTime);
						if (StringUtils.hasText(remainUnlockTime)) {
							omniUser.setUnlockTime(remainUnlockTime);
							omniUser.setFailedLoginAttempts(Integer.toString(DateUtil.getUnlockTimeTermSeconds(unlockTime)));
							omniUsers.add(omniUser);
						}

						log.debug("▶▶▶▶▶ [status check] user status under lock time : {}, current : {}", //
								DateUtil.getDateTime(DateUtil.getUnixTimestampToDateString(unlockTime)), //
								DateUtil.getCurrentDateTimeString());
						return new LoginStepVo(LoginType.LOCK, omniUsers, null, accountIncsNo);
					} else {
						log.debug("▶▶▶▶▶ [status check] user status passed lock time, do next process.");
					}
				}
				// 경로 임시테이블에 해당 사용자 조회
				List<UmChUser> chUserList = getChannelLoginUserListByFlag(chCd, loginId, decloginPwd);
				if (chUserList == null || chUserList.isEmpty()) {
					log.debug("▶▶▶▶▶ [status check] 회원(비밀번호다름) --> {}", LoginType.NEW.getDesc());
					log.debug("▶▶▶▶▶ [status check] 기존 로그인 실패 수 --> {}", omniUser.getFailedLoginAttempts());
					try {
						if(StringUtils.isEmpty(omniUser.getFailedLoginAttempts()) || Integer.parseInt(omniUser.getFailedLoginAttempts()) <4) {
							wso2SoapApiService.postLoginFailCountByUsername(loginId, Integer.parseInt(StringUtils.isEmpty(omniUser.getFailedLoginAttempts()) || omniUser.getFailedLoginAttempts() == null ? "0" : omniUser.getFailedLoginAttempts())+1);
						}else {
							//계정 잠금. 
							wso2SoapApiService.postLoginFailCountByUsername(loginId, 0);
							wso2SoapApiService.postLockByUsername(loginId);
							wso2SoapApiService.postLoginFailaccountStateByUsername(loginId);
							wso2SoapApiService.postLoginFailunlockTimeByUsername(loginId, String.valueOf(System.currentTimeMillis()/1000 + 1800));
							
							log.debug("▶▶▶▶▶ [status check] 로그인 실패 5회 이상 계정 잠금, {}", String.valueOf(System.currentTimeMillis()/1000 + 1800));
							
							return new LoginStepVo(LoginType.PWDFAIL, null, null, null);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return new LoginStepVo(LoginType.ERROR, null, null, null);
					}
					
					return new LoginStepVo(LoginType.PWDFAIL, null, null, null);
					
				} else {
					UmChUser chUser = chUserList.get(0);
					String swtYn = chUser.getIncsWebIdSwtYn(); // 통합고객웹ID전환여부

					if ("Y".equals(swtYn)) {
						log.debug("▶▶▶▶▶ [status check] 이미 전환가입한 사용자 --> {} {}", LoginType.ALREADY_TRNS_CH.getDesc(), StringUtil.printJson(chUser));
						//return new LoginStepVo(LoginType.ALREADY_TRNS_CH, null, chUserList, Integer.toString(chUser.getIncsNo()));
						return new LoginStepVo(LoginType.PWDFAIL, null, null, null);
					} else {
						//return this.authStep.loginStep(chCd, loginId.trim(), loginPwd);
						return new LoginStepVo(LoginType.PWDFAIL, null, null, null);
					}
				}
				
			}
			return new LoginStepVo(LoginType.NEW, null, null, null);
			
		} else { // 옴니에 사용자 정보 존재함.(중복된 데이터 존재 불가함)
			UmOmniUser omniUser = omniUserList.get(0);
			
			if (omniUser != null) {
				final String resetFlag = omniUser.getUmUserPasswordReset();
//				final String incsNo = omniUser.getIncsNo();
				if (StringUtils.hasText(resetFlag)) {
					if (OmniConstants.ACCOUNT_PASSWORD_RESET.equals(resetFlag)) { // 비밀번호 초기화
						log.debug("▶▶▶▶▶ [login status check] 비밀번호 초기화 여부 : {}", resetFlag);
//						return new LoginStepVo(LoginType.PWDRESET, null, null, incsNo);
					}
				}

				// 로그인 불가능 정보 추출
				final String accountIncsNo = omniUser.getIncsNo();
				final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
				final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
				final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
				final String unlockTime = omniUser.getUnlockTime() == null ? "0" : omniUser.getUnlockTime();
				try {
					//계정 조회 시, 카운트 0으로
					wso2SoapApiService.postLoginFailCountByUsername(loginId, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.debug("▶▶▶▶▶ [login status check] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
				if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자

					List<UmOmniUser> omniUsers = new ArrayList<>();
					// valid unlock time 이 아니면 lock 하지 않음.
					if (DateUtil.isValidUnlockTime(unlockTime)) {

						final String remainUnlockTime = DateUtil.getRemainedUnlockTime(unlockTime);
						if (StringUtils.hasText(remainUnlockTime)) {
							omniUser.setUnlockTime(remainUnlockTime);
							omniUser.setFailedLoginAttempts(Integer.toString(DateUtil.getUnlockTimeTermSeconds(unlockTime)));
							omniUsers.add(omniUser);
						}

						log.debug("▶▶▶▶▶ [login status check] user status under lock time : {}, current : {}", //
								DateUtil.getDateTime(DateUtil.getUnixTimestampToDateString(unlockTime)), //
								DateUtil.getCurrentDateTimeString());
						return new LoginStepVo(LoginType.LOCK, omniUsers, null, accountIncsNo);
					} else {
						log.debug("▶▶▶▶▶ [login status check] user status passed lock time, do next process.");
					}
				}
				return new LoginStepVo(LoginType.LOGIN, null, null, null);
				//return this.authStep.loginStep(chCd, loginId.trim(), loginPwd);
			} else {
				return new LoginStepVo(LoginType.NEW, null, null, null);
			}
		}
	}
	/**
	 * comment  : 뷰티포인트 고객 로그인을 위한 사용자 정보 조회
	 */
	public List<UmOmniUser> getOmniLoginUserList(final String loginId, final String loginPwd) {

		UmOmniUser umOmniUser = new UmOmniUser();
		umOmniUser.setUmUserName(loginId);
		//umOmniUser.setUmUserPassword(SecurityUtil.getEncodedWso2Password(loginPwd));
		umOmniUser.setUmUserPassword(loginPwd);
		umOmniUser.setUmAttrName(OmniConstants.UID);
		umOmniUser.setUmAttrValue(loginId);

		return this.mgmtApiMapper.getOmniLoginUserList(umOmniUser);
	}
	public UmOmniUser getOmniUserByLoginUserName(final String userName) {
		return this.mgmtApiMapper.getOmniUserByLoginUserName(userName);
	}
	
	public List<UmChUser> getChannelLoginUserListByFlag(final String chCd, final String loginId, final String loginPwd) {
		UmChUser umChUser = new UmChUser();
		umChUser.setChCd(chCd);
		umChUser.setChcsWebId(loginId);
		umChUser.setLinPwdEc(SecurityUtil.getEncodedWso2Password(loginPwd));
		return this.mgmtApiMapper.getChannelLoginUserListByFlag(umChUser);
	}

	/**
	 * <pre>
	 * comment  : lock 어뷰징 고객 조회
	 * author   : judahye
	 * date     : 2023. 4. 12. 오후 3:09:20
	 * </pre>
	 * @param incsNo
	 * @return
	 */
	public ApiBaseResponse lockusercheck(AbusingLockVo abusingLockVo) {
		//옴니 DB 어뷰징 계정 조회 
		ApiBaseResponse response = new ApiBaseResponse();
		try {
			response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
			AbusingUserVo abusingUserVo = this.mgmtApiMapper.getAbusingUserSearch(abusingLockVo.getIncsNo());
			
			if(abusingUserVo == null) {
				response.SetResponseInfo(ResultCode.SUCCESS);
				return response;
			}
			
			/*
		000 잠긴계정
		001 정상회원이 아닌 경우, CI번호 수정 불가능한 경우
		003 통합고객번호를 잘못 입력한 경우
		007 CI번호 변경 이력이 있는 경우
			 */
			log.debug("◆◆◆◆◆◆ [lock user check] by incsNo abusingUserVo: {}", StringUtil.printJson(abusingUserVo));
			abusingLockVo.setAcctLockLogTpCd(abusingUserVo.getLockRsnCd());
			//ICITSVCOM000 코드를 가진 고객이 해제 날짜가 없을 시 lock 계정으로 간주
			if ("ICITSVCOM000".equals(abusingUserVo.getLockRsnCd()) && StringUtils.isEmpty(abusingUserVo.getLockCancDttn()) /*DateUtil.getCurrentDateTimeTerms(abusingUserVo.getLockCancDttn())<0*/) {
				response.SetResponseInfo(ResultCode.LOCK_TRUE_USER);
			}else if ("ICITSVCOM001".equals(abusingUserVo.getLockRsnCd())) {
				response.SetResponseInfo(ResultCode.LOCK_ACCESS_LIMIT_USER);
				abusingLockVo.setLockCancImpsRsnCd(abusingUserVo.getLockRsnCd());
			}else if ("ICITSVCOM007".equals(abusingUserVo.getLockRsnCd())) {
				response.SetResponseInfo(ResultCode.LOCK_ACCESS_LIMIT_USER);
				abusingLockVo.setLockCancImpsRsnCd(abusingUserVo.getLockRsnCd());
			}else if ("ICITSVCOM003".equals(abusingUserVo.getLockRsnCd())) {
				response.SetResponseInfo(ResultCode.LOCK_ACCESS_LIMIT_USER);
				abusingLockVo.setLockCancImpsRsnCd(abusingUserVo.getLockRsnCd());
			}else {
				response.SetResponseInfo(ResultCode.SUCCESS);
				return response;
			}
//			if(!"ICITSVCOM000".equals(abusingUserVo.getLockRsnCd())) {
				//사용자 세션 삭제
//				String um_user_name = this.wso2ApiMapper.getUmUserNameByIncsNo(String.valueOf(abusingLockVo.getIncsNo()));
//				ResponseEntity<String> sessionResponse = this.wso2Scim2RestApiService.removeSessionByUserName(um_user_name);
//				if(sessionResponse.getStatusCode() == HttpStatus.OK || sessionResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
//					log.debug("[changepassword] 사용자 세션 삭제 성공");
//					log.info("▶▶▶▶▶ [changepassword] chCd : {}, incsNo : {}, loginId : {}", abusingLockVo.getChCd(), abusingLockVo.getIncsNo());
//				}else {
//					log.error("[changepassword] 사용자 세션 삭제 실패");
//				}
//			}
			//로그 적재
			log.debug(abusingLockVo.getDoAction());
			if("status".equals(abusingLockVo.getDoAction())) { // /login/step로 들어올 경우. 로그 한번만 등록되기 위함
				abusingUserLockLog(abusingLockVo);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
//			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			response.SetResponseInfo(ResultCode.SUCCESS);
		}
		
		return response;
	}
	
	/**
	 * <pre>
	 * comment  : 어뷰징 고객 고객통합 변경 후 lock 해제 Update
	 * author   : judahye
	 * date     : 2023. 5. 1. 오후 2:03:45
	 * </pre>
	 * @param incsNo
	 * @return
	 */
	public ApiBaseResponse lockUpdateUser(AbusingLockVo abusingLockVo) {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		try {
			abusingLockVo.setDoAction("update");
			int result=this.mgmtApiMapper.updateAbusingUser(abusingLockVo.getIncsNo());
			//로그 적재 필요
			if(result > 0) {
				response.SetResponseInfo(ResultCode.SUCCESS);
				abusingUserLockLog(abusingLockVo);
				
//					사용자 세션 삭제
//				String um_user_name = this.wso2ApiMapper.getUmUserNameByIncsNo(String.valueOf(abusingLockVo.getIncsNo()));
//				log.debug("@@@@@"+um_user_name);
//				ResponseEntity<String> sessionResponse = this.wso2Scim2RestApiService.removeSessionByUserName(um_user_name);
//				if(sessionResponse.getStatusCode() == HttpStatus.OK || sessionResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
//					log.debug("[changepassword] 사용자 세션 삭제 성공");
//					log.info("▶▶▶▶▶ [changepassword] chCd : {}, incsNo : {}, loginId : {}", abusingLockVo.getChCd(), abusingLockVo.getIncsNo());
//				}else {
//					log.error("[changepassword] 사용자 세션 삭제 실패");
//				}
//				
			}else {
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		} catch (Exception e) {
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
		}
		return response;
	}
	
	/**
	 * <pre>
	 * comment  : 고객계정잠금로그
	 * author   : judahye
	 * date     : 2023. 5. 1. 오후 4:40:20
	 * </pre>
	 * @param abusingLogingVo
	 * @return
	 */
	public ApiBaseResponse abusingUserLockLog(AbusingLockVo abusingLogingVo) {
		ApiBaseResponse response = new ApiBaseResponse();
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		try {
			int result=this.mgmtApiMapper.abusingUserLockLog(abusingLogingVo);
			if(result > 0) {
				response.SetResponseInfo(ResultCode.SUCCESS);
			}else {
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		} catch (Exception e) {
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
		}
		return response;
	}

	/**
	 * <pre>
	 * comment  : lock 고객 DB 등록 API
	 * author   : judahye
	 * date     : 2023. 5. 2. 오후 5:42:12
	 * </pre>
	 * @param abusingUserVo
	 * @return
	 */
	public ApiBaseResponse lockuserinsert(AbusingUserVo abusingUserVo) {
		ApiBaseResponse response = new ApiBaseResponse();
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		try {
			int result=this.mgmtApiMapper.abusingUserInsert(abusingUserVo);
			if(result > 0) {
				response.SetResponseInfo(ResultCode.SUCCESS);
			}else {
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			}
		} catch (Exception e) {
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
		}
		
		return response;
	}
	
	/**
	 * <pre>
	 * comment  : web2App 채널 토큰 저장 api
	 * author   : judahye
	 * date     : 2023. 7. 14. 오전 9:02:23
	 * </pre>
	 * @param web2AppVo
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public ApiBaseResponse web2AppSendAuthKey(AuthKeyVo authKey) {
		
		ApiBaseResponse response = new ApiBaseResponse();
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		
		if(StringUtils.isEmpty(authKey.getAuthKey()) || SecurityUtil.getXValue(authKey.getAuthKey()) == null || StringUtils.isEmpty(SecurityUtil.getXValue(authKey.getAuthKey()))) {
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		//authkey 복호화 해서 json으로 담기
		String resultValue = SecurityUtil.getXValue(authKey.getAuthKey()).replaceAll("\\\\", "");
		if(StringUtils.isEmpty(resultValue) || !StringUtils.hasText(resultValue)) {
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		log.debug("▶▶▶▶▶▶ [loginWeb2AppStep] Wep2App AuthKey resultValue : "+resultValue);
		JsonObject jsonObj= new JsonObject();
		try {
			jsonObj = new JsonParser().parse(resultValue).getAsJsonObject();
		} catch (Exception e) {
			log.debug("▶▶▶▶▶▶ [loginWeb2AppStep] Wep2App AuthKey : Json 형식에 맞지 않습니다.");
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		Web2AppVo web2AppVo = new Web2AppVo();
		try {
			web2AppVo.setUuid(jsonObj.get("uuid").getAsString());
			web2AppVo.setAccessToken(jsonObj.get("accessToken").getAsString());
		} catch (NullPointerException e) {
			//값이 없을 경우
			log.debug("▶▶▶▶▶▶ [loginWeb2AppStep] 필수 값 누락");
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}catch (Exception e) {
			//값이 없을 경우
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		log.debug("▶▶▶▶▶▶ [loginWeb2AppStep] Wep2App AuthKey : {}", StringUtil.printJson(web2AppVo));
		
		try {
			int result=this.mgmtApiMapper.web2AppSendAuthKey(web2AppVo);
			//로그 적재 필요
			if(result > 0) {
				response.SetResponseInfo(ResultCode.SUCCESS);
			}else {
				response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			}
		} catch (Exception e) {
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
		}
		return response;
	}
	
	/**
	 * <pre>
	 * comment  : ID / PW 로 옴니회원 플랫폼에 저장된 사용자 정보 조회
	 * author   : judahye
	 * date     : 2022. 11. 11. 오후 4:04:19
	 * </pre>
	 * @param checkUserInfoVo
	 * @return
	 */
	public OmniSearchResponse checkUserInfoPwd(CheckUserInfoVo checkUserInfoVo) {
		log.info("▶▶▶▶▶ [checkuserinfopwd] checkUserInfoVo : {}", checkUserInfoVo);
		OmniSearchResponse response = new OmniSearchResponse();
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		
		if(StringUtils.isEmpty(checkUserInfoVo.getChCd()) || StringUtils.isEmpty(checkUserInfoVo.getLoginId()) || StringUtils.isEmpty(checkUserInfoVo.getLoginPw())) {
			response.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
			return response;
		}
		if( StringUtils.isEmpty(SecurityUtil.getXyzValue(config.getDecryptPassphrase(), checkUserInfoVo.getLoginPw(),false))) {
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		
		StatusCheckResponse statusResponse = statusCheck2(checkUserInfoVo);
		log.info("▶▶▶▶▶ [checkuserinfopwd] statusResponse : {}", statusResponse);
		//-20/-40 비밀번호 틀림, 0 존재하지 않는 회원, -5 잠김, -10 탈퇴, -15 비밀번호 초기화, -30 비밀번호 캠페인
		if("-20".equals(statusResponse.getResultCode())) {
			response.SetResponseInfo(ResultCode.USER_PWD_FAILED);
			return response;
		}else if ("0".equals(statusResponse.getResultCode())) {
			response.SetResponseInfo(ResultCode.USER_SEARCH_NOT_FOUND);
			return response;
		}else if ("-5".equals(statusResponse.getResultCode())) {
			//20231103 비밀번호 실패 횟수 5->7 변경 및 안내 문구 변경
			response.SetResponseInfo(ResultCode.USER_UNLOCK_FAILED); 
			UmOmniUser omniUser = getOmniUserByLoginUserName(checkUserInfoVo.getLoginId().trim());
			if(omniUser != null) {
				String decloginPwd = SecurityUtil.getXyzValue(config.getDecryptPassphrase(), checkUserInfoVo.getLoginPw(),false);
				if(SecurityUtil.compareWso2Password(omniUser.getUmUserPassword(),decloginPwd)){ //올바른 패스워드 입력 시
					response.SetResponseInfo(ResultCode.USER_UNLOCK_TRUE); 
				}
			}
			return response;
		}
		String wso2encPwd = SecurityUtil.getEncodedWso2Password(SecurityUtil.getXyzValue(config.getDecryptPassphrase(),checkUserInfoVo.getLoginPw(),false));
		if(wso2encPwd == null) {
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
			return response;
		}
		List<UmOmniUser> omniUsers = getOmniLoginUserList(checkUserInfoVo.getLoginId(), wso2encPwd);
		
		omniUsers = (omniUsers == null) ? Collections.emptyList() : omniUsers;
		final int omnicount = omniUsers.size();
		if(omnicount>0) {
			final UmOmniUser omniUser = omniUsers.get(0);
			
			final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
			final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
			final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
			final String accountDormancy = omniUser.getUmUserDormancy(); // 휴면 사용자
			log.info("▶▶▶▶▶ [checkuserinfopwd] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
			final String accountLastPasswordUpdate = omniUser.getLastPasswordUpdate(); // 마지막으로 비밀번호 변경한 날짜(UNIXTIME)
			if ((StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState))) { // 잠김사용자
				omniUsers.add(omniUser);
				//20231103 비밀번호 실패 횟수 5->7 변경 및 안내 문구 변경 및 30분 제한 제거
				response.SetResponseInfo(ResultCode.USER_UNLOCK_FAILED); 
				String decloginPwd = SecurityUtil.getEncodedWso2Password(SecurityUtil.getXyzValue(config.getDecryptPassphrase(), checkUserInfoVo.getLoginPw(),false));
				if(SecurityUtil.compareWso2Password(omniUsers.get(0).getUmUserPassword(),decloginPwd)){ //올바른 패스워드 입력 시
					response.SetResponseInfo(ResultCode.USER_UNLOCK_TRUE); 
				}
				return response;
			}
			if (StringUtil.isTrue(accountDisabled)) { // 탈퇴사용자
				response.SetResponseInfo(ResultCode.USER_DISABLED);
				OmniUserVo omniUserVo = new OmniUserVo();
				omniUserVo.setDisabledDate(DateUtil.getUnixTimestampToDateString(omniUser.getDisabledDate(), DateUtil.DATETIME_FORMAT));
				omniUserVo.setAccountDisabled(omniUser.getAccountDisabled());
				omniUserVo.setDisabledDate(omniUser.getDisabledDate());
				response.setUserVo(omniUserVo);
				return response;
			}
			
			UserVo uservo = new UserVo();
			uservo.setUserName(checkUserInfoVo.getLoginId());
			uservo.setUserPassword(SecurityUtil.getEncodedWso2Password(SecurityUtil.getXyzValue(config.getDecryptPassphrase(),checkUserInfoVo.getLoginPw(),false)));
			OmniUserVo omniUserVo = this.mgmtApiMapper.getUserByUserId(uservo);
			log.debug("▶▶▶▶▶ [checkuserinfopwd] login process user dormancy ? {}", StringUtil.isTrue(accountDormancy));
			if (StringUtil.isTrue(accountDormancy)) { // 휴면 사용자
				omniUserVo.setUserDormancy("true");
				omniUserVo.setFullName("휴면고객");
			}
			if (StringUtils.hasText(accountLastPasswordUpdate)) {
				omniUserVo.setLastPasswordUpdate(DateUtil.getUnixTimestampToDateString(omniUser.getLastPasswordUpdate(), DateUtil.DATETIME_FORMAT));
			}
			log.info("▶▶▶▶▶ [checkuserinfo] omniUserVo : {}", StringUtil.printJson(omniUserVo));
			response.setUserVo(omniUserVo);
			response.SetResponseInfo(ResultCode.SUCCESS);
		}else {
			UmOmniUser omniUser = getOmniUserByLoginUserName(checkUserInfoVo.getLoginId().trim());
			if("-5".equals(statusResponse.getResultCode())){
				
				//20231103 비밀번호 실패 횟수 5->7 변경 및 안내 문구 변경
				if(omniUser != null) {
					response.SetResponseInfo(ResultCode.USER_UNLOCK_FAILED); 
					String decloginPwd = SecurityUtil.getEncodedWso2Password(SecurityUtil.getXyzValue(config.getDecryptPassphrase(), checkUserInfoVo.getLoginPw(),false));
					if(SecurityUtil.compareWso2Password(omniUsers.get(0).getUmUserPassword(),decloginPwd)){ //올바른 패스워드 입력 시
						response.SetResponseInfo(ResultCode.USER_UNLOCK_TRUE); 
					}
				}
				return response;
			}
			if(omniUser != null) {
				response.SetResponseInfo(ResultCode.USER_PWD_FAILED);
				return response;
			}else {
				response.SetResponseInfo(ResultCode.USER_SEARCH_NOT_FOUND);
				return response;
			}
		}
		return response;
	}

	public StatusCheckResponse statusCheck2(final CheckUserInfoVo checkUserInfoVo) {
		final String chCd = checkUserInfoVo.getChCd();
		final String encLoginid = checkUserInfoVo.getLoginId();
		final String encLoginpw = checkUserInfoVo.getLoginPw();

		log.debug("▶▶▶▶▶▶ [status check] status check(id,pwd) : {}, {}", encLoginid, encLoginpw);

		final String loginId = encLoginid;
		final String loginPwd = encLoginpw;
		
		final LoginStepVo loginStepVo = loginStatusCheck2(chCd, loginId, loginPwd);
		LoginType loginType = loginStepVo.getLoginType();
		log.debug("▶▶▶▶▶▶ [status check] login type : {}, login step vo : {}", LoginType.get(loginType.getType()), StringUtil.printJson(loginStepVo));

		if (loginType == LoginType.DORMANCYFAIL) {
			throw new OmniException("시스템 오류가 발생하였습니다. 관리자에게 문의하시기 바랍니다.");
		}
		
		final int type = loginType.getType();
		StatusCheckResponse response = new StatusCheckResponse();
		response.setStatus(type);
		response.setResultCode(Integer.toString(type));
		response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		response.setXincsno(SecurityUtil.setXyzValue(loginStepVo.getIncsNo()));

		log.debug("▶▶▶▶▶▶ [status check] login type : {}, login response vo : {}", LoginType.get(loginType.getType()), StringUtil.printJson(response));
		return response;
	}
	/**
	 * <pre>
	 * comment  : 사용자 상태 체크
	 */
	public LoginStepVo loginStatusCheck2(final String chCd, final String loginId, final String loginPwd) {
		// 옴니에 해당 사용자 조회
		String decloginPwd = SecurityUtil.getEncodedWso2Password(SecurityUtil.getXyzValue(config.getDecryptPassphrase(), loginPwd,false));
		List<UmOmniUser> omniUserList = getOmniLoginUserList(loginId, decloginPwd);
		if (omniUserList == null || omniUserList.isEmpty()) {
			// 옴니에 없을 경우 로그인 아이디로 해당 사용자 로그인 정보 추출
			UmOmniUser omniUser = getOmniUserByLoginUserName(loginId.trim());
			if (omniUser != null) {
				// 로그인 불가능 정보 추출
				final String accountIncsNo = omniUser.getIncsNo();
				final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
				final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
				final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
				log.debug("▶▶▶▶▶ [status check] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
				if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자
					List<UmOmniUser> omniUsers = new ArrayList<>();
					omniUsers.add(omniUser);
					return new LoginStepVo(LoginType.LOCK, omniUsers, null, accountIncsNo);
				}
				// 경로 임시테이블에 해당 사용자 조회
				List<UmChUser> chUserList = getChannelLoginUserListByFlag(chCd, loginId, decloginPwd);
				if (chUserList == null || chUserList.isEmpty()) {
					log.debug("▶▶▶▶▶ [status check] 회원(비밀번호다름) --> {}", LoginType.NEW.getDesc());
					log.debug("▶▶▶▶▶ [status check] 기존 로그인 실패 수 --> {}", omniUser.getFailedLoginAttempts());
					try {
						if(StringUtils.isEmpty(omniUser.getFailedLoginAttempts()) || Integer.parseInt(omniUser.getFailedLoginAttempts()) <6) {
							wso2SoapApiService.postLoginFailCountByUsername(loginId, Integer.parseInt(StringUtils.isEmpty(omniUser.getFailedLoginAttempts()) || omniUser.getFailedLoginAttempts() == null ? "0" : omniUser.getFailedLoginAttempts())+1);
						}else {
							//계정 잠금. 
							wso2SoapApiService.postLoginFailCountByUsername(loginId, 0);
							wso2SoapApiService.postLockByUsername(loginId);
							wso2SoapApiService.postLoginFailaccountStateByUsername(loginId);
							wso2SoapApiService.postLoginFailunlockTimeByUsername(loginId, String.valueOf(System.currentTimeMillis()/1000 + 1800));
							log.debug("▶▶▶▶▶ [status check] 로그인 실패 7회 이상 계정 잠금, {}", String.valueOf(System.currentTimeMillis()/1000 + 1800));
							return new LoginStepVo(LoginType.PWDFAIL, null, null, null);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return new LoginStepVo(LoginType.ERROR, null, null, null);
					}
					return new LoginStepVo(LoginType.PWDFAIL, null, null, null);
				} else {
					UmChUser chUser = chUserList.get(0);
					String swtYn = chUser.getIncsWebIdSwtYn(); // 통합고객웹ID전환여부
					if ("Y".equals(swtYn)) {
						log.debug("▶▶▶▶▶ [status check] 이미 전환가입한 사용자 --> {} {}", LoginType.ALREADY_TRNS_CH.getDesc(), StringUtil.printJson(chUser));
						return new LoginStepVo(LoginType.PWDFAIL, null, null, null);
					} else {
						return new LoginStepVo(LoginType.PWDFAIL, null, null, null);
					}
				}
			}
			return new LoginStepVo(LoginType.NEW, null, null, null);
		} else { // 옴니에 사용자 정보 존재함.(중복된 데이터 존재 불가함)
			UmOmniUser omniUser = omniUserList.get(0);
			if (omniUser != null) {
				final String resetFlag = omniUser.getUmUserPasswordReset();
//				final String incsNo = omniUser.getIncsNo();
				if (StringUtils.hasText(resetFlag)) {
					if (OmniConstants.ACCOUNT_PASSWORD_RESET.equals(resetFlag)) { // 비밀번호 초기화
						log.debug("▶▶▶▶▶ [login status check] 비밀번호 초기화 여부 : {}", resetFlag);
//						return new LoginStepVo(LoginType.PWDRESET, null, null, incsNo);
					}
				}
				// 로그인 불가능 정보 추출
				final String accountIncsNo = omniUser.getIncsNo();
				final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
				final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
				final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
				try {
					//계정 조회 시, 카운트 0으로
					wso2SoapApiService.postLoginFailCountByUsername(loginId, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.debug("▶▶▶▶▶ [login status check] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
				if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자
					List<UmOmniUser> omniUsers = new ArrayList<>();
					omniUsers.add(omniUser);
					return new LoginStepVo(LoginType.LOCK, omniUsers, null, accountIncsNo);
				}
				return new LoginStepVo(LoginType.LOGIN, null, null, null);
			} else {
				return new LoginStepVo(LoginType.NEW, null, null, null);
			}
		}
	}

}
