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
 * Author	          : mcjan
 * Date   	          : 2020. 8. 11..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.api.vo.ivo;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.api.vo.ivo 
 *    |_ CreateCustChannelJoinVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 11.
 * @version : 1.0
 * @author  : mcjan
 */
@Data
public class CreateCustChannelJoinVo {

	/**
	 * 가입망 취소 여부 (취소시 'Y')
	 */
	private String joinCnclYn;
	
	private CicuedCuChTcVo cicuedCuChTcVo;
	
	public CicuedCuChTcVo getCicuedCuChTcVo() {
		if (cicuedCuChTcVo == null) {
			cicuedCuChTcVo = new CicuedCuChTcVo();
		}
		return cicuedCuChTcVo;
	}
	
	@Data
	public static class CicuedCuChTcVo {
		/**
		 * 필수: 통합고객번호
		 */
		private String incsNo;
		/**
		 * 필수: 경로구분코드
		 * (제휴사별 경로 채널 코드  - As Is UCOD01DT에서 관리 되던 CHCD   - 방판/마트/백화점/리리코스/오설록/이니스프리/에뛰드/에스쁘아/ BC카드/다음/11번가/OK 캐쉬백)
		 */
		private String chCd;
		/**
		 * 필수: 경로고객번호
		 * (경로에서 별도 관리되는 고객번호)
		 */
		private String chcsNo;
		/**
		 * 필수: 최초접속거래처ID
		 */
		private String fstCnttPrtnId;
		/**
		 * 필수: 거래처명
		 */
		private String prtnNm;
		/**
		 * 최초 접속 일자
		 */
		private String fstCnttDt;
		/**
		 * 최초 거래 일자
		 */
		private String fstTrDt;
		/**
		 * 필수: 사용자 비밀번호 암호
		 * (웹경로일경우 필수)
		 */
		private String userPwdEc;
		/**
		 * 최초생성ID
		 */
		private String fscrId;
		/**
		 * 최초생성시각
		 */
		private String fscrTsp;
		/**
		 * 최종변경ID
		 */
		private String lschId;
		/**
		 * 최종변경시각
		 */
		private String lschTsp;
		
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
		/**
		 * 경로구분코드
		 * (제휴사별 경로 채널 코드  - As Is UCOD01DT에서 관리 되던 CHCD   - 방판/마트/백화점/리리코스/오설록/이니스프리/에뛰드/에스쁘아/ BC카드/다음/11번가/OK 캐쉬백)
		 */
		private String chCd;
		/**
		 * 필수: 이메일수신동의여부
		 */
		private String emlOptiYn;
		/**
		 * 필수: SMS수신동의여부
		 */
		private String smsOptiYn;
		/**
		 * 필수: DM수신동의여부
		 */
		private String dmOptiYn;
		/**
		 * 필수: TM수신동의여부
		 */
		private String tmOptiYn;
		/**
		 * 이메일수신동의일자
		 */
		private String emlOptiDt;
		/**
		 * SMS수신동의일자
		 */
		private String smsOptiDt;
		/**
		 * DM수신동의일자
		 */
		private String dmOptiDt;
		/**
		 * TM수신동의일자
		 */
		private String tmOptiDt;
		/**
		 * 필수: 알림톡수신동의여부
		 */
		private String intlOptiYn;
		/**
		 * 알림톡 수신동의일자
		 */
		private String intlOptiDt;
		/**
		 * 카카오 알림톡 수신동의여부
		 */
		private String kkoIntlOptiYn;
		/**
		 * 카카오 알림톡 수신동의일자
		 */
		private String kkoIntlOptiDt;
		/**
		 * 필수: 최초생성ID
		 */
		private String fscrId;
		/**
		 * 최초생성시각
		 */
		private String fscrTsp;
		/**
		 * 필수: 최종변경ID
		 */
		private String lschId;
		/**
		 * 최종변경tlrkr
		 */
		private String lschTsp;
		/**
		 * 표준시간대코드
		 */
		private String sdtpCd;
	}
}
