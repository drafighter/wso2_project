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
 *    |_ AdminSearchRequest.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 28.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class AdminSearchRequest {

	@ApiModelProperty(required = false,	value = "검색할 아이디", position = 0) //
	private String webId;
	@ApiModelProperty(required = false,	value = "검색할 통합고객번호", position = 1) //
	private String incsNo;
	@ApiModelProperty(required = true,	value = "목록에 보여지는 갯수", position = 2, example="10") //
	int listSize = 10; // 목록에 보여지는 갯수
	@ApiModelProperty(required = true,	value = "하단에 보여지는 페이지 단위 갯수", position = 3, example="10") //
	int pageUnitSize = 10; // 하단에 보여지는 페이지 단위 갯수, << < 1 2 3 4 5 6 7 8 9 10 > >> 
	@ApiModelProperty(required = true,	value = "페이징 시작 번호", position = 4, example="0") //
	int offsetSize = 0;
	@ApiModelProperty(required = true,	value = "현재 페이지 번호", position = 5, example="1") //
	int currentPage = 1;
	
}
