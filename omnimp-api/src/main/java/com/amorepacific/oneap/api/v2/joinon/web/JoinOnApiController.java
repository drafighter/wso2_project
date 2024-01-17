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
 * Date   	          : 2022. 10. 6..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v2.joinon.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.v2.joinon.service.JoinOnApiService;
import com.amorepacific.oneap.api.v2.joinon.vo.ChPwdDateResponse;
import com.amorepacific.oneap.api.v2.joinon.vo.CheckOnlineIdVo;
import com.amorepacific.oneap.api.v2.joinon.vo.IdCheckResponse;
import com.amorepacific.oneap.api.v2.joinon.vo.IdCheckVo;
import com.amorepacific.oneap.api.v2.joinon.vo.JoinOnUserResponse;
import com.amorepacific.oneap.api.v2.joinon.vo.OnlineSign2Vo;
import com.amorepacific.oneap.api.v2.joinon.vo.OnlineSignCancelVo;
import com.amorepacific.oneap.api.v2.joinon.vo.OnlineSignVo;
import com.amorepacific.oneap.api.v2.joinon.vo.PasswdChangeResponse;
import com.amorepacific.oneap.api.v2.joinon.vo.PasswordChangeVo;
import com.amorepacific.oneap.api.v2.joinon.vo.PwdChDateResponse;
import com.amorepacific.oneap.api.v2.joinon.vo.PwdChDateVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.v2.joinon.web 
 *    |_ JoinOnApiController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 10. 6.
 * @version : 1.0
 * @author  : judahye
 */
@Api(tags = { "Join-on 대체" })
@Slf4j
@RestController
@RequestMapping("/v2/joinon")
public class JoinOnApiController {
	
	@Autowired
	private JoinOnApiService joinOnApiService;
	
	@ApiOperation(value = "비밀번호 변경일자 확인", //
			notes = "사용자의 마지막 비밀번호 변경일자 확인 API",
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = PwdChDateResponse.class)
	@PostMapping("/getPwdChDate.do")
	public PwdChDateResponse pwdChDate(@ApiParam(name="PwdChDateVo", value = "PwdChDate Vo", required = true) final @RequestBody PwdChDateVo pwdChDateVo) {
		
		return joinOnApiService.pwdChDate(pwdChDateVo);
	}
	
	@ApiOperation(value = "마지막 PW 미변경 동의일자 수정", //
			notes = "사용자의 마지막 PW 미변경 동의 일자 수정 API",
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = PwdChDateResponse.class)
	@PostMapping("/chPwdDate.do")
	public ChPwdDateResponse chPwdDate(@ApiParam(name="PwdChDateVo", value = "PwdChDate Vo", required = true) final @RequestBody PwdChDateVo pwdChDateVo) {
		
		return joinOnApiService.chPwdDate(pwdChDateVo);
	}
	
	@ApiOperation(value = "온라인 회원가입 API", //
			notes = "해당 API를 이용하여 온라인 채널(000, 030, 031, 043, 070, 100+099) 및 뷰티포인트 WEBDB 가입 시켜준다",
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = PwdChDateResponse.class)
	@PostMapping("/onlinesign.do")
	public PasswdChangeResponse onlineSign(@ApiParam(name="OnlineSign", value = "OnlineSign Vo", required = true) final @RequestBody OnlineSignVo onlineSign) {
		
		return joinOnApiService.onlineSign(onlineSign);
	}
	
	@ApiOperation(value = "온라인 회원가입 망취소 API", //
			notes = "온라인 회원가입 호출 후 취소해야 하는 경우 호출하면\r\n"
			+ "온라인 채널(000, 030, 031, 043, 070, 100+099) 및 뷰티포인트 WEBDB 가입 된 내용이 삭제 처리된다\r\n"
			+ "단 해당 API는 가입중 호출이 되어야 되는 내용으로 온라인 회원가입 호출후 5분이내에만 유효하다",
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = PwdChDateResponse.class)
	@PostMapping("/onlinesigncancel.do")
	public JoinOnUserResponse onlineSignCancel(@ApiParam(name="OnlineSignCancelVo", value = "OnlineSign Vo", required = true) final @RequestBody OnlineSignCancelVo onlineSignCancelVo ) {
		
		return joinOnApiService.onlineSignCancel(onlineSignCancelVo);
	}
	
	@ApiOperation(value = "온라인 ID 유효성 체크 API", //
			notes = "온라인 가입이 가능한 아이디인지 체크해준다.",
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = PwdChDateResponse.class)
	@PostMapping("/checkonlineid.do")
	public JoinOnUserResponse checkOnlineId(@ApiParam(name="CheckOnlineIdVo", value = "CheckOnlineId Vo", required = true) final @RequestBody CheckOnlineIdVo checkOnlineIdVo ) {
		
		return joinOnApiService.checkOnlineId(checkOnlineIdVo);
	}
	
	@ApiOperation(value = "온라인 회원ID 중복 체크 API", //
			notes = "온라인 회원중 중복된 ID가 있는지 체크해준다",
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = PwdChDateResponse.class)
	@PostMapping("/idcheck.do")
	public IdCheckResponse idCheck(@ApiParam(name="IdCheckVo", value = "IdCheck Vo", required = true) final @RequestBody IdCheckVo idCheckVo ) {
		
		return joinOnApiService.idCheck(idCheckVo);
	}
	
	@ApiOperation(value = "패스워드 변경 API", //
			notes = "온라인 회원의 password를 변경해준다",
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = PwdChDateResponse.class)
	@PostMapping("/passwdchange.do")
	public PasswdChangeResponse passwdchange(@ApiParam(name="PasswordChangeVo", value = "PasswordChange Vo", required = true) final @RequestBody PasswordChangeVo passwordChangeVo ) {
		
		return joinOnApiService.passwdChange(passwordChangeVo);
	}
	
	@ApiOperation(value = "온라인 ID 유효성 체크 API", //
			notes = "온라인 가입이 가능한 아이디인지 체크해준다.",
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = PwdChDateResponse.class)
	@PostMapping("/checkOnlineId.do")
	public JoinOnUserResponse checkOnlineId2(@ApiParam(name="CheckOnlineIdVo", value = "CheckOnlineId Vo", required = true) final @RequestBody CheckOnlineIdVo checkOnlineIdVo ) {
		
		return joinOnApiService.checkOnlineId(checkOnlineIdVo);
	}
	
	@ApiOperation(value = "온라인 회원가입 API", //
			notes = "해당 API를 이용하여 온라인 채널(000, 030, 031, 043, 070, 100+099) 및 뷰티포인트 WEBDB 가입 시켜준다",
			httpMethod = "POST", //
			consumes = MediaType.APPLICATION_JSON_VALUE, //
			produces = MediaType.APPLICATION_JSON_VALUE, //
			response = PwdChDateResponse.class)
	@PostMapping("/onlineSign.do")
	public PasswdChangeResponse onlineSign2(@ApiParam(name="OnlineSign2 Vo", value = "OnlineSign2 Vo", required = true) final @RequestBody OnlineSign2Vo onlineSign2) {
		
		return joinOnApiService.onlineSign2(onlineSign2);
	}
	
	
}
