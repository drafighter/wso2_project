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
 * Date   	          : 2020. 9. 18..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.common.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amorepacific.oneap.api.exception.ApiBusinessException;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.OmniConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.common.service 
 *    |_ CommonService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 18.
 * @version : 1.0
 * @author  : takkies
 */
@Slf4j
@Service
public class CommonService {

	@Autowired
	private ChannelService channelService;
	
	public List<Channel> getChannels() {
		return this.channelService.getChannels();
	}

	public Channel getChannel(String chCd) throws ApiBusinessException {
		
		if (StringUtils.isEmpty(chCd)) {
			chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		}

		final String chCdParam = chCd;

		List<Channel> channelList = this.channelService.getChannels();
		channelList = channelList.stream() //
				.filter(ch -> chCdParam.equals(ch.getChCd())) //
				.collect(Collectors.toList());
		if (channelList == null || channelList.isEmpty()) {
			log.warn("★★★★★★★★★★ channel is empty, invalid channel code : {}", chCdParam);
			throw new ApiBusinessException(String.format("channel code invalid!!!, %s", chCdParam), ApiBusinessException.CHANNEL_CODE_ERROR);
		} else {
			Channel channel = this.channelService.getChannel(chCdParam);
			if (channel == null) {
				throw new ApiBusinessException(String.format("channel code invalid!!!, %s", chCdParam), ApiBusinessException.CHANNEL_CODE_ERROR);
			} else {
				return channel;
			}
		}

	}
}
