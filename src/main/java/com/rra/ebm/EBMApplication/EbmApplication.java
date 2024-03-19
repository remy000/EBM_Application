package com.rra.ebm.EBMApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication()
public class EbmApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbmApplication.class, args);
	}

}
