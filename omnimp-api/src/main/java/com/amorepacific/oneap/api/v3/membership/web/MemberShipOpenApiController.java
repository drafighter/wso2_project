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
 * Date   	          : 2023. 2. 9..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v3.membership.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.v3.membership.service.MemberShipOpenApiService;
import com.amorepacific.oneap.api.v3.membership.vo.ChkApUserResponse;
import com.amorepacific.oneap.api.v3.membership.vo.ChkApUserVo;
import com.amorepacific.oneap.api.v3.membership.vo.LinkMembershipResponse;
import com.amorepacific.oneap.api.v3.membership.vo.LinkMembershipVo;
import com.amorepacific.oneap.api.v3.membership.vo.UnLinkMembershipVo;
import com.amorepacific.oneap.api.v3.membership.validator.MemberShipOpenApiValidator;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v3.membership.web 
 *    |_ MemberShipOpenApiController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 2. 9.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@Api(tags = { "외부 제휴 회원 연동 Open API" })
@RestController
@RequestMapping("/v3/membership")
public class MemberShipOpenApiController {
	
	@Autowired
	private MemberShipOpenApiService memberShipOpenApiService;
	
	private boolean checkCommonValidation(ApiBaseResponse response) {
		return (!ResultCode.REQ_REQUIRED_PARAM_EMPTY.getCode().equals(response.getResultCode()) && !ResultCode.REQ_INVALID_PARAM.getCode().equals(response.getResultCode()));
	}	

	@ApiOperation(value = "외부 제휴 회원 연동 Open API - 회원 여부 조회", //
			notes = "제휴사 코드, 제휴사 측의 고객 Key를 이용하여 회원 연동 여부 조회", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ChkApUserResponse.class)
	@PostMapping("/checkapuser")
	public ChkApUserResponse checkApUser(@ApiParam(name="ChkApUserVo", value = "조회 파라미터", required = true) final @RequestBody ChkApUserVo chkApUserVo) {
		log.debug("BeautyPoint Membership Open API - Check AP User Parameter ==== {}", StringUtil.printJson(chkApUserVo));
		
		ChkApUserResponse response = new ChkApUserResponse();
		response = MemberShipOpenApiValidator.checkApUser(response, chkApUserVo);
		
		if (checkCommonValidation(response)) {
			try {
				response = memberShipOpenApiService.checkApUser(chkApUserVo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		return response;
	}
	
	@ApiOperation(value = "외부 제휴 회원 연동 Open API - 회원 가입", //
			notes = "회원의 개인정보(이름, 생년월일, 휴대폰번호, CI번호, 제휴사 측의 고객 Key)를 이용하여 회원 연동 요청", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ChkApUserResponse.class)
	@PostMapping("/linkmembership")
	public LinkMembershipResponse linkMembership(@ApiParam(name="LinkMembershipVo", value = "조회 파라미터", required = true) final @RequestBody LinkMembershipVo linkMembershipVo) {
		log.debug("BeautyPoint Membership Open API - Link Membership Parameter ==== {}", StringUtil.printJson(linkMembershipVo));
		
		LinkMembershipResponse response = new LinkMembershipResponse();
		response = MemberShipOpenApiValidator.linkMembership(response, linkMembershipVo);
		
		if (checkCommonValidation(response)) {
			try {
				response = memberShipOpenApiService.linkMembership(linkMembershipVo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		return response;
	}	
	
	@ApiOperation(value = "외부 제휴 회원 연동 Open API - 제휴사 경로 탈퇴", //
			notes = "제휴사 개인정보 제3자 제공동의 동의 및 제휴사 경로 탈퇴 처리", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = ChkApUserResponse.class)
	@PostMapping("/unlinkmembership")
	public ApiBaseResponse unLinkMembership(@ApiParam(name="unLinkMembershipVo", value = "조회 파라미터", required = true) final @RequestBody UnLinkMembershipVo unLinkMembershipVo) {
		log.debug("BeautyPoint Membership Open API - UnLink Membership Parameter ==== {}", StringUtil.printJson(unLinkMembershipVo));
		
		ApiBaseResponse response = new ApiBaseResponse();
		response = MemberShipOpenApiValidator.unLinkMembership(response, unLinkMembershipVo);
		
		if (checkCommonValidation(response)) {
			try {
				response = memberShipOpenApiService.unLinkMembership(unLinkMembershipVo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		return response;
	}	
}
