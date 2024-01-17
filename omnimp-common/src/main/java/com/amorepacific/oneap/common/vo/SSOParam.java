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
 * Date   	          : 2020. 7. 30..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.login.vo 
 *    |_ LoginParams.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 30.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class SSOParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4340787681279406077L;
	private String code;
	private String session_state;
	private String client_id;
	private String commonAuthCallerPath;
	private String forceAuth;
	private String passiveAuth;
	private String redirect_uri;
	private String response_type;
	private String scope;
	private String tenantDomain;
	private String sessionDataKey;
	private String relyingParty;
	private String type;
	private String sp;
	private String isSaaSApp;
	private String authenticators;
	private String inputType;
	private String errorCode;
	private String authFailure;
	private String authFailureMsg;
	private String snsId;
	private String mappingSnsType;
	private String snsError;
	private String snsAccesstoken;
	
	private String queryString;
	
	private String state;
	private String channelCd; // 채널코드
	private String siteCd; // 사이트코드
	private String redirectUri; // 현재 URL
	private String join; // 회원가입 페이지여부(WSO2에서 전송)
	private String cancelUri; // 취소 시 이동할 URL
	private String drcLgnTp; // SNS 및 휴대폰 로그인으로 Direct 이동
	
	private String hh; // 페이지 헤더(제목) 노출 여부, Y 페이지 헤더(제목) 미노출 
	private String dt; // 디바이스 타입, W web (Desktop Browser), M web (Mobile Browser), A app (MobileApplication) 
	private String ot; // Operating System 타입, W Windows, M MAC, A Android (Galaxy 등), I iOS (iPhone, iPad 등) 
	private String vt; // 
	
	private String kakaoEmbedded; // 카카오 임베디드 로그인 시 
	
	private String popup; // 로그인/회원 가입 페이지 팝업 여부, true 팝업창, null 일반 페이지
	
	private String isCompress; // URL 파라미터 문자열 압축 여부
	
	private String isMembership; // 제휴사 뷰티포인트 연동 여부
	
	private String idSearch; // ID 찾기 바로가기
	private String pwSearch; // PW 찾기 바로가기
	
	private String kakaoChannelPublicId; // Kakao Public ID
	
	private String hmpgUrlToCancelUri; // Home Page URL을 cancelUri 로 세팅 
	
	private String joinAditor;
}
