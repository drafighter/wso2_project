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
 * Date   	          : 2020. 7. 9..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.amorepacific.oneap.common.matcher.RegexUrlPathMatcher;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.UuidUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;

/**
 * <pre>
 * com.amorepacific.oneap.common.handler 
 *    |_ CommonTrxUuIdGenerateHandler.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 9.
 * @version : 1.0
 * @author : takkies
 */

@Component("trxUuidGeneratorHandler")
public class CommonTrxUuIdGenerateHandler extends HandlerInterceptorAdapter {

	private static ConfigUtil config = ConfigUtil.getInstance();
	
	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
		
		final String reqUrl = request.getRequestURL().toString();
		boolean match = new RegexUrlPathMatcher(config.skipResources()).matches(reqUrl, null);
		if (match) {
			return true;
		}
		
		String trxUuid = WebUtil.getHeader(OmniConstants.TRX_UUID);
		if(trxUuid == null) {
			trxUuid = UuidUtil.getUuid();
		}
		// response.setHeader(OmniConstants.TRX_UUID, trxUuid);
		// log.debug("transaction uuid is {} --> {}", handler.toString(), trxUuid);
		MDC.put(OmniConstants.TRX_UUID, trxUuid); // MDC.clear();
		return true;
	}
	
}
