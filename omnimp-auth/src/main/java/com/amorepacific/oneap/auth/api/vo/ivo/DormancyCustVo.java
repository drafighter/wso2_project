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
 * Author	          : mcjan
 * Date   	          : 2020. 8. 11..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.api.vo.ivo;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.api.vo.ivo 
 *    |_ DormancyCustVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 11.
 * @version : 1.0
 * @author  : mcjan
 */
@Data
public class DormancyCustVo {
	/**
	 * 필수: 고객명
	 */
	private String custNm;
	/**
	 * 필수: 생년월일
	 */
	private String athtDtbr;
	/**
	 * 휴대폰식별전화번호
	 */
	private String cellTidn;
	/**
	 * 휴대폰국전화번호
	 */
	private String cellTexn;
	/**
	 * 휴대폰끝전화번호
	 */
	private String cellTlsn;
	/**
	 * 멤버십카드번호
	 */
	private String incsCardNoEc;

}
