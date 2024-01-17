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
package com.amorepacific.oneap.auth.common.service;

import java.util.List;
import java.util.stream.Collectors;

//import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.CommonVo;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.user.Customer;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.common.service 
 *    |_ CommonService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 18.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class CommonService {

	@Autowired
	private ChannelService channelService;

	@Autowired
	private CustomerApiService customerApiService;

	public List<Channel> getChannels() {
		return this.channelService.getChannels();
	}

	public Channel getChannel(String chCd) {
		if (StringUtils.isEmpty(chCd)) {
			chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		}
		CommonVo commonVo = new CommonVo();
		final String chCdParam = chCd;
		
		if (StringUtils.isEmpty(chCdParam)) {
			commonVo.setResultCode("CH_CD_ERROR_INVALID");
			log.warn("★★★★★★★★★★ channel code empty, maybe session expired!!! [%s] : {}", WebUtil.getRequest().getSession().getId());
			// throw new BusinessException(commonVo, String.format("channel code empty, session expired!!! [%s]", WebUtil.getRequest().getSession().getId()));
		}

		List<Channel> channelList = this.channelService.getChannels();

		channelList = channelList.stream() //
				.filter(ch -> chCdParam.equals(ch.getChCd())) //
				.collect(Collectors.toList());

		
		if (channelList == null || channelList.isEmpty()) {
			log.warn("★★★★★★★★★★ channel is empty, invalid channel code : {}", chCdParam);
			commonVo.setResultCode("CH_CD_ERROR_INVALID");
			// throw new BusinessException(commonVo, String.format("channel code invalid!!!, %s", chCdParam));
		} else {
			final Channel channel = this.channelService.getChannel(chCdParam);
			if (channel == null) {
				commonVo.setResultCode("CH_CD_ERROR_INVALID");
				// throw new BusinessException(commonVo, String.format("channel code invalid!!!, %s", chCdParam));
			} else {
				Object obj = WebUtil.getSession(OmniConstants.SSOPARAM);
				if(obj != null) {
					SSOParam ssoParam = (SSOParam) obj;
					if(StringUtils.hasText(ssoParam.getHmpgUrlToCancelUri()) && StringUtil.isTrue(ssoParam.getHmpgUrlToCancelUri()) && StringUtils.hasText(ssoParam.getCancelUri())) {
						channel.setHmpgUrl(ssoParam.getCancelUri());
					}
				}
				return channel;
			}
		}
		return null;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 12. 오전 9:06:48
	 * </pre>
	 * 
	 * @param incsNo
	 * @return
	 */
	public CertResult getCertResult(final String incsNo) {
		if (WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION) == null) {
			CustInfoVo custInfoVo = new CustInfoVo();
			custInfoVo.setIncsNo(incsNo);
			Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
			if (customer != null) {
				CertResult certResult = new CertResult();
				certResult = new CertResult();
				certResult.setName(customer.getCustNm());
				certResult.setGender(customer.getSxclCd());
				certResult.setGenderCode(customer.getSxclCd());
				certResult.setPhone(StringUtil.mergeMobile(customer));
				certResult.setBirth(customer.getAthtDtbr());
				certResult.setCiNo(customer.getCiNo());
				certResult.setForeigner(customer.getFrclCd());
				return certResult;
			}
			return null;
		} else {
			CertResult certResult = (CertResult) WebUtil.getSession(OmniConstants.CERT_RESULT_SESSION);
			if (certResult != null) {
				// 본인인증 사용자명을 통합고객에서 받아서 업데이트
				if (StringUtils.hasText(incsNo) && !"0".equals(incsNo)) {
					CustInfoVo custInfoVo = new CustInfoVo();
					custInfoVo.setIncsNo(incsNo);
					Customer customer = this.customerApiService.getCicuemcuInfrByIncsNo(custInfoVo);
					if (customer != null) {
						certResult.setName(customer.getCustNm());
					}
					return certResult;
				}
			}
			return certResult;
		}
	}

	public void channelCacheClear() {
		this.channelService.refreshAllChannel();
		this.channelService.refreshChannel();
	}
}
