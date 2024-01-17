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
 * Date   	          : 2021. 2. 22..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.interceptor 
 *    |_ SystemCheckInterceptor.java
 * </pre>
 *
 * @desc    :
 * @date    : 2021. 2. 22.
 * @version : 1.0
 * @author  : hjw0228
 */
@Slf4j
public class SystemCheckInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private SystemInfo systemInfo;
	
	private static ConfigUtil config = ConfigUtil.getInstance();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		String isSystemCheck = config.getString("common." + profile + ".systemcheck", "false");
		
		if("true".equals(isSystemCheck)) {
			response.sendRedirect(request.getContextPath() + "/info-svc");
	        return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		return;
	}
}
