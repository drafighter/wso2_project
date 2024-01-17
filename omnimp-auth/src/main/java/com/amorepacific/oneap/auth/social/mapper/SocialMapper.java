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
 * Date   	          : 2020. 8. 18..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.social.mapper;

import java.util.Map;

import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.amorepacific.oneap.common.vo.sns.SnsUnlinkVo;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

/**
 * <pre>
 * com.amorepacific.oneap.auth.social.mapper 
 *    |_ SocialMapper.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 18.
 * @version : 1.0
 * @author  : takkies
 */

public interface SocialMapper {

	public String getIDNAssociatedId(Map<String, String> param);
	
	public SnsUnlinkVo getIDNAssociatedIdIncsNo(Map<String, String> param);
	
	public UmOmniUser getSnsMappingIncsNo(final SnsParam snsParam);
	
	public int updateSnsMapping(final SnsParam snsParam);
}
