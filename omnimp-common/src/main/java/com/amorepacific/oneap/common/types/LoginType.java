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
 * Date   	          : 2020. 8. 13..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.types;

import com.amorepacific.oneap.common.vo.OmniConstants;

/**
 * <pre>
 * com.amorepacific.oneap.auth.step 
 *    |_ LoginType.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 13.
 * @version : 1.0
 * @author  : takkies
 */

public enum LoginType {
	ALREADY_TRNS_CH(OmniConstants.LOGIN_TYPE_ALREADY_TRNS_CH, "이미 전환가입한 경로 사용자"), //
	DORMANCYFAIL(-40, "휴면 해제 EAI 에러"),
	PWDCHANGE(-30, "비밀번호 변경 캠페인"), //
	PWDFAIL(-20, "비밀번호 실패"), //
	PWDRESET(-15, "비밀번호 초기화"), //
	DISABLED(-10, "탈퇴 계정"), //
	LOCK(-5, "잠김 계정"), //
	ERROR(-1, "에러"), //
	NEW(0, "로그인정보없는신규회원"), //
	LOGIN(OmniConstants.LOGIN_TYPE_LOGIN, "로그인"), // 로그인 
	CS(OmniConstants.LOGIN_TYPE_CS, "고객센터 안내"), // 고객센터 안내
	OMNI_JOIN(OmniConstants.LOGIN_TYPE_OMNI_JOIN, "통합아이디 등록"), // 통합아이디 등록
	CORPAGREE(OmniConstants.LOGIN_TYPE_CORPAGREE, "전사약관동의"), //
	AGREE(OmniConstants.LOGIN_TYPE_AGREE, "채널약관동의"), // 채널약관동의
	TRNS_JOIN(OmniConstants.LOGIN_TYPE_TRNS_JOIN, "전환가입"), // 전환가입
	INTG_JOIN(OmniConstants.LOGIN_TYPE_INTG_JOIN, "통합가입"), // 통합가입
	CONV_JOIN(OmniConstants.LOGIN_TYPE_CONV_JOIN, "통합가입"), // 통합가입
	TRNS_BP(OmniConstants.LOGIN_TYPE_TRNS_BP, "뷰티포인트 전환가입"), // 전환가입
	TRNS_CH_MINE(OmniConstants.LOGIN_TYPE_TRNS_CH_MINE, "경로고객동일아이디 사용"), //
	TRNS_CH_OTHER(OmniConstants.LOGIN_TYPE_TRNS_CH_OTHER, "경로고객동일아이디 타인사용"), //
	NEW_BPTERMS(OmniConstants.LOGIN_TYPE_NEW_BPTERMS, "오픈 후 최초 통합회원 로그인 > 신규 약관 동의 안내"),
	LOCK_ABUSING(70, "어뷰징 잠김 계정"),
	LOCK_ACCESS_LIMIT(-60, "어뷰징 LOCK 접근 제한 계정")//
	
	; 
	
	private int type;
	private String desc;
	
	LoginType(final int type, final String desc) {
		this.type = type;
		this.desc = desc;
	}
	
	public int getType() {
		return this.type;
	}
	public String getDesc() {
		return this.desc;
	}
	
	public static LoginType get(int type) {
		LoginType[] loginTypes = LoginType.values();
		for (LoginType loginType : loginTypes) {
			if (loginType.getType() == type) {
				return loginType;
			}
		}
		return LoginType.ERROR;
	}
}
