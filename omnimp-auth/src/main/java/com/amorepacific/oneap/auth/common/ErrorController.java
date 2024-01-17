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
 * Date   	          : 2020. 11. 17..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.common;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * <pre>
 * com.amorepacific.oneap.auth.common 
 *    |_ ErrorController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 11. 17.
 * @version : 1.0
 * @author  : hjw0228
 */
@Controller
public class ErrorController {
	
	private String VIEW_PATH = "errors/";
	
	@GetMapping(value = "error/404")
	public String handleError404Get(HttpServletRequest request) {
		return VIEW_PATH + "404";
	}
	
	@PostMapping(value = "error/404")
	public String handleError404Post(HttpServletRequest request) {
		return VIEW_PATH + "404";
	}
	
	@GetMapping(value = "error/500")
	public String handleError500Get(HttpServletRequest request) {
		return VIEW_PATH + "500";
	}
	
	@PostMapping(value = "error/500")
	public String handleError500Post(HttpServletRequest request) {
		return VIEW_PATH + "500";
	}
}