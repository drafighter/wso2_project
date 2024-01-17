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
 * Date   	          : 2021. 12. 23..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.emp;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.emp 
 *    |_ Employeer.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 12. 23.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
public class Employeer implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = -7555120403968585759L;
	
	private String incsNo; // 통합고객번호
	private String custNm; // 고객명
	private String cmpyCd; // 회사코드
	private String cmpyNm; // 회사명
	private String empId; // 사원아이디
	private String cmpyEmid; // 회사이메일계정
	private String cmpyEmdn; // 회사이메일번지
	private String empBnftTgtCd; // 임직원혜택대상코드
	private String empAthtDt; // 인증일자
	private String empRsgnDt; // 퇴사일자
	private String rsltCd; // 결과코드
	private String rsltMsg; // 결과메세지

}
