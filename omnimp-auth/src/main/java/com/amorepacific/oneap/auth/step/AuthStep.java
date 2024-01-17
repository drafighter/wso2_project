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
 * Date   	          : 2020. 8. 13..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.step;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.api.vo.ivo.AbusingLockVo;
import com.amorepacific.oneap.auth.api.vo.ivo.CreateCustChannelRequest;
import com.amorepacific.oneap.auth.api.vo.ovo.CreateCustChannelJoinResponse;
import com.amorepacific.oneap.auth.api.vo.ovo.CustYnResponse;
import com.amorepacific.oneap.auth.cert.service.CertService;
import com.amorepacific.oneap.auth.common.service.CommonService;
import com.amorepacific.oneap.auth.join.vo.JoinData;
import com.amorepacific.oneap.auth.join.vo.JoinResponse;
import com.amorepacific.oneap.auth.mgmt.service.MgmtService;
import com.amorepacific.oneap.auth.terms.service.TermsService;
import com.amorepacific.oneap.auth.terms.vo.TermsVo;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.types.JoinDivisionType;
import com.amorepacific.oneap.common.types.JoinType;
import com.amorepacific.oneap.common.types.LoginType;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.SecurityUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.JoinStepVo;
import com.amorepacific.oneap.common.vo.LoginStepVo;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoResponse;
import com.amorepacific.oneap.common.vo.api.CustInfoVo;
import com.amorepacific.oneap.common.vo.api.UpdateCustResponse;
import com.amorepacific.oneap.common.vo.api.UpdateCustVo;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.user.Customer;
import com.amorepacific.oneap.common.vo.user.UmChUser;
import com.amorepacific.oneap.common.vo.user.UmOmniUser;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.login.step 
 *    |_ LoginStep.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 13.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Component
public class AuthStep {

	@Autowired
	private MgmtService mgmtService;

	@Autowired
	private CustomerApiService customerApiService;

	@Autowired
	private TermsService termsService;

	@Autowired
	private CertService certService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SystemInfo systemInfo;

	private ConfigUtil config = ConfigUtil.getInstance();

	/**
	 * 
	 * <pre>
	 * comment  : 회원가입 조건 체크
	 * 	// CI 있으면 --> 이미가입
	 *	// CI 없음 --> 신규, 3개 일치 하면 기 가입 안내, 불일치 시 신규 가입
	 *	// CI 2개 이상 
	 *		CI로 조회해서 나오면 일단 통합고객
	 *		뷰티포인트에 가입되어 있다는 의미 (A0103화면참조)
	 *		로그인 하면, 경로에 가입이 안되어있는 고객이라면 경로 가입을 함 (A0105화면참조)
	 *
	 * -------------------------------------------
	 * 
	 * X X X 탈퇴 후 30 일 이내 A0104
	 * X X X 신규고객 A0101
	 * X X O 자체고객 A0101
	 * O X X 타오프라인 경로 자체 ) 가입 고객  A0103 -> A0207 채널약관 동의 목록 노출 화면
	 * O X O 해당 경로 자체 만 가입된 고객 A0103 -> A0202 경로 자체 고객 ID 사용가능
	 * O X O 해당 경로 자체 만 가입된 고객 A0103 -> A0203 경로 자체 고객 ID 타인 사용
	 * O O X 경로 자체 ) 첫 방문 뷰티포인트 고객 A0103 -> A0105
	 * O O O 이미 가입된 고객 A0103
	 * 
	 * -------------------------------------------
	 *   
	 * author   : takkies
	 * date     : 2020. 9. 4. 오후 2:47:20
	 * </pre>
	 * 
	 * @param certResult
	 * @return
	 */
	public JoinResponse certJoinByCiNo(final CertResult certResult) {

		JoinResponse response = new JoinResponse();

		final String custCiNoCert = certResult.getCiNo();
		final String custNameCert = certResult.getName();
		final String custBirthCert = certResult.getBirth();
		String custPhoneCert = certResult.getPhone();

		// 1. 고객통합 플랫폼 CI로 조회
		CustInfoVo custInfo = new CustInfoVo();
		custInfo.setCiNo(custCiNoCert);
		CustInfoResponse custresponse = this.customerApiService.getCustList(custInfo);

		if (custresponse == null) {
			log.info("▶▶▶▶▶▶ [cert join] 신규가입");
			response.setType(JoinDivisionType.CHANNEL_JOIN.getType());
		}

		final String custRsltCd = custresponse.getRsltCd();

		JoinDivisionType joinType = JoinDivisionType.getByCode(custRsltCd);

		response.setType(joinType.getType());
		Customer custs[] = custresponse.getCicuemCuInfTcVo(); // 고객통합 정보 사용자 조회
		
		// 회원가입시 인증받은 휴대폰번호와 다른 경우(동일 CI)
		// - 기가입된 고객정보가 [CI+이름] 또는 [CI+생년월일] 의 기준에 부합할 경우 인증받은 휴대폰번호로 update하여 VOC 최소화
		if (!StringUtils.isEmpty(custPhoneCert)) { // jsjang 추가
			boolean isUpateCust = false;
			String incsNo = "";
			
			if (custs != null && custs.length > 0) {
				for (Customer customer : custs) {
					log.debug("▶▶▶▶▶▶ custPhoneCert : {}  customer : {}", custPhoneCert, StringUtil.mergeMobile(customer));
					log.debug("▶▶▶▶▶▶ custCiNoCert : {}  customer : {}", custCiNoCert, customer.getCiNo());
					log.debug("▶▶▶▶▶▶ custNameCert : {}  customer : {}", custNameCert, customer.getCustNm());
					if (!custPhoneCert.equals(StringUtil.mergeMobile(customer)) &&
							(
								(custCiNoCert.equals(customer.getCiNo()) && custNameCert.equals(customer.getCustNm()))
								|| (custCiNoCert.equals(customer.getCiNo()) && custBirthCert.equals(customer.getAthtDtbr()))
							)
						) {
						log.debug("▶▶▶▶▶▶ [cert join] phone update");
						isUpateCust = true;
						incsNo = customer.getIncsNo();
						break;
					}
				}
				
				if (isUpateCust && !incsNo.isEmpty()) {
					// 인증받은 휴대폰 번호로 update
					String mobile[] = StringUtil.splitMobile(custPhoneCert);
					log.debug("custPhoneCert {}", custPhoneCert);
					UpdateCustVo updateCustVo = new UpdateCustVo();
					updateCustVo.setIncsNo(incsNo);
					updateCustVo.setCellTidn(mobile[0]);
					updateCustVo.setCellTexn(mobile[1]);
					updateCustVo.setCellTlsn(mobile[2]);
					
					String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
					chcd = "000";
					log.debug("▶▶▶▶▶▶ [cert join] chcd : {}", chcd);
					updateCustVo.setChCd(chcd);
					updateCustVo.setChgChCd(chcd);
					updateCustVo.setLschId("OCP");
					
					UpdateCustResponse updateCustResponse = this.customerApiService.updateCust(updateCustVo);
					
					if ("ICITSVCOM999".equals(updateCustResponse.getRsltCd())) {
						// TODO : update error
						final String updateCustRsltCd = updateCustResponse.getRsltCd();
						JoinDivisionType updateJoinType = JoinDivisionType.getByCode(updateCustRsltCd);
						
						log.info("▶▶▶▶▶▶ [cert join] update customer erro : {}", StringUtil.printJson(updateJoinType));
						response.setType(JoinDivisionType.ERROR.getType());
						
						return response;
					}
					
					custresponse = this.customerApiService.getCustList(custInfo);
					custs = custresponse.getCicuemCuInfTcVo(); // 고객통합 정보 사용자 조회
				}
			}
		}
		
		if (joinType == JoinDivisionType.WITHDRAW) {

			if (custs != null && custs.length > 0) {
				response.setCustomerList(custs);
				Customer cust = custs[0];
				response.setCustomer(cust);
			}

			log.debug("▶▶▶▶▶▶ [cert join] customer check withdraw : {}", StringUtil.printJson(joinType));
			response.setType(JoinDivisionType.WITHDRAW.getType()); // 탈퇴후 30일이 지나면 해당 값이 고객통합에서 고객정보화 함께 삭제됨.
			return response;
		}
		
		if(joinType == JoinDivisionType.ERROR) {
			log.debug("▶▶▶▶▶▶ [cert join] customer check erro : {}", StringUtil.printJson(joinType));
			response.setType(JoinDivisionType.ERROR.getType());
			return response;
		}

		if (custs != null && custs.length > 0) {
			response.setCustomerList(custs);
			if (custs.length == 1) {
				Customer cust = custs[0];
				log.debug("▶▶▶▶▶▶ [cert join] customer info for api : {}", StringUtil.printJson(cust));
				response.setCustomer(cust);

				//final String wtyn = cust.getCustWtYn();
				final String wtdt = cust.getCustWtDttm();
				//if ("Y".equals(wtyn)) {
				if (StringUtils.hasText(wtdt)) {
					log.info("▶▶▶▶▶▶ [cert join] customer check withdraw : {}[{}]", cust.getCustWtYn(), cust.getCustWtDttm());
					response.setType(JoinDivisionType.WITHDRAW.getType()); // 탈퇴후 30일이 지나면 해당 값이 고객통합에서 고객정보화 함께 삭제됨.
					return response;
				}
				if (StringUtils.isEmpty(certResult.getPhone())) {
					certResult.setPhone(StringUtil.mergeMobile(cust));
					WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certResult);
				}
				final String custCiToApi = cust.getCiNo();
				log.info("▶▶▶▶▶▶ [cert join] 고객통합api조회CI=본인인증CI : {} --> {}", custCiToApi.equals(custCiNoCert), "본인인증결과와 조회결과 동일 → 가입사실");
				response.setType(JoinDivisionType.EXIST.getType());
			} else { // 2개 이상 --> 이미가입, 마지막에 가입한 회원으로 경로 가입
				log.debug("▶▶▶▶▶▶ [cert join] customer count : {}", custs.length);
				Customer cust = custs[0];
				log.debug("▶▶▶▶▶▶ [cert join] customer info for api : {}", cust.toString());
				response.setCustomer(cust);
				
				//final String wtyn = cust.getCustWtYn();
				final String wtdt = cust.getCustWtDttm();
				//if ("Y".equals(wtyn)) {
				if (StringUtils.hasText(wtdt)) {
					log.info("▶▶▶▶▶▶ [cert join] customer check withdraw : {}[{}]", cust.getCustWtYn(), cust.getCustWtDttm());
					response.setType(JoinDivisionType.WITHDRAW.getType()); // 탈퇴후 30일이 지나면 해당 값이 고객통합에서 고객정보화 함께 삭제됨.
					return response;
				}
				
				if (StringUtils.isEmpty(certResult.getPhone())) {
					certResult.setPhone(StringUtil.mergeMobile(cust));
					WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certResult);
				}				
				
				log.debug("▶▶▶▶▶▶ [cert join] customer count : {}", custs.length);
				response.setType(JoinDivisionType.EXIST.getType());
			}

		} else {

			log.info("▶▶▶▶▶▶ [cert join] CI 불일치 → 이름, 생년월일, 휴대폰으로 다시 조회");
			CustInfoVo newCustInfoVo = new CustInfoVo();
			newCustInfoVo.setCustName(custNameCert);
			newCustInfoVo.setAthtDtbr(custBirthCert);
			newCustInfoVo.setCustMobile(custPhoneCert);

			if (StringUtils.isEmpty(custNameCert) //
					|| StringUtils.isEmpty(custBirthCert) //
					|| StringUtils.isEmpty(custPhoneCert) //
			) { // 해당 정보가 없으면 2차 정보 안나오므로 신규로 간주

				log.info("▶▶▶▶▶▶ [cert join] 신규가입");
				response.setType(JoinDivisionType.CHANNEL_JOIN.getType());
				return response;
			}

			custresponse = this.customerApiService.getCustList(newCustInfoVo);
			final String custsRsltCd = custresponse.getRsltCd();
			log.debug("▶▶▶▶▶▶ [cert join] customer api result : {} -> {}", custRsltCd, custresponse.getRsltMsg());
			joinType = JoinDivisionType.getByCode(custsRsltCd);
			
			if (joinType == JoinDivisionType.JOIN) {
				log.info("▶▶▶▶▶▶ [cert join] 신규가입");
				response.setType(JoinDivisionType.CHANNEL_JOIN.getType());
				return response;
			} else  if (joinType == JoinDivisionType.ERROR) { // 시스템 오류
				log.info("▶▶▶▶▶▶ [cert join] customer check erro : {}", StringUtil.printJson(joinType));
				response.setType(JoinDivisionType.ERROR.getType());
				return response;
			} 
			
			custs = custresponse.getCicuemCuInfTcVo();
			if (custs != null && custs.length > 0) {
				
				// 휴면복구시 휴대폰 번호가 인증받은 휴대폰번호와 다른 경우(동일 CI)
				// - 복구된 고객정보가 [CI+이름] 또는 [CI+생년월일] 의 기준에 부합할 경우 인증받은 휴대폰번호로 update하여 VOC 최소화
				if (joinType == JoinDivisionType.DORMANCY) {
					if (!StringUtils.isEmpty(custPhoneCert)) { // jsjang 추가
						boolean isUpateCust = false;
						String incsNo = "";
						
						CustInfoResponse tempCustresponse = this.customerApiService.getCustList(newCustInfoVo);
						Customer tempCusts[] = tempCustresponse.getCicuemCuInfTcVo();
						// 휴면 복구 시 복구된 값으로 변경 - 2021.12.09
						custs = tempCustresponse.getCicuemCuInfTcVo();
						
						for (Customer customer : tempCusts) {
							log.debug("▶▶▶▶▶▶ custPhoneCert : {}  customer : {}", custPhoneCert, StringUtil.mergeMobile(customer));
							log.debug("▶▶▶▶▶▶ custCiNoCert : {}  customer : {}", custCiNoCert, customer.getCiNo());
							log.debug("▶▶▶▶▶▶ custNameCert : {}  customer : {}", custNameCert, customer.getCustNm());
							if (!custPhoneCert.equals(StringUtil.mergeMobile(customer)) &&
									(
										(custCiNoCert.equals(customer.getCiNo()) && custNameCert.equals(customer.getCustNm()))
										|| (custCiNoCert.equals(customer.getCiNo()) && custBirthCert.equals(customer.getAthtDtbr()))
									)
								) {
								log.debug("▶▶▶▶▶▶ [cert join] release dormancy. phone num update");
								isUpateCust = true;
								incsNo = customer.getIncsNo();
								break;
							}
						}
						
						if (isUpateCust && !incsNo.isEmpty()) {
							// 인증받은 휴대폰 번호로 update
							String mobile[] = StringUtil.splitMobile(custPhoneCert);
							log.debug("custPhoneCert : {}, incsNo : {}", custPhoneCert, incsNo);
							UpdateCustVo updateCustVo = new UpdateCustVo();
							updateCustVo.setIncsNo(incsNo);
							updateCustVo.setCellTidn(mobile[0]);
							updateCustVo.setCellTexn(mobile[1]);
							updateCustVo.setCellTlsn(mobile[2]);
							
							String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
							chcd = "000";
							log.debug("▶▶▶▶▶▶ [cert join] chcd : {}", chcd);
							updateCustVo.setChCd(chcd);
							updateCustVo.setChgChCd(chcd);
							updateCustVo.setLschId("OCP");
							
							UpdateCustResponse updateCustResponse = this.customerApiService.updateCust(updateCustVo);
							
							if ("ICITSVCOM999".equals(updateCustResponse.getRsltCd())) {
								// TODO : update error
								final String updateCustRsltCd = updateCustResponse.getRsltCd();
								JoinDivisionType updateJoinType = JoinDivisionType.getByCode(updateCustRsltCd);
								
								log.info("▶▶▶▶▶▶ [cert join] update customer erro : {}", StringUtil.printJson(updateJoinType));
								response.setType(JoinDivisionType.ERROR.getType());
								
								return response;
							}
						}
					}
				}
				
				response.setCustomerList(custs);
				if (custs.length == 1) {
					Customer cust = custs[0];
					log.debug("▶▶▶▶▶▶ [cert join] customer info for api : {}", StringUtil.printJson(cust));
					response.setCustomer(cust);

					//final String wtyn = cust.getCustWtYn();
					final String wtdt = cust.getCustWtDttm();
					//if ("Y".equals(wtyn)) {
					if (StringUtils.hasText(wtdt)) {
						log.info("▶▶▶▶▶▶ [cert join] customer check withdraw : {}[{}]", cust.getCustWtYn(), cust.getCustWtDttm());
						response.setType(JoinDivisionType.WITHDRAW.getType()); // 탈퇴후 30일이 지나면 해당 값이 고객통합에서 고객정보화 함께 삭제됨.
						return response;
					}
					
					if (StringUtils.isEmpty(certResult.getPhone())) {
						certResult.setPhone(StringUtil.mergeMobile(cust));
						WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certResult);
					}

					final String custNameApi = cust.getCustNm();
					final String custBirthApi = cust.getAthtDtbr(); // 6자리 유의
					final String custPhoneApi = StringUtil.mergeMobile(cust);
					final String custDrccCd = cust.getDrccCd();
					log.info("▶▶▶▶▶▶ [cert join] 고객통합api조회이름(2차)=본인인증이름({}={}), {}", custNameApi, custNameCert, custNameApi.equals(custNameCert));
					log.info("▶▶▶▶▶▶ [cert join] 고객통합api생년월일(2차)=본인인증생년월일({}={}), {}", custBirthApi, custBirthCert, custBirthApi.equals(custBirthCert));
					log.info("▶▶▶▶▶▶ [cert join] 고객통합api휴대폰(2차)=본인인증휴대폰({}={}), {}", custPhoneApi, custPhoneCert, custPhoneApi.equals(custPhoneCert));
					log.info("▶▶▶▶▶▶ [cert join] 고객통합api휴면고객여부: {}", custDrccCd);
					if (custNameApi.equals(custNameCert) && custBirthApi.equals(custBirthCert) && custPhoneApi.equals(custPhoneCert)) { // 3개 일치 하고 휴면고객이 아닌 경우 기 가입 안내, 불일치 시 신규 가입
						log.info("▶▶▶▶▶▶ [cert join] 본인인증결과와 조회결과 동일 → 가입사실");

						this.certService.updateOccupationCi(cust, certResult);

						response.setType(JoinDivisionType.EXIST.getType());
					} else if ("Y".equals(custDrccCd)) {
						log.info("▶▶▶▶▶▶ [dormancy join] 휴면회원 → 가입사실");
						response.setType(JoinDivisionType.EXIST.getType());
					} else {
						log.info("▶▶▶▶▶▶ [cert join] 신규가입");
						response.setType(JoinDivisionType.CHANNEL_JOIN.getType());
					}
				} else {
					Customer cust = custs[0];
					log.debug("▶▶▶▶▶▶ [cert join] customer info for api : {}", StringUtil.printJson(cust));
					response.setCustomer(cust);

					//final String wtyn = cust.getCustWtYn();
					final String wtdt = cust.getCustWtDttm();
					//if ("Y".equals(wtyn)) {
					if (StringUtils.hasText(wtdt)) {
						log.info("▶▶▶▶▶▶ [cert join] customer check withdraw : {}[{}]", cust.getCustWtYn(), cust.getCustWtDttm());
						response.setType(JoinDivisionType.WITHDRAW.getType()); // 탈퇴후 30일이 지나면 해당 값이 고객통합에서 고객정보화 함께 삭제됨.
						return response;
					}
					if (StringUtils.isEmpty(certResult.getPhone())) {
						certResult.setPhone(StringUtil.mergeMobile(cust));
						WebUtil.setSession(OmniConstants.CERT_RESULT_SESSION, certResult);
					}
					log.info("▶▶▶▶▶▶ [cert join] 2개 이상 --> 이미가입 → 마지막에 가입한 회원으로 경로 가입");
					response.setType(JoinDivisionType.EXIST.getType());
				}
			} else {
				log.info("▶▶▶▶▶▶ [cert join] 신규가입");
				response.setType(JoinDivisionType.CHANNEL_JOIN.getType());
			}
		}
		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 회원가입 시 동선 STEP 처리 - 본인인증 처리 후 
	 * 
	 * -------------------------------------------
	 * 
	 * X X X 탈퇴 후 30 일 이내 A0104
	 * X X X 신규고객 A0101
	 * X X O 자체고객 A0101
	 * O X X 타오프라인 경로 자체 ) 가입 고객  A0103 -> A0207 채널약관 동의 목록 노출 화면
	 * O X O 해당 경로 자체 만 가입된 고객 A0103 -> A0202 경로 자체 고객 ID 사용가능
	 * O X O 해당 경로 자체 만 가입된 고객 A0103 -> A0203 경로 자체 고객 ID 타인 사용
	 * O O X 경로 자체 ) 첫 방문 뷰티포인트 고객 A0103 -> A0105
	 * O O O 이미 가입된 고객 A0103
	 * 
	 * -------------------------------------------
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 20. 오후 5:17:10
	 * </pre>
	 * 
	 * @param joinDivType 고객통합 플랫폼 조회 후 처리된 가입타입
	 * @param incsno 통합고객번호
	 * @param chcd 경로코드
	 * @return
	 */
	public JoinStepVo joinStep(int joinDivType, final int incsno, final String chcd) {

		List<UmOmniUser> omniUsers = this.mgmtService.getOmniUserList(incsno);
		List<UmChUser> chUsers = this.mgmtService.getChannelUserList(incsno, chcd);
		omniUsers = (omniUsers == null) ? Collections.emptyList() : omniUsers;
		chUsers = (chUsers == null) ? java.util.Collections.emptyList() : chUsers;

		String userIncsNo = Integer.toString(incsno);
		final int omnicount = omniUsers.size();
		int chcount = chUsers.size();

		log.debug("▶▶▶▶▶▶ [join step] div type : {}", JoinDivisionType.get(joinDivType).toString());
		log.debug("▶▶▶▶▶▶ [join step] omnicount : {}, chcount : {}", omnicount, chcount);

		if (joinDivType == JoinDivisionType.WITHDRAW.getType()) {
			return new JoinStepVo(JoinDivisionType.WITHDRAW.getType(), JoinType.JOIN, omniUsers, chUsers, userIncsNo);
		}
		
		if (joinDivType == JoinDivisionType.ERROR.getType()) {
			return new JoinStepVo(JoinDivisionType.ERROR.getType(), JoinType.JOIN, omniUsers, chUsers, userIncsNo);
		}

		if (StringUtils.hasText(userIncsNo)) {
			if ("0".equals(userIncsNo)) { // 숫자형 빈값이면 통합고객없음. 신규가입
				userIncsNo = "";
				joinDivType = JoinDivisionType.CHANNEL_JOIN.getType();
				chcount = 0;
			}
		} else { // 문자형 빈값이면 통합고객없음. 신규가입
			joinDivType = JoinDivisionType.CHANNEL_JOIN.getType();
			chcount = 0;
		}

		if (joinDivType == JoinDivisionType.CHANNEL_JOIN.getType()) { // 통합고객없는 경우
			// * X X X 탈퇴 후 30 일 이내 A0104
			// * X X X 신규고객 A0101
			// * X X O 자체고객 A0101
			// 통합고객이 없는데 뷰포가 있는 고객은 없음.
			if (chcount > 0) { // 자체고객
				log.info("▶▶▶▶▶▶ [join step] 자체고객 --> {}", JoinType.CHANNEL.getDesc());
				return new JoinStepVo(joinDivType, JoinType.CHANNEL, omniUsers, chUsers, userIncsNo); // 자체고객 A0101
			} else {
				log.info("▶▶▶▶▶▶ [join step] 회원가입 진행 --> {}", JoinType.JOIN.getDesc());
				return new JoinStepVo(joinDivType, JoinType.JOIN, omniUsers, chUsers, userIncsNo); // 회원가입 진행 A0101
			}
		} else if (joinDivType == JoinDivisionType.EXIST.getType()) { // 통합고객이 있는 경우
			if (omnicount > 0) { // 뷰포 있음

				final UmOmniUser omniUser = omniUsers.get(0);

				// 로그인 불가능 정보 추출
				final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
				final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
				final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
				final String unlockTime = omniUser.getUnlockTime() == null ? "0" : omniUser.getUnlockTime();

				log.info("▶▶▶▶▶ [join step] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
				if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자
					// valid unlock time 이 아니면 lock 하지 않음.
					if (DateUtil.isValidUnlockTime(unlockTime)) {
						log.info("▶▶▶▶▶ [join step] user status under lock time : {}, current : {}", DateUtil.getUnixTimestampToDateString(unlockTime), DateUtil.getCurrentDateTimeString());
						return new JoinStepVo(JoinDivisionType.LOCK.getType(), JoinType.JOIN, omniUsers, chUsers, userIncsNo);
					} else {
						log.info("▶▶▶▶▶ [join step] user status passed lock time, do next process.");
					}
				}

				if (StringUtil.isTrue(accountDisabled)) { // 탈퇴사용자
					return new JoinStepVo(JoinDivisionType.WITHDRAW.getType(), JoinType.JOIN, omniUsers, chUsers, userIncsNo);
				}

				if (chcount > 0) { // 채널 있음
					log.info("▶▶▶▶▶▶ [join step] 뷰포 있음, 채널 있음 (O O O) 25 --> {}", JoinType.JOINED_OMNI_CH.getDesc());
					return new JoinStepVo(joinDivType, JoinType.JOINED_OMNI_CH, omniUsers, chUsers, userIncsNo); // A0103(온라인-로그인하기, 오프라인-확인) --> 가입사실(중복체크) - 옴니조회
				} else { // 채널없음 -> 경로(자체) 첫 방문 뷰포 고객
					log.info("▶▶▶▶▶▶ [join step] 뷰포 있음, 채널 없음 (O O X) 30 -> 경로(자체) 첫 방문 뷰포 고객 --> {}", JoinType.JOINED_OMNI.getDesc());
					return new JoinStepVo(joinDivType, JoinType.JOINED_OMNI, omniUsers, chUsers, userIncsNo); // A0103 --> A0105
				}
			} else { // 뷰포 없음
				if (chcount > 0) { // 채널 있음 -> 해당 경로(자체)만 가입고객 O X O
					log.info("▶▶▶▶▶▶ [join step] 뷰포 없음, 채널 있음 (O X O) 20 -> 해당 경로(자체)만 가입고객 --> {}", JoinType.COVERSION.getDesc());
					return new JoinStepVo(joinDivType, JoinType.COVERSION, omniUsers, chUsers, userIncsNo);
				} else { // 채널없음 -> 타 오프라인 경로(자체) 가입고객
					log.info("▶▶▶▶▶▶ [join step] 뷰포 없음, 채널 없음 (O X X) -> {}", JoinType.JOINED_OFF.getDesc());
					return new JoinStepVo(joinDivType, JoinType.JOINED_OFF, omniUsers, chUsers, userIncsNo); // A0103 --> A0207
				}
			}
		} else {
			if (omnicount > 0) { // 뷰포 있음

				final UmOmniUser omniUser = omniUsers.get(0);

				// 로그인 불가능 정보 추출
				final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
				final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
				final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
				final String unlockTime = omniUser.getUnlockTime() == null ? "0" : omniUser.getUnlockTime();

				log.info("▶▶▶▶▶ [join step] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
				if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자
					// valid unlock time 이 아니면 lock 하지 않음.
					if (DateUtil.isValidUnlockTime(unlockTime)) {
						log.info("▶▶▶▶▶ [join step] user status under lock time : {}, current : {}", DateUtil.getUnixTimestampToDateString(unlockTime), DateUtil.getCurrentDateTimeString());
						return new JoinStepVo(JoinDivisionType.LOCK.getType(), JoinType.JOIN, omniUsers, chUsers, userIncsNo);
					} else {
						log.info("▶▶▶▶▶ [join step] user status passed lock time, do next process.");
					}
				}

				if (StringUtil.isTrue(accountDisabled)) { // 탈퇴사용자
					return new JoinStepVo(JoinDivisionType.WITHDRAW.getType(), JoinType.JOIN, omniUsers, chUsers, userIncsNo);
				}

				if (chcount > 0) { // 채널 있음
					log.info("▶▶▶▶▶▶ [join step] 뷰포 있음, 채널 있음 --> {}", JoinType.JOINED.getDesc());
					return new JoinStepVo(joinDivType, JoinType.JOINED, omniUsers, chUsers, userIncsNo); // A0103(온라인-로그인하기, 오프라인-확인) -->
				} else { // 채널없음 -> 경로(자체) 첫 방문 뷰포 고객
					log.info("▶▶▶▶▶▶ [join step] 뷰포 있음, 채널 없음 -> 경로(자체) 첫 방문 뷰포 고객 --> {}", JoinType.JOINED.getDesc());
					return new JoinStepVo(joinDivType, JoinType.JOINED, omniUsers, chUsers, userIncsNo); // A0103 --> A0105
				}
			} else { // 뷰포 없음
				if (chcount > 0) { // 채널 있음 -> 해당 경로(자체)만 가입고객
					log.info("▶▶▶▶▶▶ [join step] 뷰포 없음, 채널 있음 -> 해당 경로(자체)만 가입고객 --> {}", JoinType.JOINED.getDesc());
					return new JoinStepVo(joinDivType, JoinType.COVERSION, omniUsers, chUsers, userIncsNo);
				} else { // 채널없음 -> 타 오프라인 경로(자체) 가입고객
					log.info("▶▶▶▶▶▶ [join step] 뷰포 없음, 채널 없음 -> 타 오프라인 경로(자체) 가입고객 --> {}", JoinType.JOINED.getDesc());
					return new JoinStepVo(joinDivType, JoinType.JOINED, omniUsers, chUsers, userIncsNo); // A0103 --> A0207
				}
			}
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 10. 21. 오후 4:55:39
	 * </pre>
	 * 
	 * @param joinDivType
	 * @param incsno
	 * @param chcd
	 * @return
	 */
	public JoinStepVo joinStepOff(int joinDivType, final int incsno, final String chcd, final CertResult certResult) {
		List<UmOmniUser> omniUsers = this.mgmtService.getOmniUserList(incsno);

		String userIncsNo = Integer.toString(incsno);
		
		CustInfoVo custInfoVo = new CustInfoVo();
		custInfoVo.setChCd(chcd);
		custInfoVo.setIncsNo(userIncsNo);
		custInfoVo.setCiNo(certResult.getCiNo());
		CustYnResponse custynResponse = this.customerApiService.getCustYn(custInfoVo);
		int chcount = 0;
		if ("ICITSVCOM000".equals(custynResponse.getRsltCd())) {
			chcount = 1; // 경로에 있음.
			if(chcd.equals(OmniConstants.OSULLOC_OFFLINE_CHCD)) { // 오설록 티하우스에서 회원가입 시 백화점 오설록으로 한번 더 체크
				chcount = 0;
				custInfoVo.setChCd(OmniConstants.OSULLOC_DEPARTMENT_CHCD);
				custynResponse = this.customerApiService.getCustYn(custInfoVo);
				if ("ICITSVCOM000".equals(custynResponse.getRsltCd())) {
					chcount = 1; // 백화점 오설록도 있음.
				}
			} else if(chcd.equals(OmniConstants.OSULLOC_DEPARTMENT_CHCD)) { // 백화점 오설록 에서 회원가입 시 오설록 티하우스로 한번 더 체크
				chcount = 0;
				custInfoVo.setChCd(OmniConstants.OSULLOC_OFFLINE_CHCD);
				custynResponse = this.customerApiService.getCustYn(custInfoVo);
				if ("ICITSVCOM000".equals(custynResponse.getRsltCd())) {
					chcount = 1; // 오설록 티하우스도 있음.
				}
			}
			
			if(chcount == 1 && (chcd.equals(OmniConstants.OSULLOC_OFFLINE_CHCD) || chcd.equals(OmniConstants.OSULLOC_DEPARTMENT_CHCD))) { // 오설록 티하우스, 백화점 오설록에서 회원 가입 시 오설록 Mall 필수약관도 체크
				chcount = 0;
				UmOmniUser omniUser = new UmOmniUser();
				String onlineChCd = ChannelPairs.getOnlineCd(chcd);
				omniUser.setIncsNo(userIncsNo);
				omniUser.setChCd(onlineChCd);
				
				if (this.termsService.hasTermsAgree(omniUser)) {
					chcount = 1;
				}
			}
		}

		omniUsers = (omniUsers == null) ? Collections.emptyList() : omniUsers;
		final int omnicount = omniUsers.size();

		log.debug("▶▶▶▶▶▶ [join step off] div type : {}", JoinDivisionType.get(joinDivType).toString());
		log.debug("▶▶▶▶▶▶ [join step off] omnicount : {}, chcount : {}", omnicount, chcount);

		if (joinDivType == JoinDivisionType.WITHDRAW.getType()) {
			return new JoinStepVo(JoinDivisionType.WITHDRAW.getType(), JoinType.JOIN, omniUsers, null, userIncsNo);
		}
		
		if (joinDivType == JoinDivisionType.ERROR.getType()) {
			return new JoinStepVo(JoinDivisionType.ERROR.getType(), JoinType.JOIN, omniUsers, null, userIncsNo);
		}

		if (StringUtils.hasText(userIncsNo)) {
			if ("0".equals(userIncsNo)) { // 숫자형 빈값이면 통합고객없음. 신규가입
				userIncsNo = "";
				joinDivType = JoinDivisionType.CHANNEL_JOIN.getType(); // ME-FO-A0101
				chcount = 0;
			}
		} else { // 문자형 빈값이면 통합고객없음. 신규가입
			joinDivType = JoinDivisionType.CHANNEL_JOIN.getType(); // ME-FO-A0101
			chcount = 0;
		}

		if (joinDivType == JoinDivisionType.CHANNEL_JOIN.getType()) { // 통합고객없는 경우
			return new JoinStepVo(joinDivType, JoinType.CHANNEL_OFF, omniUsers, null, userIncsNo); // 자체고객 A0101
		} else if (joinDivType == JoinDivisionType.EXIST.getType()) { // 통합고객이 있는 경우
			if (omnicount > 0) { // 뷰포 있음

				final UmOmniUser omniUser = omniUsers.get(0);

				// 로그인 불가능 정보 추출
				final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
				final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
				final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
				final String unlockTime = omniUser.getUnlockTime() == null ? "0" : omniUser.getUnlockTime();

				log.info("▶▶▶▶▶ [join step off] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
				if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자
					// valid unlock time 이 아니면 lock 하지 않음.
					if (DateUtil.isValidUnlockTime(unlockTime)) {
						log.info("▶▶▶▶▶ [join step off] user status under lock time : {}, current : {}", DateUtil.getUnixTimestampToDateString(unlockTime), DateUtil.getCurrentDateTimeString());
						return new JoinStepVo(JoinDivisionType.LOCK.getType(), JoinType.JOIN, omniUsers, null, userIncsNo);
					} else {
						log.info("▶▶▶▶▶ [join step off] user status passed lock time, do next process.");
					}
				}

				if (StringUtil.isTrue(accountDisabled)) { // 탈퇴사용자
					return new JoinStepVo(JoinDivisionType.WITHDRAW.getType(), JoinType.JOIN, omniUsers, null, userIncsNo);
				}

				if (chcount > 0) { // 채널 있음 --> 가입사실안내 ME-FO-A0103
					log.info("▶▶▶▶▶▶ [join step off] 뷰포 있음, 채널 있음, 오프라인 가입 사일 안내 --> {}", JoinType.JOINED_CH_OFF.getDesc());
					return new JoinStepVo(joinDivType, JoinType.JOINED_CH_OFF, omniUsers, null, userIncsNo); // A0103(온라인-로그인하기, 오프라인-확인)
				} else { // 채널없음 -> 경로(자체) 첫 방문 뷰포 고객 경로약관동의 ME-FO-A0105
					log.info("▶▶▶▶▶▶ [join step off] 뷰포 있음, 채널 없음 -> 오프라인 경로약관동의 --> {}", JoinType.JOINED_AGREE_CH_OFF.getDesc());
					return new JoinStepVo(joinDivType, JoinType.JOINED_AGREE_CH_OFF, omniUsers, null, userIncsNo); // A0105
				}
			} else { // 뷰포 없음

				if (chcount > 0) { // 채널 있음 -> 통합아이디 등록 ME-FO-A0216
					log.info("▶▶▶▶▶▶ [join step off] 뷰포 없음, 채널 있음 -> 해당 경로(자체)만 가입고객 --> {}", JoinType.JOIN_OFF.getDesc());
					return new JoinStepVo(joinDivType, JoinType.JOIN_OFF, omniUsers, null, userIncsNo);
				} else { // 채널없음 -> 신규고객등록 (ME-FO-A0101)
					log.info("▶▶▶▶▶▶ [join step off] 뷰포 없음, 채널 없음 -> {}", JoinType.JOINED_STEP_OFF.getDesc());
					return new JoinStepVo(joinDivType, JoinType.JOINED_STEP_OFF, omniUsers, null, userIncsNo); // A0103 --> A0207
				}

			}
		} else {
			return new JoinStepVo(joinDivType, JoinType.CHANNEL, omniUsers, null, userIncsNo); // 신규고객등록 ME-FO-A0101
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 로그인 시 동선 STEP 처리 
	 * author   : takkies
	 * date     : 2020. 8. 20. 오후 5:16:41
	 * 
	 * O O O 뷰티포인트 정상 로그인 처리
	 * O O O 경로 자체 고객 뷰티포인트 ID 가 1 개 A0204 단일 계정 화면 약관 비노출
	 * O O O 경로 자체 고객 뷰티포인트 ID 가 2 개이상 A0204 복수 계정 화면 약관 비노출
	 * O O X 뷰티포인트 A0105
	 * O X O 경로 자체 고객 동일 ID 사용가능 A0202
	 * O X O 경로 자체 고객 동일 ID 타인사용 A0203
	 * O X X 휴대폰 휴대폰로그인 A0207 채널약관 동의 목록 노출 화면
	 * O X O 휴대폰 휴대폰로그인 A0207
	 * X X O 경로 자체 고객 경로 자체 고객자체회원 A0201 전환가입 화면 전환가입으로 케이스로 이동
	 * </pre>
	 * 
	 * @param chCd 경로코드
	 * @param loginId 로그인 아이디
	 * @param loginPwd 로그인 비.밀.번.호
	 * @param status /login/step 일 경우 true
	 * @return
	 */
	@SuppressWarnings("static-access")
	public LoginStepVo loginStep(final String chCd, final String loginId, final String loginPwd, boolean status) {

		List<UmOmniUser> omniUsers = this.mgmtService.getOmniLoginUserList(loginId, loginPwd);
		omniUsers = (omniUsers == null) ? Collections.emptyList() : omniUsers;

		final int omnicount = omniUsers.size();

		if (omnicount > 0) { // 뷰티포인트
			final UmOmniUser omniUser = omniUsers.get(0);
			final String omniIncsNo = omniUser.getIncsNo();

			List<UmChUser> chUsers = this.mgmtService.getChannelUserList(Integer.parseInt(omniIncsNo), chCd);
			final int chcount = chUsers.size();
			
			//lock 어뷰징 계정 조회 202304
			String profile2 = this.systemInfo.getActiveProfiles()[0];
			profile2 = StringUtils.isEmpty(profile2) ? "dev" : profile2;
			profile2 = profile2.equalsIgnoreCase("default") ? "dev" : profile2;
			AbusingLockVo abusingLockVo = new AbusingLockVo();
			
			abusingLockVo.setChCd(chCd);
			abusingLockVo.setIncsNo(Integer.parseInt(omniUser.getIncsNo()));
			abusingLockVo.setClntIp((String) WebUtil.getSession(OmniConstants.Client_IP));
			abusingLockVo.setClntUaVl((String) WebUtil.getSession(OmniConstants.Client_Agent));
			if(status == true) {
				abusingLockVo.setDoAction("status");
			}
			
			log.debug("▶▶▶▶▶ [loginStep] 어뷰징 계정 조회 : {}", StringUtil.printJson(abusingLockVo));
			
			 ApiBaseResponse result = this.customerApiService.lockUserCheck(abusingLockVo);
			 if(result.getResultCode().equals(ResultCode.LOCK_TRUE_USER.getCode())) { //어뷰징 lock 계정
				 return new LoginStepVo(LoginType.LOCK_ABUSING, omniUsers, chUsers, omniIncsNo);
			 }else if (result.getResultCode().equals(ResultCode.LOCK_ACCESS_LIMIT_USER.getCode())) {
				 return new LoginStepVo(LoginType.LOCK_ACCESS_LIMIT, omniUsers, chUsers, omniIncsNo);
			}else if (result.getResultCode().equals(ResultCode.SYSTEM_ERROR.getCode())) {
				 return new LoginStepVo(LoginType.ERROR, omniUsers, chUsers, omniIncsNo);
			}else if (result.getResultCode().equals(ResultCode.LOCK_CLEAR_USER.getCode())) { //정상 계정
				log.info("▶▶▶▶▶ [login step] not aubusing account. pass");
			}
			 
			 
			if (chcount > 0) { // 정상로그인 처리
				// 로그인 불가능 정보 추출
				final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
				final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
				final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
				final String accountDormancy = omniUser.getUmUserDormancy(); // 휴면 사용자
				final String accountLastPasswordUpdate = omniUser.getLastPasswordUpdate(); // 마지막으로 비밀번호 변경한 날짜(UNIXTIME)
				final String accountPasswordReset = omniUser.getUmUserPasswordReset();
				final String unlockTime = omniUser.getUnlockTime() == null ? "0" : omniUser.getUnlockTime();

				log.info("▶▶▶▶▶ [login step] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
				if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자
					// valid unlock time 이 아니면 lock 하지 않음.
					if (DateUtil.isValidUnlockTime(unlockTime)) {
						final String remainUnlockTime = DateUtil.getRemainedUnlockTime(unlockTime);
						if (StringUtils.hasText(remainUnlockTime)) {
							omniUser.setUnlockTime(remainUnlockTime);
							omniUser.setFailedLoginAttempts(Integer.toString(DateUtil.getUnlockTimeTermSeconds(unlockTime)));
							omniUsers.add(omniUser);
						}
						log.info("▶▶▶▶▶ [login step] user status under lock time : {}, current : {}", DateUtil.getUnixTimestampToDateString(unlockTime), DateUtil.getCurrentDateTimeString());
						return new LoginStepVo(LoginType.LOCK, omniUsers, chUsers, omniIncsNo);
					} else {
						log.info("▶▶▶▶▶ [login step] user status passed lock time, do next process.");
					}
				}

				if (StringUtil.isTrue(accountDisabled)) { // 탈퇴사용자
					return new LoginStepVo(LoginType.DISABLED, omniUsers, chUsers, omniIncsNo);
				}

				log.debug("▶▶▶▶▶ [login step] login process user dormancy ? {}", StringUtil.isTrue(accountDormancy));
				if (StringUtil.isTrue(accountDormancy)) { // 휴면 사용자이면 휴면 해제
					boolean rtn = this.customerApiService.releaseDormancy(omniIncsNo, chCd);
					log.debug("▶▶▶▶▶ [login step] login process for dormancy : {}, {} {}", LoginType.LOGIN.toString(), omniIncsNo, rtn);
					if(!rtn) {
						return new LoginStepVo(LoginType.DORMANCYFAIL, omniUsers, chUsers, omniIncsNo);
					}
				}
				
				if (OmniConstants.ACCOUNT_PASSWORD_RESET.equals(accountPasswordReset)) {
					return new LoginStepVo(LoginType.PWDRESET, omniUsers, chUsers, omniIncsNo);
				}

				if (StringUtils.hasText(accountLastPasswordUpdate)) {
					int lastPasswordTerms = DateUtil.getLastPasswordUpdateTermDays(accountLastPasswordUpdate);
					int changeTerms = this.config.getChangePasswordTerm();
					log.info("▶▶▶▶▶ [login step] 마지막 비밀번호 변경후 {}(days) 지남, 변경 주기 : {}(days)", lastPasswordTerms, changeTerms);
					// 변경한 후의 기간이 지정한 기간을 지나면 변경해야함.
					if (lastPasswordTerms > changeTerms) {
						return new LoginStepVo(LoginType.PWDCHANGE, omniUsers, chUsers, omniIncsNo);
					}
				}
				
				omniUser.setChCd(chCd);
				
				// 전사 약관 + 경로 약관 모두 처리 시는 로그인으로
				boolean corpTerms = this.termsService.hasCorpTermsAgree(omniUser);
				boolean chTerms = this.termsService.hasTermsAgree(omniUser);
				log.debug("▶▶▶▶▶ [login step] 전사 약관 : {}, 경로 약관 : {}", corpTerms, chTerms);
				if(!corpTerms || !chTerms) {
					return new LoginStepVo(LoginType.AGREE, omniUsers, chUsers, omniIncsNo);
				}

				return new LoginStepVo(LoginType.LOGIN, omniUsers, chUsers, omniIncsNo);

			} else { // A0105 (O O X 케이스)

				omniUser.setChCd(chCd);

				// 전사 약관 + 경로 약관 모두 처리 시는 로그인으로
				boolean corpTerms = this.termsService.hasCorpTermsAgree(omniUser);
				boolean chTerms = this.termsService.hasTermsAgree(omniUser);
				log.debug("▶▶▶▶▶ [login step] 전사 약관 : {}, 경로 약관 : {}", corpTerms, chTerms);
				// 로그인 불가능 정보 추출
				final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
				final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
				final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
				final String accountDormancy = omniUser.getUmUserDormancy(); // 휴면 사용자
				final String accountLastPasswordUpdate = omniUser.getLastPasswordUpdate(); // 마지막으로 비밀번호 변경한 날짜(UNIXTIME)
				final String accountPasswordReset = omniUser.getUmUserPasswordReset();
				final String unlockTime = omniUser.getUnlockTime() == null ? "0" : omniUser.getUnlockTime();

				log.info("▶▶▶▶▶ [login step] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
				if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자

					// valid unlock time 이 아니면 lock 하지 않음.
					if (DateUtil.isValidUnlockTime(unlockTime)) {
						final String remainUnlockTime = DateUtil.getRemainedUnlockTime(unlockTime);
						if (StringUtils.hasText(remainUnlockTime)) {
							omniUser.setUnlockTime(remainUnlockTime);
							omniUser.setFailedLoginAttempts(Integer.toString(DateUtil.getUnlockTimeTermSeconds(unlockTime)));
							omniUsers.add(omniUser);
						}
						log.info("▶▶▶▶▶ [login step] user status under lock time : {}, current : {}", DateUtil.getUnixTimestampToDateString(unlockTime), DateUtil.getCurrentDateTimeString());
						return new LoginStepVo(LoginType.LOCK, omniUsers, chUsers, omniIncsNo);
					} else {
						log.debug("▶▶▶▶▶ [login step] user status passed lock time, do next process.");
					}

				}

				if (StringUtil.isTrue(accountDisabled)) { // 탈퇴사용자
					return new LoginStepVo(LoginType.DISABLED, omniUsers, chUsers, omniIncsNo);
				}
				
				log.debug("▶▶▶▶▶ [login step] login process user dormancy ? {}", StringUtil.isTrue(accountDormancy));
				if (StringUtil.isTrue(accountDormancy)) { // 휴면 사용자이면 휴면 해제
					boolean rtn = this.customerApiService.releaseDormancy(omniIncsNo, chCd);
					log.debug("▶▶▶▶▶ [login step] login process for dormancy : {}, {} {}", LoginType.LOGIN.toString(), omniIncsNo, rtn);
					if(!rtn) {
						return new LoginStepVo(LoginType.DORMANCYFAIL, omniUsers, chUsers, omniIncsNo);
					}
				}
				
				if (OmniConstants.ACCOUNT_PASSWORD_RESET.equals(accountPasswordReset)) {
					return new LoginStepVo(LoginType.PWDRESET, omniUsers, chUsers, omniIncsNo);
				}

				if (StringUtils.hasText(accountLastPasswordUpdate)) {
					int lastPasswordTerms = DateUtil.getLastPasswordUpdateTermDays(accountLastPasswordUpdate);
					int changeTerms = this.config.getChangePasswordTerm();
					log.info("▶▶▶▶▶ [login step] 마지막 비밀번호 변경후 {}(days) 지남, 변경 주기 : {}(days)", lastPasswordTerms, changeTerms);
					// 변경한 후의 기간이 지정한 기간을 지나면 변경해야함.
					if (lastPasswordTerms > changeTerms) {
						return new LoginStepVo(LoginType.PWDCHANGE, omniUsers, chUsers, omniIncsNo);
					}
				}

				// 브랜드 사이트의 경우 처리하지 않음 2021-08-03 hjw0228
				String profile = this.systemInfo.getActiveProfiles()[0];
				profile = StringUtils.isEmpty(profile) ? "dev" : profile;
				profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
				if (corpTerms && chTerms && !config.isBrandSite(chCd, profile)) {
					// 해당 경로시스템의 수신 동의 여부가 없을 경우 고객통합에 경로 가입 여부 확인 후 경로 가입 처리
					TermsVo termsVo = new TermsVo();
					termsVo.setChCd(chCd);
					List<TermsVo> termsVos = this.termsService.getCorpTerms(termsVo);
					if(termsVos == null || termsVos.size() == 0) {
						CustInfoVo custInfoVo = new CustInfoVo();
						custInfoVo.setIncsNo(omniIncsNo);
						custInfoVo.setChCd(chCd);

						CustYnResponse custYnResponse = this.customerApiService.getCustYn(custInfoVo);
						
						if(custYnResponse != null && "ICITSVCOM001".equals(custYnResponse.getRsltCd())) { // 경로 가입 되어 있지 않으면 경로 가입 API 호출
							
							boolean isMarketing = StringUtil.isTrue(this.config.getChannelApi(chCd, "ismarketing", profile));
							// 해당 경로 미 가입 상태 시 문자 수신 동의 여부를 받는 경우 ex) APMall
							if(isMarketing) {
								return new LoginStepVo(LoginType.AGREE, omniUsers, chUsers, omniIncsNo);
							}
							
							final Channel channel = commonService.getChannel(chCd);
							log.debug("고객통합 경로 등록 API {} : {}", chCd, loginId);

							CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedChannelCustomerData(channel, omniIncsNo, loginId, SecurityUtil.getEncodedWso2Password(loginPwd));

							log.debug("▶▶▶▶▶▶ integrated channel customer api data : {}", StringUtil.printJson(chCustRequest));

							CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
							log.debug("▶▶▶▶▶▶ integrated channel {} customer api response : {}", channel.getChCdNm(), StringUtil.printJson(chjoinResponse));

							// 경로 고객 존재하는 경우도 성공으로 판단
							boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse.getRsltCd());

							log.info("▶▶▶▶▶▶ integrated channel result : {} ---> {}", chjoinResponse.getRsltCd(), success);
						}
					}
					
					return new LoginStepVo(LoginType.LOGIN, omniUsers, chUsers, omniIncsNo);
				} else if (corpTerms && chTerms && config.isBrandSite(chCd, profile)) { // 브랜드 사이트의 경우 로그인 처리 2021-08-03 hjw0228
					return new LoginStepVo(LoginType.LOGIN, omniUsers, chUsers, omniIncsNo);
				}
				
				return new LoginStepVo(LoginType.AGREE, omniUsers, chUsers, omniIncsNo);

			}

		} else { // 뷰티포인트 없음(O X O)
			
			// 이미 전환가입한 사용자
			List<UmChUser> chTransUsers = this.mgmtService.getChannelTransferUserList(loginId, loginPwd, chCd);
			if (chTransUsers != null && chTransUsers.size() > 0) {
				UmChUser chUser = chTransUsers.get(0);
				return new LoginStepVo(LoginType.ALREADY_TRNS_CH, null, chTransUsers, Integer.toString(chUser.getIncsNo()));
			}

			List<UmChUser> chUsers = this.mgmtService.getChannelLoginUserList(chCd, loginId, loginPwd);

			final int chcount = chUsers.size();

			if (chcount > 0) {

				final UmChUser chUser = chUsers.get(0);

				if (chUser.getIncsNo() <= 0) { // X X O 통합고객번호없음. 자체회원인 경우 전환가입으로 처리해야함.
					return new LoginStepVo(LoginType.CONV_JOIN, omniUsers, chUsers, null);
				}

				final String chInCsNo = Integer.toString(chUser.getIncsNo());

				// 통합고객으로 조회
				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setIncsNo(chInCsNo);

				CustInfoResponse custResponse = this.customerApiService.getCustList(custInfoVo);

				boolean emptyitgcheck = false;
				if (custResponse != null) {
					if("ICITSVCOM999.".contentEquals(custResponse.getRsltCd())) {
						return new LoginStepVo(LoginType.DORMANCYFAIL, omniUsers, chUsers, chInCsNo);
					}
					Customer users[] = custResponse.getCicuemCuInfTcVo();
					if (users == null || users.length == 0) {
						emptyitgcheck = true;
					} else {
						log.debug("▶▶▶▶▶ [login step] login process customer : {}", StringUtil.printJson(users));
					}
					
					// 2022.06.17 고객통합에 해당 경로 가입 여부 조회 (CustList 에서 고객통합번호, 채널코드로 조회 불가능함에 따라 호출)
					custInfoVo.setChCd(chCd);
					CustYnResponse CustYnResponse = this.customerApiService.getCustYn(custInfoVo);
					
					if("ICITSVCOM001".equals(CustYnResponse.getRsltCd())) {
						emptyitgcheck = true;
					}
					
					UmOmniUser omniUser = new UmOmniUser();
					omniUser.setChCd(chCd);
					omniUser.setIncsNo(custResponse.getCicuemCuInfTcVo()[0].getIncsNo());
					
					boolean corpTerms = this.termsService.hasCorpTermsAgree(omniUser);
					boolean chTerms = this.termsService.hasTermsAgree(omniUser);
					
					if(!corpTerms || !chTerms) {
						emptyitgcheck = true;
					}
				} else {
					emptyitgcheck = true;
				}

				if (emptyitgcheck) { // A0201 전환가입
					log.info("▶▶▶▶▶ [login step] A0201 전환가입 --> {}", LoginType.CONV_JOIN.getDesc());
					return new LoginStepVo(LoginType.CONV_JOIN, omniUsers, chUsers, chInCsNo);

				} else {

					List<UmOmniUser> omniUserList = this.mgmtService.getOmniUserList(chUser.getIncsNo());
					final int omniListcount = omniUserList.size();
					if (omniListcount > 0) { // A0204
						log.info("▶▶▶▶▶ [login step] A0204 통합 가입 진행 --> {}", LoginType.INTG_JOIN.getDesc());
						return new LoginStepVo(LoginType.INTG_JOIN, omniUserList, chUsers, chInCsNo);
					} else { // A0202, A0203
						log.info("▶▶▶▶▶ [login step] A0202, A0203 전환 가입 진행 --> {}", LoginType.TRNS_JOIN.getDesc());
						return new LoginStepVo(LoginType.TRNS_JOIN, omniUsers, chUsers, chInCsNo);
					}
				}

			} else {
				log.debug("▶▶▶▶▶ [login step] 신규회원 --> {}", LoginType.NEW.getDesc());
				return new LoginStepVo(LoginType.NEW, omniUsers, chUsers, null);
			}

		}

	}

	/**
	 * 
	 * <pre>
	 * comment  : 통합회원전환 조건 체크
	 *  // CI 일치
	 *  		- 성명일치 -> 전환
	 *  		- 성명비일치, 휴대폰번호, 생년월일 일치 -> 전환(개명)
	 *  		- 성명비일치, 휴대폰번호, 생년월일 비일치 -> 고객센터(개명+휴대폰번호변경)
	 *  		- 2건 이상 조회 -> 이미가입된 회원
	 *  			마지막에 가입한 회원으로 전환가입
	 * 				동일한 CI의 고객이 여러명 있을시 최근에 가입한 정보부터 나오기 때문에
	 * 				제일 처음 나오는게 마지막 가입한 회원
	 * 				개인정보 판단도 해야합니다.
	 * 				여기에서 일치 정보가 2개 이상이라는건 CI/개인정보가 모두 일치하는(처음 케이스) 정보가 2개 이상일때를 의미
	 *  // CI 비일치
	 *  		- 성명, 휴대폰번호, 생년월일 일치 -> 전환 (CI 업데이트)
	 *  		- 성명, 휴대폰번호, 생년월일 비일치 -> 신규
	 * author   : takkies
	 * date     : 2020. 9. 4. 오후 2:48:21
	 * </pre>
	 * 
	 * @param certResult
	 * @return
	 */
	public JoinResponse certConditionForConversion(final CertResult certResult) {
		JoinResponse response = new JoinResponse();

		final String custCiNoCert = certResult.getCiNo();
		final String custNameCert = certResult.getName();
		final String custBirthCert = certResult.getBirth();
		String custPhoneCert = certResult.getPhone(); // IPIN인 경우 없음.

		// 1. 고객통합 플랫폼 CI로 조회
		CustInfoVo custInfo = new CustInfoVo();
		custInfo.setCiNo(custCiNoCert);
		CustInfoResponse custresponse = this.customerApiService.getCustList(custInfo);
		final String custRsltCd = custresponse.getRsltCd();
		log.info("▶▶▶▶▶▶ [cert conversion] customer api result : {} -> {}", custRsltCd, custresponse.getRsltMsg());
		JoinDivisionType joinType = JoinDivisionType.getByCode(custRsltCd);
		response.setType(joinType.getType());

		if (joinType == JoinDivisionType.WITHDRAW) {
			log.info("▶▶▶▶▶▶ [cert conversion] customer check withdraw : {}", StringUtil.printJson(joinType));
			response.setType(JoinDivisionType.WITHDRAW.getType()); // 탈퇴후 30일이 지나면 해당 값이 고객통합에서 고객정보화 함께 삭제됨.
			response.setCustomer(custresponse.getCicuemCuInfTcVo()[0]);
			return response;
		}

		Customer custs[] = custresponse.getCicuemCuInfTcVo(); // 고객통합 정보 사용자 조회
		if (custs != null && custs.length > 0) {
			// 전환가입 진행시 인증받은 휴대폰번호와 다른 경우(동일 CI)
			// - 기가입된 고객정보가 [CI+이름]의 기준에 부합할 경우 인증받은 휴대폰번호로 update하여 VOC 최소화
			boolean isUpateCust = false; // jsjang 추가
			String incsNo = "";
			
			if (!StringUtils.isEmpty(custPhoneCert)) {
				for (Customer customer : custs) {
					log.debug("▶▶▶▶▶▶ custPhoneCert : {}  customer : {}", custPhoneCert, StringUtil.mergeMobile(customer));
					if (!custPhoneCert.equals(StringUtil.mergeMobile(customer)) 
							&& (custCiNoCert.equals(customer.getCiNo()) && custNameCert.equals(customer.getCustNm()))
						) {
						log.debug("▶▶▶▶▶▶ [cert conversion] phone update");
						isUpateCust = true;
						incsNo = customer.getIncsNo();
						break;
					}
				}
				
				if (isUpateCust && !incsNo.isEmpty()) {
					// 인증받은 휴대폰 번호로 update
					String mobile[] = StringUtil.splitMobile(custPhoneCert);
					
					UpdateCustVo updateCustVo = new UpdateCustVo();
					updateCustVo.setIncsNo(incsNo);
					updateCustVo.setCellTidn(mobile[0]);
					updateCustVo.setCellTexn(mobile[1]);
					updateCustVo.setCellTlsn(mobile[2]);
					
					String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
					chcd = "000";
					updateCustVo.setChCd(chcd);
					updateCustVo.setChgChCd(chcd);
					updateCustVo.setLschId("OCP");
					
					UpdateCustResponse updateCustResponse = this.customerApiService.updateCust(updateCustVo);
					
					if ("ICITSVCOM999".equals(updateCustResponse.getRsltCd())) {
						// TODO : update error
						final String updateCustRsltCd = updateCustResponse.getRsltCd();
						JoinDivisionType updateJoinType = JoinDivisionType.getByCode(updateCustRsltCd);
						
						log.info("▶▶▶▶▶▶ [cert conversion] update customer erro : {}", StringUtil.printJson(updateJoinType));
						response.setType(JoinDivisionType.ERROR.getType());
						
						return response;
					}
					
					custresponse = this.customerApiService.getCustList(custInfo);
					custs = custresponse.getCicuemCuInfTcVo(); // 고객통합 정보 사용자 조회
				}
			}
			
			response.setCustomerList(custs);
			if (custs.length == 1) {
				Customer cust = custs[0];
				log.debug("▶▶▶▶▶▶ [cert conversion] customer info for api : {}", cust.toString());
				response.setCustomer(cust);

				//final String wtyn = cust.getCustWtYn();
				final String wtdt = cust.getCustWtDttm();
				//if ("Y".equals(wtyn)) {
				if (StringUtils.hasText(wtdt)) {
					log.info("▶▶▶▶▶▶ [cert conversion] customer check withdraw : {}[{}]", cust.getCustWtYn(), cust.getCustWtDttm());
					response.setCustomer(cust);
					response.setType(JoinDivisionType.WITHDRAW.getType()); // 탈퇴후 30일이 지나면 해당 값이 고객통합에서 고객정보화 함께 삭제됨.
					return response;
				}

				final String custCiToApi = cust.getCiNo();
				log.info("▶▶▶▶▶▶ [cert conversion] 고객통합api조회CI=본인인증CI : {}", custCiToApi.equals(custCiNoCert));

				final String custNameToApi = cust.getCustNm();
				if (custNameToApi.equals(custNameCert)) {
					log.info("▶▶▶▶▶▶ [cert conversion] 성명일치 → 전환가입");
					response.setType(JoinDivisionType.CONVERSION.getType());
				} else {
					final String custBirthApi = cust.getAthtDtbr();
					final String custPhoneApi = StringUtil.mergeMobile(cust);

					log.info("▶▶▶▶▶▶ [cert conversion] 고객통합api생년월일(2차)=본인인증생년월일({}={}), {}", custBirthApi, custBirthCert, custBirthApi.equals(custBirthCert));
					
					if (StringUtils.isEmpty(custPhoneCert)) {
						custPhoneCert = custPhoneApi; // IPIN 인 경우 없으므로 
					}
					
					log.info("▶▶▶▶▶▶ [cert conversion] 고객통합api휴대폰(2차)=본인인증휴대폰({}={}), {}", custPhoneApi, custPhoneCert, custPhoneApi.equals(custPhoneCert));
					if (custBirthApi.equals(custBirthCert) //
							&& custPhoneApi.equals(custPhoneCert)) { // 본인인증결과와 조회결과 동일 -> 가입사실
						log.info("▶▶▶▶▶▶ [cert conversion] 생년월일,휴대폰일치(개명으로 판단) → 전환가입");
						response.setType(JoinDivisionType.CONVERSION.getType());

					} else {
						log.info("▶▶▶▶▶▶ [cert conversion] 이름, 생년월일, 휴대폰 불일치(고객정보 오류로 판단) → 고객센터");
						response.setType(JoinDivisionType.INFO_MISMATCH.getType());
					}

				}

			} else { // 2개 이상, 이미가입, 마지막에 가입한 회원으로 경로 가입
				// 일치 정보가 2개 이상이라는건 CI/개인정보가 모두 일치하는(처음 케이스) 정보가 2개 이상일때를 의미
				log.debug("▶▶▶▶▶▶ [cert conversion] customer count : {}", custs.length);
				Customer cust = custs[0];
				log.debug("▶▶▶▶▶▶ [cert conversion] customer info for api : {}", StringUtil.printJson(cust));
				response.setCustomer(cust);

				//final String wtyn = cust.getCustWtYn();
				final String wtdt = cust.getCustWtDttm();
				//if ("Y".equals(wtyn)) {
				if (StringUtils.hasText(wtdt)) {
					log.info("▶▶▶▶▶▶ [cert conversion] customer check withdraw : {}[{}]", cust.getCustWtYn(), cust.getCustWtDttm());
					response.setCustomer(cust);
					response.setType(JoinDivisionType.WITHDRAW.getType()); // 탈퇴후 30일이 지나면 해당 값이 고객통합에서 고객정보화 함께 삭제됨.
					return response;
				}

				response.setType(JoinDivisionType.CONVERSION.getType());
			}
		} else {

			log.info("▶▶▶▶▶▶ [cert conversion] CI 불일치 → 이름, 생년월일, 휴대폰으로 다시 조회");
			CustInfoVo newCustInfoVo = new CustInfoVo();
			newCustInfoVo.setCustName(custNameCert);
			newCustInfoVo.setAthtDtbr(custBirthCert);
			newCustInfoVo.setCustMobile(custPhoneCert);

			custresponse = this.customerApiService.getCustList(newCustInfoVo);
			final String custsRsltCd = custresponse.getRsltCd();
			log.info("▶▶▶▶▶▶ [cert conversion] customer api result : {} -> {}", custRsltCd, custresponse.getRsltMsg());
			joinType = JoinDivisionType.getByCode(custsRsltCd);
			custs = custresponse.getCicuemCuInfTcVo();
			if (custs != null && custs.length > 0) {
				response.setCustomerList(custs);
				Customer cust = custs[0];
				log.debug("▶▶▶▶▶▶ [cert conversion] customer info for api : {}", cust.toString());
				response.setCustomer(cust);
				if (custs.length == 1) {

					//final String wtyn = cust.getCustWtYn();
					final String wtdt = cust.getCustWtDttm();
					//if ("Y".equals(wtyn)) {
					if (StringUtils.hasText(wtdt)) {
						log.info("▶▶▶▶▶▶ [cert conversion] customer check withdraw : {}[{}]", cust.getCustWtYn(), cust.getCustWtDttm());
						response.setCustomer(cust);
						response.setType(JoinDivisionType.WITHDRAW.getType()); // 탈퇴후 30일이 지나면 해당 값이 고객통합에서 고객정보화 함께 삭제됨.
						return response;
					}

					final String custNameApi = cust.getCustNm();
					final String custBirthApi = cust.getAthtDtbr();
					final String custPhoneApi = StringUtil.mergeMobile(cust);
					log.info("▶▶▶▶▶▶ [cert conversion] 고객통합api조회이름(2차)=본인인증이름({}={}), {}", custNameApi, custNameCert, custNameApi.equals(custNameCert));
					log.info("▶▶▶▶▶▶ [cert conversion] 고객통합api생년월일(2차)=본인인증생년월일({}={}), {}", custBirthApi, custBirthCert, custBirthApi.equals(custBirthCert));
					log.info("▶▶▶▶▶▶ [cert conversion] 고객통합api휴대폰(2차)=본인인증휴대폰({}={}), {}", custPhoneApi, custPhoneCert, custPhoneApi.equals(custPhoneCert));
					if (custNameApi.equals(custNameCert) && custBirthApi.equals(custBirthCert) && custPhoneApi.equals(custPhoneCert)) { // 전환

						// 통합DB에 존재하는 회원으로 판단 전환 프로세스 진행
						// 통합DB의 CI 교체 점유인증 활동 CASE로 판단 (통합DB에 있는 CI가 점유인증 CI인 경우에만)
						this.certService.updateOccupationCi(cust, certResult);

						log.info("▶▶▶▶▶▶ [cert conversion] 점유인증 활동 CASE로 판단 → 전환 프로세스");
						response.setType(JoinDivisionType.CONVERSION.getType());
					} else {
						log.info("▶▶▶▶▶▶ [cert conversion] 신규가입");
						response.setType(JoinDivisionType.CHANNEL_JOIN.getType());
					}
				} else {
					log.debug("▶▶▶▶▶▶ [cert conversion] customer count : {}", custs.length);
					log.debug("▶▶▶▶▶▶ [cert conversion] 2개 이상 --> 이미가입 → 마지막에 가입한 회원으로 경로 가입");
					response.setCustomer(cust);

					//final String wtyn = cust.getCustWtYn();
					final String wtdt = cust.getCustWtDttm();
					//if ("Y".equals(wtyn)) {
					if (StringUtils.hasText(wtdt)) {
						log.info("▶▶▶▶▶▶ [cert conversion] customer check withdraw : {}[{}]", cust.getCustWtYn(), cust.getCustWtDttm());
						response.setCustomer(cust);
						response.setType(JoinDivisionType.WITHDRAW.getType()); // 탈퇴후 30일이 지나면 해당 값이 고객통합에서 고객정보화 함께 삭제됨.
						return response;
					}

					response.setType(JoinDivisionType.EXIST.getType());
				}
			} else {
				log.info("▶▶▶▶▶▶ [cert conversion] 신규가입");
				response.setType(JoinDivisionType.CHANNEL_JOIN.getType());
			}
		}

		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 9. 4. 오후 6:27:45
	 * </pre>
	 * 
	 * @param chCd
	 * @param incsNo
	 * @return
	 */
	public LoginStepVo conversionStep(final String chCd, final String incsNo, final String loginId) {

		List<UmOmniUser> omniUsers = this.mgmtService.getOmniConversionUserList(incsNo);
		List<UmChUser> chUsers = null;
		String chcsWebId = WebUtil.getStringSession("chcsWebId"); // 로그인 시 받은 로그인 아이디를 받아서 검색해야함.
		if (StringUtils.isEmpty(chcsWebId)) {
			String encLoginId = WebUtil.getStringSession(OmniConstants.XID_SESSION);
			chcsWebId = SecurityUtil.getXValue(encLoginId, false);
		}

		if (StringUtils.hasText(chcsWebId)) {
			chUsers = this.mgmtService.getChannelUserIdList(chCd, chcsWebId);
		} else {
			chUsers = this.mgmtService.getChannelUserIdList(chCd, loginId);
		}

		final int omnicount = omniUsers.size();
		final int chcount = chUsers.size();

		log.debug("▶▶▶▶▶ [conversion step] omnicount : {}, chcount : {}", omnicount, chcount);

		if (omnicount > 0) {
			// 복수계정 체크는 화면에서 직접
			log.info("▶▶▶▶▶ [conversion step] login conversion type : {}", LoginType.TRNS_BP.toString());
			return new LoginStepVo(LoginType.TRNS_BP, omniUsers, chUsers, incsNo);
		} else {
			if (chcount > 0) {
				// 옴니에 동일 아이디가 있는지 체크
				UmChUser sameIdChUser = new UmChUser();
				sameIdChUser.setChCd(chCd);
				sameIdChUser.setChcsWebId(chcsWebId);
				boolean same = this.mgmtService.hasSameLoginIdByLoginId(sameIdChUser);

				log.debug("▶▶▶▶▶ [conversion step] use same login id already ? {}", same);

				if (same) { // 동일 아이디 타인 사용
					log.info("▶▶▶▶▶ [conversion step] login conversion type(동일 아이디 타인 사용) : {}", LoginType.TRNS_CH_OTHER.toString());
					return new LoginStepVo(LoginType.TRNS_CH_OTHER, omniUsers, chUsers, incsNo);
				} else { // 동일 아이디 사용
					log.info("▶▶▶▶▶ [conversion step] login conversion type(동일 아이디 사용) : {}", LoginType.TRNS_CH_MINE.toString());
					return new LoginStepVo(LoginType.TRNS_CH_MINE, omniUsers, chUsers, incsNo);
				}
			} else {
				return new LoginStepVo(LoginType.NEW, omniUsers, chUsers, incsNo);
			}
		}
	}

	/**
	 * 
	 * <pre>
	 * comment  : 휴대폰 로그인 시 동선 STEP 처리 - 본인인증 처리 후
	 * author   : takkies
	 * date     : 2020. 8. 20. 오후 5:16:19
	 * 
	 * O O O 정상 로그인 처리 통합 고객번호 or ID 가 2 개 이상이면 로그인 불가
	 * O O X A0105
	 * O X O 동일ID 사용가능 A0207
	 * O X O 동일ID 타인사용 A0207
	 * O X X A0216 채널약관 동의 목록 노출 화면
	 * </pre>
	 * 
	 * @param incsno 통합고객번호
	 * @param chcd 경로코드
	 * @return
	 */
	public LoginStepVo loginPhoneStep(String chcd, int incsno) {

		final String accountIncsNo = Integer.toString(incsno);

		List<UmOmniUser> omniUsers = this.mgmtService.getOmniUserList(incsno);
		omniUsers = (omniUsers == null) ? java.util.Collections.emptyList() : omniUsers;
		final int omnicount = omniUsers.size();

		if (omnicount > 0) { // 로그인 처리 : 정상로그인 처리 시도
			UmOmniUser omniUser = omniUsers.get(0);
			final String omniIncsNo = omniUser.getIncsNo();
			List<UmChUser> chUsers = this.mgmtService.getChannelUserList(Integer.parseInt(omniIncsNo), chcd);
			final int chcount = chUsers.size();
			if (omnicount > 1) {
				log.info("▶▶▶▶▶ [mobile login step] 고객센터 --> {}", LoginType.CS.getDesc());
				return new LoginStepVo(LoginType.CS, omniUsers, chUsers, accountIncsNo);
			}

			if (chcount > 0) { // 경로 : 정상로그인
				// 로그인 불가능 정보 추출
				final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
				final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
				final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
				final String accountDormancy = omniUser.getUmUserDormancy(); // 휴면 사용자
				final String unlockTime = omniUser.getUnlockTime() == null ? "0" : omniUser.getUnlockTime();
				log.info("▶▶▶▶▶ [mobile login step] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);

				if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자
					// valid unlock time 이 아니면 lock 하지 않음.
					if (DateUtil.isValidUnlockTime(unlockTime)) {
						final String remainUnlockTime = DateUtil.getRemainedUnlockTime(unlockTime);
						if (StringUtils.hasText(remainUnlockTime)) {
							omniUser.setUnlockTime(remainUnlockTime);
							omniUser.setFailedLoginAttempts(Integer.toString(DateUtil.getUnlockTimeTermSeconds(unlockTime)));
							omniUsers.add(omniUser);
						}
						log.info("▶▶▶▶▶ [mobile login step] user status under lock time : {}, current : {}", DateUtil.getUnixTimestampToDateString(unlockTime), DateUtil.getCurrentDateTimeString());
						return new LoginStepVo(LoginType.LOCK, omniUsers, chUsers, accountIncsNo);
					} else {
						log.info("▶▶▶▶▶ [mobile login step] user status passed lock time, do next process.");
					}
				}

				if (StringUtil.isTrue(accountDisabled)) { // 탈퇴사용자
					return new LoginStepVo(LoginType.DISABLED, omniUsers, chUsers, accountIncsNo);
				}

				if (omnicount > 1) { // ME-FO-A0204, 약관은 받을 필요 없음,제3자 정보제공동의 필요, 뷰.포ID 2개이상 : 뷰.포 ID 선택
					log.info("▶▶▶▶▶ [mobile login step] 로그인 진행 --> {}", LoginType.TRNS_JOIN.getDesc());
					return new LoginStepVo(LoginType.TRNS_JOIN, omniUsers, chUsers, accountIncsNo);
				} else {
					log.debug("▶▶▶▶▶ [mobile login step] 로그인 진행");
				}

				log.debug("▶▶▶▶▶ [mobile login step] login process : {}", LoginType.LOGIN.toString());
				if (StringUtil.isTrue(accountDormancy)) { // 휴면 사용자이면 휴면 해제
					boolean rtn = this.customerApiService.releaseDormancy(accountIncsNo, chcd);
					log.debug("▶▶▶▶▶ [mobile login step] login process for dormancy : {}, {} {}", LoginType.LOGIN.toString(), accountIncsNo, rtn);
					if(!rtn) {
						return new LoginStepVo(LoginType.DORMANCYFAIL, omniUsers, chUsers, omniIncsNo);
					}
				}
				
				omniUser.setChCd(chcd);
				
				// 전사 약관 + 경로 약관 모두 처리 시는 로그인으로
				boolean corpTerms = this.termsService.hasCorpTermsAgree(omniUser);
				boolean chTerms = this.termsService.hasTermsAgree(omniUser);
				log.debug("▶▶▶▶▶ [login step] 전사 약관 : {}, 경로 약관 : {}", corpTerms, chTerms);
				if(!corpTerms || !chTerms) {
					return new LoginStepVo(LoginType.AGREE, omniUsers, chUsers, omniIncsNo);
				}

				return new LoginStepVo(LoginType.LOGIN, omniUsers, chUsers, accountIncsNo);
			} else { // A0105
				// 통합회원으로 로그인 시 진입 채널에 약관동의가 되어있지 않은 경우 제공
				omniUser.setChCd(chcd);
				// 전사 약관 + 경로 약관 모두 처리 시는 로그인으로
				boolean corpTerms = this.termsService.hasCorpTermsAgree(omniUser);
				boolean chTerms = this.termsService.hasTermsAgree(omniUser);

				log.debug("▶▶▶▶▶ [mobile login step] 전사 약관 : {}, 경로 약관 : {}", corpTerms, chTerms);

				if (corpTerms && chTerms) {
					// 로그인 불가능 정보 추출
					final String accountLock = omniUser.getAccountLock(); // 잠김 사용자가 아닌 경우, accountLock == null || accountLock == false
					final String accountState = omniUser.getAccountState(); // 잠김 사용자가 아닌 경우, accountState == null || accountState == UNLOCKED
					final String accountDisabled = omniUser.getAccountDisabled(); // 탈퇴된 사용자가 아닌 경우, accountDisabled == null || accountDisabled == false
					final String accountDormancy = omniUser.getUmUserDormancy(); // 휴면 사용자
					final String accountLastPasswordUpdate = omniUser.getLastPasswordUpdate(); // 마지막으로 비밀번호 변경한 날짜(UNIXTIME)
					final String accountPasswordReset = omniUser.getUmUserPasswordReset();
					final String unlockTime = omniUser.getUnlockTime() == null ? "0" : omniUser.getUnlockTime();

					log.info("▶▶▶▶▶ [mobile login step] user status : accountLock : {}, accountState : {}, accountDisabled : {}", accountLock, accountState, accountDisabled);
					if (StringUtil.isTrue(accountLock) && OmniConstants.ACCOUNT_STATE_LOCK.equals(accountState)) { // 잠김사용자

						// valid unlock time 이 아니면 lock 하지 않음.
						if (DateUtil.isValidUnlockTime(unlockTime)) {
							final String remainUnlockTime = DateUtil.getRemainedUnlockTime(unlockTime);
							if (StringUtils.hasText(remainUnlockTime)) {
								omniUser.setUnlockTime(remainUnlockTime);
								omniUser.setFailedLoginAttempts(Integer.toString(DateUtil.getUnlockTimeTermSeconds(unlockTime)));
								omniUsers.add(omniUser);
							}
							log.info("▶▶▶▶▶ [mobile login step] user status under lock time : {}, current : {}", DateUtil.getUnixTimestampToDateString(unlockTime), DateUtil.getCurrentDateTimeString());
							return new LoginStepVo(LoginType.LOCK, omniUsers, chUsers, omniIncsNo);
						} else {
							log.info("▶▶▶▶▶ [mobile login step] user status passed lock time, do next process.");
						}

					}

					if (StringUtil.isTrue(accountDisabled)) { // 탈퇴사용자
						return new LoginStepVo(LoginType.DISABLED, omniUsers, chUsers, omniIncsNo);
					}
					
					if (StringUtil.isTrue(accountDormancy)) { // 휴면 사용자이면 휴면 해제
						boolean rtn = this.customerApiService.releaseDormancy(omniIncsNo, chcd);
						log.debug("▶▶▶▶▶ [mobile login step] login process for dormancy : {}, {} {}", LoginType.LOGIN.toString(), omniIncsNo, rtn);
						if(!rtn) {
							return new LoginStepVo(LoginType.DORMANCYFAIL, omniUsers, chUsers, omniIncsNo);
						}
					}

					if (OmniConstants.ACCOUNT_PASSWORD_RESET.equals(accountPasswordReset)) {

						return new LoginStepVo(LoginType.PWDRESET, omniUsers, chUsers, omniIncsNo);
					}

					if (StringUtils.hasText(accountLastPasswordUpdate)) {
						int lastPasswordTerms = DateUtil.getLastPasswordUpdateTermDays(accountLastPasswordUpdate);
						int changeTerms = this.config.getChangePasswordTerm();
						log.info("▶▶▶▶▶ [mobile login step] 마지막 비밀번호 변경후 {}(days) 지남, 변경 주기 : {}(days)", lastPasswordTerms, changeTerms);
						// 변경한 후의 기간이 지정한 기간을 지나면 변경해야함.
						if (lastPasswordTerms > changeTerms) {
							return new LoginStepVo(LoginType.PWDCHANGE, omniUsers, chUsers, omniIncsNo);
						}
					}
					
					// 브랜드 사이트의 경우 처리하지 않음 2021-08-03 hjw0228
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					if (corpTerms && chTerms && !config.isBrandSite(chcd, profile)) {
						// 해당 경로시스템의 수신 동의 여부가 없을 경우 고객통합에 경로 가입 여부 확인 후 경로 가입 처리
						TermsVo termsVo = new TermsVo();
						termsVo.setChCd(chcd);
						List<TermsVo> termsVos = this.termsService.getCorpTerms(termsVo);
						if(termsVos == null || termsVos.size() == 0) {
							CustInfoVo custInfoVo = new CustInfoVo();
							custInfoVo.setIncsNo(omniIncsNo);
							custInfoVo.setChCd(chcd);

							CustYnResponse custYnResponse = this.customerApiService.getCustYn(custInfoVo);
							
							if(custYnResponse != null && "ICITSVCOM001".equals(custYnResponse.getRsltCd())) { // 경로 가입 되어 있지 않으면 경로 가입 API 호출
								
								boolean isMarketing = StringUtil.isTrue(this.config.getChannelApi(chcd, "ismarketing", profile));
								// 해당 경로 미 가입 상태 시 문자 수신 동의 여부를 받는 경우 ex) APMall
								if(isMarketing) {
									return new LoginStepVo(LoginType.AGREE, omniUsers, chUsers, omniIncsNo);
								}
								
								final Channel channel = commonService.getChannel(chcd);
								log.debug("고객통합 경로 등록 API {} : {}", chcd, omniUser.getUmUserName());

								CreateCustChannelRequest chCustRequest = JoinData.buildIntegratedChannelCustomerData(channel, omniIncsNo, omniUser.getUmUserName(), omniUser.getUmUserPassword());

								log.debug("▶▶▶▶▶▶ integrated channel customer api data : {}", StringUtil.printJson(chCustRequest));

								CreateCustChannelJoinResponse chjoinResponse = customerApiService.createCustChannelMember(chCustRequest);
								log.debug("▶▶▶▶▶▶ integrated channel {} customer api response : {}", channel.getChCdNm(), StringUtil.printJson(chjoinResponse));

								// 경로 고객 존재하는 경우도 성공으로 판단
								boolean success = "ICITSVCOM000".equals(chjoinResponse.getRsltCd()) || "ICITSVBIZ157".equals(chjoinResponse.getRsltCd());

								log.info("▶▶▶▶▶▶ integrated channel result : {} ---> {}", chjoinResponse.getRsltCd(), success);
							}
						}
						
						return new LoginStepVo(LoginType.LOGIN, omniUsers, chUsers, omniIncsNo);
					} else if (corpTerms && chTerms && config.isBrandSite(chcd, profile)) { // 브랜드 사이트의 경우 로그인 처리 2021-08-03 hjw0228
						return new LoginStepVo(LoginType.LOGIN, omniUsers, chUsers, omniIncsNo);
					}

					return new LoginStepVo(LoginType.LOGIN, omniUsers, chUsers, omniIncsNo);
				}
				return new LoginStepVo(LoginType.AGREE, omniUsers, chUsers, omniIncsNo);
			}
		} else { // 뷰티포인트 없는 경우

			List<UmChUser> chUsers = this.mgmtService.getChannelUserList(incsno, chcd);

			final int chcount = chUsers.size();

			if (chcount > 0) {

				final UmChUser chUser = chUsers.get(0);

				if (chUser.getIncsNo() <= 0) { // 통합고객번호없음. 자체회원인 경우 A0217
					return new LoginStepVo(LoginType.CONV_JOIN, omniUsers, chUsers, null);
				}

				final String chInCsNo = Integer.toString(chUser.getIncsNo());

				if (chcount > 1) { // 채널 복수ID -> 고객센터 안내 A0500
					return new LoginStepVo(LoginType.CS, omniUsers, chUsers, chInCsNo);
				}

				CustInfoVo custInfoVo = new CustInfoVo();
				custInfoVo.setIncsNo(chInCsNo);

				// 휴대폰 로그인은 통합고객없는 케이스 없음.
				CustInfoResponse custResponse = this.customerApiService.getCustList(custInfoVo);

				boolean emptyitgcheck = false;
				if (custResponse != null) {
					if("ICITSVCOM999.".contentEquals(custResponse.getRsltCd())) {
						return new LoginStepVo(LoginType.DORMANCYFAIL, omniUsers, chUsers, chInCsNo);
					}
					
					Customer users[] = custResponse.getCicuemCuInfTcVo();
					if (users == null || users.length == 0) {
						emptyitgcheck = true;
					}
					
					// 2022.06.17 고객통합에 해당 경로 가입 여부 조회 (CustList 에서 고객통합번호, 채널코드로 조회 불가능함에 따라 호출)
					custInfoVo.setChCd(chcd);
					CustYnResponse CustYnResponse = this.customerApiService.getCustYn(custInfoVo);
					
					if("ICITSVCOM001".equals(CustYnResponse.getRsltCd())) {
						emptyitgcheck = true;
					}
					
					UmOmniUser omniUser = new UmOmniUser();
					omniUser.setChCd(chcd);
					omniUser.setIncsNo(custResponse.getCicuemCuInfTcVo()[0].getIncsNo());
					
					boolean corpTerms = this.termsService.hasCorpTermsAgree(omniUser);
					boolean chTerms = this.termsService.hasTermsAgree(omniUser);
					
					if(!corpTerms || !chTerms) {
						emptyitgcheck = true;
					}
				} else {
					emptyitgcheck = true;
				}
				
				if (emptyitgcheck) { // A0217

					log.info("▶▶▶▶▶ [mobile login step] 로그인 진행 --> {}", LoginType.CONV_JOIN.getDesc());
					return new LoginStepVo(LoginType.CONV_JOIN, omniUsers, chUsers, chInCsNo);

				} else {

					log.info("▶▶▶▶▶ [mobile login step] O X O --> {}", LoginType.TRNS_JOIN.getDesc());

					return new LoginStepVo(LoginType.TRNS_JOIN, null, chUsers, chInCsNo);
				}

			} else {
				log.info("▶▶▶▶▶ [mobile login step] 신규회원 통합아이디 등록(207) --> {}", LoginType.NEW.getDesc());
				return new LoginStepVo(LoginType.NEW, omniUsers, chUsers, null);

			}

		}
	}

}
