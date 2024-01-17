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
 * Date   	          : 2020. 8. 28..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ CreateBpUserData.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 28.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class BpUserData implements Serializable {
	
	private static final long serialVersionUID = 1867014083604619784L;
	private String incsNo; // 통합고객번호
	private String cstmid; // 온라인ID
	private String pswd; // 온라인Password
	private String chcd;
	private String apiKey; // 호출날짜(yyyy-MM-dd  + chcd) 를 SHA-512로 암호화하여 호출
	private String smsReceiveType;
}
