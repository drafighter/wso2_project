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
package com.amorepacific.oneap.api.v2.join.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.api.v2.join.mapper.JoinMapper;
import com.amorepacific.oneap.api.v2.mgmt.service.MgmtV2Service;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.api.IncsRcvData;
import com.amorepacific.oneap.common.vo.dormancy.DormancyResponse;
import com.amorepacific.oneap.common.vo.dormancy.DormancyVo;

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
	private DormancyService dormancyService;

	@Autowired
	private MgmtV2Service mgmtService;

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

}
