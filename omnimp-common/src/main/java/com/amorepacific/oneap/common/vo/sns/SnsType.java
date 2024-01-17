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
 * Author	          : hkdang
 * Date   	          : 2020. 9. 17..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.vo.sns;

/**
 * <pre>
 * com.amorepacific.oneap.common.vo.sns 
 *    |_ SnsType.java
 * </pre>
 *
 * @desc    : SNS type
 * @date    : 2020. 9. 17.
 * @version : 1.0
 * @author  : hkdang
 */

public enum SnsType {
	KAKAO("KA")
	, NAVER("NA")
	, FACEBOOK("FB")
	, APPLE("AP")
	;
	
	private String type;
	
	private SnsType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}	
}
