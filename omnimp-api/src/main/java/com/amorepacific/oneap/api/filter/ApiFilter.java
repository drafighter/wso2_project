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
 * Date   	          : 2020. 11. 3..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre>
 * com.amorepacific.oneap.api.filter 
 *    |_ ApiFilter.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 3.
 * @version : 1.0
 * @author  : takkies
 */
public class ApiFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		
		if (response instanceof HttpServletResponse) {
			((HttpServletResponse)response).setHeader("X-Content-Type-Options", "nosniff");
			((HttpServletResponse)response).setHeader("X-Frame-Options", "deny");
			//((HttpServletResponse)response).setHeader("Content-Security-Policy", "default-src 'none'");
		}
		
		filterChain.doFilter(request, response);
	}

}
