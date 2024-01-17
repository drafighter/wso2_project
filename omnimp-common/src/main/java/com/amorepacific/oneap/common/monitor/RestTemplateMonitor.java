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
package com.amorepacific.oneap.common.monitor;

import java.util.Set;

import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amorepacific.oneap.common.config.RestConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.util 
 *    |_ RestTemplateManager.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 13.
 * @version : 1.0
 * @author : takkies
 */

@Slf4j
@Component
public class RestTemplateMonitor {

	@Autowired
	private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

	@Autowired
	private RestConfiguration httpConfig;

	public boolean checkPending() {
		PoolStats totalStats = this.poolingHttpClientConnectionManager.getTotalStats();
		int pendingCount = totalStats.getPending();
		log.debug("▣▣▣▣▣▣ RestTemplateMonitor.pendingCount : {}", pendingCount);
		if (pendingCount > this.httpConfig.getMaxPending()) {
			log.debug("▣▣▣▣▣▣ RestTemplateMonitor.checkPending : false");
			return false;
		}
		log.debug("▣▣▣▣▣▣ RestTemplateMonitor.checkPending : true");
		return true;
	}

	public String createHttpInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("=========================").append("\n");
		sb.append("General Info:").append("\n");
		sb.append("-------------------------").append("\n");
		sb.append("MaxTotal: ").append(poolingHttpClientConnectionManager.getMaxTotal()).append("\n");
		sb.append("DefaultMaxPerRoute: ").append(poolingHttpClientConnectionManager.getDefaultMaxPerRoute()).append("\n");
		sb.append("ValidateAfterInactivity: ").append(poolingHttpClientConnectionManager.getValidateAfterInactivity()).append("\n");
		sb.append("=========================").append("\n");

		PoolStats totalStats = poolingHttpClientConnectionManager.getTotalStats();
		sb.append(createPoolStatsInfo("Total Stats", totalStats));

		Set<HttpRoute> routes = poolingHttpClientConnectionManager.getRoutes();

		if (routes != null) {
			for (HttpRoute route : routes) {
				sb.append(createRouteInfo(poolingHttpClientConnectionManager, route));
			}
		}

		return sb.toString();
	}

	private String createRouteInfo(PoolingHttpClientConnectionManager connectionManager, HttpRoute route) {
		PoolStats routeStats = connectionManager.getStats(route);
		return createPoolStatsInfo(route.getTargetHost().toURI(), routeStats);
	}

	private String createPoolStatsInfo(String title, PoolStats poolStats) {
		StringBuilder sb = new StringBuilder();
		sb.append(title + ":").append("\n");
		sb.append("-------------------------").append("\n");
		if (poolStats != null) {
			sb.append("Available: ").append(poolStats.getAvailable()).append("\n");
			sb.append("Leased: ").append(poolStats.getLeased()).append("\n");
			sb.append("Max: ").append(poolStats.getMax()).append("\n");
			sb.append("Pending: ").append(poolStats.getPending()).append("\n");
		}

		sb.append("=========================").append("\n");
		return sb.toString();
	}
	
}
