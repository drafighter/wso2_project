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
 * Date   	          : 2020. 11. 27..
 * Description       : OMNIMP v1 옴니회원플랫폼 구축 프로젝트.
 * </pre>
 */
package com.amorepacific.oneap.api.common;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StopWatch.TaskInfo;
import org.springframework.util.StringUtils;

import com.amorepacific.oneap.api.common.mapper.CommonMapper;
import com.amorepacific.oneap.api.common.service.CommonService;
import com.amorepacific.oneap.api.exception.ApiBusinessException;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.OmniUtil;
import com.amorepacific.oneap.common.util.StringUtil;
import com.amorepacific.oneap.common.util.UuidUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.amorepacific.oneap.common.vo.BaseResponse;
import com.amorepacific.oneap.common.vo.Channel;
import com.amorepacific.oneap.common.vo.ErrorParams;
import com.amorepacific.oneap.common.vo.OfflineParam;
import com.amorepacific.oneap.common.vo.OmniConstants;
import com.amorepacific.oneap.common.vo.ProcessVo;
import com.amorepacific.oneap.common.vo.SSOParam;
import com.amorepacific.oneap.common.vo.ServiceVo;
import com.amorepacific.oneap.common.vo.TaskVo;
import com.amorepacific.oneap.common.vo.join.ChannelParam;

/**
 * <pre>
 * com.amorepacific.oneap.auth.common 
 *    |_ CommonAuth.java
 * </pre>
 *
 * @desc :
 * @date : 2020. 11. 27.
 * @version : 1.0
 * @author : takkies
 */
@Component
public class CommonAuth {

	@Autowired
	private CommonMapper commonMapper;

	@Autowired
	private CommonService commonService;

	@Autowired
	private SystemInfo systemInfo;

	private ConfigUtil config = ConfigUtil.getInstance();

	/**
	 * 
	 * <pre>
	 * comment  : 에러 페이지에 전달하기 위한 파라미터 취합 
	 * author   : takkies
	 * date     : 2020. 11. 27. 오후 4:08:28
	 * </pre>
	 * 
	 * @return
	 */
	public ErrorParams getErrorParams() {
		return getErrorParams(null, null, null, null, null);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 에러 페이지에 전달하기 위한 파라미터 취합
	 * author   : takkies
	 * date     : 2020. 11. 27. 오후 4:09:24
	 * </pre>
	 * 
	 * @param channel
	 * @return
	 */
	public ErrorParams getErrorParams(Channel channel) {
		return getErrorParams(channel, null, null, null, null);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 에러 페이지에 전달하기 위한 파라미터 취합 
	 * author   : takkies
	 * date     : 2020. 11. 27. 오후 4:08:45
	 * </pre>
	 * 
	 * @param channel
	 * @param response
	 * @return
	 */
	public ErrorParams getErrorParams(Channel channel, final BaseResponse response) {
		return getErrorParams(channel, response, null, null, null);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 에러 페이지에 전달하기 위한 파라미터 취합 
	 * author   : takkies
	 * date     : 2020. 11. 27. 오후 4:08:55
	 * </pre>
	 * 
	 * @param channel
	 * @param response
	 * @param ssoParam
	 * @param channelParam
	 * @return
	 */
	public ErrorParams getErrorParams( //
			Channel channel, //
			final BaseResponse response, //
			final SSOParam ssoParam, //
			final OfflineParam offlineParam, //
			final ChannelParam channelParam) {

		ErrorParams errorParams = new ErrorParams();

		if (channel == null) {
			String chCd = WebUtil.getStringSession(OmniConstants.CH_CD_SESSION);
			try {
				channel = this.commonService.getChannel(chCd);
			} catch (ApiBusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (channel != null) { // 채널 정보가 있을 경우
			if (OmniUtil.isOffline(channel)) { // 오프라인
				Object obj = WebUtil.getSession(OmniConstants.OFFLINEPARAM);
				if (obj != null) {
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					if (offlineParam != null) {
						errorParams.setChCd(channel.getChCd());
						errorParams.setHome(OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
						errorParams.setHomeurl(OmniUtil.getCancelRedirectOfflineUrl(channel, profile));
						errorParams.setChnCd(offlineParam.getChnCd());
						errorParams.setStoreCd(offlineParam.getStoreCd());
						errorParams.setStorenm(offlineParam.getStorenm());
						errorParams.setUser_id(offlineParam.getUser_id());
					}
				} else {
					String profile = this.systemInfo.getActiveProfiles()[0];
					profile = StringUtils.isEmpty(profile) ? "dev" : profile;
					profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
					errorParams.setOfflineInitUrl(OmniUtil.getRedirectOfflineInitUrl(channel, profile));
				}

			} else { // 온라인

				// SSO 파라미터를 세션에서 꺼내지 않고 별도로 전달받는 이유
				// 온라인이라고 반드시 SSO PARAM의 redirect 정보를 사용하는것이 아니기 때문
				if (ssoParam != null) {
					errorParams.setHome(ssoParam.getRedirectUri());
					errorParams.setHomeurl(ssoParam.getRedirectUri());
				} else {
					errorParams.setHome(OmniUtil.getRedirectUrl(channel));
					errorParams.setHomeurl(OmniUtil.getRedirectUrl(channel));
				}

				if (channelParam != null) {
					errorParams.setHome(channelParam.getRedirectUri());
					errorParams.setHomeurl(channelParam.getRedirectUri());
				}

			}

			errorParams.setChannelName(channel.getChCdNm());

			if (response != null) {
				errorParams.setMessage(response.getMessage());
				errorParams.setErrorcode(response.getResultCode());
				errorParams.setErrormsg(response.getMessage());
			}

		} else {
			errorParams.setHome("");
			errorParams.setHomeurl("");
		}

		return errorParams;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 프로세스 처리 로그 남길지 여부 
	 * author   : takkies
	 * date     : 2020. 12. 1. 오후 2:56:37
	 * </pre>
	 * 
	 * @return
	 */
	public boolean isProcessLogging() {
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		return this.config.isProcessLogging(profile);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 
	 * author   : takkies
	 * date     : 2020. 12. 3. 오후 4:32:35
	 * </pre>
	 * 
	 * @return
	 */
	public boolean isServiceLogging() {
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;
		return this.config.isServiceLogging(profile);
	}

	/**
	 * 
	 * <pre>
	 * comment  : 프로세스 로그 기록
	 * author   : takkies
	 * date     : 2020. 12. 1. 오후 2:56:53
	 * </pre>
	 * 
	 * @param processVo
	 * @return
	 */
	public boolean insertProcessLog(final ProcessVo processVo) {

		return this.commonMapper.insertProcessLog(processVo) > 0;

	}

	/**
	 * 
	 * <pre>
	 * comment  : 서비스 로그 기록 
	 * author   : takkies
	 * date     : 2020. 12. 1. 오후 2:57:07
	 * </pre>
	 * 
	 * @param serviceVo
	 * @return
	 */
	public boolean insertServiceLog(final ServiceVo serviceVo) {
		return this.commonMapper.insertServiceLog(serviceVo) > 0;
	}

	/**
	 * 
	 * <pre>
	 * comment  : 프로세스 태스크 로그(stopWatch) 기록
	 * 반영시 아래 주석 해제하고
	 * **Step.java 에 로그 기록 관련 주석 해제
	 * author   : takkies
	 * date     : 2020. 12. 1. 오후 2:57:17
	 * </pre>
	 * 
	 * @param request
	 * @param stopWatch
	 * @return
	 */
	public boolean insertTaskLog(final Map<String, Object> request, final StopWatch stopWatch) {

		if (isProcessLogging()) {
			String loggingId = (String) request.get(OmniConstants.PROCESS_LOGGING_ID);
			if (StringUtils.isEmpty(loggingId)) {
				loggingId = UuidUtil.getIdByDate("P");
			}
			try {
				if (stopWatch != null) {
					Map<String, List<TaskVo>> taskVos = new HashMap<>();
					List<TaskVo> tasklist = new ArrayList<>();
					TaskInfo taskinfos[] = stopWatch.getTaskInfo();
					int idx = 1;
					for (TaskInfo taskinfo : taskinfos) {

						NumberFormat nf = NumberFormat.getNumberInstance();
						nf.setMinimumIntegerDigits(3);
						nf.setGroupingUsed(false);
						TaskVo task = new TaskVo();

						task.setMbpfUnitBsnsExctLogNo(idx); // task.setTaskSeq(idx);
						task.setMbpfUnitBsnsNo(loggingId); // task.setTaskId(loggingId);
						task.setMbpfUnitBsnsNm(taskinfo.getTaskName()); // task.setTaskName(taskinfo.getTaskName());
						task.setMbpfUnitBsnsExctNss(taskinfo.getTimeSeconds()); // task.setTaskExecuteSecs(taskinfo.getTimeSeconds());
						task.setMbpfUnitBsnsExctNmss(taskinfo.getTimeMillis()); // task.setTaskExecuteMillis(taskinfo.getTimeMillis());
						task.setMbpfUnitBsnsExctPrt(nf.format(Math.round(100.0 * ((double) taskinfo.getTimeNanos() / stopWatch.getTotalTimeNanos())))); // task.setTaskRatio(nf.format(Math.round(100.0 * ((double)
																																						// taskinfo.getTimeNanos() / stopWatch.getTotalTimeNanos()))));

						tasklist.add(task);
						idx++;
					}
					taskVos.put("list", tasklist);

					return this.commonMapper.insertProcessTask(taskVos) > 0;
				}
			} catch (Exception e) {
				// NO PMD
			}
		}
		return true;
	}

	public void insertProcessLog() {

	}

	/**
	 * 
	 * <pre>
	 * comment  : 프로세스 태스크 로그(stopWatch) 기록
	 * 반영시 아래 주석 해제하고
	 * **Step.java 에 로그 기록 관련 주석 해제 
	 * author   : takkies
	 * date     : 2020. 12. 1. 오후 2:57:48
	 * </pre>
	 * 
	 * @param loggingId
	 * @param prcName
	 * @param className
	 * @param methodName
	 * @param type
	 * @param input
	 * @param output
	 * @param result
	 * @param msg
	 */
	public void insertProcessLog( //
			final String loggingId, //
			final String prcName, //
			final String className, //
			final String methodName, //
			final String type, //
			String input, //
			String output, //
			final String result, //
			final String msg //
	) {

		if (isProcessLogging()) {
			
			
			if (StringUtils.hasText(input)) {
				if (StringUtil.length(input) >= 4000) {
					input = StringUtil.substring(input, 4000);
				}
			}

			if (StringUtils.hasText(output)) {
				if (StringUtil.length(output) >= 2000) {
					output = StringUtil.substring(output, 2000);
				}
			}
			
			
			ProcessVo processVo = new ProcessVo();
			processVo.setMbpfPrceNo(loggingId); // processVo.setPrcId(loggingId);
			processVo.setMbpfPrceNm(prcName); // processVo.setPrcName(prcName); // ("고객통합 등록 API 오프라인 가입");
			processVo.setMbpfJvcsNm(className); // processVo.setPrcClass(className); // (customerApiService.getClass().getCanonicalName());
			processVo.setMbpfJvmtNm(methodName); // processVo.setPrcMethod(methodName); // ("createCust");
			processVo.setMbpfPrceTpCd(type); // processVo.setPrcType(type); // ("C");
			processVo.setInParmLv(input); // processVo.setPrcInput(input); // (createCustVo.toString()); // processVo.setPrcInput(StringUtil.printJson(createCustVo));
			processVo.setTlmsParmLv(output); // processVo.setPrcOutput(output); // (custResponse.toString()); // processVo.setPrcOutput(StringUtil.printJson(custResponse));
			processVo.setMbpfPrceExctRsltCd(result); // processVo.setPrcResult(result); // (custResponse.getRsltCd());
			processVo.setMbpfPrceExctRsltMsg(msg); // processVo.setPrcMsg(msg); // (custResponse.getRsltMsg());
			insertProcessLog(processVo);
		}

	}

	/**
	 * 
	 * <pre>
	 * comment  : 프로세스 태스크 로그(stopWatch) 기록
	 * author   : takkies
	 * date     : 2020. 12. 9. 오후 1:19:00
	 * </pre>
	 * @param loggingId
	 * @param prcName
	 * @param className
	 * @param methodName
	 * @param type
	 * @param input
	 * @param output
	 */
	public void insertProcessLog( //
			final String loggingId, //
			final String prcName, //
			final String className, //
			final String methodName, //
			final String type, //
			final String input, //
			final String output //
	) {

		this.insertProcessLog(loggingId, prcName, className, methodName, type, input, output, null, null);

	}

}
