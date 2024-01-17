package com.amorepacific.oneap.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.amorepacific.oneap"})
@SpringBootApplication
public class OmnimpCommonApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmnimpCommonApplication.class, args);
	}

}
