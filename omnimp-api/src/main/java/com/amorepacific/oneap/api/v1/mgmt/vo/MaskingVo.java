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
 * Date   	          : 2020. 8. 4..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.mgmt.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.mgmt.vo 
 *    |_ MaskingVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 4.
 * @version : 1.0
 * @author  : yjhan
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaskingVo {
	
	@ApiModelProperty(required = true, value = "경로구분코드", position = 0, example="030") 
	private String chCd = "030"; 
	
	@ApiModelProperty(required = true, value = "마스킹할 문자열", position = 1) 
	private String maskingText; 
	
	@ApiModelProperty(required = true, value = "마스킹할 타입", position = 2) 
	private String maskingType; 
	
	@ApiModelProperty(required = false, value = "마스킹할 국가코드", position = 3, example="KR") 
	private String maskingLocale = "KR"; 


}
