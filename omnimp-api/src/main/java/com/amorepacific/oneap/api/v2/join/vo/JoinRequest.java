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
 * Date   	          : 2020. 8. 27..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v2.join.vo;

import java.util.List;

import com.amorepacific.oneap.common.vo.Marketing;
import com.amorepacific.oneap.common.vo.Terms;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.join.vo 
 *    |_ JoinRequest.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 27.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class JoinRequest {

	private String unm;
	private String uid;
	private String loginid;
	private String loginpassword;
	private String loginconfirmpassword;
	private String agreeCheck;
	private String joinType;
	private String gender;
	private String phone;
	private String birth;
	private String ci;
	private String national;
	private String chcd;
	private String incsno;
	private String integrateid; // 오프라인인경우 통합회원아이디 등록 여부
	
	private List<Terms> bpterms; // 뷰티포인트 약관 동의
	
	private List<Terms> terms; // 채널 약관 동의 정보
	
	private List<Marketing> marketings; //  수신동의
	
	private boolean callOnlineApi = true; // 온라인 API 호출여부
	private boolean callOfflineApi = true; // 오프라인 API 호출여부
	private boolean callBpApi = true; // 뷰티포인트 API 호출여부
	
	private boolean transCustomer = false; // 전환처리자
	
	private String joinPrtnId;
	private String joinPrtnNm;
	private String joinEmpId;
	private String addInfo;
	private String redirectUrl;
	
	private String convsType;
	private boolean offLine;
	
	private String agreeType;
	
	private boolean corpTerms;
	
	private String trnsType;
	
	private String withdrawCode;
	private String withdrawDate;
	private boolean withdraw;
	
	private String kkoIntlOptiYn; // 카카오 알림톡 수신동의 여부: Y/N
	private String kkoIntlOptiDt; // 카카오 알림톡 수신동의 일시
	
	private String osType; // 고객가입디바이스운영체제구분코드 01: Android, 02 : iOS, 03 : MAC, 04 : WINDOWS
	private String deviceType; // 가입디바이스코드 W:WEB, M:MOBILE, A:APP
	private String switchJoinYn; // 경로고객통합전환가입여부 Y : 전환가입을 통한회원가입 유입, N : 그 외는 모두 N (default)
	private String snsIdPrcnCd; // SNS계정제휴사코드 KA: 카카오싱크를 이용한 가입 
	
	private boolean joinCnclYn = false;
}
