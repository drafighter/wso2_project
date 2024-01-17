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
 * Author	          : judahye
 * Date   	          : 2022. 10. 7..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v2.joinon.service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.api.common.service.ChannelService;
import com.amorepacific.oneap.api.common.service.CommonService;
import com.amorepacific.oneap.api.common.service.RestApiService;
import com.amorepacific.oneap.api.exception.ApiBusinessException;
import com.amorepacific.oneap.api.v1.channel.service.ChannelApiService;
import com.amorepacific.oneap.api.v1.mgmt.service.MgmtApiService;
import com.amorepacific.oneap.api.v1.mgmt.validator.MgmtApiValidator;
import com.amorepacific.oneap.api.v1.mgmt.vo.DupIdVo;
import com.amorepacific.oneap.api.v1.wso2.service.Wso2RusmSoapApiService;
import com.amorepacific.oneap.api.v1.wso2.service.Wso2Scim2RestApiService;
import com.amorepacific.oneap.api.v2.join.service.ApiOnlineProcessStep;
import com.amorepacific.oneap.api.v2.join.service.CustomerApiService;
import com.amorepacific.oneap.api.v2.join.vo.JoinRequest;
import com.amorepacific.oneap.api.v2.joinon.mapper.JoinOnApiMapper;
import com.amorepacific.oneap.api.v2.joinon.vo.ChPwdDateResponse;
import com.amorepacific.oneap.api.v2.joinon.vo.CheckOnlineIdVo;
import com.amorepacific.oneap.api.v2.joinon.vo.IdCheckResponse;
import com.amorepacific.oneap.api.v2.joinon.vo.IdCheckVo;
import com.amorepacific.oneap.api.v2.joinon.vo.JoinOnResultCode;
import com.amorepacific.oneap.api.v2.joinon.vo.JoinOnUserResponse;
import com.amorepacific.oneap.api.v2.joinon.vo.OnlineSign2Vo;
import com.amorepacific.oneap.api.v2.joinon.vo.OnlineSignCancelVo;
import com.amorepacific.oneap.api.v2.joinon.vo.OnlineSignVo;
import com.amorepacific.oneap.api.v2.joinon.vo.PasswdChangeResponse;
import com.amorepacific.oneap.api.v2.joinon.vo.PasswordChangeVo;
import com.amorepacific.oneap.api.v2.joinon.vo.PwdChDateResponse;
import com.amorepacific.oneap.api.v2.joinon.vo.PwdChDateVo;
import com.amorepacific.oneap.api.v2.terms.service.TermsService;
import com.amorepacific.oneap.api.v2.terms.vo.TermsVo;
import com.amorepacific.oneap.common.check.CheckResponse;
import com.amorepacific.oneap.common.check.Checker;
import com.amorepacific.oneap.common.check.actor.CheckActor;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.LocaleUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.Marketing;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.BpChangePasswordRequest;
import com.amorepacific.oneap.common.vo.api.BpChangePasswordResponse;
import com.amorepacific.oneap.common.vo.api.BpUserData;
import com.amorepacific.oneap.common.vo.api.ChangePasswordData;
import com.amorepacific.oneap.common.vo.api.CipAthtVo;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.InitPasswordData;
import com.amorepacific.oneap.common.vo.join.JoinApplyRequest;
import com.amorepacific.oneap.common.vo.sms.SmsIdata;
import com.amorepacific.oneap.common.vo.sms.SmsOdata;
import com.amorepacific.oneap.common.vo.sms.SmsRequest;
import com.amorepacific.oneap.common.vo.sms.SmsRequestHeader;
import com.amorepacific.oneap.common.vo.sms.SmsRequestInput;
import com.amorepacific.oneap.common.vo.sms.SmsResponse;
import com.amorepacific.oneap.common.vo.sms.SmsResponseHeader;
import com.amorepacific.oneap.common.vo.sms.SmsVo;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;
import com.amorepacific.oneap.common.vo.user.UserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v2.joinon.service 
 *    |_ JoinOnApiService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 10. 7.
 * @version : 1.0
 * @author  : judahye
 */
@Slf4j
@Service
public class JoinOnApiService {
	
	@Value("${omni.api.endpoint.initpasswordcurrentpassword}")
	private String initPasswordCurrentPassword; // 비밀번호 초기화 api url
	
	// 고객상세조회 API URL
	@Value("${external.cip.api.getcicuemcuinfrbyincsno}")
	private String getcicuemcuinfrbyincsno;
	
	@Autowired
	private CustomerApiService customerApiService;
	
	// REST API 호출 서비스
	@Autowired
	private RestApiService restApiService;
	
	@Autowired
	private MgmtApiService mgmtApiService;

	@Autowired
	private SystemInfo systemInfo;
	
	@Autowired
	private JoinOnApiMapper joinOnApiMapper;
	
	@Autowired
	private ChannelApiService channelApiService;
	
	@Autowired
	private Wso2Scim2RestApiService wso2Scim2RestApiService;
	
	@Autowired
	private Wso2RusmSoapApiService wso2RusmSoapApiService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ApiOnlineProcessStep apiOnlineProcessStep;
	
	@Autowired
	private TermsService termsService;
	
	@Autowired
	private ChannelService channelService;
	
	@Value("${sms.url}")
	private String smsUrl;
	
	@Value("${sms.source}")
	private String smsSource;

	@Value("${sms.id}")
	private String smsId;
	
	@Value("${sms.username}")
	private String smsUsername;

	@Value("${sms.userpassword}")
	private String smsUserpassword;
	
	@Value("${sms.applclcd}")
	private String applClCd;
	
	@Value("${sms.pltfclcd}")
	private String pltfClCd;
	
	@Value("${sms.eaifl}")
	private String eaiFl;
	
	@Value("${sms.callback}")
	private String smsCallback;
	
	@Value("${external.bp.api.checkonlineid}")
	private String checkBpOnlineId; // 뷰티포인트 회원아이디유효성체크 api url
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	
	/**
	 * <pre>
	 * comment  : 비밀번호 변경일자 확인
	 * author   : judahye
	 * date     : 2022. 10. 7. 오후 2:10:09
	 * </pre>
	 * @param pwdChDateVo
	 * @return
	 */
	public PwdChDateResponse pwdChDate(PwdChDateVo pwdChDateVo) {
		log.debug("▶▶▶▶▶▶ [pwdChDate] PwdChDateVo : {}", StringUtil.printJson(pwdChDateVo));

		PwdChDateResponse res = new PwdChDateResponse();
		
		if(StringUtils.isEmpty(pwdChDateVo.getChCd()) || StringUtils.isEmpty(pwdChDateVo.getUserId()) || StringUtils.isEmpty(pwdChDateVo.getApiKey())) {
			res.SetResponseInfo(JoinOnResultCode.UNKNOWN_ERROR);
			return res;
		}
		try {
			//SHA-512(chCd+userId)
			String checkKey1 = SecurityUtil.getEncodedSHA512Password(pwdChDateVo.getChCd()+pwdChDateVo.getUserId());
			String checkKey2 = checkKey1.toUpperCase();
			if(!checkKey1.equals(pwdChDateVo.getApiKey()) && !checkKey2.equals(pwdChDateVo.getApiKey())) {
				log.debug("▶▶▶▶▶▶ [pwdChDate] 잘못된 apiKey : {}", checkKey1);
				res.SetResponseInfo(JoinOnResultCode.JOINON_API_KEY);
				return res; 
			}
			
			UmOmniUser umWso2User = new UmOmniUser();
			umWso2User.setUmAttrName("umUserName");
			umWso2User.setUmAttrValue(pwdChDateVo.getUserId());
			List<UmOmniUser> omniUsers= this.joinOnApiMapper.getOmniJoinUserList(umWso2User);
			
			if(omniUsers.size()<1) {
				log.debug("▶▶▶▶▶▶ [pwdChDate] 옴니 회원 미존재");
				res.SetResponseInfo(JoinOnResultCode.JOINON_ID_INVALID);
				return res;
			}
			UmOmniUser omniUser = omniUsers.get(0);
			log.debug("▶▶▶▶▶▶ [pwdChDate] omniUser : {}", StringUtil.printJson(omniUser));
			
			long timestamp = Long.parseLong(omniUser.getLastPasswordUpdate());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			date.setTime(timestamp);
			String cdate = sdf.format(date);
			
			res.SetResponseInfo(JoinOnResultCode.JOINON_SUCCESS);
			res.setChDate(cdate);
			
		} catch (Exception e) {
			res.SetResponseInfo(JoinOnResultCode.UNKNOWN_ERROR);
		}
		
		return res;
	}
	
	/**
	 * <pre>
	 * comment  : 마지막 PW 미변경 동의일자 수정
	 * author   : judahye
	 * date     : 2022. 10. 11. 오전 9:36:47
	 * </pre>
	 * @param pwdChDateVo
	 * @return
	 */
	public ChPwdDateResponse chPwdDate(PwdChDateVo pwdChDateVo) {
		log.debug("▶▶▶▶▶▶ chPwdDate - PwdChDateVo : {}", StringUtil.printJson(pwdChDateVo));

		ChPwdDateResponse res = new ChPwdDateResponse();
		
		if(StringUtils.isEmpty(pwdChDateVo.getChCd()) || StringUtils.isEmpty(pwdChDateVo.getUserId()) || StringUtils.isEmpty(pwdChDateVo.getApiKey())) {
			res.SetResponseInfo(JoinOnResultCode.UNKNOWN_ERROR);
			return res;
		}
		
		// /pwdstatus 참고. 동일 비밀번호 update하여 갱신일 초기화함
		try {
			//SHA-512(chCd+userId)
			String checkKey1 = SecurityUtil.getEncodedSHA512Password(pwdChDateVo.getChCd()+pwdChDateVo.getUserId());
			String checkKey2 = checkKey1.toUpperCase();
			if(!checkKey1.equals(pwdChDateVo.getApiKey()) && !checkKey2.equals(pwdChDateVo.getApiKey())) {
				log.debug("▶▶▶▶▶▶ [pwdChDate] 잘못된 apiKey : {}", checkKey1);
				res.SetResponseInfo(JoinOnResultCode.JOINON_API_KEY);
				return res; 
			}
			
			UmOmniUser umWso2User = new UmOmniUser();
			umWso2User.setUmAttrName("umUserName");
			umWso2User.setUmAttrValue(pwdChDateVo.getUserId());
			List<UmOmniUser> omniUsers= this.joinOnApiMapper.getOmniJoinUserList(umWso2User);
			
			if(omniUsers.size()<1) {
				log.debug("▶▶▶▶▶▶ [pwdChDate] 옴니 회원 미존재");
				res.SetResponseInfo(JoinOnResultCode.JOINON_ID_INVALID);
				return res;
			}
			UmOmniUser omniUser = omniUsers.get(0);
			log.debug("▶▶▶▶▶▶ [pwdChDate] omniUser : {}", StringUtil.printJson(omniUser));
			
			UserData userData = new UserData();
			userData.setLoginId(omniUser.getUmUserName());
			userData.setIncsNo(omniUser.getIncsNo());
			userData.setPassword(omniUser.getUmUserPassword());
			
			if (StringUtils.isEmpty(userData.getIncsNo()) || "0".equals(userData.getIncsNo())) {
				res.SetResponseInfo(JoinOnResultCode.JOINON_ID_INVALID);
				return res;
			}
			
			// 경로구분코드 - 필수아님
			InitPasswordData initPwdVo = new InitPasswordData();
			initPwdVo.setChCd(pwdChDateVo.getChCd());
			initPwdVo.setIncsNo(Integer.parseInt(omniUser.getIncsNo()));
			initPwdVo.setLoginId(omniUser.getUmUserName());
			initPwdVo.setPassword(omniUser.getUmUserPassword());
			initPwdVo.setMustchange("N");
			
			ApiBaseResponse response = initPasswordCurrentPassword(initPwdVo);
			log.debug("▶▶▶▶▶▶ [update last password] Password Change Response : {}", StringUtil.printJson(response));
			
			if(!response.getResultCode().equals(ResultCode.SUCCESS.getCode())) {
				res.SetResponseInfo(JoinOnResultCode.UNKNOWN_ERROR);
				return res;
			}
			
			int result = 0;
			result = this.joinOnApiMapper.updateUmUserPassword(userData); //옴니 회원가입 update
			if(!(result > 0)) {
				res.SetResponseInfo(JoinOnResultCode.UNKNOWN_ERROR);
				return res;
			}else {
				res.SetResponseInfo(JoinOnResultCode.JOINON_SUCCESS);		//성공
				log.debug("▶▶▶▶▶▶ [chPwdDate] PW 미변경 동의 일자 수정 완료");
			}
			
		} catch (Exception e) {
			res.SetResponseInfo(JoinOnResultCode.UNKNOWN_ERROR);
		}
		
		return res;
	}
	
	/**
	 * <pre>
	 * comment  : 온라인 회원가입 API
	 * 해당 API를 이용하여 온라인 채널(000, 030, 031, 043, 070, 100) 및 뷰티포인트 WEBDB 가입 시켜준다
	 * author   : judahye
	 * date     : 2022. 10. 12. 오후 9:27:28
	 * </pre>
	 * @param onlineSign
	 * @return
	 */
	public PasswdChangeResponse onlineSign(OnlineSignVo onlineSign) {
		//이미 가입된 고객통합번호로 조회하여  및 뷰포+옴니 가입
		//비밀번호 없을 시, 자동 생성 후 안내
		//고객통합번호 숫자 검사
		log.debug("▶▶▶▶▶▶ onlinesign - onlineSign : {}", StringUtil.printJson(onlineSign));

		PasswdChangeResponse res = new PasswdChangeResponse();
		res.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		
		if(StringUtils.isEmpty(onlineSign.getIncsNo()) || StringUtils.isEmpty(onlineSign.getCstmid())) {
			res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SYSTEM_ERROR);
			return res;
		}
		try {			
			if(StringUtils.isEmpty(onlineSign.getChCd())) { //채널코드 없을 경우 030
				onlineSign.setChCd("030");
			}
			//옴니에 연동된 채널코드가 아닌 경우 030 
			final String chCdParam = onlineSign.getChCd();
			List<Channel> channelList = this.channelService.getChannels();
			channelList = channelList.stream() //
					.filter(ch -> chCdParam.equals(ch.getChCd())) //
					.collect(Collectors.toList());
			if (channelList == null || channelList.isEmpty()) {
				log.debug(" channel is empty, invalid channel code : {}", chCdParam);
				onlineSign.setChCd("030");
			}
			//ID 형식 체크
			CheckOnlineIdVo checkidVo = new CheckOnlineIdVo();
			checkidVo.setCstmid(onlineSign.getCstmid());
			checkidVo.setIncsNo(onlineSign.getIncsNo());
			JoinOnUserResponse idcheckRes = checkOnlineId(checkidVo);
			
			if("080".equals(idcheckRes.getRsltCd())) {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_VALIDCHECK_FAIL);
				res.setResult("ERROR");
				return res;
			}else if ("010".equals(idcheckRes.getRsltCd()) || "020".equals(idcheckRes.getRsltCd())) {
				//ID 중복 이거나, 탈퇴 1달 미만일 경우 ID 중복으로 반환(070)
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_ALREADY_EXIST);
				res.setResult("DUPLICATE");
				return res;
			}else if ("040".equals(idcheckRes.getRsltCd())) { //이미 등록되어 있을 시 050 반환 // 통합ID 체크
				if("이미 가입된 통합회원입니다".equals(idcheckRes.getRsltMsg())) {
					res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ALREADY_EXIST);
					res.setResult("DUPLICATE");
					return res;
				}
				res.setRsltCd("050");
				res.setRsltMsg(idcheckRes.getRsltMsg());
				res.setResult("DUPLICATE");
				return res;
			}else if("060".equals(idcheckRes.getRsltCd())) {
				//고객통합번호가 숫자가 아닐 시, 060 반환
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_NOTFOUND);
				res.setResult("INFOFAIL");
				return res;
			}
				
			Customer customer = getcicuemcuinfrbyincsno(onlineSign.getIncsNo()); //고객통합 조회
			if(customer == null) {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SYSTEM_ERROR);
				res.setResult("INFOFAIL");
				return res;
			}
			if(!StringUtils.isEmpty(customer.getChcsNo())) {
				res.setRsltCd("050");
				res.setRsltMsg(customer.getChcsNo());
				res.setResult("DUPLICATE");
				return res;
			}
			
			//090 반환 통합회원정보와 실명인증 정보 일치x
			CustInfoVo custInfovo = new CustInfoVo();
			custInfovo.setCiNo(customer.getCiNo());
			custInfovo.setIncsNo(customer.getIncsNo());
			CustInfoResponse custrespon = customerApiService.getCustList(custInfovo);
			if(custrespon == null || custrespon.getCicuemCuInfTcVo() == null || custrespon.getCicuemCuInfTcVo().length == 0) {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SYSTEM_ERROR);
				res.setResult("INFOFAIL");
				return res;
			}
			Customer customer2 = custrespon.getCicuemCuInfTcVo()[0];
			if(!customer.getCustNm().equals(customer2.getCustNm())) {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_NOTMATCH);
				res.setResult("INFOFAIL");
				log.debug("▶▶▶▶▶▶ onlinesign - 실명인증 정보 다름 : {}, {} ",customer.getCustNm(),customer2.getCustNm() );
				return res;
			}
			
			String pwd=null;
			if(StringUtils.isEmpty(onlineSign.getPswd())) {
				//랜덤 패스워드 : skag4762(앞4자리 난수+휴대번호 뒷자리)
				pwd = randomStr("", "011", 4) + customer.getCellTlsn();
			}else {
				pwd = onlineSign.getPswd();
			}
			log.debug("▶▶▶▶▶▶ onlinesign - customer : {}", StringUtil.printJson(customer));			
			if ("ICITSVCOM001".equals(customer.getRsltCd()) || "ICITSVCOM002".equals(customer.getRsltCd())) { // ICITSVCOM001 : 통합고객이 존재하지 않습니다,  ICITSVCOM002	 : 조회된 데이터가 없습니다.	
				//고객통합만 가입한 계정. 탈퇴 한달 체크 ?
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_NOTFOUND);
				res.setResult("INFOFAIL");
				return res;
			}
			
			//온라인 수신 동의 -> 2번 째, 회원가입에 넣기
			//String smsReceiveType = "Y".equals(request.getParameter("smsReceiveType")) ? "Y" : "N";
			
			//온라인 채널 가입(xoo 경우로 하면 되려나...)
			customer.setChcsNo(onlineSign.getCstmid());
			String signresult = joinSetting(customer, pwd, onlineSign.getChCd());
			log.debug("▶▶▶▶▶▶ onlinesign - signresult : {}", StringUtil.printJson(signresult));
			
			if("000".equals(signresult)) { //생성 코드별로 에러 분기처리
				//생성 완료 되면, 고객통합번호에 있는 휴대폰 번호로 비밀번호 SMS 발송
				int status=0;
				SmsVo result = new SmsVo();
				
				String profile = systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				
				if(StringUtils.isEmpty(onlineSign.getPswd())) { //패스워드 null 일 시, 문자 전송
					if(!"prod".equals(profile)) {
						result = joinonPasswordSend("01000000000", pwd);
					}else{
						result = joinonPasswordSend(customer.getCellTidn()+customer.getCellTexn()+customer.getCellTlsn(), pwd);
					}
				}
				
				if(result != null) {
					status = result.getStatus();
				}
				
				if(status !=1) {
					res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SUCCESS);
					res.setResult("SUCCESS");
					log.debug("▶▶▶▶▶▶ onlinesign - SUCCESS-ONLINESIGN - SMSERROR");
					return res;
				}else {
					res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SUCCESS);
					res.setResult("SUCCESS");
					log.debug("▶▶▶▶▶▶ onlinesign - SUCCESS-ONLINESIGN");
					return res;
				}
			}else {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SYSTEM_ERROR);
				res.setResult("ERROR");
				return res;
			}

		} catch (Exception e) {
			res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SYSTEM_ERROR);
			res.setResult("ERROR");
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * <pre>
	 * comment  : 온라인 회원가입 망취소 API
	 * 온라인 회원가입 호출 후 취소해야 하는 경우 호출하면
	 *온라인 채널(000, 030, 031, 043, 070, 100) 및 뷰티포인트 WEBDB 가입 된 내용이 삭제 처리된다
	 *단 해당 API는 가입중 호출이 되어야 되는 내용으로 온라인 회원가입 호출후 5분이내에만 유효하다
	 * author   : judahye
	 * date     : 2022. 10. 12. 오후 9:27:54
	 * </pre>
	 * @param onlineSign
	 * @return
	 */
	public JoinOnUserResponse onlineSignCancel(OnlineSignCancelVo onlineSignCancelVo) {
		
		//옴니/뷰포/고객통합약관 삭제
		
		
		return null;
		
	}
	
	/**
	 * <pre>
	 * comment  : 온라인 ID 유효성 체크 API
	 * author   : judahye
	 * date     : 2022. 10. 13. 오후 2:27:55
	 * </pre>
	 * @param checkOnlineIdVo
	 * @return
	 */
	public JoinOnUserResponse checkOnlineId(CheckOnlineIdVo checkOnlineIdVo) {
		log.debug("▶▶▶▶▶▶ checkonlineid - checkOnlineIdVo : {}", StringUtil.printJson(checkOnlineIdVo));

		JoinOnUserResponse res = new JoinOnUserResponse();
		
		if((StringUtils.isEmpty(checkOnlineIdVo.getIncsNo()) && StringUtils.isEmpty(checkOnlineIdVo.getCstmid()))) {
			res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_PARAM_EMPTY);
			return res;
		}
		if(!StringUtils.isEmpty(checkOnlineIdVo.getIncsNo()) && StringUtils.isEmpty(checkOnlineIdVo.getCstmid())){ //통합고객번호만 받았을 경우
			UmOmniUser umWso2User = new UmOmniUser();
			umWso2User.setUmAttrName("incsNo");
			umWso2User.setUmAttrValue(checkOnlineIdVo.getIncsNo());
			List<UmOmniUser> omniUsers= this.joinOnApiMapper.getOmniJoinUserList(umWso2User);
			if(omniUsers.size()<1) {
				log.debug("▶▶▶▶▶▶ [checkonlineid] 옴니 회원 미존재"); //온라인 ID가 없을 경우 000 반환
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SUCCESS);
				return res;
			}else {
				UmOmniUser omniUser = omniUsers.get(0);
				log.debug("▶▶▶▶▶▶ [checkonlineid] omniUser : {}", StringUtil.printJson(omniUser));
				if(!StringUtils.isEmpty(omniUser.getUmUserName())){
					//고객통합번호로 온라인 데이터가 존재할 경우 040			
					//통합회원번호가 이미 있는 경우 사용중인 아이디를 rsltMsg 값에 셋팅
					res.setRsltCd("040");
					res.setRsltMsg(omniUser.getUmUserName());
					log.debug("◆◆◆◆◆◆ [checkonlineid] disabled user id : 온라인 ID 존재");
					return res;
				}
				//고객통합번호가 1달이내 탈퇴 이력이 있을 시, 탈퇴 반환
				if(checkDisabled(omniUser.getDisabledDate())) {
					res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_USER_DISABLED);
					log.debug("◆◆◆◆◆◆ [checkonlineid] disabled user id : 1달 이내 탈퇴 이력 有");
					return res;
				}
				log.debug("◆◆◆◆◆◆ [checkonlineid] 이미 가입된 회원");
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ALREADYEXIST);
				res.setRsltMsg(omniUser.getUmUserName());
				return res;
			}
		}
		Pattern p = Pattern.compile("(^[A-Za-z0-9]{4,12}$)");
		Matcher m = p.matcher(checkOnlineIdVo.getCstmid());
		if(StringUtils.isEmpty(checkOnlineIdVo.getCstmid()) && !m.find()) {
			res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_VALIDCHECK_FAIL);
			return res;
		}
		if(!StringUtils.isEmpty(checkOnlineIdVo.getIncsNo()) &&!StringUtil.isNumeric(checkOnlineIdVo.getIncsNo())) { //060
			res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_INVALID_PARAMS);
			return res;
		}
		
		DupIdVo dupIdVo = new DupIdVo();
		dupIdVo.setLoginId(checkOnlineIdVo.getCstmid());
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MgmtApiValidator.checkDuplicateId(response, dupIdVo);

		if (checkCommonValidation(response)) {

			CheckResponse chkResponse = new Checker.Builder().checkType(CheckActor.Type.valueOf("ID")).checkValue(dupIdVo.getLoginId()).build().check();
			if (chkResponse.getStatus() == CheckActor.SUCCESS) {
				try {
					boolean rtn = mgmtApiService.isDisabledUser(dupIdVo.getLoginId());
					if (rtn) {
						log.debug("◆◆◆◆◆◆ [checkonlineid] disabled user id : {}", dupIdVo.getLoginId());
						res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_EXIST);
						return res;
					}
					
					boolean rtn1 = mgmtApiService.checkDuplicateId(dupIdVo);
					if (!rtn1) {
						res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SUCCESS);
					} else {
						res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_EXIST);
						log.debug("◆◆◆◆◆◆ [checkonlineid] disabled user id : 아이디 중복");
						return res;
					}
					
					//뷰포, 고객통합, 채널 중복 ID 검사
					// 온라인 회원ID 중복 체크
					BpUserData searchUserData = new BpUserData();
					//searchUserData.setIncsNo(checkOnlineIdVo.getIncsNo());
					searchUserData.setCstmid(checkOnlineIdVo.getCstmid());
					ApiResponse createResponse = customerApiService.checkBpOnlineId(searchUserData);
					log.debug("▶▶▶▶▶▶ [checkonlineid] createResponse : {}", StringUtil.printJson(createResponse));
					
					if("010".equals(createResponse.getRsltCd())) {
						log.debug("◆◆◆◆◆◆ [checkonlineid] disabled user id : 아이디 중복");
						res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_EXIST);
						return res;
					}

					if(!StringUtils.isEmpty(checkOnlineIdVo.getIncsNo())) {
						UmOmniUser umWso2User = new UmOmniUser();
						umWso2User.setUmAttrName("incsNo");
						umWso2User.setUmAttrValue(checkOnlineIdVo.getIncsNo());
						List<UmOmniUser> omniUsers= this.joinOnApiMapper.getOmniJoinUserList(umWso2User);
						if(omniUsers.size()<1) {
							log.debug("▶▶▶▶▶▶ [checkonlineid] 옴니 회원 미존재");
						}else {
							UmOmniUser omniUser = omniUsers.get(0);
							log.debug("▶▶▶▶▶▶ [checkonlineid] omniUser : {}", StringUtil.printJson(omniUser));
							if ("040".equals(createResponse.getRsltCd())) {
								if(omniUsers.size()>0) {
									UmOmniUser omniUserdisable = omniUsers.get(0);
									//고객통합번호가 1달이내 탈퇴 이력이 있을 시, 탈퇴 반환
									if(checkDisabled(omniUserdisable.getDisabledDate())) {
										res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_USER_DISABLED);
										log.debug("◆◆◆◆◆◆ [checkonlineid] disabled user id : 1달 이내 탈퇴 이력 有");
										return res;
									}
								}
								log.debug("◆◆◆◆◆◆ [checkonlineid] 이미 가입된 회원");
								res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ALREADYEXIST);
								res.setRsltMsg(omniUser.getUmUserName());
								return res;
							}
							if(!StringUtils.isEmpty(omniUser.getUmUserName())){
								//고객통합번호로 온라인 데이터가 존재할 경우 040			
								//통합회원번호가 이미 있는 경우 사용중인 아이디를 rsltMsg 값에 셋팅
								//res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ALREADYEXIST);
								res.setRsltCd("040");
								res.setRsltMsg(omniUser.getUmUserName());
								log.debug("◆◆◆◆◆◆ [checkonlineid] disabled user id : 온라인 ID 존재");
								return res;
							}
							
							//고객통합번호가 1달이내 탈퇴 이력이 있을 시, 탈퇴 반환
							if(checkDisabled(omniUser.getDisabledDate())) {
								res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_USER_DISABLED);
								log.debug("◆◆◆◆◆◆ [checkonlineid] disabled user id : 1달 이내 탈퇴 이력 有");
								return res;
							}
						}
					}
					
				} catch (ApiBusinessException e) {
					// AP B2C 표준 로그 설정
					LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
							LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
					LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
					LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
					log.error("api.onlinesign.ApiBusinessException = {}", e.getMessage());
					LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
					res.SetResponseInfo(JoinOnResultCode.UNKNOWN_ERROR); //에러 코드 확인 필요
				}
			} else {
				// 체크실패
				if (chkResponse.getCode().equalsIgnoreCase(OmniConstants.ID_SIZE_FAIL)) {
					res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_VALIDCHECK_FAIL);
				} else { 
					res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_VALIDCHECK_FAIL);
				}
			}
		}
		log.debug("[check duplicate ID] response code : {}", response.getResultCode());
		
		return res;
	}
	
	/**
	 * comment  : 필수값 관련 코드 (REQ_REQUIRED_PARAM_EMPTY, REQ_INVALID_PARAM)는  공통 체크
	 */
	private boolean checkCommonValidation(ApiBaseResponse response) {
		return (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode()) && !ResultCode.REQ_INVALID_PARAM.getCode().equals(response.getResultCode()));
	}
	
	private boolean checkDisabled(String date) {
		boolean result = false;
		try {
			String nowdate = DateUtil.getCurrentDateTime();
			Date format1 = new SimpleDateFormat("yyyyMMddHHmmss").parse(nowdate);
			Date format2 = new SimpleDateFormat("yyyyMMddHHmmss").parse(StringUtils.isEmpty(date) ? "00000000000000" : date);
			long diffDays = (format1.getTime() - format2.getTime()) / 1000 / (24*60*60)  ;
			if(diffDays<30) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("◆◆◆◆◆◆ [checkDisabled] : 에러");
		}
		return result;
	}
	
	/**
	 * <pre>
	 * comment  : 온라인 회원ID 중복 체크 API
	 * author   : judahye
	 * date     : 2022. 10. 13. 오후 4:23:08
	 * </pre>
	 * @param idcheck
	 * @return
	 */
	public IdCheckResponse idCheck(IdCheckVo idcheck) {
		log.debug("▶▶▶▶▶▶ idcheck - IdCheckVo : {}", StringUtil.printJson(idcheck));

		IdCheckResponse res = new IdCheckResponse();
		
		if(StringUtils.isEmpty(idcheck.getId())) {
			res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_OK);
			return res;
		}
		
		DupIdVo dupIdVo = new DupIdVo();
		dupIdVo.setLoginId(idcheck.getId());
		try {
			boolean rtn = mgmtApiService.isDisabledUser(dupIdVo.getLoginId());
			if (rtn) {
				log.debug("◆◆◆◆◆◆ [checkonlineid] disabled user id : {}", dupIdVo.getLoginId());
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_NOK);
				return res;
			}
			boolean rtn1 = mgmtApiService.checkDuplicateId(dupIdVo);
			if (!rtn1) {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_OK);
			} else {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_NOK);
				log.debug("◆◆◆◆◆◆ [checkonlineid] disabled user id : 아이디 중복");
			}
		} catch (Exception e) {
			res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_OK);
		}
		
		return res;
	}
	
	public PasswdChangeResponse passwdChange(PasswordChangeVo passwordChangeVo) {
		
		log.debug("▶▶▶▶▶▶ passwdChange - passwordChangeVo : {}", StringUtil.printJson(passwordChangeVo));

		PasswdChangeResponse res = new PasswdChangeResponse();
		try {
			if(StringUtils.isEmpty(passwordChangeVo.getCstmId()) || StringUtils.isEmpty(passwordChangeVo.getPasswd()) || StringUtils.isEmpty(passwordChangeVo.getPasswd_new())) {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_REQ_PARAM_EMPTY);
				return res;
			}
			
			UmOmniUser umWso2User = new UmOmniUser();
			umWso2User.setUmAttrName("umUserName");
			umWso2User.setUmAttrValue(passwordChangeVo.getCstmId());
			List<UmOmniUser> omniUsers= this.joinOnApiMapper.getOmniJoinUserList(umWso2User);
			
			if(omniUsers.size()<1) {
				log.debug("▶▶▶▶▶▶ [passwdChange] 옴니 회원 미존재");
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_PWD_ERROR);
				return res;
			}
			UmOmniUser omniUser = omniUsers.get(0);
			log.debug("▶▶▶▶▶▶ [passwdChange] omniUser : {}", StringUtil.printJson(omniUser));
			
			// 존재하는 사용자인지 체크
			if (omniUser != null) {
				// 탈퇴 사용자인지 체크
				boolean isDisabled = mgmtApiService.isDisabledUser(passwordChangeVo.getCstmId());
				if (!isDisabled) {
					if(!SecurityUtil.getEncodedWso2Password(passwordChangeVo.getPasswd()).equals(omniUser.getUmUserPassword())) {
						res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_FAIL_CONFIRM);
						log.error("[changepassword] input PW not same current PW");
						return res;
					}
					if(passwordChangeVo.getPasswd().equals(passwordChangeVo.getPasswd_new())) {
						res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_FAIL_SAME);
						return res;
					}
					
					// 현재 비밀번호 확인
					String currPw = passwordChangeVo.getPasswd();
					String dbPw = mgmtApiService.getPassword(passwordChangeVo.getCstmId());
					
					if (!SecurityUtil.compareWso2Password(dbPw, currPw)) {
						// AP B2C 표준 로그 설정
						LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_REQUIRED_VALUE_MISSING_FAIL, null, null, null,
								LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
						LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
						LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
						log.error("[changepassword] input PW not same current PW");
						LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
						res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_FAIL_CONFIRM);
						return res;
					}
					
					ResponseEntity<String> pwdResponse;
					
					if (StringUtils.hasText(passwordChangeVo.getPasswd())) { // 이전 비밀번호있을 경우 -> 이쪽으로 타야함 
						pwdResponse = this.wso2RusmSoapApiService.postPasswordByUsername(passwordChangeVo.getCstmId(), passwordChangeVo.getPasswd(), passwordChangeVo.getPasswd_new());
					} else {
						pwdResponse = this.wso2RusmSoapApiService.patchPasswordByUsername(passwordChangeVo.getCstmId(), passwordChangeVo.getPasswd_new());
					}
					
					// WSO2 API 성공 후
					if (pwdResponse.getStatusCode() == HttpStatus.OK) {
						ChangePasswordData changePasswordData = new ChangePasswordData();
						changePasswordData.setLoginId(passwordChangeVo.getCstmId());
						changePasswordData.setIncsNo(Integer.parseInt(omniUser.getIncsNo()));
						changePasswordData.setChCd("030");
						changePasswordData.setCurrentPassword(passwordChangeVo.getPasswd());
						changePasswordData.setChangePassword(passwordChangeVo.getPasswd_new());
						// 고객통합 비밀번호 변경하기
						// 고객통합 비밀번호 변경하면 뷰티포인트에도 전파하나 시차가 있음.
						boolean success = changeIntegratedUserPassword(changePasswordData);
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
							
							BpChangePasswordResponse response = this.channelApiService.beautyPointChangePassword(bpResponse, bpChangePasswordRequest);
							
							if ("000".equalsIgnoreCase(response.getRsltCd())) {
								log.debug("[changepassword] 뷰티포인트 비밀번호 변경 성공 : {}", response.getRsltMsg());
							}else if ("401".equalsIgnoreCase(response.getRsltCd())) {
								log.debug("[changepassword] 뷰티포인트 비밀번호 변경 실패(기 등록된 패스워드) : {}", StringUtil.printJson(response));
								res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_FAIL_SAME);
								return res;
							} else {
								log.debug("[changepassword] 뷰티포인트 비밀번호 변경 실패 : {}", StringUtil.printJson(response));
							}
						}
						
						String profile = this.systemInfo.getActiveProfiles()[0];
						profile = StringUtils.isEmpty(profile) ? "dev" : profile;
						profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
						
						ResponseEntity<String> sessionResponse = this.wso2Scim2RestApiService.removeSessionByUserName(omniUser.getUmUserName());
						if(sessionResponse.getStatusCode() == HttpStatus.OK || sessionResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
							log.debug("[changepassword] 사용자 세션 삭제 성공");
							log.info("▶▶▶▶▶ [changepassword] chCd : {}, incsNo : {}, loginId : {}", changePasswordData.getChCd(), changePasswordData.getIncsNo(), changePasswordData.getLoginId());
						}
						else {
							log.error("[changepassword] 사용자 세션 삭제 실패");
						}
						
						res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SUCCESS);
					} else {
						// AP B2C 표준 로그 설정
						LogInfo.setLogInfo(OmniStdLogConstants.OMNI, OmniStdLogConstants.OMNI_API, OmniStdLogConstants.API_SERVER_ERROR, null, null, null,
								LogInfoConstants.LOG_APPSIDE_ADMIN_CD, null, null);
						LogInfo.setLogInfoLvl(LogInfoConstants.LOG_LEVEL_ERROR_CD);
						LogInfo.setLogInfoAttr("WEB", "PCWeb"); // PCWeb, MobileWeb, MobileApp 지정
						log.error("api.changepassword.Exception = {}", pwdResponse.getBody());
						LogInfo.clearInfo(); // AP B2C 표준 로그 초기화
						res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_PWD_ERROR);
					}
				} else {
					res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_PWD_ERROR);
				}
			} else {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_PWD_ERROR);
			}
		} catch (Exception e) {
			res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_PWD_ERROR);
		}
		return res;
	}
	
	
	/**
	 * <pre>
	 * comment  : 온라인 회원가입 API
	 * 해당 API를 이용하여 온라인 채널(000, 030, 031, 043, 070, 100) 및 뷰티포인트 WEBDB 가입 시켜준다
	 * author   : judahye
	 * date     : 2022. 10. 12. 오후 9:27:28
	 * </pre>
	 * @param onlineSign
	 * @return
	 */
	public PasswdChangeResponse onlineSign2(OnlineSign2Vo onlineSign2) {
		//이미 가입된 고객통합번호로 조회하여  및 뷰포+옴니 가입
		//비밀번호 없을 시, 자동 생성 후 안내
		//고객통합번호 숫자 검사
		log.debug("▶▶▶▶▶▶ onlinesign2 - onlineSign2 : {}", StringUtil.printJson(onlineSign2));

		PasswdChangeResponse res = new PasswdChangeResponse();
		
		if(StringUtils.isEmpty(onlineSign2.getIncsNo()) || StringUtils.isEmpty(onlineSign2.getCstmid()) || StringUtils.isEmpty(onlineSign2.getChcd())) {
			res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SYSTEM_ERROR);
			return res;
		}
		try {
			if(StringUtils.isEmpty(onlineSign2.getChcd())) { //채널코드 없을 경우 030
				onlineSign2.setChcd("030");
			}
			//옴니에 연동된 채널코드가 아닌 경우 030 
			final String chCdParam = onlineSign2.getChcd();
			List<Channel> channelList = this.channelService.getChannels();
			channelList = channelList.stream() //
					.filter(ch -> chCdParam.equals(ch.getChCd())) //
					.collect(Collectors.toList());
			if (channelList == null || channelList.isEmpty()) {
				log.debug(" channel is empty, invalid channel code : {}", chCdParam);
				onlineSign2.setChcd("030");
			}
			//ID 형식 체크
			CheckOnlineIdVo checkidVo = new CheckOnlineIdVo();
			checkidVo.setCstmid(onlineSign2.getCstmid());
			checkidVo.setIncsNo(onlineSign2.getIncsNo());
			JoinOnUserResponse idcheckRes = checkOnlineId(checkidVo);
			
			if("080".equals(idcheckRes.getRsltCd())) {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_VALIDCHECK_FAIL);
				res.setResult("ERROR");
				return res;
			}else if ("010".equals(idcheckRes.getRsltCd()) || "020".equals(idcheckRes.getRsltCd())) {
				//ID 중복 이거나, 탈퇴 1달 미만일 경우 ID 중복으로 반환(070)
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ID_ALREADY_EXIST);
				res.setResult("DUPLICATE");
				return res;
			}else if ("040".equals(idcheckRes.getRsltCd())) { //옴니에 ID 등록되어 있을 시 050 반환 // 통합ID 체크
				if("이미 가입된 통합회원입니다".equals(idcheckRes.getRsltMsg())) {
					res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_ALREADY_EXIST);
					res.setResult("DUPLICATE");
					return res;
				}
				res.setRsltCd("050");
				res.setRsltMsg(idcheckRes.getRsltMsg());
				res.setResult("DUPLICATE");
				return res;
			}else if("060".equals(idcheckRes.getRsltCd())) {
				//고객통합번호가 숫자가 아닐 시, 060 반환
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_NOTFOUND);
				res.setResult("INFOFAIL");
				return res;
			}
				
			Customer customer = getcicuemcuinfrbyincsno(onlineSign2.getIncsNo()); //고객통합 조회
			if(customer == null) {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SYSTEM_ERROR);
				res.setResult("INFOFAIL");
				return res;
			}
			
			if(!StringUtils.isEmpty(customer.getChcsNo())) {
				res.setRsltCd("050");
				res.setRsltMsg(customer.getChcsNo());
				res.setResult("DUPLICATE");
				return res;
			}
			
			//090 반환 통합회원정보와 실명인증 정보 일치x
			CustInfoVo custInfovo = new CustInfoVo();
			custInfovo.setCiNo(customer.getCiNo());
			custInfovo.setIncsNo(customer.getIncsNo());
			CustInfoResponse custrespon = customerApiService.getCustList(custInfovo);
			if(custrespon == null || custrespon.getCicuemCuInfTcVo() == null || custrespon.getCicuemCuInfTcVo().length == 0) {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SYSTEM_ERROR);
				res.setResult("INFOFAIL");
				return res;
			}
			Customer customer2 = custrespon.getCicuemCuInfTcVo()[0];
			if(!customer.getCustNm().equals(customer2.getCustNm())) {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_NOTMATCH);
				res.setResult("INFOFAIL");
				log.debug("▶▶▶▶▶▶ onlinesign - 실명인증 정보 다름 : {}, {} ",customer.getCustNm(),customer2.getCustNm() );
				return res;
			}
			
			//랜덤 패스워드 : skag4762(앞4자리 난수+휴대번호 뒷자리)
			String pwd = randomStr("", "011", 4) + customer.getCellTlsn();

			log.debug("▶▶▶▶▶▶ onlinesign - customer : {}", StringUtil.printJson(customer));			
			if ("ICITSVCOM001".equals(customer.getRsltCd()) || "ICITSVCOM002".equals(customer.getRsltCd())) { // ICITSVCOM001 : 통합고객이 존재하지 않습니다,  ICITSVCOM002	 : 조회된 데이터가 없습니다.	
				//고객통합만 가입한 계정. 탈퇴 한달 체크 ?
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_NOTFOUND);
				res.setResult("INFOFAIL");
				return res;
			}
			
			//온라인 수신 동의
			String smsReceiveType = "Y".equals(onlineSign2.getSmsReceiveType()) ? "Y" : "N";
			
			customer.setChcsNo(onlineSign2.getCstmid());
			String signresult = joinSetting(customer, pwd, onlineSign2.getChcd());
			log.debug("▶▶▶▶▶▶ onlinesign - signresult : {}", StringUtil.printJson(signresult));
			
			if("000".equals(signresult)) { //생성 코드별로 에러 분기처리
				//생성 완료 되면, 고객통합번호에 있는 휴대폰 번호로 비밀번호 SMS 발송
				int status=0;
				SmsVo result = new SmsVo();
				
				String profile = systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				
				if(!"prod".equals(profile)) {
					result = joinonPasswordSend("01000000000", pwd); 
				}else{
					result = joinonPasswordSend(customer.getCellTidn()+customer.getCellTexn()+customer.getCellTlsn(), pwd);
				}
				
				if(result != null) {
					status = result.getStatus();
				}
				
				if(status !=1) {
					res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SUCCESS);
					log.debug("▶▶▶▶▶▶ onlinesign - SUCCESS-ONLINESIGN - SMSERROR");
					res.setResult("SUCCESS");
					return res;
				}else {
					res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SUCCESS);
					log.debug("▶▶▶▶▶▶ onlinesign - SUCCESS-ONLINESIGN");
					res.setResult("SUCCESS");
					return res;
				}
			}else {
				res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SYSTEM_ERROR);
				res.setResult("ERROR");
				return res;
			}

		} catch (Exception e) {
			res.SetResponseInfo(JoinOnResultCode.JOINON_JOIN_SYSTEM_ERROR);
			res.setResult("ERROR");
			e.printStackTrace();
		}
		
		return res;
	}
	
	
	/**
	 * comment  : 비밀번호 초기화 API 호출 (기존 패스워드로 업데이트)
	 */
	public ApiBaseResponse initPasswordCurrentPassword(final InitPasswordData initPwdVo) {

		log.debug("▶▶▶▶▶▶ init password current password : {}", StringUtil.printJson(initPwdVo));
		final HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(OmniConstants.XAPIKEY, this.config.apiKey());

		ResponseEntity<ApiBaseResponse> response = this.restApiService.post(initPasswordCurrentPassword, headers, initPwdVo, ApiBaseResponse.class);

		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			ApiBaseResponse ar = new ApiBaseResponse();
			ar.setResultCode("ICITSVCOM999");
			return ar;
		}
		return response.getBody();
	}	
	
	/**
	 * comment  : 고객통합번호로 고객통합 조회
	 */
	public Customer getcicuemcuinfrbyincsno(String incsNo) {
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.hasText(incsNo)) {
			params.put("incsNo", incsNo);
		}
		
		CipAthtVo cipAthtVo = CipAthtVo.builder().build();
		params.put("cipAthtVo", cipAthtVo);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		ResponseEntity<Customer> response = this.restApiService.post(this.getcicuemcuinfrbyincsno, headers, json, Customer.class);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			return null;
		}
		
		Customer customer = response.getBody();
			
		return customer;
		
	}

	public boolean changeIntegratedUserPassword(ChangePasswordData changePasswordData) {
		return this.mgmtApiService.changeIntegratedUserPassword(changePasswordData);
	}
	
	public SmsVo joinonPasswordSend(String sendmobile, String password) {
		log.debug("▶▶▶▶▶ [send sms eai] sms password ");
		SmsVo vo = new SmsVo();
		if (StringUtils.hasText(password)) {
			String sendmessage = this.messageSource.getMessage("[뷰티포인트]  새로운비밀번호는["+password+"]입니다.", new String[] { password }, LocaleUtil.getLocale());
			if (StringUtils.hasText(sendmobile) && StringUtils.hasText(sendmessage)) {
				SmsResponse response = this.sendSmsEai(sendmobile, sendmessage);
				log.debug("▶▶▶▶▶ [send sms eai] response : {} --> {}", StringUtil.printJson(response));

				String rtnCode = response.getRESPONSE().getHEADER().getRTN_CODE();
				rtnCode = StringUtils.isEmpty(rtnCode) ? response.getRESPONSE().getHEADER().getRTN_TYPE() : rtnCode;
				if (OmniConstants.SEND_SMS_EAI_SUCCESS.equals(rtnCode)) {
					vo.setStatus(1);
					return vo; // 발송 성공
				}
			} else {
				vo.setStatus(-1);
				return vo; // 발송 INF 실패
			}
		}
		return vo;
	}
	
	public SmsResponse sendSmsEai(final String phoneNo, final String sendMessage) {
		final SmsRequestHeader smsheader = new SmsRequestHeader(this.smsSource);
		final SmsRequestInput smsinput = new SmsRequestInput(phoneNo, sendMessage);
		smsinput.setREQDATE(DateUtil.getCurrentDateString("yyyy-MM-dd HH:mm:ss"));
		smsinput.setSERIALNUM(OmniUtil.getSerialNumber());
		smsinput.setID(this.smsId);
		smsinput.setCALLBACK(this.smsCallback);
		smsinput.setAPPL_CL_CD(this.applClCd);
		smsinput.setPLTF_CL_CD(this.pltfClCd);
		smsinput.setEAI_FL(this.eaiFl);
		final SmsIdata smsidata = new SmsIdata(smsheader, smsinput);
		final SmsRequest smsrequest = new SmsRequest(smsidata);
		return sendSmsEai(smsrequest);
	}
	
	public SmsResponse sendSmsEai(final SmsRequest request) {
		
		log.debug("▶▶▶▶▶▶ sms eai request : {}", StringUtil.printJson(request));
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		final String jsonBody = gson.toJson(request);

		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		final String auth = SecurityUtil.getBasicAuthorizationBase64(this.smsUsername, this.smsUserpassword);
		headers.add("Authorization", auth);
		ResponseEntity<String> resp = this.restApiService.post(this.smsUrl, headers, jsonBody, String.class);
		if (resp == null || (resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)) {
			SmsResponse response = new SmsResponse();
			SmsOdata odata = new SmsOdata();
			SmsResponseHeader header = new SmsResponseHeader();
			header.setRTN_CODE("F");
			header.setRTN_TYPE("F");
			header.setRTN_MSG("sms send fail");
			odata.setHEADER(header);
			response.setRESPONSE(odata);
			return response;
		}
		return gson.fromJson(resp.getBody(), SmsResponse.class);
	}
	
	//auth join setting , joinController /setcomplete
	public String joinSetting(Customer customer, String pwd, String rqchCd) {
		final String chcd = rqchCd; 

		Channel channel=null;
		final String chCdParam = chcd;

		List<Channel> channelList = this.channelService.getChannels();
		channelList = channelList.stream() //
				.filter(ch -> chCdParam.equals(ch.getChCd())) //
				.collect(Collectors.toList());
		if (channelList == null || channelList.isEmpty()) {
			log.debug(" channel is empty, invalid channel code : {}", chCdParam);
			channel = this.channelService.getChannel("030");
		} else {
			channel = this.channelService.getChannel(chCdParam);
		}
//		try {
//			channel = this.commonService.getChannel(chcd);
//		} catch (ApiBusinessException e) {
//			log.debug("★★★★★★★★★★ channel is empty, invalid channel code : {}", chcd);
//		}
		String incsNo = customer.getIncsNo();

		if (StringUtils.hasText(incsNo)) {
			if ("0".equals(incsNo)) {
				incsNo = "";
			}
		}
		JoinRequest joinRequest = new JoinRequest();
		JoinApplyRequest joinApplyRequest = new JoinApplyRequest();

		joinRequest.setJoinType("35"); // 35(O X X) 일때는 경로도 등록해야함.
		
		joinRequest.setUnm(customer.getCustNm());
		joinRequest.setLoginid(customer.getChcsNo());
		joinRequest.setLoginpassword(pwd);
		joinRequest.setGender(customer.getSxclCd());
		joinRequest.setPhone(customer.getCellTidn()+customer.getCellTexn()+customer.getCellTlsn());
		joinRequest.setBirth(customer.getAthtDtbr());
		joinRequest.setCi(customer.getCiNo());
		joinRequest.setNational(customer.getFrclCd());
		joinRequest.setChcd(chcd);	
		//joinRequest.setOffLine(OmniUtil.isOffline(channel));
		joinRequest.setIncsno(incsNo);
		joinRequest.setIntegrateid(joinApplyRequest.getIntegrateid());
		if (StringUtils.hasText(incsNo)) {
			joinApplyRequest.setIncsno(incsNo);
		}

		List<String> bpterms = joinApplyRequest.getBpterms();
		List<String> bptcatcds = joinApplyRequest.getBpTcatCds();
		List<String> bptncvnos = joinApplyRequest.getBpTncvNos();
		List<String> terms = joinApplyRequest.getTerms();
		List<String> tcatcds = joinApplyRequest.getTcatCds(); // 약관동의 내역 약관동의코드
		List<String> tncvnos = joinApplyRequest.getTncvNos(); // 약관동의 내역 약관번호

		String bptermsarr[] = OmniUtil.getListToArray(bpterms);
		String bptcatcdsarrs[] = OmniUtil.getListToArray(bptcatcds);
		String bptncvnosarrs[] = OmniUtil.getListToArray(bptncvnos);

		String termarrs[] = OmniUtil.getListToArray(terms);
		String tcatcdarrs[] = OmniUtil.getListToArray(tcatcds);
		String tncvnosarrs[] = OmniUtil.getListToArray(tncvnos);

		List<String> marketings = joinApplyRequest.getMarketing();
		List<String> marketingChcds = joinApplyRequest.getMarketingChcd();

		String marketingsarr[] = OmniUtil.getListToArray(marketings);
		String marketingchcdsarr[] = OmniUtil.getListToArray(marketingChcds);

		// 수신동의
		List<Marketing> joinMarketings = new ArrayList<>();
		if (marketings != null && !marketings.isEmpty()) {
			Marketing agree = null;
			for (int i = 0; i < marketingsarr.length; i++) {
				agree = new Marketing();
				agree.setChCd(marketingchcdsarr[i]);
				agree.setSmsAgree(marketingsarr[i].equals("on") ? "Y" : "N");
				joinMarketings.add(agree);
			}
		}

		joinRequest.setMarketings(joinMarketings);

		// 뷰포 약관동의
		List<Terms> joinBpTerms = new ArrayList<>();
		if (bpterms != null && !bpterms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < bptermsarr.length; i++) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn(bptermsarr[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(bptcatcdsarrs[i]);
				term.setTncvNo(bptncvnosarrs[i]);
				term.setChgChCd(OmniConstants.JOINON_CHCD);
				joinBpTerms.add(term);
			}
		}
		joinRequest.setBpterms(joinBpTerms);
		
		// 경로약관동의
		List<Terms> joinTerms = new ArrayList<>();
		if (terms != null && !terms.isEmpty()) {
			Terms term = null;
			for (int i = 0; i < termarrs.length; i++) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn(termarrs[i].equals("on") ? "Y" : "N");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(tcatcdarrs[i]);
				term.setTncvNo(tncvnosarrs[i]);
				term.setChgChCd(channel.getChCd());
				joinTerms.add(term);
			}
		}

		// 통합약관도 등록
		TermsVo termsVo = new TermsVo();
		if (OmniUtil.isOffline(channel)) {
			termsVo.setChCd(ChannelPairs.getOnlineCd(channel.getChCd()));
		} else {
			termsVo.setChCd(channel.getChCd());
		}
		List<TermsVo> corpTerms = this.termsService.getCorpTerms(termsVo);
		if (corpTerms != null && !corpTerms.isEmpty()) {
			Terms term = null;
			for (TermsVo corpterm : corpTerms) {
				term = new Terms();
				if (StringUtils.hasText(incsNo)) {
					term.setIncsNo(Integer.parseInt(incsNo)); // 고객통합 플랫폼 등록 후에 받아와서 설정해야함.
				}
				term.setTncAgrYn("Y");
				term.setTncaChgDt(DateUtil.getCurrentDate());
				term.setTcatCd(corpterm.getTcatCd());
				term.setTncvNo(corpterm.getTncvNo());
				term.setChgChCd(corpterm.getChCd());
				joinTerms.add(term);
			}
		}
		joinRequest.setTerms(joinTerms);
		
		BaseResponse response = null;
		log.debug("▶▶▶▶▶▶ [joinon-join setting] request settings : {}", StringUtil.printJson(joinRequest));
		response = this.apiOnlineProcessStep.registCustomerProcess(joinRequest);
		
		log.debug("▶▶▶▶▶▶ [joinon-join setting] result : {}", StringUtil.printJson(response));
		

		if (ResultCode.SUCCESS.getCode().equals(response.getResultCode())) {
			
			return "000";
		} else {

			if (OmniUtil.isOffline(channel)) {
			} else {
				if ("ICITSVCOM004".equals(response.getResultCode())) { // 존재하는 고객
					return "/info-exist"; // 존재하는 고객이면 알림 주고 로그인으로 이동
				} else if ("ICITSVCOM008".equals(response.getResultCode())) { // 망취소
					log.debug("▶▶▶▶▶▶ [joinon-join setting] result  - 망취소 : {}", StringUtil.printJson(response.getResultCode()));
					return "info/api_error";
				} else if (ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getResultCode())) {
					return "info/channel_withdraw";
				} else {
					return "info/api_error";
				}
			}
		}
		// 가입처리 끝
		return "finish";
	}
	
	
	/**
	 * <pre>
	 * comment  : 랜덤 문자열 생성
	 * author   : judahye
	 * date     : 2022. 10. 27. 오전 9:34:35
	 * </pre>
	 * @param prefix : 랜덤 문자열 앞에 고정적으로 들어갈 문자열
	 * @param gubn : 구분자 ( 111 )를 조합하여 랜덤문자열에 포함시킬 유형을 결정한다.
	 * @param length : 생성될 랜덤문자열의 길이 을 지정한다.
	 * @return
	 */
	public static String randomStr( String prefix, String gubn, int length ) {

		if ( gubn == null || gubn.length() == 0 )
			return prefix;

		String temp1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String temp2 = "abcdefghijklmnopqrstuvwxyz";
		String temp3 = "0123456789";
		String [] temp = {temp1, temp2, temp3};

		String seedStr = "";
		StringBuffer rndStr = new StringBuffer();

		for ( int i=0; (i<gubn.length()) && (i<3); i++ ) {
			char c = gubn.charAt( i );
			if ( c == '1' )
				seedStr += temp[i];
		}

		Date dt = new Date();
		Random rnd = new Random( dt.getTime() );
		int rndLen = length - trim(prefix).length();
		for( int j=0; j<rndLen; j++ ) {

			int index = Math.abs(rnd.nextInt()) % seedStr.length();
			rndStr.append( seedStr.charAt(index) );
		}

		return trim(prefix) + rndStr.toString();
	}	
	public static String trim(String str) {
		return trim(str, "");
	}
	public static String trim(String str, String defaultStr) {
		return StringUtils.isEmpty(str) ? defaultStr : StringUtils.trimWhitespace(str);
	}
	
	
}
