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
 * Date   	          : 2020. 7. 9..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.exception;

import com.amorepacific.oneap.common.vo.CommonVo;

/**
 * <pre>
 * com.apmorepacific.oneap.common.exception 
 *    |_ BizException.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

public class BusinessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5482882139499635295L;

	CommonVo commonVo;
	
	public BusinessException(CommonVo commonVo, String message) {
		super(message);
        this.commonVo = commonVo;
    }
	
	public BusinessException(CommonVo commonVo) {
        super("ErrorCode : " + commonVo.getResultCode());
        
        this.commonVo = commonVo;
    }
	
	public String getResultCode() {
		return this.commonVo.getResultCode();
	}
	
	
    
    public BusinessException(String message) {
        super(message);
    }   
}
