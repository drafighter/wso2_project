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
package com.amorepacific.oneap.auth.join.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.api.service.ApiEndPoint;
import com.amorepacific.oneap.auth.api.service.CustomerApiService;
import com.amorepacific.oneap.auth.common.service.DormancyService;
import com.amorepacific.oneap.auth.join.mapper.JoinMapper;
import com.amorepacific.oneap.auth.join.vo.JoinResponse;
import com.amorepacific.oneap.auth.mgmt.service.MgmtService;
import com.amorepacific.oneap.auth.step.AuthStep;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.LocaleUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.JoinStepVo;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.ApiBaseResponse;
import com.amorepacific.oneap.common.vo.api.IncsRcvData;
import com.amorepacific.oneap.common.vo.cert.CertResult;
import com.amorepacific.oneap.common.vo.dormancy.DormancyResponse;
import com.amorepacific.oneap.common.vo.dormancy.DormancyVo;
import com.amorepacific.oneap.common.vo.kakao.KakaoNoticeRequest;
import com.amorepacific.oneap.common.vo.user.Customer;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.join.service 
 *    |_ JoinService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 8. 6.
 * @version : 1.0
 * @author : mcjan
 */
@Slf4j
@Service
public class JoinService {

	@Autowired
	private JoinMapper joinMapper;

	@Autowired
	private AuthStep authStep;

	@Autowired
	private DormancyService dormancyService;

	@Autowired
	private MgmtService mgmtService;
	
	@Autowired
	private ApiEndPoint apiEndpoint;
	
	@Autowired
	private CustomerApiService customerApiService;
	
	@Autowired
	private MessageSource messageSource;

	/**
	 * 
	 * <pre>
	 * comment  : 회원 가입 케이스 분기
	 * 
	 *	회원가입 케이스
	 *	ICITSVCOM001 : 통합고객이 존재하지 않습니다
	 *
	 *	고객센터 안내 케이스(/mgmt/csinfo)
	 *	ICITSVBIZ135: 성명정보 불일치 ----> 2단계 체크 (비일치 ->고객센터, 일치 -> 가입사실)
	 *	
	 *  가입사실 안내 케이스
	 *	ICITSVCOM000 : 정상
	 *	ICITSVBIZ155 : 휴면고객정보가 존재
	 *	ECOMSVVAL004 : 중복된 {0} 입니다.   (멤버십카드번호, CI번호)
	 *	
	 *  오류 처리 케이스
	 *	ICITSVCOM003 : 필수항목 누락
	 *	ICITSVCOM999 : 시스템오류
	 *
	 * -------------------------------------------
	 * 
	 *	X X X 탈퇴 후 30 일 이내 A0104
	 *	X X X 신규고객 A0101
	 *	X X O 자체고객 A0101
	 *	O X X 타오프라인 경로 자체 ) 가입 고객  A0103 -> A0207 채널약관 동의 목록 노출 화면
	 *	O X O 해당 경로 자체 만 가입된 고객 A0103 -> A0202 경로 자체 고객 ID 사용가능
	 *	O X O 해당 경로 자체 만 가입된 고객 A0103 -> A0203 경로 자체 고객 ID 타인 사용
	 *	O O X 경로 자체 ) 첫 방문 뷰티포인트 고객 A0103 -> A0105
	 *	O O O 이미 가입된 고객 A0103
	 *
	 * -------------------------------------------
	 * 
	 * author   : takkies
	 * date     : 2020. 8. 12. 오후 7:59:46
	 * </pre>
	 * 
	 * @param certResult
	 * @return
	 */
	public JoinResponse checkJoinCondition(final CertResult certResult, final boolean isOffline) {

		// 본인인증 CI 조회
		JoinResponse response = this.authStep.certJoinByCiNo(certResult);
		log.debug("▶▶▶▶▶▶ [checkJoinCondition] JoinResponse : {}", StringUtil.printJson(response));

		int incsno;
		String chcd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
		Customer customer = response.getCustomer();
		if (customer != null) {
			incsno = StringUtils.hasText(customer.getIncsNo()) ? Integer.parseInt(customer.getIncsNo()) : 0;
		} else {
			incsno = WebUtil.getSession(OmniConstants.INCS_NO_SESSION) == null ? 0 : (int) WebUtil.getSession(OmniConstants.INCS_NO_SESSION);
		}

		// OMNI, 경로조회
		JoinStepVo joinstep = null;
		if (isOffline) {
			joinstep = this.authStep.joinStepOff(response.getType(), incsno, chcd, certResult);
		} else {
			joinstep = this.authStep.joinStep(response.getType(), incsno, chcd);
		}
		response.setJoinStep(joinstep);

		return response;
	}

	public int getCountAvaiableUserId(final String webLoginId) {
		return this.joinMapper.getCountAvaiableUserId(webLoginId);
	}

	/**
	 * <pre>
	 * comment  : 휴면 고객인지 조회
	 * author   : mcjan
	 * date     : 2020. 8. 11. 오후 1:35:14
	 * </pre>
	 * 
	 * @param dormancyCustVo
	 * @return
	 */
	public boolean isDormancyCustomer(final String incsNo) {

		if (StringUtils.isEmpty(incsNo) || "0".equals(incsNo)) {
			return false;
		}

		String dormancy = null;

		if (StringUtils.hasText(incsNo)) {
			dormancy = joinMapper.isDormancyUser(incsNo);
		} else {

		}

		return (dormancy != null && ("true".equalsIgnoreCase(dormancy) || "Y".equalsIgnoreCase(dormancy)));
	}

	/**
	 * 
	 * <pre>
	 * comment  : 휴면 복구 및 사용자명 변경
	 * author   : takkies
	 * date     : 2020. 10. 12. 오후 12:13:31
	 * </pre>
	 * 
	 * @param incsNo
	 * @param chCd
	 * @return
	 */
	public String releaseDormancyCustomerName(String incsNo, String chCd) {

		if (StringUtils.isEmpty(incsNo) || "0".equals(incsNo)) {
			return "";
		}

		final StopWatch stopwatch = new StopWatch("release dormancy custoer name EAI Service");
		if (!stopwatch.isRunning()) {
			stopwatch.start("release dormancy eai service");
		}
		
		String custname = "";
		DormancyVo dormancyVo = new DormancyVo();
		dormancyVo.setIncsNo(incsNo);
		dormancyVo.setChCd(chCd);

		log.debug("▶▶▶▶▶▶ [release dormancy] incsNo : {}, chCd : {}", incsNo, chCd);

		// EAI 휴면해제 API call
		DormancyResponse response = this.dormancyService.releaseDormancy(dormancyVo);
		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		
		if (response == null) {
			return "";
		}
		
		log.info("▶▶▶▶▶▶ [release dormancy] response : {}", StringUtil.printJson(response));

		String rtnCode = response.getRESPONSE().getHEADER().getRTN_CODE();
		rtnCode = StringUtils.isEmpty(rtnCode) ? response.getRESPONSE().getHEADER().getRTN_TYPE() : rtnCode;
		log.debug("▶▶▶▶▶▶ [release dormancy] rtnCode : {}", rtnCode);
		if (rtnCode.equals(OmniConstants.SEND_SMS_EAI_SUCCESS)) {
			custname = response.getRESPONSE().getHEADER().getCSTMNM();

			IncsRcvData incsRcvData = new IncsRcvData();
			incsRcvData.setCustNm(custname);
			incsRcvData.setIncsNo(Integer.parseInt(incsNo));
			incsRcvData.setDrccCd("N"); // 휴면 해제
			if (!stopwatch.isRunning()) {
				stopwatch.start("insert or update recieve data for eai trigger");
			}

			if (this.mgmtService.existRcvData(incsRcvData)) { // 통합고객수신데이터가 존재할 경우
				this.mgmtService.updateRcvName(incsRcvData); // 통합고객수신데이터 업데이트(사용자명, 휴면해제플래그)
			} else {
				this.mgmtService.insertRcvName(incsRcvData); // 통합고객수신데이터 인서트(사용자명, 휴면해제플래그)
			}

			this.joinMapper.updateDormancyUser(incsNo); // 옴니 사용자 휴면 해제
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			log.info("\n" + stopwatch.prettyPrint());
		}
	

		return custname;
	}
	
	/**
	 * 
	 * <pre>
	 * comment  : 휴면 복구 시 [RA-01403: no data found] 휴면 DB에 사용자 없는 경우 확인, 정상인 경우 사용자명 변경
	 * author   : takkies
	 * date     : 2020. 10. 12. 오후 12:13:31
	 * </pre>
	 * 
	 * @param incsNo
	 * @param chCd
	 * @return
	 */
	public DormancyResponse releaseDormancyCustomerName1(String incsNo, String chCd) {

		if (StringUtils.isEmpty(incsNo) || "0".equals(incsNo)) {
			return null;
		}

		final StopWatch stopwatch = new StopWatch("release dormancy custoer name EAI Service");
		if (!stopwatch.isRunning()) {
			stopwatch.start("release dormancy eai service");
		}
		
		String custname = "";
		DormancyVo dormancyVo = new DormancyVo();
		dormancyVo.setIncsNo(incsNo);
		dormancyVo.setChCd(chCd);

		log.debug("▶▶▶▶▶▶ [release dormancy] incsNo : {}, chCd : {}", incsNo, chCd);

		// EAI 휴면해제 API call
		DormancyResponse response = this.dormancyService.releaseDormancy(dormancyVo);
		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		
		if (response == null) {
			return null;
		}
		
		log.info("▶▶▶▶▶▶ [release dormancy] response : {}", StringUtil.printJson(response));

		String rtnCode = response.getRESPONSE().getHEADER().getRTN_CODE();
		rtnCode = StringUtils.isEmpty(rtnCode) ? response.getRESPONSE().getHEADER().getRTN_TYPE() : rtnCode;
		log.debug("▶▶▶▶▶▶ [release dormancy] rtnCode : {}", rtnCode);
		if (rtnCode.equals(OmniConstants.SEND_SMS_EAI_SUCCESS)) {
			custname = response.getRESPONSE().getHEADER().getCSTMNM();

			IncsRcvData incsRcvData = new IncsRcvData();
			incsRcvData.setCustNm(custname);
			incsRcvData.setIncsNo(Integer.parseInt(incsNo));
			incsRcvData.setDrccCd("N"); // 휴면 해제
			if (!stopwatch.isRunning()) {
				stopwatch.start("insert or update recieve data for eai trigger");
			}

			if (this.mgmtService.existRcvData(incsRcvData)) { // 통합고객수신데이터가 존재할 경우
				this.mgmtService.updateRcvName(incsRcvData); // 통합고객수신데이터 업데이트(사용자명, 휴면해제플래그)
			} else {
				this.mgmtService.insertRcvName(incsRcvData); // 통합고객수신데이터 인서트(사용자명, 휴면해제플래그)
			}

			this.joinMapper.updateDormancyUser(incsNo); // 옴니 사용자 휴면 해제
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			log.info("\n" + stopwatch.prettyPrint());
		}
	

//		return custname;
		return response;
	}

	/**
	 * <pre>
	 * comment  : 휴면 복구 신청 (옴니 업데이트 처리 이동)
	 * author   : mcjan
	 * date     : 2020. 8. 11. 오후 2:00:30
	 * </pre>
	 * 
	 * @param releaseDormancyVo
	 * @return
	 */
	public boolean releaseDormancyCustomer(String incsNo, String chCd) {

		if (StringUtils.isEmpty(incsNo) || "0".equals(incsNo)) {
			return true;
		}

		final StopWatch stopwatch = new StopWatch("release dormancy customer EAI Service");
		
		DormancyVo dormancyVo = new DormancyVo();
		dormancyVo.setIncsNo(incsNo);
		dormancyVo.setChCd(chCd);

		log.debug("▶▶▶▶▶▶ [release dormancy] incsNo : {}, chCd : {}", incsNo, chCd);

		if (!stopwatch.isRunning()) {
			stopwatch.start("release dormancy eai service");
		}
		// EAI 휴면해제 API call
		DormancyResponse response = this.dormancyService.releaseDormancy(dormancyVo);

		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		
		if (response == null) {
			return false;
		}
		
		log.debug("▶▶▶▶▶▶ [release dormancy] response : {}", StringUtil.printJson(response));

		String rtnCode = response.getRESPONSE().getHEADER().getRTN_CODE();
		rtnCode = StringUtils.isEmpty(rtnCode) ? response.getRESPONSE().getHEADER().getRTN_TYPE() : rtnCode;
		log.debug("▶▶▶▶▶▶ [release dormancy] rtnCode : {}", rtnCode);

		if (rtnCode.equals(OmniConstants.SEND_SMS_EAI_SUCCESS)) {
			final String name = response.getRESPONSE().getHEADER().getCSTMNM();

			IncsRcvData incsRcvData = new IncsRcvData();
			incsRcvData.setCustNm(name);
			incsRcvData.setIncsNo(Integer.parseInt(incsNo));
			incsRcvData.setDrccCd("N"); // 휴면 해제
			if (!stopwatch.isRunning()) {
				stopwatch.start("insert or update recieve data for eai trigger");
			}
			if (this.mgmtService.existRcvData(incsRcvData)) { // 통합고객수신데이터가 존재할 경우
				this.mgmtService.updateRcvName(incsRcvData); // 통합고객수신데이터 업데이트(사용자명, 휴면해제플래그)
				// this.mgmtService.updateRcvDormancy(incsRcvData); // 통합고객수신데이터 업데이트(휴면해제플래그)
			} else {
				this.mgmtService.insertRcvName(incsRcvData); // 통합고객수신데이터 인서트(사용자명, 휴면해제플래그)
				// this.mgmtService.insertRcvDormancy(incsRcvData); // 통합고객수신데이터 인서트(휴면해제플래그)
			}

			// DB업데이트 - EAI 성공시 처리하도록 이동하였음.
			boolean rtn = joinMapper.updateDormancyUser(incsNo) > 0; // 옴니 사용자 휴면 해제
			
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			
			log.info("\n" + stopwatch.prettyPrint());
			return rtn;
		}
		return false;
	}
	
	/**
	 * <pre>
	 * comment  : 휴면 복구 시 [RA-01403: no data found] 휴면 DB에 사용자 없는 경우 확인, 정상인 경우  휴면 복구 신청 (옴니 업데이트 처리 이동)
	 * author   : mcjan
	 * date     : 2020. 8. 11. 오후 2:00:30
	 * </pre>
	 * 
	 * @param releaseDormancyVo
	 * @return -1 : 실패, 0 : EAI 성공시 처리하도록 이동하였음, 1 : EAI 실패 시 처리  2: [RA-01403: no data found] 휴면 DB에 사용자 없는 경우
	 */
	public int releaseDormancyCustomer1(String incsNo, String chCd) {

		if (StringUtils.isEmpty(incsNo) || "0".equals(incsNo)) {
			return -1;
		}

		final StopWatch stopwatch = new StopWatch("release dormancy customer EAI Service");
		
		DormancyVo dormancyVo = new DormancyVo();
		dormancyVo.setIncsNo(incsNo);
		dormancyVo.setChCd(chCd);

		log.debug("▶▶▶▶▶▶ [release dormancy] incsNo : {}, chCd : {}", incsNo, chCd);

		if (!stopwatch.isRunning()) {
			stopwatch.start("release dormancy eai service");
		}
		// EAI 휴면해제 API call
		DormancyResponse response = this.dormancyService.releaseDormancy(dormancyVo);

		if (stopwatch.isRunning()) {
			stopwatch.stop();
		}
		
		if (response == null) {
			return -1;
		}
		
		log.debug("▶▶▶▶▶▶ [release dormancy] response : {}", StringUtil.printJson(response));

		String rtnCode = response.getRESPONSE().getHEADER().getRTN_CODE();
		rtnCode = StringUtils.isEmpty(rtnCode) ? response.getRESPONSE().getHEADER().getRTN_TYPE() : rtnCode;
		log.debug("▶▶▶▶▶▶ [release dormancy] rtnCode : {}", rtnCode);

		if (rtnCode.equals(OmniConstants.SEND_SMS_EAI_SUCCESS)) {
			final String name = response.getRESPONSE().getHEADER().getCSTMNM();

			IncsRcvData incsRcvData = new IncsRcvData();
			incsRcvData.setCustNm(name);
			incsRcvData.setIncsNo(Integer.parseInt(incsNo));
			incsRcvData.setDrccCd("N"); // 휴면 해제
			if (!stopwatch.isRunning()) {
				stopwatch.start("insert or update recieve data for eai trigger");
			}
			if (this.mgmtService.existRcvData(incsRcvData)) { // 통합고객수신데이터가 존재할 경우
				this.mgmtService.updateRcvName(incsRcvData); // 통합고객수신데이터 업데이트(사용자명, 휴면해제플래그)
				// this.mgmtService.updateRcvDormancy(incsRcvData); // 통합고객수신데이터 업데이트(휴면해제플래그)
			} else {
				this.mgmtService.insertRcvName(incsRcvData); // 통합고객수신데이터 인서트(사용자명, 휴면해제플래그)
				// this.mgmtService.insertRcvDormancy(incsRcvData); // 통합고객수신데이터 인서트(휴면해제플래그)
			}

			// DB업데이트 - EAI 성공시 처리하도록 이동하였음.
			boolean rtn = joinMapper.updateDormancyUser(incsNo) > 0; // 옴니 사용자 휴면 해제
			
			if (stopwatch.isRunning()) {
				stopwatch.stop();
			}
			
			log.info("\n" + stopwatch.prettyPrint());
			return rtn ? 0 : 1;
		} else if (rtnCode.equals("E") // 휴면 복구 시 에러 메시지 [RA-01403: no data found] : 휴면 DB에 사용자 없는 경우 확인 신규 가입 진행
				&& response.getRESPONSE().getHEADER().getRTN_MSG().equals("[ORA-01403: no data found]")) {
			return 2;
		}
		
		return -1;
	}

	/**
	 * <pre>
	 * comment  : 채널 필수 약관 동의 여부 조회 . 모두 동의시 1, 미동의 약관이 있으면 0 
	 * author   : mcjang
	 * date     : 2020. 8. 7. 오후 1:43:35
	 * </pre>
	 * 
	 * @param incsNo
	 * @param channelCode
	 * @return
	 */
	public boolean isReqAgreeTermsOfChannel(String incsNo, String chCd) {

		if (StringUtils.isEmpty(incsNo) || "0".equals(incsNo)) {
			return true;
		}

		Map<String, Object> sqlParam = new HashMap<>();
		sqlParam.put("incsNo", incsNo);
		sqlParam.put("chCd", chCd);

		return joinMapper.getAcceptTermsOfChannelCount(sqlParam) > 0;
	}

	public ApiBaseResponse sendKakaoNotice(final Customer customer, final Channel channel) {
		KakaoNoticeRequest request = KakaoNoticeRequest.builder()
				.ID(this.apiEndpoint.getKakaoNoticeId())
				.STATUS("1")
				.CALLBACK(this.apiEndpoint.getKakaoNoticeCallback())
				.TEMPLATE_CODE(this.apiEndpoint.getKakaoNoticeTemplateCode())
				.FAILED_TYPE(this.apiEndpoint.getKakaoNoticeFailedType())
				.FAILED_SUBJECT(this.apiEndpoint.getKakaoNoticeFailedSubject())
				.PROFILE_KEY(this.apiEndpoint.getKakaoNoticeProfileKey())
				.APPL_CL_CD(this.apiEndpoint.getKakaoNoticeApplClCd())
				.PLTF_CL_CD(this.apiEndpoint.getKakaoNoticePltfClCd()).build();
		
		// 카카오 알림톡 발송될 휴대폰번호 설정 (네아로 휴대전화번호)
		request.setPHONE(StringUtil.mergeMobile(customer));
		
		// 카카오 알림톡 메세지 발송 시간 설정
		request.setREQDATE(DateUtil.getTimestampAfterSecond("yyyy-MM-dd HH:mm:ss", 60));
		
		// 카카오 알림톡 메세지 설정
		String msg = "안녕하세요, 아모레퍼시픽 뷰티포인트입니다.\r\n"
				+ "\r\n"
				+ "㈜아모레퍼시픽은 (주)카카오(으)로부터 고객님의 개인정보를 제공받았으며, 개인정보보호법 제20조에 의거하여 아래와 같이 개인정보 수집 출처를 안내해 드립니다.\r\n"
				+ "\r\n"
				+ "- 개인정보 수집 출처: " + "(주)카카오\r\n"
				+ "- 개인정보 수집 항목: " + this.messageSource.getMessage("kakaosync.join.kakao.notice.talk.category", null, LocaleUtil.getLocale()) + "\r\n"
				+ "- 개인정보 처리 목적: " + this.messageSource.getMessage("kakaosync.join.kakao.notice.talk.purpose", null, LocaleUtil.getLocale()) + "\r\n"
				+ "- 개인정보 보유 및 이용기간: " + DateUtil.getCurrentDateString("yyyy-MM-dd") + "부터 회원 탈퇴 또는 동의 철회 시까지" + "\r\n"
				+ "\r\n"
				+ "개인정보 처리를 원치 않는 경우 아모레퍼시픽 개인정보 처리 동의 철회 페이지(" + this.messageSource.getMessage("kakaosync.join.kakao.notice.talk.url", null, LocaleUtil.getLocale()) + ")를 통해 개인정보 처리 정지를 요청하실 수 있습니다.";
		request.setMSG(msg);
		request.setFAILED_MSG(msg);
		
		return customerApiService.sendKakaoNoticeTalkEai(request);
	}
}
