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
 * Author	          : hkdang
 * Date   	          : 2020. 11. 11..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.social.web;

import java.io.Serializable;

import com.amorepacific.oneap.common.vo.sns.SnsProfileResponse;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.social.web 
 *    |_ SnsAuthUserVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 11.
 * @version : 1.0
 * @author  : hkdang
 */

@Data
public class SnsAuthUserVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7644880610884171651L;
	
	private String snsId;
	private String snsType;
	private String token;
	private SnsProfileResponse profileResponse;
	private boolean isMobileAuth;
	
	private String loginId;
	private String incsNo;
}
