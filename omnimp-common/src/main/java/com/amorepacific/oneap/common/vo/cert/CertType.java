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
 * Date   	          : 2020. 12. 2..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.cert;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.cert 
 *    |_ CertType.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 12. 2.
 * @version : 1.0
 * @author  : takkies
 */
@Getter
@Setter
public class CertType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6026906081880398964L;
	private String certiType;
}
