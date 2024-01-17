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
 * Author	          : hjw0228
 * Date   	          : 2020. 10. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * <pre>
 * com.amorepacific.oneap.api.config 
 *    |_ DataSourceHikariProperties.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 10. 21.
 * @version : 1.0
 * @author  : hjw0228
 */

@Configuration
@ConfigurationProperties(prefix="spring.datasource.hikari")
@Data
public class DataSourceHikariProperties {
	private String minimumIdle;
	private String maximumPoolSize;
	private String maxLifetime;
	private String idleTimeout;
	private String connectionTimeout;
	private String connectionTestQuery;
	private String validationTimeout;
}
