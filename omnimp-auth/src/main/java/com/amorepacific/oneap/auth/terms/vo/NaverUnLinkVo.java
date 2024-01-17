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
 * Date   	          : 2023. 4. 28..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.terms.vo;

import java.util.List;

import com.amorepacific.oneap.auth.api.vo.ivo.CicuedCuTncaTcVo;
import com.amorepacific.oneap.auth.membership.vo.naver.NaverMembershipTermsVo;
import com.amorepacific.oneap.common.vo.api.CicuedCuChQcVo;
import com.amorepacific.oneap.common.vo.api.CuoptiResponse.CicuemCuOptiQcVo;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.terms.vo 
 *    |_ NaverUnLinkVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 4. 28.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
public class NaverUnLinkVo {
	
	private String incsNo;
	private String xincsNo;
	private boolean naverLinked;
	
	private List<CicuedCuTncaTcVo> cicuedCuTncaTcVo;	// 통합고객 동의 약관 정보
	private List<NaverMembershipTermsVo> naverMembershipTermsVoList;	// 통합고객 동의 약관 상세 정보
	private List<CicuemCuOptiQcVo> afltChCicuemCuOptiQcVoList;	// 통합고객 마케팅 수신 동의 정보
	
	private List<String> afltChCdList;					// 제휴사 채널코드 목록
	
	private String smsOptiYn;
	private String smsOptiDt;

	private String resultCode;
	private String resultMessage;
	private String returnUrl;
}
