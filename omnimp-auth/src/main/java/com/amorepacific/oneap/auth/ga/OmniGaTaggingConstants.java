package com.amorepacific.oneap.auth.ga;

/**
 * 클래스설명 : 
 * @version : 2021. 11. 22.
 * @author : kspark01
 * @분류 : 
 * omnimp-failover / package com.amorepacific.oneap.failover.common;
 */

/**
 * 1. ClassName : 
 * 2. FileName          : OmniGaTaggingConstants.java
 * 3. Package           : com.amorepacific.oneap.failover.common
 * 4. Commnet           : 
 * 5. 작성자                       : kspark01
 * 6. 작성일                       : 2021. 11. 22. 오후 1:43:23
 */
public class OmniGaTaggingConstants {
	
	//////////////////// ga tagging /////////////////////////////////////
	public static final String CID = "cid";//google ga tagging client_id
	public static final String EL = "el"; //google ga tagging 이벤트 라벨
	public static final String EA = "ea"; //google ga tagging 이벤트 액선
	public static final String CD21 = "cd21";//google ga tagging 체널명
	public static final String CD22 = "cd22";//google ga tagging 체널코드
	public static final String CD23 = "cd23";//google ga tagging 회원가입인증
	public static final String CD24 = "cd24";//google ga tagging 회원가입완료_분류
	public static final String CD27 = "cd27";//google ga tagging 로그인방법
	public static final String CD26 = "cd26";//google ga tagging 로그인 에러(시도중,성공처리중)
	
	public static final String CM6 = "cm6";//google ga tagging 통합회원 가입수
	public static final String CM7 = "cm7";//google ga tagging 경로회원 가입수
	
	public static final String LOGIN_BASIC = "loing-basic";//google ga tagging 일반로그인(아이디)
	public static final String LOGIN_HANDPHOME = "loing-handphone";//google ga tagging sns 명 ( 카카오,네이버,페이스북 )
	public static final String LOGIN_SNS = "loing-sns";//google ga tagging sns 명 ( 카카오,네이버,페이스북 )
	
	public static final String GA_EVENT_ACTION_START = "start";//google ga tagging start event action
	public static final String GA_EVENT_ACTION_SUCCESS = "success";//google ga tagging success event action
	public static final String GA_EVENT_ACTION_FAIL = "fail";//google ga tagging  fail event action
    
	
	public static final String GA_EVENT_ACTION_JOIN_START = "join_start";//google ga tagging start event action
	public static final String GA_EVENT_ACTION_JOIN_SUCCESS = "join_success";//google ga tagging success event action
	public static final String GA_EVENT_ACTION_JOIN_FAIL = "join_fail";//google ga tagging  fail event action
		
	public static final String CHANNEL_LABEL = "CH";//google ga tagging 경로가입 처리 체크 라벨
	
	public static final String BRAND_SITE_CODE_PREFIX = "030-";//google ga tagging 경로가입 처리 체크 라벨
	
	public static final String LOGIN_TYPE_ID = "id";//로그인 타입
	
	public static final String GA_TAGGING_CLIENT_ID = "_ga";//ui ga tagging client ID cookie 
	
	public static final String GA_TAGGING_GID = "_gid";//ui ga tagging client ID cookie 
	
	public static final String GA_TAGGING_UIP = "uip";//ui ga tagging user extenal ip address
	public static final String GA_TAGGING_UA = "ua";//ui ga tagging user agent 
	
	public static final int GA_CORE_POOL_SIZE = 4;
	public static final int GA_MAX_POOL_SIZE = 8;
	public static final int GA_QUEUE_CAPACITY = 1000;
	public static final boolean GA_ALLOW_CORE_THREAD_TIME_OUT = false;
	public static final int GA_KEEPAlIVE_SECONDS = 3;
	public static final boolean GA_WAIT_FOR_TASKS_TO_COMPLETE_SHUTDOWN = false;
	public static final int GA_AWAIT_TERMINATION_SECONDS = 1; 	
	public static final String GA_THREAD_NAME_PREFIX = "ga-tagging-pool-";
	
	public static final String GA_SIGNUP_AUTH_HANDPHONE = "휴대폰";
	public static final String GA_SIGNUP_AUTH_IPIN = "아이핀";
	public static final String GA_SIGNUP_AUTH_KAKAO = "카카오";
	public static final String GA_SIGNUP_AUTH_ID = "아이디";
	public static final String GA_SIGNUP_AUTH_NAVER = "네이버";
	public static final String GA_SIGNUP_AUTH_FACEBOOK = "페이스북";
	
	public static final String GA_SIGNUP_CASE_INTERGRATED = "회원가입";
	public static final String GA_SIGNUP_CASE_MEMBER = "기가입";
	public static final String GA_SIGNUP_CASE_ONLINE_MEMBER = "온라인가입";
	public static final String GA_SIGNUP_CASE_CHANNEL = "경로가입";
	public static final String GA_SIGNUP_STEP_TYPE = "join_step_type";
	public static final String GA_SIGNUP_CASE = "join_case";
	public static final String GA_SIGNUP_TYPE = "join_type";
	public static final String GA_SIGNUP_CASE_INTERGRATED_CODE = "new";
	public static final String GA_SIGNUP_CASE_MEMBER_CODE = "member";
	public static final String GA_SIGNUP_CASE_CHANNEL_CODE = "channel";
	public static final String GA_SIGNUP_CASE_ONLINE_CODE = "omni";
	public static final String GA_DUPLICATION_JOIN_START_CHECK_KEY = "ga-userid-start-key";
	public static final String GA_DUPLICATION_JOIN_SUCCESS_CHECK_KEY = "ga-userid-success-key";
	public static final String GA_JOIN_EVENT_LABEL = "ga-join-event_label";
	public static final String GA_SIGNUP_TYPE_ALL = "all";
	public static final String GA_SIGNUP_TYPE_CHANNEL = "channel";
	
	public static final String LOG_SEND_DATE_FORMAT = "yyyyMMdd";	
	public static final String LOG_SEND_TIME_FORMAT = "HHmm";

	
	
	
}
