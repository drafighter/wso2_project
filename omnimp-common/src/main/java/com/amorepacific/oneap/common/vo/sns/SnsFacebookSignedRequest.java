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
 * Author	          : jspark2
 * Date   	          : 2021. 1. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.sns;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.sns 
 *    |_ SnsFacebookSignedRequest.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 1. 21.
 * @version : 1.0
 * @author  : jspark2
 */
@Data
public class SnsFacebookSignedRequest {
	@JsonProperty("signed_request")
	@SerializedName("signed_request")
	private String signedRequest;
	
	
	// form-urlencoded 데이터 받을 때 사용
	public void setSigned_request(String signedRequest) {
        setSignedRequest(signedRequest);
    }
}
