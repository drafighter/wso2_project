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
 * Date   	          : 2022. 10. 18..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v2.joinon.vo;

import com.amorepacific.oneap.common.vo.CommonVo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 * com.amorepacific.oneap.api.v2.joinon.vo 
 *    |_ IdCheckResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 10. 18.
 * @version : 1.0
 * @author  : judahye
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class IdCheckResponse extends CommonVo{
	
	private static final long serialVersionUID = 1236836274853571095L;
	private String rsltCd;
	public void SetResponseInfo(JoinOnResultCode resultCode) {
		this.setRsltCd(resultCode.getCode()); // add result code
	}

}
