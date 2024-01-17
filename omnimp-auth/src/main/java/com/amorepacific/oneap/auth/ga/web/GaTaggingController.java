package com.amorepacific.oneap.auth.ga.web;

import java.util.Map;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.amorepacific.oneap.auth.ga.GaTaggingUtils;
import com.amorepacific.oneap.auth.ga.OmniGaTaggingConstants;
import com.amorepacific.oneap.auth.ga.vo.GaTagData;
import com.amorepacific.oneap.common.util.WebUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 클래스설명 : 
 * @version : 2021. 11. 25.
 * @author : kspark01
 * @분류 : 
 * omnimp-auth / package com.amorepacific.oneap.auth.common;
 */

/**
 * 1. ClassName : 2. FileName : GaTaggingUserStopController.java 3. Package : com.amorepacific.oneap.auth.common 4. Commnet : 5. 작성자 :
 * kspark01 6. 작성일 : 2021. 11. 25. 오후 1:00:38
 */
@Slf4j
@RestController
public class GaTaggingController {

	@Autowired
	private GaTaggingUtils googleGaTaggingUtils;

	
	/**
	 * 1. MethodName        : gaCookie
	 * 2. ClassName         : GaTaggingController
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 1. 20. 오전 8:51:55
	 * @return ResponseEntity<String>
	 * @param request
	 * @return
	 */
	@GetMapping("/ga/tagging/cookie")
	public ResponseEntity<String> gaCookie(HttpServletRequest request) {

		log.info("ga tagging cookie set start");
		

		try {
						
			Cookie gaCidCookie = WebUtil.getCookies(request, OmniGaTaggingConstants.GA_TAGGING_CLIENT_ID);//필수 cid
			Cookie gaGidCookie = WebUtil.getCookies(request, OmniGaTaggingConstants.GA_TAGGING_GID); //optional 값이 있음 넣는다.
			
			if(Objects.nonNull(gaCidCookie) && Objects.nonNull(gaGidCookie)) {
				
				WebUtil.setSession(OmniGaTaggingConstants.CID, googleGaTaggingUtils.getClientId(gaCidCookie.getValue(), "\\."));//GA1.2 는 버전 번호라 콤마로 split 해서 제외한다.
				WebUtil.setSession(OmniGaTaggingConstants.GA_TAGGING_GID, googleGaTaggingUtils.getClientId(gaGidCookie.getValue(), "\\."));//GA1.2 는 버전 번호라 콤마로 split 해서 제외한다.
				WebUtil.setSession(OmniGaTaggingConstants.GA_TAGGING_UIP,request.getParameter(OmniGaTaggingConstants.GA_TAGGING_UIP));
				WebUtil.setSession(OmniGaTaggingConstants.GA_TAGGING_UA,request.getHeader("User-Agent"));
				
				
				log.info("▶▶▶▶▶▶ [login page init] GA Tagging Cookie ga  : {} _gid : {}", gaCidCookie.getValue(),gaGidCookie.getValue());
				
				log.info("▶▶▶▶▶▶ [login page init] GA Tagging Cookie uip  : {} ua : {}", request.getParameter(OmniGaTaggingConstants.GA_TAGGING_UIP),request.getHeader("User-Agent"));
				
			}else {
				log.info("▶▶▶▶▶▶ [login page init] GA Tagging Cookie _ga is null and  _gid is null");
			}
	 				
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return ResponseEntity.ok("▶▶▶▶▶▶ ga tagging cookie set end [SERVER IP : " + request.getLocalAddr() + "]");
	}
	
	
	/**
	 * 1. MethodName        : gaLoginStart
	 * 2. ClassName         : GaTaggingController
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 1. 20. 오전 8:52:00
	 * @return ResponseEntity<String>
	 * @param joinType
	 * @param request
	 * @return
	 */
	@GetMapping("/ga/tagging/join/{joinType}")
	public ResponseEntity<String> gaJoinSuccess(@PathVariable("joinType") String joinType, HttpServletRequest request) {

		log.info("ga tagging join");

		//회원가입 ga tagging 중복 처리 방지용 저장된 값이 없으면 실행
		
							
		try {			
			
			if (Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY)) && !Objects.isNull(WebUtil.getSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_START_CHECK_KEY))) {

				if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
						|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
						|| OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals((String) WebUtil.getSession(OmniGaTaggingConstants.EL)))) {

					String joinCase = OmniGaTaggingConstants.GA_SIGNUP_CASE_INTERGRATED;

					if (Objects.nonNull(joinType)) {
						if (OmniGaTaggingConstants.GA_SIGNUP_CASE_MEMBER_CODE.equals(joinType)) { // 기가입 O O O
							joinCase = OmniGaTaggingConstants.GA_SIGNUP_CASE_MEMBER;
						} else if (OmniGaTaggingConstants.GA_SIGNUP_CASE_ONLINE_CODE.equals(joinType)) { // 온라인 가입 O X O
							joinCase = OmniGaTaggingConstants.GA_SIGNUP_CASE_ONLINE_MEMBER;
						}
					}

					final Map<String, String> gaCookieMap = googleGaTaggingUtils.getGaCookieMap(request);
					final String eventAction = getGaAction();

					log.debug("▶▶▶▶▶▶  GA Tagging JOIN SUCCESS(UI) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{},joinType:{}]", gaCookieMap.get(OmniGaTaggingConstants.CID),
							gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID), eventAction, WebUtil.getSession(OmniGaTaggingConstants.CD21), WebUtil.getSession(OmniGaTaggingConstants.CD22), joinType);

					log.debug("▶▶▶▶▶▶  GA Tagging JOIN SUCCESS(UI) : [joinType:{},totalJoinCnt:{},channelJointCnt:{}]", joinType, WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL),
							WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL));

					GaTagData gaTagDto = GaTagData.builder()
							.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
							.gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
							.el(eventAction)
							.loginType(eventAction)
							.uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
							.ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
							.chCd((String) WebUtil.getSession(OmniGaTaggingConstants.CD22))
							.chCdNm((String) WebUtil.getSession(OmniGaTaggingConstants.CD21))
							.eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS)
							.joinType(joinCase)
							.totalJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_ALL))
							.channelJoinCnt((String) WebUtil.getSession(OmniGaTaggingConstants.GA_SIGNUP_TYPE_CHANNEL))
							.sendFlag("Y")
							.sessionId(request.getSession().getId())
							.sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]")
							.build();

					googleGaTaggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);

					WebUtil.setSession(OmniGaTaggingConstants.GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY, OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_SUCCESS);
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}	
		return ResponseEntity.ok("call ga join taggging");
	}
	
	/**
	 * 1. MethodName        : gaLoginStop
	 * 2. ClassName         : GaTaggingController
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 1. 20. 오전 8:52:05
	 * @return ResponseEntity<String>
	 * @param termType
	 * @param request
	 * @return
	 */
	@GetMapping("/ga/tagging/login/stop/{termType}")
	public ResponseEntity<String> gaLoginStop(@PathVariable("termType") String termType,  HttpServletRequest request) {

		log.info("start ga tagging login stop");

		try {
			
			
			
			final Map<String,String> gaCookieMap = googleGaTaggingUtils.getGaCookieMap(request);
			
			final String eventAction = getGaAction();
	 		
			log.debug("▶▶▶▶▶▶  GA Tagging LOGIN STOP (/ga/tagging/login/stop 로그인중단) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",gaCookieMap.get(OmniGaTaggingConstants.CID)
					,gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),eventAction,WebUtil.getSession(OmniGaTaggingConstants.CD21), WebUtil.getSession(OmniGaTaggingConstants.CD22));
			
			
			
			GaTagData gaTagDto = GaTagData.builder()
					.cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
                    .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
                    .el(eventAction)
                    .loginType(eventAction)
                    .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
                    .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
                    .chCd((String) WebUtil.getSession(OmniGaTaggingConstants.CD22))
                    .chCdNm((String) WebUtil.getSession(OmniGaTaggingConstants.CD21))
                    .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_FAIL)
                    .errorMessage("로그인중단")
                    .sendFlag("Y")
                    .sessionId(request.getSession().getId())
                    .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]").build();
			
//			GaTagData gaTagDto = new GaTagData(gaCookieMap.get(OmniGaTaggingConstants.CID),
//					gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),
//					eventAction, 
//					eventAction, 
//					(String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP), //user ip address
//					(String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA), //user agnet
//					(String) WebUtil.getSession(OmniGaTaggingConstants.CD21), 
//                    (String) WebUtil.getSession(OmniGaTaggingConstants.CD22), 
//                    OmniGaTaggingConstants.GA_EVENT_ACTION_FAIL,
//                    (String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_BASIC),
//                    null,
//                    null, 
//                    "가입취소",
//                    request.getLocalAddr(),
//                    "Y",
//                    "[GaTaggingController.gaLoginStop:180]"); 
					 
			googleGaTaggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);	
			
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return ResponseEntity.ok("call stop ga login taggging");
	}
	
	/**
	 * 1. MethodName        : gaJoinStop
	 * 2. ClassName         : GaTaggingController
	 * 3. Commnet           : 
	 * 4. 작성자                       : kspark01
	 * 5. 작성일                       : 2022. 1. 20. 오전 8:52:10
	 * @return ResponseEntity<String>
	 * @param termType
	 * @param request
	 * @return
	 */
	@GetMapping("/ga/tagging/join/stop/{termType}")
	public ResponseEntity<String> gaJoinStop(@PathVariable("termType") String termType, HttpServletRequest request) {

		log.info("start ga tagging join stop");

		try {
			
			final Map<String,String> gaCookieMap = googleGaTaggingUtils.getGaCookieMap(request);	 	
			final String eventAction = getGaAction();
						
		    log.debug("▶▶▶▶▶▶ GA Tagging JOIN STOP (/ga/tagging/join/stop 회원가입중단) : [cid:{},gid:{},event:{},chCdNm:{},chCd:{}]",
		    		gaCookieMap.get(OmniGaTaggingConstants.CID),gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID),
					eventAction,WebUtil.getSession(OmniGaTaggingConstants.CD21), WebUtil.getSession(OmniGaTaggingConstants.CD22));
			
			//아이디 회원가입은 제외
		   if(Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL)) 
				  && (OmniGaTaggingConstants.GA_SIGNUP_AUTH_HANDPHONE.equals(eventAction)
						  || OmniGaTaggingConstants.GA_SIGNUP_AUTH_IPIN.equals(eventAction) 
						  || OmniGaTaggingConstants.GA_SIGNUP_AUTH_KAKAO.equals(eventAction))){	
			
			    GaTagData gaTagDto = GaTagData.builder().cid(gaCookieMap.get(OmniGaTaggingConstants.CID))
			    		                                .gid(gaCookieMap.get(OmniGaTaggingConstants.GA_TAGGING_GID))
			    		                                .el(eventAction)
			    		                                .loginType(eventAction)
			    		                                .uip((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UIP))
			    		                                .ua((String) WebUtil.getSession(OmniGaTaggingConstants.GA_TAGGING_UA))
			    		                                .chCd((String) WebUtil.getSession(OmniGaTaggingConstants.CD22))
			    		                                .chCdNm((String) WebUtil.getSession(OmniGaTaggingConstants.CD21))
			    		                                .eventAction(OmniGaTaggingConstants.GA_EVENT_ACTION_JOIN_FAIL)
                                                        .joinType((String) WebUtil.getSession(OmniGaTaggingConstants.EL))
                                                        .errorMessage("가입취소")
                                                        .sendFlag("Y")
                                                        .sessionId(request.getSession().getId())
                                                        .sourcePath("[" + Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + new Exception().getStackTrace()[0].getLineNumber() + "]").build();

				googleGaTaggingUtils.googleGaTaggingDirectJoinPushApi(gaTagDto);
			
		  }		
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return ResponseEntity.ok("call stop ga join taggging");
	}
	
    private String getGaAction() {
		String eventAction = null;

		if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.LOGIN_HANDPHOME))) {
			eventAction = (String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_HANDPHOME);
		}
		if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.LOGIN_SNS))) {
			eventAction = (String) WebUtil.getSession(OmniGaTaggingConstants.LOGIN_SNS);
		}
		if (Objects.nonNull(WebUtil.getSession(OmniGaTaggingConstants.EL))) {
			eventAction = (String) WebUtil.getSession(OmniGaTaggingConstants.EL);
		}
		return eventAction;
    }

}
