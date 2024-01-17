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
 * Date   	          : 2020. 7. 13..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.config;

import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.amorepacific.oneap.auth.interceptor.RestTemplateInterceptor;
import com.amorepacific.oneap.common.config.RestConfiguration;

/**
 * <pre>
 * com.amorepacific.oneap.api.config 
 *    |_ RestTemplateConfiguration.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 13.
 * @version : 1.0
 * @author : takkies
 */
@Configuration
public class RestTemplateConfiguration {

	@Value("${logging.resttemplate}")
	private boolean isLoggingRestTemplate;
	
	@Autowired
	private RestConfiguration httpConfig;

	@Bean
	public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
		PoolingHttpClientConnectionManager result = new PoolingHttpClientConnectionManager();
		result.setMaxTotal(this.httpConfig.getMaxTotal());
		result.setDefaultMaxPerRoute(this.httpConfig.getDefaultMaxPerRoute()); // Default max per route is used in case it's not set for a specific route
		// and / or
		if (!CollectionUtils.isEmpty(this.httpConfig.getMaxPerRoutes())) {
			for (RestConfiguration.RestRoute httpHostConfig : this.httpConfig.getMaxPerRoutes()) {
				HttpHost host = new HttpHost(httpHostConfig.getHost(), httpHostConfig.getPort(), httpHostConfig.getScheme());
				result.setMaxPerRoute(new HttpRoute(host), httpHostConfig.getMaxPerRoute()); // Max per route for a specific host route
			}
		}
		return result;
	}

	@Bean
	public RequestConfig requestConfig() {
		return RequestConfig.custom() //
				.setConnectionRequestTimeout(this.httpConfig.getConnectionRequestTimeout()) //
				.setConnectTimeout(this.httpConfig.getConnectionTimeout()) //
				.setSocketTimeout(this.httpConfig.getSocketTimeout()) //
				.build();
	}

	@Bean
	public CloseableHttpClient httpClient() {
		// SSL ignore 처리
		// JksTrustStoreLoaderListener 와 충돌나므로 확인 필요
		try {
			TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					return true;
				}
			};
			SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
			SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

			return HttpClientBuilder.create() //
					.setSSLSocketFactory(sslSocketFactory) //
					.setConnectionManager(poolingHttpClientConnectionManager()) 
					.setDefaultRequestConfig(requestConfig()) //
					.setRedirectStrategy(new LaxRedirectStrategy()) // 요청된 결과가 302인 경우 해당 페이지로 리다이렉트 된 결과를 반환
					.build();
		} catch (Exception e) {
			// NO PMD
		}
		return null;
	}

	@Bean
	public RestTemplateCustomizer restTemplateCustomizer() {
		boolean isLoggingRestTemplate = this.isLoggingRestTemplate;
		return new RestTemplateCustomizer() {
			@Override
			public void customize(RestTemplate restTemplate) {
				
				List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
				StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
				stringConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
				messageConverters.add(stringConverter);  
				
				//Add the Jackson Message converter
				MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

				// Note: here we are making this converter to process any kind of response, 
				// not only application/*json, which is the default behaviour
				converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
				messageConverters.add(converter);  
				
				HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
				requestFactory.setHttpClient(httpClient());
				restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(requestFactory));

				
				restTemplate.setMessageConverters(messageConverters);
				if (isLoggingRestTemplate) {
					restTemplate.getInterceptors().add(new RestTemplateInterceptor());	
				}
				
			}
		};
	}

	@Bean
	public RestTemplateBuilder restTemplateBuilder() {
		return new RestTemplateBuilder(restTemplateCustomizer());
	}

}
