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
 * Date   	          : 2020. 9. 16..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.sns;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.sns 
 *    |_ JoinOnUnlinkRequest.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 16.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class SnsConnectRequest {
	
	@ApiModelProperty(value = "연결/해제(연결 Y , 연결해제 N)", position = 0)
	private String connectYN; // 연결/해제
	@ApiModelProperty(value = "통합고객번호", position = 1)
	private String ucstmid; // 통합고객번호
	@ApiModelProperty(value = "고객WEB ID", position = 2)
	private String cstmid; // 고객WEB ID
	@ApiModelProperty(value = "SNS KEY", position = 3)
	private String snsAuthkey; // SNS KEY
	@ApiModelProperty(value = "SNS TYPE(KA:카카오, NA:naver, FB: face book)", position = 4)
	private String snsType; // SNS TYPE KA:카카오, NA:naver, FB: face book

}
