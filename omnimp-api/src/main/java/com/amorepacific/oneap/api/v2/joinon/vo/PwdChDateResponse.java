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
 * Date   	          : 2022. 10. 7..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v2.joinon.vo;

import com.amorepacific.oneap.common.vo.CommonVo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.v2.joinon.vo 
 *    |_ PwdChDateResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 10. 7.
 * @version : 1.0
 * @author  : judahye
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class PwdChDateResponse extends CommonVo{
	
	private static final long serialVersionUID = 1236836274853571095L;
	private String chDate; //변경일
	private String resultCode; //결과 코드
	private String resultMsg;	//결과 메세지
	
	public void SetResponseInfo(JoinOnResultCode resultCode) {
		this.setResultCode(resultCode.getCode()); // add result code
		this.setResultMsg(resultCode.message());
	}

}
