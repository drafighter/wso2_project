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
 * Date   	          : 2020. 9. 28..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.admin.vo 
 *    |_ AdminSearchList.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 28.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class AdminSearchList {
	
	@ApiModelProperty(required = false,	value = "아이디", position = 0) //
	private String webId;
	@ApiModelProperty(required = false,	value = "통합고객번호", position = 1) //
	private String incsNo;
	@ApiModelProperty(required = false,	value = "고객명", position = 2) //
	private String name;
	@ApiModelProperty(required = false,	value = "경로코드", position = 3) //
	private String chCd;
	@ApiModelProperty(required = false,	value = "전환여부", position = 4) //
	private String swtYn;
	@ApiModelProperty(required = false,	value = "전환일시", position = 5) //
    private String swtDttm; 
	@ApiModelProperty(required = false,	value = "고객휴대폰번호", position = 6) //
    private String mobile = "";
}
