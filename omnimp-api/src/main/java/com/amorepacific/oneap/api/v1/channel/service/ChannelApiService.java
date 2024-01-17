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
 * Date   	          : 2020. 9. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.channel.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.api.common.service.RestApiService;
import com.amorepacific.oneap.api.v1.channel.validator.ChannelApiValidator;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.BpChangePasswordRequest;
import com.amorepacific.oneap.common.vo.api.BpChangePasswordResponse;
import com.amorepacific.oneap.common.vo.api.BpUserData;
import com.amorepacific.oneap.common.vo.api.CipAthtVo;
import com.amorepacific.oneap.common.vo.api.CreateChCustRequest;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.SearchChCustRequest;
import com.amorepacific.oneap.common.vo.api.SearchChCustResponse;
import com.amorepacific.oneap.common.vo.api.UserInfo;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.channel.service 
 *    |_ ChannelApiService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 21.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class ChannelApiService {

	private ConfigUtil config = ConfigUtil.getInstance();

	@Autowired
	private RestApiService restApiService;

	@Autowired
	private SystemInfo systemInfo;

	// 고객상세조회 API URL
	@Value("${external.cip.api.getcicuemcuinfrbyincsno}")
	private String getcicuemcuinfrbyincsno;
	
	// 고객목록조회 API URL
	@Value("${external.cip.api.getcicuemcuinfrlist}")
	private String getcicuemcuinfrlist;

	@Value("${external.bp.api.checkonlineid}")
	private String checkBpOnlineIdEndpoint; // 뷰티포인트 회원아이디유효성체크 api url

	@Value("${external.bp.api.changepassword}")
	private String bpChangepasswordEndpoint;

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 21. 오전 8:48:53
	 * </pre>
	 * 
	 * @param response
	 * @param chCd
	 * @param createChCustRequest
	 * @return
	 */
	public ApiBaseResponse registChannelCustomer(ApiBaseResponse response, final String chCd, final CreateChCustRequest createChCustRequest) {

		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		log.debug("▶▶▶▶▶▶  [channel] system profile : {}", profile);

		response = ChannelApiValidator.registChannelOnline(response, chCd, createChCustRequest, profile);

		log.debug("channel code : {}", chCd);
		log.debug("channel data : {}", StringUtil.printJson(createChCustRequest));

		if (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode()) //
				&& !ResultCode.REQ_INVALID_PARAM.getCode().equals(response.getResultCode())) {

			final String apiKey = this.config.getChannelApi(chCd, "apikey", profile);
			final String apiUrl = this.config.getChannelApi(chCd, "url", profile);

			if (StringUtils.isEmpty(apiUrl)) {
				response.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				return response;
			}

			final HttpHeaders headers = new HttpHeaders();
			final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			headers.setContentType(mediaType);
			//headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			if (StringUtils.hasText(apiKey)) {
				headers.add(OmniConstants.XAPIKEY, apiKey);
			}

//			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//			String json = gson.toJson(createChCustRequest);

			ResponseEntity<ApiResponse> apiResponse = this.restApiService.post(apiUrl, headers, createChCustRequest, ApiResponse.class);

			log.debug("▶▶▶▶▶▶ regist channel response: {}", StringUtil.printJson(apiResponse.getBody()));

			if (apiResponse == null || apiResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				ApiBaseResponse cr = new ApiBaseResponse();
				cr.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				cr.setMessage("오류가 발생하였습니다.");
				cr.setTrxUuid(response.getTrxUuid());
				log.debug("▶▶▶▶▶▶ regist channel online response error: {}", StringUtil.printJson(cr));
				return cr;
			}
			
			
			if (apiResponse.getBody() == null || apiResponse.getBody().toString().equals("")) {
				ApiBaseResponse cr = new ApiBaseResponse();
				cr.SetResponseInfo(ResultCode.SYSTEM_ERROR);
				cr.setMessage("오류가 발생하였습니다. empty!!!");
				cr.setTrxUuid(response.getTrxUuid());
				log.debug("▶▶▶▶▶▶ regist channel online response error: {}", StringUtil.printJson(cr));
				return cr;
			}

			ApiResponse result = apiResponse.getBody();

			if (ResultCode.SUCCESS.getCode().equals(result.getRsltCd())) {
				response.SetResponseInfo(ResultCode.SUCCESS);
			} else {
				response.setResultCode(result.getResultCode());
				response.setMessage(result.getMessage());
			}

		}

		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 21. 오전 8:50:24
	 * </pre>
	 * 
	 * @param response
	 * @param chCd
	 * @param searchCustRequest
	 * @return
	 */
	public SearchChCustResponse searchChannelCustomer(SearchChCustResponse response, final String chCd, final SearchChCustRequest searchCustRequest) {

		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		log.debug("▶▶▶▶▶▶  [channel] system profile : {}", profile);

		response = ChannelApiValidator.searchChannelCustomer(response, chCd, searchCustRequest, profile);

		log.debug("channel code : {}", chCd);
		log.debug("channel data : {}", StringUtil.printJson(searchCustRequest));

		if (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode())) {

			final String apiKey = this.config.getChannelApi(chCd, "apikey", profile);
			final String apiUrl = this.config.getChannelApi(chCd, "search", profile);

			final HttpHeaders headers = new HttpHeaders();
			final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			headers.setContentType(mediaType);
			// headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			if (StringUtils.hasText(apiKey)) {
				headers.add(OmniConstants.XAPIKEY, apiKey);
			}

//			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//			String json = gson.toJson(searchCustRequest);

			// 검색 api url이 있는 경우(없으면 통합고객검색)
			if (StringUtils.hasText(apiUrl)) {
				ResponseEntity<SearchChCustResponse> apiResponse = this.restApiService.post(apiUrl, headers, searchCustRequest, SearchChCustResponse.class);

				log.debug("▶▶▶▶▶▶ search channel response: {}", StringUtil.printJson(apiResponse.getBody()));

				if (apiResponse == null || apiResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
					SearchChCustResponse cr = new SearchChCustResponse();
					cr.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
					cr.setMessage("오류가 발생하였습니다.");
					cr.setTrxUuid(response.getTrxUuid());
					log.debug("▶▶▶▶▶▶ search channel online response error: {}", StringUtil.printJson(cr));
					return cr;
				}

				SearchChCustResponse result = apiResponse.getBody();
				
				if (apiResponse.getBody() == null || apiResponse.getBody().toString().equals("")) {
					SearchChCustResponse cr = new SearchChCustResponse();
					cr.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
					cr.setMessage("오류가 발생하였습니다. empty!!!");
					cr.setTrxUuid(response.getTrxUuid());
					log.debug("▶▶▶▶▶▶ search channel online response error: {}", StringUtil.printJson(cr));
					return cr;
				}

				if (ResultCode.SUCCESS.getCode().equals(result.getResultCode())) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
					response.setUserInfo(result.getUserInfo());
				} else {
					response.setResultCode(result.getResultCode());
					response.setMessage(result.getMessage());
				}
			} else {
				CustInfoVo custInfoVo = new CustInfoVo();
				//custInfoVo.setChCd(searchCustRequest.getChCd());
				custInfoVo.setIncsNo(searchCustRequest.getIncsNo());
				if (StringUtils.hasText(searchCustRequest.getCi())) {
					custInfoVo.setCiNo(searchCustRequest.getCi());
				}
				Customer customer = getCicuemcuInfrByIncsNo(custInfoVo);
				if (customer != null) {
					response.setResultCode(ResultCode.SUCCESS.getCode());
					UserInfo userInfo = new UserInfo();
					userInfo.setChCd(customer.getChCd());
					userInfo.setCi(customer.getCiNo());
					userInfo.setIncsNo(Integer.parseInt(customer.getIncsNo()));
					userInfo.setName(customer.getCustNm());
					userInfo.setPhone(StringUtil.mergeMobile(customer));
					userInfo.setWebId(customer.getChcsNo());
					List<UserInfo> list = new ArrayList<>();
					list.add(userInfo);
					response.setUserInfo(list.toArray(new UserInfo[list.size()]));
				}
			}
		}

		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 21. 오후 8:33:18
	 * </pre>
	 * 
	 * @param userData
	 * @return
	 */
	public ApiResponse beautyPointCheckOnlineId(BpChangePasswordResponse response, final BpUserData userData) {

		ApiResponse apiResponse = new ApiResponse();
		log.debug("▶▶▶▶▶▶ check user : {}", StringUtil.printJson(userData));

		response = ChannelApiValidator.beautyPointCheckOnlineId(response, userData);

		if (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode()) //
				&& !ResultCode.REQ_INVALID_PARAM.getCode().equals(response.getResultCode())) {
			final HttpHeaders headers = new HttpHeaders();
			MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			headers.setContentType(mediaType);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			ResponseEntity<ApiResponse> apiBpResponse = this.restApiService.post(this.checkBpOnlineIdEndpoint, headers, userData, ApiResponse.class);

			if (apiBpResponse == null || apiBpResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				ApiResponse ar = new ApiResponse();
				ar.setRsltCd(ResultCode.SYSTEM_ERROR.getCode());
				ar.setResultCode(ResultCode.SYSTEM_ERROR.getCode());
				return ar;
			}
			return apiBpResponse.getBody();
		}
		apiResponse.setResultCode(response.getResultCode());
		return apiResponse;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 21. 오후 8:28:36
	 * </pre>
	 * 
	 * @param response
	 * @param bpChangePasswordRequest
	 * @return
	 */
	public BpChangePasswordResponse beautyPointChangePassword(BpChangePasswordResponse response, final BpChangePasswordRequest bpChangePasswordRequest) {
		log.debug("beautypoint data : {}", StringUtil.printJson(bpChangePasswordRequest));
		
		// STG 환경에서는 뷰티포인트 API 호출 시 성공 처리
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		// if("stg".equals(profile)) {
			BpChangePasswordResponse cr = new BpChangePasswordResponse();
			cr.setResultCode(ResultCode.SUCCESS.getCode());
			return cr;
		// }

		/*
		 * response = ChannelApiValidator.beautyPointChangePassword(response, bpChangePasswordRequest);
		 * 
		 * if (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode())) { final HttpHeaders headers = new HttpHeaders();
		 * final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8); headers.setContentType(mediaType);
		 * headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		 * 
		 * ResponseEntity<String> apiResponse = this.restApiService.post(this.bpChangepasswordEndpoint, headers, bpChangePasswordRequest,
		 * String.class);
		 * 
		 * Gson gson = new GsonBuilder().disableHtmlEscaping().create(); BpChangePasswordResponse result = gson.fromJson(apiResponse.getBody(),
		 * BpChangePasswordResponse.class);
		 * 
		 * log.debug("▶▶▶▶▶▶ beautypoint change password response: {}", StringUtil.printJson(result)); if (apiResponse.getStatusCode() ==
		 * HttpStatus.INTERNAL_SERVER_ERROR) { BpChangePasswordResponse cr = new BpChangePasswordResponse();
		 * cr.setResultCode(ResultCode.SYSTEM_ERROR.getCode()); cr.setTrxUuid(response.getTrxUuid());
		 * log.debug("▶▶▶▶▶▶ beautypoint change password response error: {}", StringUtil.printJson(cr)); return cr; }
		 * 
		 * if ("000".equals(result.getResultCode())) { response.setResultCode(ResultCode.SUCCESS.getCode()); response.setResult(result.getResult());
		 * response.setRsltCd(result.getRsltCd()); response.setRsltMsg(result.getRsltMsg()); } else {
		 * response.setResultCode(ResultCode.SYSTEM_ERROR.getCode()); response.setResult(result.getResult());
		 * response.setRsltCd(result.getRsltCd()); response.setRsltMsg(result.getRsltMsg()); } }
		 * 
		 * return response;
		 */
	}

	public Customer getCicuemcuInfrByIncsNo(final CustInfoVo custInfoVo) {
		log.debug("▶▶▶▶▶▶ InfrByIncsNo : {}", StringUtil.printJson(custInfoVo));

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		// headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.hasText(custInfoVo.getIncsNo())) {
			params.put("incsNo", custInfoVo.getIncsNo());
		}
		if (StringUtils.hasText(custInfoVo.getCiNo())) {
			// params.put("ciNo", custInfoVo.getCiNo());
			return this.getcicuemcuinfrlist(custInfoVo);
		}
		
		// 경로코드를 입력을 안할 경우 '000'채널 정보를 조회.
		// 탈퇴여부가 'N'인 경우만 조회됨.
		// As Is인 경우 '000' 경로가 가입시 바로 탈퇴로 만들기 때문에
		// As Is 데이터는 경로코드를 입력을 안할 경우 조회가 안될 수 있음.
//		if (StringUtils.hasText(custInfoVo.getChCd())) {
//			params.put("chCd", custInfoVo.getChCd());
//		}
		
		CipAthtVo cipAthtVo = CipAthtVo.builder().build();
		params.put("cipAthtVo", cipAthtVo);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);

		log.debug("▶▶▶▶▶▶ getCicuemcuInfrByIncsNo: {}", StringUtil.printJson(params));

		ResponseEntity<Customer> response = this.restApiService.post(this.getcicuemcuinfrbyincsno, headers, json, Customer.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			Customer customer = new Customer();
			customer.setRsltCd("ICITSVCOM999");
			return customer;
		}

		Customer customer = response.getBody();

		if ("ICITSVCOM001".equals(customer.getRsltCd())) {
			customer.setCiNo(""); // 없는 사용자이면
			customer.setRsltCd("ICITSVCOM999");
			return customer;
		}

		return customer;
	}
	
	public Customer getcicuemcuinfrlist(final CustInfoVo custInfoVo) {
		log.debug("▶▶▶▶▶▶ InfrList : {}", StringUtil.printJson(custInfoVo));

		final HttpHeaders headers = new HttpHeaders();
		final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		// headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setContentType(mediaType);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		Map<String, Object> params = new HashMap<String, Object>();

		if (StringUtils.hasText(custInfoVo.getCiNo())) {
			params.put("ciNo", custInfoVo.getCiNo());
		}
		
		CipAthtVo cipAthtVo = CipAthtVo.builder().build();
		params.put("cipAthtVo", cipAthtVo);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(params);

		log.debug("▶▶▶▶▶▶ getCicuemcuInfrAllList: {}", StringUtil.printJson(params));

		ResponseEntity<CustInfoResponse> response = this.restApiService.post(this.getcicuemcuinfrlist, headers, json, CustInfoResponse.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			Customer customer = new Customer();
			customer.setRsltCd("ICITSVCOM999");
			return customer;
		}

		CustInfoResponse custInfoResponse = response.getBody();
		Customer customer = custInfoResponse.getCicuemCuInfTcVo()[0]; // 중요) 첫번째 데이터가 최신임.
		
		if ("ICITSVCOM001".equals(custInfoResponse.getRsltCd())) {
			customer.setCiNo(""); // 없는 사용자이면
			customer.setRsltCd("ICITSVCOM999");
			return customer;
		}

		return customer;
	}	
}
