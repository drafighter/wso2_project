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
 * Date   	          : 2020. 7. 16..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * com.amorepacific.oneap.auth.common 
 *    |_ I18MessageController.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 16.
 * @version : 1.0
 * @author : takkies
 */
@RestController
public class I18MessageController {

	@GetMapping("/messages/{messagename}")
	public void getMessages(@PathVariable("messagename") final String messagename, final HttpServletResponse response, final Locale locale) throws IOException {

		String messageprop = StringUtils.isEmpty(messagename) ? "message" : messagename;
		OutputStream outputStream = response.getOutputStream();
		Resource resource = new ClassPathResource("/messages/" + messageprop);
		try (InputStream inputStream = resource.getInputStream();) {
			List<String> readLines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
			IOUtils.writeLines(readLines, null, outputStream, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// NOPMD
		}
	}

}
