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
 * Date   	          : 2020. 7. 30..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.amorepacific.oneap.common.exception.SystemException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.experimental.UtilityClass;

/**
 * <pre>
 * com.amorepacific.oneap.common.util 
 *    |_ ObjectUtil.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 7. 30.
 * @version : 1.0
 * @author : takkies
 */

@UtilityClass
public class ObjectUtil {

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 7. 30. 오전 11:30:01
	 * </pre>
	 * 
	 * @param <T>
	 * @param map
	 * @param clazz
	 * @return
	 */
	public <T> T convertMapToObject(Map<?, ?> map, Class<T> clazz) throws SystemException {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			return mapper.convertValue(map, clazz);
		} catch (Exception e) {
			throw new SystemException(String.format("Unrecognized field : %s", map.toString()));
		}

	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 23. 오후 6:48:31
	 * </pre>
	 * @param request
	 * @return
	 */
	public Map<String, Object> convertRequestToMap(HttpServletRequest request) {
		Map<String, Object> hmap = new HashMap<>();
		String key;
		Enumeration<?> enums = request.getParameterNames();
		while (enums.hasMoreElements()) {
			key = (String) enums.nextElement();
			if (request.getParameterValues(key).length > 1) {
				hmap.put(key, request.getParameterValues(key));
			} else {
				try {
					hmap.put(key, URLDecoder.decode(request.getParameter(key), StandardCharsets.UTF_8.name()));
				} catch (UnsupportedEncodingException e) {
					hmap.put(key, request.getParameter(key));
				}
			}
		}
		return hmap;
	}
	
	public JsonObject convertStringToJsonObject(String str) {
		try {
			JsonObject convertedObject = new Gson().fromJson(str, JsonObject.class);
			System.out.println("convertedObject : " + convertedObject.toString());
			return convertedObject;
		} catch (Exception e) {
			return null;
		}
	}
}
