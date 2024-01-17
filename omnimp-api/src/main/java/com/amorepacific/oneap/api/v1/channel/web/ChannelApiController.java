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
 * Date   	          : 2020. 9. 15..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.channel.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.v1.channel.service.ChannelApiService;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.api.BpChangePasswordRequest;
import com.amorepacific.oneap.common.vo.api.BpChangePasswordResponse;
import com.amorepacific.oneap.common.vo.api.BpUserData;
import com.amorepacific.oneap.common.vo.api.CreateChCustRequest;
import com.amorepacific.oneap.common.vo.api.SearchChCustRequest;
import com.amorepacific.oneap.common.vo.api.SearchChCustResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.channel.web 
 *    |_ ChannelApiController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 15.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Api(tags = { "채널 API" })
@RestController
@RequestMapping("/v1/channel")
public class ChannelApiController {

	@Autowired
	private ChannelApiService channelApiService;

	// 온라인/오프라인 경로 등록
	@ApiOperation(value = "채널 온라인/오프라인 경로 등록", //
			notes = "채널 온라인/오프라인 경로 등록 API", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class, //
			hidden = true)
	@PostMapping("/regist/{chCd}")
	public ApiBaseResponse registChannelCustomer( //
			@ApiParam(name = "chCd", value = "채널 코드") //
			final @PathVariable("chCd") String chCd, //
			@ApiParam(name = "createChCustRequest", value = "채널 등록 파라미터") //
			final @RequestBody CreateChCustRequest createChCustRequest) {

		ApiBaseResponse response = new ApiBaseResponse();
		response = this.channelApiService.registChannelCustomer(response, chCd, createChCustRequest);

		return response;
	}

	// 경로자체회원 ID 조회
	@ApiOperation(value = "경로자체회원 ID 조회", //
			notes = "경로자체회원 ID 조회 API", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = SearchChCustResponse.class, //
			hidden = true)
	@PostMapping("/search/{chCd}")
	public SearchChCustResponse searchChannelCustomer( //
			@ApiParam(name = "chCd", value = "채널 코드") //
			final @PathVariable("chCd") String chCd, //
			@ApiParam(name = "searchCustRequest", value = "경로자체회원 ID 조회 파라미터") //
			final @RequestBody SearchChCustRequest searchCustRequest) {

		SearchChCustResponse response = new SearchChCustResponse();
		response = this.channelApiService.searchChannelCustomer(response, chCd, searchCustRequest);

		return response;
	}
	
	@ApiOperation(value = "뷰티포인트 비밀번호 변경", //
			notes = "뷰티포인트 비밀번호 변경 API", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = BpChangePasswordResponse.class, //
			hidden = true)
	@PostMapping("/beautypoint/changepassword")
	public BpChangePasswordResponse beautyPointChangePassword( //
			@ApiParam(name = "bpChangePasswordRequest", value = "뷰티포인트 비밀번호 변경") //
			final @RequestBody BpChangePasswordRequest bpChangePasswordRequest) {
		
		BpChangePasswordResponse response = new BpChangePasswordResponse();
		
		final BpUserData userData = new BpUserData();
		userData.setIncsNo(bpChangePasswordRequest.getIncsNo());
		userData.setCstmid(bpChangePasswordRequest.getCstmId());
		ApiResponse apiResponse = this.channelApiService.beautyPointCheckOnlineId(response, userData);
		
		log.debug("beauty point online user check : {}", StringUtil.printJson(apiResponse));
		
		if ("000".equals(apiResponse.getRsltCd())) {
			response = this.channelApiService.beautyPointChangePassword(response, bpChangePasswordRequest);
			
			log.debug("beauty point change password : {}", StringUtil.printJson(response));
			
		} else {
			response.setRsltCd(apiResponse.getRsltCd());
			response.setRsltMsg(apiResponse.getRsltMsg());
		}
		
		return response;
	}
}
