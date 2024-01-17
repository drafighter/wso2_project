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
 * Date   	          : 2020. 12. 2..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ SnsVo.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 12. 2.
 * @version : 1.0
 * @author : takkies
 */
@Getter
@Setter
public class SnsVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2758618781885299214L;
	private String connectYN; // 연결/해제(연결 Y , 연결해제 N)
	private String ucstmid; // 통합고객번호
	private String cstmid; // 고객WEB ID
	private String snsAuthkey; // SNS KEY
	private String snsType; // SNS TYPE KA:카카오, NA:naver, FB: face book

}
