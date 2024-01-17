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
 * Date   	          : 2023. 3. 23..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v4.membership.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.v4.membership.service.NaverMemberShipApiService;
import com.amorepacific.oneap.api.v4.membership.validator.NaverMemberShipApiValidator;
import com.amorepacific.oneap.api.v4.membership.vo.NaverRequest;
import com.amorepacific.oneap.api.v4.membership.vo.NaverResponse;
import com.amorepacific.oneap.common.util.StringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v4.membership.web 
 *    |_ NaverMemberShipApiController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 3. 23.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@Api(tags = { "네이버 회원 연동 API" })
@RestController
@RequestMapping("/v4/membership")
public class NaverMemberShipApiController {
	
	@Autowired
	private NaverMemberShipApiService naverMemberShipApiService;

	@ApiOperation(value = "네이버 회원 연동 API - LIF-0002 연동 정보 조회", //
			notes = "제휴사에 저장된 제휴회원연동 매핑 정보를 조회한다. 매핑이 존재하는 경우만 response로 전달한다.", //
			httpMethod = "GET", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE)
	@GetMapping("/checkapuser")
	public ResponseEntity<NaverResponse> checkApUser(
			@ApiParam(name="affiliateMemberIdNo", value = "제휴사회원식별번호", required = true) final @RequestParam String affiliateMemberIdNo,
			@ApiParam(name="interlockMemberIdNo", value = "네이버회원식별번호", required = true) final @RequestParam String interlockMemberIdNo,
			@ApiParam(name="interlockSellerNo", value = "제휴사연동스토어(브랜드)번호", required = true, defaultValue = "PWeO1oa9SWuKdu4tqqzxew") final @RequestParam String interlockSellerNo) {
		NaverRequest naverRequest = new NaverRequest();
		naverRequest.setAffiliateMemberIdNo(affiliateMemberIdNo);
		naverRequest.setInterlockMemberIdNo(interlockMemberIdNo);
		naverRequest.setInterlockSellerNo(interlockSellerNo);
		log.debug("BeautyPoint Membership Naver API - Check AP User Parameter ==== {}", StringUtil.printJson(naverRequest));
		
		if(!NaverMemberShipApiValidator.checkApUser(naverRequest)) return new ResponseEntity<NaverResponse>(HttpStatus.BAD_REQUEST);
		
		NaverResponse naverResponse = naverMemberShipApiService.checkApUser(naverRequest);

		return new ResponseEntity<NaverResponse>(naverResponse, HttpStatus.OK);
	}
	
	@ApiOperation(value = "네이버 회원 연동 API - LIF-0003 연동 정보 삭제", //
			notes = "제휴사에 저장된 제휴회원연동 매핑 정보를 삭제한다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE)
	@PostMapping("/unlinkmembership")
	public ResponseEntity<NaverResponse> unLinkMembership(@ApiParam(name="NaverRequest", value = "조회 파라미터", required = true) final @RequestBody NaverRequest naverRequest) {
		log.debug("BeautyPoint Membership Naver API - UnLink Membership User Parameter ==== {}", StringUtil.printJson(naverRequest));
		
		if(!NaverMemberShipApiValidator.unLinkMembership(naverRequest)) return new ResponseEntity<NaverResponse>(HttpStatus.BAD_REQUEST);
		
		NaverResponse naverResponse = naverMemberShipApiService.unLinkMembership(naverRequest);

		return new ResponseEntity<NaverResponse>(naverResponse, HttpStatus.OK);
	}
	
	@ApiOperation(value = "네이버 회원 연동 API - NIF-0004 연동정보삭제 API", //
			notes = "네이버에 저장된 제휴회원연동 매핑 정보를 삭제한다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE)
	@PostMapping("/unlinkmembership/naver")
	public ResponseEntity<NaverResponse> unLinkNaverMembership(
			@ApiParam(name="chCd", value = "경로구분코드", required = true) final @RequestParam String chCd,
			@ApiParam(name="incsNo", value = "통합고객번호", required = true) final @RequestParam String incsNo) {
		log.debug("BeautyPoint Membership Naver API - UnLink Naver Membership User Parameter ==== {}, {}", chCd, incsNo);
		
		if(!NaverMemberShipApiValidator.unLinkNaverMembership(chCd, incsNo)) return new ResponseEntity<NaverResponse>(HttpStatus.BAD_REQUEST);
		
		NaverResponse naverResponse = naverMemberShipApiService.unLinkNaverMembership(chCd, incsNo);

		return new ResponseEntity<NaverResponse>(naverResponse, HttpStatus.OK);
	}	
}
