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
 * Author	          : yjhan
 * Date   	          : 2020. 8. 7..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.mgmt.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.mgmt.vo 
 *    |_ UserVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 7.
 * @version : 1.0
 * @author  : yjhan
 */
@Getter
@Setter
public class AbusingUserVo{

	private String lockEmpId; //계정잠금요청직원ID
	private int incsNo; //계정잠금통합고객번호
	private String lockRsnCd;  //회원플랫폼계정잠금사유코드
	private String lockRegDttm; //계정잠금등록일시
	private String lockCancDttn; //계정잠금해제일시 
	private String lockCanaChCd; //계정잠금해제경로구분코드
	private String fscrId; //최초생성ID
	private String fscrTsp; //최초생성시각
	private String lschId; //최종변경ID
	private String lschTsp; //최종변경시각
	private String sdtpCd; //표준시간대코드
	private String applCld; //등록어플리케이션구분코드
	
}
