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
 * Date   	          : 2020. 8. 10..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.api.vo.ivo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.api.vo.ivo 
 *    |_ CreateCustVO.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 10.
 * @version : 1.0
 * @author  : mcjan
 */
@Data
public class CreateCustVO {

	/**
	 * 필수: 고객명
	 */
	private String custNm;
	/**
	 * 필수: 생년월일
	 */
	private String athtDtbr;
	/**
	 * 필수: 내외국인구분코드 (내국인 : K, 외국인 : F)
	 */
	private String frclCd; 
	/**
	 * 필수: 성별구분코드 (여성 : F, 남성: M)
	 */
	private String sxclCd; 
	private String pmdcCd;        // 우편물수신처구분코드 (자택 : H, 직장 : C, 기타)
	private String homeZip;       // 자택우편번호
	private String homeBscsAddr;  // 자택기본주소
	private String homeDtlAddr;   // 자택상세주소
	/**
	 * 필수: 휴대폰식별전화번호
	 */
	private String cellTidn; 
	/**
	 * 필수: 휴대폰국전화번호
	 */
	private String cellTexn; 
	/**
	 * 필수: 휴대폰끝전화번호
	 */
	private String cellTlsn; 
	private String custEmid;      // 고객이메일계정
	private String custEmdn;      // 고객이메일번지
	/**
	 * 필수: ci번호 (고객의 개인식별 번호)
	 */
	private String ciNo; 
	private String rnarCd;        // 실명인증결과코드 (COM_CAT_CD : 10155 (1:실명인증성공, 2:실명인증실패, 3: 해당자료없음, 4:통신오류, 5:체크썸오류, 50:명의도용차단가입자))
	private String rnmAthtDt;     // 실명인증일자
	private String ofcBscsAddr;   // 직장기본주소
	private String ofcDtlAddr;    // 직장상세주소
	private String ofcZip;        // 직장우편번호
	private String addrXcrd;      // 주소X좌표
	private String addrYcrd;      // 주소Y좌표
	private String rfrsCd;        // 정제결과코드
	private String blmnNo;        // 건물관리번호
	private String adclCd;        // 주소구분코드 (1 : 입력지번, 2 : 입력도로명, 3 : 표준지번, 4 : 표준도로명)
	private String adocCd;        // 주소입수구분코드 (1 : 온라인, 2 : 일괄전환, 3 : 대외, 4 : 기타)
	private String drccCd;        // 휴면고객구분코드 (Y : 휴면, A : 예정)
	private String drcsRegDt;     // 휴면고객등록일자 (휴면고객으로 등록되는 날짜(휴면고객구분값이 예정인 경우는 휴면고객등록예정일자))
	/**
	 * 필수: 가입경로구분코드 (가입경로 채널코드)
	 */
	private String joinChCd; 
	/**
	 * 필수: 가입거래처ID (가입경로의 매장코드)
	 */
	private String joinPrtnId; 
	private String jndvCd;        // 가입디바이스코드 (고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP))
	/**
	 * 필수: 휴대폰인증구분코드 (COM_CAT_CD:10139 (10:소유, 20:점유, 30:조정))
	 */
	private String atclCd;
	private String smsNum;        // 휴대폰인증번호
	
	
	private String newCustJoinRqSiteCd; // 옴니는 사용않음 -> 2021-08-31 사이트 코드 경로시스템 (ex: 아이오페) 적용 시 사용
	private String custJndvOsClCd; // 고객가입디바이스운영체제구분코드
	private String snsIdPrcnCd; // SNS계정제휴사코드
	private String chcsIntgSwtJoinYn; // 경로고객통합전환가입여부
	
	/**
	 * 필수: 최초생성ID (API를 호출한 사번)
	 */
	private String fscrId;
	/**
	 * 필수: 최종변경ID (API를 호출한 사번)
	 */
	private String lschId;
	private String joinCnclYn; // 회원가입망취소시 'Y'
	
	private CicuemCuAdtInfTcVo CicuemCuAdtInfTcVo; // 기념일
	private List<CicuedCuChCsTcVo> CicuedCuChCsTcVo; // 경로
	private List<CicuedCuTncaTcVo> CicuedCuTncaTcVo; // 약관
	private List<CicuemCuOptiCsTcVo> CicuemCuOptiCsTcVo; // 마케팅 동의
	private List<CicuedCuAddrTcVo> cicuedCuAddrTcVo; // 주소
	
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
		if (this.CicuemCuOptiCsTcVo == null) {
			this.CicuemCuOptiCsTcVo = new ArrayList<>();
		}
		this.CicuemCuOptiCsTcVo.add(cicuemCuOptiCsTcVo);
	}
	
	public void addAddress(CicuedCuAddrTcVo cicuedCuAddrTcVo) {
		if (this.cicuedCuAddrTcVo == null) {
			this.cicuedCuAddrTcVo = new ArrayList<>();
		}
		this.cicuedCuAddrTcVo.add(cicuedCuAddrTcVo);
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
		
		private String prcnLnkgStorNo; // 제휴사연동스토어번호
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
		
		//private int incsNo;
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
		// private int incsNo;
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
