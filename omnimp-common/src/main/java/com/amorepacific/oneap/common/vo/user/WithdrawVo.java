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
 *    |_ WithdrawVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 17.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class WithdrawVo {
	
	private int incsNo; // 필수 통합고객번호
	private String wtpsCd; // 필수 진행상태코드 요청 : 10
	private String wtrqIp; // 탈퇴요청IP
	private String wtrqDttm; // 탈퇴요청일자, 8자리 
	private String wtrdCd; // 탈퇴요청디바이스코드 (W:WEB, M:MOBILE, A:APP)
	private String wtrqChCd; // 필수 탈퇴요청채널코드
	private String wtrqPrtnId; // 필수 탈퇴요청거래처코드
	private String wtrsCd; // 탈퇴사유코드 서비스불만 : '01', 개인정보유출우려 : '02', 서비스미사용 : '03', 혜택부족 : '04', 기타 : '99'
	private String wtrsTxt; // 탈퇴사유내용
	private String fscrId; // 최초생성ID
	private String lschId; // 최종변경ID // 필수
	
}
