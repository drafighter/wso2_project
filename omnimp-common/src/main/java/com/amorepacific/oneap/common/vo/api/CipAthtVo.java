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
 * Date   	          : 2021. 10. 27..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.api;

import com.amorepacific.oneap.common.vo.OmniConstants;

import lombok.Builder;
import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ CipAthtVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 10. 27.
 * @version : 1.0
 * @author  : hjw0228
 */
@Data
@Builder
public class CipAthtVo {

	@Builder.Default
	private final String chCd = OmniConstants.CIP_ATHT_CH_CD; // 필수: 채널코드
	@Builder.Default
	private String sysCd = OmniConstants.CIP_ATHT_SYS_CD;; // 필수: 어플리케이션 구분코드
}
