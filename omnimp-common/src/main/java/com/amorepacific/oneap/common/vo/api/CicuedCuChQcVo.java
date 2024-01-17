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
 * Date   	          : 2022. 2. 16..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ CicuedCuChQcVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 2. 16.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
public class CicuedCuChQcVo {

	private String incsNo; // 통합고객번호
	private String chCd; // 채널코드
	private String chNm; // 채널명
	private String chcsNo; // 온라인ID
	private String fstCnttPrtnId; // 최초접촉거래처ID
	private String prtnNm; // 거래처명
	private String fstCnttDt; // 최초접속일자
	private String fstTrDt; // 최초거래일자
	private String userPwdEc; // 사용자비밀번호 
	private String delYn; // 고객상태코드
	private String fscrId; // 최초등록ID
	private String fscrTsp; // 최초등록시간
	private String lschId; // 최종변경ID
	private String lschTsp; // 최종변경시간
}
