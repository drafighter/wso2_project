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
 * Date   	          : 2020. 7. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.sec;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ClassUtils;

import com.amorepacific.oneap.common.util.ConfigUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.common.sec 
 *    |_ SecurityFactory.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 21.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
public class SecurityFactory {

	public static final String AES = "aes";
	public static final String SHA = "sha";
	
	private Map<String, Object> objectMap = new HashMap<String, Object>();
	
	private ConfigUtil config = ConfigUtil.getInstance();
	
	public static class Singleton {
		private static final SecurityFactory instance = new SecurityFactory();
	}

	public static SecurityFactory getInstance() {
		return Singleton.instance;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 
	 * author   : takkies
	 * date     : 2020. 7. 21. 오후 7:04:20
	 * </pre>
	 * @param algorithm
	 * @return
	 */
	public SecurityEncoder getEncoder(final String algorithm) {
		String keyName = "security.".concat(algorithm).concat(".encoder");
		SecurityEncoder encoderObject = null;
		String encoderClass = this.config.getString(keyName);
		try {
			encoderObject = (SecurityEncoder) objectMap.get(keyName);
			if (encoderObject == null) {
				encoderObject = (SecurityEncoder) ClassUtils.forName(encoderClass, this.getClass().getClassLoader()).newInstance();
				objectMap.put(keyName, encoderObject);
			}
		} catch (Exception e) {
			log.error(String.format("{} : {}", encoderClass, e.getMessage()));
		}
		return encoderObject;
	}

	/**
	 * 
	 * <pre>
	 * commnet  : 
	 * author   : takkies
	 * date     : 2020. 7. 21. 오후 7:04:24
	 * </pre>
	 * @param algorithm
	 * @return
	 */
	public SecurityDecoder getDecorder(final String algorithm) {
		String keyName = "security.".concat(algorithm).concat(".decoder");
		SecurityDecoder encoderObject = null;
		String decoderClass = this.config.getString(keyName);
		try {
			encoderObject = (SecurityDecoder) objectMap.get(keyName);
			if (encoderObject == null) {
				encoderObject = (SecurityDecoder) ClassUtils.forName(decoderClass, this.getClass().getClassLoader()).newInstance();
				objectMap.put(keyName, encoderObject);
			}
		} catch (Exception e) {
			log.error(String.format("{} : {}", decoderClass, e.getMessage()));
		}
		return encoderObject;
	}
	
}
