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
 * Date   	          : 2020. 11. 20..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v2.join.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amorepacific.oneap.common.util.ConfigUtil;

import lombok.Getter;

/**
 * <pre>
 * com.amorepacific.oneap.auth.api.service 
 *    |_ ApiEndPoint.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 20.
 * @version : 1.0
 * @author  : takkies
 */
@Getter
@Component
public class ApiEndPoint {

	// 고객여부조회 API URL
	@Value("${external.cip.api.getcicuemcuyn}")
	private String getcicuemcuyn;

	// 고객목록조회 API URL
	@Value("${external.cip.api.getcicuemcuinfrlist}")
	private String getcicuemcuinfrlist;

	// 통합고객 등록 API URL
	@Value("${external.cip.api.createcicuemcuinfrjoin}")
	private String createcicuemcuinfrjoin;

	// 휴면 고객 조회 API URL
	@Value("${external.cip.api.getcustdrcsinq}")
	private String getcustdrcsinq;

	// 휴면 복구 신청 API URL
	@Value("${external.cip.api.createcicueldrcsrstrq}")
	private String createcicueldrcsrstrq;

	// 경로가입 API URL
	@Value("${external.cip.api.createcustchnjoin}")
	private String createcustchnjoin;

	// 고객상세조회 API URL
	@Value("${external.cip.api.getcicuemcuinfrbyincsno}")
	private String getcicuemcuinfrbyincsno;

	// 고객상세조회(약관동의,수신동의 배열)
	@Value("${external.cip.api.getcicuemcuinfrarrayincsno}")
	private String getcicuemcuinfrarrayincsno;

	// 회원 CI 업데이트
	@Value("${external.cip.api.updatecustcino}")
	private String updatecustcino;

	// 통합 고객 변경
	@Value("${external.cip.api.updatecicuemcuinfrfull}")
	private String updatecicuemcuinfrfull;

	// 서비스약관동의/철회 다건 저장
	@Value("${external.cip.api.savecicuedcutnca}")
	private String savecicuedcutnca;

	// 마케팅정보수신동의/철회 다건 저장
	@Value("${external.cip.api.savecicuemcuoptilist}")
	private String savecicuemcuoptilist;

	// 고객탈퇴
	@Value("${external.cip.api.createcicuelcuwt}")
	private String createcicuelcuwt;

	// 포인트 조회
	@Value("${external.cip.api.getptinq}")
	private String getptinq;
	
	// 마케팅정보수신동의조회
	@Value("${external.cip.api.getcicuemcuoptilist}")
	private String getcicuemcuoptilist;
	
	// 통합고객가입경로정보조회
	@Value("${external.cip.api.getcustchlist}")
	private String getcustchlist;
	
	// 경로고객번호 존재유무확인
	@Value("${external.cip.api.membership.getCustbyChCsNo}")
	private String getCustbyChCsNo;

	@Value("${omni.api.endpoint.changepassword}")
	private String changePassword; // 비밀번호 변경 api url

	@Value("${omni.api.endpoint.initpassword}")
	private String initPassword; // 비밀번호 초기화 api url
	
	@Value("${omni.api.endpoint.initpasswordcurrentpassword}")
	private String initPasswordCurrentPassword; // 비밀번호 초기화 api url

	@Value("${omni.api.endpoint.createuser}")
	private String createUser; // 옴니 회원 등록 api url

	@Value("${omni.api.endpoint.disableuser}")
	private String disableUser;

	@Value("${omni.api.endpoint.enableuser}")
	private String endableUser;

	@Value("${omni.api.endpoint.channelcreateuser}")
	private String channelCreateUserEndpoint;

	@Value("${omni.api.endpoint.channelsearchuser}")
	private String channelSearchUser;

	@Value("${omni.api.endpoint.snsassociate}")
	private String snsAssociate; // 옴니회원-sns 맵핑 api url

	@Value("${omni.api.endpoint.snsdisconnect}")
	private String snsDisconnect; // 옴니회원-sns 연동해제 api url
	
	@Value("${omni.api.endpoint.verifypasswordpolicy}")
	private String verifyPasswordPolicy;
	
	@Value("${omni.api.endpoint.dscnctsnsass}")
	private String dscnctsnsass;

	@Value("${external.bp.api.createuser}")
	private String createBpUser; // 뷰티포인트 회원등록 api url

	@Value("${external.bp.api.canceluser}")
	private String cancelBpUser; // 뷰티포인트 회원등록 취소 api url

	@Value("${external.bp.api.checkonlineid}")
	private String checkBpOnlineId; // 뷰티포인트 회원아이디유효성체크 api url

	@Value("${external.bp.api.checkid}")
	private String checkBpUserId; // 뷰티포인트 회원아이디 중복체크 api url

	@Value("${external.bp.api.edit}")
	private String checkBpEditUser; // 뷰티포인트 회원정보 수정 api url
	
	@Value("${wso2.ssocommonauthurl}")
	private String ssocommonauthurl;

	private ConfigUtil config = ConfigUtil.getInstance();
	
	public String getApikey() {
		return this.config.apiKey();
	}
	
	public String getChannelApiKey(String chCd, String profile) {
		return this.config.getChannelApi(chCd, "apikey", profile);
	}
	
	public String getChannelApiUrl(String chCd, String profile) {
		return this.config.getChannelApi(chCd, "url", profile);
	}
	
	public String getChannelSearchApi(String chCd, String profile) {
		return this.config.getChannelApi(chCd, "search", profile);
	}
	
	public String getChannelApiCheck(String chCd, String profile) {
		return this.config.getChannelApi(chCd, "check", profile);
	}
	
	public String getChannelAuthCodeUrl(String chCd, String profile) {
		return this.config.getChannelApi(chCd, "authcodeurl", profile);
	}
	
	public String getChannelUserInfoUrl(String chCd, String profile) {
		return this.config.getChannelApi(chCd, "userinfourl", profile);
	}

	public String getChannelLinkUrl(String chCd, String profile) {
		return this.config.getChannelApi(chCd, "linkurl", profile);
	}
	
	public String getSnsRestApikey(String snsType, String profile) {
		return this.config.getSnsInfo(profile, snsType.toLowerCase(), "restkey");
	}
	
	public String getSnsSecretKey(String snsType, String profile) {
		return this.config.getSnsInfo(profile, snsType.toLowerCase(), "secretkey");
	}
	
	public String getSnsCallback(String snsType, String profile) {
		return this.config.getSnsInfo(profile, snsType.toLowerCase(), "callback");
	}
	
	public String getSnsJoinonLinker(String snsType, String profile) {
		return config.getSnsInfo(profile, snsType.toLowerCase(), "joinon");
	}
	
	public boolean isEcpApi(String chCd, String profile) {
		return this.config.isEcpApi(chCd, profile);
	}
}
