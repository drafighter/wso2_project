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
 * Author	          : hjw0228
 * Date   	          : 2022. 8. 31..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.sns;

import java.util.List;

import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.sns 
 *    |_ ApplePublicKeysResponse.java
 * </pre>
 *
 * @desc    :
 * @date    : 2022. 8. 31.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class ApplePublicKeysResponse extends ApiBaseResponse {/**
	 * 
	 */
	private static final long serialVersionUID = 7603494692924222557L;
	
	private List<Key> keys;
    
    @Data
    public static class Key {
    	private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;
        private String e;
    }
}
