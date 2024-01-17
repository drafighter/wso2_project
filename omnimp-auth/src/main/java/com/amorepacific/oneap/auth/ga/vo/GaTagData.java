package com.amorepacific.oneap.auth.ga.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 클래스설명 : 
 * @version : 2022. 1. 18.
 * @author : kspark01
 * @분류 : 
 * omnimp-auth / package com.amorepacific.oneap.auth.common;
 */

/**
 * 1. ClassName : 
 * 2. FileName          : GaTagDto.java
 * 3. Package           : com.amorepacific.oneap.auth.common
 * 4. Commnet           : 
 * 5. 작성자                       : kspark01
 * 6. 작성일                       : 2022. 1. 18. 오후 2:54:53
 */
@Data
@Builder
public class GaTagData{
		
	private String cid;
	private String gid;
	private String el;
	private String loginType;
	private String uip;
	private String ua;
	private String chCdNm;
	private String chCd;
	private String eventAction;
	private String joinType;
	private String totalJoinCnt;
	private String channelJoinCnt;	
	private String errorMessage;
	private String serverIp;
	private String sendFlag;
	private String sourcePath;
	private String incsNo;
	private String sessionId;
	private String hostName;
	private String sendDate;
	private String sendTime;
	private String loginId;
	
}
