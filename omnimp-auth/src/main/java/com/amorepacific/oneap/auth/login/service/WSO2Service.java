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
 * Author	          : mcjan
 * Date   	          : 2020. 8. 6..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.login.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amorepacific.oneap.auth.login.mapper.WSO2Mapper;
import com.amorepacific.oneap.auth.social.mapper.SocialMapper;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

/**
 * <pre>
 * com.amorepacific.oneap.auth.login.service 
 *    |_ WSO2Service.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 6.
 * @version : 1.0
 * @author  : mcjan
 */
@Service
public class WSO2Service {

	@Autowired
	private WSO2Mapper wso2Mapper;
	
	@Autowired
	private SocialMapper socialMapper;
	
	public String getUserNameBySnsInfo(String snsType, String snsId) {
		Map<String, String> sqlParam = new HashMap<>();
		sqlParam.put("snsType", snsType);
		sqlParam.put("snsId", snsId);
		
		return this.socialMapper.getIDNAssociatedId(sqlParam);
	}
			
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 28. 오전 9:58:20
	 * </pre>
	 * @param omniUser
	 * @return
	 */
	public List<UmOmniUser> getWso2UserInfo(final UmOmniUser omniUser) {
		return wso2Mapper.getWso2UserInfo(omniUser);
	}
	
	public List<UmChUser> getChannelUserInfo(final UmChUser chUser) {
		return wso2Mapper.getChannelUserInfo(chUser);
	}
	
	public boolean isExistChannelUser(final String incsNo, final String chCd) {
		return wso2Mapper.isExistChannelUser(incsNo, chCd) > 0;
	}
}
