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
 * Date   	          : 2020. 8. 27..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.common.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.amorepacific.oneap.api.common.mapper.CommonMapper;
import com.amorepacific.oneap.common.vo.Channel;

/**
 * <pre>
 * com.amorepacific.oneap.auth.common.service 
 *    |_ CommonService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 27.
 * @version : 1.0
 * @author  : takkies
 */
@Service
public class ChannelService {

	@Autowired
	private CommonMapper commonMapper;
	
	@Cacheable(value = "findChannelInfo", key="#chCd")
	public Channel getChannel(final String chCd) {
		return this.commonMapper.getChannel(chCd);
	}
	
	@Cacheable(value = "findChannelInfos")
	public List<Channel> getChannels() {
		return this.commonMapper.getChannels();
	}
	
	@CacheEvict(value = "findChannelInfos", allEntries = true)
	public void refreshAllChannel() {
		 
	}
	
	@CacheEvict(value = "findChannelInfo", allEntries = true)
	public void refreshChannel() {
		
	}
}
