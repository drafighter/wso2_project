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
package com.amorepacific.oneap.auth.interceptor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
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
@Slf4j
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

	private static String LINE_SEPERATOR = System.getProperty("line.separator");

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		final StopWatch stopwatch = new StopWatch("RestTemplateInterceptor - " + request.getURI());
		if (!stopwatch.isRunning()) {
			stopwatch.start("RestTemplateInterceptor - " + request.getURI());
		}
		
		traceRequest(request, body);
		ClientHttpResponse response = null;
		try {
			response = execution.execute(request, body);
		} catch (Exception e) {
			log.error("Rest Template Error - " + e.getMessage() + "\nRequest URI : {}\n", request.getURI(), e);
			
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			log.info("\n" + stopwatch.prettyPrint());
			response = new ClientHttpResponse() {
				
				@Override
				public HttpHeaders getHeaders() {
					// TODO Auto-generated method stub
					HttpHeaders headers = new HttpHeaders();
		            headers.setContentType(MediaType.APPLICATION_JSON);
		            return headers;
				}
				
				@Override
				public InputStream getBody() throws IOException {
					// TODO Auto-generated method stub
					return new ByteArrayInputStream(e.getMessage().getBytes());
				}
				
				@Override
				public String getStatusText() throws IOException {
					// TODO Auto-generated method stub
					return "internal_server_error";
				}
				
				@Override
				public HttpStatus getStatusCode() throws IOException {
					// TODO Auto-generated method stub
					return HttpStatus.INTERNAL_SERVER_ERROR;
				}
				
				@Override
				public int getRawStatusCode() throws IOException {
					// TODO Auto-generated method stub
					return 500;
				}
				
				@Override
				public void close() {
					// TODO Auto-generated method stub
					
				}
			};
			
			return response;
		}
		if (response != null) {
			traceResponse(response);
		} else {
			throw new IOException("rest template error.");
		}
		
		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		log.info("\n" + stopwatch.prettyPrint());
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

	@Autowired
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
		} else if (body.contains("<div ") && body.contains("agree-txt")) {
			body = "SKIP HTML RESPONSE, maybe Terms contents...";
		}
		final StringBuilder trace = new StringBuilder(LINE_SEPERATOR);
		trace.append(StringUtils.center("  TRACE RESPONSE  ", 120, "▦")).append(LINE_SEPERATOR);
		trace.append("┌").append(StringUtils.rightPad("", 120, "-")).append(LINE_SEPERATOR);
		trace.append("│").append(StringUtils.rightPad(" Status code   : {}", 120, " ")).append(LINE_SEPERATOR);
		trace.append("│").append(StringUtils.rightPad(" Status text   : {}", 120, " ")).append(LINE_SEPERATOR);
		trace.append("│").append(StringUtils.rightPad(" Headers       : {}", 120, " ")).append(LINE_SEPERATOR);
		trace.append("│").append(StringUtils.rightPad(" Response body : {}", 120, " ")).append(LINE_SEPERATOR);
		trace.append("└").append(StringUtils.rightPad("", 120, "-"));
		
		// Response Header 에서 Charset 추출 후 인코딩 수정
		if(response.getHeaders() != null && response.getHeaders().getContentType() != null && response.getHeaders().getContentType().getCharset() != null) {
			Charset charset = response.getHeaders().getContentType().getCharset();
			if(Charset.forName("EUC-KR").equals(charset)) {
				CharBuffer cbuffer = CharBuffer.wrap((new String(body.getBytes(), "EUC-KR").toCharArray()));
				Charset utf8charset = Charset.forName("UTF-8");
				ByteBuffer byteBuffer = utf8charset.encode(cbuffer);
				body = new String(byteBuffer.array());
			} else if(Charset.forName("ISO-8859-1").equals(charset)) {
				CharBuffer cbuffer = CharBuffer.wrap((new String(body.getBytes(), "ISO-8859-1").toCharArray()));
				Charset utf8charset = Charset.forName("ISO-8859-1");
				ByteBuffer byteBuffer = utf8charset.encode(cbuffer);
				body = new String(byteBuffer.array());
			}
		}

		log.info(trace.toString(), new Object[] { //
				response.getStatusCode(), //
				response.getStatusText(), //
				response.getHeaders(), //
				StringUtil.replaceLast(body, "\n", "") //
		});

	}
}
