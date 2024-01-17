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
 * Date   	          : 2020. 8. 7..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.common.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.common.mapper.SmsMapper;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.sms.SmsIdata;
import com.amorepacific.oneap.common.vo.sms.SmsOdata;
import com.amorepacific.oneap.common.vo.sms.SmsRequest;
import com.amorepacific.oneap.common.vo.sms.SmsRequestHeader;
import com.amorepacific.oneap.common.vo.sms.SmsRequestInput;
import com.amorepacific.oneap.common.vo.sms.SmsResponse;
import com.amorepacific.oneap.common.vo.sms.SmsResponseHeader;
import com.amorepacific.oneap.common.vo.sms.SmsRetryData;
import com.amorepacific.oneap.common.vo.sms.SmsVo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.common.sms.service 
 *    |_ SmsService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 7.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class SmsService {

	@Value("${sms.url}")
	private String smsUrl;
	
	@Value("${sms.callback}")
	private String smsCallback;

	@Value("${sms.source}")
	private String smsSource;

	@Value("${sms.username}")
	private String smsUsername;

	@Value("${sms.userpassword}")
	private String smsUserpassword;

	@Value("${sms.id}")
	private String smsId;
	
	@Value("${sms.applclcd}")
	private String applClCd;
	
	@Value("${sms.pltfclcd}")
	private String pltfClCd;
	
	@Value("${sms.eaifl}")
	private String eaiFl;
	
	@Autowired
	private SmsMapper smsMapper;

	@Autowired
	private ApiService apiService;

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 13. 오후 3:07:11
	 * </pre>
	 * 
	 * @param smsVo
	 * @return
	 */
	public SmsVo sendSms(final SmsVo smsVo) {

		//if (smsVo.getIncsNo() <= 0) {
		//	SmsVo vo = new SmsVo();
		//	vo.setStatus(0);
		//	return vo; // 통합고객번호 없음.
		//}
		
		// 종료시간(30분이후시간) - 현재시간
		// 실패한지 30분이 지난 데이터가 있는지 확인
		SmsRetryData retryData = this.smsMapper.selectSmsRetryAvaiable(smsVo);
		if (retryData == null) {
			retryData = new SmsRetryData();
			retryData.setMins(0D);
			retryData.setSecs(0D);
			
		}
		log.debug("▶▶▶▶▶ sms retry data : {}", retryData.toString());
		Double min = (retryData == null) ? 0D : retryData.getMins();
		int retryavaiablemin = min.intValue();
		if (retryavaiablemin > 0) { // 5번 실패한지 30분 경과되지 않음
			smsVo.setSmsAthtSendNo(0);
			smsVo.setSmsAthtNoVl("");
			smsVo.setName("");
			Double sec = retryData.getSecs();
			int retryavaiablesec = sec.intValue();
			String sectime = StringUtil.padLeft(Integer.toString(retryavaiablesec), 2, "0");
			String times = Integer.toString(retryavaiablemin).concat(":").concat(sectime);
			log.debug("▶▶▶▶▶ sms auth limit exceeded ---> remain time : {}", times);
			smsVo.setTimes(times);
			smsVo.setStatus(OmniConstants.SMS_AUTH_LIMIT_EXCEEDED);
			return smsVo;
		}

		if (StringUtils.hasText(smsVo.getPhoneNo())) { // 전화번호가 있는 경우만 진행
			// SMS EAI INF 발송
			final SmsRequestHeader smsheader = new SmsRequestHeader(this.smsSource);
			final SmsRequestInput smsinput = new SmsRequestInput(smsVo.getPhoneNo(), smsVo.getSendMessage());
			smsinput.setREQDATE(DateUtil.getCurrentDateString("yyyy-MM-dd HH:mm:ss"));
			smsinput.setSERIALNUM(OmniUtil.getSerialNumber());
			smsinput.setID(this.smsId);
			smsinput.setCALLBACK(this.smsCallback);
			smsinput.setAPPL_CL_CD(this.applClCd);
			smsinput.setPLTF_CL_CD(this.pltfClCd);
			smsinput.setEAI_FL(this.eaiFl);
			
			final SmsIdata smsidata = new SmsIdata(smsheader, smsinput);
			final SmsRequest smsrequest = new SmsRequest(smsidata);
			final SmsResponse response = this.sendSmsEai(smsrequest);
			
			log.debug("▶▶▶▶▶▶ send sms result : {}", StringUtil.printJson(response));
			
			String rtnCode = response.getRESPONSE().getHEADER().getRTN_CODE();
			rtnCode = StringUtils.isEmpty(rtnCode) ? response.getRESPONSE().getHEADER().getRTN_TYPE() : rtnCode;
			
			if (OmniConstants.SEND_SMS_EAI_SUCCESS.equals(rtnCode)) {
				
				/* 동시에 호출될 경우 키값 "sms_atht_send_no"이 중복되어 에러 발생으로 인해 주석처리
					int seq = this.smsMapper.selectSmsMaxSeq(); // SMS인증발송번호시퀀스 조회
					smsVo.setSmsAthtSendNo(seq);
					smsVo.setSmsAthtProcRsltCd(rtnCode);
					log.debug("▶▶▶▶▶▶ send sms : {}", StringUtil.printJson(smsVo));
					int rtn = this.smsMapper.insertSmsData(smsVo); // SMS 발송 대장 기록
					log.debug("▶▶▶▶▶▶ send sms result : {}", rtn);
					smsVo.setStatus(rtn);
					//return rtn > 0 ? smsVo : null;
				*/
				smsVo.setSmsAthtProcRsltCd(rtnCode);
				int rtn = this.smsMapper.insertSmsData(smsVo);
				log.debug("▶▶▶▶▶▶ send sms : {}", StringUtil.printJson(smsVo));
				
				log.debug("▶▶▶▶▶▶ send sms result : {}", rtn);
				smsVo.setStatus(rtn);
				//return rtn > 0 ? smsVo : null;
				if (rtn > 0) {
					return smsVo;
				} else {
					SmsVo vo = new SmsVo();
					vo.setStatus(0);
					return vo; // 발송 INF 실패
				}
			} else {
				SmsVo vo = new SmsVo();
				vo.setStatus(0);
				return vo; // 발송 INF 실패
			}
		} else {
			SmsVo vo = new SmsVo();
			vo.setStatus(0);
			return vo; // 발송 전화번호없음.
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 25. 오후 6:26:23
	 * </pre>
	 * 
	 * @param smsVo
	 * @return
	 */
	public boolean invalidSms(final SmsVo smsVo) {
		return this.smsMapper.deleteSmsData(smsVo) >= 0;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 21. 오후 12:14:45
	 * </pre>
	 * 
	 * @param phoneNo
	 * @param sendMessage
	 * @return
	 */
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

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 21. 오전 9:22:05
	 * </pre>
	 * 
	 * @param request
	 * @return
	 */
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
		ResponseEntity<String> resp = this.apiService.post(this.smsUrl, headers, jsonBody, String.class);
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

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 25. 오후 6:26:06
	 * </pre>
	 * 
	 * @param smsVo
	 * @return
	 */
	public SmsVo authSms(final SmsVo smsVo) {

		int failcount = this.smsMapper.selectSmsFailCount(smsVo); // 현재 실패건수 확인
		log.debug("▶▶▶▶▶▶ fail count : {}", failcount);
		if (failcount >= 5) {
			log.warn("▶▶▶▶▶▶ fail count limit exceeded..");
			smsVo.setSmsAthtSendNo(0);
			smsVo.setSmsAthtNoVl("");
			smsVo.setName("");
			smsVo.setStatus(OmniConstants.SMS_AUTH_LIMIT_EXCEEDED);
			return smsVo;
		}

		SmsVo authSmsVo = this.smsMapper.selectAuthSms(smsVo); // 인증데이터가 있는지 확인

		if (authSmsVo != null) {
			smsVo.setStatus(OmniConstants.SMS_AUTH_SUCCESS);
			smsVo.setIncsNo(authSmsVo.getIncsNo());
			smsVo.setSmsAthtSendNo(0);
			smsVo.setSmsAthtNoVl("");
			smsVo.setName("");
		} else {
			smsVo.setSmsAthtProcRsltCd("F");
			int rtn = this.smsMapper.updateSmsDataFailCount(smsVo); // 없으면 실패건수 업데이트
			smsVo.setStatus(rtn);
			if (rtn > 0) {
				SmsVo authinfo = this.smsMapper.selectSmsData(smsVo); // 전송용 인증 정보 조회
				if (authinfo != null) {
					if (authinfo.getSmsAthtFailCnt() == 5) {
						smsVo.setStatus(OmniConstants.SMS_AUTH_LIMIT_EXCEEDED);
						smsVo.setSmsAthtSendNo(0);
						smsVo.setSmsAthtNoVl("");
					} else {
						return authinfo;
					}
				} else {
					smsVo.setStatus(0);
					smsVo.setSmsAthtSendNo(0);
					smsVo.setSmsAthtNoVl("");
				}
			} else {
				smsVo.setStatus(0);
				smsVo.setSmsAthtSendNo(0);
				smsVo.setSmsAthtNoVl("");
			}
		}
		return smsVo;
	}
}
