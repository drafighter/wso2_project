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
package com.amorepacific.oneap.admin.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.amorepacific.oneap.common.util.LocaleUtil;

/**
 * <pre>
 * com.amorepacific.oneap.admin.sample 
 *    |_ SampleController.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 13.
 * @version : 1.0
 * @author : takkies
 */

@Controller
public class SampleController {
	
	@Autowired
	private MessageSource messageSource;

	@GetMapping("/sample")
	public String sample(@RequestParam("param") String param, Model model) {
		model.addAttribute("sample", "sample param : " + param);
		String msg = this.messageSource.getMessage("sample.message", null, LocaleUtil.getLocale());
		model.addAttribute("message", msg);
		return "sample/sample";
	}
	
	@GetMapping("/sample/{value}")
	public String sample2(@PathVariable("value") String value, Model model) {
		model.addAttribute("sample", "sample pathvarible = " + value);
		String msg = this.messageSource.getMessage("sample.message", null, LocaleUtil.getLocale());
		model.addAttribute("message", msg);
		return "sample/sample";
	}
}
