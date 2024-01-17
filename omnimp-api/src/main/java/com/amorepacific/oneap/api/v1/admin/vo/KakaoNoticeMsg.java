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
 * Date   	          : 2023. 4. 12..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.admin.vo 
 *    |_ KakaoNoticeMsg.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 4. 12.
 * @version : 1.0
 * @author  : hjw0228
 */

@Getter
@Setter
public class KakaoNoticeMsg {
	
	@ApiModelProperty(required = true,	value = "휴대폰 번호",	position = 0) 
	private String phone; // 휴대폰 번호
	
	@ApiModelProperty(required = true,	value = "제휴사명", example = "네이버(주)",	position = 1)
	private String prtnName; // 제휴사명
	
	@ApiModelProperty(required = true,	value = "항목", example = "성명, 휴대전화번호, CI, 생년월일, 내/외국인 구분, 고객번호",	position = 2)
	private String category; // 항목
	
	@ApiModelProperty(required = true,	value = "목적", example = "아모레퍼시픽 뷰티포인트 적립",	position = 3)
	private String purpose; // 목적
	
	@ApiModelProperty(required = true,	value = "URL", example = "https://one-ap.amorepacific.com/auth/terms/naver",	position = 4)
	private String url; // URL
}
