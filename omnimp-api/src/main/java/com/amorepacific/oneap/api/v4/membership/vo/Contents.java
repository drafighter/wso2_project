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
 * Date   	          : 2023. 3. 25..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v4.membership.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * com.amorepacific.oneap.api.v4.membership.vo 
 *    |_ Contents.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 3. 25.
 * @version : 1.0
 * @author  : hjw0228
 */

@Getter
@Setter
public class Contents {

	private String affiliateMemberIdNo; // 제휴사회원식별번호
	private String interlockMemberIdNo; // 네이버회원식별번호
	private String interlockSellerNo; // 제휴사연동스토어(브랜드)번호
	private boolean interlock; // 연동상태
}
