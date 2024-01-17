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
 * Date   	          : 2020. 7. 24..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.amorepacific.oneap.common.matcher.RegexUrlPathMatcher;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.WebUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.interceptor 
 *    |_ AuthLoggingInterceptor.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 24.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
public class AuthLoggingInterceptor extends HandlerInterceptorAdapter {

	private static ConfigUtil config = ConfigUtil.getInstance();
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//final String reqUrl = request.getRequestURL().toString();
		//boolean match = new RegexUrlPathMatcher(config.skipResources()).matches(reqUrl, null);
		//if (match) {
		//	return true;
		//}
		
		return true; // return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
		final String reqUrl = request.getRequestURL().toString();
		boolean match = new RegexUrlPathMatcher(config.skipResources()).matches(reqUrl, null);
		if (match) {
			return;
		}
		
		log.debug("auth handler : {}", handler.toString());
		log.debug("auth logging uri : {}, url : {}", request.getRequestURI(), WebUtil.getCurrentUrl(request));
		log.debug("auth logging referer : {}", WebUtil.getReferer(request));

		// super.postHandle(request, response, handler, modelAndView);
	}

}
