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
 * Date   	          : 2020. 11. 5..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.auth.step;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.auth.join.vo.JoinRequest;
import com.amorepacific.oneap.common.code.ResultCode;
import com.amorepacific.oneap.common.util.DateUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.ChannelPairs;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.OnOffline;
import com.amorepacific.oneap.common.vo.api.ApiProcessResponse;
import com.amorepacific.oneap.common.vo.api.ApiStep;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * com.amorepacific.oneap.auth.step 
 *    |_ ApiOnlineProcessService.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 11. 5.
 * @version : 1.0
 * @author : takkies
 */
@Slf4j
@Service
public class ApiProcessService {

	@Autowired
	private ApiProcessor apiProcessor;

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 5. 오후 1:56:17
	 * </pre>
	 * 
	 * @param apiStep
	 * @param joinRequest
	 * @return
	 */
	public ApiProcessResponse executeOnlineApiProcess(final ApiStep apiStep, final JoinRequest joinRequest) {

		log.debug("▶▶▶▶▶ api step {}", StringUtil.printJson(apiStep));

		ApiProcessResponse response = new ApiProcessResponse();

		final StopWatch stopwatch = new StopWatch("ApiProcessService.executeOnlineApiProcess");
		Map<String, Object> object = new HashMap<>();

		object.put(OmniConstants.PROCESS_REQUEST, joinRequest);
		object.put(OmniConstants.PROCESS_STOPWATCH, stopwatch);

		if (apiStep.isIntgCustomer()) {
			
			response = this.apiProcessor.registIntegratedCustomer(object);
			if (StringUtils.hasText(response.getInscNo())) {
				joinRequest.setIncsno(response.getInscNo());
				object.put(OmniConstants.PROCESS_REQUEST, joinRequest);
			}
			
		} else {
			
			response = this.apiProcessor.updateIntegratedCustomer(object);
			
		}

		if (!ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {

			log.info("\n" + stopwatch.prettyPrint());

			return response;
		}

		if (apiStep.isBpCustomer()) {
			
			response = this.apiProcessor.registBeautyPointCustomer(object);
			
		}

		if (!ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {

			response = this.apiProcessor.cancelIntegratedCustomer(object);

			log.info("\n" + stopwatch.prettyPrint());

			return response;
		}

		if (apiStep.isOffChCustomer()) {

			String offlineChannelCd = ChannelPairs.getOfflineCd(joinRequest.getChcd());

			if (StringUtils.isEmpty(offlineChannelCd)) {

				BaseResponse baseResponse = new BaseResponse();
				baseResponse.setResultCode(ResultCode.SUCCESS.getCode());
				response.setBaseResponse(baseResponse);

			} else {
				
				Map<String, Object> offObject = new HashMap<>();
				joinRequest.setChcd(offlineChannelCd);
				offObject.put(OmniConstants.PROCESS_REQUEST, joinRequest);
				offObject.put(OmniConstants.PROCESS_STOPWATCH, stopwatch);
				response = this.apiProcessor.registOfflineChannelCustomer(offObject);

			}

		}

		if (!ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {

			ApiProcessResponse cancelResponse = this.apiProcessor.cancelIntegrateChannelCustomer(OnOffline.Offline, object);
			
			// 채널 등록 시 탈퇴회원 처리
			if (response.isWithdraw() //
					&& ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getWithdrawCode()) //
					&& StringUtils.hasText(response.getWithdrawDate()) //
					&& DateUtil.isValidDateFormat(response.getWithdrawDate())) {
				
				log.debug("탈퇴회원...");
				
				if (cancelResponse != null) {
					
					cancelResponse.setWithdraw(response.isWithdraw());
					cancelResponse.setWithdrawCode(response.getWithdrawCode());
					cancelResponse.setWithdrawDate(response.getWithdrawDate());

					BaseResponse baseResponse = cancelResponse.getBaseResponse();
					if (baseResponse != null) {
						baseResponse.setResultCode(ResultCode.CHANNEL_WITHDRAW.getCode());
						baseResponse.setMessage(response.getWithdrawDate());
						cancelResponse.setBaseResponse(baseResponse);
					}
					
					log.info("\n" + stopwatch.prettyPrint());
					
					return cancelResponse;
				}
				
			}
			
			log.info("\n" + stopwatch.prettyPrint());

			return response;
		} else {
			// 온라인 시 오프라인은 성공처리해야 온라인에 영향없음.
			BaseResponse baseResponse = new BaseResponse();
			baseResponse.SetResponseInfo(ResultCode.SUCCESS);
			response.setBaseResponse(baseResponse);
		}

		if (apiStep.isOnChCustomer()) {
			response = this.apiProcessor.registOnlineChannelCustomer(object);
		}

		if (!ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {

			ApiProcessResponse cancelResponse = this.apiProcessor.cancelIntegrateChannelCustomer(OnOffline.Online, object);
			
			// 채널 등록 시 탈퇴회원 처리
			if (response.isWithdraw() //
					&& ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getWithdrawCode()) //
					&& StringUtils.hasText(response.getWithdrawDate()) //
					&& DateUtil.isValidDateFormat(response.getWithdrawDate())) {
				
				log.debug("탈퇴회원...");
				
				if (cancelResponse != null) {
					
					cancelResponse.setWithdraw(response.isWithdraw());
					cancelResponse.setWithdrawCode(response.getWithdrawCode());
					cancelResponse.setWithdrawDate(response.getWithdrawDate());

					BaseResponse baseResponse = cancelResponse.getBaseResponse();
					if (baseResponse != null) {
						baseResponse.setResultCode(ResultCode.CHANNEL_WITHDRAW.getCode());
						baseResponse.setMessage(response.getWithdrawDate());
						cancelResponse.setBaseResponse(baseResponse);
					}
					
					log.info("\n" + stopwatch.prettyPrint());
					
					return cancelResponse;
				}
				
			}
			

			log.info("\n" + stopwatch.prettyPrint());

			return response;
		}

		if (apiStep.isOmniCustomer()) {
			response = this.apiProcessor.registOmniCustomer(object);
		}

		if (!ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {

			if (apiStep.isOffChCustomer()) {
				response = this.apiProcessor.cancelIntegrateChannelCustomer(OnOffline.Offline, object);
			}
			
			if (apiStep.isOnChCustomer()) {
				response = this.apiProcessor.cancelIntegrateChannelCustomer(OnOffline.Online, object);
			}

			log.info("\n" + stopwatch.prettyPrint());

			return response;
		}

		log.info("\n" + stopwatch.prettyPrint());

		return response;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 11. 5. 오후 1:56:10
	 * </pre>
	 * 
	 * @param apiStep
	 * @param joinRequest
	 * @return
	 */
	public ApiProcessResponse executeOfflineApiProcess(final ApiStep apiStep, final JoinRequest joinRequest) {
		log.debug("▶▶▶▶▶ api step {}", StringUtil.printJson(apiStep));

		ApiProcessResponse response = new ApiProcessResponse();

		final StopWatch stopwatch = new StopWatch("ApiProcessService.executeOfflineApiProcess");
		Map<String, Object> object = new HashMap<>();

		object.put(OmniConstants.PROCESS_REQUEST, joinRequest);
		object.put(OmniConstants.PROCESS_STOPWATCH, stopwatch);

		if (apiStep.isIntgCustomer()) {
			response = this.apiProcessor.registIntegratedCustomer(object);
			if (StringUtils.hasText(response.getInscNo())) {
				joinRequest.setIncsno(response.getInscNo());
				object.put(OmniConstants.PROCESS_REQUEST, joinRequest);
			}
		} else {
			response = this.apiProcessor.updateIntegratedCustomer(object);
		}

		if (!ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {

			log.info("\n" + stopwatch.prettyPrint());

			return response;
		}

		if (apiStep.isBpCustomer()) {
			response = this.apiProcessor.registBeautyPointCustomer(object);
		}

		if (!ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {

			response = this.apiProcessor.cancelIntegratedCustomer(object);

			log.info("\n" + stopwatch.prettyPrint());

			return response;
		}

		if (apiStep.isOffChCustomer()) {
			response = this.apiProcessor.registOfflineChannelCustomer(object);
		}

		if (!ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {

			ApiProcessResponse cancelResponse = this.apiProcessor.cancelIntegrateChannelCustomer(OnOffline.Offline, object);
			
			// 채널 등록 시 탈퇴회원 처리
			if (response.isWithdraw() //
					&& ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getWithdrawCode()) //
					&& StringUtils.hasText(response.getWithdrawDate()) //
					&& DateUtil.isValidDateFormat(response.getWithdrawDate())) {
				
				log.debug("탈퇴회원...");
				
				if (cancelResponse != null) {
					
					cancelResponse.setWithdraw(response.isWithdraw());
					cancelResponse.setWithdrawCode(response.getWithdrawCode());
					cancelResponse.setWithdrawDate(response.getWithdrawDate());

					BaseResponse baseResponse = cancelResponse.getBaseResponse();
					if (baseResponse != null) {
						baseResponse.setResultCode(ResultCode.CHANNEL_WITHDRAW.getCode());
						baseResponse.setMessage(response.getWithdrawDate());
						cancelResponse.setBaseResponse(baseResponse);
					}
					
					log.info("\n" + stopwatch.prettyPrint());
					
					return cancelResponse;
				}
				
			}
			

			log.info("\n" + stopwatch.prettyPrint());

			return response;
		}

		if (apiStep.isOnChCustomer()) {

			String onlineChannelCd = ChannelPairs.getOnlineCd(joinRequest.getChcd());

			Map<String, Object> onObject = new HashMap<>();
			joinRequest.setChcd(onlineChannelCd);
			onObject.put(OmniConstants.PROCESS_REQUEST, joinRequest);
			onObject.put(OmniConstants.PROCESS_STOPWATCH, stopwatch);
			response = this.apiProcessor.registOnlineChannelCustomer(onObject);
		}

		if (!ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {

			ApiProcessResponse cancelResponse = this.apiProcessor.cancelIntegrateChannelCustomer(OnOffline.Online, object);

			// 채널 등록 시 탈퇴회원 처리
			if (response.isWithdraw() //
					&& ResultCode.CHANNEL_WITHDRAW.getCode().equals(response.getWithdrawCode()) //
					&& StringUtils.hasText(response.getWithdrawDate()) //
					&& DateUtil.isValidDateFormat(response.getWithdrawDate())) {
				
				log.debug("탈퇴회원...");
				
				if (cancelResponse != null) {
					
					cancelResponse.setWithdraw(response.isWithdraw());
					cancelResponse.setWithdrawCode(response.getWithdrawCode());
					cancelResponse.setWithdrawDate(response.getWithdrawDate());

					BaseResponse baseResponse = cancelResponse.getBaseResponse();
					if (baseResponse != null) {
						baseResponse.setResultCode(ResultCode.CHANNEL_WITHDRAW.getCode());
						baseResponse.setMessage(response.getWithdrawDate());
						cancelResponse.setBaseResponse(baseResponse);
					}
					
					log.info("\n" + stopwatch.prettyPrint());
					
					return cancelResponse;
				}
				
			} else {
				// 오프라인 시 온라인은 성공처리해야 오프라인에 영향없음.
				BaseResponse baseResponse = new BaseResponse();
				baseResponse.SetResponseInfo(ResultCode.SUCCESS);
				response.setBaseResponse(baseResponse);
			}
			
			log.info("\n" + stopwatch.prettyPrint());

			return response;
		}

		if (apiStep.isOmniCustomer()) {
			response = this.apiProcessor.registOmniCustomer(object);
		}

		if (!ResultCode.SUCCESS.getCode().equals(response.getBaseResponse().getResultCode())) {

			if (apiStep.isOffChCustomer()) {
				response = this.apiProcessor.cancelIntegrateChannelCustomer(OnOffline.Offline, object);
			}
			if (apiStep.isOnChCustomer()) {
				response = this.apiProcessor.cancelIntegrateChannelCustomer(OnOffline.Online, object);
			}

			log.info("\n" + stopwatch.prettyPrint());

			return response;
		}

		log.info("\n" + stopwatch.prettyPrint());

		return response;
	}

}
