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
package com.amorepacific.oneap.api.health.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amorepacific.oneap.api.health.mapper.HealthMapper;
import com.amorepacific.oneap.api.health.service.HealthService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.api.health.service 
 *    |_ HealthService.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 10. 21.
 * @version : 1.0
 * @author  : hjw0228
 */

@Slf4j
@Service
public class HealthService {

	@Autowired
	HealthMapper healthMapper;
	
	public JsonObject healthCheck() {
		log.debug("Omnimp-api Health Check");
		
		JsonObject obj = new JsonObject();
		obj.addProperty("key", "omnimp-api");
		obj.addProperty("value", healthMapper.validationQuery());
		
		return obj;
	}
}
