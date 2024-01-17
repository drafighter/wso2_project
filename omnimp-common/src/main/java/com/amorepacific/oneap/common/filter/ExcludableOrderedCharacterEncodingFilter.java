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
 * Date   	          : 2020. 8. 4..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.filter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;

/**
 * <pre>
 * com.amorepacific.oneap.common.filter 
 *    |_ ExcludableOrderedCharacterEncodingFilter.java
 *    
 *    특정 URL의 request charset을 변경
 *    기술한 패턴이외 URL의 charset은 spring boot autoconfigure 설정을 따름
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 4.
 * @version : 1.0
 * @author : takkies
 */
public class ExcludableOrderedCharacterEncodingFilter extends OrderedCharacterEncodingFilter {

	private String charset;
	private Set<String> excludeEncodingPathSet;

	/**
	 * @param charset 입력한 excludepath에 해당하는 path에 대한 요청은 해당 charset으로 변환된다. 나머지는 Spring Boot Autoconfigure 설정을 따른다.
	 * @throws UnsupportedEncodingException
	 */
	public ExcludableOrderedCharacterEncodingFilter(final String charset) throws UnsupportedEncodingException {
		if (!Charset.isSupported(charset)) {
			throw new UnsupportedEncodingException(charset);
		}
		this.charset = charset;
		excludeEncodingPathSet = new HashSet<>();
	}

	@Override
	protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
		final String servletPath = request.getServletPath();

		if (excludeEncodingPathSet.contains(servletPath)) {

			try {
				// log.debug("*** set encoding( {} ): {}", this.charset, servletPath);
				request.setCharacterEncoding(this.charset);
			} catch (UnsupportedEncodingException uee) {
				// 추가 해당 오류건 발생시 false처리
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public void addExcludePath(String path) {
		this.excludeEncodingPathSet.add(path);
	}

}
