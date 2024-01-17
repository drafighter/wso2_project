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
package com.amorepacific.oneap.api.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

/**
 * <pre>
 * com.amorepacific.oneap.api.config 
 *    |_ WebClientConfiguration.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 13.
 * @version : 1.0
 * @author : takkies
 */

@Slf4j
@Configuration
public class WebClientConfiguration {

	@Autowired
	private WebClientConfig webClientConfig;

	@Bean
	public TcpClient tcpClient() {
		return TcpClient.create() //
		.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.webClientConfig.getConnectionTimeout()) //
		.doOnConnected(connection -> {
			connection.addHandlerLast(new ReadTimeoutHandler(this.webClientConfig.getConnectionReadTimeout(), TimeUnit.MILLISECONDS));
			connection.addHandlerLast(new WriteTimeoutHandler(this.webClientConfig.getConnectionWriteTimeout(), TimeUnit.MILLISECONDS));
		});
	}
	
	@Bean
	public WebClientCustomizer webClient() {
		final String baseUrl = this.webClientConfig.getScheme() //
				.concat(this.webClientConfig.getHost()).concat(":") //
				.concat(Integer.toString(this.webClientConfig.getPort()));
		return new WebClientCustomizer() {
			@Override
			public void customize(Builder webClientBuilder) {
				webClientBuilder.baseUrl(baseUrl) //
				.clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient()))) //
				.filter(logRequest()) //
				.filter(logResponse()); //
				// .build();
			}
		};
	}
	
	private ExchangeFilterFunction logRequest() {
	    return (clientRequest, next) -> {
	        log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
	        clientRequest.headers()
	                .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
	        return next.exchange(clientRequest);
	    };
	}

	private ExchangeFilterFunction logResponse() {
	    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
	        log.info("Response: {}", clientResponse.headers().asHttpHeaders().get("property-header"));
	        return Mono.just(clientResponse);
	    });
	}
	
}
