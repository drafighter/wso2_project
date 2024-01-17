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
 * Date   	          : 2020. 8. 4..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.login.vo;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.login.vo 
 *    |_ AuthVo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 4.
 * @version : 1.0
 * @author  : takkies
 */
@Data
public class Web2AppVo {
	
	private String web2appid;
	private String accesstoken;
	private String username;
	private String rqdttm;
	private String exprdttm;
	private String athtdttm;
	private String userathtst;
	private int consumerkeyid;
	private String requiretime;
	private String expiretime;
	private String chcd;
}
