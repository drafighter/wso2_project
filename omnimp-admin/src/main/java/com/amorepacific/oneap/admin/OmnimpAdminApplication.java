package com.amorepacific.oneap.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@MapperScan("com.amorepacific.oneap")
@ComponentScan("com.amorepacific.oneap")
@PropertySources( {@PropertySource("classpath:config/common-config-local.xml")} )
@SpringBootApplication
public class OmnimpAdminApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(OmnimpAdminApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(OmnimpAdminApplication.class);
	}
}
