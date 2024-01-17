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
package com.amorepacific.oneap.common.vo.user;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.user 
 *    |_ UmChUser.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 13.
 * @version : 1.0
 * @author : takkies
 */
@Data
@SuppressWarnings("serial")
public class UmChUser implements Serializable{

	/**
	 * 
	 */
	private String chCd; // 경로구분코드
	private String chcsWebId; // 경로고객웹ID
	private String linPwdEc; // 로그인 비.밀.번.호
	private int incsNo; // 통합고객번호
	private String incsWebIdSwtYn; // 통합고객웹ID전환여부
	private Timestamp incsWebIdSwtDttm; // 통합고객웹ID전환일시
	
}
