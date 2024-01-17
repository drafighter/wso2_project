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
 * Date   	          : 2023. 3. 28..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.membership.vo.naver;

import java.io.Serializable;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.auth.membership.vo.naver 
 *    |_ Contents.java
 * </pre>
 *
 * @desc    :
 * @date    : 2023. 3. 28.
 * @version : 1.0
 * @author  : hjw0228
 */

@Data
public class Contents implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5784423256389643323L;
	private String interlockMemberIdNo; // 네이버회원식별번호
	private String interlockSellerNo; // 제휴사연동스토어(브랜드)번호
}
