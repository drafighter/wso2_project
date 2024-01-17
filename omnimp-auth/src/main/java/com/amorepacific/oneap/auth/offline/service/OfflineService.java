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
 * Date   	          : 2023. 9. 6..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.offline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.api.service.ApiEndPoint;
import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.offline.vo.OfflineLoginRequest;
import com.amorepacific.oneap.auth.offline.vo.OfflineLoginResponse;
import com.amorepacific.oneap.auth.offline.vo.SendKakaoVo;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.types.LoginType;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.LoginStepVo;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.kakao.KakaoNoticeRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.offline.service 
 *    |_ OfflineService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 9. 6.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@Service
public class OfflineService {
	
	@Autowired
	private CustomerApiService customerApiService;
	
	@Autowired
	private ApiEndPoint apiEndpoint;
	
	@Autowired
	private SystemInfo systemInfo;
	
	private ConfigUtil config = ConfigUtil.getInstance();

	public LoginStepVo loginStatusCheck(final String chCd, final String loginId, final String loginPwd) {
		
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		// POS 로그인 API 호출을 위한 암호화 Key
		String key = config.getChannelApi(chCd, "aeskey", profile);
		
		if(StringUtils.isEmpty(key)) {
			// 암호화를 위한 Key 가 없는 경우 에러 리턴
			log.error("▶▶▶▶▶▶ [Offline login status check] Encrypt Key is null, chCd : {}", chCd);
			return new LoginStepVo(LoginType.ERROR, null, null, null);
		}
		
		// POS 로그인 API 호출을 위한 파라미터 생성
		// String s_controller = config.getChannelApi(chCd, "s_controller", profile);
		// String s_method = config.getChannelApi(chCd, "s_method", profile);
		String chnCd = SecurityUtil.encript(chCd, key);
		String login_id = SecurityUtil.encript(loginId, key);
		String login_pw = SecurityUtil.encript(loginPwd, key);
		// String eqDiv = SecurityUtil.encript(config.getChannelApi(chCd, "eqdiv", profile), key);
		
		// Request 파라미터 생성
		OfflineLoginRequest offlineLoginRequest = OfflineLoginRequest.builder().chnCd(chnCd).login_id(login_id).login_pw(login_pw).build();
		
		// 필수 파라미터 체크
		if(StringUtils.isEmpty(chnCd) || StringUtils.isEmpty(login_id) || StringUtils.isEmpty(login_pw)) {
			// API 호출을 위한 Request 파라미터 체크
			log.error("▶▶▶▶▶▶ [Offline login status check] Parameter is null, offline login request param : {}", StringUtil.printJson(offlineLoginRequest));
			return new LoginStepVo(LoginType.ERROR, null, null, null);
		}
		
		OfflineLoginResponse offlineLoginResponse = customerApiService.getOfflineLoginInfo(chCd, key, offlineLoginRequest);
		
		if(offlineLoginResponse == null || !"1".equals(offlineLoginResponse.getResultCode()) || ResultCode.SYSTEM_ERROR.getCode().equals(offlineLoginResponse.getResultCode())) {
			return new LoginStepVo(LoginType.ERROR, null, null, null);
		}
		
		if("N".equals(offlineLoginResponse.getLogin_yn())) {
			return new LoginStepVo(LoginType.PWDFAIL, null, null, null);
		}
		
		// 성공일 경우 OfflineLoginResponse 객체 세션에 저장 후 리턴
		WebUtil.setSession(OmniConstants.OFFLINE_LOGIN_RESPONSE, offlineLoginResponse);
		
		return new LoginStepVo(LoginType.LOGIN, null, null, null);
	}
	
	public ApiBaseResponse sendKakaoNotice(final SendKakaoVo sendKakaoVo, final String chCd, final String joinUrl) {
		KakaoNoticeRequest request = KakaoNoticeRequest.builder()
				.ID(config.getKakaoNoticeId(chCd))
				.STATUS("1")
				.CALLBACK(this.apiEndpoint.getKakaoNoticeCallback())
				.TEMPLATE_CODE(config.getKakaoNoticeTemplateCode(chCd))
				.FAILED_TYPE(this.apiEndpoint.getKakaoNoticeFailedType())
				.FAILED_SUBJECT("[회원가입 안내]")
				.PROFILE_KEY(this.apiEndpoint.getKakaoNoticeProfileKey())
				.APPL_CL_CD(this.apiEndpoint.getKakaoNoticeApplClCd())
				.PLTF_CL_CD(this.apiEndpoint.getKakaoNoticePltfClCd()).build();
		
		// 카카오 알림톡 발송될 휴대폰번호 설정
		request.setPHONE(sendKakaoVo.getUserPhone());
		
		// 카카오 알림톡 메세지 발송 시간 설정
		request.setREQDATE(DateUtil.getTimestampAfterSecond("yyyy-MM-dd HH:mm:ss", 60));

		String kakaoNoticePrtnNm = config.getKakaoNoticePrtnNm(chCd);
		
		// 카카오 알림톡 메세지 설정
		String msg = "[회원가입 안내]\r\n"
				+ "\r\n"
				+ "아모레퍼시픽 뷰티포인트 통합 아이디로 " + kakaoNoticePrtnNm + "의 서비스를 이용하실 수 있습니다.\r\n"
				+ "아래 링크를 통해 회원 가입하시고, " + kakaoNoticePrtnNm + "에서 제공하는 다양한 혜택을 누려보세요.\r\n"
				+ "\r\n"
				+ "- 회원가입 링크: " + joinUrl
				+ "\r\n"
				+ "\r\n"
				+ "※ 본 메시지는 아모레퍼시픽 회원가입 요청 고객 대상으로 발송된 알림입니다.";
		request.setMSG(msg);
		request.setFAILED_MSG(msg);
		
		return customerApiService.sendKakaoNoticeTalkEai(request);
	}
}
