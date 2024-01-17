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
 * Author	          : judahye
 * Date   	          : 2023. 3. 15..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import springfox.documentation.oas.web.OpenApiTransformationContext;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spi.DocumentationType;

/**
 * <pre>
 * com.amorepacific.oneap.api.config 
 *    |_ Workaround.java
 * </pre>
 *
 * @desc    : Swagger UI Servers list
 * @date    : 2023. 3. 15.
 * @version : 1.0
 * @author  : judahye
 */
@Component
public class Workaround implements WebMvcOpenApiTransformationFilter{
		
		@Value("${omni.api.domain}")
		private String omniApiDomain;
	
		@Value("${server.port}")
		private int serverPort;
	
		@Value("${spring.profiles.active}")
		private String profile;
		@Override
		public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
			OpenAPI openApi = context.getSpecification();

	        Server testServer = new Server();
	        testServer.setDescription(profile);
	        testServer.setUrl(profile.equals("local")?"http://"+getSwaggerHost():"https://"+getSwaggerHost());
	        openApi.setServers(Arrays.asList(testServer));
	        return openApi;
		}

		@Override
	    public boolean supports(DocumentationType documentationType) {
	        return documentationType.equals(DocumentationType.OAS_30);
	    }
		

		private String getSwaggerHost() {
			String swaggerHsot = "";
			try {
				URL url = new URL(omniApiDomain);
				String host = url.getHost();
				String port = url.getPort() == -1 ? "" : ":" + url.getPort();
				swaggerHsot = "local".equals(profile) ? "localhost:" + serverPort : host + port;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return swaggerHsot;
		}
}
