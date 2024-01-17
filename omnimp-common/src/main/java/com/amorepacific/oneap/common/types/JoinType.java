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
 * Date   	          : 2020. 8. 14..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.types;

import com.amorepacific.oneap.common.vo.OmniConstants;

/**
 * <pre>
 * com.amorepacific.oneap.common.types 
 *    |_ JoinType.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 14.
 * @version : 1.0
 * @author  : takkies
 */

public enum JoinType {
	
	ERROR(0, "에러"), //
	JOIN(OmniConstants.JOIN_TYPE_JOIN, "신규고객"), //
	JOIN_OFF(OmniConstants.JOIN_TYPE_JOIN_OFF, "통합아이디 등록"), //
	JOINED_STEP_OFF(OmniConstants.JOIN_TYPE_JOIN_STEP_OFF, "통합아이디 등록"), //
	CHANNEL(OmniConstants.JOIN_TYPE_CHANNEL, "자체고객"), //
	JOINED(OmniConstants.JOIN_TYPE_JOINED, "회원가입사실안내"), //
	JOINED_OMNI_CH(OmniConstants.JOIN_TYPE_JOINED_OMNI_CH, "회원가입사실안내(옴니,채널)"), //
	JOINED_OMNI(OmniConstants.JOIN_TYPE_JOINED_OMNI, "회원가입사실안내(옴니)"), //
	JOINED_OFF(OmniConstants.JOIN_TYPE_JOINED_OFF, "타 오프라인 경로(자체) 가입고객"), //
	JOINED_CH_OFF(OmniConstants.JOIN_TYPE_JOINED_CH_OFF, "오프라인 회원 가입 사일 안내"), //
	JOINED_AGREE_CH_OFF(OmniConstants.JOIN_TYPE_JOINED_CH_AGREE, "오프라인 경로 약관 동의"), //
	JOINED_AGREE(OmniConstants.JOIN_TYPE_JOINED_AGREE, "경로약관동의"), //
	COVERSION(OmniConstants.JOIN_TYPE_CONVERSION, "통합회원전환"), //
	CHANNEL_OFF(OmniConstants.JOIN_TYPE_CHANNEL_OFF, ""), //
	;
	private int type;
	private String desc;
	
	JoinType(final int type, final String desc) {
		this.type = type;
		this.desc = desc;
	}
	
	public int getType() {
		return this.type;
	}
	public String getDesc() {
		return this.desc;
	}
	
	public static JoinType get(int type) {
		JoinType[] joinTypes = JoinType.values();
		for (JoinType joinType : joinTypes) {
			if (joinType.getType() == type) {
				return joinType;
			}
		}
		return JoinType.ERROR;
	}
}
