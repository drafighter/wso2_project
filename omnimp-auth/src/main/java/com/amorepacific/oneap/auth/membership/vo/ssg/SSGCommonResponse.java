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
 * Date   	          : 2022. 3. 23..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.membership.vo.ssg;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * com.amorepacific.oneap.auth.membership.vo.ssg 
 *    |_ SSGCommonResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 3. 23.
 * @version : 1.0
 * @author  : hjw0228
 */

@ToString
@Getter
@Setter
public class SSGCommonResponse implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = -6661779076877549891L;
	private int status;
	private String message;
	private String resultCode;
	private String resultMessage;
	
	public SSGCommonResponse() {
		
	}
	
	public SSGCommonResponse(int status, String message, String resultCode, String resultMessage) {
		this.status = status;
		this.message = message;
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
	}
}
