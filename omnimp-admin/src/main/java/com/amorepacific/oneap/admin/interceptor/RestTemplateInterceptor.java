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
 * Date   	          : 2020. 7. 14..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.admin.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.amorepacific.oneap.common.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.interceptor 
 *    |_ RestTemplateInterceptor.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 14.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

	private static String LINE_SEPERATOR = System.getProperty("line.separator");
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		traceRequest(request, body);
		ClientHttpResponse response = null;
		try {
			response = execution.execute(request, body);
		} catch (Exception e) {
			log.debug(e.toString());
		}
		if (response != null) {
			traceResponse(response);
		} else {
			throw new IOException("intercept error.");
		}
		return response;
	}

	private void traceRequest(HttpRequest request, byte[] body) throws IOException {
		StringBuilder trace = new StringBuilder(LINE_SEPERATOR);
		trace.append(StringUtils.center("  TRACE REQUEST  ", 120, "▤")).append(LINE_SEPERATOR);
		trace.append("┌").append(StringUtils.rightPad("", 120, "-")).append(LINE_SEPERATOR);
		trace.append("│").append(StringUtils.rightPad(" URI           : {}", 120, " ")).append(LINE_SEPERATOR);
		trace.append("│").append(StringUtils.rightPad(" Method        : {}", 120, " ")).append(LINE_SEPERATOR);
		trace.append("│").append(StringUtils.rightPad(" Headers       : {}", 120, " ")).append(LINE_SEPERATOR);
		trace.append("│").append(StringUtils.rightPad(" Request body  : {}", 120, " ")).append(LINE_SEPERATOR);
		trace.append("└").append(StringUtils.rightPad("", 120, "-"));

		log.debug(trace.toString(), new Object[] { //
				request.getURI(), //
				request.getMethod(), //
				request.getHeaders(), //
				new String(body, "UTF-8") //
		});
	}

	private void traceResponse(ClientHttpResponse response) throws IOException {
		StringBuilder inputStringBuilder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
		String line = bufferedReader.readLine();
		while (line != null) {
			inputStringBuilder.append(line);
			inputStringBuilder.append('\n');
			line = bufferedReader.readLine();
		}

		String body = inputStringBuilder.toString();
		if (body.contains("<html ") && body.contains("<head>") && body.contains("<title>")) {
			body = "SKIP HTML RESPONSE...";
		}
		final StringBuilder trace = new StringBuilder(LINE_SEPERATOR);
		trace.append(StringUtils.center("  TRACE RESPONSE  ", 120, "▦")).append(LINE_SEPERATOR);
		trace.append("┌").append(StringUtils.rightPad("", 120, "-")).append(LINE_SEPERATOR);
		trace.append("│").append(StringUtils.rightPad(" Status code   : {}", 120, " ")).append(LINE_SEPERATOR);
		trace.append("│").append(StringUtils.rightPad(" Status text   : {}", 120, " ")).append(LINE_SEPERATOR);
		trace.append("│").append(StringUtils.rightPad(" Headers       : {}", 120, " ")).append(LINE_SEPERATOR);
		trace.append("│").append(StringUtils.rightPad(" Response body : {}", 120, " ")).append(LINE_SEPERATOR);
		trace.append("└").append(StringUtils.rightPad("", 120, "-"));

		log.debug(trace.toString(), new Object[] { //
				response.getStatusCode(), //
				response.getStatusText(), //
				response.getHeaders(), //
				StringUtil.replaceLast(body, "\n", "") //
		});

	}
}
