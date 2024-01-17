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
 * Date   	          : 2020. 8. 28..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import com.amorepacific.oneap.common.vo.BaseResponse;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ ApiResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 28.
 * @version : 1.0
 * @author  : takkies
 */
@Getter
@Setter
public class ApiResponse extends BaseResponse {

	private static final long serialVersionUID = 8784543175485508638L;
	// 000: 정상처리되었습니다     
	// 010: 약관 미동의     
	// 020: 1달 이내 탈퇴 이력 有     
	// 030: 온라인 회원정보 존재     
	// 040: SMS 수신 미동의     
	// 050: 이미 온라인에 가입된 통합회원입니다     
	// 060: 존재하지 않는 통합고객번호입니다    
	// 070: ID 중복     
	// 080: ID 형식이 맞지 않음     
	// 090: 통합회원정보와 실명인증 정보가 일치하지 않습니다     
	// 100: 회원가입중 에러가 발생되었습니다
	private String rsltCd; // 결과코드
	private String rsltMsg; // 결과메시지
	private String result; // 결과코드(아이디체크)
	
}
