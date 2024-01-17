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
 * Date   	          : 2020. 9. 14..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.sns;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.sns 
 *    |_ SnsData.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 14.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class SnsParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6696294706688153977L;
	private String snsType;
	private String snsId;
	private String userName; // 사용자 실명 or 닉네임
	private String birth;
	private String phone;
	private String gender;
	private String ciNo;
	private String joinDate;
	private String loginId; // 사용자 로그인 아이디
	private String email;
	
	private String newId;
	private String password;
	private String chcd;
	
	private String incsNo;
	
	private String joinTo; // 어디로 가입시킬지 (all(통합), ch(경로), ommni(옴니))
}
