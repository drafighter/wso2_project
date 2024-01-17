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
 * Date   	          : 2020. 9. 16..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.sns.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.exception.ApiBusinessException;
import com.amorepacific.oneap.api.v1.sns.service.SnsApiService;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.sns.SnsConnectRequest;
import com.amorepacific.oneap.common.vo.sns.SnsTokenVo;
import com.amorepacific.oneap.common.vo.sns.SnsUnlinkResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.sns.web 
 *    |_ SnsApiController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 16.
 * @version : 1.0
 * @author : takkies
 */
@Api(tags = { "SNS API" })
@RestController
@RequestMapping("/v1/sns")
public class SnsApiController {

	@Autowired
	private SnsApiService snsApiService;

	/*
	@ApiOperation(value = "SNS 토큰 조회", //
			notes = "SNS 토큰 조회 API", //
			httpMethod = "POST", //
			consumes = MediaType.ALL_VALUE, // consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = SnsTokenResponse.class, //
			hidden = true)
	@PostMapping("/token/{snsType}")
	public SnsTokenResponse getNewToken( //
			@ApiParam(name = "snsType", value = "SNS 타입") //
			final @PathVariable("snsType") String snsType, //
			@ApiParam(name = "snsTokenVo", value = "SNS 토큰 파라미터") //
			final @RequestBody SnsTokenVo snsTokenVo) throws ApiBusinessException {

		SnsTokenResponse response = new SnsTokenResponse();
		return this.snsApiService.getNewToken(response, snsType, snsTokenVo);		
	}
	*/

	@ApiOperation(value = "SNS 계정 연결 해제", //
			notes = "SNS 계정 연결 해제 API", //
			httpMethod = "POST", //
			consumes = MediaType.ALL_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = SnsUnlinkResponse.class, //
			hidden = true)
	@PostMapping("/unlink/{snsType}")
	public SnsUnlinkResponse unlink( //
			@ApiParam(name = "snsType", value = "SNS 타입") //
			final @PathVariable("snsType") String snsType, //
			@ApiParam(name = "snsTokenVo", value = "SNS 토큰 파라미터") //
			final @RequestBody SnsTokenVo snsTokenVo) throws ApiBusinessException {

		SnsUnlinkResponse response = new SnsUnlinkResponse();
		return this.snsApiService.unlink(response, snsType, snsTokenVo);

	}

	@ApiOperation(value = "조인온 SNS연결/해제", //
			notes = "조인온 SNS연결/해제 API", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ApiBaseResponse.class, //
			hidden = true)
	@PostMapping("/joinon")
	public ApiBaseResponse joinOnSnsLinker( //
			@ApiParam(name = "snsConnectRequest", value = "조인온 SNS 연결/해제 파라미터") //
			final @RequestBody SnsConnectRequest snsConnectRequest) throws ApiBusinessException {

		ApiBaseResponse response = new ApiBaseResponse();
		return this.snsApiService.joinOnSnsLinker(response, snsConnectRequest);
	}

	/*
	// 사용자 정보 가져오기
	@ApiOperation(value = "SNS 사용자 정보 가져오기", //
			notes = "SNS 사용자 정보 가져오기 API", //
			httpMethod = "POST", //
			consumes = MediaType.ALL_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = SnsProfileResponse.class, //
			hidden = true)
	@PostMapping("/profile/{snsType}")
	public SnsProfileResponse getProfile( //
			@ApiParam(name = "snsType", value = "SNS 타입") //
			final @PathVariable("snsType") String snsType, //
			@ApiParam(name = "snsTokenVo", value = "SNS 토큰 파라미터") //
			final @RequestBody SnsTokenVo snsTokenVo) throws ApiBusinessException {

		SnsProfileResponse response = new SnsProfileResponse();
		return this.snsApiService.getProfile(response, snsType, snsTokenVo);
	}
	*/
}
