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
 * Date   	          : 2023. 2. 17..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ DeleteCustChRequest.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 2. 17.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
public class DeleteCustChRequest {

	private String joinCnclYn;
	private List<CicuedCuChTcVo> cicuedCuChTcVo;
	
	public void addCicuedCuChTcVo(final CicuedCuChTcVo cicuedCuChTcVos) {
		if (cicuedCuChTcVo == null) {
			cicuedCuChTcVo = new ArrayList<>();
		}
		cicuedCuChTcVo.add(cicuedCuChTcVos);
	}
	
	@Data
	public static class CicuedCuChTcVo {
		private String incsNo; //필수: 통합고객번호
		private String chCd; // 필수: 경로구분코드
	}
}
