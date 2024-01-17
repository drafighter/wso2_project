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
 * Date   	          : 2020. 7. 31..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v1.mgmt.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.amorepacific.oneap.api.v1.mgmt.vo.AbusingLockVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.AbusingUserVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChUserVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.ChnTermsCndVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.DupIdVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.MappingIdSearchVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.OmniUserVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.SearchSnsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.SnsUnlinkVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.TermsResponseVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.TermsVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.UserVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.VerifyPwdVo;
import com.amorepacific.oneap.api.v1.mgmt.vo.Web2AppVo;
import com.amorepacific.oneap.common.vo.api.CheckSnsIdUserVo;
import com.amorepacific.oneap.common.vo.api.CheckSnsIdVo;
import com.amorepacific.oneap.common.vo.api.CreateDupUserRequest;
import com.amorepacific.oneap.common.vo.api.CreateDupUserResponse;
import com.amorepacific.oneap.common.vo.api.IncsRcvData;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;


/**
 * <pre>
 * com.amorepacific.oneap.api.v1.mgmt 
 *    |_ MgmtApiMapper.java
 * </pre>
 *
 * @desc    :
 * @date    : 2020. 7. 31.
 * @version : 1.0
 * @author  : takkies
 */
@Mapper
public interface MgmtApiMapper {
	
	int isUserExist(final String incsNo);
	
	int isUserExistByLoginId(final String userName);
	
	String isDisabledUser(final String userName);
	
	String isLockedUser(final String userName);
	
	String isDormancyUser(final String userName);
	
	String getAccountState(final String userName);
	
	int isTermsExist(final TermsVo termsVo);
		
	List<String> getAssociatedSnsId(final SnsUnlinkVo snsUnlinkVo);
	
	int isMappingExsist(final MappingIdSearchVo mappingIdSearchVo);
	
	int isMappingOther(final MappingIdSearchVo mappingIdSearchVo);
	
	UserVo getUserByIncsNo(final String incsNo);
	
	UserVo getUserByIncsNoAndUserName(final UserVo userVo);
	
	UserVo getUser(final UserVo userVo);

	int mergeTermYn(TermsVo termsVo);
	
	int checkDuplicateId(final DupIdVo dupIdVo);
	
	String getPassword(final String loginId);
	
	int verifyPassword(final VerifyPwdVo verifyPwdVo);
	
	int updatePassword(final UserVo userVo);
	
	int updateJoinDate(final UserVo userVo);
	
	List<SearchSnsVo> getSnsInfoList(final String userName);
	
	List<TermsResponseVo> getTermsList(final ChnTermsCndVo chnTermsCndVo);
	
	List<TermsResponseVo> getRequiredTermsList(final ChnTermsCndVo reqTermsCndVo);
	
	int hasTermsAgree(final ChnTermsCndVo reqTermsCndVo);
	
	int hasCorpTermsAgree(final ChnTermsCndVo reqTermsCndVo);

	int deleteAssociatedId(final SnsUnlinkVo snsUnlinkVo);
		
	int inserOccuCustTncHist(final TermsVo termsVo);
	
	int deleteChannelUser(final TermsVo termsVo);
	
	int updatePasswordReset(final String userName);
	
	ChUserVo getChUser(final ChUserVo chUserVo);
	
	int updateChUserPassword(final ChUserVo chUserVo);
	
	String getCorpTermsCode(final String chCd);
	
	int existUserByLoginidAndIncsNo(final Map<String, Object> params);
	
	int existUserByLoginidAndIncsNoNew(final Map<String, Object> params);
	
	CreateDupUserResponse existDisabledUser(final Map<String, Object> params);
	
	int updateIncsNoByNewUser(final Map<String, Object> params); 
	
	int updateIncsNoByNewUserAttribute(final CreateDupUserRequest createDupUserRequest);
	
	List<TermsResponseVo> getUserTermsListByIncsNo(final CreateDupUserRequest createDupUserRequest);
	
	int deleteSnsMapping(final String incsNo);
	
	int existRcvDormancyData(final IncsRcvData incsRcvData);
	
	int insertRcvDormancyName(final IncsRcvData incsRcvData);
	
	int updateRcvDormancyName(final IncsRcvData incsRcvData);
	
	int updateReleaseDormancyUser(final String incsNo);
	
	int updateSnsUserName(final String userName, final String userNameNew);
	
	int updateUserIncsNo(final String chCd, final String webId, final String asisIncsNo, final String tobeIncsNo);
	
	CheckSnsIdUserVo getUserBySnsId(final CheckSnsIdVo checkSnsIdVo);
	
	List<UmOmniUser> getOmniLoginUserList(UmOmniUser umOmniUser);
	
	UmOmniUser getOmniUserByLoginUserName(final String userName);
	
	OmniUserVo getUserByUserId(final UserVo userVo);
	
	List<UmChUser> getChannelLoginUserListByFlag(UmChUser umChUser);
	
	AbusingUserVo getAbusingUserSearch(int incsNo);
	
	int updateAbusingUser(int incsNo);
	
	int abusingUserLockLog(AbusingLockVo abusingLogingVo);
	
	int abusingUserInsert(AbusingUserVo abusingUserVo);
	
	int web2AppSendAuthKey(Web2AppVo web2AppVo);
}
