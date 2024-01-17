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
 * Date   	          : 2020. 7. 22..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.login.vo;

import java.util.List;
import java.util.Map;

import com.amorepacific.oneap.common.vo.SSOParam;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.common.vo 
 *    |_ LoginResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 22.
 * @version : 1.0
 * @author : takkies
 */
@Data
public class LoginResponse {

	private Integer status;
	private String result;
	private String message;
	private String redirectUrl;
	private Map<String, String> idpAuthenticatorMapping;
	private List<String> localAuthenticatorNames;
	private Integer hasLocalLoginOptions;
	private Integer includeBasicAuth;
	private Integer isBackChannelBasicAuth;
	private String authUrl;
	private String loginFormActionUrl;
	private String username;
	
	private String sessionDataKey;
	private String queryString;
	
	private String saveId;
	private boolean autoLoginOption;
	
	private SSOParam ssoParam;
}
