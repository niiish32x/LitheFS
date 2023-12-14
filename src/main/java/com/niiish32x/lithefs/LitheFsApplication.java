package com.niiish32x.lithefs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LitheFsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LitheFsApplication.class, args);
	}

}
