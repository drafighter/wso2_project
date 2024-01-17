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
 * Date   	          : 2020. 9. 15..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ ChTermsVo.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 15.
 * @version : 1.0
 * @author : takkies
 */
@Data
public class ChTermsVo implements Serializable {
	
	private static final long serialVersionUID = -1160919538994491330L;
	private String incsNo;
	private String tcatCd; // 코드값 일치여부 체크함. 잘못 된 코드나 N로 전달 되는 경우 9000번 에러메시지
	private String tncvNo;
	private String tncAgrYn; // tncAgrYn 값의 Y 일치여부 판단. 잘못 된 코드나 N로 전달 되는 경우 9000번 에러메시지
	private String tncaDttm;

}
