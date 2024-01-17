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
 * Date   	          : 2020. 7. 15..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo 
 *    |_ UserAgentAccessInfo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 15.
 * @version : 1.0
 * @author : takkies
 */
@Getter
@Setter
@ToString
public class UserAgentAccessInfo implements Serializable {
	
	private static final long serialVersionUID = 794413478655987431L;
	private String browserName;
	private String browserMajor;
	private String browserMinor;
	private String browserPatch;
	private String osName;
	private String osMajor;
	private String osMinor;
	private String osPatch;
	private String osPatchMinor;
	private String deviceName;
	
}
