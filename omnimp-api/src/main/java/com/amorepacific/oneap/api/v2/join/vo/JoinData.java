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
 * Date   	          : 2020. 8. 27..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.v2.join.vo;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.api.v2.join.service.CustomerApiService;
import com.amorepacific.oneap.api.v2.join.vo.CreateCustChannelRequest.CicuedCuChTcVo;
import com.amorepacific.oneap.api.v2.join.vo.CreateCustChannelRequest.CicuemCuOptiTcVo;
import com.amorepacific.oneap.api.v2.join.vo.CreateCustVO.CicuedCuAddrTcVo;
import com.amorepacific.oneap.api.v2.join.vo.CreateCustVO.CicuedCuChCsTcVo;
import com.amorepacific.oneap.api.v2.join.vo.CreateCustVO.CicuedCuTncaTcVo;
import com.amorepacific.oneap.api.v2.join.vo.CreateCustVO.CicuemCuAdtInfTcVo;
import com.amorepacific.oneap.api.v2.join.vo.CreateCustVO.CicuemCuOptiCsTcVo;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.Marketing;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OnOffline;
import com.amorepacific.oneap.common.vo.Terms;
import com.amorepacific.oneap.common.vo.api.CuoptiResponse;
import com.amorepacific.oneap.common.vo.api.CuoptiResponse.CicuemCuOptiQcVo;
import com.amorepacific.oneap.common.vo.api.CuoptiVo;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.join.vo 
 *    |_ JoinData.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 27.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Component
public class JoinData {
	
	@Autowired
	private SystemInfo systemInfoBean;
	private static SystemInfo systemInfo;
	
	@Autowired
	private CustomerApiService customerApiServiceBean;
	private static CustomerApiService customerApiService;
	
	@SuppressWarnings("static-access")
	@PostConstruct
	private void initialize() {
		this.systemInfo = systemInfoBean;
		this.customerApiService = customerApiServiceBean;
	}
	
	private static ConfigUtil config = ConfigUtil.getInstance();

	/**
	 * <pre>
	 * comment  : 온라인 통합고객 데이터 생성
	 * 
	 * "이니스프리" 매장코드 및 매장명
	 * 온라인인 경우 온/오프 13000001 이니스프리쇼핑몰
	 * 
	 * "에스쁘아"  매장코드 및 매장명
	 * 온라인인 경우 온/오프 11000494 에스쁘아쇼핑몰
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 27. 오후 9:36:20
	 * </pre>
	 * 
	 * @param joinRequest
	 * @return
	 */
	public static CreateCustVO buildIntegratedOnlineCreateCustomerData(JoinRequest joinRequest, Channel channel) {
		
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		
		// 외부 제휴몰에서 뷰티멤버십 연동을 통한 신규 회원 가입 시 채널 등록은 후순위로 수행
		final String isMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_MEMBERSHIP);
		boolean isMembership = OmniUtil.isMembership(isMembershipSession);

		joinRequest.setIncsno(null);
		CreateCustVO createCustVo = new CreateCustVO();
		// Mandatory
		createCustVo.setCustNm(joinRequest.getUnm());
		createCustVo.setAthtDtbr(joinRequest.getBirth());
		createCustVo.setFrclCd(joinRequest.getNational()); // 내국인K, 외국인F

		if (joinRequest.getGender().equals("1")) {
			createCustVo.setSxclCd("M"); // 여성F, 남성M
		} else if (joinRequest.getGender().equals("0")) {
			createCustVo.setSxclCd("F"); // 여성F, 남성M
		} else {
			createCustVo.setSxclCd(joinRequest.getGender()); // 여성F, 남성M
		}

		if (StringUtils.hasText(joinRequest.getPhone())) {
			String phones[] = StringUtil.splitMobile(joinRequest.getPhone());
			if (phones != null && phones.length == 3) {
				createCustVo.setCellTidn(phones[0]); // 휴대폰식별전화번호
				createCustVo.setCellTexn(phones[1]); // 휴대폰국전화번호
				createCustVo.setCellTlsn(phones[2]); // 휴대폰끝전화번호
			}
		}
		createCustVo.setCiNo(joinRequest.getCi());
		// createCustVo.setJoinChCd(joinRequest.getChcd()); // 가입경로구분코드 -> 아모레 브랜드 사이트의 경우 joinChCd eq null ? 030 : joinChCd 으로 변경 2021-08-04
		if(config.isBrandSite(joinRequest.getChcd(), profile)) {
			createCustVo.setJoinChCd(config.getJoinChCd(joinRequest.getChcd(), profile));
			createCustVo.setNewCustJoinRqSiteCd(joinRequest.getChcd()); // 아모레 브랜드 사이트의 경우 신규고객가입요청사이트코드 값 추가 2021-08-31 
		} else {
			createCustVo.setJoinChCd(joinRequest.getChcd());
		}
		
		// 뷰티 멤버십으로 회원 가입 시 가입 채널코드는 000 으로 설정
		if(isMembership) {
			createCustVo.setJoinChCd("000");
		}

		if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
			createCustVo.setJoinPrtnId(joinRequest.getJoinPrtnId()); // 가입경로의 매장코드
		} else {
			createCustVo.setJoinPrtnId(config.getJoinPrtnCode(joinRequest.getChcd())); // 필수, 거래처 명
		}
		
		createCustVo.setAtclCd(""); // COM_CAT_CD:10139 (10:소유, 20:점유, 30:조정)
		createCustVo.setFscrId("OCP");
		createCustVo.setLschId("OCP");
		// Optional
		createCustVo.setPmdcCd("");
		createCustVo.setHomeZip("");
		createCustVo.setHomeBscsAddr("");
		createCustVo.setHomeDtlAddr("");
		createCustVo.setCustEmid("");
		createCustVo.setCustEmdn("");
		// COM_CAT_CD : 10155 (1:실명인증성공, 2:실명인증실패, 3: 해당자료없음, 4:통신오류, 5:체크썸오류, 50:명의도용차단가입자)
		createCustVo.setRnarCd("1");
		createCustVo.setRnmAthtDt("");
		createCustVo.setOfcBscsAddr("");
		createCustVo.setOfcDtlAddr("");
		createCustVo.setOfcZip("");
		createCustVo.setAddrXcrd("");
		createCustVo.setAddrYcrd("");
		createCustVo.setRfrsCd("");
		createCustVo.setBlmnNo("");
		createCustVo.setAdclCd("");
		createCustVo.setAdocCd("");
		createCustVo.setDrccCd("");
		createCustVo.setDrcsRegDt("");
		
		// 가입디바이스코드
		if (StringUtils.hasText(joinRequest.getDeviceType())) {
			createCustVo.setJndvCd(joinRequest.getDeviceType());// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)
		} else {
			createCustVo.setJndvCd("W"); 
		}
		
		// 고객가입디바이스운영체제구분코드
		if (StringUtils.hasText(joinRequest.getOsType())) {
			createCustVo.setCustJndvOsClCd(joinRequest.getOsType()); // 01: Android, 02 : iOS, 03 : MAC, 04 : WINDOWS
		}
		
		// 경로고객통합전환가입여부
		if (StringUtils.hasText(joinRequest.getSwitchJoinYn())) {
			createCustVo.setChcsIntgSwtJoinYn(joinRequest.getSwitchJoinYn());
		} else {
			createCustVo.setChcsIntgSwtJoinYn("N");
		}
		
		// SNS계정제휴사코드
		if (StringUtils.hasText(joinRequest.getSnsIdPrcnCd())) {
			createCustVo.setSnsIdPrcnCd(joinRequest.getSnsIdPrcnCd());
		}
		
		createCustVo.setSmsNum("");
		CicuemCuAdtInfTcVo adt = new CicuemCuAdtInfTcVo();

		// Mandatory
		adt.setSlccCd("S");
		adt.setPsnDtbr(joinRequest.getBirth());
		adt.setFscrId("OCP");
		adt.setLschId("OCP");
		// Optional
		adt.setWdanDt("");
		adt.setJobCd("");
		adt.setFscrId("OCP");
		adt.setLschId("OCP");

		createCustVo.setCicuemCuAdtInfTcVo(adt);
		// 뷰티포인트 약관 동의
		List<Terms> joinBpTerms = joinRequest.getBpterms();
		if (joinBpTerms != null && !joinBpTerms.isEmpty()) {
			for (Terms joinTerm : joinBpTerms) {
				CicuedCuTncaTcVo terms = new CicuedCuTncaTcVo();
				terms.setTcatCd(joinTerm.getTcatCd());
				terms.setTncvNo(joinTerm.getTncvNo());
				terms.setFscrId("OCP");
				terms.setLschId("OCP");
				terms.setTncAgrYn(joinTerm.getTncAgrYn());
				terms.setTncaDttm(joinTerm.getTncaChgDt());
				createCustVo.addTerms(terms);
			}
		}
		// 경로 약관 - 경로 약관은 옴니에만 등록
		List<Terms> joinTerms = joinRequest.getTerms();
		if (joinTerms != null && !joinTerms.isEmpty()) {
			for (Terms joinTerm : joinTerms) {
				CicuedCuTncaTcVo terms = new CicuedCuTncaTcVo();
				terms.setTcatCd(joinTerm.getTcatCd());
				terms.setTncvNo(joinTerm.getTncvNo());
				terms.setFscrId("OCP");
				terms.setLschId("OCP");
				terms.setTncAgrYn(joinTerm.getTncAgrYn());
				terms.setTncaDttm(joinTerm.getTncaChgDt());
				// createCustVo.addTerms(terms);
			}
		}
		
		// 수신 동의 처리
		List<Marketing> joinMarketings = joinRequest.getMarketings();
		if (joinMarketings != null && !joinMarketings.isEmpty()) {
			for (Marketing joinMarketing : joinMarketings) {
				
				// 뷰티멤버십 연동으로 회원 가입 시 제휴사는 처리하지 않음
				if(isMembership && channel.getChCd().equals(joinMarketing.getChCd())) {
					break;
				}

				CicuemCuOptiCsTcVo marketing = new CicuemCuOptiCsTcVo();
				// Mandatory
				marketing.setChCd(joinMarketing.getChCd());
				// Optional
				marketing.setSmsOptiYn(joinMarketing.getSmsAgree()); // SMS수신동의여부
				marketing.setSmsOptiDt(DateUtil.getCurrentDate()); // SMS수신동의일자
				marketing.setEmlOptiYn("N"); // 이메일수신동의여부
				marketing.setEmlOptiDt(DateUtil.getCurrentDate());
				marketing.setDmOptiYn("N"); // DM수신동의여부
				marketing.setDmOptiDt(DateUtil.getCurrentDate());
				marketing.setTmOptiYn("N"); // TM수신동의여부
				marketing.setTmOptiDt(DateUtil.getCurrentDate());
				marketing.setIntlOptiYn("N"); // 알림톡수신동의여부
				marketing.setIntlOptiDt(DateUtil.getCurrentDate());
				marketing.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
				marketing.setKkoIntlOptiDt(DateUtil.getCurrentDate());
				marketing.setFscrId("OCP");
				marketing.setLschId("OCP");
				createCustVo.addAgreeMarketing(marketing);

				// 경로 정보는 마케팅정보와 쌍이 맞지 않으면 오류 발생
				CicuedCuChCsTcVo chcsTcVo = new CicuedCuChCsTcVo();

				// Mandatory
				chcsTcVo.setChCd(joinMarketing.getChCd());
				chcsTcVo.setFscrId("OCP");
				chcsTcVo.setLschId("OCP");
				// Optional & Mandatory
				// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 chcsNo 가 웹 아이디가 됨.
				if (StringUtils.hasText(joinRequest.getLoginid())) {
					chcsTcVo.setChcsNo(joinRequest.getLoginid());
				} else {
					chcsTcVo.setChcsNo(""); // 필수 아님.
				}
				
				// As Is Legacy는 입력하면 오류 발생, 경로(030)
				// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 userPwdEc 가 웹 비밀번호가 됨.
				// 비밀번호 넣을 경우 채널코드 관계없이 "패스워드 자리수 오류" 발생
				// channel.setUserPwdEc(snsMemberInfoWithIdPassword.get("password").toString());
				if (StringUtils.hasText(joinRequest.getLoginpassword())) {
					chcsTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword()));
				} else {
					chcsTcVo.setUserPwdEc("");
				}

				// Optional
				if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
					chcsTcVo.setPrtnNm(joinRequest.getJoinPrtnNm());
				} else {
					chcsTcVo.setPrtnNm(config.getJoinPrtnName(joinRequest.getChcd()));
				}

				createCustVo.addChannel(chcsTcVo);
			}
			
			// 수신 동의 처리 시 해당 경로시스템의 수신 동의 여부가 없을 경우 N 으로 처리 2021-05-20 hjw0228
			// -> 수신 동의 여부가 없고 isMarketingSyncBpEnable 가 true 면 030 채널 수신동의 여부와 동기화 2022-01-12 hjw0228
			// 브랜드 사이트의 경우 처리하지 않음 2021-08-03 hjw0228
			if(joinMarketings.size() == 1 && !channel.getChCd().equals(joinMarketings.get(0).getChCd()) && !config.isBrandSite(channel.getChCd(), profile) && !isMembership) {
				CicuemCuOptiCsTcVo marketing = new CicuemCuOptiCsTcVo();
				
				// Mandatory
				marketing.setChCd(channel.getChCd());
				// Optional
				// isMarketingSyncBpEnable 면 000 채널 수신동의 여부와 동기화
				if(config.isMarketingSyncBpEnable(channel.getChCd(), profile)) {
					// join-on 가입하는 API에 수신동의 항목 추가
					if (joinMarketings != null && !joinMarketings.isEmpty()) {
						for (Marketing joinMarketing : joinMarketings) {
							if ("000".equals(joinMarketing.getChCd())) {
								marketing.setSmsOptiYn(joinMarketing.getSmsAgree()); // SMS수신동의여부
							}
						}
					}
					
					marketing.setSmsOptiDt(DateUtil.getCurrentDate()); // SMS수신동의일자
				} else {
					marketing.setSmsOptiYn("N"); // SMS수신동의여부
					marketing.setSmsOptiDt(DateUtil.getCurrentDate()); // SMS수신동의일자
				}
				
				marketing.setEmlOptiYn("N"); // 이메일수신동의여부
				marketing.setEmlOptiDt(DateUtil.getCurrentDate());
				marketing.setDmOptiYn("N"); // DM수신동의여부
				marketing.setDmOptiDt(DateUtil.getCurrentDate());
				marketing.setTmOptiYn("N"); // TM수신동의여부
				marketing.setTmOptiDt(DateUtil.getCurrentDate());
				marketing.setIntlOptiYn("N"); // 알림톡수신동의여부
				marketing.setIntlOptiDt(DateUtil.getCurrentDate());
				marketing.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
				marketing.setKkoIntlOptiDt(DateUtil.getCurrentDate());
				marketing.setFscrId("OCP");
				marketing.setLschId("OCP");
				createCustVo.addAgreeMarketing(marketing);
				
				// 경로 정보는 마케팅정보와 쌍이 맞지 않으면 오류 발생
				CicuedCuChCsTcVo chcsTcVo = new CicuedCuChCsTcVo();

				// Mandatory
				chcsTcVo.setChCd(channel.getChCd());
				chcsTcVo.setFscrId("OCP");
				chcsTcVo.setLschId("OCP");
				// Optional & Mandatory
				// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 chcsNo 가 웹 아이디가 됨.
				if (StringUtils.hasText(joinRequest.getLoginid())) {
					chcsTcVo.setChcsNo(joinRequest.getLoginid());
				} else {
					chcsTcVo.setChcsNo(""); // 필수 아님.
				}

				// As Is Legacy는 입력하면 오류 발생, 경로(030)
				// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 userPwdEc 가 웹 비밀번호가 됨.
				// 비밀번호 넣을 경우 채널코드 관계없이 "패스워드 자리수 오류" 발생
				// channel.setUserPwdEc(snsMemberInfoWithIdPassword.get("password").toString());
				if (StringUtils.hasText(joinRequest.getLoginpassword())) {
					chcsTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword()));
				} else {
					chcsTcVo.setUserPwdEc("");
				}

				// Optional
				if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
					chcsTcVo.setPrtnNm(joinRequest.getJoinPrtnNm());
				} else {
					chcsTcVo.setPrtnNm(config.getJoinPrtnName(joinRequest.getChcd()));
				}

				createCustVo.addChannel(chcsTcVo);
			}
			
			// 030을 포함한 6개 채널 동시 가입 처리 2021-08-04 hjw0228
			if (StringUtils.hasText(joinRequest.getLoginid()) /* && !"stg".equals(profile) */) { // 온라인 ID 입력여부 체크한 경우에만 030을 포함한 6개 채널 동시 가입 처리(stg환경 제외:뷰티포인트 없음)
				for(Object chCd : config.getBpChannelCodes()) {
					CicuemCuOptiCsTcVo marketing = new CicuemCuOptiCsTcVo();
					
					// Mandatory
					marketing.setChCd(chCd.toString());
					// Optional
					// join-on 가입하는 API에 수신동의 항목 추가
					if (joinMarketings != null && !joinMarketings.isEmpty()) {
						for (Marketing joinMarketing : joinMarketings) {
							if ("000".equals(joinMarketing.getChCd())) {
								marketing.setSmsOptiYn(joinMarketing.getSmsAgree()); // SMS수신동의여부
							}
						}
					}
					marketing.setSmsOptiDt(DateUtil.getCurrentDate()); // SMS수신동의일자
					marketing.setEmlOptiYn("N"); // 이메일수신동의여부
					marketing.setEmlOptiDt(DateUtil.getCurrentDate());
					marketing.setDmOptiYn("N"); // DM수신동의여부
					marketing.setDmOptiDt(DateUtil.getCurrentDate());
					marketing.setTmOptiYn("N"); // TM수신동의여부
					marketing.setTmOptiDt(DateUtil.getCurrentDate());
					marketing.setIntlOptiYn("N"); // 알림톡수신동의여부
					marketing.setIntlOptiDt(DateUtil.getCurrentDate());
					marketing.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
					marketing.setKkoIntlOptiDt(DateUtil.getCurrentDate());
					marketing.setFscrId("OCP");
					marketing.setLschId("OCP");
					createCustVo.addAgreeMarketing(marketing);
					
					// 경로 정보는 마케팅정보와 쌍이 맞지 않으면 오류 발생
					CicuedCuChCsTcVo chcsTcVo = new CicuedCuChCsTcVo();

					// Mandatory
					chcsTcVo.setChCd(chCd.toString());
					chcsTcVo.setFscrId("OCP");
					chcsTcVo.setLschId("OCP");
					// Optional & Mandatory
					// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 chcsNo 가 웹 아이디가 됨.
					if (StringUtils.hasText(joinRequest.getLoginid())) {
						chcsTcVo.setChcsNo(joinRequest.getLoginid());
					} else {
						chcsTcVo.setChcsNo(""); // 필수 아님.
					}

					// As Is Legacy는 입력하면 오류 발생, 경로(030)
					// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 userPwdEc 가 웹 비밀번호가 됨.
					// 비밀번호 넣을 경우 채널코드 관계없이 "패스워드 자리수 오류" 발생
					// channel.setUserPwdEc(snsMemberInfoWithIdPassword.get("password").toString());
					if (StringUtils.hasText(joinRequest.getLoginpassword())) {
						chcsTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword()));
					} else {
						chcsTcVo.setUserPwdEc("");
					}

					// Optional
					if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
						chcsTcVo.setPrtnNm(joinRequest.getJoinPrtnNm());
					} else {
						chcsTcVo.setPrtnNm(config.getJoinPrtnName(joinRequest.getChcd()));
					}

					createCustVo.addChannel(chcsTcVo);
				}
			}
		}

		CicuedCuAddrTcVo address = new CicuedCuAddrTcVo();
		// Mandatory
		address.setFscrId("OCP");
		address.setLschId("OCP");

		// Optional
		address.setZip("");
		address.setBscsAddr("");
		address.setDtlAddr("");
		address.setAddrChcYn("");
		address.setAdclCd("");
		address.setAdocCd("");
		address.setBlmnNo("");
		address.setAddrXcrd("");
		address.setAddrYcrd("");
		createCustVo.addAddress(address);
		return createCustVo;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 오프라인 통합고객 데이터 생성
	 * 
	 * "이니스프리" 매장코드 및 매장명
	 * 오프라인인 경우 온라인 13000001 이니스프리쇼핑몰
	 * 
	 * "에스쁘아"  매장코드 및 매장명
	 * 오프라인인 경우 온라인 11000494 에스쁘아쇼핑몰
	 * author   : takkies
	 * date     : 2020. 11. 26. 오후 8:28:14
	 * </pre>
	 * @param joinRequest
	 * @param channel
	 * @return
	 */
	public static CreateCustVO buildIntegratedOfflineCreateCustomerData(JoinRequest joinRequest, Channel channel) {
		
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;

		joinRequest.setIncsno(null);
		CreateCustVO createCustVo = new CreateCustVO();
		// Mandatory
		createCustVo.setCustNm(joinRequest.getUnm());
		createCustVo.setAthtDtbr(joinRequest.getBirth());
		createCustVo.setFrclCd(joinRequest.getNational()); // 내국인K, 외국인F

		if (joinRequest.getGender().equals("1")) {
			createCustVo.setSxclCd("M"); // 여성F, 남성M
		} else if (joinRequest.getGender().equals("0")) {
			createCustVo.setSxclCd("F"); // 여성F, 남성M
		} else {
			createCustVo.setSxclCd(joinRequest.getGender()); // 여성F, 남성M
		}

		if (StringUtils.hasText(joinRequest.getPhone())) {
			String phones[] = StringUtil.splitMobile(joinRequest.getPhone());
			if (phones != null && phones.length == 3) {
				createCustVo.setCellTidn(phones[0]); // 휴대폰식별전화번호
				createCustVo.setCellTexn(phones[1]); // 휴대폰국전화번호
				createCustVo.setCellTlsn(phones[2]); // 휴대폰끝전화번호
			}
		}
		createCustVo.setCiNo(joinRequest.getCi());
		// createCustVo.setJoinChCd(joinRequest.getChcd()); // 가입경로구분코드 -> 아모레 브랜드 사이트의 경우 joinChCd eq null ? 030 : joinChCd 으로 변경 2021-08-04
		if(config.isBrandSite(joinRequest.getChcd(), profile)) {
			createCustVo.setJoinChCd(config.getJoinChCd(joinRequest.getChcd(), profile));
			createCustVo.setNewCustJoinRqSiteCd(joinRequest.getChcd()); // 아모레 브랜드 사이트의 경우 신규고객가입요청사이트코드 값 추가 2021-08-31
		} else {
			createCustVo.setJoinChCd(joinRequest.getChcd());
		}

		if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
			createCustVo.setJoinPrtnId(joinRequest.getJoinPrtnId()); // 가입경로의 매장코드
		} else {
			createCustVo.setJoinPrtnId(config.getJoinPrtnCode(joinRequest.getChcd())); // 필수, 거래처 명
		}
		
		createCustVo.setAtclCd(""); // COM_CAT_CD:10139 (10:소유, 20:점유, 30:조정)
		createCustVo.setFscrId("OCP");
		createCustVo.setLschId("OCP");
		// Optional
		createCustVo.setPmdcCd("");
		createCustVo.setHomeZip("");
		createCustVo.setHomeBscsAddr("");
		createCustVo.setHomeDtlAddr("");
		createCustVo.setCustEmid("");
		createCustVo.setCustEmdn("");
		// COM_CAT_CD : 10155 (1:실명인증성공, 2:실명인증실패, 3: 해당자료없음, 4:통신오류, 5:체크썸오류, 50:명의도용차단가입자)
		createCustVo.setRnarCd("1");
		createCustVo.setRnmAthtDt("");
		createCustVo.setOfcBscsAddr("");
		createCustVo.setOfcDtlAddr("");
		createCustVo.setOfcZip("");
		createCustVo.setAddrXcrd("");
		createCustVo.setAddrYcrd("");
		createCustVo.setRfrsCd("");
		createCustVo.setBlmnNo("");
		createCustVo.setAdclCd("");
		createCustVo.setAdocCd("");
		createCustVo.setDrccCd("");
		createCustVo.setDrcsRegDt("");
		
		// 가입디바이스코드
		if (StringUtils.hasText(joinRequest.getDeviceType())) {
			createCustVo.setJndvCd(joinRequest.getDeviceType());// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)
		} else {
			createCustVo.setJndvCd("W"); 
		}
		
		// 고객가입디바이스운영체제구분코드
		if (StringUtils.hasText(joinRequest.getOsType())) {
			createCustVo.setCustJndvOsClCd(joinRequest.getOsType()); // 01: Android, 02 : iOS, 03 : MAC, 04 : WINDOWS
		}
		
		// 경로고객통합전환가입여부
		if (StringUtils.hasText(joinRequest.getSwitchJoinYn())) {
			createCustVo.setChcsIntgSwtJoinYn(joinRequest.getSwitchJoinYn());
		} else {
			createCustVo.setChcsIntgSwtJoinYn("N");
		}
		
		// SNS계정제휴사코드
		if (StringUtils.hasText(joinRequest.getSnsIdPrcnCd())) {
			createCustVo.setSnsIdPrcnCd(joinRequest.getSnsIdPrcnCd());
		}
		
		createCustVo.setSmsNum("");
		CicuemCuAdtInfTcVo adt = new CicuemCuAdtInfTcVo();

		// Mandatory
		adt.setSlccCd("S");
		adt.setPsnDtbr(joinRequest.getBirth());
		adt.setFscrId("OCP");
		adt.setLschId("OCP");
		// Optional
		adt.setWdanDt("");
		adt.setJobCd("");
		adt.setFscrId("OCP");
		adt.setLschId("OCP");

		createCustVo.setCicuemCuAdtInfTcVo(adt);
		// 뷰티포인트 약관 동의
		List<Terms> joinBpTerms = joinRequest.getBpterms();
		if (joinBpTerms != null && !joinBpTerms.isEmpty()) {
			for (Terms joinTerm : joinBpTerms) {
				CicuedCuTncaTcVo terms = new CicuedCuTncaTcVo();
				terms.setTcatCd(joinTerm.getTcatCd());
				terms.setTncvNo(joinTerm.getTncvNo());
				terms.setFscrId("OCP");
				terms.setLschId("OCP");
				terms.setTncAgrYn(joinTerm.getTncAgrYn());
				terms.setTncaDttm(joinTerm.getTncaChgDt());
				createCustVo.addTerms(terms);
			}
		}
		// 경로 약관 - 경로 약관은 옴니에만 등록
		List<Terms> joinTerms = joinRequest.getTerms();
		if (joinTerms != null && !joinTerms.isEmpty()) {
			for (Terms joinTerm : joinTerms) {
				CicuedCuTncaTcVo terms = new CicuedCuTncaTcVo();
				terms.setTcatCd(joinTerm.getTcatCd());
				terms.setTncvNo(joinTerm.getTncvNo());
				terms.setFscrId("OCP");
				terms.setLschId("OCP");
				terms.setTncAgrYn(joinTerm.getTncAgrYn());
				terms.setTncaDttm(joinTerm.getTncaChgDt());
				// createCustVo.addTerms(terms);
			}
		}
		// 수신 동의 처리
		List<Marketing> joinMarketings = joinRequest.getMarketings();
		if (joinMarketings != null && !joinMarketings.isEmpty()) {
			for (Marketing joinMarketing : joinMarketings) {

				CicuemCuOptiCsTcVo marketing = new CicuemCuOptiCsTcVo();
				// Mandatory
				marketing.setChCd(joinMarketing.getChCd());
				// Optional
				marketing.setSmsOptiYn(joinMarketing.getSmsAgree()); // SMS수신동의여부
				marketing.setSmsOptiDt(DateUtil.getCurrentDate()); // SMS수신동의일자
				marketing.setEmlOptiYn("N"); // 이메일수신동의여부
				marketing.setEmlOptiDt(DateUtil.getCurrentDate());
				marketing.setDmOptiYn("N"); // DM수신동의여부
				marketing.setDmOptiDt(DateUtil.getCurrentDate());
				marketing.setTmOptiYn("N"); // TM수신동의여부
				marketing.setTmOptiDt(DateUtil.getCurrentDate());
				marketing.setIntlOptiYn("N"); // 알림톡수신동의여부
				marketing.setIntlOptiDt(DateUtil.getCurrentDate());
				marketing.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
				marketing.setKkoIntlOptiDt(DateUtil.getCurrentDate());
				marketing.setFscrId("OCP");
				marketing.setLschId("OCP");
				createCustVo.addAgreeMarketing(marketing);

				// 경로 정보는 마케팅정보와 쌍이 맞지 않으면 오류 발생
				CicuedCuChCsTcVo chcsTcVo = new CicuedCuChCsTcVo();

				// Mandatory
				chcsTcVo.setChCd(joinMarketing.getChCd());
				chcsTcVo.setFscrId("OCP");
				chcsTcVo.setLschId("OCP");
				// Optional & Mandatory
				// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 chcsNo 가 웹 아이디가 됨.
				if (StringUtils.hasText(joinRequest.getLoginid())) {
					chcsTcVo.setChcsNo(joinRequest.getLoginid());
				} else {
					chcsTcVo.setChcsNo(""); // 필수 아님.
				}

				// As Is Legacy는 입력하면 오류 발생, 경로(030)
				// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 userPwdEc 가 웹 비밀번호가 됨.
				// 비밀번호 넣을 경우 채널코드 관계없이 "패스워드 자리수 오류" 발생
				// channel.setUserPwdEc(snsMemberInfoWithIdPassword.get("password").toString());
				if (StringUtils.hasText(joinRequest.getLoginpassword())) {
					chcsTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword()));
				} else {
					chcsTcVo.setUserPwdEc("");
				}

				// Optional
				if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
					chcsTcVo.setPrtnNm(joinRequest.getJoinPrtnNm());
				} else {
					chcsTcVo.setPrtnNm(config.getJoinPrtnName(joinRequest.getChcd()));
				}

				createCustVo.addChannel(chcsTcVo);
			}
			
			// 수신 동의 처리 시 해당 경로시스템의 수신 동의 여부가 없을 경우 N 으로 처리 2021-05-20 hjw0228
			// 브랜드 사이트의 경우 처리하지 않음 2021-08-03 hjw0228
			if(joinMarketings.size() == 1 && !channel.getChCd().equals(joinMarketings.get(0).getChCd()) && !config.isBrandSite(channel.getChCd(), profile)) {
				CicuemCuOptiCsTcVo marketing = new CicuemCuOptiCsTcVo();
				// Mandatory
				marketing.setChCd(channel.getChCd());
				// Optional
				marketing.setSmsOptiYn("N"); // SMS수신동의여부
				marketing.setSmsOptiDt(DateUtil.getCurrentDate()); // SMS수신동의일자
				marketing.setEmlOptiYn("N"); // 이메일수신동의여부
				marketing.setEmlOptiDt(DateUtil.getCurrentDate());
				marketing.setDmOptiYn("N"); // DM수신동의여부
				marketing.setDmOptiDt(DateUtil.getCurrentDate());
				marketing.setTmOptiYn("N"); // TM수신동의여부
				marketing.setTmOptiDt(DateUtil.getCurrentDate());
				marketing.setIntlOptiYn("N"); // 알림톡수신동의여부
				marketing.setIntlOptiDt(DateUtil.getCurrentDate());
				marketing.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
				marketing.setKkoIntlOptiDt(DateUtil.getCurrentDate());
				marketing.setFscrId("OCP");
				marketing.setLschId("OCP");
				createCustVo.addAgreeMarketing(marketing);
				
				// 경로 정보는 마케팅정보와 쌍이 맞지 않으면 오류 발생
				CicuedCuChCsTcVo chcsTcVo = new CicuedCuChCsTcVo();

				// Mandatory
				chcsTcVo.setChCd(channel.getChCd());
				chcsTcVo.setFscrId("OCP");
				chcsTcVo.setLschId("OCP");
				// Optional & Mandatory
				// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 chcsNo 가 웹 아이디가 됨.
				if (StringUtils.hasText(joinRequest.getLoginid())) {
					chcsTcVo.setChcsNo(joinRequest.getLoginid());
				} else {
					chcsTcVo.setChcsNo(""); // 필수 아님.
				}

				// As Is Legacy는 입력하면 오류 발생, 경로(030)
				// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 userPwdEc 가 웹 비밀번호가 됨.
				// 비밀번호 넣을 경우 채널코드 관계없이 "패스워드 자리수 오류" 발생
				// channel.setUserPwdEc(snsMemberInfoWithIdPassword.get("password").toString());
				if (StringUtils.hasText(joinRequest.getLoginpassword())) {
					chcsTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword()));
				} else {
					chcsTcVo.setUserPwdEc("");
				}

				// Optional
				if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
					chcsTcVo.setPrtnNm(joinRequest.getJoinPrtnNm());
				} else {
					chcsTcVo.setPrtnNm(config.getJoinPrtnName(joinRequest.getChcd()));
				}

				createCustVo.addChannel(chcsTcVo);
			}
			
			// 030을 포함한 6개 채널 동시 가입 처리 2021-08-04 hjw0228
			if (StringUtils.hasText(joinRequest.getLoginid()) /* && !"stg".equals(profile) */) { // 온라인 ID 입력여부 체크한 경우에만 030을 포함한 6개 채널 동시 가입 처리(stg환경 제외:뷰티포인트 없음)
				for(Object chCd : config.getBpChannelCodes()) {
					CicuemCuOptiCsTcVo marketing = new CicuemCuOptiCsTcVo();
					
					// Mandatory
					marketing.setChCd(chCd.toString());
					// Optional
					// join-on 가입하는 API에 수신동의 항목 추가
					if (joinMarketings != null && !joinMarketings.isEmpty()) {
						for (Marketing joinMarketing : joinMarketings) {
							if ("000".equals(joinMarketing.getChCd())) {
								marketing.setSmsOptiYn(joinMarketing.getSmsAgree()); // SMS수신동의여부
							}
						}
					}
					marketing.setSmsOptiDt(DateUtil.getCurrentDate()); // SMS수신동의일자
					marketing.setEmlOptiYn("N"); // 이메일수신동의여부
					marketing.setEmlOptiDt(DateUtil.getCurrentDate());
					marketing.setDmOptiYn("N"); // DM수신동의여부
					marketing.setDmOptiDt(DateUtil.getCurrentDate());
					marketing.setTmOptiYn("N"); // TM수신동의여부
					marketing.setTmOptiDt(DateUtil.getCurrentDate());
					marketing.setIntlOptiYn("N"); // 알림톡수신동의여부
					marketing.setIntlOptiDt(DateUtil.getCurrentDate());
					marketing.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
					marketing.setKkoIntlOptiDt(DateUtil.getCurrentDate());
					marketing.setFscrId("OCP");
					marketing.setLschId("OCP");
					createCustVo.addAgreeMarketing(marketing);
					
					// 경로 정보는 마케팅정보와 쌍이 맞지 않으면 오류 발생
					CicuedCuChCsTcVo chcsTcVo = new CicuedCuChCsTcVo();

					// Mandatory
					chcsTcVo.setChCd(chCd.toString());
					chcsTcVo.setFscrId("OCP");
					chcsTcVo.setLschId("OCP");
					// Optional & Mandatory
					// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 chcsNo 가 웹 아이디가 됨.
					if (StringUtils.hasText(joinRequest.getLoginid())) {
						chcsTcVo.setChcsNo(joinRequest.getLoginid());
					} else {
						chcsTcVo.setChcsNo(""); // 필수 아님.
					}

					// As Is Legacy는 입력하면 오류 발생, 경로(030)
					// 고객가입시 사용한 매체 관리를 위한 코드(W:WEB, M:MOBILE, A:APP)가 Web 일 경우 userPwdEc 가 웹 비밀번호가 됨.
					// 비밀번호 넣을 경우 채널코드 관계없이 "패스워드 자리수 오류" 발생
					// channel.setUserPwdEc(snsMemberInfoWithIdPassword.get("password").toString());
					if (StringUtils.hasText(joinRequest.getLoginpassword())) {
						chcsTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword()));
					} else {
						chcsTcVo.setUserPwdEc("");
					}

					// Optional
					if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
						chcsTcVo.setPrtnNm(joinRequest.getJoinPrtnNm());
					} else {
						chcsTcVo.setPrtnNm(config.getJoinPrtnName(joinRequest.getChcd()));
					}

					createCustVo.addChannel(chcsTcVo);
				}
			}
		}

		CicuedCuAddrTcVo address = new CicuedCuAddrTcVo();
		// Mandatory
		address.setFscrId("OCP");
		address.setLschId("OCP");

		// Optional
		address.setZip("");
		address.setBscsAddr("");
		address.setDtlAddr("");
		address.setAddrChcYn("");
		address.setAdclCd("");
		address.setAdocCd("");
		address.setBlmnNo("");
		address.setAddrXcrd("");
		address.setAddrYcrd("");
		createCustVo.addAddress(address);
		return createCustVo;
	}
	

	/**
	 * 
	 * <pre>
	 * comment  : 고객통합 경로 등록
	 * 
	 * "이니스프리" 매장코드 및 매장명
	 * 온라인인 경우 온/오프 13000001 이니스프리쇼핑몰
	 * 오프라인인 경우 온라인 13000001 이니스프리쇼핑몰
	 * 
	 * "에스쁘아"  매장코드 및 매장명
	 * 온라인인 경우 온/오프 11000494 에스쁘아쇼핑몰
	 * 오프라인인 경우 온라인 11000494 에스쁘아쇼핑몰 
	 * author   : takkies
	 * date     : 2020. 9. 7. 오후 2:16:30
	 * </pre>
	 * 
	 * @param joinRequest
	 * @return
	 */
	public static CreateCustChannelRequest buildIntegratedOnlineChannelCustomerData(final OnOffline onoffline, final Channel channel, final JoinRequest joinRequest) {

		CreateCustChannelRequest createCustChannel = new CreateCustChannelRequest();

		CicuedCuChTcVo cicuedCuChTcVo = new CicuedCuChTcVo();
		// Mandatory
		cicuedCuChTcVo.setIncsNo(joinRequest.getIncsno()); // 필수
		cicuedCuChTcVo.setChCd(joinRequest.getChcd()); // 필수

		if (onoffline == OnOffline.Online) {

			if (StringUtils.hasText(joinRequest.getLoginid())) { // 경로에서 별도 관리되는 고객번호
				cicuedCuChTcVo.setChcsNo(joinRequest.getLoginid()); // 필수, 선택한 로그인 아이디, 경로에서 별도 관리되는 고객번호
			} else {
				cicuedCuChTcVo.setChcsNo(joinRequest.getIncsno());
			}
			
			// 뷰티멤버십 연동으로 회원 가입 시 chcsNo 값은 제휴사 membership ID 로 입력
			//final String isMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_MEMBERSHIP);
			//boolean isMembership = OmniUtil.isMembership(isMembershipSession);
//			if(isMembership) {
//				final MembershipUserInfo membershipUserInfo = (MembershipUserInfo) WebUtil.getSession(OmniConstants.MEMBERSHIP_USERINFO);
//				cicuedCuChTcVo.setChcsNo(membershipUserInfo.getMbrId());
//			}

			if (StringUtils.hasText(joinRequest.getLoginpassword())) { // 필수, 선택한 로그인 아이디 비밀번호, 웹경로일경우 필수
				cicuedCuChTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword()));
			}

			log.debug("buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", onoffline.name(), joinRequest.getJoinPrtnId(), joinRequest.getJoinPrtnNm());

			if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
				cicuedCuChTcVo.setFstCnttPrtnId(joinRequest.getJoinPrtnId()); // 필수, 최초접촉거래처ID
				log.debug("▶▶▶▶▶▶ buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", joinRequest.getChcd(), config.getJoinPrtnCode(joinRequest.getChcd()), config.getJoinPrtnName(joinRequest.getChcd()));
			} else {
				cicuedCuChTcVo.setFstCnttPrtnId(config.getJoinPrtnCode(joinRequest.getChcd())); // 필수, 최초접촉거래처ID
			}

			if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
				cicuedCuChTcVo.setPrtnNm(joinRequest.getJoinPrtnNm()); // 필수, 거래처 명
			} else {
				cicuedCuChTcVo.setPrtnNm(config.getJoinPrtnName(joinRequest.getChcd())); // 필수, 거래처 명
			}
		} else {
			// 온라인 가입 시 추가적으로 오프라인 가입
			if (StringUtils.hasText(joinRequest.getLoginid())) { // 경로에서 별도 관리되는 고객번호
				cicuedCuChTcVo.setChcsNo(joinRequest.getLoginid()); // 필수, 선택한 로그인 아이디, 경로에서 별도 관리되는 고객번호
			} else {
				cicuedCuChTcVo.setChcsNo(joinRequest.getIncsno());
			}

			log.debug("▶▶▶▶▶▶ buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", onoffline.name(), joinRequest.getJoinPrtnId(), joinRequest.getJoinPrtnNm());
			if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
				cicuedCuChTcVo.setFstCnttPrtnId(joinRequest.getJoinPrtnId()); // 필수, 최초접촉거래처ID
			} else {
				log.debug("▶▶▶▶▶▶ buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", joinRequest.getChcd(), config.getJoinPrtnCode(joinRequest.getChcd()), config.getJoinPrtnName(joinRequest.getChcd()));
				cicuedCuChTcVo.setFstCnttPrtnId(config.getJoinPrtnCode(joinRequest.getChcd())); // 필수, 최초접촉거래처ID
			}

			if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
				cicuedCuChTcVo.setPrtnNm(joinRequest.getJoinPrtnNm()); // 필수, 거래처 명
			} else {
				log.debug("▶▶▶▶▶▶ buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", joinRequest.getChcd(), config.getJoinPrtnCode(joinRequest.getChcd()), config.getJoinPrtnName(joinRequest.getChcd()));
				cicuedCuChTcVo.setPrtnNm(config.getJoinPrtnName(joinRequest.getChcd())); // 필수, 거래처 명
			}	
		}

		cicuedCuChTcVo.setFscrId("OCP");
		cicuedCuChTcVo.setLschId("OCP");

		CicuemCuOptiTcVo cicuemCuOptiTcVo = cicuedCuChTcVo.getCicuemCuOptiTcVo();
		String smsAgree = "";

		// 수신 동의 처리
		List<Marketing> joinMarketings = joinRequest.getMarketings();
		if (joinMarketings != null && !joinMarketings.isEmpty()) {
			for (Marketing joinMarketing : joinMarketings) {

				// 경로는 030 스킵
				if (OmniConstants.JOINON_CHCD.equals(joinMarketing.getChCd())) {
					continue;
				}
				
				// Mandatory
				// cicuemCuOptiTcVo.setChCd(joinMarketing.getChCd());
				cicuemCuOptiTcVo.setChCd(joinRequest.getChcd());

				// Mandatory
				cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수
				cicuemCuOptiTcVo.setSmsOptiYn(joinMarketing.getSmsAgree()); // 필수 SMS수신동의여부
				smsAgree = joinMarketing.getSmsAgree();
				cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // 필수 SMS수신동의일자
				cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수 이메일수신동의여부
				cicuemCuOptiTcVo.setDmOptiYn("N"); // 필수 DM수신동의여부
				cicuemCuOptiTcVo.setTmOptiYn("N"); // 필수 TM수신동의여부
				cicuemCuOptiTcVo.setIntlOptiYn("N"); // 필수 알림톡수신동의여부
				cicuemCuOptiTcVo.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
				cicuemCuOptiTcVo.setFscrId("OCP");
				cicuemCuOptiTcVo.setLschId("OCP");
				// Optional
				cicuemCuOptiTcVo.setEmlOptiDt("");
				cicuemCuOptiTcVo.setDmOptiDt("");
				cicuemCuOptiTcVo.setTmOptiDt("");
				cicuemCuOptiTcVo.setIntlOptiDt("");
				cicuemCuOptiTcVo.setKkoIntlOptiDt("");
				cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);
			}
		} else { // 수신 동의 처리 시 해당 경로시스템의 수신 동의 여부가 없을 경우 N 으로 처리 2021-06-10 hjw0228
				 // -> 수신 동의 여부가 없고 isMarketingSyncBpEnable 가 true 면 030 채널 수신동의 여부와 동기화 2022-01-12 hjw0228
			// 브랜드 사이트의 경우 처리하지 않음 2021-08-03 hjw0228
			String profile = systemInfo.getActiveProfiles()[0];
			profile = StringUtils.isEmpty(profile) ? "dev" : profile;
			profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
			if(!config.isBrandSite(channel.getChCd(), profile)) {
				
				cicuemCuOptiTcVo.setChCd(joinRequest.getChcd());
				
				// isMarketingSyncBpEnable 면 030 채널 수신동의 여부와 동기화
				if(config.isMarketingSyncBpEnable(channel.getChCd(), profile)) {
					CuoptiVo cuoptiVo = new CuoptiVo();
					cuoptiVo.setIncsNo(joinRequest.getIncsno());
					
					log.debug("CuoptiVo ▶▶▶▶▶▶ {}", StringUtil.printJson(cuoptiVo));
					CuoptiResponse cuoptiResponse = customerApiService.getCicuemcuoptiList(cuoptiVo);
					
					if(!"ICITSVCOM999".equals(cuoptiResponse.getRsltCd())) {
						CicuemCuOptiQcVo cicuemCuOptiQc = new CicuemCuOptiQcVo();
						
						// 030 채널 마케팅 정보 수신 동의 여부로 값 세팅 
						for(CicuemCuOptiQcVo cicuemCuOptiQcVo : cuoptiResponse.getCicuemCuOptiQcVo()) {
							if("030".equals(cicuemCuOptiQcVo.getChCd())) { 
								cicuemCuOptiQc = cicuemCuOptiQcVo; 
							} 
						}
						
						// 값이 없으면 N 처리
						final String empOptiYn = StringUtils.isEmpty(cicuemCuOptiQc.getEmlOptiYn()) ? "N" : cicuemCuOptiQc.getEmlOptiYn();
						final String smsOptiYn = StringUtils.isEmpty(cicuemCuOptiQc.getSmsOptiYn()) ? "N" : cicuemCuOptiQc.getSmsOptiYn();
						final String dmOptiYn = StringUtils.isEmpty(cicuemCuOptiQc.getDmOptiYn()) ? "N" : cicuemCuOptiQc.getDmOptiYn();
						final String tmOptiYn = StringUtils.isEmpty(cicuemCuOptiQc.getTmOptiYn()) ? "N" : cicuemCuOptiQc.getTmOptiYn();
						final String intlOptiYn = StringUtils.isEmpty(cicuemCuOptiQc.getIntlOptiYn()) ? "N" : cicuemCuOptiQc.getIntlOptiYn();
						final String kkoIntlOptiYn = StringUtils.isEmpty(cicuemCuOptiQc.getKkoIntlOptiYn()) ? "N" : cicuemCuOptiQc.getKkoIntlOptiYn();
						
						// Mandatory
						cicuemCuOptiTcVo.setEmlOptiYn(empOptiYn); // 필수
						cicuemCuOptiTcVo.setSmsOptiYn(smsOptiYn); // 필수 SMS수신동의여부
						cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // 필수 SMS수신동의일자
						cicuemCuOptiTcVo.setDmOptiYn(dmOptiYn); // 필수 DM수신동의여부
						cicuemCuOptiTcVo.setTmOptiYn(tmOptiYn); // 필수 TM수신동의여부
						cicuemCuOptiTcVo.setIntlOptiYn(intlOptiYn); // 필수 알림톡수신동의여부
						cicuemCuOptiTcVo.setKkoIntlOptiYn(kkoIntlOptiYn); // 카카오 알림톡수신동의여부
						cicuemCuOptiTcVo.setFscrId("OCP");
						cicuemCuOptiTcVo.setLschId("OCP");
						// Optional
						cicuemCuOptiTcVo.setEmlOptiDt(DateUtil.getCurrentDate());
						cicuemCuOptiTcVo.setDmOptiDt(DateUtil.getCurrentDate());
						cicuemCuOptiTcVo.setTmOptiDt(DateUtil.getCurrentDate());
						cicuemCuOptiTcVo.setIntlOptiDt(DateUtil.getCurrentDate());
						cicuemCuOptiTcVo.setKkoIntlOptiDt(DateUtil.getCurrentDate());
					}
				} else {
					// Mandatory
					cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수
					cicuemCuOptiTcVo.setSmsOptiYn("N"); // 필수 SMS수신동의여부
					cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // 필수 SMS수신동의일자
					cicuemCuOptiTcVo.setDmOptiYn("N"); // 필수 DM수신동의여부
					cicuemCuOptiTcVo.setTmOptiYn("N"); // 필수 TM수신동의여부
					cicuemCuOptiTcVo.setIntlOptiYn("N"); // 필수 알림톡수신동의여부
					cicuemCuOptiTcVo.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
					cicuemCuOptiTcVo.setFscrId("OCP");
					cicuemCuOptiTcVo.setLschId("OCP");
					// Optional
					cicuemCuOptiTcVo.setEmlOptiDt("");
					cicuemCuOptiTcVo.setDmOptiDt("");
					cicuemCuOptiTcVo.setTmOptiDt("");
					cicuemCuOptiTcVo.setIntlOptiDt("");
					cicuemCuOptiTcVo.setKkoIntlOptiDt("");
				}
				cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);	
			}
		}

		createCustChannel.addCicuedCuChTcVo(cicuedCuChTcVo);
		
		// 030을 포함한 6개 채널 동시 가입 처리 2021-10-26 hjw0228
		/*
		 * String profile = systemInfo.getActiveProfiles()[0]; profile = StringUtils.isEmpty(profile) ? "dev" : profile; profile =
		 * profile.equalsIgnoreCase("default") ? "dev" : profile; if (onoffline == OnOffline.Online && StringUtils.hasText(joinRequest.getLoginid())
		 * && !"stg".equals(profile) && !"prod".equals(profile)) { // 온라인 ID 입력여부 체크한 경우에만 030을 포함한 6개 채널 동시 가입 처리(stg환경 제외:뷰티포인트 없음) CuoptiVo
		 * cuoptiVo = new CuoptiVo(); cuoptiVo.setIncsNo(joinRequest.getIncsno()); CipAthtVo cipAthtVo = new CipAthtVo(); cipAthtVo.setChCd("000");
		 * cipAthtVo.setSysCd("OCP"); cuoptiVo.setCipAthtVo(cipAthtVo);
		 * 
		 * log.debug("CuoptiVo ▶▶▶▶▶▶ {}", StringUtil.printJson(cuoptiVo)); CuoptiResponse cuoptiResponse =
		 * customerApiService.getCicuemcuoptiList(cuoptiVo);
		 * 
		 * if(!"ICITSVCOM999".equals(cuoptiResponse.getRsltCd())) { CicuemCuOptiQcVo cicuemCuOptiQc = new CicuemCuOptiQcVo();
		 * 
		 * // 000 채널 마케팅 정보 수신 동의 여부로 값 세팅 for(CicuemCuOptiQcVo cicuemCuOptiQcVo : cuoptiResponse.getCicuemCuOptiQcVo()) {
		 * if("000".equals(cicuemCuOptiQcVo.getChCd())) { cicuemCuOptiQc = cicuemCuOptiQcVo; } }
		 * 
		 * if(cicuemCuOptiQc != null && "000".equals(cicuemCuOptiQc.getChCd())) { for (Object chCd : config.getBpChannelCodes()) { cicuedCuChTcVo =
		 * new CicuedCuChTcVo(); // Mandatory cicuedCuChTcVo.setIncsNo(joinRequest.getIncsno()); // 필수 cicuedCuChTcVo.setChCd(chCd.toString()); //
		 * 필수
		 * 
		 * if (StringUtils.hasText(joinRequest.getLoginid())) { // 경로에서 별도 관리되는 고객번호 cicuedCuChTcVo.setChcsNo(joinRequest.getLoginid()); // 필수, 선택한
		 * 로그인 아이디, 경로에서 별도 관리되는 고객번호 } else { cicuedCuChTcVo.setChcsNo(joinRequest.getIncsno()); }
		 * 
		 * if (StringUtils.hasText(joinRequest.getLoginpassword())) { // 필수, 선택한 로그인 아이디 비밀번호, 웹경로일경우 필수
		 * cicuedCuChTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword())); }
		 * 
		 * log.debug("buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", onoffline.name(), joinRequest.getJoinPrtnId(),
		 * joinRequest.getJoinPrtnNm());
		 * 
		 * // 오프라인 가입 시는 파라미터로 전송됨. if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
		 * cicuedCuChTcVo.setFstCnttPrtnId(joinRequest.getJoinPrtnId()); // 필수, 최초접촉거래처ID } else { cicuedCuChTcVo.setFstCnttPrtnId(""); // 필수,
		 * 최초접촉거래처ID }
		 * 
		 * cicuedCuChTcVo.setPrtnNm(config.getBpChannelName(chCd.toString())); // 필수, 거래처 명
		 * 
		 * cicuedCuChTcVo.setFscrId("OCP"); cicuedCuChTcVo.setLschId("OCP");
		 * 
		 * cicuemCuOptiTcVo = cicuedCuChTcVo.getCicuemCuOptiTcVo();
		 * 
		 * cicuemCuOptiTcVo.setChCd(chCd.toString());
		 * 
		 * // Mandatory cicuemCuOptiTcVo.setEmlOptiYn(cicuemCuOptiQc.getEmlOptiYn()); // 필수 이메일수신동의여부
		 * cicuemCuOptiTcVo.setEmlOptiDt(cicuemCuOptiQc.getEmlOptiDt()); // 필수 이메일수신동의일자
		 * cicuemCuOptiTcVo.setSmsOptiYn(cicuemCuOptiQc.getSmsOptiYn()); // 필수 SMS수신동의여부
		 * cicuemCuOptiTcVo.setSmsOptiDt(cicuemCuOptiQc.getSmsOptiDt()); // 필수 SMS수신동의일자 cicuemCuOptiTcVo.setDmOptiYn(cicuemCuOptiQc.getDmOptiYn());
		 * // 필수 DM수신동의여부 cicuemCuOptiTcVo.setDmOptiDt(cicuemCuOptiQc.getDmOptiDt()); // 필수 DM수신동의일자
		 * cicuemCuOptiTcVo.setTmOptiYn(cicuemCuOptiQc.getTmOptiYn()); // 필수 TM수신동의여부 cicuemCuOptiTcVo.setTmOptiDt(cicuemCuOptiQc.getTmOptiDt()); //
		 * 필수 TM수신동의일자 cicuemCuOptiTcVo.setIntlOptiYn(cicuemCuOptiQc.getIntlOptiYn()); // 필수 알림톡수신동의여부
		 * cicuemCuOptiTcVo.setIntlOptiDt(cicuemCuOptiQc.getIntlOptiDt()); // 필수 알림톡수신동의일자
		 * cicuemCuOptiTcVo.setKkoIntlOptiYn(cicuemCuOptiQc.getKkoIntlOptiYn()); // 카카오 알림톡수신동의여부
		 * cicuemCuOptiTcVo.setKkoIntlOptiDt(cicuemCuOptiQc.getKkoIntlOptiDt()); // 카카오 알림톡수신동의여부
		 * 
		 * cicuemCuOptiTcVo.setFscrId("OCP"); cicuemCuOptiTcVo.setLschId("OCP");
		 * 
		 * cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);
		 * 
		 * createCustChannel.addCicuedCuChTcVo(cicuedCuChTcVo); } } } }
		 */
		
		// 오설록 Mall을 통해 회원 가입 시 오설록 백화점도 강제 가입 처리
		String joinChCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		if(OmniConstants.OSULLOC_CHCD.equals(joinChCd) && OmniConstants.OSULLOC_OFFLINE_CHCD.equals(channel.getChCd())) {
			cicuedCuChTcVo = new CicuedCuChTcVo();
			// Mandatory
			cicuedCuChTcVo.setIncsNo(joinRequest.getIncsno()); // 필수
			cicuedCuChTcVo.setChCd(OmniConstants.OSULLOC_DEPARTMENT_CHCD); // 필수
			
			// 온라인 가입 시 추가적으로 오프라인 가입
			if (StringUtils.hasText(joinRequest.getLoginid())) { // 경로에서 별도 관리되는 고객번호
				cicuedCuChTcVo.setChcsNo(joinRequest.getLoginid()); // 필수, 선택한 로그인 아이디, 경로에서 별도 관리되는 고객번호
			} else {
				cicuedCuChTcVo.setChcsNo(joinRequest.getIncsno());
			}

			log.debug("▶▶▶▶▶▶ buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", onoffline.name(), joinRequest.getJoinPrtnId(), joinRequest.getJoinPrtnNm());
			if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
				cicuedCuChTcVo.setFstCnttPrtnId(joinRequest.getJoinPrtnId()); // 필수, 최초접촉거래처ID
			} else {
				log.debug("▶▶▶▶▶▶ buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", joinRequest.getChcd(), config.getJoinPrtnCode(joinRequest.getChcd()), config.getJoinPrtnName(joinRequest.getChcd()));
				cicuedCuChTcVo.setFstCnttPrtnId(config.getJoinPrtnCode(joinRequest.getChcd())); // 필수, 최초접촉거래처ID
			}

			if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
				cicuedCuChTcVo.setPrtnNm(joinRequest.getJoinPrtnNm()); // 필수, 거래처 명
			} else {
				log.debug("▶▶▶▶▶▶ buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", joinRequest.getChcd(), config.getJoinPrtnCode(joinRequest.getChcd()), config.getJoinPrtnName(joinRequest.getChcd()));
				cicuedCuChTcVo.setPrtnNm(config.getJoinPrtnName(joinRequest.getChcd())); // 필수, 거래처 명
			}
			
			cicuedCuChTcVo.setFscrId("OCP");
			cicuedCuChTcVo.setLschId("OCP");
			
			cicuemCuOptiTcVo = cicuedCuChTcVo.getCicuemCuOptiTcVo();
			
			cicuemCuOptiTcVo.setChCd(OmniConstants.OSULLOC_DEPARTMENT_CHCD);

			// Mandatory
			cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수
			cicuemCuOptiTcVo.setSmsOptiYn(smsAgree); // 필수 SMS수신동의여부
			cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // 필수 SMS수신동의일자
			cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수 이메일수신동의여부
			cicuemCuOptiTcVo.setDmOptiYn("N"); // 필수 DM수신동의여부
			cicuemCuOptiTcVo.setTmOptiYn("N"); // 필수 TM수신동의여부
			cicuemCuOptiTcVo.setIntlOptiYn("N"); // 필수 알림톡수신동의여부
			cicuemCuOptiTcVo.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
			cicuemCuOptiTcVo.setFscrId("OCP");
			cicuemCuOptiTcVo.setLschId("OCP");
			// Optional
			cicuemCuOptiTcVo.setEmlOptiDt("");
			cicuemCuOptiTcVo.setDmOptiDt("");
			cicuemCuOptiTcVo.setTmOptiDt("");
			cicuemCuOptiTcVo.setIntlOptiDt("");
			cicuemCuOptiTcVo.setKkoIntlOptiDt("");
			cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);
			
			createCustChannel.addCicuedCuChTcVo(cicuedCuChTcVo);
		}

		return createCustChannel;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 오프라인 고객통합 경로 가입
	 * 오프라인은 고객통합에서 하므로 온라인만 함
	 * 
	 * "이니스프리" 매장코드 및 매장명
	 * 오프라인인 경우 온라인 13000001 이니스프리쇼핑몰
	 * 
	 * "에스쁘아"  매장코드 및 매장명
	 * 오프라인인 경우 온라인 11000494 에스쁘아쇼핑몰 
	 * author   : takkies
	 * date     : 2020. 11. 26. 오후 8:33:55
	 * </pre>
	 * @param onoffline
	 * @param channel
	 * @param joinRequest
	 * @return
	 */
	public static CreateCustChannelRequest buildIntegratedOfflineChannelCustomerData(final OnOffline onoffline, final Channel channel, final JoinRequest joinRequest) {

		CreateCustChannelRequest createCustChannel = new CreateCustChannelRequest();

		CicuedCuChTcVo cicuedCuChTcVo = new CicuedCuChTcVo();
		// Mandatory
		cicuedCuChTcVo.setIncsNo(joinRequest.getIncsno()); // 필수
		cicuedCuChTcVo.setChCd(joinRequest.getChcd()); // 필수

		if (StringUtils.hasText(joinRequest.getLoginid())) { // 경로에서 별도 관리되는 고객번호
			cicuedCuChTcVo.setChcsNo(joinRequest.getLoginid()); // 필수, 선택한 로그인 아이디, 경로에서 별도 관리되는 고객번호
		} else {
			cicuedCuChTcVo.setChcsNo(joinRequest.getIncsno());
		}

		if (StringUtils.hasText(joinRequest.getLoginpassword())) { // 필수, 선택한 로그인 아이디 비밀번호, 웹경로일경우 필수
			cicuedCuChTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword()));
		}

		log.debug("buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", onoffline.name(), joinRequest.getJoinPrtnId(), joinRequest.getJoinPrtnNm());

		if (onoffline == OnOffline.Online) {

			cicuedCuChTcVo.setFstCnttPrtnId(config.getJoinPrtnCode(joinRequest.getChcd())); // 필수, 최초접촉거래처ID
			cicuedCuChTcVo.setPrtnNm(config.getJoinPrtnName(joinRequest.getChcd())); // 필수, 거래처 명
			
		} else {
			// 오프라인 가입 시는 파라미터로 전송됨.
			if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
				cicuedCuChTcVo.setFstCnttPrtnId(joinRequest.getJoinPrtnId()); // 필수, 최초접촉거래처ID
			} else {
				cicuedCuChTcVo.setFstCnttPrtnId(""); // 필수, 최초접촉거래처ID
			}

			if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
				cicuedCuChTcVo.setPrtnNm(joinRequest.getJoinPrtnNm()); // 필수, 거래처 명
			} else {
				cicuedCuChTcVo.setPrtnNm(""); // 필수, 거래처 명
			}
		}

		cicuedCuChTcVo.setFscrId("OCP");
		cicuedCuChTcVo.setLschId("OCP");

		CicuemCuOptiTcVo cicuemCuOptiTcVo = cicuedCuChTcVo.getCicuemCuOptiTcVo();
		String smsAgree = "";

		// 수신 동의 처리
		List<Marketing> joinMarketings = joinRequest.getMarketings();
		if (joinMarketings != null && !joinMarketings.isEmpty()) {
			for (Marketing joinMarketing : joinMarketings) {

				// 경로는 030 스킵
				if (OmniConstants.JOINON_CHCD.equals(joinMarketing.getChCd())) {
					continue;
				}
				
				// Mandatory
				// cicuemCuOptiTcVo.setChCd(joinMarketing.getChCd());
				cicuemCuOptiTcVo.setChCd(joinRequest.getChcd());

				// Mandatory
				cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수
				cicuemCuOptiTcVo.setSmsOptiYn(joinMarketing.getSmsAgree()); // 필수 SMS수신동의여부
				smsAgree = joinMarketing.getSmsAgree();
				cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // 필수 SMS수신동의일자
				cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수 이메일수신동의여부
				cicuemCuOptiTcVo.setDmOptiYn("N"); // 필수 DM수신동의여부
				cicuemCuOptiTcVo.setTmOptiYn("N"); // 필수 TM수신동의여부
				cicuemCuOptiTcVo.setIntlOptiYn("N"); // 필수 알림톡수신동의여부
				cicuemCuOptiTcVo.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
				cicuemCuOptiTcVo.setFscrId("OCP");
				cicuemCuOptiTcVo.setLschId("OCP");
				// Optional
				cicuemCuOptiTcVo.setEmlOptiDt("");
				cicuemCuOptiTcVo.setDmOptiDt("");
				cicuemCuOptiTcVo.setTmOptiDt("");
				cicuemCuOptiTcVo.setIntlOptiDt("");
				cicuemCuOptiTcVo.setKkoIntlOptiDt("");
				cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);

			}
		} else { // 수신 동의 처리 시 해당 경로시스템의 수신 동의 여부가 없을 경우 N 으로 처리 2021-06-10 hjw0228
			// 브랜드 사이트의 경우 처리하지 않음 2021-08-03 hjw0228
			String profile = systemInfo.getActiveProfiles()[0];
			profile = StringUtils.isEmpty(profile) ? "dev" : profile;
			profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
			if(!config.isBrandSite(channel.getChCd(), profile)) {
				cicuemCuOptiTcVo.setChCd(joinRequest.getChcd());

				// Mandatory
				cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수
				cicuemCuOptiTcVo.setSmsOptiYn("N"); // 필수 SMS수신동의여부
				cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // 필수 SMS수신동의일자
				cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수 이메일수신동의여부
				cicuemCuOptiTcVo.setDmOptiYn("N"); // 필수 DM수신동의여부
				cicuemCuOptiTcVo.setTmOptiYn("N"); // 필수 TM수신동의여부
				cicuemCuOptiTcVo.setIntlOptiYn("N"); // 필수 알림톡수신동의여부
				cicuemCuOptiTcVo.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
				cicuemCuOptiTcVo.setFscrId("OCP");
				cicuemCuOptiTcVo.setLschId("OCP");
				// Optional
				cicuemCuOptiTcVo.setEmlOptiDt("");
				cicuemCuOptiTcVo.setDmOptiDt("");
				cicuemCuOptiTcVo.setTmOptiDt("");
				cicuemCuOptiTcVo.setIntlOptiDt("");
				cicuemCuOptiTcVo.setKkoIntlOptiDt("");
				cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);
			}
		}
		
		createCustChannel.addCicuedCuChTcVo(cicuedCuChTcVo);
		
		// 030을 포함한 6개 채널 동시 가입 처리 2021-10-26 hjw0228
		/*
		 * String profile = systemInfo.getActiveProfiles()[0]; profile = StringUtils.isEmpty(profile) ? "dev" : profile; profile =
		 * profile.equalsIgnoreCase("default") ? "dev" : profile; if (onoffline == OnOffline.Online && StringUtils.hasText(joinRequest.getLoginid())
		 * && !"stg".equals(profile) && !"prod".equals(profile)) { // 온라인 ID 입력여부 체크한 경우에만 030을 포함한 6개 채널 동시 가입 처리(stg환경 제외:뷰티포인트 없음) CuoptiVo
		 * cuoptiVo = new CuoptiVo(); cuoptiVo.setIncsNo(joinRequest.getIncsno()); CipAthtVo cipAthtVo = new CipAthtVo(); cipAthtVo.setChCd("000");
		 * cipAthtVo.setSysCd("OCP"); cuoptiVo.setCipAthtVo(cipAthtVo);
		 * 
		 * log.debug("CuoptiVo ▶▶▶▶▶▶ {}", StringUtil.printJson(cuoptiVo)); CuoptiResponse cuoptiResponse =
		 * customerApiService.getCicuemcuoptiList(cuoptiVo);
		 * 
		 * if(!"ICITSVCOM999".equals(cuoptiResponse.getRsltCd())) { CicuemCuOptiQcVo cicuemCuOptiQc = new CicuemCuOptiQcVo();
		 * 
		 * // 000 채널 마케팅 정보 수신 동의 여부로 값 세팅 for(CicuemCuOptiQcVo cicuemCuOptiQcVo : cuoptiResponse.getCicuemCuOptiQcVo()) {
		 * if("000".equals(cicuemCuOptiQcVo.getChCd())) { cicuemCuOptiQc = cicuemCuOptiQcVo; } }
		 * 
		 * if(cicuemCuOptiQc != null && "000".equals(cicuemCuOptiQc.getChCd())) { for (Object chCd : config.getBpChannelCodes()) { cicuedCuChTcVo =
		 * new CicuedCuChTcVo(); // Mandatory cicuedCuChTcVo.setIncsNo(joinRequest.getIncsno()); // 필수 cicuedCuChTcVo.setChCd(chCd.toString()); //
		 * 필수
		 * 
		 * if (StringUtils.hasText(joinRequest.getLoginid())) { // 경로에서 별도 관리되는 고객번호 cicuedCuChTcVo.setChcsNo(joinRequest.getLoginid()); // 필수, 선택한
		 * 로그인 아이디, 경로에서 별도 관리되는 고객번호 } else { cicuedCuChTcVo.setChcsNo(joinRequest.getIncsno()); }
		 * 
		 * if (StringUtils.hasText(joinRequest.getLoginpassword())) { // 필수, 선택한 로그인 아이디 비밀번호, 웹경로일경우 필수
		 * cicuedCuChTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword())); }
		 * 
		 * log.debug("buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", onoffline.name(), joinRequest.getJoinPrtnId(),
		 * joinRequest.getJoinPrtnNm());
		 * 
		 * // 오프라인 가입 시는 파라미터로 전송됨. if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
		 * cicuedCuChTcVo.setFstCnttPrtnId(joinRequest.getJoinPrtnId()); // 필수, 최초접촉거래처ID } else { cicuedCuChTcVo.setFstCnttPrtnId(""); // 필수,
		 * 최초접촉거래처ID }
		 * 
		 * cicuedCuChTcVo.setPrtnNm(config.getBpChannelName(chCd.toString())); // 필수, 거래처 명
		 * 
		 * cicuedCuChTcVo.setFscrId("OCP"); cicuedCuChTcVo.setLschId("OCP");
		 * 
		 * cicuemCuOptiTcVo = cicuedCuChTcVo.getCicuemCuOptiTcVo();
		 * 
		 * cicuemCuOptiTcVo.setChCd(chCd.toString());
		 * 
		 * // Mandatory cicuemCuOptiTcVo.setEmlOptiYn(cicuemCuOptiQc.getEmlOptiYn()); // 필수 이메일수신동의여부
		 * cicuemCuOptiTcVo.setEmlOptiDt(cicuemCuOptiQc.getEmlOptiDt()); // 필수 이메일수신동의일자
		 * cicuemCuOptiTcVo.setSmsOptiYn(cicuemCuOptiQc.getSmsOptiYn()); // 필수 SMS수신동의여부
		 * cicuemCuOptiTcVo.setSmsOptiDt(cicuemCuOptiQc.getSmsOptiDt()); // 필수 SMS수신동의일자 cicuemCuOptiTcVo.setDmOptiYn(cicuemCuOptiQc.getDmOptiYn());
		 * // 필수 DM수신동의여부 cicuemCuOptiTcVo.setDmOptiDt(cicuemCuOptiQc.getDmOptiDt()); // 필수 DM수신동의일자
		 * cicuemCuOptiTcVo.setTmOptiYn(cicuemCuOptiQc.getTmOptiYn()); // 필수 TM수신동의여부 cicuemCuOptiTcVo.setTmOptiDt(cicuemCuOptiQc.getTmOptiDt()); //
		 * 필수 TM수신동의일자 cicuemCuOptiTcVo.setIntlOptiYn(cicuemCuOptiQc.getIntlOptiYn()); // 필수 알림톡수신동의여부
		 * cicuemCuOptiTcVo.setIntlOptiDt(cicuemCuOptiQc.getIntlOptiDt()); // 필수 알림톡수신동의일자
		 * cicuemCuOptiTcVo.setKkoIntlOptiYn(cicuemCuOptiQc.getKkoIntlOptiYn()); // 카카오 알림톡수신동의여부
		 * cicuemCuOptiTcVo.setKkoIntlOptiDt(cicuemCuOptiQc.getKkoIntlOptiDt()); // 카카오 알림톡수신동의여부
		 * 
		 * cicuemCuOptiTcVo.setFscrId("OCP"); cicuemCuOptiTcVo.setLschId("OCP");
		 * 
		 * cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);
		 * 
		 * createCustChannel.addCicuedCuChTcVo(cicuedCuChTcVo); } } } }
		 */
		
		// 오설록 티하우스 가입 시 오설록 백화점도 강제 가입 처리
		String joinChCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		if(OmniConstants.OSULLOC_OFFLINE_CHCD.equals(joinChCd) && (OmniConstants.OSULLOC_OFFLINE_CHCD.equals(channel.getChCd()) || OmniConstants.OSULLOC_CHCD.equals(channel.getChCd()))) {
			cicuedCuChTcVo = new CicuedCuChTcVo();
			// Mandatory
			cicuedCuChTcVo.setIncsNo(joinRequest.getIncsno()); // 필수
			cicuedCuChTcVo.setChCd(OmniConstants.OSULLOC_DEPARTMENT_CHCD); // 필수
			
			if (StringUtils.hasText(joinRequest.getLoginid())) { // 경로에서 별도 관리되는 고객번호
				cicuedCuChTcVo.setChcsNo(joinRequest.getLoginid()); // 필수, 선택한 로그인 아이디, 경로에서 별도 관리되는 고객번호
			} else {
				cicuedCuChTcVo.setChcsNo(joinRequest.getIncsno());
			}

			if (StringUtils.hasText(joinRequest.getLoginpassword())) { // 필수, 선택한 로그인 아이디 비밀번호, 웹경로일경우 필수
				cicuedCuChTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword()));
			}

			log.debug("buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", onoffline.name(), joinRequest.getJoinPrtnId(), joinRequest.getJoinPrtnNm());

			// 오프라인 가입 시는 파라미터로 전송됨.
			if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
				cicuedCuChTcVo.setFstCnttPrtnId(joinRequest.getJoinPrtnId()); // 필수, 최초접촉거래처ID
			} else {
				cicuedCuChTcVo.setFstCnttPrtnId(""); // 필수, 최초접촉거래처ID
			}

			if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
				cicuedCuChTcVo.setPrtnNm(joinRequest.getJoinPrtnNm()); // 필수, 거래처 명
			} else {
				cicuedCuChTcVo.setPrtnNm(""); // 필수, 거래처 명
			}
			
			cicuedCuChTcVo.setFscrId("OCP");
			cicuedCuChTcVo.setLschId("OCP");
			
			cicuemCuOptiTcVo = cicuedCuChTcVo.getCicuemCuOptiTcVo();
			
			cicuemCuOptiTcVo.setChCd(OmniConstants.OSULLOC_DEPARTMENT_CHCD);

			// Mandatory
			cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수
			cicuemCuOptiTcVo.setSmsOptiYn(smsAgree); // 필수 SMS수신동의여부
			cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // 필수 SMS수신동의일자
			cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수 이메일수신동의여부
			cicuemCuOptiTcVo.setDmOptiYn("N"); // 필수 DM수신동의여부
			cicuemCuOptiTcVo.setTmOptiYn("N"); // 필수 TM수신동의여부
			cicuemCuOptiTcVo.setIntlOptiYn("N"); // 필수 알림톡수신동의여부
			cicuemCuOptiTcVo.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
			cicuemCuOptiTcVo.setFscrId("OCP");
			cicuemCuOptiTcVo.setLschId("OCP");
			// Optional
			cicuemCuOptiTcVo.setEmlOptiDt("");
			cicuemCuOptiTcVo.setDmOptiDt("");
			cicuemCuOptiTcVo.setTmOptiDt("");
			cicuemCuOptiTcVo.setIntlOptiDt("");
			cicuemCuOptiTcVo.setKkoIntlOptiDt("");
			cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);
			
			createCustChannel.addCicuedCuChTcVo(cicuedCuChTcVo);
		}
		
		// 오설록 백화점 가입 시 오설록 티하우스도 강제 가입 처리
		if(OmniConstants.OSULLOC_DEPARTMENT_CHCD.equals(joinChCd) && (OmniConstants.OSULLOC_DEPARTMENT_CHCD.equals(channel.getChCd()) || OmniConstants.OSULLOC_CHCD.equals(channel.getChCd()))) {
			cicuedCuChTcVo = new CicuedCuChTcVo();
			// Mandatory
			cicuedCuChTcVo.setIncsNo(joinRequest.getIncsno()); // 필수
			cicuedCuChTcVo.setChCd(OmniConstants.OSULLOC_OFFLINE_CHCD); // 필수
			
			if (StringUtils.hasText(joinRequest.getLoginid())) { // 경로에서 별도 관리되는 고객번호
				cicuedCuChTcVo.setChcsNo(joinRequest.getLoginid()); // 필수, 선택한 로그인 아이디, 경로에서 별도 관리되는 고객번호
			} else {
				cicuedCuChTcVo.setChcsNo(joinRequest.getIncsno());
			}

			if (StringUtils.hasText(joinRequest.getLoginpassword())) { // 필수, 선택한 로그인 아이디 비밀번호, 웹경로일경우 필수
				cicuedCuChTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword()));
			}

			log.debug("buildChannelCustomerData {}, joinprtnid : {}, joinprtnnm : {}", onoffline.name(), joinRequest.getJoinPrtnId(), joinRequest.getJoinPrtnNm());

			// 오프라인 가입 시는 파라미터로 전송됨.
			if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
				cicuedCuChTcVo.setFstCnttPrtnId(joinRequest.getJoinPrtnId()); // 필수, 최초접촉거래처ID
			} else {
				cicuedCuChTcVo.setFstCnttPrtnId(""); // 필수, 최초접촉거래처ID
			}

			if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
				cicuedCuChTcVo.setPrtnNm(joinRequest.getJoinPrtnNm()); // 필수, 거래처 명
			} else {
				cicuedCuChTcVo.setPrtnNm(""); // 필수, 거래처 명
			}
			
			cicuedCuChTcVo.setFscrId("OCP");
			cicuedCuChTcVo.setLschId("OCP");
			
			cicuemCuOptiTcVo = cicuedCuChTcVo.getCicuemCuOptiTcVo();
			
			cicuemCuOptiTcVo.setChCd(OmniConstants.OSULLOC_OFFLINE_CHCD);

			// Mandatory
			cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수
			cicuemCuOptiTcVo.setSmsOptiYn(smsAgree); // 필수 SMS수신동의여부
			cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // 필수 SMS수신동의일자
			cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수 이메일수신동의여부
			cicuemCuOptiTcVo.setDmOptiYn("N"); // 필수 DM수신동의여부
			cicuemCuOptiTcVo.setTmOptiYn("N"); // 필수 TM수신동의여부
			cicuemCuOptiTcVo.setIntlOptiYn("N"); // 필수 알림톡수신동의여부
			cicuemCuOptiTcVo.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
			cicuemCuOptiTcVo.setFscrId("OCP");
			cicuemCuOptiTcVo.setLschId("OCP");
			// Optional
			cicuemCuOptiTcVo.setEmlOptiDt("");
			cicuemCuOptiTcVo.setDmOptiDt("");
			cicuemCuOptiTcVo.setTmOptiDt("");
			cicuemCuOptiTcVo.setIntlOptiDt("");
			cicuemCuOptiTcVo.setKkoIntlOptiDt("");
			cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);
			
			createCustChannel.addCicuedCuChTcVo(cicuedCuChTcVo);
		}

		return createCustChannel;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 경로 가입 시 수신 동의 없는 케이스 처리 (Default N)
	 * 
	 * 
	 * author   : hjw0228
	 * date     : 2021. 06. 10. 오후 4:43:55
	 * </pre>
	 * @param onoffline
	 * @param channel
	 * @param joinRequest
	 * @return
	 */
	public static CreateCustChannelRequest buildIntegratedChannelCustomerData(final Channel channel, final String incsno, final String loginId, final String loginPwd) {
		CreateCustChannelRequest createCustChannel = new CreateCustChannelRequest();

		CicuedCuChTcVo cicuedCuChTcVo = new CicuedCuChTcVo();
		// Mandatory
		cicuedCuChTcVo.setIncsNo(incsno); // 필수
		cicuedCuChTcVo.setChCd(channel.getChCd()); // 필수

		if (StringUtils.hasText(loginId)) { // 경로에서 별도 관리되는 고객번호
			cicuedCuChTcVo.setChcsNo(loginId); // 필수, 선택한 로그인 아이디, 경로에서 별도 관리되는 고객번호
		} else {
			cicuedCuChTcVo.setChcsNo(incsno);
		}

		if (StringUtils.hasText(loginPwd)) { // 필수, 선택한 로그인 아이디 비밀번호, 웹경로일경우 필수
			if(SecurityUtil.isBase64(loginPwd)) {
					try {
						cicuedCuChTcVo.setUserPwdEc(new String(SecurityUtil.hex(SecurityUtil.base64(loginPwd))));
					} catch (DecoderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						cicuedCuChTcVo.setUserPwdEc(null);
					}
			} else {
				cicuedCuChTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(loginPwd));
			}
		}

		cicuedCuChTcVo.setFstCnttPrtnId(config.getJoinPrtnCode(channel.getChCd())); // 필수, 최초접촉거래처ID
		cicuedCuChTcVo.setPrtnNm(config.getJoinPrtnName(channel.getChCd())); // 필수, 거래처 명

		cicuedCuChTcVo.setFscrId("OCP");
		cicuedCuChTcVo.setLschId("OCP");

		CicuemCuOptiTcVo cicuemCuOptiTcVo = cicuedCuChTcVo.getCicuemCuOptiTcVo();

		 // 수신 동의 처리 시 해당 경로시스템의 수신 동의 여부가 없을 경우 N 으로 처리 2021-06-10 hjw0228
		// -> 수신 동의 여부가 없고 isMarketingSyncBpEnable 가 true 면 030 채널 수신동의 여부와 동기화 2022-01-12 hjw0228
		String profile = systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;		
		
		cicuemCuOptiTcVo.setChCd(channel.getChCd());
		
		// isMarketingSyncBpEnable 면 030 채널 수신동의 여부와 동기화
		if(config.isMarketingSyncBpEnable(channel.getChCd(), profile)) {
			CuoptiVo cuoptiVo = new CuoptiVo();
			cuoptiVo.setIncsNo(incsno);
			
			log.debug("CuoptiVo ▶▶▶▶▶▶ {}", StringUtil.printJson(cuoptiVo));
			CuoptiResponse cuoptiResponse = customerApiService.getCicuemcuoptiList(cuoptiVo);
			
			if(!"ICITSVCOM999".equals(cuoptiResponse.getRsltCd())) {
				CicuemCuOptiQcVo cicuemCuOptiQc = new CicuemCuOptiQcVo();
				
				// 030 채널 마케팅 정보 수신 동의 여부로 값 세팅 
				for(CicuemCuOptiQcVo cicuemCuOptiQcVo : cuoptiResponse.getCicuemCuOptiQcVo()) {
					if("030".equals(cicuemCuOptiQcVo.getChCd())) { 
						cicuemCuOptiQc = cicuemCuOptiQcVo; 
					} 
				}
				
				// 값이 없으면 N 처리
				final String empOptiYn = StringUtils.isEmpty(cicuemCuOptiQc.getEmlOptiYn()) ? "N" : cicuemCuOptiQc.getEmlOptiYn();
				final String smsOptiYn = StringUtils.isEmpty(cicuemCuOptiQc.getSmsOptiYn()) ? "N" : cicuemCuOptiQc.getSmsOptiYn();
				final String dmOptiYn = StringUtils.isEmpty(cicuemCuOptiQc.getDmOptiYn()) ? "N" : cicuemCuOptiQc.getDmOptiYn();
				final String tmOptiYn = StringUtils.isEmpty(cicuemCuOptiQc.getTmOptiYn()) ? "N" : cicuemCuOptiQc.getTmOptiYn();
				final String intlOptiYn = StringUtils.isEmpty(cicuemCuOptiQc.getIntlOptiYn()) ? "N" : cicuemCuOptiQc.getIntlOptiYn();
				final String kkoIntlOptiYn = StringUtils.isEmpty(cicuemCuOptiQc.getKkoIntlOptiYn()) ? "N" : cicuemCuOptiQc.getKkoIntlOptiYn();
				
				// Mandatory
				cicuemCuOptiTcVo.setEmlOptiYn(empOptiYn); // 필수
				cicuemCuOptiTcVo.setSmsOptiYn(smsOptiYn); // 필수 SMS수신동의여부
				cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // 필수 SMS수신동의일자
				cicuemCuOptiTcVo.setDmOptiYn(dmOptiYn); // 필수 DM수신동의여부
				cicuemCuOptiTcVo.setTmOptiYn(tmOptiYn); // 필수 TM수신동의여부
				cicuemCuOptiTcVo.setIntlOptiYn(intlOptiYn); // 필수 알림톡수신동의여부
				cicuemCuOptiTcVo.setKkoIntlOptiYn(kkoIntlOptiYn); // 카카오 알림톡수신동의여부
				cicuemCuOptiTcVo.setFscrId("OCP");
				cicuemCuOptiTcVo.setLschId("OCP");
				// Optional
				cicuemCuOptiTcVo.setEmlOptiDt(DateUtil.getCurrentDate());
				cicuemCuOptiTcVo.setDmOptiDt(DateUtil.getCurrentDate());
				cicuemCuOptiTcVo.setTmOptiDt(DateUtil.getCurrentDate());
				cicuemCuOptiTcVo.setIntlOptiDt(DateUtil.getCurrentDate());
				cicuemCuOptiTcVo.setKkoIntlOptiDt(DateUtil.getCurrentDate());
			}
		} else {
			// Mandatory
			cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수
			cicuemCuOptiTcVo.setSmsOptiYn("N"); // 필수 SMS수신동의여부
			cicuemCuOptiTcVo.setSmsOptiDt(DateUtil.getCurrentDate()); // 필수 SMS수신동의일자
			cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수 이메일수신동의여부
			cicuemCuOptiTcVo.setDmOptiYn("N"); // 필수 DM수신동의여부
			cicuemCuOptiTcVo.setTmOptiYn("N"); // 필수 TM수신동의여부
			cicuemCuOptiTcVo.setIntlOptiYn("N"); // 필수 알림톡수신동의여부
			cicuemCuOptiTcVo.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
			cicuemCuOptiTcVo.setFscrId("OCP");
			cicuemCuOptiTcVo.setLschId("OCP");
			// Optional
			cicuemCuOptiTcVo.setEmlOptiDt("");
			cicuemCuOptiTcVo.setDmOptiDt("");
			cicuemCuOptiTcVo.setTmOptiDt("");
			cicuemCuOptiTcVo.setIntlOptiDt("");
			cicuemCuOptiTcVo.setKkoIntlOptiDt("");
			cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);	
		}

		createCustChannel.addCicuedCuChTcVo(cicuedCuChTcVo);
		
		// 030을 포함한 6개 채널 동시 가입 처리 2021-10-26 hjw0228
		/*
		 * String profile = systemInfo.getActiveProfiles()[0]; profile = StringUtils.isEmpty(profile) ? "dev" : profile; profile =
		 * profile.equalsIgnoreCase("default") ? "dev" : profile; if (StringUtils.hasText(loginId) && !"prod".equals(profile)) { // 온라인 ID 입력여부 체크한
		 * 경우에만 030을 포함한 6개 채널 동시 가입 처리 CuoptiVo cuoptiVo = new CuoptiVo(); cuoptiVo.setIncsNo(incsno); CipAthtVo cipAthtVo = new CipAthtVo();
		 * cipAthtVo.setChCd("000"); cipAthtVo.setSysCd("OCP"); cuoptiVo.setCipAthtVo(cipAthtVo);
		 * 
		 * log.debug("CuoptiVo ▶▶▶▶▶▶ {}", StringUtil.printJson(cuoptiVo)); CuoptiResponse cuoptiResponse =
		 * customerApiService.getCicuemcuoptiList(cuoptiVo);
		 * 
		 * if(!"ICITSVCOM999".equals(cuoptiResponse.getRsltCd())) { CicuemCuOptiQcVo cicuemCuOptiQc = new CicuemCuOptiQcVo();
		 * 
		 * // 000 채널 마케팅 정보 수신 동의 여부로 값 세팅 for(CicuemCuOptiQcVo cicuemCuOptiQcVo : cuoptiResponse.getCicuemCuOptiQcVo()) {
		 * if("000".equals(cicuemCuOptiQcVo.getChCd())) { cicuemCuOptiQc = cicuemCuOptiQcVo; } }
		 * 
		 * if(cicuemCuOptiQc != null && "000".equals(cicuemCuOptiQc.getChCd())) { for (Object chCd : config.getBpChannelCodes()) { cicuedCuChTcVo =
		 * new CicuedCuChTcVo(); // Mandatory cicuedCuChTcVo.setIncsNo(incsno); // 필수 cicuedCuChTcVo.setChCd(chCd.toString()); // 필수
		 * 
		 * if (StringUtils.hasText(loginId)) { // 경로에서 별도 관리되는 고객번호 cicuedCuChTcVo.setChcsNo(loginId); // 필수, 선택한 로그인 아이디, 경로에서 별도 관리되는 고객번호 } else
		 * { cicuedCuChTcVo.setChcsNo(incsno); }
		 * 
		 * if (StringUtils.hasText(loginPwd)) { // 필수, 선택한 로그인 아이디 비밀번호, 웹경로일경우 필수 if(SecurityUtil.isBase64(loginPwd)) { try {
		 * cicuedCuChTcVo.setUserPwdEc(new String(SecurityUtil.hex(SecurityUtil.base64(loginPwd)))); } catch (DecoderException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); cicuedCuChTcVo.setUserPwdEc(null); } } else {
		 * cicuedCuChTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(loginPwd)); } }
		 * 
		 * cicuedCuChTcVo.setFstCnttPrtnId(config.getJoinPrtnCode(channel.getChCd())); // 필수, 최초접촉거래처ID
		 * cicuedCuChTcVo.setPrtnNm(config.getBpChannelName(chCd.toString())); // 필수, 거래처 명
		 * 
		 * cicuedCuChTcVo.setFscrId("OCP"); cicuedCuChTcVo.setLschId("OCP");
		 * 
		 * cicuemCuOptiTcVo = cicuedCuChTcVo.getCicuemCuOptiTcVo();
		 * 
		 * cicuemCuOptiTcVo.setChCd(chCd.toString());
		 * 
		 * // Mandatory cicuemCuOptiTcVo.setEmlOptiYn(cicuemCuOptiQc.getEmlOptiYn()); // 필수 이메일수신동의여부
		 * cicuemCuOptiTcVo.setEmlOptiDt(cicuemCuOptiQc.getEmlOptiDt()); // 필수 이메일수신동의일자
		 * cicuemCuOptiTcVo.setSmsOptiYn(cicuemCuOptiQc.getSmsOptiYn()); // 필수 SMS수신동의여부
		 * cicuemCuOptiTcVo.setSmsOptiDt(cicuemCuOptiQc.getSmsOptiDt()); // 필수 SMS수신동의일자 cicuemCuOptiTcVo.setDmOptiYn(cicuemCuOptiQc.getDmOptiYn());
		 * // 필수 DM수신동의여부 cicuemCuOptiTcVo.setDmOptiDt(cicuemCuOptiQc.getDmOptiDt()); // 필수 DM수신동의일자
		 * cicuemCuOptiTcVo.setTmOptiYn(cicuemCuOptiQc.getTmOptiYn()); // 필수 TM수신동의여부 cicuemCuOptiTcVo.setTmOptiDt(cicuemCuOptiQc.getTmOptiDt()); //
		 * 필수 TM수신동의일자 cicuemCuOptiTcVo.setIntlOptiYn(cicuemCuOptiQc.getIntlOptiYn()); // 필수 알림톡수신동의여부
		 * cicuemCuOptiTcVo.setIntlOptiDt(cicuemCuOptiQc.getIntlOptiDt()); // 필수 알림톡수신동의일자
		 * cicuemCuOptiTcVo.setKkoIntlOptiYn(cicuemCuOptiQc.getKkoIntlOptiYn()); // 카카오 알림톡수신동의여부
		 * cicuemCuOptiTcVo.setKkoIntlOptiDt(cicuemCuOptiQc.getKkoIntlOptiDt()); // 카카오 알림톡수신동의여부
		 * 
		 * cicuemCuOptiTcVo.setFscrId("OCP"); cicuemCuOptiTcVo.setLschId("OCP");
		 * 
		 * cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);
		 * 
		 * createCustChannel.addCicuedCuChTcVo(cicuedCuChTcVo); } } } }
		 */
		
		return createCustChannel;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 고객통합 경로 등록 (030을 포함한 7개 채널)
	 * 
	 * "이니스프리" 매장코드 및 매장명
	 * 온라인인 경우 온/오프 13000001 이니스프리쇼핑몰
	 * 오프라인인 경우 온라인 13000001 이니스프리쇼핑몰
	 * 
	 * "에스쁘아"  매장코드 및 매장명
	 * 온라인인 경우 온/오프 11000494 에스쁘아쇼핑몰
	 * 오프라인인 경우 온라인 11000494 에스쁘아쇼핑몰 
	 * author   : hjw0228
	 * date     : 2022. 7. 1. 오전 10:46:30
	 * </pre>
	 * 
	 * @param joinRequest
	 * @return
	 */
	public static CreateCustChannelRequest buildIntegrated030ChannelCustomerData(final JoinRequest joinRequest) {

		CreateCustChannelRequest createCustChannel = new CreateCustChannelRequest();

		CicuedCuChTcVo cicuedCuChTcVo = new CicuedCuChTcVo();
		// Mandatory
		cicuedCuChTcVo.setIncsNo(joinRequest.getIncsno()); // 필수
		cicuedCuChTcVo.setChCd(joinRequest.getChcd()); // 필수


		if (StringUtils.hasText(joinRequest.getLoginid())) { // 경로에서 별도 관리되는 고객번호
			cicuedCuChTcVo.setChcsNo(joinRequest.getLoginid()); // 필수, 선택한 로그인 아이디, 경로에서 별도 관리되는 고객번호
		} else {
			cicuedCuChTcVo.setChcsNo(joinRequest.getIncsno());
		}
		
		// 뷰티멤버십 연동으로 회원 가입 시 chcsNo 값은 제휴사 membership ID 로 입력
//		final String isMembershipSession = (String) WebUtil.getSession(OmniConstants.IS_MEMBERSHIP);
//		boolean isMembership = OmniUtil.isMembership(isMembershipSession);
//		if(isMembership) {
//			final MembershipUserInfo membershipUserInfo = (MembershipUserInfo) WebUtil.getSession(OmniConstants.MEMBERSHIP_USERINFO);
//			cicuedCuChTcVo.setChcsNo(membershipUserInfo.getMbrId());
//		}

		if (StringUtils.hasText(joinRequest.getLoginpassword())) { // 필수, 선택한 로그인 아이디 비밀번호, 웹경로일경우 필수
			cicuedCuChTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword()));
		}

		log.debug("build030CustomerData joinprtnid : {}, joinprtnnm : {}", joinRequest.getJoinPrtnId(), joinRequest.getJoinPrtnNm());

		if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
			cicuedCuChTcVo.setFstCnttPrtnId(joinRequest.getJoinPrtnId()); // 필수, 최초접촉거래처ID
			log.debug("▶▶▶▶▶▶ build030CustomerData {}, joinprtnid : {}, joinprtnnm : {}", joinRequest.getChcd(), config.getJoinPrtnCode(joinRequest.getChcd()), config.getJoinPrtnName(joinRequest.getChcd()));
		} else {
			cicuedCuChTcVo.setFstCnttPrtnId(config.getJoinPrtnCode(joinRequest.getChcd())); // 필수, 최초접촉거래처ID
		}

		if (StringUtils.hasText(joinRequest.getJoinPrtnNm())) {
			cicuedCuChTcVo.setPrtnNm(joinRequest.getJoinPrtnNm()); // 필수, 거래처 명
		} else {
			cicuedCuChTcVo.setPrtnNm(config.getJoinPrtnName(joinRequest.getChcd())); // 필수, 거래처 명
		}

		cicuedCuChTcVo.setFscrId("OCP");
		cicuedCuChTcVo.setLschId("OCP");

		CicuemCuOptiTcVo cicuemCuOptiTcVo = cicuedCuChTcVo.getCicuemCuOptiTcVo();
		
		String profile = systemInfo.getActiveProfiles()[0]; 
		profile = StringUtils.isEmpty(profile) ? "dev" : profile; 
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile; 
		if (StringUtils.hasText(joinRequest.getLoginid())) { 
		 
			for (Object chCd : config.getBpChannelCodes()) { 
				cicuedCuChTcVo = new CicuedCuChTcVo(); // Mandatory 
				cicuedCuChTcVo.setIncsNo(joinRequest.getIncsno()); // 필수 
				cicuedCuChTcVo.setChCd(chCd.toString()); // 필수
 
				if (StringUtils.hasText(joinRequest.getLoginid())) { // 경로에서 별도 관리되는 고객번호 
					cicuedCuChTcVo.setChcsNo(joinRequest.getLoginid()); // 필수, 선택한 로그인 아이디, 경로에서 별도 관리되는 고객번호 
				} else { 
					cicuedCuChTcVo.setChcsNo(joinRequest.getIncsno()); 
				}
 
				if (StringUtils.hasText(joinRequest.getLoginpassword())) { // 필수, 선택한 로그인 아이디 비밀번호, 웹경로일경우 필수
					cicuedCuChTcVo.setUserPwdEc(SecurityUtil.getEncodedSHA512Password(joinRequest.getLoginpassword())); 
				}
 
				log.debug("build030CustomerData joinprtnid : {}, joinprtnnm : {}", joinRequest.getJoinPrtnId(), joinRequest.getJoinPrtnNm());
 
				// 오프라인 가입 시는 파라미터로 전송됨. 
				if (StringUtils.hasText(joinRequest.getJoinPrtnId())) {
					cicuedCuChTcVo.setFstCnttPrtnId(joinRequest.getJoinPrtnId()); // 필수, 최초접촉거래처ID 
				} else { 
					cicuedCuChTcVo.setFstCnttPrtnId(""); // 필수, 최초접촉거래처ID 
				}
 
				cicuedCuChTcVo.setPrtnNm(config.getBpChannelName(chCd.toString())); // 필수, 거래처 명
				cicuedCuChTcVo.setFscrId("OCP"); cicuedCuChTcVo.setLschId("OCP");
				cicuemCuOptiTcVo = cicuedCuChTcVo.getCicuemCuOptiTcVo();
				cicuemCuOptiTcVo.setChCd(chCd.toString());
	 
				// Mandatory 
				cicuemCuOptiTcVo.setEmlOptiYn("N"); // 필수 이메일수신동의여부
				cicuemCuOptiTcVo.setEmlOptiDt("N"); // 필수 이메일수신동의일자
				cicuemCuOptiTcVo.setSmsOptiYn("N"); // 필수 SMS수신동의여부
				cicuemCuOptiTcVo.setSmsOptiDt("N"); // 필수 SMS수신동의일자 
				cicuemCuOptiTcVo.setDmOptiYn("N");
				// 필수 DM수신동의여부 
				cicuemCuOptiTcVo.setDmOptiDt("N"); // 필수 DM수신동의일자
				cicuemCuOptiTcVo.setTmOptiYn("N"); // 필수 TM수신동의여부 
				cicuemCuOptiTcVo.setTmOptiDt("N"); // 필수 TM수신동의일자 
				cicuemCuOptiTcVo.setIntlOptiYn("N"); // 필수 알림톡수신동의여부
				cicuemCuOptiTcVo.setIntlOptiDt("N"); // 필수 알림톡수신동의일자
				cicuemCuOptiTcVo.setKkoIntlOptiYn("N"); // 카카오 알림톡수신동의여부
				cicuemCuOptiTcVo.setKkoIntlOptiDt("N"); // 카카오 알림톡수신동의여부
				cicuemCuOptiTcVo.setFscrId("OCP"); 
				cicuemCuOptiTcVo.setLschId("OCP");
	  
				cicuedCuChTcVo.setCicuemCuOptiTcVo(cicuemCuOptiTcVo);
	 
				createCustChannel.addCicuedCuChTcVo(cicuedCuChTcVo); 
			} 
		}

		return createCustChannel;
	}
}
