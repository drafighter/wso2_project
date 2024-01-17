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
 * Date   	          : 2020. 7. 9..
 * Description 	  : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.check.actor;

import com.amorepacific.oneap.common.check.CheckResponse;

/**
 * <pre>
 * com.apmorepacific.oneap.common.check.actor 
 *    |_ CheckActor.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

public interface CheckActor {
	
	public static final int SUCCESS = 100;
	
	public static final int FAIL = -900;
	

	// 비밀번호 체크용 : 영문, 숫자, 특수문자
	public static final String PASSWORD_PATTERN1 = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&\\^:;()_=<>`\\[\\]{}\\|'\"~])[a-z[0-9]$@$!%*#?&\\^:;()_=<>`\\[\\]{}\\|'\"~]{8,16}$"; 

	// 비밀번호 체크용 : 영문, 숫자
	public static final String PASSWORD_PATTERN2 = "^(?=.*[A-Za-z])(?=.*\\d)[a-z\\d]{8,16}$"; 

	// 비밀번호 체크용 : 영문, 특수문자
	public static final String PASSWORD_PATTERN3 = "^(?=.*[A-Za-z])(?=.*[$@$!%*#?&\\^:;()_=<>`\\[\\]{}\\|'\"~])[a-z$@$!%*#?&\\^:;()_=<>`\\[\\]{}\\|'\"~]{8,16}$"; 

	// 비밀번호 체크용 : 특수문자, 숫자
	public static final String PASSWORD_PATTERN4 = "^(?=.*\\d)(?=.*[$@$!%*#?&\\^:;()_=<>`\\[\\]{}\\|'\"~])[\\d$@$!%*#?&\\^:;()_=<>`\\[\\]{}\\|'\"~]{8,16}$"; 

	// 아이디체크용 : 영문, 숫자, @(at sign)
	public static final String ID_PATTERN = "^[A-Za-z[0-9@]]{4,12}$";

	// 자리수 제외한 아이디체크용 : 영문, 숫자, @(at sign)
	public static final String ID_CHAR_PATTERN = "^[a-zA-Z0-9@]*$";
	
	
	public static final String IS_UPPER_ENG_PATTERN = "((?=.*[A-Z])).{8,16}";
	public static final String IS_LOWER_ENG_PATTERN = "((?=.*[a-z])).{8,16}";
	public static final String IS_NUMBER_PATTERN = "((?=.*[0-9])).{8,16}";
	public static final String IS_SPECIAL_CHAR_PATTERN = "((?=.*[~'!@#$%?\\\\/&*\\]|\\[=()}\"{+_:;,.><'-])).{8,16}";
	public static final String IS_WHITESPACE_PATTERN = ".*\\s.*";
	
	public enum Type {
		IDCHAR, ID, PASSWORD;
	}
	
	public CheckResponse check(final String checkValue, final String compareValue, final String beforeValue);
}
