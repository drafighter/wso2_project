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
 * Author	          : hkdang
 * Date   	          : 2020. 9. 17..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.social.handler;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.terms.service.TermsService;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.api.ApiResponse;
import com.amorepacific.oneap.common.vo.sns.SnsKakaoAllowedTermsVo;
import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.amorepacific.oneap.common.vo.sns.SnsTermsResponse;
import com.amorepacific.oneap.common.vo.sns.SnsTokenVo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.social.handler 
 *    |_ SnsAuth.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 17.
 * @version : 1.0
 * @author  : hkdang
 */

@Slf4j
@Component
public class SnsAuth {
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private TermsService termsService;
	
	@Autowired
	private CustomerApiService customerApiService;
	
	@Autowired
	private SystemInfo systemInfo;

	private ConfigUtil config = ConfigUtil.getInstance();
	
	@Getter
	private static String naverState = "";
	
	SnsAuth() {
		if(naverState.isEmpty()) {
			SecureRandom random = new SecureRandom();
			naverState = new BigInteger(130, random).toString();
		}
	}
	
	public String getSystemProfile() {
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		log.debug("▶▶▶▶▶▶  [SnsAuth] system profile : {}", profile);
		
		return profile;
	}
	
	public String getKey(String snsType, String keyName) {		
		
		final String key = this.config.getSnsInfo(getSystemProfile(), snsType.toLowerCase(), keyName);
		log.debug("▶▶▶▶▶▶  [SnsAuth getKey] keyName : {}, key : {}", keyName, key);
		
		return key;
	}
	
	public String getAuthorizeUrl(String snsType, String chCd) {

		final String restApiKey = this.config.getSnsInfo(getSystemProfile(), snsType.toLowerCase(), "restkey");
		final String callbackUrl = this.config.getSnsInfo(getSystemProfile(), snsType.toLowerCase(), "callback");
		
		String authorizeUrl = "";
		switch(snsType) {
		case "KA":
			authorizeUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + restApiKey
							+ "&redirect_uri=" + callbackUrl
							+ "&response_type=code"
							+ "&service_terms=" + this.getTermTagListString(chCd) // 동의 약관 선택
							//+ "&channel_public_id=" + this.getChAgreeToReceive(chCd) // 등록 채널 별 카카오톡 수신 설정
							+ "&state=" + chCd;	//채널코드 : 오프라인 redirect 시 사용
			break;
		case "NA":
			authorizeUrl = "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=" + restApiKey
							+ "&redirect_uri=" + callbackUrl
							+ "&state=" + naverState;
			break;
		case "FB":
			authorizeUrl = "https://www.facebook.com/dialog/oauth?client_id=" + restApiKey
							+ "&redirect_uri=" + callbackUrl;
			break;
		case "AP":
			authorizeUrl = "https://appleid.apple.com/auth/authorize?client_id=" + restApiKey
							+ "&redirect_uri=" + callbackUrl
							+ "&response_type=code id_token&response_mode=form_post&scope=name";
			break;			
		}
		
		log.debug("▶▶▶▶▶▶ [SnsAuth] restApiKey : {}", restApiKey);
		log.debug("▶▶▶▶▶▶ [SnsAuth] callbackUrl : {}", callbackUrl);
		log.debug("▶▶▶▶▶▶ [SnsAuth] authorizeUrl : {}", authorizeUrl);
		
		return authorizeUrl;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 경로별 약관 태그 목록
	 * author   : hkdang
	 * date     : 2020. 10. 29. 오전 11:26:10
	 * </pre>
	 * @param chCd
	 * @return
	 */
	public List<String> getChTermsTagList(String chCd) {
		
		if(StringUtils.isEmpty(chCd)) {
			chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		}
		
		final Channel channel = commonService.getChannel(chCd);
		final boolean isOffline = OmniUtil.isOffline(channel);
		final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
		if(isOffline && StringUtils.hasText(onlineChCd)) { // 오프라인으로 회원 가입 시 온라인 약관 태그 목록도 같이 조회
			chCd = onlineChCd;
		}
		
		// DB 에서 해당 경로의 약관 태그 목록 가져옴
		List<String> chTags = this.termsService.getTermsTagList(chCd);
		log.debug("▶▶▶▶▶▶ [SnsAuth] getChTermTag List : {}", chTags);
		
		return chTags;
	}

	// 카카오에 요청할 약관
	public String getTermTagListString(String chCd) {
		
		// 설정에서 본사 약관 목록 가져옴
		String bpTags = config.getTermsTags() + "," 
						+ config.getMarketingTermsTag(OmniConstants.JOINON_CHCD) + "," // 뷰티포인트 문자 수신 
						+ config.getMarketingTermsTag(chCd) + "," // 경로 문자 수신
						+ config.getPrivacyTermsTag(chCd) + "," // 경로 개인정보 제공 동의
						+ config.getYearsTermsTags();
		
		log.debug("▶▶▶▶▶▶ [SnsAuth getTermTagListString] BP Tag List String : {}", bpTags);
		
		if(StringUtils.isEmpty(chCd)) {
			chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		}
		
		// 오프라인이면 기본 약관만
		// if(chCd.equals(OmniConstants.INNISFREE_OFFLINE_CHCD) || chCd.equals(OmniConstants.ESPOIR_OFFLINE_CHCD) || chCd.equals(OmniConstants.ECRIS_OFFLINE_CHCD)) {
		//	log.debug("▶▶▶▶▶▶ [SnsAuth getTermTagListString] Offline Terms : {}", bpTags);
		//	return bpTags;
		// }
		
		String chTags = getChTermsTagList(chCd).toString();
		chTags = chTags.replace("[", "");
		chTags = chTags.replace("]", "");
		chTags = chTags.replace(" ", "");
		log.debug("▶▶▶▶▶▶ [SnsAuth getTermTagListString] CH Tag List String : {}", chTags);
		
		String resultTags = bpTags;
		if(StringUtils.hasText(chTags)) {
			resultTags += ',' + chTags;
		}
		
		log.debug("▶▶▶▶▶▶ [SnsAuth getTermTagListString] KakaoTerms Request Tag List String : {}", resultTags);
		
		return resultTags;
	}
	
	/**
	 * <pre>
	 * comment  : SNS로 동의한 약관 태그 목록
	 * author   : hkdang
	 * date     : 2020. 10. 29. 오전 11:46:50
	 * </pre>
	 * @param chCd
	 * @return boolean
	 */
	public List<String> getSnsAllowedTerms(String snsType, String token) {
			
		if(StringUtils.isEmpty(token)) {
			log.debug("▶▶▶▶▶▶ [SnsAuth] checkKakaoTerms Fail. accessToken is null");
			return null;	
		}
		
		SnsTokenVo snsTokenVo = new SnsTokenVo();
		snsTokenVo.setAccessToken(token);
				
		// 동의한 약관 목록
		List<String> tags = new ArrayList<>();
		SnsTermsResponse termsResponse = this.customerApiService.getSnsTerms(snsType, snsTokenVo);
		for(SnsKakaoAllowedTermsVo terms : termsResponse.getAllowed_service_terms()) {
			tags.add(terms.getTag());
		}
		
		log.debug("▶▶▶▶▶▶ [SnsAuth] getSnsAllowedTerms : {}", tags);
		return tags;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 채널별 카카오톡 수신 동의 (공개 ID 기준) 
	 * author   : hkdang
	 * date     : 2020. 12. 2. 오전 11:20:55
	 * </pre>
	 * @param chCd
	 * @return
	 */
	public String getChPublicIds(String chCd) {
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		String publicId = this.config.getChPublicId(profile, "030"); // default 뷰티포인트
		String chId = this.config.getChPublicId(profile, chCd);
		
		if(StringUtils.isEmpty(chId)) {
			final Channel channel = commonService.getChannel(chCd);
			final boolean isOffline = OmniUtil.isOffline(channel);
			final String onlineChCd = ChannelPairs.getOnlineCd(chCd);
			if(isOffline && StringUtils.hasText(onlineChCd)) { // 오프라인으로 회원 가입 시 온라인 약관 태그 목록도 같이 조회
				chId = this.config.getChPublicId(profile, onlineChCd);
			}
		}
		
		SSOParam ssoParam = (SSOParam) WebUtil.getSession(OmniConstants.SSOPARAM);
		if(ssoParam != null && StringUtils.hasText(ssoParam.getKakaoChannelPublicId())) { // 아모레 성수, 아모레 광교에서 아모레몰을 통한 O2O 서비스로 접근 시 처리
			chId = ssoParam.getKakaoChannelPublicId();
		}
		
		if(StringUtils.hasText(chId)) {
			return publicId + "," + chId;
		}
				
		return publicId;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : hkdang
	 * date     : 2020. 10. 14. 오전 10:53:25
	 * </pre>
	 * @param snsParam	(loginId, incsNo 필수. ci값 있을시 체크)
	 * @return
	 */
	public boolean doSnsMapping(SnsParam snsParam) {
		
		String id = snsParam.getSnsId();
		String type = snsParam.getSnsType();
		
		Object ssoObj = WebUtil.getSession(OmniConstants.SSOPARAM);
		if (ssoObj != null) {
			SSOParam ssoParam = (SSOParam) ssoObj;
			log.debug("▶▶▶▶▶ [doSnsMapping] SSO Param : {}", StringUtil.printJson(ssoParam));
			id = ssoParam.getSnsId();
			type = ssoParam.getMappingSnsType();
		} 
		
		// SsoParam에 없을시 세션에서 한번 더 확인
		if (StringUtils.isEmpty(id)) {
			id = WebUtil.getStringSession(OmniConstants.SNS_FIRST_MAPPING_SNS_ID);
		}
		if (StringUtils.isEmpty(type)) {
			type = WebUtil.getStringSession(OmniConstants.SNS_FIRST_MAPPING_SNS_TYPE);
		}

		if (StringUtils.hasText(id) && StringUtils.hasText(type)) {
			
			snsParam.setSnsId(id);
			snsParam.setSnsType(type);
			
			log.debug("▶▶▶▶▶ [doSnsMapping] SNS Mapping Param : {}", StringUtil.printJson(snsParam));
			ApiResponse res = this.customerApiService.doSnsAssociate(snsParam);
			
			if (res.getResultCode().equals(ResultCode.SUCCESS.getCode())) {
				WebUtil.removeSession(OmniConstants.SNS_FIRST_MAPPING_SNS_ID);
				WebUtil.removeSession(OmniConstants.SNS_FIRST_MAPPING_SNS_TYPE);
				
				return true;
			}
			
			log.debug("▶▶▶▶▶ [doSnsMapping] doSnsAssociate Fail Code: {}, Msg: {}", res.getResultCode(), res.getMessage());
		} else {
			log.debug("▶▶▶▶▶ [doSnsMapping] SNS Mapping Info is NULL");
		}
		
		return false;
	}
	
}
