package com.amorepacific.oneap.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@MapperScan("com.amorepacific.oneap")
@ComponentScan("com.amorepacific.oneap")
@PropertySources({ @PropertySource("classpath:config/common-config-${spring.profiles.active}.xml") })
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class OmnimpApiApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(OmnimpApiApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(OmnimpApiApplication.class);
	}
}
