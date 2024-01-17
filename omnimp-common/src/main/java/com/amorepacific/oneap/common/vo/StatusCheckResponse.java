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
 * Date   	          : 2020. 9. 22..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo 
 *    |_ StatusCheckResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 22.
 * @version : 1.0
 * @author  : takkies
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StatusCheckResponse extends BaseResponse {

	private static final long serialVersionUID = -2286031420491648763L;
	private String xincsno;
	private String remainUnLockTime;
	private int remainUnLockSeconds;
	private String channelName;
	
	private int correctPwd=0; //비밀번호 횟수 초과로 lock 시 해당 값 1이면 정상 password 입력
}
