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
 * Date   	          : 2020. 8. 12..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.cert;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.cert.vo 
 *    |_ CertResult.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 12.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class CertResult implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4986713851163884453L;
	private String name; // 이름
	private String gender; // 성별
	private String genderCode; // 성별코드값
	private String birth; // 생년월일
	private String phone; // 휴대폰번호
	private String foreigner; //내외국인
	private String ciNo; // CI값
	
	private String chCd;
	private String id; // 회원아이디
	private String category; // 인증 종류 : 휴대폰, 아이핀, SNS
}
