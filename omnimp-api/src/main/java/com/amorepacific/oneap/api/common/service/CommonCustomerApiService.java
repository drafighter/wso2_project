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
 * Date   	          : 2023. 2. 15..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.common.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.log.client.LogInfo;
import com.amorepacific.log.client.LogInfoConstants;
import com.amorepacific.oneap.api.v2.join.vo.CustMarketingRequest;
import com.amorepacific.oneap.api.v2.join.vo.CustTncaRequest;
import com.amorepacific.oneap.api.v4.membership.vo.NaverRequest;
import com.amorepacific.oneap.api.v4.membership.vo.NaverResponse;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.OmniStdLogConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.CipAthtVo;
import com.amorepacific.oneap.common.vo.api.CreateCustChRequest;
import com.amorepacific.oneap.common.vo.api.CustChListResponse;
import com.amorepacific.oneap.common.vo.api.CustChResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.CustbyChCsNoResponse;
import com.amorepacific.oneap.common.vo.api.DeleteCustChRequest;
import com.amorepacific.oneap.common.vo.kakao.KakaoNoticeRequest;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.common.service 
 *    |_ CommonCustomerApiService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 2. 15.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@Service
public class CommonCustomerApiService {

	@Value("${external.cip.api.membership.getCustbyChCsNo}")
	private String getCustbyChCsNo;
	
	// 고객상세조회 API URL
	@Value("${external.cip.api.getcicuemcuinfrbyincsno}")
	private String getCicuemcuinfrbyincsno;
	
	// 고객목록조회 API URL
	@Value("${external.cip.api.getcicuemcuinfrlist}")
	private String getCicuemcuinfrlist;
	
	// 경로가입 API URL
	@Value("${external.cip.api.createcustchnjoin}")
	private String getCreatecustchnjoin;
	
	// 경로탈퇴 API URL
	@Value("${external.cip.api.deletecicuedcuchcustwt}")
	private String getDeletecicuedcuchcustwt;	
	
	// 통합고객가입경로정보조회
	@Value("${external.cip.api.getcustchlist}")
	private String getCustchlist;
	
	// 서비스약관동의/철회 다건 저장
	@Value("${external.cip.api.savecicuedcutnca}")
	private String getSavecicuedcutnca;

	// 마케팅정보수신동의/철회 다건 저장
	@Value("${external.cip.api.savecicuemcuoptilist}")
	private String getSavecicuemcuoptilist;	
	
	// 카카오 알림톡 EAI authorization
	@Value("${kakao.notice.authorization}")
	private String kakaoNoticeAuthorization;
	
	// 카카오 알림톡 EAI URL
	@Value("${kakao.notice.url}")
	private String kakaoNoticeUrl;
	
	@Autowired
	private RestApiService restApiService;
	
	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	public CustbyChCsNoResponse getCustbyChCsNo(final String chCd, final String memberId) {
		CustbyChCsNoResponse custbyChCsNoResponse = new CustbyChCsNoResponse();
		
		log.debug("▶▶▶▶▶▶ getCustbyChCsNo [chCd]  : {}, [memberId]  : {}", chCd, memberId);
		
		try {
			final HttpHeaders headers = new HttpHeaders();
			final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			headers.setContentType(mediaType);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			params.put("chCd", chCd);
			params.put("chcsNo", memberId);

			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			String json = gson.toJson(params);
			ResponseEntity<CustbyChCsNoResponse> response = this.restApiService.post(this.getCustbyChCsNo, headers, json, CustbyChCsNoResponse.class);
			
			if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				custbyChCsNoResponse.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
				return custbyChCsNoResponse;
			}
			
			custbyChCsNoResponse = response.getBody();
			
			return custbyChCsNoResponse;
			
		} catch (Exception e) {
			e.printStackTrace();
			custbyChCsNoResponse.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
			return custbyChCsNoResponse;
		}
	}
	
	public Customer getCicuemcuInfrByIncsNo(final String incsNo) {
		Customer customer = new Customer();
		
		log.debug("▶▶▶▶▶▶ getCicuemcuInfrByIncsNo [incsNo]  : {}", incsNo);
		
		try {
			final HttpHeaders headers = new HttpHeaders();
			final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			headers.setContentType(mediaType);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			params.put("incsNo", incsNo);
			
			CipAthtVo cipAthtVo = CipAthtVo.builder().build();
			params.put("cipAthtVo", cipAthtVo);
			
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			String json = gson.toJson(params);
			
			log.debug("▶▶▶▶▶▶ getCicuemcuInfrByIncsNo: {}", StringUtil.printJson(params));
			
			ResponseEntity<Customer> response = this.restApiService.post(this.getCicuemcuinfrbyincsno, headers, json, Customer.class);
			
			if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				customer.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
				return customer;
			}
			
			customer = response.getBody();
			
			return customer;
		} catch (Exception e) {
			e.printStackTrace();
			customer.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
			return customer;
		}
	}
	
	public Customer getCicuemcuInfrList(final CustInfoVo custInfoVo) {
		Customer customer = new Customer();
		
		log.debug("▶▶▶▶▶▶ getCicuemcuInfrList [custInfoVo]  : {}", StringUtil.printJson(custInfoVo));
		
		try {
			final HttpHeaders headers = new HttpHeaders();
			final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			headers.setContentType(mediaType);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			if(StringUtils.hasText(custInfoVo.getCiNo())) {
				params.put("ciNo", custInfoVo.getCiNo());
			} else if(StringUtils.hasText(custInfoVo.getCustMobile())) {
				String mobile[] = StringUtil.splitMobile(custInfoVo.getCustMobile());
				if (StringUtils.hasText(custInfoVo.getAthtDtbr())) {
					params.put("athtDtbr", custInfoVo.getAthtDtbr().length() == 8 ? custInfoVo.getAthtDtbr().substring(2) : custInfoVo.getAthtDtbr()); // 고객생일
					params.put("custNm", custInfoVo.getCustName());
					params.put("cellTlsn", mobile[2]); // 끝 4자리
				} else {
					params.put("custNm", custInfoVo.getCustName());
					params.put("cellTidn", mobile[0]);
					params.put("cellTexn", mobile[1]);
					params.put("cellTlsn", mobile[2]);
				}
			}
			
			CipAthtVo cipAthtVo = CipAthtVo.builder().build();
			params.put("cipAthtVo", cipAthtVo);
			
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			String json = gson.toJson(params);
			
			log.debug("▶▶▶▶▶▶ getCicuemcuInfrList: {}", StringUtil.printJson(params));
			
			ResponseEntity<CustInfoResponse> response = this.restApiService.post(this.getCicuemcuinfrlist, headers, json, CustInfoResponse.class);
			
			if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				customer.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
				return customer;
			}
			
			CustInfoResponse custInfoResponse = response.getBody();
			
			Customer customers[] = custInfoResponse.getCicuemCuInfQcVo();
			
			if(customers == null || customers.length == 0) {
				customer.setRsltCd(ResultCode.USER_NOT_FOUND.getCode());
				return customer;
			} else {
				customers[0].setRsltCd(custInfoResponse.getRsltCd());
				customers[0].setRsltMsg(custInfoResponse.getRsltMsg());
			}
			
			return customers[0];
			
		} catch (Exception e) {
			e.printStackTrace();
			customer.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
			return customer;
		}
	}
	
	public CustChResponse createCustChannelMember(final CreateCustChRequest createCustChRequest) {
		
		// 브랜드 사이트일 경우 고객통합 플랫폼에 경로 가입 처리 하지 않음
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		if(config.isBrandSite(createCustChRequest.getCicuedCuChTcVo().get(0).getChCd(), profile)) {
			CustChResponse cr = new CustChResponse();
			cr.setRsltCd("ICITSVCOM000");
			return cr;
		}

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(createCustChRequest);
		log.debug("▶▶▶▶▶▶ create custChannel member: {}", StringUtil.printJson(createCustChRequest));
		ResponseEntity<CustChResponse> response = this.restApiService.post(this.getCreatecustchnjoin, headers, json, CustChResponse.class);
		
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			CustChResponse cr = new CustChResponse();
			cr.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
			return cr;
		}
		
		return response.getBody();
	}
	
	public CustChResponse deleteCustChannelMember(final DeleteCustChRequest deleteCustChRequest) {
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(deleteCustChRequest);
		log.debug("▶▶▶▶▶▶ delete custChannel member: {}", StringUtil.printJson(deleteCustChRequest));
		ResponseEntity<CustChResponse> response = this.restApiService.post(this.getDeletecicuedcuchcustwt, headers, json, CustChResponse.class);
		
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			CustChResponse cr = new CustChResponse();
			cr.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
			return cr;
		}
		
		return response.getBody();
	}
	
	public CustChListResponse getCustChList(final CustInfoVo custInfoVo) {
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		// headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.hasText(custInfoVo.getIncsNo())) {
			params.put("incsNo", custInfoVo.getIncsNo());
		}
		
		// 통합고객가입경로정보조회 API 의 경우 v1.10만 권한 정보 입력
		if(this.getCustchlist.contains("v1.10")) {
			CipAthtVo cipAthtVo = CipAthtVo.builder().build();
			params.put("cipAthtVo", cipAthtVo);	
		}
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);
		
		log.debug("▶▶▶▶▶▶ getCustChList: {}", StringUtil.printJson(params));
		
		ResponseEntity<CustChListResponse> response = this.restApiService.post(this.getCustchlist, headers, json, CustChListResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			CustChListResponse cr = new CustChListResponse();
			cr.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
			return cr;
		}
		
		if(response.getBody().getCicuedCuChQcVo() == null && response.getBody().getCicuedCuChTcVo() != null) response.getBody().setCicuedCuChQcVo(response.getBody().getCicuedCuChTcVo());
		
		return response.getBody();
	}
	
	public ApiResponse savecicuedcutnca(final CustTncaRequest custTncaRequest) {
		
		log.debug("▶▶▶▶▶▶ [save term] request : {}", StringUtil.printJson(custTncaRequest));

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(custTncaRequest);

		ResponseEntity<ApiResponse> response = this.restApiService.post(this.getSavecicuedcutnca, headers, json, ApiResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			
			ApiResponse ur = new ApiResponse();
			ur.setRsltCd("ICITSVCOM999");
			return ur;
		}
		
		return response.getBody();
	}	
	
	public ApiResponse savecicuemcuoptilist(final CustMarketingRequest custMarketingRequest) {
		
		log.debug("▶▶▶▶▶▶ [save marketing] request : {}", StringUtil.printJson(custMarketingRequest));

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(custMarketingRequest);

		ResponseEntity<ApiResponse> response = this.restApiService.post(this.getSavecicuemcuoptilist, headers, json, ApiResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			ApiResponse ur = new ApiResponse();
			ur.setRsltCd("ICITSVCOM999");
			return ur;
		}
		
		return response.getBody();

	}
	
	public NaverResponse deleteNaverMembership(final String url, final String stmApiKey, final String affiliateSellerKey, final NaverRequest naverRequest) {
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		// headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("stm-api-key", stmApiKey);
		headers.add("affiliate-seller-key", affiliateSellerKey);

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(naverRequest);
		
		log.debug("▶▶▶▶▶▶ deleteNaverMembership: {}", StringUtil.printJson(json));
		
		ResponseEntity<NaverResponse> response = this.restApiService.delete(url, headers, json, NaverResponse.class);
		if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			NaverResponse nr = new NaverResponse();
			nr.setOperationResult("FAIL");
			return nr;
		}
		
		return response.getBody();
	}
	
	public ApiBaseResponse sendKakaoNoticeTalkEai(final KakaoNoticeRequest request) {
		log.debug("▶▶▶▶▶▶ kakao notice talk eai request : {}", StringUtil.printJson(request));
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		final String jsonBody = gson.toJson(request);
		
		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", this.kakaoNoticeAuthorization);
		
		ResponseEntity<String> resp = this.restApiService.post(this.kakaoNoticeUrl, headers, jsonBody, String.class);
		if (resp == null || (resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)) {
			ApiBaseResponse response = new ApiBaseResponse();
			response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
			return response;
		}
		
		log.debug("▶▶▶▶▶▶ kakao notice talk eai response : {}", StringUtil.printJson(resp.getBody()));
		
		ApiBaseResponse response = new ApiBaseResponse();
		response.SetResponseInfo(ResultCode.SUCCESS);
		
		return response;	
	}
}
