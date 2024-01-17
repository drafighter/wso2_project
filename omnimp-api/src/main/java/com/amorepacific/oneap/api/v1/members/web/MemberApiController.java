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
package com.amorepacific.oneap.api.v1.members.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.v1.members.servicd.MemberApiService;
import com.amorepacific.oneap.api.v1.members.vo.SsgCheckVo;
import com.amorepacific.oneap.api.v1.members.vo.SsgResponse;
import com.amorepacific.oneap.api.v1.members.vo.SsgUnLinkResponse;
import com.amorepacific.oneap.api.v1.members.vo.SsgUnLinkVo;
import com.amorepacific.oneap.api.v1.members.vo.SsgUserVo;
import com.amorepacific.oneap.api.v1.members.vo.UserVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.member.web 
 *    |_ MemberApiController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 3. 10.
 * @version : 1.0
 * @author  : judahye
 */
@Slf4j
@Api(tags = { "SSG 연계 API" })
@RestController
@RequestMapping("/v1/members")
public class MemberApiController {
	
	@Autowired
	private MemberApiService memberApiService;
	

	@ApiOperation(value = "SSG 회원가입 여부 조회", //
			notes = "SSG 회원가입 여부 조회(이름, CI번호, 휴대폰번호 등으로 아모레퍼시픽 회원 여부 확인) 입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = SsgResponse.class)
	@PostMapping("/checkapuser")
	public SsgResponse checkApUser(@ApiParam(name="SsgUserVo", value = "조회 파라미터", required = true) final @RequestBody SsgUserVo ssgUserVo) {
		SsgResponse response = new SsgResponse();
		try {
			response = memberApiService.checkApUser(ssgUserVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	@ApiOperation(value = "SSG와 연동 여부 확인", //
			notes = "SSG와 연동 여부 확인입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = UserVo.class)
	@PostMapping("/checkchanneluser")
	public SsgResponse checkchanneluser(@ApiParam(name="SsgCheckVo", value = "연동 조회 파라미터", required = true) final @RequestBody SsgCheckVo ssgCheckVo) {
		
		SsgResponse response = new SsgResponse();
		
		try {
			response = memberApiService.checkChannelUser(ssgCheckVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	@ApiOperation(value = "SSG와 연동 해제", //
			notes = "SSG와 연동 해제 API입니다.", //
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = UserVo.class)
	@PostMapping("/unLinkSSG")
	public SsgUnLinkResponse unLinkSSG(@ApiParam(name="SsgUnLinkVo", value = "SSG UnLink VO", required = true) final @RequestBody SsgUnLinkVo ssgUnLinkVo) {
		
		return memberApiService.unLinkSSG(ssgUnLinkVo);
	}
}
