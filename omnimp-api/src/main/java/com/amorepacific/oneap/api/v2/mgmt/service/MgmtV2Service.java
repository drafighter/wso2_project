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
package com.amorepacific.oneap.api.v2.mgmt.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.api.v2.mgmt.mapper.MgmtV2Mapper;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.IncsRcvData;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.mgmt.service 
 *    |_ MgmtService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 6.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class MgmtV2Service {

	@Autowired
	private MgmtV2Mapper mgmtMapper;

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 4. 오후 5:38:49
	 * </pre>
	 * 
	 * @param chCd
	 * @param incsNo
	 * @return
	 */
	public List<UmChUser> getChannelConversionUserList(final String chCd, final String incsNo) {
		
		if (StringUtils.isEmpty(incsNo) || "0".equals(incsNo)) {
			return Collections.emptyList();
		}
		
		UmChUser umChUser = new UmChUser();
		umChUser.setChCd(chCd);
		umChUser.setIncsNo(Integer.parseInt(incsNo));

		return this.mgmtMapper.getChannelConversionUserList(umChUser);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 임시 채널고객조회(회원가입, 휴대폰 로그인 시)
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 13. 오후 5:28:06
	 * </pre>
	 * 
	 * @param incsNo
	 * @param chCd
	 * @return
	 */
	public List<UmChUser> getChannelUserList(final int incsNo, final String chCd) {
		
		if (incsNo <= 0) {
			return Collections.emptyList();
		}
		
		UmChUser umChUser = new UmChUser();
		umChUser.setIncsNo(incsNo);
		umChUser.setChCd(chCd);
		return this.mgmtMapper.getChannelJoinUserList(umChUser);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 채널 회원 전환 완료 처리
	 * author   : takkies
	 * date     : 2020. 9. 5. 오후 3:52:46
	 * </pre>
	 * 
	 * @param umChUser
	 * @return
	 */
	public boolean updateConversionComplete(UmChUser umChUser) {
		
		if (umChUser.getIncsNo() <= 0) {
			return true;
		}
		
		return this.mgmtMapper.updateConversionComplete(umChUser) > 0;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 20. 오후 9:13:19
	 * </pre>
	 * 
	 * @param umChUser
	 * @return
	 */
	public boolean updateConversionCompleteById(UmChUser umChUser) {
		
		if (umChUser.getIncsNo() <= 0) {
			return true;
		}
		
		return this.mgmtMapper.updateConversionCompleteById(umChUser) > 0;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 자체고객의 자체회원아이디 조회
	 * 
	 * author   : takkies
	 * date     : 2020. 10. 22. 오후 3:02:15
	 * </pre>
	 * 
	 * @param chCd
	 * @param incsNo
	 * @param sessionEncLoginId
	 * @return
	 */
	public String searchChannelWebId(final String trnsType, final String chCd, final String incsNo, final String sessionEncLoginId) {

		log.debug("▶▶▶▶▶▶ [search channel webid] trnsType : {}, chCd : {}, incsNo : {}, sessionwebid : {}", trnsType, chCd, incsNo, sessionEncLoginId);

		if (StringUtils.hasText(incsNo) && !"0".equals(incsNo)) {
			List<UmChUser> chUsers = getChannelConversionUserList(chCd, incsNo);
			if (chUsers == null || chUsers.isEmpty()) {
				log.debug("▶▶▶▶▶▶ [search channel webid] channel session login id : {}", sessionEncLoginId);
				return sessionEncLoginId;
			} else {
				return chUsers.get(0).getChcsWebId();
			}
		}
		return "";
	}
	
	public boolean existRcvData(final IncsRcvData incsRcvData) {
		if (incsRcvData.getIncsNo() <= 0) {
			return false;
		}
		return this.mgmtMapper.existRcvData(incsRcvData) > 0;
	}
	public boolean updateRcvName(final IncsRcvData incsRcvData) {
		if (incsRcvData.getIncsNo() <= 0) {
			return true;
		}
		
		return this.mgmtMapper.updateRcvName(incsRcvData) > 0;
	}
	public boolean insertRcvName(final IncsRcvData incsRcvData) {
		
		if (incsRcvData.getIncsNo() <= 0) {
			return true;
		}
		
		return this.mgmtMapper.insertRcvName(incsRcvData) > 0;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 뷰티포인트 고객 로그인을 위한 사용자 정보 조회
	 * @param loginId
	 * @param loginPwd
	 * @return
	 */
	public List<UmOmniUser> getOmniLoginUserList(final String loginId, final String loginPwd) {

		UmOmniUser umOmniUser = new UmOmniUser();
		umOmniUser.setUmUserName(loginId);
		umOmniUser.setUmUserPassword(SecurityUtil.getEncodedWso2Password(loginPwd));
		umOmniUser.setUmAttrName(OmniConstants.UID);
		umOmniUser.setUmAttrValue(loginId);

		return this.mgmtMapper.getOmniLoginUserList(umOmniUser);
	}
	
}
