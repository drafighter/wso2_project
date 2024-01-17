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

import com.amorepacific.oneap.common.vo.api.CipAthtVo;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.emp 
 *    |_ SendPrcnEmpInfoVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 12. 23.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
public class SendPrcnEmpInfoVo implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = -3890536344473163560L;
	private String incsNo; // 통합고객번호
	private String chcsNo; // 경로고객번호
	private String athtRqDt; // 인증요청일자
	private String cmpyCd; // 회사코드
	private String cmpyEmid; // 회사이메일계정
	private String cmpyEmdn; // 회사이메일번지
	private String athtUrl; // 인증URL
	private String returnUrl; // Landing URL
	private String fscrId; // 등록자ID
	
	private CipAthtVo cipAthtVo;
}
