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
 * Date   	          : 2020. 7. 22..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.matcher;

import java.util.regex.Pattern;

/**
 * <pre>
 * com.amorepacific.oneap.common.matcher 
 *    |_ RegexUrlPathMatcher.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 22.
 * @version : 1.0
 * @author : takkies
 */

public class RegexUrlPathMatcher implements UrlPathMatcher {

	private Pattern pattern;

	private String[] httpMethods;

	public RegexUrlPathMatcher(String pattern) {
		this(pattern, "*", false);
	}

	public RegexUrlPathMatcher(String pattern, String... httpMethods) {
		this(pattern, httpMethods, false);
	}

	public RegexUrlPathMatcher(String pattern, String httpMethod, boolean caseInsensitive) {
		this(pattern, new String[] { httpMethod }, caseInsensitive);
	}

	public RegexUrlPathMatcher(String pattern, String[] httpMethods, boolean caseInsensitive) {
		if (caseInsensitive) {
			this.pattern = Pattern.compile(pattern, 2);
		} else {

			this.pattern = Pattern.compile(pattern);
		}
		(new String[1])[0] = "*";
		this.httpMethods = (httpMethods == null || httpMethods.length == 0) ? new String[1] : httpMethods;
	}

	@Override
	public boolean matches(String checkUrl, String checkHttpMethod) {
		if (checkHttpMethod != null) {
			boolean httpMethodMatched = false;
			for (String hm : this.httpMethods) {
				if ("*".equalsIgnoreCase(hm)) {
					httpMethodMatched = true;
					break;
				}
				if (hm.equalsIgnoreCase(checkHttpMethod)) {
					httpMethodMatched = true;
					break;
				}
			}
			if (!httpMethodMatched) {
				return false;
			}
		}
		return this.pattern.matcher(checkUrl).matches();
	}

	
//	public static void main(String args[]) {
//		boolean match = new RegexUrlPathMatcher("(/sample/).*").matches("/sample/test.jsp", "get");
//		System.out.println(match);
//	}
}
