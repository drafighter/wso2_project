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
 * Date   	          : 2020. 9. 17..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.user;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.user 
 *    |_ PointResponse.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 17.
 * @version : 1.0
 * @author : takkies
 */
@Data
public class PointResponse {
	private String rsltCd; // 응답코드
	private String rsltMsg; // 결과메시지
	private String incsNo; // 통합고객번호
	private String rmnPt; // 잔여포인트
	private String accmAcmlPt; // 누적적립포인트
	private String accmUsgPt; // 누적사용포인트

	private String accmExtcPt; // 누적소멸포인트
	private String custNm; // 고객명

}
