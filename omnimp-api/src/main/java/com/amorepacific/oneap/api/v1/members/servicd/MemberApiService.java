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
 * Date   	          : 2022. 3. 10..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.members.servicd;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.api.common.service.RestApiService;
import com.amorepacific.oneap.api.v1.members.vo.CicuemCuInfQcVo;
import com.amorepacific.oneap.api.v1.members.vo.SsgCheckVo;
import com.amorepacific.oneap.api.v1.members.vo.SsgResponse;
import com.amorepacific.oneap.api.v1.members.vo.SsgUnLinkResponse;
import com.amorepacific.oneap.api.v1.members.vo.SsgUnLinkVo;
import com.amorepacific.oneap.api.v1.members.vo.SsgUserVo;
import com.amorepacific.oneap.api.v1.members.vo.UserVo;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.CustbyChCsNoResponse;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.member.service 
 *    |_ MemberApiService.java
 *    https://dev-one-ap.amorepacific.com/api/v1/members/checkapuser
 * </pre>
 *
 * @desc :
 * @date : 2022. 3. 10.
 * @version : 1.0
 * @author : judahye
 */
@Slf4j
@Service
public class MemberApiService {
		
	@Value("${external.cip.api.getcicuemcuinfrlist}")
	private String checkapuser;
	
	@Value("${external.cip.api.membership.getCustbyChCsNo}")
	private String getCustbyChCsNo;
	
	@Value("${external.cip.api.getcicuemcuinfrbyincsno}")
	private String getcicuemcuinfrbyincsno;
	
	@Autowired
	private RestApiService restApiService;
	
	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();	

	public SsgResponse checkApUser(final SsgUserVo ssgUserVo) throws Exception {
		SsgResponse ssgResponse=new SsgResponse();
		ssgResponse.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
				try {
					log.debug("◆◆◆◆◆◆ [checkApUser]  : {}", StringUtil.printJson(ssgUserVo));
					
					final HttpHeaders headers = new HttpHeaders();
					final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
					headers.setContentType(mediaType);
					headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
					Map<String, Object> params = new HashMap<String, Object>();
					
					if(!StringUtils.hasText(ssgUserVo.getChCd())) {	//채널 코드 필수 체크
						ssgResponse.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
						return ssgResponse;
					}
					
					if(StringUtils.hasText(ssgUserVo.getCiNo()) && ssgUserVo.getCiNo().length()==88) {
						params.put("ciNo", ssgUserVo.getCiNo());
					}else if(StringUtils.hasText(ssgUserVo.getFullName()) && StringUtils.hasText(ssgUserVo.getPhoneNumber())) {
						params.put("custNm", ssgUserVo.getFullName());
						String phoneNumber[] = phoneNumberSplit(ssgUserVo.getPhoneNumber());
						
						if(ArrayUtils.isEmpty(phoneNumber)) {
							ssgResponse.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
							return ssgResponse;
						}else {
							params.put("cellTidn", phoneNumber[0]);
							params.put("cellTexn", phoneNumber[1]);
							params.put("cellTlsn", phoneNumber[2]);
						}
					}else if (!(ssgUserVo.getCiNo().length()==88)) {
						ssgResponse.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
						return ssgResponse;
					} else {
						ssgResponse.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
						return ssgResponse;
					}
					
									
					Gson gson = new GsonBuilder().disableHtmlEscaping().create();
					String json = gson.toJson(params);
					CicuemCuInfQcVo cicuemCuInfQcVo=null;
					Customer customer=null;
					ResponseEntity<CicuemCuInfQcVo> response = this.restApiService.post(this.checkapuser, headers, json, CicuemCuInfQcVo.class);
					if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
						ssgResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						return ssgResponse;
					}
					if (response.getStatusCode() == HttpStatus.OK) {
						cicuemCuInfQcVo= response.getBody();
						
						 if ("ICITSVCOM001".equals(cicuemCuInfQcVo.getRsltCd()) || "ICITSVCOM002".equals(cicuemCuInfQcVo.getRsltCd())) { //통합고객/정보 존재하지 않음
								ssgResponse.SetResponseInfo(ResultCode.USER_NOT_FOUND);
								return ssgResponse;
							}
						
						customer=cicuemCuInfQcVo.getCicuemCuInfQcVo()[0];
						log.debug("◆◆◆◆◆◆ [Uservo]  : {}", StringUtil.printJson(customer));
						
						if("ICITSVCOM000".equals(customer.getRsltCd()) || "ICITSVCOM004".equals(customer.getRsltCd())) { //정상 처리/존재하는 고객
							UserVo userVo = new UserVo();
							
							userVo.setXincsNo(SecurityUtil.encryptionAESKey(customer.getIncsNo())); //고객통합번호 암호화 EET
							
							userVo.setUserDormancy(false); //휴면여부
							userVo.setAccountDisabled(false);//탈퇴여부
							
							//경로 가입 여부
							SsgCheckVo ssgCheckVo = new SsgCheckVo();
							ssgCheckVo.setChCd(ssgUserVo.getChCd());
							ssgCheckVo.setXincsNo(customer.getIncsNo());
							CicuemCuInfQcVo cicuemCuInfQcVo2=getCustbyChCsNo(ssgCheckVo);
							
							log.debug("◆◆◆◆◆◆ [cicuemCuInfQcVo2]  : {}", StringUtil.printJson(cicuemCuInfQcVo2));
							
							if ("9000".equals(cicuemCuInfQcVo2.getRsltCd())) {
								ssgResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
								return ssgResponse;
							}else {
								if("ICITSVCOM000".equals(cicuemCuInfQcVo2.getRsltCd())) {
									userVo.setChannelJoin(true);
								}else if("ICITSVCOM002".equals(cicuemCuInfQcVo2.getRsltCd())) {	//연동 조회된 데이터가 없음
									userVo.setChannelJoin(false);
								}else if("ICITSVCOM003".equals(cicuemCuInfQcVo2.getRsltCd())) {	//입력 정보 오류
									ssgResponse.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
									return ssgResponse;
								}else {
									ssgResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
									return ssgResponse;
								}
							}

							try {
								SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
								Date date = new Date();
								if(!(customer.getMbrJoinDt()==null || customer.getMbrJoinDt().isEmpty())) {
									date = formatter.parse(customer.getMbrJoinDt());
									userVo.setCreateDate(date);
								}
								if(!(customer.getCustWtDttm()==null || customer.getCustWtDttm().isEmpty())) {
									date = formatter.parse(customer.getCustWtDttm());
									userVo.setDisabledDate(date);					
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
							ssgResponse.setUserVo(userVo);
							ssgResponse.setMessage(customer.getRsltMsg());
							ssgResponse.setResultCode(customer.getRsltCd());
							ssgResponse.SetResponseInfo(ResultCode.SUCCESS);
						}else if ("ICITSVBIZ155".equals(customer.getRsltCd())) { //휴면
							UserVo userVo = new UserVo();
							userVo.setUserDormancy(true); //휴면여부
							ssgResponse.setUserVo(userVo);
							ssgResponse.setMessage(customer.getRsltMsg());
							ssgResponse.setResultCode(customer.getRsltCd());
							ssgResponse.SetResponseInfo(ResultCode.SUCCESS);
							
						}else if ("ICITSVCOM001".equals(customer.getRsltCd()) || "ICITSVCOM002".equals(customer.getRsltCd())) { //통합고객/정보 존재하지 않음
							ssgResponse.SetResponseInfo(ResultCode.USER_NOT_FOUND);
						}else if ("ICITSVCOM003".equals(customer.getRsltCd())) { //입력정보 오류
							ssgResponse.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
						}else if ("ICITSVBIZ152".equals(customer.getRsltCd())) { //탈퇴 고객
							UserVo userVo = new UserVo();
							//userVo.setXincsNo(SecurityUtil.encryptionAESKey(customer.getIncsNo())); //고객통합번호 암호화 EET	->협의 후 진행
							userVo.setAccountDisabled(true);//탈퇴여부
							
							SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
							Date date = new Date();
							if(!(customer.getCustWtDttm()==null || customer.getCustWtDttm().isEmpty())) {
								date = formatter.parse(customer.getCustWtDttm());
								userVo.setDisabledDate(date);					
							}

							ssgResponse.setUserVo(userVo);
							ssgResponse.setMessage(customer.getRsltMsg());
							ssgResponse.setResultCode(customer.getRsltCd());
							ssgResponse.SetResponseInfo(ResultCode.USER_DISABLED);
						}else if ("ICITSVBIZ103".equals(customer.getRsltCd())  || "ICITSVBIZ102".equals(customer.getRsltCd())  || "ICITSVBIZ104".equals(customer.getRsltCd())) {//
							ssgResponse.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
						}else{
							ssgResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						}
					} else {
						ssgResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						return ssgResponse;
					}
					log.debug("◆◆◆◆◆◆ [ssgResponse]  : {}", StringUtil.printJson(ssgResponse));
					return ssgResponse;
					
				} catch (Exception e) {
					e.printStackTrace();
					ssgResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
					return ssgResponse;
				}
	}
	
	
	
	public SsgResponse checkChannelUser(SsgCheckVo ssgCheckVo) {
		SsgResponse ssgResponse=new SsgResponse();
		ssgResponse.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
		CicuemCuInfQcVo cicuemCuInfQcVo = new CicuemCuInfQcVo();
		
		try {
			if(!StringUtils.hasText(ssgCheckVo.getChCd()) || !StringUtils.hasText(ssgCheckVo.getMemberId())) {
				ssgResponse.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
				return ssgResponse;
			}			
			
			if(StringUtils.hasText(ssgCheckVo.getXincsNo())) {
				try {
					ssgCheckVo.setXincsNo(SecurityUtil.decryptionAESKey(ssgCheckVo.getXincsNo())); //암호화 된 고객통합번호 복호화
				}catch (NumberFormatException e) {
					log.debug("◆◆◆◆◆◆ [XincsNo 복호화 오류] ");
					ssgResponse.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
					return ssgResponse;
				} catch (Exception e) {
					ssgResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
					return ssgResponse;
				}
			}
			//경로가입여부 확인api
			cicuemCuInfQcVo=getCustbyChCsNo(ssgCheckVo);
			
			if ("9000".equals(cicuemCuInfQcVo.getRsltCd())) {
				ssgResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				return ssgResponse;
			}else {
				UserVo userVo = new UserVo();
				log.debug("◆◆◆◆◆◆ [cicuemCuInfQcVo]  : {}", StringUtil.printJson(cicuemCuInfQcVo));
				
				if("ICITSVCOM000".equals(cicuemCuInfQcVo.getRsltCd())) {	//연동된 회원일 시
					userVo.setChannelJoin(true);
					//고객 정보 조회
					Customer customer = getcicuemcuinfrbyincsno(cicuemCuInfQcVo.getIncsNo());
					
					if ("9000".equals(customer.getRsltCd())) {
						ssgResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						return ssgResponse;
					}else {
						if ("ICITSVCOM000".equals(customer.getRsltCd())) {
							if("Y".equals(customer.getDrccCd())) {
								userVo.setUserDormancy(true); //휴면여부
								ssgResponse.setUserVo(userVo);
								ssgResponse.setMessage(customer.getRsltMsg());
								ssgResponse.setResultCode(customer.getRsltCd());
								ssgResponse.SetResponseInfo(ResultCode.SUCCESS);
							}else {
								userVo.setXincsNo(SecurityUtil.encryptionAESKey(customer.getIncsNo())); //고객통합번호 암호화 EET
								userVo.setUserDormancy(false); //휴면여부
								userVo.setAccountDisabled(false);//탈퇴여부
								
								try {
									SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
									Date date = new Date();
									if(!(customer.getMbrJoinDt()==null || customer.getMbrJoinDt().isEmpty())) {
										date = formatter.parse(customer.getMbrJoinDt());
										userVo.setCreateDate(date);
									}
									if(!(customer.getCustWtDttm()==null || customer.getCustWtDttm().isEmpty())) {
										date = formatter.parse(customer.getCustWtDttm());
										userVo.setDisabledDate(date);					
									}
								} catch (ParseException e) {
									e.printStackTrace();
								}
								ssgResponse.setUserVo(userVo);
								ssgResponse.setMessage(customer.getRsltMsg());
								ssgResponse.setResultCode(customer.getRsltCd());
								ssgResponse.SetResponseInfo(ResultCode.SUCCESS);
							}
							
						}else if ("ICITSVCOM001".equals(customer.getRsltCd()) || "ICITSVCOM002".equals(customer.getRsltCd())) { //통합고객/정보 존재하지 않음
							ssgResponse.SetResponseInfo(ResultCode.USER_NOT_FOUND);
						}else if ("ICITSVCOM003".equals(customer.getRsltCd())) { //입력정보 오류
							ssgResponse.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
						}else if ("ICITSVBIZ103".equals(customer.getRsltCd())  || "ICITSVBIZ102".equals(customer.getRsltCd())  || "ICITSVBIZ104".equals(customer.getRsltCd())) {
							ssgResponse.SetResponseInfo(ResultCode.REQ_REQUIRED_PARAM_EMPTY);
						}else{
							ssgResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
						}
					}

				}else if("ICITSVCOM002".equals(cicuemCuInfQcVo.getRsltCd())) {	//연동 조회된 데이터가 없음
					userVo.setChannelJoin(false);
					ssgResponse.setUserVo(userVo);
					ssgResponse.SetResponseInfo(ResultCode.SUCCESS);
				}else if("ICITSVCOM003".equals(cicuemCuInfQcVo.getRsltCd())) {	//입력 정보 오류
					ssgResponse.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
					return ssgResponse;
				}else if("ICITSVBIZ236".equals(cicuemCuInfQcVo.getRsltCd())) {	//고객통합 번호와 경로가입ID 불일치
					ssgResponse.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
					return ssgResponse;
				}else {
					ssgResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
					return ssgResponse;
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ssgResponse.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			return ssgResponse;
		}
		
		return ssgResponse;
	}
	
	//고객통합 경로 가입 조회
	public CicuemCuInfQcVo getCustbyChCsNo(SsgCheckVo ssgCheckVo) {
		CicuemCuInfQcVo cicuemCuInfQcVo = new CicuemCuInfQcVo();
		
		log.debug("◆◆◆◆◆◆ [ssgCheckVo]  : {}", StringUtil.printJson(ssgCheckVo));
		
//		String profile = systemInfo.getActiveProfiles()[0];
//		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
//		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
//		
//		// Local Sample Data
//		if("local".equals(profile)) {
//			cicuemCuInfQcVo.setRsltCd("ICITSVCOM002");
//			return cicuemCuInfQcVo;
//		}
		
		try {
			final HttpHeaders headers = new HttpHeaders();
			final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			headers.setContentType(mediaType);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			Map<String, Object> params = new HashMap<String, Object>();
			
			if(StringUtils.hasText(ssgCheckVo.getChCd()) && StringUtils.hasText(ssgCheckVo.getMemberId())) {
				params.put("chCd", ssgCheckVo.getChCd());
				params.put("chcsNo", ssgCheckVo.getMemberId());
				if(StringUtils.hasText(ssgCheckVo.getXincsNo())) {
					params.put("incsNo", ssgCheckVo.getXincsNo());
				}
			}else {
				params.put("chCd", ssgCheckVo.getChCd());
				params.put("incsNo", ssgCheckVo.getXincsNo());	//필수 값 x
			}
			
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			String json = gson.toJson(params);
			ResponseEntity<CicuemCuInfQcVo> response = this.restApiService.post(this.getCustbyChCsNo, headers, json, CicuemCuInfQcVo.class);
			
			if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				cicuemCuInfQcVo.setRsltCd("9000");
				return cicuemCuInfQcVo;
			}
			
			cicuemCuInfQcVo= response.getBody();
			
			return cicuemCuInfQcVo;
			
		}catch (Exception e) {
				e.printStackTrace();
				cicuemCuInfQcVo.setRsltCd("9000");
				return cicuemCuInfQcVo;
		}
		
	}
	
	public Customer getcicuemcuinfrbyincsno(String incsNo) {
		Customer customer = new Customer();
		
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("incsNo", incsNo);
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		//고객 조회
		ResponseEntity<Customer> response = this.restApiService.post(this.getcicuemcuinfrbyincsno, headers, json, Customer.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			customer.setRsltCd("9000");
			return customer;
		}
		customer = response.getBody();
		
		return customer;
	}
	
	public SsgUnLinkResponse unLinkSSG(SsgUnLinkVo ssgUnLinkVo) {
		log.debug("▶▶▶▶▶▶ SsgUnLinkVo : {}", StringUtil.printJson(ssgUnLinkVo));
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		if(StringUtils.isEmpty(ssgUnLinkVo.getChCd()) || StringUtils.isEmpty(ssgUnLinkVo.getMbrId())) {
			SsgUnLinkResponse ssgUnLinkResponse = new SsgUnLinkResponse();
			ssgUnLinkResponse.setMessage("필수 항목 누락");
			ssgUnLinkResponse.setStatus(404);
			return ssgUnLinkResponse;
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.hasText(ssgUnLinkVo.getMbrId())) {
			params.put("mbrId", ssgUnLinkVo.getMbrId());
		}
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		String unLinkUrl = this.config.getChannelApi(ssgUnLinkVo.getChCd(), "unlinkurl", profile);
		
		unLinkUrl += "?membershipId=" + this.config.getChannelApi(ssgUnLinkVo.getChCd(), "membershipid", profile);
		unLinkUrl += "&&apiKey=" + this.config.getChannelApi(ssgUnLinkVo.getChCd(), "apikey", profile);
		
		ResponseEntity<SsgUnLinkResponse> response = this.restApiService.post(unLinkUrl, headers, json, SsgUnLinkResponse.class);
		
		return response.getBody();
	}
	
	
	
	
	
	public static String[] phoneNumberSplit(String phoneNumber){

        Pattern tellPattern = Pattern.compile( "^(01\\d{1}|02|0505|0502|0506|0\\d{1,2})-?(\\d{3,4})-?(\\d{4})");
        try {
        	Matcher matcher = tellPattern.matcher(phoneNumber);
        	if(matcher.matches()) {
        		//정규식에 적합하면 matcher.group으로 리턴
        		return new String[]{ matcher.group(1), matcher.group(2), matcher.group(3)};
        	}else{
        		//정규식에 적합하지 않으면 substring으로 휴대폰 번호 나누기
        		
        		String str1 = phoneNumber.substring(0, 3);
        		String str2 = phoneNumber.substring(3, 7);
        		String str3 = phoneNumber.substring(7, 11);
        		return new String[]{str1, str2, str3};
        	}
		} catch (Exception e) {
			return null;
		}
    }
	
	
	
	
}
