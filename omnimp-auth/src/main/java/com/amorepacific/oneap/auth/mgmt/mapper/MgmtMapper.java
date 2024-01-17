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
package com.amorepacific.oneap.auth.mgmt.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.amorepacific.oneap.auth.login.vo.Web2AppVo;
import com.amorepacific.oneap.auth.mgmt.vo.WithdrawUserVo;
import com.amorepacific.oneap.common.vo.api.IncsRcvData;
import com.amorepacific.oneap.common.vo.sns.SnsParam;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;
import com.amorepacific.oneap.common.vo.user.UserData;

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
public interface MgmtMapper {
	
	List<String> getIntegratedExtraId(final String loginid);
	
	List<UserData> getUserLoginInfoListByLoginId(final UserData userData);
	
	List<UserData> getOmniUserDataList(final UserData userData);
	
	List<UmOmniUser> getOmniConversionUserList(UmOmniUser umOmniUser);
	
	List<UmChUser> getChannelConversionUserList(UmChUser umChUser);
	
	List<UmChUser> getChannelUserIdList(UmChUser umChUser);
	
	List<UmOmniUser> getOmniLoginUserList(UmOmniUser umOmniUser);
	
	List<UmChUser> getChannelLoginUserList(UmChUser umChUser);
	
	List<UmChUser> getChannelLoginUserListByFlag(UmChUser umChUser);
	
	List<UmOmniUser> getOmniJoinUserList(UmOmniUser umOmniUser);
	
	List<UmChUser> getChannelJoinUserList(UmChUser umChUser);
	
	List<UmChUser> getChannelTransferUserList(final UmChUser umChUser);
	
	List<WithdrawUserVo> getWithdrawedUserList();
	
	int updateWithdrawedUser(WithdrawUserVo withdrawUserVo);
	
	int existWithdrawFlagUser(WithdrawUserVo withdrawUserVo);
	
	int insertWithdrawFlagUser(WithdrawUserVo withdrawUserVo);
	
	int updateWithdrawFlagUser(WithdrawUserVo withdrawUserVo);
	
	int deleteWithdrawedUserDisabledAfter(WithdrawUserVo withdrawUserVo);
	
	int updateConversionComplete(UmChUser umChUser);
	
	int updateConversionCompleteById(UmChUser umChUser);
	
	List<UserData> getOmniUserLoginIdList(final UserData userData);
	
	List<UserData> getChannelUserLoginIdList(final UserData userData);
	
	List<UserData> getChannelUserLoginIdByChId(final UserData userData);
	
	int updateUserDormancyRelease(UmOmniUser omniUser);
	
	int updateUserPasswordResetFlagInit(UmOmniUser omniUser);
	
	String getSnsMappingTime(SnsParam snsParam);
	
	UmOmniUser getOmniUserByLoginUserName(final String userName);
	
	int updateChannelPassword(final UserData userData);
	
	int hasSameLoginId(final UmChUser chUser);
	
	int hasSameLoginIdByLoginId(final UmChUser chUser);
	
	int existRcvData(final IncsRcvData incsRcvData);
	
	int insertRcvName(final IncsRcvData incsRcvData);
	
	int updateRcvName(final IncsRcvData incsRcvData);
	
	int insertRcvDormancy(final IncsRcvData incsRcvData);
	
	int updateRcvDormancy(final IncsRcvData incsRcvData);
	
	String getOminUserPasswordByIncsNo(final UserData userData);
	
	int updateUmUserPassword(final UserData userData);
	
	int updateLastPasswordUpdateNow(final UserData userDate);
	
	int insertWeb2AppData(final Web2AppVo web2AppVo);
	
	Web2AppVo selectWeb2AppData(final String uuid);
	
	HashMap<String, String> selectTokenValid(final Web2AppVo WebAppResponse);
	
	int updateWeb2AppData(final Web2AppVo web2AppVo);
	
	int updateWeb2AppAuthKey(final Web2AppVo web2AppVo);
	
	int selectConsumerAppId(final String consumerkey);

}
