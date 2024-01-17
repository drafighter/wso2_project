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
 * Date   	          : 2020. 8. 12..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import com.amorepacific.oneap.common.matcher.RegexUrlPathMatcher;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.filter 
 *    |_ AuthFilter.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 12.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
public class AuthFilter implements Filter {

	private boolean isLoggingFilter;

	private static ConfigUtil config = ConfigUtil.getInstance();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		this.isLoggingFilter = Boolean.parseBoolean(filterConfig.getInitParameter("loggingFilter"));

		log.debug("*** is logging filter : {}", this.isLoggingFilter);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;
		// p3p 헤더 추가
		//resp.setHeader("P3P", "CP='IDC DSP COR CURa ADMa OUR IND PHY ONL COM STA'");
		resp.addHeader("P3P", "CP=\"IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT\"");
		resp.setHeader("X-Content-Type-Options", "nosniff");
		resp.setHeader("X-Frame-Options", "deny");
		final String chCd = WebUtil.getStringParameter(request, OmniConstants.CH_CD, WebUtil.getStringSession(OmniConstants.CH_CD_SESSION)).replaceAll("(?i)script|object|applet|embed|form|alert|href|cookie|input|src|fromcharcode|encodeuri|encodeuricomponent|expression|iframe|window|location|style|eval","").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>","").replaceAll("\r|\n|&nbsp;","");
		final String channelCd = WebUtil.getStringParameter(request, "channelCd").replaceAll("(?i)script|object|applet|embed|form|alert|href|cookie|input|src|fromcharcode|encodeuri|encodeuricomponent|expression|iframe|window|location|style|eval","").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>","").replaceAll("\r|\n|&nbsp;","");
		if (StringUtils.hasText(chCd)) {
			WebUtil.setSession(request, OmniConstants.CH_CD_SESSION, chCd);
		} else { // 파라미터에도 없고 세션에도 없는 경우
			WebUtil.setSession(request, OmniConstants.CH_CD_SESSION, channelCd);
		}

		final String reqUrl = request.getRequestURL().toString();
		boolean match = new RegexUrlPathMatcher(config.skipResources()).matches(reqUrl, null);
		if (!match) {
			if (this.isLoggingFilter) {
				log.debug("***** do logging filter : {}", reqUrl);
			}
		}

		filterChain.doFilter(servletRequest, resp);

	}

}
