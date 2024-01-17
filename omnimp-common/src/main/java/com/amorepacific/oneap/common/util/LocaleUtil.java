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
package com.amorepacific.oneap.common.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.context.i18n.LocaleContextHolder;

import lombok.experimental.UtilityClass;

/**
 * <pre>
 * com.amorepacific.oneap.common.util 
 *    |_ LocaleUtil.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 13.
 * @version : 1.0
 * @author : takkies
 */
@UtilityClass
public class LocaleUtil {

	private Locale locale = LocaleContextHolder.getLocale();
	
	/**
	 * 
	 * <pre>
	 * commnet  : 로케일 조회
	 * author   : takkies
	 * date     : 2020. 7. 13. 오전 9:16:17
	 * </pre>
	 * @return
	 */
	public Locale getLocale() {
		return locale;
	}
	
	/**
	 * 
	 * <pre>
	 * commnet  : 로케일에서 언어 조회
	 * author   : takkies
	 * date     : 2020. 7. 13. 오전 9:16:59
	 * </pre>
	 * @return
	 */
	public String getLocaleLanguage() {
		return locale.getLanguage();
	}
	
	/**
	 * 
	 * <pre>
	 * commnet  : 로케일에서 국가 조회
	 * author   : takkies
	 * date     : 2020. 7. 13. 오전 9:18:02
	 * </pre>
	 * @return
	 */
	public String getLocaleCountry() {
		return locale.getCountry();
	}
	
	/**
	 * 
	 * <pre>
	 * commnet  : 로케일에서 타임존 ID 조회
	 * author   : takkies
	 * date     : 2020. 7. 16. 오후 12:23:33
	 * </pre>
	 * @return
	 */
	public String getTimezone() {
		Calendar calenar = new GregorianCalendar(locale);
	    TimeZone timezone = calenar.getTimeZone();
	    return timezone.getID();
	}
	
}
