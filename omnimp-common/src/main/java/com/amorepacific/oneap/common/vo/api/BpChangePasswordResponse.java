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
 * Date   	          : 2020. 9. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import com.amorepacific.oneap.common.vo.CommonVo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ BpChangePasswordResponse.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 21.
 * @version : 1.0
 * @author : takkies
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BpChangePasswordResponse extends CommonVo {

	private static final long serialVersionUID = -8867021738478645505L;
	private String result; // SUCCESS : 성공 ERROR : 오류
	private String rsltCd; // 000, 400, 401, 402, 900
	private String rsltMsg; // 000: 정상처리되었습니다 400: 필수 파라미터 부족 401: 기 등록된 패스워드 402: 패스워드 불일치 900: 비밀번호 변경 중 오류 발생 |

}
