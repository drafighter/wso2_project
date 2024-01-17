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
 * <pre>
 * com.amorepacific.oneap.auth.api.vo.ivo 
 *    |_ UpdateCustVo.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 8.
 * @version : 1.0
 * @author : takkies
 */
@Data
public class UpdateCustVo {
	private String incsNo; // 통합고객번호 incsNo String 9 필수
	private String custNm; // 고객명 custNm String 50
	private String custWtDt; // 고객탈퇴일시 custWtDt String 8 변경가능여부 : Y
	private String athtDtbr; // 생년월일 athtDtbr String 8
	private String frclCd; // 내외국인구분코드 frclCd String 1 내국인 : K, 외국인 : F
	private String sxclCd; // 성별구분코드 sxclCd String 1 "변경가능여부 : Y 여성 : F, 남성: M"
	private String pmdcCd; // 우편물수신처구분코드 pmdcCd String 1 "변경가능여부 : Y 자택 : H, 직장 : C, 기타"
	private String homeZip; // 자택우편번호 homeZip String 6 변경가능여부 : Y
	private String homeBscsAddr; // 자택기본주소 homeBscsAddr String 300 변경가능여부 : Y
	private String homeDtlAddr; // 자택상세주소 homeDtlAddr String 300 변경가능여부 : Y
	private String cellTidn; // 휴대폰식별전화번호 cellTidn String 4 변경가능여부 : Y
	private String cellTexn; // 휴대폰국전화번호 cellTexn String 4 변경가능여부 : Y
	private String cellTlsn; // 휴대폰끝전화번호 cellTlsn String 4 변경가능여부 : Y
	private String custEmid; // 고객이메일계정 custEmid String 20 변경가능여부 : Y
	private String custEmdn; // 고객이메일번지 custEmdn String 40 변경가능여부 : Y
	private String ciNo; // CI번호 ciNo String 88
	private String mbrJoinDt; // 회원가입일자 mbrJoinDt String 8
	private String rnarCd; // 실명인증결과코드 rnarCd String 2 변경가능여부 : Y
	private String rnmAthtDt; // 실명인증일자 rnmAthtDt String 8 변경가능여부 : Y
	private String custWtYn; // 고객탈퇴여부 custWtYn String 1 변경가능여부 : Y
	private String ofcBscsAddr; // 직장기본주소 ofcBscsAddr String 300 변경가능여부 : Y
	private String ofcDtlAddr; // 직장상세주소 ofcDtlAddr String 300 변경가능여부 : Y
	private String ofcZip; // 직장우편번호 ofcZip String 6 변경가능여부 : Y
	private String addrXcrd; // 주소X좌표 addrXcrd String 20 변경가능여부 : Y
	private String addrYcrd; // 주소Y좌표 addrYcrd String 20 변경가능여부 : Y
	private String rfrsCd; // 정제결과코드 rfrsCd String 1 변경가능여부 : Y
	private String blmnNo; // 건물관리번호 blmnNo String 25 변경가능여부 : Y
	private String adclCd; // 주소구분코드 adclCd String 1 변경가능여부 : Y
	private String adocCd; // 주소입수구분코드 adocCd String 1 변경가능여부 : Y
	private String drccCd; // 휴면고객구분코드 drccCd String 1
	private String drcsRegDt; // 휴면고객등록일자 drcsRegDt String 8
	private String joinChCd; // 가입경로구분코드 joinChCd String 3
	private String joinPrtnId; // 가입거래처ID joinPrtnId String 10
	private String jndvCd; // 가입디바이스코드 jndvCd String 1
	private String chCd; // 경로구분코드 chCd String 3 필수
	private String chgChCd; // 변경요청경로코드 chgChCd String 3 필수 변경 요청한 경로코드
	private String lschId; // 최종변경ID lschId String 12 필수 API를 호출한 사번

	private CicuemCuAdtInfTcVo CicuemCuAdtInfTcVo; // 기념일
	private List<CicuedCuTncaTcVo> CicuedCuTncaTcVo; // 약관
	//private List<CicuemCuOptiCsTcVo> CicuemCuOptiTcVo; // 마케팅 동의
	private CicuemCuOptiCsTcVo CicuemCuOptiTcVo; // 마케팅 동의
	private List<CicuedCuChCsTcVo> CicuedCuChCsTcVo; // 경로

	public void addChannel(CicuedCuChCsTcVo cicuedCuChCsTcVo) {
		if (this.CicuedCuChCsTcVo == null) {
			this.CicuedCuChCsTcVo = new ArrayList<>();
		}
		this.CicuedCuChCsTcVo.add(cicuedCuChCsTcVo);
	}
	
	public void addTerms(CicuedCuTncaTcVo cicuedCuTncaTcVo) {
		if (this.CicuedCuTncaTcVo == null) {
			this.CicuedCuTncaTcVo = new ArrayList<>();
		}
		this.CicuedCuTncaTcVo.add(cicuedCuTncaTcVo);
	}
	
	public void addAgreeMarketing(CicuemCuOptiCsTcVo cicuemCuOptiCsTcVo) {
//		if (this.CicuemCuOptiTcVo == null) {
//			this.CicuemCuOptiTcVo = new ArrayList<>();
//		}
		//this.CicuemCuOptiTcVo.add(cicuemCuOptiCsTcVo);
		
		this.CicuemCuOptiTcVo = cicuemCuOptiCsTcVo;
		
	}
	
	@Data
	public static class CicuemCuAdtInfTcVo {
		private String chCd;
		private String chgChCd;
		/**
		 * 필수: 양음력구분코드
		 */
		private String slccCd; 
		/** 
		 * 필수: 개인생년월일
		 */
		private String psnDtbr;
		private String wdanDt; //결혼기념일자
		private String jobCd; // 직업코드
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
	public static class CicuedCuChCsTcVo {
		/**
		 * 필수: 경로코드
		 */
		private int incsNo;
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
	public static class CicuedCuTncaTcVo {
		/**
		 * 필수: 약관유형관리코드
		 */
		private String tcatCd; 
		/**
		 * 필수: 개정버전번호
		 */
		private String tncvNo; 
		private String tncAgrYn; // 약관동의여부
		private String tncaDttm; // 약관동의일시
		private String chgChCd;
		/**
		 * 필수: 최초생성ID
		 */
		private String fscrId; 
		/**
		 * 필수: 최종변경ID
		 */
		private String lschId; 
		
		private int incsNo;
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
		private String kkoIntlOptiYn; // 카카오 알림톡수신동의여부 
		private String kkoIntlOptiDt; // 카카오 알림톡수신동의일자
		private String chgChCd;
		/**
		 * 필수: 최초 생성ID
		 */
		private String fscrId; 
		/**
		 * 필수: 최종 변경ID
		 */
		private String lschId; 
		private int incsNo;
	}
	
	@Data
	public static class CicuedCuAddrTcVo {
		private String zip;        // 우편번호
		private String bscsAddr;   // 기본주소
		private String dtlAddr;    // 주소상세
		private String addrChcYn;  // 주소선택여부
		private String adclCd;     // 주소구분코드
		private String adocCd;     // 주소입수구분코드
		private String blmnNo;     // 건물관리번호
		private String addrXcrd;   // 주소X좌표
		private String addrYcrd;   // 주소Y좌표
		private String rfrsCd;     // 정제결과코드
		private String admcCd;     // 주소관리구분코드
		private String fscrId;     // 최초생성ID
		private String fscrTsp;    // 최초생성시각
		private String lschId;     // 최종변경ID
		private String lschTsp;    // 최종변경시각
		private String sdtpCd;     // 표준시간대코드
	}
	
}
