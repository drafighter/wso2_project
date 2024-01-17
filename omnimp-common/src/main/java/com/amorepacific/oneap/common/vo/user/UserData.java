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
 * Date   	          : 2020. 8. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.user;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.user 
 *    |_ UserResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 21.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class UserData {

	private String loginId;
	private String incsNo;
	private String name;
	private String mobile;
	private String password;
	private String passwordReset; // 비밀번호 RESET 여부(Y이면 로그인 시 비밀번호 변경 페이지 진입)
	private String confirmPassword;
	private int umId;
	private String lastPasswordUpdate;
	private String chCd;
	private String uid;
	
	private String omniLoginId;
	private String omniIncsNo;
	private String omniName;
	private String omniMobile;
	
	private String chLoginId;
	private String chIncsNo;
	private String chName;
	private String chMobile;
	
}
