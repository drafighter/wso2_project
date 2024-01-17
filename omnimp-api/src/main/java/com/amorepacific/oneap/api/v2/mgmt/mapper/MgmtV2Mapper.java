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
 * Date   	          : 2020. 8. 6..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v2.mgmt.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.amorepacific.oneap.common.vo.api.IncsRcvData;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

/**
 * <pre>
 * com.amorepacific.oneap.auth.mgmt.mapper 
 *    |_ MgmtMapper.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 8. 6.
 * @version : 1.0
 * @author  : takkies
 */
@Mapper
public interface MgmtV2Mapper {
	
	List<UmChUser> getChannelConversionUserList(UmChUser umChUser); //
	
	List<UmChUser> getChannelJoinUserList(UmChUser umChUser); //
	
	int updateConversionComplete(UmChUser umChUser); //
	
	int updateConversionCompleteById(UmChUser umChUser); //
	
	int existRcvData(final IncsRcvData incsRcvData);
	
	int updateRcvName(final IncsRcvData incsRcvData);
	
	int insertRcvName(final IncsRcvData incsRcvData);
	
	List<UmOmniUser> getOmniLoginUserList(UmOmniUser umOmniUser);
	
}
