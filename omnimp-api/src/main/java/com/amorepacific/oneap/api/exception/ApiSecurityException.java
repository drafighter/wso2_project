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
 * Date   	          : 2020. 7. 14..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.exception;

/**
 * <pre>
 * com.amorepacific.oneap.api.exception 
 *    |_ ApiSecurityException.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 14.
 * @version : 1.0
 * @author : takkies
 */

public class ApiSecurityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8664369202292123758L;

	private int errorCode = -1;
	
	public static final int API_KEY_NOT_FOUND = 0;
	public static final int API_KEY_INVALID = 1;
	
	private static final String messageKeys[] = {//
			"api.sec.error.apikey.not.found", // 0
			"api.sec.error.apikey.invalid" // 1
	};
	
	public ApiSecurityException(String msg) {
		super(msg);
	}
	
	public ApiSecurityException(String msg, int errorCode) {
		super(msg);
		this.errorCode = errorCode;
	}
	
	public int getErrorCode() {
		return this.errorCode;
	}
	
	public String getMessageKey() {
		if (this.errorCode == -1) {
			return null;
		}
		return messageKeys[this.errorCode];
	}
}
