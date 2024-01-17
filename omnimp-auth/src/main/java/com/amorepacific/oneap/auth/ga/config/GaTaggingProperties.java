package com.amorepacific.oneap.auth.ga.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 클래스설명 : 
 * @version : 2021. 11. 12.
 * @author : kspark01
 * @분류 : 
 * omnimp-auth / package com.amorepacific.oneap.auth.common;
 */

/**
 * 1. ClassName : 
 * 2. FileName          : GaTaggingProperties.java
 * 3. Package           : com.amorepacific.oneap.auth.common
 * 4. Commnet           : 
 * 5. 작성자                       : kspark01
 * 6. 작성일                       : 2021. 11. 12. 오전 8:39:31
 */

@Component
@ConfigurationProperties(prefix = "ga")
@Data
@Primary
public class GaTaggingProperties {
	
	private String base;	
	private String path;	
	private String headers;	
	private int timeout;
	private Map<String,String> logins;
	private Map<String,String> datas;
	private Map<String,String> joins;

}
