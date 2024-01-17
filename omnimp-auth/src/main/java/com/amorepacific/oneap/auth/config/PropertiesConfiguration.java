package com.amorepacific.oneap.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.amorepacific.oneap.auth.ga.config.GaTaggingProperties;

/**
 * 클래스설명 : 
 * @version : 2021. 11. 19.
 * @author : kspark01
 * @분류 : 
 * omnimp-failover / package com.amorepacific.oneap.failover.config;
 */

/**
 * 1. ClassName : 
 * 2. FileName          : PropertiesConfiguration.java
 * 3. Package           : com.amorepacific.oneap.failover.config
 * 4. Commnet           : 
 * 5. 작성자                       : kspark01
 * 6. 작성일                       : 2021. 11. 19. 오전 11:29:07
 */
@Configuration
@EnableConfigurationProperties({
	GaTaggingProperties.class
})
@PropertySource(value = {"classpath:ga.properties" }, encoding = "UTF-8")
public class PropertiesConfiguration {

}
