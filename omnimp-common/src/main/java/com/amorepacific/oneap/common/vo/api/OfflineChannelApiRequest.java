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
 * Date   	          : 2020. 9. 8..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


/**
 * 회원정보 실시간 I/F 를 위한 회원가입시 API 호출로직 추가 Request
 * 2023.04.18 오설록 백화점(008) / 설화수FSS(077) / 백화점(101)
 */
@Data
public class OfflineChannelApiRequest {
	private int incsNo; // 통합고객번호 incsNo String 9 필수
	private String custNm; // 고객명 custNm String 50
	private String joinChCd; // 가입경로구분코드 joinChCd String 3
	private String jndvCd; // 가입디바이스코드 jndvCd String 1
	private String atclCd; // 휴대폰인증구분코드 atclCd String (COM_CAT_CD:10139 (10:소유, 20:점유, 30:조정))
	private String athtDtbr; // 생년월일 athtDtbr String 8
	private String frclCd; // 내외국인구분코드 frclCd String 1 내국인 : K, 외국인 : F
	private String sxclCd; // 성별구분코드 sxclCd String 1 "변경가능여부 : Y 여성 : F, 남성: M"
	private String cellTidn; // 휴대폰식별전화번호 cellTidn String 4 변경가능여부 : Y
	private String cellTexn; // 휴대폰국전화번호 cellTexn String 4 변경가능여부 : Y
	private String cellTlsn; // 휴대폰끝전화번호 cellTlsn String 4 변경가능여부 : Y
	private String ciNo; // CI번호 ciNo String 88
	private String fscrId; // 최초생성ID fscrId String
	private String lschId; //최종변경 ID String 
	private String joinPrtnId; // 가입거래처ID joinPrtnId String 10
	private String joinEmpId; // 가입직원ID 가입직원ID
	
	private CicuemCuOptiCsTcVo cicuemCuOptiCsTcVo ; // 마케팅 동의
	private CicuedCuChCsTcVo cicuedCuChCsTcVo; // 경로

//	private List<CicuemCuOptiCsTcVo> cicuemCuOptiCsTcVo ; // 마케팅 동의
//	private List<CicuedCuChCsTcVo> cicuedCuChCsTcVo; // 경로

//	public void addChannel(CicuedCuChCsTcVo cicuedCuChCsTcVo) {
//		if (this.cicuedCuChCsTcVo == null) {
//			this.cicuedCuChCsTcVo = new ArrayList<>();
//		}
//		this.cicuedCuChCsTcVo.add(cicuedCuChCsTcVo);
//	}
//	
//	public void addAgreeMarketing(CicuemCuOptiCsTcVo cicuemCuOptiCsTcVo) {
//		if (this.cicuemCuOptiCsTcVo == null) {
//			this.cicuemCuOptiCsTcVo = new ArrayList<>();
//		}
//		this.cicuemCuOptiCsTcVo.add(cicuemCuOptiCsTcVo);
//	}
	
	@Data
	public static class CicuedCuChCsTcVo {
		/**
		 * 필수: 경로코드
		 */
		private String chCd; 
		private String chcsNo; // 경로고객번호
		private String userPwdEc; // 사용자비밀번호암호 (As Is Legacy는 입력 하지 마세요경로(030) webID password)
		private String prtnNm; // 거래처명
		/**
		 * 필수: 최초생성ID
		 */
		private String fscrId; 
		/**
		 * 필수: 최종변경ID
		 */
		private String lschId; 
	}
	
	@Data
	public static class CicuemCuOptiCsTcVo {
		/**
		 * 필수: 경로구분코드
		 */
		private String chCd;         
		private String emlOptiYn;  // 이메일수신동의여부  
		private String smsOptiYn;  // SMS수신동의여부     
		private String dmOptiYn;   // DM수신동의여부      
		private String tmOptiYn;   // TM수신동의여부      
		private String emlOptiDt;  // 이메일수신동의일자  
		private String smsOptiDt;  // SMS수신동의일자     
		private String dmOptiDt;   // DM수신동의일자      
		private String tmOptiDt;   // TM수신동의일자     
		private String intlOptiYn; // 알림톡수신동의여부 
		private String intlOptiDt; // 알림톡수신동의일자 
		/**
		 * 필수: 최초 생성ID
		 */
		private String fscrId; 
		/**
		 * 필수: 최종 변경ID
		 */
		private String lschId; 
	}
}
