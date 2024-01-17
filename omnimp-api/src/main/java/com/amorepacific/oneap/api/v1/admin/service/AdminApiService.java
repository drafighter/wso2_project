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
 * Date   	          : 2020. 9. 28..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.admin.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.api.common.service.CommonCustomerApiService;
import com.amorepacific.oneap.api.common.service.RestApiService;
import com.amorepacific.oneap.api.v1.admin.mapper.AdminApiMapper;
import com.amorepacific.oneap.api.v1.admin.vo.AdminSearchList;
import com.amorepacific.oneap.api.v1.admin.vo.AdminSearchRequest;
import com.amorepacific.oneap.api.v1.admin.vo.AdminSearchResponse;
import com.amorepacific.oneap.api.v1.admin.vo.KakaoNoticeMsg;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.SearchChCustRequest;
import com.amorepacific.oneap.common.vo.api.SearchChCustResponse;
import com.amorepacific.oneap.common.vo.kakao.KakaoNoticeRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.admin.service 
 *    |_ AdminApiService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 28.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class AdminApiService {
	
	@Value("${kakao.notice.id}")
	private String kakaoNoticeId;
	
	@Value("${kakao.notice.callback}")
	private String kakaoNoticeCallback;
	
	@Value("${kakao.notice.templatecode}")
	private String kakaoNoticeTemplateCode;
	
	@Value("${kakao.notice.failedtype}")
	private String kakaoNoticeFailedType;
	
	@Value("${kakao.notice.failedsubject}")
	private String kakaoNoticeFailedSubject;
	
	@Value("${kakao.notice.profilekey}")
	private String kakaoNoticeProfileKey;
	
	@Value("${kakao.notice.applclcd}")
	private String kakaoNoticeApplClCd;
	
	@Value("${kakao.notice.pltfclcd}")
	private String kakaoNoticePltfClCd;

	@Autowired
	private AdminApiMapper adminApiMapper;

	@Autowired
	private RestApiService restApiService;
	
	@Autowired
	private CommonCustomerApiService customerApiService;
	
	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	public List<AdminSearchList> getAdminUser(final AdminSearchRequest adminSearchRequest) {
		return this.adminApiMapper.getAdminUser(adminSearchRequest);
	}
	
	public AdminSearchResponse getAdminUserSearchListPaging(final AdminSearchRequest adminSearchRequest) {

		log.debug("{}", StringUtil.printJson(adminSearchRequest));

		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		log.debug("▶▶▶▶▶▶ AdminSearchResponse's system profile : {}", profile);
		
		AdminSearchResponse response = new AdminSearchResponse();
		
		List<AdminSearchList> list = this.adminApiMapper.getAdminUserSearchListPaging(adminSearchRequest);		
		
		// 통합고객번호 없을시 경로API 호출해서 휴대폰번호 넣어줌
		for(AdminSearchList search : list ) {
			
			// 통합고객번호 있으면 continue
			if( StringUtils.hasText(search.getIncsNo()) ) {
				continue;
			}
				
			// 경로코드 없으면 continue
			if(StringUtils.isEmpty(search.getChCd())) {
				continue;
			}
			
			final String apiKey = this.config.getChannelApi(search.getChCd(), "apikey", profile);
			final String apiUrl = this.config.getChannelApi(search.getChCd(), "search", profile);
			
			// 경로API URL 없으면 continue
			if(StringUtils.isEmpty(apiUrl)) {
				continue;
			}

			final HttpHeaders headers = new HttpHeaders();
			final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			headers.setContentType(mediaType);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));			
			if (StringUtils.hasText(apiKey)) {
				headers.add(OmniConstants.XAPIKEY, apiKey);
			}
			
			SearchChCustRequest searchChCustRequest = new SearchChCustRequest();
			searchChCustRequest.setWebId(search.getWebId());
			
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			String json = gson.toJson(searchChCustRequest);
			
			ResponseEntity<SearchChCustResponse> apiResponse = this.restApiService.post(apiUrl, headers, json, SearchChCustResponse.class);
			
			log.debug("▶▶▶▶▶▶ [ AdminSearchResponse] search channel response: {}", StringUtil.printJson(apiResponse.getBody()));
			
			if(apiResponse == null || apiResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				continue;
			}
			
			if(apiResponse.getBody() == null || apiResponse.getBody().toString().isEmpty()) {
				continue;
			}
			
			if(apiResponse.getBody().getUserInfo().length == 0) {
				continue;
			}
			
			search.setName( apiResponse.getBody().getUserInfo()[0].getName() );
			search.setMobile( apiResponse.getBody().getUserInfo()[0].getPhone() );
		}
		response.setList(list);

		int totalSize = this.adminApiMapper.getAdminUserSearchListPagingCount(adminSearchRequest);
		if(totalSize == 0) {
			response.SetResponseInfo(ResultCode.SEARCH_INVALID_PAGE);
			return response;
		}
		
		response.setCurrentPage(adminSearchRequest.getCurrentPage());
		response.setListSize(adminSearchRequest.getListSize());
		response.setOffsetSize(adminSearchRequest.getOffsetSize());
		response.setPageUnitSize(adminSearchRequest.getPageUnitSize());
		response.setTotalSize(totalSize);

		int totalPageCount = (int) Math.ceil((float) response.getTotalSize() / (float) response.getListSize());
		int startPageIndex = ((response.getCurrentPage() - 1) / response.getPageUnitSize()) * response.getPageUnitSize();
		if (startPageIndex == 0 || startPageIndex % 10 == 0) {
			startPageIndex += 1;
		}
		int endPageIndex = startPageIndex + response.getPageUnitSize() - 1;
		if (endPageIndex > totalPageCount) {
			endPageIndex = totalPageCount;
		}
		response.setTotalPageIndexCount(totalPageCount);
		response.setStartPageIndex(startPageIndex);
		response.setEndPageIndex(endPageIndex);
		return response;
	}
	
	public ApiBaseResponse sendKakaoNotice(final KakaoNoticeMsg kakaoNoticeMsg) {
		KakaoNoticeRequest request = KakaoNoticeRequest.builder()
				.ID(kakaoNoticeId)
				.STATUS("1")
				.CALLBACK(kakaoNoticeCallback)
				.TEMPLATE_CODE(kakaoNoticeTemplateCode)
				.FAILED_TYPE(kakaoNoticeFailedType)
				.FAILED_SUBJECT(kakaoNoticeFailedSubject)
				.PROFILE_KEY(kakaoNoticeProfileKey)
				.APPL_CL_CD(kakaoNoticeApplClCd)
				.PLTF_CL_CD(kakaoNoticePltfClCd).build();
		
		// 카카오 알림톡 발송될 휴대폰번호 설정
		request.setPHONE(kakaoNoticeMsg.getPhone());
		
		// 카카오 알림톡 메세지 발송 시간 설정
		request.setREQDATE(DateUtil.getTimestampAfterSecond("yyyy-MM-dd HH:mm:ss", 60));
		
		// 카카오 알림톡 메세지 설정
		String msg = "안녕하세요, 아모레퍼시픽 뷰티포인트입니다.\r\n"
				+ "\r\n"
				+ "㈜아모레퍼시픽은 " + kakaoNoticeMsg.getPrtnName() + "(으)로부터 고객님의 개인정보를 제공받았으며, 개인정보보호법 제20조에 의거하여 아래와 같이 개인정보 수집 출처를 안내해 드립니다.\r\n"
				+ "\r\n"
				+ "- 개인정보 수집 출처: " + kakaoNoticeMsg.getPrtnName() + "\r\n"
				+ "- 개인정보 수집 항목: " + kakaoNoticeMsg.getCategory() + "\r\n"
				+ "- 개인정보 처리 목적: " + kakaoNoticeMsg.getPurpose() + "\r\n"
				+ "- 개인정보 보유 및 이용기간: " + DateUtil.getCurrentDateString("yyyy-MM-dd") + "부터 철회일까지" + "\r\n"
				+ "\r\n"
				+ "개인정보 처리를 원치 않는 경우 아모레퍼시픽 개인정보 처리 동의 철회 페이지(" + kakaoNoticeMsg.getUrl() + ")를 통해 개인정보 처리 정지를 요청하실 수 있습니다.";
		request.setMSG(msg);
		request.setFAILED_MSG(msg);
		
		return customerApiService.sendKakaoNoticeTalkEai(request);
	}
}
