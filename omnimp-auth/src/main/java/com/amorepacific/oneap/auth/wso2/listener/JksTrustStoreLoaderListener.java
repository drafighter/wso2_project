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
 * Date   	          : 2020. 7. 23..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.wso2.listener;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.wso2.listener 
 *    |_ JksTrustStoreLoaderListener.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 23.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
public class JksTrustStoreLoaderListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		Resource rs = new ClassPathResource("oneap-jks.properties");
		if (rs.isFile() && rs.isReadable()) {
			try {
				FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class) //
						.configure(new Parameters().fileBased().setURL(rs.getURL()));

				Configuration config = builder.getConfiguration();

				if (!config.isEmpty()) {
					log.debug("*** jks keystore path : {}", rs.getURL().getPath());
					log.debug("*** jks keystore name : {}", config.getString("keystorename"));
					System.setProperty("javax.net.ssl.trustStore", rs.getURL().getPath());
					System.setProperty("javax.net.ssl.trustStorePassword", config.getString("keystorepassword"));
				}

			} catch (IOException | ConfigurationException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

}
