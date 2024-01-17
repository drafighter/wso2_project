package com.amorepacific.oneap.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.amorepacific.oneap")
@ComponentScan("com.amorepacific.oneap")
@PropertySources({ @PropertySource("classpath:config/common-config-${spring.profiles.active}.xml") })
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class, SessionAutoConfiguration.class })
public class OmnimpAuthApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(OmnimpAuthApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(OmnimpAuthApplication.class);
	}

}
