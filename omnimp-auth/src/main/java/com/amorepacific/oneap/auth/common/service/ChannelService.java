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
package com.amorepacific.oneap.auth.common.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.common.mapper.CommonMapper;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.OmniConstants;

/**
 * <pre>
 * com.amorepacific.oneap.auth.common.service 
 *    |_ CommonService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 27.
 * @version : 1.0
 * @author : takkies
 */
@Service
public class ChannelService {

	@Value("${kmcis.url.code}")
	private String localKmcUrlCode;
	
	@Autowired
	private CommonMapper commonMapper;
	
	@Autowired
	private SystemInfo systemInfo;

	@Cacheable(value = "findChannelInfo", key="#chCd")
	public Channel getChannel(final String chCd) {
		Channel channel = this.commonMapper.getChannel(chCd);
		if (this.systemInfo.hasLocalProfile()) {
			channel.setKmcChnUrlCdVl(localKmcUrlCode);
		}
		
		// 허용경로라디이렉션URL목록 처리
		if(StringUtils.hasText(channel.getPrmsChnRdrcUrlLv())) {
			String[] prmsChnRdrcUrlList = channel.getPrmsChnRdrcUrlLv().split(",");
			channel.setPrmsChnRdrcUrlList(prmsChnRdrcUrlList);
		}
		
		return channel;
	}

	@Cacheable(value = "findChannelInfos")
	public List<Channel> getChannels() {
		List<Channel> channels = this.commonMapper.getChannels();
		if (this.systemInfo.hasLocalProfile()) {
			for (Channel channel : channels) {
				channel.setKmcChnUrlCdVl(localKmcUrlCode);
				
				// 허용경로라디이렉션URL목록 처리
				if(StringUtils.hasText(channel.getPrmsChnRdrcUrlLv())) {
					String[] prmsChnRdrcUrlList = channel.getPrmsChnRdrcUrlLv().split(",");
					channel.setPrmsChnRdrcUrlList(prmsChnRdrcUrlList);
				}
			}
		}
		return channels;
	}

	public boolean isChannelEntryUser(final String chCd) {
		Channel channel = getChannel(chCd);
		if (channel.getChCd().equals(OmniConstants.JOINON_CHCD)) {
			return false;
		} else {
			return true;
		}
	}

	@CacheEvict(value = "findChannelInfos", allEntries = true)
	public void refreshAllChannel() {

	}

	@CacheEvict(value = "findChannelInfo", allEntries = true)
	public void refreshChannel() {

	}
}
