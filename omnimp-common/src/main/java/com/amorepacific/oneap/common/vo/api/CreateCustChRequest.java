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
 * Date   	          : 2023. 2. 17..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ CreateCustChRequest.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 2. 17.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
public class CreateCustChRequest {

	private String joinCnclYn;
	private List<CicuedCuChTcVo> cicuedCuChTcVo;
	
	public void addCicuedCuChTcVo(final CicuedCuChTcVo cicuedCuChTcVos) {
		if (cicuedCuChTcVo == null) {
			cicuedCuChTcVo = new ArrayList<>();
		}
		cicuedCuChTcVo.add(cicuedCuChTcVos);
	}
	
	@Data
	public static class CicuedCuChTcVo {
		private String incsNo; //필수: 통합고객번호
		private String chCd; // 필수: 경로구분코드
		private String chcsNo; // 필수: 경로고객번호
		private String fstCnttPrtnId; // 필수: 최초접속거래처ID
		private String prtnNm; // 필수: 거래처명
		private String fstCnttDt; // 최초 접속 일자
		private String fstTrDt; // 최초 거래 일자
		private String userPwdEc; // 필수: 사용자 비밀번호 암호  (웹경로일경우 필수)
		private String fscrId; // 최초생성ID
		private String fscrTsp; // 최초생성시각
		private String lschId; // 최종변경ID
		private String lschTsp; // 최종변경시각
		private CicuemCuOptiTcVo cicuemCuOptiTcVo;
		public CicuemCuOptiTcVo getCicuemCuOptiTcVo() {
			if (cicuemCuOptiTcVo == null) {
				cicuemCuOptiTcVo = new CicuemCuOptiTcVo();
			}
			return cicuemCuOptiTcVo;
		}
	}
	
	@Data
	public static class CicuemCuOptiTcVo {
		private String chCd; // 경로구분코드
		private String emlOptiYn; // 필수: 이메일수신동의여부
		private String smsOptiYn; // 필수: SMS수신동의여부
		private String dmOptiYn; // 필수: DM수신동의여부
		private String tmOptiYn; // 필수: TM수신동의여부
		private String emlOptiDt; // 이메일수신동의일자
		private String smsOptiDt; // SMS수신동의일자
		private String dmOptiDt; // DM수신동의일자
		private String tmOptiDt; // TM수신동의일자
		private String intlOptiYn; // 필수: 알림톡수신동의여부
		private String intlOptiDt; // 알림톡 수신동의일자
		private String kkoIntlOptiYn; // 카카오 알림톡 수신동의여부
		private String kkoIntlOptiDt; // 카카오 알림톡 수신동의일자
		private String fscrId; // 필수: 최초생성ID
		private String fscrTsp; // 최초생성시각
		private String lschId; // 필수: 최종변경ID
		private String lschTsp; // 최종변경tlrkr
		private String sdtpCd; // 표준시간대코드
	}
}
