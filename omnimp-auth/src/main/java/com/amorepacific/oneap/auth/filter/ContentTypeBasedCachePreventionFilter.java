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
package com.amorepacific.oneap.auth.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.filter 
 *    |_ CachePreventionFilter.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 9. 18.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
public class ContentTypeBasedCachePreventionFilter implements Filter {

	// Headers to be sent in the response
	private static final String HEADER_NAME_CACHE_CONTROL = "Cache-Control";
	private static final String HEADER_VALUE_CACHE_CONTROL = "no-store, no-cache, must-revalidate, private";
	private static final String HEADER_NAME_EXPIRES = "Expires";
	private static final String HEADER_VALUE_EXPIRES = "0";
	private static final String HEADER_NAME_PRAGMA = "Pragma";
	private static final String HEADER_VALUE_PRAGMA = "no-cache";

	// Configurations in the web.xml
	private static final String PARAM_NAME_HTTP_HEADERS = "httpHeaders";
	private static final String PARAM_NAME_PATTERNS = "patterns";
	private static final String PARAM_NAME_FILTER_ACTION = "filterAction";
	private static final String PARAM_VALUE_PATTERNS_ACTION_SKIP = "skip";
	private static final String PARAM_VALUE_PATTERNS_ACTION_ENFORCE = "enforce";

	// Delimiters in the web.xml
	private static final String DELIMITER_MULTI_VALUE = "\"\\s*,";
	private static final String DELIMITER_HEADER_NAME_VALUE = ":";

	private String patternAction;
	private ArrayList<Pattern> patternsList = new ArrayList<Pattern>();
	private Map<String, String> headersMap = new HashMap<String, String>();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		initHeaders(filterConfig);
		initPatternsAndAction(filterConfig);
	}

	/**
	 * This method will check for any headers defined in the web.xml. If any headers are found in the web.xml, those headers with the
	 * corresponding values will be set to prevent caching. If any headers aren't found in the web.xml, the default headers with values will be
	 * set for cache prevention.
	 *
	 * @param filterConfig Configurations specific to this filter in the web.xml
	 */
	protected void initHeaders(FilterConfig filterConfig) throws ServletException {

		String httpHeadersParam = filterConfig.getInitParameter(PARAM_NAME_HTTP_HEADERS);

		if (StringUtils.hasText(httpHeadersParam)) {
			String[] httpHeadersArray = httpHeadersParam.split(DELIMITER_MULTI_VALUE);

			for (String header : httpHeadersArray) {
				header = header.trim();
				header = header.replaceAll("\"", "");

				if (StringUtils.hasText(header)) {
					String[] entry = header.split(DELIMITER_HEADER_NAME_VALUE);

					if (entry.length == 2) {
						String headerName = entry[0].trim();
						String headerValue = entry[1].trim();
						headersMap.put(headerName, headerValue);
					} else {
						throw new ServletException("Malformed header [" + header + "] defined in the web.xml");
					}
				}
			}
		}

		// If custom headers aren't defined. populate with default headers
		if (headersMap.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Valid custom headers are not defined. Using default cache prevention headers");
			}
			headersMap.put(HEADER_NAME_CACHE_CONTROL, HEADER_VALUE_CACHE_CONTROL);
			headersMap.put(HEADER_NAME_EXPIRES, HEADER_VALUE_EXPIRES);
			headersMap.put(HEADER_NAME_PRAGMA, HEADER_VALUE_PRAGMA);
		}
	}

	/**
	 * Reading the filtering pattern and the action to be performed against the pattern from the web.xml. The pattern is a regex that could
	 * define content types or URLs. Multiple patterns can be defined in each new line.
	 *
	 * @param filterConfig Configurations specific to this filter in the web.xml
	 */
	protected void initPatternsAndAction(FilterConfig filterConfig) {

		String patternsParam = filterConfig.getInitParameter(PARAM_NAME_PATTERNS);

		// If a filtering pattern is available split the pattern using carriage return and line feed characters and put into an array.
		if (!StringUtils.isEmpty(patternsParam)) {
			String[] patternsArray = patternsParam.split(DELIMITER_MULTI_VALUE);

			// Iterate through the filtering patterns array and put into an arrayList.
			for (String pattern : patternsArray) {
				patternsList.add(Pattern.compile(pattern.trim()));
			}
		}

		// Read the action to be performed from the web.xml.
		// The action could be either enforce or skip.
		String patternActionConfig = filterConfig.getInitParameter(PARAM_NAME_FILTER_ACTION);
		patternAction = PARAM_VALUE_PATTERNS_ACTION_ENFORCE.equals(patternActionConfig) ? PARAM_VALUE_PATTERNS_ACTION_ENFORCE : PARAM_VALUE_PATTERNS_ACTION_SKIP;
	}

	/**
	 * Checks for a match with the filtering pattern and the Content Type/ URL.
	 *
	 * @param stringToBeMatched Requested URL or the Content Type in the response
	 * @return true for either to set headers for a match, or skip setting headers to a match
	 */
	protected boolean canApplyCachePreventionHeaders(String stringToBeMatched) {

		// If no patterns are defined, headers would be applied by default.
		if (patternsList.isEmpty()) {
			return true;
		}

		boolean patternMatched = false;

		for (Pattern pattern : patternsList) {
			if (pattern.matcher(stringToBeMatched).matches()) {
				patternMatched = true;
				break;
			}
		}

		// Checking to skip or apply headers to the matching URL/Content Type.
		if (patternMatched) {
			return PARAM_VALUE_PATTERNS_ACTION_ENFORCE.equals(patternAction) ? true : false;
		}
		// Checking to skip or apply headers to URLs/Content Types that doesn't mactch.
		else {
			return PARAM_VALUE_PATTERNS_ACTION_SKIP.equals(patternAction) ? true : false;
		}
	}

	/**
	 * Setting cache prevention headers to responses.
	 *
	 * @param response HTTP Servlet Response
	 */
	protected void applyCachePreventionHeaders(HttpServletResponse response) {
		for (Map.Entry<String, String> headerEntry : headersMap.entrySet()) {
			response.setHeader(headerEntry.getKey(), headerEntry.getValue());
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, new HttpServletResponseWrapper((HttpServletResponse) response) {

			@Override
			/**
			 * {@inheritDoc}
			 */
			public void setContentType(String contentType) {
				if (canApplyCachePreventionHeaders(contentType)) {
					applyCachePreventionHeaders(this);
				}
				super.setContentType(contentType);
			}
		});

	}

	@Override
	public void destroy() {
	}

}
