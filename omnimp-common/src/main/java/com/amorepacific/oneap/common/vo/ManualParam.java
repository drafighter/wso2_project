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
 * Date   	          : 2020. 9. 29..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo 
 *    |_ ManualParam.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 29.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class ManualParam implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7939079888312618671L;
	private String chCd;
	private String loginId;
	private String loginPassword;
	private String userName;
	private String userMobile;
	private String userBirth;
	private String userGender;
	private String userForeigner;
	private String userCi;
	
	private String joinPrtnId;
	private String joinPrtnNm;
	
	private String joinEmpId; // 이크리스 추가 정보
	
	private String returnUrl;
	
	private String certiType;

}
