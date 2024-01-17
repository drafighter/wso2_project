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
 * Date   	          : 2020. 7. 10..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.common.validation;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.amorepacific.oneap.common.exception.SystemException;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.common.validation 
 *    |_ SystemInfo.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 10.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Component("systemInfoBean")
public class SystemInfo {

	@Autowired
	private Environment environment;
	
	private String[] activeProfiles;
	private String[] defaultProfiles;
	
	public static final String PROFILE_DEFAULT = "default";
	public static final String PROFILE_LOCAL = "local";
	public static final String PROFILE_DEV = "dev";
	public static final String PROFILE_STG = "stg";
	public static final String PROFILE_PRD = "prd";
	
	@PostConstruct
	private void init() {
		this.activeProfiles = this.environment.getActiveProfiles();
		this.defaultProfiles = this.environment.getDefaultProfiles();
		log.debug("*** active profiles : {}", Arrays.deepToString(this.activeProfiles));
		log.debug("*** default profiles : {}", Arrays.deepToString(this.defaultProfiles));
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 3. 오후 2:51:12
	 * </pre>
	 * @return
	 * @throws SystemException
	 */
	public String[] getActiveProfiles() throws SystemException {
		validateProfilesBean();
		
		if (ArrayUtils.isEmpty(this.activeProfiles)) {
			return this.defaultProfiles;
		}
		return this.activeProfiles;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 3. 오후 2:53:15
	 * </pre>
	 * @return
	 * @throws SystemException
	 */
	public boolean hasProdProfile() throws SystemException {
		return hasProfile(PROFILE_PRD);
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 10. 오후 1:31:24
	 * </pre>
	 * @return
	 * @throws SystemException
	 */
	public boolean hasLocalProfile() throws SystemException {
		return hasProfile(PROFILE_LOCAL) || hasProfile(PROFILE_DEFAULT);
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 3. 오후 2:51:16
	 * </pre>
	 * @param profile
	 * @return
	 * @throws SystemException
	 */
	public boolean hasProfile(String profile) throws SystemException {
		validateProfilesBean();
		if (ArrayUtils.isEmpty(this.activeProfiles)) {
			return Arrays.asList(this.defaultProfiles).contains(profile);
		}
		return Arrays.asList(this.activeProfiles).contains(profile);
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 3. 오후 2:51:21
	 * </pre>
	 * @throws SystemException
	 */
	private void validateProfilesBean() throws SystemException {
		if (ArrayUtils.isEmpty(this.activeProfiles) && ArrayUtils.isEmpty(this.defaultProfiles)) {
			throw new SystemException("one-ap Profiles info does not exist.");
		}
	}
	
}
