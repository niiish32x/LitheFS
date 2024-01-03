package com.niiish32x.lithefs.core;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@EnableScheduling

// 这个必须是指定具体的mapper报名才可以顺利 否则就会报错
@MapperScan("com.niiish32x.lithefs.core.dao.mapper")
@EnableFeignClients
@SpringBootApplication
public class CoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

}
