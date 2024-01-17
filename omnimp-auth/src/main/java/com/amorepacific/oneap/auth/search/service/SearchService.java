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
 * Date   	          : 2020. 8. 21..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.search.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.mgmt.service.MgmtService;
import com.amorepacific.oneap.auth.search.mapper.SearchMapper;
import com.amorepacific.oneap.auth.search.vo.SearchData;
import com.amorepacific.oneap.auth.search.vo.SearchResponse;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.mask.Masker;
import com.amorepacific.oneap.common.mask.Masker.Builder;
import com.amorepacific.oneap.common.mask.actor.MaskActor;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.SearchChCustRequest;
import com.amorepacific.oneap.common.vo.api.SearchChCustResponse;
import com.amorepacific.oneap.common.vo.api.UserInfo;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;
import com.amorepacific.oneap.common.vo.user.UserData;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.search.service 
 *    |_ SearchService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 21.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class SearchService {

	@Autowired
	private SearchMapper searchMapper;

	@Autowired
	private MgmtService mgmtService;

	@Autowired
	private CustomerApiService customerApiService;

	@Autowired
	private CommonService commonService;

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 21. 오후 2:36:10
	 * </pre>
	 * 
	 * @param loginid
	 * @return
	 */
	public boolean hasLoginid(final String loginid) {
		return this.searchMapper.hasLoginid(loginid) > 0;
	}

	public UmOmniUser getUserInfo(final UserData userData) {
		
		if (StringUtils.isEmpty(userData.getIncsNo()) || "0".equals(userData.getIncsNo())) {
			return null;
		}
		
		return this.searchMapper.getUserInfo(userData);
	}

	public UmOmniUser getOmniUserInfo(final UserData userData) {
		
		if (StringUtils.isEmpty(userData.getIncsNo()) || "0".equals(userData.getIncsNo())) {
			return null;
		}
		
		return this.searchMapper.getOmniUserInfo(userData);
	}

	public UmOmniUser getChannelUserInfo(final UserData userData) {
		
		if (StringUtils.isEmpty(userData.getIncsNo()) || "0".equals(userData.getIncsNo())) {
			return null;
		}
		
		return this.searchMapper.getChannelUserInfo(userData);
	}

	public SearchResponse searchIdResult(final CertResult certresult, final String chCd) {
		SearchResponse response = new SearchResponse();

		List<SearchData> searchOmniUsers = new ArrayList<>();
		List<SearchData> searchChannelUsers = new ArrayList<>();
		Builder maskBuilder = new Masker.Builder().maskType(MaskActor.Type.USERID);

		// 통합고객플랫폼 조회
		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setCustName(certresult.getName());
		custInfoVo.setCustMobile(certresult.getPhone());
		CustInfoResponse custResponse = this.customerApiService.getCustList(custInfoVo);
		if (custResponse != null) {
			Customer users[] = custResponse.getCicuemCuInfTcVo();
			if (users != null && users.length > 0) {
				log.debug("▶▶▶▶▶▶▶▶▶▶▶ search customer api result : {}", StringUtil.printJson(users));

				SearchData searchUser;
				Customer user = users[0];

				UserData userData = new UserData();
				userData.setChCd(user.getChCd());
				userData.setIncsNo(user.getIncsNo());
				List<UserData> userlist = this.mgmtService.getOmniUserLoginIdList(userData);
				if (userlist != null && !userlist.isEmpty()) {
					for (UserData userdata : userlist) {
						searchUser = new SearchData();
						searchUser.setLoginId(maskBuilder.maskValue(userdata.getLoginId()).build().masking());
						searchUser.setPassId(SecurityUtil.setXyzValue(userdata.getLoginId()));
						searchUser.setMobile(SecurityUtil.setXyzValue(StringUtil.mergeMobile(user)));
						searchUser.setIncsNo(userdata.getIncsNo());
						log.debug("▶▶▶▶▶▶ {}", StringUtil.printJson(searchUser));
						searchOmniUsers.add(searchUser);
					}
					response.setSearchOmniUsers(searchOmniUsers);
				}
			}
		}

		SearchChCustRequest searchCustRequest = new SearchChCustRequest();
		searchCustRequest.setChCd(chCd);
		searchCustRequest.setName(certresult.getName());
		searchCustRequest.setPhone(certresult.getPhone());
		SearchChCustResponse chCustRespnose = this.customerApiService.getChannelUser(chCd, searchCustRequest);
		if (chCustRespnose != null) {
			if (chCustRespnose.getUserInfo() != null) {
				UserInfo userinfos[] = chCustRespnose.getUserInfo();
				log.debug("▶▶▶▶▶▶▶▶▶▶▶ search channel customer api result : {}", StringUtil.printJson(userinfos));

				SearchData searchUser;
				if (userinfos != null && userinfos.length > 0) {
					UserInfo userinfo = userinfos[0];

					UserData userData = new UserData();
					userData.setChCd(chCd);
					userData.setChLoginId(userinfo.getWebId());
					//userData.setIncsNo(Integer.toString(userinfo.getIncsNo()));
					List<UserData> userlist = this.mgmtService.getChannelUserLoginIdByChId(userData);
					log.debug("▶▶▶▶▶▶▶▶▶▶▶ search channel customer tempTable result : {}", StringUtil.printJson(userlist));
					if (userlist != null && !userlist.isEmpty()) {
						final Channel channel = this.commonService.getChannel(chCd);
						for (UserData userdata : userlist) {
							searchUser = new SearchData();
							searchUser.setLoginId(maskBuilder.maskValue(userdata.getLoginId()).build().masking());
							searchUser.setPassId(SecurityUtil.setXyzValue(userdata.getLoginId()));
							searchUser.setMobile(SecurityUtil.setXyzValue(userinfo.getPhone()));
							searchUser.setIncsNo(userdata.getIncsNo());
							searchUser.setChCdName(channel.getChCdNm());
							log.debug("▶▶▶▶▶▶ {}", StringUtil.printJson(searchUser));
							searchChannelUsers.add(searchUser);
						}
						response.setSearchChannelUsers(searchChannelUsers);
					}
				}
			}
		}

		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 8. 26. 오후 12:37:05
	 * </pre>
	 * 
	 * @param userData
	 * @return
	 */
	public BaseResponse passwordCheckAndUpdate(final UserData omniUserData, final UserData chUserData) {
		
		BaseResponse response = new BaseResponse();
		
		if(omniUserData == null && chUserData == null) {
			response.setStatus(-30); // param is null
			return response;
		}
		
		int updateCount = 0;

		if (omniUserData != null) {
			// 비.밀.번.호 확인체크
			if (StringUtils.hasText(omniUserData.getConfirmPassword())) {
				if (!omniUserData.getPassword().equals(omniUserData.getConfirmPassword())) {
					response.setStatus(-20); // 비.밀.번.호와 비.밀.번.호 확인값이 다름.
					return response;
				}
			}

			if (omniUserData.getLoginId().equals(omniUserData.getPassword())) {
				response.setStatus(-25); // 비.밀.번.호와 아이디가 같음
				return response;
			}

			if (StringUtils.isEmpty(omniUserData.getIncsNo()) || "0".equals(omniUserData.getIncsNo())) {
				response.SetResponseInfo(ResultCode.SUCCESS);
				return response;
			}
			
			UserData userData = new UserData();
			userData.setChCd(omniUserData.getChCd());
			userData.setLoginId(omniUserData.getLoginId());
			userData.setIncsNo(omniUserData.getIncsNo());
			userData.setPassword(omniUserData.getPassword());
			userData.setConfirmPassword(omniUserData.getConfirmPassword());
			// 통합고객번호를 이용하여 사용자 정보 조회
			final UmOmniUser userinfo = getOmniUserInfo(userData);
			log.debug("▶▶▶▶▶▶ [pwd change] get Omni UserInfo : {}", StringUtil.printJson(userinfo));
			if (userinfo != null) {
//				ApiBaseResponse apiResponse = this.mgmtService.updateOmniUserPassword(userData);
//				if (apiResponse.getResultCode().equals(ResultCode.SUCCESS.getCode())) {
//					updateCount++;
//				} else {
//					response.setStatus(-10); // 비.밀.번.호 변경 실패
//				}
				
				userData.setLoginId(userinfo.getUmUserName()); // DB 조회한 로그인 정보 설정
				final String dbpassword = userinfo.getUmUserPassword(); // DB 조회한 비밀번호
				boolean rtn = SecurityUtil.compareWso2Password(dbpassword, userData.getPassword()); // 기존 비밀번호와 변경비밀번호 같은지 체크

				if (!rtn) { // 같지 않으면
					ApiBaseResponse apiResponse = this.mgmtService.updateOmniUserPassword(userData);
					if (apiResponse.getResultCode().equals(ResultCode.SUCCESS.getCode())) {
						updateCount++;
					} else {
						response.setStatus(-10); // 비.밀.번.호 변경 실패
					}
				} else {
					response.setStatus(-15); // 비.밀.번.호 같으면 실패
				}
				
			} else {
				response.setStatus(-5); // 사용자정보 없음.
			}
		}

		if (chUserData != null) {
			// 비.밀.번.호 확인체크
			if (StringUtils.hasText(chUserData.getConfirmPassword())) {
				if (!chUserData.getPassword().equals(chUserData.getConfirmPassword())) {
					response.setStatus(-20); // 비.밀.번.호와 비.밀.번.호 확인값이 다름.
					return response;
				}
			}

			if (chUserData.getLoginId().equals(chUserData.getPassword())) {
				response.setStatus(-25); // 비.밀.번.호와 아이디가 같음
				return response;
			}

			/*
			// 아이디로 검색하도록 수정했음
			if (StringUtils.isEmpty(chUserData.getIncsNo()) || "0".equals(chUserData.getIncsNo())) {
				response.SetResponseInfo(ResultCode.SUCCESS);
				return response;
			}
			*/
			
			UserData userData = new UserData();
			userData.setChCd(chUserData.getChCd());
			userData.setLoginId(chUserData.getLoginId());
			userData.setIncsNo(chUserData.getIncsNo());
			userData.setPassword(chUserData.getPassword());
			userData.setConfirmPassword(chUserData.getConfirmPassword());
			// 통합고객번호를 이용하여 사용자 정보 조회
			final UmOmniUser userinfo = getChannelUserInfo(userData);
			log.debug("▶▶▶▶▶▶ [pwd change] get Channel UserInfo : {}", StringUtil.printJson(userData));
			if (userinfo != null) {
	//			boolean rtn = this.mgmtService.updateChannelPassword(userData);
	//			if (rtn) {
	//				updateCount++;
	//			} else {
	//				response.setStatus(-10); // 비.밀.번.호 변경 실패
	//			}
				
				userData.setLoginId(userinfo.getUmUserName()); // DB 조회한 로그인 정보 설정
				final String dbpassword = userinfo.getUmUserPassword(); // DB 조회한 비밀번호
				boolean rtn = SecurityUtil.compareWso2Password(dbpassword, userData.getPassword()); // 기존 비밀번호와 변경비밀번호 같은지 체크
	
				if (!rtn) { // 같지 않으면
					rtn = this.mgmtService.updateChannelPassword(userData);
					if (rtn) {
						updateCount++;
					} else {
						response.setStatus(-10); // 비.밀.번.호 변경 실패
					}
				} else {
					response.setStatus(-15); // 비.밀.번.호 같으면 실패
				}
				
			} else {
				response.setStatus(-5); // 사용자정보 없음.
			}
		}
		

		if (updateCount > 0) {
			response.setStatus(1);
		}

		return response;
	}
}
