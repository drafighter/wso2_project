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
 * Date   	          : 2020. 11. 2..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <pre>
 * com.amorepacific.oneap.auth.config 
 *    |_ RedisSessionConfiguration.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 11. 2.
 * @version : 1.0
 * @author : takkies
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 60) // 세션만료시간(초)
public class RedisHttpSessionConfiguration extends AbstractHttpSessionApplicationInitializer {

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private Integer port;

	@Value("${spring.redis.password}")
	private String password;

	@Autowired
	private ObjectMapper mapper;

	@Profile({"dev", "stg", "prod"})
	@Bean
	public ConfigureRedisAction configureRedisAction() {
	    return ConfigureRedisAction.NO_OP;
	}
	
	@Bean
	public RedisConnectionFactory lettuceConnectionFactory() {
		RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(host, port);
		standaloneConfiguration.setPassword(password.isEmpty() ? RedisPassword.none() : RedisPassword.of(password));
		return new LettuceConnectionFactory(standaloneConfiguration);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(lettuceConnectionFactory());
		redisTemplate.setEnableTransactionSupport(true);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

}
