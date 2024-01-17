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
package com.amorepacific.oneap.auth.common;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * <pre>
 * com.amorepacific.oneap.auth.common 
 *    |_ AuthErrorController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 23.
 * @version : 1.0
 * @author : takkies
 */

@Controller
public class AuthErrorController implements ErrorController {

	private String VIEW_PATH = "errors/";

	@GetMapping(value = "error")
	public String handleErrorGet(HttpServletRequest request, final Model model) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if (status != null) {
			HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(status.toString()));
			int statusCode = Integer.valueOf(status.toString());
			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				model.addAttribute("message", httpStatus.getReasonPhrase());
				return VIEW_PATH + "404";
			} else if (statusCode == HttpStatus.FORBIDDEN.value()) {
				model.addAttribute("message", httpStatus.getReasonPhrase());
				return VIEW_PATH + "500";
			} else if (statusCode == 9999) {
				model.addAttribute("message", httpStatus.getReasonPhrase());
				return VIEW_PATH + "oauth2_error";
			} else {
				model.addAttribute("message", httpStatus.getReasonPhrase());
				return VIEW_PATH + "500";
			}
		}
		return "error";
	}
	
	@PostMapping(value = "error")
	public String handleErrorPost(HttpServletRequest request, final Model model) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if (status != null) {
			HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(status.toString()));
			int statusCode = Integer.valueOf(status.toString());
			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				model.addAttribute("message", httpStatus.getReasonPhrase());
				return VIEW_PATH + "404";
			} else if (statusCode == HttpStatus.FORBIDDEN.value()) {
				model.addAttribute("message", httpStatus.getReasonPhrase());
				return VIEW_PATH + "500";
			} else if (statusCode == 9999) {
				model.addAttribute("message", httpStatus.getReasonPhrase());
				return VIEW_PATH + "oauth2_error";
			} else {
				model.addAttribute("message", httpStatus.getReasonPhrase());
				return VIEW_PATH + "500";
			}
		}
		return "error";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

}
