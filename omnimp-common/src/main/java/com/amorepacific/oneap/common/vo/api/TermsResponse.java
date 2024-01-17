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
package com.amorepacific.oneap.common.vo.api;

import com.amorepacific.oneap.common.vo.CommonVo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.api 
 *    |_ ChTermsResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 17.
 * @version : 1.0
 * @author  : takkies
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TermsResponse extends CommonVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1147092283445775490L;

	@ApiModelProperty(value = "API별 결과코드값", position = 2)
	private String resultMessage;
	
	@ApiModelProperty(value = "약관동의 타입", position = 3)
	private String agreeType;
	
	@ApiModelProperty(value = "약관동의 URL", position = 4)
	private String agreeUrl;
	
	@ApiModelProperty(value = "암호화된 고객통합번호", position = 5)
	private String xincsNo;
	
}
