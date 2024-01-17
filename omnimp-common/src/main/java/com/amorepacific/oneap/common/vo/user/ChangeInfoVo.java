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
 * Date   	          : 2020. 9. 17..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.user;

import java.util.List;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.user 
 *    |_ ChangeInfoVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 17.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class ChangeInfoVo {
	
	private String cpw; // current password
	private String npw; // new password
	private String ncpw; // new confirm password
	
	private String reason; // withdraw reason
	private String content;  // withdraw content
	
	private List<ChangeTerms> marketing;
	private List<ChangeTerms> bpterms;
	
	@Data
	public static class ChangeTerms {
		private String tncAgrYn;
		private String tcatCd;
		private String tncvNo;
	}
	
}
