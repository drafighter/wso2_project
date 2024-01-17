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
 * Date   	          : 2020. 9. 17..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.user;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.user 
 *    |_ WithdrawResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 17.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class WithdrawResponse {
	
	private int incsNo; // 고객의 유일한 식별 번호	
	private int rsltRow; // 처리건수
	private String rsltCd; // 결과코드
	private String rsltMsg; // 결과메시지
	private int cnt; // 존재건수
	private String existYn; // 존재여부
	private int custinfrChgSn; // 고객통합변경이력순번
	private int chCustCnt; // 경로고객건수
	private String rsltFlag; // 결과구분자
	private String incsCardNoEc; // 통합고객카드번호
	private String cert90Flag; // 90일인증여부
	private String ptAcmlRt; // 포인트적립률
	private int joincnt; // 경로가입
	private int infrInqMax; // 정보조회순번

}
