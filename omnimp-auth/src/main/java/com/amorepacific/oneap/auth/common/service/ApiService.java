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
package com.amorepacific.oneap.auth.common.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.common.service 
 *    |_ ApiService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 24.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class ApiService {

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	//@Autowired
	//private RestTemplateMonitor restTemplateMonitor;

	@SuppressWarnings("unchecked")
	public <T> ResponseEntity<T> get(String url, HttpHeaders httpHeaders) {
		return get(url, httpHeaders, (Class<T>) Object.class, null);
	}

	public <T> ResponseEntity<T> get(String url, HttpHeaders httpHeaders, Class<T> clazz) {
		return get(url, httpHeaders, clazz, null);
	}

	public <T> ResponseEntity<T> get(String url, HttpHeaders httpHeaders, Class<T> clazz, Map<String, String> params) {
		try {
			return callApiEndpoint(url, HttpMethod.GET, httpHeaders, null, clazz, params);
		} catch (URISyntaxException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> ResponseEntity<T> post(String url, HttpHeaders httpHeaders, Object body) {
		return post(url, httpHeaders, body, (Class<T>) Object.class, null);
	}

	public <T> ResponseEntity<T> post(String url, HttpHeaders httpHeaders, Object body, Class<T> clazz) {
		return post(url, httpHeaders, body, clazz, null);
	}

	public <T> ResponseEntity<T> post(String url, HttpHeaders httpHeaders, Object body, Class<T> clazz, Map<String, String> params) {
		try {
			return callApiEndpoint(url, HttpMethod.POST, httpHeaders, body, clazz, params);
		} catch (URISyntaxException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public <T> ResponseEntity<T> put(String url, HttpHeaders httpHeaders, Object body, Class<T> clazz) {
		return put(url, httpHeaders, body, clazz, null);
	}

	public <T> ResponseEntity<T> put(String url, HttpHeaders httpHeaders, Object body, Class<T> clazz, Map<String, String> params) {
		try {
			return callApiEndpoint(url, HttpMethod.PUT, httpHeaders, body, clazz, params);
		} catch (URISyntaxException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public <T> ResponseEntity<T> patch(String url, HttpHeaders httpHeaders, Object body, Class<T> clazz) {
		return patch(url, httpHeaders, body, clazz, null);
	}

	public <T> ResponseEntity<T> patch(String url, HttpHeaders httpHeaders, Object body, Class<T> clazz, Map<String, String> params) {
		try {
			return callApiEndpoint(url, HttpMethod.PATCH, httpHeaders, body, clazz, params);
		} catch (URISyntaxException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public <T> ResponseEntity<T> delete(String url, HttpHeaders httpHeaders, Object body, Class<T> clazz) {
		return delete(url, httpHeaders, body, clazz, null);
	}

	public <T> ResponseEntity<T> delete(String url, HttpHeaders httpHeaders, Object body, Class<T> clazz, Map<String, String> params) {
		try {
			return callApiEndpoint(url, HttpMethod.DELETE, httpHeaders, body, clazz, params);
		} catch (URISyntaxException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	private <T> ResponseEntity<T> callApiEndpoint(final String url, HttpMethod httpMethod, HttpHeaders httpHeaders, Object body, Class<T> clazz, Map<String, String> params) throws URISyntaxException {
		String restUrl = url;
		if (httpHeaders != null && httpHeaders.getContentType() != null) {
			if (httpHeaders.getContentType().equals(MediaType.APPLICATION_FORM_URLENCODED)) {
				// log.debug("▣▣▣▣▣▣ url : {}, content type : {}", url, httpHeaders.getContentType());
				if (body != null && body instanceof MultiValueMap) {
					@SuppressWarnings("unchecked")
					MultiValueMap<String, String> parameters = (MultiValueMap<String, String>) body;
					UriComponentsBuilder componentBuilder = UriComponentsBuilder.newInstance().uri(new URI(restUrl));
					componentBuilder.path("").queryParams(parameters);
					UriComponents uriComponents = componentBuilder.build();
					restUrl = uriComponents.toUriString();
				} else {
					if (params != null && !params.isEmpty()) {
						MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
						for (String key : params.keySet()) {
							// log.debug(String.format("▣▣▣▣▣▣ 키 : %s, 값 : %s", key, params.get(key)));
							parameters.add(key, params.get(key) == null ? "" : params.get(key));
						}
						UriComponentsBuilder componentBuilder = UriComponentsBuilder.newInstance().uri(new URI(restUrl));
						componentBuilder.path("").queryParams(parameters);
						UriComponents uriComponents = componentBuilder.build();
						restUrl = uriComponents.toUriString();
					} else {
						restUrl = url;
					}
				}
			} else {
				restUrl = url;
			}
		}
		// log.debug("▣▣▣▣▣▣ rest url : {}", restUrl);
		// log.debug("▣▣▣▣▣▣ rest info\n{}", this.restTemplateMonitor.createHttpInfo());
		try {
			GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
			
			//final List<MediaType> supportedMediaTypes = new LinkedList<MediaType>(converter.getSupportedMediaTypes());
			//final MediaType utf8MediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			//final MediaType bpMediaType = new MediaType("application", "octet-stream", StandardCharsets.UTF_8);
			//final MediaType euckrMediaType = new MediaType("text", "html", Charset.forName("EUC-KR"));
			//final MediaType iso88591MediaType = new MediaType("text", "html", Charset.forName("ISO-8859-1"));
			//supportedMediaTypes.add(utf8MediaType);
			//supportedMediaTypes.add(bpMediaType);
			//supportedMediaTypes.add(euckrMediaType);
			//supportedMediaTypes.add(iso88591MediaType);
			// converter.setSupportedMediaTypes(supportedMediaTypes);
			converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
			
			//restTemplate.getMessageConverters().add(converter);
			
			//return restTemplate.exchange(restUrl, httpMethod, new HttpEntity<>(body, httpHeaders), clazz);
			
			// return restTemplateBuilder.getMessageConverters(converter).build().exchange(restUrl, httpMethod, new HttpEntity<>(body, httpHeaders), clazz);
			return restTemplateBuilder.additionalMessageConverters(converter).build().exchange(restUrl, httpMethod, new HttpEntity<>(body, httpHeaders), clazz);
			// return this.restTemplate.exchange(restUrl, httpMethod, new HttpEntity<>(body, httpHeaders), clazz);
		} catch (RestClientException e) {
			// log.debug("▣▣▣▣▣▣ rest exception info\n{}", this.restTemplateMonitor.createHttpInfo());
			// throw new RestClientException(e.getMessage());
			log.error("▣▣▣▣▣▣ rest exception info\n{}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
