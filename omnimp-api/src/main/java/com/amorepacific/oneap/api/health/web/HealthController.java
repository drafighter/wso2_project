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
package com.amorepacific.oneap.api.health.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.api.health.service.HealthService;

/**
 * <pre>
 * com.amorepacific.oneap.api.health.web 
 *    |_ HealthController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 10. 21.
 * @version : 1.0
 * @author  : hjw0228
 */

@RestController
public class HealthController {

	@Autowired
	HealthService healthService;
	
	@ResponseBody
	@GetMapping("/health")
	public ResponseEntity<String> healthCheck() {
		return new ResponseEntity<>(healthService.healthCheck().toString(), HttpStatus.OK);
	}
}
