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
 * Date   	          : 2022. 3. 18..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.members.vo;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.api.v1.members.vo 
 *    |_ SsgCheckVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 3. 18.
 * @version : 1.0
 * @author  : judahye
 */
@Data
public class SsgCheckVo {
	private String chCd;
	private String xincsNo;
	private String memberId;
}
