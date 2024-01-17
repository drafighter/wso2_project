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
 * Date   	          : 2020. 7. 9..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * com.apmorepacific.oneap.common.vo 
 *    |_ CommonVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */
@ToString
@Getter
@Setter
public class CommonVo implements Serializable {
	
	private static final long serialVersionUID = -6920522178502636749L;

		// 공통 응답 코드 설명		
		@ApiModelProperty(value = "처리 트랜잭션값", hidden = false, position = 0)
		private String trxUuid;
		
		@ApiModelProperty(value = "API별 결과코드값", position = 1)
		private String resultCode;

		public CommonVo() {
		}

		public CommonVo(String resultCode) {
			this.resultCode = resultCode;
		}
}
