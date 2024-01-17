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
 * Author	          : hkdang
 * Date   	          : 2020. 9. 18..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.social.web;

//import java.util.Map;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;

//import com.amorepacific.oneap.auth.api.service.CustomerApiService;
//import com.amorepacific.oneap.auth.social.handler.SnsAuth;
//import com.amorepacific.oneap.common.util.WebUtil;
//import com.amorepacific.oneap.common.vo.sns.SnsTokenResponse;
//import com.amorepacific.oneap.common.vo.sns.SnsTokenVo;
//import com.amorepacific.oneap.common.vo.sns.SnsType;

//import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.social.web 
 *    |_ SampleMappingPage.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 9. 18.
 * @version : 1.0
 * @author  : hkdang
 */

@Controller
//@Slf4j
public class SampleMappingPage {

	//@Autowired
	//private CustomerApiService customerApiService;
	
	//@Autowired
	//private SnsAuth snsAuth;
	
	/**
	 * 
	 * <pre>
	 * comment  : SNS 매핑 테스트 
	 * author   : hkdang
	 * date     : 2020. 11. 10. 오후 4:29:58
	 * </pre>
	 * @param request
	 * @param model
	 * @return
	 */
	/*
	@GetMapping("/sample/sns_mapping")
	public String sampleSnsMapping(HttpServletRequest request, final Model model) {
		
		String snsType = request.getParameter("snsType");
		String resultCode = request.getParameter("resultCode");
		
		log.debug("▶▶▶▶▶▶ [sampleSnsMapping] snsType = {}, resultCode = {} ", snsType, resultCode);
		
		if(StringUtils.hasText(snsType)) {
			model.addAttribute("snsType", snsType);
		}
		
		if(StringUtils.hasText(resultCode)) {
			model.addAttribute("resultCode", resultCode);
		}
		
		return "sample/sns_mapping";
	}
	*/
	
	/**
	 * 
	 * <pre>
	 * comment  : 카카오 로그인 테스트
	 * author   : hkdang
	 * date     : 2020. 11. 10. 오후 4:29:45
	 * </pre>
	 * @param request
	 * @param response
	 * @param model
	 * @param param
	 * @return
	 */
	/*
	@GetMapping("/sample/sns_kakao") 
	public String sampleSnsKakao(HttpServletRequest request, HttpServletResponse response, final Model model, @RequestParam Map<String, String> param) {
		
		model.addAttribute("sdkKey", snsAuth.getKey(SnsType.KAKAO.getType(), "sdkkey"));
		model.addAttribute("callback", snsAuth.getKey(SnsType.KAKAO.getType(), "callback"));
		
		if(param.size() > 0) {
			String error = param.get("error");
			if(StringUtils.hasText(error)) {
				model.addAttribute("error", error);
			} else {
				SnsTokenVo snsTokenVo = new SnsTokenVo();
				snsTokenVo.setCode(param.get("code"));
				
				SnsTokenResponse snsTokenResponse = this.customerApiService.getSnsToken("KA", snsTokenVo);
				String token = snsTokenResponse.getAccessToken();
				
				WebUtil.setCookies(response, "authorize-access-token", token);
			}
		}
		
		return "sample/sns_kakao";
	}
	*/
	
	/*
	@GetMapping(value="/sns/finish")
    private String snsFinish(final Model model, @RequestParam("snsType") String snsType, @RequestParam("resultCode") String resultCode) {
    	
    	log.debug("■■■■■■ [snsFinish] snsType = {}, resultCode = {}", snsType, resultCode);
    	
    	model.addAttribute("snsType", snsType);
    	model.addAttribute("resultCode", resultCode);
    	    	
    	return "sample/sns_finish";
    }
    */
}
