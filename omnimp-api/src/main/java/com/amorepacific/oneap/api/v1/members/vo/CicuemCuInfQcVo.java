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
 * Author	          : judahye
 * Date   	          : 2022. 3. 17..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.members.vo;

import com.amorepacific.oneap.common.vo.user.Customer;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.member.vo 
 *    |_ CicuemCuInfQcVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 3. 17.
 * @version : 1.0
 * @author  : judahye
 */
@Data
public class CicuemCuInfQcVo {
	private Customer[] cicuemCuInfQcVo;
	
	private String rsltCd;
	private String rsltMsg;
	
	private String chcsNo;
	private String chCd;
	private String incsNo;
}
