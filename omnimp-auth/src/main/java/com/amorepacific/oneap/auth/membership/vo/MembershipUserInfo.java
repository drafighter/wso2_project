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
 * Date   	          : 2022. 3. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.membership.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * com.amorepacific.oneap.auth.membership.vo 
 *    |_ MembershipUserInfo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 3. 21.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipUserInfo implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 8150496549119812574L;
	private String chCd;
	private String resultCode;
	private String resultMessage;
	
	private String id;
	private String incsNo;
	private String xincsNo;
	private String name;
	
	private String ciNo; // CI
	private String phoneNumber; // 휴대폰번호
	private String mbrId; // 제휴사 ID
	private String xmbrId; // 제휴사 ID (암호화된 값)
	
	private String membershipId;
	private String apiKey; // API Key
}
