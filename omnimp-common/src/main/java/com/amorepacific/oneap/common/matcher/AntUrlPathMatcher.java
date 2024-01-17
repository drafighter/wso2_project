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

import org.springframework.util.AntPathMatcher;

/**
 * <pre>
 * com.amorepacific.oneap.common.matcher 
 *    |_ AntUrlPathMatcher.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 22.
 * @version : 1.0
 * @author : takkies
 */
public class AntUrlPathMatcher implements UrlPathMatcher {

	private static final String MATCH_ALL = "/**";

	private String pattern;

	private Matcher matcher;

	private String[] httpMethods;

	private boolean caseSensitive;

	public AntUrlPathMatcher(String pattern) {
		this(pattern, "*", false);
	}

	public AntUrlPathMatcher(String pattern, String... httpMethods) {
		this(pattern, httpMethods, false);
	}

	public AntUrlPathMatcher(String pattern, String httpMethod, boolean caseInsensitive) {
		this(pattern, new String[] { httpMethod }, caseInsensitive);
	}

	public AntUrlPathMatcher(String pattern, String[] httpMethods, boolean caseInsensitive) {
		if (pattern.equals(MATCH_ALL) || pattern.equals("**")) {
			pattern = MATCH_ALL;
			this.matcher = null;
		} else {
			this.caseSensitive = !caseInsensitive;
			if (!this.caseSensitive) {
				pattern = pattern.toLowerCase();
			}
			if (pattern.endsWith(MATCH_ALL) && pattern.indexOf('?') == -1 && pattern.indexOf("*") == pattern.length() - 2) {
				this.matcher = new SubpathMatcher(pattern.substring(0, pattern.length() - 3));
			} else {
				this.matcher = new SpringAntMatcher(pattern);
			}
		}

		this.pattern = pattern;
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

		if (this.pattern.equals(MATCH_ALL)) {
			return true;
		}

		if (!this.caseSensitive) {
			return this.matcher.matches(checkUrl.toLowerCase());
		}

		return this.matcher.matches(checkUrl);
	}

	private static interface Matcher {
		boolean matches(String checkUrl);
	}

	private static class SpringAntMatcher implements Matcher {
		private static final AntPathMatcher antMatcher = new AntPathMatcher();

		private final String pattern;

		private SpringAntMatcher(String pattern) {
			this.pattern = pattern;
		}

		public boolean matches(String path) {
			return antMatcher.match(this.pattern, path);
		}
	}

	private static class SubpathMatcher implements Matcher {
		private final String subpath;
		private final int length;
		private SubpathMatcher(String subpath) {
			assert !subpath.contains("*");
			this.subpath = subpath;
			this.length = subpath.length();
		}
		public boolean matches(String path) {
			return (path.startsWith(this.subpath) && (path.length() == this.length || path.charAt(this.length) == '/'));
		}
	}
	
//	public static void main(String args[]) {
//		boolean match = new AntUrlPathMatcher("/sample/**").matches("/sample/Test.jsp", null);
//		System.out.println(match);
//	}
}
