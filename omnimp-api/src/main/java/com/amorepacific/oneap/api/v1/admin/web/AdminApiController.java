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
package com.amorepacific.oneap.api.v1.admin.web;

import java.util.List;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.v1.admin.service.AdminApiService;
import com.amorepacific.oneap.api.v1.admin.vo.AdminSearchList;
import com.amorepacific.oneap.api.v1.admin.vo.AdminSearchRequest;
import com.amorepacific.oneap.api.v1.admin.vo.AdminSearchResponse;
import com.amorepacific.oneap.api.v1.admin.vo.KakaoNoticeMsg;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.admin.web 
 *    |_ AdminApiController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 28.
 * @version : 1.0
 * @author  : takkies
 */
@Slf4j
@Api(tags = { "Admin API" })
@RestController
@RequestMapping("/v1/admin")
public class AdminApiController {

	@Autowired
	private AdminApiService adminApiService; 
	
	// 온라인/오프라인 경로 등록
	@ApiOperation(value = "관리자 검색", //
			notes = "관리자 검색 API", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = AdminSearchResponse.class, //
			hidden = false)
	@PostMapping("/search")
	public AdminSearchResponse search(
			@ApiParam(name = "adminSearchRequest", value = "검색 파라미터") //
			final @RequestBody AdminSearchRequest adminSearchRequest
			) {
		
		AdminSearchResponse response = new AdminSearchResponse();
		
		log.debug("▶▶▶▶▶▶ AdminSearchRequest = {}", StringUtil.printJson(adminSearchRequest));
		
		// 하단에 보여지는 패이지 갯수, 현재 페이지 번호  값 1 이상
		if( adminSearchRequest.getPageUnitSize() < 1 || adminSearchRequest.getCurrentPage() < 1) {
			response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);			
		} else {
			// 통합고객번호 valid check
			if(StringUtils.hasText(adminSearchRequest.getIncsNo())) {
				if( !StringUtil.checkParameter("^[0-9]{9}$", adminSearchRequest.getIncsNo()) ) {
					response.SetResponseInfo(ResultCode.REQ_INVALID_PARAM);
					return response;
				} else {
					
					List<AdminSearchList> list = this.adminApiService.getAdminUser(adminSearchRequest);
					if(list.isEmpty()) {
						response.SetResponseInfo(ResultCode.USER_NOT_FOUND);
						return response;
					}
					
					if( StringUtils.hasText(adminSearchRequest.getWebId()) ) {
						boolean isValid = false;
						for(AdminSearchList user : list) {
							if(user.getWebId().equals(adminSearchRequest.getWebId())) {
								isValid = true;
								break;
							}
						}
						
						if(isValid == false) {
							response.SetResponseInfo(ResultCode.USER_INVALID);
							return response;
						}
					}
				}	
			} else {
				if(StringUtils.isEmpty(adminSearchRequest.getWebId())) {	// 통합고객번호, 사용자아이디 모두 없으면 사용자 정보 에러
					response.SetResponseInfo(ResultCode.USER_INVALID);
					return response;
				}
			}
			
			final String trxuuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
			int offsetSize = (adminSearchRequest.getCurrentPage() - 1) * adminSearchRequest.getListSize();
			adminSearchRequest.setOffsetSize(offsetSize);
			
			response = this.adminApiService.getAdminUserSearchListPaging(adminSearchRequest);
			response.SetResponseInfo(ResultCode.SUCCESS);
			
			if (StringUtils.isEmpty(trxuuid)) {
				response.setTrxUuid(MDC.get(OmniConstants.TRX_UUID));
			} else {
				response.setTrxUuid(trxuuid);
			}
		}
		
		return response;		
	}
	
	@ApiOperation(value = "카카오 알림톡 발송 테스트 API", //
			notes = "카카오톡으로 알림톡을 발송한다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE)
	@PostMapping("/kakao/notice/send")
	public ResponseEntity<ApiBaseResponse> sendKakaoNotice(@ApiParam(name="kakaoNoticeMsg", value = "휴대폰 번호", required = true) final @RequestBody KakaoNoticeMsg kakaoNoticeMsg) {
		log.debug("Send Kakao Notice API Parameter ==== {}", StringUtil.printJson(kakaoNoticeMsg));
		
		ApiBaseResponse ApiBaseResponse = adminApiService.sendKakaoNotice(kakaoNoticeMsg);

		return new ResponseEntity<ApiBaseResponse>(ApiBaseResponse, HttpStatus.OK);
	}		
}
