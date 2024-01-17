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
 * Date   	          : 2020. 8. 13..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.user;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.user 
 *    |_ UmWso2User.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 13.
 * @version : 1.0
 * @author : takkies
 */
@Data
public class UmOmniUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8605128373878422651L;
	private int umId;
	private String umUserId;
	private String umUserName; // 로그인 아이디
	private String umUserPassword; // 로그인 비.밀.번.호
	private String umUserDormancy; // 휴면여부
	private String umUserPasswordReset; //
	private String umAttrName;
	private String umAttrValue;
	private String createdDate; // 가입일(화면 출력에 필요)
	
	private String uid; // loginid = um_user_name
	private String incsNo;
	private String mobile;
	private String fullName;
	private String lastLoginTime;
	private String lastPasswordUpdate;
	private String accountState;
	private String accountLock;
	private String failedLoginAttempts;
	private String accountDisabled;
	private String unlockTime;
	private String disabledDate;
	private String chCd;
	
}
