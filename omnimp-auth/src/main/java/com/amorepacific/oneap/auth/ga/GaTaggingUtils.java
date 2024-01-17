package com.amorepacific.oneap.auth.ga;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.amorepacific.oneap.auth.ga.config.GaTaggingProperties;
import com.amorepacific.oneap.auth.ga.OmniGaTaggingConstants;
import com.amorepacific.oneap.auth.ga.vo.GaTagData;
import com.amorepacific.oneap.common.util.ConfigUtil;
import com.amorepacific.oneap.common.util.WebUtil;
import com.amorepacific.oneap.common.validation.SystemInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/**
 * 클래스설명 : 
 * @version : 2021. 11. 16.
 * @author : kspark01
 * @분류 : 
 * omnimp-auth / package com.amorepacific.oneap.auth.common;
 */

/**
 * 1. ClassName : 2. FileName : GoogleGaTagginClientIdGenerator.java 3. Package : com.amorepacific.oneap.auth.common 4. Commnet : 5. 작성자 :
 * kspark01 6. 작성일 : 2021. 11. 16. 오후 2:09:58
 */
/**
 * 1. ClassName : 2. FileName : GoogleGaTaggingUtils.java 3. Package : com.amorepacific.oneap.auth.common 4. Commnet : 5. 작성자 : kspark01 6.
 * 작성일 : 2022. 1. 19. 오전 9:09:28
 */
/**
 * 1. ClassName : 
 * 2. FileName          : GaTaggingUtils.java
 * 3. Package           : com.amorepacific.oneap.auth.ga
 * 4. Commnet           : 
 * 5. 작성자                       : kspark01
 * 6. 작성일                       : 2022. 5. 24. 오전 11:07:59
 */
@Component
@Slf4j
public class GaTaggingUtils {

	private static final int leftLimit = 48;
	private static final int rightLimit = 57;

	@Autowired
	private GaTaggingProperties gaTaggingProperties;

	@Autowired
	private SystemInfo systemInfo;
	
	
	/**
	 * 1. MethodName : getClientId 2. ClassName : GoogleGaTagginUtils 3. Commnet : Google GA Tagging 사용자 구분 Client ID 4. 작성자 : kspark01 5. 작성일 :
	 * 2021. 11. 17. 오전 9:45:36
	 * 
	 * @return String : ga tagging client id
	 * @param fistLength : 구분자 기준으로 client id fist length
	 * @param lastLength : 구분자 기준으로 client id last length
	 * @return
	 */
	public String getClientId(final int fistLength, final int lastLength) {
		Random random = new Random();

		String fistString = random.ints(leftLimit, rightLimit + 1).limit(fistLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		String lastString = random.ints(leftLimit, rightLimit + 1).limit(lastLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return fistString + "." + lastString;
	}

	public String getClientId(final String value, final String split) {

		String result = "";

		try {

			String[] splitArrayValue = value.split(split);
			result = splitArrayValue[2] + "." + splitArrayValue[3];

		} catch (Exception ex) {
			log.error("ga client id :{} get error : {}", value, ex.getMessage());
		}

		return result;
	}

	public void gaTaggingResetSetSessions(final String eventType, final String loginType) {

		WebUtil.setSession(eventType, loginType); // 이벤트타입

	}

	private String[] getStringToArray(final String arrayValue) {

		String[] result = (arrayValue != null) ? arrayValue.split(",") : new String[] { arrayValue };

		return result;
	}

	public String[] getArraySetValues(final String event) {

		return getStringToArray(gaTaggingProperties.getDatas().get(event));
	}

	/**
	 * 1. MethodName : getStringToMap 2. ClassName : GoogleGaTagginUtils 3. Commnet : gson parameter type 4. 작성자 : kspark01 5. 작성일 : 2021. 11.
	 * 22. 오전 10:53:33
	 * 
	 * @return Map<String,String>
	 * @param json
	 * @return
	 */
	private Map<String, String> getStringToMap(final String json) {
		Gson gson = new Gson();
		Type stringMapType = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> stringToMap = gson.fromJson(json, stringMapType);

		return stringToMap;
	}

	/**
	 * 1. MethodName : parametersMultiValue 2. ClassName : GoogleGaTagginUtils 3. Commnet : restTemplate parameters value setting 4. 작성자 :
	 * kspark01 5. 작성일 : 2021. 11. 22. 오전 10:52:43
	 * 
	 * @return MultiValueMap<String,String>
	 * @param event
	 * @param values
	 * @return
	 */
	public MultiValueMap<String, String> parametersMultiValue(final String event, final Map<String, String> requestVal, final String... values) {

		Map<String, String> propertiesMap = new HashMap<>();

		if (event.startsWith("join")) {

			propertiesMap = getStringToMap(gaTaggingProperties.getJoins().get(event));

		} else {

			propertiesMap = getStringToMap(gaTaggingProperties.getLogins().get(event));
		}

		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

		for (String key : propertiesMap.keySet()) {

			if (Objects.nonNull(propertiesMap.get(key))) {
				parameters.add(key, propertiesMap.get(key));
			}
		}

		for (String value : values) {
			if (Objects.nonNull(requestVal.get(value))) {
				parameters.add(value, (String) requestVal.get(value));
			}
		}

		return parameters;
	}
	

	/**
	 * 1. MethodName        : gaTaggingWebClientApi
	 * 2. ClassName         : GaTaggingUtils
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 4. 8. 오후 1:01:22
	 * @return void
	 * @param event
	 * @param requestVal
	 * @param gaTagData
	 */
	public void gaTaggingWebClientApi(final String event, final Map<String, String> requestVal,GaTagData gaTagData) {

		try {

			log.debug("▶▶▶▶▶▶ GA Tagging Send Start ( event : {} )",event);
			
			//login 을 제외 할 경우 로직 수정 필요
			setLogValue(gaTagData);
			//TODO Json data transfer openserch log file write
			log.info("▶▶▶▶▶▶ GA Tagging Send Data Json:{}",printJson(gaTagData));
	
			String[] propertyValues = getArraySetValues(event);

			Map<String, String> headerMap = getStringToMap(gaTaggingProperties.getHeaders());
			MultiValueMap<String, String> params = parametersMultiValue(event, requestVal, propertyValues);
												
			Mono<String> monoReponse = WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient.newConnection()))
            .baseUrl(gaTaggingProperties.getBase())
            .build().get().uri(uriBuilder -> uriBuilder.path(gaTaggingProperties.getPath()).queryParams(params).build()).headers(header -> {
				header.setContentType(new MediaType(MediaType.APPLICATION_FORM_URLENCODED, Charset.forName("UTF-8")));
				for (String key : headerMap.keySet()) {
					header.add(key, headerMap.get(key));
				}
			}).retrieve().bodyToMono(String.class);              
            
			monoReponse.subscribe(result -> log.debug("▶▶▶▶▶▶ GA Tagging WebClient ( result : {} )",result),
					              error -> { log.debug("▶▶▶▶▶▶ GA Tagging WebClient ( error : {} )",error); 
//						              setLogValue(gaTagData,false);
					      			  log.debug("▶▶▶▶▶▶ GA Tagging Send Data Json:{}",printJson(gaTagData));
					              },
					              () -> {
					            	  log.debug("▶▶▶▶▶▶ GA Tagging WebClient Done");
//					            	  setLogValue(gaTagData,true);
					      			  log.debug("▶▶▶▶▶▶ GA Tagging Send Data Json:{}",printJson(gaTagData));
					      	      });
			
			//TODO elasticsearch json format log write
						
			log.debug("▶▶▶▶▶▶ GA Tagging Send Success ( event : {} )",event);

		} catch (Exception ex) {
			
			log.error(ex.getMessage());
		}		
	}


	/**
	 * 1. MethodName        : setLogValue
	 * 2. ClassName         : GaTaggingUtils
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 8. 24. 오전 11:01:45
	 * @return void
	 * @param gaTagData : ga vo
	 * @param sendFlag : ga server send flag
	 */
	private void setLogValue(GaTagData gaTagData) {
	
		if(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL.equals(gaTagData.getEventAction())){ 
			gaTagData.setEventAction(getErrorEventName(gaTagData.getEventAction()));
		}else {
			gaTagData.setEventAction(getEventName(gaTagData.getEventAction()));
		}
		gaTagData.setSendDate(getSendLocalDataTime(OmniGaTaggingConstants.LOG_SEND_DATE_FORMAT));
		gaTagData.setSendTime(getSendLocalDataTime(OmniGaTaggingConstants.LOG_SEND_TIME_FORMAT));
		//gaTagData.setServerIp(getServerIp());
		//gaTagData.setHostName(getServerName());
	}

	/**
	 * 1. MethodName : googleGaTaggingDirectLoginPushApi 2. ClassName : GoogleGaTaggingUtils 3. Commnet : 4. 작성자 : kspark01 5. 작성일 : 2022. 1.
	 * 18. 오후 3:00:56
	 * 
	 * @return void
	 * @param gaTagDto
	 */
	@Async("asyncTaggingThreadTaskExecutor")
	public void googleGaTaggingDirectLoginPushApi(final GaTagData gaTagDto) {

		Map<String, String> requestValues = new HashMap<>();

		requestValues.put(OmniGaTaggingConstants.CID, gaTagDto.getCid());
		requestValues.put(OmniGaTaggingConstants.GA_TAGGING_GID, gaTagDto.getGid());

		requestValues.put(OmniGaTaggingConstants.GA_TAGGING_UIP, gaTagDto.getUip());// user ip address
		requestValues.put(OmniGaTaggingConstants.GA_TAGGING_UA, gaTagDto.getUa());// user agnet
		
		requestValues.put(OmniGaTaggingConstants.EL, gaTagDto.getEl()); // 이벤트타입
		requestValues.put(OmniGaTaggingConstants.CD21, gaTagDto.getChCdNm());// 채널명
		requestValues.put(OmniGaTaggingConstants.CD22, getChannelAndBrandSiteCd(gaTagDto.getChCd()));// 채널코드


		requestValues.put(OmniGaTaggingConstants.CD27, gaTagDto.getLoginType());// 로그인방법

		if (OmniGaTaggingConstants.GA_EVENT_ACTION_FAIL.equals(gaTagDto.getEventAction()) && Objects.nonNull(gaTagDto.getErrorMessage())) {
			requestValues.put(OmniGaTaggingConstants.CD26, gaTagDto.getErrorMessage());
		}

		try {

			if (Objects.nonNull(gaTagDto.getCid()) && Objects.nonNull(gaTagDto.getGid())) {
			
				gaTaggingWebClientApi(gaTagDto.getEventAction(), requestValues,gaTagDto);
				
			} else {
				log.debug("Login Tagging cid is {}, gid is {}", gaTagDto.getCid(), gaTagDto.getGid());
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

	/**
	 * 1. MethodName : googleGaTaggingDirectJoinPushApi 2. ClassName : GoogleGaTaggingUtils 3. Commnet : 4. 작성자 : kspark01 5. 작성일 : 2021. 11.
	 * 26. 오전 10:53:52
	 * 
	 * @return void
	 * @param eventLabel(el) : 이벤트 타입(휴대폰,아이핀,SNS:카카오)
	 * @param loginType(cd23) : 회원가입인증(휴대폰,아이핀,SNS)
	 * @param chCdNm (cd21): 채널명
	 * @param chCd(cd22) : 채널코드
	 * @param eventAction : 회원가입 이벤트 엑션(join_start,join_fail,jonin_success)
	 * @param joinType(cd24) : 회원가입완료_분류 : 회원가입(신규) , 기가입(일반/전환)
	 * @param totalJoinCnt(cm6) : 통회원가입 가입수 (전환)
	 * @param channelJoinCnt(cm7) : 경로회원 가입수 (일반)
	 * @param errorMessage
	 */
	@Async("asyncTaggingThreadTaskExecutor")
	public void googleGaTaggingDirectJoinPushApi(final GaTagData gaTagDto) {

		Map<String, String> requestValues = new HashMap<>();

		requestValues.put(OmniGaTaggingConstants.EL, gaTagDto.getEl()); // 이벤트타입
		requestValues.put(OmniGaTaggingConstants.CID, gaTagDto.getCid()); // client id
		requestValues.put(OmniGaTaggingConstants.GA_TAGGING_GID, gaTagDto.getGid()); // _gid
		requestValues.put(OmniGaTaggingConstants.GA_TAGGING_UIP, gaTagDto.getUip());// user ip address
		requestValues.put(OmniGaTaggingConstants.GA_TAGGING_UA, gaTagDto.getUa());// user agnet

		if (Objects.nonNull(gaTagDto.getChCdNm())) {
			requestValues.put(OmniGaTaggingConstants.CD21, gaTagDto.getChCdNm()); // 채널명
		}
		if (Objects.nonNull(gaTagDto.getChCd())) {
			requestValues.put(OmniGaTaggingConstants.CD22, getChannelAndBrandSiteCd(gaTagDto.getChCd()));// 채널코드
		}

		requestValues.put(OmniGaTaggingConstants.CD23, gaTagDto.getLoginType());// 회원가입 인증

		if (OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS.equals(gaTagDto.getEventAction())) {
			requestValues.put(OmniGaTaggingConstants.CD24, gaTagDto.getJoinType()); // 회원가입완료_분류

			if (Objects.nonNull(gaTagDto.getTotalJoinCnt())) {
				requestValues.put(OmniGaTaggingConstants.CM6, gaTagDto.getTotalJoinCnt());// 통합회원
			}
			if (Objects.nonNull(gaTagDto.getChannelJoinCnt())) {
				requestValues.put(OmniGaTaggingConstants.CM7, gaTagDto.getChannelJoinCnt()); // 경로회원
			}
		}

		if (OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL.equals(gaTagDto.getEventAction()) && Objects.nonNull(gaTagDto.getErrorMessage())) {
			requestValues.put(OmniGaTaggingConstants.CD26, gaTagDto.getErrorMessage());
		}

		try {

			if (Objects.nonNull(gaTagDto.getCid()) && Objects.nonNull(gaTagDto.getGid())) {
				gaTaggingWebClientApi(gaTagDto.getEventAction(), requestValues,gaTagDto);
			} else {
				log.debug("Join Tagging cid is {}, gid is {}", gaTagDto.getCid(), gaTagDto.getGid());
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());

		}

	}

	/**
	 * 1. MethodName        : getChannelAndBrandSiteCd
	 * 2. ClassName         : GaTaggingUtils
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 4. 25. 오전 10:51:08
	 * @return String
	 * @param chCd
	 * @return
	 */
	private String getChannelAndBrandSiteCd(final String chCd) {
		String profile = this.systemInfo.getActiveProfiles()[0];
		profile = StringUtils.isEmpty(profile) ? "dev" : profile;
		profile = profile.equalsIgnoreCase("default") ? "dev" : profile;

		log.debug("profile is {}, chCd {}", profile, chCd);

		if (ConfigUtil.getInstance().isBrandSite(chCd, profile)) {
			return OmniGaTaggingConstants.BRAND_SITE_CODE_PREFIX.concat(chCd);
		} else {
			return chCd;
		}
	}

	/**
	 * 1. MethodName        : getGaCookieMap
	 * 2. ClassName         : GoogleGaTaggingUtils
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 2. 16. 오전 9:52:27
	 * @return Map<String,String>
	 * @param request
	 * @return
	 */
	public Map<String,String> getGaCookieMap(HttpServletRequest request) {
		
		Map<String,String> resultMap = new HashMap<>();
		
		try {
			
			Cookie gaCidCookie = WebUtil.getCookies(request, OmniGaTaggingConstants.GA_TAGGING_CLIENT_ID);// 필수 cid
			Cookie gaGidCookie = WebUtil.getCookies(request, OmniGaTaggingConstants.GA_TAGGING_GID); // optional 값이 있음 넣는다.

			if (Objects.nonNull(gaCidCookie) && Objects.nonNull(gaGidCookie)) {
				log.debug("▶▶▶▶▶▶  GA Tagging Cookie ga  : {} _gid : {}", gaCidCookie.getValue(), gaGidCookie.getValue());
			} else {
				log.debug("▶▶▶▶▶▶  GA Tagging Cookie _ga is null and  _gid is null");
			}
			
			if(Objects.nonNull(gaCidCookie) && Objects.nonNull(gaGidCookie)) {
				resultMap.put(OmniGaTaggingConstants.CID,getClientId(gaCidCookie.getValue(), "\\."));
				resultMap.put(OmniGaTaggingConstants.GA_TAGGING_GID,getClientId(gaGidCookie.getValue(), "\\."));
			} else {

				log.debug("▶▶▶▶▶▶   GA Tagging Map ga  : {} _gid : {}", resultMap.get(OmniGaTaggingConstants.CID), resultMap.get(OmniGaTaggingConstants.GA_TAGGING_GID));
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		
		return resultMap;
	}
	
	/**
	 * 1. MethodName        : getSendLocalDataTime
	 * 2. ClassName         : GaTaggingUtils
	 * 3. Commnet           : format yyyy/MM/dd HH:mm:ss
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 5. 24. 오전 11:08:03
	 * @return String
	 * @param format
	 * @return
	 */
	private String getSendLocalDataTime(String format) { 
		
		DateTimeFormatter formater = DateTimeFormatter.ofPattern(format);
		return formater.format(LocalDateTime.now());
		
	}
	
	/**
	 * 1. MethodName        : getEventName
	 * 2. ClassName         : GaTaggingUtils
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 6. 20. 오후 2:59:01
	 * @return String
	 * @param eventAction
	 * @return
	 */
	private String getEventName(final String eventAction) {
		
		String eventName = "";
		if (eventAction.startsWith("join")) {
			eventName = eventAction;
		} else {
			eventName = "login_" + eventAction;
		}
		return eventName;
	}
	
	/**
	 * 1. MethodName        : getErrorEventName
	 * 2. ClassName         : GaTaggingUtils
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 9. 2. 오전 11:50:59
	 * @return String
	 * @param eventAction
	 * @return
	 */
	private String getErrorEventName(final String eventAction) {
		
		String eventName = "";
		if (eventAction.startsWith("join")) {
			eventName = "join_failed";
		} else {
			eventName = "login_failed";
		}
		return eventName;
	}
	
	/**
	 * 1. MethodName        : printJson
	 * 2. ClassName         : GaTaggingUtils
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 9. 2. 오전 11:51:03
	 * @return String
	 * @param obj
	 * @return
	 */
	private String printJson(Object obj) {
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			JsonElement je = JsonParser.parseString(mapper.writeValueAsString(obj));
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			return gson.toJson(je);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 1. MethodName        : getServerIp
	 * 2. ClassName         : GaTaggingUtils
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 6. 20. 오후 2:59:04
	 * @return String
	 * @return
	 */
//	private String getServerIp(){
//	    String result = null;
//	    try {
//	        result = InetAddress.getLocalHost().getHostAddress();
//	    } catch (UnknownHostException e) {
//	        result = "0:0:0:0";
//	    }
//	   return result; 
//	}
	
	/**
	 * 1. MethodName        : getServerName
	 * 2. ClassName         : GaTaggingUtils
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 6. 20. 오후 2:59:07
	 * @return String
	 * @return
	 */
//	private String getServerName(){
//	    String result = null;
//	    try {
//	        result = InetAddress.getLocalHost().getCanonicalHostName();
//	    } catch (UnknownHostException e) {
//	        result = "localhost";
//	    }
//	   return result; 
//	}
}