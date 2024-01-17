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
package com.amorepacific.oneap.api.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestExecution;
import org.springframework.http.client.AsyncClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.util.UriComponentsBuilder;

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
@SuppressWarnings("deprecation")
@Slf4j
public class AsyncRestTemplateInterceptor implements AsyncClientHttpRequestInterceptor {

	private static String LINE_SEPERATOR = System.getProperty("line.separator");

	@Override
	public ListenableFuture<ClientHttpResponse> intercept(HttpRequest request, byte[] body, AsyncClientHttpRequestExecution execution) throws IOException {
		traceRequest(request, body);
		ListenableFuture<ClientHttpResponse> response = null;
		try {
			response = execution.executeAsync(request, body);
			
			/*
			 * response.addCallback(result -> { // log.debug("패스워드 변경 비동기 처리 - getStatusCode : {}\ngetBody : {}", result.getStatusCode(),
			 * result.getBody());
			 * 
			 * try { StringBuilder inputStringBuilder = new StringBuilder(); BufferedReader bufferedReader = new BufferedReader(new
			 * InputStreamReader(result.getBody(), "UTF-8")); String line = bufferedReader.readLine(); while (line != null) {
			 * inputStringBuilder.append(line); inputStringBuilder.append('\n'); line = bufferedReader.readLine(); }
			 * 
			 * String responseBody = inputStringBuilder.toString(); if (responseBody.contains("<html ") && responseBody.contains("<head>") &&
			 * responseBody.contains("<title>")) { responseBody = "SKIP HTML RESPONSE..."; } final StringBuilder trace = new
			 * StringBuilder(LINE_SEPERATOR); trace.append(StringUtils.center("  TRACE RESPONSE  ", 120, "▦")).append(LINE_SEPERATOR);
			 * trace.append("┌").append(StringUtils.rightPad("", 120, "-")).append(LINE_SEPERATOR);
			 * trace.append("│").append(StringUtils.rightPad(" Status code   : {}", 120, " ")).append(LINE_SEPERATOR);
			 * trace.append("│").append(StringUtils.rightPad(" Status text   : {}", 120, " ")).append(LINE_SEPERATOR);
			 * trace.append("│").append(StringUtils.rightPad(" Headers       : {}", 120, " ")).append(LINE_SEPERATOR);
			 * trace.append("│").append(StringUtils.rightPad(" Response body : {}", 120, " ")).append(LINE_SEPERATOR);
			 * trace.append("└").append(StringUtils.rightPad("", 120, "-"));
			 * 
			 * log.info(trace.toString(), new Object[] { // result.getStatusCode(), // result.getStatusText(), // result.getHeaders(), //
			 * StringUtil.replaceLast(responseBody, "\n", "") // }); } catch (Exception e) { e.printStackTrace(); } }, ex ->
			 * log.error("패스워드 비동기 처리 에러 - Message : {}", ex.getMessage()));
			 */

		} catch (Exception e) {
			log.debug(e.toString());
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
		
		// URI에 password가 포함되어 있을 경우 *(asterisk)로 변환
		String uri = (request.getURI() != null) ? request.getURI().toString() : "";
		MultiValueMap<String, String> parameters =
	            UriComponentsBuilder.fromUriString(uri).build().getQueryParams();
		
		if(parameters.get("password") != null && StringUtils.isNotEmpty(parameters.get("password").get(0))) {
			String originParam = "password=".concat(parameters.get("password").get(0));
			String encodeParam = "";
			for(int i=0;i<parameters.get("password").get(0).length();i++) {
				encodeParam += "*";
			}
			
			uri = uri.replace(originParam, "password=".concat(encodeParam));
		}

		log.info(trace.toString(), new Object[] { //
				uri, //
				request.getMethod(), //
				request.getHeaders(), //
				new String(body, "UTF-8") //
		});
	}
}
