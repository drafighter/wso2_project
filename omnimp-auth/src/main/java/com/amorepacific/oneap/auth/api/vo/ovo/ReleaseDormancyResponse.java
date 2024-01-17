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
package com.amorepacific.oneap.auth.api.vo.ovo;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.api.vo.ovo 
 *    |_ ReleaseDormancyResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 11.
 * @version : 1.0
 * @author  : mcjan
 */
@Data
public class ReleaseDormancyResponse {
	/**
	 * 통합고객코드
	 */
	private String incsNo;
	/**
	 * 처리건수
	 */
	private String rsltRow;
	/**
	 * 결과코드
	 */
	private String rsltCd;
	/**
	 * 결과메시지
	 */
	private String rsltMsg;
	/**
	 * 존재건수
	 */
	private String cnt;
	/**
	 * 존재여부
	 */
	private String existYn;
	/**
	 * 고객통합변경이력순번
	 */
	private String custInfrChgSn;
	/**
	 * 경로고객건수
	 */
	private String chCustCnt;
	/**
	 * 결과구분자
	 */
	private String rsltFlag;
	/**
	 * 통합고객카드번호
	 */
	private String incsCardNoEc;
	/**
	 * 90일인증여부 (Y,N 결과값으로 확인가능)
	 */
	private String cert90Flag;
	/**
	 * 포인트적립률
	 */
	private String ptAcmlRt;
	/**
	 * 경로가입
	 */
	private String joincnt;
	/**
	 * 정보조회순번
	 */
	private String infrInqMax;

}
