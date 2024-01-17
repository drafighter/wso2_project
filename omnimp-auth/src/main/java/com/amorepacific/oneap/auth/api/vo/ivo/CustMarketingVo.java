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
 * Date   	          : 2020. 9. 14..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.api.vo.ivo;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.api.vo.ivo 
 *    |_ CustMarketingVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 14.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class CustMarketingVo {
	
	private String chCd;
	private String chgChCd;
	private String incsNo;
	private String emlOptiYn;
	private String smsOptiYn;
	private String dmOptiYn;
	private String tmOptiYn;
	private String kkoIntlOptiYn;
	private String fscrId;
	private String lschId;
	
}
