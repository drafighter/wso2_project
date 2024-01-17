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
package com.amorepacific.oneap.common.vo;

import java.io.Serializable;
import java.util.List;

import com.amorepacific.oneap.common.types.LoginType;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

import lombok.AllArgsConstructor;
import lombok.Data;
//import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * com.amorepacific.oneap.auth.step 
 *    |_ LoginStepVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 13.
 * @version : 1.0
 * @author  : takkies
 */
@Data
@AllArgsConstructor
//@RequiredArgsConstructor
public class LoginStepVo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2752120603965594459L;
	private LoginType loginType;
	private List<UmOmniUser> omniUsers;
	private List<UmChUser> chUsers;
	private String incsNo;
	
}
