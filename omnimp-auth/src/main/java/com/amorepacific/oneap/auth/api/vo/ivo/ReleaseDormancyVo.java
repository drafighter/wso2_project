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
 *    |_ ReleaseDormancyVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 11.
 * @version : 1.0
 * @author  : mcjan
 */
@Data
public class ReleaseDormancyVo {

	/**
	 * 필수: 고객명
	 */
	private String custNm;
	/**
	 * 인증생년월일
	 */
	private String athtDtbr;
	/**
	 * CI번호
	 */
	private String ciNo;
	/**
	 * 필수: 성별구분코드 (M: 남자, F: 여자)
	 */
	private String sxclCd;
	/**
	 * 필수: 통신사 구분 코드 
	 * KT: 케이티, LGU:엘지유플러스, SKT:SK텔레콤,
	 * KTM:케이티알뜰폰, LGM:엘지유펄러스알뜰폰, SKM:SK텔레콤 알뜰폰
	 * (데이터 입력시 영문 코드 입력)
	 */
	private String tlccCd;
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
	/**
	 * 필수: 신청경로구분코드
	 */
	private String reqChCd;
	/**
	 * 신청자 IP
	 */
	private String reqrIp;
	/**
	 * 필수: 신청상태코드 (E:오류, R:신청, S:완료 (신청시 R로 작성))
	 */
	private String rqstCd;
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
	 * 최종변경시각
	 */
	private String lschTsp;
	/**
	 * 표준시간대코드
	 */
	private String sdtpCd;

	
}
