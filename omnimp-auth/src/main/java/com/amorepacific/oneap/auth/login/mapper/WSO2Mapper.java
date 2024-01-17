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
 * Author	          : mcjan
 * Date   	          : 2020. 8. 6..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.login.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

/**
 * <pre>
 * com.amorepacific.oneap.auth.login.mapper 
 *    |_ WSO2Mapper.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 6.
 * @version : 1.0
 * @author  : mcjan
 */
@Mapper
public interface WSO2Mapper {

	List<UmOmniUser> getWso2UserInfo(final UmOmniUser omniUser);
	
	List<UmChUser> getChannelUserInfo(final UmChUser chUser);
	
	int isExistChannelUser(@Param("incsNo") final String incsNo, @Param("chCd") final String chCd);
}
