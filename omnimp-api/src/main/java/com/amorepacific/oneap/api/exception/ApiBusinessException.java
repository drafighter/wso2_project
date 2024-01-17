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
 * Date   	          : 2020. 7. 13..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.exception;

/**
 * <pre>
 * com.amorepacific.oneap.api.exception 
 *    |_ ApiBusinessException.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 13.
 * @version : 1.0
 * @author : takkies
 */

public class ApiBusinessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3745069729962867652L;
	
	private int errorCode = -1;
	
	public static final int PARAM_NOT_FOUND = 0;
	public static final int DB_EXEUTE_ERROR = 1;
	public static final int WSO2_API_ERROR = 2;
	public static final int CHANNEL_CODE_ERROR = 4;
	
	private static final String messageKeys[] = {//
			"api.biz.error.param.not.found", // 0
			"api.biz.error.db.execute", // 1
			"api.biz.error.wso2.api.error", // 2
			"api.biz.error.channel.code.error" // 3
	};

	public ApiBusinessException(String msg) {
		super(msg);
	}
	
	public ApiBusinessException(String msg, int errorCode) {
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
