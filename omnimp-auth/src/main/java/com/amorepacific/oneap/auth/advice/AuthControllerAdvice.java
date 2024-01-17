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
 * Date   	          : 2020. 8. 14..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.advice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.amorepacific.oneap.auth.common.CommonAuth;
import com.amorepacific.oneap.common.exception.BusinessException;
import com.amorepacific.oneap.common.exception.OmniException;
import com.amorepacific.oneap.common.exception.SystemException;
import com.amorepacific.oneap.common.vo.ErrorParams;

/**
 * <pre>
 * com.amorepacific.oneap.auth.advice 
 *    |_ AuthControllerAdvice.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 14.
 * @version : 1.0
 * @author : takkies
 */
@Controller
@ControllerAdvice("com.amorepacific.oneap.auth")
public class AuthControllerAdvice {

	@Autowired
	private CommonAuth commonAuth;
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ BusinessException.class, SystemException.class, OmniException.class })
	public String handle(final Exception e, final HttpServletRequest request, final Model model) {

		ErrorParams errorParams = this.commonAuth.getErrorParams();
		if (errorParams != null) {
			model.addAttribute("chCd", errorParams.getChCd());
			model.addAttribute("home", errorParams.getHome());
			model.addAttribute("homeurl", errorParams.getHomeurl());
			model.addAttribute("chnCd", errorParams.getChnCd());
			model.addAttribute("storeCd", errorParams.getStoreCd());
			model.addAttribute("storenm", errorParams.getStorenm());
			model.addAttribute("user_id", errorParams.getUser_id());
			model.addAttribute("channelName", errorParams.getChannelName());
		}
		if (e instanceof SystemException) {
			model.addAttribute("message", "시스템 오류가 발생하였습니다. 관리자에게 문의하시기 바랍니다.");
		} else if (e instanceof BusinessException) {
			String rscd = ((BusinessException) e).getResultCode();
			if (rscd.contains("CH_CD_ERROR")) {
				model.addAttribute("message", "경로코드를 확인하시기 바랍니다.");
			} else {
				model.addAttribute("message", "관리자에게 문의하시기 바랍니다.");
			}
		} else if (e instanceof OmniException) {
			model.addAttribute("message", e.getMessage());
		}
		return "info/error_page";
	}

}
