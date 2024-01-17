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
 * Date   	          : 2020. 8. 12..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.types;

import com.amorepacific.oneap.common.vo.OmniConstants;

/**
 * <pre>
 * com.amorepacific.oneap.auth.join.vo 
 *    |_ JoinType.java
 * 고객통합 플랫폼 API 조회 결과에 대한 구분타입   
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 12.
 * @version : 1.0
 * @author : takkies
 */

public enum JoinDivisionType {

	EXIST("ICITSVCOM000", OmniConstants.JOIN_DIV_TYPE_EXIST), // 정상이면 존재
	JOIN("ICITSVCOM001", OmniConstants.JOIN_DIV_TYPE_JOIN), // 통합고객이 존재하지 않습니다
	NEW("ICITSVCOM002", OmniConstants.JOIN_DIV_TYPE_JOIN), // 통합고객이 존재하지 않습니다
	CHANNEL_JOIN("CHANNEL_JOIN", OmniConstants.JOIN_DIV_TYPE_CHANNEL_JOIN), // 통합고객이 존재하지 않을 경우 채널체크
	NAME_MISMATCH("ICITSVBIZ135", OmniConstants.JOIN_DIV_TYPE_NAME_MISMATCH), // 성명정보 불일치
	DORMANCY("ICITSVBIZ155", OmniConstants.JOIN_DIV_TYPE_DORMANCY), // 휴면고객정보가 존재
	DUPLICATE("ECOMSVVAL004", OmniConstants.JOIN_DIV_TYPE_DUPLICATE), // 중복된 {0} 입니다.   (멤버십카드번호, CI번호)
	OMISSION("ICITSVCOM003", OmniConstants.JOIN_DIV_TYPE_OMISSION), // 필수항목 누락
	INFO_MISMATCH("INFO_MISMATCH", OmniConstants.JOIN_DIV_TYPE_INFO_MISMATCH),
	WITHDRAW("ICITSVBIZ152", OmniConstants.JOIN_DIV_TYPE_WITHDRAW), // 탈퇴사용자 WITHDRAW
	LOCK("LOCK", OmniConstants.JOIN_DIV_TYPE_LOCK), // 잠김
	CONVERSION("CONVERSION", OmniConstants.JOIN_DIV_TYPE_CONVERSION), // 전환가입자
	ERROR("ICITSVCOM999", OmniConstants.JOIN_DIV_TYPE_ERROR); // 시스템오류

	JoinDivisionType(final String code, final int type) {
		this.code = code;
		this.type = type;
	}

	private String code;
	private int type;

	public String getCode() {
		return this.code;
	}

	public int getType() {
		return this.type;
	}

	public static JoinDivisionType getByCode(final String code) {
		JoinDivisionType[] joinDivTypes = JoinDivisionType.values();
		for (JoinDivisionType joinDivType : joinDivTypes) {
			if (joinDivType.getCode().equals(code)) {
				return joinDivType;
			}
		}
		return JoinDivisionType.ERROR;
	}
	
	public static JoinDivisionType get(final int type) {
		JoinDivisionType[] joinDivTypes = JoinDivisionType.values();
		for (JoinDivisionType joinDivType : joinDivTypes) {
			if (joinDivType.getType() == type) {
				return joinDivType;
			}
		}
		return JoinDivisionType.ERROR;
	}
}
