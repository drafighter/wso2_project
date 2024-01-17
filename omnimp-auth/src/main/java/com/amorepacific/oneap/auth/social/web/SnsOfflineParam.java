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
 * Author	          : hkdang
 * Date   	          : 2020. 11. 19..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.social.web;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.social.web 
 *    |_ SnsOfflineInfoVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 19.
 * @version : 1.0
 * @author  : hkdang
 */

@Data
public class SnsOfflineParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8579604822986492000L;
	
	private String chCd; // 채널코드
	private String joinPrtnId; // 가입경로 매장코드
	private String joinPrtnNm; // 가입경로 매장 이름
	private String addInfo; // 부가정보 (Y = redirect url 로 바로, N = 옴니 가입완료 페이지)
	
	private String returnUrl; // 성공시 redirect url
	private String cancelUrl; // 실패(취소)시 redirect url
	
	/*
	// 이니스프리 -> 추가정보입력에 필요한 파라미터. 카카오 가입에는 제거
	private String storenm; // 매장 이름
	private String storeCd; // 매장 코드
	private String user_id; // 매장 아이디
	private String chnCd; // 채널코드
	*/
}

