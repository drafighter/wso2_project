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
 * Date   	          : 2021. 6. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.user;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.user 
 *    |_ CustomerData.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 6. 21.
 * @version : 1.0
 * @author  : jsjang
 */
@Data
public class CustomerData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2719880616607853775L;
	private String incsNo; // 통합고객번호
	private String custNm; // 고객명
	private String athtDtbr; // 인증생년월일 - 주민등록번호앞6자리
	private String ciNo; // CI번호
	private String sxclCd; // 성별구분코드 
	private String mbrJoinDt; // 회원가입일시
	private String custWtYn; // 고객탈퇴여부
	private String custWtDttm; // 고객탈퇴일시 YYYYMMDD
	private String drccCd; // 휴면고객구분코드
	private String drcsRegDt; // 휴면고객등록일자
	private String joinChCd; // 가입경로구분코드
	private String joinPrtnId; // 가입거래처ID
	private String chcsNo; // 온라인ID
	private String chCdNm; // 경로구분명
	private String chCd; // 경로구분
	private String rmnPt; // 포인트잔액
	private String cellTidn; // 고객의휴대전화번호1
	private String cellTexn; // 고객의휴대전화번호2
	private String cellTlsn; // 고객의휴대전화번호3
	private String atclCd; // 휴대폰인증구분코드
	private String joinCnclYn; // 가입망취소여부
	private String rsltMsg;
	private String rsltCd;
	private String frclCd; // 내외국인구분
	private String incsCardNoEc; // 카드번호 
	
	public List<CicuedCuChArrayTcVo> CicuedCuChArrayTcVo;

}
