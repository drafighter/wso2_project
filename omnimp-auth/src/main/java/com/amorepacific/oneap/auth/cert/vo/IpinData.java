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
 * Date   	          : 2020. 8. 5..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.cert.vo;

import com.amorepacific.oneap.auth.login.vo.LoginVo;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.cert.vo 
 *    |_ IpinData.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 5.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class IpinData {

	private int result;
	private String encData;
	private String cpRequest;
	private String certUrl;
	private LoginVo login;
	
	
}
