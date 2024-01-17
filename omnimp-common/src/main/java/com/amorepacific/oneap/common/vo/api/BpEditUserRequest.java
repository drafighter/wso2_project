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
 * Date   	          : 2020. 11. 12..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ BpEditUserData.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 12.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class BpEditUserRequest implements Serializable {

	private static final long serialVersionUID = -1337985828418002165L;
	private String paramSiteCd; // 유입채널 Site Code (default : CMC)
	private String appChCd; // 유입경로 (W : Web(default), M : Mobile, A : App)
	private String cstmId; // 웹ID
	private String pswd; // 비밀번호
	
	// 선택
	private String apAgree; // 약관동의 : 개인정보 제3자 제공동의
	private String infoProvide; // 약관동의 : 개인정보 선택 이용동의 
	private String frtroptfl; // 약관동의 : 국외이전동의(선택)여부
	private String mktuseinfsupfl; // 약관동의 : 외부컨텐츠마케팅활용_제3자제공동의(선택)여부
	
}
