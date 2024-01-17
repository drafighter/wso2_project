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
package com.amorepacific.oneap.admin.config;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.amorepacific.oneap.admin.interceptor.RestTemplateInterceptor;
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
		return HttpClientBuilder.create() //
				.setConnectionManager(poolingHttpClientConnectionManager()) //
				.setDefaultRequestConfig(requestConfig()) //
				.build();
	}

	@Bean
	public RestTemplateCustomizer restTemplate() {
		return new RestTemplateCustomizer() {
			@Override
			public void customize(RestTemplate restTemplate) {
				HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
				requestFactory.setHttpClient(httpClient());
				// restTemplate.setRequestFactory(requestFactory);
				restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(requestFactory));
				restTemplate.getInterceptors().add(new RestTemplateInterceptor());
			}
		};
	}
	
//	@Bean
//	public RestTemplate restTemplate1() {
//		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//		requestFactory.setHttpClient(httpClient());
//		return new RestTemplate(requestFactory);
//	}
}
